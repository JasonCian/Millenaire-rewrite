package org.millenaire.common.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillWall extends Block {
  public static final PropertyBool UP = PropertyBool.create("up");
  
  public static final PropertyBool NORTH = PropertyBool.create("north");
  
  public static final PropertyBool EAST = PropertyBool.create("east");
  
  public static final PropertyBool SOUTH = PropertyBool.create("south");
  
  public static final PropertyBool WEST = PropertyBool.create("west");
  
  protected static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[] { 
      new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), 
      new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D) };
  
  protected static final AxisAlignedBB[] CLIP_AABB_BY_INDEX = new AxisAlignedBB[] { 
      AABB_BY_INDEX[0].setMaxY(1.5D), AABB_BY_INDEX[1].setMaxY(1.5D), AABB_BY_INDEX[2].setMaxY(1.5D), AABB_BY_INDEX[3]
      .setMaxY(1.5D), AABB_BY_INDEX[4].setMaxY(1.5D), AABB_BY_INDEX[5].setMaxY(1.5D), AABB_BY_INDEX[6].setMaxY(1.5D), AABB_BY_INDEX[7].setMaxY(1.5D), AABB_BY_INDEX[8]
      .setMaxY(1.5D), AABB_BY_INDEX[9].setMaxY(1.5D), 
      AABB_BY_INDEX[10].setMaxY(1.5D), AABB_BY_INDEX[11].setMaxY(1.5D), AABB_BY_INDEX[12].setMaxY(1.5D), AABB_BY_INDEX[13]
      .setMaxY(1.5D), AABB_BY_INDEX[14].setMaxY(1.5D), AABB_BY_INDEX[15].setMaxY(1.5D) };
  
  private final Block baseBlock;
  
  private static int getAABBIndex(IBlockState state) {
    int i = 0;
    if (((Boolean)state.getValue((IProperty)NORTH)).booleanValue())
      i |= 1 << EnumFacing.NORTH.getHorizontalIndex(); 
    if (((Boolean)state.getValue((IProperty)EAST)).booleanValue())
      i |= 1 << EnumFacing.EAST.getHorizontalIndex(); 
    if (((Boolean)state.getValue((IProperty)SOUTH)).booleanValue())
      i |= 1 << EnumFacing.SOUTH.getHorizontalIndex(); 
    if (((Boolean)state.getValue((IProperty)WEST)).booleanValue())
      i |= 1 << EnumFacing.WEST.getHorizontalIndex(); 
    return i;
  }
  
  protected static boolean isExcepBlockForAttachWithPiston(Block p_194143_0_) {
    return (Block.isExceptBlockForAttachWithPiston(p_194143_0_) || p_194143_0_ == Blocks.BARRIER || p_194143_0_ == Blocks.MELON_BLOCK || p_194143_0_ == Blocks.PUMPKIN || p_194143_0_ == Blocks.LIT_PUMPKIN);
  }
  
  public BlockMillWall(String blockName, Block baseBlock) {
    super(baseBlock.getMaterial(null));
    this.baseBlock = baseBlock;
    setCreativeTab(MillBlocks.tabMillenaire);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)UP, Boolean.valueOf(false)).withProperty((IProperty)NORTH, Boolean.valueOf(false)).withProperty((IProperty)EAST, Boolean.valueOf(false))
        .withProperty((IProperty)SOUTH, Boolean.valueOf(false)).withProperty((IProperty)WEST, Boolean.valueOf(false)));
    setHardness(baseBlock.getBlockHardness(null, null, null));
    setResistance(baseBlock.getExplosionResistance(null) * 5.0F / 3.0F);
    setSoundType(baseBlock.getSoundType());
  }
  
  public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
    if (!isActualState)
      state = getActualState(state, (IBlockAccess)worldIn, pos); 
    addCollisionBoxToList(pos, entityBox, collidingBoxes, CLIP_AABB_BY_INDEX[getAABBIndex(state)]);
  }
  
  public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
    Block connector = world.getBlockState(pos.offset(facing)).getBlock();
    return (connector instanceof net.minecraft.block.BlockWall || connector instanceof net.minecraft.block.BlockFenceGate || connector instanceof BlockMillWall);
  }
  
  private boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing p_176253_3_) {
    IBlockState iblockstate = worldIn.getBlockState(pos);
    Block block = iblockstate.getBlock();
    BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, p_176253_3_);
    boolean flag = (blockfaceshape == BlockFaceShape.MIDDLE_POLE_THICK || (blockfaceshape == BlockFaceShape.MIDDLE_POLE && block instanceof net.minecraft.block.BlockFenceGate));
    return ((!isExcepBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID) || flag || block instanceof net.minecraft.block.BlockPane);
  }
  
  public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
    return true;
  }
  
  private boolean canWallConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
    BlockPos other = pos.offset(facing);
    Block block = world.getBlockState(other).getBlock();
    return (block.canBeConnectedTo(world, other, facing.getOpposite()) || canConnectTo(world, other, facing.getOpposite()));
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)UP, (IProperty)NORTH, (IProperty)EAST, (IProperty)WEST, (IProperty)SOUTH });
  }
  
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    boolean flag = canWallConnectTo(worldIn, pos, EnumFacing.NORTH);
    boolean flag1 = canWallConnectTo(worldIn, pos, EnumFacing.EAST);
    boolean flag2 = canWallConnectTo(worldIn, pos, EnumFacing.SOUTH);
    boolean flag3 = canWallConnectTo(worldIn, pos, EnumFacing.WEST);
    boolean flag4 = ((flag && !flag1 && flag2 && !flag3) || (!flag && flag1 && !flag2 && flag3));
    return state.withProperty((IProperty)UP, Boolean.valueOf((!flag4 || !worldIn.isAirBlock(pos.up())))).withProperty((IProperty)NORTH, Boolean.valueOf(flag)).withProperty((IProperty)EAST, Boolean.valueOf(flag1))
      .withProperty((IProperty)SOUTH, Boolean.valueOf(flag2)).withProperty((IProperty)WEST, Boolean.valueOf(flag3));
  }
  
  public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos) {
    return PathNodeType.FENCE;
  }
  
  public Block getBaseBlock() {
    return this.baseBlock;
  }
  
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return (face != EnumFacing.UP && face != EnumFacing.DOWN) ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    state = getActualState(state, source, pos);
    return AABB_BY_INDEX[getAABBIndex(state)];
  }
  
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    blockState = getActualState(blockState, worldIn, pos);
    return CLIP_AABB_BY_INDEX[getAABBIndex(blockState)];
  }
  
  public int getMetaFromState(IBlockState state) {
    return 0;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
  
  public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
    return false;
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    return this.baseBlock.onBlockActivated(worldIn, pos, this.baseBlock.getDefaultState(), playerIn, hand, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
  }
  
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return (side == EnumFacing.DOWN) ? super.shouldSideBeRendered(blockState, blockAccess, pos, side) : true;
  }
}
