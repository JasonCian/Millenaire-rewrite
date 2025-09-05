package org.millenaire.common.utilities;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.quest.Quest;
import org.millenaire.common.quest.QuestStep;

public class LanguageData {
  private static final int PARCHMENT = 0;
  
  private static final int HELP = 1;
  
  public String language;
  
  private static class ParchmentFileFilter implements FilenameFilter {
    private final String filePrefix;
    
    public ParchmentFileFilter(String filePrefix) {
      this.filePrefix = filePrefix;
    }
    
    public boolean accept(File file, String name) {
      if (!name.startsWith(this.filePrefix))
        return false; 
      if (!name.endsWith(".txt"))
        return false; 
      String id = name.substring(this.filePrefix.length() + 1, name.length() - 4);
      if (id.length() == 0 || Integer.parseInt(id) < 1)
        return false; 
      return true;
    }
  }
  
  public static void printErrors(String languageKey, BufferedWriter writer, Set<String> errors, String message) throws IOException {
    boolean consolePrint = (MillConfigValues.DEV && languageKey.equals("en"));
    if (errors.size() > 0) {
      List<String> errorsList = Lists.newArrayList(errors);
      Collections.sort(errorsList);
      writer.write(message + "\n" + "\n");
      if (consolePrint)
        MillLog.writeTextRaw(message); 
      for (String s : errorsList) {
        writer.write(s + "\n");
        if (consolePrint)
          MillLog.writeTextRaw(s); 
      } 
      writer.write("\n");
      errors.clear();
    } 
  }
  
  public String topLevelLanguage = null;
  
  public boolean serverContent;
  
  public HashMap<String, String> strings = new HashMap<>();
  
  public HashMap<String, String> questStrings = new HashMap<>();
  
  public HashMap<Integer, List<List<String>>> texts = new HashMap<>();
  
  public HashMap<Integer, String> textsVersion = new HashMap<>();
  
  public HashMap<Integer, List<List<String>>> help = new HashMap<>();
  
  public HashMap<Integer, String> helpVersion = new HashMap<>();
  
  public LanguageData(String key, boolean serverContent) {
    this.language = key;
    if ((this.language.split("_")).length > 1)
      this.topLevelLanguage = this.language.split("_")[0]; 
    this.serverContent = serverContent;
  }
  
