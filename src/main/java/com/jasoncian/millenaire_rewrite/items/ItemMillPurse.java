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
 * 高级钱包系统 - 存储和管理三种货币
 * 基于原版Millenaire的ItemMillPurse实现
 * 
 * 功能特性：
 * - 存储三种货币：铜德尼尔、银德尼尔、金德尼尔
 * - NBT数据持久化
 * - 工具提示显示货币数量
 * - 右键打开钱包界面（后续实现GUI）
 * - 自动币种转换（1金=10银=100铜）
 */
public class ItemMillPurse extends Item {
    
    /** NBT标签常量 */
    private static final String NBT_COPPER_DENIERS = "copper_deniers";
    private static final String NBT_SILVER_DENIERS = "silver_deniers";
    private static final String NBT_GOLD_DENIERS = "gold_deniers";
    private static final String NBT_LAST_USE_TIME = "last_use_time";
    
    /** 双击间隔时间（毫秒） */
    private static final long DOUBLE_CLICK_INTERVAL = 500;
    
    /** 货币转换比率 */
    public static final int COPPER_PER_SILVER = 10;
    public static final int SILVER_PER_GOLD = 10;
    public static final int COPPER_PER_GOLD = COPPER_PER_SILVER * SILVER_PER_GOLD;
    
    public ItemMillPurse(Properties properties) {
        super(properties);
    }
    
    /**
     * 右键使用钱包
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            long currentTime = System.currentTimeMillis();
            long lastUseTime = getLastUseTime(itemStack);
            
            if (player.isCrouching()) {
                // 潜行右键：收集背包中的散币到钱包
                collectCoinsFromInventory(itemStack, player);
            } else if (currentTime - lastUseTime < DOUBLE_CLICK_INTERVAL) {
                // 双击：取出所有货币
                withdrawAllCoins(itemStack, player);
            } else {
                // 普通右键：显示钱包内容
                displayPurseContents(itemStack, player);
            }
            
            // 更新最后使用时间
            setLastUseTime(itemStack, currentTime);
        }
        
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
    
    /**
     * 显示钱包内容
     */
    private static void displayPurseContents(ItemStack purseStack, Player player) {
        int copper = getCopperDeniers(purseStack);
        int silver = getSilverDeniers(purseStack);
        int gold = getGoldDeniers(purseStack);
        
        player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.contents", gold, silver, copper)
            .withStyle(ChatFormatting.YELLOW));
                
