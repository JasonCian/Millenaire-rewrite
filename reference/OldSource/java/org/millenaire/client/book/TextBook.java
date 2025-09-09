package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.List;

public class TextBook {
  private final List<TextPage> pages;
  
  public static TextBook convertLinesToBook(List<List<TextLine>> linesList) {
    List<TextPage> pages = new ArrayList<>();
    for (List<TextLine> lines : linesList)
      pages.add(new TextPage(lines)); 
    return new TextBook(pages);
  }
  
  public static TextBook convertStringsToBook(List<List<String>> stringsList) {
    List<TextPage> pages = new ArrayList<>();
    for (List<String> strings : stringsList)
      pages.add(TextPage.convertStringsToPage(strings)); 
    return new TextBook(pages);
  }
  
  public TextBook() {
    this.pages = new ArrayList<>();
  }
  
  public TextBook(List<TextPage> pages) {
    this.pages = pages;
  }
  
  public void addBook(TextBook book) {
    for (TextPage page : book.getPages())
      this.pages.add(page); 
  }
  
  public void addPage(TextPage page) {
    this.pages.add(page);
  }
  
  public TextPage getPage(int pos) {
    return this.pages.get(pos);
  }
  
  public List<TextPage> getPages() {
    return this.pages;
  }
  
  public int nbPages() {
    return this.pages.size();
  }
}
