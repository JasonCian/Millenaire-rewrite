package com.jasoncian.millenaire_rewrite.item.spawn;

import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.entity.villager.MillVillagerEntity;
import com.jasoncian.millenaire_rewrite.init.ModEntityTypes;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 千年村民生成器物品
 * 
 * 用于开发和测试阶段的村民生成工具。
 * 右键点击方块可以在该位置生成一个千年村民实体。
 * 
 * 注意：这是一个开发工具，在正式版本中应该移除或限制使用。
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public class MillVillagerSpawnerItem extends Item {

    /**
     * 创建千年村民生成器物品
     * 
     * @param properties 物品属性
     */
    public MillVillagerSpawnerItem(Properties properties) {
        super(properties);
    }

    /**
     * 处理右键点击方块事件
     * 
     * 在玩家右键点击方块时，在该位置上方生成一个千年村民。
     * 
     * @param context 使用上下文
     * @return 交互结果
     */
    @Override
    @NotNull
    public InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        
        // 只在服务端执行生成逻辑
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.FAIL;
        }
        
        BlockPos clickedPos = context.getClickedPos();
        BlockPos spawnPos = clickedPos.above(); // 在点击的方块上方生成
        
        try {
            // 创建村民实体
            MillVillagerEntity villager = ModEntityTypes.MILL_VILLAGER.get().create(serverLevel);
            if (villager == null) {
                if (ModConstants.DEBUG_MODE) {
                    MillenaireLogger.warn(LogCategory.ENTITY, "无法创建千年村民实体");
                }
                return InteractionResult.FAIL;
            }
            
            // 设置生成位置
            villager.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            
            // 初始化村民属性
            villager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos), 
                                 MobSpawnType.SPAWNER, null, null);
            
            // 将村民添加到世界中
            boolean success = serverLevel.addFreshEntity(villager);
            
            if (success) {
                if (ModConstants.DEBUG_MODE) {
                    MillenaireLogger.info(LogCategory.ENTITY, 
                                        "成功生成千年村民: 位置=({}, {}, {}), 文化={}, 职业={}, 性别={}", 
                                        spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                                        villager.getCultureType(), villager.getProfession(), villager.getGender());
                }
                
                // 消耗物品（创造模式下不消耗）
                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                
                return InteractionResult.CONSUME;
            } else {
                if (ModConstants.DEBUG_MODE) {
                    MillenaireLogger.warn(LogCategory.ENTITY, "添加千年村民到世界失败");
                }
                return InteractionResult.FAIL;
            }
            
        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.ENTITY, "生成千年村民时发生异常", e);
            return InteractionResult.FAIL;
        }
    }

    /**
     * 检查是否可以在指定位置生成村民
     * 
     * @param level 世界
     * @param pos 位置
     * @return 是否可以生成
     */
    @SuppressWarnings("unused")
    private boolean canSpawnAt(Level level, BlockPos pos) {
        // 检查位置是否有足够的空间
        if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        
        // 检查下方是否有固体方块支撑
        if (!level.getBlockState(pos.below()).canOcclude()) {
            return false;
        }
        
        // 检查光照等级（避免在过暗的地方生成）
        int lightLevel = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
        if (lightLevel < 7) {
            return false;
        }
        
        return true;
    }
}
