package com.jasoncian.millenaire_rewrite.item.weapon;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.init.ModTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 华夏文化武器物品类
 * 
 * 实现华夏文化的传统武器体系：
 * - 冷兵器：剑、刀、枪、戟、弓、弩等
 * - 防具：盔甲、盾牌等
 * - 特殊武器：暗器、机关等
 * 
 * 华夏武器的特点：
 * - 工艺精良：注重锻造工艺和装饰
 * - 类型丰富：十八般武艺，各有所长
 * - 文化内涵：承载深厚的武术文化
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaWeaponItem extends SwordItem {
    
    /** 华夏武器类型枚举 */
    public enum HuaxiaWeaponType {
        /** 剑类 - 君子之兵 */
        JIAN("剑", "华夏传统直剑，君子佩剑", WeaponCategory.SWORD, 1.0f, 0.2f),
        DAO("刀", "华夏传统单刃武器，实战利器", WeaponCategory.SWORD, 1.2f, -0.1f),
        
        /** 长兵器类 */
        QIANG("枪", "华夏长兵器之王", WeaponCategory.SPEAR, 0.8f, -0.3f),
        JI("戟", "华夏古代重型长兵器", WeaponCategory.SPEAR, 1.5f, -0.4f),
        
        /** 远程武器类 */
        GONG("弓", "华夏传统弓箭", WeaponCategory.RANGED, 0.3f, 0.1f),
        NU("弩", "华夏机械弓弩", WeaponCategory.RANGED, 0.6f, -0.2f),
        
        /** 特殊武器类 */
        SHUANG_DAO("双刀", "成对使用的短刀", WeaponCategory.DUAL, 0.9f, 0.3f),
        BIAN("鞭", "华夏软兵器", WeaponCategory.SPECIAL, 0.7f, 0.4f),
        
        /** 暗器类 */
        FEI_DAO("飞刀", "投掷暗器", WeaponCategory.THROWING, 0.5f, 0.5f),
        DARTS("飞镖", "小型投掷武器", WeaponCategory.THROWING, 0.3f, 0.8f);
        
        private final String displayName;
        private final String description;
        private final WeaponCategory category;
        private final float damageModifier;
        private final float speedModifier;
        
        HuaxiaWeaponType(String displayName, String description, WeaponCategory category,
                        float damageModifier, float speedModifier) {
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.damageModifier = damageModifier;
            this.speedModifier = speedModifier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public WeaponCategory getCategory() {
            return category;
        }
        
        public float getDamageModifier() {
            return damageModifier;
        }
        
        public float getSpeedModifier() {
            return speedModifier;
        }
    }
    
    /** 武器分类枚举 */
    public enum WeaponCategory {
        SWORD("剑类", "近战单手武器"),
        SPEAR("长兵", "长柄武器"),
        RANGED("远程", "远程攻击武器"),
        DUAL("双兵", "双手武器"),
        SPECIAL("奇门", "特殊武器"),
        THROWING("暗器", "投掷武器");
        
        private final String displayName;
        private final String description;
        
        WeaponCategory(String displayName, String description) {
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
    
    /** 华夏武器类型 */
    private final HuaxiaWeaponType weaponType;
    
    /** 武器品质等级 */
    private final WeaponQuality quality;
    
    /**
     * 武器品质等级枚举
     */
    public enum WeaponQuality {
        COMMON("普通", ChatFormatting.GRAY, 1.0f),
        FINE("精良", ChatFormatting.GREEN, 1.2f),
        MASTERWORK("精品", ChatFormatting.BLUE, 1.5f),
        LEGENDARY("传说", ChatFormatting.LIGHT_PURPLE, 2.0f),
        ARTIFACT("神器", ChatFormatting.GOLD, 3.0f);
        
        private final String displayName;
        private final ChatFormatting color;
        private final float qualityModifier;
        
        WeaponQuality(String displayName, ChatFormatting color, float qualityModifier) {
            this.displayName = displayName;
            this.color = color;
            this.qualityModifier = qualityModifier;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public ChatFormatting getColor() {
            return color;
        }
        
        public float getQualityModifier() {
            return qualityModifier;
        }
    }
    
    /**
     * 创建华夏武器物品
     * 
     * @param tier 武器材质等级
     * @param attackDamageIn 基础攻击伤害
     * @param attackSpeedIn 基础攻击速度
     * @param properties 物品属性
     * @param weaponType 华夏武器类型
     * @param quality 武器品质
     */
    public HuaxiaWeaponItem(Tier tier, int attackDamageIn, float attackSpeedIn,
                           Properties properties, HuaxiaWeaponType weaponType,
                           WeaponQuality quality) {
        super(tier, 
              Math.round(attackDamageIn * weaponType.getDamageModifier() * quality.getQualityModifier()), 
              attackSpeedIn + weaponType.getSpeedModifier(), 
              properties);
        this.weaponType = weaponType;
        this.quality = quality;
    }
    
    /**
     * 创建华夏武器物品（普通品质）
     * 
     * @param tier 武器材质等级
     * @param attackDamageIn 基础攻击伤害
     * @param attackSpeedIn 基础攻击速度
     * @param properties 物品属性
     * @param weaponType 华夏武器类型
     */
    public HuaxiaWeaponItem(Tier tier, int attackDamageIn, float attackSpeedIn,
                           Properties properties, HuaxiaWeaponType weaponType) {
        this(tier, attackDamageIn, attackSpeedIn, properties, weaponType, WeaponQuality.COMMON);
    }
    
    /**
     * 获取华夏武器类型
     * 
     * @return 华夏武器类型
     */
    public HuaxiaWeaponType getHuaxiaWeaponType() {
        return weaponType;
    }
    
    /**
     * 获取武器品质
     * 
     * @return 武器品质
     */
    public WeaponQuality getWeaponQuality() {
        return quality;
    }
    
    /**
     * 获取武器所属的文化类型
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
        
        // 添加武器类型信息
        tooltip.add(Component.literal("类型: " + weaponType.getDisplayName() + 
                                    " (" + weaponType.getCategory().getDisplayName() + ")")
                .withStyle(ChatFormatting.GOLD));
        
        // 添加武器描述
        tooltip.add(Component.literal(weaponType.getDescription())
                .withStyle(ChatFormatting.GRAY));
        
        // 添加品质信息
        tooltip.add(Component.literal("品质: " + quality.getDisplayName())
                .withStyle(quality.getColor()));
        
        // 添加文化标识
        tooltip.add(Component.literal("文化: 华夏")
                .withStyle(ChatFormatting.YELLOW));
        
        // 添加材质信息
        String tierName = ModTiers.getDisplayName(this.getTier());
        tooltip.add(Component.literal("材质: " + tierName)
                .withStyle(ChatFormatting.AQUA));
        
        // 添加特殊属性信息
        if (quality != WeaponQuality.COMMON) {
            tooltip.add(Component.literal("品质加成: " + 
                                        String.format("%.0f%%", (quality.getQualityModifier() - 1) * 100))
                    .withStyle(ChatFormatting.GREEN));
        }
    }
    
    /**
     * 检查是否为华夏武器
     * 
     * @param stack 物品栈
     * @return 是否为华夏武器
     */
    public static boolean isHuaxiaWeapon(ItemStack stack) {
        return stack.getItem() instanceof HuaxiaWeaponItem;
    }
    
    /**
     * 获取华夏武器的类型
     * 
     * @param stack 物品栈
     * @return 华夏武器类型，如果不是华夏武器则返回null
     */
    @Nullable
    public static HuaxiaWeaponType getHuaxiaWeaponType(ItemStack stack) {
        if (stack.getItem() instanceof HuaxiaWeaponItem huaxiaWeapon) {
            return huaxiaWeapon.getHuaxiaWeaponType();
        }
        return null;
    }
    
    /**
     * 获取华夏武器的品质
     * 
     * @param stack 物品栈
     * @return 武器品质，如果不是华夏武器则返回null
     */
    @Nullable
    public static WeaponQuality getHuaxiaWeaponQuality(ItemStack stack) {
        if (stack.getItem() instanceof HuaxiaWeaponItem huaxiaWeapon) {
            return huaxiaWeapon.getWeaponQuality();
        }
        return null;
    }
}
