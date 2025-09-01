package com.jasoncian.millenaire_rewrite.core;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Mod Food Properties Configuration
 * 
 * Defines nutrition values, saturation and special effects for all Millenaire foods
 * Reference to Minecraft vanilla food balance design
 */
public class ModFoodProperties {
    
    // ================ Fruits ================
    
    /** Cider Apple - Basic fruit, provides small saturation */
    public static final FoodProperties CIDER_APPLE = new FoodProperties.Builder()
        .nutrition(4)           // 2 hunger bars
        .saturationMod(0.3f)    // Saturation modifier
        .build();

    // ================ Beverages ================
    
    /** Cider - Norman specialty drink, provides small saturation and minor positive effects */
    public static final FoodProperties CIDER = new FoodProperties.Builder()
        .nutrition(3)
        .saturationMod(0.2f)
        .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.3f) // 30% chance for regeneration
        .build();

    /** Calva - Norman spirits, high saturation but with side effects */
    public static final FoodProperties CALVA = new FoodProperties.Builder()
        .nutrition(2)
        .saturationMod(0.1f)
        .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0), 0.8f) // 80% chance for strength
        .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 0.4f)    // 40% chance for nausea
        .build();

    /** Sake - Japanese specialty drink, provides jump effect */
    public static final FoodProperties SAKE = new FoodProperties.Builder()
        .nutrition(2)
        .saturationMod(0.2f)
        .effect(() -> new MobEffectInstance(MobEffects.JUMP, 480, 1), 1.0f) // 100% chance for jump boost II
        .build();

    /** Wine - Byzantine specialty drink */
    public static final FoodProperties WINE = new FoodProperties.Builder()
        .nutrition(3)
        .saturationMod(0.3f)
        .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 0), 0.4f) // 40% chance for health boost
        .build();

    /** Malvasia Wine - Premium wine, provides resistance effect */
    public static final FoodProperties MALVASIA_WINE = new FoodProperties.Builder()
        .nutrition(4)
        .saturationMod(0.4f)
        .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 480, 0), 1.0f) // 100% chance for resistance
        .build();

    // ================ Main Dishes ================
    
    /** Vegetable Curry - Indian specialty vegetarian dish, nutritious */
    public static final FoodProperties VEG_CURRY = new FoodProperties.Builder()
        .nutrition(8)           // 4 hunger bars
        .saturationMod(0.6f)    // High saturation
        .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 100, 0), 0.7f) // 70% chance for saturation
        .build();

    /** Chicken Curry - Indian specialty meat dish, high nutrition */
    public static final FoodProperties MURGH_CURRY = new FoodProperties.Builder()
        .nutrition(10)          // 5 hunger bars
        .saturationMod(0.8f)    // Very high saturation
        .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0), 0.8f) // 80% chance for saturation
        .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 480, 0), 1.0f) // 100% chance for fire resistance
        .build();

    /** Udon - Japanese specialty noodles */
    public static final FoodProperties UDON = new FoodProperties.Builder()
        .nutrition(7)
        .saturationMod(0.7f)
        .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 150, 0), 0.6f) // 60% chance for saturation
        .build();

    // ================ Norman Specialty Foods ================
    
    /** Tripes - Norman specialty food, high nutrition but may have side effects */
    public static final FoodProperties TRIPES = new FoodProperties.Builder()
        .nutrition(10)          // High nutrition value
        .saturationMod(1.0f)    // Very high saturation
        .build();

    /** Black Pudding - Norman specialty food, high nutrition */
    public static final FoodProperties BOUDIN_NOIR = new FoodProperties.Builder()
        .nutrition(10)
        .saturationMod(1.0f)
        .build();

    // ================ Indian Desserts ================
    
    /** Rasgulla - Provides speed effect */
    public static final FoodProperties RASGULLA = new FoodProperties.Builder()
        .nutrition(4)           // Dessert, medium nutrition
        .saturationMod(0.3f)
        .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 480, 1), 1.0f) // 100% chance for speed II
        .alwaysEat()            // Can always be eaten
        .build();

    // ================ Mayan Foods ================
    
    /** Cacauhaa - Mayan specialty drink, provides night vision */
    public static final FoodProperties CACAUHAA = new FoodProperties.Builder()
        .nutrition(6)
        .saturationMod(0.3f)
        .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 480, 0), 1.0f) // 100% chance for night vision
        .build();

    /** Masa - Mayan specialty food */
    public static final FoodProperties MASA = new FoodProperties.Builder()
        .nutrition(6)
        .saturationMod(0.6f)
        .build();

    /** Wah - Mayan specialty food, provides dig speed effect */
    public static final FoodProperties WAH = new FoodProperties.Builder()
        .nutrition(10)
        .saturationMod(1.0f)
        .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 480, 0), 1.0f) // 100% chance for dig speed
        .build();

    // ================ Byzantine Foods ================
    
    /** Feta Cheese - Byzantine specialty food */
    public static final FoodProperties FETA = new FoodProperties.Builder()
        .nutrition(3)
        .saturationMod(0.1f)
        .build();

    /** Souvlaki - Byzantine specialty food, provides instant healing */
    public static final FoodProperties SOUVLAKI = new FoodProperties.Builder()
        .nutrition(10)
        .saturationMod(1.0f)
        .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 0), 1.0f) // 100% chance for instant healing
        .build();

    // ================ Japanese Seafood ================
    
    /** Ikayaki - Japanese specialty food, provides water breathing */
    public static final FoodProperties IKAYAKI = new FoodProperties.Builder()
        .nutrition(10)
        .saturationMod(1.0f)
        .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 480, 2), 1.0f) // 100% chance for water breathing III
        .build();

    // ================ Builder Pattern Helper Methods ================
    
    /**
     * Create basic food properties
     * @param nutrition Nutrition value (half of hunger points)
     * @param saturation Saturation modifier value
     * @return Food properties builder
     */
    public static FoodProperties.Builder basicFood(int nutrition, float saturation) {
        return new FoodProperties.Builder()
            .nutrition(nutrition)
            .saturationMod(saturation);
    }

    /**
     * Create food properties with positive effects
     * @param nutrition Nutrition value
     * @param saturation Saturation modifier value
     * @param effect Effect instance
     * @param probability Effect probability
     * @return Food properties builder
     */
    public static FoodProperties.Builder foodWithEffect(int nutrition, float saturation, 
                                                        MobEffectInstance effect, float probability) {
        return basicFood(nutrition, saturation)
            .effect(() -> effect, probability);
    }
}
