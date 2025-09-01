package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeStoneBlock;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeWoodBlock;
import com.jasoncian.millenaire_rewrite.blocks.decorative.DecorativeEarthBlock;
import com.jasoncian.millenaire_rewrite.blocks.decorative.StoneDecorativeVariant;
import com.jasoncian.millenaire_rewrite.blocks.decorative.WoodDecorativeVariant;
import com.jasoncian.millenaire_rewrite.blocks.decorative.EarthDecorativeVariant;
import com.jasoncian.millenaire_rewrite.items.blocks.DecorativeBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组方块物品注册器 - 1.20.1现代化实现
 * 
 * 专门负责注册所有方块的物品形式
 * 采用现代化设计：每个装饰方块变体都有独立的物品注册
 * 
 * 设计理念：
 * - 分离关注点：方块逻辑与物品逻辑分开
 * - 变体独立：每个变体都是独立的物品，便于配方和创造模式管理
 * - 类型安全：使用泛型确保类型安全
 */
public class ModBlockItems {
    
    public static final DeferredRegister<Item> BLOCK_ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, MillenaireRewrite.MOD_ID);

    // ================ 核心功能方块物品 ================
    
    /** Village Stone Block Item - 村庄核心方块物品 */
    public static final RegistryObject<Item> VILLAGE_STONE = BLOCK_ITEMS.register("village_stone",
        () -> new BlockItem(ModBlocks.VILLAGE_STONE.get(), new Item.Properties())
    );

    // ================ 石材装饰方块变体物品 ================
    
    /** 金装饰石块物品 - 高级装饰建筑 */
    public static final RegistryObject<Item> GOLD_ORNAMENT = BLOCK_ITEMS.register("gold_ornament",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_STONE, 
            StoneDecorativeVariant.GOLD_ORNAMENT,
            new Item.Properties()
        )
    );
    
    /** 烧制砖块物品 - 基础建筑材料 */
    public static final RegistryObject<Item> COOKED_BRICK = BLOCK_ITEMS.register("cooked_brick",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_STONE, 
            StoneDecorativeVariant.COOKED_BRICK,
            new Item.Properties()
        )
    );
    
    /** Galianite方块物品 - 特殊魔法材料 */
    public static final RegistryObject<Item> GALIANITE_BLOCK = BLOCK_ITEMS.register("galianite_block",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_STONE, 
            StoneDecorativeVariant.GALIANITE_BLOCK,
            new Item.Properties()
        )
    );

    // ================ 木材装饰方块变体物品 ================
    
    /** 简朴木框架物品 - 基础诺曼建筑 */
    public static final RegistryObject<Item> PLAIN_TIMBER_FRAME = BLOCK_ITEMS.register("plain_timber_frame",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_WOOD, 
            WoodDecorativeVariant.PLAIN_TIMBER_FRAME,
            new Item.Properties()
        )
    );
    
    /** 十字木框架物品 - 高级诺曼建筑 */
    public static final RegistryObject<Item> CROSS_TIMBER_FRAME = BLOCK_ITEMS.register("cross_timber_frame",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_WOOD, 
            WoodDecorativeVariant.CROSS_TIMBER_FRAME,
            new Item.Properties()
        )
    );
    
    /** 茅草物品 - 屋顶材料 */
    public static final RegistryObject<Item> THATCH = BLOCK_ITEMS.register("thatch",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_WOOD, 
            WoodDecorativeVariant.THATCH,
            new Item.Properties()
        )
    );
    
    /** 养蚕架物品 - 日式农业建筑 */
    public static final RegistryObject<Item> SERICULTURE = BLOCK_ITEMS.register("sericulture",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_WOOD, 
            WoodDecorativeVariant.SERICULTURE,
            new Item.Properties()
        )
    );

    // ================ 土质装饰方块变体物品 ================
    
    /** 土墙物品 - 基础建筑材料 */
    public static final RegistryObject<Item> DIRT_WALL = BLOCK_ITEMS.register("dirt_wall",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_EARTH, 
            EarthDecorativeVariant.DIRT_WALL,
            new Item.Properties()
        )
    );
    
    /** 风干砖物品 - 印度风格建筑 */
    public static final RegistryObject<Item> DRIED_BRICK = BLOCK_ITEMS.register("dried_brick",
        () -> new DecorativeBlockItem<>(
            ModBlocks.DECORATIVE_EARTH, 
            EarthDecorativeVariant.DRIED_BRICK,
            new Item.Properties()
        )
    );

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
