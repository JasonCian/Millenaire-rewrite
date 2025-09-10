package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

/**
 * 数据生成器事件处理器
 * 
 * 负责协调和注册所有数据生成器，遵循Millenaire Rewrite的开发规范。
 * 所有物品、方块、配方、语言文件等数据必须通过DataProvider生成，禁止手写JSON。
 * 
 * 功能列表:
 * - 协调所有数据生成器的注册
 * - 方块状态和模型生成
 * - 物品模型生成
 * - 战利品表生成
 * - 配方生成
 * - 标签生成
 * - 语言文件生成（中英双语）
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 1.20.1
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        System.out.println("=== DataGen事件被触发！=== DataGen Event Triggered!");
        MillenaireLogger.info(LogCategory.DATAGEN, "=== 千年村庄重写版DataGen开始执行 ===");

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // 客户端数据生成器
        if (event.includeClient()) {
            MillenaireLogger.info(LogCategory.DATAGEN, "注册客户端数据生成器...");

            // 纹理占位生成器（用于纹理开发）
            // generator.addProvider(true, new
            // MillenaireTexturePlaceholderProvider(packOutput));
            // MillenaireLogger.info(LogCategory.DATAGEN, "纹理占位生成器已注册");

            // 方块状态和模型数据生成器
            generator.addProvider(true, new MillenaireBlockStateProvider(packOutput,
                    existingFileHelper));

            // 物品模型数据生成器
            generator.addProvider(true, new MillenaireItemModelProvider(packOutput, existingFileHelper));
            MillenaireLogger.info(LogCategory.DATAGEN, "物品模型生成器已注册");

            // 语言文件数据生成器 - 支持中文和英文（包含文化系统翻译）
            generator.addProvider(true, new MillenaireLanguageProvider(packOutput, "en_us"));
            generator.addProvider(true, new MillenaireLanguageProvider(packOutput, "zh_cn"));
            MillenaireLogger.info(LogCategory.DATAGEN, "语言文件生成器（含文化系统）已注册");
        }

        // 服务端数据生成器
        if (event.includeServer()) {
            // 配方数据生成器
            generator.addProvider(true, new MillenaireRecipeProvider(packOutput));

            // 战利品表生成器
            generator.addProvider(true, MillenaireLootTableProvider.create(packOutput));

            // 方块标签数据生成器
            MillenaireBlockTagsProvider blockTagsProvider = new MillenaireBlockTagsProvider(
                    packOutput, lookupProvider, existingFileHelper);
            generator.addProvider(true, blockTagsProvider);

            // 文化数据生成器
            generator.addProvider(true, new CultureDataProvider(packOutput));
            MillenaireLogger.info(LogCategory.DATAGEN, "文化数据生成器已注册");
        }

        // 日志记录
        MillenaireLogger.info(LogCategory.DATAGEN, "千年村庄重写版数据生成器已注册完成");
    }
}
