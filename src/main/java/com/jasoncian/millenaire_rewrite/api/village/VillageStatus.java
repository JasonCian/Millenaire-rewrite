package com.jasoncian.millenaire_rewrite.api.village;

import org.jetbrains.annotations.ApiStatus;

/**
 * 村庄状态枚举
 * 
 * 定义了村庄在不同生命周期阶段的状态。每种状态代表
 * 村庄的不同发展阶段或特殊情况，影响村庄的行为和功能。
 * 
 * <p><strong>状态说明：</strong>
 * <ul>
 *   <li><strong>INITIALIZING（初始化中）</strong> - 村庄刚创建，正在进行初始设置</li>
 *   <li><strong>GROWING（发展中）</strong> - 村庄正常发展，人口和建筑持续增长</li>
 *   <li><strong>STABLE（稳定）</strong> - 村庄发展达到平衡状态</li>
 *   <li><strong>DECLINING（衰落中）</strong> - 村庄面临困难，人口或资源下降</li>
 *   <li><strong>ABANDONED（废弃）</strong> - 村庄已被遗弃，停止所有活动</li>
 *   <li><strong>UNDER_ATTACK（受攻击）</strong> - 村庄正在遭受攻击或威胁</li>
 *   <li><strong>CELEBRATING（庆祝中）</strong> - 村庄正在举行节日或庆典</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public enum VillageStatus {
    
    /** 初始化中 - 村庄刚创建，正在设置基础设施 */
    INITIALIZING("initializing", "初始化中", "村庄刚创建，正在进行初始设置", true),
    
    /** 发展中 - 村庄正常发展阶段 */
    GROWING("growing", "发展中", "村庄正常发展，人口和建筑持续增长", true),
    
    /** 稳定 - 村庄发展达到平衡状态 */
    STABLE("stable", "稳定", "村庄发展达到平衡状态，维持正常运作", true),
    
    /** 衰落中 - 村庄面临困难 */
    DECLINING("declining", "衰落中", "村庄面临困难，人口或资源逐渐下降", true),
    
    /** 废弃 - 村庄已被遗弃 */
    ABANDONED("abandoned", "废弃", "村庄已被遗弃，停止所有发展活动", false),
    
    /** 受攻击 - 村庄正在遭受威胁 */
    UNDER_ATTACK("under_attack", "受攻击", "村庄正在遭受攻击或外部威胁", true),
    
    /** 庆祝中 - 村庄正在举行庆典 */
    CELEBRATING("celebrating", "庆祝中", "村庄正在举行节日庆典或特殊活动", true);
    
    /** 状态的内部标识符 */
    private final String id;
    
    /** 状态的显示名称 */
    private final String displayName;
    
    /** 状态的描述信息 */
    private final String description;
    
    /** 该状态下村庄是否处于活跃状态 */
    private final boolean active;
    
    /**
     * 构造函数
     * 
     * @param id 内部标识符
     * @param displayName 显示名称
     * @param description 描述信息
     * @param active 是否为活跃状态
     */
    VillageStatus(String id, String displayName, String description, boolean active) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.active = active;
    }
    
    /**
     * 获取状态的内部标识符
     * 
     * @return 标识符字符串
     */
    public String getId() {
        return id;
    }
    
    /**
     * 获取状态的显示名称
     * 
     * @return 本地化的显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取状态的描述信息
     * 
     * @return 本地化的描述信息
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查该状态下村庄是否处于活跃状态
     * 
     * @return 如果村庄活跃则返回true，否则返回false
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * 根据ID字符串获取对应的状态枚举值
     * 
     * @param id 要查找的ID
     * @return 对应的枚举值，如果不存在则返回STABLE
     */
    public static VillageStatus fromId(String id) {
        for (VillageStatus status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return STABLE; // 默认返回稳定状态
    }
}
