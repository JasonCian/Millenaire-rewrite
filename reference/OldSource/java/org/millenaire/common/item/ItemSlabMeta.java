package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

public class ItemSlabMeta extends ItemSlab {
  public ItemSlabMeta(BlockSlab halfBlock, BlockSlab fullBlock) {
    super((Block)halfBlock, halfBlock, fullBlock);
    if (!(this.block instanceof IMetaBlockName))
      throw new IllegalArgumentException(String.format("The given Block %s is not an instance of IMetaBlockName!", new Object[] { this.block
              .getUnlocalizedName() })); 
    setMaxDamage(0);
    setHasSubtypes(true);
  }
  
  public int getMetadata(int damage) {
    return damage;
  }
  
  public String getTranslationKey(ItemStack stack) {
    return ((IMetaBlockName)this.block).getSpecialName(stack);
  }
}
