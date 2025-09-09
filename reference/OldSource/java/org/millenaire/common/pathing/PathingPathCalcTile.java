package org.millenaire.common.pathing;

public class PathingPathCalcTile {
  public boolean ladder;
  
  public boolean isWalkable;
  
  public short[] position;
  
  public PathingPathCalcTile(boolean walkable, boolean lad, short[] pos) {
    this.ladder = lad;
    if (this.ladder == true) {
      this.isWalkable = false;
    } else if (((!this.ladder ? 1 : 0) & ((walkable == true) ? 1 : 0)) != 0) {
      this.isWalkable = true;
    } 
    this.position = (short[])pos.clone();
  }
  
  public PathingPathCalcTile(PathingPathCalcTile c) {
    this.ladder = c.ladder;
    this.isWalkable = c.isWalkable;
    this.position = (short[])c.position.clone();
  }
}
