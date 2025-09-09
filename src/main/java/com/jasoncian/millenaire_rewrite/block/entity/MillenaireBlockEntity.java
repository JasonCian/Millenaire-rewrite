package com.jasoncian.millenaire_rewrite.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 千年村庄方块实体基类
 * 
 * 为所有千年村庄的方块实体提供通用功能和接口：
 * - 统一的数据持久化机制
 * - 客户端-服务端同步支持
 * - 文化系统集成接口
 * - 性能优化的更新机制
 * 
 * 设计原则：
 * - 遵循Forge 1.20.1的BlockEntity最佳实践
 * - 支持多种文化的差异化扩展
 * - 提供高效的网络同步机制
 * - 模块化设计便于功能扩展
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public abstract class MillenaireBlockEntity extends BlockEntity {
    
    /**
     * 构造千年村庄方块实体
     * 
     * @param type 方块实体类型
     * @param pos 方块位置
     * @param state 方块状态
     */
    public MillenaireBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    /**
     * 检查是否需要客户端-服务端同步
     * 
     * 子类可以重写此方法来控制何时需要同步数据到客户端。
     * 默认实现不进行同步，子类应根据需要重写。
     * 
     * @return true如果需要同步到客户端
     */
    protected boolean needsSync() {
        return false;
    }
    
    /**
     * 触发方块实体数据同步
     * 
     * 当方块实体数据发生变化需要同步到客户端时调用此方法。
     * 这会标记方块为"脏"状态并请求数据同步。
     */
    protected void sync() {
        if (level != null && !level.isClientSide) {
            setChanged();
            // 请求方块状态更新以触发客户端同步
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    /**
     * 安全地标记方块实体为已更改
     * 
     * 重写基类方法以添加额外的同步逻辑。
     * 当方块实体数据发生变化时会自动调用。
     */
    @Override
    public void setChanged() {
        super.setChanged();
        
        // 如果需要同步且在服务端，执行同步
        if (needsSync() && level != null && !level.isClientSide) {
            sync();
        }
    }
    
    /**
     * 获取方块实体的文化类型
     * 
     * 子类应该重写此方法来返回对应的文化类型。
     * 这用于文化系统的集成和差异化处理。
     * 
     * @return 文化类型字符串，如"huaxia"、"norman"等
     */
    public abstract String getCultureType();
    
    /**
     * 获取方块实体的功能类型
     * 
     * 子类应该重写此方法来返回对应的功能类型。
     * 这用于系统识别方块实体的具体功能。
     * 
     * @return 功能类型字符串，如"storage"、"workstation"等
     */
    public abstract String getFunctionType();
}
