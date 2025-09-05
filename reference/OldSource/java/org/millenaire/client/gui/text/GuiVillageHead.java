package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.world.UserProfile;

public class GuiVillageHead extends GuiText {
  private final MillVillager chief;
  
  private final EntityPlayer player;
  
  private static class GuiButtonChief extends GuiText.MillGuiButton {
    private static final String PRAISE = "PRAISE";
    
    private static final String SLANDER = "SLANDER";
    
    private static final String BUILDING = "BUILDING";
    
    private static final String VILLAGE_SCROLL = "VILLAGE_SCROLL";
    
    private static final String CULTURE_CONTROL = "CULTURE_CONTROL";
    
    private static final String CROP = "CROP";
    
    private static final String HUNTING_DROP = "HUNTING_DROP";
    
    private Point village;
    
    private String value;
    
    private final String key;
    
    private GuiButtonChief(String key, String label) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
    }
    
    private GuiButtonChief(String key, String label, Point v) {
      super(0, 0, 0, 0, 0, label);
      this.village = v;
      this.key = key;
    }
    
    public GuiButtonChief(String key, String label, String plan) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
      this.value = plan;
    }
  }
  
  private class VillageRelation implements Comparable<VillageRelation> {
    int relation;
    
    Point pos;
    
    String name;
    
    VillageRelation(Point p, int r, String name) {
      this.relation = r;
      this.pos = p;
      this.name = name;
    }
    
    public int compareTo(VillageRelation arg0) {
      return this.name.compareTo(arg0.name);
    }
    
    public boolean equals(Object o) {
      if (o == null || !(o instanceof VillageRelation))
        return false; 
      return this.pos.equals(((VillageRelation)o).pos);
    }
    
    public int hashCode() {
      return this.pos.hashCode();
    }
  }
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/village_chief.png");
  
  public GuiVillageHead(EntityPlayer player, MillVillager chief) {
    this.chief = chief;
    this.player = player;
    this.bookManager = new BookManager(256, 200, 160, 240, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (guibutton instanceof GuiButtonChief) {
      GuiButtonChief gb = (GuiButtonChief)guibutton;
      boolean close = false;
      if (gb.key == "PRAISE") {
        ClientSender.villageChiefPerformDiplomacy(this.player, this.chief, gb.village, true);
      } else if (gb.key == "SLANDER") {
        ClientSender.villageChiefPerformDiplomacy(this.player, this.chief, gb.village, false);
      } else if (gb.key == "VILLAGE_SCROLL") {
        ClientSender.villageChiefPerformVillageScroll(this.player, this.chief);
        close = true;
      } else if (gb.key == "CULTURE_CONTROL") {
        ClientSender.villageChiefPerformCultureControl(this.player, this.chief);
        close = true;
      } else if (gb.key == "BUILDING") {
        ClientSender.villageChiefPerformBuilding(this.player, this.chief, gb.value);
        close = true;
      } else if (gb.key == "CROP") {
        ClientSender.villageChiefPerformCrop(this.player, this.chief, gb.value);
        close = true;
      } else if (gb.key == "HUNTING_DROP") {
        ClientSender.villageChiefPerformHuntingDrop(this.player, this.chief, gb.value);
        close = true;
      } 
      if (close) {
        closeWindow();
      } else {
        this.textBook = getData();
        buttonPagination();
      } 
    } 
    super.actionPerformed(guibutton);
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  private TextBook getData() {
    List<TextLine> text = new ArrayList<>();
    String game = "";
    if (this.chief.getGameOccupationName(this.player.getName()).length() > 0)
      game = " (" + this.chief.getGameOccupationName(this.player.getName()) + ")"; 
    text.add(new TextLine(this.chief.getName() + ", " + this.chief.getNativeOccupationName() + game, "§1", new GuiText.GuiButtonReference(this.chief.vtype)));
    text.add(new TextLine(LanguageUtilities.string("ui.villagechief", new String[] { this.chief.getTownHall().getVillageQualifiedName() })));
    text.add(new TextLine());
    String col = "";
    if (this.chief.getTownHall().getReputation(this.player) >= 32768) {
      col = "§2";
    } else if (this.chief.getTownHall().getReputation(this.player) >= 4096) {
      col = "§1";
    } else if (this.chief.getTownHall().getReputation(this.player) < -256) {
      col = "§4";
    } else if (this.chief.getTownHall().getReputation(this.player) < 0) {
      col = "§c";
    } 
    text.add(new TextLine(LanguageUtilities.string("ui.yourstatus") + ": " + this.chief.getTownHall().getReputationLevelLabel(this.player), col));
    text.add(new TextLine(this.chief.getTownHall().getReputationLevelDesc(this.player).replaceAll("\\$name", this.player.getName()), col));
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.possiblehousing") + ":", "§1"));
    text.add(new TextLine());
    UserProfile profile = Mill.proxy.getClientProfile();
    int reputation = this.chief.getTownHall().getReputation(this.player);
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if ((this.chief.getTownHall()).buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = (List<BuildingProject>)(this.chief.getTownHall()).buildingProjects.get(ep);
        for (BuildingProject project : projectsLevel) {
          if (project.planSet != null) {
            BuildingPlan plan = project.planSet.getFirstStartingPlan();
            if (plan != null && plan.price > 0 && !plan.isgift) {
              String status = "";
              boolean buyButton = false;
              if (project.location != null) {
                status = LanguageUtilities.string("ui.alreadybuilt") + ".";
              } else if ((this.chief.getTownHall()).buildingsBought.contains(project.key)) {
                status = LanguageUtilities.string("ui.alreadyrequested") + ".";
              } else if (plan.reputation > reputation) {
                status = LanguageUtilities.string("ui.notavailableyet") + ".";
              } else if (plan.price > MillCommonUtilities.countMoney((IInventory)this.player.inventory)) {
                status = LanguageUtilities.string("ui.youaremissing", new String[] { "" + MillCommonUtilities.getShortPrice(plan.price - MillCommonUtilities.countMoney((IInventory)this.player.inventory)) });
              } else {
                status = LanguageUtilities.string("ui.available") + ".";
                buyButton = true;
              } 
              text.add(new TextLine(plan.nativeName + ": " + status, false));
              if (buyButton) {
                GuiButtonChief button = new GuiButtonChief("BUILDING", LanguageUtilities.string("ui.buybuilding", new String[] { plan.nativeName, MillCommonUtilities.getShortPrice(plan.price) }), plan.buildingKey);
                button.itemStackIconLeft = plan.getIcon();
                button.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
                text.add(new TextLine(button));
              } 
              continue;
            } 
            if (plan.isgift && MillConfigValues.bonusEnabled && !Mill.isDistantClient()) {
              String status = "";
              boolean buyButton = false;
              if (project.location != null) {
                status = LanguageUtilities.string("ui.alreadybuilt") + ".";
              } else if ((this.chief.getTownHall()).buildingsBought.contains(project.key)) {
                status = LanguageUtilities.string("ui.alreadyrequested") + ".";
              } else {
                status = LanguageUtilities.string("ui.bonusavailable") + ".";
                buyButton = true;
              } 
              text.add(new TextLine(plan.nativeName + ": " + status, false));
              if (buyButton) {
                GuiButtonChief button = new GuiButtonChief("BUILDING", LanguageUtilities.string("ui.buybonusbuilding", new String[] { plan.nativeName }), plan.buildingKey);
                button.itemStackIconLeft = plan.getIcon();
                text.add(new TextLine(button));
              } 
            } 
          } 
        } 
      } 
    } 
    if (8192 > reputation) {
      text.add(new TextLine(LanguageUtilities.string("ui.scrollsnoreputation")));
    } else if (128 > MillCommonUtilities.countMoney((IInventory)this.player.inventory)) {
      text.add(new TextLine(
            LanguageUtilities.string("ui.scrollsnotenoughmoney", new String[] { "" + MillCommonUtilities.getShortPrice(128 - MillCommonUtilities.countMoney((IInventory)this.player.inventory)) })));
    } else {
      text.add(new TextLine(LanguageUtilities.string("ui.scrollsok"), false));
      GuiButtonChief button = new GuiButtonChief("VILLAGE_SCROLL", LanguageUtilities.string("ui.buyscroll"), MillCommonUtilities.getShortPrice(128));
      button.itemStackIconLeft = new ItemStack((Item)MillItems.PARCHMENT_VILLAGE_SCROLL, 1);
      button.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
      text.add(new TextLine(button));
    } 
    if ((this.chief.getCulture()).knownCrops.size() > 0) {
      text.add(new TextLine(LanguageUtilities.string("ui.cropsknown")));
      text.add(new TextLine());
      for (String crop : (this.chief.getCulture()).knownCrops) {
        Item itemCrop = Item.getByNameOrId("millenaire:" + crop);
        String localizedName = I18n.format(itemCrop.getUnlocalizedName() + ".name", new Object[0]);
        if (profile.isTagSet("cropplanting_" + crop)) {
          text.add(new TextLine(LanguageUtilities.string("ui.cropknown", new String[] { localizedName })));
          continue;
        } 
        if (8192 > reputation) {
          text.add(new TextLine(LanguageUtilities.string("ui.cropinsufficientreputation", new String[] { localizedName })));
          continue;
        } 
        if (512 > MillCommonUtilities.countMoney((IInventory)this.player.inventory)) {
          text.add(new TextLine(LanguageUtilities.string("ui.cropnotenoughmoney", new String[] { localizedName, "" + 
                    MillCommonUtilities.getShortPrice(512 - MillCommonUtilities.countMoney((IInventory)this.player.inventory)) })));
          continue;
        } 
        text.add(new TextLine(LanguageUtilities.string("ui.cropoktolearn", new String[] { localizedName }), false));
        GuiButtonChief button = new GuiButtonChief("CROP", LanguageUtilities.string("ui.croplearn", new String[] { "" + MillCommonUtilities.getShortPrice(512) }), crop);
        button.itemStackIconLeft = new ItemStack(itemCrop, 1);
        button.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
        text.add(new TextLine(button));
      } 
      text.add(new TextLine());
    } 
    if ((this.chief.getCulture()).knownHuntingDrops.size() > 0) {
      text.add(new TextLine(LanguageUtilities.string("ui.huntingdropsknown")));
      text.add(new TextLine());
      for (String crop : (this.chief.getCulture()).knownHuntingDrops) {
        Item itemCrop = Item.getByNameOrId("millenaire:" + crop);
        String localizedName = I18n.format(itemCrop.getUnlocalizedName() + ".name", new Object[0]);
        if (profile.isTagSet("huntingdrop_" + crop)) {
          text.add(new TextLine(LanguageUtilities.string("ui.huntingdropknown", new String[] { localizedName })));
          continue;
        } 
        if (8192 > reputation) {
          text.add(new TextLine(LanguageUtilities.string("ui.huntingdropinsufficientreputation", new String[] { localizedName })));
          continue;
        } 
        if (512 > MillCommonUtilities.countMoney((IInventory)this.player.inventory)) {
          text.add(new TextLine(LanguageUtilities.string("ui.huntingdropnotenoughmoney", new String[] { localizedName, "" + 
                    MillCommonUtilities.getShortPrice(512 - MillCommonUtilities.countMoney((IInventory)this.player.inventory)) })));
          continue;
        } 
        text.add(new TextLine(LanguageUtilities.string("ui.huntingdropoktolearn", new String[] { localizedName }), false));
        GuiButtonChief button = new GuiButtonChief("HUNTING_DROP", LanguageUtilities.string("ui.huntingdroplearn", new String[] { "" + MillCommonUtilities.getShortPrice(512) }), crop);
        button.itemStackIconLeft = new ItemStack(itemCrop, 1);
        button.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
        text.add(new TextLine(button));
      } 
      text.add(new TextLine());
    } 
    if (profile.isTagSet("culturecontrol_" + (this.chief.getCulture()).key)) {
      text.add(new TextLine(LanguageUtilities.string("ui.control_alreadydone", new String[] { this.chief.getCulture().getAdjectiveTranslated() })));
    } else if (131072 > reputation) {
      text.add(new TextLine(LanguageUtilities.string("ui.control_noreputation", new String[] { this.chief.getCulture().getAdjectiveTranslated() })));
    } else {
      text.add(new TextLine(LanguageUtilities.string("ui.control_ok", new String[] { this.chief.getCulture().getAdjectiveTranslated() }), false));
      GuiButtonChief button = new GuiButtonChief("CULTURE_CONTROL", LanguageUtilities.string("ui.control_get"));
      button.itemStackIconLeft = this.chief.getCulture().getIcon();
      button.itemStackIconRight = new ItemStack((Item)Items.IRON_HELMET, 1);
      text.add(new TextLine(button));
    } 
    List<List<TextLine>> pages = new ArrayList<>();
    pages.add(text);
    text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("ui.relationlist"), "§1"));
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("ui.relationpoints", new String[] { "" + profile.getDiplomacyPoints(this.chief.getTownHall()) })));
    text.add(new TextLine());
    List<VillageRelation> relations = new ArrayList<>();
    for (Point p : this.chief.getTownHall().getKnownVillages()) {
      Building b = (this.chief.getTownHall()).mw.getBuilding(p);
      if (b != null)
        relations.add(new VillageRelation(p, this.chief.getTownHall().getRelationWithVillage(p), b.getVillageQualifiedName())); 
    } 
    Collections.sort(relations);
    for (VillageRelation vr : relations) {
      Building b = (this.chief.getTownHall()).mw.getBuilding(vr.pos);
      if (b != null) {
        col = "";
        if (vr.relation > 70) {
          col = "<darkgreen>";
        } else if (vr.relation > 30) {
          col = "<darkblue>";
        } else if (vr.relation <= -90) {
          col = "<darkred>";
        } else if (vr.relation <= -30) {
          col = "<lightred>";
        } 
        text.add(new TextLine(col + LanguageUtilities.string("ui.villagerelations", new String[] { b.getVillageQualifiedName(), b.villageType.name, b.culture.getAdjectiveTranslated(), 
                  LanguageUtilities.string(VillageUtilities.getRelationName(vr.relation)) + " (" + vr.relation + ")" }), false));
        GuiButtonChief praise = null;
        GuiButtonChief slander = null;
        if (profile.getDiplomacyPoints(this.chief.getTownHall()) > 0 && reputation > 0) {
          if (vr.relation < 100) {
            praise = new GuiButtonChief("PRAISE", LanguageUtilities.string("ui.relationpraise"), vr.pos);
            praise.itemStackIconLeft = new ItemStack((Block)Blocks.RED_FLOWER, 1);
          } 
          if (vr.relation > -100) {
            slander = new GuiButtonChief("SLANDER", LanguageUtilities.string("ui.relationslander"), vr.pos);
            slander.itemStackIconLeft = new ItemStack(Items.IRON_SWORD, 1);
          } 
          text.add(new TextLine(praise, slander));
          text.add(new TextLine());
          continue;
        } 
        text.add(new TextLine("<darkred>" + LanguageUtilities.string("ui.villagerelationsnobutton")));
        text.add(new TextLine());
      } 
    } 
    pages.add(text);
    text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("ui.relationhelp")));
    pages.add(text);
    return this.bookManager.convertAndAdjustLines(pages);
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    this.textBook = getData();
  }
}
