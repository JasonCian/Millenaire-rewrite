package com.jasoncian.millenaire_rewrite.item.currency;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 货币系统管理器
 * 
 * 提供货币价值计算、兑换和管理功能：
 * - 不同文化货币之间的价值换算
 * - 货币自动兑换功能
 * - 货币价值评估
 * - 支付计算功能
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CurrencyManager {
    
    /** 单例实例 */
    private static CurrencyManager instance;
    
    /** 文化间货币汇率表（相对于最小货币单位的比率） */
    private final Map<CultureType, Double> exchangeRates;
    
    /**
     * 私有构造函数
     */
    private CurrencyManager() {
        exchangeRates = new HashMap<>();
        initializeExchangeRates();
    }
    
    /**
     * 获取单例实例
     * 
     * @return 货币管理器实例
     */
    public static CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }
    
    /**
     * 初始化货币汇率
     * 设置各文化货币相对于华夏铜钱的汇率
     */
    private void initializeExchangeRates() {
        // 华夏文化作为基准货币（1.0倍率）
        exchangeRates.put(CultureType.HUAXIA, 1.0);
        
        // TODO: 添加其他文化的汇率
        // exchangeRates.put(CultureType.NORMAN, 1.2); // 诺曼德尼尔相对较值钱
        // exchangeRates.put(CultureType.BYZANTINE, 1.5); // 拜占庭货币更值钱
        // exchangeRates.put(CultureType.JAPANESE, 0.8); // 日本货币相对便宜
    }
    
    /**
     * 计算物品栈的总价值（转换为标准货币单位）
     * 
     * @param stack 货币物品栈
     * @return 标准货币单位的价值
     */
    public int getStandardValue(ItemStack stack) {
        if (!CurrencyItem.isCurrency(stack)) {
            return 0;
        }
        
        CurrencyItem currencyItem = (CurrencyItem) stack.getItem();
        CultureType culture = currencyItem.getCultureType();
        int baseValue = currencyItem.getTotalValue(stack);
        double exchangeRate = exchangeRates.getOrDefault(culture, 1.0);
        
        return (int) Math.round(baseValue * exchangeRate);
    }
    
    /**
     * 检查是否有足够的货币支付指定金额
     * 
     * @param currencyStacks 货币物品栈列表
     * @param requiredAmount 需要的金额（标准货币单位）
     * @return 是否有足够的货币
     */
    public boolean hasEnoughCurrency(List<ItemStack> currencyStacks, int requiredAmount) {
        return getTotalValue(currencyStacks) >= requiredAmount;
    }
    
    /**
     * 计算货币列表的总价值
     * 
     * @param currencyStacks 货币物品栈列表
     * @return 总价值（标准货币单位）
     */
    public int getTotalValue(List<ItemStack> currencyStacks) {
        return currencyStacks.stream()
                .mapToInt(this::getStandardValue)
                .sum();
    }
    
    /**
     * 从货币列表中扣除指定金额
     * 
     * @param currencyStacks 货币物品栈列表（会被修改）
     * @param amount 要扣除的金额（标准货币单位）
     * @return 是否成功扣除
     */
    public boolean deductCurrency(List<ItemStack> currencyStacks, int amount) {
        if (!hasEnoughCurrency(currencyStacks, amount)) {
            return false;
        }
        
        int remainingAmount = amount;
        
        // 从价值最低的货币开始扣除
        currencyStacks.sort((a, b) -> {
            if (!CurrencyItem.isCurrency(a) || !CurrencyItem.isCurrency(b)) {
                return 0;
            }
            
            CurrencyItem itemA = (CurrencyItem) a.getItem();
            CurrencyItem itemB = (CurrencyItem) b.getItem();
            
            return Integer.compare(itemA.getBaseValue(), itemB.getBaseValue());
        });
        
        for (ItemStack stack : currencyStacks) {
            if (remainingAmount <= 0) break;
            if (!CurrencyItem.isCurrency(stack)) continue;
            
            int singleValue = getStandardValue(new ItemStack(stack.getItem(), 1));
            
            while (remainingAmount >= singleValue && stack.getCount() > 0) {
                stack.shrink(1);
                remainingAmount -= singleValue;
            }
        }
        
        // 移除空的物品栈
        currencyStacks.removeIf(ItemStack::isEmpty);
        
        return remainingAmount == 0;
    }
    
    /**
     * 将标准货币单位转换为指定文化的货币表示
     * 
     * @param standardValue 标准货币单位
     * @param targetCulture 目标文化
     * @return 转换后的货币价值
     */
    public int convertToCulture(int standardValue, CultureType targetCulture) {
        double exchangeRate = exchangeRates.getOrDefault(targetCulture, 1.0);
        return (int) Math.round(standardValue / exchangeRate);
    }
    
    /**
     * 获取推荐的找零货币组合
     * 
     * @param changeAmount 找零金额（标准货币单位）
     * @param culture 文化类型
     * @return 推荐的找零货币列表
     */
    public List<CurrencyChange> getOptimalChange(int changeAmount, CultureType culture) {
        List<CurrencyChange> result = new ArrayList<>();
        
        if (culture == CultureType.HUAXIA) {
            // 华夏货币的找零逻辑
            int remaining = changeAmount;
            
            // 金元宝 (10000)
            if (remaining >= 10000) {
                int goldCount = remaining / 10000;
                result.add(new CurrencyChange(HuaxiaCurrencyItem.HuaxiaCurrencyType.GOLD_INGOT, goldCount));
                remaining %= 10000;
            }
            
            // 银两 (100)
            if (remaining >= 100) {
                int silverCount = remaining / 100;
                result.add(new CurrencyChange(HuaxiaCurrencyItem.HuaxiaCurrencyType.SILVER_TAEL, silverCount));
                remaining %= 100;
            }
            
            // 铜钱 (1)
            if (remaining > 0) {
                result.add(new CurrencyChange(HuaxiaCurrencyItem.HuaxiaCurrencyType.COPPER_COIN, remaining));
            }
        }
        
        return result;
    }
    
    /**
     * 货币找零结果类
     */
    public static class CurrencyChange {
        private final Object currencyType;
        private final int count;
        
        public CurrencyChange(Object currencyType, int count) {
            this.currencyType = currencyType;
            this.count = count;
        }
        
        public Object getCurrencyType() {
            return currencyType;
        }
        
        public int getCount() {
            return count;
        }
    }
}
