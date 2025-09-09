package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.village.VillageMapInfo;

public class RegionMapper {
  private static final int MIN_SIZE_FOR_REGION_BRIDGING = 200;
  
  private static class Node {
    RegionMapper.Point2D pos;
    
    List<Node> neighbours;
    
    HashMap<Node, Integer> costs;
    
    int id;
    
    int fromDist;
    
    int toDist;
    
    int cornerSide;
    
    int region = 0;
    
    public Node(RegionMapper.Point2D p, int pid, int cornerSide, boolean ptemp) {
      this.pos = p;
      this.id = pid;
      this.cornerSide = cornerSide;
      this.neighbours = new ArrayList<>();
      this.costs = new HashMap<>();
    }
    
    public boolean equals(Object obj) {
      if (obj.getClass() != getClass())
        return false; 
      Node n = (Node)obj;
      return (n.hashCode() == hashCode());
    }
    
    public int hashCode() {
      return this.pos.x + (this.pos.z << 16);
    }
    
    public String toString() {
      return "Node " + this.id + ": " + this.pos + " group: " + this.region + " neighbours: " + this.neighbours.size() + "(fromDist: " + this.fromDist + ", toDist: " + this.toDist + ")";
    }
  }
  
  public static class Point2D {
    int x;
    
    int z;
    
    public Point2D(int px, int pz) {
      this.x = px;
      this.z = pz;
    }
    
    public int distanceTo(Point2D p) {
      int d = p.x - this.x;
      int d1 = p.z - this.z;
      return (int)Math.sqrt((d * d + d1 * d1));
    }
    
    public boolean equals(Object obj) {
      if (!(obj instanceof Point2D))
        return false; 
      Point2D p = (Point2D)obj;
      return (this.x == p.x && this.z == p.z);
    }
    
    public int hashCode() {
      return this.x << 16 & this.z;
    }
    
    public String toString() {
      return this.x + "/" + this.z;
    }
  }
  
  private static final AStarConfig JPS_CONFIG = new AStarConfig(true, false, false, false, true);
  
  public VillageMapInfo winfo;
  
  public boolean[][] top;
  
  public boolean[][] bottom;
  
  public boolean[][] left;
  
  public boolean[][] right;
  
  public short[][] topGround;
  
  public short[][] regions;
  
  public short thRegion;
  
  public List<Node> nodes;
  
  private int boolDisplay(boolean a, boolean b, boolean c, boolean d) {
    int i = a ? 1 : 0;
    i += b ? 2 : 0;
    i += c ? 4 : 0;
    i += d ? 8 : 0;
    return i;
  }
  
