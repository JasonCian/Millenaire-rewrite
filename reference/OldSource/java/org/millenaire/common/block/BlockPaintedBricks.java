package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.item.ItemPaintBucket;
import org.millenaire.common.utilities.Point;

public class BlockPaintedBricks extends Block implements IPaintedBlock {
  public static final PropertyBool TOP_FRIEZE = PropertyBool.create("top_frieze");
  
  public static final PropertyBool BOTTOM_FRIEZE = PropertyBool.create("bottom_frieze");
  
  private final String blockName;
  
  private final String baseBlockName;
  
  private final EnumDyeColor colour;
  
  public static IBlockState getBlockStateWithColour(IBlockState input, EnumDyeColor colour) {
    IPaintedBlock paintedBlock = (IPaintedBlock)input.getBlock();
    Block newBlock = (Block)((Map)MillBlocks.PAINTED_BRICK_MAP.get(paintedBlock.getBlockType())).get(colour);
    return newBlock.getStateFromMeta(input.getBlock().getMetaFromState(input));
  }
  
  public static String getColorName(EnumDyeColor colour) {
    String colourName = colour.getName();
    if (colourName.equalsIgnoreCase("lightBlue"))
      colourName = "light_blue"; 
    return colourName;
  }
  
  public static EnumDyeColor getColourFromBlockState(IBlockState bs) {
    if (bs.getBlock() instanceof IPaintedBlock)
      return ((IPaintedBlock)bs.getBlock()).getDyeColour(); 
    return null;
  }
  
  public BlockPaintedBricks(String baseBlockName, EnumDyeColor colour) {
    super(Material.ROCK);
    this.baseBlockName = baseBlockName;
    String colourName = getColorName(colour);
    this.blockName = baseBlockName + "_" + colourName;
    this.colour = colour;
    setUnlocalizedName("millenaire." + this.blockName);
    setRegistryName(this.blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setHarvestLevel("pickaxe", 0);
    setHardness(1.5F);
    setResistance(10.0F);
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)TOP_FRIEZE, (IProperty)BOTTOM_FRIEZE });
  }
  
  public int friezePriority(IBlockAccess worldIn, BlockPos pos, IBlockState ourState, IBlockState otherState, EnumFacing side) {
    if (getColourFromBlockState(otherState) == this.colour) {
      if (otherState.isSideSolid(worldIn, pos, side))
        return 0; 
      return 3;
    } 
    if (otherState.getCollisionBoundingBox(worldIn, pos) == NULL_AABB)
      return 5; 
    if (otherState.getBlock() instanceof net.minecraft.block.BlockPane)
      return 2; 
    if (otherState.getBlock() instanceof IPaintedBlock)
      return 1; 
    return 10;
  }
  
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    int topPriority = friezePriority(worldIn, pos.up(), state, worldIn.getBlockState(pos.up()), EnumFacing.DOWN);
    int bottomPriority = friezePriority(worldIn, pos.down(), state, worldIn.getBlockState(pos.down()), EnumFacing.UP);
    if (topPriority > bottomPriority)
      return state.withProperty((IProperty)TOP_FRIEZE, Boolean.valueOf(true)).withProperty((IProperty)BOTTOM_FRIEZE, Boolean.valueOf(false)); 
    if (bottomPriority > 0)
      return state.withProperty((IProperty)BOTTOM_FRIEZE, Boolean.valueOf(true)).withProperty((IProperty)TOP_FRIEZE, Boolean.valueOf(false)); 
    return state.withProperty((IProperty)BOTTOM_FRIEZE, Boolean.valueOf(false)).withProperty((IProperty)TOP_FRIEZE, Boolean.valueOf(false));
  }
  
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }
  
  public String getBlockType() {
    return this.baseBlockName;
  }
  
  public EnumDyeColor getDyeColour() {
    return this.colour;
  }
  
  public int getMetaFromState(IBlockState state) {
    return 0;
  }
  
  public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    return getStateFromMeta(meta);
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() instanceof ItemPaintBucket) {
      ItemStack bucket = player.inventory.getCurrentItem();
      EnumDyeColor targetColor = ((ItemPaintBucket)player.inventory.getCurrentItem().getItem()).getColour();
      if (targetColor != null)
        if (this.colour != targetColor) {
          List<Point> pointsToTest = new ArrayList<>();
          pointsToTest.add(new Point(pos));
          int blockColoured = 0;
          while (!pointsToTest.isEmpty()) {
            Point p = pointsToTest.get(pointsToTest.size() - 1);
            IBlockState bs = p.getBlockActualState(worldIn);
            if (getColourFromBlockState(bs) == this.colour) {
              p.setBlockState(worldIn, getBlockStateWithColour(bs, targetColor));
              blockColoured++;
              pointsToTest.add(p.getAbove());
              pointsToTest.add(p.getBelow());
              pointsToTest.add(p.getNorth());
              pointsToTest.add(p.getEast());
              pointsToTest.add(p.getSouth());
              pointsToTest.add(p.getWest());
            } 
            pointsToTest.remove(p);
          } 
          if (blockColoured < bucket.getMaxDamage() - bucket.getItemDamage()) {
            bucket.damageItem(blockColoured, (EntityLivingBase)player);
          } else {
            player.inventory.removeStackFromSlot(player.inventory.currentItem);
            player.inventory.add(player.inventory.currentItem, new ItemStack(Items.BUCKET));
          } 
          MillAdvancements.RAINBOW.grant(player);
          return true;
        }  
    } 
    return false;
  }
}
