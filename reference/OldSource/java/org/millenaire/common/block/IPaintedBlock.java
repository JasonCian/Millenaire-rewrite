package org.millenaire.common.block;

import net.minecraft.item.EnumDyeColor;

public interface IPaintedBlock {
  String getBlockType();
  
  EnumDyeColor getDyeColour();
  
  void initModel();
}
