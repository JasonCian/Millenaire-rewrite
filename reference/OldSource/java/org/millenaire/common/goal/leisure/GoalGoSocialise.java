package org.millenaire.common.goal.leisure;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@Documentation("Makes villagers go to buildings with the leisure tag, so they can meet people to chat with.")
public class GoalGoSocialise extends Goal {
  public int actionDuration(MillVillager villager) {
    return 200;
  }
  
  public boolean allowRandomMoves() {
    return true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    Point dest = null;
    Building destB = null;
    List<Building> possibleDests = new ArrayList<>();
    for (Building b : villager.getTownHall().getBuildings()) {
      if (b.containsTags("leasure"))
        possibleDests.add(b); 
    } 
    if (possibleDests.isEmpty())
      possibleDests.add(villager.getTownHall()); 
    destB = possibleDests.get(MillCommonUtilities.randomInt(possibleDests.size()));
    dest = destB.getResManager().getLeasurePos();
    return packDest(dest, destB);
  }
  
  public boolean performAction(MillVillager villager) {
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 5;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
}
