package org.millenaire.common.buildingplan;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageData;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class BuildingDevUtilities {
  private static final String EOL = "\n";
  
  public static void exportMissingTravelBookDesc() {
    int translationLogLevel = MillConfigValues.LogTranslation;
    MillConfigValues.LogTranslation = 0;
    for (Iterator<Culture> iterator = Culture.ListCultures.iterator(); iterator.hasNext(); ) {
      Culture culture = iterator.next();
      File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "travelbook");
      dir.mkdirs();
      File file = new File(dir, culture.key + "_travelbook.txt");
      try {
        BufferedWriter writer = MillCommonUtilities.getWriter(file);
        writer.write("//Elements without travel book descriptions\n\n");
        for (VillagerType villagerType : culture.listVillagerTypes.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.villager." + villagerType.key + ".desc";
          if (!culture.hasCultureString(key))
            writer.write(key + "=" + "\n"); 
        } 
        writer.write("\n");
        for (VillageType villageType : culture.listVillageTypes.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.village." + villageType.key + ".desc";
          if (!culture.hasCultureString(key))
            writer.write(key + "=" + "\n"); 
        } 
        writer.write("\n");
        for (BuildingPlanSet planSet : culture.ListPlanSets.stream().filter(p -> (p.getFirstStartingPlan()).travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.building." + planSet.key + ".desc";
          if (!culture.hasCultureString(key))
            writer.write(key + "=" + "\n"); 
        } 
        writer.write("\n");
        for (TradeGood tradeGood : culture.goodsList.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.trade_good." + tradeGood.key + ".desc";
          if (!culture.hasCultureString(key))
            writer.write(key + "=" + "\n"); 
        } 
        writer.close();
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
    MillConfigValues.LogTranslation = translationLogLevel;
  }
  
  public static void exportTravelBookDescCSV() {
    int translationLogLevel = MillConfigValues.LogTranslation;
    MillConfigValues.LogTranslation = 0;
    Map<String, String> tradeGoodsDesc = new HashMap<>();
    Map<String, String> tradeGoodsCultures = new HashMap<>();
    for (Culture culture : Culture.ListCultures) {
      File file1 = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "travelbook");
      file1.mkdirs();
      File file2 = new File(file1, culture.key + "_travelbook.csv");
      try {
        BufferedWriter writer = MillCommonUtilities.getWriter(file2);
        for (VillagerType villagerType : culture.listVillagerTypes.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.villager." + villagerType.key + ".desc";
          if (!culture.hasCultureString(key)) {
            writer.write(key + ";" + "\n");
            continue;
          } 
          writer.write(key + ";" + culture.getCultureString(key) + "\n");
        } 
        for (VillageType villageType : culture.listVillageTypes.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.village." + villageType.key + ".desc";
          if (!culture.hasCultureString(key)) {
            writer.write(key + ";" + "\n");
            continue;
          } 
          writer.write(key + ";" + culture.getCultureString(key) + "\n");
        } 
        for (BuildingPlanSet planSet : culture.ListPlanSets.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.building." + planSet.key + ".desc";
          if (!culture.hasCultureString(key)) {
            writer.write(key + ";" + "\n");
            continue;
          } 
          writer.write(key + ";" + culture.getCultureString(key) + "\n");
        } 
        for (TradeGood tradeGood : culture.goodsList.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
          String key = "travelbook.trade_good." + tradeGood.key + ".desc";
          if (!culture.hasCultureString(key)) {
            writer.write(key + ";" + "\n");
            if (!tradeGoodsDesc.containsKey(key))
              tradeGoodsDesc.put(key, null); 
          } else {
            writer.write(key + ";" + culture.getCultureString(key) + "\n");
            tradeGoodsDesc.put(key, culture.getCultureString(key));
          } 
          String treadeGoodCulture = culture.key + " (" + tradeGood.travelBookCategory + ")";
          if (tradeGoodsCultures.containsKey(key)) {
            tradeGoodsCultures.put(key, (String)tradeGoodsCultures.get(key) + " " + treadeGoodCulture);
            continue;
          } 
          tradeGoodsCultures.put(key, treadeGoodCulture);
        } 
        writer.close();
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
    File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "travelbook");
    dir.mkdirs();
    File file = new File(dir, "tradegoods.csv");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      for (String key : tradeGoodsDesc.keySet().stream().sorted().collect(Collectors.toList())) {
        if (tradeGoodsDesc.get(key) == null) {
          writer.write(key + ";;;;" + (String)tradeGoodsCultures.get(key) + "\n");
          continue;
        } 
        writer.write(key + ";" + (String)tradeGoodsDesc.get(key) + ";;;" + (String)tradeGoodsCultures.get(key) + "\n");
      } 
      writer.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
    MillConfigValues.LogTranslation = translationLogLevel;
  }
  
  public static void generateBuildingRes() {
    File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      if (MillConfigValues.DEV)
        generateSignBuildings(writer); 
      for (Culture culture : Culture.ListCultures) {
        writer.write(culture.key + ": " + "\n");
        generateVillageTypeListing(writer, culture.listVillageTypes);
        writer.write("\n");
        generateVillageTypeListing(writer, culture.listLoneBuildingTypes);
      } 
      writer.write("\n");
      writer.write("\n");
      for (Culture culture : Culture.ListCultures) {
        for (BuildingPlanSet set : culture.ListPlanSets)
          writePlanCostWikiStyle(set, writer); 
      } 
      writer.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(null, "Wrote resources used.txt"); 
    for (Culture culture : Culture.ListCultures)
      generateCultureBuildingRes(culture); 
  }
  
  public static void generateColourSheet() {
    try {
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(null, "Generating colour sheet."); 
      BufferedImage pict = new BufferedImage(200, PointType.colourPoints.size() * 20 + 25, 1);
      Graphics2D graphics = pict.createGraphics();
      graphics.setColor(new Color(16777215));
      graphics.fillRect(0, 0, pict.getWidth(), pict.getHeight());
      graphics.setColor(new Color(0));
      graphics.drawString("Generated colour sheet.", 5, 20);
      int pos = 1;
      for (File loadDir : Mill.loadingDirs) {
        File mainList = new File(loadDir, "blocklist.txt");
        if (mainList.exists())
          pos = generateColourSheetHandleFile(graphics, pos, mainList); 
      } 
      try {
        ImageIO.write(pict, "png", new File(MillCommonUtilities.getMillenaireContentDir(), "Colour Sheet.png"));
      } catch (Exception e) {
        MillLog.printException(e);
      } 
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(null, "Finished generating colour sheet."); 
    } catch (Exception e) {
      MillLog.printException("Exception when trying to generate Colour Sheet:", e);
    } 
  }
  
  private static int generateColourSheetHandleFile(Graphics2D graphics, int pos, File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] params = line.split(";", -1);
          String[] rgb = params[4].split("/", -1);
          int colour = (Integer.parseInt(rgb[0]) << 16) + (Integer.parseInt(rgb[1]) << 8) + (Integer.parseInt(rgb[2]) << 0);
          graphics.setColor(new Color(0));
          graphics.drawString(params[0], 20, 17 + 20 * pos);
          graphics.setColor(new Color(colour));
          graphics.fillRect(0, 5 + 20 * pos, 15, 15);
          pos++;
        } 
      } 
    } catch (Exception e) {
      MillLog.printException(e);
    } 
    return pos;
  }
  
  private static void generateCultureBuildingRes(Culture culture) {
    File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used " + culture.key + ".txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      for (BuildingPlanSet set : culture.ListPlanSets)
        writePlanCostTextStyle(set, writer); 
      writer.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private static void generateSignBuildings(BufferedWriter writer) throws Exception {
    writer.write("\n\n\nBuildings with signs (not panels):\n\n\n");
    for (Culture culture : Culture.ListCultures) {
      for (BuildingPlanSet set : culture.ListPlanSets) {
        for (BuildingPlan[] plans : set.plans) {
          for (BuildingPlan plan : plans) {
            if (!plan.containsTags("hof"))
              for (PointType[][] level : plan.plan) {
                for (PointType[] row : level) {
                  for (PointType pt : row) {
                    if (pt != null && pt.specialType != null && pt.specialType.startsWith("plainSignGuess"))
                      writer.write("Sign in " + plan.toString() + "\n"); 
                  } 
                } 
              }  
          } 
        } 
      } 
    } 
    writer.write("\n\n\n");
  }
  
  public static void generateTranslatedHoFData(LanguageData language) {
    List<String> hofData = LanguageUtilities.getHoFData();
    File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "hof_" + language.language + ".txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      boolean isFirstLine = true;
      for (String line : hofData) {
        if (isFirstLine) {
          isFirstLine = false;
          continue;
        } 
        String[] lines = line.split(";");
        String output = lines[0];
        int roleStart = 1;
        if (lines[1].length() > 0 && !lines[1].startsWith("hof.")) {
          output = output + " " + lines[1];
          roleStart = 2;
        } 
        for (int i = roleStart; i < lines.length; i++) {
          if (lines[i].length() > 0) {
            if (lines[i].endsWith("2")) {
              output = output + " ";
            } else {
              output = output + ";";
            } 
            if (language.strings.containsKey(lines[i].toLowerCase())) {
              output = output + (String)language.strings.get(lines[i].toLowerCase());
            } else {
              MillLog.error(null, "Unknown HoF translation: " + lines[i] + " in language " + language.language);
            } 
          } 
        } 
        writer.write(output);
        writer.newLine();
      } 
      writer.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private static void generateVillageTypeListing(BufferedWriter writer, List<VillageType> villages) throws IOException {
    for (VillageType villageType : villages) {
      Map<InvItem, Integer> villageCost = villageType.computeVillageTypeCost();
      writer.write(villageType.key + " resource use: " + "\n");
      for (InvItem key : villageCost.keySet())
        writer.write(key.getName() + ": " + villageCost.get(key) + "\n"); 
      writer.write("\n");
    } 
  }
  
  public static void generateWikiTable() throws MillLog.MillenaireException {
    HashMap<InvItem, String> picts = new HashMap<>();
    HashMap<InvItem, String> links = new HashMap<>();
    picts.put(InvItem.createInvItem(Blocks.LOG, -1), "Wood_Any.gif");
    picts.put(InvItem.createInvItem(Blocks.LOG, 0), "Wood.png");
    picts.put(InvItem.createInvItem(Blocks.LOG, 1), "Wood_Pine.png");
    picts.put(InvItem.createInvItem(Blocks.LOG, 2), "Wood_Birch.png");
    picts.put(InvItem.createInvItem(Blocks.LOG, 3), "Wood_Jungle.png");
    picts.put(InvItem.createInvItem(Blocks.LOG2, 0), "Wood_Acacia.png");
    picts.put(InvItem.createInvItem(Blocks.LOG2, 1), "Wood_DarkOak.png");
    picts.put(InvItem.createInvItem(Blocks.STONE, 0), "Stone.png");
    picts.put(InvItem.createInvItem(Blocks.GLASS, 0), "Glass.png");
    picts.put(InvItem.createInvItem(Blocks.WOOL, 0), "White_Wool.png");
    picts.put(InvItem.createInvItem(Blocks.SANDSTONE, 0), "Sandstone.png");
    picts.put(InvItem.createInvItem(Blocks.COBBLESTONE, 0), "Cobblestone.png");
    picts.put(InvItem.createInvItem(Blocks.BRICK_BLOCK, 0), "Brick.png");
    picts.put(InvItem.createInvItem((Block)Blocks.SAND, 0), "Sand.png");
    picts.put(InvItem.createInvItem(Blocks.GLOWSTONE, 0), "Glowstone_(Block).png");
    picts.put(InvItem.createInvItem(Blocks.BOOKSHELF, 0), "Bookshelf.png");
    picts.put(InvItem.createInvItem(Blocks.GRAVEL, 0), "Gravel.png");
    picts.put(InvItem.createInvItem(Blocks.SANDSTONE, 2), "SmoothSandstone.png");
    picts.put(InvItem.createInvItem(Blocks.STONEBRICK, 3), "ChiselledStoneBricks.png");
    picts.put(InvItem.createInvItem(Blocks.STONEBRICK, 2), "CrackedStoneBricks.png");
    picts.put(InvItem.createInvItem((Block)Blocks.TALLGRASS, 1), "TallGrass.png");
    picts.put(InvItem.createInvItem((Block)Blocks.TALLGRASS, 2), "Fern.png");
    picts.put(InvItem.createInvItem(Blocks.MOSSY_COBBLESTONE, 0), "MossyCobblestone.png");
    picts.put(InvItem.createInvItem(Blocks.STONEBRICK, 1), "MossyStoneBricks.png");
    picts.put(InvItem.createInvItem(Blocks.IRON_ORE, 0), "Ore_Iron.png");
    picts.put(InvItem.createInvItem(Blocks.COAL_ORE, 0), "Ore_Coal.png");
    picts.put(InvItem.createInvItem(Blocks.GOLD_ORE, 0), "Ore_Gold.png");
    picts.put(InvItem.createInvItem(Blocks.REDSTONE_ORE, 0), "Ore_Redstone.png");
    picts.put(InvItem.createInvItem(Blocks.LAPIS_ORE, 0), "Ore_Lapis_Lazuli.png");
    picts.put(InvItem.createInvItem(Blocks.DIAMOND_ORE, 0), "Ore_Diamond.png");
    picts.put(InvItem.createInvItem(Blocks.LIT_PUMPKIN, 0), "Jack-O-Lantern.png");
    picts.put(InvItem.createInvItem(Blocks.MELON_BLOCK, 0), "Melon (Block).png");
    picts.put(InvItem.createInvItem(Blocks.LAPIS_BLOCK, 0), "Lapis_Lazuli_(Block).png");
    picts.put(InvItem.createInvItem(Blocks.UNLIT_REDSTONE_TORCH, 0), "Redstone_Torch.png");
    picts.put(InvItem.createInvItem(Blocks.BEDROCK, 0), "Bedrock.png");
    picts.put(InvItem.createInvItem(Blocks.NETHER_WART, 0), "Nether_Wart.png");
    picts.put(InvItem.createInvItem((Block)Blocks.LAVA, 0), "Lava.png");
    picts.put(InvItem.createInvItem((Block)Blocks.FLOWING_LAVA, 0), "Lava.png");
    picts.put(InvItem.createInvItem(Blocks.STONE_BUTTON, 0), "Stone_Button.png");
    picts.put(InvItem.createInvItem((Block)Blocks.REDSTONE_WIRE, 0), "Redstone_Dust.png");
    picts.put(InvItem.createInvItem(Blocks.STONE, 0), "Stone.png");
    picts.put(InvItem.createInvItem(Items.IRON_INGOT, 0), "Ironitm.png");
    picts.put(InvItem.createInvItem(Items.GOLD_INGOT, 0), "Golditm.png");
    picts.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 0), "ML_colombages_plain.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 0), "|link=Norman:Colombages");
    picts.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 1), "ML_colombages_cross.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 1), "|link=Norman:Colombages");
    picts.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 2), "ML_Thatch.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 2), "|link=Japanese:Thatch");
    picts.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 1), "ML_whitewashedbricks.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 1), "|link=Hindi:Cooked brick");
    picts.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 0), "ML_mudbrick.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 0), "|link=Hindi:Mud brick");
    picts.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 2), "ML_Mayan_Gold.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 2), "|link=Maya:Gold Ornament");
    picts.put(InvItem.createInvItem((Block)MillBlocks.PAPER_WALL, 0), "ML_paperwall.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.PAPER_WALL, 0), "|link=Japanese:Paper Wall");
    picts.put(InvItem.createInvItem((Item)MillItems.TAPESTRY, 0), "ML_tapestry.png");
    links.put(InvItem.createInvItem((Item)MillItems.TAPESTRY, 0), "|link=Norman:Tapisserie");
    picts.put(InvItem.createInvItem((Item)MillItems.INDIAN_STATUE, 0), "ML_IndianStatue.png");
    links.put(InvItem.createInvItem((Item)MillItems.INDIAN_STATUE, 0), "|link=Hindi:Statue");
    picts.put(InvItem.createInvItem((Item)MillItems.MAYAN_STATUE, 0), "ML_MayanStatue.png");
    links.put(InvItem.createInvItem((Item)MillItems.MAYAN_STATUE, 0), "|link=Maya:Carving");
    picts.put(InvItem.createInvItem((Item)MillItems.BYZANTINE_ICON_SMALL, 0), "ML_ByzantineIconSmall.png");
    links.put(InvItem.createInvItem((Item)MillItems.BYZANTINE_ICON_SMALL, 0), "|link=Byzantine:IIcon");
    picts.put(InvItem.createInvItem((Item)MillItems.BYZANTINE_ICON_MEDIUM, 0), "ML_ByzantineIconMedium.png");
    links.put(InvItem.createInvItem((Item)MillItems.BYZANTINE_ICON_MEDIUM, 0), "|link=Byzantine:IIcon");
    picts.put(InvItem.createInvItem((Item)MillItems.BYZANTINE_ICON_LARGE, 0), "ML_ByzantineIconLarge.png");
    links.put(InvItem.createInvItem((Item)MillItems.BYZANTINE_ICON_LARGE, 0), "|link=Byzantine:IIcon");
    picts.put(InvItem.createInvItem((Block)MillBlocks.BYZANTINE_TILES, 0), "ML_byzSlab.png");
    links.put(InvItem.createInvItem((Block)MillBlocks.BYZANTINE_TILES, 0), "|link=Blocks#Byzantine");
    try {
      HashMap<String, Integer> nameCount = new HashMap<>();
      HashMap<BuildingPlanSet, String> uniqueNames = new HashMap<>();
      for (Culture culture : Culture.ListCultures) {
        for (BuildingPlanSet set : culture.ListPlanSets) {
          String name = (((BuildingPlan[])set.plans.get(0))[0]).nativeName;
          if (!nameCount.containsKey(name)) {
            nameCount.put(name, Integer.valueOf(1));
            continue;
          } 
          nameCount.put(name, Integer.valueOf(((Integer)nameCount.get(name)).intValue() + 1));
        } 
      } 
      for (Culture culture : Culture.ListCultures) {
        for (BuildingPlanSet set : culture.ListPlanSets) {
          if (((Integer)nameCount.get((((BuildingPlan[])set.plans.get(0))[0]).nativeName)).intValue() > 1) {
            uniqueNames.put(set, (((BuildingPlan[])set.plans.get(0))[0]).nativeName + "~" + set.key);
            continue;
          } 
          uniqueNames.put(set, (((BuildingPlan[])set.plans.get(0))[0]).nativeName);
        } 
      } 
      File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used wiki building list.txt");
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      writer.write("{| class=\"wikitable\"\n");
      writer.write("|-\n");
      writer.write("! Requirements Template Building Name\n");
      writer.write("|-\n");
      for (Culture culture : Culture.ListCultures) {
        for (BuildingPlanSet set : culture.ListPlanSets) {
          writer.write("! " + (String)uniqueNames.get(set) + "\n");
          writer.write("|-\n");
        } 
      } 
      writer.write("|}");
      writer.close();
      file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used wiki.txt");
      writer = MillCommonUtilities.getWriter(file);
      writer.write("{{#switch: {{{1|{{BASEPAGENAME}}}}}\n");
      for (Culture culture : Culture.ListCultures) {
        for (BuildingPlanSet set : culture.ListPlanSets) {
          writer.write("|" + (String)uniqueNames.get(set) + " = <table><tr><td style=\"vertical-align:top;\">" + "\n");
          for (BuildingPlan[] plans : set.plans) {
            if (set.plans.size() > 1) {
              writer.write("<table class=\"reqirements\"><tr><th scope=\"col\" style=\"text-align:center;\">Variation " + (char)(65 + (plans[0]).variation) + "</th>");
            } else {
              writer.write("<table class=\"reqirements\"><tr><th scope=\"col\" style=\"text-align:center;\"></th>");
            } 
            List<InvItem> items = new ArrayList<>();
            for (BuildingPlan plan : plans) {
              for (InvItem key : plan.resCost.keySet()) {
                if (!items.contains(key))
                  items.add(key); 
              } 
            } 
            Collections.sort(items);
            for (InvItem key : items) {
              String pict = "Unknown Pict:" + key.item + "/" + key.meta;
              String link = "";
              if (picts.containsKey(key))
                pict = picts.get(key); 
              if (links.containsKey(key))
                link = links.get(key); 
              writer.write("<td>[[File:" + pict + "|32px" + link + "|" + key.getName() + "]]</td>");
            } 
            writer.write("</tr>\n");
            for (BuildingPlan plan : plans) {
              if (plan.level == 0) {
                writer.write("<tr><th scope=\"row\">Construction</th>");
              } else {
                writer.write("<tr><th scope=\"row\">Upgrade " + plan.level + "</th>");
              } 
              for (InvItem key : items) {
                if (plan.resCost.containsKey(key)) {
                  writer.write("<td>" + plan.resCost.get(key) + "</td>");
                  continue;
                } 
                writer.write("<td></td>");
              } 
              writer.write("</tr>\n");
            } 
            writer.write("</table>\n");
          } 
          writer.write("</table>\n\n");
        } 
      } 
      writer.write("| #default = {{msgbox | title = Requirements not found| text = The requirements template couldn't find the upgrade table of the building you were looking for.Please consult the building list at [[Template:Requirements|this page]] to find the correct name.}}}}<noinclude>[[Category:Templates formatting|{{PAGENAME}}]]{{documentation}}</noinclude>");
      writer.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(null, "Wrote resources used wiki.txt"); 
  }
  
  public static void writePlanCostTextStyle(BuildingPlanSet set, BufferedWriter writer) throws IOException {
    writer.write((((BuildingPlan[])set.plans.get(0))[0]).nativeName + "\n" + (((BuildingPlan[])set.plans.get(0))[0]).buildingKey + "\n" + "\n");
    for (BuildingPlan[] plans : set.plans) {
      if (set.plans.size() > 1)
        writer.write("===Variation " + (char)(65 + (plans[0]).variation) + "===" + "\n"); 
      writer.write("\nTotal Cost\n");
      Map<InvItem, Integer> totalCost = new HashMap<>();
      for (BuildingPlan plan : plans) {
        for (InvItem key : plan.resCost.keySet()) {
          if (totalCost.containsKey(key)) {
            totalCost.put(key, Integer.valueOf(((Integer)totalCost.get(key)).intValue() + ((Integer)plan.resCost.get(key)).intValue()));
            continue;
          } 
          totalCost.put(key, plan.resCost.get(key));
        } 
      } 
      for (InvItem key : totalCost.keySet())
        writer.write(key.getName() + "(" + key.item.getRegistryName() + "/" + key.meta + "): " + totalCost.get(key) + "\n"); 
      for (BuildingPlan plan : plans) {
        if (plan.level == 0) {
          writer.write("\nInitial Construction\n");
        } else {
          writer.write("\nUpgrade " + plan.level + "\n");
        } 
        for (InvItem key : plan.resCost.keySet())
          writer.write(key.getName() + "(" + key.item.getRegistryName() + "/" + key.meta + "): " + plan.resCost.get(key) + "\n"); 
      } 
    } 
    writer.write("\n");
  }
  
  public static void writePlanCostWikiStyle(BuildingPlanSet set, BufferedWriter writer) throws IOException {
    writer.write((((BuildingPlan[])set.plans.get(0))[0]).nativeName + "\n" + (((BuildingPlan[])set.plans.get(0))[0]).buildingKey + "\n" + "\n");
    writer.write("==Requirements==\n");
    for (BuildingPlan[] plans : set.plans) {
      if (set.plans.size() > 1)
        writer.write("===Variation " + (char)(65 + (plans[0]).variation) + "===" + "\n"); 
      for (BuildingPlan plan : plans) {
        if (plan.level == 0) {
          writer.write("Initial Construction\n\n");
        } else {
          writer.write("Upgrade " + plan.level + "\n" + "\n");
        } 
        writer.write("{| border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 300px;\"\n");
        writer.write("! scope=\"col\"|Resource\n");
        writer.write("! scope=\"col\"|Quantity\n");
        for (InvItem key : plan.resCost.keySet()) {
          writer.write("|-\n");
          writer.write("| style=\"text-align: center; \"|" + key.getName() + "\n");
          writer.write("| style=\"text-align: center; \"|" + plan.resCost.get(key) + "\n");
        } 
        writer.write("|}\n\n\n");
      } 
    } 
  }
}
