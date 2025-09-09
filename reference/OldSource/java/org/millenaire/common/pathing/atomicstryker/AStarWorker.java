package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import net.minecraft.world.World;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.ThreadSafeUtilities;

public class AStarWorker implements Runnable {
  private final long SEARCH_TIME_LIMIT = 150L;
  
  public AStarPathPlannerJPS boss;
  
  AStarConfig config;
  
  public boolean isRunning = false;
  
  public final ArrayList<AStarNode> closedNodes;
  
  private AStarNode startNode;
  
  protected AStarNode targetNode;
  
  protected World world;
  
  private long timeLimit;
  
  private final PriorityQueue<AStarNode> queue;
  
  private boolean isBusy = false;
  
  public AStarWorker() {
    this.boss = null;
    this.closedNodes = new ArrayList<>();
    this.queue = new PriorityQueue<>(500);
  }
  
  public AStarWorker(AStarPathPlannerJPS creator) {
    this.boss = creator;
    this.closedNodes = new ArrayList<>();
    this.queue = new PriorityQueue<>(500);
  }
  
  private void addToBinaryHeap(AStarNode input) {
    this.queue.offer(input);
  }
  
  private void checkPossibleLadder(AStarNode node) throws ThreadSafeUtilities.ChunkAccessException {
    int x = node.x;
    int y = node.y;
    int z = node.z;
    if (AStarStatic.isLadder(this.world, ThreadSafeUtilities.getBlock(this.world, x, y, z), x, y, z)) {
      AStarNode ladder = null;
      if (AStarStatic.isLadder(this.world, ThreadSafeUtilities.getBlock(this.world, x, y + 1, z), x, y + 1, z));
      ladder = new AStarNode(x, y + 1, z, node.getG() + 2, node, this.targetNode);
      if (!tryToUpdateExistingHeapNode(node, ladder))
        addToBinaryHeap(ladder); 
      if (AStarStatic.isLadder(this.world, ThreadSafeUtilities.getBlock(this.world, x, y - 1, z), x, y - 1, z));
      ladder = new AStarNode(x, y - 1, z, node.getG() + 2, node, this.targetNode);
      if (!tryToUpdateExistingHeapNode(node, ladder))
        addToBinaryHeap(ladder); 
    } 
  }
  
  private int getCostNodeToNode(AStarNode a, AStarNode b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z);
  }
  
  public void getNextCandidates(AStarNode parent, boolean droppingAllowed) throws ThreadSafeUtilities.ChunkAccessException {
    int x = parent.x;
    int y = parent.y;
    int z = parent.z;
    int[][] c = droppingAllowed ? AStarStatic.candidates_allowdrops : AStarStatic.candidates;
    for (int i = 0; i < c.length; i++) {
      AStarNode check = new AStarNode(x + c[i][0], y + c[i][1], z + c[i][2], parent.getG() + c[i][3], parent, this.targetNode);
      try {
        boolean found = false;
        Iterator<AStarNode> iter = this.closedNodes.iterator();
        while (iter.hasNext()) {
          AStarNode toUpdate = iter.next();
          if (check.equals(toUpdate)) {
            toUpdate.updateDistance(check.getG() + getCostNodeToNode(toUpdate, check), parent);
            found = true;
            break;
          } 
        } 
        if (!found && !tryToUpdateExistingHeapNode(parent, check) && 
          AStarStatic.isViable(this.world, check, c[i][1], this.config))
          addToBinaryHeap(check); 
      } catch (Exception e) {
        if (MillConfigValues.LogChunkLoader >= 2)
          MillLog.minor(this, e.getLocalizedMessage()); 
      } 
    } 
  }
  
  public ArrayList<AStarNode> getPath(AStarNode start, AStarNode end, boolean searchMode) throws ThreadSafeUtilities.ChunkAccessException {
    this.queue.offer(start);
    this.targetNode = end;
    AStarNode current = start;
    while (!isNodeEnd(current, end)) {
      this.closedNodes.add(this.queue.poll());
      getNextCandidates(current, searchMode);
      if (this.queue.isEmpty() || shouldInterrupt())
        return null; 
      current = this.queue.peek();
    } 
    ArrayList<AStarNode> foundpath = new ArrayList<>();
    foundpath.add(current);
    while (current != start) {
      foundpath.add(current.parent);
      current = current.parent;
    } 
    return foundpath;
  }
  
  public boolean isBusy() {
    return this.isBusy;
  }
  
  protected boolean isCoordsEnd(int x, int y, int z, AStarNode end) {
    if (!this.config.tolerance)
      return (x == end.x && y == end.y && z == end.z); 
    if (Math.abs(x - end.x) <= this.config.toleranceHorizontal && Math.abs(z - end.z) <= this.config.toleranceHorizontal && Math.abs(y - end.y) <= this.config.toleranceVertical)
      return true; 
    return false;
  }
  
  protected boolean isNodeEnd(AStarNode cn, AStarNode end) {
    return isCoordsEnd(cn.x, cn.y, cn.z, end);
  }
  
  public void run() {
    this.isBusy = true;
    this.timeLimit = System.currentTimeMillis() + 150L;
    ArrayList<AStarNode> result = null;
    try {
      result = getPath(this.startNode, this.targetNode, this.config.allowDropping);
    } catch (org.millenaire.common.utilities.ThreadSafeUtilities.ChunkAccessException e) {
      MillLog.error(this, "Chunk access violation while calculating a path for " + this.boss);
      this.boss.onNoPathAvailable();
    } catch (Throwable e) {
      MillLog.printException("Exception while calculating a path:", e);
      this.boss.onNoPathAvailable();
    } 
    if (result == null) {
      this.boss.onNoPathAvailable();
    } else {
      this.boss.onFoundPath(result);
    } 
    this.isBusy = false;
  }
  
  public ArrayList<AStarNode> runSync() {
    this.timeLimit = System.currentTimeMillis() + 150L;
    ArrayList<AStarNode> result = null;
    try {
      result = getPath(this.startNode, this.targetNode, this.config.allowDropping);
    } catch (org.millenaire.common.utilities.ThreadSafeUtilities.ChunkAccessException e) {
      MillLog.error(this, "Chunk access violation while calculating a path for " + this.boss);
      return null;
    } catch (Throwable e) {
      MillLog.printException("Exception while calculating a path:", e);
      return null;
    } 
    return result;
  }
  
  public void setup(World winput, AStarNode start, AStarNode end, AStarConfig config) {
    this.world = winput;
    this.startNode = start;
    this.targetNode = end;
    this.config = config;
  }
  
  protected boolean shouldInterrupt() {
    return (System.currentTimeMillis() > this.timeLimit);
  }
  
  private boolean tryToUpdateExistingHeapNode(AStarNode parent, AStarNode checkedOne) {
    Iterator<AStarNode> iter = this.queue.iterator();
    while (iter.hasNext()) {
      AStarNode itNode = iter.next();
      if (itNode.equals(checkedOne)) {
        itNode.updateDistance(checkedOne.getG(), parent);
        return true;
      } 
    } 
    return false;
  }
}
