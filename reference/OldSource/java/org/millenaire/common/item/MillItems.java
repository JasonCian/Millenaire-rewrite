package org.millenaire.common.item;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.utilities.MillLog;

public class MillItems {
  public static final class MillItemNames {
    private static final String DENIER = "denier";
    
    private static final String DENIEROR = "denieror";
    
    private static final String DENIERARGENT = "denierargent";
    
    private static final String CALVA = "calva";
    
    private static final String TRIPES = "tripes";
    
    private static final String BOUDIN = "boudin";
    
    private static final String NORMANPICKAXE = "normanpickaxe";
    
    private static final String NORMANAXE = "normanaxe";
    
    private static final String NORMANSHOVEL = "normanshovel";
    
    private static final String NORMANHOE = "normanhoe";
    
    private static final String NORMANBROADSWORD = "normanbroadsword";
    
    private static final String NORMANHELMET = "normanhelmet";
    
    private static final String NORMANPLATE = "normanplate";
    
    private static final String NORMANLEGS = "normanlegs";
    
    private static final String NORMANBOOTS = "normanboots";
    
    private static final String RICE = "rice";
    
    private static final String TURMERIC = "turmeric";
    
    private static final String VEGCURRY = "vegcurry";
    
    private static final String CHICKENCURRY = "chickencurry";
    
    private static final String BRICKMOULD = "brickmould";
    
    private static final String RASGULLA = "rasgulla";
    
    private static final String INDIANSTATUE = "indianstatue";
    
    private static final String CIDERAPPLE = "ciderapple";
    
    private static final String OLIVES = "olives";
    
    public static final String CIDERAPPLE_PUBLIC = "ciderapple";
    
    public static final String OLIVES_PUBLIC = "olives";
    
    private static final String OLIVEOIL = "oliveoil";
    
    private static final String CIDER = "cider";
    
    private static final String SUMMONINGWAND = "summoningwand";
    
    private static final String NEGATIONWAND = "negationwand";
    
    private static final String NORMANVILLAGERS = "parchment_normanvillagers";
    
    private static final String NORMANITEMS = "parchment_normanitems";
    
    private static final String NORMANBUILDINGS = "parchment_normanbuildings";
    
    private static final String NORMANFULL = "parchment_normanfull";
    
    private static final String TAPESTRY = "tapestry";
    
    private static final String VISHNU_AMULET = "vishnu_amulet";
    
    private static final String ALCHEMIST_AMULET = "alchemist_amulet";
    
    private static final String YGGDRASIL_AMULET = "yggdrasil_amulet";
    
    private static final String SKOLL_HATI_AMULET = "skoll_hati_amulet";
    
    private static final String VILLAGESCROLL = "parchment_villagescroll";
    
    private static final String PAINT_BUCKET = "paint_bucket";
    
    private static final String INDIANVILLAGERS = "parchment_indianvillagers";
    
    private static final String INDIANITEMS = "parchment_indianitems";
    
    private static final String INDIANBUILDINGS = "parchment_indianbuildings";
    
    private static final String INDIANFULL = "parchment_indianfull";
    
    private static final String WAH = "wah";
    
    private static final String BLANCHE = "balche";
    
    private static final String SIKILPAH = "sikilpah";
    
    private static final String MASA = "masa";
    
    private static final String MAIZE = "maize";
    
    private static final String MAYANSTATUE = "mayanstatue";
    
    private static final String MAYANVILLAGERS = "parchment_mayanvillagers";
    
    private static final String MAYANITEMS = "parchment_mayanitems";
    
    private static final String MAYANBUILDINGS = "parchment_mayanbuildings";
    
    private static final String MAYANFULL = "parchment_mayanfull";
    
    private static final String MAYANMACE = "mayanmace";
    
    private static final String MAYANPICKAXE = "mayanpickaxe";
    
    private static final String MAYANAXE = "mayanaxe";
    
    private static final String MAYANSHOVEL = "mayanshovel";
    
    private static final String MAYANHOE = "mayanhoe";
    
    private static final String OBSIDIANFLAKE = "obsidianflake";
    
    private static final String CACAUHAA = "cacauhaa";
    
    private static final String UDON = "udon";
    
    private static final String TACHISWORD = "tachisword";
    
    private static final String YUMIBOW = "yumibow";
    
    private static final String SAKE = "sake";
    
    private static final String IKAYAKI = "ikayaki";
    
    private static final String JAPANESEBLUELEGS = "japanesebluelegs";
    
    private static final String JAPANESEBLUEHELMET = "japanesebluehelmet";
    
    private static final String JAPANESEBLUEPLATE = "japaneseblueplate";
    
    private static final String JAPANESEBLUEBOOTS = "japaneseblueboots";
    
    private static final String JAPANESEREDLEGS = "japaneseredlegs";
    
    private static final String JAPANESEREDHELMET = "japaneseredhelmet";
    
    private static final String JAPANESEREDPLATE = "japaneseredplate";
    
    private static final String JAPANESEREDBOOTS = "japaneseredboots";
    
    private static final String JAPANESEGUARDLEGS = "japaneseguardlegs";
    
    private static final String JAPANESEGUARDHELMET = "japaneseguardhelmet";
    
    private static final String JAPANESEGUARDPLATE = "japaneseguardplate";
    
    private static final String JAPANESEGUARDBOOTS = "japaneseguardboots";
    
    private static final String JAPANESEVILLAGERS = "parchment_japanesevillagers";
    
    private static final String JAPANESEITEMS = "parchment_japaneseitems";
    
    private static final String JAPANESEBUILDINGS = "parchment_japanesebuildings";
    
    private static final String JAPANESEFULL = "parchment_japanesefull";
    
    private static final String PARCHMENTSADHU = "parchment_sadhu";
    
    private static final String UNKNOWNPOWDER = "unknownpowder";
    
    private static final String MAYANQUESTCROWN = "mayanquestcrown";
    
    private static final String GRAPES = "grapes";
    
    private static final String WINEFANCY = "winefancy";
    
    private static final String SILK = "silk";
    
    private static final String BYZANTINEICONSMALL = "byzantineiconsmall";
    
    private static final String BYZANTINEICONMEDIUM = "byzantineiconmedium";
    
