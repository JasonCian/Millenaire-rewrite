package com.jasoncian.millenaire_rewrite.block.huaxia.decorative;

import com.jasoncian.millenaire_rewrite.block.huaxia.HuaxiaBuildingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 华夏屏风方块
 * 
 * 传统华夏文化的室内隔断装饰，既有实用功能又有装饰价值。
 * 屏风常用于分隔空间，同时展示华夏文化的艺术美感。
 * 
 * 特性：
 * - 较高但较薄的形状（不是完整方块）
 * - 支持方向性放置
 * - 木质材料和音效
 * - 可以部分阻挡视线但不完全封闭空间
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaScreenBlock extends HuaxiaBuildingBlock {
    
    /** 屏风的形状 - 薄薄的垂直板状 */
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    private static final VoxelShape EAST_WEST_SHAPE = box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    
    /**
     * 构造华夏屏风方块
     */
    public HuaxiaScreenBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)                // 木质色调
                .strength(1.5F, 3.0F)                   // 中等硬度
                .sound(SoundType.WOOD)                  // 木质音效
                .noOcclusion()                          // 不完全阻挡光线
        );
    }
    
    /**
     * 根据朝向获取屏风的形状
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (this.getFacing(state)) {
            case NORTH:
            case SOUTH:
                return NORTH_SOUTH_SHAPE;
            case EAST:
            case WEST:
            default:
                return EAST_WEST_SHAPE;
        }
    }
    
    /**
     * 碰撞形状与视觉形状相同
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }
}
