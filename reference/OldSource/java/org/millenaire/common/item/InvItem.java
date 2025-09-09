package org.millenaire.common.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public final class InvItem implements Comparable<InvItem> {
  private static Map<Integer, InvItem> CACHE = new HashMap<>();
  
  public static final int ANYENCHANTED = 1;
  
  public static final int ENCHANTEDSWORD = 2;
  
  public static final List<InvItem> freeGoods = new ArrayList<>();
  
  public static final HashMap<String, InvItem> INVITEMS_BY_NAME = new HashMap<>();
  
  public final Item item;
  
  public final Block block;
  
  public final ItemStack staticStack;
  
  public final ItemStack[] staticStackArray;
  
  public final int meta;
  
  public final int special;
  
  static {
    freeGoods.add(createInvItem(Blocks.DIRT, 0));
    freeGoods.add(createInvItem((Block)MillBlocks.EARTH_DECORATION, 0));
    freeGoods.add(createInvItem((Block)Blocks.WATER, 0));
    freeGoods.add(createInvItem(Blocks.SAPLING, 0));
    freeGoods.add(createInvItem((Block)Blocks.YELLOW_FLOWER, 0));
    freeGoods.add(createInvItem((Block)Blocks.RED_FLOWER, 0));
    freeGoods.add(createInvItem((Block)Blocks.TALLGRASS, 0));
    freeGoods.add(createInvItem(Blocks.CLAY, 0));
    freeGoods.add(createInvItem(Blocks.BREWING_STAND, 0));
    freeGoods.add(createInvItem((Block)Blocks.LEAVES, -1));
    freeGoods.add(createInvItem(Blocks.SAPLING, -1));
    freeGoods.add(createInvItem(Blocks.CAKE, 0));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHDIRT, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHDIRT_SLAB, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHGRAVEL, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHGRAVEL_SLAB, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHSLABS, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHSLABS_SLAB, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHSANDSTONE, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHSANDSTONE_SLAB, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHGRAVELSLABS, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHGRAVELSLABS_SLAB, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHOCHRESLABS, -1));
    freeGoods.add(createInvItem((Block)MillBlocks.PATHOCHRESLABS_SLAB, -1));
  }
  
  private static int computeHash(Item item, int meta, int special) {
    if (item == null)
      return (meta << 8) + (special << 12); 
    return item.hashCode() + (meta << 8) + (special << 12);
  }
  
  public static InvItem createInvItem(Block block) {
    return createInvItem(block, 0);
  }
  
  public static InvItem createInvItem(Block block, int meta) {
    Item item = Item.getItemFromBlock(block);
    int hash = computeHash(item, meta, 0);
    if (CACHE.containsKey(Integer.valueOf(hash))) {
      if (((InvItem)CACHE.get(Integer.valueOf(hash))).item == item)
        return CACHE.get(Integer.valueOf(hash)); 
      MillLog.error(null, "Collision between InvItem hash? " + CACHE.get(Integer.valueOf(hash)) + " has same hash as " + item + ":" + meta + ": " + hash);
    } 
    InvItem ii = new InvItem(block, meta);
    CACHE.put(Integer.valueOf(hash), ii);
    return ii;
  }
  
  public static InvItem createInvItem(IBlockState bs) {
    return createInvItem(bs.getBlock(), bs.getBlock().getMetaFromState(bs));
  }
  
  public static InvItem createInvItem(int special) {
    int hash = computeHash(null, 0, special);
    if (CACHE.containsKey(Integer.valueOf(hash))) {
      if (((InvItem)CACHE.get(Integer.valueOf(hash))).special == special)
        return CACHE.get(Integer.valueOf(hash)); 
      MillLog.error(null, "Collision between InvItem hash? " + CACHE.get(Integer.valueOf(hash)) + " has same hash as special: " + special + ": " + hash);
    } 
    InvItem ii = new InvItem(special);
    CACHE.put(Integer.valueOf(hash), ii);
    return ii;
  }
  
  public static InvItem createInvItem(Item item) {
    return createInvItem(item, 0);
  }
  
  public static InvItem createInvItem(Item item, int meta) {
    int hash = computeHash(item, meta, 0);
    if (CACHE.containsKey(Integer.valueOf(hash))) {
      if (((InvItem)CACHE.get(Integer.valueOf(hash))).item == item)
        return CACHE.get(Integer.valueOf(hash)); 
      MillLog.error(null, "Collision between InvItem hash? " + CACHE.get(Integer.valueOf(hash)) + " has same hash as " + item + ":" + meta + ": " + hash);
    } 
    InvItem ii = new InvItem(item, meta);
    CACHE.put(Integer.valueOf(hash), ii);
    return ii;
  }
  
  public static InvItem createInvItem(ItemStack is) {
    return createInvItem(is.getItem(), is.getMetadata());
  }
  
  private static void loadInvItemList(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        try {
          if (line.trim().length() > 0 && !line.startsWith("//")) {
            String[] temp = line.trim().split(";");
            if (temp.length > 2) {
              Item item = Item.getByNameOrId(temp[1]);
              if (item != null) {
                INVITEMS_BY_NAME.put(temp[0], createInvItem(item, Integer.parseInt(temp[2])));
                continue;
              } 
              Block block = Block.getBlockFromName(temp[1]);
              if (block == null) {
                MillLog.error(null, "Could not load good: " + temp[1]);
                continue;
              } 
              if (Item.getItemFromBlock(block) == null) {
                MillLog.error(null, "Tried to create good from block with no item: " + line);
                continue;
              } 
              INVITEMS_BY_NAME.put(temp[0], createInvItem(block, Integer.parseInt(temp[2])));
            } 
          } 
        } catch (Exception e) {
          MillLog.printException("Exception while reading line: " + line, e);
        } 
      } 
    } catch (IOException e) {
      MillLog.printException(e);
      return;
    } 
  }
  
  public static void loadItemList() {
    for (File loadDir : Mill.loadingDirs) {
      File mainList = new File(loadDir, "itemlist.txt");
      if (mainList.exists())
        loadInvItemList(mainList); 
    } 
    INVITEMS_BY_NAME.put("anyenchanted", createInvItem(1));
    INVITEMS_BY_NAME.put("enchantedsword", createInvItem(2));
    for (String key : INVITEMS_BY_NAME.keySet())
      ((InvItem)INVITEMS_BY_NAME.get(key)).setKey(key); 
  }
  
  private String key = null;
  
  private InvItem(Block block, int meta) {
    this.block = block;
    this.item = Item.getItemFromBlock(block);
    this.meta = meta;
    this.staticStack = new ItemStack(this.item, 1, meta);
    this.staticStackArray = new ItemStack[] { this.staticStack };
    this.special = 0;
    checkValidity();
  }
  
  private InvItem(int special) {
    this.special = special;
    this.staticStack = null;
    this.staticStackArray = new ItemStack[] { this.staticStack };
    this.item = null;
    this.block = null;
    this.meta = 0;
    checkValidity();
  }
  
  private InvItem(Item item, int meta) {
    this.item = item;
    if (Block.getBlockFromItem(item) != Blocks.AIR) {
      this.block = Block.getBlockFromItem(item);
    } else {
      this.block = null;
    } 
    this.meta = meta;
    this.staticStack = new ItemStack(item, 1, meta);
    this.staticStackArray = new ItemStack[] { this.staticStack };
    this.special = 0;
    checkValidity();
  }
  
  private InvItem(ItemStack is) {
    this.item = is.getItem();
    if (Block.getBlockFromItem(this.item) != Blocks.AIR) {
      this.block = Block.getBlockFromItem(this.item);
    } else {
      this.block = null;
    } 
    if (is.getItemDamage() > 0) {
      this.meta = is.getItemDamage();
    } else {
      this.meta = 0;
    } 
    this.staticStack = new ItemStack(this.item, 1, this.meta);
    this.staticStackArray = new ItemStack[] { this.staticStack };
    this.special = 0;
    checkValidity();
  }
  
  private InvItem(String s) {
    this.special = 0;
    if ((s.split("/")).length > 2) {
      int id = Integer.parseInt(s.split("/")[0]);
      if (Item.getItemById(id) == null) {
        MillLog.printException("Tried creating InvItem with null id from string: " + s, new Exception());
        this.item = null;
      } else {
        this.item = Item.getItemById(id);
      } 
      if (Block.getBlockById(id) == null) {
        this.block = null;
      } else {
        this.block = Block.getBlockById(id);
      } 
      this.meta = Integer.parseInt(s.split("/")[1]);
      this.staticStack = new ItemStack(this.item, 1, this.meta);
    } else {
      this.staticStack = null;
      this.item = null;
      this.block = null;
      this.meta = 0;
    } 
    this.staticStackArray = new ItemStack[] { this.staticStack };
    checkValidity();
  }
  
  private void checkValidity() {
    if (this.block == Blocks.AIR)
      MillLog.error(this, "Attempted to create an InvItem for air blocks."); 
    if (this.item == null && this.block == null && this.special == 0)
      MillLog.error(this, "Attempted to create an empty InvItem."); 
  }
  
  public int compareTo(InvItem ii) {
    if (this.special > 0 || ii.special > 0)
      return this.special - ii.special; 
    if (this.item == null || ii.item == null)
      return this.special - ii.special; 
    return this.item.getUnlocalizedName().compareTo(ii.item.getUnlocalizedName()) + this.meta - ii.meta;
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof InvItem))
      return false; 
    InvItem other = (InvItem)obj;
    return (other.item == this.item && other.meta == this.meta && other.special == this.special);
  }
  
  public Block getBlock() {
    return this.block;
  }
  
  public Item getItem() {
    return this.item;
  }
  
  public ItemStack getItemStack() {
    if (this.staticStack == null)
      return null; 
    return this.staticStack;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public String getName() {
    if (this.special == 1)
      return LanguageUtilities.string("ui.anyenchanted"); 
    if (this.special == 2)
      return LanguageUtilities.string("ui.enchantedsword"); 
    if (this.meta == -1 && this.block == Blocks.LOG)
      return LanguageUtilities.string("ui.woodforplanks"); 
    if (this.meta == 0 && this.block == Blocks.LOG)
      return LanguageUtilities.string("ui.woodoak"); 
    if (this.meta == 1 && this.block == Blocks.LOG)
      return LanguageUtilities.string("ui.woodpine"); 
    if (this.meta == 2 && this.block == Blocks.LOG)
      return LanguageUtilities.string("ui.woodbirch"); 
    if (this.meta == 3 && this.block == Blocks.LOG)
      return LanguageUtilities.string("ui.woodjungle"); 
    if (this.meta == 0 && this.block == Blocks.LOG2)
      return LanguageUtilities.string("ui.woodacacia"); 
    if (this.meta == 1 && this.block == Blocks.LOG2)
      return LanguageUtilities.string("ui.wooddarkoak"); 
    if (this.meta == -1)
      return (new ItemStack(this.item, 0)).getDisplayName(); 
    if (this.item != null)
      return (new ItemStack(this.item, 1, this.meta)).getDisplayName(); 
    MillLog.printException((Throwable)new MillLog.MillenaireException("Trying to get the name of an invalid InvItem."));
    return "id:" + this.item + ";meta:" + this.meta;
  }
  
  public String getTranslationKey() {
    return "_item:" + Item.getIdFromItem(this.item) + ":" + this.meta;
  }
  
  public int hashCode() {
    return computeHash(this.item, this.meta, this.special);
  }
  
  public boolean matches(InvItem ii) {
    return (ii.item == this.item && (ii.meta == this.meta || ii.meta == -1 || this.meta == -1));
  }
  
  public void setKey(String key) {
    this.key = key;
  }
  
  public String toString() {
    return getName() + "/" + this.meta;
  }
  
  public static interface IItemInitialEnchantmens {
    void applyEnchantments(ItemStack param1ItemStack);
  }
}
