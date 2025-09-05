package org.millenaire.common.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityMockBanner;

public class ItemMockBanner extends ItemBlock {
  public static int BANNER_VILLAGE = 0;
  
  public static int BANNER_CULTURE = 1;
  
  public static final EnumDyeColor[] BANNER_COLOURS = new EnumDyeColor[] { EnumDyeColor.RED, EnumDyeColor.YELLOW };
  
  public static final String[] BANNER_DESIGNS = new String[] { "{Patterns:[{Pattern:dls,Color:15},{Pattern:ls,Color:15}]}", "{Patterns:[{Pattern:ls,Color:0},{Pattern:ts,Color:0},{Pattern:bs,Color:0}]}" };
  
  private final int bannerDesign;
  
  private final BlockBanner.BlockBannerHanging wallBanner;
  
  private final EnumDyeColor color;
  
  public static ItemStack makeBanner(Item banner, EnumDyeColor color, @Nullable NBTTagCompound patterns) {
    ItemStack itemstack = new ItemStack(banner, 1, color.getDyeDamage());
    if (patterns != null && !patterns.hasNoTags())
      itemstack.getOrCreateSubCompound("BlockEntityTag").setTag("Patterns", (NBTBase)patterns.copy().getTagList("Patterns", 10)); 
    return itemstack;
  }
  
  public ItemMockBanner(BlockBanner standingBanner, BlockBanner.BlockBannerHanging wallBanner, EnumDyeColor color, int design) {
    super((Block)standingBanner);
    this.wallBanner = wallBanner;
    this.color = color;
    this.bannerDesign = design;
    this.maxStackSize = 16;
    setCreativeTab(MillBlocks.tabMillenaireContentCreator);
    setMaxDamage(0);
  }
  
  public CreativeTabs getCreativeTab() {
    return MillBlocks.tabMillenaireContentCreator;
  }
  
  public int getMetadata(ItemStack stack) {
    return 0;
  }
  
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    if (isInCreativeTab(tab))
      try {
        items.add(makeBanner((Item)this, this.color, JsonToNBT.getTagFromJson(BANNER_DESIGNS[this.bannerDesign])));
      } catch (NBTException e) {
        items.add(makeBanner((Item)this, this.color, (NBTTagCompound)null));
      }  
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation((Item)this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
  
  public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    IBlockState iblockstate = worldIn.getBlockState(pos);
    boolean flag = iblockstate.getBlock().isReplaceable((IBlockAccess)worldIn, pos);
    if (facing != EnumFacing.DOWN && (iblockstate.getMaterial().isSolid() || flag) && (!flag || facing == EnumFacing.UP)) {
      pos = pos.offset(facing);
      ItemStack itemstack = player.getHeldItem(hand);
      if (player.canPlayerEdit(pos, facing, itemstack) && this.block.canPlaceBlockAt(worldIn, pos)) {
        pos = flag ? pos.down() : pos;
        if (facing == EnumFacing.UP) {
          int i = MathHelper.floor(((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 0xF;
          worldIn.setBlockState(pos, this.block.getDefaultState().withProperty((IProperty)BlockStandingSign.ROTATION, Integer.valueOf(i)), 3);
        } else {
          worldIn.setBlockState(pos, this.wallBanner.getDefaultState().withProperty((IProperty)BlockWallSign.FACING, (Comparable)facing), 3);
        } 
        TileEntity bannerEntity = worldIn.getTileEntity(pos);
        if (bannerEntity instanceof TileEntityMockBanner) {
          ItemStack bannerStack = itemstack.copy();
          bannerStack.getOrCreateSubCompound("BlockEntityTag").setTag("Base", (NBTBase)new NBTTagInt(this.color.getDyeDamage()));
          ((TileEntityMockBanner)bannerEntity).setItemValues(bannerStack, true);
        } 
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
