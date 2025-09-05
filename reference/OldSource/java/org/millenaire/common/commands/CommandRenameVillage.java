package org.millenaire.common.commands;

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
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class CommandRenameVillage implements ICommand {
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return sender.canUseCommand(getRequiredPermissionLevel(), getName());
  }
  
  public int compareTo(ICommand o) {
    return getName().compareTo(o.getName());
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (!world.isRemote) {
      if (args.length != 2 && args.length != 3)
        throw new WrongUsageException(getUsage(sender), new Object[0]); 
      MillWorldData worldData = Mill.getMillWorld(world);
      List<Building> townHalls = CommandUtilities.getMatchingVillages(worldData, args[0]);
      if (townHalls.size() == 0)
        throw new CommandException(LanguageUtilities.string("command.tp_nomatchingvillage"), new Object[0]); 
      if (townHalls.size() > 1)
        throw new CommandException(LanguageUtilities.string("command.tp_multiplematchingvillages", new String[] { "" + townHalls.size() }), new Object[0]); 
      Building village = townHalls.get(0);
      String oldName = village.getVillageQualifiedName();
      village.changeVillageName(args[1]);
      if (args.length > 2) {
        village.changeVillageQualifier(args[2]);
      } else {
        village.changeVillageQualifier("");
      } 
      for (EntityPlayer player : world.playerEntities) {
        ServerSender.sendTranslatedSentence(player, '9', "command.renamevillage_notification", new String[] { sender.getName(), oldName, village.getVillageQualifiedName() });
      } 
    } 
  }
  
  public List<String> getAliases() {
    return Collections.emptyList();
  }
  
  public String getName() {
    return "millRenameVillage";
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
      for (Building th : townHalls)
        possibleMatches.add(CommandUtilities.normalizeString(th.getVillageQualifiedName())); 
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
