package org.millenaire.common.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.millenaire.common.advancements.GenericAdvancement;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemParchment;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.quest.SpecialQuestActions;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;
import org.millenaire.common.world.WorldGenVillage;

public class GuiActions {
  public static final int VILLAGE_SCROLL_PRICE = 128;
  
  public static final int VILLAGE_SCROLL_REPUTATION = 8192;
  
  public static final int CROP_REPUTATION = 8192;
  
  public static final int CROP_PRICE = 512;
  
  public static final int CULTURE_CONTROL_REPUTATION = 131072;
  
  public static void activateMillChest(EntityPlayer player, Point p) {
    World world = player.world;
    if (MillConfigValues.DEV) {
      MillWorldData mw = Mill.getMillWorld(world);
      if (mw.buildingExists(p)) {
        Building ent = mw.getBuilding(p);
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock((Block)Blocks.SAND)) {
          ent.testModeGoods();
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock((Block)MillBlocks.PATHDIRT)) {
          ent.recalculatePaths(true);
          ent.clearOldPaths();
          ent.constructCalculatedPaths();
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock((Block)MillBlocks.PATHGRAVEL)) {
          ent.clearOldPaths();
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock((Block)MillBlocks.PATHDIRT_SLAB)) {
          ent.recalculatePaths(true);
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == MillItems.DENIER_OR) {
          ent.displayInfos(player);
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == Items.GLASS_BOTTLE) {
          mw.setGlobalTag("alchemy");
          MillLog.major(mw, "Set alchemy tag.");
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == MillItems.SUMMONING_WAND) {
          ent.displayInfos(player);
          try {
            if (ent.isTownhall)
              ent.rushCurrentConstructions(false); 
            if (ent.isInn)
              ent.attemptMerchantMove(true); 
            if (ent.hasVisitors)
              ent.getVisitorManager().update(true); 
          } catch (Exception e) {
            MillLog.printException(e);
          } 
          return;
        } 
        if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == Item.getItemFromBlock((Block)MillBlocks.PAINTED_BRICK_WHITE)) {
          ent.choseAndApplyBrickTheme();
          MillLog.major(mw, "Changed theme of village " + ent.getVillageQualifiedName() + " to: " + ent.brickColourTheme.key);
          return;
        } 
      } 
    } 
    ServerSender.displayMillChest(player, p);
  }
  
  public static void controlledBuildingsForgetBuilding(EntityPlayer player, Building townHall, BuildingProject project) {
    townHall.cancelBuilding(project.location);
  }
  
  public static void controlledBuildingsToggleUpgrades(EntityPlayer player, Building townHall, BuildingProject project, boolean allow) {
    project.location.upgradesAllowed = allow;
    if (allow)
      townHall.noProjectsLeft = false; 
  }
  
  public static void controlledMilitaryCancelRaid(EntityPlayer player, Building townHall) {
    if (townHall.raidStart == 0L) {
      townHall.cancelRaid();
      if (!townHall.world.isRemote)
        townHall.sendBuildingPacket(player, false); 
    } 
  }
  
  public static void controlledMilitaryDiplomacy(EntityPlayer player, Building townHall, Point target, int level) {
    townHall.adjustRelation(target, level, true);
    if (!townHall.world.isRemote)
      townHall.sendBuildingPacket(player, false); 
  }
  
  public static void controlledMilitaryPlanRaid(EntityPlayer player, Building townHall, Building target) {
    if (townHall.raidStart == 0L) {
      townHall.adjustRelation(target.getPos(), -100, true);
      townHall.planRaid(target);
      if (!townHall.world.isRemote)
        townHall.sendBuildingPacket(player, false); 
    } 
  }
  
  public static void hireExtend(EntityPlayer player, MillVillager villager) {
    villager.hiredBy = player.getName();
    villager.hiredUntil += 24000L;
    MillCommonUtilities.changeMoney((IInventory)player.inventory, -villager.getHireCost(player), player);
  }
  
  public static void hireHire(EntityPlayer player, MillVillager villager) {
    villager.hiredBy = player.getName();
    villager.hiredUntil = villager.world.getWorldTime() + 24000L;
    VillagerRecord vr = villager.getRecord();
    if (vr != null)
      vr.awayhired = true; 
    MillAdvancements.HIRED.grant(player);
    MillCommonUtilities.changeMoney((IInventory)player.inventory, -villager.getHireCost(player), player);
  }
  
  public static void hireRelease(EntityPlayer player, MillVillager villager) {
    villager.hiredBy = null;
    villager.hiredUntil = 0L;
    VillagerRecord vr = villager.getRecord();
    if (vr != null)
      vr.awayhired = false; 
  }
  
  public static void hireToggleStance(EntityPlayer player, boolean stance) {
    AxisAlignedBB surroundings = (new AxisAlignedBB(player.posX, player.posY, player.posZ, player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D)).expand(16.0D, 8.0D, 16.0D).expand(-16.0D, -8.0D, -16.0D);
    List list = player.world.getEntitiesWithinAABB(MillVillager.class, surroundings);
    for (Object o : list) {
      MillVillager villager = (MillVillager)o;
      if (player.getName().equals(villager.hiredBy))
        villager.aggressiveStance = stance; 
    } 
  }
  
  public static void newBuilding(EntityPlayer player, Building townHall, Point pos, String planKey) {
    BuildingPlanSet set = townHall.culture.getBuildingPlanSet(planKey);
    if (set == null)
      return; 
    BuildingPlan plan = set.getRandomStartingPlan();
    BuildingPlan.LocationReturn lr = plan.testSpot(townHall.winfo, townHall.regionMapper, townHall.getPos(), pos.getiX() - townHall.winfo.mapStartX, pos.getiZ() - townHall.winfo.mapStartZ, 
        MillCommonUtilities.getRandom(), -1, true);
    if (lr.location == null) {
      String error = null;
      if (lr.errorCode == 3) {
        error = "ui.constructionforbidden";
      } else if (lr.errorCode == 2) {
        error = "ui.locationclash";
      } else if (lr.errorCode == 1) {
        error = "ui.outsideradius";
      } else if (lr.errorCode == 4) {
        error = "ui.wrongelevation";
      } else if (lr.errorCode == 5) {
        error = "ui.danger";
      } else if (lr.errorCode == 6) {
        error = "ui.notreachable";
      } else {
        error = "ui.unknownerror";
      } 
      if (MillConfigValues.DEV)
        WorldUtilities.setBlock(townHall.mw.world, lr.errorPos.getRelative(0.0D, 30.0D, 0.0D), Blocks.GRAVEL); 
      ServerSender.sendTranslatedSentence(player, '6', "ui.problemat", new String[] { pos.distanceDirectionShort(lr.errorPos), error });
    } else {
      lr.location.level = -1;
      BuildingProject project = new BuildingProject(set);
      project.location = lr.location;
      setSign(townHall, lr.location.minx, lr.location.minz, project);
      setSign(townHall, lr.location.maxx, lr.location.minz, project);
      setSign(townHall, lr.location.minx, lr.location.maxz, project);
      setSign(townHall, lr.location.maxx, lr.location.maxz, project);
      ((CopyOnWriteArrayList<BuildingProject>)townHall.buildingProjects.get(BuildingProject.EnumProjects.CUSTOMBUILDINGS)).add(project);
      townHall.noProjectsLeft = false;
      ServerSender.sendTranslatedSentence(player, '2', "ui.projectadded", new String[0]);
    } 
  }
  
  public static void newCustomBuilding(EntityPlayer player, Building townHall, Point pos, String planKey) {
    BuildingCustomPlan customBuilding = townHall.culture.getBuildingCustom(planKey);
    if (customBuilding != null)
      try {
        townHall.addCustomBuilding(customBuilding, pos);
      } catch (Exception e) {
        MillLog.printException("Exception when creation custom building: " + planKey, e);
      }  
  }
  
  public static void newVillageCreation(EntityPlayer player, Point pos, String cultureKey, String villageTypeKey) {
    Culture culture = Culture.getCultureByName(cultureKey);
    if (culture == null)
      return; 
    VillageType villageType = culture.getVillageType(villageTypeKey);
    if (villageType == null)
      return; 
    WorldGenVillage genVillage = new WorldGenVillage();
    boolean result = genVillage.generateVillageAtPoint(player.world, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, false, 0, villageType, null, null, 0.0F);
    if (result) {
      MillAdvancements.SUMMONING_WAND.grant(player);
      if (villageType.playerControlled && 
        MillAdvancements.VILLAGE_LEADER_ADVANCEMENTS.containsKey(cultureKey))
        ((GenericAdvancement)MillAdvancements.VILLAGE_LEADER_ADVANCEMENTS.get(cultureKey)).grant(player); 
      if (villageType.playerControlled && villageType.customCentre != null)
        MillAdvancements.AMATEUR_ARCHITECT.grant(player); 
    } 
  }
  
  public static void pujasChangeEnchantment(EntityPlayer player, Building temple, int enchantmentId) {
    if (temple != null && temple.pujas != null) {
      temple.pujas.changeEnchantment(enchantmentId);
      temple.sendBuildingPacket(player, false);
      if (temple.pujas.type == 0) {
        MillAdvancements.PUJA.grant(player);
      } else if (temple.pujas.type == 1) {
        MillAdvancements.SACRIFICE.grant(player);
      } 
    } 
  }
  
  public static void questCompleteStep(EntityPlayer player, MillVillager villager) {
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    QuestInstance qi = (QuestInstance)profile.villagersInQuests.get(Long.valueOf(villager.getVillagerId()));
    if (qi == null) {
      MillLog.error(villager, "Could not find quest instance for this villager.");
    } else {
      qi.completeStep(player, villager);
    } 
  }
  
  public static void questRefuse(EntityPlayer player, MillVillager villager) {
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    QuestInstance qi = (QuestInstance)profile.villagersInQuests.get(Long.valueOf(villager.getVillagerId()));
    if (qi == null) {
      MillLog.error(villager, "Could not find quest instance for this villager.");
    } else {
      qi.refuseQuest(player, villager);
    } 
  }
  
  private static void setSign(Building townHall, int i, int j, BuildingProject project) {
    WorldUtilities.setBlockAndMetadata(townHall.world, i, WorldUtilities.findTopSoilBlock(townHall.world, i, j), j, Blocks.STANDING_SIGN, 0, true, false);
    TileEntitySign sign = (TileEntitySign)townHall.world.getTileEntity(new BlockPos(i, WorldUtilities.findTopSoilBlock(townHall.world, i, j), j));
    if (sign != null) {
      sign.signText[0] = (ITextComponent)new TextComponentString(project.getNativeName());
      sign.signText[1] = (ITextComponent)new TextComponentString("");
      sign.signText[2] = (ITextComponent)new TextComponentString(project.getGameName());
      sign.signText[3] = (ITextComponent)new TextComponentString("");
    } 
  }
  
  public static void updateCustomBuilding(EntityPlayer player, Building building) {
    if (building.location.getCustomPlan() != null)
      building.location.getCustomPlan().registerResources(building, building.location); 
  }
  
  public static void useNegationWand(EntityPlayer player, Building townHall) {
    ServerSender.sendTranslatedSentence(player, '4', "negationwand.destroyed", new String[] { townHall.villageType.name });
    if (!townHall.villageType.lonebuilding)
      MillAdvancements.SCIPIO.grant(player); 
    townHall.destroyVillage();
  }
  
  public static EnumActionResult useSummoningWand(EntityPlayerMP player, Point pos) {
    MillWorldData mw = Mill.getMillWorld(player.world);
    Block block = WorldUtilities.getBlock(player.world, pos);
    Building closestVillage = mw.getClosestVillage(pos);
    if (closestVillage != null && pos.squareRadiusDistance(closestVillage.getPos()) < closestVillage.villageType.radius + 10) {
      if (block == Blocks.STANDING_SIGN)
        return EnumActionResult.FAIL; 
      if (closestVillage.controlledBy((EntityPlayer)player)) {
        Building b = closestVillage.getBuildingAtCoordPlanar(pos);
        if (b != null) {
          if (b.location.isCustomBuilding) {
            ServerSender.displayNewBuildingProjectGUI((EntityPlayer)player, closestVillage, pos);
          } else {
            ServerSender.sendTranslatedSentence((EntityPlayer)player, 'e', "ui.wand_locationinuse", new String[0]);
          } 
        } else {
          ServerSender.displayNewBuildingProjectGUI((EntityPlayer)player, closestVillage, pos);
        } 
        return EnumActionResult.SUCCESS;
      } 
      ServerSender.sendTranslatedSentence((EntityPlayer)player, 'e', "ui.wand_invillagerange", new String[] { closestVillage.getVillageQualifiedName() });
      return EnumActionResult.FAIL;
    } 
    if (block == Blocks.STANDING_SIGN) {
      if (!Mill.proxy.isTrueServer() || player.getServer().getPlayerList().canSendCommands(player.getGameProfile())) {
        BuildingImportExport.summoningWandImportBuildingRequest((EntityPlayer)player, ((MillWorldData)Mill.serverWorlds.get(0)).world, pos);
      } else {
        ServerSender.sendTranslatedSentence((EntityPlayer)player, '4', "ui.serverimportforbidden", new String[0]);
      } 
      return EnumActionResult.SUCCESS;
    } 
    if (block == MillBlocks.LOCKED_CHEST)
      return EnumActionResult.PASS; 
    if (block == Blocks.OBSIDIAN) {
      WorldGenVillage genVillage = new WorldGenVillage();
      genVillage.generateVillageAtPoint(player.world, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), (EntityPlayer)player, false, true, false, 0, null, null, null, 0.0F);
      return EnumActionResult.SUCCESS;
    } 
    if (block == Blocks.GOLD_BLOCK) {
      ServerSender.displayNewVillageGUI((EntityPlayer)player, pos);
      return EnumActionResult.SUCCESS;
    } 
    if (mw.getProfile((EntityPlayer)player).isTagSet("normanmarvel_picklocation")) {
      SpecialQuestActions.normanMarvelPickLocation(mw, (EntityPlayer)player, pos);
      return EnumActionResult.SUCCESS;
    } 
    ServerSender.sendTranslatedSentence((EntityPlayer)player, 'f', "ui.wandinstruction", new String[0]);
    return EnumActionResult.FAIL;
  }
  
  public static void villageChiefPerformBuilding(EntityPlayer player, MillVillager chief, String planKey) {
    BuildingPlan plan = (chief.getTownHall()).culture.getBuildingPlanSet(planKey).getRandomStartingPlan();
    (chief.getTownHall()).buildingsBought.add(planKey);
    MillCommonUtilities.changeMoney((IInventory)player.inventory, -plan.price, player);
    ServerSender.sendTranslatedSentence(player, 'f', "ui.housebought", new String[] { chief.getName(), plan.nativeName });
  }
  
  public static void villageChiefPerformCrop(EntityPlayer player, MillVillager chief, String value) {
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    profile.setTag("cropplanting_" + value);
    MillCommonUtilities.changeMoney((IInventory)player.inventory, -512, player);
    Item crop = Item.getByNameOrId("millenaire:" + value);
    ServerSender.sendTranslatedSentence(player, 'f', "ui.croplearned", new String[] { chief.getName(), "ui.crop." + crop.getRegistryName().getResourcePath() });
  }
  
  public static void villageChiefPerformCultureControl(EntityPlayer player, MillVillager chief) {
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    profile.setTag("culturecontrol_" + (chief.getCulture()).key);
    ServerSender.sendTranslatedSentence(player, 'f', "ui.control_gotten", new String[] { chief.getName(), chief.getCulture().getAdjectiveTranslatedKey() });
  }
  
  public static void villageChiefPerformDiplomacy(EntityPlayer player, MillVillager chief, Point village, boolean praise) {
    float effect = 0.0F;
    if (praise) {
      effect = 10.0F;
    } else {
      effect = -10.0F;
    } 
    int reputation = Math.min(chief.getTownHall().getReputation(player), 32768);
    float coeff = (float)((Math.log(reputation) / Math.log(32768.0D) * 2.0D + (reputation / 32768)) / 3.0D);
    effect *= coeff;
    effect = (float)(effect * (MillCommonUtilities.randomInt(40) + 80) / 100.0D);
    chief.getTownHall().adjustRelation(village, (int)effect, false);
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    profile.adjustDiplomacyPoint(chief.getTownHall(), -1);
    if (MillConfigValues.LogVillage >= 1)
      MillLog.major(chief.getTownHall(), "Adjusted relation by " + effect + " (coef: " + coeff + ")"); 
  }
  
  public static void villageChiefPerformHuntingDrop(EntityPlayer player, MillVillager chief, String value) {
    UserProfile profile = Mill.getMillWorld(player.world).getProfile(player);
    profile.setTag("huntingdrop_" + value);
    MillCommonUtilities.changeMoney((IInventory)player.inventory, -512, player);
    Item drop = Item.getByNameOrId("millenaire:" + value);
    ServerSender.sendTranslatedSentence(player, 'f', "ui.huntingdroplearned", new String[] { chief.getName(), "ui.huntingdrop." + drop.getRegistryName().getResourcePath() });
  }
  
  public static void villageChiefPerformVillageScroll(EntityPlayer player, MillVillager chief) {
    for (int i = 0; i < (Mill.getMillWorld(player.world)).villagesList.pos.size(); i++) {
      Point p = (Mill.getMillWorld(player.world)).villagesList.pos.get(i);
      if (chief.getTownHall().getPos().sameBlock(p)) {
        MillCommonUtilities.changeMoney((IInventory)player.inventory, -128, player);
        player.inventory.addItemStackToInventory(ItemParchment.createParchmentForVillage(chief.getTownHall()));
        ServerSender.sendTranslatedSentence(player, 'f', "ui.scrollbought", new String[] { chief.getName() });
      } 
    } 
  }
}
