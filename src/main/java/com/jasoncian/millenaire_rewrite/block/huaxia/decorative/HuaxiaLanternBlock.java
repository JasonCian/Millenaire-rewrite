package com.jasoncian.millenaire_rewrite.block.huaxia.decorative;

import com.jasoncian.millenaire_rewrite.block.huaxia.HuaxiaBuildingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 华夏灯笼方块
 * 
 * 传统的华夏文化装饰方块，提供照明和装饰功能。
 * 灯笼是华夏文化中重要的装饰元素，常用于节庆和建筑装饰。
 * 
 * 特性：
 * - 提供15级光照
 * - 具有独特的形状（非完整方块）
 * - 支持方向性放置
 * - 木质音效
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaLanternBlock extends HuaxiaBuildingBlock {
    
    /** 灯笼的碰撞箱形状 - 中央悬挂的灯笼造型 */
    private static final VoxelShape SHAPE = Shapes.or(
            // 灯笼主体 (较宽的圆柱形)
            box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D),
            // 顶部悬挂部分
            box(6.0D, 14.0D, 6.0D, 10.0D, 16.0D, 10.0D)
    );
    
    /**
     * 构造华夏灯笼方块
     */
    public HuaxiaLanternBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_RED)           // 红色调，传统灯笼颜色
                .strength(1.0F, 2.0F)                   // 较低硬度，易破坏
                .sound(SoundType.WOOD)                  // 木质音效
                .lightLevel((state) -> 15)              // 提供最高级别光照
                .noOcclusion()                          // 不阻挡光线传播
        );
    }
    
    /**
     * 获取灯笼的形状
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    /**
     * 获取碰撞形状，与视觉形状相同
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
