package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.world.UserProfile;

public class GuiNewVillage extends GuiText {
  private List<VillageType> possibleVillages = new ArrayList<>();
  
  private final Point pos;
  
  private final EntityPlayer player;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");
  
  public GuiNewVillage(EntityPlayer player, Point p) {
    this.pos = p;
    this.player = player;
    this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (guibutton instanceof GuiText.MillGuiButton) {
      if (!guibutton.enabled)
        return; 
      VillageType village = this.possibleVillages.get(guibutton.id);
      closeWindow();
      if (village.customCentre == null) {
        ClientSender.newVillageCreation(this.player, this.pos, village.culture.key, village.key);
      } else {
        DisplayActions.displayNewCustomBuildingGUI(this.player, this.pos, village);
      } 
    } 
    super.actionPerformed(guibutton);
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("ui.selectavillage"), "§1"));
    text.add(new TextLine(false));
    text.add(new TextLine(LanguageUtilities.string("ui.leadershipstatus") + ":"));
    text.add(new TextLine());
    boolean notleader = false;
    UserProfile profile = Mill.proxy.getClientProfile();
    for (Culture culture : Culture.ListCultures) {
      if (profile.isTagSet("culturecontrol_" + culture.key)) {
        text.add(new TextLine(LanguageUtilities.string("ui.leaderin", new String[] { culture.getAdjectiveTranslated() }), new GuiText.GuiButtonReference(culture)));
        continue;
      } 
      text.add(new TextLine(LanguageUtilities.string("ui.notleaderin", new String[] { culture.getAdjectiveTranslated() }), new GuiText.GuiButtonReference(culture)));
      notleader = true;
    } 
    if (notleader) {
      text.add(new TextLine());
      text.add(new TextLine(LanguageUtilities.string("ui.leaderinstruction")));
    } 
    text.add(new TextLine());
    this.possibleVillages = VillageType.spawnableVillages(this.player);
    for (int i = 0; i < this.possibleVillages.size(); i++) {
      text.add(new TextLine(new GuiText.MillGuiButton(((VillageType)this.possibleVillages.get(i)).name, i, ((VillageType)this.possibleVillages.get(i)).culture.getIcon(), ((VillageType)this.possibleVillages.get(i)).getIcon())));
      String extraInfo = ((VillageType)this.possibleVillages.get(i)).culture.getAdjectiveTranslated();
      String nameTranslated = ((VillageType)this.possibleVillages.get(i)).getNameTranslated();
      if (nameTranslated != null)
        extraInfo = extraInfo + ", " + nameTranslated; 
      text.add(new TextLine("(" + extraInfo + ")"));
      text.add(new TextLine());
    } 
    List<List<TextLine>> pages = new ArrayList<>();
    pages.add(text);
    this.textBook = this.bookManager.convertAndAdjustLines(pages);
  }
}
