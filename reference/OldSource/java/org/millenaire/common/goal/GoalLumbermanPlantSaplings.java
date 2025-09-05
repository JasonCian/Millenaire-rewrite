package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@Documentation("Go plant saplings in a grove.")
public class GoalLumbermanPlantSaplings extends Goal {
  public int actionDuration(MillVillager villager) {
    return 20;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    List<Point> vp = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    for (Building grove : villager.getTownHall().getBuildingsWithTag("grove")) {
      Point point = grove.getResManager().getPlantingLocation();
      if (point != null) {
        vp.add(point);
        buildingp.add(grove.getPos());
      } 
    } 
    if (vp.isEmpty())
      return null; 
    Point p = vp.get(0);
    Point buildingP = buildingp.get(0);
    for (int i = 1; i < vp.size(); i++) {
      if (((Point)vp.get(i)).horizontalDistanceToSquared((Entity)villager) < p.horizontalDistanceToSquared((Entity)villager)) {
        p = vp.get(i);
        buildingP = buildingp.get(i);
      } 
    } 
    return packDest(p, buildingP);
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    String saplingType = villager.getGoalBuildingDest().getResManager().getPlantingLocationType(villager.getGoalDestPoint());
    int meta = 0;
    if ("pinespawn".equals(saplingType))
      meta = 1; 
    if ("birchspawn".equals(saplingType))
      meta = 2; 
    if ("junglespawn".equals(saplingType))
      meta = 3; 
    if ("acaciaspawn".equals(saplingType))
      meta = 4; 
    if ("darkoakspawn".equals(saplingType))
      meta = 5; 
    return new ItemStack[] { new ItemStack(Blocks.SAPLING, 1, meta) };
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_WIDE_NO_LEAVES; 
    return JPS_CONFIG_WIDE;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    for (Building grove : villager.getTownHall().getBuildingsWithTag("grove")) {
      Point p = grove.getResManager().getPlantingLocation();
      if (p != null)
        return true; 
    } 
    return false;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    Block block = WorldUtilities.getBlock(villager.world, villager.getGoalDestPoint());
    if (block == Blocks.AIR || block == Blocks.SNOW_LAYER || (BlockItemUtilities.isBlockDecorativePlant(block) && !(block instanceof net.minecraft.block.BlockSapling))) {
      String saplingType = villager.getGoalBuildingDest().getResManager().getPlantingLocationType(villager.getGoalDestPoint());
      int meta = 0;
      if ("pinespawn".equals(saplingType))
        meta = 1; 
      if ("birchspawn".equals(saplingType))
        meta = 2; 
      if ("junglespawn".equals(saplingType))
        meta = 3; 
      if ("acaciaspawn".equals(saplingType))
        meta = 4; 
      if ("darkoakspawn".equals(saplingType))
        meta = 5; 
      villager.takeFromInv(Blocks.SAPLING, meta, 1);
      villager.setBlockAndMetadata(villager.getGoalDestPoint(), Blocks.SAPLING, meta);
      villager.swingArm(EnumHand.MAIN_HAND);
      if (MillConfigValues.LogLumberman >= 3 && villager.extraLog)
        MillLog.debug(this, "Planted at: " + villager.getGoalDestPoint()); 
    } else if (MillConfigValues.LogLumberman >= 3 && villager.extraLog) {
      MillLog.debug(this, "Failed to plant at: " + villager.getGoalDestPoint());
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 120;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
}
