package com.jasoncian.millenaire_rewrite.api.village;

import com.jasoncian.millenaire_rewrite.api.culture.ICulture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 村庄对象接口
 * 
 * 定义了千年村庄模组中村庄的基本属性和行为。
 * 每个村庄都有独特的文化背景、发展水平和管理结构。
 * 
 * <p><strong>村庄核心特征：</strong>
 * <ul>
 *   <li>唯一标识符和基本位置信息</li>
 *   <li>所属文化和发展等级</li>
 *   <li>人口规模和经济状况</li>
 *   <li>建筑布局和扩展规划</li>
 * </ul></p>
 * 
 * @author JasonCian
 * @version 0.1.4-alpha
 * @since 2025-09-09
 */
@ApiStatus.Experimental
public interface IVillage {
    
    /**
     * 获取村庄的唯一标识符
     * 
     * @return 村庄UUID
     */
    @NotNull
    UUID getId();
    
    /**
     * 获取村庄的显示名称
     * 
     * @return 本地化的村庄名称
     */
    @NotNull
    String getName();
    
    /**
     * 获取村庄所属的文化
     * 
     * @return 文化对象
     */
    @NotNull
    ICulture getCulture();
    
    /**
     * 获取村庄的中心位置
     * 
     * @return 村庄中心坐标
     */
    @NotNull
    BlockPos getCenterPos();
    
    /**
     * 获取村庄所在的世界
     * 
     * @return 世界对象
     */
    @NotNull
    Level getLevel();
    
    /**
     * 获取村庄当前的发展等级
     * 
     * @return 发展等级（1-10）
     */
    int getDevelopmentLevel();
    
    /**
     * 获取村庄当前人口数量
     * 
     * @return 人口数量
     */
    int getPopulation();
    
    /**
     * 获取村庄的最大人口容量
     * 
     * @return 最大人口数量
     */
    int getMaxPopulation();
    
    /**
     * 获取村庄的当前状态
     * 
     * @return 村庄状态枚举值
     */
    @NotNull
    VillageStatus getStatus();
    
    /**
     * 检查村庄是否处于活跃状态
     * 
     * @return 如果村庄活跃则返回true，否则返回false
     */
    boolean isActive();
    
    /**
     * 更新村庄状态（每个游戏tick调用）
     */
    void tick();
    
    /**
     * 保存村庄数据
     * 
     * @return 如果保存成功则返回true，否则返回false
     */
    boolean save();
}
