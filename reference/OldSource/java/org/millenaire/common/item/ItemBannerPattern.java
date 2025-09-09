package org.millenaire.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.forge.Mill;

public class ItemBannerPattern extends ItemMill {
  public ItemBannerPattern(String itemName) {
    super(itemName);
    setHasSubtypes(true);
    setMaxDamage(0);
  }
  
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    if (isInCreativeTab(tab))
      for (int i = 0; i < Mill.BANNER_SHORTNAMES.length; i++)
        items.add(new ItemStack(this, 1, i));  
  }
  
  public String getTranslationKey(ItemStack stack) {
    return getUnlocalizedName();
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (int i = 0; i < Mill.BANNER_SHORTNAMES.length; i++)
      ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName(), "inventory")); 
  }
}
