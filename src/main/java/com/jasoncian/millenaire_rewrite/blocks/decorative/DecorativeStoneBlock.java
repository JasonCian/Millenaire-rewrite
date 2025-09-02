package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

/**
 * 石材装饰方块 - 1.20.1现代化实现
 * 
 * 基于legacy BlockDecorativeStone重新实现，提供：
 * - 金装饰石块（高级建筑）
 * - 烧制砖块（基础建筑）  
 * - Galianite方块（特殊材料）
 * 
 * 对应legacy: BlockDecorativeStone.java
 * 材质: Material.rock (现代化为MapColor.STONE)
 */
public class DecorativeStoneBlock extends BaseDecorativeBlock<StoneDecorativeVariant> {
    
    /**
     * 石材变体属性
     * 对应legacy的VARIANT = PropertyEnum.create("variant", BlockDecorativeStone.EnumType.class)
     */
    public static final EnumProperty<StoneDecorativeVariant> VARIANT = 
            EnumProperty.create("variant", StoneDecorativeVariant.class);
    
    /**
     * 构造函数
     * 创建石材装饰方块，包含所有三种变体
     */
    public DecorativeStoneBlock() {
        super(
            Properties.of()
                .mapColor(MapColor.STONE)  // 对应legacy Material.rock
                .strength(2.0F, 6.0F)     // 基础硬度，具体硬度由变体决定
                .requiresCorrectToolForDrops(), // 需要镐子挖掘
            VARIANT,
            false  // 石材装饰方块不支持方向性（基于legacy分析）
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
     * 默认为金装饰，对应legacy的第一个变体
     */
    @Override
    protected StoneDecorativeVariant getDefaultVariant() {
        return StoneDecorativeVariant.GOLD_ORNAMENT;
    }
    
    /**
     * 根据变体获取动态硬度
     * 重写以支持变体特定的硬度值
     */
    @Override
    public float getDestroyProgress(net.minecraft.world.level.block.state.BlockState state, 
                                  net.minecraft.world.entity.player.Player player, 
                                  net.minecraft.world.level.BlockGetter level, 
                                  net.minecraft.core.BlockPos pos) {
        StoneDecorativeVariant variant = state.getValue(VARIANT);
        
        // 使用变体特定的硬度
        float hardness = variant.getHardness();
        if (hardness == -1.0F) {
            return 0.0F;
        } else {
            // 简化的硬度计算，基于玩家的挖掘速度
            return player.getDigSpeed(state, pos) / hardness / 30.0F;
        }
    }
    
    /**
     * 根据变体获取动态爆炸抗性
     */
    @Override
    public float getExplosionResistance() {
        // 返回最高的爆炸抗性，具体值在运行时根据状态确定
        return 12.0F; // Galianite方块的最高抗性
    }
}
