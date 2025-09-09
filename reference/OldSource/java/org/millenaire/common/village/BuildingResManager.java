package org.millenaire.common.village;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.block.BlockSilkWorm;
import org.millenaire.common.block.BlockSnailSoil;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class BuildingResManager {
  public CopyOnWriteArrayList<Point> brickspot = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> chests = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> fishingspots = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> sugarcanesoils = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> healingspots = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> furnaces = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> firepits = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> brewingStands = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> signs = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> banners = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> cultureBanners = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> sources = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<IBlockState> sourceTypes = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> spawns = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<ResourceLocation> spawnTypes = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> mobSpawners = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<ResourceLocation> mobSpawnerTypes = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> soils = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<ResourceLocation> soilTypes = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> stalls = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> woodspawn = new CopyOnWriteArrayList<>();
  
  public ConcurrentHashMap<Point, String> woodspawnTypes = new ConcurrentHashMap<>();
  
  public CopyOnWriteArrayList<Point> netherwartsoils = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> silkwormblock = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> snailsoilblock = new CopyOnWriteArrayList<>();
  
  public CopyOnWriteArrayList<Point> dispenderUnknownPowder = new CopyOnWriteArrayList<>();
  
  private Point sleepingPos = null;
  
  private Point sellingPos = null;
  
  private Point craftingPos = null;
  
  private Point defendingPos = null;
  
  private Point shelterPos = null;
  
  private Point pathStartPos = null;
  
  private Point leasurePos = null;
  
  private final Building building;
  
  public BuildingResManager(Building b) {
    this.building = b;
  }
  
  public void addMobSpawnerPoint(ResourceLocation type, Point p) {
    if (!this.mobSpawnerTypes.contains(type)) {
      CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<>();
      spawnsPoint.add(p);
      this.mobSpawners.add(spawnsPoint);
      this.mobSpawnerTypes.add(type);
    } else {
      for (int i = 0; i < this.mobSpawnerTypes.size(); i++) {
        if (((ResourceLocation)this.mobSpawnerTypes.get(i)).equals(type) && 
          !((CopyOnWriteArrayList)this.mobSpawners.get(i)).contains(p))
          ((CopyOnWriteArrayList<Point>)this.mobSpawners.get(i)).add(p); 
      } 
    } 
  }
  
  public void addSoilPoint(ResourceLocation type, Point p) {
    if (!this.soilTypes.contains(type)) {
      CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<>();
      spawnsPoint.add(p);
      this.soils.add(spawnsPoint);
      this.soilTypes.add(type);
    } else {
      for (int i = 0; i < this.soilTypes.size(); i++) {
        if (((ResourceLocation)this.soilTypes.get(i)).equals(type) && 
          !((CopyOnWriteArrayList)this.soils.get(i)).contains(p))
          ((CopyOnWriteArrayList<Point>)this.soils.get(i)).add(p); 
      } 
    } 
  }
  
  public void addSourcePoint(IBlockState blockState, Point p) {
    if (!this.sourceTypes.contains(blockState)) {
      CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<>();
      spawnsPoint.add(p);
      this.sources.add(spawnsPoint);
      this.sourceTypes.add(blockState);
    } else {
      for (int i = 0; i < this.sourceTypes.size(); i++) {
        if (((IBlockState)this.sourceTypes.get(i)).equals(blockState) && 
          !((CopyOnWriteArrayList)this.sources.get(i)).contains(p))
          ((CopyOnWriteArrayList<Point>)this.sources.get(i)).add(p); 
      } 
    } 
  }
  
  public void addSpawnPoint(ResourceLocation type, Point p) {
    if (!this.spawnTypes.contains(type)) {
      CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<>();
      spawnsPoint.add(p);
      this.spawns.add(spawnsPoint);
      this.spawnTypes.add(type);
    } else {
      for (int i = 0; i < this.spawnTypes.size(); i++) {
        if (((ResourceLocation)this.spawnTypes.get(i)).equals(type) && 
          !((CopyOnWriteArrayList)this.spawns.get(i)).contains(p))
          ((CopyOnWriteArrayList<Point>)this.spawns.get(i)).add(p); 
      } 
    } 
  }
  
  public HashMap<InvItem, Integer> getChestsContent() {
    HashMap<InvItem, Integer> contents = new HashMap<>();
    for (Point p : this.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.building.world);
      if (chest != null)
        for (int i = 0; i < chest.getSizeInventory(); i++) {
          ItemStack stack = chest.getStackInSlot(i);
          if (stack != null && stack.getItem() != Items.AIR) {
            InvItem key = InvItem.createInvItem(stack);
            if (stack != null)
              if (contents.containsKey(key)) {
                contents.put(key, Integer.valueOf(stack.getCount() + ((Integer)contents.get(key)).intValue()));
              } else {
                contents.put(key, Integer.valueOf(stack.getCount()));
              }  
          } 
        }  
    } 
    return contents;
  }
  
  public Point getCocoaHarvestLocation() {
    for (int i = 0; i < this.soilTypes.size(); i++) {
      if (((ResourceLocation)this.soilTypes.get(i)).equals(Mill.CROP_CACAO))
        for (Point p : this.soils.get(i)) {
          IBlockState state = p.getBlockActualState(this.building.world);
          if (state.getBlock() == Blocks.COCOA && (
            (Integer)state.getValue((IProperty)BlockCocoa.AGE)).intValue() >= 2)
            return p; 
        }  
    } 
    return null;
  }
  
  public Point getCocoaPlantingLocation() {
    for (int i = 0; i < this.soilTypes.size(); i++) {
      if (((ResourceLocation)this.soilTypes.get(i)).equals(Mill.CROP_CACAO))
        for (Point p : this.soils.get(i)) {
          if (p.getBlock(this.building.world) == Blocks.AIR) {
            if (p.getNorth().getBlock(this.building.world) == Blocks.LOG && 
              isBlockJungleWood(p.getNorth().getBlockActualState(this.building.world)))
              return p; 
            if (p.getEast().getBlock(this.building.world) == Blocks.LOG && 
              isBlockJungleWood(p.getEast().getBlockActualState(this.building.world)))
              return p; 
            if (p.getSouth().getBlock(this.building.world) == Blocks.LOG && 
              isBlockJungleWood(p.getSouth().getBlockActualState(this.building.world)))
              return p; 
            if (p.getWest().getBlock(this.building.world) == Blocks.LOG && 
              isBlockJungleWood(p.getWest().getBlockActualState(this.building.world)))
              return p; 
          } 
        }  
    } 
    return null;
  }
  
  public Point getCraftingPos() {
    if (this.craftingPos != null)
      return this.craftingPos; 
    if (this.sellingPos != null)
      return this.sellingPos; 
    return this.sleepingPos;
  }
  
  public Point getDefendingPos() {
    if (this.defendingPos != null)
      return this.defendingPos; 
    if (this.sellingPos != null)
      return this.sellingPos; 
    return this.sleepingPos;
  }
  
  public Point getEmptyBrickLocation() {
    if (this.brickspot.size() == 0)
      return null; 
    for (int i = 0; i < this.brickspot.size(); i++) {
      Point p = this.brickspot.get(i);
      if (WorldUtilities.getBlock(this.building.world, p) == Blocks.AIR)
        return p; 
    } 
    return null;
  }
  
  public Point getFullBrickLocation() {
    if (this.brickspot.size() == 0)
      return null; 
    for (int i = 0; i < this.brickspot.size(); i++) {
      Point p = this.brickspot.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.BS_MUD_BRICK)
        return p; 
    } 
    return null;
  }
  
  public Point getLeasurePos() {
    if (this.leasurePos != null)
      return this.leasurePos; 
    return getSellingPos();
  }
  
  public int getNbEmptyBrickLocation() {
    if (this.brickspot.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.brickspot.size(); i++) {
      Point p = this.brickspot.get(i);
      if (WorldUtilities.getBlock(this.building.world, p) == Blocks.AIR)
        nb++; 
    } 
    return nb;
  }
  
  public int getNbFullBrickLocation() {
    if (this.brickspot.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.brickspot.size(); i++) {
      Point p = this.brickspot.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.BS_MUD_BRICK)
        nb++; 
    } 
    return nb;
  }
  
  public int getNbNetherWartHarvestLocation() {
    if (this.netherwartsoils.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.netherwartsoils.size(); i++) {
      Point p = this.netherwartsoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.NETHER_WART && 
        WorldUtilities.getBlockMeta(this.building.world, p.getAbove()) >= 3)
        nb++; 
    } 
    return nb;
  }
  
  public int getNbNetherWartPlantingLocation() {
    if (this.netherwartsoils.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.netherwartsoils.size(); i++) {
      Point p = this.netherwartsoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.AIR)
        nb++; 
    } 
    return nb;
  }
  
  public int getNbSilkWormHarvestLocation() {
    if (this.silkwormblock.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.silkwormblock.size(); i++) {
      Point p = this.silkwormblock.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.SILK_WORM.getDefaultState()
        .withProperty((IProperty)BlockSilkWorm.PROGRESS, (Comparable)BlockSilkWorm.EnumType.SILKWORMFULL))
        nb++; 
    } 
    return nb;
  }
  
  public int getNbSnailSoilHarvestLocation() {
    if (this.snailsoilblock.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.snailsoilblock.size(); i++) {
      Point p = this.snailsoilblock.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.SNAIL_SOIL.getDefaultState().withProperty((IProperty)BlockSnailSoil.PROGRESS, (Comparable)BlockSnailSoil.EnumType.SNAIL_SOIL_FULL))
        nb++; 
    } 
    return nb;
  }
  
  public int getNbSugarCaneHarvestLocation() {
    if (this.sugarcanesoils.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.sugarcanesoils.size(); i++) {
      Point p = this.sugarcanesoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getRelative(0.0D, 2.0D, 0.0D)) == Blocks.REEDS)
        nb++; 
    } 
    return nb;
  }
  
  public int getNbSugarCanePlantingLocation() {
    if (this.sugarcanesoils.size() == 0)
      return 0; 
    int nb = 0;
    for (int i = 0; i < this.sugarcanesoils.size(); i++) {
      Point p = this.sugarcanesoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.AIR)
        nb++; 
    } 
    return nb;
  }
  
  public Point getNetherWartsHarvestLocation() {
    if (this.netherwartsoils.size() == 0)
      return null; 
    int start = MillCommonUtilities.randomInt(this.netherwartsoils.size());
    int i;
    for (i = start; i < this.netherwartsoils.size(); i++) {
      Point p = this.netherwartsoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.NETHER_WART && 
        WorldUtilities.getBlockMeta(this.building.world, p.getAbove()) == 3)
        return p; 
    } 
    for (i = 0; i < start; i++) {
      Point p = this.netherwartsoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.NETHER_WART && 
        WorldUtilities.getBlockMeta(this.building.world, p.getAbove()) == 3)
        return p; 
    } 
    return null;
  }
  
  public Point getNetherWartsPlantingLocation() {
    if (this.netherwartsoils.size() == 0)
      return null; 
    int start = MillCommonUtilities.randomInt(this.netherwartsoils.size());
    int i;
    for (i = start; i < this.netherwartsoils.size(); i++) {
      Point p = this.netherwartsoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.AIR && 
        WorldUtilities.getBlock(this.building.world, p) == Blocks.SOUL_SAND)
        return p; 
    } 
    for (i = 0; i < start; i++) {
      Point p = this.netherwartsoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.AIR && 
        WorldUtilities.getBlock(this.building.world, p) == Blocks.SOUL_SAND)
        return p; 
    } 
    return null;
  }
  
  public Point getPathStartPos() {
    if (this.pathStartPos != null)
      return this.pathStartPos; 
    return getSellingPos();
  }
  
  public Point getPlantingLocation() {
    for (Point p : this.woodspawn) {
      Block block = WorldUtilities.getBlock(this.building.world, p);
      if (block == Blocks.AIR || block == Blocks.SNOW_LAYER || (
        BlockItemUtilities.isBlockDecorativePlant(block) && !(block instanceof net.minecraft.block.BlockSapling) && !(block instanceof org.millenaire.common.block.BlockMillSapling)))
        return p; 
    } 
    return null;
  }
  
  public String getPlantingLocationType(Point target) {
    return this.woodspawnTypes.get(target);
  }
  
  public Point getSellingPos() {
    if (this.sellingPos != null)
      return this.sellingPos; 
    return this.sleepingPos;
  }
  
  public Point getShelterPos() {
    if (this.shelterPos != null)
      return this.shelterPos; 
    return this.sleepingPos;
  }
  
  public Point getSilkwormHarvestLocation() {
    if (this.silkwormblock.size() == 0)
      return null; 
    int start = MillCommonUtilities.randomInt(this.silkwormblock.size());
    int i;
    for (i = start; i < this.silkwormblock.size(); i++) {
      Point p = this.silkwormblock.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.SILK_WORM.getDefaultState()
        .withProperty((IProperty)BlockSilkWorm.PROGRESS, (Comparable)BlockSilkWorm.EnumType.SILKWORMFULL))
        return p; 
    } 
    for (i = 0; i < start; i++) {
      Point p = this.silkwormblock.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.SILK_WORM.getDefaultState()
        .withProperty((IProperty)BlockSilkWorm.PROGRESS, (Comparable)BlockSilkWorm.EnumType.SILKWORMFULL))
        return p; 
    } 
    return null;
  }
  
  public Point getSleepingPos() {
    return this.sleepingPos;
  }
  
  public Point getSnailSoilHarvestLocation() {
    if (this.snailsoilblock.size() == 0)
      return null; 
    int start = MillCommonUtilities.randomInt(this.snailsoilblock.size());
    int i;
    for (i = start; i < this.snailsoilblock.size(); i++) {
      Point p = this.snailsoilblock.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.SNAIL_SOIL.getDefaultState().withProperty((IProperty)BlockSnailSoil.PROGRESS, (Comparable)BlockSnailSoil.EnumType.SNAIL_SOIL_FULL))
        return p; 
    } 
    for (i = 0; i < start; i++) {
      Point p = this.snailsoilblock.get(i);
      if (WorldUtilities.getBlockState(this.building.world, p) == MillBlocks.SNAIL_SOIL.getDefaultState().withProperty((IProperty)BlockSnailSoil.PROGRESS, (Comparable)BlockSnailSoil.EnumType.SNAIL_SOIL_FULL))
        return p; 
    } 
    return null;
  }
  
  public List<Point> getSoilPoints(ResourceLocation type) {
    for (int i = 0; i < this.soilTypes.size(); i++) {
      if (((ResourceLocation)this.soilTypes.get(i)).equals(type))
        return this.soils.get(i); 
    } 
    return null;
  }
  
  public Point getSugarCaneHarvestLocation() {
    if (this.sugarcanesoils.size() == 0)
      return null; 
    int start = MillCommonUtilities.randomInt(this.sugarcanesoils.size());
    int i;
    for (i = start; i < this.sugarcanesoils.size(); i++) {
      Point p = this.sugarcanesoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getRelative(0.0D, 2.0D, 0.0D)) == Blocks.REEDS)
        return p; 
    } 
    for (i = 0; i < start; i++) {
      Point p = this.sugarcanesoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getRelative(0.0D, 2.0D, 0.0D)) == Blocks.REEDS)
        return p; 
    } 
    return null;
  }
  
  public Point getSugarCanePlantingLocation() {
    if (this.sugarcanesoils.size() == 0)
      return null; 
    int start = MillCommonUtilities.randomInt(this.sugarcanesoils.size());
    int i;
    for (i = start; i < this.sugarcanesoils.size(); i++) {
      Point p = this.sugarcanesoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.AIR)
        return p; 
    } 
    for (i = 0; i < start; i++) {
      Point p = this.sugarcanesoils.get(i);
      if (WorldUtilities.getBlock(this.building.world, p.getAbove()) == Blocks.AIR)
        return p; 
    } 
    return null;
  }
  
  private boolean isBlockJungleWood(IBlockState state) {
    return (state.getValue((IProperty)BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE);
  }
  
  public void readDataStream(PacketBuffer ds) throws IOException {
    this.chests = StreamReadWrite.readPointList(ds);
    this.furnaces = StreamReadWrite.readPointList(ds);
    this.firepits = StreamReadWrite.readPointList(ds);
    this.signs = StreamReadWrite.readPointList(ds);
    this.stalls = StreamReadWrite.readPointList(ds);
    this.banners = StreamReadWrite.readPointList(ds);
    this.cultureBanners = StreamReadWrite.readPointList(ds);
    for (Point p : this.chests) {
      TileEntityLockedChest chest = p.getMillChest(this.building.mw.world);
      if (chest != null)
        chest.buildingPos = this.building.getPos(); 
    } 
  }
  
  public void readFromNBT(NBTTagCompound nbttagcompound) {
    this.sleepingPos = Point.read(nbttagcompound, "spawnPos");
    this.sellingPos = Point.read(nbttagcompound, "sellingPos");
    this.craftingPos = Point.read(nbttagcompound, "craftingPos");
    this.defendingPos = Point.read(nbttagcompound, "defendingPos");
    this.shelterPos = Point.read(nbttagcompound, "shelterPos");
    this.pathStartPos = Point.read(nbttagcompound, "pathStartPos");
    this.leasurePos = Point.read(nbttagcompound, "leasurePos");
    if (this.sleepingPos == null)
      this.sleepingPos = this.building.getPos().getAbove(); 
    NBTTagList nbttaglist = nbttagcompound.getTagList("chests", 10);
    int i;
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null && 
        !this.chests.contains(p))
        this.chests.add(p); 
    } 
    if (!this.chests.contains(this.building.getPos()))
      this.chests.add(0, this.building.getPos()); 
    nbttaglist = nbttagcompound.getTagList("furnaces", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.furnaces.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("firepits", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.firepits.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("brewingStands", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.brewingStands.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("banners", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.banners.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("culturebanners", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.cultureBanners.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("signs", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      this.signs.add(p);
    } 
    nbttaglist = nbttagcompound.getTagList("netherwartsoils", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.netherwartsoils.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("silkwormblock", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.silkwormblock.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("snailsoilblock", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.snailsoilblock.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("sugarcanesoils", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.sugarcanesoils.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("fishingspots", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.fishingspots.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("healingspots", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.healingspots.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("stalls", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.stalls.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("woodspawn", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null) {
        this.woodspawn.add(p);
        String type = nbttagcompound1.getString("type");
        if (type != null)
          this.woodspawnTypes.put(p, type); 
      } 
    } 
    nbttaglist = nbttagcompound.getTagList("brickspot", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      Point p = Point.read(nbttagcompound1, "pos");
      if (p != null)
        this.brickspot.add(p); 
    } 
    nbttaglist = nbttagcompound.getTagList("spawns", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      ResourceLocation spawnType = new ResourceLocation(nbttagcompound1.getString("type"));
      this.spawnTypes.add(spawnType);
      CopyOnWriteArrayList<Point> v = new CopyOnWriteArrayList<>();
      NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", 10);
      for (int j = 0; j < nbttaglist2.tagCount(); j++) {
        NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
        Point p = Point.read(nbttagcompound2, "pos");
        if (p != null) {
          v.add(p);
          if (MillConfigValues.LogHybernation >= 2)
            MillLog.minor(this, "Loaded spawn point: " + p); 
        } 
      } 
      this.spawns.add(v);
      if (MillConfigValues.LogHybernation >= 2)
        MillLog.minor(this, "Loaded " + v.size() + " spawn points for " + this.spawnTypes.get(i)); 
    } 
    nbttaglist = nbttagcompound.getTagList("mobspawns", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      this.mobSpawnerTypes.add(new ResourceLocation(nbttagcompound1.getString("type")));
      CopyOnWriteArrayList<Point> v = new CopyOnWriteArrayList<>();
      NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", 10);
      for (int j = 0; j < nbttaglist2.tagCount(); j++) {
        NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
        Point p = Point.read(nbttagcompound2, "pos");
        if (p != null) {
          v.add(p);
          if (MillConfigValues.LogHybernation >= 2)
            MillLog.minor(this, "Loaded spawn point: " + p); 
        } 
      } 
      this.mobSpawners.add(v);
      if (MillConfigValues.LogHybernation >= 2)
        MillLog.minor(this, "Loaded " + v.size() + " mob spawn points for " + this.spawnTypes.get(i)); 
    } 
    nbttaglist = nbttagcompound.getTagList("sources", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      if (nbttagcompound1.hasKey("block_rl")) {
        Block block = Block.getBlockFromName(nbttagcompound1.getString("block_rl"));
        int meta = nbttagcompound1.getInteger("block_meta");
        IBlockState blockState = block.getStateFromMeta(meta);
        this.sourceTypes.add(blockState);
      } else {
        int blockId = nbttagcompound1.getInteger("type");
        this.sourceTypes.add(Block.getBlockById(blockId).getDefaultState());
      } 
      CopyOnWriteArrayList<Point> v = new CopyOnWriteArrayList<>();
      NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", 10);
      for (int j = 0; j < nbttaglist2.tagCount(); j++) {
        NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
        Point p = Point.read(nbttagcompound2, "pos");
        if (p != null) {
          v.add(p);
          if (MillConfigValues.LogHybernation >= 3)
            MillLog.debug(this, "Loaded source point: " + p); 
        } 
      } 
      this.sources.add(v);
      if (MillConfigValues.LogHybernation >= 1)
        MillLog.debug(this, "Loaded " + v.size() + " sources points for " + ((IBlockState)this.sourceTypes.get(i)).toString()); 
    } 
    nbttaglist = nbttagcompound.getTagList("genericsoils", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      ResourceLocation type = new ResourceLocation(nbttagcompound1.getString("type"));
      NBTTagList nbttaglist2 = nbttagcompound1.getTagList("points", 10);
      for (int j = 0; j < nbttaglist2.tagCount(); j++) {
        NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(j);
        Point p = Point.read(nbttagcompound2, "pos");
        if (p != null)
          addSoilPoint(type, p); 
      } 
    } 
    for (Point p : this.chests) {
      if (this.building.world.isChunkGeneratedAt(p.getiX() / 16, p.getiZ() / 16) && p
        .getMillChest(this.building.world) != null)
        (p.getMillChest(this.building.world)).buildingPos = this.building.getPos(); 
    } 
  }
  
  public void sendBuildingPacket(PacketBuffer data) throws IOException {
    StreamReadWrite.writePointList(this.chests, data);
    StreamReadWrite.writePointList(this.furnaces, data);
    StreamReadWrite.writePointList(this.firepits, data);
    StreamReadWrite.writePointList(this.signs, data);
    StreamReadWrite.writePointList(this.stalls, data);
    StreamReadWrite.writePointList(this.banners, data);
    StreamReadWrite.writePointList(this.cultureBanners, data);
  }
  
  public void setCraftingPos(Point p) {
    this.craftingPos = p;
  }
  
  public void setDefendingPos(Point p) {
    this.defendingPos = p;
  }
  
  public void setLeasurePos(Point p) {
    this.leasurePos = p;
  }
  
  public void setPathStartPos(Point p) {
    this.pathStartPos = p;
  }
  
  public void setSellingPos(Point p) {
    this.sellingPos = p;
  }
  
  public void setShelterPos(Point p) {
    this.shelterPos = p;
  }
  
  public void setSleepingPos(Point p) {
    this.sleepingPos = p;
  }
  
  public void writeToNBT(NBTTagCompound nbttagcompound) {
    if (this.sleepingPos != null)
      this.sleepingPos.write(nbttagcompound, "spawnPos"); 
    if (this.sellingPos != null)
      this.sellingPos.write(nbttagcompound, "sellingPos"); 
    if (this.craftingPos != null)
      this.craftingPos.write(nbttagcompound, "craftingPos"); 
    if (this.defendingPos != null)
      this.defendingPos.write(nbttagcompound, "defendingPos"); 
    if (this.shelterPos != null)
      this.shelterPos.write(nbttagcompound, "shelterPos"); 
    if (this.pathStartPos != null)
      this.pathStartPos.write(nbttagcompound, "pathStartPos"); 
    if (this.leasurePos != null)
      this.leasurePos.write(nbttagcompound, "leasurePos"); 
    NBTTagList nbttaglist = new NBTTagList();
    for (Point p : this.signs) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      if (p != null) {
        p.write(nbttagcompound1, "pos");
      } else {
        nbttagcompound1.setDouble("pos_xCoord", 0.0D);
        nbttagcompound1.setDouble("pos_yCoord", 0.0D);
        nbttagcompound1.setDouble("pos_zCoord", 0.0D);
      } 
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("signs", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.netherwartsoils) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("netherwartsoils", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.silkwormblock) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("silkwormblock", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.snailsoilblock) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("snailsoilblock", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.sugarcanesoils) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("sugarcanesoils", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.fishingspots) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("fishingspots", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.healingspots) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("healingspots", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.stalls) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("stalls", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.woodspawn) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      if (this.woodspawnTypes.containsKey(p))
        nbttagcompound1.setString("type", this.woodspawnTypes.get(p)); 
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("woodspawn", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.brickspot) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("brickspot", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    int i;
    for (i = 0; i < this.spawns.size(); i++) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("type", ((ResourceLocation)this.spawnTypes.get(i)).toString());
      NBTTagList nbttaglist2 = new NBTTagList();
      for (Point p : this.spawns.get(i)) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        p.write(nbttagcompound2, "pos");
        nbttaglist2.appendTag((NBTBase)nbttagcompound2);
      } 
      nbttagcompound1.setTag("points", (NBTBase)nbttaglist2);
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("spawns", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (i = 0; i < this.soilTypes.size(); i++) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("type", ((ResourceLocation)this.soilTypes.get(i)).toString());
      NBTTagList nbttaglist2 = new NBTTagList();
      for (Point p : this.soils.get(i)) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        p.write(nbttagcompound2, "pos");
        nbttaglist2.appendTag((NBTBase)nbttagcompound2);
      } 
      nbttagcompound1.setTag("points", (NBTBase)nbttaglist2);
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("genericsoils", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (i = 0; i < this.mobSpawners.size(); i++) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("type", ((ResourceLocation)this.mobSpawnerTypes.get(i)).toString());
      NBTTagList nbttaglist2 = new NBTTagList();
      for (Point p : this.mobSpawners.get(i)) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        p.write(nbttagcompound2, "pos");
        nbttaglist2.appendTag((NBTBase)nbttagcompound2);
      } 
      nbttagcompound1.setTag("points", (NBTBase)nbttaglist2);
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("mobspawns", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (i = 0; i < this.sources.size(); i++) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("block_rl", ((IBlockState)this.sourceTypes.get(i)).getBlock().getRegistryName().toString());
      nbttagcompound1.setInteger("block_meta", ((IBlockState)this.sourceTypes
          .get(i)).getBlock().getMetaFromState(this.sourceTypes.get(i)));
      NBTTagList nbttaglist2 = new NBTTagList();
      for (Point p : this.sources.get(i)) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        p.write(nbttagcompound2, "pos");
        nbttaglist2.appendTag((NBTBase)nbttagcompound2);
      } 
      nbttagcompound1.setTag("points", (NBTBase)nbttaglist2);
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("sources", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.chests) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("chests", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.furnaces) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("furnaces", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.firepits) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("firepits", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.brewingStands) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("brewingStands", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.banners) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("banners", (NBTBase)nbttaglist);
    nbttaglist = new NBTTagList();
    for (Point p : this.cultureBanners) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      p.write(nbttagcompound1, "pos");
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag("culturebanners", (NBTBase)nbttaglist);
  }
}
