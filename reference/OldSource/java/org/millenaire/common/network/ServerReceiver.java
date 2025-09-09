package org.millenaire.common.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityImportTable;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.ui.GuiActions;
import org.millenaire.common.utilities.DevModUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.world.MillWorldData;

public class ServerReceiver {
  public static final int PACKET_BUILDING = 2;
  
  public static final int PACKET_VILLAGER = 3;
  
  public static final int PACKET_MILLCHEST = 5;
  
  public static final int PACKET_MAPINFO = 7;
  
  public static final int PACKET_VILLAGELIST = 9;
  
  public static final int PACKET_SERVER_CONTENT = 10;
  
  public static final int PACKET_SHOP = 11;
  
  public static final int PACKET_ALL_VILLAGER_RECORD = 12;
  
  public static final int PACKET_TRANSLATED_CHAT = 100;
  
  public static final int PACKET_PROFILE = 101;
  
  public static final int PACKET_QUESTINSTANCE = 102;
  
  public static final int PACKET_QUESTINSTANCE_DESTROY = 103;
  
  public static final int PACKET_OPENGUI = 104;
  
  public static final int PACKET_ANIMALBREED = 107;
  
  public static final int PACKET_VILLAGER_SENTENCE = 108;
  
  public static final int PACKET_ADVANCEMENT_EARNED = 109;
  
  public static final int PACKET_CONTENT_UNLOCKED = 110;
  
  public static final int PACKET_CONTENT_UNLOCKED_MULTIPLE = 111;
  
  public static final int PACKET_GUIACTION = 200;
  
  public static final int PACKET_VILLAGELIST_REQUEST = 201;
  
  public static final int PACKET_DECLARERELEASENUMBER = 202;
  
  public static final int PACKET_MAPINFO_REQUEST = 203;
  
  public static final int PACKET_VILLAGERINTERACT_REQUEST = 204;
  
  public static final int PACKET_AVAILABLECONTENT = 205;
  
  public static final int PACKET_DEVCOMMAND = 206;
  
  public static final int GUIACTION_CHIEF_BUILDING = 1;
  
  public static final int GUIACTION_CHIEF_CROP = 2;
  
  public static final int GUIACTION_CHIEF_CONTROL = 3;
  
  public static final int GUIACTION_CHIEF_DIPLOMACY = 4;
  
  public static final int GUIACTION_CHIEF_SCROLL = 5;
  
  public static final int GUIACTION_CHIEF_HUNTING_DROP = 6;
  
  public static final int GUIACTION_QUEST_COMPLETESTEP = 10;
  
  public static final int GUIACTION_QUEST_REFUSE = 11;
  
  public static final int GUIACTION_NEWVILLAGE = 20;
  
  public static final int GUIACTION_HIRE_HIRE = 30;
  
  public static final int GUIACTION_HIRE_EXTEND = 31;
  
  public static final int GUIACTION_HIRE_RELEASE = 32;
  
  public static final int GUIACTION_TOGGLE_STANCE = 33;
  
  public static final int GUIACTION_NEGATION_WAND = 40;
  
  public static final int GUIACTION_NEW_BUILDING_PROJECT = 50;
  
  public static final int GUIACTION_NEW_CUSTOM_BUILDING_PROJECT = 51;
  
  public static final int GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT = 52;
  
  public static final int GUIACTION_PUJAS_CHANGE_ENCHANTMENT = 60;
  
  public static final int GUIACTION_TRADE_TOGGLE_DONATION = 61;
  
  public static final int GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED = 70;
  
  public static final int GUIACTION_CONTROLLEDBUILDING_FORGET = 71;
  
  public static final int GUIACTION_MILLCHESTACTIVATE = 81;
  
  public static final int GUIACTION_MILITARY_RELATIONS = 90;
  
  public static final int GUIACTION_MILITARY_RAID = 91;
  
  public static final int GUIACTION_MILITARY_CANCEL_RAID = 92;
  
  public static final int GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN = 100;
  
  public static final int GUIACTION_IMPORTTABLE_CHANGESETTINGS = 101;
  
