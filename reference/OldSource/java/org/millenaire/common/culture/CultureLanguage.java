package org.millenaire.common.culture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageData;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.VillagerRecord;

public class CultureLanguage {
  public Culture culture;
  
  public String language;
  
  public boolean serverContent;
  
  public static class Dialogue implements MillCommonUtilities.WeightedChoice {
    private static String adult = "adult", child = "child", male = "male", female = "female", hasspouse = "hasspouse", nospouse = "nospouse", vtype = "vtype", notvtype = "notvtype";
    
    private static String rel_spouse = "spouse", rel_parent = "parent", rel_child = "child", rel_sibling = "sibling";
    
    private static String tag_raining = "raining";
    
    private static String tag_notraining = "notraining";
    
    public String key = null;
    
    private int weight = 10;
    
    private final List<String> villager1 = new ArrayList<>();
    
    private final List<String> villager2 = new ArrayList<>();
    
    private final List<String> relations = new ArrayList<>();
    
    private final List<String> not_relations = new ArrayList<>();
    
    private final List<String> buildings = new ArrayList<>();
    
    private final List<String> not_buildings = new ArrayList<>();
    
    private final List<String> villagers = new ArrayList<>();
    
    private final List<String> not_villagers = new ArrayList<>();
    
    private final List<String> tags = new ArrayList<>();
    
    public final List<Integer> timeDelays = new ArrayList<>();
    
    public final List<Integer> speechBy = new ArrayList<>();
    
    Dialogue(String config) {
      config = config.toLowerCase();
      this.key = null;
      for (String s : config.split(",")) {
        if ((s.split(":")).length > 1) {
          String key = s.split(":")[0].trim();
          String val = s.split(":")[1].trim();
          if ((s.split(":")).length > 2)
            val = val + ":" + s.split(":")[2]; 
          if (key.equals("key")) {
            this.key = val;
          } else if (key.equals("weigth")) {
            this.weight = Integer.parseInt(val);
          } else if (key.equals("v1")) {
            this.villager1.add(val);
          } else if (key.equals("v2")) {
            this.villager2.add(val);
          } else if (key.equals("rel")) {
            this.relations.add(val);
          } else if (key.equals("notrel")) {
            this.not_relations.add(val);
          } else if (key.equals("building")) {
            this.buildings.add(val);
          } else if (key.equals("notbuilding")) {
            this.not_buildings.add(val);
          } else if (key.equals("villager")) {
            this.villagers.add(val);
          } else if (key.equals("notvillager")) {
            this.not_villagers.add(val);
          } else if (key.equals("tag")) {
            this.tags.add(val);
          } else {
            MillLog.error(this, "Could not recognise key " + key + " in dialogue declaration " + config);
          } 
        } 
      } 
    }
    
