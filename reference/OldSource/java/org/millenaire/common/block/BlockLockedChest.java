package org.millenaire.common.block;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.ui.ContainerLockedChest;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class BlockLockedChest extends BlockContainer {
  public static final PropertyDirection FACING = BlockHorizontal.FACING;
  
  protected static final AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0D, 0.9375D, 0.875D, 0.9375D);
  
  protected static final AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 1.0D);
  
  protected static final AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
  
  protected static final AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 1.0D, 0.875D, 0.9375D);
  
  protected static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
  
  public static ContainerLockedChest createContainer(World world, int i, int j, int k, EntityPlayer entityplayer) {
    TileEntityLockedChest lockedchest = (TileEntityLockedChest)world.getTileEntity(new BlockPos(i, j, k));
    IInventory chest = getInventory(lockedchest, world, i, j, k);
    Building building = Mill.getMillWorld(world).getBuilding(lockedchest.buildingPos);
    return new ContainerLockedChest((IInventory)entityplayer.inventory, chest, entityplayer, building, lockedchest.isLockedFor(entityplayer));
  }
  
  public static IInventory getInventory(TileEntityLockedChest lockedchest, World world, int i, int j, int k) {
    InventoryLargeChest inventoryLargeChest = null;
    String largename = lockedchest.getInvLargeName();
    TileEntityLockedChest tileEntityLockedChest = lockedchest;
    Block block = world.getBlockState(new BlockPos(i, j, k)).getBlock();
    if (world.getBlockState(new BlockPos(i - 1, j, k)).getBlock() == block)
      inventoryLargeChest = new InventoryLargeChest(largename, (ILockableContainer)world.getTileEntity(new BlockPos(i - 1, j, k)), (ILockableContainer)tileEntityLockedChest); 
    if (world.getBlockState(new BlockPos(i + 1, j, k)).getBlock() == block)
      inventoryLargeChest = new InventoryLargeChest(largename, (ILockableContainer)inventoryLargeChest, (ILockableContainer)world.getTileEntity(new BlockPos(i + 1, j, k))); 
    if (world.getBlockState(new BlockPos(i, j, k - 1)).getBlock() == block)
      inventoryLargeChest = new InventoryLargeChest(largename, (ILockableContainer)world.getTileEntity(new BlockPos(i, j, k - 1)), (ILockableContainer)inventoryLargeChest); 
    if (world.getBlockState(new BlockPos(i, j, k + 1)).getBlock() == block)
      inventoryLargeChest = new InventoryLargeChest(largename, (ILockableContainer)inventoryLargeChest, (ILockableContainer)world.getTileEntity(new BlockPos(i, j, k + 1))); 
    return (IInventory)(inventoryLargeChest != null ? inventoryLargeChest : tileEntityLockedChest);
  }
  
  public BlockLockedChest(String blockName) {
    super(Material.WOOD);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setHarvestLevel("axe", 0);
    setHardness(50.0F);
    setResistance(2000.0F);
    setSoundType(SoundType.WOOD);
  }
  
  public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    int i = 0;
    BlockPos blockpos = pos.west();
    BlockPos blockpos1 = pos.east();
    BlockPos blockpos2 = pos.north();
    BlockPos blockpos3 = pos.south();
    if (worldIn.getBlockState(blockpos).getBlock() == this) {
      if (isDoubleChest(worldIn, blockpos))
        return false; 
      i++;
    } 
    if (worldIn.getBlockState(blockpos1).getBlock() == this) {
      if (isDoubleChest(worldIn, blockpos1))
        return false; 
      i++;
    } 
    if (worldIn.getBlockState(blockpos2).getBlock() == this) {
      if (isDoubleChest(worldIn, blockpos2))
        return false; 
      i++;
    } 
    if (worldIn.getBlockState(blockpos3).getBlock() == this) {
      if (isDoubleChest(worldIn, blockpos3))
        return false; 
      i++;
    } 
    return (i <= 1);
  }
  
  public boolean canProvidePower(IBlockState state) {
    return false;
  }
  
  public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state) {
    if (worldIn.isRemote)
      return state; 
    IBlockState iblockstate = worldIn.getBlockState(pos.north());
    IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
    IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
    IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
    EnumFacing enumfacing = (EnumFacing)state.getValue((IProperty)FACING);
    if (iblockstate.getBlock() != this && iblockstate1.getBlock() != this) {
      boolean flag = iblockstate.isFullBlock();
      boolean flag1 = iblockstate1.isFullBlock();
      if (iblockstate2.getBlock() == this || iblockstate3.getBlock() == this) {
        EnumFacing enumfacing2;
        BlockPos blockpos1 = (iblockstate2.getBlock() == this) ? pos.west() : pos.east();
        IBlockState iblockstate7 = worldIn.getBlockState(blockpos1.north());
        IBlockState iblockstate6 = worldIn.getBlockState(blockpos1.south());
        enumfacing = EnumFacing.SOUTH;
        if (iblockstate2.getBlock() == this) {
          enumfacing2 = (EnumFacing)iblockstate2.getValue((IProperty)FACING);
        } else {
          enumfacing2 = (EnumFacing)iblockstate3.getValue((IProperty)FACING);
        } 
        if (enumfacing2 == EnumFacing.NORTH)
          enumfacing = EnumFacing.NORTH; 
        if ((flag || iblockstate7.isFullBlock()) && !flag1 && !iblockstate6.isFullBlock())
          enumfacing = EnumFacing.SOUTH; 
        if ((flag1 || iblockstate6.isFullBlock()) && !flag && !iblockstate7.isFullBlock())
          enumfacing = EnumFacing.NORTH; 
      } 
    } else {
      EnumFacing enumfacing1;
      BlockPos blockpos = (iblockstate.getBlock() == this) ? pos.north() : pos.south();
      IBlockState iblockstate4 = worldIn.getBlockState(blockpos.west());
      IBlockState iblockstate5 = worldIn.getBlockState(blockpos.east());
      enumfacing = EnumFacing.EAST;
      if (iblockstate.getBlock() == this) {
        enumfacing1 = (EnumFacing)iblockstate.getValue((IProperty)FACING);
      } else {
        enumfacing1 = (EnumFacing)iblockstate1.getValue((IProperty)FACING);
      } 
      if (enumfacing1 == EnumFacing.WEST)
        enumfacing = EnumFacing.WEST; 
      if ((iblockstate2.isFullBlock() || iblockstate4.isFullBlock()) && !iblockstate3.isFullBlock() && !iblockstate5.isFullBlock())
        enumfacing = EnumFacing.EAST; 
      if ((iblockstate3.isFullBlock() || iblockstate5.isFullBlock()) && !iblockstate2.isFullBlock() && !iblockstate4.isFullBlock())
        enumfacing = EnumFacing.WEST; 
    } 
    state = state.withProperty((IProperty)FACING, (Comparable)enumfacing);
    worldIn.setBlockState(pos, state, 3);
    return state;
  }
  
  public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state) {
    EnumFacing enumfacing = null;
    for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
      IBlockState iblockstate = worldIn.getBlockState(pos.offset(enumfacing1));
      if (iblockstate.getBlock() == this)
        return state; 
      if (iblockstate.isFullBlock()) {
        if (enumfacing != null) {
          enumfacing = null;
          break;
        } 
        enumfacing = enumfacing1;
      } 
    } 
    if (enumfacing != null)
      return state.withProperty((IProperty)FACING, (Comparable)enumfacing.getOpposite()); 
    EnumFacing enumfacing2 = (EnumFacing)state.getValue((IProperty)FACING);
    if (worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock())
      enumfacing2 = enumfacing2.getOpposite(); 
    if (worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock())
      enumfacing2 = enumfacing2.rotateY(); 
    if (worldIn.getBlockState(pos.offset(enumfacing2)).isFullBlock())
      enumfacing2 = enumfacing2.getOpposite(); 
    return state.withProperty((IProperty)FACING, (Comparable)enumfacing2);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)FACING });
  }
  
  public TileEntity createNewTileEntity(World world, int p_149915_2_) {
    return (TileEntity)new TileEntityLockedChest();
  }
  
  public TileEntity createTileEntity(World world, IBlockState state) {
    return (TileEntity)new TileEntityLockedChest();
  }
  
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return BlockFaceShape.UNDEFINED;
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    if (source.getBlockState(pos.north()).getBlock() == this)
      return NORTH_CHEST_AABB; 
    if (source.getBlockState(pos.south()).getBlock() == this)
      return SOUTH_CHEST_AABB; 
    if (source.getBlockState(pos.west()).getBlock() == this)
      return WEST_CHEST_AABB; 
    return (source.getBlockState(pos.east()).getBlock() == this) ? EAST_CHEST_AABB : NOT_CONNECTED_AABB;
  }
  
  public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
    return Container.calcRedstoneFromInventory((IInventory)getLockableContainer(worldIn, pos));
  }
  
  public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking) {
    TileEntity tileentity = worldIn.getTileEntity(pos);
    if (!(tileentity instanceof TileEntityLockedChest))
      return null; 
    TileEntityLockedChest tileEntityLockedChest = (TileEntityLockedChest)tileentity;
    if (isBlocked(worldIn, pos))
      return null; 
    TileEntityLockedChest.InventoryLockedLargeChest inventoryLockedLargeChest = null;
    Iterator<EnumFacing> iterator = EnumFacing.Plane.HORIZONTAL.iterator();
    while (iterator.hasNext()) {
      EnumFacing enumfacing = iterator.next();
      BlockPos blockpos1 = pos.offset(enumfacing);
      Block block = worldIn.getBlockState(blockpos1).getBlock();
      if (block == this) {
        if (isBlocked(worldIn, blockpos1))
          return null; 
        TileEntity tileentity1 = worldIn.getTileEntity(blockpos1);
        if (tileentity1 instanceof TileEntityLockedChest) {
          if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
            inventoryLockedLargeChest = new TileEntityLockedChest.InventoryLockedLargeChest("container.chestDouble", tileEntityLockedChest, (TileEntityLockedChest)tileentity1);
          } else {
            inventoryLockedLargeChest = new TileEntityLockedChest.InventoryLockedLargeChest("container.chestDouble", (TileEntityLockedChest)tileentity1, tileEntityLockedChest);
          }
        } 
      } 
    } 
    return (ILockableContainer)inventoryLockedLargeChest;
  }
  
  @Nullable
  public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
    return getContainer(worldIn, pos, false);
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((EnumFacing)state.getValue((IProperty)FACING)).getIndex();
  }
  
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
  }
  
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return getDefaultState().withProperty((IProperty)FACING, (Comparable)placer.getHorizontalFacing());
  }
  
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing enumfacing = EnumFacing.getFront(meta);
    if (enumfacing.getAxis() == EnumFacing.Axis.Y)
      enumfacing = EnumFacing.NORTH; 
    return getDefaultState().withProperty((IProperty)FACING, (Comparable)enumfacing);
  }
  
  public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return (side == EnumFacing.UP) ? blockState.getWeakPower(blockAccess, pos, side) : 0;
  }
  
  public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    if (!blockState.canProvidePower())
      return 0; 
    int i = 0;
    TileEntity tileentity = blockAccess.getTileEntity(pos);
    if (tileentity instanceof TileEntityLockedChest)
      i = ((TileEntityLockedChest)tileentity).numPlayersUsing; 
    return MathHelper.clamp(i, 0, 15);
  }
  
  public boolean hasComparatorInputOverride(IBlockState state) {
    return true;
  }
  
  @SideOnly(Side.CLIENT)
  public boolean hasCustomBreakingProgress(IBlockState state) {
    return true;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
  
  private boolean isBelowSolidBlock(World worldIn, BlockPos pos) {
    return worldIn.getBlockState(pos.up()).doesSideBlockChestOpening((IBlockAccess)worldIn, pos.up(), EnumFacing.DOWN);
  }
  
  private boolean isBlocked(World worldIn, BlockPos pos) {
    return (isBelowSolidBlock(worldIn, pos) || isOcelotSittingOnChest(worldIn, pos));
  }
  
  private boolean isDoubleChest(World worldIn, BlockPos pos) {
    if (worldIn.getBlockState(pos).getBlock() != this)
      return false; 
    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
      if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() == this)
        return true; 
    } 
    return false;
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
    for (Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), (pos.getY() + 1), pos.getZ(), (pos.getX() + 1), (pos.getY() + 2), (pos.getZ() + 1)))) {
      EntityOcelot entityocelot = (EntityOcelot)entity;
      if (entityocelot.isSitting())
        return true; 
    } 
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
  
  public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
    super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    TileEntity tileentity = worldIn.getTileEntity(pos);
    if (tileentity instanceof TileEntityLockedChest)
      tileentity.updateContainingBlockInfo(); 
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote)
      ClientSender.activateMillChest(playerIn, new Point(pos)); 
    return true;
  }
  
  public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
    checkForSurroundingChests(worldIn, pos, state);
    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
      BlockPos blockpos = pos.offset(enumfacing);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      if (iblockstate.getBlock() == this)
        checkForSurroundingChests(worldIn, blockpos, iblockstate); 
    } 
  }
  
  public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
    super.onBlockClicked(worldIn, pos, playerIn);
  }
  
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor((placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3).getOpposite();
    state = state.withProperty((IProperty)FACING, (Comparable)enumfacing);
    BlockPos blockpos = pos.north();
    BlockPos blockpos1 = pos.south();
    BlockPos blockpos2 = pos.west();
    BlockPos blockpos3 = pos.east();
    boolean flag = (this == worldIn.getBlockState(blockpos).getBlock());
    boolean flag1 = (this == worldIn.getBlockState(blockpos1).getBlock());
    boolean flag2 = (this == worldIn.getBlockState(blockpos2).getBlock());
    boolean flag3 = (this == worldIn.getBlockState(blockpos3).getBlock());
    if (!flag && !flag1 && !flag2 && !flag3) {
      worldIn.setBlockState(pos, state, 3);
    } else if (enumfacing.getAxis() != EnumFacing.Axis.X || (!flag && !flag1)) {
      if (enumfacing.getAxis() == EnumFacing.Axis.Z && (flag2 || flag3)) {
        if (flag2) {
          worldIn.setBlockState(blockpos2, state, 3);
        } else {
          worldIn.setBlockState(blockpos3, state, 3);
        } 
        worldIn.setBlockState(pos, state, 3);
      } 
    } else {
      if (flag) {
        worldIn.setBlockState(blockpos, state, 3);
      } else {
        worldIn.setBlockState(blockpos1, state, 3);
      } 
      worldIn.setBlockState(pos, state, 3);
    } 
    if (stack.hasDisplayName()) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityLockedChest)
        ((TileEntityLockedChest)tileentity).setCustomName(stack.getDisplayName()); 
    } 
  }
  
  public int quantityDropped(Random random) {
    return 0;
  }
  
  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
    return (!isDoubleChest(world, pos) && super.rotateBlock(world, pos, axis));
  }
  
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue((IProperty)FACING)));
  }
  
  public IBlockState withRotation(IBlockState state, Rotation rot) {
    return state.withProperty((IProperty)FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)FACING)));
  }
}
