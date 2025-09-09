package org.millenaire.common.utilities;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.millenaire.common.block.BlockPathSlab;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingBlock;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;

public class PathUtilities {
  private static final boolean PATH_RAISE = false;
  
  private static final boolean PATH_DROP = true;
  
  private static boolean attemptPathBuild(Building th, World world, List<BuildingBlock> pathPoints, Point p, Block pathBlock, int pathMeta) {
    IBlockState blockState = p.getBlockActualState(world);
    if (th.isPointProtectedFromPathBuilding(p))
      return false; 
    if (p.getRelative(0.0D, 2.0D, 0.0D).isBlockPassable(world) && p.getAbove().isBlockPassable(world) && canPathBeBuiltHere(blockState)) {
      pathPoints.add(new BuildingBlock(p, pathBlock, pathMeta));
      return true;
    } 
    return false;
  }
  
  public static List<BuildingBlock> buildPath(Building th, List<AStarNode> path, Block pathBlock, int pathMeta, int pathWidth) {
    List<BuildingBlock> pathPoints = new ArrayList<>();
    boolean lastNodeHalfSlab = false;
    boolean[] pathShouldBuild = new boolean[path.size()];
    int ip;
    for (ip = 0; ip < path.size(); ip++)
      pathShouldBuild[ip] = true; 
    for (ip = 0; ip < path.size(); ip++) {
      AStarNode node = path.get(ip);
      Point p = new Point(node);
      BuildingLocation l = th.getLocationAtCoordPlanar(p);
      if (l != null)
        if (ip == 0) {
          pathShouldBuild[ip] = true;
          clearPathForward(path, pathShouldBuild, th, l, ip);
        } else if (ip == path.size() - 1) {
          pathShouldBuild[ip] = true;
          clearPathBackward(path, pathShouldBuild, th, l, ip);
        } else {
          boolean stablePath = isPointOnStablePath(p, th.world);
          if (stablePath) {
            pathShouldBuild[ip] = true;
            clearPathBackward(path, pathShouldBuild, th, l, ip);
            clearPathForward(path, pathShouldBuild, th, l, ip);
          } 
        }  
    } 
    for (ip = 0; ip < path.size(); ip++) {
      if (pathShouldBuild[ip]) {
        Block blockPathSlab;
        AStarNode node = path.get(ip);
        AStarNode lastNode = null, nextNode = null;
        if (ip > 0)
          lastNode = path.get(ip - 1); 
        if (ip + 1 < path.size())
          nextNode = path.get(ip + 1); 
        boolean halfSlab = false;
        if (lastNode != null && nextNode != null) {
          Point point1 = new Point(node);
          Point nextp = new Point(nextNode);
          Point lastp = new Point(lastNode);
          if (!isStairsOrSlabOrChest(th.world, nextp.getBelow()) && !isStairsOrSlabOrChest(th.world, lastp.getBelow())) {
            if ((point1.x == lastp.x && point1.x == nextp.x) || point1.z != lastp.z || point1.z != nextp.z);
            if (lastNode.y == nextNode.y && node.y < lastNode.y && point1.getRelative(0.0D, (lastNode.y - node.y), 0.0D).isBlockPassable(th.world) && point1
              .getRelative(0.0D, (lastNode.y - node.y + 1), 0.0D).isBlockPassable(th.world)) {
              halfSlab = true;
            } else if (!lastNodeHalfSlab && node.y == lastNode.y && node.y > nextNode.y) {
              halfSlab = true;
            } else if (!lastNodeHalfSlab && node.y == nextNode.y && node.y > lastNode.y) {
              halfSlab = true;
            } 
          } else {
            Block block = point1.getBelow().getBlock(th.world);
            if (BlockItemUtilities.isPathSlab(block))
              halfSlab = true; 
          } 
        } 
        Point p = (new Point(node)).getBelow();
        Block nodePathBlock = pathBlock;
        if (BlockItemUtilities.isPath(nodePathBlock) && halfSlab) {
          blockPathSlab = ((IBlockPath)nodePathBlock).getSingleSlab();
        } else {
          blockPathSlab = nodePathBlock;
        }
        attemptPathBuild(th, th.world, pathPoints, p, blockPathSlab, pathMeta);
        if (lastNode != null) {
          int dx = p.getiX() - lastNode.x;
          int dz = p.getiZ() - lastNode.z;
          int nbPass = 1;
          if (dx != 0 && dz != 0)
            nbPass = 2; 
          for (int i = 0; i < nbPass; i++) {
            int direction = (i == 0) ? 1 : -1;
            Point secondPoint = null, secondPointAlternate = null, thirdPoint = null;
            if (pathWidth > 1) {
              if (dx == 0 && direction == 1) {
                secondPoint = p.getRelative(direction, 0.0D, 0.0D);
                secondPointAlternate = p.getRelative(-direction, 0.0D, 0.0D);
              } else if (dz == 0 && direction == 1) {
                secondPoint = p.getRelative(0.0D, 0.0D, direction);
                secondPointAlternate = p.getRelative(0.0D, 0.0D, -direction);
              } else {
                secondPoint = p.getRelative((dx * direction), 0.0D, 0.0D);
                thirdPoint = p.getRelative(0.0D, 0.0D, (dz * direction));
              } 
            } else if (dx != 0 && dz != 0) {
              secondPoint = p.getRelative((dx * direction), 0.0D, 0.0D);
              secondPointAlternate = p.getRelative(0.0D, 0.0D, (dz * direction));
            } 
            if (secondPoint != null) {
              boolean success = attemptPathBuild(th, th.world, pathPoints, secondPoint, blockPathSlab, pathMeta);
              if (!success && secondPointAlternate != null)
                attemptPathBuild(th, th.world, pathPoints, secondPointAlternate, blockPathSlab, pathMeta); 
            } 
            if (thirdPoint != null)
              attemptPathBuild(th, th.world, pathPoints, thirdPoint, blockPathSlab, pathMeta); 
          } 
        } 
        lastNodeHalfSlab = halfSlab;
      } else {
        lastNodeHalfSlab = false;
      } 
    } 
    return pathPoints;
  }
  
