package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;

@Documentation("Seek out mobs around the village and attack them.")
public class GoalHuntMonster extends Goal {
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(villager.world, EntityMob.class, villager.getTownHall().getPos(), 50, 10);
    if (mobs == null)
      return null; 
    int bestDist = Integer.MAX_VALUE;
    Entity target = null;
    for (Entity ent : mobs) {
      if (ent instanceof EntityMob && !(ent instanceof net.minecraft.entity.monster.EntityCreeper) && 
        villager.getPos().distanceToSquared(ent) < bestDist && villager.getTownHall().getAltitude((int)ent.posX, (int)ent.posZ) < ent.posY) {
        target = ent;
        bestDist = (int)villager.getPos().distanceToSquared(ent);
      } 
    } 
    if (target == null)
      return null; 
    return packDest(null, null, target);
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
    return new ItemStack[] { villager.getWeapon() };
  }
  
  public boolean isFightingGoal() {
    return true;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    if (villager.world.getWorldTime() % 10L == 0L)
      setVillagerDest(villager); 
    return (villager.getGoalDestPoint() != null);
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(villager.world, EntityMob.class, villager.getPos(), 4, 4);
    for (Entity ent : mobs) {
      if (!ent.isDead && ent instanceof EntityMob && villager.canEntityBeSeen(ent)) {
        EntityMob mob = (EntityMob)ent;
        villager.setAttackTarget((EntityLivingBase)mob);
        if (MillConfigValues.LogGeneralAI >= 1)
          MillLog.major(this, "Attacking entity: " + ent); 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 50;
  }
}
