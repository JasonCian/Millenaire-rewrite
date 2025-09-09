package org.millenaire.common.buildingplan;

import java.io.DataInputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.block.mock.MockBlockBannerHanging;
import org.millenaire.common.block.mock.MockBlockBannerStanding;
import org.millenaire.common.entity.EntityWallDecoration;
import org.millenaire.common.entity.TileEntityMockBanner;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemMockBanner;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.PathUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.WorldGenAppleTree;
import org.millenaire.common.world.WorldGenCherry;
import org.millenaire.common.world.WorldGenOliveTree;
import org.millenaire.common.world.WorldGenPistachio;
import org.millenaire.common.world.WorldGenSakura;

public class BuildingBlock {
  public static byte TAPESTRY = 1;
  
  public static byte OAKSPAWN = 2;
  
  public static byte PINESPAWN = 3;
  
  public static byte BIRCHSPAWN = 4;
  
  public static byte INDIANSTATUE = 5;
  
  public static byte PRESERVEGROUNDDEPTH = 6;
  
  public static byte CLEARTREE = 7;
  
  public static byte MAYANSTATUE = 8;
  
  public static byte SPAWNERSKELETON = 9;
  
  public static byte SPAWNERZOMBIE = 10;
  
  public static byte SPAWNERSPIDER = 11;
  
  public static byte SPAWNERCAVESPIDER = 12;
  
  public static byte SPAWNERCREEPER = 13;
  
  public static byte DISPENDERUNKNOWNPOWDER = 14;
  
  public static byte JUNGLESPAWN = 15;
  
  public static byte INVERTED_DOOR = 16;
  
  public static byte CLEARGROUND = 17;
  
  public static byte BYZANTINEICONSMALL = 18;
  
  public static byte BYZANTINEICONMEDIUM = 19;
  
  public static byte BYZANTINEICONLARGE = 20;
  
  public static byte PRESERVEGROUNDSURFACE = 21;
  
  public static byte SPAWNERBLAZE = 22;
  
  public static byte ACACIASPAWN = 23;
  
  public static byte DARKOAKSPAWN = 24;
  
  public static byte TORCHGUESS = 25;
  
  public static byte CHESTGUESS = 26;
  
  public static byte FURNACEGUESS = 27;
  
  public static byte CLEARGROUNDOUTSIDEBUILDING = 28;
  
  public static byte HIDEHANGING = 29;
  
  public static byte APPLETREESPAWN = 30;
  
  public static byte CLEARGROUNDBORDER = 31;
  
  public static byte OLIVETREESPAWN = 32;
  
  public static byte PISTACHIOTREESPAWN = 33;
  
  public static byte WALLCARPETSMALL = 40;
  
  public static byte WALLCARPETMEDIUM = 41;
  
  public static byte WALLCARPETLARGE = 42;
  
  public static byte CHERRYTREESPAWN = 43;
  
  public static byte SAKURATREESPAWN = 43;
  
  public final Block block;
  
  private byte meta;
  
  public final Point p;
  
  private IBlockState blockState;
  
  public byte special;
  
  public BuildingBlock(Point p, Block block, int meta) {
    this.p = p;
    this.block = block;
    this.meta = (byte)meta;
    this.blockState = block.getStateFromMeta(meta);
    this.special = 0;
  }
  
  public BuildingBlock(Point p, DataInputStream ds) throws IOException {
    this.p = p;
    this.block = Block.getBlockById(ds.readInt());
    this.meta = ds.readByte();
    this.special = ds.readByte();
    if (this.block != null) {
      this.blockState = this.block.getStateFromMeta(this.meta);
    } else {
      this.blockState = Blocks.AIR.getDefaultState();
    } 
  }
  
  public BuildingBlock(Point p, IBlockState bs) {
    this.p = p;
    this.block = bs.getBlock();
    this.meta = (byte)bs.getBlock().getMetaFromState(bs);
    this.blockState = bs;
    this.special = 0;
  }
  
  public BuildingBlock(Point p, int special) {
    this.p = p;
    this.block = Blocks.AIR;
    this.meta = 0;
    this.special = (byte)special;
    this.blockState = Blocks.AIR.getDefaultState();
  }
  
