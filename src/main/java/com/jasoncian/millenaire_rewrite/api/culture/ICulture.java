package com.jasoncian.millenaire_rewrite.api.culture;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * 文化对象接口
 * 
 * 定义了千年村庄模组中文化的基本属性和行为。
 * 每个文化代表一种独特的建筑风格、社会结构和经济模式。
 * 
 * <p><strong>文化特征包括：</strong>
 * <ul>
 *   <li>唯一标识符和显示名称</li>
 *   <li>建筑风格和材料偏好</li>
 *   <li>社会等级和职业体系</li>
 *   <li>经济结构和贸易偏好</li>
 *   <li>特色物品和装备</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public interface ICulture {
    
    /**
     * 获取文化的唯一标识符
     * 
     * @return 文化ID，格式为"modid:culture_name"
     */
    @NotNull
    ResourceLocation getId();
    
    /**
     * 获取文化的显示名称
     * 
     * @return 本地化的文化名称
     */
    @NotNull
    String getDisplayName();
    
    /**
     * 获取文化的描述信息
     * 
     * @return 本地化的文化描述
     */
    @NotNull
    String getDescription();
    
    /**
     * 检查此文化是否为默认文化
     * 
     * @return 如果是默认文化则返回true，否则返回false
     */
    boolean isDefault();
    
    /**
     * 检查此文化是否可用（已完全实现）
     * 
     * @return 如果文化可用则返回true，否则返回false
     */
    boolean isAvailable();
    
    /**
     * 获取文化的发展优先级
     * 不同文化可能有不同的发展重点（如军事、商业、学术等）
     * 
     * @return 发展优先级枚举值
     */
    @NotNull
    CulturePriority getDevelopmentPriority();
    
    /**
     * 获取文化的建筑风格标识符
     * 
     * @return 建筑风格ID
     */
    @NotNull
    ResourceLocation getArchitecturalStyle();
}
