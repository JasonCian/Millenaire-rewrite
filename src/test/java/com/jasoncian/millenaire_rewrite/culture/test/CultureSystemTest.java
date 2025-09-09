package com.jasoncian.millenaire_rewrite.culture.test;

import com.jasoncian.millenaire_rewrite.culture.data.Culture;
import com.jasoncian.millenaire_rewrite.culture.data.CultureRegistry;
import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;

/**
 * 文化系统测试类
 * 
 * 用于测试文化系统的基本功能，包括：
 * - 文化类型枚举的正确性
 * - Culture类的基本功能
 * - CultureRegistry的注册和查询功能
 * 
 * 这个类主要用于开发阶段的功能验证。
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public class CultureSystemTest {
    
    /**
     * 测试文化类型枚举
     */
    public static void testCultureTypes() {
        MillenaireLogger.info(LogCategory.CULTURE, "=== Testing Culture Types ===");
        
        for (CultureType cultureType : CultureType.values()) {
            MillenaireLogger.info(LogCategory.CULTURE, "Culture Type: {} ({})", 
                                cultureType.getName(), cultureType.getSerializedName());
            MillenaireLogger.info(LogCategory.CULTURE, "  Display Key: {}", cultureType.getTranslationKey());
            MillenaireLogger.info(LogCategory.CULTURE, "  Data Path: {}", cultureType.getDataPath());
            MillenaireLogger.info(LogCategory.CULTURE, "  Color: 0x{}", Integer.toHexString(cultureType.getPrimaryColor()));
            MillenaireLogger.info(LogCategory.CULTURE, "  Is Legacy: {}", cultureType.isLegacyCulture());
            MillenaireLogger.info(LogCategory.CULTURE, "  Is New: {}", cultureType.isNewCulture());
            MillenaireLogger.info(LogCategory.CULTURE, "  Resource Location: {}", cultureType.getResourceLocation());
        }
        
        MillenaireLogger.info(LogCategory.CULTURE, "Total culture types: {}", CultureType.values().length);
        MillenaireLogger.info(LogCategory.CULTURE, "Legacy cultures: {}", CultureType.getLegacyCultures().length);
        MillenaireLogger.info(LogCategory.CULTURE, "New cultures: {}", CultureType.getNewCultures().length);
    }
    
    /**
     * 测试Culture类的基本功能
     */
    public static void testCultureClass() {
        MillenaireLogger.info(LogCategory.CULTURE, "=== Testing Culture Class ===");
        
        // 创建测试文化实例
        Culture testCulture = new Culture(CultureType.HUAXIA);
        testCulture.setDisplayName("测试华夏文化");
        testCulture.setDescription("这是一个测试用的华夏文化实例");
        testCulture.setArchitecturalStyle("traditional_chinese");
        
        MillenaireLogger.info(LogCategory.CULTURE, "Created test culture: {}", testCulture);
        MillenaireLogger.info(LogCategory.CULTURE, "  Type: {}", testCulture.getType());
        MillenaireLogger.info(LogCategory.CULTURE, "  Display Name: {}", testCulture.getDisplayName());
        MillenaireLogger.info(LogCategory.CULTURE, "  Description: {}", testCulture.getDescription());
        MillenaireLogger.info(LogCategory.CULTURE, "  Architectural Style: {}", testCulture.getArchitecturalStyle());
        MillenaireLogger.info(LogCategory.CULTURE, "  Version: {}", testCulture.getVersion());
        MillenaireLogger.info(LogCategory.CULTURE, "  Enabled: {}", testCulture.isEnabled());
        
        // 测试集合
        MillenaireLogger.info(LogCategory.CULTURE, "  Supported Professions: {}", testCulture.getSupportedProfessions().size());
        MillenaireLogger.info(LogCategory.CULTURE, "  Building Types: {}", testCulture.getBuildingTypes().size());
        MillenaireLogger.info(LogCategory.CULTURE, "  Currency Types: {}", testCulture.getCurrencyTypes().size());
    }
    
    /**
     * 测试CultureRegistry的基本功能
     */
    public static void testCultureRegistry() {
        MillenaireLogger.info(LogCategory.CULTURE, "=== Testing Culture Registry ===");
        
        CultureRegistry registry = CultureRegistry.getInstance();
        
        MillenaireLogger.info(LogCategory.CULTURE, "Registry initialized: {}", registry.isInitialized());
        MillenaireLogger.info(LogCategory.CULTURE, "Registered culture count: {}", registry.getRegisteredCultureCount());
        MillenaireLogger.info(LogCategory.CULTURE, "Has loading failures: {}", registry.hasLoadingFailures());
        
        // 测试文化查询
        for (CultureType cultureType : CultureType.values()) {
            boolean isRegistered = registry.isRegistered(cultureType);
            MillenaireLogger.info(LogCategory.CULTURE, "Culture {} registered: {}", 
                                cultureType.getName(), isRegistered);
            
            if (isRegistered) {
                Culture culture = registry.getCulture(cultureType);
                if (culture != null) {
                    MillenaireLogger.info(LogCategory.CULTURE, "  Culture instance: {}", culture);
                }
            }
        }
        
        // 输出诊断信息
        registry.printDiagnostics();
    }
    
    /**
     * 运行所有测试
     */
    public static void runAllTests() {
        MillenaireLogger.info(LogCategory.CULTURE, "Starting Culture System Tests...");
        
        try {
            testCultureTypes();
            testCultureClass();
            testCultureRegistry();
            
            MillenaireLogger.info(LogCategory.CULTURE, "All culture system tests completed successfully!");
            
        } catch (Exception e) {
            MillenaireLogger.error(LogCategory.CULTURE, "Culture system test failed", e);
        }
    }
}
