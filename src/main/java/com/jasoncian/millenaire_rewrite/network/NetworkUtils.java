package com.jasoncian.millenaire_rewrite.network;

import com.jasoncian.millenaire_rewrite.network.packet.SimpleSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网络工具类
 * 
 * 提供便捷的网络操作方法，封装常用的网络通信功能。
 * 这些方法简化了数据包的发送过程，并提供了类型安全的接口。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class NetworkUtils {

    /** 网络工具类专用日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);

    /**
     * 私有构造函数，防止实例化工具类
     */
    private NetworkUtils() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    /**
     * 向指定玩家发送简单同步消息
     * 
     * @param player  目标玩家
     * @param message 消息内容
     * @param type    消息类型
     */
    public static void sendSimpleMessage(ServerPlayer player, String message, SimpleSyncPacket.MessageType type) {
        if (player == null) {
            LOGGER.warn("尝试向null玩家发送消息: {}", message);
            return;
        }

        try {
            SimpleSyncPacket packet = new SimpleSyncPacket(message, type);
            NetworkHandler.sendToPlayer(packet, player);
            LOGGER.debug("已向玩家 {} 发送消息: {}", player.getName().getString(), message);
        } catch (Exception e) {
            LOGGER.error("向玩家 {} 发送消息时发生错误: {}", player.getName().getString(), message, e);
        }
    }

    /**
     * 向所有在线玩家广播简单同步消息
     * 
     * @param message 消息内容
     * @param type    消息类型
     */
    public static void broadcastSimpleMessage(String message, SimpleSyncPacket.MessageType type) {
        try {
            SimpleSyncPacket packet = new SimpleSyncPacket(message, type);
            NetworkHandler.sendToAllClients(packet);
            LOGGER.debug("已广播消息: {}", message);
        } catch (Exception e) {
            LOGGER.error("广播消息时发生错误: {}", message, e);
        }
    }

    /**
     * 向指定维度的所有玩家发送简单同步消息
     * 
     * @param dimension 目标维度
     * @param message   消息内容
     * @param type      消息类型
     */
    public static void sendSimpleMessageToDimension(net.minecraft.resources.ResourceKey<Level> dimension,
            String message, SimpleSyncPacket.MessageType type) {
        if (dimension == null) {
            LOGGER.warn("尝试向null维度发送消息: {}", message);
            return;
        }

        try {
            SimpleSyncPacket packet = new SimpleSyncPacket(message, type);
            NetworkHandler.sendToDimension(packet, dimension);
            LOGGER.debug("已向维度 {} 发送消息: {}", dimension.location(), message);
        } catch (Exception e) {
            LOGGER.error("向维度 {} 发送消息时发生错误: {}", dimension.location(), message, e);
        }
    }

    /**
     * 向指定范围内的玩家发送简单同步消息
     * 
     * @param level   世界实例
     * @param x       中心X坐标
     * @param y       中心Y坐标
     * @param z       中心Z坐标
     * @param range   发送范围
     * @param message 消息内容
     * @param type    消息类型
     */
    public static void sendSimpleMessageToNearby(Level level, double x, double y, double z, double range,
            String message, SimpleSyncPacket.MessageType type) {
        if (level == null || level.isClientSide) {
            LOGGER.warn("尝试在客户端或null世界中发送范围消息: {}", message);
            return;
        }

        try {
            SimpleSyncPacket packet = new SimpleSyncPacket(message, type);
            NetworkHandler.sendToNearby(packet, x, y, z, range, level.dimension());
            LOGGER.debug("已向坐标 ({}, {}, {}) 范围 {} 内发送消息: {}", x, y, z, range, message);
        } catch (Exception e) {
            LOGGER.error("向范围内发送消息时发生错误: {}", message, e);
        }
    }

    /**
     * 从客户端向服务端发送简单同步消息
     * 
     * @param message 消息内容
     * @param type    消息类型
     */
    public static void sendSimpleMessageToServer(String message, SimpleSyncPacket.MessageType type) {
        try {
            SimpleSyncPacket packet = new SimpleSyncPacket(message, type);
            NetworkHandler.sendToServer(packet);
            LOGGER.debug("已向服务端发送消息: {}", message);
        } catch (Exception e) {
            LOGGER.error("向服务端发送消息时发生错误: {}", message, e);
        }
    }

    /**
     * 发送ping消息到服务端（用于测试网络连接）
     */
    public static void pingServer() {
        sendSimpleMessageToServer("ping", SimpleSyncPacket.MessageType.PING);
    }

    /**
     * 向玩家发送信息消息
     * 
     * @param player  目标玩家
     * @param message 消息内容
     */
    public static void sendInfoMessage(ServerPlayer player, String message) {
        sendSimpleMessage(player, message, SimpleSyncPacket.MessageType.INFO);
    }

    /**
     * 向玩家发送请求消息
     * 
     * @param player  目标玩家
     * @param message 消息内容
     */
    public static void sendRequestMessage(ServerPlayer player, String message) {
        sendSimpleMessage(player, message, SimpleSyncPacket.MessageType.REQUEST);
    }

    /**
     * 广播信息消息
     * 
     * @param message 消息内容
     */
    public static void broadcastInfo(String message) {
        broadcastSimpleMessage(message, SimpleSyncPacket.MessageType.INFO);
    }

    /**
     * 检查玩家是否可以接收网络消息
     * 
     * @param player 要检查的玩家
     * @return 如果玩家可以接收消息则返回true
     */
    public static boolean canReceiveMessages(Player player) {
        return player != null &&
                !player.isRemoved() &&
                player instanceof ServerPlayer;
    }

    /**
     * 获取网络统计信息
     * 
     * @return 包含网络统计信息的字符串
     */
    public static String getNetworkStats() {
        return String.format("已注册数据包类型: %d, 协议版本: %s",
                NetworkHandler.getRegisteredPacketCount(),
                NetworkHandler.getProtocolVersion());
    }
}
