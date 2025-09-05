package org.millenaire.common.buildingplan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.block.BlockPaintedBricks;
import org.millenaire.common.block.BlockSilkWorm;
import org.millenaire.common.block.BlockSnailSoil;
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
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.RegionMapper;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.IntPoint;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.VillageMapInfo;
import org.millenaire.common.world.MillWorldData;

public class BuildingPlan implements IBuildingPlan, MillCommonUtilities.WeightedChoice {
  private static final int LARGE_BUILDING_FLOOR_SIZE = 2500;
  
  public static final int NORTH_FACING = 0;
  
  public static final int WEST_FACING = 1;
  
  public static final int SOUTH_FACING = 2;
  
  public static final int EAST_FACING = 3;
  
  public static class LocationBuildingPair {
    public Building building;
    
    public BuildingLocation location;
    
    public LocationBuildingPair(Building b, BuildingLocation l) {
      this.building = b;
      this.location = l;
    }
  }
  
  public static class LocationReturn {
    public static final int OUTSIDE_RADIUS = 1;
    
    public static final int LOCATION_CLASH = 2;
    
    public static final int CONSTRUCTION_FORBIDEN = 3;
    
    public static final int WRONG_ALTITUDE = 4;
    
    public static final int DANGER = 5;
    
    public static final int NOT_REACHABLE = 6;
    
    public static final int TOO_CLOSE_TO_TAG = 7;
    
    public static final int TOO_FAR_FROM_TAG = 8;
    
    public BuildingLocation location;
    
    public int errorCode;
    
    public Point errorPos;
    
    public LocationReturn(BuildingLocation l) {
      this.location = l;
      this.errorCode = 0;
      this.errorPos = null;
    }
    
    public LocationReturn(int error, Point p) {
      this.location = null;
      this.errorCode = error;
      this.errorPos = p;
    }
  }
  
  public static class StartingGood {
    public InvItem item;
    
    public double probability;
    
    public int fixedNumber;
    
    public int randomNumber;
    
    public StartingGood(InvItem item, double probability, int fixedNumber, int randomNumber) {
      this.item = item;
      this.probability = probability;
      this.fixedNumber = fixedNumber;
      this.randomNumber = randomNumber;
    }
  }
  
  public static final String[] FACING_KEYS = new String[] { "north", "west", "south", "east" };
  
