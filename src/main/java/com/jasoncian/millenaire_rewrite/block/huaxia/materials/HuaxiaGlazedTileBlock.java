package com.jasoncian.millenaire_rewrite.block.huaxia.materials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * 琉璃瓦方块
 * 
 * 华夏文化的高级屋顶建材，用于重要建筑的屋顶装饰。
 * 琉璃瓦色彩绚丽，工艺精湛，体现了华夏建筑的精美特色。
 * 
 * 特性：
 * - 中等硬度
 * - 陶土音效（更轻脆的声音）
 * - 金黄色泽，象征皇室威严
 * - 主要用于屋顶装饰
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaGlazedTileBlock extends Block {
    
    /**
     * 构造琉璃瓦方块
     */
    public HuaxiaGlazedTileBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)        // 金黄色调
                .strength(1.8F, 4.0F)                   // 适中的硬度，反映陶瓷特性
                .sound(SoundType.DECORATED_POT)         // 陶瓷音效
                .requiresCorrectToolForDrops()          // 需要正确工具才能掉落
        );
    }
}
