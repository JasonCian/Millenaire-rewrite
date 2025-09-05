package org.millenaire.common.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

@Documentation("Go to the temple to be available for pujas or sacrifices.")
public class GoalBePujaPerformer extends Goal {
  public static final int sellingRadius = 7;
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple != null && temple.pujas != null && (temple.pujas.priest == null || temple.pujas.priest == villager)) {
      if (MillConfigValues.LogPujas >= 3)
        MillLog.debug(villager, "Destination for bepujaperformer: " + temple); 
      return packDest(temple.getResManager().getCraftingPos(), temple);
    } 
    return null;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    Building temple = null;
    if (villager.canMeditate()) {
      if (!villager.mw.isGlobalTagSet("pujas"))
        return false; 
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      if (!villager.mw.isGlobalTagSet("mayansacrifices"))
        return false; 
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple == null)
      return false; 
    EntityPlayer player = villager.world.getClosestPlayer(temple.getResManager().getCraftingPos().getiX(), temple.getResManager().getCraftingPos().getiY(), temple
        .getResManager().getCraftingPos().getiZ(), 7.0D, false);
    boolean valid = (player != null && temple.getResManager().getCraftingPos().distanceTo((Entity)player) < 7.0D);
    if (!valid)
      return false; 
    return (getDestination(villager) != null);
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple == null)
      return false; 
    EntityPlayer player = villager.world.getClosestPlayer(temple.getResManager().getCraftingPos().getiX(), temple.getResManager().getCraftingPos().getiY(), temple
        .getResManager().getCraftingPos().getiZ(), 7.0D, false);
    boolean valid = (player != null && temple.getResManager().getCraftingPos().distanceTo((Entity)player) < 7.0D);
    if (!valid && MillConfigValues.LogPujas >= 1)
      MillLog.major(this, "Be Puja Performer no longer valid."); 
    return (valid && !temple.pujas.canPray());
  }
  
  public String labelKey(MillVillager villager) {
    if (villager != null && villager.canPerformSacrifices())
      return "besacrificeperformer"; 
    return this.key;
  }
  
  public String labelKeyWhileTravelling(MillVillager villager) {
    if (villager != null && villager.canPerformSacrifices())
      return "besacrificeperformer"; 
    return this.key;
  }
  
  public boolean lookAtPlayer() {
    return true;
  }
  
  public void onAccept(MillVillager villager) {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple == null)
      return; 
    EntityPlayer player = villager.world.getClosestPlayer(temple.getResManager().getCraftingPos().getiX(), temple.getResManager().getCraftingPos().getiY(), temple
        .getResManager().getCraftingPos().getiZ(), 7.0D, false);
    if (villager.canMeditate()) {
      ServerSender.sendTranslatedSentence(player, 'f', "pujas.priestcoming", new String[] { villager.getName() });
    } else if (villager.canPerformSacrifices()) {
      ServerSender.sendTranslatedSentence(player, 'f', "sacrifices.priestcoming", new String[] { villager.getName() });
    } 
  }
  
  public boolean performAction(MillVillager villager) {
    Building temple = null;
    if (villager.canMeditate()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
    } else if (villager.canPerformSacrifices()) {
      temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
    } 
    if (temple == null)
      return true; 
    temple.pujas.priest = villager;
    return temple.pujas.canPray();
  }
  
  public int priority(MillVillager villager) {
    return 300;
  }
  
  public int range(MillVillager villager) {
    return 2;
  }
}
