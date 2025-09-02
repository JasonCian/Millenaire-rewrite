package com.jasoncian.millenaire_rewrite.util;

import com.jasoncian.millenaire_rewrite.core.ModItems;
import com.jasoncian.millenaire_rewrite.items.ItemMillPurse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

/**
 * 货币系统工具类
 * 提供玩家货币管理的便捷方法
 * 
 * 功能特性：
 * - 在玩家背包中查找钱包
 * - 统计玩家所有货币
 * - 执行货币交易
 * - 货币转换
 */
public class CurrencyUtils {
    
    /** 货币转换比率 */
    public static final int COPPER_PER_SILVER = ItemMillPurse.COPPER_PER_SILVER;
    public static final int SILVER_PER_GOLD = ItemMillPurse.SILVER_PER_GOLD;
    public static final int COPPER_PER_GOLD = ItemMillPurse.COPPER_PER_GOLD;
    
    /**
     * 在玩家背包中查找钱包
     */
    public static ItemStack findPurseInInventory(Player player) {
        Inventory inventory = player.getInventory();
        
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == ModItems.PURSE.get()) {
                return stack;
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    /**
     * 获取玩家的总货币数量（铜德尼尔）
     * 包括背包中的散币和钱包中的货币
     */
    public static int getPlayerTotalCurrency(Player player) {
        Inventory inventory = player.getInventory();
        int total = 0;
        
        // 检查钱包
        ItemStack purse = findPurseInInventory(player);
        if (!purse.isEmpty()) {
            total += ItemMillPurse.getTotalValueInCopper(purse);
        }
        
        // 检查散币
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            
            if (stack.getItem() == ModItems.DENIER.get()) {
                total += stack.getCount();
            } else if (stack.getItem() == ModItems.DENIER_ARGENT.get()) {
                total += stack.getCount() * COPPER_PER_SILVER;
            } else if (stack.getItem() == ModItems.DENIER_OR.get()) {
                total += stack.getCount() * COPPER_PER_GOLD;
            }
        }
        
        return total;
    }
    
    /**
     * 尝试从玩家扣除指定数量的货币
     * 优先从钱包扣除，然后从散币扣除
     */
    public static boolean deductCurrencyFromPlayer(Player player, int copperAmount) {
        if (copperAmount <= 0) return true;
        
        // 先尝试从钱包扣除
        ItemStack purse = findPurseInInventory(player);
        if (!purse.isEmpty()) {
            int purseValue = ItemMillPurse.getTotalValueInCopper(purse);
            if (purseValue >= copperAmount) {
                return ItemMillPurse.deductCopper(purse, copperAmount);
            } else {
                // 钱包不够，全部扣除然后从散币补足
                ItemMillPurse.clear(purse);
                copperAmount -= purseValue;
            }
        }
        
        // 从散币扣除剩余部分
        return deductLooseCoins(player, copperAmount);
    }
    
