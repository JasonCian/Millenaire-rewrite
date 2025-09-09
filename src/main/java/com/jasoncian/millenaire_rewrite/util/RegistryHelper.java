package com.jasoncian.millenaire_rewrite.util;

import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * 注册表助手工具类
 * 
 * 提供统一的注册表操作方法，简化物品、方块等对象的注册流程。
 * 包含常用的注册模式和便捷方法，确保注册过程的一致性和可维护性。
 * 
 * <p><strong>主要功能：</strong>
 * <ul>
 *   <li>方块和对应物品的联合注册</li>
 *   <li>批量物品注册</li>
 *   <li>常用属性配置的便捷方法</li>
 *   <li>注册过程的日志记录</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
public final class RegistryHelper {

    // 防止实例化工具类
    private RegistryHelper() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    /**
     * 注册方块并自动创建对应的物品
     * 
     * @param <T> 方块类型
     * @param blockRegister 方块注册表
     * @param itemRegister 物品注册表
     * @param name 注册名称
     * @param blockSupplier 方块供应商
     * @return 方块的RegistryObject
     */
    public static <T extends Block> RegistryObject<T> registerBlockWithItem(
            DeferredRegister<Block> blockRegister,
            DeferredRegister<Item> itemRegister,
            String name,
            Supplier<T> blockSupplier) {
        
        // 注册方块
        RegistryObject<T> blockRegistryObject = blockRegister.register(name, blockSupplier);
        
        // 注册对应的物品
        itemRegister.register(name, () -> new BlockItem(blockRegistryObject.get(), basicItemProperties()));
        
        logRegistration("方块", name);
        return blockRegistryObject;
    }

    /**
     * 注册方块并创建自定义物品
     * 
     * @param <T> 方块类型
     * @param <U> 物品类型
     * @param blockRegister 方块注册表
     * @param itemRegister 物品注册表
     * @param name 注册名称
     * @param blockSupplier 方块供应商
     * @param itemSupplier 物品供应商
     * @return 方块的RegistryObject
     */
    public static <T extends Block, U extends Item> RegistryObject<T> registerBlockWithCustomItem(
            DeferredRegister<Block> blockRegister,
            DeferredRegister<Item> itemRegister,
            String name,
            Supplier<T> blockSupplier,
            Supplier<U> itemSupplier) {
        
        // 注册方块
        RegistryObject<T> blockRegistryObject = blockRegister.register(name, blockSupplier);
        
        // 注册自定义物品
        itemRegister.register(name, itemSupplier);
        
        logRegistration("方块(自定义物品)", name);
        return blockRegistryObject;
    }

    /**
     * 批量注册物品
     * 
     * @param itemRegister 物品注册表
     * @param items 物品条目数组
     * @return 物品RegistryObject数组
     */
    @SuppressWarnings("unchecked")
    public static RegistryObject<Item>[] registerItems(
            DeferredRegister<Item> itemRegister,
            ItemEntry... items) {
        
        RegistryObject<Item>[] result = new RegistryObject[items.length];
        
        for (int i = 0; i < items.length; i++) {
            ItemEntry entry = items[i];
            result[i] = itemRegister.register(entry.name, entry.supplier);
            logRegistration("物品", entry.name);
        }
        
        return result;
    }

    /**
     * 物品注册条目记录
     * 包含物品名称和供应商的配对
     */
    public record ItemEntry(String name, Supplier<Item> supplier) {
        // 记录类自动生成构造函数和访问器
    }

    // ========== 常用属性配置方法 ==========

    /**
     * 获取基础物品属性
     * 
     * @return 基础物品属性对象
     */
    public static Item.Properties basicItemProperties() {
        return new Item.Properties();
    }

    /**
     * 获取指定堆叠大小的物品属性
     * 
     * @param stackSize 堆叠大小
     * @return 物品属性对象
     */
    public static Item.Properties itemPropertiesWithStackSize(int stackSize) {
        return new Item.Properties().stacksTo(stackSize);
    }

    /**
     * 获取不可堆叠物品属性
     * 
     * @return 不可堆叠的物品属性对象
     */
    public static Item.Properties unstackableItemProperties() {
        return new Item.Properties().stacksTo(1);
    }

    // ========== 工具方法 ==========

    /**
     * 创建模组命名空间下的资源位置
     * 
     * @param path 资源路径
     * @return ResourceLocation对象
     */
    public static ResourceLocation modLoc(String path) {
        return ModConstants.modLoc(path);
    }

    /**
     * 记录注册操作的日志
     * 
     * @param type 注册类型
     * @param name 注册名称
     */
    public static void logRegistration(String type, String name) {
        MillenaireLogger.debugOnly(LogCategory.CORE, "注册{}: {}", type, name);
    }
}
