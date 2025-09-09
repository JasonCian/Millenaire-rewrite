package org.millenaire.client.gui;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.ui.ContainerTrade;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.UserProfile;

public class GuiTrade extends GuiContainer {
  private static final int DONATION_BUTTON_Y = 122;
  
  private static final int DONATION_BUTTON_X = 8;
  
  private Building building;
  
  private MillVillager merchant;
  
  private final EntityPlayer player;
  
  private final UserProfile profile;
  
  private int sellingRow = 0;
  
  private int buyingRow = 0;
  
  private final ContainerTrade container;
  
  private final Method drawSlotInventory;
  
  private final Method drawItemStackInventory;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/trade.png");
  
  public GuiTrade(EntityPlayer player, Building building) {
    super((Container)new ContainerTrade(player, building));
    this.drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);
    this.drawItemStackInventory = MillCommonUtilities.getDrawItemStackInventoryMethod(this);
    this.container = (ContainerTrade)this.inventorySlots;
    this.building = building;
    this.player = player;
    this.profile = building.mw.getProfile(player);
    this.ySize = 222;
    this.xSize = 248;
    updateRows(false, 0, 0);
    updateRows(true, 0, 0);
  }
  
  public GuiTrade(EntityPlayer player, MillVillager merchant) {
    super((Container)new ContainerTrade(player, merchant));
    this.drawSlotInventory = MillCommonUtilities.getDrawSlotInventoryMethod(this);
    this.drawItemStackInventory = MillCommonUtilities.getDrawItemStackInventoryMethod(this);
    this.container = (ContainerTrade)this.inventorySlots;
    this.merchant = merchant;
    this.player = player;
    this.profile = merchant.mw.getProfile(player);
    this.ySize = 222;
    this.xSize = 248;
    updateRows(false, 0, 0);
    updateRows(true, 0, 0);
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button instanceof GuiText.MillGuiButton) {
      Culture culture;
      if (this.building != null) {
        culture = this.building.culture;
      } else {
        culture = this.merchant.getCulture();
      } 
      GuiTravelBook guiTravelBook = new GuiTravelBook((EntityPlayer)(Minecraft.getMinecraft()).player);
      guiTravelBook.setCallingScreen((GuiScreen)this);
      guiTravelBook.jumpToDetails(culture, GuiText.GuiButtonReference.RefType.CULTURE, null, false);
      Minecraft.getMinecraft().displayGuiScreen((GuiScreen)guiTravelBook);
      return;
    } 
    super.actionPerformed(button);
  }
  
  protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(this.background);
    int x = (this.width - this.xSize) / 2;
    int y = (this.height - this.ySize) / 2;
    drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    if (this.sellingRow == 0)
      drawTexturedModalRect(x + 216, y + 68, 5, 5, 11, 7); 
    if (this.buyingRow == 0)
      drawTexturedModalRect(x + 216, y + 122, 5, 5, 11, 7); 
    if (this.sellingRow >= this.container.nbRowSelling - 2)
      drawTexturedModalRect(x + 230, y + 68, 5, 5, 11, 7); 
    if (this.buyingRow >= this.container.nbRowBuying - 2)
      drawTexturedModalRect(x + 230, y + 122, 5, 5, 11, 7); 
    if (!this.profile.donationActivated) {
      drawTexturedModalRect(x + 8, y + 122, 0, 238, 16, 16);
      drawTexturedModalRect(x + 8 + 16, y + 122, 16, 222, 16, 16);
    } else {
      drawTexturedModalRect(x + 8, y + 122, 0, 222, 16, 16);
      drawTexturedModalRect(x + 8 + 16, y + 122, 16, 238, 16, 16);
    } 
  }
  
  protected void drawGuiContainerForegroundLayer(int x, int y) {
    if (this.building != null) {
      this.fontRenderer.drawString(this.building.getNativeBuildingName(), 8, 6, 4210752);
      this.fontRenderer.drawString(LanguageUtilities.string("ui.wesell") + ":", 8, 22, 4210752);
      this.fontRenderer.drawString(LanguageUtilities.string("ui.webuy") + ":", 8, 76, 4210752);
    } else {
      this.fontRenderer.drawString(this.merchant.getName() + ": " + this.merchant.getNativeOccupationName(), 8, 6, 4210752);
      this.fontRenderer.drawString(LanguageUtilities.string("ui.isell") + ":", 8, 22, 4210752);
    } 
    this.fontRenderer.drawString(LanguageUtilities.string("ui.inventory"), 44, this.ySize - 96 + 2, 4210752);
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    for (int m = 0; m < this.buttonList.size(); m++)
      ((GuiButton)this.buttonList.get(m)).drawButton(this.mc, mouseX, mouseY, partialTicks); 
    for (int j = 0; j < this.labelList.size(); j++)
      ((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY); 
    int i = this.guiLeft;
    int n = this.guiTop;
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.pushMatrix();
    GlStateManager.translate(i, n, 0.0F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableRescaleNormal();
    Slot hoveredSlot = null;
    String currentProblemString = null;
    int k = 240;
    int l = 240;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); i1++) {
      Slot slot = this.inventorySlots.inventorySlots.get(i1);
      if (slot.isEnabled())
        try {
          this.drawSlotInventory.invoke(this, new Object[] { slot });
        } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
          MillLog.printException(e);
        }  
      String problem = null;
      if (slot instanceof ContainerTrade.TradeSlot) {
        ContainerTrade.TradeSlot tslot = (ContainerTrade.TradeSlot)slot;
        problem = tslot.isProblem();
        if (problem != null) {
          GlStateManager.disableLighting();
          GlStateManager.disableDepth();
          int j1 = slot.xPos;
          int l1 = slot.yPos;
          drawGradientRect(j1, l1, j1 + 16, l1 + 16, -2147483648, -2147483648);
          GlStateManager.enableLighting();
          GlStateManager.enableDepth();
        } 
      } else if (slot instanceof ContainerTrade.MerchantSlot) {
        ContainerTrade.MerchantSlot tslot = (ContainerTrade.MerchantSlot)slot;
        problem = tslot.isProblem();
        if (problem != null) {
          GlStateManager.disableLighting();
          GlStateManager.disableDepth();
          int j1 = slot.xPos;
          int l1 = slot.yPos;
          drawGradientRect(j1, l1, j1 + 16, l1 + 16, -2147483648, -2147483648);
          GlStateManager.enableLighting();
          GlStateManager.enableDepth();
        } 
      } 
      if (getIsMouseOverSlot(slot, mouseX, mouseY)) {
        hoveredSlot = slot;
        currentProblemString = problem;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int j1 = slot.xPos;
        int k1 = slot.yPos;
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
        this.drawItemStackInventory.invoke(this, new Object[] { inventoryplayer.getItemStack(), Integer.valueOf(i - 240 - 8), Integer.valueOf(n - 240 - 8), currentProblemString });
      } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
        MillLog.printException(e);
      }  
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    RenderHelper.enableStandardItemLighting();
    if (inventoryplayer.getItemStack().getItem() == Items.AIR && hoveredSlot != null && hoveredSlot.getHasStack())
      if (hoveredSlot instanceof ContainerTrade.TradeSlot) {
        ContainerTrade.TradeSlot tslot = (ContainerTrade.TradeSlot)hoveredSlot;
        int price = 0;
        String priceText = null;
        int priceColour = 0;
        if (tslot.sellingSlot) {
          price = tslot.good.getCalculatedSellingPrice(this.building, this.player);
        } else {
          price = tslot.good.getCalculatedBuyingPrice(this.building, this.player);
        } 
        priceText = MillCommonUtilities.getShortPrice(price);
        priceColour = MillCommonUtilities.getPriceColourMC(price);
        ItemStack itemstack = hoveredSlot.getStack();
        try {
          List<String> list = itemstack.getTooltip((EntityPlayer)this.mc.player, this.mc.gameSettings.advancedItemTooltips ? (ITooltipFlag)ITooltipFlag.TooltipFlags.ADVANCED : (ITooltipFlag)ITooltipFlag.TooltipFlags.NORMAL);
          if (((ContainerTrade.TradeSlot)hoveredSlot).sellingSlot || !this.profile.donationActivated) {
            list.add("§" + Integer.toHexString(priceColour) + priceText);
            list.add(LanguageUtilities.string("ui.repgain", new String[] { "" + price }));
          } else {
            list.add("§6" + LanguageUtilities.string("ui.donatinggoods"));
            list.add(LanguageUtilities.string("ui.repgain", new String[] { "" + (price * 4) }));
          } 
          if (currentProblemString != null)
            list.add("§4" + currentProblemString); 
          renderToolTipCustom(itemstack, mouseX, mouseY, list);
        } catch (Exception e) {
          MillLog.printException("Exception when rendering tooltip for stack: " + itemstack, e);
        } 
      } else if (hoveredSlot instanceof ContainerTrade.MerchantSlot) {
        ContainerTrade.MerchantSlot tslot = (ContainerTrade.MerchantSlot)hoveredSlot;
        String price = MillCommonUtilities.getShortPrice(tslot.good.getCalculatedSellingPrice(this.merchant));
        int priceColour = MillCommonUtilities.getPriceColourMC(tslot.good.getCalculatedSellingPrice(this.merchant));
        ItemStack itemstack = hoveredSlot.getStack();
        List<String> list = itemstack.getTooltip((EntityPlayer)this.mc.player, this.mc.gameSettings.advancedItemTooltips ? (ITooltipFlag)ITooltipFlag.TooltipFlags.ADVANCED : (ITooltipFlag)ITooltipFlag.TooltipFlags.NORMAL);
        list.add("§" + Integer.toHexString(priceColour) + price);
        if (currentProblemString != null)
          list.add("§4" + currentProblemString); 
        renderToolTipCustom(itemstack, mouseX, mouseY, list);
      } else {
        ItemStack itemstack = hoveredSlot.getStack();
        renderToolTip(itemstack, mouseX, mouseY);
      }  
    int startx = (this.width - this.xSize) / 2;
    int starty = (this.height - this.ySize) / 2;
    int dx = mouseX - startx;
    int dy = mouseY - starty;
    if (dy >= 122 && dy <= 138)
      if (dx >= 8 && dx <= 24) {
        String toolTip = LanguageUtilities.string("ui.trade_buying");
        int stringlength = this.fontRenderer.getStringWidth(toolTip);
        drawGradientRect(mouseX + 5, mouseY - 3, mouseX + stringlength + 10, mouseY + 8 + 3, -1073741824, -1073741824);
        this.fontRenderer.drawString(toolTip, mouseX + 8, mouseY, 16777215);
      } else if (dx >= 24 && dx <= 40) {
        String toolTip = LanguageUtilities.string("ui.trade_donation");
        int stringlength = this.fontRenderer.getStringWidth(toolTip);
        drawGradientRect(mouseX + 5, mouseY - 3, mouseX + stringlength + 10, mouseY + 8 + 3, -1073741824, -1073741824);
        this.fontRenderer.drawString(toolTip, mouseX + 8, mouseY, 16777215);
      }  
    renderHoveredToolTip(mouseX, mouseY);
  }
  
  private boolean getIsMouseOverSlot(Slot slot, int i, int j) {
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    i -= k;
    j -= l;
    return (i >= slot.xPos - 1 && i < slot.xPos + 16 + 1 && j >= slot.yPos - 1 && j < slot.yPos + 16 + 1);
  }
  
  protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
    if (slotIn != null && !(slotIn instanceof ContainerTrade.TradeSlot) && !(slotIn instanceof ContainerTrade.MerchantSlot))
      return; 
    if (slotIn == null || slotIn.getStack() == null)
      return; 
    super.handleMouseClick(slotIn, slotId, mouseButton, type);
  }
  
  public void initGui() {
    super.initGui();
    if (this.building != null) {
      int xStart = (this.width - this.xSize) / 2;
      int yStart = (this.height - this.ySize) / 2;
      this.buttonList.add(new GuiText.MillGuiButton(0, xStart + this.xSize - 21, yStart + 5, 15, 20, "?"));
    } 
  }
  
  protected void mouseClicked(int x, int y, int clickType) throws IOException {
    if (clickType == 0) {
      int startx = (this.width - this.xSize) / 2;
      int starty = (this.height - this.ySize) / 2;
      int dx = x - startx;
      int dy = y - starty;
      if (dy >= 68 && dy <= 74) {
        if (dx >= 216 && dx <= 226) {
          if (this.sellingRow > 0) {
            this.sellingRow--;
            updateRows(true, 1, this.sellingRow);
          } 
        } else if (dx >= 230 && dx <= 240 && 
          this.sellingRow < this.container.nbRowSelling - 2) {
          this.sellingRow++;
          updateRows(true, -1, this.sellingRow);
        } 
      } else if (dy >= 122 && dy <= 127) {
        if (dx >= 216 && dx <= 226) {
          if (this.buyingRow > 0) {
            this.buyingRow--;
            updateRows(false, 1, this.buyingRow);
          } 
        } else if (dx >= 230 && dx <= 240 && 
          this.buyingRow < this.container.nbRowBuying - 2) {
          this.buyingRow++;
          updateRows(false, -1, this.buyingRow);
        } 
      } 
      if (dy >= 122 && dy <= 138)
        if (dx >= 8 && dx <= 24) {
          if (this.profile.donationActivated) {
            this.profile.donationActivated = false;
            ClientSender.playerToggleDonation(this.player, this.profile.donationActivated);
          } 
        } else if (dx >= 24 && dx <= 40 && 
          !this.profile.donationActivated) {
          this.profile.donationActivated = true;
          ClientSender.playerToggleDonation(this.player, this.profile.donationActivated);
        }  
    } 
    super.mouseClicked(x, y, clickType);
  }
  
  protected void renderToolTipCustom(ItemStack stack, int x, int y, List<String> customToolTip) {
    FontRenderer font = stack.getItem().getFontRenderer(stack);
    GuiUtils.preItemToolTip(stack);
    drawHoveringText(customToolTip, x, y, (font == null) ? this.fontRenderer : font);
    GuiUtils.postItemToolTip();
  }
  
  private void updateRows(boolean selling, int change, int row) {
    int pos = 0;
    for (Object o : this.container.inventorySlots) {
      Slot slot = (Slot)o;
      if (slot instanceof ContainerTrade.TradeSlot) {
        ContainerTrade.TradeSlot tradeSlot = (ContainerTrade.TradeSlot)slot;
        if (tradeSlot.sellingSlot == selling) {
          tradeSlot.yPos += 18 * change;
          if (pos / 13 < row || pos / 13 > row + 1) {
            if (tradeSlot.xPos > 0)
              tradeSlot.xPos -= 1000; 
          } else if (tradeSlot.xPos < 0) {
            tradeSlot.xPos += 1000;
          } 
          pos++;
        } 
        continue;
      } 
      if (slot instanceof ContainerTrade.MerchantSlot && selling) {
        ContainerTrade.MerchantSlot merchantSlot = (ContainerTrade.MerchantSlot)slot;
        merchantSlot.yPos += 18 * change;
        if (pos / 13 < row || pos / 13 > row + 1) {
          if (merchantSlot.xPos > 0)
            merchantSlot.xPos -= 1000; 
        } else if (merchantSlot.xPos < 0) {
          merchantSlot.xPos += 1000;
        } 
        pos++;
      } 
    } 
  }
}
