package org.millenaire.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;

public class CommandDebugResetVillagers implements ICommand {
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return true;
  }
  
  public int compareTo(ICommand o) {
    return getName().compareTo(o.getName());
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (!world.isRemote) {
      if (!(sender instanceof EntityPlayer))
        throw new WrongUsageException(getUsage(sender), new Object[0]); 
      EntityPlayer senderPlayer = (EntityPlayer)sender;
      MillWorldData worldData = Mill.getMillWorld(world);
      Building village = worldData.getClosestVillage(new Point((Entity)senderPlayer));
      if (village == null || village.getPos().distanceTo((Entity)senderPlayer) > 50.0D)
        throw new CommandException("No village within 50 blocks.", new Object[0]); 
      int despawnedVillagers = 0;
      int respawnedVillagers = 0;
      for (VillagerRecord vr : village.getAllVillagerRecords()) {
        List<MillVillager> matchingVillagers = new ArrayList<>();
        for (MillVillager villager : worldData.getAllKnownVillagers()) {
          if (villager.getVillagerId() == vr.getVillagerId())
            matchingVillagers.add(villager); 
        } 
        for (int i = matchingVillagers.size() - 1; i >= 0; i--) {
          if (((MillVillager)matchingVillagers.get(i)).isDead) {
            ((MillVillager)matchingVillagers.get(i)).despawnVillagerSilent();
            despawnedVillagers++;
            matchingVillagers.remove(i);
          } 
        } 
        if (matchingVillagers.size() == 0) {
          village.respawnVillager(vr, (vr.getHouse()).location.sleepingPos);
          respawnedVillagers++;
        } 
      } 
      ServerSender.sendChat(senderPlayer, TextFormatting.DARK_GREEN, "Repeared the villager list of " + village.getVillageQualifiedName() + ". Despawned " + despawnedVillagers + " dead villager(s) and respawned " + respawnedVillagers + " villagers.");
    } 
  }
  
  public List<String> getAliases() {
    return Collections.emptyList();
  }
  
  public String getName() {
    return "millDebugResetVillagers";
  }
  
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
    return Collections.emptyList();
  }
  
  public String getUsage(ICommandSender sender) {
    return "commands." + getName().toLowerCase() + ".usage";
  }
  
  public boolean isUsernameIndex(String[] args, int index) {
    return false;
  }
}
