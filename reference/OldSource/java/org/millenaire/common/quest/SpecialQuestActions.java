package org.millenaire.common.quest;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.EntityTargetedBlaze;
import org.millenaire.common.entity.EntityTargetedGhast;
import org.millenaire.common.entity.EntityTargetedWitherSkeleton;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.WorldGenVillage;

public class SpecialQuestActions {
  private static final int MARVEL_MIN_DISTANCE = 200;
  
  public static final String COMPLETE = "_complete";
  
  public static final String EXPLORE_TAG = "action_explore_";
  
  public static final String ENCHANTMENTTABLE = "action_build_enchantment_table";
  
  public static final String UNDERWATER_GLASS = "action_underwater_glass";
  
  public static final String UNDERWATER_DIVE = "action_underwater_dive";
  
  public static final String TOPOFTHEWORLD = "action_topoftheworld";
  
  public static final String BOTTOMOFTHEWORLD = "action_bottomoftheworld";
  
  public static final String BOREHOLE = "action_borehole";
  
  public static final String BOREHOLETNT = "action_boreholetnt";
  
  public static final String BOREHOLETNTLIT = "action_boreholetntlit";
  
  public static final String THEVOID = "action_thevoid";
  
  public static final String MAYANSIEGE = "action_mayansiege";
  
  public static final String NORMANMARVEL_PICKLOCATION = "normanmarvel_picklocation";
  
  public static final String NORMANMARVEL_GENERATE = "normanmarvel_generate";
  
  public static final String NORMANMARVEL_LOCATION = "normanmarvel_location";
  
  private static final String NORMANMARVEL_VILLAGEPOS = "normanmarvel_villagepos";
  
  private static Field FIELD_BIOME_NAME = ReflectionHelper.findField(Biome.class, "biomeName", "biomeName");
  
