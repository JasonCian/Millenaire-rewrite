package org.millenaire.common.goal.generic;

import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericCrafting extends GoalGeneric {
  public static final String GOAL_TYPE = "crafting";
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "Each action will require and use all the inputs.")
  public HashMap<InvItem, Integer> input = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "Each action will produce all the outputs.")
  public HashMap<InvItem, Integer> output = new HashMap<>();
  
  public void applyDefaultSettings() {}
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Building> buildings = getBuildings(villager);
    for (Building dest : buildings) {
      if (isDestPossible(villager, dest))
        return packDest(dest.getResManager().getCraftingPos(), dest); 
    } 
    return null;
  }
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    if (!this.output.isEmpty())
      return ((InvItem)this.output.keySet().iterator().next()).getItemStack(); 
    return null;
  }
  
  public String getTypeLabel() {
    return "crafting";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    for (InvItem item : this.input.keySet()) {
      if (villager.countInv(item) + b.countGoods(item) < ((Integer)this.input.get(item)).intValue())
        return false; 
    } 
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Building dest = villager.getGoalBuildingDest();
    if (dest == null)
      return true; 
    for (InvItem item : this.input.keySet()) {
      if (villager.countInv(item) + dest.countGoods(item) < ((Integer)this.input.get(item)).intValue())
        return true; 
    } 
    for (InvItem item : this.input.keySet()) {
      int nbTaken = villager.takeFromInv(item, ((Integer)this.input.get(item)).intValue());
      if (nbTaken < ((Integer)this.input.get(item)).intValue())
        dest.takeGoods(item, ((Integer)this.input.get(item)).intValue() - nbTaken); 
    } 
    for (InvItem item : this.output.keySet())
      dest.storeGoods(item, ((Integer)this.output.get(item)).intValue()); 
    if (this.sound != null)
      WorldUtilities.playSoundByMillName(villager.world, villager.getPos(), this.sound, 1.0F); 
    return true;
  }
  
  public boolean swingArms() {
    return true;
  }
  
  public boolean validateGoal() {
    if (this.output.isEmpty()) {
      MillLog.error(this, "Generic crafting goals require at least one output.");
      return false;
    } 
    return true;
  }
}
