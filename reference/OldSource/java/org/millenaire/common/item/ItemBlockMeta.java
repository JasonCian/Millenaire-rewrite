package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock {
  public ItemBlockMeta(Block block) {
    super(block);
    if (!(block instanceof IMetaBlockName))
      throw new IllegalArgumentException(String.format("The given Block %s is not an instance of IMetaBlockName!", new Object[] { block
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
