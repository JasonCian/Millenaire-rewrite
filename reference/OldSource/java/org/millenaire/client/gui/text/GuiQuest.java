package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.quest.QuestStep;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.world.UserProfile;

public class GuiQuest extends GuiText {
  private final MillVillager villager;
  
  private final EntityPlayer player;
  
  private boolean showOk = false;
  
  private int type;
  
  private boolean firstStep;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");
  
  public GuiQuest(EntityPlayer player, MillVillager villager) {
    this.villager = villager;
    this.player = player;
    this.bookManager = new BookManager(256, 220, 160, 240, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (!(guibutton instanceof GuiText.GuiButtonReference)) {
      UserProfile profile = Mill.proxy.getClientProfile();
      QuestInstance qi = (QuestInstance)profile.villagersInQuests.get(Long.valueOf(this.villager.getVillagerId()));
      boolean questActionHandled = false;
      if (qi != null)
        if (guibutton.id == 0) {
          boolean firstStep = (qi.currentStep == 0);
          String res = qi.completeStep(this.player, this.villager);
          ClientSender.questCompleteStep(this.player, this.villager);
          initStatus(1, res, firstStep);
          questActionHandled = true;
        } else if (guibutton.id == 1) {
          boolean firstStep = (qi.currentStep == 0);
          String res = qi.refuseQuest(this.player, this.villager);
          ClientSender.questRefuse(this.player, this.villager);
          initStatus(2, res, firstStep);
          questActionHandled = true;
        }  
      if (!questActionHandled) {
        this.mc.displayGuiScreen(null);
        this.mc.setIngameFocus();
        ClientSender.villagerInteractSpecial(this.player, this.villager);
      } 
    } 
    super.actionPerformed(guibutton);
  }
  
  public void buttonPagination() {
    super.buttonPagination();
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    if (this.type == 0) {
      if (this.firstStep) {
        if (this.showOk) {
          this.buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("quest.refuse")));
          this.buttonList.add(new GuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("quest.accept")));
        } else {
          this.buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("quest.refuse")));
          this.buttonList.add(new GuiButton(2, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("quest.close")));
        } 
      } else if (this.showOk) {
        this.buttonList.add(new GuiButton(0, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, LanguageUtilities.string("quest.continue")));
      } else {
        this.buttonList.add(new GuiButton(2, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, LanguageUtilities.string("quest.close")));
      } 
    } else {
      this.buttonList.add(new GuiButton(2, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, LanguageUtilities.string("quest.close")));
    } 
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  private TextBook getData(int type, String baseText) {
    List<TextLine> text = new ArrayList<>();
    String game = "";
    if (this.villager.getGameOccupationName(this.player.getName()).length() > 0)
      game = " (" + this.villager.getGameOccupationName(this.player.getName()) + ")"; 
    text.add(new TextLine(this.villager.getName() + ", " + this.villager.getNativeOccupationName() + game, "§1", new GuiText.GuiButtonReference(this.villager.vtype)));
    text.add(new TextLine());
    text.add(new TextLine(baseText.replaceAll("\\$name", this.player.getName())));
    UserProfile profile = Mill.proxy.getClientProfile();
    if (type == 0) {
      QuestStep step = ((QuestInstance)profile.villagersInQuests.get(Long.valueOf(this.villager.getVillagerId()))).getCurrentStep();
      String error = step.lackingConditions(this.player);
      if (error != null) {
        text.add(new TextLine());
        text.add(new TextLine(error));
        this.showOk = false;
      } else {
        this.showOk = true;
      } 
    } 
    List<List<TextLine>> ftext = new ArrayList<>();
    ftext.add(text);
    return this.bookManager.convertAndAdjustLines(ftext);
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    UserProfile profile = Mill.proxy.getClientProfile();
    String baseText = ((QuestInstance)profile.villagersInQuests.get(Long.valueOf(this.villager.getVillagerId()))).getDescription(profile);
    boolean firstStep = (((QuestInstance)profile.villagersInQuests.get(Long.valueOf(this.villager.getVillagerId()))).currentStep == 0);
    initStatus(0, baseText, firstStep);
  }
  
  private void initStatus(int type, String baseText, boolean firstStep) {
    this.pageNum = 0;
    this.type = type;
    this.firstStep = firstStep;
    this.textBook = getData(type, baseText);
    buttonPagination();
  }
  
  protected void keyTyped(char c, int i) {
    if (i == 1) {
      this.mc.displayGuiScreen(null);
      this.mc.setIngameFocus();
      ClientSender.villagerInteractSpecial(this.player, this.villager);
    } 
  }
}
