package com.jasoncian.millenaire_rewrite.client;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import com.jasoncian.millenaire_rewrite.core.ModItems;
import com.jasoncian.millenaire_rewrite.items.magic.DynamicAmuletItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端颜色注册
 * 处理护身符的动态颜色显示
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
}
