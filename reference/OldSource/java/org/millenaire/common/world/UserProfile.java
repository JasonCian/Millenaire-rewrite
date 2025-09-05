package org.millenaire.common.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import org.millenaire.common.advancements.GenericAdvancement;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.quest.Quest;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.quest.QuestInstanceVillager;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class UserProfile {
  public static final String CROP_PLANTING = "cropplanting_";
  
  public static final String HUNTING_DROP = "huntingdrop_";
  
  private static final int CULTURE_MAX_REPUTATION = 4096;
  
  private static final int CULTURE_MIN_REPUTATION = -640;
  
  public static final int FRIEND_OF_THE_VILLAGE = 8192;
  
  public static final int ONE_OF_US = 32768;
  
  public static final int UPDATE_ALL = 1;
  
  public static final int UPDATE_REPUTATION = 2;
  
  public static final int UPDATE_DIPLOMACY = 3;
  
  public static final int UPDATE_ACTIONDATA = 4;
  
  public static final int UPDATE_TAGS = 5;
  
  public static final int UPDATE_LANGUAGE = 6;
  
  public static final int UPDATE_GLOBAL_TAGS = 7;
  
  public static final int UPDATE_UNLOCKED_CONTENT = 8;
  
  public static final int UNLOCKED_BUILDING = 1;
  
  public static final int UNLOCKED_VILLAGE = 2;
  
  public static final int UNLOCKED_VILLAGER = 3;
  
  public static final int UNLOCKED_TRADE_GOOD = 4;
  
  public static final int UNLOCKED_TRADE_GOOD_MULTIPLE = 5;
  
  public static UserProfile readProfile(MillWorldData world, File dir) {
    UserProfile profile;
    String key = dir.getName();
    boolean legacyProfile = ((key.split("-")).length != 5);
    if (legacyProfile) {
      profile = new UserProfile(world, key);
    } else {
      UUID uuid = UUID.fromString(key);
      profile = new UserProfile(world, uuid);
    } 
    if (legacyProfile) {
      File oldDir = new File(new File(world.millenaireDir, "profiles"), key);
      oldDir.renameTo(profile.getDir());
      MillLog.major(null, "Renamed profile from " + key + " to " + profile.uuid);
    } 
    profile.loadProfileConfig(new File(profile.getDir(), "config.txt"));
    profile.loadProfileTags();
    profile.loadActionData(new File(profile.getDir(), "actiondata.txt"));
    profile.loadQuestInstances(new File(profile.getDir(), "quests.txt"));
    profile.loadUnlockedContent(new File(profile.getDir(), "unlockedcontent.txt"));
    if (legacyProfile)
      profile.saveProfile(); 
    return profile;
  }
  
  private final Set<String> unlockedVillagers = new HashSet<>();
  
  private final Set<String> unlockedVillages = new HashSet<>();
  
  private final Set<String> unlockedBuildings = new HashSet<>();
  
  private final Set<String> unlockedTradeGoods = new HashSet<>();
  
  private final HashMap<Point, Integer> villageReputations = new HashMap<>();
  
  private final HashMap<Point, Byte> villageDiplomacy = new HashMap<>();
  
  private final HashMap<String, Integer> cultureReputations = new HashMap<>();
  
  private final HashMap<String, Integer> cultureLanguages = new HashMap<>();
  
  private final List<String> profileTags = new ArrayList<>();
  
  public UUID uuid;
  
  public String playerName;
  
  public boolean donationActivated = false;
  
  public List<QuestInstance> questInstances = new ArrayList<>();
  
  public HashMap<Long, QuestInstance> villagersInQuests = new HashMap<>();
  
  private final HashMap<String, String> actionData = new HashMap<>();
  
  private final MillWorldData mw;
  
  boolean connectionActionDone = false;
  
  public boolean connected = false;
  
  private boolean showNewWorldMessageDone = false;
  
  public String releaseNumber = null;
  
  public HashMap<Point, Integer> panelsSent = new HashMap<>();
  
  public HashMap<Point, Long> panelsSentTime = new HashMap<>();
  
  public HashMap<Point, Long> buildingsSent = new HashMap<>();
  
  public UserProfile(MillWorldData world, EntityPlayer player) {
    this.uuid = player.getUniqueID();
    this.playerName = player.getName();
    this.mw = world;
  }
  
  private UserProfile(MillWorldData world, String playerName) {
    UUID uuid = world.world.getMinecraftServer().getPlayerProfileCache().getGameProfileForUsername(playerName).getId();
    MillLog.major(null, "Loading profile using name '" + playerName + "', convnerting to uuid: " + uuid);
    this.uuid = uuid;
    this.playerName = playerName;
    this.mw = world;
  }
  
  private UserProfile(MillWorldData world, UUID uuid) {
    this.uuid = uuid;
    this.mw = world;
  }
  
  public UserProfile(MillWorldData mw, UUID uuid, String name) {
    this.uuid = uuid;
    this.playerName = name;
    this.mw = mw;
  }
  
  public void adjustDiplomacyPoint(Building b, int change) {
    int dp = 0;
    if (this.villageDiplomacy.containsKey(b.getPos()))
      dp = ((Byte)this.villageDiplomacy.get(b.getPos())).byteValue(); 
    dp += change;
    if (dp > 5)
      dp = 5; 
    if (dp < 0)
      dp = 0; 
    this.villageDiplomacy.put(b.getPos(), Byte.valueOf((byte)dp));
    saveProfileConfig();
    sendProfilePacket(3);
  }
  
  public void adjustLanguage(String culture, int change) {
    if (this.cultureLanguages.containsKey(culture)) {
      this.cultureLanguages.put(culture, Integer.valueOf(((Integer)this.cultureLanguages.get(culture)).intValue() + change));
    } else {
      this.cultureLanguages.put(culture, Integer.valueOf(change));
    } 
    saveProfileConfig();
    sendProfilePacket(6);
  }
  
  public void adjustReputation(Building b, int change) {
    if (b == null)
      return; 
    int newReputation = change;
    if (this.villageReputations.containsKey(b.getPos()))
      newReputation += ((Integer)this.villageReputations.get(b.getPos())).intValue(); 
    this.villageReputations.put(b.getPos(), Integer.valueOf(newReputation));
    if (newReputation > 8192) {
      MillAdvancements.A_FRIEND_INDEED.grant(getPlayer());
      int nbFriends = 0;
      for (UserProfile up : this.mw.profiles.values()) {
        if (up.getReputation(b) > 8192)
          nbFriends++; 
      } 
      if (nbFriends > 3)
        MillAdvancements.MP_FRIENDLYVILLAGE.grant(getPlayer()); 
    } 
    if (newReputation > 32768) {
      String cultureKey = b.culture.key.toLowerCase();
      if (MillAdvancements.REP_ADVANCEMENTS.containsKey(cultureKey))
        ((GenericAdvancement)MillAdvancements.REP_ADVANCEMENTS.get(cultureKey)).grant(getPlayer()); 
    } 
    int rep = 0;
    if (this.cultureReputations.containsKey(b.culture.key))
      rep = ((Integer)this.cultureReputations.get(b.culture.key)).intValue(); 
    rep += change / 10;
    if (change > 0) {
      if (Math.random() * 10.0D < (change % 10))
        rep++; 
    } else if (Math.random() * 10.0D < (-change % 10)) {
      rep--;
    } 
    rep = Math.max(-640, rep);
    rep = Math.min(4096, rep);
    this.cultureReputations.put(b.culture.key, Integer.valueOf(rep));
    if (rep <= -640) {
      int nbAwfulRep = 0;
      for (Iterator<Integer> iterator = this.cultureReputations.values().iterator(); iterator.hasNext(); ) {
        int cultureRep = ((Integer)iterator.next()).intValue();
        if (cultureRep <= -640)
          nbAwfulRep++; 
      } 
      if (nbAwfulRep >= 3)
        MillAdvancements.ATTILA.grant(getPlayer()); 
    } 
    saveProfileConfig();
    sendProfilePacket(2);
  }
  
  public void clearActionData(String key) {
    if (this.actionData.containsKey(key)) {
      this.actionData.remove(key);
      saveActionData();
      sendProfilePacket(4);
    } 
  }
  
  private void clearFarAwayPanels() {
    List<Point> farAway = new ArrayList<>();
    EntityPlayer player = getPlayer();
    for (Point p : this.panelsSent.keySet()) {
      if (p.distanceToSquared((Entity)player) > 900.0D)
        farAway.add(p); 
    } 
    for (Point p : farAway) {
      this.panelsSent.remove(p);
      this.panelsSentTime.remove(p);
    } 
  }
  
  public void clearTag(String tag) {
    if (this.profileTags.contains(tag)) {
      this.profileTags.remove(tag);
      saveProfileTags();
      sendProfilePacket(5);
    } 
  }
  
  public void connectUser() {
    this.connected = true;
    this.connectionActionDone = false;
  }
  
  private void deleteQuestInstance(long id) {
    List<Long> toDelete = new ArrayList<>();
    Iterator<Long> iterator;
    for (iterator = this.villagersInQuests.keySet().iterator(); iterator.hasNext(); ) {
      long vid = ((Long)iterator.next()).longValue();
      if (((QuestInstance)this.villagersInQuests.get(Long.valueOf(vid))).uniqueid == id)
        toDelete.add(Long.valueOf(vid)); 
    } 
    for (iterator = toDelete.iterator(); iterator.hasNext(); ) {
      long vid = ((Long)iterator.next()).longValue();
      this.villagersInQuests.remove(Long.valueOf(vid));
    } 
    for (int i = this.questInstances.size() - 1; i >= 0; i--) {
      if (((QuestInstance)this.questInstances.get(i)).uniqueid == id)
        this.questInstances.remove(i); 
    } 
  }
  
  public void disconnectUser() {
    this.connected = false;
    this.panelsSent.clear();
    this.panelsSentTime.clear();
    this.buildingsSent.clear();
    if (MillConfigValues.LogNetwork >= 1)
      MillLog.major(this, "Disconnected user."); 
  }
  
  public String getActionData(String key) {
    return this.actionData.get(key);
  }
  
  public int getCultureLanguageKnowledge(String key) {
    if (this.cultureLanguages.containsKey(key))
      return ((Integer)this.cultureLanguages.get(key)).intValue(); 
    return 0;
  }
  
  public int getCultureReputation(String key) {
    if (this.cultureReputations.containsKey(key))
      return ((Integer)this.cultureReputations.get(key)).intValue(); 
    return 0;
  }
  
  public int getDiplomacyPoints(Building b) {
    int dp = 0;
    if (this.villageDiplomacy.containsKey(b.getPos()))
      dp = ((Byte)this.villageDiplomacy.get(b.getPos())).byteValue(); 
    return dp;
  }
  
  private File getDir() {
    File dir = new File(new File(this.mw.millenaireDir, "profiles"), this.uuid.toString());
    if (!dir.exists())
      dir.mkdirs(); 
    return dir;
  }
  
  public int getNbUnlockedBuildings(Culture culture) {
    int nbunlocked = 0;
    for (BuildingPlanSet planSet : culture.ListPlanSets) {
      if ((planSet.getFirstStartingPlan()).travelBookDisplay && this.unlockedBuildings.contains(culture.key + "_" + planSet.key))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedBuildings(Culture culture, String category) {
    int nbunlocked = 0;
    for (BuildingPlanSet planSet : culture.ListPlanSets) {
      if ((planSet.getFirstStartingPlan()).travelBookDisplay && this.unlockedBuildings.contains(culture.key + "_" + planSet.key) && category.equals((planSet.getFirstStartingPlan()).travelBookCategory))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedCultures() {
    int nbunlocked = 0;
    for (Culture culture : Culture.ListCultures) {
      if (isCultureUnlocked(culture))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedTradeGoods(Culture culture) {
    int nbunlocked = 0;
    for (TradeGood tradeGood : culture.goodsList) {
      if (tradeGood.travelBookDisplay && this.unlockedTradeGoods.contains(culture.key + "_" + tradeGood.key))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedTradeGoods(Culture culture, String category) {
    int nbunlocked = 0;
    for (TradeGood tradeGood : culture.goodsList) {
      if (tradeGood.travelBookDisplay && this.unlockedTradeGoods.contains(culture.key + "_" + tradeGood.key) && category.equals(tradeGood.travelBookCategory))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedVillagers(Culture culture) {
    int nbunlocked = 0;
    for (VillagerType vtype : culture.listVillagerTypes) {
      if (vtype.travelBookDisplay && this.unlockedVillagers.contains(culture.key + "_" + vtype.key))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedVillagers(Culture culture, String category) {
    int nbunlocked = 0;
    for (VillagerType vtype : culture.listVillagerTypes) {
      if (vtype.travelBookDisplay && this.unlockedVillagers.contains(culture.key + "_" + vtype.key) && category.equals(vtype.travelBookCategory))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public int getNbUnlockedVillages(Culture culture) {
    int nbunlocked = 0;
    for (VillageType vtype : culture.listVillageTypes) {
      if (vtype.travelBookDisplay && this.unlockedVillages.contains(culture.key + "_" + vtype.key))
        nbunlocked++; 
    } 
    return nbunlocked;
  }
  
  public EntityPlayer getPlayer() {
    return this.mw.world.getPlayerEntityByUUID(this.uuid);
  }
  
  public int getReputation(Building b) {
    int rep = 0;
    if (this.villageReputations.containsKey(b.getPos()))
      rep = ((Integer)this.villageReputations.get(b.getPos())).intValue(); 
    if (b.culture != null && this.cultureReputations.containsKey(b.culture.key))
      rep += ((Integer)this.cultureReputations.get(b.culture.key)).intValue(); 
    return rep;
  }
  
  public List<String> getWorldQuestStatus() {
    List<String> res = new ArrayList<>();
    boolean remaining = false;
    for (String questKey : Quest.WORLD_MISSION_KEYS) {
      String status = getActionData(questKey + "queststatus");
      String chapterName = LanguageUtilities.string("quest.cqchapter" + questKey);
      if (status == null) {
        res.add(LanguageUtilities.string("quest.cqchapternotstarted", new String[] { chapterName }));
        res.add("");
        res.add(LanguageUtilities.string("quest.cq" + questKey + "startexplanation"));
        remaining = true;
      } else {
        int mission = Integer.parseInt(status);
        int nbMission = ((Integer)Quest.WORLD_MISSION_NB.get(questKey)).intValue();
        if (mission >= nbMission) {
          res.add(LanguageUtilities.string("quest.cqchaptercompleted", new String[] { chapterName }));
        } else {
          res.add(LanguageUtilities.string("quest.cqchapterinprogress", new String[] { chapterName, "" + mission, "" + nbMission }));
          remaining = true;
        } 
      } 
      res.add("");
    } 
    if (!remaining) {
      res.add(LanguageUtilities.string("quest.cqallcompleted"));
      res.add("");
      res.add(LanguageUtilities.string("quest.cqcheckforupdates"));
    } 
    return res;
  }
  
  private String getWorldQuestStatusShort() {
    String res = LanguageUtilities.string("quest.creationqueststatusshort") + " ";
    for (String questKey : Quest.WORLD_MISSION_KEYS) {
      String status = getActionData(questKey + "queststatus");
      String chapterName = LanguageUtilities.string("quest.cqchapter" + questKey);
      if (status == null) {
        res = res + LanguageUtilities.string("quest.cqchapternotstartedshort", new String[] { chapterName }) + " ";
      } else {
        int mission = Integer.parseInt(status);
        int nbMission = ((Integer)Quest.WORLD_MISSION_NB.get(questKey)).intValue();
        if (mission >= nbMission) {
          res = res + LanguageUtilities.string("quest.cqchaptercompletedshort", new String[] { chapterName }) + " ";
        } else {
          res = res + LanguageUtilities.string("quest.cqchapterinprogressshort", new String[] { chapterName, "" + mission, "" + nbMission }) + " ";
        } 
      } 
    } 
    return res + " " + LanguageUtilities.string("quest.cqcheckquestlistandhelp", new String[] { Mill.proxy.getQuestKeyName() });
  }
  
  public boolean isBuildingUnlocked(Culture culture, BuildingPlanSet planSet) {
    String combinedKey = culture.key + "_" + planSet.key;
    return this.unlockedBuildings.contains(combinedKey);
  }
  
  public boolean isCultureUnlocked(Culture culture) {
    for (String key : this.unlockedBuildings) {
      if (key.startsWith(culture.key))
        return true; 
    } 
    for (String key : this.unlockedVillagers) {
      if (key.startsWith(culture.key))
        return true; 
    } 
    for (String key : this.unlockedVillages) {
      if (key.startsWith(culture.key))
        return true; 
    } 
    for (String key : this.unlockedTradeGoods) {
      if (key.startsWith(culture.key))
        return true; 
    } 
    return false;
  }
  
  public boolean isTagSet(String tag) {
    return this.profileTags.contains(tag);
  }
  
  public boolean isTradeGoodUnlocked(Culture culture, TradeGood tradeGood) {
    String combinedKey = culture.key + "_" + tradeGood.key;
    return this.unlockedTradeGoods.contains(combinedKey);
  }
  
  public boolean isVillagerUnlocked(Culture culture, VillagerType villagerType) {
    String combinedKey = culture.key + "_" + villagerType.key;
    return this.unlockedVillagers.contains(combinedKey);
  }
  
  public boolean isVillageUnlocked(Culture culture, VillageType villageType) {
    String combinedKey = culture.key + "_" + villageType.key;
    return this.unlockedVillages.contains(combinedKey);
  }
  
  public boolean isWorldQuestFinished(String questKey) {
    String status = getActionData(questKey + "queststatus");
    if (status == null)
      return false; 
    int mission = Integer.parseInt(status);
    int nbMission = ((Integer)Quest.WORLD_MISSION_NB.get(questKey)).intValue();
    if (mission < nbMission)
      return false; 
    return true;
  }
  
  private void loadActionData(File dataFile) {
    this.actionData.clear();
    if (dataFile.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(dataFile);
        String line = reader.readLine();
        while (line != null) {
          if (line.trim().length() > 0 && (line.split(":")).length == 2)
            this.actionData.put(line.split(":")[0], line.split(":")[1]); 
          line = reader.readLine();
        } 
        if (MillConfigValues.LogWorldGeneration >= 1)
          MillLog.major(null, "Loaded " + this.actionData.size() + " action data."); 
      } catch (Exception e) {
        MillLog.printException(e);
      }  
  }
  
  private void loadProfileConfig(File configFile) {
    if (configFile != null && configFile.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(configFile);
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.trim().length() > 0 && !line.startsWith("//")) {
            String[] temp = line.split("=");
            if (temp.length == 2) {
              String key = temp[0];
              String value = temp[1];
              if (key.equalsIgnoreCase("culture_reputation")) {
                String c = value.split(",")[0];
                int level = Integer.parseInt(value.split(",")[1]);
                this.cultureReputations.put(c, Integer.valueOf(level));
                continue;
              } 
              if (key.equalsIgnoreCase("culture_language")) {
                String c = value.split(",")[0];
                int level = Integer.parseInt(value.split(",")[1]);
                this.cultureLanguages.put(c, Integer.valueOf(level));
                continue;
              } 
              if (key.equalsIgnoreCase("village_reputations")) {
                Point p = new Point(value.split(",")[0]);
                int level = Integer.parseInt(value.split(",")[1]);
                this.villageReputations.put(p, Integer.valueOf(level));
                continue;
              } 
              if (key.equalsIgnoreCase("village_diplomacy")) {
                Point p = new Point(value.split(",")[0]);
                int level = Integer.parseInt(value.split(",")[1]);
                this.villageDiplomacy.put(p, Byte.valueOf((byte)level));
                continue;
              } 
              if (key.equalsIgnoreCase("player_name")) {
                this.playerName = value.trim();
                continue;
              } 
              if (key.equalsIgnoreCase("donation_mode"))
                this.donationActivated = Boolean.parseBoolean(value); 
            } 
          } 
        } 
        reader.close();
      } catch (IOException e) {
        MillLog.printException(e);
      }  
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(null, "Config loaded. generateVillages: " + MillConfigValues.generateVillages); 
  }
  
  private void loadProfileTags() {
    File tagsFile = new File(getDir(), "tags.txt");
    this.profileTags.clear();
    if (tagsFile.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(tagsFile);
        String line = reader.readLine();
        while (line != null) {
          if (line.trim().length() > 0)
            this.profileTags.add(line.trim()); 
          line = reader.readLine();
        } 
        if (MillConfigValues.LogWorldGeneration >= 1)
          MillLog.major(this, "Loaded " + this.profileTags.size() + " tags."); 
      } catch (Exception e) {
        MillLog.printException(e);
      }  
  }
  
  private void loadQuestInstances(File questDataFile) {
    this.questInstances.clear();
    this.villagersInQuests.clear();
    try {
      if (questDataFile != null && questDataFile.exists()) {
        BufferedReader reader = MillCommonUtilities.getReader(questDataFile);
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.trim().length() > 0 && !line.startsWith("//")) {
            QuestInstance qi = QuestInstance.loadFromString(this.mw, line, this);
            if (qi != null) {
              this.questInstances.add(qi);
              for (QuestInstanceVillager qiv : qi.villagers.values())
                this.villagersInQuests.put(Long.valueOf(qiv.id), qi); 
            } 
          } 
        } 
        reader.close();
      } 
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  private void loadUnlockedContent(File dataFile) {
    this.unlockedVillagers.clear();
    this.unlockedVillages.clear();
    this.unlockedBuildings.clear();
    this.unlockedTradeGoods.clear();
    if (dataFile.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(dataFile);
        String line = reader.readLine();
        while (line != null) {
          if (line.trim().length() > 0 && (line.split(":")).length == 2) {
            String type = line.split(":")[0];
            String key = line.split(":")[1];
            if (type.equals("villager")) {
              this.unlockedVillagers.add(key);
            } else if (type.equals("village")) {
              this.unlockedVillages.add(key);
            } else if (type.equals("building")) {
              this.unlockedBuildings.add(key);
            } else if (type.equals("tradegood")) {
              this.unlockedTradeGoods.add(key);
            } 
          } 
          line = reader.readLine();
        } 
        if (MillConfigValues.LogWorldGeneration >= 1)
          MillLog.major(null, "Loaded " + this.actionData.size() + " action data."); 
      } catch (Exception e) {
        MillLog.printException(e);
      }  
  }
  
  public void migrateToPlayer(EntityPlayer player) {
    MillLog.major(null, "Migrating profile of UUID " + this.uuid + " to UUID " + player.getUniqueID() + " (" + player.getName() + ").");
    UUID oldUUID = this.uuid;
    this.uuid = player.getUniqueID();
    this.playerName = player.getName();
    this.mw.profiles.put(this.uuid, this);
    this.mw.profiles.remove(oldUUID);
    saveProfile();
    File oldDir = new File(new File(this.mw.millenaireDir, "profiles"), oldUUID.toString());
    MillCommonUtilities.deleteDir(oldDir);
  }
  
  public void receiveDeclareReleaseNumberPacket(PacketBuffer packetBuffer) {
    this.releaseNumber = packetBuffer.readString(2048);
    if (MillConfigValues.LogNetwork >= 1)
      MillLog.major(this, "Declared release number: " + this.releaseNumber); 
  }
  
  public void receiveProfilePacket(PacketBuffer data) {
    int updateType = data.readInt();
    if (MillConfigValues.LogNetwork >= 2)
      MillLog.minor(null, "Receiving profile packet of content: " + updateType); 
    this.donationActivated = data.readBoolean();
    if (updateType == 1 || updateType == 2) {
      int nb = data.readInt();
      this.villageReputations.clear();
      int i;
      for (i = 0; i < nb; i++) {
        Point p = StreamReadWrite.readNullablePoint(data);
        int rep = data.readInt();
        if (MillConfigValues.LogNetwork >= 2)
          MillLog.minor(this, "Setting reputation to " + rep + " for village at " + p); 
        this.villageReputations.put(p, Integer.valueOf(rep));
      } 
      nb = data.readInt();
      this.cultureReputations.clear();
      for (i = 0; i < nb; i++) {
        String culture = data.readString(2048);
        int rep = data.readInt();
        this.cultureReputations.put(culture, Integer.valueOf(rep));
        if (MillConfigValues.LogNetwork >= 2)
          MillLog.minor(this, "Setting reputation to " + rep + " for culture " + culture); 
      } 
    } 
    if (updateType == 1 || updateType == 6) {
      int nb = data.readInt();
      this.cultureLanguages.clear();
      for (int i = 0; i < nb; i++)
        this.cultureLanguages.put(data.readString(2048), Integer.valueOf(data.readInt())); 
    } 
    if (updateType == 1 || updateType == 3) {
      int nb = data.readInt();
      this.villageDiplomacy.clear();
      for (int i = 0; i < nb; i++)
        this.villageDiplomacy.put(StreamReadWrite.readNullablePoint(data), Byte.valueOf((byte)data.readInt())); 
    } 
    if (updateType == 1 || updateType == 4) {
      int nb = data.readInt();
      this.actionData.clear();
      for (int i = 0; i < nb; i++)
        this.actionData.put(data.readString(2048), StreamReadWrite.readNullableString(data)); 
    } 
    if (updateType == 1 || updateType == 5) {
      int nb = data.readInt();
      this.profileTags.clear();
      for (int i = 0; i < nb; i++)
        this.profileTags.add(data.readString(2048)); 
    } 
    if (updateType == 1 || updateType == 7) {
      int nb = data.readInt();
      this.mw.globalTags.clear();
      for (int i = 0; i < nb; i++)
        this.mw.globalTags.add(data.readString(2048)); 
    } 
    if (updateType == 1 || updateType == 8) {
      this.unlockedVillagers.clear();
      this.unlockedVillagers.addAll(StreamReadWrite.readStringCollection(data));
      this.unlockedVillages.clear();
      this.unlockedVillages.addAll(StreamReadWrite.readStringCollection(data));
      this.unlockedBuildings.clear();
      this.unlockedBuildings.addAll(StreamReadWrite.readStringCollection(data));
      this.unlockedTradeGoods.clear();
      this.unlockedTradeGoods.addAll(StreamReadWrite.readStringCollection(data));
    } 
    showNewWorldMessage();
  }
  
  public void receiveQuestInstanceDestroyPacket(PacketBuffer data) {
    long uid = data.readLong();
    deleteQuestInstance(uid);
  }
  
  public void receiveQuestInstancePacket(PacketBuffer data) {
    QuestInstance qi = StreamReadWrite.readNullableQuestInstance(this.mw, data);
    deleteQuestInstance(qi.uniqueid);
    this.questInstances.add(qi);
    for (String key : qi.villagers.keySet())
      this.villagersInQuests.put(Long.valueOf(((QuestInstanceVillager)qi.villagers.get(key)).id), qi); 
  }
  
  private void saveActionData() {
    if (this.mw.world.isRemote)
      return; 
    File configFile = new File(getDir(), "actiondata.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(configFile);
      for (String key : this.actionData.keySet())
        writer.write(key + ":" + (String)this.actionData.get(key) + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void saveProfile() {
    if (this.mw.world.isRemote)
      return; 
    saveProfileConfig();
    saveProfileTags();
    saveQuestInstances();
    saveActionData();
    saveUnloadedContent();
  }
  
  private void saveProfileConfig() {
    if (this.mw.world.isRemote)
      return; 
    File configFile = new File(getDir(), "config.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(configFile);
      writer.write("player_name=" + this.playerName + "\n");
      writer.write("donation_mode=" + this.donationActivated + "\n");
      for (String c : this.cultureReputations.keySet())
        writer.write("culture_reputation=" + c + "," + this.cultureReputations.get(c) + "\n"); 
      for (String c : this.cultureLanguages.keySet())
        writer.write("culture_language=" + c + "," + this.cultureLanguages.get(c) + "\n"); 
      for (Point p : this.villageReputations.keySet())
        writer.write("village_reputations=" + p + "," + this.villageReputations.get(p) + "\n"); 
      for (Point p : this.villageDiplomacy.keySet())
        writer.write("village_diplomacy=" + p + "," + this.villageDiplomacy.get(p) + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  private void saveProfileTags() {
    if (this.mw.world.isRemote)
      return; 
    File configFile = new File(getDir(), "tags.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(configFile);
      for (String tag : this.profileTags)
        writer.write(tag + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void saveQuestInstances() {
    if (this.mw.world.isRemote)
      return; 
    File questDataFile = new File(getDir(), "quests.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(questDataFile);
      for (QuestInstance qi : this.questInstances)
        writer.write(qi.writeToString() + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  private void saveUnloadedContent() {
    if (this.mw.world.isRemote)
      return; 
    File configFile = new File(getDir(), "unlockedcontent.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(configFile);
      for (String key : this.unlockedVillagers)
        writer.write("villager:" + key + "\n"); 
      for (String key : this.unlockedVillages)
        writer.write("village:" + key + "\n"); 
      for (String key : this.unlockedBuildings)
        writer.write("building:" + key + "\n"); 
      for (String key : this.unlockedTradeGoods)
        writer.write("tradegood:" + key + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void sendInitialPackets() {
    if (MillConfigValues.LogNetwork >= 1)
      MillLog.major(this, "Sending initial packets to " + this.playerName); 
    sendProfilePacket(1);
    for (QuestInstance qi : this.questInstances)
      sendQuestInstancePacket(qi); 
  }
  
  public void sendProfilePacket(int updateType) {
    if (this.mw.world.isRemote)
      return; 
    if (getPlayer() == null) {
      MillLog.printException((Throwable)new MillLog.MillenaireException("Null player while trying to send packet:"));
      return;
    } 
    PacketBuffer data = ServerSender.getPacketBuffer();
    data.writeInt(101);
    data.writeInt(updateType);
    data.writeBoolean(this.donationActivated);
    if (updateType == 1 || updateType == 2) {
      data.writeInt(this.villageReputations.size());
      for (Point p : this.villageReputations.keySet()) {
        StreamReadWrite.writeNullablePoint(p, data);
        data.writeInt(((Integer)this.villageReputations.get(p)).intValue());
      } 
      data.writeInt(this.cultureReputations.size());
      for (String culture : this.cultureReputations.keySet()) {
        data.writeString(culture);
        data.writeInt(((Integer)this.cultureReputations.get(culture)).intValue());
      } 
    } 
    if (updateType == 1 || updateType == 6) {
      data.writeInt(this.cultureLanguages.size());
      for (String culture : this.cultureLanguages.keySet()) {
        data.writeString(culture);
        data.writeInt(((Integer)this.cultureLanguages.get(culture)).intValue());
      } 
    } 
    if (updateType == 1 || updateType == 3) {
      data.writeInt(this.villageDiplomacy.size());
      for (Point p : this.villageDiplomacy.keySet()) {
        StreamReadWrite.writeNullablePoint(p, data);
        data.writeInt(((Byte)this.villageDiplomacy.get(p)).byteValue());
      } 
    } 
    if (updateType == 1 || updateType == 4) {
      data.writeInt(this.actionData.size());
      for (String key : this.actionData.keySet()) {
        data.writeString(key);
        StreamReadWrite.writeNullableString(this.actionData.get(key), data);
      } 
    } 
    if (updateType == 1 || updateType == 5) {
      data.writeInt(this.profileTags.size());
      for (String tag : this.profileTags)
        data.writeString(tag); 
    } 
    if (updateType == 1 || updateType == 7) {
      data.writeInt(this.mw.globalTags.size());
      for (String tag : this.mw.globalTags)
        data.writeString(tag); 
    } 
    if (updateType == 1 || updateType == 8) {
      StreamReadWrite.writeStringCollection(this.unlockedVillagers, data);
      StreamReadWrite.writeStringCollection(this.unlockedVillages, data);
      StreamReadWrite.writeStringCollection(this.unlockedBuildings, data);
      StreamReadWrite.writeStringCollection(this.unlockedTradeGoods, data);
    } 
    ServerSender.sendPacketToPlayer(data, getPlayer());
  }
  
  public void sendQuestInstanceDestroyPacket(long uid) {
    if (this.mw.world.isRemote)
      return; 
    PacketBuffer data = ServerSender.getPacketBuffer();
    data.writeInt(103);
    data.writeLong(uid);
    ServerSender.sendPacketToPlayer(data, getPlayer());
  }
  
  public void sendQuestInstancePacket(QuestInstance qi) {
    if (this.mw.world.isRemote)
      return; 
    for (QuestInstanceVillager qiv : qi.villagers.values()) {
      Building th = qiv.getTownHall(this.mw.world);
      if (th != null && 
        !this.buildingsSent.containsKey(th.getPos()))
        th.sendBuildingPacket(getPlayer(), false); 
    } 
    PacketBuffer data = ServerSender.getPacketBuffer();
    data.writeInt(102);
    StreamReadWrite.writeNullableQuestInstance(qi, data);
    ServerSender.sendPacketToPlayer(data, getPlayer());
  }
  
  public void setActionData(String key, String value) {
    if (!this.actionData.containsKey(key) || !((String)this.actionData.get(key)).equals(value)) {
      this.actionData.put(key, value);
      saveActionData();
      sendProfilePacket(4);
    } 
  }
  
  public void setTag(String tag) {
    if (!this.profileTags.contains(tag)) {
      this.profileTags.add(tag);
      saveProfileTags();
      sendProfilePacket(5);
    } 
  }
  
  public void showNewWorldMessage() {
    if (!this.showNewWorldMessageDone) {
      MillConfigValues.checkBonusCode(false);
      if (MillConfigValues.displayStart)
        ServerSender.sendChat(getPlayer(), TextFormatting.YELLOW, getWorldQuestStatusShort()); 
      (new MillCommonUtilities.VersionCheckThread()).start();
      this.showNewWorldMessageDone = true;
    } 
  }
  
  public void testQuests() {
    if (!this.mw.world.isRemote) {
      int j;
      boolean change = false;
      for (int i = this.questInstances.size() - 1; i >= 0; i--) {
        QuestInstance qi = this.questInstances.get(i);
        try {
          change |= qi.checkStatus(this.mw.world);
        } catch (Exception e) {
          try {
            MillLog.printException("Error while updating quest " + qi + ": ", e);
            qi.destroyQuest();
            MillLog.warning(this, "Destroyed quest following exception.");
          } catch (Exception e2) {
            MillLog.printException("Error while printing error on quest destruction: ", e);
          } 
        } 
      } 
      for (Quest q : Quest.quests.values()) {
        QuestInstance qi = q.testQuest(this.mw, this);
        change = change || (qi != null);
        if (qi != null)
          sendQuestInstancePacket(qi); 
      } 
      if (change)
        saveQuestInstances(); 
    } 
  }
  
  public String toString() {
    return "Profile: " + this.uuid + "/" + this.playerName;
  }
  
  public boolean unlockBuilding(Culture culture, BuildingPlanSet planSet) {
    if (planSet == null)
      return false; 
    String combinedKey = culture.key + "_" + planSet.key;
    if (!this.unlockedBuildings.contains(combinedKey)) {
      this.unlockedBuildings.add(combinedKey);
      int nbTotal = ((List)culture.ListPlanSets.stream().filter(p -> (p.getFirstStartingPlan()).travelBookDisplay).collect(Collectors.toList())).size();
      if ((planSet.getFirstStartingPlan()).travelBookDisplay) {
        ServerSender.sendContentUnlocked(getPlayer(), 1, culture.key, planSet.key, getNbUnlockedBuildings(culture), nbTotal);
        if (this.unlockedBuildings.size() == 1)
          ServerSender.sendTranslatedSentence(getPlayer(), '9', "travelbook.unlockedbuilding_first", new String[0]); 
      } 
      sendProfilePacket(8);
      saveProfile();
      return true;
    } 
    return false;
  }
  
  public boolean unlockTradeGood(Culture culture, TradeGood tradeGood) {
    String combinedKey = culture.key + "_" + tradeGood.key;
    if (!this.unlockedTradeGoods.contains(combinedKey)) {
      this.unlockedTradeGoods.add(combinedKey);
      int nbTotal = ((List)culture.goodsList.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
      if (tradeGood.travelBookDisplay) {
        ServerSender.sendContentUnlocked(getPlayer(), 4, culture.key, tradeGood.key, getNbUnlockedTradeGoods(culture), nbTotal);
        if (this.unlockedTradeGoods.size() == 1)
          ServerSender.sendTranslatedSentence(getPlayer(), '9', "travelbook.unlockedtradegood_first", new String[0]); 
      } 
      sendProfilePacket(8);
      saveProfile();
      return true;
    } 
    return false;
  }
  
  public boolean unlockTradeGoods(Culture culture, List<TradeGood> tradeGoods) {
    List<String> unlockedGoods = new ArrayList<>();
    for (TradeGood tradeGood : tradeGoods) {
      String combinedKey = culture.key + "_" + tradeGood.key;
      if (!this.unlockedTradeGoods.contains(combinedKey)) {
        this.unlockedTradeGoods.add(combinedKey);
        unlockedGoods.add(tradeGood.key);
      } 
    } 
    if (unlockedGoods.isEmpty())
      return false; 
    int nbTotal = ((List)culture.goodsList.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
    ServerSender.sendContentUnlockedMultiple(getPlayer(), 5, culture.key, unlockedGoods, getNbUnlockedTradeGoods(culture), nbTotal);
    if (this.unlockedTradeGoods.size() == unlockedGoods.size())
      ServerSender.sendTranslatedSentence(getPlayer(), '9', "travelbook.unlockedtradegood_first", new String[0]); 
    sendProfilePacket(8);
    saveProfile();
    return true;
  }
  
  public boolean unlockVillage(Culture culture, VillageType villageType) {
    String combinedKey = culture.key + "_" + villageType.key;
    if (!this.unlockedVillages.contains(combinedKey)) {
      this.unlockedVillages.add(combinedKey);
      int nbTotal = ((List)culture.listVillageTypes.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
      if (villageType.travelBookDisplay && !villageType.lonebuilding) {
        ServerSender.sendContentUnlocked(getPlayer(), 2, culture.key, villageType.key, getNbUnlockedVillages(culture), nbTotal);
        if (this.unlockedVillages.size() == 1)
          ServerSender.sendTranslatedSentence(getPlayer(), '9', "travelbook.unlockedvillage_first", new String[0]); 
      } 
      sendProfilePacket(8);
      saveProfile();
      return true;
    } 
    return false;
  }
  
  public boolean unlockVillager(Culture culture, VillagerType villagerType) {
    String combinedKey = culture.key + "_" + villagerType.key;
    if (!this.unlockedVillagers.contains(combinedKey)) {
      this.unlockedVillagers.add(combinedKey);
      int nbTotal = ((List)culture.listVillagerTypes.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList())).size();
      if (villagerType.travelBookDisplay) {
        ServerSender.sendContentUnlocked(getPlayer(), 3, culture.key, villagerType.key, getNbUnlockedVillagers(culture), nbTotal);
        if (this.unlockedVillagers.size() == 1)
          ServerSender.sendTranslatedSentence(getPlayer(), '9', "travelbook.unlockedvillager_first", new String[0]); 
      } 
      sendProfilePacket(8);
      saveProfile();
      return true;
    } 
    return false;
  }
  
  public void updateProfile() {
    EntityPlayer player = getPlayer();
    if (this.connected)
      if (player != null) {
        clearFarAwayPanels();
        if (player.dimension == 0 && 
          !this.connectionActionDone && !this.mw.world.isRemote) {
          sendInitialPackets();
          this.connectionActionDone = true;
        } 
        if (player != null && this.mw.world.getWorldTime() % 1000L == 0L && this.mw.world.isDaytime())
          testQuests(); 
        if (MillConfigValues.DEV && player != null && this.mw.world.getWorldTime() % 20L == 0L && this.mw.world.isDaytime())
          testQuests(); 
      } else {
        this.connectionActionDone = false;
      }  
  }
}
