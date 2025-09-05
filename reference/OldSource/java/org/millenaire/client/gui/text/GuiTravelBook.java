package org.millenaire.client.gui.text;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.BookManagerTravelBook;
import org.millenaire.client.book.TextBook;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.UserProfile;

public class GuiTravelBook extends GuiText {
  public static final int BUTTON_CLOSE = 0;
  
  public static final int BUTTON_BACK = 1;
  
  public enum ButtonTypes {
    CHOOSE_CULTURE, VIEW_BUILDINGS, VIEW_VILLAGERS, VIEW_VILLAGES, VIEW_TRADE_GOODS, BUILDING_DETAIL, VILLAGER_DETAIL, VILLAGE_DETAIL, TRADE_GOODS_DETAILS, BACK, NEXT;
  }
  
  public static class GuiButtonTravelBook extends GuiText.MillGuiButton {
    private String value;
    
    private final GuiTravelBook.ButtonTypes key;
    
    public GuiButtonTravelBook(GuiTravelBook.ButtonTypes key, String label) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
    }
    
    public GuiButtonTravelBook(GuiTravelBook.ButtonTypes key, String label, int x, int y, int width, int height) {
      super(0, x, y, width, height, label);
      this.key = key;
    }
    
    public GuiButtonTravelBook(GuiTravelBook.ButtonTypes key, String label, ItemStack icon) {
      super(label, 0, icon);
      this.key = key;
    }
    
    public GuiButtonTravelBook(GuiTravelBook.ButtonTypes key, String label, String value) {
      super(0, 0, 0, 0, 0, label);
      this.key = key;
      this.value = value;
    }
    
    public GuiButtonTravelBook(GuiTravelBook.ButtonTypes key, String label, String value, ItemStack icon) {
      super(label, 0, icon);
      this.key = key;
      this.value = value;
    }
    
