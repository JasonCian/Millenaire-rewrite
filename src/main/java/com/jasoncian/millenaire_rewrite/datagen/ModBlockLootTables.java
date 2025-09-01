package com.jasoncian.millenaire_rewrite.datagen;

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
 */
public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // Village Stone - 不掉落任何物品（参考legacy的quantityDropped返回0）
        // 通过不添加战利品表来实现无掉落
        
        // TODO: 后续为其他方块添加适当的战利品表
        // 例如：this.dropSelf(ModBlocks.SOME_BLOCK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
