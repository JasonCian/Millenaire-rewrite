package org.millenaire.common.pathing.atomicstryker;

public class AStarConfig {
  public boolean canUseDoors = false;
  
  public boolean canTakeDiagonals = false;
  
  public boolean allowDropping = false;
  
  public boolean canSwim = false;
  
  public boolean canClearLeaves = true;
  
  public boolean tolerance = false;
  
  public int toleranceHorizontal = 0;
  
  public int toleranceVertical = 0;
  
  public AStarConfig(boolean canUseDoors, boolean makePathDiagonals, boolean allowDropping, boolean canSwim, boolean canClearLeaves) {
    this.canUseDoors = canUseDoors;
    this.canTakeDiagonals = makePathDiagonals;
    this.allowDropping = allowDropping;
    this.canSwim = canSwim;
    this.canClearLeaves = canClearLeaves;
  }
  
  public AStarConfig(boolean canUseDoors, boolean makePathDiagonals, boolean allowDropping, boolean canSwim, boolean canClearLeaves, int toleranceHorizontal, int toleranceVertical) {
    this.canUseDoors = canUseDoors;
    this.canTakeDiagonals = makePathDiagonals;
    this.allowDropping = allowDropping;
    this.toleranceHorizontal = toleranceHorizontal;
    this.toleranceVertical = toleranceVertical;
    this.canSwim = canSwim;
    this.canClearLeaves = canClearLeaves;
    this.tolerance = true;
  }
}
