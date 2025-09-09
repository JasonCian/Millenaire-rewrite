package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.IMetaBlockName;

public class BlockPathSlab extends Block implements IMetaBlockName, IBlockPath {
  protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D);
  
  protected static final AxisAlignedBB AABB_TOP_HALF = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 0.9375D, 1.0D);
  
  private final String singleSlabBlockName;
  
  private final String doubleSlabName;
  
  public BlockPathSlab(String blockName, MapColor color, SoundType soundType) {
    super(Material.GROUND, color);
    setSoundType(soundType);
    this.singleSlabBlockName = blockName + "_slab";
    this.doubleSlabName = blockName;
    setUnlocalizedName("millenaire." + this.doubleSlabName);
    setRegistryName(this.singleSlabBlockName);
    this.useNeighborBrightness = true;
    IBlockState iblockstate = this.blockState.getBaseState();
    iblockstate = iblockstate.withProperty((IProperty)IBlockPath.STABLE, Boolean.valueOf(false));
    iblockstate = iblockstate.withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
    setDefaultState(iblockstate);
    setHarvestLevel("shovel", 0);
    setHardness(0.8F);
    setCreativeTab(MillBlocks.tabMillenaire);
    setSoundType(SoundType.GROUND);
    this.fullBlock = false;
    setLightOpacity(255);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)BlockSlab.HALF, (IProperty)IBlockPath.STABLE });
  }
  
  public int damageDropped(IBlockState state) {
    return 0;
  }
  
  public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
    if (ForgeModContainer.disableStairSlabCulling)
      return super.doesSideBlockRendering(state, world, pos, face); 
    BlockSlab.EnumBlockHalf side = (BlockSlab.EnumBlockHalf)state.getValue((IProperty)BlockSlab.HALF);
    return ((side == BlockSlab.EnumBlockHalf.TOP && face == EnumFacing.UP) || (side == BlockSlab.EnumBlockHalf.BOTTOM && face == EnumFacing.DOWN));
  }
  
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return (face == EnumFacing.DOWN && state.getValue((IProperty)BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
  }
  
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return (state.getValue((IProperty)BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP) ? AABB_TOP_HALF : AABB_BOTTOM_HALF;
  }
  
  public BlockPath getDoubleSlab() {
    return (BlockPath)Block.getBlockFromName("millenaire:" + this.doubleSlabName);
  }
  
  public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    return new ItemStack(Block.getBlockFromName("millenaire:" + this.singleSlabBlockName), 1, 0);
  }
  
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(Block.getBlockFromName("millenaire:" + this.singleSlabBlockName));
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    if (((Boolean)state.getValue((IProperty)IBlockPath.STABLE)).booleanValue())
      i |= 0x1; 
    if (state.getValue((IProperty)BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP)
      i |= 0x8; 
    return i;
  }
  
  public BlockPathSlab getSingleSlab() {
    return (BlockPathSlab)Block.getBlockFromName("millenaire:" + this.singleSlabBlockName);
  }
  
  public String getSpecialName(ItemStack stack) {
    return getUnlocalizedName();
  }
  
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM).withProperty((IProperty)IBlockPath.STABLE, Boolean.valueOf(true));
    return (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || hitY <= 0.5D)) ? iblockstate : iblockstate.withProperty((IProperty)BlockSlab.HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP);
  }
  
  public IBlockState getStateFromMeta(int meta) {
    IBlockState iblockstate = getDefaultState();
    if ((meta & 0x1) == 1)
      iblockstate = iblockstate.withProperty((IProperty)IBlockPath.STABLE, Boolean.valueOf(true)); 
    iblockstate = iblockstate.withProperty((IProperty)BlockSlab.HALF, ((meta & 0x8) == 0) ? (Comparable)BlockSlab.EnumBlockHalf.BOTTOM : (Comparable)BlockSlab.EnumBlockHalf.TOP);
    return iblockstate;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "half=bottom,stable=false"));
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  public boolean isFullPath() {
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
  
  public boolean isTopSolid(IBlockState state) {
    return (state.getValue((IProperty)BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP);
  }
  
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    IBlockState iblockstate;
    Block block;
    switch (side) {
      case UP:
        return true;
      case NORTH:
      case SOUTH:
      case WEST:
      case EAST:
        iblockstate = blockAccess.getBlockState(pos.offset(side));
        block = iblockstate.getBlock();
        return (!iblockstate.isOpaqueCube() && block != Blocks.FARMLAND && block != Blocks.GRASS_PATH);
    } 
    return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
  }
}
