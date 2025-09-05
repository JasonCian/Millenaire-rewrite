package org.millenaire.common.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.UserProfile;

public class ContainerTrade extends Container {
  public static final int DONATION_REP_MULTIPLIER = 4;
  
  private Building building;
  
  private MillVillager merchant;
  
  public static class MerchantSlot extends Slot {
    public MillVillager merchant;
    
    public EntityPlayer player;
    
    public final TradeGood good;
    
    public MerchantSlot(MillVillager merchant, EntityPlayer player, TradeGood good, int xpos, int ypos) {
      super(null, -1, xpos, ypos);
      this.merchant = merchant;
      this.good = good;
      this.player = player;
    }
    
    public ItemStack decrStackSize(int i) {
      return null;
    }
    
    public boolean getHasStack() {
      return (getStack() != null);
    }
    
    public int getSlotStackLimit() {
      return 0;
    }
    
    public ItemStack getStack() {
      return new ItemStack(this.good.item.getItem(), Math.max(Math.min(this.merchant.getHouse().countGoods(this.good.item), 99), 1), this.good.item.meta);
    }
    
    public boolean isItemValid(ItemStack itemstack) {
      return true;
    }
    
    public String isProblem() {
      if (this.merchant.getHouse().countGoods(this.good.item) < 1)
        return LanguageUtilities.string("ui.outofstock"); 
      int playerMoney = MillCommonUtilities.countMoney((IInventory)this.player.inventory);
      if (this.merchant.getCulture().getTradeGood(this.good.item) != null) {
        if (playerMoney < this.good.getCalculatedSellingPrice(this.merchant))
          return LanguageUtilities.string("ui.missingdeniers").replace("<0>", "" + (this.good.getCalculatedSellingPrice(this.merchant) - playerMoney)); 
      } else {
        MillLog.error(null, "Unknown trade good: " + this.good);
      } 
      return null;
    }
    
    public void onSlotChanged() {}
    
    public void putStack(ItemStack itemstack) {}
    
    public String toString() {
      return this.good.getName();
    }
  }
  
  public static class TradeSlot extends Slot {
    public final Building building;
    
    public final EntityPlayer player;
    
    public final TradeGood good;
    
    public final boolean sellingSlot;
    
    public TradeSlot(Building building, EntityPlayer player, boolean sellingSlot, TradeGood good, int xpos, int ypos) {
      super((IInventory)player.inventory, -1, xpos, ypos);
      this.building = building;
      if (good.item.item == Items.AIR)
        MillLog.error(good, "Trying to add air to the trade UI."); 
      this.good = good;
      this.player = player;
      this.sellingSlot = sellingSlot;
    }
    
    public ItemStack decrStackSize(int i) {
      return null;
    }
    
    public boolean getHasStack() {
      return (getStack() != null);
    }
    
    public int getSlotStackLimit() {
      return 0;
    }
    
    public ItemStack getStack() {
      if (this.sellingSlot)
        return new ItemStack(this.good.item.getItem(), Math.max(Math.min(this.building.countGoods(this.good.item.getItem(), this.good.item.meta), 99), 1), this.good.item.meta); 
      return new ItemStack(this.good.item.getItem(), Math.max(Math.min(MillCommonUtilities.countChestItems((IInventory)this.player.inventory, this.good.item.getItem(), this.good.item.meta), 99), 1), this.good.item.meta);
    }
    
    public boolean isItemValid(ItemStack itemstack) {
      return true;
    }
    
    public String isProblem() {
      if (this.sellingSlot) {
        if (this.building.countGoods(this.good.item.getItem(), this.good.item.meta) < 1 && this.good.requiredTag != null && !this.building.containsTags(this.good.requiredTag))
          return LanguageUtilities.string("ui.missingequipment") + ": " + this.good.requiredTag; 
        if (this.building.countGoods(this.good.item.getItem(), this.good.item.meta) < 1 && !this.good.autoGenerate)
          return LanguageUtilities.string("ui.outofstock"); 
        if (this.building.getTownHall().getReputation(this.player) < this.good.minReputation)
          return LanguageUtilities.string("ui.reputationneeded", new String[] { this.building.culture.getReputationLevelLabel(this.good.minReputation) }); 
        int playerMoney = MillCommonUtilities.countMoney((IInventory)this.player.inventory);
        if (playerMoney < this.good.getCalculatedSellingPrice(this.building, this.player))
          return LanguageUtilities.string("ui.missingdeniers").replace("<0>", "" + (this.good.getCalculatedSellingPrice(this.building, this.player) - playerMoney)); 
      } else if (MillCommonUtilities.countChestItems((IInventory)this.player.inventory, this.good.item.getItem(), this.good.item.meta) == 0) {
        return LanguageUtilities.string("ui.noneininventory");
      } 
      return null;
    }
    
