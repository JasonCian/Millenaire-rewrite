package org.millenaire.common.village.buildingmanagers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.village.VillagerRecord;

public class PanelManager {
  public static final int MAX_LINE_NB = 8;
  
  public enum EnumSignType {
    DEFAULT, HOUSE, TOWNHALL, INN, ARCHIVES, MARVEL, VISITORS, WALL, WALLBUILD;
  }
  
  public static class ResourceLine implements Comparable<ResourceLine> {
    public InvItem res;
    
    public int cost;
    
    public int has;
    
    ResourceLine(InvItem res, int cost, int has) {
      this.res = res;
      this.cost = cost;
      this.has = has;
    }
    
    public int compareTo(ResourceLine o) {
      return -this.cost + o.cost;
    }
  }
  
  public static class WallStatusInfos {
    public final List<PanelManager.ResourceLine> resources;
    
    public final int segmentsDone;
    
    public final int segmentsToDo;
    
    public WallStatusInfos(List<PanelManager.ResourceLine> resources, int segmentsDone, int segmentsToDo) {
      this.resources = resources;
      this.segmentsDone = segmentsDone;
      this.segmentsToDo = segmentsToDo;
    }
  }
  
  private static ItemStack FLOWER_PINK = new ItemStack((Block)Blocks.RED_FLOWER, 1, 7);
  
  private static ItemStack FLOWER_BLUE = new ItemStack((Block)Blocks.RED_FLOWER, 1, 1);
  
  private static Map<Item, ItemStack> itemStacks = new HashMap<>();
  
  private static ItemStack stackFromBlock(Block block) {
    Item item = Item.getItemFromBlock(block);
    return stackFromItem(item);
  }
  
  private static ItemStack stackFromItem(Item item) {
    if (itemStacks.containsKey(item))
      return itemStacks.get(item); 
    ItemStack stack = new ItemStack(item);
    itemStacks.put(item, stack);
    return stack;
  }
  
  public long lastSignUpdate = 0L;
  
  private final Building building;
  
  private final Building townHall;
  
  public PanelManager(Building building) {
    this.building = building;
    this.townHall = building.getTownHall();
  }
  
