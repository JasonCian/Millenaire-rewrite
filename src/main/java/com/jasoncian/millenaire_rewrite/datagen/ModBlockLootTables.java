package com.jasoncian.millenaire_rewrite.datagen;

// import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

/**
 * 方块战利品表数据生成器
 * 
 * 负责生成方块被破坏时掉落的物品
 * Village Stone根据legacy逻辑不应该掉落任何物品
 * 
 * ⚠️  注意：装饰方块战利品表已弃用，需要为新的统一方块系统添加支持
 */
public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // Village Stone - 不掉落任何物品（参考legacy的quantityDropped返回0）
        // 通过不添加战利品表来实现无掉落
        
        // ================ 装饰方块战利品表 - 已弃用 ================
        // ⚠️  装饰方块战利品表已迁移至统一方块系统
        // 新系统会自动为每个 BasicBuildingMaterial 生成对应的战利品表
        // 所有建筑方块族（基础方块+楼梯+半砖+墙）都会掉落自身
        
        /*
        // 装饰方块 - 掉落自身 - 已弃用
        this.dropSelf(ModBlocks.DECORATIVE_STONE.get());
        this.dropSelf(ModBlocks.DECORATIVE_WOOD.get());
        this.dropSelf(ModBlocks.DECORATIVE_EARTH.get());
        */
        
        // TODO: 为新的统一方块系统添加自动化战利品表生成支持
        // TODO: 通过BuildingBlockRegistry自动生成所有方块族的战利品表
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // 注意：目前使用 ModBlocks 作为主要的方块注册器
        // 已整合了原ModBlocksNew的统一方块系统功能
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
