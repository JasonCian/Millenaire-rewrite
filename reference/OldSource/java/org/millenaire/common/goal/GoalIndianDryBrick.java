package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@Documentation("Go and dry bricks in a building with the kiln tag.")
public class GoalIndianDryBrick extends Goal {
  public GoalIndianDryBrick() {
    this.maxSimultaneousInBuilding = 1;
    this.tags.add("tag_construction");
    this.icon = InvItem.createInvItem(MillBlocks.BS_WET_BRICK);
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
        int nb = kiln.getResManager().getNbEmptyBrickLocation();
        boolean validTarget = false;
        if (nb > 0 && minimumBricksNotRequired)
          validTarget = true; 
        if (nb > 4)
          validTarget = true; 
        if (validTarget) {
          Point point = kiln.getResManager().getEmptyBrickLocation();
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
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return new ItemStack[] { new ItemStack((Item)MillItems.BRICK_MOULD, 1, 0) };
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws MillLog.MillenaireException {
    return (getDestination(villager) != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws MillLog.MillenaireException {
    if (WorldUtilities.getBlock(villager.world, villager.getGoalDestPoint()) == Blocks.AIR) {
      villager.setBlockstate(villager.getGoalDestPoint(), MillBlocks.BS_WET_BRICK);
      villager.swingArm(EnumHand.MAIN_HAND);
    } 
    if (villager.getGoalBuildingDest().getResManager().getNbEmptyBrickLocation() > 0) {
      villager.setGoalInformation(getDestination(villager));
      return false;
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    int p = 120;
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
