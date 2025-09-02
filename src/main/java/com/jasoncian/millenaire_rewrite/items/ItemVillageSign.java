package com.jasoncian.millenaire_rewrite.items;

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

import javax.annotation.Nullable;
import java.util.List;

/**
 * 村庄标牌系统 - 用于建立和管理村庄
 * 基于原版Millenaire的村庄建设系统
 * 
 * 功能特性：
 * - 存储村庄类型信息
 * - 存储文化类型
 * - 放置后生成村庄中心
 * - 工具提示显示村庄信息
 */
public class ItemVillageSign extends Item {
    
    /** NBT标签常量 */
    private static final String NBT_VILLAGE_TYPE = "village_type";
    private static final String NBT_CULTURE = "culture";
    private static final String NBT_VILLAGE_NAME = "village_name";
    
    /** 村庄类型枚举 */
    public enum VillageType {
        SMALL("小型村庄", "适合初学者的基础村庄"),
        MEDIUM("中型村庄", "平衡发展的标准村庄"),
        LARGE("大型村庄", "高级玩家的复杂村庄"),
        CAPITAL("首都", "文化的中心和最高级村庄");
        
        private final String displayName;
        private final String description;
        
        VillageType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /** 文化类型枚举 */
    public enum Culture {
        NORMAN("诺曼", "北欧战士文化", ChatFormatting.BLUE),
        BYZANTINE("拜占庭", "东罗马帝国文化", ChatFormatting.DARK_PURPLE),
        INDIAN("印度", "次大陆香料文化", ChatFormatting.GOLD),
        MAYAN("玛雅", "中美洲古文明", ChatFormatting.GREEN),
        JAPANESE("日本", "东方武士文化", ChatFormatting.RED);
        
        private final String displayName;
        private final String description;
        private final ChatFormatting color;
        
        Culture(String displayName, String description, ChatFormatting color) {
            this.displayName = displayName;
            this.description = description;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public ChatFormatting getColor() { return color; }
    }
    
    public ItemVillageSign(Properties properties) {
        super(properties);
    }
    
    /**
     * 右键使用村庄标牌
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // TODO: 打开村庄选择GUI或直接放置
            // 目前先发送消息显示标牌信息
            Culture culture = getCulture(itemStack);
            VillageType villageType = getVillageType(itemStack);
            String villageName = getVillageName(itemStack);
            
            if (culture != null && villageType != null) {
                String displayName = villageName.isEmpty() ? 
                    Component.translatable("item.millenaire_rewrite.village_sign.unnamed").getString() : 
                    villageName;
                
                player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.village_sign.established", 
                        displayName, culture.getDisplayName(), villageType.getDisplayName())
                    .withStyle(ChatFormatting.YELLOW));
                        
                player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.village_sign.place_hint")
                    .withStyle(ChatFormatting.GREEN));
            } else {
                // 空白标牌，可以设置
                player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.village_sign.blank")
                    .withStyle(ChatFormatting.GRAY));
                player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.village_sign.configure_hint")
                    .withStyle(ChatFormatting.YELLOW));
            }
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
    
    /**
     * 物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        
        Culture culture = getCulture(stack);
        VillageType villageType = getVillageType(stack);
        String villageName = getVillageName(stack);
        
        if (culture != null && villageType != null) {
            tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.info").withStyle(ChatFormatting.GRAY));
            
            if (!villageName.isEmpty()) {
                tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.name", villageName)
                    .withStyle(culture.getColor()));
            }
            
            tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.culture", culture.getDisplayName())
                .withStyle(culture.getColor()));
            tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.type", villageType.getDisplayName())
                .withStyle(ChatFormatting.WHITE));
            tooltip.add(Component.literal("  " + villageType.getDescription())
                .withStyle(ChatFormatting.DARK_GRAY));
            
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.place_hint")
                .withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.blank")
                .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.millenaire_rewrite.village_sign.configure_hint")
                .withStyle(ChatFormatting.YELLOW));
        }
    }
    
    // ================ 数据管理方法 ================
    
    /**
     * 获取村庄类型
     */
    public static VillageType getVillageType(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(NBT_VILLAGE_TYPE)) {
            try {
                return VillageType.valueOf(nbt.getString(NBT_VILLAGE_TYPE));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 获取文化类型
     */
    public static Culture getCulture(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(NBT_CULTURE)) {
            try {
                return Culture.valueOf(nbt.getString(NBT_CULTURE));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 获取村庄名称
     */
    public static String getVillageName(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getString(NBT_VILLAGE_NAME);
    }
    
    /**
     * 设置村庄类型
     */
    public static void setVillageType(ItemStack stack, VillageType villageType) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (villageType != null) {
            nbt.putString(NBT_VILLAGE_TYPE, villageType.name());
        } else {
            nbt.remove(NBT_VILLAGE_TYPE);
        }
    }
    
    /**
     * 设置文化类型
     */
    public static void setCulture(ItemStack stack, Culture culture) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (culture != null) {
            nbt.putString(NBT_CULTURE, culture.name());
        } else {
            nbt.remove(NBT_CULTURE);
        }
    }
    
    /**
     * 设置村庄名称
     */
    public static void setVillageName(ItemStack stack, String name) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (name != null && !name.trim().isEmpty()) {
            nbt.putString(NBT_VILLAGE_NAME, name.trim());
        } else {
            nbt.remove(NBT_VILLAGE_NAME);
        }
    }
    
    /**
     * 检查标牌是否已配置
     */
    public static boolean isConfigured(ItemStack stack) {
        return getVillageType(stack) != null && getCulture(stack) != null;
    }
    
    /**
     * 创建预配置的村庄标牌
     */
    public static ItemStack createConfiguredSign(Culture culture, VillageType villageType, String name) {
        ItemStack stack = new ItemStack(com.jasoncian.millenaire_rewrite.core.ModItems.VILLAGE_SIGN.get());
        setCulture(stack, culture);
        setVillageType(stack, villageType);
        if (name != null && !name.trim().isEmpty()) {
            setVillageName(stack, name);
        }
        return stack;
    }
}
