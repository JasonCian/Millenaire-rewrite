package org.millenaire.common.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.village.Building;

@Documentation("Perform a puja or a Maya sacrifice.")
public class GoalPerformPuja extends Goal {
  public int actionDuration(MillVillager villager) throws Exception {
    return 5;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple != null && temple.pujas != null && (temple.pujas.priest == null || temple.pujas.priest == villager) && temple.pujas.canPray())
      return packDest(temple.getResManager().getCraftingPos(), temple); 
    return null;
  }
  
  public ItemStack[] getHeldItemsDestination(MillVillager villager) {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple.pujas.getStackInSlot(0) != null)
      return new ItemStack[] { temple.pujas.getStackInSlot(0) }; 
    return null;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) throws Exception {
    if (villager.canMeditate()) {
      if (!villager.mw.isGlobalTagSet("pujas"))
        return false; 
    } else if (villager.canPerformSacrifices() && 
      !villager.mw.isGlobalTagSet("mayansacrifices")) {
      return false;
    } 
    return (getDestination(villager) != null);
  }
  
  public String labelKey(MillVillager villager) {
    if (villager != null && villager.canPerformSacrifices())
      return "performsacrifices"; 
    return this.key;
  }
  
  public String labelKeyWhileTravelling(MillVillager villager) {
    if (villager != null && villager.canPerformSacrifices())
      return "performsacrifices"; 
    return this.key;
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    boolean canContinue = temple.pujas.performPuja(villager);
    EntityPlayer player = villager.world.getClosestPlayerToEntity((Entity)villager, 16.0D);
    if (player != null)
      temple.sendBuildingPacket(player, false); 
    if (!canContinue)
      return true; 
    return false;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 500;
  }
  
  public boolean swingArms() {
    return true;
  }
}
