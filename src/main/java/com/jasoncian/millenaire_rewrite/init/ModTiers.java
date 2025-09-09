package com.jasoncian.millenaire_rewrite.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeTier;

/**
 * 模组工具等级定义
 * 
 * 定义模组中所有自定义工具等级，包括：
 * - 华夏文化传统材质工具等级
 * - 其他文化特色工具等级
 * - 特殊材质工具等级
 * 
 * 工具等级决定了工具的：
 * - 挖掘等级（能挖掘的方块类型）
 * - 耐久度
 * - 挖掘速度
 * - 攻击伤害加成
 * - 附魔能力
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class ModTiers {
    
    // 防止实例化工具类
    private ModTiers() {
        throw new UnsupportedOperationException("工具等级定义类不能被实例化");
    }
    
    // ========== 华夏文化工具等级 ==========
    
    /**
     * 竹制工具等级
     * 特点：轻便、快速，但耐久度较低
     * 适用于：日常农具、临时工具
     */
    public static final Tier BAMBOO = new ForgeTier(
            0,                              // 挖掘等级（相当于木制）
            89,                             // 耐久度（比木制稍高）
            3.0f,                          // 挖掘速度
            0.0f,                          // 攻击伤害加成
            15,                            // 附魔能力
            BlockTags.MINEABLE_WITH_AXE,   // 能挖掘的方块标签
            () -> Ingredient.of(Blocks.BAMBOO) // 修复材料
    );
    
    /**
     * 青铜工具等级
     * 特点：介于石制和铁制之间，华夏青铜时代的代表
     * 适用于：传统农具、手工业工具
     */
    public static final Tier BRONZE = new ForgeTier(
            2,                              // 挖掘等级（相当于铁制）
            200,                            // 耐久度（比铁制稍低）
            5.5f,                          // 挖掘速度
            1.5f,                          // 攻击伤害加成
            12,                            // 附魔能力
            BlockTags.MINEABLE_WITH_PICKAXE, // 能挖掘的方块标签
            () -> Ingredient.of(ModItems.HUAXIA_COPPER_COIN.get()) // 修复材料（临时使用铜币）
    );
    
    /**
     * 精铁工具等级
     * 特点：华夏传统炼铁工艺，质量上乘
     * 适用于：高级工具、武器
     */
    public static final Tier REFINED_IRON = new ForgeTier(
            2,                              // 挖掘等级（相当于铁制）
            280,                            // 耐久度（比铁制稍高）
            6.5f,                          // 挖掘速度
            2.0f,                          // 攻击伤害加成
            14,                            // 附魔能力
            BlockTags.MINEABLE_WITH_PICKAXE, // 能挖掘的方块标签
            () -> Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT) // 修复材料
    );
    
    /**
     * 百炼钢工具等级
     * 特点：华夏古代最高工艺水平，接近钻石品质
     * 适用于：顶级工具、名匠作品
     */
    public static final Tier HUNDRED_REFINED_STEEL = new ForgeTier(
            3,                              // 挖掘等级（相当于钻石）
            1800,                           // 耐久度（接近钻石）
            8.5f,                          // 挖掘速度
            3.0f,                          // 攻击伤害加成
            12,                            // 附魔能力
            BlockTags.MINEABLE_WITH_PICKAXE, // 能挖掘的方块标签
            () -> Ingredient.of(net.minecraft.world.item.Items.DIAMOND) // 修复材料（临时使用钻石）
    );
    
    // ========== 其他文化工具等级（预留） ==========
    
    /**
     * 诺曼钢工具等级
     * 特点：中世纪欧洲锻造工艺
     */
    public static final Tier NORMAN_STEEL = new ForgeTier(
            2,                              // 挖掘等级
            250,                            // 耐久度
            6.0f,                          // 挖掘速度
            2.0f,                          // 攻击伤害加成
            10,                            // 附魔能力
            BlockTags.MINEABLE_WITH_PICKAXE, // 能挖掘的方块标签
            () -> Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT) // 修复材料
    );
    
    /**
     * 日式钢工具等级
     * 特点：日本传统折叠锻造工艺，锋利但脆弱
     */
    public static final Tier JAPANESE_STEEL = new ForgeTier(
            2,                              // 挖掘等级
            180,                            // 耐久度（较低，但高攻击）
            7.0f,                          // 挖掘速度
            3.5f,                          // 攻击伤害加成（很高）
            16,                            // 附魔能力
            BlockTags.MINEABLE_WITH_PICKAXE, // 能挖掘的方块标签
            () -> Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT) // 修复材料
    );
    
    /**
     * 拜占庭金工具等级
     * 特点：华丽但实用性一般，高附魔能力
     */
    public static final Tier BYZANTINE_GOLD = new ForgeTier(
            0,                              // 挖掘等级（低）
            80,                             // 耐久度（很低）
            12.0f,                         // 挖掘速度（很快）
            0.0f,                          // 攻击伤害加成
            25,                            // 附魔能力（极高）
            BlockTags.MINEABLE_WITH_PICKAXE, // 能挖掘的方块标签
            () -> Ingredient.of(net.minecraft.world.item.Items.GOLD_INGOT) // 修复材料
    );
    
    // ========== 工具等级比较方法 ==========
    
    /**
     * 检查工具等级是否为华夏文化专属
     * 
     * @param tier 工具等级
     * @return 是否为华夏工具等级
     */
    public static boolean isHuaxiaTier(Tier tier) {
        return tier == BAMBOO || tier == BRONZE || 
               tier == REFINED_IRON || tier == HUNDRED_REFINED_STEEL;
    }
    
    /**
     * 获取工具等级的文化类型名称
     * 
     * @param tier 工具等级
     * @return 文化类型名称
     */
    public static String getCultureName(Tier tier) {
        if (tier == BAMBOO || tier == BRONZE || tier == REFINED_IRON || tier == HUNDRED_REFINED_STEEL) {
            return "华夏";
        } else if (tier == NORMAN_STEEL) {
            return "诺曼";
        } else if (tier == JAPANESE_STEEL) {
            return "日式";
        } else if (tier == BYZANTINE_GOLD) {
            return "拜占庭";
        } else {
            return "通用";
        }
    }
    
    /**
     * 获取工具等级的显示名称
     * 
     * @param tier 工具等级
     * @return 显示名称
     */
    public static String getDisplayName(Tier tier) {
        if (tier == BAMBOO) {
            return "竹制";
        } else if (tier == BRONZE) {
            return "青铜";
        } else if (tier == REFINED_IRON) {
            return "精铁";
        } else if (tier == HUNDRED_REFINED_STEEL) {
            return "百炼钢";
        } else if (tier == NORMAN_STEEL) {
            return "诺曼钢";
        } else if (tier == JAPANESE_STEEL) {
            return "日式钢";
        } else if (tier == BYZANTINE_GOLD) {
            return "拜占庭金";
        } else {
            return "未知";
        }
    }
}
