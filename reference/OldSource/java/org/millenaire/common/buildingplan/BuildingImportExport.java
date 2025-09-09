package org.millenaire.common.buildingplan;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.block.mock.MockBlockAnimalSpawn;
import org.millenaire.common.block.mock.MockBlockDecor;
import org.millenaire.common.block.mock.MockBlockFree;
import org.millenaire.common.block.mock.MockBlockMarker;
import org.millenaire.common.block.mock.MockBlockSoil;
import org.millenaire.common.block.mock.MockBlockSource;
import org.millenaire.common.block.mock.MockBlockTreeSpawn;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.TileEntityImportTable;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.BuildingLocation;

public class BuildingImportExport {
  public static String EXPORT_DIR = "exportdir";
  
  private static HashMap<Integer, PointType> reverseColourPoints = new HashMap<>();
  
  public static Point adjustForOrientation(int x, int y, int z, int xoffset, int zoffset, int orientation) {
    Point pos = null;
    if (orientation == 0) {
      pos = new Point((x + xoffset), y, (z + zoffset));
    } else if (orientation == 1) {
      pos = new Point((x + zoffset), y, (z - xoffset));
    } else if (orientation == 2) {
      pos = new Point((x - xoffset), y, (z - zoffset - 1));
    } else if (orientation == 3) {
      pos = new Point((x - zoffset - 1), y, (z + xoffset));
    } 
    return pos;
  }
  
