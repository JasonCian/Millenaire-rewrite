package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.forge.ClientProxy;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;

public class GuiHire extends GuiText {
  private static final int REPUTATION_NEEDED = 4096;
  
  public static final int BUTTON_CLOSE = 0;
  
  public static final int BUTTON_HIRE = 1;
  
  public static final int BUTTON_EXTEND = 2;
  
  public static final int BUTTON_RELEASE = 3;
  
  private final MillVillager villager;
  
  private final EntityPlayer player;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");
  
  public GuiHire(EntityPlayer player, MillVillager villager) {
    this.villager = villager;
    this.player = player;
    this.bookManager = new BookManager(256, 220, 160, 240, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (guibutton instanceof GuiText.MillGuiButton)
      if (guibutton.id == 0) {
        this.mc.displayGuiScreen(null);
        this.mc.setIngameFocus();
      } else if (guibutton.id == 1) {
        ClientSender.hireHire(this.player, this.villager);
        refreshContent();
      } else if (guibutton.id == 2) {
        ClientSender.hireExtend(this.player, this.villager);
        refreshContent();
      } else if (guibutton.id == 3) {
        ClientSender.hireRelease(this.player, this.villager);
        refreshContent();
      }  
    super.actionPerformed(guibutton);
  }
  
  public void buttonPagination() {
    super.buttonPagination();
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    if (this.villager.hiredBy != null) {
      if (MillCommonUtilities.countMoney((IInventory)this.player.inventory) >= this.villager.getHireCost(this.player))
        this.buttonList.add(new GuiText.MillGuiButton(2, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 63, 20, LanguageUtilities.string("hire.extend"))); 
      this.buttonList.add(new GuiText.MillGuiButton(3, xStart + getXSize() / 2 - 32, yStart + getYSize() - 40, 64, 20, LanguageUtilities.string("hire.release")));
      this.buttonList.add(new GuiText.MillGuiButton(0, xStart + getXSize() / 2 + 37, yStart + getYSize() - 40, 63, 20, LanguageUtilities.string("hire.close")));
    } else {
      if (this.villager.getTownHall().getReputation(this.player) >= 4096 && MillCommonUtilities.countMoney((IInventory)this.player.inventory) >= this.villager.getHireCost(this.player))
        this.buttonList.add(new GuiText.MillGuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("hire.hire"))); 
      this.buttonList.add(new GuiText.MillGuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("hire.close")));
    } 
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  private TextBook getData() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(this.villager.getName() + ", " + this.villager.getNativeOccupationName(), "§1", new GuiText.GuiButtonReference(this.villager.vtype)));
    text.add(new TextLine());
    if (this.villager.hiredBy != null) {
      text.add(new TextLine(LanguageUtilities.string("hire.hiredvillager", new String[] { "" + Math.round((float)((this.villager.hiredUntil - this.villager.world.getWorldTime()) / 1000L)), 
                Keyboard.getKeyName(ClientProxy.KB_ESCORTS.getKeyCode()) })));
    } else if (this.villager.getTownHall().getReputation(this.player) >= 4096) {
      text.add(new TextLine(LanguageUtilities.string("hire.hireablevillager")));
    } else {
      text.add(new TextLine(LanguageUtilities.string("hire.hireablevillagernoreputation")));
    } 
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("hire.health") + ": " + (this.villager.getHealth() * 0.5D) + "/" + (this.villager.getMaxHealth() * 0.5D)));
    text.add(new TextLine(LanguageUtilities.string("hire.strength") + ": " + this.villager.getAttackStrength()));
    text.add(new TextLine(LanguageUtilities.string("hire.cost") + ": " + MillCommonUtilities.getShortPrice(this.villager.getHireCost(this.player))));
    List<List<TextLine>> ftext = new ArrayList<>();
    ftext.add(text);
    return this.bookManager.convertAndAdjustLines(ftext);
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    refreshContent();
  }
  
  private void refreshContent() {
    this.textBook = getData();
    buttonPagination();
  }
}
