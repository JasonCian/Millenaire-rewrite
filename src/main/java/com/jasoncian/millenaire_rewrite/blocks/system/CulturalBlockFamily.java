package com.jasoncian.millenaire_rewrite.blocks.system;

import net.minecraft.world.item.CreativeModeTab;
import com.jasoncian.millenaire_rewrite.core.MillCreativeTabs;

import java.util.function.Supplier;

/**
 * 文化方块系列枚举 - 定义所有文化类型的方块系列
 *
 * 统一管理不同文化的方块系列，包括创造模式标签页分配、
 * 文化特色属性等。支持扩展新的文化类型。
 *
 * 功能特性：
 * - 文化类型统一管理
 * - 创造模式标签页自动分配
 * - 本地化字符串生成
 * - 扩展友好的设计
 *
 * @author JasonCian
 * @version 1.0.0
 */
public enum CulturalBlockFamily {

    // ================ 核心建筑方块 ================

    /** 基础建筑方块 - 所有文化通用的基础建筑材料 */
    BASIC(
            "basic",
            "基础建筑",
            () -> MillCreativeTabs.MILLENAIRE_BLOCKS.get(),
            true// 生成所有变体
    ),

    // ================ 文化特色方块 ================

    /** 诺曼文化方块系列 */
    NORMAN(
            "norman",
            "诺曼文化",
            () -> MillCreativeTabs.MILLENAIRE_NORMAN.get(),
            true),

    /** 印度文化方块系列 */
    INDIAN(
            "indian",
            "印度文化",
            () -> MillCreativeTabs.MILLENAIRE_INDIAN.get(),
            true),

    /** 日本文化方块系列 */
    JAPANESE(
            "japanese",
            "日本文化",
            () -> MillCreativeTabs.MILLENAIRE_JAPANESE.get(),
            true),

    /** 玛雅文化方块系列 */
    MAYAN(
            "mayan",
            "玛雅文化",
            () -> MillCreativeTabs.MILLENAIRE_MAYAN.get(),
            true),

    /** 拜占庭文化方块系列 */
    BYZANTINE(
            "byzantine",
            "拜占庭文化",
            () -> MillCreativeTabs.MILLENAIRE_BYZANTINE.get(),
            true),

    /** 因纽特文化方块系列 */
    INUIT(
            "inuit",
            "因纽特文化",
            () -> MillCreativeTabs.MILLENAIRE_INUIT.get(),
            true),

    // 注释暂时不存在的文化，但允许扩展
    // /** 华夏文化方块系列 */
    // HUAXIA(
    // "huaxia",
    // "华夏文化",
    // () -> MillCreativeTabs.MILLENAIRE_MAYAN.get(), // 暂时使用玛雅标签页
    // true
    // ),

    // /** 萨拉逊文化方块系列 */
    // SARACEN(
    // "saracen",
    // "萨拉逊文化",
    // () -> MillCreativeTabs.MILLENAIRE_MAYAN.get(), // 暂时使用玛雅标签页
    // true
    // ),

    // /** 希腊文化方块系列 */
    // GREEK(
    // "greek",
    // "希腊文化",
    // () -> MillCreativeTabs.MILLENAIRE_MAYAN.get(), // 暂时使用玛雅标签页
    // true
    // )
    ;

    // ================ 属性字段 ================

    private final String registryPrefix;
    private final String displayName;
    private final Supplier<CreativeModeTab> creativeTabSupplier;
    private final boolean generateAllVariants;

    /**
     * 构造函数
     *
     * @param registryPrefix      注册名前缀
     * @param displayName         显示名称
     * @param creativeTabSupplier 创造模式标签页供应器
     * @param generateAllVariants 是否生成所有变体
     */
    CulturalBlockFamily(String registryPrefix, String displayName,
            Supplier<CreativeModeTab> creativeTabSupplier, boolean generateAllVariants) {
        this.registryPrefix = registryPrefix;
        this.displayName = displayName;
        this.creativeTabSupplier = creativeTabSupplier;
        this.generateAllVariants = generateAllVariants;
    }

    // ================ 访问器方法 ================

    /** 获取注册名前缀 */
    public String getRegistryPrefix() {
        return registryPrefix;
    }

    /** 获取显示名称 */
    public String getDisplayName() {
        return displayName;
    }

    /** 获取创造模式标签页 */
    public CreativeModeTab getCreativeTab() {
        return creativeTabSupplier.get();
    }

    /** 是否生成所有变体 */
    public boolean shouldGenerateAllVariants() {
        return generateAllVariants;
    }

    // ================ 实用方法 ================

    /**
     * 生成完整的注册名
     *
     * @param blockType    方块类型
     * @param materialName 材料名称
     * @return 完整的注册名
     */
    public String generateRegistryName(String blockType, String materialName) {
        if (this == BASIC) {
            return materialName + "_" + blockType;
        }
        return registryPrefix + "_" + materialName + "_" + blockType;
    }

    /**
     * 生成本地化键名
     *
     * @param blockType    方块类型
     * @param materialName 材料名称
     * @return 本地化键名
     */
    public String generateTranslationKey(String blockType, String materialName) {
        return "block.millenaire_rewrite." + generateRegistryName(blockType, materialName);
    }
}
