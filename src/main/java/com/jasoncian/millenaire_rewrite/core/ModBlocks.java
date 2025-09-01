package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.blocks.functional.VillageStoneBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组方块注册器
 * 
 * 负责注册所有Millenaire mod的方块
 * 开始方块系统迁移，从核心的Village Stone开始
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, MillenaireRewrite.MOD_ID);

    // ================ 核心功能方块 ================
    
    /**
     * Village Stone - 村庄核心方块
     * 基于legacy BlockVillageStone重新实现
     * 整个mod最重要的方块，标记和管理村庄
     */
    public static final RegistryObject<Block> VILLAGE_STONE = BLOCKS.register("village_stone",
        VillageStoneBlock::new);

    // TODO: 后续添加装饰方块系统
    // TODO: 添加路径系统
    // TODO: 添加功能性方块

    /**
     * 注册所有方块到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
