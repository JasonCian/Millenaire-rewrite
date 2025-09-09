package com.jasoncian.millenaire_rewrite.init;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * 模组注册表统一管理器
 * 
 * 负责初始化和管理所有模组相关的注册表，包括：
 * - 物品注册表
 * - 方块注册表
 * - 方块实体注册表
 * - 实体类型注册表
 * - 创造模式物品栏注册表
 * - 其他自定义注册表
 * 
 * 采用现代化的DeferredRegister方式进行注册，确保线程安全和性能优化。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class ModRegistries {

    // 防止实例化工具类
    private ModRegistries() {
        throw new UnsupportedOperationException("注册表管理器不能被实例化");
    }

    /**
     * 初始化所有模组注册表
     * 
     * 此方法应该在模组构造函数中调用，按照依赖顺序初始化各个注册表。
     * 
     * @param modEventBus 模组事件总线，用于注册DeferredRegister
     */
    public static void init(IEventBus modEventBus) {
        // 记录注册开始
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CORE, "开始注册模组内容...");
        }

        // 按照依赖顺序初始化注册表

        // 1. 物品注册表（基础，其他注册表可能依赖）
        ModItems.init(modEventBus);

        // 2. 方块注册表（可能依赖物品）
        ModBlocks.init(modEventBus);

        // 3. 方块实体注册表（依赖方块）
        ModBlockEntities.init(modEventBus);

        // 4. 实体类型注册表
        // TODO: ModEntityTypes.init(modEventBus);

        // 5. 声音事件注册表
        // TODO: ModSounds.init(modEventBus);

        // 6. 药水效果注册表
        // TODO: ModEffects.init(modEventBus);

        // 7. 附魔注册表
        // TODO: ModEnchantments.init(modEventBus);

        // 8. 生物群系注册表
        // TODO: ModBiomes.init(modEventBus);

        // 9. 结构注册表
        // TODO: ModStructures.init(modEventBus);

        // 10. 创造模式物品栏注册表（最后注册，依赖物品和方块）
        ModCreativeTabs.init(modEventBus);

        // 11. 自定义注册表
        // TODO: 初始化文化注册表
        // TODO: 初始化村庄类型注册表
        // TODO: 初始化建筑类型注册表

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CORE, "模组内容注册完成");
        }
    }
}
