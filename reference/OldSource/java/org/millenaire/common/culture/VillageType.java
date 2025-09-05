package org.millenaire.common.culture;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class VillageType implements MillCommonUtilities.WeightedChoice {
  private static final String VILLAGE_TYPE_HAMEAU = "hameau";
  
  private static final String VILLAGE_TYPE_MARVEL = "marvel";
  
  private static final float MINIMUM_VALID_BIOME_PERC = 0.6F;
  
  public static class BrickColourTheme implements MillCommonUtilities.WeightedChoice {
    public final String key;
    
    public final int weight;
    
    public final Map<EnumDyeColor, Map<EnumDyeColor, Integer>> colours;
    
    public BrickColourTheme(String key, int weight, Map<EnumDyeColor, Map<EnumDyeColor, Integer>> colours) {
      this.key = key;
      this.weight = weight;
      this.colours = colours;
    }
    
    public int getChoiceWeight(EntityPlayer player) {
      return this.weight;
    }
    
    public EnumDyeColor getRandomDyeColour(EnumDyeColor colour) {
      int totalWeight = 0;
      Map<EnumDyeColor, Integer> colourMap = this.colours.get(colour);
      for (EnumDyeColor possibleColor : colourMap.keySet())
        totalWeight += ((Integer)colourMap.get(possibleColor)).intValue(); 
      int pickedValue = MillCommonUtilities.randomInt(totalWeight);
      int currentWeightTotal = 0;
      for (EnumDyeColor possibleColor : colourMap.keySet()) {
        currentWeightTotal += ((Integer)colourMap.get(possibleColor)).intValue();
        if (pickedValue < currentWeightTotal)
          return possibleColor; 
      } 
      return EnumDyeColor.WHITE;
    }
    
    public String toString() {
      return "theme: " + this.key;
    }
  }
  
  public static List<VillageType> loadLoneBuildings(VirtualDir cultureVirtualDir, Culture culture) {
    VirtualDir lonebuildingsVirtualDir = cultureVirtualDir.getChildDirectory("lonebuildings");
    List<VillageType> v = new ArrayList<>();
    for (File file : lonebuildingsVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"))) {
      try {
        if (MillConfigValues.LogVillage >= 1)
          MillLog.major(file, "Loading lone building: " + file.getAbsolutePath()); 
        VillageType village = loadVillageType(file, culture, true);
        v.remove(village);
        v.add(village);
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
    return v;
  }
  
  public static List<VillageType> loadVillages(VirtualDir cultureVirtualDir, Culture culture) {
    VirtualDir villagesVirtualDir = cultureVirtualDir.getChildDirectory("villages");
    List<VillageType> villages = new ArrayList<>();
    for (File file : villagesVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"))) {
      try {
        if (MillConfigValues.LogVillage >= 1)
          MillLog.major(file, "Loading village: " + file.getAbsolutePath()); 
        VillageType village = loadVillageType(file, culture, false);
        villages.remove(village);
        villages.add(village);
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
    return villages;
  }
  
  public static VillageType loadVillageType(File file, Culture c, boolean lonebuilding) {
    VillageType villageType = new VillageType(c, file.getName().split("\\.")[0], lonebuilding);
    try {
      ParametersManager.loadAnnotedParameterData(file, villageType, null, "village type", c);
      if (villageType.name == null)
        throw new MillLog.MillenaireException("No name found for village: " + villageType.key); 
      if (villageType.centreBuilding == null && villageType.customCentre == null)
        throw new MillLog.MillenaireException("No central building found for village: " + villageType.key); 
      if (!villageType.playerControlled && !"hameau".equalsIgnoreCase(villageType.specialType) && !"marvel".equalsIgnoreCase(villageType.specialType) && !villageType.lonebuilding)
        for (BuildingPlanSet set : villageType.culture.ListPlanSets) {
          if (!villageType.excludedBuildings.contains(set)) {
            int nb = 0;
            for (BuildingPlanSet aset : villageType.startBuildings) {
              if (aset == set)
                nb++; 
            } 
            for (BuildingPlanSet aset : villageType.coreBuildings) {
              if (aset == set)
                nb++; 
            } 
            for (BuildingPlanSet aset : villageType.secondaryBuildings) {
              if (aset == set)
                nb++; 
            } 
            for (int i = nb; i < set.max; i++)
              villageType.extraBuildings.add(set); 
          } 
        }  
      if (villageType.pathMaterial.size() == 0)
        villageType.pathMaterial.add((InvItem)InvItem.INVITEMS_BY_NAME.get("pathgravel")); 
      if (MillConfigValues.LogVillage >= 1)
        MillLog.major(villageType, "Loaded village type " + villageType.name + ". NameList: " + villageType.nameList); 
      return villageType;
    } catch (Exception e) {
      MillLog.printException(e);
      return null;
    } 
  }
  
  public static List<VillageType> spawnableVillages(EntityPlayer player) {
    List<VillageType> villages = new ArrayList<>();
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    for (Culture culture : Culture.ListCultures) {
      for (VillageType village : culture.listVillageTypes) {
        if (village.spawnable && village.playerControlled && (MillConfigValues.DEV || profile.isTagSet("culturecontrol_" + village.culture.key)))
          villages.add(village); 
      } 
      for (VillageType village : culture.listVillageTypes) {
        if (village.spawnable && !village.playerControlled)
          villages.add(village); 
      } 
      for (VillageType village : culture.listLoneBuildingTypes) {
        if (village.spawnable && (MillConfigValues.DEV || !village.playerControlled || profile.isTagSet("culturecontrol_" + village.culture.key)))
          villages.add(village); 
      } 
    } 
    return villages;
  }
  
  public String key = null;
  
  public Culture culture;
  
  public boolean lonebuilding = false;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Name of the villager in the culture's language.")
  public String name = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Name of a good whose icon represents this village.")
  private final InvItem icon = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "banner_basecolor")
  @FieldDocumentation(explanation = "A color the village's banner can have as its base color.")
  public List<String> banner_baseColors = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "banner_patterncolor")
  @FieldDocumentation(explanation = "A color the village's banner can have as its pattern color.")
  public List<String> banner_patternsColors = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "banner_chargecolor")
  @FieldDocumentation(explanation = "A color the village's banner can have as its charge color.")
  public List<String> banner_chargeColors = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "banner_pattern")
  @FieldDocumentation(explanation = "A pattern for the banner. Uses one of the patterncolors.")
  public List<String> banner_Patterns = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "banner_chargepattern")
  @FieldDocumentation(explanation = "A charge pattern for the banner. Uses one of the chargecolors.")
  public List<String> banner_chargePatterns = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "banner_json")
  @FieldDocumentation(explanation = "A JSON object that specifies the banner's appearance. Used instead of the patterns and colors entries.")
  public List<String> banner_JSONs = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "travelbook_display", defaultValue = "true")
  @FieldDocumentation(explanation = "Whether to display this villager type in the Travel Book.")
  public boolean travelBookDisplay = true;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "Generation weight. The higher it is, the more chance that this village type will be picked.", explanationCategory = "World Generation")
  public int weight;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "biome")
  @FieldDocumentation(explanation = "A biome the village can spawn in.", explanationCategory = "World Generation")
  public List<String> biomes = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "-1")
  @FieldDocumentation(explanation = "Maximum number of this village type that can be generated in a given world. -1 for no limits.", explanationCategory = "World Generation")
  public int max;
  
  @ConfigField(type = AnnotedParameter.ParameterType.FLOAT, defaultValue = "0.6")
  @FieldDocumentation(explanation = "% of village that must in the appropriate biome.", explanationCategory = "World Generation")
  private float minimumBiomeValidity;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "true")
  @FieldDocumentation(explanation = "Whether this village type can be generated on an MP server.", explanationCategory = "World Generation")
  public boolean generateOnServer;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "generateforplayer", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether this village type is generated for a specific player and will be listed only for him (used for 'hidden' quest buildings).", explanationCategory = "World Generation")
  public boolean generatedForPlayer;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "-1")
  @FieldDocumentation(explanation = "Minimum distance from spawn point at which this village can appear. -1 for no limits.", explanationCategory = "World Generation")
  public int minDistanceFromSpawn;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "requiredtag")
  @FieldDocumentation(explanation = "A global tag that has to be set for this village type to generate.", explanationCategory = "World Generation")
  List<String> requiredTags = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "forbiddentag")
  @FieldDocumentation(explanation = "A global tag that stops the village from generating if present.", explanationCategory = "World Generation")
  List<String> forbiddenTags = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Key lone buildings like the alchemist' tower have priority in generation and get listed in the village list.", explanationCategory = "World Generation")
  public boolean keyLonebuilding;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Player-specific tag (given by missions) that activate the higher generation chance.", explanationCategory = "World Generation")
  public String keyLoneBuildingGenerateTag = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Player-controlled village, always spawned with a wand.", explanationCategory = "Village type")
  public boolean playerControlled;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "hameau")
  @FieldDocumentation(explanation = "Hamlet type that will be generated around this village.", explanationCategory = "Village type")
  public List<String> hamlets = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING, paramName = "type")
  @FieldDocumentation(explanation = "Special type of village. For example, 'hamlet', which excludes extra buildings from the project list.", explanationCategory = "Village type")
  private String specialType = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN)
  @FieldDocumentation(explanation = "Whether this village type can be spawned with a wand. Defaults to true for villages, false for lone buildings.", explanationCategory = "Village type")
  public boolean spawnable;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "centre")
  @FieldDocumentation(explanation = "The building at the centre of the village.", explanationCategory = "Village Buildings")
  public BuildingPlanSet centreBuilding = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDINGCUSTOM)
  @FieldDocumentation(explanation = "The custom building template at the centre of a custom controlled village.", explanationCategory = "Village Buildings")
  public BuildingCustomPlan customCentre = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING_ADD, paramName = "start")
  @FieldDocumentation(explanation = "A starting building.", explanationCategory = "Village Buildings")
  public List<BuildingPlanSet> startBuildings = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING_ADD, paramName = "player")
  @FieldDocumentation(explanation = "A player-purchasable building.", explanationCategory = "Village Buildings")
  public List<BuildingPlanSet> playerBuildings = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING_ADD, paramName = "core")
  @FieldDocumentation(explanation = "A core building, to be built with high priority by the village type.", explanationCategory = "Village Buildings")
  public List<BuildingPlanSet> coreBuildings = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING_ADD, paramName = "secondary")
  @FieldDocumentation(explanation = "A secondary building, to be build with reduced priority by the village type.", explanationCategory = "Village Buildings")
  public List<BuildingPlanSet> secondaryBuildings = new ArrayList<>();
  
  public List<BuildingPlanSet> extraBuildings = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING_ADD, paramName = "never")
  @FieldDocumentation(explanation = "A building this village will never build.", explanationCategory = "Village Buildings")
  public List<BuildingPlanSet> excludedBuildings = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDINGCUSTOM_ADD, paramName = "customBuilding")
  @FieldDocumentation(explanation = "A custom building template usable in this custom controlled village.", explanationCategory = "Village Buildings")
  public List<BuildingCustomPlan> customBuildings = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "Radius of the village. Overwrites the default value from the settings.", explanationCategory = "Village Behaviour")
  public int radius = MillConfigValues.VillageRadius;
  
  @ConfigField(type = AnnotedParameter.ParameterType.WALL_TYPE)
  @FieldDocumentation(explanation = "Type of the outer village walls, if any (built on the village's edge).", explanationCategory = "Village Behaviour")
  public WallType outerWallType = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.WALL_TYPE)
  @FieldDocumentation(explanation = "Type of the inner village walls (built at a set radius inside the village), if any.", explanationCategory = "Village Behaviour")
  public WallType innerWallType = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "50")
  @FieldDocumentation(explanation = "Radius of the inner village walls.", explanationCategory = "Village Behaviour")
  public int innerWallRadius = 0;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "1")
  @FieldDocumentation(explanation = "Maximum number of builders that can work at the same time in the village.", explanationCategory = "Village Behaviour")
  public int maxSimultaneousConstructions;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "Maximum number of builders that can work on wall buildings at the same time in the village.", explanationCategory = "Village Behaviour")
  public int maxSimultaneousWallConstructions;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Whether this village type carries out raids.", explanationCategory = "Village Behaviour")
  public boolean carriesRaid;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_ADD)
  @FieldDocumentation(explanation = "A block to use as path material. If more than one in the file, they will be upgraded in the same order.", explanationCategory = "Village Behaviour")
  public List<InvItem> pathMaterial = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_PRICE_ADD, paramName = "sellingPrice")
  @FieldDocumentation(explanation = "A custom selling price for this good in the village type, overriding the culture one.", explanationCategory = "Village Behaviour")
  public HashMap<InvItem, Integer> sellingPrices = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_PRICE_ADD, paramName = "buyingPrice")
  @FieldDocumentation(explanation = "A custom buying price for this good in the village type, overriding the culture one.", explanationCategory = "Village Behaviour")
  public HashMap<InvItem, Integer> buyingPrices = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BRICK_COLOUR_THEME_ADD, paramName = "brickColourTheme")
  @FieldDocumentation(explanation = "Colour bricks 'themes' for Indian villages, used to defined what colours houses will have.", explanationCategory = "Village Behaviour")
  public List<BrickColourTheme> brickColourThemes = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Name list to use for this village. 'villages' by default.", explanationCategory = "Village Name")
  public String nameList = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_CASE_SENSITIVE_ADD, paramName = "qualifier")
  @FieldDocumentation(explanation = "Village qualifier applicable without further conditions.", explanationCategory = "Village Name")
  public List<String> qualifiers = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to hills.", explanationCategory = "Village Name")
  public String hillQualifier = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to mountains.", explanationCategory = "Village Name")
  public String mountainQualifier = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to deserts.", explanationCategory = "Village Name")
  public String desertQualifier = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to forests.", explanationCategory = "Village Name")
  public String forestQualifier = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to lava.", explanationCategory = "Village Name")
  public String lavaQualifier = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to lakes.", explanationCategory = "Village Name")
  public String lakeQualifier = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY)
  @FieldDocumentation(explanation = "Qualifier for the village if spawned next to seas.", explanationCategory = "Village Name")
  public String oceanQualifier = null;
  
  public VillageType(Culture c, String key, boolean lone) {
    this.key = key;
    this.culture = c;
    this.lonebuilding = lone;
    this.spawnable = !this.lonebuilding;
    if (this.lonebuilding) {
      this.nameList = null;
    } else {
      this.nameList = "villages";
    } 
  }
  
  public int computeTotalVillageBuildingProjects() {
    int nbBuildingsProjects = ((BuildingPlan[])this.centreBuilding.plans.get(0)).length - 1;
    for (BuildingPlanSet planSet : this.startBuildings)
      nbBuildingsProjects += ((BuildingPlan[])planSet.plans.get(0)).length; 
    for (BuildingPlanSet planSet : this.coreBuildings)
      nbBuildingsProjects += ((BuildingPlan[])planSet.plans.get(0)).length; 
    for (BuildingPlanSet planSet : this.secondaryBuildings)
      nbBuildingsProjects += ((BuildingPlan[])planSet.plans.get(0)).length; 
    for (BuildingPlanSet planSet : this.extraBuildings)
      nbBuildingsProjects += ((BuildingPlan[])planSet.plans.get(0)).length; 
    return nbBuildingsProjects;
  }
  
  public Map<InvItem, Integer> computeVillageTypeCost() {
    HashMap<InvItem, Integer> villageCost = new HashMap<>();
    List<BuildingPlanSet> planSets = getAllBuildingPlanSets();
    for (BuildingPlanSet planSet : planSets) {
      for (BuildingPlan plan : (BuildingPlan[])planSet.plans.get(0)) {
        for (InvItem key : plan.resCost.keySet()) {
          if (villageCost.containsKey(key)) {
            villageCost.put(key, Integer.valueOf(((Integer)villageCost.get(key)).intValue() + ((Integer)plan.resCost.get(key)).intValue()));
            continue;
          } 
          villageCost.put(key, (Integer)plan.resCost.get(key));
        } 
      } 
    } 
    return villageCost;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof VillageType))
      return false; 
    VillageType v = (VillageType)obj;
    return (v.culture == this.culture && v.key.equals(this.key));
  }
  
  public List<BuildingPlanSet> getAllBuildingPlanSets() {
    List<BuildingPlanSet> planSets = new ArrayList<>();
    if (this.centreBuilding != null)
      planSets.add(this.centreBuilding); 
    for (BuildingPlanSet set : this.startBuildings)
      planSets.add(set); 
    if (!this.playerControlled) {
      for (BuildingPlanSet set : this.playerBuildings)
        planSets.add(set); 
      for (BuildingPlanSet set : this.coreBuildings)
        planSets.add(set); 
      for (BuildingPlanSet set : this.secondaryBuildings)
        planSets.add(set); 
      for (BuildingPlanSet set : this.extraBuildings)
        planSets.add(set); 
    } else {
      for (BuildingPlanSet set : this.playerBuildings)
        planSets.add(set); 
      for (BuildingPlanSet set : this.coreBuildings)
        planSets.add(set); 
    } 
    List<BuildingPlanSet> planSetsCopy = new ArrayList<>(planSets);
    for (BuildingPlanSet planSet : planSetsCopy) {
      BuildingPlan plan = ((BuildingPlan[])planSet.plans.get(0))[((BuildingPlan[])planSet.plans.get(0)).length - 1];
      for (String buildingKey : plan.subBuildings)
        planSets.add(this.culture.getBuildingPlanSet(buildingKey)); 
      for (String buildingKey : plan.startingSubBuildings)
        planSets.add(this.culture.getBuildingPlanSet(buildingKey)); 
    } 
    return planSets;
  }
  
  public ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> getBuildingProjects() {
    CopyOnWriteArrayList<BuildingProject> centre = new CopyOnWriteArrayList<>();
    if (this.centreBuilding != null)
      centre.add(this.centreBuilding.getBuildingProject()); 
    CopyOnWriteArrayList<BuildingProject> start = new CopyOnWriteArrayList<>();
    for (BuildingPlanSet set : this.startBuildings)
      start.add(set.getBuildingProject()); 
    CopyOnWriteArrayList<BuildingProject> players = new CopyOnWriteArrayList<>();
    if (!this.playerControlled)
      for (BuildingPlanSet set : this.playerBuildings)
        players.add(set.getBuildingProject());  
    CopyOnWriteArrayList<BuildingProject> core = new CopyOnWriteArrayList<>();
    if (!this.playerControlled)
      for (BuildingPlanSet set : this.coreBuildings)
        core.add(set.getBuildingProject());  
    CopyOnWriteArrayList<BuildingProject> secondary = new CopyOnWriteArrayList<>();
    if (!this.playerControlled)
      for (BuildingPlanSet set : this.secondaryBuildings)
        secondary.add(set.getBuildingProject());  
    CopyOnWriteArrayList<BuildingProject> extra = new CopyOnWriteArrayList<>();
    for (BuildingPlanSet set : this.extraBuildings)
      extra.add(set.getBuildingProject()); 
    ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> v = new ConcurrentHashMap<>();
    v.put(BuildingProject.EnumProjects.CENTRE, centre);
    v.put(BuildingProject.EnumProjects.START, start);
    v.put(BuildingProject.EnumProjects.PLAYER, players);
    v.put(BuildingProject.EnumProjects.CORE, core);
    v.put(BuildingProject.EnumProjects.SECONDARY, secondary);
    v.put(BuildingProject.EnumProjects.EXTRA, extra);
    v.put(BuildingProject.EnumProjects.CUSTOMBUILDINGS, new CopyOnWriteArrayList<>());
    return v;
  }
  
  public int getChoiceWeight(EntityPlayer player) {
    if (isKeyLoneBuildingForGeneration(player))
      return 10000; 
    return this.weight;
  }
  
  public ItemStack getIcon() {
    if (this.icon == null)
      return null; 
    return this.icon.getItemStack();
  }
  
  public float getMinimumBiomeValidity() {
    return this.minimumBiomeValidity;
  }
  
  public String getNameNative() {
    return this.name;
  }
  
  public String getNameNativeAndTranslated() {
    String fullName = getNameNative();
    if (getNameTranslated() != null && getNameTranslated().length() > 0)
      fullName = fullName + " (" + getNameTranslated() + ")"; 
    return fullName;
  }
  
  public String getNameTranslated() {
    if (this.culture.canReadBuildingNames())
      return this.culture.getCultureString("village." + this.key); 
    return null;
  }
  
  public String getNameTranslationKey(UserProfile profile) {
    if (profile.getCultureLanguageKnowledge(this.culture.key) > 100 || !MillConfigValues.languageLearning)
      return "culture:" + this.culture.key + ":village." + this.key; 
    return null;
  }
  
  public int hashCode() {
    return this.culture.hashCode() + this.key.hashCode();
  }
  
  public boolean isHamlet() {
    return "hameau".equals(this.specialType);
  }
  
  public boolean isKeyLoneBuildingForGeneration(EntityPlayer player) {
    if (this.keyLonebuilding)
      return true; 
    if (player != null) {
      UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
      if (this.keyLoneBuildingGenerateTag != null && profile.isTagSet(this.keyLoneBuildingGenerateTag))
        return true; 
    } 
    return false;
  }
  
  public boolean isMarvel() {
    return "marvel".equals(this.specialType);
  }
  
  public boolean isRegularVillage() {
    return (this.specialType == null && !this.lonebuilding);
  }
  
  public boolean isValidForGeneration(MillWorldData mw, EntityPlayer player, HashMap<String, Integer> nbVillages, Point pos, String biome, boolean keyLoneBuildingsOnly) {
    if (!this.generateOnServer && Mill.proxy.isTrueServer())
      return false; 
    if (this.minDistanceFromSpawn >= 0 && pos.horizontalDistanceTo(mw.world.getSpawnPoint()) <= this.minDistanceFromSpawn)
      return false; 
    if (!MillConfigValues.generateHamlets && !this.hamlets.isEmpty())
      return false; 
    for (String tag : this.requiredTags) {
      if (!mw.isGlobalTagSet(tag))
        return false; 
    } 
    for (String tag : this.forbiddenTags) {
      if (mw.isGlobalTagSet(tag))
        return false; 
    } 
    if (keyLoneBuildingsOnly && !isKeyLoneBuildingForGeneration(player))
      return false; 
    if (!this.biomes.contains(biome))
      return false; 
    if (!isKeyLoneBuildingForGeneration(player)) {
      if (this.max != -1 && nbVillages.containsKey(this.key) && ((Integer)nbVillages.get(this.key)).intValue() >= this.max)
        return false; 
    } else {
      boolean existingOneInRange = false;
      for (int i = 0; i < mw.loneBuildingsList.pos.size(); i++) {
        if (((String)mw.loneBuildingsList.types.get(i)).equals(this.key) && 
          pos.horizontalDistanceTo(mw.loneBuildingsList.pos.get(i)) < 2000.0D)
          existingOneInRange = true; 
      } 
      if (existingOneInRange)
        return false; 
    } 
    return true;
  }
  
  public void readVillageTypeInfoPacket(PacketBuffer data) throws IOException {
    this.playerControlled = data.readBoolean();
    this.spawnable = data.readBoolean();
    this.name = StreamReadWrite.readNullableString(data);
    this.specialType = StreamReadWrite.readNullableString(data);
    this.radius = data.readInt();
  }
  
  public String toString() {
    return this.key;
  }
  
  public void writeVillageTypeInfo(PacketBuffer data) throws IOException {
    data.writeString(this.key);
    data.writeBoolean(this.playerControlled);
    data.writeBoolean(this.spawnable);
    StreamReadWrite.writeNullableString(this.name, data);
    StreamReadWrite.writeNullableString(this.specialType, data);
    data.writeInt(this.radius);
  }
}
