package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.init.ModBlocks;
import com.jasoncian.millenaire_rewrite.init.ModItems;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * 语言文件数据生成器
 * 
 * 自动生成模组的本地化语言文件，支持中英双语。
 * 遵循千年村庄重写版的国际化规范，所有文本必须通过此类生成。
 * 
 * 功能特性：
 * - 支持多语言本地化
 * - 物品和方块名称翻译
 * - GUI界面文本翻译
 * - 消息和提示文本翻译
 * - 文化系统专用翻译
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireLanguageProvider extends LanguageProvider {

    private final String locale;

    public MillenaireLanguageProvider(PackOutput output, String locale) {
        super(output, ModConstants.MOD_ID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        MillenaireLogger.info(LogCategory.DATAGEN, "正在生成 {} 语言文件...", locale);

        if ("en_us".equals(locale)) {
            addEnglishTranslations();
        } else if ("zh_cn".equals(locale)) {
            addChineseTranslations();
        }

        MillenaireLogger.info(LogCategory.DATAGEN, "{} 语言文件生成完成", locale);
    }

    /**
     * 添加英文翻译
     */
    private void addEnglishTranslations() {
        // 模组基础信息
        add("modmenu.nameTranslation." + ModConstants.MOD_ID, "Millenaire Rewrite");
        add("modmenu.descriptionTranslation." + ModConstants.MOD_ID,
                "A complete rewrite of the Millenaire mod for 1.20.1");

        // 华夏货币物品翻译
        addItem(ModItems.HUAXIA_COPPER_COIN, "Copper Coin");
        addItem(ModItems.HUAXIA_SILVER_TAEL, "Silver Tael");
        addItem(ModItems.HUAXIA_GOLD_INGOT, "Gold Ingot");

        // 通用钱包翻译
        addItem(ModItems.UNIVERSAL_WALLET, "Universal Wallet");

        // 华夏工具物品翻译
        addItem(ModItems.HUAXIA_BAMBOO_HOE, "Bamboo Hoe");
        addItem(ModItems.HUAXIA_BRONZE_SICKLE, "Bronze Sickle");
        addItem(ModItems.HUAXIA_IRON_FLAIL, "Iron Flail");
        addItem(ModItems.HUAXIA_BAMBOO_CHISEL, "Bamboo Chisel");
        addItem(ModItems.HUAXIA_IRON_SAW, "Iron Saw");
        addItem(ModItems.HUAXIA_BRONZE_HAMMER, "Bronze Hammer");
        addItem(ModItems.HUAXIA_ABACUS, "Abacus");
        addItem(ModItems.HUAXIA_BAMBOO_SCROLL, "Bamboo Scroll");
        addItem(ModItems.HUAXIA_COMPASS, "Compass");

        // 华夏武器物品翻译
        addItem(ModItems.HUAXIA_BRONZE_JIAN, "Bronze Jian");
        addItem(ModItems.HUAXIA_IRON_DAO, "Iron Dao");
        addItem(ModItems.HUAXIA_STEEL_JIAN_MASTERWORK, "Steel Jian (Masterwork)");
        addItem(ModItems.HUAXIA_IRON_QIANG, "Iron Qiang");
        addItem(ModItems.HUAXIA_STEEL_JI, "Steel Ji");
        addItem(ModItems.HUAXIA_SHUANG_DAO, "Shuang Dao");
        addItem(ModItems.HUAXIA_BIAN_FINE, "Bian (Fine)");

        // 华夏食物物品翻译
        addItem(ModItems.HUAXIA_RICE, "Rice");
        addItem(ModItems.HUAXIA_NOODLES, "Noodles");
        addItem(ModItems.HUAXIA_BAOZI, "Baozi");
        addItem(ModItems.HUAXIA_JIAOZI, "Jiaozi");
        addItem(ModItems.HUAXIA_MAPO_TOFU, "Mapo Tofu");
        addItem(ModItems.HUAXIA_KUNG_PAO_CHICKEN, "Kung Pao Chicken");
        addItem(ModItems.HUAXIA_BRAISED_PORK, "Braised Pork");
        addItem(ModItems.HUAXIA_MOONCAKE, "Mooncake");
        addItem(ModItems.HUAXIA_TANGYUAN, "Tangyuan");
        addItem(ModItems.HUAXIA_ZONGZI, "Zongzi");
        addItem(ModItems.HUAXIA_GREEN_TEA, "Green Tea");
        addItem(ModItems.HUAXIA_OOLONG_TEA, "Oolong Tea");
        addItem(ModItems.HUAXIA_HUANGJIU, "Huangjiu");
        addItem(ModItems.HUAXIA_GINSENG_SOUP, "Ginseng Soup");
        addItem(ModItems.HUAXIA_BIRD_NEST, "Bird Nest");
        addItem(ModItems.HUAXIA_GOJI_TEA, "Goji Tea");

        // 华夏贸易商品翻译
        // 茶叶类
        addItem(ModItems.HUAXIA_GREEN_TEA_LEAVES, "Green Tea Leaves");
        addItem(ModItems.HUAXIA_OOLONG_TEA_LEAVES, "Oolong Tea Leaves");
        addItem(ModItems.HUAXIA_LONGJING_TEA_LEAVES, "Longjing Tea Leaves");
        addItem(ModItems.HUAXIA_DAHONGPAO_TEA_LEAVES, "Dahongpao Tea Leaves");

        // 瓷器类
        addItem(ModItems.HUAXIA_CELADON, "Celadon");
        addItem(ModItems.HUAXIA_WHITE_PORCELAIN, "White Porcelain");
        addItem(ModItems.HUAXIA_BLUE_WHITE_PORCELAIN, "Blue and White Porcelain");
        addItem(ModItems.HUAXIA_DOUCAI_PORCELAIN, "Doucai Porcelain");

        // 丝绸类
        addItem(ModItems.HUAXIA_RAW_SILK, "Raw Silk");
        addItem(ModItems.HUAXIA_SILK_FABRIC, "Silk Fabric");
        addItem(ModItems.HUAXIA_BROCADE, "Brocade");
        addItem(ModItems.HUAXIA_CLOUD_BROCADE, "Cloud Brocade");

        // 香料类
        addItem(ModItems.HUAXIA_STAR_ANISE, "Star Anise");
        addItem(ModItems.HUAXIA_SICHUAN_PEPPER, "Sichuan Pepper");
        addItem(ModItems.HUAXIA_CINNAMON, "Cinnamon");
        addItem(ModItems.HUAXIA_AGARWOOD, "Agarwood");

        // 华夏特殊物品翻译
        // 卷轴类
        addItem(ModItems.HUAXIA_BLUEPRINT_SCROLL, "Blueprint Scroll");
        addItem(ModItems.HUAXIA_TRADE_RECORD_SCROLL, "Trade Record Scroll");
        addItem(ModItems.HUAXIA_VILLAGE_MAP_SCROLL, "Village Map Scroll");
        addItem(ModItems.HUAXIA_SPELL_SCROLL, "Spell Scroll");

        // 印章类
        addItem(ModItems.HUAXIA_VILLAGE_CHIEF_SEAL, "Village Chief Seal");
        addItem(ModItems.HUAXIA_TRADE_SEAL, "Trade Seal");
        addItem(ModItems.HUAXIA_ARTISAN_SEAL, "Artisan Seal");
        addItem(ModItems.HUAXIA_IMPERIAL_SEAL, "Imperial Seal");

        // 风水罗盘类
        addItem(ModItems.HUAXIA_BASIC_FENGSHUI_COMPASS, "Basic Fengshui Compass");
        addItem(ModItems.HUAXIA_ADVANCED_FENGSHUI_COMPASS, "Advanced Fengshui Compass");
        addItem(ModItems.HUAXIA_MASTER_FENGSHUI_COMPASS, "Master Fengshui Compass");

        // ===== 开发工具物品翻译 =====
        addItem(ModItems.MILL_VILLAGER_SPAWNER, "Millenaire Villager Spawner");

        // ===== 实体翻译 =====
        add("entity.millenaire_rewrite.mill_villager", "Millenaire Villager");

        // ===== 村民职业翻译 =====
        add("villager.profession.farmer", "Farmer");
        add("villager.profession.herder", "Herder");
        add("villager.profession.blacksmith", "Blacksmith");
        add("villager.profession.carpenter", "Carpenter");
        add("villager.profession.tailor", "Tailor");
        add("villager.profession.merchant", "Merchant");
        add("villager.profession.shopkeeper", "Shopkeeper");
        add("villager.profession.guard", "Guard");
        add("villager.profession.archer", "Archer");
        add("villager.profession.scholar", "Scholar");
        add("villager.profession.tea_merchant", "Tea Merchant");
        add("villager.profession.potter", "Potter");
        add("villager.profession.cook", "Cook");
        add("villager.profession.healer", "Healer");

        // ===== 村民性别翻译 =====
        add("villager.gender.male", "Male");
        add("villager.gender.female", "Female");

        // TODO: 添加其他物品翻译
        // addItem(ModItems.CULTURE_SCROLL, "Culture Scroll");
        // addItem(ModItems.DENIER, "Denier");

        // ===== 华夏方块翻译 =====

        // 华夏建筑材料
        addBlock(ModBlocks.HUAXIA_GREEN_BRICK, "Green Brick");
        addBlock(ModBlocks.HUAXIA_RED_WALL, "Red Wall");
        addBlock(ModBlocks.HUAXIA_GLAZED_TILE, "Glazed Tile");

        // 华夏装饰方块
        addBlock(ModBlocks.HUAXIA_LANTERN, "Lantern");
        addBlock(ModBlocks.HUAXIA_SCREEN, "Screen");
        addBlock(ModBlocks.HUAXIA_STONE_LION, "Stone Lion");

        // 华夏功能方块
        addBlock(ModBlocks.HUAXIA_CHEST, "Huaxia Chest");
        addBlock(ModBlocks.HUAXIA_TEA_TABLE, "Tea Table");
        addBlock(ModBlocks.HUAXIA_ALCHEMY_CAULDRON, "Alchemy Cauldron");
        addBlock(ModBlocks.HUAXIA_LOOM, "Loom");

        // TODO: 添加其他文化方块翻译
        // addBlock(ModBlocks.VILLAGE_STONE, "Village Stone");

        // GUI翻译
        addGuiTranslations();

        // 消息翻译
        addMessageTranslations();

        // 配置翻译
        addConfigTranslations();

        // 文化系统翻译
        addCultureTranslations();
    }

    /**
     * 添加中文翻译
     */
    private void addChineseTranslations() {
        // 模组基础信息
        add("modmenu.nameTranslation." + ModConstants.MOD_ID, "千年村庄重写版");
        add("modmenu.descriptionTranslation." + ModConstants.MOD_ID, "千年村庄模组的1.20.1完全重写版");

        // 华夏货币物品翻译
        addItem(ModItems.HUAXIA_COPPER_COIN, "铜钱");
        addItem(ModItems.HUAXIA_SILVER_TAEL, "银两");
        addItem(ModItems.HUAXIA_GOLD_INGOT, "金元宝");

        // 通用钱包翻译
        addItem(ModItems.UNIVERSAL_WALLET, "通用钱包");

        // 华夏工具物品翻译
        addItem(ModItems.HUAXIA_BAMBOO_HOE, "竹制锄头");
        addItem(ModItems.HUAXIA_BRONZE_SICKLE, "青铜镰刀");
        addItem(ModItems.HUAXIA_IRON_FLAIL, "精铁连枷");
        addItem(ModItems.HUAXIA_BAMBOO_CHISEL, "竹制凿子");
        addItem(ModItems.HUAXIA_IRON_SAW, "精铁锯子");
        addItem(ModItems.HUAXIA_BRONZE_HAMMER, "青铜锤子");
        addItem(ModItems.HUAXIA_ABACUS, "算盘");
        addItem(ModItems.HUAXIA_BAMBOO_SCROLL, "竹简");
        addItem(ModItems.HUAXIA_COMPASS, "风水罗盘");

        // 华夏武器物品翻译
        addItem(ModItems.HUAXIA_BRONZE_JIAN, "青铜剑");
        addItem(ModItems.HUAXIA_IRON_DAO, "精铁刀");
        addItem(ModItems.HUAXIA_STEEL_JIAN_MASTERWORK, "炼钢剑");
        addItem(ModItems.HUAXIA_IRON_QIANG, "精铁枪");
        addItem(ModItems.HUAXIA_STEEL_JI, "炼钢戟");
        addItem(ModItems.HUAXIA_SHUANG_DAO, "双刀");
        addItem(ModItems.HUAXIA_BIAN_FINE, "软鞭");

        // 华夏食物物品翻译
        addItem(ModItems.HUAXIA_RICE, "米饭");
        addItem(ModItems.HUAXIA_NOODLES, "面条");
        addItem(ModItems.HUAXIA_BAOZI, "包子");
        addItem(ModItems.HUAXIA_JIAOZI, "饺子");
        addItem(ModItems.HUAXIA_MAPO_TOFU, "麻婆豆腐");
        addItem(ModItems.HUAXIA_KUNG_PAO_CHICKEN, "宫保鸡丁");
        addItem(ModItems.HUAXIA_BRAISED_PORK, "红烧肉");
        addItem(ModItems.HUAXIA_MOONCAKE, "月饼");
        addItem(ModItems.HUAXIA_TANGYUAN, "汤圆");
        addItem(ModItems.HUAXIA_ZONGZI, "粽子");
        addItem(ModItems.HUAXIA_GREEN_TEA, "绿茶");
        addItem(ModItems.HUAXIA_OOLONG_TEA, "乌龙茶");
        addItem(ModItems.HUAXIA_HUANGJIU, "黄酒");
        addItem(ModItems.HUAXIA_GINSENG_SOUP, "人参汤");
        addItem(ModItems.HUAXIA_BIRD_NEST, "燕窝");
        addItem(ModItems.HUAXIA_GOJI_TEA, "枸杞茶");

        // 华夏贸易商品翻译
        // 茶叶类
        addItem(ModItems.HUAXIA_GREEN_TEA_LEAVES, "绿茶叶");
        addItem(ModItems.HUAXIA_OOLONG_TEA_LEAVES, "乌龙茶叶");
        addItem(ModItems.HUAXIA_LONGJING_TEA_LEAVES, "龙井茶叶");
        addItem(ModItems.HUAXIA_DAHONGPAO_TEA_LEAVES, "大红袍茶叶");

        // 瓷器类
        addItem(ModItems.HUAXIA_CELADON, "青瓷");
        addItem(ModItems.HUAXIA_WHITE_PORCELAIN, "白瓷");
        addItem(ModItems.HUAXIA_BLUE_WHITE_PORCELAIN, "青花瓷");
        addItem(ModItems.HUAXIA_DOUCAI_PORCELAIN, "斗彩瓷");

        // 丝绸类
        addItem(ModItems.HUAXIA_RAW_SILK, "生丝");
        addItem(ModItems.HUAXIA_SILK_FABRIC, "绢布");
        addItem(ModItems.HUAXIA_BROCADE, "锦缎");
        addItem(ModItems.HUAXIA_CLOUD_BROCADE, "云锦");

        // 香料类
        addItem(ModItems.HUAXIA_STAR_ANISE, "八角");
        addItem(ModItems.HUAXIA_SICHUAN_PEPPER, "花椒");
        addItem(ModItems.HUAXIA_CINNAMON, "肉桂");
        addItem(ModItems.HUAXIA_AGARWOOD, "沉香");

        // 华夏特殊物品翻译
        // 卷轴类
        addItem(ModItems.HUAXIA_BLUEPRINT_SCROLL, "建筑图纸");
        addItem(ModItems.HUAXIA_TRADE_RECORD_SCROLL, "贸易记录");
        addItem(ModItems.HUAXIA_VILLAGE_MAP_SCROLL, "村庄地图");
        addItem(ModItems.HUAXIA_SPELL_SCROLL, "法术卷轴");

        // 印章类
        addItem(ModItems.HUAXIA_VILLAGE_CHIEF_SEAL, "村长印章");
        addItem(ModItems.HUAXIA_TRADE_SEAL, "贸易印章");
        addItem(ModItems.HUAXIA_ARTISAN_SEAL, "工匠印章");
        addItem(ModItems.HUAXIA_IMPERIAL_SEAL, "皇室印章");

        // 风水罗盘类
        addItem(ModItems.HUAXIA_BASIC_FENGSHUI_COMPASS, "基础风水罗盘");
        addItem(ModItems.HUAXIA_ADVANCED_FENGSHUI_COMPASS, "精制风水罗盘");
        addItem(ModItems.HUAXIA_MASTER_FENGSHUI_COMPASS, "大师风水罗盘");

        // ===== 开发工具物品翻译 =====
        addItem(ModItems.MILL_VILLAGER_SPAWNER, "千年村民生成器");

        // ===== 实体翻译 =====
        add("entity.millenaire_rewrite.mill_villager", "千年村民");

        // ===== 村民职业翻译 =====
        add("villager.profession.farmer", "农民");
        add("villager.profession.herder", "牧民");
        add("villager.profession.blacksmith", "铁匠");
        add("villager.profession.carpenter", "木匠");
        add("villager.profession.tailor", "裁缝");
        add("villager.profession.merchant", "商人");
        add("villager.profession.shopkeeper", "店主");
        add("villager.profession.guard", "守卫");
        add("villager.profession.archer", "弓箭手");
        add("villager.profession.scholar", "学者");
        add("villager.profession.tea_merchant", "茶商");
        add("villager.profession.potter", "瓷匠");
        add("villager.profession.cook", "厨师");
        add("villager.profession.healer", "治疗师");

        // ===== 村民性别翻译 =====
        add("villager.gender.male", "男性");
        add("villager.gender.female", "女性");

        // TODO: 添加其他物品翻译
        // addItem(ModItems.CULTURE_SCROLL, "文化卷轴");
        // addItem(ModItems.DENIER, "德尼尔币");

        // ===== 华夏方块翻译 =====

        // 华夏建筑材料
        addBlock(ModBlocks.HUAXIA_GREEN_BRICK, "青砖");
        addBlock(ModBlocks.HUAXIA_RED_WALL, "红墙");
        addBlock(ModBlocks.HUAXIA_GLAZED_TILE, "琉璃瓦");

        // 华夏装饰方块
        addBlock(ModBlocks.HUAXIA_LANTERN, "灯笼");
        addBlock(ModBlocks.HUAXIA_SCREEN, "屏风");
        addBlock(ModBlocks.HUAXIA_STONE_LION, "石狮");

        // 华夏功能方块
        addBlock(ModBlocks.HUAXIA_TEA_TABLE, "茶桌");
        addBlock(ModBlocks.HUAXIA_ALCHEMY_CAULDRON, "药鼎");
        addBlock(ModBlocks.HUAXIA_LOOM, "织机");

        // TODO: 添加其他文化方块翻译
        // addBlock(ModBlocks.VILLAGE_STONE, "村庄石材");

        // GUI翻译
        addGuiTranslationsZh();

        // 消息翻译
        addMessageTranslationsZh();

        // 配置翻译
        addConfigTranslationsZh();

        // 文化系统翻译
        addCultureTranslationsZh();
    }

    /**
     * 添加GUI相关的英文翻译
     */
    private void addGuiTranslations() {
        // 创造标签页翻译
        add("creativetab.millenaire_rewrite.millenaire_tab", "Millenaire");
        add("creativetab.millenaire_rewrite.millenaire_blocks_tab", "Millenaire Blocks");
        add("creativetab.millenaire_rewrite.millenaire_culture_tab", "Millenaire Cultures");

        // 容器界面翻译
        add("container.millenaire_rewrite.huaxia_chest", "Huaxia Chest");
        add("container.millenaire_rewrite.huaxia_tea_table", "Tea Table");

        // GUI界面翻译
        add("gui.millenaire_rewrite.village_info.title", "Village Information");
        add("gui.millenaire_rewrite.culture_selection.title", "Choose Culture");
        add("gui.millenaire_rewrite.trade.title", "Trade");
        add("gui.millenaire_rewrite.quest.title", "Quests");
        add("gui.millenaire_rewrite.reputation.title", "Reputation");
    }

    /**
     * 添加GUI相关的中文翻译
     */
    private void addGuiTranslationsZh() {
        // 创造标签页翻译
        add("creativetab.millenaire_rewrite.millenaire_tab", "千年村庄");
        add("creativetab.millenaire_rewrite.millenaire_blocks_tab", "千年村庄方块");
        add("creativetab.millenaire_rewrite.millenaire_culture_tab", "千年村庄文化");

        // 容器界面翻译
        add("container.millenaire_rewrite.huaxia_chest", "华夏箱子");
        add("container.millenaire_rewrite.huaxia_tea_table", "茶桌");

        // GUI界面翻译
        add("gui.millenaire_rewrite.village_info.title", "村庄信息");
        add("gui.millenaire_rewrite.culture_selection.title", "选择文化");
        add("gui.millenaire_rewrite.trade.title", "贸易");
        add("gui.millenaire_rewrite.quest.title", "任务");
        add("gui.millenaire_rewrite.reputation.title", "声望");
    }

    /**
     * 添加消息相关的英文翻译
     */
    private void addMessageTranslations() {
        add("message.millenaire_rewrite.village_created", "Village created successfully!");
        add("message.millenaire_rewrite.invalid_location", "Cannot create village here");
        add("message.millenaire_rewrite.not_enough_space", "Not enough space for village");
        add("message.millenaire_rewrite.reputation_gained", "Reputation gained with %s");
        add("message.millenaire_rewrite.reputation_lost", "Reputation lost with %s");

        // 华夏功能方块交互消息
        add("block.millenaire_rewrite.huaxia_tea_table.message",
                "A place for tea ceremony and social gatherings. The essence of Huaxia tea culture.");
        add("block.millenaire_rewrite.huaxia_alchemy_cauldron.message",
                "An ancient cauldron for alchemy and medicine. Embodies the wisdom of traditional Chinese medicine.");
        add("block.millenaire_rewrite.huaxia_loom.message",
                "A traditional loom for weaving silk and fabrics. Represents the exquisite textile craftsmanship of Huaxia.");
    }

    /**
     * 添加消息相关的中文翻译
     */
    private void addMessageTranslationsZh() {
        add("message.millenaire_rewrite.village_created", "村庄创建成功！");
        add("message.millenaire_rewrite.invalid_location", "无法在此处创建村庄");
        add("message.millenaire_rewrite.not_enough_space", "村庄空间不足");
        add("message.millenaire_rewrite.reputation_gained", "在%s获得了声望");
        add("message.millenaire_rewrite.reputation_lost", "在%s失去了声望");

        // 华夏功能方块交互消息
        add("block.millenaire_rewrite.huaxia_tea_table.message", "茶道与社交聚会之所，体现华夏茶文化的精髓。");
        add("block.millenaire_rewrite.huaxia_alchemy_cauldron.message", "古老的炼药鼎炉，承载着中华传统医学的智慧。");
        add("block.millenaire_rewrite.huaxia_loom.message", "传统的织机，用于编织丝绸与布料，展现华夏精湛的纺织工艺。");
    }

    /**
     * 添加配置相关的英文翻译
     */
    private void addConfigTranslations() {
        add("config.millenaire_rewrite.village_spawn_rate", "Village Spawn Rate");
        add("config.millenaire_rewrite.village_spawn_rate.tooltip",
                "Controls how frequently villages spawn in the world");
        add("config.millenaire_rewrite.max_villagers_per_village", "Max Villagers per Village");
        add("config.millenaire_rewrite.enable_huaxia_culture", "Enable Huaxia Culture");
    }

    /**
     * 添加配置相关的中文翻译
     */
    private void addConfigTranslationsZh() {
        add("config.millenaire_rewrite.village_spawn_rate", "村庄生成频率");
        add("config.millenaire_rewrite.village_spawn_rate.tooltip", "控制世界中村庄生成的频率");
        add("config.millenaire_rewrite.max_villagers_per_village", "每个村庄最大村民数");
        add("config.millenaire_rewrite.enable_huaxia_culture", "启用华夏文化");
    }

    /**
     * 添加文化系统相关的英文翻译
     */
    private void addCultureTranslations() {
        // 文化类型翻译 - 使用CultureType的翻译键
        add(CultureType.NORMAN.getTranslationKey(), "Norman Culture");
        add(CultureType.NORMAN.getTranslationKey() + ".description",
                "Medieval European culture featuring castles, knights, and feudal society");

        add(CultureType.BYZANTINE.getTranslationKey(), "Byzantine Culture");
        add(CultureType.BYZANTINE.getTranslationKey() + ".description",
                "Eastern Roman Empire culture blending Greek, Roman, and Christian traditions");

        add(CultureType.JAPANESE.getTranslationKey(), "Japanese Culture");
        add(CultureType.JAPANESE.getTranslationKey() + ".description",
                "Traditional Japanese culture emphasizing honor, discipline, and harmony with nature");

        add(CultureType.HUAXIA.getTranslationKey(), "Huaxia Culture");
        add(CultureType.HUAXIA.getTranslationKey() + ".description",
                "Traditional Chinese culture embodying deep historical heritage and unique Eastern aesthetics");

        add(CultureType.INDIAN.getTranslationKey(), "Indian Culture");
        add(CultureType.INDIAN.getTranslationKey() + ".description",
                "South Asian culture rich in philosophy, spirituality, and diverse traditions");

        add(CultureType.MAYAN.getTranslationKey(), "Mayan Culture");
        add(CultureType.MAYAN.getTranslationKey() + ".description",
                "Ancient Mesoamerican civilization known for astronomy, mathematics, and monumental architecture");

        // 文化系统UI翻译
        add("gui.millenaire_rewrite.culture.title", "Culture Information");
        add("gui.millenaire_rewrite.culture.name", "Name");
        add("gui.millenaire_rewrite.culture.description", "Description");
        add("gui.millenaire_rewrite.culture.style", "Architectural Style");
        add("gui.millenaire_rewrite.culture.enabled", "Enabled");
        add("gui.millenaire_rewrite.culture.version", "Version");

        // 文化选择界面
        add("gui.millenaire_rewrite.culture_selector.title", "Select Culture");
        add("gui.millenaire_rewrite.culture_selector.confirm", "Confirm Selection");
        add("gui.millenaire_rewrite.culture_selector.cancel", "Cancel");

        // 文化信息面板
        add("gui.millenaire_rewrite.culture_info.professions", "Supported Professions");
        add("gui.millenaire_rewrite.culture_info.buildings", "Building Types");
        add("gui.millenaire_rewrite.culture_info.specialties", "Production Specialties");
        add("gui.millenaire_rewrite.culture_info.technologies", "Technology Levels");

        // 状态信息
        add("status.millenaire_rewrite.culture.loading", "Loading cultures...");
        add("status.millenaire_rewrite.culture.loaded", "Cultures loaded successfully");
        add("status.millenaire_rewrite.culture.failed", "Failed to load some cultures");
        add("status.millenaire_rewrite.culture.not_found", "Culture not found");

        // 职业翻译
        addProfessionTranslations();

        // 建筑类型翻译
        addBuildingTypeTranslations();

        // 货币翻译
        addCurrencyTranslations();

        // 技术领域翻译
        addTechnologyTranslations();
    }

    /**
     * 添加职业翻译（英文）
     */
    private void addProfessionTranslations() {
        // Norman职业
        add("profession.millenaire_rewrite.knight", "Knight");
        add("profession.millenaire_rewrite.farmer", "Farmer");
        add("profession.millenaire_rewrite.craftsman", "Craftsman");
        add("profession.millenaire_rewrite.merchant", "Merchant");
        add("profession.millenaire_rewrite.priest", "Priest");
        add("profession.millenaire_rewrite.baker", "Baker");
        add("profession.millenaire_rewrite.blacksmith", "Blacksmith");
        add("profession.millenaire_rewrite.carpenter", "Carpenter");

        // Japanese职业
        add("profession.millenaire_rewrite.samurai", "Samurai");
        add("profession.millenaire_rewrite.artisan", "Artisan");
        add("profession.millenaire_rewrite.monk", "Monk");
        add("profession.millenaire_rewrite.tea_master", "Tea Master");
        add("profession.millenaire_rewrite.sword_smith", "Sword Smith");
        add("profession.millenaire_rewrite.archer", "Archer");
        add("profession.millenaire_rewrite.fisherman", "Fisherman");
        add("profession.millenaire_rewrite.poet", "Poet");

        // Byzantine职业
        add("profession.millenaire_rewrite.scholar", "Scholar");
        add("profession.millenaire_rewrite.soldier", "Soldier");
        add("profession.millenaire_rewrite.diplomat", "Diplomat");
        add("profession.millenaire_rewrite.architect", "Architect");
        add("profession.millenaire_rewrite.silk_weaver", "Silk Weaver");
        add("profession.millenaire_rewrite.scribe", "Scribe");
        add("profession.millenaire_rewrite.sailor", "Sailor");

        // Huaxia职业
        add("profession.millenaire_rewrite.official", "Official");
        add("profession.millenaire_rewrite.doctor", "Doctor");
        add("profession.millenaire_rewrite.teacher", "Teacher");
        add("profession.millenaire_rewrite.calligrapher", "Calligrapher");
    }

    /**
     * 添加建筑类型翻译（英文）
     */
    private void addBuildingTypeTranslations() {
        // Norman建筑
        add("building.millenaire_rewrite.castle", "Castle");
        add("building.millenaire_rewrite.manor_house", "Manor House");
        add("building.millenaire_rewrite.peasant_house", "Peasant House");
        add("building.millenaire_rewrite.church", "Church");
        add("building.millenaire_rewrite.smithy", "Smithy");
        add("building.millenaire_rewrite.mill", "Mill");
        add("building.millenaire_rewrite.tavern", "Tavern");
        add("building.millenaire_rewrite.guard_tower", "Guard Tower");

        // Japanese建筑
        add("building.millenaire_rewrite.dojo", "Dojo");
        add("building.millenaire_rewrite.traditional_house", "Traditional House");
        add("building.millenaire_rewrite.temple", "Temple");
        add("building.millenaire_rewrite.shrine", "Shrine");
        add("building.millenaire_rewrite.tea_house", "Tea House");
        add("building.millenaire_rewrite.zen_garden", "Zen Garden");
        add("building.millenaire_rewrite.pagoda", "Pagoda");

        // Byzantine建筑
        add("building.millenaire_rewrite.palace", "Palace");
        add("building.millenaire_rewrite.cathedral", "Cathedral");
        add("building.millenaire_rewrite.monastery", "Monastery");
        add("building.millenaire_rewrite.basilica", "Basilica");
        add("building.millenaire_rewrite.workshop", "Workshop");
        add("building.millenaire_rewrite.bazaar", "Bazaar");
        add("building.millenaire_rewrite.harbor", "Harbor");
        add("building.millenaire_rewrite.bathhouse", "Bathhouse");
        add("building.millenaire_rewrite.library", "Library");
        add("building.millenaire_rewrite.aqueduct", "Aqueduct");

        // Huaxia建筑
        add("building.millenaire_rewrite.courtyard_house", "Courtyard House");
        add("building.millenaire_rewrite.garden", "Garden");
        add("building.millenaire_rewrite.study_hall", "Study Hall");
        add("building.millenaire_rewrite.pharmacy", "Pharmacy");
        add("building.millenaire_rewrite.gate_tower", "Gate Tower");
        add("building.millenaire_rewrite.bridge", "Bridge");

        // 通用建筑
        add("building.millenaire_rewrite.house", "House");
        add("building.millenaire_rewrite.farm", "Farm");
        add("building.millenaire_rewrite.market", "Market");
    }

    /**
     * 添加货币翻译（英文）
     */
    private void addCurrencyTranslations() {
        // Norman货币
        add("currency.millenaire_rewrite.denier", "Denier");
        add("currency.millenaire_rewrite.denier_or", "Gold Denier");
        add("currency.millenaire_rewrite.denier_argent", "Silver Denier");

        // Japanese货币
        add("currency.millenaire_rewrite.koban", "Koban");
        add("currency.millenaire_rewrite.mon", "Mon");
        add("currency.millenaire_rewrite.ryo", "Ryo");

        // Byzantine货币
        add("currency.millenaire_rewrite.solidus", "Solidus");
        add("currency.millenaire_rewrite.nomisma", "Nomisma");

        // Huaxia货币
        add("currency.millenaire_rewrite.copper_coin", "Copper Coin");
        add("currency.millenaire_rewrite.silver_tael", "Silver Tael");
        add("currency.millenaire_rewrite.gold_ingot", "Gold Ingot");
    }

    /**
     * 添加技术领域翻译（英文）
     */
    private void addTechnologyTranslations() {
        add("technology.millenaire_rewrite.agriculture", "Agriculture");
        add("technology.millenaire_rewrite.construction", "Construction");
        add("technology.millenaire_rewrite.craftsmanship", "Craftsmanship");
        add("technology.millenaire_rewrite.metallurgy", "Metallurgy");
        add("technology.millenaire_rewrite.military", "Military");
        add("technology.millenaire_rewrite.navigation", "Navigation");
        add("technology.millenaire_rewrite.scholarship", "Scholarship");
        add("technology.millenaire_rewrite.medicine", "Medicine");
        add("technology.millenaire_rewrite.astronomy", "Astronomy");
        add("technology.millenaire_rewrite.aesthetics", "Aesthetics");
        add("technology.millenaire_rewrite.trade", "Trade");
    }

    /**
     * 添加文化系统相关的中文翻译
     */
    private void addCultureTranslationsZh() {
        // 文化类型翻译 - 使用CultureType的翻译键
        add(CultureType.NORMAN.getTranslationKey(), "诺曼文化");
        add(CultureType.NORMAN.getTranslationKey() + ".description",
                "中世纪欧洲文化，以城堡、骑士和封建社会为特色");

        add(CultureType.BYZANTINE.getTranslationKey(), "拜占庭文化");
        add(CultureType.BYZANTINE.getTranslationKey() + ".description",
                "东罗马帝国文化，融合了希腊、罗马和基督教传统");

        add(CultureType.JAPANESE.getTranslationKey(), "日本文化");
        add(CultureType.JAPANESE.getTranslationKey() + ".description",
                "传统日本文化，强调荣誉、纪律和与自然的和谐");

        add(CultureType.HUAXIA.getTranslationKey(), "华夏文化");
        add(CultureType.HUAXIA.getTranslationKey() + ".description",
                "中华传统文化，体现深厚的历史底蕴和独特的东方美学");

        add(CultureType.INDIAN.getTranslationKey(), "印度文化");
        add(CultureType.INDIAN.getTranslationKey() + ".description",
                "南亚文化，富含哲学、灵性和多样化的传统");

        add(CultureType.MAYAN.getTranslationKey(), "玛雅文化");
        add(CultureType.MAYAN.getTranslationKey() + ".description",
                "古代中美洲文明，以天文学、数学和纪念性建筑闻名");

        // 文化系统UI翻译
        add("gui.millenaire_rewrite.culture.title", "文化信息");
        add("gui.millenaire_rewrite.culture.name", "名称");
        add("gui.millenaire_rewrite.culture.description", "描述");
        add("gui.millenaire_rewrite.culture.style", "建筑风格");
        add("gui.millenaire_rewrite.culture.enabled", "启用");
        add("gui.millenaire_rewrite.culture.version", "版本");

        // 文化选择界面
        add("gui.millenaire_rewrite.culture_selector.title", "选择文化");
        add("gui.millenaire_rewrite.culture_selector.confirm", "确认选择");
        add("gui.millenaire_rewrite.culture_selector.cancel", "取消");

        // 文化信息面板
        add("gui.millenaire_rewrite.culture_info.professions", "支持的职业");
        add("gui.millenaire_rewrite.culture_info.buildings", "建筑类型");
        add("gui.millenaire_rewrite.culture_info.specialties", "生产专长");
        add("gui.millenaire_rewrite.culture_info.technologies", "技术水平");

        // 状态信息
        add("status.millenaire_rewrite.culture.loading", "正在加载文化...");
        add("status.millenaire_rewrite.culture.loaded", "文化加载成功");
        add("status.millenaire_rewrite.culture.failed", "部分文化加载失败");
        add("status.millenaire_rewrite.culture.not_found", "未找到文化");

        // 职业翻译
        addProfessionTranslationsZh();

        // 建筑类型翻译
        addBuildingTypeTranslationsZh();

        // 货币翻译
        addCurrencyTranslationsZh();

        // 技术领域翻译
        addTechnologyTranslationsZh();
    }

    /**
     * 添加职业翻译（中文）
     */
    private void addProfessionTranslationsZh() {
        // Norman职业
        add("profession.millenaire_rewrite.knight", "骑士");
        add("profession.millenaire_rewrite.farmer", "农夫");
        add("profession.millenaire_rewrite.craftsman", "工匠");
        add("profession.millenaire_rewrite.merchant", "商人");
        add("profession.millenaire_rewrite.priest", "牧师");
        add("profession.millenaire_rewrite.baker", "面包师");
        add("profession.millenaire_rewrite.blacksmith", "铁匠");
        add("profession.millenaire_rewrite.carpenter", "木匠");

        // Japanese职业
        add("profession.millenaire_rewrite.samurai", "武士");
        add("profession.millenaire_rewrite.artisan", "手工艺人");
        add("profession.millenaire_rewrite.monk", "僧侣");
        add("profession.millenaire_rewrite.tea_master", "茶道大师");
        add("profession.millenaire_rewrite.sword_smith", "刀匠");
        add("profession.millenaire_rewrite.archer", "弓手");
        add("profession.millenaire_rewrite.fisherman", "渔夫");
        add("profession.millenaire_rewrite.poet", "诗人");

        // Byzantine职业
        add("profession.millenaire_rewrite.scholar", "学者");
        add("profession.millenaire_rewrite.soldier", "士兵");
        add("profession.millenaire_rewrite.diplomat", "外交官");
        add("profession.millenaire_rewrite.architect", "建筑师");
        add("profession.millenaire_rewrite.silk_weaver", "丝绸织工");
        add("profession.millenaire_rewrite.scribe", "抄写员");
        add("profession.millenaire_rewrite.sailor", "水手");

        // Huaxia职业
        add("profession.millenaire_rewrite.official", "官员");
        add("profession.millenaire_rewrite.doctor", "医师");
        add("profession.millenaire_rewrite.teacher", "先生");
        add("profession.millenaire_rewrite.calligrapher", "书法家");
    }

    /**
     * 添加建筑类型翻译（中文）
     */
    private void addBuildingTypeTranslationsZh() {
        // Norman建筑
        add("building.millenaire_rewrite.castle", "城堡");
        add("building.millenaire_rewrite.manor_house", "庄园住宅");
        add("building.millenaire_rewrite.peasant_house", "农舍");
        add("building.millenaire_rewrite.church", "教堂");
        add("building.millenaire_rewrite.smithy", "铁匠铺");
        add("building.millenaire_rewrite.mill", "磨坊");
        add("building.millenaire_rewrite.tavern", "酒馆");
        add("building.millenaire_rewrite.guard_tower", "瞭望塔");

        // Japanese建筑
        add("building.millenaire_rewrite.dojo", "道场");
        add("building.millenaire_rewrite.traditional_house", "传统民居");
        add("building.millenaire_rewrite.temple", "寺庙");
        add("building.millenaire_rewrite.shrine", "神社");
        add("building.millenaire_rewrite.tea_house", "茶屋");
        add("building.millenaire_rewrite.zen_garden", "禅意花园");
        add("building.millenaire_rewrite.pagoda", "宝塔");

        // Byzantine建筑
        add("building.millenaire_rewrite.palace", "宫殿");
        add("building.millenaire_rewrite.cathedral", "大教堂");
        add("building.millenaire_rewrite.monastery", "修道院");
        add("building.millenaire_rewrite.basilica", "巴西利卡");
        add("building.millenaire_rewrite.workshop", "工坊");
        add("building.millenaire_rewrite.bazaar", "集市");
        add("building.millenaire_rewrite.harbor", "港口");
        add("building.millenaire_rewrite.bathhouse", "浴场");
        add("building.millenaire_rewrite.library", "图书馆");
        add("building.millenaire_rewrite.aqueduct", "引水渠");

        // Huaxia建筑
        add("building.millenaire_rewrite.courtyard_house", "四合院");
        add("building.millenaire_rewrite.garden", "园林");
        add("building.millenaire_rewrite.study_hall", "书院");
        add("building.millenaire_rewrite.pharmacy", "药铺");
        add("building.millenaire_rewrite.gate_tower", "门楼");
        add("building.millenaire_rewrite.bridge", "桥梁");

        // 通用建筑
        add("building.millenaire_rewrite.house", "住宅");
        add("building.millenaire_rewrite.farm", "农场");
        add("building.millenaire_rewrite.market", "市场");
    }

    /**
     * 添加货币翻译（中文）
     */
    private void addCurrencyTranslationsZh() {
        // Norman货币
        add("currency.millenaire_rewrite.denier", "德尼尔");
        add("currency.millenaire_rewrite.denier_or", "金德尼尔");
        add("currency.millenaire_rewrite.denier_argent", "银德尼尔");

        // Japanese货币
        add("currency.millenaire_rewrite.koban", "小判");
        add("currency.millenaire_rewrite.mon", "文");
        add("currency.millenaire_rewrite.ryo", "两");

        // Byzantine货币
        add("currency.millenaire_rewrite.solidus", "索里达");
        add("currency.millenaire_rewrite.nomisma", "诺米斯玛");

        // Huaxia货币
        add("currency.millenaire_rewrite.copper_coin", "铜钱");
        add("currency.millenaire_rewrite.silver_tael", "银两");
        add("currency.millenaire_rewrite.gold_ingot", "金锭");
    }

    /**
     * 添加技术领域翻译（中文）
     */
    private void addTechnologyTranslationsZh() {
        add("technology.millenaire_rewrite.agriculture", "农业");
        add("technology.millenaire_rewrite.construction", "建筑");
        add("technology.millenaire_rewrite.craftsmanship", "工艺");
        add("technology.millenaire_rewrite.metallurgy", "冶金");
        add("technology.millenaire_rewrite.military", "军事");
        add("technology.millenaire_rewrite.navigation", "航海");
        add("technology.millenaire_rewrite.scholarship", "学术");
        add("technology.millenaire_rewrite.medicine", "医学");
        add("technology.millenaire_rewrite.astronomy", "天文");
        add("technology.millenaire_rewrite.aesthetics", "美学");
        add("technology.millenaire_rewrite.trade", "贸易");
    }
}