  public boolean alreadyDone(World world) {
    if (this.special != 0)
      return false; 
    Block block = WorldUtilities.getBlock(world, this.p);
    if (this.block != block)
      return false; 
    int meta = WorldUtilities.getBlockMeta(world, this.p);
    if (this.meta != meta)
      return false; 
    return true;
  }
  
  public boolean build(World world, Building townHall, boolean worldGeneration, boolean wandimport) {
    boolean blockSet = false;
    try {
      boolean notifyBlocks = true;
      boolean playSound = (!worldGeneration && !wandimport);
      if (this.special == 0) {
        blockSet = buildNormalBlock(world, townHall, wandimport, true, playSound);
      } else if (this.special == PRESERVEGROUNDDEPTH || this.special == PRESERVEGROUNDSURFACE) {
        blockSet = buildPreserveGround(world, worldGeneration, true, playSound);
      } else if (this.special == CLEARTREE) {
        blockSet = buildClearTree(world, worldGeneration, true, playSound);
      } else if (this.special == CLEARGROUND || this.special == CLEARGROUNDOUTSIDEBUILDING || this.special == CLEARGROUNDBORDER) {
        blockSet = buildClearGround(world, worldGeneration, wandimport, true, playSound);
      } else if (this.special == TAPESTRY || this.special == INDIANSTATUE || this.special == MAYANSTATUE || this.special == BYZANTINEICONSMALL || this.special == BYZANTINEICONMEDIUM || this.special == BYZANTINEICONLARGE || this.special == HIDEHANGING || this.special == WALLCARPETSMALL || this.special == WALLCARPETMEDIUM || this.special == WALLCARPETLARGE) {
        blockSet = buildPicture(world);
      } else if (this.special == OAKSPAWN || this.special == PINESPAWN || this.special == BIRCHSPAWN || this.special == JUNGLESPAWN || this.special == ACACIASPAWN || this.special == DARKOAKSPAWN || this.special == APPLETREESPAWN || this.special == OLIVETREESPAWN || this.special == PISTACHIOTREESPAWN || this.special == CHERRYTREESPAWN || this.special == SAKURATREESPAWN) {
        blockSet = buildTreeSpawn(world, worldGeneration);
      } else if (this.special == SPAWNERSKELETON) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.MOB_SPAWNER, 0);
        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
        tileentitymobspawner.getSpawnerBaseLogic().setEntityId(Mill.ENTITY_SKELETON);
        blockSet = true;
      } else if (this.special == SPAWNERZOMBIE) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.MOB_SPAWNER, 0);
        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
        tileentitymobspawner.getSpawnerBaseLogic().setEntityId(Mill.ENTITY_ZOMBIE);
        blockSet = true;
      } else if (this.special == SPAWNERSPIDER) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.MOB_SPAWNER, 0);
        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
        tileentitymobspawner.getSpawnerBaseLogic().setEntityId(Mill.ENTITY_SPIDER);
        blockSet = true;
      } else if (this.special == SPAWNERCAVESPIDER) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.MOB_SPAWNER, 0);
        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
        tileentitymobspawner.getSpawnerBaseLogic().setEntityId(Mill.ENTITY_CAVESPIDER);
        blockSet = true;
      } else if (this.special == SPAWNERCREEPER) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.MOB_SPAWNER, 0);
        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
        tileentitymobspawner.getSpawnerBaseLogic().setEntityId(new ResourceLocation("creeper"));
        blockSet = true;
      } else if (this.special == SPAWNERBLAZE) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.MOB_SPAWNER, 0);
        TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)this.p.getTileEntity(world);
        tileentitymobspawner.getSpawnerBaseLogic().setEntityId(new ResourceLocation("blaze"));
        blockSet = true;
      } else if (this.special == DISPENDERUNKNOWNPOWDER) {
        WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.DISPENSER, 0);
        TileEntityDispenser dispenser = this.p.getDispenser(world);
        MillCommonUtilities.putItemsInChest((IInventory)dispenser, (Item)MillItems.UNKNOWN_POWDER, 2);
        blockSet = true;
      } else if (this.special == FURNACEGUESS) {
        EnumFacing facing = guessChestFurnaceFacing(world, this.p);
        IBlockState furnaceBS = Blocks.FURNACE.getDefaultState().withProperty((IProperty)BlockFurnace.FACING, (Comparable)facing);
        world.setBlockState(this.p.getBlockPos(), furnaceBS);
        blockSet = true;
      } else if (this.special == CHESTGUESS) {
        EnumFacing facing = guessChestFurnaceFacing(world, this.p);
        IBlockState chestBS = MillBlocks.LOCKED_CHEST.getDefaultState().withProperty((IProperty)BlockChest.FACING, (Comparable)facing);
        world.setBlockState(this.p.getBlockPos(), chestBS);
        blockSet = true;
      } else if (this.special == TORCHGUESS) {
        BlockTorch blockTorch = (BlockTorch)Blocks.TORCH;
        IBlockState bs = blockTorch.getStateForPlacement(world, this.p.getBlockPos(), EnumFacing.UP, 0.0F, 0.0F, 0.0F, 0, null);
        world.setBlockState(this.p.getBlockPos(), bs);
        blockSet = true;
      } else if (this.special == INVERTED_DOOR) {
        world.setBlockState(this.p.getBlockPos(), this.blockState);
        if (this.blockState.getValue((IProperty)BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
          IBlockState bs = this.p.getBlockActualState(world).withProperty((IProperty)BlockDoor.HALF, (Comparable)BlockDoor.EnumDoorHalf.UPPER);
          bs = bs.withProperty((IProperty)BlockDoor.HINGE, (Comparable)BlockDoor.EnumHingePosition.RIGHT);
          WorldUtilities.setBlockstate(world, this.p.getAbove(), bs, true, playSound);
        } 
        blockSet = true;
      } 
    } catch (Exception e) {
      MillLog.printException("Exception in BuildingBlock.build():", e);
    } 
    return blockSet;
  }
  
  private boolean buildClearGround(World world, boolean worldGeneration, boolean wandimport, boolean notifyBlocks, boolean playSound) {
    boolean shouldSetBlock = false;
    boolean shouldSetBlockBelow = false;
    Block existingBlock = WorldUtilities.getBlock(world, this.p);
    IBlockState targetBlockState = null;
    IBlockState targetBelowBlockState = null;
    if (!wandimport || (existingBlock != Blocks.STANDING_SIGN && existingBlock != MillBlocks.IMPORT_TABLE))
      if (!BlockItemUtilities.isBlockDecorativePlant(existingBlock))
        if (this.special == CLEARGROUNDBORDER && !(existingBlock instanceof BlockLeaves) && existingBlock != Blocks.AIR) {
          if (this.p.getEast().getBlock(world) instanceof net.minecraft.block.BlockLiquid || this.p.getWest().getBlock(world) instanceof net.minecraft.block.BlockLiquid || this.p.getNorth().getBlock(world) instanceof net.minecraft.block.BlockLiquid || this.p
            .getSouth().getBlock(world) instanceof net.minecraft.block.BlockLiquid) {
            IBlockState iBlockState = WorldUtilities.getBlockState(world, this.p.getBelow());
            targetBlockState = WorldUtilities.getBlockStateValidGround(iBlockState, true);
            if (targetBlockState == null)
              targetBlockState = Blocks.DIRT.getDefaultState(); 
            if (existingBlock != targetBlockState.getBlock())
              shouldSetBlock = true; 
          } else if (existingBlock != Blocks.AIR) {
            targetBlockState = Blocks.AIR.getDefaultState();
            shouldSetBlock = true;
          } 
        } else if (existingBlock != Blocks.AIR && ((this.special != CLEARGROUNDOUTSIDEBUILDING && this.special != CLEARGROUNDBORDER) || !(existingBlock instanceof BlockLeaves))) {
          targetBlockState = Blocks.AIR.getDefaultState();
          shouldSetBlock = true;
        }   
    IBlockState blockStateBelow = WorldUtilities.getBlockState(world, this.p.getBelow());
    targetBelowBlockState = WorldUtilities.getBlockStateValidGround(blockStateBelow, true);
    if (worldGeneration && targetBelowBlockState == Blocks.DIRT.getDefaultState()) {
      targetBelowBlockState = Blocks.GRASS.getDefaultState();
      shouldSetBlockBelow = true;
    } else if (targetBlockState != null) {
      if (targetBlockState != Blocks.DIRT.getDefaultState() || blockStateBelow.getBlock() != Blocks.GRASS)
        shouldSetBlock = true; 
    } 
    if (shouldSetBlock)
      WorldUtilities.setBlockstate(world, this.p, targetBlockState, notifyBlocks, playSound); 
    if (shouldSetBlockBelow)
      WorldUtilities.setBlockstate(world, this.p.getBelow(), targetBelowBlockState, notifyBlocks, playSound); 
    return (shouldSetBlock || shouldSetBlockBelow);
  }
  
  private boolean buildClearTree(World world, boolean worldGeneration, boolean notifyBlocks, boolean playSound) {
    Block block = WorldUtilities.getBlock(world, this.p);
    if (block instanceof net.minecraft.block.BlockLog) {
      WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.AIR, 0, notifyBlocks, playSound);
      IBlockState blockStateBelow = WorldUtilities.getBlockState(world, this.p.getBelow());
      IBlockState targetBlockState = WorldUtilities.getBlockStateValidGround(blockStateBelow, true);
      if (worldGeneration && targetBlockState != null && targetBlockState.getBlock() == Blocks.DIRT) {
        WorldUtilities.setBlock(world, this.p.getBelow(), (Block)Blocks.GRASS, notifyBlocks, playSound);
      } else if (targetBlockState != null) {
        if (targetBlockState != Blocks.DIRT.getDefaultState() || block != Blocks.GRASS)
          WorldUtilities.setBlockstate(world, this.p.getBelow(), targetBlockState, notifyBlocks, playSound); 
      } 
      return true;
    } 
    return false;
  }
  
  private boolean buildNormalBlock(World world, Building townHall, boolean wandimport, boolean notifyBlocks, boolean playSound) {
    boolean blockSet = false;
    if (this.block instanceof BlockDoor) {
      if (this.blockState.getValue((IProperty)BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER)
        WorldUtilities.setBlockAndMetadata(world, this.p.getAbove(), Blocks.AIR, 0, notifyBlocks, playSound); 
    } else if (this.block instanceof net.minecraft.block.BlockBed) {
      EnumFacing facing = (EnumFacing)this.blockState.getValue((IProperty)BlockHorizontal.FACING);
      if (facing == EnumFacing.EAST) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getWest(), Blocks.AIR, 0, notifyBlocks, playSound);
      } else if (facing == EnumFacing.SOUTH) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getNorth(), Blocks.AIR, 0, notifyBlocks, playSound);
      } else if (facing == EnumFacing.WEST) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getEast(), Blocks.AIR, 0, notifyBlocks, playSound);
      } else if (facing == EnumFacing.NORTH) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getSouth(), Blocks.AIR, 0, notifyBlocks, playSound);
      } 
    } 
    if (!wandimport || this.block != Blocks.AIR || WorldUtilities.getBlock(world, this.p) != Blocks.STANDING_SIGN) {
      Block existingBlock = WorldUtilities.getBlock(world, this.p);
      if (this.block == Blocks.AIR) {
        if (!BlockItemUtilities.isBlockDecorativePlant(existingBlock)) {
          WorldUtilities.setBlockAndMetadata(world, this.p, this.block, this.meta, notifyBlocks, playSound);
          blockSet = true;
        } 
      } else if (this.block instanceof BlockFlowerPot) {
        if (this.meta == -1)
          this.meta = 0; 
        WorldUtilities.setBlockstate(world, this.p, this.blockState.withProperty((IProperty)BlockFlowerPot.CONTENTS, (Comparable)BlockFlowerPot.EnumFlowerType.values()[this.meta]), notifyBlocks, playSound);
      } else {
        if (existingBlock instanceof net.minecraft.block.BlockBed)
          existingBlock.breakBlock(world, this.p.getBlockPos(), world.getBlockState(this.p.getBlockPos())); 
        if (this.block instanceof net.minecraft.block.BlockBed) {
          WorldUtilities.setBlockAndMetadata(world, this.p, Blocks.AIR, 0, notifyBlocks, playSound);
          EnumFacing facing = (EnumFacing)this.blockState.getValue((IProperty)BlockHorizontal.FACING);
          if (facing == EnumFacing.EAST) {
            WorldUtilities.setBlockAndMetadata(world, this.p.getWest(), Blocks.AIR, 0, notifyBlocks, playSound);
          } else if (facing == EnumFacing.SOUTH) {
            WorldUtilities.setBlockAndMetadata(world, this.p.getNorth(), Blocks.AIR, 0, notifyBlocks, playSound);
          } else if (facing == EnumFacing.WEST) {
            WorldUtilities.setBlockAndMetadata(world, this.p.getEast(), Blocks.AIR, 0, notifyBlocks, playSound);
          } else if (facing == EnumFacing.NORTH) {
            WorldUtilities.setBlockAndMetadata(world, this.p.getSouth(), Blocks.AIR, 0, notifyBlocks, playSound);
          } 
        } 
        if (this.blockState != Blocks.DIRT.getDefaultState() || existingBlock != Blocks.GRASS) {
          WorldUtilities.setBlockAndMetadata(world, this.p, this.block, this.meta, notifyBlocks, playSound);
          blockSet = true;
        } 
      } 
    } 
    if (this.block instanceof BlockDoor) {
      if (this.blockState.getValue((IProperty)BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
        IBlockState bs = this.blockState.withProperty((IProperty)BlockDoor.HALF, (Comparable)BlockDoor.EnumDoorHalf.UPPER);
        if (this.special == INVERTED_DOOR)
          bs = bs.withProperty((IProperty)BlockDoor.HINGE, (Comparable)BlockDoor.EnumHingePosition.RIGHT); 
        WorldUtilities.setBlockstate(world, this.p.getAbove(), bs, notifyBlocks, playSound);
      } 
    } else if (this.block instanceof net.minecraft.block.BlockBed) {
      EnumFacing facing = (EnumFacing)this.blockState.getValue((IProperty)BlockHorizontal.FACING);
      if (facing == EnumFacing.EAST) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getWest(), this.block, this.meta - 8, notifyBlocks, playSound);
      } else if (facing == EnumFacing.SOUTH) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getNorth(), this.block, this.meta - 8, notifyBlocks, playSound);
      } else if (facing == EnumFacing.WEST) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getEast(), this.block, this.meta - 8, notifyBlocks, playSound);
      } else if (facing == EnumFacing.NORTH) {
        WorldUtilities.setBlockAndMetadata(world, this.p.getSouth(), this.block, this.meta - 8, notifyBlocks, playSound);
      } 
    } else if (this.block == Blocks.WATER) {
      world.setBlockState(this.p.getBlockPos(), Blocks.FLOWING_WATER.getDefaultState(), 11);
    } else if (this.block == Blocks.PORTAL) {
      Blocks.PORTAL.trySpawnPortal(world, this.p.getBlockPos());
    } else if (this.block instanceof BlockDoublePlant) {
      IBlockState bs = this.blockState.withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER);
      WorldUtilities.setBlockstate(world, this.p.getAbove(), bs, notifyBlocks, playSound);
    } else if (this.block instanceof BlockFlowerPot) {
      TileEntity te = this.p.getTileEntity(world);
      if (te instanceof TileEntityFlowerPot) {
        TileEntityFlowerPot teFlowerPot = (TileEntityFlowerPot)te;
        teFlowerPot.setItemStack(BlockItemUtilities.getFlowerpotItemStackFromEnum(BlockFlowerPot.EnumFlowerType.values()[this.meta]));
        teFlowerPot.markDirty();
        world.notifyBlockUpdate(this.p.getBlockPos(), this.blockState, this.blockState.withProperty((IProperty)BlockFlowerPot.CONTENTS, (Comparable)BlockFlowerPot.EnumFlowerType.values()[this.meta]), 3);
      } 
    } else if (this.block instanceof MockBlockBannerHanging) {
      MockBlockBannerHanging bannerBlock = (MockBlockBannerHanging)this.block;
      TileEntity bannerEntity = world.getTileEntity(this.p.getBlockPos());
      if (bannerEntity instanceof TileEntityMockBanner)
        try {
          if (townHall == null) {
            ItemStack bannerStack = ItemMockBanner.makeBanner(Item.getItemFromBlock((Block)bannerBlock), ItemMockBanner.BANNER_COLOURS[bannerBlock.bannerType], 
                JsonToNBT.getTagFromJson(ItemMockBanner.BANNER_DESIGNS[bannerBlock.bannerType]));
            bannerStack.getOrCreateSubCompound("BlockEntityTag").setTag("Base", (NBTBase)new NBTTagInt(ItemMockBanner.BANNER_COLOURS[bannerBlock.bannerType].getDyeDamage()));
            ((TileEntityMockBanner)bannerEntity).setItemValues(bannerStack, true);
          } else if (bannerBlock.bannerType == ItemMockBanner.BANNER_VILLAGE) {
            ((TileEntityMockBanner)bannerEntity).setItemValues(townHall.getBannerStack(), true);
          } else {
            ((TileEntityMockBanner)bannerEntity).setItemValues(townHall.culture.cultureBannerItemStack, true);
          } 
        } catch (NBTException e) {
          MillLog.printException((Throwable)e);
        }  
    } else if (this.block instanceof MockBlockBannerStanding) {
      MockBlockBannerStanding bannerBlock = (MockBlockBannerStanding)this.block;
      TileEntity bannerEntity = world.getTileEntity(this.p.getBlockPos());
      if (bannerEntity instanceof TileEntityMockBanner)
        try {
          if (townHall == null) {
            ItemStack bannerStack = ItemMockBanner.makeBanner(Item.getItemFromBlock((Block)bannerBlock), ItemMockBanner.BANNER_COLOURS[bannerBlock.bannerType], 
                JsonToNBT.getTagFromJson(ItemMockBanner.BANNER_DESIGNS[bannerBlock.bannerType]));
            bannerStack.getOrCreateSubCompound("BlockEntityTag").setTag("Base", (NBTBase)new NBTTagInt(ItemMockBanner.BANNER_COLOURS[bannerBlock.bannerType].getDyeDamage()));
            ((TileEntityMockBanner)bannerEntity).setItemValues(bannerStack, true);
          } else if (bannerBlock.bannerType == ItemMockBanner.BANNER_VILLAGE) {
            ((TileEntityMockBanner)bannerEntity).setItemValues(townHall.getBannerStack(), true);
          } else {
            ((TileEntityMockBanner)bannerEntity).setItemValues(townHall.culture.cultureBannerItemStack, true);
          } 
        } catch (NBTException e) {
          MillLog.printException((Throwable)e);
        }  
    } 
    return blockSet;
  }
  
  private boolean buildPicture(World world) {
    EntityWallDecoration art = null;
    if (this.special == TAPESTRY) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 1);
    } else if (this.special == INDIANSTATUE) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 2);
    } else if (this.special == MAYANSTATUE) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 3);
    } else if (this.special == BYZANTINEICONSMALL) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 4);
    } else if (this.special == BYZANTINEICONMEDIUM) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 5);
    } else if (this.special == BYZANTINEICONLARGE) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 6);
    } else if (this.special == HIDEHANGING) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 7);
    } else if (this.special == WALLCARPETSMALL) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 8);
    } else if (this.special == WALLCARPETMEDIUM) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 9);
    } else if (this.special == WALLCARPETLARGE) {
      art = EntityWallDecoration.createWallDecoration(world, this.p, 10);
    } 
    if (art.onValidSurface() && 
      !world.isRemote) {
      world.spawnEntity((Entity)art);
      return true;
    } 
    return false;
  }
  
  private boolean buildPreserveGround(World world, boolean worldGeneration, boolean notifyBlocks, boolean playSound) {
    IBlockState existingBlockState = WorldUtilities.getBlockState(world, this.p);
    boolean surface = (this.special == PRESERVEGROUNDSURFACE);
    if (!surface && existingBlockState.isFullBlock() && existingBlockState.isFullCube()) {
      Material material = existingBlockState.getMaterial();
      if (material == Material.GROUND || material == Material.ROCK || material == Material.SAND || material == Material.CLAY)
        return false; 
    } 
    IBlockState targetGroundBlockState = WorldUtilities.getBlockStateValidGround(existingBlockState, surface);
    if (targetGroundBlockState == null) {
      Point below = this.p.getBelow();
      while (targetGroundBlockState == null && below.getiY() > 0) {
        this.blockState = WorldUtilities.getBlockState(world, below);
        if (WorldUtilities.getBlockStateValidGround(this.blockState, surface) != null)
          targetGroundBlockState = WorldUtilities.getBlockStateValidGround(this.blockState, surface); 
        below = below.getBelow();
      } 
      if (targetGroundBlockState == null)
        targetGroundBlockState = Blocks.DIRT.getDefaultState(); 
    } 
    if (targetGroundBlockState.getBlock() == Blocks.DIRT && worldGeneration && surface)
      targetGroundBlockState = Blocks.GRASS.getDefaultState(); 
    if (targetGroundBlockState.getBlock() == Blocks.GRASS && !worldGeneration)
      targetGroundBlockState = Blocks.DIRT.getDefaultState(); 
    if (targetGroundBlockState == null || targetGroundBlockState.getBlock() == Blocks.AIR)
      if (worldGeneration && surface) {
        targetGroundBlockState = Blocks.GRASS.getDefaultState();
      } else {
        targetGroundBlockState = Blocks.DIRT.getDefaultState();
      }  
    if (targetGroundBlockState == existingBlockState)
      return false; 
    if (existingBlockState.getBlock() == Blocks.GRASS && targetGroundBlockState.getBlock() == Blocks.DIRT)
      return false; 
    WorldUtilities.setBlockstate(world, this.p, targetGroundBlockState, notifyBlocks, playSound);
    return true;
  }
  
  private boolean buildTreeSpawn(World world, boolean worldGeneration) {
    if (worldGeneration) {
      WorldGenerator wg = null;
      if (this.special == OAKSPAWN) {
        wg = new WorldGenTrees(false);
      } else if (this.special == PINESPAWN) {
        wg = new WorldGenTaiga2(false);
      } else if (this.special == BIRCHSPAWN) {
        wg = new WorldGenBirchTree(false, true);
      } else if (this.special == JUNGLESPAWN) {
        IBlockState iblockstate = Blocks.LOG.getDefaultState().withProperty((IProperty)BlockOldLog.VARIANT, (Comparable)BlockPlanks.EnumType.JUNGLE);
        IBlockState iblockstate1 = Blocks.LEAVES.getDefaultState().withProperty((IProperty)BlockOldLeaf.VARIANT, (Comparable)BlockPlanks.EnumType.JUNGLE).withProperty((IProperty)BlockLeaves.CHECK_DECAY, 
            Boolean.valueOf(false));
        wg = new WorldGenTrees(true, 4 + MillCommonUtilities.random.nextInt(7), iblockstate, iblockstate1, false);
      } else if (this.special == ACACIASPAWN) {
        wg = new WorldGenSavannaTree(false);
      } else if (this.special == DARKOAKSPAWN) {
        wg = new WorldGenCanopyTree(true);
      } else if (this.special == APPLETREESPAWN) {
        wg = new WorldGenAppleTree(true);
      } else if (this.special == OLIVETREESPAWN) {
        wg = new WorldGenOliveTree(true);
      } else if (this.special == PISTACHIOTREESPAWN) {
        wg = new WorldGenPistachio(true);
      } else if (this.special == CHERRYTREESPAWN) {
        wg = new WorldGenCherry(true);
      } else if (this.special == SAKURATREESPAWN) {
        wg = new WorldGenSakura(true);
      } 
      
      if (wg != null) {
        wg.generate(world, MillCommonUtilities.random, this.p.getBlockPos());
        return true;
      }
    } 
    return false;
  }
  
  public IBlockState getBlockstate() {
    return this.blockState;
  }
  
  public byte getMeta() {
    return this.meta;
  }
  
  private EnumFacing guessChestFurnaceFacing(World world, Point p) {
    IBlockState bsNorth = p.getNorth().getBlockActualState(world);
    IBlockState bsSouth = p.getSouth().getBlockActualState(world);
    IBlockState bsWest = p.getWest().getBlockActualState(world);
    IBlockState bsEast = p.getEast().getBlockActualState(world);
    if (bsNorth.isOpaqueCube() && bsNorth.getBlock() != Blocks.FURNACE && bsNorth.getBlock() != MillBlocks.LOCKED_CHEST)
      if (!bsSouth.isOpaqueCube())
        return EnumFacing.SOUTH;  
    if (bsSouth.isOpaqueCube() && bsSouth.getBlock() != Blocks.FURNACE && bsSouth.getBlock() != MillBlocks.LOCKED_CHEST && 
      !bsNorth.isOpaqueCube())
      return EnumFacing.NORTH; 
    if (bsWest.isOpaqueCube() && bsWest.getBlock() != Blocks.FURNACE && bsWest.getBlock() != MillBlocks.LOCKED_CHEST && 
      !bsEast.isOpaqueCube())
      return EnumFacing.EAST; 
    if (bsEast.isOpaqueCube() && bsEast.getBlock() != Blocks.FURNACE && bsEast.getBlock() != MillBlocks.LOCKED_CHEST && 
      !bsWest.isOpaqueCube())
      return EnumFacing.WEST; 
    if (!bsSouth.isOpaqueCube())
      return EnumFacing.SOUTH; 
    if (!bsNorth.isOpaqueCube())
      return EnumFacing.NORTH; 
    if (!bsEast.isOpaqueCube())
      return EnumFacing.EAST; 
    if (!bsWest.isOpaqueCube())
      return EnumFacing.WEST; 
    return EnumFacing.NORTH;
  }
  
  public void pathBuild(Building th) {
    IBlockState currentBlockState = this.p.getBlockActualState(th.world);
    if (!BlockItemUtilities.isPath(currentBlockState.getBlock()) && PathUtilities.canPathBeBuiltHere(currentBlockState)) {
      build(th.world, null, false, false);
    } else if (BlockItemUtilities.isPath(currentBlockState.getBlock())) {
      int targetPathLevel = 0;
      IBlockPath bp = (IBlockPath)this.block;
      for (int i = 0; i < th.villageType.pathMaterial.size(); i++) {
        if (BlockItemUtilities.isPath(this.block) && ((
          (InvItem)th.villageType.pathMaterial.get(i)).getBlock() == bp.getSingleSlab() || ((InvItem)th.villageType.pathMaterial.get(i)).getBlock() == bp.getDoubleSlab()))
          targetPathLevel = i; 
      } 
      int currentPathLevel = Integer.MAX_VALUE;
      IBlockPath currentPathBlock = (IBlockPath)currentBlockState.getBlock();
      for (int j = 0; j < th.villageType.pathMaterial.size(); j++) {
        if (((InvItem)th.villageType.pathMaterial.get(j)).getBlock() == currentPathBlock.getDoubleSlab() || ((InvItem)th.villageType.pathMaterial.get(j)).getBlock() == currentPathBlock.getSingleSlab())
          currentPathLevel = j; 
      } 
      if (currentPathLevel < targetPathLevel)
        build(th.world, null, false, false); 
    } 
  }
  
  public void setBlockstate(IBlockState bs) {
    this.blockState = bs;
    this.meta = (byte)this.block.getMetaFromState(bs);
  }
  
  public void setMeta(byte meta) {
    this.meta = meta;
    this.blockState = this.block.getStateFromMeta(meta);
  }
  
  public String toString() {
    return "(block: " + this.block + " meta: " + this.meta + " pos:" + this.p + ")";
  }
}
