package com.jasoncian.millenaire_rewrite.items;

import com.jasoncian.millenaire_rewrite.client.gui.ParchmentScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 羊皮纸/卷轴系统 - 显示文档和指南信息
 * 
 * 功能列表:
 * - 存储文档标题和内容
 * - 右键显示文档内容
 * - 支持多页内容系统
 * - NBT数据持久化
 * - 客户端GUI界面显示
 * - 工具提示信息展示
 * 
 * @author JasonCian
 * @version 0.1.3-alpha
 * @since 1.20.1
 */
public class ItemMillParchment extends Item {
    
    /** NBT标签常量 */
    private static final String NBT_TITLE = "parchment_title";
    private static final String NBT_CONTENTS = "parchment_contents";
    private static final String NBT_CULTURE = "parchment_culture";
    private static final String NBT_TYPE = "parchment_type";
    
    /** 羊皮纸类型枚举 */
    public enum ParchmentType {
        VILLAGER("villager", "item.millenaire_rewrite.parchment.type.villager", ChatFormatting.GREEN),
        BUILDING("building", "item.millenaire_rewrite.parchment.type.building", ChatFormatting.BLUE),
        ITEM("item", "item.millenaire_rewrite.parchment.type.item", ChatFormatting.YELLOW),
        ALL("all", "item.millenaire_rewrite.parchment.type.all", ChatFormatting.GOLD);
        
        private final String name;
        private final String translationKey;
        private final ChatFormatting color;
        
        ParchmentType(String name, String translationKey, ChatFormatting color) {
            this.name = name;
            this.translationKey = translationKey;
            this.color = color;
        }
        
        public String getName() { return name; }
        public String getTranslationKey() { return translationKey; }
        public String getDisplayName() { 
            return Component.translatable(translationKey).getString(); 
        }
        public ChatFormatting getColor() { return color; }
        
        public static ParchmentType fromName(String name) {
            for (ParchmentType type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return VILLAGER;
        }
    }
    
    /** 文化类型枚举 */
    public enum Culture {
        NORMAN("norman", "item.millenaire_rewrite.parchment.culture.norman", ChatFormatting.BLUE),
        BYZANTINE("byzantine", "item.millenaire_rewrite.parchment.culture.byzantine", ChatFormatting.DARK_PURPLE),
        HINDI("hindi", "item.millenaire_rewrite.parchment.culture.hindi", ChatFormatting.GOLD),
        MAYAN("mayan", "item.millenaire_rewrite.parchment.culture.mayan", ChatFormatting.GREEN),
        JAPANESE("japanese", "item.millenaire_rewrite.parchment.culture.japanese", ChatFormatting.RED);
        
        private final String name;
        private final String translationKey;
        private final ChatFormatting color;
        
        Culture(String name, String translationKey, ChatFormatting color) {
            this.name = name;
            this.translationKey = translationKey;
            this.color = color;
        }
        
        public String getName() { return name; }
        public String getTranslationKey() { return translationKey; }
        public String getDisplayName() { 
            return Component.translatable(translationKey).getString(); 
        }
        public ChatFormatting getColor() { return color; }
        
        public static Culture fromName(String name) {
            for (Culture culture : values()) {
                if (culture.name.equals(name)) {
                    return culture;
                }
            }
            return NORMAN;
        }
    }
    
    public ItemMillParchment(Properties properties) {
        super(properties);
    }
    
    /**
     * 右键使用羊皮纸
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (level.isClientSide) {
            // 客户端打开GUI
            openParchmentGui(itemStack);
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
    
    /**
     * 打开羊皮纸GUI（仅客户端）
     */
    @OnlyIn(Dist.CLIENT)
    private void openParchmentGui(ItemStack stack) {
        Minecraft.getInstance().setScreen(new ParchmentScreen(stack));
    }
    
