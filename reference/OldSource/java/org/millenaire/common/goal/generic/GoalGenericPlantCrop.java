package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
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
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GoalGenericPlantCrop extends GoalGeneric {
  public static final String GOAL_TYPE = "planting";
  
  public static int getCropBlockMeta(ResourceLocation cropType2) {
    return 0;
  }
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCK_ID)
  @FieldDocumentation(explanation = "Type of plant to plant.")
  public ResourceLocation cropType = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCKSTATE_ADD)
  @FieldDocumentation(explanation = "Blockstate to plant. If not set, defaults to cropType. If more than one set, picks one at random.")
  public List<IBlockState> plantBlockState = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Seed item that gets consumed when planting.")
  public InvItem seed = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BLOCK_ID, defaultValue = "minecraft:farmland")
  @FieldDocumentation(explanation = "Block to set below the crop.")
  public ResourceLocation soilType = null;
  
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
            if (isValidPlantingLocation(villager.world, p) && (
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
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    if (this.seed != null)
      return this.seed.getItemStack(); 
    if (this.heldItems != null && this.heldItems.length > 0)
      return this.heldItems[0]; 
    return null;
  }
  
  public String getTypeLabel() {
    return "planting";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    if (this.seed != null && b.countGoods(this.seed) + villager.countInv(this.seed) == 0)
      return false; 
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  private boolean isValidPlantingLocation(World world, Point p) {
    Block blockTwoAbove = p.getAbove().getAbove().getBlock(world);
    Block blockAbove = p.getAbove().getBlock(world);
    Block farmBlock = p.getBlock(world);
    if ((blockAbove == Blocks.AIR || blockAbove == Blocks.SNOW || blockAbove == Blocks.LEAVES) && (blockTwoAbove == Blocks.AIR || blockTwoAbove == Blocks.SNOW || blockTwoAbove == Blocks.LEAVES) && (farmBlock == Blocks.GRASS || farmBlock == Blocks.DIRT || farmBlock == Blocks.FARMLAND))
      return true; 
    if (BlockItemUtilities.isBlockDecorativePlant(blockAbove)) {
      if (!this.cropType.equals(Mill.CROP_FLOWER))
        return true; 
      if (blockAbove != Blocks.RED_FLOWER && blockAbove != Blocks.YELLOW_FLOWER && blockAbove != Blocks.DOUBLE_PLANT)
        return true; 
    } 
    return false;
  }
  
  public boolean performAction(MillVillager villager) {
    Building dest = villager.getGoalBuildingDest();
    if (dest == null)
      return true; 
    if (!isValidPlantingLocation(villager.world, villager.getGoalDestPoint().getBelow()))
      return true; 
    if (this.seed != null) {
      int taken = villager.takeFromInv(this.seed, 1);
      if (taken == 0)
        dest.takeGoods(this.seed, 1); 
    } 
    Block soil = (Block)Block.REGISTRY.getObject(this.soilType);
    if (villager.getGoalDestPoint().getBelow().getBlock(villager.world) != soil)
      villager.setBlockAndMetadata(villager.getGoalDestPoint().getBelow(), soil, 0); 
    if (!this.plantBlockState.isEmpty()) {
      IBlockState cropState = this.plantBlockState.get(MillCommonUtilities.randomInt(this.plantBlockState.size()));
      villager.setBlockstate(villager.getGoalDestPoint(), cropState);
      if (cropState.getBlock() instanceof BlockDoublePlant)
        villager.setBlockstate(villager.getGoalDestPoint().getAbove(), cropState.withProperty((IProperty)BlockDoublePlant.HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER)); 
    } else {
      Block cropBlock = (Block)Block.REGISTRY.getObject(this.cropType);
      int cropMeta = getCropBlockMeta(this.cropType);
      villager.setBlockAndMetadata(villager.getGoalDestPoint(), cropBlock, cropMeta);
      if (cropBlock instanceof BlockDoublePlant || cropBlock instanceof org.millenaire.common.block.BlockGrapeVine)
        villager.setBlockAndMetadata(villager.getGoalDestPoint().getAbove(), cropBlock, cropMeta | 0x8); 
    } 
    villager.swingArm(EnumHand.MAIN_HAND);
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
    return (int)(100.0D - villager.getPos().distanceTo(info.getDest()));
  }
  
  public boolean validateGoal() {
    if (this.cropType == null) {
      MillLog.error(this, "The croptype is mandatory in custom planting goals.");
      return false;
    } 
    return true;
  }
}
