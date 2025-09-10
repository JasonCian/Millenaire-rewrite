package com.jasoncian.millenaire_rewrite.client.model.entity;

import com.jasoncian.millenaire_rewrite.entity.villager.MillVillagerEntity;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * 千年村民实体模型
 * 
 * 基于Minecraft原版村民模型进行修改，支持：
 * - 文化相关的模型变体
 * - 职业相关的装备和道具
 * - 性别差异的视觉表现
 * - 动画系统
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public class MillVillagerModel<T extends MillVillagerEntity> extends HierarchicalModel<T> {

    // ====================== 模型层位置 ======================
    
    /**
     * 村民模型层位置
     */
    public static final ModelLayerLocation LAYER_LOCATION = 
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "mill_villager"), "main");

    // ====================== 模型部件 ======================
    
    /**
     * 根部件（整个模型的根节点）
     */
    private final ModelPart root;
    
    /**
     * 头部部件
     */
    private final ModelPart head;
    
    /**
     * 身体部件
     */
    private final ModelPart body;
    
    /**
     * 右臂部件
     */
    private final ModelPart rightArm;
    
    /**
     * 左臂部件
     */
    private final ModelPart leftArm;
    
    /**
     * 右腿部件
     */
    private final ModelPart rightLeg;
    
    /**
     * 左腿部件
     */
    private final ModelPart leftLeg;
    
    /**
     * 鼻子部件（村民特色）
     */
    private final ModelPart nose;

    // ====================== 构造函数 ======================

    /**
     * 创建千年村民模型
     * 
     * @param root 根模型部件
     */
    public MillVillagerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.nose = this.head.getChild("nose");
    }

    // ====================== 模型定义 ======================

    /**
     * 创建模型层定义
     * 
     * 定义村民模型的几何结构，包括各个身体部位的尺寸、位置和纹理映射。
     * 
     * @return 层定义
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        // 头部（包含鼻子）
        PartDefinition head = partDefinition.addOrReplaceChild("head", 
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 鼻子（村民特色）
        head.addOrReplaceChild("nose", 
                CubeListBuilder.create()
                        .texOffs(24, 0)
                        .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, -2.0F, 0.0F));

        // 身体
        partDefinition.addOrReplaceChild("body", 
                CubeListBuilder.create()
                        .texOffs(16, 20)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 右臂
        partDefinition.addOrReplaceChild("right_arm", 
                CubeListBuilder.create()
                        .texOffs(44, 22)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        // 左臂
        partDefinition.addOrReplaceChild("left_arm", 
                CubeListBuilder.create()
                        .texOffs(44, 22)
                        .mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(5.0F, 2.0F, 0.0F));

        // 右腿
        partDefinition.addOrReplaceChild("right_leg", 
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        // 左腿
        partDefinition.addOrReplaceChild("left_leg", 
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    // ====================== 动画系统 ======================

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, 
                         float ageInTicks, float netHeadYaw, float headPitch) {
        
        // 头部旋转
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // 手臂摆动动画
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;

        // 腿部行走动画
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;
        this.rightLeg.zRot = 0.0F;
        this.leftLeg.zRot = 0.0F;

        // 手臂自然摆动
        if (entity.isAlive()) {
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
            
            // 添加轻微的呼吸动画
            float breathingAnimation = Mth.sin(ageInTicks * 0.067F) * 0.05F;
            this.body.y = breathingAnimation;
        }

        // 根据职业调整姿势
        adjustPoseForProfession(entity);
    }

    /**
     * 根据职业调整姿势
     * 
     * 不同职业的村民会有不同的站立姿势和手臂位置。
     * 
     * @param entity 村民实体
     */
    private void adjustPoseForProfession(T entity) {
        switch (entity.getProfession()) {
            case SCHOLAR:
                // 学者：更直挺的姿势，手臂稍微向前
                this.rightArm.xRot += -0.1F;
                this.leftArm.xRot += -0.1F;
                break;
                
            case BLACKSMITH:
                // 铁匠：更宽的站立姿势，手臂稍微分开
                this.rightArm.zRot += 0.1F;
                this.leftArm.zRot -= 0.1F;
                break;
                
            case GUARD:
                // 守卫：军姿，更直的姿势
                this.body.xRot = -0.05F;
                this.rightArm.xRot = -0.1F;
                this.leftArm.xRot = -0.1F;
                break;
                
            default:
                // 默认姿势（农民等）
                break;
        }
    }

    // ====================== 工具方法 ======================

    @Override
    @NotNull
    public ModelPart root() {
        return this.root;
    }

    /**
     * 获取头部模型部件
     * 
     * @return 头部部件
     */
    public ModelPart getHead() {
        return this.head;
    }

    /**
     * 设置模型部件的可见性
     * 
     * @param visible 是否可见
     */
    public void setAllVisible(boolean visible) {
        this.head.visible = visible;
        this.body.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }

    /**
     * 重置模型部件到默认位置
     */
    public void resetToDefaultPose() {
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
        this.leftArm.setPos(5.0F, 2.0F, 0.0F);
        this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
        this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
        
        // 重置旋转
        this.head.setRotation(0.0F, 0.0F, 0.0F);
        this.body.setRotation(0.0F, 0.0F, 0.0F);
        this.rightArm.setRotation(0.0F, 0.0F, 0.0F);
        this.leftArm.setRotation(0.0F, 0.0F, 0.0F);
        this.rightLeg.setRotation(0.0F, 0.0F, 0.0F);
        this.leftLeg.setRotation(0.0F, 0.0F, 0.0F);
    }
}
