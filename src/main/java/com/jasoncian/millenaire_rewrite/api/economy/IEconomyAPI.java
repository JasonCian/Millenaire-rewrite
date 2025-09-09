package com.jasoncian.millenaire_rewrite.api.economy;

import com.jasoncian.millenaire_rewrite.api.village.IVillage;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 经济系统API接口
 * 
 * 提供对千年村庄模组经济系统的访问，包括货币管理、
 * 价格计算、贸易路线等功能。经济系统支持多种货币类型
 * 和文化特色的经济模式。
 * 
 * <p><strong>经济特性：</strong>
 * <ul>
 *   <li>多货币体系（迪纳厄、金迪纳厄、银迪纳厄等）</li>
 *   <li>动态价格调节机制</li>
 *   <li>文化间贸易汇率</li>
 *   <li>供需平衡算法</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public interface IEconomyAPI {
    
    /**
     * 计算物品在指定村庄的价格
     * 
     * @param item 要计算价格的物品
     * @param village 目标村庄
     * @return 物品的价格（以基础货币单位计算）
     */
    int calculatePrice(@NotNull ItemStack item, @NotNull IVillage village);
    
    /**
     * 获取物品的基础价值
     * 
     * @param item 要查询的物品
     * @return 物品的基础价值
     */
    int getBaseValue(@NotNull ItemStack item);
    
    /**
     * 检查村庄是否有足够的货币购买物品
     * 
     * @param village 村庄对象
     * @param totalCost 总费用
     * @return 如果村庄有足够货币则返回true，否则返回false
     */
    boolean canAfford(@NotNull IVillage village, int totalCost);
    
    /**
     * 从村庄扣除指定金额的货币
     * 
     * @param village 村庄对象
     * @param amount 要扣除的金额
     * @return 如果扣除成功则返回true，否则返回false
     */
    boolean deductMoney(@NotNull IVillage village, int amount);
    
    /**
     * 向村庄添加货币
     * 
     * @param village 村庄对象
     * @param amount 要添加的金额
     */
    void addMoney(@NotNull IVillage village, int amount);
    
    /**
     * 获取村庄的当前财富状况
     * 
     * @param village 村庄对象
     * @return 包含各种货币数量的映射
     */
    @NotNull
    Map<CurrencyType, Integer> getVillageWealth(@NotNull IVillage village);
    
    /**
     * 计算两种货币之间的汇率
     * 
     * @param fromCurrency 源货币类型
     * @param toCurrency 目标货币类型
     * @return 汇率值
     */
    double getExchangeRate(@NotNull CurrencyType fromCurrency, 
                          @NotNull CurrencyType toCurrency);
    
    /**
     * 兑换货币
     * 
     * @param village 执行兑换的村庄
     * @param fromCurrency 源货币类型
     * @param toCurrency 目标货币类型
     * @param amount 要兑换的金额
     * @return 兑换后的金额，如果兑换失败则返回0
     */
    int exchangeCurrency(@NotNull IVillage village,
                        @NotNull CurrencyType fromCurrency,
                        @NotNull CurrencyType toCurrency,
                        int amount);
}
