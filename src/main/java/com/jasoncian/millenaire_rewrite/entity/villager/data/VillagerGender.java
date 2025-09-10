package com.jasoncian.millenaire_rewrite.entity.villager.data;

/**
 * 村民性别枚举
 * 
 * 定义千年村民的性别类型，影响：
 * - 外观纹理和模型变体
 * - 某些职业的适应性
 * - 姓名生成规则
 * - 社交互动行为
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public enum VillagerGender {
    
    /**
     * 男性村民
     * 
     * 通常具有：
     * - 更粗犷的外观
     * - 适合重体力劳动的职业
     * - 男性化的姓名
     */
    MALE("male", "男性"),
    
    /**
     * 女性村民
     * 
     * 通常具有：
     * - 更精致的外观
     * - 适合精细工作的职业
     * - 女性化的姓名
     */
    FEMALE("female", "女性");

    // ====================== 字段 ======================
    
    /**
     * 性别标识符（用于资源文件命名等）
     */
    private final String id;
    
    /**
     * 性别显示名称（中文）
     */
    private final String displayName;

    // ====================== 构造函数 ======================
    
    /**
     * 构造村民性别枚举
     * 
     * @param id 性别标识符
     * @param displayName 显示名称
     */
    VillagerGender(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    // ====================== Getter方法 ======================
    
    /**
     * 获取性别标识符
     * 
     * @return 性别ID
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * 获取性别显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return this.displayName;
    }

    // ====================== 工具方法 ======================
    
    /**
     * 根据ID获取性别枚举
     * 
     * @param id 性别ID
     * @return 对应的性别枚举，如果未找到则返回MALE
     */
    public static VillagerGender fromId(String id) {
        for (VillagerGender gender : values()) {
            if (gender.getId().equals(id)) {
                return gender;
            }
        }
        return MALE;  // 默认返回男性
    }
    
    /**
     * 获取随机性别
     * 
     * @return 随机的性别枚举
     */
    public static VillagerGender random() {
        VillagerGender[] genders = values();
        return genders[(int) (Math.random() * genders.length)];
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
