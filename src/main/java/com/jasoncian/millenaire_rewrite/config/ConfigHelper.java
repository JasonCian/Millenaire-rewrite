package com.jasoncian.millenaire_rewrite.config;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置助手类
 * 
 * 提供配置系统的便捷工具方法，包括：
 * - 配置变更监听器管理
 * - 配置值缓存
 * - 配置同步工具
 * - 配置验证
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class ConfigHelper {
    
    // ==================== 配置变更监听器 ====================
    
    /** 客户端配置变更监听器列表 */
    private static final List<Runnable> CLIENT_CONFIG_LISTENERS = new ArrayList<>();
    
    /** 通用配置变更监听器列表 */
    private static final List<Runnable> COMMON_CONFIG_LISTENERS = new ArrayList<>();
    
    /** 服务端配置变更监听器列表 */
    private static final List<Runnable> SERVER_CONFIG_LISTENERS = new ArrayList<>();
    
    // ==================== 配置监听器注册 ====================
    
    /**
     * 注册客户端配置变更监听器
     * 
     * @param listener 监听器回调函数
     */
    public static void registerClientConfigListener(Runnable listener) {
        CLIENT_CONFIG_LISTENERS.add(listener);
    }
    
    /**
     * 注册通用配置变更监听器
     * 
     * @param listener 监听器回调函数
     */
    public static void registerCommonConfigListener(Runnable listener) {
        COMMON_CONFIG_LISTENERS.add(listener);
    }
    
    /**
     * 注册服务端配置变更监听器
     * 
     * @param listener 监听器回调函数
     */
    public static void registerServerConfigListener(Runnable listener) {
        SERVER_CONFIG_LISTENERS.add(listener);
    }
    
    // ==================== 配置变更通知 ====================
    
    /**
     * 通知客户端配置已变更
     * 在客户端配置重新加载时调用
     */
    public static void notifyClientConfigChanged() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CLIENT_CONFIG_LISTENERS.forEach(listener -> {
                try {
                    listener.run();
                } catch (Exception e) {
                    MillenaireLogger.error(LogCategory.CONFIG, "客户端配置监听器执行失败", e);
                }
            });
            
            if (ModConfig.Helper.isDebugMode()) {
                MillenaireLogger.info(LogCategory.CONFIG, "已通知 {} 个客户端配置监听器", CLIENT_CONFIG_LISTENERS.size());
            }
        }
    }
    
    /**
     * 通知通用配置已变更
     * 在通用配置重新加载时调用
     */
    public static void notifyCommonConfigChanged() {
        COMMON_CONFIG_LISTENERS.forEach(listener -> {
            try {
                listener.run();
            } catch (Exception e) {
                MillenaireLogger.error(LogCategory.CONFIG, "通用配置监听器执行失败", e);
            }
        });
        
        if (ModConfig.Helper.isDebugMode()) {
            MillenaireLogger.info(LogCategory.CONFIG, "已通知 {} 个通用配置监听器", COMMON_CONFIG_LISTENERS.size());
        }
    }
    
    /**
     * 通知服务端配置已变更
     * 在服务端配置重新加载时调用
     */
    public static void notifyServerConfigChanged() {
        SERVER_CONFIG_LISTENERS.forEach(listener -> {
            try {
                listener.run();
            } catch (Exception e) {
                MillenaireLogger.error(LogCategory.CONFIG, "服务端配置监听器执行失败", e);
            }
        });
        
        if (ModConfig.Helper.isDebugMode()) {
            MillenaireLogger.info(LogCategory.CONFIG, "已通知 {} 个服务端配置监听器", SERVER_CONFIG_LISTENERS.size());
        }
    }
    
    // ==================== 配置值工具方法 ====================
    
    /**
     * 安全获取配置值，带默认值
     * 
     * @param configSupplier 配置值提供者
     * @param defaultValue 默认值
     * @param <T> 值类型
     * @return 配置值或默认值
     */
    public static <T> T getSafeConfigValue(java.util.function.Supplier<T> configSupplier, T defaultValue) {
        try {
            T value = configSupplier.get();
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            MillenaireLogger.warn(LogCategory.CONFIG, "获取配置值失败，使用默认值", e);
            return defaultValue;
        }
    }
    
    /**
     * 验证数值范围
     * 
     * @param value 要验证的值
     * @param min 最小值
     * @param max 最大值
     * @return 验证通过返回true
     */
    public static boolean validateRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * 验证数值范围
     * 
     * @param value 要验证的值
     * @param min 最小值
     * @param max 最大值
     * @return 验证通过返回true
     */
    public static boolean validateRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    // ==================== 配置同步工具 ====================
    
    /**
     * 同步配置给指定玩家
     * 用于在玩家加入服务器时同步服务端配置
     * 
     * @param player 目标玩家
     */
    public static void syncConfigToPlayer(ServerPlayer player) {
        try {
            var configPacket = new com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket();
            com.jasoncian.millenaire_rewrite.network.NetworkHandler.sendToPlayer(configPacket, player);
            
            if (ModConfig.Helper.isDebugMode()) {
                MillenaireLogger.info(LogCategory.CONFIG, "同步配置给玩家: {}", player.getName().getString());
            }
        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CONFIG, "同步配置给玩家时发生错误", e);
        }
    }
    
    /**
     * 向所有玩家广播配置更新
     * 用于在服务端配置变更时通知所有客户端
     */
    public static void broadcastConfigUpdate() {
        try {
            var configPacket = new com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket();
            com.jasoncian.millenaire_rewrite.network.NetworkHandler.sendToAllClients(configPacket);
            
            if (ModConfig.Helper.isDebugMode()) {
                MillenaireLogger.info(LogCategory.CONFIG, "广播配置更新给所有玩家");
            }
        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CONFIG, "广播配置更新时发生错误", e);
        }
    }
    
    // ==================== 配置验证 ====================
    
    /**
     * 验证所有配置的有效性
     * 在配置加载后调用，确保配置值在合理范围内
     * 
     * @return 验证通过返回true
     */
    public static boolean validateAllConfigs() {
        boolean valid = true;
        
        try {
            // 验证客户端配置
            if (FMLEnvironment.dist == Dist.CLIENT) {
                valid &= validateClientConfig();
            }
            
            // 验证通用配置
            valid &= validateCommonConfig();
            
            // 验证服务端配置
            if (!FMLEnvironment.dist.isClient()) {
                valid &= validateServerConfig();
            }
            
        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CONFIG, "配置验证过程中发生错误", e);
            valid = false;
        }
        
        if (!valid) {
            MillenaireLogger.warn(LogCategory.CONFIG, "配置验证失败，某些配置值可能无效");
        }
        
        return valid;
    }
    
    /**
     * 验证客户端配置
     */
    private static boolean validateClientConfig() {
        boolean valid = true;
        
        // 验证渲染距离
        int renderDistance = ModConfig.CLIENT.villagerRenderDistance.get();
        if (!validateRange(renderDistance, 16, 256)) {
            MillenaireLogger.warn(LogCategory.CONFIG, "村民渲染距离无效: {}", renderDistance);
            valid = false;
        }
        
        // 验证GUI缩放
        double guiScale = ModConfig.CLIENT.guiScale.get();
        if (!validateRange(guiScale, 0.5, 3.0)) {
            MillenaireLogger.warn(LogCategory.CONFIG, "GUI缩放比例无效: {}", guiScale);
            valid = false;
        }
        
        return valid;
    }
    
    /**
     * 验证通用配置
     */
    private static boolean validateCommonConfig() {
        boolean valid = true;
        
        // 验证通胀率
        double inflationRate = ModConfig.COMMON.inflationRate.get();
        if (!validateRange(inflationRate, -0.1, 0.1)) {
            MillenaireLogger.warn(LogCategory.CONFIG, "通胀率无效: {}", inflationRate);
            valid = false;
        }
        
        // 验证村民AI更新间隔
        int aiInterval = ModConfig.COMMON.villagerAiUpdateInterval.get();
        if (!validateRange(aiInterval, 5, 200)) {
            MillenaireLogger.warn(LogCategory.CONFIG, "村民AI更新间隔无效: {}", aiInterval);
            valid = false;
        }
        
        return valid;
    }
    
    /**
     * 验证服务端配置
     */
    private static boolean validateServerConfig() {
        boolean valid = true;
        
        // 验证村庄生成概率
        double genChance = ModConfig.SERVER.villageGenerationChance.get();
        if (!validateRange(genChance, 0.0, 1.0)) {
            MillenaireLogger.warn(LogCategory.CONFIG, "村庄生成概率无效: {}", genChance);
            valid = false;
        }
        
        // 验证村庄距离配置
        int minDistance = ModConfig.SERVER.villageMinDistance.get();
        int maxDistance = ModConfig.SERVER.villageMaxDistance.get();
        if (minDistance >= maxDistance) {
            MillenaireLogger.warn(LogCategory.CONFIG, "村庄最小距离 {} 应小于最大距离 {}", minDistance, maxDistance);
            valid = false;
        }
        
        return valid;
    }
    
    // ==================== 配置缓存管理 ====================
    
    /** 配置值缓存 */
    private static final java.util.Map<String, Object> CONFIG_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    
    /**
     * 清空配置缓存
     * 在配置重新加载时调用
     */
    public static void clearConfigCache() {
        CONFIG_CACHE.clear();
        if (ModConfig.Helper.isDebugMode()) {
            MillenaireLogger.info(LogCategory.CONFIG, "配置缓存已清空");
        }
    }
    
    /**
     * 获取缓存的配置值
     * 
     * @param key 缓存键
     * @param valueSupplier 值提供者
     * @param <T> 值类型
     * @return 缓存值或新计算的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCachedValue(String key, java.util.function.Supplier<T> valueSupplier) {
        return (T) CONFIG_CACHE.computeIfAbsent(key, k -> valueSupplier.get());
    }
}
