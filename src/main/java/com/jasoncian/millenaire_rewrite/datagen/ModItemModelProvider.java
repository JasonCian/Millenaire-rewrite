package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;

/**
 * 物品模型数据生成器 - 自动生成所有mod物品的模型文件
 *
 * 自动生成所有模组物品的基础模型文件
 * 减少手动创建模型的工作量，提高开发效率
 *
 * 功能特性：
 * - 简单物品模型生成
 * - 工具物品模型生成
 * - 羊皮纸特殊模型生成
 * - 自定义材质路径支持
 *
 * @author JasonCian
 * @version 0.1.0-alpha
 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MillenaireRewrite.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Regular Item Models: " + MillenaireRewrite.MOD_ID;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return super.run(cache).thenRun(() -> {
            try {
                fixBowModelPredicates();
            } catch (IOException e) {
                throw new RuntimeException("Failed to fix bow model predicates", e);
            }
        });
    }

    private void fixBowModelPredicates() throws IOException {
        // 修复生成的弓模型文件中的predicate格式
        String[] bowItems = {"inuit_bow", "seljuk_bow", "japanese_bow"};
        
        for (String bowItem : bowItems) {
            Path modelFile = output.getOutputFolder()
                    .resolve("assets")
                    .resolve(MillenaireRewrite.MOD_ID)
                    .resolve("models")
                    .resolve("item")
                    .resolve(bowItem + ".json");
            
            if (Files.exists(modelFile)) {
                String content = Files.readString(modelFile, StandardCharsets.UTF_8);
                // 替换 "minecraft:pulling" 为 "pulling"
                content = content.replace("\"minecraft:pulling\"", "\"pulling\"");
                // 替换 "minecraft:pull" 为 "pull"
                content = content.replace("\"minecraft:pull\"", "\"pull\"");
                Files.writeString(modelFile, content, StandardCharsets.UTF_8);
            }
        }
    }

    @Override
    protected void registerModels() {
        // 货币系统
        simpleItem(ModItems.DENIER);
        simpleItem(ModItems.DENIER_OR);
        simpleItem(ModItems.DENIER_ARGENT);

        // 基础材料
        simpleItem(ModItems.SILK);
        simpleItem(ModItems.OBSIDIAN_FLAKE);
        simpleItem(ModItems.UNKNOWN_POWDER);
        simpleItem(ModItems.GALIANITE_DUST);

        // 服装材料
        simpleItem(ModItems.WOOL_CLOTHES);
        simpleItem(ModItems.SILK_CLOTHES);

        // 农作物
        simpleItem(ModItems.TURMERIC);
        simpleItem(ModItems.RICE);
        simpleItem(ModItems.MAIZE);
        simpleItem(ModItems.GRAPES);

        // 诺曼食物
        simpleItem(ModItems.CIDER_APPLE);
        simpleItem(ModItems.CIDER);
        simpleItem(ModItems.CALVA);
        simpleItem(ModItems.TRIPES);
        simpleItem(ModItems.BOUDIN_NOIR);

        // 印度食物
        simpleItem(ModItems.VEG_CURRY);
        simpleItem(ModItems.MURGH_CURRY);
        simpleItem(ModItems.RASGULLA);

        // 玛雅食物
        simpleItem(ModItems.CACAUHAA);
        simpleItem(ModItems.MASA);
        simpleItem(ModItems.WAH);
        simpleItem(ModItems.BALCHE);
        simpleItem(ModItems.SIKILPAH);

        // 日本食物
        simpleItem(ModItems.SAKE);
        simpleItem(ModItems.UDON);
        simpleItem(ModItems.IKAYAKI);
        simpleItem(ModItems.CHERRIES);
        simpleItem(ModItems.CHERRY_BLOSSOM);

        // 拜占庭食物
        simpleItem(ModItems.WINE);
        simpleItem(ModItems.MALVASIA_WINE);
        simpleItem(ModItems.FETA);
        simpleItem(ModItems.SOUVLAKI);

        // 特殊物品
        simpleItem(ModItems.PURSE);
        simpleItem(ModItems.VILLAGE_SIGN);

        // ================ Norman Tools & Weapons ================
        handheldItem(ModItems.NORMAN_SWORD);
        handheldItem(ModItems.NORMAN_AXE);
        handheldItem(ModItems.NORMAN_PICKAXE);
        handheldItem(ModItems.NORMAN_SHOVEL);
        handheldItem(ModItems.NORMAN_HOE);

        // ================ Norman Armor ================
        simpleItem(ModItems.NORMAN_HELMET);
        simpleItem(ModItems.NORMAN_CHESTPLATE);
        simpleItem(ModItems.NORMAN_LEGGINGS);
        simpleItem(ModItems.NORMAN_BOOTS);

        // ================ Mayan Obsidian Tools ================
        handheldItem(ModItems.MAYAN_AXE);
        handheldItem(ModItems.MAYAN_PICKAXE);
        handheldItem(ModItems.MAYAN_SHOVEL);
        handheldItem(ModItems.MAYAN_HOE);
        handheldItem(ModItems.MAYAN_MACE);

        // ================ Byzantine Tools & Weapons ================
        handheldItem(ModItems.BYZANTINE_MACE);
        handheldItem(ModItems.BYZANTINE_PICKAXE);
        handheldItem(ModItems.BYZANTINE_AXE);
        handheldItem(ModItems.BYZANTINE_SHOVEL);
        handheldItem(ModItems.BYZANTINE_HOE);

        // ================ Byzantine Armor ================
        simpleItem(ModItems.BYZANTINE_HELMET);
        simpleItem(ModItems.BYZANTINE_CHESTPLATE);
        simpleItem(ModItems.BYZANTINE_LEGGINGS);
        simpleItem(ModItems.BYZANTINE_BOOTS);

        // ================ Japanese Tools & Weapons ================
        handheldItem(ModItems.JAPANESE_SWORD);
        bowItem(ModItems.JAPANESE_BOW);

        // ================ Japanese Guard Armor ================
        simpleItem(ModItems.JAPANESE_GUARD_HELMET);
        simpleItem(ModItems.JAPANESE_GUARD_CHESTPLATE);
        simpleItem(ModItems.JAPANESE_GUARD_LEGGINGS);
        simpleItem(ModItems.JAPANESE_GUARD_BOOTS);

        // ================ Japanese Blue Samurai Armor ================
        simpleItem(ModItems.JAPANESE_BLUE_HELMET);
        simpleItem(ModItems.JAPANESE_BLUE_CHESTPLATE);
        simpleItem(ModItems.JAPANESE_BLUE_LEGGINGS);
        simpleItem(ModItems.JAPANESE_BLUE_BOOTS);

        // ================ Japanese Red Samurai Armor ================
        simpleItem(ModItems.JAPANESE_RED_HELMET);
        simpleItem(ModItems.JAPANESE_RED_CHESTPLATE);
        simpleItem(ModItems.JAPANESE_RED_LEGGINGS);
        simpleItem(ModItems.JAPANESE_RED_BOOTS);

        // ================ Special Armor ================
        simpleItem(ModItems.MAYAN_QUEST_CROWN);

        // ================ Magic Items - Wands ================
        handheldItem(ModItems.WAND_SUMMONING);
        handheldItem(ModItems.WAND_NEGATION);
        handheldItem(ModItems.WAND_CREATIVE);
        handheldItem(ModItems.TUNING_FORK);

        // ================ Magic Amulets ================
        simpleItem(ModItems.AMULET_SKOLL_HATI);  // 没有overlay纹理，使用simple
        layeredItem(ModItems.AMULET_ALCHEMIST);
        layeredItem(ModItems.AMULET_VISHNU);
        layeredItem(ModItems.AMULET_YGGDRASIL);

        // ================ Decorative Items ================
        simpleItem(ModItems.TAPESTRY);
        simpleItem(ModItems.INDIAN_STATUE);
        simpleItem(ModItems.MAYAN_STATUE);
        simpleItem(ModItems.BYZANTINE_ICON_SMALL);
        simpleItem(ModItems.BYZANTINE_ICON_MEDIUM);
        simpleItem(ModItems.BYZANTINE_ICON_LARGE);

        // ================ 特殊工具和材料 ================
        simpleItem(ModItems.BRICK_MOULD);
        simpleItem(ModItems.OLIVES);
        simpleItem(ModItems.OLIVE_OIL);

        // ================ 因纽特文明 ================
        // 因纽特武器
        handheldItem(ModItems.INUIT_TRIDENT);
        bowItem(ModItems.INUIT_BOW);
        handheldItem(ModItems.ULU);

        // 因纽特盔甲
        simpleItem(ModItems.FUR_HELMET);
        simpleItem(ModItems.FUR_CHESTPLATE);
        simpleItem(ModItems.FUR_LEGGINGS);
        simpleItem(ModItems.FUR_BOOTS);

        // 因纽特食物
        simpleItem(ModItems.BEAR_MEAT_RAW);
        simpleItem(ModItems.BEAR_MEAT_COOKED);
        simpleItem(ModItems.WOLF_MEAT_RAW);
        simpleItem(ModItems.WOLF_MEAT_COOKED);
        simpleItem(ModItems.SEAFOOD_RAW);
        simpleItem(ModItems.SEAFOOD_COOKED);
        simpleItem(ModItems.INUIT_BEAR_STEW);
        simpleItem(ModItems.INUIT_MEATY_STEW);
        simpleItem(ModItems.INUIT_POTATO_STEW);

        // 因纽特材料
        simpleItem(ModItems.TANNED_HIDE);
        simpleItem(ModItems.HIDE_HANGING);

        // ================ 塞尔柱文明 ================
        // 塞尔柱武器
        handheldItem(ModItems.SELJUK_SCIMITAR);
        bowItem(ModItems.SELJUK_BOW);

        // 塞尔柱盔甲
        simpleItem(ModItems.SELJUK_TURBAN);
        simpleItem(ModItems.SELJUK_HELMET);
        simpleItem(ModItems.SELJUK_CHESTPLATE);
        simpleItem(ModItems.SELJUK_LEGGINGS);
        simpleItem(ModItems.SELJUK_BOOTS);

        // 塞尔柱食物
        simpleItem(ModItems.PIDE);
        simpleItem(ModItems.HELVA);
        simpleItem(ModItems.LOKUM);
        simpleItem(ModItems.AYRAN);
        simpleItem(ModItems.YOGURT);
        simpleItem(ModItems.PISTACHIOS);

        // 塞尔柱材料和作物
        simpleItem(ModItems.COTTON);
        simpleItem(ModItems.SELJUK_WOOL_CLOTHES);
        simpleItem(ModItems.SELJUK_COTTON_CLOTHES);

        // 塞尔柱装饰品
        simpleItem(ModItems.WALL_CARPET_SMALL);
        simpleItem(ModItems.WALL_CARPET_MEDIUM);
        simpleItem(ModItems.WALL_CARPET_LARGE);

        // ================ Parchments/Scrolls ================
        // Norman Parchments - 使用对应类型的材质
        parchmentItem(ModItems.PARCHMENT_NORMAN_VILLAGER, "parchmentvillagers");
        parchmentItem(ModItems.PARCHMENT_NORMAN_BUILDING, "parchmentbuildings");
        parchmentItem(ModItems.PARCHMENT_NORMAN_ITEM, "parchmentitems");
        parchmentItem(ModItems.PARCHMENT_NORMAN_ALL, "parchmentall");

        // Byzantine Parchments - 使用对应类型的材质
        parchmentItem(ModItems.PARCHMENT_BYZANTINE_VILLAGER, "parchmentvillagers");
        parchmentItem(ModItems.PARCHMENT_BYZANTINE_BUILDING, "parchmentbuildings");
        parchmentItem(ModItems.PARCHMENT_BYZANTINE_ITEM, "parchmentitems");
        parchmentItem(ModItems.PARCHMENT_BYZANTINE_ALL, "parchmentall");

        // Hindi Parchments - 使用对应类型的材质
        parchmentItem(ModItems.PARCHMENT_HINDI_VILLAGER, "parchmentvillagers");
        parchmentItem(ModItems.PARCHMENT_HINDI_BUILDING, "parchmentbuildings");
        parchmentItem(ModItems.PARCHMENT_HINDI_ITEM, "parchmentitems");
        parchmentItem(ModItems.PARCHMENT_HINDI_ALL, "parchmentall");

        // Mayan Parchments - 使用对应类型的材质
        parchmentItem(ModItems.PARCHMENT_MAYAN_VILLAGER, "parchmentvillagers");
        parchmentItem(ModItems.PARCHMENT_MAYAN_BUILDING, "parchmentbuildings");
        parchmentItem(ModItems.PARCHMENT_MAYAN_ITEM, "parchmentitems");
        parchmentItem(ModItems.PARCHMENT_MAYAN_ALL, "parchmentall");

        // Japanese Parchments - 使用对应类型的材质
        parchmentItem(ModItems.PARCHMENT_JAPANESE_VILLAGER, "parchmentvillagers");
        parchmentItem(ModItems.PARCHMENT_JAPANESE_BUILDING, "parchmentbuildings");
        parchmentItem(ModItems.PARCHMENT_JAPANESE_ITEM, "parchmentitems");
        parchmentItem(ModItems.PARCHMENT_JAPANESE_ALL, "parchmentall");

        // Special Parchments - 特殊羊皮纸使用特定材质
        parchmentItem(ModItems.PARCHMENT_VILLAGE_SCROLL, "parchmentvillagers");
        parchmentItem(ModItems.PARCHMENT_SADHU, "parchmentall");

        // ================ Paint Buckets ================
        simpleItem(ModItems.PAINT_BUCKET_WHITE);
        simpleItem(ModItems.PAINT_BUCKET_ORANGE);
        simpleItem(ModItems.PAINT_BUCKET_MAGENTA);
        simpleItem(ModItems.PAINT_BUCKET_LIGHT_BLUE);
        simpleItem(ModItems.PAINT_BUCKET_YELLOW);
        simpleItem(ModItems.PAINT_BUCKET_LIME);
        simpleItem(ModItems.PAINT_BUCKET_PINK);
        simpleItem(ModItems.PAINT_BUCKET_GRAY);
        simpleItem(ModItems.PAINT_BUCKET_LIGHT_GRAY);
        simpleItem(ModItems.PAINT_BUCKET_CYAN);
        simpleItem(ModItems.PAINT_BUCKET_PURPLE);
        simpleItem(ModItems.PAINT_BUCKET_BLUE);
        simpleItem(ModItems.PAINT_BUCKET_BROWN);
        simpleItem(ModItems.PAINT_BUCKET_GREEN);
        simpleItem(ModItems.PAINT_BUCKET_RED);
        simpleItem(ModItems.PAINT_BUCKET_BLACK);
    }

    /**
     * 创建简单物品模型
     * 使用标准的generated父模型和对应的材质
     */
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID,
                                "item/" + item.getId().getPath()));
    }

    /**
     * 创建手持工具模型
     * 使用handheld父模型，适用于剑、斧头、镐子等工具
     */
    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/handheld")).texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID,
                                "item/" + item.getId().getPath()));
    }

    /**
     * 创建弓类武器模型
     * 使用bow父模型，适用于弓箭类武器
     * 包含完整的拉弓动画状态配置
     */
    private ItemModelBuilder bowItem(RegistryObject<Item> item) {
        String itemName = item.getId().getPath();

        // 先显式创建并保存拉弓状态的子模型
        withExistingParent(itemName + "_pulling_0", mcLoc("item/bow"))
                .texture("layer0", modLoc("item/" + itemName + "_pulling_0"));

        withExistingParent(itemName + "_pulling_1", mcLoc("item/bow"))
                .texture("layer0", modLoc("item/" + itemName + "_pulling_1"));

        withExistingParent(itemName + "_pulling_2", mcLoc("item/bow"))
                .texture("layer0", modLoc("item/" + itemName + "_pulling_2"));

        // 创建基础弓模型并添加拉弓状态的overrides
        return withExistingParent(itemName, mcLoc("item/bow"))
                .texture("layer0", modLoc("item/" + itemName))
                .override()
                .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0f)
                .model(getExistingFile(modLoc("item/" + itemName + "_pulling_0")))
                .end()
                .override()
                .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0f)
                .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65f)
                .model(getExistingFile(modLoc("item/" + itemName + "_pulling_1")))
                .end()
                .override()
                .predicate(ResourceLocation.withDefaultNamespace("pulling"), 1.0f)
                .predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9f)
                .model(getExistingFile(modLoc("item/" + itemName + "_pulling_2")))
                .end();
    }

    /**
     * 创建羊皮纸物品模型
     * 使用指定的材质文件
     */
    private ItemModelBuilder parchmentItem(RegistryObject<Item> item, String textureName) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                        ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "item/" + textureName));
    }

    /**
     * 创建双层纹理物品模型
     * 适用于需要base + overlay的护身符等物品
     */
    private ItemModelBuilder layeredItem(RegistryObject<Item> item) {
        String itemName = item.getId().getPath();
        return withExistingParent(itemName,
                ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "item/" + itemName))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID,
                        "item/" + itemName + "_overlay"));
    }
}
