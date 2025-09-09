package com.jasoncian.millenaire_rewrite.api.culture;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

/**
 * 文化系统API接口
 * 
 * 提供对千年村庄模组文化系统的访问，包括文化的注册、查询、
 * 管理等功能。文化系统是模组的核心组件之一，定义了不同
 * 村庄的建筑风格、经济模式、社会结构等特征。
 * 
 * <p><strong>支持的文化类型：</strong>
 * <ul>
 *   <li>Norman（诺曼）- 中世纪欧洲风格</li>
 *   <li>Japanese（日本）- 日式传统风格</li>
 *   <li>Byzantine（拜占庭）- 东罗马帝国风格</li>
 *   <li>Huaxia（华夏）- 中国传统文化风格</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public interface ICultureAPI {
    
    /**
     * 注册新的文化类型
     * 
     * @param culture 要注册的文化对象
     * @return 如果注册成功返回true，如果文化已存在则返回false
     * @throws IllegalArgumentException 如果文化对象为null或其ID无效
     */
    boolean registerCulture(@NotNull ICulture culture);
    
    /**
     * 根据ID获取文化对象
     * 
     * @param cultureId 文化的唯一标识符
     * @return 包装在Optional中的文化对象，如果不存在则为空
     */
    @NotNull
    Optional<ICulture> getCulture(@NotNull ResourceLocation cultureId);
    
    /**
     * 获取所有已注册的文化
     * 
     * @return 所有文化对象的不可变集合
     */
    @NotNull
    Collection<ICulture> getAllCultures();
    
    /**
     * 检查指定文化是否已注册
     * 
     * @param cultureId 要检查的文化ID
     * @return 如果文化已注册则返回true，否则返回false
     */
    boolean isCultureRegistered(@NotNull ResourceLocation cultureId);
    
    /**
     * 根据名称搜索文化（支持模糊匹配）
     * 
     * @param cultureName 文化名称或部分名称
     * @return 匹配的文化对象集合
     */
    @NotNull
    Collection<ICulture> searchCulturesByName(@NotNull String cultureName);
    
    /**
     * 获取默认文化
     * 
     * @return 默认文化对象，通常为Huaxia文化
     */
    @NotNull
    ICulture getDefaultCulture();
}
