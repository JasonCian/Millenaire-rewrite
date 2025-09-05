package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GuiCustomBuilding extends GuiText {
  private static final int BUTTON_CONFIRM = 1;
  
  private final Building townHall;
  
  private final Building existingBuilding;
  
  private final Point pos;
  
  private final VillageType villageType;
  
  private final BuildingCustomPlan customBuilding;
  
  private final EntityPlayer player;
  
  private final Map<BuildingCustomPlan.TypeRes, List<Point>> resources;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");
  
  public GuiCustomBuilding(EntityPlayer player, Building building) {
    this.townHall = building.getTownHall();
    this.existingBuilding = building;
    this.villageType = null;
    this.pos = building.getPos();
    this.player = player;
    this.customBuilding = building.location.getCustomPlan();
    this.resources = this.customBuilding.findResources(this.townHall.world, this.pos, this.townHall, building.location);
    this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
  }
  
  public GuiCustomBuilding(EntityPlayer player, Building th, Point p, BuildingCustomPlan customBuilding) {
    this.townHall = th;
    this.villageType = null;
    this.existingBuilding = null;
    this.pos = p;
    this.player = player;
    this.customBuilding = customBuilding;
    this.resources = customBuilding.findResources(th.world, this.pos, th, null);
    this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
  }
  
  public GuiCustomBuilding(EntityPlayer player, Point p, VillageType villageType) {
    this.townHall = null;
    this.existingBuilding = null;
    this.villageType = villageType;
    this.pos = p;
    this.player = player;
    this.customBuilding = villageType.customCentre;
    this.resources = this.customBuilding.findResources(player.world, this.pos, null, null);
    this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (guibutton instanceof GuiText.MillGuiButton)
      if (guibutton.id == 1) {
        if (this.townHall != null) {
          if (this.existingBuilding == null) {
            ClientSender.newCustomBuilding(this.player, this.townHall, this.pos, this.customBuilding.buildingKey);
          } else {
            ClientSender.updateCustomBuilding(this.player, this.existingBuilding);
          } 
        } else {
          ClientSender.newVillageCreation(this.player, this.pos, this.villageType.culture.key, this.villageType.key);
        } 
        closeWindow();
      } else {
        closeWindow();
        if (this.townHall != null) {
          DisplayActions.displayNewBuildingProjectGUI(this.player, this.townHall, this.pos);
        } else {
          DisplayActions.displayNewVillageGUI(this.player, this.pos);
        } 
      }  
    super.actionPerformed(guibutton);
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public void decrementPage() {
    super.decrementPage();
    buttonPagination();
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void incrementPage() {
    super.incrementPage();
    buttonPagination();
  }
  
  public void initData() {
    boolean validBuild = true;
    for (BuildingCustomPlan.TypeRes res : this.customBuilding.minResources.keySet()) {
      if (!this.resources.containsKey(res) || ((List)this.resources.get(res)).size() < ((Integer)this.customBuilding.minResources.get(res)).intValue())
        validBuild = false; 
    } 
    List<List<TextLine>> pages = new ArrayList<>();
    List<TextLine> text = new ArrayList<>();
    if (this.townHall != null) {
      text.add(new TextLine(this.townHall.getVillageQualifiedName(), "§1", new GuiText.GuiButtonReference(this.townHall.villageType)));
    } else {
      text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_newvillage"), "§1"));
    } 
    text.add(new TextLine());
    if (this.existingBuilding != null) {
      text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_edit", new String[] { this.customBuilding.getFullDisplayName() })));
    } else if (validBuild) {
      text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_confirm", new String[] { this.customBuilding.getFullDisplayName() })));
    } else {
      text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_cantconfirm", new String[] { this.customBuilding.getFullDisplayName() })));
    } 
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_radius", new String[] { "" + this.customBuilding.radius, "" + this.customBuilding.heightRadius })));
    if (this.resources.containsKey(BuildingCustomPlan.TypeRes.SIGN) && ((List)this.resources.get(BuildingCustomPlan.TypeRes.SIGN)).size() > 1) {
      text.add(new TextLine());
      text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_signnumber", new String[] { "" + ((List)this.resources.get(BuildingCustomPlan.TypeRes.SIGN)).size() })));
    } 
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_resneededintro")));
    text.add(new TextLine());
    for (BuildingCustomPlan.TypeRes res : this.customBuilding.minResources.keySet()) {
      int resFound = 0;
      if (this.resources.containsKey(res))
        resFound = ((List)this.resources.get(res)).size(); 
      text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_resneeded", new String[] { LanguageUtilities.string("custombuilding." + res.key), "" + resFound, "" + this.customBuilding.minResources
                .get(res), "" + this.customBuilding.maxResources.get(res) })));
    } 
    text.add(new TextLine());
    if (validBuild) {
      text.add(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.close"), 0), new GuiText.MillGuiButton(LanguageUtilities.string("ui.confirm"), 1)));
    } else {
      text.add(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.close"), 0)));
    } 
    pages.add(text);
    this.textBook = this.bookManager.convertAndAdjustLines(pages);
  }
}
