package org.millenaire.common.goal.leisure;

import java.util.List;
import net.minecraft.entity.Entity;
import org.millenaire.common.config.DocumentedElement.Documentation;
import org.millenaire.common.culture.CultureLanguage;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.WorldUtilities;

@Documentation("Make the villager meet a villager looking for someone to chat, to initiate a dialog")
public class GoalGoChat extends Goal {
  private final char[] chatColours = new char[] { 'f', '3', 'a', '7', 'c' };
  
  public GoalGoChat() {
    this.leasure = true;
    this.travelBookShow = false;
    this.sprint = false;
  }
  
  public int actionDuration(MillVillager villager) {
    return 40;
  }
  
  public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
    for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
      if (v != villager && Goal.gosocialise.key.equals(v.goalKey) && v.getPos().distanceToSquared((Entity)villager) < 25.0D)
        return packDest(null, null, (Entity)v); 
    } 
    return null;
  }
  
  protected boolean isPossibleSpecific(MillVillager villager) throws Exception {
    return (getDestination(villager) != null);
  }
  
  public boolean lookAtGoal() {
    return true;
  }
  
  public void onAccept(MillVillager villager) throws Exception {
    Goal.GoalInformation info = getDestination(villager);
    if (info != null) {
      MillVillager target = (MillVillager)info.getTargetEnt();
      target.clearGoal();
      target.goalKey = this.key;
      target.setGoalDestEntity((Entity)villager);
      CultureLanguage.Dialogue dialog = villager.getCulture().pickNewDialogue(villager, target);
      if (dialog != null) {
        int role = dialog.validRoleFor(villager);
        villager.setGoalInformation(null);
        villager.setGoalDestEntity((Entity)target);
        char col = this.chatColours[MillCommonUtilities.randomInt(this.chatColours.length)];
        col = 'f';
        if (dialog != null) {
          List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(villager.world, MillVillager.class, villager.getPos(), 5, 5);
          boolean dialogueChat = true;
          for (Entity ent : entities) {
            if (ent != villager && ent != target) {
              MillVillager v = (MillVillager)ent;
              if (this.key.equals(v.goalKey) && v.dialogueChat)
                dialogueChat = false; 
            } 
          } 
          villager.dialogueKey = dialog.key;
          villager.dialogueRole = role;
          villager.dialogueStart = villager.world.getWorldTime();
          villager.dialogueColour = col;
          villager.dialogueChat = dialogueChat;
          target.dialogueKey = dialog.key;
          target.dialogueRole = 3 - role;
          target.dialogueStart = villager.world.getWorldTime();
          target.dialogueColour = col;
          target.dialogueChat = dialogueChat;
        } 
      } 
    } 
    super.onAccept(villager);
  }
  
  public boolean performAction(MillVillager villager) throws Exception {
    return (villager.dialogueKey == null);
  }
  
  public int priority(MillVillager villager) throws Exception {
    return 10;
  }
  
  public int range(MillVillager villager) {
    return 3;
  }
}
