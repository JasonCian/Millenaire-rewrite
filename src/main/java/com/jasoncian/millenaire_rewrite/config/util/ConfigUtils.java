package com.jasoncian.millenaire_rewrite.config.util;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.config.ConfigHelper;
import com.jasoncian.millenaire_rewrite.config.ModConfig;
import net.minecraft.server.level.ServerPlayer;

/**
 * 配置实用工具类
 * 
 * 提供配置系统的高级功能和便捷方法，包括：
 * - 配置热重载演示
 * - 配置性能监控
 * - 配置统计信息
 * - 配置调试工具
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class ConfigUtils {
    
    /**
     * 演示配置热重载功能
     * 注册一个配置监听器，当配置变更时自动执行相应的操作
     */
    public static void demonstrateConfigHotReload() {
        // 注册村民AI配置变更监听器
        ConfigHelper.registerCommonConfigListener(() -> {
            int newInterval = ModConfig.Helper.getVillagerAiUpdateInterval();
            MillenaireLogger.info(LogCategory.CONFIG,
                "配置热重载：村民AI更新间隔已更改为 {} tick", newInterval);
            
            // TODO: 在这里可以通知AI系统更新参数
            // 例如：AIManager.updateTickInterval(newInterval);
        });
        
        // 注册经济系统配置变更监听器
        ConfigHelper.registerCommonConfigListener(() -> {
            boolean economyEnabled = ModConfig.Helper.isEconomyEnabled();
            MillenaireLogger.info(LogCategory.CONFIG,
                "配置热重载：经济系统状态已更改为 {}", economyEnabled ? "启用" : "禁用");
            
            // TODO: 在这里可以启用或禁用经济系统
            // 例如：EconomyManager.setEnabled(economyEnabled);
        });
        
        // 注册渲染配置变更监听器（仅客户端）
        ConfigHelper.registerClientConfigListener(() -> {
            int renderDistance = ModConfig.Helper.getVillagerRenderDistance();
            boolean lodEnabled = ModConfig.Helper.isLodOptimizationEnabled();
            
            MillenaireLogger.info(LogCategory.CONFIG,
                "配置热重载：渲染设置已更新 - 距离: {}, LOD优化: {}", 
                renderDistance, lodEnabled ? "启用" : "禁用");
            
            // TODO: 在这里可以更新渲染器设置
            // 例如：RendererManager.updateSettings(renderDistance, lodEnabled);
        });
    }
    
    /**
     * 获取配置统计信息
     * 
     * @return 配置统计信息字符串
     */
    public static String getConfigStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== 千年村庄配置统计 ===\n");
        
        // 客户端配置统计
        stats.append("客户端配置:\n");
        stats.append("  调试模式: ").append(ModConfig.Helper.isDebugMode()).append("\n");
        stats.append("  村民渲染距离: ").append(ModConfig.Helper.getVillagerRenderDistance()).append("\n");
        stats.append("  LOD优化: ").append(ModConfig.Helper.isLodOptimizationEnabled()).append("\n");
        
        // 通用配置统计
        stats.append("通用配置:\n");
        stats.append("  经济系统: ").append(ModConfig.Helper.isEconomyEnabled()).append("\n");
        stats.append("  村民AI间隔: ").append(ModConfig.Helper.getVillagerAiUpdateInterval()).append("\n");
        stats.append("  文化冲突: ").append(ModConfig.Helper.isCultureConflictEnabled()).append("\n");
        
        // 服务端配置统计
        stats.append("服务端配置:\n");
        stats.append("  村庄生成概率: ").append(String.format("%.2f", ModConfig.Helper.getVillageGenerationChance())).append("\n");
        stats.append("  村庄最大人口: ").append(ModConfig.Helper.getVillageMaxPopulation()).append("\n");
        stats.append("  最大并发村庄: ").append(ModConfig.Helper.getMaxConcurrentVillages()).append("\n");
        
        return stats.toString();
    }
    
    /**
     * 验证配置完整性
     * 检查所有配置值是否在合理范围内
     * 
     * @return 验证结果
     */
    public static ConfigValidationResult validateConfigIntegrity() {
        ConfigValidationResult result = new ConfigValidationResult();
        
        // 验证性能相关配置
        int aiInterval = ModConfig.Helper.getVillagerAiUpdateInterval();
        if (aiInterval < 5 || aiInterval > 200) {
            result.addWarning("村民AI更新间隔可能影响性能: " + aiInterval);
        }
        
        int renderDistance = ModConfig.Helper.getVillagerRenderDistance();
        if (renderDistance > 128) {
            result.addWarning("村民渲染距离可能影响性能: " + renderDistance);
        }
        
        // 验证游戏平衡性配置
        double genChance = ModConfig.Helper.getVillageGenerationChance();
        if (genChance > 0.2) {
            result.addWarning("村庄生成概率过高可能导致世界拥挤: " + genChance);
        }
        
        int maxPopulation = ModConfig.Helper.getVillageMaxPopulation();
        if (maxPopulation > 100) {
            result.addWarning("村庄人口上限过高可能影响性能: " + maxPopulation);
        }
        
        // 验证逻辑一致性
        if (!ModConfig.Helper.isEconomyEnabled() && ModConfig.Helper.isCultureConflictEnabled()) {
            result.addInfo("经济系统禁用时，文化冲突功能可能受限");
        }
        
        return result;
    }
    
    /**
     * 重置配置为默认值（开发调试用）
     * 注意：这个方法仅用于开发和调试，不能在生产环境中随意使用
     */
    public static void resetConfigToDefaults() {
        if (!ModConfig.Helper.isDebugMode()) {
            MillenaireLogger.warn(LogCategory.CONFIG, "非调试模式下不允许重置配置");
            return;
        }
        
        MillenaireLogger.info(LogCategory.CONFIG, "重置配置为默认值（仅调试模式）");
        
        // TODO: 实现配置重置逻辑
        // 注意：ForgeConfigSpec的值是只读的，需要通过其他方式实现重置
        // 可能需要重新加载配置文件或使用特殊的API
    }
    
    /**
     * 为服务器管理员提供配置调试命令
     * 
     * @param player 执行命令的玩家
     * @param args   命令参数
     */
    public static void handleConfigCommand(ServerPlayer player, String[] args) {
        if (args.length == 0) {
            sendMessage(player, "可用的配置命令:");
            sendMessage(player, "/millenaire config stats - 显示配置统计");
            sendMessage(player, "/millenaire config validate - 验证配置");
            sendMessage(player, "/millenaire config sync - 强制同步配置");
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "stats" -> {
                String stats = getConfigStats();
                for (String line : stats.split("\n")) {
                    sendMessage(player, line);
                }
            }
            case "validate" -> {
                ConfigValidationResult result = validateConfigIntegrity();
                sendMessage(player, "配置验证结果:");
                result.getWarnings().forEach(warning -> sendMessage(player, "⚠ " + warning));
                result.getInfos().forEach(info -> sendMessage(player, "ℹ " + info));
                if (result.isValid()) {
                    sendMessage(player, "✓ 配置验证通过");
                }
            }
            case "sync" -> {
                ConfigHelper.syncConfigToPlayer(player);
                sendMessage(player, "配置已强制同步");
            }
            default -> sendMessage(player, "未知的配置命令: " + args[0]);
        }
    }
    
    /**
     * 发送消息给玩家
     */
    private static void sendMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
    }
    
    /**
     * 配置验证结果类
     */
    public static class ConfigValidationResult {
        private final java.util.List<String> warnings = new java.util.ArrayList<>();
        private final java.util.List<String> infos = new java.util.ArrayList<>();
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public void addInfo(String info) {
            infos.add(info);
        }
        
        public java.util.List<String> getWarnings() {
            return warnings;
        }
        
        public java.util.List<String> getInfos() {
            return infos;
        }
        
        public boolean isValid() {
            return warnings.isEmpty();
        }
    }
}
