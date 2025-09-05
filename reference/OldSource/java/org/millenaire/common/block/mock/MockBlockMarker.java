package org.millenaire.common.block.mock;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.IMetaBlockName;
import org.millenaire.common.item.MillItems;

public class MockBlockMarker extends Block implements IMetaBlockName {
  public enum Type implements IStringSerializable {
    PRESERVE_GROUND(0, "preserveground"),
    SLEEPING_POS(1, "sleepingpos"),
    SELLING_POS(2, "sellingpos"),
    CRAFTING_POS(3, "craftingpos"),
    DEFENDING_POS(4, "defendingpos"),
    SHELTER_POS(5, "shelterpos"),
    PATH_START_POS(6, "pathstartpos"),
    LEISURE_POS(7, "leisurepos"),
    STALL(8, "stall"),
    BRICK_SPOT(9, "brickspot"),
    HEALING_SPOT(10, "healingspot"),
    FISHING_SPOT(11, "fishingspot");
    
    public final int meta;
    
    public final String name;
    
    public static Type fromMeta(int meta) {
      for (Type t : values()) {
        if (t.meta == meta)
          return t; 
      } 
      return null;
    }
    
    public static int getMetaFromName(String name) {
      for (Type type : values()) {
        if (type.name.equalsIgnoreCase(name))
          return type.meta; 
      } 
      return -1;
    }
    
    Type(int m, String n) {
      this.meta = m;
      this.name = n;
    }
    
    public int getMetadata() {
      return this.meta;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String toString() {
      return "Marker Pos (" + this.name + ")";
    }
  }
  
  protected static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
  
  public static final PropertyEnum<Type> VARIANT = PropertyEnum.create("variant", Type.class);
  
  public MockBlockMarker(String blockName) {
    super(Material.ROCK);
    disableStats();
    setUnlocalizedName("millenaire." + blockName);
    setRegistryName(blockName);
    setBlockUnbreakable();
    setCreativeTab(MillBlocks.tabMillenaireContentCreator);
    this.useNeighborBrightness = true;
    this.fullBlock = false;
  }
  
  public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
    return true;
  }
  
  public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
    EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));
    switch (plantType) {
      case Crop:
        return (state.getValue((IProperty)VARIANT) == Type.PRESERVE_GROUND);
    } 
    return false;
  }
  
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { (IProperty)VARIANT });
  }
  
  public int damageDropped(IBlockState state) {
    return ((Type)state.getValue((IProperty)VARIANT)).getMetadata();
  }
  
  public float getAmbientOcclusionLightValue(IBlockState state) {
    return 1.0F;
  }
  
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    if (state.getValue((IProperty)VARIANT) == Type.PRESERVE_GROUND)
      return BlockFaceShape.SOLID; 
    return (face == EnumFacing.DOWN) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
  }
  
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    if (state.getValue((IProperty)VARIANT) == Type.PRESERVE_GROUND)
      return FULL_BLOCK_AABB; 
    return CARPET_AABB;
  }
  
  @Nullable
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    if (blockState.getValue((IProperty)VARIANT) == Type.PRESERVE_GROUND)
      return FULL_BLOCK_AABB; 
    return NULL_AABB;
  }
  
  public int getMetaFromState(IBlockState state) {
    return ((Type)state.getValue((IProperty)VARIANT)).meta;
  }
  
  public EnumBlockRenderType getRenderType(IBlockState state) {
    return EnumBlockRenderType.MODEL;
  }
  
  public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
    return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.0D, pos.getZ() + 1.0D);
  }
  
  public String getSpecialName(ItemStack stack) {
    return "tile.millenaire." + getRegistryName().getResourcePath() + "." + ((Type)getStateFromMeta(stack.getMetadata()).getValue((IProperty)VARIANT)).getName();
  }
  
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty((IProperty)VARIANT, Type.fromMeta(meta));
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    for (Type enumtype : Type.values())
      items.add(new ItemStack(this, 1, enumtype.getMetadata())); 
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    for (Type enumtype : Type.values())
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumtype.getMetadata(), new ModelResourceLocation(getRegistryName(), "variant=" + enumtype.getName())); 
  }
  
  public boolean isFullCube(IBlockState state) {
    return (state.getValue((IProperty)VARIANT) == Type.PRESERVE_GROUND);
  }
  
  public boolean isOpaqueCube(IBlockState state) {
    return (state.getValue((IProperty)VARIANT) == Type.PRESERVE_GROUND);
  }
  
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (worldIn.isRemote)
      return true; 
    if (playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == MillItems.NEGATION_WAND) {
      int meta = state.getBlock().getMetaFromState(state) + 1;
      if (Type.fromMeta(meta) == null)
        meta = 0; 
      worldIn.setBlockState(pos, state.withProperty((IProperty)VARIANT, Type.fromMeta(meta)), 3);
      Mill.proxy.sendLocalChat(playerIn, 'a', (Type.fromMeta(meta)).name);
      return true;
    } 
    return false;
  }
  
  public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    int color = 16777215;
    switch ((Type)stateIn.getValue((IProperty)VARIANT)) {
      case PRESERVE_GROUND:
        return;
      case SLEEPING_POS:
        color = 14680244;
        break;
      case SELLING_POS:
        color = 65484;
        break;
      case CRAFTING_POS:
        color = 1158400;
        break;
      case DEFENDING_POS:
        color = 16711680;
        break;
      case SHELTER_POS:
        color = 8323127;
        break;
      case PATH_START_POS:
        color = 721110;
        break;
      case LEISURE_POS:
        color = 15763456;
        break;
      case STALL:
        color = 9868800;
        break;
      case BRICK_SPOT:
        color = 8847360;
        break;
      case HEALING_SPOT:
        color = 53760;
        break;
      case FISHING_SPOT:
        color = 120;
        break;
    } 
    double r = (color >> 16 & 0xFF) / 255.0D;
    double g = (color >> 8 & 0xFF) / 255.0D;
    double b = (color & 0xFF) / 255.0D;
    worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, r, g, b, new int[0]);
  }
}