  public static final int GUIACTION_IMPORTTABLE_CREATEBUILDING = 102;
  
  public static final int DEV_COMMAND_TOGGLE_AUTO_MOVE = 1;
  
  public static final int DEV_COMMAND_TEST_PATH = 2;
  
  @SubscribeEvent
  public void onPacketData(FMLNetworkEvent.ServerCustomPacketEvent event) {
    if (Mill.serverWorlds.size() == 0)
      return; 
    final PacketBuffer packetBuffer = new PacketBuffer(event.getPacket().payload());
    final EntityPlayerMP sender = ((NetHandlerPlayServer)event.getHandler()).player;
    sender.getServer().addScheduledTask(new Runnable() {
          public void run() {
            ServerReceiver.this.processPacket(sender, packetBuffer);
          }
        });
  }
  
  private void processPacket(EntityPlayerMP sender, PacketBuffer packetBuffer) {
    MillWorldData mw = Mill.getMillWorld(sender.world);
    if (mw == null)
      mw = Mill.serverWorlds.get(0); 
    if (mw == null)
      MillLog.error(this, "ServerReceiver.onPacketData: could not find MillWorldData."); 
    int packettype = packetBuffer.readInt();
    if (MillConfigValues.LogNetwork >= 3)
      MillLog.debug(this, "Receiving packet type " + packettype); 
    if (packettype == 200) {
      readGuiActionPacket((EntityPlayer)sender, packetBuffer);
    } else if (packettype == 203) {
      readMapInfoRequestPacket((EntityPlayer)sender, packetBuffer);
    } else if (packettype == 201) {
      mw.displayVillageList((EntityPlayer)sender, packetBuffer.readBoolean());
    } else if (packettype == 202) {
      mw.getProfile((EntityPlayer)sender).receiveDeclareReleaseNumberPacket(packetBuffer);
    } else if (packettype == 204) {
      readVillagerInteractRequestPacket((EntityPlayer)sender, packetBuffer);
    } else if (packettype == 205) {
      readAvailableContentPacket((EntityPlayer)sender, packetBuffer);
    } else if (packettype == 206) {
      readDevCommandPacket((EntityPlayer)sender, packetBuffer);
    } 
  }
  
