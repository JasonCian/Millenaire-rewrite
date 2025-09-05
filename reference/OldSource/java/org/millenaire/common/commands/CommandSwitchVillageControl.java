package org.millenaire.common.commands;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class CommandSwitchVillageControl implements ICommand {
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return sender.canUseCommand(getRequiredPermissionLevel(), getName());
  }
  
  public int compareTo(ICommand o) {
    return getName().compareTo(o.getName());
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (!world.isRemote) {
      if (args.length != 2)
        throw new WrongUsageException(getUsage(sender), new Object[0]); 
      MillWorldData worldData = Mill.getMillWorld(world);
      List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, args[0]);
      if (townHalls.size() == 0)
        throw new CommandException(LanguageUtilities.string("command.tp_nomatchingvillage"), new Object[0]); 
      if (townHalls.size() > 1)
        throw new CommandException(LanguageUtilities.string("command.tp_multiplematchingvillages", new String[] { "" + townHalls.size() }), new Object[0]); 
      Building village = townHalls.get(0);
      if (!village.villageType.playerControlled)
        throw new CommandException(LanguageUtilities.string("command.switchcontrol_notcontrolled", new String[] { village.getVillageQualifiedName() }), new Object[0]); 
      String playerName = args[1];
      GameProfile profile = world.getMinecraftServer().getPlayerProfileCache().getGameProfileForUsername(playerName);
      if (profile == null)
        throw new CommandException(LanguageUtilities.string("command.switchcontrol_playernotfound", new String[] { playerName }), new Object[0]); 
      String oldControllerName = village.controlledByName;
      village.controlledBy = profile.getId();
      village.controlledByName = profile.getName();
      MillLog.major(this, "Switched controller from " + oldControllerName + " to " + village.controlledByName + " via command by " + sender.getName() + ".");
      for (EntityPlayer player : world.playerEntities) {
        ServerSender.sendTranslatedSentence(player, '9', "command.switchcontrol_notification", new String[] { sender.getName(), oldControllerName, profile.getName(), village
              .getVillageQualifiedName() });
      } 
    } 
  }
  
  public List<String> getAliases() {
    return Collections.emptyList();
  }
  
  public String getName() {
    return "millSwitchVillageControl";
  }
  
  public int getRequiredPermissionLevel() {
    return 3;
  }
  
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
    if (args.length == 1) {
      World world = sender.getEntityWorld();
      MillWorldData worldData = Mill.getMillWorld(world);
      List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, args[0]);
      List<String> possibleMatches = new ArrayList<>();
      for (Building th : townHalls) {
        if (th.villageType.playerControlled)
          possibleMatches.add(CommandUtilities.normalizeString(th.getVillageQualifiedName())); 
      } 
      return possibleMatches;
    } 
    if (args.length == 2) {
      World world = sender.getEntityWorld();
      List<String> possibleMatches = new ArrayList<>();
      String normalizedQuery = CommandUtilities.normalizeString(args[1]);
      for (String userName : world.getMinecraftServer().getPlayerProfileCache().getUsernames()) {
        String normalizedName = CommandUtilities.normalizeString(userName);
        if (normalizedName.startsWith(normalizedQuery))
          possibleMatches.add(normalizedName); 
      } 
      return possibleMatches;
    } 
    return Collections.emptyList();
  }
  
  public String getUsage(ICommandSender sender) {
    return "commands." + getName().toLowerCase() + ".usage";
  }
  
  public boolean isUsernameIndex(String[] args, int index) {
    return false;
  }
}
