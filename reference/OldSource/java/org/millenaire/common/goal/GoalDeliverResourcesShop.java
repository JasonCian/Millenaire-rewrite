package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.village.Building;

@Documentation("Deliver resources to a shop from the villager's inventory. Paired with gethousethresources that picks them up.")
public class GoalDeliverResourcesShop extends Goal {
  public int actionDuration(MillVillager villager) {
    return 40;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    return getDestination(villager, false);
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager, boolean test) {
    boolean delayOver;
    if (!test) {
      delayOver = true;
    } else if (!villager.lastGoalTime.containsKey(this)) {
      delayOver = true;
    } else {
      delayOver = (villager.world.getWorldTime() > ((Long)villager.lastGoalTime.get(this)).longValue() + 2000L);
    } 
    for (Building shop : villager.getTownHall().getShops()) {
      int nb = 0;
      if ((villager.getCulture()).shopNeeds.containsKey(shop.location.shop))
        for (InvItem item : (villager.getCulture()).shopNeeds.get(shop.location.shop)) {
          int nbcount = villager.countInv(item.getItem(), item.meta);
          if (nbcount > 0) {
            nb += nbcount;
            if (delayOver)
              return packDest(shop.getResManager().getSellingPos(), shop); 
            if (nb > 16)
              return packDest(shop.getResManager().getSellingPos(), shop); 
          } 
        }  
    } 
    return null;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    List<ItemStack> items = new ArrayList<>();
    Building shop = villager.getGoalBuildingDest();
    if (shop != null && 
      (villager.getCulture()).shopNeeds.containsKey(shop.location.shop))
      for (InvItem item : (villager.getCulture()).shopNeeds.get(shop.location.shop)) {
        if (villager.countInv(item.getItem(), item.meta) > 0)
          items.add(new ItemStack(item.getItem(), 1, item.meta)); 
      }  
    return items.<ItemStack>toArray(new ItemStack[items.size()]);
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (getDestination(villager, true) != null);
  }
  
  public boolean performAction(MillVillager villager) {
    Building shop = villager.getGoalBuildingDest();
    if (shop != null && 
      (villager.getCulture()).shopNeeds.containsKey(shop.location.shop))
      for (InvItem item : (villager.getCulture()).shopNeeds.get(shop.location.shop))
        villager.putInBuilding(shop, item.getItem(), item.meta, 256);  
    return true;
  }
  
  public int priority(MillVillager villager) {
    int priority = 0;
    for (Building shop : villager.getTownHall().getShops()) {
      if ((villager.getCulture()).shopNeeds.containsKey(shop.location.shop))
        for (InvItem item : (villager.getCulture()).shopNeeds.get(shop.location.shop))
          priority += villager.countInv(item.getItem(), item.meta) * 10;  
    } 
    return priority;
  }
}
