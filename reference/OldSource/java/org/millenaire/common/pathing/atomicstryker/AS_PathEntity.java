package org.millenaire.common.pathing.atomicstryker;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;

public class AS_PathEntity extends Path {
  private long timeLastPathIncrement = 0L;
  
  public final PathPoint[] pointsCopy;
  
  private int pathIndexCopy;
  
  public AS_PathEntity(PathPoint[] points) {
    super(points);
    this.timeLastPathIncrement = System.currentTimeMillis();
    this.pointsCopy = points;
    this.pathIndexCopy = 0;
  }
  
  public void advancePathIndex() {
    this.timeLastPathIncrement = System.currentTimeMillis();
    this.pathIndexCopy++;
    setCurrentPathIndex(this.pathIndexCopy);
  }
  
  public PathPoint getCurrentTargetPathPoint() {
    if (isFinished())
      return null; 
    return this.pointsCopy[getCurrentPathIndex()];
  }
  
  public PathPoint getFuturePathPoint(int jump) {
    if (getCurrentPathIndex() >= this.pointsCopy.length - jump)
      return null; 
    return this.pointsCopy[getCurrentPathIndex() + jump];
  }
  
  public PathPoint getNextTargetPathPoint() {
    if (getCurrentPathIndex() >= this.pointsCopy.length - 1)
      return null; 
    return this.pointsCopy[getCurrentPathIndex() + 1];
  }
  
  public PathPoint getPastTargetPathPoint(int jump) {
    if (getCurrentPathIndex() < jump || this.pointsCopy.length == 0)
      return null; 
    return this.pointsCopy[getCurrentPathIndex() - jump];
  }
  
  public Vec3d getPosition(Entity var1) {
    if (isFinished())
      return null; 
    return super.getPosition(var1);
  }
  
  public PathPoint getPreviousTargetPathPoint() {
    if (getCurrentPathIndex() < 1 || this.pointsCopy.length == 0)
      return null; 
    return this.pointsCopy[getCurrentPathIndex() - 1];
  }
  
  public long getTimeSinceLastPathIncrement() {
    return System.currentTimeMillis() - this.timeLastPathIncrement;
  }
  
  public void setCurrentPathIndex(int par1) {
    this.timeLastPathIncrement = System.currentTimeMillis();
    this.pathIndexCopy = par1;
    super.setCurrentPathIndex(par1);
  }
}
