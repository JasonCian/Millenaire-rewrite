package com.jasoncian.millenaire_rewrite.config;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * 模组配置管理器
 * 
 * 现代化的配置系统，支持：
 * - 客户端/服务端配置分离
 * - 配置热重载
 * - 类型安全的配置访问
 * - 配置变更事件监听
 * 
 * 配置分为三个层次：
 * - CLIENT: 客户端专用配置（渲染、GUI等）
 * - COMMON: 通用配置（游戏逻辑、平衡性等）
 * - SERVER: 服务端配置（世界生成、性能等）
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    // ==================== 客户端配置 ====================
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    // ==================== 通用配置 ====================
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    // ==================== 服务端配置 ====================
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    // 初始化所有配置
    static {
        // 客户端配置
        var clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();

        // 通用配置
        var commonPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();

        // 服务端配置
        var serverPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }

    /**
     * 客户端配置类
     * 包含所有客户端专用的配置选项
     */
    public static class ClientConfig {
        
        // ========== 渲染配置 ==========
        public final ForgeConfigSpec.BooleanValue enableAdvancedRendering;
        public final ForgeConfigSpec.IntValue villagerRenderDistance;
        public final ForgeConfigSpec.BooleanValue enableParticleEffects;
        public final ForgeConfigSpec.BooleanValue enableLodOptimization;
        
        // ========== GUI配置 ==========
        public final ForgeConfigSpec.BooleanValue enableTooltips;
        public final ForgeConfigSpec.DoubleValue guiScale;
        public final ForgeConfigSpec.BooleanValue enableAnimations;
        
        // ========== 调试配置 ==========
        public final ForgeConfigSpec.BooleanValue debugMode;
        public final ForgeConfigSpec.BooleanValue showDebugInfo;
        public final ForgeConfigSpec.BooleanValue enableDebugRenderer;

        ClientConfig(ForgeConfigSpec.Builder builder) {
            // 渲染配置
            builder.comment("渲染配置").push("rendering");
            
            enableAdvancedRendering = builder
                .comment("是否启用高级渲染功能")
                .define("enableAdvancedRendering", true);
                
            villagerRenderDistance = builder
                .comment("村民实体渲染距离（方块数）")
                .defineInRange("villagerRenderDistance", 64, 16, 256);
                
            enableParticleEffects = builder
                .comment("是否启用粒子效果")
                .define("enableParticleEffects", true);
                
            enableLodOptimization = builder
                .comment("是否启用LOD优化（距离细节层次）")
                .define("enableLodOptimization", true);
                
            builder.pop();
            
            // GUI配置
            builder.comment("用户界面配置").push("gui");
            
            enableTooltips = builder
                .comment("是否显示工具提示")
                .define("enableTooltips", true);
                
            guiScale = builder
                .comment("GUI缩放比例")
                .defineInRange("guiScale", 1.0, 0.5, 3.0);
                
            enableAnimations = builder
                .comment("是否启用GUI动画")
                .define("enableAnimations", true);
                
            builder.pop();
            
            // 调试配置
            builder.comment("调试配置").push("debug");
            
            debugMode = builder
                .comment("是否启用调试模式")
                .define("debugMode", ModConstants.DEBUG_MODE);
                
            showDebugInfo = builder
                .comment("是否显示调试信息")
                .define("showDebugInfo", false);
                
            enableDebugRenderer = builder
                .comment("是否启用调试渲染器")
                .define("enableDebugRenderer", false);
                
            builder.pop();
        }
    }

    /**
     * 通用配置类
     * 包含客户端和服务端都需要的配置选项
     */
    public static class CommonConfig {
        
        // ========== 通用配置 ==========
        public final ForgeConfigSpec.BooleanValue verboseLogging;
        
        // ========== 经济系统配置 ==========
        public final ForgeConfigSpec.BooleanValue economyEnabled;
        public final ForgeConfigSpec.DoubleValue inflationRate;
        public final ForgeConfigSpec.DoubleValue tradeTaxRate;
        public final ForgeConfigSpec.DoubleValue currencyFluctuation;
        
        // ========== 文化系统配置 ==========
        public final ForgeConfigSpec.BooleanValue cultureConflictEnabled;
        public final ForgeConfigSpec.DoubleValue cultureSpreadRate;
        public final ForgeConfigSpec.IntValue cultureInfluenceRange;
        
        // ========== 村民AI配置 ==========
        public final ForgeConfigSpec.IntValue villagerAiUpdateInterval;
        public final ForgeConfigSpec.DoubleValue villagerWorkEfficiency;
        public final ForgeConfigSpec.DoubleValue villagerMovementSpeed;

        CommonConfig(ForgeConfigSpec.Builder builder) {
            // 通用配置
            builder.comment("通用配置").push("general");
            
            verboseLogging = builder
                .comment("是否启用详细日志")
                .define("verboseLogging", false);
                
            builder.pop();
            
            // 经济系统配置
            builder.comment("经济系统配置").push("economy");
            
            economyEnabled = builder
                .comment("是否启用经济系统")
                .define("enabled", true);
                
            inflationRate = builder
                .comment("通胀率（每天的价格变化百分比）")
                .defineInRange("inflationRate", 0.01, -0.1, 0.1);
                
            tradeTaxRate = builder
                .comment("贸易税率（百分比）")
                .defineInRange("tradeTaxRate", 0.05, 0.0, 0.5);
                
            currencyFluctuation = builder
                .comment("货币汇率波动幅度")
                .defineInRange("currencyFluctuation", 0.02, 0.0, 0.2);
                
            builder.pop();
            
            // 文化系统配置
            builder.comment("文化系统配置").push("culture");
            
            cultureConflictEnabled = builder
                .comment("是否启用文化冲突")
                .define("conflictEnabled", true);
                
            cultureSpreadRate = builder
                .comment("文化传播速度")
                .defineInRange("spreadRate", 0.1, 0.0, 1.0);
                
            cultureInfluenceRange = builder
                .comment("文化影响范围（方块数）")
                .defineInRange("influenceRange", 100, 10, 500);
                
            builder.pop();
            
            // 村民AI配置
            builder.comment("村民AI配置").push("villager_ai");
            
            villagerAiUpdateInterval = builder
                .comment("村民AI更新间隔（tick）")
                .defineInRange("aiUpdateInterval", ModConstants.DEFAULT_VILLAGER_AI_UPDATE_INTERVAL, 5, 200);
                
            villagerWorkEfficiency = builder
                .comment("村民工作效率倍数")
                .defineInRange("workEfficiency", 1.0, 0.1, 5.0);
                
            villagerMovementSpeed = builder
                .comment("村民移动速度倍数")
                .defineInRange("movementSpeed", 1.0, 0.1, 3.0);
                
            builder.pop();
        }
    }

    /**
     * 服务端配置类
     * 包含所有服务端专用的配置选项，通常涉及世界生成和性能优化
     */
    public static class ServerConfig {
        
        // ========== 村庄系统配置 ==========
        public final ForgeConfigSpec.DoubleValue villageGenerationChance;
        public final ForgeConfigSpec.IntValue villageMinDistance;
        public final ForgeConfigSpec.IntValue villageMaxDistance;
        public final ForgeConfigSpec.IntValue villageMaxPopulation;
        public final ForgeConfigSpec.IntValue villageUpdateInterval;
        
        // ========== 性能优化配置 ==========
        public final ForgeConfigSpec.IntValue maxConcurrentVillages;
        public final ForgeConfigSpec.BooleanValue enableVillageOptimization;
        public final ForgeConfigSpec.IntValue tickBudgetPerChunk;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            // 村庄系统配置
            builder.comment("村庄系统配置").push("village");
            
            villageGenerationChance = builder
                .comment("村庄生成概率 (0.0-1.0)")
                .defineInRange("generationChance", 0.05, 0.0, 1.0);
                
            villageMinDistance = builder
                .comment("村庄最小间距（区块数）")
                .defineInRange("minDistance", 5, 1, 50);
                
            villageMaxDistance = builder
                .comment("村庄最大间距（区块数）")
                .defineInRange("maxDistance", 15, 5, 100);
                
            villageMaxPopulation = builder
                .comment("村庄人口上限")
                .defineInRange("maxPopulation", 50, 5, 200);
                
            villageUpdateInterval = builder
                .comment("村庄更新间隔（tick）")
                .defineInRange("updateInterval", 100, 20, 1200);
                
            builder.pop();
            
            // 性能优化配置
            builder.comment("性能优化配置").push("performance");
            
            maxConcurrentVillages = builder
                .comment("同时处理的村庄数量上限")
                .defineInRange("maxConcurrentVillages", 10, 1, 50);
                
            enableVillageOptimization = builder
                .comment("是否启用村庄性能优化")
                .define("enableVillageOptimization", true);
                
            tickBudgetPerChunk = builder
                .comment("每个区块的tick预算（微秒）")
                .defineInRange("tickBudgetPerChunk", 1000, 100, 10000);
                
            builder.pop();
        }
    }

    /**
     * 配置加载事件处理
     * 支持配置热重载
     * 
     * @param event 配置事件
     */
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getModId().equals(ModConstants.MOD_ID)) {
            // 根据配置类型处理不同的配置变更
            switch (event.getConfig().getType()) {
                case CLIENT -> handleClientConfigChange();
                case COMMON -> handleCommonConfigChange();
                case SERVER -> handleServerConfigChange();
            }
        }
    }

    /**
     * 处理客户端配置变更
     */
    private static void handleClientConfigChange() {
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CONFIG, "客户端配置已重新加载");
        }
        
        // 清空配置缓存
        ConfigHelper.clearConfigCache();
        
        // 验证配置
        ConfigHelper.validateAllConfigs();
        
        // 通知监听器
        ConfigHelper.notifyClientConfigChanged();
    }

    /**
     * 处理通用配置变更
     */
    private static void handleCommonConfigChange() {
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CONFIG, "通用配置已重新加载");
        }
        
        // 清空配置缓存
        ConfigHelper.clearConfigCache();
        
        // 验证配置
        ConfigHelper.validateAllConfigs();
        
        // 通知监听器
        ConfigHelper.notifyCommonConfigChanged();
    }

    /**
     * 处理服务端配置变更
     */
    private static void handleServerConfigChange() {
        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.info(LogCategory.CONFIG, "服务端配置已重新加载");
        }
        
        // 清空配置缓存
        ConfigHelper.clearConfigCache();
        
        // 验证配置
        ConfigHelper.validateAllConfigs();
        
        // 通知监听器
        ConfigHelper.notifyServerConfigChanged();
        
        // 广播配置更新给所有客户端
        ConfigHelper.broadcastConfigUpdate();
    }

    /**
     * 获取配置值的便捷方法
     */
    public static class Helper {
        
        /**
         * 检查是否启用调试模式
         * @return 是否启用调试模式
         */
        public static boolean isDebugMode() {
            return CLIENT.debugMode.get();
        }
        
        /**
         * 检查是否启用经济系统
         * 在联机时优先使用服务端同步的配置
         * @return 是否启用经济系统
         */
        public static boolean isEconomyEnabled() {
            return com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket.ClientConfigCache.isEconomyEnabled();
        }
        
        /**
         * 获取村庄生成概率
         * @return 村庄生成概率
         */
        public static double getVillageGenerationChance() {
            return SERVER.villageGenerationChance.get();
        }
        
        /**
         * 获取村民AI更新间隔
         * @return 村民AI更新间隔（tick）
         */
        public static int getVillagerAiUpdateInterval() {
            return COMMON.villagerAiUpdateInterval.get();
        }
        
        /**
         * 获取村民渲染距离
         * @return 村民渲染距离（方块数）
         */
        public static int getVillagerRenderDistance() {
            return CLIENT.villagerRenderDistance.get();
        }
        
        /**
         * 获取村民工作效率
         * 在联机时优先使用服务端同步的配置
         * @return 村民工作效率倍数
         */
        public static double getVillagerWorkEfficiency() {
            return com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket.ClientConfigCache.getVillagerWorkEfficiency();
        }
        
        /**
         * 获取村民移动速度
         * 在联机时优先使用服务端同步的配置
         * @return 村民移动速度倍数
         */
        public static double getVillagerMovementSpeed() {
            return com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket.ClientConfigCache.getVillagerMovementSpeed();
        }
        
        /**
         * 获取文化传播速度
         * 在联机时优先使用服务端同步的配置
         * @return 文化传播速度
         */
        public static double getCultureSpreadRate() {
            return com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket.ClientConfigCache.getCultureSpreadRate();
        }
        
        /**
         * 获取文化影响范围
         * 在联机时优先使用服务端同步的配置
         * @return 文化影响范围（方块数）
         */
        public static int getCultureInfluenceRange() {
            return com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket.ClientConfigCache.getCultureInfluenceRange();
        }
        
        /**
         * 检查是否启用文化冲突
         * 在联机时优先使用服务端同步的配置
         * @return 是否启用文化冲突
         */
        public static boolean isCultureConflictEnabled() {
            return com.jasoncian.millenaire_rewrite.config.network.ConfigSyncPacket.ClientConfigCache.isCultureConflictEnabled();
        }
        
        /**
         * 检查是否启用详细日志
         * @return 是否启用详细日志
         */
        public static boolean isVerboseLogging() {
            return COMMON.verboseLogging.get();
        }
        
        /**
         * 获取村庄最大人口
         * @return 村庄人口上限
         */
        public static int getVillageMaxPopulation() {
            return SERVER.villageMaxPopulation.get();
        }
        
        /**
         * 获取村庄更新间隔
         * @return 村庄更新间隔（tick）
         */
        public static int getVillageUpdateInterval() {
            return SERVER.villageUpdateInterval.get();
        }
        
        /**
         * 检查是否启用LOD优化
         * @return 是否启用LOD优化
         */
        public static boolean isLodOptimizationEnabled() {
            return CLIENT.enableLodOptimization.get();
        }
        
        /**
         * 获取最大并发村庄数
         * @return 同时处理的村庄数量上限
         */
        public static int getMaxConcurrentVillages() {
            return SERVER.maxConcurrentVillages.get();
        }
    }
}
