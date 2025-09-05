package org.millenaire.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.entity.EntityWallDecoration;

@SideOnly(Side.CLIENT)
public class RenderWallDecoration extends Render<EntityWallDecoration> {
  public static class FactoryRenderWallDecoration implements IRenderFactory<EntityWallDecoration> {
    public Render<? super EntityWallDecoration> createRenderFor(RenderManager manager) {
      return new RenderWallDecoration(manager);
    }
  }
  
  public static final FactoryRenderWallDecoration FACTORY_WALL_DECORATION = new FactoryRenderWallDecoration();
  
  public static final ResourceLocation textureTapestries = new ResourceLocation("millenaire", "textures/painting/tapestry.png");
  
  public static final ResourceLocation textureSculptures = new ResourceLocation("millenaire", "textures/painting/sculptures.png");
  
  protected RenderWallDecoration(RenderManager renderManager) {
    super(renderManager);
  }
  
  public void doRender(EntityWallDecoration entity, double x, double y, double z, float entityYaw, float partialTicks) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
    GlStateManager.enableRescaleNormal();
    bindEntityTexture(entity);
    EntityWallDecoration.EnumWallDecoration enumart = entity.millArt;
    GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);
    if (this.renderOutlines) {
      GlStateManager.enableColorMaterial();
      GlStateManager.enableOutlineMode(getTeamColor(entity));
    } 
    renderPainting(entity, enumart.sizeX, enumart.sizeY, enumart.offsetX, enumart.offsetY);
    if (this.renderOutlines) {
      GlStateManager.disableOutlineMode();
      GlStateManager.disableColorMaterial();
    } 
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
  }
  
  protected ResourceLocation getEntityTexture(EntityWallDecoration entity) {
    if (entity.type == 1)
      return textureTapestries; 
    return textureSculptures;
  }
  
  private void renderPainting(EntityWallDecoration painting, int width, int height, int textureU, int textureV) {
    float f = -width / 2.0F;
    float f1 = -height / 2.0F;
    for (int i = 0; i < width / 16; i++) {
      for (int j = 0; j < height / 16; j++) {
        float f15 = f + ((i + 1) * 16);
        float f16 = f + (i * 16);
        float f17 = f1 + ((j + 1) * 16);
        float f18 = f1 + (j * 16);
        setLightmap(painting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
        float f19 = (textureU + width - i * 16) / 256.0F;
        float f20 = (textureU + width - (i + 1) * 16) / 256.0F;
        float f21 = (textureV + height - j * 16) / 256.0F;
        float f22 = (textureV + height - (j + 1) * 16) / 256.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(f15, f18, -0.5D).tex(f20, f21).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(f16, f18, -0.5D).tex(f19, f21).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(f16, f17, -0.5D).tex(f19, f22).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(f15, f17, -0.5D).tex(f20, f22).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(f15, f17, 0.5D).tex(0.75D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(f16, f17, 0.5D).tex(0.8125D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(f16, f18, 0.5D).tex(0.8125D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(f15, f18, 0.5D).tex(0.75D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(f15, f17, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f17, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f17, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f17, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f18, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f18, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f18, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f18, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f17, 0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f18, 0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f18, -0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f15, f17, -0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f17, -0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f18, -0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f18, 0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(f16, f17, 0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
        tessellator.draw();
      } 
    } 
  }
  
  private void setLightmap(EntityWallDecoration painting, float p_77008_2_, float p_77008_3_) {
    int i = MathHelper.floor(painting.posX);
    int j = MathHelper.floor(painting.posY + (p_77008_3_ / 16.0F));
    int k = MathHelper.floor(painting.posZ);
    EnumFacing enumfacing = painting.facingDirection;
    if (enumfacing == EnumFacing.NORTH)
      i = MathHelper.floor(painting.posX + (p_77008_2_ / 16.0F)); 
    if (enumfacing == EnumFacing.WEST)
      k = MathHelper.floor(painting.posZ - (p_77008_2_ / 16.0F)); 
    if (enumfacing == EnumFacing.SOUTH)
      i = MathHelper.floor(painting.posX - (p_77008_2_ / 16.0F)); 
    if (enumfacing == EnumFacing.EAST)
      k = MathHelper.floor(painting.posZ + (p_77008_2_ / 16.0F)); 
    int l = this.renderManager.world.getCombinedLight(new BlockPos(i, j, k), 0);
    int i1 = l % 65536;
    int j1 = l / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1, j1);
    GlStateManager.color(1.0F, 1.0F, 1.0F);
  }
}
