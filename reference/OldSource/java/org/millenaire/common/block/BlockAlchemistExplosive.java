package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.utilities.WorldUtilities;

public class BlockAlchemistExplosive extends Block {
  private static final int EXPLOSION_RADIUS = 32;
  
  public BlockAlchemistExplosive(String blockName) {
    super(Material.ROCK);
    setHarvestLevel("pickaxe", 0);
    setHardness(1.5F);
    setResistance(10.0F);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setCreativeTab(MillBlocks.tabMillenaire);
  }
  
  private void alchemistExplosion(World world, BlockPos pos) {
    int centreX = pos.getX();
    int centreY = pos.getY();
    int centreZ = pos.getZ();
    WorldUtilities.setBlockAndMetadata(world, centreX, centreY, centreZ, Blocks.AIR, 0, true, false);
    for (int dy = 32; dy >= -32; dy--) {
      if (dy + centreY >= 0 && dy + centreY < 128)
        for (int dx = -32; dx <= 32; dx++) {
          for (int dz = -32; dz <= 32; dz++) {
            if (dx * dx + dy * dy + dz * dz <= 1024) {
              Block block = WorldUtilities.getBlock(world, centreX + dx, centreY + dy, centreZ + dz);
              if (block != Blocks.AIR)
                WorldUtilities.setBlockAndMetadata(world, centreX + dx, centreY + dy, centreZ + dz, Blocks.AIR, 0, true, false); 
            } 
          } 
        }  
    } 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(
          getRegistryName(), ""));
  }
  
  public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
    alchemistExplosion(world, pos);
  }
}
