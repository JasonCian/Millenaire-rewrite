package com.jasoncian.millenaire_rewrite.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.ChatFormatting;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Millenaire法杖物品 - 现代化实现
 * 
 * 功能列表:
 * - 调音叉功能：检查方块信息
 * - 召唤法杖：建筑导入功能
 * - 否定法杖：建筑导出功能  
 * - 创意法杖：作物权限和箱子管理
 * - 右键交互系统
 * - 工具提示信息显示
 * 
 * @author JasonCian
 * @version 0.1.3-alpha
 * @since 1.20.1
 */
public class MillWandItem extends Item {
    
    public enum WandType {
        TUNING_FORK("tuning_fork"),
        SUMMONING("summoning"),
        NEGATION("negation"), 
        CREATIVE("creative");
        
        private final String name;
        
        WandType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private final WandType wandType;
    
    public MillWandItem(WandType type, Properties properties) {
        super(properties.stacksTo(1));
        this.wandType = type;
    }
    
    public MillWandItem(Properties properties) {
        this(WandType.TUNING_FORK, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        
        if (player == null || world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        switch (wandType) {
            case TUNING_FORK:
                // 调音叉 - 显示方块信息
                BlockState state = world.getBlockState(pos);
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.block_info", 
                    state.getBlock().getName().getString()).withStyle(ChatFormatting.AQUA));
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.position", 
                    pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
                return InteractionResult.SUCCESS;
                
            case SUMMONING:
                // 召唤法杖 - 用于建筑导入功能
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.summoning.use")
                    .withStyle(ChatFormatting.GREEN));
                return InteractionResult.SUCCESS;
                
            case NEGATION:
                // 否定法杖 - 用于建筑导出功能  
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.negation.use")
                    .withStyle(ChatFormatting.RED));
                return InteractionResult.SUCCESS;
                
            case CREATIVE:
                // 创意法杖 - 作物权限和箱子锁定
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.creative.use")
                    .withStyle(ChatFormatting.GOLD));
                return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (world.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        
        // 右键空气时的功能
        switch (wandType) {
            case TUNING_FORK:
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.tuning_fork.help")
                    .withStyle(ChatFormatting.AQUA));
                break;
            case SUMMONING:
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.summoning.help")
                    .withStyle(ChatFormatting.GREEN));
                break;
            case NEGATION:
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.negation.help")
                    .withStyle(ChatFormatting.RED));
                break;
            case CREATIVE:
                player.sendSystemMessage(Component.translatable("debug.millenaire_rewrite.wand.creative.help")
                    .withStyle(ChatFormatting.GOLD));
                break;
        }
        
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        switch (wandType) {
            case TUNING_FORK:
                tooltip.add(Component.translatable("item.millenaire_rewrite.tuning_fork.tooltip.1")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.millenaire_rewrite.tuning_fork.tooltip.2")
                    .withStyle(ChatFormatting.GRAY));
                break;
            case SUMMONING:
                tooltip.add(Component.translatable("item.millenaire_rewrite.summoning_wand.tooltip.1")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.millenaire_rewrite.summoning_wand.tooltip.2")
                    .withStyle(ChatFormatting.GRAY));
                break;
            case NEGATION:
                tooltip.add(Component.translatable("item.millenaire_rewrite.negation_wand.tooltip.1")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.millenaire_rewrite.negation_wand.tooltip.2")
                    .withStyle(ChatFormatting.GRAY));
                break;
            case CREATIVE:
                tooltip.add(Component.translatable("item.millenaire_rewrite.creative_wand.tooltip.1")
                    .withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("item.millenaire_rewrite.creative_wand.tooltip.2")
                    .withStyle(ChatFormatting.GRAY));
                break;
        }
        
        super.appendHoverText(stack, world, tooltip, flag);
    }
    
    public WandType getWandType() {
        return wandType;
    }
}
