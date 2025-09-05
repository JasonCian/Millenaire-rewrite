package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

/**
 * 工具材料配置器 - 定义Millenaire各文明的自定义工具材料
 *
 * 为各个文明定义独特的工具特性和修复材料
 * 每个文化都有独特的工具特征和修复材料
 *
 * 功能特性：
 * - 诺曼文明工具材料
 * - 玛雅文明工具材料
 * - 拜占庭文明工具材料
 * - 平衡的挖掘等级和耐久度设计
 *
 * @author JasonCian
 * @version 0.1.0-alpha
 */
public class ModToolMaterials {
    
    // ================ 诺曼工具 ================
    
    /** 诺曼钢 - 平衡的中世纪工具 */
    public static final Tier NORMAN = TierSortingRegistry.registerTier(
        new ForgeTier(
            2,                              // Harvest level (iron level)
            400,                            // Uses/Durability
            7.0F,                           // Speed
            2.5F,                           // Attack damage bonus
            12,                             // Enchantability
            null,                           // Tag (for harvest blocks)
            () -> Ingredient.of(Items.IRON_INGOT)  // Repair material
        ),
        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "norman"),
        List.of(),  // After tiers
        List.of()   // Before tiers
    );
    
    // ================ 玛雅黑曜石工具 ================
    
    /** 玛雅黑曜石 - 锋利但易碎的火山玻璃工具 */
    public static final Tier MAYAN_OBSIDIAN = TierSortingRegistry.registerTier(
        new ForgeTier(
            1,                              // Harvest level (stone level)
            180,                            // Uses/Durability (fragile)
            8.5F,                           // Speed (very fast)
            3.0F,                           // Attack damage bonus (sharp)
            5,                              // Enchantability (low)
            null,                           // Tag
            () -> Ingredient.of(Items.OBSIDIAN)  // Repair material
        ),
        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "mayan_obsidian"),
        List.of(),
        List.of()
    );
    
    // ================ 拜占庭钢制工具 ================
    
    /** 拜占庭钢 - 先进的中世纪金属工艺 */
    public static final Tier BYZANTINE = TierSortingRegistry.registerTier(
        new ForgeTier(
            2,                              // Harvest level (iron level)
            450,                            // Uses/Durability (slightly better than norman)
            6.5F,                           // Speed (balanced)
            2.8F,                           // Attack damage bonus
            15,                             // Enchantability (good)
            null,                           // Tag
            () -> Ingredient.of(Items.GOLD_INGOT)  // Repair material (expensive)
        ),
        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "byzantine"),
        List.of(),
        List.of()
    );
    
    // ================ 日本钢制工具 ================
    
    /** 日本钢 - 大师级折叠钢武器 */
    public static final Tier JAPANESE = TierSortingRegistry.registerTier(
        new ForgeTier(
            3,                              // Harvest level (diamond level)
            500,                            // Uses/Durability (high quality)
            8.0F,                           // Speed (fast)
            3.5F,                           // Attack damage bonus (excellent)
            18,                             // Enchantability (very good)
            null,                           // Tag
            () -> Ingredient.of(Items.DIAMOND)  // Repair material (expensive)
        ),
        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "japanese"),
        List.of(),
        List.of()
    );
    
    // ================ 因纽特生存工具 ================
    
    /** 因纽特工具 - 传统北极生存工具，针对严酷环境平衡 */
    public static final Tier INUIT = TierSortingRegistry.registerTier(
        new ForgeTier(
            1,                              // Harvest level (stone level)
            250,                            // Uses/Durability (moderate for survival tools)
            5.0F,                           // Speed (moderate)
            2.0F,                           // Attack damage bonus (decent)
            10,                             // Enchantability (good)
            null,                           // Tag
            () -> Ingredient.of(Items.BONE)  // Repair material (bone for traditional tools)
        ),
        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "inuit"),
        List.of(),
        List.of()
    );
    
    // ================ 塞尔柱钢制工具 ================
    
    /** 塞尔柱钢 - 高品质大马士革钢武器 */
    public static final Tier SELJUK = TierSortingRegistry.registerTier(
        new ForgeTier(
            3,                              // Harvest level (diamond level)
            600,                            // Uses/Durability (excellent)
            7.5F,                           // Speed (good)
            4.0F,                           // Attack damage bonus (very good)
            20,                             // Enchantability (excellent)
            null,                           // Tag
            () -> Ingredient.of(Items.GOLD_INGOT)  // Repair material (gold for luxury)
        ),
        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "seljuk"),
        List.of(),
        List.of()
    );
    
    /**
     * Initialize tier sorting relationships
     * Called during mod setup to ensure proper tool tier ordering
     */
    public static void initializeTierSorting() {
        MillenaireRewrite.LOGGER.info("Initializing Millenaire tool tier sorting...");
        
        // All our tiers are properly registered via TierSortingRegistry.registerTier
        // This method is kept for future tier relationship setup if needed
    }
}
