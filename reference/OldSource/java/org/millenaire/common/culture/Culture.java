package org.millenaire.common.culture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ProgressManager;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;

public class Culture {
  public static final int LANGUAGE_FLUENT = 500;
  
  public static final int LANGUAGE_MODERATE = 200;
  
  public static final int LANGUAGE_BEGINNER = 100;
  
  private static final String PLAYERBUILDING = "playerbuilding";
  
  private static final String TOWNHALL = "townhall";
  
  private static final String HOUSE = "house";
  
  private static final String OTHERVILLAGE = "othervillage";
  
  private static final String LONEBUILDING = "lonebuilding";
  
  private static final String MARVEL = "marvel";
  
  private static final String WALL = "wall";
  
  private static final String VILLAGER = "villager";
  
  private static final String LONEVILLAGER = "lonevillager";
  
  private static final String VISITOR = "visitor";
  
  private static final String LEADER = "leader";
  
  private static final String MARVELVILLAGER = "marvelvillager";
  
  public static List<Culture> ListCultures = new ArrayList<>();
  
  private static HashMap<String, Culture> cultures = new HashMap<>();
  
  private static HashMap<String, Culture> serverCultures = new HashMap<>();
  
  public static Culture getCultureByName(String name) {
    if (cultures.containsKey(name))
      return cultures.get(name); 
    if (serverCultures.containsKey(name))
      return serverCultures.get(name); 
    if (Mill.isDistantClient()) {
      Culture culture = new Culture(name);
      serverCultures.put(name, culture);
      return culture;
    } 
    return null;
  }
  
  public static boolean loadCultures(ProgressManager.ProgressBar bar) {
    VirtualDir cultureVirtualDir = Mill.virtualLoadingDir.getChildDirectory("cultures");
    for (VirtualDir cultureDir : cultureVirtualDir.listSubDirs()) {
      if (MillConfigValues.LogCulture >= 1)
        MillLog.major(cultureDir, "Loading culture: " + cultureDir.getName()); 
      bar.step("Culture - " + cultureDir.getName());
      Culture culture = new Culture(cultureDir.getName());
      culture.load(cultureDir);
      culture.makeBannerItem();
      cultures.put(culture.key, culture);
      ListCultures.add(culture);
    } 
    if (MillConfigValues.LogCulture >= 1)
      MillLog.major(null, "Finished loading cultures."); 
    return false;
  }
  
  public static void readCultureMissingContentPacket(PacketBuffer data) {
    try {
      String key = data.readString(2048);
      Culture culture = getCultureByName(key);
      CultureLanguage main = new CultureLanguage(culture, MillConfigValues.effective_language, true);
      CultureLanguage fallback = new CultureLanguage(culture, MillConfigValues.fallback_language, true);
      culture.mainLanguageServer = main;
      culture.fallbackLanguageServer = fallback;
      String playerName = Mill.proxy.getTheSinglePlayer().getName();
      CultureLanguage[] langs = { main, fallback };
      for (CultureLanguage lang : langs) {
        HashMap<String, String> strings = StreamReadWrite.readStringStringMap(data);
        for (String k : strings.keySet()) {
          if (!lang.strings.containsKey(k))
            lang.strings.put(k, ((String)strings.get(k)).replaceAll("\\$name", playerName)); 
        } 
        strings = StreamReadWrite.readStringStringMap(data);
        for (String k : strings.keySet()) {
          if (!lang.buildingNames.containsKey(k))
            lang.buildingNames.put(k, ((String)strings.get(k)).replaceAll("\\$name", playerName)); 
        } 
        HashMap<String, List<String>> sentences = StreamReadWrite.readStringStringListMap(data);
        for (String k : sentences.keySet()) {
          if (!lang.sentences.containsKey(k)) {
            List<String> v = new ArrayList<>();
            for (String s : sentences.get(k))
              v.add(s.replaceAll("\\$name", playerName)); 
            lang.sentences.put(k, v);
          } 
        } 
      } 
      int nb = data.readShort();
      int i;
      for (i = 0; i < nb; i++) {
        key = data.readString(2048);
        BuildingPlanSet set = culture.getBuildingPlanSet(key);
        set.readBuildingPlanSetInfoPacket(data);
      } 
      nb = data.readShort();
      for (i = 0; i < nb; i++) {
        key = data.readString(2048);
        VillagerType vtype = culture.getVillagerType(key);
        vtype.readVillagerTypeInfoPacket(data);
      } 
      nb = data.readShort();
      for (i = 0; i < nb; i++) {
        key = data.readString(2048);
        VillageType vtype = culture.getVillageType(key);
        vtype.readVillageTypeInfoPacket(data);
      } 
      nb = data.readShort();
      for (i = 0; i < nb; i++) {
        key = data.readString(2048);
        VillageType vtype = culture.getLoneBuildingType(key);
        vtype.readVillageTypeInfoPacket(data);
      } 
    } catch (IOException e) {
      MillLog.printException("Error in readCultureInfoPacket: ", e);
    } 
  }
  
  public static void refreshLists() {
    ListCultures.clear();
    for (String k : cultures.keySet()) {
      Culture c = cultures.get(k);
      ListCultures.add(c);
    } 
    for (String k : serverCultures.keySet()) {
      Culture c = serverCultures.get(k);
      ListCultures.add(c);
    } 
    for (Culture c : ListCultures) {
      c.ListPlanSets.clear();
      for (String key : c.planSet.keySet())
        c.ListPlanSets.add(c.planSet.get(key)); 
      for (String key : c.serverPlanSet.keySet())
        c.ListPlanSets.add(c.serverPlanSet.get(key)); 
      c.listVillagerTypes.clear();
      for (String key : c.villagerTypes.keySet())
        c.listVillagerTypes.add(c.villagerTypes.get(key)); 
      for (String key : c.serverVillagerTypes.keySet())
        c.listVillagerTypes.add(c.serverVillagerTypes.get(key)); 
      c.listVillageTypes.clear();
      for (String key : c.villageTypes.keySet())
        c.listVillageTypes.add(c.villageTypes.get(key)); 
      for (String key : c.serverVillageTypes.keySet())
        c.listVillageTypes.add(c.serverVillageTypes.get(key)); 
      c.listLoneBuildingTypes.clear();
      for (String key : c.loneBuildingTypes.keySet())
        c.listLoneBuildingTypes.add(c.loneBuildingTypes.get(key)); 
      for (String key : c.serverLoneBuildingTypes.keySet())
        c.listLoneBuildingTypes.add(c.serverLoneBuildingTypes.get(key)); 
    } 
  }
  
