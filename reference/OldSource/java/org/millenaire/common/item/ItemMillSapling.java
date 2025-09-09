package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.world.UserProfile;

public class ItemMillSapling extends ItemBlock {
  public final String cropKey;
  
  public ItemMillSapling(Block block, String cropKey) {
    super(block);
    this.cropKey = cropKey;
  }
  
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    UserProfile profile = Mill.getMillWorld(world).getProfile(player);
    if (!profile.isTagSet("cropplanting_" + this.cropKey) && !MillConfigValues.DEV) {
      if (!world.isRemote)
        ServerSender.sendTranslatedSentence(player, 'f', "ui.cropplantingknowledge", new String[0]); 
      return EnumActionResult.FAIL;
    } 
    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }
}
