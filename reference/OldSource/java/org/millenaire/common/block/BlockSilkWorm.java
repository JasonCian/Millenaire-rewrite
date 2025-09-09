package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
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

public class BlockSilkWorm extends Block implements IMetaBlockName {
  public enum EnumType implements IStringSerializable {
    SILKWORMEMPTY(0, "silkwormempty"),
    SILKWORMIP1(1, "silkwormip1"),
    SILKWORMIP2(2, "silkwormip2"),
    SILKWORMFULL(3, "silkwormfull");
    
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
  
  public static final PropertyEnum<EnumType> PROGRESS = PropertyEnum.create("progress", EnumType.class);
  
  public BlockSilkWorm(String blockName) {
    super(Material.WOOD);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setTickRandomly(true);
    setHarvestLevel("axe", 0);
    setHardness(2.0F);
    setResistance(5.0F);
    setSoundType(SoundType.WOOD);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)PROGRESS, EnumType.SILKWORMEMPTY));
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)PROGRESS });
  }
  
  public int damageDropped(IBlockState state) {
    return 0;
  }
  
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return BlockFaceShape.UNDEFINED;
  }
  
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
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
    items.add(new ItemStack(this, 1, 3));
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (EnumType enumtype : EnumType.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "progress=" + enumtype.getName())); 
  }
  
  public boolean isFullCube(IBlockState state) {
    return false;
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    int currentValue = ((EnumType)state.getValue((IProperty)PROGRESS)).getMetadata();
    if (currentValue < 3 && 
      worldIn.getLight(pos.up()) < 7 && 
      rand.nextInt(2) == 0) {
      currentValue++;
      IBlockState newState = state.withProperty((IProperty)PROGRESS, EnumType.byMetadata(currentValue));
      worldIn.setBlockState(pos, newState);
    } 
  }
}
