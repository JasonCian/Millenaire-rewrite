package org.millenaire.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import org.millenaire.common.entity.MillVillager;

public class ModelMillVillager extends ModelBiped {
  public ModelMillVillager() {
    this(0.0F);
  }
  
  public ModelMillVillager(float f) {
    super(f);
  }
  
  public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    MillVillager villager = (MillVillager)entityIn;
    if (villager.travelBookMockVillager && villager.heldItem != null) {
      this.bipedLeftArm.rotateAngleX = -0.6F;
      this.bipedLeftArm.rotateAngleZ = -0.2F;
    } 
    if (villager.travelBookMockVillager && villager.heldItemOffHand != null) {
      this.bipedRightArm.rotateAngleX = -0.5F;
      this.bipedRightArm.rotateAngleZ = 0.1F;
    } 
  }
}