    public void onSlotChanged() {}
    
    public void putStack(ItemStack itemstack) {}
    
    public String toString() {
      return this.good.name + (this.sellingSlot ? LanguageUtilities.string("ui.selling") : LanguageUtilities.string("ui.buying"));
    }
  }
  
  public int nbRowSelling = 0, nbRowBuying = 0;
  
  public ContainerTrade(EntityPlayer player, Building building) {
    this.building = building;
    Set<TradeGood> sellingGoods = building.getSellingGoods(player);
    int slotnb = 0;
    if (sellingGoods != null)
      for (TradeGood g : sellingGoods) {
        int slotrow = slotnb / 13;
        addSlotToContainer(new TradeSlot(building, player, true, g, 8 + 18 * (slotnb - 13 * slotrow), 32 + slotrow * 18));
        slotnb++;
      }  
    this.nbRowSelling = slotnb / 13 + 1;
    Set<TradeGood> buyingGoods = building.getBuyingGoods(player);
    slotnb = 0;
    if (buyingGoods != null)
      for (TradeGood g : buyingGoods) {
        int slotrow = slotnb / 13;
        addSlotToContainer(new TradeSlot(building, player, false, g, 8 + 18 * (slotnb - 13 * slotrow), 86 + slotrow * 18));
        slotnb++;
      }  
    this.nbRowBuying = slotnb / 13 + 1;
    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++)
        addSlotToContainer(new Slot((IInventory)player.inventory, k1 + l * 9 + 9, 8 + k1 * 18 + 36, 103 + l * 18 + 37)); 
    } 
    for (int i1 = 0; i1 < 9; i1++)
      addSlotToContainer(new Slot((IInventory)player.inventory, i1, 8 + i1 * 18 + 36, 198)); 
    if (!building.world.isRemote) {
      UserProfile profile = building.mw.getProfile(player);
      unlockTradableGoods(profile);
    } 
  }
  
  public ContainerTrade(EntityPlayer player, MillVillager merchant) {
    this.merchant = merchant;
    int slotnb = 0;
    Set<TradeGood> sellingGoods = merchant.merchantSells.keySet();
    if (sellingGoods != null)
      for (TradeGood g : sellingGoods) {
        int slotrow = slotnb / 13;
        addSlotToContainer(new MerchantSlot(merchant, player, g, 8 + 18 * (slotnb - 13 * slotrow), 32 + slotrow * 18));
        slotnb++;
      }  
    this.nbRowSelling = slotnb / 13 + 1;
    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++)
        addSlotToContainer(new Slot((IInventory)player.inventory, k1 + l * 9 + 9, 8 + k1 * 18 + 36, 103 + l * 18 + 37)); 
    } 
    for (int i1 = 0; i1 < 9; i1++)
      addSlotToContainer(new Slot((IInventory)player.inventory, i1, 8 + i1 * 18 + 36, 198)); 
    if (!merchant.world.isRemote) {
      UserProfile profile = merchant.mw.getProfile(player);
      unlockTradableGoods(profile);
    } 
  }
  
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return true;
  }
  
  public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
    TradeGood good = null;
    if (slotId >= 0 && slotId < this.inventorySlots.size()) {
      Slot slot = this.inventorySlots.get(slotId);
      if (slot != null && slot instanceof TradeSlot) {
        TradeSlot tslot = (TradeSlot)slot;
        good = tslot.good;
        UserProfile profile = this.building.mw.getProfile(player);
        int nbItems = 1;
        if (clickTypeIn == ClickType.QUICK_MOVE) {
          nbItems = 64;
        } else if (clickTypeIn == ClickType.PICKUP && dragType == 1) {
          nbItems = 8;
        } 
        if (tslot.isProblem() == null) {
          int playerMoney = MillCommonUtilities.countMoney((IInventory)player.inventory);
          if (tslot.sellingSlot) {
            if (playerMoney < good.getCalculatedSellingPrice(this.building, player) * nbItems)
              nbItems = MathHelper.floor((playerMoney / good.getCalculatedSellingPrice(this.building, player))); 
            if (!good.autoGenerate && this.building.countGoods(good.item.getItem(), good.item.meta) < nbItems)
              nbItems = this.building.countGoods(good.item.getItem(), good.item.meta); 
            nbItems = MillCommonUtilities.putItemsInChest((IInventory)player.inventory, good.item.getItem(), good.item.meta, nbItems);
            MillCommonUtilities.changeMoney((IInventory)player.inventory, -good.getCalculatedSellingPrice(this.building, player) * nbItems, player);
            if (!good.autoGenerate)
              this.building.takeGoods(good.item.getItem(), good.item.meta, nbItems); 
            if ((this.building.getTownHall()).controlledBy != null && !(this.building.getTownHall()).controlledBy.equals(player.getUniqueID())) {
              EntityPlayer owner = this.building.world.getPlayerEntityByUUID((this.building.getTownHall()).controlledBy);
              if (owner != null)
                MillAdvancements.MP_NEIGHBOURTRADE.grant(owner); 
            } 
            this.building.adjustReputation(player, good.getCalculatedSellingPrice(this.building, player) * nbItems);
            this.building.getTownHall().adjustLanguage(player, nbItems);
          } else {
            if (MillCommonUtilities.countChestItems((IInventory)player.inventory, good.item.getItem(), good.item.meta) < nbItems)
              nbItems = MillCommonUtilities.countChestItems((IInventory)player.inventory, good.item.getItem(), good.item.meta); 
            nbItems = this.building.storeGoods(good.item.getItem(), good.item.meta, nbItems);
            WorldUtilities.getItemsFromChest((IInventory)player.inventory, good.item.getItem(), good.item.meta, nbItems);
            if (!profile.donationActivated)
              MillCommonUtilities.changeMoney((IInventory)player.inventory, good.getCalculatedBuyingPrice(this.building, player) * nbItems, player); 
            if ((this.building.getTownHall()).controlledBy != null && !(this.building.getTownHall()).controlledBy.equals(player.getUniqueID())) {
              EntityPlayer owner = this.building.world.getPlayerEntityByUUID((this.building.getTownHall()).controlledBy);
              if (owner != null)
                MillAdvancements.MP_NEIGHBOURTRADE.grant(owner); 
            } 
            int repMultiplier = 1;
            if (profile.donationActivated)
              repMultiplier = 4; 
            this.building.adjustReputation(player, good.getCalculatedBuyingPrice(this.building, player) * nbItems * repMultiplier);
            this.building.getTownHall().adjustLanguage(player, nbItems);
          } 
        } 
        detectAndSendChanges();
        this.building.invalidateInventoryCache();
        if (!this.building.world.isRemote)
          this.building.sendChestPackets(player); 
        if (!this.building.world.isRemote)
          unlockTradableGoods(profile); 
        return slot.getStack();
      } 
      if (slot != null && slot instanceof MerchantSlot) {
        MerchantSlot tslot = (MerchantSlot)slot;
        good = tslot.good;
        int nbItems = 1;
        if (dragType == 1)
          nbItems = 64; 
        if (tslot.isProblem() == null) {
          int playerMoney = MillCommonUtilities.countMoney((IInventory)player.inventory);
          if (playerMoney < good.getCalculatedSellingPrice(this.merchant) * nbItems)
            nbItems = MathHelper.floor((playerMoney / good.getCalculatedSellingPrice(this.merchant))); 
          if (this.merchant.getHouse().countGoods(good.item) < nbItems)
            nbItems = this.merchant.getHouse().countGoods(good.item); 
          nbItems = MillCommonUtilities.putItemsInChest((IInventory)player.inventory, good.item.getItem(), good.item.meta, nbItems);
          MillCommonUtilities.changeMoney((IInventory)player.inventory, -good.getCalculatedSellingPrice(this.merchant) * nbItems, player);
          this.merchant.getHouse().takeGoods(good.item, nbItems);
          Mill.getMillWorld(player.world).getProfile(player).adjustLanguage((this.merchant.getCulture()).key, nbItems);
        } 
        detectAndSendChanges();
        this.merchant.getHouse().invalidateInventoryCache();
        if (!(this.merchant.getHouse()).world.isRemote)
          this.merchant.getHouse().sendChestPackets(player); 
        if (!this.merchant.world.isRemote) {
          UserProfile profile = this.merchant.mw.getProfile(player);
          unlockTradableGoods(profile);
        } 
        return slot.getStack();
      } 
    } 
    return null;
  }
  
  private void unlockTradableGoods(UserProfile profile) {
    List<TradeGood> unlockedGoods = new ArrayList<>();
    for (Slot slot : this.inventorySlots) {
      if (slot instanceof TradeSlot) {
        TradeSlot tradeSlot = (TradeSlot)slot;
        if (tradeSlot.isProblem() == null)
          unlockedGoods.add(tradeSlot.good); 
        continue;
      } 
      if (slot instanceof MerchantSlot) {
        MerchantSlot merchantSlot = (MerchantSlot)slot;
        if (merchantSlot.isProblem() == null)
          unlockedGoods.add(merchantSlot.good); 
      } 
    } 
    if (!unlockedGoods.isEmpty())
      if (this.building != null) {
        profile.unlockTradeGoods(this.building.culture, unlockedGoods);
      } else if (this.merchant != null) {
        profile.unlockTradeGoods(this.merchant.getCulture(), unlockedGoods);
      }  
  }
}
