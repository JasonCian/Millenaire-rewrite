package org.millenaire.common.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.world.MillWorldData;

public class VillagerRecord implements Cloneable {
  private static final double RIGHT_HANDED_CHANCE = 0.8D;
  
  private Culture culture;
  
  public static VillagerRecord createVillagerRecord(Culture c, String type, MillWorldData worldData, Point housePos, Point thPos, String firstName, String familyName, long villagerId, boolean mockVillager) {
    if (!mockVillager && (
      worldData.world.isRemote || !(worldData.world instanceof net.minecraft.world.WorldServer))) {
      MillLog.printException("Tried creating a villager record in client world: " + worldData.world, new Exception());
      return null;
    } 
    VillagerRecord villagerRecord = new VillagerRecord(worldData);
    if (type == null || type.length() == 0)
      MillLog.error(null, "Tried creating villager of null type: " + type); 
    if (c.getVillagerType(type.toLowerCase()) == null)
      for (Culture c2 : Culture.ListCultures) {
        if (c2.getVillagerType(type) != null) {
          MillLog.error(null, "Could not find villager type " + type + " in culture " + c.key + " but could in " + c2.key + " so switching.");
          c = c2;
        } 
      }  
    villagerRecord.setCulture(c);
    if (c.getVillagerType(type.toLowerCase()) != null) {
      VillagerType vtype = c.getVillagerType(type.toLowerCase());
      villagerRecord.type = vtype.key;
      if (!mockVillager) {
        villagerRecord.setHousePos(housePos);
        villagerRecord.setTownHallPos(thPos);
      } 
      if (familyName != null) {
        villagerRecord.familyName = familyName;
      } else {
        Set<String> namesTaken;
        if (thPos != null) {
          namesTaken = worldData.getBuilding(thPos).getAllFamilyNames();
        } else {
          namesTaken = new HashSet<>();
        } 
        villagerRecord.familyName = vtype.getRandomFamilyName(namesTaken);
      } 
      if (firstName != null) {
        villagerRecord.firstName = firstName;
      } else {
        villagerRecord.firstName = vtype.getRandomFirstName();
      } 
      if (villagerId == -1L) {
        villagerRecord.setVillagerId(Math.abs(MillCommonUtilities.randomLong()));
      } else {
        villagerRecord.setVillagerId(villagerId);
      } 
      villagerRecord.gender = vtype.gender;
      villagerRecord.texture = vtype.getNewTexture();
      initialisePersonalizedData(villagerRecord, vtype);
      villagerRecord.rightHanded = (MillCommonUtilities.random.nextDouble() < 0.8D);
    } else {
      MillLog.error(null, "Unknown villager type: " + type + " for culture " + c);
      return null;
    } 
    if (MillConfigValues.LogVillagerSpawn >= 1)
      MillLog.major(villagerRecord, "Created new villager record."); 
    if (!mockVillager)
      worldData.registerVillagerRecord(villagerRecord, true); 
    return villagerRecord;
  }
  
  private static void initialisePersonalizedData(VillagerRecord villagerRecord, VillagerType vtype) {
    if (vtype.isChild) {
      villagerRecord.size = 0;
      villagerRecord.scale = (villagerRecord.getType()).baseScale;
    } else {
      villagerRecord.scale = (villagerRecord.getType()).baseScale * (80.0F + MillCommonUtilities.randomInt(10)) / 100.0F;
    } 
  }
  
