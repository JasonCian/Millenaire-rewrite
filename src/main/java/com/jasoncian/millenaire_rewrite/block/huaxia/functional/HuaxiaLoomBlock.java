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
 * 华夏织机方块
 * 
 * 华夏文化的传统纺织设备，用于制作丝绸和其他纺织品。
 * 织机体现了华夏悠久的纺织文化和精湛的手工艺传统。
 * 
 * 特性：
 * - 复杂的机械结构外观
 * - 支持方向性放置
 * - 右键交互显示纺织信息
 * - 木质材料音效
 * - 可能在未来版本中添加实际纺织功能
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaLoomBlock extends HuaxiaBuildingBlock {
    
    /** 织机的形状 - 复杂的框架结构 */
    private static final VoxelShape SHAPE = Shapes.or(
            // 底座框架
            box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D),
            // 垂直支柱
            box(2.0D, 2.0D, 2.0D, 4.0D, 14.0D, 4.0D),      // 左前柱
            box(12.0D, 2.0D, 2.0D, 14.0D, 14.0D, 4.0D),    // 右前柱
            box(2.0D, 2.0D, 12.0D, 4.0D, 14.0D, 14.0D),    // 左后柱
            box(12.0D, 2.0D, 12.0D, 14.0D, 14.0D, 14.0D),  // 右后柱
            // 上方横梁
            box(2.0D, 12.0D, 2.0D, 14.0D, 14.0D, 4.0D),    // 前横梁
            box(2.0D, 12.0D, 12.0D, 14.0D, 14.0D, 14.0D),  // 后横梁
            // 编织区域
            box(3.0D, 4.0D, 5.0D, 13.0D, 6.0D, 11.0D)      // 编织台面
    );
    
    /**
     * 构造华夏织机方块
     */
    public HuaxiaLoomBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)                // 木质色调
                .strength(2.5F, 4.0F)                   // 中等偏高硬度
                .sound(SoundType.WOOD)                  // 木质音效
                .noOcclusion()                          // 不完全阻挡光线
        );
    }
    
    /**
     * 获取织机的形状
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
            // 显示纺织相关信息
            player.sendSystemMessage(Component.translatable("block.millenaire_rewrite.huaxia_loom.message"));
        }
        return InteractionResult.SUCCESS;
    }
}
