package com.jasoncian.millenaire_rewrite.api.core;

import com.jasoncian.millenaire_rewrite.api.culture.ICultureAPI;
import com.jasoncian.millenaire_rewrite.api.economy.IEconomyAPI;
import com.jasoncian.millenaire_rewrite.api.village.IVillageAPI;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 千年村庄重写版API管理器
 * 
 * 作为模组API的统一访问入口，管理所有子系统的API接口实例。
 * 提供线程安全的API访问和生命周期管理。
 * 
 * <p><strong>设计原则：</strong>
 * <ul>
 *   <li>单例模式 - 确保全局唯一的API管理器实例</li>
 *   <li>延迟初始化 - 按需创建和初始化API实例</li>
 *   <li>线程安全 - 支持多线程环境下的安全访问</li>
 *   <li>异常安全 - 提供优雅的错误处理和降级机制</li>
 * </ul></p>
 * 
 * <p><strong>使用示例：</strong></p>
 * <pre>{@code
 * // 获取API管理器实例
 * MillenaireAPIManager manager = MillenaireAPIManager.getInstance();
 * 
 * // 访问文化系统API
 * ICultureAPI cultureAPI = manager.getCultureAPI();
 * if (cultureAPI != null) {
 *     Optional<ICulture> culture = cultureAPI.getCulture(cultureId);
 * }
 * }</pre>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public final class MillenaireAPIManager implements IMillenaireAPI {
    
    /** 单例实例 */
    private static volatile MillenaireAPIManager instance;
    
    /** 初始化锁 */
    private static final Object INIT_LOCK = new Object();
    
    /** 文化系统API */
    private volatile ICultureAPI cultureAPI;
    
    /** 村庄系统API */
    private volatile IVillageAPI villageAPI;
    
    /** 经济系统API */
    private volatile IEconomyAPI economyAPI;
    
    /** 初始化状态 */
    private volatile boolean initialized = false;
    
    /**
     * 私有构造函数（单例模式）
     */
    private MillenaireAPIManager() {
        MillenaireLogger.debug(LogCategory.CORE, "Creating MillenaireAPIManager instance");
    }
    
    /**
     * 获取API管理器的单例实例
     * 
     * @return API管理器实例
     */
    @NotNull
    public static MillenaireAPIManager getInstance() {
        if (instance == null) {
            synchronized (INIT_LOCK) {
                if (instance == null) {
                    instance = new MillenaireAPIManager();
                    MillenaireLogger.info(LogCategory.CORE, "MillenaireAPIManager instance created");
                }
            }
        }
        return instance;
    }
    
    /**
     * 初始化API管理器
     * 
     * @return 如果初始化成功则返回true，否则返回false
     */
    public boolean initialize() {
        if (initialized) {
            MillenaireLogger.warn(LogCategory.CORE, "MillenaireAPIManager already initialized");
            return true;
        }
        
        synchronized (INIT_LOCK) {
            if (initialized) {
                return true;
            }
            
            try {
                MillenaireLogger.info(LogCategory.CORE, "Initializing MillenaireAPIManager...");
                
                // 这里将来会初始化各个API实现
                // cultureAPI = new CultureAPIImpl();
                // villageAPI = new VillageAPIImpl();
                // economyAPI = new EconomyAPIImpl();
                
                initialized = true;
                MillenaireLogger.info(LogCategory.CORE, "MillenaireAPIManager initialized successfully");
                return true;
                
            } catch (Exception e) {
                MillenaireLogger.error(LogCategory.CORE, "Failed to initialize MillenaireAPIManager", e);
                return false;
            }
        }
    }
    
    /**
     * 关闭API管理器，清理资源
     */
    public void shutdown() {
        synchronized (INIT_LOCK) {
            if (!initialized) {
                return;
            }
            
            MillenaireLogger.info(LogCategory.CORE, "Shutting down MillenaireAPIManager...");
            
            // 清理API实例
            cultureAPI = null;
            villageAPI = null;
            economyAPI = null;
            
            initialized = false;
            MillenaireLogger.info(LogCategory.CORE, "MillenaireAPIManager shutdown completed");
        }
    }
    
    // ========== API访问方法 ==========
    
    /**
     * 获取文化系统API
     * 
     * @return 文化系统API实例，如果未初始化则返回null
     */
    @Nullable
    public ICultureAPI getCultureAPI() {
        if (!initialized) {
            MillenaireLogger.warn(LogCategory.CORE, "Attempting to access CultureAPI before initialization");
            return null;
        }
        return cultureAPI;
    }
    
    /**
     * 获取村庄系统API
     * 
     * @return 村庄系统API实例，如果未初始化则返回null
     */
    @Nullable
    public IVillageAPI getVillageAPI() {
        if (!initialized) {
            MillenaireLogger.warn(LogCategory.CORE, "Attempting to access VillageAPI before initialization");
            return null;
        }
        return villageAPI;
    }
    
    /**
     * 获取经济系统API
     * 
     * @return 经济系统API实例，如果未初始化则返回null
     */
    @Nullable
    public IEconomyAPI getEconomyAPI() {
        if (!initialized) {
            MillenaireLogger.warn(LogCategory.CORE, "Attempting to access EconomyAPI before initialization");
            return null;
        }
        return economyAPI;
    }
    
    // ========== IMillenaireAPI实现 ==========
    
    @Override
    public String getModId() {
        return ModConstants.MOD_ID;
    }
    
    @Override
    public String getModVersion() {
        return ModConstants.MOD_VERSION;
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public ResourceLocation createModResource(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource path cannot be null or empty");
        }
        return ModConstants.modLoc(path);
    }
}
