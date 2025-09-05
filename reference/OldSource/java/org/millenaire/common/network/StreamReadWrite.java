package org.millenaire.common.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.quest.Quest;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.quest.QuestInstanceVillager;
import org.millenaire.common.ui.PujaSacrifice;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class StreamReadWrite {
  public static final int MAX_STR_LENGTH = 2048;
  
  public static CopyOnWriteArrayList<Boolean> readBooleanList(PacketBuffer ds) {
    CopyOnWriteArrayList<Boolean> v = new CopyOnWriteArrayList<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++)
      v.add(Boolean.valueOf(ds.readBoolean())); 
    return v;
  }
  
  public static List<BuildingLocation> readBuildingLocationList(PacketBuffer ds) {
    List<BuildingLocation> v = new ArrayList<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++)
      v.add(readNullableBuildingLocation(ds)); 
    return v;
  }
  
  public static BuildingPlan readBuildingPlanInfo(PacketBuffer ds, Culture culture) {
    String key = ds.readString(2048);
    int level = ds.readInt();
    int variation = ds.readInt();
    BuildingPlan plan = new BuildingPlan(key, level, variation, culture);
    plan.planName = readNullableString(ds);
    plan.nativeName = readNullableString(ds);
    plan.requiredGlobalTag = readNullableString(ds);
    plan.forbiddenTagsInVillage = readStringList(ds);
    plan.shop = readNullableString(ds);
    plan.isSubBuilding = ds.readBoolean();
    plan.price = ds.readInt();
    plan.reputation = ds.readInt();
    plan.version = ds.readInt();
    plan.maleResident = readStringList(ds);
    plan.femaleResident = readStringList(ds);
    plan.visitors = readStringList(ds);
    plan.startingSubBuildings = readStringList(ds);
    plan.subBuildings = readStringList(ds);
    plan.tags = readStringList(ds);
    return plan;
  }
  
  public static HashMap<InvItem, Integer> readInventory(PacketBuffer ds) {
    HashMap<InvItem, Integer> inv = new HashMap<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++) {
      InvItem item = InvItem.createInvItem(Item.getItemById(ds.readInt()), ds.readInt());
      inv.put(item, Integer.valueOf(ds.readInt()));
    } 
    return inv;
  }
  
  private static ItemStack readItemStack(PacketBuffer par1PacketBuffer) throws IOException {
    ItemStack is = null;
    int id = par1PacketBuffer.readInt();
    if (id >= 0) {
      byte nb = par1PacketBuffer.readByte();
      short meta = par1PacketBuffer.readShort();
      is = new ItemStack(Item.getItemById(id), nb, meta);
      boolean hasNBTTag = par1PacketBuffer.readBoolean();
      if (hasNBTTag) {
        NBTTagCompound tag = readNBTTagCompound(par1PacketBuffer);
        is.setTagCompound(tag);
      } 
    } 
    return is;
  }
  
  public static CopyOnWriteArrayList<ItemStack> readItemStackList(PacketBuffer ds) throws IOException {
    CopyOnWriteArrayList<ItemStack> v = new CopyOnWriteArrayList<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++)
      v.add(readNullableItemStack(ds)); 
    return v;
  }
  
  private static NBTTagCompound readNBTTagCompound(PacketBuffer par1PacketBuffer) throws IOException {
    short var2 = par1PacketBuffer.readShort();
    if (var2 < 0)
      return null; 
    return par1PacketBuffer.readCompoundTag();
  }
  
  public static BuildingLocation readNullableBuildingLocation(PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    BuildingLocation bl = new BuildingLocation();
    bl.isCustomBuilding = ds.readBoolean();
    bl.planKey = readNullableString(ds);
    bl.shop = readNullableString(ds);
    bl.minx = ds.readInt();
    bl.maxx = ds.readInt();
    bl.miny = ds.readInt();
    bl.maxy = ds.readInt();
    bl.minz = ds.readInt();
    bl.maxz = ds.readInt();
    bl.minxMargin = ds.readInt();
    bl.maxxMargin = ds.readInt();
    bl.minyMargin = ds.readInt();
    bl.maxyMargin = ds.readInt();
    bl.minzMargin = ds.readInt();
    bl.maxzMargin = ds.readInt();
    bl.orientation = ds.readInt();
    bl.length = ds.readInt();
    bl.width = ds.readInt();
    bl.level = ds.readInt();
    bl.setVariation(ds.readInt());
    bl.reputation = ds.readInt();
    bl.price = ds.readInt();
    bl.version = ds.readInt();
    bl.pos = readNullablePoint(ds);
    bl.chestPos = readNullablePoint(ds);
    bl.sleepingPos = readNullablePoint(ds);
    bl.sellingPos = readNullablePoint(ds);
    bl.craftingPos = readNullablePoint(ds);
    bl.shelterPos = readNullablePoint(ds);
    bl.defendingPos = readNullablePoint(ds);
    String cultureKey = readNullableString(ds);
    bl.culture = Culture.getCultureByName(cultureKey);
    bl.subBuildings = readStringList(ds);
    bl.showTownHallSigns = ds.readBoolean();
    bl.upgradesAllowed = ds.readBoolean();
    bl.bedrocklevel = ds.readBoolean();
    bl.isSubBuildingLocation = ds.readBoolean();
    return bl;
  }
  
  public static BuildingProject readNullableBuildingProject(PacketBuffer ds, Culture culture) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    BuildingProject bp = new BuildingProject();
    bp.isCustomBuilding = ds.readBoolean();
    bp.key = readNullableString(ds);
    bp.location = readNullableBuildingLocation(ds);
    if (culture != null)
      if (bp.isCustomBuilding) {
        bp.customBuildingPlan = culture.getBuildingCustom(bp.key);
      } else {
        bp.planSet = culture.getBuildingPlanSet(bp.key);
      }  
    return bp;
  }
  
  public static TradeGood readNullableGoods(PacketBuffer ds, Culture culture) throws MillLog.MillenaireException {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    String cultureKey = ds.readString(2048);
    InvItem iv = InvItem.createInvItem(MillCommonUtilities.getItemById(ds.readInt()), ds.readByte());
    TradeGood g = new TradeGood("generated", Culture.getCultureByName(cultureKey), iv);
    g.requiredTag = readNullableString(ds);
    g.travelBookCategory = readNullableString(ds);
    g.autoGenerate = ds.readBoolean();
    g.minReputation = ds.readInt();
    return g;
  }
  
  public static ItemStack readNullableItemStack(PacketBuffer ds) throws IOException {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    return readItemStack(ds);
  }
  
  public static Point readNullablePoint(PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    int x = ds.readInt();
    int y = ds.readInt();
    int z = ds.readInt();
    return new Point(x, y, z);
  }
  
  public static QuestInstance readNullableQuestInstance(MillWorldData mw, PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    long id = ds.readLong();
    String questKey = ds.readString(2048);
    if (!Quest.quests.containsKey(questKey))
      return null; 
    Quest quest = (Quest)Quest.quests.get(questKey);
    UserProfile profile = mw.getProfile(ds.readUniqueId());
    int currentStep = ds.readUnsignedByte();
    long startTime = ds.readLong();
    long currentStepStart = ds.readLong();
    HashMap<String, QuestInstanceVillager> villagers = new HashMap<>();
    int nb = ds.readUnsignedByte();
    for (int i = 0; i < nb; i++) {
      String key = ds.readString(2048);
      villagers.put(key, readNullableQuestVillager(mw, ds));
    } 
    QuestInstance qi = new QuestInstance(mw, quest, profile, villagers, startTime, currentStep, currentStepStart);
    qi.uniqueid = id;
    return qi;
  }
  
  public static QuestInstanceVillager readNullableQuestVillager(MillWorldData mw, PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    return new QuestInstanceVillager(mw, readNullablePoint(ds), ds.readLong());
  }
  
  public static ResourceLocation readNullableResourceLocation(PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    return new ResourceLocation(ds.readString(2048));
  }
  
  public static String readNullableString(PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    return ds.readString(2048);
  }
  
  public static UUID readNullableUUID(PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    return ds.readUniqueId();
  }
  
  public static VillagerRecord readNullableVillagerRecord(MillWorldData mw, PacketBuffer ds) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    VillagerRecord vr = new VillagerRecord(mw);
    vr.setVillagerId(ds.readLong());
    vr.type = readNullableString(ds);
    vr.firstName = readNullableString(ds);
    vr.familyName = readNullableString(ds);
    vr.texture = readNullableResourceLocation(ds);
    vr.nb = ds.readInt();
    vr.gender = ds.readInt();
    vr.size = ds.readInt();
    vr.scale = ds.readFloat();
    vr.rightHanded = ds.readBoolean();
    vr.setCulture(Culture.getCultureByName(readNullableString(ds)));
    vr.fathersName = readNullableString(ds);
    vr.mothersName = readNullableString(ds);
    vr.spousesName = readNullableString(ds);
    vr.maidenName = readNullableString(ds);
    vr.killed = ds.readBoolean();
    vr.raidingVillage = ds.readBoolean();
    vr.awayraiding = ds.readBoolean();
    vr.awayhired = ds.readBoolean();
    vr.setHousePos(readNullablePoint(ds));
    vr.setTownHallPos(readNullablePoint(ds));
    vr.originalVillagePos = readNullablePoint(ds);
    vr.raiderSpawn = ds.readLong();
    vr.inventory = readInventory(ds);
    vr.questTags = readStringList(ds);
    return vr;
  }
  
  public static PujaSacrifice readOrUpdateNullablePuja(PacketBuffer ds, Building b, PujaSacrifice puja) {
    boolean isnull = ds.readBoolean();
    if (isnull)
      return null; 
    short type = ds.readShort();
    if (puja == null)
      puja = new PujaSacrifice(b, type); 
    int enchantmentId = ds.readShort();
    for (int i = 0; i < puja.getTargets().size(); i++) {
      if (Enchantment.getEnchantmentID(((PujaSacrifice.PrayerTarget)puja.getTargets().get(i)).enchantment) == enchantmentId)
        puja.currentTarget = puja.getTargets().get(i); 
    } 
    puja.pujaProgress = ds.readShort();
    puja.offeringNeeded = ds.readShort();
    puja.offeringProgress = ds.readShort();
    return puja;
  }
  
  public static ConcurrentHashMap<Point, Integer> readPointIntegerMap(PacketBuffer ds) {
    ConcurrentHashMap<Point, Integer> map = new ConcurrentHashMap<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++) {
      Point p = readNullablePoint(ds);
      map.put(p, Integer.valueOf(ds.readInt()));
    } 
    return map;
  }
  
  public static CopyOnWriteArrayList<Point> readPointList(PacketBuffer ds) {
    CopyOnWriteArrayList<Point> v = new CopyOnWriteArrayList<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++)
      v.add(readNullablePoint(ds)); 
    return v;
  }
  
  public static ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> readProjectListList(PacketBuffer ds, Culture culture) {
    ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> v = new ConcurrentHashMap<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++) {
      int nb2 = ds.readInt();
      CopyOnWriteArrayList<BuildingProject> v2 = new CopyOnWriteArrayList<>();
      for (int j = 0; j < nb2; j++)
        v2.add(readNullableBuildingProject(ds, culture)); 
      v.put(BuildingProject.EnumProjects.getById(i), v2);
    } 
    return v;
  }
  
  public static Collection<String> readStringCollection(PacketBuffer ds) {
    return readStringList(ds);
  }
  
  public static CopyOnWriteArrayList<String> readStringList(PacketBuffer ds) {
    CopyOnWriteArrayList<String> v = new CopyOnWriteArrayList<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++)
      v.add(readNullableString(ds)); 
    return v;
  }
  
  public static String[][] readStringStringArray(PacketBuffer ds) {
    String[][] strings = new String[ds.readInt()][];
    for (int i = 0; i < strings.length; i++) {
      String[] array = new String[ds.readInt()];
      for (int j = 0; j < array.length; j++)
        array[j] = readNullableString(ds); 
      strings[i] = array;
    } 
    return strings;
  }
  
  public static HashMap<String, List<String>> readStringStringListMap(PacketBuffer ds) {
    HashMap<String, List<String>> v = new HashMap<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++) {
      String key = ds.readString(2048);
      v.put(key, readStringList(ds));
    } 
    return v;
  }
  
  public static HashMap<String, String> readStringStringMap(PacketBuffer ds) {
    HashMap<String, String> v = new HashMap<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++) {
      String key = ds.readString(2048);
      v.put(key, readNullableString(ds));
    } 
    return v;
  }
  
  public static List<VillagerRecord> readVillagerRecordList(MillWorldData mw, PacketBuffer ds) {
    List<VillagerRecord> v = new ArrayList<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++)
      v.add(readNullableVillagerRecord(mw, ds)); 
    return v;
  }
  
  public static Map<Long, VillagerRecord> readVillagerRecordMap(MillWorldData mw, PacketBuffer ds) {
    Map<Long, VillagerRecord> v = new HashMap<>();
    int nb = ds.readInt();
    for (int i = 0; i < nb; i++) {
      Long key = Long.valueOf(ds.readLong());
      v.put(key, readNullableVillagerRecord(mw, ds));
    } 
    return v;
  }
  
  public static void writeBooleanList(List<Boolean> list, PacketBuffer data) {
    data.writeInt(list.size());
    for (Boolean b : list)
      data.writeBoolean(b.booleanValue()); 
  }
  
  public static void writeBuildingLocationList(List<BuildingLocation> bls, PacketBuffer data) {
    data.writeInt(bls.size());
    for (BuildingLocation bl : bls)
      writeNullableBuildingLocation(bl, data); 
  }
  
  public static void writeBuildingPlanInfo(BuildingPlan plan, PacketBuffer data) {
    data.writeString(plan.buildingKey);
    data.writeInt(plan.level);
    data.writeInt(plan.variation);
    writeNullableString(plan.planName, data);
    writeNullableString(plan.nativeName, data);
    writeNullableString(plan.requiredGlobalTag, data);
    writeStringList(plan.forbiddenTagsInVillage, data);
    writeNullableString(plan.shop, data);
    data.writeBoolean(plan.isSubBuilding);
    data.writeInt(plan.price);
    data.writeInt(plan.reputation);
    data.writeInt(plan.version);
    writeStringList(plan.maleResident, data);
    writeStringList(plan.femaleResident, data);
    writeStringList(plan.visitors, data);
    writeStringList(plan.startingSubBuildings, data);
    writeStringList(plan.subBuildings, data);
    writeStringList(plan.tags, data);
  }
  
  public static void writeInventory(HashMap<InvItem, Integer> inventory, PacketBuffer data) {
    data.writeInt(inventory.size());
    for (InvItem key : inventory.keySet()) {
      data.writeInt(Item.getIdFromItem(key.getItem()));
      data.writeInt(key.meta);
      data.writeInt(((Integer)inventory.get(key)).intValue());
    } 
  }
  
  private static void writeItemStack(ItemStack par1ItemStack, PacketBuffer par2PacketBuffer) {
    if (par1ItemStack == null) {
      par2PacketBuffer.writeShort(-1);
    } else {
      par2PacketBuffer.writeInt(Item.getIdFromItem(par1ItemStack.getItem()));
      par2PacketBuffer.writeByte(par1ItemStack.getCount());
      par2PacketBuffer.writeShort(par1ItemStack.getItemDamage());
      if (par1ItemStack.isItemStackDamageable()) {
        par2PacketBuffer.writeBoolean(true);
        writeNBTTagCompound(par1ItemStack.getTagCompound(), par2PacketBuffer);
      } else {
        par2PacketBuffer.writeBoolean(false);
      } 
    } 
  }
  
  public static void writeItemStackList(List<ItemStack> values, PacketBuffer data) {
    data.writeInt(values.size());
    for (ItemStack s : values)
      writeNullableItemStack(s, data); 
  }
  
  private static void writeNBTTagCompound(NBTTagCompound par1NBTTagCompound, PacketBuffer par2PacketBuffer) {
    if (par1NBTTagCompound == null) {
      par2PacketBuffer.writeShort(-1);
    } else {
      par2PacketBuffer.writeCompoundTag(par1NBTTagCompound);
    } 
  }
  
  public static void writeNullableBuildingLocation(BuildingLocation bl, PacketBuffer data) {
    data.writeBoolean((bl == null));
    if (bl != null) {
      data.writeBoolean(bl.isCustomBuilding);
      writeNullableString(bl.planKey, data);
      writeNullableString(bl.shop, data);
      data.writeInt(bl.minx);
      data.writeInt(bl.maxx);
      data.writeInt(bl.miny);
      data.writeInt(bl.maxy);
      data.writeInt(bl.minz);
      data.writeInt(bl.maxz);
      data.writeInt(bl.minxMargin);
      data.writeInt(bl.maxxMargin);
      data.writeInt(bl.minyMargin);
      data.writeInt(bl.maxyMargin);
      data.writeInt(bl.minzMargin);
      data.writeInt(bl.maxzMargin);
      data.writeInt(bl.orientation);
      data.writeInt(bl.length);
      data.writeInt(bl.width);
      data.writeInt(bl.level);
      data.writeInt(bl.getVariation());
      data.writeInt(bl.reputation);
      data.writeInt(bl.price);
      data.writeInt(bl.version);
      writeNullablePoint(bl.pos, data);
      writeNullablePoint(bl.chestPos, data);
      writeNullablePoint(bl.sleepingPos, data);
      writeNullablePoint(bl.sellingPos, data);
      writeNullablePoint(bl.craftingPos, data);
      writeNullablePoint(bl.shelterPos, data);
      writeNullablePoint(bl.defendingPos, data);
      writeNullableString(bl.culture.key, data);
      writeStringList(bl.subBuildings, data);
      data.writeBoolean(bl.showTownHallSigns);
      data.writeBoolean(bl.upgradesAllowed);
      data.writeBoolean(bl.bedrocklevel);
      data.writeBoolean(bl.isSubBuildingLocation);
    } 
  }
  
  public static void writeNullableBuildingProject(BuildingProject bp, PacketBuffer data) {
    data.writeBoolean((bp == null));
    if (bp != null) {
      data.writeBoolean(bp.isCustomBuilding);
      writeNullableString(bp.key, data);
      writeNullableBuildingLocation(bp.location, data);
    } 
  }
  
  public static void writeNullableGoods(TradeGood g, PacketBuffer data) {
    data.writeBoolean((g == null));
    if (g != null) {
      data.writeString(g.culture.key);
      data.writeInt(Item.getIdFromItem(g.item.getItem()));
      data.writeByte(g.item.meta);
      writeNullableString(g.requiredTag, data);
      writeNullableString(g.travelBookCategory, data);
      data.writeBoolean(g.autoGenerate);
      data.writeInt(g.minReputation);
    } 
  }
  
  public static void writeNullableItemStack(ItemStack is, PacketBuffer data) {
    data.writeBoolean((is == null));
    if (is != null)
      writeItemStack(is, data); 
  }
  
  public static void writeNullablePoint(Point p, PacketBuffer data) {
    data.writeBoolean((p == null));
    if (p != null) {
      data.writeInt(p.getiX());
      data.writeInt(p.getiY());
      data.writeInt(p.getiZ());
    } 
  }
  
  public static void writeNullablePuja(PujaSacrifice puja, PacketBuffer data) {
    data.writeBoolean((puja == null));
    if (puja != null) {
      data.writeShort(puja.type);
      if (puja.currentTarget != null) {
        data.writeShort(Enchantment.getEnchantmentID(puja.currentTarget.enchantment));
      } else {
        data.writeShort(0);
      } 
      data.writeShort(puja.pujaProgress);
      data.writeShort(puja.offeringNeeded);
      data.writeShort(puja.offeringProgress);
    } 
  }
  
  public static void writeNullableQuestInstance(QuestInstance qi, PacketBuffer ds) {
    ds.writeBoolean((qi == null));
    if (qi != null) {
      ds.writeLong(qi.uniqueid);
      ds.writeString(qi.quest.key);
      ds.writeUniqueId(qi.profile.uuid);
      ds.writeByte(qi.currentStep);
      ds.writeLong(qi.startTime);
      ds.writeLong(qi.currentStepStart);
      ds.writeByte(qi.villagers.size());
      for (String key : qi.villagers.keySet()) {
        ds.writeString(key);
        writeNullableQuestVillager((QuestInstanceVillager)qi.villagers.get(key), ds);
      } 
    } 
  }
  
  public static void writeNullableQuestVillager(QuestInstanceVillager v, PacketBuffer data) {
    data.writeBoolean((v == null));
    if (v != null) {
      writeNullablePoint(v.townHall, data);
      data.writeLong(v.id);
    } 
  }
  
  public static void writeNullableResourceLocation(ResourceLocation rs, PacketBuffer data) {
    data.writeBoolean((rs == null));
    if (rs != null)
      data.writeString(rs.toString()); 
  }
  
  public static void writeNullableString(String s, PacketBuffer data) {
    data.writeBoolean((s == null));
    if (s != null)
      data.writeString(s); 
  }
  
  public static void writeNullableUUID(UUID uuid, PacketBuffer data) {
    data.writeBoolean((uuid == null));
    if (uuid != null)
      data.writeUniqueId(uuid); 
  }
  
  public static void writeNullableVillagerRecord(VillagerRecord vr, PacketBuffer data) {
    data.writeBoolean((vr == null));
    if (vr != null) {
      data.writeLong(vr.getVillagerId());
      writeNullableString(vr.type, data);
      writeNullableString(vr.firstName, data);
      writeNullableString(vr.familyName, data);
      writeNullableResourceLocation(vr.texture, data);
      data.writeInt(vr.nb);
      data.writeInt(vr.gender);
      data.writeInt(vr.size);
      data.writeFloat(vr.scale);
      data.writeBoolean(vr.rightHanded);
      writeNullableString((vr.getCulture()).key, data);
      writeNullableString(vr.fathersName, data);
      writeNullableString(vr.mothersName, data);
      writeNullableString(vr.spousesName, data);
      writeNullableString(vr.maidenName, data);
      data.writeBoolean(vr.killed);
      data.writeBoolean(vr.raidingVillage);
      data.writeBoolean(vr.awayraiding);
      data.writeBoolean(vr.awayhired);
      writeNullablePoint(vr.getHousePos(), data);
      writeNullablePoint(vr.getTownHallPos(), data);
      writeNullablePoint(vr.originalVillagePos, data);
      data.writeLong(vr.raiderSpawn);
      writeInventory(vr.inventory, data);
      writeStringList(vr.questTags, data);
    } 
  }
  
  public static void writePointIntegerMap(Map<Point, Integer> map, PacketBuffer data) {
    data.writeInt(map.size());
    for (Point p : map.keySet()) {
      writeNullablePoint(p, data);
      data.writeInt(((Integer)map.get(p)).intValue());
    } 
  }
  
  public static void writePointList(List<Point> points, PacketBuffer data) {
    data.writeInt(points.size());
    for (Point p : points)
      writeNullablePoint(p, data); 
  }
  
  public static void writeProjectListList(Map<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> projects, PacketBuffer data) {
    data.writeInt((BuildingProject.EnumProjects.values()).length);
    for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
      if (projects.containsKey(ep)) {
        data.writeInt(((CopyOnWriteArrayList)projects.get(ep)).size());
        for (BuildingProject bp : projects.get(ep))
          writeNullableBuildingProject(bp, data); 
      } else {
        data.writeInt(0);
      } 
    } 
  }
  
  public static void writeStringCollection(Collection<String> strings, PacketBuffer data) {
    data.writeInt(strings.size());
    for (String s : strings)
      writeNullableString(s, data); 
  }
  
  public static void writeStringList(List<String> strings, PacketBuffer data) {
    data.writeInt(strings.size());
    for (String s : strings)
      writeNullableString(s, data); 
  }
  
  public static void writeStringStringArray(String[][] strings, PacketBuffer data) {
    data.writeInt(strings.length);
    for (String[] array : strings) {
      data.writeInt(array.length);
      for (String s : array)
        writeNullableString(s, data); 
    } 
  }
  
  public static void writeStringStringListMap(Map<String, List<String>> strings, PacketBuffer data) {
    if (strings == null) {
      data.writeInt(0);
      return;
    } 
    data.writeInt(strings.size());
    for (String key : strings.keySet()) {
      data.writeString(key);
      writeStringList(strings.get(key), data);
    } 
  }
  
  public static void writeStringStringMap(Map<String, String> strings, PacketBuffer data) {
    if (strings == null) {
      data.writeInt(0);
      return;
    } 
    data.writeInt(strings.size());
    for (String s : strings.keySet()) {
      data.writeString(s);
      writeNullableString(strings.get(s), data);
    } 
  }
  
  public static void writeVillagerRecordList(List<VillagerRecord> vrecords, PacketBuffer data) {
    data.writeInt(vrecords.size());
    for (VillagerRecord vr : vrecords)
      writeNullableVillagerRecord(vr, data); 
  }
  
  public static void writeVillagerRecordMap(Map<Long, VillagerRecord> vrecords, PacketBuffer data) {
    if (vrecords == null) {
      data.writeInt(0);
      return;
    } 
    data.writeInt(vrecords.size());
    for (Long id : vrecords.keySet()) {
      data.writeLong(id.longValue());
      writeNullableVillagerRecord(vrecords.get(id), data);
    } 
  }
}
