package org.millenaire.common.buildingplan;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class BuildingMetadataLoader {
  private static boolean EXPORT_AND_REPLACE = false;
  
  public static void exportAllBuildingPlansTextFiles() {
    int count = 0;
    for (Culture culture : Culture.ListCultures) {
      for (BuildingPlanSet planSet : culture.ListPlanSets) {
        exportBuildingPlanTextFile(planSet);
        count++;
      } 
    } 
    MillLog.major(null, "Exported " + count + " plans to the new format.");
  }
  
  private static void exportBuildingPlanTextFile(BuildingPlanSet planSet) {
    File exportDirectory = new File(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "Converted Buildings"), planSet.culture.key);
    exportDirectory.mkdirs();
    for (int variation = 0; variation < planSet.plans.size(); variation++) {
      File file;
      if (!EXPORT_AND_REPLACE) {
        file = new File(exportDirectory, planSet.key + "_" + (char)(65 + variation) + ".txt");
      } else {
        file = new File(planSet.mainFile.getParentFile(), planSet.key + "_" + (char)(65 + variation) + ".txt");
      } 
      file.delete();
      try {
        BufferedWriter writer = MillCommonUtilities.getAppendWriter(file);
        writer.write("//Parameters for the building as a whole\n");
        ParametersManager.writeAnnotedParameters(writer, ((BuildingPlan[])planSet.plans.get(variation))[0], "init", null, "building");
        writer.write("\n");
        BuildingPlan previousPlan = null;
        for (int level = 0; level < ((BuildingPlan[])planSet.plans.get(variation)).length; level++) {
          String prefix;
          if (level == 0) {
            writer.write("//Parameters for initial construction\n");
            prefix = "initial";
          } else {
            prefix = "upgrade" + level;
          } 
          if (level == 1)
            writer.write("//Parameters for specific upgrades\n"); 
          BuildingPlan plan = ((BuildingPlan[])planSet.plans.get(variation))[level];
          int result = ParametersManager.writeAnnotedParameters(writer, plan, "upgrade", previousPlan, prefix);
          if (result > 0)
            writer.write("\n"); 
          previousPlan = plan;
        } 
        writer.close();
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
  }
  
  private static String toTitleCase(String input) {
    StringBuilder titleCase = new StringBuilder();
    boolean nextTitleCase = true;
    for (char c : input.toCharArray()) {
      if (Character.isSpaceChar(c)) {
        nextTitleCase = true;
      } else if (nextTitleCase) {
        c = Character.toTitleCase(c);
        nextTitleCase = false;
      } 
      titleCase.append(c);
    } 
    return titleCase.toString();
  }
  
  private static void validateBuildingPlan(BuildingPlan buildingPlan) {
    if (buildingPlan.culture != null) {
      for (String maleVillager : buildingPlan.maleResident) {
        if (((VillagerType)buildingPlan.culture.villagerTypes.get(maleVillager)).gender == 2)
          MillLog.error(buildingPlan, "Attempted to add a female villager using the 'male' tag: " + maleVillager); 
      } 
      for (String femaleVillager : buildingPlan.femaleResident) {
        if (((VillagerType)buildingPlan.culture.villagerTypes.get(femaleVillager)).gender == 1)
          MillLog.error(buildingPlan, "Attempted to add a male villager using the 'female' tag: " + femaleVillager); 
      } 
    } 
  }
  
  List<String> lines = new ArrayList<>();
  
  boolean legacyFile = false;
  
  public BuildingMetadataLoader(List<String> lines) {
    this.lines = lines;
    if (lines.size() > 0 && ((String)lines.get(0)).contains("length:"))
      this.legacyFile = true; 
  }
  
  private void initParametersPostHandling(BuildingPlan plan) {
    if (plan.areaToClearLengthBefore == -1)
      plan.areaToClearLengthBefore = plan.areaToClear; 
    if (plan.areaToClearLengthAfter == -1)
      plan.areaToClearLengthAfter = plan.areaToClear; 
    if (plan.areaToClearWidthBefore == -1)
      plan.areaToClearWidthBefore = plan.areaToClear; 
    if (plan.areaToClearWidthAfter == -1)
      plan.areaToClearWidthAfter = plan.areaToClear; 
  }
  
  private void legacyInitialisePlanConfig(BuildingPlan buildingPlan, BuildingPlan previousUpgradePlan) {
    if (previousUpgradePlan == null) {
      buildingPlan.max = 1;
      buildingPlan.priority = 1;
      buildingPlan.priorityMoveIn = 10;
      buildingPlan.areaToClear = 1;
      buildingPlan.startLevel = 0;
      buildingPlan.buildingOrientation = 1;
      buildingPlan.minDistance = 0.0F;
      buildingPlan.maxDistance = 1.0F;
      buildingPlan.reputation = 0;
      buildingPlan.price = 0;
      buildingPlan.showTownHallSigns = true;
    } else {
      buildingPlan.max = previousUpgradePlan.max;
      buildingPlan.priority = previousUpgradePlan.priority;
      buildingPlan.priorityMoveIn = previousUpgradePlan.priorityMoveIn;
      buildingPlan.nativeName = previousUpgradePlan.nativeName;
      buildingPlan.areaToClear = previousUpgradePlan.areaToClear;
      buildingPlan.startLevel = previousUpgradePlan.startLevel;
      buildingPlan.buildingOrientation = previousUpgradePlan.buildingOrientation;
      buildingPlan.signOrder = previousUpgradePlan.signOrder;
      buildingPlan.tags = new ArrayList<>(previousUpgradePlan.tags);
      buildingPlan.villageTags = new ArrayList<>(previousUpgradePlan.villageTags);
      buildingPlan.parentTags = new ArrayList<>(previousUpgradePlan.parentTags);
      buildingPlan.requiredTags = new ArrayList<>(previousUpgradePlan.requiredTags);
      buildingPlan.requiredParentTags = new ArrayList<>(previousUpgradePlan.requiredParentTags);
      buildingPlan.requiredVillageTags = new ArrayList<>(previousUpgradePlan.requiredVillageTags);
      buildingPlan.farFromTag = new HashMap<>(previousUpgradePlan.farFromTag);
      buildingPlan.maleResident = previousUpgradePlan.maleResident;
      buildingPlan.femaleResident = previousUpgradePlan.femaleResident;
      buildingPlan.shop = previousUpgradePlan.shop;
      buildingPlan.width = previousUpgradePlan.width;
      buildingPlan.length = previousUpgradePlan.length;
      buildingPlan.minDistance = previousUpgradePlan.minDistance;
      buildingPlan.maxDistance = previousUpgradePlan.maxDistance;
      buildingPlan.reputation = previousUpgradePlan.reputation;
      buildingPlan.isgift = previousUpgradePlan.isgift;
      buildingPlan.price = previousUpgradePlan.price;
      buildingPlan.pathLevel = previousUpgradePlan.pathLevel;
      buildingPlan.pathWidth = previousUpgradePlan.pathWidth;
      buildingPlan.subBuildings = new ArrayList<>(previousUpgradePlan.subBuildings);
      buildingPlan.startingSubBuildings = new ArrayList<>();
      buildingPlan.startingGoods = new ArrayList<>();
      buildingPlan.parent = previousUpgradePlan;
      if (MillConfigValues.LogBuildingPlan >= 2) {
        String s = "";
        for (String s2 : buildingPlan.subBuildings)
          s = s + s2 + " "; 
        if (s.length() > 0)
          MillLog.minor(buildingPlan, "Copied sub-buildings from parent: " + s); 
      } 
      buildingPlan.showTownHallSigns = previousUpgradePlan.showTownHallSigns;
      buildingPlan.exploreTag = previousUpgradePlan.exploreTag;
      buildingPlan.irrigation = previousUpgradePlan.irrigation;
      buildingPlan.isSubBuilding = previousUpgradePlan.isSubBuilding;
      buildingPlan.abstractedProduction = new HashMap<>(previousUpgradePlan.abstractedProduction);
    } 
  }
  
  private void legacyReadConfigLine(BuildingPlan buildingPlan, String line, boolean importPlan) {
    String[] configs = line.split(";", -1);
    for (String config : configs) {
      if ((config.split(":")).length == 2) {
        String key = config.split(":")[0].toLowerCase();
        String value = config.split(":")[1];
        if (key.equalsIgnoreCase("max")) {
          buildingPlan.max = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("priority")) {
          buildingPlan.priority = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("moveinpriority")) {
          buildingPlan.priorityMoveIn = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("french") || key.equalsIgnoreCase("native")) {
          buildingPlan.nativeName = value;
        } else if (key.equalsIgnoreCase("english") || key.startsWith("name_")) {
          if (key.equals("english")) {
            buildingPlan.translatedNames.put("en", value);
          } else {
            buildingPlan.translatedNames.put(key.split("_")[1], value);
          } 
        } else if (key.equalsIgnoreCase("around")) {
          buildingPlan.areaToClear = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("startLevel")) {
          buildingPlan.startLevel = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("orientation")) {
          buildingPlan.buildingOrientation = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("pathlevel")) {
          buildingPlan.pathLevel = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("rebuildpath")) {
          buildingPlan.rebuildPath = Boolean.parseBoolean(value);
        } else if (key.equalsIgnoreCase("isgift")) {
          buildingPlan.isgift = Boolean.parseBoolean(value);
        } else if (key.equalsIgnoreCase("pathwidth")) {
          buildingPlan.pathWidth = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("reputation")) {
          try {
            buildingPlan.reputation = MillCommonUtilities.readInteger(value);
          } catch (Exception e) {
            buildingPlan.reputation = 0;
            MillLog.error(null, "Error when reading reputation line in " + buildingPlan.getLoadedFromFile().getName() + ": " + line + " : " + e.getMessage());
          } 
        } else if (key.equalsIgnoreCase("price")) {
          try {
            buildingPlan.price = MillCommonUtilities.readInteger(value);
          } catch (Exception e) {
            buildingPlan.price = 0;
            MillLog.error(buildingPlan, "Error when reading reputation line in " + buildingPlan.getLoadedFromFile().getName() + ": " + line + " : " + e.getMessage());
          } 
        } else if (key.equalsIgnoreCase("version")) {
          try {
            buildingPlan.version = MillCommonUtilities.readInteger(value);
          } catch (Exception e) {
            buildingPlan.version = 0;
            MillLog.error(buildingPlan, "Error when reading version line in: " + line + " : " + e.getMessage());
          } 
        } else if (key.equalsIgnoreCase("length")) {
          buildingPlan.length = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("width")) {
          buildingPlan.width = Integer.parseInt(value);
        } else if (!importPlan && key.equalsIgnoreCase("male")) {
          if (buildingPlan.culture.villagerTypes.containsKey(value.toLowerCase())) {
            if (((VillagerType)buildingPlan.culture.villagerTypes.get(value.toLowerCase())).gender == 2) {
              MillLog.error(buildingPlan, "Attempted to add a female villager using the 'male' tag: " + value);
            } else {
              buildingPlan.maleResident.add(value.toLowerCase());
            } 
          } else {
            MillLog.error(buildingPlan, "Attempted to load unknown male villager: " + value);
          } 
        } else if (!importPlan && key.equalsIgnoreCase("female")) {
          if (buildingPlan.culture.villagerTypes.containsKey(value.toLowerCase())) {
            if (((VillagerType)buildingPlan.culture.villagerTypes.get(value.toLowerCase())).gender == 1) {
              MillLog.error(buildingPlan, "Attempted to add a male villager using the 'female' tag: " + value);
            } else {
              buildingPlan.femaleResident.add(value.toLowerCase());
            } 
          } else {
            MillLog.error(buildingPlan, "Attempted to load unknown female villager: " + value);
          } 
        } else if (!importPlan && key.equalsIgnoreCase("visitor")) {
          if (buildingPlan.culture.villagerTypes.containsKey(value.toLowerCase())) {
            buildingPlan.visitors.add(value.toLowerCase());
          } else {
            MillLog.error(buildingPlan, "Attempted to load unknown visitor: " + value);
          } 
        } else if (key.equalsIgnoreCase("exploretag")) {
          buildingPlan.exploreTag = value.toLowerCase();
        } else if (key.equalsIgnoreCase("requiredglobalTag")) {
          buildingPlan.requiredGlobalTag = value.toLowerCase();
        } else if (key.equalsIgnoreCase("irrigation")) {
          buildingPlan.irrigation = Integer.parseInt(value);
        } else if (!importPlan && key.equalsIgnoreCase("shop")) {
          if (buildingPlan.culture != null)
            if (buildingPlan.culture.shopBuys.containsKey(value) || buildingPlan.culture.shopSells.containsKey(value) || buildingPlan.culture.shopBuysOptional.containsKey(value)) {
              buildingPlan.shop = value;
            } else {
              MillLog.error(buildingPlan, "Undefined shop type: " + value);
            }  
        } else if (key.equalsIgnoreCase("minDistance")) {
          buildingPlan.minDistance = Float.parseFloat(value) / 100.0F;
        } else if (key.equalsIgnoreCase("maxDistance")) {
          buildingPlan.maxDistance = Float.parseFloat(value) / 100.0F;
        } else if (key.equalsIgnoreCase("fixedorientation")) {
          if (value.equalsIgnoreCase("east")) {
            buildingPlan.fixedOrientation = 3;
          } else if (value.equalsIgnoreCase("west")) {
            buildingPlan.fixedOrientation = 1;
          } else if (value.equalsIgnoreCase("north")) {
            buildingPlan.fixedOrientation = 0;
          } else if (value.equalsIgnoreCase("south")) {
            buildingPlan.fixedOrientation = 2;
          } else {
            MillLog.error(buildingPlan, "Unknown fixed orientation: " + value);
          } 
        } else if (key.equalsIgnoreCase("signs")) {
          String[] temp = value.split(",");
          if (temp[0].length() > 0) {
            buildingPlan.signOrder = new int[temp.length];
            for (int i = 0; i < temp.length; i++)
              buildingPlan.signOrder[i] = Integer.parseInt(temp[i]); 
          } 
        } else if (key.equalsIgnoreCase("tag")) {
          buildingPlan.tags.add(value.toLowerCase());
        } else if (key.equalsIgnoreCase("villageTag")) {
          buildingPlan.villageTags.add(value.toLowerCase());
        } else if (key.equalsIgnoreCase("parentTag")) {
          buildingPlan.parentTags.add(value.toLowerCase());
        } else if (key.equalsIgnoreCase("requiredTag")) {
          buildingPlan.requiredTags.add(value.toLowerCase());
        } else if (key.equalsIgnoreCase("requiredVillageTag")) {
          buildingPlan.requiredVillageTags.add(value.toLowerCase());
        } else if (key.equalsIgnoreCase("requiredParentTag")) {
          buildingPlan.requiredParentTags.add(value.toLowerCase());
        } else if (key.equalsIgnoreCase("farFromTag")) {
          buildingPlan.farFromTag.put(value.split(",")[0].toLowerCase(), Integer.valueOf(Integer.parseInt(value.split(",")[1])));
        } else if (key.equalsIgnoreCase("subbuilding")) {
          buildingPlan.subBuildings.add(value);
        } else if (key.equalsIgnoreCase("startingsubbuilding")) {
          buildingPlan.startingSubBuildings.add(value);
        } else if (!importPlan && key.equalsIgnoreCase("startinggood")) {
          String[] temp = value.split(",");
          if (temp.length != 4) {
            MillLog.error(buildingPlan, "Error when reading starting good: expected four fields, found " + temp.length + ": " + value);
          } else {
            String s = temp[0];
            if (!InvItem.INVITEMS_BY_NAME.containsKey(s)) {
              MillLog.error(buildingPlan, "Error when reading starting good: unknown good: " + s);
            } else {
              BuildingPlan.StartingGood sg = new BuildingPlan.StartingGood((InvItem)InvItem.INVITEMS_BY_NAME.get(s), Double.parseDouble(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
              buildingPlan.startingGoods.add(sg);
            } 
          } 
        } else if (key.equalsIgnoreCase("type")) {
          if (value.equalsIgnoreCase("subbuilding"))
            buildingPlan.isSubBuilding = true; 
        } else if (key.equalsIgnoreCase("showtownhallsigns")) {
          buildingPlan.showTownHallSigns = Boolean.parseBoolean(value);
        } else if (key.equalsIgnoreCase("abstractedProduction")) {
          if (InvItem.INVITEMS_BY_NAME.containsKey(value.split(",")[0].toLowerCase())) {
            InvItem iv = (InvItem)InvItem.INVITEMS_BY_NAME.get(value.split(",")[0].toLowerCase());
            int quantity = Integer.parseInt(value.split(",")[1]);
            if (iv.meta >= 0) {
              buildingPlan.abstractedProduction.put(iv, Integer.valueOf(quantity));
            } else {
              MillLog.error(buildingPlan, "Abstracted production goods should not include generic goods like any_wood. Skipping it.");
            } 
          } else {
            MillLog.error(buildingPlan, "Unknown abstracted production good found when loading building plan : " + value);
          } 
        } else if (!importPlan) {
          MillLog.error(buildingPlan, "Could not recognise key on line: " + config);
        } 
      } 
    } 
    if (buildingPlan.isSubBuilding)
      buildingPlan.max = 0; 
    if (buildingPlan.priority < 1)
      MillLog.error(buildingPlan, "Null or negative weight found in config!"); 
    if (MillConfigValues.LogBuildingPlan >= 3) {
      String s = "";
      for (String s2 : buildingPlan.subBuildings)
        s = s + s2 + " "; 
      if (s.length() > 0)
        MillLog.minor(buildingPlan, "Sub-buildings after read: " + s); 
    } 
  }
  
  public void loadDataForPlan(BuildingPlan plan, BuildingPlan previousPlan, boolean importPlan) {
    if (this.legacyFile) {
      legacyInitialisePlanConfig(plan, previousPlan);
      if (this.lines.size() > plan.level)
        legacyReadConfigLine(plan, this.lines.get(plan.level), importPlan); 
    } else {
      String prefix;
      ParametersManager.initAnnotedParameterData(plan, previousPlan, "init", plan.culture);
      ParametersManager.initAnnotedParameterData(plan, previousPlan, "upgrade", plan.culture);
      if (plan.level == 0) {
        ParametersManager.loadPrefixedAnnotedParameterData(this.lines, "building", plan, "init", "building", plan.getLoadedFromFile().getName(), plan
            .getCulture());
        initParametersPostHandling(plan);
      } 
      if (plan.level == 0) {
        prefix = "initial";
      } else {
        prefix = "upgrade" + plan.level;
      } 
      ParametersManager.loadPrefixedAnnotedParameterData(this.lines, prefix, plan, "upgrade", "building", plan.getLoadedFromFile().getName(), plan.getCulture());
      validateBuildingPlan(plan);
    } 
  }
}
