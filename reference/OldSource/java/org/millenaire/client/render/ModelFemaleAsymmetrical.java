package org.millenaire.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFemaleAsymmetrical extends ModelMillVillager {
  private final ModelRenderer bipedBreast;
  
  public ModelFemaleAsymmetrical() {
    this(0.0F);
  }
  
  public ModelFemaleAsymmetrical(float f) {
    this(f, 0.0F);
  }
  
  public ModelFemaleAsymmetrical(float f, float f1) {
    this.isSneak = false;
    this.bipedHead = new ModelRenderer((ModelBase)this, 0, 0);
    this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
    this.bipedHead.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    this.bipedHeadwear = new ModelRenderer((ModelBase)this, 32, 0);
    this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
    this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    this.bipedBody = new ModelRenderer((ModelBase)this, 16, 17);
    this.bipedBody.addBox(-3.5F, 0.0F, -1.5F, 7, 12, 3, f);
    this.bipedBody.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    this.bipedBreast = new ModelRenderer((ModelBase)this, 17, 18);
    this.bipedBreast.addBox(-3.5F, 0.75F, -3.0F, 7, 4, 2, f);
    this.bipedBreast.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
    this.bipedBody.addChild(this.bipedBreast);
    this.bipedRightArm = new ModelRenderer((ModelBase)this, 36, 17);
    this.bipedRightArm.addBox(-1.5F, -2.0F, -1.5F, 3, 12, 3, f);
    this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + f1, 0.0F);
    this.bipedLeftArm = new ModelRenderer((ModelBase)this, 36, 17);
    this.bipedLeftArm.mirror = true;
    this.bipedLeftArm.addBox(-1.5F, -2.0F, -1.5F, 3, 12, 3, f);
    this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + f1, 0.0F);
    this.bipedRightLeg = new ModelRenderer((ModelBase)this, 0, 16);
    this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
    this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + f1, 0.0F);
    this.bipedLeftLeg = new ModelRenderer((ModelBase)this, 48, 16);
    this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
    this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + f1, 0.0F);
  }
  
  public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
  }
}
