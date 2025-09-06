package com.jasoncian.millenaire_rewrite.blocks.system;

/**
 * 基础建筑材料定义 - 定义所有基础建筑材料类型
 *
 * 统一管理所有基础建筑材料的名称和属性，
 * 用于自动生成完整的方块族系。
 *
 * 功能特性：
 * - 标准化的材料名称定义
 * - 材料属性映射
 * - 自动化注册支持
 * - 扩展友好的设计
 *
 * @author JasonCian
 * @version 1.0.0
 */
public enum BasicBuildingMaterial {
    
    // ================ 石质材料 ================
    
    /** 普通石材 */
    STONE(
        "stone", 
        "石头",
        BuildingMaterial.STONE
    ),
    
    /** 圆石 */
    COBBLESTONE(
        "cobblestone",
        "圆石", 
        BuildingMaterial.STONE
    ),
    
    /** 石砖 */
    STONE_BRICKS(
        "stone_bricks",
        "石砖",
        BuildingMaterial.STONE
    ),
    
    /** 苔石砖 */
    MOSSY_STONE_BRICKS(
        "mossy_stone_bricks",
        "苔石砖",
        BuildingMaterial.STONE
    ),
    
    /** 裂纹石砖 */
    CRACKED_STONE_BRICKS(
        "cracked_stone_bricks", 
        "裂纹石砖",
        BuildingMaterial.STONE
    ),
    
    /** 雕刻石砖 */
    CHISELED_STONE_BRICKS(
        "chiseled_stone_bricks",
        "雕刻石砖", 
        BuildingMaterial.CARVED_STONE
    ),
    
    // ================ 砂岩材料 ================
    
