package org.millenaire.common.annotedparameters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.WallType;
import org.millenaire.common.utilities.MillLog;

public abstract class CultureValueIO extends ValueIO {
  public static class BuildingCustomAddIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      if (culture.getBuildingCustom(value) != null) {
        ((List<BuildingCustomPlan>)field.get(targetClass)).add(culture.getBuildingCustom(value));
      } else {
        throw new MillLog.MillenaireException("Unknown custom building: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      List<BuildingCustomPlan> customPlans = (List<BuildingCustomPlan>)rawValue;
      List<String> results = new ArrayList<>();
      for (BuildingCustomPlan customPlan : customPlans)
        results.add(customPlan.buildingKey); 
      return results;
    }
  }
  
  public static class BuildingCustomIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      if (culture.getBuildingCustom(value) != null) {
        field.set(targetClass, culture.getBuildingCustom(value));
      } else {
        throw new MillLog.MillenaireException("Unknown custom building: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      BuildingCustomPlan plan = (BuildingCustomPlan)rawValue;
      return createListFromValue(plan.buildingKey);
    }
  }
  
  public static class BuildingSetAddIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      if (culture.getBuildingPlanSet(value) != null) {
        ((List<BuildingPlanSet>)field.get(targetClass)).add(culture.getBuildingPlanSet(value));
      } else {
        throw new MillLog.MillenaireException("Unknown building: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      List<BuildingPlanSet> plans = (List<BuildingPlanSet>)rawValue;
      List<String> results = new ArrayList<>();
      for (BuildingPlanSet plan : plans)
        results.add(plan.key); 
      return results;
    }
  }
  
  public static class BuildingSetIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      if (culture.getBuildingPlanSet(value) != null) {
        field.set(targetClass, culture.getBuildingPlanSet(value));
      } else {
        throw new MillLog.MillenaireException("Unknown building: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      BuildingPlanSet plan = (BuildingPlanSet)rawValue;
      return createListFromValue(plan.key);
    }
  }
  
  public static class ShopIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      value = value.toLowerCase();
      if (culture == null || culture.shopBuys.containsKey(value) || culture.shopSells.containsKey(value) || culture.shopBuysOptional.containsKey(value)) {
        field.set(targetClass, value);
      } else {
        throw new MillLog.MillenaireException("Unknown shop: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      String value = (String)rawValue;
      return createListFromValue(value);
    }
  }
  
  public static class VillagerAddIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      if (culture == null || culture.villagerTypes.get(value.toLowerCase()) != null) {
        ((List<String>)field.get(targetClass)).add(value.toLowerCase());
      } else {
        throw new MillLog.MillenaireException("Unknown villager type: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      List<String> values = (List<String>)rawValue;
      return values;
    }
  }
  
  public static class WallIO extends CultureValueIO {
    public void readValueCulture(Culture culture, Object targetClass, Field field, String value) throws Exception {
      if (culture.wallTypes.containsKey(value)) {
        field.set(targetClass, culture.wallTypes.get(value));
      } else {
        throw new MillLog.MillenaireException("Unknown wall type: " + value);
      } 
    }
    
    public List<String> writeValue(Object rawValue) throws Exception {
      WallType wall = (WallType)rawValue;
      return createListFromValue(wall.key);
    }
  }
  
  public void readValue(Object targetClass, Field field, String value) throws Exception {
    MillLog.error(this, "Using readValue on a CultureValueIO object.");
  }
  
  public boolean useCulture() {
    return true;
  }
}
