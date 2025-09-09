package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;

@Documentation("Brings resources home from the villager's inventory. Typically used to bring back resources produced by mining, crafting etc.")
public class GoalBringBackResourcesHome extends Goal {
  public int actionDuration(MillVillager villager) {
    return 40;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    List<ItemStack> items = new ArrayList<>();
    for (InvItem key : villager.getInventoryKeys()) {
      for (InvItem key2 : villager.getGoodsToBringBackHome()) {
        if (key2.equals(key))
          items.add(new ItemStack(key.getItem(), 1, key.meta)); 
      } 
    } 
    return items.<ItemStack>toArray(new ItemStack[items.size()]);
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    boolean delayOver;
    if (villager.getGoodsToBringBackHome().size() == 0)
      return false; 
    int nb = 0;
    if (!villager.lastGoalTime.containsKey(this)) {
      delayOver = true;
    } else {
      delayOver = (villager.world.getWorldTime() > ((Long)villager.lastGoalTime.get(this)).longValue() + 2000L);
    } 
    for (InvItem key : villager.getInventoryKeys()) {
      if (villager.countInv(key) > 0)
        for (InvItem key2 : villager.getGoodsToBringBackHome()) {
          if (key2.matches(key)) {
            nb += villager.countInv(key);
            if (delayOver)
              return true; 
            if (nb > 16)
              return true; 
          } 
        }  
    } 
    return false;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    for (InvItem key : villager.getInventoryKeys()) {
      for (InvItem key2 : villager.getGoodsToBringBackHome()) {
        if (key2.matches(key))
          villager.putInBuilding(villager.getHouse(), key.getItem(), key.meta, 256); 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    int nbGoods = 0;
    for (InvItem key : villager.getInventoryKeys()) {
      for (InvItem key2 : villager.getGoodsToBringBackHome()) {
        if (key2.matches(key))
          nbGoods += villager.countInv(key); 
      } 
    } 
    return 10 + nbGoods * 3;
  }
}
