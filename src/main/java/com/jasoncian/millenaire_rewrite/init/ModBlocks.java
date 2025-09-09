package com.jasoncian.millenaire_rewrite.init;

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

    // ========== 文化特色方块 ==========

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
    @SuppressWarnings("unused") // 暂时未使用，但为将来的方块注册做准备
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
    @SuppressWarnings("unused") // 暂时未使用，但为将来的方块注册做准备
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
