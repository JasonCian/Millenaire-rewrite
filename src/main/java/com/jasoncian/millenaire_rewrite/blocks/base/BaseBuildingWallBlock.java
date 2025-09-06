package com.jasoncian.millenaire_rewrite.blocks.base;

import com.jasoncian.millenaire_rewrite.blocks.system.BasicBuildingMaterial;
import com.jasoncian.millenaire_rewrite.blocks.system.BlockVariantType;
import com.jasoncian.millenaire_rewrite.blocks.system.CulturalBlockFamily;
import net.minecraft.world.level.block.WallBlock;

/**
 * 基础建筑墙方块 - 1.20.1现代化实现
 *
 * 基于BaseBuildingBlock的墙方块实现，
 * 提供统一的墙方块行为和属性继承。
 *
 * 功能特性：
 * - 继承基础方块的所有属性
 * - 标准的墙方块连接行为
 * - 自动的材料属性应用
 * - 文化归属管理
 *
 * @author JasonCian
 * @version 1.0.0
 */
public class BaseBuildingWallBlock extends WallBlock {
    
    protected final BasicBuildingMaterial baseMaterial;
    protected final CulturalBlockFamily culture;
    protected final String blockName;
    
    /**
     * 构造函数
     *
     * @param baseMaterial 基础建筑材料
     * @param culture 文化系列
     */
    public BaseBuildingWallBlock(BasicBuildingMaterial baseMaterial, CulturalBlockFamily culture) {
        super(BaseBuildingBlock.createProperties(baseMaterial.getMaterialProperties()));
        this.baseMaterial = baseMaterial;
        this.culture = culture;
        this.blockName = baseMaterial.generateBlockRegistryName(culture, BlockVariantType.WALL);
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
        return baseMaterial.generateTranslationKey(culture, BlockVariantType.WALL);
    }
}
