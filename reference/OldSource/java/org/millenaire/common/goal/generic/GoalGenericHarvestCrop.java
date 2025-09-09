package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericHarvestCrop extends GoalGeneric {
  public static final String GOAL_TYPE = "harvesting";
  
  public static int getCropBlockRipeMeta(ResourceLocation cropType) {
    return 7;
  }
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCK_ID)
  @FieldDocumentation(explanation = "Type of plant to harvest.")
  public ResourceLocation cropType = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BONUS_ITEM_ADD)
  @FieldDocumentation(explanation = "Item to be harvested, with chance.")
  public List<AnnotedParameter.BonusItem> harvestItem = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Boons for irrigated villages.")
  public InvItem irrigationBonusCrop = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCKSTATE)
  @FieldDocumentation(explanation = "Blockstate the crop must have to be harvested. If not set, must have a meta of 7.")
  public IBlockState harvestBlockState = null;
  
  public void applyDefaultSettings() {
    this.duration = 2;
    this.lookAtGoal = true;
    this.tags.add("tag_agriculture");
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
    Point dest = null;
    Building destBuilding = null;
    List<Building> buildings = getBuildings(villager);
    for (Building buildingDest : buildings) {
      if (isDestPossible(villager, buildingDest)) {
        List<Point> soils = buildingDest.getResManager().getSoilPoints(this.cropType);
        if (soils != null)
          for (Point p : soils) {
            if (isValidHarvestSoil(villager.world, p) && (
              dest == null || p.distanceTo((Entity)villager) < dest.distanceTo((Entity)villager))) {
              dest = p.getAbove();
              destBuilding = buildingDest;
            } 
          }  
      } 
    } 
    if (dest == null)
      return null; 
    return packDest(dest, destBuilding);
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    return villager.getBestHoeStack();
  }
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    if (!this.harvestItem.isEmpty())
      return ((AnnotedParameter.BonusItem)this.harvestItem.get(0)).item.getItemStack(); 
    return null;
  }
  
  public String getTypeLabel() {
    return "harvesting";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  private boolean isValidHarvestSoil(World world, Point p) {
    if (this.harvestBlockState != null)
      return (p.getAbove().getBlockActualState(world) == this.harvestBlockState); 
    return (p.getAbove().getBlock(world) == Block.REGISTRY.getObject(this.cropType) && p.getAbove().getMeta(world) == getCropBlockRipeMeta(this.cropType));
  }
  
  public boolean performAction(MillVillager villager) {
    if (isValidHarvestSoil(villager.world, villager.getGoalDestPoint().getBelow())) {
      if (this.irrigationBonusCrop != null) {
        float irrigation = villager.getTownHall().getVillageIrrigation();
        double rand = Math.random();
        if (rand < (irrigation / 100.0F))
          villager.addToInv(this.irrigationBonusCrop, 1); 
      } 
      Building dest = villager.getGoalBuildingDest();
      for (AnnotedParameter.BonusItem bonusItem : this.harvestItem) {
        if ((bonusItem.tag == null || (dest != null && dest.containsTags(bonusItem.tag))) && 
          MillCommonUtilities.randomInt(100) <= bonusItem.chance)
          villager.addToInv(bonusItem.item, 1); 
      } 
      villager.setBlockAndMetadata(villager.getGoalDestPoint(), Blocks.AIR, 0);
      if (villager.getBlock(villager.getGoalDestPoint().getAbove()) instanceof net.minecraft.block.BlockDoublePlant)
        villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(), Blocks.AIR, 0); 
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
  
  public int priority(MillVillager villager) throws MillLog.MillenaireException {
    Goal.GoalInformation info = getDestination(villager);
    if (info == null || info.getDest() == null)
      return -1; 
    return (int)(1000.0D - villager.getPos().distanceTo(info.getDest()));
  }
  
  public boolean validateGoal() {
    if (this.cropType == null) {
      MillLog.error(this, "The croptype is mandatory in custom harvest goals.");
      return false;
    } 
    return true;
  }
}
