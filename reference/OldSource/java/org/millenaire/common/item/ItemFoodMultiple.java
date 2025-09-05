package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.MillBlocks;

public class ItemFoodMultiple extends ItemFood {
  public static final int DAMAGE_PER_PORTION = 64;
  
  private static final ItemStack MILKSTACK = new ItemStack(Items.MILK_BUCKET, 1);
  
  private final int healthAmount;
  
  private final boolean drink;
  
  private final int regenerationDuration;
  
  private final int drunkDuration;
  
  private PotionEffect potionId = null;
  
  private boolean clearEffects = false;
  
  public ItemFoodMultiple(String foodName, int healthAmount, int regenerationDuration, int foodAmount, float saturation, boolean drink, int drunkDuration) {
    super(foodAmount, saturation, false);
    this.healthAmount = healthAmount;
    this.drink = drink;
    this.regenerationDuration = regenerationDuration;
    this.drunkDuration = drunkDuration;
    if (healthAmount > 0)
      setAlwaysEdible(); 
    setCreativeTab(MillBlocks.tabMillenaire);
    setUnlocalizedName("millenaire." + foodName);
    setRegistryName(foodName);
    setMaxStackSize(1);
  }
  
  public int getDrunkDuration() {
    return this.drunkDuration;
  }
  
  public int getHealthAmount() {
    return this.healthAmount;
  }
  
  public EnumAction getItemUseAction(ItemStack itemstack) {
    if (this.drink)
      return EnumAction.DRINK; 
    return EnumAction.EAT;
  }
  
  public PotionEffect getPotionId() {
    return this.potionId;
  }
  
  public int getRegenerationDuration() {
    return this.regenerationDuration;
  }
  
  @SideOnly(Side.CLIENT)
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation((Item)this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
  }
  
  public boolean isClearEffects() {
    return this.clearEffects;
  }
  
  public boolean isDrink() {
    return this.drink;
  }
  
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    if (entityLiving instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer)entityLiving;
      if (!worldIn.isRemote && this.clearEffects)
        entityLiving.curePotionEffects(MILKSTACK); 
      entityplayer.getFoodStats().addStats(this, stack);
      entityplayer.heal(this.healthAmount);
      worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand
          .nextFloat() * 0.1F + 0.9F);
      onFoodEaten(stack, worldIn, entityplayer);
      entityplayer.addStat(StatList.getObjectUseStats((Item)this));
      if (this.drink)
        MillAdvancements.CHEERS.grant(entityplayer); 
      if (this.regenerationDuration > 0)
        entityplayer.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, this.regenerationDuration * 20, 0)); 
      if (this.drunkDuration > 0)
        entityplayer.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, this.drunkDuration * 20, 0)); 
      if (entityplayer instanceof EntityPlayerMP)
        CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack); 
    } 
    if (stack.getItemDamage() + 64 < stack.getMaxDamage()) {
      stack.setItemDamage(stack.getItemDamage() + 64);
    } else {
      stack.setCount(stack.getCount() - 1);
    } 
    return stack;
  }
  
  public ItemFoodMultiple setClearEffects(boolean clearEffects) {
    this.clearEffects = clearEffects;
    if (clearEffects)
      setAlwaysEdible(); 
    return this;
  }
  
  public ItemFood setPotionEffect(PotionEffect effect, float probability) {
    super.setPotionEffect(effect, probability);
    this.potionId = effect;
    setAlwaysEdible();
    return this;
  }
}
