package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.ThreadSafeUtilities;

public class AStarWorkerJPS extends AStarWorker {
  private static final int MAX_SKIP_DISTANCE = 25;
  
  private static final int[][] neighbourOffsets = new int[][] { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 } };
  
  private final PriorityQueue<AStarNode> openQueue;
  
  private AStarNode currentNode;
  
  public AStarWorkerJPS(AStarPathPlannerJPS creator) {
    super(creator);
    this.openQueue = new PriorityQueue<>();
  }
  
  private void addOrUpdateNode(AStarNode newNode) {
    boolean found = false;
    Iterator<AStarNode> iter = this.closedNodes.iterator();
    while (iter.hasNext()) {
      AStarNode toUpdate = iter.next();
      if (newNode.equals(toUpdate)) {
        toUpdate.updateDistance(newNode.getG(), newNode.parent);
        found = true;
        break;
      } 
    } 
    if (!found)
      this.openQueue.offer(newNode); 
  }
  
  private ArrayList<AStarNode> backTrace(AStarNode start) throws ThreadSafeUtilities.ChunkAccessException {
    ArrayList<AStarNode> foundpath = new ArrayList<>();
    foundpath.add(this.currentNode);
    while (!this.currentNode.equals(start)) {
      int x = this.currentNode.x;
      int y = this.currentNode.y;
      int z = this.currentNode.z;
      int px = this.currentNode.parent.x;
      int pz = this.currentNode.parent.z;
      int dx = (px - x) / Math.max(Math.abs(x - px), 1);
      int dz = (pz - z) / Math.max(Math.abs(z - pz), 1);
      x += dx;
      z += dz;
      while (x != px || z != pz) {
        y = getGroundNodeHeight(x, y, z);
        foundpath.add(new AStarNode(x, y, z, 0, null));
        x += dx;
        z += dz;
      } 
      foundpath.add(this.currentNode.parent);
      this.currentNode = this.currentNode.parent;
    } 
    return foundpath;
  }
  
  private ArrayList<AStarNode> findNeighbours(AStarNode node) throws ThreadSafeUtilities.ChunkAccessException {
    ArrayList<AStarNode> r = new ArrayList<>();
    int x = node.x;
    int y = node.y;
    int z = node.z;
    int dist = node.getG();
    if (node.parent != null) {
      int px = node.parent.x;
      int py = node.parent.y;
      int pz = node.parent.z;
      boolean stairs = (py != y);
      int dx = (x - px) / Math.max(Math.abs(x - px), 1);
      int dz = (z - pz) / Math.max(Math.abs(z - pz), 1);
      if (dx != 0 && dz != 0) {
        if (stairs)
          return getAllNeighborsWithoutParent(x, y, z, dx, dz, node); 
        int left = 0;
        int right = 0;
        int nY = getGroundNodeHeight(x, y, z + dz);
        if (nY > 0) {
          left = nY;
          r.add(new AStarNode(x, nY, z + dz, dist + 1, node));
        } 
        nY = getGroundNodeHeight(x + dx, y, z);
        if (nY > 0) {
          right = nY;
          r.add(new AStarNode(x + dx, nY, z, dist + 1, node));
        } 
        if (left != 0 || right != 0)
          r.add(new AStarNode(x + dx, Math.max(left, right), z + dz, dist + 2, node)); 
        if (left != 0 && 
          getGroundNodeHeight(x - dx, py, z) == 0)
          r.add(new AStarNode(x - dx, left, z + dz, dist + 2, node)); 
        if (right != 0 && 
          getGroundNodeHeight(x, py, z - dz) == 0)
          r.add(new AStarNode(x + dx, right, z - dz, dist + 2, node)); 
      } else if (dx == 0) {
        int nY = getGroundNodeHeight(x, y, z + dz);
        if (nY > 0) {
          r.add(new AStarNode(x, nY, z + dz, dist + 1, node));
          if (stairs) {
            r.add(new AStarNode(x + 1, nY, z + dz, dist + 2, node));
            r.add(new AStarNode(x - 1, nY, z + dz, dist + 2, node));
          } else {
            int nnY = getGroundNodeHeight(x + 1, nY, z);
            if (nnY == 0)
              r.add(new AStarNode(x + 1, nY, z + dz, dist + 2, node)); 
            nnY = getGroundNodeHeight(x - 1, nY, z);
            if (nnY == 0)
              r.add(new AStarNode(x - 1, nY, z + dz, dist + 2, node)); 
          } 
        } 
      } else {
        int nY = getGroundNodeHeight(x + dx, y, z);
        if (nY > 0) {
          r.add(new AStarNode(x + dx, nY, z, dist + 1, node));
          if (stairs) {
            r.add(new AStarNode(x + dx, nY, z + 1, dist + 2, node));
            r.add(new AStarNode(x + dx, nY, z - 1, dist + 2, node));
          } else {
            int nnY = getGroundNodeHeight(x, nY, z + 1);
            if (nnY == 0)
              r.add(new AStarNode(x + dx, nY, z + 1, dist + 2, node)); 
            nnY = getGroundNodeHeight(x, nY, z - 1);
            if (nnY == 0)
              r.add(new AStarNode(x + dx, nY, z - 1, dist + 2, node)); 
          } 
        } 
      } 
    } else {
      for (int[] offset : neighbourOffsets) {
        int nY = getGroundNodeHeight(x + offset[0], y, z + offset[1]);
        if (nY > 0)
          r.add(new AStarNode(x + offset[0], nY, z + offset[1], nY, node)); 
      } 
    } 
    return r;
  }
  
  private ArrayList<AStarNode> getAllNeighborsWithoutParent(int x, int y, int z, int dx, int dz, AStarNode node) throws ThreadSafeUtilities.ChunkAccessException {
    ArrayList<AStarNode> r = new ArrayList<>();
    for (int[] offset : neighbourOffsets) {
      if (offset[0] != -dx || offset[1] != -dz) {
        int nY = getGroundNodeHeight(x + offset[0], y, z + offset[1]);
        if (nY > 0)
          r.add(new AStarNode(x + offset[0], nY, z + offset[1], nY, node)); 
      } 
    } 
    return r;
  }
  
  private int getGroundNodeHeight(int xN, int yN, int zN) throws ThreadSafeUtilities.ChunkAccessException {
    if (AStarStatic.isViable(this.world, xN, yN, zN, 0, this.config))
      return yN; 
    if (AStarStatic.isViable(this.world, xN, yN - 1, zN, -1, this.config))
      return yN - 1; 
    if (AStarStatic.isViable(this.world, xN, yN + 1, zN, 1, this.config))
      return yN + 1; 
    return 0;
  }
  
  public ArrayList<AStarNode> getPath(AStarNode start, AStarNode end, boolean searchMode) throws ThreadSafeUtilities.ChunkAccessException {
    this.openQueue.offer(start);
    this.targetNode = end;
    this.currentNode = start;
    int nbLoop = 0;
    while (!this.openQueue.isEmpty() && !shouldInterrupt()) {
      this.currentNode = this.openQueue.poll();
      this.closedNodes.add(this.currentNode);
      if (isNodeEnd(this.currentNode, end) || identifySuccessors(this.currentNode, nbLoop))
        return backTrace(start); 
      nbLoop++;
    } 
    return null;
  }
  
  private boolean identifySuccessors(AStarNode node, int nbLoop) throws ThreadSafeUtilities.ChunkAccessException {
    int x = node.x;
    int y = node.y;
    int z = node.z;
    ArrayList<AStarNode> successors = findNeighbours(node);
    for (AStarNode s : successors) {
      AStarNode jumpPoint = jump(s.x, s.y, s.z, x, y, z);
      if (jumpPoint == null || 
        this.closedNodes.contains(jumpPoint))
        continue; 
      addOrUpdateNode(jumpPoint);
    } 
    if (nbLoop == 0 && this.openQueue.isEmpty() && 
      MillConfigValues.LogChunkLoader >= 1)
      MillLog.major(this, "Failed on first loop. Neighbours: " + successors.toArray()); 
    return false;
  }
  
  private AStarNode jump(int nextX, int nextY, int nextZ, int px, int py, int pz) throws ThreadSafeUtilities.ChunkAccessException {
    int x = nextX;
    int y = nextY;
    int z = nextZ;
    int dist = this.currentNode.getG() + Math.abs(x - this.currentNode.x) + Math.abs(y - this.currentNode.y) + Math.abs(z - this.currentNode.z);
    int dx = x - px;
    int dz = z - pz;
    py = y;
    y = getGroundNodeHeight(x, py, z);
    if (y == 0)
      return null; 
    if (isCoordsEnd(x, y, z, this.targetNode) || dist >= 25)
      return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode); 
    int nxY = (dx != 0) ? getGroundNodeHeight(x + dx, y, z) : 0;
    int nzY = (dz != 0) ? getGroundNodeHeight(x, y, z + dz) : 0;
    if (dx != 0 && dz != 0) {
      if ((getGroundNodeHeight(x - dx, y, z + dz) != 0 && getGroundNodeHeight(x - dx, py, z) == 0) || (
        getGroundNodeHeight(x + dx, y, z - dz) != 0 && getGroundNodeHeight(x, py, z - dz) == 0))
        return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode); 
    } else if (dx != 0) {
      if (nxY != y || (getGroundNodeHeight(x, y, z + 1) == 0 && getGroundNodeHeight(x + dx, nxY, z + 1) != 0) || (
        getGroundNodeHeight(x, y, z - 1) == 0 && getGroundNodeHeight(x + dx, nxY, z - 1) != 0))
        return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode); 
    } else if (nzY != y || (getGroundNodeHeight(x + 1, y, z) == 0 && getGroundNodeHeight(x + 1, nzY, z + dz) != 0) || (
      getGroundNodeHeight(x - 1, y, z) == 0 && getGroundNodeHeight(x - 1, nzY, z + dz) != 0)) {
      return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode);
    } 
    if (dx != 0 && dz != 0) {
      AStarNode jx = jump(x + dx, y, z, x, y, z);
      AStarNode jy = jump(x, y, z + dz, x, y, z);
      if (jx != null || jy != null)
        return new AStarNode(x, y, z, dist, this.currentNode, this.targetNode); 
    } 
    if (nxY != 0 || nzY != 0)
      return jump(x + dx, y, z + dz, x, y, z); 
    return null;
  }
}
