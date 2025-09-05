package org.millenaire.common.forge;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class BuildingChunkLoader {
  Building townHall;
  
  public static class ChunkLoaderCallback implements ForgeChunkManager.LoadingCallback {
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
      for (ForgeChunkManager.Ticket ticket : tickets)
        ForgeChunkManager.releaseTicket(ticket); 
    }
  }
  
  List<ForgeChunkManager.Ticket> tickets = new ArrayList<>();
  
  public boolean chunksLoaded = false;
  
  public BuildingChunkLoader(Building th) {
    this.townHall = th;
  }
  
  private ForgeChunkManager.Ticket getTicket() {
    for (ForgeChunkManager.Ticket ticket1 : this.tickets) {
      if (ticket1.getChunkList().size() < ticket1.getChunkListDepth() - 1)
        return ticket1; 
    } 
    ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(Mill.instance, this.townHall.world, ForgeChunkManager.Type.NORMAL);
    if (ticket == null) {
      MillLog.warning(this.townHall, "Couldn't get ticket in BuildingChunkLoader. Your Forge chunk loading settings must be interfearing.");
      return null;
    } 
    this.tickets.add(ticket);
    return ticket;
  }
  
  public void loadChunks() {
    if (this.townHall.winfo != null) {
      int nbLoaded = 0;
      for (int cx = this.townHall.winfo.chunkStartX - 1; cx < this.townHall.winfo.chunkStartX + this.townHall.winfo.length / 16 + 1; 
        cx++) {
        for (int cz = this.townHall.winfo.chunkStartZ - 1; cz < this.townHall.winfo.chunkStartZ + this.townHall.winfo.width / 16 + 1; 
          cz++) {
          ForgeChunkManager.Ticket ticket = getTicket();
          if (ticket != null) {
            ChunkPos chunkCoords = new ChunkPos(cx, cz);
            ForgeChunkManager.forceChunk(ticket, chunkCoords);
            nbLoaded++;
          } 
        } 
      } 
      this.chunksLoaded = true;
      if (MillConfigValues.LogChunkLoader >= 1)
        MillLog.major(this.townHall, "Force-Loaded " + nbLoaded + " chunks."); 
    } 
  }
  
  public void unloadChunks() {
    for (ForgeChunkManager.Ticket ticket : this.tickets)
      ForgeChunkManager.releaseTicket(ticket); 
    this.tickets.clear();
    this.chunksLoaded = false;
    if (MillConfigValues.LogChunkLoader >= 1)
      MillLog.major(this.townHall, "Unloaded the chunks."); 
  }
}
