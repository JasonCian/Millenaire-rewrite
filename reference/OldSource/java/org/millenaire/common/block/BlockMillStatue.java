package org.millenaire.common.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillStatue extends BlockDirectional {
  public static final PropertyDirection FACING = BlockHorizontal.FACING;
  
  private static final AxisAlignedBB CARVING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
  
  public BlockMillStatue(String blockName, SoundType sound, Material material) {
    super(material);
    setCreativeTab(MillBlocks.tabMillenaire);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH));
    setHarvestLevel("pickaxe", 0);
    setHardness(0.5F);
    setResistance(2.0F);
    setSoundType(sound);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setLightOpacity(0);
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return CARVING_AABB;
  }
  
  public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
    addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getBoundingBox((IBlockAccess)worldIn, pos));
  }
  
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
    EnumFacing f = EnumFacing.getDirectionFromEntityLiving(pos, placer);
    if (f != EnumFacing.DOWN && f != EnumFacing.UP)
      return getDefaultState().withProperty((IProperty)FACING, (Comparable)f); 
    return getDefaultState().withProperty((IProperty)FACING, (Comparable)EnumFacing.NORTH);
  }
  
  @Nullable
  public static EnumFacing getFacing(int meta) {
    int i = meta & 0x7;
    return (i > 5) ? null : EnumFacing.getFront(i);
  }
  
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing facing = getFacing(meta);
    if (facing == null) {
      facing = EnumFacing.NORTH;
    }
    return getDefaultState().withProperty((IProperty)FACING, (Comparable)facing);
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    i |= ((EnumFacing)state.getValue((IProperty)FACING)).getIndex();
    return i;
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)FACING });
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), "facing=up"));
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
}
