package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.init.ModBlocks;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * 方块状态数据生成器
 * 
 * 负责生成所有方块的blockstates JSON文件，这比手动创建文件更高效且不容易出错。
 * 遵循千年村庄重写版的数据生成规范，所有方块状态必须通过此类生成。
 * 
 * 功能特性:
 * - 自动生成方块状态JSON文件
 * - 支持简单立方体方块
 * - 支持带方向的方块
 * - 支持多变体方块
 * - 为文化系统预留扩展接口
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireBlockStateProvider extends BlockStateProvider {

    public MillenaireBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ModConstants.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        MillenaireLogger.info(LogCategory.DATAGEN, "正在生成方块状态和模型数据...");

        // TODO: 在这里添加方块状态和模型生成
        // 示例：
        // simpleBlockWithItem(ModBlocks.VILLAGE_STONE.get(),
        // cubeAll(ModBlocks.VILLAGE_STONE.get()));

        // 为文化系统预留的扩展点
        generateCultureBlocks();

        // 为建筑系统预留的扩展点
        generateBuildingBlocks();

        // 为装饰系统预留的扩展点
        generateDecorativeBlocks();

        MillenaireLogger.info(LogCategory.DATAGEN, "方块状态和模型数据生成完成");
    }

    /**
     * 生成文化相关方块的状态和模型
     * 
     * 此方法为各种文化体系的特色方块生成数据，包括：
     * - Norman（诺曼）文化方块
     * - Japanese（日本）文化方块
     * - Byzantine（拜占庭）文化方块
     * - Huaxia（华夏）文化方块
     */
    private void generateCultureBlocks() {
        // ===== 华夏文化方块 =====
        
        // 华夏建筑材料 - 简单立方体方块
        simpleBlockWithItem(ModBlocks.HUAXIA_GREEN_BRICK.get(),
                cubeAll(ModBlocks.HUAXIA_GREEN_BRICK.get()));
        simpleBlockWithItem(ModBlocks.HUAXIA_RED_WALL.get(),
                cubeAll(ModBlocks.HUAXIA_RED_WALL.get()));
        simpleBlockWithItem(ModBlocks.HUAXIA_GLAZED_TILE.get(),
                cubeAll(ModBlocks.HUAXIA_GLAZED_TILE.get()));
        
        // 华夏装饰方块 - 需要自定义模型的复杂方块
        generateHuaxiaLantern();
        generateHuaxiaScreen();
        generateHuaxiaStoneLion();
        
        // 华夏功能方块 - 需要自定义模型的复杂方块
        generateHuaxiaChest();
        generateHuaxiaTeaTable();
        generateHuaxiaAlchemyCauldron();
        generateHuaxiaLoom();
        
        // TODO: 实现其他文化方块的状态和模型生成
    }

    /**
     * 生成建筑相关方块的状态和模型
     * 
     * 包括各种建筑构件、功能性方块等
     */
    private void generateBuildingBlocks() {
        // TODO: 实现建筑方块的状态和模型生成
        // 这将在建筑系统实现时添加
    }

    /**
     * 生成装饰性方块的状态和模型
     * 
     * 包括各种装饰物品、雕像、旗帜等
     */
    private void generateDecorativeBlocks() {
        // TODO: 实现装饰方块的状态和模型生成
        // 这将在装饰系统实现时添加
    }

    /**
     * 为简单的立方体方块生成状态和模型
     * 这个工具方法会：
     * 1. 生成blockstates文件
     * 2. 生成方块模型
     * 3. 生成物品模型（引用方块模型）
     */
    // TODO: 添加更多工具方法来支持复杂的方块状态生成
    
    /**
     * 生成华夏灯笼的方块状态和模型
     */
    private void generateHuaxiaLantern() {
        // 华夏灯笼有特殊的悬挂形状，需要自定义模型
        var lanternModel = models().getBuilder("huaxia_lantern")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", blockTexture(ModBlocks.HUAXIA_LANTERN.get()))
                .texture("lantern", blockTexture(ModBlocks.HUAXIA_LANTERN.get()))
                .element()
                    .from(4, 2, 4).to(12, 14, 12)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#lantern"))
                .end()
                .element()
                    .from(6, 14, 6).to(10, 16, 10)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#lantern"))
                .end();
                
        horizontalBlock(ModBlocks.HUAXIA_LANTERN.get(), lanternModel);
        
        // 物品模型单独生成
        itemModels().getBuilder("huaxia_lantern")
                .parent(models().getExistingFile(modLoc("block/huaxia_lantern")));
    }
    
    /**
     * 生成华夏屏风的方块状态和模型
     */
    private void generateHuaxiaScreen() {
        // 华夏屏风是薄薄的垂直板状
        var screenModel = models().getBuilder("huaxia_screen")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", blockTexture(ModBlocks.HUAXIA_SCREEN.get()))
                .texture("screen", blockTexture(ModBlocks.HUAXIA_SCREEN.get()))
                .element()
                    .from(0, 0, 6).to(16, 16, 10)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#screen"))
                .end();
                
        horizontalBlock(ModBlocks.HUAXIA_SCREEN.get(), screenModel);
        
        // 物品模型单独生成
        itemModels().getBuilder("huaxia_screen")
                .parent(models().getExistingFile(modLoc("block/huaxia_screen")));
    }
    
    /**
     * 生成华夏石狮的方块状态和模型
     */
    private void generateHuaxiaStoneLion() {
        // 华夏石狮有底座和狮子雕像
        var lionModel = models().getBuilder("huaxia_stone_lion")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", blockTexture(ModBlocks.HUAXIA_STONE_LION.get()))
                .texture("stone", blockTexture(ModBlocks.HUAXIA_STONE_LION.get()))
                .element() // 底座
                    .from(2, 0, 2).to(14, 4, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#stone"))
                .end()
                .element() // 狮子身体
                    .from(4, 4, 4).to(12, 12, 12)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#stone"))
                .end()
                .element() // 狮子头部
                    .from(5, 8, 2).to(11, 14, 6)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#stone"))
                .end();
                
        horizontalBlock(ModBlocks.HUAXIA_STONE_LION.get(), lionModel);
        
        // 物品模型单独生成
        itemModels().getBuilder("huaxia_stone_lion")
                .parent(models().getExistingFile(modLoc("block/huaxia_stone_lion")));
    }
    
    /**
     * 生成华夏箱子的方块状态和模型
     */
    private void generateHuaxiaChest() {
        // 华夏箱子作为BlockEntity，使用简单的方块状态
        // 实际的箱子模型由BlockEntityRenderer处理
        simpleBlock(ModBlocks.HUAXIA_CHEST.get(), 
            models().cubeAll("huaxia_chest", blockTexture(ModBlocks.HUAXIA_CHEST.get())));
        
        // 物品模型使用方块纹理
        itemModels().withExistingParent("huaxia_chest", modLoc("block/huaxia_chest"));
    }
    
    /**
     * 生成华夏茶桌的方块状态和模型
     */
    private void generateHuaxiaTeaTable() {
        // 华夏茶桌有桌面和四个桌腿
        var tableModel = models().getBuilder("huaxia_tea_table")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", blockTexture(ModBlocks.HUAXIA_TEA_TABLE.get()))
                .texture("wood", blockTexture(ModBlocks.HUAXIA_TEA_TABLE.get()))
                .element() // 桌面
                    .from(1, 8, 1).to(15, 10, 15)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 左前腿
                    .from(2, 0, 2).to(4, 8, 4)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 右前腿
                    .from(12, 0, 2).to(14, 8, 4)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 左后腿
                    .from(2, 0, 12).to(4, 8, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 右后腿
                    .from(12, 0, 12).to(14, 8, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end();
                
        horizontalBlock(ModBlocks.HUAXIA_TEA_TABLE.get(), tableModel);
        
        // 物品模型单独生成
        itemModels().getBuilder("huaxia_tea_table")
                .parent(models().getExistingFile(modLoc("block/huaxia_tea_table")));
    }
    
    /**
     * 生成华夏药鼎的方块状态和模型
     */
    private void generateHuaxiaAlchemyCauldron() {
        // 华夏药鼎有圆形鼎身和三足
        var cauldronModel = models().getBuilder("huaxia_alchemy_cauldron")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", blockTexture(ModBlocks.HUAXIA_ALCHEMY_CAULDRON.get()))
                .texture("metal", blockTexture(ModBlocks.HUAXIA_ALCHEMY_CAULDRON.get()))
                .element() // 鼎身主体
                    .from(3, 4, 3).to(13, 12, 13)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#metal"))
                .end()
                .element() // 左前足
                    .from(2, 0, 2).to(5, 4, 5)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#metal"))
                .end()
                .element() // 右前足
                    .from(11, 0, 2).to(14, 4, 5)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#metal"))
                .end()
                .element() // 后足
                    .from(6.5f, 0, 11).to(9.5f, 4, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#metal"))
                .end()
                .element() // 左耳
                    .from(1, 8, 7).to(3, 10, 9)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#metal"))
                .end()
                .element() // 右耳
                    .from(13, 8, 7).to(15, 10, 9)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#metal"))
                .end();
                
        horizontalBlock(ModBlocks.HUAXIA_ALCHEMY_CAULDRON.get(), cauldronModel);
        
        // 物品模型单独生成
        itemModels().getBuilder("huaxia_alchemy_cauldron")
                .parent(models().getExistingFile(modLoc("block/huaxia_alchemy_cauldron")));
    }
    
    /**
     * 生成华夏织机的方块状态和模型
     */
    private void generateHuaxiaLoom() {
        // 华夏织机有复杂的框架结构
        var loomModel = models().getBuilder("huaxia_loom")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", blockTexture(ModBlocks.HUAXIA_LOOM.get()))
                .texture("wood", blockTexture(ModBlocks.HUAXIA_LOOM.get()))
                .element() // 底座框架
                    .from(1, 0, 1).to(15, 2, 15)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 左前柱
                    .from(2, 2, 2).to(4, 14, 4)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 右前柱
                    .from(12, 2, 2).to(14, 14, 4)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 左后柱
                    .from(2, 2, 12).to(4, 14, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 右后柱
                    .from(12, 2, 12).to(14, 14, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 前横梁
                    .from(2, 12, 2).to(14, 14, 4)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 后横梁
                    .from(2, 12, 12).to(14, 14, 14)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end()
                .element() // 编织台面
                    .from(3, 4, 5).to(13, 6, 11)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#wood"))
                .end();
                
        horizontalBlock(ModBlocks.HUAXIA_LOOM.get(), loomModel);
        
        // 物品模型单独生成
        itemModels().getBuilder("huaxia_loom")
                .parent(models().getExistingFile(modLoc("block/huaxia_loom")));
    }
}
