package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillStairs extends BlockStairs {
  public BlockMillStairs(String blockName, IBlockState baseBlock) {
    super(baseBlock);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    this.useNeighborBrightness = true;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(
          getRegistryName(), "facing=east,half=bottom,shape=straight"));
  }
}
