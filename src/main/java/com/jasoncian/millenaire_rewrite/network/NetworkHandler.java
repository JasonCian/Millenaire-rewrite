package com.jasoncian.millenaire_rewrite.network;

import com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket;
import com.jasoncian.millenaire_rewrite.network.packet.SimpleSyncPacket;
import com.jasoncian.millenaire_rewrite.network.packet.VillageDataSyncPacket;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 千年村庄网络通信处理器
 * 
 * 负责管理客户端和服务端之间的数据包通信，包括：
 * - 村庄数据同步
 * - 村民状态更新
 * - 经济系统数据传输
 * - GUI交互数据传输
 * 
 * 基于Forge 1.20.1的网络API实现，使用SimpleChannel进行数据包管理。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class NetworkHandler {

    /** 网络处理器专用日志记录器 */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHandler.class);

    /** 网络协议版本号 */
    private static final String PROTOCOL_VERSION = "1";

    /** 网络通道名称 */
    private static final ResourceLocation CHANNEL_NAME = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID,
            "main");

    /** 主网络通道实例 */
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    /** 数据包ID计数器，用于自动分配数据包ID */
    private static int packetId = 0;

    /**
     * 注册所有网络数据包
     * 此方法应在模组初始化阶段调用，用于注册所有需要在网络中传输的数据包类型
     */
    public static void register() {
        LOGGER.info("正在注册千年村庄网络数据包...");

        // 注册简单同步数据包（双向通信）
        INSTANCE.registerMessage(packetId++,
                SimpleSyncPacket.class,
                SimpleSyncPacket::encode,
                SimpleSyncPacket::decode,
                SimpleSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(packetId++,
                SimpleSyncPacket.class,
                SimpleSyncPacket::encode,
                SimpleSyncPacket::decode,
                SimpleSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // 注册村庄数据同步数据包（双向通信）
        INSTANCE.registerMessage(packetId++,
                VillageDataSyncPacket.class,
                VillageDataSyncPacket::encode,
                VillageDataSyncPacket::decode,
                VillageDataSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(packetId++,
                VillageDataSyncPacket.class,
                VillageDataSyncPacket::encode,
                VillageDataSyncPacket::decode,
                VillageDataSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // 注册配置同步数据包（服务端到客户端）
        INSTANCE.registerMessage(packetId++,
                ConfigSyncPacket.class,
                ConfigSyncPacket::toBytes,
                ConfigSyncPacket::new,
                ConfigSyncPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // TODO: 注册其他数据包类型
        // 村民状态更新数据包
        // 经济系统数据包
        // GUI交互数据包
        // 建筑数据同步数据包

        LOGGER.info("千年村庄网络数据包注册完成，共注册 {} 个数据包类型", packetId);
    }

    /**
     * 向客户端发送数据包
     * 
     * @param <MSG>   消息类型
     * @param message 要发送的消息
     * @param target  目标分发器（指定发送给哪些客户端）
     */
    public static <MSG> void sendToClient(MSG message, PacketDistributor.PacketTarget target) {
        try {
            INSTANCE.send(target, message);
            LOGGER.debug("已向客户端发送数据包: {}", message.getClass().getSimpleName());
        } catch (Exception e) {
            LOGGER.error("向客户端发送数据包时发生错误: {}", message.getClass().getSimpleName(), e);
        }
    }

    /**
     * 向服务端发送数据包
     * 
     * @param <MSG>   消息类型
     * @param message 要发送的消息
     */
    public static <MSG> void sendToServer(MSG message) {
        try {
            INSTANCE.sendToServer(message);
            LOGGER.debug("已向服务端发送数据包: {}", message.getClass().getSimpleName());
        } catch (Exception e) {
            LOGGER.error("向服务端发送数据包时发生错误: {}", message.getClass().getSimpleName(), e);
        }
    }

    /**
     * 向所有客户端广播数据包
     * 
     * @param <MSG>   消息类型
     * @param message 要广播的消息
     */
    public static <MSG> void sendToAllClients(MSG message) {
        sendToClient(message, PacketDistributor.ALL.noArg());
    }

    /**
     * 向指定玩家发送数据包
     * 
     * @param <MSG>   消息类型
     * @param message 要发送的消息
     * @param player  目标玩家
     */
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        sendToClient(message, PacketDistributor.PLAYER.with(() -> player));
    }

    /**
     * 向指定维度的所有玩家发送数据包
     * 
     * @param <MSG>     消息类型
     * @param message   要发送的消息
     * @param dimension 目标维度
     */
    public static <MSG> void sendToDimension(MSG message,
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension) {
        sendToClient(message, PacketDistributor.DIMENSION.with(() -> dimension));
    }

    /**
     * 向指定范围内的玩家发送数据包
     * 
     * @param <MSG>     消息类型
     * @param message   要发送的消息
     * @param x         中心X坐标
     * @param y         中心Y坐标
     * @param z         中心Z坐标
     * @param range     发送范围
     * @param dimension 维度
     */
    public static <MSG> void sendToNearby(MSG message, double x, double y, double z, double range,
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension) {
        PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(x, y, z, range, dimension);
        sendToClient(message, PacketDistributor.NEAR.with(() -> targetPoint));
    }

    /**
     * 获取当前注册的数据包数量
     * 
     * @return 已注册的数据包数量
     */
    public static int getRegisteredPacketCount() {
        return packetId;
    }

    /**
     * 获取网络协议版本
     * 
     * @return 协议版本字符串
     */
    public static String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    /**
     * 获取网络通道名称
     * 
     * @return 通道资源位置
     */
    public static ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }
}
