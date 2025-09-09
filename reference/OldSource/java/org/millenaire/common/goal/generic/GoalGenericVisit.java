package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class GoalGenericVisit extends GoalGeneric {
  public static final String GOAL_TYPE = "visit";
  
  @ConfigField(type = AnnotedParameter.ParameterType.POS_TYPE, defaultValue = "sleeping")
  @FieldDocumentation(explanation = "Pos type where the goal occurs. Defaults to sleeping pos.")
  public AnnotedParameter.PosType targetPosition;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_LIST)
  @FieldDocumentation(explanation = "If set, the goal will have villagers doing one of the provided goal as destination. Replaces buildings as destination.")
  public List<String> targetVillagerGoals = null;
  
  public void applyDefaultSettings() {
    this.travelBookShow = false;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    if (this.targetVillagerGoals != null) {
      List<MillVillager> targets = new ArrayList<>();
      for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
        if (v != villager && v.goalKey != null && validGoalForTargetting(v.goalKey))
          targets.add(v); 
      } 
      if (targets.isEmpty())
        return null; 
      return packDest(null, null, (Entity)targets.get(MillCommonUtilities.randomInt(targets.size())));
    } 
    List<Building> buildings = getBuildings(villager);
    for (Building dest : buildings) {
      if (isDestPossible(villager, dest))
        return packDest(this.targetPosition.getPosition(dest), dest); 
    } 
    return null;
  }
  
  public String getTypeLabel() {
    return "visit";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return (villager.getPos().distanceTo(b.getResManager().getCraftingPos()) > 5.0D);
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return true;
  }
  
  protected boolean isStillValidSpecific(MillVillager villager) throws Exception {
    if (villager.getGoalDestEntity() != null && (villager.getGoalDestEntity()).isDead)
      return false; 
    if (this.targetVillagerGoals != null && villager.getGoalDestEntity() != null && villager.getGoalDestEntity() instanceof MillVillager) {
      MillVillager targetVillager = (MillVillager)villager.getGoalDestEntity();
      if (targetVillager.goalKey == null || !validGoalForTargetting(targetVillager.goalKey))
        return false; 
    } 
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    int result = super.priority(villager);
    return result;
  }
  
  public boolean validateGoal() {
    return true;
  }
  
  private boolean validGoalForTargetting(String goalKey) {
    if (this.targetVillagerGoals == null)
      return false; 
    Goal goal = (Goal)Goal.goals.get(goalKey);
    if (goal == null) {
      MillLog.error(this, "Villager had unknown goal: " + goalKey);
      return false;
    } 
    for (String target : this.targetVillagerGoals) {
      if (goal.tags.contains(target))
        return true; 
    } 
    return false;
  }
}
