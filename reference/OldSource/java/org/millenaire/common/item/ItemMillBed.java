package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityMillBed;

public class ItemMillBed extends ItemBlock {
  public ItemMillBed(Block bed) {
    super(bed);
  }
  
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    if (isInCreativeTab(tab))
      items.add(new ItemStack((Item)this, 1, 0)); 
  }
  
  public String getTranslationKey(ItemStack stack) {
    return getUnlocalizedName();
  }
  
  public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote)
      return EnumActionResult.SUCCESS; 
    if (facing != EnumFacing.UP)
      return EnumActionResult.FAIL; 
    IBlockState iblockstate = worldIn.getBlockState(pos);
    Block placedBlock = iblockstate.getBlock();
    boolean flag = placedBlock.isReplaceable((IBlockAccess)worldIn, pos);
    if (!flag)
      pos = pos.up(); 
    int i = MathHelper.floor((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3;
    EnumFacing enumfacing = EnumFacing.getFront(i);
    BlockPos blockpos = pos.offset(enumfacing);
    ItemStack itemstack = player.getHeldItem(hand);
    if (player.canPlayerEdit(pos, facing, itemstack) && player.canPlayerEdit(blockpos, facing, itemstack)) {
      IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
      boolean flag1 = iblockstate1.getBlock().isReplaceable((IBlockAccess)worldIn, blockpos);
      boolean flag2 = (flag || worldIn.isAirBlock(pos));
      boolean flag3 = (flag1 || worldIn.isAirBlock(blockpos));
      if (flag2 && flag3 && worldIn.getBlockState(pos.down()).isTopSolid() && worldIn.getBlockState(blockpos.down()).isTopSolid()) {
        IBlockState iblockstate2 = this.block.getDefaultState().withProperty((IProperty)BlockBed.OCCUPIED, Boolean.valueOf(false)).withProperty((IProperty)BlockHorizontal.FACING, (Comparable)enumfacing).withProperty((IProperty)BlockBed.PART, (Comparable)BlockBed.EnumPartType.FOOT);
        worldIn.setBlockState(pos, iblockstate2, 10);
        worldIn.setBlockState(blockpos, iblockstate2.withProperty((IProperty)BlockBed.PART, (Comparable)BlockBed.EnumPartType.HEAD), 10);
        SoundType soundtype = iblockstate2.getBlock().getSoundType(iblockstate2, worldIn, pos, (Entity)player);
        worldIn.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        TileEntity tileentity = worldIn.getTileEntity(blockpos);
        if (tileentity instanceof TileEntityMillBed)
          ((TileEntityMillBed)tileentity).setItemValues(itemstack); 
        TileEntity tileentity1 = worldIn.getTileEntity(pos);
        if (tileentity1 instanceof TileEntityMillBed)
          ((TileEntityMillBed)tileentity1).setItemValues(itemstack); 
        worldIn.notifyNeighborsRespectDebug(pos, placedBlock, false);
        worldIn.notifyNeighborsRespectDebug(blockpos, iblockstate1.getBlock(), false);
        if (player instanceof EntityPlayerMP)
          CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack); 
        itemstack.shrink(1);
        return EnumActionResult.SUCCESS;
      } 
      return EnumActionResult.FAIL;
    } 
    return EnumActionResult.FAIL;
  }
}
