package org.millenaire.client.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.VillagerRecord;

public class TravelBookExporter {
  private static class ExportBook {
    public List<TravelBookExporter.ExportPage> pages = new ArrayList<>();
    
    public ExportBook(TextBook book) {
      for (TextPage page : book.getPages())
        this.pages.add(new TravelBookExporter.ExportPage(page)); 
    }
  }
  
  private static class ExportLine {
    public String style = "";
    
    public String text = "";
    
    public String referenceButtonCulture = null;
    
    public String referenceButtonType = null;
    
    public String referenceButtonKey = null;
    
    public String referenceButtonLabel = null;
    
    public String referenceButtonIconKey = null;
    
    public String iconKey = null;
    
    public String iconLabel = null;
    
    public List<ExportLine> columns = null;
    
    public Boolean exportTwoColumns = null;
    
    public String specialTag = null;
    
    public ExportLine(TextLine textLine) {
      this.text = textLine.text;
      this.specialTag = textLine.exportSpecialTag;
      if (textLine.style != null)
        if (textLine.style.equals(TextLine.ITALIC)) {
          this.style = "subheader";
        } else if (textLine.style.equals("§1")) {
          this.style = "header";
        } else {
          this.style = textLine.style;
        }  
      if (textLine.referenceButton != null) {
        this.referenceButtonCulture = textLine.referenceButton.culture.key;
        switch (textLine.referenceButton.type) {
          case BUILDING_DETAIL:
            this.referenceButtonType = "buildings";
            break;
          case CULTURE:
            this.referenceButtonType = "cultures";
            break;
          case TRADE_GOOD_DETAIL:
            this.referenceButtonType = "tradegoods";
            break;
          case VILLAGE_DETAIL:
            this.referenceButtonType = "villages";
            break;
          case VILLAGER_DETAIL:
            this.referenceButtonType = "villagers";
            break;
        } 
        this.referenceButtonKey = textLine.referenceButton.key;
        this.referenceButtonIconKey = TravelBookExporter.getIconKey(textLine.referenceButton.getIcon());
        this.referenceButtonLabel = textLine.referenceButton.getIconFullLegendExport();
      } 
      if (textLine.icons != null && textLine.icons.size() > 0) {
        this.iconKey = TravelBookExporter.getIconKey(textLine.icons.get(0));
        if (textLine.iconExtraLegends != null && textLine.iconExtraLegends.size() > 0 && textLine.iconExtraLegends.get(0) != null) {
          this.iconLabel = textLine.iconExtraLegends.get(0);
        } else if (textLine.displayItemLegend()) {
          this.iconLabel = ((ItemStack)textLine.icons.get(0)).getDisplayName();
        } 
      } 
      if (textLine.columns != null) {
        this.columns = new ArrayList<>();
        for (TextLine col : textLine.columns)
          this.columns.add(new ExportLine(col)); 
        if (textLine.exportTwoColumns)
          this.exportTwoColumns = Boolean.TRUE; 
      } 
    }
  }
  
  private static class ExportPage {
    public List<TravelBookExporter.ExportLine> lines = new ArrayList<>();
    
    public ExportPage(TextPage page) {
      for (TextLine line : page.getLines()) {
        if (line.columns != null && this.lines.size() > 0 && ((TravelBookExporter.ExportLine)this.lines.get(this.lines.size() - 1)).columns != null && !line.exportTwoColumns) {
          TravelBookExporter.ExportLine previousLine = this.lines.get(this.lines.size() - 1);
          TravelBookExporter.ExportLine newLine = new TravelBookExporter.ExportLine(line);
          previousLine.columns.addAll(newLine.columns);
          continue;
        } 
        if (line.columns != null || (line.text != null && line.text.trim().length() > 0))
          this.lines.add(new TravelBookExporter.ExportLine(line)); 
      } 
    }
  }
  
  private static Map<String, ItemStack> itemsToRender = new HashMap<>();
  
  private static final String EOL = "\n";
  
  private static String escapeQuotes(String label) {
    label = label.replaceAll("'", "''");
    label = label.replaceAll("\"", "\\\\\"");
    return label;
  }
  
