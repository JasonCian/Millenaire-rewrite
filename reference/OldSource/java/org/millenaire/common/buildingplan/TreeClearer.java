package org.millenaire.common.buildingplan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.IntPoint;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.BuildingLocation;

public class TreeClearer {
  private static final int LEAF_CLEARING_Y_END = 30;
  
  private static final int LEAF_CLEARING_Y_START = -10;
  
  private static final int LOG_SEARCH_MARGIN = 4;
  
  private static final int LEAF_CLEAR_MARGIN = 2;
  
  private static final int NON_DECAY_RANGE = 3;
  
  public static long cumulatedTimeTreeFinding = 0L;
  
  public static long cumulatedTimeLeaveDecay = 0L;
  
  private final World world;
  
  private final BuildingLocation location;
  
  private final BuildingPlan plan;
  
  final Set<IntPoint> pointsTested = new HashSet<>();
  
  final Set<IntPoint> pointsTree = new HashSet<>();
  
  IBlockState decayingLeaves = Blocks.AIR.getDefaultState();
  
  IBlockState decayedTree = Blocks.AIR.getDefaultState();
  
  IBlockState nonDecayingLeaves = null;
  
  IBlockState nonDecayedTree = null;
  
  boolean testMode = false;
  
  public TreeClearer(BuildingPlan plan, BuildingLocation location, World world) {
    this.location = location;
    this.world = world;
    this.plan = plan;
    if (this.testMode) {
      this.decayingLeaves = Blocks.GLASS.getDefaultState();
      this.decayedTree = Blocks.GOLD_BLOCK.getDefaultState();
      this.nonDecayingLeaves = Blocks.STAINED_GLASS.getStateFromMeta(15);
      this.nonDecayedTree = Blocks.IRON_BLOCK.getDefaultState();
    } 
  }
  
  public void cleanup() {
    long startTime = System.nanoTime();
    findTrees();
    cumulatedTimeTreeFinding += System.nanoTime() - startTime;
    startTime = System.nanoTime();
    decayLogsAndLeaves();
    cumulatedTimeLeaveDecay += System.nanoTime() - startTime;
  }
  
  private void decayLogsAndLeaves() {
    long startTime = System.nanoTime();
    Set<IntPoint> nonDecayPosSet = new HashSet<>();
    for (IntPoint logPos : this.pointsTree) {
      for (int i = -3; i <= 3; i++) {
        for (int dz = -3; dz <= 3; dz++) {
          for (int dy = -3; dy <= 3; dy++) {
            IntPoint nonDecayPoint = logPos.getRelative(i, dy, dz);
            nonDecayPosSet.add(nonDecayPoint);
            if (this.testMode) {
              Block block = nonDecayPoint.getBlock(this.world);
              if (!isLogBlock(block) && block.getDefaultState() != this.nonDecayedTree)
                nonDecayPoint.setBlockState(this.world, Blocks.STAINED_GLASS.getStateFromMeta(0)); 
            } 
          } 
        } 
      } 
      if (this.nonDecayedTree != null)
        logPos.setBlockState(this.world, this.nonDecayedTree); 
    } 
    int nbLeavesDecayed = 0, nbLeavesSpared = 0;
    int x = this.location.pos.getiX();
    int z = this.location.pos.getiZ();
    int orientation = this.location.orientation;
    int randomWoolColour = MillCommonUtilities.randomInt(16);
    for (int dx = -this.plan.areaToClearLengthBefore - 2; dx < this.plan.length + this.plan.areaToClearLengthAfter + 2; dx++) {
      for (int dz = -this.plan.areaToClearWidthBefore - 2; dz < this.plan.width + this.plan.areaToClearWidthAfter + 2; dz++) {
        boolean isXOutsidePlan = (dx < 0 || dx > this.plan.length);
        boolean isZOutsidePlan = (dz < 0 || dz > this.plan.width);
        if (isXOutsidePlan || isZOutsidePlan) {
          for (int y = this.location.pos.getiY() + -10; y < this.location.pos.getiY() + 30; y++) {
            IntPoint p = BuildingPlan.adjustForOrientation(x, y, z, dx - this.plan.lengthOffset, dz - this.plan.widthOffset, orientation).getIntPoint();
            Block block = p.getBlock(this.world);
            if (isLogBlock(block)) {
              if (!this.pointsTree.contains(p))
                p.setBlockState(this.world, this.decayedTree); 
            } else if (isLeaveBlock(block)) {
              if (!nonDecayPosSet.contains(p)) {
                nbLeavesDecayed++;
                p.setBlockState(this.world, this.decayingLeaves);
              } else {
                nbLeavesSpared++;
                if (this.nonDecayingLeaves != null)
                  p.setBlockState(this.world, this.nonDecayingLeaves); 
              } 
            } 
          } 
          if (this.testMode) {
            IntPoint p = BuildingPlan.adjustForOrientation(x, this.location.pos.getiY() + 15, z, dx - this.plan.lengthOffset, dz - this.plan.widthOffset, orientation).getIntPoint();
            p.setBlockState(this.world, Blocks.STAINED_GLASS.getStateFromMeta(randomWoolColour));
          } 
        } 
      } 
    } 
    if (MillConfigValues.LogWorldGeneration >= 1)
      MillLog.debug(this, "Finished decaying " + nbLeavesDecayed + " leaves. Spared " + nbLeavesSpared + " . Total time in ns: " + (System.nanoTime() - startTime)); 
  }
  
