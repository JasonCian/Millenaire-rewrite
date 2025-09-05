package org.millenaire.client.gui;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.ui.ContainerPuja;
import org.millenaire.common.ui.PujaSacrifice;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class GuiPujas extends GuiContainer {
  private static final ResourceLocation texturePujas = new ResourceLocation("millenaire", "textures/gui/pujas.png");
  
  private static final ResourceLocation textureSacrifices = new ResourceLocation("millenaire", "textures/gui/mayansacrifices.png");
  
  private final Building temple;
  
  private final EntityPlayer player;
  
  private final Method drawSlotInventory;
  
  private final Method drawItemStackInventory;
  
  public GuiPujas(EntityPlayer player, Building temple) {
    super((Container)new ContainerPuja(player, temple));
    this.ySize = 188;
    this.temple = temple;
    this.player = player;
    if (MillConfigValues.LogPujas >= 3)
      MillLog.debug(this, "Opening shrine GUI"); 
    this.drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);
    this.drawItemStackInventory = MillCommonUtilities.getDrawItemStackInventoryMethod(this);
  }
  
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    if (this.temple.pujas != null && this.temple.pujas.type == 1) {
      this.mc.getTextureManager().bindTexture(textureSacrifices);
    } else {
      this.mc.getTextureManager().bindTexture(texturePujas);
    } 
    int x = (this.width - this.xSize) / 2;
    int y = (this.height - this.ySize) / 2;
    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    if (this.temple.pujas != null) {
      int linePos = 0;
      int colPos = 0;
      for (int cp = 0; cp < this.temple.pujas.getTargets().size(); cp++) {
        if (this.temple.pujas.currentTarget == this.temple.pujas.getTargets().get(cp)) {
          drawTexturedModalRect(x + getTargetXStart() + colPos * getButtonWidth(), y + 
              getTargetYStart() + getButtonHeight() * linePos, ((PujaSacrifice.PrayerTarget)this.temple.pujas
              .getTargets().get(cp)).startXact, ((PujaSacrifice.PrayerTarget)this.temple.pujas
              .getTargets().get(cp)).startYact, getButtonWidth(), getButtonHeight());
        } else {
          drawTexturedModalRect(x + getTargetXStart() + colPos * getButtonWidth(), y + 
              getTargetYStart() + getButtonHeight() * linePos, ((PujaSacrifice.PrayerTarget)this.temple.pujas
              .getTargets().get(cp)).startX, ((PujaSacrifice.PrayerTarget)this.temple.pujas.getTargets().get(cp)).startY, 
              getButtonWidth(), getButtonHeight());
        } 
        colPos++;
        if (colPos >= getNbPerLines()) {
          colPos = 0;
          linePos++;
        } 
      } 
      int progress = this.temple.pujas.getPujaProgressScaled(13);
      drawTexturedModalRect(x + 27, y + 39 + 13 - progress, 176, 13 - progress, 15, progress);
      progress = this.temple.pujas.getOfferingProgressScaled(16);
      drawTexturedModalRect(x + 84, y + 63 + 16 - progress, 176, 47 - progress, 19, progress);
    } 
  }
  
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    if (this.temple.pujas.type == 1) {
      this.fontRenderer.drawString(LanguageUtilities.string("sacrifices.offering"), 8, 6, 4210752);
      this.fontRenderer.drawString(LanguageUtilities.string("sacrifices.panditfee"), 8, 75, 4210752);
    } else {
      this.fontRenderer.drawString(LanguageUtilities.string("pujas.offering"), 8, 6, 4210752);
      this.fontRenderer.drawString(LanguageUtilities.string("pujas.panditfee"), 8, 75, 4210752);
    } 
    this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 94 + 2, 4210752);
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    int i = this.guiLeft;
    int j = this.guiTop;
    drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    try {
      for (int m = 0; m < this.buttonList.size(); m++) {
        GuiButton guibutton = this.buttonList.get(m);
        guibutton.drawButton(this.mc, i, j, partialTicks);
      } 
    } catch (Exception e) {
      MillLog.printException("Exception in button rendering: ", e);
    } 
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.pushMatrix();
    GlStateManager.translate(i, j, 0.0F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableRescaleNormal();
    int k = 240;
    int l = 240;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    Slot hoveredSlot = null;
    for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); i1++) {
      Slot slot1 = this.inventorySlots.inventorySlots.get(i1);
      drawSlotInventory(slot1);
      if (getIsMouseOverSlot(slot1, mouseX, mouseY)) {
        hoveredSlot = slot1;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int j1 = hoveredSlot.xPos;
        int k1 = hoveredSlot.yPos;
        GlStateManager.colorMask(true, true, true, false);
        drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
      } 
    } 
    RenderHelper.disableStandardItemLighting();
    drawGuiContainerForegroundLayer(mouseX, mouseY);
    RenderHelper.enableGUIStandardItemLighting();
    MinecraftForge.EVENT_BUS.post((Event)new GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
    InventoryPlayer inventoryplayer = this.mc.player.inventory;
    if (inventoryplayer.getItemStack() != null)
      try {
        this.drawItemStackInventory.invoke(this, new Object[] { inventoryplayer.getItemStack(), Integer.valueOf(i - 240 - 8), Integer.valueOf(j - 240 - 8), null });
      } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
        MillLog.printException(e);
      }  
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    RenderHelper.enableStandardItemLighting();
    if (inventoryplayer.getItemStack().isEmpty() && hoveredSlot != null) {
      List<String> list = null;
      ItemStack itemstack = null;
      if (hoveredSlot.getHasStack()) {
        itemstack = hoveredSlot.getStack();
        list = itemstack.getTooltip((EntityPlayer)this.mc.player, this.mc.gameSettings.advancedItemTooltips ? (ITooltipFlag)ITooltipFlag.TooltipFlags.ADVANCED : (ITooltipFlag)ITooltipFlag.TooltipFlags.NORMAL);
        renderToolTipCustom(itemstack, mouseX, mouseY, list);
      } else if (hoveredSlot instanceof ContainerPuja.OfferingSlot) {
        list = new ArrayList<>();
        list.add("§6" + LanguageUtilities.string("pujas.offeringslot"));
        list.add("§7" + LanguageUtilities.string("pujas.offeringslot2"));
        renderToolTipCustom(itemstack, mouseX, mouseY, list);
      } else if (hoveredSlot instanceof ContainerPuja.MoneySlot) {
        list = new ArrayList<>();
        list.add("§6" + LanguageUtilities.string("pujas.moneyslot"));
        renderToolTipCustom(itemstack, mouseX, mouseY, list);
      } else if (hoveredSlot instanceof ContainerPuja.ToolSlot) {
        list = new ArrayList<>();
        list.add("§6" + LanguageUtilities.string("pujas.toolslot"));
        renderToolTipCustom(itemstack, mouseX, mouseY, list);
      } 
    } 
    int startx = (this.width - this.xSize) / 2;
    int starty = (this.height - this.ySize) / 2;
    if (this.temple.pujas != null) {
      int linePos = 0;
      int colPos = 0;
      for (int cp = 0; cp < this.temple.pujas.getTargets().size(); cp++) {
        if (mouseX > startx + getTargetXStart() + colPos * getButtonWidth() && mouseX < startx + 
          getTargetXStart() + (colPos + 1) * getButtonWidth() && mouseY > starty + 
          getTargetYStart() + getButtonHeight() * linePos && mouseY < starty + 
          getTargetYStart() + getButtonHeight() * (linePos + 1)) {
          String s = LanguageUtilities.string(((PujaSacrifice.PrayerTarget)this.temple.pujas.getTargets().get(cp)).mouseOver);
          int stringlength = this.fontRenderer.getStringWidth(s);
          drawGradientRect(mouseX + 5, mouseY - 3, mouseX + stringlength + 10, mouseY + 8 + 3, -1073741824, -1073741824);
          this.fontRenderer.drawString(s, mouseX + 8, mouseY, 15790320);
        } 
        colPos++;
        if (colPos >= getNbPerLines()) {
          colPos = 0;
          linePos++;
        } 
      } 
    } 
  }
  
  public void drawSlotInventory(Slot slot) {
    try {
      this.drawSlotInventory.invoke(this, new Object[] { slot });
    } catch (Exception e) {
      MillLog.printException("Exception when trying to access drawSlotInventory", e);
    } 
  }
  
  private int getButtonHeight() {
    if (this.temple.pujas == null)
      return 0; 
    if (this.temple.pujas.type == 0)
      return 17; 
    if (this.temple.pujas.type == 1)
      return 20; 
    return 0;
  }
  
  private int getButtonWidth() {
    if (this.temple.pujas == null)
      return 0; 
    if (this.temple.pujas.type == 0)
      return 46; 
    if (this.temple.pujas.type == 1)
      return 20; 
    return 0;
  }
  
  private boolean getIsMouseOverSlot(Slot slot, int i, int j) {
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    i -= k;
    j -= l;
    return (i >= slot.xPos - 1 && i < slot.xPos + 16 + 1 && j >= slot.yPos - 1 && j < slot.yPos + 16 + 1);
  }
  
  private int getNbPerLines() {
    if (this.temple.pujas == null)
      return 1; 
    if (this.temple.pujas.type == 0)
      return 1; 
    if (this.temple.pujas.type == 1)
      return 3; 
    return 1;
  }
  
  private int getTargetXStart() {
    if (this.temple.pujas == null)
      return 0; 
    if (this.temple.pujas.type == 0)
      return 118; 
    if (this.temple.pujas.type == 1)
      return 110; 
    return 0;
  }
  
  private int getTargetYStart() {
    if (this.temple.pujas == null)
      return 0; 
    if (this.temple.pujas.type == 0)
      return 22; 
    if (this.temple.pujas.type == 1)
      return 22; 
    return 0;
  }
  
  protected void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);
    int startx = (this.width - this.xSize) / 2;
    int starty = (this.height - this.ySize) / 2;
    if (this.temple.pujas != null) {
      int linePos = 0;
      int colPos = 0;
      for (int cp = 0; cp < this.temple.pujas.getTargets().size(); cp++) {
        if (x > startx + getTargetXStart() + colPos * getButtonWidth() && x < startx + 
          getTargetXStart() + (colPos + 1) * getButtonWidth() && y > starty + 
          getTargetYStart() + getButtonHeight() * linePos && y < starty + 
          getTargetYStart() + getButtonHeight() * (linePos + 1))
          ClientSender.pujasChangeEnchantment(this.player, this.temple, cp); 
        colPos++;
        if (colPos >= getNbPerLines()) {
          colPos = 0;
          linePos++;
        } 
      } 
    } 
  }
  
  protected void renderToolTipCustom(ItemStack stack, int x, int y, List<String> customToolTip) {
    if (stack == null)
      stack = ItemStack.EMPTY; 
    FontRenderer font = stack.getItem().getFontRenderer(stack);
    GuiUtils.preItemToolTip(stack);
    drawHoveringText(customToolTip, x, y, (font == null) ? this.fontRenderer : font);
    GuiUtils.postItemToolTip();
  }
}
