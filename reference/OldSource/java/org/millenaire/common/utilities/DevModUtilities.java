package org.millenaire.common.utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.utilities.ThreadSafeUtilities;

public class DevModUtilities {
  private static class DevPathedEntity implements IAStarPathedEntity {
    World world;
    
    EntityPlayer caller;
    
    DevPathedEntity(World w, EntityPlayer p) {
      this.world = w;
      this.caller = p;
    }
    
    public void onFoundPath(List<AStarNode> result) {
      int meta = MillCommonUtilities.randomInt(16);
      for (AStarNode node : result) {
        if (node != result.get(0) && node != result.get(result.size() - 1))
          WorldUtilities.setBlockAndMetadata(this.world, (new Point(node)).getBelow(), Blocks.WOOL, meta); 
      } 
    }
    
    public void onNoPathAvailable() {
      ServerSender.sendChat(this.caller, TextFormatting.DARK_RED, "No path available.");
    }
  }
  
  private static HashMap<EntityPlayer, Integer> autoMoveDirection = new HashMap<>();
  
  private static HashMap<EntityPlayer, Integer> autoMoveTarget = new HashMap<>();
  
  public static void fillInFreeGoods(EntityPlayer player) {
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_BLUE_LEGGINGS, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_BLUE_BOOTS, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_BLUE_HELMET, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_BLUE_CHESTPLATE, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_RED_LEGGINGS, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_RED_BOOTS, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_RED_HELMET, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_RED_CHESTPLATE, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_GUARD_LEGGINGS, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_GUARD_BOOTS, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_GUARD_HELMET, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.JAPANESE_GUARD_CHESTPLATE, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.SUMMONING_WAND, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.AMULET_SKOLL_HATI, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Items.CLOCK, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.NORMAN_AXE, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.NORMAN_PICKAXE, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.NORMAN_SHOVEL, 1);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Blocks.GOLD_BLOCK, 0, 64);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Blocks.LOG, 64);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Items.COAL, 64);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Blocks.COBBLESTONE, 128);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Blocks.STONE, 512);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Block)Blocks.SAND, 128);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, Blocks.WOOL, 64);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.CALVA, 0, 2);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.CHICKEN_CURRY, 2);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.RICE, 0, 64);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.MAIZE, 0, 64);
    MillCommonUtilities.putItemsInChest((IInventory)player.inventory, (Item)MillItems.TURMERIC, 0, 64);
  }
  
  public static void runAutoMove(World world) {
    for (Object o : world.playerEntities) {
      if (o instanceof EntityPlayer) {
        EntityPlayer p = (EntityPlayer)o;
        if (autoMoveDirection.containsKey(p)) {
          if (((Integer)autoMoveDirection.get(p)).intValue() == 1) {
            if (((Integer)autoMoveTarget.get(p)).intValue() < p.posX) {
              autoMoveDirection.put(p, Integer.valueOf(-1));
              autoMoveTarget.put(p, Integer.valueOf((int)(p.posX - 100000.0D)));
              ServerSender.sendChat(p, TextFormatting.GREEN, "Auto-move: turning back.");
            } 
          } else if (((Integer)autoMoveDirection.get(p)).intValue() == -1 && (
            (Integer)autoMoveTarget.get(p)).intValue() > p.posX) {
            autoMoveDirection.put(p, Integer.valueOf(1));
            autoMoveTarget.put(p, Integer.valueOf((int)(p.posX + 100000.0D)));
            ServerSender.sendChat(p, TextFormatting.GREEN, "Auto-move: turning back again.");
          } 
          p.setPositionAndUpdate(p.posX + ((Integer)autoMoveDirection.get(p)).intValue() * 0.5D, p.posY, p.posZ);
          p.setPositionAndRotation(p.posX + ((Integer)autoMoveDirection.get(p)).intValue() * 0.5D, p.posY, p.posZ, p.rotationYaw, p.rotationPitch);
        } 
      } 
    } 
  }
  
  public static void testGetItemFromBlock() {
    long starttime = System.nanoTime();
    Iterator<Block> iterator = Block.REGISTRY.iterator();
    int count = 0;
    while (iterator.hasNext()) {
      Block block = iterator.next();
      Item.getItemFromBlock(block);
      count++;
    } 
    MillLog.temp(null, "Took " + (1.0D * (System.nanoTime() - starttime) / 1000000.0D) + " ms to load " + count + " items from blocks.");
  }
  
  public static void testPaths(EntityPlayer player) {
    Point centre = new Point((Entity)player);
    MillLog.temp(null, "Attempting test path around: " + player);
    Point start = null;
    Point end = null;
    int toleranceMode = 0;
    for (int i = 0; i < 100 && (start == null || end == null); i++) {
      for (int j = 0; j < 100 && (start == null || end == null); j++) {
        for (int k = 0; k < 100 && (start == null || end == null); k++) {
          for (int l = 0; l < 8 && (start == null || end == null); l++) {
            Point p = centre.getRelative((i * (1 - (l & 0x1) * 2)), (j * (1 - (l & 0x2))), (k * (1 - (l & 0x4) / 2)));
            Block block = WorldUtilities.getBlock(player.world, p);
            if (start == null && block == Blocks.GOLD_BLOCK)
              start = p; 
            if (end == null && block == Blocks.IRON_BLOCK) {
              end = p.getAbove();
              toleranceMode = 0;
            } else if (end == null && block == Blocks.DIAMOND_BLOCK) {
              end = p.getAbove();
              toleranceMode = 1;
            } else if (end == null && block == Blocks.LAPIS_BLOCK) {
              end = p.getAbove();
              toleranceMode = 2;
            } 
          } 
        } 
      } 
    } 
    if (start != null && end != null) {
      AStarConfig jpsConfig;
      DevPathedEntity pathedEntity = new DevPathedEntity(player.world, player);
      if (toleranceMode == 1) {
        jpsConfig = new AStarConfig(true, false, false, true, true, 2, 2);
      } else if (toleranceMode == 2) {
        jpsConfig = new AStarConfig(true, false, false, true, true, 2, 20);
      } else {
        jpsConfig = new AStarConfig(true, false, false, true, true);
      } 
      ServerSender.sendChat(player, TextFormatting.DARK_GREEN, "Calculating path. Tolerance H: " + jpsConfig.toleranceHorizontal);
      AStarPathPlannerJPS jpsPathPlanner = new AStarPathPlannerJPS(player.world, pathedEntity, true);
      try {
        jpsPathPlanner.getPath(start.getiX(), start.getiY(), start.getiZ(), end.getiX(), end.getiY(), end.getiZ(), jpsConfig);
      } catch (ThreadSafeUtilities.ChunkAccessException e) {
        MillLog.printException(e);
      } 
    } else {
      ServerSender.sendChat(player, TextFormatting.DARK_RED, "Could not find start or end: " + start + " - " + end);
    } 
  }
  
  public static void toggleAutoMove(EntityPlayer player) {
    if (autoMoveDirection.containsKey(player)) {
      autoMoveDirection.remove(player);
      autoMoveTarget.remove(player);
      ServerSender.sendChat(player, TextFormatting.GREEN, "Auto-move disabled");
    } else {
      autoMoveDirection.put(player, Integer.valueOf(1));
      autoMoveTarget.put(player, Integer.valueOf((int)(player.posX + 100000.0D)));
      ServerSender.sendChat(player, TextFormatting.GREEN, "Auto-move enabled");
    } 
  }
  
  public static void validateResourceMap(Map<InvItem, Integer> map) {
    int errors = 0;
    for (InvItem item : map.keySet()) {
      if (item == null) {
        MillLog.printException(new MillLog.MillenaireException("Found a null InvItem in map!"));
        errors++;
        continue;
      } 
      if (!map.containsKey(item)) {
        MillLog.printException(new MillLog.MillenaireException("Key: " + item + " not present in map???"));
        errors++;
        continue;
      } 
      if (map.get(item) == null) {
        MillLog.printException(new MillLog.MillenaireException("Key: " + item + " has null value in map."));
        errors++;
      } 
    } 
    if (map.size() > 0)
      MillLog.error(null, "Validated map. Found " + errors + " amoung " + map.size() + " keys."); 
  }
  
  public static void villagerInteractDev(EntityPlayer entityplayer, MillVillager villager) {
    if (villager.isChild()) {
      villager.growSize();
      ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": Size: " + villager.getSize() + " gender: " + villager.gender);
      if (entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() == MillItems.SUMMONING_WAND) {
        (villager.getRecord()).size = 20;
        villager.growSize();
      } 
    } 
    if (entityplayer.inventory.getCurrentItem() == ItemStack.EMPTY || entityplayer.inventory.getCurrentItem().getItem() == Items.AIR) {
      ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": Current goal: " + villager.getGoalLabel(villager.goalKey) + " Current pos: " + villager.getPos());
      ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": House: " + villager.housePoint + " Town Hall: " + villager.townHallPoint);
      ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": ID: " + villager.getVillagerId());
      if (villager.getRecord() != null)
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": Spouse: " + (villager.getRecord()).spousesName); 
      if (villager.getPathDestPoint() != null && villager.pathEntity != null && villager.pathEntity.getCurrentPathLength() > 1) {
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": Dest: " + villager.getPathDestPoint() + " distance: " + villager
            .getPathDestPoint().distanceTo((Entity)villager) + " stuck: " + villager.longDistanceStuck + " jump:" + villager.pathEntity.getNextTargetPathPoint());
      } else {
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, villager.getName() + ": No dest point.");
      } 
      String s = "";
      if (villager.getRecord() != null)
        for (String tag : (villager.getRecord()).questTags)
          s = s + tag + " ";  
      if ((villager.mw.getProfile(entityplayer)).villagersInQuests.containsKey(Long.valueOf(villager.getVillagerId())))
        s = s + " quest: " + ((QuestInstance)(villager.mw.getProfile(entityplayer)).villagersInQuests.get(Long.valueOf(villager.getVillagerId()))).quest.key + "/" + (((QuestInstance)(villager.mw.getProfile(entityplayer)).villagersInQuests.get(Long.valueOf(villager.getVillagerId()))).getCurrentVillager()).id; 
      if (s != null && s.length() > 0)
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "Tags: " + s); 
      s = "";
      for (InvItem key : villager.inventory.keySet()) {
        if (((Integer)villager.inventory.get(key)).intValue() > 0)
          s = s + key + ":" + villager.inventory.get(key) + " "; 
      } 
      if (villager.getAttackTarget() != null)
        s = s + "attacking: " + villager.getAttackTarget() + " "; 
      if (s != null && s.length() > 0)
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "Inv: " + s); 
    } else if (entityplayer.inventory.getCurrentItem() != ItemStack.EMPTY && entityplayer.inventory.getCurrentItem().getItem() == Item.getItemFromBlock((Block)Blocks.SAND)) {
      if (villager.hiredBy == null) {
        villager.hiredBy = entityplayer.getName();
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "Hired: " + entityplayer.getName());
      } else {
        villager.hiredBy = null;
        ServerSender.sendChat(entityplayer, TextFormatting.GREEN, "No longer hired");
      } 
    } else if (entityplayer.inventory.getCurrentItem() != ItemStack.EMPTY && entityplayer.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(Blocks.DIRT) && villager.pathEntity != null) {
      int meta = MillCommonUtilities.randomInt(16);
      for (PathPoint pathPoint : villager.pathEntity.pointsCopy) {
        if (WorldUtilities.getBlock(villager.world, pathPoint.x, pathPoint.y - 1, pathPoint.z) != MillBlocks.LOCKED_CHEST)
          WorldUtilities.setBlockAndMetadata(villager.world, (new Point(pathPoint)).getBelow(), Blocks.WOOL, meta); 
      } 
      PathPoint p = villager.pathEntity.getCurrentTargetPathPoint();
      if (p != null && WorldUtilities.getBlock(villager.world, p.x, p.y - 1, p.z) != MillBlocks.LOCKED_CHEST)
        WorldUtilities.setBlockAndMetadata(villager.world, (new Point(p)).getBelow(), Blocks.GOLD_BLOCK, 0); 
      p = villager.pathEntity.getNextTargetPathPoint();
      if (p != null && WorldUtilities.getBlock(villager.world, p.x, p.y - 1, p.z) != MillBlocks.LOCKED_CHEST)
        WorldUtilities.setBlockAndMetadata(villager.world, (new Point(p)).getBelow(), Blocks.DIAMOND_BLOCK, 0); 
      p = villager.pathEntity.getPreviousTargetPathPoint();
      if (p != null && WorldUtilities.getBlock(villager.world, p.x, p.y - 1, p.z) != MillBlocks.LOCKED_CHEST)
        WorldUtilities.setBlockAndMetadata(villager.world, (new Point(p)).getBelow(), Blocks.IRON_BLOCK, 0); 
    } 
    if (villager.hasChildren() && entityplayer.inventory.getCurrentItem() != ItemStack.EMPTY && entityplayer.inventory.getCurrentItem().getItem() == MillItems.SUMMONING_WAND) {
      MillVillager child = villager.getHouse().createChild(villager, villager.getTownHall(), (villager.getRecord()).spousesName);
      if (child != null) {
        (child.getRecord()).size = 20;
        child.growSize();
      } 
    } 
  }
}
