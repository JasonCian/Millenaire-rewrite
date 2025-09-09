package com.jasoncian.millenaire_rewrite.block.huaxia.functional;

import com.jasoncian.millenaire_rewrite.block.entity.HuaxiaChestBlockEntity;
import com.jasoncian.millenaire_rewrite.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 华夏箱子方块
 * 
 * 华夏文化特色的存储容器方块，具有以下特性：
 * - 27槽位的存储空间
 * - 支持方向性放置
 * - 华夏风格的外观和音效
 * - 完整的容器功能（开关动画、红石信号等）
 * - 支持管道等自动化设备交互
 * 
 * 设计特点：
 * - 继承BaseEntityBlock以支持方块实体
 * - 使用现代的BlockEntity系统
 * - 支持方向属性（可朝向不同方向放置）
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaChestBlock extends BaseEntityBlock {
    
    /** 方向属性 */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    
    /** 箱子的形状（略小于完整方块） */
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    
    /**
     * 构造华夏箱子方块
     */
    public HuaxiaChestBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)                // 木质色调
                .strength(2.5F)                         // 中等硬度
                .sound(SoundType.WOOD)                  // 木质音效
                .noOcclusion()                          // 不完全阻挡光线
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }
    
    // =============================================================================
    // 方块状态定义
    // =============================================================================
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    // =============================================================================
    // 方块形状
    // =============================================================================
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    // =============================================================================
    // 方块实体相关
    // =============================================================================
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HuaxiaChestBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        // 只在客户端提供ticker用于动画
        return level.isClientSide ? 
                createTickerHelper(blockEntityType, ModBlockEntities.HUAXIA_CHEST.get(), HuaxiaChestBlockEntity::lidAnimationTick) : 
                null;
    }
    
    // =============================================================================
    // 玩家交互
    // =============================================================================
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HuaxiaChestBlockEntity chestBlockEntity) {
            player.openMenu(chestBlockEntity);
            player.awardStat(Stats.OPEN_CHEST);
        }
        
        return InteractionResult.CONSUME;
    }
    
    // =============================================================================
    // 方块销毁和掉落
    // =============================================================================
    
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof HuaxiaChestBlockEntity chestBlockEntity) {
                if (level instanceof ServerLevel) {
                    // 掉落箱子中的所有物品
                    Containers.dropContents(level, pos, chestBlockEntity);
                    // 更新比较器信号
                    level.updateNeighbourForOutputSignal(pos, this);
                }
            }
        }
        
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    // =============================================================================
    // 红石信号支持
    // =============================================================================
    
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }
    
    // =============================================================================
    // 随机刻处理（用于一些特殊效果）
    // =============================================================================
    
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HuaxiaChestBlockEntity) {
            // 这里可以添加一些华夏箱子的特殊效果
            // 比如定期检查箱子状态、触发文化相关事件等
        }
    }
}
