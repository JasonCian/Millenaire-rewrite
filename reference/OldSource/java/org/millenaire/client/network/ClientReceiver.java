package org.millenaire.client.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.millenaire.client.MillClientUtilities;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.gui.UnlockingToast;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.ui.MillMapInfo;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.UserProfile;

public class ClientReceiver {
  @SubscribeEvent
  public void onPacketData(FMLNetworkEvent.ClientCustomPacketEvent event) {
    if (FMLCommonHandler.instance().getSide().isServer() && MillConfigValues.LogNetwork >= 1) {
      MillLog.major(this, "Received a packet despite being server.");
      return;
    } 
    if (event.getPacket() == null) {
      MillLog.error(this, "Received a null packet!");
      return;
    } 
    if (event.getPacket().payload() == null) {
      MillLog.error(this, "Received a packet with null data on channel: " + event.getPacket().channel());
      return;
    } 
    if (Mill.clientWorld == null) {
      MillLog.error(this, "Received a packet despite null clientWorld.");
      return;
    } 
    final PacketBuffer data = new PacketBuffer(event.getPacket().payload());
    Minecraft.getMinecraft().addScheduledTask(new Runnable() {
          public void run() {
            ClientReceiver.this.processPacket(data);
          }
        });
  }
  
  private void processPacket(PacketBuffer data) {
    try {
      int packettype = data.readInt();
      Mill.clientWorld.millenaireEnabled = true;
      if (MillConfigValues.LogNetwork >= 3)
        MillLog.debug(this, "Received client packet type: " + packettype); 
      UserProfile profile = Mill.proxy.getClientProfile();
      if (packettype == 2) {
        Building.readBuildingPacket(Mill.clientWorld, data);
      } else if (packettype == 11) {
        Building.readShopPacket(Mill.clientWorld, data);
      } else if (packettype == 3) {
        MillVillager.readVillagerPacket(data);
      } else if (packettype == 100) {
        readTranslatedChatPackage(data);
      } else if (packettype == 108) {
        readVillagerSentencePackage(data);
      } else if (packettype == 109) {
        readAdvancementPackage(data);
      } else if (packettype == 110) {
        readContentUnlockedPackage(data);
      } else if (packettype == 111) {
        readContentUnlockedPackageMultiple(data);
      } else if (packettype == 5) {
        TileEntityLockedChest.readUpdatePacket(data, Mill.clientWorld.world);
      } else if (packettype == 101) {
        profile.receiveProfilePacket(data);
      } else if (packettype == 102) {
        profile.receiveQuestInstancePacket(data);
      } else if (packettype == 103) {
        profile.receiveQuestInstanceDestroyPacket(data);
      } else if (packettype == 104) {
        readGUIPacket(data);
      } else if (packettype == 7) {
        MillMapInfo.readPacket(data);
      } else if (packettype == 9) {
        Mill.clientWorld.receiveVillageListPacket(data);
      } else if (packettype == 10) {
        readServerContentPacket(data);
      } else if (packettype == 107) {
        readAnimalBreedPacket(data);
      } else {
        MillLog.error(null, "Received packet with unknown type: " + packettype);
      } 
    } catch (Exception e) {
      MillLog.printException("Error in ClientReceiver.onPacketData:", e);
    } 
  }
  
  private void readAdvancementPackage(PacketBuffer data) {
    String advancementKey = data.readString(2048);
    MillAdvancements.addToStats(Mill.proxy.getTheSinglePlayer(), advancementKey);
  }
  
  private void readAnimalBreedPacket(PacketBuffer data) {
    Point p = StreamReadWrite.readNullablePoint(data);
    int endId = data.readInt();
    List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(Mill.clientWorld.world, EntityAnimal.class, p, 5, 5);
    for (Entity ent : animals) {
      EntityAnimal animal = (EntityAnimal)ent;
      if (animal.getEntityId() == endId) {
        animal.setInLove(null);
        MillCommonUtilities.generateHearts((Entity)animal);
      } 
    } 
  }
  
