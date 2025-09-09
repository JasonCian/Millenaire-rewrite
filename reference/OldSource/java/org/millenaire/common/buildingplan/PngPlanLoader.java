package org.millenaire.common.buildingplan;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;

public class PngPlanLoader {
  private static void computeCost(BuildingPlan buildingPlan) throws MillLog.MillenaireException {
    buildingPlan.resCost = new HashMap<>();
    for (int i = 0; i < buildingPlan.nbfloors; i++) {
      for (int j = 0; j < buildingPlan.length; j++) {
        for (int k = 0; k < buildingPlan.width; k++) {
          PointType p = buildingPlan.plan[i][j][k];
          if (p.getCostQuantity() > 0) {
            if (p.getCostInvItem().getItem() == Items.AIR)
              MillLog.error(p, "cost in air!"); 
            buildingPlan.addToCost(p.getCostInvItem(), p.getCostQuantity());
          } 
        } 
      } 
    } 
    InvItem stickInvItem = InvItem.createInvItem(Items.STICK);
    if (buildingPlan.resCost.containsKey(stickInvItem)) {
      int stickQuantity = ((Integer)buildingPlan.resCost.get(stickInvItem)).intValue();
      buildingPlan.resCost.remove(stickInvItem);
      buildingPlan.addToCost(Blocks.LOG, -1, (int)Math.max(Math.ceil(stickQuantity * 1.0D / 4.0D), 1.0D));
    } 
    for (BlockPlanks.EnumType plankType : BlockPlanks.EnumType.values()) {
      InvItem plankInvItem = InvItem.createInvItem(Blocks.PLANKS.getDefaultState().withProperty((IProperty)BlockPlanks.VARIANT, (Comparable)plankType));
      if (buildingPlan.resCost.containsKey(plankInvItem)) {
        int plankQuantity = ((Integer)buildingPlan.resCost.get(plankInvItem)).intValue();
        if (plankType != BlockPlanks.EnumType.ACACIA && plankType != BlockPlanks.EnumType.DARK_OAK) {
          buildingPlan.addToCost(Blocks.LOG.getDefaultState().withProperty((IProperty)BlockOldLog.VARIANT, (Comparable)plankType), (int)Math.max(Math.ceil(plankQuantity * 1.0D / 4.0D), 1.0D));
        } else {
          buildingPlan.addToCost(Blocks.LOG2.getDefaultState().withProperty((IProperty)BlockNewLog.VARIANT, (Comparable)plankType), (int)Math.max(Math.ceil(plankQuantity * 1.0D / 4.0D), 1.0D));
        } 
        buildingPlan.resCost.remove(plankInvItem);
      } 
    } 
    InvItem glassPaneInvItem = InvItem.createInvItem(Blocks.GLASS_PANE);
    if (buildingPlan.resCost.containsKey(glassPaneInvItem)) {
      int paneQuantity = ((Integer)buildingPlan.resCost.get(glassPaneInvItem)).intValue();
      buildingPlan.addToCost(Blocks.GLASS.getDefaultState(), (int)Math.max(Math.ceil(paneQuantity * 6.0D / 16.0D), 1.0D));
      buildingPlan.resCost.remove(glassPaneInvItem);
    } 
    InvItem byzSlabInvItem = InvItem.createInvItem((Block)MillBlocks.BYZANTINE_TILES_SLAB, 4);
    if (buildingPlan.resCost.containsKey(byzSlabInvItem)) {
      int slabQuantity = ((Integer)buildingPlan.resCost.get(byzSlabInvItem)).intValue();
      buildingPlan.addToCost((Block)MillBlocks.BYZANTINE_TILES, 0, (int)Math.max(Math.ceil((slabQuantity / 2)), 1.0D));
      buildingPlan.resCost.remove(byzSlabInvItem);
    } 
    InvItem graySlabInvItem = InvItem.createInvItem((Block)MillBlocks.GRAY_TILES_SLAB, 4);
    if (buildingPlan.resCost.containsKey(graySlabInvItem)) {
      int slabQuantity = ((Integer)buildingPlan.resCost.get(graySlabInvItem)).intValue();
      buildingPlan.addToCost((Block)MillBlocks.GRAY_TILES, 0, (int)Math.max(Math.ceil(slabQuantity / 2.0D), 1.0D));
      buildingPlan.resCost.remove(graySlabInvItem);
    } 
    InvItem greenSlabInvItem = InvItem.createInvItem((Block)MillBlocks.GREEN_TILES_SLAB, 4);
    if (buildingPlan.resCost.containsKey(greenSlabInvItem)) {
      int slabQuantity = ((Integer)buildingPlan.resCost.get(greenSlabInvItem)).intValue();
      buildingPlan.addToCost((Block)MillBlocks.GRAY_TILES, 0, (int)Math.max(Math.ceil(slabQuantity / 2.0D), 1.0D));
      buildingPlan.resCost.remove(greenSlabInvItem);
    } 
    InvItem redSlabInvItem = InvItem.createInvItem((Block)MillBlocks.RED_TILES_SLAB, 4);
    if (buildingPlan.resCost.containsKey(redSlabInvItem)) {
      int slabQuantity = ((Integer)buildingPlan.resCost.get(redSlabInvItem)).intValue();
      buildingPlan.addToCost((Block)MillBlocks.GRAY_TILES, 0, (int)Math.max(Math.ceil(slabQuantity / 2.0D), 1.0D));
      buildingPlan.resCost.remove(redSlabInvItem);
    } 
  }
  
