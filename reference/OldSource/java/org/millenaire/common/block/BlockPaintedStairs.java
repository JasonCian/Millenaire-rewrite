package org.millenaire.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;

public class BlockPaintedStairs extends BlockMillStairs implements IPaintedBlock {
  private final String baseBlockName;
  
  private final EnumDyeColor colour;
  
  public BlockPaintedStairs(String baseBlockName, IBlockState baseBlock, EnumDyeColor colour) {
    super(baseBlockName + "_" + colour.getName(), baseBlock);
    this.baseBlockName = baseBlockName;
    this.colour = colour;
  }
  
  public String getBlockType() {
    return this.baseBlockName;
  }
  
  public EnumDyeColor getDyeColour() {
    return this.colour;
  }
}
