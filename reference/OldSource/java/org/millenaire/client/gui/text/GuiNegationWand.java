package org.millenaire.client.gui.text;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextPage;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.village.Building;

public class GuiNegationWand extends GuiText {
  private final Building th;
  
  private final EntityPlayer player;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");
  
  public GuiNegationWand(EntityPlayer player, Building th) {
    this.th = th;
    this.player = player;
    this.bookManager = new BookManager(256, 220, 160, 240, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (guibutton instanceof GuiText.MillGuiButton) {
      if (guibutton.id == 0)
        ClientSender.negationWand(this.player, this.th); 
      this.mc.displayGuiScreen(null);
      this.mc.setIngameFocus();
    } 
    super.actionPerformed(guibutton);
  }
  
  public void buttonPagination() {
    super.buttonPagination();
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    this.buttonList.add(new GuiText.MillGuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("negationwand.cancel")));
    this.buttonList.add(new GuiText.MillGuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("negationwand.confirm")));
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    this.textBook = new TextBook();
    TextPage page = new TextPage();
    page.addLine(LanguageUtilities.string("negationwand.confirmmessage", new String[] { this.th.villageType.name }));
    this.textBook.addPage(page);
    this.textBook = this.bookManager.adjustTextBookLineLength(this.textBook);
    this.pageNum = 0;
  }
}
