package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

@Documentation("For local merchants, pick up goods from village shops for exports and drop off goods brought from other villages.")
public class GoalMerchantVisitBuilding extends Goal {
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    for (TradeGood good : (villager.getTownHall()).culture.goodsList) {
      if (good.item.meta >= 0 && 
        villager.countInv(good.item.getItem(), good.item.meta) > 0 && villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta) > 0) {
        if (MillConfigValues.LogMerchant >= 3)
          MillLog.debug(villager, "TH needs " + villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta) + " good " + good.item.getName() + ", merchant has " + villager
              .countInv(good.item.getItem(), good.item.meta)); 
        return packDest(villager.getTownHall().getResManager().getSellingPos(), villager.getTownHall());
      } 
    } 
    HashMap<TradeGood, Integer> neededGoods = villager.getTownHall().getImportsNeededbyOtherVillages();
    for (Building shop : villager.getTownHall().getShops()) {
      for (TradeGood good : (villager.getTownHall()).culture.goodsList) {
        if (good.item.meta >= 0 && 
          !shop.isInn && shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false) > 0 && neededGoods.containsKey(good) && ((Integer)neededGoods
          .get(good)).intValue() > villager.getHouse().countGoods(good.item.getItem(), good.item.meta) + villager.countInv(good.item.getItem(), good.item.meta)) {
          if (MillConfigValues.LogMerchant >= 3)
            MillLog.debug(villager, "Shop " + shop + " has " + shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false) + " good to pick up."); 
          return packDest(shop.getResManager().getSellingPos(), shop);
        } 
      } 
    } 
    return null;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    List<ItemStack> items = new ArrayList<>();
    for (InvItem item : villager.getInventoryKeys()) {
      if (villager.countInv(item) > 0)
        items.add(new ItemStack(item.getItem(), 1, item.meta)); 
    } 
    return items.<ItemStack>toArray(new ItemStack[items.size()]);
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Building shop = villager.getGoalBuildingDest();
    HashMap<TradeGood, Integer> neededGoods = villager.getTownHall().getImportsNeededbyOtherVillages();
    if (shop == null || shop.isInn)
      return true; 
    if (shop.isTownhall)
      for (TradeGood good : (villager.getTownHall()).culture.goodsList) {
        if (good.item.meta >= 0) {
          int nbNeeded = shop.nbGoodNeeded(good.item.getItem(), good.item.meta);
          if (nbNeeded > 0) {
            int nb = villager.putInBuilding(shop, good.item.getItem(), good.item.meta, nbNeeded);
            if (nb > 0 && MillConfigValues.LogMerchant >= 2)
              MillLog.minor(shop, villager + " delivered " + nb + " " + good.getName() + "."); 
          } 
        } 
      }  
    for (TradeGood good : (villager.getTownHall()).culture.goodsList) {
      if (good.item.meta >= 0 && 
        neededGoods.containsKey(good) && 
        shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false) > 0 && villager
        .getHouse().countGoods(good.item.getItem(), good.item.meta) + villager.countInv(good.item.getItem(), good.item.meta) < ((Integer)neededGoods.get(good)).intValue()) {
        int nb = Math.min(shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false), ((Integer)neededGoods
            .get(good)).intValue() - villager.getHouse().countGoods(good.item.getItem(), good.item.meta) - villager.countInv(good.item.getItem(), good.item.meta));
        nb = villager.takeFromBuilding(shop, good.item.getItem(), good.item.meta, nb);
        if (MillConfigValues.LogMerchant >= 2)
          MillLog.minor(shop, villager + " took " + nb + " " + good.getName() + " for trading."); 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 100;
  }
}