  public static void removeServerContent() {
    serverCultures.clear();
    for (String k : cultures.keySet()) {
      Culture c = cultures.get(k);
      c.serverPlanSet.clear();
      c.serverVillageTypes.clear();
      c.serverVillagerTypes.clear();
      c.serverLoneBuildingTypes.clear();
      c.mainLanguageServer = null;
      c.fallbackLanguageServer = null;
    } 
    refreshLists();
  }
  
  public final Set<String> missingBuildingNames = new HashSet<>();
  
  private CultureLanguage mainLanguage;
  
  private CultureLanguage fallbackLanguage;
  
  private CultureLanguage mainLanguageServer;
  
  private CultureLanguage fallbackLanguageServer;
  
  final HashMap<String, CultureLanguage> loadedLanguages = new HashMap<>();
  
  public String key;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING, defaultValue = " ")
  @FieldDocumentation(explanation = "Separator between a village's name and its qualifier.")
  public String qualifierSeparator = " ";
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Name of a good whose icon represents this culture.")
  private final InvItem icon = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "knownCrop")
  @FieldDocumentation(explanation = "A crop know to the culture, that can be taught to the player.")
  public List<String> knownCrops = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "knownHuntingDrop")
  @FieldDocumentation(explanation = "A hunting drop know to the culture, that can be taught to the player.")
  public List<String> knownHuntingDrops = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "travelBookVillagerCategory")
  @FieldDocumentation(explanation = "A category of villagers for the Travel Book.")
  public List<String> travelBookVillagerCategories = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "travelBookBuildingCategory")
  @FieldDocumentation(explanation = "A category of buildings for the Travel Book.")
  public List<String> travelBookBuildingCategories = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "travelBookTradeGoodCategory")
  @FieldDocumentation(explanation = "A category of trade goods for the Travel Book.")
  public List<String> travelBookTradeGoodCategories = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_INVITEM_ADD, paramName = "travelBookCategoryIcon")
  @FieldDocumentation(explanation = "The icon to use for this Travel Book category.")
  private final Map<String, InvItem> travelBookCategoriesIcons = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING, defaultValue = "")
  @FieldDocumentation(explanation = "A JSON object that specifies the culture's banner's appearance.")
  public String cultureBanner = "";
  
  @ConfigField(type = AnnotedParameter.ParameterType.RESOURCE_LOCATION, defaultValue = "millenaire:textures/entity/panels/default.png")
  @FieldDocumentation(explanation = "A resource path to a panel texture.")
  public ResourceLocation panelTexture = null;
  
  public ItemStack cultureBannerItemStack;
  
  Map<String, BuildingPlanSet> planSet = new HashMap<>();
  
  private Map<String, BuildingCustomPlan> customBuildings = new HashMap<>();
  
  private final Map<String, BuildingPlanSet> serverPlanSet = new HashMap<>();
  
  private final Map<String, BuildingCustomPlan> serverCustomBuildings = new HashMap<>();
  
  public List<BuildingPlanSet> ListPlanSets = new ArrayList<>();
  
  private final Map<String, VillageType> villageTypes = new HashMap<>();
  
  private final Map<String, VillageType> serverVillageTypes = new HashMap<>();
  
  public List<VillageType> listVillageTypes = new ArrayList<>();
  
  public Map<String, WallType> wallTypes = new HashMap<>();
  
  private final Map<String, VillageType> loneBuildingTypes = new HashMap<>();
  
  private final Map<String, VillageType> serverLoneBuildingTypes = new HashMap<>();
  
  public List<VillageType> listLoneBuildingTypes = new ArrayList<>();
  
  public final Map<String, VillagerType> villagerTypes = new HashMap<>();
  
  private final Map<String, VillagerType> serverVillagerTypes = new HashMap<>();
  
  public List<VillagerType> listVillagerTypes = new ArrayList<>();
  
  private final Map<String, List<String>> nameLists = new HashMap<>();
  
  public Map<String, List<TradeGood>> shopSells = new HashMap<>();
  
  public Map<String, List<TradeGood>> shopBuys = new HashMap<>();
  
  public Map<String, List<TradeGood>> shopBuysOptional = new HashMap<>();
  
  public Map<String, List<InvItem>> shopNeeds = new HashMap<>();
  
  public List<TradeGood> goodsList = new ArrayList<>();
  
  private final Map<String, TradeGood> tradeGoods = new HashMap<>();
  
  private final Map<InvItem, TradeGood> goodsByItem = new HashMap<>();
  
  public Culture(String s) {
    this.key = s;
  }
  
  public boolean canReadBuildingNames() {
    if (Mill.proxy.getClientProfile() == null)
      return true; 
    return (!MillConfigValues.languageLearning || Mill.proxy.getClientProfile().getCultureLanguageKnowledge(this.key) >= 100);
  }
  
  public boolean canReadDialogues(String username) {
    if (Mill.proxy.getClientProfile() == null)
      return true; 
    return (!MillConfigValues.languageLearning || Mill.proxy.getClientProfile().getCultureLanguageKnowledge(this.key) >= 500);
  }
  
  public boolean canReadVillagerNames() {
    if (Mill.proxy.getClientProfile() == null)
      return true; 
    return (!MillConfigValues.languageLearning || Mill.proxy.getClientProfile().getCultureLanguageKnowledge(this.key) >= 200);
  }
  
  private void checkGoodsList() {
    for (TradeGood good : this.goodsList)
      good.validateGood(); 
  }
  
  public int[] compareCultureLanguages(String main, String ref, BufferedWriter writer) throws Exception {
    CultureLanguage maincl = null, refcl = null;
    if (this.loadedLanguages.containsKey(main))
      maincl = this.loadedLanguages.get(main); 
    if (this.loadedLanguages.containsKey(ref))
      refcl = this.loadedLanguages.get(ref); 
    if (refcl == null)
      return new int[] { 0, 0 }; 
    if (maincl == null) {
      writer.write("Data for culture " + this.key + " is missing." + "\n" + "\n");
      return new int[] { 0, refcl.buildingNames.size() + refcl.reputationLevels.size() + refcl.sentences.size() + refcl.strings.size() };
    } 
    return maincl.compareWithLanguage(refcl, writer);
  }
  
  public String getAdjectiveTranslated() {
    return getCultureString("culture." + this.key);
  }
  
  public String getAdjectiveTranslatedKey() {
    return "culture:" + this.key + ":culture." + this.key;
  }
  
  public List<Goal> getAllUsedGoals() {
    List<Goal> goals = new ArrayList<>();
    for (Goal goal : Goal.goals.values()) {
      boolean found = false;
      for (VillagerType villagerType : this.villagerTypes.values()) {
        if (villagerType.goals.contains(goal)) {
          found = true;
          break;
        } 
      } 
      if (found)
        goals.add(goal); 
    } 
    return goals;
  }
  
  public BuildingCustomPlan getBuildingCustom(String key) {
    if (this.customBuildings.containsKey(key))
      return this.customBuildings.get(key); 
    if (this.serverCustomBuildings.containsKey(key))
      return this.serverCustomBuildings.get(key); 
    if (Mill.isDistantClient()) {
      BuildingCustomPlan set = new BuildingCustomPlan(this, key);
      this.serverCustomBuildings.put(key, set);
      return set;
    } 
    return null;
  }
  
  public String getBuildingGameName(BuildingPlan plan) {
    String planNameLC = plan.planName.toLowerCase();
    if (this.mainLanguage != null && this.mainLanguage.buildingNames.containsKey(planNameLC))
      return this.mainLanguage.buildingNames.get(planNameLC); 
    if (this.mainLanguageServer != null && this.mainLanguageServer.buildingNames.containsKey(planNameLC))
      return this.mainLanguageServer.buildingNames.get(planNameLC); 
    if (this.fallbackLanguage != null && this.fallbackLanguage.buildingNames.containsKey(planNameLC))
      return this.fallbackLanguage.buildingNames.get(planNameLC); 
    if (this.fallbackLanguageServer != null && this.fallbackLanguageServer.buildingNames.containsKey(planNameLC))
      return this.fallbackLanguageServer.buildingNames.get(planNameLC); 
    BuildingPlan previousLevelPlan = plan.getPreviousBuildingPlan();
    if (previousLevelPlan != null)
      return getBuildingGameName(previousLevelPlan); 
    if (plan.parent != null)
      return getBuildingGameName(plan.parent); 
    return null;
  }
  
  public BuildingPlan getBuildingPlan(String planKey) {
    String suffix = planKey.split("_")[(planKey.split("_")).length - 1];
    String buildingKey = planKey.substring(0, planKey.length() - suffix.length() - 1);
    BuildingPlanSet set = getBuildingPlanSet(buildingKey);
    if (set == null)
      return null; 
    int variation = suffix.toUpperCase().charAt(0) - 65;
    int level = Integer.parseInt(suffix.substring(1, suffix.length()));
    return set.getPlan(variation, level);
  }
  
  public BuildingPlanSet getBuildingPlanSet(String key) {
    if (this.planSet.containsKey(key))
      return this.planSet.get(key); 
    if (this.serverPlanSet.containsKey(key))
      return this.serverPlanSet.get(key); 
    if (Mill.isDistantClient()) {
      BuildingPlanSet set = new BuildingPlanSet(this, key, null, null);
      this.serverPlanSet.put(key, set);
      return set;
    } 
    return null;
  }
  
  public ItemStack getCategoryIcon(String category) {
    if (this.travelBookCategoriesIcons.containsKey(category))
      return ((InvItem)this.travelBookCategoriesIcons.get(category)).getItemStack(); 
    return null;
  }
  
  public String getCategoryName(String category) {
    if (hasCultureString("travelbook_category." + category))
      return getCultureString("travelbook_category." + category); 
    return LanguageUtilities.string("travelbook_category." + category);
  }
  
  public int getChoiceWeight() {
    return 10;
  }
  
  public String getCultureString(String key) {
    key = key.toLowerCase();
    if (this.mainLanguage != null && this.mainLanguage.strings.containsKey(key))
      return this.mainLanguage.strings.get(key); 
    if (LanguageUtilities.getRawStringMainOnly(key, false) != null)
      return LanguageUtilities.getRawStringMainOnly(key, false); 
    if (this.mainLanguageServer != null && this.mainLanguageServer.strings.containsKey(key))
      return this.mainLanguageServer.strings.get(key); 
    if (this.fallbackLanguage != null && this.fallbackLanguage.strings.containsKey(key))
      return this.fallbackLanguage.strings.get(key); 
    if (LanguageUtilities.getRawStringFallbackOnly(key, false) != null)
      return LanguageUtilities.getRawStringFallbackOnly(key, false); 
    if (this.fallbackLanguageServer != null && this.fallbackLanguageServer.strings.containsKey(key))
      return this.fallbackLanguageServer.strings.get(key); 
    if (MillConfigValues.DEV && MillConfigValues.LogTranslation >= 1) {
      MillLog.temp(this, "Reloading strings because of missing key:" + key);
      this.loadedLanguages.clear();
      loadLanguages(LanguageUtilities.getLanguageDirs(), MillConfigValues.effective_language, MillConfigValues.fallback_language);
    } 
    return key;
  }
  
  public String getCustomBuildingGameName(BuildingCustomPlan customBuilding) {
    String planNameLC;
    if (customBuilding.gameNameKey != null) {
      planNameLC = customBuilding.gameNameKey.toLowerCase();
    } else {
      planNameLC = customBuilding.buildingKey.toLowerCase();
    } 
    if (this.mainLanguage != null && this.mainLanguage.buildingNames.containsKey(planNameLC))
      return this.mainLanguage.buildingNames.get(planNameLC); 
    if (this.mainLanguageServer != null && this.mainLanguageServer.buildingNames.containsKey(planNameLC))
      return this.mainLanguageServer.buildingNames.get(planNameLC); 
    if (this.fallbackLanguage != null && this.fallbackLanguage.buildingNames.containsKey(planNameLC))
      return this.fallbackLanguage.buildingNames.get(planNameLC); 
    if (this.fallbackLanguageServer != null && this.fallbackLanguageServer.buildingNames.containsKey(planNameLC))
      return this.fallbackLanguageServer.buildingNames.get(planNameLC); 
    if (MillConfigValues.LogTranslation >= 1 || MillConfigValues.generateTranslationGap)
      MillLog.major(this, "Could not find the custom building name for :" + customBuilding.buildingKey); 
    return null;
  }
  
  public CultureLanguage.Dialogue getDialogue(String key) {
    if (this.mainLanguage.dialogues.containsKey(key))
      return this.mainLanguage.dialogues.get(key); 
    if (this.mainLanguageServer != null && this.mainLanguageServer.dialogues.containsKey(key))
      return this.mainLanguageServer.dialogues.get(key); 
    if (this.fallbackLanguage != null && this.fallbackLanguage.dialogues.containsKey(key))
      return this.fallbackLanguage.dialogues.get(key); 
    if (this.fallbackLanguageServer != null && this.fallbackLanguageServer.dialogues.containsKey(key))
      return this.fallbackLanguageServer.dialogues.get(key); 
    return null;
  }
  
  public ItemStack getIcon() {
    if (this.icon != null)
      return this.icon.getItemStack(); 
    return null;
  }
  
  public Set<InvItem> getInvItemsWithTradeGoods() {
    return this.goodsByItem.keySet();
  }
  
  public String getLanguageLevelString() {
    if (Mill.proxy.getClientProfile() == null)
      return LanguageUtilities.string("culturelanguage.minimal"); 
    if (Mill.proxy.getClientProfile().getCultureLanguageKnowledge(this.key) >= 500)
      return LanguageUtilities.string("culturelanguage.fluent"); 
    if (Mill.proxy.getClientProfile().getCultureLanguageKnowledge(this.key) >= 200)
      return LanguageUtilities.string("culturelanguage.moderate"); 
    if (Mill.proxy.getClientProfile().getCultureLanguageKnowledge(this.key) >= 100)
      return LanguageUtilities.string("culturelanguage.beginner"); 
    return LanguageUtilities.string("culturelanguage.minimal");
  }
  
  public int getLocalPlayerReputation() {
    if (Mill.proxy.getClientProfile() == null)
      return 0; 
    return Mill.proxy.getClientProfile().getCultureReputation(this.key);
  }
  
  public String getLocalPlayerReputationString() {
    if (Mill.proxy.getClientProfile() == null)
      return LanguageUtilities.string("culturereputation.neutral"); 
    int reputation = Mill.proxy.getClientProfile().getCultureReputation(this.key);
    if (reputation < 0) {
      if (reputation <= -640)
        return LanguageUtilities.string("culturereputation.scourgeofgod"); 
      if (reputation < -128)
        return LanguageUtilities.string("culturereputation.dreadful"); 
      return LanguageUtilities.string("culturereputation.bad");
    } 
    if (reputation > 2048)
      return LanguageUtilities.string("culturereputation.stellar"); 
    if (reputation > 1024)
      return LanguageUtilities.string("culturereputation.excellent"); 
    if (reputation > 512)
      return LanguageUtilities.string("culturereputation.good"); 
    if (reputation > 256)
      return LanguageUtilities.string("culturereputation.decent"); 
    return LanguageUtilities.string("culturereputation.neutral");
  }
  
  public VillageType getLoneBuildingType(String key) {
    if (this.loneBuildingTypes.containsKey(key))
      return this.loneBuildingTypes.get(key); 
    if (this.serverLoneBuildingTypes.containsKey(key))
      return this.serverLoneBuildingTypes.get(key); 
    if (Mill.isDistantClient()) {
      VillageType vtype = new VillageType(this, key, false);
      this.serverLoneBuildingTypes.put(key, vtype);
      return vtype;
    } 
    return null;
  }
  
  public String getNameTranslated() {
    return getCultureString("culture.fullname");
  }
  
  public VillagerType getRandomForeignMerchant() {
    List<VillagerType> foreignMerchants = new ArrayList<>();
    for (VillagerType v : this.listVillagerTypes) {
      if (v.isForeignMerchant)
        foreignMerchants.add(v); 
    } 
    if (foreignMerchants.size() == 0)
      return null; 
    return (VillagerType)MillCommonUtilities.getWeightedChoice(foreignMerchants, null);
  }
  
  public String getRandomNameFromList(String listName) {
    List<String> list = this.nameLists.get(listName);
    if (list == null) {
      MillLog.error(this, "Could not find name list: " + listName);
      return null;
    } 
    return list.get(MillCommonUtilities.randomInt(list.size()));
  }
  
  public String getRandomNameFromList(String listName, Set<String> namesTaken) {
    List<String> list = this.nameLists.get(listName);
    if (list == null) {
      MillLog.error(this, "Could not find name list: " + listName);
      return null;
    } 
    list = new ArrayList<>(list);
    list.removeAll(namesTaken);
    if (list.isEmpty()) {
      MillLog.warning(this, "Name list " + listName + " is empty after removing " + namesTaken.size() + ". Provide a bigger list!");
      return getRandomNameFromList(listName);
    } 
    return list.get(MillCommonUtilities.randomInt(list.size()));
  }
  
  public VillageType getRandomVillage() {
    return (VillageType)MillCommonUtilities.getWeightedChoice(this.listVillageTypes, null);
  }
  
  public CultureLanguage.ReputationLevel getReputationLevel(int reputation) {
    CultureLanguage.ReputationLevel rlevel = null;
    if (this.mainLanguage != null)
      rlevel = this.mainLanguage.getReputationLevel(reputation); 
    if (rlevel != null)
      return rlevel; 
    if (this.fallbackLanguage != null)
      return this.fallbackLanguage.getReputationLevel(reputation); 
    return null;
  }
  
  public String getReputationLevelDesc(int reputation) {
    CultureLanguage.ReputationLevel rlevel = getReputationLevel(reputation);
    if (rlevel != null)
      return rlevel.desc; 
    return "";
  }
  
  public String getReputationLevelLabel(int reputation) {
    CultureLanguage.ReputationLevel rlevel = getReputationLevel(reputation);
    if (rlevel != null)
      return rlevel.label; 
    return "";
  }
  
  public List<String> getSentences(String key) {
    if (this.mainLanguage != null && this.mainLanguage.sentences.containsKey(key))
      return this.mainLanguage.sentences.get(key); 
    if (this.mainLanguageServer != null && this.mainLanguageServer.sentences.containsKey(key))
      return this.mainLanguageServer.sentences.get(key); 
    if (this.fallbackLanguage != null && this.fallbackLanguage.sentences.containsKey(key))
      return this.fallbackLanguage.sentences.get(key); 
    if (this.fallbackLanguageServer != null && this.fallbackLanguageServer.sentences.containsKey(key))
      return this.fallbackLanguageServer.sentences.get(key); 
    return null;
  }
  
  public TradeGood getTradeGood(InvItem invItem) {
    return this.goodsByItem.get(invItem);
  }
  
  public TradeGood getTradeGood(String key) {
    return this.tradeGoods.get(key);
  }
  
  public VillagerType getVillagerType(String key) {
    if (this.villagerTypes.containsKey(key))
      return this.villagerTypes.get(key); 
    if (this.serverVillagerTypes.containsKey(key))
      return this.serverVillagerTypes.get(key); 
    if (Mill.isDistantClient()) {
      VillagerType vtype = new VillagerType(this, key);
      this.serverVillagerTypes.put(key, vtype);
      return vtype;
    } 
    MillLog.error(this, "Could not find villager type: " + key);
    return null;
  }
  
  public VillageType getVillageType(String key) {
    if (this.villageTypes.containsKey(key))
      return this.villageTypes.get(key); 
    if (this.serverVillageTypes.containsKey(key))
      return this.serverVillageTypes.get(key); 
    if (this.loneBuildingTypes.containsKey(key))
      return this.loneBuildingTypes.get(key); 
    if (this.serverLoneBuildingTypes.containsKey(key))
      return this.serverLoneBuildingTypes.get(key); 
    if (Mill.isDistantClient()) {
      VillageType vtype = new VillageType(this, key, false);
      this.serverVillageTypes.put(key, vtype);
      return vtype;
    } 
    return null;
  }
  
  public boolean hasCultureString(String key) {
    key = key.toLowerCase();
    if (this.mainLanguage != null && this.mainLanguage.strings.containsKey(key))
      return true; 
    if (LanguageUtilities.getRawStringMainOnly(key, false) != null)
      return true; 
    if (this.mainLanguageServer != null && this.mainLanguageServer.strings.containsKey(key))
      return true; 
    if (this.fallbackLanguage != null && this.fallbackLanguage.strings.containsKey(key))
      return true; 
    if (LanguageUtilities.getRawStringFallbackOnly(key, false) != null)
      return true; 
    if (this.fallbackLanguageServer != null && this.fallbackLanguageServer.strings.containsKey(key))
      return true; 
    if (MillConfigValues.DEV && MillConfigValues.LogTranslation >= 1) {
      MillLog.temp(this, "Reloading strings because of missing key:" + key);
      this.loadedLanguages.clear();
      loadLanguages(LanguageUtilities.getLanguageDirs(), MillConfigValues.effective_language, MillConfigValues.fallback_language);
    } 
    return false;
  }
  
  public boolean hasSentences(String key) {
    return (getSentences(key) != null);
  }
  
  public boolean load(VirtualDir cultureVirtualDir) {
    try {
      readConfig(cultureVirtualDir);
      loadNameLists(cultureVirtualDir);
      loadGoods(cultureVirtualDir);
      loadShops(cultureVirtualDir);
      loadVillagerTypes(cultureVirtualDir);
      long startTime = System.currentTimeMillis();
      this.planSet = BuildingPlan.loadPlans(cultureVirtualDir, this);
      MillLog.temp(this, "Loaded plans in " + (System.currentTimeMillis() - startTime) + " ms.");
      if (this.planSet == null)
        return false; 
      this.customBuildings = BuildingCustomPlan.loadCustomBuildings(cultureVirtualDir, this);
      if (this.customBuildings == null)
        return false; 
      this.ListPlanSets.addAll(this.planSet.values());
      if (MillConfigValues.LogBuildingPlan >= 1)
        for (BuildingPlanSet set : this.ListPlanSets)
          MillLog.major(set, "Loaded plan set: " + set.key);  
      this.wallTypes = WallType.loadWalls(cultureVirtualDir, this);
      this.listVillageTypes = VillageType.loadVillages(cultureVirtualDir, this);
      if (this.listVillageTypes == null)
        return false; 
      for (VillageType v : this.listVillageTypes)
        this.villageTypes.put(v.key, v); 
      this.listLoneBuildingTypes = VillageType.loadLoneBuildings(cultureVirtualDir, this);
      for (VillageType v : this.listLoneBuildingTypes)
        this.loneBuildingTypes.put(v.key, v); 
      validateTradeGoods();
      setTravelBookDefaults();
      if (MillConfigValues.LogCulture >= 1)
        MillLog.major(this, "Finished loading culture."); 
      return true;
    } catch (Exception e) {
      MillLog.printException("Error when loading culture: ", e);
      return false;
    } 
  }
  
  private void loadGoods(VirtualDir cultureVirtualDir) {
    for (File file : cultureVirtualDir.getAllChildFiles("traded_goods.txt")) {
      try {
        if (!file.exists())
          file.createNewFile(); 
        BufferedReader reader = MillCommonUtilities.getReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.trim().length() > 0 && !line.startsWith("//"))
            try {
              String[] values = line.split(",");
              String key = values[0].toLowerCase();
              if (InvItem.INVITEMS_BY_NAME.containsKey(key)) {
                InvItem item = (InvItem)InvItem.INVITEMS_BY_NAME.get(key);
                if (item.item == Items.AIR)
                  MillLog.error(item, "Attempted to load a good with a null item: " + key); 
                int sellingPrice = (values.length > 1 && !values[1].isEmpty()) ? MillCommonUtilities.readInteger(values[1]) : 0;
                int buyingPrice = (values.length > 2 && !values[2].isEmpty()) ? MillCommonUtilities.readInteger(values[2]) : 0;
                int reservedQuantity = (values.length > 3 && !values[3].isEmpty()) ? MillCommonUtilities.readInteger(values[3]) : 0;
                int targetQuantity = (values.length > 4 && !values[4].isEmpty()) ? MillCommonUtilities.readInteger(values[4]) : 0;
                int foreignMerchantPrice = (values.length > 5 && !values[5].isEmpty()) ? MillCommonUtilities.readInteger(values[5]) : 0;
                boolean autoGenerate = (values.length > 6 && !values[6].isEmpty()) ? Boolean.parseBoolean(values[6]) : false;
                String tag = (values.length > 7 && !values[7].isEmpty()) ? values[7] : null;
                int minReputation = (values.length > 8 && !values[8].isEmpty()) ? MillCommonUtilities.readInteger(values[8]) : Integer.MIN_VALUE;
                String desc = (values.length > 9 && !values[9].isEmpty()) ? values[9] : "foreigntrade";
                TradeGood good = new TradeGood(key, this, key, item, sellingPrice, buyingPrice, reservedQuantity, targetQuantity, foreignMerchantPrice, autoGenerate, tag, minReputation, desc);
                if (this.tradeGoods.containsKey(key) || this.goodsByItem.containsKey(good.item))
                  MillLog.error(this, "Good " + key + " is present twice in the goods list."); 
                this.tradeGoods.put(key, good);
                this.goodsByItem.put(good.item, good);
                this.goodsList.remove(good);
                this.goodsList.add(good);
                if (MillConfigValues.LogCulture >= 2)
                  MillLog.minor(this, "Loaded traded good: " + key + " prices: " + sellingPrice + "/" + buyingPrice); 
                continue;
              } 
              MillLog.error(this, "Unknown good on line: " + line);
            } catch (Exception e) {
              MillLog.printException("Exception when trying to read trade good on line: " + line, e);
            }  
        } 
        reader.close();
      } catch (Exception e) {
        MillLog.printException(e);
      } 
    } 
    checkGoodsList();
  }
  
  private CultureLanguage loadLanguage(List<File> languageDirs, String key) {
    if (this.loadedLanguages.containsKey(key))
      return this.loadedLanguages.get(key); 
    CultureLanguage lang = new CultureLanguage(this, key, false);
    List<File> languageDirsWithCusto = new ArrayList<>(languageDirs);
    File dircusto = new File(new File(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "custom cultures"), key), "languages");
    if (dircusto.exists())
      languageDirsWithCusto.add(dircusto); 
    lang.loadFromDisk(languageDirsWithCusto);
    return lang;
  }
  
  public void loadLanguages(List<File> languageDirs, String effective_language, String fallback_language) {
    this.mainLanguage = loadLanguage(languageDirs, effective_language);
    if (!effective_language.equals(fallback_language)) {
      this.fallbackLanguage = loadLanguage(languageDirs, fallback_language);
    } else {
      this.fallbackLanguage = this.mainLanguage;
    } 
    File mainDir = languageDirs.get(0);
    for (File lang : mainDir.listFiles()) {
      if (lang.isDirectory() && !lang.isHidden()) {
        String key = lang.getName().toLowerCase();
        if (!this.loadedLanguages.containsKey(key))
          loadLanguage(languageDirs, key); 
      } 
    } 
  }
  
  private void loadNameLists(VirtualDir cultureVirtualDir) {
    VirtualDir namelistsVirtualDir = cultureVirtualDir.getChildDirectory("namelists");
    try {
      for (File file : namelistsVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"))) {
        List<String> list = new ArrayList<>();
        BufferedReader reader = MillCommonUtilities.getReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
          line = line.trim();
          if (line.length() > 0)
            list.add(line); 
        } 
        this.nameLists.put(file.getName().split("\\.")[0], list);
      } 
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private void loadShop(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] temp = line.split("=");
          if (temp.length != 2) {
            MillLog.error(null, "Invalid line when loading shop " + file.getName() + ": " + line);
            continue;
          } 
          String key = temp[0].toLowerCase();
          String value = temp[1].toLowerCase();
          if (key.equals("buys")) {
            List<TradeGood> buys = new ArrayList<>();
            for (String name : value.split(",")) {
              if (this.tradeGoods.containsKey(name)) {
                buys.add(this.tradeGoods.get(name));
                if (MillConfigValues.LogSelling >= 2)
                  MillLog.minor(this, "Loaded buying good " + name + " for shop " + file.getName()); 
              } else {
                MillLog.error(this, "Unknown good when loading shop " + file.getName() + ": " + name);
              } 
            } 
            this.shopBuys.put(file.getName().split("\\.")[0], buys);
            continue;
          } 
          if (key.equals("buysoptional")) {
            List<TradeGood> buys = new ArrayList<>();
            for (String name : value.split(",")) {
              if (this.tradeGoods.containsKey(name)) {
                buys.add(this.tradeGoods.get(name));
                if (MillConfigValues.LogSelling >= 2)
                  MillLog.minor(this, "Loaded optional buying good " + name + " for shop " + file.getName()); 
              } else {
                MillLog.error(this, "Unknown good when loading shop " + file.getName() + ": " + name);
              } 
            } 
            this.shopBuysOptional.put(file.getName().split("\\.")[0], buys);
            continue;
          } 
          if (key.equals("sells")) {
            List<TradeGood> sells = new ArrayList<>();
            for (String name : value.split(",")) {
              if (this.tradeGoods.containsKey(name)) {
                sells.add(this.tradeGoods.get(name));
              } else {
                MillLog.error(this, "Unknown good when loading shop " + file.getName() + ": " + name);
              } 
            } 
            this.shopSells.put(file.getName().split("\\.")[0], sells);
            continue;
          } 
          if (key.equals("deliverto")) {
            List<InvItem> needs = new ArrayList<>();
            for (String name : value.split(",")) {
              if (InvItem.INVITEMS_BY_NAME.containsKey(name)) {
                needs.add((InvItem)InvItem.INVITEMS_BY_NAME.get(name));
              } else {
                MillLog.error(this, "Unknown good when loading shop " + file.getName() + ": " + name);
              } 
            } 
            this.shopNeeds.put(file.getName().split("\\.")[0], needs);
            continue;
          } 
          MillLog.error(this, "Unknown parameter when loading shop " + file.getName() + ": " + line);
        } 
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private void loadShops(VirtualDir cultureVirtualDir) {
    VirtualDir shopVirtualDir = cultureVirtualDir.getChildDirectory("shops");
    try {
      for (File file : shopVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt")))
        loadShop(file); 
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private void loadVillagerTypes(VirtualDir cultureVirtualDir) {
    VirtualDir villagersVirtualDir = cultureVirtualDir.getChildDirectory("villagers");
    try {
      for (File file : villagersVirtualDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"))) {
        VillagerType vtype = VillagerType.loadVillagerType(file, this);
        if (vtype != null) {
          if (this.villagerTypes.containsKey(vtype.key))
            MillLog.warning(this, "Found villager " + vtype.key + " twice in different subdirectories. If you want to replace one with the other they must be in the same subdirectory."); 
          this.villagerTypes.put(vtype.key, vtype);
          this.listVillagerTypes.add(vtype);
        } 
      } 
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private void makeBannerItem() {
    if (!this.cultureBanner.isEmpty()) {
      String bannerJSON = this.cultureBanner.replace("blockentitytag", "BlockEntityTag").replace("base", "Base").replace("pattern", "Pattern").replace("color", "Color");
      this.cultureBannerItemStack = new ItemStack(Items.BANNER, 1);
      try {
        this.cultureBannerItemStack.setTagCompound(JsonToNBT.getTagFromJson(bannerJSON));
        return;
      } catch (NBTException nbtException) {
        MillLog.error(this, "Bad culture banner JSON " + bannerJSON + " due to error " + nbtException.getMessage());
        MillLog.error(this, "Using default banner settings for culture " + this.key);
      } 
    } else {
      MillLog.warning(this, "No culture banner for culture " + this.key);
    } 
    this.cultureBannerItemStack = ItemBanner.makeBanner(EnumDyeColor.WHITE, null);
  }
  
  public CultureLanguage.Dialogue pickNewDialogue(MillVillager v1, MillVillager v2) {
    CultureLanguage.Dialogue d = null;
    if (this.fallbackLanguage != null)
      d = this.fallbackLanguage.getDialogue(v1, v2); 
    if (d != null)
      return d; 
    if (this.fallbackLanguageServer != null)
      d = this.fallbackLanguageServer.getDialogue(v1, v2); 
    if (d != null)
      return d; 
    d = this.mainLanguage.getDialogue(v1, v2);
    if (d != null)
      return d; 
    if (this.mainLanguageServer != null)
      d = this.mainLanguageServer.getDialogue(v1, v2); 
    if (d != null)
      return d; 
    return null;
  }
  
  private void readConfig(VirtualDir cultureVirtualDir) {
    try {
      File file = cultureVirtualDir.getChildFile("culture.txt");
      if (file != null)
        ParametersManager.loadAnnotedParameterData(file, this, null, "culture", null); 
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private void setTravelBookDefaults() {
    for (BuildingPlanSet planSet : this.ListPlanSets) {
      BuildingPlan startingPlan = planSet.getFirstStartingPlan();
      if (startingPlan.travelBookCategory == null && !startingPlan.isSubBuilding)
        startingPlan.travelBookCategory = setTravelBookDefaults_findBuildingPlanCategory(planSet); 
    } 
    for (BuildingPlanSet parentPlanSet : this.ListPlanSets) {
      BuildingPlan parentPlan = parentPlanSet.getPlan(0, ((BuildingPlan[])parentPlanSet.plans.get(0)).length - 1);
      String category = (parentPlanSet.getFirstStartingPlan()).travelBookCategory;
      for (String key : parentPlan.startingSubBuildings) {
        BuildingPlan subPlan = getBuildingPlanSet(key).getFirstStartingPlan();
        if (subPlan.travelBookCategory == null)
          subPlan.travelBookCategory = category; 
      } 
      for (String key : parentPlan.subBuildings) {
        BuildingPlan subPlan = getBuildingPlanSet(key).getFirstStartingPlan();
        if (subPlan.travelBookCategory == null)
          subPlan.travelBookCategory = category; 
      } 
    } 
    for (VillagerType villagerType : this.listVillagerTypes) {
      if (villagerType.travelBookCategory == null)
        villagerType.travelBookCategory = setTravelBookDefaults_findVillagerCategory(villagerType); 
    } 
    if (MillConfigValues.DEV) {
      for (BuildingPlanSet planSet : this.ListPlanSets) {
        BuildingPlan startingPlan = planSet.getFirstStartingPlan();
        if (startingPlan.travelBookDisplay && startingPlan.getIcon() == ItemStack.EMPTY)
          MillLog.warning(this, "Building " + startingPlan.buildingKey + " has no icon."); 
      } 
      for (VillagerType villagerType : this.listVillagerTypes) {
        if (villagerType.travelBookDisplay && villagerType.getIcon() == ItemStack.EMPTY)
          MillLog.warning(this, "Villager " + villagerType.key + " has no icon."); 
      } 
    } 
  }
  
  private String setTravelBookDefaults_findBuildingPlanCategory(BuildingPlanSet planSet) {
    BuildingPlan startingPlan = planSet.getFirstStartingPlan();
    for (VillageType village : this.listVillageTypes) {
      if (village.centreBuilding == planSet) {
        if (village.playerControlled)
          return startingPlan.travelBookCategory = "playerbuilding"; 
        return startingPlan.travelBookCategory = "townhall";
      } 
    } 
    if (startingPlan.isgift || startingPlan.price > 0)
      return startingPlan.travelBookCategory = "playerbuilding"; 
    if (startingPlan.isWallSegment)
      return "wall"; 
    if (startingPlan.isBorderBuilding)
      return "othervillage"; 
    for (VillageType village : this.listVillageTypes) {
      if (!village.isMarvel() && (
        village.startBuildings.contains(planSet) || village.coreBuildings.contains(planSet) || village.secondaryBuildings.contains(planSet) || village.extraBuildings.contains(planSet))) {
        if (startingPlan.femaleResident.size() > 0 || startingPlan.maleResident.size() > 0)
          return "house"; 
        return "othervillage";
      } 
    } 
    for (VillageType village : this.listVillageTypes) {
      if (village.isMarvel() && (
        village.centreBuilding == planSet || village.startBuildings.contains(planSet) || village.coreBuildings.contains(planSet) || village.secondaryBuildings.contains(planSet) || village.extraBuildings
        .contains(planSet)))
        return "marvel"; 
    } 
    for (VillageType village : this.listLoneBuildingTypes) {
      if (village.centreBuilding == planSet || village.startBuildings.contains(planSet) || village.coreBuildings.contains(planSet) || village.secondaryBuildings.contains(planSet) || village.extraBuildings
        .contains(planSet))
        return "lonebuilding"; 
    } 
    if (startingPlan.travelBookDisplay)
      MillLog.warning(this, "Could not categorize plan: " + planSet.key); 
    return null;
  }
  
  private String setTravelBookDefaults_findVillagerCategory(VillagerType villagerType) {
    if (villagerType.visitor)
      return "visitor"; 
    boolean foundInMarvel = false;
    boolean foundInVillage = false;
    boolean foundInLoneBuilding = false;
    for (BuildingPlanSet planSet : this.ListPlanSets) {
      BuildingPlan firstPlan = planSet.getFirstStartingPlan();
      if ((firstPlan.femaleResident.contains(villagerType.key) || firstPlan.maleResident.contains(villagerType.key)) && 
        firstPlan.travelBookCategory != null) {
        if (firstPlan.travelBookCategory.equals("marvel")) {
          foundInMarvel = true;
          continue;
        } 
        if (firstPlan.travelBookCategory.equals("townhall") || firstPlan.travelBookCategory.equals("house") || firstPlan.travelBookCategory.equals("othervillage") || firstPlan.travelBookCategory
          .equals("playerbuilding") || firstPlan.travelBookCategory.equals("wall")) {
          foundInVillage = true;
          continue;
        } 
        if (firstPlan.travelBookCategory.equals("lonebuilding"))
          foundInLoneBuilding = true; 
      } 
    } 
    if (foundInLoneBuilding && !foundInVillage && !foundInMarvel)
      return "lonevillager"; 
    if (foundInMarvel && !foundInVillage)
      return "marvelvillager"; 
    if (foundInVillage) {
      if (villagerType.isChief)
        return "leader"; 
      return "villager";
    } 
    if (villagerType.isChild)
      return "villager"; 
    if (villagerType.travelBookDisplay)
      MillLog.temp(this, "Could not auto-compute travel book category of villager: " + villagerType.key); 
    return null;
  }
  
  public String toString() {
    return "Culture: " + this.key;
  }
  
  private void validateTradeGoods() {
    for (TradeGood tradeGood : this.goodsList) {
      InvItem invItem = tradeGood.item;
      boolean inUse = false;
      for (List<TradeGood> tgs : this.shopBuys.values()) {
        for (TradeGood tg : tgs) {
          if (tg == tradeGood)
            inUse = true; 
        } 
      } 
      for (List<TradeGood> tgs : this.shopBuys.values()) {
        for (TradeGood tg : tgs) {
          if (tg == tradeGood)
            inUse = true; 
        } 
      } 
      for (List<TradeGood> tgs : this.shopBuysOptional.values()) {
        for (TradeGood tg : tgs) {
          if (tg == tradeGood)
            inUse = true; 
        } 
      } 
      for (List<TradeGood> tgs : this.shopSells.values()) {
        for (TradeGood tg : tgs) {
          if (tg == tradeGood)
            inUse = true; 
        } 
      } 
      for (VillagerType vtype : this.listVillagerTypes) {
        for (InvItem ii : vtype.foreignMerchantStock.keySet()) {
          if (invItem == ii)
            inUse = true; 
        } 
      } 
      if (!inUse && !tradeGood.key.equals("wood_any"))
        MillLog.warning(this, "Trade good " + tradeGood.key + " is used neither in shops nor for market merchants."); 
    } 
    for (TradeGood tradeGood : this.goodsList) {
      if (!tradeGood.travelBookCategory.equals("hidden") && !this.travelBookTradeGoodCategories.contains(tradeGood.travelBookCategory))
        MillLog.warning(this, "Trade good " + tradeGood.key + " has an unregsietred category: " + tradeGood.travelBookCategory); 
      if (tradeGood.travelBookCategory.equals("foreigntrade"))
        for (String key : this.shopSells.keySet()) {
          List<TradeGood> goods = this.shopSells.get(key);
          if (goods.contains(tradeGood))
            MillLog.warning(this, "Trade good " + tradeGood.key + " is listed as a foreign good but is sold by the culture in shop: " + key); 
        }  
    } 
  }
  
  public void writeCultureAvailableContentPacket(PacketBuffer data) throws IOException {
    data.writeString(this.key);
    data.writeShort(this.mainLanguage.strings.size());
    data.writeShort(this.mainLanguage.buildingNames.size());
    data.writeShort(this.mainLanguage.sentences.size());
    data.writeShort(this.fallbackLanguage.strings.size());
    data.writeShort(this.fallbackLanguage.buildingNames.size());
    data.writeShort(this.fallbackLanguage.sentences.size());
    data.writeShort(this.ListPlanSets.size());
    for (BuildingPlanSet set : this.ListPlanSets)
      data.writeString(set.key); 
    data.writeShort(this.villagerTypes.size());
    for (String key : this.villagerTypes.keySet()) {
      VillagerType vtype = this.villagerTypes.get(key);
      data.writeString(vtype.key);
    } 
    data.writeShort(this.villageTypes.size());
    for (String key : this.villageTypes.keySet()) {
      VillageType vtype = this.villageTypes.get(key);
      data.writeString(vtype.key);
    } 
    data.writeShort(this.loneBuildingTypes.size());
    for (String key : this.loneBuildingTypes.keySet()) {
      VillageType vtype = this.loneBuildingTypes.get(key);
      data.writeString(vtype.key);
    } 
  }
  
  public void writeCultureMissingContentPackPacket(PacketBuffer data, String mainLanguage, String fallbackLanguage, int nbStrings, int nbBuildingNames, int nbSentences, int nbFallbackStrings, int nbFallbackBuildingNames, int nbFallbackSentences, List<String> planSetAvailable, List<String> villagerAvailable, List<String> villagesAvailable, List<String> loneBuildingsAvailable) throws IOException {
    data.writeString(this.key);
    CultureLanguage clientMain = null, clientFallback = null;
    if (this.loadedLanguages.containsKey(mainLanguage)) {
      clientMain = this.loadedLanguages.get(mainLanguage);
    } else if (this.loadedLanguages.containsKey(mainLanguage.split("_")[0])) {
      clientMain = this.loadedLanguages.get(mainLanguage.split("_")[0]);
    } 
    if (this.loadedLanguages.containsKey(fallbackLanguage)) {
      clientFallback = this.loadedLanguages.get(fallbackLanguage);
    } else if (this.loadedLanguages.containsKey(fallbackLanguage.split("_")[0])) {
      clientFallback = this.loadedLanguages.get(fallbackLanguage.split("_")[0]);
    } 
    if (clientMain != null && clientMain.strings.size() > nbStrings) {
      StreamReadWrite.writeStringStringMap(clientMain.strings, data);
    } else {
      StreamReadWrite.writeStringStringMap(null, data);
    } 
    if (clientMain != null && clientMain.buildingNames.size() > nbBuildingNames) {
      StreamReadWrite.writeStringStringMap(clientMain.buildingNames, data);
    } else {
      StreamReadWrite.writeStringStringMap(null, data);
    } 
    if (clientMain != null && clientMain.sentences.size() > nbSentences) {
      StreamReadWrite.writeStringStringListMap(clientMain.sentences, data);
    } else {
      StreamReadWrite.writeStringStringMap(null, data);
    } 
    if (clientFallback != null && clientFallback.strings.size() > nbFallbackStrings) {
      StreamReadWrite.writeStringStringMap(clientFallback.strings, data);
    } else {
      StreamReadWrite.writeStringStringMap(null, data);
    } 
    if (clientFallback != null && clientFallback.buildingNames.size() > nbFallbackBuildingNames) {
      StreamReadWrite.writeStringStringMap(clientFallback.buildingNames, data);
    } else {
      StreamReadWrite.writeStringStringMap(null, data);
    } 
    if (clientFallback != null && clientFallback.sentences.size() > nbFallbackSentences) {
      StreamReadWrite.writeStringStringListMap(clientFallback.sentences, data);
    } else {
      StreamReadWrite.writeStringStringMap(null, data);
    } 
    int nbToWrite = 0;
    for (BuildingPlanSet set : this.ListPlanSets) {
      if (planSetAvailable == null || !planSetAvailable.contains(set.key))
        nbToWrite++; 
    } 
    data.writeShort(nbToWrite);
    for (BuildingPlanSet set : this.ListPlanSets) {
      if (planSetAvailable == null || !planSetAvailable.contains(set.key))
        set.writeBuildingPlanSetInfo(data); 
    } 
    nbToWrite = 0;
    for (String key : this.villagerTypes.keySet()) {
      if (villagerAvailable == null || !villagerAvailable.contains(key))
        nbToWrite++; 
    } 
    data.writeShort(nbToWrite);
    for (String key : this.villagerTypes.keySet()) {
      if (villagerAvailable == null || !villagerAvailable.contains(key)) {
        VillagerType vtype = this.villagerTypes.get(key);
        vtype.writeVillagerTypeInfo(data);
      } 
    } 
    nbToWrite = 0;
    for (String key : this.villageTypes.keySet()) {
      if (villagesAvailable == null || !villagesAvailable.contains(key))
        nbToWrite++; 
    } 
    data.writeShort(nbToWrite);
    for (String key : this.villageTypes.keySet()) {
      if (villagesAvailable == null || !villagesAvailable.contains(key)) {
        VillageType vtype = this.villageTypes.get(key);
        vtype.writeVillageTypeInfo(data);
      } 
    } 
    nbToWrite = 0;
    for (String key : this.loneBuildingTypes.keySet()) {
      if (loneBuildingsAvailable == null || !loneBuildingsAvailable.contains(key))
        nbToWrite++; 
    } 
    data.writeShort(nbToWrite);
    for (String key : this.loneBuildingTypes.keySet()) {
      if (loneBuildingsAvailable == null || !loneBuildingsAvailable.contains(key)) {
        VillageType vtype = this.loneBuildingTypes.get(key);
        vtype.writeVillageTypeInfo(data);
      } 
    } 
  }
}
