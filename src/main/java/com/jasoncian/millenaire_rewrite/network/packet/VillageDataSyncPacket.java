package com.jasoncian.millenaire_rewrite.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 村庄数据同步数据包
 * 
 * 用于在客户端和服务端之间同步村庄的基础信息。
 * 这个数据包将在村庄创建、更新或玩家进入村庄范围时发送。
 * 
 * 包含的信息：
 * - 村庄ID和名称
 * - 村庄位置和边界
 * - 文化类型
 * - 人口信息
 * - 基础经济状态
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class VillageDataSyncPacket extends AbstractPacket<VillageDataSyncPacket> {

    /** 村庄唯一标识符 */
    private int villageId;

    /** 村庄名称 */
    private String villageName;

    /** 村庄中心位置 */
    private BlockPos centerPos;

    /** 文化类型标识符 */
    private ResourceLocation cultureType;

    /** 当前人口 */
    private int population;

    /** 最大人口容量 */
    private int maxPopulation;

    /** 村庄等级 */
    private int villageLevel;

    /** 村庄状态 */
    private VillageStatus status;

    /** 经济数据（简化版） */
    private Map<String, Integer> economicData;

    /** 同步类型 */
    private SyncType syncType;

    /**
     * 默认构造函数（用于反射）
     */
    public VillageDataSyncPacket() {
        super();
        this.economicData = new HashMap<>();
    }

    /**
     * 创建完整的村庄数据同步数据包
     * 
     * @param villageId     村庄ID
     * @param villageName   村庄名称
     * @param centerPos     村庄中心位置
     * @param cultureType   文化类型
     * @param population    当前人口
     * @param maxPopulation 最大人口
     * @param villageLevel  村庄等级
     * @param status        村庄状态
     * @param economicData  经济数据
     * @param syncType      同步类型
     */
    public VillageDataSyncPacket(int villageId, String villageName, BlockPos centerPos,
            ResourceLocation cultureType, int population, int maxPopulation,
            int villageLevel, VillageStatus status,
            Map<String, Integer> economicData, SyncType syncType) {
        this.villageId = villageId;
        this.villageName = villageName != null ? villageName : "未命名村庄";
        this.centerPos = centerPos != null ? centerPos : BlockPos.ZERO;
        this.cultureType = cultureType;
        this.population = Math.max(0, population);
        this.maxPopulation = Math.max(1, maxPopulation);
        this.villageLevel = Math.max(1, villageLevel);
        this.status = status != null ? status : VillageStatus.NORMAL;
        this.economicData = economicData != null ? new HashMap<>(economicData) : new HashMap<>();
        this.syncType = syncType != null ? syncType : SyncType.FULL_UPDATE;
    }

    /**
     * 创建简化的村庄数据同步数据包（仅基础信息）
     */
    public static VillageDataSyncPacket createBasicSync(int villageId, String villageName,
            BlockPos centerPos, ResourceLocation cultureType) {
        return new VillageDataSyncPacket(villageId, villageName, centerPos, cultureType,
                0, 1, 1, VillageStatus.NORMAL, null, SyncType.BASIC_INFO);
    }

    /**
     * 编码数据包到网络缓冲区
     */
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(villageId);
        writeString(buffer, villageName);
        buffer.writeBlockPos(centerPos);

        // 写入文化类型（可能为null）
        writeOptional(buffer, cultureType, (buf, culture) -> buf.writeResourceLocation(culture));

        buffer.writeInt(population);
        buffer.writeInt(maxPopulation);
        buffer.writeInt(villageLevel);
        buffer.writeEnum(status);
        buffer.writeEnum(syncType);

        // 写入经济数据
        buffer.writeInt(economicData.size());
        for (Map.Entry<String, Integer> entry : economicData.entrySet()) {
            writeString(buffer, entry.getKey());
            buffer.writeInt(entry.getValue());
        }
    }

    /**
     * 从网络缓冲区解码数据包
     * 
     * @param buffer 包含数据包内容的网络缓冲区
     * @return 解码后的数据包实例
     */
    public static VillageDataSyncPacket decode(FriendlyByteBuf buffer) {
        VillageDataSyncPacket packet = new VillageDataSyncPacket();

        packet.villageId = buffer.readInt();
        packet.villageName = readString(buffer);
        packet.centerPos = buffer.readBlockPos();

        // 读取文化类型
        packet.cultureType = readOptional(buffer, FriendlyByteBuf::readResourceLocation);

        packet.population = buffer.readInt();
        packet.maxPopulation = buffer.readInt();
        packet.villageLevel = buffer.readInt();
        packet.status = buffer.readEnum(VillageStatus.class);
        packet.syncType = buffer.readEnum(SyncType.class);

        // 读取经济数据
        int economicDataSize = buffer.readInt();
        packet.economicData = new HashMap<>();
        for (int i = 0; i < economicDataSize; i++) {
            String key = readString(buffer);
            int value = buffer.readInt();
            packet.economicData.put(key, value);
        }

        return packet;
    }

    /**
     * 在主线程中处理数据包
     */
    @Override
    protected void handleOnMainThread(NetworkEvent.Context context) {
        if (isFromServer(context)) {
            // 客户端处理来自服务端的村庄数据
            handleOnClient(context);
        } else if (isFromClient(context)) {
            // 服务端处理来自客户端的村庄数据请求
            handleOnServer(context);
        }
    }

    /**
     * 客户端处理逻辑
     */
    private void handleOnClient(NetworkEvent.Context context) {
        LOGGER.info("客户端接收到村庄数据同步: ID={}, 名称={}, 文化={}, 人口={}/{}",
                villageId, villageName,
                cultureType != null ? cultureType.toString() : "未知",
                population, maxPopulation);

        // TODO: 更新客户端的村庄数据缓存
        // VillageClientManager.updateVillageData(this);

        switch (syncType) {
            case FULL_UPDATE -> {
                // 完整更新村庄数据
                LOGGER.debug("执行村庄完整数据更新: {}", villageId);
            }
            case BASIC_INFO -> {
                // 仅更新基础信息
                LOGGER.debug("执行村庄基础信息更新: {}", villageId);
            }
            case ECONOMIC_UPDATE -> {
                // 仅更新经济数据
                LOGGER.debug("执行村庄经济数据更新: {}", villageId);
            }
            case POPULATION_UPDATE -> {
                // 仅更新人口数据
                LOGGER.debug("执行村庄人口数据更新: {}", villageId);
            }
        }
    }

    /**
     * 服务端处理逻辑
     */
    private void handleOnServer(NetworkEvent.Context context) {
        var sender = context.getSender();
        if (sender == null) {
            LOGGER.warn("接收到VillageDataSyncPacket但发送者为null");
            return;
        }

        LOGGER.info("服务端接收到来自玩家 {} 的村庄数据请求: ID={}",
                sender.getName().getString(), villageId);

        // TODO: 处理客户端的村庄数据请求
        // VillageManager.handleDataRequest(sender, this);
    }

    /**
     * 验证数据包的有效性
     */
    @Override
    public boolean isValid() {
        return villageId >= 0 &&
                villageName != null && villageName.length() <= 64 &&
                centerPos != null &&
                population >= 0 &&
                maxPopulation > 0 &&
                villageLevel > 0 &&
                status != null &&
                syncType != null &&
                economicData != null &&
                economicData.size() <= 20; // 限制经济数据条目数量
    }

    /**
     * 获取数据包的估计大小
     */
    @Override
    public int getEstimatedSize() {
        int baseSize = 4 + 4 + 4 + 4 + 4 + 4 + 1 + 1; // 基础字段
        int nameSize = villageName != null ? villageName.length() * 2 : 0;
        int cultureSize = cultureType != null ? 32 : 1; // ResourceLocation大约32字节
        int economicSize = economicData.size() * 20; // 每个条目约20字节
        return baseSize + nameSize + cultureSize + economicSize;
    }

    // Getter方法
    public int getVillageId() {
        return villageId;
    }

    public String getVillageName() {
        return villageName;
    }

    public BlockPos getCenterPos() {
        return centerPos;
    }

    public ResourceLocation getCultureType() {
        return cultureType;
    }

    public int getPopulation() {
        return population;
    }

    public int getMaxPopulation() {
        return maxPopulation;
    }

    public int getVillageLevel() {
        return villageLevel;
    }

    public VillageStatus getStatus() {
        return status;
    }

    public Map<String, Integer> getEconomicData() {
        return new HashMap<>(economicData);
    }

    public SyncType getSyncType() {
        return syncType;
    }

    /**
     * 村庄状态枚举
     */
    public enum VillageStatus {
        /** 正常运行 */
        NORMAL,
        /** 发展中 */
        GROWING,
        /** 衰落中 */
        DECLINING,
        /** 被攻击 */
        UNDER_ATTACK,
        /** 已废弃 */
        ABANDONED
    }

    /**
     * 同步类型枚举
     */
    public enum SyncType {
        /** 完整更新 */
        FULL_UPDATE,
        /** 基础信息 */
        BASIC_INFO,
        /** 经济更新 */
        ECONOMIC_UPDATE,
        /** 人口更新 */
        POPULATION_UPDATE
    }
}
