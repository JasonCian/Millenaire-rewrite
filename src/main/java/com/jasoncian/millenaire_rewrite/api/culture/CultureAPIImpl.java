package com.jasoncian.millenaire_rewrite.api.culture;

import com.jasoncian.millenaire_rewrite.culture.data.Culture;
import com.jasoncian.millenaire_rewrite.culture.data.CultureRegistry;
import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文化系统API实现类
 * 
 * 实现ICultureAPI接口，提供对文化系统的完整访问功能。
 * 该类作为文化系统的对外接口，封装了CultureRegistry的功能，
 * 并提供更友好的API访问方式。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CultureAPIImpl implements ICultureAPI {

    /** 文化注册器实例 */
    private final CultureRegistry cultureRegistry;

    /**
     * 创建文化API实现实例
     */
    public CultureAPIImpl() {
        this.cultureRegistry = CultureRegistry.getInstance();
    }

    @Override
    public boolean registerCulture(@NotNull ICulture culture) {
        // 由于我们使用的是基于枚举的文化系统，暂时不支持动态注册
        // 这个方法在当前实现中返回false，表示不支持动态注册
        return false;
    }

    @Override
    @NotNull
    public Optional<ICulture> getCulture(@NotNull ResourceLocation cultureId) {
        // 将ResourceLocation转换为CultureType
        CultureType cultureType = parseCultureType(cultureId);
        if (cultureType == null) {
            return Optional.empty();
        }

        Culture culture = cultureRegistry.getCulture(cultureType);
        if (culture == null) {
            return Optional.empty();
        }

        // 将Culture包装为ICulture
        return Optional.of(new CultureWrapper(culture));
    }

    @Override
    @NotNull
    public Collection<ICulture> getAllCultures() {
        return cultureRegistry.getRegisteredCultures()
                .stream()
                .map(CultureWrapper::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCultureRegistered(@NotNull ResourceLocation cultureId) {
        CultureType cultureType = parseCultureType(cultureId);
        return cultureType != null && cultureRegistry.isRegistered(cultureType);
    }

    @Override
    @NotNull
    public Collection<ICulture> searchCulturesByName(@NotNull String cultureName) {
        return cultureRegistry.getRegisteredCultures()
                .stream()
                .filter(culture -> culture.getDisplayName().toLowerCase().contains(cultureName.toLowerCase()) ||
                        culture.getType().getName().toLowerCase().contains(cultureName.toLowerCase()))
                .map(CultureWrapper::new)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public ICulture getDefaultCulture() {
        // 华夏文化作为默认文化
        Culture chineseCulture = cultureRegistry.getCulture(CultureType.HUAXIA);
        if (chineseCulture != null) {
            return new CultureWrapper(chineseCulture);
        }

        // 如果华夏文化不可用，使用Norman作为回退
        Culture normanCulture = cultureRegistry.getCulture(CultureType.NORMAN);
        if (normanCulture != null) {
            return new CultureWrapper(normanCulture);
        }

        // 如果都不可用，使用任何可用的文化
        Collection<Culture> availableCultures = cultureRegistry.getRegisteredCultures();
        if (!availableCultures.isEmpty()) {
            return new CultureWrapper(availableCultures.iterator().next());
        }

        // 极端情况：创建一个临时的默认文化
        Culture defaultCulture = new Culture(CultureType.HUAXIA);
        defaultCulture.setDisplayName("Default Culture");
        defaultCulture.setDescription("Fallback default culture");
        return new CultureWrapper(defaultCulture);
    }

    // ===== 辅助方法 =====

    /**
     * 将ResourceLocation解析为CultureType
     * 
     * @param location 资源位置
     * @return 对应的文化类型，如果解析失败返回null
     */
    @Nullable
    private CultureType parseCultureType(ResourceLocation location) {
        // 检查命名空间是否匹配
        if (!ModConstants.MOD_ID.equals(location.getNamespace())) {
            return null;
        }

        // 解析路径部分
        String path = location.getPath();
        return CultureType.fromString(path);
    }

    // ===== 额外的便利方法 =====

    /**
     * 根据文化类型获取文化实例
     * 
     * @param cultureType 文化类型
     * @return 文化实例，如果未找到返回null
     */
    @Nullable
    public Culture getCultureByType(CultureType cultureType) {
        return cultureRegistry.getCulture(cultureType);
    }

    /**
     * 根据文化名称获取文化实例
     * 
     * @param cultureName 文化名称
     * @return 文化实例，如果未找到返回null
     */
    @Nullable
    public Culture getCultureByName(String cultureName) {
        return cultureRegistry.getCultureByName(cultureName);
    }

    /**
     * 检查文化系统是否已初始化
     * 
     * @return 如果已初始化返回true，否则返回false
     */
    public boolean isInitialized() {
        return cultureRegistry.isInitialized();
    }

    /**
     * 检查是否有文化加载失败
     * 
     * @return 如果有失败的文化返回true，否则返回false
     */
    public boolean hasLoadingFailures() {
        return cultureRegistry.hasLoadingFailures();
    }

    /**
     * 获取所有已注册的文化类型
     * 
     * @return 文化类型集合
     */
    public Collection<CultureType> getRegisteredCultureTypes() {
        return cultureRegistry.getRegisteredCultureTypes();
    }

    /**
     * 获取加载状态诊断信息
     */
    public void printDiagnostics() {
        cultureRegistry.printDiagnostics();
    }
}
