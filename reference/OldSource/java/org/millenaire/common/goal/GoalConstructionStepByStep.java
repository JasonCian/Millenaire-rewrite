package org.millenaire.common.goal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.millenaire.common.buildingplan.BuildingBlock;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.ConstructionIP;

@Documentation("Build a building")
public class GoalConstructionStepByStep extends Goal {
  public GoalConstructionStepByStep() {
    this.tags.add("tag_construction");
    this.icon = InvItem.createInvItem(Items.IRON_SHOVEL);
  }
  
  public int actionDuration(MillVillager villager) {
    ConstructionIP cip = villager.getCurrentConstruction();
    if (cip == null)
      return 0; 
    BuildingBlock bblock = cip.getCurrentBlock();
    if (bblock == null)
      return 0; 
    int toolEfficiency = (int)villager.getBestShovel().getDestroySpeed(new ItemStack((Item)villager.getBestShovel(), 1), Blocks.DIRT.getDefaultState());
    int duration = 14;
    if (toolEfficiency > 8) {
      duration = 7;
    } else if (toolEfficiency == 8) {
      duration = 8;
    } else if (toolEfficiency >= 6) {
      duration = 10;
    } else if (toolEfficiency >= 4) {
      duration = 12;
    } else if (toolEfficiency >= 2) {
      duration = 14;
    } else {
      duration = 16;
    } 
    if (bblock.block == Blocks.AIR || bblock.block == Blocks.DIRT || bblock.block == Blocks.GRASS || bblock.block == Blocks.SAND)
      return (int)(duration / 4.0F); 
    return duration;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    ConstructionIP cip = villager.getCurrentConstruction();
    if (cip == null)
      return null; 
    BuildingBlock bblock = cip.getCurrentBlock();
    if (bblock == null)
      return null; 
    return packDest(bblock.p);
  }
  
