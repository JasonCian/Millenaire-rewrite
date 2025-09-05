package org.millenaire.common.goal.generic;

import java.util.HashMap;
import java.util.List;
import net.minecraft.init.Blocks;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class GoalGenericTakeFromBuilding extends GoalGeneric {
  public static final String GOAL_TYPE = "takefrombuilding";
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "4")
  @FieldDocumentation(explanation = "Minimum number of items to gather in one go.")
  public int minimumpickup;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "collect_good")
  @FieldDocumentation(explanation = "Goods to be picked up from the target building, with maximum quantity to have in inventory.")
  public HashMap<InvItem, Integer> collectGoods = new HashMap<>();
  
  public void applyDefaultSettings() {
    this.lookAtGoal = true;
    this.icon = InvItem.createInvItem(Blocks.FURNACE);
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Building> buildings = getBuildings(villager);
    for (Building dest : buildings) {
      if (isDestPossible(villager, dest)) {
        int totalCount = 0;
        for (InvItem ii : this.collectGoods.keySet()) {
          int quantityNeeded = ((Integer)this.collectGoods.get(ii)).intValue() - villager.countInv(ii);
          if (quantityNeeded > 0)
            totalCount += Math.min(quantityNeeded, dest.nbGoodAvailable(ii, false, false, false)); 
        } 
        if (totalCount >= this.minimumpickup)
          return packDest(dest.getResManager().getSellingPos(), dest); 
      } 
    } 
    return null;
  }
  
  public String getTypeLabel() {
    return "takefrombuilding";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    if (getDestination(villager) == null)
      return false; 
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Building dest = villager.getGoalBuildingDest();
    if (dest != null)
      for (InvItem ii : this.collectGoods.keySet()) {
        int quantityNeeded = ((Integer)this.collectGoods.get(ii)).intValue() - villager.countInv(ii);
        if (quantityNeeded > 0)
          villager.takeFromBuilding(dest, ii.getItem(), ii.meta, Math.min(quantityNeeded, dest.nbGoodAvailable(ii, false, false, false))); 
      }  
    return true;
  }
  
  public boolean validateGoal() {
    if (this.collectGoods.size() == 0) {
      MillLog.error(this, "Generic take from building goals require at least one good to collect.");
      return false;
    } 
    return true;
  }
}
