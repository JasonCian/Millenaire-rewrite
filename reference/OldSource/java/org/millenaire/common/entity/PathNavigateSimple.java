package org.millenaire.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PathNavigateSimple extends PathNavigate {
  public PathNavigateSimple(EntityLiving entityIn, World worldIn) {
    super(entityIn, worldIn);
  }
  
  protected boolean canNavigate() {
    return true;
  }
  
  private boolean getCanSwim() {
    return true;
  }
  
  protected Vec3d getEntityPosition() {
    return new Vec3d(this.entity.posX, getPathablePosY(), this.entity.posZ);
  }
  
  private int getPathablePosY() {
    if (this.entity.isInWater() && getCanSwim()) {
      int i = (int)(this.entity.getEntityBoundingBox()).minY;
      Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))).getBlock();
      int j = 0;
      while (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
        i++;
        block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ))).getBlock();
        j++;
        if (j > 16)
          return (int)(this.entity.getEntityBoundingBox()).minY; 
      } 
      return i;
    } 
    return (int)((this.entity.getEntityBoundingBox()).minY + 0.5D);
  }
  
  protected PathFinder getPathFinder() {
    this.nodeProcessor = (NodeProcessor)new WalkNodeProcessor();
    this.nodeProcessor.setCanEnterDoors(true);
    return null;
  }
  
  protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
    int i = MathHelper.floor(posVec31.x);
    int j = MathHelper.floor(posVec31.z);
    double d0 = posVec32.x - posVec31.x;
    double d1 = posVec32.z - posVec31.z;
    double d2 = d0 * d0 + d1 * d1;
    if (d2 < 1.0E-8D)
      return false; 
    double d3 = 1.0D / Math.sqrt(d2);
    d0 *= d3;
    d1 *= d3;
    sizeX += 2;
    sizeZ += 2;
    if (!isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1))
      return false; 
    sizeX -= 2;
    sizeZ -= 2;
    double d4 = 1.0D / Math.abs(d0);
    double d5 = 1.0D / Math.abs(d1);
    double d6 = i - posVec31.x;
    double d7 = j - posVec31.z;
    if (d0 >= 0.0D)
      d6++; 
    if (d1 >= 0.0D)
      d7++; 
    d6 /= d0;
    d7 /= d1;
    int k = (d0 < 0.0D) ? -1 : 1;
    int l = (d1 < 0.0D) ? -1 : 1;
    int i1 = MathHelper.floor(posVec32.x);
    int j1 = MathHelper.floor(posVec32.z);
    int k1 = i1 - i;
    int l1 = j1 - j;
    while (k1 * k > 0 || l1 * l > 0) {
      if (d6 < d7) {
        d6 += d4;
        i += k;
        k1 = i1 - i;
      } else {
        d7 += d5;
        j += l;
        l1 = j1 - j;
      } 
      if (!isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1))
        return false; 
    } 
    return true;
  }
  
  private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
    for (BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
      double d0 = blockpos.getX() + 0.5D - p_179692_7_.x;
      double d1 = blockpos.getZ() + 0.5D - p_179692_7_.z;
      if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D) {
        Block block = this.world.getBlockState(blockpos).getBlock();
        if (!block.isPassable((IBlockAccess)this.world, blockpos))
          return false; 
      } 
    } 
    return true;
  }
  
  private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
    int i = x - sizeX / 2;
    int j = z - sizeZ / 2;
    if (!isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_))
      return false; 
    for (int k = i; k < i + sizeX; k++) {
      for (int l = j; l < j + sizeZ; l++) {
        double d0 = k + 0.5D - vec31.x;
        double d1 = l + 0.5D - vec31.z;
        if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
          PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType((IBlockAccess)this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);
          if (pathnodetype == PathNodeType.WATER)
            return false; 
          if (pathnodetype == PathNodeType.LAVA)
            return false; 
          if (pathnodetype == PathNodeType.OPEN)
            return false; 
          pathnodetype = this.nodeProcessor.getPathNodeType((IBlockAccess)this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
          float f = this.entity.getPathPriority(pathnodetype);
          if (f < 0.0F || f >= 8.0F)
            return false; 
          if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER)
            return false; 
        } 
      } 
    } 
    return true;
  }
  
  protected void pathFollow() {
    Vec3d vec3d = getEntityPosition();
    int i = this.currentPath.getCurrentPathLength();
    for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); j++) {
      if ((this.currentPath.getPathPointFromIndex(j)).y != Math.floor(vec3d.y)) {
        i = j;
        break;
      } 
    } 
    this.maxDistanceToWaypoint = (this.entity.width > 0.75F) ? (this.entity.width / 2.0F) : (0.75F - this.entity.width / 2.0F);
    Vec3d vec3d1 = this.currentPath.getCurrentPos();
    if (MathHelper.abs((float)(this.entity.posX - vec3d1.x + 0.5D)) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.entity.posZ - vec3d1.z + 0.5D)) < this.maxDistanceToWaypoint && Math.abs(this.entity.posY - vec3d1.y) < 1.0D)
      this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1); 
    int k = MathHelper.ceil(this.entity.width);
    int l = MathHelper.ceil(this.entity.height);
    int i1 = k;
    for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); j1--) {
      if (isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex((Entity)this.entity, j1), k, l, i1)) {
        this.currentPath.setCurrentPathIndex(j1);
        break;
      } 
    } 
  }
}
