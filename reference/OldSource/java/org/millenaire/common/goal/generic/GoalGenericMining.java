package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericMining extends GoalGeneric {
  private static final ItemStack[] IS_ULU = new ItemStack[] { new ItemStack((Item)MillItems.ULU, 1) };
  
  public static final String GOAL_TYPE = "mining";
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCKSTATE)
  @FieldDocumentation(explanation = "Blockstate of the source, like stone (not necessarily the block being harvest - stone gives cobblestone for example).")
  public IBlockState sourceBlockState = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "loot")
  @FieldDocumentation(explanation = "Blocks or items  gained when mining.")
  public Map<InvItem, Integer> loots = new HashMap<>();
  
  public int actionDuration(MillVillager villager) {
    Block block = this.sourceBlockState.getBlock();
    if (block == Blocks.STONE || block == Blocks.SANDSTONE) {
      int toolEfficiency = (int)villager.getBestPickaxe().getDestroySpeed(new ItemStack((Item)villager.getBestPickaxe(), 1), Blocks.SANDSTONE.getDefaultState());
      return 140 - 4 * toolEfficiency;
    } 
    if (block == Blocks.SAND || block == Blocks.CLAY || block == Blocks.GRAVEL) {
      int toolEfficiency = (int)villager.getBestShovel().getDestroySpeed(new ItemStack((Item)villager.getBestShovel(), 1), Blocks.SAND.getDefaultState());
      return 140 - 4 * toolEfficiency;
    } 
    return 70;
  }
  
  public void applyDefaultSettings() {
    this.lookAtGoal = true;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Building> buildings = getBuildings(villager);
    List<Point> validSources = new ArrayList<>();
    List<Building> validDests = new ArrayList<>();
    for (Building possibleDest : buildings) {
      List<CopyOnWriteArrayList<Point>> sources = (possibleDest.getResManager()).sources;
      for (int i = 0; i < sources.size(); i++) {
        if (this.sourceBlockState == (possibleDest.getResManager()).sourceTypes.get(i))
          for (int j = 0; j < ((CopyOnWriteArrayList)sources.get(i)).size(); j++) {
            IBlockState actualBlockState = WorldUtilities.getBlockState(villager.world, ((CopyOnWriteArrayList<Point>)sources.get(i)).get(j));
            if (actualBlockState == this.sourceBlockState) {
              validSources.add(((CopyOnWriteArrayList<Point>)sources.get(i)).get(j));
              validDests.add(possibleDest);
            } 
          }  
      } 
    } 
    if (validSources.isEmpty())
      return null; 
    int randomTarget = MillCommonUtilities.randomInt(validSources.size());
    return packDest(validSources.get(randomTarget), validDests.get(randomTarget));
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    Block targetBlock = this.sourceBlockState.getBlock();
    if (targetBlock == Blocks.SAND || targetBlock == Blocks.CLAY || targetBlock == Blocks.GRAVEL)
      return villager.getBestShovelStack(); 
    if (targetBlock == Blocks.SNOW_LAYER || targetBlock == Blocks.ICE)
      return IS_ULU; 
    return villager.getBestPickaxeStack();
  }
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    if (this.sourceBlockState != null)
      return new ItemStack(this.sourceBlockState.getBlock(), 1, this.sourceBlockState.getBlock().getMetaFromState(this.sourceBlockState)); 
    return null;
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_WIDE_NO_LEAVES; 
    return JPS_CONFIG_WIDE;
  }
  
  public String getTypeLabel() {
    return "mining";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    for (InvItem key : this.loots.keySet()) {
      villager.addToInv(key, ((Integer)this.loots.get(key)).intValue());
      if (MillConfigValues.LogMiner >= 3 && villager.extraLog)
        MillLog.debug(this, "Gathered " + key + " at: " + villager.getGoalDestPoint()); 
    } 
    WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), this.sourceBlockState.getBlock(), 1.0F);
    return true;
  }
  
  public boolean stuckAction(MillVillager villager) throws Exception {
    return performAction(villager);
  }
  
  public boolean swingArms() {
    return true;
  }
  
  public boolean validateGoal() {
    return true;
  }
}
