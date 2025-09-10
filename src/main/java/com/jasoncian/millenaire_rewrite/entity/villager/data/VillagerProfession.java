package com.jasoncian.millenaire_rewrite.entity.villager.data;

import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import java.util.Arrays;
import java.util.List;

/**
 * 村民职业枚举
 * 
 * 定义千年村民的各种职业类型，每种职业具有：
 * - 独特的工作行为和技能
 * - 文化相关的适应性
 * - 特定的装备和工具需求
 * - 经济价值和贸易能力
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public enum VillagerProfession {
    
    // ====================== 农业相关职业 ======================
    
    /**
     * 农民 - 基础农业工作者
     * 
     * 工作内容：
     * - 种植和收获农作物
     * - 饲养家畜
     * - 生产基础食物
     * 
     * 文化适应性：所有文化
     */
    FARMER("farmer", "农民", ProfessionCategory.AGRICULTURE, 
           Arrays.asList(CultureType.values())),
    
    /**
     * 牧民 - 专业畜牧工作者
     * 
     * 工作内容：
     * - 饲养和繁殖牲畜
     * - 生产肉类、奶制品、皮革
     * - 管理牧场设施
     * 
     * 文化适应性：所有文化
     */
    HERDER("herder", "牧民", ProfessionCategory.AGRICULTURE,
           Arrays.asList(CultureType.values())),

    // ====================== 手工业职业 ======================
    
    /**
     * 铁匠 - 金属加工专家
     * 
     * 工作内容：
     * - 制作工具和武器
     * - 修理金属物品
     * - 生产农具和工艺品
     * 
     * 文化适应性：所有文化
     */
    BLACKSMITH("blacksmith", "铁匠", ProfessionCategory.CRAFTING,
               Arrays.asList(CultureType.values())),
    
    /**
     * 木匠 - 木材加工专家
     * 
     * 工作内容：
     * - 制作家具和建筑构件
     * - 生产木制工具
     * - 建造和维修建筑
     * 
     * 文化适应性：所有文化
     */
    CARPENTER("carpenter", "木匠", ProfessionCategory.CRAFTING,
              Arrays.asList(CultureType.values())),
    
    /**
     * 裁缝 - 纺织品制作者
     * 
     * 工作内容：
     * - 制作服装和布料
     * - 生产旗帜和装饰品
     * - 修补衣物
     * 
     * 文化适应性：所有文化
     */
    TAILOR("tailor", "裁缝", ProfessionCategory.CRAFTING,
           Arrays.asList(CultureType.values())),

    // ====================== 商业职业 ======================
    
    /**
     * 商人 - 贸易专家
     * 
     * 工作内容：
     * - 买卖商品
     * - 管理库存
     * - 组织贸易路线
     * 
     * 文化适应性：所有文化
     */
    MERCHANT("merchant", "商人", ProfessionCategory.TRADE,
             Arrays.asList(CultureType.values())),
    
    /**
     * 店主 - 零售经营者
     * 
     * 工作内容：
     * - 经营商店
     * - 服务顾客
     * - 管理日常销售
     * 
     * 文化适应性：所有文化
     */
    SHOPKEEPER("shopkeeper", "店主", ProfessionCategory.TRADE,
               Arrays.asList(CultureType.values())),

    // ====================== 军事职业 ======================
    
    /**
     * 守卫 - 村庄防卫者
     * 
     * 工作内容：
     * - 巡逻和警戒
     * - 保护村民
     * - 维护秩序
     * 
     * 文化适应性：所有文化
     */
    GUARD("guard", "守卫", ProfessionCategory.MILITARY,
          Arrays.asList(CultureType.values())),
    
    /**
     * 弓箭手 - 远程战斗专家
     * 
     * 工作内容：
     * - 远程防御
     * - 狩猎活动
     * - 箭矢制作
     * 
     * 文化适应性：所有文化（华夏文化特别擅长弩）
     */
    ARCHER("archer", "弓箭手", ProfessionCategory.MILITARY,
           Arrays.asList(CultureType.values())),

    // ====================== 华夏文化特色职业 ======================
    
    /**
     * 学者 - 华夏文化知识分子
     * 
     * 工作内容：
     * - 教育和培训
     * - 文献管理
     * - 文化传承
     * 
     * 文化适应性：主要为华夏文化
     */
    SCHOLAR("scholar", "学者", ProfessionCategory.CULTURAL,
            Arrays.asList(CultureType.HUAXIA)),
    
    /**
     * 茶商 - 华夏茶叶贸易专家
     * 
     * 工作内容：
     * - 茶叶种植和加工
     * - 茶艺表演
     * - 茶叶贸易
     * 
     * 文化适应性：主要为华夏文化
     */
    TEA_MERCHANT("tea_merchant", "茶商", ProfessionCategory.CULTURAL,
                 Arrays.asList(CultureType.HUAXIA)),
    
    /**
     * 瓷匠 - 华夏陶瓷工艺师
     * 
     * 工作内容：
     * - 制作瓷器
     * - 陶艺创作
     * - 工艺品贸易
     * 
     * 文化适应性：主要为华夏文化
     */
    POTTER("potter", "瓷匠", ProfessionCategory.CULTURAL,
           Arrays.asList(CultureType.HUAXIA)),

    // ====================== 服务职业 ======================
    
    /**
     * 厨师 - 食物制作专家
     * 
     * 工作内容：
     * - 烹饪美食
     * - 经营餐厅
     * - 食物保存
     * 
     * 文化适应性：所有文化
     */
    COOK("cook", "厨师", ProfessionCategory.SERVICE,
         Arrays.asList(CultureType.values())),
    
    /**
     * 治疗师 - 医疗服务提供者
     * 
     * 工作内容：
     * - 治疗伤病
     * - 制作药物
     * - 健康保健
     * 
     * 文化适应性：所有文化
     */
    HEALER("healer", "治疗师", ProfessionCategory.SERVICE,
           Arrays.asList(CultureType.values()));

    // ====================== 字段 ======================
    
    /**
     * 职业标识符
     */
    private final String id;
    
    /**
     * 职业显示名称
     */
    private final String displayName;
    
    /**
     * 职业分类
     */
    private final ProfessionCategory category;
    
    /**
     * 适用的文化类型列表
     */
    private final List<CultureType> compatibleCultures;

    // ====================== 构造函数 ======================
    
    /**
     * 构造村民职业枚举
     * 
     * @param id 职业标识符
     * @param displayName 显示名称
     * @param category 职业分类
     * @param compatibleCultures 兼容的文化类型
     */
    VillagerProfession(String id, String displayName, ProfessionCategory category,
                      List<CultureType> compatibleCultures) {
        this.id = id;
        this.displayName = displayName;
        this.category = category;
        this.compatibleCultures = compatibleCultures;
    }

    // ====================== Getter方法 ======================
    
    /**
     * 获取职业标识符
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * 获取职业显示名称
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * 获取职业分类
     */
    public ProfessionCategory getCategory() {
        return this.category;
    }
    
    /**
     * 获取兼容的文化类型列表
     */
    public List<CultureType> getCompatibleCultures() {
        return this.compatibleCultures;
    }

    // ====================== 工具方法 ======================
    
    /**
     * 检查职业是否与指定文化兼容
     * 
     * @param culture 文化类型
     * @return 是否兼容
     */
    public boolean isCompatibleWith(CultureType culture) {
        return this.compatibleCultures.contains(culture);
    }
    
    /**
     * 根据ID获取职业枚举
     * 
     * @param id 职业ID
     * @return 对应的职业枚举，如果未找到则返回FARMER
     */
    public static VillagerProfession fromId(String id) {
        for (VillagerProfession profession : values()) {
            if (profession.getId().equals(id)) {
                return profession;
            }
        }
        return FARMER;  // 默认返回农民
    }
    
    /**
     * 获取与指定文化兼容的所有职业
     * 
     * @param culture 文化类型
     * @return 兼容的职业数组
     */
    public static VillagerProfession[] getCompatibleProfessions(CultureType culture) {
        return Arrays.stream(values())
                .filter(profession -> profession.isCompatibleWith(culture))
                .toArray(VillagerProfession[]::new);
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    // ====================== 内部枚举 ======================
    
    /**
     * 职业分类枚举
     */
    public enum ProfessionCategory {
        AGRICULTURE("农业", "从事农业生产的职业"),
        CRAFTING("手工业", "制作物品和工艺品的职业"),
        TRADE("商业", "从事贸易和商业活动的职业"),
        MILITARY("军事", "负责防卫和军事的职业"),
        SERVICE("服务业", "提供各种服务的职业"),
        CULTURAL("文化特色", "具有文化特色的职业");
        
        private final String displayName;
        private final String description;
        
        ProfessionCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return this.displayName;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        @Override
        public String toString() {
            return this.displayName;
        }
    }
}
