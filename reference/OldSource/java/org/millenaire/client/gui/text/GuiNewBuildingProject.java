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
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.buildingplan.IBuildingPlan;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GuiNewBuildingProject extends GuiText {
  private final Building townHall;
  
  private final Point pos;
  
  private final EntityPlayer player;
  
  public static class GuiButtonNewBuilding extends GuiText.MillGuiButton {
    private final String key;
    
    private final boolean custom;
    
    public GuiButtonNewBuilding(String key, String label, boolean custom) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
      this.custom = custom;
    }
  }
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");
  
  public GuiNewBuildingProject(EntityPlayer player, Building th, Point p) {
    this.townHall = th;
    this.pos = p;
    this.player = player;
    this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (guibutton instanceof GuiButtonNewBuilding) {
      GuiButtonNewBuilding button = (GuiButtonNewBuilding)guibutton;
      if (!button.custom) {
        ClientSender.newBuilding(this.player, this.townHall, this.pos, button.key);
      } else {
        closeWindow();
        BuildingCustomPlan customBuilding = this.townHall.culture.getBuildingCustom(button.key);
        if (customBuilding != null)
          DisplayActions.displayNewCustomBuildingGUI(this.player, this.townHall, this.pos, customBuilding); 
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
  
  private String getCustomPlanDesc(IBuildingPlan customPlan) {
    String desc = "";
    if (customPlan.getNameTranslated() != null && customPlan.getNameTranslated().length() > 0)
      desc = desc + customPlan.getNameTranslated() + ". "; 
    if (customPlan.getMaleResident().size() > 0 || customPlan.getFemaleResident().size() > 0) {
      desc = desc + LanguageUtilities.string("ui.inhabitants") + ": ";
      boolean first = true;
      for (String inhabitant : customPlan.getMaleResident()) {
        if (first) {
          first = false;
        } else {
          desc = desc + ", ";
        } 
        desc = desc + (customPlan.getCulture().getVillagerType(inhabitant)).name;
      } 
      for (String inhabitant : customPlan.getFemaleResident()) {
        if (first) {
          first = false;
        } else {
          desc = desc + ", ";
        } 
        desc = desc + (customPlan.getCulture().getVillagerType(inhabitant)).name;
      } 
      desc = desc + ". ";
    } 
    return desc;
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void incrementPage() {
    super.incrementPage();
    buttonPagination();
  }
  
  public void initData() {
    List<List<TextLine>> pages = new ArrayList<>();
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(this.townHall.getVillageQualifiedName(), "§1", new GuiText.GuiButtonReference(this.townHall.villageType)));
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.selectabuildingproject")));
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.selectabuildingproject_custom")));
    for (BuildingCustomPlan customBuilding : this.townHall.villageType.customBuildings) {
      text.add(new TextLine(new GuiButtonNewBuilding(customBuilding.buildingKey, LanguageUtilities.string("ui.custom") + " " + customBuilding.getNativeName(), true)));
      String desc = getCustomPlanDesc((IBuildingPlan)customBuilding).trim();
      if (desc.length() > 0)
        text.add(new TextLine(desc)); 
      text.add(new TextLine());
    } 
    pages.add(text);
    text = new ArrayList<>();
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.selectabuildingproject_standard")));
    for (BuildingPlanSet planSet : this.townHall.villageType.coreBuildings) {
      if (this.townHall.isValidProject(planSet.getBuildingProject())) {
        GuiButtonNewBuilding button = new GuiButtonNewBuilding(planSet.key, LanguageUtilities.string("ui.millenaireplan") + " " + planSet.getNameNative(), false);
        button.itemStackIconLeft = planSet.getIcon();
        text.add(new TextLine(button));
        String desc = getCustomPlanDesc((IBuildingPlan)planSet.getFirstStartingPlan()).trim();
        if (desc.length() > 0)
          text.add(new TextLine(desc)); 
        text.add(new TextLine());
      } 
    } 
    pages.add(text);
    this.textBook = this.bookManager.convertAndAdjustLines(pages);
  }
}
