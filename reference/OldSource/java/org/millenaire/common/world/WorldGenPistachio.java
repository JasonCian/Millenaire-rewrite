package org.millenaire.common.world;

import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.utilities.MillCommonUtilities;

public class WorldGenPistachio extends WorldGenAbstractTree {
  private final int minTreeHeight;
  
  private final IBlockState metaWood;
  
  private final IBlockState metaLeaves;
  
  public WorldGenPistachio(boolean notify) {
    super(notify);
    this.minTreeHeight = 6;
    this.metaWood = Blocks.LOG.getDefaultState().withProperty((IProperty)BlockOldLog.VARIANT, (Comparable)BlockPlanks.EnumType.OAK);
    this.metaLeaves = MillBlocks.LEAVES_PISTACHIO.getDefaultState().withProperty((IProperty)BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
  }
  
  public boolean generate(World worldIn, Random rand, BlockPos position) {
    int treeHeight = rand.nextInt(3) + this.minTreeHeight;
    boolean obstacleMet = true;
    if (position.getY() >= 1 && position.getY() + treeHeight + 1 <= worldIn.getHeight()) {
      for (int j = position.getY(); j <= position.getY() + 1 + treeHeight; j++) {
        int k = 1;
        if (j == position.getY())
          k = 0; 
        if (j >= position.getY() + 1 + treeHeight - 2)
          k = 2; 
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int l = position.getX() - k; l <= position.getX() + k && obstacleMet; l++) {
          for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && obstacleMet; i1++) {
            if (j >= 0 && j < worldIn.getHeight()) {
              if (!isReplaceable(worldIn, (BlockPos)blockpos$mutableblockpos.setPos(l, j, i1)))
                obstacleMet = false; 
            } else {
              obstacleMet = false;
            } 
          } 
        } 
      } 
      if (!obstacleMet)
        return false; 
      IBlockState state = worldIn.getBlockState(position.down());
      if (state.getBlock().canSustainPlant(state, (IBlockAccess)worldIn, position.down(), EnumFacing.UP, (IPlantable)Blocks.SAPLING) && position
        .getY() < worldIn.getHeight() - treeHeight - 1) {
        state.getBlock().onPlantGrow(state, worldIn, position.down(), position);
        for (int yPos = position.getY() + 3; yPos <= position.getY() + treeHeight; yPos++) {
          int leavesRadius = 4;
          if (yPos < position.getY() + 5) {
            leavesRadius -= position.getY() + 5 - yPos;
          } else if (yPos > position.getY() + treeHeight - 3) {
            leavesRadius -= yPos - position.getY() + treeHeight - 3;
          } 
          for (int xPos = position.getX() - leavesRadius; xPos <= position.getX() + leavesRadius; xPos++) {
            int distanceFromTrunkX = xPos - position.getX();
            for (int zPos = position.getZ() - leavesRadius; zPos <= position.getZ() + leavesRadius; zPos++) {
              int distanceFromTrunkZ = zPos - position.getZ();
              int chanceOn100 = 100;
              if (Math.abs(distanceFromTrunkX) == leavesRadius && Math.abs(distanceFromTrunkZ) == leavesRadius) {
                chanceOn100 = 0;
              } else if (Math.abs(distanceFromTrunkX) == leavesRadius || Math.abs(distanceFromTrunkZ) == leavesRadius) {
                chanceOn100 = 80;
              } 
              if (MillCommonUtilities.randomInt(100) < chanceOn100) {
                BlockPos blockpos = new BlockPos(xPos, yPos, zPos);
                state = worldIn.getBlockState(blockpos);
                if (state.getBlock().isAir(state, (IBlockAccess)worldIn, blockpos) || state.getBlock().isLeaves(state, (IBlockAccess)worldIn, blockpos) || state.getMaterial() == Material.VINE)
                  setBlockAndNotifyAdequately(worldIn, blockpos, this.metaLeaves); 
              } 
            } 
          } 
        } 
        for (int j3 = 0; j3 < treeHeight; j3++) {
          BlockPos upN = position.up(j3);
          state = worldIn.getBlockState(upN);
          if (state.getBlock().isAir(state, (IBlockAccess)worldIn, upN) || state.getBlock().isLeaves(state, (IBlockAccess)worldIn, upN) || state.getMaterial() == Material.VINE)
            setBlockAndNotifyAdequately(worldIn, position.up(j3), this.metaWood); 
        } 
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL.facings()) {
          if (MillCommonUtilities.randomInt(100) < 70) {
            int branchMaxY = treeHeight - rand.nextInt(2);
            int branchMinY = 3 + rand.nextInt(2);
            int horizontalOffset = 3 - rand.nextInt(2);
            int xPos = position.getX();
            int zPos = position.getZ();
            for (int i = 0; i < branchMaxY; i++) {
              int i2 = position.getY() + i;
              if (i >= branchMinY && horizontalOffset > 0) {
                xPos += enumfacing.getFrontOffsetX();
                zPos += enumfacing.getFrontOffsetZ();
                horizontalOffset--;
              } 
              BlockPos blockpos = new BlockPos(xPos, i2, zPos);
              state = worldIn.getBlockState(blockpos);
              if (state.getBlock().isAir(state, (IBlockAccess)worldIn, blockpos) || state.getBlock().isLeaves(state, (IBlockAccess)worldIn, blockpos))
                setBlockAndNotifyAdequately(worldIn, blockpos, this.metaWood); 
            } 
          } 
        } 
        return true;
      } 
      return false;
    } 
    return false;
  }
}
