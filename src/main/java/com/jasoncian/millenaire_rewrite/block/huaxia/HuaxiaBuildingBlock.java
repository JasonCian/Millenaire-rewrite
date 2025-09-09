package com.jasoncian.millenaire_rewrite.block.huaxia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.Direction;
import javax.annotation.Nullable;

/**
 * 华夏文化建筑方块基类
 * 
 * 为华夏文化建筑方块提供通用功能，包括：
 * - 方向性放置（朝向控制）
 * - 华夏建筑风格的共同属性
 * - 统一的碰撞箱和选择框处理
 * 
 * 所有华夏文化的特色建筑方块都应继承此类。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public abstract class HuaxiaBuildingBlock extends HorizontalDirectionalBlock {
    
    /** 方向属性，控制方块的朝向 */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    
    /**
     * 构造华夏建筑方块
     * 
     * @param properties 方块属性
     */
    public HuaxiaBuildingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        // 默认朝向北方
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    /**
     * 根据放置上下文确定方块状态
     * 特别处理华夏建筑的朝向规则
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // 华夏建筑通常面向玩家
        Direction direction = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction);
    }
    
    /**
     * 创建方块状态定义
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    /**
     * 获取方块的碰撞形状
     * 子类可以重写此方法以提供自定义的碰撞箱
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }
    
    /**
     * 获取方块的选择形状
     * 子类可以重写此方法以提供自定义的选择框
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }
    
    /**
     * 获取方块当前的朝向
     * 
     * @param state 方块状态
     * @return 朝向方向
     */
    protected Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    }
    
    /**
     * 判断是否为华夏文化建筑方块
     * 
     * @param block 要检查的方块
     * @return 如果是华夏建筑方块返回true
     */
    public static boolean isHuaxiaBuildingBlock(Block block) {
        return block instanceof HuaxiaBuildingBlock;
    }
}
