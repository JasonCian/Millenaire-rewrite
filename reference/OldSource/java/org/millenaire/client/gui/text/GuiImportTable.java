package org.millenaire.client.gui.text;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingFileFiler;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.TileEntityImportTable;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.virtualdir.VirtualDir;

public class GuiImportTable extends GuiText {
  private static final String SETTINGS_CLEARGROUND = "clearground";
  
  private static final String SETTINGS_WIDTH_MINUS = "width_minus";
  
  private static final String SETTINGS_WIDTH_PLUS = "width_plus";
  
  private static final String SETTINGS_LENGTH_PLUS = "length_plus";
  
  private static final String SETTINGS_LENGTH_MINUS = "length_minus";
  
  private static final String IMPORT_ALL = "all";
  
  private static final String SETTINGS_IMPORTMOCKBLOCKS = "importmockblocks";
  
  private static final String SETTINGS_CONVERTTOPRESERVEGROUND = "onverttopreserveground";
  
  private static final String SETTINGS_EXPORTSNOW = "exportsnow";
  
  private static final String SETTINGS_EXPORTREGULARCHESTS = "exportregularchests";
  
  private static final String SETTINGS_STARTINGLEVEL_PLUS = "startinglevel_plus";
  
  private static final String SETTINGS_STARTINGLEVEL_MINUS = "startinglevel_minus";
  
  private static final String SETTINGS_ORIENTATION = "orientation";
  
  public static final int BUTTON_CLOSE = 0;
  
  public static final int BUTTON_BACK = 1;
  
  private enum ButtonTypes {
    BACK, IMPORT_EXPORT_DIR, IMPORT_EXPORT_DIR_BUILDING, IMPORT_EXPORT_DIR_BUILDING_IMPORT, IMPORT_CULTURE, IMPORT_CULTURE_BUILDING, IMPORT_CULTURE_BUILDING_SUBDIR, IMPORT_CULTURE_BUILDING_IMPORT, REIMPORT, REIMPORTALL, EXPORT, EXPORTNEWLEVEL, CHANGE_PLAN, SETTINGS, NEWBUILDING, SETTINGS_CHANGE, NEWBUILDING_SETTING, NEWBUILDING_CREATE, EXPORT_COST;
  }
  
  private static class GuiButtonImportTable extends GuiText.MillGuiButton {
    private String value;
    
    private final GuiImportTable.ButtonTypes key;
    
    public GuiButtonImportTable(GuiImportTable.ButtonTypes key, String label) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
    }
    
    public GuiButtonImportTable(GuiImportTable.ButtonTypes key, String label, ItemStack icon) {
      super(label, 0, icon);
      this.key = key;
    }
    
