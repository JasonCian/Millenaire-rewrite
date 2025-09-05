package org.millenaire.common.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.quest.SpecialQuestActions;
import org.millenaire.common.utilities.DevModUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;

public class MillWorldData {
  public static final String CULTURE_CONTROL = "culturecontrol_";
  
  public static final String PUJAS = "pujas";
  
  public static final String MAYANSACRIFICES = "mayansacrifices";
  
  private static HashMap<Point, String> buildingsTags = new HashMap<>();
  
  private static HashMap<Point, Integer> buildingsVariation = new HashMap<>();
  
  private static HashMap<Point, String> buildingsLocation = new HashMap<>();
  
  private final HashMap<Point, Building> buildings = new HashMap<>();
  
  private final HashMap<Long, MillVillager> villagers = new HashMap<>();
  
  private final HashMap<Long, VillagerRecord> villagerRecords = new HashMap<>();
  
  public final List<String> globalTags = new ArrayList<>();
  
  public MillCommonUtilities.VillageList loneBuildingsList = new MillCommonUtilities.VillageList();
  
  public final File millenaireDir;
  
  public File saveDir = null;
  
  public MillCommonUtilities.VillageList villagesList = new MillCommonUtilities.VillageList();
  
  public long lastWorldUpdate = 0L;
  
  public HashMap<UUID, UserProfile> profiles = new HashMap<>();
  
  public List<TileEntityPanel.PanelPacketInfo> panelPacketInfos = new ArrayList<>();
  
  public World world;
  
  public boolean millenaireEnabled = false;
  
  private int lastForcePreloadUpdate;
  
  private long lastWorldTime = Long.MAX_VALUE;
  
  public boolean generateVillages;
  
  public boolean generateVillagesSet = false;
  
  public MillWorldData(World world) {
    this.world = world;
    if (!world.isRemote) {
      this.saveDir = MillCommonUtilities.getWorldSaveDir(world);
      this.millenaireEnabled = true;
      this.millenaireDir = new File(this.saveDir, "millenaire");
      if (!this.millenaireDir.exists())
        this.millenaireDir.mkdir(); 
    } else {
      this.millenaireDir = null;
    } 
    Culture.removeServerContent();
  }
  
  public void addBuilding(Building b, Point p) {
    this.buildings.put(p, b);
  }
  
  public Collection<Building> allBuildings() {
    return this.buildings.values();
  }
  
  public boolean buildingExists(Point p) {
    return this.buildings.containsKey(p);
  }
  
  public void checkConnections() {
    for (UserProfile profile : this.profiles.values()) {
      if (profile.connected && 
        profile.getPlayer() == null)
        profile.disconnectUser(); 
    } 
  }
  
  public void clearGlobalTag(String tag) {
    if (this.globalTags.contains(tag)) {
      this.globalTags.remove(tag);
      saveGlobalTags();
      if (!this.world.isRemote)
        for (UserProfile up : this.profiles.values()) {
          if (up.connected)
            up.sendProfilePacket(7); 
        }  
    } 
  }
  
  public void clearPanelQueue() {
    List<TileEntityPanel.PanelPacketInfo> toDelete = new ArrayList<>();
    for (TileEntityPanel.PanelPacketInfo pinfo : this.panelPacketInfos) {
      TileEntityPanel panel = pinfo.pos.getPanel(this.world);
      if (panel != null) {
        panel.panelType = pinfo.panelType;
        panel.buildingPos = pinfo.buildingPos;
        panel.villager_id = pinfo.villager_id;
        toDelete.add(pinfo);
      } 
    } 
    for (TileEntityPanel.PanelPacketInfo pinfo : toDelete)
      this.panelPacketInfos.remove(pinfo); 
  }
  
  public void clearVillagerOfId(long id) {
    if (this.villagers.get(Long.valueOf(id)) != null) {
      MillVillager villager = this.villagers.get(Long.valueOf(id));
      if (MillConfigValues.LogVillagerSpawn >= 1)
        MillLog.major(this, "Removing village from global list: " + villager); 
      if (this.villagers.remove(Long.valueOf(id)) == null)
        MillLog.error(this, "Could not delete villager " + villager); 
      if (villager.getHouse() != null)
        villager.getHouse().rebuildVillagerList(); 
      if (villager.getTownHall() != null && villager.getTownHall() != villager.getHouse())
        villager.getTownHall().rebuildVillagerList(); 
    } else if (MillConfigValues.LogVillagerSpawn >= 1) {
      MillLog.major(this, "Could not find villager of id " + id + " to despawn him.");
    } 
  }
  
  public void displayTagActionData(EntityPlayer player) {
    String s = "";
    for (String tag : this.globalTags)
      s = s + tag + " "; 
    ServerSender.sendChat(player, TextFormatting.GREEN, "Tags: " + s);
    ServerSender.sendChat(player, TextFormatting.GREEN, "ActionData: " + s);
    ServerSender.sendChat(player, TextFormatting.GREEN, "Time: " + (this.world.getWorldTime() % 24000L) + " / " + this.world.getWorldTime());
  }
  
