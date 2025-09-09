package org.millenaire.common.block;

import net.minecraft.block.Block;

public class BlockMillSlab extends BlockHalfSlab {
  public BlockMillSlab(String name, Block baseBlock) {
    super(baseBlock);
    setUnlocalizedName("millenaire." + name);
    setRegistryName(name);
    setHarvestLevel("pickaxe", 0);
    setHardness(1.5F);
    setResistance(10.0F);
  }
}
