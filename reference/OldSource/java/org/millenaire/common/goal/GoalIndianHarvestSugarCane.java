package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@Documentation("Gather sugar cane from a building with the sugar cane plantation tag.")
public class GoalIndianHarvestSugarCane extends Goal {
  private static ItemStack[] SUGARCANE = new ItemStack[] { new ItemStack(Items.REEDS, 1) };
  
  public GoalIndianHarvestSugarCane() {
    this.tags.add("tag_agriculture");
    this.icon = InvItem.createInvItem(Items.REEDS);
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    List<Point> vp = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    for (Building plantation : villager.getTownHall().getBuildingsWithTag("sugarplantation")) {
      Point point = plantation.getResManager().getSugarCaneHarvestLocation();
      if (point != null) {
        vp.add(point);
        buildingp.add(plantation.getPos());
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
    return SUGARCANE;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return villager.getBestHoeStack();
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    boolean delayOver;
    int nbsimultaneous = 0;
    for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
      if (v != villager && this.key.equals(v.goalKey))
        nbsimultaneous++; 
    } 
    if (nbsimultaneous > 2)
      return false; 
    if (!villager.lastGoalTime.containsKey(this)) {
      delayOver = true;
    } else {
      delayOver = (villager.world.getWorldTime() > ((Long)villager.lastGoalTime.get(this)).longValue() + 2000L);
    } 
    for (Building kiln : villager.getTownHall().getBuildingsWithTag("sugarplantation")) {
      int nb = kiln.getResManager().getNbSugarCaneHarvestLocation();
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
    Point cropPoint = villager.getGoalDestPoint().getRelative(0.0D, 3.0D, 0.0D);
    if (villager.getBlock(cropPoint) == Blocks.REEDS) {
      villager.setBlockAndMetadata(cropPoint, Blocks.AIR, 0);
      int nbcrop = 1;
      float irrigation = villager.getTownHall().getVillageIrrigation();
      double rand = Math.random();
      if (rand < (irrigation / 100.0F))
        nbcrop++; 
      villager.addToInv(Items.REEDS, nbcrop);
    } 
    cropPoint = villager.getGoalDestPoint().getRelative(0.0D, 2.0D, 0.0D);
    if (villager.getBlock(cropPoint) == Blocks.REEDS) {
      villager.setBlockAndMetadata(cropPoint, Blocks.AIR, 0);
      int nbcrop = 1;
      float irrigation = villager.getTownHall().getVillageIrrigation();
      double rand = Math.random();
      if (rand < (irrigation / 100.0F))
        nbcrop++; 
      villager.swingArm(EnumHand.MAIN_HAND);
      villager.addToInv(Items.REEDS, nbcrop);
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    int p = 200 - villager.getTownHall().nbGoodAvailable(Items.REEDS, 0, false, false, false) * 4;
    for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
      if (this.key.equals(v.goalKey))
        p /= 2; 
    } 
    return p;
  }
}
