package org.millenaire.common.forge;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class MillEventController {
  @SubscribeEvent
  public void addInuitDrops(LivingDropsEvent event) {
    if (event.getEntityLiving() instanceof net.minecraft.entity.monster.EntityGuardian || event.getEntityLiving() instanceof net.minecraft.entity.passive.EntitySquid) {
      inuitDropsSeaFood(event);
    } else if (event.getEntityLiving() instanceof net.minecraft.entity.passive.EntityWolf) {
      inuitDropsWolfMeat(event);
    } else if (event.getEntityLiving() instanceof net.minecraft.entity.monster.EntityPolarBear) {
      int quantity = 1 + MillCommonUtilities.randomInt(2);
      event.getDrops().add(new EntityItem((event.getEntityLiving()).world, (event.getEntityLiving()).posX, (event.getEntityLiving()).posY, (event.getEntityLiving()).posZ, new ItemStack((Item)MillItems.BEARMEAT_RAW, quantity)));
    } 
  }
  
  @SubscribeEvent
  public void clientLoggedIn(FMLNetworkEvent.ClientConnectedToServerEvent event) {
    Mill.proxy.handleClientLogin();
  }
  
  @SubscribeEvent
  public void connectionClosed(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
    for (MillWorldData mw : Mill.serverWorlds)
      mw.checkConnections(); 
  }
  
  @SubscribeEvent
  public void damageOnPlayer(LivingDamageEvent event) {
    if (event.getEntityLiving() instanceof EntityPlayer) {
      EntityLivingBase source = null;
      if (event.getSource().getImmediateSource() != null && event.getSource().getImmediateSource() instanceof EntityLivingBase) {
        source = (EntityLivingBase)event.getSource().getImmediateSource();
      } else if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
        source = (EntityLivingBase)event.getSource().getTrueSource();
      } 
      if (source != null) {
        MillWorldData mw = Mill.getMillWorld((event.getEntityLiving()).world);
        EntityPlayer player = (EntityPlayer)event.getEntityLiving();
        String playerName = player.getName();
        for (MillVillager villager : mw.getAllKnownVillagers()) {
          if (playerName.equals(villager.hiredBy))
            villager.setAttackTarget((EntityLivingBase)event.getSource().getTrueSource()); 
        } 
      } 
    } 
  }
  
  @SubscribeEvent
  public void handleFurnaceWithdrawals(PlayerEvent.ItemSmeltedEvent event) {
    if (event.smelting.getCount() == 0)
      return; 
    EntityPlayer player = event.player;
    MillWorldData mwd = Mill.getMillWorld(player.world);
    Point playerPos = new Point((Entity)player);
    Building closestVillageTH = mwd.getClosestVillage(playerPos);
    if (closestVillageTH != null && !closestVillageTH.controlledBy(player)) {
      BuildingLocation location = closestVillageTH.getLocationAtCoordWithTolerance(playerPos, 4);
      if (location != null) {
        Building building = location.getBuilding(player.world);
        if (building != null) {
          boolean isBuildingPlayerOwned = (building.location.getPlan() != null && ((building.location.getPlan()).price > 0 || (building.location.getPlan()).isgift));
          if (!isBuildingPlayerOwned && !(building.getResManager()).furnaces.isEmpty()) {
            UserProfile serverProfile = VillageUtilities.getServerProfile(player.world, player);
            if (serverProfile != null) {
              int reputationLost = event.smelting.getCount() * 100;
              serverProfile.adjustReputation(closestVillageTH, -reputationLost);
              ServerSender.sendTranslatedSentence(player, '6', "ui.stealingsmelteditems", new String[] { "" + reputationLost });
            } 
          } 
        } 
      } 
    } 
  }
  
  private void inuitDropsSeaFood(LivingDropsEvent event) {
    EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();
    UserProfile profile = Mill.getMillWorld((event.getEntity()).world).getProfile(player);
    if (event.getSource() != null && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer && profile.isTagSet("huntingdrop_" + MillItems.SEAFOOD_RAW.getRegistryName().getResourcePath())) {
      int quantity = 0;
      if (event.getEntityLiving() instanceof net.minecraft.entity.passive.EntitySquid) {
        if (MillCommonUtilities.chanceOn(10))
          quantity = 1; 
      } else if (event.getEntityLiving() instanceof net.minecraft.entity.monster.EntityElderGuardian) {
        quantity = 5 + MillCommonUtilities.randomInt(5);
      } else if (event.getEntityLiving() instanceof net.minecraft.entity.monster.EntityGuardian) {
        quantity = 2 + MillCommonUtilities.randomInt(2);
      } 
      if (quantity > 0) {
        event.getDrops().add(new EntityItem((event.getEntityLiving()).world, (event.getEntityLiving()).posX, (event.getEntityLiving()).posY, (event.getEntityLiving()).posZ, new ItemStack((Item)MillItems.SEAFOOD_RAW, quantity)));
        MillAdvancements.GREAT_HUNTER.grant(player);
      } 
    } 
  }
  
  private void inuitDropsWolfMeat(LivingDropsEvent event) {
    EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();
    UserProfile profile = Mill.getMillWorld((event.getEntity()).world).getProfile(player);
    if (event.getSource() != null && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer && profile.isTagSet("huntingdrop_" + MillItems.WOLFMEAT_RAW.getRegistryName().getResourcePath())) {
      int quantity = MillCommonUtilities.randomInt(3);
      if (quantity > 0) {
        event.getDrops().add(new EntityItem((event.getEntityLiving()).world, (event.getEntityLiving()).posX, (event.getEntityLiving()).posY, (event.getEntityLiving()).posZ, new ItemStack((Item)MillItems.WOLFMEAT_RAW, quantity)));
        MillAdvancements.GREAT_HUNTER.grant(player);
      } 
    } 
  }
  
  @SubscribeEvent
  public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    try {
      UserProfile profile = VillageUtilities.getServerProfile(event.player.world, event.player);
      if (profile != null && !event.player.getName().equals(profile.playerName)) {
        MillLog.major(null, "Name of player with UUID '" + profile.uuid + "' changed from '" + profile.playerName + "' to '" + event.player.getName() + "'.");
        profile.playerName = event.player.getName();
        profile.saveProfile();
      } 
      if (profile != null) {
        profile.connectUser();
      } else {
        MillLog.error(this, "Could not get profile on login for user: " + event.player.getName());
      } 
    } catch (Exception e) {
      MillLog.printException("Error in ConnectionHandler.playerLoggedIn:", e);
    } 
  }
  
  @SubscribeEvent
  public void worldLoaded(WorldEvent.Load event) {
    Mill.proxy.loadLanguagesIfNeeded();
    if (Mill.displayMillenaireLocationError && !Mill.proxy.isTrueServer()) {
      Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), '4', "ERREUR: Impossible de trouver le fichier de configuration " + Mill.proxy
          .getConfigFile().getAbsolutePath() + ". Vérifiez que le dossier millenaire est bien dans minecraft/mods/");
      Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), '4', "ERROR: Could not find the config file at " + Mill.proxy
          .getConfigFile().getAbsolutePath() + ". Check that the millenaire directory is in minecraft/mods/");
      return;
    } 
    if (!(event.getWorld() instanceof net.minecraft.world.WorldServer)) {
      Mill.clientWorld = new MillWorldData(event.getWorld());
    } else if (!(event.getWorld() instanceof net.minecraft.world.WorldServerMulti)) {
      MillWorldData newWorld = new MillWorldData(event.getWorld());
      Mill.serverWorlds.add(newWorld);
      newWorld.loadData();
    } 
  }
  
  @SubscribeEvent
  public void worldSaved(WorldEvent.Save event) {
    if (Mill.startupError)
      return; 
    if ((event.getWorld()).provider.getDimension() != 0)
      return; 
    if (!(event.getWorld() instanceof net.minecraft.world.WorldServer)) {
      Mill.clientWorld.saveEverything();
    } else {
      for (MillWorldData mw : Mill.serverWorlds) {
        if (mw.world == event.getWorld())
          mw.saveEverything(); 
      } 
    } 
  }
  
  @SubscribeEvent
  public void worldUnloaded(WorldEvent.Unload event) {
    if (Mill.startupError)
      return; 
    if ((event.getWorld()).provider.getDimension() != 0)
      return; 
    if (!(event.getWorld() instanceof net.minecraft.world.WorldServer)) {
      if (Mill.clientWorld.world == event.getWorld())
        Mill.clientWorld = null; 
    } else {
      List<MillWorldData> toDelete = new ArrayList<>();
      for (MillWorldData mw : Mill.serverWorlds) {
        if (mw.world == event.getWorld())
          toDelete.add(mw); 
      } 
      for (MillWorldData mw : toDelete)
        Mill.serverWorlds.remove(mw); 
    } 
  }
}
