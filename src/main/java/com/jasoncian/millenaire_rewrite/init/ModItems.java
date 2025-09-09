package com.jasoncian.millenaire_rewrite.init;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.item.currency.HuaxiaCurrencyItem;
import com.jasoncian.millenaire_rewrite.item.currency.UniversalWalletItem;
import com.jasoncian.millenaire_rewrite.item.tool.HuaxiaToolItem;
import com.jasoncian.millenaire_rewrite.item.weapon.HuaxiaWeaponItem;
import com.jasoncian.millenaire_rewrite.item.food.HuaxiaFoodItem;
import com.jasoncian.millenaire_rewrite.item.trade.HuaxiaTradeItem;
import com.jasoncian.millenaire_rewrite.item.special.HuaxiaSpecialItem;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组物品注册表
 * 
 * 负责注册所有模组物品，包括：
 * - 货币系统物品（铜币、银币、金币）
 * - 文化工具和武器
 * - 贸易商品
 * - 特殊物品（召唤法杖、护身符等）
 * 
 * 使用DeferredRegister进行现代化注册，确保线程安全。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class ModItems {

    // 防止实例化工具类
    private ModItems() {
        throw new UnsupportedOperationException("物品注册表不能被实例化");
    }

    /** 物品延迟注册器 */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            ModConstants.MOD_ID);

    // ========== 货币系统物品 ==========

    /** 华夏文化 - 铜钱（基础货币单位） */
    public static final RegistryObject<Item> HUAXIA_COPPER_COIN = ITEMS.register("huaxia_copper_coin",
            () -> new HuaxiaCurrencyItem(new Item.Properties(), HuaxiaCurrencyItem.HuaxiaCurrencyType.COPPER_COIN));

    /** 华夏文化 - 银两（中等价值货币） */
    public static final RegistryObject<Item> HUAXIA_SILVER_TAEL = ITEMS.register("huaxia_silver_tael",
            () -> new HuaxiaCurrencyItem(new Item.Properties(), HuaxiaCurrencyItem.HuaxiaCurrencyType.SILVER_TAEL));

    /** 华夏文化 - 金元宝（高价值货币） */
    public static final RegistryObject<Item> HUAXIA_GOLD_INGOT = ITEMS.register("huaxia_gold_ingot",
            () -> new HuaxiaCurrencyItem(new Item.Properties(), HuaxiaCurrencyItem.HuaxiaCurrencyType.GOLD_INGOT));

    // TODO: 注册其他文化的货币物品
    // public static final RegistryObject<Item> DENIER = ITEMS.register("denier",
    // () -> new CurrencyItem(new Item.Properties()));

    // ========== 文化工具物品 ==========

    // === 华夏工具 - 农具类 ===
    
    /** 华夏竹制锄头 */
    public static final RegistryObject<Item> HUAXIA_BAMBOO_HOE = ITEMS.register("huaxia_bamboo_hoe",
            () -> new HuaxiaToolItem(ModTiers.BAMBOO, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.HOE));

    /** 华夏青铜镰刀 */
    public static final RegistryObject<Item> HUAXIA_BRONZE_SICKLE = ITEMS.register("huaxia_bronze_sickle",
            () -> new HuaxiaToolItem(ModTiers.BRONZE, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.SICKLE));

    /** 华夏精铁连枷 */
    public static final RegistryObject<Item> HUAXIA_IRON_FLAIL = ITEMS.register("huaxia_iron_flail",
            () -> new HuaxiaToolItem(ModTiers.REFINED_IRON, 2, -2.8f, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.FLAIL, 1.2f, 20));

    // === 华夏工具 - 手工业工具类 ===
    
    /** 华夏竹制凿子 */
    public static final RegistryObject<Item> HUAXIA_BAMBOO_CHISEL = ITEMS.register("huaxia_bamboo_chisel",
            () -> new HuaxiaToolItem(ModTiers.BAMBOO, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.CHISEL));

    /** 华夏精铁锯子 */
    public static final RegistryObject<Item> HUAXIA_IRON_SAW = ITEMS.register("huaxia_iron_saw",
            () -> new HuaxiaToolItem(ModTiers.REFINED_IRON, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.SAW));

    /** 华夏青铜锤子 */
    public static final RegistryObject<Item> HUAXIA_BRONZE_HAMMER = ITEMS.register("huaxia_bronze_hammer",
            () -> new HuaxiaToolItem(ModTiers.BRONZE, 1, -2.4f, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.HAMMER, 1.0f, 0));

    // === 华夏工具 - 特殊工具类 ===
    
    /** 华夏算盘 */
    public static final RegistryObject<Item> HUAXIA_ABACUS = ITEMS.register("huaxia_abacus",
            () -> new HuaxiaToolItem(ModTiers.BAMBOO, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.ABACUS));

    /** 华夏竹简 */
    public static final RegistryObject<Item> HUAXIA_BAMBOO_SCROLL = ITEMS.register("huaxia_bamboo_scroll",
            () -> new HuaxiaToolItem(ModTiers.BAMBOO, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.BAMBOO_SCROLL));

    /** 华夏风水罗盘 */
    public static final RegistryObject<Item> HUAXIA_COMPASS = ITEMS.register("huaxia_compass",
            () -> new HuaxiaToolItem(ModTiers.BRONZE, new Item.Properties(), 
                    HuaxiaToolItem.HuaxiaToolType.COMPASS));

    // ========== 文化武器物品 ==========

    // === 华夏武器 - 剑类 ===
    
    /** 华夏青铜剑 */
    public static final RegistryObject<Item> HUAXIA_BRONZE_JIAN = ITEMS.register("huaxia_bronze_jian",
            () -> new HuaxiaWeaponItem(ModTiers.BRONZE, 3, -2.4f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.JIAN));

    /** 华夏精铁刀 */
    public static final RegistryObject<Item> HUAXIA_IRON_DAO = ITEMS.register("huaxia_iron_dao",
            () -> new HuaxiaWeaponItem(ModTiers.REFINED_IRON, 4, -2.4f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.DAO));

    /** 华夏百炼钢剑（精品） */
    public static final RegistryObject<Item> HUAXIA_STEEL_JIAN_MASTERWORK = ITEMS.register("huaxia_steel_jian_masterwork",
            () -> new HuaxiaWeaponItem(ModTiers.HUNDRED_REFINED_STEEL, 5, -2.0f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.JIAN, HuaxiaWeaponItem.WeaponQuality.MASTERWORK));

    // === 华夏武器 - 长兵器类 ===
    
    /** 华夏精铁枪 */
    public static final RegistryObject<Item> HUAXIA_IRON_QIANG = ITEMS.register("huaxia_iron_qiang",
            () -> new HuaxiaWeaponItem(ModTiers.REFINED_IRON, 5, -3.0f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.QIANG));

    /** 华夏百炼钢戟 */
    public static final RegistryObject<Item> HUAXIA_STEEL_JI = ITEMS.register("huaxia_steel_ji",
            () -> new HuaxiaWeaponItem(ModTiers.HUNDRED_REFINED_STEEL, 6, -3.2f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.JI));

    // === 华夏武器 - 特殊武器类 ===
    
    /** 华夏双刀 */
    public static final RegistryObject<Item> HUAXIA_SHUANG_DAO = ITEMS.register("huaxia_shuang_dao",
            () -> new HuaxiaWeaponItem(ModTiers.REFINED_IRON, 3, -1.8f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.SHUANG_DAO));

    /** 华夏精品软鞭 */
    public static final RegistryObject<Item> HUAXIA_BIAN_FINE = ITEMS.register("huaxia_bian_fine",
            () -> new HuaxiaWeaponItem(ModTiers.REFINED_IRON, 2, -1.6f, new Item.Properties(), 
                    HuaxiaWeaponItem.HuaxiaWeaponType.BIAN, HuaxiaWeaponItem.WeaponQuality.FINE));

    // ========== 贸易商品物品 ==========

    // === 华夏食物 - 主食类 ===
    
    /** 华夏米饭 */
    public static final RegistryObject<Item> HUAXIA_RICE = ITEMS.register("huaxia_rice",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.RICE));

    /** 华夏面条 */
    public static final RegistryObject<Item> HUAXIA_NOODLES = ITEMS.register("huaxia_noodles",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.NOODLES));

    /** 华夏包子 */
    public static final RegistryObject<Item> HUAXIA_BAOZI = ITEMS.register("huaxia_baozi",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.BAOZI, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    /** 华夏饺子 */
    public static final RegistryObject<Item> HUAXIA_JIAOZI = ITEMS.register("huaxia_jiaozi",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.JIAOZI, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    // === 华夏食物 - 菜肴类 ===
    
    /** 华夏麻婆豆腐 */
    public static final RegistryObject<Item> HUAXIA_MAPO_TOFU = ITEMS.register("huaxia_mapo_tofu",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.MAPO_TOFU, 
                    HuaxiaFoodItem.CraftingDifficulty.COMPLEX));

    /** 华夏宫保鸡丁 */
    public static final RegistryObject<Item> HUAXIA_KUNG_PAO_CHICKEN = ITEMS.register("huaxia_kung_pao_chicken",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.KUNG_PAO_CHICKEN, 
                    HuaxiaFoodItem.CraftingDifficulty.COMPLEX));

    /** 华夏红烧肉 */
    public static final RegistryObject<Item> HUAXIA_BRAISED_PORK = ITEMS.register("huaxia_braised_pork",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.BRAISED_PORK, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    // === 华夏食物 - 小食类 ===
    
    /** 华夏月饼 */
    public static final RegistryObject<Item> HUAXIA_MOONCAKE = ITEMS.register("huaxia_mooncake",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.MOONCAKE, 
                    HuaxiaFoodItem.CraftingDifficulty.COMPLEX));

    /** 华夏汤圆 */
    public static final RegistryObject<Item> HUAXIA_TANGYUAN = ITEMS.register("huaxia_tangyuan",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.TANGYUAN, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    /** 华夏粽子 */
    public static final RegistryObject<Item> HUAXIA_ZONGZI = ITEMS.register("huaxia_zongzi",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.ZONGZI, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    // === 华夏食物 - 饮品类 ===
    
    /** 华夏绿茶 */
    public static final RegistryObject<Item> HUAXIA_GREEN_TEA = ITEMS.register("huaxia_green_tea",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.GREEN_TEA));

    /** 华夏乌龙茶 */
    public static final RegistryObject<Item> HUAXIA_OOLONG_TEA = ITEMS.register("huaxia_oolong_tea",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.OOLONG_TEA, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    /** 华夏黄酒 */
    public static final RegistryObject<Item> HUAXIA_HUANGJIU = ITEMS.register("huaxia_huangjiu",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.HUANGJIU, 
                    HuaxiaFoodItem.CraftingDifficulty.COMPLEX));

    // === 华夏食物 - 药膳类 ===
    
    /** 华夏人参汤 */
    public static final RegistryObject<Item> HUAXIA_GINSENG_SOUP = ITEMS.register("huaxia_ginseng_soup",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.GINSENG_SOUP, 
                    HuaxiaFoodItem.CraftingDifficulty.MASTER));

    /** 华夏燕窝 */
    public static final RegistryObject<Item> HUAXIA_BIRD_NEST = ITEMS.register("huaxia_bird_nest",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.BIRD_NEST, 
                    HuaxiaFoodItem.CraftingDifficulty.LEGENDARY));

    /** 华夏枸杞茶 */
    public static final RegistryObject<Item> HUAXIA_GOJI_TEA = ITEMS.register("huaxia_goji_tea",
            () -> new HuaxiaFoodItem(new Item.Properties(), HuaxiaFoodItem.HuaxiaFoodType.GOJI_TEA, 
                    HuaxiaFoodItem.CraftingDifficulty.MODERATE));

    // ========== 华夏贸易商品物品 ==========

    // === 茶叶类贸易商品 ===
    
    /** 华夏绿茶叶 */
    public static final RegistryObject<Item> HUAXIA_GREEN_TEA_LEAVES = ITEMS.register("huaxia_green_tea_leaves",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.GREEN_TEA_LEAVES));

    /** 华夏乌龙茶叶 */
    public static final RegistryObject<Item> HUAXIA_OOLONG_TEA_LEAVES = ITEMS.register("huaxia_oolong_tea_leaves",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.OOLONG_TEA_LEAVES));

    /** 华夏龙井茶叶 */
    public static final RegistryObject<Item> HUAXIA_LONGJING_TEA_LEAVES = ITEMS.register("huaxia_longjing_tea_leaves",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.LONGJING_TEA_LEAVES));

    /** 华夏大红袍茶叶 */
    public static final RegistryObject<Item> HUAXIA_DAHONGPAO_TEA_LEAVES = ITEMS.register("huaxia_dahongpao_tea_leaves",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.DAHONGPAO_TEA_LEAVES));

    // === 瓷器类贸易商品 ===
    
    /** 华夏青瓷 */
    public static final RegistryObject<Item> HUAXIA_CELADON = ITEMS.register("huaxia_celadon",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.CELADON));

    /** 华夏白瓷 */
    public static final RegistryObject<Item> HUAXIA_WHITE_PORCELAIN = ITEMS.register("huaxia_white_porcelain",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.WHITE_PORCELAIN));

    /** 华夏青花瓷 */
    public static final RegistryObject<Item> HUAXIA_BLUE_WHITE_PORCELAIN = ITEMS.register("huaxia_blue_white_porcelain",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.BLUE_WHITE_PORCELAIN));

    /** 华夏斗彩瓷 */
    public static final RegistryObject<Item> HUAXIA_DOUCAI_PORCELAIN = ITEMS.register("huaxia_doucai_porcelain",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.DOUCAI_PORCELAIN));

    // === 丝绸类贸易商品 ===
    
    /** 华夏生丝 */
    public static final RegistryObject<Item> HUAXIA_RAW_SILK = ITEMS.register("huaxia_raw_silk",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.RAW_SILK));

    /** 华夏绢布 */
    public static final RegistryObject<Item> HUAXIA_SILK_FABRIC = ITEMS.register("huaxia_silk_fabric",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.SILK_FABRIC));

    /** 华夏锦缎 */
    public static final RegistryObject<Item> HUAXIA_BROCADE = ITEMS.register("huaxia_brocade",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.BROCADE));

    /** 华夏云锦 */
    public static final RegistryObject<Item> HUAXIA_CLOUD_BROCADE = ITEMS.register("huaxia_cloud_brocade",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.CLOUD_BROCADE));

    // === 香料类贸易商品 ===
    
    /** 华夏八角 */
    public static final RegistryObject<Item> HUAXIA_STAR_ANISE = ITEMS.register("huaxia_star_anise",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.STAR_ANISE));

    /** 华夏花椒 */
    public static final RegistryObject<Item> HUAXIA_SICHUAN_PEPPER = ITEMS.register("huaxia_sichuan_pepper",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.SICHUAN_PEPPER));

    /** 华夏肉桂 */
    public static final RegistryObject<Item> HUAXIA_CINNAMON = ITEMS.register("huaxia_cinnamon",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.CINNAMON));

    /** 华夏沉香 */
    public static final RegistryObject<Item> HUAXIA_AGARWOOD = ITEMS.register("huaxia_agarwood",
            () -> new HuaxiaTradeItem(new Item.Properties(), HuaxiaTradeItem.HuaxiaTradeType.AGARWOOD));

    // ========== 华夏特殊物品 ==========

    // === 卷轴类特殊物品 ===
    
    /** 华夏建筑图纸卷轴 */
    public static final RegistryObject<Item> HUAXIA_BLUEPRINT_SCROLL = ITEMS.register("huaxia_blueprint_scroll",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.BLUEPRINT_SCROLL));

    /** 华夏贸易记录卷轴 */
    public static final RegistryObject<Item> HUAXIA_TRADE_RECORD_SCROLL = ITEMS.register("huaxia_trade_record_scroll",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.TRADE_RECORD_SCROLL));

    /** 华夏村庄地图卷轴 */
    public static final RegistryObject<Item> HUAXIA_VILLAGE_MAP_SCROLL = ITEMS.register("huaxia_village_map_scroll",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.VILLAGE_MAP_SCROLL));

    /** 华夏法术卷轴 */
    public static final RegistryObject<Item> HUAXIA_SPELL_SCROLL = ITEMS.register("huaxia_spell_scroll",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.SPELL_SCROLL));

    // === 印章类特殊物品 ===
    
    /** 华夏村长印章 */
    public static final RegistryObject<Item> HUAXIA_VILLAGE_CHIEF_SEAL = ITEMS.register("huaxia_village_chief_seal",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.VILLAGE_CHIEF_SEAL));

    /** 华夏贸易印章 */
    public static final RegistryObject<Item> HUAXIA_TRADE_SEAL = ITEMS.register("huaxia_trade_seal",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.TRADE_SEAL));

    /** 华夏工匠印章 */
    public static final RegistryObject<Item> HUAXIA_ARTISAN_SEAL = ITEMS.register("huaxia_artisan_seal",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.ARTISAN_SEAL));

    /** 华夏皇室印章 */
    public static final RegistryObject<Item> HUAXIA_IMPERIAL_SEAL = ITEMS.register("huaxia_imperial_seal",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.IMPERIAL_SEAL));

    // === 风水罗盘类特殊物品 ===
    
    /** 华夏基础风水罗盘 */
    public static final RegistryObject<Item> HUAXIA_BASIC_FENGSHUI_COMPASS = ITEMS.register("huaxia_basic_fengshui_compass",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.BASIC_FENGSHUI_COMPASS));

    /** 华夏精制风水罗盘 */
    public static final RegistryObject<Item> HUAXIA_ADVANCED_FENGSHUI_COMPASS = ITEMS.register("huaxia_advanced_fengshui_compass",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.ADVANCED_FENGSHUI_COMPASS));

    /** 华夏大师风水罗盘 */
    public static final RegistryObject<Item> HUAXIA_MASTER_FENGSHUI_COMPASS = ITEMS.register("huaxia_master_fengshui_compass",
            () -> new HuaxiaSpecialItem(new Item.Properties(), HuaxiaSpecialItem.HuaxiaSpecialType.MASTER_FENGSHUI_COMPASS));

    // TODO: 注册其他文化的货币物品
    // public static final RegistryObject<Item> DENIER = ITEMS.register("denier",
    // () -> new CurrencyItem(new Item.Properties()));

    // ========== 特殊物品 ==========

    /** 通用钱包 - 可存储所有文化的货币 */
    public static final RegistryObject<Item> UNIVERSAL_WALLET = ITEMS.register("universal_wallet",
            () -> new UniversalWalletItem(new Item.Properties()));

    // TODO: 注册其他特殊物品
    // public static final RegistryObject<Item> VILLAGE_WAND =
    // ITEMS.register("village_wand",
    // () -> new VillageWandItem(new Item.Properties().stacksTo(1)));

    /**
     * 初始化物品注册表
     * 
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CORE, "物品注册表已初始化");
        }
    }
}
