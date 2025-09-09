package com.jasoncian.millenaire_rewrite.init;

import com.jasoncian.millenaire_rewrite.block.entity.HuaxiaChestBlockEntity;
import com.jasoncian.millenaire_rewrite.block.entity.HuaxiaTeaTableBlockEntity;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组方块实体注册
 * 
 * 统一管理所有方块实体类型的注册，包括：
 * - 功能性方块实体（存储、工作台等）
 * - 装饰性方块实体（带有特殊功能的装饰品）
 * - 文化特定的方块实体
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class ModBlockEntities {
    
    /** 方块实体注册器 */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModConstants.MOD_ID);
    
    // =============================================================================
    // 华夏文化功能方块实体
    // =============================================================================
    
    /**
     * 华夏箱子方块实体
     * 用于存储物品的功能性方块实体，支持GUI交互
     */
    public static final RegistryObject<BlockEntityType<HuaxiaChestBlockEntity>> HUAXIA_CHEST = 
            BLOCK_ENTITIES.register("huaxia_chest", () ->
                    BlockEntityType.Builder.of(HuaxiaChestBlockEntity::new,
                            ModBlocks.HUAXIA_CHEST.get())
                            .build(null));
    
    /**
     * 华夏茶桌方块实体
     * 用于茶具存储和茶艺功能的特殊方块实体
     */
    public static final RegistryObject<BlockEntityType<HuaxiaTeaTableBlockEntity>> HUAXIA_TEA_TABLE = 
            BLOCK_ENTITIES.register("huaxia_tea_table", () ->
                    BlockEntityType.Builder.of(HuaxiaTeaTableBlockEntity::new,
                            ModBlocks.HUAXIA_TEA_TABLE.get())
                            .build(null));
    
    // =============================================================================
    // 未来扩展预留
    // =============================================================================
    
    // TODO: 华夏织机方块实体 - 用于纺织功能
    // TODO: 华夏炼丹炉方块实体 - 用于炼制物品
    // TODO: 华夏印刷台方块实体 - 用于书籍制作
    // TODO: 其他文化的功能方块实体（诺曼、拜占庭、日本等）
    
    /**
     * 初始化方块实体注册表
     * 
     * @param modEventBus 模组事件总线
     */
    public static void init(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
        com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger.debug(
                com.jasoncian.millenaire_rewrite.common.logging.LogCategory.CORE,
                "Block entities registry initialized");
    }
}
