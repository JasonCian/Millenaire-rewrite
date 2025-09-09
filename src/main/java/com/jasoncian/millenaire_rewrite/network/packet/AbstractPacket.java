package com.jasoncian.millenaire_rewrite.network.packet;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * 网络数据包抽象基类
 * 
 * 提供数据包的通用实现和工具方法，简化具体数据包类的实现。
 * 所有千年村庄模组的数据包都应该继承自此类。
 * 
 * @param <T> 数据包的具体类型
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public abstract class AbstractPacket<T extends AbstractPacket<T>> implements IPacket<T> {

    /** 数据包专用日志记录器 */
    protected static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 默认构造函数
     */
    protected AbstractPacket() {
        // 空构造函数，用于反射创建实例
    }

    /**
     * 处理网络事件上下文的通用逻辑
     * 
     * 此方法处理网络事件的通用部分，包括验证和错误处理。
     * 子类应该重写 {@link #handleOnMainThread} 方法来实现具体的处理逻辑。
     * 
     * @param context 网络事件上下文
     */
    @Override
    public final void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();

        try {
            // 验证数据包有效性
            if (!isValid()) {
                LOGGER.warn("接收到无效的数据包: {}", getPacketType());
                return;
            }

            // 在主线程中处理数据包
            ctx.enqueueWork(() -> {
                try {
                    handleOnMainThread(ctx);
                    LOGGER.debug("成功处理数据包: {}", getPacketType());
                } catch (Exception e) {
                    LOGGER.error("处理数据包时发生错误: {}", getPacketType(), e);
                }
            });

            // 确认数据包已处理
            ctx.setPacketHandled(true);

        } catch (Exception e) {
            LOGGER.error("处理网络事件时发生错误: {}", getPacketType(), e);
            ctx.setPacketHandled(true);
        }
    }

    /**
     * 在主线程中处理数据包的具体逻辑
     * 
     * 子类必须实现此方法来定义数据包的具体处理逻辑。
     * 此方法在主线程中执行，可以安全地访问游戏世界和实体。
     * 
     * @param context 网络事件上下文
     */
    protected abstract void handleOnMainThread(NetworkEvent.Context context);

    /**
     * 获取数据包的发送方向描述
     * 
     * @param context 网络事件上下文
     * @return 发送方向的字符串描述
     */
    protected String getDirectionDescription(NetworkEvent.Context context) {
        return context.getDirection().getReceptionSide().toString();
    }

    /**
     * 检查数据包是否来自服务端
     * 
     * @param context 网络事件上下文
     * @return 如果数据包来自服务端则返回true
     */
    protected boolean isFromServer(NetworkEvent.Context context) {
        return context.getDirection().getReceptionSide().isClient();
    }

    /**
     * 检查数据包是否来自客户端
     * 
     * @param context 网络事件上下文
     * @return 如果数据包来自客户端则返回true
     */
    protected boolean isFromClient(NetworkEvent.Context context) {
        return context.getDirection().getReceptionSide().isServer();
    }

    /**
     * 安全地写入字符串到缓冲区
     * 
     * @param buffer 网络缓冲区
     * @param value  要写入的字符串值
     */
    protected static void writeString(FriendlyByteBuf buffer, String value) {
        buffer.writeUtf(value != null ? value : "", 32767);
    }

    /**
     * 安全地从缓冲区读取字符串
     * 
     * @param buffer 网络缓冲区
     * @return 读取的字符串值
     */
    protected static String readString(FriendlyByteBuf buffer) {
        return buffer.readUtf(32767);
    }

    /**
     * 安全地写入可选的对象到缓冲区
     * 
     * @param buffer 网络缓冲区
     * @param value  要写入的值（可为null）
     * @param writer 写入器函数
     * @param <V>    值的类型
     */
    protected static <V> void writeOptional(FriendlyByteBuf buffer, V value, Writer<V> writer) {
        if (value != null) {
            buffer.writeBoolean(true);
            writer.write(buffer, value);
        } else {
            buffer.writeBoolean(false);
        }
    }

    /**
     * 安全地从缓冲区读取可选的对象
     * 
     * @param buffer 网络缓冲区
     * @param reader 读取器函数
     * @param <V>    值的类型
     * @return 读取的值（可能为null）
     */
    protected static <V> V readOptional(FriendlyByteBuf buffer, Reader<V> reader) {
        if (buffer.readBoolean()) {
            return reader.read(buffer);
        }
        return null;
    }

    /**
     * 写入器函数接口
     */
    @FunctionalInterface
    protected interface Writer<V> {
        void write(FriendlyByteBuf buffer, V value);
    }

    /**
     * 读取器函数接口
     */
    @FunctionalInterface
    protected interface Reader<V> {
        V read(FriendlyByteBuf buffer);
    }
}