  public static BuildingPlan loadFromPngs(File pngFile, String buildingKey, int level, int variation, BuildingPlan previousPlanUpgrade, BuildingMetadataLoader metadataLoader, Culture c, boolean importPlan) {
    BufferedImage PNGFile;
    BuildingPlan buildingPlan = new BuildingPlan();
    char varChar = 'A';
    varChar = (char)(varChar + variation);
    buildingPlan.planName = buildingKey + "_" + varChar + "" + level;
    buildingPlan.buildingKey = buildingKey;
    buildingPlan.isUpdate = (level > 0);
    buildingPlan.level = level;
    buildingPlan.variation = variation;
    buildingPlan.culture = c;
    buildingPlan.setLoadedFromFile(pngFile);
    metadataLoader.loadDataForPlan(buildingPlan, previousPlanUpgrade, importPlan);
    try {
      PNGFile = ImageIO.read(pngFile);
    } catch (IOException e) {
      MillLog.printException("Exception when loading PNG file " + pngFile.getName(), e);
      return null;
    } 
    BufferedImage pictPlan = new BufferedImage(PNGFile.getWidth(), PNGFile.getHeight(), 6);
    Graphics2D fig = pictPlan.createGraphics();
    fig.drawImage(PNGFile, 0, 0, (ImageObserver)null);
    fig.dispose();
    pictPlan.flush();
    buildingPlan.lengthOffset = (int)Math.floor(buildingPlan.length * 0.5D);
    buildingPlan.widthOffset = (int)Math.floor(buildingPlan.width * 0.5D);
    if (pictPlan.getHeight() != buildingPlan.length) {
      MillLog.error(buildingPlan, buildingPlan.planName + ": Expected length is " + buildingPlan.length + " but file height is " + pictPlan.getHeight());
      return null;
    } 
    float fnbfloors = (pictPlan.getWidth() + 1.0F) / (buildingPlan.width + 1.0F);
    if (Math.round(fnbfloors) != fnbfloors) {
      MillLog.error(buildingPlan, buildingPlan.planName + ": With a width of " + buildingPlan.width + ", getting non-integer floor number: " + fnbfloors);
      return null;
    } 
    buildingPlan.nbfloors = (int)fnbfloors;
    buildingPlan.plan = new PointType[buildingPlan.nbfloors][buildingPlan.length][buildingPlan.width];
    if (pictPlan.getType() != 5 && pictPlan.getType() != 6)
      MillLog.error(buildingPlan, "Picture " + buildingPlan.planName + ".png could not be loaded as type TYPE_3BYTE_BGR or TYPE_4BYTE_ABGR but instead as: " + pictPlan.getType()); 
    boolean alphaLayer = false;
    if (pictPlan.getType() == 6)
      alphaLayer = true; 
    boolean sleepingPos = false;
    boolean mainChestPos = false;
    for (int floorPos = 0; floorPos < buildingPlan.nbfloors; floorPos++) {
      for (int lengthPos = 0; lengthPos < buildingPlan.length; lengthPos++) {
        for (int widthPos = 0; widthPos < buildingPlan.width; widthPos++) {
          int py = lengthPos;
          int px = floorPos * (buildingPlan.width + 1);
          px += buildingPlan.width - widthPos - 1;
          int colour = pictPlan.getRGB(px, py);
          if (alphaLayer) {
            if ((colour & 0xFF000000) != -16777216) {
              colour = 16777215;
            } else {
              colour &= 0xFFFFFF;
            } 
          } else {
            colour &= 0xFFFFFF;
          } 
          if (!PointType.colourPoints.containsKey(Integer.valueOf(colour))) {
            MillLog.error(buildingPlan, buildingPlan.planName + ": Unknown colour " + BuildingPlan.getColourString(colour) + " at: " + px + "/" + py + ", skipping it.");
            colour = 16777215;
          } 
          buildingPlan.plan[floorPos][lengthPos][widthPos] = PointType.colourPoints.get(Integer.valueOf(colour));
          String specialType = (buildingPlan.plan[floorPos][lengthPos][widthPos]).specialType;
          if ("sleepingPos".equals(specialType))
            sleepingPos = true; 
          if (buildingPlan.plan[floorPos][lengthPos][widthPos].isSubType("mainchest"))
            mainChestPos = true; 
          if (specialType != null && specialType.equals("mainchestGuess") && level > 0) {
            MillLog.error(buildingPlan, "Main chest detected at " + px + "/" + py + " but we are in an upgrade. Removing it.");
            buildingPlan.plan[floorPos][lengthPos][widthPos] = PointType.colourPoints.get(Integer.valueOf(16777215));
          } 
        } 
      } 
    } 
    try {
      computeCost(buildingPlan);
    } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
      MillLog.printException("Exception when computing the cost of building plan " + buildingPlan, (Throwable)e);
      return null;
    } 
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(buildingPlan, "Loaded plan " + buildingKey + "_" + level + ": " + buildingPlan.nativeName + " pop: " + buildingPlan.maleResident + "/" + buildingPlan.femaleResident + "/priority:" + buildingPlan.priority); 
    if (!sleepingPos) {
      if ((buildingPlan.maleResident.size() > 0 || buildingPlan.femaleResident.size() > 0) && level == 0)
        MillLog.error(buildingPlan, "Has residents but the sleeping pos is missing!"); 
      if (buildingPlan.level == 0 && !buildingPlan.tags.isEmpty())
        MillLog.error(buildingPlan, "All tagged plans should have at least a sleeping pos. It is used for all goals targetting the plan."); 
    } 
    if (mainChestPos && level > 0)
      MillLog.error(buildingPlan, "Plan beyond level 0 should never include main chests!"); 
    if (!mainChestPos && level == 0 && buildingPlan.isSubBuilding)
      MillLog.error(buildingPlan, "Sub-buildings absolutely must have a main chest."); 
    validateBuildingPlan(buildingPlan);
    return buildingPlan;
  }
  
  private static void validateBuildingPlan(BuildingPlan buildingPlan) {
    int pigs = 0, sheep = 0, chicken = 0, cow = 0;
    for (int i = 0; i < buildingPlan.nbfloors; i++) {
      for (int j = 0; j < buildingPlan.length; j++) {
        for (int k = 0; k < buildingPlan.width; k++) {
          if (buildingPlan.plan[i][j][k].isType("chickenspawn") && !buildingPlan.containsTags("chicken")) {
            MillLog.warning(buildingPlan, "Building has chicken spawn but no chicken tag.");
          } else if (buildingPlan.plan[i][j][k].isType("cowspawn") && !buildingPlan.containsTags("cattle")) {
            MillLog.warning(buildingPlan, "Building has cattle spawn but no cattle tag.");
          } else if (buildingPlan.plan[i][j][k].isType("sheepspawn") && !buildingPlan.containsTags("sheeps")) {
            MillLog.warning(buildingPlan, "Building has sheeps spawn but no sheeps tag.");
          } else if (buildingPlan.plan[i][j][k].isType("pigspawn") && !buildingPlan.containsTags("pigs")) {
            MillLog.warning(buildingPlan, "Building has pig spawn but no pig tag.");
          } else if (buildingPlan.plan[i][j][k].isType("squidspawn") && !buildingPlan.containsTags("squids")) {
            MillLog.warning(buildingPlan, "Building has squid spawn but no squid tag.");
          } 
          if (buildingPlan.plan[i][j][k].isType("chickenspawn")) {
            chicken++;
          } else if (buildingPlan.plan[i][j][k].isType("cowspawn")) {
            cow++;
          } else if (buildingPlan.plan[i][j][k].isType("sheepspawn")) {
            sheep++;
          } else if (buildingPlan.plan[i][j][k].isType("pigspawn")) {
            pigs++;
          } else if (buildingPlan.plan[i][j][k].isSubType("lockedchest") || buildingPlan.plan[i][j][k].isSubType("mainchest")) {
            if (buildingPlan.isgift || buildingPlan.price > 0)
              MillLog.warning(buildingPlan, "The building plan has a main chest or locked chests despite being a player building."); 
          } else if (buildingPlan.plan[i][j][k].getBlock() == Blocks.CHEST && 
            !buildingPlan.isgift && buildingPlan.price == 0) {
            MillLog.warning(buildingPlan, "The building plan has regular chests despite not being a player building.");
          } 
        } 
      } 
    } 
    if (chicken % 2 == 1)
      MillLog.warning(buildingPlan, "Odd number of chicken spawn: " + chicken); 
    if (sheep % 2 == 1)
      MillLog.warning(buildingPlan, "Odd number of sheep spawn: " + sheep); 
    if (cow % 2 == 1)
      MillLog.warning(buildingPlan, "Odd number of cow spawn: " + cow); 
    if (pigs % 2 == 1)
      MillLog.warning(buildingPlan, "Odd number of pigs spawn: " + pigs); 
  }
}
