package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@Documentation("Go and mine rocks etc at the villager's house.")
public class GoalMinerMineResource extends Goal {
  private static final ItemStack[] IS_ULU = new ItemStack[] { new ItemStack((Item)MillItems.ULU, 1) };
  
  public String buildingTag = null;
  
  public GoalMinerMineResource() {
    this.icon = InvItem.createInvItem(Items.IRON_PICKAXE);
  }
  
  public int actionDuration(MillVillager villager) {
    Block block = villager.getBlock(villager.getGoalDestPoint());
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
  
  public List<Building> getBuildings(MillVillager villager) {
    List<Building> buildings = new ArrayList<>();
    if (this.buildingTag == null) {
      buildings.add(villager.getHouse());
    } else {
      for (Building b : villager.getTownHall().getBuildingsWithTag(this.buildingTag))
        buildings.add(b); 
    } 
    return buildings;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Building> buildings = getBuildings(villager);
    List<Point> validSources = new ArrayList<>();
    List<Building> validDests = new ArrayList<>();
    for (Building possibleDest : buildings) {
      List<CopyOnWriteArrayList<Point>> sources = (possibleDest.getResManager()).sources;
      for (int i = 0; i < sources.size(); i++) {
        IBlockState sourceBlockState = (possibleDest.getResManager()).sourceTypes.get(i);
        for (int j = 0; j < ((CopyOnWriteArrayList)sources.get(i)).size(); j++) {
          IBlockState actualBlockState = WorldUtilities.getBlockState(villager.world, ((CopyOnWriteArrayList<Point>)sources.get(i)).get(j));
          if (actualBlockState == sourceBlockState) {
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
    Block targetBlock = villager.getBlock(villager.getGoalDestPoint());
    if (targetBlock == Blocks.SAND || targetBlock == Blocks.CLAY || targetBlock == Blocks.GRAVEL)
      return villager.getBestShovelStack(); 
    if (targetBlock == Blocks.SNOW_LAYER || targetBlock == Blocks.ICE)
      return IS_ULU; 
    return villager.getBestPickaxeStack();
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_WIDE_NO_LEAVES; 
    return JPS_CONFIG_WIDE;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    IBlockState blockState = WorldUtilities.getBlockState(villager.world, villager.getGoalDestPoint());
    Block block = blockState.getBlock();
    if (block == Blocks.SAND) {
      villager.addToInv((Block)Blocks.SAND, 1);
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), (Block)Blocks.SAND, 1.0F);
      if (MillConfigValues.LogMiner >= 3 && villager.extraLog)
        MillLog.debug(this, "Gathered sand at: " + villager.getGoalDestPoint()); 
    } else if (block == Blocks.STONE) {
      if (blockState.getValue((IProperty)BlockStone.VARIANT) == BlockStone.EnumType.STONE) {
        villager.addToInv(Blocks.COBBLESTONE, 1);
      } else {
        villager.addToInv(blockState, 1);
      } 
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), Blocks.STONE, 1.0F);
      if (MillConfigValues.LogMiner >= 3 && villager.extraLog)
        MillLog.debug(this, "Gather cobblestone at: " + villager.getGoalDestPoint()); 
    } else if (block == Blocks.SANDSTONE) {
      villager.addToInv(Blocks.SANDSTONE, 1);
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), Blocks.SANDSTONE, 1.0F);
      if (MillConfigValues.LogMiner >= 3 && villager.extraLog)
        MillLog.debug(this, "Gather sand stone at: " + villager.getGoalDestPoint()); 
    } else if (block == Blocks.CLAY) {
      villager.addToInv(Items.CLAY_BALL, 1);
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), Blocks.CLAY, 1.0F);
      if (MillConfigValues.LogMiner >= 3 && villager.extraLog)
        MillLog.debug(this, "Gather clay at: " + villager.getGoalDestPoint()); 
    } else if (block == Blocks.GRAVEL) {
      villager.addToInv(Blocks.GRAVEL, 1);
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), Blocks.GRAVEL, 1.0F);
      if (MillConfigValues.LogMiner >= 3 && villager.extraLog)
        MillLog.debug(this, "Gather gravel at: " + villager.getGoalDestPoint()); 
    } else if (block == Blocks.SNOW_LAYER) {
      villager.addToInv((Block)MillBlocks.SNOW_BRICK, 1);
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), Blocks.SNOW, 1.0F);
      if (MillConfigValues.LogMiner >= 3)
        MillLog.debug(this, "Gather snow at: " + villager.getGoalDestPoint()); 
    } else if (block == Blocks.ICE) {
      villager.addToInv((Block)MillBlocks.ICE_BRICK, 1);
      WorldUtilities.playSoundBlockBreaking(villager.world, villager.getGoalDestPoint(), Blocks.ICE, 1.0F);
      if (MillConfigValues.LogMiner >= 3)
        MillLog.debug(this, "Gather ice at: " + villager.getGoalDestPoint()); 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 30;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
  
  public boolean stuckAction(MillVillager villager) throws Exception {
    return performAction(villager);
  }
  
  public boolean swingArms() {
    return true;
  }
}