        // 提示如何收集散币
        int looseCoins = countLooseCoinsInInventory(player);
        if (looseCoins > 0) {
            player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.loose_coins", looseCoins)
                .withStyle(ChatFormatting.GRAY)
                .append(Component.translatable("item.millenaire_rewrite.purse.collect_hint")
                    .withStyle(ChatFormatting.GREEN)));
        }
        
        // 提示如何取出货币 - 暂时通过命令，后续可以改为GUI
        int totalInPurse = getCopperDeniers(purseStack) + getSilverDeniers(purseStack) + getGoldDeniers(purseStack);
        if (totalInPurse > 0) {
            player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.quick_withdraw")
                .withStyle(ChatFormatting.GRAY));
        }
    }
    
    /**
     * 获取最后使用时间
     */
    private static long getLastUseTime(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getLong(NBT_LAST_USE_TIME);
    }
    
    /**
     * 设置最后使用时间
     */
    private static void setLastUseTime(ItemStack stack, long time) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putLong(NBT_LAST_USE_TIME, time);
    }
    
    /**
     * 收集背包中的散币到钱包
     */
    private static void collectCoinsFromInventory(ItemStack purseStack, Player player) {
        int collectedCopper = 0;
        int collectedSilver = 0;
        int collectedGold = 0;
        
        // 遍历背包收集散币
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            
            if (stack.getItem() == com.jasoncian.millenaire_rewrite.core.ModItems.DENIER.get()) {
                collectedCopper += stack.getCount();
                player.getInventory().setItem(i, ItemStack.EMPTY);
            } else if (stack.getItem() == com.jasoncian.millenaire_rewrite.core.ModItems.DENIER_ARGENT.get()) {
                collectedSilver += stack.getCount();
                player.getInventory().setItem(i, ItemStack.EMPTY);
            } else if (stack.getItem() == com.jasoncian.millenaire_rewrite.core.ModItems.DENIER_OR.get()) {
                collectedGold += stack.getCount();
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
        
        if (collectedCopper > 0 || collectedSilver > 0 || collectedGold > 0) {
            // 添加到钱包
            addCopperDeniers(purseStack, collectedCopper);
            addSilverDeniers(purseStack, collectedSilver);
            addGoldDeniers(purseStack, collectedGold);
            
            // 通知玩家
            player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.collected", 
                collectedGold, collectedSilver, collectedCopper)
                .withStyle(ChatFormatting.GREEN));
                    
            // 显示新的钱包内容
            displayPurseContents(purseStack, player);
        } else {
            player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.no_loose_coins")
                .withStyle(ChatFormatting.GRAY));
        }
    }
    
    /**
     * 统计背包中的散币数量
     */
    private static int countLooseCoinsInInventory(Player player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            
            if (stack.getItem() == com.jasoncian.millenaire_rewrite.core.ModItems.DENIER.get() ||
                stack.getItem() == com.jasoncian.millenaire_rewrite.core.ModItems.DENIER_ARGENT.get() ||
                stack.getItem() == com.jasoncian.millenaire_rewrite.core.ModItems.DENIER_OR.get()) {
                count += stack.getCount();
            }
        }
        return count;
    }
    
    /**
     * 物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        
        int copper = getCopperDeniers(stack);
        int silver = getSilverDeniers(stack);
        int gold = getGoldDeniers(stack);
        
        // 显示货币数量
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.storage").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.gold", gold).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.silver", silver).withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.copper", copper).withStyle(ChatFormatting.YELLOW));
        
        // 显示总价值（转换为铜德尼尔）
        int totalCopper = getTotalValueInCopper(stack);
        if (totalCopper > 0) {
            tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.total_value", totalCopper)
                .withStyle(ChatFormatting.AQUA));
        }
        
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.right_click").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.sneak_collect").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("item.millenaire_rewrite.purse.tooltip.double_click").withStyle(ChatFormatting.GOLD));
    }
    
    // ================ 货币管理方法 ================
    
    /**
     * 获取铜德尼尔数量
     */
    public static int getCopperDeniers(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getInt(NBT_COPPER_DENIERS);
    }
    
    /**
     * 获取银德尼尔数量
     */
    public static int getSilverDeniers(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getInt(NBT_SILVER_DENIERS);
    }
    
    /**
     * 获取金德尼尔数量
     */
    public static int getGoldDeniers(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.getInt(NBT_GOLD_DENIERS);
    }
    
    /**
     * 设置铜德尼尔数量
     */
    public static void setCopperDeniers(ItemStack stack, int amount) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(NBT_COPPER_DENIERS, Math.max(0, amount));
    }
    
    /**
     * 设置银德尼尔数量
     */
    public static void setSilverDeniers(ItemStack stack, int amount) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(NBT_SILVER_DENIERS, Math.max(0, amount));
    }
    
    /**
     * 设置金德尼尔数量
     */
    public static void setGoldDeniers(ItemStack stack, int amount) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(NBT_GOLD_DENIERS, Math.max(0, amount));
    }
    
    /**
     * 添加铜德尼尔
     */
    public static void addCopperDeniers(ItemStack stack, int amount) {
        setCopperDeniers(stack, getCopperDeniers(stack) + amount);
    }
    
    /**
     * 添加银德尼尔
     */
    public static void addSilverDeniers(ItemStack stack, int amount) {
        setSilverDeniers(stack, getSilverDeniers(stack) + amount);
    }
    
    /**
     * 添加金德尼尔
     */
    public static void addGoldDeniers(ItemStack stack, int amount) {
        setGoldDeniers(stack, getGoldDeniers(stack) + amount);
    }
    
    /**
     * 获取总价值（转换为铜德尼尔）
     */
    public static int getTotalValueInCopper(ItemStack stack) {
        int copper = getCopperDeniers(stack);
        int silver = getSilverDeniers(stack);
        int gold = getGoldDeniers(stack);
        
        return copper + (silver * COPPER_PER_SILVER) + (gold * COPPER_PER_GOLD);
    }
    
    /**
     * 尝试从钱包扣除指定数量的铜德尼尔
     * 会自动进行币种转换
     * 
     * @param stack 钱包物品堆
     * @param copperAmount 要扣除的铜德尼尔数量
     * @return 是否成功扣除
     */
    public static boolean deductCopper(ItemStack stack, int copperAmount) {
        if (copperAmount <= 0) return true;
        
        int totalCopper = getTotalValueInCopper(stack);
        if (totalCopper < copperAmount) {
            return false; // 余额不足
        }
        
        // 从铜德尼尔开始扣除
        int copper = getCopperDeniers(stack);
        int silver = getSilverDeniers(stack);
        int gold = getGoldDeniers(stack);
        
        int remaining = copperAmount;
        
        // 先扣除铜德尼尔
        if (copper >= remaining) {
            setCopperDeniers(stack, copper - remaining);
            return true;
        }
        
        remaining -= copper;
        setCopperDeniers(stack, 0);
        
        // 扣除银德尼尔（转换为铜德尼尔）
        int silverNeeded = (remaining + COPPER_PER_SILVER - 1) / COPPER_PER_SILVER; // 向上取整
        if (silver >= silverNeeded) {
            setSilverDeniers(stack, silver - silverNeeded);
            // 找零
            int change = (silverNeeded * COPPER_PER_SILVER) - remaining;
            if (change > 0) {
                setCopperDeniers(stack, change);
            }
            return true;
        }
        
        remaining -= silver * COPPER_PER_SILVER;
        setSilverDeniers(stack, 0);
        
        // 扣除金德尼尔（转换为铜德尼尔）
        int goldNeeded = (remaining + COPPER_PER_GOLD - 1) / COPPER_PER_GOLD; // 向上取整
        if (gold >= goldNeeded) {
            setGoldDeniers(stack, gold - goldNeeded);
            // 找零
            int change = (goldNeeded * COPPER_PER_GOLD) - remaining;
            if (change > 0) {
                // 优化找零：尽量给银德尼尔
                int silverChange = change / COPPER_PER_SILVER;
                int copperChange = change % COPPER_PER_SILVER;
                setSilverDeniers(stack, silverChange);
                setCopperDeniers(stack, copperChange);
            }
            return true;
        }
        
        return false; // 理论上不应该到达这里
    }
    
    /**
     * 添加总价值（自动转换为最优币种组合）
     */
    public static void addTotalValue(ItemStack stack, int copperValue) {
        if (copperValue <= 0) return;
        
        int currentCopper = getCopperDeniers(stack);
        int currentSilver = getSilverDeniers(stack);
        int currentGold = getGoldDeniers(stack);
        
        // 计算新的总铜德尼尔数量
        int totalCopper = currentCopper + copperValue;
        
        // 转换为最优币种组合
        int newGold = totalCopper / COPPER_PER_GOLD;
        totalCopper %= COPPER_PER_GOLD;
        
        int newSilver = totalCopper / COPPER_PER_SILVER;
        totalCopper %= COPPER_PER_SILVER;
        
        // 更新钱包
        setGoldDeniers(stack, currentGold + newGold);
        setSilverDeniers(stack, currentSilver + newSilver);
        setCopperDeniers(stack, totalCopper);
    }
    
    /**
     * 检查钱包是否为空
     */
    public static boolean isEmpty(ItemStack stack) {
        return getTotalValueInCopper(stack) == 0;
    }
    
    /**
     * 清空钱包
     */
    public static void clear(ItemStack stack) {
        setCopperDeniers(stack, 0);
        setSilverDeniers(stack, 0);
        setGoldDeniers(stack, 0);
    }
    
    /**
     * 从钱包取出所有货币到背包
     */
    public static void withdrawAllCoins(ItemStack purseStack, Player player) {
        int copper = getCopperDeniers(purseStack);
        int silver = getSilverDeniers(purseStack);
        int gold = getGoldDeniers(purseStack);
        
        if (copper == 0 && silver == 0 && gold == 0) {
            player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.empty")
                .withStyle(ChatFormatting.GRAY));
            return;
        }
        
        // 清空钱包
        clear(purseStack);
        
        // 给予玩家散币
        if (gold > 0) {
            ItemStack goldStack = new ItemStack(com.jasoncian.millenaire_rewrite.core.ModItems.DENIER_OR.get(), gold);
            if (!player.getInventory().add(goldStack)) {
                player.drop(goldStack, false);
            }
        }
        
        if (silver > 0) {
            ItemStack silverStack = new ItemStack(com.jasoncian.millenaire_rewrite.core.ModItems.DENIER_ARGENT.get(), silver);
            if (!player.getInventory().add(silverStack)) {
                player.drop(silverStack, false);
            }
        }
        
        if (copper > 0) {
            ItemStack copperStack = new ItemStack(com.jasoncian.millenaire_rewrite.core.ModItems.DENIER.get(), copper);
            if (!player.getInventory().add(copperStack)) {
                player.drop(copperStack, false);
            }
        }
        
        // 通知玩家
        player.sendSystemMessage(Component.translatable("item.millenaire_rewrite.purse.withdrawn", 
            gold, silver, copper)
            .withStyle(ChatFormatting.GREEN));
    }
}
