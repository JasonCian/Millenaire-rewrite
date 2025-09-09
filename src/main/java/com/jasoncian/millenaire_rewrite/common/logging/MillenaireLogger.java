package com.jasoncian.millenaire_rewrite.common.logging;

import com.jasoncian.millenaire_rewrite.util.ModConstants;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * 千年村庄重写版日志管理器
 * 
 * 提供统一的日志记录功能，支持不同级别和类型的日志输出。
 * 使用SLF4J作为日志框架，支持标记化日志和结构化输出。
 * 
 * <p>
 * <strong>日志特性：</strong>
 * <ul>
 * <li>分类日志记录 - 根据功能模块区分日志类型</li>
 * <li>可配置的日志级别 - 支持调试、信息、警告、错误等级别</li>
 * <li>标记化日志 - 支持日志分类和过滤</li>
 * <li>性能友好 - 避免不必要的字符串构造</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <strong>使用示例：</strong>
 * </p>
 * 
 * <pre>{@code
 * // 基础日志记录
 * MillenaireLogger.info("Village created: {}", villageName);
 * 
 * // 带标记的日志记录
 * MillenaireLogger.debug(LogCategory.VILLAGE, "Village population: {}", population);
 * 
 * // 错误日志记录
 * MillenaireLogger.error("Failed to save village data", exception);
 * }</pre>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class MillenaireLogger {

    /** 主日志记录器 */
    private static final Logger LOGGER = LogUtils.getLogger();

    // 防止实例化工具类
    private MillenaireLogger() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    // ========== 基础日志方法 ==========

    /**
     * 记录调试级别日志
     * 
     * @param message 日志消息
     * @param args    消息参数
     */
    public static void debug(String message, Object... args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录信息级别日志
     * 
     * @param message 日志消息
     * @param args    消息参数
     */
    public static void info(String message, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录警告级别日志
     * 
     * @param message 日志消息
     * @param args    消息参数
     */
    public static void warn(String message, Object... args) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录错误级别日志
     * 
     * @param message 日志消息
     * @param args    消息参数
     */
    public static void error(String message, Object... args) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录错误级别日志（带异常）
     * 
     * @param message   日志消息
     * @param throwable 异常对象
     */
    public static void error(String message, Throwable throwable) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(ModConstants.LOG_PREFIX + message, throwable);
        }
    }

    // ========== 带标记的日志方法 ==========

    /**
     * 记录带标记的调试级别日志
     * 
     * @param category 日志分类
     * @param message  日志消息
     * @param args     消息参数
     */
    public static void debug(LogCategory category, String message, Object... args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(category.getMarker(), ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录带标记的信息级别日志
     * 
     * @param category 日志分类
     * @param message  日志消息
     * @param args     消息参数
     */
    public static void info(LogCategory category, String message, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(category.getMarker(), ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录带标记的警告级别日志
     * 
     * @param category 日志分类
     * @param message  日志消息
     * @param args     消息参数
     */
    public static void warn(LogCategory category, String message, Object... args) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(category.getMarker(), ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录带标记的错误级别日志
     * 
     * @param category 日志分类
     * @param message  日志消息
     * @param args     消息参数
     */
    public static void error(LogCategory category, String message, Object... args) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(category.getMarker(), ModConstants.LOG_PREFIX + message, args);
        }
    }

    /**
     * 记录带标记的错误级别日志（带异常）
     * 
     * @param category  日志分类
     * @param message   日志消息
     * @param throwable 异常对象
     */
    public static void error(LogCategory category, String message, Throwable throwable) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(category.getMarker(), ModConstants.LOG_PREFIX + message, throwable);
        }
    }

    // ========== 条件日志方法 ==========

    /**
     * 仅在调试模式下记录日志
     * 
     * @param message 日志消息
     * @param args    消息参数
     */
    public static void debugOnly(String message, Object... args) {
        if (ModConstants.DEBUG_MODE && LOGGER.isDebugEnabled()) {
            LOGGER.debug(ModConstants.LOG_PREFIX + "[DEBUG_MODE] " + message, args);
        }
    }

    /**
     * 仅在调试模式下记录带标记的日志
     * 
     * @param category 日志分类
     * @param message  日志消息
     * @param args     消息参数
     */
    public static void debugOnly(LogCategory category, String message, Object... args) {
        if (ModConstants.DEBUG_MODE && LOGGER.isDebugEnabled()) {
            LOGGER.debug(category.getMarker(), ModConstants.LOG_PREFIX + "[DEBUG_MODE] " + message, args);
        }
    }

    // ========== 性能监控相关 ==========

    /**
     * 记录性能监控日志
     * 
     * @param operation 操作名称
     * @param startTime 开始时间（纳秒）
     */
    public static void logPerformance(String operation, long startTime) {
        if (LOGGER.isDebugEnabled()) {
            long duration = System.nanoTime() - startTime;
            double milliseconds = duration / 1_000_000.0;
            LOGGER.debug(LogCategory.PERFORMANCE.getMarker(),
                    ModConstants.LOG_PREFIX + "Performance: {} took {:.2f}ms",
                    operation, milliseconds);
        }
    }

    /**
     * 获取当前时间戳（用于性能监控）
     * 
     * @return 当前时间戳（纳秒）
     */
    public static long startTimer() {
        return System.nanoTime();
    }
}
