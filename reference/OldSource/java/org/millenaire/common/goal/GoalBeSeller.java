package org.millenaire.common.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;

@Documentation("Go and sell to the player.")
public class GoalBeSeller extends Goal {
  public static final int sellingRadius = 7;
  
  private static ItemStack[] PURSE = new ItemStack[] { new ItemStack((Item)MillItems.PURSE, 1) };
  
  private static ItemStack[] DENIER = new ItemStack[] { new ItemStack((Item)MillItems.DENIER, 1) };
  
  public Goal.GoalInformation getDestination(MillVillager villager) {
    return packDest((villager.getTownHall()).sellingPlace);
  }
  
  public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
    return DENIER;
  }
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) throws Exception {
    return PURSE;
  }
  
  public boolean isPossibleSpecific(MillVillager villager) {
    return false;
  }
  
  public boolean isStillValidSpecific(MillVillager villager) throws Exception {
    if ((villager.getTownHall()).sellingPlace == null)
      return false; 
    EntityPlayer player = villager.world.getClosestPlayer((villager.getTownHall()).sellingPlace.getiX(), (villager.getTownHall()).sellingPlace.getiY(), 
        (villager.getTownHall()).sellingPlace.getiZ(), 7.0D, false);
    boolean valid = (player != null && (villager.getTownHall()).sellingPlace.distanceTo((Entity)player) < 7.0D);
    if (!valid && MillConfigValues.LogWifeAI >= 1)
      MillLog.major(this, "Selling goal no longer valid."); 
    return valid;
  }
  
  public boolean lookAtPlayer() {
    return true;
  }
  
  public void onAccept(MillVillager villager) {
    EntityPlayer player = villager.world.getClosestPlayer((villager.getTownHall()).sellingPlace.getiX(), (villager.getTownHall()).sellingPlace.getiY(), 
        (villager.getTownHall()).sellingPlace.getiZ(), 7.0D, false);
    ServerSender.sendTranslatedSentence(player, 'f', "ui.sellercoming", new String[] { villager.getName() });
  }
  
  public void onComplete(MillVillager villager) {
    EntityPlayer player = villager.world.getClosestPlayer(villager.getTownHall().getResManager().getSellingPos().getiX(), villager.getTownHall().getResManager().getSellingPos().getiY(), villager
        .getTownHall().getResManager().getSellingPos().getiZ(), 17.0D, false);
    ServerSender.sendTranslatedSentence(player, 'f', "ui.tradecomplete", new String[] { villager.getName() });
    (villager.getTownHall()).seller = null;
    (villager.getTownHall()).sellingPlace = null;
  }
  
  public boolean performAction(MillVillager villager) {
    if ((villager.getTownHall()).sellingPlace == null) {
      MillLog.error(this, "villager.townHall.sellingPlace is null.");
      return true;
    } 
    return false;
  }
  
  public int priority(MillVillager villager) {
    return 0;
  }
  
  public int range(MillVillager villager) {
    return 2;
  }
}
