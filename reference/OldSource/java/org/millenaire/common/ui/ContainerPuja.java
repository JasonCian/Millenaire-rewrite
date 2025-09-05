package org.millenaire.common.ui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class ContainerPuja extends Container {
  PujaSacrifice shrine;
  
  ToolSlot slotTool;
  
  public static class MoneySlot extends Slot {
    PujaSacrifice shrine;
    
    public MoneySlot(PujaSacrifice shrine, int par2, int par3, int par4) {
      super(shrine, par2, par3, par4);
      this.shrine = shrine;
    }
    
    public boolean isItemValid(ItemStack is) {
      return (is.getItem() == MillItems.DENIER || is.getItem() == MillItems.DENIER_OR || is.getItem() == MillItems.DENIER_ARGENT);
    }
    
    public void onSlotChanged() {
      if (!this.shrine.temple.world.isRemote)
        this.shrine.temple.getTownHall().requestSave("Puja money slot changed"); 
      super.onSlotChanged();
    }
  }
  
  public static class OfferingSlot extends Slot {
    PujaSacrifice shrine;
    
    public OfferingSlot(PujaSacrifice shrine, int par2, int par3, int par4) {
      super(shrine, par2, par3, par4);
      this.shrine = shrine;
    }
    
    public boolean isItemValid(ItemStack par1ItemStack) {
      return (this.shrine.getOfferingValue(par1ItemStack) > 0);
    }
    
    public void onSlotChanged() {
      if (!this.shrine.temple.world.isRemote)
        this.shrine.temple.getTownHall().requestSave("Puja offering slot changed"); 
      super.onSlotChanged();
    }
  }
  
  public static class ToolSlot extends Slot {
    PujaSacrifice shrine;
    
    public ToolSlot(PujaSacrifice shrine, int par2, int par3, int par4) {
      super(shrine, par2, par3, par4);
      this.shrine = shrine;
    }
    
    public boolean isItemValid(ItemStack is) {
      Item item = is.getItem();
      if (this.shrine.type == 1)
        return (item instanceof net.minecraft.item.ItemSword || item instanceof net.minecraft.item.ItemArmor || item instanceof net.minecraft.item.ItemBow || item instanceof net.minecraft.item.ItemAxe); 
      return (item instanceof net.minecraft.item.ItemSpade || item instanceof net.minecraft.item.ItemAxe || item instanceof net.minecraft.item.ItemPickaxe);
    }
    
    public void onSlotChanged() {
      this.shrine.calculateOfferingsNeeded();
      if (!this.shrine.temple.world.isRemote)
        this.shrine.temple.getTownHall().requestSave("Puja tool slot changed"); 
      super.onSlotChanged();
    }
  }
  
  public ContainerPuja(EntityPlayer player, Building temple) {
    try {
      this.shrine = temple.pujas;
      this.slotTool = new ToolSlot(temple.pujas, 4, 86, 37);
      addSlotToContainer(new OfferingSlot(temple.pujas, 0, 26, 19));
      addSlotToContainer(new MoneySlot(temple.pujas, 1, 8, 55));
      addSlotToContainer(new MoneySlot(temple.pujas, 2, 26, 55));
      addSlotToContainer(new MoneySlot(temple.pujas, 3, 44, 55));
      addSlotToContainer(this.slotTool);
      for (int i = 0; i < 3; i++) {
        for (int k = 0; k < 9; k++)
          addSlotToContainer(new Slot((IInventory)player.inventory, k + i * 9 + 9, 8 + k * 18, 106 + i * 18)); 
      } 
      for (int j = 0; j < 9; j++)
        addSlotToContainer(new Slot((IInventory)player.inventory, j, 8 + j * 18, 164)); 
    } catch (Exception e) {
      MillLog.printException("Exception in ContainerPuja(): ", e);
    } 
  }
  
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return true;
  }
  
  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int stackID) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(stackID);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (stackID == 4) {
        if (!mergeItemStack(itemstack1, 5, 41, true))
          return ItemStack.EMPTY; 
        slot.onSlotChange(itemstack1, itemstack);
      } else if (stackID > 4) {
        if (itemstack1.getItem() == MillItems.DENIER || itemstack1.getItem() == MillItems.DENIER_ARGENT || itemstack1.getItem() == MillItems.DENIER_OR) {
          if (!mergeItemStack(itemstack1, 1, 4, false))
            return ItemStack.EMPTY; 
        } else if (this.shrine.getOfferingValue(itemstack1) > 0) {
          if (!mergeItemStack(itemstack1, 0, 1, false))
            return ItemStack.EMPTY; 
        } else if (this.slotTool.isItemValid(itemstack1)) {
          if (!mergeItemStack(itemstack1, 4, 5, false))
            return ItemStack.EMPTY; 
        } else {
          return ItemStack.EMPTY;
        } 
      } else if (!mergeItemStack(itemstack1, 5, 41, false)) {
        return ItemStack.EMPTY;
      } 
      if (itemstack1.getCount() == 0) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      } 
      if (itemstack1.getCount() == itemstack.getCount())
        return ItemStack.EMPTY; 
      slot.onTake(par1EntityPlayer, itemstack1);
    } 
    return itemstack;
  }
}
