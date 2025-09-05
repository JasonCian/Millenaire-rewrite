package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.MillLog;

@Documentation("For local merchants, drop off picked up goods at the Inn for export and take goods for import.")
public class GoalMerchantVisitInn extends Goal {
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    return packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    List<ItemStack> items = new ArrayList<>();
    for (InvItem good : villager.getInventoryKeys()) {
      if (villager.countInv(good.getItem(), good.meta) > 0)
        items.add(new ItemStack(good.getItem(), 1, good.meta)); 
    } 
    return items.<ItemStack>toArray(new ItemStack[items.size()]);
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    boolean delayOver;
    if (!villager.lastGoalTime.containsKey(this)) {
      delayOver = true;
    } else {
      delayOver = (villager.world.getWorldTime() > ((Long)villager.lastGoalTime.get(this)).longValue() + 2000L);
    } 
    int nb = 0;
    for (InvItem good : villager.getInventoryKeys()) {
      int nbcount = villager.countInv(good.getItem(), good.meta);
      if (nbcount > 0 && villager.getTownHall().nbGoodNeeded(good.getItem(), good.meta) == 0) {
        nb += nbcount;
        if (delayOver)
          return true; 
        if (nb > 64)
          return true; 
      } 
    } 
    for (TradeGood good : (villager.getTownHall()).culture.goodsList) {
      if (good.item.meta >= 0 && 
        villager.getHouse().countGoods(good.item.getItem(), good.item.meta) > 0 && villager
        .countInv(good.item.getItem(), good.item.meta) < villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta)) {
        if (MillConfigValues.LogMerchant >= 1)
          MillLog.major(this, "Visiting the Inn to take imports"); 
        return true;
      } 
    } 
    return false;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    String s = "";
    for (InvItem invItem : villager.getInventoryKeys()) {
      if (villager.countInv(invItem.getItem(), invItem.meta) > 0 && villager.getTownHall().nbGoodNeeded(invItem.getItem(), invItem.meta) == 0) {
        int nb = villager.putInBuilding(villager.getHouse(), invItem.getItem(), invItem.meta, 99999999);
        if (villager.getCulture().getTradeGood(invItem) != null && 
          nb > 0)
          s = s + ";" + (villager.getCulture().getTradeGood(invItem)).key + "/" + nb; 
      } 
    } 
    if (s.length() > 0)
      (villager.getHouse()).visitorsList.add("storedexports;" + villager.getName() + s); 
    s = "";
    for (TradeGood good : (villager.getTownHall()).culture.goodsList) {
      if (good.item.meta >= 0) {
        int nbNeeded = villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta);
        if (villager.countInv(good.item.getItem(), good.item.meta) < nbNeeded) {
          int nb = villager.takeFromBuilding(villager.getHouse(), good.item.getItem(), good.item.meta, nbNeeded - villager.countInv(good.item.getItem(), good.item.meta));
          if (nb > 0)
            s = s + ";" + good.key + "/" + nb; 
        } 
      } 
    } 
    if (s.length() > 0)
      (villager.getHouse()).visitorsList.add("broughtimport;" + villager.getName() + s); 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 100;
  }
}
