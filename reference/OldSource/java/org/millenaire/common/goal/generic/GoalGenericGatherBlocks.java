package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericGatherBlocks extends GoalGeneric {
  public static final String GOAL_TYPE = "gatherblocks";
  
  @ConfigField(type = AnnotedParameter.ParameterType.BONUS_ITEM_ADD)
  @FieldDocumentation(explanation = "Item to be harvested, with chance.")
  public List<AnnotedParameter.BonusItem> harvestItem = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCKSTATE)
  @FieldDocumentation(explanation = "Blockstate to gather.")
  public IBlockState gatherBlockState = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCKSTATE)
  @FieldDocumentation(explanation = "Blockstate to place instead of the 'gathered' block. If null, the block will be left as-is.")
  public IBlockState resultingBlockState = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "4")
  @FieldDocumentation(explanation = "Minimum number of available blocks in a building for the goal to start.")
  public Integer minimumAvailableBlocks;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "Whether to store the collected goods in the destination building (if false, they go in the villager's inventory).")
  public boolean collectInBuilding;
  
  public void applyDefaultSettings() {
    this.duration = 2;
    this.lookAtGoal = true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
    List<Point> vp = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    List<Building> buildings = getBuildings(villager);
    for (Building buildingDest : buildings) {
      if (getTargetCount(buildingDest) >= this.minimumAvailableBlocks.intValue()) {
        Point point = getTargetLocation(buildingDest);
        if (point != null) {
          vp.add(point);
          buildingp.add(buildingDest.getPos());
        } 
      } 
    } 
    if (vp.isEmpty())
      return null; 
    Point p = vp.get(0);
    Point buildingP = buildingp.get(0);
    for (int i = 1; i < vp.size(); i++) {
      if (((Point)vp.get(i)).horizontalDistanceToSquared((Entity)villager) < p.horizontalDistanceToSquared((Entity)villager)) {
        p = vp.get(i);
        buildingP = buildingp.get(i);
      } 
    } 
    return packDest(p, buildingP);
  }
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    if (!this.harvestItem.isEmpty())
      return ((AnnotedParameter.BonusItem)this.harvestItem.get(0)).item.getItemStack(); 
    return new ItemStack(this.gatherBlockState.getBlock(), 1);
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_CHOPLUMBER_NO_LEAVES; 
    return JPS_CONFIG_CHOPLUMBER;
  }
  
  private int getTargetCount(Building target) {
    int nb = 0;
    for (int x = target.location.minx - 3; x < target.location.maxx + 3; x++) {
      for (int y = target.location.pos.getiY() - 1; y < target.location.pos.getiY() + 10; y++) {
        for (int z = target.location.minz - 3; z < target.location.maxz + 3; z++) {
          if (WorldUtilities.getBlockState(target.world, x, y, z) == this.gatherBlockState)
            nb++; 
        } 
      } 
    } 
    return nb;
  }
  
  private Point getTargetLocation(Building building) {
    for (int xPos = building.location.minx - 3; xPos < building.location.maxx + 3; xPos++) {
      for (int yPos = building.location.miny - 1; yPos < building.location.maxy + 20; yPos++) {
        for (int zPos = building.location.minz - 3; zPos < building.location.maxz + 3; zPos++) {
          if (WorldUtilities.getBlockState(building.world, xPos, yPos, zPos) == this.gatherBlockState)
            return new Point(xPos, yPos, zPos); 
        } 
      } 
    } 
    return null;
  }
  
  public String getTypeLabel() {
    return "gatherblocks";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean performAction(MillVillager villager) {
    if (villager.getGoalDestPoint().getBlockActualState(villager.world) == this.gatherBlockState) {
      for (AnnotedParameter.BonusItem bonusItem : this.harvestItem) {
        if (MillCommonUtilities.randomInt(100) <= bonusItem.chance) {
          if (this.collectInBuilding) {
            villager.getGoalBuildingDest().storeGoods(bonusItem.item, 1);
            continue;
          } 
          villager.addToInv(bonusItem.item, 1);
        } 
      } 
      if (this.resultingBlockState != null)
        villager.setBlockstate(villager.getGoalDestPoint(), this.resultingBlockState); 
      villager.swingArm(EnumHand.MAIN_HAND);
    } 
    if (isDestPossibleSpecific(villager, villager.getGoalBuildingDest())) {
      try {
        villager.setGoalInformation(getDestination(villager));
      } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
        MillLog.printException((Throwable)e);
      } 
      return false;
    } 
    return true;
  }
  
  public int range(MillVillager villager) {
    return 8;
  }
  
  public boolean validateGoal() {
    if (this.gatherBlockState == null) {
      MillLog.error(this, "The gather block state is mandatory in custom gather block goals.");
      return false;
    } 
    return true;
  }
}
