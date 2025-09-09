package com.jasoncian.millenaire_rewrite.block.huaxia.materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * 红墙方块
 * 
 * 华夏文化的宫殿和重要建筑的专用墙体材料。
 * 红墙象征着威严和权力，常用于皇宫、庙宇等重要建筑。
 * 
 * 特性：
 * - 高硬度和爆炸抗性
 * - 石质音效
 * - 鲜艳的红色外观
 * - 适用于重要建筑的外墙
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaRedWallBlock extends Block {
    
    /**
     * 构造红墙方块
     */
    public HuaxiaRedWallBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_RED)           // 红色调
                .strength(2.5F, 8.0F)                   // 比青砖更高的硬度和爆炸抗性
                .sound(SoundType.STONE)                 // 石质音效
                .requiresCorrectToolForDrops()          // 需要正确工具才能掉落
        );
    }
}
