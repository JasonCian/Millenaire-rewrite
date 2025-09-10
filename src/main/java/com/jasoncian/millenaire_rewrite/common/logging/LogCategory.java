package com.jasoncian.millenaire_rewrite.common.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * 日志分类枚举
 * 
 * 定义了千年村庄模组中不同功能模块的日志分类。
 * 通过标记（Marker）机制，可以方便地过滤和管理不同类型的日志。
 * 
 * <p><strong>分类说明：</strong>
 * <ul>
 *   <li><strong>CORE</strong> - 核心系统日志</li>
 *   <li><strong>CULTURE</strong> - 文化系统日志</li>
 *   <li><strong>VILLAGE</strong> - 村庄系统日志</li>
 *   <li><strong>ECONOMY</strong> - 经济系统日志</li>
 *   <li><strong>AI</strong> - AI系统日志</li>
 *   <li><strong>NETWORK</strong> - 网络通信日志</li>
 *   <li><strong>CONFIG</strong> - 配置系统日志</li>
 *   <li><strong>PERFORMANCE</strong> - 性能监控日志</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public enum LogCategory {
    
    /** 核心系统 - 模组初始化、注册系统等 */
    CORE("CORE", "核心系统"),
    
    /** 文化系统 - 文化注册、管理等 */
    CULTURE("CULTURE", "文化系统"),
    
    /** 村庄系统 - 村庄创建、管理、更新等 */
    VILLAGE("VILLAGE", "村庄系统"),
    
    /** 经济系统 - 贸易、货币、价格等 */
    ECONOMY("ECONOMY", "经济系统"),
    
    /** AI系统 - 村民AI、行为逻辑等 */
    AI("AI", "AI系统"),
    
    /** 实体系统 - 实体创建、管理、行为等 */
    ENTITY("ENTITY", "实体系统"),
    
    /** 建筑系统 - 建筑生成、规划等 */
    BUILDING("BUILDING", "建筑系统"),
    
    /** 网络通信 - 数据包发送、接收等 */
    NETWORK("NETWORK", "网络通信"),
    
    /** 配置系统 - 配置加载、保存等 */
    CONFIG("CONFIG", "配置系统"),
    
    /** 数据生成 - DataGen相关操作 */
    DATAGEN("DATAGEN", "数据生成"),
    
    /** 客户端 - 渲染、GUI、事件等 */
    CLIENT("CLIENT", "客户端"),
    
    /** 服务端 - 服务端专用逻辑 */
    SERVER("SERVER", "服务端"),
    
    /** 性能监控 - 性能分析、优化等 */
    PERFORMANCE("PERFORMANCE", "性能监控"),
    
    /** 调试 - 开发调试信息 */
    DEBUG("DEBUG", "调试"),
    
    /** 兼容性 - 第三方模组集成 */
    INTEGRATION("INTEGRATION", "兼容性");
    
    /** 标记名称 */
    private final String markerName;
    
    /** 分类显示名称 */
    private final String displayName;
    
    /** SLF4J标记对象 */
    private final Marker marker;
    
    /**
     * 构造函数
     * 
     * @param markerName 标记名称
     * @param displayName 显示名称
     */
    LogCategory(String markerName, String displayName) {
        this.markerName = markerName;
        this.displayName = displayName;
        this.marker = MarkerFactory.getMarker("MILLENAIRE_" + markerName);
    }
    
    /**
     * 获取标记名称
     * 
     * @return 标记名称
     */
    public String getMarkerName() {
        return markerName;
    }
    
    /**
     * 获取显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取SLF4J标记对象
     * 
     * @return Marker对象
     */
    public Marker getMarker() {
        return marker;
    }
    
    /**
     * 根据标记名称获取对应的日志分类
     * 
     * @param markerName 标记名称
     * @return 对应的日志分类，如果不存在则返回CORE
     */
    public static LogCategory fromMarkerName(String markerName) {
        for (LogCategory category : values()) {
            if (category.getMarkerName().equals(markerName)) {
                return category;
            }
        }
        return CORE; // 默认返回核心分类
    }
}
