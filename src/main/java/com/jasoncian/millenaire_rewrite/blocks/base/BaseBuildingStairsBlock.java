package com.jasoncian.millenaire_rewrite.blocks.base;

import com.jasoncian.millenaire_rewrite.blocks.system.BasicBuildingMaterial;
import com.jasoncian.millenaire_rewrite.blocks.system.BlockVariantType;
import com.jasoncian.millenaire_rewrite.blocks.system.CulturalBlockFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Supplier;

/**
 * 基础建筑楼梯方块 - 1.20.1现代化实现
 *
 * 基于BaseBuildingBlock的楼梯方块实现，
 * 提供统一的楼梯方块行为和属性继承。
 *
 * 功能特性：
 * - 继承基础方块的所有属性
 * - 标准的楼梯方块行为
 * - 自动的材料属性应用
 * - 文化归属管理
 *
 * @author JasonCian
 * @version 1.0.0
 */
public class BaseBuildingStairsBlock extends StairBlock {
    
    protected final BasicBuildingMaterial baseMaterial;
    protected final CulturalBlockFamily culture;
    protected final String blockName;
    
    /**
     * 构造函数
     *
     * @param baseBlockState 基础方块状态供应器
     * @param baseMaterial 基础建筑材料
     * @param culture 文化系列
     */
    public BaseBuildingStairsBlock(Supplier<BlockState> baseBlockState, BasicBuildingMaterial baseMaterial, CulturalBlockFamily culture) {
        super(baseBlockState, BaseBuildingBlock.createProperties(baseMaterial.getMaterialProperties()));
        this.baseMaterial = baseMaterial;
        this.culture = culture;
        this.blockName = baseMaterial.generateBlockRegistryName(culture, BlockVariantType.STAIRS);
    }
    
    /**
     * 根据建筑材料创建方块属性（静态方法供外部使用）
     *
     * @param material 建筑材料
     * @return 方块属性
     */
    public static Properties createProperties(com.jasoncian.millenaire_rewrite.blocks.system.BuildingMaterial material) {
        Properties properties = Properties.of()
            .strength(material.getHardness(), material.getExplosionResistance())
            .sound(material.getSoundType())
            .mapColor(material.getMapColor())
            .pushReaction(material.getPushReaction());
            
        if (material.requiresCorrectTool()) {
            properties = properties.requiresCorrectToolForDrops();
        }
        
        return properties;
    }
    
    // ================ 访问器方法 ================
    
    /** 获取基础建筑材料 */
    public BasicBuildingMaterial getBaseMaterial() {
        return baseMaterial;
    }
    
    /** 获取文化系列 */
    public CulturalBlockFamily getCulture() {
        return culture;
    }
    
    /** 获取方块名称 */
    public String getBlockName() {
        return blockName;
    }
    
    /**
     * 生成本地化键名
     *
     * @return 本地化键名
     */
    public String getTranslationKey() {
        return baseMaterial.generateTranslationKey(culture, BlockVariantType.STAIRS);
    }
}
