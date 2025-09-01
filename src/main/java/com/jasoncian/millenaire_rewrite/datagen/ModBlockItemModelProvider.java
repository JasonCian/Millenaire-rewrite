package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModBlocks;
import com.jasoncian.millenaire_rewrite.core.ModBlockItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块物品模型数据生成器 - 1.20.1现代化实现
 * 
 * 专门处理方块物品的模型生成，特别是装饰方块变体
 * 每个装饰方块变体都有独立的纹理和模型
 * 
 * 纹理命名约定：
 * - 核心方块：textures/block/{block_name}.png
 * - 装饰变体：textures/block/{variant_name}.png
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

        // Village Stone - 继承方块模型
        withExistingParent(ModBlocks.VILLAGE_STONE.getId().getPath(),
                modLoc("block/" + ModBlocks.VILLAGE_STONE.getId().getPath()));

        // ================ 石材装饰方块变体物品 ================

        // 金装饰石块 - 高级装饰
        decorativeBlockItem(ModBlockItems.GOLD_ORNAMENT, "decorative_stone_gold_ornament");

        // 烧制砖块 - 基础建筑
        decorativeBlockItem(ModBlockItems.COOKED_BRICK, "decorative_stone_cooked_brick");

        // Galianite方块 - 特殊材料
        decorativeBlockItem(ModBlockItems.GALIANITE_BLOCK, "decorative_stone_galianite_block");

        // ================ 木材装饰方块变体物品 ================

        // 简朴木框架 - 基础诺曼建筑
        decorativeBlockItem(ModBlockItems.PLAIN_TIMBER_FRAME, "decorative_wood_plain_timber_frame");

        // 十字木框架 - 高级诺曼建筑
        decorativeBlockItem(ModBlockItems.CROSS_TIMBER_FRAME, "decorative_wood_cross_timber_frame");

        // 茅草 - 屋顶材料
        decorativeBlockItem(ModBlockItems.THATCH, "decorative_wood_thatch");

        // 养蚕架 - 日式农业建筑
        decorativeBlockItem(ModBlockItems.SERICULTURE, "decorative_wood_sericulture");

        // ================ 土质装饰方块变体物品 ================

        // 土墙 - 基础建筑材料
        decorativeBlockItem(ModBlockItems.DIRT_WALL, "decorative_earth_dirt_wall");

        // 风干砖 - 印度风格建筑
        decorativeBlockItem(ModBlockItems.DRIED_BRICK, "decorative_earth_dried_brick");

        MillenaireRewrite.LOGGER.info("Generated models for {} block items", 10);
    }

    /**
     * 为装饰方块物品生成立方体模型
     * 直接引用对应的方块模型，确保物品显示为立体方块
     * 
     * @param item           装饰方块物品注册对象
     * @param blockModelName 对应的方块模型名称
     */
    private void decorativeBlockItem(RegistryObject<Item> item, String blockModelName) {
        withExistingParent(item.getId().getPath(),
                modLoc("block/" + blockModelName));
    }

    /**
     * 为方块物品生成简单的立方体模型
     * 模型引用对应的方块纹理
     * 
     * @param item        方块物品注册对象
     * @param textureName 纹理文件名（不含.png扩展名）
     */
    private void simpleBlockItem(RegistryObject<Item> item, String textureName) {
        withExistingParent(item.getId().getPath(),
                mcLoc("item/cube_all"))
                .texture("all", modLoc("block/" + textureName));
    }
}
