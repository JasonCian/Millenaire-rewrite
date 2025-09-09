package org.millenaire.common.network;

import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class ServerSender {
  public static void displayControlledMilitaryGUI(EntityPlayer player, Building townHall) {
    townHall.sendBuildingPacket(player, false);
    MillWorldData mw = Mill.getMillWorld(player.world);
    for (Point p : townHall.getKnownVillages()) {
      Building b = mw.getBuilding(p);
      if (b != null)
        b.sendBuildingPacket(player, false); 
    } 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(14);
    StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayControlledProjectGUI(EntityPlayer player, Building townHall) {
    townHall.sendBuildingPacket(player, false);
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(11);
    StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayHireGUI(EntityPlayer player, MillVillager villager) {
    villager.getTownHall().sendBuildingPacket(player, false);
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(12);
    data.writeLong(villager.getVillagerId());
    sendPacketToPlayer(data, player);
  }
  
  public static void displayImportTableGUI(EntityPlayer player, Point tableLocation) {
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(15);
    StreamReadWrite.writeNullablePoint(tableLocation, data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayMerchantTradeGUI(EntityPlayer player, MillVillager villager) {
    PacketBuffer data = getPacketBuffer();
    int[] ids = MillCommonUtilities.packLong(villager.getVillagerId());
    data.writeInt(104);
    data.writeInt(8);
    data.writeInt(ids[0]);
    data.writeInt(ids[1]);
    villager.getHouse().sendBuildingPacket(player, true);
    villager.getTownHall().sendBuildingPacket(player, true);
    sendPacketToPlayer(data, player);
    player.openGui(Mill.instance, 8, player.world, ids[0], ids[1], 0);
  }
  
  public static void displayMillChest(EntityPlayer player, Point chestPos) {
    TileEntityLockedChest chest = chestPos.getMillChest(player.world);
    if (chest == null)
      return; 
    MillWorldData mw = Mill.getMillWorld(player.world);
    if (chest.buildingPos != null) {
      Building building = mw.getBuilding(chest.buildingPos);
      if (building != null) {
        building.sendBuildingPacket(player, true);
      } else {
        chest.buildingPos = null;
        chest.sendUpdatePacket(player);
      } 
    } else {
      chest.sendUpdatePacket(player);
    } 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(1);
    StreamReadWrite.writeNullablePoint(chestPos, data);
    data.writeBoolean(chest.isLockedFor(player));
    sendPacketToPlayer(data, player);
    player.openGui(Mill.instance, 1, player.world, chestPos.getiX(), chestPos.getiY(), chestPos.getiZ());
  }
  
  public static void displayNegationWandGUI(EntityPlayer player, Building townHall) {
    townHall.sendBuildingPacket(player, false);
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(9);
    StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayNewBuildingProjectGUI(EntityPlayer player, Building townHall, Point pos) {
    townHall.sendBuildingPacket(player, false);
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(10);
    StreamReadWrite.writeNullablePoint(townHall.getPos(), data);
    StreamReadWrite.writeNullablePoint(pos, data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayNewVillageGUI(EntityPlayer player, Point pos) {
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(13);
    StreamReadWrite.writeNullablePoint(pos, data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayPanel(EntityPlayer player, Point signPos) {
    TileEntityPanel panel = signPos.getPanel(player.world);
    if (panel == null)
      return; 
    MillWorldData mw = Mill.getMillWorld(player.world);
    if (panel.buildingPos != null) {
      Building building = mw.getBuilding(panel.buildingPos);
      if (building != null)
        building.sendBuildingPacket(player, true); 
    } 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(7);
    StreamReadWrite.writeNullablePoint(signPos, data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayQuestGUI(EntityPlayer player, MillVillager villager) {
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(3);
    data.writeLong(villager.getVillagerId());
    sendPacketToPlayer(data, player);
  }
  
  public static void displayVillageBookGUI(EntityPlayer player, Point p) {
    MillWorldData mw = Mill.getMillWorld(player.world);
    Building th = mw.getBuilding(p);
    if (th == null)
      return; 
    th.sendBuildingPacket(player, true);
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(5);
    StreamReadWrite.writeNullablePoint(p, data);
    sendPacketToPlayer(data, player);
  }
  
  public static void displayVillageChiefGUI(EntityPlayer player, MillVillager chief) {
    if (chief.getTownHall() == null) {
      MillLog.error(chief, "Needed to send chief's TH but TH is null.");
      return;
    } 
    chief.getTownHall().sendBuildingPacket(player, false);
    MillWorldData mw = Mill.getMillWorld(player.world);
    for (Point p : chief.getTownHall().getKnownVillages()) {
      Building b = mw.getBuilding(p);
      if (b != null)
        b.sendBuildingPacket(player, false); 
    } 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(4);
    data.writeLong(chief.getVillagerId());
    sendPacketToPlayer(data, player);
  }
  
  public static void displayVillageTradeGUI(EntityPlayer player, Building building) {
    building.computeShopGoods(player);
    building.sendShopPacket(player);
    building.sendBuildingPacket(player, true);
    if (!building.isTownhall)
      building.getTownHall().sendBuildingPacket(player, false); 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(104);
    data.writeInt(2);
    StreamReadWrite.writeNullablePoint(building.getPos(), data);
    sendPacketToPlayer(data, player);
    player.openGui(Mill.instance, 2, player.world, building.getPos().getiX(), building.getPos().getiY(), building.getPos().getiZ());
  }
  
  public static PacketBuffer getPacketBuffer() {
    return new PacketBuffer(Unpooled.buffer());
  }
  
  public static void sendAdvancementEarned(EntityPlayerMP player, String advancementKey) {
    if (player == null)
      return; 
    if (!(player instanceof EntityPlayerMP))
      return; 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(109);
    data.writeString(advancementKey);
    sendPacketToPlayer(data, (EntityPlayer)player);
  }
  
  public static void sendAnimalBreeding(EntityAnimal animal) {
    PacketBuffer data = getPacketBuffer();
    Point pos = new Point((Entity)animal);
    data.writeInt(107);
    StreamReadWrite.writeNullablePoint(pos, data);
    data.writeInt(animal.getEntityId());
    sendPacketToPlayersInRange(data, pos, 50);
  }
  
  public static void sendChat(EntityPlayer player, TextFormatting colour, String s) {
    TextComponentString chat = new TextComponentString(s);
    chat.getStyle().setColor(colour);
    player.sendMessage((ITextComponent)chat);
  }
  
  public static void sendContentUnlocked(EntityPlayer player, int contentType, String cultureKey, String contentKey, int nbUnlocked, int nbTotal) {
    if (player == null)
      return; 
    if (!(player instanceof EntityPlayerMP))
      return; 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(110);
    data.writeInt(contentType);
    data.writeString(cultureKey);
    data.writeString(contentKey);
    data.writeInt(nbUnlocked);
    data.writeInt(nbTotal);
    sendPacketToPlayer(data, player);
  }
  
  public static void sendContentUnlockedMultiple(EntityPlayer player, int contentType, String cultureKey, List<String> contentKeys, int nbUnlocked, int nbTotal) {
    if (player == null)
      return; 
    if (!(player instanceof EntityPlayerMP))
      return; 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(111);
    data.writeInt(contentType);
    data.writeString(cultureKey);
    StreamReadWrite.writeStringList(contentKeys, data);
    data.writeInt(nbUnlocked);
    data.writeInt(nbTotal);
    sendPacketToPlayer(data, player);
  }
  
  public static void sendLockedChestUpdatePacket(TileEntityLockedChest chest, EntityPlayer player) {
    PacketBuffer data = getPacketBuffer();
    Point pos = new Point(chest.getPos());
    data.writeInt(5);
    StreamReadWrite.writeNullablePoint(pos, data);
    StreamReadWrite.writeNullablePoint(chest.buildingPos, data);
    data.writeBoolean(MillConfigValues.DEV);
    data.writeByte(chest.getSizeInventory());
    for (int i = 0; i < chest.getSizeInventory(); i++)
      StreamReadWrite.writeNullableItemStack(chest.getStackInSlot(i), data); 
    sendPacketToPlayer(data, player);
  }
  
  public static void sendPacketToPlayer(PacketBuffer packetBuffer, EntityPlayer player) {
    FMLProxyPacket proxyPacket = new FMLProxyPacket(packetBuffer, "millenaire");
    Mill.millChannel.sendTo(proxyPacket, (EntityPlayerMP)player);
  }
  
  public static void sendPacketToPlayersInRange(PacketBuffer packetBuffer, Point p, int range) {
    FMLProxyPacket proxyPacket = new FMLProxyPacket(packetBuffer, "millenaire");
    NetworkRegistry.TargetPoint tp = new NetworkRegistry.TargetPoint(0, p.x, p.y, p.z, range);
    Mill.millChannel.sendToAllAround(proxyPacket, tp);
  }
  
  public static void sendTranslatedSentence(EntityPlayer player, char colour, String code, String... values) {
    if (player == null)
      return; 
    if (!(player instanceof EntityPlayerMP))
      return; 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(100);
    data.writeChar(colour);
    data.writeString(code);
    data.writeInt(values.length);
    for (String value : values)
      StreamReadWrite.writeNullableString(value, data); 
    sendPacketToPlayer(data, player);
  }
  
  public static void sendTranslatedSentenceInRange(World world, Point p, int range, char colour, String key, String... values) {
    for (Object oplayer : world.playerEntities) {
      EntityPlayer player = (EntityPlayer)oplayer;
      if (p.distanceTo((Entity)player) < range)
        sendTranslatedSentence(player, colour, key, values); 
    } 
  }
  
  public static void sendVillagerSentence(EntityPlayerMP player, MillVillager v) {
    if (player == null)
      return; 
    if (!(player instanceof EntityPlayerMP))
      return; 
    PacketBuffer data = getPacketBuffer();
    data.writeInt(108);
    data.writeLong(v.getVillagerId());
    sendPacketToPlayer(data, (EntityPlayer)player);
  }
  
  public static void sendVillageSentenceInRange(World world, Point p, int range, MillVillager v) {
    for (Object oplayer : world.playerEntities) {
      EntityPlayerMP player = (EntityPlayerMP)oplayer;
      if (p.distanceTo((Entity)player) < range)
        sendVillagerSentence(player, v); 
    } 
  }
}
