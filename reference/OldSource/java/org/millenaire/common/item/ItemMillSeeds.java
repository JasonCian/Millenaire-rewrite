package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.world.UserProfile;

public class ItemMillSeeds extends ItemMill implements IPlantable {
  public final Block crops;
  
  public final String cropKey;
  
  public ItemMillSeeds(Block crops, String cropKey) {
    super(cropKey);
    this.crops = crops;
    this.cropKey = cropKey;
  }
  
  public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
    return this.crops.getDefaultState();
  }
  
  public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    return EnumPlantType.Crop;
  }
  
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack itemstack = player.getHeldItem(hand);
    IBlockState state = world.getBlockState(pos);
    if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, itemstack) && state.getBlock().canSustainPlant(state, (IBlockAccess)world, pos, EnumFacing.UP, this) && world
      .isAirBlock(pos.up())) {
      UserProfile profile = Mill.getMillWorld(world).getProfile(player);
      if (!profile.isTagSet("cropplanting_" + this.cropKey) && !MillConfigValues.DEV) {
        if (!world.isRemote)
          ServerSender.sendTranslatedSentence(player, 'f', "ui.cropplantingknowledge", new String[0]); 
        return EnumActionResult.FAIL;
      } 
      world.setBlockState(pos.up(), this.crops.getDefaultState());
      this.crops.onBlockPlacedBy(world, pos.up(), this.crops.getDefaultState(), (EntityLivingBase)player, itemstack);
      MillAdvancements.MASTER_FARMER.grant(player);
      if (player instanceof EntityPlayerMP)
        CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos.up(), itemstack); 
      itemstack.shrink(1);
      return EnumActionResult.SUCCESS;
    } 
    return EnumActionResult.FAIL;
  }
}
