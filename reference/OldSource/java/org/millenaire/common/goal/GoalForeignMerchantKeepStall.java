package org.millenaire.common.goal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;

@Documentation("For market merchants, hold their stalls so the player can trade with them.")
public class GoalForeignMerchantKeepStall extends Goal {
  private static ItemStack[] PURSE = new ItemStack[] { new ItemStack((Item)MillItems.PURSE, 1) };
  
  private static ItemStack[] DENIER_ARGENT = new ItemStack[] { new ItemStack((Item)MillItems.DENIER_ARGENT, 1) };
  
  public int actionDuration(MillVillager villager) throws Exception {
    return 1200;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    if (villager.foreignMerchantStallId >= (villager.getHouse().getResManager()).stalls.size())
      return null; 
    return packDest((villager.getHouse().getResManager()).stalls.get(villager.foreignMerchantStallId), villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
    return DENIER_ARGENT;
  }
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) throws Exception {
    return PURSE;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return true;
  }
  
  public boolean lookAtPlayer() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    return MillCommonUtilities.chanceOn(600);
  }
  
  public int priority(MillVillager villager) throws Exception {
    return MillCommonUtilities.randomInt(50);
  }
}
