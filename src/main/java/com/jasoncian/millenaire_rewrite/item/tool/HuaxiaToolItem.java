package com.jasoncian.millenaire_rewrite.item.tool;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.init.ModTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 华夏文化工具物品基类
 * 
 * 定义华夏文化特色工具的通用属性和行为：
 * - 传统农具（锄头、镰刀、连枷等）
 * - 手工业工具（凿子、锯子、刨子等）
 * - 特殊工具（算盘、竹简等）
 * 
 * 华夏工具的特点：
 * - 材质多样：木、竹、铁、铜等传统材料
 * - 功能性强：针对特定职业和任务设计
 * - 文化标识：体现华夏文明的智慧和工艺水平
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaToolItem extends TieredItem {
    
    /** 华夏工具类型枚举 */
    public enum HuaxiaToolType {
        /** 农具类 */
        HOE("锄头", "用于耕地的传统农具"),
        SICKLE("镰刀", "用于收割作物的弯刀"),
        FLAIL("连枷", "用于打谷的传统工具"),
        PLOW("犁", "用于深耕的大型农具"),
        
        /** 手工业工具类 */
        CHISEL("凿子", "用于雕刻和凿削的精细工具"),
        SAW("锯子", "用于切割木材的工具"),
        PLANE("刨子", "用于刨平木材表面的工具"),
        HAMMER("锤子", "用于锻造和敲击的工具"),
        
        /** 特殊工具类 */
        ABACUS("算盘", "用于计算的传统工具"),
        BAMBOO_SCROLL("竹简", "用于记录的文字载体"),
        COMPASS("罗盘", "用于导航和风水的工具"),
        INKSTONE("砚台", "用于磨墨写字的文房用具");
        
        private final String displayName;
        private final String description;
        
        HuaxiaToolType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /** 华夏工具类型 */
    private final HuaxiaToolType toolType;
    
    /** 工具的效率加成 */
    private final float efficiencyBonus;
    
    /** 工具的耐久度加成 */
    private final int durabilityBonus;
    
    /**
     * 创建华夏工具物品
     * 
     * @param tier 工具等级
     * @param attackDamageIn 攻击伤害
     * @param attackSpeedIn 攻击速度
     * @param properties 物品属性
     * @param toolType 华夏工具类型
     * @param efficiencyBonus 效率加成
     * @param durabilityBonus 耐久度加成
     */
    public HuaxiaToolItem(Tier tier, int attackDamageIn, float attackSpeedIn, 
                          Properties properties, HuaxiaToolType toolType,
                          float efficiencyBonus, int durabilityBonus) {
        super(tier, properties.stacksTo(1));
        this.toolType = toolType;
        this.efficiencyBonus = efficiencyBonus;
        this.durabilityBonus = durabilityBonus;
    }
    
    /**
     * 创建华夏工具物品（简化构造函数）
     * 
     * @param tier 工具等级
     * @param properties 物品属性
     * @param toolType 华夏工具类型
     */
    public HuaxiaToolItem(Tier tier, Properties properties, HuaxiaToolType toolType) {
        this(tier, 0, 0.0f, properties, toolType, 1.0f, 0);
    }
    
    /**
     * 获取华夏工具类型
     * 
     * @return 华夏工具类型
     */
    public HuaxiaToolType getHuaxiaToolType() {
        return toolType;
    }
    
    /**
     * 获取效率加成
     * 
     * @return 效率加成倍数
     */
    public float getEfficiencyBonus() {
        return efficiencyBonus;
    }
    
    /**
     * 获取耐久度加成
     * 
     * @return 耐久度加成数值
     */
    public int getDurabilityBonus() {
        return durabilityBonus;
    }
    
    /**
     * 获取工具所属的文化类型
     * 
     * @return 华夏文化类型
     */
    public CultureType getCultureType() {
        return CultureType.HUAXIA;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, 
                               List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        // 添加工具类型信息
        tooltip.add(Component.literal("类型: " + toolType.getDisplayName())
                .withStyle(ChatFormatting.GOLD));
        
        // 添加工具描述
        tooltip.add(Component.literal(toolType.getDescription())
                .withStyle(ChatFormatting.GRAY));
        
        // 添加文化标识
        tooltip.add(Component.literal("文化: 华夏")
                .withStyle(ChatFormatting.YELLOW));
        
        // 添加特殊属性信息
        if (efficiencyBonus != 1.0f) {
            tooltip.add(Component.literal("效率: " + String.format("%.1f%%", efficiencyBonus * 100))
                    .withStyle(ChatFormatting.GREEN));
        }
        
        if (durabilityBonus > 0) {
            tooltip.add(Component.literal("耐久度加成: +" + durabilityBonus)
                    .withStyle(ChatFormatting.BLUE));
        }
    }
    
    /**
     * 检查是否为华夏工具
     * 
     * @param stack 物品栈
     * @return 是否为华夏工具
     */
    public static boolean isHuaxiaTool(ItemStack stack) {
        return stack.getItem() instanceof HuaxiaToolItem;
    }
    
    /**
     * 获取华夏工具的类型
     * 
     * @param stack 物品栈
     * @return 华夏工具类型，如果不是华夏工具则返回null
     */
    @Nullable
    public static HuaxiaToolType getHuaxiaToolType(ItemStack stack) {
        if (stack.getItem() instanceof HuaxiaToolItem huaxiaTool) {
            return huaxiaTool.getHuaxiaToolType();
        }
        return null;
    }
}
