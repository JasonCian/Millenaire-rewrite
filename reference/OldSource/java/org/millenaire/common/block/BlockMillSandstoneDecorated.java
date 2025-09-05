package org.millenaire.common.block;

import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillSandstoneDecorated extends BlockMillSandstone {
  public BlockMillSandstoneDecorated(String blockName) {
    super(blockName);
  }
  
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT;
  }
}
