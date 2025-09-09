package com.jasoncian.millenaire_rewrite.api.village;

import com.jasoncian.millenaire_rewrite.api.culture.ICulture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * 村庄系统API接口
 * 
 * 提供对千年村庄模组村庄系统的访问，包括村庄的创建、查询、
 * 管理等功能。村庄系统是模组的核心功能，负责村庄的生成、
 * 发展、村民管理等各个方面。
 * 
 * <p><strong>村庄特性：</strong>
 * <ul>
 *   <li>基于文化的差异化建筑和发展模式</li>
 *   <li>动态的人口增长和经济发展</li>
 *   <li>智能的建筑规划和资源管理</li>
 *   <li>与玩家的交互和声望系统</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public interface IVillageAPI {
    
    /**
     * 在指定位置创建新村庄
     * 
     * @param level 世界对象
     * @param centerPos 村庄中心位置
     * @param culture 村庄文化类型
     * @return 创建的村庄对象，如果创建失败则为空
     */
    @NotNull
    Optional<IVillage> createVillage(@NotNull Level level, 
                                    @NotNull BlockPos centerPos, 
                                    @NotNull ICulture culture);
    
    /**
     * 根据UUID获取村庄
     * 
     * @param villageId 村庄的唯一标识符
     * @return 包装在Optional中的村庄对象，如果不存在则为空
     */
    @NotNull
    Optional<IVillage> getVillage(@NotNull UUID villageId);
    
    /**
     * 根据位置获取最近的村庄
     * 
     * @param level 世界对象
     * @param pos 查询位置
     * @param maxDistance 最大搜索距离（以方块为单位）
     * @return 最近的村庄对象，如果范围内没有村庄则为空
     */
    @NotNull
    Optional<IVillage> getNearestVillage(@NotNull Level level, 
                                        @NotNull BlockPos pos, 
                                        double maxDistance);
    
    /**
     * 获取指定世界中的所有村庄
     * 
     * @param level 世界对象
     * @return 该世界中所有村庄的集合
     */
    @NotNull
    Collection<IVillage> getVillagesInLevel(@NotNull Level level);
    
    /**
     * 获取所有村庄
     * 
     * @return 所有村庄的集合
     */
    @NotNull
    Collection<IVillage> getAllVillages();
    
    /**
     * 根据文化类型获取村庄
     * 
     * @param culture 文化类型
     * @return 该文化类型的所有村庄集合
     */
    @NotNull
    Collection<IVillage> getVillagesByCulture(@NotNull ICulture culture);
    
    /**
     * 检查指定位置是否可以建立村庄
     * 
     * @param level 世界对象
     * @param centerPos 候选位置
     * @param culture 村庄文化类型
     * @return 如果位置适合建村则返回true，否则返回false
     */
    boolean canCreateVillageAt(@NotNull Level level, 
                              @NotNull BlockPos centerPos, 
                              @NotNull ICulture culture);
    
    /**
     * 删除指定的村庄
     * 
     * @param villageId 要删除的村庄ID
     * @return 如果删除成功则返回true，否则返回false
     */
    boolean removeVillage(@NotNull UUID villageId);
    
    /**
     * 保存所有村庄数据
     * 
     * @return 如果保存成功则返回true，否则返回false
     */
    boolean saveAllVillages();
}
