package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.block.SoundType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomSnow extends BlockSnowBlock {
  public BlockCustomSnow(String blockName) {
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
    setHarvestLevel("shovel", 0);
    setSoundType(SoundType.SNOW);
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
  
  public int quantityDropped(Random random) {
    return 1;
  }
}
