package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * 方块物品模型数据生成器
 * 
 * 负责生成方块对应的物品模型
 * 通常方块物品模型会引用方块模型
 */
public class ModBlockItemModelProvider extends ItemModelProvider {

    public ModBlockItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MillenaireRewrite.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Block Item Models: " + MillenaireRewrite.MOD_ID;
    }

    @Override
    protected void registerModels() {
        MillenaireRewrite.LOGGER.info("Generating block item models...");

        // Village Stone - 继承方块模型
        withExistingParent(ModBlocks.VILLAGE_STONE.getId().getPath(),
                modLoc("block/" + ModBlocks.VILLAGE_STONE.getId().getPath()));

        // TODO: 后续添加更多方块物品模型
    }
}