  private static void copyPlanSetToExportDir(BuildingPlanSet planSet) {
    File exportDir = MillCommonUtilities.getExportDir();
    Path exportPath = exportDir.toPath();
    Path inputPath = planSet.getFirstStartingPlan().getLoadedFromFile().toPath().getParent();
    try {
      for (int exportVariation = 0; exportVariation < planSet.plans.size(); exportVariation++) {
        char exportVariationLetter = (char)(65 + exportVariation);
        String txtFileName = planSet.key + "_" + exportVariationLetter + ".txt";
        Files.copy(inputPath.resolve(txtFileName), exportPath.resolve(txtFileName), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
        for (int buildingUpgrade = 0; buildingUpgrade < ((BuildingPlan[])planSet.plans.get(exportVariation)).length; buildingUpgrade++) {
          String pngFileName = planSet.key + "_" + exportVariationLetter + buildingUpgrade + ".png";
          Files.copy(inputPath.resolve(pngFileName), exportPath.resolve(pngFileName), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
        } 
      } 
    } catch (IOException e) {
      MillLog.printException("Error when copying files to export dir:", e);
    } 
  }
  
  private static void doubleHeightPlan(EntityPlayer player, BuildingPlanSet existingSet) {
    for (BuildingPlan[] plans : existingSet.plans) {
      for (BuildingPlan plan : plans) {
        PointType[][][] newPlan = new PointType[plan.plan.length * 2][(plan.plan[0]).length][(plan.plan[0][0]).length];
        for (int i = 0; i < plan.plan.length; i++) {
          for (int j = 0; j < (plan.plan[0]).length; j++) {
            for (int k = 0; k < (plan.plan[0][0]).length; k++) {
              newPlan[i * 2][j][k] = plan.plan[i][j][k];
              newPlan[i * 2 + 1][j][k] = plan.plan[i][j][k];
            } 
          } 
        } 
        plan.plan = newPlan;
        plan.nbfloors *= 2;
      } 
    } 
    ServerSender.sendTranslatedSentence(player, 'f', "import.doublevertical", new String[0]);
    MillLog.major(null, "Building height: " + (((BuildingPlan[])existingSet.plans.get(0))[0]).plan.length);
  }
  
  private static void drawWoolBorders(EntityPlayer player, Point tablePos, int orientatedLength, int orientatedWidth) {
    for (int x = 1; x <= orientatedLength; x++) {
      int meta = 0;
      if ((x - 1) % 10 < 5)
        meta = 14; 
      tablePos.getRelative(x, -1.0D, 0.0D).setBlock(player.world, Blocks.WOOL, meta, true, false);
      tablePos.getRelative(x, -1.0D, (orientatedWidth + 1)).setBlock(player.world, Blocks.WOOL, meta, true, false);
    } 
    for (int z = 1; z <= orientatedWidth; z++) {
      int meta = 0;
      if ((z - 1) % 10 < 5)
        meta = 14; 
      tablePos.getRelative(0.0D, -1.0D, z).setBlock(player.world, Blocks.WOOL, meta, true, false);
      tablePos.getRelative((orientatedLength + 1), -1.0D, z).setBlock(player.world, Blocks.WOOL, meta, true, false);
    } 
    tablePos.getRelative(0.0D, -1.0D, 0.0D).setBlock(player.world, Blocks.WOOL, 15, true, false);
    tablePos.getRelative((orientatedLength + 1), -1.0D, 0.0D).setBlock(player.world, Blocks.WOOL, 15, true, false);
    tablePos.getRelative(0.0D, -1.0D, (orientatedWidth + 1)).setBlock(player.world, Blocks.WOOL, 15, true, false);
    tablePos.getRelative((orientatedLength + 1), -1.0D, (orientatedWidth + 1)).setBlock(player.world, Blocks.WOOL, 15, true, false);
  }
  
  public static int exportBuilding(World world, Point startPoint, String planName, int variation, int length, int width, int orientation, int upgradeLevel, int startLevel, boolean exportSnow, boolean exportRegularChests, boolean autoconvertToPreserveGround) throws Exception, IOException, UnsupportedEncodingException, FileNotFoundException {
    loadReverseBuildingPoints(Boolean.valueOf(autoconvertToPreserveGround), Boolean.valueOf(exportRegularChests));
    File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
    if (!exportDir.exists())
      exportDir.mkdirs(); 
    char variationLetter = 'A';
    variationLetter = (char)(variationLetter + variation);
    File buildingFile = new File(exportDir, planName + "_" + variationLetter + ".txt");
    PointType[][][] existingPoints = (PointType[][][])null;
    int existingMinLevel = 0;
    BuildingPlanSet existingSet = null;
    if (buildingFile.exists()) {
      existingSet = loadPlanSetFromExportDir(planName);
      if ((((BuildingPlan[])existingSet.plans.get(variation))[0]).length != length) {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorlength", new String[] { "" + length, "" + (((BuildingPlan[])existingSet.plans.get(variation))[0]).length });
        return upgradeLevel;
      } 
      if ((((BuildingPlan[])existingSet.plans.get(variation))[0]).width != width) {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorwidth", new String[] { "" + width, "" + (((BuildingPlan[])existingSet.plans.get(variation))[0]).width });
        return upgradeLevel;
      } 
      if (upgradeLevel == -1)
        upgradeLevel = ((BuildingPlan[])existingSet.plans.get(variation)).length; 
      if ((((BuildingPlan[])existingSet.plans.get(variation))[0]).parentBuildingPlan != null) {
        String parentBuildingPlanKey = (((BuildingPlan[])existingSet.plans.get(variation))[0]).parentBuildingPlan;
        String parentSuffix = parentBuildingPlanKey.split("_")[(parentBuildingPlanKey.split("_")).length - 1].toUpperCase();
        int parentVariation = parentSuffix.charAt(0) - 65;
        int parentLevel = Integer.parseInt(parentSuffix.substring(1, parentSuffix.length()));
        String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
        BuildingPlanSet parentSet = loadPlanSetFromExportDir(parentBuildingKey);
        existingPoints = getConsolidatedPlanWithParent(parentSet, parentVariation, parentLevel, existingSet, variation, upgradeLevel - 1);
        existingMinLevel = Math.min(existingSet.getMinLevel(variation, upgradeLevel - 1), parentSet.getMinLevel(parentVariation, parentLevel));
      } else {
        existingPoints = getConsolidatedPlan(existingSet, variation, upgradeLevel - 1);
        existingMinLevel = existingSet.getMinLevel(variation, upgradeLevel - 1);
      } 
    } else {
      upgradeLevel = 0;
    } 
    List<PointType[][]> export = (List)new ArrayList<>();
    boolean stop = false;
    int orientatedLength = length;
    int orientatedWidth = width;
    if (orientation % 2 == 1) {
      orientatedLength = width;
      orientatedWidth = length;
    } 
    Point centrePos = startPoint.getRelative((orientatedLength / 2 + 1), 0.0D, (orientatedWidth / 2 + 1));
    int x = centrePos.getiX();
    int y = centrePos.getiY();
    int z = centrePos.getiZ();
    int lengthOffset = (int)Math.floor(length * 0.5D);
    int widthOffset = (int)Math.floor(width * 0.5D);
    int dy = 0;
    while (!stop) {
      PointType[][] level = new PointType[length][width];
      boolean blockFound = false;
      for (int dx = 0; dx < length; dx++) {
        for (int dz = 0; dz < width; dz++) {
          level[dx][dz] = null;
          Point p = adjustForOrientation(x, y + dy + startLevel, z, dx - lengthOffset, dz - widthOffset, orientation);
          Block block = WorldUtilities.getBlock(world, p);
          int meta = WorldUtilities.getBlockMeta(world, p);
          if (block != Blocks.AIR)
            blockFound = true; 
          if (block instanceof BlockFlowerPot)
            meta = ((BlockFlowerPot.EnumFlowerType)WorldUtilities.getBlockState(world, p).getActualState((IBlockAccess)world, new BlockPos(p.x, p.y, p.z)).getValue((IProperty)BlockFlowerPot.CONTENTS)).ordinal(); 
          PointType pt = reverseColourPoints.get(Integer.valueOf(getPointHash(block, meta)));
          if (pt != null) {
            if (exportSnow || pt.getBlock() != Blocks.SNOW_LAYER) {
              PointType existing = null;
              if (existingPoints != null && dy + startLevel >= existingMinLevel && dy + startLevel < existingMinLevel + existingPoints.length) {
                existing = existingPoints[dy + startLevel - existingMinLevel][dx][dz];
                if (existing == null)
                  MillLog.major(null, "Existing pixel is null"); 
              } 
              if (existing == null) {
                if (pt.specialType != null || pt.getBlock() != Blocks.AIR || upgradeLevel != 0)
                  level[dx][dz] = pt; 
              } else if (existing != pt && (!existing.isType("empty") || pt.getBlock() != Blocks.AIR)) {
                level[dx][dz] = pt;
              } 
            } 
          } else if (!(block instanceof net.minecraft.block.BlockBed) && !(block instanceof net.minecraft.block.BlockDoublePlant) && !(block instanceof net.minecraft.block.BlockLiquid)) {
            Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorunknownblockid", new String[] { "" + block + "/" + meta + "/" + 
                  getPointHash(block, meta) });
          } 
        } 
      } 
      if (blockFound || (existingPoints != null && export.size() < existingPoints.length)) {
        export.add(level);
      } else {
        stop = true;
      } 
      dy++;
      if (dy + startPoint.getiY() + startLevel >= 256)
        stop = true; 
    } 
    BufferedImage pict = new BufferedImage(export.size() * width + export.size() - 1, length, 1);
    Graphics2D graphics = pict.createGraphics();
    graphics.setColor(new Color(11730865));
    graphics.fillRect(0, 0, pict.getWidth(), pict.getHeight());
    for (dy = 0; dy < export.size(); dy++) {
      PointType[][] level = export.get(dy);
      for (int i = 0; i < length; i++) {
        for (int k = 0; k < width; k++) {
          int colour = 16777215;
          PointType pt = level[i][k];
          if (pt != null)
            colour = pt.colour; 
          graphics.setColor(new Color(colour));
          graphics.fillRect(dy * width + dy + width - k - 1, i, 1, 1);
        } 
      } 
    } 
    String fileName = planName + "_" + variationLetter + upgradeLevel + ".png";
    ImageIO.write(pict, "png", new File(exportDir, fileName));
    if (upgradeLevel == 0 && existingSet == null) {
      BufferedWriter writer = MillCommonUtilities.getWriter(new File(exportDir, planName + "_" + variationLetter + ".txt"));
      writer.write("building.length=" + length + "\n");
      writer.write("building.width=" + width + "\n");
      writer.write("\n");
      writer.write("initial.startlevel=" + startLevel + "\n");
      writer.write("initial.nativename=" + planName + "\n");
      writer.close();
    } else if (upgradeLevel > ((BuildingPlan[])existingSet.plans.get(variation)).length) {
      BufferedWriter writer = MillCommonUtilities.getAppendWriter(new File(exportDir, planName + "_" + variationLetter + ".txt"));
      writer.write("upgrade" + upgradeLevel + ".startlevel=" + startLevel + "\n");
      writer.close();
    } 
    Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), 'f', "export.buildingexported", new String[] { fileName });
    return upgradeLevel;
  }
  
  private static PointType[][][] getConsolidatedPlan(BuildingPlanSet planSet, int variation, int upgradeLevel) {
    int minLevel = planSet.getMinLevel(variation, upgradeLevel);
    int maxLevel = planSet.getMaxLevel(variation, upgradeLevel);
    int length = ((((BuildingPlan[])planSet.plans.get(variation))[0]).plan[0]).length;
    int width = ((((BuildingPlan[])planSet.plans.get(variation))[0]).plan[0][0]).length;
    PointType[][][] consolidatedPlan = new PointType[maxLevel - minLevel][length][width];
    for (int lid = 0; lid <= upgradeLevel; lid++) {
      BuildingPlan plan = ((BuildingPlan[])planSet.plans.get(variation))[lid];
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(planSet, "Consolidating plan: adding level " + lid); 
      int ioffset = plan.startLevel - minLevel;
      for (int i = 0; i < plan.plan.length; i++) {
        for (int j = 0; j < length; j++) {
          for (int k = 0; k < width; k++) {
            PointType pt = plan.plan[i][j][k];
            if (!pt.isType("empty") || lid == 0)
              consolidatedPlan[i + ioffset][j][k] = pt; 
          } 
        } 
      } 
    } 
    return consolidatedPlan;
  }
  
  private static PointType[][][] getConsolidatedPlanWithParent(BuildingPlanSet parentSet, int parentVariation, int parentUpgradeLevel, BuildingPlanSet planSet, int variation, int upgradeLevel) {
    int minLevel = Math.min(planSet.getMinLevel(variation, upgradeLevel), parentSet.getMinLevel(parentVariation, parentUpgradeLevel));
    int maxLevel = Math.max(planSet.getMaxLevel(variation, upgradeLevel), parentSet.getMaxLevel(parentVariation, parentUpgradeLevel));
    int length = ((((BuildingPlan[])planSet.plans.get(variation))[0]).plan[0]).length;
    int width = ((((BuildingPlan[])planSet.plans.get(variation))[0]).plan[0][0]).length;
    PointType[][][] consolidatedPlan = new PointType[maxLevel - minLevel][length][width];
    for (int lid = 0; lid <= parentUpgradeLevel; lid++) {
      BuildingPlan plan = ((BuildingPlan[])parentSet.plans.get(parentVariation))[lid];
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(parentSet, "Consolidating plan: adding level " + lid); 
      int ioffset = plan.startLevel - minLevel;
      for (int j = 0; j < plan.plan.length; j++) {
        for (int k = 0; k < length; k++) {
          for (int m = 0; m < width; m++) {
            PointType pt = plan.plan[j][k][m];
            if (!pt.isType("empty") || lid == 0)
              consolidatedPlan[j + ioffset][k][m] = pt; 
          } 
        } 
      } 
    } 
    PointType airPt = reverseColourPoints.get(Integer.valueOf(getPointHash(Blocks.AIR, 0)));
    if (parentSet.getMaxLevel(parentVariation, parentUpgradeLevel) < planSet.getMaxLevel(variation, upgradeLevel))
      for (int j = parentSet.getMaxLevel(parentVariation, parentUpgradeLevel); j <= planSet.getMaxLevel(variation, upgradeLevel); j++) {
        for (int k = 0; k < length; k++) {
          for (int m = 0; m < width; m++)
            consolidatedPlan[j][k][m] = airPt; 
        } 
      }  
    for (int i = 0; i <= upgradeLevel; i++) {
      BuildingPlan plan = ((BuildingPlan[])planSet.plans.get(variation))[i];
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(planSet, "Consolidating plan: adding level " + i); 
      int ioffset = plan.startLevel - minLevel;
      for (int j = 0; j < plan.plan.length; j++) {
        for (int k = 0; k < length; k++) {
          for (int m = 0; m < width; m++) {
            PointType pt = plan.plan[j][k][m];
            if (!pt.isType("empty"))
              consolidatedPlan[j + ioffset][k][m] = pt; 
          } 
        } 
      } 
    } 
    return consolidatedPlan;
  }
  
  private static int getPointHash(Block b, int meta) {
    if (b != null)
      return (b.getRegistryName() + "_" + meta).hashCode(); 
    return ("unknownBlock_" + meta).hashCode();
  }
  
  private static int getPointHash(IBlockState bs) {
    if (bs != null)
      return (bs.getBlock().getRegistryName() + "_" + bs.getBlock().getMetaFromState(bs)).hashCode(); 
    return "unknownBlock".hashCode();
  }
  
  private static PointType getPointTypeFromBlockState(IBlockState blockState) {
    for (PointType newPt : PointType.colourPoints.values()) {
      if (newPt.getBlockState() != null && newPt.getBlockState().equals(blockState))
        return newPt; 
    } 
    return null;
  }
  
  public static void importTableCreateNewBuilding(EntityPlayer player, TileEntityImportTable importTable, int length, int width, int startLevel, boolean clearGround) {
    File exportDir = MillCommonUtilities.getExportDir();
    VirtualDir exportVirtualDir = new VirtualDir(exportDir);
    int exportNumber = 1;
    while (exportVirtualDir.getChildFile("export" + exportNumber + "_A.txt") != null)
      exportNumber++; 
    if (clearGround)
      for (int x = importTable.getPos().getX(); x < importTable.getPos().getX() + 2 + length; x++) {
        for (int z = importTable.getPos().getZ(); z < importTable.getPos().getZ() + 2 + width; z++) {
          int startingY = Math.min(0, startLevel);
          int y;
          for (y = importTable.getPos().getY() + startingY; y < importTable.getPos().getY(); y++)
            player.world.setBlockState(new BlockPos(x, y, z), MillBlocks.MARKER_BLOCK.getDefaultState().withProperty((IProperty)MockBlockMarker.VARIANT, (Comparable)MockBlockMarker.Type.PRESERVE_GROUND)); 
          for (y = importTable.getPos().getY(); y < importTable.getPos().getY() + 50; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!pos.equals(importTable.getPos()))
              player.world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState()); 
          } 
        } 
      }  
    drawWoolBorders(player, importTable.getPosPoint(), length, width);
    importTable.updatePlan("export" + exportNumber, length, width, 0, 0, startLevel, player);
  }
  
  public static void importTableExportBuildingPlan(World world, TileEntityImportTable importTable, int level) {
    if (importTable.getOrientation() != 0) {
      Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.northfacingonly", new String[0]);
      return;
    } 
    try {
      int upgradeLevelExported = exportBuilding(world, new Point(importTable.getPos()), importTable.getBuildingKey(), importTable.getVariation(), importTable
          .getLength(), importTable.getWidth(), importTable.getOrientation(), level, importTable.getStartingLevel(), importTable.exportSnow(), importTable.exportRegularChests(), importTable
          .autoconvertToPreserveGround());
      if (upgradeLevelExported != level)
        ClientSender.importTableUpdateSettings(new Point(importTable.getPos()), upgradeLevelExported, importTable.getOrientation(), importTable.getStartingLevel(), importTable.exportSnow(), importTable
            .importMockBlocks(), importTable.autoconvertToPreserveGround(), importTable.exportRegularChests()); 
    } catch (Exception e) {
      MillLog.printException("Error when exporting building:", e);
    } 
  }
  
  public static void importTableExportPlanCost(String buildingKey) {
    BuildingPlanSet set = loadPlanSetFromExportDir(buildingKey);
    File file = new File(MillCommonUtilities.getExportDir(), set.key + " resources used.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      BuildingDevUtilities.writePlanCostTextStyle(set, writer);
      writer.close();
      Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), 'f', "importtable.costexported", new String[] { "export/" + file.getName() });
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public static int importTableHandleImportRequest(EntityPlayer player, Point tablePos, String source, String buildingKey, boolean importAll, int variation, int level, int orientation, boolean importMockBlocks) {
    BuildingPlanSet importSet = null;
    if (source.equals(EXPORT_DIR)) {
      importSet = loadPlanSetFromExportDir(buildingKey);
    } else {
      importSet = Culture.getCultureByName(source).getBuildingPlanSet(buildingKey);
      copyPlanSetToExportDir(importSet);
      if ((importSet.getFirstStartingPlan()).parentBuildingPlan != null) {
        String parentBuildingPlanKey = (importSet.getFirstStartingPlan()).parentBuildingPlan;
        String parentSuffix = parentBuildingPlanKey.split("_")[(parentBuildingPlanKey.split("_")).length - 1].toUpperCase();
        String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
        copyPlanSetToExportDir(Culture.getCultureByName(source).getBuildingPlanSet(parentBuildingKey));
      } 
    } 
    if (importSet != null) {
      if (!importAll) {
        importTableImportBuilding(player, tablePos, null, importSet, variation, level, orientation, importMockBlocks);
        return (((BuildingPlan[])importSet.plans.get(variation))[0]).length + 2 + (((BuildingPlan[])importSet.plans.get(variation))[0]).areaToClear;
      } 
      int xDelta = 0;
      for (int aVariation = 0; aVariation < importSet.plans.size(); aVariation++) {
        BuildingPlan basePlan = ((BuildingPlan[])importSet.plans.get(aVariation))[0];
        int orientatedLength = basePlan.length;
        int orientatedWidth = basePlan.width;
        int orientedGapLength = basePlan.areaToClearLengthAfter + basePlan.areaToClearLengthBefore;
        int orientedGapWidth = basePlan.areaToClearWidthAfter + basePlan.areaToClearWidthBefore;
        if (orientation % 2 == 1) {
          orientatedLength = basePlan.width;
          orientatedWidth = basePlan.length;
          orientedGapLength = basePlan.areaToClearWidthAfter + basePlan.areaToClearWidthBefore;
          orientedGapWidth = basePlan.areaToClearLengthAfter + basePlan.areaToClearLengthBefore;
        } 
        int zDelta = 0;
        for (int aLevel = 0; aLevel < ((BuildingPlan[])importSet.plans.get(aVariation)).length; aLevel++) {
          importTableImportBuilding(player, tablePos.getRelative(xDelta, 0.0D, zDelta), tablePos, importSet, aVariation, aLevel, orientation, importMockBlocks);
          zDelta += orientatedWidth + 2 + orientedGapWidth;
        } 
        xDelta += orientatedLength + 2 + orientedGapLength;
      } 
      return xDelta;
    } 
    return 0;
  }
  
  public static void importTableImportBuilding(EntityPlayer player, Point tablePos, Point parentTablePos, BuildingPlanSet planSet, int variation, int maxLevel, int orientation, boolean importMockBlocks) {
    BuildingPlan basePlan = planSet.getPlan(variation, 0);
    int orientatedLength = basePlan.length;
    int orientatedWidth = basePlan.width;
    if (orientation % 2 == 1) {
      orientatedLength = basePlan.width;
      orientatedWidth = basePlan.length;
    } 
    if (basePlan.parentBuildingPlan != null) {
      String parentBuildingPlanKey = basePlan.parentBuildingPlan;
      String parentSuffix = parentBuildingPlanKey.split("_")[(parentBuildingPlanKey.split("_")).length - 1].toUpperCase();
      int parentVariation = parentSuffix.charAt(0) - 65;
      int parentLevel = Integer.parseInt(parentSuffix.substring(1, parentSuffix.length()));
      String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
      BuildingPlanSet parentBuildingSet = loadPlanSetFromExportDir(parentBuildingKey);
      BuildingLocation parentLocation = new BuildingLocation(parentBuildingSet.getPlan(parentVariation, parentLevel), tablePos.getRelative((orientatedLength / 2 + 1), 0.0D, (orientatedWidth / 2 + 1)), orientation);
      for (int i = 0; i <= parentLevel; i++) {
        parentLocation.level = i;
        parentBuildingSet.buildLocation(Mill.getMillWorld(player.world), null, parentLocation, !importMockBlocks, false, null, true, importMockBlocks, null);
      } 
    } 
    BuildingLocation location = new BuildingLocation(basePlan, tablePos.getRelative((orientatedLength / 2 + 1), 0.0D, (orientatedWidth / 2 + 1)), orientation);
    for (int level = 0; level <= maxLevel; level++) {
      location.level = level;
      planSet.buildLocation(Mill.getMillWorld(player.world), null, location, true, false, null, true, importMockBlocks, null);
    } 
    drawWoolBorders(player, tablePos, orientatedLength, orientatedWidth);
    TileEntityImportTable table = tablePos.getImportTable(player.world);
    if (table == null) {
      tablePos.setBlock(player.world, (Block)MillBlocks.IMPORT_TABLE, 0, true, false);
      table = tablePos.getImportTable(player.world);
    } 
    if (table == null) {
      MillLog.error(null, "Can neither find nor create import table at location: " + tablePos);
    } else {
      BuildingPlan plan = planSet.getPlan(variation, maxLevel);
      table.updatePlan(planSet.key, plan.length, plan.width, variation, maxLevel, plan.startLevel, player);
      table.setParentTablePos(parentTablePos);
    } 
    ServerSender.sendTranslatedSentence(player, 'f', "importtable.importedbuildingplan", new String[] { (planSet.getPlan(variation, maxLevel)).planName });
  }
  
  private static BuildingPlanSet loadPlanSetFromExportDir(String parentBuildingKey) {
    File exportDir = MillCommonUtilities.getExportDir();
    VirtualDir exportVirtualDir = new VirtualDir(exportDir);
    File parentBuildingFile = new File(exportDir, parentBuildingKey + "_A.txt");
    BuildingPlanSet parentBuildingSet = new BuildingPlanSet(null, parentBuildingKey, exportVirtualDir, parentBuildingFile);
    try {
      parentBuildingSet.loadPictPlans(true);
    } catch (Exception e) {
      MillLog.printException("Exception when loading plan:", e);
    } 
    return parentBuildingSet;
  }
  
  public static void loadReverseBuildingPoints(Boolean exportPreserveGround, Boolean exportRegularChests) {
    reverseColourPoints.clear();
    for (PointType pt : PointType.colourPoints.values()) {
      if (pt.specialType == null) {
        Block block = pt.getBlock();
        if (block == null) {
          MillLog.error(pt, "PointType has neither name nor block.");
          continue;
        } 
        reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState())), pt);
        if (block == Blocks.LEAVES || block == Blocks.LEAVES2) {
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlock(), pt
                  .getBlock().getMetaFromState(pt.getBlockState().withProperty((IProperty)BlockLeaves.DECAYABLE, Boolean.valueOf(true)).withProperty((IProperty)BlockLeaves.CHECK_DECAY, Boolean.valueOf(true))))), pt);
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlock(), pt
                  .getBlock().getMetaFromState(pt.getBlockState().withProperty((IProperty)BlockLeaves.DECAYABLE, Boolean.valueOf(true)).withProperty((IProperty)BlockLeaves.CHECK_DECAY, Boolean.valueOf(false))))), pt);
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlock(), pt
                  .getBlock().getMetaFromState(pt.getBlockState().withProperty((IProperty)BlockLeaves.DECAYABLE, Boolean.valueOf(false)).withProperty((IProperty)BlockLeaves.CHECK_DECAY, Boolean.valueOf(true))))), pt);
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlock(), pt
                  .getBlock().getMetaFromState(pt.getBlockState().withProperty((IProperty)BlockLeaves.DECAYABLE, Boolean.valueOf(false)).withProperty((IProperty)BlockLeaves.CHECK_DECAY, Boolean.valueOf(false))))), pt);
          continue;
        } 
        if (BlockItemUtilities.isPath(block)) {
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState().withProperty((IProperty)IBlockPath.STABLE, Boolean.valueOf(false)))), pt);
          continue;
        } 
        if (pt.getBlock() == MillBlocks.PANEL) {
          reverseColourPoints.put(Integer.valueOf(getPointHash(Blocks.WALL_SIGN, pt.getMeta())), pt);
          continue;
        } 
        if (pt.getBlock() instanceof BlockDoor) {
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState())), pt);
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState().withProperty((IProperty)BlockDoor.OPEN, Boolean.valueOf(true)))), pt);
          continue;
        } 
        if (pt.getBlock() instanceof BlockFenceGate) {
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState())), pt);
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState().withProperty((IProperty)BlockFenceGate.OPEN, Boolean.valueOf(true)))), pt);
          continue;
        } 
        if (pt.getBlock() == Blocks.FURNACE) {
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlockState())), pt);
          int meta = pt.getBlock().getMetaFromState(pt.getBlockState());
          IBlockState litBlockState = Blocks.LIT_FURNACE.getStateFromMeta(meta);
          reverseColourPoints.put(Integer.valueOf(getPointHash(litBlockState)), pt);
          continue;
        } 
        if (pt.getBlock() instanceof BlockFlowerPot)
          reverseColourPoints.put(Integer.valueOf(getPointHash(pt.getBlock(), pt.getMeta())), pt); 
      } 
    } 
    for (PointType pt : PointType.colourPoints.values()) {
      if (pt.specialType != null) {
        if (pt.specialType.equals("brewingstand")) {
          for (int i = 0; i < 16; i++)
            reverseColourPoints.put(Integer.valueOf(getPointHash(Blocks.BREWING_STAND, i)), pt); 
          continue;
        } 
        if (pt.specialType.equals("lockedchestTop")) {
          IBlockState bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.WEST);
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          if (!exportRegularChests.booleanValue()) {
            bs = Blocks.CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.WEST);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
          continue;
        } 
        if (pt.specialType.equals("lockedchestBottom")) {
          IBlockState bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.EAST);
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          if (!exportRegularChests.booleanValue()) {
            bs = Blocks.CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.EAST);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
          continue;
        } 
        if (pt.specialType.equals("lockedchestLeft")) {
          IBlockState bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.SOUTH);
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          if (!exportRegularChests.booleanValue()) {
            bs = Blocks.CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.SOUTH);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
          continue;
        } 
        if (pt.specialType.equals("lockedchestRight")) {
          IBlockState bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.NORTH);
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          if (!exportRegularChests.booleanValue()) {
            bs = Blocks.CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.NORTH);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
          continue;
        } 
        if (pt.specialType.equals("mainchestTop")) {
          IBlockState bs = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.WEST.getIndex());
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.specialType.equals("mainchestBottom")) {
          IBlockState bs = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.EAST.getIndex());
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.specialType.equals("mainchestLeft")) {
          IBlockState bs = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.SOUTH.getIndex());
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.specialType.equals("mainchestRight")) {
          IBlockState bs = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.NORTH.getIndex());
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.specialType.equals("grass") && !exportPreserveGround.booleanValue()) {
          IBlockState bs = Blocks.GRASS.getDefaultState();
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.isSubType("villageBannerWall")) {
          String facing = pt.specialType.substring(17);
          IBlockState bs = MillBlocks.VILLAGE_BANNER_WALL.getDefaultState().withProperty((IProperty)BlockBanner.FACING, (Comparable)EnumFacing.byName(facing));
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.isSubType("villageBannerStanding")) {
          int rotation = Integer.parseInt(pt.specialType.substring(21));
          IBlockState bs = MillBlocks.VILLAGE_BANNER_STANDING.getStateFromMeta(rotation);
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.isSubType("cultureBannerWall")) {
          String facing = pt.specialType.substring(17);
          IBlockState bs = MillBlocks.CULTURE_BANNER_WALL.getDefaultState().withProperty((IProperty)BlockBanner.FACING, (Comparable)EnumFacing.byName(facing));
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        if (pt.isSubType("cultureBannerStanding")) {
          int rotation = Integer.parseInt(pt.specialType.substring(21));
          IBlockState bs = MillBlocks.CULTURE_BANNER_STANDING.getStateFromMeta(rotation);
          reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          continue;
        } 
        for (MockBlockMarker.Type type : MockBlockMarker.Type.values()) {
          if (type.name.equalsIgnoreCase(pt.specialType)) {
            IBlockState bs = MillBlocks.MARKER_BLOCK.getStateFromMeta(type.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
        if (exportPreserveGround.booleanValue() && 
          "preserveground".equalsIgnoreCase(pt.specialType)) {
          reverseColourPoints.put(Integer.valueOf(getPointHash((Block)Blocks.GRASS, 0)), pt);
          reverseColourPoints.put(Integer.valueOf(getPointHash((Block)Blocks.SAND, 0)), pt);
        } 
        for (MockBlockAnimalSpawn.Creature creature : MockBlockAnimalSpawn.Creature.values()) {
          if (pt.specialType.equalsIgnoreCase(creature.name + "spawn")) {
            IBlockState bs = MillBlocks.ANIMAL_SPAWN.getStateFromMeta(creature.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
        for (MockBlockSource.Resource resource : MockBlockSource.Resource.values()) {
          if (pt.specialType.equalsIgnoreCase(resource.name + "source")) {
            IBlockState bs = MillBlocks.SOURCE.getStateFromMeta(resource.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
        for (MockBlockFree.Resource resource : MockBlockFree.Resource.values()) {
          if (pt.specialType.equalsIgnoreCase("free" + resource.name)) {
            IBlockState bs = MillBlocks.FREE_BLOCK.getStateFromMeta(resource.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
        for (MockBlockTreeSpawn.TreeType treeType : MockBlockTreeSpawn.TreeType.values()) {
          if (pt.specialType.equalsIgnoreCase(treeType.name + "spawn")) {
            IBlockState bs = MillBlocks.TREE_SPAWN.getStateFromMeta(treeType.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
        for (MockBlockSoil.CropType cropType : MockBlockSoil.CropType.values()) {
          if (pt.specialType.equalsIgnoreCase(cropType.name)) {
            IBlockState bs = MillBlocks.SOIL_BLOCK.getStateFromMeta(cropType.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
        for (MockBlockDecor.Type decorType : MockBlockDecor.Type.values()) {
          if (pt.specialType.equalsIgnoreCase(decorType.name)) {
            IBlockState bs = MillBlocks.DECOR_BLOCK.getStateFromMeta(decorType.meta);
            reverseColourPoints.put(Integer.valueOf(getPointHash(bs)), pt);
          } 
        } 
      } 
    } 
  }
  
  private static IBlockState mirrorBlock(PointType pt, boolean horizontal) {
    Comparable rawFacingValue = BlockStateUtilities.getPropertyValueByName(pt.getBlockState(), "facing");
    if (rawFacingValue != null && rawFacingValue instanceof EnumFacing) {
      EnumFacing facing = (EnumFacing)rawFacingValue;
      if (horizontal) {
        if (facing == EnumFacing.EAST) {
          facing = EnumFacing.WEST;
        } else if (facing == EnumFacing.WEST) {
          facing = EnumFacing.EAST;
        } 
      } else if (facing == EnumFacing.NORTH) {
        facing = EnumFacing.SOUTH;
      } else if (facing == EnumFacing.SOUTH) {
        facing = EnumFacing.NORTH;
      } 
      IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(pt.getBlockState(), "facing", (Comparable)facing);
      return adjustedBlockState;
    } 
    return null;
  }
  
  private static void mirrorPlan(BuildingPlanSet existingSet, boolean horizontalmirror) {
    for (BuildingPlan[] plans : existingSet.plans) {
      for (BuildingPlan plan : plans) {
        int planLength = (plan.plan[0]).length;
        int planWidth = (plan.plan[0][0]).length;
        for (int floorPos = 0; floorPos < plan.plan.length; floorPos++) {
          for (int lengthPos = 0; lengthPos < (plan.plan[0]).length; lengthPos++) {
            for (int widthPos = 0; widthPos < (plan.plan[0][0]).length; widthPos++) {
              int newLengthPos;
              int newWidthPos;
              if (horizontalmirror) {
                newLengthPos = planLength - lengthPos - 1;
                newWidthPos = widthPos;
              } else {
                newLengthPos = lengthPos;
                newWidthPos = planWidth - widthPos - 1;
              } 
              if (!plan.plan[floorPos][lengthPos][widthPos].isType("empty"))
                if ((plan.plan[floorPos][lengthPos][widthPos]).specialType != null) {
                  plan.plan[floorPos][newLengthPos][newWidthPos] = plan.plan[floorPos][lengthPos][widthPos];
                } else {
                  IBlockState blockState = mirrorBlock(plan.plan[floorPos][lengthPos][widthPos], horizontalmirror);
                  if (blockState != null) {
                    PointType newPt = getPointTypeFromBlockState(blockState);
                    if (newPt != null)
                      plan.plan[floorPos][lengthPos][widthPos] = newPt; 
                  } else {
                    plan.plan[floorPos][newLengthPos][newWidthPos] = plan.plan[floorPos][lengthPos][widthPos];
                  } 
                }  
            } 
          } 
        } 
      } 
    } 
  }
  
  public static void negationWandExportBuilding(EntityPlayer player, World world, Point startPoint) {
    try {
      TileEntitySign sign = startPoint.getSign(world);
      if (sign == null)
        return; 
      if (sign.signText[0] == null || sign.signText[0].getUnformattedText().length() == 0) {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errornoname", new String[0]);
        return;
      } 
      String planName = sign.signText[0].getUnformattedText().toLowerCase();
      int variation = 0;
      for (int letter = 0; letter < 26; letter++) {
        if (planName.endsWith("_" + (char)(97 + letter))) {
          planName = planName.substring(0, planName.length() - 2);
          variation = letter;
        } 
      } 
      int upgradeLevel = -1;
      if (sign.signText[1] != null && sign.signText[1].getUnformattedText().length() > 0)
        try {
          upgradeLevel = Integer.parseInt(sign.signText[1].getUnformattedText());
        } catch (Exception e) {
          ServerSender.sendTranslatedSentence(player, '6', "export.errorinvalidupgradelevel", new String[0]);
          return;
        }  
      int xEnd = startPoint.getiX() + 1;
      boolean found = false;
      while (!found && xEnd < startPoint.getiX() + 257) {
        Block block = WorldUtilities.getBlock(world, xEnd, startPoint.getiY(), startPoint.getiZ());
        if (block == Blocks.STANDING_SIGN) {
          found = true;
          break;
        } 
        xEnd++;
      } 
      if (!found) {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errornoendsigneast", new String[0]);
        return;
      } 
      int zEnd = startPoint.getiZ() + 1;
      found = false;
      while (!found && zEnd < startPoint.getiZ() + 257) {
        Block block = WorldUtilities.getBlock(world, startPoint.getiX(), startPoint.getiY(), zEnd);
        if (block == Blocks.STANDING_SIGN) {
          found = true;
          break;
        } 
        zEnd++;
      } 
      if (!found) {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errornoendsignsouth", new String[0]);
        return;
      } 
      int startLevel = -1;
      if (sign.signText[2] != null && sign.signText[2].getUnformattedText().length() > 0) {
        try {
          startLevel = Integer.parseInt(sign.signText[2].getUnformattedText());
        } catch (Exception e) {
          Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.errorstartinglevel", new String[0]);
        } 
      } else {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), 'f', "export.defaultstartinglevel", new String[0]);
      } 
      boolean exportSnow = false;
      if (sign.signText[3] != null && sign.signText[3].getUnformattedText().equals("snow"))
        exportSnow = true; 
      int length = xEnd - startPoint.getiX() - 1;
      int width = zEnd - startPoint.getiZ() - 1;
      int orientation = 0;
      if (sign.signText[3] != null && sign.signText[3].getUnformattedText().startsWith("or:")) {
        String orientationString = sign.signText[3].getUnformattedText().substring(3, sign.signText[3].getUnformattedText().length());
        orientation = Integer.parseInt(orientationString);
      } 
      if (orientation != 0) {
        Mill.proxy.localTranslatedSentence(Mill.proxy.getTheSinglePlayer(), '6', "export.northfacingonly", new String[0]);
        return;
      } 
      exportBuilding(world, startPoint, planName, variation, length, width, orientation, upgradeLevel, startLevel, exportSnow, false, true);
    } catch (Exception e) {
      MillLog.printException("Error when trying to store a building: ", e);
    } 
  }
  
  private static void replaceWoodType(BuildingPlanSet existingSet, BlockPlanks.EnumType newWoodType) {
    Block newLogBlock;
    BlockLeaves blockLeaves;
    PropertyEnum propertyEnum1, propertyEnum2;
    if (newWoodType.equals(BlockPlanks.EnumType.ACACIA) || newWoodType.equals(BlockPlanks.EnumType.DARK_OAK)) {
      newLogBlock = Blocks.LOG2;
      blockLeaves = Blocks.LEAVES2;
      propertyEnum1 = BlockNewLog.VARIANT;
      propertyEnum2 = BlockNewLeaf.VARIANT;
    } else {
      newLogBlock = Blocks.LOG;
      blockLeaves = Blocks.LEAVES;
      propertyEnum1 = BlockOldLog.VARIANT;
      propertyEnum2 = BlockOldLeaf.VARIANT;
    } 
    Block[][] blocksToReplace = { { Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.ACACIA_STAIRS, Blocks.DARK_OAK_STAIRS }, { (Block)Blocks.OAK_DOOR, (Block)Blocks.SPRUCE_DOOR, (Block)Blocks.BIRCH_DOOR, (Block)Blocks.JUNGLE_DOOR, (Block)Blocks.ACACIA_DOOR, (Block)Blocks.DARK_OAK_DOOR }, { Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE }, { Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE } };
    for (BuildingPlan[] plans : existingSet.plans) {
      for (BuildingPlan plan : plans) {
        for (int floorPos = 0; floorPos < plan.plan.length; floorPos++) {
          for (int lengthPos = 0; lengthPos < (plan.plan[0]).length; lengthPos++) {
            for (int widthPos = 0; widthPos < (plan.plan[0][0]).length; widthPos++) {
              PointType pt = plan.plan[floorPos][lengthPos][widthPos];
              if (pt.getBlock() == Blocks.LOG || pt.getBlock() == Blocks.LOG2) {
                IBlockState newBlockState = newLogBlock.getStateFromMeta(pt.getMeta()).withProperty((IProperty)propertyEnum1, (Comparable)newWoodType);
                PointType newPt = getPointTypeFromBlockState(newBlockState);
                if (newPt != null)
                  plan.plan[floorPos][lengthPos][widthPos] = newPt; 
              } else if (pt.getBlock() == Blocks.LEAVES || pt.getBlock() == Blocks.LEAVES2) {
                IBlockState newBlockState = blockLeaves.getStateFromMeta(pt.getMeta()).withProperty((IProperty)propertyEnum2, (Comparable)newWoodType);
                PointType newPt = getPointTypeFromBlockState(newBlockState);
                if (newPt != null)
                  plan.plan[floorPos][lengthPos][widthPos] = newPt; 
              } else if (pt.getBlockState() != null) {
                Comparable rawWoodTypeValue = BlockStateUtilities.getPropertyValueByName(pt.getBlockState(), "variant");
                if (rawWoodTypeValue != null && rawWoodTypeValue instanceof BlockPlanks.EnumType) {
                  IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(pt.getBlockState(), "variant", (Comparable)newWoodType);
                  PointType newPt = getPointTypeFromBlockState(adjustedBlockState);
                  if (newPt != null)
                    plan.plan[floorPos][lengthPos][widthPos] = newPt; 
                } 
                for (Block[] blockList : blocksToReplace) {
                  for (Block block : blockList) {
                    if (pt.getBlock() == block) {
                      Block newBlock = blockList[newWoodType.getMetadata()];
                      IBlockState newBlockState = newBlock.getStateFromMeta(pt.getMeta());
                      PointType newPt = getPointTypeFromBlockState(newBlockState);
                      if (newPt != null)
                        plan.plan[floorPos][lengthPos][widthPos] = newPt; 
                    } 
                  } 
                } 
              } 
            } 
          } 
        } 
      } 
    } 
  }
  
  public static void summoningWandImportBuildingPlan(EntityPlayer player, World world, Point startPoint, int variation, BuildingPlanSet existingSet, int upgradeLevel, boolean includeSpecialPoints, int orientation, boolean createSign) {
    BuildingPlan basePlan = ((BuildingPlan[])existingSet.plans.get(variation))[0];
    BuildingPlan upgradePlan = ((BuildingPlan[])existingSet.plans.get(variation))[upgradeLevel];
    if (createSign) {
      Point startingSign = startPoint;
      startingSign.setBlock(world, Blocks.STANDING_SIGN, 0, true, false);
      TileEntitySign tileEntitySign = startingSign.getSign(world);
      char variationLetter = (char)(65 + variation);
      tileEntitySign.signText[0] = (ITextComponent)new TextComponentString(existingSet.key + "_" + variationLetter);
      tileEntitySign.signText[1] = (ITextComponent)new TextComponentString("" + upgradeLevel);
      tileEntitySign.signText[2] = (ITextComponent)new TextComponentString("" + upgradePlan.startLevel);
      if (!includeSpecialPoints) {
        tileEntitySign.signText[3] = (ITextComponent)new TextComponentString("nomock");
      } else if (orientation > 0) {
        tileEntitySign.signText[3] = (ITextComponent)new TextComponentString("or:" + orientation);
      } else {
        tileEntitySign.signText[3] = (ITextComponent)new TextComponentString("");
      } 
    } 
    int orientatedLength = basePlan.length;
    int orientatedWidth = basePlan.width;
    if (orientation % 2 == 1) {
      orientatedLength = basePlan.width;
      orientatedWidth = basePlan.length;
    } 
    BuildingLocation location = new BuildingLocation(basePlan, startPoint.getRelative((orientatedLength / 2 + 1), 0.0D, (orientatedWidth / 2 + 1)), orientation);
    for (int i = 0; i <= upgradeLevel; i++) {
      if (player != null)
        ServerSender.sendTranslatedSentence(player, 'f', "import.buildinglevel", new String[] { "" + i }); 
      existingSet.buildLocation(Mill.getMillWorld(world), null, location, !includeSpecialPoints, false, null, true, includeSpecialPoints, null);
      location.level++;
    } 
    Point eastSign = startPoint.getRelative((orientatedLength + 1), 0.0D, 0.0D);
    eastSign.setBlock(world, Blocks.STANDING_SIGN, 0, true, false);
    TileEntitySign sign = eastSign.getSign(world);
    sign.signText[0] = (ITextComponent)new TextComponentString("East End");
    sign.signText[1] = (ITextComponent)new TextComponentString("(length)");
    sign.signText[2] = (ITextComponent)new TextComponentString("");
    sign.signText[3] = (ITextComponent)new TextComponentString("");
    Point southSign = startPoint.getRelative(0.0D, 0.0D, (orientatedWidth + 1));
    southSign.setBlock(world, Blocks.STANDING_SIGN, 0, true, false);
    sign = southSign.getSign(world);
    sign.signText[0] = (ITextComponent)new TextComponentString("South End");
    sign.signText[1] = (ITextComponent)new TextComponentString("(width)");
    sign.signText[2] = (ITextComponent)new TextComponentString("");
    sign.signText[3] = (ITextComponent)new TextComponentString("");
  }
  
  public static void summoningWandImportBuildingRequest(EntityPlayer player, World world, Point startPoint) {
    try {
      TileEntitySign sign = startPoint.getSign(world);
      if (sign == null)
        return; 
      if (sign.signText[0] == null || sign.signText[0].getUnformattedText().length() == 0) {
        ServerSender.sendTranslatedSentence(player, '6', "import.errornoname", new String[0]);
        return;
      } 
      String buildingKey = sign.signText[0].getUnformattedText().toLowerCase();
      int variation = 0;
      boolean explicitVariation = false;
      for (int letter = 0; letter < 26; letter++) {
        if (buildingKey.endsWith("_" + (char)(97 + letter))) {
          buildingKey = buildingKey.substring(0, buildingKey.length() - 2);
          variation = letter;
          explicitVariation = true;
        } 
      } 
      char variationLetter = 'A';
      variationLetter = (char)(variationLetter + variation);
      File exportDir = MillCommonUtilities.getExportDir();
      File buildingFile = new File(exportDir, buildingKey + "_" + variationLetter + ".txt");
      if (!buildingFile.exists()) {
        File foundFile = null;
        BuildingPlanSet foundPlanSet = null;
        for (Culture culture : Culture.ListCultures) {
          for (BuildingPlanSet planSet : culture.ListPlanSets) {
            if (foundFile == null)
              if (planSet.key.equals(buildingKey) && planSet.plans.size() > variation) {
                foundFile = ((BuildingPlan[])planSet.plans.get(variation))[0].getLoadedFromFile();
                foundPlanSet = planSet;
              }  
          } 
        } 
        if (foundFile == null)
          for (Culture culture : Culture.ListCultures) {
            for (BuildingPlanSet planSet : culture.ListPlanSets) {
              if (foundFile == null && 
                planSet.plans.size() > variation && 
                (((BuildingPlan[])planSet.plans.get(variation))[0]).nativeName != null && (((BuildingPlan[])planSet.plans.get(variation))[0]).nativeName.toLowerCase().equals(buildingKey)) {
                foundFile = ((BuildingPlan[])planSet.plans.get(variation))[0].getLoadedFromFile();
                foundPlanSet = planSet;
                buildingKey = planSet.key;
                ITextComponent[] oldSignData = sign.signText;
                startPoint.setBlock(world, Blocks.STANDING_SIGN, 0, true, false);
                TileEntitySign newSign = startPoint.getSign(world);
                newSign.signText[0] = (ITextComponent)new TextComponentString(buildingKey);
                newSign.signText[1] = oldSignData[1];
                newSign.signText[2] = oldSignData[2];
                newSign.signText[3] = oldSignData[3];
              } 
            } 
          }  
        if (foundFile != null) {
          ServerSender.sendTranslatedSentence(player, '6', "import.copyingfrom", new String[] { foundFile.getAbsolutePath().replace("\\", "/") });
          Path exportPath = exportDir.toPath();
          Path inputPath = foundFile.toPath().getParent();
          for (int exportVariation = 0; exportVariation < foundPlanSet.plans.size(); exportVariation++) {
            char exportVariationLetter = (char)(65 + exportVariation);
            String txtFileName = foundPlanSet.key + "_" + exportVariationLetter + ".txt";
            Files.copy(inputPath.resolve(txtFileName), exportPath.resolve(txtFileName), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
            for (int buildingUpgrade = 0; buildingUpgrade < ((BuildingPlan[])foundPlanSet.plans.get(exportVariation)).length; buildingUpgrade++) {
              String pngFileName = foundPlanSet.key + "_" + exportVariationLetter + buildingUpgrade + ".png";
              Files.copy(inputPath.resolve(pngFileName), exportPath.resolve(pngFileName), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
            } 
          } 
        } else {
          ServerSender.sendTranslatedSentence(player, '6', "import.errornotfound", new String[0]);
          return;
        } 
      } 
      BuildingPlanSet existingSet = loadPlanSetFromExportDir(buildingKey);
      boolean importAll = (sign.signText[1] != null && sign.signText[1].getUnformattedText().equalsIgnoreCase("all"));
      boolean includeSpecialPoints = (sign.signText[3] == null || !sign.signText[3].getUnformattedText().equalsIgnoreCase("nomock"));
      int orientation = 0;
      if (sign.signText[3] != null && sign.signText[3].getUnformattedText().startsWith("or:")) {
        String orientationString = sign.signText[3].getUnformattedText().substring(3, sign.signText[3].getUnformattedText().length());
        orientation = Integer.parseInt(orientationString);
      } 
      if (!importAll) {
        int upgradeLevel = 0;
        if (sign.signText[1] != null && sign.signText[1].getUnformattedText().length() > 0) {
          try {
            upgradeLevel = Integer.parseInt(sign.signText[1].getUnformattedText());
            ServerSender.sendTranslatedSentence(player, 'f', "import.buildingupto", new String[] { "" + upgradeLevel });
          } catch (Exception e) {
            ServerSender.sendTranslatedSentence(player, '6', "import.errorinvalidupgradelevel", new String[0]);
            return;
          } 
        } else {
          ServerSender.sendTranslatedSentence(player, 'f', "import.buildinginitialphase", new String[0]);
        } 
        if (upgradeLevel >= ((BuildingPlan[])existingSet.plans.get(variation)).length) {
          ServerSender.sendTranslatedSentence(player, '6', "import.errorupgradeleveltoohigh", new String[0]);
          return;
        } 
        if (sign.signText[2] != null) {
          String signLine = sign.signText[2].getUnformattedText();
          if (signLine.equals("x2")) {
            doubleHeightPlan(player, existingSet);
          } else if (signLine.equals("hmirror") || signLine.equals("vmirror")) {
            boolean horizontalmirror = signLine.equals("hmirror");
            mirrorPlan(existingSet, horizontalmirror);
          } else if (signLine.startsWith("wood:")) {
            String woodTypeName = signLine.substring("wood:".length(), signLine.length());
            BlockPlanks.EnumType woodType = null;
            for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
              if (type.getName().equals(woodTypeName))
                woodType = type; 
            } 
            if (woodType == null) {
              ServerSender.sendTranslatedSentence(player, '6', "import.errorunknownwoodtype", new String[] { woodTypeName });
            } else {
              replaceWoodType(existingSet, woodType);
            } 
          } 
        } 
        summoningWandImportBuildingPlan(player, world, startPoint, variation, existingSet, upgradeLevel, includeSpecialPoints, orientation, false);
      } else {
        Point adjustedStartPoint = startPoint.getRelative(1.0D, 0.0D, 0.0D);
        int variationStart = 0;
        int variationEnd = existingSet.plans.size();
        if (explicitVariation) {
          variationStart = variation;
          variationEnd = variation + 1;
        } 
        for (int variationPos = variationStart; variationPos < variationEnd; variationPos++) {
          for (int maxLevel = 0; maxLevel < ((BuildingPlan[])existingSet.plans.get(variationPos)).length; maxLevel++) {
            summoningWandImportBuildingPlan(player, world, adjustedStartPoint, variationPos, existingSet, maxLevel, includeSpecialPoints, orientation, true);
            adjustedStartPoint = adjustedStartPoint.getRelative(((((BuildingPlan[])existingSet.plans.get(variationPos))[0]).length + 10), 0.0D, 0.0D);
          } 
          adjustedStartPoint = new Point(startPoint.x, startPoint.y, adjustedStartPoint.z + (((BuildingPlan[])existingSet.plans.get(variationPos))[0]).width + 10.0D);
        } 
      } 
    } catch (Exception e) {
      MillLog.printException("Error when importing a building:", e);
    } 
  }
}
