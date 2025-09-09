package org.millenaire.common.goal;

import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;

@Documentation("Goal for village raiders, active when in the target village.")
public class GoalRaidVillage extends Goal {
  public boolean autoInterruptIfNoTarget() {
    return false;
  }
  
  public boolean canBeDoneAtNight() {
    return true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    return packDest(villager.getTownHall().getResManager().getDefendingPos(), villager.getTownHall());
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return new ItemStack[] { villager.getWeapon() };
  }
  
  public boolean isFightingGoal() {
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
