package com.jasoncian.millenaire_rewrite.item.special;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * 华夏文化特殊物品基类
 * 
 * 华夏文化的特殊物品，包括卷轴、印章、风水罗盘等具有特殊功能的文化物品。
 * 这些物品体现了华夏文化的哲学思想和实用智慧，具有独特的使用效果。
 * 
 * 功能特性：
 * - 不同特殊物品类型和功能效果
 * - 使用冷却时间和耐久度系统
 * - 文化特色的使用动画和音效
 * - 特殊的交互逻辑和效果
 * - 村庄建设和管理辅助功能
 * 
 * 设计原则：
 * - 体现华夏文化的哲学思想和智慧
 * - 平衡实用性和游戏体验
 * - 为村庄系统提供有用的工具
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class HuaxiaSpecialItem extends Item {

    /** 华夏特殊物品类型枚举 */
    public enum HuaxiaSpecialType {
        // === 卷轴类 ===
        /** 建筑图纸卷轴 - 显示建筑蓝图 */
        BLUEPRINT_SCROLL("blueprint_scroll", "建筑图纸", "记录建筑设计的珍贵卷轴", 
                        SpecialCategory.SCROLL, SpecialRarity.UNCOMMON, 
                        UseType.RIGHT_CLICK, 300, true, 64),
        
        /** 贸易记录卷轴 - 显示贸易信息 */
        TRADE_RECORD_SCROLL("trade_record_scroll", "贸易记录", "详细记录贸易往来的商业卷轴", 
                           SpecialCategory.SCROLL, SpecialRarity.COMMON, 
                           UseType.RIGHT_CLICK, 200, true, 32),
        
        /** 村庄地图卷轴 - 显示村庄地图 */
        VILLAGE_MAP_SCROLL("village_map_scroll", "村庄地图", "绘制村庄全貌的珍贵地图", 
                          SpecialCategory.SCROLL, SpecialRarity.RARE, 
                          UseType.RIGHT_CLICK, 500, true, 128),
        
        /** 法术卷轴 - 施展特殊法术 */
        SPELL_SCROLL("spell_scroll", "法术卷轴", "蕴含古代法术的神秘卷轴", 
                    SpecialCategory.SCROLL, SpecialRarity.LEGENDARY, 
                    UseType.RIGHT_CLICK, 1200, false, 1),

        // === 印章类 ===
        /** 村长印章 - 管理权限标识 */
        VILLAGE_CHIEF_SEAL("village_chief_seal", "村长印章", "象征村庄管理权威的官方印章", 
                          SpecialCategory.SEAL, SpecialRarity.RARE, 
                          UseType.RIGHT_CLICK, 0, true, 256),
        
        /** 贸易印章 - 贸易合约认证 */
        TRADE_SEAL("trade_seal", "贸易印章", "用于认证贸易合约的商业印章", 
                  SpecialCategory.SEAL, SpecialRarity.UNCOMMON, 
                  UseType.RIGHT_CLICK, 0, true, 128),
        
        /** 工匠印章 - 工艺品质认证 */
        ARTISAN_SEAL("artisan_seal", "工匠印章", "认证工艺品质量的专业印章", 
                    SpecialCategory.SEAL, SpecialRarity.UNCOMMON, 
                    UseType.RIGHT_CLICK, 0, true, 64),
        
        /** 皇室印章 - 最高权威标识 */
        IMPERIAL_SEAL("imperial_seal", "皇室印章", "代表最高权威的稀世印章", 
                     SpecialCategory.SEAL, SpecialRarity.LEGENDARY, 
                     UseType.RIGHT_CLICK, 0, true, 512),

        // === 风水罗盘类 ===
        /** 基础风水罗盘 - 基本风水检测 */
        BASIC_FENGSHUI_COMPASS("basic_fengshui_compass", "基础风水罗盘", "测量基本风水方位的传统工具", 
                              SpecialCategory.COMPASS, SpecialRarity.COMMON, 
                              UseType.RIGHT_CLICK, 100, true, 32),
        
        /** 精制风水罗盘 - 高级风水分析 */
        ADVANCED_FENGSHUI_COMPASS("advanced_fengshui_compass", "精制风水罗盘", "能够进行详细风水分析的精密仪器", 
                                 SpecialCategory.COMPASS, SpecialRarity.RARE, 
                                 UseType.RIGHT_CLICK, 200, true, 64),
        
        /** 大师风水罗盘 - 完整风水系统 */
        MASTER_FENGSHUI_COMPASS("master_fengshui_compass", "大师风水罗盘", "风水大师使用的顶级罗盘", 
                               SpecialCategory.COMPASS, SpecialRarity.LEGENDARY, 
                               UseType.RIGHT_CLICK, 300, true, 128);

        private final String registryName;
        private final String displayName;
        private final String description;
        private final SpecialCategory category;
        private final SpecialRarity rarity;
        private final UseType useType;
        private final int cooldownTicks;
        private final boolean hasDurability;
        private final int maxDurability;

        HuaxiaSpecialType(String registryName, String displayName, String description, 
                         SpecialCategory category, SpecialRarity rarity, UseType useType,
                         int cooldownTicks, boolean hasDurability, int maxDurability) {
            this.registryName = registryName;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.rarity = rarity;
            this.useType = useType;
            this.cooldownTicks = cooldownTicks;
            this.hasDurability = hasDurability;
            this.maxDurability = maxDurability;
        }

        public String getRegistryName() { return registryName; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public SpecialCategory getCategory() { return category; }
        public SpecialRarity getRarity() { return rarity; }
        public UseType getUseType() { return useType; }
        public int getCooldownTicks() { return cooldownTicks; }
        public boolean hasDurability() { return hasDurability; }
        public int getMaxDurability() { return maxDurability; }
    }

    /** 特殊物品分类 */
    public enum SpecialCategory {
        SCROLL("卷轴", ChatFormatting.YELLOW),
        SEAL("印章", ChatFormatting.RED),
        COMPASS("罗盘", ChatFormatting.AQUA);

        private final String displayName;
        private final ChatFormatting color;

        SpecialCategory(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
    }

    /** 特殊物品稀有度 */
    public enum SpecialRarity {
        COMMON("普通", ChatFormatting.WHITE),
        UNCOMMON("良品", ChatFormatting.GREEN),
        RARE("珍品", ChatFormatting.BLUE),
        LEGENDARY("神品", ChatFormatting.GOLD);

        private final String displayName;
        private final ChatFormatting color;

        SpecialRarity(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
    }

    /** 使用方式 */
    public enum UseType {
        RIGHT_CLICK("右键使用"),
        HOLD_USE("长按使用"),
        BLOCK_INTERACT("方块交互");

        private final String displayName;

        UseType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    private final HuaxiaSpecialType specialType;

    /**
     * 构造华夏特殊物品
     * 
     * @param properties 物品属性
     * @param specialType 特殊物品类型
     */
    public HuaxiaSpecialItem(Properties properties, HuaxiaSpecialType specialType) {
        super(properties.stacksTo(specialType.hasDurability() ? 1 : 16)
                       .durability(specialType.hasDurability() ? specialType.getMaxDurability() : 0));
        this.specialType = specialType;
        
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.CORE, 
                "创建华夏特殊物品: {} ({})", specialType.getDisplayName(), specialType.getRegistryName());
        }
    }

    /**
     * 获取特殊物品类型
     * 
     * @return 特殊物品类型
     */
    public HuaxiaSpecialType getSpecialType() {
        return specialType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // 检查冷却时间
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        // 执行特殊物品效果
        boolean success = executeSpecialEffect(level, player, stack, null);
        
        if (success) {
            // 播放使用音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5F, 
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            
            // 设置冷却时间
            if (specialType.getCooldownTicks() > 0) {
                player.getCooldowns().addCooldown(this, specialType.getCooldownTicks());
            }
            
            // 减少耐久度
            if (specialType.hasDurability() && !player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            }
            
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        
        return InteractionResultHolder.fail(stack);
    }

    /**
     * 执行特殊物品的效果
     * 
     * @param level 世界
     * @param player 玩家
     * @param stack 物品堆
     * @param targetPos 目标位置（可选）
     * @return 是否执行成功
     */
    protected boolean executeSpecialEffect(Level level, Player player, ItemStack stack, @Nullable BlockPos targetPos) {
        if (level.isClientSide) {
            return true; // 客户端总是返回成功，实际逻辑在服务端
        }

        ServerLevel serverLevel = (ServerLevel) level;
        
        switch (specialType) {
            case BLUEPRINT_SCROLL:
                return executeBlueprintScroll(serverLevel, player, stack);
            case TRADE_RECORD_SCROLL:
                return executeTradeRecordScroll(serverLevel, player, stack);
            case VILLAGE_MAP_SCROLL:
                return executeVillageMapScroll(serverLevel, player, stack);
            case SPELL_SCROLL:
                return executeSpellScroll(serverLevel, player, stack);
            case VILLAGE_CHIEF_SEAL:
                return executeVillageChiefSeal(serverLevel, player, stack);
            case TRADE_SEAL:
                return executeTradeSeal(serverLevel, player, stack);
            case ARTISAN_SEAL:
                return executeArtisanSeal(serverLevel, player, stack);
            case IMPERIAL_SEAL:
                return executeImperialSeal(serverLevel, player, stack);
            case BASIC_FENGSHUI_COMPASS:
            case ADVANCED_FENGSHUI_COMPASS:
            case MASTER_FENGSHUI_COMPASS:
                return executeFengshuiCompass(serverLevel, player, stack);
            default:
                return false;
        }
    }

    // === 具体效果实现方法 ===

    private boolean executeBlueprintScroll(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现建筑图纸功能
        player.sendSystemMessage(Component.literal("展示建筑蓝图...").withStyle(ChatFormatting.BLUE));
        return true;
    }

    private boolean executeTradeRecordScroll(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现贸易记录功能
        player.sendSystemMessage(Component.literal("显示贸易记录...").withStyle(ChatFormatting.GREEN));
        return true;
    }

    private boolean executeVillageMapScroll(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现村庄地图功能
        player.sendSystemMessage(Component.literal("展开村庄地图...").withStyle(ChatFormatting.YELLOW));
        return true;
    }

    private boolean executeSpellScroll(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现法术卷轴功能
        player.sendSystemMessage(Component.literal("施展古老法术...").withStyle(ChatFormatting.LIGHT_PURPLE));
        return true;
    }

    private boolean executeVillageChiefSeal(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现村长印章功能
        player.sendSystemMessage(Component.literal("行使村长权威...").withStyle(ChatFormatting.GOLD));
        return true;
    }

    private boolean executeTradeSeal(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现贸易印章功能
        player.sendSystemMessage(Component.literal("认证贸易合约...").withStyle(ChatFormatting.GREEN));
        return true;
    }

    private boolean executeArtisanSeal(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现工匠印章功能
        player.sendSystemMessage(Component.literal("认证工艺品质...").withStyle(ChatFormatting.BLUE));
        return true;
    }

    private boolean executeImperialSeal(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现皇室印章功能
        player.sendSystemMessage(Component.literal("展示皇室权威...").withStyle(ChatFormatting.GOLD));
        return true;
    }

    private boolean executeFengshuiCompass(ServerLevel level, Player player, ItemStack stack) {
        // TODO: 实现风水罗盘功能
        Random random = new Random();
        
        // 简单的风水评分计算（临时实现）
        int fengshuiScore = random.nextInt(100) + 1;
        String evaluation;
        ChatFormatting color;
        
        if (fengshuiScore >= 80) {
            evaluation = "风水极佳";
            color = ChatFormatting.GOLD;
        } else if (fengshuiScore >= 60) {
            evaluation = "风水良好";
            color = ChatFormatting.GREEN;
        } else if (fengshuiScore >= 40) {
            evaluation = "风水一般";
            color = ChatFormatting.YELLOW;
        } else {
            evaluation = "风水不佳";
            color = ChatFormatting.RED;
        }
        
        player.sendSystemMessage(Component.literal("风水评测: ")
                .append(Component.literal(evaluation + " (" + fengshuiScore + "/100)")
                        .withStyle(color)));
        return true;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        switch (specialType.getUseType()) {
            case HOLD_USE:
                return UseAnim.BOW;
            case RIGHT_CLICK:
            case BLOCK_INTERACT:
            default:
                return UseAnim.NONE;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return specialType.getUseType() == UseType.HOLD_USE ? 72000 : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加分类信息
        tooltip.add(Component.literal("分类: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(specialType.getCategory().getDisplayName())
                        .withStyle(specialType.getCategory().getColor())));

        // 添加稀有度信息
        tooltip.add(Component.literal("品质: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(specialType.getRarity().getDisplayName())
                        .withStyle(specialType.getRarity().getColor())));

        // 添加使用方式
        tooltip.add(Component.literal("使用: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(specialType.getUseType().getDisplayName())
                        .withStyle(ChatFormatting.WHITE)));

        // 添加描述
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal(specialType.getDescription())
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));

        // 添加冷却时间信息
        if (specialType.getCooldownTicks() > 0) {
            int cooldownSeconds = specialType.getCooldownTicks() / 20;
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("冷却时间: " + cooldownSeconds + "秒")
                    .withStyle(ChatFormatting.BLUE));
        }

        // 添加功能提示
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("华夏文化特殊物品，具有独特功能")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
