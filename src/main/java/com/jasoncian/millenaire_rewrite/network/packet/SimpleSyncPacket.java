package com.jasoncian.millenaire_rewrite.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

/**
 * 简单同步数据包
 * 
 * 用于演示网络通信层的基础功能，实现客户端和服务端之间的简单数据同步。
 * 这是一个示例数据包，展示了如何正确实现数据包的编码、解码和处理逻辑。
 * 
 * 功能：
 * - 从客户端向服务端发送简单的字符串消息
 * - 服务端接收后向发送者返回确认消息
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class SimpleSyncPacket extends AbstractPacket<SimpleSyncPacket> {

    /** 消息内容 */
    private String message;

    /** 发送时间戳 */
    private long timestamp;

    /** 消息类型 */
    private MessageType type;

    /**
     * 默认构造函数（用于反射）
     */
    public SimpleSyncPacket() {
        super();
    }

    /**
     * 构造一个简单同步数据包
     * 
     * @param message 要发送的消息内容
     * @param type    消息类型
     */
    public SimpleSyncPacket(String message, MessageType type) {
        this.message = message != null ? message : "";
        this.type = type != null ? type : MessageType.INFO;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 编码数据包到网络缓冲区
     */
    @Override
    public void encode(FriendlyByteBuf buffer) {
        writeString(buffer, message);
        buffer.writeLong(timestamp);
        buffer.writeEnum(type);
    }

    /**
     * 从网络缓冲区解码数据包
     * 
     * @param buffer 包含数据包内容的网络缓冲区
     * @return 解码后的数据包实例
     */
    public static SimpleSyncPacket decode(FriendlyByteBuf buffer) {
        SimpleSyncPacket packet = new SimpleSyncPacket();
        packet.message = readString(buffer);
        packet.timestamp = buffer.readLong();
        packet.type = buffer.readEnum(MessageType.class);
        return packet;
    }

    /**
     * 在主线程中处理数据包
     */
    @Override
    protected void handleOnMainThread(NetworkEvent.Context context) {
        if (isFromClient(context)) {
            // 服务端处理来自客户端的消息
            handleOnServer(context);
        } else if (isFromServer(context)) {
            // 客户端处理来自服务端的消息
            handleOnClient(context);
        }
    }

    /**
     * 服务端处理逻辑
     */
    private void handleOnServer(NetworkEvent.Context context) {
        ServerPlayer sender = context.getSender();
        if (sender == null) {
            LOGGER.warn("接收到SimpleSyncPacket但发送者为null");
            return;
        }

        LOGGER.info("服务端接收到来自玩家 {} 的消息: {} (类型: {})",
                sender.getName().getString(), message, type);

        // 处理不同类型的消息
        switch (type) {
            case PING -> {
                // 响应ping消息
                sender.sendSystemMessage(Component.literal("千年村庄: 收到你的ping消息！"));
            }
            case REQUEST -> {
                // 处理请求消息
                sender.sendSystemMessage(Component.literal("千年村庄: 正在处理你的请求: " + message));
            }
            case INFO -> {
                // 处理信息消息
                sender.sendSystemMessage(Component.literal("千年村庄: 收到信息 - " + message));
            }
        }
    }

    /**
     * 客户端处理逻辑
     */
    private void handleOnClient(NetworkEvent.Context context) {
        LOGGER.info("客户端接收到来自服务端的消息: {} (类型: {})", message, type);

        // 在客户端显示消息（这里可以添加GUI更新等逻辑）
        // 注意：在实际实现中，这里应该调用客户端专用的代码
    }

    /**
     * 验证数据包的有效性
     */
    @Override
    public boolean isValid() {
        return message != null &&
                type != null &&
                timestamp > 0 &&
                message.length() <= 256; // 限制消息长度
    }

    /**
     * 获取数据包的估计大小
     */
    @Override
    public int getEstimatedSize() {
        int messageSize = message != null ? message.length() * 2 : 0; // UTF-8大概估计
        return messageSize + 8 + 4; // message + long + enum
    }

    // Getter方法
    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MessageType getType() {
        return type;
    }

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /** 信息消息 */
        INFO,
        /** 请求消息 */
        REQUEST,
        /** Ping消息 */
        PING
    }
}