  public WallStatusInfos computeWallInfos(List<BuildingProject> projects, int wallLevel) {
    Map<InvItem, Integer> resCost = new HashMap<>();
    Map<InvItem, Integer> resHas = new HashMap<>();
    int segmentsDone = 0, segmentsToDo = 0;
    String wallTag = "wall_level_" + wallLevel;
    for (BuildingProject project : projects) {
      BuildingPlan startingPlan = project.getPlan(0, 0);
      if (startingPlan != null && startingPlan.isWallSegment) {
        if (project.getExistingPlan() != null && project.getExistingPlan().containsTags(wallTag)) {
          for (InvItem key : (project.getExistingPlan()).resCost.keySet()) {
            if (resCost.containsKey(key)) {
              resCost.put(key, Integer.valueOf(((Integer)resCost.get(key)).intValue() + ((Integer)(project.getExistingPlan()).resCost.get(key)).intValue()));
              resHas.put(key, Integer.valueOf(((Integer)resHas.get(key)).intValue() + ((Integer)(project.getExistingPlan()).resCost.get(key)).intValue()));
              continue;
            } 
            resCost.put(key, (Integer)(project.getExistingPlan()).resCost.get(key));
            resHas.put(key, (Integer)(project.getExistingPlan()).resCost.get(key));
          } 
          segmentsDone++;
          continue;
        } 
        if (project.getNextBuildingPlan(false) != null && project.getNextBuildingPlan(false).containsTags(wallTag)) {
          for (InvItem key : (project.getNextBuildingPlan(false)).resCost.keySet()) {
            if (resCost.containsKey(key)) {
              resCost.put(key, Integer.valueOf(((Integer)resCost.get(key)).intValue() + ((Integer)(project.getNextBuildingPlan(false)).resCost.get(key)).intValue()));
              continue;
            } 
            resCost.put(key, (Integer)(project.getNextBuildingPlan(false)).resCost.get(key));
            resHas.put(key, Integer.valueOf(0));
          } 
          segmentsToDo++;
        } 
      } 
    } 
    for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
      if (cip.getBuildingLocation() != null && cip.isWallConstruction()) {
        BuildingPlan plan = cip.getBuildingLocation().getPlan();
        for (InvItem key : plan.resCost.keySet()) {
          if (resCost.containsKey(key))
            resHas.put(key, Integer.valueOf(((Integer)resHas.get(key)).intValue() + ((Integer)plan.resCost.get(key)).intValue())); 
        } 
      } 
    } 
    for (InvItem key : resCost.keySet()) {
      int availableInTh = this.building.countGoods(key.getItem(), key.meta);
      resHas.put(key, Integer.valueOf(((Integer)resHas.get(key)).intValue() + availableInTh));
    } 
    for (InvItem key : resCost.keySet()) {
      if (((Integer)resHas.get(key)).intValue() > ((Integer)resCost.get(key)).intValue())
        resHas.put(key, resCost.get(key)); 
    } 
    List<ResourceLine> resources = new ArrayList<>();
    for (InvItem key : resCost.keySet())
      resources.add(new ResourceLine(key, ((Integer)resCost.get(key)).intValue(), ((Integer)resHas.get(key)).intValue())); 
    Collections.sort(resources);
    return new WallStatusInfos(resources, segmentsDone, segmentsToDo);
  }
  
  private TileEntityPanel.PanelUntranslatedLine createEmptyLine() {
    TileEntityPanel.PanelUntranslatedLine line = new TileEntityPanel.PanelUntranslatedLine();
    return line;
  }
  
  private TileEntityPanel.PanelUntranslatedLine createFullLine(String fullLine, ItemStack leftIcon, ItemStack rightIcon) {
    return createFullLine(new String[] { fullLine }, leftIcon, rightIcon);
  }
  
  private TileEntityPanel.PanelUntranslatedLine createFullLine(String[] fullLine, ItemStack leftIcon, ItemStack rightIcon) {
    TileEntityPanel.PanelUntranslatedLine line = new TileEntityPanel.PanelUntranslatedLine();
    line.setFullLine(fullLine);
    line.leftIcon = (leftIcon != null) ? leftIcon : ItemStack.EMPTY;
    line.rightIcon = (rightIcon != null) ? rightIcon : ItemStack.EMPTY;
    return line;
  }
  
  private void generateResourceLines(List<ResourceLine> resources, List<TileEntityPanel.PanelUntranslatedLine> lines) {
    int resPos = 0;
    while (resPos < Math.min(resources.size(), (8 - lines.size()) * 2)) {
      ResourceLine resource = resources.get(resPos);
      if (resource.cost < 100) {
        TileEntityPanel.PanelUntranslatedLine panelUntranslatedLine = new TileEntityPanel.PanelUntranslatedLine();
        panelUntranslatedLine.setLeftColumn(new String[] { "" + resource.has + "/" + resource.cost });
        panelUntranslatedLine.leftIcon = resource.res.staticStack;
        if (resPos + 1 < resources.size()) {
          resource = resources.get(resPos + 1);
          panelUntranslatedLine.rightColumn = new String[] { "" + resource.has + "/" + resource.cost };
          panelUntranslatedLine.middleIcon = resource.res.staticStack;
        } 
        lines.add(panelUntranslatedLine);
        resPos += 2;
        continue;
      } 
      TileEntityPanel.PanelUntranslatedLine line = new TileEntityPanel.PanelUntranslatedLine();
      line.setFullLine(new String[] { "" + resource.has + "/" + resource.cost });
      line.leftIcon = resource.res.staticStack;
      line.centerLine = false;
      lines.add(line);
      resPos++;
    } 
  }
  
  private EnumSignType getSignType() {
    if (this.building.isTownhall) {
      if (this.building.villageType.isMarvel())
        return EnumSignType.MARVEL; 
      if (this.building.location.showTownHallSigns)
        return EnumSignType.TOWNHALL; 
      if (this.building.location.getMaleResidents().size() > 0 || this.building.location.getFemaleResidents().size() > 0)
        return EnumSignType.HOUSE; 
      return EnumSignType.DEFAULT;
    } 
    if (this.building.hasVisitors)
      return EnumSignType.VISITORS; 
    if (this.building.isInn)
      return EnumSignType.INN; 
    if (this.building.containsTags("archives"))
      return EnumSignType.ARCHIVES; 
    if (this.building.containsTags("borderpostsign"))
      return EnumSignType.WALL; 
    if (this.building.location.getMaleResidents().size() > 0 || this.building.location.getFemaleResidents().size() > 0)
      return EnumSignType.HOUSE; 
    return EnumSignType.DEFAULT;
  }
  
  private void updateArchiveSigns() {
    if (this.building.world.isRemote)
      return; 
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 16.0D, false);
    if (player == null)
      return; 
    if (this.building.world.getWorldTime() - this.lastSignUpdate < 100L)
      return; 
    if ((this.building.getResManager()).signs.size() == 0)
      return; 
    for (int i = 0; i < (this.building.getResManager()).signs.size(); i++) {
      Point p = (this.building.getResManager()).signs.get(i);
      if (p != null && WorldUtilities.getBlock(this.building.world, p) != MillBlocks.PANEL) {
        EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
        if (facing != null)
          WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
      } 
    } 
    int signId = 0;
    for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
      if (!vr.raidingVillage && !(vr.getType()).visitor && (this.building.getResManager()).signs.get(signId) != null) {
        TileEntityPanel sign = ((Point)(this.building.getResManager()).signs.get(signId)).getPanel(this.building.world);
        if (sign != null) {
          List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
          lines.add(createFullLine(vr.firstName, vr.getType().getIcon(), vr.getType().getIcon()));
          lines.add(createFullLine(vr.familyName, (ItemStack)null, (ItemStack)null));
          lines.add(createEmptyLine());
          if (vr.awayraiding) {
            lines.add(createFullLine("panels.awayraiding", stackFromItem(Items.IRON_AXE), stackFromItem(Items.IRON_AXE)));
          } else if (vr.awayhired) {
            lines.add(createFullLine("panels.awayhired", stackFromItem((Item)MillItems.PURSE), stackFromItem((Item)MillItems.PURSE)));
          } else if (vr.killed) {
            lines.add(createFullLine("panels.dead", stackFromItem(Items.SKULL), stackFromItem(Items.SKULL)));
          } else {
            MillVillager villager = this.building.mw.getVillagerById(vr.getVillagerId());
            if (villager == null) {
              lines.add(createFullLine("panels.missing", stackFromItem(Items.SKULL), stackFromItem(Items.SKULL)));
            } else if (!villager.isVisitor()) {
              String distance = "" + Math.floor(this.building.getPos().distanceTo((Entity)villager));
              String direction = this.building.getPos().directionTo(villager.getPos());
              String occupation = "";
              if (villager.goalKey != null && Goal.goals.containsKey(villager.goalKey))
                occupation = "goal." + ((Goal)Goal.goals.get(villager.goalKey)).labelKey(villager); 
              lines.add(createFullLine(new String[] { "other.shortdistancedirection", distance, direction }, (ItemStack)null, (ItemStack)null));
              lines.add(createFullLine(occupation, (ItemStack)null, (ItemStack)null));
            } 
          } 
          sign.villager_id = vr.getVillagerId();
          sign.untranslatedLines = lines;
          sign.buildingPos = this.building.getTownHallPos();
          sign.panelType = 7;
          sign.texture = this.building.culture.panelTexture;
          sign.triggerUpdate();
          signId++;
        } 
      } 
      if (signId >= (this.building.getResManager()).signs.size())
        break; 
    } 
    for (int j = signId; j < (this.building.getResManager()).signs.size(); j++) {
      if ((this.building.getResManager()).signs.get(j) != null) {
        TileEntityPanel sign = ((Point)(this.building.getResManager()).signs.get(j)).getPanel(this.building.world);
        if (sign != null) {
          List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
          lines.add(createFullLine("ui.reservedforvillager1", (ItemStack)null, (ItemStack)null));
          lines.add(createFullLine("ui.reservedforvillager2", (ItemStack)null, (ItemStack)null));
          lines.add(createEmptyLine());
          lines.add(createFullLine("#" + (j + 1), (ItemStack)null, (ItemStack)null));
          sign.untranslatedLines = lines;
          sign.buildingPos = this.building.getTownHallPos();
          sign.panelType = 0;
          sign.texture = this.building.culture.panelTexture;
          sign.triggerUpdate();
        } 
      } 
    } 
    this.lastSignUpdate = this.building.world.getWorldTime();
  }
  
  private void updateBorderPostSign() {
    if (this.building.world.isRemote)
      return; 
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 20.0D, false);
    if (player == null)
      return; 
    if ((this.building.getResManager()).signs.size() == 0)
      return; 
    for (int i = 0; i < (this.building.getResManager()).signs.size(); i++) {
      Point p = (this.building.getResManager()).signs.get(i);
      if (p != null && WorldUtilities.getBlock(this.building.world, p) != MillBlocks.PANEL) {
        EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
        if (facing != null)
          WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
      } 
    } 
    TileEntityPanel sign = ((Point)(this.building.getResManager()).signs.get(0)).getPanel(this.building.world);
    if (sign != null && this.building.getTownHall() != null) {
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      int nbvill = 0;
      for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
        if (vr != null) {
          boolean belongsToVillage = (!vr.raidingVillage && vr.getType() != null && !(vr.getType()).visitor);
          if (belongsToVillage)
            nbvill++; 
        } 
      } 
      lines.add(createFullLine(this.building.getTownHall().getVillageNameWithoutQualifier(), this.building.getTownHall().getBannerStack(), this.building.getTownHall().getBannerStack()));
      lines.add(createFullLine(this.building.getTownHall().getQualifier(), (ItemStack)null, (ItemStack)null));
      lines.add(createEmptyLine());
      lines.add(createFullLine(new String[] { "ui.populationnumber", "" + nbvill }, (ItemStack)null, (ItemStack)null));
      if ((this.building.getTownHall()).controlledBy != null) {
        lines.add(createEmptyLine());
        lines.add(createFullLine((this.building.getTownHall()).controlledByName, stackFromItem((Item)Items.GOLDEN_HELMET), stackFromItem((Item)Items.GOLDEN_HELMET)));
      } else {
        lines.add(createEmptyLine());
        lines.add(createFullLine("Visits welcome", (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine("ui.borderpost_constructionforbidden", (ItemStack)null, (ItemStack)null));
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getTownHallPos();
      sign.panelType = 8;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateDefaultSign() {
    if (this.building.world.isRemote)
      return; 
    if ((this.building.getResManager()).signs.size() == 0)
      return; 
    if (this.building.getPos() == null || this.building.location == null)
      return; 
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 16.0D, false);
    if (player == null)
      return; 
    if (this.building.world.getWorldTime() - this.lastSignUpdate < 100L)
      return; 
    Point p = (this.building.getResManager()).signs.get(0);
    if (p == null)
      return; 
    if (WorldUtilities.getBlock(this.building.world, p.getiX(), p.getiY(), p.getiZ()) != MillBlocks.PANEL) {
      EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
      if (facing != null)
        WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
    } 
    TileEntityPanel sign = p.getPanel(this.building.world);
    if (sign == null) {
      MillLog.error(this, "No TileEntitySign at: " + p);
    } else {
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
      lines.add(createEmptyLine());
      lines.add(createFullLine(this.building.getGameBuildingName(), (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
    this.lastSignUpdate = this.building.world.getWorldTime();
  }
  
  private void updateHouseSign() {
    if (this.building.world.isRemote)
      return; 
    if ((this.building.getResManager()).signs.size() == 0)
      return; 
    if (this.building.getPos() == null || this.building.location == null)
      return; 
    if (this.building.isTownhall && this.building.location.showTownHallSigns)
      return; 
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 16.0D, false);
    if (player == null)
      return; 
    if (this.building.world.getWorldTime() - this.lastSignUpdate < 100L)
      return; 
    VillagerRecord wife = null, husband = null;
    int nbMaleAdults = 0, nbFemaleAdults = 0, nbResidents = 0;
    for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
      if (this.building.getPos().equals(vr.getHousePos())) {
        if (vr.gender == 2 && (vr.getType() == null || !(vr.getType()).isChild)) {
          wife = vr;
          nbFemaleAdults++;
        } 
        if (vr.gender == 1 && (vr.getType() == null || !(vr.getType()).isChild)) {
          husband = vr;
          nbMaleAdults++;
        } 
        nbResidents++;
      } 
    } 
    Point p = (this.building.getResManager()).signs.get(0);
    if (p == null)
      return; 
    if (WorldUtilities.getBlock(this.building.world, p.getiX(), p.getiY(), p.getiZ()) != MillBlocks.PANEL) {
      EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
      if (facing != null)
        WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
    } 
    TileEntityPanel sign = p.getPanel(this.building.world);
    if (sign == null) {
      MillLog.error(this, "No TileEntitySign at: " + p);
    } else {
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
      lines.add(createEmptyLine());
      if ((wife != null || husband != null) && nbMaleAdults < 2 && nbFemaleAdults < 2) {
        if (husband != null && wife != null) {
          lines.add(createFullLine(new String[] { "panels.nameand", wife.firstName }, wife.getType().getIcon(), wife.getType().getIcon()));
          lines.add(createFullLine(husband.firstName, husband.getType().getIcon(), husband.getType().getIcon()));
          lines.add(createFullLine(husband.familyName, (ItemStack)null, (ItemStack)null));
        } else if (husband != null) {
          lines.add(createFullLine(husband.firstName, husband.getType().getIcon(), husband.getType().getIcon()));
          lines.add(createFullLine(husband.familyName, (ItemStack)null, (ItemStack)null));
        } else if (wife != null) {
          lines.add(createFullLine(wife.firstName, wife.getType().getIcon(), wife.getType().getIcon()));
          lines.add(createFullLine(wife.familyName, (ItemStack)null, (ItemStack)null));
        } 
      } else if (nbResidents > 0) {
        for (VillagerRecord vr : this.building.getTownHall().getVillagerRecords().values()) {
          if (this.building.getPos().equals(vr.getHousePos()))
            lines.add(createFullLine(vr.firstName, vr.getType().getIcon(), vr.getType().getIcon())); 
        } 
      } else {
        lines.add(createFullLine("ui.currentlyempty1", (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine("ui.currentlyempty2", (ItemStack)null, (ItemStack)null));
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 5;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
    this.lastSignUpdate = this.building.world.getWorldTime();
  }
  
  private void updateInnSign() {
    if (this.building.world.isRemote)
      return; 
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 20.0D, false);
    if (player == null)
      return; 
    if ((this.building.getResManager()).signs.size() == 0)
      return; 
    for (int i = 0; i < (this.building.getResManager()).signs.size(); i++) {
      Point p = (this.building.getResManager()).signs.get(i);
      if (p != null && WorldUtilities.getBlock(this.building.world, p) != MillBlocks.PANEL) {
        EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
        if (facing != null)
          WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
      } 
    } 
    TileEntityPanel sign = ((Point)(this.building.getResManager()).signs.get(0)).getPanel(this.building.world);
    if (sign != null) {
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
      lines.add(createEmptyLine());
      lines.add(createFullLine("ui.visitorslist1", (ItemStack)null, (ItemStack)null));
      lines.add(createFullLine("ui.visitorslist2", (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 11;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
    if ((this.building.getResManager()).signs.size() < 2)
      return; 
    sign = ((Point)(this.building.getResManager()).signs.get(1)).getPanel(this.building.world);
    if (sign != null) {
      List<String[]> linesFull = (List)new ArrayList<>();
      List<ItemStack> icons = new ArrayList<>();
      linesFull.add(new String[] { "ui.goodstraded" });
      linesFull.add(new String[] { "" });
      linesFull.add(new String[] { "ui.import_total", "" + MillCommonUtilities.getInvItemHashTotal(this.building.imported) });
      linesFull.add(new String[] { "ui.export_total", "" + MillCommonUtilities.getInvItemHashTotal(this.building.exported) });
      icons.add(stackFromBlock((Block)Blocks.CHEST));
      sign.buildingPos = this.building.getPos();
      sign.panelType = 10;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateMarvelDonationsSign(TileEntityPanel sign) {
    if (sign != null) {
      Set<String> villages = new HashSet<>();
      for (String s : this.townHall.getMarvelManager().getDonationList()) {
        String village = s.split(";")[1];
        villages.add(village);
      } 
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine("ui.paneldonations1", stackFromItem((Item)MillItems.DENIER_OR), stackFromItem((Item)MillItems.DENIER_OR)));
      lines.add(createEmptyLine());
      lines.add(createFullLine(new String[] { "ui.paneldonations2", "" + this.townHall.getMarvelManager().getDonationList().size() }, (ItemStack)null, (ItemStack)null));
      lines.add(createFullLine(new String[] { "ui.paneldonations3", "" + villages.size() }, (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 20;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateMarvelProjectsSign(TileEntityPanel sign) {
    if (sign != null) {
      List<BuildingProject> projects = this.townHall.getFlatProjectList();
      int totalProjects = 0, doneProjects = 0;
      for (BuildingProject project : projects) {
        BuildingPlan plan = project.planSet.getFirstStartingPlan();
        BuildingPlan parentPlan = project.parentPlan;
        if (plan.containsTags("marvel") || (parentPlan != null && parentPlan.containsTags("marvel"))) {
          totalProjects += ((BuildingPlan[])project.planSet.plans.get(0)).length;
          if (project.location == null || project.location.level < 0)
            continue; 
          boolean obsolete = (project.planSet != null && project.location.version != (((BuildingPlan[])project.planSet.plans.get(project.location.getVariation()))[0]).version);
          if (project.location.level + 1 >= project.getLevelsNumber(project.location.getVariation())) {
            doneProjects += ((BuildingPlan[])project.planSet.plans.get(0)).length;
            continue;
          } 
          if (obsolete) {
            doneProjects += project.location.level + 1;
            continue;
          } 
          doneProjects += project.location.level + 1;
        } 
      } 
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine("ui.panelmarvelprojects", stackFromItem(Items.IRON_SHOVEL), stackFromItem(Items.IRON_SHOVEL)));
      lines.add(createEmptyLine());
      lines.add(createFullLine(new String[] { "ui.panelmarvelprojectsdone", "" + doneProjects }, (ItemStack)null, (ItemStack)null));
      lines.add(createFullLine(new String[] { "ui.panelmarvelprojectstotal", "" + totalProjects }, (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 3;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateMarvelResourcesSign(TileEntityPanel sign) {
    if (sign != null) {
      Map<InvItem, Integer> totalCost = this.townHall.villageType.computeVillageTypeCost();
      Map<InvItem, Integer> remainingNeeds = this.townHall.getMarvelManager().computeNeeds();
      int totalCostSum = 0, remainingNeedsSum = 0;
      for (Integer cost : totalCost.values())
        totalCostSum += cost.intValue(); 
      for (Integer needs : remainingNeeds.values()) {
        if (needs.intValue() > 0)
          remainingNeedsSum += needs.intValue(); 
      } 
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine("ui.panelmarvelres1", stackFromItem(Item.getItemFromBlock((Block)Blocks.CHEST)), stackFromItem(Item.getItemFromBlock((Block)Blocks.CHEST))));
      lines.add(createFullLine("ui.panelmarvelres2", (ItemStack)null, (ItemStack)null));
      lines.add(createEmptyLine());
      lines.add(createFullLine(new String[] { "ui.panelmarvelrescount", String.format("%,d", new Object[] { Integer.valueOf(totalCostSum - remainingNeedsSum) }), String.format("%,d", new Object[] { Integer.valueOf(totalCostSum) }) }, (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 3;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateMarvelSigns(boolean forced) {
    if (this.townHall.world.isRemote)
      return; 
    EntityPlayer player = this.townHall.world.getClosestPlayer(this.townHall.getPos().getiX(), this.townHall.getPos().getiY(), this.townHall.getPos().getiZ(), 20.0D, false);
    if (player == null)
      return; 
    if (!forced && this.townHall.world.getWorldTime() - this.lastSignUpdate < 40L)
      return; 
    if ((this.townHall.getResManager()).signs.size() < 7)
      return; 
    for (int i = 0; i < (this.townHall.getResManager()).signs.size(); i++) {
      Point p = (this.townHall.getResManager()).signs.get(i);
      if (p != null && WorldUtilities.getBlock(this.townHall.world, p) != MillBlocks.PANEL) {
        EnumFacing facing = WorldUtilities.guessPanelFacing(this.townHall.world, p);
        if (facing != null)
          WorldUtilities.setBlockstate(this.townHall.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
      } 
    } 
    int signPos = 0;
    TileEntityPanel sign = (TileEntityPanel)this.townHall.world.getTileEntity(((Point)(this.townHall.getResManager()).signs.get(signPos)).getBlockPos());
    updateSignTHVillageName(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateSignTHResources(sign);
    signPos++;
    signPos++;
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateSignTHProject(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateSignTHConstruction(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateSignTHEtatCivil(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateSignTHMap(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateSignTHMilitary(sign);
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateMarvelProjectsSign(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateMarvelResourcesSign(sign);
    signPos++;
    sign = ((Point)(this.townHall.getResManager()).signs.get(signPos)).getPanel(this.townHall.world);
    updateMarvelDonationsSign(sign);
    signPos++;
    this.lastSignUpdate = this.townHall.world.getWorldTime();
  }
  
  public void updateSigns() {
    EnumSignType type = getSignType();
    if (type == EnumSignType.MARVEL) {
      updateMarvelSigns(false);
    } else if (type == EnumSignType.TOWNHALL) {
      updateTownHallSigns(false);
    } else if (type == EnumSignType.ARCHIVES) {
      updateArchiveSigns();
    } else if (type == EnumSignType.VISITORS) {
      updateVisitorsSigns();
    } else if (type == EnumSignType.INN) {
      updateInnSign();
    } else if (type == EnumSignType.WALL) {
      updateBorderPostSign();
    } else if (type == EnumSignType.HOUSE) {
      updateHouseSign();
    } else if (type == EnumSignType.DEFAULT) {
      updateDefaultSign();
    } 
  }
  
  private void updateSignTHConstruction(TileEntityPanel sign) {
    if (sign != null) {
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      ConstructionIP activeCIP = null;
      int nbActiveCIP = 0;
      for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
        if (cip.getBuildingLocation() != null) {
          nbActiveCIP++;
          if (activeCIP == null)
            activeCIP = cip; 
        } 
      } 
      if (nbActiveCIP == 1) {
        String[] status, loc, constr;
        BuildingPlanSet planSet = this.building.culture.getBuildingPlanSet((activeCIP.getBuildingLocation()).planKey);
        String planName = planSet.getNameNative();
        if ((activeCIP.getBuildingLocation()).level == 0) {
          status = new String[] { "ui.inconstruction" };
        } else {
          status = new String[] { "ui.upgrading", "" + (activeCIP.getBuildingLocation()).level };
        } 
        if (activeCIP.getBuildingLocation() != null) {
          int distance = MathHelper.floor(this.building.getPos().distanceTo((activeCIP.getBuildingLocation()).pos));
          String direction = this.building.getPos().directionTo((activeCIP.getBuildingLocation()).pos);
          loc = new String[] { "other.shortdistancedirection", "" + distance, "" + direction };
        } else {
          loc = new String[] { "" };
        } 
        if (activeCIP.getBblocks() != null && (activeCIP.getBblocks()).length > 0) {
          constr = new String[] { "ui.progress", "" + (int)Math.floor((activeCIP.getBblocksPos() * 100 / (activeCIP.getBblocks()).length)) };
        } else {
          constr = new String[] { "ui.progressnopercent" };
        } 
        lines.add(createFullLine(planName, planSet.getIcon(), planSet.getIcon()));
        lines.add(createEmptyLine());
        lines.add(createFullLine(constr, (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine(status, (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine(loc, (ItemStack)null, (ItemStack)null));
      } else if (nbActiveCIP > 1) {
        lines.add(createFullLine(new String[] { "ui.xconstructions", "" + nbActiveCIP }, (ItemStack)null, (ItemStack)null));
        lines.add(createEmptyLine());
        int cipPos = 0;
        for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
          if (cip.getBuildingLocation() != null && cipPos < 4) {
            String planName = this.building.culture.getBuildingPlanSet((cip.getBuildingLocation()).planKey).getNameNative();
            ItemStack icon = this.building.culture.getBuildingPlanSet((cip.getBuildingLocation()).planKey).getIcon();
            String level = "l0";
            if ((cip.getBuildingLocation()).level > 0)
              level = "l" + (cip.getBuildingLocation()).level; 
            lines.add(createFullLine(planName + " " + level, icon, icon));
            cipPos++;
          } 
        } 
      } else {
        lines.add(createEmptyLine());
        lines.add(createEmptyLine());
        lines.add(createFullLine("ui.noconstruction1", (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine("ui.noconstruction2", (ItemStack)null, (ItemStack)null));
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 2;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateSignTHEtatCivil(TileEntityPanel sign) {
    if (sign != null) {
      int nbMen = 0, nbFemale = 0, nbGrownBoy = 0, nbGrownGirl = 0, nbBoy = 0, nbGirl = 0;
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      for (VillagerRecord vr : this.building.getVillagerRecords().values()) {
        boolean belongsToVillage = (vr.getType() != null && !(vr.getType()).visitor && !vr.raidingVillage);
        if (belongsToVillage) {
          if (!(vr.getType()).isChild) {
            if (vr.gender == 1) {
              nbMen++;
              continue;
            } 
            nbFemale++;
            continue;
          } 
          if (vr.size == 20) {
            if (vr.gender == 1) {
              nbGrownBoy++;
              continue;
            } 
            nbGrownGirl++;
            continue;
          } 
          if (vr.gender == 1) {
            nbBoy++;
            continue;
          } 
          nbGirl++;
        } 
      } 
      lines.add(createFullLine("ui.population", FLOWER_BLUE, FLOWER_PINK));
      lines.add(createEmptyLine());
      lines.add(createFullLine(new String[] { "ui.adults", "" + (nbMen + nbFemale), "" + nbMen, "" + nbFemale }, (ItemStack)null, (ItemStack)null));
      lines.add(createFullLine(new String[] { "ui.teens", "" + (nbGrownBoy + nbGrownGirl), "" + nbGrownBoy, "" + nbGrownGirl }, (ItemStack)null, (ItemStack)null));
      lines.add(createFullLine(new String[] { "ui.children", "" + (nbBoy + nbGirl), "" + nbBoy, "" + nbGirl }, (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 1;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateSignTHMap(TileEntityPanel sign) {
    List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
    lines.add(createFullLine("ui.villagemap", stackFromItem((Item)Items.FILLED_MAP), stackFromItem((Item)Items.FILLED_MAP)));
    lines.add(createEmptyLine());
    int nbBuildings = 0;
    for (Building building : this.building.getBuildings()) {
      if (building.location.isCustomBuilding || !(building.location.getPlan()).isWallSegment)
        nbBuildings++; 
    } 
    lines.add(createFullLine(new String[] { "ui.nbbuildings", "" + nbBuildings }, (ItemStack)null, (ItemStack)null));
    sign.untranslatedLines = lines;
    sign.buildingPos = this.building.getPos();
    sign.panelType = 8;
    sign.texture = this.building.culture.panelTexture;
    sign.triggerUpdate();
  }
  
  private void updateSignTHMilitary(TileEntityPanel sign) {
    if (sign != null) {
      int type;
      String status = "";
      if (this.building.raidTarget != null) {
        if (this.building.raidStart > 0L) {
          status = "panels.raidinprogress";
        } else {
          status = "panels.planningraid";
        } 
      } else if (this.building.underAttack) {
        status = "panels.underattack";
      } 
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine("panels.military", stackFromItem(Items.IRON_SWORD), stackFromItem(Items.IRON_SWORD)));
      lines.add(createEmptyLine());
      if (status.length() > 0)
        lines.add(createFullLine(status, (ItemStack)null, (ItemStack)null)); 
      lines.add(createFullLine(new String[] { "panels.offense", "" + this.building.getVillageRaidingStrength() }, stackFromItem(Items.IRON_AXE), stackFromItem(Items.IRON_AXE)));
      lines.add(createFullLine(new String[] { "panels.defense", "" + this.building.getVillageDefendingStrength() }, stackFromItem((Item)Items.IRON_CHESTPLATE), stackFromItem((Item)Items.IRON_CHESTPLATE)));
      if (this.building.villageType.playerControlled) {
        type = 13;
      } else {
        type = 9;
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = type;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateSignTHProject(TileEntityPanel sign) {
    if (sign != null) {
      int type;
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      String[] status = null;
      if (this.building.buildingGoal == null) {
        lines.add(createEmptyLine());
        lines.add(createEmptyLine());
        lines.add(createFullLine("ui.goalscompleted1", (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine("ui.goalscompleted2", (ItemStack)null, (ItemStack)null));
      } else {
        BuildingPlan goal = this.building.getCurrentGoalBuildingPlan();
        boolean inprogress = false;
        lines.add(createFullLine("ui.project", goal.getIcon(), goal.getIcon()));
        lines.add(createEmptyLine());
        lines.add(createFullLine(goal.nativeName, (ItemStack)null, (ItemStack)null));
        lines.add(createFullLine(goal.getGameNameKey(), (ItemStack)null, (ItemStack)null));
        for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
          if (cip.getBuildingLocation() != null && (cip.getBuildingLocation()).planKey.equals(this.building.buildingGoal)) {
            if ((cip.getBuildingLocation()).level == 0) {
              status = new String[] { "ui.inconstruction" };
            } else {
              status = new String[] { "ui.upgrading", "" + (cip.getBuildingLocation()).level };
            } 
            inprogress = true;
          } 
        } 
        if (!inprogress)
          status = new String[] { this.building.buildingGoalIssue }; 
        lines.add(createEmptyLine());
        lines.add(createFullLine(status, (ItemStack)null, (ItemStack)null));
      } 
      if (this.building.villageType.playerControlled) {
        type = 4;
      } else {
        type = 3;
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = type;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateSignTHResources(TileEntityPanel sign) {
    if (sign != null) {
      BuildingPlan goalPlan = this.building.getCurrentGoalBuildingPlan();
      List<InvItem> res = new ArrayList<>();
      List<Integer> resCost = new ArrayList<>();
      List<Integer> resHas = new ArrayList<>();
      if (goalPlan != null) {
        boolean inprogress = false;
        for (ConstructionIP cip : this.building.getConstructionsInProgress()) {
          if (cip.getBuildingLocation() != null && (cip.getBuildingLocation()).planKey.equals(this.building.buildingGoal))
            inprogress = true; 
        } 
        if (inprogress) {
          for (InvItem key : goalPlan.resCost.keySet()) {
            res.add(key);
            resCost.add((Integer)goalPlan.resCost.get(key));
            resHas.add((Integer)goalPlan.resCost.get(key));
          } 
        } else {
          for (InvItem key : goalPlan.resCost.keySet()) {
            res.add(key);
            resCost.add((Integer)goalPlan.resCost.get(key));
            int has = this.building.countGoods(key.getItem(), key.meta);
            if (has > ((Integer)goalPlan.resCost.get(key)).intValue())
              has = ((Integer)goalPlan.resCost.get(key)).intValue(); 
            resHas.add(Integer.valueOf(has));
          } 
        } 
      } 
      List<ResourceLine> resources = new ArrayList<>();
      for (int i = 0; i < res.size(); i++)
        resources.add(new ResourceLine(res.get(i), ((Integer)resCost.get(i)).intValue(), ((Integer)resHas.get(i)).intValue())); 
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      if (goalPlan != null) {
        lines.add(createFullLine("ui.resources", stackFromBlock((Block)Blocks.CHEST), stackFromBlock((Block)Blocks.CHEST)));
        if (res.size() < 12)
          lines.add(createEmptyLine()); 
        generateResourceLines(resources, lines);
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 6;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateSignTHVillageName(TileEntityPanel sign) {
    if (sign != null) {
      int nbvill = 0;
      for (VillagerRecord vr : this.building.getVillagerRecords().values()) {
        if (vr != null) {
          boolean belongsToVillage = (!vr.raidingVillage && vr.getType() != null && !(vr.getType()).visitor);
          if (belongsToVillage)
            nbvill++; 
        } 
      } 
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine(this.building.getVillageNameWithoutQualifier(), this.building.getBannerStack(), this.building.getBannerStack()));
      lines.add(createFullLine(this.building.getQualifier(), (ItemStack)null, (ItemStack)null));
      lines.add(createEmptyLine());
      lines.add(createFullLine(this.building.villageType.name, (ItemStack)null, (ItemStack)null));
      lines.add(createFullLine(new String[] { "ui.populationnumber", "" + nbvill }, (ItemStack)null, (ItemStack)null));
      if (this.building.controlledBy != null) {
        lines.add(createEmptyLine());
        lines.add(createFullLine(this.building.controlledByName, stackFromItem((Item)Items.GOLDEN_HELMET), stackFromItem((Item)Items.GOLDEN_HELMET)));
      } 
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = 1;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
  
  private void updateSignTHWalls(TileEntityPanel sign) {
    if (sign == null)
      return; 
    List<BuildingProject> projects = this.townHall.getFlatProjectList();
    int wallLevel = this.townHall.computeCurrentWallLevel();
    List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
    lines.add(createFullLine("ui.panelwalls", stackFromBlock(Blocks.COBBLESTONE_WALL), stackFromBlock(Blocks.COBBLESTONE_WALL)));
    if (wallLevel == Integer.MAX_VALUE) {
      lines.add(createEmptyLine());
      lines.add(createFullLine("ui.panelwallscomplete", (ItemStack)null, (ItemStack)null));
    } else if (wallLevel == -1) {
      lines.add(createEmptyLine());
      lines.add(createFullLine("ui.panelwallnowalls", (ItemStack)null, (ItemStack)null));
    } else {
      WallStatusInfos wallInfos = computeWallInfos(projects, wallLevel);
      lines.add(createFullLine(new String[] { "ui.panelwallslevel", "" + wallLevel, "" + wallInfos.segmentsDone, "" + (wallInfos.segmentsDone + wallInfos.segmentsToDo) }, (ItemStack)null, (ItemStack)null));
      if (wallInfos.resources.size() < (8 - lines.size()) * 2)
        lines.add(createEmptyLine()); 
      generateResourceLines(wallInfos.resources, lines);
    } 
    sign.untranslatedLines = lines;
    sign.buildingPos = this.building.getPos();
    sign.panelType = 15;
    sign.texture = this.building.culture.panelTexture;
    sign.triggerUpdate();
  }
  
  private void updateTownHallSigns(boolean forced) {
    if (this.building.world.isRemote)
      return; 
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 20.0D, false);
    if (player == null)
      return; 
    if (!forced && this.building.world.getWorldTime() - this.lastSignUpdate < 40L)
      return; 
    for (int i = 0; i < (this.building.getResManager()).signs.size(); i++) {
      Point p = (this.building.getResManager()).signs.get(i);
      if (p != null) {
        if (WorldUtilities.getBlock(this.building.world, p) != MillBlocks.PANEL) {
          EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
          if (facing != null)
            WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
        } 
        TileEntityPanel sign = (TileEntityPanel)this.building.world.getTileEntity(p.getBlockPos());
        if (sign != null)
          switch (i) {
            case 0:
              updateSignTHVillageName(sign);
              break;
            case 1:
              updateSignTHResources(sign);
              break;
            case 2:
              updateSignTHWalls(sign);
              break;
            case 3:
              sign.texture = this.building.culture.panelTexture;
              sign.triggerUpdate();
              break;
            case 4:
              updateSignTHProject(sign);
              break;
            case 5:
              updateSignTHConstruction(sign);
              break;
            case 6:
              updateSignTHEtatCivil(sign);
              break;
            case 7:
              updateSignTHMap(sign);
              break;
            case 8:
              updateSignTHMilitary(sign);
              break;
          }  
      } 
    } 
    this.lastSignUpdate = this.building.world.getWorldTime();
  }
  
  public void updateVisitorsSigns() {
    EntityPlayer player = this.building.world.getClosestPlayer(this.building.getPos().getiX(), this.building.getPos().getiY(), this.building.getPos().getiZ(), 20.0D, false);
    if (player == null)
      return; 
    if ((this.building.getResManager()).signs.size() == 0 || (this.building.getResManager()).signs.get(0) == null)
      return; 
    for (int i = 0; i < (this.building.getResManager()).signs.size(); i++) {
      Point p = (this.building.getResManager()).signs.get(i);
      if (p != null && WorldUtilities.getBlock(this.building.world, p) != MillBlocks.PANEL) {
        EnumFacing facing = WorldUtilities.guessPanelFacing(this.building.world, p);
        if (facing != null)
          WorldUtilities.setBlockstate(this.building.world, p, MillBlocks.PANEL.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), true, false); 
      } 
    } 
    TileEntityPanel sign = ((Point)(this.building.getResManager()).signs.get(0)).getPanel(this.building.world);
    if (sign != null) {
      List<TileEntityPanel.PanelUntranslatedLine> lines = new ArrayList<>();
      lines.add(createFullLine(this.building.getNativeBuildingName(), this.building.getIcon(), this.building.getIcon()));
      lines.add(createEmptyLine());
      lines.add(createFullLine("ui.visitorslist2", (ItemStack)null, (ItemStack)null));
      int type = 0;
      if (this.building.isMarket) {
        lines.add(createFullLine("ui.merchants", (ItemStack)null, (ItemStack)null));
        type = 12;
      } else {
        lines.add(createFullLine("ui.visitors", (ItemStack)null, (ItemStack)null));
        type = 14;
      } 
      lines.add(createFullLine(new String[] { "" + this.building.getAllVillagerRecords().size() }, (ItemStack)null, (ItemStack)null));
      sign.untranslatedLines = lines;
      sign.buildingPos = this.building.getPos();
      sign.panelType = type;
      sign.texture = this.building.culture.panelTexture;
      sign.triggerUpdate();
    } 
  }
}