  private void readAvailableContentPacket(EntityPlayer player, PacketBuffer packetBuffer) {
    HashMap<String, Integer> nbStrings = new HashMap<>();
    HashMap<String, Integer> nbBuildingNames = new HashMap<>();
    HashMap<String, Integer> nbSentences = new HashMap<>();
    HashMap<String, Integer> nbFallbackStrings = new HashMap<>();
    HashMap<String, Integer> nbFallbackBuildingNames = new HashMap<>();
    HashMap<String, Integer> nbFallbackSentences = new HashMap<>();
    HashMap<String, List<String>> planSets = new HashMap<>();
    HashMap<String, List<String>> villagers = new HashMap<>();
    HashMap<String, List<String>> villages = new HashMap<>();
    HashMap<String, List<String>> lonebuildings = new HashMap<>();
    try {
      String clientMainLanguage = packetBuffer.readString(2048);
      String clientFallbackLanguage = packetBuffer.readString(2048);
      int nbCultures = packetBuffer.readShort();
      for (int i = 0; i < nbCultures; i++) {
        String key = packetBuffer.readString(2048);
        nbStrings.put(key, Integer.valueOf(packetBuffer.readShort()));
        nbBuildingNames.put(key, Integer.valueOf(packetBuffer.readShort()));
        nbSentences.put(key, Integer.valueOf(packetBuffer.readShort()));
        nbFallbackStrings.put(key, Integer.valueOf(packetBuffer.readShort()));
        nbFallbackBuildingNames.put(key, Integer.valueOf(packetBuffer.readShort()));
        nbFallbackSentences.put(key, Integer.valueOf(packetBuffer.readShort()));
        List<String> v = new ArrayList<>();
        int nb = packetBuffer.readShort();
        int j;
        for (j = 0; j < nb; j++)
          v.add(packetBuffer.readString(2048)); 
        planSets.put(key, v);
        v = new ArrayList<>();
        nb = packetBuffer.readShort();
        for (j = 0; j < nb; j++)
          v.add(packetBuffer.readString(2048)); 
        villagers.put(key, v);
        v = new ArrayList<>();
        nb = packetBuffer.readShort();
        for (j = 0; j < nb; j++)
          v.add(packetBuffer.readString(2048)); 
        villages.put(key, v);
        v = new ArrayList<>();
        nb = packetBuffer.readShort();
        for (j = 0; j < nb; j++)
          v.add(packetBuffer.readString(2048)); 
        lonebuildings.put(key, v);
      } 
      PacketBuffer data = ServerSender.getPacketBuffer();
      data.writeInt(10);
      data.writeShort(Culture.ListCultures.size());
      for (Culture culture : Culture.ListCultures) {
        if (!nbStrings.containsKey(culture.key)) {
          culture.writeCultureMissingContentPackPacket(data, clientMainLanguage, clientFallbackLanguage, 0, 0, 0, 0, 0, 0, null, null, null, null);
          continue;
        } 
        culture.writeCultureMissingContentPackPacket(data, clientMainLanguage, clientFallbackLanguage, ((Integer)nbStrings.get(culture.key)).intValue(), ((Integer)nbBuildingNames.get(culture.key)).intValue(), ((Integer)nbSentences
            .get(culture.key)).intValue(), ((Integer)nbFallbackStrings.get(culture.key)).intValue(), ((Integer)nbFallbackBuildingNames.get(culture.key)).intValue(), ((Integer)nbFallbackSentences.get(culture.key)).intValue(), planSets.get(culture.key), villagers
            .get(culture.key), villages.get(culture.key), lonebuildings.get(culture.key));
      } 
      ServerSender.sendPacketToPlayer(data, player);
    } catch (IOException e) {
      MillLog.printException("Error in readAvailableContentPacket: ", e);
    } 
  }
  
  private void readDevCommandPacket(EntityPlayer player, PacketBuffer packetBuffer) {
    int commandId = packetBuffer.readInt();
    if (commandId == 1) {
      DevModUtilities.toggleAutoMove(player);
    } else if (commandId == 2) {
      DevModUtilities.testPaths(player);
    } 
  }
  
