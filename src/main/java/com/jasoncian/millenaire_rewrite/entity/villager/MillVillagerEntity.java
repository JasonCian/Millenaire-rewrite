package com.jasoncian.millenaire_rewrite.entity.villager;

import com.jasoncian.millenaire_rewrite.common.logging.LogCategory;
import com.jasoncian.millenaire_rewrite.common.logging.MillenaireLogger;
import com.jasoncian.millenaire_rewrite.culture.data.CultureType;
import com.jasoncian.millenaire_rewrite.entity.villager.data.VillagerGender;
import com.jasoncian.millenaire_rewrite.entity.villager.data.VillagerProfession;
import com.jasoncian.millenaire_rewrite.util.ModConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.RandomSource;

/**
 * 千年村民实体
 * 
 * 这是千年村庄模组的核心实体，代表具有文化特色的村民。
 * 支持以下功能：
 * - 基于文化的外观和行为差异
 * - 职业系统和技能发展
 * - 性别和年龄系统
 * - 基础AI行为（移动、交互、工作）
 * - 与玩家和其他村民的社交互动
 * 
 * @author JasonCian
 * @version 0.1.5-alpha
 * @since 2025-09-10
 */
public class MillVillagerEntity extends PathfinderMob implements VariantHolder<CultureType> {

    // ====================== 属性配置 ======================
    
