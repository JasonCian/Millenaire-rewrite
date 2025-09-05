package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBars extends BlockPane {
  protected BlockBars(String blockName) {
    super(Material.WOOD, true);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setHardness(5.0F);
    setResistance(10.0F);
    setSoundType(SoundType.WOOD);
    setCreativeTab(MillBlocks.tabMillenaire);
  }
  
  public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
    BlockPos other = pos.offset(dir);
    IBlockState state = world.getBlockState(other);
    return (state.getBlock().canBeConnectedTo(world, other, dir.getOpposite()) || attachesTo(world, state, other, dir.getOpposite()) || state.getBlock() instanceof BlockMillWall);
  }
  
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty((IProperty)NORTH, Boolean.valueOf((canPaneConnectTo(worldIn, pos, EnumFacing.NORTH) || canPaneConnectTo(worldIn, pos, EnumFacing.SOUTH))))
      .withProperty((IProperty)SOUTH, Boolean.valueOf((canPaneConnectTo(worldIn, pos, EnumFacing.SOUTH) || canPaneConnectTo(worldIn, pos, EnumFacing.NORTH))))
      .withProperty((IProperty)WEST, Boolean.valueOf((canPaneConnectTo(worldIn, pos, EnumFacing.WEST) || canPaneConnectTo(worldIn, pos, EnumFacing.EAST))))
      .withProperty((IProperty)EAST, Boolean.valueOf((canPaneConnectTo(worldIn, pos, EnumFacing.EAST) || canPaneConnectTo(worldIn, pos, EnumFacing.WEST))));
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block)this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
}
