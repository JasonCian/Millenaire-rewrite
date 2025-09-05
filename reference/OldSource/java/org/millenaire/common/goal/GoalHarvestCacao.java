package org.millenaire.common.goal;

import net.minecraft.block.BlockCocoa;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.Point;

@Documentation("Goal that harvests ripe cacao.")
public class GoalHarvestCacao extends Goal {
  private static ItemStack[] CACAO = new ItemStack[] { new ItemStack(Items.DYE, 1, 3) };
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    Point p = villager.getHouse().getResManager().getCocoaHarvestLocation();
    return packDest(p, villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) {
    return CACAO;
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
    Point cropPoint = villager.getGoalDestPoint();
    if (cropPoint.getBlock(villager.world) == Blocks.COCOA) {
      IBlockState bs = cropPoint.getBlockActualState(villager.world);
      if (((Integer)bs.getValue((IProperty)BlockCocoa.AGE)).intValue() >= 2) {
        villager.setBlockAndMetadata(cropPoint, Blocks.AIR, 0);
        int nbcrop = 2;
        float irrigation = villager.getTownHall().getVillageIrrigation();
        double rand = Math.random();
        if (rand < (irrigation / 100.0F))
          nbcrop++; 
        villager.addToInv(Items.DYE, 3, nbcrop);
        villager.swingArm(EnumHand.MAIN_HAND);
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 100;
  }
}
