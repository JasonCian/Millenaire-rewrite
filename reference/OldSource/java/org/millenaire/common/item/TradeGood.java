package org.millenaire.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class TradeGood {
  public static final String HIDDEN = "hidden";
  
  public static final String FOREIGNTRADE = "foreigntrade";
  
  public Culture culture;
  
  public InvItem item;
  
  public String name;
  
  private final int sellingPrice;
  
  private final int buyingPrice;
  
  public int reservedQuantity;
  
  public int targetQuantity;
  
  public int foreignMerchantPrice;
  
  public String requiredTag;
  
  public boolean autoGenerate = false;
  
  public int minReputation;
  
  public String travelBookCategory = null;
  
  public final String key;
  
  public boolean travelBookDisplay = true;
  
  public TradeGood(String key, Culture culture, InvItem iv) {
    this.item = iv;
    this.name = this.item.getName();
    this.sellingPrice = 0;
    this.buyingPrice = 1;
    this.requiredTag = null;
    this.culture = culture;
    this.key = key;
  }
  
  public TradeGood(String key, Culture culture, String name, InvItem item, int sellingPrice, int buyingPrice, int reservedQuantity, int targetQuantity, int foreignMerchantPrice, boolean autoGenerate, String tag, int minReputation, String desc) {
    this.culture = culture;
    this.key = key;
    this.name = name;
    this.item = item;
    this.sellingPrice = sellingPrice;
    this.buyingPrice = buyingPrice;
    this.requiredTag = tag;
    this.autoGenerate = autoGenerate;
    this.reservedQuantity = reservedQuantity;
    this.targetQuantity = targetQuantity;
    this.foreignMerchantPrice = foreignMerchantPrice;
    this.minReputation = minReputation;
    this.travelBookCategory = desc;
    this.travelBookDisplay = (this.travelBookCategory != null && !this.travelBookCategory.equals("hidden"));
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof TradeGood))
      return false; 
    TradeGood g = (TradeGood)obj;
    return g.item.equals(obj);
  }
  
  public int getBasicBuyingPrice(Building shop) {
    if (shop == null)
      return this.buyingPrice; 
    if ((shop.getTownHall()).villageType.buyingPrices.containsKey(this.item))
      return ((Integer)(shop.getTownHall()).villageType.buyingPrices.get(this.item)).intValue(); 
    return this.buyingPrice;
  }
  
  public int getBasicSellingPrice(Building shop) {
    if (shop == null)
      return this.sellingPrice; 
    if ((shop.getTownHall()).villageType.sellingPrices.containsKey(this.item))
      return ((Integer)(shop.getTownHall()).villageType.sellingPrices.get(this.item)).intValue(); 
    return this.sellingPrice;
  }
  
  public int getCalculatedBuyingPrice(Building shop, EntityPlayer player) {
    if (shop == null)
      return this.buyingPrice; 
    return shop.getBuyingPrice(this, player);
  }
  
  public int getCalculatedSellingPrice(Building shop, EntityPlayer player) {
    if (shop == null)
      return this.sellingPrice; 
    return shop.getSellingPrice(this, player);
  }
  
  public int getCalculatedSellingPrice(MillVillager merchant) {
    if (merchant == null)
      return this.foreignMerchantPrice; 
    if (merchant.merchantSells.containsKey(this))
      return ((Integer)merchant.merchantSells.get(this)).intValue(); 
    return this.foreignMerchantPrice;
  }
  
  public ItemStack getIcon() {
    return this.item.getItemStack();
  }
  
  public String getName() {
    if (this.item != null && this.item.block == Blocks.LOG && this.item.meta == -1)
      return LanguageUtilities.string("travelbook.anywood"); 
    return (new ItemStack(this.item.getItem(), 1, this.item.meta)).getDisplayName();
  }
  
  public int hashCode() {
    return this.item.hashCode();
  }
  
  public String toString() {
    return "Goods@" + this.culture.key + ":" + this.key + "/" + this.item.getItemStack().getUnlocalizedName();
  }
  
  public void validateGood() {
    if (this.buyingPrice != 0 && this.sellingPrice != 0 && this.sellingPrice <= this.buyingPrice)
      MillLog.minor(this, "Selling price of " + this.sellingPrice + " should be superior to buying price (" + this.buyingPrice + ")."); 
  }
}
