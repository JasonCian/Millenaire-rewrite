package org.millenaire.common.buildingplan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.millenaire.common.utilities.MillLog;

public abstract class SpecialPointTypeList {
  public static final String bempty = "empty";
  
  public static final String bpreserveground = "preserveground";
  
  public static final String ballbuttrees = "allbuttrees";
  
  public static final String bgrass = "grass";
  
  public static final String bsoil = "soil";
  
  public static final String bricesoil = "ricesoil";
  
  public static final String bturmericsoil = "turmericsoil";
  
  public static final String bmaizesoil = "maizesoil";
  
  public static final String bcarrotsoil = "carrotsoil";
  
  public static final String bpotatosoil = "potatosoil";
  
  public static final String bflowersoil = "flowersoil";
  
  public static final String bsugarcanesoil = "sugarcanesoil";
  
  public static final String bnetherwartsoil = "netherwartsoil";
  
  public static final String bvinesoil = "vinesoil";
  
  public static final String bcottonsoil = "cottonsoil";
  
  public static final String bsilkwormblock = "silkwormblock";
  
  public static final String bsnailsoilblock = "snailsoilblock";
  
  public static final String bcacaospot = "cacaospot";
  
  public static final String blockedchestGuess = "lockedchestGuess";
  
  public static final String blockedchestTop = "lockedchestTop";
  
  public static final String blockedchestBottom = "lockedchestBottom";
  
  public static final String blockedchestLeft = "lockedchestLeft";
  
  public static final String blockedchestRight = "lockedchestRight";
  
  public static final String bmainchestGuess = "mainchestGuess";
  
  public static final String bmainchestTop = "mainchestTop";
  
  public static final String bmainchestBottom = "mainchestBottom";
  
  public static final String bmainchestLeft = "mainchestLeft";
  
  public static final String bmainchestRight = "mainchestRight";
  
  public static final String bsleepingPos = "sleepingPos";
  
  public static final String bsellingPos = "sellingPos";
  
  public static final String bcraftingPos = "craftingPos";
  
  public static final String bdefendingPos = "defendingPos";
  
  public static final String bshelterPos = "shelterPos";
  
  public static final String bpathStartPos = "pathStartPos";
  
  public static final String bleasurePos = "leisurePos";
  
  public static final String btorchGuess = "torchGuess";
  
  public static final String bladderGuess = "ladderGuess";
  
  public static final String bsignwallGuess = "signwallGuess";
  
  public static final String bfreestone = "freestone";
  
  public static final String bfreesand = "freesand";
  
  public static final String bfreesandstone = "freesandstone";
  
  public static final String bfreegravel = "freegravel";
  
  public static final String bfreewool = "freewool";
  
  public static final String bfreecobblestone = "freecobblestone";
  
  public static final String bfreestonebrick = "freestonebrick";
  
  public static final String bfreepaintedbrick = "freepaintedbrick";
  
  public static final String bfreegrass_block = "freegrass_block";
  
  public static final String bstall = "stall";
  
  public static final String bfurnaceGuess = "furnaceGuess";
  
  public static final String boakspawn = "oakspawn";
  
  public static final String bpinespawn = "pinespawn";
  
  public static final String bbirchspawn = "birchspawn";
  
  public static final String bjunglespawn = "junglespawn";
  
  public static final String bdarkoakspawn = "darkoakspawn";
  
  public static final String bacaciaspawn = "acaciaspawn";
  
  public static final String bappletreespawn = "appletreespawn";
  
  public static final String bolivetreespawn = "olivetreespawn";
  
  public static final String bpistachiotreespawn = "pistachiotreespawn";
  
  public static final String bcherrytreespawn = "cherrytreespawn";
  
  public static final String bsakuratreespawn = "sakuratreespawn";
  
  public static final String bcowspawn = "cowspawn";
  
  public static final String bsheepspawn = "sheepspawn";
  
  public static final String bchickenspawn = "chickenspawn";
  
  public static final String bpigspawn = "pigspawn";
  
  public static final String bsquidspawn = "squidspawn";
  
  public static final String bwolfspawn = "wolfspawn";
  
  public static final String bpolarbearspawn = "polarbearspawn";
  
  public static final String bstonesource = "stonesource";
  
  public static final String bsandsource = "sandsource";
  
  public static final String bsandstonesource = "sandstonesource";
  
  public static final String bclaysource = "claysource";
  
  public static final String bgravelsource = "gravelsource";
  
  public static final String bgranitesource = "granitesource";
  
  public static final String bdioritesource = "dioritesource";
  
  public static final String bandesitesource = "andesitesource";
  
  public static final String bsnowsource = "snowsource";
  
  public static final String bicesource = "icesource";
  
  public static final String bredsandstonesource = "redsandstonesource";
  
  public static final String bquartzsource = "quartzsource";
  
  public static final String bbrickspot = "brickspot";
  
  public static final String btapestry = "tapestry";
  
  public static final String bindianstatue = "indianstatue";
  
  public static final String bmayanstatue = "mayanstatue";
  
