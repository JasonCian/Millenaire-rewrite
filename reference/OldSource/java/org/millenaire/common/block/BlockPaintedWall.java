package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;

public class BlockPaintedWall extends BlockMillWall implements IPaintedBlock {
  private final String baseBlockName;
  
  private final EnumDyeColor colour;
  
  public BlockPaintedWall(String baseBlockName, Block baseBlock, EnumDyeColor colour) {
    super(baseBlockName + "_" + colour.getName(), baseBlock);
    this.baseBlockName = baseBlockName;
    this.colour = colour;
    setHarvestLevel("pickaxe", 0);
    this.useNeighborBrightness = true;
  }
  
  public String getBlockType() {
    return this.baseBlockName;
  }
  
  public EnumDyeColor getDyeColour() {
    return this.colour;
  }
}
