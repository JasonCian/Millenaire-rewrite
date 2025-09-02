package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

/**
 * 木材装饰方块 - 1.20.1现代化实现
 * 
 * 基于legacy BlockDecorativeWood重新实现，提供：
 * - 简朴木框架（基础诺曼建筑）
 * - 十字木框架（高级诺曼建筑）
 * - 茅草（屋顶材料）
 * - 养蚕架（日式农业）
 * 
 * 对应legacy: BlockDecorativeWood.java
 * 材质: Material.wood (现代化为MapColor.WOOD)
 */
public class DecorativeWoodBlock extends BaseDecorativeBlock<WoodDecorativeVariant> {
    
    /**
     * 木材变体属性
     * 对应legacy的VARIANT = PropertyEnum.create("variant", BlockDecorativeWood.EnumType.class)
     */
    public static final EnumProperty<WoodDecorativeVariant> VARIANT = 
            EnumProperty.create("variant", WoodDecorativeVariant.class);
    
    /**
     * 构造函数
     * 创建木材装饰方块，包含所有四种变体
     */
    public DecorativeWoodBlock() {
        super(
            Properties.of()
                .mapColor(MapColor.WOOD)  // 对应legacy Material.wood
                .strength(2.0F, 3.0F)    // 基础硬度，具体硬度由变体决定
                .ignitedByLava()         // 木材可被岩浆点燃
                .sound(net.minecraft.world.level.block.SoundType.WOOD), // 木材音效
            VARIANT,
            false  // 木材装饰方块不支持方向性（基于legacy分析）
        );
        
        // 设置默认状态
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, getDefaultVariant()));
    }
    
    /**
     * 创建方块状态定义
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }
    
    /**
     * 获取默认变体
     * 默认为简朴木框架，对应legacy的第一个变体
     */
    @Override
    protected WoodDecorativeVariant getDefaultVariant() {
        return WoodDecorativeVariant.PLAIN_TIMBER_FRAME;
    }
    
    /**
     * 根据变体获取动态MapColor
     * 不同木材变体可能有不同的地图颜色
     */
    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultMapColor) {
        WoodDecorativeVariant variant = state.getValue(VARIANT);
        return variant.getMapColor();
    }
    
    /**
     * 根据变体获取动态硬度
     * 茅草比其他木材更软
     */
    @Override
    public float getDestroyProgress(net.minecraft.world.level.block.state.BlockState state, 
                                  net.minecraft.world.entity.player.Player player, 
                                  net.minecraft.world.level.BlockGetter level, 
                                  net.minecraft.core.BlockPos pos) {
        WoodDecorativeVariant variant = state.getValue(VARIANT);
        
        // 使用变体特定的硬度
        float hardness = variant.getHardness();
        if (hardness == -1.0F) {
            return 0.0F;
        } else {
            return player.getDigSpeed(state, pos) / hardness / 30.0F;
        }
    }
    
    /**
     * 燃烧性质
     * 茅草更容易燃烧
     */
    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) {
        WoodDecorativeVariant variant = state.getValue(VARIANT);
        
        if (variant.isThatch()) {
            return 60; // 茅草高燃烧性
        } else {
            return 20; // 普通木材燃烧性
        }
    }
    
    /**
     * 火焰传播速度
     */
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) {
        WoodDecorativeVariant variant = state.getValue(VARIANT);
        
        if (variant.isThatch()) {
            return 30; // 茅草快速传播
        } else {
            return 5;  // 普通木材正常传播
        }
    }
}
