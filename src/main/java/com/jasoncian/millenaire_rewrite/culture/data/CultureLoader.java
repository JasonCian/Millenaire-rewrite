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

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文化资源加载器
 * 
 * 专门负责从各种来源加载文化配置数据的工具类。
 * 支持从以下位置加载文化数据：
 * - 模组内置资源（默认文化配置）
 * - 数据包资源（服务器自定义配置）
 * - 外部文件（开发调试用）
 * 
 * 提供统一的加载接口和错误处理机制，支持多种文化数据格式的解析和验证。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CultureLoader {

    // ===== 常量定义 =====

    /** JSON解析器配置 */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .disableHtmlEscaping()
            .create();

    /** 默认文化配置文件的扩展名 */
    private static final String CULTURE_FILE_EXTENSION = ".json";

    // ===== 私有构造函数 =====

    /**
     * 私有构造函数，防止实例化
     * 该类设计为纯静态工具类
     */
    private CultureLoader() {
        throw new UnsupportedOperationException("CultureLoader is a utility class and cannot be instantiated");
    }

    // ===== 主要加载方法 =====

    /**
     * 从资源管理器加载指定文化的配置数据
     * 这是主要的文化数据加载方法，优先级顺序：数据包 > 模组资源包
     * 
     * @param resourceManager 资源管理器
     * @param cultureType     要加载的文化类型
     * @return 加载的文化配置JSON对象，如果加载失败返回null
     */
    @Nullable
    public static JsonObject loadCultureData(ResourceManager resourceManager, CultureType cultureType) {
        MillenaireLogger.debug(LogCategory.CULTURE, "Loading culture data for: {}", cultureType.getName());

        // 尝试从数据包加载（优先级最高）
        JsonObject datapackData = loadFromDatapack(resourceManager, cultureType);
        if (datapackData != null) {
            MillenaireLogger.info(LogCategory.CULTURE, "Loaded culture {} from datapack", cultureType.getName());
            return datapackData;
        }

        // 尝试从模组资源包加载
        JsonObject modData = loadFromModResources(resourceManager, cultureType);
        if (modData != null) {
            MillenaireLogger.info(LogCategory.CULTURE, "Loaded culture {} from mod resources", cultureType.getName());
            return modData;
        }

        // 尝试创建默认配置
        JsonObject defaultData = createDefaultCultureData(cultureType);
        if (defaultData != null) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Using default configuration for culture: {}",
                    cultureType.getName());
            return defaultData;
        }

        MillenaireLogger.error(LogCategory.CULTURE, "Failed to load culture data for: {}", cultureType.getName());
        return null;
    }

    /**
     * 从数据包资源加载文化数据
     * 数据包路径：data/millenaire_rewrite/cultures/[culture_name].json
     * 
     * @param resourceManager 资源管理器
     * @param cultureType     文化类型
     * @return 加载的JSON数据，如果找不到或加载失败返回null
     */
    @Nullable
    public static JsonObject loadFromDatapack(ResourceManager resourceManager, CultureType cultureType) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID,
                "cultures/" + cultureType.getName() + CULTURE_FILE_EXTENSION);

        return loadJsonFromResource(resourceManager, location, "datapack");
    }

    /**
     * 从模组资源包加载文化数据
     * 模组资源路径：assets/millenaire_rewrite/cultures/[culture_name].json
     * 
     * @param resourceManager 资源管理器
     * @param cultureType     文化类型
     * @return 加载的JSON数据，如果找不到或加载失败返回null
     */
    @Nullable
    public static JsonObject loadFromModResources(ResourceManager resourceManager, CultureType cultureType) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID,
                cultureType.getDataPath() + CULTURE_FILE_EXTENSION);

        return loadJsonFromResource(resourceManager, location, "mod resources");
    }

    /**
     * 从外部文件加载文化数据
     * 主要用于开发调试和外部配置
     * 
     * @param filePath 文件路径
     * @return 加载的JSON数据，如果加载失败返回null
     */
    @Nullable
    public static JsonObject loadFromFile(String filePath) {
        return loadFromFile(Paths.get(filePath));
    }

    /**
     * 从外部文件加载文化数据
     * 
     * @param path 文件路径
     * @return 加载的JSON数据，如果加载失败返回null
     */
    @Nullable
    public static JsonObject loadFromFile(Path path) {
        if (!Files.exists(path)) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture file does not exist: {}", path);
            return null;
        }

        if (!Files.isRegularFile(path)) {
            MillenaireLogger.error(LogCategory.CULTURE, "Path is not a regular file: {}", path);
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject data = GSON.fromJson(reader, JsonObject.class);

            if (data == null) {
                MillenaireLogger.error(LogCategory.CULTURE, "Empty or invalid JSON in file: {}", path);
                return null;
            }

            MillenaireLogger.debug(LogCategory.CULTURE, "Successfully loaded culture data from file: {}", path);
            return data;

        } catch (IOException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "IOException while reading file {}: {}", path, e.getMessage());
            return null;
        } catch (JsonParseException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "JSON parse error in file {}: {}", path, e.getMessage());
            return null;
        }
    }

    // ===== 辅助加载方法 =====

    /**
     * 从资源位置加载JSON数据
     * 
     * @param resourceManager 资源管理器
     * @param location        资源位置
     * @param sourceType      资源来源类型（用于日志）
     * @return 加载的JSON数据，如果加载失败返回null
     */
    @Nullable
    private static JsonObject loadJsonFromResource(ResourceManager resourceManager, ResourceLocation location,
            String sourceType) {
        try {
            Optional<Resource> resourceOpt = resourceManager.getResource(location);
            if (resourceOpt.isEmpty()) {
                MillenaireLogger.debug(LogCategory.CULTURE, "Resource not found in {}: {}", sourceType, location);
                return null;
            }

            Resource resource = resourceOpt.get();

            try (InputStream inputStream = resource.open();
                    InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

                JsonObject data = GSON.fromJson(reader, JsonObject.class);

                if (data == null) {
                    MillenaireLogger.warn(LogCategory.CULTURE, "Empty JSON data in {}: {}", sourceType, location);
                    return null;
                }

                MillenaireLogger.debug(LogCategory.CULTURE, "Successfully loaded JSON from {}: {}", sourceType,
                        location);
                return data;

            }

        } catch (IOException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "IOException while loading from {} {}: {}",
                    sourceType, location, e.getMessage());
            return null;
        } catch (JsonParseException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "JSON parse error in {} {}: {}",
                    sourceType, location, e.getMessage());
            return null;
        }
    }

    // ===== 默认配置生成 =====

    /**
     * 为指定文化类型创建默认配置数据
     * 当无法从任何来源加载文化数据时，生成基础的默认配置以确保系统正常运行
     * 
     * @param cultureType 文化类型
     * @return 默认配置JSON对象
     */
    @Nullable
    public static JsonObject createDefaultCultureData(CultureType cultureType) {
        MillenaireLogger.info(LogCategory.CULTURE, "Creating default configuration for culture: {}",
                cultureType.getName());

        try {
            JsonObject config = new JsonObject();

            // 基础信息
            config.addProperty("displayName", formatDisplayName(cultureType.getName()));
            config.addProperty("description", "Default configuration for " + cultureType.getName() + " culture");
            config.addProperty("version", "1.0.0-default");
            config.addProperty("enabled", true);

            // 社会结构默认配置
            JsonObject social = new JsonObject();
            addDefaultSocialData(social, cultureType);
            config.add("social", social);

            // 建筑系统默认配置
            JsonObject architecture = new JsonObject();
            addDefaultArchitecturalData(architecture, cultureType);
            config.add("architecture", architecture);

            // 经济系统默认配置
            JsonObject economy = new JsonObject();
            addDefaultEconomicData(economy, cultureType);
            config.add("economy", economy);

            // 文化特色默认配置
            JsonObject features = new JsonObject();
            addDefaultCulturalFeatures(features, cultureType);
            config.add("culture_features", features);

            // 技术系统默认配置
            JsonObject technology = new JsonObject();
            addDefaultTechnologyData(technology, cultureType);
            config.add("technology", technology);

            MillenaireLogger.debug(LogCategory.CULTURE, "Successfully created default configuration for: {}",
                    cultureType.getName());
            return config;

        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CULTURE, "Failed to create default configuration for {}: {}",
                    cultureType.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * 添加默认社会结构数据
     */
    private static void addDefaultSocialData(JsonObject social, CultureType cultureType) {
        // 根据文化类型设置默认职业
        switch (cultureType) {
            case NORMAN:
                social.addProperty("professions", "[\"knight\", \"farmer\", \"craftsman\", \"merchant\", \"priest\"]");
                social.addProperty("hierarchy", "[\"lord\", \"knight\", \"citizen\", \"peasant\"]");
                break;
            case BYZANTINE:
                social.addProperty("professions",
                        "[\"merchant\", \"craftsman\", \"scholar\", \"soldier\", \"priest\"]");
                social.addProperty("hierarchy", "[\"emperor\", \"noble\", \"citizen\", \"peasant\"]");
                break;
            case JAPANESE:
                social.addProperty("professions", "[\"samurai\", \"farmer\", \"artisan\", \"merchant\", \"monk\"]");
                social.addProperty("hierarchy", "[\"daimyo\", \"samurai\", \"citizen\", \"peasant\"]");
                break;
            case HUAXIA:
                social.addProperty("professions", "[\"scholar\", \"farmer\", \"artisan\", \"merchant\", \"official\"]");
                social.addProperty("hierarchy", "[\"emperor\", \"mandarin\", \"citizen\", \"peasant\"]");
                break;
            default:
                social.addProperty("professions", "[\"farmer\", \"craftsman\", \"merchant\"]");
                social.addProperty("hierarchy", "[\"leader\", \"citizen\", \"peasant\"]");
                break;
        }
    }

    /**
     * 添加默认建筑数据
     */
    private static void addDefaultArchitecturalData(JsonObject architecture, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                architecture.addProperty("style", "medieval_european");
                architecture.addProperty("building_types", "[\"castle\", \"house\", \"church\", \"farm\", \"market\"]");
                break;
            case BYZANTINE:
                architecture.addProperty("style", "byzantine");
                architecture.addProperty("building_types",
                        "[\"palace\", \"house\", \"cathedral\", \"workshop\", \"bazaar\"]");
                break;
            case JAPANESE:
                architecture.addProperty("style", "traditional_japanese");
                architecture.addProperty("building_types", "[\"dojo\", \"house\", \"temple\", \"farm\", \"market\"]");
                break;
            case HUAXIA:
                architecture.addProperty("style", "traditional_chinese");
                architecture.addProperty("building_types",
                        "[\"palace\", \"house\", \"temple\", \"garden\", \"market\"]");
                break;
            default:
                architecture.addProperty("style", "generic");
                architecture.addProperty("building_types", "[\"house\", \"farm\", \"market\"]");
                break;
        }
    }

    /**
     * 添加默认经济数据
     */
    private static void addDefaultEconomicData(JsonObject economy, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                economy.addProperty("currencies", "[\"denier\"]");
                economy.addProperty("production_specialties", "[\"wool\", \"grain\", \"metal_goods\"]");
                break;
            case BYZANTINE:
                economy.addProperty("currencies", "[\"solidus\", \"denier\"]");
                economy.addProperty("production_specialties", "[\"silk\", \"spices\", \"luxury_goods\"]");
                break;
            case JAPANESE:
                economy.addProperty("currencies", "[\"koban\", \"mon\"]");
                economy.addProperty("production_specialties", "[\"rice\", \"tea\", \"crafts\"]");
                break;
            case HUAXIA:
                economy.addProperty("currencies", "[\"copper_coin\", \"silver_tael\", \"gold_ingot\"]");
                economy.addProperty("production_specialties", "[\"tea\", \"silk\", \"porcelain\", \"spices\"]");
                break;
            default:
                economy.addProperty("currencies", "[\"coin\"]");
                economy.addProperty("production_specialties", "[\"food\", \"basic_goods\"]");
                break;
        }
    }

    /**
     * 添加默认文化特色数据
     */
    private static void addDefaultCulturalFeatures(JsonObject features, CultureType cultureType) {
        switch (cultureType) {
            case NORMAN:
                features.addProperty("cultural_items", "[\"norman_sword\", \"norman_shield\", \"norman_helm\"]");
                break;
            case BYZANTINE:
                features.addProperty("cultural_items", "[\"byzantine_armor\", \"byzantine_cross\", \"silk_robe\"]");
                break;
            case JAPANESE:
                features.addProperty("cultural_items", "[\"katana\", \"wakizashi\", \"kimono\", \"tea_set\"]");
                break;
            case HUAXIA:
                features.addProperty("cultural_items", "[\"chinese_sword\", \"silk_robe\", \"tea_set\", \"compass\"]");
                break;
            default:
                features.addProperty("cultural_items", "[\"basic_tool\", \"basic_clothing\"]");
                break;
        }
    }

    /**
     * 添加默认技术数据
     */
    private static void addDefaultTechnologyData(JsonObject technology, CultureType cultureType) {
        technology.addProperty("levels", "{\"agriculture\": 3, \"construction\": 3, \"craftsmanship\": 3}");
        technology.addProperty("research_aptitudes",
                "{\"agriculture\": 0.6, \"construction\": 0.6, \"craftsmanship\": 0.6}");
    }

    // ===== 文件操作工具方法 =====

    /**
     * 保存文化数据到文件
     * 
     * @param cultureData 要保存的文化数据
     * @param filePath    目标文件路径
     * @return 如果保存成功返回true，否则返回false
     */
    public static boolean saveCultureDataToFile(JsonObject cultureData, String filePath) {
        return saveCultureDataToFile(cultureData, Paths.get(filePath));
    }

    /**
     * 保存文化数据到文件
     * 
     * @param cultureData 要保存的文化数据
     * @param path        目标文件路径
     * @return 如果保存成功返回true，否则返回false
     */
    public static boolean saveCultureDataToFile(JsonObject cultureData, Path path) {
        try {
            // 确保父目录存在
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            // 写入文件
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(cultureData, writer);
            }

            MillenaireLogger.info(LogCategory.CULTURE, "Successfully saved culture data to: {}", path);
            return true;

        } catch (IOException e) {
            MillenaireLogger.error(LogCategory.CULTURE, "Failed to save culture data to {}: {}", path, e.getMessage());
            return false;
        }
    }

    /**
     * 验证文化数据文件是否有效
     * 
     * @param filePath 文件路径
     * @return 如果文件有效返回true，否则返回false
     */
    public static boolean validateCultureDataFile(String filePath) {
        return validateCultureDataFile(Paths.get(filePath));
    }

    /**
     * 验证文化数据文件是否有效
     * 
     * @param path 文件路径
     * @return 如果文件有效返回true，否则返回false
     */
    public static boolean validateCultureDataFile(Path path) {
        JsonObject data = loadFromFile(path);
        return data != null && validateCultureDataStructure(data);
    }

    /**
     * 验证文化数据的JSON结构是否有效
     * 
     * @param data 要验证的JSON数据
     * @return 如果结构有效返回true，否则返回false
     */
    public static boolean validateCultureDataStructure(JsonObject data) {
        // 检查必要的字段
        if (!data.has("displayName")) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture data missing displayName field");
            return false;
        }

        if (!data.has("version")) {
            MillenaireLogger.warn(LogCategory.CULTURE, "Culture data missing version field");
            return false;
        }

        // 检查主要章节
        String[] requiredSections = { "social", "architecture", "economy", "culture_features", "technology" };
        for (String section : requiredSections) {
            if (!data.has(section)) {
                MillenaireLogger.warn(LogCategory.CULTURE, "Culture data missing section: {}", section);
                // 警告但不阻止加载，可以使用默认值
            }
        }

        MillenaireLogger.debug(LogCategory.CULTURE, "Culture data structure validation passed");
        return true;
    }

    // ===== 工具方法 =====

    /**
     * 格式化显示名称
     * 将下划线分隔的名称转换为首字母大写的显示名称
     * 
     * @param name 原始名称
     * @return 格式化后的显示名称
     */
    private static String formatDisplayName(String name) {
        if (name == null || name.isEmpty()) {
            return "Unknown";
        }

        return Arrays.stream(name.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    /**
     * 获取文化数据的资源位置
     * 
     * @param cultureType  文化类型
     * @param fromDatapack 是否从数据包获取
     * @return 资源位置
     */
    public static ResourceLocation getCultureResourceLocation(CultureType cultureType, boolean fromDatapack) {
        String path = fromDatapack ? "cultures/" + cultureType.getName() + CULTURE_FILE_EXTENSION
                : cultureType.getDataPath() + CULTURE_FILE_EXTENSION;

        return ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, path);
    }
}
