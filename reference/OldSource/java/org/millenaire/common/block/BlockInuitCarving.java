package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInuitCarving extends BlockRotatedPillar {
  private static final AxisAlignedBB CARVING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
  
  protected BlockInuitCarving(String blockName) {
    super(Material.ICE);
    setCreativeTab(MillBlocks.tabMillenaire);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)AXIS, (Comparable)EnumFacing.Axis.X));
    setHarvestLevel("pickaxe", 0);
    setHardness(0.5F);
    setResistance(2.0F);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return CARVING_AABB;
  }
  
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    if (facing.getAxis() == EnumFacing.Axis.Y)
      return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty((IProperty)AXIS, (Comparable)EnumFacing.Axis.X); 
    return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty((IProperty)AXIS, (Comparable)facing.getAxis());
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    items.add(new ItemStack((Block)this, 1, getMetaFromState(getDefaultState())));
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 4, new ModelResourceLocation(getRegistryName(), "axis=x"));
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
}
