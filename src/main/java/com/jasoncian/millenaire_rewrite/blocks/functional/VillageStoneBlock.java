package com.jasoncian.millenaire_rewrite.blocks.functional;

import com.jasoncian.millenaire_rewrite.blockentities.VillageStoneBlockEntity;
import com.jasoncian.millenaire_rewrite.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Village Stone Block - 村庄核心方块
 * 
 * 基于legacy的BlockVillageStone.java重新实现
 * 这是整个Millenaire mod的核心方块，负责：
 * - 标记和管理村庄
 * - 存储村庄数据
 * - 处理玩家交互
 * - 村庄事件和爆炸逻辑
 */
public class VillageStoneBlock extends BaseEntityBlock {

    public VillageStoneBlock() {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 3600000.0F) // 无法破坏，超高爆炸抗性
                .noLootTable() // 不掉落任何物品，参考legacy的quantityDropped返回0
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // 1.20.1中替代legacy的getRenderType() return 3
    }

    /**
     * 玩家右键交互处理
     * 基于legacy的onBlockActivated重新实现
     */
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, 
                                InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            // 客户端显示消息，参考legacy的ChatComponentText
            pPlayer.displayClientMessage(
                Component.literal("The Village name almost seems to shimmer in the twilight"), 
                false
            );
        } else {
            // 服务端逻辑
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof VillageStoneBlockEntity) {
                VillageStoneBlockEntity villageStone = (VillageStoneBlockEntity) blockEntity;
                // 参考legacy的testVar逻辑
                if (villageStone.getTestVar() < 16) {
                    villageStone.incrementTestVar();
                } else {
                    villageStone.resetTestVar();
                }
                villageStone.setChanged(); // 标记为已修改，用于保存数据
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 创建BlockEntity
     * 替代legacy的createNewTileEntity
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new VillageStoneBlockEntity(pPos, pState);
    }

    /**
     * 获取BlockEntity的Ticker
     * 1.20.1中用于处理BlockEntity的定时更新
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }
        
        return createTickerHelper(pBlockEntityType, ModBlockEntities.VILLAGE_STONE.get(),
                (level, pos, state, blockEntity) -> {
                    VillageStoneBlockEntity villageStone = (VillageStoneBlockEntity) blockEntity;
                    villageStone.tick(level, pos, state);
                });
    }

    /**
     * 村庄石的否定/爆炸逻辑
     * 基于legacy的negate方法重新实现
     * 
     * @param level 世界
     * @param pos 位置
     * @param player 触发的玩家
     */
    public void negate(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof VillageStoneBlockEntity) {
            VillageStoneBlockEntity villageStone = (VillageStoneBlockEntity) blockEntity;
            villageStone.setWillExplode(true);
            level.scheduleTick(pos, this, 60); // 60tick后爆炸
            
            // 播放音效，参考legacy的playSoundEffect
            level.playSound(null, pos, 
                net.minecraft.sounds.SoundEvents.PORTAL_AMBIENT, 
                net.minecraft.sounds.SoundSource.BLOCKS,
                1.0F, 0.01F);
        }
    }

    /**
     * 定时更新处理
     * 基于legacy的updateTick重新实现
     * 在1.20.1中，通过scheduleTick调用
     */
    @Override
    public void tick(BlockState pState, net.minecraft.server.level.ServerLevel pLevel, BlockPos pPos, net.minecraft.util.RandomSource pRandom) {
        if (!pLevel.isClientSide) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof VillageStoneBlockEntity) {
                VillageStoneBlockEntity villageStone = (VillageStoneBlockEntity) blockEntity;
                if (villageStone.willExplode()) {
                    // 执行爆炸逻辑，参考legacy的createExplosion
                    pLevel.removeBlock(pPos, false);
                    pLevel.explode(null, pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, 
                                 2.0F, Level.ExplosionInteraction.BLOCK);
                }
            }
        }
    }

    /**
     * 确保方块在被移除时不会掉落
     * 参考legacy的quantityDropped返回0
     */
    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        // 不掉落任何东西
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }
}
