package org.millenaire.common.block;

import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOrientedSlabDoubleDecorated extends BlockOrientedSlab {
  public BlockOrientedSlabDoubleDecorated(String slabName) {
    super(slabName);
  }
  
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }
  
  public boolean isDouble() {
    return true;
  }
}