  private static final short PART_SIZE = 8;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "Length of the building. In the PNG file, this is the picture's height.")
  public int length;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "Width of the building. In the PNG file, this is the width of every floor in the picture.")
  public int width;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "5")
  @FieldDocumentation(explanation = "Area to clear around the building.")
  public int areaToClear;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "-1")
  @FieldDocumentation(explanation = "Area to clear around the building on the length axis before the building. If set to -1 (default), the areaToClear value will be used.")
  public int areaToClearLengthBefore;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "-1")
  @FieldDocumentation(explanation = "Area to clear around the building on the length axis after the building. If set to -1 (default), the areaToClear value will be used.")
  public int areaToClearLengthAfter;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "-1")
  @FieldDocumentation(explanation = "Area to clear around the building on the width axis before the building. If set to -1 (default), the areaToClear value will be used.")
  public int areaToClearWidthBefore;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "-1")
  @FieldDocumentation(explanation = "Area to clear around the building on the width axis after the building. If set to -1 (default), the areaToClear value will be used.")
  public int areaToClearWidthAfter;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "1")
  @FieldDocumentation(explanation = "Orientation of the building within the plan. 1 means the building 'faces' to the left of the PNG file.")
  public int buildingOrientation;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Amount by which this building should be raised or lowered compared to the \"ground level\".")
  public int altitudeOffset;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "10")
  @FieldDocumentation(explanation = "How deep the foundations of the building should be.")
  public int foundationDepth;
  
  public static Point adjustForOrientation(int x, int y, int z, int xoffset, int zoffset, int orientation) {
    Point pos = null;
    if (orientation == 0) {
      pos = new Point((x + xoffset), y, (z + zoffset));
    } else if (orientation == 1) {
      pos = new Point((x + zoffset), y, (z - xoffset - 1));
    } else if (orientation == 2) {
      pos = new Point((x - xoffset - 1), y, (z - zoffset - 1));
    } else if (orientation == 3) {
      pos = new Point((x - zoffset - 1), y, (z + xoffset));
    } 
    return pos;
  }
  
  public static int computeOrientation(Point buildingPos, Point facingPos) {
    int relx = buildingPos.getiX() - facingPos.getiX();
    int relz = buildingPos.getiZ() - facingPos.getiZ();
    if (relx * relx > relz * relz) {
      if (relx > 0)
        return 0; 
      return 2;
    } 
    if (relz > 0)
      return 3; 
    return 1;
  }
  
  static String getColourString(int colour) {
    return ((colour & 0xFF0000) >> 16) + "/" + ((colour & 0xFF00) >> 8) + "/" + ((colour & 0xFF) >> 0) + "/" + Integer.toHexString(colour);
  }
  
  public static boolean loadBuildingPoints() {
    for (File loadDir : Mill.loadingDirs) {
      File mainList = new File(loadDir, "blocklist.txt");
      if (mainList.exists() && 
        loadBuildingPointsFile(mainList))
        return true; 
    } 
    BuildingImportExport.loadReverseBuildingPoints(Boolean.valueOf(true), Boolean.valueOf(false));
    if (!Mill.proxy.isTrueServer())
      BuildingDevUtilities.generateColourSheet(); 
    return false;
  }
  
  private static boolean loadBuildingPointsFile(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//"))
          try {
            PointType cp = PointType.readColourPoint(line);
            for (PointType cp2 : PointType.colourPoints.values()) {
              if (cp2.colour == cp.colour)
                throw new MillLog.MillenaireException("Colour " + getColourString(cp.colour) + " in line <" + line + "> is already taken."); 
            } 
            PointType.colourPoints.put(Integer.valueOf(cp.colour), cp);
          } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
            MillLog.error(null, "Error when loading a block type: " + e.getMessage());
          } catch (Exception e) {
            MillLog.printException("Exception when parsing blocklist line: " + line, e);
          }  
      } 
    } catch (IOException e) {
      MillLog.printException(e);
      return true;
    } 
    return false;
  }
  
  public static HashMap<String, BuildingPlanSet> loadPlans(VirtualDir cultureVirtualDir, Culture culture) {
    HashMap<String, BuildingPlanSet> plans = new HashMap<>();
    VirtualDir buildingsVirtualDir = cultureVirtualDir.getChildDirectory("buildings");
    BuildingFileFiler pictPlans = new BuildingFileFiler("_A.txt");
    for (File file : buildingsVirtualDir.listFilesRecursive(pictPlans)) {
      try {
        if (MillConfigValues.LogBuildingPlan >= 1)
          MillLog.major(file, "Loading pict building: " + file.getAbsolutePath()); 
        BuildingPlanSet set = new BuildingPlanSet(culture, file.getName().substring(0, file.getName().length() - 6), buildingsVirtualDir, file);
        set.loadPictPlans(false);
        if (file.getParentFile().getName().startsWith("lone"))
          set.max = 0; 
        plans.put(set.key, set);
      } catch (Exception e) {
        MillLog.printException("Exception when loading " + file.getName() + " plan set in culture " + culture.key + ":", e);
      } 
    } 
    validatePlans(plans);
    return plans;
  }
  
  private static void validatePlans(HashMap<String, BuildingPlanSet> plans) {
    for (BuildingPlanSet planSet : plans.values()) {
      if ((planSet.getFirstStartingPlan()).isSubBuilding)
        if ((planSet.getFirstStartingPlan()).parentBuildingPlan == null) {
          MillLog.warning(planSet, "This plan is a sub-building but has no referenced parent plan. This will make it impossible to import and export it correctly using an Import Table.");
        } else {
          String parentBuildingPlanKey = (planSet.getFirstStartingPlan()).parentBuildingPlan;
          String parentSuffix = parentBuildingPlanKey.split("_")[(parentBuildingPlanKey.split("_")).length - 1].toUpperCase();
          int parentVariation = parentSuffix.charAt(0) - 65;
          int parentLevel = Integer.parseInt(parentSuffix.substring(1, parentSuffix.length()));
          String parentBuildingKey = parentBuildingPlanKey.substring(0, parentBuildingPlanKey.length() - parentSuffix.length() - 1);
          if (!plans.containsKey(parentBuildingKey)) {
            MillLog.warning(planSet, "Unknown parent building plan: " + parentBuildingPlanKey);
          } else if (((BuildingPlanSet)plans.get(parentBuildingKey)).getPlan(parentVariation, parentLevel) == null) {
            MillLog.warning(planSet, "Unknown level or upgrade for parent building plan: " + parentBuildingPlanKey);
          } else if ((((BuildingPlanSet)plans.get(parentBuildingKey)).getPlan(parentVariation, parentLevel)).length != (planSet.getFirstStartingPlan()).length) {
            MillLog.warning(planSet, "Length of parent building plan does not match that of sub building plan.");
          } else if ((((BuildingPlanSet)plans.get(parentBuildingKey)).getPlan(parentVariation, parentLevel)).width != (planSet.getFirstStartingPlan()).width) {
            MillLog.warning(planSet, "Width of parent building plan does not match that of sub building plan.");
          } 
        }  
      for (int variation = 0; variation < planSet.plans.size(); variation++) {
        Set<String> subBuildingsToDelete = new HashSet<>();
        int level;
        for (level = 0; level < ((BuildingPlan[])planSet.plans.get(variation)).length; level++) {
          for (String planKey : (planSet.getPlan(variation, level)).subBuildings) {
            if (!plans.containsKey(planKey))
              subBuildingsToDelete.add(planKey); 
          } 
          for (String planKey : (planSet.getPlan(variation, level)).startingSubBuildings) {
            if (!plans.containsKey(planKey))
              subBuildingsToDelete.add(planKey); 
          } 
        } 
        for (String planKey : subBuildingsToDelete)
          MillLog.error(planSet, "Unknown sub-building: " + planKey); 
        if (!subBuildingsToDelete.isEmpty())
          for (level = 0; level < ((BuildingPlan[])planSet.plans.get(variation)).length; level++) {
            (planSet.getPlan(variation, level)).subBuildings.removeAll(subBuildingsToDelete);
            (planSet.getPlan(variation, level)).startingSubBuildings.removeAll(subBuildingsToDelete);
          }  
      } 
    } 
  }
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.DIRECTION)
  @FieldDocumentation(explanation = "A fixed direction the building will be oriented to face (instead of facing the town hall like normal).")
  public int fixedOrientation = -1;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Name of a good whose icon represents this building.")
  private final InvItem icon = null;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.STRING, paramName = "travelbook_category")
  @FieldDocumentation(explanation = "Category in the Travel Book to appear in.")
  public String travelBookCategory = null;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "travelbook_display", defaultValue = "true")
  @FieldDocumentation(explanation = "Whether to display this building in the Travel Book.")
  public boolean travelBookDisplay = true;
  
  @ConfigField(fieldCategory = "init", paramName = "startingsubbuilding", type = AnnotedParameter.ParameterType.STRING_CASE_SENSITIVE_ADD)
  @FieldDocumentation(explanation = "Sub-buildings generated when this building is spawned.")
  public List<String> startingSubBuildings = new ArrayList<>();
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Whether this is a sub-building.")
  public boolean isSubBuilding;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Optional parent plan (used when importing the sub-building).")
  public String parentBuildingPlan = null;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "'Version' of the building. Changing this will stop buildings started with a previous version from getting upgraded to the new one.")
  public int version;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "1")
  @FieldDocumentation(explanation = "Maximum amount of this building in a village (can be overriden in a village's config).")
  public int max;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.FLOAT, defaultValue = "0.0")
  @FieldDocumentation(explanation = "Minimum distance from the village centre, on a scale of 0 to 1.")
  public float minDistance;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.FLOAT, defaultValue = "1.0")
  @FieldDocumentation(explanation = "Maximum distance from the village centre, on a scale of 0 to 1.")
  public float maxDistance;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.STRING_INTEGER_ADD)
  @FieldDocumentation(explanation = "Must be build at least this distance from any building with the provided tag.")
  public Map<String, Integer> farFromTag = new HashMap<>();
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.STRING_INTEGER_ADD)
  @FieldDocumentation(explanation = "Must be build at least this close to a building with the provided tag.")
  public Map<String, Integer> closeToTag = new HashMap<>();
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "If non-0, the reputation the player must have to request that this building be built.")
  public int reputation;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "If non-0, the price the player must pay to request that this building be built.")
  public int price;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Whether the building is a gift house, available to donors.")
  public boolean isgift;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "true")
  @FieldDocumentation(explanation = "Show townhall-style signs (if this is a town hall). If false, show house signs")
  public boolean showTownHallSigns;
  
  @ConfigField(fieldCategory = "init", paramName = "startinggood", type = AnnotedParameter.ParameterType.STARTING_ITEM_ADD)
  @FieldDocumentation(explanation = "Items to add to the building's chest when generated (used for loot in abandonned lone buildings).")
  public List<StartingGood> startingGoods = new ArrayList<>();
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "This building is part of the city walls. It will be build by dedicated wall builders.")
  public boolean isWallSegment;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "This building is a 'border' building, like city walls, but will be built by regular builders.")
  public boolean isBorderBuilding;
  
  @ConfigField(fieldCategory = "init", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "1")
  @FieldDocumentation(explanation = "Generation weight. The higher it is, the more chance that this plan will be picked. Only used when there are more than one variation of a building.")
  public int weight;
  
  @ConfigField(fieldCategory = "init", paramName = "randombrickcolour", type = AnnotedParameter.ParameterType.RANDOM_BRICK_COLOUR_ADD)
  @FieldDocumentation(explanation = "Colours that painted bricks can randomly turn to.")
  public Map<EnumDyeColor, Map<EnumDyeColor, Integer>> randomBrickColours = new HashMap<>();
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Level from the ground where the building plan starts. 0 is just above ground level.")
  public int startLevel;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Name of the building in the village's language.")
  public String nativeName = null;
  
  @ConfigField(fieldCategory = "upgrade", paramName = "translatedname", type = AnnotedParameter.ParameterType.TRANSLATED_STRING_ADD)
  @FieldDocumentation(explanation = "Name of the building in a specified language. Used for library buildings.")
  public final HashMap<String, String> translatedNames = new HashMap<>();
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "1")
  @FieldDocumentation(explanation = "Priority given to this building or upgrade for construction. Adjusted based on whether the building is core, secondary or extra.")
  public int priority;
  
  @ConfigField(fieldCategory = "upgrade", paramName = "male", type = AnnotedParameter.ParameterType.VILLAGER_ADD)
  @FieldDocumentation(explanation = "A male villager that will populate this building.")
  public List<String> maleResident = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "female", type = AnnotedParameter.ParameterType.VILLAGER_ADD)
  @FieldDocumentation(explanation = "A female villager that will populate this building.")
  public List<String> femaleResident = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "visitor", type = AnnotedParameter.ParameterType.VILLAGER_ADD)
  @FieldDocumentation(explanation = "A villager type that can temporarily visit this building.")
  public List<String> visitors = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "10")
  @FieldDocumentation(explanation = "Priority given to this building by teenagers moving in.")
  public int priorityMoveIn;
  
  @ConfigField(fieldCategory = "upgrade", paramName = "subbuilding", type = AnnotedParameter.ParameterType.STRING_CASE_SENSITIVE_ADD)
  @FieldDocumentation(explanation = "Sub-building that can be constructed once the building reaches this level.")
  public List<String> subBuildings = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Level of the path connected to this building.")
  public int pathLevel;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "2")
  @FieldDocumentation(explanation = "Width of the path to this building.")
  public int pathWidth;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Rebuild the path to this building when the upgrade is complete. Use if the layout has changed.")
  public boolean rebuildPath;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "This building should not have paths leading to it.")
  public boolean noPathsToBuilding;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "A tag to give the player when he is near this building. Used in some quests.")
  public String exploreTag = null;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "A global tag that must be set for this building or upgrade to be a valid construction.")
  public String requiredGlobalTag = null;
  
  @ConfigField(fieldCategory = "upgrade", paramName = "tag", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "Tag to apply to the building. Either activates special behaviour (see provided list), or for use in quests, goal references etc.")
  public List<String> tags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "clearTag", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "Tag to remove from the building.")
  public List<String> clearTags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "forbiddenTagInVillage", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "A tag that will stop this upgrade from being built if present on any village building. Used for instance to stop wall sections from getting upgraded to level 3 before the whole wall is level2.")
  public List<String> forbiddenTagsInVillage = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "requiredTag", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "A tag that must be set on this building for this building or upgrade to be a valid construction.")
  public List<String> requiredTags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "villageTag", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "A tag that get applied to the village/Town Hall on construction or on upgrade of this plan.")
  public List<String> villageTags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "requiredVillageTag", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "A tag that must be set on this village's town hall for this building or upgrade to be a valid construction.")
  public List<String> requiredVillageTags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "parentTag", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "A tag that get applied to the building's parent building on construction or on upgrade of this plan.")
  public List<String> parentTags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", paramName = "requiredParentTags", type = AnnotedParameter.ParameterType.STRING_ADD)
  @FieldDocumentation(explanation = "A tag that must be set on this building's parent building for this building or upgrade to be a valid construction.")
  public List<String> requiredParentTags = new ArrayList<>();
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Amount by which the building increases the village's irrigation level, boosting harvests.")
  public int irrigation;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Number of extra construction slots to activate in the village for regular buildings (not walls).")
  public int extraSimultaneousConstructions;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Number of extra construction slots to activate in the village for wall buildings.")
  public int extraSimultaneousWallConstructions;
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.SHOP)
  @FieldDocumentation(explanation = "The shop file this building uses to trade with the player.")
  public String shop = null;
  
  @ConfigField(fieldCategory = "upgrade", paramName = "signs", type = AnnotedParameter.ParameterType.INTEGER_ARRAY)
  @FieldDocumentation(explanation = "Order of the signs in the plan.")
  public int[] signOrder = new int[] { 0 };
  
  @ConfigField(fieldCategory = "upgrade", type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "An abstracted daily production of a good. Used to calculate marvel donations.")
  public Map<InvItem, Integer> abstractedProduction = new HashMap<>();
  
  public int nbfloors;
  
  public int lengthOffset;
  
  public int widthOffset;
  
  public boolean isUpdate = false;
  
  public int level;
  
  public PointType[][][] plan = (PointType[][][])null;
  
  public String planName = "";
  
  public String buildingKey;
  
  public HashMap<InvItem, Integer> resCost;
  
  public int variation;
  
  public Culture culture;
  
  public BuildingPlan parent;
  
  private File loadedFromFile = null;
  
  public BuildingPlan() {}
  
  public BuildingPlan(String buildingKey, int level, int variation, Culture c) {
    this.buildingKey = buildingKey;
    this.isUpdate = (level > 0);
    this.level = level;
    this.variation = variation;
    this.culture = c;
  }
  
  public void addToAnyWoodCost(int nb) {
    addToCost(Blocks.LOG, -1, nb);
  }
  
  public void addToCost(Block block, int nb) {
    addToCost(block, 0, nb);
  }
  
  public void addToCost(Block block, int meta, int nb) {
    try {
      InvItem key = InvItem.createInvItem(block, meta);
      if (this.resCost.containsKey(key)) {
        nb += ((Integer)this.resCost.get(key)).intValue();
        this.resCost.put(key, Integer.valueOf(nb));
      } else {
        this.resCost.put(key, Integer.valueOf(nb));
      } 
    } catch (Exception e) {
      MillLog.printException("Exception when calculating cost of: " + this, e);
    } 
  }
  
  public void addToCost(IBlockState blockState, int nb) {
    addToCost(blockState.getBlock(), blockState.getBlock().getMetaFromState(blockState), nb);
  }
  
  public void addToCost(InvItem invitem, int nb) {
    try {
      if (this.resCost.containsKey(invitem)) {
        nb += ((Integer)this.resCost.get(invitem)).intValue();
        this.resCost.put(invitem, Integer.valueOf(nb));
      } else {
        this.resCost.put(invitem, Integer.valueOf(nb));
      } 
    } catch (Exception e) {
      MillLog.printException("Exception when calculating cost of: " + this, e);
    } 
  }
  
  public void addToCost(Item item, int nb) {
    try {
      InvItem key = InvItem.createInvItem(item, 0);
      if (this.resCost.containsKey(key)) {
        nb += ((Integer)this.resCost.get(key)).intValue();
        this.resCost.put(key, Integer.valueOf(nb));
      } else {
        this.resCost.put(key, Integer.valueOf(nb));
      } 
    } catch (Exception e) {
      MillLog.printException("Exception when calculating cost of: " + this, e);
    } 
  }
  
  private BlockLog.EnumAxis adjustAxisForOrientation(BlockLog.EnumAxis axis, int orientation) {
    if (axis == BlockLog.EnumAxis.Y || axis == BlockLog.EnumAxis.NONE)
      return axis; 
    if (orientation % 2 == 1)
      if (axis == BlockLog.EnumAxis.X) {
        axis = BlockLog.EnumAxis.Z;
      } else if (axis == BlockLog.EnumAxis.Z) {
        axis = BlockLog.EnumAxis.X;
      }  
    return axis;
  }
  
  private EnumFacing adjustFacingForOrientation(EnumFacing facing, int orientation) {
    if (facing == EnumFacing.DOWN || facing == EnumFacing.UP)
      return facing; 
    for (int i = 0; i < orientation; i++) {
      if (facing == EnumFacing.NORTH) {
        facing = EnumFacing.WEST;
      } else if (facing == EnumFacing.WEST) {
        facing = EnumFacing.SOUTH;
      } else if (facing == EnumFacing.SOUTH) {
        facing = EnumFacing.EAST;
      } else if (facing == EnumFacing.EAST) {
        facing = EnumFacing.NORTH;
      } 
    } 
    return facing;
  }
  
  private IBlockState attemptAutoRotation(IBlockState bs, int orientation) {
    Comparable rawFacingValue = BlockStateUtilities.getPropertyValueByName(bs, "facing");
    if (rawFacingValue != null && rawFacingValue instanceof EnumFacing) {
      EnumFacing facing = (EnumFacing)rawFacingValue;
      facing = adjustFacingForOrientation(facing, orientation);
      IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(bs, "facing", (Comparable)facing);
      return adjustedBlockState;
    } 
    if (orientation % 2 == 1) {
      Comparable rawAxisValue = BlockStateUtilities.getPropertyValueByName(bs, "axis");
      if (rawAxisValue != null && rawAxisValue instanceof BlockLog.EnumAxis) {
        BlockLog.EnumAxis axis = (BlockLog.EnumAxis)rawAxisValue;
        axis = adjustAxisForOrientation(axis, orientation);
        IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(bs, "axis", (Comparable)axis);
        return adjustedBlockState;
      } 
    } 
    if (orientation % 2 == 1) {
      Comparable variantValue = BlockStateUtilities.getPropertyValueByName(bs, "variant");
      if (variantValue != null && variantValue instanceof BlockQuartz.EnumType) {
        BlockQuartz.EnumType quartzType = (BlockQuartz.EnumType)variantValue;
        if (quartzType == BlockQuartz.EnumType.LINES_X) {
          IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(bs, "variant", (Comparable)BlockQuartz.EnumType.LINES_Z);
          return adjustedBlockState;
        } 
        if (quartzType == BlockQuartz.EnumType.LINES_Z) {
          IBlockState adjustedBlockState = BlockStateUtilities.setPropertyValueByName(bs, "variant", (Comparable)BlockQuartz.EnumType.LINES_X);
          return adjustedBlockState;
        } 
      } 
    } 
    return null;
  }
  
  private IBlockState attemptAutoRotation(PointType pt, int orientation) {
    if (orientation == 0)
      return pt.getBlockState(); 
    return attemptAutoRotation(pt.getBlockState(), orientation);
  }
  
  public void autoGuessLaddersDoorsStairs(List<BuildingBlock> bblocks) {
    List<BuildingBlock> stairs = new ArrayList<>();
    List<BuildingBlock> ladders = new ArrayList<>();
    List<BuildingBlock> doors = new ArrayList<>();
    HashMap<Point, BuildingBlock> map = new HashMap<>();
    for (BuildingBlock block : bblocks) {
      map.put(block.p, block);
      if (block.block == Blocks.LADDER && block.getMeta() == -1) {
        ladders.add(block);
        continue;
      } 
      if (block.block == Blocks.OAK_DOOR) {
        doors.add(block);
        continue;
      } 
      if ((block.block == Blocks.STONE_STAIRS || block.block == Blocks.OAK_STAIRS) && block.getMeta() == -1) {
        block.setMeta((byte)-1);
        stairs.add(block);
      } 
    } 
    boolean[] northValid = new boolean[ladders.size()];
    boolean[] southValid = new boolean[ladders.size()];
    boolean[] westValid = new boolean[ladders.size()];
    boolean[] eastValid = new boolean[ladders.size()];
    int i = 0;
    for (BuildingBlock ladder : ladders) {
      northValid[i] = mapIsOpaqueBlock(map, ladder.p.getNorth());
      southValid[i] = mapIsOpaqueBlock(map, ladder.p.getSouth());
      westValid[i] = mapIsOpaqueBlock(map, ladder.p.getWest());
      eastValid[i] = mapIsOpaqueBlock(map, ladder.p.getEast());
      if (northValid[i] && !southValid[i] && !westValid[i] && !eastValid[i])
        ladder.setMeta((byte)EnumFacing.SOUTH.getIndex()); 
      if (!northValid[i] && southValid[i] && !westValid[i] && !eastValid[i])
        ladder.setMeta((byte)EnumFacing.NORTH.getIndex()); 
      if (!northValid[i] && !southValid[i] && westValid[i] && !eastValid[i])
        ladder.setMeta((byte)EnumFacing.EAST.getIndex()); 
      if (!northValid[i] && !southValid[i] && !westValid[i] && eastValid[i])
        ladder.setMeta((byte)EnumFacing.WEST.getIndex()); 
      i++;
    } 
    boolean goOn = true;
    while (goOn) {
      goOn = false;
      i = 0;
      for (BuildingBlock ladder : ladders) {
        if (ladder.getMeta() == 0) {
          if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(this, this.buildingKey + ": ladder " + ladder + " has no metada, trying to find neighbours.");
            if (map.containsKey(ladder.p.getAbove()))
              MillLog.major(this, this.buildingKey + ": Above: " + map.get(ladder.p.getAbove())); 
            if (map.containsKey(ladder.p.getBelow()))
              MillLog.major(this, this.buildingKey + ": Below: " + map.get(ladder.p.getBelow())); 
          } 
          if (map.containsKey(ladder.p.getAbove())) {
            BuildingBlock b = map.get(ladder.p.getAbove());
            if (b.block == Blocks.LADDER && b.getMeta() != 0)
              if (b.getMeta() == 5 && northValid[i]) {
                ladder.setMeta(b.getMeta());
                goOn = true;
              } else if (b.getMeta() == 4 && southValid[i]) {
                ladder.setMeta(b.getMeta());
                goOn = true;
              } else if (b.getMeta() == 3 && westValid[i]) {
                ladder.setMeta(b.getMeta());
                goOn = true;
              } else if (b.getMeta() == 2 && eastValid[i]) {
                ladder.setMeta(b.getMeta());
                goOn = true;
              }  
          } 
          if (ladder.getMeta() == 0 && map.containsKey(ladder.p.getBelow())) {
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, this.buildingKey + ": trying ladder below. " + northValid[i] + "/" + southValid[i] + "/" + westValid[i] + "/" + eastValid[i]); 
            BuildingBlock b = map.get(ladder.p.getBelow());
            if (b.block == Blocks.LADDER && b.getMeta() != 0)
              if (b.getMeta() == 5 && northValid[i]) {
                if (MillConfigValues.LogBuildingPlan >= 1)
                  MillLog.major(this, this.buildingKey + ": copying blow: north"); 
                ladder.setMeta(b.getMeta());
                goOn = true;
              } else if (b.getMeta() == 4 && southValid[i]) {
                if (MillConfigValues.LogBuildingPlan >= 1)
                  MillLog.major(this, this.buildingKey + ": copying blow: south"); 
                ladder.setMeta(b.getMeta());
                goOn = true;
              } else if (b.getMeta() == 3 && westValid[i]) {
                if (MillConfigValues.LogBuildingPlan >= 1)
                  MillLog.major(this, this.buildingKey + ": copying blow: west"); 
                ladder.setMeta(b.getMeta());
                goOn = true;
              } else if (b.getMeta() == 2 && eastValid[i]) {
                if (MillConfigValues.LogBuildingPlan >= 1)
                  MillLog.major(this, this.buildingKey + ": copying blow: east"); 
                ladder.setMeta(b.getMeta());
                goOn = true;
              }  
          } 
        } 
        i++;
      } 
    } 
    northValid = new boolean[stairs.size()];
    southValid = new boolean[stairs.size()];
    westValid = new boolean[stairs.size()];
    eastValid = new boolean[stairs.size()];
    i = 0;
    for (BuildingBlock stair : stairs) {
      northValid[i] = (!mapIsOpaqueBlock(map, stair.p.getSouth()) && (
        !mapIsOpaqueBlock(map, stair.p.getNorth().getAbove()) || mapIsOpaqueBlock(map, stair.p.getNorth().getAbove())));
      southValid[i] = (!mapIsOpaqueBlock(map, stair.p.getNorth()) && (
        !mapIsOpaqueBlock(map, stair.p.getSouth().getAbove()) || mapIsOpaqueBlock(map, stair.p.getSouth().getAbove())));
      westValid[i] = (!mapIsOpaqueBlock(map, stair.p.getEast()) && (!mapIsOpaqueBlock(map, stair.p.getWest().getAbove()) || mapIsOpaqueBlock(map, stair.p.getWest().getAbove())));
      eastValid[i] = (!mapIsOpaqueBlock(map, stair.p.getWest()) && (!mapIsOpaqueBlock(map, stair.p.getEast().getAbove()) || mapIsOpaqueBlock(map, stair.p.getEast().getAbove())));
      if (MillConfigValues.LogBuildingPlan >= 1)
        if (northValid[i]) {
          MillLog.major(this, this.buildingKey + ": northValid");
        } else if (southValid[i]) {
          MillLog.major(this, this.buildingKey + ": southValid");
        } else if (westValid[i]) {
          MillLog.major(this, this.buildingKey + ": westValid");
        } else if (eastValid[i]) {
          MillLog.major(this, this.buildingKey + ": eastValid");
        } else {
          MillLog.major(this, this.buildingKey + ": none valid");
        }  
      if (northValid[i]) {
        stair.setMeta((byte)1);
      } else if (southValid[i]) {
        stair.setMeta((byte)0);
      } else if (westValid[i]) {
        stair.setMeta((byte)3);
      } else if (eastValid[i]) {
        stair.setMeta((byte)2);
      } else {
        stair.setMeta((byte)0);
      } 
      i++;
    } 
    for (BuildingBlock door : doors) {
      IBlockState bs = Blocks.OAK_DOOR.getStateFromMeta(door.getMeta());
      EnumFacing facing = (EnumFacing)bs.getValue((IProperty)BlockDoor.FACING);
      boolean invert = false;
      if (facing == EnumFacing.NORTH) {
        if ((!map.containsKey(door.p.getWest()) || ((BuildingBlock)map.get(door.p.getWest())).block == Blocks.AIR || ((BuildingBlock)map.get(door.p.getWest())).block == Blocks.OAK_DOOR) && map
          .containsKey(door.p.getEast()))
          invert = true; 
      } else if (facing == EnumFacing.EAST) {
        if ((!map.containsKey(door.p.getNorth()) || ((BuildingBlock)map.get(door.p.getNorth())).block == Blocks.AIR || ((BuildingBlock)map.get(door.p.getNorth())).block == Blocks.OAK_DOOR) && map
          .containsKey(door.p.getSouth()))
          invert = true; 
      } else if (facing == EnumFacing.SOUTH) {
        if ((!map.containsKey(door.p.getEast()) || ((BuildingBlock)map.get(door.p.getEast())).block == Blocks.AIR || ((BuildingBlock)map.get(door.p.getEast())).block == Blocks.OAK_DOOR) && map
          .containsKey(door.p.getWest()))
          invert = true; 
      } else if (facing == EnumFacing.WEST && (
        !map.containsKey(door.p.getSouth()) || ((BuildingBlock)map.get(door.p.getSouth())).block == Blocks.AIR || ((BuildingBlock)map.get(door.p.getSouth())).block == Blocks.OAK_DOOR) && map
        .containsKey(door.p.getNorth())) {
        invert = true;
      } 
      if (invert)
        door.special = BuildingBlock.INVERTED_DOOR; 
    } 
  }
  
  public List<LocationBuildingPair> build(MillWorldData mw, VillageType villageType, BuildingLocation location, boolean villageGeneration, boolean isBuildingTownHall, Building townHall, boolean wandimport, boolean includeSpecialPoints, EntityPlayer owner, boolean rushBuilding) {
    if (!isBuildingTownHall && townHall == null && !wandimport)
      MillLog.error(this, "Building is not TH and does not have TH's position."); 
    World world = mw.world;
    List<LocationBuildingPair> buildings = new ArrayList<>();
    boolean[][] laySnow = (boolean[][])null;
    if (villageGeneration || wandimport)
      laySnow = checkForSnow(world, location); 
    boolean initialBuild = (location.level == 0 && !location.isSubBuildingLocation);
    boolean isLargeBuilding = (initialBuild && this.width * this.length > 2500);
    if (isLargeBuilding)
      ServerSender.sendTranslatedSentenceInRange(world, location.pos, 2147483647, '4', "other.largebuildinggeneration", new String[] { this.nativeName }); 
    BuildingBlock[] bblocks = getBuildingPoints(world, location, villageGeneration, includeSpecialPoints, false);
    boolean measureTime = (MillConfigValues.DEV && isLargeBuilding);
    long startTime = System.currentTimeMillis();
    if (measureTime)
      MillLog.temp(this, "Starting build. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    for (BuildingBlock bblock : bblocks)
      bblock.build(world, townHall, villageGeneration, wandimport); 
    if (measureTime)
      MillLog.temp(this, "Finished building. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    if (containsTags("hof"))
      fillHoFSigns(location, world); 
    if (laySnow != null)
      setSnow(world, location, laySnow); 
    Point townHallPos = null;
    if (townHall != null)
      townHallPos = townHall.getPos(); 
    if (bblocks.length > 0 && !wandimport)
      if (location.level == 0) {
        Building building = new Building(mw, this.culture, villageType, location, isBuildingTownHall, villageGeneration, townHallPos);
        if (MillConfigValues.LogWorldGeneration >= 2)
          MillLog.minor(this, "Building " + this.planName + " at " + location); 
        referenceBuildingPoints(building);
        building.initialise(owner, (villageGeneration || rushBuilding));
        building.fillStartingGoods();
        buildings.add(new LocationBuildingPair(building, location));
        building.updateBanners();
        if (isBuildingTownHall) {
          townHallPos = building.getPos();
          townHall = building;
        } 
        updateTags(building);
      } else {
        Building building = location.getBuilding(world);
        if (building != null)
          updateBuildingForPlan(building); 
        townHall = building;
      }  
    if (bblocks.length > 0 && wandimport && location.level == 0)
      displayPanelNumbers(world, location); 
    if (this.culture != null && !wandimport && location.level == 0)
      for (String sb : this.startingSubBuildings) {
        boolean validSubBuilding = true;
        if (townHall != null)
          validSubBuilding = townHall.isValidProject(new BuildingProject(this.culture.getBuildingPlanSet(sb), this)); 
        if (validSubBuilding) {
          BuildingPlan plan = this.culture.getBuildingPlanSet(sb).getRandomStartingPlan();
          BuildingLocation l = location.createLocationForStartingSubBuilding(sb);
          List<LocationBuildingPair> vb = plan.build(mw, villageType, l, villageGeneration, false, townHall, false, false, owner, rushBuilding);
          location.subBuildings.add(sb);
          for (LocationBuildingPair p : vb)
            buildings.add(p); 
          continue;
        } 
        MillLog.temp(this, "Cannot create starting subbuilding " + sb + " as it is invalid.");
      }  
    if (villageGeneration || wandimport)
      build_cleanup(location, world); 
    world.markBlockRangeForRenderUpdate(location.pos.getiX() - this.length / 2 - 5, location.pos.getiY() - this.plan.length - 5, location.pos.getiZ() - this.width / 2 - 5, location.pos
        .getiX() + this.length / 2 + 5, location.pos.getiY() - 5, location.pos.getiZ() + this.width / 2 + 5);
    return buildings;
  }
  
  private void build_cleanup(BuildingLocation location, World world) {
    List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(world, EntityItem.class, location.pos, Math.max(this.width / 2, this.length / 2) + 5, 10);
    for (Entity entity : entities)
      entity.setDead(); 
    entities = WorldUtilities.getEntitiesWithinAABB(world, EntityAnimal.class, location.pos, Math.max(this.width / 2, this.length / 2) + 5, 10);
    for (Entity entity : entities) {
      EntityAnimal animal = (EntityAnimal)entity;
      if (animal.isEntityInsideOpaqueBlock() && !animal.isNoDespawnRequired())
        entity.setDead(); 
    } 
    entities = WorldUtilities.getEntitiesWithinAABB(world, EntityMob.class, location.pos, Math.max(this.width / 2, this.length / 2) + 5, 10);
    for (Entity entity : entities) {
      EntityMob mob = (EntityMob)entity;
      if (mob.isEntityInsideOpaqueBlock() && !mob.isNoDespawnRequired())
        entity.setDead(); 
    } 
    TreeClearer treeClearer = new TreeClearer(this, location, world);
    treeClearer.cleanup();
  }
  
  private boolean[][] checkForSnow(World world, BuildingLocation location) {
    boolean[][] snow = new boolean[location.length + this.areaToClearLengthBefore + this.areaToClearLengthAfter][location.width + this.areaToClearWidthBefore + this.areaToClearWidthAfter];
    int x = location.pos.getiX();
    int z = location.pos.getiZ();
    int orientation = location.orientation;
    for (int dx = -this.areaToClearLengthBefore; dx < this.length + this.areaToClearLengthAfter; dx++) {
      for (int dz = -this.areaToClearWidthBefore; dz < this.width + this.areaToClearWidthAfter; dz++) {
        Point p = adjustForOrientation(x, 256, z, dx - this.lengthOffset, dz - this.widthOffset, orientation);
        boolean stop = false;
        boolean isSnow = false;
        while (!stop && p.y > 0.0D) {
          if (p.getBlock(world) == Blocks.SNOW || p.getBlock(world) == Blocks.SNOW_LAYER) {
            stop = true;
            isSnow = true;
          } else if (p.getBlockActualState(world).isFullBlock()) {
            stop = true;
          } 
          p = p.getBelow();
        } 
        snow[dx + this.areaToClearLengthBefore][dz + this.areaToClearWidthBefore] = isSnow;
      } 
    } 
    return snow;
  }
  
  public boolean containsTags(String tag) {
    return (this.tags.contains(tag.toLowerCase()) && !this.clearTags.contains(tag.toLowerCase()));
  }
  
  public void displayPanelNumbers(World world, BuildingLocation location) {
    int x = location.pos.getiX();
    int y = location.pos.getiY();
    int z = location.pos.getiZ();
    int orientation = location.orientation;
    int signNb = 0;
    for (int dy = 0; dy < this.nbfloors; dy++) {
      for (int dx = 0; dx < this.length; dx++) {
        for (int dz = 0; dz < this.width; dz++) {
          PointType pt = this.plan[dy][dx][dz];
          Point p = adjustForOrientation(x, y + dy + this.startLevel, z, dx - this.lengthOffset, dz - this.widthOffset, orientation);
          if (pt.isType("signwallGuess") || pt.getBlock() == MillBlocks.PANEL) {
            TileEntitySign signEntity = p.getSign(world);
            if (signEntity != null) {
              signEntity.signText[0] = (ITextComponent)new TextComponentString("Panel " + (signNb + 1));
              if (signNb < this.signOrder.length)
                if (this.signOrder.length > 8) {
                  int panelType = this.signOrder[signNb];
                  String panelName = "";
                  if (panelType == 0) {
                    panelName = panelType + ": Village Name";
                  } else if (panelType == 1) {
                    panelName = panelType + ": Res 1";
                  } else if (panelType == 2) {
                    panelName = panelType + ": Res 2";
                  } else if (panelType == 3) {
                    panelName = panelType + ": Res 3";
                  } else if (panelType == 4) {
                    panelName = panelType + ": Project";
                  } else if (panelType == 5) {
                    panelName = panelType + ": Construction";
                  } else if (panelType == 6) {
                    panelName = panelType + ": Population";
                  } else if (panelType == 7) {
                    panelName = panelType + ": Map";
                  } else if (panelType == 8) {
                    panelName = panelType + ": Military";
                  } else {
                    panelName = "" + panelType;
                  } 
                  signEntity.signText[2] = (ITextComponent)new TextComponentString(panelName);
                } else {
                  int panelType = this.signOrder[signNb];
                  signEntity.signText[2] = (ITextComponent)new TextComponentString("" + panelType);
                }  
            } 
            signNb++;
          } 
        } 
      } 
    } 
  }
  
  private void fillHoFSigns(BuildingLocation location, World world) {
    int signNb = 0;
    List<String> hofData = LanguageUtilities.getHoFData();
    for (int z = location.pos.getiZ() - this.width / 2; z < location.pos.getiZ() + this.width / 2; z++) {
      for (int x = location.pos.getiX() + this.length / 2; x >= location.pos.getiX() - this.length / 2; x--) {
        for (int y = location.pos.getiY() + this.plan.length; y >= location.pos.getiY(); y--) {
          if (WorldUtilities.getBlock(world, x, y, z) == Blocks.WALL_SIGN || WorldUtilities.getBlock(world, x, y, z) == MillBlocks.PANEL) {
            TileEntitySign sign = (new Point(x, y, z)).getSign(world);
            if (sign != null) {
              if (signNb < hofData.size()) {
                String[] lines = ((String)hofData.get(signNb)).split(";");
                for (int i = 0; i < Math.min(4, lines.length); i++) {
                  if ((i == 0 && signNb != 8 && signNb != 48) || lines[i].length() == 0) {
                    sign.signText[i] = (ITextComponent)new TextComponentString(lines[i]);
                  } else {
                    sign.signText[i] = (ITextComponent)new TextComponentString(LanguageUtilities.string(lines[i]));
                  } 
                } 
              } 
              signNb++;
            } 
          } 
        } 
      } 
    } 
  }
  
  public BuildingLocation findBuildingLocation(VillageMapInfo winfo, RegionMapper regionMapper, Point centre, int maxRadius, Random random, int porientation) {
    long startTime = System.nanoTime();
    int ci = centre.getiX() - winfo.mapStartX;
    int cj = centre.getiZ() - winfo.mapStartZ;
    int radius = (int)(maxRadius * this.minDistance);
    maxRadius = (int)(maxRadius * this.maxDistance);
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(this, "testBuildWorldInfo: Called to test for building " + this.planName + " around " + centre + "(" + ci + "/" + cj + "), start radius: " + radius + ", max radius: " + maxRadius); 
    if (porientation == -1)
      porientation = this.fixedOrientation; 
    for (int i = 0; i < winfo.length; i++) {
      for (int j = 0; j < winfo.width; j++)
        winfo.buildTested[i][j] = false; 
    } 
    while (radius < maxRadius) {
      int mini = Math.max(0, ci - radius);
      int maxi = Math.min(winfo.length - 1, ci + radius);
      int minj = Math.max(0, cj - radius);
      int maxj = Math.min(winfo.width - 1, cj + radius);
      if (MillConfigValues.LogWorldGeneration >= 3)
        MillLog.debug(this, "Testing square: " + mini + "/" + minj + " to " + maxi + "/" + maxj); 
      for (int k = mini; k < maxi; k++) {
        if (cj - radius == minj) {
          LocationReturn lr = testSpot(winfo, regionMapper, centre, k, minj, random, porientation, false);
          if (lr.location != null) {
            if (MillConfigValues.LogBuildingPlan >= 2)
              MillLog.minor(this, "Time taken for location search: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
            return lr.location;
          } 
        } 
        if (cj + radius == maxj) {
          LocationReturn lr = testSpot(winfo, regionMapper, centre, k, maxj, random, porientation, false);
          if (lr.location != null) {
            if (MillConfigValues.LogBuildingPlan >= 2)
              MillLog.minor(this, "Time taken for location search: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
            return lr.location;
          } 
        } 
      } 
      for (int j = minj; j < maxj; j++) {
        if (ci - radius == mini) {
          LocationReturn lr = testSpot(winfo, regionMapper, centre, mini, j, random, porientation, false);
          if (lr.location != null) {
            if (MillConfigValues.LogBuildingPlan >= 2)
              MillLog.minor(this, "Time taken for location search: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
            return lr.location;
          } 
        } 
        if (ci + radius == maxi) {
          LocationReturn lr = testSpot(winfo, regionMapper, centre, maxi, j, random, porientation, false);
          if (lr.location != null) {
            if (MillConfigValues.LogBuildingPlan >= 2)
              MillLog.minor(this, "Time taken for location search: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
            return lr.location;
          } 
        } 
      } 
      radius++;
    } 
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(this, "Could not find acceptable location (radius: " + radius + ")"); 
    if (MillConfigValues.LogBuildingPlan >= 2)
      MillLog.minor(this, "Time taken for unsuccessful location search: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
    return null;
  }
  
  public BuildingBlock[] getBuildingPoints(World world, BuildingLocation location, boolean villageGeneration, boolean includeSpecialPoints, boolean deletionLogs) {
    boolean isLargeBuilding = (location.level == 0 && !location.isSubBuildingLocation && this.width * this.length > 2500);
    boolean measureTime = (MillConfigValues.DEV && isLargeBuilding);
    long startTime = System.currentTimeMillis();
    if (measureTime)
      MillLog.temp(this, "Starting. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    int locationX = location.pos.getiX();
    int locationY = location.pos.getiY();
    int locationZ = location.pos.getiZ();
    int orientation = location.orientation;
    int approximateBlocks = (this.length + this.areaToClearLengthBefore + this.areaToClearLengthAfter) * (this.width + this.areaToClearWidthBefore + this.areaToClearWidthAfter) * (this.nbfloors + 50);
    List<BuildingBlock> bblocksCombined = new ArrayList<>(approximateBlocks + 100);
    if (MillConfigValues.LogWorldGeneration >= 2)
      MillLog.minor(this, "Getting blocks for " + this.planName + " at " + locationX + "/" + locationY + "/" + locationZ + "/" + orientation); 
    if (measureTime)
      MillLog.temp(this, "Allocated list. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    if (!this.isUpdate && !this.isSubBuilding && !location.bedrocklevel)
      getBuildingPoints_prepareGround(location, locationX, locationY, locationZ, orientation, bblocksCombined); 
    if (measureTime)
      MillLog.temp(this, "Prepared ground. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    List<BuildingBlock> bblocksPreserveGroundClearTrees = new ArrayList<>();
    List<BuildingBlock> bblocksDeletion = new ArrayList<>();
    List<BuildingBlock> bblocksFirstPass = new ArrayList<>();
    BuildingBlock mainChest = null;
    short[] XZCoords = getBuildingPoints_computeXZCoords((short)this.length, (short)this.width);
    int deltaY;
    for (deltaY = this.nbfloors - 1; deltaY >= 0; deltaY--) {
      for (int xzPos = 0; xzPos < XZCoords.length; xzPos += 2) {
        short deltaX = XZCoords[xzPos];
        short deltaZ = XZCoords[xzPos + 1];
        PointType pt = this.plan[deltaY][deltaX][deltaZ];
        if (pt.getBlock() == Blocks.AIR) {
          Point p = adjustForOrientation(locationX, locationY + deltaY + this.startLevel, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
          bblocksDeletion.add(new BuildingBlock(p, Blocks.AIR, 0));
        } 
      } 
    } 
    for (deltaY = 0; deltaY < this.nbfloors; deltaY++) {
      for (int xzPos = 0; xzPos < XZCoords.length; xzPos += 2) {
        short deltaX = XZCoords[xzPos];
        short deltaZ = XZCoords[xzPos + 1];
        boolean surfaceAndBelow = (deltaY + this.startLevel < 0);
        int adjustedDeltaY = surfaceAndBelow ? (-deltaY - this.startLevel - 1) : deltaY;
        PointType pt = this.plan[adjustedDeltaY][deltaX][deltaZ];
        Point p = adjustForOrientation(locationX, locationY + adjustedDeltaY + this.startLevel, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
        boolean underground = (adjustedDeltaY + this.startLevel < -1);
        if (pt.isType("preserveground")) {
          if (underground) {
            bblocksPreserveGroundClearTrees.add(new BuildingBlock(p, BuildingBlock.PRESERVEGROUNDDEPTH));
          } else {
            bblocksPreserveGroundClearTrees.add(new BuildingBlock(p, BuildingBlock.PRESERVEGROUNDSURFACE));
          } 
        } else if (pt.isType("allbuttrees")) {
          bblocksPreserveGroundClearTrees.add(new BuildingBlock(p, BuildingBlock.CLEARTREE));
        } 
        getBuildingPoints_buildingFirstPass(location, villageGeneration, includeSpecialPoints, orientation, bblocksFirstPass, pt, p);
      } 
    } 
    bblocksCombined.addAll(bblocksPreserveGroundClearTrees);
    bblocksCombined.addAll(bblocksDeletion);
    bblocksCombined.addAll(bblocksFirstPass);
    if (measureTime)
      MillLog.temp(this, "First pass done. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    for (deltaY = 0; deltaY < this.nbfloors; deltaY++) {
      for (int xzPos = 0; xzPos < XZCoords.length; xzPos += 2) {
        short deltaX = XZCoords[xzPos];
        short deltaZ = XZCoords[xzPos + 1];
        int adjustedDeltaY = (deltaY + this.startLevel < 0) ? (-deltaY - this.startLevel - 1) : deltaY;
        PointType pt = this.plan[adjustedDeltaY][deltaX][deltaZ];
        Point p = adjustForOrientation(locationX, locationY + adjustedDeltaY + this.startLevel, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
        getBuildingPoints_buildingSecondPass(orientation, bblocksCombined, pt, p);
        getBuildingPoints_specialBlocks(bblocksCombined, pt, p);
        if (includeSpecialPoints && pt.specialType != null && pt.specialType.length() > 0 && !pt.specialType.contains("torch") && !pt.specialType.contains("chest") && 
          !pt.specialType.contains("furnace") && !pt.specialType.contains("sign") && !pt.specialType.contains("Banner"))
          getBuildingPoints_mockBlocks(includeSpecialPoints, bblocksCombined, pt, p); 
        if (pt.isSubType("mainchest")) {
          location.chestPos = p;
          mainChest = getBuildingPoints_mainChest(location, includeSpecialPoints, pt, p);
        } 
      } 
    } 
    if (mainChest != null)
      bblocksCombined.add(mainChest); 
    if (location.chestPos == null)
      location.chestPos = ((BuildingBlock)bblocksCombined.get(bblocksCombined.size() - 1)).p; 
    autoGuessLaddersDoorsStairs(bblocksCombined);
    if (location.sleepingPos == null)
      location.sleepingPos = location.chestPos; 
    if (measureTime)
      MillLog.temp(this, "Second pass over. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    Map<IntPoint, BuildingBlock> bbmap = new TreeMap<>();
    boolean[] toDelete = new boolean[bblocksCombined.size()];
    long totalWorldCall = 0L, totalLogic = 0L, totalIteration = 0L;
    int deleteSameBlock = 0, deleteClearTree = 0, deleteClearGround = 0, deletePreserveDepth = 0, deletePreserveSurface = 0;
    for (int i = 0; i < bblocksCombined.size(); i++) {
      IBlockState blockState;
      int special;
      long nanoStart2 = System.nanoTime();
      BuildingBlock bb = bblocksCombined.get(i);
      IntPoint ip = bb.p.getIntPoint();
      long nanoStart = System.nanoTime();
      if (bbmap.containsKey(ip)) {
        blockState = ((BuildingBlock)bbmap.get(ip)).getBlockstate();
        special = ((BuildingBlock)bbmap.get(ip)).special;
      } else {
        blockState = WorldUtilities.getBlockState(world, bb.p);
        special = 0;
      } 
      totalWorldCall += System.nanoTime() - nanoStart;
      nanoStart = System.nanoTime();
      Block block = blockState.getBlock();
      if (((blockState == bb.getBlockstate() && special == 0) || (block == Blocks.GRASS && bb.block == Blocks.DIRT)) && bb.special == 0) {
        toDelete[i] = true;
        deleteSameBlock++;
        if (deletionLogs)
          MillLog.minor(bb, "Removing identical block states: " + blockState); 
      } else if (bb.special == BuildingBlock.CLEARTREE && block != Blocks.LOG && block != Blocks.LEAVES && block != Blocks.LOG2 && block != Blocks.LEAVES2) {
        toDelete[i] = true;
        deleteClearTree++;
        if (deletionLogs)
          MillLog.minor(bb, "Removing clear tree: " + blockState); 
      } else if (bb.special == BuildingBlock.CLEARGROUND && (block == null || block == Blocks.AIR)) {
        toDelete[i] = true;
        deleteClearGround++;
        if (deletionLogs)
          MillLog.minor(bb, "Removing clear ground: " + blockState); 
      } else if (bb.special == BuildingBlock.PRESERVEGROUNDDEPTH && WorldUtilities.getBlockStateValidGround(blockState, false) == blockState) {
        toDelete[i] = true;
        deletePreserveDepth++;
        if (deletionLogs)
          MillLog.minor(bb, "Removing preserve ground depth: " + blockState); 
      } else if (bb.special == BuildingBlock.PRESERVEGROUNDSURFACE && WorldUtilities.getBlockStateValidGround(blockState, true) == blockState) {
        toDelete[i] = true;
        deletePreserveSurface++;
        if (deletionLogs)
          MillLog.minor(bb, "Removing preserve ground surface: " + blockState); 
      } else {
        bbmap.put(ip, bb);
        toDelete[i] = false;
      } 
      totalLogic += System.nanoTime() - nanoStart;
      totalIteration += System.nanoTime() - nanoStart2;
    } 
    if (measureTime) {
      MillLog.temp(this, "Computed map. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L));
      MillLog.temp(this, "totalLogic: " + (totalLogic / 1000000L) + ", totalWorldCall: " + (totalWorldCall / 1000000L) + ", totalIteration: " + (totalIteration / 1000000L));
    } 
    int finalSize = 0;
    for (int j = 0; j < bblocksCombined.size(); j++) {
      if (!toDelete[j])
        finalSize++; 
    } 
    if (finalSize == 0 && !deletionLogs) {
      MillLog.warning(this, "BBlocks size is zero (there is nothing to do to build this building). Size before deletion: " + bblocksCombined.size());
      MillLog.warning(this, "Deletion counters: deleteSameBlock: " + deleteSameBlock + ", deleteClearTree: " + deleteClearTree + ", deleteClearGround: " + deleteClearGround + ", deletePreserveDepth: " + deletePreserveDepth + ", deletePreserveSurface: " + deletePreserveSurface);
      getBuildingPoints(world, location, villageGeneration, includeSpecialPoints, true);
    } 
    BuildingBlock[] abblocks = new BuildingBlock[finalSize];
    int arrayPos = 0;
    for (int k = 0; k < bblocksCombined.size(); k++) {
      if (!toDelete[k]) {
        abblocks[arrayPos] = bblocksCombined.get(k);
        arrayPos++;
      } 
    } 
    if (measureTime)
      MillLog.temp(this, "Removed unneeded blocks. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    if (measureTime)
      MillLog.temp(this, "Done. Time passed: " + Math.round((float)(System.currentTimeMillis() - startTime)) + "ms. Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L / 1024L)); 
    return abblocks;
  }
  
  private void getBuildingPoints_buildingFirstPass(BuildingLocation location, boolean villageGeneration, boolean includeSpecialPoints, int orientation, List<BuildingBlock> bblocks, PointType pt, Point p) {
    int m = 0;
    Block b = null;
    IBlockState bs = null;
    if (pt.getBlock() != null && pt.getBlock() != Blocks.AIR && !pt.secondStep) {
      bs = pt.getBlockState();
      if (orientation != 0) {
        IBlockState rotatedBlockState = attemptAutoRotation(pt, orientation);
        if (rotatedBlockState != null)
          bs = rotatedBlockState; 
      } 
      if (!includeSpecialPoints && bs.getBlock() instanceof org.millenaire.common.block.IPaintedBlock) {
        EnumDyeColor color = BlockPaintedBricks.getColourFromBlockState(bs);
        if (location.paintedBricksColour.containsKey(color))
          bs = BlockPaintedBricks.getBlockStateWithColour(bs, (EnumDyeColor)location.paintedBricksColour.get(color)); 
      } 
    } else if (pt.isType("empty") && !this.isUpdate && !this.isSubBuilding) {
      b = Blocks.AIR;
    } else if (pt.isType("grass") && (villageGeneration || includeSpecialPoints)) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("grass") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("soil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("ricesoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("turmericsoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("maizesoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("carrotsoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("potatosoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("flowersoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("sugarcanesoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("vinesoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("cacaospot") && villageGeneration) {
      b = Blocks.COCOA;
    } else if (pt.isType("cottonsoil") && villageGeneration) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("soil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("ricesoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("turmericsoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("maizesoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("carrotsoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("potatosoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("flowersoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("sugarcanesoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("vinesoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("cacaospot") && !villageGeneration) {
      b = null;
    } else if (pt.isType("cottonsoil") && !villageGeneration) {
      b = Blocks.DIRT;
    } else if (pt.isType("netherwartsoil")) {
      b = Blocks.SOUL_SAND;
    } else if (pt.isType("silkwormblock")) {
      BlockSilkWorm blockSilkWorm = MillBlocks.SILK_WORM;
      m = 0;
    } else if (pt.isType("snailsoilblock")) {
      BlockSnailSoil blockSnailSoil = MillBlocks.SNAIL_SOIL;
      m = 0;
    } else if (pt.isType("lockedchestTop")) {
      bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.WEST);
    } else if (pt.isType("lockedchestBottom")) {
      bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.EAST);
    } else if (pt.isType("lockedchestLeft")) {
      bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.SOUTH);
    } else if (pt.isType("lockedchestRight")) {
      bs = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.NORTH);
    } else if (pt.isType("brewingstand")) {
      bs = Blocks.BREWING_STAND.getDefaultState();
    } else if (!includeSpecialPoints && pt.specialType != null && MockBlockMarker.Type.getMetaFromName(pt.specialType) > 0) {
      b = Blocks.AIR;
      if (pt.isType("sleepingPos")) {
        location.sleepingPos = p;
      } else if (pt.isType("sellingPos")) {
        location.sellingPos = p;
      } else if (pt.isType("craftingPos")) {
        location.craftingPos = p;
      } else if (pt.isType("shelterPos")) {
        location.shelterPos = p;
      } else if (pt.isType("defendingPos")) {
        location.defendingPos = p;
      } 
    } else if (!includeSpecialPoints && pt.specialType != null && MockBlockAnimalSpawn.Creature.getMetaFromName(pt.specialType.substring(0, pt.specialType.length() - 5)) > -1) {
      b = Blocks.AIR;
    } else if (!includeSpecialPoints && pt.specialType != null && MockBlockTreeSpawn.TreeType.getMetaFromName(pt.specialType.substring(0, pt.specialType.length() - 5)) > -1) {
      b = Blocks.AIR;
    } else if (!includeSpecialPoints && pt.specialType != null && MockBlockDecor.Type.getMetaFromName(pt.specialType) > -1) {
      b = Blocks.AIR;
    } else if (pt.isType("sandsource")) {
      BlockSand blockSand = Blocks.SAND;
    } else if (pt.isType("sandstonesource")) {
      b = Blocks.SANDSTONE;
    } else if (pt.isType("redsandstonesource")) {
      b = Blocks.RED_SANDSTONE;
    } else if (pt.isType("claysource")) {
      b = Blocks.CLAY;
    } else if (pt.isType("gravelsource")) {
      b = Blocks.GRAVEL;
    } else if (pt.isType("stonesource")) {
      b = Blocks.STONE;
    } else if (pt.isType("granitesource")) {
      bs = Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.GRANITE);
    } else if (pt.isType("dioritesource")) {
      bs = Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.DIORITE);
    } else if (pt.isType("andesitesource")) {
      bs = Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.ANDESITE);
    } else if (pt.isType("snowsource")) {
      b = Blocks.SNOW_LAYER;
    } else if (pt.isType("icesource")) {
      b = Blocks.ICE;
    } else if (pt.isType("quartzsource")) {
      b = Blocks.QUARTZ_ORE;
    } else if (pt.isType("freesand")) {
      BlockSand blockSand = Blocks.SAND;
    } else if (pt.isType("freesandstone")) {
      b = Blocks.SANDSTONE;
    } else if (pt.isType("freegravel")) {
      b = Blocks.GRAVEL;
    } else if (pt.isType("freewool")) {
      b = Blocks.WOOL;
    } else if (pt.isType("freestone")) {
      b = Blocks.STONE;
    } else if (pt.isType("freecobblestone")) {
      b = Blocks.COBBLESTONE;
    } else if (pt.isType("freestonebrick")) {
      b = Blocks.STONEBRICK;
    } else if (pt.isType("freepaintedbrick")) {
      BlockPaintedBricks blockPaintedBricks = MillBlocks.PAINTED_BRICK_WHITE;
    } else if (pt.isType("freegrass_block")) {
      BlockGrass blockGrass = Blocks.GRASS;
    } else if (pt.isType("squidspawn")) {
      BlockStaticLiquid blockStaticLiquid = Blocks.WATER;
    } 
    if (includeSpecialPoints && pt.specialType != null && pt.specialType.length() > 0 && !pt.specialType.contains("torch") && !pt.specialType.contains("chest") && 
      !pt.specialType.contains("furnace") && !pt.specialType.contains("sign")) {
      boolean mockBlockFound = (pt.specialType.equalsIgnoreCase("grass") || pt.specialType.equalsIgnoreCase("empty"));
      for (MockBlockMarker.Type type : MockBlockMarker.Type.values()) {
        if (type.name.equalsIgnoreCase(pt.specialType)) {
          bs = MillBlocks.MARKER_BLOCK.getStateFromMeta(type.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      for (MockBlockAnimalSpawn.Creature type : MockBlockAnimalSpawn.Creature.values()) {
        if (pt.specialType.equalsIgnoreCase(type.name + "spawn")) {
          bs = MillBlocks.ANIMAL_SPAWN.getStateFromMeta(type.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      for (MockBlockSource.Resource resource : MockBlockSource.Resource.values()) {
        if (pt.specialType.equalsIgnoreCase(resource.name + "source")) {
          bs = MillBlocks.SOURCE.getStateFromMeta(resource.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      for (MockBlockFree.Resource resource : MockBlockFree.Resource.values()) {
        if (pt.specialType.equalsIgnoreCase("free" + resource.name)) {
          bs = MillBlocks.FREE_BLOCK.getStateFromMeta(resource.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      for (MockBlockTreeSpawn.TreeType treeType : MockBlockTreeSpawn.TreeType.values()) {
        if (pt.specialType.equalsIgnoreCase(treeType.name + "spawn")) {
          bs = MillBlocks.TREE_SPAWN.getStateFromMeta(treeType.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      for (MockBlockSoil.CropType cropType : MockBlockSoil.CropType.values()) {
        if (pt.specialType.equalsIgnoreCase(cropType.name)) {
          bs = MillBlocks.SOIL_BLOCK.getStateFromMeta(cropType.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      for (MockBlockDecor.Type decorType : MockBlockDecor.Type.values()) {
        if (pt.specialType.equalsIgnoreCase(decorType.name)) {
          bs = MillBlocks.DECOR_BLOCK.getStateFromMeta(decorType.meta);
          mockBlockFound = true;
          break;
        } 
      } 
      if (!mockBlockFound)
        if (pt.isSubType("cultureBannerWall")) {
          String facing = pt.specialType.substring(17);
          bs = attemptAutoRotation(MillBlocks.CULTURE_BANNER_WALL.getDefaultState().withProperty((IProperty)BlockBanner.FACING, (Comparable)EnumFacing.byName(facing)), orientation);
          mockBlockFound = true;
        } else if (pt.isSubType("villageBannerWall")) {
          String facing = pt.specialType.substring(17);
          bs = attemptAutoRotation(MillBlocks.VILLAGE_BANNER_WALL.getDefaultState().withProperty((IProperty)BlockBanner.FACING, (Comparable)EnumFacing.byName(facing)), orientation);
          mockBlockFound = true;
        } else if (pt.isSubType("cultureBannerStanding")) {
          int rotation = Integer.parseInt(pt.specialType.substring(21));
          bs = MillBlocks.CULTURE_BANNER_STANDING.getStateFromMeta(rotation);
          mockBlockFound = true;
        } else if (pt.isSubType("villageBannerStanding")) {
          int rotation = Integer.parseInt(pt.specialType.substring(21));
          bs = MillBlocks.VILLAGE_BANNER_STANDING.getStateFromMeta(rotation);
          mockBlockFound = true;
        }  
      if (!mockBlockFound) {
        String s = LanguageUtilities.string("import.missingmockblock", new String[] { pt.specialType });
        System.out.println("Missing placefirst mock block " + pt.specialType);
        Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), 'c', s);
      } 
    } 
    if (bs != null) {
      b = bs.getBlock();
      m = b.getMetaFromState(bs);
      bblocks.add(new BuildingBlock(p, b, m));
    } else if (b != null) {
      bblocks.add(new BuildingBlock(p, b, m));
    } 
  }
  
  private void getBuildingPoints_buildingSecondPass(int orientation, List<BuildingBlock> bblocks, PointType pt, Point p) {
    int m = 0;
    Block b = null;
    IBlockState bs = null;
    if (pt.getBlock() != null && pt.getBlock() != Blocks.AIR && !(pt.getBlock() instanceof net.minecraft.block.BlockFlowerPot) && pt.secondStep) {
      bs = pt.getBlockState();
      if (orientation != 0) {
        IBlockState rotatedBlockState = attemptAutoRotation(pt, orientation);
        if (rotatedBlockState != null)
          bs = rotatedBlockState; 
      } 
    } else if (pt.isType("ladderGuess")) {
      b = Blocks.LADDER;
      m = -1;
    } else if (pt.isType("signwallGuess")) {
      bs = MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)guessWallOrientation(bblocks, p, false));
    } else if (pt.isType("plainSignGuess")) {
      bs = Blocks.WALL_SIGN.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)guessWallOrientation(bblocks, p, false));
    } else if (pt.getBlock() instanceof net.minecraft.block.BlockFlowerPot) {
      bs = null;
      b = pt.getBlock();
      m = pt.getMeta();
    } else if (pt.isSubType("cultureBannerWall")) {
      String facing = pt.specialType.substring(17);
      bs = attemptAutoRotation(MillBlocks.CULTURE_BANNER_WALL.getDefaultState().withProperty((IProperty)BlockBanner.FACING, (Comparable)EnumFacing.byName(facing)), orientation);
    } else if (pt.isSubType("villageBannerWall")) {
      String facing = pt.specialType.substring(17);
      bs = attemptAutoRotation(MillBlocks.VILLAGE_BANNER_WALL.getDefaultState().withProperty((IProperty)BlockBanner.FACING, (Comparable)EnumFacing.byName(facing)), orientation);
    } else if (pt.isSubType("cultureBannerStanding")) {
      int rotation = Integer.parseInt(pt.specialType.substring(21));
      bs = MillBlocks.CULTURE_BANNER_STANDING.getStateFromMeta(rotation);
    } else if (pt.isSubType("villageBannerStanding")) {
      int rotation = Integer.parseInt(pt.specialType.substring(21));
      bs = MillBlocks.VILLAGE_BANNER_STANDING.getStateFromMeta(rotation);
    } 
    if (bs != null) {
      b = bs.getBlock();
      m = b.getMetaFromState(bs);
      bblocks.add(new BuildingBlock(p, b, m));
    } else if (b != null) {
      bblocks.add(new BuildingBlock(p, b, m));
    } 
  }
  
  private short[] getBuildingPoints_computeXZCoords(short targetLength, short targetWidth) {
    short[] XZCoords = new short[targetLength * targetWidth * 2];
    int pos = 0;
    short partX;
    for (partX = 0; partX < targetLength / 8 + 1; partX = (short)(partX + 1)) {
      short partMaxX = 8;
      if ((partX + 1) * 8 > targetLength)
        partMaxX = (short)(targetLength - partX * 8); 
      short partZ;
      for (partZ = 0; partZ < targetWidth / 8 + 1; partZ = (short)(partZ + 1)) {
        short partMaxZ = 8;
        if ((partZ + 1) * 8 > targetWidth)
          partMaxZ = (short)(targetWidth - partZ * 8); 
        short withinPartX;
        for (withinPartX = 0; withinPartX < partMaxX; withinPartX = (short)(withinPartX + 1)) {
          short withinPartZ;
          for (withinPartZ = 0; withinPartZ < partMaxZ; withinPartZ = (short)(withinPartZ + 1)) {
            short adjustedWithinPartZ = (short)((withinPartX % 2 == 0) ? withinPartZ : (partMaxZ - withinPartZ - 1));
            short deltaX = (short)(partX * 8 + withinPartX);
            short deltaZ = (short)(partZ * 8 + adjustedWithinPartZ);
            XZCoords[pos] = deltaX;
            XZCoords[pos + 1] = deltaZ;
            pos += 2;
          } 
        } 
      } 
    } 
    return XZCoords;
  }
  
  private BuildingBlock getBuildingPoints_mainChest(BuildingLocation location, boolean includeSpecialPoints, PointType pt, Point p) {
    IBlockState chestBlockState = null;
    if (includeSpecialPoints) {
      if (pt.specialType.equals("mainchestTop")) {
        chestBlockState = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.WEST.getIndex());
      } else if (pt.specialType.equals("mainchestBottom")) {
        chestBlockState = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.EAST.getIndex());
      } else if (pt.specialType.equals("mainchestLeft")) {
        chestBlockState = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.SOUTH.getIndex());
      } else if (pt.specialType.equals("mainchestRight")) {
        chestBlockState = MillBlocks.MAIN_CHEST.getStateFromMeta(EnumFacing.NORTH.getIndex());
      } else if (pt.specialType.equals("mainchestGuess")) {
        return new BuildingBlock(p, BuildingBlock.CHESTGUESS);
      } 
      return new BuildingBlock(p, chestBlockState);
    } 
    if (pt.isType("mainchestGuess"))
      return new BuildingBlock(p, BuildingBlock.CHESTGUESS); 
    if (pt.isType("lockedchestTop") || pt.isType("mainchestTop")) {
      chestBlockState = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.WEST);
    } else if (pt.isType("lockedchestBottom") || pt.isType("mainchestBottom")) {
      chestBlockState = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.EAST);
    } else if (pt.isType("lockedchestLeft") || pt.isType("mainchestLeft")) {
      chestBlockState = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.SOUTH);
    } else if (pt.isType("lockedchestRight") || pt.isType("mainchestRight")) {
      chestBlockState = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)EnumFacing.NORTH);
    } 
    return new BuildingBlock(p, chestBlockState);
  }
  
  private void getBuildingPoints_mockBlocks(boolean includeSpecialPoints, List<BuildingBlock> bblocks, PointType pt, Point p) {
    boolean mockBlockFound = (pt.specialType.equalsIgnoreCase("grass") || pt.specialType.equalsIgnoreCase("empty"));
    for (MockBlockMarker.Type type : MockBlockMarker.Type.values()) {
      if (type.name.equalsIgnoreCase(pt.specialType)) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.MARKER_BLOCK, type.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    for (MockBlockAnimalSpawn.Creature type : MockBlockAnimalSpawn.Creature.values()) {
      if (pt.specialType.equalsIgnoreCase(type.name + "spawn")) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.ANIMAL_SPAWN, type.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    for (MockBlockSource.Resource resource : MockBlockSource.Resource.values()) {
      if (pt.specialType.equalsIgnoreCase(resource.name + "source")) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.SOURCE, resource.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    for (MockBlockFree.Resource resource : MockBlockFree.Resource.values()) {
      if (pt.specialType.equalsIgnoreCase("free" + resource.name)) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.FREE_BLOCK, resource.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    for (MockBlockTreeSpawn.TreeType treeType : MockBlockTreeSpawn.TreeType.values()) {
      if (pt.specialType.equalsIgnoreCase(treeType.name + "spawn")) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.TREE_SPAWN, treeType.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    for (MockBlockSoil.CropType cropType : MockBlockSoil.CropType.values()) {
      if (pt.specialType.equalsIgnoreCase(cropType.name)) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.SOIL_BLOCK, cropType.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    for (MockBlockDecor.Type decorType : MockBlockDecor.Type.values()) {
      if (pt.specialType.equalsIgnoreCase(decorType.name)) {
        bblocks.add(new BuildingBlock(p, (Block)MillBlocks.DECOR_BLOCK, decorType.meta));
        mockBlockFound = true;
        break;
      } 
    } 
    if (!mockBlockFound) {
      String s = LanguageUtilities.string("import.missingmockblock", new String[] { pt.specialType });
      System.out.println("Missing placelast mock block " + pt.specialType);
      Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), 'c', s);
    } 
  }
  
  private void getBuildingPoints_prepareGround(BuildingLocation location, int locationX, int locationY, int locationZ, int orientation, List<BuildingBlock> bblocks) {
    short[] XZCoords = getBuildingPoints_computeXZCoords((short)(this.length + this.areaToClearLengthBefore + this.areaToClearLengthAfter), (short)(this.width + this.areaToClearWidthBefore + this.areaToClearWidthAfter));
    int xzPos;
    for (xzPos = 0; xzPos < XZCoords.length; xzPos += 2) {
      short deltaX = (short)(XZCoords[xzPos] - this.areaToClearLengthBefore);
      short deltaZ = (short)(XZCoords[xzPos + 1] - this.areaToClearWidthBefore);
      short deltaY;
      for (deltaY = (short)(this.nbfloors + 50); deltaY > -1; deltaY = (short)(deltaY - 1)) {
        int offsetX = 0;
        if (deltaX < 0) {
          offsetX = -deltaX;
        } else if (deltaX >= this.length - 1) {
          offsetX = deltaX - this.length + 1;
        } 
        int offsetZ = 0;
        if (deltaZ < 0) {
          offsetZ = -deltaZ;
        } else if (deltaZ >= this.width - 1) {
          offsetZ = deltaZ - this.width + 1;
        } 
        int offset = Math.max(offsetX, offsetZ);
        if (Math.abs(offsetX - offsetZ) < 3)
          offset++; 
        offset--;
        if (deltaY >= offset - 2) {
          Point p = adjustForOrientation(locationX, locationY + deltaY, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
          if (deltaX < 0 || deltaZ < 0 || deltaX > this.length || deltaZ > this.width) {
            if (deltaY == offset - 2 || deltaY == 0) {
              bblocks.add(new BuildingBlock(p, BuildingBlock.CLEARGROUNDBORDER));
            } else if (deltaX == -this.areaToClearLengthBefore || deltaZ == -this.areaToClearWidthBefore || deltaX == this.length + this.areaToClearLengthAfter - 1 || deltaZ == this.width + this.areaToClearWidthAfter - 1) {
              bblocks.add(new BuildingBlock(p, BuildingBlock.CLEARGROUNDBORDER));
            } else {
              bblocks.add(new BuildingBlock(p, BuildingBlock.CLEARGROUNDOUTSIDEBUILDING));
            } 
          } else {
            bblocks.add(new BuildingBlock(p, BuildingBlock.CLEARGROUND));
          } 
        } else {
          Point p = adjustForOrientation(locationX, locationY + deltaY, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
          bblocks.add(new BuildingBlock(p, BuildingBlock.CLEARTREE));
        } 
      } 
    } 
    for (xzPos = 0; xzPos < XZCoords.length; xzPos += 2) {
      short deltaX = (short)(XZCoords[xzPos] - this.areaToClearLengthBefore);
      short deltaZ = (short)(XZCoords[xzPos + 1] - this.areaToClearWidthAfter);
      short deltaY;
      for (deltaY = (short)(-this.foundationDepth + this.startLevel); deltaY < 0; deltaY = (short)(deltaY + 1)) {
        int offsetX = 0;
        if (deltaX < 0) {
          offsetX = -deltaX;
        } else if (deltaX >= this.length - 1) {
          offsetX = deltaX - this.length + 1;
        } 
        int offsetZ = 0;
        if (deltaZ < 0) {
          offsetZ = -deltaZ;
        } else if (deltaZ >= this.width - 1) {
          offsetZ = deltaZ - this.width + 1;
        } 
        int offset = Math.max(offsetX, offsetZ);
        if (Math.abs(offsetX - offsetZ) < 3)
          offset++; 
        offset--;
        if (-deltaY > offset) {
          Point p = adjustForOrientation(locationX, locationY + deltaY, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
          bblocks.add(new BuildingBlock(p, BuildingBlock.PRESERVEGROUNDDEPTH));
        } else if (-deltaY >= offset - 1) {
          Point p = adjustForOrientation(locationX, locationY + deltaY, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
          bblocks.add(new BuildingBlock(p, BuildingBlock.PRESERVEGROUNDSURFACE));
        } else {
          Point p = adjustForOrientation(locationX, locationY + deltaY, locationZ, deltaX - this.lengthOffset, deltaZ - this.widthOffset, orientation);
          bblocks.add(new BuildingBlock(p, BuildingBlock.CLEARTREE));
        } 
      } 
    } 
  }
  
  private void getBuildingPoints_specialBlocks(List<BuildingBlock> bblocks, PointType pt, Point p) {
    if (pt.isType("lockedchestGuess")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.CHESTGUESS));
    } else if (pt.isType("torchGuess")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.TORCHGUESS));
    } else if (pt.isType("furnaceGuess")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.FURNACEGUESS));
    } else if (pt.isType("tapestry")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.TAPESTRY));
    } else if (pt.isType("indianstatue")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.INDIANSTATUE));
    } else if (pt.isType("mayanstatue")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.MAYANSTATUE));
    } else if (pt.isType("byzantineiconsmall")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.BYZANTINEICONSMALL));
    } else if (pt.isType("byzantineiconmedium")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.BYZANTINEICONMEDIUM));
    } else if (pt.isType("byzantineiconlarge")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.BYZANTINEICONLARGE));
    } else if (pt.isType("hidehanging")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.HIDEHANGING));
    } else if (pt.isType("wallcarpetsmall")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.WALLCARPETSMALL));
    } else if (pt.isType("wallcarpetmedium")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.WALLCARPETMEDIUM));
    } else if (pt.isType("wallcarpetlarge")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.WALLCARPETLARGE));
    } else if (pt.isType("oakspawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.OAKSPAWN));
    } else if (pt.isType("pinespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.PINESPAWN));
    } else if (pt.isType("birchspawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.BIRCHSPAWN));
    } else if (pt.isType("junglespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.JUNGLESPAWN));
    } else if (pt.isType("acaciaspawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.ACACIASPAWN));
    } else if (pt.isType("darkoakspawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.DARKOAKSPAWN));
    } else if (pt.isType("appletreespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.APPLETREESPAWN));
    } else if (pt.isType("olivetreespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.OLIVETREESPAWN));
    } else if (pt.isType("pistachiotreespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.PISTACHIOTREESPAWN));
    } else if (pt.isType("cherrytreespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.CHERRYTREESPAWN));
    } else if (pt.isType("sakuratreespawn")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SAKURATREESPAWN));
    } else if (pt.isType("spawnerskeleton")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SPAWNERSKELETON));
    } else if (pt.isType("spawnerzombie")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SPAWNERZOMBIE));
    } else if (pt.isType("spawnerspider")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SPAWNERSPIDER));
    } else if (pt.isType("spawnercavespider")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SPAWNERCAVESPIDER));
    } else if (pt.isType("spawnercreeper")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SPAWNERCREEPER));
    } else if (pt.isType("spawnerblaze")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.SPAWNERBLAZE));
    } else if (pt.isType("dispenserunknownpowder")) {
      bblocks.add(new BuildingBlock(p, BuildingBlock.DISPENDERUNKNOWNPOWDER));
    } 
  }
  
  public int getChoiceWeight(EntityPlayer player) {
    return this.weight;
  }
  
  public Culture getCulture() {
    return this.culture;
  }
  
  public List<String> getFemaleResident() {
    return this.femaleResident;
  }
  
  public String getGameNameKey() {
    return "_buildingGame:" + this.culture.key + ":" + this.buildingKey + ":" + this.variation + ":" + this.level;
  }
  
  public ItemStack getIcon() {
    if (this.icon == null)
      return ItemStack.EMPTY; 
    return this.icon.getItemStack();
  }
  
  public File getLoadedFromFile() {
    return this.loadedFromFile;
  }
  
  public List<String> getMaleResident() {
    return this.maleResident;
  }
  
  public String getNameNative() {
    return this.nativeName;
  }
  
  public String getNameNativeAndTranslated() {
    String name = this.nativeName;
    if (getNameTranslated() != null && getNameTranslated().length() > 0)
      name = name + " (" + getNameTranslated() + ")"; 
    return name;
  }
  
  public String getNameTranslated() {
    if (this.culture.canReadBuildingNames())
      return this.culture.getBuildingGameName(this); 
    return "";
  }
  
  public String getNativeName() {
    return this.nativeName;
  }
  
  public BuildingPlanSet getPlanSet() {
    return this.culture.getBuildingPlanSet(this.buildingKey);
  }
  
  public BuildingPlan getPreviousBuildingPlan() {
    if (this.level == 0)
      return null; 
    return ((BuildingPlan[])(this.culture.getBuildingPlanSet(this.buildingKey)).plans.get(this.variation))[this.level - 1];
  }
  
  public List<String> getVisitors() {
    return this.visitors;
  }
  
  private EnumFacing guessWallOrientation(List<BuildingBlock> bblocks, Point p, boolean allowStanding) {
    Point below = p.getRelative(0.0D, -1.0D, 0.0D);
    if (allowStanding)
      for (BuildingBlock block : bblocks) {
        if (block.p.sameBlock(below) && isBlockOpaqueCube(block.block))
          return EnumFacing.UP; 
      }  
    boolean westOpen = true, eastOpen = true, northOpen = true, southOpen = true;
    Point west = p.getWest();
    Point east = p.getEast();
    Point south = p.getSouth();
    Point north = p.getNorth();
    for (BuildingBlock block : bblocks) {
      if (block.p.sameBlock(west) && isBlockOpaqueCube(block.block)) {
        westOpen = false;
        continue;
      } 
      if (block.p.sameBlock(east) && isBlockOpaqueCube(block.block)) {
        eastOpen = false;
        continue;
      } 
      if (block.p.sameBlock(south) && isBlockOpaqueCube(block.block)) {
        southOpen = false;
        continue;
      } 
      if (block.p.sameBlock(north) && isBlockOpaqueCube(block.block))
        northOpen = false; 
    } 
    if (!eastOpen)
      return EnumFacing.WEST; 
    if (!westOpen)
      return EnumFacing.EAST; 
    if (!southOpen)
      return EnumFacing.NORTH; 
    if (!northOpen)
      return EnumFacing.SOUTH; 
    return EnumFacing.NORTH;
  }
  
  public boolean isBlockOpaqueCube(Block block) {
    return BlockItemUtilities.isBlockOpaqueCube(block);
  }
  
  public boolean mapIsOpaqueBlock(Map<Point, BuildingBlock> map, Point p) {
    return (map.containsKey(p) && isBlockOpaqueCube(((BuildingBlock)map.get(p)).block));
  }
  
  private void referenceBuildingPoints(Building building) {
    int x = building.location.pos.getiX();
    int y = building.location.pos.getiY();
    int z = building.location.pos.getiZ();
    int orientation = building.location.orientation;
    int signNb = 0;
    if (building.containsTags("archives"))
      signNb = (building.getResManager()).signs.size(); 
    int i;
    for (i = (building.getResManager()).signs.size(); i < this.signOrder.length; i++)
      (building.getResManager()).signs.add(null); 
    for (i = 0; i < this.nbfloors; i++) {
      for (int j = 0; j < this.length; j++) {
        for (int k = 0; k < this.width; k++) {
          PointType pt = this.plan[i][j][k];
          Point p = adjustForOrientation(x, y + i + this.startLevel, z, j - this.lengthOffset, k - this.widthOffset, orientation);
          if (pt.isType("soil")) {
            building.getResManager().addSoilPoint(Mill.CROP_WHEAT, p);
          } else if (pt.isType("ricesoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_RICE, p);
          } else if (pt.isType("turmericsoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_TURMERIC, p);
          } else if (pt.isType("maizesoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_MAIZE, p);
          } else if (pt.isType("carrotsoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_CARROT, p);
          } else if (pt.isType("potatosoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_POTATO, p);
          } else if (pt.isType("flowersoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_FLOWER, p);
          } else if (pt.isType("sugarcanesoil")) {
            if (!(building.getResManager()).sugarcanesoils.contains(p))
              (building.getResManager()).sugarcanesoils.add(p); 
          } else if (pt.isType("netherwartsoil")) {
            if (!(building.getResManager()).netherwartsoils.contains(p))
              (building.getResManager()).netherwartsoils.add(p); 
          } else if (pt.isType("vinesoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_VINE, p);
          } else if (pt.isType("cacaospot")) {
            building.getResManager().addSoilPoint(Mill.CROP_CACAO, p);
          } else if (pt.isType("cottonsoil")) {
            building.getResManager().addSoilPoint(Mill.CROP_COTTON, p);
          } else if (pt.isType("silkwormblock")) {
            if (!(building.getResManager()).silkwormblock.contains(p))
              (building.getResManager()).silkwormblock.add(p); 
          } else if (pt.isType("snailsoilblock")) {
            if (!(building.getResManager()).snailsoilblock.contains(p))
              (building.getResManager()).snailsoilblock.add(p); 
          } else if (pt.isType("stall")) {
            if (!(building.getResManager()).stalls.contains(p))
              (building.getResManager()).stalls.add(p); 
          } else if (pt.isType("oakspawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("pinespawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("birchspawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("junglespawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("acaciaspawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("darkoakspawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("appletreespawn") || pt.isType("olivetreespawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("pistachiotreespawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("cherrytreespawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("sakuratreespawn")) {
            if (!(building.getResManager()).woodspawn.contains(p)) {
              (building.getResManager()).woodspawn.add(p);
              (building.getResManager()).woodspawnTypes.put(p, pt.getSpecialType());
            } 
          } else if (pt.isType("brickspot")) {
            if (!(building.getResManager()).brickspot.contains(p))
              (building.getResManager()).brickspot.add(p); 
          } else if (pt.isType("chickenspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_CHICKEN, p);
          } else if (pt.isType("cowspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_COW, p);
          } else if (pt.isType("pigspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_PIG, p);
          } else if (pt.isType("squidspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_SQUID, p);
          } else if (pt.isType("sheepspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_SHEEP, p);
          } else if (pt.isType("wolfspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_WOLF, p);
          } else if (pt.isType("polarbearspawn")) {
            building.getResManager().addSpawnPoint(Mill.ENTITY_POLAR_BEAR, p);
          } else if (pt.isType("stonesource")) {
            building.getResManager().addSourcePoint(Blocks.STONE.getDefaultState(), p);
          } else if (pt.isType("sandsource")) {
            building.getResManager().addSourcePoint(Blocks.SAND.getDefaultState(), p);
          } else if (pt.isType("sandstonesource")) {
            building.getResManager().addSourcePoint(Blocks.SANDSTONE.getDefaultState(), p);
          } else if (pt.isType("claysource")) {
            building.getResManager().addSourcePoint(Blocks.CLAY.getDefaultState(), p);
          } else if (pt.isType("gravelsource")) {
            building.getResManager().addSourcePoint(Blocks.GRAVEL.getDefaultState(), p);
          } else if (pt.isType("granitesource")) {
            building.getResManager().addSourcePoint(Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.GRANITE), p);
          } else if (pt.isType("dioritesource")) {
            building.getResManager().addSourcePoint(Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.DIORITE), p);
          } else if (pt.isType("andesitesource")) {
            building.getResManager().addSourcePoint(Blocks.STONE.getDefaultState().withProperty((IProperty)BlockStone.VARIANT, (Comparable)BlockStone.EnumType.ANDESITE), p);
          } else if (pt.isType("redsandstonesource")) {
            building.getResManager().addSourcePoint(Blocks.RED_SANDSTONE.getDefaultState(), p);
          } else if (pt.isType("quartzsource")) {
            building.getResManager().addSourcePoint(Blocks.QUARTZ_ORE.getDefaultState(), p);
          } else if (pt.isType("snowsource")) {
            building.getResManager().addSourcePoint(Blocks.SNOW_LAYER.getDefaultState(), p);
          } else if (pt.isType("icesource")) {
            building.getResManager().addSourcePoint(Blocks.ICE.getDefaultState(), p);
          } else if (pt.isType("spawnerskeleton")) {
            building.getResManager().addMobSpawnerPoint(Mill.ENTITY_SKELETON, p);
          } else if (pt.isType("spawnerzombie")) {
            building.getResManager().addMobSpawnerPoint(Mill.ENTITY_ZOMBIE, p);
          } else if (pt.isType("spawnerspider")) {
            building.getResManager().addMobSpawnerPoint(Mill.ENTITY_SPIDER, p);
          } else if (pt.isType("spawnercavespider")) {
            building.getResManager().addMobSpawnerPoint(Mill.ENTITY_CAVESPIDER, p);
          } else if (pt.isType("spawnercreeper")) {
            building.getResManager().addMobSpawnerPoint(Mill.ENTITY_CREEPER, p);
          } else if (pt.isType("dispenserunknownpowder")) {
            if (!(building.getResManager()).dispenderUnknownPowder.contains(p))
              (building.getResManager()).dispenderUnknownPowder.add(p); 
          } else if (pt.isType("fishingspot")) {
            if (!(building.getResManager()).fishingspots.contains(p))
              (building.getResManager()).fishingspots.add(p); 
          } else if (pt.isType("healingspot")) {
            if (!(building.getResManager()).healingspots.contains(p))
              (building.getResManager()).healingspots.add(p); 
          } else if (pt.isSubType("lockedchest") || pt.isSubType("mainchest")) {
            if (!(building.getResManager()).chests.contains(p))
              (building.getResManager()).chests.add(p); 
          } else if (pt.isType("furnaceGuess") || pt.getBlock() == Blocks.FURNACE) {
            if (!(building.getResManager()).furnaces.contains(p))
              (building.getResManager()).furnaces.add(p); 
          } else if (pt.getBlock() == MillBlocks.FIRE_PIT) {
            if (!(building.getResManager()).firepits.contains(p))
              (building.getResManager()).firepits.add(p); 
          } else if (pt.isType("brewingstand")) {
            if (!(building.getResManager()).brewingStands.contains(p))
              (building.getResManager()).brewingStands.add(p); 
          } else if (pt.isSubType("villageBanner")) {
            if (!(building.getResManager()).banners.contains(p))
              (building.getResManager()).banners.add(p); 
          } else if (pt.isSubType("cultureBanner")) {
            if (!(building.getResManager()).cultureBanners.contains(p))
              (building.getResManager()).cultureBanners.add(p); 
          } else if (pt.getBlock() instanceof BlockBanner) {
            if (!(building.getResManager()).banners.contains(p))
              (building.getResManager()).banners.add(p); 
          } else if (pt.isType("sleepingPos")) {
            building.getResManager().setSleepingPos(p);
          } else if (pt.isType("sellingPos")) {
            building.getResManager().setSellingPos(p);
          } else if (pt.isType("craftingPos")) {
            building.getResManager().setCraftingPos(p);
          } else if (pt.isType("defendingPos")) {
            building.getResManager().setDefendingPos(p);
          } else if (pt.isType("shelterPos")) {
            building.getResManager().setShelterPos(p);
          } else if (pt.isType("pathStartPos")) {
            building.getResManager().setPathStartPos(p);
          } else if (pt.isType("leisurePos")) {
            building.getResManager().setLeasurePos(p);
          } else if (pt.isType("signwallGuess") || pt.getBlock() == MillBlocks.PANEL) {
            if (signNb < this.signOrder.length) {
              if (this.signOrder[signNb] < (building.getResManager()).signs.size()) {
                (building.getResManager()).signs.set(this.signOrder[signNb], p);
              } else {
                MillLog.warning(this, "Building has a sign order of " + this.signOrder[signNb] + " signs but only " + (building.getResManager()).signs.size() + " signs.");
              } 
            } else {
              MillLog.warning(this, "Building has at least " + signNb + " signs but only " + this.signOrder.length + " sign orders.");
            } 
            signNb++;
          } 
        } 
      } 
    } 
  }
  
  public void setLoadedFromFile(File loadedFromFile) {
    this.loadedFromFile = loadedFromFile;
  }
  
  private void setSnow(World world, BuildingLocation location, boolean[][] snow) {
    IBlockState snowLayer = Blocks.SNOW_LAYER.getDefaultState();
    int x = location.pos.getiX();
    int z = location.pos.getiZ();
    int orientation = location.orientation;
    for (int dx = -this.areaToClearLengthBefore; dx < this.length + this.areaToClearLengthAfter; dx++) {
      for (int dz = -this.areaToClearWidthBefore; dz < this.width + this.areaToClearWidthAfter; dz++) {
        if (snow[dx + this.areaToClearLengthBefore][dz + this.areaToClearWidthBefore]) {
          Point p = adjustForOrientation(x, 256, z, dx - this.lengthOffset, dz - this.widthOffset, orientation);
          boolean stop = false;
          while (!stop && p.y > 0.0D) {
            if (p.getBlockActualState(world).isFullBlock() && p.getAbove().getBlock(world) == Blocks.AIR) {
              p.getAbove().setBlockState(world, snowLayer);
              stop = true;
            } else if (p.getBlock(world) != Blocks.AIR) {
              stop = true;
            } 
            p = p.getBelow();
          } 
        } 
      } 
    } 
  }
  
  public LocationReturn testSpot(VillageMapInfo winfo, RegionMapper regionMapper, Point centre, int x, int z, Random random, int porientation, boolean ignoreExtraConstraints) {
    int xwidth, zwidth;
    Point testPosHorizontal = new Point((x + winfo.mapStartX), 64.0D, (z + winfo.mapStartZ));
    if (x < 0 || winfo.length <= x)
      return new LocationReturn(1, testPosHorizontal); 
    if (z < 0 || winfo.width <= z)
      return new LocationReturn(1, testPosHorizontal); 
    winfo.buildTested[x][z] = true;
    if (MillConfigValues.LogWorldGeneration >= 3)
      MillLog.debug(this, "Testing: " + x + "/" + z); 
    if (!ignoreExtraConstraints)
      for (String tag : this.farFromTag.keySet()) {
        for (BuildingLocation location : winfo.getBuildingLocations()) {
          if (location.containsPlanTag(tag) && location.pos.horizontalDistanceTo(testPosHorizontal) < ((Integer)this.farFromTag.get(tag)).intValue())
            return new LocationReturn(7, testPosHorizontal); 
        } 
      }  
    if (!ignoreExtraConstraints)
      for (String tag : this.closeToTag.keySet()) {
        boolean foundNearbyBuilding = false;
        for (BuildingLocation location : winfo.getBuildingLocations()) {
          if (location.containsPlanTag(tag) && location.pos.horizontalDistanceTo(testPosHorizontal) < ((Integer)this.closeToTag.get(tag)).intValue())
            foundNearbyBuilding = true; 
        } 
        if (!foundNearbyBuilding)
          return new LocationReturn(8, testPosHorizontal); 
      }  
    int orientation;
    if (porientation == -1) {
      orientation = computeOrientation(new Point((x + winfo.mapStartX), 0.0D, (z + winfo.mapStartZ)), centre);
    } else {
      orientation = porientation;
    } 
    orientation = (orientation + this.buildingOrientation) % 4;
    if (orientation == 0 || orientation == 2) {
      xwidth = this.length + this.areaToClearLengthBefore + this.areaToClearLengthAfter + 2;
      zwidth = this.width + this.areaToClearWidthBefore + this.areaToClearWidthAfter + 2;
    } else {
      xwidth = this.width + this.areaToClearWidthBefore + this.areaToClearWidthAfter + 2;
      zwidth = this.length + this.areaToClearLengthBefore + this.areaToClearLengthAfter + 2;
    } 
    int altitudeTotal = 0;
    int nbPoints = 0;
    int nbError = 0;
    int allowedErrors = 10;
    boolean hugeBuilding = false;
    if (xwidth * zwidth > 2000) {
      allowedErrors = xwidth * zwidth / 10;
      hugeBuilding = true;
    } else if (xwidth * zwidth > 200) {
      allowedErrors = xwidth * zwidth / 20;
    } 
    boolean reachable = false;
    for (int i = 0; i <= (int)Math.floor((xwidth / 2)); i++) {
      for (int j = 0; j <= (int)Math.floor((zwidth / 2)); j++) {
        for (int k = 0; k < 4; k++) {
          int ci, cj;
          if (k == 0) {
            ci = x + i;
            cj = z + j;
          } else if (k == 1) {
            ci = x - i;
            cj = z + j;
          } else if (k == 2) {
            ci = x - i;
            cj = z - j;
          } else {
            ci = x + i;
            cj = z - j;
          } 
          if (ci < 0 || cj < 0 || ci >= winfo.length || cj >= winfo.width) {
            Point p = new Point((ci + winfo.mapStartX), 64.0D, (cj + winfo.mapStartZ));
            return new LocationReturn(1, p);
          } 
          if (winfo.buildingLocRef[ci][cj] != null) {
            Point p = new Point((ci + winfo.mapStartX), 64.0D, (cj + winfo.mapStartZ));
            return new LocationReturn(2, p);
          } 
          if (winfo.buildingForbidden[ci][cj]) {
            if (!hugeBuilding || nbError > allowedErrors) {
              Point p = new Point((ci + winfo.mapStartX), 64.0D, (cj + winfo.mapStartZ));
              return new LocationReturn(3, p);
            } 
            nbError++;
          } else if (winfo.danger[ci][cj]) {
            if (nbError > allowedErrors) {
              Point p = new Point((ci + winfo.mapStartX), 64.0D, (cj + winfo.mapStartZ));
              return new LocationReturn(5, p);
            } 
            nbError++;
          } else if (!winfo.canBuild[ci][cj]) {
            if (nbError > allowedErrors) {
              Point p = new Point((ci + winfo.mapStartX), 64.0D, (cj + winfo.mapStartZ));
              return new LocationReturn(4, p);
            } 
            nbError++;
          } 
          if (regionMapper != null)
            if (ci < regionMapper.regions.length && cj < (regionMapper.regions[0]).length) {
              if (regionMapper.regions[ci][cj] != regionMapper.thRegion) {
                reachable = false;
              } else {
                reachable = true;
              } 
            } else {
              MillLog.error(null, "Out of array value for regions");
            }  
          altitudeTotal += winfo.topGround[ci][cj];
          nbPoints++;
        } 
      } 
    } 
    if (!ignoreExtraConstraints && 
      regionMapper != null && !reachable)
      return new LocationReturn(6, centre); 
    int altitude = Math.round(altitudeTotal * 1.0F / nbPoints);
    altitude += this.altitudeOffset;
    BuildingLocation l = new BuildingLocation(this, new Point((x + winfo.mapStartX), altitude, (z + winfo.mapStartZ)), orientation);
    return new LocationReturn(l);
  }
  
  public LocationReturn testSpotBedrock(World world, int cx, int cz) {
    for (int x = cx - this.width - 2; x < cx + this.width + 2; x++) {
      for (int z = cz - this.length - 2; z < cz + this.length + 2; z++) {
        for (int y = 0; y < this.plan.length + 2; y++) {
          Block block = WorldUtilities.getBlock(world, x, y, z);
          if (block != Blocks.BEDROCK && block != Blocks.STONE && block != Blocks.DIRT && block != Blocks.GRAVEL && block != Blocks.COAL_ORE && block != Blocks.DIAMOND_ORE && block != Blocks.GOLD_ORE && block != Blocks.IRON_ORE && block != Blocks.LAPIS_ORE && block != Blocks.REDSTONE_ORE)
            return new LocationReturn(3, null); 
        } 
      } 
    } 
    BuildingLocation l = new BuildingLocation(this, new Point(cx, 2.0D, cz), 0);
    l.bedrocklevel = true;
    return new LocationReturn(l);
  }
  
  public String toString() {
    if (this.culture != null)
      return this.culture.key + ":" + this.planName; 
    return "null culture:" + this.planName;
  }
  
  public void updateBuildingForPlan(Building building) {
    referenceBuildingPoints(building);
    updateTags(building);
  }
  
  public void updateTags(Building building) {
    if (!this.tags.isEmpty()) {
      building.addTags(this.tags, this.buildingKey + ": registering new tags");
      if (MillConfigValues.LogTags >= 2)
        MillLog.minor(this, "Applying tags: " + (String)this.tags.stream().collect(Collectors.joining(", ")) + ", result: " + (String)building.getTags().stream().collect(Collectors.joining(", "))); 
    } 
    if (!this.clearTags.isEmpty()) {
      building.clearTags(this.clearTags, this.buildingKey + ": clearing tags");
      if (MillConfigValues.LogTags >= 2)
        MillLog.minor(this, "Clearing tags: " + (String)this.clearTags.stream().collect(Collectors.joining(", ")) + ", result: " + (String)building.getTags().stream().collect(Collectors.joining(", "))); 
    } 
    if (!this.parentTags.isEmpty()) {
      building.getParentBuilding().addTags(this.parentTags, this.buildingKey + ": registering new parent tags");
      if (MillConfigValues.LogTags >= 2)
        MillLog.minor(this, "Applying parent tags: " + (String)this.parentTags.stream().collect(Collectors.joining(", ")) + ", result: " + (String)building
            .getParentBuilding().getTags().stream().collect(Collectors.joining(", "))); 
    } 
    if (!this.villageTags.isEmpty()) {
      building.getTownHall().addTags(this.villageTags, this.buildingKey + ": registering new village tags");
      if (MillConfigValues.LogTags >= 2)
        MillLog.minor(this, "Applying village tags: " + (String)this.villageTags.stream().collect(Collectors.joining(", ")) + ", result: " + (String)building
            .getTownHall().getTags().stream().collect(Collectors.joining(", "))); 
    } 
  }
}
