package org.millenaire.common.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.network.ServerSender;

public class GenericAdvancement implements ICriterionTrigger<AlwaysTrueCriterionInstance> {
  private final String key;
  
  private final ResourceLocation triggerRL;
  
  private final Map<PlayerAdvancements, PlayerListeners> playerListeners = new HashMap<>();
  
  public GenericAdvancement(String key) {
    this.key = key;
    this.triggerRL = new ResourceLocation(key);
  }
  
  public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
    PlayerListeners listeners = this.playerListeners.get(playerAdvancementsIn);
    if (listeners == null) {
      listeners = new PlayerListeners(playerAdvancementsIn);
      this.playerListeners.put(playerAdvancementsIn, listeners);
    } 
    listeners.add(listener);
  }
  
  public AlwaysTrueCriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
    return new AlwaysTrueCriterionInstance(getId());
  }
  
  public ResourceLocation getId() {
    return this.triggerRL;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public void grant(EntityPlayer player) {
    if (player instanceof EntityPlayerMP) {
      PlayerListeners playerListeners = this.playerListeners.get(((EntityPlayerMP)player).getAdvancements());
      if (playerListeners != null)
        playerListeners.grantAndNotify(); 
      ServerSender.sendAdvancementEarned((EntityPlayerMP)player, this.key);
    } 
    MillAdvancements.addToStats(player, this.key);
  }
  
  public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
    this.playerListeners.remove(playerAdvancementsIn);
  }
  
  public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
    PlayerListeners listeners = this.playerListeners.get(playerAdvancementsIn);
    if (listeners != null) {
      listeners.remove(listener);
      if (listeners.isEmpty())
        this.playerListeners.remove(playerAdvancementsIn); 
    } 
  }
}
