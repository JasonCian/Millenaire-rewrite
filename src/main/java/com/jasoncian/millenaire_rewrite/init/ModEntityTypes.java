package com.jasoncian.millenaire_rewrite.init;

import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.entity.villager.MillVillagerEntity;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组实体类型注册表
 * 
 * 负责注册所有模组实体类型，包括：
 * - 村民实体（MillVillagerEntity）
 * - 动物实体
 * - 特殊实体
 * 
 * 使用现代化的DeferredRegister系统确保线程安全的实体类型注册。
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public final class ModEntityTypes {

    /**
     * 实体类型延迟注册器
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModConstants.MOD_ID);

    // ====================== 村民实体 ======================
    
    /**
     * 千年村民实体类型
     * 
     * 基础的村民实体，支持：
     * - 文化相关的外观和行为
     * - 基础移动和交互
     * - 职业系统
     * - 年龄和性别变化
     */
    public static final RegistryObject<EntityType<MillVillagerEntity>> MILL_VILLAGER = 
            ENTITY_TYPES.register("mill_villager", () -> EntityType.Builder.of(
                    MillVillagerEntity::new, 
                    MobCategory.CREATURE
            )
            .sized(0.6F, 1.95F)  // 与原版村民相同的碰撞箱尺寸
            .clientTrackingRange(10)  // 客户端追踪范围
            .updateInterval(3)  // 更新间隔（tick）
            .setShouldReceiveVelocityUpdates(true)
            .fireImmune()  // 是否免疫火焰伤害（可配置）
            .build("mill_villager"));

    // TODO: 后续扩展其他实体
    // public static final RegistryObject<EntityType<MillHorseEntity>> MILL_HORSE = ...
    // public static final RegistryObject<EntityType<MillCowEntity>> MILL_COW = ...

    /**
     * 防止实例化工具类
     */
    private ModEntityTypes() {
        throw new UnsupportedOperationException("实体类型注册表不能被实例化");
    }

    /**
     * 初始化实体类型注册表
     * 
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "注册实体类型注册表...");
        }
        
        ENTITY_TYPES.register(modEventBus);
        
        // 注册属性创建事件监听器
        modEventBus.addListener(ModEntityTypes::onAttributeCreate);
        
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.ENTITY, "实体类型注册表初始化完成");
        }
    }

    /**
     * 实体属性创建事件处理器
     * 
     * 为所有自定义实体注册属性供应器，定义实体的基础属性值。
     * 
     * @param event 实体属性创建事件
     */
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "注册实体属性...");
        }
        
        // 注册村民实体属性
        event.put(MILL_VILLAGER.get(), MillVillagerEntity.createAttributes().build());
        
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.ENTITY, "实体属性注册完成");
        }
    }
}
