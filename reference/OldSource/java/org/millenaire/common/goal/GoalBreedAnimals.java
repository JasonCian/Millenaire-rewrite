package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.WorldUtilities;

@Documentation("Breed animals at the villager's house, of types decided by tags like 'cattle', 'pig' etc tags.")
public class GoalBreedAnimals extends Goal {
  private static final Item[] CEREALS = new Item[] { Items.WHEAT, (Item)MillItems.RICE, (Item)MillItems.MAIZE };
  
  private static final Item[] SEEDS = new Item[] { Items.WHEAT_SEEDS, (Item)MillItems.RICE, (Item)MillItems.MAIZE };
  
  private static final Item[] CARROTS = new Item[] { Items.CARROT };
  
  private Item[] getBreedingItems(Class<? extends EntityAnimal> animalClass) {
    if (animalClass == EntityCow.class || animalClass == EntitySheep.class)
      return CEREALS; 
    if (animalClass == EntityPig.class)
      return CARROTS; 
    if (animalClass == EntityChicken.class)
      return SEEDS; 
    return null;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    List<Class<? extends EntityAnimal>> validAnimals = getValidAnimalClasses(villager);
    for (Class<? extends EntityAnimal> animalClass : validAnimals) {
      Item[] breedingItems = getBreedingItems(animalClass);
      boolean available = false;
      if (breedingItems == null) {
        available = true;
      } else {
        for (Item breedingItem : breedingItems) {
          if (!available && villager.getHouse().countGoods(breedingItem) > 0)
            available = true; 
        } 
      } 
      if (available) {
        int targetAnimals = 0;
        for (int i = 0; i < (villager.getHouse().getResManager()).spawns.size(); i++) {
          if (animalClass.isAssignableFrom(EntityList.getClass((villager.getHouse().getResManager()).spawnTypes.get(i))))
            targetAnimals = ((CopyOnWriteArrayList<?>)(villager.getHouse().getResManager()).spawns.get(i)).size(); 
        } 
        List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.world, animalClass, villager.getHouse().getPos(), 15, 10);
        int nbAdultAnimal = 0, nbAnimal = 0;
        for (Entity ent : animals) {
          EntityAnimal animal = (EntityAnimal)ent;
          if (animal.getGrowingAge() == 0)
            nbAdultAnimal++; 
          nbAnimal++;
        } 
        if (nbAdultAnimal >= 2 && nbAnimal < targetAnimals * 2)
          for (Entity ent : animals) {
            EntityAnimal animal = (EntityAnimal)ent;
            if (animal.getGrowingAge() == 0 && !animal.isInLove())
              return packDest(null, villager.getHouse(), (Entity)animal); 
          }  
      } 
    } 
    return null;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    if (villager.getGoalDestEntity() == null || !(villager.getGoalDestEntity() instanceof EntityAnimal))
      return null; 
    EntityAnimal animal = (EntityAnimal)villager.getGoalDestEntity();
    Item[] breedingItems = getBreedingItems(animal.getClass());
    if (breedingItems != null)
      for (Item breedingItem : breedingItems) {
        if (villager.getHouse().countGoods(breedingItem) > 0)
          return new ItemStack[] { new ItemStack(breedingItem, 1) }; 
      }  
    return null;
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    if (!villager.canVillagerClearLeaves())
      return JPS_CONFIG_WIDE_NO_LEAVES; 
    return JPS_CONFIG_WIDE;
  }
  
  private List<Class<? extends EntityAnimal>> getValidAnimalClasses(MillVillager villager) {
    List<Class<? extends EntityAnimal>> validAnimals = new ArrayList<>();
    if (villager.getHouse().containsTags("sheeps")) {
      validAnimals.add(EntitySheep.class);
      validAnimals.add(EntityChicken.class);
    } 
    if (villager.getHouse().containsTags("cattle"))
      validAnimals.add(EntityCow.class); 
    if (villager.getHouse().containsTags("pigs"))
      validAnimals.add(EntityPig.class); 
    if (villager.getHouse().containsTags("chicken"))
      validAnimals.add(EntityChicken.class); 
    return validAnimals;
  }
  
  public boolean isFightingGoal() {
    return false;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    List<Class<? extends EntityAnimal>> validAnimals = getValidAnimalClasses(villager);
    for (Class<? extends EntityAnimal> animalClass : validAnimals) {
      List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.world, animalClass, villager.getPos(), 4, 2);
      for (Entity ent : animals) {
        if (!ent.isDead) {
          EntityAnimal animal = (EntityAnimal)ent;
          Item[] breedingItems = getBreedingItems(animal.getClass());
          boolean available = false;
          Item foundBreedingItem = null;
          if (breedingItems == null) {
            available = true;
          } else {
            for (Item breedingItem : breedingItems) {
              if (!available && villager.getHouse().countGoods(breedingItem) > 0) {
                available = true;
                foundBreedingItem = breedingItem;
              } 
            } 
          } 
          if (available)
            if (!animal.isChild() && !animal.isInLove() && animal.getGrowingAge() == 0) {
              animal.setInLove(null);
              animal.setAttackTarget(null);
              if (foundBreedingItem != null)
                villager.getHouse().takeGoods(foundBreedingItem, 1); 
              villager.swingArm(EnumHand.MAIN_HAND);
              ServerSender.sendAnimalBreeding(animal);
            }  
        } 
      } 
    } 
    return true;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 10000;
  }
  
  public int range(MillVillager villager) {
    return 5;
  }
}
