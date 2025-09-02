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
 * 基于原版Millenaire的ItemMillParchment实现
 * 
 * 功能特性：
 * - 存储文档标题和内容
 * - 右键显示文档内容
 * - 支持多页内容
 * - 文化主题的信息展示
 * - NBT数据持久化
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
            tooltip.add(Component.literal("标题: " + title)
                .withStyle(type.getColor()));
        }
        
        tooltip.add(Component.literal("文化: " + culture.getDisplayName())
            .withStyle(culture.getColor()));
        tooltip.add(Component.literal("类型: " + type.getDisplayName())
            .withStyle(type.getColor()));
        
        String[] contents = getContents(stack);
        if (contents.length > 0) {
            tooltip.add(Component.literal("内容条目: " + contents.length)
                .withStyle(ChatFormatting.GRAY));
        }
        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("右键打开羊皮纸界面")
            .withStyle(ChatFormatting.GREEN));
    }
    
    // ================ 数据管理方法 ================
    
    /**
     * 获取标题
     */
    public static String getTitle(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_TITLE)) {
            return tag.getString(NBT_TITLE);
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
            ListTag listTag = tag.getList(NBT_CONTENTS, 8); // 8 = StringTag
            String[] contents = new String[listTag.size()];
            for (int i = 0; i < listTag.size(); i++) {
                contents[i] = listTag.getString(i);
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
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_CULTURE)) {
            return Culture.fromName(tag.getString(NBT_CULTURE));
        }
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
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_TYPE)) {
            return ParchmentType.fromName(tag.getString(NBT_TYPE));
        }
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
        ItemStack stack = new ItemStack(com.jasoncian.millenaire_rewrite.core.ModItems.PARCHMENT_NORMAN_VILLAGER.get());
        setTitle(stack, title);
        setContents(stack, contents);
        setCulture(stack, culture);
        setParchmentType(stack, type);
        return stack;
    }
}