  public void displayVillageList(EntityPlayer player, boolean loneBuildings) {
    MillCommonUtilities.VillageList list;
    if (loneBuildings) {
      list = this.loneBuildingsList;
    } else {
      list = this.villagesList;
    } 
    UserProfile profile = getProfile(player);
    List<MillCommonUtilities.VillageInfo> villageList = new ArrayList<>();
    int i;
    for (i = 0; i < list.names.size(); i++) {
      Point p = list.pos.get(i);
      int distance = MathHelper.floor(p.horizontalDistanceTo((Entity)player));
      if (distance <= MillConfigValues.BackgroundRadius) {
        String loaded;
        VillageType villageType;
        String direction = (new Point((Entity)player)).directionTo(p, true);
        Building townHall = getBuilding(p);
        if (townHall == null) {
          loaded = "command.inactive";
        } else if (townHall.isActive) {
          loaded = "command.active";
        } else if (!townHall.isAreaLoaded) {
          loaded = "command.inactive";
        } else {
          loaded = "command.frozen";
        } 
        if (loneBuildings) {
          villageType = Culture.getCultureByName(list.cultures.get(i)).getLoneBuildingType(list.types.get(i));
        } else {
          villageType = Culture.getCultureByName(list.cultures.get(i)).getVillageType(list.types.get(i));
        } 
        MillCommonUtilities.VillageInfo vi = new MillCommonUtilities.VillageInfo();
        vi.distance = distance;
        if (villageType != null) {
          String villageTypeNameKey = villageType.getNameTranslationKey(profile);
          if (villageTypeNameKey != null) {
            vi.textKey = "command.villagelisttranslated";
            vi.values = new String[] { list.names.get(i), loaded, "" + distance, direction, villageType.name, villageTypeNameKey };
          } else {
            vi.textKey = "command.villagelist";
            vi.values = new String[] { list.names.get(i), loaded, "" + distance, direction, villageType.name };
          } 
        } 
        villageList.add(vi);
      } 
    } 
    if (!loneBuildings)
      for (i = 0; i < this.loneBuildingsList.names.size(); i++) {
        VillageType village = Culture.getCultureByName(this.loneBuildingsList.cultures.get(i)).getLoneBuildingType(this.loneBuildingsList.types.get(i));
        if (village.keyLonebuilding || village.keyLoneBuildingGenerateTag != null)
          if (!village.generatedForPlayer || player.getName().equalsIgnoreCase(this.loneBuildingsList.generatedFor.get(i))) {
            Point p = this.loneBuildingsList.pos.get(i);
            int distance = MathHelper.floor(p.horizontalDistanceTo((Entity)player));
            if (distance <= 2000) {
              String direction = (new Point((Entity)player)).directionTo(p, true);
              MillCommonUtilities.VillageInfo vi = new MillCommonUtilities.VillageInfo();
              vi.distance = distance;
              if (village != null) {
                vi.textKey = "command.villagelistkeylonebuilding";
                vi.values = new String[] { village.name, "" + distance, direction };
              } 
              villageList.add(vi);
            } 
          }  
      }  
    if (villageList.size() == 0) {
      ServerSender.sendTranslatedSentence(player, '7', "command.noknowvillage", new String[0]);
    } else {
      Collections.sort(villageList);
      for (MillCommonUtilities.VillageInfo vi : villageList)
        ServerSender.sendTranslatedSentence(player, '7', vi.textKey, vi.values); 
      String direction = "other.tothe" + MillCommonUtilities.getCardinalDirectionStringFromAngle((int)player.rotationYaw);
      ServerSender.sendTranslatedSentence(player, '2', "command.facingdirection", new String[] { direction });
    } 
  }
  
  public void forcePreload() {
    int centreX, centreZ;
    if (this.world.isRemote || MillConfigValues.forcePreload <= 0)
      return; 
    this.lastForcePreloadUpdate++;
    if (this.lastForcePreloadUpdate < 50)
      return; 
    this.lastForcePreloadUpdate = 0;
    if (this.world.playerEntities.size() > 0) {
      Object o = this.world.playerEntities.get(0);
      EntityPlayer player = (EntityPlayer)o;
      centreX = (int)(player.posX / 16.0D);
      centreZ = (int)(player.posZ / 16.0D);
    } else {
      centreX = this.world.getSpawnPoint().getX() / 16;
      centreZ = this.world.getSpawnPoint().getZ() / 16;
    } 
    int nbGenerated = 0;
    for (int radius = 1; radius < MillConfigValues.forcePreload; radius++) {
      for (int i = -MillConfigValues.forcePreload; i < MillConfigValues.forcePreload && nbGenerated < 100; i++) {
        for (int j = -MillConfigValues.forcePreload; j < MillConfigValues.forcePreload && nbGenerated < 100; j++) {
          if (i * i + j * j < radius * radius && 
            !this.world.isChunkGeneratedAt(i + centreX, j + centreZ)) {
            this.world.getChunkProvider().provideChunk(i + centreX, j + centreZ);
            MillLog.minor(this, "Forcing population of chunk " + (i + centreX) + "/" + (j + centreZ));
            nbGenerated++;
          } 
        } 
      } 
    } 
    if (this.world.getChunkProvider() instanceof ChunkProviderServer)
      ((ChunkProviderServer)this.world.getChunkProvider()).saveChunks(false); 
  }
  
  public Collection<MillVillager> getAllKnownVillagers() {
    return this.villagers.values();
  }
  
