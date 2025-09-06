package com.jasoncian.millenaire_rewrite.blocks.system;

/**
 * 方块变体类型枚举 - 定义所有基础建筑方块的变体类型
 *
 * 统一管理方块的不同形状变体，包括普通方块、楼梯、台阶、墙等。
 * 用于自动生成完整的方块族系，提高开发效率。
 *
 * 功能特性：
 * - 标准化的变体类型定义
 * - 自动化注册名生成
 * - 统一的属性继承
 * - 扩展友好的设计
 *
 * @author JasonCian
 * @version 1.0.0
 */
public enum BlockVariantType {
    
    // ================ 基础变体 ================
    
    /** 普通方块 - 完整的1x1x1方块 */
    BLOCK(
        "",           // 无后缀
        "方块",
        true,         // 是基础方块
        false         // 不需要基础方块引用
    ),
    
    // ================ 形状变体 ================
    
    /** 楼梯方块 - 阶梯形状 */
    STAIRS(
        "_stairs",
        "楼梯",
        false,
        true
    ),
    
    /** 台阶方块 - 半高方块 */
    SLAB(
        "_slab",
        "台阶", 
        false,
        true
    ),
    
    /** 墙方块 - 连接型围墙 */
    WALL(
        "_wall",
        "墙",
        false,
        true
    ),
    
    /** 栅栏方块 - 连接型栅栏 */
    FENCE(
        "_fence",
        "栅栏",
        false,
        true
    ),
    
    /** 栅栏门方块 - 可开关的栅栏门 */
    FENCE_GATE(
        "_fence_gate",
        "栅栏门",
        false,
        true
    ),
    
    // ================ 装饰变体 ================
    
    /** 柱子方块 - 可旋转的柱状方块 */
    PILLAR(
        "_pillar",
        "柱子",
        false,
        true
    ),
    
    /** 雕刻变体 - 装饰性雕刻方块 */
    CARVED(
        "_carved",
        "雕刻",
        false,
        true
    ),
    
    /** 抛光变体 - 光滑表面方块 */
    POLISHED(
        "_polished",
        "抛光",
        false,
        true
    ),
    
    /** 砖块变体 - 砖块纹理方块 */
    BRICKS(
        "_bricks",
        "砖块",
        false,
        true
    ),
    
    // ================ 特殊变体 ================
    
    /** 压力板 - 踩踏激活的压力板 */
    PRESSURE_PLATE(
        "_pressure_plate",
        "压力板",
        false,
        true
    ),
    
    /** 按钮 - 点击激活的按钮 */
    BUTTON(
        "_button", 
        "按钮",
        false,
        true
    );
    
    // ================ 属性字段 ================
    
    private final String registrySuffix;
    private final String displayName;
    private final boolean isBaseBlock;
    private final boolean requiresBaseBlock;
    
    /**
     * 构造函数
     *
     * @param registrySuffix 注册名后缀
     * @param displayName 显示名称
     * @param isBaseBlock 是否为基础方块
     * @param requiresBaseBlock 是否需要基础方块引用
     */
    BlockVariantType(String registrySuffix, String displayName, boolean isBaseBlock, boolean requiresBaseBlock) {
        this.registrySuffix = registrySuffix;
        this.displayName = displayName;
        this.isBaseBlock = isBaseBlock;
        this.requiresBaseBlock = requiresBaseBlock;
    }
    
    // ================ 访问器方法 ================
    
    /** 获取注册名后缀 */
    public String getRegistrySuffix() {
        return registrySuffix;
    }
    
    /** 获取显示名称 */
    public String getDisplayName() {
        return displayName;
    }
    
    /** 是否为基础方块 */
    public boolean isBaseBlock() {
        return isBaseBlock;
    }
    
    /** 是否需要基础方块引用 */
    public boolean requiresBaseBlock() {
        return requiresBaseBlock;
    }
    
    // ================ 实用方法 ================
    
    /**
     * 生成完整的注册名
     *
     * @param baseName 基础名称
     * @return 完整的注册名
     */
    public String generateRegistryName(String baseName) {
        return baseName + registrySuffix;
    }
    
    /**
     * 生成本地化键名
     *
     * @param baseName 基础名称
     * @return 本地化键名
     */
    public String generateTranslationKey(String baseName) {
        return "block.millenaire_rewrite." + generateRegistryName(baseName);
    }
    
    /**
     * 获取所有标准变体类型（用于自动生成）
     * 包括：方块、楼梯、台阶、墙
     *
     * @return 标准变体类型数组
     */
    public static BlockVariantType[] getStandardVariants() {
        return new BlockVariantType[] {
            BLOCK, STAIRS, SLAB, WALL
        };
    }
    
    /**
     * 获取所有装饰变体类型
     * 包括：柱子、雕刻、抛光、砖块
     *
     * @return 装饰变体类型数组
     */
    public static BlockVariantType[] getDecorativeVariants() {
        return new BlockVariantType[] {
            PILLAR, CARVED, POLISHED, BRICKS
        };
    }
    
    /**
     * 获取所有交互变体类型
     * 包括：压力板、按钮
     *
     * @return 交互变体类型数组
     */
    public static BlockVariantType[] getInteractiveVariants() {
        return new BlockVariantType[] {
            PRESSURE_PLATE, BUTTON
        };
    }
}
