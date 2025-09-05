package org.millenaire.common.block;

import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPaintedSlab extends BlockHalfSlab implements IPaintedBlock {
  private final EnumDyeColor colour;
  
  private final String blockType;
  
  public BlockPaintedSlab(String blockType, Block baseBlock, EnumDyeColor colour) {
    super(baseBlock);
    this.blockType = blockType;
    this.colour = colour;
    setUnlocalizedName("millenaire." + blockType + "_" + colour.getName());
    setRegistryName(blockType + "_" + colour.getName());
    setHarvestLevel("pickaxe", 0);
    setHardness(1.5F);
    setResistance(10.0F);
  }
  
  public String getBlockType() {
    return this.blockType;
  }
  
  public EnumDyeColor getDyeColour() {
    return this.colour;
  }
  
  public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    return new ItemStack((Block)((Map)MillBlocks.PAINTED_BRICK_MAP.get(getBlockType())).get(this.colour));
  }
  
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    return Item.getItemFromBlock((Block)((Map)MillBlocks.PAINTED_BRICK_MAP.get(getBlockType())).get(this.colour));
  }
}
