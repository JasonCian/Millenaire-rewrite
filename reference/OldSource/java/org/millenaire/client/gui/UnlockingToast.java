package org.millenaire.client.gui;

import java.util.List;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;

public class UnlockingToast implements IToast {
  private static final long SINGLE_ITEM_DURATION = 2000L;
  
  private static final long MULTI_ITEMS_DURATION = 1000L;
  
  private final BuildingPlanSet planSet;
  
  private final VillageType villageType;
  
  private final VillagerType villagerType;
  
  private final TradeGood tradeGood;
  
  private final List<TradeGood> tradeGoods;
  
  private final int nbUnlocked;
  
  private final int nbTotal;
  
  private long firstDrawTime;
  
  private boolean firstPassDone = false;
  
  public UnlockingToast(BuildingPlanSet planSet, int nbUnlocked, int nbTotal) {
    this.planSet = planSet;
    this.villageType = null;
    this.villagerType = null;
    this.tradeGood = null;
    this.tradeGoods = null;
    this.nbUnlocked = nbUnlocked;
    this.nbTotal = nbTotal;
  }
  
  public UnlockingToast(List<TradeGood> tradeGoods, int nbUnlocked, int nbTotal) {
    this.tradeGoods = tradeGoods;
    this.villageType = null;
    this.villagerType = null;
    this.tradeGood = null;
    this.planSet = null;
    this.nbUnlocked = nbUnlocked;
    this.nbTotal = nbTotal;
  }
  
  public UnlockingToast(TradeGood tradeGood, int nbUnlocked, int nbTotal) {
    this.tradeGood = tradeGood;
    this.villageType = null;
    this.villagerType = null;
    this.planSet = null;
    this.tradeGoods = null;
    this.nbUnlocked = nbUnlocked;
    this.nbTotal = nbTotal;
  }
  
  public UnlockingToast(VillagerType villagerType, int nbUnlocked, int nbTotal) {
    this.villagerType = villagerType;
    this.villageType = null;
    this.planSet = null;
    this.tradeGood = null;
    this.tradeGoods = null;
    this.nbUnlocked = nbUnlocked;
    this.nbTotal = nbTotal;
  }
  
  public UnlockingToast(VillageType villageType, int nbUnlocked, int nbTotal) {
    this.villageType = villageType;
    this.planSet = null;
    this.villagerType = null;
    this.tradeGood = null;
    this.tradeGoods = null;
    this.nbUnlocked = nbUnlocked;
    this.nbTotal = nbTotal;
  }
  
  public IToast.Visibility draw(GuiToast toastGui, long delta) {
    if (!this.firstPassDone) {
      this.firstDrawTime = delta;
      this.firstPassDone = true;
    } 
    String title = null;
    String text = null;
    ItemStack icon = null;
    try {
      if (this.planSet != null) {
        title = this.planSet.getNameNative();
        text = LanguageUtilities.string("travelbook.unlockedbuilding", new String[] { this.planSet.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal });
        icon = this.planSet.getIcon();
      } else if (this.villageType != null) {
        title = this.villageType.name;
        text = LanguageUtilities.string("travelbook.unlockedvillage", new String[] { this.villageType.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal });
        icon = this.villageType.getIcon();
      } else if (this.villagerType != null) {
        title = this.villagerType.name;
        text = LanguageUtilities.string("travelbook.unlockedvillager", new String[] { this.villagerType.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal });
        icon = this.villagerType.getIcon();
      } else if (this.tradeGood != null) {
        title = this.tradeGood.getName();
        text = LanguageUtilities.string("travelbook.unlockedtradegood", new String[] { this.tradeGood.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal });
        icon = this.tradeGood.getIcon();
      } else if (this.tradeGoods != null) {
        int pos = (int)((delta - this.firstDrawTime) / 1000L);
        if (pos >= 0 && pos < this.tradeGoods.size()) {
          TradeGood tradeGood = this.tradeGoods.get(pos);
          title = tradeGood.getName();
          text = LanguageUtilities.string("travelbook.unlockedtradegood", new String[] { tradeGood.culture.getAdjectiveTranslated(), "" + this.nbUnlocked, "" + this.nbTotal });
          icon = tradeGood.getIcon();
        } 
      } 
      toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
      (toastGui.getMinecraft()).fontRenderer.drawString(title, 30, 7, -11534256);
      (toastGui.getMinecraft()).fontRenderer.drawString(text, 30, 18, -16777216);
      RenderHelper.enableGUIStandardItemLighting();
      if (icon != null)
        toastGui.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, icon, 8, 8); 
    } catch (Exception e) {
      MillLog.printException(toString(), e);
      return IToast.Visibility.HIDE;
    } 
    return (delta - this.firstDrawTime >= getDisplayDuration()) ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
  }
  
  private long getDisplayDuration() {
    if (this.tradeGoods != null)
      return 1000L * this.tradeGoods.size(); 
    return 2000L;
  }
  
  public String toString() {
    if (this.planSet != null)
      return "Toast:" + this.planSet; 
    if (this.villageType != null)
      return "Toast:" + this.villageType; 
    if (this.villagerType != null)
      return "Toast:" + this.villagerType; 
    if (this.tradeGood != null)
      return "Toast:" + this.tradeGood; 
    if (this.tradeGoods != null)
      return "Toast:" + this.tradeGoods; 
    return "Toast:no data";
  }
}
