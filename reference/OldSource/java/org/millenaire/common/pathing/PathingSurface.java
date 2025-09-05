package org.millenaire.common.pathing;

import java.util.Collections;
import java.util.LinkedList;

public class PathingSurface {
  public LinkedList<ExtendedPathTile> alltiles;
  
  public class ExtendedPathTile extends PathingPathCalcTile implements Comparable<ExtendedPathTile> {
    public LinkedList<ExtendedPathTile> neighbors;
    
    public short distance;
    
    public int key;
    
    public ExtendedPathTile(boolean walkable, boolean lad, short[] pos) {
      super(walkable, lad, pos);
      this.neighbors = new LinkedList<>();
      this.key = pos[0] + (pos[1] << 10) + (pos[2] << 20);
      this.distance = Short.MAX_VALUE;
    }
    
    public ExtendedPathTile(PathingPathCalcTile c) {
      super(c);
      this.neighbors = new LinkedList<>();
      this.key = c.position[0] + (c.position[1] << 10) + (c.position[2] << 20);
      this.distance = Short.MAX_VALUE;
    }
    
    public int compareTo(ExtendedPathTile arg0) {
      if (this.key == arg0.key)
        return 0; 
      if (this.key > arg0.key)
        return 1; 
      return -1;
    }
    
    public boolean equals(Object o) {
      if (o == null || !(o instanceof ExtendedPathTile))
        return false; 
      return (this.key == ((ExtendedPathTile)o).key);
    }
    
    public int hashCode() {
      return this.key;
    }
  }
  
