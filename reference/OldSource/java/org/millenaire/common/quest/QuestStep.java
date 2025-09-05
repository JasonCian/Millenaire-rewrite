package org.millenaire.common.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class QuestStep {
  private final Quest quest;
  
  int pos;
  
  public static class QuestStepRelationChange {
    public final String firstVillager;
    
    public final String secondVillager;
    
    public final int change;
    
    public static QuestStepRelationChange parseString(String input) throws Exception {
      String[] params = input.split(",");
      if (params.length != 3)
        throw new Exception("Relation changes must have three parameters: villager1, villager2, change"); 
      return new QuestStepRelationChange(params[0], params[1], Integer.parseInt(params[2]));
    }
    
    private QuestStepRelationChange(String firstVillager, String secondVillager, int change) {
      this.firstVillager = firstVillager;
      this.secondVillager = secondVillager;
      this.change = change;
    }
  }
  
  public List<String> clearGlobalTagsFailure = new ArrayList<>();
  
  public List<String> clearGlobalTagsSuccess = new ArrayList<>();
  
  public List<String> clearPlayerTagsFailure = new ArrayList<>();
  
  public List<String> clearPlayerTagsSuccess = new ArrayList<>();
  
  public List<String[]> clearTagsFailure = (List)new ArrayList<>();
  
  public List<String[]> clearTagsSuccess = (List)new ArrayList<>();
  
  public final HashMap<String, String> descriptions = new HashMap<>();
  
  public final HashMap<String, String> descriptionsRefuse = new HashMap<>();
  
  public final HashMap<String, String> descriptionsSuccess = new HashMap<>();
  
  public final HashMap<String, String> descriptionsTimeUp = new HashMap<>();
  
  public final HashMap<String, String> labels = new HashMap<>();
  
  public final HashMap<String, String> listings = new HashMap<>();
  
  public int duration = 1;
  
  public List<String> forbiddenGlobalTag = new ArrayList<>();
  
  public List<String> forbiddenPlayerTag = new ArrayList<>();
  
  public int penaltyReputation = 0;
  
  public HashMap<InvItem, Integer> requiredGood = new HashMap<>();
  
  public List<String> stepRequiredGlobalTag = new ArrayList<>();
  
  public List<String> stepRequiredPlayerTag = new ArrayList<>();
  
  public HashMap<InvItem, Integer> rewardGoods = new HashMap<>();
  
  public int rewardMoney = 0;
  
  public int rewardReputation = 0;
  
  public List<String> bedrockbuildings = new ArrayList<>();
  
  public List<String> setGlobalTagsFailure = new ArrayList<>();
  
  public List<String> setGlobalTagsSuccess = new ArrayList<>();
  
  public List<String> setPlayerTagsFailure = new ArrayList<>();
  
  public List<String> setPlayerTagsSuccess = new ArrayList<>();
  
  public List<String[]> setVillagerTagsFailure = (List)new ArrayList<>();
  
  public List<String[]> setVillagerTagsSuccess = (List)new ArrayList<>();
  
  public List<String[]> setActionDataSuccess = (List)new ArrayList<>();
  
  public List<QuestStepRelationChange> relationChanges = new ArrayList<>();
  
  public boolean showRequiredGoods = true;
  
  public String villager;
  
  public QuestStep(Quest quest, int pos) {
    this.quest = quest;
    this.pos = pos;
  }
  
  public String getDescription() {
    return LanguageUtilities.questString(getStringKey() + "description", true);
  }
  
  public String getDescriptionRefuse() {
    return LanguageUtilities.questString(getStringKey() + "description_refuse", true);
  }
  
  public String getDescriptionSuccess() {
    return LanguageUtilities.questString(getStringKey() + "description_success", true);
  }
  
  public String getDescriptionTimeUp() {
    return LanguageUtilities.questString(getStringKey() + "description_timeup", false);
  }
  
  public String getLabel() {
    return LanguageUtilities.questString(getStringKey() + "label", true);
  }
  
  public String getListing() {
    return LanguageUtilities.questString(getStringKey() + "listing", false);
  }
  
  public String getStringKey() {
    return this.quest.key + "_" + this.pos + "_";
  }
  
  public String lackingConditions(EntityPlayer player) {
    MillWorldData mw = Mill.getMillWorld(player.world);
    UserProfile profile = mw.getProfile(player);
    String lackingGoods = null;
    for (InvItem item : this.requiredGood.keySet()) {
      int diff;
      if (item.special == 1) {
        int nbenchanted = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
          ItemStack stack = player.inventory.getStackInSlot(i);
          if (stack != null && stack.isItemEnchanted())
            nbenchanted++; 
        } 
        diff = ((Integer)this.requiredGood.get(item)).intValue() - nbenchanted;
      } else if (item.special == 2) {
        int nbenchanted = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
          ItemStack stack = player.inventory.getStackInSlot(i);
          if (stack != null && stack.isItemEnchanted() && stack.getItem() instanceof net.minecraft.item.ItemSword)
            nbenchanted++; 
        } 
        diff = ((Integer)this.requiredGood.get(item)).intValue() - nbenchanted;
      } else {
        diff = ((Integer)this.requiredGood.get(item)).intValue() - MillCommonUtilities.countChestItems((IInventory)player.inventory, item.getItem(), item.meta);
      } 
      if (diff > 0) {
        if (lackingGoods != null) {
          lackingGoods = lackingGoods + ", ";
        } else {
          lackingGoods = "";
        } 
        lackingGoods = lackingGoods + diff + " " + item.getName();
      } 
    } 
    if (lackingGoods != null)
      if (this.showRequiredGoods) {
        lackingGoods = LanguageUtilities.string("quest.lackingcondition") + " " + lackingGoods;
      } else {
        lackingGoods = LanguageUtilities.string("quest.lackinghiddengoods");
      }  
    boolean tagsOk = true;
    for (String tag : this.stepRequiredGlobalTag) {
      if (!mw.isGlobalTagSet(tag))
        tagsOk = false; 
    } 
    for (String tag : this.forbiddenGlobalTag) {
      if (mw.isGlobalTagSet(tag))
        tagsOk = false; 
    } 
    for (String tag : this.stepRequiredPlayerTag) {
      if (!profile.isTagSet(tag))
        tagsOk = false; 
    } 
    for (String tag : this.forbiddenPlayerTag) {
      if (profile.isTagSet(tag))
        tagsOk = false; 
    } 
    if (!tagsOk) {
      if (lackingGoods != null) {
        lackingGoods = lackingGoods + ". ";
      } else {
        lackingGoods = "";
      } 
      lackingGoods = lackingGoods + LanguageUtilities.string("quest.conditionsnotmet");
    } 
    return lackingGoods;
  }
}
