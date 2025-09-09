package org.millenaire.common.goal.generic;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;

public abstract class GoalGeneric extends Goal implements ParametersManager.DefaultValueOverloaded {
  public static final String GOAL_TYPE_FIELD_NAME = "GOAL_TYPE";
  
  public static final Class[] GENERIC_GOAL_CLASSES = new Class[] { 
      GoalGenericCooking.class, GoalGenericTendFurnace.class, GoalGenericTakeFromBuilding.class, GoalGenericCrafting.class, GoalGenericSlaughterAnimal.class, GoalGenericHarvestCrop.class, GoalGenericPlantCrop.class, GoalGenericVisit.class, GoalGenericMining.class, GoalGenericGatherBlocks.class, 
      GoalGenericPlantSapling.class };
  
  private static List<File> getGenericGoalFiles(String directoryName) {
    VirtualDir virtualGoalDir = Mill.virtualLoadingDir.getChildDirectory("goals").getChildDirectory(directoryName);
    return virtualGoalDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"));
  }
  
  public static void loadGenericGoals() {
    for (Class<?> genericGoalClass : GENERIC_GOAL_CLASSES) {
      try {
        String goalType = (String)genericGoalClass.getField("GOAL_TYPE").get(null);
        for (File file : getGenericGoalFiles("generic" + goalType)) {
          try {
            GoalGeneric goal = (GoalGeneric)genericGoalClass.newInstance();
            String key = file.getName().split("\\.")[0].toLowerCase();
            goal.file = file;
            ParametersManager.loadAnnotedParameterData(file, goal, null, "generic " + goal.getTypeLabel() + " goal", null);
            goal.applyDefaultSettings();
            if (goal != null && goal.validateGoal()) {
              if (MillConfigValues.LogGeneralAI >= 1)
                MillLog.major(goal, "loaded " + goalType + " goal"); 
              goals.put(key, goal);
            } 
          } catch (Exception e) {
            MillLog.printException(e);
          } 
        } 
      } catch (Exception e) {
        MillLog.printException("Exception when loading generic goal type:", e);
      } 
    } 
  }
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Tag a building must have for action to be possible. If absent, then the villager's house is used.")
  public String buildingTag = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Extra tag required for the destination to be valid.")
  public String requiredTag = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "If true, this goal happens in the central building.")
  public boolean townHallGoal;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "50")
  @FieldDocumentation(explanation = "The goal's priority. The higher it is the more likely villagers will pick it.")
  public int priority;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "10")
  @FieldDocumentation(explanation = "A random value between 0 and this to add to the goal's priority.")
  public int priorityRandom;
  
  @ConfigField(type = AnnotedParameter.ParameterType.MILLISECONDS, defaultValue = "5000")
  @FieldDocumentation(explanation = "Duration in ms of the action.")
  public int duration = 100;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Specify if the sentences for this goal is not the name of the goal itself.")
  public String sentenceKey = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Specify if the label for this goal is not the name of the goal itself.")
  public String labelKey = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.ITEMSTACK_ARRAY)
  @FieldDocumentation(explanation = "Items held by the villager, including when traveling.")
  public ItemStack[] heldItems = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.ITEMSTACK_ARRAY)
  @FieldDocumentation(explanation = "Items held by the villager in his off hand, including when traveling.")
  public ItemStack[] heldItemsOffHand = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.ITEMSTACK_ARRAY)
  @FieldDocumentation(explanation = "Items held by the villager, at destination only.")
  public ItemStack[] heldItemsDestination = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.ITEMSTACK_ARRAY)
  @FieldDocumentation(explanation = "Items held by the villager in his off hand, at destination only.")
  public ItemStack[] heldItemsOffHandDestination = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Sound to play when the goal is performed (metal, wool...).")
  public String sound = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "If true, the villager can move 'randomly' after reaching the destination.")
  public boolean allowRandomMoves;
  
  @ConfigField(type = AnnotedParameter.ParameterType.MILLISECONDS, defaultValue = "5000")
  @FieldDocumentation(explanation = "Duration in ms before the action can reoccur.")
  public int reoccurDelay = 0;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "If true, the villager will hold his best weapon while doing this goal.")
  public boolean holdWeapons;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Whether the villager should look at the goal's destination. True or false by default depending on the generic goal.")
  public boolean lookAtGoal;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "3")
  @FieldDocumentation(explanation = "Range from destination from which the goal is doable.")
  public int range;
  
  File file = null;
  
  public int actionDuration(MillVillager villager) throws Exception {
    return this.duration;
  }
  
  public boolean allowRandomMoves() {
    return this.allowRandomMoves;
  }
  
  public List<Building> getBuildings(MillVillager villager) {
    List<Building> buildings = new ArrayList<>();
    if (this.townHallGoal) {
      if (this.requiredTag == null || villager.getTownHall().containsTags(this.requiredTag))
        buildings.add(villager.getTownHall()); 
    } else if (this.buildingTag == null) {
      if (this.requiredTag == null || villager.getHouse().containsTags(this.requiredTag))
        buildings.add(villager.getHouse()); 
    } else {
      for (Building b : villager.getTownHall().getBuildingsWithTag(this.buildingTag)) {
        if (this.requiredTag == null || b.containsTags(this.requiredTag))
          buildings.add(b); 
      } 
    } 
    return buildings;
  }
  
  public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
    if (this.holdWeapons)
      return new ItemStack[] { villager.getWeapon() }; 
    if (this.heldItemsDestination != null)
      return this.heldItemsDestination; 
    return this.heldItems;
  }
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) throws Exception {
    if (this.heldItemsOffHandDestination != null)
      return this.heldItemsOffHandDestination; 
    return this.heldItemsOffHand;
  }
  
  public ItemStack[] getHeldItemsOffHandTravelling(MillVillager villager) throws Exception {
    return this.heldItemsOffHand;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    if (this.holdWeapons)
      return new ItemStack[] { villager.getWeapon() }; 
    return this.heldItems;
  }
  
  public abstract String getTypeLabel();
  
  public final boolean isDestPossible(MillVillager villager, Building dest) throws MillLog.MillenaireException {
    return (validateDest(villager, dest) && isDestPossibleSpecific(villager, dest));
  }
  
  public abstract boolean isDestPossibleSpecific(MillVillager paramMillVillager, Building paramBuilding);
  
  public abstract boolean isPossibleGenericGoal(MillVillager paramMillVillager) throws Exception;
  
  public final boolean isPossibleSpecific(MillVillager villager) throws Exception {
    if (this.reoccurDelay > 0 && villager.lastGoalTime.containsKey(this) && ((Long)villager.lastGoalTime.get(this)).longValue() + this.reoccurDelay > villager.world.getWorldTime())
      return false; 
    if (!isPossibleGenericGoal(villager))
      return false; 
    List<Building> buildings = getBuildings(villager);
    boolean destFound = false;
    if (!buildings.isEmpty()) {
      for (Building dest : buildings) {
        if (!destFound)
          destFound = isDestPossible(villager, dest); 
      } 
      return destFound;
    } 
    return false;
  }
  
  public String labelKey(MillVillager villager) {
    if (this.labelKey == null)
      return this.key; 
    return this.labelKey;
  }
  
  public String labelKeyWhileTravelling(MillVillager villager) {
    if (this.labelKey == null)
      return this.key; 
    return this.labelKey;
  }
  
  public final boolean lookAtGoal() {
    return this.lookAtGoal;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return this.priority + villager.getRNG().nextInt(this.priorityRandom);
  }
  
  public int range(MillVillager villager) {
    return this.range;
  }
  
  public String sentenceKey() {
    if (this.sentenceKey == null)
      return this.key; 
    return this.sentenceKey;
  }
  
  public String toString() {
    if (this.key != null)
      return "goal:" + this.key; 
    if (this.file != null)
      return "goal:" + this.file.getName(); 
    return "goal:unknownkey";
  }
  
  public abstract boolean validateGoal();
}
