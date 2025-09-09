package com.jasoncian.millenaire_rewrite.init;

import com.jasoncian.millenaire_rewrite.block.huaxia.decorative.HuaxiaLanternBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.decorative.HuaxiaScreenBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.decorative.HuaxiaStoneLionBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.functional.HuaxiaAlchemyCauldronBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.functional.HuaxiaChestBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.functional.HuaxiaLoomBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.functional.HuaxiaTeaTableBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.materials.HuaxiaGlazedTileBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.materials.HuaxiaGreenBrickBlock;
import com.jasoncian.millenaire_rewrite.block.huaxia.materials.HuaxiaRedWallBlock;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 模组方块注册表
 * 
 * 负责注册所有模组方块，包括：
 * - 文化特色方块（不同文化的建筑材料）
 * - 功能性方块（工作台、熔炉等）
 * - 装饰性方块（雕像、图腾等）
 * - 村庄专用方块（村庄中心等）
 * 
 * 使用DeferredRegister进行现代化注册，确保线程安全。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class ModBlocks {

    // 防止实例化工具类
    private ModBlocks() {
        throw new UnsupportedOperationException("方块注册表不能被实例化");
    }

    /** 方块延迟注册器 */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            ModConstants.MOD_ID);

    // ========== 华夏文化方块 ==========
    
    // === 华夏建筑材料 ===
    /** 华夏青砖 - 传统建筑的主要墙体材料 */
    public static final RegistryObject<Block> HUAXIA_GREEN_BRICK = 
        registerBlock("huaxia_green_brick", HuaxiaGreenBrickBlock::new);
    
    /** 华夏红墙 - 宫殿和重要建筑的专用墙体 */
    public static final RegistryObject<Block> HUAXIA_RED_WALL = 
        registerBlock("huaxia_red_wall", HuaxiaRedWallBlock::new);
    
    /** 华夏琉璃瓦 - 高级屋顶建材 */
    public static final RegistryObject<Block> HUAXIA_GLAZED_TILE = 
        registerBlock("huaxia_glazed_tile", HuaxiaGlazedTileBlock::new);
    
    // === 华夏装饰方块 ===
    /** 华夏灯笼 - 传统照明装饰 */
    public static final RegistryObject<Block> HUAXIA_LANTERN = 
        registerBlock("huaxia_lantern", HuaxiaLanternBlock::new);
    
    /** 华夏屏风 - 室内隔断装饰 */
    public static final RegistryObject<Block> HUAXIA_SCREEN = 
        registerBlock("huaxia_screen", HuaxiaScreenBlock::new);
    
    /** 华夏石狮 - 镇宅神兽装饰 */
    public static final RegistryObject<Block> HUAXIA_STONE_LION = 
        registerBlock("huaxia_stone_lion", HuaxiaStoneLionBlock::new);
    
    // === 华夏功能方块 ===
    /** 华夏箱子 - 存储容器 */
    public static final RegistryObject<Block> HUAXIA_CHEST = 
        registerBlock("huaxia_chest", HuaxiaChestBlock::new);
    
    /** 华夏茶桌 - 品茶社交功能 */
    public static final RegistryObject<Block> HUAXIA_TEA_TABLE = 
        registerBlock("huaxia_tea_table", HuaxiaTeaTableBlock::new);
    
    /** 华夏药鼎 - 炼药设备 */
    public static final RegistryObject<Block> HUAXIA_ALCHEMY_CAULDRON = 
        registerBlock("huaxia_alchemy_cauldron", HuaxiaAlchemyCauldronBlock::new);
    
    /** 华夏织机 - 纺织设备 */
    public static final RegistryObject<Block> HUAXIA_LOOM = 
        registerBlock("huaxia_loom", HuaxiaLoomBlock::new);

    // ========== 其他文化方块（待实现） ==========

    // TODO: 注册诺曼文化方块
    // public static final RegistryObject<Block> NORMAN_STONE =
    // registerBlock("norman_stone",
    // () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // TODO: 注册日本文化方块
    // public static final RegistryObject<Block> JAPANESE_WOOD =
    // registerBlock("japanese_wood",
    // () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    // TODO: 注册拜占庭文化方块
    // public static final RegistryObject<Block> BYZANTINE_MARBLE =
    // registerBlock("byzantine_marble",
    // () -> new Block(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK)));

    // ========== 功能性方块 ==========

    // TODO: 注册文化工作台
    // public static final RegistryObject<Block> CULTURE_WORKBENCH =
    // registerBlock("culture_workbench",
    // () -> new
    // CultureWorkbenchBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));

    // ========== 装饰性方块 ==========

    // TODO: 注册文化图腾
    // public static final RegistryObject<Block> CULTURE_TOTEM =
    // registerBlock("culture_totem",
    // () -> new CultureTotemBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    // ========== 村庄专用方块 ==========

    // TODO: 注册村庄中心方块
    // public static final RegistryObject<Block> VILLAGE_CENTER =
    // registerBlock("village_center",
    // () -> new VillageCenterBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    /**
     * 注册方块并自动创建对应的物品
     * 
     * @param name  方块注册名称
     * @param block 方块提供器
     * @return 注册的方块对象
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    /**
     * 为方块注册对应的物品
     * 
     * @param name  物品注册名称
     * @param block 方块注册对象
     * @return 注册的方块物品对象
     */
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /**
     * 初始化方块注册表
     * 
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CORE, "方块注册表已初始化");
        }
    }
}
