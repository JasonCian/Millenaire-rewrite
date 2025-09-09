package org.millenaire.common.utilities;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.item.InvItem;

public class WorldUtilities {
  public static boolean checkChunksGenerated(World world, int start_x, int start_z, int end_x, int end_z) {
    start_x >>= 4;
    start_z >>= 4;
    end_x >>= 4;
    end_z >>= 4;
    end_x++;
    end_z++;
    for (int k1 = start_x; k1 <= end_x; k1++) {
      for (int l1 = start_z; l1 <= end_z; l1++) {
        if (!world.isChunkGeneratedAt(k1, l1))
          return false; 
      } 
    } 
    return true;
  }
  
  public static int countBlocksAround(World world, int x, int y, int z, int rx, int ry, int rz) {
    int counter = 0;
    for (int i = x - rx; i <= x + rx; i++) {
      for (int j = y - ry; j <= y + ry; j++) {
        for (int k = z - rz; k <= z + rz; k++) {
          if (getBlock(world, i, j, k) != null && getBlockState(world, i, j, k).getMaterial().blocksMovement())
            counter++; 
        } 
      } 
    } 
    return counter;
  }
  
  public static Point findRandomStandingPosAround(World world, Point dest) {
    if (dest == null)
      return null; 
    for (int i = 0; i < 50; i++) {
      Point testdest = dest.getRelative((5 - MillCommonUtilities.randomInt(10)), (5 - MillCommonUtilities.randomInt(20)), (5 - MillCommonUtilities.randomInt(10)));
      if (BlockItemUtilities.isBlockWalkable(getBlock(world, testdest.getiX(), testdest.getiY() - 1, testdest.getiZ())) && 
        !BlockItemUtilities.isBlockSolid(getBlock(world, testdest.getiX(), testdest.getiY(), testdest.getiZ())) && 
        !BlockItemUtilities.isBlockSolid(getBlock(world, testdest.getiX(), testdest.getiY() + 1, testdest.getiZ())))
        return testdest; 
    } 
    return null;
  }
  
  public static int findSurfaceBlock(World world, int x, int z) {
    BlockPos pos = new BlockPos(x, world.getHeight(), z);
    while (pos.getY() > -1 && !BlockItemUtilities.isBlockGround(getBlock(world, x, pos.getY(), z)) && !(getBlock(world, x, pos.getY(), z) instanceof net.minecraft.block.BlockLiquid))
      pos = new BlockPos(x, pos.getY() - 1, z); 
    if (pos.getY() > 254)
      pos = new BlockPos(x, 254, z); 
    return pos.getY() + 1;
  }
  
  public static Point findTopNonPassableBlock(World world, int x, int z) {
    for (int y = 255; y > 0; y--) {
      if (getBlock(world, x, y, z).getDefaultState().getMaterial().isSolid())
        return new Point(x, y, z); 
    } 
    return null;
  }
  
  public static int findTopSoilBlock(World world, int x, int z) {
    BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
    while (pos.getY() > -1 && !BlockItemUtilities.isBlockGround(getBlock(world, x, pos.getY(), z)))
      pos = new BlockPos(x, pos.getY() - 1, z); 
    if (pos.getY() > 254)
      pos = new BlockPos(x, 254, z); 
    return pos.getY() + 1;
  }
  
  public static Point findVerticalStandingPos(World world, Point dest) {
    if (dest == null)
      return null; 
    int y = dest.getiY();
    while (y < 250 && (BlockItemUtilities.isBlockSolid(getBlock(world, dest.getiX(), y, dest.getiZ())) || BlockItemUtilities.isBlockSolid(getBlock(world, dest.getiX(), y + 1, dest.getiZ()))))
      y++; 
    while (y > 0 && !BlockItemUtilities.isBlockSolid(getBlock(world, dest.getiX(), y - 1, dest.getiZ())))
      y--; 
    if (y == 250)
      return null; 
    if (!BlockItemUtilities.isBlockWalkable(getBlock(world, dest.getiX(), y - 1, dest.getiZ())))
      return null; 
    return new Point(dest.getiX(), y, dest.getiZ());
  }
  
  public static Block getBlock(World world, int x, int y, int z) {
    return world.getBlockState(new BlockPos(x, y, z)).getBlock();
  }
  
  public static Block getBlock(World world, Point p) {
    if (p.x < -3.2E7D || p.z < -3.2E7D || p.x >= 3.2E7D || p.z > 3.2E7D)
      return null; 
    if (p.y < 0.0D)
      return null; 
    if (p.y >= 256.0D)
      return null; 
    return getBlock(world, p.getiX(), p.getiY(), p.getiZ());
  }
  
