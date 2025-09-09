package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;
import org.millenaire.common.utilities.LanguageUtilities;

public class GuiHelp extends GuiText {
  public static final int NB_CHAPTERS = 13;
  
  int helpDisplayed = 1;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/help.png");
  
  public GuiHelp() {
    this.bookManager = new BookManager(256, 224, 200, 180, 36, new GuiText.FontRendererGUIWrapper(this));
  }
  
  public TextBook convertAdjustHelpText(List<List<String>> baseText) {
    TextBook adjustedBook = new TextBook();
    for (List<String> page : baseText) {
      TextPage newPage = new TextPage();
      for (String s : page)
        newPage.addLine(new TextLine(s, true)); 
      adjustedBook.addPage(newPage);
    } 
    return this.bookManager.adjustTextBookLineLength(adjustedBook);
  }
  
  protected void customDrawBackground(int mouseX, int mouseY, float f) {
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    GL11.glDisable(2896);
    GL11.glDisable(2929);
    for (int i = 0; i < 7; i++) {
      if (this.helpDisplayed - 1 != i) {
        int extraFirstRow = (i == 0) ? 1 : 0;
        drawGradientRect(xStart, yStart - extraFirstRow + 32 * i + 1, xStart + 32, yStart + 32 * i + 32, -1610612736, -1610612736);
      } 
      if (this.helpDisplayed - 8 != i) {
        int extraFirstRow = (i == 0) ? 1 : 0;
        drawGradientRect(xStart + 224, yStart - extraFirstRow + 32 * i + 1, xStart + 32 + 224, yStart + 32 * i + 32, -1610612736, -1610612736);
      } 
    } 
    GL11.glEnable(2896);
    GL11.glEnable(2929);
  }
  
  protected void customDrawScreen(int mouseX, int mouseY, float f) {
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    GL11.glDisable(2896);
    GL11.glDisable(2929);
    mouseX -= xStart;
    mouseY -= yStart;
    if (mouseX > 0 && mouseX < 32) {
      int pos = mouseY / 32;
      if (pos >= 0 && pos < 13) {
        int stringlength = this.fontRenderer.getStringWidth(LanguageUtilities.string("help.tab_" + (pos + 1)));
        drawGradientRect(mouseX + 10 - 3, mouseY + 10 - 3, mouseX + 10 + stringlength + 3, mouseY + 10 + 14, -1073741824, -1073741824);
        this.fontRenderer.drawString(LanguageUtilities.string("help.tab_" + (pos + 1)), mouseX + 10, mouseY + 10, 9474192);
      } 
    } 
    if (mouseX > 224 && mouseX < 256) {
      int pos = mouseY / 32;
      if (pos >= 0 && pos < 6) {
        int stringlength = this.fontRenderer.getStringWidth(LanguageUtilities.string("help.tab_" + (pos + 8)));
        drawGradientRect(mouseX + 10 - 3, mouseY + 10 - 3, mouseX + 10 + stringlength + 3, mouseY + 10 + 14, -1073741824, -1073741824);
        this.fontRenderer.drawString(LanguageUtilities.string("help.tab_" + (pos + 8)), mouseX + 10, mouseY + 10, 9474192);
      } 
    } 
    GL11.glEnable(2896);
    GL11.glEnable(2929);
  }
  
  public boolean doesGuiPauseGame() {
    return true;
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    List<List<String>> baseText = LanguageUtilities.getHelp(this.helpDisplayed);
    if (baseText != null) {
      this.textBook = convertAdjustHelpText(baseText);
    } else {
      this.textBook = new TextBook();
      TextPage page = new TextPage();
      page.addLine("Il n'y a malheuresement pas d'aide disponible dans votre langue.");
      page.addLine("");
      page.addLine("Unfortunately there is no help available in your language.");
      this.textBook.addPage(page);
      this.textBook = this.bookManager.adjustTextBookLineLength(this.textBook);
    } 
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int k) throws IOException {
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    int ai = mouseX - xStart;
    int aj = mouseY - yStart;
    if (aj > getYSize() - 14 && aj < getYSize())
      if (ai > 36 && ai < 64) {
        decrementPage();
      } else if (ai > getXSize() - 64 && ai < getXSize() - 36) {
        incrementPage();
      }  
    if (ai > 0 && ai < 32) {
      int pos = aj / 32;
      if (pos >= 0 && pos < 13) {
        pos++;
        this.pageNum = 0;
        if (pos != this.helpDisplayed) {
          this.helpDisplayed = pos;
          initData();
        } 
      } 
    } 
    if (ai > 224 && ai < 256) {
      int pos = aj / 32;
      if (pos >= 0 && pos < 6) {
        pos += 8;
        this.pageNum = 0;
        if (pos != this.helpDisplayed) {
          this.helpDisplayed = pos;
          initData();
        } 
      } 
    } 
    super.mouseClicked(mouseX, mouseY, k);
  }
}
