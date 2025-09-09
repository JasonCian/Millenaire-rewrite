package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericPlantSapling extends GoalGeneric {
  public static final String GOAL_TYPE = "plantsapling";
  
  public void applyDefaultSettings() {
    this.duration = 2;
    this.lookAtGoal = true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
    List<Building> buildings = getBuildings(villager);
    List<Point> vp = new ArrayList<>();
    List<Point> buildingp = new ArrayList<>();
    for (Building grove : buildings) {
      Point point = grove.getResManager().getPlantingLocation();
      if (point != null) {
        vp.add(point);
        buildingp.add(grove.getPos());
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
    return InvItem.createInvItem(Blocks.SAPLING).getItemStack();
  }
  
  public String getTypeLabel() {
    return "plantsapling";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean performAction(MillVillager villager) {
    Block block = WorldUtilities.getBlock(villager.world, villager.getGoalDestPoint());
    if (block == Blocks.AIR || block == Blocks.SNOW_LAYER || (BlockItemUtilities.isBlockDecorativePlant(block) && !(block instanceof BlockSapling))) {
      String saplingType = villager.getGoalBuildingDest().getResManager().getPlantingLocationType(villager.getGoalDestPoint());
      IBlockState saplingBS = Blocks.SAPLING.getDefaultState();
      if ("pinespawn".equals(saplingType)) {
        saplingBS = Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.SPRUCE);
      } else if ("birchspawn".equals(saplingType)) {
        saplingBS = Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.BIRCH);
      } else if ("junglespawn".equals(saplingType)) {
        saplingBS = Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.JUNGLE);
      } else if ("acaciaspawn".equals(saplingType)) {
        saplingBS = Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.ACACIA);
      } else if ("darkoakspawn".equals(saplingType)) {
        saplingBS = Blocks.SAPLING.getDefaultState().withProperty((IProperty)BlockSapling.TYPE, (Comparable)BlockPlanks.EnumType.DARK_OAK);
      } else if ("appletreespawn".equals(saplingType)) {
        saplingBS = MillBlocks.SAPLING_APPLETREE.getDefaultState();
      } else if ("olivetreespawn".equals(saplingType)) {
        saplingBS = MillBlocks.SAPLING_OLIVETREE.getDefaultState();
      } else if ("pistachiotreespawn".equals(saplingType)) {
        saplingBS = MillBlocks.SAPLING_PISTACHIO.getDefaultState();
      } else if ("cherrytreespawn".equals(saplingType)) {
        saplingBS = MillBlocks.SAPLING_CHERRY.getDefaultState();
      } else if ("sakuratreespawn".equals(saplingType)) {
        saplingBS = MillBlocks.SAPLING_SAKURA.getDefaultState();
      } 
      villager.takeFromInv(saplingBS, 1);
      villager.setBlockstate(villager.getGoalDestPoint(), saplingBS);
      villager.swingArm(EnumHand.MAIN_HAND);
      if (MillConfigValues.LogLumberman >= 3 && villager.extraLog)
        MillLog.debug(this, "Planted at: " + villager.getGoalDestPoint()); 
    } else if (MillConfigValues.LogLumberman >= 3 && villager.extraLog) {
      MillLog.debug(this, "Failed to plant at: " + villager.getGoalDestPoint());
    } 
    return true;
  }
  
  public boolean validateGoal() {
    return true;
  }
}