  public PathingSurface(PathingPathCalcTile[][][] region, PathingPathCalcTile ct) {
    ExtendedPathTile[][][] surface = new ExtendedPathTile[region.length][(region[0]).length][(region[0][0]).length];
    for (int i = 0; i < region.length; i++) {
      for (int j = 0; j < (region[0]).length - 2; j++) {
        for (int k = 0; k < (region[0][0]).length; k++) {
          if (region[i][j][k] != null && ((j + 2 < (region[0]).length && region[i][j + 1][k] == null && region[i][j + 2][k] == null) || ((region[i][j][k]).ladder && (region[i][j + 1][k] == null || (region[i][j + 1][k]).ladder)))) {
            surface[i][j][k] = new ExtendedPathTile(region[i][j][k]);
          } else {
            surface[i][j][k] = null;
          } 
        } 
      } 
    } 
    ExtendedPathTile centraltile = new ExtendedPathTile(ct);
    this.alltiles = new LinkedList<>();
    LinkedList<ExtendedPathTile> toprocess = new LinkedList<>();
    if (surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]] != null) {
      toprocess.add(surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]]);
      (surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]]).distance = (short)((surface[centraltile.position[0]][centraltile.position[1]][centraltile.position[2]]).distance - 1);
    } 
    while (!toprocess.isEmpty()) {
      ExtendedPathTile current = toprocess.pollFirst();
      this.alltiles.add(current);
      short s1 = current.position[0];
      short j = current.position[1];
      short k = current.position[2];
      byte t;
      for (t = -1; t <= 1; t = (byte)(t + 1)) {
        if ((surface[s1][j][k]).ladder) {
          if (j + t >= 0 && j + t < (surface[0]).length) {
            if ((surface[s1][j + 1][k]).ladder) {
              if ((surface[s1][j + t][k]).distance == Short.MAX_VALUE) {
                toprocess.add(surface[s1][j + t][k]);
                (surface[s1][j + t][k]).distance = (short)((surface[s1][j + t][k]).distance - 1);
              } 
              current.neighbors.add(surface[s1][j + t][k]);
            } 
            if (surface[s1][j - 1][k] != null) {
              if ((surface[s1][j + t][k]).distance == Short.MAX_VALUE) {
                toprocess.add(surface[s1][j + t][k]);
                (surface[s1][j + t][k]).distance = (short)((surface[s1][j + t][k]).distance - 1);
              } 
              current.neighbors.add(surface[s1][j + t][k]);
            } 
          } 
        } else if (j + t >= 0 && j + t < (surface[0]).length) {
          if (s1 + 1 < surface.length && 
            surface[s1 + 1][j + t][k] != null)
            if ((surface[s1 + 1][j + t][k]).ladder) {
              if (t == 1 || (t == 0 && surface[s1 + 1][j + t + 2][k] == null)) {
                if ((surface[s1 + 1][j + t][k]).distance == Short.MAX_VALUE) {
                  toprocess.add(surface[s1 + 1][j + t][k]);
                  (surface[s1 + 1][j + t][k]).distance = (short)((surface[s1 + 1][j + t][k]).distance - 1);
                } 
                current.neighbors.add(surface[s1 + 1][j + t][k]);
              } 
            } else {
              if ((surface[s1 + 1][j + t][k]).distance == Short.MAX_VALUE) {
                toprocess.add(surface[s1 + 1][j + t][k]);
                (surface[s1 + 1][j + t][k]).distance = (short)((surface[s1 + 1][j + t][k]).distance - 1);
              } 
              if (t == 0) {
                current.neighbors.add(surface[s1 + 1][j + t][k]);
              } else if (t == 1 && surface[s1][j + 3][k] == null) {
                current.neighbors.add(surface[s1 + 1][j + t][k]);
              } else if (t == -1 && surface[s1 + 1][j + t + 3][k] == null) {
                current.neighbors.add(surface[s1 + 1][j + t][k]);
              } 
            }  
          if (s1 - 1 >= 0 && 
            surface[s1 - 1][j + t][k] != null)
            if ((surface[s1 - 1][j + t][k]).ladder) {
              if (t == 1 || (t == 0 && surface[s1 - 1][j + t + 2][k] == null)) {
                if ((surface[s1 - 1][j + t][k]).distance == Short.MAX_VALUE) {
                  toprocess.add(surface[s1 - 1][j + t][k]);
                  (surface[s1 - 1][j + t][k]).distance = (short)((surface[s1 - 1][j + t][k]).distance - 1);
                } 
                current.neighbors.add(surface[s1 - 1][j + t][k]);
              } 
            } else {
              if ((surface[s1 - 1][j + t][k]).distance == Short.MAX_VALUE) {
                toprocess.add(surface[s1 - 1][j + t][k]);
                (surface[s1 - 1][j + t][k]).distance = (short)((surface[s1 - 1][j + t][k]).distance - 1);
              } 
              if (t == 0) {
                current.neighbors.add(surface[s1 - 1][j + t][k]);
              } else if (t == 1 && surface[s1][j + 3][k] == null) {
                current.neighbors.add(surface[s1 - 1][j + t][k]);
              } else if (t == -1 && surface[s1 - 1][j + t + 3][k] == null) {
                current.neighbors.add(surface[s1 - 1][j + t][k]);
              } 
            }  
          if (k - 1 >= 0 && 
            surface[s1][j + t][k - 1] != null)
            if ((surface[s1][j + t][k - 1]).ladder) {
              if (t == 1 || (t == 0 && surface[s1][j + t + 2][k - 1] == null)) {
                if ((surface[s1][j + t][k - 1]).distance == Short.MAX_VALUE) {
                  toprocess.add(surface[s1][j + t][k - 1]);
                  (surface[s1][j + t][k - 1]).distance = (short)((surface[s1][j + t][k - 1]).distance - 1);
                } 
                current.neighbors.add(surface[s1][j + t][k - 1]);
              } 
            } else {
              if ((surface[s1][j + t][k - 1]).distance == Short.MAX_VALUE) {
                toprocess.add(surface[s1][j + t][k - 1]);
                (surface[s1][j + t][k - 1]).distance = (short)((surface[s1][j + t][k - 1]).distance - 1);
              } 
              if (t == 0) {
                current.neighbors.add(surface[s1][j + t][k - 1]);
              } else if (t == 1 && surface[s1][j + 3][k] == null) {
                current.neighbors.add(surface[s1][j + t][k - 1]);
              } else if (t == -1 && surface[s1][j + t + 3][k - 1] == null) {
                current.neighbors.add(surface[s1][j + t][k - 1]);
              } 
            }  
          if (k + 1 < (surface[0][0]).length && 
            surface[s1][j + t][k + 1] != null)
            if ((surface[s1][j + t][k + 1]).ladder) {
              if (t == 1 || (t == 0 && surface[s1][j + t + 2][k + 1] == null)) {
                if ((surface[s1][j + t][k + 1]).distance == Short.MAX_VALUE) {
                  toprocess.add(surface[s1][j + t][k + 1]);
                  (surface[s1][j + t][k + 1]).distance = (short)((surface[s1][j + t][k + 1]).distance - 1);
                } 
                current.neighbors.add(surface[s1][j + t][k + 1]);
              } 
            } else {
              if ((surface[s1][j + t][k + 1]).distance == Short.MAX_VALUE) {
                toprocess.add(surface[s1][j + t][k + 1]);
                (surface[s1][j + t][k + 1]).distance = (short)((surface[s1][j + t][k + 1]).distance - 1);
              } 
              if (t == 0) {
                current.neighbors.add(surface[s1][j + t][k + 1]);
              } else if (t == 1 && surface[s1][j + 3][k] == null) {
                current.neighbors.add(surface[s1][j + t][k + 1]);
              } else if (t == -1 && surface[s1][j + t + 3][k + 1] == null) {
                current.neighbors.add(surface[s1][j + t][k + 1]);
              } 
            }  
        } 
      } 
    } 
    Collections.sort(this.alltiles);
  }
  
  public boolean contains(short[] pos) {
    boolean contains = false;
    int targetkey = pos[0] + (pos[1] << 10) + (pos[2] << 20);
    int currentindex = this.alltiles.size() / 2;
    int change = currentindex;
    ExtendedPathTile current = this.alltiles.get(currentindex);
    if (current.key == ((ExtendedPathTile)this.alltiles.get(0)).key) {
      current = this.alltiles.get(0);
    } else {
      while (current.key != targetkey && change > 1) {
        if (current.key > targetkey) {
          currentindex -= change / 2;
          change = (change + 1) / 2;
        } 
        if (current.key < targetkey) {
          currentindex += change / 2;
          change = (change + 1) / 2;
        } 
        current = this.alltiles.get(currentindex);
      } 
    } 
    if (current.position[0] == pos[0] && current.position[1] == pos[1] && current.position[2] == pos[2])
      contains = true; 
    return contains;
  }
  
  public LinkedList<short[]> getPath(short[] start, short[] target) {
    LinkedList<short[]> way = (LinkedList)new LinkedList<>();
    int targetkey = target[0] + (target[1] << 10) + (target[2] << 20);
    int currentindex = this.alltiles.size() / 2;
    int change = currentindex;
    ExtendedPathTile current = this.alltiles.get(currentindex);
    if (targetkey == ((ExtendedPathTile)this.alltiles.get(0)).key) {
      current = this.alltiles.get(0);
    } else {
      while (current.key != targetkey && change > 1) {
        if (current.key > targetkey) {
          currentindex -= change / 2;
          change = (change + 1) / 2;
        } 
        if (current.key < targetkey) {
          currentindex += change / 2;
          change = (change + 1) / 2;
        } 
        current = this.alltiles.get(currentindex);
      } 
    } 
    LinkedList<ExtendedPathTile> processing = new LinkedList<>();
    LinkedList<ExtendedPathTile> processing2 = new LinkedList<>();
    Boolean wayfound = Boolean.valueOf(false);
    if (current.position[0] == target[0] && current.position[1] == target[1] && current.position[2] == target[2]) {
      processing.add(current);
      processing2.add(current);
      current.distance = 0;
    } else {
      return null;
    } 
    while (!processing.isEmpty()) {
      current = processing.pollFirst();
      for (int i = 0; i < current.neighbors.size(); i++) {
        if (((ExtendedPathTile)current.neighbors.get(i)).distance > current.distance + 1) {
          ((ExtendedPathTile)current.neighbors.get(i)).distance = (short)(current.distance + 1);
          processing.add(current.neighbors.get(i));
          processing2.add(current.neighbors.get(i));
        } 
      } 
      if (current.position[0] == start[0] && current.position[1] == start[1] && current.position[2] == start[2]) {
        wayfound = Boolean.valueOf(true);
        break;
      } 
    } 
    ExtendedPathTile nexttile = current;
    if (wayfound.booleanValue()) {
      way.addLast(current.position);
      while (current.distance > 0) {
        for (int i = 0; i < current.neighbors.size(); i++) {
          if (((ExtendedPathTile)current.neighbors.get(i)).distance < nexttile.distance)
            nexttile = current.neighbors.get(i); 
        } 
        current = nexttile;
        way.addLast(current.position);
      } 
    } 
    while (!processing2.isEmpty()) {
      current = processing2.pollFirst();
      current.distance = 32766;
    } 
    return way;
  }
}
