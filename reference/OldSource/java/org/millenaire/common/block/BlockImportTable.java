package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.entity.TileEntityImportTable;

public class BlockImportTable extends Block implements ITileEntityProvider {
  public BlockImportTable(String blockName) {
    super(Material.WOOD);
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setHardness(1.0F);
    setCreativeTab(MillBlocks.tabMillenaireContentCreator);
    setSoundType(SoundType.WOOD);
  }
  
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return (TileEntity)new TileEntityImportTable();
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), ""));
  }
  
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    TileEntityImportTable importTable = (TileEntityImportTable)world.getTileEntity(pos);
    if (importTable == null)
      return false; 
    importTable.activate(entityplayer);
    return true;
  }
}
