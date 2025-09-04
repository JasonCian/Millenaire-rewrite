package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.items.ItemMillPurse;
import com.jasoncian.millenaire_rewrite.items.ItemVillageSign;
import com.jasoncian.millenaire_rewrite.items.ItemMillParchment;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Mod Items Registry
 * 
 * Responsible for registering all Millenaire mod items, organized by type
 * Prioritize implementing basic item system rather than block system
 * Complete migration of all simple items from legacy mod
 */
public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, MillenaireRewrite.MOD_ID);

    // ================ Currency System ================
    
    /** Basic Copper Denier - Most basic currency unit */
    public static final RegistryObject<Item> DENIER = ITEMS.register("denier", 
        () -> new Item(new Item.Properties())
    );
    
    /** Gold Denier - High value currency */
    public static final RegistryObject<Item> DENIER_OR = ITEMS.register("denier_or", 
        () -> new Item(new Item.Properties())
    );

    /** Silver Denier - Medium value currency */
    public static final RegistryObject<Item> DENIER_ARGENT = ITEMS.register("denier_argent", 
        () -> new Item(new Item.Properties())
    );

    // ================ Basic Materials ================
    
    /** Silk - Important trading and crafting material */
    public static final RegistryObject<Item> SILK = ITEMS.register("silk", 
        () -> new Item(new Item.Properties())
    );
    
    /** Obsidian Flake - Special material */
    public static final RegistryObject<Item> OBSIDIAN_FLAKE = ITEMS.register("obsidian_flake", 
        () -> new Item(new Item.Properties())
    );
    
    /** Unknown Powder - Mysterious material */
    public static final RegistryObject<Item> UNKNOWN_POWDER = ITEMS.register("unknown_powder", 
        () -> new Item(new Item.Properties())
    );
    
    /** Galianite Dust - Special mineral dust */
    public static final RegistryObject<Item> GALIANITE_DUST = ITEMS.register("galianite_dust", 
        () -> new Item(new Item.Properties())
    );

    // ================ Clothing Materials ================
    
    /** Wool Clothes - Basic clothing material */
    public static final RegistryObject<Item> WOOL_CLOTHES = ITEMS.register("wool_clothes", 
        () -> new Item(new Item.Properties())
    );
    
    /** Silk Clothes - Advanced clothing material */
    public static final RegistryObject<Item> SILK_CLOTHES = ITEMS.register("silk_clothes", 
        () -> new Item(new Item.Properties())
    );

    // ================ Crops ================
    
    /** Turmeric - Indian culture crop */
    public static final RegistryObject<Item> TURMERIC = ITEMS.register("turmeric", 
        () -> new Item(new Item.Properties())
    );
    
    /** Rice - Asian culture crop */
    public static final RegistryObject<Item> RICE = ITEMS.register("rice", 
        () -> new Item(new Item.Properties())
    );
    
    /** Maize - American culture crop */
    public static final RegistryObject<Item> MAIZE = ITEMS.register("maize", 
        () -> new Item(new Item.Properties())
    );
    
    /** Grapes - Mediterranean culture crop */
    public static final RegistryObject<Item> GRAPES = ITEMS.register("grapes", 
        () -> new Item(new Item.Properties())
    );

    // ================ Norman Foods ================
    
    /** Cider Apple - Norman specialty food ingredient */
    public static final RegistryObject<Item> CIDER_APPLE = ITEMS.register("cider_apple", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.CIDER_APPLE))
    );
    
    /** Cider - Norman specialty drink */
    public static final RegistryObject<Item> CIDER = ITEMS.register("cider", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.CIDER))
    );
    
    /** Calva - Norman spirits */
    public static final RegistryObject<Item> CALVA = ITEMS.register("calva", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.CALVA))
    );

    /** Tripes - Norman specialty food */
    public static final RegistryObject<Item> TRIPES = ITEMS.register("tripes", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.TRIPES))
    );

    /** Black Pudding - Norman specialty food */
    public static final RegistryObject<Item> BOUDIN_NOIR = ITEMS.register("boudin_noir", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.BOUDIN_NOIR))
    );

    // ================ Indian Foods ================
    
    /** Vegetable Curry - Indian specialty vegetarian dish, nutritious */
    public static final RegistryObject<Item> VEG_CURRY = ITEMS.register("veg_curry", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.VEG_CURRY))
    );
    
    /** Chicken Curry - Indian specialty meat dish, high nutrition */
    public static final RegistryObject<Item> MURGH_CURRY = ITEMS.register("murgh_curry", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.MURGH_CURRY))
    );

    /** Rasgulla - Provides speed effect */
    public static final RegistryObject<Item> RASGULLA = ITEMS.register("rasgulla", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.RASGULLA))
    );

    // ================ Mayan Foods ================
    
    /** Cacauhaa - Mayan specialty drink, provides night vision */
    public static final RegistryObject<Item> CACAUHAA = ITEMS.register("cacauhaa", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.CACAUHAA))
    );

    /** Masa - Mayan specialty food */
    public static final RegistryObject<Item> MASA = ITEMS.register("masa", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.MASA))
    );

    /** Wah - Mayan specialty food, provides dig speed effect */
    public static final RegistryObject<Item> WAH = ITEMS.register("wah", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.WAH))
    );


    // ================ Japanese Foods ================
    
    /** Sake - Japanese specialty drink, provides jump effect */
    public static final RegistryObject<Item> SAKE = ITEMS.register("sake", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.SAKE))
    );
    
    /** Udon - Japanese specialty noodles */
    public static final RegistryObject<Item> UDON = ITEMS.register("udon", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.UDON))
    );

    /** Ikayaki - Japanese specialty food, provides water breathing */
    public static final RegistryObject<Item> IKAYAKI = ITEMS.register("ikayaki", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.IKAYAKI))
    );

    // ================ Byzantine Foods ================
    
    /** Wine - Byzantine specialty drink */
    public static final RegistryObject<Item> WINE = ITEMS.register("wine", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.WINE))
    );
    
    /** Malvasia Wine - Premium wine, provides resistance effect */
    public static final RegistryObject<Item> MALVASIA_WINE = ITEMS.register("malvasia_wine", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.MALVASIA_WINE))
    );

    /** Feta Cheese - Byzantine specialty food */
    public static final RegistryObject<Item> FETA = ITEMS.register("feta", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.FETA))
    );

    /** Souvlaki - Byzantine specialty food, provides instant healing */
    public static final RegistryObject<Item> SOUVLAKI = ITEMS.register("souvlaki", 
        () -> new Item(new Item.Properties().food(ModFoodProperties.SOUVLAKI))
    );

    // ================ Special Items ================
    
    /** Purse - For storing currency */
    public static final RegistryObject<Item> PURSE = ITEMS.register("purse", 
        () -> new ItemMillPurse(new Item.Properties().stacksTo(1))
    );

    /** Village Sign - Village construction sign */
    public static final RegistryObject<Item> VILLAGE_SIGN = ITEMS.register("village_sign", 
        () -> new ItemVillageSign(new Item.Properties().stacksTo(16))
    );



    // ================ Decorative Items ================
    
    /** Tapestry - Norman decorative wall hanging */
    public static final RegistryObject<Item> TAPESTRY = ITEMS.register("tapestry",
        () -> new Item(new Item.Properties())
    );
    
    /** Indian Statue - Decorative statue from Indian culture */
    public static final RegistryObject<Item> INDIAN_STATUE = ITEMS.register("indian_statue",
        () -> new Item(new Item.Properties())
    );
    
    /** Mayan Statue - Decorative statue from Mayan culture */
    public static final RegistryObject<Item> MAYAN_STATUE = ITEMS.register("mayan_statue",
        () -> new Item(new Item.Properties())
    );
    
    /** Byzantine Icon Small - Small religious icon from Byzantine culture */
    public static final RegistryObject<Item> BYZANTINE_ICON_SMALL = ITEMS.register("byzantine_icon_small",
        () -> new Item(new Item.Properties())
    );
    
    /** Byzantine Icon Medium - Medium religious icon from Byzantine culture */
    public static final RegistryObject<Item> BYZANTINE_ICON_MEDIUM = ITEMS.register("byzantine_icon_medium",
        () -> new Item(new Item.Properties())
    );
    
    /** Byzantine Icon Large - Large religious icon from Byzantine culture */
    public static final RegistryObject<Item> BYZANTINE_ICON_LARGE = ITEMS.register("byzantine_icon_large",
        () -> new Item(new Item.Properties())
    );

    // ================ Special Tools & Materials ================
    
    /** Brick Mould - Tool for making bricks in Indian culture */
    public static final RegistryObject<Item> BRICK_MOULD = ITEMS.register("brick_mould",
        () -> new Item(new Item.Properties())
    );

    // ================ Byzantine Foods (Missing Items) ================
    
    /** Olives - Mediterranean olives used in Byzantine cuisine */
    public static final RegistryObject<Item> OLIVES = ITEMS.register("olives",
        () -> new Item(new Item.Properties().food(ModFoodProperties.OLIVES))
    );
    
    /** Olive Oil - Premium cooking oil from olives */
    public static final RegistryObject<Item> OLIVE_OIL = ITEMS.register("olive_oil",
        () -> new Item(new Item.Properties())
    );

    // ================ Norman Tools & Weapons ================
    
    /** Norman Sword - Well-balanced medieval blade */
    public static final RegistryObject<Item> NORMAN_SWORD = ITEMS.register("norman_sword",
        () -> new SwordItem(ModToolMaterials.NORMAN, 3, -2.4F, new Item.Properties())
    );
    
    /** Norman Axe - Medieval logging and combat axe */
    public static final RegistryObject<Item> NORMAN_AXE = ITEMS.register("norman_axe",
        () -> new AxeItem(ModToolMaterials.NORMAN, 6.0F, -3.0F, new Item.Properties())
    );
    
    /** Norman Pickaxe - Medieval mining tool */
    public static final RegistryObject<Item> NORMAN_PICKAXE = ITEMS.register("norman_pickaxe",
        () -> new PickaxeItem(ModToolMaterials.NORMAN, 1, -2.8F, new Item.Properties())
    );
    
    /** Norman Shovel - Medieval digging tool */
    public static final RegistryObject<Item> NORMAN_SHOVEL = ITEMS.register("norman_shovel",
        () -> new ShovelItem(ModToolMaterials.NORMAN, 1.5F, -3.0F, new Item.Properties())
    );
    
    /** Norman Hoe - Medieval farming tool */
    public static final RegistryObject<Item> NORMAN_HOE = ITEMS.register("norman_hoe",
        () -> new HoeItem(ModToolMaterials.NORMAN, -2, -1.0F, new Item.Properties())
    );

    // ================ Norman Armor ================
    
    /** Norman Helmet - Medieval chain/plate helm */
    public static final RegistryObject<Item> NORMAN_HELMET = ITEMS.register("norman_helmet",
        () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Norman Chestplate - Medieval chain/plate armor */
    public static final RegistryObject<Item> NORMAN_CHESTPLATE = ITEMS.register("norman_chestplate",
        () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Norman Leggings - Medieval chain/plate legs */
    public static final RegistryObject<Item> NORMAN_LEGGINGS = ITEMS.register("norman_leggings",
        () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Norman Boots - Medieval chain/plate boots */
    public static final RegistryObject<Item> NORMAN_BOOTS = ITEMS.register("norman_boots",
        () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Mayan Obsidian Tools ================
    
    /** Mayan Axe - Sharp obsidian axe */
    public static final RegistryObject<Item> MAYAN_AXE = ITEMS.register("mayan_axe",
        () -> new AxeItem(ModToolMaterials.MAYAN_OBSIDIAN, 7.0F, -3.0F, new Item.Properties())
    );
    
    /** Mayan Pickaxe - Sharp obsidian pickaxe */
    public static final RegistryObject<Item> MAYAN_PICKAXE = ITEMS.register("mayan_pickaxe",
        () -> new PickaxeItem(ModToolMaterials.MAYAN_OBSIDIAN, 1, -2.8F, new Item.Properties())
    );
    
    /** Mayan Shovel - Sharp obsidian shovel */
    public static final RegistryObject<Item> MAYAN_SHOVEL = ITEMS.register("mayan_shovel",
        () -> new ShovelItem(ModToolMaterials.MAYAN_OBSIDIAN, 1.5F, -3.0F, new Item.Properties())
    );
    
    /** Mayan Hoe - Sharp obsidian hoe */
    public static final RegistryObject<Item> MAYAN_HOE = ITEMS.register("mayan_hoe",
        () -> new HoeItem(ModToolMaterials.MAYAN_OBSIDIAN, -1, 0.0F, new Item.Properties())
    );
    
    /** Mayan Mace - Ceremonial obsidian mace */
    public static final RegistryObject<Item> MAYAN_MACE = ITEMS.register("mayan_mace",
        () -> new SwordItem(ModToolMaterials.MAYAN_OBSIDIAN, 4, -2.6F, new Item.Properties())
    );

    // ================ Byzantine Tools & Weapons ================
    
    /** Byzantine Mace - Advanced steel mace */
    public static final RegistryObject<Item> BYZANTINE_MACE = ITEMS.register("byzantine_mace",
        () -> new SwordItem(ModToolMaterials.BYZANTINE, 4, -2.5F, new Item.Properties())
    );

    // ================ Byzantine Armor ================
    
    /** Byzantine Helmet - Advanced lamellar helm */
    public static final RegistryObject<Item> BYZANTINE_HELMET = ITEMS.register("byzantine_helmet",
        () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Byzantine Chestplate - Advanced lamellar armor */
    public static final RegistryObject<Item> BYZANTINE_CHESTPLATE = ITEMS.register("byzantine_chestplate",
        () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Byzantine Leggings - Advanced lamellar legs */
    public static final RegistryObject<Item> BYZANTINE_LEGGINGS = ITEMS.register("byzantine_leggings",
        () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Byzantine Boots - Advanced lamellar boots */
    public static final RegistryObject<Item> BYZANTINE_BOOTS = ITEMS.register("byzantine_boots",
        () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Japanese Tools & Weapons ================
    
    /** Japanese Sword - Masterwork katana */
    public static final RegistryObject<Item> JAPANESE_SWORD = ITEMS.register("japanese_sword",
        () -> new SwordItem(ModToolMaterials.JAPANESE, 4, -2.0F, new Item.Properties())
    );
    
    /** Japanese Bow - Traditional yumi bow */
    public static final RegistryObject<Item> JAPANESE_BOW = ITEMS.register("japanese_bow",
        () -> new BowItem(new Item.Properties().durability(500))
    );

    // ================ Japanese Guard Armor ================
    
    /** Japanese Guard Helmet - Basic samurai helm */
    public static final RegistryObject<Item> JAPANESE_GUARD_HELMET = ITEMS.register("japanese_guard_helmet",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Japanese Guard Chestplate - Basic samurai armor */
    public static final RegistryObject<Item> JAPANESE_GUARD_CHESTPLATE = ITEMS.register("japanese_guard_chestplate",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Japanese Guard Leggings - Basic samurai legs */
    public static final RegistryObject<Item> JAPANESE_GUARD_LEGGINGS = ITEMS.register("japanese_guard_leggings",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Japanese Guard Boots - Basic samurai boots */
    public static final RegistryObject<Item> JAPANESE_GUARD_BOOTS = ITEMS.register("japanese_guard_boots",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Japanese Blue Samurai Armor ================
    
    /** Japanese Blue Helmet - Elite blue samurai helm */
    public static final RegistryObject<Item> JAPANESE_BLUE_HELMET = ITEMS.register("japanese_blue_helmet",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Japanese Blue Chestplate - Elite blue samurai armor */
    public static final RegistryObject<Item> JAPANESE_BLUE_CHESTPLATE = ITEMS.register("japanese_blue_chestplate",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Japanese Blue Leggings - Elite blue samurai legs */
    public static final RegistryObject<Item> JAPANESE_BLUE_LEGGINGS = ITEMS.register("japanese_blue_leggings",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Japanese Blue Boots - Elite blue samurai boots */
    public static final RegistryObject<Item> JAPANESE_BLUE_BOOTS = ITEMS.register("japanese_blue_boots",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Japanese Red Samurai Armor ================
    
    /** Japanese Red Helmet - Master red samurai helm */
    public static final RegistryObject<Item> JAPANESE_RED_HELMET = ITEMS.register("japanese_red_helmet",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Japanese Red Chestplate - Master red samurai armor */
    public static final RegistryObject<Item> JAPANESE_RED_CHESTPLATE = ITEMS.register("japanese_red_chestplate",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Japanese Red Leggings - Master red samurai legs */
    public static final RegistryObject<Item> JAPANESE_RED_LEGGINGS = ITEMS.register("japanese_red_leggings",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Japanese Red Boots - Master red samurai boots */
    public static final RegistryObject<Item> JAPANESE_RED_BOOTS = ITEMS.register("japanese_red_boots",
        () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Inuit Civilization ================
    
    /** Inuit Trident - Traditional fishing and hunting spear */
    public static final RegistryObject<Item> INUIT_TRIDENT = ITEMS.register("inuit_trident",
        () -> new SwordItem(ModToolMaterials.INUIT, 5, -2.8F, new Item.Properties())
    );
    
    /** Inuit Bow - Traditional bone and sinew composite bow */
    public static final RegistryObject<Item> INUIT_BOW = ITEMS.register("inuit_bow",
        () -> new BowItem(new Item.Properties().durability(384))
    );
    
    /** Ulu - Traditional Inuit women's knife for processing hides and food */
    public static final RegistryObject<Item> ULU = ITEMS.register("ulu",
        () -> new SwordItem(ModToolMaterials.INUIT, 2, -1.0F, new Item.Properties())
    );

    // ================ Inuit Fur Armor ================
    
    /** Fur Helmet - Warm winter headgear */
    public static final RegistryObject<Item> FUR_HELMET = ITEMS.register("fur_helmet",
        () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Fur Chestplate - Warm winter chest protection */
    public static final RegistryObject<Item> FUR_CHESTPLATE = ITEMS.register("fur_chestplate",
        () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Fur Leggings - Warm winter leg protection */
    public static final RegistryObject<Item> FUR_LEGGINGS = ITEMS.register("fur_leggings",
        () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Fur Boots - Warm winter foot protection */
    public static final RegistryObject<Item> FUR_BOOTS = ITEMS.register("fur_boots",
        () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Inuit Foods ================
    
    /** Bear Meat Raw - Raw bear meat for cooking */
    public static final RegistryObject<Item> BEAR_MEAT_RAW = ITEMS.register("bear_meat_raw",
        () -> new Item(new Item.Properties().food(ModFoodProperties.BEAR_MEAT_RAW))
    );
    
    /** Bear Meat Cooked - Cooked bear meat, provides cold resistance */
    public static final RegistryObject<Item> BEAR_MEAT_COOKED = ITEMS.register("bear_meat_cooked",
        () -> new Item(new Item.Properties().food(ModFoodProperties.BEAR_MEAT_COOKED))
    );
    
    /** Wolf Meat Raw - Raw wolf meat for cooking */
    public static final RegistryObject<Item> WOLF_MEAT_RAW = ITEMS.register("wolf_meat_raw",
        () -> new Item(new Item.Properties().food(ModFoodProperties.WOLF_MEAT_RAW))
    );
    
    /** Wolf Meat Cooked - Cooked wolf meat, provides speed boost */
    public static final RegistryObject<Item> WOLF_MEAT_COOKED = ITEMS.register("wolf_meat_cooked",
        () -> new Item(new Item.Properties().food(ModFoodProperties.WOLF_MEAT_COOKED))
    );
    
    /** Seafood Raw - Raw arctic seafood */
    public static final RegistryObject<Item> SEAFOOD_RAW = ITEMS.register("seafood_raw",
        () -> new Item(new Item.Properties().food(ModFoodProperties.SEAFOOD_RAW))
    );
    
    /** Seafood Cooked - Cooked arctic seafood */
    public static final RegistryObject<Item> SEAFOOD_COOKED = ITEMS.register("seafood_cooked",
        () -> new Item(new Item.Properties().food(ModFoodProperties.SEAFOOD_COOKED))
    );
    
    /** Inuit Bear Stew - Hearty survival stew */
    public static final RegistryObject<Item> INUIT_BEAR_STEW = ITEMS.register("inuit_bear_stew",
        () -> new Item(new Item.Properties().food(ModFoodProperties.INUIT_BEAR_STEW))
    );
    
    /** Inuit Meaty Stew - Mixed meat survival stew */
    public static final RegistryObject<Item> INUIT_MEATY_STEW = ITEMS.register("inuit_meaty_stew",
        () -> new Item(new Item.Properties().food(ModFoodProperties.INUIT_MEATY_STEW))
    );
    
    /** Inuit Potato Stew - Vegetable-based survival stew */
    public static final RegistryObject<Item> INUIT_POTATO_STEW = ITEMS.register("inuit_potato_stew",
        () -> new Item(new Item.Properties().food(ModFoodProperties.INUIT_POTATO_STEW))
    );

    // ================ Inuit Materials ================
    
    /** Tanned Hide - Processed hide for crafting */
    public static final RegistryObject<Item> TANNED_HIDE = ITEMS.register("tanned_hide",
        () -> new Item(new Item.Properties())
    );
    
    /** Hide Hanging - Decorative hide wall hanging */
    public static final RegistryObject<Item> HIDE_HANGING = ITEMS.register("hide_hanging",
        () -> new Item(new Item.Properties())
    );

    // ================ Seljuk Civilization ================
    
    /** Seljuk Scimitar - Curved steel sword of the Seljuk Turks */
    public static final RegistryObject<Item> SELJUK_SCIMITAR = ITEMS.register("seljuk_scimitar",
        () -> new SwordItem(ModToolMaterials.SELJUK, 4, -2.2F, new Item.Properties())
    );
    
    /** Seljuk Bow - Composite recurved bow */
    public static final RegistryObject<Item> SELJUK_BOW = ITEMS.register("seljuk_bow",
        () -> new BowItem(new Item.Properties().durability(450))
    );

    // ================ Seljuk Armor ================
    
    /** Seljuk Turban - Traditional headwear */
    public static final RegistryObject<Item> SELJUK_TURBAN = ITEMS.register("seljuk_turban",
        () -> new ArmorItem(ModArmorMaterials.SELJUK_WOOL, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Seljuk Helmet - Steel helmet with Islamic design */
    public static final RegistryObject<Item> SELJUK_HELMET = ITEMS.register("seljuk_helmet",
        () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.HELMET, new Item.Properties())
    );
    
    /** Seljuk Chestplate - Lamellar steel armor */
    public static final RegistryObject<Item> SELJUK_CHESTPLATE = ITEMS.register("seljuk_chestplate",
        () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.CHESTPLATE, new Item.Properties())
    );
    
    /** Seljuk Leggings - Steel leg protection */
    public static final RegistryObject<Item> SELJUK_LEGGINGS = ITEMS.register("seljuk_leggings",
        () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.LEGGINGS, new Item.Properties())
    );
    
    /** Seljuk Boots - Steel foot protection */
    public static final RegistryObject<Item> SELJUK_BOOTS = ITEMS.register("seljuk_boots",
        () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.BOOTS, new Item.Properties())
    );

    // ================ Seljuk Foods ================
    
    /** Pide - Turkish flatbread */
    public static final RegistryObject<Item> PIDE = ITEMS.register("pide",
        () -> new Item(new Item.Properties().food(ModFoodProperties.PIDE))
    );
    
    /** Helva - Turkish sweet confection */
    public static final RegistryObject<Item> HELVA = ITEMS.register("helva",
        () -> new Item(new Item.Properties().food(ModFoodProperties.HELVA))
    );
    
    /** Lokum - Turkish delight candy */
    public static final RegistryObject<Item> LOKUM = ITEMS.register("lokum",
        () -> new Item(new Item.Properties().food(ModFoodProperties.LOKUM))
    );
    
    /** Ayran - Traditional yogurt drink */
    public static final RegistryObject<Item> AYRAN = ITEMS.register("ayran",
        () -> new Item(new Item.Properties().food(ModFoodProperties.AYRAN))
    );
    
    /** Yogurt - Fermented dairy product */
    public static final RegistryObject<Item> YOGURT = ITEMS.register("yogurt",
        () -> new Item(new Item.Properties().food(ModFoodProperties.YOGURT))
    );
    
    /** Pistachios - Nuts from pistachio trees */
    public static final RegistryObject<Item> PISTACHIOS = ITEMS.register("pistachios",
        () -> new Item(new Item.Properties().food(ModFoodProperties.PISTACHIOS))
    );

    // ================ Seljuk Materials & Crops ================
    
    /** Cotton - Textile crop from Seljuk regions */
    public static final RegistryObject<Item> COTTON = ITEMS.register("cotton",
        () -> new Item(new Item.Properties())
    );
    
    /** Seljuk Wool Clothes - Traditional wool garments */
    public static final RegistryObject<Item> SELJUK_WOOL_CLOTHES = ITEMS.register("seljuk_wool_clothes",
        () -> new Item(new Item.Properties())
    );
    
    /** Seljuk Cotton Clothes - Luxury cotton garments */
    public static final RegistryObject<Item> SELJUK_COTTON_CLOTHES = ITEMS.register("seljuk_cotton_clothes",
        () -> new Item(new Item.Properties())
    );

    // ================ Seljuk Decorative Items ================
    
    /** Wall Carpet Small - Small decorative carpet */
    public static final RegistryObject<Item> WALL_CARPET_SMALL = ITEMS.register("wall_carpet_small",
        () -> new Item(new Item.Properties())
    );
    
    /** Wall Carpet Medium - Medium decorative carpet */
    public static final RegistryObject<Item> WALL_CARPET_MEDIUM = ITEMS.register("wall_carpet_medium",
        () -> new Item(new Item.Properties())
    );
    
    /** Wall Carpet Large - Large decorative carpet */
    public static final RegistryObject<Item> WALL_CARPET_LARGE = ITEMS.register("wall_carpet_large",
        () -> new Item(new Item.Properties())
    );

    // ================ Special Armor ================
    
    /** Mayan Quest Crown - Ceremonial quest reward */
    public static final RegistryObject<Item> MAYAN_QUEST_CROWN = ITEMS.register("mayan_quest_crown",
        () -> new ArmorItem(ModArmorMaterials.MAYAN_CEREMONIAL, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1))
    );

    // ================ Magic Items - Wands ================
    
    /** Summoning Wand - Used for building import from templates */
    public static final RegistryObject<Item> WAND_SUMMONING = ITEMS.register("wand_summoning",
        () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
            com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.SUMMONING,
            new Item.Properties()
        )
    );
    
    /** Negation Wand - Used for building export to templates */
    public static final RegistryObject<Item> WAND_NEGATION = ITEMS.register("wand_negation",
        () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
            com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.NEGATION,
            new Item.Properties()
        )
    );
    
    /** Creative Wand - Manage crop permissions and chest locks */
    public static final RegistryObject<Item> WAND_CREATIVE = ITEMS.register("wand_creative",
        () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
            com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.CREATIVE,
            new Item.Properties()
        )
    );
    
    /** Tuning Fork - Block inspection tool */
    public static final RegistryObject<Item> TUNING_FORK = ITEMS.register("tuning_fork",
        () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
            com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.TUNING_FORK,
            new Item.Properties()
        )
    );

    // ================ Magic Items - Amulets ================
    
    /** Skoll and Hati Amulet - Controls day/night cycle */
    public static final RegistryObject<Item> AMULET_SKOLL_HATI = ITEMS.register("amulet_skoll_hati",
        () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
            com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.SKOLL_HATI, 
            new Item.Properties()
        )
    );
    
    /** Alchemist Amulet - Detects nearby ores */
    public static final RegistryObject<Item> AMULET_ALCHEMIST = ITEMS.register("amulet_alchemist",
        () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
            com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.ALCHEMIST, 
            new Item.Properties()
        )
    );
    
    /** Vishnu Amulet - Detects nearby creatures */
    public static final RegistryObject<Item> AMULET_VISHNU = ITEMS.register("amulet_vishnu",
        () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
            com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.VISHNU, 
            new Item.Properties()
        )
    );
    
    /** Yggdrasil Amulet - Shows altitude information */
    public static final RegistryObject<Item> AMULET_YGGDRASIL = ITEMS.register("amulet_yggdrasil",
        () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
            com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.YGGDRASIL, 
            new Item.Properties()
        )
    );

    // ================ Parchments/Scrolls ================
    
    // Norman Parchments
    /** Norman Villager Parchment - Guide to Norman people */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_VILLAGER = ITEMS.register("parchment_norman_villager",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Norman Building Parchment - Guide to Norman buildings */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_BUILDING = ITEMS.register("parchment_norman_building",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Norman Item Parchment - Guide to Norman items */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_ITEM = ITEMS.register("parchment_norman_item",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Norman All Parchment - Complete Norman guide */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_ALL = ITEMS.register("parchment_norman_all",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    // Byzantine Parchments
    /** Byzantine Villager Parchment - Guide to Byzantine people */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_VILLAGER = ITEMS.register("parchment_byzantine_villager",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Byzantine Building Parchment - Guide to Byzantine buildings */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_BUILDING = ITEMS.register("parchment_byzantine_building",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Byzantine Item Parchment - Guide to Byzantine items */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_ITEM = ITEMS.register("parchment_byzantine_item",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Byzantine All Parchment - Complete Byzantine guide */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_ALL = ITEMS.register("parchment_byzantine_all",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    // Hindi Parchments
    /** Hindi Villager Parchment - Guide to Hindi people */
    public static final RegistryObject<Item> PARCHMENT_HINDI_VILLAGER = ITEMS.register("parchment_hindi_villager",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Hindi Building Parchment - Guide to Hindi buildings */
    public static final RegistryObject<Item> PARCHMENT_HINDI_BUILDING = ITEMS.register("parchment_hindi_building",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Hindi Item Parchment - Guide to Hindi items */
    public static final RegistryObject<Item> PARCHMENT_HINDI_ITEM = ITEMS.register("parchment_hindi_item",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Hindi All Parchment - Complete Hindi guide */
    public static final RegistryObject<Item> PARCHMENT_HINDI_ALL = ITEMS.register("parchment_hindi_all",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    // Mayan Parchments
    /** Mayan Villager Parchment - Guide to Mayan people */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_VILLAGER = ITEMS.register("parchment_mayan_villager",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Mayan Building Parchment - Guide to Mayan buildings */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_BUILDING = ITEMS.register("parchment_mayan_building",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Mayan Item Parchment - Guide to Mayan items */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_ITEM = ITEMS.register("parchment_mayan_item",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Mayan All Parchment - Complete Mayan guide */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_ALL = ITEMS.register("parchment_mayan_all",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    // Japanese Parchments
    /** Japanese Villager Parchment - Guide to Japanese people */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_VILLAGER = ITEMS.register("parchment_japanese_villager",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Japanese Building Parchment - Guide to Japanese buildings */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_BUILDING = ITEMS.register("parchment_japanese_building",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Japanese Item Parchment - Guide to Japanese items */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_ITEM = ITEMS.register("parchment_japanese_item",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );
    
    /** Japanese All Parchment - Complete Japanese guide */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_ALL = ITEMS.register("parchment_japanese_all",
        () -> new ItemMillParchment(new Item.Properties().stacksTo(16))
    );

    // ================ Block Items ================
    // 注意：方块物品现在在ModBlockItems中单独注册
    // 这样可以更好地处理装饰方块的各个变体

    /**
     * Register all items to the mod event bus
     * 
     * @param eventBus Mod event bus
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

