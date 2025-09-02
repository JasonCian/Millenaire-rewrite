package com.jasoncian.millenaire_rewrite.client;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModItems;
import com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端颜色注册和物品属性注册
 * 处理护身符的动态颜色显示和弓的动画属性
 */
@Mod.EventBusSubscriber(modid = MillenaireRewrite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientColorHandlers {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // 注册护身符的颜色处理器
        event.register(DynamicAmuletItem::getColor, 
            ModItems.AMULET_ALCHEMIST.get(),
            ModItems.AMULET_VISHNU.get(),
            ModItems.AMULET_YGGDRASIL.get()
        );
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        // 注册弓的拉弓动画属性
        event.enqueueWork(() -> {
            // pull属性：拉弓的程度（0.0到1.0）
            ItemProperties.register(ModItems.JAPANESE_BOW.get(), 
                ResourceLocation.withDefaultNamespace("pull"), 
                (itemStack, clientWorld, livingEntity, seed) -> {
                    if (livingEntity == null) {
                        return 0.0F;
                    } else {
                        return livingEntity.getUseItem() != itemStack ? 0.0F : 
                            (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
                    }
                });

            // pulling属性：是否正在拉弓（0.0或1.0）
            ItemProperties.register(ModItems.JAPANESE_BOW.get(), 
                ResourceLocation.withDefaultNamespace("pulling"), 
                (itemStack, clientWorld, livingEntity, seed) -> 
                    livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F);
        });
    }
}
