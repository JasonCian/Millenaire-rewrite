# 千年村庄重写版 - 网络通信层

## 概述

网络通信层是千年村庄重写版模组的核心基础设施之一，负责客户端和服务端之间的数据同步和通信。本系统基于 Forge 1.20.1 的现代网络 API 构建，提供了类型安全、高效的数据传输机制。

## 架构设计

### 核心组件

1. **NetworkHandler** - 网络处理器主类

   - 管理网络通道和数据包注册
   - 提供数据包发送的便捷方法
   - 处理网络协议版本控制

2. **IPacket** - 数据包接口

   - 定义所有数据包必须实现的基本方法
   - 确保数据包编解码的一致性

3. **AbstractPacket** - 抽象数据包基类

   - 提供通用的错误处理和验证逻辑
   - 简化具体数据包的实现

4. **NetworkUtils** - 网络工具类
   - 封装常用的网络操作
   - 提供类型安全的便捷方法

## 已实现的数据包

### SimpleSyncPacket

基础同步数据包，用于简单的消息传递：

- 支持不同消息类型（INFO、REQUEST、PING）
- 包含时间戳和消息验证
- 双向通信支持

### VillageDataSyncPacket

村庄数据同步数据包，用于村庄信息传输：

- 完整的村庄信息（ID、名称、位置、文化等）
- 人口和经济数据
- 多种同步类型支持
- 数据有效性验证

## 使用示例

### 发送简单消息

```java
// 向玩家发送信息消息
NetworkUtils.sendInfoMessage(player, "欢迎来到千年村庄！");

// 向所有客户端广播消息
NetworkUtils.broadcastInfo("服务器重启通知");

// 从客户端向服务端发送ping
NetworkUtils.pingServer();
```

### 发送村庄数据

```java
// 创建基础村庄信息
VillageDataSyncPacket basicVillage = VillageDataSyncPacket.createBasicSync(
    1001,
    "诺曼村庄",
    new BlockPos(100, 64, 200),
    ResourceLocation.fromNamespaceAndPath("millenaire_rewrite", "norman")
);

// 发送到指定玩家
NetworkHandler.sendToPlayer(basicVillage, player);
```

### 创建自定义数据包

```java
public class CustomPacket extends AbstractPacket<CustomPacket> {
    private String data;

    public CustomPacket(String data) {
        this.data = data;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        writeString(buffer, data);
    }

    public static CustomPacket decode(FriendlyByteBuf buffer) {
        return new CustomPacket(readString(buffer));
    }

    @Override
    protected void handleOnMainThread(NetworkEvent.Context context) {
        // 处理逻辑
    }
}
```

## 测试功能

使用内置的网络测试命令来验证通信层功能：

```
/millenaire network ping                    # 测试基础连接
/millenaire network message <消息>         # 发送测试消息
/millenaire network village test           # 测试村庄数据
/millenaire network village sync           # 测试完整同步
/millenaire network stats                  # 显示统计信息
/millenaire network broadcast <消息>       # 广播消息
```

## 技术特性

### 数据包注册

- 自动分配数据包 ID
- 支持双向通信
- 版本兼容性检查

### 错误处理

- 全面的异常捕获和日志记录
- 数据包有效性验证
- 网络异常恢复机制

### 性能优化

- 数据包大小估计和监控
- 缓冲区操作优化
- 内存使用控制

### 安全性

- 数据包内容验证
- 权限检查支持
- 防止恶意数据注入

## 扩展指南

### 添加新数据包类型

1. 继承 AbstractPacket 类
2. 实现 encode/decode 方法
3. 实现 handleOnMainThread 方法
4. 在 NetworkHandler 中注册

### 添加网络工具方法

在 NetworkUtils 类中添加特定用途的便捷方法，遵循现有的命名和错误处理模式。

## 依赖关系

- **前置条件**: ModConstants、基础注册系统
- **使用者**: 村庄系统、GUI 系统、经济系统等

## 开发状态

✅ **已完成**:

- 基础网络框架
- 数据包基类和接口
- 简单同步数据包
- 村庄数据同步数据包
- 网络工具类
- 测试命令

🚧 **开发中**:

- 更多业务数据包类型
- 性能监控和优化
- 客户端专用处理逻辑

📋 **计划中**:

- 高级数据压缩
- 数据包缓存机制
- 网络诊断工具
- 更详细的统计信息

## 注意事项

1. 所有数据包必须进行有效性验证
2. 网络操作应该包含适当的错误处理
3. 大数据包应该考虑分片传输
4. 客户端专用代码需要正确的环境检查
5. 服务端数据包处理需要权限验证

## 文档更新

- **创建日期**: 2025-09-09
- **最后更新**: 2025-09-09
- **版本**: 0.1.4-alpha
- **作者**: JasonCian
