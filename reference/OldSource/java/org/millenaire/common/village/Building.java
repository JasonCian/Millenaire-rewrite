package org.millenaire.common.village;

import com.mojang.authlib.GameProfile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.millenaire.common.advancements.GenericAdvancement;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.BlockMillStainedGlass;
import org.millenaire.common.block.BlockPaintedBricks;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingBlock;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.BuildingChunkLoader;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemParchment;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;
import org.millenaire.common.pathing.atomicstryker.RegionMapper;
import org.millenaire.common.ui.MillMapInfo;
import org.millenaire.common.ui.PujaSacrifice;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.PathUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.buildingmanagers.MarvelManager;
import org.millenaire.common.village.buildingmanagers.PanelManager;
import org.millenaire.common.village.buildingmanagers.ResManager;
import org.millenaire.common.village.buildingmanagers.VisitorManager;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class Building {
  private class PathCreator implements IAStarPathedEntity {
    final Building.PathCreatorQueue queue;
    
    final InvItem pathConstructionGood;
    
    final int pathWidth;
    
    final Building destination;
    
    final Point startPos;
    
    final Point endPos;
    
    PathCreator(Building.PathCreatorQueue info, InvItem pathConstructionGood, int pathWidth, Building destination, Point startPos, Point endPos) {
      this.pathConstructionGood = pathConstructionGood;
      this.pathWidth = pathWidth;
      this.destination = destination;
      this.queue = info;
      this.startPos = startPos;
      this.endPos = endPos;
    }
    
    public void onFoundPath(List<AStarNode> result) {
      if (this.queue.isComplete()) {
        MillLog.error(Building.this, "onFoundPath triggered on completed info object.");
        return;
      } 
      this.queue.addReceivedPath(result);
    }
    
    public void onNoPathAvailable() {
      if (this.queue.isComplete()) {
        MillLog.error(Building.this, "onNoPathAvailable triggered on completed info object.");
        return;
      } 
      if (MillConfigValues.LogVillagePaths >= 2)
        MillLog.minor(Building.this, "Path calculation failed. Target: " + this.destination); 
      this.queue.addFailedPath();
    }
  }
  
  private class PathCreatorQueue {
    private final List<Building.PathCreator> pathCreators = new ArrayList<>();
    
    private final List<List<AStarNode>> pathsReceived = new ArrayList<>();
    
    int nbAnswers = 0;
    
    int pos = 0;
    
    public void addFailedPath() {
      this.pathsReceived.add((List<AStarNode>)null);
      this.nbAnswers++;
      if (isComplete()) {
        sendNewPathsToBuilding();
      } else {
        startNextPath();
      } 
    }
    
    public void addPathCreator(Building.PathCreator pathCreator) {
      this.pathCreators.add(pathCreator);
    }
    
    public void addReceivedPath(List<AStarNode> path) {
      this.pathsReceived.add(path);
      this.nbAnswers++;
      if (isComplete()) {
        sendNewPathsToBuilding();
      } else {
        startNextPath();
      } 
    }
    
    public boolean isComplete() {
      return (this.pathCreators.size() == this.nbAnswers);
    }
    
    private void sendNewPathsToBuilding() {
      Building.this.pathQueue = this;
    }
    
    public void startNextPath() {
      if (this.pos < this.pathCreators.size()) {
        Building.PathCreator pathCreator = this.pathCreators.get(this.pos);
        this.pos++;
        AStarPathPlannerJPS jpsPathPlanner = new AStarPathPlannerJPS(Building.this.world, pathCreator, false);
        try {
          jpsPathPlanner.getPath(pathCreator.startPos.getiX(), pathCreator.startPos.getiY(), pathCreator.startPos.getiZ(), pathCreator.endPos.getiX(), pathCreator.endPos.getiY(), pathCreator.endPos
              .getiZ(), Building.PATH_BUILDER_JPS_CONFIG);
        } catch (org.millenaire.common.utilities.ThreadSafeUtilities.ChunkAccessException e) {
          if (MillConfigValues.LogChunkLoader >= 1)
            MillLog.major(this, "Chunk access violation while calculating new path."); 
        } 
      } 
    }
  }
  
  public class RegionMapperThread extends Thread {
    VillageMapInfo winfo;
    
    public RegionMapperThread(VillageMapInfo wi) {
      this.winfo = wi;
    }
    
    public void run() {
      RegionMapper temp = new RegionMapper();
      if (MillConfigValues.LogPathing >= 1)
        MillLog.major(this, "Start"); 
      long tm = System.currentTimeMillis();
      try {
        if (temp.createConnectionsTable(this.winfo, Building.this.resManager.getSleepingPos())) {
          Building.this.regionMapper = temp;
          Building.this.lastPathingUpdate = Building.this.world.getWorldTime();
        } else {
          Building.this.lastPathingUpdate = Building.this.world.getWorldTime();
          Building.this.regionMapper = null;
        } 
      } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
        MillLog.printException((Throwable)e);
      } 
      if (MillConfigValues.LogPathing >= 1)
        MillLog.major(this, "Done: " + ((System.currentTimeMillis() - tm) * 1.0D / 1000.0D)); 
      Building.this.rebuildingRegionMapper = false;
    }
  }
  
  private class SaveWorker extends Thread {
    private final String reason;
    
    private SaveWorker(String reason) {
      this.reason = reason;
    }
    
    public void run() {
      if (!Building.this.isTownhall)
        return; 
      synchronized (Building.this) {
        long startTime = System.currentTimeMillis();
        NBTTagCompound mainTag = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < Building.this.buildings.size(); i++) {
          Point p = Building.this.buildings.get(i);
          if (p != null) {
            Building b = Building.this.mw.getBuilding(p);
            if (b != null) {
              NBTTagCompound buildingTag = new NBTTagCompound();
              b.writeToNBT(buildingTag);
              nbttaglist.appendTag((NBTBase)buildingTag);
            } 
          } 
        } 
        mainTag.setTag("buildings", (NBTBase)nbttaglist);
        File millenaireDir = Building.this.mw.millenaireDir;
        if (!millenaireDir.exists())
          millenaireDir.mkdir(); 
        File buildingsDir = new File(millenaireDir, "buildings");
        if (!buildingsDir.exists())
          buildingsDir.mkdir(); 
        File tempFile = new File(buildingsDir, Building.this.getPos().getPathString() + "_temp.gz");
        try {
          FileOutputStream fileoutputstream = new FileOutputStream(tempFile);
          CompressedStreamTools.writeCompressed(mainTag, fileoutputstream);
          fileoutputstream.flush();
          fileoutputstream.close();
          Path finalPath = (new File(buildingsDir, Building.this.getPos().getPathString() + ".gz")).toPath();
          Files.move(tempFile.toPath(), finalPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
        } catch (IOException e) {
          MillLog.printException(e);
        } 
        if (MillConfigValues.LogHybernation >= 1)
          MillLog.major(Building.this, "Saved " + Building.this.buildings.size() + " buildings in " + (System.currentTimeMillis() - startTime) + " ms due to " + this.reason + " (" + Building.this.saveReason + ")."); 
        Building.this.lastSaved = Building.this.world.getWorldTime();
        Building.this.saveNeeded = false;
        Building.this.saveReason = null;
        Building.this.saveWorker = null;
      } 
    }
  }
  
  public static final AStarConfig PATH_BUILDER_JPS_CONFIG = new AStarConfig(true, false, false, false, true);
  
  public static final int INVADER_SPAWNING_DELAY = 500;
  
  public static final int RELATION_NEUTRAL = 0;
  
  public static final int RELATION_FAIR = 10;
  
  public static final int RELATION_DECENT = 30;
  
  public static final int RELATION_GOOD = 50;
  
  public static final int RELATION_VERYGOOD = 70;
  
  public static final int RELATION_EXCELLENT = 90;
  
  public static final int RELATION_CHILLY = -10;
  
  public static final int RELATION_BAD = -30;
  
  public static final int RELATION_VERYBAD = -50;
  
  public static final int RELATION_ATROCIOUS = -70;
  
  public static final int RELATION_OPENCONFLICT = -90;
  
  public static final int RELATION_MAX = 100;
  
  public static final int RELATION_MIN = -100;
  
  public static final String blTownhall = "townHall";
  
  private static final int LOCATION_SEARCH_DELAY = 80000;
  
  public static final int MIN_REPUTATION_FOR_TRADE = -1024;
  
  public static final int MAX_REPUTATION = 32768;
  
  public static final String versionCompatibility = "1.0";
  
  public static void readBuildingPacket(MillWorldData mw, PacketBuffer ds) {
    Point pos = null;
    pos = StreamReadWrite.readNullablePoint(ds);
    Building building = mw.getBuilding(pos);
    boolean newbuilding = false;
    if (building == null) {
      building = new Building(mw);
      newbuilding = true;
    } 
    building.pos = pos;
    try {
      building.isTownhall = ds.readBoolean();
      building.chestLocked = ds.readBoolean();
      building.controlledBy = StreamReadWrite.readNullableUUID(ds);
      building.controlledByName = StreamReadWrite.readNullableString(ds);
      building.townHallPos = StreamReadWrite.readNullablePoint(ds);
      String cultureKey = StreamReadWrite.readNullableString(ds);
      building.culture = Culture.getCultureByName(cultureKey);
      if (building.culture == null)
        MillLog.error(building, "Received from the server a building of unknown culture: " + cultureKey); 
      String vtype = StreamReadWrite.readNullableString(ds);
      if (building.culture != null && building.culture.getVillageType(vtype) != null) {
        building.villageType = building.culture.getVillageType(vtype);
      } else if (building.culture != null && building.culture.getLoneBuildingType(vtype) != null) {
        building.villageType = building.culture.getLoneBuildingType(vtype);
      } 
      building.location = StreamReadWrite.readNullableBuildingLocation(ds);
      building.addTags(StreamReadWrite.readStringCollection(ds), "reading tags client-side");
      building.buildingGoal = StreamReadWrite.readNullableString(ds);
      building.buildingGoalIssue = StreamReadWrite.readNullableString(ds);
      building.buildingGoalLevel = ds.readInt();
      building.buildingGoalVariation = ds.readInt();
      building.buildingGoalLocation = StreamReadWrite.readNullableBuildingLocation(ds);
      List<Boolean> isCIPwall = StreamReadWrite.readBooleanList(ds);
      List<BuildingLocation> bls = StreamReadWrite.readBuildingLocationList(ds);
      building.getConstructionsInProgress().clear();
      int cip_id = 0;
      for (BuildingLocation bl : bls) {
        ConstructionIP cip = new ConstructionIP(building, cip_id, ((Boolean)isCIPwall.get(cip_id)).booleanValue());
        building.getConstructionsInProgress().add(cip);
        cip.setBuildingLocation(bl);
        cip_id++;
      } 
      building.buildingProjects = StreamReadWrite.readProjectListList(ds, building.culture);
      building.buildings = StreamReadWrite.readPointList(ds);
      building.buildingsBought = StreamReadWrite.readStringList(ds);
      building.relations = StreamReadWrite.readPointIntegerMap(ds);
      building.raidsPerformed = StreamReadWrite.readStringList(ds);
      building.raidsSuffered = StreamReadWrite.readStringList(ds);
      Map<Long, VillagerRecord> vrecords = StreamReadWrite.readVillagerRecordMap(mw, ds);
      for (VillagerRecord villagerRecord : vrecords.values())
        mw.registerVillagerRecord(villagerRecord, false); 
      building.pujas = StreamReadWrite.readOrUpdateNullablePuja(ds, building, building.pujas);
      building.visitorsList = StreamReadWrite.readStringList(ds);
      building.imported = StreamReadWrite.readInventory(ds);
      building.exported = StreamReadWrite.readInventory(ds);
      building.name = StreamReadWrite.readNullableString(ds);
      building.qualifier = StreamReadWrite.readNullableString(ds);
      building.raidTarget = StreamReadWrite.readNullablePoint(ds);
      building.raidPlanningStart = ds.readLong();
      building.raidStart = ds.readLong();
      building.resManager.readDataStream(ds);
      if (building.isTownhall && building.villageType.isMarvel()) {
        building.marvelManager = new MarvelManager(building);
        building.marvelManager.readDataStream(ds);
      } 
    } catch (IOException e) {
      MillLog.printException(e);
    } 
    if (newbuilding)
      mw.addBuilding(building, pos); 
  }
  
  public static void readShopPacket(MillWorldData mw, PacketBuffer ds) {
    Point pos = null;
    pos = StreamReadWrite.readNullablePoint(ds);
    Building building = mw.getBuilding(pos);
    if (building == null) {
      MillLog.error(null, "Received shop packet for null building at: " + pos);
      return;
    } 
    try {
      int nbSells = ds.readInt();
      if (nbSells > 0) {
        LinkedHashMap<TradeGood, Integer> shopSellsPlayer = new LinkedHashMap<>();
        for (int i = 0; i < nbSells; i++) {
          TradeGood g = StreamReadWrite.readNullableGoods(ds, building.culture);
          shopSellsPlayer.put(g, Integer.valueOf(ds.readInt()));
        } 
        building.shopSells.put(Mill.proxy.getSinglePlayerName(), shopSellsPlayer);
      } 
      int nbBuys = ds.readInt();
      if (nbBuys > 0) {
        LinkedHashMap<TradeGood, Integer> shopBuysPlayer = new LinkedHashMap<>();
        for (int i = 0; i < nbBuys; i++) {
          TradeGood g = StreamReadWrite.readNullableGoods(ds, building.culture);
          shopBuysPlayer.put(g, Integer.valueOf(ds.readInt()));
        } 
        building.shopBuys.put(Mill.proxy.getSinglePlayerName(), shopBuysPlayer);
      } 
    } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
      MillLog.printException((Throwable)e);
    } 
  }
  
  private boolean pathsChanged = false;
  
  private ItemStack bannerStack = ItemStack.EMPTY;
  
  public String buildingGoal;
  
  public String buildingGoalIssue;
  
  public int buildingGoalLevel = 0;
  
  public BuildingLocation buildingGoalLocation = null;
  
  public int buildingGoalVariation = 0;
  
  public ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> buildingProjects = new ConcurrentHashMap<>();
  
  public CopyOnWriteArrayList<Point> buildings = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<String> buildingsBought = new CopyOnWriteArrayList<>();
  
  public Culture culture;
  
  private boolean declaredPos = false;
  
  public HashMap<InvItem, Integer> exported = new HashMap<>();
  
  public HashMap<InvItem, Integer> imported = new HashMap<>();
  
  public boolean isActive = false, isAreaLoaded = false;
  
  public boolean chestLocked;
  
  public boolean isTownhall = false;
  
  public boolean isInn = false;
  
  public boolean isMarket = false;
  
  public boolean hasVisitors = false;
  
  public boolean hasAutoSpawn = false;
  
  private long lastFailedOtherLocationSearch = 0L;
  
  private long lastFailedProjectLocationSearch = 0L;
  
  public long lastPathingUpdate;
  
  private long lastSaved = 0L;
  
  public long lastVillagerRecordsRepair = 0L;
  
  public BuildingLocation location;
  
  public VillagerRecord merchantRecord = null;
  
  private String name = null;
  
  private String qualifier = "";
  
  public int nbNightsMerchant = 0;
  
  private HashMap<TradeGood, Integer> neededGoodsCached = new HashMap<>();
  
  private long neededGoodsLastGenerated = 0L;
  
  public boolean nightActionPerformed = false;
  
  public boolean noProjectsLeft = false;
  
  public RegionMapper regionMapper = null;
  
  public EntityPlayer closestPlayer = null;
  
  private Point pos = null;
  
  private boolean rebuildingRegionMapper = false;
  
  private boolean saveNeeded = false;
  
  private String saveReason = null;
  
  public MillVillager seller = null;
  
  public Point sellingPlace = null;
  
  private Point townHallPos = null;
  
  private Set<MillVillager> villagers = new LinkedHashSet<>();
  
  public CopyOnWriteArrayList<String> visitorsList = new CopyOnWriteArrayList<>();
  
  private final Map<Long, VillagerRecord> vrecords = new HashMap<>();
  
  public VillageType villageType = null;
  
  private ConcurrentHashMap<Point, Integer> relations = new ConcurrentHashMap<>();
  
  public Point parentVillage = null;
  
  public VillageMapInfo winfo = new VillageMapInfo();
  
  public MillMapInfo mapInfo = null;
  
  public MillWorldData mw;
  
  public World world;
  
  private boolean nightBackgroundActionPerformed;
  
  private boolean updateRaidPerformed;
  
  public CopyOnWriteArrayList<String> raidsPerformed = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<String> raidsSuffered = new CopyOnWriteArrayList<>();
  
  public Point raidTarget;
  
  public long raidStart = 0L;
  
  public long raidPlanningStart;
  
  public boolean underAttack = false;
  
  private int nbAnimalsRespawned;
  
  public PujaSacrifice pujas = null;
  
  public UUID controlledBy = null;
  
  public String controlledByName = null;
  
  private SaveWorker saveWorker = null;
  
  private long lastGoodsRefresh = 0L;
  
  private boolean refreshGoodsNightActionPerformed;
  
  private BuildingChunkLoader chunkLoader = null;
  
  private final CopyOnWriteArrayList<ConstructionIP> constructionsIP = new CopyOnWriteArrayList<>();
  
  public List<List<BuildingBlock>> pathsToBuild = null;
  
  private PathCreatorQueue pathQueue = null;
  
  public int pathsToBuildIndex = 0;
  
  public int pathsToBuildPathIndex = 0;
  
  public List<Point> oldPathPointsToClear = null;
  
  public int oldPathPointsToClearIndex = 0;
  
  private boolean autobuildPaths = false;
  
  private final HashMap<String, LinkedHashMap<TradeGood, Integer>> shopBuys = new HashMap<>();
  
  private final HashMap<String, LinkedHashMap<TradeGood, Integer>> shopSells = new HashMap<>();
  
  private final ResManager resManager = new ResManager(this);
  
  public CopyOnWriteArrayList<Point> subBuildings = new CopyOnWriteArrayList<>();
  
  private Map<InvItem, Integer> inventoryCache = null;
  
  private MarvelManager marvelManager;
  
  private VisitorManager visitorManager = null;
  
  private PanelManager panelManager = null;
  
  private final CopyOnWriteArraySet<String> tags = new CopyOnWriteArraySet<>();
  
  public VillageType.BrickColourTheme brickColourTheme = null;
  
  private Building(MillWorldData mw) {
    this.mw = mw;
    this.world = mw.world;
  }
  
  public Building(MillWorldData mw, Culture c, VillageType villageType, BuildingLocation location, boolean townHall, boolean villageCreation, Point townHallPos) {
    this.pos = location.chestPos;
    this.mw = mw;
    this.world = mw.world;
    this.location = location.clone();
    if (location.getPlan() != null)
      addTags((location.getPlan()).tags, "Adding plan tags"); 
    this.culture = c;
    this.villageType = villageType;
    if (this.world == null) {
      MillLog.MillenaireException e = new MillLog.MillenaireException("Null world!");
      MillLog.printException((Throwable)e);
    } 
    if (this.pos == null) {
      MillLog.MillenaireException e = new MillLog.MillenaireException("Null pos!");
      MillLog.printException((Throwable)e);
    } 
    if (this.location == null) {
      MillLog.MillenaireException e = new MillLog.MillenaireException("Null location!");
      MillLog.printException((Throwable)e);
    } 
    if (this.culture == null) {
      MillLog.MillenaireException e = new MillLog.MillenaireException("Null culture!");
      MillLog.printException((Throwable)e);
    } 
    mw.addBuilding(this, location.chestPos);
    this.isTownhall = townHall;
    this.regionMapper = null;
    if (this.isTownhall) {
      this.townHallPos = getPos();
    } else {
      this.townHallPos = townHallPos;
    } 
    this.isTownhall = townHall;
    if (containsTags("inn") && !this.isTownhall)
      this.isInn = true; 
    if (containsTags("market") && !this.isTownhall) {
      this.isMarket = true;
      this.hasVisitors = true;
    } 
    if (containsTags("autospawnvillagers"))
      this.hasAutoSpawn = true; 
    if (!location.getVisitors().isEmpty())
      this.hasVisitors = true; 
    if (containsTags("pujas"))
      this.pujas = new PujaSacrifice(this, (short)0); 
    if (containsTags("sacrifices"))
      this.pujas = new PujaSacrifice(this, (short)1); 
    if (this.isTownhall && villageType.isMarvel())
      this.marvelManager = new MarvelManager(this); 
  }
  
  public Building(MillWorldData mw, NBTTagCompound nbttagcompound) {
    this.mw = mw;
    this.world = mw.world;
    readFromNBT(nbttagcompound);
    if (this.pos == null) {
      MillLog.MillenaireException e = new MillLog.MillenaireException("Null pos!");
      MillLog.printException((Throwable)e);
    } 
    mw.addBuilding(this, this.pos);
  }
  
  public void addAdult(MillVillager child) throws MillLog.MillenaireException {
    List<String> residents;
    String type = null;
    HashMap<String, Integer> villagerCount = new HashMap<>();
    HashMap<String, Integer> residentCount = new HashMap<>();
    if (child.gender == 1) {
      residents = this.location.getMaleResidents();
    } else {
      residents = this.location.getFemaleResidents();
    } 
    for (String s : residents) {
      if (residentCount.containsKey(s)) {
        residentCount.put(s, Integer.valueOf(((Integer)residentCount.get(s)).intValue() + 1));
        continue;
      } 
      residentCount.put(s, Integer.valueOf(1));
    } 
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (villagerCount.containsKey(vr.type)) {
        villagerCount.put(vr.type, Integer.valueOf(((Integer)villagerCount.get(vr.type)).intValue() + 1));
        continue;
      } 
      villagerCount.put(vr.type, Integer.valueOf(1));
    } 
    for (String s : residentCount.keySet()) {
      if (!villagerCount.containsKey(s)) {
        type = s;
        continue;
      } 
      if (((Integer)villagerCount.get(s)).intValue() < ((Integer)residentCount.get(s)).intValue())
        type = s; 
    } 
    if (type == null) {
      MillLog.error(this, "Could not find a villager type to create. Gender: " + child.gender);
      MillLog.error(this, "Villager types: " + ((child.gender == 1) ? 
          MillCommonUtilities.flattenStrings(this.location.getMaleResidents()) : MillCommonUtilities.flattenStrings(this.location.getFemaleResidents())));
      String s = "";
      for (VillagerRecord vr : getVillagerRecords().values())
        s = s + vr.type + " "; 
      MillLog.error(this, "Current residents: " + s);
      return;
    } 
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(this, "Creating " + type + " with child " + child.getName() + "/" + child.getVillagerId()); 
    this.mw.removeVillagerRecord(child.getVillagerId());
    VillagerRecord adultRecord = VillagerRecord.createVillagerRecord(this.culture, type, this.mw, getPos(), getTownHallPos(), child.firstName, child.familyName, child.getVillagerId(), false);
    VillagerRecord childRecord = child.getRecord();
    if (childRecord != null)
      adultRecord.rightHanded = childRecord.rightHanded; 
    MillVillager adult = MillVillager.createVillager(adultRecord, this.world, child.getPos(), false);
    if (adult == null) {
      MillLog.error(this, "Couldn't create adult of type " + type + " from child " + child);
      return;
    } 
    adultRecord.updateRecord(adult);
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.gender != adult.gender) {
        if (adult.gender == 2) {
          adultRecord.maidenName = adultRecord.familyName;
          adultRecord.familyName = vr.familyName;
          adult.familyName = vr.familyName;
        } 
        if (vr.gender == 2) {
          vr.maidenName = vr.familyName;
          vr.familyName = adult.familyName;
          MillVillager spouse = this.mw.getVillagerById(vr.getVillagerId());
          if (spouse != null)
            spouse.familyName = vr.familyName; 
        } 
        adultRecord.spousesName = vr.getName();
        vr.spousesName = adult.getName();
      } 
    } 
    child.despawnVillager();
    this.world.spawnEntity((Entity)adult);
    if (this.isInn) {
      merchantCreated(adultRecord);
    } else {
      getPanelManager().updateSigns();
    } 
  }
  
  public void addCustomBuilding(BuildingCustomPlan customBuilding, Point pos) throws MillLog.MillenaireException {
    BuildingLocation location = new BuildingLocation(customBuilding, pos, false);
    Building building = new Building(this.mw, this.culture, this.villageType, location, false, false, getPos());
    customBuilding.registerResources(building, location);
    building.initialise(null, false);
    registerBuildingEntity(building);
    BuildingProject project = new BuildingProject(customBuilding, location);
    if (!this.buildingProjects.containsKey(BuildingProject.EnumProjects.CUSTOMBUILDINGS))
      this.buildingProjects.put(BuildingProject.EnumProjects.CUSTOMBUILDINGS, new CopyOnWriteArrayList<>()); 
    ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.CUSTOMBUILDINGS)).add(project);
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(this, "Created new Custom Building Entity: " + customBuilding.buildingKey + " at " + pos); 
  }
  
  public void addTags(Collection<String> newTags, String reason) {
    int nbTags = this.tags.size();
    List<String> addedTags = new ArrayList<>();
    for (String tag : newTags) {
      if (!this.tags.contains(tag)) {
        addedTags.add(tag);
        this.tags.add(tag);
      } 
    } 
    if (MillConfigValues.LogTags >= 1 && addedTags.size() > 0 && !reason.contains("client-side"))
      MillLog.major(this, "Added tags due to '" + reason + "': " + MillCommonUtilities.flattenStrings(addedTags) + ", went from " + nbTags + " to " + this.tags.size() + ". Current tags: " + 
          MillCommonUtilities.flattenStrings(this.tags)); 
  }
  
  public void addToExports(InvItem good, int quantity) {
    if (this.exported.containsKey(good)) {
      this.exported.put(good, Integer.valueOf(((Integer)this.exported.get(good)).intValue() + quantity));
    } else {
      this.exported.put(good, Integer.valueOf(quantity));
    } 
  }
  
  public void addToImports(InvItem good, int quantity) {
    if (this.imported.containsKey(good)) {
      this.imported.put(good, Integer.valueOf(((Integer)this.imported.get(good)).intValue() + quantity));
    } else {
      this.imported.put(good, Integer.valueOf(quantity));
    } 
  }
  
  public void adjustLanguage(EntityPlayer player, int l) {
    this.mw.getProfile(player).adjustLanguage((getTownHall()).culture.key, l);
  }
  
  public void adjustRelation(Point villagePos, int change, boolean reset) {
    int relation = change;
    if (this.relations.containsKey(villagePos) && !reset)
      relation += ((Integer)this.relations.get(villagePos)).intValue(); 
    if (relation > 100) {
      relation = 100;
    } else if (relation < -100) {
      relation = -100;
    } 
    this.relations.put(villagePos, Integer.valueOf(relation));
    this.saveNeeded = true;
    if (!this.isActive)
      saveTownHall("distance relation change"); 
    Building village = this.mw.getBuilding(villagePos);
    if (village == null) {
      MillLog.error(this, "Could not find village at " + villagePos + " in order to adjust relation.");
    } else {
      village.relations.put(getPos(), Integer.valueOf(relation));
      village.saveTownHall("distance relation change");
    } 
  }
  
  public void adjustReputation(EntityPlayer player, int l) {
    this.mw.getProfile(player).adjustReputation(getTownHall(), l);
  }
  
  public void attemptMerchantMove(boolean forced) {
    Building inn;
    List<Building> targets = new ArrayList<>();
    List<Building> backupTargets = new ArrayList<>();
    for (Point vp : (getTownHall()).relations.keySet()) {
      Building townHall = this.mw.getBuilding(vp);
      if (townHall != null && getTownHall() != null && townHall.villageType != (getTownHall()).villageType && ((
        (Integer)(getTownHall()).relations.get(vp)).intValue() >= 0 || (((Integer)(getTownHall()).relations.get(vp)).intValue() >= 0 && townHall.culture == this.culture)) && 
        getPos().distanceTo(townHall.getPos()) < 2000.0D) {
        if (MillConfigValues.LogMerchant >= 2)
          MillLog.debug(this, "Considering village " + townHall.getVillageQualifiedName() + " for merchant : " + this.merchantRecord); 
        for (Building building : townHall.getBuildingsWithTag("inn")) {
          boolean moveNeeded = false;
          HashMap<InvItem, Integer> content = this.resManager.getChestsContent();
          for (InvItem good : content.keySet()) {
            if (((Integer)content.get(good)).intValue() > 0 && building.getTownHall().nbGoodNeeded(good.getItem(), good.meta) > 0) {
              moveNeeded = true;
              break;
            } 
          } 
          if (moveNeeded) {
            if (building.merchantRecord == null) {
              targets.add(building);
              targets.add(building);
              targets.add(building);
            } else if (building.nbNightsMerchant > 1 || forced) {
              targets.add(building);
            } 
            if (MillConfigValues.LogMerchant >= 2)
              MillLog.debug(this, "Found good move in " + townHall.getVillageQualifiedName() + " for merchant : " + this.merchantRecord); 
            continue;
          } 
          if (this.nbNightsMerchant > 3) {
            backupTargets.add(building);
            if (MillConfigValues.LogMerchant >= 2)
              MillLog.debug(this, "Found backup move in " + townHall.getVillageQualifiedName() + " for merchant : " + this.merchantRecord); 
          } 
        } 
      } 
    } 
    if (targets.size() == 0 && backupTargets.size() == 0) {
      if (MillConfigValues.LogMerchant >= 2)
        MillLog.minor(this, "Failed to find a destination for merchant: " + this.merchantRecord); 
      return;
    } 
    if (targets.size() > 0) {
      inn = targets.get(MillCommonUtilities.randomInt(targets.size()));
    } else {
      inn = backupTargets.get(MillCommonUtilities.randomInt(backupTargets.size()));
    } 
    if (inn.merchantRecord == null) {
      moveMerchant(inn);
    } else if (inn.nbNightsMerchant > 1 || forced) {
      swapMerchants(inn);
    } 
  }
  
  private void attemptPlanNewRaid() {
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.raidingVillage)
        return; 
    } 
    int raidingStrength = (int)(getVillageRaidingStrength() * 2.0F);
    if (MillConfigValues.LogDiplomacy >= 3)
      MillLog.debug(this, "Checking out for new raid, strength: " + raidingStrength); 
    if (raidingStrength > 0) {
      List<Building> targets = new ArrayList<>();
      if (this.villageType.lonebuilding) {
        for (Building distantVillage : this.mw.allBuildings()) {
          if (distantVillage != null && distantVillage.isTownhall && distantVillage.villageType != null && !distantVillage.villageType.lonebuilding && 
            getPos().distanceTo(distantVillage.getPos()) < MillConfigValues.BanditRaidRadius && distantVillage.getVillageDefendingStrength() < raidingStrength) {
            if (MillConfigValues.LogDiplomacy >= 3)
              MillLog.debug(this, "Lone building valid target: " + distantVillage); 
            targets.add(distantVillage);
          } 
        } 
      } else {
        for (Point p : this.relations.keySet()) {
          if (((Integer)this.relations.get(p)).intValue() < -90) {
            Building distantVillage = this.mw.getBuilding(p);
            if (distantVillage != null) {
              if (MillConfigValues.LogDiplomacy >= 3)
                MillLog.debug(this, "Testing village valid target: " + distantVillage + "/" + distantVillage.getVillageDefendingStrength()); 
              if (distantVillage.getVillageDefendingStrength() < raidingStrength) {
                if (MillConfigValues.LogDiplomacy >= 3)
                  MillLog.debug(this, "Village valid target: " + distantVillage); 
                targets.add(distantVillage);
              } 
            } 
          } 
        } 
      } 
      if (!targets.isEmpty()) {
        Building target = targets.get(MillCommonUtilities.randomInt(targets.size()));
        if (this.isActive || target.isActive)
          planRaid(target); 
      } 
    } 
  }
  
  public List<TradeGood> calculateBuyingGoods(IInventory playerInventory) {
    if (!this.culture.shopBuys.containsKey(this.location.shop) && !this.culture.shopBuysOptional.containsKey(this.location.shop))
      return null; 
    List<TradeGood> baseGoods = (List<TradeGood>)this.culture.shopBuys.get(this.location.shop);
    List<TradeGood> extraGoods = new ArrayList<>();
    if (this.culture.shopBuysOptional.containsKey(this.location.shop))
      for (TradeGood g : this.culture.shopBuysOptional.get(this.location.shop)) {
        if (playerInventory == null || MillCommonUtilities.countChestItems(playerInventory, g.item.getItem(), g.item.meta) > 0)
          extraGoods.add(g); 
      }  
    if (this.isTownhall) {
      BuildingPlan goalPlan = getCurrentGoalBuildingPlan();
      if (goalPlan != null)
        for (InvItem key : goalPlan.resCost.keySet()) {
          if (key.meta != -1) {
            boolean found = false;
            for (TradeGood tg : baseGoods) {
              if (tg.item.getItem() == key.getItem() && tg.item.meta == key.meta)
                found = true; 
            } 
            if (!found) {
              if (this.culture.getTradeGood(key) != null) {
                extraGoods.add(this.culture.getTradeGood(key));
                continue;
              } 
              extraGoods.add(new TradeGood("generated", this.culture, key));
            } 
          } 
        }  
    } 
    if (extraGoods.size() == 0)
      return baseGoods; 
    List<TradeGood> finalGoods = new ArrayList<>();
    for (TradeGood good : baseGoods)
      finalGoods.add(good); 
    for (TradeGood good : extraGoods)
      finalGoods.add(good); 
    return finalGoods;
  }
  
  private void calculateInventoryCache() {
    this.inventoryCache = new HashMap<>();
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      if (chest != null)
        for (int i = 0; i < chest.getSizeInventory(); i++) {
          ItemStack stack = chest.getStackInSlot(i);
          if (!stack.isEmpty()) {
            InvItem invItem = InvItem.createInvItem(stack);
            if (this.inventoryCache.containsKey(invItem)) {
              this.inventoryCache.put(invItem, Integer.valueOf(((Integer)this.inventoryCache.get(invItem)).intValue() + stack.getCount()));
            } else {
              this.inventoryCache.put(invItem, Integer.valueOf(stack.getCount()));
            } 
          } 
        }  
    } 
    for (Point p : this.resManager.furnaces) {
      TileEntityFurnace furnace = p.getFurnace(this.world);
      if (furnace != null) {
        ItemStack stack = furnace.getStackInSlot(2);
        if (stack != null && !stack.isEmpty()) {
          InvItem invItem = InvItem.createInvItem(stack);
          if (this.inventoryCache.containsKey(invItem)) {
            this.inventoryCache.put(invItem, Integer.valueOf(((Integer)this.inventoryCache.get(invItem)).intValue() + stack.getCount()));
            continue;
          } 
          this.inventoryCache.put(invItem, Integer.valueOf(stack.getCount()));
        } 
      } 
    } 
    for (Point p : this.resManager.firepits) {
      TileEntityFirePit firepit = p.getFirePit(this.world);
      if (firepit != null)
        for (int slotPos = 0; slotPos < 3; slotPos++) {
          ItemStack stack = firepit.outputs.getStackInSlot(slotPos);
          if (stack != null && !stack.isEmpty()) {
            InvItem invItem = InvItem.createInvItem(stack);
            if (this.inventoryCache.containsKey(invItem)) {
              this.inventoryCache.put(invItem, Integer.valueOf(((Integer)this.inventoryCache.get(invItem)).intValue() + stack.getCount()));
            } else {
              this.inventoryCache.put(invItem, Integer.valueOf(stack.getCount()));
            } 
          } 
        }  
    } 
  }
  
  public void calculatePathsToClear() {
    if (this.pathsToBuild != null) {
      List<List<BuildingBlock>> pathsToBuildLocal = this.pathsToBuild;
      long startTime = System.currentTimeMillis();
      List<Point> oldPathPointsToClearNew = new ArrayList<>();
      HashSet<Point> newPathPoints = new HashSet<>();
      for (List<BuildingBlock> path : pathsToBuildLocal) {
        for (BuildingBlock bp : path)
          newPathPoints.add(bp.p); 
      } 
      int minX = Math.max(this.winfo.mapStartX, getPos().getiX() - this.villageType.radius);
      int maxX = Math.min(this.winfo.mapStartX + this.winfo.length - 1, getPos().getiX() + this.villageType.radius);
      int minZ = Math.max(this.winfo.mapStartZ, getPos().getiZ() - this.villageType.radius);
      int maxZ = Math.min(this.winfo.mapStartZ + this.winfo.width - 1, getPos().getiZ() + this.villageType.radius);
      for (int x = minX; x < maxX; x++) {
        for (int z = minZ; z < maxZ; z++) {
          int basey = this.winfo.topGround[x - this.winfo.mapStartX][z - this.winfo.mapStartZ];
          for (int dy = -2; dy < 4; dy++) {
            int y = dy + basey;
            IBlockState blockState = WorldUtilities.getBlockState(this.world, x, y, z);
            if (BlockItemUtilities.isPath(blockState.getBlock()))
              if (!((Boolean)blockState.getValue((IProperty)IBlockPath.STABLE)).booleanValue()) {
                Point p = new Point(x, y, z);
                if (!newPathPoints.contains(p))
                  oldPathPointsToClearNew.add(p); 
              }  
          } 
        } 
      } 
      this.oldPathPointsToClearIndex = 0;
      this.oldPathPointsToClear = oldPathPointsToClearNew;
      if (MillConfigValues.LogVillagePaths >= 2)
        MillLog.minor(this, "Finished looking for paths to clear. Found: " + this.oldPathPointsToClear.size() + ". Duration: " + (System.currentTimeMillis() - startTime) + " ms."); 
    } 
  }
  
  public List<TradeGood> calculateSellingGoods(IInventory playerInventory) {
    if (!this.culture.shopSells.containsKey(this.location.shop))
      return null; 
    return (List<TradeGood>)this.culture.shopSells.get(this.location.shop);
  }
  
  public void callForHelp(EntityLivingBase attacker) {
    if (MillConfigValues.LogGeneralAI >= 3)
      MillLog.debug(this, "Calling for help among: " + getKnownVillagers().size() + " villagers."); 
    for (MillVillager villager : getKnownVillagers()) {
      if (MillConfigValues.LogGeneralAI >= 3)
        MillLog.debug(villager, "Testing villager. Will fight? " + villager.helpsInAttacks() + ". Current target? " + villager.getAttackTarget() + ". Distance to threat: " + villager
            .getPos().distanceTo((Entity)attacker)); 
      if (villager.getAttackTarget() == null && villager.helpsInAttacks() && !villager.isRaider)
        if (villager.getPos().distanceTo((Entity)attacker) < 80.0D) {
          if (MillConfigValues.LogGeneralAI >= 1)
            MillLog.major(villager, "Off to help a friend attacked by attacking: " + attacker); 
          villager.setAttackTarget(attacker);
          villager.clearGoal();
          villager.speakSentence("calltoarms", 0, 50, 1);
        }  
    } 
  }
  
  private boolean canAffordBuild(BuildingPlan plan) {
    if (plan == null) {
      MillLog.error(this, "Needed to compute plan cost but the plan is null.");
      return false;
    } 
    if (plan.resCost == null) {
      MillLog.error(this, "Needed to compute plan cost but the plan cost map is null.");
      return false;
    } 
    for (InvItem key : plan.resCost.keySet()) {
      if (((Integer)plan.resCost.get(key)).intValue() > nbGoodAvailable(key, true, false, false))
        return false; 
    } 
    return true;
  }
  
  private boolean canAffordProject(BuildingPlan plan) {
    if (plan == null) {
      MillLog.error(this, "Needed to compute plan cost but the plan is null.");
      return false;
    } 
    if (plan.resCost == null) {
      MillLog.error(this, "Needed to compute plan cost but the plan cost map is null.");
      return false;
    } 
    for (InvItem key : plan.resCost.keySet()) {
      if (((Integer)plan.resCost.get(key)).intValue() > countGoods(key))
        return false; 
    } 
    return true;
  }
  
  public void cancelBuilding(BuildingLocation location) {
    ConstructionIP cip = findConstructionIPforLocation(location);
    if (cip != null && 
      location.isLocationSamePlace(cip.getBuildingLocation()))
      cip.setBuildingLocation(null); 
    if (location.isLocationSamePlace(this.buildingGoalLocation)) {
      this.buildingGoalLocation = null;
      this.buildingGoal = null;
    } 
    for (List<BuildingProject> projects : this.buildingProjects.values()) {
      for (BuildingProject project : projects) {
        if (project.location == location)
          projects.remove(project); 
      } 
    } 
    Building building = location.getBuilding(this.world);
    if (building != null) {
      ArrayList<MillVillager> villagersCopy = new ArrayList<>(building.villagers);
      for (MillVillager villager : villagersCopy)
        villager.despawnVillagerSilent(); 
      Collection<VillagerRecord> vrecordsCopy = new ArrayList<>(building.getAllVillagerRecords());
      for (VillagerRecord vr : vrecordsCopy)
        this.mw.removeVillagerRecord(vr.getVillagerId()); 
    } 
    this.buildings.remove(location.pos);
    this.winfo.removeBuildingLocation(location);
    this.mw.removeBuilding(location.pos);
  }
  
  public void cancelRaid() {
    if (MillConfigValues.LogDiplomacy >= 1)
      MillLog.major(this, "Cancelling raid"); 
    this.raidPlanningStart = 0L;
    this.raidStart = 0L;
    this.raidTarget = null;
  }
  
  public boolean canChildMoveIn(int pGender, String familyName) {
    if (pGender == 2 && this.location.getFemaleResidents().size() == 0)
      return false; 
    if (pGender == 1 && this.location.getMaleResidents().size() == 0)
      return false; 
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.gender != pGender && !(vr.getType()).isChild && vr.familyName.equals(familyName) && equals(vr.getHouse()))
        return false; 
    } 
    int nbAdultSameSex = 0;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.gender == pGender && vr.getType() != null && !(vr.getType()).isChild && equals(vr.getHouse()))
        nbAdultSameSex++; 
    } 
    if (pGender == 1 && nbAdultSameSex >= this.location.getMaleResidents().size())
      return false; 
    if (pGender == 2 && nbAdultSameSex >= this.location.getFemaleResidents().size())
      return false; 
    return true;
  }
  
  public void changeVillageName(String s) {
    this.name = s;
    int i;
    for (i = 0; i < this.mw.villagesList.pos.size(); i++) {
      if (((Point)this.mw.villagesList.pos.get(i)).equals(getPos()))
        this.mw.villagesList.names.set(i, getVillageQualifiedName()); 
    } 
    for (i = 0; i < this.mw.loneBuildingsList.pos.size(); i++) {
      if (((Point)this.mw.loneBuildingsList.pos.get(i)).equals(getPos()))
        this.mw.loneBuildingsList.names.set(i, getVillageQualifiedName()); 
    } 
  }
  
  public void changeVillageQualifier(String s) {
    this.qualifier = s;
    int i;
    for (i = 0; i < this.mw.villagesList.pos.size(); i++) {
      if (((Point)this.mw.villagesList.pos.get(i)).equals(getPos()))
        this.mw.villagesList.names.set(i, getVillageQualifiedName()); 
    } 
    for (i = 0; i < this.mw.loneBuildingsList.pos.size(); i++) {
      if (((Point)this.mw.loneBuildingsList.pos.get(i)).equals(getPos()))
        this.mw.loneBuildingsList.names.set(i, getVillageQualifiedName()); 
    } 
  }
  
  public void checkBattleStatus() {
    int nbAttackers = 0;
    int nbLiveAttackers = 0;
    int nbLiveDefenders = 0;
    Point attackingVillagePos = null;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.raidingVillage) {
        nbAttackers++;
        if (!vr.killed)
          nbLiveAttackers++; 
        attackingVillagePos = vr.originalVillagePos;
        continue;
      } 
      if ((vr.getType()).helpInAttacks && !vr.killed && !vr.awayraiding && !vr.awayhired)
        nbLiveDefenders++; 
    } 
    if (this.isTownhall)
      if (this.chestLocked && nbLiveDefenders == 0) {
        unlockAllChests();
        ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.BackgroundRadius, '4', "ui.allchestsunlocked", new String[] { getVillageQualifiedName() });
      } else if (!this.chestLocked && nbLiveDefenders > 0) {
        lockAllBuildingsChests();
      }  
    if (nbAttackers > 0) {
      this.underAttack = true;
      if (nbLiveAttackers == 0 || nbLiveDefenders == 0) {
        boolean finish = false;
        if (nbLiveAttackers > 0) {
          for (MillVillager v : getKnownVillagers()) {
            if (!finish && v.isRaider && this.resManager.getDefendingPos().distanceToSquared((Entity)v) < 25.0D)
              finish = true; 
          } 
        } else {
          finish = true;
        } 
        if (finish)
          if (attackingVillagePos == null) {
            MillLog.error(this, "Wanted to end raid but can't find originating village's position.");
            clearAllAttackers();
          } else {
            Building attackingVillage = this.mw.getBuilding(attackingVillagePos);
            if (attackingVillage == null) {
              clearAllAttackers();
            } else {
              boolean endedProperly = attackingVillage.endRaid();
              if (!endedProperly)
                clearAllAttackers(); 
            } 
          }  
      } 
    } else {
      this.underAttack = false;
    } 
  }
  
  private void checkExploreTag(EntityPlayer player) {
    if (player != null && this.location.getPlan() != null && !this.mw.getProfile(player).isTagSet((this.location.getPlan()).exploreTag))
      if (this.resManager.getSleepingPos().distanceToSquared((Entity)player) < 16.0D) {
        boolean valid = true;
        int x = this.resManager.getSleepingPos().getiX();
        int z = this.resManager.getSleepingPos().getiZ();
        while (valid && (x != (int)player.posX || z != (int)player.posZ)) {
          Block block = WorldUtilities.getBlock(this.world, x, this.resManager.getSleepingPos().getiY() + 1, z);
          if (block != Blocks.AIR && block.getDefaultState().getMaterial().blocksMovement()) {
            valid = false;
            continue;
          } 
          if (x > (int)player.posX) {
            x--;
            continue;
          } 
          if (x < (int)player.posX) {
            x++;
            continue;
          } 
          if (z > (int)player.posZ) {
            z--;
            continue;
          } 
          if (z < (int)player.posZ)
            z++; 
        } 
        if (valid) {
          this.mw.getProfile(player).setTag((this.location.getPlan()).exploreTag);
          ServerSender.sendTranslatedSentence(player, '2', "other.exploredbuilding", new String[] { (this.location.getPlan()).nativeName });
        } 
      }  
  }
  
  private boolean checkProjectValidity(BuildingProject project, BuildingPlan plan) {
    if (plan.requiredGlobalTag != null && !this.mw.isGlobalTagSet(plan.requiredGlobalTag))
      return false; 
    if (!plan.requiredTags.isEmpty()) {
      if (project.location == null) {
        MillLog.error(this, "Plan " + plan + " has required tags but no location.");
        return false;
      } 
      Building building = getBuildingFromLocation(project.location);
      if (building == null) {
        MillLog.error(this, "Plan " + plan + " has required tags but building can't be found.");
        return false;
      } 
      for (String tag : plan.requiredTags) {
        if (!building.containsTags(tag)) {
          if (MillConfigValues.LogTags >= 2)
            MillLog.minor(this, "Can't build plan " + plan + " as building is missing tag:" + tag); 
          return false;
        } 
      } 
    } 
    if (!plan.forbiddenTagsInVillage.isEmpty())
      for (String forbiddenTag : plan.forbiddenTagsInVillage) {
        Building matchingBuilding = getFirstBuildingWithTag(forbiddenTag);
        if (matchingBuilding != null) {
          if (MillConfigValues.LogTags >= 2)
            MillLog.minor(this, "Can't build plan " + plan + " as building " + matchingBuilding + " has tag " + forbiddenTag); 
          return false;
        } 
      }  
    if (plan.level > 0 && plan.containsTags("no_upgrade_till_wall_initialized"))
      for (BuildingProject existingProject : getFlatProjectList()) {
        BuildingPlan existingProjectPlan = existingProject.getNextBuildingPlan(false);
        if (existingProjectPlan != null && existingProjectPlan.isWallSegment && existingProjectPlan.level == 0) {
          if (MillConfigValues.LogTags >= 2)
            MillLog.minor(this, "Can't build plan " + plan + " as it requires all wall segments to be initialized."); 
          return false;
        } 
      }  
    for (String tag : plan.requiredVillageTags) {
      if (!containsTags(tag)) {
        if (MillConfigValues.LogTags >= 2)
          MillLog.minor(this, "Can't build plan " + plan + " as village is missing tag:" + tag); 
        return false;
      } 
    } 
    if (project.location != null && !plan.requiredParentTags.isEmpty()) {
      Building parent = null;
      for (BuildingLocation alocation : getLocations()) {
        if (!alocation.isSubBuildingLocation && alocation.pos.equals(project.location.pos))
          parent = alocation.getBuilding(this.world); 
      } 
      if (parent == null) {
        MillLog.error(this, "Building plan " + plan + " has required parent tags but the parent for location " + project.location + " cannot be found.");
        return false;
      } 
      for (String tag : plan.requiredParentTags) {
        if (!parent.containsTags(tag)) {
          if (MillConfigValues.LogTags >= 2)
            MillLog.temp(this, "Can't build plan " + plan + " as parent is missing tag: " + tag); 
          return false;
        } 
      } 
    } 
    return true;
  }
  
  public void checkSeller() {
    if (!this.world.isDaytime() || this.underAttack) {
      if (MillConfigValues.LogSelling >= 2)
        MillLog.major(this, "Not sending seller because either: !world.isDaytime(): " + (!this.world.isDaytime() ? 1 : 0) + " or underAttack: " + this.underAttack); 
      return;
    } 
    if (this.closestPlayer == null || controlledBy(this.closestPlayer)) {
      if (this.closestPlayer == null && MillConfigValues.LogSelling >= 3) {
        MillLog.debug(this, "Not sending seller because there are no nearby player.");
      } else if (this.closestPlayer != null && controlledBy(this.closestPlayer) && MillConfigValues.LogSelling >= 2) {
        MillLog.minor(this, "Not sending seller because the nearby player owns the village.");
      } 
      return;
    } 
    if (!this.chestLocked && MillConfigValues.LogSelling >= 2)
      MillLog.minor(this, "Not sending seller because village chests are not locked."); 
    if (getReputation(this.closestPlayer) < -1024 && MillConfigValues.LogSelling >= 2)
      MillLog.minor(this, "Not sending seller because player's reputation is not sufficient: " + getReputation(this.closestPlayer)); 
    if (this.closestPlayer != null && this.seller == null && getReputation(this.closestPlayer) >= -1024 && this.chestLocked) {
      this.sellingPlace = null;
      if (MillConfigValues.LogSelling >= 2)
        MillLog.minor(this, "A seller is required for " + this.closestPlayer.getName()); 
      for (BuildingLocation l : getLocations()) {
        if (l.level >= 0 && l.chestPos != null && l.shop != null && l.shop.length() > 0) {
          if (l.getSellingPos() != null && l.getSellingPos().distanceTo((Entity)this.closestPlayer) < 3.0D) {
            this.sellingPlace = l.getSellingPos();
            continue;
          } 
          if (l.getSellingPos() == null && l.sleepingPos.distanceTo((Entity)this.closestPlayer) < 3.0D)
            this.sellingPlace = l.sleepingPos; 
        } 
      } 
      if (this.sellingPlace == null && MillConfigValues.LogSelling >= 2)
        MillLog.minor(this, "Can't send player because there is no selling place."); 
      if (this.sellingPlace != null) {
        if (MillConfigValues.LogSelling >= 2)
          MillLog.minor(this, "Checking through villagers to find a seller."); 
        for (MillVillager villager : getKnownVillagers()) {
          if (villager.isSeller())
            if (getConstructionIPforBuilder(villager) == null && (this.seller == null || this.sellingPlace.distanceToSquared((Entity)villager) < this.sellingPlace.distanceToSquared((Entity)this.seller)))
              this.seller = villager;  
        } 
        if (this.seller != null) {
          this.seller.clearGoal();
          this.seller.goalKey = Goal.beSeller.key;
          Goal.beSeller.onAccept(this.seller);
          if (MillConfigValues.LogSelling >= 3)
            MillLog.debug(this, "Sending seller: " + this.seller); 
        } 
      } 
    } 
  }
  
  public void checkWorkers() {
    if (this.seller != null && !Goal.beSeller.key.equals(this.seller.goalKey))
      this.seller = null; 
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuilder() != null)
        if (((cip.getBuilder()).goalKey != null && !Goal.getResourcesForBuild.key.equals((cip.getBuilder()).goalKey) && !Goal.construction.key.equals((cip.getBuilder()).goalKey)) || cip
          .getId() != (cip.getBuilder()).constructionJobId) {
          if (MillConfigValues.LogBuildingPlan >= 1)
            MillLog.major(this, cip.getBuilder().getName() + " is no longer building."); 
          cip.setBuilder(null);
        }  
    } 
  }
  
  public void choseAndApplyBrickTheme() {
    if (this.villageType.brickColourThemes.isEmpty())
      return; 
    this.brickColourTheme = (VillageType.BrickColourTheme)MillCommonUtilities.getWeightedChoice(this.villageType.brickColourThemes, null);
    for (Building b : getBuildings()) {
      if ((b.location.getPlan()).randomBrickColours.isEmpty()) {
        b.location.initialiseBrickColoursFromTheme(this, this.brickColourTheme);
        for (int x = b.location.minx; x <= b.location.maxx; x++) {
          for (int z = b.location.minz; z <= b.location.maxz; z++) {
            for (int y = b.location.miny - 20; y <= b.location.maxy + 20; y++) {
              BlockPos bp = new BlockPos(x, y, z);
              IBlockState bs = this.world.getBlockState(bp);
              if (bs.getBlock() instanceof org.millenaire.common.block.IPaintedBlock) {
                EnumDyeColor currentColor = BlockPaintedBricks.getColourFromBlockState(bs);
                if (b.location.paintedBricksColour.containsKey(currentColor))
                  this.world.setBlockState(bp, BlockPaintedBricks.getBlockStateWithColour(bs, b.location.paintedBricksColour.get(currentColor))); 
              } 
            } 
          } 
        } 
      } 
    } 
  }
  
  private void clearAllAttackers() {
    int nbCleared = 0;
    List<VillagerRecord> villagerRecordsTemp = new ArrayList<>(getAllVillagerRecords());
    for (VillagerRecord vr : villagerRecordsTemp) {
      if (vr.raidingVillage) {
        this.mw.removeVillagerRecord(vr.getVillagerId());
        nbCleared++;
      } 
    } 
    if (MillConfigValues.LogDiplomacy >= 1)
      MillLog.major(this, "Cleared " + nbCleared + " attackers."); 
    Set<MillVillager> villagersTemp = new HashSet<>(getKnownVillagers());
    for (MillVillager villager : villagersTemp) {
      if (villager.isRaider) {
        villager.despawnVillagerSilent();
        if (MillConfigValues.LogDiplomacy >= 1)
          MillLog.major(this, "Despawning invader: " + villager); 
      } 
    } 
  }
  
  public void clearOldPaths() {
    if (this.oldPathPointsToClear != null) {
      for (Point p : this.oldPathPointsToClear)
        PathUtilities.clearPathBlock(p, this.world); 
      this.oldPathPointsToClear = null;
      this.pathsChanged = true;
      requestSave("paths clearing rushed");
    } 
  }
  
  public void clearTags(Collection<String> tagsToClear, String reason) {
    int nbTags = this.tags.size();
    List<String> clearedTags = new ArrayList<>();
    for (String tag : tagsToClear) {
      if (this.tags.contains(tag)) {
        clearedTags.add(tag);
        this.tags.remove(tag);
      } 
    } 
    if (MillConfigValues.LogTags >= 1 && clearedTags.size() > 0 && !reason.contains("client-side"))
      MillLog.major(this, "Cleared tags due to '" + reason + "': " + MillCommonUtilities.flattenStrings(clearedTags) + ", went from " + nbTags + " to " + this.tags.size() + ". Current tags: " + 
          MillCommonUtilities.flattenStrings(this.tags)); 
  }
  
  private void completeConstruction(ConstructionIP cip) throws MillLog.MillenaireException {
    if (cip.getBuildingLocation() != null && cip.getBblocks() == null) {
      BuildingPlan plan = getBuildingPlanForConstruction(cip);
      registerBuildingLocation(cip.getBuildingLocation());
      updateWorldInfo();
      if (cip.getBuildingLocation() != null && cip.getBuildingLocation().isSameLocation(this.buildingGoalLocation)) {
        this.buildingGoalLocation = null;
        this.buildingGoal = null;
        this.buildingGoalIssue = null;
        this.buildingGoalLevel = -1;
      } 
      cip.setBuilder(null);
      cip.setBuildingLocation(null);
      if (plan.rebuildPath || plan.level == 0 || (plan.getPreviousBuildingPlan()).pathLevel != plan.pathLevel)
        recalculatePaths(false); 
    } 
  }
  
  public int computeCurrentWallLevel() {
    int wallLevel = Integer.MAX_VALUE;
    boolean wallFound = false;
    List<BuildingProject> projects = getFlatProjectList();
    for (BuildingProject project : projects) {
      BuildingPlan initialPlan = project.getPlan(0, 0);
      BuildingPlan plan = project.getNextBuildingPlan(false);
      if (plan != null && plan.isWallSegment) {
        wallFound = true;
        if (isValidProject(project))
          for (int i = 0; i < 10; i++) {
            if (i < wallLevel && plan.containsTags("wall_level_" + i))
              wallLevel = i; 
          }  
        continue;
      } 
      if (initialPlan != null && initialPlan.isWallSegment)
        wallFound = true; 
    } 
    if (!wallFound)
      return -1; 
    return wallLevel;
  }
  
  public void computeShopGoods(EntityPlayer player) {
    List<TradeGood> sellingGoods = calculateSellingGoods((IInventory)player.inventory);
    if (sellingGoods != null) {
      LinkedHashMap<TradeGood, Integer> shopSellsPlayer = new LinkedHashMap<>();
      for (TradeGood g : sellingGoods) {
        if (g.getBasicSellingPrice(this) > 0)
          shopSellsPlayer.put(g, Integer.valueOf(g.getBasicSellingPrice(this))); 
      } 
      this.shopSells.put(player.getName(), shopSellsPlayer);
    } 
    List<TradeGood> buyingGoods = calculateBuyingGoods((IInventory)player.inventory);
    if (buyingGoods != null) {
      LinkedHashMap<TradeGood, Integer> shopBuysPlayer = new LinkedHashMap<>();
      for (TradeGood g : buyingGoods) {
        if (g.getBasicBuyingPrice(this) > 0)
          shopBuysPlayer.put(g, Integer.valueOf(g.getBasicBuyingPrice(this))); 
      } 
      this.shopBuys.put(player.getName(), shopBuysPlayer);
    } 
  }
  
  public void constructCalculatedPaths() {
    if (this.pathsToBuild != null) {
      if (MillConfigValues.LogVillagePaths >= 2)
        MillLog.minor(this, "Rebuilding calculated paths."); 
      for (List<BuildingBlock> path : this.pathsToBuild) {
        if (!path.isEmpty())
          for (BuildingBlock bp : path)
            bp.pathBuild(this);  
      } 
      this.pathsToBuild = null;
      this.pathsChanged = true;
      requestSave("paths rushed");
    } 
  }
  
  public boolean containsTags(String tag) {
    return this.tags.contains(tag.toLowerCase());
  }
  
  public boolean controlledBy(EntityPlayer player) {
    if (!this.isTownhall && getTownHall() != null) {
      if (getTownHall() == this) {
        MillLog.error(this, "isTownHall is false but building is its own Town Hall.");
        return false;
      } 
      return getTownHall().controlledBy(player);
    } 
    return (this.controlledBy != null && this.controlledBy.equals((this.mw.getProfile(player)).uuid));
  }
  
  public int countChildren() {
    int nb = 0;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.getType() != null && (vr.getType()).isChild)
        nb++; 
    } 
    return nb;
  }
  
  public int countGoods(Block block, int meta) {
    return countGoods(Item.getItemFromBlock(block), meta);
  }
  
  public int countGoods(IBlockState blockState) {
    return countGoods(Item.getItemFromBlock(blockState.getBlock()), blockState.getBlock().getMetaFromState(blockState));
  }
  
  public int countGoods(InvItem iv) {
    return countGoods(iv.getItem(), iv.meta);
  }
  
  public int countGoods(Item item) {
    return countGoods(item, 0);
  }
  
  public int countGoods(Item item, int meta) {
    return getInventoryCountFromCache(InvItem.createInvItem(item, meta));
  }
  
  public int countGoodsOld(Item item, int meta) {
    int count = 0;
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      count += MillCommonUtilities.countChestItems((IInventory)chest, item, meta);
    } 
    return count;
  }
  
  public int countVillageGoods(InvItem iv) {
    int count = 0;
    for (Building b : getBuildings())
      count += b.countGoods(iv.getItem(), iv.meta); 
    return count;
  }
  
  public MillVillager createChild(MillVillager mother, Building townHall, String fathersName) {
    try {
      if (MillConfigValues.LogWorldGeneration >= 2)
        MillLog.minor(this, "Creating child: " + mother.familyName); 
      int gender = getNewGender();
      String type = (gender == 1) ? mother.getMaleChild() : mother.getFemaleChild();
      VillagerRecord vr = VillagerRecord.createVillagerRecord(townHall.culture, type, this.mw, getPos(), getTownHallPos(), null, mother.familyName, -1L, false);
      MillVillager child = MillVillager.createVillager(vr, this.world, this.resManager.getSleepingPos(), false);
      if (child == null)
        throw new MillLog.MillenaireException("Child not instancied in createVillager"); 
      vr.fathersName = fathersName;
      vr.mothersName = mother.getName();
      this.world.spawnEntity((Entity)child);
      return child;
    } catch (Exception e) {
      Mill.proxy.sendChatAdmin("Error in createChild(). Check millenaire.log.");
      MillLog.error(this, "Exception in createChild.onUpdate(): ");
      MillLog.printException(e);
      return null;
    } 
  }
  
  public MillVillager createNewVillager(String type) throws MillLog.MillenaireException {
    VillagerRecord vr = VillagerRecord.createVillagerRecord(this.culture, type, this.mw, getPos(), getTownHallPos(), null, null, -1L, false);
    MillVillager villager = MillVillager.createVillager(vr, this.world, this.resManager.getSleepingPos(), false);
    this.world.spawnEntity((Entity)villager);
    if (villager.vtype.isChild) {
      vr.size = 20;
      villager.growSize();
    } 
    return villager;
  }
  
  public String createResidents() throws MillLog.MillenaireException {
    if (this.location.getMaleResidents().size() + this.location.getFemaleResidents().size() == 0)
      return null; 
    String familyName = null;
    String husbandType = null;
    if (this.location.getMaleResidents().size() > 0 && !(this.culture.getVillagerType((String)this.location.getMaleResidents().get(0))).isChild)
      husbandType = this.location.getMaleResidents().get(0); 
    String wifeType = null;
    if (this.location.getFemaleResidents().size() > 0 && !(this.culture.getVillagerType((String)this.location.getFemaleResidents().get(0))).isChild)
      wifeType = this.location.getFemaleResidents().get(0); 
    if (MillConfigValues.LogMerchant >= 2)
      MillLog.minor(this, "Creating " + husbandType + " and " + wifeType + ": " + familyName); 
    VillagerRecord husbandRecord = null, wifeRecord = null;
    if (this.resManager.getSleepingPos() == null) {
      MillLog.error(this, "Wanted to create villagers but sleepingPos is null!");
      return "";
    } 
    if (husbandType != null) {
      husbandRecord = VillagerRecord.createVillagerRecord(this.culture, husbandType, this.mw, getPos(), getTownHallPos(), null, null, -1L, false);
      MillVillager husband = MillVillager.createVillager(husbandRecord, this.world, this.resManager.getSleepingPos(), false);
      familyName = husband.familyName;
      this.world.spawnEntity((Entity)husband);
    } 
    if (wifeType != null) {
      wifeRecord = VillagerRecord.createVillagerRecord(this.culture, wifeType, this.mw, getPos(), getTownHallPos(), null, familyName, -1L, false);
      MillVillager wife = MillVillager.createVillager(wifeRecord, this.world, this.resManager.getSleepingPos(), false);
      wifeRecord = new VillagerRecord(this.mw, wife);
      this.world.spawnEntity((Entity)wife);
    } 
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(this, "Records: " + wifeRecord + "/" + husbandRecord); 
    if (wifeRecord != null && husbandRecord != null) {
      wifeRecord.spousesName = husbandRecord.getName();
      husbandRecord.spousesName = wifeRecord.getName();
    } 
    int startPos = (husbandType == null) ? 0 : 1;
    int i;
    for (i = startPos; i < this.location.getMaleResidents().size(); i++)
      createNewVillager(this.location.getMaleResidents().get(i)); 
    startPos = (wifeType == null) ? 0 : 1;
    for (i = startPos; i < this.location.getFemaleResidents().size(); i++)
      createNewVillager(this.location.getFemaleResidents().get(i)); 
    if (this.isInn) {
      merchantCreated(husbandRecord);
    } else {
      getPanelManager().updateSigns();
    } 
    return familyName;
  }
  
  public void destroyVillage() {
    if (MillConfigValues.LogVillage >= 1)
      MillLog.major(this, "Destroying the village!"); 
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      if (chest != null)
        chest.buildingPos = null; 
    } 
    for (Point p : this.buildings) {
      Building building = this.mw.getBuilding(p);
      if (building != null)
        for (Point p2 : this.resManager.chests) {
          TileEntityLockedChest chest = p2.getMillChest(this.world);
          if (chest != null)
            chest.buildingPos = null; 
        }  
    } 
    List<MillVillager> villagersToDestroy = new ArrayList<>(getKnownVillagers());
    for (MillVillager villager : villagersToDestroy)
      villager.despawnVillager(); 
    for (Point p : this.buildings)
      this.mw.removeBuilding(p); 
    this.mw.removeVillageOrLoneBuilding(getPos());
    File millenaireDir = this.mw.millenaireDir;
    if (!millenaireDir.exists())
      millenaireDir.mkdir(); 
    File buildingsDir = new File(millenaireDir, "buildings");
    if (!buildingsDir.exists())
      buildingsDir.mkdir(); 
    File file1 = new File(buildingsDir, getPos().getPathString() + ".gz");
    if (file1.exists()) {
      file1.renameTo(new File(millenaireDir, getPos().getPathString() + "ToDelete"));
      file1.delete();
    } 
  }
  
  public void displayInfos(EntityPlayer player) {
    if (this.location == null)
      return; 
    int nbAdults = 0, nbGrownChild = 0;
    for (MillVillager villager : getKnownVillagers()) {
      if (!villager.isChild()) {
        nbAdults++;
        continue;
      } 
      if (villager.getSize() == 20)
        nbGrownChild++; 
    } 
    ServerSender.sendChat(player, TextFormatting.GREEN, "It has " + getKnownVillagers().size() + " villagers registered. (" + nbAdults + " adults, " + nbGrownChild + " grown children)");
    ServerSender.sendChat(player, TextFormatting.GREEN, "Pos: " + getPos() + " sell pos:" + this.resManager.getSellingPos());
    if (this.isTownhall) {
      ServerSender.sendChat(player, TextFormatting.GREEN, "It has " + this.buildings.size() + " houses registered.");
      ServerSender.sendChat(player, TextFormatting.GREEN, "Connections build: " + ((this.regionMapper != null) ? 1 : 0));
      ServerSender.sendChat(player, TextFormatting.GREEN, "Village name: " + getVillageQualifiedName());
      for (ConstructionIP cip : getConstructionsInProgress()) {
        if (cip.getBuildingLocation() != null) {
          ServerSender.sendChat(player, TextFormatting.GREEN, "Construction IP: " + getBuildingPlanForConstruction(cip) + " at " + cip.getBuildingLocation());
          ServerSender.sendChat(player, TextFormatting.GREEN, "Current builder: " + cip.getBuilder());
        } 
      } 
      ServerSender.sendChat(player, TextFormatting.GREEN, "Current seller: " + this.seller);
      ServerSender.sendChat(player, TextFormatting.GREEN, "Rep: " + getReputation(player) + " bought: " + this.buildingsBought);
    } 
    if (this.isInn) {
      ServerSender.sendChat(player, TextFormatting.GREEN, "Merchant: " + this.merchantRecord);
      ServerSender.sendChat(player, TextFormatting.GREEN, "Merchant nights: " + this.nbNightsMerchant);
    } 
    if (getTags() == null) {
      ServerSender.sendChat(player, TextFormatting.GREEN, "UNKNOWN TAGS");
    } else if (getTags().size() > 0) {
      String str = "Tags: ";
      for (String tag : getTags())
        str = str + tag + " "; 
      ServerSender.sendChat(player, TextFormatting.GREEN, str);
    } 
    if (this.resManager.chests.size() > 1)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Chests registered: " + this.resManager.chests.size()); 
    if (this.resManager.furnaces.size() > 1)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Furnaces registered: " + this.resManager.furnaces.size()); 
    if (this.resManager.firepits.size() > 1)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Firepits registered: " + this.resManager.firepits.size()); 
    for (int i = 0; i < this.resManager.soilTypes.size(); i++)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Fields registered: " + this.resManager.soilTypes.get(i) + ": " + ((CopyOnWriteArrayList)this.resManager.soils.get(i)).size()); 
    if (this.resManager.sugarcanesoils.size() > 0)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Sugar cane soils registered: " + this.resManager.sugarcanesoils.size()); 
    if (this.resManager.fishingspots.size() > 0)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Fishing spots registered: " + this.resManager.fishingspots.size()); 
    if (this.resManager.stalls.size() > 0)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Stalls registered: " + this.resManager.stalls.size()); 
    if (this.resManager.woodspawn.size() > 0)
      ServerSender.sendChat(player, TextFormatting.GREEN, "Wood spawn registered: " + this.resManager.woodspawn.size()); 
    if (this.resManager.spawns.size() > 0) {
      String str = "Pens: ";
      for (int j = 0; j < this.resManager.spawns.size(); j++)
        str = str + this.resManager.spawnTypes.get(j) + ": " + ((CopyOnWriteArrayList)this.resManager.spawns.get(j)).size() + " "; 
      ServerSender.sendChat(player, TextFormatting.GREEN, str);
    } 
    if (this.resManager.mobSpawners.size() > 0) {
      String str = "Mob spawners: ";
      for (int j = 0; j < this.resManager.mobSpawners.size(); j++)
        str = str + this.resManager.mobSpawnerTypes.get(j) + ": " + ((CopyOnWriteArrayList)this.resManager.mobSpawners.get(j)).size() + " "; 
      ServerSender.sendChat(player, TextFormatting.GREEN, str);
    } 
    if (this.resManager.sources.size() > 0) {
      String str = "Sources: ";
      for (int j = 0; j < this.resManager.sources.size(); j++)
        str = str + ((IBlockState)this.resManager.sourceTypes.get(j)).toString() + ": " + ((CopyOnWriteArrayList)this.resManager.sources.get(j)).size() + " "; 
      ServerSender.sendChat(player, TextFormatting.GREEN, str);
    } 
    for (MillVillager villager : getKnownVillagers()) {
      if (villager == null) {
        ServerSender.sendChat(player, TextFormatting.GREEN, "NULL villager!");
        continue;
      } 
      ServerSender.sendChat(player, TextFormatting.GREEN, villager
          .getClass().getSimpleName() + ": " + villager.getPos() + (villager.isEntityAlive() ? "" : " DEAD") + " " + villager.getGoalLabel(villager.goalKey));
    } 
    String s = "LKey: " + this.location.planKey + " Shop: " + this.location.shop + " special: ";
    if (this.isTownhall)
      s = s + "Town Hall "; 
    if (this.isInn)
      s = s + "Inn "; 
    if (this.isMarket)
      s = s + "Market"; 
    if (this.pujas != null)
      s = s + "Shrine "; 
    if (!s.equals(""))
      ServerSender.sendChat(player, TextFormatting.GREEN, s); 
    if (this.pathsToBuild != null || this.oldPathPointsToClear != null) {
      if (this.pathsToBuild != null) {
        s = "pathsToBuild: " + this.pathsToBuild.size() + " " + this.pathsToBuildIndex + "/" + this.pathsToBuildPathIndex;
      } else {
        s = "pathsToBuild:null";
      } 
      if (this.oldPathPointsToClear != null) {
        s = s + " oldPathPointsToClear: " + this.oldPathPointsToClear.size() + " " + this.oldPathPointsToClearIndex;
      } else {
        s = s + " oldPathPointsToClear:null";
      } 
      ServerSender.sendChat(player, TextFormatting.GREEN, s);
    } 
    validateVillagerList();
  }
  
  private boolean endRaid() {
    boolean attackersWon;
    Building targetVillage = this.mw.getBuilding(this.raidTarget);
    if (targetVillage == null) {
      MillLog.error(this, "endRaid() called but couldn't find raidTarget at: " + this.raidTarget);
      return false;
    } 
    if (targetVillage.location == null) {
      MillLog.error(this, "endRaid() called but target is missing its location at: " + this.raidTarget);
      return false;
    } 
    if (MillConfigValues.LogDiplomacy >= 1)
      MillLog.major(this, "Called to end raid on " + targetVillage); 
    float defendingForce = targetVillage.getVillageDefendingStrength() * (1.0F + MillCommonUtilities.random.nextFloat());
    float attackingForce = targetVillage.getVillageAttackerStrength() * (1.0F + MillCommonUtilities.random.nextFloat());
    if (attackingForce == 0.0F) {
      attackersWon = false;
    } else if (defendingForce == 0.0F) {
      attackersWon = true;
    } else {
      float ratio = attackingForce / defendingForce;
      attackersWon = (ratio > 1.2D);
    } 
    if (MillConfigValues.LogDiplomacy >= 1)
      MillLog.major(this, "Result of raid: " + attackersWon + " (" + attackingForce + "/" + attackingForce + ")"); 
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.awayraiding) {
        vr.awayraiding = false;
        VillagerRecord awayRecord = this.mw.getVillagerRecordById(vr.getOriginalId());
        if (awayRecord != null) {
          vr.killed = awayRecord.killed;
          continue;
        } 
        vr.killed = false;
      } 
    } 
    targetVillage.clearAllAttackers();
    for (MillVillager v : targetVillage.getKnownVillagers()) {
      if (v.getAttackTarget() != null && v.getAttackTarget() instanceof MillVillager)
        v.setAttackTarget(null); 
    } 
    cancelRaid();
    targetVillage.underAttack = false;
    if (attackersWon) {
      int nbStolen = 0;
      String taken = "";
      for (TradeGood good : this.culture.goodsList) {
        if (nbStolen <= 1024) {
          int nbToTake = nbGoodNeeded(good.item.getItem(), good.item.meta);
          nbToTake = Math.min(nbToTake, Math.max(0, 1024 - nbStolen));
          if (nbToTake > 0) {
            nbToTake = Math.min(nbToTake, targetVillage.countGoods(good.item));
            if (nbToTake > 0) {
              if (MillConfigValues.LogDiplomacy >= 3)
                MillLog.debug(this, "Able to take: " + nbToTake + " " + good.getName()); 
              targetVillage.takeGoods(good.item, nbToTake);
              storeGoods(good.item, nbToTake);
              nbStolen += nbToTake;
              taken = taken + ";" + good.item.getItem() + "/" + good.item.meta + "/" + nbToTake;
            } 
          } 
        } 
      } 
      this.raidsPerformed.add("success;" + targetVillage.getVillageQualifiedName() + taken);
      targetVillage.raidsSuffered.add("success;" + getVillageQualifiedName() + taken);
      if (MillConfigValues.LogDiplomacy >= 1)
        MillLog.major(this, "Raid on " + targetVillage + " successfull (" + attackingForce + "/" + defendingForce + ")"); 
      ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.BackgroundRadius, '4', "raid.raidsuccesfull", new String[] { getVillageQualifiedName(), targetVillage
            .getVillageQualifiedName(), "" + nbStolen });
      if (this.controlledBy != null) {
        EntityPlayer owner = this.world.getPlayerEntityByUUID(this.controlledBy);
        if (owner != null) {
          MillAdvancements.VIKING.grant(owner);
          if (this.culture.key.equals("seljuk") && targetVillage.culture.key.equals("byzantines"))
            MillAdvancements.ISTANBUL.grant(owner); 
          if (targetVillage.controlledBy != null && !this.controlledBy.equals(targetVillage.controlledBy))
            MillAdvancements.MP_RAIDONPLAYER.grant(owner); 
        } 
      } 
    } else {
      this.raidsPerformed.add("failure;" + targetVillage.getVillageQualifiedName());
      targetVillage.raidsSuffered.add("failure;" + getVillageQualifiedName());
      if (MillConfigValues.LogDiplomacy >= 1)
        MillLog.major(this, "Raid on " + targetVillage + " failed (" + attackingForce + "/" + defendingForce + ")"); 
      if (targetVillage.controlledBy != null && 
        this.culture.key.equals("seljuk") && targetVillage.culture.key.equals("byzantines")) {
        EntityPlayer targetOwner = this.world.getPlayerEntityByUUID(targetVillage.controlledBy);
        MillAdvancements.NOTTODAY.grant(targetOwner);
      } 
      ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.BackgroundRadius, '4', "raid.raidfailed", new String[] { getVillageQualifiedName(), targetVillage
            .getVillageQualifiedName() });
    } 
    MillLog.major(this, "Finished ending raid. Records: " + getVillagerRecords().size());
    targetVillage.saveTownHall("Raid on village ended");
    this.saveNeeded = true;
    this.saveReason = "Raid finished";
    return true;
  }
  
  public int estimateAbstractedProductionCapacity(InvItem invItem) {
    BuildingPlan plan = this.location.getPlan();
    if (plan != null && plan.abstractedProduction.containsKey(invItem))
      return ((Integer)plan.abstractedProduction.get(invItem)).intValue(); 
    return 0;
  }
  
  private void fillinBuildingLocationInProjects(BuildingLocation location) {
    this.mw.testLocations("fillinBuildingLocation start");
    boolean registered = false;
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        List<BuildingProject> temp = new ArrayList<>(projectsLevel);
        for (BuildingProject project : temp) {
          int pos = 0;
          if (!registered && project.location == null && location.planKey.equals(project.key)) {
            project.location = location;
            registered = true;
            if (MillConfigValues.LogBuildingPlan >= 2)
              MillLog.minor(this, "Registered building: " + location + " (level " + location.level + ", variation: " + location.getVariation() + ")"); 
            if (project.location.level >= 0)
              for (String s : project.location.subBuildings) {
                BuildingProject newproject = new BuildingProject(this.culture.getBuildingPlanSet(s), project.location.getPlan());
                newproject.location = location.createLocationForSubBuilding(s);
                projectsLevel.add(pos + 1, newproject);
                if (MillConfigValues.LogBuildingPlan >= 1)
                  MillLog.major(this, "Adding sub-building to project list: " + newproject + " at pos " + pos + " in " + projectsLevel); 
              }  
            pos++;
            continue;
          } 
          if (!registered && project.location != null && project.location.level < 0 && project.location.isSameLocation(location)) {
            project.location = location;
            registered = true;
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, "Registered subbuilding: " + location + " (level " + location.level + ", variation: " + location.getVariation() + ")"); 
          } 
        } 
      } 
    } 
    if (!registered)
      if (location.isCustomBuilding) {
        BuildingProject project = new BuildingProject(this.culture.getBuildingCustom(location.planKey), location);
        ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.CUSTOMBUILDINGS)).add(project);
      } else {
        BuildingProject project = new BuildingProject(this.culture.getBuildingPlanSet(location.planKey));
        project.location = location;
        if (this.villageType.playerControlled) {
          ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.CORE)).add(project);
        } else if ((location.getPlan()).isWallSegment) {
          if (!this.buildingProjects.containsKey(BuildingProject.EnumProjects.WALLBUILDING))
            this.buildingProjects.put(BuildingProject.EnumProjects.WALLBUILDING, new CopyOnWriteArrayList<>()); 
          ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.WALLBUILDING)).add(project);
        } else {
          ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.EXTRA)).add(project);
        } 
      }  
    this.mw.testLocations("fillinBuildingLocation end");
  }
  
  public void fillStartingGoods() {
    if (this.location.getPlan() == null)
      return; 
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      if (chest != null)
        for (int i = 0; i < chest.getSizeInventory(); i++)
          chest.setInventorySlotContents(i, ItemStack.EMPTY);  
    } 
    for (BuildingPlan.StartingGood sg : (this.location.getPlan()).startingGoods) {
      if (MillCommonUtilities.probability(sg.probability)) {
        int nb = sg.fixedNumber;
        if (sg.randomNumber > 0)
          nb += MillCommonUtilities.randomInt(sg.randomNumber + 1); 
        if (nb > 0) {
          int chestId = MillCommonUtilities.randomInt(this.resManager.chests.size());
          TileEntityLockedChest chest = ((Point)this.resManager.chests.get(chestId)).getMillChest(this.world);
          if (chest != null)
            MillCommonUtilities.putItemsInChest((IInventory)chest, sg.item.getItem(), sg.item.meta, nb); 
        } 
      } 
    } 
    invalidateInventoryCache();
    if (MillConfigValues.DEV)
      testModeGoods(); 
  }
  
  private Point findAttackerSpawnPoint(Point origin) {
    int x;
    int z;
    if (origin.getiX() > this.pos.getiX()) {
      x = Math.min(this.winfo.length - 5, this.winfo.length / 2 + 50);
    } else {
      x = Math.max(5, this.winfo.length / 2 - 50);
    } 
    if (origin.getiZ() > this.pos.getiZ()) {
      z = Math.min(this.winfo.width - 5, this.winfo.width / 2 + 50);
    } else {
      z = Math.max(5, this.winfo.width / 2 - 50);
    } 
    for (int i = 0; i < 40; i++) {
      int tx = x + MillCommonUtilities.randomInt(5 + i) - MillCommonUtilities.randomInt(5 + i);
      int tz = z + MillCommonUtilities.randomInt(5 + i) - MillCommonUtilities.randomInt(5 + i);
      tx = Math.max(Math.min(tx, this.winfo.length - 1), 0);
      tz = Math.max(Math.min(tz, this.winfo.width - 1), 0);
      tx = Math.min(tx, this.winfo.length / 2 + 50);
      tx = Math.max(tx, this.winfo.length / 2 - 50);
      tz = Math.min(tz, this.winfo.width / 2 + 50);
      tz = Math.max(tz, this.winfo.width / 2 - 50);
      if (this.winfo.canBuild[tx][tz]) {
        Chunk chunk = this.world.getChunkFromBlockCoords(new BlockPos(this.winfo.mapStartX + tx, 0, this.winfo.mapStartZ + tz));
        if (chunk.isLoaded())
          return new Point((this.winfo.mapStartX + tx), (WorldUtilities.findTopSoilBlock(this.world, this.winfo.mapStartX + tx, this.winfo.mapStartZ + tz) + 1), (this.winfo.mapStartZ + tz)); 
      } 
    } 
    return this.resManager.getDefendingPos();
  }
  
  private boolean findBuildingConstruction(boolean ignoreCost) {
    if (this.buildingGoal == null)
      return false; 
    if (this.regionMapper == null)
      try {
        rebuildRegionMapper(true);
      } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
        MillLog.printException((Throwable)e);
      }  
    ConstructionIP targetConstruction = null;
    int nbNonWallConstructions = 0;
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (!cip.isWallConstruction()) {
        if (targetConstruction == null && cip.getBuildingLocation() == null)
          targetConstruction = cip; 
        nbNonWallConstructions++;
      } 
    } 
    if (targetConstruction == null && nbNonWallConstructions < getSimultaneousConstructionSlots()) {
      targetConstruction = new ConstructionIP(this, getConstructionsInProgress().size(), false);
      getConstructionsInProgress().add(targetConstruction);
    } 
    if (targetConstruction == null)
      return false; 
    BuildingProject goalProject = null;
    if (findConstructionIPforLocation(this.buildingGoalLocation) == null && findConstructionIPforBuildingPlanKey(this.buildingGoal, false) == null) {
      for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
        if (this.buildingProjects.containsKey(ep)) {
          List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
          for (BuildingProject project : projectsLevel) {
            if (this.buildingGoalLocation != null && this.buildingGoalLocation.isSameLocation(project.location)) {
              goalProject = project;
              continue;
            } 
            if (this.buildingGoalLocation == null && project.location == null && this.buildingGoal.equals(project.key))
              goalProject = project; 
          } 
        } 
      } 
      if (MillConfigValues.LogBuildingPlan >= 3)
        MillLog.debug(this, "Building goal project: " + goalProject + " "); 
      if (goalProject == null) {
        MillLog.error(this, "Could not find building project for " + this.buildingGoal + " and " + this.buildingGoalLocation + ", cancelling goal.");
        this.buildingGoal = null;
        this.buildingGoalLocation = null;
        return false;
      } 
      if (goalProject.location != null && goalProject.location.level >= 0 && goalProject.location.upgradesAllowed) {
        if (ignoreCost || canAffordProject(goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel))) {
          BuildingLocation bl;
          if (this.buildingGoalLocation != null) {
            bl = this.buildingGoalLocation;
          } else {
            bl = goalProject.location;
          } 
          targetConstruction.startNewConstruction(bl, goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel).getBuildingPoints(this.world, bl, false, false, false));
          if (MillConfigValues.LogBuildingPlan >= 1)
            MillLog.major(this, "Upgrade project possible at: " + this.location + " for level " + this.buildingGoalLevel); 
          if ((targetConstruction.getBblocks()).length == 0) {
            MillLog.error(this, "No bblocks for\t " + targetConstruction.getBuildingLocation());
            try {
              rushCurrentConstructions(false);
            } catch (Exception e) {
              MillLog.printException("Exception when trying to rush building:", e);
            } 
          } 
        } else {
          this.buildingGoalIssue = "ui.lackingresources";
        } 
      } else if (goalProject.location != null && goalProject.location.level < 0) {
        if (ignoreCost || canAffordProject(goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel))) {
          BuildingLocation bl;
          if (this.buildingGoalLocation != null) {
            bl = this.buildingGoalLocation;
          } else {
            bl = goalProject.location;
          } 
          targetConstruction.startNewConstruction(bl, goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel).getBuildingPoints(this.world, bl, false, false, false));
          if ((targetConstruction.getBblocks()).length == 0)
            MillLog.error(this, "No bblocks for\t " + targetConstruction.getBuildingLocation()); 
        } else {
          this.buildingGoalIssue = "ui.lackingresources";
        } 
      } else if (goalProject.location == null) {
        boolean canAffordProject = (ignoreCost || canAffordProject(goalProject.getPlan(this.buildingGoalVariation, 0)));
        if (System.currentTimeMillis() - this.lastFailedProjectLocationSearch > 80000L && canAffordProject) {
          BuildingLocation location = goalProject.getPlan(this.buildingGoalVariation, 0).findBuildingLocation(this.winfo, this.regionMapper, this.location.pos, this.villageType.radius, 
              MillCommonUtilities.getRandom(), -1);
          this.lastFailedProjectLocationSearch = System.currentTimeMillis();
          if (location != null) {
            this.lastFailedProjectLocationSearch = 0L;
            if (this.brickColourTheme != null)
              if ((location.getPlan()).randomBrickColours.isEmpty())
                location.initialiseBrickColoursFromTheme(this, this.brickColourTheme);  
            this.buildingGoalLocation = location;
            targetConstruction.startNewConstruction(location, goalProject.getPlan(this.buildingGoalVariation, 0).getBuildingPoints(this.world, location, false, false, false));
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, "New project location: Loaded " + (targetConstruction
                  .getBblocks()).length + " building blocks for " + (goalProject.getPlan(this.buildingGoalVariation, 0)).planName); 
            int groundLevel = WorldUtilities.findTopSoilBlock(this.world, location.pos.getiX(), location.pos.getiZ());
            for (int i = groundLevel + 1; i < location.pos.getiY(); i++)
              WorldUtilities.setBlockAndMetadata(this.world, location.pos, Blocks.DIRT, 0); 
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, "Found location for building project: " + location); 
          } else {
            this.buildingGoalIssue = "ui.nospace";
            this.lastFailedProjectLocationSearch = System.currentTimeMillis();
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, "Searching for a location for the new project failed."); 
          } 
        } else if (!canAffordProject) {
          this.buildingGoalIssue = "ui.lackingresources";
          if (MillConfigValues.LogBuildingPlan >= 3)
            MillLog.debug(this, "Cannot afford building project."); 
        } else {
          this.buildingGoalIssue = "ui.nospace";
        } 
      } 
    } 
    if (targetConstruction.getBuildingLocation() != null)
      return true; 
    boolean attemptedConstruction = false;
    List<BuildingProject> possibleProjects = getAllPossibleProjects();
    List<BuildingProject> affordableProjects = new ArrayList<>();
    for (BuildingProject project : possibleProjects) {
      if (project.planSet != null && (goalProject == null || project != goalProject)) {
        if (project.location == null || project.location.level < 0) {
          if (findConstructionIPforBuildingPlanKey(project.planSet.key, true) == null && (
            ignoreCost || canAffordBuild(project.planSet.getFirstStartingPlan())))
            affordableProjects.add(project); 
          continue;
        } 
        if (findConstructionIPforLocation(project.location) == null) {
          BuildingPlan plan = project.getNextBuildingPlan(true);
          if (ignoreCost || canAffordBuild(plan))
            affordableProjects.add(project); 
        } 
      } 
    } 
    if (affordableProjects.isEmpty()) {
      this.lastFailedOtherLocationSearch = System.currentTimeMillis();
      return false;
    } 
    BuildingProject newProject = BuildingProject.getRandomProject(affordableProjects);
    if (newProject.location == null || newProject.location.level < 0) {
      BuildingPlan plan = newProject.planSet.getRandomStartingPlan();
      BuildingLocation location = null;
      if (ignoreCost || canAffordBuild(plan))
        if (newProject.location == null && System.currentTimeMillis() - this.lastFailedOtherLocationSearch > 80000L) {
          location = plan.findBuildingLocation(this.winfo, this.regionMapper, this.location.pos, this.villageType.radius, MillCommonUtilities.getRandom(), -1);
        } else if (newProject.location != null) {
          location = newProject.location.createLocationForLevel(0);
        }  
      if (location != null) {
        this.lastFailedOtherLocationSearch = 0L;
        targetConstruction.startNewConstruction(location, plan.getBuildingPoints(this.world, location, false, false, false));
        if (MillConfigValues.LogBuildingPlan >= 1)
          MillLog.major(this, "New location non-project: Loaded " + (targetConstruction.getBblocks()).length + " building blocks for " + plan.planName); 
      } else {
        attemptedConstruction = true;
      } 
    } else {
      int level = newProject.location.level + 1;
      int variation = newProject.location.getVariation();
      BuildingLocation bl = newProject.location.createLocationForLevel(level);
      targetConstruction.startNewConstruction(bl, newProject.getPlan(variation, level).getBuildingPoints(this.world, bl, false, false, false));
      if (MillConfigValues.LogBuildingPlan >= 1)
        MillLog.major(this, "Upgrade non-project: Loaded " + (targetConstruction.getBblocks()).length + " building blocks for " + (newProject.getPlan(variation, level)).planName + " upgrade. Old level: " + newProject.location.level + " New level: " + level); 
    } 
    if (attemptedConstruction)
      this.lastFailedOtherLocationSearch = System.currentTimeMillis(); 
    return true;
  }
  
  private boolean findBuildingConstructionWall(boolean ignoreCost) {
    ConstructionIP targetConstruction = null;
    int nbWallConstructions = 0;
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.isWallConstruction()) {
        if (targetConstruction == null && cip.getBuildingLocation() == null)
          targetConstruction = cip; 
        nbWallConstructions++;
      } 
    } 
    if (targetConstruction == null && nbWallConstructions < getSimultaneousWallConstructionSlots()) {
      targetConstruction = new ConstructionIP(this, getConstructionsInProgress().size(), true);
      getConstructionsInProgress().add(targetConstruction);
    } 
    if (targetConstruction == null)
      return false; 
    if (!this.buildingProjects.containsKey(BuildingProject.EnumProjects.WALLBUILDING))
      return false; 
    List<BuildingProject> wallProjects = this.buildingProjects.get(BuildingProject.EnumProjects.WALLBUILDING);
    for (BuildingProject project : wallProjects) {
      if (project.planSet != null && findConstructionIPforLocation(project.location) == null)
        if (project.location.level < 0 && (ignoreCost || canAffordBuild(project.location.getPlan()))) {
          BuildingPlan plan = project.location.getPlan();
          if (isValidProject(project)) {
            BuildingLocation location = project.location.createLocationForLevel(0);
            targetConstruction.startNewConstruction(location, plan.getBuildingPoints(this.world, location, false, false, false));
          } 
        } else {
          int level = project.location.level + 1;
          int variation = project.location.getVariation();
          if (level < project.getLevelsNumber(variation) && 
            isValidUpgrade(project) && project.location.upgradesAllowed && (ignoreCost || canAffordBuild(project.getPlan(variation, level)))) {
            BuildingLocation bl = project.location.createLocationForLevel(level);
            targetConstruction.startNewConstruction(bl, project.getPlan(variation, level).getBuildingPoints(this.world, bl, false, false, false));
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, " Wall upgrade non-project: Loaded " + (targetConstruction.getBblocks()).length + " building blocks for " + (project.getPlan(variation, level)).planName + " upgrade. Old level: " + project.location.level + " New level: " + level); 
          } 
        }  
      if (targetConstruction.getBuildingLocation() != null)
        return true; 
    } 
    return false;
  }
  
  private boolean findBuildingProject() {
    if (this.buildingGoal != null && this.buildingGoal.length() > 0)
      return false; 
    if (this.noProjectsLeft && (this.world.getWorldTime() + hashCode()) % 600L != 3L)
      return false; 
    this.buildingGoal = null;
    this.buildingGoalLocation = null;
    if (MillConfigValues.LogBuildingPlan >= 2)
      MillLog.minor(this, "Searching for new building goal"); 
    List<BuildingProject> possibleProjects = getAllPossibleProjects();
    if (possibleProjects.size() == 0) {
      this.noProjectsLeft = true;
      return false;
    } 
    this.noProjectsLeft = false;
    BuildingProject project = BuildingProject.getRandomProject(possibleProjects);
    BuildingPlan plan = project.getNextBuildingPlan(true);
    this.buildingGoal = project.key;
    this.buildingGoalLevel = plan.level;
    this.buildingGoalVariation = plan.variation;
    if (project.location == null) {
      this.buildingGoalLocation = null;
      ConstructionIP cip = findConstructionIPforBuildingPlanKey(this.buildingGoal, true);
      if (cip != null)
        this.buildingGoalLocation = cip.getBuildingLocation(); 
    } else {
      this.buildingGoalLocation = project.location.createLocationForLevel(this.buildingGoalLevel);
    } 
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(this, "Picked new upgrade goal: " + this.buildingGoal + " level: " + this.buildingGoalLevel + " buildingGoalLocation: " + this.buildingGoalLocation); 
    return true;
  }
  
  public ConstructionIP findConstructionIPforBuildingPlanKey(String key, boolean newBuildingOnly) {
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip != null && cip.getBuildingLocation() != null && (cip.getBuildingLocation().getPlan()).buildingKey.equals(key) && (
        !newBuildingOnly || (cip.getBuildingLocation()).level == 0))
        return cip; 
    } 
    return null;
  }
  
  public ConstructionIP findConstructionIPforLocation(BuildingLocation bl) {
    if (bl == null)
      return null; 
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip != null && bl.isSameLocation(cip.getBuildingLocation()))
        return cip; 
    } 
    return null;
  }
  
  public void findName(String pname) {
    if (pname != null) {
      this.name = pname;
    } else {
      if (this.villageType.nameList == null) {
        this.name = null;
        return;
      } 
      this.name = this.culture.getRandomNameFromList(this.villageType.nameList);
    } 
    List<String> qualifiers = new ArrayList<>();
    for (String s : this.villageType.qualifiers)
      qualifiers.add(s); 
    if (this.villageType.hillQualifier != null && this.pos.getiY() > 75 && this.pos.getiY() < 85) {
      qualifiers.add(this.villageType.hillQualifier);
    } else if (this.villageType.mountainQualifier != null && this.pos.getiY() >= 85) {
      qualifiers.add(this.villageType.mountainQualifier);
    } 
    if (this.villageType.desertQualifier != null || this.villageType.forestQualifier != null || this.villageType.lavaQualifier != null || this.villageType.lakeQualifier != null || this.villageType.oceanQualifier != null) {
      int cactus = 0, wood = 0, lake = 0, ocean = 0, lava = 0;
      for (int i = -50; i < 50; i++) {
        for (int j = -10; j < 20; j++) {
          for (int k = -50; k < 50; k++) {
            Block block = WorldUtilities.getBlock(this.world, i + this.pos.getiX(), j + this.pos.getiY(), k + this.pos.getiZ());
            if (block == Blocks.CACTUS) {
              cactus++;
            } else if (block == Blocks.LOG || block == Blocks.LOG2) {
              wood++;
            } else if (block == Blocks.LAVA) {
              lava++;
            } else if (block == Blocks.WATER && 
              WorldUtilities.getBlock(this.world, i + this.pos.getiX(), j + this.pos.getiY() + 1, k + this.pos.getiZ()) == Blocks.AIR) {
              if (j + this.pos.getiY() < 65) {
                ocean++;
              } else {
                lake++;
              } 
            } 
          } 
        } 
      } 
      if (this.villageType.desertQualifier != null && cactus > 0)
        qualifiers.add(this.villageType.desertQualifier); 
      if (this.villageType.forestQualifier != null && wood > 40)
        qualifiers.add(this.villageType.forestQualifier); 
      if (this.villageType.lavaQualifier != null && lava > 0)
        qualifiers.add(this.villageType.lavaQualifier); 
      if (this.villageType.lakeQualifier != null && lake > 0)
        qualifiers.add(this.villageType.lakeQualifier); 
      if (this.villageType.oceanQualifier != null && ocean > 0)
        qualifiers.add(this.villageType.oceanQualifier); 
    } 
    if (qualifiers.size() > 0) {
      this.qualifier = qualifiers.get(MillCommonUtilities.randomInt(qualifiers.size()));
    } else {
      this.qualifier = "";
    } 
  }
  
  public void generateBannerPattern() {
    if (!this.villageType.banner_JSONs.isEmpty()) {
      String bannerJSON = ((String)this.villageType.banner_JSONs.get(MillCommonUtilities.randomInt(this.villageType.banner_JSONs.size()))).replace("blockentitytag", "BlockEntityTag").replace("base", "Base").replace("pattern", "Pattern").replace("color", "Color");
      this.bannerStack = new ItemStack(Items.BANNER, 1);
      try {
        this.bannerStack.setTagCompound(JsonToNBT.getTagFromJson(bannerJSON));
        return;
      } catch (NBTException nbtException) {
        this.bannerStack = null;
        MillLog.error(this, "Bad banner JSON " + bannerJSON + ", using default banner settings");
      } 
    } 
    String baseColor = "black";
    if (!this.villageType.banner_baseColors.isEmpty())
      baseColor = this.villageType.banner_baseColors.get(MillCommonUtilities.randomInt(this.villageType.banner_baseColors.size())); 
    EnumDyeColor baseDyeColor = EnumDyeColor.BLACK;
    for (EnumDyeColor dyeColor : EnumDyeColor.values()) {
      if (dyeColor.getName().equals(baseColor))
        baseDyeColor = dyeColor; 
    } 
    NBTTagList patternList = new NBTTagList();
    if (!this.villageType.banner_patternsColors.isEmpty() && !this.villageType.banner_Patterns.isEmpty()) {
      String patternColor = this.villageType.banner_patternsColors.get(MillCommonUtilities.randomInt(this.villageType.banner_patternsColors.size()));
      int patternColorDamage = 0;
      for (EnumDyeColor dyeColor : EnumDyeColor.values()) {
        if (dyeColor.getName().equals(patternColor)) {
          patternColorDamage = dyeColor.getDyeDamage();
          break;
        } 
      } 
      String patterns = this.villageType.banner_Patterns.get(MillCommonUtilities.randomInt(this.villageType.banner_Patterns.size()));
      for (String pattern : patterns.split(",")) {
        NBTTagCompound patternNBT = new NBTTagCompound();
        patternNBT.setString("Pattern", pattern);
        patternNBT.setInteger("Color", patternColorDamage);
        patternList.appendTag((NBTBase)patternNBT);
      } 
    } 
    if (!this.villageType.banner_chargeColors.isEmpty() && !this.villageType.banner_chargePatterns.isEmpty()) {
      String chargeColor = this.villageType.banner_chargeColors.get(MillCommonUtilities.randomInt(this.villageType.banner_chargeColors.size()));
      int chargeColorDamage = 0;
      for (EnumDyeColor dyeColor : EnumDyeColor.values()) {
        if (dyeColor.getName().equals(chargeColor)) {
          chargeColorDamage = dyeColor.getDyeDamage();
          break;
        } 
      } 
      String chargePatterns = this.villageType.banner_chargePatterns.get(MillCommonUtilities.randomInt(this.villageType.banner_chargePatterns.size()));
      for (String chargePattern : chargePatterns.split(",")) {
        NBTTagCompound chargeNBT = new NBTTagCompound();
        chargeNBT.setString("Pattern", chargePattern);
        chargeNBT.setInteger("Color", chargeColorDamage);
        patternList.appendTag((NBTBase)chargeNBT);
      } 
    } 
    this.bannerStack = ItemBanner.makeBanner(baseDyeColor, patternList);
  }
  
  public Set<String> getAllFamilyNames() {
    Set<String> names = new HashSet<>();
    for (VillagerRecord vr : this.vrecords.values())
      names.add(vr.familyName); 
    return names;
  }
  
  private List<BuildingProject> getAllPossibleProjects() {
    List<BuildingProject> possibleProjects = new ArrayList<>();
    boolean foundNewBuildingsLevel = false;
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep) && ep != BuildingProject.EnumProjects.WALLBUILDING) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        boolean includedNewBuildings = false;
        for (BuildingProject project : projectsLevel) {
          project.projectTier = ep;
          if ((project.location == null || project.location.level < 0) && !foundNewBuildingsLevel) {
            if (isValidProject(project)) {
              possibleProjects.add(project);
              includedNewBuildings = true;
              if (MillConfigValues.LogBuildingPlan >= 3)
                MillLog.debug(this, "Found a new building to add: " + project); 
              if (MillConfigValues.LogBuildingPlan >= 2 && project.getChoiceWeight(null) < 1)
                MillLog.minor(this, "Project has null or negative weight: " + project + ": " + project.getChoiceWeight(null)); 
            } 
            continue;
          } 
          if (project.location != null && isValidUpgrade(project) && project.location.level >= 0 && project.location.level < project
            .getLevelsNumber(project.location.getVariation()) && project.location.upgradesAllowed && project.getChoiceWeight(null) > 0)
            possibleProjects.add(project); 
        } 
        if (includedNewBuildings)
          foundNewBuildingsLevel = true; 
      } 
    } 
    return possibleProjects;
  }
  
  public Collection<VillagerRecord> getAllVillagerRecords() {
    return getVillagerRecords().values();
  }
  
  public int getAltitude(int x, int z) {
    if (this.winfo == null)
      return -1; 
    if (x < this.winfo.mapStartX || x >= this.winfo.mapStartX + this.winfo.length || z < this.winfo.mapStartZ || z >= this.winfo.mapStartZ + this.winfo.width)
      return -1; 
    return this.winfo.topGround[x - this.winfo.mapStartX][z - this.winfo.mapStartZ];
  }
  
  public ItemStack getBannerStack() {
    return this.bannerStack;
  }
  
  public Building getBuildingAtCoordPlanar(Point p) {
    for (Building b : getBuildings()) {
      if (b.location.isInsidePlanar(p))
        return b; 
    } 
    return null;
  }
  
  public Building getBuildingFromLocation(BuildingLocation location) {
    for (Point p : this.buildings) {
      Building building = this.mw.getBuilding(p);
      if (building != null && 
        building.location.isSameLocation(location))
        return building; 
    } 
    return null;
  }
  
  public BuildingPlan getBuildingPlanForConstruction(ConstructionIP cip) {
    if (cip.getBuildingLocation() == null) {
      MillLog.error(this, "Couldn't find project for construction with no location: " + cip);
      return null;
    } 
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        for (BuildingProject project : projectsLevel) {
          if ((cip.getBuildingLocation()).level == 0 && (project.location == null || project.location.level < 0) && project.key.equals((cip.getBuildingLocation()).planKey)) {
            if (MillConfigValues.LogBuildingPlan >= 3)
              MillLog.debug(this, "Returning building plan for " + cip
                  .getBuildingLocation() + ": " + project.getPlan(cip.getBuildingLocation().getVariation(), (cip.getBuildingLocation()).level)); 
            return project.getPlan(cip.getBuildingLocation().getVariation(), (cip.getBuildingLocation()).level);
          } 
          if (cip.getBuildingLocation().isSameLocation(project.location)) {
            if (MillConfigValues.LogBuildingPlan >= 3)
              MillLog.debug(this, "Returning building plan for " + cip
                  .getBuildingLocation() + ": " + project.getPlan(cip.getBuildingLocation().getVariation(), (cip.getBuildingLocation()).level)); 
            return project.getPlan(cip.getBuildingLocation().getVariation(), (cip.getBuildingLocation()).level);
          } 
        } 
      } 
    } 
    MillLog.error(this, "Could not find project for current building location: " + cip.getBuildingLocation());
    return null;
  }
  
  public List<Building> getBuildings() {
    List<Building> vbuildings = new ArrayList<>();
    for (Point p : this.buildings) {
      Building building = this.mw.getBuilding(p);
      if (building != null && building.location != null)
        vbuildings.add(building); 
    } 
    return vbuildings;
  }
  
  public List<Building> getBuildingsWithTag(String s) {
    List<Building> matches = new ArrayList<>();
    for (Point p : this.buildings) {
      Building building = this.mw.getBuilding(p);
      if (building != null && building.location != null && building.getTags() != null && building.containsTags(s))
        matches.add(building); 
    } 
    return matches;
  }
  
  public Set<TradeGood> getBuyingGoods(EntityPlayer player) {
    if (!this.shopBuys.containsKey(player.getName()))
      return null; 
    return this.shopBuys.get(player.getName()).keySet();
  }
  
  public int getBuyingPrice(TradeGood g, EntityPlayer player) {
    if (!this.shopBuys.containsKey(player.getName()) || this.shopBuys.get(player.getName()) == null)
      return 0; 
    return this.shopBuys.get(player.getName()).get(g).intValue();
  }
  
  public ConstructionIP getConstructionIPforBuilder(MillVillager builder) {
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuilder() == builder)
        return cip; 
    } 
    return null;
  }
  
  public List<ConstructionIP> getConstructionsInProgress() {
    return this.constructionsIP;
  }
  
  public Point getCurrentClearPathPoint() {
    if (this.oldPathPointsToClear == null)
      return null; 
    if (this.oldPathPointsToClearIndex >= this.oldPathPointsToClear.size()) {
      this.oldPathPointsToClear = null;
      return null;
    } 
    return this.oldPathPointsToClear.get(this.oldPathPointsToClearIndex);
  }
  
  public BuildingPlan getCurrentGoalBuildingPlan() {
    if (this.buildingGoal == null)
      return null; 
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        for (BuildingProject project : projectsLevel) {
          if (project.key.equals(this.buildingGoal)) {
            if (this.buildingGoalLocation == null)
              return project.getPlan(this.buildingGoalVariation, 0); 
            return project.getPlan(this.buildingGoalVariation, this.buildingGoalLocation.level);
          } 
        } 
      } 
    } 
    return null;
  }
  
  public BuildingBlock getCurrentPathBuildingBlock() {
    if (this.pathsToBuild == null)
      return null; 
    while (true) {
      if (this.pathsToBuildIndex >= this.pathsToBuild.size()) {
        this.pathsToBuild = null;
        return null;
      } 
      if (this.pathsToBuildPathIndex >= this.pathsToBuild.get(this.pathsToBuildIndex).size()) {
        this.pathsToBuildIndex++;
        this.pathsToBuildPathIndex = 0;
        continue;
      } 
      BuildingBlock b = this.pathsToBuild.get(this.pathsToBuildIndex).get(this.pathsToBuildPathIndex);
      IBlockState blockState = b.p.getBlockActualState(this.world);
      if (PathUtilities.canPathBeBuiltHere(blockState) && blockState != b.getBlockstate())
        return b; 
      this.pathsToBuildPathIndex++;
    } 
  }
  
  public Building getFirstBuildingWithTag(String s) {
    for (Point p : this.buildings) {
      Building building = this.mw.getBuilding(p);
      if (building != null && building.location != null && building.getTags() != null && building.containsTags(s))
        return building; 
    } 
    return null;
  }
  
  public List<BuildingProject> getFlatProjectList() {
    List<BuildingProject> projects = new ArrayList<>();
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        for (BuildingProject project : projectsLevel)
          projects.add(project); 
      } 
    } 
    return projects;
  }
  
  public String getGameBuildingName() {
    return this.location.getGameName();
  }
  
  public ItemStack getIcon() {
    BuildingPlanSet planSet = this.culture.getBuildingPlanSet(this.location.planKey);
    if (planSet != null) {
      BuildingPlan plan = planSet.getPlan(this.location.getVariation(), this.location.level);
      if (plan != null)
        return plan.getIcon(); 
    } 
    return ItemStack.EMPTY;
  }
  
  public HashMap<TradeGood, Integer> getImportsNeededbyOtherVillages() {
    if (this.neededGoodsCached != null && System.currentTimeMillis() < this.neededGoodsLastGenerated + 60000L)
      return this.neededGoodsCached; 
    this.neededGoodsCached = new HashMap<>();
    for (Point vp : this.mw.villagesList.pos) {
      Chunk chunk = this.world.getChunkFromBlockCoords(new BlockPos(vp.getiX(), 0, vp.getiZ()));
      if (chunk.isLoaded()) {
        Building townHall = this.mw.getBuilding(vp);
        if (townHall != null && getTownHall() != null && townHall.villageType != (getTownHall()).villageType && townHall.culture == (getTownHall()).culture && 
          townHall.getBuildingsWithTag("inn").size() > 0)
          townHall.getNeededImportGoods(this.neededGoodsCached); 
      } 
    } 
    this.neededGoodsLastGenerated = System.currentTimeMillis();
    return this.neededGoodsCached;
  }
  
  private int getInventoryCountFromCache(InvItem invItem) {
    if (this.inventoryCache == null)
      calculateInventoryCache(); 
    if (invItem.item == Item.getItemFromBlock(Blocks.LOG) && invItem.meta == -1) {
      int count = 0;
      for (int meta = 0; meta < 15; meta++) {
        InvItem invItemAdjusted = InvItem.createInvItem(invItem.item, meta);
        count += getInventoryCountFromCache(invItemAdjusted);
      } 
      Item itemLog2 = Item.getItemFromBlock(Blocks.LOG2);
      for (int i = 0; i < 15; i++) {
        InvItem invItemAdjusted = InvItem.createInvItem(itemLog2, i);
        count += getInventoryCountFromCache(invItemAdjusted);
      } 
      return count;
    } 
    if (invItem.meta == -1) {
      int count = 0;
      for (int meta = 0; meta < 15; meta++) {
        InvItem invItemAdjusted = InvItem.createInvItem(invItem.item, meta);
        count += getInventoryCountFromCache(invItemAdjusted);
      } 
      return count;
    } 
    if (this.inventoryCache.containsKey(invItem))
      return this.inventoryCache.get(invItem).intValue(); 
    return 0;
  }
  
  public Set<MillVillager> getKnownVillagers() {
    return this.villagers;
  }
  
  public Set<Point> getKnownVillages() {
    return this.relations.keySet();
  }
  
  public BuildingLocation getLocationAtCoord(Point p) {
    return getLocationAtCoordWithTolerance(p, 0);
  }
  
  public BuildingLocation getLocationAtCoordPlanar(Point p) {
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuildingLocation() != null && cip.getBuildingLocation().isInsidePlanar(p))
        return cip.getBuildingLocation(); 
    } 
    for (BuildingLocation bl : getLocations()) {
      if (bl.isInsidePlanar(p))
        return bl; 
    } 
    return null;
  }
  
  public BuildingLocation getLocationAtCoordWithTolerance(Point p, int tolerance) {
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuildingLocation() != null && cip.getBuildingLocation().isInsideWithTolerance(p, tolerance))
        return cip.getBuildingLocation(); 
    } 
    for (BuildingLocation bl : getLocations()) {
      if (!bl.isSubBuildingLocation && bl.isInsideWithTolerance(p, tolerance))
        return bl; 
    } 
    return null;
  }
  
  public List<BuildingLocation> getLocations() {
    List<BuildingLocation> locations = new ArrayList<>();
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        for (BuildingProject project : projectsLevel) {
          if (project.location != null)
            locations.add(project.location); 
        } 
      } 
    } 
    return locations;
  }
  
  public MarvelManager getMarvelManager() {
    return this.marvelManager;
  }
  
  public String getNativeBuildingName() {
    return this.location.getNativeName();
  }
  
  public int getNbProjects() {
    int nb = 0;
    for (List<BuildingProject> projects : this.buildingProjects.values())
      nb += projects.size(); 
    return nb;
  }
  
  public void getNeededImportGoods(HashMap<TradeGood, Integer> neededGoods) {
    for (TradeGood good : this.culture.goodsList) {
      int nbneeded = nbGoodNeeded(good.item.getItem(), good.item.meta);
      if (nbneeded > 0) {
        if (MillConfigValues.LogMerchant >= 3)
          MillLog.debug(this, "Import needed: " + good.getName() + " - " + nbneeded); 
        if (neededGoods.containsKey(good)) {
          neededGoods.put(good, Integer.valueOf(((Integer)neededGoods.get(good)).intValue() + nbneeded));
          continue;
        } 
        neededGoods.put(good, Integer.valueOf(nbneeded));
      } 
    } 
  }
  
  public int getNewGender() {
    int nbmales = 0, nbfemales = 0;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.gender == 1) {
        nbmales++;
        continue;
      } 
      nbfemales++;
    } 
    int maleChance = 3 + nbfemales - nbmales;
    return (MillCommonUtilities.randomInt(6) < maleChance) ? 1 : 2;
  }
  
  public PanelManager getPanelManager() {
    if (this.panelManager == null)
      this.panelManager = new PanelManager(this); 
    return this.panelManager;
  }
  
  public Building getParentBuilding() {
    if (!this.location.isSubBuildingLocation)
      return this; 
    Optional<BuildingLocation> parentLocation = getTownHall().getLocations().stream().filter(locationTested -> (!locationTested.isSubBuildingLocation && locationTested.pos.equals(this.location.pos))).findFirst();
    if (parentLocation.isPresent())
      return ((BuildingLocation)parentLocation.get()).getBuilding(this.world); 
    MillLog.error(this, "Can't find parent building. Returning itself instead.");
    return this;
  }
  
  public Point getPos() {
    return this.pos;
  }
  
  public String getQualifier() {
    return this.qualifier;
  }
  
  public Map<Point, Integer> getRelations() {
    return this.relations;
  }
  
  public int getRelationWithVillage(Point p) {
    if (this.relations.containsKey(p))
      return ((Integer)this.relations.get(p)).intValue(); 
    return 0;
  }
  
  public int getReputation(EntityPlayer player) {
    return this.mw.getProfile(player).getReputation(this);
  }
  
  public String getReputationLevelDesc(EntityPlayer player) {
    return this.culture.getReputationLevelDesc(getReputation(player));
  }
  
  public String getReputationLevelLabel(EntityPlayer player) {
    return this.culture.getReputationLevelLabel(getReputation(player));
  }
  
  public ResManager getResManager() {
    return this.resManager;
  }
  
  public Set<TradeGood> getSellingGoods(EntityPlayer player) {
    if (!this.shopSells.containsKey(player.getName())) {
      MillLog.error(this, "No selling data from player " + player.getName() + ", only has data for " + this.shopSells.keySet().toArray().toString());
      return null;
    } 
    return this.shopSells.get(player.getName()).keySet();
  }
  
  public int getSellingPrice(TradeGood g, EntityPlayer player) {
    if (player == null || !this.shopSells.containsKey(player.getName()))
      return 0; 
    return this.shopSells.get(player.getName()).get(g).intValue();
  }
  
  public List<Building> getShops() {
    List<Building> shops = new ArrayList<>();
    for (Point p : this.buildings) {
      Building building = this.mw.getBuilding(p);
      if (building != null && building.location != null && building.location.shop != null && building.location.shop.length() > 0)
        shops.add(building); 
    } 
    return shops;
  }
  
  private int getSimultaneousConstructionSlots() {
    if (this.villageType == null)
      return 1; 
    int nb = this.villageType.maxSimultaneousConstructions;
    for (BuildingLocation bl : getLocations()) {
      if (bl.getPlan() != null)
        nb += (bl.getPlan()).extraSimultaneousConstructions; 
    } 
    return nb;
  }
  
  private int getSimultaneousWallConstructionSlots() {
    if (this.villageType == null)
      return 1; 
    int nb = this.villageType.maxSimultaneousWallConstructions;
    for (BuildingLocation bl : getLocations()) {
      if (bl.getPlan() != null)
        nb += (bl.getPlan()).extraSimultaneousWallConstructions; 
    } 
    return nb;
  }
  
  public Set<String> getTags() {
    return (Set<String>)this.tags.stream().sorted().collect(Collectors.toCollection(java.util.TreeSet::new));
  }
  
  public Building getTownHall() {
    if (getTownHallPos() == null)
      return null; 
    return this.mw.getBuilding(getTownHallPos());
  }
  
  public Point getTownHallPos() {
    return this.townHallPos;
  }
  
  public int getVillageAttackerStrength() {
    int strength = 0;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.raidingVillage && !vr.killed)
        strength += vr.getMilitaryStrength(); 
    } 
    return strength;
  }
  
  public int getVillageDefendingStrength() {
    int strength = 0;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.getType() != null && (vr.getType()).helpInAttacks && !vr.killed && !vr.raidingVillage)
        strength += vr.getMilitaryStrength(); 
    } 
    return strength;
  }
  
  public int getVillageIrrigation() {
    int irrigation = 0;
    for (BuildingLocation bl : getLocations()) {
      if (bl.getPlan() != null)
        irrigation += (bl.getPlan()).irrigation; 
    } 
    return irrigation;
  }
  
  public String getVillageNameWithoutQualifier() {
    if (this.name == null || this.name.length() == 0) {
      if (this.villageType != null)
        return this.villageType.name; 
      return getNativeBuildingName();
    } 
    return this.name;
  }
  
  public String getVillageQualifiedName() {
    if (this.name == null || this.name.length() == 0) {
      if (this.villageType != null)
        return this.villageType.name; 
      return getNativeBuildingName();
    } 
    if (getQualifier() == null || getQualifier().length() == 0)
      return this.name; 
    return this.name + this.culture.qualifierSeparator.replaceAll("_", " ") + getQualifier();
  }
  
  public int getVillageRaidingStrength() {
    int strength = 0;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.getType() != null && (vr.getType()).isRaider && !vr.killed && !vr.raidingVillage)
        strength += vr.getMilitaryStrength(); 
    } 
    return strength;
  }
  
  public VillagerRecord getVillagerRecordById(long id) {
    return getVillagerRecords().get(Long.valueOf(id));
  }
  
  public Map<Long, VillagerRecord> getVillagerRecords() {
    return this.vrecords;
  }
  
  public VisitorManager getVisitorManager() {
    if (this.visitorManager == null)
      this.visitorManager = new VisitorManager(this); 
    return this.visitorManager;
  }
  
  public int getWoodCount() {
    if (!containsTags("grove"))
      return 0; 
    int nb = 0;
    for (int i = this.location.minx - 3; i < this.location.maxx + 3; i++) {
      for (int j = this.location.pos.getiY() - 1; j < this.location.pos.getiY() + 10; j++) {
        for (int k = this.location.minz - 3; k < this.location.maxz + 3; k++) {
          if (WorldUtilities.getBlock(this.world, i, j, k) == Blocks.LOG || WorldUtilities.getBlock(this.world, i, j, k) == Blocks.LOG2)
            nb++; 
        } 
      } 
    } 
    return nb;
  }
  
  public Point getWoodLocation() {
    if (!containsTags("grove"))
      return null; 
    for (int xPos = this.location.minx - 3; xPos < this.location.maxx + 3; xPos++) {
      for (int yPos = this.location.miny - 1; yPos < this.location.maxy + 20; yPos++) {
        for (int zPos = this.location.minz - 3; zPos < this.location.maxz + 3; zPos++) {
          Block block = WorldUtilities.getBlock(this.world, xPos, yPos, zPos);
          if (block == Blocks.LOG || block == Blocks.LOG2) {
            Point p = new Point(xPos, yPos, zPos);
            return p;
          } 
        } 
      } 
    } 
    return null;
  }
  
  public void growTree(World world, int x, int y, int z, Random random) {
    BlockPos bp = new BlockPos(x, y, z);
    IBlockState saplingBlockState = WorldUtilities.getBlockState(world, x, y, z);
    if (saplingBlockState.getBlock() != Blocks.SAPLING)
      return; 
    BlockPlanks.EnumType saplingType = (BlockPlanks.EnumType)saplingBlockState.getValue((IProperty)BlockSapling.TYPE);
    if (saplingType == BlockPlanks.EnumType.DARK_OAK) {
      if (!MillCommonUtilities.chanceOn(5))
        return; 
      WorldGenerator treeGenerator = null;
      if (WorldUtilities.getBlockState(world, x + 1, y, z).getBlock() != Blocks.SAPLING || WorldUtilities.getBlockState(world, x, y, z + 1).getBlock() != Blocks.SAPLING || 
        WorldUtilities.getBlockState(world, x + 1, y, z + 1).getBlock() != Blocks.SAPLING)
        return; 
      WorldUtilities.setBlockAndMetadata(world, x, y, z, Blocks.AIR, 0, true, false);
      WorldUtilities.setBlockAndMetadata(world, x + 1, y, z, Blocks.AIR, 0, true, false);
      WorldUtilities.setBlockAndMetadata(world, x, y, z + 1, Blocks.AIR, 0, true, false);
      WorldUtilities.setBlockAndMetadata(world, x + 1, y, z + 1, Blocks.AIR, 0, true, false);
      WorldGenCanopyTree worldGenCanopyTree = new WorldGenCanopyTree(true);
      boolean success = worldGenCanopyTree.generate(world, random, bp);
      if (!success) {
        WorldUtilities.setBlockstate(world, new Point(x, y, z), saplingBlockState, true, false);
        WorldUtilities.setBlockstate(world, new Point((x + 1), y, z), saplingBlockState, true, false);
        WorldUtilities.setBlockstate(world, new Point(x, y, (z + 1)), saplingBlockState, true, false);
        WorldUtilities.setBlockstate(world, new Point((x + 1), y, (z + 1)), saplingBlockState, true, false);
      } 
    } else {
      WorldGenSavannaTree worldGenSavannaTree = null;
      WorldGenerator treeGenerator = null;
      if (saplingType == BlockPlanks.EnumType.OAK) {
        treeGenerator = new WorldGenTrees(true);
      } else if (saplingType == BlockPlanks.EnumType.SPRUCE) {
        treeGenerator = new WorldGenTaiga2(true);
      } else if (saplingType == BlockPlanks.EnumType.BIRCH) {
        treeGenerator = new WorldGenBirchTree(true, false);
      } else if (saplingType == BlockPlanks.EnumType.JUNGLE) {
        IBlockState iblockstate = Blocks.LOG.getDefaultState().withProperty((IProperty)BlockOldLog.VARIANT, (Comparable)BlockPlanks.EnumType.JUNGLE);
        IBlockState iblockstate1 = Blocks.LEAVES.getDefaultState().withProperty((IProperty)BlockOldLeaf.VARIANT, (Comparable)BlockPlanks.EnumType.JUNGLE).withProperty((IProperty)BlockLeaves.CHECK_DECAY, 
            Boolean.valueOf(false));
        treeGenerator = new WorldGenTrees(true, 4, iblockstate, iblockstate1, false);
      } else if (saplingType == BlockPlanks.EnumType.ACACIA) {
        worldGenSavannaTree = new WorldGenSavannaTree(true);
      } else {
        MillLog.error(this, "Tried forcing a sapling to grow but its type is not recognised: " + saplingType);
        return;
      } 
      if (worldGenSavannaTree != null) {
        WorldUtilities.setBlockAndMetadata(world, x, y, z, Blocks.AIR, 0, true, false);
        boolean success = worldGenSavannaTree.generate(world, random, bp);
        if (!success)
          WorldUtilities.setBlockstate(world, new Point(x, y, z), saplingBlockState, true, false); 
      } else if (treeGenerator != null) {
        WorldUtilities.setBlockAndMetadata(world, x, y, z, Blocks.AIR, 0, true, false);
        boolean success = treeGenerator.generate(world, random, bp);
        if (!success)
          WorldUtilities.setBlockstate(world, new Point(x, y, z), saplingBlockState, true, false);
      }
    } 
  }
  
  private void handlePathingResult() {
    if (this.pathQueue != null) {
      Collections.reverse(this.pathQueue.pathsReceived);
      Collections.reverse(this.pathQueue.pathCreators);
      this.pathsToBuild = new ArrayList<>();
      for (int i = 0; i < this.pathQueue.pathsReceived.size(); i++) {
        if (this.pathQueue.pathsReceived.get(i) != null)
          this.pathsToBuild.add(PathUtilities.buildPath(this, this.pathQueue.pathsReceived.get(i), (this.pathQueue.pathCreators.get(i)).pathConstructionGood.block, 
                (this.pathQueue.pathCreators.get(i)).pathConstructionGood.meta, (this.pathQueue.pathCreators.get(i)).pathWidth)); 
      } 
      this.pathsToBuildIndex = 0;
      this.pathsToBuildPathIndex = 0;
      calculatePathsToClear();
      this.pathsChanged = true;
      this.pathQueue = null;
    } 
  }
  
  public void initialise(EntityPlayer owner, boolean villageCreation) {
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(this, "Initialising building at " + getPos() + ", TH pos: " + getTownHallPos() + ", TH: " + getTownHall()); 
    if (isHouse()) {
      try {
        initialiseHouse(villageCreation);
      } catch (Exception e) {
        MillLog.printException("Error when trying to create a building: " + this.name, e);
      } 
      getPanelManager().updateSigns();
    } 
    if (this.isTownhall) {
      initialiseTownHall(owner);
    } else {
      this.chestLocked = (getTownHall()).chestLocked;
      if (!this.chestLocked)
        unlockChests(); 
    } 
    if (villageCreation && this.resManager.spawns.size() > 0)
      updatePens(true); 
  }
  
  public void initialiseBuildingProjects() {
    if (this.villageType == null) {
      MillLog.error(this, "villageType is null!");
      return;
    } 
    this.buildingProjects = this.villageType.getBuildingProjects();
  }
  
  public void initialiseConstruction(ConstructionIP cip, Point refPos) throws MillLog.MillenaireException {
    boolean isTownHall = false;
    if (cip.getBuildingLocation().equals(this.location))
      isTownHall = true; 
    if ((cip.getBuildingLocation()).level != 0) {
      MillLog.printException((Throwable)new MillLog.MillenaireException("Trying to call initialiseConstruction on a location with non-0 level: " + cip.getBuildingLocation()));
      return;
    } 
    Building building = new Building(this.mw, this.culture, this.villageType, cip.getBuildingLocation(), isTownHall, false, getPos());
    BuildingPlan plan = getBuildingPlanForConstruction(cip);
    plan.updateBuildingForPlan(building);
    building.initialise(null, false);
    registerBuildingEntity(building);
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(this, "Created new Building Entity: " + plan.planName + " at " + refPos); 
    completeConstruction(cip);
  }
  
  private void initialiseHouse(boolean villageCreation) throws MillLog.MillenaireException {
    if (villageCreation)
      createResidents(); 
  }
  
  public void initialiseRelations(Point parentVillage) {
    if (this.villageType.lonebuilding)
      return; 
    this.parentVillage = parentVillage;
    for (Point p : this.mw.villagesList.pos) {
      if (!this.pos.sameBlock(p) && this.pos.distanceToSquared(p) < (MillConfigValues.BackgroundRadius * MillConfigValues.BackgroundRadius)) {
        Building distantVillage = this.mw.getBuilding(p);
        if (distantVillage != null) {
          if (parentVillage != null && (p.sameBlock(parentVillage) || parentVillage.sameBlock(distantVillage.parentVillage))) {
            adjustRelation(p, 100, true);
            continue;
          } 
          if (this.villageType.playerControlled && this.controlledBy.equals(distantVillage.controlledBy)) {
            adjustRelation(p, 100, true);
            continue;
          } 
          if (distantVillage.culture == this.culture) {
            adjustRelation(p, 50, true);
            continue;
          } 
          adjustRelation(p, -30, true);
        } 
      } 
    } 
  }
  
  public void initialiseTownHall(EntityPlayer controller) {
    if (this.name == null)
      findName(null); 
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.major(this, "Initialising town hall: " + getVillageQualifiedName()); 
    this.buildings.add(getPos());
    if (this.villageType.playerControlled && controller != null) {
      UserProfile profile = this.mw.getProfile(controller);
      this.controlledBy = profile.uuid;
      profile.adjustReputation(this, 131072);
    } 
  }
  
  public void initialiseVillage() {
    boolean noMenLeft = true;
    for (VillagerRecord vr : getVillagerRecords().values()) {
      if (vr.gender == 1 && !(vr.getType()).isChild)
        noMenLeft = false; 
    } 
    for (Point p : this.buildings) {
      Building b = this.mw.getBuilding(p);
      if (b != null) {
        if (noMenLeft) {
          b.unlockChests();
          continue;
        } 
        b.lockChests();
      } 
    } 
    choseAndApplyBrickTheme();
    recalculatePaths(true);
  }
  
  public void invalidateInventoryCache() {
    this.inventoryCache = null;
  }
  
  public boolean isDisplayableProject(BuildingProject project) {
    if ((project.getPlan(0, 0)).requiredGlobalTag != null) {
      if (!this.mw.isGlobalTagSet((project.getPlan(0, 0)).requiredGlobalTag))
        return false; 
    } else if ((project.getPlan(0, 0)).isgift && !MillConfigValues.bonusEnabled) {
      return false;
    } 
    return true;
  }
  
  public boolean isHouse() {
    return (this.location != null && (this.location.getMaleResidents().size() > 0 || this.location.getFemaleResidents().size() > 0));
  }
  
  public boolean isPointProtectedFromPathBuilding(Point p) {
    Point above = p.getAbove(), below = p.getBelow();
    for (Building b : getBuildings()) {
      if (b.location != null && b.location.isInsidePlanar(p)) {
        if (b.containsTags("nopaths"))
          return true; 
        if (b.resManager.soils != null)
          for (List<Point> vpoints : b.resManager.soils) {
            if (vpoints.contains(p) || vpoints.contains(above) || vpoints.contains(below))
              return true; 
          }  
        if (b.resManager.sources != null)
          for (List<Point> vpoints : b.resManager.sources) {
            if (vpoints.contains(p) || vpoints.contains(above) || vpoints.contains(below))
              return true; 
          }  
      } 
    } 
    return false;
  }
  
  public boolean isReachableFromRegion(short regionId) {
    if ((getTownHall()).regionMapper == null)
      return true; 
    if ((getTownHall()).regionMapper.regions[this.resManager.getSleepingPos().getiX() - (getTownHall()).winfo.mapStartX][this.resManager.getSleepingPos().getiZ() - (getTownHall()).winfo.mapStartZ] != regionId)
      return false; 
    if ((getTownHall()).regionMapper.regions[this.resManager.getSellingPos().getiX() - (getTownHall()).winfo.mapStartX][this.resManager.getSellingPos().getiZ() - (getTownHall()).winfo.mapStartZ] != regionId)
      return false; 
    if ((getTownHall()).regionMapper.regions[this.resManager.getDefendingPos().getiX() - (getTownHall()).winfo.mapStartX][this.resManager.getDefendingPos().getiZ() - 
        (getTownHall()).winfo.mapStartZ] != regionId)
      return false; 
    if ((getTownHall()).regionMapper.regions[this.resManager.getShelterPos().getiX() - (getTownHall()).winfo.mapStartX][this.resManager.getShelterPos().getiZ() - (getTownHall()).winfo.mapStartZ] != regionId)
      return false; 
    return true;
  }
  
  public boolean isValidProject(BuildingProject project) {
    BuildingPlan plan = project.getNextBuildingPlan(false);
    if (plan == null) {
      MillLog.error(this, "Building project " + project + " has no building plan.");
      return false;
    } 
    if (!this.villageType.playerControlled && (plan.price > 0 || plan.isgift) && !this.buildingsBought.contains(project.key))
      return false; 
    return checkProjectValidity(project, plan);
  }
  
  public boolean isValidUpgrade(BuildingProject project) {
    if (project.location == null)
      return false; 
    if (project.getPlan(project.location.getVariation(), project.location.level + 1) == null)
      return false; 
    if ((project.getPlan(project.location.getVariation(), project.location.level + 1)).version != project.location.version)
      return false; 
    return checkProjectValidity(project, project.getPlan(project.location.getVariation(), project.location.level + 1));
  }
  
  private boolean isVillageChunksLoaded() {
    if (this.world.isRemote) {
      MillLog.printException("Trying to check chunk status client side", new Exception());
      return false;
    } 
    ChunkProviderServer chunkProvider = ((WorldServer)this.world).getChunkProvider();
    for (int x = this.winfo.mapStartX; x < this.winfo.mapStartX + this.winfo.width; x += 16) {
      for (int z = this.winfo.mapStartZ; z < this.winfo.mapStartZ + this.winfo.length; z += 16) {
        if (!chunkProvider.chunkExists(x >> 4, z >> 4))
          return false; 
      } 
    } 
    return true;
  }
  
  private void killMobs() {
    if (this.winfo == null)
      return; 
    Point start = new Point(this.location.pos.x - this.villageType.radius, (this.location.pos.getiY() - 20), this.location.pos.z - this.villageType.radius);
    Point end = new Point(this.location.pos.x + this.villageType.radius, (this.location.pos.getiY() + 50), this.location.pos.z + this.villageType.radius);
    if (containsTags("despawnallmobs")) {
      List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(this.world, EntityMob.class, start, end);
      for (Entity ent : mobs) {
        if (!ent.isDead) {
          if (MillConfigValues.LogTileEntityBuilding >= 3)
            MillLog.debug(this, "Killing mob " + ent + " at " + ent.posX + "/" + ent.posY + "/" + ent.posZ); 
          ent.setDead();
        } 
      } 
    } else {
      List<Entity> creepers = WorldUtilities.getEntitiesWithinAABB(this.world, EntityCreeper.class, start, end);
      for (Entity ent : creepers) {
        if (!ent.isDead) {
          if (MillConfigValues.LogTileEntityBuilding >= 3)
            MillLog.debug(this, "Killing creeper " + ent + " at " + ent.posX + "/" + ent.posY + "/" + ent.posZ); 
          ent.setDead();
        } 
      } 
      List<Entity> endermen = WorldUtilities.getEntitiesWithinAABB(this.world, EntityEnderman.class, start, end);
      for (Entity ent : endermen) {
        if (!ent.isDead) {
          if (MillConfigValues.LogTileEntityBuilding >= 3)
            MillLog.debug(this, "Killing enderman " + ent + " at " + ent.posX + "/" + ent.posY + "/" + ent.posZ); 
          ent.setDead();
        } 
      } 
    } 
  }
  
  private void loadChunks() {
    if (this.winfo != null && this.winfo.width > 0) {
      if (this.chunkLoader == null)
        this.chunkLoader = new BuildingChunkLoader(this); 
      if (!this.chunkLoader.chunksLoaded)
        this.chunkLoader.loadChunks(); 
    } 
  }
  
  public void lockAllBuildingsChests() {
    for (Point p : this.buildings) {
      Building b = this.mw.getBuilding(p);
      if (b != null)
        b.lockChests(); 
    } 
    this.saveNeeded = true;
    this.saveReason = "Locking chests";
  }
  
  public void lockChests() {
    this.chestLocked = true;
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      if (chest != null)
        chest.buildingPos = getPos(); 
    } 
  }
  
  public boolean lockedForPlayer(EntityPlayer player) {
    if (!this.chestLocked)
      return false; 
    return !controlledBy(player);
  }
  
  private void merchantCreated(VillagerRecord villagerRecord) {
    if (MillConfigValues.LogMerchant >= 2)
      MillLog.minor(this, "Creating a new merchant"); 
    this.merchantRecord = villagerRecord;
    this.visitorsList.add("panels.startedtrading;" + this.merchantRecord.getName() + ";" + this.merchantRecord.getNativeOccupationName());
  }
  
  private void moveMerchant(Building destInn) {
    HashMap<InvItem, Integer> contents = this.resManager.getChestsContent();
    for (InvItem key : contents.keySet()) {
      int nb = takeGoods(key.getItem(), key.meta, 9999999);
      destInn.storeGoods(key.getItem(), key.meta, nb);
      destInn.addToImports(key, nb);
      addToExports(key, nb);
    } 
    transferVillagerPermanently(this.merchantRecord, destInn);
    this.visitorsList.add("panels.merchantmovedout;" + this.merchantRecord.getName() + ";" + this.merchantRecord.getNativeOccupationName() + ";" + destInn.getTownHall().getVillageQualifiedName() + ";" + this.nbNightsMerchant);
    destInn.visitorsList.add("panels.merchantarrived;" + this.merchantRecord.getName() + ";" + this.merchantRecord.getNativeOccupationName() + ";" + getTownHall().getVillageQualifiedName());
    if (MillConfigValues.LogMerchant >= 1)
      MillLog.major(this, "Moved merchant " + this.merchantRecord + " to " + destInn.getTownHall()); 
    destInn.merchantRecord = this.merchantRecord;
    this.merchantRecord = null;
    this.nbNightsMerchant = 0;
  }
  
  public int nbGoodAvailable(IBlockState bs, boolean forConstruction, boolean forExport, boolean forShop) {
    return nbGoodAvailable(InvItem.createInvItem(bs), forConstruction, forExport, forShop);
  }
  
  public int nbGoodAvailable(InvItem ii, boolean forConstruction, boolean forExport, boolean forShop) {
    if (this.resManager.chests.isEmpty())
      return 0; 
    if (forShop && 
      this.culture.shopNeeds.containsKey(this.location.shop))
      for (InvItem item : this.culture.shopNeeds.get(this.location.shop)) {
        if (item.matches(ii))
          return 0; 
      }  
    int nb = countGoods(ii.getItem(), ii.meta);
    if (this.isTownhall) {
      boolean projectHandled = false;
      BuildingPlan project = getCurrentGoalBuildingPlan();
      for (ConstructionIP cip : getConstructionsInProgress()) {
        if (cip.getBuildingLocation() != null) {
          BuildingPlan plan = cip.getBuildingLocation().getPlan();
          if (plan != null)
            for (InvItem key : plan.resCost.keySet()) {
              if (key.matches(ii)) {
                int builderHas = (cip.getBuilder() != null) ? cip.getBuilder().countInv(key) : 0;
                if (builderHas < ((Integer)plan.resCost.get(key)).intValue())
                  nb -= ((Integer)plan.resCost.get(key)).intValue() - builderHas; 
              } 
            }  
          if (project == plan)
            projectHandled = true; 
        } 
      } 
      if (!projectHandled && project != null)
        for (InvItem key : project.resCost.keySet()) {
          if (key.matches(ii))
            nb -= ((Integer)project.resCost.get(key)).intValue(); 
        }  
    } 
    boolean tradedHere = false;
    if (this.location.shop != null && this.culture.shopSells.containsKey(this.location.shop))
      for (TradeGood g : this.culture.shopSells.get(this.location.shop)) {
        if (g.item.matches(ii))
          tradedHere = true; 
      }  
    if (!forConstruction)
      if (this.isTownhall || tradedHere || forExport)
        for (InvItem key : this.culture.getInvItemsWithTradeGoods()) {
          if (key.matches(ii) && 
            this.culture.getTradeGood(key) != null) {
            TradeGood good = this.culture.getTradeGood(key);
            if (good != null) {
              if (forExport) {
                nb -= good.targetQuantity;
                continue;
              } 
              nb -= good.reservedQuantity;
            } 
          } 
        }   
    if (!forConstruction)
      for (VillagerRecord vr : getVillagerRecords().values()) {
        if (vr.getHousePos() != null && vr.getHousePos().equals(getPos()) && vr.getType() != null)
          for (InvItem requiredItem : (vr.getType()).requiredFoodAndGoods.keySet()) {
            if (ii.matches(requiredItem))
              nb -= ((Integer)(vr.getType()).requiredFoodAndGoods.get(requiredItem)).intValue(); 
          }  
      }  
    return Math.max(nb, 0);
  }
  
  public int nbGoodAvailable(Item item, int meta, boolean forConstruction, boolean forExport, boolean forShop) {
    return nbGoodAvailable(InvItem.createInvItem(item, meta), forConstruction, forExport, forShop);
  }
  
  public int nbGoodNeeded(Item item, int meta) {
    int nb = countGoods(item, meta);
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuilder() != null && cip.getBuildingLocation() != null && (cip.getBuildingLocation()).planKey.equals(this.buildingGoal))
        nb += cip.getBuilder().countInv(item, meta); 
    } 
    int targetAmount = 0;
    InvItem invitem = InvItem.createInvItem(item, meta);
    if (meta == -1) {
      for (int i = 0; i < 16; i++) {
        if (this.culture.getTradeGood(invitem) != null) {
          TradeGood good = this.culture.getTradeGood(InvItem.createInvItem(item, i));
          if (good != null)
            targetAmount += good.targetQuantity; 
        } 
      } 
    } else if (this.culture.getTradeGood(invitem) != null) {
      TradeGood good = this.culture.getTradeGood(invitem);
      if (good != null)
        targetAmount = good.targetQuantity; 
    } 
    BuildingPlan project = getCurrentGoalBuildingPlan();
    int neededForProject = 0;
    if (project != null)
      for (InvItem key : project.resCost.keySet()) {
        if (key.getItem() == item && (key.meta == meta || meta == -1 || key.meta == -1))
          neededForProject += ((Integer)project.resCost.get(key)).intValue(); 
      }  
    int needed = Math.max(neededForProject + targetAmount - nb, 0);
    if (needed == 0)
      return 0; 
    if (MillConfigValues.LogMerchant >= 3)
      MillLog.debug(this, "Goods needed: " + invitem.getName() + ": " + targetAmount + "/" + neededForProject + "/" + nb); 
    return needed;
  }
  
  public void planRaid(Building target) {
    this.raidPlanningStart = this.world.getWorldTime();
    this.raidStart = 0L;
    this.raidTarget = target.getPos();
    if (MillConfigValues.LogDiplomacy >= 1)
      MillLog.major(this, "raidTarget set: " + this.raidTarget + " name: " + target.name); 
    this.saveNeeded = true;
    this.saveReason = "Raid planned";
    ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.BackgroundRadius, '4', "raid.planningstarted", new String[] { getVillageQualifiedName(), target
          .getVillageQualifiedName() });
  }
  
  public boolean readFromNBT(NBTTagCompound nbttagcompound) {
    try {
      String version = nbttagcompound.getString("versionCompatibility");
      if (!version.equals("1.0")) {
        MillLog.error(this, "Tried to load building with incompatible version: " + version);
        return false;
      } 
      if (this.pos == null)
        this.pos = Point.read(nbttagcompound, "pos"); 
      this.chestLocked = nbttagcompound.getBoolean("chestLocked");
      List<String> tags = new ArrayList<>();
      NBTTagList nbttaglist = nbttagcompound.getTagList("tags", 10);
      for (int i = 0; i < nbttaglist.tagCount(); i++) {
        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
        String value = nbttagcompound1.getString("value");
        tags.add(value);
        if (MillConfigValues.LogTags >= 2)
          MillLog.minor(this, "Loading tag: " + value); 
      } 
      addTags(tags, "loading from NBT");
      if (getTags().size() > 0 && MillConfigValues.LogTags >= 1)
        MillLog.major(this, "Tags loaded: " + MillCommonUtilities.flattenStrings(getTags())); 
      this.location = BuildingLocation.read(nbttagcompound, "buildingLocation", "self", this);
      if (this.location == null) {
        MillLog.error(this, "No location found!");
        return false;
      } 
      String cultureKey = nbttagcompound.getString("culture");
      if (cultureKey.equals("hindi")) {
        MillLog.major(this, "Converting village culture from hindi to indian.");
        cultureKey = "indian";
      } 
      this.culture = Culture.getCultureByName(cultureKey);
      if (this.culture == null) {
        MillLog.error(this, "Could not load culture: " + nbttagcompound.getString("culture") + ", skipping building.");
        return false;
      } 
      if (nbttagcompound.hasKey("isTownhall")) {
        this.isTownhall = nbttagcompound.getBoolean("isTownhall");
      } else {
        this.isTownhall = this.location.planKey.equals("townHall");
      } 
      this.townHallPos = Point.read(nbttagcompound, "townHallPos");
      this.nightActionPerformed = nbttagcompound.getBoolean("nightActionPerformed");
      this.nightBackgroundActionPerformed = nbttagcompound.getBoolean("nightBackgroundActionPerformed");
      this.nbAnimalsRespawned = nbttagcompound.getInteger("nbAnimalsRespawned");
      if (nbttagcompound.hasKey("villagersrecords")) {
        nbttaglist = nbttagcompound.getTagList("villagersrecords", 10);
        MillLog.major(this, "Loading " + nbttaglist.tagCount() + " villagers from building list.");
        for (int k = 0; k < nbttaglist.tagCount(); k++) {
          NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(k);
          VillagerRecord vr = VillagerRecord.read(this.mw, nbttagcompound1, "vr");
          if (vr == null) {
            MillLog.error(this, "Couldn't load VR record.");
          } else {
            this.mw.registerVillagerRecord(vr, false);
            if (MillConfigValues.LogHybernation >= 2)
              MillLog.minor(this, "Loaded VR: " + vr); 
          } 
        } 
        MillLog.major(this, "Finished loading villagers from building list.");
      } 
      nbttaglist = nbttagcompound.getTagList("visitorsList", 10);
      int j;
      for (j = 0; j < nbttaglist.tagCount(); j++) {
        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
        this.visitorsList.add(nbttagcompound1.getString("visitor"));
      } 
      nbttaglist = nbttagcompound.getTagList("subBuildings", 10);
      for (j = 0; j < nbttaglist.tagCount(); j++) {
        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
        Point p = Point.read(nbttagcompound1, "pos");
        if (p != null)
          this.subBuildings.add(p); 
      } 
      if (containsTags("pujas") || containsTags("sacrifices")) {
        this.pujas = new PujaSacrifice(this, nbttagcompound.getCompoundTag("pujas"));
        if (MillConfigValues.LogPujas >= 2)
          MillLog.minor(this, "read pujas object"); 
      } 
      this.lastGoodsRefresh = nbttagcompound.getLong("lastGoodsRefresh");
      if (containsTags("inn") && !this.isTownhall) {
        this.isInn = true;
        readInn(nbttagcompound);
      } 
      if (this.isInn && getVillagerRecords().size() > 0)
        this.merchantRecord = getVillagerRecords().get(getVillagerRecords().keySet().iterator().next()); 
      if (containsTags("autospawnvillagers"))
        this.hasAutoSpawn = true; 
      if (containsTags("market") && !this.isTownhall) {
        this.isMarket = true;
        this.hasVisitors = true;
      } 
      if (!this.location.getVisitors().isEmpty())
        this.hasVisitors = true; 
      if (this.isTownhall) {
        if (MillConfigValues.LogHybernation >= 1)
          MillLog.major(this, "Loading Townhall data."); 
        readTownHall(nbttagcompound);
      } 
      this.resManager.readFromNBT(nbttagcompound);
      if (this.isTownhall && this.villageType.isMarvel()) {
        this.marvelManager = new MarvelManager(this);
        this.marvelManager.readFromNBT(nbttagcompound);
      } 
      BuildingPlan plan = this.location.getPlan();
      if (plan != null) {
        if (plan.shop != null && this.location.shop == null)
          this.location.shop = plan.shop; 
        plan.updateTags(this);
      } 
      if (MillConfigValues.LogTileEntityBuilding >= 3)
        MillLog.debug(this, "Loading building. Type: " + this.location + ", pos: " + getPos()); 
      return true;
    } catch (Exception e) {
      Mill.proxy.sendChatAdmin("Error when trying to load building. Check millenaire.log.");
      MillLog.error(this, "Error when trying to load building of type: " + this.location);
      MillLog.printException(e);
      return false;
    } 
  }
  
  public void readInn(NBTTagCompound nbttagcompound) throws MillLog.MillenaireException {
    NBTTagList nbttaglist = nbttagcompound.getTagList("importedGoods", 10);
    int i;
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
      InvItem good = InvItem.createInvItem(Item.getItemById(tag.getInteger("itemid")), tag.getInteger("itemmeta"));
      this.imported.put(good, Integer.valueOf(tag.getInteger("quantity")));
    } 
    nbttaglist = nbttagcompound.getTagList("exportedGoods", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
      InvItem good = InvItem.createInvItem(Item.getItemById(tag.getInteger("itemid")), tag.getInteger("itemmeta"));
      this.exported.put(good, Integer.valueOf(tag.getInteger("quantity")));
    } 
    nbttaglist = nbttagcompound.getTagList("importedGoodsNew", 10);
    MillCommonUtilities.readInventory(nbttaglist, this.imported);
    nbttaglist = nbttagcompound.getTagList("exportedGoodsNew", 10);
    MillCommonUtilities.readInventory(nbttaglist, this.exported);
  }
  
  private void readPaths() {
    File buildingsDir = MillCommonUtilities.getBuildingsDir(this.world);
    File file1 = new File(buildingsDir, getPos().getPathString() + "_paths.bin");
    if (file1.exists())
      try {
        FileInputStream fis = new FileInputStream(file1);
        DataInputStream ds = new DataInputStream(fis);
        int size = ds.readInt();
        this.pathsToBuild = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          List<BuildingBlock> path = new ArrayList<>();
          int sizePath = ds.readInt();
          for (int j = 0; j < sizePath; j++) {
            Point p = new Point(ds.readInt(), ds.readShort(), ds.readInt());
            BuildingBlock b = new BuildingBlock(p, ds);
            path.add(b);
          } 
          this.pathsToBuild.add(path);
        } 
        ds.close();
      } catch (Exception e) {
        MillLog.printException("Error when reading pathsToBuild: ", e);
      }  
    file1 = new File(buildingsDir, getPos().getPathString() + "_pathstoclear.bin");
    if (file1.exists())
      try {
        FileInputStream fis = new FileInputStream(file1);
        DataInputStream ds = new DataInputStream(fis);
        int size = ds.readInt();
        this.oldPathPointsToClear = new ArrayList<>();
        for (int i = 0; i < size; i++) {
          Point p = new Point(ds.readInt(), ds.readShort(), ds.readInt());
          this.oldPathPointsToClear.add(p);
        } 
        ds.close();
      } catch (Exception e) {
        MillLog.printException("Error when reading oldPathPointsToClear: ", e);
      }  
  }
  
  public void readTownHall(NBTTagCompound nbttagcompound) {
    this.name = nbttagcompound.getString("name");
    this.qualifier = nbttagcompound.getString("qualifier");
    String vtype = nbttagcompound.getString("villageType");
    if (vtype.length() == 0) {
      this.villageType = this.culture.getRandomVillage();
    } else if (this.culture.getVillageType(vtype) != null) {
      this.villageType = this.culture.getVillageType(vtype);
    } else if (this.culture.getLoneBuildingType(vtype) != null) {
      this.villageType = this.culture.getLoneBuildingType(vtype);
    } else {
      this.villageType = this.culture.getRandomVillage();
    } 
    if (nbttagcompound.getString("controlledBy").length() > 0) {
      String controlledByName = nbttagcompound.getString("controlledBy");
      GameProfile profile = this.world.getMinecraftServer().getPlayerProfileCache().getGameProfileForUsername(controlledByName);
      if (profile != null) {
        this.controlledBy = profile.getId();
        MillLog.major(this, "Converted controlledBy from name '" + controlledByName + "' to UUID " + this.controlledBy);
      } else {
        MillLog.error(this, "Could not convert controlledBy from name '" + controlledByName + "'.");
      } 
    } 
    if (!nbttagcompound.getUniqueId("controlledByUUID").equals(new UUID(0L, 0L))) {
      this.controlledBy = nbttagcompound.getUniqueId("controlledByUUID");
      GameProfile profile = this.mw.world.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(this.controlledBy);
      if (profile != null)
        this.controlledByName = profile.getName(); 
    } 
    NBTTagList nbttaglist = nbttagcompound.getTagList("buildings", 10);
    int i;
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        if (this.buildings.contains(p)) {
          MillLog.warning(this, "Trying to add a building that is already there: " + p);
        } else {
          this.buildings.add(p);
        }  
    } 
    initialiseBuildingProjects();
    nbttaglist = nbttagcompound.getTagList("locations", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      BuildingLocation location = BuildingLocation.read(nbttagcompound1, "location", "locations", null);
      if (location == null) {
        MillLog.error(this, "Could not load building location. Skipping.");
      } else {
        fillinBuildingLocationInProjects(location);
      } 
    } 
    for (i = this.buildings.size() - 1; i >= 0; i--) {
      boolean foundLocation = false;
      for (BuildingLocation l : getLocations()) {
        if (((Point)this.buildings.get(i)).equals(l.chestPos))
          foundLocation = true; 
      } 
      if (!foundLocation) {
        MillLog.error(this, "Deleting building as could not find the location for it at: " + this.buildings.get(i));
        this.buildings.remove(i);
      } 
    } 
    if (this.villageType.playerControlled)
      for (List<BuildingProject> level : this.buildingProjects.values()) {
        List<BuildingProject> toDelete = new ArrayList<>();
        for (BuildingProject project : level) {
          if (project.location == null)
            toDelete.add(project); 
        } 
        for (BuildingProject project : toDelete)
          level.remove(project); 
      }  
    this.buildingGoal = nbttagcompound.getString("buildingGoal");
    if (this.culture.getBuildingPlanSet(this.buildingGoal) == null) {
      this.buildingGoal = null;
      this.buildingGoalLevel = 0;
      this.buildingGoalVariation = 0;
      if (MillConfigValues.LogHybernation >= 1)
        MillLog.major(this, "No goal found: " + this.buildingGoal); 
    } else {
      this.buildingGoalLevel = nbttagcompound.getInteger("buildingGoalLevel");
      this.buildingGoalVariation = nbttagcompound.getInteger("buildingGoalVariation");
      if (MillConfigValues.LogHybernation >= 1)
        MillLog.major(this, "Reading building goal: " + this.buildingGoal); 
    } 
    this.buildingGoalLocation = BuildingLocation.read(nbttagcompound, "buildingGoalLocation", "buildingGoalLocation", null);
    if (this.buildingGoalLocation != null && 
      MillConfigValues.LogHybernation >= 1)
      MillLog.major(this, "Loaded buildingGoalLocation: " + this.buildingGoalLocation); 
    this.buildingGoalIssue = nbttagcompound.getString("buildingGoalIssue");
    if (this.buildingGoal != null) {
      BuildingPlanSet planSet = this.culture.getBuildingPlanSet(this.buildingGoal);
      if (planSet != null)
        if (this.buildingGoalVariation >= planSet.plans.size() || this.buildingGoalLevel >= ((BuildingPlan[])planSet.plans.get(this.buildingGoalVariation)).length) {
          this.buildingGoal = null;
          this.buildingGoalLevel = 0;
          this.buildingGoalVariation = 0;
          this.buildingGoalLocation = null;
        } else if (this.buildingGoalLocation != null && this.buildingGoalLocation.version != (((BuildingPlan[])planSet.plans.get(this.buildingGoalVariation))[0]).version) {
          this.buildingGoal = null;
          this.buildingGoalLevel = 0;
          this.buildingGoalVariation = 0;
          this.buildingGoalLocation = null;
        }  
    } 
    int nbConstructions = nbttagcompound.getInteger("nbConstructions");
    int j;
    for (j = 0; j < nbConstructions; j++) {
      ConstructionIP cip = new ConstructionIP(this, j, nbttagcompound.getBoolean("buildingLocationIP_" + j + "_isWall"));
      getConstructionsInProgress().add(cip);
      cip.setBuildingLocation(BuildingLocation.read(nbttagcompound, "buildingLocationIP_" + j, "buildingLocationIP_" + j, null));
      if (cip.getBuildingLocation() != null) {
        if (this.culture.getBuildingPlanSet((cip.getBuildingLocation()).planKey) == null) {
          cip.clearBuildingLocation();
        } else {
          BuildingPlanSet set = this.culture.getBuildingPlanSet((cip.getBuildingLocation()).planKey);
          if ((cip.getBuildingLocation()).level >= ((BuildingPlan[])set.plans.get(cip.getBuildingLocation().getVariation())).length)
            cip.clearBuildingLocation(); 
        } 
        cip.readBblocks();
        cip.setBblockPos(nbttagcompound.getInteger("bblocksPos_" + j));
      } 
    } 
    nbttaglist = nbttagcompound.getTagList("buildingsBought", 10);
    for (j = 0; j < nbttaglist.tagCount(); j++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
      this.buildingsBought.add(nbttagcompound1.getString("key"));
    } 
    this.parentVillage = Point.read(nbttagcompound, "parentVillage");
    if (nbttagcompound.hasKey("relations")) {
      nbttaglist = nbttagcompound.getTagList("relations", 10);
      for (j = 0; j < nbttaglist.tagCount(); j++) {
        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
        this.relations.put(Point.read(nbttagcompound1, "pos"), Integer.valueOf(nbttagcompound1.getInteger("value")));
      } 
    } 
    this.updateRaidPerformed = nbttagcompound.getBoolean("updateRaidPerformed");
    this.nightBackgroundActionPerformed = nbttagcompound.getBoolean("nightBackgroundActionPerformed");
    this.raidTarget = Point.read(nbttagcompound, "raidTarget");
    this.raidPlanningStart = nbttagcompound.getLong("raidPlanningStart");
    this.raidStart = nbttagcompound.getLong("raidStart");
    this.underAttack = nbttagcompound.getBoolean("underAttack");
    nbttaglist = nbttagcompound.getTagList("raidsPerformed", 10);
    for (j = 0; j < nbttaglist.tagCount(); j++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
      this.raidsPerformed.add(nbttagcompound1.getString("raid"));
    } 
    nbttaglist = nbttagcompound.getTagList("raidsTaken", 10);
    for (j = 0; j < nbttaglist.tagCount(); j++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(j);
      this.raidsSuffered.add(nbttagcompound1.getString("raid"));
    } 
    this.pathsToBuildIndex = nbttagcompound.getInteger("pathsToBuildIndex");
    this.pathsToBuildPathIndex = nbttagcompound.getInteger("pathsToBuildPathIndex");
    this.oldPathPointsToClearIndex = nbttagcompound.getInteger("oldPathPointsToClearIndex");
    String brickThemeKey = nbttagcompound.getString("brickColourTheme");
    if (this.villageType != null && brickThemeKey != null && brickThemeKey.length() > 0) {
      for (VillageType.BrickColourTheme theme : this.villageType.brickColourThemes) {
        if (theme.key.equals(brickThemeKey))
          this.brickColourTheme = theme; 
      } 
      if (this.brickColourTheme == null) {
        MillLog.warning(this, "Could not load brick colour theme: " + brickThemeKey);
      } else {
        MillLog.temp(this, "Loaded theme: " + this.brickColourTheme.key);
      } 
    } 
    readPaths();
    if (nbttagcompound.hasKey("bannerStack")) {
      this.bannerStack = new ItemStack(nbttagcompound.getCompoundTag("bannerStack"));
    } else {
      generateBannerPattern();
    } 
  }
  
  public boolean rebuildRegionMapper(boolean sync) throws MillLog.MillenaireException {
    updateWorldInfo();
    if (sync) {
      RegionMapper temp = new RegionMapper();
      if (temp.createConnectionsTable(this.winfo, this.resManager.getSleepingPos())) {
        this.regionMapper = temp;
        this.lastPathingUpdate = this.world.getWorldTime();
        return true;
      } 
      this.regionMapper = null;
      this.lastPathingUpdate = this.world.getWorldTime();
      return false;
    } 
    if (!this.rebuildingRegionMapper) {
      try {
        this.rebuildingRegionMapper = true;
        RegionMapperThread thread = new RegionMapperThread(this.winfo.clone());
        thread.setPriority(1);
        if (MillConfigValues.LogPathing >= 1)
          MillLog.major(this, "Thread starting."); 
        thread.start();
        if (MillConfigValues.LogPathing >= 1)
          MillLog.major(this, "Thread started."); 
      } catch (CloneNotSupportedException e) {
        MillLog.printException(e);
      } 
      return true;
    } 
    return true;
  }
  
  public void rebuildVillagerList() {
    Set<MillVillager> newSet = new LinkedHashSet<>();
    for (MillVillager villager : this.mw.getAllKnownVillagers()) {
      if (villager.getHouse() == this || villager.getTownHall() == this)
        newSet.add(villager); 
    } 
    this.villagers = newSet;
    if (MillConfigValues.LogVillagerSpawn >= 2) {
      List<Entity> nearbyVillagers = WorldUtilities.getEntitiesWithinAABB(this.world, MillVillager.class, this.pos, Math.max(this.winfo.length, this.winfo.width) / 2 + 20, 40);
      for (Entity villagerEntity : nearbyVillagers) {
        MillVillager villager = (MillVillager)villagerEntity;
        if ((villager.getTownHall() == this || villager.getHouse() == this) && 
          !this.villagers.contains(villager))
          MillLog.warning(this, "Found a villager nearby that isn't registered! : " + villager); 
      } 
    } 
  }
  
  public void recalculatePaths(boolean autobuild) {
    if (!MillConfigValues.BuildVillagePaths)
      return; 
    int nbPaths = 0;
    for (Building b : getBuildings()) {
      if (b != this && b.location != null && b.location.getPlan() != null && !(b.location.getPlan()).isSubBuilding && b.resManager.getPathStartPos() != null)
        nbPaths++; 
    } 
    PathCreatorQueue queue = new PathCreatorQueue();
    this.autobuildPaths = autobuild;
    Point townHallPathPoint = this.resManager.getPathStartPos();
    List<Point> nodePoints = new ArrayList<>();
    for (Building b : getBuildings()) {
      if (b != this && b.containsTags("pathnode"))
        nodePoints.add(b.resManager.getPathStartPos()); 
    } 
    if (MillConfigValues.LogVillagePaths >= 2)
      MillLog.minor(this, "Launching path rebuild, expected paths number: " + nbPaths); 
    for (Building b : getBuildings()) {
      Point pathStartPos = b.resManager.getPathStartPos();
      if (b != this && b.location != null && b.location.getPlan() != null && !(b.location.getPlan()).isSubBuilding && pathStartPos != null && !(b.location.getPlan()).noPathsToBuilding) {
        InvItem pathMaterial = this.villageType.pathMaterial.get(0);
        if ((b.location.getPlan()).pathLevel < this.villageType.pathMaterial.size())
          pathMaterial = this.villageType.pathMaterial.get((b.location.getPlan()).pathLevel); 
        Point pathDest = townHallPathPoint;
        if (!b.containsTags("pathnode")) {
          for (Point nodePoint : nodePoints) {
            if (pathDest == townHallPathPoint) {
              if (nodePoint.distanceTo(pathStartPos) * 1.3D < pathDest.distanceTo(pathStartPos))
                pathDest = nodePoint; 
              continue;
            } 
            if (nodePoint.distanceTo(pathStartPos) < pathDest.distanceTo(pathStartPos))
              pathDest = nodePoint; 
          } 
          if (pathDest.distanceTo(pathStartPos) > 20.0D) {
            Point otherBuildingDest = null;
            for (Building otherBuilding : getBuildings()) {
              if (otherBuilding != this && otherBuilding.location != null && otherBuilding.location.getPlan() != null && !(otherBuilding.location.getPlan()).isSubBuilding && otherBuilding.resManager
                .getPathStartPos() != null && !otherBuilding.containsTags("pathnode") && 
                !(otherBuilding.location.getPlan()).noPathsToBuilding) {
                Point otherBuildingPathStart = otherBuilding.resManager.getPathStartPos();
                if (townHallPathPoint.distanceToSquared(pathStartPos) > townHallPathPoint.distanceToSquared(otherBuildingPathStart))
                  if (otherBuildingPathStart.distanceTo(pathStartPos) * 1.5D < pathDest.distanceTo(pathStartPos))
                    if (otherBuildingDest == null || pathStartPos.distanceToSquared(otherBuildingDest) > pathStartPos.distanceToSquared(otherBuildingPathStart))
                      otherBuildingDest = otherBuildingPathStart;   
              } 
            } 
            if (otherBuildingDest != null)
              pathDest = otherBuildingDest; 
          } 
        } 
        PathCreator pathCreator = new PathCreator(queue, pathMaterial, (b.location.getPlan()).pathWidth, b, pathDest, pathStartPos);
        queue.addPathCreator(pathCreator);
      } 
    } 
    queue.startNextPath();
  }
  
  private void refreshGoods() {
    if (this.location == null || this.location.getPlan() == null || (this.location.getPlan()).startingGoods.size() == 0)
      return; 
    if (this.world.isDaytime()) {
      this.refreshGoodsNightActionPerformed = false;
    } else if (!this.refreshGoodsNightActionPerformed) {
      long interval;
      if (this.chestLocked) {
        interval = 20L;
      } else {
        interval = 100L;
      } 
      if (this.lastGoodsRefresh + interval * 24000L < this.world.getWorldTime() && this.chestLocked) {
        fillStartingGoods();
        this.lastGoodsRefresh = this.world.getWorldTime();
      } 
      this.refreshGoodsNightActionPerformed = true;
    } 
  }
  
  public void registerBuildingEntity(Building buildingEntity) throws MillLog.MillenaireException {
    if (this.buildings.contains(buildingEntity.getPos())) {
      MillLog.warning(this, "Trying to the register building that is already present: " + buildingEntity.getPos());
    } else {
      this.buildings.add(buildingEntity.getPos());
    } 
    this.saveNeeded = true;
    this.saveReason = "Registering building";
  }
  
  public void registerBuildingLocation(BuildingLocation location) {
    boolean registered = false;
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (this.buildingProjects.containsKey(ep)) {
        List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
        for (BuildingProject project : projectsLevel) {
          if (location.level == 0 && location.isSubBuildingLocation) {
            if (project.key.equals(location.planKey) && (project.location == null || project.location.level < 0)) {
              if (project.location != null)
                location.upgradesAllowed = project.location.upgradesAllowed; 
              project.location = location.clone();
              registered = true;
              if (MillConfigValues.LogBuildingPlan >= 1)
                MillLog.major(this, "Updated building project: " + project + " with initial location."); 
            } 
          } else if (location.level == 0) {
            if (project.key.equals(location.planKey) && (project.location == null || (project.location.level < 0 && project.location.isSameLocation(location)))) {
              if (project.location != null)
                location.upgradesAllowed = project.location.upgradesAllowed; 
              project.location = location;
              registered = true;
              if (MillConfigValues.LogBuildingPlan >= 1)
                MillLog.major(this, "Updated building project: " + project + " with initial location."); 
            } 
          } else if (location.isSameLocation(project.location)) {
            if (MillConfigValues.LogBuildingPlan >= 1)
              MillLog.major(this, "Updated building project: " + project + " from level " + project.location.level + " to " + location.level); 
            location.upgradesAllowed = project.location.upgradesAllowed;
            project.location = location;
            registered = true;
          } 
          if (registered)
            break; 
        } 
      } 
      if (registered)
        break; 
    } 
    if (!registered) {
      BuildingProject project = new BuildingProject(location.getPlan().getPlanSet());
      project.location = location;
      if ((location.getPlan()).isWallSegment) {
        if (!this.buildingProjects.containsKey(BuildingProject.EnumProjects.WALLBUILDING))
          this.buildingProjects.put(BuildingProject.EnumProjects.WALLBUILDING, new CopyOnWriteArrayList<>()); 
        ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.WALLBUILDING)).add(project);
      } else {
        ((CopyOnWriteArrayList<BuildingProject>)this.buildingProjects.get(BuildingProject.EnumProjects.EXTRA)).add(project);
      } 
    } 
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(this, "Registered building location: " + location); 
    BuildingPlan plan = location.getPlan();
    if (plan != null)
      for (Point p : this.buildings) {
        Building building = this.mw.getBuilding(p);
        if (building != null && building.location != null && building.location.isSameLocation(location)) {
          building.location = building.location.createLocationForLevel(location.level);
          plan = location.getPlan();
          if (MillConfigValues.LogBuildingPlan >= 1)
            MillLog.major(this, "Updated building location for building: " + building + " now at upgrade: " + location.level); 
        } 
      }  
    for (String s : location.subBuildings) {
      boolean found = false;
      List<BuildingProject> parentProjectLevel = null;
      int parentPos = 0;
      for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
        if (this.buildingProjects.containsKey(ep)) {
          List<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
          int pos = 0;
          for (BuildingProject project : projectsLevel) {
            if (project.location != null)
              if (project.location.isLocationSamePlace(location) && project.key.equals(s)) {
                found = true;
              } else if (project.location.isSameLocation(location)) {
                parentProjectLevel = projectsLevel;
                parentPos = pos;
              }  
            pos++;
          } 
        } 
      } 
      if (!found && parentProjectLevel != null) {
        if (this.culture.getBuildingPlanSet(s) == null) {
          MillLog.error(this, "Could not find plan for finished building: " + s);
          return;
        } 
        BuildingProject project = new BuildingProject(this.culture.getBuildingPlanSet(s), location.getPlan());
        project.location = location.createLocationForSubBuilding(s);
        parentProjectLevel.add(parentPos + 1, project);
      } 
    } 
    this.saveNeeded = true;
    this.saveReason = "Registering location";
  }
  
  public void registerVillagerRecord(VillagerRecord villagerRecord) {
    getVillagerRecords().put(Long.valueOf(villagerRecord.getVillagerId()), villagerRecord);
  }
  
  public boolean removeVillagerRecord(long vid) {
    return (getVillagerRecords().remove(Long.valueOf(vid)) != null);
  }
  
  public void requestSave(String reason) {
    this.saveNeeded = true;
    this.saveReason = reason;
  }
  
  public void resetConstructionsAndGoals() {
    this.constructionsIP.clear();
    this.buildingGoal = null;
    this.buildingGoalLocation = null;
  }
  
  public void respawnVillager(VillagerRecord vr, Point villagerPos) {
    MillVillager villager = MillVillager.createVillager(vr, this.world, villagerPos, true);
    if (villager == null) {
      MillLog.error(this, "Could not recreate villager " + vr + " of type " + vr.type);
    } else {
      if (!vr.killed) {
        if (MillConfigValues.LogVillagerSpawn >= 1)
          MillLog.major(this, "Giving the villager back " + vr.inventory.size() + " item types."); 
        for (InvItem iv : vr.inventory.keySet())
          villager.inventory.put(iv, vr.inventory.get(iv)); 
      } 
      if (!vr.isTextureValid(vr.texture.getResourcePath()))
        vr.texture = vr.getType().getNewTexture(); 
      vr.killed = false;
      if (villager.getHouse() != null) {
        villager.setTexture(vr.texture);
        villager.isRaider = vr.raidingVillage;
        if (villager.isChild())
          villager.computeChildScale(); 
        this.world.spawnEntity((Entity)villager);
      } 
    } 
  }
  
  private void respawnVillagersIfNeeded() throws MillLog.MillenaireException {
    int time = (int)(this.world.getWorldTime() % 24000L);
    boolean resurect = (time >= 13000 && time < 13100);
    for (VillagerRecord vr : getVillagerRecords().values()) {
      MillVillager foundVillager = this.mw.getVillagerById(vr.getVillagerId());
      if (foundVillager == null) {
        boolean respawn = false;
        if (!vr.flawedRecord)
          if (vr.raidingVillage) {
            if (!vr.killed && this.world.getWorldTime() > vr.raiderSpawn + 500L)
              respawn = true; 
          } else if (!vr.awayraiding && !vr.awayhired && !(vr.getType()).noResurrect) {
            if (!vr.killed || resurect)
              respawn = true; 
          }  
        if (respawn) {
          Point villagerPos;
          if (MillConfigValues.LogVillagerSpawn >= 1)
            MillLog.major(this, "Recreating missing villager from record " + vr + ". Killed: " + vr.killed); 
          if (this.mw.getBuilding(vr.getHousePos()) == null) {
            MillLog.error(this, "Error when trying to recreate a villager from record " + vr + ": couldn't load house at " + vr.getHousePos() + ".");
            vr.flawedRecord = true;
            continue;
          } 
          if (vr.raidingVillage && vr.originalVillagePos != null) {
            villagerPos = findAttackerSpawnPoint(vr.originalVillagePos);
          } else if (this.underAttack) {
            if ((vr.getType()).helpInAttacks) {
              villagerPos = this.resManager.getDefendingPos();
            } else {
              villagerPos = this.resManager.getShelterPos();
            } 
          } else {
            villagerPos = (this.mw.getBuilding(vr.getHousePos())).resManager.getSleepingPos();
          } 
          respawnVillager(vr, villagerPos);
        } 
        continue;
      } 
      if (vr.getHousePos() == null || vr.texture == null || vr.getNameKey() == null || vr.getNameKey().length() == 0 || vr.getNameKey().equals("villager")) {
        MillLog.major(this, "Updating record for villager: " + foundVillager);
        vr.updateRecord(foundVillager);
        vr.flawedRecord = false;
      } 
    } 
  }
  
  public int rushCurrentConstructions(boolean worldGeneration) throws MillLog.MillenaireException {
    int nbRushed = 0;
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuildingLocation() != null) {
        List<BuildingPlan.LocationBuildingPair> lbps;
        BuildingPlan plan = getBuildingPlanForConstruction(cip);
        nbRushed++;
        if (cip.getBuildingLocation().isSameLocation(this.location)) {
          lbps = plan.build(this.mw, this.villageType, cip.getBuildingLocation(), worldGeneration, true, this, false, false, null, true);
        } else {
          lbps = plan.build(this.mw, this.villageType, cip.getBuildingLocation(), worldGeneration, false, this, false, false, null, true);
        } 
        for (BuildingPlan.LocationBuildingPair b : lbps)
          registerBuildingEntity(b.building); 
        cip.clearBblocks();
        completeConstruction(cip);
      } 
    } 
    updateConstructionQueue(worldGeneration);
    return nbRushed;
  }
  
  public void saveTownHall(String reason) {
    if (!this.world.isRemote && 
      this.saveWorker == null) {
      this.saveWorker = new SaveWorker(reason);
      this.saveWorker.start();
    } 
  }
  
  public void sendBuildingPacket(EntityPlayer player, boolean sendChest) {
    if (this.world.isRemote)
      return; 
    if (this.culture == null) {
      MillLog.error(this, "Unknown culture for this village.");
      return;
    } 
    if (sendChest)
      sendChestPackets(player); 
    PacketBuffer data = ServerSender.getPacketBuffer();
    try {
      data.writeInt(2);
      StreamReadWrite.writeNullablePoint(getPos(), data);
      data.writeBoolean(this.isTownhall);
      data.writeBoolean(this.chestLocked);
      StreamReadWrite.writeNullableUUID(this.controlledBy, data);
      StreamReadWrite.writeNullableString(this.controlledByName, data);
      StreamReadWrite.writeNullablePoint(getTownHallPos(), data);
      StreamReadWrite.writeNullableString(this.culture.key, data);
      String vtype = null;
      if (this.villageType != null)
        vtype = this.villageType.key; 
      StreamReadWrite.writeNullableString(vtype, data);
      StreamReadWrite.writeNullableBuildingLocation(this.location, data);
      StreamReadWrite.writeStringCollection(getTags(), data);
      StreamReadWrite.writeNullableString(this.buildingGoal, data);
      StreamReadWrite.writeNullableString(this.buildingGoalIssue, data);
      data.writeInt(this.buildingGoalLevel);
      data.writeInt(this.buildingGoalVariation);
      StreamReadWrite.writeNullableBuildingLocation(this.buildingGoalLocation, data);
      List<BuildingLocation> bls = new ArrayList<>();
      List<Boolean> isCIPwall = new ArrayList<>();
      for (ConstructionIP cip : getConstructionsInProgress()) {
        isCIPwall.add(Boolean.valueOf(cip.isWallConstruction()));
        bls.add(cip.getBuildingLocation());
      } 
      StreamReadWrite.writeBooleanList(isCIPwall, data);
      StreamReadWrite.writeBuildingLocationList(bls, data);
      StreamReadWrite.writeProjectListList(this.buildingProjects, data);
      StreamReadWrite.writePointList(this.buildings, data);
      StreamReadWrite.writeStringList(this.buildingsBought, data);
      StreamReadWrite.writePointIntegerMap(this.relations, data);
      StreamReadWrite.writeStringList(this.raidsPerformed, data);
      StreamReadWrite.writeStringList(this.raidsSuffered, data);
      StreamReadWrite.writeVillagerRecordMap(getVillagerRecords(), data);
      StreamReadWrite.writeNullablePuja(this.pujas, data);
      StreamReadWrite.writeStringList(this.visitorsList, data);
      StreamReadWrite.writeInventory(this.imported, data);
      StreamReadWrite.writeInventory(this.exported, data);
      StreamReadWrite.writeNullableString(this.name, data);
      StreamReadWrite.writeNullableString(getQualifier(), data);
      StreamReadWrite.writeNullablePoint(this.raidTarget, data);
      data.writeLong(this.raidPlanningStart);
      data.writeLong(this.raidStart);
      this.resManager.sendBuildingPacket(data);
      if (this.marvelManager != null)
        this.marvelManager.sendBuildingPacket(data); 
    } catch (IOException e) {
      MillLog.printException(this + ": Error in sendUpdatePacket", e);
    } 
    (this.mw.getProfile(player)).buildingsSent.put(this.pos, Long.valueOf(this.mw.world.getWorldTime()));
    ServerSender.sendPacketToPlayer(data, player);
  }
  
  public void sendChestPackets(EntityPlayer player) {
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      if (chest != null) {
        chest.buildingPos = getPos();
        chest.sendUpdatePacket(player);
      } 
    } 
  }
  
  private void sendInitialBuildingPackets() {
    for (EntityPlayer player : VillageUtilities.getServerPlayers(this.mw.world)) {
      if (this.pos.distanceToSquared((Entity)player) < 256.0D) {
        UserProfile profile = VillageUtilities.getServerProfile(this.mw.world, player);
        if (profile != null && !profile.buildingsSent.containsKey(this.pos))
          sendBuildingPacket(player, false); 
      } 
    } 
  }
  
  public void sendMapInfo(EntityPlayer player) {
    if (getTownHall() != null && (getTownHall()).winfo != null) {
      MillMapInfo minfo = new MillMapInfo(getTownHall(), (getTownHall()).winfo);
      minfo.sendMapInfoPacket(player);
    } 
  }
  
  public void sendShopPacket(EntityPlayer player) {
    PacketBuffer data = ServerSender.getPacketBuffer();
    data.writeInt(11);
    StreamReadWrite.writeNullablePoint(getPos(), data);
    if (this.shopSells.containsKey(player.getName())) {
      data.writeInt(((LinkedHashMap)this.shopSells.get(player.getName())).size());
      for (TradeGood g : this.shopSells.get(player.getName()).keySet()) {
        StreamReadWrite.writeNullableGoods(g, data);
        data.writeInt(this.shopSells.get(player.getName()).get(g).intValue());
      } 
    } else {
      data.writeInt(0);
    } 
    if (this.shopBuys.containsKey(player.getName())) {
      data.writeInt(((LinkedHashMap)this.shopBuys.get(player.getName())).size());
      for (TradeGood g : this.shopBuys.get(player.getName()).keySet()) {
        StreamReadWrite.writeNullableGoods(g, data);
        data.writeInt(this.shopBuys.get(player.getName()).get(g).intValue());
      } 
    } else {
      data.writeInt(0);
    } 
    ServerSender.sendPacketToPlayer(data, player);
  }
  
  public void sendVillagerOnRaid(VillagerRecord vr, Building target) {
    if (MillConfigValues.LogDiplomacy >= 2)
      MillLog.minor(this, "Sending villager " + vr + " to raid" + target + "."); 
    vr.awayraiding = true;
    VillagerRecord raidRecord = vr.generateRaidRecord(target);
    this.mw.registerVillagerRecord(raidRecord, true);
    MillVillager v = this.mw.getVillagerById(vr.getVillagerId());
    if (v != null) {
      v.despawnVillagerSilent();
      if (MillConfigValues.LogDiplomacy >= 3)
        MillLog.debug(this, "Villager entity despawned."); 
    } 
    target.getTownHall().saveTownHall("incoming villager");
  }
  
  public void setGoods(Item item, int meta, int newVal) {
    int nb = countGoods(item, meta);
    if (nb < newVal) {
      storeGoods(item, meta, newVal - nb);
    } else {
      takeGoods(item, meta, nb - newVal);
    } 
  }
  
  public void setNewVillagerList(Set<MillVillager> villagers) {
    this.villagers = villagers;
  }
  
  private void startRaid() {
    Building distantVillage = this.mw.getBuilding(this.raidTarget);
    if (this.relations.get(this.raidTarget) != null && ((Integer)this.relations.get(this.raidTarget)).intValue() > -90)
      cancelRaid(); 
    if (distantVillage != null) {
      this.raidStart = this.world.getWorldTime();
      int nbRaider = 0;
      Collection<VillagerRecord> vrecordsCopy = new ArrayList<>(getVillagerRecords().values());
      for (VillagerRecord vr : vrecordsCopy) {
        if ((vr.getType()).isRaider && !vr.killed && !vr.raidingVillage && !vr.awayraiding && !vr.awayhired) {
          if (MillConfigValues.LogDiplomacy >= 2)
            MillLog.minor(this, "Need to transfer raider; " + vr); 
          vr.getHouse().sendVillagerOnRaid(vr, distantVillage);
          nbRaider++;
        } 
      } 
      if (nbRaider > 0) {
        ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.BackgroundRadius, '4', "raid.started", new String[] { getVillageQualifiedName(), distantVillage
              .getVillageQualifiedName(), "" + nbRaider });
        distantVillage.cancelRaid();
        distantVillage.underAttack = true;
      } else {
        cancelRaid();
      } 
    } else {
      cancelRaid();
    } 
  }
  
  public int storeGoods(Block block, int toPut) {
    return storeGoods(Item.getItemFromBlock(block), 0, toPut);
  }
  
  public int storeGoods(Block block, int meta, int toPut) {
    return storeGoods(Item.getItemFromBlock(block), meta, toPut);
  }
  
  public int storeGoods(IBlockState bs, int toPut) {
    return storeGoods(Item.getItemFromBlock(bs.getBlock()), bs.getBlock().getMetaFromState(bs), toPut);
  }
  
  public int storeGoods(InvItem item, int toPut) {
    return storeGoods(item.getItem(), item.meta, toPut);
  }
  
  public int storeGoods(Item item, int toPut) {
    return storeGoods(item, 0, toPut);
  }
  
  public int storeGoods(Item item, int meta, int toPut) {
    int stored = 0;
    int i = 0;
    while (stored < toPut && i < this.resManager.chests.size()) {
      TileEntityLockedChest chest = ((Point)this.resManager.chests.get(i)).getMillChest(this.world);
      if (chest != null)
        stored += MillCommonUtilities.putItemsInChest((IInventory)chest, item, meta, toPut - stored); 
      i++;
    } 
    invalidateInventoryCache();
    return stored;
  }
  
  public boolean storeItemStack(ItemStack is) {
    for (Point p : this.resManager.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.world);
      if (chest != null)
        for (int i = 0; i < chest.getSizeInventory(); i++) {
          ItemStack stack = chest.getStackInSlot(i);
          if (stack.isEmpty()) {
            chest.setInventorySlotContents(i, is);
            invalidateInventoryCache();
            return true;
          } 
        }  
    } 
    return false;
  }
  
  private void swapMerchants(Building destInn) {
    HashMap<InvItem, Integer> contents = this.resManager.getChestsContent();
    HashMap<InvItem, Integer> destContents = destInn.resManager.getChestsContent();
    for (InvItem key : contents.keySet()) {
      int nb = takeGoods(key.getItem(), key.meta, ((Integer)contents.get(key)).intValue());
      destInn.storeGoods(key.getItem(), key.meta, nb);
      destInn.addToImports(key, nb);
      addToExports(key, nb);
    } 
    for (InvItem key : destContents.keySet()) {
      int nb = destInn.takeGoods(key.getItem(), key.meta, ((Integer)destContents.get(key)).intValue());
      storeGoods(key.getItem(), key.meta, nb);
      destInn.addToExports(key, nb);
      addToImports(key, nb);
    } 
    VillagerRecord oldMerchant = this.merchantRecord;
    VillagerRecord newMerchant = destInn.merchantRecord;
    transferVillagerPermanently(this.merchantRecord, destInn);
    destInn.transferVillagerPermanently(destInn.merchantRecord, this);
    this.visitorsList
      .add("panels.merchantmovedout;" + oldMerchant.getName() + ";" + oldMerchant.getNativeOccupationName() + ";" + destInn.getTownHall().getVillageQualifiedName() + ";" + this.nbNightsMerchant);
    destInn.visitorsList
      .add("panels.merchantmovedout;" + newMerchant.getName() + ";" + newMerchant.getNativeOccupationName() + ";" + getTownHall().getVillageQualifiedName() + ";" + this.nbNightsMerchant);
    this.visitorsList.add("panels.merchantarrived;" + newMerchant.getName() + ";" + newMerchant.getNativeOccupationName() + ";" + destInn.getTownHall().getVillageQualifiedName());
    destInn.visitorsList.add("panels.merchantarrived;" + oldMerchant.getName() + ";" + oldMerchant.getNativeOccupationName() + ";" + getTownHall().getVillageQualifiedName());
    if (MillConfigValues.LogMerchant >= 1)
      MillLog.major(this, "Swaped merchant " + oldMerchant + " and " + newMerchant + " with " + destInn.getTownHall()); 
    this.merchantRecord = newMerchant;
    destInn.merchantRecord = oldMerchant;
    this.nbNightsMerchant = 0;
    destInn.nbNightsMerchant = 0;
    destInn.saveTownHall("merchant moved");
    this.saveNeeded = true;
    this.saveReason = "Swapped merchant";
  }
  
  public int takeGoods(Block block, int meta, int toTake) {
    return takeGoods(Item.getItemFromBlock(block), meta, toTake);
  }
  
  public int takeGoods(IBlockState blockState, int toTake) {
    return takeGoods(Item.getItemFromBlock(blockState.getBlock()), blockState.getBlock().getMetaFromState(blockState), toTake);
  }
  
  public int takeGoods(InvItem item, int toTake) {
    return takeGoods(item.getItem(), item.meta, toTake);
  }
  
  public int takeGoods(Item item, int toTake) {
    return takeGoods(item, 0, toTake);
  }
  
  public int takeGoods(Item item, int meta, int toTake) {
    int taken = 0;
    int i = 0;
    while (taken < toTake && i < this.resManager.chests.size()) {
      TileEntityLockedChest chest = ((Point)this.resManager.chests.get(i)).getMillChest(this.world);
      if (chest != null)
        taken += WorldUtilities.getItemsFromChest((IInventory)chest, item, meta, toTake - taken); 
      i++;
    } 
    i = 0;
    while (taken < toTake && i < this.resManager.furnaces.size()) {
      TileEntityFurnace furnace = ((Point)this.resManager.furnaces.get(i)).getFurnace(this.world);
      if (furnace != null)
        taken += WorldUtilities.getItemsFromFurnace(furnace, item, toTake - taken); 
      i++;
    } 
    i = 0;
    while (taken < toTake && i < this.resManager.firepits.size()) {
      TileEntityFirePit firepit = (TileEntityFirePit)this.world.getTileEntity(((Point)this.resManager.firepits.get(i)).getBlockPos());
      if (firepit != null)
        taken += WorldUtilities.getItemsFromFirePit(firepit, item, toTake - taken); 
      i++;
    } 
    invalidateInventoryCache();
    return taken;
  }
  
  public void testModeGoods() {
    if (this.isTownhall && !this.villageType.lonebuilding) {
      int stored = storeGoods((Item)MillItems.DENIER_OR, 64);
      if (stored < 64)
        MillLog.error(this, "Should have stored 64 test goods but stored only " + stored); 
      storeGoods((Item)MillItems.SUMMONING_WAND, 1);
      if (this.culture.key.equals("indian")) {
        storeGoods((Item)MillItems.INDIAN_STATUE, 64);
        storeGoods(MillBlocks.BS_MUD_BRICK, 2048);
        storeGoods((Block)MillBlocks.PAINTED_BRICK_WHITE, 0, 2048);
        storeGoods((Block)MillBlocks.PAINTED_BRICK_DECORATED_WHITE, 0, 512);
        storeGoods(Blocks.SANDSTONE, 2048);
        storeGoods(Blocks.STONE, 2048);
        storeGoods(Blocks.COBBLESTONE, 512);
        storeGoods(Blocks.LOG2, 0, 1024);
        storeGoods((Block)MillBlocks.BED_CHARPOY, 0, 64);
        storeGoods((Block)MillBlocks.WOODEN_BARS, 0, 64);
        storeGoods((Block)MillBlocks.WOODEN_BARS_INDIAN, 0, 64);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 2, 512);
      } else if (this.culture.key.equals("mayan")) {
        storeGoods(Blocks.SANDSTONE, 512);
        storeGoods(Blocks.STONE, 3500);
        storeGoods(Blocks.COBBLESTONE, 2048);
        storeGoods((Block)MillBlocks.STONE_DECORATION, 2, 64);
        storeGoods(Blocks.LOG, 1, 512);
        storeGoods(Blocks.LOG, 3, 1024);
      } else if (this.culture.key.equals("japanese")) {
        storeGoods(Blocks.SAPLING, 64);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 2, 2048);
        storeGoods(Blocks.GRAVEL, 512);
        storeGoods((Block)MillBlocks.PAPER_WALL, 2048);
        storeGoods(Blocks.STONE, 2048);
        storeGoods(Blocks.COBBLESTONE, 1024);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 0, 512);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 1, 512);
        storeGoods(Blocks.LOG, 1, 512);
      } else if (this.culture.key.equals("byzantines")) {
        storeGoods(Blocks.GLASS, 512);
        storeGoods(Blocks.COBBLESTONE, 1500);
        storeGoods(Blocks.STONE, 1500);
        storeGoods(Blocks.BRICK_BLOCK, 512);
        storeGoods(Blocks.SANDSTONE, 512);
        storeGoods(Blocks.WOOL, 11, 64);
        storeGoods(Blocks.WOOL, 14, 64);
        storeGoods(Blocks.LOG, 2, 128);
        storeGoods(Blocks.BOOKSHELF, 0, 64);
        storeGoods((Block)MillBlocks.BYZANTINE_TILES, 128);
        storeGoods((Block)MillBlocks.BYZANTINE_TILES_SLAB, 128);
        storeGoods((Block)MillBlocks.BYZANTINE_STONE_TILES, 128);
      } else if (this.culture.key.equals("norman")) {
        storeGoods(Blocks.GLASS, 64);
        storeGoods(Blocks.COBBLESTONE, 2048);
        storeGoods(Blocks.STONE, 3500);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 0, 2048);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 1, 2048);
        storeGoods(Blocks.WOOL, 11, 64);
        storeGoods(Blocks.WOOL, 14, 64);
        storeGoods(Blocks.LOG.getDefaultState().withProperty((IProperty)BlockOldLog.VARIANT, (Comparable)BlockPlanks.EnumType.SPRUCE), 512);
        storeGoods(Blocks.LOG2.getDefaultState().withProperty((IProperty)BlockNewLog.VARIANT, (Comparable)BlockPlanks.EnumType.DARK_OAK), 1024);
        storeGoods((Block)MillBlocks.BED_STRAW, 64);
        storeGoods(MillBlocks.STAINED_GLASS.getDefaultState().withProperty((IProperty)BlockMillStainedGlass.VARIANT, (Comparable)BlockMillStainedGlass.EnumType.WHITE), 64);
        storeGoods(MillBlocks.STAINED_GLASS.getDefaultState().withProperty((IProperty)BlockMillStainedGlass.VARIANT, (Comparable)BlockMillStainedGlass.EnumType.YELLOW), 64);
        storeGoods(MillBlocks.STAINED_GLASS.getDefaultState().withProperty((IProperty)BlockMillStainedGlass.VARIANT, (Comparable)BlockMillStainedGlass.EnumType.YELLOW_RED), 64);
        storeGoods(MillBlocks.STAINED_GLASS.getDefaultState().withProperty((IProperty)BlockMillStainedGlass.VARIANT, (Comparable)BlockMillStainedGlass.EnumType.GREEN_BLUE), 64);
        storeGoods(MillBlocks.STAINED_GLASS.getDefaultState().withProperty((IProperty)BlockMillStainedGlass.VARIANT, (Comparable)BlockMillStainedGlass.EnumType.RED_BLUE), 64);
        storeGoods((Block)MillBlocks.ROSETTE, 64);
        storeGoods((Block)MillBlocks.BED_STRAW, 64);
      } else if (this.culture.key.equals("inuits")) {
        storeGoods((Block)MillBlocks.ICE_BRICK, 128);
        storeGoods((Block)MillBlocks.SNOW_BRICK, 1024);
        storeGoods((Block)MillBlocks.SNOW_WALL, 128);
        storeGoods(Blocks.LOG, 1, 1024);
        storeGoods(Blocks.LOG, 2, 1024);
        storeGoods(Blocks.BONE_BLOCK, 256);
        storeGoods((Block)MillBlocks.INUIT_CARVING, 4, 64);
      } else {
        storeGoods(Blocks.GLASS, 512);
        storeGoods(Blocks.COBBLESTONE, 2048);
        storeGoods(Blocks.STONE, 3500);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 0, 2048);
        storeGoods((Block)MillBlocks.WOOD_DECORATION, 1, 2048);
        storeGoods(Blocks.WOOL, 11, 64);
        storeGoods(Blocks.WOOL, 14, 64);
      } 
      storeGoods(Blocks.LOG, 1024);
      storeGoods(Items.IRON_INGOT, 256);
      storeGoods(Blocks.WOOL, 64);
      storeGoods(Blocks.GRAVEL, 64);
      storeGoods((Block)Blocks.SAND, 64);
      storeGoods((Block)MillBlocks.BED_STRAW, 0, 128);
    } 
  }
  
  public void testShowGroundLevel() {
    for (int i = 0; i < this.winfo.length; i++) {
      for (int j = 0; j < this.winfo.width; j++) {
        Point p = new Point((this.winfo.mapStartX + i), (this.winfo.topGround[i][j] - 1), (this.winfo.mapStartZ + j));
        if (WorldUtilities.getBlock(this.world, p) != MillBlocks.LOCKED_CHEST)
          if (!this.winfo.topAdjusted[i][j]) {
            WorldUtilities.setBlockAndMetadata(this.world, p, Blocks.WOOL, this.regionMapper.regions[i][j] % 16);
          } else {
            WorldUtilities.setBlockAndMetadata(this.world, p, Blocks.IRON_BLOCK, 0);
          }  
      } 
    } 
  }
  
  public String toString() {
    if (this.location != null)
      return "(" + this.location + "/" + getVillageQualifiedName() + "/" + this.world + ")"; 
    return super.toString();
  }
  
  public void transferVillagerPermanently(VillagerRecord vr, Building dest) {
    if (MillConfigValues.LogDiplomacy >= 2)
      MillLog.minor(this, "Transfering villager " + vr + " permanently to " + dest + "."); 
    this.mw.removeVillagerRecord(vr.getVillagerId());
    vr.setHousePos(dest.getPos());
    vr.setTownHallPos(dest.getTownHall().getPos());
    this.mw.registerVillagerRecord(vr, true);
    MillVillager v = this.mw.getVillagerById(vr.getVillagerId());
    if (v != null) {
      this.villagers.remove(v);
      (getTownHall()).villagers.remove(v);
      if ((dest.getTownHall()).isActive) {
        v.setHousePoint(dest.getPos());
        v.setTownHallPoint(dest.getTownHall().getPos());
        v.isRaider = false;
        v.setPosition(dest.resManager.getSleepingPos().getiX(), dest.resManager.getSleepingPos().getiY(), dest.resManager.getSleepingPos().getiZ());
      } else {
        v.despawnVillager();
        if (MillConfigValues.LogDiplomacy >= 3)
          MillLog.debug(this, "Villager entity despawned."); 
      } 
    } 
    dest.getTownHall().saveTownHall("incoming villager");
  }
  
  private void triggerCompletionAdvancements() {
    if (this.buildingGoal != null)
      return; 
    if (!this.villageType.isRegularVillage())
      return; 
    EntityPlayer player = this.world.getClosestPlayer(this.pos.getiX(), this.pos.getiY(), this.pos.getiZ(), 5.0D, false);
    if (player == null)
      return; 
    if (getReputation(player) > 32768) {
      String cultureKey = this.culture.key.toLowerCase();
      if (MillAdvancements.COMPLETE_ADVANCEMENTS.containsKey(cultureKey))
        ((GenericAdvancement)MillAdvancements.COMPLETE_ADVANCEMENTS.get(cultureKey)).grant(player); 
    } 
  }
  
  private void unloadChunks() {
    if (this.chunkLoader != null && this.chunkLoader.chunksLoaded)
      this.chunkLoader.unloadChunks(); 
  }
  
  public void unlockAllChests() {
    this.chestLocked = false;
    for (Point p : this.buildings) {
      Building b = this.mw.getBuilding(p);
      if (b != null)
        b.unlockChests(); 
    } 
    if (countGoods((Item)MillItems.NEGATION_WAND) == 0)
      storeGoods((Item)MillItems.NEGATION_WAND, 1); 
  }
  
  public void unlockChests() {
    if (!this.isMarket) {
      this.chestLocked = false;
      for (Point p : this.resManager.chests) {
        TileEntityLockedChest chest = p.getMillChest(this.world);
        if (chest != null)
          chest.buildingPos = getPos(); 
      } 
    } 
  }
  
  private void unlockForNearbyPlayers() {
    Point p = this.resManager.getSellingPos();
    if (p == null)
      p = this.pos; 
    List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((this.location.minx - 2), (this.location.miny - 2), (this.location.minz - 2), (this.location.maxx + 2), (this.location.maxy + 2), (this.location.maxz + 2)), null);
    for (EntityPlayer player : players) {
      UserProfile profile = this.mw.getProfile(player);
      if (profile != null) {
        profile.unlockBuilding(this.culture, this.culture.getBuildingPlanSet(this.location.planKey));
        if (getTownHall() != null)
          profile.unlockVillage(this.culture, (getTownHall()).villageType); 
      } 
    } 
  }
  
  private void updateAchievements() {
    if (this.villageType == null)
      return; 
    List<Entity> players = WorldUtilities.getEntitiesWithinAABB(this.world, EntityPlayer.class, getPos(), this.villageType.radius, 20);
    for (Entity ent : players) {
      EntityPlayer player = (EntityPlayer)ent;
      if (this.villageType.lonebuilding)
        MillAdvancements.EXPLORER.grant(player); 
      if (containsTags("hof"))
        MillAdvancements.PANTHEON.grant(player); 
      int nbcultures = this.mw.nbCultureInGeneratedVillages();
      if (nbcultures >= 3)
        MillAdvancements.MARCO_POLO.grant(player); 
      if (nbcultures >= Culture.ListCultures.size())
        MillAdvancements.MAGELLAN.grant(player); 
    } 
    if (this.controlledBy != null && getVillagerRecords().size() >= 100) {
      EntityPlayer player = this.world.getPlayerEntityByUUID(this.controlledBy);
      if (player != null)
        MillAdvancements.MEDIEVAL_METROPOLIS.grant(player); 
    } 
  }
  
  private void updateAutoSpawn() {
    if (!this.world.isDaytime() && (
      this.location.getMaleResidents().size() > 0 || this.location.getFemaleResidents().size() > 0) && getVillagerRecords().size() == 0)
      try {
        createResidents();
      } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
        MillLog.printException("Exception when auto-spawning villagers for " + this + ":", (Throwable)e);
      }  
  }
  
  public void updateBackgroundVillage() {
    if (this.world.isRemote)
      return; 
    if (this.villageType == null || !this.isTownhall || this.location == null)
      return; 
    EntityPlayer player = this.world.getClosestPlayer(this.pos.getiX(), this.pos.getiY(), this.pos.getiZ(), MillConfigValues.BackgroundRadius, false);
    if (player != null)
      if (this.world.isDaytime()) {
        this.nightBackgroundActionPerformed = false;
      } else if (!this.nightBackgroundActionPerformed) {
        if (this.villageType.carriesRaid && this.raidTarget == null && MillCommonUtilities.randomInt(100) < MillConfigValues.RaidingRate) {
          if (MillConfigValues.LogDiplomacy >= 3)
            MillLog.debug(this, "Calling attemptPlanNewRaid"); 
          attemptPlanNewRaid();
        } 
        this.nightBackgroundActionPerformed = true;
      }  
    if (this.world.getWorldTime() % 24000L > 23500L && this.raidTarget != null) {
      if (!this.updateRaidPerformed) {
        if (MillConfigValues.LogDiplomacy >= 3)
          MillLog.debug(this, "Calling updateRaid for raid: " + this.raidPlanningStart + "/" + this.raidStart + "/" + this.world.getWorldTime()); 
        updateRaid();
        this.updateRaidPerformed = true;
      } 
    } else {
      this.updateRaidPerformed = false;
    } 
  }
  
  public void updateBanners() {
    if ((getTownHall()).bannerStack == ItemStack.EMPTY)
      getTownHall().generateBannerPattern(); 
    for (Point p : this.resManager.banners) {
      TileEntity te = this.mw.world.getTileEntity(p.getBlockPos());
      if (te instanceof TileEntityBanner) {
        NBTTagCompound bannerCompound = (getTownHall()).bannerStack.getSubCompound("BlockEntityTag");
        boolean getBaseColorFromNBT = (bannerCompound != null) ? bannerCompound.hasKey("Base") : false;
        ((TileEntityBanner)te).setItemValues((getTownHall()).bannerStack, getBaseColorFromNBT);
        this.mw.world.notifyBlockUpdate(te.getPos(), this.mw.world.getBlockState(te.getPos()), this.mw.world.getBlockState(te.getPos()), 3);
      } 
    } 
    for (Point p : this.resManager.cultureBanners) {
      TileEntity te = this.mw.world.getTileEntity(p.getBlockPos());
      if (te instanceof TileEntityBanner) {
        ((TileEntityBanner)te).setItemValues(this.culture.cultureBannerItemStack, true);
        this.mw.world.notifyBlockUpdate(te.getPos(), this.mw.world.getBlockState(te.getPos()), this.mw.world.getBlockState(te.getPos()), 3);
      } 
    } 
  }
  
  public void updateBuildingClient() {
    if ((this.world.getWorldTime() + hashCode()) % 20L == 8L)
      rebuildVillagerList(); 
    if (this.isActive && this.isTownhall && (this.world.getWorldTime() + hashCode()) % 100L == 48L)
      triggerCompletionAdvancements(); 
  }
  
  public void updateBuildingServer() {
    if (this.world.isRemote)
      return; 
    if (this.mw.getBuilding(this.pos) != this)
      MillLog.error(this, "Other building registered in my place: " + this.mw.getBuilding(this.pos)); 
    if (this.location == null)
      return; 
    if (this.isActive && (this.world.getWorldTime() + hashCode()) % 40L == 15L)
      rebuildVillagerList(); 
    if (this.isActive && (this.world.getWorldTime() + hashCode()) % 100L == 48L)
      triggerCompletionAdvancements(); 
    EntityPlayer player = this.world.getClosestPlayer(this.pos.getiX(), this.pos.getiY(), this.pos.getiZ(), MillConfigValues.KeepActiveRadius, false);
    if (this.isTownhall) {
      if (player != null && getPos().distanceTo((Entity)player) < MillConfigValues.KeepActiveRadius) {
        loadChunks();
      } else if (player == null || getPos().distanceTo((Entity)player) > (MillConfigValues.KeepActiveRadius + 32)) {
        unloadChunks();
      } 
      this.isAreaLoaded = isVillageChunksLoaded();
      if (this.isActive && !this.isAreaLoaded) {
        this.isActive = false;
        for (Object o : this.world.playerEntities) {
          EntityPlayer p = (EntityPlayer)o;
          sendBuildingPacket(p, false);
        } 
        if (MillConfigValues.LogChunkLoader >= 1)
          MillLog.major(this, "Becoming inactive"); 
        saveTownHall("becoming inactive");
      } else if (!this.isActive && this.isAreaLoaded) {
        for (Object o : this.world.playerEntities) {
          EntityPlayer p = (EntityPlayer)o;
          sendBuildingPacket(p, false);
        } 
        if (MillConfigValues.LogChunkLoader >= 1)
          MillLog.major(this, "Becoming active"); 
        this.isActive = true;
      } 
      if (!this.isActive)
        return; 
    } else if (getTownHall() == null || !(getTownHall()).isActive) {
      return;
    } 
    if (this.location == null)
      return; 
    try {
      if (this.isTownhall && this.villageType != null)
        updateTownHall(); 
      if (containsTags("grove"))
        updateGrove(); 
      if (this.resManager.spawns.size() > 0)
        updatePens(false); 
      if (this.resManager.healingspots.size() > 0)
        updateHealingSpots(); 
      if (this.resManager.mobSpawners.size() > 0 && player != null && this.pos.distanceToSquared((Entity)player) < 400.0D)
        updateMobSpawners(); 
      if (this.resManager.dispenderUnknownPowder.size() > 0)
        updateDispensers(); 
      if (this.resManager.netherwartsoils.size() > 0)
        updateNetherWartSoils(); 
      if (this.isInn)
        updateInn(); 
      if (this.hasVisitors)
        getVisitorManager().update(false); 
      if (this.hasAutoSpawn)
        updateAutoSpawn(); 
      if (Math.abs(this.world.getWorldTime() + hashCode()) % 20L == 4L)
        unlockForNearbyPlayers(); 
      getPanelManager().updateSigns();
      if (this.isTownhall)
        if (this.saveNeeded) {
          saveTownHall("Save needed");
        } else if (this.world.getWorldTime() - this.lastSaved > 1000L) {
          saveTownHall("Delay up");
        }  
      if (player != null && this.location.getPlan() != null && (this.location.getPlan()).exploreTag != null)
        checkExploreTag(player); 
      sendInitialBuildingPackets();
      if (MillCommonUtilities.chanceOn(100))
        for (Point p : this.resManager.chests) {
          if (p.getMillChest(this.world) != null)
            (p.getMillChest(this.world)).buildingPos = getPos(); 
        }  
      refreshGoods();
    } catch (Exception e) {
      int nbRepeats = MillLog.printException("Exception in TileEntityBuilding.onUpdate() for building " + this + ": ", e);
      if (nbRepeats < 5)
        Mill.proxy.sendChatAdmin(LanguageUtilities.string("ui.updateEntity")); 
    } 
  }
  
  private boolean updateConstructionQueue(boolean ignoreCost) {
    boolean change = false;
    if (MillConfigValues.ignoreResourceCost)
      ignoreCost = true; 
    change = findBuildingProject();
    change |= findBuildingConstruction(ignoreCost);
    if (getSimultaneousWallConstructionSlots() > 0)
      change |= findBuildingConstructionWall(ignoreCost); 
    return change;
  }
  
  private void updateDispensers() {
    for (Point p : this.resManager.dispenderUnknownPowder) {
      if (MillCommonUtilities.chanceOn(5000)) {
        TileEntityDispenser dispenser = p.getDispenser(this.world);
        if (dispenser != null)
          MillCommonUtilities.putItemsInChest((IInventory)dispenser, (Item)MillItems.UNKNOWN_POWDER, 1); 
      } 
    } 
  }
  
  private void updateGrove() {
    for (Point p : this.resManager.woodspawn) {
      if (MillCommonUtilities.chanceOn(4000) && WorldUtilities.getBlock(this.world, p) == Blocks.SAPLING)
        growTree(this.world, p.getiX(), p.getiY(), p.getiZ(), MillCommonUtilities.random); 
    } 
  }
  
  private void updateHealingSpots() {
    if (this.world.getWorldTime() % 100L == 0L)
      for (Point p : this.resManager.healingspots) {
        EntityPlayer player = this.world.getClosestPlayer(p.getiX(), p.getiY(), p.getiZ(), 4.0D, false);
        if (player != null && player.getHealth() < player.getMaxHealth()) {
          player.setHealth(player.getHealth() + 1.0F);
          ServerSender.sendTranslatedSentence(player, 'a', "other.buildinghealing", new String[] { getNativeBuildingName() });
        } 
      }  
  }
  
  private void updateInn() {
    if (this.world.isDaytime()) {
      this.nightActionPerformed = false;
    } else if (!this.nightActionPerformed) {
      if (this.merchantRecord != null) {
        this.nbNightsMerchant++;
        if (this.nbNightsMerchant > 1)
          attemptMerchantMove(false); 
      } 
      this.nightActionPerformed = true;
    } 
  }
  
  private void updateMobSpawners() {
    for (int i = 0; i < this.resManager.mobSpawners.size(); i++) {
      for (int j = 0; j < ((CopyOnWriteArrayList)this.resManager.mobSpawners.get(i)).size(); j++) {
        if (MillCommonUtilities.chanceOn(180)) {
          Block block = WorldUtilities.getBlock(this.world, ((CopyOnWriteArrayList<Point>)this.resManager.mobSpawners.get(i)).get(j));
          if (block == Blocks.MOB_SPAWNER) {
            Class<? extends Entity> targetClass = EntityList.getClass(this.resManager.mobSpawnerTypes.get(i));
            List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(this.world, targetClass, ((CopyOnWriteArrayList<Point>)this.resManager.mobSpawners.get(i)).get(j), 10, 5);
            int nbmob = mobs.size();
            if (nbmob < 4)
              WorldUtilities.spawnMobsSpawner(this.world, ((CopyOnWriteArrayList<Point>)this.resManager.mobSpawners.get(i)).get(j), this.resManager.mobSpawnerTypes.get(i)); 
          } 
        } 
      } 
    } 
  }
  
  private void updateNetherWartSoils() {
    for (Point p : this.resManager.netherwartsoils) {
      if (MillCommonUtilities.chanceOn(1000) && 
        WorldUtilities.getBlock(this.world, p.getAbove()) == Blocks.NETHER_WART) {
        int meta = WorldUtilities.getBlockMeta(this.world, p.getAbove());
        if (meta < 3)
          WorldUtilities.setBlockMetadata(this.world, p.getAbove(), meta + 1); 
      } 
    } 
  }
  
  private void updatePens(boolean completeRespawn) {
    if ((completeRespawn || !this.world.isDaytime()) && (getVillagerRecords().size() > 0 || (this.location.getMaleResidents().isEmpty() && this.location.getFemaleResidents().isEmpty())) && !this.world.isRemote) {
      int nbMaxRespawn = 0;
      for (List<Point> spawnPoints : this.resManager.spawns)
        nbMaxRespawn += spawnPoints.size(); 
      if (this.nbAnimalsRespawned <= nbMaxRespawn) {
        int sheep = 0, cow = 0, pig = 0, chicken = 0, squid = 0, wolves = 0;
        List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(this.world, EntityAnimal.class, getPos(), 15, 20);
        for (Entity animal : animals) {
          if (animal instanceof net.minecraft.entity.passive.EntitySheep) {
            sheep++;
            continue;
          } 
          if (animal instanceof net.minecraft.entity.passive.EntityPig) {
            pig++;
            continue;
          } 
          if (animal instanceof net.minecraft.entity.passive.EntityCow) {
            cow++;
            continue;
          } 
          if (animal instanceof net.minecraft.entity.passive.EntityChicken) {
            chicken++;
            continue;
          } 
          if (animal instanceof net.minecraft.entity.passive.EntitySquid) {
            squid++;
            continue;
          } 
          if (animal instanceof net.minecraft.entity.passive.EntityWolf)
            wolves++; 
        } 
        for (int i = 0; i < this.resManager.spawns.size(); i++) {
          int nb = 0;
          if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_SHEEP)) {
            nb = sheep;
          } else if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_CHICKEN)) {
            nb = chicken;
          } else if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_PIG)) {
            nb = pig;
          } else if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_COW)) {
            nb = cow;
          } else if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_SQUID)) {
            nb = squid;
          } else if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_WOLF)) {
            nb = wolves;
          } 
          int multipliyer = 1;
          if (((ResourceLocation)this.resManager.spawnTypes.get(i)).equals(Mill.ENTITY_SQUID))
            multipliyer = 2; 
          for (int j = 0; j < this.resManager.spawns.get(i).size() * multipliyer - nb; j++) {
            if (completeRespawn || MillCommonUtilities.chanceOn(100)) {
              EntityLiving animal = (EntityLiving)EntityList.createEntityByIDFromName(this.resManager.spawnTypes.get(i), this.world);
              Point pen = this.resManager.spawns.get(i).get(MillCommonUtilities.randomInt(this.resManager.spawns.get(i).size()));
              animal.setPosition(pen.getiX() + 0.5D, pen.getiY(), pen.getiZ() + 0.5D);
              this.world.spawnEntity((Entity)animal);
              this.nbAnimalsRespawned++;
            } 
          } 
        } 
      } 
    } else {
      this.nbAnimalsRespawned = 0;
    } 
  }
  
  private void updateRaid() {
    if (this.world.getWorldTime() > this.raidPlanningStart + 24000L && this.raidStart == 0L) {
      if (MillConfigValues.LogDiplomacy >= 2)
        MillLog.minor(this, "Starting raid on " + this.mw.getBuilding(this.raidTarget)); 
      startRaid();
    } else if (this.raidStart > 0L && this.world.getWorldTime() > this.raidStart + 23000L) {
      Building distantVillage = this.mw.getBuilding(this.raidTarget);
      if (distantVillage != null) {
        if (!distantVillage.isActive)
          endRaid(); 
      } else {
        cancelRaid();
        for (VillagerRecord vr : getVillagerRecords().values())
          vr.awayraiding = false; 
      } 
    } 
  }
  
  private void updateTownHall() throws MillLog.MillenaireException {
    if (getVillagerRecords().size() > 0)
      updateWorldInfo(); 
    if (MillConfigValues.autoConvertProfiles && !Mill.proxy.isTrueServer() && this.villageType.playerControlled && Mill.proxy.getTheSinglePlayer() != null && (
      this.controlledBy == null || !this.controlledBy.equals(Mill.proxy.getTheSinglePlayer().getUniqueID()))) {
      UUID oldControlledBy = this.controlledBy;
      this.controlledBy = Mill.proxy.getTheSinglePlayer().getUniqueID();
      this.controlledByName = Mill.proxy.getTheSinglePlayer().getName();
      MillLog.major(this, "Switched controller from " + oldControlledBy + " to " + this.controlledBy + " (" + this.controlledByName + "), the new single player.");
    } 
    this.closestPlayer = this.world.getClosestPlayer(this.pos.getiX(), this.pos.getiY(), this.pos.getiZ(), 100.0D, false);
    for (ConstructionIP cip : getConstructionsInProgress())
      completeConstruction(cip); 
    if ((this.world.getWorldTime() + hashCode()) % 20L == 3L)
      updateConstructionQueue(false); 
    checkSeller();
    checkWorkers();
    if ((this.world.getWorldTime() + hashCode()) % 10L == 0L)
      checkBattleStatus(); 
    if ((this.world.getWorldTime() + hashCode()) % 10L == 5L)
      killMobs(); 
    if (!this.declaredPos && this.world != null) {
      if (this.villageType.lonebuilding) {
        this.mw.registerLoneBuildingsLocation(this.world, getPos(), getVillageQualifiedName(), this.villageType, this.culture, false, null);
      } else {
        this.mw.registerVillageLocation(this.world, getPos(), getVillageQualifiedName(), this.villageType, this.culture, false, null);
      } 
      this.declaredPos = true;
    } 
    if (this.lastVillagerRecordsRepair == 0L) {
      this.lastVillagerRecordsRepair = this.world.getWorldTime();
    } else if (this.world.getWorldTime() - this.lastVillagerRecordsRepair >= 100L) {
      respawnVillagersIfNeeded();
      this.lastVillagerRecordsRepair = this.world.getWorldTime();
    } 
    if (this.world.isDaytime()) {
      this.nightActionPerformed = false;
    } else if (!this.nightActionPerformed) {
      if (!this.villageType.playerControlled && !this.villageType.lonebuilding) {
        for (EntityPlayer player : VillageUtilities.getServerPlayers(this.world)) {
          UserProfile profile = VillageUtilities.getServerProfile(this.world, player);
          if (profile != null)
            profile.adjustDiplomacyPoint(this, 5); 
        } 
        for (Point p : this.relations.keySet()) {
          if (MillCommonUtilities.chanceOn(10)) {
            Building village = this.mw.getBuilding(p);
            if (village != null) {
              int improveChance, relation = ((Integer)this.relations.get(p)).intValue();
              if (relation < -90) {
                improveChance = 0;
              } else if (relation < -50) {
                improveChance = 30;
              } else if (relation < 0) {
                improveChance = 40;
              } else if (relation > 90) {
                improveChance = 100;
              } else if (relation > 50) {
                improveChance = 70;
              } else {
                improveChance = 60;
              } 
              if (MillCommonUtilities.randomInt(100) < improveChance) {
                if (((Integer)this.relations.get(p)).intValue() < 100) {
                  adjustRelation(p, 10 + MillCommonUtilities.randomInt(10), false);
                  ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.KeepActiveRadius, '2', "ui.relationfriendly", new String[] { getVillageQualifiedName(), village.getVillageQualifiedName(), VillageUtilities.getRelationName(((Integer)this.relations.get(p)).intValue()) });
                } 
                continue;
              } 
              if (((Integer)this.relations.get(p)).intValue() > -100) {
                adjustRelation(p, -10 - MillCommonUtilities.randomInt(10), false);
                ServerSender.sendTranslatedSentenceInRange(this.world, getPos(), MillConfigValues.KeepActiveRadius, '6', "ui.relationunfriendly", new String[] { getVillageQualifiedName(), village.getVillageQualifiedName(), VillageUtilities.getRelationName(((Integer)this.relations.get(p)).intValue()) });
              } 
            } 
          } 
        } 
      } 
      this.nightActionPerformed = true;
    } 
    if (this.world.getWorldTime() % 1000L == 0L && (
      this.villageType.playerControlled || MillConfigValues.DEV) && countGoods((Item)MillItems.PARCHMENT_VILLAGE_SCROLL, 0) == 0)
      for (int i = 0; i < this.mw.villagesList.pos.size(); i++) {
        Point p = this.mw.villagesList.pos.get(i);
        if (getPos().sameBlock(p))
          storeItemStack(ItemParchment.createParchmentForVillage(this)); 
      }  
    if (this.villageType.playerControlled && this.world.getWorldTime() % 1000L == 0L && countGoods((Item)MillItems.NEGATION_WAND) == 0)
      storeGoods((Item)MillItems.NEGATION_WAND, 1); 
    if (this.controlledBy != null && this.controlledByName == null) {
      GameProfile profile = this.world.getMinecraftServer().getPlayerProfileCache().getProfileByUUID(this.controlledBy);
      if (profile != null)
        this.controlledByName = profile.getName(); 
    } 
    if (this.world.getWorldTime() % 200L == 0L)
      updateAchievements(); 
    handlePathingResult();
    if (this.autobuildPaths) {
      clearOldPaths();
      constructCalculatedPaths();
    } 
    if (this.marvelManager != null)
      this.marvelManager.update(); 
  }
  
  public void updateWorldInfo() throws MillLog.MillenaireException {
    if (this.villageType == null) {
      MillLog.error(this, "updateWorldInfo: villageType is null");
      return;
    } 
    List<BuildingLocation> locations = new ArrayList<>();
    for (BuildingLocation l : getLocations())
      locations.add(l); 
    for (ConstructionIP cip : getConstructionsInProgress()) {
      if (cip.getBuildingLocation() != null)
        locations.add(cip.getBuildingLocation()); 
    } 
    if (this.winfo.world == null) {
      boolean areaChanged = this.winfo.update(this.world, locations, this.location.pos, this.villageType.radius);
      if (areaChanged)
        rebuildRegionMapper(true); 
    } else {
      this.winfo.updateNextChunk();
    } 
  }
  
  private void validateVillagerList() {
    for (MillVillager v : getKnownVillagers()) {
      if (v == null)
        MillLog.error(this, "Null value in villager list"); 
      if (v.isReallyDead() && MillConfigValues.LogTileEntityBuilding >= 2)
        MillLog.minor(this, "Villager " + v + " is dead."); 
      List<VillagerRecord> found = new ArrayList<>();
      for (VillagerRecord vr : getVillagerRecords().values()) {
        if (vr.matches(v))
          found.add(vr); 
      } 
      if (found.size() == 0) {
        MillLog.error(this, "Villager " + v + " not present in records.");
        continue;
      } 
      if (found.size() > 1) {
        MillLog.error(this, "Villager " + v + " present " + found.size() + " times in records: ");
        for (VillagerRecord vr : found)
          MillLog.major(this, vr.toString() + " / " + vr.hashCode()); 
      } 
    } 
    for (VillagerRecord vr : getVillagerRecords().values()) {
      List<MillVillager> found = new ArrayList<>();
      if (vr.getHousePos() == null)
        MillLog.error(this, "Record " + vr + " has no house."); 
      for (MillVillager v : getKnownVillagers()) {
        if (vr.matches(v))
          found.add(v); 
      } 
      if (found.size() != vr.nb) {
        MillLog.error(this, "Record " + vr + " present " + found.size() + " times in villagers, previously: " + vr.nb + ". Villagers: ");
        for (MillVillager v : found)
          MillLog.major(this, v.toString() + " / " + v.hashCode()); 
        vr.nb = found.size();
      } 
    } 
  }
  
  private void writePaths() {
    File buildingsDir = MillCommonUtilities.getBuildingsDir(this.world);
    File file1 = new File(buildingsDir, getPos().getPathString() + "_paths.bin");
    if (this.pathsToBuild != null) {
      try {
        FileOutputStream fos = new FileOutputStream(file1);
        DataOutputStream ds = new DataOutputStream(fos);
        ds.writeInt(this.pathsToBuild.size());
        for (List<BuildingBlock> path : this.pathsToBuild) {
          ds.writeInt(path.size());
          for (BuildingBlock b : path) {
            ds.writeInt(b.p.getiX());
            ds.writeShort(b.p.getiY());
            ds.writeInt(b.p.getiZ());
            ds.writeInt(Block.getIdFromBlock(b.block));
            ds.writeByte(b.getMeta());
            ds.writeByte(b.special);
          } 
        } 
        ds.close();
        fos.close();
      } catch (IOException e) {
        MillLog.printException("Error when writing pathsToBuild: ", e);
      } 
    } else {
      file1.renameTo(new File(buildingsDir, getPos().getPathString() + "ToDelete"));
      file1.delete();
    } 
    file1 = new File(buildingsDir, getPos().getPathString() + "_pathstoclear.bin");
    if (this.oldPathPointsToClear != null) {
      try {
        FileOutputStream fos = new FileOutputStream(file1);
        DataOutputStream ds = new DataOutputStream(fos);
        ds.writeInt(this.oldPathPointsToClear.size());
        for (Point p : this.oldPathPointsToClear) {
          ds.writeInt(p.getiX());
          ds.writeShort(p.getiY());
          ds.writeInt(p.getiZ());
        } 
        ds.close();
        fos.close();
      } catch (IOException e) {
        MillLog.printException("Error when writing oldPathPointsToClear: ", e);
      } 
    } else {
      file1.delete();
    } 
    this.pathsChanged = false;
  }
  
  public void writeToNBT(NBTTagCompound nbttagcompound) {
    if (this.location == null) {
      MillLog.error(this, "Null location. Skipping write.");
      return;
    } 
    nbttagcompound.setString("versionCompatibility", "1.0");
    try {
      this.pos.write(nbttagcompound, "pos");
      this.location.writeToNBT(nbttagcompound, "buildingLocation", "self");
      nbttagcompound.setBoolean("chestLocked", this.chestLocked);
      if (this.name != null && this.name.length() > 0)
        nbttagcompound.setString("name", this.name); 
      nbttagcompound.setString("qualifier", getQualifier());
      nbttagcompound.setBoolean("isTownhall", this.isTownhall);
      nbttagcompound.setString("culture", this.culture.key);
      if (this.villageType != null)
        nbttagcompound.setString("villageType", this.villageType.key); 
      if (this.controlledBy != null)
        nbttagcompound.setUniqueId("controlledByUUID", this.controlledBy); 
      if (getTownHallPos() != null)
        getTownHallPos().write(nbttagcompound, "townHallPos"); 
      nbttagcompound.setBoolean("nightActionPerformed", this.nightActionPerformed);
      nbttagcompound.setBoolean("nightBackgroundActionPerformed", this.nightBackgroundActionPerformed);
      nbttagcompound.setInteger("nbAnimalsRespawned", this.nbAnimalsRespawned);
      NBTTagList nbttaglist = new NBTTagList();
      for (Point p : this.buildings) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        p.write(nbttagcompound1, "pos");
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      nbttagcompound.setTag("buildings", (NBTBase)nbttaglist);
      for (ConstructionIP cip : getConstructionsInProgress()) {
        nbttagcompound.setInteger("bblocksPos_" + cip.getId(), cip.getBblocksPos());
        if (cip.isBblocksChanged()) {
          cip.writeBblocks();
          if (MillConfigValues.LogHybernation >= 1)
            MillLog.major(this, "Saved bblocks."); 
        } 
      } 
      nbttaglist = new NBTTagList();
      for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
        if (this.buildingProjects.containsKey(ep)) {
          CopyOnWriteArrayList<BuildingProject> projectsLevel = this.buildingProjects.get(ep);
          for (BuildingProject project : projectsLevel) {
            if (project.location != null && !project.location.isSubBuildingLocation) {
              NBTTagCompound nbttagcompound1 = new NBTTagCompound();
              project.location.writeToNBT(nbttagcompound1, "location", "buildingProjects");
              nbttaglist.appendTag((NBTBase)nbttagcompound1);
              if (MillConfigValues.LogHybernation >= 1)
                MillLog.major(this, "Writing building location: " + project.location + " (level: " + project.location.level + ", variation: " + project.location.getVariation() + ")"); 
            } 
          } 
          for (BuildingProject project : projectsLevel) {
            if (project.location != null && project.location.isSubBuildingLocation) {
              NBTTagCompound nbttagcompound1 = new NBTTagCompound();
              project.location.writeToNBT(nbttagcompound1, "location", "buildingProjects");
              nbttaglist.appendTag((NBTBase)nbttagcompound1);
              if (MillConfigValues.LogHybernation >= 1)
                MillLog.major(this, "Writing building location: " + project.location + " (level: " + project.location.level + ", variation: " + project.location.getVariation() + ")"); 
            } 
          } 
        } 
      } 
      nbttagcompound.setTag("locations", (NBTBase)nbttaglist);
      if (this.buildingGoal != null) {
        nbttagcompound.setString("buildingGoal", this.buildingGoal);
        if (MillConfigValues.LogHybernation >= 1)
          MillLog.major(this, "Writing building goal: " + this.buildingGoal); 
      } 
      nbttagcompound.setInteger("buildingGoalLevel", this.buildingGoalLevel);
      nbttagcompound.setInteger("buildingGoalVariation", this.buildingGoalVariation);
      if (this.buildingGoalIssue != null)
        nbttagcompound.setString("buildingGoalIssue", this.buildingGoalIssue); 
      if (this.buildingGoalLocation != null) {
        this.buildingGoalLocation.writeToNBT(nbttagcompound, "buildingGoalLocation", "buildingGoalLocation");
        if (MillConfigValues.LogHybernation >= 1)
          MillLog.major(this, "Writing buildingGoalLocation: " + this.buildingGoalLocation); 
      } 
      nbttagcompound.setInteger("nbConstructions", getConstructionsInProgress().size());
      for (ConstructionIP cip : getConstructionsInProgress()) {
        nbttagcompound.setBoolean("buildingLocationIP_" + cip.getId() + "_isWall", cip.isWallConstruction());
        if (cip.getBuildingLocation() != null) {
          cip.getBuildingLocation().writeToNBT(nbttagcompound, "buildingLocationIP_" + cip.getId(), "buildingLocationIP_" + cip.getId());
          if (MillConfigValues.LogHybernation >= 1)
            MillLog.major(this, "Writing buildingLocationIP_" + cip.getId() + ": " + cip.getBuildingLocation()); 
        } 
      } 
      nbttaglist = new NBTTagList();
      for (String s : this.visitorsList) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setString("visitor", s);
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      nbttagcompound.setTag("visitorsList", (NBTBase)nbttaglist);
      nbttaglist = new NBTTagList();
      for (String s : this.buildingsBought) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setString("key", s);
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      nbttagcompound.setTag("buildingsBought", (NBTBase)nbttaglist);
      nbttagcompound.setBoolean("updateRaidPerformed", this.updateRaidPerformed);
      nbttagcompound.setBoolean("nightBackgroundActionPerformed", this.nightBackgroundActionPerformed);
      nbttagcompound.setBoolean("nightActionPerformed", this.nightActionPerformed);
      nbttagcompound.setBoolean("underAttack", this.underAttack);
      if (this.raidTarget != null) {
        this.raidTarget.write(nbttagcompound, "raidTarget");
        nbttagcompound.setLong("raidPlanningStart", this.raidPlanningStart);
        nbttagcompound.setLong("raidStart", this.raidStart);
      } 
      nbttaglist = new NBTTagList();
      for (String s : this.raidsPerformed) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setString("raid", s);
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      nbttagcompound.setTag("raidsPerformed", (NBTBase)nbttaglist);
      nbttaglist = new NBTTagList();
      for (String s : this.raidsSuffered) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setString("raid", s);
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      nbttagcompound.setTag("raidsTaken", (NBTBase)nbttaglist);
      if (this.villageType != null && !this.villageType.lonebuilding) {
        nbttaglist = new NBTTagList();
        for (Point p : this.relations.keySet()) {
          Building dv = this.mw.getBuilding(p);
          if (dv != null && dv.villageType == null) {
            MillLog.error(dv, "No village type!");
            continue;
          } 
          if (dv != null && !dv.villageType.lonebuilding) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            p.write(nbttagcompound1, "pos");
            nbttagcompound1.setInteger("value", ((Integer)this.relations.get(p)).intValue());
            nbttaglist.appendTag((NBTBase)nbttagcompound1);
          } 
        } 
        nbttagcompound.setTag("relations", (NBTBase)nbttaglist);
      } 
      if (this.parentVillage != null)
        this.parentVillage.write(nbttagcompound, "parentVillage"); 
      nbttaglist = MillCommonUtilities.writeInventory(this.imported);
      nbttagcompound.setTag("importedGoodsNew", (NBTBase)nbttaglist);
      nbttaglist = MillCommonUtilities.writeInventory(this.exported);
      nbttagcompound.setTag("exportedGoodsNew", (NBTBase)nbttaglist);
      if (MillConfigValues.LogTileEntityBuilding >= 3)
        MillLog.debug(this, "Saving building. Location: " + this.location + ", pos: " + getPos()); 
      nbttaglist = new NBTTagList();
      for (Point p : this.subBuildings) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        p.write(nbttagcompound1, "pos");
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      nbttagcompound.setTag("subBuildings", (NBTBase)nbttaglist);
      if (this.pujas != null) {
        NBTTagCompound tag = new NBTTagCompound();
        this.pujas.writeToNBT(tag);
        nbttagcompound.setTag("pujas", (NBTBase)tag);
      } 
      nbttagcompound.setLong("lastGoodsRefresh", this.lastGoodsRefresh);
      nbttagcompound.setInteger("pathsToBuildIndex", this.pathsToBuildIndex);
      nbttagcompound.setInteger("pathsToBuildPathIndex", this.pathsToBuildPathIndex);
      nbttagcompound.setInteger("oldPathPointsToClearIndex", this.oldPathPointsToClearIndex);
      if (this.brickColourTheme != null)
        nbttagcompound.setString("brickColourTheme", this.brickColourTheme.key); 
      Set<String> tags = getTags();
      if (tags.size() > 0 && MillConfigValues.LogTags >= 1)
        MillLog.major(this, "Tags to write: " + MillCommonUtilities.flattenStrings(tags)); 
      nbttaglist = new NBTTagList();
      for (String tag : tags) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setString("value", tag);
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
        if (MillConfigValues.LogTags >= 3)
          MillLog.debug(this, "Writing tag: " + tag); 
      } 
      nbttagcompound.setTag("tags", (NBTBase)nbttaglist);
      this.resManager.writeToNBT(nbttagcompound);
      if (this.marvelManager != null)
        this.marvelManager.writeToNBT(nbttagcompound); 
      if (this.pathsChanged)
        writePaths(); 
      if (this.isTownhall && this.bannerStack != null)
        nbttagcompound.setTag("bannerStack", (NBTBase)this.bannerStack.writeToNBT(new NBTTagCompound())); 
    } catch (Exception e) {
      Mill.proxy.sendChatAdmin("Error when trying to save building. Check millenaire.log.");
      MillLog.error(this, "Exception in Villager.onUpdate(): ");
      MillLog.printException(e);
    } 
  }
}
