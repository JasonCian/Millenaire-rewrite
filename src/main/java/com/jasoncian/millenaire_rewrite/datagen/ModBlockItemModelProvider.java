package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
// import com.jasoncian.millenaire_rewrite.core.ModBlockItems;
import net.minecraft.data.PackOutput;
// import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
// import net.minecraftforge.registries.RegistryObject;

/**
 * 方块物品模型数据生成器
 * 
 * 为方块物品生成对应的.json模型文件
 * 这些模型决定物品在物品栏和手中的显示效果
 * 
 * ⚠️  注意：装饰方块物品模型生成已弃用，需要为新的统一方块系统添加支持
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

        // ================ 核心功能方块 ================

        // TODO: 添加真正需要的方块物品模型

        // ================ 装饰方块变体物品 - 已弃用 ================
        // ⚠️  装饰方块物品模型生成已弃用，已迁移至统一方块系统
        // 新系统会自动处理所有方块族的物品模型生成
        
        /*
        // ================ 石材装饰方块变体物品 ================

        // 金装饰石块 - 高级装饰 - 已弃用
        decorativeBlockItem(ModBlockItems.GOLD_ORNAMENT, "decorative_stone_gold_ornament");

        // 烧制砖块 - 基础建筑 - 已弃用
        decorativeBlockItem(ModBlockItems.COOKED_BRICK, "decorative_stone_cooked_brick");

        // Galianite方块 - 特殊魔法材料 - 已弃用
        decorativeBlockItem(ModBlockItems.GALIANITE_BLOCK, "decorative_stone_galianite_block");

        // ================ 木材装饰方块变体物品 ================

        // 简朴木框架 - 基础诺曼建筑 - 已弃用
        decorativeBlockItem(ModBlockItems.PLAIN_TIMBER_FRAME, "decorative_wood_plain_timber_frame");

        // 十字木框架 - 高级诺曼建筑 - 已弃用
        decorativeBlockItem(ModBlockItems.CROSS_TIMBER_FRAME, "decorative_wood_cross_timber_frame");

        // 茅草 - 屋顶材料 - 已弃用
        decorativeBlockItem(ModBlockItems.THATCH, "decorative_wood_thatch");

        // 养蚕架 - 日式农业建筑 - 已弃用
        decorativeBlockItem(ModBlockItems.SERICULTURE, "decorative_wood_sericulture");

        // ================ 土质装饰方块变体物品 ================

        // 土墙 - 基础建筑材料 - 已弃用
        decorativeBlockItem(ModBlockItems.DIRT_WALL, "decorative_earth_dirt_wall");

        // 风干砖 - 印度风格建筑 - 已弃用
        decorativeBlockItem(ModBlockItems.DRIED_BRICK, "decorative_earth_dried_brick");
        */

        // TODO: 为新的统一方块系统添加自动化模型生成支持
        // TODO: 通过BuildingBlockRegistry自动生成所有方块族的物品模型

        MillenaireRewrite.LOGGER.info("Generated block item models for {} items", 1); // 只有Village Stone了
    }

    /**
     * 为装饰方块物品生成立方体模型 - 已弃用
     * 
     * @deprecated 装饰方块已迁移至统一方块系统，需要为新系统编写对应的数据生成逻辑
     */
    /*
    private void decorativeBlockItem(RegistryObject<Item> item, String blockModelName) {
        withExistingParent(item.getId().getPath(),
                modLoc("block/" + blockModelName));
    }
    */
}
