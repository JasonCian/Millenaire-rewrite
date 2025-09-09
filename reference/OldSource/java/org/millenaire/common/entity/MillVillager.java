package org.millenaire.common.entity;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.CultureLanguage;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemClothes;
import org.millenaire.common.item.ItemMillenaireBow;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.DevModUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public abstract class MillVillager extends EntityCreature implements IEntityAdditionalSpawnData, IAStarPathedEntity {
  public static class EntityGenericAsymmFemale extends MillVillager {
    public EntityGenericAsymmFemale(World world) {
      super(world);
    }
  }
  
  public static class EntityGenericMale extends MillVillager {
    public EntityGenericMale(World world) {
      super(world);
    }
  }
  
  public static class EntityGenericSymmFemale extends MillVillager {
    public EntityGenericSymmFemale(World world) {
      super(world);
    }
  }
  
  public static class InvItemAlphabeticalComparator implements Comparator<InvItem> {
    public int compare(InvItem arg0, InvItem arg1) {
      return arg0.getName().compareTo(arg1.getName());
    }
  }
  
  private static final UUID SPRINT_SPEED_BOOST_ID = UUID.fromString("B9766B59-8456-5632-BC1F-2EE2A276D836");
  
  private static final AttributeModifier SPRINT_SPEED_BOOST = new AttributeModifier(SPRINT_SPEED_BOOST_ID, "Sprint speed boost", 0.1D, 1);
  
  private static final double DEFAULT_MOVE_SPEED = 0.5D;
  
  public static final int ATTACK_RANGE_DEFENSIVE = 20;
  
  private static final String FREE_CLOTHES = "free";
  
  private static final String NATURAL = "natural";
  
  private static final int CONCEPTION_CHANCE = 2;
  
  private static final int VISITOR_NB_NIGHTS_BEFORE_LEAVING = 5;
  
  public static final int MALE = 1;
  
  public static final int FEMALE = 2;
  
  public static final ResourceLocation GENERIC_VILLAGER = new ResourceLocation("millenaire", "GenericVillager");
  
  public static final ResourceLocation GENERIC_ASYMM_FEMALE = new ResourceLocation("millenaire", "GenericAsimmFemale");
  
  public static final ResourceLocation GENERIC_SYMM_FEMALE = new ResourceLocation("millenaire", "GenericSimmFemale");
  
  public static final ResourceLocation GENERIC_ZOMBIE = new ResourceLocation("millenaire", "GenericZombie");
  
  private static ItemStack[] WOODDEN_HOE_STACK = new ItemStack[] { new ItemStack(Items.WOODEN_HOE, 1) };
  
  private static ItemStack[] WOODDEN_SHOVEL_STACK = new ItemStack[] { new ItemStack(Items.WOODEN_SHOVEL, 1) };
  
  private static ItemStack[] WOODDEN_PICKAXE_STACK = new ItemStack[] { new ItemStack(Items.WOODEN_PICKAXE, 1) };
  
  private static ItemStack[] WOODDEN_AXE_STACK = new ItemStack[] { new ItemStack(Items.WOODEN_AXE, 1) };
  
  static final int GATHER_RANGE = 20;
  
  private static final int HOLD_DURATION = 20;
  
  public static final int ATTACK_RANGE = 80;
  
  public static final int ARCHER_RANGE = 20;
  
  public static final int MAX_CHILD_SIZE = 20;
  
  private static final AStarConfig JPS_CONFIG_DEFAULT = new AStarConfig(true, false, false, false, true);
  
  private static final AStarConfig JPS_CONFIG_NO_LEAVES = new AStarConfig(true, false, false, false, false);
  
  public VillagerType vtype;
  
  public static MillVillager createMockVillager(VillagerRecord villagerRecord, World world) {
    MillVillager villager = (MillVillager)EntityList.createEntityByIDFromName(villagerRecord.getType().getEntityName(), world);
    if (villager == null) {
      MillLog.error(villagerRecord, "Could not create mock villager of dynamic type: " + villagerRecord.getType() + " entity: " + villagerRecord.getType().getEntityName());
      return null;
    } 
    villager.vtype = villagerRecord.getType();
    villager.gender = (villagerRecord.getType()).gender;
    villager.firstName = villagerRecord.firstName;
    villager.familyName = villagerRecord.familyName;
    villager.texture = villagerRecord.texture;
    villager.setHealth(villager.getMaxHealth());
    villager.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((villagerRecord.getType()).health);
    villager.updateClothTexturePath();
    return villager;
  }
  
  public static MillVillager createVillager(VillagerRecord villagerRecord, World world, Point spawnPos, boolean respawn) {
    if (world.isRemote || !(world instanceof net.minecraft.world.WorldServer)) {
      MillLog.printException("Tried creating a villager in client world: " + world, new Exception());
      return null;
    } 
    if (villagerRecord == null) {
      MillLog.error(villagerRecord, "Tried creating villager from a null record");
      return null;
    } 
    if (villagerRecord.getType() == null) {
      MillLog.error(null, "Tried creating villager of null type: " + villagerRecord.getType());
      return null;
    } 
    MillVillager villager = (MillVillager)EntityList.createEntityByIDFromName(villagerRecord.getType().getEntityName(), world);
    if (villager == null) {
      MillLog.error(villagerRecord, "Could not create villager of dynamic type: " + villagerRecord.getType() + " entity: " + villagerRecord.getType().getEntityName());
      return null;
    } 
    villager.housePoint = villagerRecord.getHousePos();
    villager.townHallPoint = villagerRecord.getTownHallPos();
    villager.vtype = villagerRecord.getType();
    villager.setVillagerId(villagerRecord.getVillagerId());
    villager.gender = (villagerRecord.getType()).gender;
    villager.firstName = villagerRecord.firstName;
    villager.familyName = villagerRecord.familyName;
    villager.texture = villagerRecord.texture;
    villager.setHealth(villager.getMaxHealth());
    villager.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((villagerRecord.getType()).health);
    villager.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((villagerRecord.getType()).baseSpeed);
    villager.updateClothTexturePath();
    if (!respawn)
      for (InvItem item : (villagerRecord.getType()).startingInv.keySet())
        villager.addToInv(item.getItem(), item.meta, ((Integer)(villagerRecord.getType()).startingInv.get(item)).intValue());  
    villager.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
    if (MillConfigValues.LogVillagerSpawn >= 1)
      MillLog.major(villager, "Created new villager from record."); 
    return villager;
  }
  
  public static void readVillagerPacket(PacketBuffer data) {
    try {
      long villager_id = data.readLong();
      if (Mill.clientWorld.getVillagerById(villager_id) != null) {
        Mill.clientWorld.getVillagerById(villager_id).readVillagerStreamdata(data);
      } else if (MillConfigValues.LogNetwork >= 2) {
        MillLog.minor(null, "readVillagerPacket for unknown villager: " + villager_id);
      } 
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public int action = 0;
  
  public String goalKey = null;
  
  private Goal.GoalInformation goalInformation = null;
  
  private Point pathDestPoint;
  
  private Building house = null;
  
  private Building townHall = null;
  
  public Point housePoint = null;
  
  public Point prevPoint = null;
  
  public Point townHallPoint = null;
  
  public boolean extraLog = false;
  
  public String firstName = "";
  
  public String familyName = "";
  
  public ItemStack heldItem = ItemStack.EMPTY;
  
  public ItemStack heldItemOffHand = ItemStack.EMPTY;
  
  public long timer = 0L;
  
  public long actionStart = 0L;
  
  public boolean allowRandomMoves = false;
  
  public boolean stopMoving = false;
  
  public int gender = 0;
  
  public boolean registered = false;
  
  public int longDistanceStuck;
  
  public boolean nightActionPerformed = false;
  
  public long speech_started = 0L;
  
  public HashMap<InvItem, Integer> inventory;
  
  public Block previousBlock;
  
  public int previousBlockMeta;
  
  public long pathingTime;
  
  public long timeSinceLastPathingTimeDisplay;
  
  private long villagerId = -1L;
  
  public int nbPathsCalculated = 0;
  
  public int nbPathNoStart = 0;
  
  public int nbPathNoEnd = 0;
  
  public int nbPathAborted = 0;
  
  public int nbPathFailure = 0;
  
  public long goalStarted = 0L;
  
  public int constructionJobId = -1;
  
  public int heldItemCount = 0;
  
  public int heldItemId = -1;
  
  public int heldItemOffHandId = -1;
  
  public String speech_key = null;
  
  public int speech_variant = 0;
  
  public String dialogueKey = null;
  
  public int dialogueRole = 0;
  
  public long dialogueStart = 0L;
  
  public char dialogueColour = 'f';
  
  public boolean dialogueChat = false;
  
  public String dialogueTargetFirstName = null;
  
  public String dialogueTargetLastName = null;
  
  private Point doorToClose = null;
  
  public int visitorNbNights = 0;
  
  public int foreignMerchantStallId = -1;
  
  public boolean lastAttackByPlayer = false;
  
  public HashMap<Goal, Long> lastGoalTime = new HashMap<>();
  
  public String hiredBy = null;
  
  public boolean aggressiveStance = false;
  
  public long hiredUntil = 0L;
  
  public boolean isUsingBow;
  
  public boolean isUsingHandToHand;
  
  public boolean isRaider = false;
  
  public AStarPathPlannerJPS pathPlannerJPS;
  
  public AS_PathEntity pathEntity;
  
  public int updateCounter = 0;
  
  public long client_lastupdated;
  
  public MillWorldData mw;
  
  private boolean pathFailedSincelastTick = false;
  
  private List<AStarNode> pathCalculatedSinceLastTick = null;
  
  private int localStuck = 0;
  
  private final ResourceLocation[] clothTexture = new ResourceLocation[2];
  
  private String clothName = null;
  
  public boolean shouldLieDown = false;
  
  public LinkedHashMap<TradeGood, Integer> merchantSells = new LinkedHashMap<>();
  
  public ResourceLocation texture = null;
  
  private int attackTime;
  
  public boolean isDeadOnServer = false;
  
  public boolean travelBookMockVillager = false;
  
  public MillVillager(World world) {
    super(world);
    this.world = world;
    this.mw = Mill.getMillWorld(world);
    this.inventory = new HashMap<>();
    setHealth(getMaxHealth());
    this.isImmuneToFire = true;
    this.client_lastupdated = world.getWorldTime();
    if (!world.isRemote)
      this.pathPlannerJPS = new AStarPathPlannerJPS(world, this, MillConfigValues.jpsPathing); 
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    if (MillConfigValues.LogVillagerSpawn >= 3) {
      Exception e = new Exception();
      MillLog.printException("Creating villager " + this + " in world: " + world, e);
    } 
  }
  
  public void addToInv(Block block, int nb) {
    addToInv(Item.getItemFromBlock(block), 0, nb);
  }
  
  public void addToInv(Block block, int meta, int nb) {
    addToInv(Item.getItemFromBlock(block), meta, nb);
  }
  
  public void addToInv(IBlockState bs, int nb) {
    addToInv(Item.getItemFromBlock(bs.getBlock()), bs.getBlock().getMetaFromState(bs), nb);
  }
  
  public void addToInv(InvItem iv, int nb) {
    addToInv(iv.getItem(), iv.meta, nb);
  }
  
  public void addToInv(Item item, int nb) {
    addToInv(item, 0, nb);
  }
  
  public void addToInv(Item item, int meta, int nb) {
    InvItem key = InvItem.createInvItem(item, meta);
    if (this.inventory.containsKey(key)) {
      this.inventory.put(key, Integer.valueOf(((Integer)this.inventory.get(key)).intValue() + nb));
    } else {
      this.inventory.put(key, Integer.valueOf(nb));
    } 
    updateVillagerRecord();
    updateClothTexturePath();
  }
  
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(computeMaxHealth());
  }
  
  private void applyPathCalculatedSinceLastTick() {
    try {
      AS_PathEntity path = AStarStatic.translateAStarPathtoPathEntity(this.world, this.pathCalculatedSinceLastTick, getPathingConfig());
      registerNewPath(path);
    } catch (Exception e) {
      MillLog.printException("Exception when finding JPS path:", e);
    } 
    this.pathCalculatedSinceLastTick = null;
  }
  
  public boolean attackEntity(Entity entity) {
    double distance = getPos().distanceTo(entity);
    if (this.vtype.isArcher && distance > 5.0D && hasBow()) {
      this.isUsingBow = true;
      attackEntity_testHiredGoon(entity);
      if (distance < 20.0D && entity instanceof EntityLivingBase)
        if (this.attackTime <= 0) {
          this.attackTime = 100;
          swingArm(EnumHand.MAIN_HAND);
          float distanceFactor = (float)(distance / 20.0D);
          distanceFactor = MathHelper.clamp(distanceFactor, 0.1F, 1.0F);
          attackEntityWithRangedAttack((EntityLivingBase)entity, distanceFactor);
        } else {
          this.attackTime--;
        }  
    } else {
      if (this.attackTime <= 0 && distance < 2.0D && (entity.getEntityBoundingBox()).maxY > (getEntityBoundingBox()).minY && (entity.getEntityBoundingBox()).minY < (getEntityBoundingBox()).maxY) {
        this.attackTime = 20;
        swingArm(EnumHand.MAIN_HAND);
        attackEntity_testHiredGoon(entity);
        return entity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)this), getAttackStrength());
      } 
      this.attackTime--;
      this.isUsingHandToHand = true;
    } 
    return true;
  }
  
  private void attackEntity_testHiredGoon(Entity targetedEntity) {
    if (targetedEntity instanceof EntityPlayer && this.hiredBy != null) {
      EntityPlayer owner = this.world.getPlayerEntityByName(this.hiredBy);
      if (owner != null && owner != targetedEntity)
        MillAdvancements.MP_HIREDGOON.grant(owner); 
    } 
  }
  
  public boolean attackEntityFrom(DamageSource ds, float i) {
    if (ds.getTrueSource() == null && ds != DamageSource.OUT_OF_WORLD)
      return false; 
    boolean hadFullHealth = (getMaxHealth() == getHealth());
    boolean b = super.attackEntityFrom(ds, i);
    Entity entity = ds.getTrueSource();
    this.lastAttackByPlayer = false;
    if (entity != null && 
      entity instanceof EntityLivingBase)
      if (entity instanceof EntityPlayer) {
        if (!((EntityPlayer)entity).isSpectator() && !((EntityPlayer)entity).isCreative()) {
          this.lastAttackByPlayer = true;
          EntityPlayer player = (EntityPlayer)entity;
          if (!this.isRaider) {
            if (this.vtype != null && !this.vtype.hostile) {
              UserProfile serverProfile = VillageUtilities.getServerProfile(player.world, player);
              if (serverProfile != null)
                serverProfile.adjustReputation(getTownHall(), (int)(-i * 10.0F)); 
            } 
            if (this.world.getDifficulty() != EnumDifficulty.PEACEFUL && getHealth() < getMaxHealth() - 10.0F) {
              setAttackTarget((EntityLivingBase)entity);
              clearGoal();
              if (getTownHall() != null)
                getTownHall().callForHelp((EntityLivingBase)entity); 
            } 
            if (this.vtype != null && !this.vtype.hostile && hadFullHealth && (player
              .getHeldItem(EnumHand.MAIN_HAND) == null || MillCommonUtilities.getItemWeaponDamage(player.getActiveItemStack().getItem()) <= 1.0D) && !this.world.isRemote)
              ServerSender.sendTranslatedSentence(player, '6', "ui.communicationexplanations", new String[0]); 
          } 
          if (this.lastAttackByPlayer && getHealth() <= 0.0F)
            if (this.vtype != null && this.vtype.hostile) {
              MillAdvancements.SELF_DEFENSE.grant(player);
            } else {
              MillAdvancements.DARK_SIDE.grant(player);
            }  
        } 
      } else if (entity instanceof MillVillager) {
        MillVillager attackingVillager = (MillVillager)entity;
        if (this.isRaider != attackingVillager.isRaider || getTownHall() != attackingVillager.getTownHall()) {
          setAttackTarget((EntityLivingBase)entity);
          clearGoal();
          if (getTownHall() != null)
            getTownHall().callForHelp((EntityLivingBase)entity); 
        } 
      } else {
        setAttackTarget((EntityLivingBase)entity);
        clearGoal();
        if (getTownHall() != null)
          getTownHall().callForHelp((EntityLivingBase)entity); 
      }  
    return b;
  }
  
  public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
    EntityArrow entityarrow = getArrow(distanceFactor);
    double d0 = target.posX - this.posX;
    double d1 = (target.getEntityBoundingBox()).minY + (target.height / 3.0F) - entityarrow.posY;
    double d2 = target.posZ - this.posZ;
    double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
    float speedFactor = 1.0F;
    float damageBonus = 0.0F;
    ItemStack weapon = getWeapon();
    if (weapon != null) {
      Item item = weapon.getItem();
      if (item instanceof ItemMillenaireBow) {
        ItemMillenaireBow bow = (ItemMillenaireBow)item;
        if (bow.speedFactor > speedFactor)
          speedFactor = bow.speedFactor; 
        if (bow.damageBonus > damageBonus)
          damageBonus = bow.damageBonus; 
      } 
    } 
    entityarrow.setDamage(entityarrow.getDamage() + damageBonus);
    entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (14 - this.world.getDifficulty().ordinal() * 4) * speedFactor);
    playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
    this.world.spawnEntity((Entity)entityarrow);
  }
  
  public boolean attemptChildConception() {
    int nbChildren = 0;
    for (MillVillager villager : getHouse().getKnownVillagers()) {
      if (villager.isChild())
        nbChildren++; 
    } 
    if (nbChildren > 1) {
      if (MillConfigValues.LogChildren >= 3)
        MillLog.debug(this, "Wife already has " + nbChildren + " children, no need for more."); 
      return true;
    } 
    int nbChildVillage = getTownHall().countChildren();
    if (nbChildVillage > MillConfigValues.maxChildrenNumber) {
      if (MillConfigValues.LogChildren >= 3)
        MillLog.debug(this, "Village already has " + nbChildVillage + ", no need for more."); 
      return true;
    } 
    boolean couldMoveIn = false;
    for (Point housePoint : (getTownHall()).buildings) {
      Building house = this.mw.getBuilding(housePoint);
      if (house != null && !house.equals(getHouse()) && house.isHouse())
        if (house.canChildMoveIn(1, this.familyName) || house.canChildMoveIn(2, this.familyName))
          couldMoveIn = true;  
    } 
    if (nbChildVillage > 5 && !couldMoveIn) {
      if (MillConfigValues.LogChildren >= 3)
        MillLog.debug(this, "Village already has " + nbChildVillage + " and no slot is available for the new child."); 
      return true;
    } 
    List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(this.world, MillVillager.class, getPos(), 4, 2);
    boolean manFound = false;
    for (Entity ent : entities) {
      MillVillager villager = (MillVillager)ent;
      if (villager.gender == 1 && !villager.isChild())
        manFound = true; 
    } 
    if (!manFound)
      return false; 
    if (MillConfigValues.LogChildren >= 3)
      MillLog.debug(this, "Less than two kids and man present, trying for new child."); 
    boolean createChild = false;
    int conceptionChances = 2;
    InvItem conceptionFood = getConfig().getBestConceptionFood(getHouse());
    if (conceptionFood != null) {
      getHouse().takeGoods(conceptionFood, 1);
      conceptionChances += ((Integer)(getConfig()).foodsConception.get(conceptionFood)).intValue();
    } 
    if (MillCommonUtilities.randomInt(10) < conceptionChances) {
      createChild = true;
      if (MillConfigValues.LogChildren >= 2)
        MillLog.minor(this, "Conceiving child. Food available: " + conceptionFood); 
    } else if (MillConfigValues.LogChildren >= 2) {
      MillLog.minor(this, "Failed to conceive child. Food available: " + conceptionFood);
    } 
    if (MillConfigValues.DEV)
      createChild = true; 
    if (createChild)
      getHouse().createChild(this, getTownHall(), (getRecord()).spousesName); 
    return true;
  }
  
  public void calculateMerchantGoods() {
    for (InvItem key : this.vtype.foreignMerchantStock.keySet()) {
      if (getCulture().getTradeGood(key) != null && getBasicForeignMerchantPrice(key) > 0)
        this.merchantSells.put(getCulture().getTradeGood(key), Integer.valueOf(getBasicForeignMerchantPrice(key))); 
    } 
  }
  
  public boolean canBeLeashedTo(EntityPlayer player) {
    return false;
  }
  
  public boolean canDespawn() {
    return false;
  }
  
  public boolean canMeditate() {
    return this.vtype.canMeditate;
  }
  
  public boolean canPerformSacrifices() {
    return this.vtype.canPerformSacrifices;
  }
  
  public boolean canVillagerClearLeaves() {
    return !this.vtype.noleafclearing;
  }
  
  private void checkGoalHeldItems(Goal goal, Point target) throws Exception {
    if (this.heldItemCount > 20) {
      ItemStack[] heldItems = null;
      if (target != null && target.horizontalDistanceTo((Entity)this) < goal.range(this)) {
        heldItems = goal.getHeldItemsDestination(this);
      } else {
        heldItems = goal.getHeldItemsTravelling(this);
      } 
      if (heldItems != null && heldItems.length > 0) {
        this.heldItemId = (this.heldItemId + 1) % heldItems.length;
        this.heldItem = heldItems[this.heldItemId];
      } 
      heldItems = null;
      if (target != null && target.horizontalDistanceTo((Entity)this) < goal.range(this)) {
        heldItems = goal.getHeldItemsOffHandDestination(this);
      } else {
        heldItems = goal.getHeldItemsOffHandTravelling(this);
      } 
      if (heldItems != null && heldItems.length > 0) {
        this.heldItemOffHandId = (this.heldItemOffHandId + 1) % heldItems.length;
        this.heldItemOffHand = heldItems[this.heldItemOffHandId];
      } 
      this.heldItemCount = 0;
    } 
    if (this.heldItemCount == 0 && goal.swingArms(this))
      swingArm(EnumHand.MAIN_HAND); 
    this.heldItemCount++;
  }
  
  public void checkGoals() throws Exception {
    Goal goal = (Goal)Goal.goals.get(this.goalKey);
    if (goal == null) {
      MillLog.error(this, "Invalid goal key: " + this.goalKey);
      this.goalKey = null;
      return;
    } 
    if (getGoalDestEntity() != null)
      if ((getGoalDestEntity()).isDead) {
        setGoalDestEntity((Entity)null);
        setPathDestPoint((Point)null, 0);
      } else {
        setPathDestPoint(new Point(getGoalDestEntity()), 2);
      }  
    Point target = null;
    boolean continuingGoal = true;
    if (getPathDestPoint() != null) {
      target = getPathDestPoint();
      if (this.pathEntity != null && this.pathEntity.getCurrentPathLength() > 0)
        target = new Point(this.pathEntity.getFinalPathPoint()); 
    } 
    speakSentence(goal.sentenceKey());
    if (getGoalDestPoint() == null && getGoalDestEntity() == null) {
      goal.setVillagerDest(this);
      if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog)
        MillLog.minor(this, "Goal destination: " + getGoalDestPoint() + "/" + getGoalDestEntity()); 
    } else if (target != null && target.horizontalDistanceTo((Entity)this) < goal.range(this)) {
      if (this.actionStart == 0L) {
        this.stopMoving = goal.stopMovingWhileWorking();
        this.actionStart = this.world.getWorldTime();
        this.shouldLieDown = goal.shouldVillagerLieDown();
        if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog)
          MillLog.minor(this, "Starting action: " + this.actionStart); 
      } 
      if (this.world.getWorldTime() - this.actionStart >= goal.actionDuration(this)) {
        if (goal.performAction(this)) {
          clearGoal();
          this.goalKey = goal.nextGoal(this);
          this.stopMoving = false;
          this.shouldLieDown = false;
          this.heldItem = ItemStack.EMPTY;
          this.heldItemOffHand = ItemStack.EMPTY;
          continuingGoal = false;
          if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog)
            MillLog.minor(this, "Goal performed. Now doing: " + this.goalKey); 
        } else {
          this.stopMoving = goal.stopMovingWhileWorking();
        } 
        this.actionStart = 0L;
        this.goalStarted = this.world.getWorldTime();
      } 
    } else {
      this.stopMoving = false;
      this.shouldLieDown = false;
    } 
    if (!continuingGoal)
      return; 
    if (goal.isStillValid(this)) {
      if (this.world.getWorldTime() - this.goalStarted > goal.stuckDelay(this)) {
        boolean actionDone = goal.stuckAction(this);
        if (actionDone)
          this.goalStarted = this.world.getWorldTime(); 
        if (goal.isStillValid(this)) {
          this.allowRandomMoves = goal.allowRandomMoves();
          if (this.stopMoving) {
            this.navigator.clearPath();
            this.pathEntity = null;
          } 
          checkGoalHeldItems(goal, target);
        } 
      } else {
        checkGoalHeldItems(goal, target);
      } 
    } else {
      this.stopMoving = false;
      this.shouldLieDown = false;
      goal.onComplete(this);
      clearGoal();
      this.goalKey = goal.nextGoal(this);
      this.heldItemCount = 21;
      this.heldItemId = -1;
      this.heldItemOffHandId = -1;
    } 
  }
  
  public void clearGoal() {
    setGoalDestPoint((Point)null);
    setGoalBuildingDestPoint((Point)null);
    setGoalDestEntity((Entity)null);
    this.goalKey = null;
    this.shouldLieDown = false;
  }
  
  private boolean closeFenceGate(int i, int j, int k) {
    Point p = new Point(i, j, k);
    IBlockState state = p.getBlockActualState(this.world);
    if (BlockItemUtilities.isFenceGate(state.getBlock()) && ((Boolean)state.getValue((IProperty)BlockFenceGate.OPEN)).booleanValue()) {
      p.setBlockState(this.world, state.withProperty((IProperty)BlockFenceGate.OPEN, Boolean.valueOf(false)));
      return true;
    } 
    return false;
  }
  
  public void computeChildScale() {
    if (getRecord() == null)
      return; 
    if (getSize() == 20) {
      if (this.gender == 1) {
        (getRecord()).scale = 0.9F;
      } else {
        (getRecord()).scale = 0.8F;
      } 
    } else {
      (getRecord()).scale = 0.5F + getSize() / 100.0F;
    } 
  }
  
  public float computeMaxHealth() {
    if (this.vtype == null || getRecord() == null)
      return 40.0F; 
    if (isChild())
      return (10 + getSize()); 
    return this.vtype.health;
  }
  
  private List<PathPoint> computeNewPath(Point dest) {
    if (getPos().sameBlock(dest))
      return null; 
    try {
      if (this.goalKey != null && Goal.goals.containsKey(this.goalKey)) {
        Goal goal = (Goal)Goal.goals.get(this.goalKey);
        if (goal.range(this) >= getPos().horizontalDistanceTo(getPathDestPoint()))
          return null; 
      } 
      if (this.pathPlannerJPS.isBusy())
        this.pathPlannerJPS.stopPathSearch(true); 
      AStarNode destNode = null;
      AStarNode[] possibles = AStarStatic.getAccessNodesSorted(this.world, doubleToInt(this.posX), doubleToInt(this.posY), doubleToInt(this.posZ), getPathDestPoint().getiX(), getPathDestPoint().getiY(), 
          getPathDestPoint().getiZ(), getPathingConfig());
      if (possibles.length != 0)
        destNode = possibles[0]; 
      if (destNode != null) {
        Point startPos = getPos().getBelow();
        if (!startPos.isBlockPassable(this.world)) {
          startPos = startPos.getAbove();
          if (!startPos.isBlockPassable(this.world))
            startPos = startPos.getAbove(); 
        } 
        this.pathPlannerJPS.getPath(doubleToInt(this.posX), doubleToInt(this.posY), doubleToInt(this.posZ), destNode.x, destNode.y, destNode.z, getPathingConfig());
      } else {
        onNoPathAvailable();
      } 
    } catch (org.millenaire.common.utilities.ThreadSafeUtilities.ChunkAccessException e) {
      if (MillConfigValues.LogChunkLoader >= 2)
        MillLog.minor(this, "Chunk access violation while calculating path."); 
    } 
    return null;
  }
  
  public int countInv(Block block, int meta) {
    return countInv(InvItem.createInvItem(Item.getItemFromBlock(block), meta));
  }
  
  public int countInv(IBlockState blockState) {
    return countInv(InvItem.createInvItem(Item.getItemFromBlock(blockState.getBlock()), blockState.getBlock().getMetaFromState(blockState)));
  }
  
  public int countInv(InvItem key) {
    if (key.block == Blocks.LOG && key.meta == -1) {
      int nb = 0;
      InvItem tkey = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG), 0);
      if (this.inventory.containsKey(tkey))
        nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      tkey = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG), 1);
      if (this.inventory.containsKey(tkey))
        nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      tkey = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG), 2);
      if (this.inventory.containsKey(tkey))
        nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      tkey = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG), 3);
      if (this.inventory.containsKey(tkey))
        nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      tkey = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG2), 0);
      if (this.inventory.containsKey(tkey))
        nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      tkey = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG2), 1);
      if (this.inventory.containsKey(tkey))
        nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      return nb;
    } 
    if (key.meta == -1) {
      int nb = 0;
      for (int i = 0; i < 16; i++) {
        InvItem tkey = InvItem.createInvItem(key.item, i);
        if (this.inventory.containsKey(tkey))
          nb += ((Integer)this.inventory.get(tkey)).intValue(); 
      } 
      return nb;
    } 
    if (this.inventory.containsKey(key))
      return ((Integer)this.inventory.get(key)).intValue(); 
    return 0;
  }
  
  public int countInv(Item item) {
    return countInv(item, 0);
  }
  
  public int countInv(Item item, int meta) {
    return countInv(InvItem.createInvItem(item, meta));
  }
  
  public int countItemsAround(Item[] items, int radius) {
    List<Entity> list = WorldUtilities.getEntitiesWithinAABB(this.world, EntityItem.class, getPos(), radius, radius);
    int count = 0;
    if (list != null)
      for (int i = 0; i < list.size(); i++) {
        if (((Entity)list.get(i)).getClass() == EntityItem.class) {
          EntityItem entity = (EntityItem)list.get(i);
          if (!entity.isDead)
            for (Item id : items) {
              if (id == entity.getItem().getItem())
                count++; 
            }  
        } 
      }  
    return count;
  }
  
  public void despawnVillager() {
    if (this.world.isRemote)
      return; 
    if (this.hiredBy != null) {
      EntityPlayer owner = this.world.getPlayerEntityByName(this.hiredBy);
      if (owner != null)
        ServerSender.sendTranslatedSentence(owner, '4', "hire.hiredied", new String[] { getName() }); 
    } 
    this.mw.clearVillagerOfId(getVillagerId());
    super.setDead();
  }
  
  public void despawnVillagerSilent() {
    if (MillConfigValues.LogVillagerSpawn >= 3) {
      Exception e = new Exception();
      MillLog.printException("Despawning villager: " + this, e);
    } 
    this.mw.clearVillagerOfId(getVillagerId());
    super.setDead();
  }
  
  public void detrampleCrops() {
    if (getPos().sameBlock(this.prevPoint) && (this.previousBlock == Blocks.WHEAT || this.previousBlock instanceof org.millenaire.common.block.BlockMillCrops) && getBlock(getPos()) != Blocks.AIR && 
      getBlock(getPos().getBelow()) == Blocks.DIRT) {
      setBlock(getPos(), this.previousBlock);
      setBlockMetadata(getPos(), this.previousBlockMeta);
      setBlock(getPos().getBelow(), Blocks.FARMLAND);
    } 
    this.previousBlock = getBlock(getPos());
    this.previousBlockMeta = getBlockMeta(getPos());
  }
  
  public int doubleToInt(double input) {
    return AStarStatic.getIntCoordFromDoubleCoord(input);
  }
  
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof MillVillager))
      return false; 
    MillVillager v = (MillVillager)obj;
    return (getVillagerId() == v.villagerId);
  }
  
  public void faceEntity(Entity par1Entity, float par2, float par3) {}
  
  public void faceEntityMill(Entity entityIn, float par2, float par3) {
    getLookHelper().setLookPositionWithEntity(entityIn, par2, par3);
  }
  
  public void facePoint(Point p, float par2, float par3) {
    double x = p.x + 0.5D;
    double z = p.z + 0.5D;
    double y = p.y + 1.0D;
    getLookHelper().setLookPosition(x, y, z, 10.0F, getVerticalFaceSpeed());
  }
  
  private void foreignMerchantUpdate() {
    if (this.foreignMerchantStallId < 0)
      for (int i = 0; i < (getHouse().getResManager()).stalls.size() && this.foreignMerchantStallId < 0; i++) {
        boolean taken = false;
        for (MillVillager v : getHouse().getKnownVillagers()) {
          if (v.foreignMerchantStallId == i)
            taken = true; 
        } 
        if (!taken)
          this.foreignMerchantStallId = i; 
      }  
    if (this.foreignMerchantStallId < 0)
      this.foreignMerchantStallId = 0; 
  }
  
  private Goal getActiveGoal() {
    if (this.goalKey != null && Goal.goals.containsKey(this.goalKey))
      return (Goal)Goal.goals.get(this.goalKey); 
    return null;
  }
  
  protected EntityArrow getArrow(float distanceFactor) {
    EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, (EntityLivingBase)this);
    entitytippedarrow.setEnchantmentEffectsFromEntity((EntityLivingBase)this, distanceFactor);
    return (EntityArrow)entitytippedarrow;
  }
  
  public int getAttackStrength() {
    int attackStrength = this.vtype.baseAttackStrength;
    ItemStack weapon = getWeapon();
    if (weapon != null)
      attackStrength = (int)(attackStrength + Math.ceil(((float)MillCommonUtilities.getItemWeaponDamage(weapon.getItem()) / 2.0F))); 
    return attackStrength;
  }
  
  public int getBasicForeignMerchantPrice(InvItem item) {
    if (getTownHall() == null)
      return 0; 
    if (getCulture().getTradeGood(item) != null) {
      if (getCulture() != (getTownHall()).culture)
        return (int)((getCulture().getTradeGood(item)).foreignMerchantPrice * 1.5D); 
      return (getCulture().getTradeGood(item)).foreignMerchantPrice;
    } 
    return 0;
  }
  
  public float getBedOrientationInDegrees() {
    Point ref = getPos();
    if (getGoalDestPoint() != null)
      ref = getGoalDestPoint(); 
    Block block = WorldUtilities.getBlock(this.world, ref);
    if (block instanceof net.minecraft.block.BlockBed) {
      IBlockState state = ref.getBlockActualState(this.world);
      EnumFacing side = (EnumFacing)state.getValue((IProperty)BlockHorizontal.FACING);
      if (side == EnumFacing.SOUTH)
        return 0.0F; 
      if (side == EnumFacing.NORTH)
        return 180.0F; 
      if (side == EnumFacing.EAST)
        return 270.0F; 
      if (side == EnumFacing.WEST)
        return 90.0F; 
    } else {
      if (WorldUtilities.getBlock(this.world, ref.getSouth()) == Blocks.AIR)
        return 0.0F; 
      if (WorldUtilities.getBlock(this.world, ref.getWest()) == Blocks.AIR)
        return 90.0F; 
      if (WorldUtilities.getBlock(this.world, ref.getNorth()) == Blocks.AIR)
        return 180.0F; 
      if (WorldUtilities.getBlock(this.world, ref.getEast()) == Blocks.AIR)
        return 270.0F; 
    } 
    return 0.0F;
  }
  
  public ItemTool getBestAxe() {
    InvItem bestItem = getConfig().getBestAxe(this);
    if (bestItem != null)
      return (ItemTool)bestItem.item; 
    return (ItemTool)Items.WOODEN_AXE;
  }
  
  public ItemStack[] getBestAxeStack() {
    InvItem bestItem = getConfig().getBestAxe(this);
    if (bestItem != null)
      return bestItem.staticStackArray; 
    return WOODDEN_AXE_STACK;
  }
  
  public ItemStack[] getBestHoeStack() {
    InvItem bestItem = getConfig().getBestHoe(this);
    if (bestItem != null)
      return bestItem.staticStackArray; 
    return WOODDEN_HOE_STACK;
  }
  
  public ItemTool getBestPickaxe() {
    InvItem bestItem = getConfig().getBestPickaxe(this);
    if (bestItem != null)
      return (ItemTool)bestItem.item; 
    return (ItemTool)Items.WOODEN_PICKAXE;
  }
  
  public ItemStack[] getBestPickaxeStack() {
    InvItem bestItem = getConfig().getBestPickaxe(this);
    if (bestItem != null)
      return bestItem.staticStackArray; 
    return WOODDEN_PICKAXE_STACK;
  }
  
  public ItemTool getBestShovel() {
    InvItem bestItem = getConfig().getBestShovel(this);
    if (bestItem != null)
      return (ItemTool)bestItem.item; 
    return (ItemTool)Items.WOODEN_SHOVEL;
  }
  
  public ItemStack[] getBestShovelStack() {
    InvItem bestItem = getConfig().getBestShovel(this);
    if (bestItem != null)
      return bestItem.staticStackArray; 
    return WOODDEN_SHOVEL_STACK;
  }
  
  public Block getBlock(Point p) {
    return WorldUtilities.getBlock(this.world, p);
  }
  
  public int getBlockMeta(Point p) {
    return WorldUtilities.getBlockMeta(this.world, p);
  }
  
  public float getBlockPathWeight(BlockPos pos) {
    if (!this.allowRandomMoves) {
      if (MillConfigValues.LogPathing >= 3 && this.extraLog)
        MillLog.debug(this, "Forbiding random moves. Current goal: " + Goal.goals.get(this.goalKey) + " Returning: " + -99999.0F); 
      return Float.NEGATIVE_INFINITY;
    } 
    Point rp = new Point(pos);
    double dist = rp.distanceTo(this.housePoint);
    if (WorldUtilities.getBlock(this.world, rp.getBelow()) == Blocks.FARMLAND)
      return -50.0F; 
    if (dist > 10.0D)
      return -((float)dist); 
    return MillCommonUtilities.randomInt(10);
  }
  
  public EntityItem getClosestItemVertical(List<InvItem> goods, int radius, int vertical) {
    return WorldUtilities.getClosestItemVertical(this.world, getPos(), goods, radius, vertical);
  }
  
  public ResourceLocation getClothTexturePath(int layer) {
    return this.clothTexture[layer];
  }
  
  public VillagerConfig getConfig() {
    if (this.vtype == null || this.vtype.villagerConfig == null)
      return VillagerConfig.DEFAULT_CONFIG; 
    return this.vtype.villagerConfig;
  }
  
  public Culture getCulture() {
    if (this.vtype == null)
      return null; 
    return this.vtype.culture;
  }
  
  public ConstructionIP getCurrentConstruction() {
    if (this.constructionJobId > -1 && this.constructionJobId < getTownHall().getConstructionsInProgress().size()) {
      ConstructionIP cip = getTownHall().getConstructionsInProgress().get(this.constructionJobId);
      if (cip.getBuilder() == null || cip.getBuilder() == this)
        return cip; 
    } 
    return null;
  }
  
  public Goal getCurrentGoal() {
    if (Goal.goals.containsKey(this.goalKey))
      return (Goal)Goal.goals.get(this.goalKey); 
    return null;
  }
  
  protected int getExperiencePoints(EntityPlayer par1EntityPlayer) {
    return this.vtype.expgiven;
  }
  
  public String getFemaleChild() {
    return this.vtype.femaleChild;
  }
  
  public String getGameOccupationName(String playername) {
    if (getCulture() == null || this.vtype == null || getRecord() == null)
      return ""; 
    if (!getCulture().canReadVillagerNames())
      return ""; 
    if (isChild() && getSize() == 20)
      return getCulture().getCultureString("villager." + this.vtype.altkey); 
    return getCulture().getCultureString("villager." + this.vtype.key);
  }
  
  public String getGameSpeech(String playername) {
    if (getCulture() == null)
      return null; 
    String speech = VillageUtilities.getVillagerSentence(this, playername, false);
    if (speech != null) {
      int duration = 10 + speech.length() / 5;
      duration = Math.min(duration, 30);
      if (this.speech_started + (20 * duration) < this.world.getWorldTime())
        return null; 
    } 
    return speech;
  }
  
  public int getGatheringRange() {
    return 20;
  }
  
  public String getGenderString() {
    if (this.gender == 1)
      return "male"; 
    return "female";
  }
  
  public Building getGoalBuildingDest() {
    return this.mw.getBuilding(getGoalBuildingDestPoint());
  }
  
  public Point getGoalBuildingDestPoint() {
    if (this.goalInformation == null)
      return null; 
    return this.goalInformation.getDestBuildingPos();
  }
  
  public Entity getGoalDestEntity() {
    if (this.goalInformation == null)
      return null; 
    return this.goalInformation.getTargetEnt();
  }
  
  public Point getGoalDestPoint() {
    if (this.goalInformation == null)
      return null; 
    return this.goalInformation.getDest();
  }
  
  public String getGoalLabel(String goal) {
    if (Goal.goals.containsKey(goal))
      return ((Goal)Goal.goals.get(goal)).gameName(this); 
    return "none";
  }
  
  public List<Goal> getGoals() {
    if (this.vtype != null)
      return this.vtype.goals; 
    return null;
  }
  
  public List<InvItem> getGoodsToBringBackHome() {
    return this.vtype.bringBackHomeGoods;
  }
  
  public List<InvItem> getGoodsToCollect() {
    return this.vtype.collectGoods;
  }
  
  public int getHireCost(EntityPlayer player) {
    int cost = this.vtype.hireCost;
    if (getTownHall().controlledBy(player))
      cost /= 2; 
    return cost;
  }
  
  public Building getHouse() {
    if (this.house != null)
      return this.house; 
    if (MillConfigValues.LogVillager >= 3 && this.extraLog)
      MillLog.debug(this, "Seeking uncached house"); 
    if (this.mw != null) {
      this.house = this.mw.getBuilding(this.housePoint);
      return this.house;
    } 
    return null;
  }
  
  public Set<InvItem> getInventoryKeys() {
    return this.inventory.keySet();
  }
  
  public List<InvItem> getItemsNeeded() {
    return this.vtype.itemsNeeded;
  }
  
  public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
    if (slotIn == EntityEquipmentSlot.HEAD) {
      for (InvItem item : (getConfig()).armoursHelmetSorted) {
        if (countInv(item) > 0)
          return item.getItemStack(); 
      } 
      return ItemStack.EMPTY;
    } 
    if (slotIn == EntityEquipmentSlot.CHEST) {
      for (InvItem item : (getConfig()).armoursChestplateSorted) {
        if (countInv(item) > 0)
          return item.getItemStack(); 
      } 
      return ItemStack.EMPTY;
    } 
    if (slotIn == EntityEquipmentSlot.LEGS) {
      for (InvItem item : (getConfig()).armoursLeggingsSorted) {
        if (countInv(item) > 0)
          return item.getItemStack(); 
      } 
      return ItemStack.EMPTY;
    } 
    if (slotIn == EntityEquipmentSlot.FEET) {
      for (InvItem item : (getConfig()).armoursBootsSorted) {
        if (countInv(item) > 0)
          return item.getItemStack(); 
      } 
      return ItemStack.EMPTY;
    } 
    if (this.heldItem != null && slotIn == EntityEquipmentSlot.MAINHAND)
      return this.heldItem; 
    if (this.heldItemOffHand != null && slotIn == EntityEquipmentSlot.OFFHAND)
      return this.heldItemOffHand; 
    return ItemStack.EMPTY;
  }
  
  public String getMaleChild() {
    return this.vtype.maleChild;
  }
  
  public String getName() {
    return this.firstName + " " + this.familyName;
  }
  
  public String getNameKey() {
    if (this.vtype == null || getRecord() == null)
      return ""; 
    if (isChild() && getSize() == 20)
      return this.vtype.altkey; 
    return this.vtype.key;
  }
  
  public String getNativeOccupationName() {
    if (this.vtype == null)
      return null; 
    if (isChild() && getSize() == 20)
      return this.vtype.altname; 
    return this.vtype.name;
  }
  
  public String getNativeSpeech(String playername) {
    if (getCulture() == null)
      return null; 
    String speech = VillageUtilities.getVillagerSentence(this, playername, true);
    if (speech != null) {
      int duration = 10 + speech.length() / 5;
      duration = Math.min(duration, 30);
      if (this.speech_started + (20 * duration) < this.world.getWorldTime())
        return null; 
    } 
    return speech;
  }
  
  public Point getPathDestPoint() {
    return this.pathDestPoint;
  }
  
  private AStarConfig getPathingConfig() {
    if (getActiveGoal() != null)
      return getActiveGoal().getPathingConfig(this); 
    return getVillagerPathingConfig();
  }
  
  public PathPoint getPathPointPos() {
    return new PathPoint(MathHelper.floor((getEntityBoundingBox()).minX), MathHelper.floor((getEntityBoundingBox()).minY), MathHelper.floor((getEntityBoundingBox()).minZ));
  }
  
  public Point getPos() {
    return new Point(this.posX, this.posY, this.posZ);
  }
  
  public EnumHandSide getPrimaryHand() {
    if (getRecord() != null && (getRecord()).rightHanded)
      return EnumHandSide.RIGHT; 
    return EnumHandSide.LEFT;
  }
  
  public String getRandomFamilyName() {
    return getCulture().getRandomNameFromList(this.vtype.familyNameList);
  }
  
  public VillagerRecord getRecord() {
    if (this.mw == null)
      return null; 
    return this.mw.getVillagerRecordById(getVillagerId());
  }
  
  public int getSize() {
    if (getRecord() == null)
      return 0; 
    return (getRecord()).size;
  }
  
  public MillVillager getSpouse() {
    if (getHouse() == null || isChild())
      return null; 
    for (MillVillager v : getHouse().getKnownVillagers()) {
      if (!v.isChild() && v.gender != this.gender)
        return v; 
    } 
    return null;
  }
  
  public ResourceLocation getTexture() {
    return this.texture;
  }
  
  public List<String> getToolsCategoriesNeeded() {
    return this.vtype.toolsCategoriesNeeded;
  }
  
  public int getTotalArmorValue() {
    if (getRecord() == null)
      return 0; 
    return getRecord().getTotalArmorValue();
  }
  
  public Building getTownHall() {
    if (this.townHall != null)
      return this.townHall; 
    if (MillConfigValues.LogVillager >= 3 && this.extraLog)
      MillLog.debug(this, "Seeking uncached townHall"); 
    if (this.mw != null) {
      this.townHall = this.mw.getBuilding(this.townHallPoint);
      return this.townHall;
    } 
    return null;
  }
  
  public long getVillagerId() {
    return this.villagerId;
  }
  
  public AStarConfig getVillagerPathingConfig() {
    if (this.vtype.noleafclearing)
      return JPS_CONFIG_NO_LEAVES; 
    return JPS_CONFIG_DEFAULT;
  }
  
  public ItemStack getWeapon() {
    if (this.vtype == null)
      return ItemStack.EMPTY; 
    if (this.isUsingBow) {
      InvItem weapon = getConfig().getBestWeaponRanged(this);
      if (weapon != null)
        return weapon.getItemStack(); 
    } 
    if (this.isUsingHandToHand || !this.vtype.isArcher) {
      InvItem weapon = getConfig().getBestWeaponHandToHand(this);
      if (weapon != null)
        return weapon.getItemStack(); 
    } 
    if (this.vtype.startingWeapon != null)
      return this.vtype.startingWeapon.getItemStack(); 
    return ItemStack.EMPTY;
  }
  
  public void growSize() {
    if (getRecord() == null)
      return; 
    int growth = 2;
    int nb = 0;
    nb = getHouse().takeGoods(Items.EGG, 1);
    if (nb == 1)
      growth += 1 + MillCommonUtilities.randomInt(5); 
    for (InvItem food : (getConfig()).foodsGrowthSorted) {
      if (growth < 10 && (getRecord()).size + growth < 20 && getHouse().countGoods(food) > 0) {
        getHouse().takeGoods(food, 1);
        growth += ((Integer)(getConfig()).foodsGrowth.get(food)).intValue() + MillCommonUtilities.randomInt(((Integer)(getConfig()).foodsGrowth.get(food)).intValue());
      } 
    } 
    (getRecord()).size += growth;
    if ((getRecord()).size > 20)
      (getRecord()).size = 20; 
    computeChildScale();
    if (MillConfigValues.LogChildren >= 2)
      MillLog.minor(this, "Child growing by " + growth + ", new size: " + (getRecord()).size); 
  }
  
  private void handleDoorsAndFenceGates() {
    if (this.doorToClose != null)
      if (this.pathEntity == null || this.pathEntity.getCurrentPathLength() == 0 || (this.pathEntity.getPastTargetPathPoint(2) != null && this.doorToClose.sameBlock(this.pathEntity.getPastTargetPathPoint(2))))
        if (BlockItemUtilities.isWoodenDoor(getBlock(this.doorToClose))) {
          if (((Boolean)this.doorToClose.getBlockActualState(this.world).getValue((IProperty)BlockDoor.OPEN)).booleanValue())
            toggleDoor(this.doorToClose); 
          for (Point nearbyDoor : new Point[] { this.doorToClose.getNorth(), this.doorToClose.getSouth(), this.doorToClose.getEast(), this.doorToClose.getWest() }) {
            if (BlockItemUtilities.isWoodenDoor(getBlock(nearbyDoor)) && (
              (Boolean)nearbyDoor.getBlockActualState(this.world).getValue((IProperty)BlockDoor.OPEN)).booleanValue())
              toggleDoor(nearbyDoor); 
          } 
          this.doorToClose = null;
        } else if (BlockItemUtilities.isFenceGate(getBlock(this.doorToClose))) {
          if (closeFenceGate(this.doorToClose.getiX(), this.doorToClose.getiY(), this.doorToClose.getiZ()))
            this.doorToClose = null; 
        } else {
          this.doorToClose = null;
        }   
    if (this.pathEntity != null && this.pathEntity.getCurrentPathLength() > 0) {
      PathPoint p = null;
      if (this.pathEntity.getCurrentTargetPathPoint() != null) {
        Block currentTargetPathPointBlock = WorldUtilities.getBlock(this.world, (this.pathEntity.getCurrentTargetPathPoint()).x, (this.pathEntity.getCurrentTargetPathPoint()).y, 
            (this.pathEntity.getCurrentTargetPathPoint()).z);
        if (BlockItemUtilities.isWoodenDoor(currentTargetPathPointBlock))
          p = this.pathEntity.getCurrentTargetPathPoint(); 
      } else if (this.pathEntity.getNextTargetPathPoint() != null) {
        Block nextTargetPathPointBlock = WorldUtilities.getBlock(this.world, (this.pathEntity.getNextTargetPathPoint()).x, (this.pathEntity.getNextTargetPathPoint()).y, 
            (this.pathEntity.getNextTargetPathPoint()).z);
        if (BlockItemUtilities.isWoodenDoor(nextTargetPathPointBlock))
          p = this.pathEntity.getNextTargetPathPoint(); 
      } 
      if (p != null) {
        Point point = new Point(p);
        if (!((Boolean)point.getBlockActualState(this.world).getValue((IProperty)BlockDoor.OPEN)).booleanValue())
          toggleDoor(new Point(p)); 
        this.doorToClose = new Point(p);
      } else {
        if (this.pathEntity.getNextTargetPathPoint() != null && 
          BlockItemUtilities.isFenceGate(WorldUtilities.getBlock(this.world, (this.pathEntity.getNextTargetPathPoint()).x, (this.pathEntity.getNextTargetPathPoint()).y, (this.pathEntity.getNextTargetPathPoint()).z))) {
          p = this.pathEntity.getNextTargetPathPoint();
        } else if (this.pathEntity.getCurrentTargetPathPoint() != null && 
          BlockItemUtilities.isFenceGate(WorldUtilities.getBlock(this.world, (this.pathEntity.getCurrentTargetPathPoint()).x, (this.pathEntity.getCurrentTargetPathPoint()).y, (this.pathEntity.getCurrentTargetPathPoint()).z))) {
          p = this.pathEntity.getCurrentTargetPathPoint();
        } 
        if (p != null) {
          Point point = new Point(p);
          openFenceGate(p.x, p.y, p.z);
          this.doorToClose = point;
        } 
      } 
    } 
  }
  
  private void handleLeaveClearing() {
    if (this.pathEntity != null && this.pathEntity.getCurrentPathLength() > 0) {
      List<Point> pointsToCheck = new ArrayList<>();
      if (this.pathEntity.getCurrentTargetPathPoint() != null) {
        Point p = new Point(this.pathEntity.getCurrentTargetPathPoint());
        pointsToCheck.add(p);
        pointsToCheck.add(p.getAbove());
      } 
      if (this.pathEntity.getNextTargetPathPoint() != null) {
        Point p = new Point(this.pathEntity.getNextTargetPathPoint());
        for (int dx = -1; dx < 2; dx++) {
          for (int dz = -1; dz < 2; dz++) {
            pointsToCheck.add(p.getRelative(dx, 0.0D, dz));
            pointsToCheck.add(p.getRelative(dx, 1.0D, dz));
          } 
        } 
      } 
      for (Point point : pointsToCheck) {
        IBlockState blockState = point.getBlockActualState(this.world);
        if (blockState.getBlock() instanceof BlockLeaves) {
          if (blockState.getBlock() == Blocks.LEAVES || blockState.getBlock() == Blocks.LEAVES2) {
            if (((Boolean)blockState.getValue((IProperty)BlockLeaves.DECAYABLE)).booleanValue() == true)
              WorldUtilities.setBlock(this.world, point, Blocks.AIR, true, true); 
            continue;
          } 
          if (blockState.getBlock() instanceof org.millenaire.common.block.BlockFruitLeaves)
            continue; 
          if (BlockStateUtilities.hasPropertyByName(blockState, "decayable")) {
            if (((Boolean)blockState.getValue((IProperty)BlockLeaves.DECAYABLE)).booleanValue() == true)
              WorldUtilities.setBlock(this.world, point, Blocks.AIR, true, true); 
            continue;
          } 
          WorldUtilities.setBlock(this.world, point, Blocks.AIR, true, true);
        } 
      } 
    } 
  }
  
  private boolean hasBow() {
    return (getConfig().getBestWeaponRanged(this) != null);
  }
  
  public boolean hasChildren() {
    return (this.vtype.maleChild != null && this.vtype.femaleChild != null);
  }
  
  public int hashCode() {
    return (int)getVillagerId();
  }
  
  public boolean helpsInAttacks() {
    return this.vtype.helpInAttacks;
  }
  
  public void interactDev(EntityPlayer entityplayer) {
    DevModUtilities.villagerInteractDev(entityplayer, this);
  }
  
  public boolean interactSpecial(EntityPlayer entityplayer) {
    if (getTownHall() == null)
      MillLog.error(this, "Trying to interact with a villager with no TH."); 
    if (getHouse() == null)
      MillLog.error(this, "Trying to interact with a villager with no house."); 
    if (isChief()) {
      ServerSender.displayVillageChiefGUI(entityplayer, this);
      return true;
    } 
    UserProfile profile = this.mw.getProfile(entityplayer);
    if ((canMeditate() && this.mw.isGlobalTagSet("pujas")) || (canPerformSacrifices() && this.mw.isGlobalTagSet("mayansacrifices"))) {
      if (MillConfigValues.LogPujas >= 3)
        MillLog.debug(this, "canMeditate"); 
      if (getTownHall().getReputation(entityplayer) >= -1024) {
        for (BuildingLocation l : getTownHall().getLocations()) {
          if (l.level >= 0 && l.getSellingPos() != null && l.getSellingPos().distanceTo((Entity)this) < 8.0D) {
            Building b = l.getBuilding(this.world);
            if (b.pujas != null) {
              if (MillConfigValues.LogPujas >= 3)
                MillLog.debug(this, "Found shrine: " + b); 
              Point p = b.getPos();
              entityplayer.openGui(Mill.instance, 6, this.world, p.getiX(), p.getiY(), p.getiZ());
              return true;
            } 
          } 
        } 
      } else {
        ServerSender.sendTranslatedSentence(entityplayer, 'f', "ui.sellerboycott", new String[] { getName() });
        return false;
      } 
    } 
    if (isSeller() && !getTownHall().controlledBy(entityplayer))
      if (getTownHall().getReputation(entityplayer) >= -1024 && (getTownHall()).chestLocked) {
        for (BuildingLocation l : getTownHall().getLocations()) {
          if (l.level >= 0 && l.shop != null && l.shop.length() > 0 && ((
            l.getSellingPos() != null && l.getSellingPos().distanceTo((Entity)this) < 5.0D) || l.sleepingPos.distanceTo((Entity)this) < 5.0D)) {
            ServerSender.displayVillageTradeGUI(entityplayer, l.getBuilding(this.world));
            return true;
          } 
        } 
      } else {
        if (!(getTownHall()).chestLocked) {
          ServerSender.sendTranslatedSentence(entityplayer, 'f', "ui.sellernotcurrently possible", new String[] { getName() });
          return false;
        } 
        ServerSender.sendTranslatedSentence(entityplayer, 'f', "ui.sellerboycott", new String[] { getName() });
        return false;
      }  
    if (isForeignMerchant()) {
      ServerSender.displayMerchantTradeGUI(entityplayer, this);
      return true;
    } 
    if (this.vtype.hireCost > 0) {
      if (this.hiredBy == null || this.hiredBy.equals(entityplayer.getName())) {
        ServerSender.displayHireGUI(entityplayer, this);
        return true;
      } 
      ServerSender.sendTranslatedSentence(entityplayer, 'f', "hire.hiredbyotherplayer", new String[] { getName(), this.hiredBy });
      return false;
    } 
    if (isLocalMerchant() && !profile.villagersInQuests.containsKey(Long.valueOf(getVillagerId()))) {
      ServerSender.sendTranslatedSentence(entityplayer, '6', "other.localmerchantinteract", new String[] { getName() });
      return false;
    } 
    return false;
  }
  
  public boolean isChief() {
    return this.vtype.isChief;
  }
  
  public boolean isChild() {
    if (this.vtype == null)
      return false; 
    return this.vtype.isChild;
  }
  
  public boolean isForeignMerchant() {
    return this.vtype.isForeignMerchant;
  }
  
  public boolean isHostile() {
    return this.vtype.hostile;
  }
  
  public boolean isLocalMerchant() {
    return this.vtype.isLocalMerchant;
  }
  
  protected boolean isMovementBlocked() {
    return (getHealth() <= 0.0F || isVillagerSleeping());
  }
  
  public boolean isReallyDead() {
    return (this.isDead && getHealth() <= 0.0F);
  }
  
  public boolean isSeller() {
    return this.vtype.canSell;
  }
  
  public boolean isTextureValid(String texture) {
    if (this.vtype != null)
      return this.vtype.isTextureValid(texture); 
    return true;
  }
  
  public boolean isVillagerSleeping() {
    return this.shouldLieDown;
  }
  
  public boolean isVisitor() {
    if (this.vtype == null)
      return false; 
    return this.vtype.visitor;
  }
  
  private void jumpToDest() {
    Point jumpTo = WorldUtilities.findVerticalStandingPos(this.world, getPathDestPoint());
    if (jumpTo != null && jumpTo.distanceTo(getPathDestPoint()) < 4.0D) {
      if (MillConfigValues.LogPathing >= 1 && this.extraLog)
        MillLog.major(this, "Jumping from " + getPos() + " to " + jumpTo); 
      setPosition(jumpTo.getiX() + 0.5D, jumpTo.getiY() + 0.5D, jumpTo.getiZ() + 0.5D);
      this.longDistanceStuck = 0;
      this.localStuck = 0;
    } else if (this.goalKey != null && Goal.goals.containsKey(this.goalKey)) {
      Goal goal = (Goal)Goal.goals.get(this.goalKey);
      try {
        goal.unreachableDestination(this);
      } catch (Exception e) {
        MillLog.printException(this + ": Exception in handling unreachable dest for goal " + this.goalKey, e);
      } 
    } 
  }
  
  public void killVillager() {
    if (this.world.isRemote || !(this.world instanceof net.minecraft.world.WorldServer)) {
      super.setDead();
      return;
    } 
    for (InvItem iv : this.inventory.keySet()) {
      if (((Integer)this.inventory.get(iv)).intValue() > 0)
        WorldUtilities.spawnItem(this.world, getPos(), new ItemStack(iv.getItem(), ((Integer)this.inventory.get(iv)).intValue(), iv.meta), 0.0F); 
    } 
    if (this.hiredBy != null) {
      EntityPlayer owner = this.world.getPlayerEntityByName(this.hiredBy);
      if (owner != null)
        ServerSender.sendTranslatedSentence(owner, 'f', "hire.hiredied", new String[] { getName() }); 
    } 
    VillagerRecord vr = getRecord();
    if (vr != null) {
      if (MillConfigValues.LogGeneralAI >= 1)
        MillLog.major(this, getTownHall() + ": Villager has been killed!"); 
      vr.killed = true;
    } 
    super.setDead();
  }
  
  private void leaveVillage() {
    for (InvItem iv : this.vtype.foreignMerchantStock.keySet())
      getHouse().takeGoods(iv.getItem(), iv.meta, ((Integer)this.vtype.foreignMerchantStock.get(iv)).intValue()); 
    this.mw.removeVillagerRecord(this.villagerId);
    despawnVillager();
  }
  
  public void localMerchantUpdate() throws Exception {
    if (getHouse() != null && getHouse() == getTownHall()) {
      List<Building> buildings = getTownHall().getBuildingsWithTag("inn");
      Building inn = null;
      for (Building building : buildings) {
        if (building.merchantRecord == null)
          inn = building; 
      } 
      if (inn == null) {
        this.mw.removeVillagerRecord(this.villagerId);
        despawnVillager();
        MillLog.error(this, "Merchant had Town Hall as house and inn is full. Killing him.");
      } else {
        setHousePoint(inn.getPos());
        VillagerRecord vr = getRecord();
        vr.updateRecord(this);
        this.mw.registerVillagerRecord(vr, true);
        MillLog.error(this, "Merchant had Town Hall as house. Moving him to the inn.");
      } 
    } 
  }
  
  public void onDeath(DamageSource cause) {
    super.onDeath(cause);
  }
  
  public void onFoundPath(List<AStarNode> result) {
    this.pathCalculatedSinceLastTick = result;
  }
  
  public void onLivingUpdate() {
    super.onLivingUpdate();
    updateArmSwingProgress();
    setFacingDirection();
    if (isVillagerSleeping()) {
      this.motionX = 0.0D;
      this.motionY = 0.0D;
      this.motionZ = 0.0D;
    } 
  }
  
  public void onNoPathAvailable() {
    this.pathFailedSincelastTick = true;
  }
  
  public void onUpdate() {
    long startTime = System.nanoTime();
    if (this.world.provider.getDimension() != 0)
      despawnVillagerSilent(); 
    try {
      if (this.vtype == null) {
        if (!this.isDead) {
          MillLog.error(this, "Unknown villager type. Killing him.");
          despawnVillagerSilent();
        } 
        return;
      } 
      if (this.pathFailedSincelastTick)
        pathFailedSinceLastTick(); 
      if (this.pathCalculatedSinceLastTick != null)
        applyPathCalculatedSinceLastTick(); 
      if (this.world.isRemote) {
        super.onUpdate();
        return;
      } 
      if (this.isDead) {
        super.onUpdate();
        return;
      } 
      if (Math.abs(this.world.getWorldTime() + hashCode()) % 10L == 2L)
        sendVillagerPacket(); 
      if (Math.abs(this.world.getWorldTime() + hashCode()) % 40L == 4L)
        unlockForNearbyPlayers(); 
      if (this.hiredBy != null) {
        updateHired();
        super.onUpdate();
        return;
      } 
      if (getTownHall() == null || getHouse() == null)
        return; 
      if (getTownHall() != null && !(getTownHall()).isActive)
        return; 
      if (getPos().distanceTo(getTownHall().getPos()) > ((getTownHall()).villageType.radius + 100)) {
        MillLog.error(this, "Villager is far away from village. Despawning him.");
        despawnVillagerSilent();
      } 
      try {
        this.timer++;
        if ((((getHealth() < getMaxHealth()) ? 1 : 0) & ((MillCommonUtilities.randomInt(1600) == 0) ? 1 : 0)) != 0)
          setHealth(getHealth() + 1.0F); 
        detrampleCrops();
        this.allowRandomMoves = true;
        this.stopMoving = false;
        if (getTownHall() == null || getHouse() == null) {
          super.onUpdate();
          return;
        } 
        if (Goal.beSeller.key.equals(this.goalKey)) {
          this.townHall.seller = this;
        } else if (Goal.getResourcesForBuild.key.equals(this.goalKey) || Goal.construction.key.equals(this.goalKey)) {
          if (MillConfigValues.LogTileEntityBuilding >= 3)
            MillLog.debug(this, "Registering as builder for: " + this.townHall); 
          if (this.constructionJobId > -1 && this.townHall.getConstructionsInProgress().size() > this.constructionJobId)
            ((ConstructionIP)this.townHall.getConstructionsInProgress().get(this.constructionJobId)).setBuilder(this); 
        } 
        if ((getTownHall()).underAttack) {
          if (this.goalKey == null || (!this.goalKey.equals(Goal.raidVillage.key) && !this.goalKey.equals(Goal.defendVillage.key) && !this.goalKey.equals(Goal.hide.key)))
            clearGoal(); 
          if (this.isRaider) {
            this.goalKey = Goal.raidVillage.key;
            targetDefender();
          } else if (helpsInAttacks()) {
            this.goalKey = Goal.defendVillage.key;
            targetRaider();
          } else {
            this.goalKey = Goal.hide.key;
          } 
          checkGoals();
        } 
        if (getAttackTarget() != null) {
          if (this.vtype.isDefensive && getPos().distanceTo(getHouse().getResManager().getDefendingPos()) > 20.0D) {
            setAttackTarget(null);
          } else if (!getAttackTarget().isEntityAlive() || getPos().distanceTo((Entity)getAttackTarget()) > 80.0D || (this.world
            .getDifficulty() == EnumDifficulty.PEACEFUL && getAttackTarget() instanceof EntityPlayer)) {
            setAttackTarget(null);
          } 
          if (getAttackTarget() != null) {
            this.shouldLieDown = false;
            attackEntity((Entity)getAttackTarget());
            if (!(getAttackTarget()).isAirBorne) {
              setPathDestPoint(new Point((Entity)getAttackTarget()), 1);
            } else {
              Point posToAttack = new Point((Entity)getAttackTarget());
              while (posToAttack.y > 0.0D && posToAttack.isBlockPassable(this.world))
                posToAttack = posToAttack.getBelow(); 
              if (posToAttack != null)
                setPathDestPoint(posToAttack.getAbove(), 3); 
            } 
          } 
        } else if (isHostile() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && (getTownHall()).closestPlayer != null && 
          getPos().distanceTo((Entity)(getTownHall()).closestPlayer) <= 80.0D) {
          int range = 80;
          if (this.vtype.isDefensive)
            range = 20; 
          setAttackTarget((EntityLivingBase)this.world.getClosestPlayer(this.posX, this.posY, this.posZ, range, true));
          clearGoal();
        } 
        if (getAttackTarget() != null) {
          setGoalDestPoint(new Point((Entity)getAttackTarget()));
          this.heldItem = getWeapon();
          this.heldItemOffHand = ItemStack.EMPTY;
          if (this.goalKey != null && 
            !((Goal)Goal.goals.get(this.goalKey)).isFightingGoal())
            clearGoal(); 
        } else if (!(getTownHall()).underAttack) {
          if (this.world.isDaytime()) {
            speakSentence("greeting", 12000, 3, 10);
            this.nightActionPerformed = false;
            List<InvItem> goods = getGoodsToCollect();
            if (goods != null && (this.world.getWorldTime() + getVillagerId()) % 20L == 0L) {
              EntityItem item = getClosestItemVertical(goods, 5, 30);
              if (item != null) {
                item.setDead();
                if (item.getItem().getItem() == Item.getItemFromBlock(Blocks.SAPLING)) {
                  addToInv(item.getItem().getItem(), item.getItem().getItemDamage() & 0x3, 1);
                } else {
                  addToInv(item.getItem().getItem(), item.getItem().getItemDamage(), 1);
                } 
              } 
            } 
            specificUpdate();
            if (!this.isRaider) {
              if (this.goalKey == null)
                setNextGoal(); 
              if (this.goalKey != null) {
                checkGoals();
              } else {
                this.shouldLieDown = false;
              } 
            } 
          } else if (!this.isRaider) {
            if (this.goalKey == null)
              setNextGoal(); 
            if (this.goalKey != null) {
              checkGoals();
            } else {
              this.shouldLieDown = false;
            } 
          } 
        } 
        if (getPathDestPoint() != null && this.pathEntity != null && this.pathEntity.getCurrentPathLength() > 0 && !this.stopMoving) {
          double olddistance = this.prevPoint.horizontalDistanceToSquared(getPathDestPoint());
          double newdistance = getPos().horizontalDistanceToSquared(getPathDestPoint());
          if (olddistance - newdistance < 2.0E-4D) {
            this.longDistanceStuck++;
          } else {
            this.longDistanceStuck--;
          } 
          if (this.longDistanceStuck < 0)
            this.longDistanceStuck = 0; 
          if (this.pathEntity != null && this.pathEntity.getCurrentPathLength() > 1 && 
            MillConfigValues.LogPathing >= 2 && this.extraLog)
            MillLog.minor(this, "Stuck: " + this.longDistanceStuck + " pos " + getPos() + " node: " + this.pathEntity.getCurrentTargetPathPoint() + " next node: " + this.pathEntity
                .getNextTargetPathPoint() + " dest: " + getPathDestPoint()); 
          if (this.longDistanceStuck > 3000 && (!this.vtype.noTeleport || (getRecord() != null && (getRecord()).raidingVillage)))
            jumpToDest(); 
          PathPoint nextPoint = this.pathEntity.getNextTargetPathPoint();
          if (nextPoint != null) {
            olddistance = this.prevPoint.distanceToSquared(nextPoint);
            newdistance = getPos().distanceToSquared(nextPoint);
            if (olddistance - newdistance < 2.0E-4D) {
              this.localStuck += 4;
            } else {
              this.localStuck--;
            } 
            if (this.localStuck < 0)
              this.localStuck = 0; 
            if (this.localStuck > 30) {
              this.navigator.clearPath();
              this.pathEntity = null;
            } 
            if (this.localStuck > 100) {
              setPosition(nextPoint.x + 0.5D, nextPoint.y + 0.5D, nextPoint.z + 0.5D);
              this.localStuck = 0;
            } 
          } 
        } else {
          this.longDistanceStuck = 0;
          this.localStuck = 0;
        } 
        if (getPathDestPoint() != null && !this.stopMoving)
          updatePathIfNeeded(getPathDestPoint()); 
        if (this.stopMoving || this.pathPlannerJPS.isBusy()) {
          this.navigator.clearPath();
          this.pathEntity = null;
        } 
        this.prevPoint = getPos();
        if (canVillagerClearLeaves())
          if (Math.abs(this.world.getWorldTime() + hashCode()) % 10L == 6L)
            handleLeaveClearing();  
        handleDoorsAndFenceGates();
        if (System.currentTimeMillis() - this.timeSinceLastPathingTimeDisplay > 10000L) {
          if (this.pathingTime > 500L) {
            if (getPathDestPoint() != null) {
              MillLog.warning(this, "Pathing time in last 10 secs: " + this.pathingTime + " dest: " + getPathDestPoint() + " dest bid: " + WorldUtilities.getBlock(this.world, getPathDestPoint()) + " above bid: " + 
                  WorldUtilities.getBlock(this.world, getPathDestPoint().getAbove()));
            } else {
              MillLog.warning(this, "Pathing time in last 10 secs: " + this.pathingTime + " null dest point.");
            } 
            MillLog.warning(this, "nbPathsCalculated: " + this.nbPathsCalculated + " nbPathNoStart: " + this.nbPathNoStart + " nbPathNoEnd: " + this.nbPathNoEnd + " nbPathAborted: " + this.nbPathAborted + " nbPathFailure: " + this.nbPathFailure);
            if (this.goalKey != null)
              MillLog.warning(this, "Current goal: " + Goal.goals.get(this.goalKey)); 
          } 
          this.timeSinceLastPathingTimeDisplay = System.currentTimeMillis();
          this.pathingTime = 0L;
          this.nbPathsCalculated = 0;
          this.nbPathNoStart = 0;
          this.nbPathNoEnd = 0;
          this.nbPathAborted = 0;
          this.nbPathFailure = 0;
        } 
      } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
        Mill.proxy.sendChatAdmin(getName() + ": Error in onUpdate(). Check millenaire.log.");
        MillLog.error(this, e.getMessage());
      } catch (Exception e) {
        Mill.proxy.sendChatAdmin(getName() + ": Error in onUpdate(). Check millenaire.log.");
        MillLog.error(this, "Exception in Villager.onUpdate(): ");
        MillLog.printException(e);
      } 
      if (Math.abs(this.world.getWorldTime() + hashCode()) % 10L == 5L)
        triggerMobAttacks(); 
      updateDialogue();
      this.isUsingBow = false;
      this.isUsingHandToHand = false;
      super.onUpdate();
      if (MillConfigValues.DEV) {
        if (getPathDestPoint() == null || this.pathPlannerJPS.isBusy() || this.pathEntity == null);
        if (getPathDestPoint() == null || getGoalDestPoint() == null || getPathDestPoint().distanceTo(getGoalDestPoint()) > 20.0D);
      } 
    } catch (Exception e) {
      MillLog.printException("Exception in onUpdate() of villager: " + this, e);
    } 
    if (getTownHall() != null)
      this.mw.reportTime(getTownHall(), System.nanoTime() - startTime, true); 
  }
  
  private boolean openFenceGate(int i, int j, int k) {
    Point p = new Point(i, j, k);
    IBlockState state = p.getBlockActualState(this.world);
    if (BlockItemUtilities.isFenceGate(state.getBlock()) && !((Boolean)state.getValue((IProperty)BlockFenceGate.OPEN)).booleanValue())
      p.setBlockState(this.world, state.withProperty((IProperty)BlockFenceGate.OPEN, Boolean.valueOf(true))); 
    return true;
  }
  
  private void pathFailedSinceLastTick() {
    if (!this.vtype.noTeleport || (getRecord() != null && (getRecord()).raidingVillage))
      jumpToDest(); 
    this.pathFailedSincelastTick = false;
  }
  
  public boolean performNightAction() {
    if (getRecord() == null || getHouse() == null || getTownHall() == null)
      return false; 
    if (isChild())
      if (getSize() < 20) {
        growSize();
      } else {
        teenagerNightAction();
      }  
    if ((getHouse()).hasVisitors)
      visitorNightAction(); 
    if (hasChildren())
      return attemptChildConception(); 
    return true;
  }
  
  public boolean processInteract(EntityPlayer entityplayer, EnumHand hand) {
    if (isVillagerSleeping())
      return true; 
    MillAdvancements.FIRST_CONTACT.grant(entityplayer);
    if (this.vtype != null && (this.vtype.key.equals("indian_sadhu") || this.vtype.key.equals("alchemist")))
      MillAdvancements.MAITRE_A_PENSER.grant(entityplayer); 
    if (this.world.isRemote)
      return true; 
    UserProfile profile = this.mw.getProfile(entityplayer);
    if (profile.villagersInQuests.containsKey(Long.valueOf(getVillagerId()))) {
      QuestInstance qi = (QuestInstance)profile.villagersInQuests.get(Long.valueOf(getVillagerId()));
      if ((qi.getCurrentVillager()).id == getVillagerId()) {
        ServerSender.displayQuestGUI(entityplayer, this);
      } else {
        interactSpecial(entityplayer);
      } 
    } else {
      interactSpecial(entityplayer);
    } 
    if (MillConfigValues.DEV)
      interactDev(entityplayer); 
    return true;
  }
  
  public int putInBuilding(Building building, Item item, int meta, int nb) {
    nb = takeFromInv(item, meta, nb);
    building.storeGoods(item, meta, nb);
    return nb;
  }
  
  public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    super.readEntityFromNBT(nbttagcompound);
    String type = nbttagcompound.getString("vtype");
    String culture = nbttagcompound.getString("culture");
    if (Culture.getCultureByName(culture) != null) {
      if (Culture.getCultureByName(culture).getVillagerType(type) != null) {
        this.vtype = Culture.getCultureByName(culture).getVillagerType(type);
      } else {
        MillLog.error(this, "Could not load dynamic NPC: unknown type: " + type + " in culture: " + culture);
      } 
    } else {
      MillLog.error(this, "Could not load dynamic NPC: unknown culture: " + culture);
    } 
    this.texture = new ResourceLocation("millenaire", nbttagcompound.getString("texture"));
    this.housePoint = Point.read(nbttagcompound, "housePos");
    if (this.housePoint == null) {
      MillLog.error(this, "Error when loading villager: housePoint null");
      Mill.proxy.sendChatAdmin(getName() + ": Could not load house position. Check millenaire.log");
    } 
    this.townHallPoint = Point.read(nbttagcompound, "townHallPos");
    if (this.townHallPoint == null) {
      MillLog.error(this, "Error when loading villager: townHallPoint null");
      Mill.proxy.sendChatAdmin(getName() + ": Could not load town hall position. Check millenaire.log");
    } 
    setGoalDestPoint(Point.read(nbttagcompound, "destPoint"));
    setPathDestPoint(Point.read(nbttagcompound, "pathDestPoint"), 0);
    setGoalBuildingDestPoint(Point.read(nbttagcompound, "destBuildingPoint"));
    this.prevPoint = Point.read(nbttagcompound, "prevPoint");
    this.doorToClose = Point.read(nbttagcompound, "doorToClose");
    this.action = nbttagcompound.getInteger("action");
    this.goalKey = nbttagcompound.getString("goal");
    if (this.goalKey.trim().length() == 0)
      this.goalKey = null; 
    if (this.goalKey != null && !Goal.goals.containsKey(this.goalKey))
      this.goalKey = null; 
    this.constructionJobId = nbttagcompound.getInteger("constructionJobId");
    this.dialogueKey = nbttagcompound.getString("dialogueKey");
    this.dialogueStart = nbttagcompound.getLong("dialogueStart");
    this.dialogueRole = nbttagcompound.getInteger("dialogueRole");
    this.dialogueColour = (char)nbttagcompound.getInteger("dialogueColour");
    this.dialogueChat = nbttagcompound.getBoolean("dialogueChat");
    if (this.dialogueKey.trim().length() == 0)
      this.dialogueKey = null; 
    this.familyName = nbttagcompound.getString("familyName");
    this.firstName = nbttagcompound.getString("firstName");
    this.gender = nbttagcompound.getInteger("gender");
    if (nbttagcompound.hasKey("villager_lid"))
      setVillagerId(Math.abs(nbttagcompound.getLong("villager_lid"))); 
    if (!isTextureValid(this.texture.toString())) {
      ResourceLocation newTexture = this.vtype.getNewTexture();
      MillLog.major(this, "Texture " + this.texture.toString() + " cannot be found, replacing it with " + newTexture.toString());
      this.texture = newTexture;
    } 
    NBTTagList nbttaglist = nbttagcompound.getTagList("inventoryNew", 10);
    MillCommonUtilities.readInventory(nbttaglist, this.inventory);
    this.previousBlock = Block.getBlockById(nbttagcompound.getInteger("previousBlock"));
    this.previousBlockMeta = nbttagcompound.getInteger("previousBlockMeta");
    this.hiredBy = nbttagcompound.getString("hiredBy");
    this.hiredUntil = nbttagcompound.getLong("hiredUntil");
    this.aggressiveStance = nbttagcompound.getBoolean("aggressiveStance");
    this.isRaider = nbttagcompound.getBoolean("isRaider");
    this.visitorNbNights = nbttagcompound.getInteger("visitorNbNights");
    if (this.hiredBy.equals(""))
      this.hiredBy = null; 
    if (nbttagcompound.hasKey("clothTexture")) {
      this.clothTexture[0] = new ResourceLocation("millenaire", nbttagcompound.getString("clothTexture"));
    } else {
      for (int i = 0; i < 2; i++) {
        if (nbttagcompound.getString("clothTexture_" + i).length() > 0) {
          String texture = nbttagcompound.getString("clothTexture_" + i);
          if (texture.contains(":")) {
            this.clothTexture[i] = new ResourceLocation(texture);
          } else {
            this.clothTexture[i] = new ResourceLocation("millenaire", texture);
          } 
        } else {
          this.clothTexture[i] = null;
        } 
      } 
    } 
    this.clothName = nbttagcompound.getString("clothName");
    if (this.clothName.equals("")) {
      this.clothName = null;
      for (int i = 0; i < 2; i++)
        this.clothTexture[i] = null; 
    } 
    updateClothTexturePath();
  }
  
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
  }
  
  public void readSpawnData(ByteBuf ds) {
    PacketBuffer data = new PacketBuffer(ds);
    try {
      setVillagerId(data.readLong());
      readVillagerStreamdata(data);
    } catch (IOException e) {
      MillLog.printException("Error in readSpawnData for villager " + this, e);
    } 
  }
  
  private void readVillagerStreamdata(PacketBuffer data) throws IOException {
    Culture culture = Culture.getCultureByName(StreamReadWrite.readNullableString(data));
    String vt = StreamReadWrite.readNullableString(data);
    if (culture != null)
      this.vtype = culture.getVillagerType(vt); 
    this.texture = StreamReadWrite.readNullableResourceLocation(data);
    this.goalKey = StreamReadWrite.readNullableString(data);
    this.constructionJobId = data.readInt();
    this.housePoint = StreamReadWrite.readNullablePoint(data);
    this.townHallPoint = StreamReadWrite.readNullablePoint(data);
    this.firstName = StreamReadWrite.readNullableString(data);
    this.familyName = StreamReadWrite.readNullableString(data);
    this.gender = data.readInt();
    this.hiredBy = StreamReadWrite.readNullableString(data);
    this.aggressiveStance = data.readBoolean();
    this.hiredUntil = data.readLong();
    this.isUsingBow = data.readBoolean();
    this.isUsingHandToHand = data.readBoolean();
    this.isRaider = data.readBoolean();
    this.speech_key = StreamReadWrite.readNullableString(data);
    this.speech_variant = data.readInt();
    this.speech_started = data.readLong();
    this.heldItem = StreamReadWrite.readNullableItemStack(data);
    this.heldItemOffHand = StreamReadWrite.readNullableItemStack(data);
    this.inventory = StreamReadWrite.readInventory(data);
    this.clothName = StreamReadWrite.readNullableString(data);
    for (int i = 0; i < 2; i++)
      this.clothTexture[i] = StreamReadWrite.readNullableResourceLocation(data); 
    setGoalDestPoint(StreamReadWrite.readNullablePoint(data));
    this.shouldLieDown = data.readBoolean();
    this.dialogueTargetFirstName = StreamReadWrite.readNullableString(data);
    this.dialogueTargetLastName = StreamReadWrite.readNullableString(data);
    this.dialogueColour = data.readChar();
    this.dialogueChat = data.readBoolean();
    setHealth(data.readFloat());
    this.visitorNbNights = data.readInt();
    UUID uuid = StreamReadWrite.readNullableUUID(data);
    if (uuid != null) {
      Entity targetEntity = WorldUtilities.getEntityByUUID(this.world, uuid);
      if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
        setAttackTarget((EntityLivingBase)targetEntity);
      } else {
        setAttackTarget(null);
      } 
    } else {
      setAttackTarget(null);
    } 
    int nbMerchantSells = data.readInt();
    if (nbMerchantSells > -1) {
      this.merchantSells.clear();
      for (int j = 0; j < nbMerchantSells; j++) {
        try {
          TradeGood g = StreamReadWrite.readNullableGoods(data, culture);
          this.merchantSells.put(g, Integer.valueOf(data.readInt()));
        } catch (org.millenaire.common.utilities.MillLog.MillenaireException e) {
          MillLog.printException((Throwable)e);
        } 
      } 
    } 
    int goalDestEntityID = data.readInt();
    if (goalDestEntityID != -1) {
      Entity ent = this.world.getEntityByID(goalDestEntityID);
      if (ent != null)
        setGoalDestEntity(ent); 
    } 
    this.isDeadOnServer = data.readBoolean();
    this.client_lastupdated = this.world.getWorldTime();
  }
  
  public void registerNewPath(AS_PathEntity path) throws Exception {
    if (path == null) {
      boolean handled = false;
      if (this.goalKey != null) {
        Goal goal = (Goal)Goal.goals.get(this.goalKey);
        handled = goal.unreachableDestination(this);
      } 
      if (!handled)
        clearGoal(); 
    } else {
      this.navigator.setPath((Path)path, 0.5D);
      this.pathEntity = path;
      this.moveStrafing = 0.0F;
    } 
  }
  
  public void registerNewPath(List<PathPoint> result) throws Exception {
    AS_PathEntity path = null;
    if (result != null) {
      PathPoint[] pointsCopy = new PathPoint[result.size()];
      int i = 0;
      for (PathPoint p : result) {
        if (p == null) {
          pointsCopy[i] = null;
        } else {
          PathPoint p2 = new PathPoint(p.x, p.y, p.z);
          pointsCopy[i] = p2;
        } 
        i++;
      } 
      path = new AS_PathEntity(pointsCopy);
    } 
    registerNewPath(path);
  }
  
  public HashMap<InvItem, Integer> requiresGoods() {
    if (isChild() && getSize() < 20)
      return this.vtype.requiredFoodAndGoods; 
    if (hasChildren() && getHouse() != null && getHouse().getKnownVillagers().size() < 4)
      return this.vtype.requiredFoodAndGoods; 
    return this.vtype.requiredGoods;
  }
  
  private void sendVillagerPacket() {
    PacketBuffer data = ServerSender.getPacketBuffer();
    try {
      data.writeInt(3);
      writeVillagerStreamData((ByteBuf)data, false);
    } catch (IOException e) {
      MillLog.printException(this + ": Error in sendVillagerPacket", e);
    } 
    ServerSender.sendPacketToPlayersInRange(data, getPos(), 100);
  }
  
  public boolean setBlock(Point p, Block block) {
    return WorldUtilities.setBlock(this.world, p, block, true, true);
  }
  
  public boolean setBlockAndMetadata(Point p, Block block, int metadata) {
    return WorldUtilities.setBlockAndMetadata(this.world, p, block, metadata, true, true);
  }
  
  public boolean setBlockMetadata(Point p, int metadata) {
    return WorldUtilities.setBlockMetadata(this.world, p, metadata);
  }
  
  public boolean setBlockstate(Point p, IBlockState bs) {
    return WorldUtilities.setBlockstate(this.world, p, bs, true, true);
  }
  
  public void setDead() {
    if (getHealth() <= 0.0F)
      killVillager(); 
    super.setDead();
  }
  
  private void setFacingDirection() {
    if (getAttackTarget() != null) {
      faceEntityMill((Entity)getAttackTarget(), 30.0F, 30.0F);
      return;
    } 
    if (this.goalKey != null && (getGoalDestPoint() != null || getGoalDestEntity() != null)) {
      Goal goal = (Goal)Goal.goals.get(this.goalKey);
      if (goal.lookAtGoal())
        if (getGoalDestEntity() != null && getPos().distanceTo(getGoalDestEntity()) < goal.range(this)) {
          faceEntityMill(getGoalDestEntity(), 10.0F, 10.0F);
        } else if (getGoalDestPoint() != null && getPos().distanceTo(getGoalDestPoint()) < goal.range(this)) {
          facePoint(getGoalDestPoint(), 10.0F, 10.0F);
        }  
      if (goal.lookAtPlayer()) {
        EntityPlayer player = this.world.getClosestPlayerToEntity((Entity)this, 10.0D);
        if (player != null) {
          faceEntityMill((Entity)player, 10.0F, 10.0F);
          return;
        } 
      } 
    } 
  }
  
  public void setGoalBuildingDestPoint(Point newDest) {
    if (this.goalInformation == null)
      this.goalInformation = new Goal.GoalInformation(null, null, null); 
    this.goalInformation.setDestBuildingPos(newDest);
  }
  
  public void setGoalDestEntity(Entity ent) {
    if (this.goalInformation == null)
      this.goalInformation = new Goal.GoalInformation(null, null, null); 
    this.goalInformation.setTargetEnt(ent);
    if (ent != null)
      setPathDestPoint(new Point(ent), 2); 
    if (ent instanceof MillVillager) {
      MillVillager v = (MillVillager)ent;
      this.dialogueTargetFirstName = v.firstName;
      this.dialogueTargetLastName = v.familyName;
    } 
  }
  
  public void setGoalDestPoint(Point newDest) {
    if (this.goalInformation == null)
      this.goalInformation = new Goal.GoalInformation(null, null, null); 
    this.goalInformation.setDest(newDest);
    setPathDestPoint(newDest, 0);
  }
  
  public void setGoalInformation(Goal.GoalInformation info) {
    this.goalInformation = info;
    if (info != null) {
      if (info.getTargetEnt() != null) {
        setPathDestPoint(new Point(info.getTargetEnt()), 2);
      } else if (info.getDest() != null) {
        setPathDestPoint(info.getDest(), 0);
      } else {
        setPathDestPoint((Point)null, 0);
      } 
    } else {
      setPathDestPoint((Point)null, 0);
    } 
  }
  
  public void setHousePoint(Point p) {
    this.housePoint = p;
    this.house = null;
  }
  
  public void setInv(Item item, int meta, int nb) {
    this.inventory.put(InvItem.createInvItem(item, meta), Integer.valueOf(nb));
    updateVillagerRecord();
  }
  
  public void setNextGoal() throws Exception {
    Goal nextGoal = null;
    clearGoal();
    for (Goal goal : getGoals()) {
      if (goal.isPossible(this)) {
        if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog)
          MillLog.minor(this, "Priority for goal " + goal.gameName(this) + ": " + goal.priority(this)); 
        if (nextGoal == null || (nextGoal.leasure && !goal.leasure)) {
          nextGoal = goal;
          continue;
        } 
        if (nextGoal == null || nextGoal.priority(this) < goal.priority(this))
          nextGoal = goal; 
      } 
    } 
    if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog)
      MillLog.minor(this, "Selected this: " + nextGoal); 
    if (nextGoal != null) {
      speakSentence(nextGoal.key + ".chosen");
      this.goalKey = nextGoal.key;
      this.heldItem = ItemStack.EMPTY;
      this.heldItemOffHand = ItemStack.EMPTY;
      this.heldItemCount = Integer.MAX_VALUE;
      nextGoal.onAccept(this);
      this.goalStarted = this.world.getWorldTime();
      this.lastGoalTime.put(nextGoal, Long.valueOf(this.world.getWorldTime()));
      IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      iattributeinstance.removeModifier(SPRINT_SPEED_BOOST);
      if (nextGoal.sprint)
        iattributeinstance.applyModifier(SPRINT_SPEED_BOOST); 
    } else {
      this.goalKey = null;
    } 
    if (MillConfigValues.LogBuildingPlan >= 1 && nextGoal != null && nextGoal.key.equals(Goal.getResourcesForBuild.key)) {
      ConstructionIP cip = getCurrentConstruction();
      if (cip != null)
        MillLog.major(this, getName() + " is new builder, for: " + (cip.getBuildingLocation()).planKey + "_" + (cip.getBuildingLocation()).level + ". Blocks loaded: " + (cip.getBblocks()).length); 
    } 
  }
  
  public void setPathDestPoint(Point newDest, int tolerance) {
    if ((newDest == null || !newDest.equals(this.pathDestPoint)) && (
      this.pathDestPoint == null || newDest == null || tolerance < newDest.distanceTo(this.pathDestPoint))) {
      this.navigator.clearPath();
      this.pathEntity = null;
    } 
    this.pathDestPoint = newDest;
  }
  
  public void setTexture(ResourceLocation tx) {
    this.texture = tx;
  }
  
  public void setTownHallPoint(Point p) {
    this.townHallPoint = p;
    this.townHall = null;
  }
  
  public void setVillagerId(long villagerId) {
    this.villagerId = villagerId;
  }
  
  public void speakSentence(String key) {
    speakSentence(key, 600, 3, 1);
  }
  
  public void speakSentence(String key, int delay, int distance, int chanceOn) {
    if (delay > this.world.getWorldTime() - this.speech_started)
      return; 
    if (!MillCommonUtilities.chanceOn(chanceOn))
      return; 
    if (getTownHall() == null || (getTownHall()).closestPlayer == null || getPos().distanceTo((Entity)(getTownHall()).closestPlayer) > distance)
      return; 
    key = key.toLowerCase();
    this.speech_key = null;
    if (getCulture().hasSentences(getNameKey() + "." + key)) {
      this.speech_key = getNameKey() + "." + key;
    } else if (getCulture().hasSentences(getGenderString() + "." + key)) {
      this.speech_key = getGenderString() + "." + key;
    } else if (getCulture().hasSentences("villager." + key)) {
      this.speech_key = "villager." + key;
    } 
    if (this.speech_key != null) {
      this.speech_variant = MillCommonUtilities.randomInt(getCulture().getSentences(this.speech_key).size());
      this.speech_started = this.world.getWorldTime();
      sendVillagerPacket();
      ServerSender.sendVillageSentenceInRange(this.world, getPos(), 30, this);
    } 
  }
  
  public void specificUpdate() throws Exception {
    if (isLocalMerchant())
      localMerchantUpdate(); 
    if (isForeignMerchant())
      foreignMerchantUpdate(); 
  }
  
  public int takeFromBuilding(Building building, Item item, int meta, int nb) {
    if (item == Item.getItemFromBlock(Blocks.LOG) && meta == -1) {
      int total = 0;
      int nb2 = building.takeGoods(item, 0, nb);
      addToInv(item, 0, nb2);
      total += nb2;
      nb2 = building.takeGoods(item, 1, nb - total);
      addToInv(item, 0, nb2);
      total += nb2;
      nb2 = building.takeGoods(item, 2, nb - total);
      addToInv(item, 0, nb2);
      total += nb2;
      nb2 = building.takeGoods(item, 3, nb - total);
      addToInv(item, 0, nb2);
      total += nb2;
      nb2 = building.takeGoods(Item.getItemFromBlock(Blocks.LOG2), 0, nb - total);
      addToInv(item, 0, nb2);
      total += nb2;
      nb2 = building.takeGoods(Item.getItemFromBlock(Blocks.LOG2), 1, nb - total);
      addToInv(item, 0, nb2);
      total += nb2;
      return total;
    } 
    nb = building.takeGoods(item, meta, nb);
    addToInv(item, meta, nb);
    return nb;
  }
  
  public int takeFromInv(Block block, int meta, int nb) {
    return takeFromInv(Item.getItemFromBlock(block), meta, nb);
  }
  
  public int takeFromInv(IBlockState blockState, int nb) {
    return takeFromInv(Item.getItemFromBlock(blockState.getBlock()), blockState.getBlock().getMetaFromState(blockState), nb);
  }
  
  public int takeFromInv(InvItem item, int nb) {
    return takeFromInv(item.getItem(), item.meta, nb);
  }
  
  public int takeFromInv(Item item, int meta, int nb) {
    if (item == Item.getItemFromBlock(Blocks.LOG) && meta == -1) {
      int total = 0;
      int i;
      for (i = 0; i < 16; i++) {
        InvItem invItem = InvItem.createInvItem(item, i);
        if (this.inventory.containsKey(invItem)) {
          int nb2 = Math.min(nb, ((Integer)this.inventory.get(invItem)).intValue());
          this.inventory.put(invItem, Integer.valueOf(((Integer)this.inventory.get(invItem)).intValue() - nb2));
          total += nb2;
        } 
      } 
      for (i = 0; i < 16; i++) {
        InvItem invItem = InvItem.createInvItem(Item.getItemFromBlock(Blocks.LOG2), i);
        if (this.inventory.containsKey(invItem)) {
          int nb2 = Math.min(nb, ((Integer)this.inventory.get(invItem)).intValue());
          this.inventory.put(invItem, Integer.valueOf(((Integer)this.inventory.get(invItem)).intValue() - nb2));
          total += nb2;
        } 
      } 
      updateVillagerRecord();
      return total;
    } 
    InvItem key = InvItem.createInvItem(item, meta);
    if (this.inventory.containsKey(key)) {
      nb = Math.min(nb, ((Integer)this.inventory.get(key)).intValue());
      this.inventory.put(key, Integer.valueOf(((Integer)this.inventory.get(key)).intValue() - nb));
      updateVillagerRecord();
      updateClothTexturePath();
      return nb;
    } 
    return 0;
  }
  
  private void targetDefender() {
    int bestDist = Integer.MAX_VALUE;
    MillVillager target = null;
    for (MillVillager v : getTownHall().getKnownVillagers()) {
      if (v.helpsInAttacks() && !v.isRaider)
        if (getPos().distanceToSquared((Entity)v) < bestDist) {
          target = v;
          bestDist = (int)getPos().distanceToSquared((Entity)v);
        }  
    } 
    if (target != null && getPos().distanceToSquared((Entity)target) <= 100.0D)
      setAttackTarget((EntityLivingBase)target); 
  }
  
  private void targetRaider() {
    int bestDist = Integer.MAX_VALUE;
    MillVillager target = null;
    for (MillVillager v : getTownHall().getKnownVillagers()) {
      if (v.isRaider)
        if (getPos().distanceToSquared((Entity)v) < bestDist) {
          target = v;
          bestDist = (int)getPos().distanceToSquared((Entity)v);
        }  
    } 
    if (target != null && getPos().distanceToSquared((Entity)target) <= 25.0D)
      setAttackTarget((EntityLivingBase)target); 
  }
  
  private void teenagerNightAction() {
    for (Point p : getTownHall().getKnownVillages()) {
      if (getTownHall().getRelationWithVillage(p) > 90) {
        Building distantVillage = this.mw.getBuilding(p);
        if (distantVillage != null && distantVillage.culture == getCulture() && distantVillage != getTownHall()) {
          boolean canMoveIn = false;
          if (MillConfigValues.LogChildren >= 1)
            MillLog.major(this, "Attempting to move to village: " + distantVillage.getVillageQualifiedName()); 
          Building distantInn = null;
          for (Building distantBuilding : distantVillage.getBuildings()) {
            if (!canMoveIn && distantBuilding != null && distantBuilding.isHouse()) {
              if (distantBuilding.canChildMoveIn(this.gender, this.familyName))
                canMoveIn = true; 
              continue;
            } 
            if (distantInn == null && distantBuilding.isInn && 
              distantBuilding.getAllVillagerRecords().size() < 2)
              distantInn = distantBuilding; 
          } 
          if (canMoveIn && distantInn != null) {
            if (MillConfigValues.LogChildren >= 1)
              MillLog.major(this, "Moving to village: " + distantVillage.getVillageQualifiedName()); 
            getHouse().transferVillagerPermanently(getRecord(), distantInn);
            distantInn.visitorsList.add("panels.childarrived;" + getName() + ";" + getTownHall().getVillageQualifiedName());
          } 
        } 
      } 
    } 
  }
  
  public boolean teleportTo(double d, double d1, double d2) {
    double d3 = this.posX;
    double d4 = this.posY;
    double d5 = this.posZ;
    this.posX = d;
    this.posY = d1;
    this.posZ = d2;
    boolean flag = false;
    int i = MathHelper.floor(this.posX);
    int j = MathHelper.floor(this.posY);
    int k = MathHelper.floor(this.posZ);
    if (this.world.isBlockLoaded(new BlockPos(i, j, k))) {
      boolean flag1;
      for (flag1 = false; !flag1 && j > 0; ) {
        IBlockState bs = WorldUtilities.getBlockState(this.world, i, j - 1, k);
        if (bs.getBlock() == Blocks.AIR || !bs.getMaterial().blocksMovement()) {
          this.posY--;
          j--;
          continue;
        } 
        flag1 = true;
      } 
      if (flag1) {
        setPosition(this.posX, this.posY, this.posZ);
        if (this.world.getCollisionBoxes((Entity)this, getEntityBoundingBox()).size() == 0 && !this.world.containsAnyLiquid(getEntityBoundingBox()))
          flag = true; 
      } 
    } 
    if (!flag) {
      setPosition(d3, d4, d5);
      return false;
    } 
    return true;
  }
  
  public boolean teleportToEntity(Entity entity) {
    Vec3d vec3d = new Vec3d(this.posX - entity.posX, (getEntityBoundingBox()).minY + (this.height / 2.0F) - entity.posY + entity.getEyeHeight(), this.posZ - entity.posZ);
    vec3d = vec3d.normalize();
    double d = 16.0D;
    double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
    double d2 = this.posY + (this.rand.nextInt(16) - 8) - vec3d.y * 16.0D;
    double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
    return teleportTo(d1, d2, d3);
  }
  
  private void toggleDoor(Point p) {
    IBlockState state = p.getBlockActualState(this.world);
    if (((Boolean)state.getValue((IProperty)BlockDoor.OPEN)).booleanValue()) {
      state = state.withProperty((IProperty)BlockDoor.OPEN, Boolean.valueOf(false));
    } else {
      state = state.withProperty((IProperty)BlockDoor.OPEN, Boolean.valueOf(true));
    } 
    p.setBlockState(this.world, state);
  }
  
  public String toString() {
    if (this.vtype != null)
      return getName() + "/" + this.vtype.key + "/" + getVillagerId() + "/" + getPos(); 
    return getName() + "/none/" + getVillagerId() + "/" + getPos();
  }
  
  private void triggerMobAttacks() {
    List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(this.world, EntityMob.class, getPos(), 16, 5);
    for (Entity ent : entities) {
      EntityMob mob = (EntityMob)ent;
      if (mob.getAttackTarget() == null && 
        mob.canEntityBeSeen((Entity)this))
        mob.setAttackTarget((EntityLivingBase)this); 
    } 
  }
  
  private void unlockForNearbyPlayers() {
    EntityPlayer player = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 5.0D, false);
    if (player != null) {
      UserProfile profile = this.mw.getProfile(player);
      if (profile != null)
        profile.unlockVillager(getCulture(), this.vtype); 
    } 
  }
  
  private void updateClothTexturePath() {
    if (this.vtype == null)
      return; 
    boolean[] naturalLayers = this.vtype.getClothLayersOfType("natural");
    String bestClothName = null;
    int clothLevel = -1;
    if (this.vtype.hasClothTexture("free")) {
      bestClothName = "free";
      clothLevel = 0;
    } 
    for (InvItem iv : this.inventory.keySet()) {
      if (iv.item instanceof ItemClothes && ((Integer)this.inventory.get(iv)).intValue() > 0) {
        ItemClothes clothes = (ItemClothes)iv.item;
        if (clothes.getClothPriority(iv.meta) > clothLevel)
          if (this.vtype.hasClothTexture(clothes.getClothName(iv.meta))) {
            bestClothName = clothes.getClothName(iv.meta);
            clothLevel = clothes.getClothPriority(iv.meta);
          }  
      } 
    } 
    if (bestClothName != null) {
      if (!bestClothName.equals(this.clothName)) {
        this.clothName = bestClothName;
        for (int layer = 0; layer < 2; layer++) {
          String texture;
          if (naturalLayers[layer] == true) {
            texture = this.vtype.getRandomClothTexture("natural", layer);
          } else {
            texture = this.vtype.getRandomClothTexture(bestClothName, layer);
          } 
          if (texture != null && texture.length() > 0) {
            if (texture.contains(":")) {
              this.clothTexture[layer] = new ResourceLocation(texture);
            } else {
              this.clothTexture[layer] = new ResourceLocation("millenaire", texture);
            } 
          } else {
            this.clothTexture[layer] = null;
          } 
        } 
      } 
    } else {
      this.clothName = null;
      for (int i = 0; i < 2; i++)
        this.clothTexture[i] = null; 
    } 
  }
  
  private void updateDialogue() {
    if (this.dialogueKey == null)
      return; 
    CultureLanguage.Dialogue d = getCulture().getDialogue(this.dialogueKey);
    if (d == null) {
      this.dialogueKey = null;
      return;
    } 
    long timePassed = this.world.getWorldTime() - this.dialogueStart;
    if ((((Integer)d.timeDelays.get(d.timeDelays.size() - 1)).intValue() + 100) < timePassed) {
      this.dialogueKey = null;
      return;
    } 
    String toSpeakKey = null;
    for (int i = 0; i < d.speechBy.size(); i++) {
      if (this.dialogueRole == ((Integer)d.speechBy.get(i)).intValue() && timePassed >= ((Integer)d.timeDelays.get(i)).intValue())
        toSpeakKey = "chat_" + d.key + "_" + i; 
    } 
    if (toSpeakKey != null && (this.speech_key == null || !this.speech_key.contains(toSpeakKey)))
      speakSentence(toSpeakKey, 0, 10, 1); 
  }
  
  private void updateHired() {
    try {
      if ((((getHealth() < getMaxHealth()) ? 1 : 0) & ((MillCommonUtilities.randomInt(1600) == 0) ? 1 : 0)) != 0)
        setHealth(getHealth() + 1.0F); 
      EntityPlayer entityplayer = this.world.getPlayerEntityByName(this.hiredBy);
      if (this.world.getWorldTime() > this.hiredUntil) {
        if (entityplayer != null)
          ServerSender.sendTranslatedSentence(entityplayer, 'f', "hire.hireover", new String[] { getName() }); 
        this.hiredBy = null;
        this.hiredUntil = 0L;
        VillagerRecord vr = getRecord();
        if (vr != null)
          vr.awayhired = false; 
        return;
      } 
      if (getAttackTarget() != null) {
        if (getPos().distanceTo((Entity)getAttackTarget()) > 80.0D || this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (getAttackTarget()).isDead)
          setAttackTarget(null); 
      } else if (isHostile() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL && (getTownHall()).closestPlayer != null && getPos().distanceTo((Entity)(getTownHall()).closestPlayer) <= 80.0D) {
        setAttackTarget((EntityLivingBase)this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 100.0D, true));
      } 
      if (getAttackTarget() == null) {
        List<?> list = this.world.getEntitiesWithinAABB(EntityCreature.class, (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).expand(16.0D, 8.0D, 16.0D));
        for (Object o : list) {
          if (getAttackTarget() == null) {
            EntityCreature creature = (EntityCreature)o;
            if (creature.getAttackTarget() == entityplayer && !(creature instanceof net.minecraft.entity.monster.EntityCreeper))
              setAttackTarget((EntityLivingBase)creature); 
          } 
        } 
        if (getAttackTarget() == null && this.aggressiveStance) {
          list = this.world.getEntitiesWithinAABB(EntityMob.class, (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).expand(16.0D, 8.0D, 16.0D));
          if (!list.isEmpty()) {
            setAttackTarget((EntityLivingBase)list.get(this.world.rand.nextInt(list.size())));
            if (getAttackTarget() instanceof net.minecraft.entity.monster.EntityCreeper)
              setAttackTarget(null); 
          } 
          if (getAttackTarget() == null) {
            list = this.world.getEntitiesWithinAABB(MillVillager.class, (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).expand(16.0D, 8.0D, 16.0D));
            for (Object o : list) {
              if (getAttackTarget() == null) {
                MillVillager villager = (MillVillager)o;
                if (villager.isHostile())
                  setAttackTarget((EntityLivingBase)villager); 
              } 
            } 
          } 
        } 
      } 
      Entity target = null;
      if (getAttackTarget() != null) {
        EntityLivingBase entityLivingBase = getAttackTarget();
        this.heldItem = getWeapon();
        this.heldItemOffHand = ItemStack.EMPTY;
        Path newPathEntity = getNavigator().getPathToEntityLiving((Entity)entityLivingBase);
        if (newPathEntity != null)
          getNavigator().setPath(newPathEntity, 0.5D); 
        attackEntity((Entity)getAttackTarget());
      } else {
        this.heldItem = ItemStack.EMPTY;
        this.heldItemOffHand = ItemStack.EMPTY;
        EntityPlayer entityPlayer = entityplayer;
        int dist = (int)getPos().distanceTo((Entity)entityPlayer);
        if (dist > 16) {
          teleportToEntity((Entity)entityplayer);
        } else if (dist > 4) {
          boolean rebuildPath = false;
          Path currentPath = getNavigator().getPath();
          if (currentPath == null) {
            rebuildPath = true;
          } else {
            PathPoint finalPoint = currentPath.getFinalPathPoint();
            if (finalPoint != null) {
              Point currentTargetPoint = new Point(finalPoint.x, finalPoint.y, finalPoint.z);
              if (currentTargetPoint.distanceTo((Entity)entityplayer) > 2.0D)
                rebuildPath = true;
            } else {
              rebuildPath = true;
            }
          }
          if (rebuildPath) {
            Path newPathEntity = getNavigator().getPathToEntityLiving((Entity)entityPlayer);
            if (newPathEntity != null)
              getNavigator().setPath(newPathEntity, 0.5D); 
          } 
        } 
      } 
      this.prevPoint = getPos();
      handleDoorsAndFenceGates();
    } catch (Exception e) {
      MillLog.printException("Error in hired onUpdate():", e);
    } 
  }
  
  private void updatePathIfNeeded(Point dest) throws Exception {
    if (dest == null)
      return; 
    if (this.pathEntity != null && this.pathEntity.getCurrentPathLength() > 0 && !MillCommonUtilities.chanceOn(50) && this.pathEntity.getCurrentTargetPathPoint() != null) {
      getNavigator().setPath((Path)this.pathEntity, 0.5D);
    } else if (!this.pathPlannerJPS.isBusy()) {
      computeNewPath(dest);
    } 
  }
  
  public float updateRotation(float f, float f1, float f2) {
    float f3;
    for (f3 = f1 - f; f3 < -180.0F; f3 += 360.0F);
    for (; f3 >= 180.0F; f3 -= 360.0F);
    if (f3 > f2)
      f3 = f2; 
    if (f3 < -f2)
      f3 = -f2; 
    return f + f3;
  }
  
  public void updateVillagerRecord() {
    if (!this.world.isRemote)
      getRecord().updateRecord(this); 
  }
  
  private boolean visitorNightAction() {
    this.visitorNbNights++;
    if (this.visitorNbNights > 5) {
      leaveVillage();
    } else if (isForeignMerchant()) {
      boolean hasItems = false;
      for (InvItem key : this.vtype.foreignMerchantStock.keySet()) {
        if (getHouse().countGoods(key) > 0)
          hasItems = true; 
      } 
      if (!hasItems)
        leaveVillage(); 
    } 
    return true;
  }
  
  public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    try {
      if (this.vtype == null) {
        MillLog.error(this, "Not saving villager due to null vtype.");
        return;
      } 
      super.writeEntityToNBT(nbttagcompound);
      nbttagcompound.setString("vtype", this.vtype.key);
      nbttagcompound.setString("culture", (getCulture()).key);
      nbttagcompound.setString("texture", this.texture.toString());
      if (this.housePoint != null)
        this.housePoint.write(nbttagcompound, "housePos"); 
      if (this.townHallPoint != null)
        this.townHallPoint.write(nbttagcompound, "townHallPos"); 
      if (getGoalDestPoint() != null)
        getGoalDestPoint().write(nbttagcompound, "destPoint"); 
      if (getGoalBuildingDestPoint() != null)
        getGoalBuildingDestPoint().write(nbttagcompound, "destBuildingPoint"); 
      if (getPathDestPoint() != null)
        getPathDestPoint().write(nbttagcompound, "pathDestPoint"); 
      if (this.prevPoint != null)
        this.prevPoint.write(nbttagcompound, "prevPoint"); 
      if (this.doorToClose != null)
        this.doorToClose.write(nbttagcompound, "doorToClose"); 
      nbttagcompound.setInteger("action", this.action);
      if (this.goalKey != null)
        nbttagcompound.setString("goal", this.goalKey); 
      nbttagcompound.setInteger("constructionJobId", this.constructionJobId);
      nbttagcompound.setString("firstName", this.firstName);
      nbttagcompound.setString("familyName", this.familyName);
      nbttagcompound.setInteger("gender", this.gender);
      nbttagcompound.setLong("lastSpeechLong", this.speech_started);
      nbttagcompound.setLong("villager_lid", getVillagerId());
      if (this.dialogueKey != null) {
        nbttagcompound.setString("dialogueKey", this.dialogueKey);
        nbttagcompound.setLong("dialogueStart", this.dialogueStart);
        nbttagcompound.setInteger("dialogueRole", this.dialogueRole);
        nbttagcompound.setInteger("dialogueColour", this.dialogueColour);
        nbttagcompound.setBoolean("dialogueChat", this.dialogueChat);
      } 
      NBTTagList nbttaglist = MillCommonUtilities.writeInventory(this.inventory);
      nbttagcompound.setTag("inventoryNew", (NBTBase)nbttaglist);
      nbttagcompound.setInteger("previousBlock", Block.getIdFromBlock(this.previousBlock));
      nbttagcompound.setInteger("previousBlockMeta", this.previousBlockMeta);
      if (this.hiredBy != null) {
        nbttagcompound.setString("hiredBy", this.hiredBy);
        nbttagcompound.setLong("hiredUntil", this.hiredUntil);
        nbttagcompound.setBoolean("aggressiveStance", this.aggressiveStance);
      } 
      nbttagcompound.setBoolean("isRaider", this.isRaider);
      nbttagcompound.setInteger("visitorNbNights", this.visitorNbNights);
      if (this.clothName != null) {
        nbttagcompound.setString("clothName", this.clothName);
        for (int layer = 0; layer < 2; layer++) {
          if (this.clothTexture[layer] != null)
            nbttagcompound.setString("clothTexture_" + layer, this.clothTexture[layer].toString()); 
        } 
      } 
    } catch (Exception e) {
      MillLog.printException("Exception when attempting to save villager " + this, e);
    } 
  }
  
  public void writeSpawnData(ByteBuf ds) {
    try {
      writeVillagerStreamData(ds, true);
    } catch (IOException e) {
      MillLog.printException("Error in writeSpawnData for villager " + this, e);
    } 
  }
  
  private void writeVillagerStreamData(ByteBuf bb, boolean isSpawn) throws IOException {
    PacketBuffer data;
    if (this.vtype == null) {
      MillLog.error(this, "Cannot write stream data due to null vtype.");
      return;
    } 
    if (bb instanceof PacketBuffer) {
      data = (PacketBuffer)bb;
    } else {
      data = new PacketBuffer(bb);
    } 
    data.writeLong(getVillagerId());
    StreamReadWrite.writeNullableString(this.vtype.culture.key, data);
    StreamReadWrite.writeNullableString(this.vtype.key, data);
    StreamReadWrite.writeNullableResourceLocation(this.texture, data);
    StreamReadWrite.writeNullableString(this.goalKey, data);
    data.writeInt(this.constructionJobId);
    StreamReadWrite.writeNullablePoint(this.housePoint, data);
    StreamReadWrite.writeNullablePoint(this.townHallPoint, data);
    StreamReadWrite.writeNullableString(this.firstName, data);
    StreamReadWrite.writeNullableString(this.familyName, data);
    data.writeInt(this.gender);
    StreamReadWrite.writeNullableString(this.hiredBy, data);
    data.writeBoolean(this.aggressiveStance);
    data.writeLong(this.hiredUntil);
    data.writeBoolean(this.isUsingBow);
    data.writeBoolean(this.isUsingHandToHand);
    data.writeBoolean(this.isRaider);
    StreamReadWrite.writeNullableString(this.speech_key, data);
    data.writeInt(this.speech_variant);
    data.writeLong(this.speech_started);
    StreamReadWrite.writeNullableItemStack(this.heldItem, data);
    StreamReadWrite.writeNullableItemStack(this.heldItemOffHand, data);
    StreamReadWrite.writeInventory(this.inventory, data);
    StreamReadWrite.writeNullableString(this.clothName, data);
    for (int i = 0; i < 2; i++)
      StreamReadWrite.writeNullableResourceLocation(this.clothTexture[i], data); 
    StreamReadWrite.writeNullablePoint(getGoalDestPoint(), data);
    data.writeBoolean(this.shouldLieDown);
    StreamReadWrite.writeNullableString(this.dialogueTargetFirstName, data);
    StreamReadWrite.writeNullableString(this.dialogueTargetLastName, data);
    data.writeChar(this.dialogueColour);
    data.writeBoolean(this.dialogueChat);
    data.writeFloat(getHealth());
    data.writeInt(this.visitorNbNights);
    if (getAttackTarget() != null) {
      StreamReadWrite.writeNullableUUID(getAttackTarget().getUniqueID(), data);
    } else {
      StreamReadWrite.writeNullableUUID(null, data);
    } 
    if (isSpawn) {
      calculateMerchantGoods();
      data.writeInt(this.merchantSells.size());
      for (TradeGood g : this.merchantSells.keySet()) {
        StreamReadWrite.writeNullableGoods(g, data);
        data.writeInt(((Integer)this.merchantSells.get(g)).intValue());
      } 
    } else {
      data.writeInt(-1);
    } 
    if (getGoalDestEntity() != null) {
      data.writeInt(getGoalDestEntity().getEntityId());
    } else {
      data.writeInt(-1);
    } 
    data.writeBoolean(this.isDead);
  }
}
