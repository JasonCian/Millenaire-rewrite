package com.jasoncian.millenaire_rewrite.api.core;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * 千年村庄重写版核心API入口点
 * 
 * 这个接口提供了访问所有模组核心功能的统一入口，
 * 包括文化系统、村庄系统、经济系统等主要组件的API访问。
 * 
 * <p><strong>API稳定性说明：</strong>
 * 当前版本为实验性API，可能在未来版本中发生变化。
 * 建议第三方开发者谨慎使用，并关注版本更新说明。</p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public interface IMillenaireAPI {
    
    /**
     * 获取模组的唯一标识符
     * 
     * @return 模组ID字符串
     */
    String getModId();
    
    /**
     * 获取模组的当前版本
     * 
     * @return 版本字符串
     */
    String getModVersion();
    
    /**
     * 检查模组是否已完全初始化
     * 
     * @return 如果模组已初始化则返回true，否则返回false
     */
    boolean isInitialized();
    
    /**
     * 创建模组命名空间下的资源位置
     * 
     * @param path 资源路径
     * @return 带有模组命名空间的ResourceLocation对象
     * @throws IllegalArgumentException 如果路径为null或空字符串
     */
    ResourceLocation createModResource(String path);
}
