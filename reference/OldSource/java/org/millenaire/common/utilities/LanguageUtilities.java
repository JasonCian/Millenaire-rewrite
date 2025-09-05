package org.millenaire.common.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.millenaire.client.book.TravelBookExporter;
import org.millenaire.common.buildingplan.BuildingDevUtilities;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillLog.MillenaireException;

public class LanguageUtilities {
  public static final char BLACK = '0';
  
  public static final char DARKBLUE = '1';
  
  public static final char DARKGREEN = '2';
  
  public static final char LIGHTBLUE = '3';
  
  public static final char DARKRED = '4';
  
  public static final char PURPLE = '5';
  
  public static final char ORANGE = '6';
  
  public static final char LIGHTGREY = '7';
  
  public static final char DARKGREY = '8';
  
  public static final char BLUE = '9';
  
  public static final char LIGHTGREEN = 'a';
  
  public static final char CYAN = 'b';
  
  public static final char LIGHTRED = 'c';
  
  public static final char PINK = 'd';
  
  public static final char YELLOW = 'e';
  
  public static final char WHITE = 'f';
  
  public static String loadedLanguage = null;
  
  public static void applyLanguage() {
    InvItem iv = InvItem.createInvItem((Item)MillItems.SUMMONING_WAND, 1);
    MillLog.major(null, "LanguageData loaded: " + MillConfigValues.effective_language + ". Wand invitem name: " + iv.getName());
    if (MillConfigValues.generateBuildingRes && !Mill.proxy.isTrueServer()) {
      MillLog.major(null, "Generating building res file.");
      BuildingDevUtilities.generateBuildingRes();
      try {
        BuildingDevUtilities.generateWikiTable();
      } catch (MillenaireException e) {
        MillLog.printException(e);
      } 
      MillLog.major(null, "Generated building res file.");
    } 
  }
  
  public static String fillInName(String s) {
    if (s == null)
      return ""; 
    EntityPlayer player = Mill.proxy.getTheSinglePlayer();
    if (player != null)
      return s.replaceAll("\\$name", player.getName()); 
    return s;
  }
  
  public static List<List<String>> getHelp(int id) {
    if (MillConfigValues.mainLanguage.help.containsKey(Integer.valueOf(id)))
      return MillConfigValues.mainLanguage.help.get(Integer.valueOf(id)); 
    if (MillConfigValues.fallbackLanguage.help.containsKey(Integer.valueOf(id)))
      return MillConfigValues.fallbackLanguage.help.get(Integer.valueOf(id)); 
    return null;
  }
  
