package com.jasoncian.millenaire_rewrite.blocks.base;

import com.jasoncian.millenaire_rewrite.blocks.system.BuildingMaterial;
import com.jasoncian.millenaire_rewrite.blocks.system.CulturalBlockFamily;
import com.jasoncian.millenaire_rewrite.blocks.system.BasicBuildingMaterial;
import com.jasoncian.millenaire_rewrite.blocks.system.BlockVariantType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 基础建筑方块 - 1.20.1现代化实现
 *
 * 所有文化建筑方块的基类，提供统一的材料属性和行为
 * 基于新的材料系统和文化分类的现代化设计
 *
 * 功能特性：
 * - 统一的材料属性应用
 * - 文化归属管理
 * - 自动属性配置
 * - 扩展友好的设计
 * - 支持所有变体类型
 *
 * @author JasonCian
 * @version 2.0.0
 */
public class BaseBuildingBlock extends Block {
    
    protected final BasicBuildingMaterial baseMaterial;
    protected final CulturalBlockFamily culture;
    protected final BlockVariantType variant;
    protected final String blockName;
    
    /**
     * 构造函数
     *
     * @param baseMaterial 基础建筑材料
     * @param culture 文化系列
     * @param variant 方块变体类型
     */
    public BaseBuildingBlock(BasicBuildingMaterial baseMaterial, CulturalBlockFamily culture, BlockVariantType variant) {
        super(createProperties(baseMaterial.getMaterialProperties()));
        this.baseMaterial = baseMaterial;
        this.culture = culture;
        this.variant = variant;
        this.blockName = baseMaterial.generateBlockRegistryName(culture, variant);
    }
    
    /**
     * 根据建筑材料创建方块属性
     *
     * @param material 建筑材料
     * @return 方块属性
     */
    public static BlockBehaviour.Properties createProperties(BuildingMaterial material) {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
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
    
    /** 获取方块变体类型 */
    public BlockVariantType getVariant() {
        return variant;
    }
    
    /** 获取方块名称 */
    public String getBlockName() {
        return blockName;
    }
    
    /** 获取建筑材料属性 */
    public BuildingMaterial getMaterialProperties() {
        return baseMaterial.getMaterialProperties();
    }
    
    // ================ 实用方法 ================
    
    /**
     * 生成本地化键名
     *
     * @return 本地化键名
     */
    public String getTranslationKey() {
        return baseMaterial.generateTranslationKey(culture, variant);
    }
    
    /**
     * 检查是否为基础方块
     *
     * @return 是否为基础方块
     */
    public boolean isBaseBlock() {
        return variant.isBaseBlock();
    }
    
    /**
     * 检查是否需要基础方块引用
     *
     * @return 是否需要基础方块引用  
     */
    public boolean requiresBaseBlock() {
        return variant.requiresBaseBlock();
    }
}
