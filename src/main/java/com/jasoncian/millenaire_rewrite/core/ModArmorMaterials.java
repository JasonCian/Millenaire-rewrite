package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

/**
 * 模组盔甲材料
 * 
 * 为千年村庄中的不同文明定义自定义盔甲材料
 * 每个文化都有独特的盔甲特征和外观
 *
 * 功能特性：
 * - 诺曼文明盔甲材料
 * - 拜占庭文明盔甲材料
 * - 日本文明盔甲材料
 * - 特殊文明盔甲材料
 * - 平衡的防护等级和耐久度设计
 *
 * @author JasonCian
 * @version 0.1.0-alpha
 */
public enum ModArmorMaterials implements ArmorMaterial {
    
    // ================ 诺曼盔甲 ================
    
    /** 诺曼锁甲/板甲 - 重型中世纪盔甲 */
    NORMAN("norman", 12, 
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);       // Defense points
            map.put(ArmorItem.Type.LEGGINGS, 5);
            map.put(ArmorItem.Type.CHESTPLATE, 6);
            map.put(ArmorItem.Type.HELMET, 2);
        }),
        SoundEvents.ARMOR_EQUIP_IRON,              // Equip sound
        () -> Ingredient.of(Items.IRON_INGOT),     // Repair ingredient
        0.0F,                                      // Toughness
        0.0F                                       // Knockback resistance
    ),
    
    // ================ 拜占庭盔甲 ================
    
    /** 拜占庭札甲 - 先进的东方盔甲 */
    BYZANTINE("byzantine", 15,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 6);    // Slightly better than norman
            map.put(ArmorItem.Type.CHESTPLATE, 7);
            map.put(ArmorItem.Type.HELMET, 3);
        }),
        SoundEvents.ARMOR_EQUIP_GOLD,              // More refined sound
        () -> Ingredient.of(Items.GOLD_INGOT),     // Gold repair material
        1.0F,                                      // Some toughness
        0.0F
    ),
    
    // ================ 日本盔甲 ================
    
    /** 日本武士 - 大师级层叠盔甲 */
    JAPANESE_GUARD("japanese_guard", 10,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);
            map.put(ArmorItem.Type.LEGGINGS, 5);
            map.put(ArmorItem.Type.CHESTPLATE, 6);
            map.put(ArmorItem.Type.HELMET, 2);
        }),
        SoundEvents.ARMOR_EQUIP_LEATHER,           // Quieter movement
        () -> Ingredient.of(Items.IRON_INGOT),
        0.5F,                                      // Light toughness
        0.0F
    ),
    
    /** 日本蓝色武士 - 精英战士盔甲 */
    JAPANESE_BLUE("japanese_blue", 18,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 3);       // Better protection
            map.put(ArmorItem.Type.LEGGINGS, 6);
            map.put(ArmorItem.Type.CHESTPLATE, 8);
            map.put(ArmorItem.Type.HELMET, 3);
        }),
        SoundEvents.ARMOR_EQUIP_DIAMOND,           // Premium sound
        () -> Ingredient.of(Items.DIAMOND),        // Diamond repair
        2.0F,                                      // Good toughness
        0.05F                                      // Slight knockback resistance
    ),
    
    /** 日本红色武士 - 大师级战士盔甲 */
    JAPANESE_RED("japanese_red", 20,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 3);
            map.put(ArmorItem.Type.LEGGINGS, 7);    // Best protection
            map.put(ArmorItem.Type.CHESTPLATE, 8);
            map.put(ArmorItem.Type.HELMET, 4);
        }),
        SoundEvents.ARMOR_EQUIP_NETHERITE,         // Elite sound
        () -> Ingredient.of(Items.NETHERITE_INGOT), // Netherite repair
        3.0F,                                      // High toughness
        0.1F                                       // Good knockback resistance
    ),
    
    // ================ 特殊盔甲 ================
    
    /** 玛雅任务王冠 - 仪式头饰 */
    MAYAN_CEREMONIAL("mayan_ceremonial", 25,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 1);       // Light protection
            map.put(ArmorItem.Type.LEGGINGS, 2);
            map.put(ArmorItem.Type.CHESTPLATE, 3);
            map.put(ArmorItem.Type.HELMET, 4);      // Special helmet focus
        }),
        SoundEvents.ARMOR_EQUIP_GOLD,
        () -> Ingredient.of(Items.GOLD_BLOCK),     // Expensive repair
        0.0F,
        0.0F
    ),
    
    // ================ 因纽特盔甲 ================
    
    /** 毛皮盔甲 - 因纽特冬季生存装备，提供保暖 */
    FUR("fur", 8,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 2);       // Good cold protection
            map.put(ArmorItem.Type.LEGGINGS, 4);
            map.put(ArmorItem.Type.CHESTPLATE, 5);
            map.put(ArmorItem.Type.HELMET, 2);
        }),
        SoundEvents.ARMOR_EQUIP_LEATHER,           // Soft material sound
        () -> Ingredient.of(Items.LEATHER),        // Leather repair
        0.0F,                                      // No toughness
        0.0F                                       // No knockback resistance
    ),
    
    // ================ 塞尔柱盔甲 ================
    
    /** 塞尔柱钢制盔甲 - 高品质中世纪伊斯兰盔甲 */
    SELJUK("seljuk", 16,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 3);       // Excellent protection
            map.put(ArmorItem.Type.LEGGINGS, 7);
            map.put(ArmorItem.Type.CHESTPLATE, 8);
            map.put(ArmorItem.Type.HELMET, 3);
        }),
        SoundEvents.ARMOR_EQUIP_IRON,
        () -> Ingredient.of(Items.GOLD_INGOT),     // Gold repair (luxury)
        1.5F,                                      // Good toughness
        0.05F                                      // Slight knockback resistance
    ),
    
    /** 塞尔柱羊毛服装 - 传统土耳其服饰 */
    SELJUK_WOOL("seljuk_wool", 5,
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 1);       // Light protection
            map.put(ArmorItem.Type.LEGGINGS, 2);
            map.put(ArmorItem.Type.CHESTPLATE, 3);
            map.put(ArmorItem.Type.HELMET, 1);
        }),
        SoundEvents.ARMOR_EQUIP_LEATHER,           // Soft material sound
        () -> Ingredient.of(Items.WHITE_WOOL),     // Wool repair
        0.0F,
        0.0F
    );
    
    // ================ Material Properties ================
    
    private final String name;
    private final int enchantmentValue;
    private final EnumMap<ArmorItem.Type, Integer> protection;
    private final SoundEvent sound;
    private final Supplier<Ingredient> repairIngredient;
    private final float toughness;
    private final float knockbackResistance;
    
    ModArmorMaterials(String name, int enchantmentValue, EnumMap<ArmorItem.Type, Integer> protection,
                      SoundEvent sound, Supplier<Ingredient> repairIngredient, 
                      float toughness, float knockbackResistance) {
        this.name = name;
        this.enchantmentValue = enchantmentValue;
        this.protection = protection;
        this.sound = sound;
        this.repairIngredient = repairIngredient;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }
    
    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 13;
            case LEGGINGS -> 15;
            case CHESTPLATE -> 16;
            case HELMET -> 11;
            default -> 0;
        };
    }
    
    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.protection.get(type);
    }
    
    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }
    
    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
    
    @Override
    public String getName() {
        return MillenaireRewrite.MOD_ID + ":" + this.name;
    }
    
    @Override
    public float getToughness() {
        return this.toughness;
    }
    
    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
