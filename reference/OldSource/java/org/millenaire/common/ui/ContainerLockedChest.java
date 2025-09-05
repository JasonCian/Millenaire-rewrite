package org.millenaire.common.ui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.millenaire.common.village.Building;

public class ContainerLockedChest extends Container {
  private final IInventory lowerChestInventory;
  
  private final int numRows;
  
  private final boolean locked;
  
  public static class CachedSlot extends Slot {
    final Building building;
    
    public CachedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, Building building) {
      super(inventoryIn, index, xPosition, yPosition);
      this.building = building;
    }
    
    public void onSlotChanged() {
      super.onSlotChanged();
      if (this.building != null)
        this.building.invalidateInventoryCache(); 
    }
  }
  
  public static class LockedSlot extends Slot {
    public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }
    
    public boolean canTakeStack(EntityPlayer playerIn) {
      return false;
    }
  }
  
  public ContainerLockedChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player, Building building, boolean locked) {
    this.locked = locked;
    this.lowerChestInventory = chestInventory;
    this.numRows = chestInventory.getSizeInventory() / 9;
    chestInventory.openInventory(player);
    int i = (this.numRows - 4) * 18;
    for (int j = 0; j < this.numRows; j++) {
      for (int k = 0; k < 9; k++) {
        if (locked) {
          addSlotToContainer(new LockedSlot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
        } else {
          addSlotToContainer(new CachedSlot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18, building));
        } 
      } 
    } 
    for (int l = 0; l < 3; l++) {
      for (int j1 = 0; j1 < 9; j1++)
        addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i)); 
    } 
    for (int i1 = 0; i1 < 9; i1++)
      addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i)); 
  }
  
  public boolean canInteractWith(EntityPlayer playerIn) {
    return this.lowerChestInventory.isUsableByPlayer(playerIn);
  }
  
  public IInventory getLowerChestInventory() {
    return this.lowerChestInventory;
  }
  
  public void onContainerClosed(EntityPlayer playerIn) {
    super.onContainerClosed(playerIn);
    this.lowerChestInventory.closeInventory(playerIn);
  }
  
  public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
    if (slotId >= 0 && slotId < this.inventorySlots.size()) {
      Slot slot = this.inventorySlots.get(slotId);
      if (slot != null && slot instanceof LockedSlot && this.locked)
        return ItemStack.EMPTY; 
    } 
    return super.slotClick(slotId, dragType, clickTypeIn, player);
  }
  
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index < this.numRows * 9) {
        if (!mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
          return ItemStack.EMPTY; 
      } else if (!mergeItemStack(itemstack1, 0, this.numRows * 9, false)) {
        return ItemStack.EMPTY;
      } 
      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      } 
    } 
    return itemstack;
  }
}
