package org.millenaire.common.culture;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;

public class WallType {
  public static Map<String, WallType> loadWalls(VirtualDir cultureVirtualDir, Culture culture) {
    VirtualDir wallsVirtualDir = cultureVirtualDir.getChildDirectory("walls");
    Map<String, WallType> walls = new HashMap<>();
    for (File file : wallsVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"))) {
      try {
        if (MillConfigValues.LogVillage >= 1)
          MillLog.major(file, "Loading wall: " + file.getAbsolutePath()); 
        WallType wall = loadWallType(file, culture);
        walls.put(wall.key, wall);
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
    return walls;
  }
  
  private static WallType loadWallType(File file, Culture c) {
    WallType wallType = new WallType(c, file.getName().split("\\.")[0]);
    try {
      ParametersManager.loadAnnotedParameterData(file, wallType, null, "wall type", c);
      validateVillageWalls(wallType);
      return wallType;
    } catch (Exception e) {
      MillLog.printException(e);
      return null;
    } 
  }
  
  private static void validateVillageWalls(WallType wallType) {
    if (wallType.villageWall != null && (wallType.villageWallGateway == null || wallType.villageWallCorner == null)) {
      MillLog.error(wallType, "For a village to have walls, it must have gateways and corner buildings. Disabling walls");
      wallType.villageWall = null;
    } 
    if (wallType.villageWallTower == null)
      wallType.villageWallsBetweenTowers = Integer.MAX_VALUE; 
  }
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall")
  @FieldDocumentation(explanation = "Walls to surround the village.", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWall = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_tower")
  @FieldDocumentation(explanation = "Towers for the village walls.", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallTower = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_gate")
  @FieldDocumentation(explanation = "Gateways for the village wall", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallGateway = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_corner")
  @FieldDocumentation(explanation = "Corners for the village wall", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallCorner = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_cap_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that stops on the right (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallCapRight = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_cap_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that stops on the left (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallCapLeft = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_cap_both")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that stops on both sides", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallCapBoth = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope1_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the left by 1 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope1Left = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope1_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the right by 1 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope1Right = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope2_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the left by 2 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope2Left = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope2_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the right by 2 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope2Right = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope3_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the left by 3 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope3Left = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope3_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the right by 3 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope3Right = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope4_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the left by 4 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope4Left = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope4_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the right by 4 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope4Right = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope5_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the left by 5 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope5Left = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope5_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the right by 5 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope5Right = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope6_left")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the left by 6 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope6Left = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BUILDING, paramName = "village_wall_slope6_right")
  @FieldDocumentation(explanation = "Optional alternate plan for the wall that rises on the right by 6 (seen from outside)", explanationCategory = "Village Walls")
  public BuildingPlanSet villageWallSlope6Right = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "village_wall_spawn", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether to spawn the first level of walls", explanationCategory = "Village Walls")
  public boolean villageWallSpawn;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "village_wall_tower_spawn", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether to spawn the first level of wall towers", explanationCategory = "Village Walls")
  public boolean villageWallTowerSpawn;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "village_wall_gate_spawn", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether to spawn the first level of wall gateways", explanationCategory = "Village Walls")
  public boolean villageWallGatewaySpawn;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "village_wall_corner_spawn", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether to spawn the first level of wall corner towers", explanationCategory = "Village Walls")
  public boolean villageWallCornerSpawn;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "village_wall_cap_spawn", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether to spawn the first level of wall caps", explanationCategory = "Village Walls")
  public boolean villageWallCapSpawn;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, paramName = "village_wall_nb_between_towers", defaultValue = "3")
  @FieldDocumentation(explanation = "How many wall segments to place between every towers. If no walls, space in block.", explanationCategory = "Village Walls")
  public int villageWallsBetweenTowers;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, paramName = "nb_smooth_runs", defaultValue = "3")
  @FieldDocumentation(explanation = "How many times the smoothing algorithm should be applied. The more times, the 'flatter' walls will be.", explanationCategory = "Village Walls")
  public int nbSmoothRuns;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, paramName = "max_y_delta", defaultValue = "15")
  @FieldDocumentation(explanation = "Maximum difference between a wall segment's Y position and the town hall where a wall will be built.", explanationCategory = "Village Walls")
  public int maxYDelta;
  
  public String key = null;
  
  public Culture culture;
  
  public WallType(Culture c, String key) {
    this.key = key;
    this.culture = c;
  }
  
  public String toString() {
    return "Wall Type:" + this.culture.key + ":" + this.key;
  }
}
