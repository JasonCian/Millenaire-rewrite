package org.millenaire.common.quest;

import net.minecraft.world.World;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;

public class QuestInstanceVillager {
  public long id;
  
  public Point townHall;
  
  private MillVillager villager = null;
  
  private VillagerRecord vr = null;
  
  public MillWorldData mw;
  
  public QuestInstanceVillager(MillWorldData mw, Point p, long vid) {
    this.townHall = p;
    this.id = vid;
    this.mw = mw;
  }
  
  public QuestInstanceVillager(MillWorldData mw, Point p, long vid, VillagerRecord v) {
    this.townHall = p;
    this.id = vid;
    this.vr = v;
    this.mw = mw;
  }
  
  public Building getTownHall(World world) {
    return this.mw.getBuilding(this.townHall);
  }
  
  public MillVillager getVillager(World world) {
    if (this.villager == null) {
      Building th = this.mw.getBuilding(this.townHall);
      if (th != null)
        this.villager = this.mw.getVillagerById(this.id); 
    } 
    return this.villager;
  }
  
  public VillagerRecord getVillagerRecord(World world) {
    if (this.vr == null)
      this.vr = this.mw.getVillagerRecordById(this.id); 
    return this.vr;
  }
}
