package org.millenaire.common.utilities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ThreadSafeUtilities {
  public static class ChunkAccessException extends Exception {
    private static final long serialVersionUID = -7650231135028039490L;
    
    public final int x;
    
    public final int z;
    
    public ChunkAccessException(String message, int x, int z) {
      super(message);
      this.x = x;
      this.z = z;
    }
  }
  
  public static Block getBlock(World world, int x, int y, int z) throws ChunkAccessException {
    validateCoords(world, x, z);
    return world.getBlockState(new BlockPos(x, y, z)).getBlock();
  }
  
  public static IBlockState getBlockState(World world, int x, int y, int z) throws ChunkAccessException {
    validateCoords(world, x, z);
    return world.getBlockState(new BlockPos(x, y, z));
  }
  
  public static boolean isBlockPassable(Block block, World world, int x, int y, int z) throws ChunkAccessException {
    validateCoords(world, x, z);
    return block.isPassable((IBlockAccess)world, new BlockPos(x, y, z));
  }
  
  public static boolean isChunkAtGenerated(World world, int x, int z) {
    return world.isChunkGeneratedAt(x / 16, z / 16);
  }
  
  public static boolean isChunkAtLoaded(World world, int x, int z) {
    if (world instanceof WorldServer)
      return ((WorldServer)world).getChunkProvider().chunkExists(x >> 4, z >> 4); 
    if (world instanceof WorldClient)
      return ((WorldClient)world).getChunkProvider().isChunkGeneratedAt(x >> 4, z >> 4); 
    return false;
  }
  
  private static void validateCoords(World world, int x, int z) throws ChunkAccessException {
    if (world instanceof WorldServer) {
      if (!((WorldServer)world).getChunkProvider().chunkExists(x >> 4, z >> 4))
        throw new ChunkAccessException("Attempting to access a coordinate in an unloaded chunk within a thread in a server world at " + x + "/" + z + ".", x, z); 
    } else if (world instanceof WorldClient && 
      !((WorldClient)world).getChunkProvider().isChunkGeneratedAt(x >> 4, z >> 4)) {
      throw new ChunkAccessException("Attempting to access a coordinate in an unloaded chunk within a thread in a client world at " + x + "/" + z + ".", x, z);
    } 
  }
}
