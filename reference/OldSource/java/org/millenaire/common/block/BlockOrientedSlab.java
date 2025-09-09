package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.IMetaBlockName;

public abstract class BlockOrientedSlab extends BlockSlab implements IMetaBlockName {
  public static class BlockOrientedSlabDouble extends BlockOrientedSlab {
    public BlockOrientedSlabDouble(String slabName) {
      super(slabName);
    }
    
    public boolean isDouble() {
      return true;
    }
  }
  
  public static class BlockOrientedSlabSlab extends BlockOrientedSlab {
    public BlockOrientedSlabSlab(String slabName) {
      super(slabName);
    }
    
    public boolean isDouble() {
      return false;
    }
  }
  
  public enum Variant implements IStringSerializable {
    DEFAULT;
    
    public String getName() {
      return "default";
    }
  }
  
  public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
  
  public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
  
  public BlockOrientedSlab(String slabName) {
    super(Material.ROCK);
    IBlockState iblockstate = this.blockState.getBaseState();
    if (!isDouble()) {
      iblockstate = iblockstate.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
      this.useNeighborBrightness = true;
    } 
    iblockstate = iblockstate.withProperty((IProperty)VARIANT, Variant.DEFAULT);
    setDefaultState(iblockstate.withProperty((IProperty)AXIS, (Comparable)EnumFacing.Axis.X));
    setHarvestLevel("pickaxe", 0);
    setHardness(1.5F);
    setResistance(10.0F);
    setUnlocalizedName("millenaire." + slabName);
    setRegistryName(slabName);
    setCreativeTab(MillBlocks.tabMillenaire);
  }
  
  protected BlockStateContainer createBlockState() {
    return isDouble() ? new BlockStateContainer((Block)this, new IProperty[] { (IProperty)VARIANT, (IProperty)AXIS }) : new BlockStateContainer((Block)this, new IProperty[] { (IProperty)VARIANT, (IProperty)HALF, (IProperty)AXIS });
  }
  
  public int damageDropped(IBlockState state) {
    return 0;
  }
  
  public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    return new ItemStack(state.getBlock());
  }
  
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(state.getBlock());
  }
  
  public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return MapColor.ADOBE;
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis)state.getValue((IProperty)AXIS);
    if (enumfacing$axis == EnumFacing.Axis.X) {
      i |= 0x4;
    } else if (enumfacing$axis == EnumFacing.Axis.Z) {
      i |= 0x8;
    } 
    if (!isDouble() && state.getValue((IProperty)HALF) == BlockSlab.EnumBlockHalf.TOP)
      i |= 0x2; 
    return i;
  }
  
    public String getSpecialName(ItemStack stack) {
    return getUnlocalizedName();
  }

  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    IBlockState iblockstate = getStateFromMeta(meta);
    if (facing.getAxis() == EnumFacing.Axis.Y) {
      iblockstate = iblockstate.withProperty((IProperty)AXIS, (Comparable)EnumFacing.Axis.X);
    } else {
      iblockstate = iblockstate.withProperty((IProperty)AXIS, (Comparable)facing.getAxis());
    } 
    if (!isDouble())
      iblockstate = (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || hitY <= 0.5D)) ? iblockstate.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM) : iblockstate.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP); 
    return iblockstate;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
    int i = meta & 0xC;
    if (i == 4) {
      enumfacing$axis = EnumFacing.Axis.X;
    } else if (i == 8) {
      enumfacing$axis = EnumFacing.Axis.Z;
    } 
    IBlockState iblockstate = getDefaultState().withProperty((IProperty)AXIS, (Comparable)enumfacing$axis);
    if (!isDouble())
      iblockstate = iblockstate.withProperty((IProperty)HALF, ((meta & 0x2) == 0) ? (Comparable)BlockSlab.EnumBlockHalf.BOTTOM : (Comparable)BlockSlab.EnumBlockHalf.TOP); 
    return iblockstate;
  }
  
  public Comparable<?> getTypeForItem(ItemStack stack) {
    return Variant.DEFAULT;
  }
  
    public String getUnlocalizedName(int meta) {
    return getUnlocalizedName();
  }

  public IProperty<?> getVariantProperty() {
    return (IProperty<?>)VARIANT;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    if (isDouble()) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(
            getRegistryName(), "axis=x,variant=default"));
    } else {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(
            getRegistryName(), "axis=x,half=bottom,variant=default"));
    } 
  }
  
  public int quantityDropped(Random random) {
    return 1;
  }
}
