package com.jasoncian.millenaire_rewrite.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.jasoncian.millenaire_rewrite.core.ModBlockEntities;

import java.util.UUID;

/**
 * Village Stone Block Entity - 村庄核心方块实体
 * 
 * 基于legacy的TileEntityVillageStone.java重新实现
 * 负责存储和管理村庄相关数据：
 * - 村庄文化和类型
 * - 村民列表
 * - 村庄ID和名称
 * - 爆炸状态和测试变量
 */
public class VillageStoneBlockEntity extends BlockEntity {

    // 基于legacy TileEntityVillageStone的字段
    private String culture = "biome"; // 控制值，如果保持为"biome"则根据生物群系决定文化
    private boolean randomVillage = true;
    private String villageType;
    private String villageName;
    private boolean willExplode = false;
    private UUID villageID;
    private int testVar = 0; // 测试变量，参考legacy

    public VillageStoneBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.VILLAGE_STONE.get(), pPos, pBlockState);
    }

    /**
     * 定时更新方法
     * 在1.20.1中由VillageStoneBlock的getTicker调用
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return; // 只在服务端执行
        }

        // 这里可以添加村庄管理逻辑
        // 例如检查村民状态、更新建筑进度等
        
        // 基于legacy的onLoad逻辑，检查文化设置
        if ("biome".equalsIgnoreCase(culture)) {
            // TODO: 后续添加根据生物群系决定文化的逻辑
            // 参考legacy的getBiomeGenForCoords逻辑
        }
    }

    /**
     * 保存数据到NBT
     * 基于legacy TileEntity的writeToNBT
     */
    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        
        pTag.putString("culture", culture);
        pTag.putBoolean("randomVillage", randomVillage);
        if (villageType != null) {
            pTag.putString("villageType", villageType);
        }
        if (villageName != null) {
            pTag.putString("villageName", villageName);
        }
        pTag.putBoolean("willExplode", willExplode);
        if (villageID != null) {
            pTag.putUUID("villageID", villageID);
        }
        pTag.putInt("testVar", testVar);
    }

    /**
     * 从NBT加载数据
     * 基于legacy TileEntity的readFromNBT
     */
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        
        culture = pTag.getString("culture");
        randomVillage = pTag.getBoolean("randomVillage");
        if (pTag.contains("villageType")) {
            villageType = pTag.getString("villageType");
        }
        if (pTag.contains("villageName")) {
            villageName = pTag.getString("villageName");
        }
        willExplode = pTag.getBoolean("willExplode");
        if (pTag.contains("villageID")) {
            villageID = pTag.getUUID("villageID");
        }
        testVar = pTag.getInt("testVar");
    }

    // Getter和Setter方法

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
        setChanged();
    }

    public boolean isRandomVillage() {
        return randomVillage;
    }

    public void setRandomVillage(boolean randomVillage) {
        this.randomVillage = randomVillage;
        setChanged();
    }

    public String getVillageType() {
        return villageType;
    }

    public void setVillageType(String villageType) {
        this.villageType = villageType;
        setChanged();
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
        setChanged();
    }

    public boolean willExplode() {
        return willExplode;
    }

    public void setWillExplode(boolean willExplode) {
        this.willExplode = willExplode;
        setChanged();
    }

    public UUID getVillageID() {
        return villageID;
    }

    public void setVillageID(UUID villageID) {
        this.villageID = villageID;
        setChanged();
    }

    public int getTestVar() {
        return testVar;
    }

    public void incrementTestVar() {
        testVar++;
        setChanged();
    }

    public void resetTestVar() {
        testVar = 0;
        setChanged();
    }

    public void setTestVar(int testVar) {
        this.testVar = testVar;
        setChanged();
    }
}
