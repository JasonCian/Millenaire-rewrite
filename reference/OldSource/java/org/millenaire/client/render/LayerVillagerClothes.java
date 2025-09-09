package org.millenaire.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.millenaire.common.entity.MillVillager;

public class LayerVillagerClothes implements LayerRenderer<EntityLivingBase> {
  protected final ModelMillVillager modelCloth;
  
  private final RenderLivingBase<MillVillager> renderer;
  
  private final float alpha = 1.0F;
  
  private final float colorR = 1.0F;
  
  private final float colorG = 1.0F;
  
  private final float colorB = 1.0F;
  
  private final int layer;
  
  public LayerVillagerClothes(RenderLivingBase<MillVillager> rendererIn, ModelMillVillager modelbiped, int layer) {
    this.renderer = rendererIn;
    this.layer = layer;
    float offset = 0.1F * (layer + 1);
    if (modelbiped instanceof ModelFemaleAsymmetrical) {
      this.modelCloth = new ModelFemaleAsymmetrical(offset);
    } else if (modelbiped instanceof ModelFemaleSymmetrical) {
      this.modelCloth = new ModelFemaleSymmetrical(offset);
    } else {
      this.modelCloth = new ModelMillVillager(offset);
    } 
  }
  
  public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    renderClothLayer((MillVillager)entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
  }
  
  private void renderClothLayer(MillVillager villager, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    if (villager.getClothTexturePath(this.layer) != null) {
      this.modelCloth.setModelAttributes(this.renderer.getMainModel());
      this.modelCloth.setLivingAnimations((EntityLivingBase)villager, limbSwing, limbSwingAmount, partialTicks);
      this.renderer.bindTexture(villager.getClothTexturePath(this.layer));
      getClass();
      getClass();
      getClass();
      getClass();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.modelCloth.render((Entity)villager, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    } 
  }
  
  public boolean shouldCombineTextures() {
    return false;
  }
}
