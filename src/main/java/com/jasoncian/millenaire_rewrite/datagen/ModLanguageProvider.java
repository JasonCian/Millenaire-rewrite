package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModItems;
import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * 语言文件数据生成器
 * 
 * 自动生成中文和英文语言文件，避免硬编码问题
 * 支持物品、方块、工具提示、GUI等各种翻译键
 */
public class ModLanguageProvider extends LanguageProvider {

    private final String locale;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, MillenaireRewrite.MOD_ID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if ("en_us".equals(locale)) {
            addEnglishTranslations();
        } else if ("zh_cn".equals(locale)) {
            addChineseTranslations();
        }
    }

    private void addEnglishTranslations() {
        // Creative Tab
        add("creativetab.millenaire_rewrite", "Millenaire Rewrite");

        // ================ Currency System ================
        add(ModItems.DENIER.get(), "Copper Denier");
        add(ModItems.DENIER_OR.get(), "Gold Denier");
        add(ModItems.DENIER_ARGENT.get(), "Silver Denier");

        // ================ Basic Materials ================
        add(ModItems.SILK.get(), "Silk");
        add(ModItems.OBSIDIAN_FLAKE.get(), "Obsidian Flake");
        add(ModItems.UNKNOWN_POWDER.get(), "Unknown Powder");
        add(ModItems.GALIANITE_DUST.get(), "Galianite Dust");

        // ================ Clothing Materials ================
        add(ModItems.WOOL_CLOTHES.get(), "Wool Clothes");
        add(ModItems.SILK_CLOTHES.get(), "Silk Clothes");

        // ================ Crops ================
        add(ModItems.TURMERIC.get(), "Turmeric");
        add(ModItems.RICE.get(), "Rice");
        add(ModItems.MAIZE.get(), "Maize");
        add(ModItems.GRAPES.get(), "Grapes");

        // ================ Norman Foods ================
        add(ModItems.CIDER_APPLE.get(), "Cider Apple");
        add(ModItems.CIDER.get(), "Cider");
        add(ModItems.CALVA.get(), "Calvados");
        add(ModItems.TRIPES.get(), "Tripes");
        add(ModItems.BOUDIN_NOIR.get(), "Blood Sausage");

        // ================ Indian Foods ================
        add(ModItems.VEG_CURRY.get(), "Vegetable Curry");
        add(ModItems.MURGH_CURRY.get(), "Chicken Curry");
        add(ModItems.RASGULLA.get(), "Rasgulla");

        // ================ Mayan Foods ================
        add(ModItems.CACAUHAA.get(), "Cacauhaa");
        add(ModItems.MASA.get(), "Masa");
        add(ModItems.WAH.get(), "Wah");

        // ================ Japanese Foods ================
        add(ModItems.SAKE.get(), "Sake");
        add(ModItems.UDON.get(), "Udon");
        add(ModItems.IKAYAKI.get(), "Ikayaki");

        // ================ Byzantine Foods ================
        add(ModItems.WINE.get(), "Wine");
        add(ModItems.MALVASIA_WINE.get(), "Malvasia Wine");
        add(ModItems.FETA.get(), "Feta Cheese");
        add(ModItems.SOUVLAKI.get(), "Souvlaki");

        // ================ Special Items ================
        add(ModItems.PURSE.get(), "Purse");
        add(ModItems.VILLAGE_SIGN.get(), "Village Sign");

        // ================ Norman Tools & Weapons ================
        add(ModItems.NORMAN_SWORD.get(), "Norman Sword");
        add(ModItems.NORMAN_AXE.get(), "Norman Axe");
        add(ModItems.NORMAN_PICKAXE.get(), "Norman Pickaxe");
        add(ModItems.NORMAN_SHOVEL.get(), "Norman Shovel");
        add(ModItems.NORMAN_HOE.get(), "Norman Hoe");

        // ================ Norman Armor ================
        add(ModItems.NORMAN_HELMET.get(), "Norman Helmet");
        add(ModItems.NORMAN_CHESTPLATE.get(), "Norman Chestplate");
        add(ModItems.NORMAN_LEGGINGS.get(), "Norman Leggings");
        add(ModItems.NORMAN_BOOTS.get(), "Norman Boots");

        // ================ Mayan Tools ================
        add(ModItems.MAYAN_AXE.get(), "Mayan Obsidian Axe");
        add(ModItems.MAYAN_PICKAXE.get(), "Mayan Obsidian Pickaxe");
        add(ModItems.MAYAN_SHOVEL.get(), "Mayan Obsidian Shovel");
        add(ModItems.MAYAN_HOE.get(), "Mayan Obsidian Hoe");
        add(ModItems.MAYAN_MACE.get(), "Mayan Obsidian Mace");

        // ================ Byzantine Tools & Weapons ================
        add(ModItems.BYZANTINE_MACE.get(), "Byzantine Mace");

        // ================ Byzantine Armor ================
        add(ModItems.BYZANTINE_HELMET.get(), "Byzantine Helmet");
        add(ModItems.BYZANTINE_CHESTPLATE.get(), "Byzantine Chestplate");
        add(ModItems.BYZANTINE_LEGGINGS.get(), "Byzantine Leggings");
        add(ModItems.BYZANTINE_BOOTS.get(), "Byzantine Boots");

        // ================ Japanese Tools & Weapons ================
        add(ModItems.JAPANESE_SWORD.get(), "Japanese Sword");
        add(ModItems.JAPANESE_BOW.get(), "Japanese Bow");

        // ================ Japanese Armor ================
        add(ModItems.JAPANESE_GUARD_HELMET.get(), "Japanese Guard Helmet");
        add(ModItems.JAPANESE_GUARD_CHESTPLATE.get(), "Japanese Guard Chestplate");
        add(ModItems.JAPANESE_GUARD_LEGGINGS.get(), "Japanese Guard Leggings");
        add(ModItems.JAPANESE_GUARD_BOOTS.get(), "Japanese Guard Boots");

        add(ModItems.JAPANESE_BLUE_HELMET.get(), "Japanese Blue Samurai Helmet");
        add(ModItems.JAPANESE_BLUE_CHESTPLATE.get(), "Japanese Blue Samurai Chestplate");
        add(ModItems.JAPANESE_BLUE_LEGGINGS.get(), "Japanese Blue Samurai Leggings");
        add(ModItems.JAPANESE_BLUE_BOOTS.get(), "Japanese Blue Samurai Boots");

        add(ModItems.JAPANESE_RED_HELMET.get(), "Japanese Red Samurai Helmet");
        add(ModItems.JAPANESE_RED_CHESTPLATE.get(), "Japanese Red Samurai Chestplate");
        add(ModItems.JAPANESE_RED_LEGGINGS.get(), "Japanese Red Samurai Leggings");
        add(ModItems.JAPANESE_RED_BOOTS.get(), "Japanese Red Samurai Boots");

        // ================ Special Armor ================
        add(ModItems.MAYAN_QUEST_CROWN.get(), "Mayan Quest Crown");

        // ================ Magic Items - Wands ================
        add(ModItems.WAND_SUMMONING.get(), "Summoning Wand");
        add(ModItems.WAND_NEGATION.get(), "Negation Wand");
        add(ModItems.WAND_CREATIVE.get(), "Creative Wand");
        add(ModItems.TUNING_FORK.get(), "Tuning Fork");

        // ================ Magic Items - Amulets ================
        add(ModItems.AMULET_SKOLL_HATI.get(), "Skoll and Hati Amulet");
        add(ModItems.AMULET_ALCHEMIST.get(), "Alchemist Amulet");
        add(ModItems.AMULET_VISHNU.get(), "Vishnu Amulet");
        add(ModItems.AMULET_YGGDRASIL.get(), "Yggdrasil Amulet");

        // ================ Parchments ================
        // Norman Parchments
        add(ModItems.PARCHMENT_NORMAN_VILLAGER.get(), "Norman Villager Parchment");
        add(ModItems.PARCHMENT_NORMAN_BUILDING.get(), "Norman Building Parchment");
        add(ModItems.PARCHMENT_NORMAN_ITEM.get(), "Norman Item Parchment");
        add(ModItems.PARCHMENT_NORMAN_ALL.get(), "Norman Complete Parchment");

        // Byzantine Parchments
        add(ModItems.PARCHMENT_BYZANTINE_VILLAGER.get(), "Byzantine Villager Parchment");
        add(ModItems.PARCHMENT_BYZANTINE_BUILDING.get(), "Byzantine Building Parchment");
        add(ModItems.PARCHMENT_BYZANTINE_ITEM.get(), "Byzantine Item Parchment");
        add(ModItems.PARCHMENT_BYZANTINE_ALL.get(), "Byzantine Complete Parchment");

        // Hindi Parchments
        add(ModItems.PARCHMENT_HINDI_VILLAGER.get(), "Hindi Villager Parchment");
        add(ModItems.PARCHMENT_HINDI_BUILDING.get(), "Hindi Building Parchment");
        add(ModItems.PARCHMENT_HINDI_ITEM.get(), "Hindi Item Parchment");
        add(ModItems.PARCHMENT_HINDI_ALL.get(), "Hindi Complete Parchment");

        // Mayan Parchments
        add(ModItems.PARCHMENT_MAYAN_VILLAGER.get(), "Mayan Villager Parchment");
        add(ModItems.PARCHMENT_MAYAN_BUILDING.get(), "Mayan Building Parchment");
        add(ModItems.PARCHMENT_MAYAN_ITEM.get(), "Mayan Item Parchment");
        add(ModItems.PARCHMENT_MAYAN_ALL.get(), "Mayan Complete Parchment");

        // Japanese Parchments
        add(ModItems.PARCHMENT_JAPANESE_VILLAGER.get(), "Japanese Villager Parchment");
        add(ModItems.PARCHMENT_JAPANESE_BUILDING.get(), "Japanese Building Parchment");
        add(ModItems.PARCHMENT_JAPANESE_ITEM.get(), "Japanese Item Parchment");
        add(ModItems.PARCHMENT_JAPANESE_ALL.get(), "Japanese Complete Parchment");

        // ================ Blocks ================
        add(ModBlocks.VILLAGE_STONE.get(), "Village Stone");

        // ================ Tooltips and UI ================
        // Purse tooltips
        add("item.millenaire_rewrite.purse.contents", "Purse Contents: %s Gold %s Silver %s Copper");
        add("item.millenaire_rewrite.purse.loose_coins", "You have %s loose coins in inventory,");
        add("item.millenaire_rewrite.purse.collect_hint", "Sneak + Right-click to collect into purse");
        add("item.millenaire_rewrite.purse.quick_withdraw", "Double-click to quickly withdraw all currency");
        add("item.millenaire_rewrite.purse.collected", "Collected to purse: %s Gold %s Silver %s Copper");
        add("item.millenaire_rewrite.purse.no_loose_coins", "No loose coins in inventory to collect");
        add("item.millenaire_rewrite.purse.tooltip.storage", "Currency Storage:");
        add("item.millenaire_rewrite.purse.tooltip.gold", "  Gold Deniers: %s");
        add("item.millenaire_rewrite.purse.tooltip.silver", "  Silver Deniers: %s");
        add("item.millenaire_rewrite.purse.tooltip.copper", "  Copper Deniers: %s");
        add("item.millenaire_rewrite.purse.tooltip.total_value", "Total Value: %s Copper Deniers");
        add("item.millenaire_rewrite.purse.tooltip.right_click", "Right-click to view contents");
        add("item.millenaire_rewrite.purse.tooltip.sneak_collect", "Sneak + Right-click to collect loose coins");
        add("item.millenaire_rewrite.purse.tooltip.double_click", "Double-click to withdraw all currency");
        add("item.millenaire_rewrite.purse.empty", "Purse is empty");
        add("item.millenaire_rewrite.purse.withdrawn", "Withdrawn from purse: %s Gold %s Silver %s Copper");

        // Village Sign tooltips
        add("item.millenaire_rewrite.village_sign.info", "Village Information:");
        add("item.millenaire_rewrite.village_sign.name", "  Name: %s");
        add("item.millenaire_rewrite.village_sign.culture", "  Culture: %s");
        add("item.millenaire_rewrite.village_sign.type", "  Type: %s");
        add("item.millenaire_rewrite.village_sign.place_hint", "Right-click ground to establish village");
        add("item.millenaire_rewrite.village_sign.blank", "Blank Village Sign");
        add("item.millenaire_rewrite.village_sign.configure_hint", "Need to configure village information");
        add("item.millenaire_rewrite.village_sign.established", "Village Sign: %s (%s %s)");
        add("item.millenaire_rewrite.village_sign.unnamed", "Unnamed Village");

        // Amulet tooltips
        add("item.millenaire_rewrite.amulet_alchemist.tooltip", "Detects nearby ores");
        add("item.millenaire_rewrite.amulet.ore_detected", "Ore Level: %s/10");
        add("item.millenaire_rewrite.amulet_vishnu.tooltip", "Detects nearby creatures");
        add("item.millenaire_rewrite.amulet.danger_level", "Danger Level: %s/10");
        add("item.millenaire_rewrite.amulet_yggdrasil.tooltip", "Shows altitude information");
        add("item.millenaire_rewrite.amulet.altitude", "Altitude: %s");
        add("item.millenaire_rewrite.amulet_skoll_hati.tooltip", "Controls day/night cycle");
        add("item.millenaire_rewrite.amulet.day_night_control", "Right-click to switch time");

        // Wand tooltips
        add("item.millenaire_rewrite.tuning_fork.tooltip.1", "Block inspection tool");
        add("item.millenaire_rewrite.tuning_fork.tooltip.2", "Right-click blocks to get information");
        add("item.millenaire_rewrite.summoning_wand.tooltip.1", "Import buildings from templates");
        add("item.millenaire_rewrite.summoning_wand.tooltip.2", "Use to spawn village buildings");
        add("item.millenaire_rewrite.negation_wand.tooltip.1", "Export buildings to templates");
        add("item.millenaire_rewrite.negation_wand.tooltip.2", "Use to save building blueprints");
        add("item.millenaire_rewrite.creative_wand.tooltip.1", "Manage crop permissions and chest locks");
        add("item.millenaire_rewrite.creative_wand.tooltip.2", "Admin tool for village management");
        add("item.millenaire_rewrite.wand.tuning_fork.tooltip", "Block inspection tool");
        add("item.millenaire_rewrite.wand.summoning.tooltip", "Import buildings from templates");
        add("item.millenaire_rewrite.wand.negation.tooltip", "Export buildings to templates");
        add("item.millenaire_rewrite.wand.creative.tooltip", "Manage crop permissions and chest locks");

        // Debug wand messages
        add("debug.millenaire_rewrite.wand.position", "Position: %d, %d, %d");
        add("debug.millenaire_rewrite.wand.creative.help", "Creative Wand - Right-click to manage crop permissions and chest locks");
        add("debug.millenaire_rewrite.wand.block_info", "Block: %s at %d, %d, %d");
        add("debug.millenaire_rewrite.wand.summoning.use", "Summoning Wand activated at %d, %d, %d");
        add("debug.millenaire_rewrite.wand.negation.use", "Negation Wand activated at %d, %d, %d");
        add("debug.millenaire_rewrite.wand.creative.use", "Creative Wand activated at %d, %d, %d");
        add("debug.millenaire_rewrite.wand.tuning_fork.help", "Tuning Fork - Right-click blocks to inspect their properties");
        add("debug.millenaire_rewrite.wand.summoning.help", "Summoning Wand - Import and place building templates");
        add("debug.millenaire_rewrite.wand.negation.help", "Negation Wand - Export building areas to templates");

        // Parchment content headers
        add("item.millenaire_rewrite.parchment.culture.norman", "Norman");
        add("item.millenaire_rewrite.parchment.culture.byzantine", "Byzantine");
        add("item.millenaire_rewrite.parchment.culture.hindi", "Hindi");
        add("item.millenaire_rewrite.parchment.culture.mayan", "Mayan");
        add("item.millenaire_rewrite.parchment.culture.japanese", "Japanese");

        add("item.millenaire_rewrite.parchment.type.villager", "Villager Guide");
        add("item.millenaire_rewrite.parchment.type.building", "Building Guide");
        add("item.millenaire_rewrite.parchment.type.item", "Item Guide");
        add("item.millenaire_rewrite.parchment.type.all", "Complete Guide");

        // Currency formatting
        add("currency.millenaire_rewrite.gold", "Gold");
        add("currency.millenaire_rewrite.silver", "Silver");
        add("currency.millenaire_rewrite.copper", "Copper");
        add("currency.millenaire_rewrite.denier", "Denier");
        add("currency.millenaire_rewrite.zero", "0 Copper Denier");
    }

    private void addChineseTranslations() {
        // Creative Tab
        add("creativetab.millenaire_rewrite", "千年村庄重制版");

        // ================ Currency System ================
        add(ModItems.DENIER.get(), "铜第纳尔");
        add(ModItems.DENIER_OR.get(), "金第纳尔");
        add(ModItems.DENIER_ARGENT.get(), "银第纳尔");

        // ================ Basic Materials ================
        add(ModItems.SILK.get(), "丝绸");
        add(ModItems.OBSIDIAN_FLAKE.get(), "黑曜石碎片");
        add(ModItems.UNKNOWN_POWDER.get(), "未知粉末");
        add(ModItems.GALIANITE_DUST.get(), "加里亚奈特粉尘");

        // ================ Clothing Materials ================
        add(ModItems.WOOL_CLOTHES.get(), "羊毛衣物");
        add(ModItems.SILK_CLOTHES.get(), "丝绸衣物");

        // ================ Crops ================
        add(ModItems.TURMERIC.get(), "姜黄");
        add(ModItems.RICE.get(), "水稻");
        add(ModItems.MAIZE.get(), "玉米");
        add(ModItems.GRAPES.get(), "葡萄");

        // ================ Norman Foods ================
        add(ModItems.CIDER_APPLE.get(), "苹果酒苹果");
        add(ModItems.CIDER.get(), "苹果酒");
        add(ModItems.CALVA.get(), "卡尔瓦多斯");
        add(ModItems.TRIPES.get(), "内脏");
        add(ModItems.BOUDIN_NOIR.get(), "血肠");

        // ================ Indian Foods ================
        add(ModItems.VEG_CURRY.get(), "蔬菜咖喱");
        add(ModItems.MURGH_CURRY.get(), "鸡肉咖喱");
        add(ModItems.RASGULLA.get(), "印度甜球");

        // ================ Mayan Foods ================
        add(ModItems.CACAUHAA.get(), "可可亚");
        add(ModItems.MASA.get(), "玛萨");
        add(ModItems.WAH.get(), "玛雅特色食物");

        // ================ Japanese Foods ================
        add(ModItems.SAKE.get(), "清酒");
        add(ModItems.UDON.get(), "乌冬面");
        add(ModItems.IKAYAKI.get(), "鱿鱼烧");

        // ================ Byzantine Foods ================
        add(ModItems.WINE.get(), "葡萄酒");
        add(ModItems.MALVASIA_WINE.get(), "玛尔瓦西亚葡萄酒");
        add(ModItems.FETA.get(), "费塔奶酪");
        add(ModItems.SOUVLAKI.get(), "烤肉串");

        // ================ Special Items ================
        add(ModItems.PURSE.get(), "钱包");
        add(ModItems.VILLAGE_SIGN.get(), "村庄标牌");

        // ================ Norman Tools & Weapons ================
        add(ModItems.NORMAN_SWORD.get(), "诺曼剑");
        add(ModItems.NORMAN_AXE.get(), "诺曼斧");
        add(ModItems.NORMAN_PICKAXE.get(), "诺曼镐");
        add(ModItems.NORMAN_SHOVEL.get(), "诺曼铲");
        add(ModItems.NORMAN_HOE.get(), "诺曼锄");

        // ================ Norman Armor ================
        add(ModItems.NORMAN_HELMET.get(), "诺曼头盔");
        add(ModItems.NORMAN_CHESTPLATE.get(), "诺曼胸甲");
        add(ModItems.NORMAN_LEGGINGS.get(), "诺曼护腿");
        add(ModItems.NORMAN_BOOTS.get(), "诺曼靴子");

        // ================ Mayan Tools ================
        add(ModItems.MAYAN_AXE.get(), "玛雅黑曜石斧");
        add(ModItems.MAYAN_PICKAXE.get(), "玛雅黑曜石镐");
        add(ModItems.MAYAN_SHOVEL.get(), "玛雅黑曜石铲");
        add(ModItems.MAYAN_HOE.get(), "玛雅黑曜石锄");
        add(ModItems.MAYAN_MACE.get(), "玛雅黑曜石权杖");

        // ================ Byzantine Tools & Weapons ================
        add(ModItems.BYZANTINE_MACE.get(), "拜占庭权杖");

        // ================ Byzantine Armor ================
        add(ModItems.BYZANTINE_HELMET.get(), "拜占庭头盔");
        add(ModItems.BYZANTINE_CHESTPLATE.get(), "拜占庭胸甲");
        add(ModItems.BYZANTINE_LEGGINGS.get(), "拜占庭护腿");
        add(ModItems.BYZANTINE_BOOTS.get(), "拜占庭靴子");

        // ================ Japanese Tools & Weapons ================
        add(ModItems.JAPANESE_SWORD.get(), "日本刀");
        add(ModItems.JAPANESE_BOW.get(), "日本弓");

        // ================ Japanese Armor ================
        add(ModItems.JAPANESE_GUARD_HELMET.get(), "日本守卫头盔");
        add(ModItems.JAPANESE_GUARD_CHESTPLATE.get(), "日本守卫胸甲");
        add(ModItems.JAPANESE_GUARD_LEGGINGS.get(), "日本守卫护腿");
        add(ModItems.JAPANESE_GUARD_BOOTS.get(), "日本守卫靴子");

        add(ModItems.JAPANESE_BLUE_HELMET.get(), "日本蓝色武士头盔");
        add(ModItems.JAPANESE_BLUE_CHESTPLATE.get(), "日本蓝色武士胸甲");
        add(ModItems.JAPANESE_BLUE_LEGGINGS.get(), "日本蓝色武士护腿");
        add(ModItems.JAPANESE_BLUE_BOOTS.get(), "日本蓝色武士靴子");

        add(ModItems.JAPANESE_RED_HELMET.get(), "日本红色武士头盔");
        add(ModItems.JAPANESE_RED_CHESTPLATE.get(), "日本红色武士胸甲");
        add(ModItems.JAPANESE_RED_LEGGINGS.get(), "日本红色武士护腿");
        add(ModItems.JAPANESE_RED_BOOTS.get(), "日本红色武士靴子");

        // ================ Special Armor ================
        add(ModItems.MAYAN_QUEST_CROWN.get(), "玛雅任务王冠");

        // ================ Magic Items - Wands ================
        add(ModItems.WAND_SUMMONING.get(), "召唤法杖");
        add(ModItems.WAND_NEGATION.get(), "否定法杖");
        add(ModItems.WAND_CREATIVE.get(), "创意法杖");
        add(ModItems.TUNING_FORK.get(), "调音叉");

        // ================ Magic Items - Amulets ================
        add(ModItems.AMULET_SKOLL_HATI.get(), "斯库尔与哈提护符");
        add(ModItems.AMULET_ALCHEMIST.get(), "炼金术师护符");
        add(ModItems.AMULET_VISHNU.get(), "毗湿奴护符");
        add(ModItems.AMULET_YGGDRASIL.get(), "世界树护符");

        // ================ Parchments ================
        // Norman Parchments
        add(ModItems.PARCHMENT_NORMAN_VILLAGER.get(), "诺曼村民羊皮纸");
        add(ModItems.PARCHMENT_NORMAN_BUILDING.get(), "诺曼建筑羊皮纸");
        add(ModItems.PARCHMENT_NORMAN_ITEM.get(), "诺曼物品羊皮纸");
        add(ModItems.PARCHMENT_NORMAN_ALL.get(), "诺曼完整羊皮纸");

        // Byzantine Parchments
        add(ModItems.PARCHMENT_BYZANTINE_VILLAGER.get(), "拜占庭村民羊皮纸");
        add(ModItems.PARCHMENT_BYZANTINE_BUILDING.get(), "拜占庭建筑羊皮纸");
        add(ModItems.PARCHMENT_BYZANTINE_ITEM.get(), "拜占庭物品羊皮纸");
        add(ModItems.PARCHMENT_BYZANTINE_ALL.get(), "拜占庭完整羊皮纸");

        // Hindi Parchments
        add(ModItems.PARCHMENT_HINDI_VILLAGER.get(), "印度村民羊皮纸");
        add(ModItems.PARCHMENT_HINDI_BUILDING.get(), "印度建筑羊皮纸");
        add(ModItems.PARCHMENT_HINDI_ITEM.get(), "印度物品羊皮纸");
        add(ModItems.PARCHMENT_HINDI_ALL.get(), "印度完整羊皮纸");

        // Mayan Parchments
        add(ModItems.PARCHMENT_MAYAN_VILLAGER.get(), "玛雅村民羊皮纸");
        add(ModItems.PARCHMENT_MAYAN_BUILDING.get(), "玛雅建筑羊皮纸");
        add(ModItems.PARCHMENT_MAYAN_ITEM.get(), "玛雅物品羊皮纸");
        add(ModItems.PARCHMENT_MAYAN_ALL.get(), "玛雅完整羊皮纸");

        // Japanese Parchments
        add(ModItems.PARCHMENT_JAPANESE_VILLAGER.get(), "日本村民羊皮纸");
        add(ModItems.PARCHMENT_JAPANESE_BUILDING.get(), "日本建筑羊皮纸");
        add(ModItems.PARCHMENT_JAPANESE_ITEM.get(), "日本物品羊皮纸");
        add(ModItems.PARCHMENT_JAPANESE_ALL.get(), "日本完整羊皮纸");

        // ================ Blocks ================
        add(ModBlocks.VILLAGE_STONE.get(), "村庄石头");

        // ================ Tooltips and UI ================
        // Purse tooltips
        add("item.millenaire_rewrite.purse.contents", "钱包内容: %s金 %s银 %s铜");
        add("item.millenaire_rewrite.purse.loose_coins", "背包中有 %s 个散币，");
        add("item.millenaire_rewrite.purse.collect_hint", "潜行右键收集到钱包");
        add("item.millenaire_rewrite.purse.quick_withdraw", "双击快速取出所有货币");
        add("item.millenaire_rewrite.purse.collected", "收集到钱包: %s金 %s银 %s铜");
        add("item.millenaire_rewrite.purse.no_loose_coins", "背包中没有散币可收集");
        add("item.millenaire_rewrite.purse.tooltip.storage", "货币存储:");
        add("item.millenaire_rewrite.purse.tooltip.gold", "  金第纳尔: %s");
        add("item.millenaire_rewrite.purse.tooltip.silver", "  银第纳尔: %s");
        add("item.millenaire_rewrite.purse.tooltip.copper", "  铜第纳尔: %s");
        add("item.millenaire_rewrite.purse.tooltip.total_value", "总价值: %s 铜第纳尔");
        add("item.millenaire_rewrite.purse.tooltip.right_click", "右键查看内容");
        add("item.millenaire_rewrite.purse.tooltip.sneak_collect", "潜行右键收集散币");
        add("item.millenaire_rewrite.purse.tooltip.double_click", "双击取出所有货币");
        add("item.millenaire_rewrite.purse.empty", "钱包是空的");
        add("item.millenaire_rewrite.purse.withdrawn", "从钱包取出: %s金 %s银 %s铜");

        // Village Sign tooltips
        add("item.millenaire_rewrite.village_sign.info", "村庄信息:");
        add("item.millenaire_rewrite.village_sign.name", "  名称: %s");
        add("item.millenaire_rewrite.village_sign.culture", "  文化: %s");
        add("item.millenaire_rewrite.village_sign.type", "  类型: %s");
        add("item.millenaire_rewrite.village_sign.place_hint", "右键地面建立村庄");
        add("item.millenaire_rewrite.village_sign.blank", "空白村庄标牌");
        add("item.millenaire_rewrite.village_sign.configure_hint", "需要配置村庄信息");
        add("item.millenaire_rewrite.village_sign.established", "村庄标牌: %s (%s %s)");
        add("item.millenaire_rewrite.village_sign.unnamed", "未命名村庄");

        // Amulet tooltips
        add("item.millenaire_rewrite.amulet_alchemist.tooltip", "检测附近的矿物");
        add("item.millenaire_rewrite.amulet.ore_detected", "矿物级别: %s/10");
        add("item.millenaire_rewrite.amulet_vishnu.tooltip", "检测附近的生物");
        add("item.millenaire_rewrite.amulet.danger_level", "危险级别: %s/10");
        add("item.millenaire_rewrite.amulet_yggdrasil.tooltip", "显示高度信息");
        add("item.millenaire_rewrite.amulet.altitude", "高度: %s");
        add("item.millenaire_rewrite.amulet_skoll_hati.tooltip", "控制昼夜循环");
        add("item.millenaire_rewrite.amulet.day_night_control", "右键切换时间");

        // Wand tooltips
        add("item.millenaire_rewrite.tuning_fork.tooltip.1", "方块检查工具");
        add("item.millenaire_rewrite.tuning_fork.tooltip.2", "右键方块获取信息");
        add("item.millenaire_rewrite.summoning_wand.tooltip.1", "从模板导入建筑");
        add("item.millenaire_rewrite.summoning_wand.tooltip.2", "用于生成村庄建筑");
        add("item.millenaire_rewrite.negation_wand.tooltip.1", "将建筑导出为模板");
        add("item.millenaire_rewrite.negation_wand.tooltip.2", "用于保存建筑蓝图");
        add("item.millenaire_rewrite.creative_wand.tooltip.1", "管理作物权限和箱子锁定");
        add("item.millenaire_rewrite.creative_wand.tooltip.2", "村庄管理管理员工具");
        add("item.millenaire_rewrite.wand.tuning_fork.tooltip", "方块检查工具");
        add("item.millenaire_rewrite.wand.summoning.tooltip", "从模板导入建筑");
        add("item.millenaire_rewrite.wand.negation.tooltip", "将建筑导出为模板");
        add("item.millenaire_rewrite.wand.creative.tooltip", "管理作物权限和箱子锁定");

        // Debug wand messages
        add("debug.millenaire_rewrite.wand.position", "位置: %d, %d, %d");
        add("debug.millenaire_rewrite.wand.creative.help", "创意法杖 - 右键管理作物权限和箱子锁定");
        add("debug.millenaire_rewrite.wand.block_info", "方块: %s 位于 %d, %d, %d");
        add("debug.millenaire_rewrite.wand.summoning.use", "召唤法杖在 %d, %d, %d 处激活");
        add("debug.millenaire_rewrite.wand.negation.use", "否定法杖在 %d, %d, %d 处激活");
        add("debug.millenaire_rewrite.wand.creative.use", "创意法杖在 %d, %d, %d 处激活");
        add("debug.millenaire_rewrite.wand.tuning_fork.help", "调音叉 - 右键方块检查其属性");
        add("debug.millenaire_rewrite.wand.summoning.help", "召唤法杖 - 导入并放置建筑模板");
        add("debug.millenaire_rewrite.wand.negation.help", "否定法杖 - 将建筑区域导出为模板");

        // Parchment content headers
        add("item.millenaire_rewrite.parchment.culture.norman", "诺曼");
        add("item.millenaire_rewrite.parchment.culture.byzantine", "拜占庭");
        add("item.millenaire_rewrite.parchment.culture.hindi", "印度");
        add("item.millenaire_rewrite.parchment.culture.mayan", "玛雅");
        add("item.millenaire_rewrite.parchment.culture.japanese", "日本");

        add("item.millenaire_rewrite.parchment.type.villager", "村民指南");
        add("item.millenaire_rewrite.parchment.type.building", "建筑指南");
        add("item.millenaire_rewrite.parchment.type.item", "物品指南");
        add("item.millenaire_rewrite.parchment.type.all", "完整指南");

        // Currency formatting
        add("currency.millenaire_rewrite.gold", "金");
        add("currency.millenaire_rewrite.silver", "银");
        add("currency.millenaire_rewrite.copper", "铜");
        add("currency.millenaire_rewrite.denier", "第纳尔");
        add("currency.millenaire_rewrite.zero", "0 铜第纳尔");
    }
}
