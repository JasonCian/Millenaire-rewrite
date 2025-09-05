package org.millenaire.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.block.BlockLockedChest;
import org.millenaire.common.entity.TileEntityLockedChest;

public class GuiLockedChest extends GuiChest {
  public static GuiLockedChest createGUI(World world, int i, int j, int k, EntityPlayer entityplayer) {
    TileEntityLockedChest lockedchest = (TileEntityLockedChest)world.getTileEntity(new BlockPos(i, j, k));
    if (lockedchest == null || (world.isRemote && !lockedchest.loaded))
      return null; 
    IInventory chest = BlockLockedChest.getInventory(lockedchest, world, i, j, k);
    return new GuiLockedChest(entityplayer, chest, lockedchest);
  }
  
  boolean locked = true;
  
  private GuiLockedChest(EntityPlayer player, IInventory iinventory1, TileEntityLockedChest lockedchest) {
    super((IInventory)player.inventory, iinventory1);
    this.locked = lockedchest.isLockedFor(player);
  }
  
  protected void keyTyped(char par1, int par2) throws IOException {
    if (!this.locked) {
      super.keyTyped(par1, par2);
    } else if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
      this.mc.player.closeScreen();
    } 
  }
  
  protected void mouseClicked(int i, int j, int k) throws IOException {
    if (!this.locked)
      super.mouseClicked(i, j, k); 
  }
}
