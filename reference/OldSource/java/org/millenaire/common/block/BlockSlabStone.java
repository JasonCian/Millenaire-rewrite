package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.IMetaBlockName;

public class BlockSlabStone extends BlockSlab implements IMetaBlockName {
  public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
  
  public static final PropertyEnum<BlockDecorativeStone.EnumType> VARIANT = PropertyEnum.create("variant", BlockDecorativeStone.EnumType.class);
  
  public BlockSlabStone(String blockName) {
    super(Material.ROCK);
    IBlockState iblockstate = this.blockState.getBaseState();
    if (isDouble()) {
      iblockstate = iblockstate.withProperty((IProperty)SEAMLESS, Boolean.valueOf(false));
    } else {
      iblockstate = iblockstate.withProperty((IProperty)HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
    } 
    setDefaultState(iblockstate.withProperty((IProperty)VARIANT, BlockDecorativeStone.EnumType.MUDBRICK));
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setHardness(2.0F);
    setResistance(10.0F);
    setSoundType(SoundType.STONE);
    this.useNeighborBrightness = true;
  }
  
  protected BlockStateContainer createBlockState() {
    return isDouble() ? new BlockStateContainer((Block)this, new IProperty[] { (IProperty)SEAMLESS, (IProperty)VARIANT }) : new BlockStateContainer((Block)this, new IProperty[] { (IProperty)HALF, (IProperty)VARIANT });
  }
  
  public int damageDropped(IBlockState state) {
    return ((BlockDecorativeStone.EnumType)state.getValue((IProperty)VARIANT)).getMetadata();
  }
  
  public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return ((BlockDecorativeStone.EnumType)state.getValue((IProperty)VARIANT)).getMapColor();
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    i |= ((BlockDecorativeStone.EnumType)state.getValue((IProperty)VARIANT)).getMetadata();
    if (isDouble()) {
      if (((Boolean)state.getValue((IProperty)SEAMLESS)).booleanValue())
        i |= 0x8; 
    } else if (state.getValue((IProperty)HALF) == BlockSlab.EnumBlockHalf.TOP) {
      i |= 0x8;
    } 
    return i;
  }
  
  public String getSpecialName(ItemStack stack) {
    return getTranslationKey(stack.getMetadata());
  }
  
  public IBlockState getStateFromMeta(int meta) {
    IBlockState iblockstate = getDefaultState().withProperty((IProperty)VARIANT, BlockDecorativeStone.EnumType.byMetadata(meta & 0x7));
    if (isDouble()) {
      iblockstate = iblockstate.withProperty((IProperty)SEAMLESS, Boolean.valueOf(((meta & 0x8) != 0)));
    } else {
      iblockstate = iblockstate.withProperty((IProperty)HALF, ((meta & 0x8) == 0) ? (Comparable)BlockSlab.EnumBlockHalf.BOTTOM : (Comparable)BlockSlab.EnumBlockHalf.TOP);
    } 
    return iblockstate;
  }
  
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (BlockDecorativeStone.EnumType enumtype : BlockDecorativeStone.EnumType.values()) {
      if (enumtype.hasSlab())
        items.add(new ItemStack((Block)this, 1, enumtype.getMetadata())); 
    } 
  }
  
  public Comparable<?> getTypeForItem(ItemStack stack) {
    return BlockDecorativeStone.EnumType.byMetadata(stack.getMetadata() & 0x7);
  }
  
  public String getTranslationKey(int meta) {
    return "tile.millenaire.slabs_" + ((BlockDecorativeStone.EnumType)getStateFromMeta(meta).getValue((IProperty)VARIANT)).getName();
  }
  
  public String getUnlocalizedName(int meta) {
    return getUnlocalizedName();
  }
  
  public IProperty<?> getVariantProperty() {
    return (IProperty<?>)VARIANT;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (BlockDecorativeStone.EnumType enumtype : BlockDecorativeStone.EnumType.values()) {
      if (enumtype.hasSlab())
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), enumtype.getMetadata(), new ModelResourceLocation(
              getRegistryName(), "half=bottom,variant=" + enumtype.getName())); 
    } 
  }
  
  public boolean isDouble() {
    return false;
  }
}
