package org.millenaire.common.item;

public class ItemClothes extends ItemMill {
  private final String clothName;
  
  private final int priority;
  
  public ItemClothes(String itemName, int priority) {
    super(itemName);
    setMaxDamage(0);
    this.clothName = itemName;
    this.priority = priority;
  }
  
  public String getClothName(int meta) {
    return this.clothName;
  }
  
  public int getClothPriority(int meta) {
    return this.priority;
  }
}
