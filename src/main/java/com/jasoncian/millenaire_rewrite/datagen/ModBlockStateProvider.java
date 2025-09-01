package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块状态数据生成器
 * 
 * 负责生成所有方块的blockstates json文件
 * 这比手动创建文件更高效且不容易出错
 */
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MillenaireRewrite.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        MillenaireRewrite.LOGGER.info("Generating block states and models...");

        // Village Stone - 简单的立方体方块
        blockWithItem(ModBlocks.VILLAGE_STONE);

        // TODO: 后续添加更多方块的状态和模型生成
        // 例如装饰方块的多变体支持
    }

    /**
     * 为简单的立方体方块生成状态和模型
     * 这个方法会：
     * 1. 生成blockstates文件
     * 2. 生成方块模型
     * 3. 生成物品模型（引用方块模型）
     */
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
