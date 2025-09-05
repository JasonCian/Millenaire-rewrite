package org.millenaire.common.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class BlockItemUtilities {
  private static final Map<Material, String> MATERIAL_NAME_MAP = createMaterialNameMap();
  
  private static final Set<String> FORBIDDEN_MATERIALS = new HashSet<>(20);
  
  private static final Set<String> FORBIDDEN_BLOCKS = new HashSet<>(20);
  
  private static final Set<String> FORBIDDEN_EXCEPTIONS = new HashSet<>(20);
  
  private static final Set<String> GROUND_MATERIALS = new HashSet<>(20);
  
  private static final Set<String> GROUND_BLOCKS = new HashSet<>(20);
  
  private static final Set<String> GROUND_EXCEPTIONS = new HashSet<>(20);
  
  private static final Set<String> DANGER_MATERIALS = new HashSet<>(20);
  
  private static final Set<String> DANGER_BLOCKS = new HashSet<>(20);
  
  private static final Set<String> DANGER_EXCEPTIONS = new HashSet<>(20);
  
  private static final Set<String> WATER_MATERIALS = new HashSet<>(20);
  
  private static final Set<String> WATER_BLOCKS = new HashSet<>(20);
  
  private static final Set<String> WATER_EXCEPTIONS = new HashSet<>(20);
  
  private static final Set<String> PATH_REPLACEABLE_MATERIALS = new HashSet<>(20);
  
  private static final Set<String> PATH_REPLACEABLE_BLOCKS = new HashSet<>(20);
  
  private static final Set<String> PATH_REPLACEABLE_EXCEPTIONS = new HashSet<>(20);
  
  public static void checkForHarvestTheft(EntityPlayer player, BlockPos pos) {
    MillWorldData mwd = Mill.getMillWorld(player.world);
    Point actionPos = new Point(pos);
    Building closestVillageTH = mwd.getClosestVillage(actionPos);
    if (closestVillageTH != null && !closestVillageTH.controlledBy(player)) {
      BuildingLocation location = closestVillageTH.getLocationAtCoord(actionPos);
      if (location != null) {
        Building building = location.getBuilding(player.world);
        if (building != null) {
          boolean isBuildingPlayerOwned = (building.location.getPlan() != null && ((building.location.getPlan()).price > 0 || (building.location.getPlan()).isgift));
          if (!isBuildingPlayerOwned) {
            UserProfile serverProfile = VillageUtilities.getServerProfile(player.world, player);
            if (serverProfile != null) {
              int reputationLost = 100;
              serverProfile.adjustReputation(closestVillageTH, -100);
              ServerSender.sendTranslatedSentence(player, '6', "ui.stealingcrops", new String[] { "100" });
            } 
          } 
        } 
      } 
    } 
  }
  
  private static Map<Material, String> createMaterialNameMap() {
    Map<Material, String> result = new HashMap<>(50);
    result.put(Material.AIR, "air");
    result.put(Material.GRASS, "grass");
    result.put(Material.GROUND, "ground");
    result.put(Material.WOOD, "wood");
    result.put(Material.ROCK, "rock");
    result.put(Material.IRON, "iron");
    result.put(Material.ANVIL, "anvil");
    result.put(Material.WATER, "water");
    result.put(Material.LAVA, "lava");
    result.put(Material.LEAVES, "leaves");
    result.put(Material.PLANTS, "plants");
    result.put(Material.VINE, "vine");
    result.put(Material.SPONGE, "sponge");
    result.put(Material.CLOTH, "cloth");
    result.put(Material.FIRE, "fire");
    result.put(Material.SAND, "sand");
    result.put(Material.CIRCUITS, "circuits");
    result.put(Material.CARPET, "carpet");
    result.put(Material.GLASS, "glass");
    result.put(Material.REDSTONE_LIGHT, "redstone_light");
    result.put(Material.TNT, "tnt");
    result.put(Material.CORAL, "coral");
    result.put(Material.ICE, "ice");
    result.put(Material.PACKED_ICE, "packed_ice");
    result.put(Material.SNOW, "snow");
    result.put(Material.CRAFTED_SNOW, "crafted_snow");
    result.put(Material.CACTUS, "cactus");
    result.put(Material.CLAY, "clay");
    result.put(Material.GOURD, "gourd");
    result.put(Material.DRAGON_EGG, "dragon_egg");
    result.put(Material.PORTAL, "portal");
    result.put(Material.CAKE, "cake");
    result.put(Material.WEB, "web");
    return Collections.unmodifiableMap(result);
  }
  
  public static String getBlockCanonicalName(Block block) {
    if (block != null)
      return ((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString(); 
    return null;
  }
  
  public static String getBlockMaterialName(Block block) {
    if (block != null && block.getDefaultState() != null)
      return MATERIAL_NAME_MAP.get(block.getDefaultState().getMaterial()); 
    return null;
  }
  
  public static ItemStack getFlowerpotItemStackFromEnum(BlockFlowerPot.EnumFlowerType type) {
    switch (type) {
      case POPPY:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 0);
      case BLUE_ORCHID:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 1);
      case ALLIUM:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 2);
      case HOUSTONIA:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 3);
      case RED_TULIP:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 4);
      case ORANGE_TULIP:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 5);
      case WHITE_TULIP:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 6);
      case PINK_TULIP:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 7);
      case OXEYE_DAISY:
        return new ItemStack((Block)Blocks.RED_FLOWER, 1, 8);
      case DANDELION:
        return new ItemStack((Block)Blocks.YELLOW_FLOWER, 1, 0);
      case OAK_SAPLING:
        return new ItemStack(Blocks.SAPLING, 1, 0);
      case SPRUCE_SAPLING:
        return new ItemStack(Blocks.SAPLING, 1, 1);
      case BIRCH_SAPLING:
        return new ItemStack(Blocks.SAPLING, 1, 2);
      case JUNGLE_SAPLING:
        return new ItemStack(Blocks.SAPLING, 1, 3);
      case ACACIA_SAPLING:
        return new ItemStack(Blocks.SAPLING, 1, 4);
      case DARK_OAK_SAPLING:
        return new ItemStack(Blocks.SAPLING, 1, 5);
      case MUSHROOM_RED:
        return new ItemStack((Block)Blocks.RED_MUSHROOM, 1, 0);
      case MUSHROOM_BROWN:
        return new ItemStack((Block)Blocks.BROWN_MUSHROOM, 1, 0);
      case DEAD_BUSH:
        return new ItemStack((Block)Blocks.DEADBUSH, 1, 0);
      case FERN:
        return new ItemStack((Block)Blocks.TALLGRASS, 1, 2);
      case CACTUS:
        return new ItemStack((Block)Blocks.CACTUS, 1, 0);
    } 
    return ItemStack.EMPTY;
  }
  
  public static ItemStack getItemStackFromBlockState(IBlockState state, int quantity) {
    return new ItemStack(state.getBlock(), quantity, state.getBlock().getMetaFromState(state));
  }
  
  public static IBlockState getLogBlockstateFromPlankMeta(int plankMeta) {
    if (plankMeta < 4)
      return Blocks.LOG.getStateFromMeta(plankMeta); 
    return Blocks.LOG2.getStateFromMeta(plankMeta - 4);
  }
  
  public static void initBlockTypes() {
    File mainBlockTypesFile = new File(MillCommonUtilities.getMillenaireContentDir(), "blocktypes.txt");
    if (!mainBlockTypesFile.exists()) {
      System.err.println("ERROR: Could not find the blocktypes file at " + mainBlockTypesFile.getAbsolutePath());
      Mill.startupError = true;
      return;
    } 
    boolean success = readBlockTypesFile(mainBlockTypesFile);
    if (!success) {
      System.err.println("ERROR: Could not read the blocktypes file at " + mainBlockTypesFile.getAbsolutePath());
      Mill.startupError = true;
      return;
    } 
    File customBlockTypesFile = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "blocktypes.txt");
    if (customBlockTypesFile.exists())
      readBlockTypesFile(customBlockTypesFile); 
  }
  
  public static boolean isBlockDangerous(Block b) {
    if (b == null || b == Blocks.AIR || DANGER_EXCEPTIONS.contains(getBlockCanonicalName(b)))
      return false; 
    if (DANGER_MATERIALS.contains(getBlockMaterialName(b)))
      return true; 
    if (DANGER_BLOCKS.contains(getBlockCanonicalName(b)))
      return true; 
    return false;
  }
  
  public static boolean isBlockDecorativePlant(Block block) {
    if (block == null || block == Blocks.AIR)
      return false; 
    if (block instanceof org.millenaire.common.block.BlockMillCrops || block instanceof net.minecraft.block.BlockCrops)
      return false; 
    if (block instanceof net.minecraft.block.BlockBush)
      return true; 
    return false;
  }
  
  public static boolean isBlockForbidden(Block b) {
    if (b == null || b == Blocks.AIR || FORBIDDEN_EXCEPTIONS.contains(getBlockCanonicalName(b)))
      return false; 
    if (b.hasTileEntity(b.getDefaultState()))
      return true; 
    if (FORBIDDEN_MATERIALS.contains(getBlockMaterialName(b)))
      return true; 
    if (FORBIDDEN_BLOCKS.contains(getBlockCanonicalName(b)))
      return true; 
    return false;
  }
  
  public static boolean isBlockGround(Block b) {
    if (b == null || b == Blocks.AIR || GROUND_EXCEPTIONS.contains(getBlockCanonicalName(b)))
      return false; 
    if (GROUND_MATERIALS.contains(getBlockMaterialName(b)))
      return true; 
    if (GROUND_BLOCKS.contains(getBlockCanonicalName(b)))
      return true; 
    return false;
  }
  
  public static boolean isBlockLiquid(Block b) {
    if (b == null || b == Blocks.AIR)
      return false; 
    if (b instanceof net.minecraft.block.BlockLiquid || b instanceof net.minecraftforge.fluids.IFluidBlock)
      return true; 
    return false;
  }
  
  public static boolean isBlockOpaqueCube(Block block) {
    IBlockState bs = block.getDefaultState();
    return bs.isFullBlock();
  }
  
  public static boolean isBlockPathReplaceable(Block b) {
    if (b == null)
      return false; 
    if (b == Blocks.AIR)
      return false; 
    if (PATH_REPLACEABLE_EXCEPTIONS.contains(getBlockCanonicalName(b)))
      return false; 
    if (PATH_REPLACEABLE_MATERIALS.contains(getBlockMaterialName(b)))
      return true; 
    if (PATH_REPLACEABLE_BLOCKS.contains(getBlockCanonicalName(b)))
      return true; 
    return false;
  }
  
  public static boolean isBlockSolid(Block b) {
    if (b == null)
      return false; 
    if (b.getDefaultState().isFullBlock() || b.getDefaultState().isTopSolid())
      return true; 
    if (b == Blocks.GLASS || b == Blocks.GLASS_PANE || b == Blocks.STONE_SLAB || b instanceof net.minecraft.block.BlockSlab || b instanceof net.minecraft.block.BlockStairs || isFence(b) || b == MillBlocks.PAPER_WALL || b instanceof org.millenaire.common.block.IBlockPath || b instanceof net.minecraft.block.BlockFarmland)
      return true; 
    return false;
  }
  
  public static boolean isBlockWalkable(Block b) {
    if (b == null)
      return false; 
    if (b.getDefaultState().isFullBlock() || b.getDefaultState().isTopSolid())
      return true; 
    if (b == Blocks.GLASS || b == Blocks.STONE_SLAB || b instanceof net.minecraft.block.BlockSlab || b instanceof net.minecraft.block.BlockStairs || b instanceof org.millenaire.common.block.IBlockPath || b instanceof net.minecraft.block.BlockFarmland)
      return true; 
    return false;
  }
  
  public static boolean isBlockWater(Block b) {
    if (b == null || b == Blocks.AIR || WATER_EXCEPTIONS.contains(getBlockCanonicalName(b)))
      return false; 
    if (WATER_MATERIALS.contains(getBlockMaterialName(b)))
      return true; 
    if (WATER_BLOCKS.contains(getBlockCanonicalName(b)))
      return true; 
    return false;
  }
  
  public static boolean isFence(Block block) {
    return (block == Blocks.ACACIA_FENCE || block == Blocks.BIRCH_FENCE || block == Blocks.DARK_OAK_FENCE || block == Blocks.JUNGLE_FENCE || block == Blocks.OAK_FENCE || block == Blocks.SPRUCE_FENCE);
  }
  
  public static boolean isFenceGate(Block block) {
    return (block == Blocks.ACACIA_FENCE_GATE || block == Blocks.BIRCH_FENCE_GATE || block == Blocks.DARK_OAK_FENCE_GATE || block == Blocks.JUNGLE_FENCE_GATE || block == Blocks.OAK_FENCE_GATE || block == Blocks.SPRUCE_FENCE_GATE);
  }
  
  public static boolean isPath(Block block) {
    return (block instanceof org.millenaire.common.block.IBlockPath || block instanceof org.millenaire.common.block.BlockPathSlab);
  }
  
  public static boolean isPathSlab(Block block) {
    if (block instanceof org.millenaire.common.block.BlockPathSlab)
      return true; 
    return false;
  }
  
  public static boolean isWoodenDoor(Block block) {
    return (block instanceof net.minecraft.block.BlockDoor && block.getMaterial(null) == Material.WOOD);
  }
  
  private static boolean readBlockTypesFile(File file) {
    if (!file.exists())
      return false; 
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line = reader.readLine();
      while (line != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] temp = line.split("=");
          if (temp.length == 2) {
            String key = temp[0].trim().toLowerCase();
            String value = temp[1];
            if (key.equals("forbidden_materials")) {
              FORBIDDEN_MATERIALS.clear();
              FORBIDDEN_MATERIALS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("forbidden_blocks")) {
              FORBIDDEN_BLOCKS.clear();
              FORBIDDEN_BLOCKS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("forbidden_exceptions")) {
              FORBIDDEN_EXCEPTIONS.clear();
              FORBIDDEN_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("ground_materials")) {
              GROUND_MATERIALS.clear();
              GROUND_MATERIALS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("ground_blocks")) {
              GROUND_BLOCKS.clear();
              GROUND_BLOCKS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("ground_exceptions")) {
              GROUND_EXCEPTIONS.clear();
              GROUND_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("danger_materials")) {
              DANGER_MATERIALS.clear();
              DANGER_MATERIALS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("danger_blocks")) {
              DANGER_BLOCKS.clear();
              DANGER_BLOCKS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("danger_exceptions")) {
              DANGER_EXCEPTIONS.clear();
              DANGER_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("water_materials")) {
              WATER_MATERIALS.clear();
              WATER_MATERIALS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("water_blocks")) {
              WATER_BLOCKS.clear();
              WATER_BLOCKS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("water_exceptions")) {
              WATER_EXCEPTIONS.clear();
              WATER_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("path_replaceable_materials")) {
              PATH_REPLACEABLE_MATERIALS.clear();
              PATH_REPLACEABLE_MATERIALS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("path_replaceable_blocks")) {
              PATH_REPLACEABLE_BLOCKS.clear();
              PATH_REPLACEABLE_BLOCKS.addAll(Arrays.asList(value.split(",")));
            } else if (key.equals("path_replaceable_exceptions")) {
              PATH_REPLACEABLE_EXCEPTIONS.clear();
              PATH_REPLACEABLE_EXCEPTIONS.addAll(Arrays.asList(value.split(",")));
            } else {
              MillLog.error(null, "Unknown block type category on line: " + line);
            } 
          } 
        } 
        line = reader.readLine();
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException(e);
      return false;
    } 
    return true;
  }
}
