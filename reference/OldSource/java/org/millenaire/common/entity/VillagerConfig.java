package org.millenaire.common.entity;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations.ConfigField;
import org.millenaire.common.annotedparameters.ConfigAnnotations.FieldDocumentation;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;

public class VillagerConfig {
  private static final String DEFAULT = "default";
  
  public static Map<String, VillagerConfig> villagerConfigs = new HashMap<>();
  
  public static VillagerConfig DEFAULT_CONFIG;
  
  public static String CATEGORY_WEAPONSHANDTOHAND = "weaponshandtohand";
  
  public static String CATEGORY_WEAPONSRANGED = "weaponsranged";
  
  public static String CATEGORY_ARMOURSHELMET = "armourshelmet";
  
  public static String CATEGORY_ARMOURSCHESTPLATE = "armourschestplate";
  
  public static String CATEGORY_ARMOURSLEGGINGS = "armoursleggings";
  
  public static String CATEGORY_ARMOURSBOOTS = "armoursboots";
  
  public static String CATEGORY_TOOLSSWORD = "toolssword";
  
  public static String CATEGORY_TOOLSPICKAXE = "toolspickaxe";
  
  public static String CATEGORY_TOOLSAXE = "toolsaxe";
  
  public static String CATEGORY_TOOLSHOE = "toolshoe";
  
  public static String CATEGORY_TOOLSSHOVEL = "toolsshovel";
  
  public final String key;
  
  private static VillagerConfig copyDefault(String key) {
    VillagerConfig newConfig = new VillagerConfig(key);
    for (Field field : VillagerConfig.class.getFields()) {
      try {
        if (field.getType() == Map.class) {
          Map<?, ?> map = (Map)field.get(DEFAULT_CONFIG);
          field.set(newConfig, new HashMap<>(map));
        } 
      } catch (Exception e) {
        MillLog.printException("Exception when duplicating maps: " + field, e);
      } 
    } 
    return newConfig;
  }
  
  private static List<File> getVillagerConfigFiles() {
    VirtualDir virtualConfigDir = Mill.virtualLoadingDir.getChildDirectory("villagerconfig");
    return virtualConfigDir.listFilesRecursive((FilenameFilter)new MillCommonUtilities.ExtFileFilter("txt"));
  }
  
  public static void loadConfigs() {
    DEFAULT_CONFIG = new VillagerConfig("default");
    ParametersManager.loadAnnotedParameterData(Mill.virtualLoadingDir.getChildDirectory("villagerconfig").getChildFile("default.txt"), DEFAULT_CONFIG, null, "villager config", null);
    DEFAULT_CONFIG.initData();
    for (File file : getVillagerConfigFiles()) {
      if (!file.getName().equals("default.txt")) {
        String key = file.getName().split("\\.")[0].toLowerCase();
        VillagerConfig config = copyDefault(key);
        ParametersManager.loadAnnotedParameterData(file, config, null, "villager config", null);
        config.initData();
        villagerConfigs.put(key, config);
      } 
    } 
  }
  
