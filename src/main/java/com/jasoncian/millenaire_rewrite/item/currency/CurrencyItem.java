package com.jasoncian.millenaire_rewrite.item.currency;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 货币物品基类
 * 
 * 定义所有货币物品的通用行为和属性：
 * - 货币价值系统
 * - 文化归属
 * - 堆叠规则
 * - 工具提示信息
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CurrencyItem extends Item {
    
    /** 货币的基础价值（以最小货币单位计算） */
    private final int baseValue;
    
    /** 货币所属的文化类型 */
    private final CultureType cultureType;
    
    /**
     * 创建货币物品
     * 
     * @param properties 物品属性
     * @param baseValue 基础价值
     * @param cultureType 所属文化
     * @param displayName 显示名称（未使用，为了向后兼容保留参数）
     */
    public CurrencyItem(Properties properties, int baseValue, CultureType cultureType, String displayName) {
        super(properties.stacksTo(64)); // 货币物品可以堆叠到64个
        this.baseValue = baseValue;
        this.cultureType = cultureType;
    }
    
    /**
     * 获取货币的基础价值
     * 
     * @return 基础价值
     */
    public int getBaseValue() {
        return baseValue;
    }
    
    /**
     * 获取货币所属的文化类型
     * 
     * @return 文化类型
     */
    public CultureType getCultureType() {
        return cultureType;
    }
    
    /**
     * 获取物品栈的总价值
     * 
     * @param stack 物品栈
     * @return 总价值
     */
    public int getTotalValue(ItemStack stack) {
        return baseValue * stack.getCount();
    }
    
    /**
     * 检查是否为货币物品
     * 
     * @param stack 物品栈
     * @return 是否为货币物品
     */
    public static boolean isCurrency(ItemStack stack) {
        return stack.getItem() instanceof CurrencyItem;
    }
    
    /**
     * 获取物品栈的货币价值
     * 
     * @param stack 物品栈
     * @return 货币价值，如果不是货币物品则返回0
     */
    public static int getCurrencyValue(ItemStack stack) {
        if (stack.getItem() instanceof CurrencyItem currencyItem) {
            return currencyItem.getTotalValue(stack);
        }
        return 0;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加货币价值信息
        tooltip.add(Component.literal("价值: " + baseValue)
                .withStyle(ChatFormatting.GOLD));
        
        if (stack.getCount() > 1) {
            tooltip.add(Component.literal("总价值: " + getTotalValue(stack))
                    .withStyle(ChatFormatting.YELLOW));
        }
        
        // 添加文化信息
        tooltip.add(Component.translatable(cultureType.getTranslationKey())
                .withStyle(ChatFormatting.GRAY));
        
        // 添加使用说明
        tooltip.add(Component.literal("可用于村庄贸易")
                .withStyle(ChatFormatting.BLUE));
    }
}
