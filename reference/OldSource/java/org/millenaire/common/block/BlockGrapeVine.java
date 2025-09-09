package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlockGrapeVine extends BlockMillCrops {
  public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockDoublePlant.EnumBlockHalf.class);
  
  public BlockGrapeVine(String cropName, boolean requireIrrigation, boolean slowGrowth, ResourceLocation seed) {
    super(cropName, requireIrrigation, slowGrowth, seed);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.LOWER));
  }
  
  public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
    if (state.getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
      return (worldIn.getBlockState(pos.down()).getBlock() == this && worldIn
        .getBlockState(pos.down()).getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.LOWER && super
        .canBlockStay(worldIn, pos.down(), worldIn.getBlockState(pos.down()))); 
    return (worldIn.getBlockState(pos.up()).getBlock() == this && worldIn
      .getBlockState(pos.up()).getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.UPPER && super
      .canBlockStay(worldIn, pos, state));
  }
  
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    return (super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up()));
  }
  
  protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
    if (!canBlockStay(worldIn, pos, state)) {
      boolean upper = (state.getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.UPPER);
      BlockPos upperPos = upper ? pos : pos.up();
      BlockPos lowerPos = upper ? pos.down() : pos;
      Block upperBlock = upper ? (Block)this : worldIn.getBlockState(upperPos).getBlock();
      Block lowerBlock = upper ? worldIn.getBlockState(lowerPos).getBlock() : (Block)this;
      if (upperBlock == this)
        worldIn.setBlockState(upperPos, Blocks.AIR.getDefaultState(), 2); 
      if (lowerBlock == this)
        worldIn.setBlockState(lowerPos, Blocks.AIR.getDefaultState(), 3); 
    } 
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)AGE, (IProperty)HALF });
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return FULL_BLOCK_AABB;
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = getAge(state);
    i |= (state.getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) ? 8 : 0;
    return i;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return withAge(meta & 0x7).withProperty((IProperty)HALF, ((meta & 0x8) > 0) ? (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER : (Comparable)BlockDoublePlant.EnumBlockHalf.LOWER);
  }
  
  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    int i = getAge(state) + getBonemealAgeIncrease(worldIn);
    int j = getMaxAge();
    if (i > j)
      i = j; 
    if (state.getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
      worldIn.setBlockState(pos.down(), withAge(i), 2);
      worldIn.setBlockState(pos, withAge(i).withProperty((IProperty)HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    } else {
      worldIn.setBlockState(pos, withAge(i), 2);
      worldIn.setBlockState(pos.up(), withAge(i).withProperty((IProperty)HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    } 
  }
  
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    worldIn.setBlockState(pos.up(), getDefaultState().withProperty((IProperty)HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    checkAndDropBlock(worldIn, pos, state);
    if (state.getValue((IProperty)HALF) == BlockDoublePlant.EnumBlockHalf.UPPER)
      return; 
    if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
      int i = getAge(state);
      if (i < getMaxAge()) {
        float growthChance = getGrowthChance(this, worldIn, pos);
        if (growthChance > 0.0F && ForgeHooks.onCropsGrowPre(worldIn, pos, state, 
            (rand.nextInt((int)(25.0F / growthChance)) == 0))) {
          worldIn.setBlockState(pos, withAge(i + 1), 2);
          worldIn.setBlockState(pos.up(), withAge(i + 1).withProperty((IProperty)HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
          ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn
              .getBlockState(pos));
        } 
      } 
    } 
  }
}
