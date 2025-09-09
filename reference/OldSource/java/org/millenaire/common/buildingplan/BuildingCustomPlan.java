package org.millenaire.common.buildingplan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;

public class BuildingCustomPlan implements IBuildingPlan {
  public final Culture culture;
  
  public String nativeName;
  
  public enum TypeRes {
    CHEST("chest"),
    CRAFT("craft"),
    SIGN("sign"),
    FIELD("field"),
    SPAWN("spawn"),
    SAPLING("sapling"),
    STALL("stall"),
    MINING("mining"),
    FURNACE("furnace"),
    FIRE_PIT("fire_pit"),
    MUDBRICK("mudbrick"),
    SUGAR("sugar"),
    FISHING("fishing"),
    SILK("silk"),
    SNAILS("snails"),
    SQUID("squid"),
    CACAO("cacao");
    
    public final String key;
    
    TypeRes(String key) {
      this.key = key;
    }
  }
  
  public static Map<String, BuildingCustomPlan> loadCustomBuildings(VirtualDir cultureVirtualDir, Culture culture) {
    Map<String, BuildingCustomPlan> buildingCustoms = new HashMap<>();
    VirtualDir customBuildingsVirtualDir = cultureVirtualDir.getChildDirectory("custombuildings");
    BuildingFileFiler textFiles = new BuildingFileFiler(".txt");
    for (File file : customBuildingsVirtualDir.listFilesRecursive(textFiles)) {
      try {
        if (MillConfigValues.LogBuildingPlan >= 1)
          MillLog.major(file, "Loaded custom building"); 
        BuildingCustomPlan buildingCustom = new BuildingCustomPlan(file, culture);
        buildingCustoms.put(buildingCustom.buildingKey, buildingCustom);
      } catch (Exception e) {
        MillLog.printException("Error when loading " + file.getAbsolutePath(), e);
      } 
    } 
    return buildingCustoms;
  }
  
  public String shop = null;
  
  public String buildingKey;
  
  public String gameNameKey = null;
  
  public final Map<String, String> names = new HashMap<>();
  
  public List<String> maleResident = new ArrayList<>();
  
  public List<String> femaleResident = new ArrayList<>();
  
  public List<String> visitors = new ArrayList<>();
  
  public int priorityMoveIn = 1;
  
  public int radius = 6;
  
  public int heightRadius = 4;
  
  public List<String> tags = new ArrayList<>();
  
  public ResourceLocation cropType = null;
  
  public ResourceLocation spawnType = null;
  
  public Map<TypeRes, Integer> minResources = new HashMap<>();
  
  public Map<TypeRes, Integer> maxResources = new HashMap<>();
  
  public BuildingCustomPlan(Culture culture, String key) {
    this.culture = culture;
    this.buildingKey = key;
  }
  
  public BuildingCustomPlan(File file, Culture culture) throws IOException {
    this.culture = culture;
    this.buildingKey = file.getName().split("\\.")[0];
    BufferedReader reader = MillCommonUtilities.getReader(file);
    String line = reader.readLine();
    readConfigLine(line);
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(this, "Loaded custom building " + this.buildingKey + this.nativeName + " pop: " + this.maleResident + "/" + this.femaleResident); 
    if (!this.minResources.containsKey(TypeRes.SIGN))
      MillLog.error(this, "No signs in custom building."); 
  }
  
  private void adjustLocationSize(BuildingLocation location, Map<TypeRes, List<Point>> resources) {
    int startX = location.pos.getiX();
    int startY = location.pos.getiY();
    int startZ = location.pos.getiZ();
    int endX = location.pos.getiX();
    int endY = location.pos.getiY();
    int endZ = location.pos.getiZ();
    for (TypeRes type : resources.keySet()) {
      for (Point p : resources.get(type)) {
        if (startX >= p.getiX())
          startX = p.getiX(); 
        if (startY >= p.getiY())
          startY = p.getiY(); 
        if (startZ >= p.getiZ())
          startZ = p.getiZ(); 
        if (endX <= p.getiX())
          endX = p.getiX(); 
        if (endY <= p.getiY())
          endY = p.getiY(); 
        if (endZ <= p.getiZ())
          endZ = p.getiZ(); 
      } 
    } 
    location.minx = startX - 1;
    location.maxx = endX + 1;
    location.miny = startY - 1;
    location.maxy = endY + 1;
    location.minz = startZ - 1;
    location.maxz = endZ + 1;
    location.length = location.maxx - location.minx + 1;
    location.width = location.maxz - location.minz + 1;
    location.computeMargins();
  }
  
