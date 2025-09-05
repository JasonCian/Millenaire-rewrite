package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeStoneBlock;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeWoodBlock;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeEarthBlock;
import com.jasoncian.millenaire_rewrite.blocks.functional.VillageStoneBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块注册器 - 管理所有Millenaire mod方块的注册
 *
 * 负责注册所有模组方块，按功能分类组织
 * 开始方块系统迁移，从核心的Village Stone开始
 *
 * 功能特性：
 * - 核心功能方块（村庄石等）
 * - 装饰性方块系统
 * - 文化特色建筑方块
 * - 功能性建筑方块
 *
 * @author JasonCian
 * @version 0.1.0-alpha
 */
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            MillenaireRewrite.MOD_ID);

    // ================ 核心功能方块 ================
    
    /**
     * Village Stone - 村庄核心方块
     * 基于legacy BlockVillageStone重新实现
     * 整个mod最重要的方块，标记和管理村庄
     */
    public static final RegistryObject<Block> VILLAGE_STONE = BLOCKS.register("village_stone",
        VillageStoneBlock::new);

    // ================ 装饰方块系统 ================
    
    /**
     * 石材装饰方块
     * 基于legacy BlockDecorativeStone重新实现
     * 包含：金装饰、烧制砖、Galianite方块
     */
    public static final RegistryObject<Block> DECORATIVE_STONE = BLOCKS.register("decorative_stone",
        DecorativeStoneBlock::new);
    
    /**
     * 木材装饰方块  
     * 基于legacy BlockDecorativeWood重新实现
     * 包含：简朴木框架、十字木框架、茅草、养蚕架
     */
    public static final RegistryObject<Block> DECORATIVE_WOOD = BLOCKS.register("decorative_wood",
        DecorativeWoodBlock::new);
    
    /**
     * 土质装饰方块
     * 基于legacy BlockDecorativeEarth重新实现  
     * 包含：土墙、风干砖
     */
    public static final RegistryObject<Block> DECORATIVE_EARTH = BLOCKS.register("decorative_earth",
        DecorativeEarthBlock::new);

    // TODO: 添加路径系统
    // TODO: 添加功能性方块（Mill Chest, Mill Sign等）
    // TODO: 添加方向性装饰方块（楼梯、半砖等）

    /**
     * 注册所有方块到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
