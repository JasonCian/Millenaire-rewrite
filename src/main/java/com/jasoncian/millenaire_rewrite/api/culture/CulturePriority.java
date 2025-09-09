package com.jasoncian.millenaire_rewrite.api.culture;

import org.jetbrains.annotations.ApiStatus;

/**
 * 文化发展优先级枚举
 * 
 * 定义了不同文化的发展重点和特色。每种文化都有其独特的
 * 发展倾向，这会影响村庄的建设优先级、资源分配和社会结构。
 * 
 * <p><strong>优先级说明：</strong>
 * <ul>
 *   <li><strong>MILITARY（军事）</strong> - 重视防御建设和武器装备</li>
 *   <li><strong>COMMERCIAL（商业）</strong> - 专注贸易发展和经济增长</li>
 *   <li><strong>AGRICULTURAL（农业）</strong> - 重视农业生产和食物储备</li>
 *   <li><strong>CULTURAL（文化）</strong> - 注重教育、艺术和精神建设</li>
 *   <li><strong>TECHNOLOGICAL（技术）</strong> - 追求工艺进步和技术创新</li>
 *   <li><strong>BALANCED（平衡）</strong> - 各方面均衡发展</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public enum CulturePriority {
    
    /** 军事优先 - 重视防御和战争准备 */
    MILITARY("military", "军事优先", "专注于军事防御和战争准备"),
    
    /** 商业优先 - 重视贸易和经济发展 */
    COMMERCIAL("commercial", "商业优先", "专注于贸易发展和经济增长"),
    
    /** 农业优先 - 重视农业生产和食物安全 */
    AGRICULTURAL("agricultural", "农业优先", "专注于农业生产和食物储备"),
    
    /** 文化优先 - 重视教育、艺术和精神文明 */
    CULTURAL("cultural", "文化优先", "专注于教育、艺术和精神建设"),
    
    /** 技术优先 - 重视工艺技术和创新 */
    TECHNOLOGICAL("technological", "技术优先", "专注于工艺进步和技术创新"),
    
    /** 平衡发展 - 各方面均衡发展 */
    BALANCED("balanced", "平衡发展", "各方面均衡发展，无明显偏重");
    
    /** 优先级的内部标识符 */
    private final String id;
    
    /** 优先级的显示名称 */
    private final String displayName;
    
    /** 优先级的描述信息 */
    private final String description;
    
    /**
     * 构造函数
     * 
     * @param id 内部标识符
     * @param displayName 显示名称
     * @param description 描述信息
     */
    CulturePriority(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 获取优先级的内部标识符
     * 
     * @return 标识符字符串
     */
    public String getId() {
        return id;
    }
    
    /**
     * 获取优先级的显示名称
     * 
     * @return 本地化的显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取优先级的描述信息
     * 
     * @return 本地化的描述信息
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据ID字符串获取对应的优先级枚举值
     * 
     * @param id 要查找的ID
     * @return 对应的枚举值，如果不存在则返回BALANCED
     */
    public static CulturePriority fromId(String id) {
        for (CulturePriority priority : values()) {
            if (priority.getId().equals(id)) {
                return priority;
            }
        }
        return BALANCED; // 默认返回平衡发展
    }
}
