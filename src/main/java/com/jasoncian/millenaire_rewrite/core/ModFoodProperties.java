package com.jasoncian.millenaire_rewrite.core;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * 食物属性配置器 - 定义所有Millenaire食物的营养价值和特效
 *
 * 为所有模组食物定义营养值、饱食度和特殊效果
 * 参考Minecraft原版食物平衡设计
 *
 * 功能特性：
 * - 水果类食物配置
 * - 饮品类食物配置  
 * - 各文明特色食物
 * - 状态效果平衡设计
 *
 * @author JasonCian
 * @version 0.1.0-alpha
 */
public class ModFoodProperties {

    // ================ 水果类 ================

    /** 苹果酒苹果 - 基础水果，提供少量饱食度 */
    public static final FoodProperties CIDER_APPLE = new FoodProperties.Builder()
            .nutrition(4) // 2个饥饿值
            .saturationMod(0.3f) // 饱食度修饰符
            .build();

    // ================ 饮品类 ================

    /**
     * 苹果酒 - 诺曼特色饮品，提供少量饱食度和轻微正面效果
     */
    public static final FoodProperties CIDER = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.3f) // 30%概率获得生命恢复
            .build();

    /** 卡尔瓦多斯 - 诺曼烈酒，高饱食度但有副作用 */
    public static final FoodProperties CALVA = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0), 0.8f) // 80%概率获得力量
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 0), 0.4f) // 40%概率获得恶心
            .build();

    /** 清酒 - 日本特色饮品，提供跳跃效果 */
    public static final FoodProperties SAKE = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.JUMP, 480, 1), 1.0f) // 100%概率获得跳跃提升II
            .build();

    /** 葡萄酒 - 拜占庭特色饮品 */
    public static final FoodProperties WINE = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, 0), 0.4f) // 40%概率获得生命提升
            .build();

    /** 玛尔瓦西亚葡萄酒 - 高级葡萄酒，提供抗性效果 */
    public static final FoodProperties MALVASIA_WINE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 480, 0), 1.0f) // 100%概率获得抗性
            .build();

    // ================ 主要菜肴 ================

    /** 蔬菜咖喱 - 印度特色素食菜肴，营养丰富 */
    public static final FoodProperties VEG_CURRY = new FoodProperties.Builder()
            .nutrition(8) // 4个饥饿值
            .saturationMod(0.6f) // 高饱食度
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 100, 0), 0.7f) // 70%概率获得饱食度效果
            .build();

    /** 鸡肉咖喱 - 印度特色肉食菜肴，高营养 */
    public static final FoodProperties MURGH_CURRY = new FoodProperties.Builder()
            .nutrition(10) // 5个饥饿值
            .saturationMod(0.8f) // 极高饱食度
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 0), 0.8f) // 80%概率获得饱食度效果
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 480, 0), 1.0f) // 100%概率获得抗火性
            .build();

    /** 乌冬面 - 日本特色面条 */
    public static final FoodProperties UDON = new FoodProperties.Builder()
            .nutrition(7)
            .saturationMod(0.7f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 150, 0), 0.6f) // 60%概率获得饱食度效果
            .build();

    // ================ 诺曼特色食物 ================

    /** 牛肚 - 诺曼特色食物，高营养但可能有副作用 */
    public static final FoodProperties TRIPES = new FoodProperties.Builder()
            .nutrition(10) // 高营养价值
            .saturationMod(1.0f) // 极高饱食度
            .build();

    /** 血肠 - 诺曼特色食物，高营养 */
    public static final FoodProperties BOUDIN_NOIR = new FoodProperties.Builder()
            .nutrition(10)
            .saturationMod(1.0f)
            .build();

    // ================ 印度甜点 ================

    /** 乳丸 - 提供速度效果 */
    public static final FoodProperties RASGULLA = new FoodProperties.Builder()
            .nutrition(4) // 甜点，中等营养
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 480, 1), 1.0f) // 100%概率获得速度II
            .alwaysEat() // 总是可以食用
            .build();

    // ================ 玛雅食物 ================

    /** 卡卡瓦 - 玛雅特色饮品，提供夜视效果 */
    public static final FoodProperties CACAUHAA = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 480, 0), 1.0f) // 100%概率获得夜视
            .build();

    /** 玉米团 - 玛雅特色食物 */
    public static final FoodProperties MASA = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.6f)
            .build();

    /** 瓦 - 玛雅特色食物，提供挖掘速度效果 */
    public static final FoodProperties WAH = new FoodProperties.Builder()
            .nutrition(10)
            .saturationMod(1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 480, 0), 1.0f) // 100%概率获得急迫
            .build();

    /**
     * 巴尔切 - 玛雅酒精饮品，由树皮制成，提供力量但有混乱效果
     */
    public static final FoodProperties BALCHE = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 0), 0.8f) // 80%概率获得力量
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.3f) // 30%概率获得恶心
            .build();

    /** 西基尔帕 - 玛雅番茄酱，提供抗火性 */
    public static final FoodProperties SIKILPAH = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0), 0.7f) // 70%概率获得抗火性
            .build();

    // ================ 拜占庭食物 ================

    /** 羊奶酪 - 拜占庭特色食物 */
    public static final FoodProperties FETA = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.1f)
            .build();

    /** 烤肉串 - 拜占庭特色食物，提供瞬间治疗 */
    public static final FoodProperties SOUVLAKI = new FoodProperties.Builder()
            .nutrition(10)
            .saturationMod(1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.HEAL, 1, 0), 1.0f) // 100%概率获得瞬间治疗
            .build();

    /**
     * 橄榄 - 地中海橄榄，提供少量营养和缓慢生命恢复
     */
    public static final FoodProperties OLIVES = new FoodProperties.Builder()
            .nutrition(2) // 小零食
            .saturationMod(0.2f) // 低饱食度
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 60, 0), 0.5f) // 50%概率获得短暂恢复
            .alwaysEat() // 总是可以食用，如浆果
            .fast() // 快速食用动画
            .build();

    // ================ 日本海鲜 ================

    /** 烤鱿鱼 - 日本特色食物，提供水下呼吸 */
    public static final FoodProperties IKAYAKI = new FoodProperties.Builder()
            .nutrition(10)
            .saturationMod(1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 480, 2), 1.0f) // 100%概率获得水下呼吸III
            .build();

    // ================ 日本水果 ================

    /** 樱桃 - 日本甜果，提供生命恢复 */
    public static final FoodProperties CHERRIES = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.6f) // 60%概率获得生命恢复
            .build();

    /** 樱花 - 装饰性可食用花朵，提供幸运效果 */
    public static final FoodProperties CHERRY_BLOSSOM = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.LUCK, 300, 0), 1.0f) // 100%概率获得幸运
            .build();

    // ================ 因纽特食物 ================

    /** 生熊肉 - 生熊肉，高营养但可能导致饥饿 */
    public static final FoodProperties BEAR_MEAT_RAW = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f) // 30%概率获得饥饿
            .meat()
            .build();

    /**
     * 熟熊肉 - 熟熊肉，极佳营养和抗寒性
     */
    public static final FoodProperties BEAR_MEAT_COOKED = new FoodProperties.Builder()
            .nutrition(12) // 极高营养价值
            .saturationMod(1.2f) // 极佳饱食度
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0), 1.0f) // 抗寒效果
            .meat()
            .build();

    /** 生狼肉 - 生狼肉 */
    public static final FoodProperties WOLF_MEAT_RAW = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 400, 0), 0.4f) // 40% chance for hunger
            .meat()
            .build();

    /** 熟狼肉 - 熟狼肉，提供群体狩猎效果 */
    public static final FoodProperties WOLF_MEAT_COOKED = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0), 1.0f) // Speed boost
            .meat()
            .build();

    /** 生海鲜 - 生的北极海鲜 */
    public static final FoodProperties SEAFOOD_RAW = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 0.3f) // 30% chance for hunger
            .build();

    /** 熟海鲜 - 熟的北极海鲜，提供水下呼吸 */
    public static final FoodProperties SEAFOOD_COOKED = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0), 1.0f) // Water breathing
            .build();

    /** 因纽特熊肉炖菜 - 丰盛的炖菜，提供保暖和力量 */
    public static final FoodProperties INUIT_BEAR_STEW = new FoodProperties.Builder()
            .nutrition(14) // Excellent nutrition
            .saturationMod(1.4f) // Excellent saturation
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1), 1.0f) // Resistance II
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800, 0), 1.0f) // Strength
            .build();

    /** 因纽特肉类炖菜 - 混合肉类生存炖菜 */
    public static final FoodProperties INUIT_MEATY_STEW = new FoodProperties.Builder()
            .nutrition(12)
            .saturationMod(1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 200, 1), 1.0f) // Saturation II
            .build();

    /** 因纽特土豆炖菜 - 蔬菜类生存食物 */
    public static final FoodProperties INUIT_POTATO_STEW = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 0), 1.0f) // Health boost
            .build();

    // ================ Seljuk Foods ================

    /** 皮德饼 - 土耳其扁面包，提供良好饱食度 */
    public static final FoodProperties PIDE = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.8f)
            .build();

    /** 哈尔瓦 - 土耳其甜点，提供速度提升 */
    public static final FoodProperties HELVA = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0), 1.0f) // Speed boost
            .alwaysEat()
            .build();

    /** 土耳其软糖 - 土耳其软糖，提供跳跃提升 */
    public static final FoodProperties LOKUM = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.2f)
            .effect(() -> new MobEffectInstance(MobEffects.JUMP, 400, 1), 1.0f) // Jump boost II
            .alwaysEat()
            .fast()
            .build();

    /** 酸奶饮料 - 传统酸奶饮品，提供治疗 */
    public static final FoodProperties AYRAN = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0f) // Regeneration
            .alwaysEat()
            .build();

    /** 酸奶 - 发酵乳制品，提供健康提升 */
    public static final FoodProperties YOGURT = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3f)
            .effect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 200, 0), 0.7f) // Health boost
            .build();

    /** 开心果 - 营养坚果，提供经验 */
    public static final FoodProperties PISTACHIOS = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.1f)
            .alwaysEat()
            .fast()
            .build();

    // ================ 建造者模式辅助方法 ================

    /**
     * 创建基础食物属性
     * 
     * @param nutrition  营养值（饥饿点的一半）
     * @param saturation 饱食度修饰符
     * @return 食物属性建造者
     */
    public static FoodProperties.Builder basicFood(int nutrition, float saturation) {
        return new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationMod(saturation);
    }

    /**
     * 创建带有正面效果的食物属性
     * 
     * @param nutrition   营养值
     * @param saturation  饱食度修饰符
     * @param effect      效果实例
     * @param probability 效果概率
     * @return 食物属性建造者
     */
    public static FoodProperties.Builder foodWithEffect(int nutrition, float saturation,
            MobEffectInstance effect, float probability) {
        return basicFood(nutrition, saturation)
                .effect(() -> effect, probability);
    }
}
