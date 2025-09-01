package org.millenaire.items;

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

import javax.annotation.Nullable;
import java.util.List;

/**
 * 基于旧版本Millenaire的法杖实现
 * 包含召唤法杖、否定法杖、创意法杖和调音叉的功能
 */
public class ItemMillWand extends Item {
    
    public ItemMillWand() {
        super(new Properties()
                .stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        
        if (player == null || world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        String itemName = stack.getItem().toString();
        
        // 调音叉 - 显示方块信息
        if (itemName.contains("tuning_fork")) {
            BlockState state = world.getBlockState(pos);
            player.sendSystemMessage(Component.literal("Block: " + state.getBlock().getName().getString()));
            player.sendSystemMessage(Component.literal("Position: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
            return InteractionResult.SUCCESS;
        }
        
        // 召唤法杖 - 用于建筑导入功能
        if (itemName.contains("summoning")) {
            // 在旧版本中，这个法杖用于将建筑模板导入到游戏世界
            player.sendSystemMessage(Component.literal("Building import function - not yet implemented"));
            return InteractionResult.SUCCESS;
        }
        
        // 否定法杖 - 用于建筑导出功能  
        if (itemName.contains("negation")) {
            // 在旧版本中，这个法杖用于将游戏中的建筑导出为模板
            player.sendSystemMessage(Component.literal("Building export function - not yet implemented"));
            return InteractionResult.SUCCESS;
        }
        
        // 创意法杖 - 作物权限和箱子锁定
        if (itemName.contains("creative")) {
            player.sendSystemMessage(Component.literal("Creative permissions - not yet implemented"));
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
        
        String itemName = stack.getItem().toString();
        
        // 右键空气时的功能
        if (itemName.contains("tuning_fork")) {
            player.sendSystemMessage(Component.literal("Tuning Fork - Right-click on blocks to inspect them"));
        } else if (itemName.contains("summoning")) {
            player.sendSystemMessage(Component.literal("Summoning Wand - Used for building import"));
        } else if (itemName.contains("negation")) {
            player.sendSystemMessage(Component.literal("Negation Wand - Used for building export"));
        } else if (itemName.contains("creative")) {
            player.sendSystemMessage(Component.literal("Creative Wand - Manage crop permissions and chest locks"));
        }
        
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        String itemName = stack.getItem().toString();
        
        if (itemName.contains("tuning_fork")) {
            tooltip.add(Component.literal("§7Right-click blocks to inspect them"));
            tooltip.add(Component.literal("§7Shows block type and position"));
        } else if (itemName.contains("summoning")) {
            tooltip.add(Component.literal("§7Used for importing building templates"));
            tooltip.add(Component.literal("§7Creates structures in the world"));
        } else if (itemName.contains("negation")) {
            tooltip.add(Component.literal("§7Used for exporting building templates"));
            tooltip.add(Component.literal("§7Saves structures from the world"));
        } else if (itemName.contains("creative")) {
            tooltip.add(Component.literal("§7Manage crop growth permissions"));
            tooltip.add(Component.literal("§7Lock and unlock chests"));
        }
        
        super.appendHoverText(stack, world, tooltip, flag);
    }
}
