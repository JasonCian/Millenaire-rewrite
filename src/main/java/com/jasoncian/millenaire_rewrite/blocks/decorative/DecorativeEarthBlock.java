package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

/**
 * 土质装饰方块 - 1.20.1现代化实现
 * 
 * 基于legacy BlockDecorativeEarth重新实现，提供：
 * - 土墙（基础建筑材料）
 * - 风干砖（印度风格建筑）
 * 
 * 对应legacy: BlockDecorativeEarth.java
 * 材质: Material.ground (现代化为MapColor.DIRT)
 */
public class DecorativeEarthBlock extends BaseDecorativeBlock<EarthDecorativeVariant> {
    
    /**
     * 土质变体属性
     * 对应legacy的VARIANT = PropertyEnum.create("variant", BlockDecorativeEarth.EnumType.class)
     */
    public static final EnumProperty<EarthDecorativeVariant> VARIANT = 
            EnumProperty.create("variant", EarthDecorativeVariant.class);
    
    /**
     * 构造函数
     * 创建土质装饰方块，包含所有两种变体
     */
    public DecorativeEarthBlock() {
        super(
            Properties.of()
                .mapColor(MapColor.DIRT)  // 对应legacy Material.ground
                .strength(0.5F, 2.5F)    // 基础硬度，具体硬度由变体决定
                .sound(net.minecraft.world.level.block.SoundType.GRAVEL), // 土质音效
            VARIANT,
            false  // 土质装饰方块不支持方向性（基于legacy分析）
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
     * 默认为土墙，对应legacy的第一个变体
     */
    @Override
    protected EarthDecorativeVariant getDefaultVariant() {
        return EarthDecorativeVariant.DIRT_WALL;
    }
    
    /**
     * 根据变体获取动态MapColor
     * 不同土质变体可能有不同的地图颜色
     */
    @Override
    public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultMapColor) {
        EarthDecorativeVariant variant = state.getValue(VARIANT);
        return variant.getMapColor();
    }
    
    /**
     * 根据变体获取动态硬度
     * 土墙比风干砖更软，铲子对土质方块更有效
     */
    @Override
    public float getDestroyProgress(net.minecraft.world.level.block.state.BlockState state, 
                                  net.minecraft.world.entity.player.Player player, 
                                  net.minecraft.world.level.BlockGetter level, 
                                  net.minecraft.core.BlockPos pos) {
        EarthDecorativeVariant variant = state.getValue(VARIANT);
        
        // 使用变体特定的硬度
        float hardness = variant.getHardness();
        if (hardness == -1.0F) {
            return 0.0F;
        } else {
            float baseProgress = player.getDigSpeed(state, pos) / hardness / 30.0F;
            
            // 如果玩家使用铲子，提高挖掘速度
            if (player.getMainHandItem().getItem() instanceof net.minecraft.world.item.ShovelItem) {
                baseProgress *= 1.5F;
            }
            
            return baseProgress;
        }
    }
    
    /**
     * 是否可以用任何工具挖掘
     * 土质方块即使用手也能挖掘
     */
    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.entity.SpawnPlacements.Type type, net.minecraft.world.entity.EntityType<?> entityType) {
        // 土质方块可以作为生物生成表面（除了敌对生物）
        return type != net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND || 
               !entityType.getCategory().isFriendly();
    }
}
