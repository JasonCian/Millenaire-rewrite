package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.buildingmanagers.PanelContentGenerator;

public class GuiControlledMilitary extends GuiText {
  private final Building townHall;
  
  private final EntityPlayer player;
  
  public static class GuiButtonDiplomacy extends GuiText.MillGuiButton {
    public static final int REL_GOOD = 100;
    
    public static final int REL_NEUTRAL = 0;
    
    public static final int REL_BAD = -100;
    
    public static final int REL = 0;
    
    public static final int RAID = 1;
    
    public static final int RAIDCANCEL = 2;
    
    public Point targetVillage;
    
    public int data = 0;
    
    public GuiButtonDiplomacy(Point targetVillage, int id, int data, String s) {
      super(id, 0, 0, 0, 0, s);
      this.targetVillage = targetVillage;
      this.data = data;
    }
  }
  
  private class VillageRelation implements Comparable<VillageRelation> {
    int relation;
    
    Point pos;
    
    String name;
    
    VillageRelation(Point p, int r, String name) {
      this.relation = r;
      this.pos = p;
      this.name = name;
    }
    
    public int compareTo(VillageRelation arg0) {
      return this.name.compareTo(arg0.name);
    }
    
    public boolean equals(Object o) {
      if (o == null || !(o instanceof VillageRelation))
        return false; 
      return this.pos.equals(((VillageRelation)o).pos);
    }
    
    public int hashCode() {
      return this.pos.hashCode();
    }
  }
  
  ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");
  
  public GuiControlledMilitary(EntityPlayer player, Building th) {
    this.townHall = th;
    this.player = player;
    this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
  }
  
  protected void actionPerformed(GuiButton guibutton) throws IOException {
    if (!guibutton.enabled)
      return; 
    if (guibutton instanceof GuiButtonDiplomacy) {
      GuiButtonDiplomacy gbp = (GuiButtonDiplomacy)guibutton;
      if (gbp.id == 0) {
        ClientSender.controlledMilitaryDiplomacy(this.player, this.townHall, gbp.targetVillage, gbp.data);
      } else if (gbp.id == 1) {
        ClientSender.controlledMilitaryPlanRaid(this.player, this.townHall, gbp.targetVillage);
      } else if (gbp.id == 2) {
        ClientSender.controlledMilitaryCancelRaid(this.player, this.townHall);
      } 
      fillData();
    } 
    super.actionPerformed(guibutton);
  }
  
  protected void customDrawBackground(int i, int j, float f) {}
  
  protected void customDrawScreen(int i, int j, float f) {}
  
  private void fillData() {
    List<TextLine> text = new ArrayList<>();
    text.add(new TextLine(this.townHall.getVillageQualifiedName(), "§1", new GuiText.GuiButtonReference(this.townHall.villageType)));
    text.add(new TextLine(false));
    text.add(new TextLine(LanguageUtilities.string("ui.controldiplomacy"), "§1"));
    text.add(new TextLine());
    ArrayList<VillageRelation> relations = new ArrayList<>();
    for (Point p : this.townHall.getKnownVillages()) {
      Building b = this.townHall.mw.getBuilding(p);
      if (b != null)
        relations.add(new VillageRelation(p, this.townHall.getRelationWithVillage(p), b.getVillageQualifiedName())); 
    } 
    Collections.sort(relations);
    for (VillageRelation vr : relations) {
      Building b = this.townHall.mw.getBuilding(vr.pos);
      if (b != null) {
        String col = "";
        if (vr.relation > 70) {
          col = "<darkgreen>";
        } else if (vr.relation > 30) {
          col = "<darkblue>";
        } else if (vr.relation <= -90) {
          col = "<darkred>";
        } else if (vr.relation <= -30) {
          col = "<lightred>";
        } 
        text.add(new TextLine(col + LanguageUtilities.string("ui.villagerelations", new String[] { b.getVillageQualifiedName(), b.villageType.name, b.culture.getAdjectiveTranslated(), 
                  LanguageUtilities.string(VillageUtilities.getRelationName(vr.relation)) + " (" + vr.relation + ")" }), new GuiText.GuiButtonReference(b.villageType)));
        ((TextLine)text.get(text.size() - 1)).canCutAfter = false;
        GuiButtonDiplomacy relGood = new GuiButtonDiplomacy(vr.pos, 0, 100, LanguageUtilities.string("ui.relgood"));
        GuiButtonDiplomacy relNeutral = new GuiButtonDiplomacy(vr.pos, 0, 0, LanguageUtilities.string("ui.relneutral"));
        GuiButtonDiplomacy relBad = new GuiButtonDiplomacy(vr.pos, 0, -100, LanguageUtilities.string("ui.relbad"));
        text.add(new TextLine(relGood, relNeutral, relBad));
        text.add(new TextLine(false));
        if (this.townHall.raidTarget == null) {
          GuiButtonDiplomacy raid = new GuiButtonDiplomacy(vr.pos, 1, -100, LanguageUtilities.string("ui.raid"));
          raid.itemStackIconLeft = new ItemStack(Items.IRON_AXE, 1);
          text.add(new TextLine(raid));
        } else if (this.townHall.raidStart > 0L) {
          if (this.townHall.raidTarget.equals(vr.pos)) {
            text.add(new TextLine(LanguageUtilities.string("ui.raidinprogress"), "§4"));
          } else {
            text.add(new TextLine(LanguageUtilities.string("ui.otherraidinprogress"), "§4"));
          } 
        } else if (this.townHall.raidTarget.equals(vr.pos)) {
          GuiButtonDiplomacy raid = new GuiButtonDiplomacy(vr.pos, 2, 0, LanguageUtilities.string("ui.raidcancel"));
          raid.itemStackIconLeft = new ItemStack((Item)Items.LEATHER_BOOTS, 1);
          text.add(new TextLine(raid));
          text.add(new TextLine(LanguageUtilities.string("ui.raidplanned"), "§c"));
        } else {
          GuiButtonDiplomacy raid = new GuiButtonDiplomacy(vr.pos, 1, -100, LanguageUtilities.string("ui.raid"));
          raid.itemStackIconLeft = new ItemStack(Items.IRON_AXE, 1);
          text.add(new TextLine(raid));
          text.add(new TextLine(LanguageUtilities.string("ui.otherraidplanned"), "§c"));
        } 
        text.add(new TextLine());
      } 
    } 
    List<List<TextLine>> pages = new ArrayList<>();
    pages.add(text);
    this.textBook = this.bookManager.convertAndAdjustLines(pages);
    TextBook milBook = PanelContentGenerator.generateMilitary(this.townHall);
    this.textBook.addBook(milBook);
    this.textBook = this.bookManager.adjustTextBookLineLength(this.textBook);
    buttonPagination();
  }
  
  public ResourceLocation getPNGPath() {
    return this.background;
  }
  
  public void initData() {
    fillData();
  }
}
