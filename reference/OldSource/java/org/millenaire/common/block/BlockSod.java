package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.IMetaBlockName;

public class BlockSod extends Block implements IMetaBlockName {
  public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class);
  
  public BlockSod(String blockName) {
    super(Material.WOOD);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)VARIANT, (Comparable)BlockPlanks.EnumType.OAK));
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setHarvestLevel("axe", 0);
    setHardness(2.0F);
    setResistance(5.0F);
    setSoundType(SoundType.WOOD);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)VARIANT });
  }
  
  public int damageDropped(IBlockState state) {
    return ((BlockPlanks.EnumType)state.getValue((IProperty)VARIANT)).getMetadata();
  }
  
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((BlockPlanks.EnumType)state.getValue((IProperty)VARIANT)).getMetadata();
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + getRegistryName().getResourcePath() + "_" + ((BlockPlanks.EnumType)getStateFromMeta(stack.getMetadata()).getValue((IProperty)VARIANT)).getName();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)VARIANT, (Comparable)BlockPlanks.EnumType.byMetadata(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (BlockPlanks.EnumType enumtype : BlockPlanks.EnumType.values())
      items.add(new ItemStack(this, 1, enumtype.getMetadata())); 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (BlockPlanks.EnumType enumtype : BlockPlanks.EnumType.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "variant=" + enumtype.getName())); 
  }
}
