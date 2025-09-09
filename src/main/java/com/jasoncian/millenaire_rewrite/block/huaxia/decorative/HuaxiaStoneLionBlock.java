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
 * 华夏石狮方块
 * 
 * 传统华夏文化的镇宅神兽装饰，常放置在重要建筑的入口处。
 * 石狮象征着威严和守护，是华夏建筑文化的重要象征。
 * 
 * 特性：
 * - 较小的装饰性方块（不占满整个方块空间）
 * - 支持方向性放置
 * - 石质材料，高硬度
 * - 纯装饰功能，象征意义重大
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaStoneLionBlock extends HuaxiaBuildingBlock {
    
    /** 石狮的形状 - 底座加狮子雕像 */
    private static final VoxelShape SHAPE = Shapes.or(
            // 底座
            box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D),
            // 狮子身体
            box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D),
            // 狮子头部（朝向前方）
            box(5.0D, 8.0D, 2.0D, 11.0D, 14.0D, 6.0D)
    );
    
    /**
     * 构造华夏石狮方块
     */
    public HuaxiaStoneLionBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)               // 石质色调
                .strength(3.0F, 10.0F)                  // 高硬度和爆炸抗性
                .sound(SoundType.STONE)                 // 石质音效
                .requiresCorrectToolForDrops()          // 需要正确工具才能掉落
                .noOcclusion()                          // 不完全阻挡光线
        );
    }
    
    /**
     * 获取石狮的形状
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    /**
     * 碰撞形状与视觉形状相同
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