    public GuiButtonImportTable(GuiImportTable.ButtonTypes key, String label, String value) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
      this.value = value;
    }
    
    public GuiButtonImportTable(GuiImportTable.ButtonTypes key, String label, String value, ItemStack icon) {
      super(label, 0, icon);
      this.key = key;
      this.value = value;
    }
    
    public GuiButtonImportTable(GuiImportTable.ButtonTypes key, String label, String value, GuiText.SpecialIcon icon) {
      super(label, 0, icon);
      this.key = key;
      this.value = value;
    }
  }
  
  private enum GUIScreen {
    HOME, SETTINGS, NEWBUILDING, IMPORT_CULTURE, IMPORT_CULTURE_SUBDIR, IMPORT_CULTURE_BUILDING, IMPORT_EXPORT_DIR, IMPORT_EXPORT_DIR_BUILDING;
  }
  
  private GUIScreen currentScreen = GUIScreen.HOME;
  
  private final List<GUIScreen> previousScreens = new ArrayList<>();
  
  private Culture currentCulture = null;
  
  private String currentBuildingKey = null;
  
  private String currentSubDirectory = null;
  
  private int newBuildingLength;
  
  private int newBuildingWidth;
  
  private int newBuildingStartLevel;
  
  private boolean newBuildingClearGround;
  
  private final EntityPlayer player;
  
  private final Point tablePos;
  
  private final TileEntityImportTable importTable;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");
  
  public GuiImportTable(EntityPlayer player, Point tablePos) {
    this.player = player;
    this.tablePos = tablePos;
    this.importTable = tablePos.getImportTable(player.world);
    this.currentBuildingKey = this.importTable.getBuildingKey();
    this.bookManager = new BookManager(256, 220, 175, 240, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (guibutton instanceof GuiButtonImportTable) {
      GuiButtonImportTable gb = (GuiButtonImportTable)guibutton;
      boolean close = false;
      if (gb.key == ButtonTypes.REIMPORT) {
        importBuildingPlan(BuildingImportExport.EXPORT_DIR, this.importTable.getBuildingKey(), this.importTable.getVariation(), this.importTable.getUpgradeLevel());
        close = true;
      } else if (gb.key == ButtonTypes.REIMPORTALL) {
        importBuildingPlanAll(BuildingImportExport.EXPORT_DIR, this.importTable.getBuildingKey());
        close = true;
      } else if (gb.key == ButtonTypes.EXPORT) {
        BuildingImportExport.importTableExportBuildingPlan(this.player.world, this.importTable, this.importTable.getUpgradeLevel());
        close = true;
      } else if (gb.key == ButtonTypes.EXPORTNEWLEVEL) {
        BuildingImportExport.importTableExportBuildingPlan(this.player.world, this.importTable, -1);
        close = true;
      } else if (gb.key == ButtonTypes.CHANGE_PLAN) {
        this.currentCulture = null;
        this.currentBuildingKey = null;
      } else if (gb.key == ButtonTypes.EXPORT_COST) {
        BuildingImportExport.importTableExportPlanCost(this.importTable.getBuildingKey());
        close = true;
      } else if (gb.key == ButtonTypes.SETTINGS) {
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.SETTINGS;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.SETTINGS_CHANGE) {
        adjustSetting(gb.value);
      } else if (gb.key == ButtonTypes.NEWBUILDING) {
        this.newBuildingLength = 10;
        this.newBuildingWidth = 10;
        this.newBuildingStartLevel = -1;
        this.newBuildingClearGround = true;
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.NEWBUILDING;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.NEWBUILDING_SETTING) {
        adjustNewBuildingSetting(gb.value);
      } else if (gb.key == ButtonTypes.NEWBUILDING_CREATE) {
        createNewBuilding();
        close = true;
      } else if (gb.key == ButtonTypes.IMPORT_CULTURE) {
        this.currentCulture = Culture.getCultureByName(gb.value);
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.IMPORT_CULTURE;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.IMPORT_CULTURE_BUILDING_SUBDIR) {
        this.currentSubDirectory = gb.value;
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.IMPORT_CULTURE_SUBDIR;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.IMPORT_CULTURE_BUILDING) {
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.IMPORT_CULTURE_BUILDING;
        this.currentBuildingKey = gb.value;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.IMPORT_EXPORT_DIR) {
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.IMPORT_EXPORT_DIR;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.IMPORT_EXPORT_DIR_BUILDING) {
        this.previousScreens.add(this.currentScreen);
        this.currentScreen = GUIScreen.IMPORT_EXPORT_DIR_BUILDING;
        this.currentBuildingKey = gb.value;
        this.pageNum = 0;
      } else if (gb.key == ButtonTypes.IMPORT_EXPORT_DIR_BUILDING_IMPORT) {
        if (gb.value.equals("all")) {
          importBuildingPlanAll(BuildingImportExport.EXPORT_DIR, this.currentBuildingKey);
        } else {
          int variation = Integer.parseInt(gb.value.split("_")[0]);
          int upgradeLevel = Integer.parseInt(gb.value.split("_")[1]);
          importBuildingPlan(BuildingImportExport.EXPORT_DIR, this.currentBuildingKey, variation, upgradeLevel);
        } 
        close = true;
      } else if (gb.key == ButtonTypes.IMPORT_CULTURE_BUILDING_IMPORT) {
        if (gb.value.equals("all")) {
          importBuildingPlanAll(this.currentCulture.key, this.currentBuildingKey);
        } else {
          int variation = Integer.parseInt(gb.value.split("_")[0]);
          int upgradeLevel = Integer.parseInt(gb.value.split("_")[1]);
          importBuildingPlan(this.currentCulture.key, this.currentBuildingKey, variation, upgradeLevel);
        } 
        close = true;
      } 
      if (close) {
        closeWindow();
      } else {
        this.textBook = getData();
        buttonPagination();
      } 
    } else if (guibutton.id == 0) {
      this.mc.displayGuiScreen(null);
      this.mc.setIngameFocus();
    } else if (guibutton.id == 1) {
      this.currentBuildingKey = null;
      this.currentScreen = this.previousScreens.get(this.previousScreens.size() - 1);
      this.previousScreens.remove(this.previousScreens.size() - 1);
      this.pageNum = 0;
      this.textBook = getData();
      buttonPagination();
    } 
    super.actionPerformed(guibutton);
  }
  
  private void adjustNewBuildingSetting(String value) {
    if (value.equals("clearground")) {
      this.newBuildingClearGround = !this.newBuildingClearGround;
    } else if (value.equals("length_minus")) {
      this.newBuildingLength = Math.max(this.newBuildingLength - 1, 1);
    } else if (value.equals("width_minus")) {
      this.newBuildingWidth = Math.max(this.newBuildingWidth - 1, 1);
    } else if (value.equals("length_plus")) {
      this.newBuildingLength++;
    } else if (value.equals("width_plus")) {
      this.newBuildingWidth++;
    } else if (value.equals("startinglevel_minus")) {
      this.newBuildingStartLevel--;
    } else if (value.equals("startinglevel_plus")) {
      this.newBuildingStartLevel++;
    } 
  }
  
  private void adjustSetting(String value) {
    if (value.equals("exportsnow")) {
      this.importTable.setExportSnow(!this.importTable.exportSnow());
    } else if (value.equals("importmockblocks")) {
      this.importTable.setImportMockBlocks(!this.importTable.importMockBlocks());
    } else if (value.equals("onverttopreserveground")) {
      this.importTable.setAutoconvertToPreserveGround(!this.importTable.autoconvertToPreserveGround());
    } else if (value.equals("exportregularchests")) {
      this.importTable.setExportRegularChests(!this.importTable.exportRegularChests());
    } else if (value.equals("orientation")) {
      int orientation = this.importTable.getOrientation() + 1;
      orientation %= 4;
      this.importTable.setOrientation(orientation);
    } else if (value.equals("startinglevel_minus")) {
      this.importTable.setStartingLevel(this.importTable.getStartingLevel() - 1);
    } else if (value.equals("startinglevel_plus")) {
      this.importTable.setStartingLevel(this.importTable.getStartingLevel() + 1);
    } 
    ClientSender.importTableUpdateSettings(this.tablePos, this.importTable.getUpgradeLevel(), this.importTable.getOrientation(), this.importTable.getStartingLevel(), this.importTable.exportSnow(), this.importTable
        .importMockBlocks(), this.importTable.autoconvertToPreserveGround(), this.importTable.exportRegularChests());
  }
  
  public void buttonPagination() {
    super.buttonPagination();
    int xStart = (this.width - getXSize()) / 2;
    int yStart = (this.height - getYSize()) / 2;
    this.buttonList.add(new GuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("hire.close")));
    if (this.currentScreen != GUIScreen.HOME && !this.previousScreens.isEmpty())
      this.buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("importtable.back"))); 
  }
  
  private void createNewBuilding() {
    ClientSender.importTableCreateNewBuilding(this.tablePos, this.newBuildingLength, this.newBuildingWidth, this.newBuildingStartLevel, this.newBuildingClearGround);
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  private TextBook getData() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("importtable.title"), "§1"));
    text.add(new TextLine());
    if (this.currentScreen == GUIScreen.HOME) {
      if (this.currentBuildingKey == null || this.currentBuildingKey.length() == 0) {
        text.addAll(getDataNoPlan());
      } else {
        text.addAll(getDataPlan());
      } 
    } else if (this.currentScreen == GUIScreen.SETTINGS) {
      text.addAll(getDataSettings());
    } else if (this.currentScreen == GUIScreen.NEWBUILDING) {
      text.addAll(getDataNewBuilding());
    } else if (this.currentScreen == GUIScreen.IMPORT_EXPORT_DIR) {
      text.addAll(getDataImportExportDirData());
    } else if (this.currentScreen == GUIScreen.IMPORT_EXPORT_DIR_BUILDING) {
      text.addAll(getDataImportExportDirBuilding());
    } else if (this.currentScreen == GUIScreen.IMPORT_CULTURE) {
      text.addAll(getDataImportCulture());
    } else if (this.currentScreen == GUIScreen.IMPORT_CULTURE_SUBDIR) {
      text.addAll(getDataImportCultureSubDirectories());
    } else if (this.currentScreen == GUIScreen.IMPORT_CULTURE_BUILDING) {
      text.addAll(getDataImportCultureBuilding());
    } 
    List<List<TextLine>> ftext = new ArrayList<>();
    ftext.add(text);
    return this.bookManager.convertAndAdjustLines(ftext);
  }
  
  private List<TextLine> getDataImportCulture() {
    List<TextLine> text = new ArrayList<>();
    List<String> subDirectories = new ArrayList<>();
    for (BuildingPlanSet planSet : this.currentCulture.ListPlanSets) {
      String subDirName = planSet.getFirstStartingPlan().getLoadedFromFile().getParentFile().getName();
      if (!subDirectories.contains(subDirName))
        subDirectories.add(subDirName); 
    } 
    for (String subDir : subDirectories)
      text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING_SUBDIR, subDir, subDir))); 
    return text;
  }
  
  private List<TextLine> getDataImportCultureBuilding() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING_IMPORT, LanguageUtilities.string("importtable.all"), "all")));
    BuildingPlanSet set = this.currentCulture.getBuildingPlanSet(this.currentBuildingKey);
    for (int variation = 0; variation < set.plans.size(); variation++) {
      for (int level = 0; level < ((BuildingPlan[])set.plans.get(variation)).length; level++) {
        BuildingPlan plan = ((BuildingPlan[])set.plans.get(variation))[level];
        String planName = plan.planName;
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING_IMPORT, planName, variation + "_" + level, plan.getIcon())));
      } 
    } 
    return text;
  }
  
  private List<TextLine> getDataImportCultureSubDirectories() {
    List<TextLine> text = new ArrayList<>();
    List<BuildingPlanSet> sortedPlans = new ArrayList<>(this.currentCulture.ListPlanSets);
    Collections.sort(sortedPlans, (p1, p2) -> p1.key.compareTo(p2.key));
    for (BuildingPlanSet planSet : sortedPlans) {
      if (planSet.getFirstStartingPlan().getLoadedFromFile().getParentFile().getName().equals(this.currentSubDirectory))
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING, planSet.key, planSet.key, planSet.getIcon()))); 
    } 
    return text;
  }
  
  private List<TextLine> getDataImportExportDirBuilding() {
    List<TextLine> text = new ArrayList<>();
    File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
    if (!exportDir.exists())
      exportDir.mkdirs(); 
    VirtualDir exportVirtualDir = new VirtualDir(exportDir);
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR_BUILDING_IMPORT, LanguageUtilities.string("importtable.all"), "all")));
    for (File file : exportVirtualDir.listFiles()) {
      if (file.getName().matches(this.currentBuildingKey + "_\\w(\\d\\d?)\\.png")) {
        String planKey = file.getName().substring(0, file.getName().length() - 4);
        String suffix = planKey.split("_")[(planKey.split("_")).length - 1].toUpperCase();
        int variation = suffix.charAt(0) - 65;
        int level = Integer.parseInt(suffix.substring(1, suffix.length()));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR_BUILDING_IMPORT, planKey, variation + "_" + level)));
      } 
    } 
    return text;
  }
  
  private List<TextLine> getDataImportExportDirData() {
    List<TextLine> text = new ArrayList<>();
    File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
    if (!exportDir.exists())
      exportDir.mkdirs(); 
    VirtualDir exportVirtualDir = new VirtualDir(exportDir);
    for (File file : exportVirtualDir.listFiles((FilenameFilter)new BuildingFileFiler("_A.txt"))) {
      String buildingKey = file.getName().substring(0, file.getName().length() - 6);
      text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR_BUILDING, buildingKey, buildingKey)));
    } 
    return text;
  }
  
  private List<TextLine> getDataNewBuilding() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("importtable.newbuilding")));
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("importtable.length", new String[] { "" + this.newBuildingLength })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.minus"), "length_minus", GuiText.SpecialIcon.MINUS), new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, 
            LanguageUtilities.string("importtable.plus"), "length_plus", GuiText.SpecialIcon.PLUS)));
    text.add(new TextLine(LanguageUtilities.string("importtable.width", new String[] { "" + this.newBuildingWidth })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.minus"), "width_minus", GuiText.SpecialIcon.MINUS), new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, 
            LanguageUtilities.string("importtable.plus"), "width_plus", GuiText.SpecialIcon.PLUS)));
    text.add(new TextLine(LanguageUtilities.string("importtable.startinglevel", new String[] { "" + this.newBuildingStartLevel })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.minus"), "startinglevel_minus", GuiText.SpecialIcon.MINUS), new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, 
            LanguageUtilities.string("importtable.plus"), "startinglevel_plus", GuiText.SpecialIcon.PLUS)));
    text.add(new TextLine(LanguageUtilities.string("importtable.clearground", new String[] { "" + this.newBuildingClearGround })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, "" + this.newBuildingClearGround, "clearground", new ItemStack(Items.IRON_SHOVEL, 1))));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_CREATE, LanguageUtilities.string("importtable.create"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1))));
    return text;
  }
  
  private List<TextLine> getDataNoPlan() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("importtable.nocurrentplan"), TextLine.ITALIC));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING, LanguageUtilities.string("importtable.newbuilding"), new ItemStack(Items.IRON_SHOVEL, 1))));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR, LanguageUtilities.string("importtable.importexportdir"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1))));
    text.add(new TextLine(LanguageUtilities.string("importtable.importfromculture"), TextLine.ITALIC));
    for (int i = 0; i < Culture.ListCultures.size(); i += 2) {
      if (i + 1 < Culture.ListCultures.size()) {
        Culture culture1 = Culture.ListCultures.get(i);
        Culture culture2 = Culture.ListCultures.get(i + 1);
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE, culture1.getAdjectiveTranslated(), culture1.key, culture1.getIcon()), new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE, culture2
                .getAdjectiveTranslated(), culture2.key, culture2.getIcon())));
      } else {
        Culture culture1 = Culture.ListCultures.get(i);
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE, culture1.getAdjectiveTranslated(), culture1.key, culture1.getIcon()), null));
      } 
    } 
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS, LanguageUtilities.string("importtable.settings"), new ItemStack(Items.REPEATER, 1)), null));
    return text;
  }
  
  private List<TextLine> getDataPlan() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("importtable.currentplan", new String[] { this.importTable.getBuildingKey() + "_" + (char)(65 + this.importTable.getVariation()) + this.importTable.getUpgradeLevel() })));
    text.add(new TextLine(LanguageUtilities.string("importtable.currentsize", new String[] { "" + this.importTable.getLength(), "" + this.importTable.getWidth(), "" + this.importTable.getStartingLevel() })));
    text.add(new TextLine());
    text.add(new TextLine());
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.REIMPORT, LanguageUtilities.string("importtable.reimport"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1)), new GuiButtonImportTable(ButtonTypes.REIMPORTALL, 
            LanguageUtilities.string("importtable.reimportall"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1))));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.EXPORT, LanguageUtilities.string("importtable.export"), new ItemStack((Item)MillItems.NEGATION_WAND, 1)), new GuiButtonImportTable(ButtonTypes.EXPORTNEWLEVEL, 
            LanguageUtilities.string("importtable.exportnewlevel"), new ItemStack((Item)MillItems.NEGATION_WAND, 1))));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.CHANGE_PLAN, LanguageUtilities.string("importtable.changeplan"), new ItemStack((Item)Items.MAP, 1)), new GuiButtonImportTable(ButtonTypes.SETTINGS, 
            LanguageUtilities.string("importtable.settings"), new ItemStack(Items.REPEATER, 1))));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.EXPORT_COST, LanguageUtilities.string("importtable.exportcost"), new ItemStack((Item)MillItems.PURSE, 1)), null));
    return text;
  }
  
  private List<TextLine> getDataSettings() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(LanguageUtilities.string("importtable.settings")));
    text.add(new TextLine());
    text.add(new TextLine(LanguageUtilities.string("importtable.orientation", new String[] { BuildingPlan.FACING_KEYS[this.importTable.getOrientation()] })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, BuildingPlan.FACING_KEYS[this.importTable.getOrientation()], "orientation")));
    text.add(new TextLine(LanguageUtilities.string("importtable.startinglevel", new String[] { "" + this.importTable.getStartingLevel() })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, LanguageUtilities.string("importtable.minus"), "startinglevel_minus"), new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, 
            LanguageUtilities.string("importtable.plus"), "startinglevel_plus")));
    text.add(new TextLine(LanguageUtilities.string("importtable.exportsnow", new String[] { "" + this.importTable.exportSnow() })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.exportSnow(), "exportsnow")));
    text.add(new TextLine(LanguageUtilities.string("importtable.exportregularchests", new String[] { "" + this.importTable.exportRegularChests() })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.exportRegularChests(), "exportregularchests")));
    text.add(new TextLine(LanguageUtilities.string("importtable.importmockblocks", new String[] { "" + this.importTable.importMockBlocks() })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.importMockBlocks(), "importmockblocks")));
    text.add(new TextLine(LanguageUtilities.string("importtable.converttopreserveground", new String[] { "" + this.importTable.autoconvertToPreserveGround() })));
    text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.autoconvertToPreserveGround(), "onverttopreserveground")));
    return text;
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  private void importBuildingPlan(String source, String buildingKey, int variation, int targetLevel) {
    ClientSender.importTableImportBuildingPlan(this.player, this.tablePos, source, buildingKey, false, variation, targetLevel, this.importTable.getOrientation(), this.importTable.importMockBlocks());
  }
  
  private void importBuildingPlanAll(String source, String buildingKey) {
    Point parentTablePos;
    if (this.importTable.getParentTablePos() != null) {
      parentTablePos = this.importTable.getParentTablePos();
    } else {
      parentTablePos = this.tablePos;
    } 
    ClientSender.importTableImportBuildingPlan(this.player, parentTablePos, source, buildingKey, true, 0, 0, this.importTable.getOrientation(), this.importTable.importMockBlocks());
  }
  
  public void initData() {
    refreshContent();
  }
  
  private void refreshContent() {
    this.textBook = getData();
    buttonPagination();
  }
}
