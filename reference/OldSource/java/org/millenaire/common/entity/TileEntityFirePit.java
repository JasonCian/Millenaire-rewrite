package org.millenaire.common.entity;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.millenaire.common.block.BlockFirePit;
import org.millenaire.common.block.MillBlocks;

public class TileEntityFirePit extends TileEntity implements ITickable {
  public static boolean isFirePitBurnable(ItemStack stack) {
    boolean food = stack.getItem() instanceof net.minecraft.item.ItemFood;
    ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
    return (!result.isEmpty() && (food || result.getItem() instanceof net.minecraft.item.ItemFood));
  }
  
  private final ItemStackHandler items = new ItemStackHandler(7) {
      protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        if ((0 <= slot && slot < 3 && !TileEntityFirePit.isFirePitBurnable(stack)) || (3 <= slot && slot < 4 && !TileEntityFurnace.isItemFuel(stack)) || (4 <= slot && slot < 7))
          return 0; 
        return super.getStackLimit(slot, stack);
      }
      
      protected void onContentsChanged(int slot) {
        TileEntityFirePit.this.markDirty();
        IBlockState state = TileEntityFirePit.this.world.getBlockState(TileEntityFirePit.this.pos);
        TileEntityFirePit.this.world.notifyBlockUpdate(TileEntityFirePit.this.pos, state, state, 18);
      }
    };
  
  public final IItemHandlerModifiable inputs = (IItemHandlerModifiable)new RangedWrapper((IItemHandlerModifiable)this.items, 0, 3);
  
  public final IItemHandlerModifiable fuel = (IItemHandlerModifiable)new RangedWrapper((IItemHandlerModifiable)this.items, 3, 4);
  
  public final IItemHandlerModifiable outputs = (IItemHandlerModifiable)new RangedWrapper((IItemHandlerModifiable)this.items, 4, 7);
  
  private int[] cookTimes = new int[3];
  
  private int burnTime = 0;
  
  private int totalBurnTime = 0;
  
  private boolean canSmelt(int idx) {
    ItemStack stack = this.inputs.getStackInSlot(idx);
    if (stack.isEmpty())
      return false; 
    ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
    if (result.isEmpty())
      return false; 
    ItemStack output = this.outputs.getStackInSlot(idx);
    return (output.isEmpty() || (ItemHandlerHelper.canItemStacksStack(result, output) && output.getCount() + result.getCount() <= result.getMaxStackSize()));
  }
  
  public void dropAll() {
    for (int i = 0; i < this.items.getSlots(); i++) {
      ItemStack stack = this.items.getStackInSlot(i);
      if (!stack.isEmpty())
        InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), stack); 
    } 
  }
  
  public int getBurnTime() {
    return this.burnTime;
  }
  
  @Nullable
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      if (facing == null)
        return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.items); 
      if (facing == EnumFacing.UP)
        return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inputs); 
      if (facing == EnumFacing.DOWN)
        return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.outputs); 
      return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.fuel);
    } 
    return (T)super.getCapability(capability, facing);
  }
  
  public int getCookTime(int idx) {
    return this.cookTimes[idx];
  }
  
  public int getTotalBurnTime() {
    return this.totalBurnTime;
  }
  
  @Nullable
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.pos, -1, getUpdateTag());
  }
  
  @Nonnull
  public NBTTagCompound getUpdateTag() {
    return serializeNBT();
  }
  
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing));
  }
  
  @SideOnly(Side.CLIENT)
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    handleUpdateTag(pkt.getNbtCompound());
  }
  
  public void readFromNBT(NBTTagCompound compound) {
    NBTTagCompound inventory = compound.getCompoundTag("Inventory");
    inventory.removeTag("Size");
    this.items.deserializeNBT(inventory);
    this.burnTime = compound.getInteger("BurnTime");
    this.cookTimes = Arrays.copyOf(compound.getIntArray("CookTime"), 3);
    this.totalBurnTime = compound.getInteger("TotalBurnTime");
    super.readFromNBT(compound);
  }
  
  public void setBurnTime(int burnTime) {
    this.burnTime = burnTime;
  }
  
  public void setCookTime(int idx, int cookTime) {
    this.cookTimes[idx] = cookTime;
  }
  
  public void setTotalBurnTime(int totalBurnTime) {
    this.totalBurnTime = totalBurnTime;
  }
  
  public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
    return (oldState.getBlock() != newState.getBlock());
  }
  
  public void smeltItem(int idx) {
    if (canSmelt(idx)) {
      ItemStack input = this.inputs.getStackInSlot(idx);
      ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
      ItemStack output = this.outputs.getStackInSlot(idx);
      if (output.isEmpty()) {
        this.outputs.setStackInSlot(idx, result.copy());
      } else {
        output.grow(result.getCount());
      } 
      input.shrink(1);
    } 
  }
  
  public void update() {
    boolean burning = (this.burnTime > 0);
    boolean dirty = false;
    if (burning)
      this.burnTime--; 
    if (!this.world.isRemote) {
      ItemStack fuelStack = this.fuel.getStackInSlot(0);
      for (int i = 0; i < 3; i++) {
        ItemStack inputStack = this.inputs.getStackInSlot(i);
        if ((this.burnTime > 0 || !fuelStack.isEmpty()) && !inputStack.isEmpty()) {
          if (this.burnTime <= 0 && canSmelt(i)) {
            this.burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
            this.totalBurnTime = this.burnTime;
            if (this.burnTime > 0) {
              dirty = true;
              if (!fuelStack.isEmpty()) {
                fuelStack.shrink(1);
                if (fuelStack.isEmpty())
                  this.fuel.setStackInSlot(0, fuelStack.getItem().getContainerItem(fuelStack)); 
              } 
            } 
          } 
          if (this.burnTime > 0 && canSmelt(i)) {
            this.cookTimes[i] = this.cookTimes[i] + 1;
            if (this.cookTimes[i] == 200) {
              this.cookTimes[i] = 0;
              smeltItem(i);
              dirty = true;
            } 
          } else {
            this.cookTimes[i] = 0;
          } 
        } else if (this.burnTime <= 0 && this.cookTimes[i] > 0) {
          dirty = true;
          this.cookTimes[i] = MathHelper.clamp(this.cookTimes[i] - 2, 0, 200);
        } 
      } 
      if (burning != ((this.burnTime > 0))) {
        dirty = true;
        IBlockState state = this.world.getBlockState(this.pos);
        if (!(state.getBlock() instanceof BlockFirePit))
          state = MillBlocks.FIRE_PIT.getDefaultState(); 
        this.world.setBlockState(this.pos, state.withProperty((IProperty)BlockFirePit.LIT, Boolean.valueOf((this.burnTime > 0))));
      } 
    } 
    if (dirty)
      markDirty(); 
  }
  
  @Nonnull
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    NBTTagCompound inventory = this.items.serializeNBT();
    inventory.removeTag("Size");
    compound.setTag("Inventory", (NBTBase)inventory);
    compound.setInteger("BurnTime", this.burnTime);
    compound.setIntArray("CookTime", this.cookTimes);
    compound.setInteger("TotalBurnTime", this.totalBurnTime);
    return super.writeToNBT(compound);
  }
}
