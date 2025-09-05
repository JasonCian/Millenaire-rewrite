package org.millenaire.common.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class ItemParchment extends ItemMill {
  private static final String NBT_VILLAGE_POS = "village_pos_";
  
  public static final int NORMAN_VILLAGERS = 1;
  
  public static final int NORMAN_BUILDINGS = 2;
  
  public static final int NORMAN_ITEMS = 3;
  
  public static final int VILLAGE_BOOK = 4;
  
  public static final int INDIAN_VILLAGERS = 5;
  
  public static final int INDIAN_BUILDINGS = 6;
  
  public static final int INDIAN_ITEMS = 7;
  
  public static final int MAYAN_VILLAGERS = 9;
  
  public static final int MAYAN_BUILDINGS = 10;
  
  public static final int MAYAN_ITEMS = 11;
  
  public static final int JAPANESE_VILLAGERS = 16;
  
  public static final int JAPANESE_BUILDINGS = 17;
  
  public static final int JAPANESE_ITEMS = 18;
  
  public static final int SADHU = 15;
  
  private final int[] textsId;
  
  public static ItemStack createParchmentForVillage(Building townHall) {
    ItemStack parchment = new ItemStack(MillItems.PARCHMENT_VILLAGE_SCROLL);
    NBTTagCompound compound = new NBTTagCompound();
    townHall.getPos().write(compound, "village_pos_");
    parchment.setTagCompound(compound);
    return parchment;
  }
  
  public ItemParchment(String itemName, int t, boolean obsolete) {
    this(itemName, new int[] { t }, obsolete);
  }
  
  public ItemParchment(String itemName, int[] tIds, boolean obsolete) {
    super(itemName);
    this.textsId = tIds;
    this.maxStackSize = 1;
    if (obsolete)
      setCreativeTab(null); 
  }
  
  private void displayVillageBook(EntityPlayer player, ItemStack is) {
    if (player.world.isRemote)
      return; 
    Point p = Point.read(is.getTagCompound(), "village_pos_");
    Building townHall = Mill.getMillWorld(player.world).getBuilding(p);
    if (townHall == null) {
      ServerSender.sendTranslatedSentence(player, '6', "panels.invalidid", new String[0]);
      return;
    } 
    Chunk chunk = player.world.getChunkFromBlockCoords(new BlockPos(p.getiX(), 0, p.getiZ()));
    if (!chunk.isLoaded()) {
      ServerSender.sendTranslatedSentence(player, '6', "panels.toofar", new String[0]);
      return;
    } 
    if (!townHall.isActive) {
      ServerSender.sendTranslatedSentence(player, '6', "panels.toofar", new String[0]);
      return;
    } 
    ServerSender.displayVillageBookGUI(player, p);
  }
  
  @SideOnly(Side.CLIENT)
  public String getItemStackDisplayName(ItemStack stack) {
    if (this.textsId[0] == 4 && stack.getTagCompound() != null) {
      Point p = Point.read(stack.getTagCompound(), "village_pos_");
      if (p != null) {
        Building townHall = Mill.getMillWorld((World)(Minecraft.getMinecraft()).world).getBuilding(p);
        if (townHall != null)
          return super.getItemStackDisplayName(stack) + ": " + townHall.getVillageQualifiedName(); 
      } 
    } 
    return super.getItemStackDisplayName(stack);
  }
  
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityplayer, EnumHand handIn) {
    ItemStack itemstack = entityplayer.getHeldItem(handIn);
    if (this.textsId[0] == 4) {
      if (!world.isRemote && this.textsId[0] == 4) {
        displayVillageBook(entityplayer, itemstack);
        return new ActionResult(EnumActionResult.SUCCESS, entityplayer.getHeldItem(handIn));
      } 
      return new ActionResult(EnumActionResult.SUCCESS, entityplayer.getHeldItem(handIn));
    } 
    if (world.isRemote)
      if (this.textsId.length == 1) {
        List<List<String>> parchment = LanguageUtilities.getParchment(this.textsId[0]);
        if (parchment != null) {
          TextBook book = TextBook.convertStringsToBook(parchment);
          DisplayActions.displayParchmentPanelGUI(entityplayer, book, null, 0, true);
        } else {
          Mill.proxy.localTranslatedSentence(entityplayer, '6', "panels.notextfound", new String[] { "" + this.textsId[0] });
        } 
      } else {
        List<List<String>> combinedText = new ArrayList<>();
        for (int i = 0; i < this.textsId.length; i++) {
          List<List<String>> parchment = LanguageUtilities.getParchment(this.textsId[i]);
          if (parchment != null)
            for (List<String> page : parchment)
              combinedText.add(page);  
        } 
        TextBook book = TextBook.convertStringsToBook(combinedText);
        DisplayActions.displayParchmentPanelGUI(entityplayer, book, null, 0, true);
      }  
    return new ActionResult(EnumActionResult.SUCCESS, entityplayer.getHeldItem(handIn));
  }
}
