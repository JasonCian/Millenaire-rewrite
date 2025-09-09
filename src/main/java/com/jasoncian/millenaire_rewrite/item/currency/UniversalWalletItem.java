package com.jasoncian.millenaire_rewrite.item.currency;

import com.jasoncian.millenaire_rewrite.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 通用钱包物品类
 * 
 * 设计为跨文化的货币存储系统：
 * - 支持所有文化的货币存储（华夏、诺曼、拜占庭、日本等）
 * - 自动货币兑换和计算
 * - 智能存取模式
 * - 便携式货币管理
 * 
 * 钱包功能特点：
 * - 右键点击：自动收集背包中的所有货币到钱包
 * - Shift+右键：将钱包中的货币全部取出到背包
 * - 显示各文化货币的总量和等值
 * - 支持跨文化交易时的自动兑换
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class UniversalWalletItem extends Item {
    
    /** NBT标签前缀 */
    private static final String NBT_PREFIX = "millenaire_wallet_";
    
    /** 各文化货币在NBT中的键名 */
    private static final String HUAXIA_COPPER_KEY = NBT_PREFIX + "huaxia_copper";
    private static final String HUAXIA_SILVER_KEY = NBT_PREFIX + "huaxia_silver";
    private static final String HUAXIA_GOLD_KEY = NBT_PREFIX + "huaxia_gold";
    
    // TODO: 其他文化货币的键名将在实现时添加
    // private static final String NORMAN_DENIER_KEY = NBT_PREFIX + "norman_denier";
    // private static final String JAPANESE_MON_KEY = NBT_PREFIX + "japanese_mon";
    // private static final String BYZANTINE_SOLIDUS_KEY = NBT_PREFIX + "byzantine_solidus";
    
    /**
     * 创建通用钱包物品
     * 
     * @param properties 物品属性
     */
    public UniversalWalletItem(Properties properties) {
        super(properties.stacksTo(1)); // 钱包不能堆叠
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack walletStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                // Shift+右键：取出所有货币到背包
                withdrawAllCurrency(walletStack, player);
            } else {
                // 右键：收集背包中的所有货币到钱包
                collectAllCurrency(walletStack, player);
            }
        }
        
        return InteractionResultHolder.sidedSuccess(walletStack, level.isClientSide);
    }
    
    /**
     * 收集玩家背包中的所有货币到钱包
     * 
     * @param walletStack 钱包物品栈
     * @param player 玩家
     */
    private void collectAllCurrency(ItemStack walletStack, Player player) {
        Inventory inventory = player.getInventory();
        boolean collected = false;
        
        // 收集华夏货币
        collected |= collectSpecificCurrency(walletStack, inventory, ModItems.HUAXIA_COPPER_COIN.get(), HUAXIA_COPPER_KEY);
        collected |= collectSpecificCurrency(walletStack, inventory, ModItems.HUAXIA_SILVER_TAEL.get(), HUAXIA_SILVER_KEY);
        collected |= collectSpecificCurrency(walletStack, inventory, ModItems.HUAXIA_GOLD_INGOT.get(), HUAXIA_GOLD_KEY);
        
        // TODO: 添加其他文化货币的收集
        // collected |= collectSpecificCurrency(walletStack, inventory, ModItems.NORMAN_DENIER.get(), NORMAN_DENIER_KEY);
        
        if (collected) {
            player.displayClientMessage(Component.literal("§a货币已收集到钱包中"), true);
        } else {
            player.displayClientMessage(Component.literal("§7背包中没有可收集的货币"), true);
        }
    }
    
    /**
     * 收集特定类型的货币
     * 
     * @param walletStack 钱包物品栈
     * @param inventory 玩家背包
     * @param currencyItem 货币物品
     * @param nbtKey NBT键名
     * @return 是否收集了货币
     */
    private boolean collectSpecificCurrency(ItemStack walletStack, Inventory inventory, 
                                          Item currencyItem, String nbtKey) {
        int totalCollected = 0;
        
        // 遍历背包，收集指定货币
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == currencyItem) {
                totalCollected += stack.getCount();
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        
        if (totalCollected > 0) {
            // 将收集到的货币添加到钱包
            int currentAmount = getCurrencyAmount(walletStack, nbtKey);
            setCurrencyAmount(walletStack, nbtKey, currentAmount + totalCollected);
            return true;
        }
        
        return false;
    }
    
    /**
     * 取出钱包中的所有货币到背包
     * 
     * @param walletStack 钱包物品栈
     * @param player 玩家
     */
    private void withdrawAllCurrency(ItemStack walletStack, Player player) {
        Inventory inventory = player.getInventory();
        boolean withdrawn = false;
        
        // 取出华夏货币
        withdrawn |= withdrawSpecificCurrency(walletStack, inventory, ModItems.HUAXIA_COPPER_COIN.get(), HUAXIA_COPPER_KEY);
        withdrawn |= withdrawSpecificCurrency(walletStack, inventory, ModItems.HUAXIA_SILVER_TAEL.get(), HUAXIA_SILVER_KEY);
        withdrawn |= withdrawSpecificCurrency(walletStack, inventory, ModItems.HUAXIA_GOLD_INGOT.get(), HUAXIA_GOLD_KEY);
        
        // TODO: 添加其他文化货币的取出
        // withdrawn |= withdrawSpecificCurrency(walletStack, inventory, ModItems.NORMAN_DENIER.get(), NORMAN_DENIER_KEY);
        
        if (withdrawn) {
            player.displayClientMessage(Component.literal("§a货币已从钱包中取出"), true);
        } else {
            player.displayClientMessage(Component.literal("§7钱包中没有货币"), true);
        }
    }
    
    /**
     * 取出特定类型的货币
     * 
     * @param walletStack 钱包物品栈
     * @param inventory 玩家背包
     * @param currencyItem 货币物品
     * @param nbtKey NBT键名
     * @return 是否取出了货币
     */
    private boolean withdrawSpecificCurrency(ItemStack walletStack, Inventory inventory, 
                                           Item currencyItem, String nbtKey) {
        int amount = getCurrencyAmount(walletStack, nbtKey);
        if (amount <= 0) {
            return false;
        }
        
        // 创建货币物品栈并尝试放入背包
        ItemStack currencyStack = new ItemStack(currencyItem, amount);
        if (inventory.add(currencyStack)) {
            // 成功放入背包，清空钱包中的该货币
            setCurrencyAmount(walletStack, nbtKey, 0);
            return true;
        } else if (currencyStack.getCount() < amount) {
            // 部分放入背包，更新钱包中的数量
            setCurrencyAmount(walletStack, nbtKey, currencyStack.getCount());
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取钱包中特定货币的数量
     * 
     * @param walletStack 钱包物品栈
     * @param nbtKey NBT键名
     * @return 货币数量
     */
    public int getCurrencyAmount(ItemStack walletStack, String nbtKey) {
        CompoundTag tag = walletStack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getInt(nbtKey);
    }
    
    /**
     * 设置钱包中特定货币的数量
     * 
     * @param walletStack 钱包物品栈
     * @param nbtKey NBT键名
     * @param amount 货币数量
     */
    public void setCurrencyAmount(ItemStack walletStack, String nbtKey, int amount) {
        CompoundTag tag = walletStack.getOrCreateTag();
        if (amount <= 0) {
            tag.remove(nbtKey);
        } else {
            tag.putInt(nbtKey, amount);
        }
    }
    
    /**
     * 获取钱包中华夏货币的总价值（以铜钱为基本单位）
     * 
     * @param walletStack 钱包物品栈
     * @return 华夏货币总价值
     */
    public int getHuaxiaTotalValue(ItemStack walletStack) {
        int copper = getCurrencyAmount(walletStack, HUAXIA_COPPER_KEY);
        int silver = getCurrencyAmount(walletStack, HUAXIA_SILVER_KEY);
        int gold = getCurrencyAmount(walletStack, HUAXIA_GOLD_KEY);
        
        return copper + silver * 100 + gold * 10000;
    }
    
    /**
     * 检查钱包是否为空
     * 
     * @param walletStack 钱包物品栈
     * @return 是否为空
     */
    public boolean isEmpty(ItemStack walletStack) {
        CompoundTag tag = walletStack.getTag();
        if (tag == null) {
            return true;
        }
        
        // 检查所有货币类型是否都为0或不存在
        return getCurrencyAmount(walletStack, HUAXIA_COPPER_KEY) == 0 &&
               getCurrencyAmount(walletStack, HUAXIA_SILVER_KEY) == 0 &&
               getCurrencyAmount(walletStack, HUAXIA_GOLD_KEY) == 0;
        // TODO: 添加其他文化货币的检查
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                               List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加钱包说明
        tooltip.add(Component.literal("通用货币钱包")
                .withStyle(ChatFormatting.GOLD));
        
        tooltip.add(Component.literal("可存储所有文化的货币")
                .withStyle(ChatFormatting.GRAY));
        
        // 显示使用方法
        tooltip.add(Component.literal("右键: 收集货币")
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Shift+右键: 取出货币")
                .withStyle(ChatFormatting.AQUA));
        
        // 显示当前存储的货币
        boolean hasAnyCurrency = false;
        
        // 华夏货币
        int huaxiaCopper = getCurrencyAmount(stack, HUAXIA_COPPER_KEY);
        int huaxiaSilver = getCurrencyAmount(stack, HUAXIA_SILVER_KEY);
        int huaxiaGold = getCurrencyAmount(stack, HUAXIA_GOLD_KEY);
        
        if (huaxiaCopper > 0 || huaxiaSilver > 0 || huaxiaGold > 0) {
            tooltip.add(Component.literal("").append(Component.literal("华夏货币:")
                    .withStyle(ChatFormatting.YELLOW)));
            
            if (huaxiaGold > 0) {
                tooltip.add(Component.literal("  §e" + huaxiaGold + " 金元宝"));
            }
            if (huaxiaSilver > 0) {
                tooltip.add(Component.literal("  §f" + huaxiaSilver + " 银两"));
            }
            if (huaxiaCopper > 0) {
                tooltip.add(Component.literal("  §6" + huaxiaCopper + " 铜钱"));
            }
            
            // 显示总价值
            int totalValue = getHuaxiaTotalValue(stack);
            tooltip.add(Component.literal("  总价值: " + totalValue + " 铜钱")
                    .withStyle(ChatFormatting.GREEN));
            
            hasAnyCurrency = true;
        }
        
        // TODO: 添加其他文化货币的显示
        
        if (!hasAnyCurrency) {
            tooltip.add(Component.literal("空钱包")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    @Override
    public Component getName(ItemStack stack) {
        if (isEmpty(stack)) {
            return Component.literal("§f通用钱包");
        } else {
            // 显示总价值概览
            int huaxiaTotal = getHuaxiaTotalValue(stack);
            String valueDisplay = "";
            
            if (huaxiaTotal > 0) {
                // 转换为最大面额显示
                if (huaxiaTotal >= 10000) {
                    int gold = huaxiaTotal / 10000;
                    int remainder = huaxiaTotal % 10000;
                    valueDisplay = "§e" + gold + "金";
                    if (remainder >= 100) {
                        int silver = remainder / 100;
                        valueDisplay += " §f" + silver + "银";
                    }
                    if (remainder % 100 > 0) {
                        valueDisplay += " §6" + (remainder % 100) + "铜";
                    }
                } else if (huaxiaTotal >= 100) {
                    int silver = huaxiaTotal / 100;
                    int copper = huaxiaTotal % 100;
                    valueDisplay = "§f" + silver + "银";
                    if (copper > 0) {
                        valueDisplay += " §6" + copper + "铜";
                    }
                } else {
                    valueDisplay = "§6" + huaxiaTotal + "铜";
                }
            }
            
            return Component.literal("§f通用钱包: " + valueDisplay);
        }
    }
    
    /**
     * 检查是否为通用钱包
     * 
     * @param stack 物品栈
     * @return 是否为通用钱包
     */
    public static boolean isUniversalWallet(ItemStack stack) {
        return stack.getItem() instanceof UniversalWalletItem;
    }
}