  public static VillagerRecord read(MillWorldData mw, NBTTagCompound nbttagcompound, String label) {
    if (!nbttagcompound.hasKey(label + "_id") && !nbttagcompound.hasKey(label + "_lid"))
      return null; 
    VillagerRecord vr = new VillagerRecord(mw, Culture.getCultureByName(nbttagcompound.getString(label + "_culture")));
    if (nbttagcompound.hasKey(label + "_lid"))
      vr.setVillagerId(Math.abs(nbttagcompound.getLong(label + "_lid"))); 
    vr.nb = nbttagcompound.getInteger(label + "_nb");
    vr.gender = nbttagcompound.getInteger(label + "_gender");
    vr.type = nbttagcompound.getString(label + "_type").toLowerCase();
    vr.raiderSpawn = nbttagcompound.getLong(label + "_raiderSpawn");
    vr.firstName = nbttagcompound.getString(label + "_firstName");
    vr.familyName = nbttagcompound.getString(label + "_familyName");
    String texture = nbttagcompound.getString(label + "_texture");
    if (texture.contains(":")) {
      vr.texture = new ResourceLocation(texture);
    } else {
      vr.texture = new ResourceLocation("millenaire", texture);
    } 
    vr.setHousePos(Point.read(nbttagcompound, label + "_housePos"));
    vr.setTownHallPos(Point.read(nbttagcompound, label + "_townHallPos"));
    vr.originalId = nbttagcompound.getLong(label + "_originalId");
    vr.originalVillagePos = Point.read(nbttagcompound, label + "_originalVillagePos");
    vr.size = nbttagcompound.getInteger(label + "_size");
    vr.scale = nbttagcompound.getFloat(label + "_scale");
    if (nbttagcompound.hasKey(label + "_rightHanded"))
      vr.rightHanded = nbttagcompound.getBoolean(label + "_rightHanded"); 
    vr.fathersName = nbttagcompound.getString(label + "_fathersName");
    vr.mothersName = nbttagcompound.getString(label + "_mothersName");
    vr.maidenName = nbttagcompound.getString(label + "_maidenName");
    vr.spousesName = nbttagcompound.getString(label + "_spousesName");
    vr.killed = nbttagcompound.getBoolean(label + "_killed");
    vr.raidingVillage = nbttagcompound.getBoolean(label + "_raidingVillage");
    vr.awayraiding = nbttagcompound.getBoolean(label + "_awayraiding");
    vr.awayhired = nbttagcompound.getBoolean(label + "_awayhired");
    NBTTagList nbttaglist = nbttagcompound.getTagList(label + "questTags", 10);
    int i;
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      vr.questTags.add(nbttagcompound1.getString("tag"));
    } 
    nbttaglist = nbttagcompound.getTagList(label + "_inventory", 10);
    for (i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      vr.inventory.put(InvItem.createInvItem(Item.getItemById(nbttagcompound1.getInteger("item")), nbttagcompound1.getInteger("meta")), Integer.valueOf(nbttagcompound1.getInteger("amount")));
    } 
    nbttaglist = nbttagcompound.getTagList(label + "_inventoryNew", 10);
    MillCommonUtilities.readInventory(nbttaglist, vr.inventory);
    if (vr.getType() == null) {
      MillLog.error(vr, "Could not find type " + vr.type + " for VR. Skipping.");
      return null;
    } 
    if (vr.scale == 0.0F || vr.scale == 1.0F)
      initialisePersonalizedData(vr, vr.getType()); 
    return vr;
  }
  
  public String fathersName = "";
  
  public String mothersName = "";
  
  public String spousesName = "";
  
  public String maidenName = "";
  
  public boolean flawedRecord = false;
  
  public boolean killed = false;
  
  public boolean raidingVillage = false;
  
  public boolean awayraiding = false;
  
  public boolean awayhired = false;
  
  private Point housePos;
  
  private Point townHallPos;
  
  public Point originalVillagePos;
  
  private long villagerId;
  
  public long raiderSpawn = 0L;
  
  public int nb;
  
  public int gender;
  
  public int size;
  
  public float scale = 1.0F;
  
  public boolean rightHanded = true;
  
  public HashMap<InvItem, Integer> inventory = new HashMap<>();
  
  public List<String> questTags = new ArrayList<>();
  
  public String type;
  
  public String firstName;
  
  public String familyName;
  
  public ResourceLocation texture;
  
  private Building house;
  
  private Building townHall;
  
  private Building originalVillage;
  
  public MillWorldData mw;
  
  private long originalId = -1L;
  
  public VillagerRecord(MillWorldData mw) {
    this.mw = mw;
  }
  
  private VillagerRecord(MillWorldData mw, Culture c) {
    setCulture(c);
    this.mw = mw;
  }
  
  public VillagerRecord(MillWorldData mw, MillVillager v) {
    this.mw = mw;
    setCulture(v.getCulture());
    setVillagerId(v.getVillagerId());
    if (v.vtype != null)
      this.type = v.vtype.key; 
    this.firstName = v.firstName;
    this.familyName = v.familyName;
    this.gender = v.gender;
    this.nb = 1;
    this.texture = v.getTexture();
    setHousePos(v.housePoint);
    setTownHallPos(v.townHallPoint);
    this.raidingVillage = v.isRaider;
    for (InvItem iv : v.getInventoryKeys())
      this.inventory.put(iv, Integer.valueOf(v.countInv(iv))); 
    if (getHousePos() == null) {
      MillLog.error(this, "Creation constructor: House position in record is null.");
      this.flawedRecord = true;
    } 
  }
  
  public VillagerRecord clone() {
    try {
      return (VillagerRecord)super.clone();
    } catch (CloneNotSupportedException e) {
      MillLog.printException(e);
      return null;
    } 
  }
  
  public int countInv(InvItem invItem) {
    if (this.inventory.containsKey(invItem))
      return ((Integer)this.inventory.get(invItem)).intValue(); 
    return 0;
  }
  
  public int countInv(Item item) {
    return countInv(item, 0);
  }
  
  public int countInv(Item item, int meta) {
    InvItem key = InvItem.createInvItem(item, meta);
    return countInv(key);
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof VillagerRecord))
      return false; 
    VillagerRecord other = (VillagerRecord)obj;
    return (other.getVillagerId() == getVillagerId());
  }
  
  public VillagerRecord generateRaidRecord(Building target) {
    VillagerRecord raidRecord = clone();
    raidRecord.setVillagerId(Math.abs(MillCommonUtilities.randomLong()));
    raidRecord.setHousePos(target.getPos());
    raidRecord.setTownHallPos(target.getTownHall().getPos());
    raidRecord.townHall = target.getTownHall();
    raidRecord.house = target;
    raidRecord.raidingVillage = true;
    raidRecord.awayraiding = false;
    raidRecord.originalVillagePos = getTownHall().getPos();
    raidRecord.originalId = getVillagerId();
    raidRecord.raiderSpawn = (getTownHall()).world.getWorldTime();
    return raidRecord;
  }
  
  public InvItem getArmourPiece(EntityEquipmentSlot slotIn) {
    if (slotIn == EntityEquipmentSlot.HEAD) {
      for (InvItem item : (getConfig()).armoursHelmetSorted) {
        if (countInv(item) > 0)
          return item; 
      } 
      return null;
    } 
    if (slotIn == EntityEquipmentSlot.CHEST) {
      for (InvItem item : (getConfig()).armoursChestplateSorted) {
        if (countInv(item) > 0)
          return item; 
      } 
      return null;
    } 
    if (slotIn == EntityEquipmentSlot.LEGS) {
      for (InvItem item : (getConfig()).armoursLeggingsSorted) {
        if (countInv(item) > 0)
          return item; 
      } 
      return null;
    } 
    if (slotIn == EntityEquipmentSlot.FEET) {
      for (InvItem item : (getConfig()).armoursBootsSorted) {
        if (countInv(item) > 0)
          return item; 
      } 
      return null;
    } 
    return null;
  }
  
  public Item getBestMeleeWeapon() {
    double max = 1.0D;
    Item best = null;
    for (InvItem item : this.inventory.keySet()) {
      if (((Integer)this.inventory.get(item)).intValue() > 0) {
        if (item.getItem() == null) {
          MillLog.error(this, "Attempting to check null melee weapon with id: " + this.inventory.get(item));
          continue;
        } 
        if (MillCommonUtilities.getItemWeaponDamage(item.getItem()) > max) {
          max = MillCommonUtilities.getItemWeaponDamage(item.getItem());
          best = item.getItem();
        } 
      } 
    } 
    if (getType() != null && (getType()).startingWeapon != null && 
      MillCommonUtilities.getItemWeaponDamage((getType()).startingWeapon.getItem()) > max) {
      max = MillCommonUtilities.getItemWeaponDamage((getType()).startingWeapon.getItem());
      best = (getType()).startingWeapon.getItem();
    } 
    return best;
  }
  
  public VillagerConfig getConfig() {
    return (getType()).villagerConfig;
  }
  
  public Culture getCulture() {
    return this.culture;
  }
  
  public String getGameOccupation() {
    if (getCulture() == null || getCulture().getVillagerType(this.type) == null)
      return ""; 
    String s = (getCulture().getVillagerType(this.type)).name;
    if (getCulture().canReadVillagerNames()) {
      String game = getCulture().getCultureString("villager." + getNameKey());
      if (!game.equals(""))
        s = s + " (" + game + ")"; 
    } 
    return s;
  }
  
  public Building getHouse() {
    if (this.house != null)
      return this.house; 
    if (MillConfigValues.LogVillager >= 3)
      MillLog.debug(this, "Seeking uncached house"); 
    this.house = this.mw.getBuilding(getHousePos());
    return this.house;
  }
  
  public Point getHousePos() {
    return this.housePos;
  }
  
  public int getMaxHealth() {
    if (getType() == null)
      return 20; 
    if ((getType()).isChild)
      return 10 + this.size / 2; 
    return (getType()).health;
  }
  
  public int getMilitaryStrength() {
    int strength = getMaxHealth() / 2;
    int attack = (getType()).baseAttackStrength;
    Item bestMelee = getBestMeleeWeapon();
    if (bestMelee != null)
      attack = (int)(attack + MillCommonUtilities.getItemWeaponDamage(bestMelee)); 
    strength += attack * 2;
    if (((getType()).isArcher && countInv((Item)Items.BOW) > 0) || countInv((Item)MillItems.YUMI_BOW) > 0)
      strength += 10; 
    strength += getTotalArmorValue() * 2;
    return strength;
  }
  
  public String getName() {
    return this.firstName + " " + this.familyName;
  }
  
  public String getNameKey() {
    if ((getType()).isChild && this.size == 20)
      return (getType()).altkey; 
    return (getType()).key;
  }
  
  public String getNativeOccupationName() {
    if ((getType()).isChild && this.size == 20)
      return (getType()).altname; 
    return (getType()).name;
  }
  
  public long getOriginalId() {
    return this.originalId;
  }
  
  public Building getOriginalVillage() {
    if (this.originalVillage != null)
      return this.originalVillage; 
    if (MillConfigValues.LogVillager >= 3)
      MillLog.debug(this, "Seeking uncached originalVillage"); 
    this.originalVillage = this.mw.getBuilding(this.originalVillagePos);
    return this.originalVillage;
  }
  
  public int getTotalArmorValue() {
    int total = 0;
    for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET }) {
      InvItem armour = getArmourPiece(slot);
      if (armour != null && armour.getItem() instanceof ItemArmor)
        total += ((ItemArmor)armour.getItem()).damageReduceAmount; 
    } 
    return total;
  }
  
  public Building getTownHall() {
    if (this.townHall != null)
      return this.townHall; 
    if (MillConfigValues.LogVillager >= 3)
      MillLog.debug(this, "Seeking uncached townHall"); 
    this.townHall = this.mw.getBuilding(getTownHallPos());
    return this.townHall;
  }
  
  public Point getTownHallPos() {
    return this.townHallPos;
  }
  
  public VillagerType getType() {
    if (getCulture().getVillagerType(this.type) == null)
      for (Culture c : Culture.ListCultures) {
        if (c.getVillagerType(this.type) != null) {
          MillLog.error(this, "Could not find villager type " + this.type + " in culture " + (getCulture()).key + " but could in " + c.key + " so switching.");
          setCulture(c);
        } 
      }  
    return getCulture().getVillagerType(this.type);
  }
  
  public long getVillagerId() {
    return this.villagerId;
  }
  
  public int hashCode() {
    return Long.valueOf(getVillagerId()).hashCode();
  }
  
  public boolean isTextureValid(String texture) {
    if (getType() != null)
      return getType().isTextureValid(texture); 
    return true;
  }
  
  public boolean matches(MillVillager v) {
    return (getVillagerId() == v.getVillagerId());
  }
  
  public void setCulture(Culture culture) {
    this.culture = culture;
  }
  
  public void setHousePos(Point housePos) {
    this.housePos = housePos;
    this.house = null;
  }
  
  public void setTownHallPos(Point townHallPos) {
    this.townHallPos = townHallPos;
    this.townHall = null;
  }
  
  public void setVillagerId(long id) {
    this.villagerId = id;
  }
  
  public String toString() {
    return this.firstName + " " + this.familyName + "/" + this.type + "/" + getVillagerId();
  }
  
  public void updateRecord(MillVillager v) {
    if (v.vtype != null)
      this.type = v.vtype.key; 
    this.firstName = v.firstName;
    this.familyName = v.familyName;
    this.gender = v.gender;
    this.nb = 1;
    this.texture = v.getTexture();
    setHousePos(v.housePoint);
    setTownHallPos(v.townHallPoint);
    this.raidingVillage = v.isRaider;
    this.killed = v.isReallyDead();
    if (getHousePos() == null) {
      MillLog.error(this, "updateRecord(): House position in record is null.");
      this.flawedRecord = true;
    } 
    this.inventory.clear();
    for (InvItem iv : v.getInventoryKeys())
      this.inventory.put(iv, Integer.valueOf(v.countInv(iv))); 
  }
  
  public void write(NBTTagCompound nbttagcompound, String label) {
    nbttagcompound.setLong(label + "_lid", getVillagerId());
    nbttagcompound.setInteger(label + "_nb", this.nb);
    nbttagcompound.setString(label + "_type", this.type);
    nbttagcompound.setString(label + "_firstName", this.firstName);
    nbttagcompound.setString(label + "_familyName", this.familyName);
    if (this.fathersName != null && this.fathersName.length() > 0)
      nbttagcompound.setString(label + "_fathersName", this.fathersName); 
    if (this.mothersName != null && this.mothersName.length() > 0)
      nbttagcompound.setString(label + "_mothersName", this.mothersName); 
    if (this.maidenName != null && this.maidenName.length() > 0)
      nbttagcompound.setString(label + "_maidenName", this.maidenName); 
    if (this.spousesName != null && this.spousesName.length() > 0)
      nbttagcompound.setString(label + "_spousesName", this.spousesName); 
    nbttagcompound.setInteger(label + "_gender", this.gender);
    nbttagcompound.setString(label + "_texture", this.texture.toString());
    nbttagcompound.setBoolean(label + "_killed", this.killed);
    nbttagcompound.setBoolean(label + "_raidingVillage", this.raidingVillage);
    nbttagcompound.setBoolean(label + "_awayraiding", this.awayraiding);
    nbttagcompound.setBoolean(label + "_awayhired", this.awayhired);
    nbttagcompound.setLong(label + "_raiderSpawn", this.raiderSpawn);
    if (getHousePos() != null)
      getHousePos().write(nbttagcompound, label + "_housePos"); 
    if (getTownHallPos() != null)
      getTownHallPos().write(nbttagcompound, label + "_townHallPos"); 
    nbttagcompound.setLong(label + "_originalId", this.originalId);
    if (this.originalVillagePos != null)
      this.originalVillagePos.write(nbttagcompound, label + "_originalVillagePos"); 
    nbttagcompound.setInteger(label + "_size", this.size);
    nbttagcompound.setFloat(label + "_scale", this.scale);
    nbttagcompound.setBoolean(label + "_rightHanded", this.rightHanded);
    NBTTagList nbttaglist = new NBTTagList();
    for (String tag : this.questTags) {
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("tag", tag);
      nbttaglist.appendTag((NBTBase)nbttagcompound1);
    } 
    nbttagcompound.setTag(label + "questTags", (NBTBase)nbttaglist);
    nbttaglist = MillCommonUtilities.writeInventory(this.inventory);
    nbttagcompound.setTag(label + "_inventoryNew", (NBTBase)nbttaglist);
    nbttagcompound.setString(label + "_culture", (getCulture()).key);
  }
}
