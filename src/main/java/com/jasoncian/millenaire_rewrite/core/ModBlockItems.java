package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
// import com.jasoncian.millenaire_rewrite.blocks.decorative.StoneDecorativeVariant;
// import com.jasoncian.millenaire_rewrite.blocks.decorative.WoodDecorativeVariant;
// import com.jasoncian.millenaire_rewrite.blocks.decorative.EarthDecorativeVariant;
// import com.jasoncian.millenaire_rewrite.items.blocks.DecorativeBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * ⚠️  DEPRECATED - 已弃用的方块物品注册器
 * 
 * 此类已被新的统一方块系统替代。装饰方块物品现在通过
 * ModBlocks 中的 BuildingBlockRegistry 自动生成。
 * 
 * 新系统优势：
 * - 自动为每个材料生成方块+楼梯+半砖+墙的完整族
 * - 物品自动注册，无需手动逐个添加
 * - 按文化分类到不同创造模式选项卡
 * 
 * 迁移说明：
 * - 装饰方块变体现在通过 BasicBuildingMaterial 枚举定义
 * - 物品注册自动化，由 BuildingBlockRegistry 处理
 * - 创造模式选项卡按文化自动分组
 * 
 * @author JasonCian
 * @version 0.1.0-alpha
 * @deprecated 使用 {@link ModBlocks} 中的统一方块系统替代
 * @see ModBlocks
 * @see com.jasoncian.millenaire_rewrite.blocks.building.BuildingBlockRegistry
 */
@Deprecated
public class ModBlockItems {

    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            MillenaireRewrite.MOD_ID);

    // ================ 核心功能方块物品 ================

    // TODO: 添加真正需要的功能方块物品（基于 legacy 代码研究）

    // ================ 装饰方块变体物品 - 已弃用 ================
    // ⚠️  装饰方块物品注册已迁移至 ModBlocks 的统一方块系统
    // 新系统会自动为每个 BasicBuildingMaterial 生成对应的物品
    // 无需手动逐个注册每个变体的物品
    
    // 迁移说明：
    // - 原有的金装饰、烧制砖、Galianite方块等已迁移为独立材料
    // - 原有的木框架、茅草、养蚕架等已迁移为独立材料
    // - 原有的土墙、风干砖等已迁移为独立材料
    // - 每个材料自动生成：基础方块 + 楼梯 + 半砖 + 墙 + 对应物品

    // TODO: 后续添加更多方块变体物品
    // TODO: 添加路径系统方块物品
    // TODO: 添加功能性方块物品（Mill Chest, Mill Sign等）

    /**
     * 注册所有方块物品到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ITEMS.register(eventBus);
    }
}
