package org.millenaire.client.render;

import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.entity.TileEntityMockBanner;

public class TESRMockBanner extends TileEntitySpecialRenderer<TileEntityMockBanner> {
  private final ModelBanner bannerModel = new ModelBanner();
  
  @Nullable
  private ResourceLocation getBannerResourceLocation(TileEntityBanner bannerObj) {
    return BannerTextures.BANNER_DESIGNS.getResourceLocation(bannerObj.getPatternResourceLocation(), bannerObj.getPatternList(), bannerObj.getColorList());
  }
  
  public void render(TileEntityMockBanner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    boolean isWorldValid = (te.getWorld() != null);
    boolean standing = (!isWorldValid || te.getBlockType() instanceof net.minecraft.block.BlockBanner.BlockBannerStanding);
    int rotation = isWorldValid ? te.getBlockMetadata() : 0;
    long j = isWorldValid ? te.getWorld().getTotalWorldTime() : 0L;
    GlStateManager.pushMatrix();
    if (standing) {
      GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
      float f1 = (rotation * 360) / 16.0F;
      GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
      this.bannerModel.bannerStand.showModel = true;
    } else {
      float f2 = 0.0F;
      if (rotation == 2)
        f2 = 180.0F; 
      if (rotation == 4)
        f2 = 90.0F; 
      if (rotation == 5)
        f2 = -90.0F; 
      GlStateManager.translate((float)x + 0.5F, (float)y - 0.16666667F, (float)z + 0.5F);
      GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
      this.bannerModel.bannerStand.showModel = false;
    } 
    BlockPos blockpos = te.getPos();
    float f3 = (blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + (float)j + partialTicks;
    this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * 3.1415927F * 0.02F)) * 3.1415927F;
    GlStateManager.enableRescaleNormal();
    ResourceLocation resourcelocation = getBannerResourceLocation((TileEntityBanner)te);
    if (resourcelocation != null) {
      bindTexture(resourcelocation);
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
      this.bannerModel.renderBanner();
      GlStateManager.popMatrix();
    } 
    GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
    GlStateManager.popMatrix();
  }
}
