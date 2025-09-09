package org.millenaire.common.commands;

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
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class CommandDebugResendProfiles implements ICommand {
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return true;
  }
  
  public int compareTo(ICommand o) {
    return getName().compareTo(o.getName());
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (!world.isRemote) {
      if (args.length != 1)
        throw new WrongUsageException(getUsage(sender), new Object[0]); 
      if (args[0].equals("all")) {
        MillWorldData worldData = Mill.getMillWorld(world);
        for (UserProfile profile : worldData.profiles.values()) {
          if (profile.connected) {
            profile.sendInitialPackets();
            if (sender instanceof EntityPlayer) {
              ServerSender.sendTranslatedSentence((EntityPlayer)sender, '2', "Resent profile data for " + profile.playerName, new String[0]);
              continue;
            } 
            MillLog.major(profile, "Resent profile data.");
          } 
        } 
      } else {
        EntityPlayer player = world.getPlayerEntityByName(args[0]);
        if (player == null)
          throw new CommandException("This command requires a player name or 'all' as first parameter.", new Object[0]); 
        MillWorldData worldData = Mill.getMillWorld(world);
        UserProfile profile = worldData.getProfile(player);
        profile.sendInitialPackets();
        if (sender instanceof EntityPlayer) {
          ServerSender.sendTranslatedSentence((EntityPlayer)sender, '2', "Resent profile data for " + profile.playerName, new String[0]);
        } else {
          MillLog.major(profile, "Resent profile data.");
        } 
      } 
    } 
  }
  
  public List<String> getAliases() {
    return Collections.emptyList();
  }
  
  public String getName() {
    return "millDebugSendProfiles";
  }
  
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
    return Collections.emptyList();
  }
  
  public String getUsage(ICommandSender sender) {
    return "commands." + getName().toLowerCase() + ".usage";
  }
  
  public boolean isUsernameIndex(String[] args, int index) {
    return (index == 1);
  }
}
