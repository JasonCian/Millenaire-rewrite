package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

@Documentation("Go to sleep at home at night. This goal is auto-added to all villagers.")
public class GoalSleep extends Goal {
  public int actionDuration(MillVillager villager) throws Exception {
    return 10;
  }
  
  public boolean allowRandomMoves() throws Exception {
    return false;
  }
  
  public boolean canBeDoneAtNight() {
    return true;
  }
  
  public boolean canBeDoneInDayTime() {
    return false;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    World world = villager.world;
    Point sleepPos = villager.getHouse().getResManager().getSleepingPos();
    List<Point> beds = new ArrayList<>();
    for (int xDelta = 0; xDelta < 6; xDelta++) {
      for (int yDelta = 0; yDelta < 6; yDelta++) {
        for (int zDelta = 0; zDelta < 6; zDelta++) {
          for (int l = 0; l < 8; l++) {
            Point p = sleepPos.getRelative((xDelta * (1 - (l & 0x1) * 2)), (yDelta * (1 - (l & 0x2))), (zDelta * (1 - (l & 0x4) / 2)));
            Block block = WorldUtilities.getBlock(world, p);
            if (block instanceof net.minecraft.block.BlockBed) {
              int meta = WorldUtilities.getBlockMeta(world, p);
              if ((meta & 0x8) == 0) {
                boolean alreadyTaken = false;
                for (MillVillager v : villager.getHouse().getKnownVillagers()) {
                  if (v != villager && v.getGoalDestPoint() != null && 
                    v.getGoalDestPoint().equals(p))
                    alreadyTaken = true; 
                } 
                if (!alreadyTaken)
                  beds.add(p); 
              } 
            } 
          } 
        } 
      } 
    } 
    if (beds.size() > 0)
      return packDest(beds.get(0), villager.getHouse()); 
    List<Point> feetPos = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      for (int yDelta = 0; yDelta < 6; yDelta++) {
        for (int zDelta = 0; zDelta < 6; zDelta++) {
          for (int l = 0; l < 8; l++) {
            Point p = sleepPos.getRelative((i * (1 - (l & 0x1) * 2)), (yDelta * (1 - (l & 0x2))), (zDelta * (1 - (l & 0x4) / 2)));
            if (!p.isBlockPassable(world) && p.getAbove().isBlockPassable(world) && p.getRelative(0.0D, 2.0D, 0.0D).isBlockPassable(world)) {
              Point topBlock = WorldUtilities.findTopNonPassableBlock(world, p.getiX(), p.getiZ());
              if (topBlock != null && topBlock.y > p.y + 1.0D) {
                float angle = villager.getBedOrientationInDegrees();
                int dx = 0, dz = 0;
                if (angle == 0.0F) {
                  dx = 1;
                } else if (angle == 90.0F) {
                  dz = 1;
                } else if (angle == 180.0F) {
                  dx = -1;
                } else if (angle == 270.0F) {
                  dz = -1;
                } 
                Point p2 = p.getRelative(dx, 0.0D, dz);
                if (!p2.isBlockPassable(world) && p2.getAbove().isBlockPassable(world) && p2.getRelative(0.0D, 2.0D, 0.0D).isBlockPassable(world)) {
                  topBlock = WorldUtilities.findTopNonPassableBlock(world, p2.getiX(), p2.getiZ());
                  if (topBlock != null && topBlock.y > p2.y + 1.0D) {
                    p = p.getAbove();
                    boolean alreadyTaken = false;
                    for (MillVillager v : villager.getHouse().getKnownVillagers()) {
                      if (v != villager && v.getGoalDestPoint() != null) {
                        if (v.getGoalDestPoint().equals(p))
                          alreadyTaken = true; 
                        if (v.getGoalDestPoint().equals(p.getRelative(1.0D, 0.0D, 0.0D)))
                          alreadyTaken = true; 
                        if (v.getGoalDestPoint().equals(p.getRelative(0.0D, 0.0D, 1.0D)))
                          alreadyTaken = true; 
                        if (v.getGoalDestPoint().equals(p.getRelative(-1.0D, 0.0D, 0.0D)))
                          alreadyTaken = true; 
                        if (v.getGoalDestPoint().equals(p.getRelative(0.0D, 0.0D, -1.0D)))
                          alreadyTaken = true; 
                      } 
                    } 
                    if (!alreadyTaken)
                      feetPos.add(p); 
                  } 
                } 
              } 
            } 
          } 
        } 
      } 
    } 
    for (MillVillager v : villager.getHouse().getKnownVillagers()) {
      if (v != villager && v.getGoalDestPoint() != null) {
        feetPos.remove(v.getGoalDestPoint());
        feetPos.remove(v.getGoalDestPoint().getRelative(1.0D, 0.0D, 0.0D));
        feetPos.remove(v.getGoalDestPoint().getRelative(0.0D, 0.0D, 1.0D));
        feetPos.remove(v.getGoalDestPoint().getRelative(-1.0D, 0.0D, 0.0D));
        feetPos.remove(v.getGoalDestPoint().getRelative(0.0D, 0.0D, -1.0D));
      } 
    } 
    if (feetPos.size() > 0)
      return packDest(feetPos.get(0), villager.getHouse()); 
    return packDest(sleepPos, villager.getHouse());
  }
  
  public String labelKeyWhileTravelling(MillVillager villager) {
    return this.key + "_travelling";
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    float floatingHeight;
    if (!villager.nightActionPerformed)
      villager.nightActionPerformed = villager.performNightAction(); 
    villager.shouldLieDown = true;
    float angle = villager.getBedOrientationInDegrees();
    double dx = 0.5D, dz = 0.5D, fdx = 0.0D, fdz = 0.0D;
    if (angle == 0.0F) {
      dx = 0.95D;
      fdx = -10.0D;
    } else if (angle == 90.0F) {
      dz = 0.95D;
      fdz = -10.0D;
    } else if (angle == 180.0F) {
      dx = 0.05D;
      fdx = 10.0D;
    } else if (angle == 270.0F) {
      dz = 0.05D;
      fdz = 10.0D;
    } 
    if (villager.getBlock(villager.getGoalDestPoint()) instanceof net.minecraft.block.BlockBed) {
      floatingHeight = 0.7F;
    } else {
      floatingHeight = 0.2F;
    } 
    villager.setPosition((villager.getGoalDestPoint()).x + dx, (villager.getGoalDestPoint()).y + floatingHeight, (villager.getGoalDestPoint()).z + dz);
    villager.facePoint(villager.getPos().getRelative(fdx, 1.0D, fdz), 100.0F, 100.0F);
    return false;
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 50;
  }
  
  public int range(MillVillager villager) {
    return 2;
  }
  
  public boolean shouldVillagerLieDown() {
    return true;
  }
}
