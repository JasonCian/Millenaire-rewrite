package com.jasoncian.millenaire_rewrite.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 网络数据包基础接口
 * 
 * 定义了千年村庄模组中所有网络数据包必须实现的基本方法。
 * 所有自定义数据包都应该实现此接口以确保网络通信的一致性。
 * 
 * @param <T> 数据包的具体类型
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public interface IPacket<T extends IPacket<T>> {

    /**
     * 将数据包内容编码到网络缓冲区
     * 
     * 此方法负责将数据包的所有字段按照指定顺序写入到FriendlyByteBuf中。
     * 编码的顺序必须与decode方法中读取的顺序完全一致。
     * 
     * @param buffer 网络缓冲区，用于写入数据包内容
     */
    void encode(FriendlyByteBuf buffer);

    /**
     * 从网络缓冲区解码数据包内容
     * 
     * 此方法是静态方法，负责从FriendlyByteBuf中读取数据并创建数据包实例。
     * 读取的顺序必须与encode方法中写入的顺序完全一致。
     * 
     * @param buffer 包含数据包内容的网络缓冲区
     * @return 解码后的数据包实例
     */
    static <T extends IPacket<T>> T decode(FriendlyByteBuf buffer) {
        throw new UnsupportedOperationException("子类必须实现此方法");
    }

    /**
     * 处理接收到的数据包
     * 
     * 此方法在数据包接收端被调用，负责执行数据包的具体逻辑。
     * 方法会在主线程中执行，因此可以安全地访问游戏世界和实体。
     * 
     * @param context 网络事件上下文，包含发送者信息和执行环境
     */
    void handle(Supplier<NetworkEvent.Context> context);

    /**
     * 获取数据包的类型标识符
     * 
     * 用于在日志和调试中标识数据包类型。
     * 
     * @return 数据包类型的字符串标识符
     */
    default String getPacketType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 验证数据包内容的有效性
     * 
     * 在数据包处理之前调用，用于验证数据包内容是否有效。
     * 如果数据包内容无效，应该抛出异常或返回false。
     * 
     * @return 如果数据包内容有效则返回true，否则返回false
     */
    default boolean isValid() {
        return true;
    }

    /**
     * 获取数据包的估计大小（以字节为单位）
     * 
     * 用于网络性能监控和调试。子类可以覆盖此方法以提供更准确的大小估计。
     * 
     * @return 数据包的估计大小
     */
    default int getEstimatedSize() {
        return 32; // 默认估计32字节
    }
}
