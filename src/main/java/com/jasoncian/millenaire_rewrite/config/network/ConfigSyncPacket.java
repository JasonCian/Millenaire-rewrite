package com.jasoncian.millenaire_rewrite.config.network;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.config.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 配置同步数据包
 * 
 * 用于在服务端配置变更时同步配置到客户端，实现配置热重载功能。
 * 只同步客户端需要知道的配置项，避免发送敏感的服务端配置。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class ConfigSyncPacket {

    // ==================== 需要同步的配置项 ====================
    
    /** 是否启用经济系统 */
    private final boolean economyEnabled;
    
    /** 村民工作效率倍数 */
    private final double villagerWorkEfficiency;
    
    /** 村民移动速度倍数 */
    private final double villagerMovementSpeed;
    
    /** 文化传播速度 */
    private final double cultureSpreadRate;
    
    /** 文化影响范围 */
    private final int cultureInfluenceRange;
    
    /** 是否启用文化冲突 */
    private final boolean cultureConflictEnabled;

    /**
     * 构造函数 - 从服务端配置创建
     */
    public ConfigSyncPacket() {
        this.economyEnabled = ModConfig.COMMON.economyEnabled.get();
        this.villagerWorkEfficiency = ModConfig.COMMON.villagerWorkEfficiency.get();
        this.villagerMovementSpeed = ModConfig.COMMON.villagerMovementSpeed.get();
        this.cultureSpreadRate = ModConfig.COMMON.cultureSpreadRate.get();
        this.cultureInfluenceRange = ModConfig.COMMON.cultureInfluenceRange.get();
        this.cultureConflictEnabled = ModConfig.COMMON.cultureConflictEnabled.get();
    }

    /**
     * 构造函数 - 从网络数据创建
     * 
     * @param buffer 网络数据缓冲区
     */
    public ConfigSyncPacket(FriendlyByteBuf buffer) {
        this.economyEnabled = buffer.readBoolean();
        this.villagerWorkEfficiency = buffer.readDouble();
        this.villagerMovementSpeed = buffer.readDouble();
        this.cultureSpreadRate = buffer.readDouble();
        this.cultureInfluenceRange = buffer.readVarInt();
        this.cultureConflictEnabled = buffer.readBoolean();
    }

    /**
     * 将数据包写入缓冲区
     * 
     * @param buffer 网络数据缓冲区
     */
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBoolean(economyEnabled);
        buffer.writeDouble(villagerWorkEfficiency);
        buffer.writeDouble(villagerMovementSpeed);
        buffer.writeDouble(cultureSpreadRate);
        buffer.writeVarInt(cultureInfluenceRange);
        buffer.writeBoolean(cultureConflictEnabled);
    }

    /**
     * 处理数据包 - 在客户端执行
     * 
     * @param context 网络上下文
     */
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // 在客户端更新同步的配置值
            // 注意：这里不能直接修改ForgeConfigSpec的值，因为那些是只读的
            // 我们需要创建一个客户端配置缓存来存储同步的值
            
            ClientConfigCache.setEconomyEnabled(economyEnabled);
            ClientConfigCache.setVillagerWorkEfficiency(villagerWorkEfficiency);
            ClientConfigCache.setVillagerMovementSpeed(villagerMovementSpeed);
            ClientConfigCache.setCultureSpreadRate(cultureSpreadRate);
            ClientConfigCache.setCultureInfluenceRange(cultureInfluenceRange);
            ClientConfigCache.setCultureConflictEnabled(cultureConflictEnabled);
            
            if (ModConfig.Helper.isDebugMode()) {
                MillenaireLogger.info(LogCategory.CONFIG, "已从服务端同步配置");
            }
        });
        context.get().setPacketHandled(true);
    }

    /**
     * 客户端配置缓存
     * 存储从服务端同步的配置值，在联机时使用这些值而不是本地配置
     */
    public static class ClientConfigCache {
        
        /** 是否有来自服务端的配置数据 */
        private static boolean hasServerConfig = false;
        
        // 缓存的配置值
        private static boolean economyEnabled = true;
        private static double villagerWorkEfficiency = 1.0;
        private static double villagerMovementSpeed = 1.0;
        private static double cultureSpreadRate = 0.1;
        private static int cultureInfluenceRange = 100;
        private static boolean cultureConflictEnabled = true;
        
        /**
         * 清空服务端配置缓存
         * 在断开连接时调用
         */
        public static void clearServerConfig() {
            hasServerConfig = false;
        }
        
        /**
         * 检查是否有服务端配置数据
         */
        public static boolean hasServerConfig() {
            return hasServerConfig;
        }
        
        // ==================== Setter方法 ====================
        
        public static void setEconomyEnabled(boolean value) {
            economyEnabled = value;
            hasServerConfig = true;
        }
        
        public static void setVillagerWorkEfficiency(double value) {
            villagerWorkEfficiency = value;
            hasServerConfig = true;
        }
        
        public static void setVillagerMovementSpeed(double value) {
            villagerMovementSpeed = value;
            hasServerConfig = true;
        }
        
        public static void setCultureSpreadRate(double value) {
            cultureSpreadRate = value;
            hasServerConfig = true;
        }
        
        public static void setCultureInfluenceRange(int value) {
            cultureInfluenceRange = value;
            hasServerConfig = true;
        }
        
        public static void setCultureConflictEnabled(boolean value) {
            cultureConflictEnabled = value;
            hasServerConfig = true;
        }
        
        // ==================== Getter方法 ====================
        
        /**
         * 获取经济系统启用状态
         * 优先使用服务端同步的值
         */
        public static boolean isEconomyEnabled() {
            return hasServerConfig ? economyEnabled : ModConfig.COMMON.economyEnabled.get();
        }
        
        /**
         * 获取村民工作效率
         * 优先使用服务端同步的值
         */
        public static double getVillagerWorkEfficiency() {
            return hasServerConfig ? villagerWorkEfficiency : ModConfig.COMMON.villagerWorkEfficiency.get();
        }
        
        /**
         * 获取村民移动速度
         * 优先使用服务端同步的值
         */
        public static double getVillagerMovementSpeed() {
            return hasServerConfig ? villagerMovementSpeed : ModConfig.COMMON.villagerMovementSpeed.get();
        }
        
        /**
         * 获取文化传播速度
         * 优先使用服务端同步的值
         */
        public static double getCultureSpreadRate() {
            return hasServerConfig ? cultureSpreadRate : ModConfig.COMMON.cultureSpreadRate.get();
        }
        
        /**
         * 获取文化影响范围
         * 优先使用服务端同步的值
         */
        public static int getCultureInfluenceRange() {
            return hasServerConfig ? cultureInfluenceRange : ModConfig.COMMON.cultureInfluenceRange.get();
        }
        
        /**
         * 获取文化冲突启用状态
         * 优先使用服务端同步的值
         */
        public static boolean isCultureConflictEnabled() {
            return hasServerConfig ? cultureConflictEnabled : ModConfig.COMMON.cultureConflictEnabled.get();
        }
    }
}
