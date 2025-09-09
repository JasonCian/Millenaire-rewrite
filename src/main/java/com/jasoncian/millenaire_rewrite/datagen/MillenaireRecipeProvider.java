package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;

import java.util.function.Consumer;

/**
 * 配方数据生成器
 * 
 * 负责生成所有模组配方的JSON文件，包括工作台配方、熔炼配方等。
 * 遵循千年村庄重写版的配方设计规范，所有配方必须通过此类生成。
 * 
 * 功能特性：
 * - 有序配方生成（工作台合成）
 * - 无序配方生成（材料组合）
 * - 熔炼配方生成
 * - 文化特色配方
 * - 建筑材料配方
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class MillenaireRecipeProvider extends RecipeProvider {

    public MillenaireRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        MillenaireLogger.info(LogCategory.DATAGEN, "正在生成配方数据...");

        // TODO: 在这里添加配方生成
        // 示例配方（当物品和方块被实现后取消注释）：

        // 文化卷轴配方
        // ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,
        // ModItems.CULTURE_SCROLL.get())
        // .requires(Items.PAPER)
        // .requires(Items.INK_SAC)
        // .requires(Items.FEATHER)
        // .unlockedBy("has_paper", has(Items.PAPER))
        // .save(consumer);

        // 为文化系统预留的扩展点
        generateCultureRecipes(consumer);

        // 为建筑系统预留的扩展点
        generateBuildingRecipes(consumer);

        // 为货币系统预留的扩展点
        generateCurrencyRecipes(consumer);

        // 为工具系统预留的扩展点
        generateToolRecipes(consumer);

        MillenaireLogger.info(LogCategory.DATAGEN, "配方数据生成完成");
    }

    /**
     * 生成文化相关的配方
     * 
     * 包括各种文化特色物品的制作配方：
     * - Norman（诺曼）文化配方
     * - Japanese（日本）文化配方
     * - Byzantine（拜占庭）文化配方
     * - Huaxia（华夏）文化配方
     * 
     * @param consumer 配方消费者
     */
    private void generateCultureRecipes(Consumer<FinishedRecipe> consumer) {

        // ===== 华夏工具制作配方 =====

        // 竹制锄头
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HUAXIA_BAMBOO_HOE.get())
                .pattern("BB ")
                .pattern(" S ")
                .pattern(" S ")
                .define('B', Items.BAMBOO)
                .define('S', Items.STICK)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);

        // 青铜镰刀
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HUAXIA_BRONZE_SICKLE.get())
                .pattern(" C ")
                .pattern("  C")
                .pattern("S  ")
                .define('C', ModItems.HUAXIA_COPPER_COIN.get()) // 临时使用铜钱代表青铜
                .define('S', Items.STICK)
                .unlockedBy("has_copper_coin", has(ModItems.HUAXIA_COPPER_COIN.get()))
                .save(consumer);

        // 算盘
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HUAXIA_ABACUS.get())
                .pattern("BBB")
                .pattern("PPP")
                .pattern("BBB")
                .define('B', Items.BAMBOO)
                .define('P', Items.OAK_BUTTON)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);

        // 竹简
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HUAXIA_BAMBOO_SCROLL.get())
                .pattern("BBB")
                .pattern("BBB")
                .define('B', Items.BAMBOO)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);

        // ===== 华夏食物制作配方 =====

        // 米饭
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUAXIA_RICE.get())
                .requires(Items.WHEAT_SEEDS) // 临时使用小麦种子代表米
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_wheat_seeds", has(Items.WHEAT_SEEDS))
                .save(consumer);

        // 面条
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.HUAXIA_NOODLES.get())
                .pattern("WWW")
                .pattern("   ")
                .pattern("   ")
                .define('W', Items.WHEAT)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(consumer);

        // 包子
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.HUAXIA_BAOZI.get())
                .pattern(" W ")
                .pattern("WPW")
                .pattern(" W ")
                .define('W', Items.WHEAT)
                .define('P', Items.PORKCHOP)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(consumer);

        // 饺子
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.HUAXIA_JIAOZI.get(), 4)
                .pattern("WWW")
                .pattern("WPW")
                .pattern("WWW")
                .define('W', Items.WHEAT)
                .define('P', Items.PORKCHOP)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(consumer);

        // 月饼
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.HUAXIA_MOONCAKE.get())
                .pattern("WEW")
                .pattern("ESE")
                .pattern("WEW")
                .define('W', Items.WHEAT)
                .define('E', Items.EGG)
                .define('S', Items.SUGAR)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(consumer);

        // 汤圆
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUAXIA_TANGYUAN.get(), 3)
                .requires(Items.WHEAT, 2)
                .requires(Items.SUGAR)
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(consumer);

        // 绿茶
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUAXIA_GREEN_TEA.get())
                .requires(Items.FERN) // 临时使用蕨类植物代表茶叶
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_fern", has(Items.FERN))
                .save(consumer);

        // 人参汤（高级药膳）
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.HUAXIA_GINSENG_SOUP.get())
                .pattern(" G ")
                .pattern("GWG")
                .pattern(" B ")
                .define('G', Items.GLOW_BERRIES) // 临时使用发光浆果代表人参
                .define('W', Items.WATER_BUCKET)
                .define('B', Items.BOWL)
                .unlockedBy("has_glow_berries", has(Items.GLOW_BERRIES))
                .save(consumer);

        // TODO: 实现其他文化相关配方
        // 这将在其他文化系统实现时添加

        // 示例 - 文化图腾配方
        // ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
        // ModBlocks.CULTURE_TOTEM.get())
        // .pattern("CSC")
        // .pattern("STS")
        // .pattern("CSC")
        // .define('C', Items.CLAY_BALL)
        // .define('S', Items.STONE)
        // .define('T', Items.TOTEM_OF_UNDYING)
        // .unlockedBy("has_totem", has(Items.TOTEM_OF_UNDYING))
        // .save(consumer);
    }

    /**
     * 生成建筑相关的配方
     * 
     * 包括各种建筑材料和构件的制作配方
     * 
     * @param consumer 配方消费者
     */
    private void generateBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        // TODO: 实现建筑相关配方
        // 这将在建筑系统实现时添加

        // 示例 - 村庄石材配方
        // ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
        // ModBlocks.VILLAGE_STONE.get(), 4)
        // .pattern("SS")
        // .pattern("SS")
        // .define('S', Items.STONE)
        // .unlockedBy("has_stone", has(Items.STONE))
        // .save(consumer);
    }

    /**
     * 生成货币相关的配方
     * 
     * 包括各种货币的制作和兑换配方
     * 
     * @param consumer 配方消费者
     */
    private void generateCurrencyRecipes(Consumer<FinishedRecipe> consumer) {
        // TODO: 实现货币相关配方
        // 这将在货币系统实现时添加

        // 示例 - 德尼尔金币熔炼配方
        // SimpleCookingRecipeBuilder.smelting(
        // Ingredient.of(Items.GOLD_NUGGET),
        // RecipeCategory.MISC,
        // ModItems.DENIER_OR.get(),
        // 0.5F,
        // 200)
        // .unlockedBy("has_gold_nugget", has(Items.GOLD_NUGGET))
        // .save(consumer);
    }

    /**
     * 生成工具相关的配方
     * 
     * 包括各种文化特色工具和武器的制作配方
     * 
     * @param consumer 配方消费者
     */
    private void generateToolRecipes(Consumer<FinishedRecipe> consumer) {
        // TODO: 实现工具相关配方
        // 这将在工具系统实现时添加

        // 示例 - 文化剑配方
        // ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT,
        // ModItems.NORMAN_SWORD.get())
        // .pattern(" I ")
        // .pattern(" I ")
        // .pattern(" S ")
        // .define('I', Items.IRON_INGOT)
        // .define('S', Items.STICK)
        // .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
        // .save(consumer);
    }
}
