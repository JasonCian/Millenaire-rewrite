package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import com.jasoncian.millenaire_rewrite.core.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * 语言文件数据生成器 - 自动生成多语言翻译文件
 *
 * 自动生成中文和英文语言文件，避免硬编码问题
 * 支持物品、方块、工具提示等各种翻译键的生成
 *
 * 功能特性：
 * - 英文翻译生成（en_us）
 * - 中文翻译生成（zh_cn）
 * - 物品和方块名称翻译
 * - UI文本和提示翻译
 *
 * @author JasonCian
 * @version 0.1.0-alpha
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
        // Creative Tabs
        add("creativetab.millenaire_rewrite.blocks", "Millenaire Blocks");
        add("creativetab.millenaire_rewrite.huaxia", "Huaxia Culture"); // 华夏文化标签页
        add("creativetab.millenaire_rewrite.norman", "Norman Culture");
        add("creativetab.millenaire_rewrite.byzantine", "Byzantine Culture");
        add("creativetab.millenaire_rewrite.japanese", "Japanese Culture");
        add("creativetab.millenaire_rewrite.mayan", "Mayan Culture");
        add("creativetab.millenaire_rewrite.indian", "Indian Culture");
        add("creativetab.millenaire_rewrite.seljuk", "Seljuk Culture");
        add("creativetab.millenaire_rewrite.inuit", "Inuit Culture");
        add("creativetab.millenaire_rewrite.food", "Millenaire Foods");
        add("creativetab.millenaire_rewrite.misc", "Millenaire Misc");

        // Legacy Creative Tab (keep for compatibility)
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
        add(ModItems.BALCHE.get(), "Balche");
        add(ModItems.SIKILPAH.get(), "Sikilpah");

        // ================ Japanese Foods ================
        add(ModItems.SAKE.get(), "Sake");
        add(ModItems.UDON.get(), "Udon");
        add(ModItems.IKAYAKI.get(), "Ikayaki");
        add(ModItems.CHERRIES.get(), "Cherries");
        add(ModItems.CHERRY_BLOSSOM.get(), "Cherry Blossom");

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
        add(ModItems.BYZANTINE_PICKAXE.get(), "Byzantine Pickaxe");
        add(ModItems.BYZANTINE_AXE.get(), "Byzantine Axe");
        add(ModItems.BYZANTINE_SHOVEL.get(), "Byzantine Shovel");
        add(ModItems.BYZANTINE_HOE.get(), "Byzantine Hoe");

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

        // Special Parchments
        add(ModItems.PARCHMENT_VILLAGE_SCROLL.get(), "Village Scroll");
        add(ModItems.PARCHMENT_SADHU.get(), "Sadhu Book");

        // ================ Paint Buckets ================
        add(ModItems.PAINT_BUCKET_WHITE.get(), "White Paint Bucket");
        add(ModItems.PAINT_BUCKET_ORANGE.get(), "Orange Paint Bucket");
        add(ModItems.PAINT_BUCKET_MAGENTA.get(), "Magenta Paint Bucket");
        add(ModItems.PAINT_BUCKET_LIGHT_BLUE.get(), "Light Blue Paint Bucket");
        add(ModItems.PAINT_BUCKET_YELLOW.get(), "Yellow Paint Bucket");
        add(ModItems.PAINT_BUCKET_LIME.get(), "Lime Paint Bucket");
        add(ModItems.PAINT_BUCKET_PINK.get(), "Pink Paint Bucket");
        add(ModItems.PAINT_BUCKET_GRAY.get(), "Gray Paint Bucket");
        add(ModItems.PAINT_BUCKET_LIGHT_GRAY.get(), "Light Gray Paint Bucket");
        add(ModItems.PAINT_BUCKET_CYAN.get(), "Cyan Paint Bucket");
        add(ModItems.PAINT_BUCKET_PURPLE.get(), "Purple Paint Bucket");
        add(ModItems.PAINT_BUCKET_BLUE.get(), "Blue Paint Bucket");
        add(ModItems.PAINT_BUCKET_BROWN.get(), "Brown Paint Bucket");
        add(ModItems.PAINT_BUCKET_GREEN.get(), "Green Paint Bucket");
        add(ModItems.PAINT_BUCKET_RED.get(), "Red Paint Bucket");
        add(ModItems.PAINT_BUCKET_BLACK.get(), "Black Paint Bucket");

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
        add("debug.millenaire_rewrite.wand.position", "Position: %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.creative.help",
                "Creative Wand - Right-click to manage crop permissions and chest locks");
        add("debug.millenaire_rewrite.wand.block_info", "Block: %s at %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.summoning.use", "Summoning Wand activated at %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.negation.use", "Negation Wand activated at %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.creative.use", "Creative Wand activated at %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.tuning_fork.help",
                "Tuning Fork - Right-click blocks to inspect their properties");
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

        // ================ Magic Amulets ================
        add(ModItems.AMULET_VISHNU.get(), "Vishnu Amulet");
        add(ModItems.AMULET_ALCHEMIST.get(), "Alchemist Amulet");
        add(ModItems.AMULET_YGGDRASIL.get(), "Yggdrasil Amulet");
        add(ModItems.AMULET_SKOLL_HATI.get(), "Skoll Hati Amulet");

        // ================ Decorative Items ================
        add(ModItems.TAPESTRY.get(), "Tapestry");
        add(ModItems.INDIAN_STATUE.get(), "Indian Statue");
        add(ModItems.MAYAN_STATUE.get(), "Mayan Statue");
        add(ModItems.BYZANTINE_ICON_SMALL.get(), "Small Byzantine Icon");
        add(ModItems.BYZANTINE_ICON_MEDIUM.get(), "Medium Byzantine Icon");
        add(ModItems.BYZANTINE_ICON_LARGE.get(), "Large Byzantine Icon");

        // ================ Special Tools & Materials ================
        add(ModItems.BRICK_MOULD.get(), "Brick Mould");
        add(ModItems.OLIVES.get(), "Olives");
        add(ModItems.OLIVE_OIL.get(), "Olive Oil");

        // ================ Inuit Civilization ================
        add(ModItems.INUIT_TRIDENT.get(), "Inuit Trident");
        add(ModItems.INUIT_BOW.get(), "Inuit Bow");
        add(ModItems.ULU.get(), "Ulu Knife");
        add(ModItems.FUR_HELMET.get(), "Fur Helmet");
        add(ModItems.FUR_CHESTPLATE.get(), "Fur Chestplate");
        add(ModItems.FUR_LEGGINGS.get(), "Fur Leggings");
        add(ModItems.FUR_BOOTS.get(), "Fur Boots");
        add(ModItems.BEAR_MEAT_RAW.get(), "Raw Bear Meat");
        add(ModItems.BEAR_MEAT_COOKED.get(), "Cooked Bear Meat");
        add(ModItems.WOLF_MEAT_RAW.get(), "Raw Wolf Meat");
        add(ModItems.WOLF_MEAT_COOKED.get(), "Cooked Wolf Meat");
        add(ModItems.SEAFOOD_RAW.get(), "Raw Seafood");
        add(ModItems.SEAFOOD_COOKED.get(), "Cooked Seafood");
        add(ModItems.INUIT_BEAR_STEW.get(), "Inuit Bear Stew");
        add(ModItems.INUIT_MEATY_STEW.get(), "Inuit Meaty Stew");
        add(ModItems.INUIT_POTATO_STEW.get(), "Inuit Potato Stew");
        add(ModItems.TANNED_HIDE.get(), "Tanned Hide");
        add(ModItems.HIDE_HANGING.get(), "Hide Hanging");

        // ================ Seljuk Civilization ================
        add(ModItems.SELJUK_SCIMITAR.get(), "Seljuk Scimitar");
        add(ModItems.SELJUK_BOW.get(), "Seljuk Bow");
        add(ModItems.SELJUK_TURBAN.get(), "Seljuk Turban");
        add(ModItems.SELJUK_HELMET.get(), "Seljuk Helmet");
        add(ModItems.SELJUK_CHESTPLATE.get(), "Seljuk Chestplate");
        add(ModItems.SELJUK_LEGGINGS.get(), "Seljuk Leggings");
        add(ModItems.SELJUK_BOOTS.get(), "Seljuk Boots");
        add(ModItems.PIDE.get(), "Pide");
        add(ModItems.HELVA.get(), "Helva");
        add(ModItems.LOKUM.get(), "Turkish Delight");
        add(ModItems.AYRAN.get(), "Ayran");
        add(ModItems.YOGURT.get(), "Yogurt");
        add(ModItems.PISTACHIOS.get(), "Pistachios");
        add(ModItems.COTTON.get(), "Cotton");
        add(ModItems.SELJUK_WOOL_CLOTHES.get(), "Seljuk Wool Clothes");
        add(ModItems.SELJUK_COTTON_CLOTHES.get(), "Seljuk Cotton Clothes");
        add(ModItems.WALL_CARPET_SMALL.get(), "Small Wall Carpet");
        add(ModItems.WALL_CARPET_MEDIUM.get(), "Medium Wall Carpet");
        add(ModItems.WALL_CARPET_LARGE.get(), "Large Wall Carpet");

        // Currency formatting
        add("currency.millenaire_rewrite.gold", "Gold");
        add("currency.millenaire_rewrite.silver", "Silver");
        add("currency.millenaire_rewrite.copper", "Copper");
        add("currency.millenaire_rewrite.denier", "Denier");
        add("currency.millenaire_rewrite.zero", "0 Copper Denier");
        addEnglishParchmentContentTranslations();
    }

    private void addChineseTranslations() {
        // Creative Tabs
        add("creativetab.millenaire_rewrite.blocks", "千年村庄建筑方块");
        add("creativetab.millenaire_rewrite.huaxia", "华夏文化"); // 华夏文化标签页
        add("creativetab.millenaire_rewrite.norman", "诺曼文化");
        add("creativetab.millenaire_rewrite.byzantine", "拜占庭文化");
        add("creativetab.millenaire_rewrite.japanese", "日本文化");
        add("creativetab.millenaire_rewrite.mayan", "玛雅文化");
        add("creativetab.millenaire_rewrite.indian", "印度文化");
        add("creativetab.millenaire_rewrite.seljuk", "塞尔柱文化");
        add("creativetab.millenaire_rewrite.inuit", "因纽特文化");
        add("creativetab.millenaire_rewrite.food", "千年村庄食物");
        add("creativetab.millenaire_rewrite.misc", "千年村庄杂项");

        // Legacy Creative Tab (keep for compatibility)
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
        add(ModItems.BALCHE.get(), "巴尔切酒");
        add(ModItems.SIKILPAH.get(), "西基尔帕");

        // ================ Japanese Foods ================
        add(ModItems.SAKE.get(), "清酒");
        add(ModItems.UDON.get(), "乌冬面");
        add(ModItems.IKAYAKI.get(), "鱿鱼烧");
        add(ModItems.CHERRIES.get(), "樱桃");
        add(ModItems.CHERRY_BLOSSOM.get(), "樱花");

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
        add(ModItems.BYZANTINE_PICKAXE.get(), "拜占庭镐");
        add(ModItems.BYZANTINE_AXE.get(), "拜占庭斧");
        add(ModItems.BYZANTINE_SHOVEL.get(), "拜占庭铲");
        add(ModItems.BYZANTINE_HOE.get(), "拜占庭锄");

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

        // Special Parchments
        add(ModItems.PARCHMENT_VILLAGE_SCROLL.get(), "村庄卷轴");
        add(ModItems.PARCHMENT_SADHU.get(), "圣者之书");

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
        add("debug.millenaire_rewrite.wand.position", "位置: %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.creative.help", "创意法杖 - 右键管理作物权限和箱子锁定");
        add("debug.millenaire_rewrite.wand.block_info", "方块: %s 位于 %%d, %%d, %%d");
        add("debug.millenaire_rewrite.wand.summoning.use", "召唤法杖在 %%d, %%d, %%d 处激活");
        add("debug.millenaire_rewrite.wand.negation.use", "否定法杖在 %%d, %%d, %%d 处激活");
        add("debug.millenaire_rewrite.wand.creative.use", "创意法杖在 %%d, %%d, %%d 处激活");
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

        // ================ 魔法护身符 ================
        add(ModItems.AMULET_VISHNU.get(), "毗湿奴护身符");
        add(ModItems.AMULET_ALCHEMIST.get(), "炼金术士护身符");
        add(ModItems.AMULET_YGGDRASIL.get(), "世界树护身符");
        add(ModItems.AMULET_SKOLL_HATI.get(), "斯库尔·哈提护身符");

        // ================ 装饰物品 ================
        add(ModItems.TAPESTRY.get(), "挂毯");
        add(ModItems.INDIAN_STATUE.get(), "印度雕像");
        add(ModItems.MAYAN_STATUE.get(), "玛雅雕像");
        add(ModItems.BYZANTINE_ICON_SMALL.get(), "小型拜占庭圣像");
        add(ModItems.BYZANTINE_ICON_MEDIUM.get(), "中型拜占庭圣像");
        add(ModItems.BYZANTINE_ICON_LARGE.get(), "大型拜占庭圣像");

        // ================ 特殊工具与材料 ================
        add(ModItems.BRICK_MOULD.get(), "砖块模具");
        add(ModItems.OLIVES.get(), "橄榄");
        add(ModItems.OLIVE_OIL.get(), "橄榄油");

        // ================ 因纽特文明 ================
        add(ModItems.INUIT_TRIDENT.get(), "因纽特三叉戟");
        add(ModItems.INUIT_BOW.get(), "因纽特弓");
        add(ModItems.ULU.get(), "乌鲁刀");
        add(ModItems.FUR_HELMET.get(), "毛皮头盔");
        add(ModItems.FUR_CHESTPLATE.get(), "毛皮胸甲");
        add(ModItems.FUR_LEGGINGS.get(), "毛皮护腿");
        add(ModItems.FUR_BOOTS.get(), "毛皮靴子");
        add(ModItems.BEAR_MEAT_RAW.get(), "生熊肉");
        add(ModItems.BEAR_MEAT_COOKED.get(), "熟熊肉");
        add(ModItems.WOLF_MEAT_RAW.get(), "生狼肉");
        add(ModItems.WOLF_MEAT_COOKED.get(), "熟狼肉");
        add(ModItems.SEAFOOD_RAW.get(), "生海鲜");
        add(ModItems.SEAFOOD_COOKED.get(), "熟海鲜");
        add(ModItems.INUIT_BEAR_STEW.get(), "因纽特熊肉炖菜");
        add(ModItems.INUIT_MEATY_STEW.get(), "因纽特肉类炖菜");
        add(ModItems.INUIT_POTATO_STEW.get(), "因纽特土豆炖菜");
        add(ModItems.TANNED_HIDE.get(), "鞣制兽皮");
        add(ModItems.HIDE_HANGING.get(), "悬挂兽皮");

        // ================ 塞尔柱文明 ================
        add(ModItems.SELJUK_SCIMITAR.get(), "塞尔柱弯刀");
        add(ModItems.SELJUK_BOW.get(), "塞尔柱弓");
        add(ModItems.SELJUK_TURBAN.get(), "塞尔柱头巾");
        add(ModItems.SELJUK_HELMET.get(), "塞尔柱头盔");
        add(ModItems.SELJUK_CHESTPLATE.get(), "塞尔柱胸甲");
        add(ModItems.SELJUK_LEGGINGS.get(), "塞尔柱护腿");
        add(ModItems.SELJUK_BOOTS.get(), "塞尔柱靴子");
        add(ModItems.PIDE.get(), "土耳其薄饼");
        add(ModItems.HELVA.get(), "哈尔瓦");
        add(ModItems.LOKUM.get(), "土耳其软糖");
        add(ModItems.AYRAN.get(), "酸奶饮料");
        add(ModItems.YOGURT.get(), "酸奶");
        add(ModItems.PISTACHIOS.get(), "开心果");
        add(ModItems.COTTON.get(), "棉花");
        add(ModItems.SELJUK_WOOL_CLOTHES.get(), "塞尔柱羊毛服装");
        add(ModItems.SELJUK_COTTON_CLOTHES.get(), "塞尔柱棉质服装");
        add(ModItems.WALL_CARPET_SMALL.get(), "小型壁毯");
        add(ModItems.WALL_CARPET_MEDIUM.get(), "中型壁毯");
        add(ModItems.WALL_CARPET_LARGE.get(), "大型壁毯");

        // ================ 颜料桶 ================
        add(ModItems.PAINT_BUCKET_WHITE.get(), "白色颜料桶");
        add(ModItems.PAINT_BUCKET_ORANGE.get(), "橙色颜料桶");
        add(ModItems.PAINT_BUCKET_MAGENTA.get(), "品红颜料桶");
        add(ModItems.PAINT_BUCKET_LIGHT_BLUE.get(), "淡蓝颜料桶");
        add(ModItems.PAINT_BUCKET_YELLOW.get(), "黄色颜料桶");
        add(ModItems.PAINT_BUCKET_LIME.get(), "黄绿颜料桶");
        add(ModItems.PAINT_BUCKET_PINK.get(), "粉色颜料桶");
        add(ModItems.PAINT_BUCKET_GRAY.get(), "灰色颜料桶");
        add(ModItems.PAINT_BUCKET_LIGHT_GRAY.get(), "淡灰颜料桶");
        add(ModItems.PAINT_BUCKET_CYAN.get(), "青色颜料桶");
        add(ModItems.PAINT_BUCKET_PURPLE.get(), "紫色颜料桶");
        add(ModItems.PAINT_BUCKET_BLUE.get(), "蓝色颜料桶");
        add(ModItems.PAINT_BUCKET_BROWN.get(), "棕色颜料桶");
        add(ModItems.PAINT_BUCKET_GREEN.get(), "绿色颜料桶");
        add(ModItems.PAINT_BUCKET_RED.get(), "红色颜料桶");
        add(ModItems.PAINT_BUCKET_BLACK.get(), "黑色颜料桶");

        // Currency formatting
        add("currency.millenaire_rewrite.gold", "金");
        add("currency.millenaire_rewrite.silver", "银");
        add("currency.millenaire_rewrite.copper", "铜");
        add("currency.millenaire_rewrite.denier", "第纳尔");
        add("currency.millenaire_rewrite.zero", "0 铜第纳尔");
        addChineseParchmentContentTranslations();
    }

    /**
     * 添加羊皮纸内容的翻译
     */
    private void addEnglishParchmentContentTranslations() {
        // Norman Parchment Contents
        add("parchment.norman.villager.title", "Norman Villager Guide");
        add("parchment.norman.villager.chief", "Chief - Leader of the village");
        add("parchment.norman.villager.knight", "Knight - Armed warrior");
        add("parchment.norman.villager.farmer", "Farmer - Crop cultivator");
        add("parchment.norman.villager.artisan", "Artisan - Craftsman");
        add("parchment.norman.villager.women", "Women - Household manager");
        add("parchment.norman.villager.children", "Children - Future of the village");
        add("parchment.norman.villager.merchant", "Merchant - Trade expert");
        add("parchment.norman.villager.architect", "Architect - Master builder");

        add("parchment.norman.building.title", "Norman Building Guide");
        add("parchment.norman.building.town_hall", "Town Hall - Administrative center of the village");
        add("parchment.norman.building.blacksmith", "Blacksmith - Makes metal tools and weapons");
        add("parchment.norman.building.farm", "Farm - Grows wheat and raises livestock");
        add("parchment.norman.building.watchtower", "Watchtower - Defense structure and lookout point");
        add("parchment.norman.building.house", "House - Villager residence");
        add("parchment.norman.building.church", "Church - Religious activities place");
        add("parchment.norman.building.market", "Market - Trade center");
        add("parchment.norman.building.stable", "Stable - Warhorse breeding place");

        add("parchment.norman.item.title", "Norman Item Guide");
        add("parchment.norman.item.sword", "Norman Sword - Sharp one-handed sword");
        add("parchment.norman.item.axe", "Norman Axe - Practical logging tool");
        add("parchment.norman.item.pickaxe", "Norman Pickaxe - Sturdy mining tool");
        add("parchment.norman.item.shovel", "Norman Shovel - Efficient digging tool");
        add("parchment.norman.item.hoe", "Norman Hoe - Agricultural specialized tool");
        add("parchment.norman.item.cider", "Cider - Norman specialty drink");
        add("parchment.norman.item.blood_sausage", "Blood Sausage - Traditional food");
        add("parchment.norman.item.armor", "Norman Armor - Protective equipment");

        add("parchment.norman.all.title", "Norman Complete Guide");
        add("parchment.norman.all.line1", "=== Norman Culture Complete Guide ===");
        add("parchment.norman.all.line2", "Complete information about Norman villagers, buildings, and items");
        add("parchment.norman.all.line3", "Villagers: Chief, Knight, Farmer, Artisan, Women, Children, Merchant, Architect");
        add("parchment.norman.all.line4", "Buildings: Town Hall, Blacksmith, Farm, Watchtower, House, Church, Market, Stable");
        add("parchment.norman.all.line5", "Items: Norman Sword, Norman Axe, Norman Pickaxe, Norman Shovel, Norman Hoe, Cider, Blood Sausage, Norman Armor");
        add("parchment.norman.all.line6", "=== Cultural Features ===");
        add("parchment.norman.all.line7", "Normans are known for their military organization and agricultural technology");
        add("parchment.norman.all.line8", "They establish sturdy villages and effective management systems");

        // Byzantine Parchment Contents
        add("parchment.byzantine.villager.title", "Byzantine Villager Guide");
        add("parchment.byzantine.villager.governor", "Governor - Ruler of Byzantine village");
        add("parchment.byzantine.villager.centurion", "Centurion - Military commander");
        add("parchment.byzantine.villager.artisan", "Artisan - Skilled craftsman");
        add("parchment.byzantine.villager.scholar", "Scholar - Guardian of knowledge");
        add("parchment.byzantine.villager.noblewoman", "Noblewoman - Woman of high social status");
        add("parchment.byzantine.villager.slave", "Slave - Performs manual labor");
        add("parchment.byzantine.villager.merchant", "Merchant - Long-distance trader");
        add("parchment.byzantine.villager.priest", "Priest - Orthodox clergy");

        add("parchment.byzantine.building.title", "Byzantine Building Guide");
        add("parchment.byzantine.building.governor_palace", "Governor's Palace - Administrative center");
        add("parchment.byzantine.building.fortress", "Fortress - Military defense structure");
        add("parchment.byzantine.building.workshop", "Workshop - Handicraft production site");
        add("parchment.byzantine.building.library", "Library - Knowledge preservation center");
        add("parchment.byzantine.building.baths", "Baths - Public bathing facilities");
        add("parchment.byzantine.building.colosseum", "Colosseum - Entertainment arena");
        add("parchment.byzantine.building.cathedral", "Cathedral - Religious worship center");
        add("parchment.byzantine.building.port", "Port - Maritime trade base");

        add("parchment.byzantine.item.title", "Byzantine Item Guide");
        add("parchment.byzantine.item.scepter", "Byzantine Scepter - Symbol of power");
        add("parchment.byzantine.item.greek_fire", "Greek Fire - Secret military weapon");
        add("parchment.byzantine.item.wine", "Wine - Quality fermented drink");
        add("parchment.byzantine.item.malvasia", "Malvasia - Premium sweet wine");
        add("parchment.byzantine.item.feta_cheese", "Feta Cheese - Traditional dairy product");
        add("parchment.byzantine.item.kebab", "Kebab - Specialty food");
        add("parchment.byzantine.item.armor", "Byzantine Armor - Refined protective gear");
        add("parchment.byzantine.item.purple_silk", "Purple Silk - Fabric for nobility only");

        add("parchment.byzantine.all.title", "Byzantine Complete Guide");
        add("parchment.byzantine.all.line1", "=== Byzantine Culture Complete Guide ===");
        add("parchment.byzantine.all.line2", "Complete information about Byzantine villagers, buildings, and items");
        add("parchment.byzantine.all.line3", "Villagers: Governor, Centurion, Artisan, Scholar, Noblewoman, Slave, Merchant, Priest");
        add("parchment.byzantine.all.line4", "Buildings: Governor's Palace, Fortress, Workshop, Library, Baths, Colosseum, Cathedral, Port");
        add("parchment.byzantine.all.line5", "Items: Byzantine Scepter, Greek Fire, Wine, Malvasia, Feta Cheese, Kebab, Byzantine Armor, Purple Silk");
        add("parchment.byzantine.all.line6", "=== Cultural Features ===");
        add("parchment.byzantine.all.line7", "Byzantine Empire is famous for its luxury and military technology");
        add("parchment.byzantine.all.line8", "They inherit Roman traditions and develop unique Orthodox culture");

        // Hindi Parchment Contents
        add("parchment.hindi.villager.title", "Hindi Villager Guide");
        add("parchment.hindi.villager.raja", "Raja - Indian prince or ruler");
        add("parchment.hindi.villager.brahmin", "Brahmin - Priest class");
        add("parchment.hindi.villager.kshatriya", "Kshatriya - Warrior class");
        add("parchment.hindi.villager.vaishya", "Vaishya - Merchants and farmers");
        add("parchment.hindi.villager.shudra", "Shudra - Servant class");
        add("parchment.hindi.villager.yogi", "Yogi - Spiritual guide");
        add("parchment.hindi.villager.dancer", "Dancer - Traditional art performer");
        add("parchment.hindi.villager.spice_merchant", "Spice Merchant - Trade expert");

        add("parchment.hindi.building.title", "Hindi Building Guide");
        add("parchment.hindi.building.palace", "Palace - Magnificent residence of Raja");
        add("parchment.hindi.building.temple", "Temple - Religious worship place");
        add("parchment.hindi.building.market", "Market - Spice trade center");
        add("parchment.hindi.building.yoga_studio", "Yoga Studio - Meditation practice place");
        add("parchment.hindi.building.weaving_room", "Weaving Room - Silk production workshop");
        add("parchment.hindi.building.spice_garden", "Spice Garden - Grows seasoning plants");
        add("parchment.hindi.building.well", "Well - Community water supply");
        add("parchment.hindi.building.dance_hall", "Dance Hall - Art performance venue");

        add("parchment.hindi.item.title", "Hindi Item Guide");
        add("parchment.hindi.item.turmeric", "Turmeric - Precious spice seasoning");
        add("parchment.hindi.item.rice", "Rice - Main food crop");
        add("parchment.hindi.item.vegetable_curry", "Vegetable Curry - Vegetarian delicacy");
        add("parchment.hindi.item.chicken_curry", "Chicken Curry - Meat delicacy");
        add("parchment.hindi.item.gulab_jamun", "Gulab Jamun - Traditional sweet dessert");
        add("parchment.hindi.item.silk_cloth", "Silk Cloth - Luxurious textile");
        add("parchment.hindi.item.indian_sword", "Indian Sword - Decorative weapon");
        add("parchment.hindi.item.spice_powder", "Spice Powder - Seasoning material");

        add("parchment.hindi.all.title", "Hindi Complete Guide");
        add("parchment.hindi.all.line1", "=== Hindi Culture Complete Guide ===");
        add("parchment.hindi.all.line2", "Complete information about Hindi villagers, buildings, and items");
        add("parchment.hindi.all.line3", "Villagers: Raja, Brahmin, Kshatriya, Vaishya, Shudra, Yogi, Dancer, Spice Merchant");
        add("parchment.hindi.all.line4", "Buildings: Palace, Temple, Market, Yoga Studio, Weaving Room, Spice Garden, Well, Dance Hall");
        add("parchment.hindi.all.line5", "Items: Turmeric, Rice, Vegetable Curry, Chicken Curry, Gulab Jamun, Silk Cloth, Indian Sword, Spice Powder");
        add("parchment.hindi.all.line6", "=== Cultural Features ===");
        add("parchment.hindi.all.line7", "Hindi culture is known for its rich religious traditions and spice trade");
        add("parchment.hindi.all.line8", "They developed unique caste system and spiritual practice systems");

        // Mayan Parchment Contents
        add("parchment.mayan.villager.title", "Mayan Villager Guide");
        add("parchment.mayan.villager.priest_king", "Priest King - Ruler of Mayan civilization");
        add("parchment.mayan.villager.warrior", "Warrior - Brave jungle fighter");
        add("parchment.mayan.villager.astronomer", "Astronomer - Wise star observer");
        add("parchment.mayan.villager.farmer", "Farmer - Corn cultivation expert");
        add("parchment.mayan.villager.artisan", "Artisan - Obsidian carver");
        add("parchment.mayan.villager.dancer", "Dancer - Religious ceremony performer");
        add("parchment.mayan.villager.merchant", "Merchant - Long-distance trader");
        add("parchment.mayan.villager.shaman", "Shaman - Spirit communicator wizard");

        add("parchment.mayan.building.title", "Mayan Building Guide");
        add("parchment.mayan.building.pyramid", "Pyramid - Religious ceremony center");
        add("parchment.mayan.building.observatory", "Observatory - Celestial body observation");
        add("parchment.mayan.building.ball_court", "Ball Court - Traditional sports competition");
        add("parchment.mayan.building.altar", "Altar - Sacrifice ceremony place");
        add("parchment.mayan.building.steam_bath", "Steam Bath - Purification place");
        add("parchment.mayan.building.workshop", "Workshop - Obsidian processing factory");
        add("parchment.mayan.building.farmland", "Farmland - Corn cultivation area");
        add("parchment.mayan.building.cenote", "Cenote - Sacred water source");

        add("parchment.mayan.item.title", "Mayan Item Guide");
        add("parchment.mayan.item.corn", "Corn - Sacred food crop");
        add("parchment.mayan.item.cacao", "Cacao - Makes sacred drink");
        add("parchment.mayan.item.masa", "Masa - Food made from corn");
        add("parchment.mayan.item.wah", "Wah - Special ritual food");
        add("parchment.mayan.item.obsidian_tool", "Obsidian Tool - Sharp stone tool");
        add("parchment.mayan.item.scepter", "Mayan Scepter - Symbol of power");
        add("parchment.mayan.item.feather_headress", "Feather Headress - Status symbol");
        add("parchment.mayan.item.jade_ornament", "Jade Ornament - Precious decoration");

        add("parchment.mayan.all.title", "Mayan Complete Guide");
        add("parchment.mayan.all.line1", "=== Mayan Culture Complete Guide ===");
        add("parchment.mayan.all.line2", "Complete information about Mayan villagers, buildings, and items");
        add("parchment.mayan.all.line3", "Villagers: Priest King, Warrior, Astronomer, Farmer, Artisan, Dancer, Merchant, Shaman");
        add("parchment.mayan.all.line4", "Buildings: Pyramid, Observatory, Ball Court, Altar, Steam Bath, Workshop, Farmland, Cenote");
        add("parchment.mayan.all.line5", "Items: Corn, Cacao, Masa, Wah, Obsidian Tool, Mayan Scepter, Feather Headress, Jade Ornament");
        add("parchment.mayan.all.line6", "=== Cultural Features ===");
        add("parchment.mayan.all.line7", "Mayan civilization is known for its precise astronomical knowledge and grand architecture");
        add("parchment.mayan.all.line8", "They developed complex calendar systems and unique religious ceremonies");

        // Japanese Parchment Contents
        add("parchment.japanese.villager.title", "Japanese Villager Guide");
        add("parchment.japanese.villager.daimyo", "Daimyo - Feudal lord");
        add("parchment.japanese.villager.samurai", "Samurai - Professional warrior class");
        add("parchment.japanese.villager.monk", "Monk - Buddhist practitioner");
        add("parchment.japanese.villager.farmer", "Farmer - Rice cultivator");
        add("parchment.japanese.villager.artisan", "Artisan - Handicraft expert");
        add("parchment.japanese.villager.geisha", "Geisha - Traditional art performer");
        add("parchment.japanese.villager.merchant", "Merchant - Trade practitioner");
        add("parchment.japanese.villager.ninja", "Ninja - Secret spy");

        add("parchment.japanese.building.title", "Japanese Building Guide");
        add("parchment.japanese.building.castle_keep", "Castle Keep - Main tower of castle");
        add("parchment.japanese.building.shrine", "Shrine - Shinto sacred place");
        add("parchment.japanese.building.dojo", "Dojo - Martial arts training ground");
        add("parchment.japanese.building.tea_room", "Tea Room - Tea ceremony venue");
        add("parchment.japanese.building.rice_field", "Rice Field - Rice cultivation area");
        add("parchment.japanese.building.onsen", "Onsen - Natural hot spring bath");
        add("parchment.japanese.building.bamboo_forest", "Bamboo Forest - Bamboo growing area");
        add("parchment.japanese.building.zen_garden", "Zen Garden - Meditation practice place");

        add("parchment.japanese.item.title", "Japanese Item Guide");
        add("parchment.japanese.item.katana", "Katana - Sharp long sword");
        add("parchment.japanese.item.yumi", "Yumi - Traditional bow");
        add("parchment.japanese.item.sake", "Sake - Rice brewed alcohol");
        add("parchment.japanese.item.udon", "Udon - Traditional noodles");
        add("parchment.japanese.item.takoyaki", "Takoyaki - Specialty snack");
        add("parchment.japanese.item.samurai_armor", "Samurai Armor - Protective equipment");
        add("parchment.japanese.item.kimono", "Kimono - Traditional clothing");
        add("parchment.japanese.item.bamboo_product", "Bamboo Product - Practical tool");

        add("parchment.japanese.all.title", "Japanese Complete Guide");
        add("parchment.japanese.all.line1", "=== Japanese Culture Complete Guide ===");
        add("parchment.japanese.all.line2", "Complete information about Japanese villagers, buildings, and items");
        add("parchment.japanese.all.line3", "Villagers: Daimyo, Samurai, Monk, Farmer, Artisan, Geisha, Merchant, Ninja");
        add("parchment.japanese.all.line4", "Buildings: Castle Keep, Shrine, Dojo, Tea Room, Rice Field, Onsen, Bamboo Forest, Zen Garden");
        add("parchment.japanese.all.line5", "Items: Katana, Yumi, Sake, Udon, Takoyaki, Samurai Armor, Kimono, Bamboo Product");
        add("parchment.japanese.all.line6", "=== Cultural Features ===");
        add("parchment.japanese.all.line7", "Japanese culture is known for its refined arts and strict etiquette");
        add("parchment.japanese.all.line8", "They developed unique Bushido spirit and Zen philosophy");

        // GUI and Tooltips
        add("gui.millenaire_rewrite.parchment.title", "Parchment");
        add("gui.millenaire_rewrite.parchment.previous", "Previous");
        add("gui.millenaire_rewrite.parchment.next", "Next");
        add("gui.millenaire_rewrite.parchment.close", "Close");
        add("gui.millenaire_rewrite.parchment.page_info", "Page %s of %s");

        add("tooltip.millenaire_rewrite.parchment.title", "Title: %s");
        add("tooltip.millenaire_rewrite.parchment.culture", "Culture: %s");
        add("tooltip.millenaire_rewrite.parchment.type", "Type: %s");
        add("tooltip.millenaire_rewrite.parchment.entries", "Entries: %s");
        add("tooltip.millenaire_rewrite.parchment.use", "Right-click to read");
    }

    private void addChineseParchmentContentTranslations() {
        // Norman Parchment Contents
        add("parchment.norman.villager.title", "诺曼村民指南");
        add("parchment.norman.villager.chief", "首领 - 村庄的领导者");
        add("parchment.norman.villager.knight", "骑士 - 武装战士");
        add("parchment.norman.villager.farmer", "农民 - 种植作物");
        add("parchment.norman.villager.artisan", "工匠 - 手工艺人");
        add("parchment.norman.villager.women", "妇女 - 家庭管理者");
        add("parchment.norman.villager.children", "儿童 - 村庄的未来");
        add("parchment.norman.villager.merchant", "商人 - 贸易专家");
        add("parchment.norman.villager.architect", "建筑师 - 建筑大师");

        add("parchment.norman.building.title", "诺曼建筑指南");
        add("parchment.norman.building.town_hall", "市政厅 - 村庄的行政中心");
        add("parchment.norman.building.blacksmith", "铁匠铺 - 制作金属工具和武器");
        add("parchment.norman.building.farm", "农场 - 种植小麦和饲养牲畜");
        add("parchment.norman.building.watchtower", "哨塔 - 防御工事和瞭望点");
        add("parchment.norman.building.house", "民居 - 村民的住所");
        add("parchment.norman.building.church", "教堂 - 宗教活动场所");
        add("parchment.norman.building.market", "市场 - 商品交易中心");
        add("parchment.norman.building.stable", "马厩 - 饲养战马的场所");

        add("parchment.norman.item.title", "诺曼物品指南");
        add("parchment.norman.item.sword", "诺曼剑 - 锋利的单手剑");
        add("parchment.norman.item.axe", "诺曼斧 - 实用的伐木工具");
        add("parchment.norman.item.pickaxe", "诺曼镐 - 坚固的采矿工具");
        add("parchment.norman.item.shovel", "诺曼铲 - 高效的挖掘工具");
        add("parchment.norman.item.hoe", "诺曼锄 - 农业专用工具");
        add("parchment.norman.item.cider", "苹果酒 - 诺曼特色饮品");
        add("parchment.norman.item.blood_sausage", "血肠 - 传统食物");
        add("parchment.norman.item.armor", "诺曼盔甲 - 防护装备");

        add("parchment.norman.all.title", "诺曼文化全书");
        add("parchment.norman.all.line1", "=== 诺曼文化综合指南 ===");
        add("parchment.norman.all.line2", "包含诺曼村民、建筑、物品的完整信息");
        add("parchment.norman.all.line3", "村民: 首领、骑士、农民、工匠、妇女、儿童、商人、建筑师");
        add("parchment.norman.all.line4", "建筑: 市政厅、铁匠铺、农场、哨塔、民居、教堂、市场、马厩");
        add("parchment.norman.all.line5", "物品: 诺曼剑、诺曼斧、诺曼镐、诺曼铲、诺曼锄、苹果酒、血肠、诺曼盔甲");
        add("parchment.norman.all.line6", "=== 文化特色 ===");
        add("parchment.norman.all.line7", "诺曼人以其军事组织和农业技术闻名");
        add("parchment.norman.all.line8", "他们建立坚固的村庄和有效的管理体系");

        // Byzantine Parchment Contents
        add("parchment.byzantine.villager.title", "拜占庭村民指南");
        add("parchment.byzantine.villager.governor", "执政官 - 拜占庭村庄的统治者");
        add("parchment.byzantine.villager.centurion", "百夫长 - 军事指挥官");
        add("parchment.byzantine.villager.artisan", "工匠 - 熟练的手工业者");
        add("parchment.byzantine.villager.scholar", "学者 - 知识的守护者");
        add("parchment.byzantine.villager.noblewoman", "贵妇 - 社会地位崇高的女性");
        add("parchment.byzantine.villager.slave", "奴隶 - 从事体力劳动");
        add("parchment.byzantine.villager.merchant", "商贾 - 远程贸易商人");
        add("parchment.byzantine.villager.priest", "神父 - 东正教神职人员");

        add("parchment.byzantine.building.title", "拜占庭建筑指南");
        add("parchment.byzantine.building.governor_palace", "总督府 - 行政管理中心");
        add("parchment.byzantine.building.fortress", "要塞 - 军事防御建筑");
        add("parchment.byzantine.building.workshop", "工坊 - 手工业生产场所");
        add("parchment.byzantine.building.library", "图书馆 - 知识保存中心");
        add("parchment.byzantine.building.baths", "浴场 - 公共洗浴设施");
        add("parchment.byzantine.building.colosseum", "斗兽场 - 娱乐竞技场所");
        add("parchment.byzantine.building.cathedral", "大教堂 - 宗教礼拜中心");
        add("parchment.byzantine.building.port", "港口 - 海上贸易据点");

        add("parchment.byzantine.item.title", "拜占庭物品指南");
        add("parchment.byzantine.item.scepter", "拜占庭权杖 - 权力的象征");
        add("parchment.byzantine.item.greek_fire", "希腊火 - 秘密军事武器");
        add("parchment.byzantine.item.wine", "葡萄酒 - 优质发酵饮品");
        add("parchment.byzantine.item.malvasia", "马尔瓦西亚酒 - 高档甜酒");
        add("parchment.byzantine.item.feta_cheese", "羊乳酪 - 传统奶制品");
        add("parchment.byzantine.item.kebab", "烤肉串 - 特色美食");
        add("parchment.byzantine.item.armor", "拜占庭盔甲 - 精制防具");
        add("parchment.byzantine.item.purple_silk", "紫色丝绸 - 贵族专用布料");

        add("parchment.byzantine.all.title", "拜占庭文化全书");
        add("parchment.byzantine.all.line1", "=== 拜占庭文化综合指南 ===");
        add("parchment.byzantine.all.line2", "包含拜占庭村民、建筑、物品的完整信息");
        add("parchment.byzantine.all.line3", "村民: 执政官、百夫长、工匠、学者、贵妇、奴隶、商贾、神父");
        add("parchment.byzantine.all.line4", "建筑: 总督府、要塞、工坊、图书馆、浴场、斗兽场、大教堂、港口");
        add("parchment.byzantine.all.line5", "物品: 拜占庭权杖、希腊火、葡萄酒、马尔瓦西亚酒、羊乳酪、烤肉串、拜占庭盔甲、紫色丝绸");
        add("parchment.byzantine.all.line6", "=== 文化特色 ===");
        add("parchment.byzantine.all.line7", "拜占庭帝国以其豪华和军事技术闻名");
        add("parchment.byzantine.all.line8", "他们继承罗马传统并发展出独特的东正教文化");

        // Hindi Parchment Contents
        add("parchment.hindi.villager.title", "印度村民指南");
        add("parchment.hindi.villager.raja", "拉贾 - 印度王子或统治者");
        add("parchment.hindi.villager.brahmin", "婆罗门 - 祭司阶层");
        add("parchment.hindi.villager.kshatriya", "刹帝利 - 武士阶层");
        add("parchment.hindi.villager.vaishya", "吠舍 - 商人和农民");
        add("parchment.hindi.villager.shudra", "首陀罗 - 服务者阶层");
        add("parchment.hindi.villager.yogi", "瑜伽师 - 精神导师");
        add("parchment.hindi.villager.dancer", "舞者 - 传统艺术表演者");
        add("parchment.hindi.villager.spice_merchant", "香料商 - 贸易专家");

        add("parchment.hindi.building.title", "印度建筑指南");
        add("parchment.hindi.building.palace", "宫殿 - 拉贾的华丽居所");
        add("parchment.hindi.building.temple", "神庙 - 宗教朝拜场所");
        add("parchment.hindi.building.market", "市场 - 香料贸易中心");
        add("parchment.hindi.building.yoga_studio", "瑜伽馆 - 修行冥想场所");
        add("parchment.hindi.building.weaving_room", "织布房 - 丝绸生产工坊");
        add("parchment.hindi.building.spice_garden", "香料园 - 种植调料植物");
        add("parchment.hindi.building.well", "水井 - 社区供水设施");
        add("parchment.hindi.building.dance_hall", "舞蹈厅 - 艺术表演场所");

        add("parchment.hindi.item.title", "印度物品指南");
        add("parchment.hindi.item.turmeric", "姜黄 - 珍贵的调料香料");
        add("parchment.hindi.item.rice", "大米 - 主要粮食作物");
        add("parchment.hindi.item.vegetable_curry", "蔬菜咖喱 - 素食美味");
        add("parchment.hindi.item.chicken_curry", "鸡肉咖喱 - 荤食佳肴");
        add("parchment.hindi.item.gulab_jamun", "奶球甜点 - 传统甜食");
        add("parchment.hindi.item.silk_cloth", "丝绸布料 - 华丽纺织品");
        add("parchment.hindi.item.indian_sword", "印度宝剑 - 装饰性武器");
        add("parchment.hindi.item.spice_powder", "香料粉末 - 调味材料");

        add("parchment.hindi.all.title", "印度文化全书");
        add("parchment.hindi.all.line1", "=== 印度文化综合指南 ===");
        add("parchment.hindi.all.line2", "包含印度村民、建筑、物品的完整信息");
        add("parchment.hindi.all.line3", "村民: 拉贾、婆罗门、刹帝利、吠舍、首陀罗、瑜伽师、舞者、香料商");
        add("parchment.hindi.all.line4", "建筑: 宫殿、神庙、市场、瑜伽馆、织布房、香料园、水井、舞蹈厅");
        add("parchment.hindi.all.line5", "物品: 姜黄、大米、蔬菜咖喱、鸡肉咖喱、奶球甜点、丝绸布料、印度宝剑、香料粉末");
        add("parchment.hindi.all.line6", "=== 文化特色 ===");
        add("parchment.hindi.all.line7", "印度文化以其丰富的宗教传统和香料贸易闻名");
        add("parchment.hindi.all.line8", "他们发展了独特的种姓制度和精神修行体系");

        // Mayan Parchment Contents
        add("parchment.mayan.villager.title", "玛雅村民指南");
        add("parchment.mayan.villager.priest_king", "祭司王 - 玛雅文明的统治者");
        add("parchment.mayan.villager.warrior", "战士 - 勇猛的丛林战士");
        add("parchment.mayan.villager.astronomer", "天文学家 - 观测星象的智者");
        add("parchment.mayan.villager.farmer", "农民 - 种植玉米的专家");
        add("parchment.mayan.villager.artisan", "工匠 - 黑曜石雕刻师");
        add("parchment.mayan.villager.dancer", "舞者 - 宗教仪式表演者");
        add("parchment.mayan.villager.merchant", "商人 - 远距离贸易者");
        add("parchment.mayan.villager.shaman", "萨满 - 沟通神灵的巫师");

        add("parchment.mayan.building.title", "玛雅建筑指南");
        add("parchment.mayan.building.pyramid", "金字塔 - 宗教仪式中心");
        add("parchment.mayan.building.observatory", "天文台 - 观测天体运动");
        add("parchment.mayan.building.ball_court", "球场 - 传统体育竞技");
        add("parchment.mayan.building.altar", "祭坛 - 献祭仪式场所");
        add("parchment.mayan.building.steam_bath", "蒸汽浴室 - 净化身心场所");
        add("parchment.mayan.building.workshop", "工坊 - 黑曜石加工厂");
        add("parchment.mayan.building.farmland", "农田 - 玉米种植区域");
        add("parchment.mayan.building.cenote", "天坑 - 神圣的水源地");

        add("parchment.mayan.item.title", "玛雅物品指南");
        add("parchment.mayan.item.corn", "玉米 - 神圣的粮食作物");
        add("parchment.mayan.item.cacao", "可可 - 制作神圣饮品");
        add("parchment.mayan.item.masa", "玛萨 - 玉米制作的食物");
        add("parchment.mayan.item.wah", "瓦赫 - 特殊仪式食品");
        add("parchment.mayan.item.obsidian_tool", "黑曜石工具 - 锋利的石器");
        add("parchment.mayan.item.scepter", "玛雅权杖 - 权力象征");
        add("parchment.mayan.item.feather_headress", "羽毛头饰 - 地位标志");
        add("parchment.mayan.item.jade_ornament", "翡翠饰品 - 珍贵装饰");

        add("parchment.mayan.all.title", "玛雅文化全书");
        add("parchment.mayan.all.line1", "=== 玛雅文化综合指南 ===");
        add("parchment.mayan.all.line2", "包含玛雅村民、建筑、物品的完整信息");
        add("parchment.mayan.all.line3", "村民: 祭司王、战士、天文学家、农民、工匠、舞者、商人、萨满");
        add("parchment.mayan.all.line4", "建筑: 金字塔、天文台、球场、祭坛、蒸汽浴室、工坊、农田、天坑");
        add("parchment.mayan.all.line5", "物品: 玉米、可可、玛萨、瓦赫、黑曜石工具、玛雅权杖、羽毛头饰、翡翠饰品");
        add("parchment.mayan.all.line6", "=== 文化特色 ===");
        add("parchment.mayan.all.line7", "玛雅文明以其精确的天文知识和宏伟的建筑闻名");
        add("parchment.mayan.all.line8", "他们发展了复杂的历法系统和独特的宗教仪式");

        // Japanese Parchment Contents
        add("parchment.japanese.villager.title", "日本村民指南");
        add("parchment.japanese.villager.daimyo", "大名 - 封建领主");
        add("parchment.japanese.villager.samurai", "武士 - 职业战士阶层");
        add("parchment.japanese.villager.monk", "僧侣 - 佛教修行者");
        add("parchment.japanese.villager.farmer", "农民 - 稻米种植者");
        add("parchment.japanese.villager.artisan", "工匠 - 手工业专家");
        add("parchment.japanese.villager.geisha", "艺伎 - 传统艺术表演者");
        add("parchment.japanese.villager.merchant", "商人 - 贸易从业者");
        add("parchment.japanese.villager.ninja", "忍者 - 秘密间谍");

        add("parchment.japanese.building.title", "日本建筑指南");
        add("parchment.japanese.building.castle_keep", "天守阁 - 城堡主塔");
        add("parchment.japanese.building.shrine", "神社 - 神道教圣地");
        add("parchment.japanese.building.dojo", "道场 - 武术训练场");
        add("parchment.japanese.building.tea_room", "茶室 - 茶道仪式场所");
        add("parchment.japanese.building.rice_field", "稻田 - 水稻种植区");
        add("parchment.japanese.building.onsen", "温泉 - 天然热水浴场");
        add("parchment.japanese.building.bamboo_forest", "竹林 - 竹子种植区域");
        add("parchment.japanese.building.zen_garden", "禅花园 - 冥想修行场所");

        add("parchment.japanese.item.title", "日本物品指南");
        add("parchment.japanese.item.katana", "武士刀 - 锋利的长剑");
        add("parchment.japanese.item.yumi", "和弓 - 传统弓箭");
        add("parchment.japanese.item.sake", "清酒 - 米酿造酒");
        add("parchment.japanese.item.udon", "乌冬面 - 传统面条");
        add("parchment.japanese.item.takoyaki", "鱿鱼烧 - 特色小食");
        add("parchment.japanese.item.samurai_armor", "武士盔甲 - 防护装备");
        add("parchment.japanese.item.kimono", "和服 - 传统服装");
        add("parchment.japanese.item.bamboo_product", "竹制品 - 实用工具");

        add("parchment.japanese.all.title", "日本文化全书");
        add("parchment.japanese.all.line1", "=== 日本文化综合指南 ===");
        add("parchment.japanese.all.line2", "包含日本村民、建筑、物品的完整信息");
        add("parchment.japanese.all.line3", "村民: 大名、武士、僧侣、农民、工匠、艺伎、商人、忍者");
        add("parchment.japanese.all.line4", "建筑: 天守阁、神社、道场、茶室、稻田、温泉、竹林、禅花园");
        add("parchment.japanese.all.line5", "物品: 武士刀、和弓、清酒、乌冬面、鱿鱼烧、武士盔甲、和服、竹制品");
        add("parchment.japanese.all.line6", "=== 文化特色 ===");
        add("parchment.japanese.all.line7", "日本文化以其精致的艺术和严格的礼仪闻名");
        add("parchment.japanese.all.line8", "他们发展了独特的武士道精神和禅宗哲学");

        // GUI and Tooltips
        add("gui.millenaire_rewrite.parchment.title", "羊皮纸");
        add("gui.millenaire_rewrite.parchment.previous", "上一页");
        add("gui.millenaire_rewrite.parchment.next", "下一页");
        add("gui.millenaire_rewrite.parchment.close", "关闭");
        add("gui.millenaire_rewrite.parchment.page_info", "第 %s 页，共 %s 页");

        add("tooltip.millenaire_rewrite.parchment.title", "标题: %s");
        add("tooltip.millenaire_rewrite.parchment.culture", "文化: %s");
        add("tooltip.millenaire_rewrite.parchment.type", "类型: %s");
        add("tooltip.millenaire_rewrite.parchment.entries", "条目: %s");
        add("tooltip.millenaire_rewrite.parchment.use", "右键阅读");
    }

}




