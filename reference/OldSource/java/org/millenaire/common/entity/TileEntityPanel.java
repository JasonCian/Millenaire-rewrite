package org.millenaire.common.entity;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.buildingmanagers.PanelContentGenerator;

public class TileEntityPanel extends TileEntity {
  public static final int MAP_VILLAGE_MAP = 1;
  
  public static final int ETAT_CIVIL = 1;
  
  public static final int CONSTRUCTIONS = 2;
  
  public static final int PROJECTS = 3;
  
  public static final int CONTROLLED_PROJECTS = 4;
  
  public static final int HOUSE = 5;
  
  public static final int RESOURCES = 6;
  
  public static final int ARCHIVES = 7;
  
  public static final int VILLAGE_MAP = 8;
  
  public static final int MILITARY = 9;
  
  public static final int INN_TRADE_GOODS = 10;
  
  public static final int INN_VISITORS = 11;
  
  public static final int MARKET_MERCHANTS = 12;
  
  public static final int CONTROLLED_MILITARY = 13;
  
  public static final int VISITORS = 14;
  
  public static final int WALLS = 15;
  
  public static final int MARVEL_DONATIONS = 20;
  
  public static final int MARVEL_RESOURCES = 21;
  
  public static class PanelDisplayLine {
    public String fullLine = "";
    
    public String leftColumn = "";
    
    public String rightColumn = "";
    
    public ItemStack leftIcon = ItemStack.EMPTY;
    
    public ItemStack middleIcon = ItemStack.EMPTY;
    
    public ItemStack rightIcon = ItemStack.EMPTY;
    
    public boolean centerLine = true;
  }
  
  public static class PanelPacketInfo {
    public Point pos;
    
    public Point buildingPos;
    
    public String[][] lines;
    
    public long villager_id;
    
    public int panelType;
    
    public PanelPacketInfo(Point pos, String[][] lines, Point buildingPos, int panelType, long village_id) {
      this.pos = pos;
      this.lines = lines;
      this.buildingPos = buildingPos;
      this.panelType = panelType;
      this.villager_id = village_id;
    }
  }
  
  public static class PanelUntranslatedLine {
    public static PanelUntranslatedLine readFromNBT(NBTTagCompound compound) {
      PanelUntranslatedLine line = new PanelUntranslatedLine();
      line.fullLine = readText(compound, "fullLine");
      line.leftColumn = readText(compound, "leftColumn");
      line.rightColumn = readText(compound, "rightColumn");
      line.leftIcon = new ItemStack(compound.getCompoundTag("leftIcon"));
      line.middleIcon = new ItemStack(compound.getCompoundTag("middleIcon"));
      line.rightIcon = new ItemStack(compound.getCompoundTag("rightIcon"));
      line.centerLine = compound.getBoolean("centerLine");
      return line;
    }
    
    private static String[] readText(NBTTagCompound compound, String key) {
      List<String> lineFragment = new ArrayList<>();
      int i = 0;
      while (compound.hasKey(key + "_" + i)) {
        lineFragment.add(compound.getString(key + "_" + i));
        i++;
      } 
      return lineFragment.<String>toArray(new String[0]);
    }
    
    private static void writeText(NBTTagCompound compound, String[] text, String key) {
      for (int i = 0; i < text.length; i++)
        compound.setString(key + "_" + i, text[i]); 
    }
    
    private String[] fullLine = new String[] { "" };
    
    private String[] leftColumn = new String[] { "" };
    
    public String[] rightColumn = new String[] { "" };
    
    public ItemStack leftIcon = ItemStack.EMPTY;
    
    public ItemStack middleIcon = ItemStack.EMPTY;
    
    public ItemStack rightIcon = ItemStack.EMPTY;
    
    public boolean centerLine = true;
    
    public void setFullLine(String[] fullLine) {
      this.fullLine = fullLine;
      for (int i = 0; i < this.fullLine.length; i++) {
        if (this.fullLine[i] == null)
          this.fullLine[i] = ""; 
      } 
    }
    
    public void setLeftColumn(String[] leftColumn) {
      this.leftColumn = leftColumn;
      for (int i = 0; i < this.leftColumn.length; i++) {
        if (this.leftColumn[i] == null)
          this.leftColumn[i] = ""; 
      } 
    }
    
    public void setRightColumn(String[] rightColumn) {
      this.rightColumn = rightColumn;
      for (int i = 0; i < this.rightColumn.length; i++) {
        if (this.rightColumn[i] == null)
          this.rightColumn[i] = ""; 
      } 
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      writeText(compound, this.fullLine, "fullLine");
      writeText(compound, this.leftColumn, "leftColumn");
      writeText(compound, this.rightColumn, "rightColumn");
      compound.setTag("leftIcon", (NBTBase)this.leftIcon.writeToNBT(new NBTTagCompound()));
      compound.setTag("middleIcon", (NBTBase)this.middleIcon.writeToNBT(new NBTTagCompound()));
      compound.setTag("rightIcon", (NBTBase)this.rightIcon.writeToNBT(new NBTTagCompound()));
      compound.setBoolean("centerLine", this.centerLine);
      return compound;
    }
  }
  
  public List<PanelUntranslatedLine> untranslatedLines = new ArrayList<>();
  
  public List<PanelDisplayLine> displayLines = new ArrayList<>();
  
  public Point buildingPos = null;
  
  public long villager_id = 0L;
  
  public int panelType = 0;
  
  public ResourceLocation texture = null;
  
