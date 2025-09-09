package com.jasoncian.millenaire_rewrite.api.economy;

import org.jetbrains.annotations.ApiStatus;

/**
 * 货币类型枚举
 * 
 * 定义了千年村庄模组中支持的各种货币类型。
 * 不同文化使用不同的货币体系，具有独特的价值和汇率。
 * 
 * <p><strong>货币体系说明：</strong>
 * <ul>
 *   <li><strong>通用货币：</strong>迪纳厄系列（基础、金、银）</li>
 *   <li><strong>华夏货币：</strong>铜钱、银两、金锭</li>
 *   <li><strong>特殊货币：</strong>用于特定交易或文化</li>
 * </ul></p>
 * 
 * <p><strong>价值关系：</strong>
 * 基础单位为迪纳厄（DENIER），其他货币根据汇率换算。</p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public enum CurrencyType {
    
    // ========== 通用货币系列 ==========
    
    /** 迪纳厄 - 基础货币单位 */
    DENIER("denier", "迪纳厄", "基础货币单位，用于日常交易", 1.0),
    
    /** 银迪纳厄 - 中等价值货币 */
    DENIER_ARGENT("denier_argent", "银迪纳厄", "银质货币，价值较高", 12.0),
    
    /** 金迪纳厄 - 高价值货币 */
    DENIER_OR("denier_or", "金迪纳厄", "金质货币，用于大额交易", 160.0),
    
    // ========== 华夏货币系列 ==========
    
    /** 铜钱 - 华夏基础货币 */
    HUAXIA_COPPER("huaxia_copper", "铜钱", "华夏文化的基础货币", 0.8),
    
    /** 银两 - 华夏中等货币 */
    HUAXIA_SILVER("huaxia_silver", "银两", "华夏文化的银质货币", 15.0),
    
    /** 金锭 - 华夏高级货币 */
    HUAXIA_GOLD("huaxia_gold", "金锭", "华夏文化的金质货币", 200.0),
    
    // ========== 其他文化货币 ==========
    
    /** 拜占庭金币 */
    BYZANTINE_SOLIDUS("byzantine_solidus", "拜占庭金币", "拜占庭帝国的金币", 180.0),
    
    /** 日本小判 */
    JAPANESE_KOBAN("japanese_koban", "小判", "日本文化的金质货币", 150.0),
    
    // ========== 特殊货币 ==========
    
    /** 声望点数 - 虚拟货币，用于特殊交易 */
    REPUTATION_POINTS("reputation_points", "声望点数", "用于特殊交易的虚拟货币", 0.1);
    
    /** 货币的内部标识符 */
    private final String id;
    
    /** 货币的显示名称 */
    private final String displayName;
    
    /** 货币的描述信息 */
    private final String description;
    
    /** 相对于基础货币（DENIER）的汇率 */
    private final double exchangeRate;
    
    /**
     * 构造函数
     * 
     * @param id 内部标识符
     * @param displayName 显示名称
     * @param description 描述信息
     * @param exchangeRate 相对于DENIER的汇率
     */
    CurrencyType(String id, String displayName, String description, double exchangeRate) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.exchangeRate = exchangeRate;
    }
    
    /**
     * 获取货币的内部标识符
     * 
     * @return 标识符字符串
     */
    public String getId() {
        return id;
    }
    
    /**
     * 获取货币的显示名称
     * 
     * @return 本地化的显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取货币的描述信息
     * 
     * @return 本地化的描述信息
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取相对于基础货币的汇率
     * 
     * @return 汇率值
     */
    public double getExchangeRate() {
        return exchangeRate;
    }
    
    /**
     * 检查是否为基础货币
     * 
     * @return 如果是基础货币则返回true，否则返回false
     */
    public boolean isBaseCurrency() {
        return this == DENIER;
    }
    
    /**
     * 检查是否为华夏文化货币
     * 
     * @return 如果是华夏货币则返回true，否则返回false
     */
    public boolean isHuaxiaCurrency() {
        return this == HUAXIA_COPPER || this == HUAXIA_SILVER || this == HUAXIA_GOLD;
    }
    
    /**
     * 检查是否为虚拟货币
     * 
     * @return 如果是虚拟货币则返回true，否则返回false
     */
    public boolean isVirtualCurrency() {
        return this == REPUTATION_POINTS;
    }
    
    /**
     * 根据ID字符串获取对应的货币类型枚举值
     * 
     * @param id 要查找的ID
     * @return 对应的枚举值，如果不存在则返回DENIER
     */
    public static CurrencyType fromId(String id) {
        for (CurrencyType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return DENIER; // 默认返回基础货币
    }
}
