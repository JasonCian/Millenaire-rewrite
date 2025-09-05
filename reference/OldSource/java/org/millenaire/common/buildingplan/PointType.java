package org.millenaire.common.buildingplan;

import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.MillLog;

public class PointType {
  public static final String SUBTYPE_SIGN = "sign";
  
  public static final String SUBTYPE_MAINCHEST = "mainchest";
  
  public static final String SUBTYPE_LOCKEDCHEST = "lockedchest";
  
  public static final String SUBTYPE_VILLAGEBANNERWALL = "villageBannerWall";
  
  public static final String SUBTYPE_VILLAGEBANNERSTANDING = "villageBannerStanding";
  
  public static final String SUBTYPE_CULTUREBANNERWALL = "cultureBannerWall";
  
  public static final String SUBTYPE_CULTUREBANNERSTANDING = "cultureBannerStanding";
  
  public static HashMap<Integer, PointType> colourPoints = new HashMap<>();
  
  final int colour;
  
  final String specialType;
  
  final String label;
  
  private final Block block;
  
  private final int meta;
  
  private final IBlockState blockState;
  
  static PointType readColourPoint(String s) throws Exception {
    PointType pt;
    String[] params = s.trim().split(";", -1);
    if (params.length != 5 && params.length != 8)
      throw new MillLog.MillenaireException("Line " + s + " in blocklist.txt does not have five or eight fields."); 
    String[] rgb = params[4].split("/", -1);
    if (rgb.length != 3)
      throw new MillLog.MillenaireException("Colour in line " + s + " does not have three values."); 
    int colour = (Integer.parseInt(rgb[0]) << 16) + (Integer.parseInt(rgb[1]) << 8) + (Integer.parseInt(rgb[2]) << 0);
    String param_special_type = params[0];
    String param_block_location = params[1];
    String param_meta_or_values = params[2];
    String param_set_after = params[3];
    String param_cost_itemorblock = null;
    String param_cost_blockvalues = null;
    String param_cost_quantity = null;
    if (params.length >= 8) {
      param_cost_itemorblock = params[5];
      param_cost_blockvalues = params[6];
      param_cost_quantity = params[7];
    } 
    if (MillConfigValues.LogBuildingPlan >= 1)
      MillLog.major(null, "Loading colour point: " + BuildingPlan.getColourString(colour) + ", " + param_special_type); 
    if (param_block_location.length() == 0) {
      if (!SpecialPointTypeList.isSpecialPointTypeKnow(param_special_type))
        throw new MillLog.MillenaireException("Special block type " + param_special_type + " in line " + s + " is not a recognized special type."); 
      pt = new PointType(colour, param_special_type);
    } else if (param_meta_or_values.matches("\\d+")) {
      pt = new PointType(colour, param_special_type, param_block_location, Integer.parseInt(param_meta_or_values), Boolean.parseBoolean(param_set_after));
    } else {
      pt = new PointType(colour, param_special_type, param_block_location, param_meta_or_values, Boolean.parseBoolean(param_set_after));
    } 
    if (param_cost_itemorblock != null) {
      int quantity = Integer.parseInt(param_cost_quantity);
      if (quantity == 0) {
        pt.setCostToFree();
      } else if (param_cost_itemorblock.equals("anywood")) {
        pt.setCost(Items.STICK, quantity);
      } else if (param_cost_itemorblock.startsWith("item:")) {
        String itemKey = param_cost_itemorblock.substring("item:".length(), param_cost_itemorblock.length()).toLowerCase();
        if (InvItem.INVITEMS_BY_NAME.containsKey(itemKey)) {
          pt.setCost((InvItem)InvItem.INVITEMS_BY_NAME.get(itemKey), quantity);
        } else {
          throw new MillLog.MillenaireException("Unknown item: " + itemKey + " in line " + s);
        } 
      } else if (Block.getBlockFromName(param_cost_itemorblock) != null && Item.getItemFromBlock(Block.getBlockFromName(param_cost_itemorblock)) != Items.AIR) {
        Block block = Block.getBlockFromName(param_cost_itemorblock);
        if (param_cost_blockvalues.length() > 0) {
          IBlockState bs = BlockStateUtilities.getBlockStateWithValues(block.getDefaultState(), param_cost_blockvalues);
          pt.setCost(bs, quantity);
        } else {
          pt.setCost(block.getDefaultState(), quantity);
        } 
      } else if (Item.getByNameOrId(param_cost_itemorblock) != null) {
        pt.setCost(Item.getByNameOrId(param_cost_itemorblock), quantity);
      } else {
        throw new MillLog.MillenaireException("The cost of block " + param_special_type + " in line " + s + " could not be read. " + param_cost_itemorblock + " is not a recognised item or block.");
      } 
    } else if (pt.getBlockState() == null) {
      throw new MillLog.MillenaireException("The cost of block " + param_special_type + " is not set and it has no blockstate to use as a generic cost.");
    } 
    return pt;
  }
  
