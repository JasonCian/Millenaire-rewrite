package com.jasoncian.millenaire_rewrite.config.event;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.config.ConfigHelper;
import com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 配置系统事件处理器
 * 
 * 处理与配置系统相关的游戏事件，包括：
 * - 玩家连接时同步服务端配置
 * - 玩家断开连接时清理客户端配置缓存
 * - 配置文件变更时的响应处理
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConfigEventHandler {

    /**
     * 玩家登录事件处理
     * 在玩家成功连接到服务器时同步配置
     * 
     * @param event 玩家登录事件
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            // 延迟一点时间发送配置，确保客户端已经完全加载
            serverPlayer.getServer().execute(() -> {
                try {
                    ConfigHelper.syncConfigToPlayer(serverPlayer);
                } catch (Exception e) {
                    MillenaireLogger.error(LogCategory.CONFIG,
                        "为玩家 {} 同步配置时发生错误", serverPlayer.getName().getString(), e);
                }
            });
        }
    }

    /**
     * 玩家断开连接事件处理
     * 在客户端清理服务端配置缓存
     */
    @Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientConfigEventHandler {

        /**
         * 玩家登出事件处理（客户端）
         * 清理从服务端同步的配置缓存
         * 
         * @param event 玩家登出事件
         */
        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            // 清理客户端配置缓存，回到使用本地配置
            ConfigSyncPacket.ClientConfigCache.clearServerConfig();
            
            if (com.jasoncian.millenaire_rewrite.config.ModConfig.Helper.isDebugMode()) {
                MillenaireLogger.info(LogCategory.CONFIG, "已清理服务端配置缓存，恢复使用本地配置");
            }
        }
    }
}
