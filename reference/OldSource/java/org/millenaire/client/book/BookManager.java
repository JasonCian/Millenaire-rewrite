package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.utilities.MillLog;

public class BookManager {
  protected IFontRendererWrapper fontRendererWrapper;
  
  protected int lineSizeInPx;
  
  private final int textHeight;
  
  private final int xSize;
  
  private final int ySize;
  
  private final int textXStart;
  
  public static interface IFontRendererWrapper {
    int getStringWidth(String param1String);
    
    boolean isAvailable();
  }
  
  public static class FontRendererMock implements IFontRendererWrapper {
    public int getStringWidth(String text) {
      return 1;
    }
    
    public boolean isAvailable() {
      return true;
    }
  }
  
  private static String getStringUpToSize(IFontRendererWrapper fontRendererWrapper, String input, int lineWidthInPx) {
    String output = "";
    int charPos = 0;
    while (fontRendererWrapper.getStringWidth(output) < lineWidthInPx && charPos < input.length()) {
      output = output + input.substring(charPos, charPos + 1);
      charPos++;
    } 
    return output;
  }
  
  public static List<TextLine> mergeColumns(List<TextLine> leftColumn, List<TextLine> rightColumn) {
    List<TextLine> lines = new ArrayList<>();
    for (int i = 0; i < Math.max(leftColumn.size(), rightColumn.size()); i++) {
      TextLine col1, col2;
      if (i < leftColumn.size()) {
        col1 = leftColumn.get(i);
      } else {
        col1 = new TextLine();
      } 
      if (i < rightColumn.size()) {
        col2 = rightColumn.get(i);
      } else {
        col2 = new TextLine();
      } 
      lines.add(new TextLine(new TextLine[] { col1, col2 }));
    } 
    for (TextLine line : lines)
      line.exportTwoColumns = true; 
    return lines;
  }
  
  public static List<TextLine> splitInColumns(List<TextLine> lines, int nbColumns) {
    List<TextLine> splitLines = new ArrayList<>();
    for (int i = 0; i < lines.size(); i += nbColumns) {
      TextLine[] columns = new TextLine[nbColumns];
      for (int col = 0; col < nbColumns; col++) {
        if (i + col < lines.size()) {
          columns[col] = lines.get(i + col);
        } else {
          columns[col] = new TextLine();
        } 
      } 
      splitLines.add(new TextLine(columns));
    } 
    return splitLines;
  }
  
  public static List<String> splitStringByLength(IFontRendererWrapper fontRendererWrapper, String string, int lineSize) {
    if (lineSize < 5) {
      MillLog.printException("Request to split string to size: " + lineSize, new Exception());
      List<String> list = new ArrayList<>();
      list.add(string);
      return list;
    } 
    List<String> splitStrings = new ArrayList<>();
    if (string == null)
      return splitStrings; 
    if (string.trim().length() == 0) {
      splitStrings.add("");
      return splitStrings;
    } 
    if (!fontRendererWrapper.isAvailable()) {
      splitStrings.add(string);
      return splitStrings;
    } 
    while (fontRendererWrapper.getStringWidth(string) > lineSize) {
      String fittedString = getStringUpToSize(fontRendererWrapper, string, lineSize);
      int end = fittedString.lastIndexOf(' ');
      if (end < 1)
        end = fittedString.length(); 
      String subLine = string.substring(0, end);
      string = string.substring(subLine.length()).trim();
      int colPos = subLine.lastIndexOf('\u00A7');
      if (colPos > -1)
        string = subLine.substring(colPos, colPos + 2) + string; 
      splitStrings.add(subLine);
    } 
    if (string.trim().length() > 0)
      splitStrings.add(string.trim()); 
    return splitStrings;
  }
  
  public BookManager(int xSize, int ySize, int textHeight, int lineSizeInPx, IFontRendererWrapper fontRenderer) {
    this.xSize = xSize;
    this.ySize = ySize;
    this.textHeight = textHeight;
    this.lineSizeInPx = lineSizeInPx;
    this.fontRendererWrapper = fontRenderer;
    this.textXStart = 8;
  }
  
  public BookManager(int xSize, int ySize, int textHeight, int lineSizeInPx, int textXStart, IFontRendererWrapper fontRenderer) {
    this.xSize = xSize;
    this.ySize = ySize;
    this.textHeight = textHeight;
    this.lineSizeInPx = lineSizeInPx;
    this.fontRendererWrapper = fontRenderer;
    this.textXStart = textXStart;
  }
  
