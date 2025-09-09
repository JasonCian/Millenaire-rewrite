package com.jasoncian.millenaire_rewrite;

import com.jasoncian.millenaire_rewrite.api.core.MillenaireAPIManager;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.config.ModConfig;
import com.jasoncian.millenaire_rewrite.culture.data.CultureRegistry;
import com.jasoncian.millenaire_rewrite.init.ModRegistries;
import com.jasoncian.millenaire_rewrite.network.NetworkHandler;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * 千年村庄重写版主模组类
 * 
 * 这是Millenaire Rewrite模组的主入口点，负责：
 * - 初始化所有模组组件和注册表
 * - 处理模组生命周期事件
 * - 协调客户端和服务端的设置
 * - 管理API系统的初始化
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@Mod(ModConstants.MOD_ID)
public class MillenaireRewriteMod {

    /**
     * 模组构造函数
     * 在这里进行基础的模组初始化，包括注册表的初始化和事件监听器的注册
     */
    public MillenaireRewriteMod(FMLJavaModLoadingContext context) {
        long startTime = MillenaireLogger.startTimer();
        
        MillenaireLogger.info(LogCategory.CORE, "Initializing {} v{}", 
                             ModConstants.MOD_NAME, ModConstants.MOD_VERSION);
        
        IEventBus modEventBus = context.getModEventBus();

        // 注册模组生命周期事件监听器
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // 初始化所有注册表
        MillenaireLogger.debug(LogCategory.CORE, "Registering mod components...");
        ModRegistries.init(modEventBus);

        // 注册配置系统 - 现代化的分层配置系统
        MillenaireLogger.debug(LogCategory.CONFIG, "Registering mod configs...");
        context.registerConfig(Type.CLIENT, ModConfig.CLIENT_SPEC);
        context.registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        context.registerConfig(Type.SERVER, ModConfig.SERVER_SPEC);

        // 注册Forge事件总线监听器
        MinecraftForge.EVENT_BUS.register(this);
        
        // 客户端专用初始化
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
        });
        
        MillenaireLogger.logPerformance("Mod initialization", startTime);
        MillenaireLogger.info(LogCategory.CORE, "Mod initialization completed");
    }

    /**
     * 通用设置阶段
     * 在此阶段进行客户端和服务端都需要的初始化工作
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        long startTime = MillenaireLogger.startTimer();
        
        MillenaireLogger.info(LogCategory.CORE, "Starting common setup phase...");
        
        event.enqueueWork(() -> {
            try {
                // 初始化网络通信系统
                MillenaireLogger.debug(LogCategory.NETWORK, "Initializing network handler...");
                NetworkHandler.register();
                
                // 初始化文化系统
                MillenaireLogger.debug(LogCategory.CULTURE, "Initializing culture registry...");
                CultureRegistry cultureRegistry = CultureRegistry.getInstance();
                MillenaireLogger.info(LogCategory.CULTURE, "Culture registry initialized: {}", 
                                    cultureRegistry.isInitialized());
                
                // 初始化API管理器
                MillenaireLogger.debug(LogCategory.CORE, "Initializing API manager...");
                MillenaireAPIManager apiManager = MillenaireAPIManager.getInstance();
                if (!apiManager.initialize()) {
                    MillenaireLogger.error(LogCategory.CORE, "Failed to initialize API manager");
                    return;
                }
                
                MillenaireLogger.info(LogCategory.CORE, "Common setup completed successfully");
                
            } catch (Exception e) {
                MillenaireLogger.error(LogCategory.CORE, "Error during common setup", e);
            }
        });
        
        MillenaireLogger.logPerformance("Common setup", startTime);
    }

    /**
     * 客户端设置阶段
     * 仅在客户端执行的初始化逻辑
     */
    private void clientSetup(final FMLClientSetupEvent event) {
        long startTime = MillenaireLogger.startTimer();
        
        MillenaireLogger.info(LogCategory.CLIENT, "Starting client setup phase...");
        
        event.enqueueWork(() -> {
            try {
                // 初始化客户端专用组件
                // TODO: 注册渲染器、GUI等客户端组件
                
                MillenaireLogger.info(LogCategory.CLIENT, "Client setup completed successfully");
                
            } catch (Exception e) {
                MillenaireLogger.error(LogCategory.CLIENT, "Error during client setup", e);
            }
        });
        
        MillenaireLogger.logPerformance("Client setup", startTime);
    }

    /**
     * 添加物品到创造模式物品栏
     * 
     * @param event 创造模式物品栏内容构建事件
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // 当前暂时为空，后续添加物品时会在这里实现
        // TODO: 将模组物品添加到相应的创造模式物品栏
        MillenaireLogger.debug(LogCategory.CORE, "Adding items to creative tabs...");
    }

    /**
     * 服务器启动事件处理
     * 
     * @param event 服务器启动事件
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        MillenaireLogger.info(LogCategory.SERVER, "Server starting - initializing server-side components");
        
        // TODO: 初始化服务端专用组件
        // 例如：村庄管理器、世界数据等
    }
}
