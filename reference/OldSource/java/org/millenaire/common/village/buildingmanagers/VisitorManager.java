package org.millenaire.common.village.buildingmanagers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;

public class VisitorManager {
  private final Building building;
  
  private boolean nightActionPerformed = false;
  
  public VisitorManager(Building building) {
    this.building = building;
  }
  
  public void update(boolean forceAttempt) throws MillLog.MillenaireException {
    if (this.building.isMarket) {
      updateMarket(forceAttempt);
    } else {
      updateVisitors(forceAttempt);
    } 
  }
  
  private void updateMarket(boolean forceAttempt) throws MillLog.MillenaireException {
    if (this.building.world.isDaytime() && !forceAttempt) {
      this.nightActionPerformed = false;
    } else if (!this.nightActionPerformed || forceAttempt) {
      int maxMerchants = (this.building.getResManager()).stalls.size();
      if (this.building.getAllVillagerRecords().size() < maxMerchants) {
        VillagerType type;
        if (MillConfigValues.LogMerchant >= 1)
          MillLog.major(this, "Attempting to create a foreign merchant."); 
        List<VillagerType> merchantTypesOtherVillages = new ArrayList<>();
        for (Point p : this.building.getTownHall().getRelations().keySet()) {
          if (((Integer)this.building.getTownHall().getRelations().get(p)).intValue() > 70) {
            Building distantVillage = this.building.mw.getBuilding(p);
            if (distantVillage != null && distantVillage.culture != (this.building.getTownHall()).culture && distantVillage.getBuildingsWithTag("market").size() > 0)
              merchantTypesOtherVillages.add(distantVillage.culture.getRandomForeignMerchant()); 
          } 
        } 
        int foreignChance = Math.min(1 + merchantTypesOtherVillages.size(), 5);
        if (merchantTypesOtherVillages.size() > 0 && MillCommonUtilities.randomInt(11) < foreignChance) {
          if (merchantTypesOtherVillages.size() == 0) {
            type = this.building.culture.getRandomForeignMerchant();
          } else {
            type = merchantTypesOtherVillages.get(MillCommonUtilities.randomInt(merchantTypesOtherVillages.size()));
          } 
        } else {
          type = this.building.culture.getRandomForeignMerchant();
        } 
        VillagerRecord merchantRecord = VillagerRecord.createVillagerRecord(type.culture, type.key, this.building.mw, this.building.getPos(), this.building.getTownHall().getPos(), null, null, -1L, false);
        MillVillager merchant = MillVillager.createVillager(merchantRecord, this.building.world, this.building.getResManager().getSleepingPos(), false);
        this.building.world.spawnEntity((Entity)merchant);
        for (InvItem iv : merchant.vtype.foreignMerchantStock.keySet())
          this.building.storeGoods(iv.getItem(), iv.meta, ((Integer)merchant.vtype.foreignMerchantStock.get(iv)).intValue()); 
        if (MillConfigValues.LogMerchant >= 1)
          MillLog.major(this, "Created foreign merchant: " + merchantRecord); 
      } 
      this.nightActionPerformed = true;
    } 
  }
  
  private void updateVisitors(boolean forceAttempt) throws MillLog.MillenaireException {
    if (this.building.world.isDaytime() && !forceAttempt) {
      this.nightActionPerformed = false;
    } else if (!this.nightActionPerformed || forceAttempt) {
      Map<String, Integer> targetCount = new HashMap<>();
      for (String visitorType : this.building.location.getVisitors()) {
        if (targetCount.containsKey(visitorType)) {
          targetCount.put(visitorType, Integer.valueOf(((Integer)targetCount.get(visitorType)).intValue() + 1));
          continue;
        } 
        targetCount.put(visitorType, Integer.valueOf(1));
      } 
      for (String visitorType : targetCount.keySet()) {
        int currentCount = 0;
        for (VillagerRecord vr : this.building.getAllVillagerRecords()) {
          if (vr.type.equals(visitorType))
            currentCount++; 
        } 
        VillagerType type = this.building.culture.getVillagerType(visitorType);
        for (int i = currentCount; i < ((Integer)targetCount.get(visitorType)).intValue(); i++) {
          if (MillCommonUtilities.chanceOn(2)) {
            VillagerRecord visitorRecord = VillagerRecord.createVillagerRecord(type.culture, type.key, this.building.mw, this.building.getPos(), this.building.getTownHall().getPos(), null, null, -1L, false);
            MillVillager visitor = MillVillager.createVillager(visitorRecord, this.building.world, this.building.getResManager().getSleepingPos(), false);
            this.building.world.spawnEntity((Entity)visitor);
            if (MillConfigValues.LogMerchant >= 1)
              MillLog.major(this, "Created visitor: " + visitorRecord); 
          } 
        } 
      } 
      this.nightActionPerformed = true;
    } 
  }
}
