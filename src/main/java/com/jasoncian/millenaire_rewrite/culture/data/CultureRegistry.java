package com.jasoncian.millenaire_rewrite.culture.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文化注册管理器
 * 
 * 这是文化系统的核心管理类，负责：
 * - 注册和管理所有已加载的文化
 * - 从资源包和数据包加载文化配置文件
 * - 提供文化查询和访问接口
 * - 处理文化数据的重载和同步
 * - 验证文化数据的完整性和有效性
 * 
 * 设计为单例模式，保证全局唯一的文化管理器实例。
 * 支持资源包重载，允许数据包和模组包覆盖默认文化配置。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class CultureRegistry extends SimplePreparableReloadListener<Map<CultureType, JsonObject>> {

    // ===== 单例实例 =====

    /** 单例实例 */
    private static final CultureRegistry INSTANCE = new CultureRegistry();

    /** JSON解析器 */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();

    // ===== 数据存储 =====

    /** 已注册的文化映射表：文化类型 -> 文化实例 */
    private final Map<CultureType, Culture> registeredCultures = new ConcurrentHashMap<>();

    /** 文化加载状态：文化类型 -> 是否加载成功 */
    private final Map<CultureType, Boolean> loadingStatus = new ConcurrentHashMap<>();

    /** 文化数据版本：文化类型 -> 数据版本号 */
    private final Map<CultureType, String> cultureVersions = new ConcurrentHashMap<>();

    /** 加载错误信息：文化类型 -> 错误信息 */
    private final Map<CultureType, String> loadingErrors = new ConcurrentHashMap<>();

    /** 是否已完成初始化 */
    private boolean initialized = false;

    // ===== 构造函数 =====

    /**
     * 私有构造函数，确保单例模式
     */
    private CultureRegistry() {
        MillenaireLogger.debug(LogCategory.CULTURE, "Creating CultureRegistry instance");
    }

    /**
     * 获取文化注册器单例实例
     * 
     * @return 文化注册器实例
     */
    public static CultureRegistry getInstance() {
        return INSTANCE;
    }

    // ===== 资源重载监听 =====

    /**
     * 注册资源重载监听器
     * 当资源包被重载时（如/reload命令），自动重新加载文化数据
     * 
     * @param event 重载监听器添加事件
     */
    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(getInstance());
        MillenaireLogger.info(LogCategory.CULTURE, "Registered culture reload listener");
    }

    // ===== 资源加载实现 =====

    /**
     * 准备阶段：从资源管理器中读取所有文化配置文件
     * 
     * @param resourceManager 资源管理器
     * @param profiler        性能分析器
     * @return 文化类型到JSON对象的映射
     */
    @Override
    protected Map<CultureType, JsonObject> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        MillenaireLogger.info(LogCategory.CULTURE, "Starting culture data preparation phase");
        long startTime = MillenaireLogger.startTimer();

        Map<CultureType, JsonObject> preparedData = new HashMap<>();

        // 清空加载状态
        loadingStatus.clear();
        loadingErrors.clear();

        // 遍历所有文化类型，尝试加载配置文件
        for (CultureType cultureType : CultureType.values()) {
            try {
                JsonObject cultureData = loadCultureData(resourceManager, cultureType);
                if (cultureData != null) {
                    preparedData.put(cultureType, cultureData);
                    loadingStatus.put(cultureType, true);
                    MillenaireLogger.debug(LogCategory.CULTURE, "Successfully prepared culture data for: {}",
                            cultureType.getName());
                } else {
                    loadingStatus.put(cultureType, false);
                    loadingErrors.put(cultureType, "Culture data file not found");
                    MillenaireLogger.warn(LogCategory.CULTURE, "No data file found for culture: {}",
                            cultureType.getName());
                }
            } catch (Exception e) {
                loadingStatus.put(cultureType, false);
                loadingErrors.put(cultureType, e.getMessage());
                MillenaireLogger.error(LogCategory.CULTURE, "Failed to prepare culture data for {}: {}",
                        cultureType.getName(), e.getMessage());
            }
        }

        MillenaireLogger.info(LogCategory.CULTURE,
                "Culture data preparation completed in {}ms. Successfully prepared: {}/{}",
                (System.nanoTime() - startTime) / 1_000_000, preparedData.size(), CultureType.values().length);

        return preparedData;
    }

    /**
     * 应用阶段：将准备好的JSON数据转换为Culture对象并注册
     * 
     * @param preparedData    准备阶段加载的数据
     * @param resourceManager 资源管理器
     * @param profiler        性能分析器
     */
    @Override
    protected void apply(Map<CultureType, JsonObject> preparedData, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        MillenaireLogger.info(LogCategory.CULTURE, "Starting culture data application phase");
        long startTime = MillenaireLogger.startTimer();

        // 清空现有注册
        registeredCultures.clear();
        cultureVersions.clear();

        int successCount = 0;
        int totalCount = preparedData.size();

        // 处理每个文化的数据
        for (Map.Entry<CultureType, JsonObject> entry : preparedData.entrySet()) {
            CultureType cultureType = entry.getKey();
            JsonObject cultureData = entry.getValue();

            try {
                // 从JSON创建文化实例
                Culture culture = Culture.fromJson(cultureType, cultureData);

                // 验证文化数据
                if (validateCulture(culture)) {
                    // 注册文化
                    registeredCultures.put(cultureType, culture);
                    cultureVersions.put(cultureType, culture.getVersion());

                    successCount++;
                    MillenaireLogger.debug(LogCategory.CULTURE, "Successfully registered culture: {} (version: {})",
                            cultureType.getName(), culture.getVersion());
                } else {
                    loadingStatus.put(cultureType, false);
                    loadingErrors.put(cultureType, "Culture validation failed");
                    MillenaireLogger.error(LogCategory.CULTURE, "Validation failed for culture: {}",
                            cultureType.getName());
                }

            } catch (Exception e) {
                loadingStatus.put(cultureType, false);
                loadingErrors.put(cultureType, e.getMessage());
                MillenaireLogger.error(LogCategory.CULTURE, "Failed to create culture instance for {}: {}",
                        cultureType.getName(), e.getMessage());
            }
        }

        // 确保至少有一些默认文化可用
        ensureDefaultCultures();

        initialized = true;

        MillenaireLogger.info(LogCategory.CULTURE,
                "Culture registration completed in {}ms. Successfully registered: {}/{} cultures",
                (System.nanoTime() - startTime) / 1_000_000, successCount, totalCount);

        // 输出加载摘要
        logLoadingSummary();
    }

    // ===== 数据加载方法 =====

    /**
     * 从资源管理器加载指定文化的配置数据
     * 
     * @param resourceManager 资源管理器
     * @param cultureType     文化类型
     * @return 加载的JSON数据，如果找不到文件返回null
     */
    @Nullable
    private JsonObject loadCultureData(ResourceManager resourceManager, CultureType cultureType) {
        // 构建文化配置文件的资源位置
        ResourceLocation cultureLocation = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID,
                cultureType.getDataPath() + ".json");

        try {
            // 获取资源
            Optional<Resource> resourceOpt = resourceManager.getResource(cultureLocation);
            if (resourceOpt.isEmpty()) {
                MillenaireLogger.debug(LogCategory.CULTURE, "Culture data file not found: {}", cultureLocation);
                return null;
            }

            Resource resource = resourceOpt.get();

            // 读取JSON内容
            try (InputStream inputStream = resource.open();
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                JsonObject jsonData = GSON.fromJson(reader, JsonObject.class);

                if (jsonData == null) {
                    MillenaireLogger.warn(LogCategory.CULTURE, "Empty JSON data for culture: {}",
                            cultureType.getName());
                    return null;
                }

                MillenaireLogger.debug(LogCategory.CULTURE, "Successfully loaded culture data from: {}",
                        cultureLocation);
                return jsonData;

            }

        } catch (IOException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "IOException while loading culture data for {}: {}",
                    cultureType.getName(), e.getMessage());
            return null;
        } catch (JsonParseException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "JSON parse error for culture {}: {}",
                    cultureType.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 验证文化数据的完整性和有效性
     * 
     * @param culture 要验证的文化实例
     * @return 如果验证通过返回true，否则返回false
     */
    private boolean validateCulture(Culture culture) {
        if (culture == null) {
            return false;
        }

        // 基础验证
        if (culture.getType() == null) {
            MillenaireLogger.error(LogCategory.CULTURE, "Culture has null type");
            return false;
        }

        if (culture.getDisplayName() == null || culture.getDisplayName().isEmpty()) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture {} has empty display name",
                    culture.getType().getName());
            // 不阻止加载，使用默认名称
        }

        if (culture.getVersion() == null || culture.getVersion().isEmpty()) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture {} has empty version",
                    culture.getType().getName());
            // 不阻止加载，使用默认版本
        }

        // 职业验证
        if (culture.getSupportedProfessions().isEmpty()) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture {} has no supported professions",
                    culture.getType().getName());
        }

        // 建筑验证
        if (culture.getBuildingTypes().isEmpty()) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture {} has no building types",
                    culture.getType().getName());
        }

        // 货币验证
        if (culture.getCurrencyTypes().isEmpty()) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture {} has no currency types",
                    culture.getType().getName());
        }

        MillenaireLogger.debug(LogCategory.CULTURE, "Culture {} passed validation", culture.getType().getName());
        return true;
    }

    /**
     * 确保有默认文化可用
     * 如果某些核心文化加载失败，创建最小化的默认实例
     */
    private void ensureDefaultCultures() {
        // 确保至少有Norman文化可用（作为回退选项）
        if (!isRegistered(CultureType.NORMAN)) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Norman culture not loaded, creating default instance");
            Culture defaultNorman = createDefaultCulture(CultureType.NORMAN);
            registeredCultures.put(CultureType.NORMAN, defaultNorman);
            cultureVersions.put(CultureType.NORMAN, defaultNorman.getVersion());
        }
    }

    /**
     * 创建指定类型的默认文化实例
     * 
     * @param cultureType 文化类型
     * @return 默认文化实例
     */
    private Culture createDefaultCulture(CultureType cultureType) {
        Culture culture = new Culture(cultureType);
        culture.setDisplayName(cultureType.getName().substring(0, 1).toUpperCase() +
                cultureType.getName().substring(1));
        culture.setDescription("Default culture instance for " + cultureType.getName());
        culture.setVersion("1.0.0-default");
        culture.setArchitecturalStyle("default");

        MillenaireLogger.info(LogCategory.CULTURE, "Created default culture instance: {}", cultureType.getName());
        return culture;
    }

    /**
     * 输出加载摘要信息
     */
    private void logLoadingSummary() {
        MillenaireLogger.info(LogCategory.CULTURE, "=== Culture Loading Summary ===");

        for (CultureType cultureType : CultureType.values()) {
            Boolean status = loadingStatus.get(cultureType);
            if (status != null && status) {
                Culture culture = registeredCultures.get(cultureType);
                String version = culture != null ? culture.getVersion() : "unknown";
                MillenaireLogger.info(LogCategory.CULTURE, "✓ {} - v{} - Loaded successfully",
                        cultureType.getName(), version);
            } else {
                String error = loadingErrors.getOrDefault(cultureType, "Unknown error");
                MillenaireLogger.warn(LogCategory.CULTURE, "✗ {} - Failed: {}",
                        cultureType.getName(), error);
            }
        }

        MillenaireLogger.info(LogCategory.CULTURE, "Total registered cultures: {}", registeredCultures.size());
        MillenaireLogger.info(LogCategory.CULTURE, "==============================");
    }

    // ===== 公共查询接口 =====

    /**
     * 检查文化注册器是否已初始化
     * 
     * @return 如果已初始化返回true，否则返回false
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 获取指定类型的文化实例
     * 
     * @param cultureType 文化类型
     * @return 文化实例，如果未注册返回null
     */
    @Nullable
    public Culture getCulture(CultureType cultureType) {
        return registeredCultures.get(cultureType);
    }

    /**
     * 检查指定文化类型是否已注册
     * 
     * @param cultureType 文化类型
     * @return 如果已注册返回true，否则返回false
     */
    public boolean isRegistered(CultureType cultureType) {
        return registeredCultures.containsKey(cultureType);
    }

    /**
     * 获取所有已注册的文化类型
     * 
     * @return 已注册的文化类型集合
     */
    public Set<CultureType> getRegisteredCultureTypes() {
        return Collections.unmodifiableSet(registeredCultures.keySet());
    }

    /**
     * 获取所有已注册的文化实例
     * 
     * @return 已注册的文化实例集合
     */
    public Collection<Culture> getRegisteredCultures() {
        return Collections.unmodifiableCollection(registeredCultures.values());
    }

    /**
     * 获取已注册文化的数量
     * 
     * @return 文化数量
     */
    public int getRegisteredCultureCount() {
        return registeredCultures.size();
    }

    /**
     * 获取指定文化的加载状态
     * 
     * @param cultureType 文化类型
     * @return 加载状态信息
     */
    public CultureLoadingStatus getLoadingStatus(CultureType cultureType) {
        Boolean success = loadingStatus.get(cultureType);
        String error = loadingErrors.get(cultureType);
        boolean registered = isRegistered(cultureType);

        return new CultureLoadingStatus(cultureType,
                success != null ? success : false,
                registered, error);
    }

    /**
     * 获取所有文化的加载状态
     * 
     * @return 文化加载状态列表
     */
    public List<CultureLoadingStatus> getAllLoadingStatuses() {
        List<CultureLoadingStatus> statuses = new ArrayList<>();
        for (CultureType cultureType : CultureType.values()) {
            statuses.add(getLoadingStatus(cultureType));
        }
        return statuses;
    }

    /**
     * 根据名称查找文化
     * 
     * @param cultureName 文化名称
     * @return 文化实例，如果找不到返回null
     */
    @Nullable
    public Culture getCultureByName(String cultureName) {
        CultureType cultureType = CultureType.fromString(cultureName);
        return cultureType != null ? getCulture(cultureType) : null;
    }

    /**
     * 获取指定文化的版本号
     * 
     * @param cultureType 文化类型
     * @return 版本号，如果未注册返回null
     */
    @Nullable
    public String getCultureVersion(CultureType cultureType) {
        return cultureVersions.get(cultureType);
    }

    // ===== 工具方法 =====

    /**
     * 检查是否有任何文化加载失败
     * 
     * @return 如果有失败的文化返回true，否则返回false
     */
    public boolean hasLoadingFailures() {
        return loadingStatus.values().contains(false);
    }

    /**
     * 获取加载失败的文化列表
     * 
     * @return 加载失败的文化类型列表
     */
    public List<CultureType> getFailedCultures() {
        return loadingStatus.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * 获取成功加载的文化列表
     * 
     * @return 成功加载的文化类型列表
     */
    public List<CultureType> getSuccessfulCultures() {
        return loadingStatus.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();
    }

    // ===== 调试和诊断方法 =====

    /**
     * 输出文化注册器的详细诊断信息
     */
    public void printDiagnostics() {
        MillenaireLogger.info(LogCategory.CULTURE, "=== Culture Registry Diagnostics ===");
        MillenaireLogger.info(LogCategory.CULTURE, "Initialized: {}", initialized);
        MillenaireLogger.info(LogCategory.CULTURE, "Registered Cultures: {}", registeredCultures.size());
        MillenaireLogger.info(LogCategory.CULTURE, "Loading Failures: {}", getFailedCultures().size());

        if (!getFailedCultures().isEmpty()) {
            MillenaireLogger.info(LogCategory.CULTURE, "Failed Cultures:");
            for (CultureType failed : getFailedCultures()) {
                String error = loadingErrors.getOrDefault(failed, "Unknown error");
                MillenaireLogger.info(LogCategory.CULTURE, "  - {}: {}", failed.getName(), error);
            }
        }

        MillenaireLogger.info(LogCategory.CULTURE, "====================================");
    }

    /**
     * 文化加载状态数据类
     */
    public static class CultureLoadingStatus {
        private final CultureType cultureType;
        private final boolean loadingSuccess;
        private final boolean registered;
        private final String errorMessage;

        public CultureLoadingStatus(CultureType cultureType, boolean loadingSuccess,
                boolean registered, String errorMessage) {
            this.cultureType = cultureType;
            this.loadingSuccess = loadingSuccess;
            this.registered = registered;
            this.errorMessage = errorMessage;
        }

        public CultureType getCultureType() {
            return cultureType;
        }

        public boolean isLoadingSuccess() {
            return loadingSuccess;
        }

        public boolean isRegistered() {
            return registered;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public String toString() {
            return String.format("CultureLoadingStatus{type=%s, loadingSuccess=%s, registered=%s, error='%s'}",
                    cultureType.getName(), loadingSuccess, registered, errorMessage);
        }
    }
}
