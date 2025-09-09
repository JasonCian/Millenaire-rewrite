package org.millenaire.common.culture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class VillagerType implements MillCommonUtilities.WeightedChoice {
  @Documentation("The villager is a local merchant that will travel from village to village.")
  public static final String TAG_LOCALMERCHANT = "localmerchant";
  
  @Documentation("The villager is a foreign merchant that will appear in markets and disappear after a few days.")
  public static final String TAG_FOREIGNMERCHANT = "foreignmerchant";
  
  @Documentation("The villager is a child that will grow up into an adult villager.")
  public static final String TAG_CHILD = "child";
  
  @Documentation("The villager is a village chief. Allows use of the Chief UI.")
  public static final String TAG_CHIEF = "chief";
  
  @Documentation("The villager is a seller. Will come to see the player when he visits a shop, and will open the trade UI when interacted with.")
  public static final String TAG_SELLER = "seller";
  
  @Documentation("The villager provides the 'meditation' service unlocked by the Indian CQ quest.")
  public static final String TAG_MEDITATES = "meditates";
  
  @Documentation("The villager provides the 'sacrifices' service unlocked by the Maya CQ quest.")
  public static final String TAG_SACRIFICES = "performssacrifices";
  
  @Documentation("The villager is a visitor to the village, that should not be counted in population listings etc (like merchants).")
  public static final String TAG_VISITOR = "visitor";
  
  @Documentation("This villager will help defend his village against any attacks (raid, other players, mobs...).")
  public static final String TAG_HELPSINATTACKS = "helpinattacks";
  
  @Documentation("This villager will attack the player unprovoked. Used for bandits and similar mobs.")
  public static final String TAG_HOSTILE = "hostile";
  
  @Documentation("This villager cannot clear leaves when they are blocking his path. Used for villagers living in buildings with decorative leaves.")
  public static final String TAG_NOLEAFCLEARING = "noleafclearing";
  
  @Documentation("This villager is able to use a bow, if present in his inventory.")
  public static final String TAG_ARCHER = "archer";
  
  @Documentation("This villager can take part in raids organised by his village.")
  public static final String TAG_RAIDER = "raider";
  
  @Documentation("This villager cannot teleport if stuck. Used to prevent bandits and similar villagers from teleporting straight into enemy bases.")
  public static final String TAG_NOTELEPORT = "noteleport";
  
  @Documentation("This villager's name should not be displayed above his head.")
  public static final String TAG_HIDENAME = "hidename";
  
  @Documentation("This villager's health should be displayed above his head, even if not hired by the player.")
  public static final String TAG_SHOWHEALTH = "showhealth";
  
  @Documentation("This villager will stop following a target if more than 20 blocks away.")
  public static final String TAG_DEFENSIVE = "defensive";
  
  @Documentation("This villager should never be resurrected. Used for some quest opponents.")
  public static final String TAG_NORESURRECT = "noresurrect";
  
  public static final int NB_CLOTH_LAYERS = 2;
  
  public String key;
  
  public Culture culture;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY, paramName = "native_name")
  @FieldDocumentation(explanation = "Name of the villager in the culture's language.")
  public String name;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRINGDISPLAY, paramName = "alt_native_name")
  @FieldDocumentation(explanation = "Alternate name of the villager in the culture's language (used for teens).")
  public String altname;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING, paramName = "alt_key")
  @FieldDocumentation(explanation = "'Key' of the alternate type (used for teens).")
  public String altkey;
  
  public static VillagerType loadVillagerType(File file, Culture c) {
    VillagerType v = new VillagerType(c, file.getName().split("\\.")[0]);
    try {
      v.villagerConfig = VillagerConfig.DEFAULT_CONFIG;
      ParametersManager.loadAnnotedParameterData(file, v, null, "villager type", c);
      v.isChild = v.containsTags("child");
      v.isChief = v.containsTags("chief");
      v.canSell = v.containsTags("seller");
      v.canMeditate = v.containsTags("meditates");
      v.canPerformSacrifices = v.containsTags("performssacrifices");
      v.visitor = v.containsTags("visitor");
      v.helpInAttacks = v.containsTags("helpinattacks");
      v.isLocalMerchant = v.containsTags("localmerchant");
      v.isForeignMerchant = v.containsTags("foreignmerchant");
      v.hostile = v.containsTags("hostile");
      v.noleafclearing = v.containsTags("noleafclearing");
      v.isArcher = v.containsTags("archer");
      v.isRaider = v.containsTags("raider");
      v.noTeleport = v.containsTags("noteleport");
      v.hideName = v.containsTags("hidename");
      v.showHealth = v.containsTags("showhealth");
      v.isDefensive = v.containsTags("defensive");
      v.noResurrect = v.containsTags("noresurrect");
      v.goals.add(Goal.sleep);
      if (v.isChild)
        for (InvItem food : v.villagerConfig.foodsGrowthSorted)
          v.requiredFoodAndGoods.put(food, Integer.valueOf(2));  
      if (v.hasChildren())
        for (InvItem food : v.villagerConfig.foodsConceptionSorted)
          v.requiredFoodAndGoods.put(food, Integer.valueOf(2));  
      v.requiredFoodAndGoods.putAll(v.requiredGoods);
      if (v.toolsCategoriesNeeded.size() > 0) {
        boolean foundToolFetchingGoal = false;
        for (Goal g : v.goals) {
          if (g == Goal.gettool)
            foundToolFetchingGoal = true; 
        } 
        if (!foundToolFetchingGoal)
          v.goals.add(Goal.gettool); 
      } 
      for (InvItem item : v.foreignMerchantStock.keySet()) {
        if (c.getTradeGood(item) == null) {
          MillLog.warning(v, "Starting inv of foreign merchant countains non-tradeable good: " + item);
          continue;
        } 
        if ((c.getTradeGood(item)).foreignMerchantPrice < 1)
          MillLog.warning(v, "Starting inv of foreign merchant countains good with null tradeable price: " + item); 
      } 
      v.testTexturePresence();
      if (MillConfigValues.LogVillager >= 1)
        MillLog.major(v, "Loaded villager type: " + v.key + " " + v.helpInAttacks); 
      return v;
    } catch (Exception e) {
      MillLog.printException(e);
      return null;
    } 
  }
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM)
  @FieldDocumentation(explanation = "Name of a good whose icon represents this villager type.")
  private final InvItem icon = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING, paramName = "travelbook_category")
  @FieldDocumentation(explanation = "Category in the Travel Book to appear in.")
  public String travelBookCategory = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "travelbook_display", defaultValue = "true")
  @FieldDocumentation(explanation = "Whether to display this villager type in the Travel Book.")
  public boolean travelBookDisplay = true;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM, paramName = "travelbook_held_item")
  @FieldDocumentation(explanation = "Item to hold in the main hand for the Travel Book pict")
  private final InvItem travelBookHeldItem = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM, paramName = "travelbook_held_item_off_hand")
  @FieldDocumentation(explanation = "Item to hold in the off hand for the Travel Book pict")
  private final InvItem travelBookHeldItemOffHand = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.BOOLEAN, paramName = "travelbook_main_culture_villager", defaultValue = "false")
  @FieldDocumentation(explanation = "Whether this villager is the 'headline' villager for the culture (and thus gets a special export).")
  public boolean travelBookMainCultureVillager = true;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "The list to use for this villager's family name.")
  public String familyNameList;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "The list to use for this villager's given name.")
  public String firstNameList;
  
  @ConfigField(type = AnnotedParameter.ParameterType.VILLAGERCONFIG)
  @FieldDocumentation(explanation = "The villager config the villager uses, if different from the default.")
  public VillagerConfig villagerConfig = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "Model to use (used to switch women to one of the custom Millénaire female models).")
  public String model = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.FLOAT, paramName = "baseheight", defaultValue = "1")
  @FieldDocumentation(explanation = "The villager's height (1 = two blocks high).")
  public float baseScale = 1.0F;
  
  @ConfigField(type = AnnotedParameter.ParameterType.FLOAT, paramName = "basespeed", defaultValue = "0.55")
  @FieldDocumentation(explanation = "The villager's speed.")
  public float baseSpeed = 0.55F;
  
  @ConfigField(type = AnnotedParameter.ParameterType.GENDER)
  @FieldDocumentation(explanation = "Gender of the villager.")
  public int gender;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "The villager type used to create male offspring from this villager. Set only for women.")
  public String maleChild = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING)
  @FieldDocumentation(explanation = "The villager type used to create female offspring from this villager. Set only for women.")
  public String femaleChild = null;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "texture")
  @FieldDocumentation(explanation = "Texture to use for the villager. If more than one listed, will be picked at random.")
  public List<String> textures = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.CLOTHES, paramName = "clothes")
  @FieldDocumentation(explanation = "A cloth the villager can wear. 'free' is free clothes, 'natural' is fixed layers that do not change on picking up updated clothes.")
  public HashMap<String, List<String>> clothes = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "requiredgood")
  @FieldDocumentation(explanation = "A good and its quantity the villager requires. For example, inputs for a crafter.")
  public HashMap<InvItem, Integer> requiredGoods = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD)
  @FieldDocumentation(explanation = "A good and a quantity that will be in the villager's inventory on spawn. For example, a starting stock of seeds.")
  public HashMap<InvItem, Integer> startingInv = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_ADD, paramName = "itemneeded")
  @FieldDocumentation(explanation = "An item the villager needs and will keep in his inventory, like the clothes items for the Byzantines.")
  public List<InvItem> itemsNeeded = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.TOOLCATEGORIES_ADD, paramName = "toolneededclass")
  @FieldDocumentation(explanation = "Tools and similar items the villager needs and will go and pick if available.")
  public List<String> toolsCategoriesNeeded = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM, paramName = "defaultweapon")
  @FieldDocumentation(explanation = "The villager's default weapon, used when no other are available.")
  public InvItem startingWeapon;
  
  @ConfigField(type = AnnotedParameter.ParameterType.GOAL_ADD, paramName = "goal")
  @FieldDocumentation(explanation = "Goal a villager can pursue.")
  public List<Goal> goals = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_ADD, paramName = "bringbackhomegood")
  @FieldDocumentation(explanation = "An item type the villager will bring back home when present in his inventory. Used in particular to bring back goods produced via crafting/mining/harvesting etc.")
  public List<InvItem> bringBackHomeGoods = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_ADD, paramName = "collectgood")
  @FieldDocumentation(explanation = "An item type the villager will attempt to collect if present around him, like saplings for lumbermen.")
  public List<InvItem> collectGoods = new ArrayList<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, defaultValue = "1")
  @FieldDocumentation(explanation = "The villager's attack strength.")
  public int baseAttackStrength;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, paramName = "experiencegiven")
  @FieldDocumentation(explanation = "The experience quantity given by killing this villager.")
  public int expgiven = 0;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, paramName = "health", defaultValue = "30")
  @FieldDocumentation(explanation = "How much health the villager has.")
  public int health;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER, paramName = "hiringcost")
  @FieldDocumentation(explanation = "The cost in denier of hiring this villager.")
  public int hireCost;
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "merchantstock")
  @FieldDocumentation(explanation = "A good and a quantity that a foreign merchant starts with in order to sell it to the player.")
  public HashMap<InvItem, Integer> foreignMerchantStock = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INTEGER)
  @FieldDocumentation(explanation = "The weight given to this villager when picking a villager at random. Used to pick merchants for the market.")
  public int chanceWeight = 0;
  
  @ConfigField(type = AnnotedParameter.ParameterType.STRING_ADD, paramName = "tag")
  @FieldDocumentation(explanation = "A tag set for this villager type. Controls various special behaviours.")
  private final List<String> tags = new ArrayList<>();
  
  public boolean isChild = false;
  
  public boolean isReligious = false;
  
  public boolean isChief = false;
  
  public boolean canSell = false;
  
  public boolean canMeditate = false;
  
  public boolean canPerformSacrifices = false;
  
  public boolean visitor = false;
  
  public boolean helpInAttacks = false;
  
  public boolean isLocalMerchant = false;
  
  public boolean isForeignMerchant = false;
  
  public boolean hostile = false;
  
  public boolean isArcher = false;
  
  public boolean noleafclearing = false;
  
  public boolean isRaider = false;
  
  public boolean noTeleport = false;
  
  public boolean hideName = false;
  
  public boolean showHealth = false;
  
  public boolean isDefensive = false;
  
  public boolean noResurrect = false;
  
  public HashMap<InvItem, Integer> requiredFoodAndGoods = new HashMap<>();
  
  public VillagerType(Culture c, String key) {
    this.culture = c;
    this.key = key;
  }
  
  public boolean containsTags(String tag) {
    return this.tags.contains(tag.toLowerCase());
  }
  
  public int getChoiceWeight(EntityPlayer player) {
    return this.chanceWeight;
  }
  
  public boolean[] getClothLayersOfType(String clothType) {
    boolean[] layers = new boolean[2];
    for (int layer = 0; layer < 2; layer++) {
      if (this.clothes.containsKey(clothType + "_" + layer)) {
        layers[layer] = true;
      } else {
        layers[layer] = false;
      } 
    } 
    return layers;
  }
  
  public ResourceLocation getEntityName() {
    if ("femaleasymmetrical".equals(this.model))
      return MillVillager.GENERIC_ASYMM_FEMALE; 
    if ("femalesymmetrical".equals(this.model))
      return MillVillager.GENERIC_SYMM_FEMALE; 
    if ("zombie".equals(this.model))
      return MillVillager.GENERIC_ZOMBIE; 
    return MillVillager.GENERIC_VILLAGER;
  }
  
  public ItemStack getIcon() {
    if (this.icon == null)
      return ItemStack.EMPTY; 
    return this.icon.getItemStack();
  }
  
  public String getNameNative() {
    return this.name;
  }
  
  public String getNameNativeAndTranslated() {
    String fullName = this.name;
    if (getNameTranslated() != null && getNameTranslated().length() > 0)
      fullName = fullName + " (" + getNameTranslated() + ")"; 
    return fullName;
  }
  
  public String getNameTranslated() {
    if (this.culture.canReadVillagerNames())
      return this.culture.getCultureString("villager." + this.key); 
    return null;
  }
  
  public ResourceLocation getNewTexture() {
    String texture = this.textures.get(MillCommonUtilities.randomInt(this.textures.size()));
    if (texture.contains(":"))
      return new ResourceLocation(texture); 
    return new ResourceLocation("millenaire", texture);
  }
  
  public String getRandomClothTexture(String clothType, int layer) {
    if (this.clothes.containsKey(clothType + "_" + layer))
      return ((List<String>)this.clothes.get(clothType + "_" + layer)).get(MillCommonUtilities.randomInt(((List)this.clothes.get(clothType + "_" + layer)).size())); 
    return null;
  }
  
  public String getRandomFamilyName(Set<String> namesTaken) {
    return this.culture.getRandomNameFromList(this.familyNameList, namesTaken);
  }
  
  public String getRandomFirstName() {
    return this.culture.getRandomNameFromList(this.firstNameList);
  }
  
  public ItemStack getTravelBookHeldItem() {
    if (this.travelBookHeldItem == null)
      return null; 
    return this.travelBookHeldItem.getItemStack();
  }
  
  public ItemStack getTravelBookHeldItemOffHand() {
    if (this.travelBookHeldItemOffHand == null)
      return null; 
    return this.travelBookHeldItemOffHand.getItemStack();
  }
  
  public boolean hasChildren() {
    return (this.maleChild != null && this.femaleChild != null);
  }
  
  public boolean hasClothTexture(String clothType) {
    for (int layer = 0; layer < 2; layer++) {
      if (this.clothes.containsKey(clothType + "_" + layer))
        return true; 
    } 
    return false;
  }
  
  public boolean isClothValid(String clothType, String texture, int layer) {
    if (!this.clothes.containsKey(clothType + "_" + layer))
      return false; 
    for (String s : this.clothes.get(clothType + "_" + layer)) {
      if (s.equalsIgnoreCase(texture))
        return true; 
    } 
    return false;
  }
  
  public boolean isTextureValid(String texture) {
    for (String s : this.textures) {
      if (s.equalsIgnoreCase(texture))
        return true; 
    } 
    return false;
  }
  
  public void readVillagerTypeInfoPacket(PacketBuffer data) throws IOException {
    this.name = StreamReadWrite.readNullableString(data);
    this.altkey = StreamReadWrite.readNullableString(data);
    this.altname = StreamReadWrite.readNullableString(data);
    this.model = StreamReadWrite.readNullableString(data);
    this.gender = data.readInt();
  }
  
  private void testTexturePresence() {
    for (String texture : this.textures) {
      if (!MillCommonUtilities.testResourcePresence("millenaire", texture))
        MillLog.error(this, "Specified body texture cannot be found: " + texture); 
    } 
    for (String type : this.clothes.keySet()) {
      for (String texture : this.clothes.get(type)) {
        if (!MillCommonUtilities.testResourcePresence("millenaire", texture))
          MillLog.error(this, "Specified cloth texture cannot be found: " + texture); 
      } 
    } 
  }
  
  public String toString() {
    return "VT: " + this.culture.key + "/" + this.key;
  }
  
  public void writeVillagerTypeInfo(PacketBuffer data) throws IOException {
    data.writeString(this.key);
    StreamReadWrite.writeNullableString(this.name, data);
    StreamReadWrite.writeNullableString(this.altkey, data);
    StreamReadWrite.writeNullableString(this.altname, data);
    StreamReadWrite.writeNullableString(this.model, data);
    data.writeInt(this.gender);
  }
}
