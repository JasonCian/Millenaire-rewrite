package org.millenaire.common.village;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class BuildingProject implements MillCommonUtilities.WeightedChoice {
  public enum EnumProjects {
    CENTRE(0, "ui.buildingscentre"),
    START(1, "ui.buildingsstarting"),
    PLAYER(2, "ui.buildingsplayer"),
    CORE(3, "ui.buildingskey"),
    SECONDARY(4, "ui.buildingssecondary"),
    EXTRA(5, "ui.buildingsextra"),
    CUSTOMBUILDINGS(6, "ui.buildingcustom"),
    WALLBUILDING(7, "ui.buildingswall");
    
    public final int id;
    
    public final String labelKey;
    
    public static EnumProjects getById(int id) {
      for (EnumProjects ep : values()) {
        if (ep.id == id)
          return ep; 
      } 
      return null;
    }
    
    EnumProjects(int id, String labelKey) {
      this.id = id;
      this.labelKey = labelKey;
    }
  }
  
  public static BuildingProject getRandomProject(List<BuildingProject> possibleProjects) {
    return (BuildingProject)MillCommonUtilities.getWeightedChoice(possibleProjects, null);
  }
  
  public BuildingPlanSet planSet = null;
  
  public BuildingPlan parentPlan = null;
  
  public BuildingLocation location = null;
  
  public BuildingCustomPlan customBuildingPlan = null;
  
  public String key;
  
  public boolean isCustomBuilding = false;
  
  public EnumProjects projectTier = EnumProjects.EXTRA;
  
  public BuildingProject() {}
  
  public BuildingProject(BuildingCustomPlan customPlan, BuildingLocation location) {
    this.customBuildingPlan = customPlan;
    this.key = this.customBuildingPlan.buildingKey;
    this.location = location;
    this.isCustomBuilding = true;
  }
  
  public BuildingProject(BuildingPlanSet planSet) {
    this.planSet = planSet;
    try {
      this.key = (((BuildingPlan[])planSet.plans.get(0))[0]).buildingKey;
    } catch (Exception e) {
      MillLog.printException("Error when getting projet for " + this.key + ", " + planSet + ":", e);
    } 
  }
  
  public BuildingProject(BuildingPlanSet planSet, BuildingPlan parentPlan) {
    this.planSet = planSet;
    this.parentPlan = parentPlan;
    try {
      this.key = (((BuildingPlan[])planSet.plans.get(0))[0]).buildingKey;
    } catch (Exception e) {
      MillLog.printException("Error when getting projet for " + this.key + ", " + planSet + ":", e);
    } 
  }
  
  private int adjustConstructionWeight(int weight) {
    if (this.projectTier == EnumProjects.CENTRE || this.projectTier == EnumProjects.START || this.projectTier == EnumProjects.PLAYER)
      return weight * 6; 
    if (this.projectTier == EnumProjects.CORE)
      return weight * 4; 
    if (this.projectTier == EnumProjects.SECONDARY)
      return weight * 2; 
    return weight;
  }
  
  private int adjustUpgradeWeight(int weight) {
    return weight;
  }
  
  public int getChoiceWeight(EntityPlayer player) {
    if (this.planSet == null)
      return 0; 
    if (this.location == null || this.location.level < 0)
      return adjustConstructionWeight((((BuildingPlan[])this.planSet.plans.get(0))[0]).priority); 
    if (this.location.level + 1 < ((BuildingPlan[])this.planSet.plans.get(this.location.getVariation())).length)
      return adjustUpgradeWeight((((BuildingPlan[])this.planSet.plans.get(this.location.getVariation()))[this.location.level + 1]).priority); 
    return 0;
  }
  
  public BuildingPlan getExistingPlan() {
    if (this.planSet == null)
      return null; 
    if (this.location == null)
      return null; 
    if (this.location.level < 0)
      return null; 
    if (this.location.level < ((BuildingPlan[])this.planSet.plans.get(this.location.getVariation())).length)
      return ((BuildingPlan[])this.planSet.plans.get(this.location.getVariation()))[this.location.level]; 
    return null;
  }
  
  public String getFullName() {
    if (this.planSet != null)
      return this.planSet.getNameNativeAndTranslated(); 
    if (this.customBuildingPlan != null)
      return this.customBuildingPlan.getFullDisplayName(); 
    return null;
  }
  
  public String getGameName() {
    if (this.planSet != null)
      return this.planSet.getNameTranslated(); 
    if (this.customBuildingPlan != null)
      return this.customBuildingPlan.getNameTranslated(); 
    return null;
  }
  
  public int getLevelsNumber(int variation) {
    if (this.planSet == null)
      return 0; 
    if (variation >= this.planSet.plans.size())
      return 1; 
    return ((BuildingPlan[])this.planSet.plans.get(variation)).length;
  }
  
  public String getNativeName() {
    if (this.planSet != null)
      return this.planSet.getNameNative(); 
    if (this.customBuildingPlan != null)
      return this.customBuildingPlan.nativeName; 
    return null;
  }
  
  public BuildingPlan getNextBuildingPlan(boolean randomStartingPlan) {
    if (this.planSet == null)
      return null; 
    if (this.location == null) {
      if (randomStartingPlan)
        return this.planSet.getRandomStartingPlan(); 
      return this.planSet.getFirstStartingPlan();
    } 
    if (this.location.level + 1 < ((BuildingPlan[])this.planSet.plans.get(this.location.getVariation())).length)
      return ((BuildingPlan[])this.planSet.plans.get(this.location.getVariation()))[this.location.level + 1]; 
    return null;
  }
  
  public BuildingPlan getPlan(int variation, int level) {
    if (this.planSet == null)
      return null; 
    if (variation >= this.planSet.plans.size())
      return null; 
    if (level >= ((BuildingPlan[])this.planSet.plans.get(variation)).length)
      return null; 
    return ((BuildingPlan[])this.planSet.plans.get(variation))[level];
  }
  
  public String toString() {
    return "Project " + this.key + " location: " + this.location;
  }
}
