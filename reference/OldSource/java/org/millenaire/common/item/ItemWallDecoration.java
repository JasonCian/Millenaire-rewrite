package org.millenaire.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.entity.EntityWallDecoration;

public class ItemWallDecoration extends ItemMill {
  public int type;
  
  public ItemWallDecoration(String itemName, int type) {
    super(itemName);
    this.type = type;
  }
  
  public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack itemstack = player.getHeldItem(hand);
    BlockPos blockpos = pos.offset(facing);
    if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, itemstack)) {
      EntityWallDecoration entityhanging = new EntityWallDecoration(worldIn, blockpos, facing, this.type, false);
      if (entityhanging != null && entityhanging.onValidSurface()) {
        if (!worldIn.isRemote) {
          entityhanging.playPlaceSound();
          worldIn.spawnEntity((Entity)entityhanging);
        } 
        itemstack.shrink(1);
      } 
      return EnumActionResult.SUCCESS;
    } 
    return EnumActionResult.FAIL;
  }
}
