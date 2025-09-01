package com.jasoncian.millenaire_rewrite.config;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Millenaire mod配置管理
 * 
 * 处理所有mod的配置选项，包括村庄生成、村民行为等设置
 */
@Mod.EventBusSubscriber(modid = MillenaireRewrite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MillenaireConfig {

    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // === 村庄生成设置 ===
    public static final ForgeConfigSpec.BooleanValue GENERATE_VILLAGES;
    public static final ForgeConfigSpec.BooleanValue GENERATE_LONE_BUILDINGS;
    public static final ForgeConfigSpec.IntValue MIN_VILLAGE_DISTANCE;
    public static final ForgeConfigSpec.IntValue MIN_LONE_DISTANCE;
    public static final ForgeConfigSpec.IntValue SPAWN_DISTANCE;

    // === 村民行为设置 ===
    public static final ForgeConfigSpec.BooleanValue LEARN_LANGUAGES;
    public static final ForgeConfigSpec.BooleanValue VILLAGE_ANNOUNCEMENTS;
    public static final ForgeConfigSpec.BooleanValue BUILD_PATHS;
    public static final ForgeConfigSpec.IntValue MAX_CHILDREN;

    // === 性能设置 ===
    public static final ForgeConfigSpec.IntValue LOADED_RADIUS;
    public static final ForgeConfigSpec.IntValue MIN_BUILDING_DISTANCE;

    static {
        BUILDER.comment("Millenaire村庄生成设置")
               .push("village_generation");

        GENERATE_VILLAGES = BUILDER
            .comment("是否生成村庄")
            .define("generate_villages", true);

        GENERATE_LONE_BUILDINGS = BUILDER
            .comment("是否生成独立建筑")
            .define("generate_lone_buildings", true);

        MIN_VILLAGE_DISTANCE = BUILDER
            .comment("村庄间最小距离（区块）")
            .defineInRange("min_village_distance", 20, 5, 100);

        MIN_LONE_DISTANCE = BUILDER
            .comment("独立建筑间最小距离（区块）")
            .defineInRange("min_lone_distance", 10, 2, 50);

        SPAWN_DISTANCE = BUILDER
            .comment("距离出生点的最小生成距离（区块）")
            .defineInRange("spawn_distance", 20, 0, 100);

        BUILDER.pop();

        BUILDER.comment("Millenaire村民行为设置")
               .push("villager_behavior");

        LEARN_LANGUAGES = BUILDER
            .comment("玩家是否可以学习村民语言")
            .define("learn_languages", true);

        VILLAGE_ANNOUNCEMENTS = BUILDER
            .comment("是否显示村庄公告")
            .define("village_announcements", true);

        BUILD_PATHS = BUILDER
            .comment("村民是否建造道路")
            .define("build_paths", true);

        MAX_CHILDREN = BUILDER
            .comment("每个村庄的最大儿童数量")
            .defineInRange("max_children", 5, 0, 20);

        BUILDER.pop();

        BUILDER.comment("Millenaire性能设置")
               .push("performance");

        LOADED_RADIUS = BUILDER
            .comment("村庄活动半径（区块）")
            .defineInRange("loaded_radius", 8, 2, 32);

        MIN_BUILDING_DISTANCE = BUILDER
            .comment("建筑间最小距离（方块）")
            .defineInRange("min_building_distance", 5, 1, 20);

        BUILDER.pop();
    }

    // 配置规范
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    /**
     * 配置加载事件处理
     */
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        MillenaireRewrite.LOGGER.info("Millenaire config loaded: {}", configEvent.getConfig().getFileName());
    }

    /**
     * 配置重载事件处理
     */
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        MillenaireRewrite.LOGGER.info("Millenaire config reloaded: {}", configEvent.getConfig().getFileName());
    }
}
