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

/**
 * 物品模型数据生成器
 * 
 * 自动生成所有mod物品的基础模型文件
 * 减少手动创建模型的工作量
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
        
        // 日本食物
        simpleItem(ModItems.SAKE);
        simpleItem(ModItems.UDON);
        simpleItem(ModItems.IKAYAKI);
        
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

        // ================ Magic Items - Amulets ================
        layeredItem(ModItems.AMULET_ALCHEMIST);  // 使用双层纹理系统
        layeredItem(ModItems.AMULET_VISHNU);     // 使用双层纹理系统
        layeredItem(ModItems.AMULET_YGGDRASIL);  // 使用双层纹理系统
        simpleItem(ModItems.AMULET_SKOLL_HATI);  // 功能性物品，不需要overlay
        
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
    }
    
    /**
     * 创建简单物品模型
     * 使用标准的generated父模型和对应的材质
     */
    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "item/" + item.getId().getPath()));
    }
    
    /**
     * 创建手持工具模型
     * 使用handheld父模型，适用于剑、斧头、镐子等工具
     */
    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "item/" + item.getId().getPath()));
    }
    
    /**
     * 创建弓类武器模型
     * 使用bow父模型，适用于弓箭类武器
     */
    private ItemModelBuilder bowItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/bow")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "item/" + item.getId().getPath()));
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
                .texture("layer1", ResourceLocation.fromNamespaceAndPath(MillenaireRewrite.MOD_ID, "item/" + itemName + "_overlay"));
    }
}
