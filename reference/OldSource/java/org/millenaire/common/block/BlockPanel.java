package org.millenaire.common.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class BlockPanel extends BlockContainer {
  public static final PropertyDirection FACING = BlockHorizontal.FACING;
  
  protected static final AxisAlignedBB SIGN_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
  
  protected static final AxisAlignedBB SIGN_WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
  
  protected static final AxisAlignedBB SIGN_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
  
  protected static final AxisAlignedBB SIGN_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
  
  public BlockPanel(String blockName) {
    super(Material.WOOD);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setHardness(1.0F);
  }
  
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    return (!hasInvalidNeighbor(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos));
  }
  
  public boolean canSpawnInBlock() {
    return true;
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)FACING });
  }
  
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return (TileEntity)new TileEntityPanel();
  }
  
  public TileEntity createTileEntity(World world, IBlockState state) {
    return (TileEntity)new TileEntityPanel();
  }
  
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return BlockFaceShape.UNDEFINED;
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    switch ((EnumFacing)state.getValue((IProperty)FACING)) {
      default:
        return SIGN_NORTH_AABB;
      case SOUTH:
        return SIGN_SOUTH_AABB;
      case WEST:
        return SIGN_WEST_AABB;
      case EAST:
        break;
    } 
    return SIGN_EAST_AABB;
  }
  
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    return NULL_AABB;
  }
  
  public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    return ItemStack.EMPTY;
  }
  
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return null;
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((EnumFacing)state.getValue((IProperty)FACING)).getIndex();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getFront(meta);
    if (enumfacing.getAxis() == EnumFacing.Axis.Y)
      enumfacing = EnumFacing.NORTH; 
    return getDefaultState().withProperty((IProperty)FACING, (Comparable)enumfacing);
  }
  
  @SideOnly(Side.CLIENT)
  public boolean hasCustomBreakingProgress(IBlockState state) {
    return true;
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
  
  public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
    return true;
  }
  
  public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
    EnumFacing enumfacing = (EnumFacing)state.getValue((IProperty)FACING);
    if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getMaterial().isSolid()) {
      dropBlockAsItem(worldIn, pos, state, 0);
      worldIn.setBlockToAir(pos);
    } 
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
  }
  
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (world.isRemote)
      return true; 
    TileEntityPanel panel = (TileEntityPanel)world.getTileEntity(pos);
    if (panel == null || panel.panelType == 0)
      return false; 
    Building building = Mill.getMillWorld(world).getBuilding(panel.buildingPos);
    if (building == null)
      return false; 
    if (panel.panelType == 4 && building.controlledBy(entityplayer)) {
      ServerSender.displayControlledProjectGUI(entityplayer, building);
      return true;
    } 
    if (panel.panelType == 13 && building.controlledBy(entityplayer)) {
      ServerSender.displayControlledMilitaryGUI(entityplayer, building);
      return true;
    } 
    ServerSender.displayPanel(entityplayer, new Point(pos));
    return true;
  }
  
  public int quantityDropped(Random random) {
    return 0;
  }
  
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue((IProperty)FACING)));
  }
  
  public IBlockState withRotation(IBlockState state, Rotation rot) {
    return state.withProperty((IProperty)FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)FACING)));
  }
}
