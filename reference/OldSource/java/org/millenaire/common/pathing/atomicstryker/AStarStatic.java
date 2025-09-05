package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.ThreadSafeUtilities;

public class AStarStatic {
  static final int[][] candidates = new int[][] { 
      { 0, 0, -1, 1 }, { 0, 0, 1, 1 }, { 0, 1, 0, 1 }, { 1, 0, 0, 1 }, { -1, 0, 0, 1 }, { 1, 1, 0, 2 }, { -1, 1, 0, 2 }, { 0, 1, 1, 2 }, { 0, 1, -1, 2 }, { 1, -1, 0, 1 }, 
      { -1, -1, 0, 1 }, { 0, -1, 1, 1 }, { 0, -1, -1, 1 } };
  
  static final int[][] candidates_allowdrops = new int[][] { 
      { 0, 0, -1, 1 }, { 0, 0, 1, 1 }, { 1, 0, 0, 1 }, { -1, 0, 0, 1 }, { 1, 1, 0, 2 }, { -1, 1, 0, 2 }, { 0, 1, 1, 2 }, { 0, 1, -1, 2 }, { 1, -1, 0, 1 }, { -1, -1, 0, 1 }, 
      { 0, -1, 1, 1 }, { 0, -1, -1, 1 }, { 1, -2, 0, 1 }, { -1, -2, 0, 1 }, { 0, -2, 1, 1 }, { 0, -2, -1, 1 } };
  
  public static AStarNode[] getAccessNodesSorted(World world, int workerX, int workerY, int workerZ, int posX, int posY, int posZ, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
    ArrayList<AStarNode> resultList = new ArrayList<>();
    for (int xIter = -2; xIter <= 2; xIter++) {
      for (int zIter = -2; zIter <= 2; zIter++) {
        for (int yIter = -3; yIter <= 2; yIter++) {
          AStarNode aStarNode = new AStarNode(posX + xIter, posY + yIter, posZ + zIter, Math.abs(xIter) + Math.abs(yIter), null);
          if (isViable(world, aStarNode, 1, config))
            resultList.add(aStarNode); 
        } 
      } 
    } 
    Collections.sort(resultList);
    int count = 0;
    AStarNode[] returnVal = new AStarNode[resultList.size()];
    AStarNode check;
    while (!resultList.isEmpty() && (check = resultList.get(0)) != null) {
      returnVal[count] = check;
      resultList.remove(0);
      count++;
    } 
    return returnVal;
  }
  
  public static double getDistanceBetweenCoords(int x, int y, int z, int posX, int posY, int posZ) {
    return Math.sqrt(Math.pow((x - posX), 2.0D) + Math.pow((y - posY), 2.0D) + Math.pow((z - posZ), 2.0D));
  }
  
  public static double getDistanceBetweenNodes(AStarNode a, AStarNode b) {
    return Math.sqrt(Math.pow((a.x - b.x), 2.0D) + Math.pow((a.y - b.y), 2.0D) + Math.pow((a.z - b.z), 2.0D));
  }
  
  public static double getEntityLandSpeed(EntityLiving entLiving) {
    return Math.sqrt(entLiving.motionX * entLiving.motionX + entLiving.motionZ * entLiving.motionZ);
  }
  
  public static int getIntCoordFromDoubleCoord(double input) {
    return MathHelper.floor(input);
  }
  
  public static boolean isLadder(World world, Block b, int x, int y, int z) {
    if (b != null)
      return b.isLadder(b.getDefaultState(), (IBlockAccess)world, new BlockPos(x, y, z), null); 
    return false;
  }
  
  public static boolean isPassableBlock(World world, int ix, int iy, int iz, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
    IBlockState blockState = ThreadSafeUtilities.getBlockState(world, ix, iy, iz);
    Block block = blockState.getBlock();
    if (iy > 0) {
      Block blockBelow = ThreadSafeUtilities.getBlock(world, ix, iy - 1, iz);
      if (BlockItemUtilities.isFence(blockBelow) || blockBelow == Blocks.IRON_BARS || blockBelow == Blocks.NETHER_BRICK_FENCE || blockBelow instanceof net.minecraft.block.BlockWall || blockBelow instanceof org.millenaire.common.block.BlockMillWall)
        return false; 
    } 
    if (block != null) {
      if (!config.canSwim && (block == Blocks.WATER || block == Blocks.FLOWING_WATER))
        return false; 
      if (config.canUseDoors && (
        BlockItemUtilities.isWoodenDoor(block) || BlockItemUtilities.isFenceGate(block)))
        return true; 
      if (config.canClearLeaves && 
        block instanceof BlockLeaves)
        if (block == Blocks.LEAVES || block == Blocks.LEAVES2) {
          if (((Boolean)blockState.getValue((IProperty)BlockLeaves.DECAYABLE)).booleanValue() == true)
            return true; 
        } else if (BlockStateUtilities.hasPropertyByName(blockState, "decayable")) {
          if (((Boolean)blockState.getValue((IProperty)BlockLeaves.DECAYABLE)).booleanValue() == true)
            return true; 
        } else {
          return true;
        }  
      return ThreadSafeUtilities.isBlockPassable(block, world, ix, iy, iz);
    } 
    return true;
  }
  
