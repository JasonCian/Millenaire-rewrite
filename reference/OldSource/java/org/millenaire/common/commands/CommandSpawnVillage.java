package org.millenaire.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.world.WorldGenVillage;

public class CommandSpawnVillage implements ICommand {
  private final boolean spawnLoneBuilding;
  
  public CommandSpawnVillage(boolean spawnLoneBuildings) {
    this.spawnLoneBuilding = spawnLoneBuildings;
  }
  
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return sender.canUseCommand(getRequiredPermissionLevel(), getName());
  }
  
  public int compareTo(ICommand o) {
    return getName().compareTo(o.getName());
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    World world = sender.getEntityWorld();
    if (!world.isRemote) {
      VillageType villageType;
      if (args.length < 2 || args.length > 5)
        throw new WrongUsageException(getUsage(sender), new Object[0]); 
      String cultureParam = args[0];
      String villageTypeParam = args[1];
      Culture culture = Culture.getCultureByName(cultureParam);
      if (culture == null)
        throw new CommandException(LanguageUtilities.string("command.spawnvillage_unknownculture", new String[] { cultureParam }), new Object[0]); 
      if (this.spawnLoneBuilding) {
        villageType = culture.getLoneBuildingType(villageTypeParam);
      } else {
        villageType = culture.getVillageType(villageTypeParam);
      } 
      if (villageType == null)
        throw new CommandException(LanguageUtilities.string("command.spawnvillage_unknownvillage", new String[] { villageTypeParam }), new Object[0]); 
      EntityPlayer player = null;
      if (sender instanceof EntityPlayer)
        player = (EntityPlayer)sender; 
      int x = 0;
      int z = 0;
      float completion = 0.0F;
      if (args.length > 3) {
        x = CommandBase.parseInt(args[2]);
        z = CommandBase.parseInt(args[3]);
        if (args.length > 4)
          completion = CommandBase.parseInt(args[4]) / 100.0F; 
      } else if (player != null) {
        x = (int)player.posX;
        z = (int)player.posZ;
        if (args.length > 2)
          completion = CommandBase.parseInt(args[2]) / 100.0F; 
      } 
      MillLog.major(null, "Attempting to spawn village of type " + cultureParam + ":" + villageTypeParam + " at " + x + "/" + z + ".");
      WorldGenVillage genVillage = new WorldGenVillage();
      boolean result = genVillage.generateVillageAtPoint(world, MillCommonUtilities.random, x, 0, z, player, false, true, false, 0, villageType, null, null, completion);
      MillLog.major(null, "Result of spawn attempt: " + result);
    } 
  }
  
  public List<String> getAliases() {
    return Collections.emptyList();
  }
  
  public String getName() {
    if (this.spawnLoneBuilding)
      return "millSpawnLoneBuilding"; 
    return "millSpawnVillage";
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
    if (args.length == 2) {
      Culture culture = Culture.getCultureByName(args[0]);
      if (culture != null) {
        List<VillageType> types;
        List<String> possibleMatches = new ArrayList<>();
        String partialKey = CommandUtilities.normalizeString(args[1]);
        if (this.spawnLoneBuilding) {
          types = culture.listLoneBuildingTypes;
        } else {
          types = culture.listVillageTypes;
        } 
        for (VillageType vtype : types) {
          if (CommandUtilities.normalizeString(vtype.key).startsWith(partialKey))
            possibleMatches.add(CommandUtilities.normalizeString(vtype.key)); 
        } 
        return possibleMatches;
      } 
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
