package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;

@Documentation("Deliver to the villager's house goods required by the household, like food or crafting inputs. Paired with getgoodshousehold.")
public class GoalDeliverGoodsHousehold extends Goal {
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    List<ItemStack> items = new ArrayList<>();
    for (MillVillager v : villager.getHouse().getKnownVillagers()) {
      for (InvItem key : v.requiresGoods().keySet()) {
        if (villager.countInv(key.getItem(), key.meta) > 0)
          items.add(new ItemStack(key.getItem(), 1, key.meta)); 
      } 
    } 
    return items.<ItemStack>toArray(new ItemStack[items.size()]);
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return false;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    for (MillVillager v : villager.getHouse().getKnownVillagers()) {
      for (InvItem key : v.requiresGoods().keySet())
        villager.putInBuilding(villager.getHouse(), key.getItem(), key.meta, 256); 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 100;
  }
}
