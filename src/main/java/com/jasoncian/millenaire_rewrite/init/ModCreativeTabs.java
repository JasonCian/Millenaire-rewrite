package com.jasoncian.millenaire_rewrite.init;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组创造模式物品栏注册表
 * 
 * 负责注册模组的创造模式物品栏，将模组物品分类展示：
 * - 主物品栏：包含所有核心物品
 * - 方块物品栏：包含所有建筑方块
 * - 文化物品栏：按文化分类的特殊物品
 * 
 * 使用DeferredRegister进行现代化注册。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class ModCreativeTabs {

    // 防止实例化工具类
    private ModCreativeTabs() {
        throw new UnsupportedOperationException("创造模式物品栏注册表不能被实例化");
    }

    /** 创造模式物品栏延迟注册器 */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);

    /** 千年村庄主物品栏 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_TAB = CREATIVE_MODE_TABS.register("millenaire_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.HUAXIA_GOLD_INGOT.get())) // 使用华夏金元宝作为图标
                    .title(Component.translatable("creativetab.millenaire_rewrite.millenaire_tab"))
                    .displayItems((parameters, output) -> {
                        // 在这里添加物品到创造模式物品栏

                        // ===== 华夏货币系统 =====
                        output.accept(ModItems.HUAXIA_COPPER_COIN.get());
                        output.accept(ModItems.HUAXIA_SILVER_TAEL.get());
                        output.accept(ModItems.HUAXIA_GOLD_INGOT.get());

                        // ===== 华夏工具 =====
                        output.accept(ModItems.HUAXIA_BAMBOO_HOE.get());
                        output.accept(ModItems.HUAXIA_BRONZE_SICKLE.get());
                        output.accept(ModItems.HUAXIA_IRON_FLAIL.get());
                        output.accept(ModItems.HUAXIA_BAMBOO_CHISEL.get());
                        output.accept(ModItems.HUAXIA_IRON_SAW.get());
                        output.accept(ModItems.HUAXIA_BRONZE_HAMMER.get());
                        output.accept(ModItems.HUAXIA_ABACUS.get());
                        output.accept(ModItems.HUAXIA_BAMBOO_SCROLL.get());
                        output.accept(ModItems.HUAXIA_COMPASS.get());

                        // ===== 华夏武器 =====
                        output.accept(ModItems.HUAXIA_BRONZE_JIAN.get());
                        output.accept(ModItems.HUAXIA_IRON_DAO.get());
                        output.accept(ModItems.HUAXIA_STEEL_JIAN_MASTERWORK.get());
                        output.accept(ModItems.HUAXIA_IRON_QIANG.get());
                        output.accept(ModItems.HUAXIA_STEEL_JI.get());
                        output.accept(ModItems.HUAXIA_SHUANG_DAO.get());
                        output.accept(ModItems.HUAXIA_BIAN_FINE.get());

                        // ===== 华夏食物 - 主食类 =====
                        output.accept(ModItems.HUAXIA_RICE.get());
                        output.accept(ModItems.HUAXIA_NOODLES.get());
                        output.accept(ModItems.HUAXIA_BAOZI.get());
                        output.accept(ModItems.HUAXIA_JIAOZI.get());

                        // ===== 华夏食物 - 菜肴类 =====
                        output.accept(ModItems.HUAXIA_MAPO_TOFU.get());
                        output.accept(ModItems.HUAXIA_KUNG_PAO_CHICKEN.get());
                        output.accept(ModItems.HUAXIA_BRAISED_PORK.get());

                        // ===== 华夏食物 - 小食类 =====
                        output.accept(ModItems.HUAXIA_MOONCAKE.get());
                        output.accept(ModItems.HUAXIA_TANGYUAN.get());
                        output.accept(ModItems.HUAXIA_ZONGZI.get());

                        // ===== 华夏食物 - 饮品类 =====
                        output.accept(ModItems.HUAXIA_GREEN_TEA.get());
                        output.accept(ModItems.HUAXIA_OOLONG_TEA.get());
                        output.accept(ModItems.HUAXIA_HUANGJIU.get());

                        // ===== 华夏食物 - 药膳类 =====
                        output.accept(ModItems.HUAXIA_GINSENG_SOUP.get());
                        output.accept(ModItems.HUAXIA_BIRD_NEST.get());
                        output.accept(ModItems.HUAXIA_GOJI_TEA.get());

                        // ===== 华夏贸易商品 =====
                        // 茶叶类
                        output.accept(ModItems.HUAXIA_GREEN_TEA_LEAVES.get());
                        output.accept(ModItems.HUAXIA_OOLONG_TEA_LEAVES.get());
                        output.accept(ModItems.HUAXIA_LONGJING_TEA_LEAVES.get());
                        output.accept(ModItems.HUAXIA_DAHONGPAO_TEA_LEAVES.get());
                        
                        // 瓷器类
                        output.accept(ModItems.HUAXIA_CELADON.get());
                        output.accept(ModItems.HUAXIA_WHITE_PORCELAIN.get());
                        output.accept(ModItems.HUAXIA_BLUE_WHITE_PORCELAIN.get());
                        output.accept(ModItems.HUAXIA_DOUCAI_PORCELAIN.get());
                        
                        // 丝绸类
                        output.accept(ModItems.HUAXIA_RAW_SILK.get());
                        output.accept(ModItems.HUAXIA_SILK_FABRIC.get());
                        output.accept(ModItems.HUAXIA_BROCADE.get());
                        output.accept(ModItems.HUAXIA_CLOUD_BROCADE.get());
                        
                        // 香料类
                        output.accept(ModItems.HUAXIA_STAR_ANISE.get());
                        output.accept(ModItems.HUAXIA_SICHUAN_PEPPER.get());
                        output.accept(ModItems.HUAXIA_CINNAMON.get());
                        output.accept(ModItems.HUAXIA_AGARWOOD.get());

                        // ===== 华夏特殊物品 =====
                        // 卷轴类
                        output.accept(ModItems.HUAXIA_BLUEPRINT_SCROLL.get());
                        output.accept(ModItems.HUAXIA_TRADE_RECORD_SCROLL.get());
                        output.accept(ModItems.HUAXIA_VILLAGE_MAP_SCROLL.get());
                        output.accept(ModItems.HUAXIA_SPELL_SCROLL.get());
                        
                        // 印章类
                        output.accept(ModItems.HUAXIA_VILLAGE_CHIEF_SEAL.get());
                        output.accept(ModItems.HUAXIA_TRADE_SEAL.get());
                        output.accept(ModItems.HUAXIA_ARTISAN_SEAL.get());
                        output.accept(ModItems.HUAXIA_IMPERIAL_SEAL.get());
                        
                        // 风水罗盘类
                        output.accept(ModItems.HUAXIA_BASIC_FENGSHUI_COMPASS.get());
                        output.accept(ModItems.HUAXIA_ADVANCED_FENGSHUI_COMPASS.get());
                        output.accept(ModItems.HUAXIA_MASTER_FENGSHUI_COMPASS.get());

                        // ===== 特殊物品 =====
                        output.accept(ModItems.UNIVERSAL_WALLET.get());

                        // TODO: 添加其他文化货币物品
                        // output.accept(ModItems.DENIER.get());
                        // output.accept(ModItems.DENIER_ARGENT.get());
                        // output.accept(ModItems.DENIER_OR.get());

                        // TODO: 添加工具物品
                        // output.accept(ModItems.VILLAGE_WAND.get());

                        // TODO: 添加特殊物品
                        // output.accept(ModItems.CULTURE_SCROLL.get());
                    })
                    .build());

    /** 千年村庄方块物品栏 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_BLOCKS_TAB = CREATIVE_MODE_TABS.register(
            "millenaire_blocks_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.COBBLESTONE)) // 临时使用圆石作为图标
                    .title(Component.translatable("creativetab.millenaire_rewrite.millenaire_blocks_tab"))
                    .displayItems((parameters, output) -> {
                        // 在这里添加方块到创造模式物品栏
                        // 目前为空，后续添加方块时会填充

                        // TODO: 添加文化方块
                        // output.accept(ModBlocks.NORMAN_STONE.get());
                        // output.accept(ModBlocks.JAPANESE_WOOD.get());
                        // output.accept(ModBlocks.BYZANTINE_MARBLE.get());

                        // TODO: 添加功能方块
                        // output.accept(ModBlocks.CULTURE_WORKBENCH.get());
                        // output.accept(ModBlocks.VILLAGE_CENTER.get());

                        // TODO: 添加装饰方块
                        // output.accept(ModBlocks.CULTURE_TOTEM.get());
                    })
                    .build());

    /** 千年村庄华夏文化物品栏 */
    public static final RegistryObject<CreativeModeTab> MILLENAIRE_CULTURE_TAB = CREATIVE_MODE_TABS.register(
            "millenaire_culture_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.WRITTEN_BOOK)) // 临时使用成书作为图标
                    .title(Component.translatable("creativetab.millenaire_rewrite.millenaire_culture_tab"))
                    .displayItems((parameters, output) -> {
                        // 在这里添加文化相关物品到创造模式物品栏

                        // ===== 华夏文化物品 =====

                        // 华夏货币系统
                        output.accept(ModItems.HUAXIA_COPPER_COIN.get());
                        output.accept(ModItems.HUAXIA_SILVER_TAEL.get());
                        output.accept(ModItems.HUAXIA_GOLD_INGOT.get());

                        // 华夏工具
                        output.accept(ModItems.HUAXIA_BAMBOO_HOE.get());
                        output.accept(ModItems.HUAXIA_BRONZE_SICKLE.get());
                        output.accept(ModItems.HUAXIA_IRON_FLAIL.get());
                        output.accept(ModItems.HUAXIA_BAMBOO_CHISEL.get());
                        output.accept(ModItems.HUAXIA_IRON_SAW.get());
                        output.accept(ModItems.HUAXIA_BRONZE_HAMMER.get());
                        output.accept(ModItems.HUAXIA_ABACUS.get());
                        output.accept(ModItems.HUAXIA_BAMBOO_SCROLL.get());
                        output.accept(ModItems.HUAXIA_COMPASS.get());

                        // 华夏武器
                        output.accept(ModItems.HUAXIA_BRONZE_JIAN.get());
                        output.accept(ModItems.HUAXIA_IRON_DAO.get());
                        output.accept(ModItems.HUAXIA_STEEL_JIAN_MASTERWORK.get());
                        output.accept(ModItems.HUAXIA_IRON_QIANG.get());
                        output.accept(ModItems.HUAXIA_STEEL_JI.get());
                        output.accept(ModItems.HUAXIA_SHUANG_DAO.get());
                        output.accept(ModItems.HUAXIA_BIAN_FINE.get());

                        // 华夏食物（选择一些代表性的）
                        output.accept(ModItems.HUAXIA_RICE.get());
                        output.accept(ModItems.HUAXIA_JIAOZI.get());
                        output.accept(ModItems.HUAXIA_MOONCAKE.get());
                        output.accept(ModItems.HUAXIA_GREEN_TEA.get());
                        output.accept(ModItems.HUAXIA_GINSENG_SOUP.get());

                        // 特殊物品
                        output.accept(ModItems.UNIVERSAL_WALLET.get());

                        // TODO: 添加诺曼文化物品
                        // output.accept(ModItems.NORMAN_SWORD.get());
                        // output.accept(ModItems.NORMAN_HELMET.get());

                        // TODO: 添加日本文化物品
                        // output.accept(ModItems.KATANA.get());
                        // output.accept(ModItems.SAKE.get());

                        // TODO: 添加拜占庭文化物品
                        // output.accept(ModItems.BYZANTINE_CROWN.get());
                        // output.accept(ModItems.BYZANTINE_COIN.get());
                    })
                    .build());
    /** 千年村庄其他文化物品栏 */
    // public static final RegistryObject<CreativeModeTab> MILLENAIRE_CULTURE_TAB =
    // CREATIVE_MODE_TABS.register(
    // "millenaire_culture_tab",
    // () -> CreativeModeTab.builder()

    /**
     * 初始化创造模式物品栏注册表
     * 
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CORE, "创造模式物品栏注册表已初始化");
        }
    }
}
