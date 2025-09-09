package org.millenaire.common.commands;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class CommandUtilities {
  public static List<Building> getMatchingVillages(MillWorldData worldData, String param) {
    param = normalizeString(param);
    List<Building> villages = new ArrayList<>();
    for (Point thPos : worldData.villagesList.pos) {
      Building townHall = worldData.getBuilding(thPos);
      if (townHall != null && normalizeString(townHall.getVillageQualifiedName()).startsWith(param))
        villages.add(townHall); 
    } 
    return villages;
  }
  
  public static String normalizeString(String string) {
    string = string.replaceAll(" ", "_").toLowerCase();
    string = Normalizer.normalize(string, Normalizer.Form.NFD);
    string = string.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return string;
  }
}
