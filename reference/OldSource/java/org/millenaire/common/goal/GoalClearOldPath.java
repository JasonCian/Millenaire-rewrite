package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.PathUtilities;
import org.millenaire.common.utilities.Point;

@Documentation("Clear an old path.")
public class GoalClearOldPath extends Goal {
  public GoalClearOldPath() {
    this.maxSimultaneousTotal = 1;
    this.tags.add("tag_construction");
    this.icon = InvItem.createInvItem(Items.IRON_SHOVEL);
  }
  
  public int actionDuration(MillVillager villager) {
    int toolEfficiency = (int)villager.getBestShovel().getDestroySpeed(new ItemStack((Item)villager.getBestShovel(), 1), Blocks.DIRT.getDefaultState());
    return 10 - toolEfficiency;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    Point p = villager.getTownHall().getCurrentClearPathPoint();
    if (p != null)
      return packDest(p); 
    return null;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return villager.getBestShovelStack();
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_BUILDING_NO_LEAVES; 
    return JPS_CONFIG_BUILDING;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (MillConfigValues.BuildVillagePaths && villager.getTownHall().getCurrentClearPathPoint() != null);
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    return (villager.getTownHall().getCurrentClearPathPoint() != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Point p = villager.getTownHall().getCurrentClearPathPoint();
    if (p == null)
      return true; 
    if (MillConfigValues.LogVillagePaths >= 3)
      MillLog.debug(villager, "Clearing old path block: " + p); 
    PathUtilities.clearPathBlock(p, villager.world);
    (villager.getTownHall()).oldPathPointsToClearIndex++;
    p = villager.getTownHall().getCurrentClearPathPoint();
    villager.swingArm(EnumHand.MAIN_HAND);
    if (p != null) {
      villager.setGoalDestPoint(p);
      return false;
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 40;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
  
  public boolean stopMovingWhileWorking() {
    return false;
  }
  
  public boolean unreachableDestination(MillVillager villager) throws Exception {
    performAction(villager);
    return true;
  }
}