  public static List<String> getHoFData() {
    List<String> hofData = new ArrayList<>();
    try {
      BufferedReader reader = MillCommonUtilities.getReader(new File(MillCommonUtilities.getMillenaireContentDir(), "hof.txt"));
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.length() > 0 && !line.startsWith("//"))
          hofData.add(line); 
      } 
    } catch (Exception e) {
      MillLog.printException("Error when loading HoF: ", e);
    } 
    return hofData;
  }
  
  public static List<File> getLanguageDirs() {
    List<File> languageDirs = new ArrayList<>();
    for (File dir : Mill.loadingDirs) {
      File languageDir = new File(dir, "languages");
      if (languageDir.exists())
        languageDirs.add(languageDir); 
    } 
    languageDirs.add(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "languages"));
    return languageDirs;
  }
  
  public static List<List<String>> getParchment(int id) {
    if (MillConfigValues.mainLanguage.texts.containsKey(Integer.valueOf(id)))
      return MillConfigValues.mainLanguage.texts.get(Integer.valueOf(id)); 
    if (MillConfigValues.fallbackLanguage.texts.containsKey(Integer.valueOf(id)))
      return MillConfigValues.fallbackLanguage.texts.get(Integer.valueOf(id)); 
    return null;
  }
  
  public static String getRawString(String key, boolean mustFind) {
    return getRawString(key, mustFind, true, true);
  }
  
  public static String getRawString(String key, boolean mustFind, boolean main, boolean fallback) {
    if (main && MillConfigValues.mainLanguage != null && MillConfigValues.mainLanguage.strings.containsKey(key))
      return MillConfigValues.mainLanguage.strings.get(key); 
    if (main && MillConfigValues.serverMainLanguage != null && MillConfigValues.serverMainLanguage.strings.containsKey(key))
      return MillConfigValues.serverMainLanguage.strings.get(key); 
    if (fallback && MillConfigValues.fallbackLanguage != null && MillConfigValues.fallbackLanguage.strings.containsKey(key))
      return MillConfigValues.fallbackLanguage.strings.get(key); 
    if (fallback && MillConfigValues.serverFallbackLanguage != null && MillConfigValues.serverFallbackLanguage.strings.containsKey(key))
      return MillConfigValues.serverFallbackLanguage.strings.get(key); 
    if (mustFind && MillConfigValues.LogTranslation >= 1)
      MillLog.error(null, "String not found: " + key); 
    if (mustFind)
      return key; 
    return null;
  }
  
  public static String getRawStringFallbackOnly(String key, boolean mustFind) {
    return getRawString(key, mustFind, false, true);
  }
  
  public static String getRawStringMainOnly(String key, boolean mustFind) {
    return getRawString(key, mustFind, true, false);
  }
  
  public static boolean hasString(String key) {
    if (!isTranslationLoaded())
      return false; 
    key = key.toLowerCase();
    String rawResult = getRawString(key, true);
    if (key.equalsIgnoreCase(rawResult))
      return false; 
    return true;
  }
  
  public static boolean isTranslationLoaded() {
    return (MillConfigValues.mainLanguage != null);
  }
  
  public static void loadLanguages(String minecraftLanguage) {
    if (!MillConfigValues.main_language.equals("")) {
      MillConfigValues.effective_language = MillConfigValues.main_language;
    } else if (minecraftLanguage != null) {
      MillConfigValues.effective_language = minecraftLanguage;
    } else {
      MillConfigValues.effective_language = "fr";
    } 
    if (loadedLanguage != null && loadedLanguage.equals(MillConfigValues.effective_language))
      return; 
    MillLog.major(null, "Loading language: " + MillConfigValues.effective_language);
    loadedLanguage = MillConfigValues.effective_language;
    List<File> languageDirs = getLanguageDirs();
    MillConfigValues.mainLanguage = new LanguageData(MillConfigValues.effective_language, false);
    MillConfigValues.mainLanguage.loadFromDisk(languageDirs);
    if (MillConfigValues.main_language.equals(MillConfigValues.fallback_language)) {
      MillConfigValues.fallbackLanguage = MillConfigValues.mainLanguage;
    } else {
      MillConfigValues.fallbackLanguage = new LanguageData(MillConfigValues.fallback_language, false);
      MillConfigValues.fallbackLanguage.loadFromDisk(languageDirs);
    } 
    if (MillConfigValues.loadAllLanguages) {
      File mainDir = languageDirs.get(0);
      for (File lang : mainDir.listFiles()) {
        if (lang.isDirectory() && !lang.isHidden()) {
          String key = lang.getName().toLowerCase();
          if (!MillConfigValues.loadedLanguages.containsKey(key)) {
            LanguageData l = new LanguageData(key, false);
            l.loadFromDisk(languageDirs);
          } 
        } 
      } 
    } 
    if (!MillConfigValues.loadedLanguages.containsKey("fr")) {
      LanguageData l = new LanguageData("fr", false);
      l.loadFromDisk(languageDirs);
    } 
    if (!MillConfigValues.loadedLanguages.containsKey("en")) {
      LanguageData l = new LanguageData("en", false);
      l.loadFromDisk(languageDirs);
    } 
    for (Culture c : Culture.ListCultures)
      c.loadLanguages(languageDirs, MillConfigValues.effective_language, MillConfigValues.fallback_language); 
    applyLanguage();
    if (MillConfigValues.DEV && MillConfigValues.loadedLanguages.containsKey("en"))
      ((LanguageData)MillConfigValues.loadedLanguages.get("en")).testTravelBookCompletion(); 
    if (MillConfigValues.generateBuildingRes && !Mill.proxy.isTrueServer()) {
      ArrayList<LanguageData> list = new ArrayList<>(MillConfigValues.loadedLanguages.values());
      for (LanguageData l : list) {
        if (l.language.equals("en") || l.language.equals("fr"))
          BuildingDevUtilities.generateTranslatedHoFData(l); 
      } 
    } 
    if (MillConfigValues.generateTranslationGap) {
      HashMap<String, Integer> percentageComplete = new HashMap<>();
      ArrayList<LanguageData> list = new ArrayList<>(MillConfigValues.loadedLanguages.values());
      String refLanguage = MillConfigValues.fallback_language;
      LanguageData ref = null;
      if (MillConfigValues.loadedLanguages.containsKey(refLanguage)) {
        ref = (LanguageData)MillConfigValues.loadedLanguages.get(refLanguage);
      } else {
        ref = new LanguageData(refLanguage, false);
        ref.loadFromDisk(languageDirs);
      } 
      Map<String, String> referenceLangString = ref.loadLangFileFromDisk(languageDirs);
      LanguageData altRef = null;
      if (ref.language.equals("en")) {
        if (MillConfigValues.loadedLanguages.containsKey("fr")) {
          altRef = (LanguageData)MillConfigValues.loadedLanguages.get("fr");
        } else {
          altRef = new LanguageData("fr", false);
          altRef.loadFromDisk(languageDirs);
        } 
      } else if (MillConfigValues.loadedLanguages.containsKey("en")) {
        altRef = (LanguageData)MillConfigValues.loadedLanguages.get("en");
      } else {
        altRef = new LanguageData("en", false);
        altRef.loadFromDisk(languageDirs);
      } 
      Map<String, String> altReferenceLangString = altRef.loadLangFileFromDisk(languageDirs);
      for (LanguageData l : list) {
        if (l.language.equals(ref.language)) {
          l.compareWithLanguage(languageDirs, percentageComplete, altRef, altReferenceLangString);
          continue;
        } 
        l.compareWithLanguage(languageDirs, percentageComplete, ref, referenceLangString);
      } 
      File translationGapDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "Translation gaps");
      if (!translationGapDir.exists())
        translationGapDir.mkdirs(); 
      File file = new File(translationGapDir, "Results.txt");
      if (file.exists())
        file.delete(); 
      try {
        BufferedWriter writer = MillCommonUtilities.getWriter(file);
        for (String key : percentageComplete.keySet())
          writer.write(key + ": " + percentageComplete.get(key) + "%" + "\n"); 
        writer.close();
      } catch (Exception e) {
        MillLog.printException(e);
      } 
      MillLog.major(null, "Generated translation gap files for " + percentageComplete.size() + " languages.");
    } 
    if (MillConfigValues.DEV)
      MillConfigValues.writeBaseConfigFile(); 
    if (MillConfigValues.generateTravelBookExport)
      TravelBookExporter.exportTravelBookData(); 
  }
  
  public static String questString(String key, boolean required) {
    return questString(key, true, true, required);
  }
  
  public static String questString(String key, boolean main, boolean fallback, boolean required) {
    key = key.toLowerCase();
    if (main && MillConfigValues.mainLanguage != null && MillConfigValues.mainLanguage.questStrings.containsKey(key))
      return MillConfigValues.mainLanguage.questStrings.get(key); 
    if (main && MillConfigValues.serverMainLanguage != null && MillConfigValues.serverMainLanguage.questStrings.containsKey(key))
      return MillConfigValues.serverMainLanguage.questStrings.get(key); 
    if (fallback && MillConfigValues.fallbackLanguage != null && MillConfigValues.fallbackLanguage.questStrings.containsKey(key))
      return MillConfigValues.fallbackLanguage.questStrings.get(key); 
    if (fallback && MillConfigValues.serverFallbackLanguage != null && MillConfigValues.serverFallbackLanguage.questStrings.containsKey(key))
      return MillConfigValues.serverFallbackLanguage.questStrings.get(key); 
    if (required)
      return key; 
    return null;
  }
  
  public static String removeAccent(String source) {
    return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[̀-ͯ]", "");
  }
  
  public static String string(String key) {
    if (!isTranslationLoaded())
      return ""; 
    key = key.toLowerCase();
    String rawResult = getRawString(key, true);
    if (key.equalsIgnoreCase(rawResult) && MillConfigValues.DEV && MillConfigValues.LogTranslation >= 1) {
      MillLog.temp(null, "Reloading because of missing key:" + key);
      loadLanguages(null);
      rawResult = getRawString(key, true);
    } 
    return fillInName(rawResult);
  }
  
  public static String string(String key, String... values) {
    String s = string(key);
    if (!s.equalsIgnoreCase(key)) {
      int pos = 0;
      for (String value : values) {
        if (value != null) {
          s = s.replaceAll("<" + pos + ">", unknownString(value));
        } else {
          s = s.replaceAll("<" + pos + ">", "");
        } 
        pos++;
      } 
    } else {
      for (String value : values)
        s = s + ":" + value; 
    } 
    return s;
  }
  
  public static String string(String[] values) {
    if (values.length == 0)
      return ""; 
    String s = unknownString(values[0]);
    int pos = -1;
    for (String value : values) {
      if (pos > -1)
        if (value != null) {
          s = s.replaceAll("<" + pos + ">", unknownString(value));
        } else {
          s = s.replaceAll("<" + pos + ">", "");
        }  
      pos++;
    } 
    return fillInName(s);
  }
  
  public static ITextComponent textComponent(String key) {
    return (ITextComponent)new TextComponentString(string(key));
  }
  
  public static ITextComponent textComponent(String key, String... values) {
    return (ITextComponent)new TextComponentString(string(key, values));
  }
  
  public static String unknownString(String key) {
    if (key == null)
      return ""; 
    if (!isTranslationLoaded())
      return key; 
    if (key.startsWith("_item:")) {
      int id = Integer.parseInt(key.split(":")[1]);
      int meta = Integer.parseInt(key.split(":")[2]);
      InvItem item = InvItem.createInvItem(MillCommonUtilities.getItemById(id), meta);
      return item.getName();
    } 
    if (key.startsWith("_buildingGame:")) {
      String cultureKey = key.split(":")[1];
      Culture culture = Culture.getCultureByName(cultureKey);
      if (culture != null) {
        String buildingKey = key.split(":")[2];
        BuildingPlanSet set = culture.getBuildingPlanSet(buildingKey);
        if (set != null) {
          int variation = Integer.parseInt(key.split(":")[3]);
          if (variation < set.plans.size()) {
            int level = Integer.parseInt(key.split(":")[4]);
            if (level < ((BuildingPlan[])set.plans.get(variation)).length) {
              BuildingPlan plan = ((BuildingPlan[])set.plans.get(variation))[level];
              return plan.getNameTranslated();
            } 
          } 
        } 
      } 
    } 
    if (key.startsWith("culture:")) {
      String cultureKey = key.split(":")[1];
      String stringKey = key.split(":")[2];
      Culture culture = Culture.getCultureByName(cultureKey);
      if (culture != null)
        return culture.getCultureString(stringKey); 
    } 
    String rawKey = getRawString(key, false);
    if (rawKey != null)
      return fillInName(rawKey); 
    return key;
  }
}