  public Building getBuilding(Point p) {
    if (this.buildings.containsKey(p)) {
      if (this.buildings.get(p) == null) {
        MillLog.error(this, "Building record for " + p + " is null.");
      } else if (((Building)this.buildings.get(p)).location == null) {
        MillLog.printException("Building location for " + p + " is null.", new Exception());
      } 
      return this.buildings.get(p);
    } 
    if (MillConfigValues.LogWorldInfo >= 2)
      MillLog.minor(this, "Could not find a building at location " + p + " amoung " + this.buildings.size() + " records."); 
    return null;
  }
  
  public Building getClosestVillage(Point p) {
    int bestDistance = Integer.MAX_VALUE;
    Building bestVillage = null;
    for (Point villageCoord : this.villagesList.pos) {
      int dist = (int)p.distanceToSquared(villageCoord);
      if (bestVillage == null || dist < bestDistance) {
        Building village = getBuilding(villageCoord);
        if (village != null) {
          bestVillage = village;
          bestDistance = dist;
        } 
      } 
    } 
    return bestVillage;
  }
  
  public List<Point> getCombinedVillagesLoneBuildings() {
    List<Point> thPosLists = new ArrayList<>(this.villagesList.pos);
    thPosLists.addAll(this.loneBuildingsList.pos);
    return thPosLists;
  }
  
  public UserProfile getProfile(EntityPlayer player) {
    if (this.profiles.containsKey(player.getUniqueID()))
      return this.profiles.get(player.getUniqueID()); 
    if (MillConfigValues.autoConvertProfiles && !Mill.proxy.isTrueServer() && this.profiles.size() > 0) {
      UserProfile userProfile = this.profiles.get(this.profiles.keySet().stream().findFirst().get());
      userProfile.migrateToPlayer(player);
      return userProfile;
    } 
    UserProfile profile = new UserProfile(this, player);
    this.profiles.put(profile.uuid, profile);
    return profile;
  }
  
  public UserProfile getProfile(UUID uuid) {
    if (this.profiles.containsKey(uuid))
      return this.profiles.get(uuid); 
    String name = this.world.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(uuid).getName();
    UserProfile profile = new UserProfile(this, uuid, name);
    this.profiles.put(profile.uuid, profile);
    return profile;
  }
  
  public MillVillager getVillagerById(long id) {
    return this.villagers.get(Long.valueOf(id));
  }
  
  public VillagerRecord getVillagerRecordById(long villagerId) {
    return this.villagerRecords.get(Long.valueOf(villagerId));
  }
  
  public boolean isGlobalTagSet(String tag) {
    return this.globalTags.contains(tag);
  }
  