  private static void indianCQHandleBottomOfTheWorld(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_bottomoftheworld") || mw.getProfile(player).isTagSet("action_bottomoftheworld_complete"))
      return; 
    if (player.posY < 4.0D) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.bottomoftheworld_success", new String[0]);
      mw.getProfile(player).clearTag("action_bottomoftheworld");
      mw.getProfile(player).setTag("action_bottomoftheworld_complete");
      return;
    } 
  }
  
  private static void indianCQHandleContinuousExplore(MillWorldData mw, EntityPlayer player, long worldTime, String biome, ResourceLocation mob, int nbMob, int minTravel) throws IllegalArgumentException, IllegalAccessException {
    if (!mw.getProfile(player).isTagSet("action_explore_" + biome) || mw.getProfile(player).isTagSet("action_explore_" + biome + "_complete"))
      return; 
    if (mw.world.isDaytime())
      return; 
    String biomeName = ((String)FIELD_BIOME_NAME.get(mw.world.getBiome(player.getPosition()))).toLowerCase();
    if (biomeName.equals("extreme hills"))
      biomeName = "mountain"; 
    if (!biomeName.equals(biome))
      return; 
    int surface = WorldUtilities.findTopSoilBlock(mw.world, (int)player.posX, (int)player.posZ);
    if (player.posY <= (surface - 2))
      return; 
    String testnbstr = mw.getProfile(player).getActionData(biome + "_explore_nbcomplete");
    int nbtest = 0;
    if (testnbstr != null) {
      nbtest = Integer.parseInt(testnbstr);
      for (int i = 1; i <= nbtest; i++) {
        String pointstr = mw.getProfile(player).getActionData(biome + "_explore_point" + i);
        if (pointstr != null) {
          Point p = new Point(pointstr);
          if (p.horizontalDistanceTo((Entity)player) < minTravel)
            return; 
        } 
      } 
    } 
    nbtest++;
    if (nbtest >= 20) {
      ServerSender.sendTranslatedSentence(player, '7', "actions." + biome + "_success", new String[0]);
      mw.getProfile(player).clearActionData(biome + "_explore_nbcomplete");
      for (int i = 1; i <= 10; i++)
        mw.getProfile(player).clearActionData(biome + "_explore_point" + i); 
      mw.getProfile(player).clearTag("action_explore_" + biome);
      mw.getProfile(player).setTag("action_explore_" + biome + "_complete");
      return;
    } 
    mw.getProfile(player).setActionData(biome + "_explore_point" + nbtest, (new Point((Entity)player)).getIntString());
    mw.getProfile(player).setActionData(biome + "_explore_nbcomplete", "" + nbtest);
    ServerSender.sendTranslatedSentence(player, '7', "actions." + biome + "_continue", new String[] { "" + (nbtest * 5) });
    WorldUtilities.spawnMobsAround(mw.world, new Point((Entity)player), 20, mob, 2, 4);
  }
  
  private static void indianCQHandleEnchantmentTable(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_build_enchantment_table") || mw.getProfile(player).isTagSet("action_build_enchantment_table_complete"))
      return; 
    boolean closeEnough = false;
    for (int i = 0; i < mw.loneBuildingsList.types.size(); i++) {
      if (((String)mw.loneBuildingsList.types.get(i)).equals("sadhutree") && (
        (Point)mw.loneBuildingsList.pos.get(i)).distanceToSquared((Entity)player) < 100.0D)
        closeEnough = true; 
    } 
    if (!closeEnough)
      return; 
    for (int x = (int)player.posX - 5; x < (int)player.posX + 5; x++) {
      for (int z = (int)player.posZ - 5; z < (int)player.posZ + 5; z++) {
        for (int y = (int)player.posY - 3; y < (int)player.posY + 3; y++) {
          Block block = WorldUtilities.getBlock(mw.world, x, y, z);
          if (block == Blocks.ENCHANTING_TABLE) {
            int nbBookShelves = 0;
            for (int dx = -1; dx <= 1; dx++) {
              for (int dz = -1; dz <= 1; dz++) {
                if ((dx != 0 || dz != 0) && mw.world.isAirBlock(new BlockPos(x + dx, y, z + dz)) && mw.world.isAirBlock(new BlockPos(x + dx, y + 1, z + dz))) {
                  if (WorldUtilities.getBlock(mw.world, x + dx * 2, y, z + dz * 2) == Blocks.BOOKSHELF)
                    nbBookShelves++; 
                  if (WorldUtilities.getBlock(mw.world, x + dx * 2, y + 1, z + dz * 2) == Blocks.BOOKSHELF)
                    nbBookShelves++; 
                  if (dz != 0 && dx != 0) {
                    if (WorldUtilities.getBlock(mw.world, x + dx * 2, y, z + dz) == Blocks.BOOKSHELF)
                      nbBookShelves++; 
                    if (WorldUtilities.getBlock(mw.world, x + dx * 2, y + 1, z + dz) == Blocks.BOOKSHELF)
                      nbBookShelves++; 
                    if (WorldUtilities.getBlock(mw.world, x + dx, y, z + dz * 2) == Blocks.BOOKSHELF)
                      nbBookShelves++; 
                    if (WorldUtilities.getBlock(mw.world, x + dx, y + 1, z + dz * 2) == Blocks.BOOKSHELF)
                      nbBookShelves++; 
                  } 
                } 
              } 
            } 
            if (nbBookShelves > 0) {
              ServerSender.sendTranslatedSentence(player, '7', "actions.enchantmenttable_success", new String[0]);
              mw.getProfile(player).clearTag("action_build_enchantment_table");
              mw.getProfile(player).setTag("action_build_enchantment_table_complete");
              return;
            } 
          } 
        } 
      } 
    } 
  }
  
  private static void indianCQHandleTopOfTheWorld(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_topoftheworld") || mw.getProfile(player).isTagSet("action_topoftheworld_complete"))
      return; 
    if (player.posY > 250.0D) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.topoftheworld_success", new String[0]);
      mw.getProfile(player).clearTag("action_topoftheworld");
      mw.getProfile(player).setTag("action_topoftheworld_complete");
      return;
    } 
  }
  
  private static void indianCQHandleUnderwaterDive(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_underwater_dive") || mw.getProfile(player).isTagSet("action_underwater_dive_complete"))
      return; 
    Point p = new Point((Entity)player);
    int nbWater = 0;
    while (WorldUtilities.getBlock(mw.world, p) == Blocks.WATER) {
      nbWater++;
      p = p.getAbove();
    } 
    if (nbWater > 12) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.underwaterdive_success", new String[0]);
      mw.getProfile(player).clearTag("action_underwater_dive");
      mw.getProfile(player).setTag("action_underwater_dive_complete");
      return;
    } 
  }
  
  private static void indianCQHandleUnderwaterGlass(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_underwater_glass") || mw.getProfile(player).isTagSet("action_underwater_glass_complete"))
      return; 
    Point p = new Point((Entity)player);
    Block block = WorldUtilities.getBlock(mw.world, p);
    while (block != null && !BlockItemUtilities.isBlockOpaqueCube(block) && block != Blocks.GLASS && block != Blocks.GLASS_PANE) {
      p = p.getAbove();
      block = WorldUtilities.getBlock(mw.world, p);
    } 
    block = WorldUtilities.getBlock(mw.world, p);
    if (block != Blocks.GLASS && block != Blocks.GLASS_PANE)
      return; 
    p = p.getAbove();
    int nbWater = 0;
    while (WorldUtilities.getBlock(mw.world, p) == Blocks.WATER) {
      nbWater++;
      p = p.getAbove();
    } 
    if (nbWater > 15) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.underwaterglass_success", new String[0]);
      mw.getProfile(player).clearTag("action_underwater_glass");
      mw.getProfile(player).setTag("action_underwater_glass_complete");
      return;
    } 
    if (nbWater > 1)
      ServerSender.sendTranslatedSentence(player, '7', "actions.underwaterglass_notdeepenough", new String[0]); 
  }
  
  private static void mayanCQHandleMayanSiege(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_mayansiege") || mw.getProfile(player).isTagSet("action_mayansiege_complete"))
      return; 
    String siegeStatus = mw.getProfile(player).getActionData("mayan_siege_status");
    if (siegeStatus == null) {
      for (Point p : mw.loneBuildingsList.pos) {
        Building b = mw.getBuilding(p);
        if (b != null && 
          b.villageType.key.equals("questpyramid") && p.distanceTo((Entity)player) < 50.0D) {
          int nbGhasts = 0, nbBlazes = 0, nbSkel = 0;
          int i;
          for (i = 0; i < 12; i++) {
            Point spawn = b.location.pos.getRelative((-10 + MillCommonUtilities.randomInt(20)), 20.0D, (-10 + MillCommonUtilities.randomInt(20)));
            EntityTargetedGhast ent = (EntityTargetedGhast)WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_GHAST);
            if (ent != null) {
              ent.target = b.location.pos.getRelative(0.0D, 20.0D, 0.0D);
              nbGhasts++;
            } 
          } 
          for (i = 0; i < 12; i++) {
            Point spawn = b.location.pos.getRelative((-5 + MillCommonUtilities.randomInt(10)), 15.0D, (-5 + MillCommonUtilities.randomInt(10)));
            EntityTargetedBlaze ent = (EntityTargetedBlaze)WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_BLAZE);
            if (ent != null) {
              ent.target = b.location.pos.getRelative(0.0D, 10.0D, 0.0D);
              nbBlazes++;
            } 
          } 
          for (i = 0; i < 5; i++) {
            Point spawn = b.location.pos.getRelative(5.0D, 12.0D, (-5 + MillCommonUtilities.randomInt(10)));
            Entity ent = WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_WITHERSKELETON);
            if (ent != null)
              nbSkel++; 
            spawn = b.location.pos.getRelative(-5.0D, 12.0D, (-5 + MillCommonUtilities.randomInt(10)));
            ent = WorldUtilities.spawnMobsSpawner(mw.world, spawn, Mill.ENTITY_TARGETED_WITHERSKELETON);
            if (ent != null)
              nbSkel++; 
          } 
          mw.getProfile(player).setActionData("mayan_siege_status", "started");
          mw.getProfile(player).setActionData("mayan_siege_ghasts", "" + nbGhasts);
          mw.getProfile(player).setActionData("mayan_siege_blazes", "" + nbBlazes);
          mw.getProfile(player).setActionData("mayan_siege_skeletons", "" + nbSkel);
          ServerSender.sendTranslatedSentence(player, '7', "actions.mayan_siege_start", new String[] { "" + nbGhasts, "" + nbBlazes, "" + nbSkel });
        } 
      } 
    } else if (siegeStatus.equals("started")) {
      for (Point p : mw.loneBuildingsList.pos) {
        Building b = mw.getBuilding(p);
        if (b != null && 
          b.villageType.key.equals("questpyramid") && p.distanceTo((Entity)player) < 50.0D) {
          List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(mw.world, EntityTargetedGhast.class, b.location.pos, 128, 128);
          int nbGhasts = mobs.size();
          mobs = WorldUtilities.getEntitiesWithinAABB(mw.world, EntityTargetedBlaze.class, b.location.pos, 128, 128);
          int nbBlazes = mobs.size();
          mobs = WorldUtilities.getEntitiesWithinAABB(mw.world, EntityTargetedWitherSkeleton.class, b.location.pos, 128, 128);
          int nbSkel = mobs.size();
          if (nbGhasts == 0 && nbBlazes == 0 && nbSkel == 0) {
            mw.getProfile(player).setActionData("mayan_siege_status", "finished");
            mw.getProfile(player).setTag("action_mayansiege_complete");
            ServerSender.sendTranslatedSentence(player, '7', "actions.mayan_siege_success", new String[0]);
            continue;
          } 
          int oldGhasts = Integer.parseInt(mw.getProfile(player).getActionData("mayan_siege_ghasts"));
          int oldBlazes = Integer.parseInt(mw.getProfile(player).getActionData("mayan_siege_blazes"));
          int oldSkel = Integer.parseInt(mw.getProfile(player).getActionData("mayan_siege_skeletons"));
          if (oldGhasts != nbGhasts || oldBlazes != nbBlazes || oldSkel != nbSkel) {
            ServerSender.sendTranslatedSentence(player, '7', "actions.mayan_siege_update", new String[] { "" + nbGhasts, "" + nbBlazes, "" + nbSkel });
            mw.getProfile(player).setActionData("mayan_siege_ghasts", "" + nbGhasts);
            mw.getProfile(player).setActionData("mayan_siege_blazes", "" + nbBlazes);
            mw.getProfile(player).setActionData("mayan_siege_skeletons", "" + nbSkel);
          } 
        } 
      } 
    } 
  }
  
  private static void normanCQHandleBorehole(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_borehole") || mw.getProfile(player).isTagSet("action_borehole_complete"))
      return; 
    if (player.posY > 10.0D)
      return; 
    int nbok = 0;
    for (int x = (int)(player.posX - 2.0D); x < (int)player.posX + 3; x++) {
      for (int z = (int)(player.posZ - 2.0D); z < (int)player.posZ + 3; z++) {
        boolean ok = true, stop = false;
        for (int y = 127; y > 0 && !stop; y--) {
          Block block = WorldUtilities.getBlock(mw.world, x, y, z);
          if (block == Blocks.BEDROCK) {
            stop = true;
          } else if (block != Blocks.AIR) {
            stop = true;
            ok = false;
          } 
        } 
        if (ok)
          nbok++; 
      } 
    } 
    if (nbok >= 25) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.borehole_success", new String[0]);
      mw.getProfile(player).clearTag("action_borehole");
      mw.getProfile(player).setTag("action_borehole_complete");
      mw.getProfile(player).setActionData("action_borehole_pos", (new Point((Entity)player)).getIntString());
      return;
    } 
    String maxKnownStr = mw.getProfile(player).getActionData("action_borehole_max");
    int maxKnown = 0;
    if (maxKnownStr != null)
      maxKnown = Integer.parseInt(maxKnownStr); 
    if (nbok > maxKnown) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.borehole_nblineok", new String[] { "" + nbok });
      mw.getProfile(player).setActionData("action_borehole_max", "" + nbok);
    } 
  }
  
  private static void normanCQHandleBoreholeTNT(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_boreholetnt") || mw.getProfile(player).isTagSet("action_boreholetnt_complete"))
      return; 
    String pStr = mw.getProfile(player).getActionData("action_borehole_pos");
    if (pStr == null)
      return; 
    Point p = new Point(pStr);
    if (p.distanceToSquared((Entity)player) > 25.0D)
      return; 
    int nbTNT = 0;
    for (int x = p.getiX() - 2; x < p.getiX() + 3; x++) {
      for (int z = p.getiZ() - 2; z < p.getiZ() + 3; z++) {
        boolean obsidian = false;
        for (int y = 6; y > 0; y--) {
          Block block = WorldUtilities.getBlock(mw.world, x, y, z);
          if (block == Blocks.OBSIDIAN) {
            obsidian = true;
          } else if (obsidian && block == Blocks.TNT) {
            nbTNT++;
          } 
        } 
      } 
    } 
    if (nbTNT >= 20) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.boreholetnt_success", new String[0]);
      mw.getProfile(player).clearTag("action_boreholetnt");
      mw.getProfile(player).setTag("action_boreholetnt_complete");
      mw.getProfile(player).setTag("action_boreholetntlit");
      mw.getProfile(player).clearActionData("action_boreholetnt_max");
      return;
    } 
    if (nbTNT == 0)
      return; 
    String maxKnownStr = mw.getProfile(player).getActionData("action_boreholetnt_max");
    int maxKnown = 0;
    if (maxKnownStr != null)
      maxKnown = Integer.parseInt(maxKnownStr); 
    if (nbTNT > maxKnown) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.boreholetnt_nbtnt", new String[] { "" + nbTNT });
      mw.getProfile(player).setActionData("action_boreholetnt_max", "" + nbTNT);
    } 
  }
  
  private static void normanCQHandleBoreholeTNTLit(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_boreholetntlit") || mw.getProfile(player).isTagSet("action_boreholetntlit_complete"))
      return; 
    Point p = new Point(mw.getProfile(player).getActionData("action_borehole_pos"));
    int nbtnt = mw.world.getEntitiesWithinAABB(EntityTNTPrimed.class, (new AxisAlignedBB(p.x, p.y, p.z, p.x + 1.0D, p.y + 1.0D, p.z + 1.0D)).expand(8.0D, 4.0D, 8.0D)).size();
    if (nbtnt > 0) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.boreholetntlit_success", new String[0]);
      mw.getProfile(player).clearTag("action_boreholetntlit");
      mw.getProfile(player).setTag("action_boreholetntlit_complete");
      return;
    } 
  }
  
  private static void normanCQHandleTheVoid(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("action_thevoid") || mw.getProfile(player).isTagSet("action_thevoid_complete"))
      return; 
    if (player.posY > 30.0D)
      return; 
    for (int i = -5; i < 5; i++) {
      for (int j = -5; j < 5; j++) {
        Block block = WorldUtilities.getBlock(mw.world, (int)player.posX + i, 0, (int)player.posZ + j);
        if (block == Blocks.AIR) {
          ServerSender.sendTranslatedSentence(player, '7', "actions.thevoid_success", new String[0]);
          mw.getProfile(player).clearTag("action_thevoid");
          mw.getProfile(player).setTag("action_thevoid_complete");
          return;
        } 
      } 
    } 
  }
  
  private static void normanMarvelGenerateMarvel(MillWorldData mw, EntityPlayer player) {
    if (!mw.getProfile(player).isTagSet("normanmarvel_generate"))
      return; 
    String pStr = mw.getProfile(player).getActionData("normanmarvel_location");
    if (pStr == null)
      return; 
    Point pos = new Point(pStr);
    VillageType marvelVillageType = Culture.getCultureByName("norman").getVillageType("notredame");
    WorldGenVillage genVillage = new WorldGenVillage();
    boolean result = genVillage.generateVillageAtPoint(player.world, MillCommonUtilities.random, pos.getiX(), pos.getiY(), pos.getiZ(), player, false, true, false, 200, marvelVillageType, null, null, 0.0F);
    if (result) {
      ServerSender.sendTranslatedSentence(player, '7', "actions.normanmarvel_generated", new String[0]);
      mw.getProfile(player).clearTag("normanmarvel_picklocation");
      mw.getProfile(player).clearTag("normanmarvel_picklocation_complete");
      mw.getProfile(player).clearTag("normanmarvel_generate");
      Point villagePos = mw.villagesList.pos.get(mw.villagesList.pos.size() - 1);
      mw.getProfile(player).setActionData("normanmarvel_villagepos", villagePos.getIntString());
    } else {
      ServerSender.sendTranslatedSentence(player, '7', "actions.normanmarvel_notgenerated", new String[0]);
      mw.getProfile(player).clearTag("normanmarvel_picklocation_complete");
      mw.getProfile(player).clearTag("normanmarvel_generate");
    } 
  }
  
  public static void normanMarvelPickLocation(MillWorldData mw, EntityPlayer player, Point pos) {
    double closestVillageDistance = Double.MAX_VALUE;
    for (Point thp : mw.villagesList.pos) {
      double distance = pos.distanceTo(thp);
      if (distance < 200.0D && distance < closestVillageDistance)
        closestVillageDistance = distance; 
    } 
    for (Point thp : mw.loneBuildingsList.pos) {
      double distance = pos.distanceTo(thp);
      if (distance < 200.0D && distance < closestVillageDistance)
        closestVillageDistance = distance; 
    } 
    if (closestVillageDistance == Double.MAX_VALUE) {
      mw.getProfile(player).setActionData("normanmarvel_location", (new Point((Entity)player)).getIntString());
      mw.getProfile(player).setTag("normanmarvel_picklocation_complete");
      ServerSender.sendTranslatedSentence(player, '7', "actions.normanmarvel_locationset", new String[0]);
    } else {
      ServerSender.sendTranslatedSentence(player, '6', "actions.normanmarvel_villagetooclose", new String[] { "200", "" + Math.round(closestVillageDistance) });
    } 
  }
  
  public static void onTick(MillWorldData mw, EntityPlayer player) {
    long startTime;
    if (mw.lastWorldUpdate > 0L) {
      startTime = Math.max(mw.lastWorldUpdate + 1L, mw.world.getWorldTime() - 10L);
    } else {
      startTime = mw.world.getWorldTime();
    } 
    long worldTime;
    for (worldTime = startTime; worldTime <= mw.world.getWorldTime(); worldTime++) {
      if (worldTime % 250L == 0L)
        try {
          indianCQHandleContinuousExplore(mw, player, worldTime, MillConfigValues.questBiomeForest, Mill.ENTITY_ZOMBIE, 2, 15);
          indianCQHandleContinuousExplore(mw, player, worldTime, MillConfigValues.questBiomeDesert, Mill.ENTITY_SKELETON, 2, 15);
          indianCQHandleContinuousExplore(mw, player, worldTime, MillConfigValues.questBiomeMountain, Mill.ENTITY_SPIDER, 2, 10);
        } catch (IllegalArgumentException|IllegalAccessException e) {
          MillLog.printException("Error while handling Indian Creation Quest exploration:", e);
        }  
      if (worldTime % 500L == 0L)
        indianCQHandleUnderwaterGlass(mw, player); 
      if (worldTime % 100L == 0L) {
        indianCQHandleUnderwaterDive(mw, player);
        indianCQHandleTopOfTheWorld(mw, player);
        indianCQHandleBottomOfTheWorld(mw, player);
        normanCQHandleBorehole(mw, player);
        normanCQHandleBoreholeTNT(mw, player);
        normanCQHandleTheVoid(mw, player);
        indianCQHandleEnchantmentTable(mw, player);
      } 
      if (worldTime % 10L == 0L) {
        normanCQHandleBoreholeTNTLit(mw, player);
        mayanCQHandleMayanSiege(mw, player);
        normanMarvelGenerateMarvel(mw, player);
      } 
    } 
  }
}
