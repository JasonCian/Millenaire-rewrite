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
 * Mod Tool Materials
 * 
 * Defines custom tool materials for different cultures in Millenaire
 * Each culture has unique tool characteristics and repair materials
 */
public class ModToolMaterials {
    
    // ================ Norman Tools ================
    
    /** Norman Steel - Well-balanced medieval tools */
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
    
    // ================ Mayan Obsidian Tools ================
    
    /** Mayan Obsidian - Sharp but fragile volcanic glass tools */
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
    
    // ================ Byzantine Steel Tools ================
    
    /** Byzantine Steel - Advanced medieval metalworking */
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
    
    // ================ Japanese Steel Tools ================
    
    /** Japanese Steel - Masterwork folded steel weapons */
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
