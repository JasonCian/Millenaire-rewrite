package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.millenaire.common.entity.TileEntityFirePit;

public class SlotFirePitInput extends SlotItemHandler {
  public SlotFirePitInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }
  
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return TileEntityFirePit.isFirePitBurnable(stack);
  }
}
