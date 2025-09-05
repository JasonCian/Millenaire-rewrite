package org.millenaire.common.world;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
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

public class WorldGenOliveTree extends WorldGenAbstractTree {
  private static final int MIN_TREE_HEIGHT = 5;
  
  private static final IBlockState WOOD_BS = Blocks.LOG2.getDefaultState().withProperty((IProperty)BlockNewLog.VARIANT, (Comparable)BlockPlanks.EnumType.ACACIA);
  
  private static final IBlockState LEAVES_BS = MillBlocks.LEAVES_OLIVETREE.getDefaultState().withProperty((IProperty)BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
  
  public WorldGenOliveTree(boolean notify) {
    super(notify);
  }
  
  public boolean generate(World worldIn, Random rand, BlockPos position) {
    int treeHeight = rand.nextInt(2) + 5;
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
        for (int yPos = 0; yPos < 5; yPos++) {
          BlockPos upN = position.up(yPos);
          state = worldIn.getBlockState(upN);
          if (state.getBlock().isAir(state, (IBlockAccess)worldIn, upN) || state.getBlock().isLeaves(state, (IBlockAccess)worldIn, upN) || state.getMaterial() == Material.VINE)
            setBlockAndNotifyAdequately(worldIn, position.up(yPos), WOOD_BS); 
        } 
        Set<EnumFacing> branchFacings = new HashSet<>();
        branchFacings.add(EnumFacing.Plane.HORIZONTAL.random(rand));
        for (int i = 0; i < 3; i++)
          branchFacings.add(EnumFacing.Plane.HORIZONTAL.random(rand)); 
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
          int curve, branchStartY = 3 + rand.nextInt(1);
          int horizontalSize = 3 - rand.nextInt(2);
          int xPos = position.getX();
          int zPos = position.getZ();
          int k = position.getY() + branchStartY;
          if (Math.random() < 0.5D) {
            curve = 1;
          } else {
            curve = -1;
          } 
          for (int hPos = 0; hPos < horizontalSize; hPos++) {
            if (k < position.getY() + treeHeight && Math.random() < 0.7D)
              k++; 
            if (enumfacing.getFrontOffsetX() != 0) {
              xPos += enumfacing.getFrontOffsetX();
              if (Math.random() < 0.15D)
                zPos += curve; 
            } else {
              zPos += enumfacing.getFrontOffsetZ();
              if (Math.random() < 0.15D)
                xPos += curve; 
            } 
            BlockPos blockpos = new BlockPos(xPos, k, zPos);
            state = worldIn.getBlockState(blockpos);
            if (state.getBlock().isAir(state, (IBlockAccess)worldIn, blockpos) || state.getBlock().isLeaves(state, (IBlockAccess)worldIn, blockpos)) {
              setBlockAndNotifyAdequately(worldIn, blockpos, WOOD_BS.withProperty((IProperty)BlockLog.LOG_AXIS, (Comparable)BlockLog.EnumAxis.fromFacingAxis(enumfacing.getAxis())));
              for (int dx = -1; dx < 2; dx++) {
                for (int dz = -1; dz < 2; dz++) {
                  for (int dy = -1; dy < 2; dy++) {
                    BlockPos leavePos = blockpos.add(dx, dy, dz);
                    state = worldIn.getBlockState(leavePos);
                    if (state.getBlock().isAir(state, (IBlockAccess)worldIn, leavePos) && MillCommonUtilities.randomInt(100) < 50)
                      setBlockAndNotifyAdequately(worldIn, leavePos, LEAVES_BS); 
                  } 
                } 
              } 
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
