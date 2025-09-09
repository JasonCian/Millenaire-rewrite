package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingBlock;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillLog;

@Documentation("Build village paths.")
public class GoalBuildPath extends Goal {
  public GoalBuildPath() {
    this.maxSimultaneousTotal = 1;
    this.tags.add("tag_construction");
    this.icon = InvItem.createInvItem((Block)MillBlocks.PATHDIRT);
  }
  
  public int actionDuration(MillVillager villager) {
    int toolEfficiency = (int)villager.getBestShovel().getDestroySpeed(new ItemStack((Item)villager.getBestShovel(), 1), Blocks.DIRT.getDefaultState());
    return 10 - toolEfficiency;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    BuildingBlock b = villager.getTownHall().getCurrentPathBuildingBlock();
    if (b != null)
      return packDest(b.p); 
    return null;
  }
  
  public ItemStack[] getHeldItemsOffHandTravelling(MillVillager villager) {
    BuildingBlock bblock = villager.getTownHall().getCurrentPathBuildingBlock();
    if (bblock != null && 
      bblock.block != Blocks.AIR)
      return new ItemStack[] { new ItemStack(Item.getItemFromBlock(bblock.block), 1, bblock.getMeta()) }; 
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
    return (MillConfigValues.BuildVillagePaths && villager.getTownHall().getCurrentPathBuildingBlock() != null);
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    return (villager.getTownHall().getCurrentPathBuildingBlock() != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    BuildingBlock bblock = villager.getTownHall().getCurrentPathBuildingBlock();
    if (bblock == null)
      return true; 
    if (MillConfigValues.LogVillagePaths >= 3)
      MillLog.debug(villager, "Building path block: " + bblock); 
    bblock.pathBuild(villager.getTownHall());
    (villager.getTownHall()).pathsToBuildPathIndex++;
    BuildingBlock b = villager.getTownHall().getCurrentPathBuildingBlock();
    villager.swingArm(EnumHand.MAIN_HAND);
    if (b != null) {
      villager.setGoalDestPoint(b.p);
      return false;
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 50;
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
