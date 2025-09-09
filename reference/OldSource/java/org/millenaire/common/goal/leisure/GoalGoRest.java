package org.millenaire.common.goal.leisure;

import net.minecraft.entity.Entity;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;

@Documentation("Makes the villager 'rest' at home. Has a priority of 0 and is there to ensure there is always something to do.")
public class GoalGoRest extends Goal {
  public int actionDuration(MillVillager villager) {
    return 200;
  }
  
  public boolean allowRandomMoves() {
    return true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    return packDest(villager.getHouse().getResManager().getSleepingPos());
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return (villager.getHouse().getPos().distanceTo((Entity)villager) > 5.0D);
  }
  
  public boolean performAction(MillVillager villager) {
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 0;
  }
  
  public int range(MillVillager villager) {
    return 10;
  }
}
