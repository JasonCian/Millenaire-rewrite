package org.millenaire.common.block;

import net.minecraft.block.properties.PropertyBool;

public interface IBlockPath {
  public static final PropertyBool STABLE = PropertyBool.create("stable");
  
  BlockPath getDoubleSlab();
  
  BlockPathSlab getSingleSlab();
  
  boolean isFullPath();
}
