package com.jasoncian.millenaire_rewrite.block.huaxia.materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * 青砖方块
 * 
 * 华夏文化的经典建筑材料，常用于墙体和地面装饰。
 * 青砖具有朴素典雅的外观，是传统中式建筑的重要组成部分。
 * 
 * 特性：
 * - 较高的硬度和爆炸抗性
 * - 石质音效
 * - 可用于建造传统建筑的墙体
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaGreenBrickBlock extends Block {
    
    /**
     * 构造青砖方块
     */
    public HuaxiaGreenBrickBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_GRAY)          // 灰绿色调
                .strength(2.0F, 6.0F)                   // 硬度和爆炸抗性
                .sound(SoundType.STONE)                 // 石质音效
                .requiresCorrectToolForDrops()          // 需要正确工具才能掉落
        );
    }
}
