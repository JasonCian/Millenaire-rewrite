package org.millenaire.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.ui.firepit.ContainerFirePit;

public class GuiFirePit extends GuiContainer {
  private static final ResourceLocation TEXTURE = new ResourceLocation("millenaire", "textures/gui/firepit.png");
  
  private static final int[][] ARROWS = new int[][] { { 77, 22, 23, 31, 8 }, { 71, 28, 37, 14, 16 }, { 77, 42, 23, 31, 8 } };
  
  private static final int[] FIRE = new int[] { 81, 54 };
  
  private final EntityPlayer player;
  
  private final TileEntityFirePit firePit;
  
  public GuiFirePit(EntityPlayer player, TileEntityFirePit firePit) {
    super((Container)new ContainerFirePit(player, firePit));
    this.player = player;
    this.firePit = firePit;
    this.xSize = 176;
    this.ySize = 175;
  }
  
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(TEXTURE);
    int x = (this.width - this.xSize) / 2;
    int y = (this.height - this.ySize) / 2;
    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    if (this.firePit.getBurnTime() > 0) {
      int burn = getBurnLeftScaled(13);
      drawTexturedModalRect(x + FIRE[0], y + FIRE[1] + 12 - burn, this.xSize, 12 - burn, 14, burn + 1);
    } 
    for (int i = 0; i < 3; i++) {
      int[] data = ARROWS[i];
      int arrowX = data[0];
      int arrowY = data[1];
      int arrowLen = data[2];
      int arrowTexY = data[3];
      int arrowHeight = data[4];
      int progress = getCookProgressScaled(i, arrowLen);
      drawTexturedModalRect(x + arrowX, y + arrowY, this.xSize, arrowTexY, progress, arrowHeight);
    } 
  }
  
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.fontRenderer.drawString(MillBlocks.FIRE_PIT.getName(), 8, 6, 4210752);
    this.fontRenderer.drawString(this.player.inventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    renderHoveredToolTip(mouseX, mouseY);
  }
  
  private int getBurnLeftScaled(int pixels) {
    int time = this.firePit.getTotalBurnTime();
    if (time == 0)
      time = 200; 
    return this.firePit.getBurnTime() * pixels / time;
  }
  
  private int getCookProgressScaled(int idx, int pixels) {
    int cook = this.firePit.getCookTime(idx);
    return (cook != 0) ? (cook * pixels / 200) : 0;
  }
}