    private static final String BYZANTINEICONLARGE = "byzantineiconlarge";
    
    private static final String BYZANTINEBOOTS = "byzantineboots";
    
    private static final String BYZANTINELEGS = "byzantinelegs";
    
    private static final String BYZANTINEPLATE = "byzantineplate";
    
    private static final String BYZANTINEHELMET = "byzantinehelmet";
    
    private static final String BYZANTINEMACE = "byzantinemace";
    
    private static final String BYZANTINEPICKAXE = "byzantinepickaxe";
    
    private static final String BYZANTINEAXE = "byzantineaxe";
    
    private static final String BYZANTINESHOVEL = "byzantineshovel";
    
    private static final String BYZANTINEHOE = "byzantinehoe";
    
    private static final String CLOTHES_BYZ_WOOL = "clothes_byz_wool";
    
    private static final String CLOTHES_BYZ_SILK = "clothes_byz_silk";
    
    private static final String FETA = "feta";
    
    private static final String WINEBASIC = "winebasic";
    
    private static final String SOUVLAKI = "souvlaki";
    
    private static final String PURSE = "purse";
    
    private static final String BEARMEAT_RAW = "bearmeat_raw";
    
    private static final String BEARMEAT_COOKED = "bearmeat_cooked";
    
    private static final String WOLFMEAT_RAW = "wolfmeat_raw";
    
    private static final String WOLFMEAT_COOKED = "wolfmeat_cooked";
    
    private static final String SEAFOOD_RAW = "seafood_raw";
    
    private static final String SEAFOOD_COOKED = "seafood_cooked";
    
    private static final String INUITBEARSTEW = "inuitbearstew";
    
    private static final String INUITMEATYSTEW = "inuitmeatystew";
    
    private static final String INUITPOTATOSTEW = "inuitpotatostew";
    
    private static final String INUIT_TRIDENT = "inuittrident";
    
    private static final String FURHELMET = "furhelmet";
    
    private static final String FURPLATE = "furplate";
    
    private static final String FURLEGS = "furlegs";
    
    private static final String FURBOOTS = "furboots";
    
    private static final String INUIT_BOW = "inuitbow";
    
    private static final String ULU = "ulu";
    
    private static final String TANNEDHIDE = "tannedhide";
    
    private static final String HIDEHANGING = "hidehanging";
    
    private static final String VILLAGEBANNER = "villagebanner";
    
    private static final String CULTUREBANNER = "culturebanner";
    
    public static final String VILLAGEBANNER_PUBLIC = "villagebanner";
    
    public static final String CULTUREBANNER_PUBLIC = "culturebanner";
    
    public static final String BANNERPATTERN = "bannerpattern";
    
    private static final String AYRAN = "ayran";
    
    private static final String YOGURT = "yogurt";
    
    private static final String PIDE = "pide";
    
    private static final String LOKUM = "lokum";
    
    private static final String HELVA = "helva";
    
    private static final String COTTON = "cotton";
    
    private static final String PISTACHIOS = "pistachios";
    
    public static final String PISTACHIOS_PUBLIC = "pistachios";
    
    private static final String SELJUK_SCIMITAR = "seljukscimitar";
    
    private static final String SELJUK_BOW = "seljukbow";
    
    private static final String SELJUK_BOOTS = "seljukboots";
    
    private static final String SELJUK_LEGGINGS = "seljuklegs";
    
    private static final String SELJUK_CHESTPLATE = "seljukplate";
    
    private static final String SELJUK_HELMET = "seljukhelmet";
    
    private static final String SELJUK_TURBAN = "seljukturban";
    
    private static final String WALLCARPETSMALL = "wallcarpetsmall";
    
    private static final String WALLCARPETMEDIUM = "wallcarpetmedium";
    
    private static final String WALLCARPETLARGE = "wallcarpetlarge";
    
    private static final String CLOTHES_SELJUK_WOOL = "clothes_seljuk_wool";
    
    private static final String CLOTHES_SELJUK_COTTON = "clothes_seljuk_cotton";
    
    private static final String CHERRIES = "cherries";
    
    public static final String CHERRIES_PUBLIC = "cherries";
    
    private static final String CHERRY_BLOSSOM = "cherry_blossom";
    
    public static final String CHERRY_BLOSSOM_PUBLIC = "cherry_blossom";
  }
  
  static Item.ToolMaterial TOOLS_norman = EnumHelper.addToolMaterial("normanTools", 2, 1561, 10.0F, 4.0F, 10);
  
  static Item.ToolMaterial better_steel = EnumHelper.addToolMaterial("bettersteel", 2, 1561, 5.0F, 3.0F, 10);
  
  static Item.ToolMaterial TOOLS_byzantine = EnumHelper.addToolMaterial("byzantineTools", 2, 1561, 12.0F, 3.0F, 15);
  
  static Item.ToolMaterial TOOLS_obsidian = EnumHelper.addToolMaterial("obsidianTools", 3, 1561, 6.0F, 2.0F, 25);
  
