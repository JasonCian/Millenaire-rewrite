package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.Point;

@Documentation("Plant cacao seeds at home.")
public class GoalPlantCacao extends Goal {
  private static ItemStack[] cacao = new ItemStack[] { new ItemStack(Blocks.COCOA, 1) };
  
  private int getCocoaMeta(World world, Point p) {
    Block var5 = p.getRelative(0.0D, 0.0D, -1.0D).getBlock(world);
    Block var6 = p.getRelative(0.0D, 0.0D, 1.0D).getBlock(world);
    Block var7 = p.getRelative(-1.0D, 0.0D, 0.0D).getBlock(world);
    Block var8 = p.getRelative(1.0D, 0.0D, 0.0D).getBlock(world);
    byte meta = 0;
    if (var5 == Blocks.LOG)
      meta = 2; 
    if (var6 == Blocks.LOG)
      meta = 0; 
    if (var7 == Blocks.LOG)
      meta = 1; 
    if (var8 == Blocks.LOG)
      meta = 3; 
    return meta;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    Point p = villager.getHouse().getResManager().getCocoaPlantingLocation();
    return packDest(p, villager.getHouse());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return cacao;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (getDestination(villager).getDest() != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    Block block = villager.getBlock(villager.getGoalDestPoint());
    Point cropPoint = villager.getGoalDestPoint();
    block = villager.getBlock(cropPoint);
    if (block == Blocks.AIR) {
      villager.setBlockAndMetadata(cropPoint, Blocks.COCOA, getCocoaMeta(villager.world, cropPoint));
      villager.swingArm(EnumHand.MAIN_HAND);
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 120;
  }
}
