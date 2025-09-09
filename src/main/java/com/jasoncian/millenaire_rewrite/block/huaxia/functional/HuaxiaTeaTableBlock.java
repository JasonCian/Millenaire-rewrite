package com.jasoncian.millenaire_rewrite.block.huaxia.functional;

import com.jasoncian.millenaire_rewrite.block.huaxia.HuaxiaBuildingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 华夏茶桌方块
 * 
 * 华夏文化的传统功能家具，用于品茶和社交活动。
 * 茶桌是华夏文化中重要的社交场所，体现了华夏的茶文化传统。
 * 
 * 特性：
 * - 较低的桌面高度（不是完整方块）
 * - 支持方向性放置
 * - 右键交互显示茶文化信息
 * - 木质材料和音效
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaTeaTableBlock extends HuaxiaBuildingBlock {
    
    /** 茶桌的形状 - 桌面和桌腿 */
    private static final VoxelShape SHAPE = Shapes.or(
            // 桌面
            box(1.0D, 8.0D, 1.0D, 15.0D, 10.0D, 15.0D),
            // 四个桌腿
            box(2.0D, 0.0D, 2.0D, 4.0D, 8.0D, 4.0D),      // 左前腿
            box(12.0D, 0.0D, 2.0D, 14.0D, 8.0D, 4.0D),    // 右前腿
            box(2.0D, 0.0D, 12.0D, 4.0D, 8.0D, 14.0D),    // 左后腿
            box(12.0D, 0.0D, 12.0D, 14.0D, 8.0D, 14.0D)   // 右后腿
    );
    
    /**
     * 构造华夏茶桌方块
     */
    public HuaxiaTeaTableBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)                // 木质色调
                .strength(2.0F, 3.0F)                   // 中等硬度
                .sound(SoundType.WOOD)                  // 木质音效
                .noOcclusion()                          // 不完全阻挡光线
        );
    }
    
    /**
     * 获取茶桌的形状
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
    
    /**
     * 处理玩家右键交互
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // 显示茶文化相关信息
            player.sendSystemMessage(Component.translatable("block.millenaire_rewrite.huaxia_tea_table.message"));
        }
        return InteractionResult.SUCCESS;
    }
}
