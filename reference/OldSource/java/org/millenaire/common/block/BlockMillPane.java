package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMillPane extends BlockPane {
  public BlockMillPane(String blockName, Material material, SoundType soundType) {
    super(material, true);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setHarvestLevel("axe", 0);
    setHardness(0.1F);
    setCreativeTab(MillBlocks.tabMillenaire);
    setSoundType(soundType);
  }
  
  public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
    BlockPos other = pos.offset(dir);
    IBlockState state = world.getBlockState(other);
    return (state.getBlock().canBeConnectedTo(world, other, dir.getOpposite()) || attachesTo(world, state, other, dir.getOpposite()) || state.getBlock() instanceof BlockMillWall);
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
}
