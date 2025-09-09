package com.jasoncian.millenaire_rewrite.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

/**
 * 战利品表数据生成器包装类
 * 
 * 将各种战利品表生成器注册到数据生成系统，遵循千年村庄重写版的战利品设计规范。
 * 
 * 功能特性：
 * - 方块战利品表生成
 * - 实体战利品表生成
 * - 宝箱战利品表生成
 * - 文化特色战利品表
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireLootTableProvider {

    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(MillenaireBlockLootTables::new, LootContextParamSets.BLOCK)
        // TODO: 在实体系统实现时添加实体战利品表
        // , new LootTableProvider.SubProviderEntry(MillenaireEntityLootTables::new,
        // LootContextParamSets.ENTITY)
        // TODO: 在宝箱系统实现时添加宝箱战利品表
        // , new LootTableProvider.SubProviderEntry(MillenaireChestLootTables::new,
        // LootContextParamSets.CHEST)
        ));
    }
}