  static ItemArmor.ArmorMaterial ARMOUR_norman = EnumHelper.addArmorMaterial("normanArmour", "millenaire:norman", 66, new int[] { 3, 8, 6, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_japanese_red = EnumHelper.addArmorMaterial("japanese", "millenaire:japanese_red", 33, new int[] { 2, 6, 5, 2 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_japanese_blue = EnumHelper.addArmorMaterial("japanese", "millenaire:japanese_blue", 33, new int[] { 2, 6, 5, 2 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_japaneseGuard = EnumHelper.addArmorMaterial("japaneseGuard", "millenaire:japanese_guard", 25, new int[] { 2, 5, 4, 1 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_byzantine = EnumHelper.addArmorMaterial("byzantineArmour", "millenaire:byzantine", 33, new int[] { 3, 8, 6, 3 }, 20, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_mayan_quest_crown = EnumHelper.addArmorMaterial("mayanQuestCrown", "millenaire:mayan_quest_crown", 33, new int[] { 3, 6, 8, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_SELJUK = EnumHelper.addArmorMaterial("seljukArmour", "millenaire:seljuk", 66, new int[] { 3, 8, 6, 3 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_SELJUK_WOOL = EnumHelper.addArmorMaterial("seljukWoolArmour", "millenaire:seljuk_wool", 7, new int[] { 2, 5, 3, 1 }, 10, SoundEvents.BLOCK_CLOTH_PLACE, 0.0F);
  
  static ItemArmor.ArmorMaterial ARMOUR_fur = EnumHelper.addArmorMaterial("fur", "millenaire:furcoat", 7, new int[] { 2, 5, 3, 1 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
  
  @ObjectHolder("millenaire:summoningwand")
  public static ItemSummoningWand SUMMONING_WAND;
  
  @ObjectHolder("millenaire:negationwand")
  public static ItemNegationWand NEGATION_WAND;
  
  @ObjectHolder("millenaire:purse")
  public static ItemPurse PURSE;
  
  @ObjectHolder("millenaire:denier")
  public static ItemMill DENIER;
  
  @ObjectHolder("millenaire:denierargent")
  public static ItemMill DENIER_ARGENT;
  
  @ObjectHolder("millenaire:denieror")
  public static ItemMill DENIER_OR;
  
  @ObjectHolder("millenaire:ciderapple")
  public static ItemFoodMultiple CIDER_APPLE;
  
  @ObjectHolder("millenaire:cider")
  public static ItemFoodMultiple CIDER;
  
  @ObjectHolder("millenaire:calva")
  public static ItemFoodMultiple CALVA;
  
  @ObjectHolder("millenaire:boudin")
  public static ItemFoodMultiple BOUDIN;
  
  @ObjectHolder("millenaire:tripes")
  public static ItemFoodMultiple TRIPES;
  
  @ObjectHolder("millenaire:normanhoe")
  public static ItemMillenaireHoe NORMAN_HOE;
  
  @ObjectHolder("millenaire:normanaxe")
  public static ItemMillenaireAxe NORMAN_AXE;
  
  @ObjectHolder("millenaire:normanpickaxe")
  public static ItemMillenairePickaxe NORMAN_PICKAXE;
  
  @ObjectHolder("millenaire:normanshovel")
  public static ItemMillenaireShovel NORMAN_SHOVEL;
  
  @ObjectHolder("millenaire:normanbroadsword")
  public static ItemMillenaireSword NORMAN_SWORD;
  
  @ObjectHolder("millenaire:normanhelmet")
  public static ItemMillenaireArmour NORMAN_HELMET;
  
  @ObjectHolder("millenaire:normanplate")
  public static ItemMillenaireArmour NORMAN_CHESTPLATE;
  
  @ObjectHolder("millenaire:normanlegs")
  public static ItemMillenaireArmour NORMAN_LEGGINGS;
  
  @ObjectHolder("millenaire:normanboots")
  public static ItemMillenaireArmour NORMAN_BOOTS;
  
  @ObjectHolder("millenaire:tapestry")
  public static ItemWallDecoration TAPESTRY;
  
  @ObjectHolder("millenaire:mayanhoe")
  public static ItemMillenaireHoe MAYAN_HOE;
  
  @ObjectHolder("millenaire:mayanaxe")
  public static ItemMillenaireAxe MAYAN_AXE;
  
  @ObjectHolder("millenaire:mayanpickaxe")
  public static ItemMillenairePickaxe MAYAN_PICKAXE;
  
  @ObjectHolder("millenaire:mayanshovel")
  public static ItemMillenaireShovel MAYAN_SHOVEL;
  
  @ObjectHolder("millenaire:mayanmace")
  public static ItemMillenaireSword MAYAN_MACE;
  
  @ObjectHolder("millenaire:byzantinehoe")
  public static ItemMillenaireHoe BYZANTINE_HOE;
  
  @ObjectHolder("millenaire:byzantineaxe")
  public static ItemMillenaireAxe BYZANTINE_AXE;
  
  @ObjectHolder("millenaire:byzantinepickaxe")
  public static ItemMillenairePickaxe BYZANTINE_PICKAXE;
  
  @ObjectHolder("millenaire:byzantineshovel")
  public static ItemMillenaireShovel BYZANTINE_SHOVEL;
  
  @ObjectHolder("millenaire:byzantinemace")
  public static ItemMillenaireSword BYZANTINE_MACE;
  
  @ObjectHolder("millenaire:yumibow")
  public static ItemMillenaireBow YUMI_BOW;
  
  @ObjectHolder("millenaire:tachisword")
  public static ItemMillenaireSword TACHI_SWORD;
  
  @ObjectHolder("millenaire:olives")
  public static ItemFoodMultiple OLIVES;
  
  @ObjectHolder("millenaire:oliveoil")
  public static ItemFoodMultiple OLIVEOIL;
  
  @ObjectHolder("millenaire:byzantinehelmet")
  public static ItemMillenaireArmour BYZANTINE_HELMET;
  
  @ObjectHolder("millenaire:byzantineplate")
  public static ItemMillenaireArmour BYZANTINE_CHESTPLATE;
  
  @ObjectHolder("millenaire:byzantinelegs")
  public static ItemMillenaireArmour BYZANTINE_LEGGINGS;
  
  @ObjectHolder("millenaire:byzantineboots")
  public static ItemMillenaireArmour BYZANTINE_BOOTS;
  
  @ObjectHolder("millenaire:japaneseredhelmet")
  public static ItemMillenaireArmour JAPANESE_RED_HELMET;
  
  @ObjectHolder("millenaire:japaneseredplate")
  public static ItemMillenaireArmour JAPANESE_RED_CHESTPLATE;
  
  @ObjectHolder("millenaire:japaneseredlegs")
  public static ItemMillenaireArmour JAPANESE_RED_LEGGINGS;
  
  @ObjectHolder("millenaire:japaneseredboots")
  public static ItemMillenaireArmour JAPANESE_RED_BOOTS;
  
  @ObjectHolder("millenaire:japanesebluehelmet")
  public static ItemMillenaireArmour JAPANESE_BLUE_HELMET;
  
  @ObjectHolder("millenaire:japaneseblueplate")
  public static ItemMillenaireArmour JAPANESE_BLUE_CHESTPLATE;
  
  @ObjectHolder("millenaire:japanesebluelegs")
  public static ItemMillenaireArmour JAPANESE_BLUE_LEGGINGS;
  
  @ObjectHolder("millenaire:japaneseblueboots")
  public static ItemMillenaireArmour JAPANESE_BLUE_BOOTS;
  
  @ObjectHolder("millenaire:japaneseguardhelmet")
  public static ItemMillenaireArmour JAPANESE_GUARD_HELMET;
  
  @ObjectHolder("millenaire:japaneseguardplate")
  public static ItemMillenaireArmour JAPANESE_GUARD_CHESTPLATE;
  
  @ObjectHolder("millenaire:japaneseguardlegs")
  public static ItemMillenaireArmour JAPANESE_GUARD_LEGGINGS;
  
  @ObjectHolder("millenaire:japaneseguardboots")
  public static ItemMillenaireArmour JAPANESE_GUARD_BOOTS;
  
  @ObjectHolder("millenaire:parchment_normanvillagers")
  public static ItemParchment PARCHMENT_NORMAN_VILLAGERS;
  
  @ObjectHolder("millenaire:parchment_normanbuildings")
  public static ItemParchment PARCHMENT_NORMAN_BUILDINGS;
  
  @ObjectHolder("millenaire:parchment_normanitems")
  public static ItemParchment PARCHMENT_NORMAN_ITEMS;
  
  @ObjectHolder("millenaire:parchment_normanfull")
  public static ItemParchment PARCHMENT_NORMAN_COMPLETE;
  
  @ObjectHolder("millenaire:parchment_indianvillagers")
  public static ItemParchment PARCHMENT_INDIAN_VILLAGERS;
  
  @ObjectHolder("millenaire:parchment_indianbuildings")
  public static ItemParchment PARCHMENT_INDIAN_BUILDINGS;
  
  @ObjectHolder("millenaire:parchment_indianitems")
  public static ItemParchment PARCHMENT_INDIAN_ITEMS;
  
  @ObjectHolder("millenaire:parchment_indianfull")
  public static ItemParchment PARCHMENT_INDIAN_COMPLETE;
  
  @ObjectHolder("millenaire:parchment_japanesevillagers")
  public static ItemParchment PARCHMENT_JAPANESE_VILLAGERS;
  
  @ObjectHolder("millenaire:parchment_japanesebuildings")
  public static ItemParchment PARCHMENT_JAPANESE_BUILDINGS;
  
  @ObjectHolder("millenaire:parchment_japaneseitems")
  public static ItemParchment PARCHMENT_JAPANESE_ITEMS;
  
  @ObjectHolder("millenaire:parchment_japanesefull")
  public static ItemParchment PARCHMENT_JAPANESE_COMPLETE;
  
  @ObjectHolder("millenaire:parchment_mayanvillagers")
  public static ItemParchment PARCHMENT_MAYAN_VILLAGERS;
  
  @ObjectHolder("millenaire:parchment_mayanbuildings")
  public static ItemParchment PARCHMENT_MAYAN_BUILDINGS;
  
  @ObjectHolder("millenaire:parchment_mayanitems")
  public static ItemParchment PARCHMENT_MAYAN_ITEMS;
  
  @ObjectHolder("millenaire:parchment_mayanfull")
  public static ItemParchment PARCHMENT_MAYAN_COMPLETE;
  
  @ObjectHolder("millenaire:vishnu_amulet")
  public static ItemMill AMULET_VISHNU;
  
  @ObjectHolder("millenaire:alchemist_amulet")
  public static ItemMill AMULET_ALCHEMIST;
  
  @ObjectHolder("millenaire:yggdrasil_amulet")
  public static ItemMill AMULET_YDDRASIL;
  
  @ObjectHolder("millenaire:skoll_hati_amulet")
  public static ItemMill AMULET_SKOLL_HATI;
  
  @ObjectHolder("millenaire:parchment_villagescroll")
  public static ItemParchment PARCHMENT_VILLAGE_SCROLL;
  
  @ObjectHolder("millenaire:rice")
  public static ItemMillSeeds RICE;
  
  @ObjectHolder("millenaire:turmeric")
  public static ItemMillSeeds TURMERIC;
  
  @ObjectHolder("millenaire:maize")
  public static ItemMillSeeds MAIZE;
  
  @ObjectHolder("millenaire:grapes")
  public static ItemMillSeeds GRAPES;
  
  @ObjectHolder("millenaire:vegcurry")
  public static ItemFoodMultiple VEG_CURRY;
  
  @ObjectHolder("millenaire:chickencurry")
  public static ItemFoodMultiple CHICKEN_CURRY;
  
  @ObjectHolder("millenaire:brickmould")
  public static ItemMill BRICK_MOULD;
  
  @ObjectHolder("millenaire:rasgulla")
  public static ItemFoodMultiple RASGULLA;
  
  @ObjectHolder("millenaire:indianstatue")
  public static ItemWallDecoration INDIAN_STATUE;
  
  @ObjectHolder("millenaire:mayanstatue")
  public static ItemWallDecoration MAYAN_STATUE;
  
  @ObjectHolder("millenaire:wah")
  public static ItemFoodMultiple WAH;
  
  @ObjectHolder("millenaire:balche")
  public static ItemFoodMultiple BLANCHE;
  
  @ObjectHolder("millenaire:sikilpah")
  public static ItemFoodMultiple SIKILPAH;
  
  @ObjectHolder("millenaire:masa")
  public static ItemFoodMultiple MASA;
  
  @ObjectHolder("millenaire:parchment_sadhu")
  public static ItemParchment PARCHMENT_SADHU;
  
  @ObjectHolder("millenaire:unknownpowder")
  public static ItemMill UNKNOWN_POWDER;
  
  @ObjectHolder("millenaire:udon")
  public static ItemFoodMultiple UDON;
  
  @ObjectHolder("millenaire:obsidianflake")
  public static ItemMill OBSIDIAN_FLAKE;
  
  @ObjectHolder("millenaire:silk")
  public static ItemMill SILK;
  
  @ObjectHolder("millenaire:byzantineiconsmall")
  public static ItemWallDecoration BYZANTINE_ICON_SMALL;
  
  @ObjectHolder("millenaire:byzantineiconmedium")
  public static ItemWallDecoration BYZANTINE_ICON_MEDIUM;
  
  @ObjectHolder("millenaire:byzantineiconlarge")
  public static ItemWallDecoration BYZANTINE_ICON_LARGE;
  
  @ObjectHolder("millenaire:clothes_byz_wool")
  public static ItemClothes BYZANTINE_CLOTH_WOOL;
  
  @ObjectHolder("millenaire:clothes_byz_silk")
  public static ItemClothes BYZANTINE_CLOTH_SILK;
  
  @ObjectHolder("millenaire:winefancy")
  public static ItemFoodMultiple WINE_FANCY;
  
  @ObjectHolder("millenaire:winebasic")
  public static ItemFoodMultiple WINE_BASIC;
  
  @ObjectHolder("millenaire:feta")
  public static ItemFoodMultiple FETA;
  
  @ObjectHolder("millenaire:souvlaki")
  public static ItemFoodMultiple SOUVLAKI;
  
  @ObjectHolder("millenaire:sake")
  public static ItemFoodMultiple SAKE;
  
  @ObjectHolder("millenaire:cacauhaa")
  public static ItemFoodMultiple CACAUHAA;
  
  @ObjectHolder("millenaire:mayanquestcrown")
  public static ItemMayanQuestCrown MAYAN_QUEST_CROWN;
  
  @ObjectHolder("millenaire:ikayaki")
  public static ItemFoodMultiple IKAYAKI;
  
  @ObjectHolder("millenaire:bearmeat_raw")
  public static ItemFoodMultiple BEARMEAT_RAW;
  
  @ObjectHolder("millenaire:bearmeat_cooked")
  public static ItemFoodMultiple BEARMEAT_COOKED;
  
  @ObjectHolder("millenaire:wolfmeat_raw")
  public static ItemFoodMultiple WOLFMEAT_RAW;
  
  @ObjectHolder("millenaire:wolfmeat_cooked")
  public static ItemFoodMultiple WOLFMEAT_COOKED;
  
  @ObjectHolder("millenaire:seafood_raw")
  public static ItemFoodMultiple SEAFOOD_RAW;
  
  @ObjectHolder("millenaire:seafood_cooked")
  public static ItemFoodMultiple SEAFOOD_COOKED;
  
  @ObjectHolder("millenaire:inuitbearstew")
  public static ItemFoodMultiple INUITBEARSTEW;
  
  @ObjectHolder("millenaire:inuitmeatystew")
  public static ItemFoodMultiple INUITMEATYSTEW;
  
  @ObjectHolder("millenaire:inuitpotatostew")
  public static ItemFoodMultiple INUITPOTATOSTEW;
  
  @ObjectHolder("millenaire:furhelmet")
  public static ItemMillenaireArmour FUR_HELMET;
  
  @ObjectHolder("millenaire:furplate")
  public static ItemMillenaireArmour FUR_CHESTPLATE;
  
  @ObjectHolder("millenaire:furlegs")
  public static ItemMillenaireArmour FUR_LEGGINGS;
  
  @ObjectHolder("millenaire:furboots")
  public static ItemMillenaireArmour FUR_BOOTS;
  
  @ObjectHolder("millenaire:inuitbow")
  public static ItemMillenaireBow INUIT_BOW;
  
  @ObjectHolder("millenaire:ulu")
  public static ItemMill ULU;
  
  @ObjectHolder("millenaire:inuittrident")
  public static ItemMillenaireSword INUIT_TRIDENT;
  
  @ObjectHolder("millenaire:tannedhide")
  public static ItemMill TANNEDHIDE;
  
  @ObjectHolder("millenaire:hidehanging")
  public static ItemWallDecoration HIDEHANGING;
  
  @ObjectHolder("millenaire:villagebanner")
  public static ItemMockBanner VILLAGEBANNER;
  
  @ObjectHolder("millenaire:culturebanner")
  public static ItemMockBanner CULTUREBANNER;
  
  @ObjectHolder("millenaire:bannerpattern")
  public static ItemBannerPattern BANNERPATTERN;
  
  @ObjectHolder("millenaire:ayran")
  public static ItemFoodMultiple AYRAN;
  
  @ObjectHolder("millenaire:yogurt")
  public static ItemFoodMultiple YOGURT;
  
  @ObjectHolder("millenaire:pide")
  public static ItemFoodMultiple PIDE;
  
  @ObjectHolder("millenaire:lokum")
  public static ItemFoodMultiple LOKUM;
  
  @ObjectHolder("millenaire:helva")
  public static ItemFoodMultiple HELVA;
  
  @ObjectHolder("millenaire:pistachios")
  public static ItemFoodMultiple PISTACHIOS;
  
  @ObjectHolder("millenaire:cotton")
  public static ItemMillSeeds COTTON;
  
  @ObjectHolder("millenaire:seljukscimitar")
  public static ItemMillenaireSword SELJUK_SCIMITAR;
  
  @ObjectHolder("millenaire:seljukbow")
  public static ItemMillenaireBow SElJUK_BOW;
  
  @ObjectHolder("millenaire:seljukturban")
  public static ItemMillenaireArmour SELJUK_TURBAN;
  
  @ObjectHolder("millenaire:seljukhelmet")
  public static ItemMillenaireArmour SELJUK_HELMET;
  
  @ObjectHolder("millenaire:seljukplate")
  public static ItemMillenaireArmour SELJUK_CHESTPLATE;
  
  @ObjectHolder("millenaire:seljuklegs")
  public static ItemMillenaireArmour SELJUK_LEGGINGS;
  
  @ObjectHolder("millenaire:seljukboots")
  public static ItemMillenaireArmour SELJUK_BOOTS;
  
  @ObjectHolder("millenaire:wallcarpetsmall")
  public static ItemWallDecoration WALLCARPETSMALL;
  
  @ObjectHolder("millenaire:wallcarpetmedium")
  public static ItemWallDecoration WALLCARPETMEDIUM;
  
  @ObjectHolder("millenaire:wallcarpetlarge")
  public static ItemWallDecoration WALLCARPETLARGE;
  
  @ObjectHolder("millenaire:clothes_seljuk_wool")
  public static ItemClothes SELJUK_CLOTH_WOOL;
  
  @ObjectHolder("millenaire:clothes_seljuk_cotton")
  public static ItemClothes SELJUK_CLOTH_COTTON;
  
  public static Map<EnumDyeColor, ItemPaintBucket> PAINT_BUCKETS = new HashMap<>();
  
  @ObjectHolder("millenaire:cherries")
  public static ItemFoodMultiple CHERRIES;
  
  @ObjectHolder("millenaire:cherry_blossom")
  public static ItemFoodMultiple CHERRY_BLOSSOM;
  
  @SideOnly(Side.CLIENT)
  public static void registerItemModels() {
    try {
      Field[] fields = MillItems.class.getFields();
      for (Field f : fields) {
        if (ItemMill.class.isAssignableFrom(f.getType())) {
          ItemMill item = (ItemMill)f.get(null);
          item.initModel();
        } else if (ItemMillenaireArmour.class.isAssignableFrom(f.getType())) {
          ItemMillenaireArmour item = (ItemMillenaireArmour)f.get(null);
          item.initModel();
        } else if (ItemMillenaireAxe.class.isAssignableFrom(f.getType())) {
          ItemMillenaireAxe item = (ItemMillenaireAxe)f.get(null);
          item.initModel();
        } else if (ItemMillenaireHoe.class.isAssignableFrom(f.getType())) {
          ItemMillenaireHoe item = (ItemMillenaireHoe)f.get(null);
          item.initModel();
        } else if (ItemMillenairePickaxe.class.isAssignableFrom(f.getType())) {
          ItemMillenairePickaxe item = (ItemMillenairePickaxe)f.get(null);
          item.initModel();
        } else if (ItemMillenaireShovel.class.isAssignableFrom(f.getType())) {
          ItemMillenaireShovel item = (ItemMillenaireShovel)f.get(null);
          item.initModel();
        } else if (ItemMillenaireSword.class.isAssignableFrom(f.getType())) {
          ItemMillenaireSword item = (ItemMillenaireSword)f.get(null);
          item.initModel();
        } else if (ItemFoodMultiple.class.isAssignableFrom(f.getType())) {
          ItemFoodMultiple item = (ItemFoodMultiple)f.get(null);
          item.initModel();
        } else if (ItemMillenaireBow.class.isAssignableFrom(f.getType())) {
          ItemMillenaireBow item = (ItemMillenaireBow)f.get(null);
          item.initModel();
        } 
      } 
    } catch (IllegalArgumentException e) {
      MillLog.printException("Error, illegal argument, while initialising item models", e);
    } catch (IllegalAccessException e) {
      MillLog.printException("Error, illegal access, while initialising item models", e);
    } 
    MAYAN_QUEST_CROWN.initModel();
    VILLAGEBANNER.initModel();
    CULTUREBANNER.initModel();
    for (ItemPaintBucket bucket : PAINT_BUCKETS.values())
      bucket.initModel(); 
  }
  
  public static void registerItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().register((new ItemSummoningWand("summoningwand")).setFull3D());
    event.getRegistry().register((new ItemNegationWand("negationwand")).setFull3D());
    event.getRegistry().register((new ItemMill("denier")).setFull3D());
    event.getRegistry().register((new ItemMill("denierargent")).setFull3D());
    event.getRegistry().register((new ItemMill("denieror")).setFull3D());
    event.getRegistry().register((new ItemPurse("purse")).setFull3D());
    event.getRegistry().register(new ItemMillSeeds((Block)MillBlocks.CROP_RICE, "rice"));
    event.getRegistry().register(new ItemMillSeeds((Block)MillBlocks.CROP_TURMERIC, "turmeric"));
    event.getRegistry().register(new ItemMillSeeds((Block)MillBlocks.CROP_MAIZE, "maize"));
    event.getRegistry().register(new ItemMillSeeds((Block)MillBlocks.CROP_VINE, "grapes"));
    event.getRegistry().register(new ItemMillSeeds((Block)MillBlocks.CROP_COTTON, "cotton"));
    event.getRegistry().register(new ItemMillenairePickaxe("normanpickaxe", TOOLS_norman));
    event.getRegistry().register(new ItemMillenaireAxe("normanaxe", TOOLS_norman, 8.0F, -3.0F));
    event.getRegistry().register(new ItemMillenaireShovel("normanshovel", TOOLS_norman));
    event.getRegistry().register(new ItemMillenaireHoe("normanhoe", TOOLS_norman));
    event.getRegistry().register(new ItemMillenairePickaxe("mayanpickaxe", TOOLS_obsidian));
    event.getRegistry().register(new ItemMillenaireAxe("mayanaxe", TOOLS_obsidian, 8.0F, -3.0F));
    event.getRegistry().register(new ItemMillenaireShovel("mayanshovel", TOOLS_obsidian));
    event.getRegistry().register(new ItemMillenaireHoe("mayanhoe", TOOLS_obsidian));
    event.getRegistry().register(new ItemMillenairePickaxe("byzantinepickaxe", TOOLS_byzantine));
    event.getRegistry().register(new ItemMillenaireAxe("byzantineaxe", TOOLS_byzantine, 8.0F, -3.0F));
    event.getRegistry().register(new ItemMillenaireShovel("byzantineshovel", TOOLS_byzantine));
    event.getRegistry().register(new ItemMillenaireHoe("byzantinehoe", TOOLS_byzantine));
    event.getRegistry().register((new ItemMillenaireSword("normanbroadsword", TOOLS_norman, -1, false)).setFull3D());
    event.getRegistry().register((new ItemMillenaireSword("mayanmace", Item.ToolMaterial.IRON, -1, false)).setFull3D());
    event.getRegistry().register((new ItemMillenaireSword("tachisword", TOOLS_obsidian, -1, false)).setFull3D());
    event.getRegistry().register((new ItemMillenaireSword("byzantinemace", Item.ToolMaterial.IRON, -1, true)).setFull3D());
    event.getRegistry().register((new ItemMillenaireSword("inuittrident", Item.ToolMaterial.IRON, 20, false)).setFull3D());
    event.getRegistry().register((new ItemMillenaireSword("seljukscimitar", better_steel, -1, false)).setFull3D());
    event.getRegistry().register((new ItemMillenaireBow("yumibow", 2.0F, 0.5F, 1)).setFull3D());
    event.getRegistry().register((new ItemMillenaireBow("inuitbow", 1.0F, 0.0F, 20)).setFull3D());
    event.getRegistry().register((new ItemMillenaireBow("seljukbow", 1.5F, 1.5F, 20)).setFull3D());
    event.getRegistry().register(new ItemMillenaireArmour("normanhelmet", ARMOUR_norman, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("normanplate", ARMOUR_norman, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("normanlegs", ARMOUR_norman, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("normanboots", ARMOUR_norman, EntityEquipmentSlot.FEET));
    event.getRegistry().register(new ItemMillenaireArmour("japanesebluehelmet", ARMOUR_japanese_blue, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseblueplate", ARMOUR_japanese_blue, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("japanesebluelegs", ARMOUR_japanese_blue, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseblueboots", ARMOUR_japanese_blue, EntityEquipmentSlot.FEET));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseredhelmet", ARMOUR_japanese_red, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseredplate", ARMOUR_japanese_red, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseredlegs", ARMOUR_japanese_red, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseredboots", ARMOUR_japanese_red, EntityEquipmentSlot.FEET));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseguardhelmet", ARMOUR_japaneseGuard, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseguardplate", ARMOUR_japaneseGuard, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseguardlegs", ARMOUR_japaneseGuard, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("japaneseguardboots", ARMOUR_japaneseGuard, EntityEquipmentSlot.FEET));
    event.getRegistry().register(new ItemMillenaireArmour("byzantinehelmet", ARMOUR_byzantine, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("byzantineplate", ARMOUR_byzantine, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("byzantinelegs", ARMOUR_byzantine, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("byzantineboots", ARMOUR_byzantine, EntityEquipmentSlot.FEET));
    event.getRegistry().register(new ItemMillenaireArmour("furhelmet", ARMOUR_fur, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("furplate", ARMOUR_fur, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("furlegs", ARMOUR_fur, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("furboots", ARMOUR_fur, EntityEquipmentSlot.FEET));
    event.getRegistry().register(new ItemMillenaireArmour("seljukhelmet", ARMOUR_SELJUK, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("seljukturban", ARMOUR_SELJUK_WOOL, EntityEquipmentSlot.HEAD));
    event.getRegistry().register(new ItemMillenaireArmour("seljukplate", ARMOUR_SELJUK, EntityEquipmentSlot.CHEST));
    event.getRegistry().register(new ItemMillenaireArmour("seljuklegs", ARMOUR_SELJUK, EntityEquipmentSlot.LEGS));
    event.getRegistry().register(new ItemMillenaireArmour("seljukboots", ARMOUR_SELJUK, EntityEquipmentSlot.FEET));
    event.getRegistry().register((new ItemFoodMultiple("ciderapple", 0, 0, 1, 0.05F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("olives", 0, 0, 1, 0.05F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("oliveoil", 0, 0, 0, 0.0F, true, 0)).setClearEffects(true).setMaxStackSize(16));
    event.getRegistry().register((new ItemFoodMultiple("cider", 4, 15, 0, 0.0F, true, 5)).setAlwaysEdible().setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("calva", 8, 30, 0, 0.0F, true, 10)).setAlwaysEdible().setMaxDamage(1024));
    event.getRegistry().register((new ItemFoodMultiple("boudin", 0, 0, 8, 1.0F, false, 0)).setAlwaysEdible().setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("tripes", 0, 0, 10, 1.0F, false, 0)).setAlwaysEdible().setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("vegcurry", 0, 0, 6, 0.6F, false, 0)).setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("chickencurry", 0, 0, 8, 0.8F, false, 0)).setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("rasgulla", 2, 30, 0, 0.0F, false, 0))
        .setPotionEffect(new PotionEffect(MobEffects.SPEED, 9600, 1), 1.0F).setAlwaysEdible().setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("masa", 0, 0, 6, 0.6F, false, 0)).setMaxDamage(256));
    event.getRegistry().register((new ItemFoodMultiple("wah", 0, 0, 10, 1.0F, false, 0)).setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("balche", 6, 20, 0, 0.0F, true, 7)).setPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 9600, 1), 1.0F).setAlwaysEdible()
        .setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("sikilpah", 0, 0, 7, 0.7F, false, 0)).setMaxDamage(448));
    event.getRegistry().register((new ItemFoodMultiple("cacauhaa", 6, 30, 0, 0.0F, true, 0)).setPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 9600, 1), 1.0F)
        .setAlwaysEdible().setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("udon", 0, 0, 8, 0.8F, false, 0)).setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("sake", 8, 30, 0, 0.0F, true, 10)).setPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 9600, 1), 1.0F).setAlwaysEdible()
        .setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("ikayaki", 0, 0, 10, 1.0F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 9600, 2), 1.0F)
        .setAlwaysEdible().setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("feta", 2, 15, 0, 0.0F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("souvlaki", 0, 0, 10, 1.0F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 2), 1.0F).setAlwaysEdible()
        .setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("winebasic", 3, 15, 0, 0.0F, true, 5)).setAlwaysEdible().setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("winefancy", 8, 30, 0, 0.0F, true, 5)).setPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 9600, 2), 1.0F)
        .setAlwaysEdible().setMaxDamage(1024));
    event.getRegistry().register((new ItemFoodMultiple("bearmeat_raw", 0, 0, 4, 0.5F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.STRENGTH, 4800, 1), 1.0F)
        .setAlwaysEdible().setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("bearmeat_cooked", 0, 0, 10, 1.0F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.STRENGTH, 9600, 2), 1.0F)
        .setAlwaysEdible().setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("wolfmeat_raw", 0, 0, 3, 0.3F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("wolfmeat_cooked", 0, 0, 5, 0.6F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.STRENGTH, 1200, 1), 1.0F)
        .setAlwaysEdible().setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("seafood_raw", 0, 0, 2, 0.2F, false, 0)).setAlwaysEdible());
    event.getRegistry().register((new ItemFoodMultiple("seafood_cooked", 0, 0, 2, 0.25F, false, 0))
        .setPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 1200, 1), 1.0F).setAlwaysEdible());
    event.getRegistry().register((new ItemFoodMultiple("inuitpotatostew", 0, 0, 6, 0.6F, false, 0)).setMaxDamage(384));
    event.getRegistry().register((new ItemFoodMultiple("inuitmeatystew", 0, 0, 8, 0.8F, false, 0)).setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("inuitbearstew", 0, 0, 8, 1.0F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.STRENGTH, 9600, 3), 1.0F)
        .setAlwaysEdible().setMaxDamage(512));
    event.getRegistry().register((new ItemFoodMultiple("ayran", 3, 15, 0, 0.2F, true, 2)).setAlwaysEdible().setMaxDamage(6));
    event.getRegistry().register((new ItemFoodMultiple("yogurt", 0, 15, 0, 0.4F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("pide", 0, 0, 8, 1.0F, false, 0)).setMaxDamage(8));
    event.getRegistry().register((new ItemFoodMultiple("lokum", 0, 0, 3, 0.0F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.SPEED, 2400, 1), 0.2F).setAlwaysEdible());
    event.getRegistry()
      .register((new ItemFoodMultiple("helva", 0, 0, 5, 0.0F, false, 0)).setPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 2400, 1), 0.2F).setAlwaysEdible());
    event.getRegistry().register((new ItemFoodMultiple("pistachios", 0, 0, 1, 0.1F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("cherries", 0, 0, 1, 0.1F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register((new ItemFoodMultiple("cherry_blossom", 0, 0, 1, 0.1F, false, 0)).setMaxStackSize(64));
    event.getRegistry().register(new ItemWallDecoration("tapestry", 1));
    event.getRegistry().register(new ItemWallDecoration("indianstatue", 2));
    event.getRegistry().register(new ItemWallDecoration("mayanstatue", 3));
    event.getRegistry().register(new ItemWallDecoration("byzantineiconsmall", 4));
    event.getRegistry().register(new ItemWallDecoration("byzantineiconmedium", 5));
    event.getRegistry().register(new ItemWallDecoration("byzantineiconlarge", 6));
    event.getRegistry().register(new ItemWallDecoration("hidehanging", 7));
    event.getRegistry().register(new ItemWallDecoration("wallcarpetsmall", 8));
    event.getRegistry().register(new ItemWallDecoration("wallcarpetmedium", 9));
    event.getRegistry().register(new ItemWallDecoration("wallcarpetlarge", 10));
    for (EnumDyeColor colour : EnumDyeColor.values()) {
      ItemPaintBucket bucket = (ItemPaintBucket)(new ItemPaintBucket("paint_bucket", colour)).setMaxStackSize(1).setMaxDamage(2048);
      event.getRegistry().register(bucket);
      PAINT_BUCKETS.put(colour, bucket);
    } 
    event.getRegistry().register(new ItemParchment("parchment_normanvillagers", 1, true));
    event.getRegistry().register(new ItemParchment("parchment_normanbuildings", 2, true));
    event.getRegistry().register(new ItemParchment("parchment_normanitems", 3, true));
    event.getRegistry().register(new ItemParchment("parchment_normanfull", new int[] { 1, 2, 3 }, true));
    event.getRegistry().register(new ItemParchment("parchment_indianvillagers", 5, true));
    event.getRegistry().register(new ItemParchment("parchment_indianbuildings", 6, true));
    event.getRegistry().register(new ItemParchment("parchment_indianitems", 7, true));
    event.getRegistry().register(new ItemParchment("parchment_indianfull", new int[] { 5, 6, 7 }, true));
    event.getRegistry().register(new ItemParchment("parchment_mayanvillagers", 9, true));
    event.getRegistry().register(new ItemParchment("parchment_mayanbuildings", 10, true));
    event.getRegistry().register(new ItemParchment("parchment_mayanitems", 11, true));
    event.getRegistry().register(new ItemParchment("parchment_mayanfull", new int[] { 9, 10, 11 }, true));
    event.getRegistry().register(new ItemParchment("parchment_japanesevillagers", 16, true));
    event.getRegistry().register(new ItemParchment("parchment_japanesebuildings", 17, true));
    event.getRegistry().register(new ItemParchment("parchment_japaneseitems", 18, true));
    event.getRegistry()
      .register(new ItemParchment("parchment_japanesefull", new int[] { 16, 17, 18 }, true));
    event.getRegistry().register(new ItemParchment("parchment_villagescroll", 4, false));
    event.getRegistry().register((new ItemBrickMould("brickmould")).setFull3D().setMaxStackSize(1).setMaxDamage(512));
    event.getRegistry().register(new ItemMill("obsidianflake"));
    event.getRegistry().register(new ItemMill("silk"));
    event.getRegistry().register(new ItemClothes("clothes_byz_wool", 1));
    event.getRegistry().register(new ItemClothes("clothes_byz_silk", 2));
    event.getRegistry().register(new ItemClothes("clothes_seljuk_wool", 1));
    event.getRegistry().register(new ItemClothes("clothes_seljuk_cotton", 2));
    event.getRegistry().register((new ItemAmuletVishnu("vishnu_amulet")).setMaxStackSize(1));
    event.getRegistry().register((new ItemAmuletAlchemist("alchemist_amulet")).setMaxStackSize(1));
    event.getRegistry().register((new ItemAmuletYggdrasil("yggdrasil_amulet")).setMaxStackSize(1));
    event.getRegistry().register((new ItemAmuletSkollHati("skoll_hati_amulet")).setMaxStackSize(1));
    event.getRegistry().register((new ItemUlu("ulu")).setFull3D().setMaxStackSize(1).setMaxDamage(512));
    event.getRegistry().register(new ItemMill("tannedhide"));
    event.getRegistry().register(new ItemBannerPattern("bannerpattern"));
    event.getRegistry().register(new ItemParchment("parchment_sadhu", 15, false));
    event.getRegistry().register(new ItemMill("unknownpowder"));
    event.getRegistry().register(new ItemMayanQuestCrown("mayanquestcrown", EntityEquipmentSlot.HEAD));
  }
}