  public static boolean canPathBeBuiltHere(IBlockState blockState) {
    Block block = blockState.getBlock();
    if (BlockItemUtilities.isPath(block))
      return !((Boolean)blockState.getValue((IProperty)IBlockPath.STABLE)).booleanValue(); 
    if (BlockItemUtilities.isBlockPathReplaceable(block) || BlockItemUtilities.isBlockDecorativePlant(block))
      return true; 
    return false;
  }
  
  public static boolean canPathBeBuiltHereOld(IBlockState blockState) {
    Block block = blockState.getBlock();
    if (block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.SAND || block == Blocks.GRAVEL || block == Blocks.HARDENED_CLAY || 
      BlockItemUtilities.isBlockDecorativePlant(block))
      return true; 
    if (BlockItemUtilities.isPath(block) && !((Boolean)blockState.getValue((IProperty)IBlockPath.STABLE)).booleanValue())
      return true; 
    return false;
  }
  
  private static void clearPathBackward(List<AStarNode> path, boolean[] pathShouldBuild, Building th, BuildingLocation l, int index) {
    boolean exit = false;
    boolean leadsToBorder = false;
    int i;
    for (i = index - 1; i >= 0 && !exit; i--) {
      Point np = new Point(path.get(i));
      BuildingLocation l2 = th.getLocationAtCoordPlanar(np);
      if (l2 != l) {
        leadsToBorder = true;
        exit = true;
      } else if (isPointOnStablePath(np, th.world)) {
        exit = true;
      } 
    } 
    if (!leadsToBorder) {
      exit = false;
      for (i = index - 1; i >= 0 && !exit; i--) {
        Point np = new Point(path.get(i));
        BuildingLocation l2 = th.getLocationAtCoordPlanar(np);
        if (l2 != l) {
          exit = true;
        } else if (isPointOnStablePath(np, th.world)) {
          exit = true;
        } else {
          pathShouldBuild[i] = false;
        } 
      } 
    } 
  }
  
  public static void clearPathBlock(Point p, World world) {
    IBlockState bs = p.getBlockActualState(world);
    if (bs.getBlock() instanceof IBlockPath && 
      !((Boolean)bs.getValue((IProperty)IBlockPath.STABLE)).booleanValue()) {
      IBlockState blockStateBelow = p.getBelow().getBlockActualState(world);
      if (WorldUtilities.getBlockStateValidGround(blockStateBelow, true) != null) {
        p.setBlockState(world, WorldUtilities.getBlockStateValidGround(blockStateBelow, true));
      } else {
        p.setBlock(world, Blocks.DIRT, 0, true, false);
      } 
    } 
  }
  
  private static void clearPathForward(List<AStarNode> path, boolean[] pathShouldBuild, Building th, BuildingLocation l, int index) {
    boolean exit = false;
    boolean leadsToBorder = false;
    int i;
    for (i = index + 1; i < path.size() && !exit; i++) {
      Point np = new Point(path.get(i));
      BuildingLocation l2 = th.getLocationAtCoordPlanar(np);
      if (l2 != l) {
        leadsToBorder = true;
        exit = true;
      } else if (isPointOnStablePath(np, th.world)) {
        exit = true;
      } 
    } 
    if (!leadsToBorder) {
      exit = false;
      for (i = index + 1; i < path.size() && !exit; i++) {
        Point np = new Point(path.get(i));
        BuildingLocation l2 = th.getLocationAtCoordPlanar(np);
        if (l2 != l) {
          exit = true;
        } else if (isPointOnStablePath(np, th.world)) {
          exit = true;
        } else {
          pathShouldBuild[i] = false;
        } 
      } 
    } 
  }
  
  public static boolean isPointOnStablePath(Point p, World world) {
    Block block = p.getBlock(world);
    if (block instanceof IBlockPath) {
      IBlockState bs = p.getBlockActualState(world);
      if (((Boolean)bs.getValue((IProperty)IBlockPath.STABLE)).booleanValue())
        return true; 
    } 
    block = p.getBelow().getBlock(world);
    if (block instanceof IBlockPath) {
      IBlockState bs = p.getBelow().getBlockActualState(world);
      if (((Boolean)bs.getValue((IProperty)IBlockPath.STABLE)).booleanValue())
        return true; 
    } 
    return false;
  }
  
  private static boolean isStairsOrSlabOrChest(World world, Point p) {
    Block block = p.getBlock(world);
    if (block == Blocks.CHEST || block == MillBlocks.LOCKED_CHEST || block == Blocks.CRAFTING_TABLE || block == Blocks.FURNACE || block == Blocks.LIT_FURNACE)
      return true; 
    if (block instanceof net.minecraft.block.BlockStairs)
      return true; 
    if (block instanceof net.minecraft.block.BlockSlab && 
      !block.getDefaultState().isOpaqueCube())
      return true; 
    return false;
  }
}
