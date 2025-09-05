package org.millenaire.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;

public class ItemMillenaireArmour extends ItemArmor {
  public ItemMillenaireArmour(String itemName, ItemArmor.ArmorMaterial material, EntityEquipmentSlot type) {
    super(material, -1, type);
    setUnlocalizedName("millenaire." + itemName);
    setRegistryName(itemName);
    setCreativeTab(MillBlocks.tabMillenaire);
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation((Item)this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
}
