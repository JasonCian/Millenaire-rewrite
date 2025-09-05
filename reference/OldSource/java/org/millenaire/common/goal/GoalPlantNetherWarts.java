package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.Point;

@Documentation("Plant nether warts at home (for free).")
public class GoalPlantNetherWarts extends Goal {
  private static ItemStack[] WARTS = new ItemStack[] { new ItemStack(Items.NETHER_WART, 1) };
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    return packDest(villager.getHouse().getResManager().getNetherWartsPlantingLocation(), villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return WARTS;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (getDestination(villager).getDest() != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    Block block = villager.getBlock(villager.getGoalDestPoint());
    Point cropPoint = villager.getGoalDestPoint().getAbove();
    block = villager.getBlock(cropPoint);
    if (block == Blocks.AIR) {
      villager.setBlockAndMetadata(cropPoint, Blocks.NETHER_WART, 0);
      villager.swingArm(EnumHand.MAIN_HAND);
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 100;
  }
}
