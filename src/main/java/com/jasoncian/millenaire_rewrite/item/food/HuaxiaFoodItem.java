package com.jasoncian.millenaire_rewrite.item.food;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 华夏文化食物物品类
 * 
 * 实现华夏文化的传统食物体系：
 * - 主食：米饭、面条、包子、饺子等
 * - 菜肴：炒菜、煲汤、烤肉等
 * - 小食：点心、糖果、坚果等
 * - 饮品：茶水、酒类、汤品等
 * - 药膳：具有特殊效果的食疗食品
 * 
 * 华夏食物的特点：
 * - 营养丰富：不同食物提供不同的饱食度和饱和度
 * - 文化内涵：体现华夏饮食文化的博大精深
 * - 特殊效果：部分食物具有增益效果
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaFoodItem extends Item {
    
    /** 华夏食物类型枚举 */
    public enum HuaxiaFoodType {
        // ===== 主食类 =====
        RICE("米饭", "华夏人的主要主食", FoodCategory.STAPLE, "制作工艺：蒸煮"),
        NOODLES("面条", "北方人喜爱的面食", FoodCategory.STAPLE, "制作工艺：擀制"),
        BAOZI("包子", "有馅的蒸制面食", FoodCategory.STAPLE, "制作工艺：发酵蒸制"),
        JIAOZI("饺子", "传统节庆食品", FoodCategory.STAPLE, "制作工艺：包制水煮"),
        MANTOU("馒头", "北方基础主食", FoodCategory.STAPLE, "制作工艺：发酵蒸制"),
        
        // ===== 菜肴类 =====
        MAPO_TOFU("麻婆豆腐", "四川经典菜肴", FoodCategory.DISH, "口味：麻辣鲜香"),
        KUNG_PAO_CHICKEN("宫保鸡丁", "宫廷传统菜", FoodCategory.DISH, "口味：酸甜微辣"),
        SWEET_SOUR_PORK("糖醋里脊", "酸甜经典菜", FoodCategory.DISH, "口味：酸甜"),
        BRAISED_PORK("红烧肉", "家常经典菜", FoodCategory.DISH, "口味：香甜"),
        
        // ===== 小食类 =====
        MOONCAKE("月饼", "中秋传统糕点", FoodCategory.SNACK, "节庆：中秋节"),
        TANGYUAN("汤圆", "元宵节传统小食", FoodCategory.SNACK, "节庆：元宵节"),
        ZONGZI("粽子", "端午节传统食品", FoodCategory.SNACK, "节庆：端午节"),
        SESAME_BALL("芝麻球", "传统油炸点心", FoodCategory.SNACK, "制作工艺：油炸"),
        
        // ===== 饮品类 =====
        GREEN_TEA("绿茶", "华夏传统茶饮", FoodCategory.BEVERAGE, "功效：清热解毒"),
        OOLONG_TEA("乌龙茶", "半发酵茶类", FoodCategory.BEVERAGE, "功效：消脂解腻"),
        HUANGJIU("黄酒", "传统米酒", FoodCategory.BEVERAGE, "功效：温阳活血"),
        HERBAL_SOUP("药膳汤", "滋补汤品", FoodCategory.BEVERAGE, "功效：滋补强身"),
        
        // ===== 药膳类 =====
        GINSENG_SOUP("人参汤", "滋补药膳", FoodCategory.MEDICINAL, "功效：大补元气"),
        BIRD_NEST("燕窝", "珍贵滋补品", FoodCategory.MEDICINAL, "功效：养阴润燥"),
        CORDYCEPS("冬虫夏草", "名贵药材", FoodCategory.MEDICINAL, "功效：补肺益肾"),
        GOJI_TEA("枸杞茶", "养生茶饮", FoodCategory.MEDICINAL, "功效：明目养肝");
        
        private final String displayName;
        private final String description;
        private final FoodCategory category;
        private final String specialInfo;
        
        HuaxiaFoodType(String displayName, String description, FoodCategory category, String specialInfo) {
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.specialInfo = specialInfo;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public FoodCategory getCategory() {
            return category;
        }
        
        public String getSpecialInfo() {
            return specialInfo;
        }
    }
    
    /** 食物分类枚举 */
    public enum FoodCategory {
        STAPLE("主食", "提供基础营养的主要食物"),
        DISH("菜肴", "丰富口味的配菜"),
        SNACK("小食", "休闲时享用的点心"),
        BEVERAGE("饮品", "解渴或保健的液体食物"),
        MEDICINAL("药膳", "具有特殊功效的食疗食品");
        
        private final String displayName;
        private final String description;
        
        FoodCategory(String displayName, String description) {
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
    
    /** 华夏食物类型 */
    private final HuaxiaFoodType foodType;
    
    /** 制作难度等级 */
    private final CraftingDifficulty difficulty;
    
    /**
     * 制作难度枚举
     */
    public enum CraftingDifficulty {
        SIMPLE("简单", ChatFormatting.GREEN, "家常制作"),
        MODERATE("一般", ChatFormatting.YELLOW, "需要一定技巧"),
        COMPLEX("复杂", ChatFormatting.GOLD, "需要专业技艺"),
        MASTER("大师", ChatFormatting.RED, "需要精湛厨艺"),
        LEGENDARY("传说", ChatFormatting.LIGHT_PURPLE, "失传秘方");
        
        private final String displayName;
        private final ChatFormatting color;
        private final String description;
        
        CraftingDifficulty(String displayName, ChatFormatting color, String description) {
            this.displayName = displayName;
            this.color = color;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public ChatFormatting getColor() {
            return color;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 创建华夏食物物品
     * 
     * @param properties 物品属性
     * @param foodType 华夏食物类型
     * @param difficulty 制作难度
     */
    public HuaxiaFoodItem(Properties properties, HuaxiaFoodType foodType, CraftingDifficulty difficulty) {
        super(properties.food(createFoodProperties(foodType)));
        this.foodType = foodType;
        this.difficulty = difficulty;
    }
    
    /**
     * 创建华夏食物物品（简单难度）
     * 
     * @param properties 物品属性
     * @param foodType 华夏食物类型
     */
    public HuaxiaFoodItem(Properties properties, HuaxiaFoodType foodType) {
        this(properties, foodType, CraftingDifficulty.SIMPLE);
    }
    
    /**
     * 根据食物类型创建食物属性
     * 
     * @param foodType 华夏食物类型
     * @return 食物属性
     */
    private static FoodProperties createFoodProperties(HuaxiaFoodType foodType) {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        
        switch (foodType) {
            // 主食类 - 高饱食度，中等饱和度
            case RICE -> builder.nutrition(6).saturationMod(0.8f);
            case NOODLES -> builder.nutrition(8).saturationMod(0.7f);
            case BAOZI -> builder.nutrition(10).saturationMod(0.9f);
            case JIAOZI -> builder.nutrition(12).saturationMod(1.0f);
            case MANTOU -> builder.nutrition(5).saturationMod(0.6f);
            
            // 菜肴类 - 中等饱食度，高饱和度
            case MAPO_TOFU -> {
                builder.nutrition(7).saturationMod(1.2f);
                // 麻辣效果：短暂的速度提升
                builder.effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0), 1.0f);
            }
            case KUNG_PAO_CHICKEN -> {
                builder.nutrition(9).saturationMod(1.4f);
                // 宫保效果：短暂的力量提升
                builder.effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0), 1.0f);
            }
            case SWEET_SOUR_PORK -> builder.nutrition(8).saturationMod(1.1f);
            case BRAISED_PORK -> {
                builder.nutrition(10).saturationMod(1.5f);
                // 滋补效果：短暂的生命恢复
                builder.effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 300, 0), 1.0f);
            }
            
            // 小食类 - 低到中等饱食度，快速恢复
            case MOONCAKE -> builder.nutrition(4).saturationMod(0.8f).fast();
            case TANGYUAN -> builder.nutrition(3).saturationMod(0.6f).fast();
            case ZONGZI -> builder.nutrition(5).saturationMod(0.7f);
            case SESAME_BALL -> builder.nutrition(3).saturationMod(0.5f).fast();
            
            // 饮品类 - 低饱食度，特殊效果
            case GREEN_TEA -> {
                builder.nutrition(1).saturationMod(0.1f).fast();
                // 清热解毒：清除负面效果
                builder.effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 0.8f);
            }
            case OOLONG_TEA -> {
                builder.nutrition(1).saturationMod(0.1f).fast();
                // 消脂解腻：提升挖掘速度
                builder.effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0), 1.0f);
            }
            case HUANGJIU -> {
                builder.nutrition(2).saturationMod(0.3f);
                // 温阳活血：抗性提升但视觉模糊
                builder.effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 0), 1.0f);
                builder.effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 600, 0), 0.3f);
            }
            case HERBAL_SOUP -> {
                builder.nutrition(4).saturationMod(0.8f);
                // 滋补强身：综合属性提升
                builder.effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 0), 1.0f);
            }
            
            // 药膳类 - 高效果，珍贵
            case GINSENG_SOUP -> {
                builder.nutrition(8).saturationMod(1.8f);
                // 大补元气：全面属性提升
                builder.effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 6000, 1), 1.0f);
                builder.effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 1200, 1), 1.0f);
                builder.effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 0), 1.0f);
            }
            case BIRD_NEST -> {
                builder.nutrition(6).saturationMod(2.0f);
                // 养阴润燥：长效生命恢复
                builder.effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 2400, 0), 1.0f);
                builder.effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1), 1.0f);
            }
            case CORDYCEPS -> {
                builder.nutrition(4).saturationMod(1.5f);
                // 补肺益肾：呼吸和耐力强化
                builder.effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 6000, 0), 1.0f);
                builder.effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 0), 1.0f);
            }
            case GOJI_TEA -> {
                builder.nutrition(2).saturationMod(0.4f).fast();
                // 明目养肝：夜视和解毒
                builder.effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0), 1.0f);
            }
        }
        
        return builder.build();
    }
    
    /**
     * 获取华夏食物类型
     * 
     * @return 华夏食物类型
     */
    public HuaxiaFoodType getHuaxiaFoodType() {
        return foodType;
    }
    
    /**
     * 获取制作难度
     * 
     * @return 制作难度
     */
    public CraftingDifficulty getCraftingDifficulty() {
        return difficulty;
    }
    
    /**
     * 获取食物所属的文化类型
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
        
        // 添加食物类型信息
        tooltip.add(Component.literal("类型: " + foodType.getDisplayName() + 
                                    " (" + foodType.getCategory().getDisplayName() + ")")
                .withStyle(ChatFormatting.GOLD));
        
        // 添加食物描述
        tooltip.add(Component.literal(foodType.getDescription())
                .withStyle(ChatFormatting.GRAY));
        
        // 添加制作难度
        tooltip.add(Component.literal("制作难度: " + difficulty.getDisplayName())
                .withStyle(difficulty.getColor()));
        
        // 添加特殊信息
        tooltip.add(Component.literal(foodType.getSpecialInfo())
                .withStyle(ChatFormatting.AQUA));
        
        // 添加文化标识
        tooltip.add(Component.literal("文化: 华夏")
                .withStyle(ChatFormatting.YELLOW));
        
        // 添加营养信息
        FoodProperties foodProperties = this.getFoodProperties(stack, null);
        if (foodProperties != null) {
            tooltip.add(Component.literal(String.format("营养: %d 饱食度, %.1f 饱和度", 
                                                      foodProperties.getNutrition(),
                                                      foodProperties.getSaturationModifier()))
                    .withStyle(ChatFormatting.GREEN));
        }
    }
    
    /**
     * 检查是否为华夏食物
     * 
     * @param stack 物品栈
     * @return 是否为华夏食物
     */
    public static boolean isHuaxiaFood(ItemStack stack) {
        return stack.getItem() instanceof HuaxiaFoodItem;
    }
    
    /**
     * 获取华夏食物的类型
     * 
     * @param stack 物品栈
     * @return 华夏食物类型，如果不是华夏食物则返回null
     */
    @Nullable
    public static HuaxiaFoodType getHuaxiaFoodType(ItemStack stack) {
        if (stack.getItem() instanceof HuaxiaFoodItem huaxiaFood) {
            return huaxiaFood.getHuaxiaFoodType();
        }
        return null;
    }
}
