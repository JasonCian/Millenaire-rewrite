package org.millenaire.client.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.forge.Mill;

@SideOnly(Side.CLIENT)
public class ClientTickHandler {
  private boolean startupMessageShow;
  
  @SubscribeEvent
  public void tickStart(TickEvent.ClientTickEvent event) {
    if (Mill.clientWorld == null || !Mill.clientWorld.millenaireEnabled || (Minecraft.getMinecraft()).player == null)
      return; 
    boolean inOverworld = ((Minecraft.getMinecraft()).player.dimension == 0);
    Mill.clientWorld.updateWorldClient(inOverworld);
    if (!this.startupMessageShow) {
      DisplayActions.displayStartupOrError((EntityPlayer)(Minecraft.getMinecraft()).player, Mill.startupError);
      this.startupMessageShow = true;
    } 
    Mill.proxy.handleClientGameUpdate();
  }
}
