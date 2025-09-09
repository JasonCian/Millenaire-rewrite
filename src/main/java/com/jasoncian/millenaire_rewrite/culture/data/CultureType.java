package com.jasoncian.millenaire_rewrite.culture.data;

import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.util.StringRepresentable;

/**
 * 文化类型枚举
 * 
 * 定义模组中支持的所有文化类型。这个枚举设计为可扩展的，支持添加新的文化类型
 * 包括原版的欧洲文化（Norman、Byzantine、Japanese）和新增的华夏文化。
 * 
 * 每个文化类型都包含：
 * - 唯一的标识符名称（用于注册和保存）
 * - 显示名称键（用于本地化）
 * - 文化数据文件路径
 * - 文化特色配置参数
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public enum CultureType implements StringRepresentable {
    
    /**
     * 诺曼文化 - 原版经典文化
     * 特点：中世纪欧洲风格，以城堡和骑士文化为主
     */
    NORMAN("norman", "culture.millenaire_rewrite.norman", 
           "cultures/norman", 0x8B4513),
    
    /**
     * 拜占庭文化 - 原版文化
     * 特点：东罗马帝国风格，富丽堂皇的建筑和贸易文化
     */
    BYZANTINE("byzantine", "culture.millenaire_rewrite.byzantine", 
              "cultures/byzantine", 0x800080),
    
    /**
     * 日本文化 - 原版文化
     * 特点：传统日式建筑风格，武士和禅宗文化
     */
    JAPANESE("japanese", "culture.millenaire_rewrite.japanese", 
             "cultures/japanese", 0xFF6347),
    
    /**
     * 华夏文化 - 新增文化
     * 特点：中国传统文化，汉唐建筑风格，儒家文化体系
     */
    HUAXIA("huaxia", "culture.millenaire_rewrite.huaxia", 
           "cultures/huaxia", 0xFFD700),
    
    /**
     * 印度文化 - 未来扩展文化
     * 特点：南亚次大陆文化，佛教和印度教建筑风格
     */
    INDIAN("indian", "culture.millenaire_rewrite.indian", 
           "cultures/indian", 0xFF8C00),
    
    /**
     * 玛雅文化 - 未来扩展文化
     * 特点：中美洲古文明，金字塔和象形文字文化
     */
    MAYAN("mayan", "culture.millenaire_rewrite.mayan", 
          "cultures/mayan", 0x228B22);
    
    // ===== 枚举属性 =====
    
    /** 文化的唯一标识符，用于注册和数据存储 */
    private final String name;
    
    /** 本地化键名，用于显示文化名称 */
    private final String translationKey;
    
    /** 文化数据文件的基础路径 */
    private final String dataPath;
    
    /** 文化的代表颜色，用于UI显示和地图标记 */
    private final int primaryColor;
    
    // ===== 构造函数 =====
    
    /**
     * 创建文化类型枚举值
     * 
     * @param name 文化标识符名称
     * @param translationKey 本地化键名
     * @param dataPath 文化数据路径
     * @param primaryColor 代表颜色
     */
    CultureType(String name, String translationKey, String dataPath, int primaryColor) {
        this.name = name;
        this.translationKey = translationKey;
        this.dataPath = dataPath;
        this.primaryColor = primaryColor;
    }
    
    // ===== 公共方法 =====
    
    /**
     * 获取文化的字符串标识符
     * 实现StringRepresentable接口，用于Minecraft的序列化系统
     * 
     * @return 文化标识符字符串
     */
    @Override
    public String getSerializedName() {
        return name;
    }
    
    /**
     * 获取文化名称（用于内部标识）
     * 
     * @return 文化标识符字符串
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取本地化键名
     * 用于客户端显示文化名称
     * 
     * @return 本地化键字符串
     */
    public String getTranslationKey() {
        return translationKey;
    }
    
    /**
     * 获取文化数据文件路径
     * 用于加载文化相关的配置和数据文件
     * 
     * @return 数据文件路径
     */
    public String getDataPath() {
        return dataPath;
    }
    
    /**
     * 获取文化代表颜色
     * 用于UI显示、地图标记等视觉元素
     * 
     * @return 颜色值（RGB格式）
     */
    public int getPrimaryColor() {
        return primaryColor;
    }
    
    /**
     * 获取文化的完整资源位置路径
     * 结合模组ID和文化名称生成完整的资源标识符
     * 
     * @return 格式为 "modid:culture_name" 的资源位置
     */
    public String getResourceLocation() {
        return ModConstants.MOD_ID + ":" + name;
    }
    
    /**
     * 检查是否为原版文化
     * 原版文化指的是从1.12.2版本迁移过来的文化类型
     * 
     * @return 如果是原版文化返回true，否则返回false
     */
    public boolean isLegacyCulture() {
        return this == NORMAN || this == BYZANTINE || this == JAPANESE;
    }
    
    /**
     * 检查是否为新增文化
     * 新增文化指的是在重写版本中新加入的文化类型
     * 
     * @return 如果是新增文化返回true，否则返回false
     */
    public boolean isNewCulture() {
        return !isLegacyCulture();
    }
    
    /**
     * 根据字符串名称获取文化类型
     * 如果找不到对应的文化类型，返回null
     * 
     * @param name 文化名称字符串
     * @return 对应的文化类型，或null如果找不到
     */
    public static CultureType fromString(String name) {
        for (CultureType culture : values()) {
            if (culture.getName().equals(name)) {
                return culture;
            }
        }
        return null;
    }
    
    /**
     * 获取所有原版文化类型
     * 
     * @return 原版文化类型数组
     */
    public static CultureType[] getLegacyCultures() {
        return new CultureType[]{NORMAN, BYZANTINE, JAPANESE};
    }
    
    /**
     * 获取所有新增文化类型
     * 
     * @return 新增文化类型数组
     */
    public static CultureType[] getNewCultures() {
        return new CultureType[]{HUAXIA, INDIAN, MAYAN};
    }
}
