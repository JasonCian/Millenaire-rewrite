package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.millenaire.client.gui.text.GuiText;

public class TextPage {
  private final List<TextLine> lines;
  
  public static TextPage convertStringsToPage(List<String> strings) {
    List<TextLine> lines = new ArrayList<>();
    for (String s : strings)
      lines.add(new TextLine(s)); 
    return new TextPage(lines);
  }
  
  public TextPage() {
    this.lines = new ArrayList<>();
  }
  
  public TextPage(List<TextLine> lines) {
    this.lines = lines;
  }
  
  public void addBlankLine() {
    this.lines.add(new TextLine());
  }
  
  public void addLine(String string) {
    this.lines.add(new TextLine(string));
  }
  
  public void addLine(String string, GuiText.GuiButtonReference guiButtonReference) {
    this.lines.add(new TextLine(string, guiButtonReference));
  }
  
  public void addLine(String string, ItemStack icon, boolean displayItemLegend) {
    this.lines.add(new TextLine(string, icon, displayItemLegend));
  }
  
  public void addLine(String string, String colour) {
    TextLine line = new TextLine(string);
    line.style = colour;
    this.lines.add(line);
  }
  
  public void addLine(String string, String style, GuiText.GuiButtonReference guiButtonReference) {
    this.lines.add(new TextLine(string, style, guiButtonReference));
  }
  
  public void addLine(String string, String colour, ItemStack icon, boolean displayItemLegend) {
    TextLine line = new TextLine(string, icon, displayItemLegend);
    line.style = colour;
    this.lines.add(line);
  }
  
  public void addLine(TextLine line) {
    this.lines.add(line);
  }
  
  public void addLineWithColumns(TextLine... columns) {
    this.lines.add(new TextLine(columns));
  }
  
  public TextLine getLastLine() {
    if (this.lines.size() == 0)
      return null; 
    return this.lines.get(this.lines.size() - 1);
  }
  
  public TextLine getLine(int pos) {
    return this.lines.get(pos);
  }
  
  public List<TextLine> getLines() {
    return this.lines;
  }
  
  public int getNbLines() {
    return this.lines.size();
  }
  
  public int getPageHeight() {
    int height = 0;
    for (TextLine line : this.lines)
      height += line.getLineHeight(); 
    return height;
  }
  
  public void removeLine(int pos) {
    this.lines.remove(pos);
  }
}
