package com.jasoncian.millenaire_rewrite.api.culture;

import com.jasoncian.millenaire_rewrite.culture.data.Culture;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * 文化包装器类
 * 
 * 将内部的Culture类包装为ICulture接口，用于对外API访问。
 * 这个包装器提供了Culture类与ICulture接口之间的适配，
 * 确保API的稳定性和向后兼容性。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CultureWrapper implements ICulture {

    /** 被包装的文化实例 */
    private final Culture culture;

    /** 缓存的资源位置 */
    private ResourceLocation cachedId;

    /**
     * 创建文化包装器
     * 
     * @param culture 要包装的文化实例
     */
    public CultureWrapper(@NotNull Culture culture) {
        this.culture = culture;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        if (cachedId == null) {
            cachedId = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, culture.getType().getName());
        }
        return cachedId;
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return culture.getDisplayName() != null ? culture.getDisplayName() : culture.getType().getName();
    }

    @Override
    @NotNull
    public String getDescription() {
        return culture.getDescription() != null ? culture.getDescription() : "";
    }

    @Override
    public boolean isDefault() {
        // 华夏文化作为默认文化
        return culture.getType() == com.jasoncian.millenaire_rewrite.culture.data.CultureType.HUAXIA;
    }

    @Override
    public boolean isAvailable() {
        return culture.isEnabled();
    }

    @Override
    @NotNull
    public CulturePriority getDevelopmentPriority() {
        // 根据文化类型确定发展优先级
        switch (culture.getType()) {
            case HUAXIA:
                return CulturePriority.CULTURAL;
            case NORMAN:
                return CulturePriority.MILITARY;
            case JAPANESE:
                return CulturePriority.BALANCED;
            case BYZANTINE:
                return CulturePriority.COMMERCIAL;
            default:
                return CulturePriority.BALANCED;
        }
    }

    @Override
    @NotNull
    public ResourceLocation getArchitecturalStyle() {
        String style = culture.getArchitecturalStyle() != null ? culture.getArchitecturalStyle() : "default";
        return ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "architectural_style/" + style);
    }

    /**
     * 获取被包装的原始Culture对象
     * 
     * @return 原始文化实例
     */
    @NotNull
    public Culture getWrappedCulture() {
        return culture;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CultureWrapper that = (CultureWrapper) obj;
        return culture.equals(that.culture);
    }

    @Override
    public int hashCode() {
        return culture.hashCode();
    }

    @Override
    public String toString() {
        return "CultureWrapper{" +
                "id=" + getId() +
                ", displayName='" + getDisplayName() + '\'' +
                ", available=" + isAvailable() +
                '}';
    }
}