  private ConstructionIP getDoableConstructionIP(MillVillager villager) {
    for (ConstructionIP cip : villager.getTownHall().getConstructionsInProgress()) {
      boolean possible = true;
      if ((cip.getBuilder() != null && cip.getBuilder() != villager) || cip.getBuildingLocation() == null || cip.getBblocks() == null)
        possible = false; 
      if (possible) {
        if (villager.getTownHall().getBuildingPlanForConstruction(cip) == null)
          return null; 
        for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
          if ((Goal.getResourcesForBuild.key.equals(v.goalKey) || Goal.construction.key.equals(v.goalKey)) && 
            v.constructionJobId == cip.getId())
            possible = false; 
        } 
        for (InvItem key : (villager.getTownHall().getBuildingPlanForConstruction(cip)).resCost.keySet()) {
          if (villager.countInv(key) < ((Integer)(villager.getTownHall().getBuildingPlanForConstruction(cip)).resCost.get(key)).intValue())
            possible = false; 
        } 
      } 
      if (possible)
        return cip; 
    } 
    return null;
  }
  
  public ItemStack[] getHeldItemsOffHandTravelling(MillVillager villager) {
    ConstructionIP cip = villager.getCurrentConstruction();
    if (cip == null)
      return null; 
    BuildingBlock bblock = cip.getCurrentBlock();
    if (bblock != null && 
      bblock.block != Blocks.AIR && Item.getItemFromBlock(bblock.block) != null) {
      IBlockState blockState = bblock.block.getStateFromMeta(bblock.getMeta());
      Item item = bblock.block.getItemDropped(blockState, MillCommonUtilities.getRandom(), 0);
      if (item != null)
        return new ItemStack[] { new ItemStack(item, 1, bblock.block.damageDropped(blockState)) }; 
      item = Item.getItemFromBlock(bblock.block);
      return new ItemStack[] { new ItemStack(item, 1, 0) };
    } 
    return null;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return villager.getBestShovelStack();
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    ConstructionIP cip = villager.getCurrentConstruction();
    if (cip != null && cip.getBuildingLocation() != null && 
      cip.getBuildingLocation().containsPlanTag("scaffoldings"))
      return JPS_CONFIG_BUILDING_SCAFFOLDINGS; 
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_BUILDING_NO_LEAVES; 
    return JPS_CONFIG_BUILDING;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (getDoableConstructionIP(villager) != null);
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    ConstructionIP cip = villager.getCurrentConstruction();
    if (cip == null)
      return false; 
    return true;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public void onAccept(MillVillager villager) {
    ConstructionIP cip = getDoableConstructionIP(villager);
    if (cip == null)
      return; 
    cip.setBuilder(villager);
    villager.constructionJobId = cip.getId();
  }
  
  public boolean performAction(MillVillager villager) throws MillLog.MillenaireException {
    ConstructionIP cip = villager.getCurrentConstruction();
    if (cip == null)
      return true; 
    BuildingBlock bblock = cip.getCurrentBlock();
    if (bblock == null)
      return true; 
    if (MillConfigValues.LogWifeAI >= 2)
      MillLog.minor(villager, "Setting block at " + bblock.p + " type: " + bblock.block + " replacing: " + villager.getBlock(bblock.p) + " distance: " + bblock.p.distanceTo((Entity)villager)); 
    if (bblock.p.horizontalDistanceTo((Entity)villager) < 1.0D && bblock.p.getiY() > villager.posY && bblock.p.getiY() < villager.posY + 2.0D) {
      boolean jumped = false;
      World world = villager.world;
      if (!WorldUtilities.isBlockFullCube(world, villager.getPos().getiX() + 1, villager.getPos().getiY() + 1, villager.getPos().getiZ()) && 
        !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX() + 1, villager.getPos().getiY() + 2, villager.getPos().getiZ())) {
        villager.setPosition((villager.getPos().getiX() + 1), (villager.getPos().getiY() + 1), villager.getPos().getiZ());
        jumped = true;
      } 
      if (!jumped && !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX() - 1, villager.getPos().getiY() + 1, villager.getPos().getiZ()) && 
        !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX() - 1, villager.getPos().getiY() + 2, villager.getPos().getiZ())) {
        villager.setPosition((villager.getPos().getiX() - 1), (villager.getPos().getiY() + 1), villager.getPos().getiZ());
        jumped = true;
      } 
      if (!jumped && !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX(), villager.getPos().getiY(), villager.getPos().getiZ() + 1) && 
        !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX(), villager.getPos().getiY() + 2, villager.getPos().getiZ() + 1)) {
        villager.setPosition(villager.getPos().getiX(), (villager.getPos().getiY() + 1), (villager.getPos().getiZ() + 1));
        jumped = true;
      } 
      if (!jumped && !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX(), villager.getPos().getiY() + 1, villager.getPos().getiZ() - 1) && 
        !WorldUtilities.isBlockFullCube(world, villager.getPos().getiX(), villager.getPos().getiY() + 2, villager.getPos().getiZ() - 1)) {
        villager.setPosition(villager.getPos().getiX(), (villager.getPos().getiY() + 1), (villager.getPos().getiZ() - 1));
        jumped = true;
      } 
      if (!jumped && MillConfigValues.LogWifeAI >= 1)
        MillLog.major(villager, "Tried jumping in construction but couldn't"); 
    } 
    boolean blockSet = bblock.build(villager.world, villager.getTownHall(), false, false);
    while (!blockSet && cip.areBlocksLeft()) {
      cip.incrementBblockPos();
      BuildingBlock bb = cip.getCurrentBlock();
      if (bb != null && !bb.alreadyDone(villager.world))
        blockSet = bb.build(villager.world, villager.getTownHall(), false, false); 
    } 
    villager.swingArm(EnumHand.MAIN_HAND);
    villager.actionStart = 0L;
    boolean foundNextBlock = false;
    while (!foundNextBlock && cip.areBlocksLeft()) {
      cip.incrementBblockPos();
      BuildingBlock bb = cip.getCurrentBlock();
      if (bb != null && !bb.alreadyDone(villager.world)) {
        villager.setGoalDestPoint(bb.p);
        foundNextBlock = true;
      } 
    } 
    if (!cip.areBlocksLeft()) {
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(this, "Villager " + villager + " laid last block in " + (cip.getBuildingLocation()).planKey + " at " + bblock.p); 
      cip.clearBblocks();
      BuildingPlan plan = villager.getTownHall().getBuildingPlanForConstruction(cip);
      for (InvItem key : plan.resCost.keySet())
        villager.takeFromInv(key.getItem(), key.meta, ((Integer)plan.resCost.get(key)).intValue()); 
      if (cip.getBuildingLocation() != null)
        if ((cip.getBuildingLocation()).level == 0) {
          villager.getTownHall().initialiseConstruction(cip, (cip.getBuildingLocation()).chestPos);
        } else {
          Building building = cip.getBuildingLocation().getBuilding(villager.world);
          if (building != null)
            plan.updateBuildingForPlan(building); 
        }  
    } 
    if (!foundNextBlock)
      villager.setGoalDestPoint(null); 
    if (MillConfigValues.LogWifeAI >= 2 && villager.extraLog)
      MillLog.minor(villager, "Reseting actionStart after " + (villager.world.getWorldTime() - villager.actionStart)); 
    return !cip.areBlocksLeft();
  }
  
  public int priority(MillVillager villager) {
    return 1500;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
  
  public boolean stopMovingWhileWorking() {
    return false;
  }
  
  public boolean stuckAction(MillVillager villager) throws MillLog.MillenaireException {
    if (villager.getGoalDestPoint().horizontalDistanceTo((Entity)villager) < 30.0D) {
      if (MillConfigValues.LogWifeAI >= 2)
        MillLog.major(villager, "Putting block at a distance: " + villager.getGoalDestPoint().distanceTo((Entity)villager)); 
      performAction(villager);
      return true;
    } 
    return false;
  }
  
  public long stuckDelay(MillVillager villager) {
    return 100L;
  }
  
  public boolean unreachableDestination(MillVillager villager) throws Exception {
    performAction(villager);
    return true;
  }
}
