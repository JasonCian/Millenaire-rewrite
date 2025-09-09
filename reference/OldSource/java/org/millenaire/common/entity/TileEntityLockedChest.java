package org.millenaire.common.entity;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.ui.ContainerLockedChest;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.MillWorldData;

public class TileEntityLockedChest extends TileEntityLockableLoot implements ITickable, ISidedInventory {
  public static class InventoryLockedLargeChest implements ILockableContainer, ISidedInventory {
    private final String name;
    
    private final TileEntityLockedChest upperChest;
    
    private final TileEntityLockedChest lowerChest;
    
    public InventoryLockedLargeChest(String nameIn, TileEntityLockedChest upperChestIn, TileEntityLockedChest lowerChestIn) {
      this.name = nameIn;
      if (upperChestIn == null)
        upperChestIn = lowerChestIn; 
      if (lowerChestIn == null)
        lowerChestIn = upperChestIn; 
      this.upperChest = upperChestIn;
      this.lowerChest = lowerChestIn;
      if (upperChestIn.isLocked()) {
        lowerChestIn.setLockCode(upperChestIn.getLockCode());
      } else if (lowerChestIn.isLocked()) {
        upperChestIn.setLockCode(lowerChestIn.getLockCode());
      } 
    }
    
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return false;
    }
    
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      return false;
    }
    
    public void clear() {
      this.upperChest.clear();
      this.lowerChest.clear();
    }
    
    public void closeInventory(EntityPlayer player) {
      this.upperChest.closeInventory(player);
      this.lowerChest.closeInventory(player);
    }
    
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      Building building = Mill.getMillWorld(this.upperChest.world).getBuilding(this.upperChest.buildingPos);
      return (Container)new ContainerLockedChest((IInventory)playerInventory, (IInventory)this, playerIn, building, this.upperChest.isLockedFor(playerIn));
    }
    
    public ItemStack decrStackSize(int index, int count) {
      return (index >= this.upperChest.getSizeInventory()) ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count) : this.upperChest.decrStackSize(index, count);
    }
    
    public ITextComponent getDisplayName() {
      return hasCustomName() ? (ITextComponent)new TextComponentString(getName()) : (ITextComponent)new TextComponentTranslation(getName(), new Object[0]);
    }
    
    public int getField(int id) {
      return 0;
    }
    
    public int getFieldCount() {
      return 0;
    }
    
    public String getGuiID() {
      return this.upperChest.getGuiID();
    }
    
    public int getInventoryStackLimit() {
      return this.upperChest.getInventoryStackLimit();
    }
    
    public LockCode getLockCode() {
      return this.upperChest.getLockCode();
    }
    
    public String getName() {
      if (this.upperChest.hasCustomName())
        return this.upperChest.getName(); 
      return this.lowerChest.hasCustomName() ? this.lowerChest.getName() : this.name;
    }
    
    public int getSizeInventory() {
      return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }
    
    public int[] getSlotsForFace(EnumFacing side) {
      return new int[0];
    }
    
    public ItemStack getStackInSlot(int index) {
      return (index >= this.upperChest.getSizeInventory()) ? this.lowerChest.getStackInSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(index);
    }
    
    public boolean hasCustomName() {
      return (this.upperChest.hasCustomName() || this.lowerChest.hasCustomName());
    }
    
    public boolean isEmpty() {
      return (this.upperChest.isEmpty() && this.lowerChest.isEmpty());
    }
    
    public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
    }
    
    public boolean isLocked() {
      return (this.upperChest.isLocked() || this.lowerChest.isLocked());
    }
    
    public boolean isPartOfLargeChest(IInventory inventoryIn) {
      return (this.upperChest == inventoryIn || this.lowerChest == inventoryIn);
    }
    
    public boolean isUsableByPlayer(EntityPlayer player) {
      return (this.upperChest.isUsableByPlayer(player) && this.lowerChest.isUsableByPlayer(player));
    }
    
    public void markDirty() {
      this.upperChest.markDirty();
      this.lowerChest.markDirty();
    }
    
    public void openInventory(EntityPlayer player) {
      this.upperChest.openInventory(player);
      this.lowerChest.openInventory(player);
    }
    
    public ItemStack removeStackFromSlot(int index) {
      return (index >= this.upperChest.getSizeInventory()) ? this.lowerChest.removeStackFromSlot(index - this.upperChest.getSizeInventory()) : this.upperChest.removeStackFromSlot(index);
    }
    
    public void setField(int id, int value) {}
    
    public void setInventorySlotContents(int index, ItemStack stack) {
      if (index >= this.upperChest.getSizeInventory()) {
        this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
      } else {
        this.upperChest.setInventorySlotContents(index, stack);
      } 
    }
    
    public void setLockCode(LockCode code) {
      this.upperChest.setLockCode(code);
      this.lowerChest.setLockCode(code);
    }
  }
  
  public static void readUpdatePacket(PacketBuffer data, World world) {
    Point pos = StreamReadWrite.readNullablePoint(data);
    TileEntityLockedChest te = pos.getMillChest(world);
    if (te != null)
      try {
        te.buildingPos = StreamReadWrite.readNullablePoint(data);
        te.serverDevMode = data.readBoolean();
        byte nb = data.readByte();
        for (int i = 0; i < nb; i++) {
          ItemStack stack = StreamReadWrite.readNullableItemStack(data);
          if (stack == null) {
            MillLog.error(te, "Received a null stack!");
            stack = ItemStack.EMPTY;
          } 
          te.setInventorySlotContents(i, stack);
        } 
        te.loaded = true;
        if (Mill.clientWorld != null) {
          Building building = Mill.clientWorld.getBuilding(te.buildingPos);
          if (building != null)
            building.invalidateInventoryCache(); 
        } 
      } catch (IOException e) {
        MillLog.printException(te + ": Error in readUpdatePacket", e);
      }  
  }
  
  public static void registerFixesChest(DataFixer fixer) {
    fixer.registerWalker(FixTypes.BLOCK_ENTITY, (IDataWalker)new ItemStackDataLists(TileEntityLockedChest.class, new String[] { "Items" }));
  }
  
  private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
  
  public boolean adjacentChestChecked;
  
  public TileEntityLockedChest adjacentChestZNeg;
  
  public TileEntityLockedChest adjacentChestXPos;
  
  public TileEntityLockedChest adjacentChestXNeg;
  
  public TileEntityLockedChest adjacentChestZPos;
  
  public float lidAngle;
  
  public float prevLidAngle;
  
  public int numPlayersUsing;
  
  private int ticksSinceSync;
  
  public Point buildingPos = null;
  
  public boolean loaded = false;
  
  public boolean serverDevMode = false;
  
  public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
    return false;
  }
  
  public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
    return false;
  }
  
  public void checkForAdjacentChests() {
    if (!this.adjacentChestChecked) {
      this.adjacentChestChecked = true;
      this.adjacentChestXNeg = getAdjacentLockedChest(EnumFacing.WEST);
      this.adjacentChestXPos = getAdjacentLockedChest(EnumFacing.EAST);
      this.adjacentChestZNeg = getAdjacentLockedChest(EnumFacing.NORTH);
      this.adjacentChestZPos = getAdjacentLockedChest(EnumFacing.SOUTH);
    } 
  }
  
  public void closeInventory(EntityPlayer player) {
    if (!player.isSpectator() && getBlockType() instanceof org.millenaire.common.block.BlockLockedChest) {
      this.numPlayersUsing--;
      this.world.addBlockEvent(this.pos, getBlockType(), 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, getBlockType(), false);
    } 
  }
  
  public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
    fillWithLoot(playerIn);
    Building building = Mill.getMillWorld(this.world).getBuilding(this.buildingPos);
    return (Container)new ContainerLockedChest((IInventory)playerInventory, (IInventory)this, playerIn, building, isLockedFor(playerIn));
  }
  
  @Nullable
  protected TileEntityLockedChest getAdjacentLockedChest(EnumFacing side) {
    BlockPos blockpos = this.pos.offset(side);
    if (isLockedChestAt(blockpos)) {
      TileEntity tileentity = this.world.getTileEntity(blockpos);
      if (tileentity instanceof TileEntityLockedChest) {
        TileEntityLockedChest tileentitychest = (TileEntityLockedChest)tileentity;
        tileentitychest.setNeighbor(this, side.getOpposite());
        return tileentitychest;
      } 
    } 
    return null;
  }
  
  public ITextComponent getDisplayName() {
    if (this.buildingPos == null)
      return LanguageUtilities.textComponent("ui.unlockedchest"); 
    Building building = null;
    if (Mill.clientWorld != null)
      building = Mill.clientWorld.getBuilding(this.buildingPos); 
    if (building == null)
      return LanguageUtilities.textComponent("ui.unlockedchest"); 
    String s = building.getNativeBuildingName();
    if (building.chestLocked)
      return (ITextComponent)new TextComponentString(s + ": " + LanguageUtilities.string("ui.lockedchest")); 
    return (ITextComponent)new TextComponentString(s + ": " + LanguageUtilities.string("ui.unlockedchest"));
  }
  
  public String getGuiID() {
    return "minecraft:chest";
  }
  
  public int getInventoryStackLimit() {
    return 64;
  }
  
  public String getInvLargeName() {
    if (this.buildingPos == null)
      return LanguageUtilities.string("ui.largeunlockedchest"); 
    Building building = null;
    if (Mill.clientWorld != null)
      building = Mill.clientWorld.getBuilding(this.buildingPos); 
    if (building == null)
      return LanguageUtilities.string("ui.largeunlockedchest"); 
    String s = building.getNativeBuildingName();
    if (building.chestLocked)
      return s + ": " + LanguageUtilities.string("ui.largelockedchest"); 
    return s + ": " + LanguageUtilities.string("ui.largeunlockedchest");
  }
  
  protected NonNullList<ItemStack> getItems() {
    return this.chestContents;
  }
  
  public String getName() {
    return hasCustomName() ? this.customName : "container.chest";
  }
  
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(this.pos.add(-1, 0, -1), this.pos.add(2, 2, 2));
  }
  
  public IItemHandler getSingleChestHandler() {
    return (IItemHandler)getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
  }
  
  public int getSizeInventory() {
    return 27;
  }
  
  public int[] getSlotsForFace(EnumFacing side) {
    return new int[0];
  }
  
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return false; 
    return super.hasCapability(capability, facing);
  }
  
  public void invalidate() {
    super.invalidate();
    updateContainingBlockInfo();
    checkForAdjacentChests();
  }
  
  public boolean isEmpty() {
    for (ItemStack itemstack : this.chestContents) {
      if (!itemstack.isEmpty())
        return false; 
    } 
    return true;
  }
  
  private boolean isLockedChestAt(BlockPos posIn) {
    if (this.world == null)
      return false; 
    Block block = this.world.getBlockState(posIn).getBlock();
    return block instanceof org.millenaire.common.block.BlockLockedChest;
  }
  
  public boolean isLockedFor(EntityPlayer player) {
    if (player == null) {
      MillLog.printException("Null player", new Exception());
      return true;
    } 
    if (!this.loaded && this.world.isRemote)
      return true; 
    if (this.buildingPos == null)
      return false; 
    if (!this.world.isRemote && MillConfigValues.DEV)
      return false; 
    if (this.serverDevMode)
      return false; 
    MillWorldData mw = Mill.getMillWorld(this.world);
    if (mw == null) {
      MillLog.printException("Null MillWorldData", new Exception());
      return true;
    } 
    Building building = mw.getBuilding(this.buildingPos);
    if (building == null)
      return true; 
    if (building.lockedForPlayer(player))
      return true; 
    return false;
  }
  
  public void openInventory(EntityPlayer player) {
    if (!player.isSpectator()) {
      if (this.numPlayersUsing < 0)
        this.numPlayersUsing = 0; 
      this.numPlayersUsing++;
      this.world.addBlockEvent(this.pos, getBlockType(), 1, this.numPlayersUsing);
      this.world.notifyNeighborsOfStateChange(this.pos, getBlockType(), false);
    } 
  }
  
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.chestContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    if (!checkLootAndRead(compound))
      ItemStackHelper.loadAllItems(compound, this.chestContents); 
    if (compound.hasKey("CustomName", 8))
      this.customName = compound.getString("CustomName"); 
    this.buildingPos = Point.read(compound, "buildingPos");
    if (Mill.clientWorld != null) {
      Building building = Mill.clientWorld.getBuilding(this.buildingPos);
      if (building != null)
        building.invalidateInventoryCache(); 
    } 
  }
  
  public boolean receiveClientEvent(int id, int type) {
    if (id == 1) {
      this.numPlayersUsing = type;
      return true;
    } 
    return super.receiveClientEvent(id, type);
  }
  
  public void sendUpdatePacket(EntityPlayer player) {
    ServerSender.sendLockedChestUpdatePacket(this, player);
  }
  
  private void setNeighbor(TileEntityLockedChest chestTe, EnumFacing side) {
    if (chestTe.isInvalid()) {
      this.adjacentChestChecked = false;
    } else if (this.adjacentChestChecked) {
      switch (side) {
        case NORTH:
          if (this.adjacentChestZNeg != chestTe)
            this.adjacentChestChecked = false; 
          break;
        case SOUTH:
          if (this.adjacentChestZPos != chestTe)
            this.adjacentChestChecked = false; 
          break;
        case EAST:
          if (this.adjacentChestXPos != chestTe)
            this.adjacentChestChecked = false; 
          break;
        case WEST:
          if (this.adjacentChestXNeg != chestTe)
            this.adjacentChestChecked = false; 
          break;
      } 
    } 
  }
  
  public void update() {
    checkForAdjacentChests();
    int i = this.pos.getX();
    int j = this.pos.getY();
    int k = this.pos.getZ();
    this.ticksSinceSync++;
    if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
      this.numPlayersUsing = 0;
      for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((i - 5.0F), (j - 5.0F), (k - 5.0F), ((i + 1) + 5.0F), ((j + 1) + 5.0F), ((k + 1) + 5.0F)))) {
        if (entityplayer.openContainer instanceof ContainerLockedChest) {
          IInventory iinventory = ((ContainerLockedChest)entityplayer.openContainer).getLowerChestInventory();
          if (iinventory == this || (iinventory instanceof InventoryLockedLargeChest && ((InventoryLockedLargeChest)iinventory).isPartOfLargeChest((IInventory)this)))
            this.numPlayersUsing++; 
        } 
      } 
    } 
    this.prevLidAngle = this.lidAngle;
    if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
      double d1 = i + 0.5D;
      double d2 = k + 0.5D;
      if (this.adjacentChestZPos != null)
        d2 += 0.5D; 
      if (this.adjacentChestXPos != null)
        d1 += 0.5D; 
      this.world.playSound((EntityPlayer)null, d1, j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
    } 
    if ((this.numPlayersUsing <= 0 && this.lidAngle > 0.0F) || (this.numPlayersUsing > 0 && this.lidAngle < 1.0F)) {
      float f2 = this.lidAngle;
      if (this.numPlayersUsing > 0) {
        this.lidAngle += 0.1F;
      } else {
        this.lidAngle -= 0.1F;
      } 
      if (this.lidAngle > 1.0F)
        this.lidAngle = 1.0F; 
      if (this.lidAngle < 0.5F && f2 >= 0.5F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
        double d3 = i + 0.5D;
        double d0 = k + 0.5D;
        if (this.adjacentChestZPos != null)
          d0 += 0.5D; 
        if (this.adjacentChestXPos != null)
          d3 += 0.5D; 
        this.world.playSound((EntityPlayer)null, d3, j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      } 
      if (this.lidAngle < 0.0F)
        this.lidAngle = 0.0F; 
    } 
  }
  
  public void updateContainingBlockInfo() {
    super.updateContainingBlockInfo();
    this.adjacentChestChecked = false;
  }
  
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    if (!checkLootAndWrite(compound))
      ItemStackHelper.saveAllItems(compound, this.chestContents); 
    if (hasCustomName())
      compound.setString("CustomName", this.customName); 
    if (this.buildingPos != null)
      this.buildingPos.write(compound, "buildingPos"); 
    return compound;
  }
}
