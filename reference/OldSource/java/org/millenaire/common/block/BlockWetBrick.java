package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.IMetaBlockName;
import org.millenaire.common.utilities.MillCommonUtilities;

public class BlockWetBrick extends Block implements IMetaBlockName {
  public enum EnumType implements IStringSerializable {
    WETBRICK0(0, "wetbrick0"),
    WETBRICK1(1, "wetbrick1"),
    WETBRICK2(2, "wetbrick2");
    
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
    
    EnumType(int meta, String name) {
      this.meta = meta;
      this.name = name;
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
    
    public String toString() {
      return this.name;
    }
  }
  
  static final PropertyEnum<EnumType> PROGRESS = PropertyEnum.create("progress", EnumType.class);
  
  public BlockWetBrick(String blockName) {
    super(Material.GROUND);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setTickRandomly(true);
    setHarvestLevel("shovel", 0);
    setHardness(0.8F);
    setSoundType(SoundType.GROUND);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)PROGRESS, EnumType.WETBRICK0));
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)PROGRESS });
  }
  
  public int damageDropped(IBlockState state) {
    return 0;
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((EnumType)state.getValue((IProperty)PROGRESS)).getMetadata();
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + ((EnumType)getStateFromMeta(stack.getMetadata()).getValue((IProperty)PROGRESS)).getName();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)PROGRESS, EnumType.byMetadata(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    items.add(new ItemStack(this, 1, 0));
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (EnumType enumtype : EnumType.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "progress=" + enumtype.getName())); 
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    int currentValue = ((EnumType)state.getValue((IProperty)PROGRESS)).getMetadata();
    if (worldIn.getLightFromNeighbors(pos.up()) > 14) {
      currentValue++;
      if (currentValue < 2 && MillCommonUtilities.chanceOn(2)) {
        currentValue++;
        IBlockState newState = state.withProperty((IProperty)PROGRESS, EnumType.byMetadata(currentValue));
        worldIn.setBlockState(pos, newState);
      } else if (currentValue < 3) {
        IBlockState newState = state.withProperty((IProperty)PROGRESS, EnumType.byMetadata(currentValue));
        worldIn.setBlockState(pos, newState);
      } else {
        worldIn.setBlockState(pos, MillBlocks.STONE_DECORATION.getDefaultState().withProperty((IProperty)BlockDecorativeStone.VARIANT, BlockDecorativeStone.EnumType.MUDBRICK));
      } 
    } 
  }
}
