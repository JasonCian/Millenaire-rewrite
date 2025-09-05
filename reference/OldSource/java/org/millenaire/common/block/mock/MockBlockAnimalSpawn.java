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

public class MockBlockAnimalSpawn extends Block implements IMetaBlockName {
  public enum Creature implements IStringSerializable {
    COW(0, "cow"),
    PIG(1, "pig"),
    SHEEP(2, "sheep"),
    CHICKEN(3, "chicken"),
    SQUID(4, "squid"),
    WOLF(5, "wolf"),
    POLARBEAR(6, "polarbear");
    
    public final String name;
    
    public final int meta;
    
    public static Creature fromMeta(int meta) {
      for (Creature t : values()) {
        if (t.meta == meta)
          return t; 
      } 
      return null;
    }
    
    public static int getMetaFromName(String name) {
      for (Creature creature : values()) {
        if (creature.name.equalsIgnoreCase(name))
          return creature.meta; 
      } 
      return -1;
    }
    
    Creature(int m, String n) {
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
      return "Animal Spawn (" + this.name + ")";
    }
  }
  
  public static final PropertyEnum<Creature> CREATURE = PropertyEnum.create("creature", Creature.class);
  
  public MockBlockAnimalSpawn(String blockName) {
    super(Material.ROCK);
    disableStats();
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setBlockUnbreakable();
    setCreativeTab(MillBlocks.tabMillenaireContentCreator);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)CREATURE });
  }
  
  public int damageDropped(IBlockState state) {
    return ((Creature)state.getValue((IProperty)CREATURE)).getMetadata();
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((Creature)state.getValue((IProperty)CREATURE)).meta;
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + getRegistryName().getResourcePath() + "." + ((Creature)getStateFromMeta(stack.getMetadata()).getValue((IProperty)CREATURE)).getName();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)CREATURE, Creature.fromMeta(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (Creature enumtype : Creature.values())
      items.add(new ItemStack(this, 1, enumtype.getMetadata())); 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (Creature enumtype : Creature.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "creature=" + enumtype.getName())); 
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote)
      return true; 
    if (playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == MillItems.NEGATION_WAND) {
      int meta = state.getBlock().getMetaFromState(state) + 1;
      if (Creature.fromMeta(meta) == null)
        meta = 0; 
      worldIn.setBlockState(pos, state.withProperty((IProperty)CREATURE, Creature.fromMeta(meta)), 3);
      Mill.proxy.sendLocalChat(playerIn, 'a', (Creature.fromMeta(meta)).name);
      return true;
    } 
    return false;
  }
}
