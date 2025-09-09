package org.millenaire.client.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.gui.GuiFirePit;
import org.millenaire.client.gui.GuiLockedChest;
import org.millenaire.client.gui.GuiPujas;
import org.millenaire.client.gui.GuiTrade;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.forge.ServerGuiHandler;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@SideOnly(Side.CLIENT)
public class ClientGuiHandler extends ServerGuiHandler {
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == 1) {
      TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
      if (te != null && te instanceof org.millenaire.common.entity.TileEntityLockedChest)
        return GuiLockedChest.createGUI(world, x, y, z, player); 
    } else if (ID == 2) {
      Building building = Mill.clientWorld.getBuilding(new Point(x, y, z));
      if (building != null && building.getTownHall() != null)
        return new GuiTrade(player, building); 
    } else if (ID == 8) {
      long id = MillCommonUtilities.unpackLong(x, y);
      if (Mill.clientWorld.getVillagerById(id) != null)
        return new GuiTrade(player, Mill.clientWorld.getVillagerById(id)); 
      MillLog.error(player, "Failed to find merchant: " + id);
    } else if (ID == 6) {
      Building building = Mill.clientWorld.getBuilding(new Point(x, y, z));
      if (building != null && building.pujas != null)
        return new GuiPujas(player, building); 
    } else if (ID == 16) {
      TileEntity at = world.getTileEntity(new BlockPos(x, y, z));
      if (at instanceof TileEntityFirePit)
        return new GuiFirePit(player, (TileEntityFirePit)at); 
    } 
    return null;
  }
}
