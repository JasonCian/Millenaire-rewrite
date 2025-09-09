package org.millenaire.common.entity;

import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;

public class TileEntityImportTable extends TileEntity {
  private String buildingKey = null;
  
  private int variation = 0;
  
  private int upgradeLevel = 0;
  
  private int length;
  
  private int width;
  
  private int startingLevel = -1;
  
  private int orientation = 0;
  
  private boolean exportSnow = false;
  
  private boolean importMockBlocks = true;
  
  private boolean autoconvertToPreserveGround = true;
  
  private boolean exportRegularChests = false;
  
  private Point parentTablePos = null;
  
  public void activate(EntityPlayer player) {
    if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == MillItems.SUMMONING_WAND) {
      if (player.world.isRemote && this.buildingKey != null) {
        MillLog.temp(this, "Activating. Building key: " + this.buildingKey);
        ClientSender.importTableImportBuildingPlan(player, new Point(this.pos), BuildingImportExport.EXPORT_DIR, this.buildingKey, false, this.variation, this.upgradeLevel, this.orientation, this.importMockBlocks);
      } else {
        sendUpdates();
      } 
    } else if (player.inventory.getCurrentItem() != ItemStack.EMPTY && player.inventory.getCurrentItem().getItem() == MillItems.NEGATION_WAND) {
      if (player.world.isRemote && this.buildingKey != null) {
        BuildingImportExport.importTableExportBuildingPlan(player.world, this, getUpgradeLevel());
      } else {
        sendUpdates();
      } 
    } else if (!player.world.isRemote) {
      sendUpdates();
      ServerSender.displayImportTableGUI(player, new Point(getPos()));
    } 
  }
  
  public boolean autoconvertToPreserveGround() {
    return this.autoconvertToPreserveGround;
  }
  
  public boolean exportRegularChests() {
    return this.exportRegularChests;
  }
  
  public boolean exportSnow() {
    return this.exportSnow;
  }
  
  public String getBuildingKey() {
    return this.buildingKey;
  }
  
  public int getLength() {
    return this.length;
  }
  
  public int getOrientation() {
    return this.orientation;
  }
  
  public Point getParentTablePos() {
    return this.parentTablePos;
  }
  
  public Point getPosPoint() {
    return new Point(this.pos);
  }
  
  public int getStartingLevel() {
    return this.startingLevel;
  }
  
  private IBlockState getState() {
    return this.world.getBlockState(this.pos);
  }
  
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.pos, 3, getUpdateTag());
  }
  
  public NBTTagCompound getUpdateTag() {
    return writeToNBT(new NBTTagCompound());
  }
  
  public int getUpgradeLevel() {
    return this.upgradeLevel;
  }
  
  public int getVariation() {
    return this.variation;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public boolean importMockBlocks() {
    return this.importMockBlocks;
  }
  
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    handleUpdateTag(pkt.getNbtCompound());
  }
  
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.buildingKey = compound.getString("buildingKey");
    this.variation = compound.getInteger("variation");
    this.length = compound.getInteger("length");
    this.width = compound.getInteger("width");
    this.upgradeLevel = compound.getInteger("upgradeLevel");
    this.startingLevel = compound.getInteger("startingLevel");
    this.orientation = compound.getInteger("orientation");
    this.exportSnow = compound.getBoolean("exportSnow");
    this.importMockBlocks = compound.getBoolean("importMockBlocks");
    this.autoconvertToPreserveGround = compound.getBoolean("autoconvertToPreserveGround");
    this.exportRegularChests = compound.getBoolean("exportRegularChests");
    this.parentTablePos = Point.read(compound, "parentTablePos");
  }
  
  private void sendUpdates() {
    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    this.world.notifyBlockUpdate(this.pos, getState(), getState(), 3);
    this.world.scheduleBlockUpdate(this.pos, getBlockType(), 0, 0);
    markDirty();
  }
  
  public void setAutoconvertToPreserveGround(boolean autoconvertToPreserveGround) {
    this.autoconvertToPreserveGround = autoconvertToPreserveGround;
  }
  
  public void setBuildingKey(String buildingKey) {
    this.buildingKey = buildingKey;
  }
  
  public void setExportRegularChests(boolean exportRegularChests) {
    this.exportRegularChests = exportRegularChests;
  }
  
  public void setExportSnow(boolean exportSnow) {
    this.exportSnow = exportSnow;
  }
  
  public void setImportMockBlocks(boolean importMockBlocks) {
    this.importMockBlocks = importMockBlocks;
  }
  
  public void setLength(int length) {
    this.length = length;
  }
  
  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }
  
  public void setParentTablePos(Point parentTablePos) {
    this.parentTablePos = parentTablePos;
  }
  
  public void setStartingLevel(int startingLevel) {
    this.startingLevel = startingLevel;
  }
  
  public void setUpgradeLevel(int upgradeLevel) {
    this.upgradeLevel = upgradeLevel;
  }
  
  private void updateAttachedSign() {
    Point signPos = (new Point(this.pos)).getRelative(-1.0D, 0.0D, 0.0D);
    signPos.setBlockState(this.world, Blocks.WALL_SIGN.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)EnumFacing.WEST));
    TileEntitySign sign = signPos.getSign(this.world);
    if (this.buildingKey != null) {
      sign.signText[0] = (ITextComponent)new TextComponentString(this.buildingKey + "_" + (char)(65 + this.variation) + this.upgradeLevel);
    } else {
      sign.signText[0] = (ITextComponent)new TextComponentString("");
    } 
    sign.signText[1] = (ITextComponent)new TextComponentString("Start level: " + this.startingLevel);
    sign.signText[2] = (ITextComponent)new TextComponentString(this.length + "x" + this.width);
    sign.markDirty();
    IBlockState iblockstate = this.world.getBlockState(sign.getPos());
    this.world.notifyBlockUpdate(sign.getPos(), iblockstate, iblockstate, 3);
  }
  
  public void updatePlan(String buildingKey, int length, int width, int variation, int level, int startLevel, EntityPlayer player) {
    this.buildingKey = buildingKey;
    MillLog.temp(this, "updatePlan : Updating buildingKey to: " + buildingKey);
    this.length = length;
    this.width = width;
    this.variation = variation;
    this.upgradeLevel = level;
    this.startingLevel = startLevel;
    updateAttachedSign();
    if (player != null)
      sendUpdates(); 
  }
  
  public void updateSettings(int upgradeLevel, int orientation, int startingLevel, boolean exportSnow, boolean importMockBlocks, boolean autoconvertToPreserveGround, boolean exportRegularChests, EntityPlayer player) {
    this.upgradeLevel = upgradeLevel;
    this.orientation = orientation;
    this.startingLevel = startingLevel;
    this.exportSnow = exportSnow;
    this.importMockBlocks = importMockBlocks;
    this.autoconvertToPreserveGround = autoconvertToPreserveGround;
    this.exportRegularChests = exportRegularChests;
    updateAttachedSign();
    if (player != null)
      sendUpdates(); 
  }
  
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    if (this.buildingKey != null)
      compound.setString("buildingKey", this.buildingKey); 
    compound.setInteger("variation", this.variation);
    compound.setInteger("length", this.length);
    compound.setInteger("width", this.width);
    compound.setInteger("upgradeLevel", this.upgradeLevel);
    compound.setInteger("startingLevel", this.startingLevel);
    compound.setInteger("orientation", this.orientation);
    compound.setBoolean("exportSnow", this.exportSnow);
    compound.setBoolean("importMockBlocks", this.importMockBlocks);
    compound.setBoolean("autoconvertToPreserveGround", this.autoconvertToPreserveGround);
    compound.setBoolean("exportRegularChests", this.exportRegularChests);
    if (this.parentTablePos != null)
      this.parentTablePos.write(compound, "parentTablePos"); 
    return compound;
  }
}
