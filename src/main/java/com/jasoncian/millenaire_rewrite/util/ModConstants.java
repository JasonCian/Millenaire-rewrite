package com.jasoncian.millenaire_rewrite.util;

import net.minecraft.resources.ResourceLocation;

/**
 * 模组常量定义类
 * 
 * 集中定义模组中使用的所有常量，包括模组ID、版本信息、
 * 配置键值、默认值等，便于维护和修改。
 * 
 * <p><strong>常量分类：</strong>
 * <ul>
 *   <li>基础模组信息 - MOD_ID、版本等</li>
 *   <li>网络通信相关 - 协议版本、通道名称</li>
 *   <li>文化系统常量 - 默认文化、文化数量限制</li>
 *   <li>村庄系统常量 - 人口限制、发展等级</li>
 *   <li>经济系统常量 - 默认价格、汇率</li>
 *   <li>配置文件路径 - 各种配置目录</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class ModConstants {

    // 防止实例化工具类
    private ModConstants() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    // ========== 基础模组信息 ==========

    /** 模组唯一标识符 */
    public static final String MOD_ID = "millenaire_rewrite";

    /** 模组显示名称 */
    public static final String MOD_NAME = "Millenaire Rewrite";

    /** 模组版本 */
    public static final String MOD_VERSION = "0.1.4-alpha";

    /** 模组作者 */
    public static final String MOD_AUTHOR = "JasonCian";

    /** 模组描述 */
    public static final String MOD_DESCRIPTION = "千年村庄重写版 - 重现经典村庄模拟体验";

    /** 模组资源域名 */
    public static final String MOD_NAMESPACE = MOD_ID;

    // ========== 网络通信相关 ==========

    /** 网络协议版本 */
    public static final String NETWORK_VERSION = "1";

    /** 网络通道名称 */
    public static final String NETWORK_CHANNEL = "main";

    /** 最大数据包大小（字节） */
    public static final int MAX_PACKET_SIZE = 32768; // 32KB

    // ========== 文化系统常量 ==========

    /** 默认文化名称 - 华夏文化作为默认文化 */
    public static final String DEFAULT_CULTURE_NAME = "huaxia";

    /** 华夏文化名称 - 中国传统文化 */
    public static final String HUAXIA_CULTURE_NAME = "huaxia";

    /** 诺曼文化名称 - 中世纪欧洲文化 */
    public static final String NORMAN_CULTURE_NAME = "norman";

    /** 日本文化名称 */
    public static final String JAPANESE_CULTURE_NAME = "japanese";

    /** 拜占庭文化名称 */
    public static final String BYZANTINE_CULTURE_NAME = "byzantine";

    /** 最大支持文化数量 */
    public static final int MAX_CULTURES = 16;

    /** 默认文化优先级 */
    public static final int DEFAULT_CULTURE_PRIORITY = 100;

    // ========== 村庄系统常量 ==========

    /** 村庄最小人口 */
    public static final int MIN_VILLAGE_POPULATION = 5;

    /** 村庄最大人口 */
    public static final int MAX_VILLAGE_POPULATION = 300;

    /** 村庄默认人口 */
    public static final int DEFAULT_VILLAGE_POPULATION = 20;

    /** 最大村庄发展等级 */
    public static final int MAX_DEVELOPMENT_LEVEL = 10;

    /** 村庄最小半径（方块） */
    public static final int MIN_VILLAGE_RADIUS = 32;

    /** 村庄最大半径（方块） */
    public static final int MAX_VILLAGE_RADIUS = 256;

    /** 村庄间最小距离（方块） */
    public static final int MIN_VILLAGE_DISTANCE = 512;

    /** 村庄更新频率（游戏tick） */
    public static final int VILLAGE_UPDATE_INTERVAL = 100;

    /** 默认村庄搜索半径（区块） */
    public static final int DEFAULT_VILLAGE_SEARCH_RADIUS = 8;

    /** 默认村民AI更新间隔（tick） */
    public static final int DEFAULT_VILLAGER_AI_UPDATE_INTERVAL = 20;

    // ========== 经济系统常量 ==========

    /** 基础货币单位价值 */
    public static final int BASE_CURRENCY_VALUE = 1;

    /** 默认物品基础价格 */
    public static final int DEFAULT_ITEM_PRICE = 10;

    /** 最大价格波动百分比 */
    public static final double MAX_PRICE_FLUCTUATION = 0.5; // 50%

    /** 最小交易金额 */
    public static final int MIN_TRADE_AMOUNT = 1;

    /** 最大交易金额 */
    public static final int MAX_TRADE_AMOUNT = 10000;

    /** 默认货币汇率基数 */
    public static final int DEFAULT_CURRENCY_BASE = 100;

    /** 银币对铜币汇率 */
    public static final int SILVER_TO_COPPER_RATE = 10;

    /** 金币对银币汇率 */
    public static final int GOLD_TO_SILVER_RATE = 10;

    // ========== 配置相关 ==========

    /** 默认文化配置文件路径 */
    public static final String CULTURE_CONFIG_PATH = "config/" + MOD_ID + "/cultures/";

    /** 默认村庄配置文件路径 */
    public static final String VILLAGE_CONFIG_PATH = "config/" + MOD_ID + "/villages/";

    /** 默认建筑配置文件路径 */
    public static final String BUILDING_CONFIG_PATH = "config/" + MOD_ID + "/buildings/";

    // ========== 调试和日志相关 ==========

    /** 是否启用调试模式 */
    public static final boolean DEBUG_MODE = true;

    /** 日志前缀 */
    public static final String LOG_PREFIX = "[" + MOD_NAME + "] ";

    // ========== 资源相关 ==========

    /** 默认纹理路径 */
    public static final String TEXTURE_PATH = "textures/";

    /** 默认模型路径 */
    public static final String MODEL_PATH = "models/";

    /** 默认音效路径 */
    public static final String SOUND_PATH = "sounds/";

    // ========== 便捷方法 ==========

    /**
     * 创建模组命名空间下的资源位置
     * 
     * @param path 资源路径
     * @return ResourceLocation对象
     */
    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * 获取默认文化ID
     * 
     * @return 默认文化的ResourceLocation
     */
    public static ResourceLocation getDefaultCultureId() {
        return modLoc(DEFAULT_CULTURE_NAME);
    }

    /**
     * 获取华夏文化ID
     * 
     * @return 华夏文化的ResourceLocation
     */
    public static ResourceLocation getHuaxiaCultureId() {
        return modLoc(HUAXIA_CULTURE_NAME);
    }

    /**
     * 获取诺曼文化ID
     * 
     * @return 诺曼文化的ResourceLocation
     */
    public static ResourceLocation getNormanCultureId() {
        return modLoc(NORMAN_CULTURE_NAME);
    }

    /**
     * 获取日本文化ID
     * 
     * @return 日本文化的ResourceLocation
     */
    public static ResourceLocation getJapaneseCultureId() {
        return modLoc(JAPANESE_CULTURE_NAME);
    }

    /**
     * 获取拜占庭文化ID
     * 
     * @return 拜占庭文化的ResourceLocation
     */
    public static ResourceLocation getByzantineCultureId() {
        return modLoc(BYZANTINE_CULTURE_NAME);
    }
}
