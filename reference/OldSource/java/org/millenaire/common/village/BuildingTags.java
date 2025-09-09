package org.millenaire.common.village;

import org.millenaire.common.config.DocumentedElement.Documentation;

public class BuildingTags {
  @Documentation("Makes the building's tags display the villagers' locations and activities.")
  public static final String TAG_ARCHIVES = "archives";
  
  @Documentation("Makes the building a valid target for cow-breeding.")
  public static final String TAG_CATTLE = "cattle";
  
  @Documentation("Makes the building a valid target for pig-breeding.")
  public static final String TAG_PIGS = "pigs";
  
  @Documentation("Makes the building a valid target for chicken-breeding.")
  public static final String TAG_CHICKEN = "chicken";
  
  @Documentation("Makes the building a valid target for sheep-breeding.")
  public static final String TAG_SHEEPS = "sheeps";
  
  @Documentation("Makes the building a valid target for the fishing goal.")
  public static final String TAG_FISHING_SPOT = "fishingspot";
  
  @Documentation("Makes the building a valid target for chopping trees and planting saplings, and activates accelerated tree growth.")
  public static final String TAG_GROVE = "grove";
  
  @Documentation("Makes the building into an Inn where local merchants can stay.")
  public static final String TAG_INN = "inn";
  
  @Documentation("Makes the building into a valid target for mud brick drying and gathering goals.")
  public static final String TAG_KILN = "brickkiln";
  
  @Documentation("Makes the building into a market that hosts foreign merchants.")
  public static final String TAG_MARKET = "market";
  
  @Documentation("Makes the building into a valid target for planting and harvesting sugar cane.")
  public static final String TAG_SUGAR_PLANTATION = "sugarplantation";
  
  @Documentation("Makes the building display the Hall of Fame on its signs.")
  public static final String TAG_HOF = "hof";
  
  @Documentation("Makes the building into a location where pujas can be held.")
  public static final String TAG_PUJAS = "pujas";
  
  @Documentation("Makes the building into a location where Maya sacrifices can be held.")
  public static final String TAG_SACRIFICES = "sacrifices";
  
  @Documentation("Makes the building into a valid location for gathering silk.")
  public static final String TAG_SILKWORM_FARM = "silkwormfarm";
  
  @Documentation("Makes the building into a valid location for gathering snails.")
  public static final String TAG_SNAILS_FARM = "snailsfarm";
  
  @Documentation("Makes the building despawn all mobs around it, not just creepers.")
  public static final String TAG_DESPAWN_ALL_MOBS = "despawnallmobs";
  
  @Documentation("Makes the building into a place where villagers go to chat.")
  public static final String TAG_LEASURE = "leasure";
  
  @Documentation("Stops the construction of paths within this building's limits.")
  public static final String TAG_NO_PATHS = "nopaths";
  
  @Documentation("Makes the building into a path node, where village paths converge.")
  public static final String TAG_PATH_NODE = "pathnode";
  
  @Documentation("Makes the building into a marvel, with specific panel displays")
  public static final String TAG_MARVEL = "marvel";
  
  @Documentation("Makes the building's inhabitants spawn after the first night.")
  public static final String TAG_AUTO_SPAWN_VILLAGERS = "autospawnvillagers";
  
  @Documentation("Requires builders to use scaffoldings to reach the proper height to lay blocks instead of doing it at a distance.")
  public static final String TAG_SCAFFOLDINGS = "scaffoldings";
  
  @Documentation("Enables the border post type sign.")
  public static final String TAG_BORDERPOSTSIGN = "borderpostsign";
  
  @Documentation("Stops this upgrade until all wall segments have been built.")
  public static final String TAG_NO_UPGRADE_TILL_WALL_INITIALIZED = "no_upgrade_till_wall_initialized";
  
  @Documentation("Wall level, for use in the wall panel. Use with a level added like this: wall_level_0, wall_level_1...")
  public static final String TAG_WALL_LEVEL = "wall_level";
}
