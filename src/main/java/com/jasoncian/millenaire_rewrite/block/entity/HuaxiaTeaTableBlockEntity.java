package com.jasoncian.millenaire_rewrite.block.entity;

import com.jasoncian.millenaire_rewrite.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

/**
 * 华夏茶桌方块实体
 * 
 * 华夏文化的茶艺专用家具，具有以下特性：
 * - 9个槽位用于存放茶具和茶叶
 * - 专门的茶具过滤系统（只能放置特定物品）
 * - 茶艺功能（未来扩展：泡茶、品茶等）
 * - 华夏茶文化的体现
 * 
 * 槽位分配：
 * - 槽位0-2：茶叶存储
 * - 槽位3-5：茶具存储（茶壶、茶杯等）
 * - 槽位6-8：辅助物品（糖、蜂蜜等调味品）
 * 
 * 设计理念：
 * - 体现华夏茶文化的精髓
 * - 支持未来的茶艺系统扩展
 * - 与村民的社交活动集成
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class HuaxiaTeaTableBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
    
    /** 茶桌的物品槽位数量 */
    private static final int CONTAINER_SIZE = 9;
    
    /** 茶叶槽位索引 */
    private static final int[] TEA_SLOTS = {0, 1, 2};
    
    /** 茶具槽位索引 */
    private static final int[] TEAWARE_SLOTS = {3, 4, 5};
    
    /** 辅助物品槽位索引 */
    private static final int[] AUXILIARY_SLOTS = {6, 7, 8};
    
    /** 物品存储 */
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    
    /** 当前茶艺状态（未来扩展用） */
    private int teaCeremonyState = 0;
    
    /** 最后一次茶艺活动时间（游戏刻） */
    private long lastTeaCeremonyTime = 0;
    
    /** 物品处理能力的懒加载包装器 */
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.empty();
    
    /**
     * 构造华夏茶桌方块实体
     */
    public HuaxiaTeaTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HUAXIA_TEA_TABLE.get(), pos, state);
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
        return Component.translatable("container.millenaire_rewrite.huaxia_tea_table");
    }
    
    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        // 使用3x3的界面布局，暂时使用通用箱子菜单
        return ChestMenu.threeRows(id, inventory, this);
    }
    
    // =============================================================================
    // 茶具过滤系统
    // =============================================================================
    
    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        // 根据槽位类型限制可放置的物品
        if (isTeaSlot(index)) {
            return isTeaItem(stack);
        } else if (isTeawareSlot(index)) {
            return isTeawareItem(stack);
        } else if (isAuxiliarySlot(index)) {
            return isAuxiliaryItem(stack);
        }
        
        return false;
    }
    
    /**
     * 检查是否为茶叶槽位
     */
    private boolean isTeaSlot(int index) {
        for (int slot : TEA_SLOTS) {
            if (slot == index) return true;
        }
        return false;
    }
    
    /**
     * 检查是否为茶具槽位
     */
    private boolean isTeawareSlot(int index) {
        for (int slot : TEAWARE_SLOTS) {
            if (slot == index) return true;
        }
        return false;
    }
    
    /**
     * 检查是否为辅助物品槽位
     */
    private boolean isAuxiliarySlot(int index) {
        for (int slot : AUXILIARY_SLOTS) {
            if (slot == index) return true;
        }
        return false;
    }
    
    /**
     * 检查物品是否为茶叶类型
     * TODO: 当添加茶叶物品后，需要更新这个方法
     */
    private boolean isTeaItem(ItemStack stack) {
        // 暂时允许任何植物类物品作为茶叶
        return stack.is(Items.WHEAT) || 
               stack.is(Items.SWEET_BERRIES) ||
               stack.is(Items.DRIED_KELP) ||
               // 未来添加：华夏茶叶物品
               stack.getDescriptionId().contains("tea");
    }
    
    /**
     * 检查物品是否为茶具类型
     * TODO: 当添加茶具物品后，需要更新这个方法
     */
    private boolean isTeawareItem(ItemStack stack) {
        // 暂时允许碗、桶等容器类物品作为茶具
        return stack.is(Items.BOWL) ||
               stack.is(Items.BUCKET) ||
               stack.is(Items.GLASS_BOTTLE) ||
               // 未来添加：华夏茶具物品
               stack.getDescriptionId().contains("teapot") ||
               stack.getDescriptionId().contains("teacup");
    }
    
    /**
     * 检查物品是否为辅助调味品
     */
    private boolean isAuxiliaryItem(ItemStack stack) {
        // 糖、蜂蜜等调味品
        return stack.is(Items.SUGAR) ||
               stack.is(Items.HONEY_BOTTLE) ||
               stack.is(Items.MILK_BUCKET) ||
               // 未来添加：其他调味品
               stack.getDescriptionId().contains("spice");
    }
    
    // =============================================================================
    // WorldlyContainer接口实现（差异化管道访问）
    // =============================================================================
    
    @Override
    public int[] getSlotsForFace(Direction side) {
        // 根据方向提供不同的槽位访问
        return switch (side) {
            case UP -> TEA_SLOTS;              // 顶部：茶叶输入
            case DOWN -> AUXILIARY_SLOTS;      // 底部：辅助物品输出
            default -> TEAWARE_SLOTS;          // 侧面：茶具访问
        };
    }
    
    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }
    
    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        // 只允许从对应方向取出对应类型的物品
        if (direction == Direction.UP) {
            return false; // 顶部不允许取出
        }
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
        
        // 读取茶艺状态数据
        this.teaCeremonyState = tag.getInt("TeaCeremonyState");
        this.lastTeaCeremonyTime = tag.getLong("LastTeaCeremonyTime");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }
        
        // 保存茶艺状态数据
        tag.putInt("TeaCeremonyState", this.teaCeremonyState);
        tag.putLong("LastTeaCeremonyTime", this.lastTeaCeremonyTime);
    }
    
    // =============================================================================
    // 茶艺功能（未来扩展）
    // =============================================================================
    
    /**
     * 检查是否可以进行茶艺活动
     */
    public boolean canPerformTeaCeremony() {
        // 检查是否有必要的茶叶和茶具
        boolean hasTeaLeaves = false;
        boolean hasTeaware = false;
        
        for (int slot : TEA_SLOTS) {
            if (!getItem(slot).isEmpty()) {
                hasTeaLeaves = true;
                break;
            }
        }
        
        for (int slot : TEAWARE_SLOTS) {
            if (!getItem(slot).isEmpty()) {
                hasTeaware = true;
                break;
            }
        }
        
        return hasTeaLeaves && hasTeaware;
    }
    
    /**
     * 开始茶艺活动
     */
    public void startTeaCeremony() {
        if (canPerformTeaCeremony() && level != null) {
            this.teaCeremonyState = 1;
            this.lastTeaCeremonyTime = level.getGameTime();
            setChanged();
        }
    }
    
    /**
     * 获取茶艺状态
     */
    public int getTeaCeremonyState() {
        return this.teaCeremonyState;
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
     * @return 茶艺功能类型
     */
    public String getFunctionType() {
        return "tea_ceremony";
    }
    
    /**
     * 需要客户端同步（用于茶艺状态显示）
     */
    protected boolean needsSync() {
        return true;
    }
}