    /**
     * 物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        String title = getTitle(stack);
        Culture culture = getCulture(stack);
        ParchmentType type = getParchmentType(stack);

        if (!title.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.millenaire_rewrite.parchment.title", title)
                    .withStyle(type.getColor()));
        }

        // 分别创建文化名称组件和完整提示组件
        Component cultureName = Component.translatable(culture.getTranslationKey());
        tooltip.add(Component.translatable("tooltip.millenaire_rewrite.parchment.culture", cultureName)
                .withStyle(culture.getColor()));

        // 分别创建类型名称组件和完整提示组件
        Component typeName = Component.translatable(type.getTranslationKey());
        tooltip.add(Component.translatable("tooltip.millenaire_rewrite.parchment.type", typeName)
                .withStyle(type.getColor()));

        String[] contents = getContents(stack);
        if (contents.length > 0) {
            tooltip.add(Component.translatable("tooltip.millenaire_rewrite.parchment.entries", contents.length)
                    .withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tooltip.millenaire_rewrite.parchment.use")
                .withStyle(ChatFormatting.GREEN));
    }
    
    // ================ 数据管理方法 ================
    
    /**
     * 获取标题
     */
    public static String getTitle(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_TITLE)) {
            String title = tag.getString(NBT_TITLE);

            // 如果是翻译键格式（包含.），返回翻译后的文本
            if (title.contains(".")) {
                return Component.translatable(title).getString();
            }

            // 如果是旧版硬编码文本，直接返回
            return title;
        }
        return "";
    }
    
    /**
     * 设置标题
     */
    public static void setTitle(ItemStack stack, String title) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_TITLE, title);
    }
    
    /**
     * 获取内容数组
     */
    public static String[] getContents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_CONTENTS)) {
            ListTag listTag = tag.getList(NBT_CONTENTS, 8);
            String[] contents = new String[listTag.size()];

            for (int i = 0; i < listTag.size(); i++) {
                String content = listTag.getString(i);

                // 如果是翻译键格式，返回翻译后的文本
                if (content.contains(".")) {
                    contents[i] = Component.translatable(content).getString();
                } else {
                    contents[i] = content; // 旧版硬编码文本
                }
            }
            return contents;
        }
        return new String[0];
    }
    
    /**
     * 设置内容数组
     */
    public static void setContents(ItemStack stack, String[] contents) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag listTag = new ListTag();
        for (String content : contents) {
            listTag.add(StringTag.valueOf(content));
        }
        tag.put(NBT_CONTENTS, listTag);
    }
    
    /**
     * 获取文化类型
     */
    public static Culture getCulture(ItemStack stack) {
        // 首先尝试从NBT获取
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_CULTURE)) {
            return Culture.fromName(tag.getString(NBT_CULTURE));
        }

        // 如果NBT不存在，从物品ID推断文化
        return Culture.NORMAN;
    }
    
    /**
     * 设置文化类型
     */
    public static void setCulture(ItemStack stack, Culture culture) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_CULTURE, culture.getName());
    }
    
    /**
     * 获取羊皮纸类型
     */
    public static ParchmentType getParchmentType(ItemStack stack) {
        // 首先尝试从NBT获取
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_TYPE)) {
            return ParchmentType.fromName(tag.getString(NBT_TYPE));
        }

        // 如果NBT不存在，从物品ID推断类型
        return ParchmentType.VILLAGER;
    }
    
    /**
     * 设置羊皮纸类型
     */
    public static void setParchmentType(ItemStack stack, ParchmentType type) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_TYPE, type.getName());
    }


    /**
     * 创建预设的羊皮纸
     */
    public static ItemStack createParchment(String title, String[] contents, Culture culture, ParchmentType type) {
        // 根据文化和类型获取对应的物品
        Item correspondingItem = getParchmentItemForCultureAndType(culture, type);
        ItemStack stack = new ItemStack(correspondingItem);

        // 设置NBT数据
        setTitle(stack, title);
        setContents(stack, contents);
        setCulture(stack, culture);
        setParchmentType(stack, type);


        return stack;
    }

    /**
     * 简单的物品选择器 - 根据文化和类型返回对应的注册物品
     */
    private static Item getParchmentItemForCultureAndType(Culture culture, ParchmentType type) {
        String expectedName = "parchment_" + culture.getName() + "_" + type.getName();

        switch (expectedName) {
            case "parchment_norman_villager":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_NORMAN_VILLAGER.get();
            case "parchment_norman_building":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_NORMAN_BUILDING.get();
            case "parchment_norman_item":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_NORMAN_ITEM.get();
            case "parchment_norman_all":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_NORMAN_ALL.get();

            case "parchment_byzantine_villager":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_BYZANTINE_VILLAGER.get();
            case "parchment_byzantine_building":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_BYZANTINE_BUILDING.get();
            case "parchment_byzantine_item":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_BYZANTINE_ITEM.get();
            case "parchment_byzantine_all":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_BYZANTINE_ALL.get();

            case "parchment_hindi_villager":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_HINDI_VILLAGER.get();
            case "parchment_hindi_building":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_HINDI_BUILDING.get();
            case "parchment_hindi_item":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_HINDI_ITEM.get();
            case "parchment_hindi_all":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_HINDI_ALL.get();

            case "parchment_mayan_villager":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_MAYAN_VILLAGER.get();
            case "parchment_mayan_building":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_MAYAN_BUILDING.get();
            case "parchment_mayan_item":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_MAYAN_ITEM.get();
            case "parchment_mayan_all":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_MAYAN_ALL.get();

            case "parchment_japanese_villager":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_JAPANESE_VILLAGER.get();
            case "parchment_japanese_building":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_JAPANESE_BUILDING.get();
            case "parchment_japanese_item":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_JAPANESE_ITEM.get();
            case "parchment_japanese_all":
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_JAPANESE_ALL.get();

            default:
                return com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_NORMAN_VILLAGER.get();
        }
    }
}
