package com.jasoncian.millenaire_rewrite.blocks.system;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 建筑材料枚举 - 定义所有方块的材料属性
 *
 * 统一管理所有建筑方块的物理属性，包括硬度、爆炸抗性、
 * 声音类型、地图颜色等。替代原版复杂的材料系统。
 *
 * 功能特性：
 * - 统一的材料属性管理
 * - 文化特色材料支持
 * - 现代化属性配置
 * - 扩展友好的设计
 *
 * @author JasonCian
 * @version 1.0.0
 */
public enum BuildingMaterial {
    
    // ================ 石质材料 ================
    
    /** 普通石质材料 - 适用于大部分石制建筑方块 */
    STONE(
        2.0f,           // 硬度
        6.0f,           // 爆炸抗性
        SoundType.STONE, // 声音类型
        MapColor.STONE, // 地图颜色
        true,           // 需要正确工具
        PushReaction.NORMAL // 推动反应
    ),
    
    /** 雕刻石材 - 适用于装饰性石制方块 */
    CARVED_STONE(
        1.5f,
        5.0f,
        SoundType.STONE,
        MapColor.STONE,
        true,
        PushReaction.NORMAL
    ),
    
    /** 金装饰材料 - 适用于高级装饰方块 */
    GOLD_ORNAMENT(
        3.0f,
        9.0f,
        SoundType.STONE,
        MapColor.GOLD,
        true,
        PushReaction.NORMAL
    ),
    
    /** Galianite材料 - 适用于特殊魔法方块 */
    GALIANITE(
        5.0f,
        12.0f,
        SoundType.METAL,
        MapColor.COLOR_PURPLE,
        true,
        PushReaction.NORMAL
    ),
    
    /** 砂岩材料 - 适用于各种砂岩方块 */
    SANDSTONE(
        0.8f,
        0.8f,
        SoundType.STONE,
        MapColor.SAND,
        true,
        PushReaction.NORMAL
    ),
    
    /** 瓦片材料 - 适用于各种瓦片方块 */
    TILES(
        2.0f,
        10.0f,
        SoundType.STONE,
        MapColor.STONE,
        true,
        PushReaction.NORMAL
    ),
    
    // ================ 木质材料 ================
    
    /** 普通木质材料 - 适用于大部分木制建筑方块 */
    WOOD(
        2.0f,
        3.0f,
        SoundType.WOOD,
        MapColor.WOOD,
        false,
        PushReaction.NORMAL
    ),
    
    /** 木框架材料 - 适用于框架结构方块 */
    TIMBER_FRAME(
        1.5f,
        2.5f,
        SoundType.WOOD,
        MapColor.WOOD,
        false,
        PushReaction.NORMAL
    ),
    
    /** 茅草材料 - 适用于茅草屋顶等 */
    THATCH(
        0.5f,
        0.5f,
        SoundType.GRASS,
        MapColor.COLOR_YELLOW,
        false,
        PushReaction.NORMAL
    ),
    
    // ================ 泥土材料 ================
    
    /** 泥砖材料 - 适用于泥砖方块 */
    MUD_BRICK(
        1.5f,
        6.0f,
        SoundType.STONE,
        MapColor.DIRT,
        true,
        PushReaction.NORMAL
    ),
    
    /** 风干砖材料 - 适用于风干砖方块 */
    DRIED_BRICK(
        1.2f,
        5.0f,
        SoundType.STONE,
        MapColor.DIRT,
        true,
        PushReaction.NORMAL
    ),
    
    // ================ 特殊材料 ================
    
    /** 冰砖材料 - 适用于冰制方块 */
    ICE_BRICK(
        0.5f,
        0.5f,
        SoundType.GLASS,
        MapColor.ICE,
        false,
        PushReaction.NORMAL
    ),
    
    /** 雪砖材料 - 适用于雪制方块 */
    SNOW_BRICK(
        0.4f,
        0.4f,
        SoundType.SNOW,
        MapColor.SNOW,
        false,
        PushReaction.NORMAL
    ),
    
    /** 路径材料 - 适用于各种道路方块 */
    PATH(
        0.5f,
        2.5f,
        SoundType.GRAVEL,
        MapColor.DIRT,
        false,
        PushReaction.NORMAL
    );
    
    // ================ 属性字段 ================
    
    private final float hardness;
    private final float explosionResistance;
    private final SoundType soundType;
    private final MapColor mapColor;
    private final boolean requiresCorrectTool;
    private final PushReaction pushReaction;
    
    /**
     * 构造函数
     *
     * @param hardness 硬度值
     * @param explosionResistance 爆炸抗性
     * @param soundType 声音类型
     * @param mapColor 地图颜色
     * @param requiresCorrectTool 是否需要正确工具
     * @param pushReaction 推动反应
     */
    BuildingMaterial(float hardness, float explosionResistance, SoundType soundType, 
                    MapColor mapColor, boolean requiresCorrectTool, PushReaction pushReaction) {
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.soundType = soundType;
        this.mapColor = mapColor;
        this.requiresCorrectTool = requiresCorrectTool;
        this.pushReaction = pushReaction;
    }
    
    // ================ 访问器方法 ================
    
    /** 获取硬度值 */
    public float getHardness() {
        return hardness;
    }
    
    /** 获取爆炸抗性 */
    public float getExplosionResistance() {
        return explosionResistance;
    }
    
    /** 获取声音类型 */
    public SoundType getSoundType() {
        return soundType;
    }
    
    /** 获取地图颜色 */
    public MapColor getMapColor() {
        return mapColor;
    }
    
    /** 是否需要正确工具 */
    public boolean requiresCorrectTool() {
        return requiresCorrectTool;
    }
    
    /** 获取推动反应 */
    public PushReaction getPushReaction() {
        return pushReaction;
    }
}
