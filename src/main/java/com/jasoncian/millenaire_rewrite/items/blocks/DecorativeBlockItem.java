package com.jasoncian.millenaire_rewrite.items.blocks;

import com.jasoncian.millenaire_rewrite.blocks.decorative.BaseDecorativeBlock;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeVariant;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 装饰方块物品 - 1.20.1现代化实现
 * 
 * 专门处理装饰方块变体的物品类
 * 每个变体都有独立的物品，但放置时会设置正确的方块状态
 * 
 * 这样设计的优势：
 * - 创造模式中每个变体都是独立条目
 * - 配方系统可以独立处理每个变体
 * - 物品堆叠和存储更直观
 * - 便于实现变体特定的工具提示和属性
 * 
 * @param <T> 装饰变体类型
 */
public class DecorativeBlockItem<T extends Enum<T> & DecorativeVariant> extends BlockItem {
    
    /**
     * 该物品对应的装饰变体
     */
    private final T variant;
    
    /**
     * 构造函数
     * 
     * @param blockSupplier 装饰方块提供者 (RegistryObject)
     * @param variant 该物品对应的变体
     * @param properties 物品属性
     */
    public DecorativeBlockItem(Supplier<Block> blockSupplier, T variant, Properties properties) {
        super(blockSupplier.get(), properties);
        this.variant = variant;
    }
    
    /**
     * 获取该物品对应的变体
     * 
     * @return 装饰变体
     */
    public T getVariant() {
        return variant;
    }
    
    /**
     * 放置方块时设置正确的变体状态
     * 重写以确保放置的方块使用正确的变体
     */
    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        // 确保方块是BaseDecorativeBlock的实例
        if (getBlock() instanceof BaseDecorativeBlock<?> decorativeBlock) {
            // 获取包含正确变体的状态
            @SuppressWarnings("unchecked")
            BaseDecorativeBlock<T> typedBlock = (BaseDecorativeBlock<T>) decorativeBlock;
            BlockState variantState = typedBlock.getVariantState(variant, context.getHorizontalDirection());
            
            // 使用包含正确变体的状态放置方块
            return context.getLevel().setBlock(context.getClickedPos(), variantState, 
                Block.UPDATE_ALL_IMMEDIATE);
        }
        
        // 回退到默认行为
        return super.placeBlock(context, state);
    }
    
    /**
     * 获取放置的方块状态
     * 确保返回包含正确变体的状态
     */
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState baseState = super.getPlacementState(context);
        
        if (baseState != null && getBlock() instanceof BaseDecorativeBlock<?> decorativeBlock) {
            @SuppressWarnings("unchecked")
            BaseDecorativeBlock<T> typedBlock = (BaseDecorativeBlock<T>) decorativeBlock;
            return typedBlock.getVariantState(variant, context.getHorizontalDirection());
        }
        
        return baseState;
    }
    
    /**
     * 自定义物品名称
     * 使用变体的翻译键而不是基础方块的翻译键
     */
    @Override
    public String getDescriptionId() {
        return "item.millenaire_rewrite." + variant.getSerializedName();
    }
}
