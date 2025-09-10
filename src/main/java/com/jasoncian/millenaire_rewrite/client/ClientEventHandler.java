package com.jasoncian.millenaire_rewrite.client;

import com.jasoncian.millenaire_rewrite.client.renderer.entity.MillVillagerRenderer;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.init.ModEntityTypes;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端事件处理器
 * 
 * 处理模组的客户端专用事件，包括：
 * - 实体渲染器注册
 * - 纹理绑定
 * - 按键绑定
 * 
 * 注意：千年村民现在使用原版玩家模型，无需注册自定义模型层
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    /**
     * 注册实体渲染器
     * 
     * 为所有模组实体注册对应的渲染器，负责在客户端正确显示实体。
     * 
     * @param event 实体渲染器注册事件
     */
    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.CLIENT, "注册实体渲染器...");
        }

        // 注册千年村民实体渲染器（使用玩家模型）
        event.registerEntityRenderer(ModEntityTypes.MILL_VILLAGER.get(), MillVillagerRenderer::new);

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CLIENT, "实体渲染器注册完成");
        }
    }
}
