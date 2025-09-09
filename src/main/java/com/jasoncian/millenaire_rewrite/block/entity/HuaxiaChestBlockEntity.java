package com.jasoncian.millenaire_rewrite.block.entity;

import com.jasoncian.millenaire_rewrite.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

/**
 * 华夏箱子方块实体
 * 
 * 华夏文化特色的存储容器，具有以下特性：
 * - 27个槽位的存储空间（与原版箱子相同）
 * - 支持Forge物品处理能力（IItemHandler）
 * - 华夏风格的开关音效
 * - 支持漏斗等自动化交互
 * - 保存和加载物品数据
 * - 客户端-服务端数据同步
 * 
 * 实现特点：
 * - 继承RandomizableContainerBlockEntity获得完整的容器功能
 * - 实现WorldlyContainer支持管道交互
 * - 使用ContainerOpenersCounter管理开启状态和音效
 * - 通过Capability系统暴露物品处理接口
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaChestBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    
    /** 箱子的物品存储槽位数量 */
    private static final int CONTAINER_SIZE = 27;
    
    /** 所有方向的槽位索引（用于管道交互） */
    private static final int[] SLOTS = new int[CONTAINER_SIZE];
    
    static {
        // 初始化槽位索引数组
        for (int i = 0; i < CONTAINER_SIZE; i++) {
            SLOTS[i] = i;
        }
    }
    
    /** 物品存储 */
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    
    /** 开启者计数器，用于管理开关音效和状态 */
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            HuaxiaChestBlockEntity.this.playSound(state, SoundEvents.CHEST_OPEN);
        }
        
        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            HuaxiaChestBlockEntity.this.playSound(state, SoundEvents.CHEST_CLOSE);
        }
        
        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            HuaxiaChestBlockEntity.this.signalOpenCount(level, pos, state, count, openCount);
        }
        
        @Override
        protected boolean isOwnContainer(Player player) {
            if (!(player.containerMenu instanceof ChestMenu chestMenu)) {
                return false;
            }
            return chestMenu.getContainer() == HuaxiaChestBlockEntity.this;
        }
    };
    
    /** 物品处理能力的懒加载包装器 */
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.empty();
    
    /**
     * 构造华夏箱子方块实体
     */
    public HuaxiaChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HUAXIA_CHEST.get(), pos, state);
    }
    
    // =============================================================================
    // 基础容器接口实现
    // =============================================================================
    
    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }
    
    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }
    
    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }
    
    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.millenaire_rewrite.huaxia_chest");
    }
    
    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.threeRows(id, inventory, this);
    }
    
    // =============================================================================
    // WorldlyContainer接口实现（支持管道等自动化）
    // =============================================================================
    
    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }
    
    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }
    
    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }
    
    // =============================================================================
    // 数据持久化
    // =============================================================================
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items);
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }
    }
    
    // =============================================================================
    // 开关状态管理
    // =============================================================================
    
    public static void lidAnimationTick(Level level, BlockPos pos, BlockState state, HuaxiaChestBlockEntity blockEntity) {
        blockEntity.openersCounter.recheckOpeners(level, pos, state);
    }
    
    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    /**
     * 播放箱子音效
     */
    private void playSound(BlockState state, SoundEvent sound) {
        if (this.level != null) {
            double x = this.worldPosition.getX() + 0.5D;
            double y = this.worldPosition.getY() + 0.5D;
            double z = this.worldPosition.getZ() + 0.5D;
            this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, 
                               this.level.random.nextFloat() * 0.1F + 0.9F);
        }
    }
    
    /**
     * 信号开启数量变化（用于红石信号等）
     */
    private void signalOpenCount(Level level, BlockPos pos, BlockState state, int count, int openCount) {
        // 这里可以添加红石信号逻辑
        // 当前简化实现，只更新方块状态
        level.blockEvent(pos, state.getBlock(), 1, openCount);
    }
    
    // =============================================================================
    // Forge能力系统支持
    // =============================================================================
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (itemHandler.isPresent()) {
                return itemHandler.cast();
            }
            // 创建侧面物品处理器包装器
            itemHandler = LazyOptional.of(() -> new SidedInvWrapper(this, side));
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }
    
    // =============================================================================
    // 千年村庄基类接口实现
    // =============================================================================
    
    /**
     * @return 华夏文化类型
     */
    public String getCultureType() {
        return "huaxia";
    }
    
    /**
     * @return 存储功能类型
     */
    public String getFunctionType() {
        return "storage";
    }
    
    /**
     * 需要客户端同步（用于动画等）
     */
    protected boolean needsSync() {
        return true;
    }
}
