package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.village.Building;

@Documentation("Fish from fishing holes at home, bringing in standard fish.")
public class GoalFish extends Goal {
  private static ItemStack[] fishingRod = new ItemStack[] { new ItemStack((Item)Items.FISHING_ROD, 1) };
  
  public GoalFish() {
    this.buildingLimit.put(InvItem.createInvItem(Items.FISH, 0), Integer.valueOf(128));
    this.buildingLimit.put(InvItem.createInvItem(Items.COOKED_FISH, 0), Integer.valueOf(128));
    this.icon = InvItem.createInvItem((Item)Items.FISHING_ROD);
  }
  
  public int actionDuration(MillVillager villager) {
    return 500;
  }
  
  protected void addFishResults(MillVillager villager) {
    villager.addToInv(Items.FISH, 1);
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Building> vb = villager.getTownHall().getBuildingsWithTag("fishingspot");
    Building closest = null;
    for (Building b : vb) {
      if (closest == null || villager
        .getPos().horizontalDistanceToSquared(b.getResManager().getSleepingPos()) < villager.getPos().horizontalDistanceToSquared(closest.getResManager().getSleepingPos()))
        closest = b; 
    } 
    if (closest == null || (closest.getResManager()).fishingspots.size() == 0)
      return null; 
    return packDest((closest.getResManager()).fishingspots.get(MillCommonUtilities.randomInt((closest.getResManager()).fishingspots.size())), closest);
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    return fishingRod;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    for (Building b : villager.getTownHall().getBuildings()) {
      if ((b.getResManager()).fishingspots.size() > 0)
        return true; 
    } 
    return false;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    addFishResults(villager);
    villager.swingArm(EnumHand.MAIN_HAND);
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    if (villager.getGoalBuildingDest() == null)
      return 20; 
    return 100 - villager.getGoalBuildingDest().countGoods(Items.FISH);
  }
  
  public boolean stuckAction(MillVillager villager) throws Exception {
    return performAction(villager);
  }
}
