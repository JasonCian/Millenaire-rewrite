package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * 方块标签数据生成器
 * 
 * 负责生成所有模组方块的标签，用于分组和功能性标识。
 * 遵循千年村庄重写版的标签设计规范，所有方块标签必须通过此类生成。
 * 
 * 功能特性：
 * - 方块材质标签
 * - 工具采掘标签
 * - 文化相关标签
 * - 建筑系统标签
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireBlockTagsProvider extends BlockTagsProvider {

    public MillenaireBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        MillenaireLogger.info(LogCategory.DATAGEN, "正在生成方块标签数据...");

        // TODO: 在这里添加方块标签生成
        // 示例（当方块被实现后取消注释）：

        // 采掘工具标签
        // this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
        // .add(ModBlocks.VILLAGE_STONE.get())
        // .add(ModBlocks.CULTURE_TOTEM.get());

        // this.tag(BlockTags.NEEDS_STONE_TOOL)
        // .add(ModBlocks.CULTURE_TOTEM.get());

        // 为文化系统预留的扩展点
        generateCultureBlockTags(provider);

        // 为建筑系统预留的扩展点
        generateBuildingBlockTags(provider);

        // 为装饰系统预留的扩展点
        generateDecorativeBlockTags(provider);

        MillenaireLogger.info(LogCategory.DATAGEN, "方块标签数据生成完成");
    }

    /**
     * 生成文化相关方块的标签
     * 
     * 包括各种文化特色方块的标签配置
     * 
     * @param provider HolderLookup提供器
     */
    private void generateCultureBlockTags(HolderLookup.Provider provider) {
        // TODO: 实现文化方块的标签
        // 这将在文化系统实现时添加

        // 示例：
        // this.tag(ModTags.Blocks.NORMAN_BLOCKS)
        // .add(ModBlocks.NORMAN_STONE.get())
        // .add(ModBlocks.NORMAN_WOOD.get());
    }

    /**
     * 生成建筑相关方块的标签
     * 
     * 包括各种建筑材料和构件的标签配置
     * 
     * @param provider HolderLookup提供器
     */
    private void generateBuildingBlockTags(HolderLookup.Provider provider) {
        // TODO: 实现建筑方块的标签
        // 这将在建筑系统实现时添加

        // 示例：
        // this.tag(ModTags.Blocks.BUILDING_BLOCKS)
        // .add(ModBlocks.VILLAGE_STONE.get())
        // .add(ModBlocks.REINFORCED_WOOD.get());
    }

    /**
     * 生成装饰性方块的标签
     * 
     * 包括各种装饰方块的标签配置
     * 
     * @param provider HolderLookup提供器
     */
    private void generateDecorativeBlockTags(HolderLookup.Provider provider) {
        // TODO: 实现装饰方块的标签
        // 这将在装饰系统实现时添加

        // 示例：
        // this.tag(ModTags.Blocks.DECORATIVE_BLOCKS)
        // .add(ModBlocks.CULTURE_BANNER.get())
        // .add(ModBlocks.VILLAGE_STATUE.get());
    }
}
