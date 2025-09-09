package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.millenaire.common.entity.TileEntityFirePit;

public class ContainerFirePit extends Container {
  private static final int[][] INPUT_POSITIONS = new int[][] { { 56, 8 }, { 44, 28 }, { 56, 48 } };
  
  private static final int[][] OUTPUT_POSITIONS = new int[][] { { 104, 8 }, { 116, 28 }, { 104, 48 } };
  
  private static final int[] FUEL_POSITION = new int[] { 80, 70 };
  
  private static final int[] INV_POSITION = new int[] { 8, 93 };
  
  private final int inputStart;
  
  private final int inputEnd;
  
  private final int fuelStart;
  
  private final int fuelEnd;
  
  private final int outputStart;
  
  private final int outputEnd;
  
  private final int inventoryStart;
  
  private final int inventoryEnd;
  
  private final int hotbarStart;
  
  private final int hotbarEnd;
  
  private final TileEntityFirePit firePit;
  
  private static boolean inRange(int index, int start, int end) {
    return (start <= index && index < end);
  }
  
  public ContainerFirePit(EntityPlayer player, TileEntityFirePit firePit) {
    this.firePit = firePit;
    InventoryPlayer playerInventory = player.inventory;
    this.inputStart = this.inventorySlots.size();
    int i;
    for (i = 0; i < 3; i++)
      addSlotToContainer((Slot)new SlotFirePitInput((IItemHandler)firePit.inputs, i, INPUT_POSITIONS[i][0], INPUT_POSITIONS[i][1])); 
    this.inputEnd = this.inventorySlots.size();
    this.fuelStart = this.inventorySlots.size();
    addSlotToContainer((Slot)new SlotFirePitFuel((IItemHandler)firePit.fuel, 0, FUEL_POSITION[0], FUEL_POSITION[1]));
    this.fuelEnd = this.inventorySlots.size();
    this.outputStart = this.inventorySlots.size();
    for (i = 0; i < 3; i++)
      addSlotToContainer((Slot)new SlotFirePitOutput(player, (IItemHandler)firePit.outputs, i, OUTPUT_POSITIONS[i][0], OUTPUT_POSITIONS[i][1])); 
    this.outputEnd = this.inventorySlots.size();
    this.inventoryStart = this.inventorySlots.size();
    for (int row = 0; row < 3; row++) {
      for (int column = 0; column < 9; column++)
        addSlotToContainer(new Slot((IInventory)playerInventory, column + row * 9 + 9, INV_POSITION[0] + column * 18, INV_POSITION[1] + row * 18)); 
    } 
    this.inventoryEnd = this.inventorySlots.size();
    this.hotbarStart = this.inventorySlots.size();
    for (int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++)
      addSlotToContainer(new Slot((IInventory)playerInventory, hotbarIndex, INV_POSITION[0] + hotbarIndex * 18, INV_POSITION[1] + 54 + 4)); 
    this.hotbarEnd = this.inventorySlots.size();
  }
  
  public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
    return true;
  }
  
  public void detectAndSendChanges() {
    super.detectAndSendChanges();
    for (IContainerListener listener : this.listeners) {
      listener.sendWindowProperty(this, 0, this.firePit.getCookTime(0));
      listener.sendWindowProperty(this, 1, this.firePit.getCookTime(1));
      listener.sendWindowProperty(this, 2, this.firePit.getCookTime(2));
      listener.sendWindowProperty(this, 3, this.firePit.getBurnTime());
      listener.sendWindowProperty(this, 4, this.firePit.getTotalBurnTime());
    } 
  }
  
  @Nonnull
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
    ItemStack original = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack stackInSlot = slot.getStack();
      original = stackInSlot.copy();
      if (inRange(index, this.outputStart, this.outputEnd)) {
        if (!mergeItemStack(stackInSlot, this.inventoryStart, this.hotbarEnd, true))
          return ItemStack.EMPTY; 
        slot.onSlotChange(stackInSlot, original);
      } else if (inRange(index, this.inventoryStart, this.hotbarEnd)) {
        if (TileEntityFirePit.isFirePitBurnable(stackInSlot)) {
          if (!mergeItemStack(stackInSlot, this.inputStart, this.inputEnd, false))
            return ItemStack.EMPTY; 
        } else if (TileEntityFurnace.isItemFuel(stackInSlot)) {
          if (!mergeItemStack(stackInSlot, this.fuelStart, this.fuelEnd, false))
            return ItemStack.EMPTY; 
        } else if (inRange(index, this.inventoryStart, this.inventoryEnd)) {
          if (!mergeItemStack(stackInSlot, this.hotbarStart, this.hotbarEnd, false))
            return ItemStack.EMPTY; 
        } else if (!mergeItemStack(stackInSlot, this.inventoryStart, this.inventoryStart, false)) {
          return ItemStack.EMPTY;
        } 
      } else if (!mergeItemStack(stackInSlot, this.inventoryStart, this.hotbarEnd, false)) {
        return ItemStack.EMPTY;
      } 
      if (stackInSlot.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      } 
      if (stackInSlot.getCount() == original.getCount())
        return ItemStack.EMPTY; 
      slot.onTake(playerIn, stackInSlot);
    } 
    return original;
  }
  
  @SideOnly(Side.CLIENT)
  public void updateProgressBar(int id, int data) {
    switch (id) {
      case 0:
      case 1:
      case 2:
        this.firePit.setCookTime(id, data);
        break;
      case 3:
        this.firePit.setBurnTime(data);
        break;
      case 4:
        this.firePit.setTotalBurnTime(data);
        break;
    } 
  }
}
