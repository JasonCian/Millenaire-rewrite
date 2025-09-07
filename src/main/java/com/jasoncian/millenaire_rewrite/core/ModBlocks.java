package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.blocks.system.BuildingBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 方块注册器 - 管理所有Millenaire mod方块的注册
 *
 * 负责注册所有模组方块，按功能分类组织
 * 采用新的统一建筑方块系统，大幅简化注册流程
 *
 * 功能特性：
 * - 核心功能方块（村庄石等）
 * - 统一建筑方块系统（自动生成所有变体）
 * - 装饰性方块系统
 * - 文化特色建筑方块
 * - 功能性建筑方块
 *
 * 系统优势：
 * - 自动化：一次注册生成完整方块族系（方块+楼梯+台阶+墙）
 * - 统一性：所有建筑方块使用相同的属性系统
 * - 扩展性：易于添加新材料和文化
 * - 高效性：减少重复代码，提高开发效率
 *
 * @author JasonCian
 * @version 2.0.0-stable
 */
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            MillenaireRewrite.MOD_ID);

    // ================ 核心功能方块 ================

    // TODO: 添加真正需要的功能性方块（基于 legacy 代码）
    // 例如：Mill Chest, Import Table 等

    // ================ 统一建筑方块系统（已整合装饰方块） ================

    /*
     * 注意：所有基础建筑方块和装饰方块现在通过BuildingBlockRegistry自动注册
     * 
     * 这个新系统将自动为每种材料生成完整的方块族系：
     * - 基础方块 (例如: stone)
     * - 楼梯方块 (例如: stone_stairs)
     * - 台阶方块 (例如: stone_slab)
     * - 墙方块 (例如: stone_wall)
     * 
     * 支持的材料包括：
     * 
     * 【基础建筑材料】
     * - 石质系列：stone, cobblestone, stone_bricks等
     * - 砂岩系列：sandstone, red_sandstone等
     * - 泥砖系列：mud_bricks, dried_bricks等
     * - 瓦片系列：byzantine_tiles, gray_tiles等
     * - 木质系列：timber_frame, thatch
     * - 特殊系列：ice_bricks, snow_bricks
     * 
     * 【装饰材料（已整合）】
     * - 石质装饰：gold_ornament（金装饰）, galianite_block（Galianite方块）, cooked_bricks（烧制砖）
     * - 木质装饰：simple_timber_frame（简朴木框架）, cross_timber_frame（十字木框架）
     * - 土质装饰：earth_wall（土墙）, dried_bricks（风干砖）
     * 
     * 文化特色分配：
     * - BASIC: 所有基础建筑材料
     * - NORMAN: 石材系列 + 石质装饰 + 木质装饰
     * - BYZANTINE: 拜占庭瓦片 + 金装饰
     * - INDIAN: 红瓦片 + 赭石瓦片 + 土质装饰
     * - JAPANESE: 木框架 + 木质装饰
     * - MAYAN: 石材系列 + Galianite方块
     * - INUIT: 冰砖 + 雪砖
     * 
     * 访问方式：
     * BuildingBlockRegistry.getBlock(material, culture, variant)
     * 
     * 迁移说明：
     * - 原DecorativeStoneBlock的三个变体已拆分为独立材料
     * - 原DecorativeWoodBlock的变体已整合为独立木质材料
     * - 原DecorativeEarthBlock的变体已整合为独立土质材料
     * - 所有装饰方块现在支持完整的变体系统（楼梯、台阶、墙）
     */

    // ================ Legacy装饰方块（已废弃，保留用于参考） ================

    /*
     * 以下装饰方块已迁移到统一建筑方块系统，不再独立注册
     * 
     * DecorativeStoneBlock -> 拆分为：
     * - GOLD_ORNAMENT (金装饰)
     * - COOKED_BRICKS (烧制砖)
     * - GALIANITE_BLOCK (Galianite方块)
     * 
     * DecorativeWoodBlock -> 拆分为：
     * - SIMPLE_TIMBER_FRAME (简朴木框架)
     * - CROSS_TIMBER_FRAME (十字木框架)
     * - THATCH (茅草，已存在)
     * 
     * DecorativeEarthBlock -> 拆分为：
     * - EARTH_WALL (土墙)
     * - DRIED_BRICKS (风干砖)
     * - MUD_BRICKS (泥砖，已存在)
     */

    // ================ 初始化方法 ================

    /**
     * 初始化统一建筑方块系统
     * 
     * 必须在注册阶段早期调用，用于设置所有建筑方块的自动注册
     */
    public static void initializeBuildingBlocks() {
        // 注册所有建筑方块族系
        BuildingBlockRegistry.registerAllBlocks();
    }

    /**
     * 注册所有方块到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        // 首先初始化建筑方块系统
        initializeBuildingBlocks();

        // 注册传统方块
        BLOCKS.register(eventBus);

        // 注册建筑方块系统
        BuildingBlockRegistry.BLOCKS.register(eventBus);
    }

    // ================ 已完成任务 ================

    // ✅ COMPLETED: 装饰方块系统已成功迁移到统一系统
    // ✅ COMPLETED: Village Stone已迁移到新系统
    // ✅ COMPLETED: 数据生成器已更新以支持新系统

    // ================ 待完成任务 ================

    // TODO: 添加路径系统整合到统一方块系统
    // TODO: 添加功能性方块（Mill Chest, Mill Sign等）
    // TODO: 添加特殊功能方块（农作物、特殊装饰等）
    // TODO: 完善方块物品注册系统以支持新的统一方块系统
    // TODO: 更新数据生成器以支持新的方块注册模式
}