  private void readContentUnlockedPackage(PacketBuffer data) {
    int contentType = data.readInt();
    String cultureKey = data.readString(2048);
    String contentKey = data.readString(2048);
    int nbUnlocked = data.readInt();
    int nbTotal = data.readInt();
    Culture culture = Culture.getCultureByName(cultureKey);
    if (culture != null)
      if (contentType == 1) {
        BuildingPlanSet planSet = culture.getBuildingPlanSet(contentKey);
        if (planSet != null)
          Minecraft.getMinecraft().getToastGui().add((IToast)new UnlockingToast(planSet, nbUnlocked, nbTotal)); 
      } else if (contentType == 4) {
        TradeGood tradeGood = culture.getTradeGood(contentKey);
        if (tradeGood != null)
          Minecraft.getMinecraft().getToastGui().add((IToast)new UnlockingToast(tradeGood, nbUnlocked, nbTotal)); 
      } else if (contentType == 2) {
        VillageType villageType = culture.getVillageType(contentKey);
        if (villageType != null)
          Minecraft.getMinecraft().getToastGui().add((IToast)new UnlockingToast(villageType, nbUnlocked, nbTotal)); 
      } else if (contentType == 3) {
        VillagerType villagerType = culture.getVillagerType(contentKey);
        if (villagerType != null)
          Minecraft.getMinecraft().getToastGui().add((IToast)new UnlockingToast(villagerType, nbUnlocked, nbTotal)); 
      }  
  }
  
  private void readContentUnlockedPackageMultiple(PacketBuffer data) {
    int contentType = data.readInt();
    String cultureKey = data.readString(2048);
    List<String> contentKeys = StreamReadWrite.readStringList(data);
    int nbUnlocked = data.readInt();
    int nbTotal = data.readInt();
    Culture culture = Culture.getCultureByName(cultureKey);
    if (culture != null)
      if (contentType == 5) {
        List<TradeGood> tradeGoods = new ArrayList<>();
        for (String contentKey : contentKeys) {
          TradeGood tradeGood = culture.getTradeGood(contentKey);
          if (tradeGood != null)
            tradeGoods.add(tradeGood); 
        } 
        if (tradeGoods.size() > 0)
          Minecraft.getMinecraft().getToastGui().add((IToast)new UnlockingToast(tradeGoods, nbUnlocked, nbTotal)); 
      }  
  }
  
