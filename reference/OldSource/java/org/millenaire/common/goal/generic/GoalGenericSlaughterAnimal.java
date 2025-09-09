package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericSlaughterAnimal extends GoalGeneric {
  public static final String GOAL_TYPE = "slaughteranimal";
  
  @ConfigField(type = AnnotedParameter.ParameterType.ENTITY_ID)
  @FieldDocumentation(explanation = "The animal to be targeted.")
  public ResourceLocation animalKey = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BONUS_ITEM_ADD)
  @FieldDocumentation(explanation = "Extra item drop the villager can get.")
  public List<AnnotedParameter.BonusItem> bonusItem = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN)
  @FieldDocumentation(explanation = "If true, the villager will slaughter animals until only half the reference amount (the number of spawn points) is left.")
  public boolean aggressiveSlaughter = false;
  
  public void applyDefaultSettings() {
    this.duration = 2;
    this.lookAtGoal = true;
    this.icon = InvItem.createInvItem(Items.IRON_AXE);
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    Point pos = villager.getPos();
    Entity closest = null;
    Building destBuilding = null;
    double bestDist = Double.MAX_VALUE;
    List<Building> buildings = getBuildings(villager);
    for (Building dest : buildings) {
      if (isDestPossible(villager, dest)) {
        List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.world, EntityList.getClass(this.animalKey), dest.getPos(), 15, 10);
        for (Entity ent : animals) {
          if (!ent.isDead && !isEntityChild(ent) && (
            closest == null || pos.distanceTo(ent) < bestDist)) {
            closest = ent;
            destBuilding = dest;
            bestDist = pos.distanceTo(ent);
          } 
        } 
      } 
    } 
    if (closest == null)
      return null; 
    return packDest(null, destBuilding, closest);
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves()) {
      if (this.animalKey.equals(Mill.ENTITY_SQUID))
        return JPS_CONFIG_SLAUGHTERSQUIDS_NO_LEAVES; 
      return JPS_CONFIG_TIGHT_NO_LEAVES;
    } 
    if (this.animalKey.equals(Mill.ENTITY_SQUID))
      return JPS_CONFIG_SLAUGHTERSQUIDS; 
    return JPS_CONFIG_TIGHT;
  }
  
  public String getTypeLabel() {
    return "slaughteranimal";
  }
  
  public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
    List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.world, EntityList.getClass(this.animalKey), b.getPos(), 25, 10);
    if (animals == null)
      return false; 
    int nbanimals = 0;
    for (Entity ent : animals) {
      if (!ent.isDead && !isEntityChild(ent))
        nbanimals++; 
    } 
    int targetAnimals = 0;
    for (int i = 0; i < (b.getResManager()).spawns.size(); i++) {
      if (((ResourceLocation)(b.getResManager()).spawnTypes.get(i)).equals(this.animalKey))
        targetAnimals = ((CopyOnWriteArrayList)(b.getResManager()).spawns.get(i)).size(); 
    } 
    if (!this.aggressiveSlaughter)
      return (nbanimals > targetAnimals); 
    return (nbanimals > targetAnimals / 2);
  }
  
  private boolean isEntityChild(Entity ent) {
    if (!(ent instanceof EntityAgeable))
      return false; 
    EntityAgeable animal = (EntityAgeable)ent;
    return animal.isChild();
  }
  
  public boolean isFightingGoal() {
    return true;
  }
  
  public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Building dest = villager.getGoalBuildingDest();
    if (dest == null)
      return true; 
    List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.world, EntityList.getClass(this.animalKey), villager.getPos(), 1, 5);
    for (Entity ent : animals) {
      if (!ent.isDead && ent instanceof EntityLivingBase && 
        !isEntityChild(ent) && 
        villager.canEntityBeSeen(ent)) {
        EntityLivingBase entLiving = (EntityLivingBase)ent;
        villager.setAttackTarget(entLiving);
        for (AnnotedParameter.BonusItem bonusItem : this.bonusItem) {
          if ((bonusItem.tag == null || dest.containsTags(bonusItem.tag)) && 
            MillCommonUtilities.randomInt(100) <= bonusItem.chance)
            villager.addToInv(bonusItem.item, 1); 
        } 
        villager.swingArm(EnumHand.MAIN_HAND);
        return true;
      } 
    } 
    animals = WorldUtilities.getEntitiesWithinAABB(villager.world, EntityList.getClass(this.animalKey), villager.getPos(), 2, 5);
    for (Entity ent : animals) {
      if (!ent.isDead && ent instanceof EntityLivingBase && 
        !isEntityChild(ent) && 
        villager.canEntityBeSeen(ent)) {
        EntityLivingBase entLiving = (EntityLivingBase)ent;
        villager.setAttackTarget(entLiving);
        for (AnnotedParameter.BonusItem bonusItem : this.bonusItem) {
          if ((bonusItem.tag == null || dest.containsTags(bonusItem.tag)) && 
            MillCommonUtilities.randomInt(100) <= bonusItem.chance)
            villager.addToInv(bonusItem.item, 1); 
        } 
        villager.swingArm(EnumHand.MAIN_HAND);
        return true;
      } 
    } 
    return true;
  }
  
  public int range(MillVillager villager) {
    return 1;
  }
  
  public boolean validateGoal() {
    if (this.animalKey == null) {
      MillLog.error(this, "The animalKey is mandatory in custom slaughter goals.");
      return false;
    } 
    return true;
  }
}
