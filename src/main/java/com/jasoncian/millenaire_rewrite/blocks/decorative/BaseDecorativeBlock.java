package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;

/**
 * 装饰方块基类 - 1.20.1现代化实现
 * 
 * 基于legacy装饰方块系统重新设计，提供：
 * - 现代化的方块状态系统
 * - 类型安全的变体枚举
 * - 可选的方向性支持
 * - 统一的材质和硬度配置
 * 
 * @param <T> 变体枚举类型
 */
public abstract class BaseDecorativeBlock<T extends Enum<T> & DecorativeVariant> extends Block {
    
    /**
     * 方向属性 - 用于支持方向性装饰方块
     * 对应legacy的BlockDecorativeOriented.FACING
     */
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    /**
     * 变体属性 - 由子类定义具体的枚举类型
     */
    protected final EnumProperty<T> variantProperty;
    
    /**
     * 是否支持方向性放置
     */
    protected final boolean hasDirection;
    
    /**
     * 构造函数
     * 
     * @param properties 方块属性
     * @param variantProperty 变体属性
     * @param hasDirection 是否支持方向性
     */
    protected BaseDecorativeBlock(Properties properties, EnumProperty<T> variantProperty, boolean hasDirection) {
        super(properties);
        this.variantProperty = variantProperty;
        this.hasDirection = hasDirection;
    }
    
    /**
     * 获取默认变体
     */
    protected abstract T getDefaultVariant();
    
    /**
     * 获取变体属性
     */
    public EnumProperty<T> getVariantProperty() {
        return variantProperty;
    }
    
    /**
     * 方块放置时的状态设置
     * 基于legacy的onBlockPlaced现代化实现
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        
        if (hasDirection) {
            // 设置方向，面向玩家放置方向的反方向（legacy行为）
            state = state.setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        
        return state;
    }
    
    /**
     * 创建方块状态定义
     * 对应legacy的createBlockState()
     * 
     * 子类必须重写此方法以添加正确的属性
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // 基类不添加任何属性，由子类重写添加
    }
    
    /**
     * 获取特定变体的MapColor
     * 子类可以重写以提供变体特定的颜色
     */
    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultMapColor) {
        T variant = state.getValue(variantProperty);
        return variant.getMapColor();
    }
    
    /**
     * 获取某个变体的方块状态
     * 便于程序化创建特定变体的方块
     */
    public BlockState getVariantState(T variant) {
        BlockState state = this.defaultBlockState().setValue(variantProperty, variant);
        if (hasDirection) {
            state = state.setValue(FACING, Direction.NORTH);
        }
        return state;
    }
    
    /**
     * 获取某个变体和方向的方块状态
     */
    public BlockState getVariantState(T variant, Direction facing) {
        BlockState state = this.defaultBlockState().setValue(variantProperty, variant);
        if (hasDirection && facing.getAxis().isHorizontal()) {
            state = state.setValue(FACING, facing);
        }
        return state;
    }
}
