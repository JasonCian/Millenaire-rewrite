package org.millenaire.common.block.mock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.IMetaBlockName;
import org.millenaire.common.item.MillItems;

public class MockBlockSoil extends Block implements IMetaBlockName {
  public enum CropType implements IStringSerializable {
    WHEAT(0, "soil"),
    RICE(1, "ricesoil"),
    TURMERIC(2, "turmericsoil"),
    SUGAR_CANE(3, "sugarcanesoil"),
    POTATO(4, "potatosoil"),
    NETHER_WART(5, "netherwartsoil"),
    GRAPE(6, "vinesoil"),
    MAIZE(7, "maizesoil"),
    CACAO(8, "cacaospot"),
    CARROT(9, "carrotsoil"),
    FLOWER(10, "flowersoil"),
    COTTON(11, "cottonsoil");
    
    public final int meta;
    
    public final String name;
    
    public static CropType fromMeta(int meta) {
      for (CropType t : values()) {
        if (t.meta == meta)
          return t; 
      } 
      return null;
    }
    
    CropType(int m, String n) {
      this.meta = m;
      this.name = n;
    }
    
    public int getMetadata() {
      return this.meta;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String toString() {
      return "Source Block (" + this.name + ")";
    }
  }
  
  public static final PropertyEnum<CropType> CROPTYPE = PropertyEnum.create("croptype", CropType.class);
  
  public MockBlockSoil(String blockName) {
    super(Material.ROCK);
    disableStats();
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setBlockUnbreakable();
    setCreativeTab(MillBlocks.tabMillenaireContentCreator);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)CROPTYPE });
  }
  
  public int damageDropped(IBlockState state) {
    return ((CropType)state.getValue((IProperty)CROPTYPE)).getMetadata();
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((CropType)state.getValue((IProperty)CROPTYPE)).meta;
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + getRegistryName().getResourcePath() + "." + ((CropType)getStateFromMeta(stack.getMetadata()).getValue((IProperty)CROPTYPE)).getName();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)CROPTYPE, CropType.fromMeta(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (CropType enumtype : CropType.values())
      items.add(new ItemStack(this, 1, enumtype.getMetadata())); 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (CropType enumtype : CropType.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "croptype=" + enumtype.getName())); 
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote)
      return true; 
    if (playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == MillItems.NEGATION_WAND) {
      int meta = state.getBlock().getMetaFromState(state) + 1;
      if (CropType.fromMeta(meta) == null)
        meta = 0; 
      worldIn.setBlockState(pos, state.withProperty((IProperty)CROPTYPE, CropType.fromMeta(meta)), 3);
      Mill.proxy.sendLocalChat(playerIn, 'a', (CropType.fromMeta(meta)).name);
      return true;
    } 
    return false;
  }
}
