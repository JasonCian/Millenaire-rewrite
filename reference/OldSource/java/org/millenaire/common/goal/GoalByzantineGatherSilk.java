package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.BlockSilkWorm;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@Documentation("Gather ripe silk from a silk block.")
public class GoalByzantineGatherSilk extends Goal {
  private static ItemStack[] SHEARS = new ItemStack[] { new ItemStack((Item)Items.SHEARS, 1) };
  
  private static ItemStack[] SILK = new ItemStack[] { new ItemStack((Item)MillItems.SILK, 1) };
  
  public GoalByzantineGatherSilk() {
    this.maxSimultaneousInBuilding = 2;
    this.buildingLimit.put(InvItem.createInvItem((Item)MillItems.SILK), Integer.valueOf(128));
    this.townhallLimit.put(InvItem.createInvItem((Item)MillItems.SILK), Integer.valueOf(128));
    this.icon = InvItem.createInvItem((Item)MillItems.SILK);
  }
  
  public int actionDuration(MillVillager villager) {
    return 20;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    List<Point> vp = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    for (Building silkFarm : villager.getTownHall().getBuildingsWithTag("silkwormfarm")) {
      Point point = silkFarm.getResManager().getSilkwormHarvestLocation();
      if (point != null) {
        vp.add(point);
        buildingp.add(silkFarm.getPos());
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
    return SILK;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return SHEARS;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    boolean delayOver;
    if (!villager.lastGoalTime.containsKey(this)) {
      delayOver = true;
    } else {
      delayOver = (villager.world.getWorldTime() > ((Long)villager.lastGoalTime.get(this)).longValue() + 2000L);
    } 
    for (Building kiln : villager.getTownHall().getBuildingsWithTag("silkwormfarm")) {
      int nb = kiln.getResManager().getNbSilkWormHarvestLocation();
      if (nb > 0 && delayOver)
        return true; 
      if (nb > 4)
        return true; 
    } 
    return false;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    if (WorldUtilities.getBlock(villager.world, villager.getGoalDestPoint()) == MillBlocks.SILK_WORM && 
      WorldUtilities.getBlockState(villager.world, villager.getGoalDestPoint()).getValue((IProperty)BlockSilkWorm.PROGRESS) == BlockSilkWorm.EnumType.SILKWORMFULL) {
      villager.addToInv((Item)MillItems.SILK, 0, 1);
      villager.setBlockAndMetadata(villager.getGoalDestPoint(), (Block)MillBlocks.SILK_WORM, 0);
      villager.swingArm(EnumHand.MAIN_HAND);
      return false;
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    int p = 100 - villager.getTownHall().nbGoodAvailable(InvItem.createInvItem((Item)MillItems.SILK, 1), false, false, false) * 2;
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