    /**
     * 创建千年村民实体的属性配置
     * 基于PathfinderMob的默认属性进行适当调整
     */
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 10.0D);
    }

    // ====================== 数据访问器 ======================

    /**
     * 村民文化类型数据访问器
     * 用于在客户端和服务端同步村民的文化归属
     */
    private static final EntityDataAccessor<String> DATA_CULTURE_TYPE = SynchedEntityData
            .defineId(MillVillagerEntity.class, EntityDataSerializers.STRING);

    /**
     * 村民职业数据访问器
     * 用于同步村民的当前职业状态
     */
    private static final EntityDataAccessor<String> DATA_PROFESSION = SynchedEntityData
            .defineId(MillVillagerEntity.class, EntityDataSerializers.STRING);

    /**
     * 村民性别数据访问器
     * 用于同步村民的性别信息，影响外观和某些行为
     */
    private static final EntityDataAccessor<String> DATA_GENDER = SynchedEntityData.defineId(MillVillagerEntity.class,
            EntityDataSerializers.STRING);

    /**
     * 村民年龄数据访问器
     * 村民的生理年龄（以tick为单位）
     */
    private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(MillVillagerEntity.class,
            EntityDataSerializers.INT);

    /**
     * 村民名字数据访问器
     * 村民的个体名称，基于文化生成
     */
    private static final EntityDataAccessor<String> DATA_NAME = SynchedEntityData.defineId(MillVillagerEntity.class,
            EntityDataSerializers.STRING);

    // ====================== 实例字段 ======================

    /**
     * 村民文化类型缓存
     * 避免频繁的字符串解析
     */
    private CultureType cultureType;

    /**
     * 村民职业缓存
     */
    private VillagerProfession profession;

    /**
     * 村民性别缓存
     */
    private VillagerGender gender;

    // ====================== 构造函数 ======================

    /**
     * 创建千年村民实体
     * 
     * @param entityType 实体类型
     * @param level      世界级别
     */
    public MillVillagerEntity(EntityType<? extends MillVillagerEntity> entityType, Level level) {
        super(entityType, level);

        // 初始化默认值
        this.cultureType = CultureType.HUAXIA; // 默认华夏文化
        this.profession = VillagerProfession.FARMER; // 默认农民职业
        this.gender = VillagerGender.MALE; // 默认男性

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "创建千年村民实体: 文化={}, 职业={}, 性别={}",
                    this.cultureType, this.profession, this.gender);
        }
    }

    // ====================== 数据同步 ======================

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        // 注册同步数据
        this.entityData.define(DATA_CULTURE_TYPE, CultureType.NORMAN.name());
        this.entityData.define(DATA_PROFESSION, VillagerProfession.FARMER.name());
        this.entityData.define(DATA_GENDER, VillagerGender.MALE.name());
        this.entityData.define(DATA_AGE, 0);
        this.entityData.define(DATA_NAME, "");

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "千年村民实体数据同步定义完成");
        }
    }

    // ====================== AI系统 ======================

    @Override
    protected void registerGoals() {
        // 基础生存目标
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 游泳
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D)); // 恐慌逃跑

        // 社交目标
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 6.0F)); // 看向玩家
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this)); // 随机环顾

        // 移动目标
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D)); // 随机漫步

        // 目标选择器（暂时简单的自卫）
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this)); // 被攻击时反击

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "千年村民AI目标注册完成");
        }
    }

    // ====================== 生成和初始化 ======================

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty,
            @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnData,
            @Nullable CompoundTag dataTag) {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);

        // 随机生成文化、职业、性别等属性
        this.initializeRandomAttributes(level.getRandom());

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "千年村民生成完成: 文化={}, 职业={}, 性别={}",
                    this.getCultureType(), this.getProfession(), this.getGender());
        }

        return spawnData;
    }

    /**
     * 初始化随机属性
     * 
     * 根据生成环境和随机因子为村民分配文化、职业、性别等属性。
     * 
     * @param randomSource 随机数生成器
     */
    private void initializeRandomAttributes(RandomSource randomSource) {
        // 随机选择文化（后续可根据生物群系或维度调整）
        CultureType[] cultures = CultureType.values();
        this.setCultureType(cultures[randomSource.nextInt(cultures.length)]);

        // 随机选择性别
        VillagerGender[] genders = VillagerGender.values();
        this.setGender(genders[randomSource.nextInt(genders.length)]);

        // 根据文化选择合适的职业（暂时随机）
        VillagerProfession[] professions = VillagerProfession.values();
        this.setProfession(professions[randomSource.nextInt(professions.length)]);

        // 设置随机年龄（成年村民）
        this.setAge(randomSource.nextInt(24000) + 24000); // 1-2游戏日的年龄

        // 生成基于文化的名字
        this.generateRandomName(randomSource);
    }

    /**
     * 生成基于文化的随机名字
     * 
     * @param randomSource 随机数生成器
     */
    private void generateRandomName(RandomSource randomSource) {
        // TODO: 实现基于文化的名字生成系统
        // 暂时使用简单的占位符名字
        String[] maleNames = { "Aldric", "Beric", "Cedric", "Darian", "Edmund" };
        String[] femaleNames = { "Alara", "Bertha", "Cordelia", "Diana", "Eleanor" };

        String[] namePool = (this.gender == VillagerGender.MALE) ? maleNames : femaleNames;
        String name = namePool[randomSource.nextInt(namePool.length)];

        this.setVillagerName(name);
    }

    // ====================== 数据持久化 ======================

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        // 保存村民特有数据
        compound.putString("CultureType", this.getCultureType().name());
        compound.putString("Profession", this.getProfession().name());
        compound.putString("Gender", this.getGender().name());
        compound.putInt("VillagerAge", this.getAge());
        compound.putString("VillagerName", this.getVillagerName());

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "保存千年村民数据: {}", compound);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        // 读取村民特有数据
        if (compound.contains("CultureType")) {
            this.setCultureType(CultureType.valueOf(compound.getString("CultureType")));
        }
        if (compound.contains("Profession")) {
            this.setProfession(VillagerProfession.valueOf(compound.getString("Profession")));
        }
        if (compound.contains("Gender")) {
            this.setGender(VillagerGender.valueOf(compound.getString("Gender")));
        }
        if (compound.contains("VillagerAge")) {
            this.setAge(compound.getInt("VillagerAge"));
        }
        if (compound.contains("VillagerName")) {
            this.setVillagerName(compound.getString("VillagerName"));
        }

        if (ModConstants.DEBUG_MODE) {
            MillenaireLogger.debug(LogCategory.ENTITY, "读取千年村民数据: {}", compound);
        }
    }

    // ====================== Getter和Setter方法 ======================

    /**
     * 获取村民文化类型
     */
    public CultureType getCultureType() {
        if (this.cultureType == null) {
            String cultureName = this.entityData.get(DATA_CULTURE_TYPE);
            try {
                this.cultureType = CultureType.valueOf(cultureName);
            } catch (IllegalArgumentException e) {
                this.cultureType = CultureType.NORMAN; // 默认值
            }
        }
        return this.cultureType;
    }

    /**
     * 设置村民文化类型
     */
    public void setCultureType(CultureType cultureType) {
        this.cultureType = cultureType;
        this.entityData.set(DATA_CULTURE_TYPE, cultureType.name());
    }

    /**
     * 获取村民职业
     */
    public VillagerProfession getProfession() {
        if (this.profession == null) {
            String professionName = this.entityData.get(DATA_PROFESSION);
            try {
                this.profession = VillagerProfession.valueOf(professionName);
            } catch (IllegalArgumentException e) {
                this.profession = VillagerProfession.FARMER; // 默认值
            }
        }
        return this.profession;
    }

    /**
     * 设置村民职业
     */
    public void setProfession(VillagerProfession profession) {
        this.profession = profession;
        this.entityData.set(DATA_PROFESSION, profession.name());
    }

    /**
     * 获取村民性别
     */
    public VillagerGender getGender() {
        if (this.gender == null) {
            String genderName = this.entityData.get(DATA_GENDER);
            try {
                this.gender = VillagerGender.valueOf(genderName);
            } catch (IllegalArgumentException e) {
                this.gender = VillagerGender.MALE; // 默认值
            }
        }
        return this.gender;
    }

    /**
     * 设置村民性别
     */
    public void setGender(VillagerGender gender) {
        this.gender = gender;
        this.entityData.set(DATA_GENDER, gender.name());
    }

    /**
     * 获取村民年龄
     */
    public int getAge() {
        return this.entityData.get(DATA_AGE);
    }

    /**
     * 设置村民年龄
     */
    public void setAge(int age) {
        this.entityData.set(DATA_AGE, age);
    }

    /**
     * 获取村民名字
     */
    public String getVillagerName() {
        return this.entityData.get(DATA_NAME);
    }

    /**
     * 设置村民名字
     */
    public void setVillagerName(String name) {
        this.entityData.set(DATA_NAME, name);
    }

    // ====================== VariantHolder接口实现 ======================

    @Override
    public CultureType getVariant() {
        return this.getCultureType();
    }

    @Override
    public void setVariant(CultureType cultureType) {
        this.setCultureType(cultureType);
    }

    // ====================== 实体基础行为 ======================

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        // 村民不会因为距离玩家太远而消失
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        // 村民在和平模式下不会消失
        return false;
    }

    @Override
    public boolean isPushable() {
        // 村民可以被推动
        return true;
    }

    @Override
    protected boolean isImmobile() {
        // 村民不是固定不动的
        return false;
    }

    // ====================== 声音系统 ======================

    // TODO: 实现村民声音系统
    // @Override
    // protected SoundEvent getAmbientSound() { ... }
    // @Override
    // protected SoundEvent getHurtSound(DamageSource damageSource) { ... }
    // @Override
    // protected SoundEvent getDeathSound() { ... }
}