  private void readGUIPacket(PacketBuffer data) {
    int guiId = data.readInt();
    if (guiId == 3) {
      MillVillager v = Mill.clientWorld.getVillagerById(data.readLong());
      if (v != null) {
        DisplayActions.displayQuestGUI(Mill.proxy.getTheSinglePlayer(), v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 12) {
      MillVillager v = Mill.clientWorld.getVillagerById(data.readLong());
      if (v != null) {
        DisplayActions.displayHireGUI(Mill.proxy.getTheSinglePlayer(), v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 4) {
      MillVillager v = Mill.clientWorld.getVillagerById(data.readLong());
      if (v != null) {
        DisplayActions.displayVillageChiefGUI(Mill.proxy.getTheSinglePlayer(), v);
      } else {
        MillLog.error(this, "Unknown villager id in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 5) {
      Point p = StreamReadWrite.readNullablePoint(data);
      if (p != null) {
        DisplayActions.displayVillageBookGUI(Mill.proxy.getTheSinglePlayer(), p);
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 9) {
      Point p = StreamReadWrite.readNullablePoint(data);
      if (p != null) {
        Building building = Mill.clientWorld.getBuilding(p);
        if (building != null)
          DisplayActions.displayNegationWandGUI(Mill.proxy.getTheSinglePlayer(), building); 
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 10) {
      Point thPos = StreamReadWrite.readNullablePoint(data);
      Point pos = StreamReadWrite.readNullablePoint(data);
      if (thPos != null && pos != null) {
        Building townHall = Mill.clientWorld.getBuilding(thPos);
        if (townHall != null) {
          Building building = townHall.getBuildingAtCoordPlanar(pos);
          if (building == null || !building.location.isCustomBuilding) {
            DisplayActions.displayNewBuildingProjectGUI(Mill.proxy.getTheSinglePlayer(), townHall, pos);
          } else {
            DisplayActions.displayEditCustomBuildingGUI(Mill.proxy.getTheSinglePlayer(), building);
          } 
        } 
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 13) {
      Point pos = StreamReadWrite.readNullablePoint(data);
      if (pos != null) {
        DisplayActions.displayNewVillageGUI(Mill.proxy.getTheSinglePlayer(), pos);
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 11) {
      Point thPos = StreamReadWrite.readNullablePoint(data);
      if (thPos != null) {
        Building building = Mill.clientWorld.getBuilding(thPos);
        if (building != null)
          DisplayActions.displayControlledProjectGUI(Mill.proxy.getTheSinglePlayer(), building); 
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 14) {
      Point thPos = StreamReadWrite.readNullablePoint(data);
      if (thPos != null) {
        Building building = Mill.clientWorld.getBuilding(thPos);
        if (building != null)
          DisplayActions.displayControlledMilitaryGUI(Mill.proxy.getTheSinglePlayer(), building); 
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 7) {
      Point p = StreamReadWrite.readNullablePoint(data);
      if (p != null) {
        MillClientUtilities.displayPanel(Mill.clientWorld.world, Mill.proxy.getTheSinglePlayer(), p);
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 2) {
      Point p = StreamReadWrite.readNullablePoint(data);
      if (p != null) {
        Mill.proxy.getTheSinglePlayer().openGui(Mill.instance, 2, Mill.clientWorld.world, p.getiX(), p.getiY(), p.getiZ());
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 8) {
      int id1 = data.readInt();
      int id2 = data.readInt();
      Mill.proxy.getTheSinglePlayer().openGui(Mill.instance, 8, Mill.clientWorld.world, id1, id2, 0);
    } else if (guiId == 1) {
      Point p = StreamReadWrite.readNullablePoint(data);
      if (p != null) {
        TileEntityLockedChest chest = p.getMillChest(Mill.clientWorld.world);
        if (chest != null && chest.loaded)
          Mill.proxy.getTheSinglePlayer().openGui(Mill.instance, 1, Mill.clientWorld.world, p.getiX(), p.getiY(), p.getiZ()); 
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else if (guiId == 15) {
      Point tablePos = StreamReadWrite.readNullablePoint(data);
      if (tablePos != null) {
        DisplayActions.displayImportTableGUI(Mill.proxy.getTheSinglePlayer(), tablePos);
      } else {
        MillLog.error(this, "Unknown point in readGUIPacket: " + guiId);
      } 
    } else {
      MillLog.error(null, "Unknown GUI: " + guiId);
    } 
  }
  
  private void readServerContentPacket(PacketBuffer data) {
    int nbCultures = data.readShort();
    for (int i = 0; i < nbCultures; i++)
      Culture.readCultureMissingContentPacket(data); 
    Culture.refreshLists();
  }
  
  private void readTranslatedChatPackage(PacketBuffer data) {
    char colour = data.readChar();
    String s = data.readString(2048);
    String[] values = new String[data.readInt()];
    for (int i = 0; i < values.length; i++)
      values[i] = LanguageUtilities.unknownString(StreamReadWrite.readNullableString(data)); 
    s = LanguageUtilities.string(s, values);
    Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), colour, s);
  }
  
  private void readVillagerSentencePackage(PacketBuffer data) {
    MillVillager v = Mill.clientWorld.getVillagerById(data.readLong());
    if (v != null)
      MillClientUtilities.putVillagerSentenceInChat(v); 
  }
}