  private void findTrees() {
    int x = this.location.pos.getiX();
    int z = this.location.pos.getiZ();
    int orientation = this.location.orientation;
    for (int dx = -this.plan.areaToClearLengthBefore - 4; dx < this.plan.length + this.plan.areaToClearLengthAfter + 4; dx++) {
      for (int dz = -this.plan.areaToClearWidthBefore - 4; dz < this.plan.width + this.plan.areaToClearWidthAfter + 4; dz++) {
        boolean isXOutsidePlan = (dx < 0 || dx > this.plan.length);
        boolean isZOutsidePlan = (dz < 0 || dz > this.plan.width);
        if (isXOutsidePlan || isZOutsidePlan)
          for (int y = this.location.pos.getiY() + -10; y < this.location.pos.getiY() + 30; y++) {
            IntPoint p = BuildingPlan.adjustForOrientation(x, y, z, dx - this.plan.lengthOffset, dz - this.plan.widthOffset, orientation).getIntPoint();
            if (!this.pointsTested.contains(p) && isLogBlock(p.getBlock(this.world)) && BlockItemUtilities.isBlockGround(p.getBelow().getBlock(this.world)))
              handleTree(p); 
          }  
      } 
    } 
  }
  
  private void handleTree(IntPoint startingPos) {
    List<IntPoint> treePoints = new ArrayList<>();
    List<IntPoint> pointsToTest = new ArrayList<>();
    pointsToTest.add(startingPos);
    boolean abort = false;
    while (!pointsToTest.isEmpty() && !abort) {
      IntPoint p = pointsToTest.get(pointsToTest.size() - 1);
      if (!this.pointsTested.contains(p)) {
        this.pointsTested.add(p);
        Block block = p.getBlock(this.world);
        if (isLogBlock(block)) {
          treePoints.add(p);
          pointsToTest.addAll(p.getAllNeightbours());
        } 
        abort = (treePoints.size() > 100 || p.horizontalDistanceToSquared(startingPos) > 100);
      } 
      pointsToTest.remove(p);
    } 
    this.pointsTree.addAll(treePoints);
  }
  
  private boolean isLeaveBlock(Block block) {
    return block instanceof net.minecraft.block.BlockLeaves;
  }
  
  private boolean isLogBlock(Block block) {
    return block instanceof net.minecraft.block.BlockLog;
  }
}