  public static int getBlockId(Block b) {
    return Block.getIdFromBlock(b);
  }
  
  public static int getBlockMeta(World world, int i, int j, int k) {
    return getBlockMeta(world, new Point(i, j, k));
  }
  
  public static int getBlockMeta(World world, Point p) {
    if (p.x < -3.2E7D || p.z < -3.2E7D || p.x >= 3.2E7D || p.z > 3.2E7D)
      return -1; 
    if (p.y < 0.0D)
      return -1; 
    if (p.y >= 256.0D)
      return -1; 
    IBlockState state = p.getBlockActualState(world);
    return state.getBlock().getMetaFromState(state);
  }
  
  public static int getBlockMetadata(World world, int x, int y, int z) {
    BlockPos bp = new BlockPos(x, y, z);
    IBlockState bs = world.getBlockState(bp);
    return bs.getBlock().getMetaFromState(bs);
  }
  
  public static IBlockState getBlockState(World world, int x, int y, int z) {
    return world.getBlockState(new BlockPos(x, y, z));
  }
  
  public static IBlockState getBlockState(World world, Point p) {
    return world.getBlockState(new BlockPos(p.x, p.y, p.z));
  }
  
  public static IBlockState getBlockStateValidGround(IBlockState currentBlockState, boolean surface) {
    Block b = currentBlockState.getBlock();
    if (b == Blocks.BEDROCK)
      return Blocks.DIRT.getDefaultState(); 
    if (b == Blocks.STONE && surface)
      return Blocks.DIRT.getDefaultState(); 
    if (b == Blocks.STONE && !surface)
      return currentBlockState; 
    if (b == Blocks.DIRT)
      return currentBlockState; 
    if (b == Blocks.GRASS)
      return Blocks.DIRT.getDefaultState(); 
    if (b == Blocks.GRAVEL)
      return currentBlockState; 
    if (b == Blocks.SAND)
      return currentBlockState; 
    if (b == Blocks.SANDSTONE && surface)
      return Blocks.SAND.getDefaultState(); 
    if (b == Blocks.SANDSTONE && !surface)
      return currentBlockState; 
    if (b == Blocks.HARDENED_CLAY)
      return currentBlockState; 
    return null;
  }
  
  public static Point getClosestBlock(World world, Block[] blocks, Point pos, int rx, int ry, int rz) {
    return getClosestBlockMeta(world, blocks, -1, pos, rx, ry, rz);
  }
  
  public static Point getClosestBlockMeta(World world, Block[] blocks, int meta, Point pos, int rx, int ry, int rz) {
    Point closest = null;
    double minDistance = 9.99999999E8D;
    for (int i = pos.getiX() - rx; i <= pos.getiX() + rx; i++) {
      for (int j = pos.getiY() - ry; j <= pos.getiY() + ry; j++) {
        for (int k = pos.getiZ() - rz; k <= pos.getiZ() + rz; k++) {
          for (int l = 0; l < blocks.length; l++) {
            if (getBlock(world, i, j, k) == blocks[l] && (
              meta == -1 || getBlockMeta(world, i, j, k) == meta)) {
              Point temp = new Point(i, j, k);
              if (closest == null || temp.distanceTo(pos) < minDistance) {
                closest = temp;
                minDistance = closest.distanceTo(pos);
              } 
            } 
          } 
        } 
      } 
    } 
    if (minDistance < 9.99999999E8D)
      return closest; 
    return null;
  }
  
  public static EntityItem getClosestItemVertical(World world, Point p, List<InvItem> goods, int radius, int vertical) {
    List<Entity> list = getEntitiesWithinAABB(world, Entity.class, p, radius, vertical);
    double bestdist = Double.MAX_VALUE;
    EntityItem citem = null;
    for (Entity ent : list) {
      if (ent.getClass() == EntityItem.class) {
        EntityItem item = (EntityItem)ent;
        if (!item.isDead)
          for (InvItem key : goods) {
            if (item.getItem().getItem() == key.getItem() && item.getItem().getItemDamage() == key.meta) {
              double dist = item.getDistanceSq(p.x, p.y, p.z);
              if (dist < bestdist) {
                bestdist = dist;
                citem = item;
              } 
            } 
          }  
      } 
    } 
    if (citem == null)
      return null; 
    return citem;
  }
  
  public static List<Entity> getEntitiesWithinAABB(World world, Class type, Point p, int hradius, int vradius) {
    AxisAlignedBB area = (new AxisAlignedBB(p.x, p.y, p.z, p.x + 1.0D, p.y + 1.0D, p.z + 1.0D)).expand(hradius, vradius, hradius).expand(-hradius, -vradius, -hradius);
    return world.getEntitiesWithinAABB(type, area);
  }
  
