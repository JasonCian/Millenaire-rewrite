package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.world.level.material.MapColor;

/**
 * 木材装饰方块变体枚举 - 1.20.1现代化实现
 * 
 * 基于legacy BlockDecorativeWood.EnumType重新设计
 * 
 * 对应文化建筑风格：
 * - PLAIN_TIMBER_FRAME: 简朴木框架（诺曼风格）
 * - CROSS_TIMBER_FRAME: 十字木框架（高级诺曼建筑）
 * - THATCH: 茅草（多文化通用屋顶）
 * - SERICULTURE: 养蚕架（日式/中式农业建筑）
 */
public enum WoodDecorativeVariant implements DecorativeVariant {
    
    /**
     * 简朴木框架
     * 对应legacy: PLAINTIMBERFRAME(0, "plainTimberFrame")
     * 用途：基础诺曼建筑、农民住宅框架
     */
    PLAIN_TIMBER_FRAME("plain_timber_frame", "plain_timber_frame", MapColor.WOOD, 2.0F, 3.0F, false),
    
    /**
     * 十字木框架  
     * 对应legacy: CROSSTIMBERFRAME(1, "crossTimberFrame")
     * 用途：高级诺曼建筑、商人住宅、工匠作坊
     */
    CROSS_TIMBER_FRAME("cross_timber_frame", "cross_timber_frame", MapColor.WOOD, 2.0F, 3.0F, false),
    
    /**
     * 茅草
     * 对应legacy: THATCH(2, "thatch")
     * 用途：屋顶材料、农村建筑、多文化通用
     */
    THATCH("thatch", "thatch", MapColor.COLOR_YELLOW, 0.6F, 0.2F, false),
    
    /**
     * 养蚕架
     * 对应legacy: SERICULTURE(3, "sericulture") 
     * 用途：日式/中式养蚕业、农业建筑、特色装饰
     */
    SERICULTURE("sericulture", "sericulture", MapColor.WOOD, 2.0F, 3.0F, false);
    
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
    WoodDecorativeVariant(String serializedName, String translationKey, MapColor mapColor, 
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
     * 格式：block.millenaire_rewrite.decorative_wood.{variant}
     */
    public String getFullTranslationKey() {
        return "block.millenaire_rewrite.decorative_wood." + translationKey;
    }
    
    /**
     * 是否为茅草变体
     * 茅草有特殊的燃烧和建筑行为
     */
    public boolean isThatch() {
        return this == THATCH;
    }
    
    /**
     * 是否为养蚕架变体
     * 养蚕架有特殊的功能性
     */
    public boolean isSericulture() {
        return this == SERICULTURE;
    }
}
