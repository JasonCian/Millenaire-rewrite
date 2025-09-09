package org.millenaire.common.buildingplan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.world.MillWorldData;

public class BuildingPlanSet {
  public final VirtualDir virtualDir;
  
  public final String key;
  
  public int max;
  
  public List<BuildingPlan[]> plans = (List)new ArrayList<>();
  
  public final Culture culture;
  
  public final File mainFile;
  
  public BuildingPlanSet(Culture c, String key, VirtualDir virtualDir, File mainFile) {
    this.culture = c;
    this.key = key;
    this.virtualDir = virtualDir;
    this.mainFile = mainFile;
  }
  
  public List<BuildingPlan.LocationBuildingPair> buildLocation(MillWorldData mw, VillageType villageType, BuildingLocation location, boolean worldGeneration, boolean isBuildingTownHall, Building townHall, boolean wandimport, boolean includeSpecialPoints, EntityPlayer owner) {
    return ((BuildingPlan[])this.plans.get(location.getVariation()))[location.level].build(mw, villageType, location, worldGeneration, isBuildingTownHall, townHall, wandimport, includeSpecialPoints, owner, false);
  }
  
  public BuildingProject getBuildingProject() {
    return new BuildingProject(this);
  }
  
  public BuildingPlan getFirstStartingPlan() {
    if (this.plans.size() == 0)
      return null; 
    return ((BuildingPlan[])this.plans.get(0))[0];
  }
  
  public ItemStack getIcon() {
    if (this.plans.size() == 0)
      return null; 
    BuildingPlan plan = getFirstStartingPlan();
    return plan.getIcon();
  }
  
  public int getMaxLevel(int variation, int level) {
    int maxLevel = Integer.MIN_VALUE;
    for (int i = 0; i <= level; i++) {
      BuildingPlan plan = ((BuildingPlan[])this.plans.get(variation))[i];
      if (plan.plan.length + plan.startLevel > maxLevel)
        maxLevel = plan.plan.length + plan.startLevel; 
    } 
    return maxLevel;
  }
  
  public int getMinLevel(int variation, int level) {
    int minLevel = Integer.MAX_VALUE;
    for (int i = 0; i <= level; i++) {
      BuildingPlan plan = ((BuildingPlan[])this.plans.get(variation))[i];
      if (plan.startLevel < minLevel)
        minLevel = plan.startLevel; 
    } 
    return minLevel;
  }
  
  public String getNameNative() {
    if (this.plans.size() == 0)
      return this.key; 
    BuildingPlan plan = getFirstStartingPlan();
    return plan.nativeName;
  }
  
  public String getNameNativeAndTranslated() {
    BuildingPlan plan = getFirstStartingPlan();
    return plan.getNameNativeAndTranslated();
  }
  
  public String getNameTranslated() {
    BuildingPlan plan = getFirstStartingPlan();
    return plan.getNameTranslated();
  }
  
  public BuildingPlan getPlan(int variation, int level) {
    if (this.plans.size() <= variation)
      return null; 
    if (((BuildingPlan[])this.plans.get(variation)).length <= level)
      return null; 
    return ((BuildingPlan[])this.plans.get(variation))[level];
  }
  
  public BuildingPlan getRandomStartingPlan() {
    if (this.plans.size() == 0)
      return null; 
    List<BuildingPlan> initialPlans = new ArrayList<>();
    for (BuildingPlan[] variation : this.plans)
      initialPlans.add(variation[0]); 
    BuildingPlan variationPicked = (BuildingPlan)MillCommonUtilities.getWeightedChoice(initialPlans, null);
    return variationPicked;
  }
  
  public void loadPictPlans(boolean importPlan) throws Exception {
    List<List<BuildingPlan>> vplans = new ArrayList<>();
    BuildingPlan prevPlan = null;
    char varChar = 'A';
    int variation = 0;
    while (this.virtualDir.getChildFileRecursive(this.key + "_" + varChar + ".txt") != null) {
      vplans.add(new ArrayList<>());
      int level = 0;
      prevPlan = null;
      List<String> metadataLines = MillCommonUtilities.getFileLines(this.virtualDir.getChildFileRecursive(this.key + "_" + varChar + ".txt"));
      BuildingMetadataLoader metadataLoader = new BuildingMetadataLoader(metadataLines);
      while (this.virtualDir.getChildFileRecursive(this.key + "_" + varChar + level + ".png") != null) {
        prevPlan = PngPlanLoader.loadFromPngs(this.virtualDir.getChildFileRecursive(this.key + "_" + varChar + level + ".png"), this.key, level, variation, prevPlan, metadataLoader, this.culture, importPlan);
        ((List<BuildingPlan>)vplans.get(variation)).add(prevPlan);
        level++;
      } 
      if (((List)vplans.get(variation)).size() == 0)
        throw new MillLog.MillenaireException("No file found for building " + this.key + varChar); 
      varChar = (char)(varChar + 1);
      variation++;
    } 
    this.max = ((BuildingPlan)((List)vplans.get(0)).get(0)).max;
    for (List<BuildingPlan> varPlans : vplans) {
      int length = ((BuildingPlan)varPlans.get(0)).length;
      int width = ((BuildingPlan)varPlans.get(0)).width;
      for (BuildingPlan plan : varPlans) {
        if (plan.width != width)
          throw new MillLog.MillenaireException("Width of upgrade " + plan.level + " of building " + plan.buildingKey + " is " + plan.width + " instead of " + width); 
        if (plan.length != length)
          throw new MillLog.MillenaireException("Length of upgrade " + plan.level + " of building " + plan.buildingKey + " is " + plan.length + " instead of " + length); 
      } 
      BuildingPlan[] varplansarray = new BuildingPlan[varPlans.size()];
      for (int i = 0; i < varPlans.size(); i++)
        varplansarray[i] = varPlans.get(i); 
      this.plans.add(varplansarray);
    } 
  }
  
  public void readBuildingPlanSetInfoPacket(PacketBuffer data) throws IOException {
    int nb = data.readInt();
    this.plans.clear();
    for (int i = 0; i < nb; i++) {
      int nb2 = data.readInt();
      BuildingPlan[] plans = new BuildingPlan[nb2];
      for (int j = 0; j < nb2; j++)
        plans[j] = StreamReadWrite.readBuildingPlanInfo(data, this.culture); 
      this.plans.add(plans);
    } 
  }
  
  public String toString() {
    return this.key + " (" + this.plans.size() + " / " + this.max + "/" + (((BuildingPlan[])this.plans.get(0))[0]).nativeName + ")";
  }
  
  public void writeBuildingPlanSetInfo(PacketBuffer data) throws IOException {
    data.writeString(this.key);
    data.writeInt(this.plans.size());
    for (BuildingPlan[] plans : this.plans) {
      data.writeInt(plans.length);
      for (BuildingPlan plan : plans)
        StreamReadWrite.writeBuildingPlanInfo(plan, data); 
    } 
  }
}
