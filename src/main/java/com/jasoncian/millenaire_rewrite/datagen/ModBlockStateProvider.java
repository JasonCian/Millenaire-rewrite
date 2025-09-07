package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
// import com.jasoncian.millenaire_rewrite.blocks.decorative.*;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
// import net.minecraftforge.client.model.generators.ConfiguredModel;
// import net.minecraftforge.client.model.generators.ModelFile;
// import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * 方块状态数据生成器
 * 
 * 负责生成所有方块的blockstates json文件
 * 这比手动创建文件更高效且不容易出错
 * 
 * ⚠️  注意：装饰方块的数据生成已弃用，需要为新的统一方块系统添加支持
 */
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MillenaireRewrite.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        MillenaireRewrite.LOGGER.info("Generating block states and models...");

        // TODO: 添加真正需要的方块的状态和模型

        // ⚠️  装饰方块数据生成已弃用 - 装饰方块已迁移至ModBlocks的统一方块系统
        // decorativeStoneBlock();
        // decorativeWoodBlock();
        // decorativeEarthBlock();
        
        // TODO: 为新的统一方块系统添加数据生成支持
        // TODO: 通过BuildingBlockRegistry自动生成所有方块族的模型和状态

        MillenaireRewrite.LOGGER.info("Generated block states for {} blocks", 1); // 只有Village Stone了
    }

    /**
     * 生成石材装饰方块的状态和模型 - 已弃用
     * 
     * @deprecated 装饰方块已迁移至统一方块系统，需要为新系统编写对应的数据生成逻辑
     */
    /*
    private void decorativeStoneBlock() {
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.DECORATIVE_STONE.get());

        for (StoneDecorativeVariant variant : StoneDecorativeVariant.values()) {
            String modelName = "decorative_stone_" + variant.getSerializedName();
            String texturePath = "block/" + variant.getSerializedName();

            ModelFile model = models().cubeAll(modelName, modLoc(texturePath));

            builder.partialState()
                    .with(DecorativeStoneBlock.VARIANT, variant)
                    .setModels(ConfiguredModel.builder()
                            .modelFile(model)
                            .build());
        }
    }
    */

    /**
     * 生成木材装饰方块的状态和模型 - 已弃用
     * 
     * @deprecated 装饰方块已迁移至统一方块系统，需要为新系统编写对应的数据生成逻辑
     */
    /*
    private void decorativeWoodBlock() {
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.DECORATIVE_WOOD.get());

        for (WoodDecorativeVariant variant : WoodDecorativeVariant.values()) {
            String modelName = "decorative_wood_" + variant.getSerializedName();
            String texturePath = "block/" + variant.getSerializedName();

            ModelFile model = models().cubeAll(modelName, modLoc(texturePath));

            builder.partialState()
                    .with(DecorativeWoodBlock.VARIANT, variant)
                    .setModels(ConfiguredModel.builder()
                            .modelFile(model)
                            .build());
        }
    }
    */

    /**
     * 生成土质装饰方块的状态和模型 - 已弃用
     * 
     * @deprecated 装饰方块已迁移至统一方块系统，需要为新系统编写对应的数据生成逻辑
     */
    /*
    private void decorativeEarthBlock() {
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.DECORATIVE_EARTH.get());

        for (EarthDecorativeVariant variant : EarthDecorativeVariant.values()) {
            String modelName = "decorative_earth_" + variant.getSerializedName();
            String texturePath = "block/" + variant.getSerializedName();

            ModelFile model = models().cubeAll(modelName, modLoc(texturePath));

            builder.partialState()
                    .with(DecorativeEarthBlock.VARIANT, variant)
                    .setModels(ConfiguredModel.builder()
                            .modelFile(model)
                            .build());
        }
    }
    */

    /**
     * 为简单的立方体方块生成状态和模型
     * 这个方法会：
     * 1. 生成blockstates文件
     * 2. 生成方块模型
     * 3. 生成物品模型（引用方块模型）
     */
    
    // TODO: 为新的统一方块系统添加自动化数据生成支持
}