  private void buildNodes() {
    int i;
    for (i = 0; i < this.winfo.length; i++) {
      for (int j = 0; j < this.winfo.width; j++) {
        boolean isNode = false;
        int cornerSide = 0;
        if (i > 0 && j > 0 && 
          this.top[i][j] && this.left[i][j] && (!this.left[i - 1][j] || !this.top[i][j - 1])) {
          isNode = true;
          cornerSide |= 0x1;
        } 
        if (i < this.winfo.length - 1 && j > 0 && 
          this.bottom[i][j] && this.left[i][j] && (!this.left[i + 1][j] || !this.bottom[i][j - 1])) {
          isNode = true;
          cornerSide += 2;
          cornerSide |= 0x2;
        } 
        if (i > 0 && j < this.winfo.width - 1 && 
          this.top[i][j] && this.right[i][j] && (!this.right[i - 1][j] || !this.top[i][j + 1])) {
          isNode = true;
          cornerSide |= 0x4;
        } 
        if (i < this.winfo.length - 1 && j < this.winfo.width - 1 && 
          this.bottom[i][j] && this.right[i][j] && (!this.right[i + 1][j] || !this.bottom[i][j + 1])) {
          isNode = true;
          cornerSide |= 0x8;
        } 
        if (isNode)
          this.nodes.add(new Node(new Point2D(i, j), this.nodes.size(), cornerSide, false)); 
      } 
    } 
    for (Node n : this.nodes) {
      if (n.cornerSide == 1 && n.pos.x < this.winfo.length - 1 && n.pos.z < this.winfo.width - 1 && 
        this.bottom[n.pos.x][n.pos.z] && this.right[n.pos.x][n.pos.z] && this.bottom[n.pos.x][n.pos.z + 1] && this.right[n.pos.x + 1][n.pos.z]) {
        int tx = n.pos.x + 1;
        int tz = n.pos.z + 1;
        if (tx < this.winfo.length - 1 && tz < this.winfo.width - 1 && this.bottom[tx][tz] && this.right[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 2 && n.pos.x > 0 && n.pos.z < this.winfo.width - 1 && 
        this.top[n.pos.x][n.pos.z] && this.right[n.pos.x][n.pos.z] && this.top[n.pos.x][n.pos.z + 1] && this.right[n.pos.x - 1][n.pos.z]) {
        int tx = n.pos.x - 1;
        int tz = n.pos.z + 1;
        if (tx > 0 && tz < this.winfo.width - 1 && this.top[tx][tz] && this.right[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 4 && n.pos.x < this.winfo.length - 1 && n.pos.z > 0 && 
        this.bottom[n.pos.x][n.pos.z] && this.left[n.pos.x][n.pos.z] && this.bottom[n.pos.x][n.pos.z - 1] && this.left[n.pos.x + 1][n.pos.z]) {
        int tx = n.pos.x + 1;
        int tz = n.pos.z - 1;
        if (tx < this.winfo.length - 1 && tz > 0 && this.bottom[tx][tz] && this.left[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 8 && n.pos.x > 0 && n.pos.z > 0 && 
        this.top[n.pos.x][n.pos.z] && this.left[n.pos.x][n.pos.z] && this.top[n.pos.x][n.pos.z - 1] && this.left[n.pos.x - 1][n.pos.z]) {
        int tx = n.pos.x - 1;
        int tz = n.pos.z - 1;
        if (tx > 0 && tz > 0 && this.top[tx][tz] && this.left[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 3 && n.pos.z < this.winfo.width - 1 && 
        this.right[n.pos.x][n.pos.z]) {
        int tx = n.pos.x;
        int tz = n.pos.z + 1;
        if (tz < this.winfo.width - 1 && this.bottom[tx][tz] && this.right[tx][tz] && this.top[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 5 && n.pos.x < this.winfo.length - 1 && 
        this.bottom[n.pos.x][n.pos.z]) {
        int tx = n.pos.x + 1;
        int tz = n.pos.z;
        if (tx < this.winfo.length - 1 && this.bottom[tx][tz] && this.right[tx][tz] && this.left[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 10 && n.pos.x > 0 && 
        this.top[n.pos.x][n.pos.z]) {
        int tx = n.pos.x - 1;
        int tz = n.pos.z;
        if (tx > 0 && this.top[tx][tz] && this.right[tx][tz] && this.left[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
      if (n.cornerSide == 12 && n.pos.z > 0 && 
        this.left[n.pos.x][n.pos.z]) {
        int tx = n.pos.x;
        int tz = n.pos.z - 1;
        if (tx > 0 && this.top[tx][tz] && this.bottom[tx][tz] && this.left[tx][tz]) {
          n.pos.x = tx;
          n.pos.z = tz;
        } 
      } 
    } 
    for (i = this.nodes.size() - 1; i > -1; i--) {
      for (int j = i - 1; j > -1; j--) {
        if (((Node)this.nodes.get(i)).equals(this.nodes.get(j))) {
          this.nodes.remove(i);
          break;
        } 
      } 
    } 
  }
  
  public boolean canSee(Point2D p1, Point2D p2) {
    int xdist = p2.x - p1.x;
    int zdist = p2.z - p1.z;
    if (xdist == 0 && zdist == 0)
      return true; 
    int xsign = 1;
    int zsign = 1;
    if (xdist < 0)
      xsign = -1; 
    if (zdist < 0)
      zsign = -1; 
    int x = p1.x;
    int z = p1.z;
    int xdone = 0;
    int zdone = 0;
    while (x != p2.x || z != p2.z) {
      int nx, nz;
      if (xdist == 0 || (zdist != 0 && xdone * 1000 / xdist > zdone * 1000 / zdist)) {
        nz = z + zsign;
        nx = x;
        zdone += zsign;
        if (zsign == 1 && !this.right[x][z])
          return false; 
        if (zsign == -1 && !this.left[x][z])
          return false; 
      } else {
        nx = x + xsign;
        nz = z;
        xdone += xsign;
        if (xsign == 1 && !this.bottom[x][z])
          return false; 
        if (xsign == -1 && !this.top[x][z])
          return false; 
      } 
      x = nx;
      z = nz;
    } 
    return true;
  }
  
  public boolean createConnectionsTable(VillageMapInfo winfo, Point thStanding) throws MillLog.MillenaireException {
    long startTime = System.nanoTime();
    long totalStartTime = startTime;
    this.winfo = winfo;
    this.top = new boolean[winfo.length][winfo.width];
    this.bottom = new boolean[winfo.length][winfo.width];
    this.left = new boolean[winfo.length][winfo.width];
    this.right = new boolean[winfo.length][winfo.width];
    this.regions = new short[winfo.length][winfo.width];
    this.topGround = VillageMapInfo.shortArrayDeepClone(winfo.topGround);
    this.nodes = new ArrayList<>();
    for (int i = 0; i < winfo.length; i++) {
      for (int j = 0; j < winfo.width; j++) {
        int y = winfo.topGround[i][j];
        int space = winfo.spaceAbove[i][j];
        if (!winfo.danger[i][j] && !winfo.water[i][j] && 
          space > 1) {
          if (i > 0) {
            int ny = winfo.topGround[i - 1][j];
            int nspace = winfo.spaceAbove[i - 1][j];
            boolean connected = false;
            if (ny == y && nspace > 1) {
              connected = true;
            } else if (ny == y - 1 && nspace > 2) {
              connected = true;
            } else if (ny == y + 1 && nspace > 1 && space > 2) {
              connected = true;
            } 
            if (connected) {
              this.top[i][j] = true;
              this.bottom[i - 1][j] = true;
            } 
          } 
          if (j > 0) {
            int ny = winfo.topGround[i][j - 1];
            int nspace = winfo.spaceAbove[i][j - 1];
            boolean connected = false;
            if (ny == y && nspace > 1) {
              connected = true;
            } else if (ny == y - 1 && nspace > 2) {
              connected = true;
            } else if (ny == y + 1 && nspace > 1 && space > 2) {
              connected = true;
            } 
            if (connected) {
              this.left[i][j] = true;
              this.right[i][j - 1] = true;
            } 
          } 
        } 
      } 
    } 
    if (MillConfigValues.LogConnections >= 2)
      MillLog.minor(this, "Time taken for connection building: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
    startTime = System.nanoTime();
    buildNodes();
    if (MillConfigValues.LogConnections >= 2)
      MillLog.minor(this, "Time taken for nodes finding: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
    startTime = System.nanoTime();
    for (Node n : this.nodes) {
      for (Node n2 : this.nodes) {
        if (n.id < n2.id && 
          canSee(n.pos, n2.pos)) {
          Integer distance = Integer.valueOf(n.pos.distanceTo(n2.pos));
          n.costs.put(n2, distance);
          n.neighbours.add(n2);
          n2.costs.put(n, distance);
          n2.neighbours.add(n);
        } 
      } 
    } 
    if (MillConfigValues.LogConnections >= 2)
      MillLog.minor(this, "Time taken for nodes linking: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
    startTime = System.nanoTime();
    findRegions(thStanding);
    if (MillConfigValues.LogConnections >= 2)
      MillLog.minor(this, "Time taken for group finding: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
    if (MillConfigValues.LogConnections >= 1)
      MillLog.major(this, "Node graph complete. Size: " + this.nodes.size() + " Time taken: " + ((System.nanoTime() - totalStartTime) / 1000000.0D)); 
    if (MillConfigValues.LogConnections >= 3 && MillConfigValues.DEV) {
      MillLog.major(this, "Calling displayConnectionsLog");
      displayConnectionsLog();
    } 
    return true;
  }
  
  private void displayConnectionsLog() {
    long startTime = System.nanoTime();
    MillLog.minor(this, "Connections:");
    String s = "    ";
    int i3;
    for (i3 = 0; i3 < this.winfo.width; i3++)
      s = s + (MathHelper.floor((i3 / 10)) % 10); 
    MillLog.minor(this, s);
    s = "    ";
    for (i3 = 0; i3 < this.winfo.width; i3++)
      s = s + (i3 % 10); 
    MillLog.minor(this, s);
    for (int i2 = 0; i2 < this.winfo.length; i2++) {
      if (i2 < 10) {
        s = i2 + "   ";
      } else if (i2 < 100) {
        s = i2 + "  ";
      } else {
        s = i2 + " ";
      } 
      for (int i4 = 0; i4 < this.winfo.width; i4++)
        s = s + Integer.toHexString(boolDisplay(this.top[i2][i4], this.left[i2][i4], this.bottom[i2][i4], this.right[i2][i4])); 
      if (i2 < 10) {
        s = s + "   " + i2;
      } else if (i2 < 100) {
        s = s + "  " + i2;
      } else {
        s = s + " " + i2;
      } 
      MillLog.minor(this, s);
    } 
    MillLog.minor(this, "spaceAbove:");
    s = "    ";
    int i1;
    for (i1 = 0; i1 < this.winfo.width; i1++)
      s = s + (MathHelper.floor((i1 / 10)) % 10); 
    MillLog.minor(this, s);
    s = "    ";
    for (i1 = 0; i1 < this.winfo.width; i1++)
      s = s + (i1 % 10); 
    MillLog.minor(this, s);
    for (int n = 0; n < this.winfo.length; n++) {
      if (n < 10) {
        s = n + "   ";
      } else if (n < 100) {
        s = n + "  ";
      } else {
        s = n + " ";
      } 
      for (int i4 = 0; i4 < this.winfo.width; i4++)
        s = s + this.winfo.spaceAbove[n][i4]; 
      if (n < 10) {
        s = s + "   " + n;
      } else if (n < 100) {
        s = s + "  " + n;
      } else {
        s = s + " " + n;
      } 
      MillLog.minor(this, s);
    } 
    MillLog.minor(this, "Y pos:");
    s = "    ";
    int m;
    for (m = 0; m < this.winfo.width; m++)
      s = s + (MathHelper.floor((m / 10)) % 10); 
    MillLog.minor(this, s);
    s = "    ";
    for (m = 0; m < this.winfo.width; m++)
      s = s + (m % 10); 
    MillLog.minor(this, s);
    for (int k = 0; k < this.winfo.length; k++) {
      if (k < 10) {
        s = k + "   ";
      } else if (k < 100) {
        s = k + "  ";
      } else {
        s = k + " ";
      } 
      for (int i4 = 0; i4 < this.winfo.width; i4++)
        s = s + (this.winfo.topGround[k][i4] % 10); 
      if (k < 10) {
        s = s + "   " + k;
      } else if (k < 100) {
        s = s + "  " + k;
      } else {
        s = s + " " + k;
      } 
      MillLog.minor(this, s);
    } 
    MillLog.minor(this, "Nodes:");
    s = "    ";
    int j;
    for (j = 0; j < this.winfo.width; j++)
      s = s + (MathHelper.floor((j / 10)) % 10); 
    MillLog.minor(this, s);
    s = "    ";
    for (j = 0; j < this.winfo.width; j++)
      s = s + (j % 10); 
    MillLog.minor(this, s);
    for (int i = 0; i < this.winfo.length; i++) {
      if (i < 10) {
        s = i + "   ";
      } else if (i < 100) {
        s = i + "  ";
      } else {
        s = i + " ";
      } 
      for (int i4 = 0; i4 < this.winfo.width; i4++) {
        boolean found = false;
        for (Node node : this.nodes) {
          if (node.pos.x == i && node.pos.z == i4) {
            s = s + Integer.toHexString(node.id % 10);
            found = true;
          } 
        } 
        if (!found)
          if (!this.top[i][i4] && !this.bottom[i][i4] && !this.left[i][i4] && !this.right[i][i4]) {
            s = s + "#";
          } else if (!this.top[i][i4] || !this.bottom[i][i4] || !this.left[i][i4] || !this.right[i][i4]) {
            s = s + ".";
          } else {
            s = s + " ";
          }  
      } 
      if (i < 10) {
        s = s + "   " + i;
      } else if (i < 100) {
        s = s + "  " + i;
      } else {
        s = s + " " + i;
      } 
      MillLog.minor(this, s);
    } 
    MillLog.minor(this, "Displaying connections finished. Time taken: " + ((System.nanoTime() - startTime) / 1000000.0D));
  }
  
  private void findRegions(Point thStanding) throws MillLog.MillenaireException {
    int nodesMarked = 0, nodeGroup = 0;
    while (nodesMarked < this.nodes.size()) {
      nodeGroup++;
      List<Node> toVisit = new ArrayList<>();
      Node fn = null;
      int k = 0;
      while (fn == null) {
        if (((Node)this.nodes.get(k)).region == 0)
          fn = this.nodes.get(k); 
        k++;
      } 
      fn.region = nodeGroup;
      nodesMarked++;
      toVisit.add(fn);
      while (toVisit.size() > 0) {
        for (Node n : ((Node)toVisit.get(0)).neighbours) {
          if (n.region == 0) {
            n.region = nodeGroup;
            toVisit.add(n);
            nodesMarked++;
            continue;
          } 
          if (n.region != nodeGroup)
            throw new MillLog.MillenaireException("Node belongs to group " + n.region + " but reached from " + nodeGroup); 
        } 
        toVisit.remove(0);
      } 
    } 
    for (int i = 0; i < this.winfo.length; i++) {
      for (int k = 0; k < this.winfo.width; k++)
        this.regions[i][k] = -1; 
    } 
    for (Node n : this.nodes)
      this.regions[n.pos.x][n.pos.z] = (short)n.region; 
    boolean spreaddone = true;
    while (spreaddone) {
      spreaddone = false;
      for (int k = 0; k < this.winfo.length; k++) {
        for (int m = 0; m < this.winfo.width; m++) {
          if (this.regions[k][m] > 0) {
            short regionid = this.regions[k][m];
            int x = k;
            while (x > 1 && this.top[x][m] && this.regions[x - 1][m] == -1) {
              x--;
              this.regions[x][m] = regionid;
              spreaddone = true;
            } 
            x = k;
            while (x < this.winfo.length - 1 && this.bottom[x][m] && this.regions[x + 1][m] == -1) {
              x++;
              this.regions[x][m] = regionid;
              spreaddone = true;
            } 
            x = m;
            while (x > 1 && this.left[k][x] && this.regions[k][x - 1] == -1) {
              x--;
              this.regions[k][x] = regionid;
              spreaddone = true;
            } 
            x = m;
            while (x < this.winfo.width - 1 && this.right[k][x] && this.regions[k][x + 1] == -1) {
              x++;
              this.regions[k][x] = regionid;
              spreaddone = true;
            } 
          } 
        } 
      } 
    } 
    this.thRegion = this.regions[thStanding.getiX() - this.winfo.mapStartX][thStanding.getiZ() - this.winfo.mapStartZ];
    long startTime = System.nanoTime();
    int maxRegionId = -1;
    for (Node n : this.nodes) {
      if (n.region > maxRegionId)
        maxRegionId = n.region; 
    } 
    int[] regionsSize = new int[maxRegionId + 1];
    Point2D[] pointsInRegion = new Point2D[maxRegionId + 1];
    int j;
    for (j = 0; j <= maxRegionId; j++)
      regionsSize[j] = 0; 
    for (j = 0; j < this.winfo.length; j++) {
      for (int k = 0; k < this.winfo.width; k++) {
        if (this.regions[j][k] > -1)
          regionsSize[this.regions[j][k]] = regionsSize[this.regions[j][k]] + 1; 
      } 
    } 
    for (Node n : this.nodes)
      pointsInRegion[n.region] = n.pos; 
    for (j = 0; j <= maxRegionId; j++) {
      if (regionsSize[j] > 200 && j != this.thRegion)
        try {
          Point targetPoint = new Point(((pointsInRegion[j]).x + this.winfo.mapStartX), (this.winfo.topGround[(pointsInRegion[j]).x][(pointsInRegion[j]).z] - 1), ((pointsInRegion[j]).z + this.winfo.mapStartZ));
          ArrayList<AStarNode> path = getPath(thStanding.getiX(), thStanding.getiY(), thStanding.getiZ(), targetPoint.getiX(), targetPoint.getiY() + 1, targetPoint.getiZ());
          if (path != null)
            for (int x = 0; x < this.winfo.length; x++) {
              for (int z = 0; z < this.winfo.width; z++) {
                if (this.regions[x][z] == j)
                  this.regions[x][z] = this.thRegion; 
              } 
            }  
        } catch (org.millenaire.common.utilities.ThreadSafeUtilities.ChunkAccessException e) {
          if (MillConfigValues.LogChunkLoader >= 1)
            MillLog.major(this, e.getMessage()); 
        }  
    } 
    if (MillConfigValues.LogConnections >= 2)
      MillLog.minor(this, "Time taken for region bridging: " + ((System.nanoTime() - startTime) / 1000000.0D)); 
    if (MillConfigValues.LogConnections >= 2)
      MillLog.minor(this, nodeGroup + " node groups found."); 
  }
  
  private ArrayList<AStarNode> getPath(int startx, int starty, int startz, int destx, int desty, int destz) throws ThreadSafeUtilities.ChunkAccessException {
    if (!AStarStatic.isViable(this.winfo.world, startx, starty, startz, 0, JPS_CONFIG))
      starty--; 
    if (!AStarStatic.isViable(this.winfo.world, startx, starty, startz, 0, JPS_CONFIG))
      starty += 2; 
    if (!AStarStatic.isViable(this.winfo.world, startx, starty, startz, 0, JPS_CONFIG))
      starty--; 
    AStarNode starter = new AStarNode(startx, starty, startz, 0, null);
    AStarNode finish = new AStarNode(destx, desty, destz, -1, null);
    AStarWorker pathWorker = new AStarWorker();
    pathWorker.setup(this.winfo.world, starter, finish, JPS_CONFIG);
    return pathWorker.runSync();
  }
  
  public boolean isInArea(Point p) {
    return (p.x >= this.winfo.mapStartX && p.x < (this.winfo.mapStartX + this.winfo.length) && p.z >= this.winfo.mapStartZ && p.z < (this.winfo.mapStartZ + this.winfo.width));
  }
  
  public boolean isValidPoint(Point p) {
    if (!isInArea(p))
      return false; 
    return (this.winfo.spaceAbove[p.getiX() - this.winfo.mapStartX][p.getiZ() - this.winfo.mapStartZ] > 1);
  }
}
