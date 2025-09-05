package org.millenaire.common.goal;

import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;

@Documentation("Defend the village when attacked in a raid.")
public class GoalDefendVillage extends Goal {
  public boolean autoInterruptIfNoTarget() {
    return false;
  }
  
  public boolean canBeDoneAtNight() {
    return true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    if (villager.getPos().distanceToSquared(villager.getTownHall().getResManager().getDefendingPos()) <= 9.0D)
      return null; 
    return packDest(villager.getTownHall().getResManager().getDefendingPos(), villager.getTownHall());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return new ItemStack[] { villager.getWeapon() };
  }
  
  public boolean isFightingGoal() {
    return true;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return true;
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    return (villager.getTownHall()).underAttack;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    return false;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 0;
  }
  
  public int range(MillVillager villager) {
    return 1;
  }
}