  private InvItem costItem = null;
  
  private IBlockState costBlockState = null;
  
  private int costQuantity = 1;
  
  boolean secondStep = false;
  
  public PointType(int colour, String name) {
    this.specialType = name;
    this.colour = colour;
    this.block = null;
    this.label = name;
    this.meta = -1;
    this.blockState = null;
  }
  
  public PointType(int colour, String label, String minecraftBlockName, int meta, boolean secondStep) {
    this.colour = colour;
    this.block = Block.getBlockFromName(minecraftBlockName);
    this.meta = meta;
    this.secondStep = secondStep;
    this.blockState = this.block.getStateFromMeta(meta);
    this.specialType = null;
    this.label = label;
  }
  
  public PointType(int colour, String label, String minecraftBlockName, String values, boolean secondStep) throws MillLog.MillenaireException {
    this.colour = colour;
    this.block = Block.getBlockFromName(minecraftBlockName);
    if (this.block == null)
      throw new MillLog.MillenaireException("Unknown block named " + minecraftBlockName + "."); 
    IBlockState bs = BlockStateUtilities.getBlockStateWithValues(this.block.getDefaultState(), values);
    this.blockState = bs;
    this.meta = this.block.getMetaFromState(bs);
    this.secondStep = secondStep;
    this.specialType = null;
    this.label = label;
  }
  
  public Block getBlock() {
    return this.block;
  }
  
  public IBlockState getBlockState() {
    return this.blockState;
  }
  
  public InvItem getCostInvItem() {
    if (this.costQuantity == 0)
      return null; 
    if (this.costItem != null)
      return this.costItem; 
    if (this.costBlockState != null)
      return InvItem.createInvItem(this.costBlockState); 
    if (this.blockState != null && this.blockState.getBlock() != Blocks.AIR)
      return InvItem.createInvItem(this.blockState); 
    MillLog.error(this, "PointType has neither blockstate nor explicit cost item. Cannot price it.");
    return null;
  }
  
  public int getCostQuantity() {
    return this.costQuantity;
  }
  
  public int getMeta() {
    return this.meta;
  }
  
  public String getSpecialType() {
    return this.specialType;
  }
  
  public boolean isSubType(String type) {
    if (this.specialType == null)
      return false; 
    return this.specialType.startsWith(type);
  }
  
  public boolean isType(String type) {
    return type.equalsIgnoreCase(this.specialType);
  }
  
  public void setCost(IBlockState blockState, int quantity) {
    this.costBlockState = blockState;
    this.costQuantity = quantity;
    this.costItem = null;
  }
  
  public void setCost(InvItem item, int quantity) {
    this.costBlockState = null;
    this.costQuantity = quantity;
    this.costItem = item;
  }
  
  public void setCost(Item item, int quantity) {
    this.costBlockState = null;
    this.costQuantity = quantity;
    this.costItem = InvItem.createInvItem(item, 0);
  }
  
  public void setCostToFree() {
    this.costBlockState = null;
    this.costQuantity = 0;
    this.costItem = null;
  }
  
  public String toString() {
    return this.label + "/" + this.specialType + "/" + BuildingPlan.getColourString(this.colour) + "/" + this.block + "/" + this.meta;
  }
}
