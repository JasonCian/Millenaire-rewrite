package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 模组方块注册器
 * 
 * 负责注册所有Millenaire mod的方块
 * 当前阶段暂时只包含最基础的方块，后续扩展
 */
public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, MillenaireRewrite.MOD_ID);

    // TODO: 后续阶段添加方块系统
    // 当前阶段专注于物品系统的完善

    /**
     * 注册所有方块到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
