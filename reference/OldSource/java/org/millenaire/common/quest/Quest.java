package org.millenaire.common.quest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class Quest {
  public Quest() {
    WORLD_MISSION_NB.put("sadhu", Integer.valueOf(15));
    WORLD_MISSION_NB.put("alchemist", Integer.valueOf(13));
    WORLD_MISSION_NB.put("fallenking", Integer.valueOf(10));
    this.chanceperhour = 0.0D;
    this.maxsimultaneous = 5;
    this.minreputation = 0;
    this.steps = new ArrayList<>();
    this.globalTagsForbidden = new ArrayList<>();
    this.globalTagsRequired = new ArrayList<>();
    this.profileTagsForbidden = new ArrayList<>();
    this.profileTagsRequired = new ArrayList<>();
    this.villagers = new HashMap<>();
    this.villagersOrdered = new ArrayList<>();
  }
  
  public static HashMap<String, Quest> quests = new HashMap<>();
  
  private static final String REL_NEARBYVILLAGE = "nearbyvillage";
  
  private static final String REL_ANYVILLAGE = "anyvillage";
  
  private static final String REL_SAMEHOUSE = "samehouse";
  
  private static final String REL_SAMEVILLAGE = "samevillage";
  
  public static final String INDIAN_WQ = "sadhu";
  
  public static final String NORMAN_WQ = "alchemist";
  
  public static final String MAYAN_WQ = "fallenking";
  
  public static final Map<String, Integer> WORLD_MISSION_NB = new HashMap<>();
  
  public static final String[] WORLD_MISSION_KEYS = new String[] { "sadhu", "alchemist", "fallenking" };
  
  public double chanceperhour;
  
  public String key;
  
  public int maxsimultaneous;
  
  public int minreputation;
  
  public List<QuestStep> steps;
  
  public List<String> globalTagsForbidden;
  
  public List<String> globalTagsRequired;
  
  public List<String> profileTagsForbidden;
  
  public List<String> profileTagsRequired;
  
  public HashMap<String, QuestVillager> villagers;
  
  public List<QuestVillager> villagersOrdered;
  
  private static Quest loadQuest(File file) {
    Quest q = new Quest();
    q.key = file.getName().split("\\.")[0];
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      QuestStep step = null;
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] temp = line.split(":");
          if (temp.length != 2) {
            MillLog.error(null, "Invalid line when loading quest " + file.getName() + ": " + line);
            continue;
          } 
          String key = temp[0].toLowerCase();
          String value = temp[1];
          if (key.equals("step")) {
            step = new QuestStep(q, q.steps.size());
            q.steps.add(step);
            continue;
          } 
          if (key.equals("minreputation")) {
            q.minreputation = MillCommonUtilities.readInteger(value);
            continue;
          } 
          if (key.equals("chanceperhour")) {
            q.chanceperhour = Double.parseDouble(value);
            continue;
          } 
          if (key.equals("maxsimultaneous")) {
            q.maxsimultaneous = MillCommonUtilities.readInteger(value);
            continue;
          } 
          if (key.equals("definevillager")) {
            QuestVillager v = q.loadQVillager(value);
            if (v != null) {
              q.villagers.put(v.key, v);
              q.villagersOrdered.add(v);
            } 
            continue;
          } 
          if (key.equals("requiredglobaltag")) {
            q.globalTagsRequired.add(value.trim().toLowerCase());
            continue;
          } 
          if (key.equals("forbiddenglobaltag")) {
            q.globalTagsForbidden.add(value.trim().toLowerCase());
            continue;
          } 
          if (key.equals("requiredplayertag")) {
            q.profileTagsRequired.add(value.trim().toLowerCase());
            continue;
          } 
          if (key.equals("forbiddenplayertag")) {
            q.profileTagsForbidden.add(value.trim().toLowerCase());
            continue;
          } 
          if (step == null) {
            MillLog.error(q, "Reached line while not in a step: " + line);
            continue;
          } 
          if (key.equals("villager")) {
            step.villager = value;
            continue;
          } 
          if (key.equals("duration")) {
            step.duration = MillCommonUtilities.readInteger(value);
            continue;
          } 
          if (key.equals("showrequiredgoods")) {
            step.showRequiredGoods = Boolean.parseBoolean(value);
            continue;
          } 
          if (key.startsWith("label_")) {
            step.labels.put(key, value);
            continue;
          } 
          if (key.startsWith("description_success_")) {
            step.descriptionsSuccess.put(key, value);
            continue;
          } 
          if (key.startsWith("description_refuse_")) {
            step.descriptionsRefuse.put(key, value);
            continue;
          } 
          if (key.startsWith("description_timeup_")) {
            step.descriptionsTimeUp.put(key, value);
            continue;
          } 
          if (key.startsWith("description_")) {
            step.descriptions.put(key, value);
            continue;
          } 
          if (key.startsWith("listing_")) {
            step.listings.put(key, value);
            continue;
          } 
          if (key.equals("requiredgood")) {
            if (InvItem.INVITEMS_BY_NAME.containsKey(value.split(",")[0].toLowerCase())) {
              InvItem iv = (InvItem)InvItem.INVITEMS_BY_NAME.get(value.split(",")[0].toLowerCase());
              step.requiredGood.put(iv, Integer.valueOf(MillCommonUtilities.readInteger(value.split(",")[1])));
              continue;
            } 
            MillLog.error(null, "Unknown requiredgood found when loading quest " + file.getName() + ": " + value);
            continue;
          } 
          if (key.equals("rewardgood")) {
            if (InvItem.INVITEMS_BY_NAME.containsKey(value.split(",")[0].toLowerCase())) {
              InvItem iv = (InvItem)InvItem.INVITEMS_BY_NAME.get(value.split(",")[0].toLowerCase());
              step.rewardGoods.put(iv, Integer.valueOf(MillCommonUtilities.readInteger(value.split(",")[1])));
              continue;
            } 
            MillLog.error(null, "Unknown rewardGood found when loading quest " + file.getName() + ": " + value);
            continue;
          } 
          if (key.equals("rewardmoney")) {
            step.rewardMoney = MillCommonUtilities.readInteger(value);
            continue;
          } 
          if (key.equals("rewardreputation")) {
            step.rewardReputation = MillCommonUtilities.readInteger(value);
            continue;
          } 
          if (key.equals("penaltyreputation")) {
            step.penaltyReputation = MillCommonUtilities.readInteger(value);
            continue;
          } 
          if (key.equals("setactiondatasuccess")) {
            step.setActionDataSuccess.add(value.split(","));
            continue;
          } 
          if (key.equals("relationchange")) {
            try {
              QuestStep.QuestStepRelationChange relationChange = QuestStep.QuestStepRelationChange.parseString(value);
              step.relationChanges.add(relationChange);
            } catch (Exception e) {
              MillLog.error(null, "Error when loading relationchange: " + e.getMessage() + " when loading quest " + file.getName() + ": " + value);
            } 
            continue;
          } 
          if (key.equals("settagsuccess")) {
            step.setVillagerTagsSuccess.add(value.split(","));
            continue;
          } 
          if (key.equals("cleartagsuccess")) {
            step.clearTagsSuccess.add(value.split(","));
            continue;
          } 
          if (key.equals("settagfailure")) {
            step.setVillagerTagsFailure.add(value.split(","));
            continue;
          } 
          if (key.equals("cleartagfailure")) {
            step.clearTagsFailure.add(value.split(","));
            continue;
          } 
          if (key.equals("setglobaltagsuccess")) {
            step.setGlobalTagsSuccess.add(value);
            continue;
          } 
          if (key.equals("clearglobaltagsuccess")) {
            step.clearGlobalTagsSuccess.add(value);
            continue;
          } 
          if (key.equals("setglobaltagfailure")) {
            step.setGlobalTagsFailure.add(value);
            continue;
          } 
          if (key.equals("clearglobaltagfailure")) {
            step.clearGlobalTagsFailure.add(value);
            continue;
          } 
          if (key.equals("setplayertagsuccess")) {
            step.setPlayerTagsSuccess.add(value);
            continue;
          } 
          if (key.equals("clearplayertagsuccess")) {
            step.clearPlayerTagsSuccess.add(value);
            continue;
          } 
          if (key.equals("setplayertagfailure")) {
            step.setPlayerTagsFailure.add(value);
            continue;
          } 
          if (key.equals("clearplayertagfailure")) {
            step.clearPlayerTagsFailure.add(value);
            continue;
          } 
          if (key.equals("steprequiredglobaltag")) {
            step.stepRequiredGlobalTag.add(value);
            continue;
          } 
          if (key.equals("stepforbiddenglobaltag")) {
            step.forbiddenGlobalTag.add(value);
            continue;
          } 
          if (key.equals("steprequiredplayertag")) {
            step.stepRequiredPlayerTag.add(value);
            continue;
          } 
          if (key.equals("stepforbiddenplayertag")) {
            step.forbiddenPlayerTag.add(value);
            continue;
          } 
          if (key.equals("bedrockbuilding")) {
            step.bedrockbuildings.add(value.trim().toLowerCase());
            continue;
          } 
          MillLog.error(null, "Unknown parameter when loading quest " + file.getName() + ": " + line);
        } 
      } 
      reader.close();
      if (q.steps.size() == 0) {
        MillLog.error(q, "No steps found in " + file.getName() + ".");
        return null;
      } 
      if (q.villagersOrdered.size() == 0) {
        MillLog.error(q, "No villagers defined in " + file.getName() + ".");
        return null;
      } 
      if (MillConfigValues.LogQuest >= 1)
        MillLog.major(q, "Loaded quest type: " + q.key); 
      return q;
    } catch (Exception e) {
      MillLog.printException(e);
      return null;
    } 
  }
  
  public static void loadQuests() {
    VirtualDir questVirtualDir = Mill.virtualLoadingDir.getChildDirectory("quests");
    for (File file : questVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"))) {
      Quest quest = loadQuest(file);
      if (quest != null)
        quests.put(quest.key, quest); 
    } 
    if (MillConfigValues.LogQuest >= 1)
      MillLog.major(null, "Loaded " + quests.size() + " quests."); 
  }
  
  private QuestVillager loadQVillager(String line) {
    QuestVillager v = new QuestVillager();
    for (String s : line.split(",")) {
      String key = s.split("=")[0].toLowerCase();
      String val = s.split("=")[1];
      if (key.equals("key")) {
        v.key = val;
      } else if (key.equals("type")) {
        Culture c = Culture.getCultureByName(val.split("/")[0]);
        if (c == null) {
          MillLog.error(this, "Unknown culture when loading definevillager: " + line);
          return null;
        } 
        VillagerType vtype = c.getVillagerType(val.split("/")[1]);
        if (vtype == null) {
          MillLog.error(this, "Unknown villager type when loading definevillager: " + line);
          return null;
        } 
        v.types.add(vtype.key);
      } else if (key.equals("relatedto")) {
        v.relatedto = val;
      } else if (key.equals("relation")) {
        v.relation = val;
      } else if (key.equals("forbiddentag")) {
        v.forbiddenTags.add(val);
      } else if (key.equals("requiredtag")) {
        v.requiredTags.add(val);
      } else {
        MillLog.error(this, "Could not understand setting in definevillager:" + key + ", in line: " + line);
      } 
    } 
    if (v.key == null) {
      MillLog.error(this, "No key found when loading definevillager: " + line);
      return null;
    } 
    return v;
  }
  
  public QuestInstance testQuest(MillWorldData mw, UserProfile profile) {
    if (!MillCommonUtilities.probability(this.chanceperhour))
      return null; 
    int nb = 0;
    for (QuestInstance questInstance : profile.questInstances) {
      if (questInstance.quest == this)
        nb++; 
    } 
    if (nb >= this.maxsimultaneous)
      return null; 
    for (String tag : this.globalTagsRequired) {
      if (!mw.isGlobalTagSet(tag))
        return null; 
    } 
    for (String tag : this.profileTagsRequired) {
      if (!profile.isTagSet(tag))
        return null; 
    } 
    for (String tag : this.globalTagsForbidden) {
      if (mw.isGlobalTagSet(tag))
        return null; 
    } 
    for (String tag : this.profileTagsForbidden) {
      if (profile.isTagSet(tag))
        return null; 
    } 
    if (MillConfigValues.LogQuest >= 3)
      MillLog.debug(this, "Testing quest " + this.key); 
    QuestVillager startingVillager = this.villagersOrdered.get(0);
    List<HashMap<String, QuestInstanceVillager>> possibleVillagers = new ArrayList<>();
    for (Point p : mw.getCombinedVillagesLoneBuildings()) {
      Building th = mw.getBuilding(p);
      if (th != null && th.isActive && th.getReputation(profile.getPlayer()) >= this.minreputation) {
        if (MillConfigValues.LogQuest >= 3)
          MillLog.debug(this, "Looking for starting villager in: " + th.getVillageQualifiedName()); 
        for (VillagerRecord vr : th.getAllVillagerRecords()) {
          if (startingVillager.testVillager(profile, vr)) {
            HashMap<String, QuestInstanceVillager> villagers = new HashMap<>();
            villagers.put(startingVillager.key, new QuestInstanceVillager(mw, p, vr.getVillagerId(), vr));
            boolean error = false;
            if (MillConfigValues.LogQuest >= 3)
              MillLog.debug(this, "Found possible starting villager: " + vr); 
            for (QuestVillager qv : this.villagersOrdered) {
              if (!error && qv != startingVillager) {
                VillagerRecord relatedVillager;
                if (MillConfigValues.LogQuest >= 3)
                  MillLog.debug(this, "Trying to find villager type: " + qv.relation + "/" + qv.relatedto); 
                if (villagers.get(qv.relatedto) != null) {
                  relatedVillager = ((QuestInstanceVillager)villagers.get(qv.relatedto)).getVillagerRecord(mw.world);
                } else {
                  error = true;
                  break;
                } 
                if (relatedVillager == null) {
                  error = true;
                  break;
                } 
                if ("samevillage".equals(qv.relation)) {
                  List<VillagerRecord> newVillagers = new ArrayList<>();
                  for (VillagerRecord vr2 : mw.getBuilding(relatedVillager.getTownHallPos()).getAllVillagerRecords()) {
                    if (!vr2.getHousePos().equals(relatedVillager.getHousePos()) && qv.testVillager(profile, vr2))
                      newVillagers.add(vr2); 
                  } 
                  if (newVillagers.size() > 0) {
                    VillagerRecord chosen = newVillagers.get(MillCommonUtilities.randomInt(newVillagers.size()));
                    villagers.put(qv.key, new QuestInstanceVillager(mw, p, chosen.getVillagerId(), chosen));
                    continue;
                  } 
                  error = true;
                  continue;
                } 
                if ("nearbyvillage".equals(qv.relation) || "anyvillage".equals(qv.relation)) {
                  List<QuestInstanceVillager> newVillagers = new ArrayList<>();
                  for (Point p2 : mw.getCombinedVillagesLoneBuildings()) {
                    Building th2 = mw.getBuilding(p2);
                    if (th2 != null && th2 != th && ("anyvillage".equals(qv.relation) || th.getPos().distanceTo(th2.getPos()) < 2000.0D)) {
                      if (MillConfigValues.LogQuest >= 3)
                        MillLog.debug(this, "Trying to find villager type: " + qv.relation + "/" + qv.relatedto + " in " + th2.getVillageQualifiedName()); 
                      for (VillagerRecord vr2 : th2.getAllVillagerRecords()) {
                        if (MillConfigValues.LogQuest >= 3)
                          MillLog.debug(this, "Testing: " + vr2); 
                        if (qv.testVillager(profile, vr2))
                          newVillagers.add(new QuestInstanceVillager(mw, p2, vr2.getVillagerId(), vr2)); 
                      } 
                    } 
                  } 
                  if (newVillagers.size() > 0) {
                    villagers.put(qv.key, newVillagers.get(MillCommonUtilities.randomInt(newVillagers.size())));
                    continue;
                  } 
                  error = true;
                  continue;
                } 
                if ("samehouse".equals(qv.relation)) {
                  List<VillagerRecord> newVillagers = new ArrayList<>();
                  for (VillagerRecord vr2 : mw.getBuilding(relatedVillager.getTownHallPos()).getAllVillagerRecords()) {
                    if (vr2.getHousePos().equals(relatedVillager.getHousePos()) && qv.testVillager(profile, vr2))
                      newVillagers.add(vr2); 
                  } 
                  if (newVillagers.size() > 0) {
                    VillagerRecord chosen = newVillagers.get(MillCommonUtilities.randomInt(newVillagers.size()));
                    villagers.put(qv.key, new QuestInstanceVillager(mw, p, chosen.getVillagerId(), chosen));
                    continue;
                  } 
                  error = true;
                  continue;
                } 
                MillLog.error(this, "Unknown relation: " + qv.relation);
              } 
            } 
            if (!error) {
              possibleVillagers.add(villagers);
              if (MillConfigValues.LogQuest >= 3)
                MillLog.debug(this, "Found all the villagers needed: " + villagers.size()); 
            } 
          } 
        } 
      } 
    } 
    if (possibleVillagers.isEmpty())
      return null; 
    HashMap<String, QuestInstanceVillager> selectedOption = possibleVillagers.get(MillCommonUtilities.randomInt(possibleVillagers.size()));
    QuestInstance qi = new QuestInstance(mw, this, profile, selectedOption, mw.world.getWorldTime());
    profile.questInstances.add(qi);
    for (QuestInstanceVillager qiv : selectedOption.values())
      profile.villagersInQuests.put(Long.valueOf(qiv.id), qi); 
    return qi;
  }
  
  public String toString() {
    return "QT: " + this.key;
  }
}