  public static List<Entity> getEntitiesWithinAABB(World world, Class type, Point pstart, Point pend) {
    AxisAlignedBB area = new AxisAlignedBB(pstart.x, pstart.y, pstart.z, pend.x, pend.y, pend.z);
    return world.getEntitiesWithinAABB(type, area);
  }
  
  public static Entity getEntityByUUID(World world, UUID uuid) {
    for (Entity entity : world.getLoadedEntityList()) {
      if (entity.getUniqueID().equals(uuid))
        return entity; 
    } 
    return null;
  }
  
  public static int getItemsFromChest(IInventory chest, Block block, int meta, int toTake) {
    return getItemsFromChest(chest, Item.getItemFromBlock(block), meta, toTake);
  }
  
  public static int getItemsFromChest(IInventory chest, IBlockState blockState, int toTake) {
    return getItemsFromChest(chest, blockState.getBlock(), blockState.getBlock().getMetaFromState(blockState), toTake);
  }
  
  public static int getItemsFromChest(IInventory chest, Item item, int meta, int toTake) {
    if (chest == null)
      return 0; 
    int nb = 0;
    int maxSlot = chest.getSizeInventory() - 1;
    if (chest instanceof net.minecraft.entity.player.InventoryPlayer)
      maxSlot -= 4; 
    for (int i = maxSlot; i >= 0 && nb < toTake; i--) {
      ItemStack stack = chest.getStackInSlot(i);
      if (stack != null && stack.getItem() == item && (stack.getItemDamage() == meta || meta == -1))
        if (stack.getCount() <= toTake - nb) {
          nb += stack.getCount();
          chest.setInventorySlotContents(i, ItemStack.EMPTY);
        } else {
          chest.decrStackSize(i, toTake - nb);
          nb = toTake;
        }  
      if (item == Item.getItemFromBlock(Blocks.LOG) && meta == -1 && 
        stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.LOG2))
        if (stack.getCount() <= toTake - nb) {
          nb += stack.getCount();
          chest.setInventorySlotContents(i, ItemStack.EMPTY);
        } else {
          chest.decrStackSize(i, toTake - nb);
          nb = toTake;
        }  
    } 
    return nb;
  }
  
  public static int getItemsFromFirePit(TileEntityFirePit firepit, Item item, int toTake) {
    if (firepit == null)
      return 0; 
    int taken = 0;
    for (int stackNb = 0; stackNb < 3; stackNb++) {
      ItemStack stack = firepit.outputs.getStackInSlot(stackNb);
      if (taken < toTake && stack != null && stack.getItem() == item)
        taken += firepit.outputs.extractItem(stackNb, toTake, false).getCount(); 
    } 
    return taken;
  }
  
  public static int getItemsFromFurnace(TileEntityFurnace furnace, Item item, int toTake) {
    if (furnace == null)
      return 0; 
    int nb = 0;
    ItemStack stack = furnace.getStackInSlot(2);
    if (stack != null && stack.getItem() == item)
      if (stack.getCount() <= toTake - nb) {
        nb += stack.getCount();
        furnace.setInventorySlotContents(2, ItemStack.EMPTY);
      } else {
        furnace.decrStackSize(2, toTake - nb);
        nb = toTake;
      }  
    return nb;
  }
  
  public static EnumFacing guessPanelFacing(World world, Point p) {
    boolean northOpen = true, southOpen = true, eastOpen = true, westOpen = true;
    if (getBlockState(world, p.getNorth()).isFullBlock())
      northOpen = false; 
    if (getBlockState(world, p.getEast()).isFullBlock())
      eastOpen = false; 
    if (getBlockState(world, p.getSouth()).isFullBlock())
      southOpen = false; 
    if (getBlockState(world, p.getWest()).isFullBlock())
      westOpen = false; 
    if (!eastOpen)
      return EnumFacing.WEST; 
    if (!westOpen)
      return EnumFacing.EAST; 
    if (!southOpen)
      return EnumFacing.NORTH; 
    if (!northOpen)
      return EnumFacing.SOUTH; 
    return null;
  }
  
  public static boolean isBlockFullCube(World world, int i, int j, int k) {
    IBlockState bs = getBlockState(world, i, j, k);
    if (bs == null)
      return false; 
    return bs.isFullCube();
  }
  
  public static void playSound(World world, Point p, SoundEvent sound, SoundCategory category, float volume, float pitch) {
    if (world.isRemote) {
      world.playSound(((float)p.x + 0.5F), ((float)p.y + 0.5F), ((float)p.z + 0.5F), sound, category, volume, pitch, false);
    } else {
      world.playSound(null, ((float)p.x + 0.5F), ((float)p.y + 0.5F), ((float)p.z + 0.5F), sound, category, volume, pitch);
    } 
  }
  
  public static void playSoundBlockBreaking(World world, Point p, Block b, float volume) {
    if (b != null && b.getSoundType() != null)
      playSound(world, p, b.getSoundType().getBreakSound(), SoundCategory.BLOCKS, b.getSoundType().getVolume() * volume, b.getSoundType().getPitch()); 
  }
  
  public static void playSoundBlockPlaced(World world, Point p, Block b, float volume) {
    if (b != null && b.getSoundType() != null)
      playSound(world, p, b.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, b.getSoundType().getVolume() * volume, b.getSoundType().getPitch()); 
  }
  
  public static void playSoundByMillName(World world, Point p, String soundMill, float volume) {
    if (soundMill.equals("metal")) {
      playSoundBlockPlaced(world, p, Blocks.IRON_BLOCK, volume);
    } else if (soundMill.equals("wood")) {
      playSoundBlockPlaced(world, p, Blocks.LOG, volume);
    } else if (soundMill.equals("wool")) {
      playSoundBlockPlaced(world, p, Blocks.WOOL, volume);
    } else if (soundMill.equals("glass")) {
      playSoundBlockPlaced(world, p, Blocks.GLASS, volume);
    } else if (soundMill.equals("stone")) {
      playSoundBlockPlaced(world, p, Blocks.STONE, volume);
    } else if (soundMill.equals("earth")) {
      playSoundBlockPlaced(world, p, Blocks.DIRT, volume);
    } else if (soundMill.equals("sand")) {
      playSoundBlockPlaced(world, p, (Block)Blocks.SAND, volume);
    } else {
      MillLog.printException("Tried to play unknown sound: " + soundMill, new Exception());
    } 
  }
  
  public static boolean setBlock(World world, Point p, Block block) {
    return setBlock(world, p, block, true, false);
  }
  
  public static boolean setBlock(World world, Point p, Block block, boolean notify, boolean playSound) {
    if (p.x < -3.2E7D || p.z < -3.2E7D || p.x >= 3.2E7D || p.z > 3.2E7D)
      return false; 
    if (p.y < 0.0D)
      return false; 
    if (p.y >= 256.0D)
      return false; 
    if (playSound && block == Blocks.AIR) {
      Block oldBlock = getBlock(world, p.getiX(), p.getiY(), p.getiZ());
      if (oldBlock != null && 
        oldBlock.getSoundType() != null)
        if (oldBlock.getSoundType() == SoundType.GROUND) {
          playSoundBlockBreaking(world, p, oldBlock, 0.5F);
        } else {
          playSoundBlockBreaking(world, p, oldBlock, 1.0F);
        }  
    } 
    if (notify) {
      world.setBlockState(p.getBlockPos(), block.getDefaultState());
    } else {
      world.setBlockState(p.getBlockPos(), block.getDefaultState(), 2);
    } 
    if (playSound && block != Blocks.AIR && 
      block.getSoundType() != null)
      if (block.getSoundType() == SoundType.GROUND) {
        playSoundBlockBreaking(world, p, block, 0.5F);
      } else {
        playSoundBlockBreaking(world, p, block, 1.0F);
      }  
    return true;
  }
  
  public static boolean setBlockAndMetadata(World world, int x, int y, int z, Block block, int metadata, boolean notify, boolean playSound) {
    if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000)
      return false; 
    if (y < 0)
      return false; 
    if (y >= 256)
      return false; 
    if (playSound && block != Blocks.AIR) {
      Block oldBlock = getBlock(world, x, y, z);
      if (oldBlock != null && 
        oldBlock.getSoundType() != null)
        playSoundBlockBreaking(world, new Point(x, y, z), oldBlock, 1.0F); 
    } 
    if (block == null) {
      MillLog.printException("Trying to set null block", new Exception());
      return false;
    } 
    IBlockState state = block.getStateFromMeta(metadata);
    if (notify) {
      world.setBlockState(new BlockPos(x, y, z), state);
    } else {
      world.setBlockState(new BlockPos(x, y, z), state, 2);
    } 
    if (playSound && block != Blocks.AIR && 
      block.getSoundType() != null)
      playSoundBlockPlaced(world, new Point(x, y, z), block, 2.0F); 
    return true;
  }
  
  public static boolean setBlockAndMetadata(World world, Point p, Block block, int metadata) {
    return setBlockAndMetadata(world, p, block, metadata, true, false);
  }
  
  public static boolean setBlockAndMetadata(World world, Point p, Block block, int metadata, boolean notify, boolean playSound) {
    return setBlockAndMetadata(world, p.getiX(), p.getiY(), p.getiZ(), block, metadata, notify, playSound);
  }
  
  public static boolean setBlockMetadata(World world, int x, int y, int z, int metadata, boolean notify) {
    if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000)
      return false; 
    if (y < 0)
      return false; 
    if (y >= 256)
      return false; 
    Point p = new Point(x, y, z);
    IBlockState state = p.getBlockActualState(world);
    state = state.getBlock().getStateFromMeta(metadata);
    if (notify) {
      world.setBlockState(p.getBlockPos(), state);
    } else {
      world.setBlockState(p.getBlockPos(), state, 2);
    } 
    return true;
  }
  
  public static boolean setBlockMetadata(World world, Point p, int metadata) {
    return setBlockMetadata(world, p, metadata, true);
  }
  
  public static boolean setBlockMetadata(World world, Point p, int metadata, boolean notify) {
    return setBlockMetadata(world, p.getiX(), p.getiY(), p.getiZ(), metadata, notify);
  }
  
  public static boolean setBlockstate(World world, Point p, IBlockState bs, boolean b, boolean c) {
    return setBlockAndMetadata(world, p, bs.getBlock(), bs.getBlock().getMetaFromState(bs), b, c);
  }
  
  public static void spawnExp(World world, Point p, int nb) {
    if (world.isRemote)
      return; 
    for (int j = nb; j > 0; ) {
      int l = EntityXPOrb.getXPSplit(j);
      j -= l;
      world.spawnEntity((Entity)new EntityXPOrb(world, p.x + 0.5D, p.y + 5.0D, p.z + 0.5D, l));
    } 
  }
  
  public static EntityItem spawnItem(World world, Point p, ItemStack itemstack, float f) {
    if (world.isRemote)
      return null; 
    EntityItem entityitem = new EntityItem(world, p.x, p.y + f, p.z, itemstack);
    entityitem.setDefaultPickupDelay();
    world.spawnEntity((Entity)entityitem);
    return entityitem;
  }
  
  public static void spawnMobsAround(World world, Point p, int radius, ResourceLocation mobType, int minNb, int extraNb) {
    int nb = minNb;
    if (extraNb > 0)
      nb += MillCommonUtilities.randomInt(extraNb); 
    for (int i = 0; i < nb; i++) {
      EntityLiving entityliving = (EntityLiving)EntityList.createEntityByIDFromName(mobType, world);
      if (entityliving != null) {
        boolean spawned = false;
        for (int j = 0; j < 20 && !spawned; j++) {
          double ex = p.x + (world.rand.nextDouble() * 2.0D - 1.0D) * radius;
          double ey = p.y + world.rand.nextInt(3) - 1.0D;
          double ez = p.z + (world.rand.nextDouble() * 2.0D - 1.0D) * radius;
          Point ep = new Point(ex, ey, ez);
          if (ep.getBelow().getBlockActualState(world).isBlockNormalCube()) {
            entityliving.setLocationAndAngles(ex, ey, ez, world.rand.nextFloat() * 360.0F, 0.0F);
            if (entityliving.getCanSpawnHere()) {
              world.spawnEntity((Entity)entityliving);
              MillLog.major(null, "Entering world: " + entityliving.getClass().getName());
              spawned = true;
            } 
          } 
        } 
        if (!spawned)
          MillLog.major(null, "No valid space found."); 
        entityliving.spawnExplosionParticle();
      } 
    } 
  }
  
  public static Entity spawnMobsSpawner(World world, Point p, ResourceLocation mobType) {
    EntityLiving entityliving = (EntityLiving)EntityList.createEntityByIDFromName(mobType, world);
    if (entityliving == null)
      return null; 
    int x = MillCommonUtilities.randomInt(2) - 1;
    int z = MillCommonUtilities.randomInt(2) - 1;
    int ex = (int)(p.x + x);
    int ey = (int)p.y;
    int ez = (int)(p.z + z);
    if (getBlock(world, ex, ey, ez) != Blocks.AIR && getBlock(world, ex, ey + 1, ez) != Blocks.AIR)
      return null; 
    entityliving.setLocationAndAngles(ex, ey, ez, world.rand.nextFloat() * 360.0F, 0.0F);
    world.spawnEntity((Entity)entityliving);
    entityliving.spawnExplosionParticle();
    return (Entity)entityliving;
  }
}
