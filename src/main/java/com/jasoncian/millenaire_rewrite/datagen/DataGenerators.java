package com.jasoncian.millenaire_rewrite.datagen;

import com.jasoncian.millenaire_rewrite.MillenaireRewrite;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 数据生成器事件处理器
 * 
 * 负责协调所有数据生成器的注册和执行
 * 现代Minecraft模组开发的最佳实践
 */
@Mod.EventBusSubscriber(modid = MillenaireRewrite.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 客户端数据生成器
        if (event.includeClient()) {
            // 方块状态和模型数据生成器
            generator.addProvider(true, new ModBlockStateProvider(packOutput, existingFileHelper));
            
            // 普通物品模型数据生成器（护符、钱袋、工具等）
            generator.addProvider(true, new ModItemModelProvider(packOutput, existingFileHelper));
            
            // 方块物品模型数据生成器
            generator.addProvider(true, new ModBlockItemModelProvider(packOutput, existingFileHelper));
        }

        // 服务端数据生成器
        if (event.includeServer()) {
            // 方块战利品表生成器
            generator.addProvider(true, ModLootTableProvider.create(packOutput));
        }

        // 语言文件数据生成器 - 支持中文和英文
        if (event.includeClient()) {
            generator.addProvider(true, new ModLanguageProvider(packOutput, "en_us"));
            generator.addProvider(true, new ModLanguageProvider(packOutput, "zh_cn"));
        }

        // 注册配方数据生成器（当我们有配方时）
        // generator.addProvider(event.includeServer(), new
        // ModRecipeProvider(packOutput));

        // 注册标签数据生成器（当我们有标签时）
        // generator.addProvider(event.includeServer(), new
        // ModItemTagProvider(packOutput, lookupProvider, existingFileHelper));

        MillenaireRewrite.LOGGER.info("Data generators registered for Millenaire Rewrite");
    }
}
