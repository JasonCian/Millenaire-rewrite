package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.text.WordUtils;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.buildingplan.PointType;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGenericCrafting;
import org.millenaire.common.goal.generic.GoalGenericHarvestCrop;
import org.millenaire.common.goal.generic.GoalGenericMining;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemFoodMultiple;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.world.UserProfile;

public class BookManagerTravelBook extends BookManager {
  private static final int VILLAGER_PICTURE_OFFSET = 80;
  
  private static final String EXPORT_TAG_MAIN_DESC = "MAIN_DESC";
  
  private static final String[] VILLAGER_TAGS_TO_DISPLAY = new String[] { "chief", "foreignmerchant", "localmerchant", "helpinattacks", "hostile", "raider", "archer" };
  
  private static final Map<String, ItemStack> VILLAGER_TAGS_ICONS = new HashMap<String, ItemStack>() {
      private static final long serialVersionUID = 1L;
    };
  
  private static final String[] BUILDING_TAGS_TO_DISPLAY = new String[] { "archives", "hof", "leasure", "pujas", "sacrifices" };
  
  private static final Map<String, ItemStack> BUILDING_TAGS_ICONS = new HashMap<String, ItemStack>() {
      private static final long serialVersionUID = 1L;
    };
  
  private static <T> void incrementMap(Map<T, Integer> map, T key, int value) {
    if (map.containsKey(key)) {
      map.put(key, Integer.valueOf(((Integer)map.get(key)).intValue() + value));
    } else {
      map.put(key, Integer.valueOf(value));
    } 
  }
  
  public BookManagerTravelBook(int xSize, int ySize, int textHeight, int lineSizeInPx, BookManager.IFontRendererWrapper fontRenderer) {
    super(xSize, ySize, textHeight, lineSizeInPx, fontRenderer);
  }
  
  public TextBook getBookBuildingDetail(Culture culture, String itemKey, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    BuildingPlanSet planSet = culture.getBuildingPlanSet(itemKey);
    TextLine line = new TextLine(planSet.getNameNativeAndTranslated(), "§1", planSet.getIcon(), false);
    page.addLine(line);
    (page.getLastLine()).canCutAfter = false;
    page.addBlankLine();
    (page.getLastLine()).canCutAfter = false;
    boolean knownBuilding = (profile == null || profile.isBuildingUnlocked(culture, planSet));
    boolean displayFullInfos = (knownBuilding || !MillConfigValues.TRAVEL_BOOK_LEARNING);
    if (!knownBuilding) {
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknownbuilding"), "§4"));
      page.addBlankLine();
    } 
    if (displayFullInfos) {
      if ((planSet.getFirstStartingPlan()).isSubBuilding) {
        if ((planSet.getFirstStartingPlan()).parentBuildingPlan != null && culture.getBuildingPlan((planSet.getFirstStartingPlan()).parentBuildingPlan) != null) {
          BuildingPlan parentPlan = culture.getBuildingPlan((planSet.getFirstStartingPlan()).parentBuildingPlan);
          BuildingPlanSet parentSet = culture.getBuildingPlanSet(parentPlan.buildingKey);
          page.addLine(LanguageUtilities.string("travelbook.subbuildingof", new String[] { parentPlan.getNameNativeAndTranslated() }), "§4", new GuiText.GuiButtonReference(parentSet));
        } else {
          page.addLine(LanguageUtilities.string("travelbook.subbuilding"), "§4");
        } 
        page.addBlankLine();
      } 
      if (culture.hasCultureString("travelbook.building." + planSet.key + ".desc")) {
        page.addLine(culture.getCultureString("travelbook.building." + planSet.key + ".desc"));
        (page.getLastLine()).exportSpecialTag = "MAIN_DESC";
        page.addBlankLine();
      } 
      for (int variation = 0; variation < planSet.plans.size(); variation++)
        getBookBuildingDetail_exportVariation(culture, page, planSet, variation); 
    } 
    List<TextLine> infoColumns = new ArrayList<>();
    for (VillageType village : culture.listVillageTypes) {
      if (village.centreBuilding == planSet || village.startBuildings.contains(planSet) || village.coreBuildings.contains(planSet) || village.secondaryBuildings.contains(planSet) || village.extraBuildings
        .contains(planSet))
        infoColumns.add(new TextLine(village.name, new GuiText.GuiButtonReference(village))); 
    } 
    if (infoColumns.size() > 0) {
      page.addBlankLine();
      page.addLine(LanguageUtilities.string("travelbook.villageswithbuilding"), "§1");
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
      page.addBlankLine();
    } 
    book.addPage(page);
    book = offsetFirstLines(book);
    return book;
  }
  
  private void getBookBuildingDetail_exportVariation(Culture culture, TextPage page, BuildingPlanSet planSet, int variation) {
    if (planSet.plans.size() > 1) {
      page.addLine(LanguageUtilities.string("travelbook.variation", new String[] { "" + (char)(65 + variation) }), "§1");
      (page.getLastLine()).canCutAfter = false;
      page.addBlankLine();
      (page.getLastLine()).canCutAfter = false;
    } 
    BuildingPlan plan = planSet.getPlan(variation, 0);
    getBookBuildingDetail_exportVariationBasicInfos(culture, page, plan);
    for (int level = 0; level < ((BuildingPlan[])planSet.plans.get(variation)).length; level++)
      getBookBuildingDetail_exportVariationLevel(culture, page, planSet, variation, level); 
    page.addBlankLine();
  }
  