  private static void exportAllBuildings(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    boolean firstValues = true;
    writer.write("DELETE from encyclopediadata WHERE type='buildings' AND language='" + language + "';" + "\n");
    for (Culture culture : Culture.ListCultures) {
      for (BuildingPlanSet planSet : culture.ListPlanSets) {
        if ((planSet.getFirstStartingPlan()).travelBookDisplay) {
          if (firstValues)
            firstValues = false; 
          TextBook book = travelBookManager.getBookBuildingDetail(culture, planSet.key, null);
          String json = escapeQuotes(gson.toJson(new ExportBook(book)));
          String label = planSet.getNameNativeAndTranslated();
          label = escapeQuotes(label);
          String categoryLabel = escapeQuotes(culture.getCultureString("travelbook_category." + (planSet.getFirstStartingPlan()).travelBookCategory));
          String itemref = culture.key + "-buildings-" + planSet.key + "-" + language;
          writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,categorylabel,itemkey,label,iconkey,data) VALUES \n");
          writer.write("('" + itemref + "','buildings','" + language + "','" + culture.key + "','" + (planSet.getFirstStartingPlan()).travelBookCategory + "','" + categoryLabel + "','" + planSet.key + "','" + label + "','" + 
              getIconKey(planSet.getIcon()) + "','" + json + "');" + "\n");
        } 
      } 
    } 
    writer.write(";\n");
  }
  
  private static void exportAllCultures(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    boolean firstValues = true;
    writer.write("DELETE from encyclopediadata WHERE type='cultures' AND language='" + language + "';" + "\n");
    for (Culture culture : Culture.ListCultures) {
      if (firstValues)
        firstValues = false; 
      TextBook book = travelBookManager.getBookCultureForJSONExport(culture, null);
      String json = escapeQuotes(gson.toJson(new ExportBook(book)));
      String label = culture.getNameTranslated();
      label = escapeQuotes(label);
      String itemref = culture.key + "-cultures-" + culture.key + "-" + language;
      writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,itemkey,label,iconkey,data) VALUES \n");
      writer.write("('" + itemref + "','cultures','" + language + "','" + culture.key + "',NULL,'" + culture.key + "','" + label + "','" + 
          getIconKey(culture.getIcon()) + "','" + json + "');" + "\n");
    } 
    writer.write(";\n");
  }
  
  private static void exportAllTradeGoods(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    boolean firstValues = true;
    writer.write("DELETE from encyclopediadata WHERE type='tradegoods' AND language='" + language + "';" + "\n");
    for (Culture culture : Culture.ListCultures) {
      for (TradeGood tradeGood : culture.goodsList) {
        if (tradeGood.travelBookDisplay && !tradeGood.travelBookCategory.equals("foreigntrade")) {
          if (firstValues)
            firstValues = false; 
          TextBook book = travelBookManager.getBookTradeGoodDetail(culture, tradeGood.key, null);
          String json = escapeQuotes(gson.toJson(new ExportBook(book)));
          String label = tradeGood.getName();
          label = escapeQuotes(label);
          String categoryLabel = escapeQuotes(culture.getCultureString("travelbook_category." + tradeGood.travelBookCategory));
          String itemref = culture.key + "-tradegoods-" + tradeGood.key + "-" + language;
          writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,categorylabel,itemkey,label,iconkey,data) VALUES \n");
          writer.write("('" + itemref + "','tradegoods','" + language + "','" + culture.key + "','" + tradeGood.travelBookCategory + "','" + categoryLabel + "','" + tradeGood.key + "','" + label + "','" + 
              getIconKey(tradeGood.getIcon()) + "','" + json + "');" + "\n");
        } 
      } 
    } 
    writer.write(";\n");
  }
  
  private static void exportAllVillagers(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    writer.write("DELETE from encyclopediadata WHERE type='villagers' AND language='" + language + "';" + "\n");
    boolean firstValues = true;
    for (Culture culture : Culture.ListCultures) {
      for (VillagerType vtype : culture.listVillagerTypes) {
        if (vtype.travelBookDisplay) {
          if (firstValues)
            firstValues = false; 
          TextBook book = travelBookManager.getBookVillagerDetail(culture, vtype.key, null);
          String json = escapeQuotes(gson.toJson(new ExportBook(book)));
          String label = vtype.getNameNativeAndTranslated();
          label = escapeQuotes(label);
          String categoryLabel = escapeQuotes(culture.getCultureString("travelbook_category." + vtype.travelBookCategory));
          String itemref = culture.key + "-villagers-" + vtype.key + "-" + language;
          writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,categorylabel,itemkey,label,iconkey,data) VALUES \n");
          writer.write("('" + itemref + "','villagers','" + language + "','" + culture.key + "','" + vtype.travelBookCategory + "','" + categoryLabel + "','" + vtype.key + "','" + label + "','" + 
              getIconKey(vtype.getIcon()) + "','" + json + "');" + "\n");
        } 
      } 
    } 
    writer.write(";\n");
  }
  
  private static void exportAllVillages(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
    boolean firstValues = true;
    writer.write("DELETE from encyclopediadata WHERE type='villages' AND language='" + language + "';" + "\n");
    for (Culture culture : Culture.ListCultures) {
      for (VillageType vtype : culture.listVillageTypes) {
        if (vtype.travelBookDisplay) {
          if (firstValues)
            firstValues = false; 
          TextBook book = travelBookManager.getBookVillageDetail(culture, vtype.key, null);
          String json = escapeQuotes(gson.toJson(new ExportBook(book)));
          String label = vtype.getNameNativeAndTranslated();
          label = escapeQuotes(label);
          String itemref = culture.key + "-villages-" + vtype.key + "-" + language;
          writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,itemkey,label,iconkey,data) VALUES \n");
          writer.write("('" + itemref + "','villages','" + language + "','" + culture.key + "',NULL,'" + vtype.key + "','" + label + "','" + getIconKey(vtype.getIcon()) + "','" + json + "');" + "\n");
        } 
      } 
    } 
    writer.write(";\n");
  }
  
  private static void exportItemStack(ItemStack stack) throws IOException {
    int width = (Minecraft.getMinecraft().getFramebuffer()).framebufferTextureWidth;
    int height = (Minecraft.getMinecraft().getFramebuffer()).framebufferTextureHeight;
    Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
    Framebuffer framebuffer = new Framebuffer(width, height, true);
    framebuffer.bindFramebuffer(true);
    GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
    GlStateManager.clear(16384);
    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableColorMaterial();
    GlStateManager.enableLighting();
    (Minecraft.getMinecraft().getRenderItem()).zLevel = 100.0F;
    GL11.glEnable(2929);
    Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
    IntBuffer pixels = BufferUtils.createIntBuffer(width * height);
    GlStateManager.bindTexture(framebuffer.framebufferTexture);
    GlStateManager.glGetTexImage(3553, 0, 32993, 33639, pixels);
    int[] vals = new int[width * height];
    pixels.get(vals);
    TextureUtil.processPixelValues(vals, width, height);
    BufferedImage bufferedimage = new BufferedImage(width, height, 2);
    bufferedimage.setRGB(0, 0, width, height, vals, 0, width);
    framebuffer.deleteFramebuffer();
    if (fbo != null) {
      fbo.bindFramebuffer(true);
    } else {
      GL30.glBindFramebuffer(36160, 0);
      GL11.glViewport(0, 0, (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
    } 
    try {
      BufferedImage rightSizeImage = new BufferedImage(32, 32, 6);
      Graphics2D graphics = rightSizeImage.createGraphics();
      graphics.drawImage(bufferedimage, 0, 0, 32, 32, 0, 0, 32, 32, null);
      File f = new File(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "item picts"), getIconKey(stack) + ".png");
      f.mkdirs();
      f.createNewFile();
      ImageIO.write(rightSizeImage, "png", f);
    } catch (IOException e) {
      MillLog.printException(e);
      return;
    } 
  }
  
  public static void exportItemStacks() {
    for (ItemStack stack : itemsToRender.values()) {
      try {
        exportItemStack(stack);
      } catch (IOException e) {
        MillLog.printException(e);
      } 
    } 
    MillLog.major(null, "Exported " + itemsToRender.size() + " icons.");
  }
  
  public static void exportTravelBookData() {
    BookManagerTravelBook travelBookManager = new BookManagerTravelBook(50000, 50000, 50000, 50000, new BookManager.FontRendererMock());
    File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "jsonexports");
    dir.mkdirs();
    Gson gson = (new GsonBuilder()).disableHtmlEscaping().create();
    try {
      String language = MillConfigValues.effective_language;
      if (language.contains("_"))
        language = language.split("_")[0]; 
      File file = new File(dir, "travelbook_" + language + ".sql");
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      exportAllCultures(travelBookManager, writer, gson, language);
      exportAllVillagers(travelBookManager, writer, gson, language);
      exportAllBuildings(travelBookManager, writer, gson, language);
      exportAllVillages(travelBookManager, writer, gson, language);
      exportAllTradeGoods(travelBookManager, writer, gson, language);
      writer.close();
      MillLog.warning(null, "Exported travel book data to SQL for language: " + language);
    } catch (IOException e) {
      MillLog.printException(e);
    } 
  }
  
  public static void exportVillagerPicture(VillagerType villagerType, boolean mainPageExport) throws IOException {
    MillCommonUtilities.initRandom(villagerType.key.hashCode());
    VillagerRecord villagerRecord = VillagerRecord.createVillagerRecord(villagerType.culture, villagerType.key, Mill.getMillWorld((World)(Minecraft.getMinecraft()).world), null, null, null, null, -1L, true);
    MillVillager mockVillager = MillVillager.createMockVillager(villagerRecord, (World)(Minecraft.getMinecraft()).world);
    if (!mainPageExport) {
      mockVillager.heldItem = villagerType.getTravelBookHeldItem();
      mockVillager.heldItemOffHand = villagerType.getTravelBookHeldItemOffHand();
      mockVillager.travelBookMockVillager = true;
    } 
    int width = (Minecraft.getMinecraft().getFramebuffer()).framebufferTextureWidth;
    int height = (Minecraft.getMinecraft().getFramebuffer()).framebufferTextureHeight;
    Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
    Framebuffer framebuffer = new Framebuffer(width, height, true);
    framebuffer.bindFramebuffer(true);
    GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
    GlStateManager.clear(16384);
    GuiTravelBook.drawEntityOnScreen(200, 200, 100, 20.0F, 0.0F, mockVillager);
    IntBuffer pixels = BufferUtils.createIntBuffer(width * height);
    GlStateManager.bindTexture(framebuffer.framebufferTexture);
    GlStateManager.glGetTexImage(3553, 0, 32993, 33639, pixels);
    int[] vals = new int[width * height];
    pixels.get(vals);
    TextureUtil.processPixelValues(vals, width, height);
    BufferedImage bufferedimage = new BufferedImage(width, height, 2);
    bufferedimage.setRGB(0, 0, width, height, vals, 0, width);
    File f = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "villagers");
    f.mkdirs();
    if (mainPageExport) {
      f = new File(f, mockVillager.vtype.culture.key + "_" + mockVillager.vtype.key + "_main.png");
      f.createNewFile();
      BufferedImage finalImage = new BufferedImage(280, 420, 6);
      Graphics2D graphics = finalImage.createGraphics();
      graphics.drawImage(bufferedimage, 0, 0, 280, 420, 256, 0, 536, 420, null);
      ImageIO.write(finalImage, "png", f);
    } else {
      f = new File(f, mockVillager.vtype.culture.key + "_" + mockVillager.vtype.key + ".png");
      f.createNewFile();
      BufferedImage finalImage = new BufferedImage(320, 420, 6);
      Graphics2D graphics = finalImage.createGraphics();
      graphics.drawImage(bufferedimage, 0, 0, 320, 420, 195, 0, 515, 420, null);
      ImageIO.write(finalImage, "png", f);
    } 
    framebuffer.deleteFramebuffer();
    if (fbo != null) {
      fbo.bindFramebuffer(true);
    } else {
      GL30.glBindFramebuffer(36160, 0);
      GL11.glViewport(0, 0, (Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight);
    } 
  }
  
  public static void exportVillagerPictures(World world) {
    int nb = 0;
    for (Culture culture : Culture.ListCultures) {
      for (VillagerType villagerType : culture.listVillagerTypes) {
        if (villagerType.travelBookDisplay)
          try {
            exportVillagerPicture(villagerType, false);
            nb++;
          } catch (Exception e) {
            MillLog.printException(e);
          }  
        if (villagerType.travelBookMainCultureVillager)
          try {
            exportVillagerPicture(villagerType, true);
            nb++;
          } catch (Exception e) {
            MillLog.printException(e);
          }  
      } 
    } 
    MillLog.major(null, "Exported " + nb + " villager pictures.");
  }
  
  private static String getIconKey(ItemStack stack) {
    String key;
    if (stack == null)
      return ""; 
    if (stack.getItemDamage() > 0) {
      key = stack.getItem().getRegistryName().toString().replaceAll(":", "_") + "_" + stack.getItemDamage();
    } else {
      key = stack.getItem().getRegistryName().toString().replaceAll(":", "_");
    } 
    if (!itemsToRender.containsKey(key))
      itemsToRender.put(key, stack); 
    return key;
  }
}
