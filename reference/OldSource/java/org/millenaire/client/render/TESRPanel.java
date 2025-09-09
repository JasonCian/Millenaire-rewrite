package org.millenaire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.entity.TileEntityPanel;

@SideOnly(Side.CLIENT)
public class TESRPanel extends TileEntitySpecialRenderer<TileEntityPanel> {
  private static final ResourceLocation PANEL_TEXTURE = new ResourceLocation("millenaire", "textures/entity/panels/default.png");
  
  private final ModelPanel model = new ModelPanel();
  
  private void drawIcon(int linePos, ItemStack icon, float xTranslate) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(xTranslate, -0.7400000095367432D + linePos * 0.15D, -0.09D);
    renderItem2d(icon, 0.3F);
    GlStateManager.popMatrix();
  }
  
  public void render(TileEntityPanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    GlStateManager.pushMatrix();
    int k = te.getBlockMetadata();
    float f2 = 0.0F;
    if (k == 2)
      f2 = 180.0F; 
    if (k == 4)
      f2 = 90.0F; 
    if (k == 5)
      f2 = -90.0F; 
    GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
    GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(0.0F, 0.0F, -0.4375F);
    if (destroyStage >= 0) {
      bindTexture(DESTROY_STAGES[destroyStage]);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scale(4.0F, 2.0F, 1.0F);
      GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
      GlStateManager.matrixMode(5888);
    } else {
      ResourceLocation texture = (te.texture != null) ? te.texture : PANEL_TEXTURE;
      bindTexture(texture);
    } 
    GlStateManager.enableRescaleNormal();
    GlStateManager.pushMatrix();
    GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
    this.model.renderSign();
    GlStateManager.translate(0.0D, 0.24D, 0.0D);
    te.translateLines(getFontRenderer());
    for (int pos = 0; pos < te.displayLines.size(); pos++) {
      TileEntityPanel.PanelDisplayLine line = te.displayLines.get(pos);
      drawIcon(pos, line.leftIcon, -0.54F);
      drawIcon(pos, line.middleIcon, 0.08F);
      drawIcon(pos, line.rightIcon, 0.54F);
    } 
    GlStateManager.popMatrix();
    FontRenderer fontrenderer = getFontRenderer();
    GlStateManager.translate(0.0D, 0.25D, 0.046666666865348816D);
    GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
    GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
    GlStateManager.depthMask(false);
    if (destroyStage < 0)
      for (int i = 0; i < te.displayLines.size(); i++) {
        TileEntityPanel.PanelDisplayLine line = te.displayLines.get(i);
        if (line.centerLine) {
          fontrenderer.drawString(line.fullLine, -fontrenderer.getStringWidth(line.fullLine) / 2, i * 10 - 15, 0);
        } else {
          fontrenderer.drawString(line.fullLine, -29, i * 10 - 15, 0);
        } 
        fontrenderer.drawString(line.leftColumn, -29, i * 10 - 15, 0);
        fontrenderer.drawString(line.rightColumn, 11, i * 10 - 15, 0);
      }  
    GlStateManager.depthMask(true);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
    if (destroyStage >= 0) {
      GlStateManager.matrixMode(5890);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
    } 
  }
  
  private void renderItem2d(ItemStack itemStack, float scale) {
    if (!itemStack.isEmpty()) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(scale / 32.0F, scale / 32.0F, -1.0E-4F);
      GlStateManager.translate(-8.0F, -11.0F, -420.0F);
      RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
      renderItem.renderItemAndEffectIntoGUI(itemStack, 0, 0);
      GlStateManager.popMatrix();
    } 
  }
  
  private void renderItem3d(ItemStack itemstack) {
    if (!itemstack.isEmpty()) {
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
      if (itemstack.getItem() instanceof net.minecraft.item.ItemBlock) {
        GlStateManager.scale(0.25D, 0.25D, 0.25D);
      } else {
        GlStateManager.scale(-0.15D, -0.15D, 0.15D);
      } 
      GlStateManager.pushAttrib();
      RenderHelper.enableStandardItemLighting();
      Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popAttrib();
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
    } 
  }
}