  private void getBookBuildingDetail_exportVariationBasicInfos(Culture culture, TextPage page, BuildingPlan plan) {
    List<TextLine> infoColumns = new ArrayList<>();
    infoColumns.add(new TextLine(plan.length + "x" + plan.width, new ItemStack((Block)Blocks.GRASS, 1), LanguageUtilities.string("travelbook.building_size"), false));
    if (plan.shop != null)
      infoColumns.add(new TextLine(culture.getCultureString("shop." + plan.shop), new ItemStack((Item)MillItems.PURSE, 1), LanguageUtilities.string("travelbook.building_shop"), false)); 
    if (plan.price > 0) {
      String priceText = MillCommonUtilities.getShortPrice(plan.price);
      infoColumns.add(new TextLine(priceText, new ItemStack((Item)MillItems.DENIER_OR, 1), LanguageUtilities.string("travelbook.building_cost"), false));
    } 
    if (plan.reputation > 0)
      infoColumns.add(new TextLine("" + plan.reputation, new ItemStack((Block)Blocks.RED_FLOWER, 1), LanguageUtilities.string("travelbook.building_reputation"), false)); 
    if (plan.isgift)
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.building_gift"), new ItemStack(Items.CAKE, 1), LanguageUtilities.string("travelbook.building_gift_desc"), false)); 
    for (String tag : BUILDING_TAGS_TO_DISPLAY) {
      if (plan.containsTags(tag)) {
        TextLine col1 = new TextLine(LanguageUtilities.string("travelbook.specialbuildingtag." + tag), BUILDING_TAGS_ICONS.get(tag), LanguageUtilities.string("travelbook.specialbuildingtag." + tag + ".desc"), false);
        infoColumns.add(col1);
      } 
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    page.addBlankLine();
    if (plan.maleResident.size() > 0 || plan.femaleResident.size() > 0) {
      infoColumns = new ArrayList<>();
      page.addLine(LanguageUtilities.string("travelbook.residents"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      for (String maleResident : plan.maleResident) {
        VillagerType resident = culture.getVillagerType(maleResident);
        if (resident != null)
          infoColumns.add(new TextLine(resident.name, new GuiText.GuiButtonReference(resident))); 
      } 
      for (String femaleResident : plan.femaleResident) {
        VillagerType resident = culture.getVillagerType(femaleResident);
        if (resident != null)
          infoColumns.add(new TextLine(resident.name, new GuiText.GuiButtonReference(resident))); 
      } 
      linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
      page.addBlankLine();
    } 
    if (plan.startingSubBuildings.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.startingsubbuildings"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      infoColumns = new ArrayList<>();
      for (String subBuildingKey : plan.startingSubBuildings) {
        BuildingPlanSet subBuildingSet = culture.getBuildingPlanSet(subBuildingKey);
        infoColumns.add(new TextLine(subBuildingSet.getNameNative(), new GuiText.GuiButtonReference(subBuildingSet)));
      } 
      linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
    if (plan.startingGoods.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.startinggoods"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      infoColumns = new ArrayList<>();
      for (BuildingPlan.StartingGood good : plan.startingGoods) {
        int min;
        if (good.probability >= 1.0D) {
          min = good.fixedNumber;
        } else {
          min = 0;
        } 
        int max = good.fixedNumber + good.randomNumber;
        infoColumns.add(new TextLine(min + "-" + max, good.item.getItemStack(), true));
      } 
      linesWithColumns = BookManager.splitInColumns(infoColumns, 4);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
  }
  
  private void getBookBuildingDetail_exportVariationLevel(Culture culture, TextPage page, BuildingPlanSet planSet, int variation, int level) {
    BuildingPlan plan = planSet.getPlan(variation, level);
    BuildingPlan previousPlan = null;
    if (level > 0)
      previousPlan = planSet.getPlan(variation, level - 1); 
    if (level == 0) {
      page.addLine(LanguageUtilities.string("travelbook.initial"), "§1");
    } else {
      page.addLine(LanguageUtilities.string("travelbook.upgrade", new String[] { "" + level }), "§1");
    } 
    (page.getLastLine()).canCutAfter = false;
    List<TextLine> infoColumns = new ArrayList<>();
    if (plan.shop != null && level > 0 && previousPlan.shop == null)
      infoColumns.add(new TextLine(culture.getCultureString("shop." + plan.shop), new ItemStack((Item)MillItems.PURSE, 1), LanguageUtilities.string("travelbook.building_shop"), false)); 
    if (plan.irrigation > 0 && level > 0 && previousPlan.irrigation != plan.irrigation)
      infoColumns.add(new TextLine("+" + plan.irrigation + "%", new ItemStack(Items.WATER_BUCKET, 1), LanguageUtilities.string("effect.irrigation", new String[] { "" + plan.irrigation }), false)); 
    if (plan.extraSimultaneousConstructions > 0 && level > 0 && previousPlan.extraSimultaneousConstructions != plan.extraSimultaneousConstructions)
      infoColumns.add(new TextLine("+" + plan.extraSimultaneousConstructions, new ItemStack(Items.IRON_SHOVEL, 1), LanguageUtilities.string("effect.extraconstructionslot", new String[] { "" + plan.extraSimultaneousConstructions }), false)); 
    if (plan.extraSimultaneousWallConstructions > 0 && level > 0 && previousPlan.extraSimultaneousWallConstructions != plan.extraSimultaneousWallConstructions)
      infoColumns.add(new TextLine("+" + plan.extraSimultaneousWallConstructions, new ItemStack(Blocks.COBBLESTONE_WALL, 1), LanguageUtilities.string("effect.extrawallconstructionslot", new String[] { "" + plan.extraSimultaneousWallConstructions }), false)); 
    getBookBuildingDetail_loadInfosFromBlocks(plan, infoColumns);
    if (infoColumns.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.features"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> list = BookManager.splitInColumns(infoColumns, 6);
      for (TextLine l : list)
        page.addLine(l); 
    } 
    if (plan.subBuildings.size() > 0 && (previousPlan == null || previousPlan.subBuildings.size() < plan.subBuildings.size())) {
      infoColumns = new ArrayList<>();
      for (String subBuildingKey : plan.subBuildings) {
        if (previousPlan == null || !previousPlan.subBuildings.contains(subBuildingKey)) {
          BuildingPlanSet subBuildingSet = culture.getBuildingPlanSet(subBuildingKey);
          infoColumns.add(new TextLine(subBuildingSet.getNameNative(), new GuiText.GuiButtonReference(subBuildingSet)));
        } 
      } 
      if (infoColumns.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.subbuildings"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
    } 
    if (plan.visitors.size() > 0 && (previousPlan == null || previousPlan.visitors.size() < plan.visitors.size())) {
      infoColumns = new ArrayList<>();
      for (String visitor : plan.visitors) {
        if (previousPlan == null || !previousPlan.visitors.contains(visitor)) {
          VillagerType visitorType = culture.getVillagerType(visitor);
          if (visitorType != null)
            infoColumns.add(new TextLine(visitorType.name, new GuiText.GuiButtonReference(visitorType))); 
        } 
      } 
      if (infoColumns.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.visitors"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
    } 
    page.addLine(LanguageUtilities.string("travelbook.cost"), TextLine.ITALIC);
    (page.getLastLine()).canCutAfter = false;
    List<TextLine> costColumns = new ArrayList<>();
    List<InvItem> costKeys = (List<InvItem>)plan.resCost.keySet().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
    for (InvItem key : costKeys) {
      if (culture.getTradeGood(key) != null) {
        costColumns.add(new TextLine("" + plan.resCost.get(key), new GuiText.GuiButtonReference(culture.getTradeGood(key))));
        continue;
      } 
      costColumns.add(new TextLine("" + plan.resCost.get(key), key.getItemStack(), true));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(costColumns, 5);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    page.addBlankLine();
  }
  
  private void getBookBuildingDetail_loadInfosFromBlocks(BuildingPlan plan, List<TextLine> infoColumns) {
    int nbChests = 0;
    int nbFishingSpot = 0, nbFurnace = 0, nbFirePits = 0;
    Map<InvItem, Integer> plantingSpot = new HashMap<>();
    Map<InvItem, Integer> resourceSpot = new HashMap<>();
    Map<InvItem, Integer> spawnSpot = new HashMap<>();
    for (int y = 0; y < plan.plan.length; y++) {
      for (int x = 0; x < (plan.plan[y]).length; x++) {
        for (int z = 0; z < (plan.plan[y][x]).length; z++) {
          PointType pt = plan.plan[y][x][z];
          if (pt.getBlock() instanceof net.minecraft.block.BlockChest) {
            nbChests++;
          } else if (pt.getBlock() == Blocks.FURNACE) {
            nbFurnace++;
          } else if (pt.getBlock() == MillBlocks.FIRE_PIT) {
            nbFirePits++;
          } else if (pt.getSpecialType() != null) {
            if (pt.isSubType("lockedchest") || pt.isSubType("mainchest")) {
              nbChests++;
            } else if (pt.isType("fishingspot")) {
              nbFishingSpot++;
            } else if (pt.isType("furnaceGuess")) {
              nbFurnace++;
            } else if (pt.isType("soil")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Items.WHEAT), 1);
            } else if (pt.isType("ricesoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem((Item)MillItems.RICE), 1);
            } else if (pt.isType("turmericsoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem((Item)MillItems.TURMERIC), 1);
            } else if (pt.isType("maizesoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem((Item)MillItems.MAIZE), 1);
            } else if (pt.isType("carrotsoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Items.CARROT), 1);
            } else if (pt.isType("potatosoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Items.POTATO), 1);
            } else if (pt.isType("flowersoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem((Block)Blocks.RED_FLOWER), 1);
            } else if (pt.isType("sugarcanesoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Items.REEDS), 1);
            } else if (pt.isType("netherwartsoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Items.NETHER_WART), 1);
            } else if (pt.isType("vinesoil")) {
              incrementMap(plantingSpot, InvItem.createInvItem((Item)MillItems.GRAPES), 1);
            } else if (pt.isType("cacaospot")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Items.DYE, 3), 1);
            } else if (pt.isType("oakspawn")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.OAK)), 1);
            } else if (pt.isType("pinespawn")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.SPRUCE)), 1);
            } else if (pt.isType("birchspawn")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.BIRCH)), 1);
            } else if (pt.isType("junglespawn")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.JUNGLE)), 1);
            } else if (pt.isType("acaciaspawn")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.ACACIA)), 1);
            } else if (pt.isType("darkoakspawn")) {
              incrementMap(plantingSpot, InvItem.createInvItem(Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.DARK_OAK)), 1);
            } else if (pt.isType("chickenspawn")) {
              incrementMap(spawnSpot, InvItem.createInvItem(Items.EGG), 1);
            } else if (pt.isType("cowspawn")) {
              incrementMap(spawnSpot, InvItem.createInvItem(Items.BEEF), 1);
            } else if (pt.isType("pigspawn")) {
              incrementMap(spawnSpot, InvItem.createInvItem(Items.PORKCHOP), 1);
            } else if (pt.isType("squidspawn")) {
              incrementMap(spawnSpot, InvItem.createInvItem(Items.DYE, 0), 1);
            } else if (pt.isType("sheepspawn")) {
              incrementMap(spawnSpot, InvItem.createInvItem(Blocks.WOOL), 1);
            } else if (pt.isType("wolfspawn")) {
              incrementMap(spawnSpot, InvItem.createInvItem(Items.BONE), 1);
            } else if (pt.isType("silkwormblock")) {
              incrementMap(resourceSpot, InvItem.createInvItem((Item)MillItems.SILK), 1);
            } else if (pt.isType("brickspot")) {
              incrementMap(resourceSpot, InvItem.createInvItem(MillBlocks.BS_MUD_BRICK), 1);
            } else if (pt.isType("stonesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.STONE), 1);
            } else if (pt.isType("sandsource")) {
              incrementMap(resourceSpot, InvItem.createInvItem((Block)Blocks.SAND), 1);
            } else if (pt.isType("sandstonesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.SANDSTONE), 1);
            } else if (pt.isType("claysource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Items.CLAY_BALL), 1);
            } else if (pt.isType("gravelsource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.GRAVEL), 1);
            } else if (pt.isType("granitesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.GRANITE)), 1);
            } else if (pt.isType("dioritesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.DIORITE)), 1);
            } else if (pt.isType("andesitesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.ANDESITE)), 1);
            } else if (pt.isType("redsandstonesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.RED_SANDSTONE), 1);
            } else if (pt.isType("quartzsource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.QUARTZ_ORE), 1);
            } else if (pt.isType("snowsource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.SNOW), 1);
            } else if (pt.isType("icesource")) {
              incrementMap(resourceSpot, InvItem.createInvItem(Blocks.ICE), 1);
            } 
          } 
        } 
      } 
    } 
    if (nbChests > 0)
      infoColumns.add(new TextLine("" + nbChests, new ItemStack((Block)Blocks.CHEST, 1), LanguageUtilities.string("travelbook.nbchests"), false)); 
    if (nbFurnace > 0)
      infoColumns.add(new TextLine("" + nbFurnace, new ItemStack(Blocks.FURNACE, 1), LanguageUtilities.string("travelbook.nbfurnaces"), false)); 
    if (nbFirePits > 0)
      infoColumns.add(new TextLine("" + nbFirePits, new ItemStack((Block)MillBlocks.FIRE_PIT, 1), LanguageUtilities.string("travelbook.nbfirepits"), false)); 
    if (nbFishingSpot > 0)
      infoColumns.add(new TextLine("" + nbFishingSpot, new ItemStack((Item)Items.FISHING_ROD, 1), LanguageUtilities.string("travelbook.nbfishingspot"), false)); 
    for (InvItem key : plantingSpot.keySet()) {
      infoColumns.add(new TextLine("" + plantingSpot.get(key), key.getItemStack(), LanguageUtilities.string("travelbook.plantingspot", new String[] { key.getName() }), false));
    } 
    for (InvItem key : resourceSpot.keySet()) {
      infoColumns.add(new TextLine("" + resourceSpot.get(key), key.getItemStack(), LanguageUtilities.string("travelbook.resourcespot", new String[] { key.getName() }), false));
    } 
    for (InvItem key : spawnSpot.keySet())
      infoColumns.add(new TextLine("" + spawnSpot.get(key), key.getItemStack(), LanguageUtilities.string("travelbook.spawnspot"), false)); 
  }
  
  public TextBook getBookBuildingsList(Culture culture, String category, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(LanguageUtilities.string("travelbook.buildingslist", new String[] { culture.getAdjectiveTranslated() }), "§1", culture.getIcon(), false);
    page.addLine(LanguageUtilities.string("travelbook.buildingslistcategory", new String[] { culture.getCategoryName(category) }), culture.getCategoryIcon(category), false);
    page.addBlankLine();
    List<BuildingPlanSet> sortedPlans = getCurrentBuildingList(culture, category);
    int nbKnownBuildings = profile.getNbUnlockedBuildings(culture, category);
    page.addLine(LanguageUtilities.string("travelbook.buildingslistcategory_unlocked", new String[] { "" + nbKnownBuildings, "" + sortedPlans.size() }));
    page.addBlankLine();
    List<TextLine> infoColumns = new ArrayList<>();
    for (BuildingPlanSet planSet : sortedPlans) {
      String style = "";
      if (!profile.isBuildingUnlocked(culture, planSet))
        style = TextLine.ITALIC; 
      infoColumns.add(new TextLine(planSet.getNameNativeAndTranslated(), style, new GuiText.GuiButtonReference(planSet)));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    book.addPage(page);
    return book;
  }
  
  public TextBook getBookCulture(Culture culture, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(culture.getNameTranslated(), "§1", culture.getIcon(), false);
    page.addBlankLine();
    if (culture.hasCultureString("travelbook.culture.desc")) {
      page.addLine(culture.getCultureString("travelbook.culture.desc"));
      page.addBlankLine();
    } 
    int nbTotal = ((List)culture.listVillagerTypes.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
    int nbKnown = profile.getNbUnlockedVillagers(culture);
    page.addLine(LanguageUtilities.string("travelbook.villagers"), "§1");
    (page.getLastLine()).canCutAfter = false;
    page.addLine(LanguageUtilities.string("travelbook.villagerslist_unlocked", new String[] { "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated() }), TextLine.ITALIC);
    (page.getLastLine()).canCutAfter = false;
    int i;
    for (i = 0; i < culture.travelBookVillagerCategories.size(); i += 2) {
      GuiTravelBook.GuiButtonTravelBook button2 = null;
      String category = culture.travelBookVillagerCategories.get(i);
      ItemStack icon = culture.getCategoryIcon(category);
      GuiTravelBook.GuiButtonTravelBook button1 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_VILLAGERS, culture.getCategoryName(category), category, icon);
      if (i + 1 < culture.travelBookVillagerCategories.size()) {
        category = culture.travelBookVillagerCategories.get(i + 1);
        icon = culture.getCategoryIcon(category);
        button2 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_VILLAGERS, culture.getCategoryName(category), category, icon);
      } 
      page.addLine(new TextLine((GuiText.MillGuiButton)button1, (GuiText.MillGuiButton)button2));
    } 
    page.addBlankLine();
    nbTotal = ((List)culture.listVillageTypes.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
    nbKnown = profile.getNbUnlockedVillages(culture);
    page.addLine(LanguageUtilities.string("travelbook.villages"), "§1");
    (page.getLastLine()).canCutAfter = false;
    page.addLine(LanguageUtilities.string("travelbook.villageslist_unlocked", new String[] { "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated() }), TextLine.ITALIC);
    (page.getLastLine()).canCutAfter = false;
    page.addLine(new TextLine((GuiText.MillGuiButton)new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_VILLAGES, LanguageUtilities.string("travelbook.villages"), new ItemStack((Item)Items.MAP, 1)), null));
    page.addBlankLine();
    nbTotal = ((List)culture.ListPlanSets.stream().filter(p -> (p.getFirstStartingPlan()).travelBookDisplay).collect(Collectors.toList())).size();
    nbKnown = profile.getNbUnlockedBuildings(culture);
    page.addLine(LanguageUtilities.string("travelbook.buildings"), "§1");
    (page.getLastLine()).canCutAfter = false;
    page.addLine(LanguageUtilities.string("travelbook.buildingslist_unlocked", new String[] { "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated() }), TextLine.ITALIC);
    (page.getLastLine()).canCutAfter = false;
    for (i = 0; i < culture.travelBookBuildingCategories.size(); i += 2) {
      GuiTravelBook.GuiButtonTravelBook button2 = null;
      String category = culture.travelBookBuildingCategories.get(i);
      ItemStack icon = culture.getCategoryIcon(category);
      GuiTravelBook.GuiButtonTravelBook button1 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_BUILDINGS, culture.getCategoryName(category), category, icon);
      if (i + 1 < culture.travelBookBuildingCategories.size()) {
        category = culture.travelBookBuildingCategories.get(i + 1);
        icon = culture.getCategoryIcon(category);
        button2 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_BUILDINGS, culture.getCategoryName(category), category, icon);
      } 
      page.addLine(new TextLine((GuiText.MillGuiButton)button1, (GuiText.MillGuiButton)button2));
    } 
    page.addBlankLine();
    nbTotal = ((List)culture.goodsList.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
    nbKnown = profile.getNbUnlockedTradeGoods(culture);
    page.addLine(LanguageUtilities.string("travelbook.tradegoods"), "§1");
    (page.getLastLine()).canCutAfter = false;
    page.addLine(LanguageUtilities.string("travelbook.tradegoodslist_unlocked", new String[] { "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated() }), TextLine.ITALIC);
    (page.getLastLine()).canCutAfter = false;
    for (i = 0; i < culture.travelBookTradeGoodCategories.size(); i += 2) {
      GuiTravelBook.GuiButtonTravelBook button2 = null;
      String category = culture.travelBookTradeGoodCategories.get(i);
      ItemStack icon = culture.getCategoryIcon(category);
      GuiTravelBook.GuiButtonTravelBook button1 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_TRADE_GOODS, culture.getCategoryName(category), category, icon);
      if (i + 1 < culture.travelBookTradeGoodCategories.size()) {
        category = culture.travelBookTradeGoodCategories.get(i + 1);
        icon = culture.getCategoryIcon(category);
        button2 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_TRADE_GOODS, culture.getCategoryName(category), category, icon);
      } 
      page.addLine(new TextLine((GuiText.MillGuiButton)button1, (GuiText.MillGuiButton)button2));
    } 
    book.addPage(page);
    return book;
  }
  
  public TextBook getBookCultureForJSONExport(Culture culture, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(culture.getNameTranslated(), "§1", culture.getIcon(), false);
    page.addBlankLine();
    if (culture.hasCultureString("travelbook.culture.desc")) {
      page.addLine(culture.getCultureString("travelbook.culture.desc"));
      (page.getLastLine()).exportSpecialTag = "MAIN_DESC";
      page.addBlankLine();
    } 
    book.addPage(page);
    return book;
  }
  
  public TextBook getBookHome(UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(LanguageUtilities.string("travelbook.title"), "§1");
    page.addBlankLine();
    int nbKnownCultures = profile.getNbUnlockedCultures();
    page.addLine(LanguageUtilities.string("travelbook.culture_unlocked", new String[] { "" + nbKnownCultures, "" + Culture.ListCultures.size() }));
    page.addBlankLine();
    List<TextLine> infoColumns = new ArrayList<>();
    for (Culture culture : Culture.ListCultures) {
      String style = "";
      if (!profile.isCultureUnlocked(culture))
        style = TextLine.ITALIC; 
      infoColumns.add(new TextLine(culture.getNameTranslated(), style, new GuiText.GuiButtonReference(culture)));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    if (MillConfigValues.TRAVEL_BOOK_LEARNING) {
      page.addBlankLine();
      page.addLine(LanguageUtilities.string("travelbook.contentlocked"));
      page.addLine(LanguageUtilities.string("travelbook.learningsetting"), "§1");
    } else {
      page.addBlankLine();
      page.addLine(LanguageUtilities.string("travelbook.contentunlocked"));
      page.addLine(LanguageUtilities.string("travelbook.learningsetting_off"), "§1");
    } 
    book.addPage(page);
    return book;
  }
  
  public TextBook getBookTradeGoodDetail(Culture culture, String itemKey, UserProfile profile) {
    TradeGood tradeGood = culture.getTradeGood(itemKey);
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    TextLine line = new TextLine(tradeGood.getName(), "§1", tradeGood.getIcon(), false);
    page.addLine(line);
    (page.getLastLine()).canCutAfter = false;
    page.addBlankLine();
    boolean knownTradeGood = (profile == null || profile.isTradeGoodUnlocked(culture, tradeGood));
    boolean displayFullInfos = (knownTradeGood || !MillConfigValues.TRAVEL_BOOK_LEARNING);
    if (!knownTradeGood) {
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknowntradegood"), "§4"));
      page.addBlankLine();
    } 
    if (displayFullInfos) {
      if (culture.hasCultureString("travelbook.trade_good." + tradeGood.key + ".desc")) {
        page.addLine(culture.getCultureString("travelbook.trade_good." + tradeGood.key + ".desc"));
        (page.getLastLine()).exportSpecialTag = "MAIN_DESC";
        page.addBlankLine();
      } else if (LanguageUtilities.hasString("travelbook.trade_good." + tradeGood.key + ".desc")) {
        page.addLine(LanguageUtilities.string("travelbook.trade_good." + tradeGood.key + ".desc"));
        (page.getLastLine()).exportSpecialTag = "MAIN_DESC";
        page.addBlankLine();
      } 
      getBookTradeGoodDetail_basicInfos(page, tradeGood);
      getBookTradeGoodDetail_shops(culture, page, tradeGood);
      page.addBlankLine();
      getBookTradeGoodDetail_goalsInfo(culture, page, tradeGood);
      getBookTradeGoodDetail_villageUse(culture, page, tradeGood);
    } 
    book.addPage(page);
    book = offsetFirstLines(book);
    return book;
  }
  
  private void getBookTradeGoodDetail_basicInfos(TextPage page, TradeGood tradeGood) {
    List<TextLine> infoColumns = new ArrayList<>();
    if (tradeGood.getBasicBuyingPrice(null) > 0)
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_buying_price", new String[] { "" + MillCommonUtilities.getShortPrice(tradeGood.getBasicBuyingPrice(null)) }), new ItemStack((Item)MillItems.DENIER, 1), 
            LanguageUtilities.string("travelbook.trade_good_buying_price.desc"), false)); 
    if (tradeGood.getBasicSellingPrice(null) > 0)
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_selling_price", new String[] { MillCommonUtilities.getShortPrice(tradeGood.getBasicSellingPrice(null)) }), new ItemStack((Item)MillItems.DENIER_ARGENT, 1), 
            LanguageUtilities.string("travelbook.trade_good_selling_price.desc"), false)); 
    if (tradeGood.foreignMerchantPrice > 0)
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_market_price", new String[] { MillCommonUtilities.getShortPrice(tradeGood.foreignMerchantPrice) }), new ItemStack((Item)MillItems.DENIER_OR, 1), 
            LanguageUtilities.string("travelbook.trade_good_market_price.desc"), false)); 
    if (tradeGood.minReputation > 0)
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_min_reputation", new String[] { "" + tradeGood.minReputation }), new ItemStack((Block)Blocks.RED_FLOWER, 1), 
            LanguageUtilities.string("travelbook.trade_good_min_reputation.desc"), false)); 
    if (tradeGood.autoGenerate)
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_autogenerated"), new ItemStack(Blocks.DIRT, 1), 
            LanguageUtilities.string("travelbook.trade_good_autogenerated_desc"), false)); 
    if (tradeGood.item.item instanceof ItemTool) {
      ItemTool tool = (ItemTool)tradeGood.item.item;
      Block testBlock = null;
      if (tool instanceof net.minecraft.item.ItemSpade) {
        testBlock = Blocks.DIRT;
      } else if (tool instanceof ItemPickaxe) {
        testBlock = Blocks.STONE;
      } else if (tool instanceof net.minecraft.item.ItemAxe) {
        testBlock = Blocks.LOG;
      } 
      if (testBlock != null)
        infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_toolefficiency", new String[] { "" + tool.getDestroySpeed(tradeGood.item.getItemStack(), testBlock.getDefaultState()) }), new ItemStack(Items.IRON_PICKAXE, 1), 
              LanguageUtilities.string("travelbook.trade_good_toolefficiency.desc"), false)); 
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_durability", new String[] { "" + tool.getMaxDamage() }), new ItemStack(Blocks.ANVIL, 1), 
            LanguageUtilities.string("travelbook.trade_good_durability.desc"), false));
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_enchantability", new String[] { "" + tool.getItemEnchantability() }), new ItemStack(Blocks.ENCHANTING_TABLE, 1), 
            LanguageUtilities.string("travelbook.trade_good_enchantability.desc"), false));
    } 
    if (tradeGood.item.item instanceof ItemSword) {
      ItemSword sword = (ItemSword)tradeGood.item.item;
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_durability", new String[] { "" + sword.getMaxDamage() }), new ItemStack(Blocks.ANVIL, 1), 
            LanguageUtilities.string("travelbook.trade_good_durability.desc"), false));
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_enchantability", new String[] { "" + sword.getItemEnchantability() }), new ItemStack(Blocks.ENCHANTING_TABLE, 1), 
            LanguageUtilities.string("travelbook.trade_good_enchantability.desc"), false));
    } 
    if (tradeGood.item.item instanceof ItemFood) {
      ItemFood food = (ItemFood)tradeGood.item.item;
      if (food.getHealAmount(tradeGood.item.getItemStack()) > 0)
        infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_foodhealamount", new String[] { "" + food.getHealAmount(tradeGood.item.getItemStack()) }), new ItemStack(Items.APPLE, 1), 
              LanguageUtilities.string("travelbook.trade_good_foodhealamount.desc"), false)); 
      if (food.getSaturationModifier(tradeGood.item.getItemStack()) > 0.0F)
        infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_foodsaturation", new String[] { "" + food.getSaturationModifier(tradeGood.item.getItemStack()) }), new ItemStack(Items.COOKED_BEEF, 1), 
              LanguageUtilities.string("travelbook.trade_good_foodsaturation.desc"), false)); 
      if (food instanceof ItemFoodMultiple) {
        ItemFoodMultiple foodMultiple = (ItemFoodMultiple)food;
        if (foodMultiple.getHealthAmount() > 0)
          infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_multiplefoodhealth", new String[] { "" + foodMultiple.getHealthAmount() }), new ItemStack((Item)MillItems.RASGULLA, 1), 
                LanguageUtilities.string("travelbook.trade_good_multiplefoodhealth.desc"), false)); 
        if (foodMultiple.getRegenerationDuration() > 0)
          infoColumns.add(new TextLine(
                LanguageUtilities.string("travelbook.trade_good_enchantment", new String[] { I18n.format(MobEffects.REGENERATION.getName(), new Object[0]), "" + foodMultiple.getRegenerationDuration() }), new ItemStack(Items.GOLDEN_APPLE, 1), 
                LanguageUtilities.string("travelbook.trade_good_enchantment.desc"), false)); 
        if (foodMultiple.getPotionId() != null)
          infoColumns.add(new TextLine(
                LanguageUtilities.string("travelbook.trade_good_enchantment", new String[] { I18n.format(I18n.format(foodMultiple.getPotionId().getEffectName(), new Object[0]), new Object[0]), "" + (foodMultiple
                    .getPotionId().getDuration() / 20) }), new ItemStack(Items.GOLDEN_APPLE, 1), LanguageUtilities.string("travelbook.trade_good_enchantment.desc"), false)); 
        if (foodMultiple.getDrunkDuration() > 0)
          infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_drunk"), new ItemStack((Item)MillItems.CIDER, 1), 
                LanguageUtilities.string("travelbook.trade_good_drunk.desc"), false)); 
        if (foodMultiple.getMaxDamage() > 1)
          infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_nbuse", new String[] { "" + foodMultiple.getMaxDamage() }), new ItemStack((Item)MillItems.TRIPES, 1), 
                LanguageUtilities.string("travelbook.trade_good_nbuse.desc"), false)); 
      } 
    } else if (tradeGood.item.item instanceof ItemPickaxe) {
      ItemPickaxe pickaxe = (ItemPickaxe)tradeGood.item.item;
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_harvestlevel", new String[] { "" + pickaxe.getHarvestLevel(tradeGood.item.getItemStack(), "pickaxe", null, null) }), new ItemStack(Blocks.OBSIDIAN, 1), 
            LanguageUtilities.string("travelbook.trade_good_harvestlevel.desc"), false));
    } 
    if (VillagerConfig.DEFAULT_CONFIG.foodsGrowth.containsKey(tradeGood.item))
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_growthfood", new String[] { "" + VillagerConfig.DEFAULT_CONFIG.foodsGrowth.get(tradeGood.item) }), new ItemStack(Items.BREAD, 1), 
            LanguageUtilities.string("travelbook.trade_good_growthfood.desc"), false)); 
    if (VillagerConfig.DEFAULT_CONFIG.foodsConception.containsKey(tradeGood.item))
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_conceptionfood", new String[] { "+" + (((Integer)VillagerConfig.DEFAULT_CONFIG.foodsConception.get(tradeGood.item)).intValue() * 10) + "%" }), new ItemStack((Item)MillItems.CIDER, 1), 
            LanguageUtilities.string("travelbook.trade_good_conceptionfood.desc"), false)); 
    if (VillagerConfig.DEFAULT_CONFIG.weapons.containsKey(tradeGood.item)) {
      double attackBoost = Math.ceil(((float)MillCommonUtilities.getItemWeaponDamage(tradeGood.item.item) / 2.0F));
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_weapon", new String[] { "" + attackBoost }), new ItemStack(Items.IRON_SWORD, 1), 
            LanguageUtilities.string("travelbook.trade_good_weapon.desc"), false));
    } 
    if (VillagerConfig.DEFAULT_CONFIG.armoursBoots.containsKey(tradeGood.item) || VillagerConfig.DEFAULT_CONFIG.armoursChestplate.containsKey(tradeGood.item) || VillagerConfig.DEFAULT_CONFIG.armoursHelmet
      .containsKey(tradeGood.item) || VillagerConfig.DEFAULT_CONFIG.armoursLeggings.containsKey(tradeGood.item)) {
      int armourValue = ((ItemArmor)tradeGood.item.item).damageReduceAmount;
      infoColumns.add(new TextLine("" + armourValue, new ItemStack((Item)Items.IRON_CHESTPLATE, 1), LanguageUtilities.string("travelbook.trade_good_armour.desc"), false));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
  }
  
  private void getBookTradeGoodDetail_goalsInfo(Culture culture, TextPage page, TradeGood tradeGood) {
    List<Goal> craftingProducingGoals = new ArrayList<>();
    List<Goal> gatheringGoals = new ArrayList<>();
    List<Goal> harvestingGoals = new ArrayList<>();
    List<Goal> consumingGoals = new ArrayList<>();
    for (Goal goal : culture.getAllUsedGoals()) {
      if (goal instanceof GoalGenericCrafting) {
        GoalGenericCrafting craftingGoal = (GoalGenericCrafting)goal;
        if (craftingGoal.output.containsKey(tradeGood.item))
          craftingProducingGoals.add(craftingGoal); 
        if (craftingGoal.input.containsKey(tradeGood.item))
          consumingGoals.add(craftingGoal); 
        continue;
      } 
      if (goal instanceof GoalGenericMining) {
        GoalGenericMining miningGoal = (GoalGenericMining)goal;
        for (InvItem output : miningGoal.loots.keySet()) {
          if (output.equals(tradeGood.item))
            gatheringGoals.add(goal); 
        } 
        continue;
      } 
      if (goal instanceof GoalGenericHarvestCrop) {
        GoalGenericHarvestCrop harvestGoal = (GoalGenericHarvestCrop)goal;
        for (AnnotedParameter.BonusItem bonusItem : harvestGoal.harvestItem) {
          if (bonusItem.item.equals(tradeGood.item))
            harvestingGoals.add(goal); 
        } 
      } 
    } 
    if (InvItem.createInvItem(MillBlocks.BS_MUD_BRICK).equals(tradeGood.item))
      gatheringGoals.add((Goal)Goal.goals.get("gatherbrick")); 
    if (InvItem.createInvItem((Item)MillItems.SILK).equals(tradeGood.item))
      gatheringGoals.add((Goal)Goal.goals.get("gathersilk")); 
    if (InvItem.createInvItem(Items.FISH).equals(tradeGood.item)) {
      gatheringGoals.add((Goal)Goal.goals.get("fish"));
      gatheringGoals.add((Goal)Goal.goals.get("fishinuits"));
    } 
    if (InvItem.createInvItem(Blocks.BONE_BLOCK).equals(tradeGood.item))
      gatheringGoals.add((Goal)Goal.goals.get("fishinuits")); 
    if (InvItem.createInvItem(Items.DYE, 3).equals(tradeGood.item))
      harvestingGoals.add((Goal)Goal.goals.get("harvestcocoa")); 
    if (InvItem.createInvItem(Items.NETHER_WART).equals(tradeGood.item))
      harvestingGoals.add((Goal)Goal.goals.get("harvestwarts")); 
    if (InvItem.createInvItem(Items.REEDS).equals(tradeGood.item))
      harvestingGoals.add((Goal)Goal.goals.get("harvestsugarcane")); 
    if (InvItem.createInvItem(Blocks.WOOL).equals(tradeGood.item))
      gatheringGoals.add((Goal)Goal.goals.get("shearsheep")); 
    if (InvItem.createInvItem(Blocks.LOG).equals(tradeGood.item) || InvItem.createInvItem(Blocks.LOG2).equals(tradeGood.item))
      gatheringGoals.add((Goal)Goal.goals.get("choptrees")); 
    List<VillagerType> craftingVillagers = new ArrayList<>();
    List<VillagerType> harvestingVillagers = new ArrayList<>();
    List<VillagerType> gatheringVillagers = new ArrayList<>();
    List<VillagerType> usingVillagers = new ArrayList<>();
    for (VillagerType villagerType : culture.villagerTypes.values()) {
      boolean found = false;
      for (Goal goal : craftingProducingGoals) {
        if (villagerType.goals.contains(goal))
          found = true; 
      } 
      if (found)
        craftingVillagers.add(villagerType); 
      found = false;
      for (Goal goal : harvestingGoals) {
        if (villagerType.goals.contains(goal))
          found = true; 
      } 
      if (found)
        harvestingVillagers.add(villagerType); 
      found = false;
      for (Goal goal : gatheringGoals) {
        if (villagerType.goals.contains(goal))
          found = true; 
      } 
      if (found)
        gatheringVillagers.add(villagerType); 
    } 
    for (VillagerType villagerType : culture.villagerTypes.values()) {
      if (villagerType.requiredFoodAndGoods.containsKey(tradeGood.item))
        usingVillagers.add(villagerType); 
      if (villagerType.itemsNeeded.contains(tradeGood.item))
        usingVillagers.add(villagerType); 
      for (String toolcategory : villagerType.toolsCategoriesNeeded) {
        if (((List)villagerType.villagerConfig.categories.get(toolcategory)).contains(tradeGood.item))
          usingVillagers.add(villagerType); 
      } 
    } 
    List<TextLine> infoColumns = new ArrayList<>();
    boolean hasProducers = (!craftingVillagers.isEmpty() || !harvestingVillagers.isEmpty() || !gatheringVillagers.isEmpty());
    boolean hasConsumers = !usingVillagers.isEmpty();
    if (hasProducers && hasConsumers) {
      List<TextLine> producerColumn = new ArrayList<>();
      List<TextLine> consumerColumn = new ArrayList<>();
      if (craftingVillagers.size() > 0) {
        producerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_craftingvillagers"), TextLine.ITALIC, false));
        for (VillagerType villagerType : craftingVillagers)
          producerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        producerColumn.add(new TextLine());
      } 
      if (harvestingVillagers.size() > 0) {
        producerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_harvestingvillagers"), TextLine.ITALIC, false));
        for (VillagerType villagerType : harvestingVillagers)
          producerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        producerColumn.add(new TextLine());
      } 
      if (gatheringVillagers.size() > 0) {
        producerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_gatheringvillagers"), TextLine.ITALIC, false));
        producerColumn.clear();
        for (VillagerType villagerType : gatheringVillagers)
          producerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        producerColumn.add(new TextLine());
      } 
      if (usingVillagers.size() > 0) {
        consumerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_usingvillagers"), TextLine.ITALIC, false));
        for (VillagerType villagerType : usingVillagers)
          consumerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        consumerColumn.add(new TextLine());
      } 
      List<TextLine> mergedColumns = mergeColumns(producerColumn, consumerColumn);
      for (TextLine line : mergedColumns)
        page.addLine(line); 
    } else {
      if (craftingVillagers.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.trade_good_craftingvillagers"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (VillagerType villagerType : craftingVillagers)
          infoColumns.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (harvestingVillagers.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.trade_good_harvestingvillagers"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (VillagerType villagerType : harvestingVillagers)
          infoColumns.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (gatheringVillagers.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.trade_good_gatheringvillagers"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (VillagerType villagerType : gatheringVillagers)
          infoColumns.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (usingVillagers.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.trade_good_usingvillagers"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (VillagerType villagerType : usingVillagers)
          infoColumns.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType))); 
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns)
          page.addLine(l); 
        page.addBlankLine();
      } 
    } 
    if (consumingGoals.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.trade_good_consuminggoals"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      for (Goal goal : consumingGoals)
        page.addLine(new TextLine(goal.gameName(), goal.getIcon(), false)); 
      page.addBlankLine();
    } 
  }
  
  private void getBookTradeGoodDetail_shops(Culture culture, TextPage page, TradeGood tradeGood) {
    List<String> buyingShops = new ArrayList<>();
    for (String shop : culture.shopBuys.keySet()) {
      if (((List)culture.shopBuys.get(shop)).contains(tradeGood))
        buyingShops.add(shop); 
    } 
    for (String shop : culture.shopBuysOptional.keySet()) {
      if (((List)culture.shopBuysOptional.get(shop)).contains(tradeGood))
        buyingShops.add(shop); 
    } 
    List<BuildingPlanSet> buyingBuildings = new ArrayList<>();
    for (BuildingPlanSet planSet : culture.ListPlanSets) {
      if (buyingShops.contains((planSet.getFirstStartingPlan()).shop))
        buyingBuildings.add(planSet); 
    } 
    buyingBuildings = (List<BuildingPlanSet>)buyingBuildings.stream().sorted((p1, p2) -> p1.getNameNative().compareTo(p2.getNameNative())).collect(Collectors.toList());
    List<String> sellingShops = new ArrayList<>();
    for (String shop : culture.shopSells.keySet()) {
      if (((List)culture.shopSells.get(shop)).contains(tradeGood))
        sellingShops.add(shop); 
    } 
    List<BuildingPlanSet> sellingBuildings = new ArrayList<>();
    for (BuildingPlanSet planSet : culture.ListPlanSets) {
      if (sellingShops.contains((planSet.getFirstStartingPlan()).shop))
        sellingBuildings.add(planSet); 
    } 
    sellingBuildings = (List<BuildingPlanSet>)sellingBuildings.stream().sorted((p1, p2) -> p1.getNameNative().compareTo(p2.getNameNative())).collect(Collectors.toList());
    if (sellingBuildings.equals(buyingBuildings)) {
      page.addBlankLine();
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_tradingbuildings"), TextLine.ITALIC, false));
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> tradeColumns = new ArrayList<>();
      for (BuildingPlanSet planSet : buyingBuildings)
        tradeColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
      List<TextLine> linesWithColumns = BookManager.splitInColumns(tradeColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } else if (buyingBuildings.size() > 0 && sellingBuildings.size() > 0) {
      List<TextLine> buyingColumn = new ArrayList<>();
      page.addBlankLine();
      buyingColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_buyingbuildings"), TextLine.ITALIC, false));
      (page.getLastLine()).canCutAfter = false;
      for (BuildingPlanSet planSet : buyingBuildings)
        buyingColumn.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
      List<TextLine> sellingColumn = new ArrayList<>();
      page.addBlankLine();
      sellingColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_sellingbuildings"), TextLine.ITALIC, false));
      (page.getLastLine()).canCutAfter = false;
      for (BuildingPlanSet planSet : sellingBuildings)
        sellingColumn.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
      List<TextLine> mergedColumns = mergeColumns(buyingColumn, sellingColumn);
      for (TextLine line : mergedColumns)
        page.addLine(line); 
    } else if (buyingBuildings.size() > 0) {
      page.addBlankLine();
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_buyingbuildings"), TextLine.ITALIC, false));
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> columns = new ArrayList<>();
      for (BuildingPlanSet planSet : buyingBuildings)
        columns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
      List<TextLine> linesWithColumns = BookManager.splitInColumns(columns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } else if (sellingBuildings.size() > 0) {
      page.addBlankLine();
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_sellingbuildings"), TextLine.ITALIC, false));
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> columns = new ArrayList<>();
      for (BuildingPlanSet planSet : sellingBuildings)
        columns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
      List<TextLine> linesWithColumns = BookManager.splitInColumns(columns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
    List<VillagerType> merchants = new ArrayList<>();
    for (VillagerType villagerType : culture.listVillagerTypes) {
      if (villagerType.isForeignMerchant && villagerType.foreignMerchantStock.containsKey(tradeGood.item))
        merchants.add(villagerType); 
    } 
    if (merchants.size() > 0) {
      page.addBlankLine();
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_marketmerchants"), TextLine.ITALIC, false));
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> columns = new ArrayList<>();
      for (VillagerType merchant : merchants)
        columns.add(new TextLine(merchant.name, new GuiText.GuiButtonReference(merchant))); 
      List<TextLine> linesWithColumns = BookManager.splitInColumns(columns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
  }
  
  private void getBookTradeGoodDetail_villageUse(Culture culture, TextPage page, TradeGood tradeGood) {
    List<TextLine> infoColumns = new ArrayList<>();
    for (VillageType villageType : culture.listVillageTypes) {
      Integer resUse = (Integer)villageType.computeVillageTypeCost().get(tradeGood.item);
      if (resUse != null)
        infoColumns.add(new TextLine(villageType.name + ": " + resUse, new GuiText.GuiButtonReference(villageType))); 
    } 
    if (infoColumns.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.trade_good_usebyvillage"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      for (TextLine l : infoColumns)
        page.addLine(l); 
      page.addBlankLine();
    } 
    infoColumns.clear();
    for (BuildingPlanSet planSet : culture.ListPlanSets) {
      if ((planSet.getFirstStartingPlan()).travelBookDisplay)
        for (int variation = 0; variation < planSet.plans.size(); variation++) {
          Map<InvItem, Integer> totalCost = new HashMap<>();
          for (BuildingPlan plan : (BuildingPlan[])planSet.plans.get(variation)) {
            for (InvItem key : plan.resCost.keySet()) {
              if (totalCost.containsKey(key)) {
                totalCost.put(key, Integer.valueOf(((Integer)totalCost.get(key)).intValue() + ((Integer)plan.resCost.get(key)).intValue()));
                continue;
              } 
              totalCost.put(key, (Integer)plan.resCost.get(key));
            } 
          } 
          Integer resUse = totalCost.get(tradeGood.item);
          if (resUse != null) {
            String buildingName = planSet.getNameNative();
            if (planSet.plans.size() > 1)
              buildingName = buildingName + " (" + (char)(65 + variation) + ")"; 
            infoColumns.add(new TextLine(buildingName + ": " + resUse, new GuiText.GuiButtonReference(planSet)));
          } 
        }  
    } 
    if (infoColumns.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.trade_good_usebybuilding"), TextLine.ITALIC);
      (page.getLastLine()).canCutAfter = false;
      for (TextLine l : infoColumns)
        page.addLine(l); 
      page.addBlankLine();
    } 
  }
  
  public TextBook getBookTradeGoodsList(Culture culture, String category, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(LanguageUtilities.string("travelbook.tradegoodslist", new String[] { culture.getAdjectiveTranslated() }), "§1", culture.getIcon(), false);
    page.addLine(LanguageUtilities.string("travelbook.tradegoodslistcategory", new String[] { culture.getCategoryName(category) }), culture.getCategoryIcon(category), false);
    page.addBlankLine();
    List<TradeGood> sortedTradeGoods = getCurrentTradeGoodList(culture, category);
    int nbKnownTradeGoods = profile.getNbUnlockedTradeGoods(culture, category);
    page.addLine(LanguageUtilities.string("travelbook.tradegoodslistcategory_unlocked", new String[] { "" + nbKnownTradeGoods, "" + sortedTradeGoods.size() }));
    page.addBlankLine();
    List<TextLine> infoColumns = new ArrayList<>();
    for (TradeGood tradeGood : sortedTradeGoods) {
      String style = "";
      if (!profile.isTradeGoodUnlocked(culture, tradeGood))
        style = TextLine.ITALIC; 
      infoColumns.add(new TextLine(tradeGood.getName(), style, new GuiText.GuiButtonReference(tradeGood)));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    book.addPage(page);
    return book;
  }
  
  public TextBook getBookVillageDetail(Culture culture, String itemKey, UserProfile profile) {
    VillageType villageType = culture.getVillageType(itemKey);
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    TextLine line = new TextLine(villageType.name + " (" + culture.getCultureString("village." + itemKey) + ")", "§1", villageType.getIcon(), false);
    page.addLine(line);
    (page.getLastLine()).canCutAfter = false;
    page.addBlankLine();
    boolean knownVillageType = (profile == null || profile.isVillageUnlocked(culture, villageType));
    boolean displayFullInfos = (knownVillageType || !MillConfigValues.TRAVEL_BOOK_LEARNING);
    if (!knownVillageType) {
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknownvillage"), "§4"));
      page.addBlankLine();
    } 
    List<TextLine> infoColumns = new ArrayList<>();
    if (displayFullInfos) {
      if (culture.hasCultureString("travelbook.village." + villageType.key + ".desc")) {
        page.addLine(culture.getCultureString("travelbook.village." + villageType.key + ".desc"));
        (page.getLastLine()).exportSpecialTag = "MAIN_DESC";
        page.addBlankLine();
      } 
      infoColumns.add(new TextLine("" + villageType.radius, new ItemStack((Item)Items.MAP, 1), LanguageUtilities.string("travelbook.village_radius"), false));
      infoColumns.add(new TextLine("" + villageType.weight, new ItemStack(Blocks.ANVIL, 1), LanguageUtilities.string("travelbook.village_weight"), false));
      List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
      page.addBlankLine();
    } 
    List<String> validBiomes = new ArrayList<>();
    for (String biomeName : villageType.biomes) {
      for (ResourceLocation rl : Biome.REGISTRY.getKeys()) {
        if (((Biome)Biome.REGISTRY.getObject(rl)).getBiomeName().equalsIgnoreCase(biomeName))
          validBiomes.add(biomeName); 
      } 
    } 
    if (validBiomes.size() > 0) {
      String biomesList = validBiomes.stream().collect(Collectors.joining(", "));
      biomesList = WordUtils.capitalizeFully(biomesList);
      page.addLine(LanguageUtilities.string("travelbook.village_biomes", new String[] { biomesList }));
      page.addBlankLine();
    } 
    if (displayFullInfos) {
      if (villageType.hamlets.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.village_hamlets"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        for (String hamletKey : villageType.hamlets) {
          VillageType hamlet = culture.getVillageType(hamletKey);
          page.addLine(new TextLine(hamlet.name, new GuiText.GuiButtonReference(hamlet)));
        } 
        page.addBlankLine();
      } 
      page.addLine(LanguageUtilities.string("travelbook.village_townhall"), TextLine.ITALIC);
      if (villageType.centreBuilding != null)
        page.addLine(new TextLine(villageType.centreBuilding.getNameNative(), new GuiText.GuiButtonReference(villageType.centreBuilding))); 
      if (villageType.customCentre != null)
        page.addLine(LanguageUtilities.string("travelbook.customvillagecentre")); 
      page.addBlankLine();
      if (villageType.startBuildings.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.village_start"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (BuildingPlanSet planSet : villageType.startBuildings)
          infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (villageType.coreBuildings.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.village_core"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (BuildingPlanSet planSet : villageType.coreBuildings)
          infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (villageType.secondaryBuildings.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.village_secondary"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (BuildingPlanSet planSet : villageType.secondaryBuildings)
          infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (villageType.extraBuildings.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.village_extra"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (BuildingPlanSet planSet : villageType.extraBuildings)
          infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
      if (villageType.playerBuildings.size() > 0) {
        page.addLine(LanguageUtilities.string("travelbook.village_player"), TextLine.ITALIC);
        (page.getLastLine()).canCutAfter = false;
        infoColumns.clear();
        for (BuildingPlanSet planSet : villageType.playerBuildings)
          infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet))); 
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list)
          page.addLine(l); 
        page.addBlankLine();
      } 
      page.addBlankLine();
      page.addLine(LanguageUtilities.string("travelbook.village_rescost"), "§1");
      (page.getLastLine()).canCutAfter = false;
      page.addBlankLine();
      (page.getLastLine()).canCutAfter = false;
      infoColumns.clear();
      Map<InvItem, Integer> resCost = villageType.computeVillageTypeCost();
      List<InvItem> resTypes = (List<InvItem>)resCost.keySet().stream().sorted((r1, r2) -> ((Integer)resCost.get(r2)).compareTo((Integer)resCost.get(r1))).collect(Collectors.toList());
      for (InvItem res : resTypes) {
        TradeGood tradeGood = culture.getTradeGood(res);
        if (tradeGood == null) {
          infoColumns.add(new TextLine("" + resCost.get(res), res.getItemStack(), true));
          continue;
        } 
        infoColumns.add(new TextLine("" + resCost.get(res), new GuiText.GuiButtonReference(tradeGood)));
      } 
      List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
      page.addBlankLine();
    } 
    book.addPage(page);
    book = offsetFirstLines(book);
    return book;
  }
  
  public TextBook getBookVillagerDetail(Culture culture, String itemKey, UserProfile profile) {
    VillagerType villagerType = culture.getVillagerType(itemKey);
    boolean knownVillager = (profile == null || profile.isVillagerUnlocked(culture, villagerType));
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    String name = villagerType.getNameNativeAndTranslated();
    TextLine line = new TextLine(name, "§1", villagerType.getIcon(), false);
    page.addLine(line);
    (page.getLastLine()).canCutAfter = false;
    page.addBlankLine();
    (page.getLastLine()).canCutAfter = false;
    if (!knownVillager) {
      page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknownvillager"), "§4"));
      page.addBlankLine();
    } 
    if (!knownVillager && MillConfigValues.TRAVEL_BOOK_LEARNING) {
      book.addPage(page);
      getBookVillagerDetail_residence(culture, villagerType, page, false);
      book = offsetFirstLines(book);
      return book;
    } 
    this.lineSizeInPx -= 80;
    if (culture.hasCultureString("travelbook.villager." + villagerType.key + ".desc")) {
      page.addLine(culture.getCultureString("travelbook.villager." + villagerType.key + ".desc"));
      (page.getLastLine()).exportSpecialTag = "MAIN_DESC";
      page.addBlankLine();
    } 
    List<TextLine> infoColumns = new ArrayList<>();
    infoColumns.add(new TextLine("" + villagerType.health, new ItemStack((Item)Items.IRON_CHESTPLATE, 1), LanguageUtilities.string("travelbook.health"), false));
    infoColumns.add(new TextLine("" + villagerType.baseAttackStrength, new ItemStack(Items.IRON_SWORD, 1), LanguageUtilities.string("travelbook.attackstrength"), false));
    if (villagerType.hireCost > 0) {
      String cost = MillCommonUtilities.getShortPrice(villagerType.hireCost);
      infoColumns.add(new TextLine(cost, new ItemStack((Item)MillItems.DENIER, 1), LanguageUtilities.string("travelbook.hirecost"), false));
    } 
    for (String tag : VILLAGER_TAGS_TO_DISPLAY) {
      if (villagerType.containsTags(tag)) {
        TextLine col1 = new TextLine(LanguageUtilities.string("travelbook.specialbehaviours." + tag), VILLAGER_TAGS_ICONS.get(tag), LanguageUtilities.string("travelbook.specialbehaviours." + tag + ".desc"), false);
        infoColumns.add(col1);
      } 
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    page.addBlankLine();
    boolean showGoals = false;
    for (Goal goal : villagerType.goals) {
      if (goal.travelBookShow)
        showGoals = true; 
    } 
    if (showGoals) {
      page.addLine(culture.getCultureString("travelbook.goals"), "§1");
      (page.getLastLine()).canCutAfter = false;
      for (Goal goal : villagerType.goals) {
        if (goal.travelBookShow)
          page.addLine(goal.gameName(null), goal.getIcon(), false); 
      } 
      page.addBlankLine();
    } 
    infoColumns.clear();
    for (InvItem item : villagerType.requiredFoodAndGoods.keySet()) {
      TradeGood tradeGood = culture.getTradeGood(item);
      if (tradeGood != null)
        infoColumns.add(new TextLine(item.getName(), new GuiText.GuiButtonReference(tradeGood))); 
    } 
    for (String toolCategory : villagerType.toolsCategoriesNeeded) {
      InvItem iconItem = ((List<InvItem>)VillagerConfig.DEFAULT_CONFIG.categories.get(toolCategory)).get(0);
      infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.toolscategory." + toolCategory), iconItem.getItemStack(), 
            LanguageUtilities.string("travelbook.toolscategory." + toolCategory + ".desc"), false));
    } 
    if (infoColumns.size() > 0) {
      page.addLine(LanguageUtilities.string("travelbook.neededitems"), "§1");
      (page.getLastLine()).canCutAfter = false;
      linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
    getBookVillagerDetail_residence(culture, villagerType, page, true);
    book.addPage(page);
    book = offsetFirstLines(book);
    this.lineSizeInPx += 80;
    return book;
  }
  
  private void getBookVillagerDetail_residence(Culture culture, VillagerType villagerType, TextPage page, boolean villagerUnlocked) {
    List<BuildingPlanSet> buildings = new ArrayList<>();
    List<TextLine> infoColumns = new ArrayList<>();
    for (BuildingPlanSet planSet : culture.ListPlanSets) {
      if ((planSet.getRandomStartingPlan()).maleResident.contains(villagerType.key) || (planSet.getRandomStartingPlan()).femaleResident.contains(villagerType.key)) {
        infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
        buildings.add(planSet);
      } 
    } 
    if (villagerUnlocked && 
      infoColumns.size() > 0) {
      page.addBlankLine();
      page.addLine(LanguageUtilities.string("travelbook.residesin"), "§1");
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
    infoColumns.clear();
    Set<VillageType> villages = new HashSet<>();
    for (BuildingPlanSet planSet : buildings) {
      for (VillageType village : culture.listVillageTypes) {
        if (village.centreBuilding == planSet || village.startBuildings.contains(planSet) || village.coreBuildings.contains(planSet) || village.secondaryBuildings.contains(planSet) || village.extraBuildings
          .contains(planSet))
          villages.add(village); 
      } 
    } 
    for (VillageType villageType : villages)
      infoColumns.add(new TextLine(villageType.name, new GuiText.GuiButtonReference(villageType))); 
    if (infoColumns.size() > 0) {
      page.addBlankLine();
      page.addLine(LanguageUtilities.string("travelbook.residesinvillage"), "§1");
      (page.getLastLine()).canCutAfter = false;
      List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
      for (TextLine l : linesWithColumns)
        page.addLine(l); 
    } 
  }
  
  public TextBook getBookVillagersList(Culture culture, String category, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(LanguageUtilities.string("travelbook.villagerslist", new String[] { culture.getAdjectiveTranslated() }), "§1", culture.getIcon(), false);
    page.addLine(LanguageUtilities.string("travelbook.villagerslistcategory", new String[] { culture.getCategoryName(category) }), culture.getCategoryIcon(category), false);
    page.addBlankLine();
    List<VillagerType> sortedVillagers = getCurrentVillagerList(culture, category);
    int nbKnownVillagers = profile.getNbUnlockedVillagers(culture, category);
    page.addLine(LanguageUtilities.string("travelbook.villagerslistcategory_unlocked", new String[] { "" + nbKnownVillagers, "" + sortedVillagers.size() }));
    page.addBlankLine();
    List<TextLine> infoColumns = new ArrayList<>();
    for (VillagerType villagerType : sortedVillagers) {
      String style = "";
      if (!profile.isVillagerUnlocked(culture, villagerType))
        style = TextLine.ITALIC; 
      String name = villagerType.getNameNativeAndTranslated();
      infoColumns.add(new TextLine(name, style, new GuiText.GuiButtonReference(villagerType)));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    book.addPage(page);
    return book;
  }
  
  public TextBook getBookVillagesList(Culture culture, UserProfile profile) {
    TextBook book = new TextBook();
    TextPage page = new TextPage();
    page.addLine(LanguageUtilities.string("travelbook.villageslist", new String[] { culture.getAdjectiveTranslated() }), "§1", culture.getIcon(), false);
    page.addBlankLine();
    List<VillageType> sortedVillages = getCurrentVillageList(culture);
    int nbKnownVillages = profile.getNbUnlockedVillages(culture);
    page.addLine(LanguageUtilities.string("travelbook.villageslist_unlocked", new String[] { "" + nbKnownVillages, "" + sortedVillages.size(), culture.getAdjectiveTranslated() }));
    page.addBlankLine();
    List<TextLine> infoColumns = new ArrayList<>();
    for (VillageType villageType : sortedVillages) {
      String style = "";
      if (!profile.isVillageUnlocked(culture, villageType))
        style = TextLine.ITALIC; 
      String translatedName = villageType.getNameNativeAndTranslated();
      infoColumns.add(new TextLine(translatedName, style, new GuiText.GuiButtonReference(villageType)));
    } 
    List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
    for (TextLine l : linesWithColumns)
      page.addLine(l); 
    book.addPage(page);
    return book;
  }
  
  public List<BuildingPlanSet> getCurrentBuildingList(Culture culture, String category) {
    List<BuildingPlanSet> sortedPlans = new ArrayList<>(culture.ListPlanSets);
    if (category != null)
      sortedPlans = (List<BuildingPlanSet>)sortedPlans.stream().filter(p -> ((p.getFirstStartingPlan()).travelBookDisplay && category.equalsIgnoreCase((p.getFirstStartingPlan()).travelBookCategory))).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList()); 
    return sortedPlans;
  }
  
  public List<TradeGood> getCurrentTradeGoodList(Culture culture, String category) {
    List<TradeGood> sortedGoods = new ArrayList<>(culture.goodsList);
    if (category != null)
      sortedGoods = (List<TradeGood>)sortedGoods.stream().filter(p -> (p.travelBookDisplay && category.equals(p.travelBookCategory))).sorted((p1, p2) -> p1.name.compareTo(p2.name)).collect(Collectors.toList()); 
    return sortedGoods;
  }
  
  public List<VillageType> getCurrentVillageList(Culture culture) {
    List<VillageType> sortedVillages = new ArrayList<>(culture.listVillageTypes);
    sortedVillages = (List<VillageType>)sortedVillages.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList());
    return sortedVillages;
  }
  
  public List<VillagerType> getCurrentVillagerList(Culture culture, String category) {
    List<VillagerType> sortedVillagers = new ArrayList<>(culture.listVillagerTypes);
    if (category != null)
      sortedVillagers = (List<VillagerType>)sortedVillagers.stream().filter(p -> (p.travelBookDisplay && category.equals(p.travelBookCategory))).sorted((p1, p2) -> p1.name.compareTo(p2.name)).collect(Collectors.toList()); 
    return sortedVillagers;
  }
  
  private TextBook offsetFirstLines(TextBook book) {
    book = adjustTextBookLineLength(book);
    for (TextPage apage : book.getPages()) {
      apage.getLine(0).setLineMarginLeft(10);
      apage.getLine(0).setLineMarginRight(10);
      if (apage.getNbLines() > 1 && apage.getLine(0).getLineHeight() < 18) {
        apage.getLine(1).setLineMarginLeft(10);
        apage.getLine(1).setLineMarginRight(10);
      } 
    } 
    return book;
  }
}