  public Map<InvItem, Integer> weapons = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "weaponHandToHandPriority")
  @FieldDocumentation(explanation = "A hand to hand weapon and its use priority. If the priority is 0, won't get used.")
  public Map<InvItem, Integer> weaponsHandToHand = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "weaponRangedPriority")
  @FieldDocumentation(explanation = "A ranged weapon and its use priority. If the priority is 0, won't get used.")
  public Map<InvItem, Integer> weaponsRanged = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "armourHelmetPriority")
  @FieldDocumentation(explanation = "A helmet and its use priority. If the priority is 0, won't get used.")
  public Map<InvItem, Integer> armoursHelmet = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "armourChestplatePriority")
  @FieldDocumentation(explanation = "A chest plate and its use priority. If the priority is 0, won't get used.")
  public Map<InvItem, Integer> armoursChestplate = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "armourLeggingsPriority")
  @FieldDocumentation(explanation = "A leggings and its use priority. If the priority is 0, won't get used.")
  public Map<InvItem, Integer> armoursLeggings = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "armourBootsPriority")
  @FieldDocumentation(explanation = "A pair of boots and its use priority. If the priority is 0, won't get used.")
  public Map<InvItem, Integer> armoursBoots = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "toolSwordPriority")
  @FieldDocumentation(explanation = "A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
  public Map<InvItem, Integer> toolsSword = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "toolPickaxePriority")
  @FieldDocumentation(explanation = "A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
  public Map<InvItem, Integer> toolsPickaxe = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "toolAxePriority")
  @FieldDocumentation(explanation = "A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
  public Map<InvItem, Integer> toolsAxe = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "toolHoePriority")
  @FieldDocumentation(explanation = "A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
  public Map<InvItem, Integer> toolsHoe = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "toolShovelPriority")
  @FieldDocumentation(explanation = "A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
  public Map<InvItem, Integer> toolsShovel = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "foodGrowthValue")
  @FieldDocumentation(explanation = "A food a child can eat to grow and its growth value.")
  public Map<InvItem, Integer> foodsGrowth = new HashMap<>();
  
  @ConfigField(type = AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName = "foodConceptionValue")
  @FieldDocumentation(explanation = "A food an adult can eat to increase conception chances and the increase value.")
  public Map<InvItem, Integer> foodsConception = new HashMap<>();
  
  public List<InvItem> weaponsHandToHandSorted;
  
  public List<InvItem> weaponsRangedSorted;
  
  public List<InvItem> weaponsSorted;
  
  public List<InvItem> armoursHelmetSorted;
  
  public List<InvItem> armoursChestplateSorted;
  
  public List<InvItem> armoursLeggingsSorted;
  
  public List<InvItem> armoursBootsSorted;
  
  public List<InvItem> toolsSwordSorted;
  
  public List<InvItem> toolsPickaxeSorted;
  
  public List<InvItem> toolsAxeSorted;
  
  public List<InvItem> toolsHoeSorted;
  
  public List<InvItem> toolsShovelSorted;
  
  public List<InvItem> foodsGrowthSorted;
  
  public List<InvItem> foodsConceptionSorted;
  
  public Map<String, List<InvItem>> categories = new HashMap<>();
  
  public VillagerConfig(String key) {
    this.key = key;
  }
  
  public InvItem getBestAxe(MillVillager villager) {
    return getBestItem(this.toolsAxeSorted, villager);
  }
  
  public InvItem getBestConceptionFood(Building house) {
    return getBestItemInBuilding(this.foodsConceptionSorted, house);
  }
  
  public InvItem getBestHoe(MillVillager villager) {
    return getBestItem(this.toolsHoeSorted, villager);
  }
  
  private InvItem getBestItem(List<InvItem> sortedItems, MillVillager villager) {
    for (InvItem invItem : sortedItems) {
      if (villager.countInv(invItem.item) > 0)
        return invItem; 
    } 
    return null;
  }
  
  public InvItem getBestItemByCategoryName(String categoryName, MillVillager villager) {
    return getBestItem(this.categories.get(categoryName), villager);
  }
  
  private InvItem getBestItemInBuilding(List<InvItem> sortedItems, Building house) {
    for (InvItem invItem : sortedItems) {
      if (house.countGoods(invItem.item) > 0)
        return invItem; 
    } 
    return null;
  }
  
  public InvItem getBestPickaxe(MillVillager villager) {
    return getBestItem(this.toolsPickaxeSorted, villager);
  }
  
  public InvItem getBestShovel(MillVillager villager) {
    return getBestItem(this.toolsShovelSorted, villager);
  }
  
  public InvItem getBestSword(MillVillager villager) {
    return getBestItem(this.toolsSwordSorted, villager);
  }
  
  public InvItem getBestWeapon(MillVillager villager) {
    return getBestItem(this.weaponsSorted, villager);
  }
  
  public InvItem getBestWeaponHandToHand(MillVillager villager) {
    return getBestItem(this.weaponsHandToHandSorted, villager);
  }
  
  public InvItem getBestWeaponRanged(MillVillager villager) {
    return getBestItem(this.weaponsRangedSorted, villager);
  }
  
  private void initData() {
    this.weapons.putAll(this.weaponsHandToHand);
    this.weapons.putAll(this.weaponsRanged);
    for (Field field : VillagerConfig.class.getFields()) {
      try {
        if (field.getType() == Map.class) {
          ParameterizedType pt = (ParameterizedType)field.getGenericType();
          if (pt.getActualTypeArguments()[0] == InvItem.class && pt.getActualTypeArguments()[1] == Integer.class) {
            Map<InvItem, Integer> map = (Map<InvItem, Integer>)field.get(this);
            Set<InvItem> keysCopy = new HashSet<>(map.keySet());
            for (InvItem item : keysCopy) {
              if (((Integer)map.get(item)).intValue() <= 0)
                map.remove(item); 
            } 
            List<InvItem> sortedList = new ArrayList<>(map.keySet());
            Collections.sort(sortedList, (key1, key2) -> map.get(key2).compareTo(map.get(key1)));
            Field listField = VillagerConfig.class.getDeclaredField(field.getName() + "Sorted");
            listField.set(this, sortedList);
            this.categories.put(field.getName().toLowerCase(), sortedList);
          } 
        } 
      } catch (Exception e) {
        MillLog.printException("Exception when creating sorted list for field: " + field, e);
      } 
    } 
  }
}
