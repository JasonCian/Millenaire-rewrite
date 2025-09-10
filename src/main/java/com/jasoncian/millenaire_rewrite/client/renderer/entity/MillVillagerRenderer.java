package com.jasoncian.millenaire_rewrite.client.renderer.entity;

import com.jasoncian.millenaire_rewrite.entity.villager.MillVillagerEntity;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * 千年村民实体渲染器
 * 
 * 负责渲染千年村民实体，使用玩家模型来提供更好的视觉效果：
 * - 基于玩家模型的渲染
 * - 文化相关的皮肤纹理选择
 * - 职业装备渲染
 * - 性别和年龄变化的视觉表现
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public class MillVillagerRenderer extends MobRenderer<MillVillagerEntity, PlayerModel<MillVillagerEntity>> {

    // ====================== 纹理资源路径 ======================

    /**
     * 默认村民皮肤（华夏文化，男性，农民）
     */
    private static final ResourceLocation DEFAULT_SKIN = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID,
            "textures/entity/villager/huaxia_male_farmer.png");

    // ====================== 构造函数 ======================

    /**
     * 创建千年村民渲染器
     * 
     * @param context 实体渲染器提供器上下文
     */
    public MillVillagerRenderer(EntityRendererProvider.Context context) {
        // 使用原版玩家模型而不是自定义的村民模型
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);

        // TODO: 添加渲染层（装备、手持物品等）
        // this.addLayer(new MillVillagerEquipmentLayer<>(this));
        // this.addLayer(new MillVillagerHeldItemLayer<>(this));
    }

    // ====================== 核心渲染方法 ======================

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull MillVillagerEntity entity) {
        // 根据村民的文化、性别、职业获取对应的皮肤
        return getVillagerSkin(entity);
    }

    @Override
    public void render(@NotNull MillVillagerEntity entity, float entityYaw, float partialTicks,
            @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        // 根据年龄调整模型缩放
        poseStack.pushPose();
        applyAgeScaling(entity, poseStack);

        // 调用父类渲染方法
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.popPose();
    }

    // ====================== 皮肤选择系统 ======================

    /**
     * 获取村民皮肤
     * 
     * 根据村民的文化、性别、职业组合生成皮肤路径。
     * 皮肤命名规则：{culture}_{gender}_{profession}.png
     * 
     * @param entity 村民实体
     * @return 皮肤资源位置
     */
    private ResourceLocation getVillagerSkin(MillVillagerEntity entity) {
        String culture = entity.getCultureType().getSerializedName().toLowerCase();
        String gender = entity.getGender().getId().toLowerCase();
        String profession = entity.getProfession().getId().toLowerCase();

        // 构建皮肤路径
        String skinPath = String.format("textures/entity/villager/%s_%s_%s.png",
                culture, gender, profession);

        ResourceLocation skinLocation = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, skinPath);

        // TODO: 添加皮肤存在性检查，如果皮肤不存在则使用回退皮肤
        // if (!isSkinExists(skinLocation)) {
        // return getFallbackSkin(culture, gender);
        // }

        return skinLocation;
    }

    /**
     * 获取回退皮肤
     * 
     * 当特定组合的皮肤不存在时，使用回退机制：
     * 1. 尝试使用文化+性别的默认皮肤
     * 2. 尝试使用文化默认皮肤
     * 3. 使用全局默认皮肤
     * 
     * @param culture 文化标识
     * @param gender  性别标识
     * @return 回退皮肤资源位置
     */
    @SuppressWarnings("unused")
    private ResourceLocation getFallbackSkin(String culture, String gender) {
        // 尝试文化+性别的默认皮肤
        String fallbackPath = String.format("textures/entity/villager/%s_%s_default.png",
                culture, gender);
        @SuppressWarnings("unused")
        ResourceLocation fallbackLocation = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, fallbackPath);

        // TODO: 检查回退皮肤是否存在
        // if (isSkinExists(fallbackLocation)) {
        // return fallbackLocation;
        // }

        // 最终回退到默认皮肤
        return DEFAULT_SKIN;
    }

    // ====================== 模型变换 ======================

    /**
     * 根据年龄应用缩放变换
     * 
     * 年轻的村民会显得更小，年老的村民保持正常大小。
     * 这提供了视觉上的年龄差异。
     * 
     * @param entity    村民实体
     * @param poseStack 姿势堆栈
     */
    private void applyAgeScaling(MillVillagerEntity entity, PoseStack poseStack) {
        int age = entity.getAge();

        // 计算年龄系数（0-1之间）
        // 假设24000 tick（1游戏日）为成年，48000 tick为完全成熟
        float ageRatio = Math.min(1.0F, (float) age / 48000.0F);

        // 年龄缩放：从0.6（儿童）到1.0（成人）
        float scale = 0.6F + (0.4F * ageRatio);

        // 应用缩放
        poseStack.scale(scale, scale, scale);
    }

    // ====================== 工具方法 ======================

    /**
     * 检查皮肤文件是否存在
     * 
     * TODO: 实现皮肤存在性检查
     * 
     * @param skinLocation 皮肤资源位置
     * @return 皮肤是否存在
     */
    @SuppressWarnings("unused")
    private boolean isSkinExists(ResourceLocation skinLocation) {
        // 这里需要实现皮肤文件的存在性检查
        // 可以通过ResourceManager来检查资源是否存在
        return true; // 暂时总是返回true
    }

    /**
     * 获取模型缩放比例
     * 
     * @param entity 村民实体
     * @return 缩放比例
     */
    @Override
    protected float getFlipDegrees(MillVillagerEntity entity) {
        return 180.0F; // 村民死亡时的翻转角度
    }

    /**
     * 是否应该显示名称标签
     * 
     * @param entity 村民实体
     * @return 是否显示名称
     */
    @Override
    protected boolean shouldShowName(MillVillagerEntity entity) {
        // 只有在玩家靠近且村民有名字时显示名称
        return entity.hasCustomName() && entity.shouldShowName();
    }
}
