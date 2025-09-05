package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRosetteBars extends BlockBars {
  public static final PropertyDirection FACING = BlockHorizontal.FACING;
  
  static final PropertyEnum<BlockSlab.EnumBlockHalf> TOP_BOTTOM = PropertyEnum.create("topbottom", BlockSlab.EnumBlockHalf.class);
  
  public BlockRosetteBars(String blockName, Material material, SoundType soundType) {
    super(blockName);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)NORTH, Boolean.valueOf(false)).withProperty((IProperty)EAST, Boolean.valueOf(false)).withProperty((IProperty)SOUTH, Boolean.valueOf(false))
        .withProperty((IProperty)WEST, Boolean.valueOf(false)).withProperty((IProperty)FACING, (Comparable)EnumFacing.SOUTH).withProperty((IProperty)TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.TOP));
  }
  
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    if (!worldIn.isRemote)
      BlockBeacon.updateColorAsync(worldIn, pos); 
  }
  
  public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
    BlockPos other = pos.offset(dir);
    IBlockState state = world.getBlockState(other);
    return (state.getBlock().canBeConnectedTo(world, other, dir.getOpposite()) || attachesTo(world, state, other, dir.getOpposite()) || state.getBlock() instanceof BlockMillWall);
  }
  
  protected boolean canSilkHarvest() {
    return false;
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)NORTH, (IProperty)EAST, (IProperty)WEST, (IProperty)SOUTH, (IProperty)FACING, (IProperty)TOP_BOTTOM });
  }
  
  public int damageDropped(IBlockState state) {
    return getMetaFromState(getDefaultState());
  }
  
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.TRANSLUCENT;
  }
  
  public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return MapColor.GRAY;
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    if (state.getValue((IProperty)TOP_BOTTOM) == BlockSlab.EnumBlockHalf.BOTTOM)
      i |= 0x4; 
    i |= ((EnumFacing)state.getValue((IProperty)FACING)).getHorizontalIndex();
    return i;
  }
  
  public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    IBlockState iBlockStateAbove = world.getBlockState(pos.add(0, 1, 0));
    if (iBlockStateAbove.getBlock() == this && iBlockStateAbove.getValue((IProperty)TOP_BOTTOM) == BlockSlab.EnumBlockHalf.TOP)
      return getDefaultState().withProperty((IProperty)TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM).withProperty((IProperty)FACING, iBlockStateAbove.getValue((IProperty)FACING)); 
    IBlockState iBlockStateWest = world.getBlockState(pos.add(-1, 0, 0));
    if (iBlockStateWest.getBlock() == this && iBlockStateWest.getValue((IProperty)FACING) == EnumFacing.WEST)
      return getDefaultState().withProperty((IProperty)FACING, (Comparable)EnumFacing.EAST).withProperty((IProperty)TOP_BOTTOM, iBlockStateWest.getValue((IProperty)TOP_BOTTOM)); 
    IBlockState iBlockStateSouth = world.getBlockState(pos.add(0, 0, 1));
    if (iBlockStateSouth.getBlock() == this && iBlockStateSouth.getValue((IProperty)FACING) == EnumFacing.SOUTH)
      return getDefaultState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH).withProperty((IProperty)TOP_BOTTOM, iBlockStateSouth.getValue((IProperty)TOP_BOTTOM)); 
    IBlockState iBlockStateBelow = world.getBlockState(pos.add(0, -1, 0));
    if (iBlockStateBelow.getBlock() == this && iBlockStateBelow.getValue((IProperty)TOP_BOTTOM) == BlockSlab.EnumBlockHalf.BOTTOM)
      return getDefaultState().withProperty((IProperty)TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.TOP).withProperty((IProperty)FACING, iBlockStateBelow.getValue((IProperty)FACING)); 
    IBlockState iBlockStateEast = world.getBlockState(pos.add(1, 0, 0));
    if (iBlockStateEast.getBlock() == this && iBlockStateEast.getValue((IProperty)FACING) == EnumFacing.EAST)
      return getDefaultState().withProperty((IProperty)FACING, (Comparable)EnumFacing.WEST).withProperty((IProperty)TOP_BOTTOM, iBlockStateEast.getValue((IProperty)TOP_BOTTOM)); 
    IBlockState iBlockStateNorth = world.getBlockState(pos.add(0, 0, -1));
    if (iBlockStateNorth.getBlock() == this && iBlockStateNorth.getValue((IProperty)FACING) == EnumFacing.NORTH)
      return getDefaultState().withProperty((IProperty)FACING, (Comparable)EnumFacing.SOUTH).withProperty((IProperty)TOP_BOTTOM, iBlockStateNorth.getValue((IProperty)TOP_BOTTOM)); 
    IBlockState basicState = getDefaultState();
    if (!iBlockStateAbove.isFullBlock() && iBlockStateBelow.isFullBlock())
      basicState = basicState.withProperty((IProperty)TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM); 
    if (!iBlockStateWest.isFullBlock() && iBlockStateEast.isFullBlock()) {
      basicState = basicState.withProperty((IProperty)FACING, (Comparable)EnumFacing.EAST);
    } else if (!iBlockStateSouth.isFullBlock() && iBlockStateNorth.isFullBlock()) {
      basicState = basicState.withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH);
    } else if (iBlockStateSouth.isFullBlock() && !iBlockStateNorth.isFullBlock()) {
      basicState = basicState.withProperty((IProperty)FACING, (Comparable)EnumFacing.SOUTH);
    } else if (iBlockStateWest.isFullBlock() && !iBlockStateEast.isFullBlock()) {
      basicState = basicState.withProperty((IProperty)FACING, (Comparable)EnumFacing.WEST);
    } 
    return basicState;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    IBlockState iblockstate = getDefaultState();
    if ((meta & 0x4) == 4)
      iblockstate = iblockstate.withProperty((IProperty)TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM); 
    EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 0x3);
    if (enumfacing.getAxis() == EnumFacing.Axis.Y)
      enumfacing = EnumFacing.NORTH; 
    iblockstate = iblockstate.withProperty((IProperty)FACING, (Comparable)enumfacing);
    return iblockstate;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), "variant=inventory"));
  }
  
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    if (!worldIn.isRemote)
      BlockBeacon.updateColorAsync(worldIn, pos); 
  }
  
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    switch (mirrorIn) {
      case LEFT_RIGHT:
        return state.withProperty((IProperty)NORTH, state.getValue((IProperty)SOUTH)).withProperty((IProperty)SOUTH, state.getValue((IProperty)NORTH));
      case FRONT_BACK:
        return state.withProperty((IProperty)EAST, state.getValue((IProperty)WEST)).withProperty((IProperty)WEST, state.getValue((IProperty)EAST));
    } 
    return super.withMirror(state, mirrorIn);
  }
  
  public IBlockState withRotation(IBlockState state, Rotation rot) {
    switch (rot) {
      case CLOCKWISE_180:
        return state.withProperty((IProperty)NORTH, state.getValue((IProperty)SOUTH)).withProperty((IProperty)EAST, state.getValue((IProperty)WEST)).withProperty((IProperty)SOUTH, state.getValue((IProperty)NORTH)).withProperty((IProperty)WEST, state.getValue((IProperty)EAST));
      case COUNTERCLOCKWISE_90:
        return state.withProperty((IProperty)NORTH, state.getValue((IProperty)EAST)).withProperty((IProperty)EAST, state.getValue((IProperty)SOUTH)).withProperty((IProperty)SOUTH, state.getValue((IProperty)WEST)).withProperty((IProperty)WEST, state.getValue((IProperty)NORTH));
      case CLOCKWISE_90:
        return state.withProperty((IProperty)NORTH, state.getValue((IProperty)WEST)).withProperty((IProperty)EAST, state.getValue((IProperty)NORTH)).withProperty((IProperty)SOUTH, state.getValue((IProperty)EAST)).withProperty((IProperty)WEST, state.getValue((IProperty)SOUTH));
    } 
    return state;
  }
}
