package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
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
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@Documentation("Go chop trees in a grove.")
public class GoalLumbermanChopTrees extends Goal {
  public GoalLumbermanChopTrees() {
    this.maxSimultaneousInBuilding = 1;
    this.townhallLimit.put(InvItem.createInvItem(Blocks.LOG, -1), Integer.valueOf(4096));
    this.icon = InvItem.createInvItem(Items.IRON_AXE);
  }
  
  public int actionDuration(MillVillager villager) {
    int toolEfficiency = (int)villager.getBestAxe().getDestroySpeed(new ItemStack((Item)villager.getBestAxe(), 1), Blocks.LOG.getDefaultState());
    return 20 - toolEfficiency * 2;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    List<Point> woodPos = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    for (Building grove : villager.getTownHall().getBuildingsWithTag("grove")) {
      if (grove.getWoodCount() > 4) {
        Point point = grove.getWoodLocation();
        if (point != null) {
          woodPos.add(point);
          buildingp.add(grove.getPos());
          if (MillConfigValues.LogLumberman >= 3)
            MillLog.debug(this, "Found location in grove: " + point + ". Targeted block: " + point.getBlock(villager.world)); 
        } 
      } 
    } 
    if (woodPos.isEmpty())
      return null; 
    Point p = woodPos.get(0);
    Point buildingP = buildingp.get(0);
    for (int i = 1; i < woodPos.size(); i++) {
      if (((Point)woodPos.get(i)).horizontalDistanceToSquared((Entity)villager) < p.horizontalDistanceToSquared((Entity)villager)) {
        p = woodPos.get(i);
        buildingP = buildingp.get(i);
      } 
    } 
    if (MillConfigValues.LogLumberman >= 3)
      MillLog.debug(this, "Going to gather wood around: " + p + ". Targeted block: " + p.getBlock(villager.world)); 
    return packDest(p, buildingP);
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return villager.getBestAxeStack();
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_CHOPLUMBER_NO_LEAVES; 
    return JPS_CONFIG_CHOPLUMBER;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    if (villager.countInv(Blocks.LOG, -1) > 64)
      return false; 
    if (getDestination(villager) == null)
      return false; 
    return true;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    boolean woodFound = false;
    if (MillConfigValues.LogLumberman >= 3)
      MillLog.debug(this, "Attempting to gather wood around: " + villager.getGoalDestPoint() + ", central block: " + villager.getGoalDestPoint().getBlock(villager.world)); 
    for (int deltaY = 12; deltaY > -12; deltaY--) {
      for (int deltaX = -3; deltaX < 4; deltaX++) {
        for (int deltaZ = -3; deltaZ < 4; deltaZ++) {
          Point p = villager.getGoalDestPoint().getRelative(deltaX, deltaY, deltaZ);
          Block block = villager.getBlock(p);
          if (block == Blocks.LOG || block == Blocks.LOG2 || block == Blocks.LEAVES || block == Blocks.LEAVES2)
            if (!woodFound) {
              if (block == Blocks.LOG || block == Blocks.LOG2) {
                int meta = villager.getBlockMeta(p) & 0x3;
                villager.setBlock(p, Blocks.AIR);
                villager.swingArm(EnumHand.MAIN_HAND);
                if (block == Blocks.LOG) {
                  villager.addToInv(Blocks.LOG, meta, 1);
                } else {
                  villager.addToInv(Blocks.LOG2, meta, 1);
                } 
                woodFound = true;
                if (MillConfigValues.LogLumberman >= 3)
                  MillLog.debug(this, "Gathered wood at: " + p); 
              } else {
                int meta = WorldUtilities.getBlockMeta(villager.world, p);
                if (block == Blocks.LEAVES) {
                  if (MillCommonUtilities.randomInt(4) == 0)
                    villager.addToInv(Blocks.SAPLING, meta & 0x3, 1); 
                } else if ((meta & 0x3) == 0) {
                  if (MillCommonUtilities.randomInt(4) == 0)
                    villager.addToInv(Blocks.SAPLING, 4, 1); 
                } else if (MillCommonUtilities.randomInt(2) == 0) {
                  villager.addToInv(Blocks.SAPLING, 5, 1);
                } 
                villager.setBlock(p, Blocks.AIR);
                villager.swingArm(EnumHand.MAIN_HAND);
                if (MillConfigValues.LogLumberman >= 3)
                  MillLog.debug(this, "Destroyed leaves at: " + p); 
              } 
            } else {
              if (MillConfigValues.LogLumberman >= 3)
                MillLog.debug(this, "More wood found."); 
              return false;
            }  
        } 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    return Math.max(10, 125 - villager.countInv(Blocks.LOG, -1));
  }
  
  public int range(MillVillager villager) {
    return 8;
  }
}
