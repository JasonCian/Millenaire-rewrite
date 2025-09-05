package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.Point;

@Documentation("Harvest grown nether warts froom home.")
public class GoalHarvestWarts extends Goal {
  private static ItemStack[] WARTS = new ItemStack[] { new ItemStack(Items.NETHER_WART, 1) };
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    return packDest(villager.getHouse().getResManager().getNetherWartsHarvestLocation(), villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) {
    return WARTS;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return villager.getBestHoeStack();
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (getDestination(villager).getDest() != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    Point cropPoint = villager.getGoalDestPoint().getAbove();
    if (villager.getBlock(cropPoint) == Blocks.NETHER_WART && villager.getBlockMeta(cropPoint) == 3) {
      villager.setBlockAndMetadata(cropPoint, Blocks.AIR, 0);
      villager.getHouse().storeGoods(Items.NETHER_WART, 1);
      villager.swingArm(EnumHand.MAIN_HAND);
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    int p = 100 - villager.getHouse().countGoods(Items.NETHER_WART) * 4;
    return p;
  }
}
