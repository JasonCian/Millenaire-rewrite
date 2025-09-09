package org.millenaire.common.forge;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.millenaire.common.world.MillWorldData;

public class ServerTickHandler {
  @SubscribeEvent
  public void tickStart(TickEvent.ServerTickEvent event) {
    if (Mill.startupError)
      return; 
    List<MillWorldData> serversCopy = new ArrayList<>(Mill.serverWorlds);
    for (MillWorldData mw : serversCopy)
      mw.updateWorldServer(); 
  }
}
