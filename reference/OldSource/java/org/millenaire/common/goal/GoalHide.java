package org.millenaire.common.goal;

import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;

@Documentation("Makes villagers hide in the town hall during a raid.")
public class GoalHide extends Goal {
  public boolean canBeDoneAtNight() {
    return true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    if (villager.getPos().distanceToSquared(villager.getTownHall().getResManager().getShelterPos()) <= 9.0D)
      return null; 
    return packDest(villager.getTownHall().getResManager().getShelterPos(), villager.getTownHall());
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return false;
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
}