    public GuiButtonTravelBook(GuiTravelBook.ButtonTypes key, String label, String value, GuiText.SpecialIcon icon) {
      super(label, 0, icon);
      this.key = key;
      this.value = value;
    }
  }
  
  enum GUIScreen {
    HOME, CULTURE, BUILDINGS_LIST, BUILDING_DETAIL, VILLAGERS_LIST, VILLAGER_DETAIL, VILLAGES_LIST, VILLAGE_DETAIL, TRADE_GOODS_LIST, TRADE_GOOD_DETAIL;
  }
  
  private static class ScreenState {
    GuiTravelBook.GUIScreen screen;
    
    String currentItemKey;
    
    String categoryKey;
    
    int pageNum;
    
    public ScreenState(GuiTravelBook.GUIScreen screen, String objectKey, String categoryKey, int pageNum) {
      this.screen = screen;
      this.currentItemKey = objectKey;
      this.categoryKey = categoryKey;
      this.pageNum = pageNum;
    }
  }
  
  public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, MillVillager villager) {
    GlStateManager.enableColorMaterial();
    GlStateManager.pushMatrix();
    GlStateManager.translate(posX, posY, 50.0F);
    GlStateManager.scale(-scale, scale, scale);
    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
    float renderYawOffset = villager.renderYawOffset;
    float rotationYaw = villager.rotationYaw;
    float rotationPitch = villager.rotationPitch;
    float prevRotationYawHead = villager.prevRotationYawHead;
    float rotationYawHead = villager.rotationYawHead;
    GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
    RenderHelper.enableStandardItemLighting();
    GlStateManager.rotate(-((float)Math.atan((mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
    villager.renderYawOffset = (float)Math.atan((mouseX / 40.0F)) * 20.0F;
    villager.rotationYaw = (float)Math.atan((mouseX / 40.0F)) * -30.0F;
    villager.rotationPitch = -((float)Math.atan((mouseY / 40.0F))) * 20.0F;
    villager.rotationYawHead = villager.rotationYaw;
    villager.prevRotationYawHead = villager.rotationYaw;
    GlStateManager.translate(0.0F, 0.0F, 0.0F);
    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
    rendermanager.setPlayerViewY(180.0F);
    rendermanager.setRenderShadow(false);
    rendermanager.renderEntity((Entity)villager, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
    rendermanager.setRenderShadow(true);
    villager.renderYawOffset = renderYawOffset;
    villager.rotationYaw = rotationYaw;
    villager.rotationPitch = rotationPitch;
    villager.prevRotationYawHead = prevRotationYawHead;
    villager.rotationYawHead = rotationYawHead;
    GlStateManager.popMatrix();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
    GlStateManager.disableTexture2D();
    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
  }
  
  private final List<ScreenState> previousScreenStates = new ArrayList<>();
  
  private GUIScreen currentScreen = GUIScreen.HOME;
  
  private Culture currentCulture = null;
  
  private String currentItemKey = null;
  
  private String currentCategory = null;
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");
  
  private final UserProfile profile;
  
  private MillVillager mockVillager = null;
  
  long timeElapsed = 0L;
  
  private final BookManagerTravelBook travelBookManager;
  
  public GuiTravelBook(EntityPlayer player) {
    this.profile = Mill.getMillWorld(player.world).getProfile(player);
    this.travelBookManager = new BookManagerTravelBook(256, 220, 175, 240, new GuiText.FontRendererGUIWrapper(this));
    this.bookManager = (BookManager)this.travelBookManager;
  }
  
  protected void actionPerformed(GuiButton guibutton) {
    if (!guibutton.enabled)
      return; 
    try {
      if (guibutton instanceof GuiText.GuiButtonReference) {
        GuiText.GuiButtonReference refButton = (GuiText.GuiButtonReference)guibutton;
        jumpToDetails(refButton.culture, refButton.type, refButton.key, true);
      } else if (guibutton instanceof GuiButtonTravelBook) {
        GuiButtonTravelBook gb = (GuiButtonTravelBook)guibutton;
        boolean close = false;
        if (gb.key == ButtonTypes.CHOOSE_CULTURE) {
          this.currentCulture = Culture.getCultureByName(gb.value);
          storePreviousState();
          this.currentScreen = GUIScreen.CULTURE;
        } else if (gb.key == ButtonTypes.VIEW_BUILDINGS) {
          storePreviousState();
          this.currentCategory = gb.value;
          this.currentScreen = GUIScreen.BUILDINGS_LIST;
        } else if (gb.key == ButtonTypes.VIEW_VILLAGERS) {
          storePreviousState();
          this.currentCategory = gb.value;
          this.currentScreen = GUIScreen.VILLAGERS_LIST;
        } else if (gb.key == ButtonTypes.VIEW_VILLAGES) {
          storePreviousState();
          this.currentScreen = GUIScreen.VILLAGES_LIST;
        } else if (gb.key == ButtonTypes.VIEW_TRADE_GOODS) {
          storePreviousState();
          this.currentCategory = gb.value;
          this.currentScreen = GUIScreen.TRADE_GOODS_LIST;
        } else if (gb.key == ButtonTypes.BUILDING_DETAIL) {
          storePreviousState();
          this.currentScreen = GUIScreen.BUILDING_DETAIL;
          this.currentItemKey = gb.value;
          this.currentCategory = (this.currentCulture.getBuildingPlanSet(this.currentItemKey).getFirstStartingPlan()).travelBookCategory;
        } else if (gb.key == ButtonTypes.VILLAGER_DETAIL) {
          storePreviousState();
          this.currentScreen = GUIScreen.VILLAGER_DETAIL;
          this.currentItemKey = gb.value;
          this.currentCategory = (this.currentCulture.getVillagerType(this.currentItemKey)).travelBookCategory;
        } else if (gb.key == ButtonTypes.VILLAGE_DETAIL) {
          storePreviousState();
          this.currentScreen = GUIScreen.VILLAGE_DETAIL;
          this.currentItemKey = gb.value;
        } else if (gb.key == ButtonTypes.TRADE_GOODS_DETAILS) {
          storePreviousState();
          this.currentScreen = GUIScreen.TRADE_GOOD_DETAIL;
          this.currentItemKey = gb.value;
        } else if (gb.key == ButtonTypes.BACK) {
          if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
            List<BuildingPlanSet> planSets = this.travelBookManager.getCurrentBuildingList(this.currentCulture, this.currentCategory);
            String prevKey = null;
            for (BuildingPlanSet planSet : planSets) {
              if (planSet.key.equals(this.currentItemKey))
                break; 
              prevKey = planSet.key;
            } 
            this.currentItemKey = prevKey;
          } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
            List<VillagerType> villagerTypes = this.travelBookManager.getCurrentVillagerList(this.currentCulture, this.currentCategory);
            String prevKey = null;
            for (VillagerType villagerType : villagerTypes) {
              if (villagerType.key.equals(this.currentItemKey))
                break; 
              prevKey = villagerType.key;
            } 
            this.currentItemKey = prevKey;
          } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
            List<TradeGood> tradeGood = this.travelBookManager.getCurrentTradeGoodList(this.currentCulture, this.currentCategory);
            String prevKey = null;
            for (TradeGood villagerType : tradeGood) {
              if (villagerType.key.equals(this.currentItemKey))
                break; 
              prevKey = villagerType.key;
            } 
            this.currentItemKey = prevKey;
          } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
            List<VillageType> villageTypes = this.travelBookManager.getCurrentVillageList(this.currentCulture);
            String prevKey = null;
            for (VillageType villageType : villageTypes) {
              if (villageType.key.equals(this.currentItemKey))
                break; 
              prevKey = villageType.key;
            } 
            this.currentItemKey = prevKey;
          } 
        } else if (gb.key == ButtonTypes.NEXT) {
          if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
            List<BuildingPlanSet> planSets = this.travelBookManager.getCurrentBuildingList(this.currentCulture, this.currentCategory);
            String nextKey = null;
            for (int i = 0; i + 1 < planSets.size(); i++) {
              if (((BuildingPlanSet)planSets.get(i)).key.equals(this.currentItemKey))
                nextKey = ((BuildingPlanSet)planSets.get(i + 1)).key; 
            } 
            this.currentItemKey = nextKey;
          } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
            List<VillagerType> villagerTypes = this.travelBookManager.getCurrentVillagerList(this.currentCulture, this.currentCategory);
            String nextKey = null;
            for (int i = 0; i + 1 < villagerTypes.size(); i++) {
              if (((VillagerType)villagerTypes.get(i)).key.equals(this.currentItemKey))
                nextKey = ((VillagerType)villagerTypes.get(i + 1)).key; 
            } 
            this.currentItemKey = nextKey;
          } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
            List<TradeGood> tradeGood = this.travelBookManager.getCurrentTradeGoodList(this.currentCulture, this.currentCategory);
            String nextKey = null;
            for (int i = 0; i + 1 < tradeGood.size(); i++) {
              if (((TradeGood)tradeGood.get(i)).key.equals(this.currentItemKey))
                nextKey = ((TradeGood)tradeGood.get(i + 1)).key; 
            } 
            this.currentItemKey = nextKey;
          } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
            List<VillageType> villageTypes = this.travelBookManager.getCurrentVillageList(this.currentCulture);
            String nextKey = null;
            for (int i = 0; i + 1 < villageTypes.size(); i++) {
              if (((VillageType)villageTypes.get(i)).key.equals(this.currentItemKey))
                nextKey = ((VillageType)villageTypes.get(i + 1)).key; 
            } 
            this.currentItemKey = nextKey;
          } 
        } 
        this.pageNum = 0;
        this.textBook = getBook();
        buttonPagination();
      } else if (guibutton.id == 0) {
        closeGui();
      } else if (guibutton.id == 1) {
        ScreenState previousState = this.previousScreenStates.get(this.previousScreenStates.size() - 1);
        this.currentScreen = previousState.screen;
        this.currentItemKey = previousState.currentItemKey;
        this.currentCategory = previousState.categoryKey;
        this.pageNum = previousState.pageNum;
        this.previousScreenStates.remove(this.previousScreenStates.size() - 1);
        this.textBook = null;
      } 
    } catch (Exception e) {
      MillLog.printException("Exception while handling button pressed:", e);
    } 
  }
  
  public void buttonPagination() {
    try {
      super.buttonPagination();
      int xStart = (this.width - getXSize()) / 2;
      int yStart = (this.height - getYSize()) / 2;
      this.buttonList.add(new GuiButton(0, xStart + getXSize() / 2 + 5, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("hire.close")));
      if (this.currentScreen != GUIScreen.HOME && !this.previousScreenStates.isEmpty())
        this.buttonList.add(new GuiButton(1, xStart + getXSize() / 2 - 100, yStart + getYSize() - 40, 95, 20, LanguageUtilities.string("importtable.back"))); 
      if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
        List<BuildingPlanSet> buildings = this.travelBookManager.getCurrentBuildingList(this.currentCulture, this.currentCategory);
        if (buildings.size() == 0) {
          MillLog.warning(this, "Empty buildings list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
        } else {
          boolean isFirstItem = ((BuildingPlanSet)buildings.get(0)).key.equals(this.currentItemKey);
          boolean isLastItem = ((BuildingPlanSet)buildings.get(buildings.size() - 1)).key.equals(this.currentItemKey);
          GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
          backButton.enabled = !isFirstItem;
          GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + getXSize() - 15, yStart + 1, 15, 20);
          nextButton.enabled = !isLastItem;
          this.buttonList.add(backButton);
          this.buttonList.add(nextButton);
        } 
      } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
        List<VillagerType> villagerTypes = this.travelBookManager.getCurrentVillagerList(this.currentCulture, this.currentCategory);
        if (villagerTypes.size() == 0) {
          MillLog.warning(this, "Empty villagerTypes list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
        } else {
          boolean isFirstItem = ((VillagerType)villagerTypes.get(0)).key.equals(this.currentItemKey);
          boolean isLastItem = ((VillagerType)villagerTypes.get(villagerTypes.size() - 1)).key.equals(this.currentItemKey);
          GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
          backButton.enabled = !isFirstItem;
          GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + getXSize() - 15, yStart + 1, 15, 20);
          nextButton.enabled = !isLastItem;
          this.buttonList.add(backButton);
          this.buttonList.add(nextButton);
        } 
      } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
        List<TradeGood> tradeGoods = this.travelBookManager.getCurrentTradeGoodList(this.currentCulture, this.currentCategory);
        if (tradeGoods.size() == 0) {
          MillLog.warning(this, "Empty tradeGoods list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
        } else {
          boolean isFirstItem = ((TradeGood)tradeGoods.get(0)).key.equals(this.currentItemKey);
          boolean isLastItem = ((TradeGood)tradeGoods.get(tradeGoods.size() - 1)).key.equals(this.currentItemKey);
          GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
          backButton.enabled = !isFirstItem;
          GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + getXSize() - 15, yStart + 1, 15, 20);
          nextButton.enabled = !isLastItem;
          this.buttonList.add(backButton);
          this.buttonList.add(nextButton);
        } 
      } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
        List<VillageType> villageTypes = this.travelBookManager.getCurrentVillageList(this.currentCulture);
        if (villageTypes.size() == 0) {
          MillLog.warning(this, "Empty villageTypes list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
        } else {
          boolean isFirstItem = ((VillageType)villageTypes.get(0)).key.equals(this.currentItemKey);
          boolean isLastItem = ((VillageType)villageTypes.get(villageTypes.size() - 1)).key.equals(this.currentItemKey);
          GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
          backButton.enabled = !isFirstItem;
          GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + getXSize() - 15, yStart + 1, 15, 20);
          nextButton.enabled = !isLastItem;
          this.buttonList.add(backButton);
          this.buttonList.add(nextButton);
        } 
      } 
    } catch (Exception e) {
      MillLog.printException("Exception in buttonPagination:", e);
    } 
  }
  
  protected void customDrawBackground(int i, int j, float f) {
    if (this.currentScreen == GUIScreen.VILLAGER_DETAIL && this.mockVillager != null) {
      int xStart = (this.width - getXSize()) / 2;
      int yStart = (this.height - getYSize()) / 2;
      drawEntityOnScreen(xStart + getXSize() - 40, yStart + 150, 50, 20.0F, 0.0F, this.mockVillager);
    } 
  }
  
  protected void customDrawScreen(int i, int j, float f) {
    this.timeElapsed++;
    if (this.timeElapsed % 100L == 0L)
      refreshContent(); 
  }
  
  public boolean doesGuiPauseGame() {
    return true;
  }
  
  private TextBook getBook() {
    TextBook book = null;
    try {
      if (this.currentScreen == GUIScreen.HOME) {
        book = this.travelBookManager.getBookHome(this.profile);
      } else if (this.currentScreen == GUIScreen.CULTURE) {
        book = this.travelBookManager.getBookCulture(this.currentCulture, this.profile);
      } else if (this.currentScreen == GUIScreen.BUILDINGS_LIST) {
        book = this.travelBookManager.getBookBuildingsList(this.currentCulture, this.currentCategory, this.profile);
      } else if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
        book = this.travelBookManager.getBookBuildingDetail(this.currentCulture, this.currentItemKey, this.profile);
      } else if (this.currentScreen == GUIScreen.VILLAGERS_LIST) {
        book = this.travelBookManager.getBookVillagersList(this.currentCulture, this.currentCategory, this.profile);
      } else if (this.currentScreen == GUIScreen.TRADE_GOODS_LIST) {
        book = this.travelBookManager.getBookTradeGoodsList(this.currentCulture, this.currentCategory, this.profile);
      } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
        book = this.travelBookManager.getBookVillagerDetail(this.currentCulture, this.currentItemKey, this.profile);
        updateMockVillager();
      } else if (this.currentScreen == GUIScreen.VILLAGES_LIST) {
        book = this.travelBookManager.getBookVillagesList(this.currentCulture, this.profile);
      } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
        book = this.travelBookManager.getBookVillageDetail(this.currentCulture, this.currentItemKey, this.profile);
      } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
        book = this.travelBookManager.getBookTradeGoodDetail(this.currentCulture, this.currentItemKey, this.profile);
      } 
      book = this.bookManager.adjustTextBookLineLength(book);
    } catch (Exception e) {
      MillLog.printException("Error when computing Travel Book", e);
      book = new TextBook();
    } 
    return book;
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    refreshContent();
  }
  
  public void jumpToDetails(Culture culture, GuiText.GuiButtonReference.RefType type, String key, boolean withinTravelBook) {
    if (withinTravelBook) {
      storePreviousState();
      this.currentCulture = culture;
    } else {
      this.currentCulture = culture;
      this.previousScreenStates.add(new ScreenState(GUIScreen.HOME, null, null, 0));
      if (type != GuiText.GuiButtonReference.RefType.CULTURE)
        this.previousScreenStates.add(new ScreenState(GUIScreen.CULTURE, null, null, 0)); 
    } 
    this.pageNum = 0;
    this.currentItemKey = key;
    this.currentCategory = null;
    if (type == GuiText.GuiButtonReference.RefType.BUILDING_DETAIL) {
      this.currentCategory = (this.currentCulture.getBuildingPlanSet(this.currentItemKey).getFirstStartingPlan()).travelBookCategory;
      this.currentScreen = GUIScreen.BUILDING_DETAIL;
    } else if (type == GuiText.GuiButtonReference.RefType.VILLAGER_DETAIL) {
      this.currentCategory = (this.currentCulture.getVillagerType(this.currentItemKey)).travelBookCategory;
      this.currentScreen = GUIScreen.VILLAGER_DETAIL;
    } else if (type == GuiText.GuiButtonReference.RefType.VILLAGE_DETAIL) {
      this.currentScreen = GUIScreen.VILLAGE_DETAIL;
    } else if (type == GuiText.GuiButtonReference.RefType.TRADE_GOOD_DETAIL) {
      this.currentCategory = (this.currentCulture.getTradeGood(this.currentItemKey)).travelBookCategory;
      this.currentScreen = GUIScreen.TRADE_GOOD_DETAIL;
    } else if (type == GuiText.GuiButtonReference.RefType.CULTURE) {
      this.currentScreen = GUIScreen.CULTURE;
    } 
    this.textBook = getBook();
    buttonPagination();
  }
  
  private void refreshContent() {
    this.textBook = getBook();
    buttonPagination();
  }
  
  private void storePreviousState() {
    this.previousScreenStates.add(new ScreenState(this.currentScreen, this.currentItemKey, this.currentCategory, this.pageNum));
  }
  
  private void updateMockVillager() {
    VillagerType villagerType = this.currentCulture.getVillagerType(this.currentItemKey);
    boolean knownVillager = this.profile.isVillagerUnlocked(this.currentCulture, villagerType);
    if (knownVillager || !MillConfigValues.TRAVEL_BOOK_LEARNING) {
      VillagerRecord villagerRecord = VillagerRecord.createVillagerRecord(this.currentCulture, villagerType.key, Mill.getMillWorld((World)(Minecraft.getMinecraft()).world), null, null, null, null, -1L, true);
      this.mockVillager = MillVillager.createMockVillager(villagerRecord, (World)(Minecraft.getMinecraft()).world);
      this.mockVillager.heldItem = villagerType.getTravelBookHeldItem();
      this.mockVillager.heldItemOffHand = villagerType.getTravelBookHeldItemOffHand();
      this.mockVillager.travelBookMockVillager = true;
    } else {
      this.mockVillager = null;
    } 
  }
}
