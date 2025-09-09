package com.jasoncian.millenaire_rewrite.item.trade;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 华夏文化贸易商品基类
 * 
 * 华夏文化的贸易商品，包括茶叶、瓷器、丝绸、香料等文化特色商品。
 * 这些商品在村庄贸易系统中具有特殊价值，体现华夏文化的商业传统。
 * 
 * 功能特性：
 * - 不同贸易商品类型和品质等级
 * - 动态价值计算基于稀有度和品质
 * - 文化特色工具提示信息
 * - 特殊贸易加成效果
 * - 村庄声望影响价值
 * 
 * 设计原则：
 * - 体现华夏文化的商业传统和特色商品
 * - 平衡游戏性和文化真实性
 * - 为经济系统提供丰富的贸易选择
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
public class HuaxiaTradeItem extends Item {

    /** 华夏贸易商品类型枚举 */
    public enum HuaxiaTradeType {
        // === 茶叶类 ===
        /** 绿茶叶 - 基础茶叶商品 */
        GREEN_TEA_LEAVES("green_tea_leaves", "绿茶叶", "清香淡雅的绿茶叶，华夏茶文化的基础", 
                        TradeCategory.TEA, TradeRarity.COMMON, 8, 15),
        
        /** 乌龙茶叶 - 中等品质茶叶 */
        OOLONG_TEA_LEAVES("oolong_tea_leaves", "乌龙茶叶", "半发酵工艺制成的乌龙茶叶，香气浓郁", 
                         TradeCategory.TEA, TradeRarity.UNCOMMON, 15, 25),
        
        /** 龙井茶叶 - 高品质茶叶 */
        LONGJING_TEA_LEAVES("longjing_tea_leaves", "龙井茶叶", "杭州龙井，茶中珍品，形美味醇", 
                           TradeCategory.TEA, TradeRarity.RARE, 25, 45),
        
        /** 大红袍茶叶 - 顶级茶叶 */
        DAHONGPAO_TEA_LEAVES("dahongpao_tea_leaves", "大红袍茶叶", "岩茶之王，千年传承的茶中极品", 
                           TradeCategory.TEA, TradeRarity.LEGENDARY, 80, 150),

        // === 瓷器类 ===
        /** 青瓷 - 基础瓷器 */
        CELADON("celadon", "青瓷", "温润如玉的青瓷器皿，华夏瓷器工艺的经典", 
               TradeCategory.PORCELAIN, TradeRarity.COMMON, 12, 22),
        
        /** 白瓷 - 中等品质瓷器 */
        WHITE_PORCELAIN("white_porcelain", "白瓷", "洁白如雪的白瓷制品，工艺精湛", 
                       TradeCategory.PORCELAIN, TradeRarity.UNCOMMON, 20, 35),
        
        /** 青花瓷 - 高品质瓷器 */
        BLUE_WHITE_PORCELAIN("blue_white_porcelain", "青花瓷", "青花点缀的精美瓷器，艺术与实用的完美结合", 
                           TradeCategory.PORCELAIN, TradeRarity.RARE, 35, 65),
        
        /** 斗彩瓷 - 顶级瓷器 */
        DOUCAI_PORCELAIN("doucai_porcelain", "斗彩瓷", "釉下青花与釉上彩绘相结合的顶级瓷器", 
                        TradeCategory.PORCELAIN, TradeRarity.LEGENDARY, 120, 220),

        // === 丝绸类 ===
        /** 生丝 - 基础丝绸原料 */
        RAW_SILK("raw_silk", "生丝", "未经处理的天然蚕丝，丝绸之路的珍贵商品", 
                TradeCategory.SILK, TradeRarity.COMMON, 6, 12),
        
        /** 绢布 - 中等品质丝绸 */
        SILK_FABRIC("silk_fabric", "绢布", "精心织造的丝绸布料，手感光滑细腻", 
                   TradeCategory.SILK, TradeRarity.UNCOMMON, 18, 30),
        
        /** 锦缎 - 高品质丝绸 */
        BROCADE("brocade", "锦缎", "华丽的织锦缎子，宫廷贵族的专用布料", 
               TradeCategory.SILK, TradeRarity.RARE, 40, 70),
        
        /** 云锦 - 顶级丝绸 */
        CLOUD_BROCADE("cloud_brocade", "云锦", "如云似霞的顶级织锦，华夏丝织工艺的巅峰", 
                     TradeCategory.SILK, TradeRarity.LEGENDARY, 150, 280),

        // === 香料类 ===
        /** 八角 - 基础香料 */
        STAR_ANISE("star_anise", "八角", "星形的香料，为食物增添独特香味", 
                  TradeCategory.SPICE, TradeRarity.COMMON, 4, 8),
        
        /** 花椒 - 中等品质香料 */
        SICHUAN_PEPPER("sichuan_pepper", "花椒", "四川特产花椒，麻味独特，川菜必备", 
                      TradeCategory.SPICE, TradeRarity.UNCOMMON, 12, 20),
        
