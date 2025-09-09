package org.millenaire.common.goal;

import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

@Documentation("Go and get an item from a shop to keep in the villager's inventory. Typically a tool, or a new cloth in some cultures.")
public class GoalGetTool extends Goal {
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    for (Building shop : villager.getTownHall().getShops()) {
      boolean validShop = testShopValidity(villager, shop);
      if (validShop)
        return packDest(shop.getResManager().getSellingPos(), shop); 
    } 
    return null;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    for (Building shop : villager.getTownHall().getShops()) {
      boolean validShop = testShopValidity(villager, shop);
      if (validShop)
        return true; 
    } 
    return false;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Building shop = villager.getGoalBuildingDest();
    if (shop == null)
      return true; 
    for (InvItem key : villager.getItemsNeeded()) {
      if (villager.countInv(key.getItem(), key.meta) == 0 && shop.countGoods(key.getItem(), key.meta) > 0 && validateDest(villager, shop))
        villager.takeFromBuilding(shop, key.getItem(), key.meta, 1); 
    } 
    for (String toolCategory : villager.getToolsCategoriesNeeded()) {
      InvItem bestItem = villager.getConfig().getBestItemByCategoryName(toolCategory, villager);
      for (InvItem key : (villager.getConfig()).categories.get(toolCategory)) {
        if (key == bestItem)
          break; 
        if (shop.countGoods(key.getItem(), key.meta) > 0 && validateDest(villager, shop))
          villager.takeFromBuilding(shop, key.getItem(), key.meta, 1); 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 500;
  }
  
  private boolean testShopValidity(MillVillager villager, Building shop) throws MillLog.MillenaireException {
    for (InvItem key : villager.getItemsNeeded()) {
      if (villager.countInv(key.getItem(), key.meta) == 0 && shop.countGoods(key.getItem(), key.meta) > 0 && validateDest(villager, shop))
        return true; 
    } 
    for (String toolCategory : villager.getToolsCategoriesNeeded()) {
      InvItem bestItem = villager.getConfig().getBestItemByCategoryName(toolCategory, villager);
      for (InvItem key : (villager.getConfig()).categories.get(toolCategory)) {
        if (key == bestItem)
          break; 
        if (shop.countGoods(key.getItem(), key.meta) > 0 && validateDest(villager, shop))
          return true; 
      } 
    } 
    return false;
  }
}
