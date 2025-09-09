package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.goal.leisure.GoalGoChat;
import org.millenaire.common.goal.leisure.GoalGoRest;
import org.millenaire.common.goal.leisure.GoalGoSocialise;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public abstract class Goal {
  public static final int STANDARD_DELAY = 2000;
  
  public static HashMap<String, Goal> goals;
  
  public static GoalBeSeller beSeller;
  
  public static Goal construction;
  
  public static Goal deliverGoodsHousehold;
  
  public static Goal getResourcesForBuild;
  
  public static Goal raidVillage;
  
  public static Goal defendVillage;
  
  public static Goal hide;
  
  public static Goal sleep;
  
  public static Goal gettool;
  
  public static Goal gosocialise;
  
  public static class GoalInformation {
    private Point dest;
    
    private Point destBuildingPos;
    
    private Entity targetEnt;
    
    public GoalInformation(Point dest, Point buildingPos, Entity targetEnt) {
      this.dest = dest;
      this.destBuildingPos = buildingPos;
      this.targetEnt = targetEnt;
    }
    
    public Point getDest() {
      return this.dest;
    }
    
    public Point getDestBuildingPos() {
      return this.destBuildingPos;
    }
    
    public Entity getTargetEnt() {
      return this.targetEnt;
    }
    
    public void setDest(Point dest) {
      this.dest = dest;
    }
    
    public void setDestBuildingPos(Point destBuildingPos) {
      this.destBuildingPos = destBuildingPos;
    }
    
    public void setTargetEnt(Entity targetEnt) {
      this.targetEnt = targetEnt;
    }
  }
  
  public static final AStarConfig JPS_CONFIG_TIGHT = new AStarConfig(true, false, false, false, true);
  
  public static final AStarConfig JPS_CONFIG_WIDE = new AStarConfig(true, false, false, false, true, 2, 10);
  
  public static final AStarConfig JPS_CONFIG_BUILDING = new AStarConfig(true, false, false, false, true, 2, 60);
  
  public static final AStarConfig JPS_CONFIG_BUILDING_SCAFFOLDINGS = new AStarConfig(true, false, false, false, true, 2, 5);
  
  public static final AStarConfig JPS_CONFIG_CHOPLUMBER = new AStarConfig(true, false, false, false, true, 4, 60);
  
  public static final AStarConfig JPS_CONFIG_SLAUGHTERSQUIDS = new AStarConfig(true, false, false, false, true, 6, 4);
  
  public static final AStarConfig JPS_CONFIG_TIGHT_NO_LEAVES = new AStarConfig(true, false, false, false, false);
  
  public static final AStarConfig JPS_CONFIG_WIDE_NO_LEAVES = new AStarConfig(true, false, false, false, false, 2, 10);
  
  public static final AStarConfig JPS_CONFIG_BUILDING_NO_LEAVES = new AStarConfig(true, false, false, false, false, 2, 60);
  
  public static final AStarConfig JPS_CONFIG_CHOPLUMBER_NO_LEAVES = new AStarConfig(true, false, false, false, false, 4, 20);
  
  public static final AStarConfig JPS_CONFIG_SLAUGHTERSQUIDS_NO_LEAVES = new AStarConfig(true, false, false, false, false, 6, 4);
  
  public static final String TAG_CONSTRUCTION = "tag_construction";
  
  public static final String TAG_AGRICULTURE = "tag_agriculture";
  
  protected static final int ACTIVATION_RANGE = 3;
  
  public String key;
  
  public static void initGoals() {
    goals = new HashMap<>();
    goals.put("gorest", new GoalGoRest());
    gosocialise = (Goal)new GoalGoSocialise();
    goals.put("gosocialise", gosocialise);
    goals.put("chat", new GoalGoChat());
    goals.put("gathergoods", new GoalGatherGoods());
    goals.put("bringbackresourceshome", new GoalBringBackResourcesHome());
    gettool = new GoalGetTool();
    goals.put("getitemtokeep", gettool);
    goals.put("huntmonster", new GoalHuntMonster());
    goals.put("getgoodshousehold", new GoalGetGoodsForHousehold());
    sleep = new GoalSleep();
    goals.put("sleep", sleep);
    deliverGoodsHousehold = new GoalDeliverGoodsHousehold();
    goals.put("delivergoodshousehold", deliverGoodsHousehold);
    goals.put("gethousethresources", new GoalGetResourcesForShops());
    goals.put("deliverresourcesshop", new GoalDeliverResourcesShop());
    goals.put("choptrees", new GoalLumbermanChopTrees());
    goals.put("plantsaplings", new GoalLumbermanPlantSaplings());
    getResourcesForBuild = new GoalGetResourcesForBuild();
    goals.put("getresourcesforbuild", getResourcesForBuild);
    beSeller = new GoalBeSeller();
    goals.put("beseller", beSeller);
    construction = new GoalConstructionStepByStep();
    goals.put("construction", construction);
    goals.put("buildpath", new GoalBuildPath());
    goals.put("clearoldpath", new GoalClearOldPath());
    raidVillage = new GoalRaidVillage();
    goals.put("raidvillage", raidVillage);
    defendVillage = new GoalDefendVillage();
    goals.put("defendvillage", defendVillage);
    hide = new GoalHide();
    goals.put("hide", hide);
    goals.put("becomeadult", new GoalChildBecomeAdult());
    goals.put("shearsheep", new GoalShearSheep());
    goals.put("breed", new GoalBreedAnimals());
    goals.put("mining", new GoalMinerMineResource());
    goals.put("visitinn", new GoalMerchantVisitInn());
    goals.put("visitbuilding", new GoalMerchantVisitBuilding());
    goals.put("keepstall", new GoalForeignMerchantKeepStall());
    goals.put("drybrick", new GoalIndianDryBrick());
    goals.put("gatherbrick", new GoalIndianGatherBrick());
    goals.put("plantsugarcane", new GoalIndianPlantSugarCane());
    goals.put("harvestsugarcane", new GoalIndianHarvestSugarCane());
    goals.put("performpujas", new GoalPerformPuja());
    goals.put("bepujaperformer", new GoalBePujaPerformer());
    goals.put("fish", new GoalFish());
    goals.put("fishinuit", new GoalFishInuit());
    goals.put("harvestwarts", new GoalHarvestWarts());
    goals.put("plantwarts", new GoalPlantNetherWarts());
    goals.put("brewpotions", new GoalBrewPotions());
    goals.put("gathersilk", new GoalByzantineGatherSilk());
    goals.put("gathersnails", new GoalByzantineGatherSnails());
    goals.put("plantcocoa", new GoalPlantCacao());
    goals.put("harvestcocoa", new GoalHarvestCacao());
    if (MillConfigValues.generateHelpData)
      DocumentedElement.generateClassHelp("goals", "Hardcoded Goals.txt", goals, "List of hard-coded goals in Millénaire."); 
    GoalGeneric.loadGenericGoals();
    for (String s : goals.keySet()) {
      ((Goal)goals.get(s)).key = s;
      ((Goal)goals.get(s)).tags.add(s);
    } 
  }
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "false")
  @FieldDocumentation(explanation = "If true, this is a leisure activity that can be interrupted by other goals.")
  public boolean leasure = false;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, defaultValue = "true")
  @FieldDocumentation(explanation = "If true, villagers performing this goal will move faster than normal.")
  public boolean sprint = true;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "tag")
  @FieldDocumentation(explanation = "A tag to use when referring to the goal elsewhere, such as in generic visit targeting. Has no effects on the goal itself.")
  public List<String> tags = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "If more than that number of item is present in the building, stop goal.")
  public HashMap<InvItem, Integer> buildingLimit = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "If more than that number of item is present in the town hall, stop goal.")
  public HashMap<InvItem, Integer> townhallLimit = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "If more than that number of item is present in the village, stop goal.")
  public HashMap<InvItem, Integer> villageLimit = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "No more than X villagers doing this goal in the same building at the same time.")
  public int maxSimultaneousInBuilding = 0;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "0")
  @FieldDocumentation(explanation = "No more than X villagers doing this goal at the same time.")
  public int maxSimultaneousTotal = 0;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_PAIR, paramName = "itemsbalance")
  @FieldDocumentation(explanation = "Pair of items whose quantity to balance in the TH. If there are more of the second than the first, the goal won't trigger.")
  public InvItem[] balanceOutput = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "Time of the day before which the goal can't be taken, in ticks (6000 for noon for example).")
  public int minimumHour = -1;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "Time of the day after which the goal can't be taken, in ticks (6000 for noon for example).")
  public int maximumHour = -1;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN)
  @FieldDocumentation(explanation = "Whether this goal should get displayed in a villager's travel book sheet. True or false by default depending on the goal type.")
  public boolean travelBookShow = true;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Name of a good whose icon represents this goal.")
  protected InvItem icon = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Name of a good whose icon will float above the villager's head when performing this goal. Use only for goals significant for the player.")
  protected InvItem floatingIcon = null;
  
  public int actionDuration(MillVillager villager) throws Exception {
    return 10;
  }
  
  public boolean allowRandomMoves() throws Exception {
    return false;
  }
  
  public boolean autoInterruptIfNoTarget() {
    return true;
  }
  
  public boolean canBeDoneAtNight() {
    return false;
  }
  
  public boolean canBeDoneInDayTime() {
    return true;
  }
  
  public String gameName() {
    return LanguageUtilities.string("goal." + labelKey(null));
  }
  
  public String gameName(MillVillager villager) {
    if (villager != null && getCurrentGoalTarget(villager) != null && getCurrentGoalTarget(villager).horizontalDistanceTo((Entity)villager) > range(villager))
      return LanguageUtilities.string("goal." + labelKeyWhileTravelling(villager)); 
    return LanguageUtilities.string("goal." + labelKey(villager));
  }
  
  public Point getCurrentGoalTarget(MillVillager villager) {
    if (villager.getGoalDestEntity() != null)
      return new Point(villager.getGoalDestEntity()); 
    return villager.getGoalDestPoint();
  }
  
  public abstract GoalInformation getDestination(MillVillager paramMillVillager) throws Exception;
  
  public ItemStack getFloatingIcon() {
    if (this.floatingIcon == null)
      return null; 
    return this.floatingIcon.getItemStack();
  }
  
  public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
    return getHeldItemsTravelling(villager);
  }
  
  public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) throws Exception {
    return getHeldItemsOffHandTravelling(villager);
  }
  
  public ItemStack[] getHeldItemsOffHandTravelling(MillVillager villager) throws Exception {
    return null;
  }
  
  public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
    return null;
  }
  
  public ItemStack getIcon() {
    if (this.icon == null)
      return null; 
    return this.icon.getItemStack();
  }
  
  public AStarConfig getPathingConfig(MillVillager villager) {
    return villager.getVillagerPathingConfig();
  }
  
  public boolean isFightingGoal() {
    return false;
  }
  
  public final boolean isPossible(MillVillager villager) {
    try {
      if (villager.world.isDaytime() && !canBeDoneInDayTime())
        return false; 
      if (!villager.world.isDaytime() && !canBeDoneAtNight())
        return false; 
      if (this.minimumHour >= 0 && villager.world.getWorldTime() % 24000L < this.minimumHour)
        return false; 
      if (this.maximumHour >= 0 && villager.world.getWorldTime() % 24000L > this.maximumHour)
        return false; 
      for (InvItem item : this.townhallLimit.keySet()) {
        if (villager.getTownHall().nbGoodAvailable(item, false, false, false) > ((Integer)this.townhallLimit.get(item)).intValue())
          return false; 
      } 
      for (InvItem item : this.villageLimit.keySet()) {
        if (villager.getTownHall().nbGoodAvailable(item, false, false, false) > ((Integer)this.villageLimit.get(item)).intValue())
          return false; 
      } 
      if (this.balanceOutput != null && this.balanceOutput.length > 1 && 
        villager.getTownHall().nbGoodAvailable(InvItem.createInvItem((this.balanceOutput[0]).item, (this.balanceOutput[0]).meta), false, false, false) < villager.getTownHall()
        .nbGoodAvailable(InvItem.createInvItem((this.balanceOutput[1]).item, (this.balanceOutput[1]).meta), false, false, false))
        return false; 
      if (this.maxSimultaneousTotal > 0) {
        int nbSame = 0;
        for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
          if (v != villager && this.key.equals(v.goalKey))
            nbSame++; 
        } 
        if (nbSame >= this.maxSimultaneousTotal)
          return false; 
      } 
      return isPossibleSpecific(villager);
    } catch (Exception e) {
      MillLog.printException("Exception in isPossible() for goal: " + this.key + " and villager: " + villager, e);
      return false;
    } 
  }
  
  protected boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return true;
  }
  
  public final boolean isStillValid(MillVillager villager) throws Exception {
    if (villager.world.isDaytime() && !canBeDoneInDayTime())
      return false; 
    if (!villager.world.isDaytime() && !canBeDoneAtNight())
      return false; 
    if (this.leasure)
      for (Goal g : villager.getGoals()) {
        if (!g.leasure && g.isPossible(villager))
          return false; 
      }  
    if (villager.getGoalDestPoint() == null && villager.getGoalDestEntity() == null && autoInterruptIfNoTarget())
      return false; 
    return isStillValidSpecific(villager);
  }
  
  protected boolean isStillValidSpecific(MillVillager villager) throws Exception {
    return true;
  }
  
  public String labelKey(MillVillager villager) {
    return this.key;
  }
  
  public String labelKeyWhileTravelling(MillVillager villager) {
    return this.key;
  }
  
  public boolean lookAtGoal() {
    return false;
  }
  
  public boolean lookAtPlayer() {
    return false;
  }
  
  public String nextGoal(MillVillager villager) throws Exception {
    return null;
  }
  
  public void onAccept(MillVillager villager) throws Exception {}
  
  public void onComplete(MillVillager villager) throws Exception {}
  
  protected GoalInformation packDest(Point p) {
    return new GoalInformation(p, null, null);
  }
  
  protected GoalInformation packDest(Point p, Building b) {
    return new GoalInformation(p, b.getPos(), null);
  }
  
  protected GoalInformation packDest(Point p, Building b, Entity ent) {
    if (b == null)
      return new GoalInformation(p, null, ent); 
    return new GoalInformation(p, b.getPos(), ent);
  }
  
  protected GoalInformation packDest(Point p, Point p2) {
    return new GoalInformation(p, p2, null);
  }
  
  public abstract boolean performAction(MillVillager paramMillVillager) throws Exception;
  
  public abstract int priority(MillVillager paramMillVillager) throws Exception;
  
  public int range(MillVillager villager) {
    return 3;
  }
  
  public String sentenceKey() {
    return this.key;
  }
  
  public void setVillagerDest(MillVillager villager) throws Exception {
    villager.setGoalInformation(getDestination(villager));
  }
  
  public boolean shouldVillagerLieDown() {
    return false;
  }
  
  public boolean stopMovingWhileWorking() {
    return true;
  }
  
  public boolean stuckAction(MillVillager villager) throws Exception {
    return false;
  }
  
  public long stuckDelay(MillVillager villager) {
    return 200L;
  }
  
  public boolean swingArms() {
    return false;
  }
  
  public boolean swingArms(MillVillager villager) {
    if (villager != null && getCurrentGoalTarget(villager) != null && getCurrentGoalTarget(villager).horizontalDistanceTo((Entity)villager) > range(villager))
      return swingArmsWhileTravelling(); 
    return swingArms();
  }
  
  public boolean swingArmsWhileTravelling() {
    return false;
  }
  
  public String toString() {
    return "goal:" + this.key;
  }
  
  public boolean unreachableDestination(MillVillager villager) throws Exception {
    if (villager.getGoalDestPoint() == null && villager.getGoalDestEntity() == null)
      return false; 
    int[] jumpTo = MillCommonUtilities.getJumpDestination(villager.world, villager.getPathDestPoint().getiX(), villager
        .getTownHall().getAltitude(villager.getPathDestPoint().getiX(), villager.getPathDestPoint().getiZ()), villager.getPathDestPoint().getiZ());
    if (jumpTo != null) {
      if (MillConfigValues.LogPathing >= 2 && villager.extraLog)
        MillLog.minor(this, "Dest unreachable. Jumping " + villager + " from " + villager.getPos() + " to " + jumpTo[0] + "/" + jumpTo[1] + "/" + jumpTo[2]); 
      villager.setPosition(jumpTo[0] + 0.5D, jumpTo[1] + 0.5D, jumpTo[2] + 0.5D);
      return true;
    } 
    if (MillConfigValues.LogPathing >= 2 && villager.extraLog)
      MillLog.minor(this, "Dest unreachable. Couldn't jump " + villager + " from " + villager.getPos() + " to " + villager.getPathDestPoint()); 
    return false;
  }
  
  public boolean validateDest(MillVillager villager, Building dest) throws MillLog.MillenaireException {
    if (dest == null)
      throw new MillLog.MillenaireException("Given null dest in validateDest for goal: " + this.key); 
    for (InvItem item : this.buildingLimit.keySet()) {
      if (dest.nbGoodAvailable(item, false, false, false) > ((Integer)this.buildingLimit.get(item)).intValue())
        return false; 
    } 
    int nbSameBuilding = 0;
    if (this.maxSimultaneousInBuilding > 0) {
      for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
        if (v != villager && this.key.equals(v.goalKey) && 
          v.getGoalBuildingDest() == dest)
          nbSameBuilding++; 
      } 
      if (nbSameBuilding >= this.maxSimultaneousInBuilding)
        return false; 
    } 
    return true;
  }
}
