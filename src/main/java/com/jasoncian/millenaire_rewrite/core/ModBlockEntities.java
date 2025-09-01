package com.jasoncian.millenaire_rewrite.core;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.blockentities.VillageStoneBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组BlockEntity注册器
 * 
 * 负责注册所有Millenaire mod的BlockEntity类型
 * BlockEntity是1.20.1中TileEntity的现代化替代
 */
public class ModBlockEntities {
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MillenaireRewrite.MOD_ID);

    /**
     * Village Stone BlockEntity - 村庄核心方块实体
     * 替代legacy的TileEntityVillageStone
     */
    public static final RegistryObject<BlockEntityType<VillageStoneBlockEntity>> VILLAGE_STONE =
        BLOCK_ENTITIES.register("village_stone", () ->
            BlockEntityType.Builder.of(VillageStoneBlockEntity::new,
                ModBlocks.VILLAGE_STONE.get()).build(null));

    /**
     * 注册所有BlockEntity类型到模组事件总线
     * 
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