    /**
     * 从玩家背包的散币中扣除货币
     */
    private static boolean deductLooseCoins(Player player, int copperAmount) {
        Inventory inventory = player.getInventory();
        int remaining = copperAmount;
        
        // 计算总散币价值
        int totalLooseValue = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == ModItems.DENIER.get()) {
                totalLooseValue += stack.getCount();
            } else if (stack.getItem() == ModItems.DENIER_ARGENT.get()) {
                totalLooseValue += stack.getCount() * COPPER_PER_SILVER;
            } else if (stack.getItem() == ModItems.DENIER_OR.get()) {
                totalLooseValue += stack.getCount() * COPPER_PER_GOLD;
            }
        }
        
        if (totalLooseValue < remaining) {
            return false; // 余额不足
        }
        
        // 按优先级扣除：先扣铜，再扣银，最后扣金
        
        // 扣除铜德尼尔
        for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == ModItems.DENIER.get()) {
                int deduct = Math.min(remaining, stack.getCount());
                stack.shrink(deduct);
                remaining -= deduct;
            }
        }
        
        // 扣除银德尼尔
        for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == ModItems.DENIER_ARGENT.get()) {
                int silverNeeded = (remaining + COPPER_PER_SILVER - 1) / COPPER_PER_SILVER;
                int deduct = Math.min(silverNeeded, stack.getCount());
                stack.shrink(deduct);
                
                int value = deduct * COPPER_PER_SILVER;
                remaining -= value;
                
                // 找零
                if (remaining < 0) {
                    giveCurrencyToPlayer(player, -remaining);
                    remaining = 0;
                }
            }
        }
        
        // 扣除金德尼尔
        for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == ModItems.DENIER_OR.get()) {
                int goldNeeded = (remaining + COPPER_PER_GOLD - 1) / COPPER_PER_GOLD;
                int deduct = Math.min(goldNeeded, stack.getCount());
                stack.shrink(deduct);
                
                int value = deduct * COPPER_PER_GOLD;
                remaining -= value;
                
                // 找零
                if (remaining < 0) {
                    giveCurrencyToPlayer(player, -remaining);
                    remaining = 0;
                }
            }
        }
        
        return remaining == 0;
    }
    
    /**
     * 给玩家添加货币
     * 优先存入钱包，钱包满了或没有钱包则给散币
     */
    public static void giveCurrencyToPlayer(Player player, int copperAmount) {
        if (copperAmount <= 0) return;
        
        // 尝试存入钱包
        ItemStack purse = findPurseInInventory(player);
        if (!purse.isEmpty()) {
            ItemMillPurse.addTotalValue(purse, copperAmount);
            return;
        }
        
        // 没有钱包，给散币
        giveOptimalCoins(player, copperAmount);
    }
    
    /**
     * 给玩家最优的币种组合
     */
    private static void giveOptimalCoins(Player player, int copperAmount) {
        Inventory inventory = player.getInventory();
        
        // 转换为最优币种组合
        int gold = copperAmount / COPPER_PER_GOLD;
        copperAmount %= COPPER_PER_GOLD;
        
        int silver = copperAmount / COPPER_PER_SILVER;
        copperAmount %= COPPER_PER_SILVER;
        
        int copper = copperAmount;
        
        // 给予金德尼尔
        if (gold > 0) {
            ItemStack goldStack = new ItemStack(ModItems.DENIER_OR.get(), gold);
            if (!inventory.add(goldStack)) {
                player.drop(goldStack, false); // 背包满了就掉在地上
            }
        }
        
        // 给予银德尼尔
        if (silver > 0) {
            ItemStack silverStack = new ItemStack(ModItems.DENIER_ARGENT.get(), silver);
            if (!inventory.add(silverStack)) {
                player.drop(silverStack, false);
            }
        }
        
        // 给予铜德尼尔
        if (copper > 0) {
            ItemStack copperStack = new ItemStack(ModItems.DENIER.get(), copper);
            if (!inventory.add(copperStack)) {
                player.drop(copperStack, false);
            }
        }
    }
    
    /**
     * 检查玩家是否有足够的货币
     */
    public static boolean hasEnoughCurrency(Player player, int copperAmount) {
        return getPlayerTotalCurrency(player) >= copperAmount;
    }
    
    /**
     * 创建带有指定货币的钱包
     */
    public static ItemStack createPurseWithCurrency(int copper, int silver, int gold) {
        ItemStack purse = new ItemStack(ModItems.PURSE.get());
        ItemMillPurse.setCopperDeniers(purse, copper);
        ItemMillPurse.setSilverDeniers(purse, silver);
        ItemMillPurse.setGoldDeniers(purse, gold);
        return purse;
    }
    
    /**
     * 货币格式化显示
     */
    public static String formatCurrency(int copperValue) {
        if (copperValue <= 0) {
            return Component.translatable("currency.millenaire_rewrite.zero").getString();
        }
        
        int gold = copperValue / COPPER_PER_GOLD;
        copperValue %= COPPER_PER_GOLD;
        
        int silver = copperValue / COPPER_PER_SILVER;
        copperValue %= COPPER_PER_SILVER;
        
        StringBuilder sb = new StringBuilder();
        
        if (gold > 0) {
            sb.append(gold).append(" ")
              .append(Component.translatable("currency.millenaire_rewrite.gold").getString());
        }
        
        if (silver > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(silver).append(" ")
              .append(Component.translatable("currency.millenaire_rewrite.silver").getString());
        }
        
        if (copperValue > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(copperValue).append(" ")
              .append(Component.translatable("currency.millenaire_rewrite.copper").getString());
        }
        
        if (sb.length() == 0) {
            return Component.translatable("currency.millenaire_rewrite.zero").getString();
        }
        
        return sb.toString() + Component.translatable("currency.millenaire_rewrite.denier").getString();
    }
}
