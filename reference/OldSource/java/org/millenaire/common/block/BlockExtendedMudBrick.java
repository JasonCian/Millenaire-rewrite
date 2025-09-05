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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.IMetaBlockName;

public class BlockExtendedMudBrick extends BlockSlab implements IMetaBlockName {
  public enum EnumType implements IStringSerializable {
    MUDBRICK_SMOOTH(0, "mudbrick_smooth", MapColor.BROWN, true),
    MUDBRICK_SELJUK_DECORATED(1, "mudbrick_seljuk_decorated", MapColor.BLUE, true),
    MUDBRICK_SELJUK_ORNAMENTED(2, "mudbrick_seljuk_ornamented", MapColor.BROWN, true);
    
    private final boolean hasSlab;
    
    private final MapColor mapColor;
    
    private final String name;
    
    private final int meta;
    
    private static final EnumType[] META_LOOKUP = new EnumType[(values()).length];
    
    static {
      EnumType[] var0 = values();
      for (EnumType var3 : var0)
        META_LOOKUP[var3.getMetadata()] = var3; 
    }
    
    public static EnumType byMetadata(int meta) {
      if (meta < 0 || meta >= META_LOOKUP.length)
        meta = 0; 
      return META_LOOKUP[meta];
    }
    
    EnumType(int meta, String name, MapColor mapColor, boolean hasSlab) {
      this.meta = meta;
      this.name = name;
      this.mapColor = mapColor;
      this.hasSlab = hasSlab;
    }
    
    public MapColor getMapColor() {
      return this.mapColor;
    }
    
    public int getMetadata() {
      return this.meta;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String getUnlocalizedName() {
      return this.name;
    }
    
    public boolean hasSlab() {
      return this.hasSlab;
    }
    
    public String toString() {
      return this.name;
    }
  }
  
  public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);
  
  public BlockExtendedMudBrick(String blockName) {
    super(Material.ROCK);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setHarvestLevel("pickaxe", 0);
    setHardness(1.5F);
    setResistance(10.0F);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)VARIANT, EnumType.MUDBRICK_SMOOTH));
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)VARIANT });
  }
  
  public int damageDropped(IBlockState state) {
    return ((EnumType)state.getValue((IProperty)VARIANT)).getMetadata();
  }
  
  public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return ((EnumType)state.getValue((IProperty)VARIANT)).getMapColor();
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((EnumType)state.getValue((IProperty)VARIANT)).getMetadata();
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + ((EnumType)getStateFromMeta(stack.getMetadata()).getValue((IProperty)VARIANT)).getName();
  }
  
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return getStateFromMeta(meta);
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)VARIANT, EnumType.byMetadata(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (EnumType enumtype : EnumType.values())
      items.add(new ItemStack((Block)this, 1, enumtype.getMetadata())); 
  }
  
  public Comparable<?> getTypeForItem(ItemStack stack) {
    return EnumType.byMetadata(stack.getMetadata() & 0x7);
  }
  
  public String getTranslationKey(int meta) {
    return "tile.millenaire." + ((EnumType)getStateFromMeta(meta).getValue((IProperty)VARIANT)).getName();
  }
  
  public String getUnlocalizedName(int meta) {
    return getTranslationKey(meta);
  }
  
  public IProperty<?> getVariantProperty() {
    return (IProperty<?>)VARIANT;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (EnumType enumtype : EnumType.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "variant=" + enumtype.getName())); 
  }
  
  public boolean isDouble() {
    return true;
  }
  
  public int quantityDropped(Random random) {
    return 1;
  }
}