    public void checkData(Culture culture, String language) {
      for (String s : this.villager1) {
        if (!s.equals(adult) && !s.equals(child) && !s.equals(male) && !s.equals(female) && !s.equals(hasspouse) && !s.equals(nospouse) && !s.startsWith(Dialogue.vtype + ":") && 
          !s.startsWith(notvtype + ":"))
          MillLog.error(culture, language + ": Unknown v1 setting in dialogue " + this.key + ": " + s); 
        if (s.startsWith(Dialogue.vtype + ":") || s.startsWith(notvtype + ":")) {
          String s2 = s.split(":")[1].trim();
          for (String vtype : s2.split("-")) {
            vtype = vtype.trim();
            if (!culture.villagerTypes.containsKey(vtype))
              MillLog.error(culture, language + ": Unknown villager type in dialogue " + this.key + ": " + s); 
          } 
        } 
      } 
      for (String s : this.villager2) {
        if (!s.equals(adult) && !s.equals(child) && !s.equals(male) && !s.equals(female) && !s.equals(hasspouse) && !s.equals(nospouse) && !s.startsWith(Dialogue.vtype + ":") && 
          !s.startsWith(notvtype + ":"))
          MillLog.error(culture, language + ": Unknown v2 setting in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.relations) {
        if (!s.equals(rel_spouse) && !s.equals(rel_parent) && !s.equals(rel_child) && !s.equals(rel_sibling))
          MillLog.error(culture, language + ": Unknown rel setting in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.not_relations) {
        if (!s.equals(rel_spouse) && !s.equals(rel_parent) && !s.equals(rel_child) && !s.equals(rel_sibling))
          MillLog.error(culture, language + ": Unknown notrel setting in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.tags) {
        if (!s.equals(tag_raining) && !s.equals(tag_notraining))
          MillLog.error(culture, language + ": Unknown tag in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.buildings) {
        if (!culture.planSet.containsKey(s))
          MillLog.error(culture, language + ": Unknown building in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.not_buildings) {
        if (!culture.planSet.containsKey(s))
          MillLog.error(culture, language + ": Unknown notbuilding in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.villagers) {
        if (!culture.villagerTypes.containsKey(s))
          MillLog.error(culture, language + ": Unknown villager in dialogue " + this.key + ": " + s); 
      } 
      for (String s : this.not_villagers) {
        if (!culture.villagerTypes.containsKey(s))
          MillLog.error(culture, language + ": Unknown notvillager in dialogue " + this.key + ": " + s); 
      } 
    }
    
    public boolean compareWith(Dialogue d, Set<String> errors) throws IOException {
      boolean differentSentences, differentConfig = false;
      if (this.weight != d.weight)
        differentConfig = true; 
      if (!sameLists(this.villager1, d.villager1))
        differentConfig = true; 
      if (!sameLists(this.villager2, d.villager2))
        differentConfig = true; 
      if (!sameLists(this.relations, d.relations))
        differentConfig = true; 
      if (!sameLists(this.not_relations, d.not_relations))
        differentConfig = true; 
      if (!sameLists(this.buildings, d.buildings))
        differentConfig = true; 
      if (!sameLists(this.not_buildings, d.not_buildings))
        differentConfig = true; 
      if (!sameLists(this.villagers, d.villagers))
        differentConfig = true; 
      if (!sameLists(this.not_villagers, d.not_villagers))
        differentConfig = true; 
      if (!sameLists(this.tags, d.tags))
        differentConfig = true; 
      if (differentConfig)
        errors.add("Dialogue has different configurations: " + this.key); 
      if (this.timeDelays.size() != d.timeDelays.size()) {
        differentSentences = true;
        errors.add("Dialogue has different sentence numbers: " + this.key);
      } else {
        differentSentences = (!sameLists(this.timeDelays, d.timeDelays) || !sameLists(this.speechBy, d.speechBy));
        if (differentSentences)
          errors.add("Dialogue has different sentence settings: " + this.key); 
      } 
      return (!differentSentences && !differentConfig);
    }
    
    public int getChoiceWeight(EntityPlayer player) {
      return this.weight;
    }
    
    private boolean isBuildingCompatible(Building townHall) {
      for (String s : this.buildings) {
        boolean found = false;
        for (BuildingLocation bl : townHall.getLocations()) {
          if (bl.planKey.equals(s))
            found = true; 
        } 
        if (!found)
          return false; 
      } 
      for (String s : this.not_buildings) {
        for (BuildingLocation bl : townHall.getLocations()) {
          if (bl.planKey.equals(s))
            return false; 
        } 
      } 
      return true;
    }
    
    private boolean isCompatible(List<String> req, MillVillager v) {
      if (v.getRecord() == null)
        return false; 
      for (String s : req) {
        String key = s.split(":")[0];
        String val = null;
        if ((s.split(":")).length > 1)
          val = s.split(":")[1]; 
        if (key.equals(adult)) {
          if (v.vtype.isChild)
            return false; 
          continue;
        } 
        if (key.equals(child)) {
          if (!v.vtype.isChild)
            return false; 
          continue;
        } 
        if (key.equals(male)) {
          if (v.vtype.gender != 1)
            return false; 
          continue;
        } 
        if (key.equals(female)) {
          if (v.vtype.gender != 2)
            return false; 
          continue;
        } 
        if (key.equals(vtype)) {
          boolean found = false;
          for (String type : val.split("-")) {
            if (type.equals(v.vtype.key))
              found = true; 
          } 
          if (!found)
            return false; 
          continue;
        } 
        if (key.equals(notvtype)) {
          for (String type : val.split("-")) {
            if (type.equals(v.vtype.key))
              return false; 
          } 
          continue;
        } 
        if (key.equals(hasspouse)) {
          if ((v.getRecord()).spousesName == null || (v.getRecord()).spousesName.equals(""))
            return false; 
          continue;
        } 
        if ((v.getRecord()).spousesName != null && key.equals(nospouse) && 
          !(v.getRecord()).spousesName.equals(""))
          return false; 
      } 
      return true;
    }
    
    private boolean isRelCompatible(MillVillager v1, MillVillager v2) {
      for (String s : this.relations) {
        String key = s.split(":")[0];
        if (key.equals(rel_spouse)) {
          if (v1.getSpouse() != v2)
            return false; 
          continue;
        } 
        if (key.equals(rel_parent)) {
          if (!(v1.getRecord()).fathersName.equals(v2.getName()) && !(v1.getRecord()).mothersName.equals(v2.getName()))
            return false; 
          continue;
        } 
        if (key.equals(rel_child)) {
          if (!(v2.getRecord()).fathersName.equals(v1.getName()) && !(v2.getRecord()).mothersName.equals(v1.getName()))
            return false; 
          continue;
        } 
        if (key.equals(rel_sibling) && 
          !(v2.getRecord()).mothersName.equals((v1.getRecord()).mothersName))
          return false; 
      } 
      for (String s : this.not_relations) {
        String key = s.split(":")[0];
        if (key.equals(rel_spouse)) {
          if (v1.getSpouse() == v2)
            return false; 
          continue;
        } 
        if (key.equals(rel_parent)) {
          if ((v1.getRecord()).fathersName.equals(v2.getName()) || (v1.getRecord()).mothersName.equals(v2.getName()))
            return false; 
          continue;
        } 
        if (key.equals(rel_child)) {
          if ((v2.getRecord()).fathersName.equals(v1.getName()) || (v2.getRecord()).mothersName.equals(v1.getName()))
            return false; 
          continue;
        } 
        if (key.equals(rel_sibling) && 
          (v2.getRecord()).mothersName.equals((v1.getRecord()).mothersName))
          return false; 
      } 
      return true;
    }
    
    private boolean isTagCompatible(Building townHall) {
      for (String s : this.tags) {
        if (s.equals(tag_raining)) {
          if (!townHall.world.isRaining())
            return false; 
          continue;
        } 
        if (s.equals(tag_notraining) && 
          townHall.world.isRaining())
          return false; 
      } 
      return true;
    }
    
    public boolean isValidFor(MillVillager v1, MillVillager v2) {
      return (isCompatible(this.villager1, v1) && isCompatible(this.villager2, v2) && isRelCompatible(v1, v2) && isBuildingCompatible(v1.getTownHall()) && isVillagersCompatible(v1.getTownHall()) && 
        isTagCompatible(v1.getTownHall()));
    }
    
    private boolean isVillagersCompatible(Building townHall) {
      for (String s : this.villagers) {
        boolean found = false;
        for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
          if (vr.type.equals(s))
            found = true; 
        } 
        if (!found)
          return false; 
      } 
      for (String s : this.not_villagers) {
        for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
          if (vr.type.equals(s))
            return false; 
        } 
      } 
      return true;
    }
    
    private boolean sameLists(List<?> v, List<?> v2) {
      if (v.size() != v2.size())
        return false; 
      for (int i = 0; i < v.size(); i++) {
        if (!v.get(i).equals(v2.get(i)))
          return false; 
      } 
      return true;
    }
    
    public int validRoleFor(MillVillager v) {
      if (isCompatible(this.villager1, v))
        return 1; 
      if (isCompatible(this.villager2, v))
        return 2; 
      return 0;
    }
  }
  
  public static class ReputationLevel implements Comparable<ReputationLevel> {
    final String label;
    
    final String desc;
    
    public int level;
    
    public ReputationLevel(File file, String line) {
      try {
        this.level = MillCommonUtilities.readInteger(line.split(";")[0]);
      } catch (Exception e) {
        this.level = 0;
        MillLog.error(null, "Error when reading reputation line in file " + file.getAbsolutePath() + ": " + line + " : " + e.getMessage());
      } 
      this.label = line.split(";")[1];
      this.desc = line.split(";")[2];
    }
    
    public int compareTo(ReputationLevel o) {
      return this.level - o.level;
    }
    
    public boolean equals(Object o) {
      return super.equals(o);
    }
    
    public int hashCode() {
      return super.hashCode();
    }
  }
  
  public HashMap<String, List<String>> sentences = new HashMap<>();
  
  public HashMap<String, String> buildingNames = new HashMap<>();
  
  public HashMap<String, String> strings = new HashMap<>();
  
  public HashMap<String, Dialogue> dialogues = new HashMap<>();
  
  public List<ReputationLevel> reputationLevels = new ArrayList<>();
  
  public CultureLanguage(Culture c, String l, boolean serverContent) {
    this.culture = c;
    this.language = l;
    this.serverContent = serverContent;
  }
  
  public int[] compareWithLanguage(CultureLanguage ref, BufferedWriter writer) throws Exception {
    int translationsDone = 0, translationsMissing = 0;
    Set<String> errors = new HashSet<>();
    Set<String> errors2 = new HashSet<>();
    for (String key : ref.strings.keySet()) {
      if (!this.strings.containsKey(key)) {
        errors.add(key + "=");
        translationsMissing++;
        continue;
      } 
      translationsDone++;
    } 
    LanguageData.printErrors(this.language, writer, errors, "List of gaps with " + ref.language + " found in culture strings for " + this.culture.key + ": ");
    for (TradeGood g : this.culture.goodsList) {
      String key = "travelbook.trade_good." + g.key + ".desc";
      if (!this.strings.containsKey(key) && !ref.strings.containsKey(key))
        if (!((LanguageData)MillConfigValues.loadedLanguages.get(this.language)).strings.containsKey(key) && !((LanguageData)MillConfigValues.loadedLanguages.get(ref.language)).strings.containsKey(key))
          errors.add(key + "=");  
    } 
    LanguageData.printErrors(this.language, writer, errors, "Trading good desc missing in both languages for culture " + this.culture.key + ": ");
    for (String key : ref.sentences.keySet()) {
      if (!key.startsWith("villager.chat_")) {
        if (!this.sentences.containsKey(key)) {
          errors.add(key + "=");
          translationsMissing++;
          continue;
        } 
        if (((List)this.sentences.get(key)).size() < ((List)ref.sentences.get(key)).size()) {
          errors2.add(key + " (" + ((List)this.sentences.get(key)).size() + " vs " + ((List)ref.sentences.get(key)).size() + ")");
          translationsMissing++;
          continue;
        } 
        translationsDone++;
      } 
    } 
    LanguageData.printErrors(this.language, writer, errors, "List of gaps with " + ref.language + " found in culture sentences for culture " + this.culture.key + ": ");
    LanguageData.printErrors(this.language, writer, errors2, "Smaller number of sentences than in " + ref.language + " for culture " + this.culture.key + ": ");
    for (String key : ref.dialogues.keySet()) {
      if (!this.dialogues.containsKey(key)) {
        errors.add(key);
        translationsMissing++;
        continue;
      } 
      boolean matches = ((Dialogue)this.dialogues.get(key)).compareWith(ref.dialogues.get(key), errors2);
      if (matches) {
        translationsDone++;
        continue;
      } 
      translationsMissing++;
    } 
    LanguageData.printErrors(this.language, writer, errors, "Dialogues missing compared to " + ref.language + " for culture " + this.culture.key + ": ");
    LanguageData.printErrors(this.language, writer, errors2, "Mismatched dialogues with " + ref.language + " for culture " + this.culture.key + ": ");
    for (String key : ref.buildingNames.keySet()) {
      if (!this.buildingNames.containsKey(key)) {
        errors.add(key + "=");
        translationsMissing++;
        continue;
      } 
      translationsDone++;
    } 
    LanguageData.printErrors(this.language, writer, errors, "Building names missing compared to " + ref.language + " for culture " + this.culture.key + ": ");
    for (BuildingPlanSet set : this.culture.planSet.values()) {
      for (BuildingPlan[] plans : set.plans) {
        String planNameLC = (plans[0]).planName.toLowerCase();
        if (!this.buildingNames.containsKey(planNameLC) && !ref.buildingNames.containsKey(planNameLC))
          errors.add(planNameLC + "="); 
        if ((plans[0]).shop != null && !this.strings.containsKey("shop." + (plans[0]).shop) && !ref.strings.containsKey("shop." + (plans[0]).shop))
          errors2.add("shop." + (plans[0]).shop + "="); 
      } 
    } 
    LanguageData.printErrors(this.language, writer, errors, "Building names missing for culture " + this.culture.key + " in both languages: ");
    LanguageData.printErrors(this.language, writer, errors2, "Shop names missing for culture " + this.culture.key + " in both languages: ");
    for (VillagerType vt : this.culture.listVillagerTypes) {
      if (!this.strings.containsKey("villager." + vt.key) && !ref.strings.containsKey("villager." + vt.key))
        errors.add("villager." + vt.key + "="); 
    } 
    LanguageData.printErrors(this.language, writer, errors, "Villager names missing for culture " + this.culture.key + " in both languages: ");
    for (VillageType villageType : this.culture.listVillageTypes) {
      if (villageType.getNameTranslated().contains("village."))
        errors.add("village." + villageType.key + "="); 
    } 
    LanguageData.printErrors(this.language, writer, errors, "Village names missing for culture " + this.culture.key + " in both languages: ");
    if (this.reputationLevels.size() != ref.reputationLevels.size()) {
      translationsMissing += ref.reputationLevels.size() - this.reputationLevels.size();
      writer.write("Different number of reputation levels for culture " + this.culture.key + ": " + this.reputationLevels.size() + " in " + this.language + ", " + ref.reputationLevels.size() + " in " + ref.language + "." + "\n" + "\n");
    } else {
      translationsDone += ref.reputationLevels.size();
    } 
    return new int[] { translationsDone, translationsMissing };
  }
  
  public Dialogue getDialogue(MillVillager v1, MillVillager v2) {
    List<Dialogue> possibleDialogues = new ArrayList<>();
    for (Dialogue d : this.dialogues.values()) {
      if (d.isValidFor(v1, v2) || d.isValidFor(v2, v1))
        possibleDialogues.add(d); 
    } 
    if (possibleDialogues.isEmpty())
      return null; 
    MillCommonUtilities.WeightedChoice wc = MillCommonUtilities.getWeightedChoice(possibleDialogues, null);
    return (Dialogue)wc;
  }
  
  public ReputationLevel getReputationLevel(int reputation) {
    if (this.reputationLevels.size() == 0)
      return null; 
    int i = this.reputationLevels.size() - 1;
    while (i > 0 && ((ReputationLevel)this.reputationLevels.get(i)).level > reputation)
      i--; 
    return this.reputationLevels.get(i);
  }
  
  private void loadBuildingNames(List<File> languageDirs) {
    for (File languageDir : languageDirs) {
      File file = new File(new File(languageDir, this.language), this.culture.key + "_buildings.txt");
      if (!file.exists())
        file = new File(new File(languageDir, this.language.split("_")[0]), this.culture.key + "_buildings.txt"); 
      if (file.exists())
        readBuildingNameFile(file); 
    } 
    for (BuildingPlanSet set : this.culture.ListPlanSets) {
      for (BuildingPlan[] plans : set.plans) {
        for (BuildingPlan plan : plans)
          loadBuildingPlanName(plan); 
      } 
    } 
  }
  
  private void loadBuildingPlanName(BuildingPlan plan) {
    String planNameLC = plan.planName.toLowerCase();
    for (String key : plan.translatedNames.keySet()) {
      if (key.equals(this.language))
        this.buildingNames.put(planNameLC, (String)plan.translatedNames.get(key)); 
    } 
  }
  
  private void loadCultureStrings(List<File> languageDirs) {
    for (File languageDir : languageDirs) {
      File file = new File(new File(languageDir, this.language), this.culture.key + "_strings.txt");
      if (!file.exists())
        file = new File(new File(languageDir, this.language.split("_")[0]), this.culture.key + "_strings.txt"); 
      if (file.exists())
        readCultureStringFile(file); 
      file = new File(new File(languageDir, this.language), this.culture.key + "_travelbook.txt");
      if (!file.exists())
        file = new File(new File(languageDir, this.language.split("_")[0]), this.culture.key + "_travelbook.txt"); 
      if (file.exists())
        readCultureStringFile(file); 
    } 
  }
  
  private void loadDialogues(List<File> languageDirs) {
    for (File languageDir : languageDirs) {
      File file = new File(new File(languageDir, this.language), this.culture.key + "_dialogues.txt");
      if (!file.exists())
        file = new File(new File(languageDir, this.language.split("_")[0]), this.culture.key + "_dialogues.txt"); 
      if (file.exists())
        readDialoguesFile(file); 
    } 
  }
  
  public void loadFromDisk(List<File> languageDirs) {
    loadBuildingNames(languageDirs);
    loadCultureStrings(languageDirs);
    loadSentences(languageDirs);
    loadDialogues(languageDirs);
    loadReputations(languageDirs);
    if (!this.culture.loadedLanguages.containsKey(this.language))
      this.culture.loadedLanguages.put(this.language, this); 
  }
  
  private void loadReputationFile(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if ((line.split(";")).length > 2)
          this.reputationLevels.add(new ReputationLevel(file, line)); 
      } 
    } catch (Exception e) {
      MillLog.printException(e);
    } 
    Collections.sort(this.reputationLevels);
  }
  
  private void loadReputations(List<File> languageDirs) {
    for (File languageDir : languageDirs) {
      File file = new File(new File(languageDir, this.language), this.culture.key + "_reputation.txt");
      if (!file.exists())
        file = new File(new File(languageDir, this.language.split("_")[0]), this.culture.key + "_reputation.txt"); 
      if (file.exists())
        loadReputationFile(file); 
    } 
  }
  
  private void loadSentences(List<File> languageDirs) {
    for (File languageDir : languageDirs) {
      File file = new File(new File(languageDir, this.language), this.culture.key + "_sentences.txt");
      if (!file.exists())
        file = new File(new File(languageDir, this.language.split("_")[0]), this.culture.key + "_sentences.txt"); 
      if (file.exists())
        readSentenceFile(file); 
    } 
  }
  
  private void readBuildingNameFile(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] temp = line.trim().split("=");
          if (temp.length == 2) {
            String key = temp[0].toLowerCase();
            String value = temp[1].trim();
            this.buildingNames.put(key, value);
            if (MillConfigValues.LogTranslation >= 2)
              MillLog.minor(this, "Loading name: " + value + " for " + key); 
            continue;
          } 
          if (temp.length == 1) {
            String key = temp[0].toLowerCase();
            this.buildingNames.put(key, "");
            if (MillConfigValues.LogTranslation >= 2)
              MillLog.minor(this, "Loading empty name for " + key); 
          } 
        } 
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private void readCultureStringFile(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] temp = line.trim().split("=");
          if (temp.length == 2) {
            String key = temp[0].toLowerCase();
            String value = temp[1].trim();
            this.strings.put(key, value);
            if (MillConfigValues.LogTranslation >= 2)
              MillLog.minor(this, "Loading name: " + value + " for " + key); 
            continue;
          } 
          if (temp.length == 1) {
            String key = temp[0].toLowerCase();
            this.strings.put(key, "");
            if (MillConfigValues.LogTranslation >= 2)
              MillLog.minor(this, "Loading empty name for " + key); 
          } 
        } 
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException(e);
    } 
  }
  
  private boolean readDialoguesFile(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      Dialogue dialogue = null;
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          line = line.trim();
          if (line.startsWith("newchat;") && (line.split(";")).length == 2) {
            if (dialogue != null)
              if (dialogue.speechBy.size() > 0) {
                if (this.dialogues.containsKey(dialogue.key)) {
                  MillLog.error(this.culture, this.language + ": Trying to register two dialogues with the same key: " + dialogue.key);
                } else {
                  dialogue.checkData(this.culture, this.language);
                  this.dialogues.put(dialogue.key, dialogue);
                } 
              } else {
                MillLog.error(this.culture, "In dialogue file " + file.getAbsolutePath() + " dialogue " + dialogue.key + " has no sentences.");
              }  
            String s = line.split(";")[1].trim();
            dialogue = new Dialogue(s);
            if (dialogue.key == null) {
              MillLog.error(this.culture, this.language + ": Could not read dialogue line: " + line);
              dialogue = null;
            } 
            continue;
          } 
          if (dialogue != null && (line.split(";")).length == 3) {
            String[] temp = line.split(";");
            dialogue.speechBy.add(Integer.valueOf(temp[0].trim().equals("v2") ? 2 : 1));
            dialogue.timeDelays.add(Integer.valueOf(Integer.parseInt(temp[1].trim())));
            List<String> sentence = new ArrayList<>();
            sentence.add(temp[2]);
            this.sentences.put("villager.chat_" + dialogue.key + "_" + (dialogue.speechBy.size() - 1), sentence);
            continue;
          } 
          if (line.trim().length() > 0)
            MillLog.error(this.culture, this.language + ": In dialogue file " + file.getAbsolutePath() + " the following line is invalid: " + line); 
        } 
      } 
      if (dialogue.speechBy.size() > 0) {
        if (this.dialogues.containsKey(dialogue.key)) {
          MillLog.error(this.culture, this.language + ": Trying to register two dialogues with the same key: " + dialogue.key);
        } else {
          dialogue.checkData(this.culture, this.language);
          this.dialogues.put(dialogue.key, dialogue);
        } 
      } else {
        MillLog.error(this.culture, this.language + ": In dialogue file " + file.getAbsolutePath() + " dialogue " + dialogue.key + " has no sentences.");
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException(e);
      return false;
    } 
    return true;
  }
  
  private boolean readSentenceFile(File file) {
    try {
      BufferedReader reader = MillCommonUtilities.getReader(file);
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().length() > 0 && !line.startsWith("//")) {
          String[] temp = line.split("=");
          if (temp.length == 2) {
            String key = temp[0].toLowerCase();
            String value = temp[1].trim();
            if (this.sentences.containsKey(key)) {
              ((List<String>)this.sentences.get(key)).add(value);
              continue;
            } 
            List<String> v = new ArrayList<>();
            v.add(value);
            this.sentences.put(key, v);
          } 
        } 
      } 
      reader.close();
    } catch (Exception e) {
      MillLog.printException(e);
      return false;
    } 
    return true;
  }
}
