package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.world.WorldGenAppleTree;
import org.millenaire.common.world.WorldGenCherry;
import org.millenaire.common.world.WorldGenOliveTree;
import org.millenaire.common.world.WorldGenPistachio;
import org.millenaire.common.world.WorldGenSakura;

public class BlockMillSapling extends BlockBush implements IGrowable {
  public enum EnumMillWoodType {
    APPLETREE(MapColor.WOOD),
    OLIVETREE(MapColor.WOOD),
    PISTACHIO(MapColor.WOOD),
    CHERRY(MapColor.WOOD),
    SAKURA(MapColor.WOOD);
    
    private final MapColor mapColor;
    
    EnumMillWoodType(MapColor mapColorIn) {
      this.mapColor = mapColorIn;
    }
    
    public MapColor getMapColor() {
      return this.mapColor;
    }
  }
  
  public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
  
  protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
  
  private final EnumMillWoodType type;
  
  protected BlockMillSapling(String blockName, EnumMillWoodType type) {
    setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
    this.type = type;
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setHardness(0.0F);
    setSoundType(SoundType.PLANT);
    setTickRandomly(true);
  }
  
  public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
    return true;
  }
  
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    return (worldIn.rand.nextFloat() < 0.45D);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { STAGE });
  }
  
  public int damageDropped(IBlockState state) {
    return 0;
  }
  
  public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (!TerrainGen.saplingGrowTree(worldIn, rand, pos))
      return; 
    
    WorldGenerator generator = null;
    
    switch (this.type) {
      case APPLETREE:
        generator = new WorldGenAppleTree(true);
        break;
      case OLIVETREE:
        generator = new WorldGenOliveTree(true);
        break;
      case PISTACHIO:
        generator = new WorldGenPistachio(true);
        break;
      case CHERRY:
        generator = new WorldGenCherry(true);
        break;
      case SAKURA:
        generator = new WorldGenSakura(true);
        break;
      default:
        generator = (rand.nextInt(10) == 0) ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        break;
    }
    
    IBlockState iblockstate2 = Blocks.AIR.getDefaultState();
    worldIn.setBlockState(pos, iblockstate2, 4);
    
    if (!generator.generate(worldIn, rand, pos.add(0, 0, 0))) {
      worldIn.setBlockState(pos, state, 4);
    }
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return SAPLING_AABB;
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    i |= ((Integer)state.getValue(STAGE)).intValue();
    return i;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(STAGE, Integer.valueOf(meta & 0x8));
  }
  
  public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (((Integer)state.getValue(STAGE)).intValue() == 0) {
      worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
    } else {
      generateTree(worldIn, pos, state, rand);
    } 
  }
  
  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    grow(worldIn, pos, state, rand);
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
  
  public boolean isTypeAt(World worldIn, BlockPos pos, EnumMillWoodType type) {
    IBlockState iblockstate = worldIn.getBlockState(pos);
    return (iblockstate.getBlock() == this);
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    if (!worldIn.isRemote) {
      super.updateTick(worldIn, pos, state, rand);
      if (!worldIn.isAreaLoaded(pos, 1))
        return; 
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
        grow(worldIn, pos, state, rand); 
    } 
  }
}