    /** 普通砂岩 */
    SANDSTONE(
        "sandstone",
        "砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    /** 雕刻砂岩 */
    CHISELED_SANDSTONE(
        "chiseled_sandstone",
        "雕刻砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    /** 切制砂岩 */
    CUT_SANDSTONE(
        "cut_sandstone", 
        "切制砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    /** 光滑砂岩 */
    SMOOTH_SANDSTONE(
        "smooth_sandstone",
        "光滑砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    /** 红砂岩 */
    RED_SANDSTONE(
        "red_sandstone",
        "红砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    /** 雕刻红砂岩 */
    CHISELED_RED_SANDSTONE(
        "chiseled_red_sandstone",
        "雕刻红砂岩", 
        BuildingMaterial.SANDSTONE
    ),
    
    /** 切制红砂岩 */
    CUT_RED_SANDSTONE(
        "cut_red_sandstone",
        "切制红砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    /** 光滑红砂岩 */
    SMOOTH_RED_SANDSTONE(
        "smooth_red_sandstone",
        "光滑红砂岩",
        BuildingMaterial.SANDSTONE
    ),
    
    // ================ 泥砖材料 ================
    
    /** 泥砖 */
    MUD_BRICKS(
        "mud_bricks",
        "泥砖",
        BuildingMaterial.MUD_BRICK
    ),
    
    /** 风干砖 */
    DRIED_BRICKS(
        "dried_bricks",
        "风干砖",
        BuildingMaterial.DRIED_BRICK
    ),
    
    /** 烧制砖 */
    COOKED_BRICKS(
        "cooked_bricks",
        "烧制砖",
        BuildingMaterial.STONE
    ),
    
    // ================ 装饰材料（从装饰方块系统迁移） ================
    
    /** 金装饰石块 - 高级装饰建筑材料 */
    GOLD_ORNAMENT(
        "gold_ornament",
        "金装饰", 
        BuildingMaterial.GOLD_ORNAMENT
    ),
    
    /** Galianite方块 - 特殊魔法建筑材料 */
    GALIANITE_BLOCK(
        "galianite_block",
        "Galianite方块",
        BuildingMaterial.GALIANITE
    ),
    
    /** 简朴木框架 - 基础木质装饰 */
    SIMPLE_TIMBER_FRAME(
        "simple_timber_frame", 
        "简朴木框架",
        BuildingMaterial.TIMBER_FRAME
    ),
    
    /** 十字木框架 - 增强木质装饰 */
    CROSS_TIMBER_FRAME(
        "cross_timber_frame",
        "十字木框架", 
        BuildingMaterial.TIMBER_FRAME
    ),
    
    /** 土墙 - 基础土质建筑 */
    EARTH_WALL(
        "earth_wall",
        "土墙",
        BuildingMaterial.MUD_BRICK
    ),
    
    // ================ 瓦片材料 ================
    
    /** 拜占庭瓦片 */
    BYZANTINE_TILES(
        "byzantine_tiles",
        "拜占庭瓦片",
        BuildingMaterial.TILES
    ),
    
    /** 灰瓦片 */
    GRAY_TILES(
        "gray_tiles",
        "灰瓦片",
        BuildingMaterial.TILES
    ),
    
    /** 绿瓦片 */
    GREEN_TILES(
        "green_tiles", 
        "绿瓦片",
        BuildingMaterial.TILES
    ),
    
    /** 红瓦片 */
    RED_TILES(
        "red_tiles",
        "红瓦片",
        BuildingMaterial.TILES
    ),
    
    /** 赭石瓦片 */
    OCHRE_TILES(
        "ochre_tiles",
        "赭石瓦片",
        BuildingMaterial.TILES
    ),
    
    // ================ 木质材料 ================
    
    /** 木框架 */
    TIMBER_FRAME(
        "timber_frame",
        "木框架",
        BuildingMaterial.TIMBER_FRAME
    ),
    
    /** 茅草 */
    THATCH(
        "thatch",
        "茅草",
        BuildingMaterial.THATCH
    ),
    
    // ================ 特殊材料 ================
    
    /** 冰砖 */
    ICE_BRICKS(
        "ice_bricks",
        "冰砖",
        BuildingMaterial.ICE_BRICK
    ),
    
    /** 雪砖 */
    SNOW_BRICKS(
        "snow_bricks",
        "雪砖", 
        BuildingMaterial.SNOW_BRICK
    );
    
    // ================ 属性字段 ================
    
    private final String registryName;
    private final String displayName;
    private final BuildingMaterial materialProperties;
    
    /**
     * 构造函数
     *
     * @param registryName 注册名
     * @param displayName 显示名称
     * @param materialProperties 材料属性
     */
    BasicBuildingMaterial(String registryName, String displayName, BuildingMaterial materialProperties) {
        this.registryName = registryName;
        this.displayName = displayName;
        this.materialProperties = materialProperties;
    }
    
    // ================ 访问器方法 ================
    
    /** 获取注册名 */
    public String getRegistryName() {
        return registryName;
    }
    
    /** 获取显示名称 */
    public String getDisplayName() {
        return displayName;
    }
    
    /** 获取材料属性 */
    public BuildingMaterial getMaterialProperties() {
        return materialProperties;
    }
    
    // ================ 实用方法 ================
    
    /**
     * 生成完整的方块注册名
     *
     * @param culture 文化类型
     * @param variant 变体类型
     * @return 完整的注册名
     */
    public String generateBlockRegistryName(CulturalBlockFamily culture, BlockVariantType variant) {
        return culture.generateRegistryName(variant.generateRegistryName(this.registryName), "");
    }
    
    /**
     * 生成本地化键名
     *
     * @param culture 文化类型
     * @param variant 变体类型
     * @return 本地化键名
     */
    public String generateTranslationKey(CulturalBlockFamily culture, BlockVariantType variant) {
        return "block.millenaire_rewrite." + generateBlockRegistryName(culture, variant);
    }
    
    /**
     * 获取所有石质材料
     *
     * @return 石质材料数组
     */
    public static BasicBuildingMaterial[] getStoneMaterials() {
        return new BasicBuildingMaterial[] {
            STONE, COBBLESTONE, STONE_BRICKS, MOSSY_STONE_BRICKS, 
            CRACKED_STONE_BRICKS, CHISELED_STONE_BRICKS
        };
    }
    
    /**
     * 获取所有砂岩材料
     *
     * @return 砂岩材料数组
     */
    public static BasicBuildingMaterial[] getSandstoneMaterials() {
        return new BasicBuildingMaterial[] {
            SANDSTONE, CHISELED_SANDSTONE, CUT_SANDSTONE, SMOOTH_SANDSTONE,
            RED_SANDSTONE, CHISELED_RED_SANDSTONE, CUT_RED_SANDSTONE, SMOOTH_RED_SANDSTONE
        };
    }
    
    /**
     * 获取所有瓦片材料
     *
     * @return 瓦片材料数组
     */
    public static BasicBuildingMaterial[] getTileMaterials() {
        return new BasicBuildingMaterial[] {
            BYZANTINE_TILES, GRAY_TILES, GREEN_TILES, RED_TILES, OCHRE_TILES
        };
    }
    
    /**
     * 获取所有装饰材料（从装饰方块系统迁移）
     *
     * @return 装饰材料数组
     */
    public static BasicBuildingMaterial[] getDecorativeMaterials() {
        return new BasicBuildingMaterial[] {
            GOLD_ORNAMENT, GALIANITE_BLOCK, SIMPLE_TIMBER_FRAME, 
            CROSS_TIMBER_FRAME, EARTH_WALL
        };
    }
    
    /**
     * 获取石质装饰材料
     *
     * @return 石质装饰材料数组
     */
    public static BasicBuildingMaterial[] getStoneDecorativeMaterials() {
        return new BasicBuildingMaterial[] {
            GOLD_ORNAMENT, GALIANITE_BLOCK, COOKED_BRICKS
        };
    }
    
    /**
     * 获取木质装饰材料
     *
     * @return 木质装饰材料数组
     */
    public static BasicBuildingMaterial[] getWoodDecorativeMaterials() {
        return new BasicBuildingMaterial[] {
            SIMPLE_TIMBER_FRAME, CROSS_TIMBER_FRAME, THATCH
        };
    }
    
    /**
     * 获取土质装饰材料
     *
     * @return 土质装饰材料数组
     */
    public static BasicBuildingMaterial[] getEarthDecorativeMaterials() {
        return new BasicBuildingMaterial[] {
            EARTH_WALL, MUD_BRICKS, DRIED_BRICKS
        };
    }
}
