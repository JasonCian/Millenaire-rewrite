package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.common.config.MillConfigParameter;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.LanguageUtilities;

public class GuiConfig extends GuiText {
  public static class ConfigButton extends GuiText.MillGuiButton {
    public MillConfigParameter config;
    
    public ConfigButton(MillConfigParameter config) {
      super(0, 0, 0, 0, 0, config.getLabel());
      this.config = config;
      refreshLabel();
    }
    
    public void refreshLabel() {
      this.displayString = this.config.getLabel() + ": " + this.config.getStringValue();
    }
  }
  
  public static class ConfigPageButton extends GuiText.MillGuiButton {
    public int pageId;
    
    public ConfigPageButton(int pageId) {
      super(0, 0, 0, 0, 0, LanguageUtilities.string(MillConfigValues.configPageTitles.get(pageId)));
      this.pageId = pageId;
    }
  }
  
  int pageId = -1;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/config.png");
  
  public GuiConfig() {
    this.bookManager = new BookManager(256, 220, 190, 247, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (guibutton instanceof ConfigButton) {
      ConfigButton configButton = (ConfigButton)guibutton;
      int valPos = -1;
      for (int i = 0; i < (configButton.config.getPossibleVals()).length; i++) {
        Object o = configButton.config.getPossibleVals()[i];
        if (o.equals(configButton.config.getValue()))
          valPos = i; 
      } 
      valPos++;
      if (valPos >= (configButton.config.getPossibleVals()).length)
        valPos = 0; 
      configButton.config.setValue(configButton.config.getPossibleVals()[valPos]);
      configButton.refreshLabel();
      MillConfigValues.writeConfigFile();
    } else if (guibutton instanceof ConfigPageButton) {
      ConfigPageButton configPageButton = (ConfigPageButton)guibutton;
      this.pageId = configPageButton.pageId;
      this.pageNum = 0;
      this.textBook = getData();
      buttonPagination();
    } 
    super.actionPerformed(guibutton);
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public boolean doesGuiPauseGame() {
    return true;
  }
  
  private TextBook getData() {
    if (this.pageId == -1)
      return getHomepageData(); 
    return getPageData();
  }
  
  private TextBook getHomepageData() {
    List<List<TextLine>> pages = new ArrayList<>();
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine("<darkblue>" + LanguageUtilities.string("config.pagetitle"), false));
    text.add(new TextLine("", false));
    for (int i = 0; i < MillConfigValues.configPages.size(); i++)
      text.add(new TextLine(new ConfigPageButton(i))); 
    pages.add(text);
    return this.bookManager.convertAndAdjustLines(pages);
  }
  
  private TextBook getPageData() {
    int buttonId = 0;
    List<List<TextLine>> pages = new ArrayList<>();
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine("<darkblue>" + LanguageUtilities.string(MillConfigValues.configPageTitles.get(this.pageId)), false));
    text.add(new TextLine());
    if (MillConfigValues.configPageDesc.get(this.pageId) != null) {
      text.add(new TextLine(LanguageUtilities.string(MillConfigValues.configPageDesc.get(this.pageId)), false));
      text.add(new TextLine());
    } 
    for (int j = 0; j < ((List)MillConfigValues.configPages.get(this.pageId)).size(); j++) {
      MillConfigParameter config = ((List<MillConfigParameter>)MillConfigValues.configPages.get(this.pageId)).get(j);
      if (config.displayConfig || (config.displayConfigDev && MillConfigValues.DEV)) {
        if (config.getDesc().length() > 0)
          text.add(new TextLine(config.getDesc(), false)); 
        if (config.hasTextField()) {
          GuiText.MillGuiTextField textField = new GuiText.MillGuiTextField(buttonId++, this.fontRenderer, 0, 0, 0, 0, config.key);
          textField.setText(config.getStringValue());
          textField.setMaxStringLength(config.strLimit);
          textField.setTextColor(-1);
          text.add(new TextLine(config.getLabel() + ":", textField));
          text.add(new TextLine(false));
          text.add(new TextLine());
        } else {
          text.add(new TextLine(new ConfigButton(config)));
        } 
      } 
    } 
    pages.add(text);
    return this.bookManager.convertAndAdjustLines(pages);
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  protected void handleTextFieldPress(GuiText.MillGuiTextField textField) {
    if (MillConfigValues.configParameters.containsKey(textField.fieldKey)) {
      MillConfigParameter config = (MillConfigParameter)MillConfigValues.configParameters.get(textField.fieldKey);
      config.setValueFromString(textField.getText(), false);
      MillConfigValues.writeConfigFile();
    } 
  }
  
  public void initData() {
    this.textBook = getData();
  }
  
  protected void keyTyped(char c, int i) {
    if (i == 1) {
      if (this.pageId == -1) {
        this.mc.displayGuiScreen(null);
        this.mc.setIngameFocus();
      } else {
        this.pageId = -1;
        this.pageNum = 0;
        this.textBook = getData();
        buttonPagination();
      } 
    } else {
      super.keyTyped(c, i);
    } 
  }
  
  public void updateScreen() {}
}
