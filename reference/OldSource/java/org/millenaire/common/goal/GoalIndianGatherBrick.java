package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@Documentation("Pick up dried bricks from a kiln.")
public class GoalIndianGatherBrick extends Goal {
  private static ItemStack[] MUD_BRICK = new ItemStack[] { BlockItemUtilities.getItemStackFromBlockState(MillBlocks.BS_MUD_BRICK, 1) };
  
  public GoalIndianGatherBrick() {
    this.maxSimultaneousInBuilding = 1;
    this.townhallLimit.put(InvItem.createInvItem(MillBlocks.BS_MUD_BRICK), Integer.valueOf(4096));
    this.tags.add("tag_construction");
    this.icon = InvItem.createInvItem(MillBlocks.BS_MUD_BRICK);
  }
  
  public int actionDuration(MillVillager villager) {
    return 20;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
    boolean minimumBricksNotRequired;
    if (villager.goalKey != null && villager.goalKey.equals(this.key)) {
      minimumBricksNotRequired = true;
    } else if (!villager.lastGoalTime.containsKey(this)) {
      minimumBricksNotRequired = true;
    } else {
      minimumBricksNotRequired = (villager.world.getWorldTime() > ((Long)villager.lastGoalTime.get(this)).longValue() + 2000L);
    } 
    List<Point> vp = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    for (Building kiln : villager.getTownHall().getBuildingsWithTag("brickkiln")) {
      if (validateDest(villager, kiln)) {
        int nb = kiln.getResManager().getNbFullBrickLocation();
        boolean validTarget = false;
        if (nb > 0 && minimumBricksNotRequired)
          validTarget = true; 
        if (nb > 4)
          validTarget = true; 
        if (validTarget) {
          Point point = kiln.getResManager().getFullBrickLocation();
          if (point != null) {
            vp.add(point);
            buildingp.add(kiln.getPos());
          } 
        } 
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
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) {
    return MUD_BRICK;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return villager.getBestPickaxeStack();
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws MillLog.MillenaireException {
    return (getDestination(villager) != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws MillLog.MillenaireException {
    if (WorldUtilities.getBlockState(villager.world, villager.getGoalDestPoint()) == MillBlocks.BS_MUD_BRICK) {
      villager.addToInv(MillBlocks.BS_MUD_BRICK, 1);
      villager.setBlockAndMetadata(villager.getGoalDestPoint(), Blocks.AIR, 0);
      villager.swingArm(EnumHand.MAIN_HAND);
    } 
    if (villager.getGoalBuildingDest().getResManager().getNbFullBrickLocation() > 0) {
      villager.setGoalInformation(getDestination(villager));
      return false;
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    int p = 100 - villager.getTownHall().nbGoodAvailable(MillBlocks.BS_MUD_BRICK, false, false, false) * 2;
    for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
      if (this.key.equals(v.goalKey))
        p /= 2; 
    } 
    return p;
  }
  
  public boolean unreachableDestination(MillVillager villager) {
    return false;
  }
}
