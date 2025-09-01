package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.world.level.material.MapColor;

/**
 * 土质装饰方块变体枚举 - 1.20.1现代化实现
 * 
 * 基于legacy BlockDecorativeEarth.EnumType重新设计
 * 
 * 对应文化建筑材料：
 * - DIRT_WALL: 土墙（基础建筑、临时建筑）
 * - DRIED_BRICK: 风干砖（印度风格、热带地区建筑）
 */
public enum EarthDecorativeVariant implements DecorativeVariant {
    
    /**
     * 土墙
     * 对应legacy: DIRTWALL(0, "dirtWall")
     * 用途：基础建筑、临时结构、农民住宅
     */
    DIRT_WALL("dirt_wall", "dirt_wall", MapColor.DIRT, 0.5F, 2.5F, false),
    
    /**
     * 风干砖
     * 对应legacy: DRIEDBRICK(1, "driedBrick")
     * 用途：印度风格建筑、热带地区、宗教建筑
     */
    DRIED_BRICK("dried_brick", "dried_brick", MapColor.COLOR_ORANGE, 1.5F, 4.0F, false);
    
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
    EarthDecorativeVariant(String serializedName, String translationKey, MapColor mapColor, 
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
     * 格式：block.millenaire_rewrite.decorative_earth.{variant}
     */
    public String getFullTranslationKey() {
        return "block.millenaire_rewrite.decorative_earth." + translationKey;
    }
    
    /**
     * 是否为土墙变体
     * 土墙有特殊的脆弱性
     */
    public boolean isDirtWall() {
        return this == DIRT_WALL;
    }
    
    /**
     * 是否为风干砖变体
     * 风干砖更适合干燥气候
     */
    public boolean isDriedBrick() {
        return this == DRIED_BRICK;
    }
}
