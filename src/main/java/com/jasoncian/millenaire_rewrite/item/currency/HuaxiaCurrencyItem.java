package com.jasoncian.millenaire_rewrite.item.currency;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;

/**
 * 华夏货币物品类
 * 
 * 实现华夏文化的传统货币体系：
 * - 铜钱（基础货币单位）
 * - 银两（中等价值货币） 
 * - 金元宝（高价值货币）
 * 
 * 华夏货币系统的价值比例：
 * - 1 金元宝 = 100 银两 = 10000 铜钱
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaCurrencyItem extends CurrencyItem {
    
    /** 华夏货币类型枚举 */
    public enum HuaxiaCurrencyType {
        /** 铜钱 - 基础货币单位 */
        COPPER_COIN(1, "铜钱"),
        
        /** 银两 - 中等价值货币 */
        SILVER_TAEL(100, "银两"),
        
        /** 金元宝 - 高价值货币 */
        GOLD_INGOT(10000, "金元宝");
        
        private final int value;
        private final String displayName;
        
        HuaxiaCurrencyType(int value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /** 华夏货币类型 */
    private final HuaxiaCurrencyType currencyType;
    
    /**
     * 创建华夏货币物品
     * 
     * @param properties 物品属性
     * @param currencyType 华夏货币类型
     */
    public HuaxiaCurrencyItem(Properties properties, HuaxiaCurrencyType currencyType) {
        super(properties, currencyType.getValue(), CultureType.HUAXIA, currencyType.getDisplayName());
        this.currencyType = currencyType;
    }
    
    /**
     * 获取华夏货币类型
     * 
     * @return 华夏货币类型
     */
    public HuaxiaCurrencyType getHuaxiaCurrencyType() {
        return currencyType;
    }
    
    /**
     * 检查是否为华夏货币
     * 
     * @param item 物品
     * @return 是否为华夏货币
     */
    public static boolean isHuaxiaCurrency(Object item) {
        return item instanceof HuaxiaCurrencyItem;
    }
}