  private void readGuiActionPacket(EntityPlayer player, PacketBuffer packetBuffer) {
    MillWorldData mw = Mill.getMillWorld(player.world);
    int guiActionId = packetBuffer.readInt();
    if (guiActionId == 1) {
      MillVillager v = mw.getVillagerById(packetBuffer.readLong());
      if (v != null) {
        GuiActions.villageChiefPerformBuilding(player, v, packetBuffer.readString(2048));
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiActionId);
      } 
    } else if (guiActionId == 2) {
      MillVillager v = mw.getVillagerById(packetBuffer.readLong());
      if (v != null) {
        GuiActions.villageChiefPerformCrop(player, v, packetBuffer.readString(2048));
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiActionId);
      } 
    } else if (guiActionId == 6) {
      MillVillager v = mw.getVillagerById(packetBuffer.readLong());
      if (v != null) {
        GuiActions.villageChiefPerformHuntingDrop(player, v, packetBuffer.readString(2048));
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiActionId);
      } 
    } else if (guiActionId == 3) {
      MillVillager v = mw.getVillagerById(packetBuffer.readLong());
      if (v != null) {
        GuiActions.villageChiefPerformCultureControl(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiActionId);
      } 
    } else if (guiActionId == 4) {
      MillVillager v = mw.getVillagerById(packetBuffer.readLong());
      if (v != null) {
        GuiActions.villageChiefPerformDiplomacy(player, v, StreamReadWrite.readNullablePoint(packetBuffer), packetBuffer.readBoolean());
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiActionId);
      } 
    } else if (guiActionId == 5) {
      long vid = packetBuffer.readLong();
      MillVillager v = mw.getVillagerById(vid);
      if (v != null) {
        GuiActions.villageChiefPerformVillageScroll(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + vid);
      } 
    } else if (guiActionId == 10) {
      long vid = packetBuffer.readLong();
      MillVillager v = mw.getVillagerById(vid);
      if (v != null) {
        GuiActions.questCompleteStep(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + vid);
      } 
    } else if (guiActionId == 11) {
      long vid = packetBuffer.readLong();
      MillVillager v = mw.getVillagerById(vid);
      if (v != null) {
        GuiActions.questRefuse(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + vid);
      } 
    } else if (guiActionId == 20) {
      String cultureKey = packetBuffer.readString(2048);
      String villageType = packetBuffer.readString(2048);
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      GuiActions.newVillageCreation(player, pos, cultureKey, villageType);
    } else if (guiActionId == 40) {
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      Building th = mw.getBuilding(pos);
      if (th != null)
        GuiActions.useNegationWand(player, th); 
    } else if (guiActionId == 81) {
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      GuiActions.activateMillChest(player, pos);
    } else if (guiActionId == 60) {
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      Building temple = mw.getBuilding(pos);
      if (temple != null && temple.pujas != null)
        GuiActions.pujasChangeEnchantment(player, temple, packetBuffer.readShort()); 
    } else if (guiActionId == 61) {
      boolean donation = packetBuffer.readBoolean();
      (mw.getProfile(player)).donationActivated = donation;
    } else if (guiActionId == 50) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      String planKey = packetBuffer.readString(2048);
      Building th = mw.getBuilding(thPos);
      if (th != null)
        GuiActions.newBuilding(player, th, pos, planKey); 
    } else if (guiActionId == 51) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      String planKey = packetBuffer.readString(2048);
      Building th = mw.getBuilding(thPos);
      if (th != null)
        GuiActions.newCustomBuilding(player, th, pos, planKey); 
    } else if (guiActionId == 52) {
      Point pos = StreamReadWrite.readNullablePoint(packetBuffer);
      Building building = mw.getBuilding(pos);
      if (building != null)
        GuiActions.updateCustomBuilding(player, building); 
    } else if (guiActionId == 70) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      String projectKey = packetBuffer.readString(2048);
      Point projectPos = StreamReadWrite.readNullablePoint(packetBuffer);
      boolean allow = packetBuffer.readBoolean();
      Building th = mw.getBuilding(thPos);
      if (th != null) {
        BuildingProject project = null;
        for (BuildingProject p : th.getFlatProjectList()) {
          if (p.key.equals(projectKey) && p.location != null && p.location.pos.equals(projectPos))
            project = p; 
        } 
        if (project != null)
          GuiActions.controlledBuildingsToggleUpgrades(player, th, project, allow); 
      } 
    } else if (guiActionId == 71) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      String projectKey = packetBuffer.readString(2048);
      Point projectPos = StreamReadWrite.readNullablePoint(packetBuffer);
      Building th = mw.getBuilding(thPos);
      if (th != null) {
        BuildingProject project = null;
        for (BuildingProject p : th.getFlatProjectList()) {
          if (p.key.equals(projectKey) && p.location != null && p.location.pos.equals(projectPos))
            project = p; 
        } 
        if (project != null)
          GuiActions.controlledBuildingsForgetBuilding(player, th, project); 
      } 
    } else if (guiActionId == 30) {
      long vid = packetBuffer.readLong();
      MillVillager v = mw.getVillagerById(vid);
      if (v != null) {
        GuiActions.hireHire(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + vid);
      } 
    } else if (guiActionId == 31) {
      long vid = packetBuffer.readLong();
      MillVillager v = mw.getVillagerById(vid);
      if (v != null) {
        GuiActions.hireExtend(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + vid);
      } 
    } else if (guiActionId == 32) {
      long vid = packetBuffer.readLong();
      MillVillager v = mw.getVillagerById(vid);
      if (v != null) {
        GuiActions.hireRelease(player, v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + vid);
      } 
    } else if (guiActionId == 33) {
      boolean stance = packetBuffer.readBoolean();
      GuiActions.hireToggleStance(player, stance);
    } else if (guiActionId == 90) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      Point targetpos = StreamReadWrite.readNullablePoint(packetBuffer);
      int amount = packetBuffer.readInt();
      Building th = mw.getBuilding(thPos);
      if (th != null)
        GuiActions.controlledMilitaryDiplomacy(player, th, targetpos, amount); 
    } else if (guiActionId == 91) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      Point targetpos = StreamReadWrite.readNullablePoint(packetBuffer);
      Building th = mw.getBuilding(thPos);
      Building target = mw.getBuilding(targetpos);
      if (th != null)
        GuiActions.controlledMilitaryPlanRaid(player, th, target); 
    } else if (guiActionId == 92) {
      Point thPos = StreamReadWrite.readNullablePoint(packetBuffer);
      Building th = mw.getBuilding(thPos);
      if (th != null)
        GuiActions.controlledMilitaryCancelRaid(player, th); 
    } else if (guiActionId == 100) {
      Point tablePos = StreamReadWrite.readNullablePoint(packetBuffer);
      String source = StreamReadWrite.readNullableString(packetBuffer);
      String buildingKey = StreamReadWrite.readNullableString(packetBuffer);
      boolean importAll = packetBuffer.readBoolean();
      int variation = packetBuffer.readInt();
      int level = packetBuffer.readInt();
      int orientation = packetBuffer.readInt();
      boolean importMockBlocks = packetBuffer.readBoolean();
      BuildingImportExport.importTableHandleImportRequest(player, tablePos, source, buildingKey, importAll, variation, level, orientation, importMockBlocks);
    } else if (guiActionId == 101) {
      Point tablePos = StreamReadWrite.readNullablePoint(packetBuffer);
      int upgradeLevel = packetBuffer.readInt();
      int orientation = packetBuffer.readInt();
      int startingLevel = packetBuffer.readInt();
      boolean exportSnow = packetBuffer.readBoolean();
      boolean importMockBlocks = packetBuffer.readBoolean();
      boolean autoconvertToPreserveGround = packetBuffer.readBoolean();
      boolean exportRegularChests = packetBuffer.readBoolean();
      TileEntityImportTable importTable = tablePos.getImportTable(player.world);
      if (importTable != null) {
        importTable.updateSettings(upgradeLevel, orientation, startingLevel, exportSnow, importMockBlocks, autoconvertToPreserveGround, exportRegularChests, player);
      } else {
        MillLog.error(null, "Received an update packet for a missing ImportTable at: " + tablePos);
      } 
    } else if (guiActionId == 102) {
      Point tablePos = StreamReadWrite.readNullablePoint(packetBuffer);
      int length = packetBuffer.readInt();
      int width = packetBuffer.readInt();
      int startingLevel = packetBuffer.readInt();
      boolean clearGround = packetBuffer.readBoolean();
      TileEntityImportTable importTable = tablePos.getImportTable(player.world);
      if (importTable != null) {
        BuildingImportExport.importTableCreateNewBuilding(player, importTable, length, width, startingLevel, clearGround);
      } else {
        MillLog.error(null, "Received an update packet for a missing ImportTable at: " + tablePos);
      } 
    } else {
      MillLog.error(null, "Unknown Gui action: " + guiActionId);
    } 
  }
  
  private void readMapInfoRequestPacket(EntityPlayer player, PacketBuffer packetBuffer) {
    MillWorldData mw = Mill.getMillWorld(player.world);
    Point p = StreamReadWrite.readNullablePoint(packetBuffer);
    Building townHall = mw.getBuilding(p);
    if (townHall != null)
      townHall.sendMapInfo(player); 
  }
  
  private void readVillagerInteractRequestPacket(EntityPlayer player, PacketBuffer packetBuffer) {
    MillWorldData mw = Mill.getMillWorld(player.world);
    long vid = packetBuffer.readLong();
    if (mw.getVillagerById(vid) != null)
      mw.getVillagerById(vid).interactSpecial(player); 
  }
}
