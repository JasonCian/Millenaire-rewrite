package org.millenaire.common.goal.generic;

import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericCooking extends GoalGeneric {
  public static final String GOAL_TYPE = "cooking";
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "The item to be cooked.")
  public InvItem itemToCook = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "16")
  @FieldDocumentation(explanation = "Minimum number of items that can be added to a cooking.")
  public int minimumToCook;
  
  public void applyDefaultSettings() {
    this.lookAtGoal = true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Building> buildings = getBuildings(villager);
    for (Building dest : buildings) {
      if (isDestPossible(villager, dest)) {
        int countGoods = dest.countGoods(this.itemToCook) + villager.countInv(this.itemToCook);
        for (Point p : (dest.getResManager()).furnaces) {
          TileEntityFurnace furnace = p.getFurnace(villager.world);
          if (furnace != null) {
            if (countGoods >= this.minimumToCook && (furnace.getStackInSlot(0) == ItemStack.EMPTY || furnace.getStackInSlot(0).getItem() == Items.AIR || (furnace
              .getStackInSlot(0).getItem() == this.itemToCook.getItem() && furnace.getStackInSlot(0).getItemDamage() == this.itemToCook.meta && furnace
              .getStackInSlot(0).getCount() < 32)))
              return packDest(p, dest); 
            if (furnace.getStackInSlot(2) != null && furnace.getStackInSlot(2).getCount() >= this.minimumToCook)
              return packDest(p, dest); 
          } 
        } 
        boolean firepitBurnable = TileEntityFirePit.isFirePitBurnable(this.itemToCook.staticStack);
        if (firepitBurnable)
          for (Point p : (dest.getResManager()).firepits) {
            TileEntityFirePit firepit = p.getFirePit(villager.world);
            if (firepit != null) {
              int slotNb;
              for (slotNb = 0; slotNb < 3; slotNb++) {
                ItemStack stack = firepit.inputs.getStackInSlot(slotNb);
                if (countGoods >= this.minimumToCook && (stack
                  .isEmpty() || (stack.getItem() == this.itemToCook.getItem() && stack.getItemDamage() == this.itemToCook.meta && stack.getCount() < 32)))
                  return packDest(p, dest); 
              } 
              for (slotNb = 0; slotNb < 3; slotNb++) {
                ItemStack stack = firepit.outputs.getStackInSlot(slotNb);
                if (stack != null && stack.getCount() >= this.minimumToCook)
                  return packDest(p, dest); 
              } 
            } 
          }  
      } 
    } 
    return null;
  }
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    if (this.itemToCook != null)
      return this.itemToCook.getItemStack(); 
    return null;
  }
  
  public String getTypeLabel() {
    return "cooking";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    if (getDestination(villager) == null)
      return false; 
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    TileEntity tileEntity = villager.getGoalDestPoint().getTileEntity(villager.world);
    Building dest = villager.getGoalBuildingDest();
    boolean firepitBurnable = TileEntityFirePit.isFirePitBurnable(this.itemToCook.staticStack);
    if (tileEntity != null && dest != null)
      if (tileEntity instanceof TileEntityFurnace) {
        TileEntityFurnace furnace = (TileEntityFurnace)tileEntity;
        performAction_furnace(dest, furnace, villager);
      } else if (firepitBurnable && tileEntity instanceof TileEntityFirePit) {
        TileEntityFirePit firepit = (TileEntityFirePit)tileEntity;
        performAction_firepit(dest, firepit, villager);
      }  
    return true;
  }
  
  private void performAction_firepit(Building dest, TileEntityFirePit firepit, MillVillager villager) {
    int slotNb;
    for (slotNb = 0; slotNb < 3; slotNb++) {
      ItemStack stack = firepit.inputs.getStackInSlot(slotNb);
      int countGoods = dest.countGoods(this.itemToCook) + villager.countInv(this.itemToCook);
      if ((stack.isEmpty() && countGoods >= this.minimumToCook) || (
        !stack.isEmpty() && stack.getItem() == this.itemToCook.getItem() && stack.getItemDamage() == this.itemToCook.meta && stack.getCount() < 64 && countGoods > 0))
        if (stack.isEmpty()) {
          int nb = Math.min(64, countGoods);
          firepit.inputs.setStackInSlot(slotNb, new ItemStack(this.itemToCook.getItem(), nb, this.itemToCook.meta));
          dest.takeGoods(this.itemToCook, nb);
        } else {
          int nb = Math.min(64 - stack.getCount(), countGoods);
          ItemStack newStack = stack.copy();
          newStack.setCount(stack.getCount() + nb);
          firepit.inputs.setStackInSlot(slotNb, newStack);
          dest.takeGoods(this.itemToCook, nb);
        }  
    } 
    for (slotNb = 0; slotNb < 3; slotNb++) {
      ItemStack stack = firepit.outputs.getStackInSlot(slotNb);
      if (!stack.isEmpty()) {
        Item item = stack.getItem();
        int meta = stack.getItemDamage();
        dest.storeGoods(item, meta, stack.getCount());
        firepit.outputs.setStackInSlot(slotNb, ItemStack.EMPTY);
      } 
    } 
  }
  
  private void performAction_furnace(Building dest, TileEntityFurnace furnace, MillVillager villager) {
    int countGoods = dest.countGoods(this.itemToCook) + villager.countInv(this.itemToCook);
    if ((furnace.getStackInSlot(0).isEmpty() && countGoods >= this.minimumToCook) || (!furnace.getStackInSlot(0).isEmpty() && furnace.getStackInSlot(0).getItem() == this.itemToCook.getItem() && furnace
      .getStackInSlot(0).getItemDamage() == this.itemToCook.meta && furnace.getStackInSlot(0).getCount() < 64 && countGoods > 0))
      if (furnace.getStackInSlot(0).isEmpty()) {
        int nb = Math.min(64, countGoods);
        furnace.setInventorySlotContents(0, new ItemStack(this.itemToCook.getItem(), nb, this.itemToCook.meta));
        dest.takeGoods(this.itemToCook, nb);
      } else {
        int nb = Math.min(64 - furnace.getStackInSlot(0).getCount(), countGoods);
        ItemStack stack = furnace.getStackInSlot(0);
        stack.setCount(furnace.getStackInSlot(0).getCount() + nb);
        furnace.setInventorySlotContents(0, stack);
        dest.takeGoods(this.itemToCook, nb);
      }  
    if (!furnace.getStackInSlot(2).isEmpty()) {
      Item item = furnace.getStackInSlot(2).getItem();
      int meta = furnace.getStackInSlot(2).getItemDamage();
      dest.storeGoods(item, meta, furnace.getStackInSlot(2).getCount());
      furnace.setInventorySlotContents(2, ItemStack.EMPTY);
    } 
  }
  
  public boolean validateGoal() {
    if (this.itemToCook == null) {
      MillLog.error(this, "The itemtocook id is mandatory in custom cooking goals.");
      return false;
    } 
    return true;
  }
}
