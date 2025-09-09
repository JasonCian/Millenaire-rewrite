package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotFirePitOutput extends SlotItemHandler {
  private final EntityPlayer player;
  
  private int removeCount;
  
  public SlotFirePitOutput(EntityPlayer player, IItemHandler handler, int slotIndex, int xPosition, int yPosition) {
    super(handler, slotIndex, xPosition, yPosition);
    this.player = player;
  }
  
  @Nonnull
  public ItemStack decrStackSize(int amount) {
    if (getHasStack())
      this.removeCount += Math.min(amount, getStack().getCount()); 
    return super.decrStackSize(amount);
  }
  
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return false;
  }
  
  protected void onCrafting(ItemStack stack) {
    stack.onCrafting(this.player.world, this.player, this.removeCount);
    if (!this.player.world.isRemote) {
      int i = this.removeCount;
      float f = FurnaceRecipes.instance().getSmeltingExperience(stack);
      if (f == 0.0F) {
        i = 0;
      } else if (f < 1.0F) {
        int j = MathHelper.floor(i * f);
        if (j < MathHelper.ceil(i * f) && Math.random() < (i * f - j))
          j++; 
        i = j;
      } 
      while (i > 0) {
        int k = EntityXPOrb.getXPSplit(i);
        i -= k;
        this.player.world.spawnEntity((Entity)new EntityXPOrb(this.player.world, this.player.posX, this.player.posY + 0.5D, this.player.posZ + 0.5D, k));
      } 
    } 
    this.removeCount = 0;
    FMLCommonHandler.instance().firePlayerSmeltedEvent(this.player, stack);
  }
  
  protected void onCrafting(ItemStack stack, int amount) {
    this.removeCount += amount;
    onCrafting(stack);
  }
  
  @Nonnull
  public ItemStack onTake(EntityPlayer thePlayer, @Nonnull ItemStack stack) {
    onCrafting(stack);
    super.onTake(thePlayer, stack);
    return stack;
  }
}
