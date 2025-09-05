package org.millenaire.common.utilities;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class VillageUtilities {
  public static String getRelationName(int relation) {
    if (relation >= 90)
      return "relation.excellent"; 
    if (relation >= 70)
      return "relation.verygood"; 
    if (relation >= 50)
      return "relation.good"; 
    if (relation >= 30)
      return "relation.decent"; 
    if (relation >= 10)
      return "relation.fair"; 
    if (relation <= -90)
      return "relation.openconflict"; 
    if (relation <= -70)
      return "relation.atrocious"; 
    if (relation <= -50)
      return "relation.verybad"; 
    if (relation <= -30)
      return "relation.bad"; 
    if (relation <= -10)
      return "relation.chilly"; 
    return "relation.neutral";
  }
  
  public static List<EntityPlayerMP> getServerPlayers(World world) {
    List<EntityPlayerMP> players = new ArrayList<>();
    for (EntityPlayer p : world.playerEntities)
      players.add((EntityPlayerMP)p); 
    return players;
  }
  
  public static UserProfile getServerProfile(World world, EntityPlayer player) {
    MillWorldData mw = Mill.getMillWorld(world);
    if (mw == null)
      return null; 
    return mw.getProfile(player);
  }
  
  public static String getVillagerSentence(MillVillager v, String playerName, boolean nativeSpeech) {
    if (v.speech_key == null)
      return null; 
    if (!nativeSpeech && !v.getCulture().canReadDialogues(playerName))
      return null; 
    List<String> variants = v.getCulture().getSentences(v.speech_key);
    if (variants != null && variants.size() > v.speech_variant) {
      String s = ((String)variants.get(v.speech_variant)).replaceAll("\\$name", playerName);
      if (v.getGoalDestEntity() != null && v.getGoalDestEntity() instanceof MillVillager) {
        s = s.replaceAll("\\$targetfirstname", v.dialogueTargetFirstName);
        s = s.replaceAll("\\$targetlastname", v.dialogueTargetLastName);
      } else {
        s = s.replaceAll("\\$targetfirstname", "");
        s = s.replaceAll("\\$targetlastname", "");
      } 
      if (!nativeSpeech) {
        if ((s.split("/")).length > 1) {
          s = s.split("/")[1].trim();
          if (s.length() == 0)
            s = null; 
          return s;
        } 
        return null;
      } 
      if ((s.split("/")).length > 1)
        s = s.split("/")[0].trim(); 
      if (s.length() == 0)
        s = null; 
      return s;
    } 
    return v.speech_key;
  }
}
