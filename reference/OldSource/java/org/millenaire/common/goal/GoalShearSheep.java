package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

@Documentation("Sheer sheeps present around the villager's house.")
public class GoalShearSheep extends Goal {
  public GoalShearSheep() {
    this.buildingLimit.put(InvItem.createInvItem(Blocks.WOOL, 0), Integer.valueOf(1024));
    this.townhallLimit.put(InvItem.createInvItem(Blocks.WOOL, 0), Integer.valueOf(1024));
    this.icon = InvItem.createInvItem((Item)Items.SHEARS);
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    Point pos = villager.getPos();
    Entity closestSheep = null;
    double sheepBestDist = Double.MAX_VALUE;
    List<Entity> sheep = WorldUtilities.getEntitiesWithinAABB(villager.world, EntitySheep.class, villager.getHouse().getPos(), 30, 10);
    for (Entity ent : sheep) {
      if (!((EntitySheep)ent).getSheared() && !((EntitySheep)ent).isChild() && (
        closestSheep == null || pos.distanceTo(ent) < sheepBestDist)) {
        closestSheep = ent;
        sheepBestDist = pos.distanceTo(ent);
      } 
    } 
    if (closestSheep != null)
      return packDest(null, villager.getHouse(), closestSheep); 
    return null;
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_WIDE_NO_LEAVES; 
    return JPS_CONFIG_WIDE;
  }
  
  public boolean isFightingGoal() {
    return true;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    if (!villager.getHouse().containsTags("sheeps"))
      return false; 
    List<Entity> sheep = WorldUtilities.getEntitiesWithinAABB(villager.world, EntitySheep.class, villager.getHouse().getPos(), 30, 10);
    if (sheep == null)
      return false; 
    for (Entity ent : sheep) {
      EntitySheep asheep = (EntitySheep)ent;
      if (!asheep.isChild() && !asheep.isDead)
        if (!((EntitySheep)ent).getSheared())
          return true;  
    } 
    return false;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    List<Entity> sheep = WorldUtilities.getEntitiesWithinAABB(villager.world, EntitySheep.class, villager.getPos(), 4, 4);
    for (Entity ent : sheep) {
      if (!ent.isDead) {
        EntitySheep animal = (EntitySheep)ent;
        if (!animal.isChild() && 
          !animal.getSheared()) {
          villager.addToInv(Blocks.WOOL, ((EntitySheep)ent).getFleeceColor().getMetadata(), 3);
          ((EntitySheep)ent).setSheared(true);
          if (MillConfigValues.LogCattleFarmer >= 1 && villager.extraLog)
            MillLog.major(this, "Shearing: " + ent); 
          villager.swingArm(EnumHand.MAIN_HAND);
        } 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 50;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
}
