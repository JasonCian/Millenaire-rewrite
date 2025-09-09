package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotFirePitFuel extends SlotItemHandler {
  public SlotFirePitFuel(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }
  
  public int getItemStackLimit(@Nonnull ItemStack stack) {
    return SlotFurnaceFuel.isBucket(stack) ? 1 : super.getItemStackLimit(stack);
  }
  
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return (TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack));
  }
}
