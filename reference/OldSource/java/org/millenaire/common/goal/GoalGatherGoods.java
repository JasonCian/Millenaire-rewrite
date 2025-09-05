package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

@Documentation("Gather items around the villager, if they are of a type declared for that villager. For example, saplings for lumbermen.")
public class GoalGatherGoods extends Goal {
  public int actionDuration(MillVillager villager) throws Exception {
    return 40;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    EntityItem item = villager.getClosestItemVertical(villager.getGoodsToCollect(), villager.getGatheringRange(), 10);
    if (item == null)
      return null; 
    return packDest(new Point((Entity)item));
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_WIDE_NO_LEAVES; 
    return JPS_CONFIG_WIDE;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    if (villager.getGoodsToCollect().size() == 0)
      return false; 
    EntityItem item = villager.getClosestItemVertical(villager.getGoodsToCollect(), villager.getGatheringRange(), 10);
    return (item != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) {
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 500;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
  
  public boolean stuckAction(MillVillager villager) {
    List<InvItem> goods = villager.getGoodsToCollect();
    if (goods != null) {
      EntityItem item = WorldUtilities.getClosestItemVertical(villager.world, villager.getGoalDestPoint(), goods, 3, 20);
      if (item != null) {
        item.setDead();
        villager.addToInv(item.getItem().getItem(), item.getItem().getItemDamage(), 1);
        return true;
      } 
    } 
    return false;
  }
}
