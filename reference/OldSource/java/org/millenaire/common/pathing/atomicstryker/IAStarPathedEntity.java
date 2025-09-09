package org.millenaire.common.pathing.atomicstryker;

import java.util.List;

public interface IAStarPathedEntity {
  void onFoundPath(List<AStarNode> paramList);
  
  void onNoPathAvailable();
}
