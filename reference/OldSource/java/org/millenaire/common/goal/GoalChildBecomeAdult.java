package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@Documentation("Move to a house with an empty 'job' and become adult. Only valid for fully grown children.")
public class GoalChildBecomeAdult extends Goal {
  public boolean allowRandomMoves() {
    return true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
    if (villager.getSize() < 20)
      return null; 
    List<Point> possibleDest = new ArrayList<>();
    List<Point> possibleDestBuilding = new ArrayList<>();
    int maxPriority = 0;
    for (Building house : villager.getTownHall().getBuildings()) {
      if (house != null && !house.equals(villager.getHouse()) && house.isHouse())
        if (house.canChildMoveIn(villager.gender, villager.familyName) && house.location.priorityMoveIn >= maxPriority && validateDest(villager, house)) {
          if (house.location.priorityMoveIn > maxPriority) {
            possibleDest.clear();
            possibleDestBuilding.clear();
            maxPriority = house.location.priorityMoveIn;
          } 
          possibleDest.add(house.getResManager().getSleepingPos());
          possibleDestBuilding.add(house.getPos());
        }  
    } 
    if (possibleDest.size() > 0) {
      int rand = MillCommonUtilities.randomInt(possibleDest.size());
      return packDest(possibleDest.get(rand), possibleDestBuilding.get(rand));
    } 
    return null;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws MillLog.MillenaireException {
    return (getDestination(villager) != null);
  }
  
  public boolean performAction(MillVillager villager) throws MillLog.MillenaireException {
    Building house = villager.getGoalBuildingDest();
    if (house != null && 
      house.canChildMoveIn(villager.gender, villager.familyName)) {
      if (MillConfigValues.LogChildren >= 1)
        MillLog.major(this, "Adding new adult to house of type " + house.location + ". Gender: " + villager.gender); 
      house.addAdult(villager);
    } 
    return true;
  }
  
  public int priority(MillVillager villager) {
    return 100;
  }
}