  public TextBook getFullText(EntityPlayer player) {
    if (this.panelType == 0 || this.buildingPos == null)
      return null; 
    Building building = Mill.clientWorld.getBuilding(this.buildingPos);
    if (this.panelType == 1)
      return PanelContentGenerator.generateEtatCivil(building); 
    if (this.panelType == 2)
      return PanelContentGenerator.generateConstructions(building); 
    if (this.panelType == 3)
      return PanelContentGenerator.generateProjects(player, building); 
    if (this.panelType == 5)
      return PanelContentGenerator.generateHouse(building); 
    if (this.panelType == 7)
      return PanelContentGenerator.generateArchives(building, this.villager_id); 
    if (this.panelType == 6)
      return PanelContentGenerator.generateResources(building); 
    if (this.panelType == 8)
      return PanelContentGenerator.generateVillageMap(building); 
    if (this.panelType == 9)
      return PanelContentGenerator.generateMilitary(building); 
    if (this.panelType == 10)
      return PanelContentGenerator.generateInnGoods(building); 
    if (this.panelType == 11)
      return PanelContentGenerator.generateInnVisitors(building); 
    if (this.panelType == 12)
      return PanelContentGenerator.generateVisitors(building, true); 
    if (this.panelType == 14)
      return PanelContentGenerator.generateVisitors(building, false); 
    if (this.panelType == 15)
      return PanelContentGenerator.generateWalls(player, building); 
    if (this.panelType == 20)
      return building.getMarvelManager().generateDonationPanelText(); 
    if (this.panelType == 21)
      return building.getMarvelManager().generateResourcesPanelText(); 
    return null;
  }
  
  public int getMapType() {
    if (this.panelType == 8)
      return 1; 
    return 0;
  }
  
  private IBlockState getState() {
    return this.world.getBlockState(this.pos);
  }
  
  @Nullable
  public SPacketUpdateTileEntity getUpdatePacket() {
    return new SPacketUpdateTileEntity(this.pos, -1, getUpdateTag());
  }
  
  public NBTTagCompound getUpdateTag() {
    return writeToNBT(new NBTTagCompound());
  }
  
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    handleUpdateTag(pkt.getNbtCompound());
  }
  
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.buildingPos = Point.read(compound, "buildingPos");
    this.panelType = compound.getInteger("panelType");
    this.villager_id = compound.getLong("villager_id");
    if (compound.hasKey("texture")) {
      String textureString = compound.getString("texture");
      this.texture = new ResourceLocation(textureString);
    } 
    int pos = 0;
    this.untranslatedLines.clear();
    while (compound.hasKey("Lines_" + pos)) {
      this.untranslatedLines.add(PanelUntranslatedLine.readFromNBT(compound.getCompoundTag("Lines_" + pos)));
      pos++;
    } 
  }
  
  protected void setWorldCreate(World worldIn) {
    setWorld(worldIn);
  }
  
  private String translatedLines_cutLines(FontRenderer fontrenderer, String text, int maxLength) {
    if (fontrenderer.getStringWidth(text) > maxLength) {
      while (fontrenderer.getStringWidth(text + "...") > maxLength)
        text = text.substring(0, text.length() - 1); 
      text = text + "...";
    } 
    return text;
  }
  
  @SideOnly(Side.CLIENT)
  public void translateLines(FontRenderer fontrenderer) {
    this.displayLines.clear();
    int nbExtraLines = 0;
    for (PanelUntranslatedLine line : this.untranslatedLines) {
      PanelDisplayLine displayLine = new PanelDisplayLine();
      displayLine.leftIcon = line.leftIcon;
      displayLine.middleIcon = line.middleIcon;
      displayLine.rightIcon = line.rightIcon;
      displayLine.fullLine = LanguageUtilities.string(line.fullLine);
      displayLine.leftColumn = LanguageUtilities.string(line.leftColumn);
      displayLine.rightColumn = LanguageUtilities.string(line.rightColumn);
      displayLine.centerLine = line.centerLine;
      int maxLength = 80;
      if (displayLine.leftIcon.getItem() != Items.AIR)
        maxLength = 62; 
      displayLine.leftColumn = translatedLines_cutLines(fontrenderer, displayLine.leftColumn, 32);
      displayLine.rightColumn = translatedLines_cutLines(fontrenderer, displayLine.rightColumn, 32);
      List<String> splitStrings = BookManager.splitStringByLength((BookManager.IFontRendererWrapper)new GuiText.FontRendererWrapped(fontrenderer), displayLine.fullLine, maxLength);
      displayLine.fullLine = splitStrings.get(0);
      this.displayLines.add(displayLine);
      if (splitStrings.size() > 1 && this.untranslatedLines.size() + nbExtraLines + 1 < 8) {
        PanelDisplayLine extraDisplayLine = new PanelDisplayLine();
        extraDisplayLine.fullLine = splitStrings.get(1);
        this.displayLines.add(extraDisplayLine);
        nbExtraLines++;
      } 
    } 
  }
  
  public void triggerUpdate() {
    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    this.world.notifyBlockUpdate(this.pos, getState(), getState(), 3);
    this.world.scheduleBlockUpdate(this.pos, getBlockType(), 0, 0);
    markDirty();
  }
  
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    try {
      if (this.buildingPos != null)
        this.buildingPos.write(compound, "buildingPos"); 
      compound.setInteger("panelType", this.panelType);
      compound.setLong("villager_id", this.villager_id);
      if (this.texture != null)
        compound.setString("texture", this.texture.toString()); 
      for (int i = 0; i < this.untranslatedLines.size(); i++)
        compound.setTag("Lines_" + i, (NBTBase)((PanelUntranslatedLine)this.untranslatedLines.get(i)).writeToNBT(new NBTTagCompound())); 
    } catch (Exception e) {
      MillLog.printException("Error writing panel", e);
    } 
    return compound;
  }
}
