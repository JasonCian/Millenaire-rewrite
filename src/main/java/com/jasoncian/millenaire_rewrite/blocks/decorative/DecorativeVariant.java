package com.jasoncian.millenaire_rewrite.blocks.decorative;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MapColor;

/**
 * 装饰方块变体接口 - 1.20.1现代化设计
 * 
 * 替代legacy的IStringSerializable，提供更强的类型安全性
 * 所有装饰方块变体枚举都应实现此接口
 * 
 * 基于legacy装饰方块的EnumType设计，但使用现代Java特性
 */
public interface DecorativeVariant extends StringRepresentable {
    
    /**
     * 获取变体的序列化名称
     * 对应legacy的getName()
     * 
     * @return 用于JSON、NBT等的字符串标识符
     */
    @Override
    String getSerializedName();
    
    /**
     * 获取变体的本地化键名
     * 对应legacy的getUnlocalizedName()
     * 
     * @return 用于语言文件的键名
     */
    String getTranslationKey();
    
    /**
     * 获取变体的MapColor
     * 用于地图显示和颜色标识
     * 
     * @return 该变体的地图颜色
     */
    MapColor getMapColor();
    
    /**
     * 获取变体的硬度
     * 不同材质可能有不同硬度
     * 
     * @return 方块硬度值
     */
    default float getHardness() {
        return 2.0F;
    }
    
    /**
     * 获取变体的爆炸抗性
     * 
     * @return 爆炸抗性值
     */
    default float getExplosionResistance() {
        return 6.0F;
    }
    
    /**
     * 是否需要工具挖掘
     * 
     * @return true如果需要适当工具
     */
    default boolean requiresCorrectToolForDrops() {
        return false;
    }
}
