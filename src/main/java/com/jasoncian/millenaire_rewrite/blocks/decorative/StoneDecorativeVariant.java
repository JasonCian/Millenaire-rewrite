package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.world.level.material.MapColor;

/**
 * 石材装饰方块变体枚举 - 1.20.1现代化实现
 * 
 * 基于legacy BlockDecorativeStone.EnumType重新设计
 * 提供类型安全的变体定义和现代化的配置方式
 * 
 * 对应文化：
 * - GOLD_ORNAMENT: 通用金装饰（诺曼、拜占庭等）
 * - COOKED_BRICK: 烧制砖块（多文化通用）
 * - GALIANITE_BLOCK: Galianite矿物方块（特殊材料）
 */
public enum StoneDecorativeVariant implements DecorativeVariant {
    
    /**
     * 金装饰石块
     * 对应legacy: GOLDORNAMENT(0, "goldOrnament")
     * 用途：高级装饰、贵族建筑、教堂装饰
     */
    GOLD_ORNAMENT("gold_ornament", "gold_ornament", MapColor.GOLD, 3.0F, 9.0F, true),
    
    /**
     * 烧制砖块
     * 对应legacy: COOKEDBRICK(1, "cookedBrick") 
     * 用途：基础建筑、城墙、平民住宅
     */
    COOKED_BRICK("cooked_brick", "cooked_brick", MapColor.COLOR_ORANGE, 2.0F, 6.0F, true),
    
    /**
     * Galianite方块
     * 对应legacy: GALIANITEBLOCK(2, "galianiteBlock")
     * 用途：特殊装饰、魔法建筑、高级工艺
     */
    GALIANITE_BLOCK("galianite_block", "galianite_block", MapColor.COLOR_PURPLE, 5.0F, 12.0F, true);
    
    private final String serializedName;
    private final String translationKey;
    private final MapColor mapColor;
    private final float hardness;
    private final float explosionResistance;
    private final boolean requiresCorrectTool;
    
    /**
     * 构造函数
     * 
     * @param serializedName 序列化名称（JSON/NBT）
     * @param translationKey 本地化键名
     * @param mapColor 地图颜色
     * @param hardness 硬度
     * @param explosionResistance 爆炸抗性
     * @param requiresCorrectTool 是否需要正确工具
     */
    StoneDecorativeVariant(String serializedName, String translationKey, MapColor mapColor, 
                          float hardness, float explosionResistance, boolean requiresCorrectTool) {
        this.serializedName = serializedName;
        this.translationKey = translationKey;
        this.mapColor = mapColor;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.requiresCorrectTool = requiresCorrectTool;
    }
    
    @Override
    public String getSerializedName() {
        return serializedName;
    }
    
    @Override
    public String getTranslationKey() {
        return translationKey;
    }
    
    @Override
    public MapColor getMapColor() {
        return mapColor;
    }
    
    @Override
    public float getHardness() {
        return hardness;
    }
    
    @Override
    public float getExplosionResistance() {
        return explosionResistance;
    }
    
    @Override
    public boolean requiresCorrectToolForDrops() {
        return requiresCorrectTool;
    }
    
    /**
     * 获取完整的本地化键
     * 格式：block.millenaire_rewrite.decorative_stone.{variant}
     */
    public String getFullTranslationKey() {
        return "block.millenaire_rewrite.decorative_stone." + translationKey;
    }
}
