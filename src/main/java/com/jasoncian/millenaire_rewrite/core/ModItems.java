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
 * 物品注册器 - 管理所有Millenaire mod物品的注册
 *
 * 负责注册所有模组物品，按功能分类组织
 * 优先实现基础物品系统而非方块系统
 * 完成legacy mod中所有简单物品的迁移
 *
 * 功能特性：
 * - 货币系统物品（铜币、银币、金币）
 * - 基础材料物品
 * - 工具装备系统
 * - 食物系统
 * - 文化特色物品
 * - 羊皮纸知识系统
 *
 * @author JasonCian
 * @version 0.1.0-alpha
 */
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            MillenaireRewrite.MOD_ID);

    // ================ 货币系统 ================

    /** 基础铜德尼尔 - 最基本的货币单位 */
    public static final RegistryObject<Item> DENIER = ITEMS.register("denier",
            () -> new Item(new Item.Properties()));

    /** 金德尼尔 - 高价值货币 */
    public static final RegistryObject<Item> DENIER_OR = ITEMS.register("denier_or",
            () -> new Item(new Item.Properties()));

    /** 银德尼尔 - 中等价值货币 */
    public static final RegistryObject<Item> DENIER_ARGENT = ITEMS.register("denier_argent",
            () -> new Item(new Item.Properties()));

    // ================ 基础材料 ================

    /** 丝绸 - 重要的贸易和制作材料 */
    public static final RegistryObject<Item> SILK = ITEMS.register("silk",
            () -> new Item(new Item.Properties()));

    /** 黑曜石碎片 - 特殊材料 */
    public static final RegistryObject<Item> OBSIDIAN_FLAKE = ITEMS.register("obsidian_flake",
            () -> new Item(new Item.Properties()));

    /** 未知粉末 - 神秘材料 */
    public static final RegistryObject<Item> UNKNOWN_POWDER = ITEMS.register("unknown_powder",
            () -> new Item(new Item.Properties()));

    /** 加利安石粉 - 特殊矿物粉末 */
    public static final RegistryObject<Item> GALIANITE_DUST = ITEMS.register("galianite_dust",
            () -> new Item(new Item.Properties()));

    // ================ 服装材料 ================

    /** 羊毛衣物 - 基础服装材料 */
    public static final RegistryObject<Item> WOOL_CLOTHES = ITEMS.register("wool_clothes",
            () -> new Item(new Item.Properties()));

    /** 丝绸衣物 - 高级服装材料 */
    public static final RegistryObject<Item> SILK_CLOTHES = ITEMS.register("silk_clothes",
            () -> new Item(new Item.Properties()));

    // ================ 作物 ================

    /** 姜黄 - 印度文化作物 */
    public static final RegistryObject<Item> TURMERIC = ITEMS.register("turmeric",
            () -> new Item(new Item.Properties()));

    /** 稻米 - 亚洲文化作物 */
    public static final RegistryObject<Item> RICE = ITEMS.register("rice",
            () -> new Item(new Item.Properties()));

    /** 玉米 - 美洲文化作物 */
    public static final RegistryObject<Item> MAIZE = ITEMS.register("maize",
            () -> new Item(new Item.Properties()));

    /** 葡萄 - 地中海文化作物 */
    public static final RegistryObject<Item> GRAPES = ITEMS.register("grapes",
            () -> new Item(new Item.Properties()));

    // ================ 华夏文化物品 ================
    // TODO: 华夏文明物品系统（预留空间）
    //
    // 华夏货币系统：
    // - 铜钱 (COPPER_COIN) - 基础货币
    // - 银两 (SILVER_TAEL) - 中等货币 
    // - 黄金 (GOLD_TAEL) - 高级货币
    //
    // 华夏食物作物：
    // - 小麦 (HUAXIA_WHEAT) - 主要粮食作物
    // - 大米 (HUAXIA_RICE) - 南方主食
    // - 大豆 (SOYBEANS) - 制作豆腐原料
    // - 茶叶 (TEA_LEAVES) - 传统饮品原料
    // - 桃子 (PEACH) - 传统水果
    // - 竹笋 (BAMBOO_SHOOTS) - 蔬菜
    //
    // 华夏传统食物：
    // - 米饭 (COOKED_RICE) - 主食
    // - 面条 (NOODLES) - 面食
    // - 豆腐 (TOFU) - 豆制品
    // - 茶水 (TEA) - 传统饮品
    // - 饺子 (DUMPLINGS) - 传统食物
    // - 月饼 (MOON_CAKE) - 节庆食物
    //
    // 华夏工具武器：
    // - 青铜剑 (BRONZE_SWORD) - 古代兵器
    // - 汉剑 (HAN_SWORD) - 经典直剑
    // - 青铜斧 (BRONZE_AXE) - 工具兼武器
    // - 青铜镐 (BRONZE_PICKAXE) - 挖掘工具
    // - 青铜铲 (BRONZE_SHOVEL) - 挖掘工具
    // - 青铜锄 (BRONZE_HOE) - 农耕工具
    //
    // 华夏护甲：
    // - 皮甲套装 (LEATHER_ARMOR_SET) - 轻甲
    // - 青铜甲套装 (BRONZE_ARMOR_SET) - 重甲
    // - 官服套装 (OFFICIAL_ROBE_SET) - 礼服
    //
    // 华夏特色物品：
    // - 玉璧 (JADE_DISK) - 礼器
    // - 青瓷 (CELADON) - 瓷器
    // - 丝绸 (SILK_FABRIC) - 纺织品
    // - 书法卷轴 (CALLIGRAPHY_SCROLL) - 文化物品
    // - 灯笼 (LANTERN_HUAXIA) - 装饰品
    // - 屏风 (SCREEN) - 家具
    // - 石狮 (STONE_LION) - 装饰雕像
    //
    // 华夏羊皮纸：
    // - 华夏村民羊皮纸 (PARCHMENT_HUAXIA_VILLAGER)
    // - 华夏建筑羊皮纸 (PARCHMENT_HUAXIA_BUILDING)
    // - 华夏物品羊皮纸 (PARCHMENT_HUAXIA_ITEM)
    // - 华夏综合羊皮纸 (PARCHMENT_HUAXIA_ALL)

    // ================ 诺曼食物 ================

    /** 苹果酒原料 - 诺曼特色食物原料 */
    public static final RegistryObject<Item> CIDER_APPLE = ITEMS.register("cider_apple",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CIDER_APPLE)));

    /** 苹果酒 - 诺曼特色饮品 */
    public static final RegistryObject<Item> CIDER = ITEMS.register("cider",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CIDER)));

    /** 卡尔瓦多斯烈酒 - 诺曼烈酒 */
    public static final RegistryObject<Item> CALVA = ITEMS.register("calva",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CALVA)));

    /** 牛肚 - 诺曼特色食物 */
    public static final RegistryObject<Item> TRIPES = ITEMS.register("tripes",
            () -> new Item(new Item.Properties().food(ModFoodProperties.TRIPES)));

    /** 血肠 - 诺曼特色食物 */
    public static final RegistryObject<Item> BOUDIN_NOIR = ITEMS.register("boudin_noir",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BOUDIN_NOIR)));

    // ================ 印度食物 ================

    /** 蔬菜咖喱 - 印度特色素食，营养丰富 */
    public static final RegistryObject<Item> VEG_CURRY = ITEMS.register("veg_curry",
            () -> new Item(new Item.Properties().food(ModFoodProperties.VEG_CURRY)));

    /** 鸡肉咖喱 - 印度特色肉食，高营养 */
    public static final RegistryObject<Item> MURGH_CURRY = ITEMS.register("murgh_curry",
            () -> new Item(new Item.Properties().food(ModFoodProperties.MURGH_CURRY)));

    /** 奶球甜点 - 提供速度效果 */
    public static final RegistryObject<Item> RASGULLA = ITEMS.register("rasgulla",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RASGULLA)));

    // ================ 玛雅食物 ================

    /** 可可亚 - 玛雅特色饮品，提供夜视效果 */
    public static final RegistryObject<Item> CACAUHAA = ITEMS.register("cacauhaa",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CACAUHAA)));

    /** 玛萨 - 玛雅特色食物 */
    public static final RegistryObject<Item> MASA = ITEMS.register("masa",
            () -> new Item(new Item.Properties().food(ModFoodProperties.MASA)));

    /** 瓦 - 玛雅特色食物，提供挖掘速度效果 */
    public static final RegistryObject<Item> WAH = ITEMS.register("wah",
            () -> new Item(new Item.Properties().food(ModFoodProperties.WAH)));

    /** 巴尔切酒 - 玛雅树皮酿制的酒精饮品 */
    public static final RegistryObject<Item> BALCHE = ITEMS.register("balche",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BALCHE)));

    /** 西克尔帕酱 - 玛雅番茄酱 */
    public static final RegistryObject<Item> SIKILPAH = ITEMS.register("sikilpah",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SIKILPAH)));

    // ================ 日本食物 ================

    /** 清酒 - 日本特色饮品，提供跳跃效果 */
    public static final RegistryObject<Item> SAKE = ITEMS.register("sake",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SAKE)));

    /** 乌冬面 - 日本特色面条 */
    public static final RegistryObject<Item> UDON = ITEMS.register("udon",
            () -> new Item(new Item.Properties().food(ModFoodProperties.UDON)));

    /** 烤鱿鱼 - 日本特色食物，提供水下呼吸 */
    public static final RegistryObject<Item> IKAYAKI = ITEMS.register("ikayaki",
            () -> new Item(new Item.Properties().food(ModFoodProperties.IKAYAKI)));

    /** 樱桃 - 日本甜果 */
    public static final RegistryObject<Item> CHERRIES = ITEMS.register("cherries",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CHERRIES)));

    /** 樱花 - 装饰性可食用花朵 */
    public static final RegistryObject<Item> CHERRY_BLOSSOM = ITEMS.register("cherry_blossom",
            () -> new Item(new Item.Properties().food(ModFoodProperties.CHERRY_BLOSSOM)));

    // ================ 拜占庭食物 ================

    /** 葡萄酒 - 拜占庭特色饮品 */
    public static final RegistryObject<Item> WINE = ITEMS.register("wine",
            () -> new Item(new Item.Properties().food(ModFoodProperties.WINE)));

    /** 玛尔瓦西亚葡萄酒 - 高级葡萄酒，提供抗性效果 */
    public static final RegistryObject<Item> MALVASIA_WINE = ITEMS.register("malvasia_wine",
            () -> new Item(new Item.Properties().food(ModFoodProperties.MALVASIA_WINE)));

    /** 羊奶酪 - 拜占庭特色食物 */
    public static final RegistryObject<Item> FETA = ITEMS.register("feta",
            () -> new Item(new Item.Properties().food(ModFoodProperties.FETA)));

    /** 烤肉串 - 拜占庭特色食物，提供瞬间治疗 */
    public static final RegistryObject<Item> SOUVLAKI = ITEMS.register("souvlaki",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SOUVLAKI)));

    // ================ 特殊物品 ================

    /** 钱袋 - 用于存储货币 */
    public static final RegistryObject<Item> PURSE = ITEMS.register("purse",
            () -> new ItemMillPurse(new Item.Properties().stacksTo(1)));

    /** 村庄标志 - 村庄建设标志 */
    public static final RegistryObject<Item> VILLAGE_SIGN = ITEMS.register("village_sign",
            () -> new ItemVillageSign(new Item.Properties().stacksTo(16)));

    // ================ 装饰物品 ================

    /** 挂毯 - 诺曼装饰墙饰 */
    public static final RegistryObject<Item> TAPESTRY = ITEMS.register("tapestry",
            () -> new Item(new Item.Properties()));

    /** 印度雕像 - 印度文化装饰雕像 */
    public static final RegistryObject<Item> INDIAN_STATUE = ITEMS.register("indian_statue",
            () -> new Item(new Item.Properties()));

    /** 玛雅雕像 - 玛雅文化装饰雕像 */
    public static final RegistryObject<Item> MAYAN_STATUE = ITEMS.register("mayan_statue",
            () -> new Item(new Item.Properties()));

    /** 拜占庭小圣像 - 拜占庭文化小型宗教图标 */
    public static final RegistryObject<Item> BYZANTINE_ICON_SMALL = ITEMS.register("byzantine_icon_small",
            () -> new Item(new Item.Properties()));

    /** 拜占庭中圣像 - 拜占庭文化中型宗教图标 */
    public static final RegistryObject<Item> BYZANTINE_ICON_MEDIUM = ITEMS.register("byzantine_icon_medium",
            () -> new Item(new Item.Properties()));

    /** 拜占庭大圣像 - 拜占庭文化大型宗教图标 */
    public static final RegistryObject<Item> BYZANTINE_ICON_LARGE = ITEMS.register("byzantine_icon_large",
            () -> new Item(new Item.Properties()));

    // ================ 特殊工具与材料 ================

    /** 砖模 - 印度文化制砖工具 */
    public static final RegistryObject<Item> BRICK_MOULD = ITEMS.register("brick_mould",
            () -> new Item(new Item.Properties()));

    // ================ 拜占庭食物（缺失物品） ================

    /** 橄榄 - 拜占庭料理中使用的地中海橄榄 */
    public static final RegistryObject<Item> OLIVES = ITEMS.register("olives",
            () -> new Item(new Item.Properties().food(ModFoodProperties.OLIVES)));

    /** 橄榄油 - 橄榄制成的优质烹饪油 */
    public static final RegistryObject<Item> OLIVE_OIL = ITEMS.register("olive_oil",
            () -> new Item(new Item.Properties()));

    // ================ 诺曼工具与武器 ================

    /** 诺曼剑 - 平衡良好的中世纪刀刃 */
    public static final RegistryObject<Item> NORMAN_SWORD = ITEMS.register("norman_sword",
            () -> new SwordItem(ModToolMaterials.NORMAN, 3, -2.4F, new Item.Properties()));

    /** 诺曼斧 - 中世纪伐木与战斗斧 */
    public static final RegistryObject<Item> NORMAN_AXE = ITEMS.register("norman_axe",
            () -> new AxeItem(ModToolMaterials.NORMAN, 6.0F, -3.0F, new Item.Properties()));

    /** 诺曼镐 - 中世纪挖掘工具 */
    public static final RegistryObject<Item> NORMAN_PICKAXE = ITEMS.register("norman_pickaxe",
            () -> new PickaxeItem(ModToolMaterials.NORMAN, 1, -2.8F, new Item.Properties()));

    /** 诺曼铲 - 中世纪挖掘工具 */
    public static final RegistryObject<Item> NORMAN_SHOVEL = ITEMS.register("norman_shovel",
            () -> new ShovelItem(ModToolMaterials.NORMAN, 1.5F, -3.0F, new Item.Properties()));

    /** 诺曼锄 - 中世纪农耕工具 */
    public static final RegistryObject<Item> NORMAN_HOE = ITEMS.register("norman_hoe",
            () -> new HoeItem(ModToolMaterials.NORMAN, -2, -1.0F, new Item.Properties()));

    // ================ 诺曼护甲 ================

    /** 诺曼头盔 - 中世纪链甲/板甲头盔 */
    public static final RegistryObject<Item> NORMAN_HELMET = ITEMS.register("norman_helmet",
            () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 诺曼胸甲 - 中世纪链甲/板甲护甲 */
    public static final RegistryObject<Item> NORMAN_CHESTPLATE = ITEMS.register("norman_chestplate",
            () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 诺曼护腿 - 中世纪链甲/板甲腿部护甲 */
    public static final RegistryObject<Item> NORMAN_LEGGINGS = ITEMS.register("norman_leggings",
            () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 诺曼靴子 - 中世纪链甲/板甲靴子 */
    public static final RegistryObject<Item> NORMAN_BOOTS = ITEMS.register("norman_boots",
            () -> new ArmorItem(ModArmorMaterials.NORMAN, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 玛雅黑曜石工具 ================

    /** 玛雅斧 - 锋利的黑曜石斧 */
    public static final RegistryObject<Item> MAYAN_AXE = ITEMS.register("mayan_axe",
            () -> new AxeItem(ModToolMaterials.MAYAN_OBSIDIAN, 7.0F, -3.0F, new Item.Properties()));

    /** 玛雅镐 - 锋利的黑曜石镐 */
    public static final RegistryObject<Item> MAYAN_PICKAXE = ITEMS.register("mayan_pickaxe",
            () -> new PickaxeItem(ModToolMaterials.MAYAN_OBSIDIAN, 1, -2.8F, new Item.Properties()));

    /** 玛雅铲 - 锋利的黑曜石铲 */
    public static final RegistryObject<Item> MAYAN_SHOVEL = ITEMS.register("mayan_shovel",
            () -> new ShovelItem(ModToolMaterials.MAYAN_OBSIDIAN, 1.5F, -3.0F, new Item.Properties()));

    /** 玛雅锄 - 锋利的黑曜石锄 */
    public static final RegistryObject<Item> MAYAN_HOE = ITEMS.register("mayan_hoe",
            () -> new HoeItem(ModToolMaterials.MAYAN_OBSIDIAN, -1, 0.0F, new Item.Properties()));

    /** 玛雅权杖 - 仪式用黑曜石权杖 */
    public static final RegistryObject<Item> MAYAN_MACE = ITEMS.register("mayan_mace",
            () -> new SwordItem(ModToolMaterials.MAYAN_OBSIDIAN, 4, -2.6F, new Item.Properties()));

    // ================ 拜占庭工具与武器 ================

    /** 拜占庭权杖 - 先进钢制权杖 */
    public static final RegistryObject<Item> BYZANTINE_MACE = ITEMS.register("byzantine_mace",
            () -> new SwordItem(ModToolMaterials.BYZANTINE, 4, -2.5F, new Item.Properties()));

    /** 拜占庭镐 - 先进钢制采矿工具 */
    public static final RegistryObject<Item> BYZANTINE_PICKAXE = ITEMS.register("byzantine_pickaxe",
            () -> new PickaxeItem(ModToolMaterials.BYZANTINE, 1, -2.8F, new Item.Properties()));

    /** 拜占庭斧 - 先进钢制伐木与战斗斧 */
    public static final RegistryObject<Item> BYZANTINE_AXE = ITEMS.register("byzantine_axe",
            () -> new AxeItem(ModToolMaterials.BYZANTINE, 6.0F, -3.1F, new Item.Properties()));

    /** 拜占庭铲 - 先进钢制挖掘工具 */
    public static final RegistryObject<Item> BYZANTINE_SHOVEL = ITEMS.register("byzantine_shovel",
            () -> new ShovelItem(ModToolMaterials.BYZANTINE, 1.5F, -3.0F, new Item.Properties()));

    /** 拜占庭锄 - 先进钢制农耕工具 */
    public static final RegistryObject<Item> BYZANTINE_HOE = ITEMS.register("byzantine_hoe",
            () -> new HoeItem(ModToolMaterials.BYZANTINE, -2, -1.0F, new Item.Properties()));

    // ================ 拜占庭护甲 ================

    /** 拜占庭头盔 - 先进鳞甲头盔 */
    public static final RegistryObject<Item> BYZANTINE_HELMET = ITEMS.register("byzantine_helmet",
            () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 拜占庭胸甲 - 先进鳞甲护甲 */
    public static final RegistryObject<Item> BYZANTINE_CHESTPLATE = ITEMS.register("byzantine_chestplate",
            () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 拜占庭护腿 - 先进鳞甲腿部护甲 */
    public static final RegistryObject<Item> BYZANTINE_LEGGINGS = ITEMS.register("byzantine_leggings",
            () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 拜占庭靴子 - 先进鳞甲靴子 */
    public static final RegistryObject<Item> BYZANTINE_BOOTS = ITEMS.register("byzantine_boots",
            () -> new ArmorItem(ModArmorMaterials.BYZANTINE, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 日本工具与武器 ================

    /** 日本刀 - 精工武士刀 */
    public static final RegistryObject<Item> JAPANESE_SWORD = ITEMS.register("japanese_sword",
            () -> new SwordItem(ModToolMaterials.JAPANESE, 4, -2.0F, new Item.Properties()));

    /** 日本弓 - 传统和弓 */
    public static final RegistryObject<Item> JAPANESE_BOW = ITEMS.register("japanese_bow",
            () -> new BowItem(new Item.Properties().durability(500)));

    // ================ 日本护卫护甲 ================

    /** 日本护卫头盔 - 基础武士头盔 */
    public static final RegistryObject<Item> JAPANESE_GUARD_HELMET = ITEMS.register("japanese_guard_helmet",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 日本护卫胸甲 - 基础武士护甲 */
    public static final RegistryObject<Item> JAPANESE_GUARD_CHESTPLATE = ITEMS.register("japanese_guard_chestplate",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 日本护卫护腿 - 基础武士腿部护甲 */
    public static final RegistryObject<Item> JAPANESE_GUARD_LEGGINGS = ITEMS.register("japanese_guard_leggings",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 日本护卫靴子 - 基础武士靴子 */
    public static final RegistryObject<Item> JAPANESE_GUARD_BOOTS = ITEMS.register("japanese_guard_boots",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_GUARD, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 日本蓝色武士护甲 ================

    /** 日本蓝色头盔 - 精英蓝色武士头盔 */
    public static final RegistryObject<Item> JAPANESE_BLUE_HELMET = ITEMS.register("japanese_blue_helmet",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 日本蓝色胸甲 - 精英蓝色武士护甲 */
    public static final RegistryObject<Item> JAPANESE_BLUE_CHESTPLATE = ITEMS.register("japanese_blue_chestplate",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 日本蓝色护腿 - 精英蓝色武士腿部护甲 */
    public static final RegistryObject<Item> JAPANESE_BLUE_LEGGINGS = ITEMS.register("japanese_blue_leggings",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 日本蓝色靴子 - 精英蓝色武士靴子 */
    public static final RegistryObject<Item> JAPANESE_BLUE_BOOTS = ITEMS.register("japanese_blue_boots",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_BLUE, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 日本红色武士护甲 ================

    /** 日本红色头盔 - 大师级红色武士头盔 */
    public static final RegistryObject<Item> JAPANESE_RED_HELMET = ITEMS.register("japanese_red_helmet",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 日本红色胸甲 - 大师级红色武士护甲 */
    public static final RegistryObject<Item> JAPANESE_RED_CHESTPLATE = ITEMS.register("japanese_red_chestplate",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 日本红色护腿 - 大师级红色武士腿部护甲 */
    public static final RegistryObject<Item> JAPANESE_RED_LEGGINGS = ITEMS.register("japanese_red_leggings",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 日本红色靴子 - 大师级红色武士靴子 */
    public static final RegistryObject<Item> JAPANESE_RED_BOOTS = ITEMS.register("japanese_red_boots",
            () -> new ArmorItem(ModArmorMaterials.JAPANESE_RED, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 因纽特文明 ================

    /** 因纽特三叉戟 - 传统渔猎长矛 */
    public static final RegistryObject<Item> INUIT_TRIDENT = ITEMS.register("inuit_trident",
            () -> new SwordItem(ModToolMaterials.INUIT, 5, -2.8F, new Item.Properties()));

    /** 因纽特弓 - 传统骨筋复合弓 */
    public static final RegistryObject<Item> INUIT_BOW = ITEMS.register("inuit_bow",
            () -> new BowItem(new Item.Properties().durability(384)));

    /** 乌卢刀 - 传统因纽特女性用刀，用于处理皮革和食物 */
    public static final RegistryObject<Item> ULU = ITEMS.register("ulu",
            () -> new SwordItem(ModToolMaterials.INUIT, 2, -1.0F, new Item.Properties()));

    // ================ 因纽特毛皮护甲 ================

    /** 毛皮头盔 - 温暖的冬季头部装备 */
    public static final RegistryObject<Item> FUR_HELMET = ITEMS.register("fur_helmet",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 毛皮胸甲 - 温暖的冬季胸部保护 */
    public static final RegistryObject<Item> FUR_CHESTPLATE = ITEMS.register("fur_chestplate",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 毛皮护腿 - 温暖的冬季腿部保护 */
    public static final RegistryObject<Item> FUR_LEGGINGS = ITEMS.register("fur_leggings",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 毛皮靴子 - 温暖的冬季足部保护 */
    public static final RegistryObject<Item> FUR_BOOTS = ITEMS.register("fur_boots",
            () -> new ArmorItem(ModArmorMaterials.FUR, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 因纽特食物 ================

    /** 生熊肉 - 用于烹饪的生熊肉 */
    public static final RegistryObject<Item> BEAR_MEAT_RAW = ITEMS.register("bear_meat_raw",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BEAR_MEAT_RAW)));

    /** 熟熊肉 - 熟熊肉，提供抗寒性 */
    public static final RegistryObject<Item> BEAR_MEAT_COOKED = ITEMS.register("bear_meat_cooked",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BEAR_MEAT_COOKED)));

    /** 生狼肉 - 用于烹饪的生狼肉 */
    public static final RegistryObject<Item> WOLF_MEAT_RAW = ITEMS.register("wolf_meat_raw",
            () -> new Item(new Item.Properties().food(ModFoodProperties.WOLF_MEAT_RAW)));

    /** 熟狼肉 - 熟狼肉，提供速度提升 */
    public static final RegistryObject<Item> WOLF_MEAT_COOKED = ITEMS.register("wolf_meat_cooked",
            () -> new Item(new Item.Properties().food(ModFoodProperties.WOLF_MEAT_COOKED)));

    /** 生海鲜 - 生的北极海鲜 */
    public static final RegistryObject<Item> SEAFOOD_RAW = ITEMS.register("seafood_raw",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SEAFOOD_RAW)));

    /** 熟海鲜 - 熟的北极海鲜 */
    public static final RegistryObject<Item> SEAFOOD_COOKED = ITEMS.register("seafood_cooked",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SEAFOOD_COOKED)));

    /** 因纽特熊肉炖菜 - 丰盛的生存炖菜 */
    public static final RegistryObject<Item> INUIT_BEAR_STEW = ITEMS.register("inuit_bear_stew",
            () -> new Item(new Item.Properties().food(ModFoodProperties.INUIT_BEAR_STEW)));

    /** 因纽特肉类炖菜 - 混合肉类生存炖菜 */
    public static final RegistryObject<Item> INUIT_MEATY_STEW = ITEMS.register("inuit_meaty_stew",
            () -> new Item(new Item.Properties().food(ModFoodProperties.INUIT_MEATY_STEW)));

    /** 因纽特土豆炖菜 - 蔬菜类生存炖菜 */
    public static final RegistryObject<Item> INUIT_POTATO_STEW = ITEMS.register("inuit_potato_stew",
            () -> new Item(new Item.Properties().food(ModFoodProperties.INUIT_POTATO_STEW)));

    // ================ 因纽特材料 ================

    /** 鞣制皮革 - 用于制作的加工皮革 */
    public static final RegistryObject<Item> TANNED_HIDE = ITEMS.register("tanned_hide",
            () -> new Item(new Item.Properties()));

    /** 皮革挂饰 - 装饰性皮革墙饰 */
    public static final RegistryObject<Item> HIDE_HANGING = ITEMS.register("hide_hanging",
            () -> new Item(new Item.Properties()));

    // ================ 塞尔柱文明 ================

    /** 塞尔柱弯刀 - 塞尔柱土耳其人的弯曲钢剑 */
    public static final RegistryObject<Item> SELJUK_SCIMITAR = ITEMS.register("seljuk_scimitar",
            () -> new SwordItem(ModToolMaterials.SELJUK, 4, -2.2F, new Item.Properties()));

    /** 塞尔柱弓 - 复合反曲弓 */
    public static final RegistryObject<Item> SELJUK_BOW = ITEMS.register("seljuk_bow",
            () -> new BowItem(new Item.Properties().durability(450)));

    // ================ 塞尔柱盔甲 ================

    /** 塞尔柱头巾 - 传统头饰 */
    public static final RegistryObject<Item> SELJUK_TURBAN = ITEMS.register("seljuk_turban",
            () -> new ArmorItem(ModArmorMaterials.SELJUK_WOOL, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 塞尔柱头盔 - 伊斯兰设计的钢制头盔 */
    public static final RegistryObject<Item> SELJUK_HELMET = ITEMS.register("seljuk_helmet",
            () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.HELMET, new Item.Properties()));

    /** 塞尔柱胸甲 - 札甲钢制护甲 */
    public static final RegistryObject<Item> SELJUK_CHESTPLATE = ITEMS.register("seljuk_chestplate",
            () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    /** 塞尔柱护腿 - 钢制腿部保护 */
    public static final RegistryObject<Item> SELJUK_LEGGINGS = ITEMS.register("seljuk_leggings",
            () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    /** 塞尔柱靴子 - 钢制足部保护 */
    public static final RegistryObject<Item> SELJUK_BOOTS = ITEMS.register("seljuk_boots",
            () -> new ArmorItem(ModArmorMaterials.SELJUK, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ================ 塞尔柱食物 ================

    /** 皮德饼 - 土耳其扁面包 */
    public static final RegistryObject<Item> PIDE = ITEMS.register("pide",
            () -> new Item(new Item.Properties().food(ModFoodProperties.PIDE)));

    /** 哈尔瓦 - 土耳其甜点 */
    public static final RegistryObject<Item> HELVA = ITEMS.register("helva",
            () -> new Item(new Item.Properties().food(ModFoodProperties.HELVA)));

    /** 土耳其软糖 - 土耳其软糖糖果 */
    public static final RegistryObject<Item> LOKUM = ITEMS.register("lokum",
            () -> new Item(new Item.Properties().food(ModFoodProperties.LOKUM)));

    /** 酸奶饮料 - 传统酸奶饮品 */
    public static final RegistryObject<Item> AYRAN = ITEMS.register("ayran",
            () -> new Item(new Item.Properties().food(ModFoodProperties.AYRAN)));

    /** 酸奶 - 发酵乳制品 */
    public static final RegistryObject<Item> YOGURT = ITEMS.register("yogurt",
            () -> new Item(new Item.Properties().food(ModFoodProperties.YOGURT)));

    /** 开心果 - 开心果树的坚果 */
    public static final RegistryObject<Item> PISTACHIOS = ITEMS.register("pistachios",
            () -> new Item(new Item.Properties().food(ModFoodProperties.PISTACHIOS)));

    // ================ 塞尔柱材料与作物 ================

    /** 棉花 - 塞尔柱地区的纺织作物 */
    public static final RegistryObject<Item> COTTON = ITEMS.register("cotton",
            () -> new Item(new Item.Properties()));

    /** 塞尔柱羊毛衣物 - 传统羊毛服装 */
    public static final RegistryObject<Item> SELJUK_WOOL_CLOTHES = ITEMS.register("seljuk_wool_clothes",
            () -> new Item(new Item.Properties()));

    /** 塞尔柱棉布衣物 - 奢华棉布服装 */
    public static final RegistryObject<Item> SELJUK_COTTON_CLOTHES = ITEMS.register("seljuk_cotton_clothes",
            () -> new Item(new Item.Properties()));

    // ================ 塞尔柱装饰物品 ================

    /** 小型壁毯 - 小装饰地毯 */
    public static final RegistryObject<Item> WALL_CARPET_SMALL = ITEMS.register("wall_carpet_small",
            () -> new Item(new Item.Properties()));

    /** 中型壁毯 - 中装饰地毯 */
    public static final RegistryObject<Item> WALL_CARPET_MEDIUM = ITEMS.register("wall_carpet_medium",
            () -> new Item(new Item.Properties()));

    /** 大型壁毯 - 大装饰地毯 */
    public static final RegistryObject<Item> WALL_CARPET_LARGE = ITEMS.register("wall_carpet_large",
            () -> new Item(new Item.Properties()));

    // ================ 特殊盔甲 ================

    /** 玛雅任务王冠 - 仪式任务奖励 */
    public static final RegistryObject<Item> MAYAN_QUEST_CROWN = ITEMS.register("mayan_quest_crown",
            () -> new ArmorItem(ModArmorMaterials.MAYAN_CEREMONIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1)));

    // ================ 魔法物品 - 法杖 ================

    /** 召唤法杖 - 用于从模板导入建筑 */
    public static final RegistryObject<Item> WAND_SUMMONING = ITEMS.register("wand_summoning",
            () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
                    com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.SUMMONING,
                    new Item.Properties()));

    /** 否定法杖 - 用于将建筑导出为模板 */
    public static final RegistryObject<Item> WAND_NEGATION = ITEMS.register("wand_negation",
            () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
                    com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.NEGATION,
                    new Item.Properties()));

    /** 创造法杖 - 管理作物权限和箱子锁定 */
    public static final RegistryObject<Item> WAND_CREATIVE = ITEMS.register("wand_creative",
            () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
                    com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.CREATIVE,
                    new Item.Properties()));

    /** 音叉 - 方块检查工具 */
    public static final RegistryObject<Item> TUNING_FORK = ITEMS.register("tuning_fork",
            () -> new com.jasoncian.millenaire_rewrite.items.tools.MillWandItem(
                    com.jasoncian.millenaire_rewrite.items.tools.MillWandItem.WandType.TUNING_FORK,
                    new Item.Properties()));

    // ================ 魔法物品 - 护身符 ================

    /** 斯科尔与哈提护身符 - 控制昼夜循环 */
    public static final RegistryObject<Item> AMULET_SKOLL_HATI = ITEMS.register("amulet_skoll_hati",
            () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
                    com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.SKOLL_HATI,
                    new Item.Properties()));

    /** 炼金术士护身符 - 探测附近矿石 */
    public static final RegistryObject<Item> AMULET_ALCHEMIST = ITEMS.register("amulet_alchemist",
            () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
                    com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.ALCHEMIST,
                    new Item.Properties()));

    /** 毗湿奴护身符 - 探测附近生物 */
    public static final RegistryObject<Item> AMULET_VISHNU = ITEMS.register("amulet_vishnu",
            () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
                    com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.VISHNU,
                    new Item.Properties()));

    /** 世界之树护身符 - 显示高度信息 */
    public static final RegistryObject<Item> AMULET_YGGDRASIL = ITEMS.register("amulet_yggdrasil",
            () -> new com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem(
                    com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem.AmuletType.YGGDRASIL,
                    new Item.Properties()));

    // ================ 羊皮纸/卷轴 ================

    // 诺曼羊皮纸
    /** 诺曼村民羊皮纸 - 诺曼人指南 */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_VILLAGER = ITEMS.register("parchment_norman_villager",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 诺曼建筑羊皮纸 - 诺曼建筑指南 */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_BUILDING = ITEMS.register("parchment_norman_building",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 诺曼物品羊皮纸 - 诺曼物品指南 */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_ITEM = ITEMS.register("parchment_norman_item",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 诺曼全书羊皮纸 - 诺曼完整指南 */
    public static final RegistryObject<Item> PARCHMENT_NORMAN_ALL = ITEMS.register("parchment_norman_all",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    // 拜占庭羊皮纸
    /** 拜占庭村民羊皮纸 - 拜占庭人指南 */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_VILLAGER = ITEMS.register(
            "parchment_byzantine_villager",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 拜占庭建筑羊皮纸 - 拜占庭建筑指南 */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_BUILDING = ITEMS.register(
            "parchment_byzantine_building",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 拜占庭物品羊皮纸 - 拜占庭物品指南 */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_ITEM = ITEMS.register("parchment_byzantine_item",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 拜占庭全书羊皮纸 - 拜占庭完整指南 */
    public static final RegistryObject<Item> PARCHMENT_BYZANTINE_ALL = ITEMS.register("parchment_byzantine_all",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    // 印地羊皮纸
    /** 印地村民羊皮纸 - 印地人指南 */
    public static final RegistryObject<Item> PARCHMENT_HINDI_VILLAGER = ITEMS.register("parchment_hindi_villager",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 印地建筑羊皮纸 - 印地建筑指南 */
    public static final RegistryObject<Item> PARCHMENT_HINDI_BUILDING = ITEMS.register("parchment_hindi_building",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 印地物品羊皮纸 - 印地物品指南 */
    public static final RegistryObject<Item> PARCHMENT_HINDI_ITEM = ITEMS.register("parchment_hindi_item",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 印地全书羊皮纸 - 印地完整指南 */
    public static final RegistryObject<Item> PARCHMENT_HINDI_ALL = ITEMS.register("parchment_hindi_all",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    // 玛雅羊皮纸
    /** 玛雅村民羊皮纸 - 玛雅人指南 */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_VILLAGER = ITEMS.register("parchment_mayan_villager",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 玛雅建筑羊皮纸 - 玛雅建筑指南 */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_BUILDING = ITEMS.register("parchment_mayan_building",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 玛雅物品羊皮纸 - 玛雅物品指南 */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_ITEM = ITEMS.register("parchment_mayan_item",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 玛雅全书羊皮纸 - 玛雅完整指南 */
    public static final RegistryObject<Item> PARCHMENT_MAYAN_ALL = ITEMS.register("parchment_mayan_all",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    // 日本羊皮纸
    /** 日本村民羊皮纸 - 日本人指南 */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_VILLAGER = ITEMS.register("parchment_japanese_villager",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 日本建筑羊皮纸 - 日本建筑指南 */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_BUILDING = ITEMS.register("parchment_japanese_building",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 日本物品羊皮纸 - 日本物品指南 */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_ITEM = ITEMS.register("parchment_japanese_item",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 日本全书羊皮纸 - 日本完整指南 */
    public static final RegistryObject<Item> PARCHMENT_JAPANESE_ALL = ITEMS.register("parchment_japanese_all",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    // ================ 特殊羊皮纸 ================

    /** 村庄卷轴 - 一般村庄信息 */
    public static final RegistryObject<Item> PARCHMENT_VILLAGE_SCROLL = ITEMS.register("parchment_village_scroll",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    /** 苦行僧羊皮纸 - 神圣的印度教文本 */
    public static final RegistryObject<Item> PARCHMENT_SADHU = ITEMS.register("parchment_sadhu",
            () -> new ItemMillParchment(new Item.Properties().stacksTo(16)));

    // ================ 油漆桶 ================

    /** 白色油漆桶 - 用于建筑的白色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_WHITE = ITEMS.register("paint_bucket_white",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 橙色油漆桶 - 用于建筑的橙色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_ORANGE = ITEMS.register("paint_bucket_orange",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 品红色油漆桶 - 用于建筑的品红色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_MAGENTA = ITEMS.register("paint_bucket_magenta",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 淡蓝色油漆桶 - 用于建筑的淡蓝色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_LIGHT_BLUE = ITEMS.register("paint_bucket_light_blue",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 黄色油漆桶 - 用于建筑的黄色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_YELLOW = ITEMS.register("paint_bucket_yellow",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 青柠色油漆桶 - 用于建筑的青柠色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_LIME = ITEMS.register("paint_bucket_lime",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 粉色油漆桶 - 用于建筑的粉色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_PINK = ITEMS.register("paint_bucket_pink",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 灰色油漆桶 - 用于建筑的灰色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_GRAY = ITEMS.register("paint_bucket_gray",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 淡灰色油漆桶 - 用于建筑的淡灰色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_LIGHT_GRAY = ITEMS.register("paint_bucket_light_gray",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 青色油漆桶 - 用于建筑的青色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_CYAN = ITEMS.register("paint_bucket_cyan",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 紫色油漆桶 - 用于建筑的紫色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_PURPLE = ITEMS.register("paint_bucket_purple",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 蓝色油漆桶 - 用于建筑的蓝色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_BLUE = ITEMS.register("paint_bucket_blue",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 棕色油漆桶 - 用于建筑的棕色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_BROWN = ITEMS.register("paint_bucket_brown",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 绿色油漆桶 - 用于建筑的绿色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_GREEN = ITEMS.register("paint_bucket_green",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 红色油漆桶 - 用于建筑的红色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_RED = ITEMS.register("paint_bucket_red",
            () -> new Item(new Item.Properties().stacksTo(16)));

    /** 黑色油漆桶 - 用于建筑的黑色油漆 */
    public static final RegistryObject<Item> PAINT_BUCKET_BLACK = ITEMS.register("paint_bucket_black",
            () -> new Item(new Item.Properties().stacksTo(16)));

    // ================ Block Items ================
    // 注意：方块物品现在在ModBlockItems中单独注册
    // 这样可以更好地处理装饰方块的各个变体

    /**
     * 注册所有物品到模组事件总线
     *
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
