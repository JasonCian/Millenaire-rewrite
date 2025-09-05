package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.Point;

public class BlockFruitLeaves extends BlockLeaves implements IGrowable {
  public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
  
  private final BlockMillSapling.EnumMillWoodType type;
  
  private final ResourceLocation fruitRL;
  
  private final ResourceLocation saplingRL;
  
  public BlockFruitLeaves(String blockName, BlockMillSapling.EnumMillWoodType type, ResourceLocation saplingRL, ResourceLocation fruitRL) {
    this.type = type;
    this.fruitRL = fruitRL;
    this.saplingRL = saplingRL;
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    Mill.proxy.setGraphicsLevel(this, true);
    setDefaultState(this.blockState.getBaseState().withProperty((IProperty)AGE, Integer.valueOf(0)).withProperty((IProperty)CHECK_DECAY, Boolean.valueOf(true)).withProperty((IProperty)DECAYABLE, Boolean.valueOf(true)));
  }
  
  public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
    return !isMaxAge(state);
  }
  
  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    return true;
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer((Block)this, new IProperty[] { (IProperty)AGE, (IProperty)CHECK_DECAY, (IProperty)DECAYABLE });
  }
  
  public int damageDropped(IBlockState state) {
    return 0;
  }
  
  protected int getAge(IBlockState state) {
    return ((Integer)state.getValue((IProperty)getAgeProperty())).intValue();
  }
  
  protected PropertyInteger getAgeProperty() {
    return AGE;
  }
  
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock(Block.getBlockFromName(this.saplingRL.toString()));
  }
  
  public int getMaxAge() {
    return 3;
  }
  
  public int getMetaFromState(IBlockState state) {
    int i = 0;
    i |= ((Integer)state.getValue((IProperty)AGE)).intValue();
    if (!((Boolean)state.getValue((IProperty)DECAYABLE)).booleanValue())
      i |= 0x4; 
    if (((Boolean)state.getValue((IProperty)CHECK_DECAY)).booleanValue())
      i |= 0x8; 
    return i;
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)AGE, Integer.valueOf(meta & 0x3)).withProperty((IProperty)DECAYABLE, Boolean.valueOf(((meta & 0x4) == 0))).withProperty((IProperty)CHECK_DECAY, Boolean.valueOf(((meta & 0x8) > 0)));
  }
  
  public BlockMillSapling.EnumMillWoodType getType() {
    return this.type;
  }
  
  public BlockPlanks.EnumType getWoodType(int meta) {
    return null;
  }
  
  public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
    worldIn.setBlockState(pos, withAge(getMaxAge()), 2);
  }
  
  public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
    if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
      player.addStat(StatList.getBlockStats((Block)this));
    } else {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
    } 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
  
  public boolean isMaxAge(IBlockState state) {
    return (((Integer)state.getValue((IProperty)getAgeProperty())).intValue() >= getMaxAge());
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (getAge(state) == getMaxAge()) {
      BlockItemUtilities.checkForHarvestTheft(player, pos);
      spawnAsEntity(worldIn, pos.down(), new ItemStack(Item.getByNameOrId(this.fruitRL.toString()), 1));
      worldIn.setBlockState(pos, state.withProperty((IProperty)AGE, Integer.valueOf(0)));
      return true;
    } 
    return false;
  }
  
  public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
    return NonNullList.withSize(1, new ItemStack((Block)this, 1, 0));
  }
  
  public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
    super.updateTick(worldIn, pos, state, rand);
    long worldTime = worldIn.getWorldTime() % 24000L;
    int targetAge = 0;
    if (worldTime > 3000L && worldTime < 5000L) {
      targetAge = 1;
    } else if (worldTime > 5000L && worldTime < 6000L) {
      targetAge = 2;
    } else if (worldTime > 6000L && worldTime < 10000L) {
      targetAge = 3;
    } 
    int validCurrentAge = targetAge - 1;
    if (validCurrentAge < 0)
      validCurrentAge = getMaxAge(); 
    int currentAge = ((Integer)state.getValue((IProperty)AGE)).intValue();
    if (currentAge == validCurrentAge) {
      List<Point> pointsToTest = new ArrayList<>();
      pointsToTest.add(new Point(pos));
      int count = 0;
      while (!pointsToTest.isEmpty() && count < 10000) {
        Point p = pointsToTest.get(pointsToTest.size() - 1);
        IBlockState bs = p.getBlockActualState(worldIn);
        if (bs.getBlock() == this && ((Integer)bs.getValue((IProperty)AGE)).intValue() == validCurrentAge) {
          p.setBlockState(worldIn, bs.withProperty((IProperty)AGE, Integer.valueOf(targetAge)));
          for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
              for (int dz = -1; dz < 2; dz++)
                pointsToTest.add(p.getRelative(dx, dy, dz)); 
            } 
          } 
        } 
        pointsToTest.remove(p);
        count++;
      } 
    } 
  }
  
  public IBlockState withAge(int age) {
    return getDefaultState().withProperty((IProperty)getAgeProperty(), Integer.valueOf(age));
  }
}
