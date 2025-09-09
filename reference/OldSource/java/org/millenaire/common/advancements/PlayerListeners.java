package org.millenaire.common.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;

public class PlayerListeners {
  private final PlayerAdvancements playerAdvancements;
  
  private final Set<ICriterionTrigger.Listener<AlwaysTrueCriterionInstance>> listeners = Sets.newHashSet();
  
  public PlayerListeners(PlayerAdvancements playerAdvancementsIn) {
    this.playerAdvancements = playerAdvancementsIn;
  }
  
  public void add(ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
    this.listeners.add(listener);
  }
  
  public void grantAndNotify() {
    List<ICriterionTrigger.Listener<AlwaysTrueCriterionInstance>> list = null;
    for (ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener : this.listeners) {
      if (((AlwaysTrueCriterionInstance)listener.getCriterionInstance()).test()) {
        if (list == null)
          list = Lists.newArrayList(); 
        list.add(listener);
      } 
    } 
    if (list != null)
      for (ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener : list)
        listener.grantCriterion(this.playerAdvancements);  
  }
  
  public boolean isEmpty() {
    return this.listeners.isEmpty();
  }
  
  public void remove(ICriterionTrigger.Listener<AlwaysTrueCriterionInstance> listener) {
    this.listeners.remove(listener);
  }
}
