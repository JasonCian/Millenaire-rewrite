package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.items.ItemMillParchment;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.jasoncian.millenaire_rewrite.data.ParchmentContentData;

/**
 * Millenaire 创意模式标签页管理器 - 1.20.1现代化实现
 *
 * 负责创建和管理所有 Millenaire 相关的创意模式标签页
 * 按文化和功能分类组织物品，提供更好的用户体验
 *
 * 功能特性：
 * - 方块专用标签页
 * - 各文化特色物品分类
 * - 食物专用标签页
 * - 杂项物品标签页
 * - 现代化的标签页注册系统
 * - 支持动态物品添加
 *
 * 设计理念：
 * - 分类清晰：不同文化和功能的物品分开展示
 * - 易于浏览：相关物品集中在对应标签页
 * - 扩展性强：便于添加新的文化分类
 *
 * @author JasonCian
 * @version 0.1.3-alpha
 * @since 1.20.1
 */
public class MillCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MillenaireRewrite.MOD_ID);

    // ================ 方块标签页 ================

    /** Millenaire 方块标签页 - 所有建筑和装饰方块 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_BLOCKS = 
        CREATIVE_MODE_TABS.register("blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.blocks"))
            .icon(() -> new ItemStack(ModBlocks.VILLAGE_STONE.get().asItem()))
            .displayItems((parameters, output) -> {
                // 核心功能方块
                output.accept(ModBlocks.VILLAGE_STONE.get().asItem());
                
                // ⚠️  注意：统一建筑方块系统的物品现在通过 BuildingBlockRegistry 自动管理
                // 
                // 方块分类规则：
                // - BASIC文化的所有建筑方块 → 显示在此通用方块标签页
                // - 特定文化的建筑方块（NORMAN、BYZANTINE等） → 显示在对应的文化标签页
                // - 所有建筑方块族（基础方块+楼梯+半砖+墙）都会自动生成和分类
                
                // TODO: 实现新的统一方块系统的创造模式标签页集成
                // TODO: 通过 BuildingBlockRegistry 自动获取 BASIC 文化的所有建筑方块物品
                // TODO: 确保特定文化方块分配到正确的文化标签页，通用方块留在此标签页
            })
            .build());

    // ================ 华夏文化标签页 ================
    // TODO: 华夏文明 - 中华文化物品标签页（预留空间）
    // 
    // 设计规划：
    // - 中华古代文明的代表性物品和建筑
    // - 包含传统建筑方块：青砖、红木、琉璃瓦等
    // - 传统食物：米饭、茶叶、豆腐、面条等
    // - 传统服饰：汉服、官服等
    // - 传统工具：青铜器、铁器等
    // - 文化物品：书法卷轴、瓷器、丝绸等
    // - 传统装饰：屏风、灯笼、石狮等
    // - 货币系统：铜钱、银两、黄金等
    //
    // 实现时需要创建：
    // public static final RegistryObject<CreativeModeTab> MILLENAIRE_HUAXIA = 
    //     CREATIVE_MODE_TABS.register("huaxia", () -> CreativeModeTab.builder()
    //         .title(Component.translatable("creativetab.millenaire_rewrite.huaxia"))
    //         .icon(() -> new ItemStack(ModItems.HUAXIA_JADE_DISK.get())) // 使用玉璧作为图标
    //         .displayItems((parameters, output) -> {
    //             // 华夏建筑方块
    //             // 华夏食物
    //             // 华夏装备
    //             // 华夏特色物品
    //             // 华夏羊皮纸
    //         })
    //         .build());

    // ================ 诺曼文化标签页 ================

    /** Millenaire 诺曼文化标签页 - 诺曼/法兰克文化物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_NORMAN = 
        CREATIVE_MODE_TABS.register("norman", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.norman"))
            .icon(() -> new ItemStack(ModItems.DENIER_OR.get()))
            .displayItems((parameters, output) -> {
                // TODO: 添加诺曼文化的建筑方块（通过 BuildingBlockRegistry 自动获取）
                // 诺曼文化建筑方块包括：石材系列 + 木质装饰 + 石质装饰
                
                // 货币系统
                output.accept(ModItems.DENIER.get());
                output.accept(ModItems.DENIER_ARGENT.get());
                output.accept(ModItems.DENIER_OR.get());
                
                // 诺曼食物
                output.accept(ModItems.CIDER_APPLE.get());
                output.accept(ModItems.CIDER.get());
                output.accept(ModItems.WINE.get());
                output.accept(ModItems.CALVA.get());
                output.accept(ModItems.TRIPES.get());
                output.accept(ModItems.BOUDIN_NOIR.get());
                
                // 诺曼装备
                output.accept(ModItems.NORMAN_HELMET.get());
                output.accept(ModItems.NORMAN_CHESTPLATE.get());
                output.accept(ModItems.NORMAN_LEGGINGS.get());
                output.accept(ModItems.NORMAN_BOOTS.get());
                output.accept(ModItems.NORMAN_SWORD.get());
                
                // 诺曼工具
                output.accept(ModItems.NORMAN_AXE.get());
                output.accept(ModItems.NORMAN_PICKAXE.get());
                output.accept(ModItems.NORMAN_SHOVEL.get());
                output.accept(ModItems.NORMAN_HOE.get());
                
                // 诺曼特色物品
                output.accept(ModItems.SILK.get());
                output.accept(ModItems.WOOL_CLOTHES.get());
                output.accept(ModItems.SILK_CLOTHES.get());
                output.accept(ModItems.TAPESTRY.get());
                
                // 诺曼羊皮纸

            })
            .build());

    // ================ 拜占庭文化标签页 ================

    /** Millenaire 拜占庭文化标签页 - 拜占庭帝国物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_BYZANTINE = 
        CREATIVE_MODE_TABS.register("byzantine", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.byzantine"))
            .icon(() -> new ItemStack(ModItems.MALVASIA_WINE.get()))
            .displayItems((parameters, output) -> {
                // TODO: 添加拜占庭文化的建筑方块（通过 BuildingBlockRegistry 自动获取）
                // 拜占庭文化建筑方块包括：拜占庭瓦片 + 金装饰
                
                // 拜占庭食物
                output.accept(ModItems.GRAPES.get());
                output.accept(ModItems.WINE.get());
                output.accept(ModItems.MALVASIA_WINE.get());
                output.accept(ModItems.FETA.get());
                output.accept(ModItems.SOUVLAKI.get());
                output.accept(ModItems.OLIVES.get());
                output.accept(ModItems.OLIVE_OIL.get());
                
                // 拜占庭装备
                output.accept(ModItems.BYZANTINE_HELMET.get());
                output.accept(ModItems.BYZANTINE_CHESTPLATE.get());
                output.accept(ModItems.BYZANTINE_LEGGINGS.get());
                output.accept(ModItems.BYZANTINE_BOOTS.get());
                output.accept(ModItems.BYZANTINE_MACE.get());
                output.accept(ModItems.BYZANTINE_PICKAXE.get());
                output.accept(ModItems.BYZANTINE_AXE.get());
                output.accept(ModItems.BYZANTINE_SHOVEL.get());
                output.accept(ModItems.BYZANTINE_HOE.get());
                
                // 拜占庭特色物品
                output.accept(ModItems.BYZANTINE_ICON_SMALL.get());
                output.accept(ModItems.BYZANTINE_ICON_MEDIUM.get());
                output.accept(ModItems.BYZANTINE_ICON_LARGE.get());
                
                // 拜占庭羊皮纸

            })
            .build());

    // ================ 日本文化标签页 ================

    /** Millenaire 日本文化标签页 - 日本武士文化物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_JAPANESE = 
        CREATIVE_MODE_TABS.register("japanese", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.japanese"))
            .icon(() -> new ItemStack(ModItems.JAPANESE_SWORD.get()))
            .displayItems((parameters, output) -> {
                // TODO: 添加日本文化的建筑方块（通过 BuildingBlockRegistry 自动获取）
                // 日本文化建筑方块包括：木框架 + 木质装饰
                
                // 日本食物
                output.accept(ModItems.RICE.get());
                output.accept(ModItems.SAKE.get());
                output.accept(ModItems.UDON.get());
                output.accept(ModItems.IKAYAKI.get());
                output.accept(ModItems.CHERRIES.get());
                output.accept(ModItems.CHERRY_BLOSSOM.get());
                
                // 日本装备
                output.accept(ModItems.JAPANESE_GUARD_HELMET.get());
                output.accept(ModItems.JAPANESE_GUARD_CHESTPLATE.get());
                output.accept(ModItems.JAPANESE_GUARD_LEGGINGS.get());
                output.accept(ModItems.JAPANESE_GUARD_BOOTS.get());
                output.accept(ModItems.JAPANESE_BLUE_HELMET.get());
                output.accept(ModItems.JAPANESE_BLUE_CHESTPLATE.get());
                output.accept(ModItems.JAPANESE_BLUE_LEGGINGS.get());
                output.accept(ModItems.JAPANESE_BLUE_BOOTS.get());
                output.accept(ModItems.JAPANESE_RED_HELMET.get());
                output.accept(ModItems.JAPANESE_RED_CHESTPLATE.get());
                output.accept(ModItems.JAPANESE_RED_LEGGINGS.get());
                output.accept(ModItems.JAPANESE_RED_BOOTS.get());
                output.accept(ModItems.JAPANESE_SWORD.get());
                output.accept(ModItems.JAPANESE_BOW.get());
                
                // 日本羊皮纸

            })
            .build());

    // ================ 玛雅文化标签页 ================

    /** Millenaire 玛雅文化标签页 - 玛雅文明物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_MAYAN = 
        CREATIVE_MODE_TABS.register("mayan", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.mayan"))
            .icon(() -> new ItemStack(ModItems.OBSIDIAN_FLAKE.get()))
            .displayItems((parameters, output) -> {
                // TODO: 添加玛雅文化的建筑方块（通过 BuildingBlockRegistry 自动获取）
                // 玛雅文化建筑方块包括：石材系列 + Galianite方块
                
                // 玛雅食物
                output.accept(ModItems.MAIZE.get());
                output.accept(ModItems.CACAUHAA.get());
                output.accept(ModItems.MASA.get());
                output.accept(ModItems.WAH.get());
                output.accept(ModItems.BALCHE.get());
                output.accept(ModItems.SIKILPAH.get());
                
                // 玛雅工具
                output.accept(ModItems.MAYAN_AXE.get());
                output.accept(ModItems.MAYAN_PICKAXE.get());
                output.accept(ModItems.MAYAN_SHOVEL.get());
                output.accept(ModItems.MAYAN_HOE.get());
                output.accept(ModItems.MAYAN_MACE.get());
                
                // 玛雅特色物品
                output.accept(ModItems.OBSIDIAN_FLAKE.get());
                output.accept(ModItems.MAYAN_STATUE.get());
                
                // 玛雅羊皮纸

            })
            .build());

    // ================ 印度文化标签页 ================

    /** Millenaire 印度文化标签页 - 印度次大陆文化物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_INDIAN = 
        CREATIVE_MODE_TABS.register("indian", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.indian"))
            .icon(() -> new ItemStack(ModItems.TURMERIC.get()))
            .displayItems((parameters, output) -> {
                // TODO: 添加印度文化的建筑方块（通过 BuildingBlockRegistry 自动获取）
                // 印度文化建筑方块包括：特色瓦片 + 土质装饰材料
                
                // 印度食物和香料
                output.accept(ModItems.TURMERIC.get());
                output.accept(ModItems.VEG_CURRY.get());
                output.accept(ModItems.MURGH_CURRY.get());
                output.accept(ModItems.RASGULLA.get());
                
                // 印度特色物品
                output.accept(ModItems.INDIAN_STATUE.get());
                output.accept(ModItems.PARCHMENT_SADHU.get());


                
                // 印度羊皮纸

            })
            .build());

    // ================ 因纽特文化标签页 ================

    /** Millenaire 因纽特文化标签页 - 北极文化物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_INUIT = 
        CREATIVE_MODE_TABS.register("inuit", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.inuit"))
            .icon(() -> new ItemStack(ModItems.FUR_HELMET.get()))
            .displayItems((parameters, output) -> {
                // TODO: 添加因纽特文化的建筑方块（通过 BuildingBlockRegistry 自动获取）
                // 因纽特文化建筑方块包括：冰砖 + 雪砖
                
                // 因纽特食物
                output.accept(ModItems.BEAR_MEAT_RAW.get());
                output.accept(ModItems.BEAR_MEAT_COOKED.get());
                output.accept(ModItems.WOLF_MEAT_RAW.get());
                output.accept(ModItems.WOLF_MEAT_COOKED.get());
                output.accept(ModItems.SEAFOOD_RAW.get());
                output.accept(ModItems.SEAFOOD_COOKED.get());
                output.accept(ModItems.INUIT_BEAR_STEW.get());
                output.accept(ModItems.INUIT_MEATY_STEW.get());
                output.accept(ModItems.INUIT_POTATO_STEW.get());
                
                // 因纽特装备
                output.accept(ModItems.FUR_HELMET.get());
                output.accept(ModItems.FUR_CHESTPLATE.get());
                output.accept(ModItems.FUR_LEGGINGS.get());
                output.accept(ModItems.FUR_BOOTS.get());
                output.accept(ModItems.INUIT_TRIDENT.get());
                output.accept(ModItems.INUIT_BOW.get());
                output.accept(ModItems.ULU.get());
                
                // 因纽特特色物品
                output.accept(ModItems.TANNED_HIDE.get());
                output.accept(ModItems.HIDE_HANGING.get());
            })
            .build());

    // ================ 食物标签页 ================

    /** Millenaire 食物标签页 - 所有食物物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_FOOD = 
        CREATIVE_MODE_TABS.register("food", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.food"))
            .icon(() -> new ItemStack(ModItems.CIDER_APPLE.get()))
            .displayItems((parameters, output) -> {
                // 作物
                output.accept(ModItems.CIDER_APPLE.get());
                output.accept(ModItems.TURMERIC.get());
                output.accept(ModItems.RICE.get());
                output.accept(ModItems.MAIZE.get());
                output.accept(ModItems.GRAPES.get());
                output.accept(ModItems.OLIVES.get());
                output.accept(ModItems.CHERRIES.get());
                output.accept(ModItems.CHERRY_BLOSSOM.get());
                
                // 诺曼食物
                output.accept(ModItems.CIDER.get());
                output.accept(ModItems.WINE.get());
                output.accept(ModItems.CALVA.get());
                output.accept(ModItems.TRIPES.get());
                output.accept(ModItems.BOUDIN_NOIR.get());
                
                // 拜占庭食物
                output.accept(ModItems.MALVASIA_WINE.get());
                output.accept(ModItems.FETA.get());
                output.accept(ModItems.SOUVLAKI.get());
                output.accept(ModItems.OLIVE_OIL.get());
                
                // 日本食物
                output.accept(ModItems.SAKE.get());
                output.accept(ModItems.UDON.get());
                output.accept(ModItems.IKAYAKI.get());
                
                // 玛雅食物
                output.accept(ModItems.CACAUHAA.get());
                output.accept(ModItems.MASA.get());
                output.accept(ModItems.WAH.get());
                output.accept(ModItems.BALCHE.get());
                output.accept(ModItems.SIKILPAH.get());
                
                // 印度食物
                output.accept(ModItems.VEG_CURRY.get());
                output.accept(ModItems.MURGH_CURRY.get());
                output.accept(ModItems.RASGULLA.get());
                
                // 因纽特食物
                output.accept(ModItems.BEAR_MEAT_RAW.get());
                output.accept(ModItems.BEAR_MEAT_COOKED.get());
                output.accept(ModItems.WOLF_MEAT_RAW.get());
                output.accept(ModItems.WOLF_MEAT_COOKED.get());
                output.accept(ModItems.SEAFOOD_RAW.get());
                output.accept(ModItems.SEAFOOD_COOKED.get());
                output.accept(ModItems.INUIT_BEAR_STEW.get());
                output.accept(ModItems.INUIT_MEATY_STEW.get());
                output.accept(ModItems.INUIT_POTATO_STEW.get());
            })
            .build());

    // ================ 杂项标签页 ================

    /** Millenaire 杂项标签页 - 工具、材料和其他物品 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_MISC = 
        CREATIVE_MODE_TABS.register("misc", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.millenaire_rewrite.misc"))
            .icon(() -> new ItemStack(ModItems.PURSE.get()))
            .displayItems((parameters, output) -> {
                // 特殊功能物品
                output.accept(ModItems.PURSE.get());
                output.accept(ModItems.VILLAGE_SIGN.get());
                output.accept(ParchmentContentData.createPARCHMENT_VILLAGE_SCROLLParchment());
                output.accept(ParchmentContentData.createPARCHMENT_SADHUParchment());
                
                // 基础材料
                output.accept(ModItems.UNKNOWN_POWDER.get());
                output.accept(ModItems.GALIANITE_DUST.get());
                output.accept(ModItems.BRICK_MOULD.get());
                
                // 装饰物品
                output.accept(ModItems.TAPESTRY.get());
                output.accept(ModItems.INDIAN_STATUE.get());
                output.accept(ModItems.MAYAN_STATUE.get());
                output.accept(ModItems.BYZANTINE_ICON_SMALL.get());
                output.accept(ModItems.BYZANTINE_ICON_MEDIUM.get());
                output.accept(ModItems.BYZANTINE_ICON_LARGE.get());
                
                // 所有油漆桶
                output.accept(ModItems.PAINT_BUCKET_WHITE.get());
                output.accept(ModItems.PAINT_BUCKET_ORANGE.get());
                output.accept(ModItems.PAINT_BUCKET_MAGENTA.get());
                output.accept(ModItems.PAINT_BUCKET_LIGHT_BLUE.get());
                output.accept(ModItems.PAINT_BUCKET_YELLOW.get());
                output.accept(ModItems.PAINT_BUCKET_LIME.get());
                output.accept(ModItems.PAINT_BUCKET_PINK.get());
                output.accept(ModItems.PAINT_BUCKET_GRAY.get());
                output.accept(ModItems.PAINT_BUCKET_LIGHT_GRAY.get());
                output.accept(ModItems.PAINT_BUCKET_CYAN.get());
                output.accept(ModItems.PAINT_BUCKET_PURPLE.get());
                output.accept(ModItems.PAINT_BUCKET_BLUE.get());
                output.accept(ModItems.PAINT_BUCKET_BROWN.get());
                output.accept(ModItems.PAINT_BUCKET_GREEN.get());
                output.accept(ModItems.PAINT_BUCKET_RED.get());
                output.accept(ModItems.PAINT_BUCKET_BLACK.get());
            })
            .build());

    /**
     * 注册所有创意标签页到模组事件总线
     *
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