  public TextBook adjustTextBookLineLength(TextBook baseText) {
    TextBook adjustedBook = new TextBook();
    for (TextPage page : baseText.getPages()) {
      TextPage newPage = new TextPage();
      for (TextLine line : page.getLines()) {
        if (line.buttons != null || line.textField != null) {
          newPage.addLine(line);
          continue;
        } 
        if (line.columns != null) {
          int lineSize = getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
          int colSize = (lineSize - (line.columns.length - 1) * 10) / line.columns.length;
          List<List<String>> splitColumnText = new ArrayList<>();
          int maxNbLines = 0;
          for (TextLine column : line.columns) {
            int adjustedColSize = colSize - column.getTextMarginLeft() - column.getLineMarginLeft() - column.getLineMarginRight();
            List<String> splitStrings = splitStringByLength(this.fontRendererWrapper, column.text, adjustedColSize);
            splitColumnText.add(splitStrings);
            if (splitStrings.size() > maxNbLines)
              maxNbLines = splitStrings.size(); 
          } 
          for (int splitLinePos = 0; splitLinePos < maxNbLines; splitLinePos++) {
            TextLine newLine = new TextLine("", line, splitLinePos);
            TextLine[] newColumns = new TextLine[line.columns.length];
            for (int colPos = 0; colPos < line.columns.length; colPos++) {
              if (splitLinePos < ((List)splitColumnText.get(colPos)).size()) {
                newColumns[colPos] = new TextLine(((List<String>)splitColumnText.get(colPos)).get(splitLinePos), line.columns[colPos], splitLinePos);
              } else {
                newColumns[colPos] = new TextLine("", line.columns[colPos], splitLinePos);
              } 
              if ((line.columns[colPos]).referenceButton != null && splitLinePos == 0)
                (newColumns[colPos]).referenceButton = (line.columns[colPos]).referenceButton; 
            } 
            newLine.columns = newColumns;
            newPage.addLine(newLine);
          } 
          continue;
        } 
        for (String l : line.text.split("<ret>")) {
          int lineSize = getLineSizeInPx() - line.getTextMarginLeft() - line.getLineMarginLeft() - line.getLineMarginRight();
          List<String> splitStrings = splitStringByLength(this.fontRendererWrapper, l, lineSize);
          int i;
          for (i = 0; i < splitStrings.size(); i++) {
            newPage.addLine(new TextLine(splitStrings.get(i), line, i));
            if (line.referenceButton != null && i == 0)
              (newPage.getLastLine()).referenceButton = line.referenceButton; 
          } 
          if (line.icons != null)
            for (i = splitStrings.size(); i < line.icons.size() / 2; i++)
              newPage.addLine(new TextLine("", line, i));  
        } 
      } 
      while (newPage.getPageHeight() > getTextHeight()) {
        TextPage newPage2 = new TextPage();
        int nblinetaken = 0;
        for (int linePos = 0; linePos < newPage.getNbLines(); linePos++) {
          int blockSize = 0;
          for (int nextLinePos = linePos; nextLinePos < newPage.getNbLines(); nextLinePos++) {
            blockSize += newPage.getLine(nextLinePos).getLineHeight();
            if ((newPage.getLine(nextLinePos)).canCutAfter)
              break; 
          } 
          if (newPage2.getPageHeight() + blockSize > getTextHeight() && blockSize < getTextHeight())
            break; 
          newPage2.addLine(newPage.getLine(linePos));
          nblinetaken++;
        } 
        for (int i = 0; i < nblinetaken; i++)
          newPage.removeLine(0); 
        newPage2 = clearEmptyLines(newPage2);
        if (newPage2 != null)
          adjustedBook.addPage(newPage2); 
      } 
      TextPage adjustedPage = clearEmptyLines(newPage);
      if (adjustedPage != null)
        adjustedBook.addPage(adjustedPage); 
    } 
    return adjustedBook;
  }
  
  private TextPage clearEmptyLines(TextPage page) {
    TextPage clearedPage = new TextPage();
    boolean nonEmptyLine = false;
    for (TextLine line : page.getLines()) {
      if (!line.empty()) {
        clearedPage.addLine(line);
        nonEmptyLine = true;
        continue;
      } 
      if (nonEmptyLine)
        clearedPage.addLine(line); 
    } 
    if (clearedPage.getNbLines() > 0)
      return clearedPage; 
    return null;
  }
  
  public TextBook convertAndAdjustLines(List<List<TextLine>> baseText) {
    TextBook book = TextBook.convertLinesToBook(baseText);
    return adjustTextBookLineLength(book);
  }
  
  public int getLineSizeInPx() {
    return this.lineSizeInPx;
  }
  
  public int getTextHeight() {
    return this.textHeight;
  }
  
  public int getTextXStart() {
    return this.textXStart;
  }
  
  public int getXSize() {
    return this.xSize;
  }
  
  public int getYSize() {
    return this.ySize;
  }
}
