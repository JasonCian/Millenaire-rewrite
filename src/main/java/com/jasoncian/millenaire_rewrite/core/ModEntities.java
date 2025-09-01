package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 模组实体注册器
 * 
 * 负责注册所有Millenaire mod的实体类型
 */
public class ModEntities {
    
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MillenaireRewrite.MOD_ID);

    // TODO: 在后续阶段添加村民实体
    // public static final RegistryObject<EntityType<MillenaireVillager>> MILLENAIRE_VILLAGER = ...

    /**
     * 注册所有实体类型到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
