package org.millenaire.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class CommandImportCulture implements ICommand {
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return sender.canUseCommand(getRequiredPermissionLevel(), getName());
  }
  
  public int compareTo(ICommand o) {
    return getName().compareTo(o.getName());
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (!world.isRemote) {
      int x, z;
      EntityPlayer player = null;
      if (sender instanceof EntityPlayer)
        player = (EntityPlayer)sender; 
      if (args.length != 1 && args.length != 3)
        throw new WrongUsageException(getUsage(sender), new Object[0]); 
      String cultureParam = args[0];
      Culture culture = Culture.getCultureByName(cultureParam);
      if (culture == null)
        throw new CommandException(LanguageUtilities.string("command.spawnvillage_unknownculture", new String[] { cultureParam }), new Object[0]); 
      if (args.length > 1) {
        x = CommandBase.parseInt(args[1]);
        z = CommandBase.parseInt(args[2]);
      } else {
        x = (int)player.posX;
        z = (int)player.posZ;
      } 
      int y = WorldUtilities.findTopSoilBlock(world, x, z);
      Point startPoint = new Point(x, y, z);
      Point adjustedStartPoint = new Point(x, y, z);
      List<BuildingPlanSet> planSets = new ArrayList<>(culture.ListPlanSets);
      planSets = (List<BuildingPlanSet>)planSets.stream().sorted((p1, p2) -> p1.mainFile.compareTo(p2.mainFile)).collect(Collectors.toList());
      for (BuildingPlanSet planSet : planSets) {
        if (!(planSet.getFirstStartingPlan()).isSubBuilding) {
          ServerSender.sendTranslatedSentence(player, 'f', "command.importculture_importingbuilding", new String[] { planSet.getNameTranslated() });
          int xDelta = BuildingImportExport.importTableHandleImportRequest(player, adjustedStartPoint, culture.key, planSet.key, true, 0, 0, 0, true);
          adjustedStartPoint = new Point(adjustedStartPoint.x + xDelta + 5.0D, startPoint.y, startPoint.z);
        } 
      } 
    } 
  }
  
  public List<String> getAliases() {
    return Collections.emptyList();
  }
  
  public String getName() {
    return "millImportCulture";
  }
  
  public int getRequiredPermissionLevel() {
    return 3;
  }
  
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
    if (args.length == 1) {
      List<String> possibleMatches = new ArrayList<>();
      String partialKey = CommandUtilities.normalizeString(args[0]);
      for (Culture c : Culture.ListCultures) {
        if (CommandUtilities.normalizeString(c.key).startsWith(partialKey))
          possibleMatches.add(CommandUtilities.normalizeString(c.key)); 
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
