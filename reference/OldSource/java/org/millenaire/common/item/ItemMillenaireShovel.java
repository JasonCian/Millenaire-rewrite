package org.millenaire.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;

public class ItemMillenaireShovel extends ItemSpade {
  public ItemMillenaireShovel(String itemName, Item.ToolMaterial material) {
    super(material);
    setUnlocalizedName("millenaire." + itemName);
    setRegistryName(itemName);
    setCreativeTab(MillBlocks.tabMillenaire);
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation((Item)this, 0, new ModelResourceLocation(
          getRegistryName(), "inventory"));
  }
}