  public static boolean isViable(World world, AStarNode target, int yoffset, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
    return isViable(world, target.x, target.y, target.z, yoffset, config);
  }
  
  public static boolean isViable(World world, int x, int y, int z, int yoffset, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
    Block block = ThreadSafeUtilities.getBlock(world, x, y, z);
    Block blockBelow = ThreadSafeUtilities.getBlock(world, x, y - 1, z);
    if (block == Blocks.LADDER && isPassableBlock(world, x, y + 1, z, config))
      return true; 
    if (!isPassableBlock(world, x, y, z, config) || !isPassableBlock(world, x, y + 1, z, config))
      return false; 
    if (blockBelow == Blocks.WATER || blockBelow == Blocks.FLOWING_WATER)
      return false; 
    if (isPassableBlock(world, x, y - 1, z, config)) {
      if (!config.canSwim)
        return false; 
      if (block != Blocks.WATER && block != Blocks.FLOWING_WATER)
        return false; 
    } 
    if (yoffset < 0)
      yoffset *= -1; 
    int ycheckhigher = 1;
    while (ycheckhigher <= yoffset) {
      if (!isPassableBlock(world, x, y + yoffset, z, config))
        return false; 
      ycheckhigher++;
    } 
    return true;
  }
  
  public static AS_PathEntity translateAStarPathtoPathEntity(World world, List<AStarNode> input, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
    if (!config.canTakeDiagonals) {
      List<AStarNode> oldInput = input;
      input = new ArrayList<>();
      for (int j = 0; j < oldInput.size() - 1; j++) {
        input.add(oldInput.get(j));
        if (((AStarNode)oldInput.get(j)).x != ((AStarNode)oldInput.get(j + 1)).x && ((AStarNode)oldInput.get(j)).z != ((AStarNode)oldInput.get(j + 1)).z && 
          ((AStarNode)oldInput.get(j)).y == ((AStarNode)oldInput.get(j + 1)).y)
          if (!isPassableBlock(world, ((AStarNode)oldInput.get(j)).x, ((AStarNode)oldInput.get(j)).y - 1, ((AStarNode)oldInput.get(j + 1)).z, config) && 
            isPassableBlock(world, ((AStarNode)oldInput.get(j)).x, ((AStarNode)oldInput.get(j)).y, ((AStarNode)oldInput.get(j + 1)).z, config) && 
            isPassableBlock(world, ((AStarNode)oldInput.get(j)).x, ((AStarNode)oldInput.get(j)).y + 1, ((AStarNode)oldInput.get(j + 1)).z, config)) {
            AStarNode newNode = new AStarNode(((AStarNode)oldInput.get(j)).x, ((AStarNode)oldInput.get(j)).y, ((AStarNode)oldInput.get(j + 1)).z, 0, null);
            input.add(newNode);
          } else {
            AStarNode newNode = new AStarNode(((AStarNode)oldInput.get(j + 1)).x, ((AStarNode)oldInput.get(j)).y, ((AStarNode)oldInput.get(j)).z, 0, null);
            input.add(newNode);
          }  
      } 
    } 
    AS_PathPoint[] points = new AS_PathPoint[input.size()];
    int i = 0;
    int size = input.size();
    while (size > 0) {
      AStarNode reading = input.get(size - 1);
      points[i] = new AS_PathPoint(reading.x, reading.y, reading.z);
      points[i].setIndex(i);
      points[i].setTotalPathDistance(i);
      points[i].setDistanceToNext(1.0F);
      points[i].setDistanceToTarget(size);
      if (i > 0)
        points[i].setPrevious(points[i - 1]); 
      input.remove(size - 1);
      size--;
      i++;
    } 
    return new AS_PathEntity((PathPoint[])points);
  }
}