        /** 肉桂 - 高品质香料 */
        CINNAMON("cinnamon", "肉桂", "珍贵的肉桂香料，温补佳品，香气浓郁", 
                TradeCategory.SPICE, TradeRarity.RARE, 25, 45),
        
        /** 沉香 - 顶级香料 */
        AGARWOOD("agarwood", "沉香", "万香之首的沉香，珍贵稀有，香气深邃悠长", 
                TradeCategory.SPICE, TradeRarity.LEGENDARY, 200, 400);

        private final String registryName;
        private final String displayName;
        private final String description;
        private final TradeCategory category;
        private final TradeRarity rarity;
        private final int baseMinValue;
        private final int baseMaxValue;

        HuaxiaTradeType(String registryName, String displayName, String description, 
                       TradeCategory category, TradeRarity rarity, int baseMinValue, int baseMaxValue) {
            this.registryName = registryName;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.rarity = rarity;
            this.baseMinValue = baseMinValue;
            this.baseMaxValue = baseMaxValue;
        }

        public String getRegistryName() { return registryName; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public TradeCategory getCategory() { return category; }
        public TradeRarity getRarity() { return rarity; }
        public int getBaseMinValue() { return baseMinValue; }
        public int getBaseMaxValue() { return baseMaxValue; }
    }

    /** 贸易商品分类 */
    public enum TradeCategory {
        TEA("茶叶", ChatFormatting.GREEN),
        PORCELAIN("瓷器", ChatFormatting.BLUE),
        SILK("丝绸", ChatFormatting.LIGHT_PURPLE),
        SPICE("香料", ChatFormatting.GOLD);

        private final String displayName;
        private final ChatFormatting color;

        TradeCategory(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
    }

    /** 贸易商品稀有度 */
    public enum TradeRarity {
        COMMON("普通", ChatFormatting.WHITE, 1.0f),
        UNCOMMON("良品", ChatFormatting.GREEN, 1.5f),
        RARE("珍品", ChatFormatting.BLUE, 2.0f),
        LEGENDARY("神品", ChatFormatting.GOLD, 3.0f);

        private final String displayName;
        private final ChatFormatting color;
        private final float valueMultiplier;

        TradeRarity(String displayName, ChatFormatting color, float valueMultiplier) {
            this.displayName = displayName;
            this.color = color;
            this.valueMultiplier = valueMultiplier;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
        public float getValueMultiplier() { return valueMultiplier; }
    }

    private final HuaxiaTradeType tradeType;

    /**
     * 构造华夏贸易商品
     * 
     * @param properties 物品属性
     * @param tradeType 贸易商品类型
     */
    public HuaxiaTradeItem(Properties properties, HuaxiaTradeType tradeType) {
        super(properties.stacksTo(64)); // 贸易商品可以堆叠
        this.tradeType = tradeType;
        
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.CORE, 
                "创建华夏贸易商品: {} ({})", tradeType.getDisplayName(), tradeType.getRegistryName());
        }
    }

    /**
     * 获取贸易商品类型
     * 
     * @return 贸易商品类型
     */
    public HuaxiaTradeType getTradeType() {
        return tradeType;
    }

    /**
     * 计算贸易商品的基础价值
     * 
     * @param stack 物品堆
     * @return 基础价值（以铜钱计算）
     */
    public int getBaseValue(ItemStack stack) {
        int stackSize = stack.getCount();
        int unitValue = (tradeType.getBaseMinValue() + tradeType.getBaseMaxValue()) / 2;
        return Math.round(unitValue * tradeType.getRarity().getValueMultiplier() * stackSize);
    }

    /**
     * 计算贸易商品在特定市场中的价值
     * 
     * @param stack 物品堆
     * @param marketDemandMultiplier 市场需求乘数
     * @param cultureBonus 文化加成
     * @return 市场价值
     */
    public int getMarketValue(ItemStack stack, float marketDemandMultiplier, float cultureBonus) {
        float baseValue = getBaseValue(stack);
        return Math.round(baseValue * marketDemandMultiplier * cultureBonus);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加分类信息
        tooltip.add(Component.literal("分类: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(tradeType.getCategory().getDisplayName())
                        .withStyle(tradeType.getCategory().getColor())));

        // 添加稀有度信息
        tooltip.add(Component.literal("品质: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(tradeType.getRarity().getDisplayName())
                        .withStyle(tradeType.getRarity().getColor())));

        // 添加描述
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal(tradeType.getDescription())
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));

        // 添加价值信息
        int baseValue = getBaseValue(stack);
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("基础价值: ")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(baseValue + " 铜钱")
                        .withStyle(ChatFormatting.YELLOW)));

        // 添加使用提示
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("可用于村庄贸易和商店交易")
                .withStyle(ChatFormatting.BLUE));
    }
}
