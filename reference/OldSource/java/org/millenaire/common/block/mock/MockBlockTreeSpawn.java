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

public class MockBlockTreeSpawn extends Block implements IMetaBlockName {
  public enum TreeType implements IStringSerializable {
    OAK(0, "oak"),
    SPRUCE(1, "pine"),
    BIRCH(2, "birch"),
    JUNGLE(3, "jungle"),
    ACACIA(4, "acacia"),
    DARK_OAK(5, "darkoak"),
    APPLE_TREE(6, "appletree"),
    PISTACHIO(7, "pistachiotree"),
    OLIVE_TREE(8, "olivetree"),
    CHERRY_TREE(9, "cherrytree"),
    SAKURA_TREE(10, "sakuratree");
    
    public final int meta;
    
    public final String name;
    
    public static TreeType fromMeta(int meta) {
      for (TreeType t : values()) {
        if (t.meta == meta)
          return t; 
      } 
      return null;
    }
    
    public static int getMetaFromName(String name) {
      for (TreeType type : values()) {
        if (type.name.equalsIgnoreCase(name))
          return type.meta; 
      } 
      return -1;
    }
    
    TreeType(int m, String n) {
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
      return "Tree Spawn (" + this.name + ")";
    }
  }
  
  public static final PropertyEnum<TreeType> TREETYPE = PropertyEnum.create("treetype", TreeType.class);
  
  public MockBlockTreeSpawn(String blockName) {
    super(Material.PLANTS);
    disableStats();
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setBlockUnbreakable();
    setCreativeTab(MillBlocks.tabMillenaireContentCreator);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)TREETYPE });
  }
  
  public int damageDropped(IBlockState state) {
    return ((TreeType)state.getValue((IProperty)TREETYPE)).getMetadata();
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((TreeType)state.getValue((IProperty)TREETYPE)).meta;
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + getRegistryName().getResourcePath() + "." + ((TreeType)getStateFromMeta(stack.getMetadata()).getValue((IProperty)TREETYPE)).getName();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)TREETYPE, TreeType.fromMeta(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (TreeType enumtype : TreeType.values())
      items.add(new ItemStack(this, 1, enumtype.getMetadata())); 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (TreeType enumtype : TreeType.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "treetype=" + enumtype.getName())); 
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote)
      return true; 
    if (playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == MillItems.NEGATION_WAND) {
      int meta = state.getBlock().getMetaFromState(state) + 1;
      if (TreeType.fromMeta(meta) == null)
        meta = 0; 
      worldIn.setBlockState(pos, state.withProperty((IProperty)TREETYPE, TreeType.fromMeta(meta)), 3);
      Mill.proxy.sendLocalChat(playerIn, 'a', (TreeType.fromMeta(meta)).name);
      return true;
    } 
    return false;
  }
}