  public Map<TypeRes, List<Point>> findResources(World world, Point pos, Building townHall, BuildingLocation currentLocation) {
    Map<TypeRes, List<Point>> resources = new HashMap<>();
    int currentRadius = 0;
    while (currentRadius < this.radius) {
      int y = pos.getiY() - this.heightRadius + 1;
      while (y < pos.getiY() + this.heightRadius + 1) {
        int z = pos.getiZ() - currentRadius;
        int x;
        for (x = pos.getiX() - currentRadius; x <= pos.getiX() + currentRadius; x++)
          handlePoint(x, y, z, world, resources, townHall, currentLocation); 
        x = pos.getiX() - currentRadius;
        for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ() + currentRadius - 1; z++)
          handlePoint(x, y, z, world, resources, townHall, currentLocation); 
        z = pos.getiZ() + currentRadius;
        for (x = pos.getiX() - currentRadius; x <= pos.getiX() + currentRadius; x++)
          handlePoint(x, y, z, world, resources, townHall, currentLocation); 
        x = pos.getiX() + currentRadius;
        for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ() + currentRadius - 1; z++)
          handlePoint(x, y, z, world, resources, townHall, currentLocation); 
        y++;
      } 
      currentRadius++;
    } 
    return resources;
  }
  
  public Culture getCulture() {
    return this.culture;
  }
  
  public List<String> getFemaleResident() {
    return this.femaleResident;
  }
  
  public String getFullDisplayName() {
    String name = this.nativeName;
    if (getNameTranslated() != null && getNameTranslated().length() > 0)
      name = name + " (" + getNameTranslated() + ")"; 
    return name;
  }
  
  public List<String> getMaleResident() {
    return this.maleResident;
  }
  
  public String getNameTranslated() {
    if (this.culture.canReadBuildingNames())
      return this.culture.getCustomBuildingGameName(this); 
    return "";
  }
  
  public String getNativeName() {
    return this.nativeName;
  }
  
  public List<String> getVisitors() {
    return this.visitors;
  }
  
  private void handlePoint(int x, int y, int z, World world, Map<TypeRes, List<Point>> resources, Building townHall, BuildingLocation currentLocation) {
    Point p = new Point(x, y, z);
    if (townHall != null) {
      BuildingLocation locationAtPos = townHall.getLocationAtCoord(p);
      if (locationAtPos == null || !locationAtPos.equals(currentLocation))
        for (BuildingLocation bl : townHall.getLocations()) {
          if ((currentLocation == null || !currentLocation.isSameLocation(bl)) && bl.isInsideZone(p))
            return; 
        }  
    } 
    TypeRes res = identifyRes(world, p);
    if (res != null && this.maxResources.containsKey(res))
      if (resources.containsKey(res) && ((List)resources.get(res)).size() < ((Integer)this.maxResources.get(res)).intValue()) {
        ((List<Point>)resources.get(res)).add(p);
      } else if (!resources.containsKey(res)) {
        List<Point> points = new ArrayList<>();
        points.add(p);
        resources.put(res, points);
      }  
  }
  
  private TypeRes identifyRes(World world, Point p) {
    Block b = p.getBlock(world);
    int meta = p.getMeta(world);
    if (b.equals(Blocks.CHEST) || b.equals(MillBlocks.LOCKED_CHEST))
      return TypeRes.CHEST; 
    if (b.equals(Blocks.CRAFTING_TABLE))
      return TypeRes.CRAFT; 
    if (b.equals(Blocks.WALL_SIGN) || b.equals(MillBlocks.PANEL))
      return TypeRes.SIGN; 
    if (b.equals(Blocks.FARMLAND))
      return TypeRes.FIELD; 
    if (b.equals(Blocks.HAY_BLOCK))
      return TypeRes.SPAWN; 
    if (b.equals(Blocks.SAPLING) || ((b.equals(Blocks.LOG) || b.equals(Blocks.LOG2)) && p.getBelow().getBlock(world).equals(Blocks.DIRT)))
      return TypeRes.SAPLING; 
    if (b.equals(Blocks.WOOL) && p.getMeta(world) == 4)
      return TypeRes.STALL; 
    if (b.equals(Blocks.STONE) || b.equals(Blocks.SANDSTONE) || b.equals(Blocks.SAND) || b.equals(Blocks.GRAVEL) || b.equals(Blocks.CLAY))
      if (p.getAbove().getBlock(world).equals(Blocks.AIR) || p.getRelative(1.0D, 0.0D, 0.0D).getBlock(world).equals(Blocks.AIR) || p.getRelative(-1.0D, 0.0D, 0.0D).getBlock(world).equals(Blocks.AIR) || p.getRelative(0.0D, 0.0D, 1.0D).getBlock(world).equals(Blocks.AIR) || p.getRelative(0.0D, 0.0D, -1.0D).getBlock(world).equals(Blocks.AIR))
        return TypeRes.MINING;  
    if (b.equals(Blocks.FURNACE))
      return TypeRes.FURNACE; 
    if (b.equals(MillBlocks.FIRE_PIT))
      return TypeRes.FIRE_PIT; 
    if (b.equals(MillBlocks.WET_BRICK) && meta == 0)
      return TypeRes.MUDBRICK; 
    if (b.equals(Blocks.REEDS) && !p.getBelow().getBlock(world).equals(Blocks.REEDS))
      return TypeRes.SUGAR; 
    if (b.equals(Blocks.WOOL) && p.getMeta(world) == 3)
      return TypeRes.FISHING; 
    if (b.equals(Blocks.WOOL) && p.getMeta(world) == 0)
      return TypeRes.SILK; 
    if (b.equals(Blocks.WOOL) && p.getMeta(world) == 11) {
      Point[] neighbours = { p.getRelative(1.0D, 0.0D, 0.0D), p.getRelative(-1.0D, 0.0D, 0.0D), p.getRelative(0.0D, 0.0D, 1.0D), p.getRelative(0.0D, 0.0D, -1.0D) };
      boolean waterAround = true;
      for (Point p2 : neighbours) {
        if (!p2.getBlock(world).equals(Blocks.WATER))
          waterAround = false; 
      } 
      if (waterAround)
        return TypeRes.SQUID; 
    } 
    if (b.equals(Blocks.COCOA))
      return TypeRes.CACAO; 
    return null;
  }
  
  private void readConfigLine(String line) {
    String[] configs = line.split(";", -1);
    for (String config : configs) {
      if ((config.split(":")).length == 2) {
        String key = config.split(":")[0].toLowerCase();
        String value = config.split(":")[1];
        if (key.equalsIgnoreCase("moveinpriority")) {
          this.priorityMoveIn = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("radius")) {
          this.radius = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("heightradius")) {
          this.heightRadius = Integer.parseInt(value);
        } else if (key.equalsIgnoreCase("native")) {
          this.nativeName = value;
        } else if (key.equalsIgnoreCase("gameNameKey")) {
          this.gameNameKey = value;
        } else if (key.equalsIgnoreCase("cropType")) {
          this.cropType = new ResourceLocation(value);
        } else if (key.equalsIgnoreCase("spawnType")) {
          this.spawnType = new ResourceLocation(value);
        } else if (key.startsWith("name_")) {
          this.names.put(key, value);
        } else if (key.equalsIgnoreCase("male")) {
          if (this.culture.villagerTypes.containsKey(value.toLowerCase())) {
            this.maleResident.add(value.toLowerCase());
          } else {
            MillLog.error(this, "Attempted to load unknown male villager: " + value);
          } 
        } else if (key.equalsIgnoreCase("female")) {
          if (this.culture.villagerTypes.containsKey(value.toLowerCase())) {
            this.femaleResident.add(value.toLowerCase());
          } else {
            MillLog.error(this, "Attempted to load unknown female villager: " + value);
          } 
        } else if (key.equalsIgnoreCase("visitor")) {
          if (this.culture.villagerTypes.containsKey(value.toLowerCase())) {
            this.visitors.add(value.toLowerCase());
          } else {
            MillLog.error(this, "Attempted to load unknown visitor: " + value);
          } 
        } else if (key.equalsIgnoreCase("shop")) {
          if (this.culture.shopBuys.containsKey(value) || this.culture.shopSells.containsKey(value) || this.culture.shopBuysOptional.containsKey(value)) {
            this.shop = value;
          } else {
            MillLog.error(this, "Undefined shop type: " + value);
          } 
        } else if (key.equalsIgnoreCase("tag")) {
          this.tags.add(value.toLowerCase());
        } else {
          boolean found = false;
          for (TypeRes typeRes : TypeRes.values()) {
            if (typeRes.key.equals(key))
              try {
                found = true;
                if (value.contains("-")) {
                  this.minResources.put(typeRes, Integer.valueOf(Integer.parseInt(value.split("-")[0])));
                  this.maxResources.put(typeRes, Integer.valueOf(Integer.parseInt(value.split("-")[1])));
                } else {
                  this.minResources.put(typeRes, Integer.valueOf(Integer.parseInt(value)));
                  this.maxResources.put(typeRes, Integer.valueOf(Integer.parseInt(value)));
                } 
              } catch (Exception e) {
                MillLog.printException("Exception while parsing res " + typeRes.key + " in custom file " + this.buildingKey + " of culture " + this.culture.key + ":", e);
              }  
          } 
          if (!found)
            MillLog.error(this, "Could not recognise key on line: " + config); 
        } 
      } 
    } 
  }
  
  public void registerResources(Building building, BuildingLocation location) {
    Map<TypeRes, List<Point>> resources = findResources(building.world, location.pos, building.getTownHall(), location);
    adjustLocationSize(location, resources);
    building.getResManager().setSleepingPos(location.pos.getAbove());
    location.sleepingPos = location.pos.getAbove();
    if (resources.containsKey(TypeRes.CHEST)) {
      (building.getResManager()).chests.clear();
      for (Point chestP : resources.get(TypeRes.CHEST)) {
        if (chestP.getBlock(building.world).equals(Blocks.CHEST)) {
          int meta = chestP.getMeta(building.world);
          chestP.setBlock(building.world, (Block)MillBlocks.LOCKED_CHEST, meta, false, false);
        } 
        (building.getResManager()).chests.add(chestP);
      } 
    } 
    if (resources.containsKey(TypeRes.CRAFT) && ((List)resources.get(TypeRes.CRAFT)).size() > 0) {
      location.craftingPos = ((List<Point>)resources.get(TypeRes.CRAFT)).get(0);
      building.getResManager().setCraftingPos(((List<Point>)resources.get(TypeRes.CRAFT)).get(0));
    } 
    registerSigns(building, resources);
    if (this.cropType != null && resources.containsKey(TypeRes.FIELD)) {
      (building.getResManager()).soils.clear();
      (building.getResManager()).soilTypes.clear();
      for (Point p : resources.get(TypeRes.FIELD))
        building.getResManager().addSoilPoint(this.cropType, p); 
    } 
    if (this.spawnType != null && resources.containsKey(TypeRes.SPAWN)) {
      (building.getResManager()).spawns.clear();
      (building.getResManager()).spawnTypes.clear();
      for (Point p : resources.get(TypeRes.SPAWN)) {
        p.setBlock(building.world, Blocks.AIR, 0, true, false);
        building.getResManager().addSpawnPoint(this.spawnType, p);
      } 
    } 
    if (resources.containsKey(TypeRes.SAPLING)) {
      (building.getResManager()).woodspawn.clear();
      for (Point p : resources.get(TypeRes.SAPLING)) {
        (building.getResManager()).woodspawn.add(p);
        IBlockState bs = building.world.getBlockState(p.getBlockPos());
        if (bs.getBlock() == Blocks.SAPLING) {
          if (bs.getValue((IProperty)BlockSapling.TYPE) == BlockPlanks.EnumType.OAK) {
            (building.getResManager()).woodspawnTypes.put(p, "oakspawn");
            continue;
          } 
          if (bs.getValue((IProperty)BlockSapling.TYPE) == BlockPlanks.EnumType.SPRUCE) {
            (building.getResManager()).woodspawnTypes.put(p, "pinespawn");
            continue;
          } 
          if (bs.getValue((IProperty)BlockSapling.TYPE) == BlockPlanks.EnumType.BIRCH) {
            (building.getResManager()).woodspawnTypes.put(p, "birchspawn");
            continue;
          } 
          if (bs.getValue((IProperty)BlockSapling.TYPE) == BlockPlanks.EnumType.JUNGLE) {
            (building.getResManager()).woodspawnTypes.put(p, "junglespawn");
            continue;
          } 
          if (bs.getValue((IProperty)BlockSapling.TYPE) == BlockPlanks.EnumType.ACACIA) {
            (building.getResManager()).woodspawnTypes.put(p, "acaciaspawn");
            continue;
          } 
          if (bs.getValue((IProperty)BlockSapling.TYPE) == BlockPlanks.EnumType.DARK_OAK)
            (building.getResManager()).woodspawnTypes.put(p, "darkoakspawn"); 
          continue;
        } 
        if (bs.getBlock() == MillBlocks.SAPLING_APPLETREE) {
          (building.getResManager()).woodspawnTypes.put(p, "appletreespawn");
          continue;
        } 
        if (bs.getBlock() == MillBlocks.SAPLING_OLIVETREE) {
          (building.getResManager()).woodspawnTypes.put(p, "olivetreespawn");
          continue;
        } 
        if (bs.getBlock() == MillBlocks.SAPLING_PISTACHIO) {
          (building.getResManager()).woodspawnTypes.put(p, "pistachiotreespawn");
          continue;
        } 
        if (bs.getBlock() == MillBlocks.SAPLING_CHERRY) {
          (building.getResManager()).woodspawnTypes.put(p, "cherrytreespawn");
          continue;
        } 
        if (bs.getBlock() == MillBlocks.SAPLING_SAKURA)
          (building.getResManager()).woodspawnTypes.put(p, "sakuratreespawn"); 
      } 
    } 
    if (resources.containsKey(TypeRes.STALL)) {
      (building.getResManager()).stalls.clear();
      for (Point p : resources.get(TypeRes.STALL)) {
        p.setBlock(building.world, Blocks.AIR, 0, true, false);
        (building.getResManager()).stalls.add(p);
      } 
    } 
    if (resources.containsKey(TypeRes.MINING)) {
      (building.getResManager()).sources.clear();
      (building.getResManager()).sourceTypes.clear();
      for (Point p : resources.get(TypeRes.MINING))
        building.getResManager().addSourcePoint(p.getBlockActualState(building.world), p); 
    } 
    if (resources.containsKey(TypeRes.FURNACE)) {
      (building.getResManager()).furnaces.clear();
      for (Point p : resources.get(TypeRes.FURNACE))
        (building.getResManager()).furnaces.add(p); 
    } 
    if (resources.containsKey(TypeRes.FIRE_PIT)) {
      (building.getResManager()).firepits.clear();
      for (Point p : resources.get(TypeRes.FIRE_PIT))
        (building.getResManager()).firepits.add(p); 
    } 
    if (resources.containsKey(TypeRes.MUDBRICK)) {
      (building.getResManager()).brickspot.clear();
      for (Point p : resources.get(TypeRes.MUDBRICK))
        (building.getResManager()).brickspot.add(p); 
    } 
    if (resources.containsKey(TypeRes.SUGAR)) {
      (building.getResManager()).sugarcanesoils.clear();
      for (Point p : resources.get(TypeRes.SUGAR))
        (building.getResManager()).sugarcanesoils.add(p); 
    } 
    if (resources.containsKey(TypeRes.FISHING)) {
      (building.getResManager()).fishingspots.clear();
      for (Point p : resources.get(TypeRes.FISHING)) {
        p.setBlock(building.world, Blocks.AIR, 0, true, false);
        (building.getResManager()).fishingspots.add(p);
      } 
    } 
    if (resources.containsKey(TypeRes.SILK)) {
      (building.getResManager()).silkwormblock.clear();
      for (Point p : resources.get(TypeRes.SILK)) {
        p.setBlock(building.world, (Block)MillBlocks.SILK_WORM, 0, true, false);
        (building.getResManager()).silkwormblock.add(p);
      } 
    } 
    if (resources.containsKey(TypeRes.SNAILS)) {
      (building.getResManager()).snailsoilblock.clear();
      for (Point p : resources.get(TypeRes.SNAILS)) {
        p.setBlock(building.world, (Block)MillBlocks.SNAIL_SOIL, 0, true, false);
        (building.getResManager()).snailsoilblock.add(p);
      } 
    } 
    if (resources.containsKey(TypeRes.SQUID)) {
      int squidSpawnPos = -1;
      for (int i = 0; i < (building.getResManager()).spawnTypes.size(); i++) {
        if (((ResourceLocation)(building.getResManager()).spawnTypes.get(i)).equals(new ResourceLocation("Squid")))
          squidSpawnPos = i; 
      } 
      if (squidSpawnPos > -1)
        ((CopyOnWriteArrayList)(building.getResManager()).spawns.get(squidSpawnPos)).clear(); 
      for (Point p : resources.get(TypeRes.SQUID)) {
        p.setBlock(building.world, (Block)Blocks.WATER, 0, true, false);
        building.getResManager().addSpawnPoint(new ResourceLocation("Squid"), p);
      } 
    } 
    if (resources.containsKey(TypeRes.CACAO)) {
      int cocoaSoilPos = -1;
      for (int i = 0; i < (building.getResManager()).soilTypes.size(); i++) {
        if (((ResourceLocation)(building.getResManager()).soilTypes.get(i)).equals(Mill.CROP_CACAO))
          cocoaSoilPos = i; 
      } 
      if (cocoaSoilPos > -1)
        ((CopyOnWriteArrayList)(building.getResManager()).soils.get(cocoaSoilPos)).clear(); 
      for (Point p : resources.get(TypeRes.CACAO))
        building.getResManager().addSoilPoint(Mill.CROP_CACAO, p); 
    } 
    updateTags(building);
  }
  
  private void registerSigns(Building building, Map<TypeRes, List<Point>> resources) {
    (building.getResManager()).signs.clear();
    Map<Integer, Point> signsWithPos = new HashMap<>();
    List<Point> otherSigns = new ArrayList<>();
    if (resources.containsKey(TypeRes.SIGN))
      for (Point signP : resources.get(TypeRes.SIGN)) {
        TileEntitySign signEntity = signP.getSign(building.world);
        int signPos = -1;
        if (signEntity != null)
          try {
            signPos = Integer.parseInt(signEntity.signText[0].getUnformattedText()) - 1;
          } catch (Exception exception) {} 
        if (signPos > -1 && !signsWithPos.containsKey(Integer.valueOf(signPos))) {
          signsWithPos.put(Integer.valueOf(signPos), signP);
        } else {
          otherSigns.add(signP);
        } 
        if (signP.getBlock(building.world).equals(Blocks.WALL_SIGN)) {
          int meta = signP.getMeta(building.world);
          signP.setBlock(building.world, (Block)MillBlocks.PANEL, meta, true, false);
        } 
      }  
    int signNumber = signsWithPos.size() + otherSigns.size();
    for (int i = 0; i < signNumber; i++)
      (building.getResManager()).signs.add(null); 
    for (Integer pos : signsWithPos.keySet()) {
      if (pos.intValue() < signNumber) {
        (building.getResManager()).signs.set(pos.intValue(), signsWithPos.get(pos));
        continue;
      } 
      otherSigns.add(signsWithPos.get(pos));
    } 
    int posInOthers = 0;
    for (int j = 0; j < signNumber; j++) {
      if ((building.getResManager()).signs.get(j) == null) {
        (building.getResManager()).signs.set(j, otherSigns.get(posInOthers));
        posInOthers++;
      } 
    } 
  }
  
  public String toString() {
    return "custom:" + this.buildingKey + ":" + this.culture.key;
  }
  
  private void updateTags(Building building) {
    if (!this.tags.isEmpty()) {
      building.addTags(this.tags, this.buildingKey + ": registering new tags");
      if (MillConfigValues.LogTags >= 2)
        MillLog.minor(this, "Applying tags: " + (String)this.tags.stream().collect(Collectors.joining(", ")) + ", result: " + (String)building.getTags().stream().collect(Collectors.joining(", "))); 
    } 
  }
}