  private void loadBuildings() {
    long startTime = System.currentTimeMillis();
    File buildingsDir = new File(this.millenaireDir, "buildings");
    if (!buildingsDir.exists())
      buildingsDir.mkdir(); 
    for (File file : buildingsDir.listFiles((FilenameFilter)new MillCommonUtilities.ExtFileFilter("gz"))) {
      try {
        FileInputStream fileinputstream = new FileInputStream(file);
        NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
        NBTTagList nbttaglist = nbttagcompound.getTagList("buildings", 10);
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
          NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
          new Building(this, nbttagcompound1);
        } 
      } catch (Exception e) {
        MillLog.printException("Error when attempting to load building file " + file.getAbsolutePath() + ":", e);
      } catch (OutOfMemoryError e) {
        MillLog.printException("Out of memory error when attempting to load building file " + file.getAbsolutePath() + ":", e);
      } 
    } 
    if (MillConfigValues.LogHybernation >= 1) {
      for (Building b : this.buildings.values())
        MillLog.major(null, b + " - " + b.culture); 
      MillLog.major(this, "Loaded " + this.buildings.size() + " in " + (System.currentTimeMillis() - startTime) + " ms.");
    } 
  }
  
  public void loadData() {
    if (this.world.isRemote)
      return; 
    loadWorldConfig();
    loadVillagesAndLoneBuildingsLists();
    loadGlobalTags();
    loadBuildings();
    loadVillagerRecords();
    loadProfiles();
  }
  
  private void loadGlobalTags() {
    File tagsFile = new File(this.millenaireDir, "tags.txt");
    this.globalTags.clear();
    if (tagsFile.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(tagsFile);
        String line = reader.readLine();
        while (line != null) {
          if (line.trim().length() > 0)
            this.globalTags.add(line.trim()); 
          line = reader.readLine();
        } 
        if (MillConfigValues.LogWorldGeneration >= 1)
          MillLog.major(null, "Loaded " + this.globalTags.size() + " tags."); 
      } catch (Exception e) {
        MillLog.printException(e);
      }  
  }
  
  private void loadProfiles() {
    File profilesDir = new File(this.millenaireDir, "profiles");
    if (!profilesDir.exists())
      profilesDir.mkdirs(); 
    for (File profileDir : profilesDir.listFiles()) {
      if (profileDir.isDirectory() && !profileDir.isHidden()) {
        UserProfile profile = UserProfile.readProfile(this, profileDir);
        if (profile != null)
          this.profiles.put(profile.uuid, profile); 
      } 
    } 
  }
  
  private void loadVillagerRecords() {
    if (this.world.isRemote)
      return; 
    File file1 = new File(this.millenaireDir, "villagerRecords.gz");
    if (file1.exists())
      try {
        FileInputStream fileinputstream = new FileInputStream(file1);
        NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
        NBTTagList nbttaglist = nbttagcompound.getTagList("villagersrecords", 10);
        if (MillConfigValues.LogHybernation >= 1)
          MillLog.major(this, "Loading " + nbttaglist.tagCount() + " villagers from main list. Count at start: " + this.villagerRecords.size()); 
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
          NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
          VillagerRecord vr = VillagerRecord.read(this, nbttagcompound1, "vr");
          if (vr == null) {
            MillLog.error(this, "Couldn't load VR record.");
          } else {
            registerVillagerRecord(vr, false);
            if (MillConfigValues.LogHybernation >= 2)
              MillLog.minor(this, "Loaded VR: " + vr); 
          } 
        } 
        saveVillagerRecords();
        if (MillConfigValues.LogHybernation >= 1)
          MillLog.major(this, "Loading from main list over. Count at end: " + this.villagerRecords.size()); 
      } catch (Exception e) {
        MillLog.printException("Error when attempting to load villager records file " + file1.getAbsolutePath() + ":", e);
      }  
  }
  
  private void loadVillagesAndLoneBuildingsLists() {
    File villageLog = new File(this.millenaireDir, "villages.txt");
    if (villageLog.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(villageLog);
        String line = reader.readLine();
        while (line != null) {
          if (line.trim().length() > 0) {
            String[] p = line.split(";")[1].split("/");
            String type = "";
            if ((line.split(";")).length > 2)
              type = line.split(";")[2]; 
            String culture = "";
            if ((line.split(";")).length > 3)
              culture = line.split(";")[3]; 
            Culture c = Culture.getCultureByName(culture);
            if (c != null) {
              String generatedFor = null;
              if ((line.split(";")).length > 4)
                generatedFor = line.split(";")[4]; 
              registerVillageLocation(this.world, new Point(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])), line.split(";")[0], c.getVillageType(type), c, false, generatedFor);
            } else {
              MillLog.error(this, "Tried loading a village of culture " + culture + " that cannot be found.");
            } 
          } 
          line = reader.readLine();
        } 
        if (MillConfigValues.LogWorldGeneration >= 1)
          MillLog.major(null, "Loaded " + this.villagesList.names.size() + " village positions."); 
      } catch (Exception e) {
        MillLog.printException(e);
      }  
    villageLog = new File(this.millenaireDir, "lonebuildings.txt");
    if (villageLog.exists())
      try {
        BufferedReader reader = MillCommonUtilities.getReader(villageLog);
        String line = reader.readLine();
        while (line != null) {
          if (line.trim().length() > 0) {
            String[] p = line.split(";")[1].split("/");
            String type = "";
            if ((line.split(";")).length > 2)
              type = line.split(";")[2]; 
            String culture = "";
            if ((line.split(";")).length > 3)
              culture = line.split(";")[3]; 
            Culture c = Culture.getCultureByName(culture);
            String generatedFor = null;
            if ((line.split(";")).length > 4)
              generatedFor = line.split(";")[4]; 
            registerLoneBuildingsLocation(this.world, new Point(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])), line.split(";")[0], c.getLoneBuildingType(type), c, false, generatedFor);
          } 
          line = reader.readLine();
        } 
        if (MillConfigValues.LogWorldGeneration >= 1)
          MillLog.major(null, "Loaded " + this.loneBuildingsList.names.size() + " lone buildings positions."); 
      } catch (Exception e) {
        MillLog.printException(e);
      }  
  }
  
  private void loadWorldConfig() {
    this.generateVillages = MillConfigValues.generateVillages;
    File configFile = new File(this.millenaireDir, "config.txt");
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
              if (key.equalsIgnoreCase("generate_villages")) {
                this.generateVillages = Boolean.parseBoolean(value);
                this.generateVillagesSet = true;
              } 
            } 
          } 
        } 
        reader.close();
      } catch (IOException e) {
        MillLog.printException(e);
      }  
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(null, "Config loaded. generateVillages: " + this.generateVillages); 
  }
  
  public int nbCultureInGeneratedVillages() {
    List<String> cultures = new ArrayList<>();
    for (int i = 0; i < this.villagesList.names.size(); i++) {
      if (!cultures.contains(this.villagesList.cultures.get(i)))
        cultures.add(this.villagesList.cultures.get(i)); 
    } 
    return cultures.size();
  }
  
  private void rebuildVillagerList() {
    int oldVillagerNumber = this.villagers.size();
    this.villagers.clear();
    Map<Building, Set<MillVillager>> villagersBuilding = new HashMap<>();
    for (Building building : this.buildings.values())
      villagersBuilding.put(building, new HashSet<>()); 
    List<MillVillager> villagersToDespawn = new ArrayList<>();
    for (Entity entity : this.world.loadedEntityList) {
      if (entity instanceof MillVillager) {
        MillVillager villager = (MillVillager)entity;
        if (villager.getVillagerId() == 7165774886634408160L && !this.world.isRemote) {
          MillLog.major(villager, "Villager with forbidden ID again.");
          villagersToDespawn.add(villager);
          continue;
        } 
        if (!this.villagerRecords.containsKey(Long.valueOf(villager.getVillagerId())) && !this.world.isRemote) {
          MillLog.temp(villager, "Villager without registered record.");
          villagersToDespawn.add(villager);
          continue;
        } 
        if (this.villagers.containsKey(Long.valueOf(villager.getVillagerId())) && 
          MillConfigValues.LogVillagerSpawn >= 1) {
          MillLog.major(this, "Duplicate villager: " + villager);
          villagersToDespawn.add(villager);
        } 
        this.villagers.put(Long.valueOf(villager.getVillagerId()), villager);
        if (villager.getTownHall() != null && villagersBuilding.get(villager.getTownHall()) != null)
          ((Set<MillVillager>)villagersBuilding.get(villager.getTownHall())).add(villager); 
        if (villager.getHouse() != null && villager.getHouse() != villager.getTownHall() && villagersBuilding.get(villager.getHouse()) != null)
          ((Set<MillVillager>)villagersBuilding.get(villager.getHouse())).add(villager); 
      } 
    } 
    for (Building building : villagersBuilding.keySet())
      building.setNewVillagerList(villagersBuilding.get(building)); 
    for (MillVillager villager : villagersToDespawn)
      villager.despawnVillagerSilent(); 
    if (MillConfigValues.LogVillagerSpawn >= 1 && oldVillagerNumber != this.villagers.size())
      MillLog.major(this, "Villager list rebuilt. Now contains: " + this.villagers.size() + " villagers instead of " + oldVillagerNumber + " from " + this.world.loadedEntityList.size() + " entities."); 
  }
  
  @Deprecated
  public void receiveVillageListPacket(PacketBuffer data) {
    if (MillConfigValues.LogNetwork >= 2)
      MillLog.minor(this, "Received village list packet."); 
    this.villagesList = new MillCommonUtilities.VillageList();
    this.loneBuildingsList = new MillCommonUtilities.VillageList();
    int nb = data.readInt();
    int i;
    for (i = 0; i < nb; i++) {
      this.villagesList.pos.add(StreamReadWrite.readNullablePoint(data));
      this.villagesList.names.add(StreamReadWrite.readNullableString(data));
      this.villagesList.cultures.add(StreamReadWrite.readNullableString(data));
      this.villagesList.types.add(StreamReadWrite.readNullableString(data));
    } 
    nb = data.readInt();
    for (i = 0; i < nb; i++) {
      this.loneBuildingsList.pos.add(StreamReadWrite.readNullablePoint(data));
      this.loneBuildingsList.names.add(StreamReadWrite.readNullableString(data));
      this.loneBuildingsList.cultures.add(StreamReadWrite.readNullableString(data));
      this.loneBuildingsList.types.add(StreamReadWrite.readNullableString(data));
    } 
  }
  
  public void registerLoneBuildingsLocation(World world, Point pos, String name, VillageType type, Culture culture, boolean newVillage, String playerName) {
    boolean found = false;
    for (Point p : this.loneBuildingsList.pos) {
      if (p.equals(pos))
        found = true; 
    } 
    if (found)
      return; 
    if (!type.generatedForPlayer)
      playerName = null; 
    this.loneBuildingsList.addVillage(pos, name, type.key, culture.key, playerName);
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(null, "Registering lone buildings: " + name + " / " + type + " / " + culture + " / " + pos); 
    for (Object o : world.playerEntities) {
      EntityPlayer player = (EntityPlayer)o;
      if (newVillage && (type.keyLonebuilding || type.keyLoneBuildingGenerateTag != null)) {
        int distance = MathHelper.floor(pos.horizontalDistanceTo((Entity)player));
        if (distance <= 2000) {
          String direction = (new Point((Entity)player)).directionTo(pos, true);
          ServerSender.sendTranslatedSentence(player, 'e', "command.newlonebuildingfound", new String[] { type.name, "" + distance, direction });
        } 
      } 
    } 
    saveLoneBuildingsList();
  }
  
  public void registerVillageLocation(World world, Point pos, String name, VillageType type, Culture culture, boolean newVillage, String playerName) {
    boolean found = false;
    if (type == null) {
      MillLog.error(null, "Attempting to register village with null type: " + pos + "/" + culture + "/" + name + "/" + newVillage);
      return;
    } 
    if (culture == null) {
      MillLog.error(null, "Attempting to register village with null culture: " + pos + "/" + type + "/" + name + "/" + newVillage);
      return;
    } 
    for (Point p : this.villagesList.pos) {
      if (p.equals(pos))
        found = true; 
    } 
    if (found)
      return; 
    if (!type.generatedForPlayer)
      playerName = null; 
    this.villagesList.addVillage(pos, name, type.key, culture.key, playerName);
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(null, "Registering village: " + name + " / " + type + " / " + culture + " / " + pos); 
    if (newVillage)
      for (Object o : world.playerEntities) {
        EntityPlayer player = (EntityPlayer)o;
        UserProfile profile = getProfile(player);
        int distance = MathHelper.floor(pos.horizontalDistanceTo((Entity)player));
        if (distance <= 2000 && !world.isRemote) {
          String direction = (new Point((Entity)player)).directionTo(pos, true);
          String villageTypeNameKey = type.getNameTranslationKey(profile);
          if (villageTypeNameKey != null) {
            ServerSender.sendTranslatedSentence(player, 'e', "command.newvillagefoundtranslated", new String[] { name, type.name, culture.getAdjectiveTranslatedKey(), "" + distance, direction, villageTypeNameKey });
            continue;
          } 
          ServerSender.sendTranslatedSentence(player, 'e', "command.newvillagefound", new String[] { name, type.name, culture.getAdjectiveTranslatedKey(), "" + distance, direction });
        } 
      }  
    saveVillageList();
  }
  
  public void registerVillager(long id, MillVillager villager) {
    if (MillConfigValues.LogVillagerSpawn >= 1)
      MillLog.major(this, "Registering villager in global list: " + villager); 
    this.villagers.put(Long.valueOf(id), villager);
    if (villager.getHouse() != null)
      villager.getHouse().rebuildVillagerList(); 
    if (villager.getTownHall() != null && villager.getTownHall() != villager.getHouse())
      villager.getTownHall().rebuildVillagerList(); 
  }
  
  public void registerVillagerRecord(VillagerRecord villagerRecord, boolean forceSave) {
    boolean registeredHouse = false;
    boolean registeredTH = false;
    this.villagerRecords.put(Long.valueOf(villagerRecord.getVillagerId()), villagerRecord);
    if (villagerRecord.getTownHall() != null) {
      villagerRecord.getTownHall().registerVillagerRecord(villagerRecord);
      registeredTH = true;
    } 
    if (villagerRecord.getHouse() != null && villagerRecord.getHouse() != villagerRecord.getTownHall()) {
      villagerRecord.getHouse().registerVillagerRecord(villagerRecord);
      registeredHouse = true;
    } 
    if (MillConfigValues.LogHybernation >= 2)
      if (this.villagerRecords.containsKey(Long.valueOf(villagerRecord.getVillagerId()))) {
        MillLog.minor(this, "Replacing villager record: " + villagerRecord + ". Registered TH: " + registeredTH + ", registeredHouse: " + registeredHouse);
      } else {
        MillLog.minor(this, "Adding villager record: " + villagerRecord + ". Registered TH: " + registeredTH + ", registeredHouse: " + registeredHouse);
      }  
    if (!this.world.isRemote && forceSave)
      saveVillagerRecords(); 
  }
  
  public void removeBuilding(Point p) {
    this.buildings.remove(p);
  }
  
  public void removeVillageOrLoneBuilding(Point p) {
    this.loneBuildingsList.removeVillage(p);
    this.villagesList.removeVillage(p);
    saveLoneBuildingsList();
    saveVillageList();
  }
  
  public void removeVillagerRecord(long villagerId) {
    VillagerRecord villagerRecord = this.villagerRecords.get(Long.valueOf(villagerId));
    if (villagerRecord != null) {
      villagerRecord.getTownHall().removeVillagerRecord(villagerId);
      villagerRecord.getHouse().removeVillagerRecord(villagerId);
    } 
    this.villagerRecords.remove(Long.valueOf(villagerId));
    saveVillagerRecords();
  }
  
  public void reportTime(Building townHall, long timeInNs, boolean villagerTime) {
    try {
      if (townHall != null && this.villagesList.rankByPos.containsKey(townHall.getPos())) {
        int villagePos = ((Integer)this.villagesList.rankByPos.get(townHall.getPos())).intValue();
        int currentSamplePos = ((List)this.villagesList.buildingsTime.get(villagePos)).size() - 1;
        if (currentSamplePos >= 0)
          if (villagerTime) {
            ((List<Long>)this.villagesList.villagersTime.get(villagePos)).set(currentSamplePos, Long.valueOf(((Long)((List<Long>)this.villagesList.villagersTime.get(villagePos)).get(currentSamplePos)).longValue() + timeInNs));
          } else {
            ((List<Long>)this.villagesList.buildingsTime.get(villagePos)).set(currentSamplePos, Long.valueOf(((Long)((List<Long>)this.villagesList.buildingsTime.get(villagePos)).get(currentSamplePos)).longValue() + timeInNs));
          }  
      } 
    } catch (Exception e) {
      MillLog.printException("Exception raised while logging Millénaire time usage:", e);
    } 
  }
  
  public void saveEverything() {
    if (this.world.isRemote)
      return; 
    saveGlobalTags();
    saveLoneBuildingsList();
    saveVillageList();
    saveWorldConfig();
    saveVillagerRecords();
    for (Building b : this.buildings.values()) {
      if (b.isTownhall && b.isActive)
        b.saveTownHall("world save"); 
    } 
  }
  
  private void saveGlobalTags() {
    if (this.world.isRemote)
      return; 
    File configFile = new File(this.millenaireDir, "tags.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(configFile);
      for (String tag : this.globalTags)
        writer.write(tag + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void saveLoneBuildingsList() {
    if (this.world.isRemote)
      return; 
    File millenaireDir = new File(this.saveDir, "millenaire");
    if (!millenaireDir.exists())
      millenaireDir.mkdir(); 
    File villageLog = new File(millenaireDir, "lonebuildings.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(villageLog);
      for (int i = 0; i < this.loneBuildingsList.pos.size(); i++) {
        Point p = this.loneBuildingsList.pos.get(i);
        String generatedFor = this.loneBuildingsList.generatedFor.get(i);
        if (generatedFor == null)
          generatedFor = ""; 
        writer.write((String)this.loneBuildingsList.names.get(i) + ";" + p.getiX() + "/" + p.getiY() + "/" + p.getiZ() + ";" + (String)this.loneBuildingsList.types.get(i) + ";" + (String)this.loneBuildingsList.cultures.get(i) + ";" + generatedFor + 
            System.getProperty("line.separator"));
      } 
      writer.flush();
      if (MillConfigValues.LogWorldGeneration >= 1)
        MillLog.major(null, "Saved " + this.loneBuildingsList.names.size() + " lone buildings.txt positions."); 
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void saveVillageList() {
    if (this.world.isRemote)
      return; 
    File millenaireDir = new File(this.saveDir, "millenaire");
    if (!millenaireDir.exists())
      millenaireDir.mkdir(); 
    File villageLog = new File(millenaireDir, "villages.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(villageLog);
      for (int i = 0; i < this.villagesList.pos.size(); i++) {
        Point p = this.villagesList.pos.get(i);
        String generatedFor = this.villagesList.generatedFor.get(i);
        if (generatedFor == null)
          generatedFor = ""; 
        writer.write((String)this.villagesList.names.get(i) + ";" + p.getiX() + "/" + p.getiY() + "/" + p.getiZ() + ";" + (String)this.villagesList.types.get(i) + ";" + (String)this.villagesList.cultures.get(i) + ";" + generatedFor + 
            System.getProperty("line.separator"));
      } 
      writer.flush();
      if (MillConfigValues.LogWorldGeneration >= 1)
        MillLog.major(null, "Saved " + this.villagesList.names.size() + " village positions."); 
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  private void saveVillagerRecords() {
    if (this.world.isRemote)
      return; 
    NBTTagCompound mainTag = new NBTTagCompound();
    NBTTagList nbttaglist = new NBTTagList();
    for (VillagerRecord vr : this.villagerRecords.values()) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      vr.write(nbttagcompound1, "vr");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
      if (MillConfigValues.LogHybernation >= 3)
        MillLog.debug(this, "Writing VR: " + vr); 
    } 
    mainTag.setTag("villagersrecords", (NBTBase)nbttaglist);
    if (!this.millenaireDir.exists())
      this.millenaireDir.mkdir(); 
    File tempFile = new File(this.millenaireDir, "villagerRecords_temp.gz");
    try {
      FileOutputStream fileoutputstream = new FileOutputStream(tempFile);
      CompressedStreamTools.writeCompressed(mainTag, fileoutputstream);
      fileoutputstream.flush();
      fileoutputstream.close();
      Path finalPath = (new File(this.millenaireDir, "villagerRecords.gz")).toPath();
      Files.move(tempFile.toPath(), finalPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void saveWorldConfig() {
    if (this.world.isRemote)
      return; 
    File configFile = new File(this.millenaireDir, "config.txt");
    try {
      BufferedWriter writer = MillCommonUtilities.getWriter(configFile);
      if (this.generateVillagesSet)
        writer.write("generate_villages=" + this.generateVillages + "\n"); 
      writer.flush();
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public void sendAllVillagerRecords(EntityPlayer player) {
    PacketBuffer data = ServerSender.getPacketBuffer();
    data.writeInt(12);
    StreamReadWrite.writeVillagerRecordMap(this.villagerRecords, data);
    ServerSender.sendPacketToPlayer(data, player);
  }
  
  @Deprecated
  public void sendVillageListPacket(EntityPlayer player) {
    PacketBuffer data = ServerSender.getPacketBuffer();
    data.writeInt(9);
    data.writeInt(this.villagesList.pos.size());
    int i;
    for (i = 0; i < this.villagesList.pos.size(); i++) {
      StreamReadWrite.writeNullablePoint(this.villagesList.pos.get(i), data);
      StreamReadWrite.writeNullableString(this.villagesList.names.get(i), data);
      StreamReadWrite.writeNullableString(this.villagesList.cultures.get(i), data);
      StreamReadWrite.writeNullableString(this.villagesList.types.get(i), data);
    } 
    data.writeInt(this.loneBuildingsList.pos.size());
    for (i = 0; i < this.loneBuildingsList.pos.size(); i++) {
      StreamReadWrite.writeNullablePoint(this.loneBuildingsList.pos.get(i), data);
      StreamReadWrite.writeNullableString(this.loneBuildingsList.names.get(i), data);
      StreamReadWrite.writeNullableString(this.loneBuildingsList.cultures.get(i), data);
      StreamReadWrite.writeNullableString(this.loneBuildingsList.types.get(i), data);
    } 
    ServerSender.sendPacketToPlayer(data, player);
  }
  
  public void setGlobalTag(String tag) {
    if (!this.globalTags.contains(tag)) {
      this.globalTags.add(tag);
      saveGlobalTags();
      if (!this.world.isRemote)
        for (UserProfile up : this.profiles.values()) {
          if (up.connected)
            up.sendProfilePacket(7); 
        }  
    } 
  }
  
  public void testLocations(String label) {
    if (!MillConfigValues.DEV)
      return; 
    for (Building b : allBuildings()) {
      try {
        if (b.location != null) {
          String tags = "";
          for (String s : b.getTags())
            tags = tags + s + ";"; 
          if (!buildingsTags.containsKey(b.getPos())) {
            if (MillConfigValues.LogTags >= 2)
              MillLog.minor(null, "Detected new building: " + b + " with tags: " + tags); 
            buildingsTags.put(b.getPos(), tags);
          } else if (!tags.equals(buildingsTags.get(b.getPos()))) {
            MillLog.warning(null, "Testing locations due to: " + label);
            MillLog.warning(null, "Tags changed for building: " + b + ". Was: " + (String)buildingsTags.get(b.getPos()) + " now: " + tags);
            buildingsTags.put(b.getPos(), tags);
          } 
          if (!buildingsVariation.containsKey(b.getPos())) {
            if (MillConfigValues.LogTags >= 2)
              MillLog.minor(null, "Detected new building: " + b + " with variation: " + b.location.getVariation()); 
            buildingsVariation.put(b.getPos(), Integer.valueOf(b.location.getVariation()));
          } else if (!((Integer)buildingsVariation.get(b.getPos())).equals(Integer.valueOf(b.location.getVariation()))) {
            MillLog.warning(null, "Testing locations due to: " + label);
            MillLog.warning(null, "Variation changed for building: " + b + ". Was: " + buildingsVariation.get(b.getPos()) + " now: " + b.location.getVariation());
            buildingsVariation.put(b.getPos(), Integer.valueOf(b.location.getVariation()));
          } 
          if (!buildingsLocation.containsKey(b.getPos())) {
            if (MillConfigValues.LogTags >= 2)
              MillLog.minor(null, "Detected new building: " + b + " with location key: " + b.location.planKey); 
            buildingsLocation.put(b.getPos(), b.location.planKey);
            continue;
          } 
          if (!b.location.planKey.equals(buildingsLocation.get(b.getPos()))) {
            MillLog.warning(null, "Testing locations due to: " + label);
            MillLog.warning(null, "Location key changed for building: " + b + ". Was: " + (String)buildingsLocation.get(b.getPos()) + " now: " + b.location.planKey);
            buildingsLocation.put(b.getPos(), b.location.planKey);
          } 
        } 
      } catch (Exception e) {
        MillLog.printException("Error in dev monitoring of a building building: ", e);
      } 
    } 
  }
  
  private void testLog() {
    if (!MillConfigValues.logPerformed)
      if (Mill.proxy.isTrueServer()) {
        MillCommonUtilities.logInstance(this.world);
      } else if (!(this.world instanceof net.minecraft.world.WorldServer)) {
        MillCommonUtilities.logInstance(this.world);
      }  
  }
  
  private void testTimeReset() {
    if (this.world.getWorldTime() < this.lastWorldTime)
      ServerSender.sendTranslatedSentenceInRange(this.world, new Point(0.0D, 0.0D, 0.0D), 2147483647, '4', "error.backwardtime", new String[] { "" + this.lastWorldTime, "" + this.world.getWorldTime() }); 
    this.lastWorldTime = this.world.getWorldTime();
  }
  
  public String toString() {
    return "World(" + this.world.getWorldInfo().getSeed() + ")";
  }
  
  public void updateWorldClient(boolean inOverworld) {
    if (!Mill.checkedMillenaireDir && (!MillCommonUtilities.getMillenaireContentDir().exists() || !(new File(MillCommonUtilities.getMillenaireContentDir(), "config.txt")).exists())) {
      Mill.proxy.sendChatAdmin("The millenaire directory could not be found. It should be inside the minecraft \"mods\" directory, alongside the jar.");
      Mill.proxy.sendChatAdmin("Le dossier millenaire est introuvable. Il devrait être dans le dossier \"mods\" de Minecraft, à côté du jar.");
    } 
    Mill.checkedMillenaireDir = true;
    rebuildVillagerList();
    if (inOverworld)
      for (Building b : allBuildings())
        b.updateBuildingClient();  
    testLog();
  }
  
  public void updateWorldServer() {
    testTimeReset();
    rebuildVillagerList();
    for (int i = 0; i < this.villagesList.pos.size(); i++) {
      ((List<Long>)this.villagesList.buildingsTime.get(i)).add(Long.valueOf(0L));
      ((List<Long>)this.villagesList.villagersTime.get(i)).add(Long.valueOf(0L));
      if (((List)this.villagesList.buildingsTime.get(i)).size() > 20) {
        ((List)this.villagesList.buildingsTime.get(i)).remove(0);
        ((List)this.villagesList.villagersTime.get(i)).remove(0);
      } 
    } 
    for (Building b : allBuildings()) {
      long startTime = System.nanoTime();
      b.updateBuildingServer();
      b.updateBackgroundVillage();
      if (b.getTownHall() != null)
        reportTime(b.getTownHall(), System.nanoTime() - startTime, false); 
    } 
    checkConnections();
    List<UserProfile> profilesCopy = new ArrayList<>(this.profiles.values());
    for (UserProfile profile : profilesCopy) {
      if (!profile.connected && profile.getPlayer() != null)
        profile.connectUser(); 
      if (profile.connected)
        profile.updateProfile(); 
    } 
    for (Object o : this.world.playerEntities) {
      EntityPlayer player = (EntityPlayer)o;
      SpecialQuestActions.onTick(this, player);
    } 
    if (MillConfigValues.DEV)
      DevModUtilities.runAutoMove(this.world); 
    forcePreload();
    testLog();
  }
}