  public void compareWithLanguage(List<File> languageDirs, HashMap<String, Integer> percentages, LanguageData ref, Map<String, String> referenceLangStrings) {
    File translationGapDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "Translation gaps");
    if (!translationGapDir.exists())
      translationGapDir.mkdirs(); 
    File file = new File(translationGapDir, this.language + "-" + ref.language + ".txt");
    if (file.exists())
      file.delete(); 
    try {
      int percentDone, translationsMissing = 0, translationsDone = 0;
      BufferedWriter writer = MillCommonUtilities.getWriter(file);
      writer.write("Translation comparison between " + this.language + " and " + ref.language + ", version " + "8.1.1" + ", date: " + MillLog.now() + "\n" + "\n");
      Set<String> errors = new HashSet<>();
      Set<String> errors2 = new HashSet<>();
      Map<String, String> langStrings = loadLangFileFromDisk(languageDirs);
      List<String> keys = new ArrayList<>(referenceLangStrings.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        if (!langStrings.containsKey(key)) {
          errors.add(key + "=");
          translationsMissing++;
          continue;
        } 
        int nbValues = (((String)referenceLangStrings.get(key)).split("<")).length - 1;
        int nbValues2 = (((String)langStrings.get(key)).split("<")).length - 1;
        if (nbValues != nbValues2) {
          errors2.add(key);
          translationsMissing++;
          continue;
        } 
        translationsDone++;
      } 
      printErrors(writer, errors, "Gap with " + ref.language + " in .lang file: ");
      printErrors(writer, errors2, "Mismatched number of parameters with " + ref.language + " in the .lang file: ");
      keys = new ArrayList<>(ref.strings.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        if (!this.strings.containsKey(key)) {
          errors.add(key + "=");
          translationsMissing++;
          continue;
        } 
        int nbValues = (((String)ref.strings.get(key)).split("<")).length - 1;
        int nbValues2 = (((String)this.strings.get(key)).split("<")).length - 1;
        if (nbValues != nbValues2) {
          errors2.add(key);
          translationsMissing++;
          continue;
        } 
        translationsDone++;
      } 
      printErrors(writer, errors, "Gap with " + ref.language + " in strings.txt file: ");
      printErrors(writer, errors2, "Mismatched number of parameters with " + ref.language + " in the strings.txt file: ");
      keys = new ArrayList<>(ref.questStrings.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        if (!this.questStrings.containsKey(key)) {
          errors.add(key);
          translationsMissing++;
          continue;
        } 
        translationsDone++;
      } 
      printErrors(writer, errors, "Gap with " + ref.language + " in quest files: ");
      for (Goal goal : Goal.goals.values()) {
        if (!this.strings.containsKey("goal." + goal.labelKey(null)) && !ref.strings.containsKey("goal." + goal.labelKey(null)))
          errors.add("goal." + goal.labelKey(null) + "="); 
      } 
      printErrors(writer, errors, "Goals with labels missing in both " + ref.language + " and " + this.language + ":");
      for (Iterator<Integer> iterator = ref.texts.keySet().iterator(); iterator.hasNext(); ) {
        int id = ((Integer)iterator.next()).intValue();
        if (!this.texts.containsKey(Integer.valueOf(id))) {
          errors.add("Parchment " + id + " is missing.");
          translationsMissing += 10;
          continue;
        } 
        if (!((String)this.textsVersion.get(Integer.valueOf(id))).equals(ref.textsVersion.get(Integer.valueOf(id)))) {
          errors.add("Parchment " + id + " has a different version: it is at version " + (String)this.textsVersion
              .get(Integer.valueOf(id)) + " while " + ref.language + " parchment is at " + (String)ref.textsVersion.get(Integer.valueOf(id)));
          translationsMissing += 5;
          continue;
        } 
        translationsDone += 10;
      } 
      printErrors(writer, errors, "Differences in parchments with " + ref.language + ":");
      for (Iterator<Integer> iterator = ref.help.keySet().iterator(); iterator.hasNext(); ) {
        int id = ((Integer)iterator.next()).intValue();
        if (!this.help.containsKey(Integer.valueOf(id))) {
          errors.add("Help " + id + " is missing.");
          translationsMissing += 10;
          continue;
        } 
        if (!((String)this.helpVersion.get(Integer.valueOf(id))).equals(ref.helpVersion.get(Integer.valueOf(id)))) {
          errors.add("Help " + id + " has a different version: it is at version " + (String)this.helpVersion.get(Integer.valueOf(id)) + " while " + ref.language + " parchment is at " + (String)ref.helpVersion.get(Integer.valueOf(id)));
          translationsMissing += 5;
          continue;
        } 
        translationsDone += 10;
      } 
      printErrors(writer, errors, "Differences in help files with " + ref.language + ":");
      for (Culture c : Culture.ListCultures) {
        int[] res = c.compareCultureLanguages(this.language, ref.language, writer);
        translationsDone += res[0];
        translationsMissing += res[1];
      } 
      if (translationsDone + translationsMissing > 0) {
        percentDone = translationsDone * 100 / (translationsDone + translationsMissing);
      } else {
        percentDone = 0;
      } 
      percentages.put(this.language, Integer.valueOf(percentDone));
      writer.write("Traduction completness: " + percentDone + "%" + "\n");
      writer.flush();
      writer.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  public void loadFromDisk(List<File> languageDirs) {
    for (File languageDir : languageDirs) {
      File effectiveLanguageDir = new File(languageDir, this.language);
      if (!effectiveLanguageDir.exists())
        effectiveLanguageDir = new File(languageDir, this.language.split("_")[0]); 
      File stringFile = new File(effectiveLanguageDir, "strings.txt");
      if (stringFile.exists())
        loadStrings(this.strings, stringFile); 
      stringFile = new File(effectiveLanguageDir, "travelbook.txt");
      if (stringFile.exists())
        loadStrings(this.strings, stringFile); 
      if (effectiveLanguageDir.exists())
        for (File file : effectiveLanguageDir.listFiles(new MillCommonUtilities.PrefixExtFileFilter("quests", "txt")))
          loadStrings(this.questStrings, file);  
    } 
    for (Quest q : Quest.quests.values()) {
      for (QuestStep step : q.steps) {
        if (step.labels.containsKey(this.language)) {
          this.questStrings.put(step.getStringKey() + "label", (String)step.labels.get(this.language));
        } else if (this.topLevelLanguage != null && step.labels.containsKey(this.topLevelLanguage)) {
          this.questStrings.put(step.getStringKey() + "label", (String)step.labels.get(this.topLevelLanguage));
        } 
        if (step.descriptions.containsKey(this.language)) {
          this.questStrings.put(step.getStringKey() + "description", (String)step.descriptions.get(this.language));
        } else if (this.topLevelLanguage != null && step.descriptions.containsKey(this.topLevelLanguage)) {
          this.questStrings.put(step.getStringKey() + "description", (String)step.descriptions.get(this.topLevelLanguage));
        } 
        if (step.descriptionsSuccess.containsKey(this.language)) {
          this.questStrings.put(step.getStringKey() + "description_success", (String)step.descriptionsSuccess.get(this.language));
        } else if (this.topLevelLanguage != null && step.descriptionsSuccess.containsKey(this.topLevelLanguage)) {
          this.questStrings.put(step.getStringKey() + "description_success", (String)step.descriptionsSuccess.get(this.topLevelLanguage));
        } 
        if (step.descriptionsRefuse.containsKey(this.language)) {
          this.questStrings.put(step.getStringKey() + "description_refuse", (String)step.descriptionsRefuse.get(this.language));
        } else if (this.topLevelLanguage != null && step.descriptionsRefuse.containsKey(this.topLevelLanguage)) {
          this.questStrings.put(step.getStringKey() + "description_refuse", (String)step.descriptionsRefuse.get(this.topLevelLanguage));
        } 
        if (step.descriptionsTimeUp.containsKey(this.language)) {
          this.questStrings.put(step.getStringKey() + "description_timeup", (String)step.descriptionsTimeUp.get(this.language));
        } else if (this.topLevelLanguage != null && step.descriptionsTimeUp.containsKey(this.topLevelLanguage)) {
          this.questStrings.put(step.getStringKey() + "description_timeup", (String)step.descriptionsTimeUp.get(this.topLevelLanguage));
        } 
        if (step.listings.containsKey(this.language)) {
          this.questStrings.put(step.getStringKey() + "listing", (String)step.listings.get(this.language));
          continue;
        } 
        if (this.topLevelLanguage != null && step.listings.containsKey(this.topLevelLanguage))
          this.questStrings.put(step.getStringKey() + "listing", (String)step.listings.get(this.topLevelLanguage)); 
      } 
    } 
    loadTextFiles(languageDirs, 0);
    loadTextFiles(languageDirs, 1);
    if (!MillConfigValues.loadedLanguages.containsKey(this.language))
      MillConfigValues.loadedLanguages.put(this.language, this); 
  }
  
  public Map<String, String> loadLangFileFromDisk(List<File> languageDirs) {
    Map<String, String> values = new HashMap<>();
    for (File languageDir : languageDirs) {
      File effectiveLanguageDir = new File(languageDir, this.language);
      if (!effectiveLanguageDir.exists())
        effectiveLanguageDir = new File(languageDir, this.language.split("_")[0]); 
      if (effectiveLanguageDir.exists())
        for (File file : effectiveLanguageDir.listFiles(new MillCommonUtilities.ExtFileFilter("lang")))
          loadStrings(values, file);  
    } 
    return values;
  }
  
  private void loadStrings(Map<String, String> strings, File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.length() > 0 && !line.startsWith("//")) {
          String[] temp = line.split("=");
          if (temp.length == 2) {
            String key = temp[0].trim().toLowerCase();
            String value = temp[1].trim();
            if (strings.containsKey(key)) {
              MillLog.error(null, "Key " + key + " is present more than once in " + file.getAbsolutePath());
              continue;
            } 
            strings.put(key, value);
            continue;
          } 
          if (line.endsWith("=") && temp.length > 0) {
            String key = temp[0].toLowerCase();
            if (strings.containsKey(key)) {
              MillLog.error(null, "Key " + key + " is present more than once in " + file.getAbsolutePath());
              continue;
            } 
            strings.put(key, "");
            continue;
          } 
          if (line.contains("====") || line.contains("<<<<<") || line.contains(">"))
            MillLog.error(null, "Git conflict lines present in " + file.getAbsolutePath()); 
        } 
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException("Excption reading file " + file.getAbsolutePath(), e);
      return;
    } 
  }
  
  public void loadTextFiles(List<File> languageDirs, int type) {
    String dirName;
    String filePrefix;
    if (type == 0) {
      dirName = "parchments";
    } else {
      dirName = "help";
    } 
    if (type == 0) {
      filePrefix = "parchment";
    } else {
      filePrefix = "help";
    } 
    for (File languageDir : languageDirs) {
      File parchmentsDir = new File(new File(languageDir, this.language), dirName);
      if (!parchmentsDir.exists())
        parchmentsDir = new File(new File(languageDir, this.language.split("_")[0]), dirName); 
      if (!parchmentsDir.exists())
        return; 
      ParchmentFileFilter filter = new ParchmentFileFilter(filePrefix);
      for (File file : parchmentsDir.listFiles(filter)) {
        String sId = file.getName().substring(filePrefix.length() + 1, file.getName().length() - 4);
        int id = 0;
        if (sId.length() > 0) {
          try {
            id = Integer.parseInt(sId);
          } catch (Exception e) {
            MillLog.printException("Error when trying to read pachment id: ", e);
          } 
        } else {
          MillLog.error(null, "Couldn't read the ID of " + file.getAbsolutePath() + ". sId: " + sId);
        } 
        if (MillConfigValues.LogOther >= 1)
          MillLog.minor(file, "Loading " + dirName + ": " + file.getAbsolutePath()); 
        List<List<String>> text = new ArrayList<>();
        String version = "unknown";
        try {
          BufferedReader reader = MillCommonUtilities.getReader(file);
          List<String> page = new ArrayList<>();
          String line;
          while ((line = reader.readLine()) != null) {
            if (line.equals("NEW_PAGE")) {
              text.add(page);
              page = new ArrayList<>();
              continue;
            } 
            if (line.startsWith("version:")) {
              version = line.split(":")[1];
              continue;
            } 
            page.add(line);
          } 
          text.add(page);
          if (type == 0) {
            this.texts.put(Integer.valueOf(id), text);
            this.textsVersion.put(Integer.valueOf(id), version);
          } else {
            this.help.put(Integer.valueOf(id), text);
            this.helpVersion.put(Integer.valueOf(id), version);
          } 
        } catch (Exception e) {
          MillLog.printException(e);
        } 
      } 
    } 
  }
  
  private void printErrors(BufferedWriter writer, Set<String> errors, String message) throws IOException {
    printErrors(this.language, writer, errors, message);
  }
  
  public void testTravelBookCompletion() {
    for (Culture culture : Culture.ListCultures) {
      try {
        int nbVillagers = 0, nbVillagersDesc = 0;
        int nbVillages = 0, nbVillagesDesc = 0;
        int nbBuildings = 0, nbBuildingsDesc = 0;
        int nbTradeGoods = 0, nbTradeGoodsDesc = 0;
        for (VillagerType vtype : culture.listVillagerTypes) {
          nbVillagers++;
          if (vtype.travelBookDisplay && culture.hasCultureString("travelbook.villager." + vtype.key + ".desc"))
            nbVillagersDesc++; 
        } 
        for (VillageType vtype : culture.listVillageTypes) {
          nbVillages++;
          if (vtype.travelBookDisplay && culture.hasCultureString("travelbook.village." + vtype.key + ".desc"))
            nbVillagesDesc++; 
        } 
        for (BuildingPlanSet planSet : culture.ListPlanSets) {
          nbBuildings++;
          if ((planSet.getFirstStartingPlan()).travelBookDisplay && culture.hasCultureString("travelbook.building." + planSet.key + ".desc"))
            nbBuildingsDesc++; 
        } 
        for (TradeGood tradeGood : culture.goodsList) {
          nbTradeGoods++;
          if (tradeGood.travelBookDisplay && culture.hasCultureString("travelbook.trade_good." + tradeGood.key + ".desc"))
            nbTradeGoodsDesc++; 
        } 
        MillLog.temp(culture, "Travel book status: Villagers " + nbVillagersDesc + "/" + nbVillagers + ", village types " + nbVillagesDesc + "/" + nbVillages + ", buildings " + nbBuildingsDesc + "/" + nbBuildings + ", trade goods " + nbTradeGoodsDesc + "/" + nbTradeGoods);
      } catch (Exception e) {
        MillLog.printException("Error when testing Travel Book for culture " + culture.key + ":", e);
      } 
    } 
  }
  
  public String toString() {
    return this.language;
  }
}
