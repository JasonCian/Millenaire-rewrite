package org.millenaire.common.buildingplan;

import java.util.List;
import org.millenaire.common.culture.Culture;

public interface IBuildingPlan {
  Culture getCulture();
  
  List<String> getFemaleResident();
  
  List<String> getMaleResident();
  
  String getNameTranslated();
  
  String getNativeName();
  
  List<String> getVisitors();
}
