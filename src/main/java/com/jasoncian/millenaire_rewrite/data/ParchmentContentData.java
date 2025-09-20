package com.jasoncian.millenaire_rewrite.data;

import com.jasoncian.millenaire_rewrite.items.ItemMillParchment;
import net.minecraft.world.item.ItemStack;

/**
 * 羊皮纸内容数据生成器
 * 预设各个文化的羊皮纸内容，基于旧版本Millenaire的本地化文件
 */
public class ParchmentContentData {
    
    /**
     * 创建诺曼村民羊皮纸
     */
    public static ItemStack createNormanVillagerParchment() {
        String title = "parchment.norman.villager.title";
        String[] contents = {
                "parchment.norman.villager.chief",
                "parchment.norman.villager.knight",
                "parchment.norman.villager.farmer",
                "parchment.norman.villager.artisan",
                "parchment.norman.villager.women",
                "parchment.norman.villager.children",
                "parchment.norman.villager.merchant",
                "parchment.norman.villager.architect"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建诺曼建筑羊皮纸
     */
    public static ItemStack createNormanBuildingParchment() {
        String title = "parchment.norman.building.title";
        String[] contents = {
                "parchment.norman.building.town_hall",
                "parchment.norman.building.blacksmith",
                "parchment.norman.building.farm",
                "parchment.norman.building.watchtower",
                "parchment.norman.building.house",
                "parchment.norman.building.church",
                "parchment.norman.building.market",
                "parchment.norman.building.stable"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建诺曼物品羊皮纸
     */
    public static ItemStack createNormanItemParchment() {
        String title = "parchment.norman.item.title";
        String[] contents = {
                "parchment.norman.item.sword",
                "parchment.norman.item.axe",
                "parchment.norman.item.pickaxe",
                "parchment.norman.item.shovel",
                "parchment.norman.item.hoe",
                "parchment.norman.item.cider",
                "parchment.norman.item.blood_sausage",
                "parchment.norman.item.armor"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建诺曼全书羊皮纸 - 诺曼完整指南
     */
    public static ItemStack createNormanAllParchment() {
        String title = "parchment.norman.all.title";
        String[] contents = {
                "parchment.norman.all.line1",
                "parchment.norman.all.line2",
                "parchment.norman.all.line3",
                "parchment.norman.all.line4",
                "parchment.norman.all.line5",
                "",
                "parchment.norman.all.line6",
                "parchment.norman.all.line7",
                "parchment.norman.all.line8"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.NORMAN, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建拜占庭村民羊皮纸
     */
    public static ItemStack createByzantineVillagerParchment() {
        String title = "parchment.byzantine.villager.title";
        String[] contents = {
                "parchment.byzantine.villager.governor",
                "parchment.byzantine.villager.centurion",
                "parchment.byzantine.villager.artisan",
                "parchment.byzantine.villager.scholar",
                "parchment.byzantine.villager.noblewoman",
                "parchment.byzantine.villager.slave",
                "parchment.byzantine.villager.merchant",
                "parchment.byzantine.villager.priest"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建拜占庭建筑羊皮纸
     */
    public static ItemStack createByzantineBuildingParchment() {
        String title = "parchment.byzantine.building.title";
        String[] contents = {
                "parchment.byzantine.building.governor_palace",
                "parchment.byzantine.building.fortress",
                "parchment.byzantine.building.workshop",
                "parchment.byzantine.building.library",
                "parchment.byzantine.building.baths",
                "parchment.byzantine.building.colosseum",
                "parchment.byzantine.building.cathedral",
                "parchment.byzantine.building.port"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建拜占庭物品羊皮纸
     */
    public static ItemStack createByzantineItemParchment() {
        String title = "parchment.byzantine.item.title";
        String[] contents = {
                "parchment.byzantine.item.scepter",
                "parchment.byzantine.item.greek_fire",
                "parchment.byzantine.item.wine",
                "parchment.byzantine.item.malvasia",
                "parchment.byzantine.item.feta_cheese",
                "parchment.byzantine.item.kebab",
                "parchment.byzantine.item.armor",
                "parchment.byzantine.item.purple_silk"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建拜占庭全书羊皮纸 - 拜占庭完整指南
     */
    public static ItemStack createByzantineAllParchment() {
        String title = "parchment.byzantine.all.title";
        String[] contents = {
                "parchment.byzantine.all.line1",
                "parchment.byzantine.all.line2",
                "parchment.byzantine.all.line3",
                "parchment.byzantine.all.line4",
                "parchment.byzantine.all.line5",
                "",
                "parchment.byzantine.all.line6",
                "parchment.byzantine.all.line7",
                "parchment.byzantine.all.line8"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.BYZANTINE, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建印度村民羊皮纸
     */
    public static ItemStack createHindiVillagerParchment() {
        String title = "parchment.hindi.villager.title";
        String[] contents = {
                "parchment.hindi.villager.raja",
                "parchment.hindi.villager.brahmin",
                "parchment.hindi.villager.kshatriya",
                "parchment.hindi.villager.vaishya",
                "parchment.hindi.villager.shudra",
                "parchment.hindi.villager.yogi",
                "parchment.hindi.villager.dancer",
                "parchment.hindi.villager.spice_merchant"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建印度建筑羊皮纸
     */
    public static ItemStack createHindiBuildingParchment() {
        String title = "parchment.hindi.building.title";
        String[] contents = {
                "parchment.hindi.building.palace",
                "parchment.hindi.building.temple",
                "parchment.hindi.building.market",
                "parchment.hindi.building.yoga_studio",
                "parchment.hindi.building.weaving_room",
                "parchment.hindi.building.spice_garden",
                "parchment.hindi.building.well",
                "parchment.hindi.building.dance_hall"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建印度物品羊皮纸
     */
    public static ItemStack createHindiItemParchment() {
        String title = "parchment.hindi.item.title";
        String[] contents = {
                "parchment.hindi.item.turmeric",
                "parchment.hindi.item.rice",
                "parchment.hindi.item.vegetable_curry",
                "parchment.hindi.item.chicken_curry",
                "parchment.hindi.item.gulab_jamun",
                "parchment.hindi.item.silk_cloth",
                "parchment.hindi.item.indian_sword",
                "parchment.hindi.item.spice_powder"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建印度全书羊皮纸 - 印度完整指南
     */
    public static ItemStack createHindiAllParchment() {
        String title = "parchment.hindi.all.title";
        String[] contents = {
                "parchment.hindi.all.line1",
                "parchment.hindi.all.line2",
                "parchment.hindi.all.line3",
                "parchment.hindi.all.line4",
                "parchment.hindi.all.line5",
                "",
                "parchment.hindi.all.line6",
                "parchment.hindi.all.line7",
                "parchment.hindi.all.line8"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.HINDI, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建玛雅村民羊皮纸
     */
    public static ItemStack createMayanVillagerParchment() {
        String title = "parchment.mayan.villager.title";
        String[] contents = {
                "parchment.mayan.villager.priest_king",
                "parchment.mayan.villager.warrior",
                "parchment.mayan.villager.astronomer",
                "parchment.mayan.villager.farmer",
                "parchment.mayan.villager.artisan",
                "parchment.mayan.villager.dancer",
                "parchment.mayan.villager.merchant",
                "parchment.mayan.villager.shaman"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建玛雅建筑羊皮纸
     */
    public static ItemStack createMayanBuildingParchment() {
        String title = "parchment.mayan.building.title";
        String[] contents = {
                "parchment.mayan.building.pyramid",
                "parchment.mayan.building.observatory",
                "parchment.mayan.building.ball_court",
                "parchment.mayan.building.altar",
                "parchment.mayan.building.steam_bath",
                "parchment.mayan.building.workshop",
                "parchment.mayan.building.farmland",
                "parchment.mayan.building.cenote"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建玛雅物品羊皮纸
     */
    public static ItemStack createMayanItemParchment() {
        String title = "parchment.mayan.item.title";
        String[] contents = {
                "parchment.mayan.item.corn",
                "parchment.mayan.item.cacao",
                "parchment.mayan.item.masa",
                "parchment.mayan.item.wah",
                "parchment.mayan.item.obsidian_tool",
                "parchment.mayan.item.scepter",
                "parchment.mayan.item.feather_headress",
                "parchment.mayan.item.jade_ornament"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.ITEM);
    }

    /**
     * 创建玛雅全书羊皮纸
     */
    public static ItemStack createMayanAllParchment() {
        String title = "parchment.mayan.all.title";
        String[] contents = {
                "parchment.mayan.all.line1",
                "parchment.mayan.all.line2",
                "parchment.mayan.all.line3",
                "parchment.mayan.all.line4",
                "parchment.mayan.all.line5",
                "",
                "parchment.mayan.all.line6",
                "parchment.mayan.all.line7",
                "parchment.mayan.all.line8"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.MAYAN, ItemMillParchment.ParchmentType.ALL);
    }
    
    /**
     * 创建日本村民羊皮纸
     */
    public static ItemStack createJapaneseVillagerParchment() {
        String title = "parchment.japanese.villager.title";
        String[] contents = {
                "parchment.japanese.villager.daimyo",
                "parchment.japanese.villager.samurai",
                "parchment.japanese.villager.monk",
                "parchment.japanese.villager.farmer",
                "parchment.japanese.villager.artisan",
                "parchment.japanese.villager.geisha",
                "parchment.japanese.villager.merchant",
                "parchment.japanese.villager.ninja"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.VILLAGER);
    }
    
    /**
     * 创建日本建筑羊皮纸
     */
    public static ItemStack createJapaneseBuildingParchment() {
        String title = "parchment.japanese.building.title";
        String[] contents = {
                "parchment.japanese.building.castle_keep",
                "parchment.japanese.building.shrine",
                "parchment.japanese.building.dojo",
                "parchment.japanese.building.tea_room",
                "parchment.japanese.building.rice_field",
                "parchment.japanese.building.onsen",
                "parchment.japanese.building.bamboo_forest",
                "parchment.japanese.building.zen_garden"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.BUILDING);
    }
    
    /**
     * 创建日本物品羊皮纸
     */
    public static ItemStack createJapaneseItemParchment() {
        String title = "parchment.japanese.item.title";
        String[] contents = {
                "parchment.japanese.item.katana",
                "parchment.japanese.item.yumi",
                "parchment.japanese.item.sake",
                "parchment.japanese.item.udon",
                "parchment.japanese.item.takoyaki",
                "parchment.japanese.item.samurai_armor",
                "parchment.japanese.item.kimono",
                "parchment.japanese.item.bamboo_product"
        };
        return ItemMillParchment.createParchment(title, contents, 
            ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.ITEM);
    }
    /**
     * 创建日本全书羊皮纸
     */
    public static ItemStack createJapaneseAllParchment() {
        String title = "parchment.japanese.all.title";
        String[] contents = {
                "parchment.japanese.all.line1",
                "parchment.japanese.all.line2",
                "parchment.japanese.all.line3",
                "parchment.japanese.all.line4",
                "parchment.japanese.all.line5",
                "",
                "parchment.japanese.all.line6",
                "parchment.japanese.all.line7",
                "parchment.japanese.all.line8"
        };
        return ItemMillParchment.createParchment(title, contents,
                ItemMillParchment.Culture.JAPANESE, ItemMillParchment.ParchmentType.ALL);
    }
}