  public static final String bbyzantineiconsmall = "byzantineiconsmall";
  
  public static final String bbyzantineiconmedium = "byzantineiconmedium";
  
  public static final String bbyzantineiconlarge = "byzantineiconlarge";
  
  public static final String bhidehanging = "hidehanging";
  
  public static final String bwallcarpetsmall = "wallcarpetsmall";
  
  public static final String bwallcarpetmedium = "wallcarpetmedium";
  
  public static final String bwallcarpetlarge = "wallcarpetlarge";
  
  public static final String bfishingspot = "fishingspot";
  
  public static final String bspawnerskeleton = "spawnerskeleton";
  
  public static final String bspawnerzombie = "spawnerzombie";
  
  public static final String bspawnerspider = "spawnerspider";
  
  public static final String bspawnercavespider = "spawnercavespider";
  
  public static final String bspawnercreeper = "spawnercreeper";
  
  public static final String bspawnerblaze = "spawnerblaze";
  
  public static final String bdispenserunknownpowder = "dispenserunknownpowder";
  
  public static final String bhealingspot = "healingspot";
  
  public static final String bplainSignGuess = "plainSignGuess";
  
  public static final String bbrewingstand = "brewingstand";
  
  public static final String bvillageBannerWallNorth = "villageBannerWallNorth";
  
  public static final String bvillageBannerWallEast = "villageBannerWallEast";
  
  public static final String bvillageBannerWallSouth = "villageBannerWallSouth";
  
  public static final String bvillageBannerWallWest = "villageBannerWallWest";
  
  public static final String bvillageBannerStanding0 = "villageBannerStanding0";
  
  public static final String bvillageBannerStanding1 = "villageBannerStanding1";
  
  public static final String bvillageBannerStanding2 = "villageBannerStanding2";
  
  public static final String bvillageBannerStanding3 = "villageBannerStanding3";
  
  public static final String bvillageBannerStanding4 = "villageBannerStanding4";
  
  public static final String bvillageBannerStanding5 = "villageBannerStanding5";
  
  public static final String bvillageBannerStanding6 = "villageBannerStanding6";
  
  public static final String bvillageBannerStanding7 = "villageBannerStanding7";
  
  public static final String bvillageBannerStanding8 = "villageBannerStanding8";
  
  public static final String bvillageBannerStanding9 = "villageBannerStanding9";
  
  public static final String bvillageBannerStanding10 = "villageBannerStanding10";
  
  public static final String bvillageBannerStanding11 = "villageBannerStanding11";
  
  public static final String bvillageBannerStanding12 = "villageBannerStanding12";
  
  public static final String bvillageBannerStanding13 = "villageBannerStanding13";
  
  public static final String bvillageBannerStanding14 = "villageBannerStanding14";
  
  public static final String bvillageBannerStanding15 = "villageBannerStanding15";
  
  public static final String bcultureBannerWallNorth = "cultureBannerWallNorth";
  
  public static final String bcultureBannerWallEast = "cultureBannerWallEast";
  
  public static final String bcultureBannerWallSouth = "cultureBannerWallSouth";
  
  public static final String bcultureBannerWallWest = "cultureBannerWallWest";
  
  public static final String bcultureBannerStanding0 = "cultureBannerStanding0";
  
  public static final String bcultureBannerStanding1 = "cultureBannerStanding1";
  
  public static final String bcultureBannerStanding2 = "cultureBannerStanding2";
  
  public static final String bcultureBannerStanding3 = "cultureBannerStanding3";
  
  public static final String bcultureBannerStanding4 = "cultureBannerStanding4";
  
  public static final String bcultureBannerStanding5 = "cultureBannerStanding5";
  
  public static final String bcultureBannerStanding6 = "cultureBannerStanding6";
  
  public static final String bcultureBannerStanding7 = "cultureBannerStanding7";
  
  public static final String bcultureBannerStanding8 = "cultureBannerStanding8";
  
  public static final String bcultureBannerStanding9 = "cultureBannerStanding9";
  
  public static final String bcultureBannerStanding10 = "cultureBannerStanding10";
  
  public static final String bcultureBannerStanding11 = "cultureBannerStanding11";
  
  public static final String bcultureBannerStanding12 = "cultureBannerStanding12";
  
  public static final String bcultureBannerStanding13 = "cultureBannerStanding13";
  
  public static final String bcultureBannerStanding14 = "cultureBannerStanding14";
  
  public static final String bcultureBannerStanding15 = "cultureBannerStanding15";
  
  private static final List<String> KNOW_POINT_TYPES = new ArrayList<>();
  
  public static boolean isSpecialPointTypeKnow(String specialType) {
    if (KNOW_POINT_TYPES.isEmpty())
      for (Field field : SpecialPointTypeList.class.getDeclaredFields()) {
        if (field.getType() == String.class)
          try {
            KNOW_POINT_TYPES.add((String)field.get(null));
          } catch (Exception e) {
            MillLog.printException("Exception will using reflection to register special point types:", e);
          }  
      }  
    return KNOW_POINT_TYPES.contains(specialType);
  }
}
