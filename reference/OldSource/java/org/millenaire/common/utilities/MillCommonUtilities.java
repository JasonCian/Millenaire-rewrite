package org.millenaire.common.utilities;

import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.millenaire.common.advancements.GenericAdvancement;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class MillCommonUtilities {
  private static final String MILLENAIRE_ORG_ROOT = "http://millenaire.org";
  
  public static class BonusThread extends Thread {
    String login;
    
    public BonusThread(String login) {
      this.login = login;
    }
    
    public void run() {
      try {
        InputStream stream = (new URL("http://millenaire.org/php/bonuscheck.php?login=" + this.login)).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String result = reader.readLine();
        if (result.trim().equals("thik hai")) {
          MillConfigValues.bonusEnabled = true;
          MillConfigValues.bonusCode = MillConfigValues.calculateLoginMD5(this.login);
          MillConfigValues.writeConfigFile();
        } 
      } catch (Exception exception) {}
    }
  }
  
  public static class ExtFileFilter implements FilenameFilter {
    String ext = null;
    
    public ExtFileFilter(String ext) {
      this.ext = ext;
    }
    
    public boolean accept(File file, String name) {
      if (!name.toLowerCase().endsWith("." + this.ext))
        return false; 
      if (name.startsWith("."))
        return false; 
      return true;
    }
  }
  
  private static class LogThread extends Thread {
    String url;
    
    public LogThread(String url) {
      this.url = url;
    }
    
    public void run() {
      try {
        InputStream stream = (new URL(this.url)).openStream();
        stream.close();
      } catch (Exception e) {
        if (MillConfigValues.DEV == true)
          MillLog.error(null, "Exception when calling statistic service:" + e.getMessage().substring(0, e.getMessage().indexOf("?"))); 
      } 
    }
  }
  
  public static class PrefixExtFileFilter implements FilenameFilter {
    String ext = null;
    
    String prefix = null;
    
    public PrefixExtFileFilter(String pref, String ext) {
      this.ext = ext;
      this.prefix = pref;
    }
    
    public boolean accept(File file, String name) {
      if (!name.toLowerCase().endsWith("." + this.ext))
        return false; 
      if (!name.toLowerCase().startsWith(this.prefix))
        return false; 
      if (name.startsWith("."))
        return false; 
      return true;
    }
  }
  
  public static class VersionCheckThread extends Thread {
    public void run() {
      try {
        if ("8.1.1".contains("@VERSION@"))
          return; 
        Thread.sleep(60000L);
        boolean devVersion = false;
        if ("8.1.1".contains("alpha") || "8.1.1".contains("beta") || "8.1.1".contains("rc"))
          devVersion = true; 
        String url = "http://millenaire.org/lastversion/1.12.2";
        if (devVersion)
          url = url + "-dev"; 
        url = url + ".txt";
        InputStream stream = (new URL(url)).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String currentVersion = reader.readLine();
        if (currentVersion != null) {
          currentVersion = currentVersion.trim();
          if (!currentVersion.equals("8.1.1")) {
            String releaseNotesEN = reader.readLine().trim();
            String releaseNotesFR = reader.readLine().trim();
            if (MillConfigValues.mainLanguage.language.startsWith("fr")) {
              String releaseNote = releaseNotesFR;
            } else if (MillConfigValues.mainLanguage.language.startsWith("en")) {
              String releaseNote = releaseNotesEN;
            } else if (MillConfigValues.fallback_language.equals("fr")) {
              String releaseNote = releaseNotesFR;
            } else {
              String releaseNote = releaseNotesEN;
            } 
            String str1 = devVersion ? "startup.outdatedversiondev" : "startup.outdatedversion";
          } 
        } 
      } catch (Exception e) {
        MillLog.printException("Error when checking version:", e);
      } 
    }
  }
  
  public static class VillageInfo implements Comparable<VillageInfo> {
    public String textKey;
    
    public String[] values;
    
    public int distance;
    
    public int compareTo(VillageInfo arg0) {
      return arg0.distance - this.distance;
    }
    
    public boolean equals(Object o) {
      if (o == null || !(o instanceof VillageInfo))
        return false; 
      return (this.distance == ((VillageInfo)o).distance);
    }
    
    public int hashCode() {
      return super.hashCode();
    }
  }
  
  public static class VillageList {
    public List<Point> pos = new ArrayList<>();
    
    public List<String> names = new ArrayList<>();
    
    public List<String> types = new ArrayList<>();
    
    public List<String> cultures = new ArrayList<>();
    
    public List<String> generatedFor = new ArrayList<>();
    
    public List<List<Long>> buildingsTime = new ArrayList<>();
    
    public List<List<Long>> villagersTime = new ArrayList<>();
    
    public Map<Point, Integer> rankByPos = new HashMap<>();
    
    public void addVillage(Point p, String name, String type, String culture, String generatedFor) {
      this.pos.add(p);
      this.names.add(name);
      this.types.add(type);
      this.cultures.add(culture);
      this.generatedFor.add(generatedFor);
      this.buildingsTime.add(new ArrayList<>());
      this.villagersTime.add(new ArrayList<>());
      this.rankByPos.put(p, Integer.valueOf(this.pos.size() - 1));
    }
    
    public void removeVillage(Point p) {
      int id = -1;
      int i;
      for (i = 0; i < this.pos.size() && id == -1; i++) {
        if (p.sameBlock(this.pos.get(i)))
          id = i; 
      } 
      if (id != -1) {
        this.pos.remove(id);
        this.names.remove(id);
        this.types.remove(id);
        this.cultures.remove(id);
        this.generatedFor.remove(id);
      } 
      this.rankByPos.clear();
      for (i = 0; i < this.pos.size(); i++)
        this.rankByPos.put(this.pos.get(i), Integer.valueOf(i)); 
    }
  }
  
  public static Random random = new Random();
  
  private static File baseDir = null;
  
  private static File customDir = null;
  
  public static boolean chanceOn(int i) {
    return (getRandom().nextInt(i) == 0);
  }
  
  public static void changeMoney(IInventory chest, int toChange, EntityPlayer player) {
    boolean hasPurse = false;
    for (int i = 0; i < chest.getSizeInventory() && !hasPurse; i++) {
      ItemStack stack = chest.getStackInSlot(i);
      if (stack != null && 
        stack.getItem() == MillItems.PURSE)
        hasPurse = true; 
    } 
    if (hasPurse) {
      int current_denier = WorldUtilities.getItemsFromChest(chest, (Item)MillItems.DENIER, 0, 2147483647);
      int current_DENIER_ARGENT = WorldUtilities.getItemsFromChest(chest, (Item)MillItems.DENIER_ARGENT, 0, 2147483647);
      int current_DENIER_OR = WorldUtilities.getItemsFromChest(chest, (Item)MillItems.DENIER_OR, 0, 2147483647);
      int finalChange = current_DENIER_OR * 64 * 64 + current_DENIER_ARGENT * 64 + current_denier + toChange;
      for (int j = 0; j < chest.getSizeInventory() && finalChange != 0; j++) {
        ItemStack stack = chest.getStackInSlot(j);
        if (stack != null && 
          stack.getItem() == MillItems.PURSE) {
          int content = MillItems.PURSE.totalDeniers(stack) + finalChange;
          if (content >= 0) {
            MillItems.PURSE.setDeniers(stack, player, content);
            finalChange = 0;
          } else {
            MillItems.PURSE.setDeniers(stack, player, 0);
            finalChange = content;
          } 
        } 
      } 
    } else {
      int total = toChange + countMoney(chest);
      int denier = total % 64;
      int DENIER_ARGENT = (total - denier) / 64 % 64;
      int DENIER_OR = (total - denier - DENIER_ARGENT * 64) / 4096;
      if (player != null && DENIER_OR > 0)
        MillAdvancements.CRESUS.grant(player); 
      int current_denier = countChestItems(chest, (Item)MillItems.DENIER, 0);
      int current_DENIER_ARGENT = countChestItems(chest, (Item)MillItems.DENIER_ARGENT, 0);
      int current_DENIER_OR = countChestItems(chest, (Item)MillItems.DENIER_OR, 0);
      if (MillConfigValues.LogWifeAI >= 1)
        MillLog.major(null, "Putting: " + denier + "/" + DENIER_ARGENT + "/" + DENIER_OR + " replacing " + current_denier + "/" + current_DENIER_ARGENT + "/" + current_DENIER_OR); 
      if (denier < current_denier) {
        WorldUtilities.getItemsFromChest(chest, (Item)MillItems.DENIER, 0, current_denier - denier);
      } else if (denier > current_denier) {
        putItemsInChest(chest, (Item)MillItems.DENIER, 0, denier - current_denier);
      } 
      if (DENIER_ARGENT < current_DENIER_ARGENT) {
        WorldUtilities.getItemsFromChest(chest, (Item)MillItems.DENIER_ARGENT, 0, current_DENIER_ARGENT - DENIER_ARGENT);
      } else if (DENIER_ARGENT > current_DENIER_ARGENT) {
        putItemsInChest(chest, (Item)MillItems.DENIER_ARGENT, 0, DENIER_ARGENT - current_DENIER_ARGENT);
      } 
      if (DENIER_OR < current_DENIER_OR) {
        WorldUtilities.getItemsFromChest(chest, (Item)MillItems.DENIER_OR, 0, current_DENIER_OR - DENIER_OR);
      } else if (DENIER_OR > current_DENIER_OR) {
        putItemsInChest(chest, (Item)MillItems.DENIER_OR, 0, DENIER_OR - current_DENIER_OR);
      } 
    } 
  }
  
  public static int countChestItems(IInventory chest, Block block, int meta) {
    return countChestItems(chest, Item.getItemFromBlock(block), meta);
  }
  
  public static int countChestItems(IInventory chest, IBlockState blockState) {
    return countChestItems(chest, blockState.getBlock(), blockState.getBlock().getMetaFromState(blockState));
  }
  
  public static int countChestItems(IInventory chest, Item item, int meta) {
    if (chest == null)
      return 0; 
    int maxSlot = chest.getSizeInventory();
    if (chest instanceof net.minecraft.entity.player.InventoryPlayer)
      maxSlot -= 5; 
    int nb = 0;
    for (int i = 0; i < maxSlot; i++) {
      ItemStack stack = chest.getStackInSlot(i);
      if (stack != null && stack.getItem() == item && (meta == -1 || stack.getItemDamage() < 0 || stack.getItemDamage() == meta))
        nb += stack.getCount(); 
      if (item == Item.getItemFromBlock(Blocks.LOG) && meta == -1 && 
        stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.LOG2))
        nb += stack.getCount(); 
    } 
    return nb;
  }
  
  public static int countFurnaceItems(IInventory furnace, Item item, int meta) {
    if (furnace == null)
      return 0; 
    int nb = 0;
    ItemStack stack = furnace.getStackInSlot(2);
    if (stack != null && stack.getItem() == item && (meta == -1 || stack.getItemDamage() < 0 || stack.getItemDamage() == meta))
      nb += stack.getCount(); 
    if (item == Item.getItemFromBlock(Blocks.LOG) && meta == -1 && 
      stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.LOG2))
      nb += stack.getCount(); 
    return nb;
  }
  
  public static int countMoney(IInventory chest) {
    int deniers = 0;
    for (int i = 0; i < chest.getSizeInventory(); i++) {
      ItemStack stack = chest.getStackInSlot(i);
      if (stack != null)
        if (stack.getItem() == MillItems.PURSE) {
          deniers += MillItems.PURSE.totalDeniers(stack);
        } else if (stack.getItem() == MillItems.DENIER) {
          deniers += stack.getCount();
        } else if (stack.getItem() == MillItems.DENIER_ARGENT) {
          deniers += stack.getCount() * 64;
        } else if (stack.getItem() == MillItems.DENIER_OR) {
          deniers += stack.getCount() * 64 * 64;
        }  
    } 
    return deniers;
  }
  
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success)
          return false; 
      } 
    } 
    return dir.delete();
  }
  
  public static String flattenStrings(Collection<String> strings) {
    return strings.stream().collect(Collectors.joining(", "));
  }
  
  public static void generateHearts(Entity ent) {
    for (int var3 = 0; var3 < 7; var3++) {
      double var4 = random.nextGaussian() * 0.02D;
      double var6 = random.nextGaussian() * 0.02D;
      double var8 = random.nextGaussian() * 0.02D;
      ent.world.spawnParticle(EnumParticleTypes.HEART, ent.posX + (random.nextFloat() * ent.width * 2.0F) - ent.width, ent.posY + 0.5D + (random.nextFloat() * ent.height), ent.posZ + (random
          .nextFloat() * ent.width * 2.0F) - ent.width, var4, var6, var8, new int[0]);
    } 
  }
  
  public static BufferedWriter getAppendWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF8"));
  }
  
  public static File getBuildingsDir(World world) {
    File saveDir = getWorldSaveDir(world);
    File millenaireDir = new File(saveDir, "millenaire");
    if (!millenaireDir.exists())
      millenaireDir.mkdir(); 
    File buildingsDir = new File(millenaireDir, "buildings");
    if (!buildingsDir.exists())
      buildingsDir.mkdir(); 
    return buildingsDir;
  }
  
  public static String getCardinalDirectionStringFromAngle(int angle) {
    angle %= 360;
    if (angle < 0)
      angle += 360; 
    if (angle < 22 || angle > 338)
      return "south"; 
    if (angle < 68)
      return "south-west"; 
    if (angle < 112)
      return "west"; 
    if (angle < 158)
      return "north-west"; 
    if (angle < 202)
      return "north"; 
    if (angle < 248)
      return "north-east"; 
    if (angle < 292)
      return "east"; 
    return "south-east";
  }
  
  public static Method getDrawItemStackInventoryMethod(GuiContainer gui) {
    return ReflectionHelper.findMethod(GuiContainer.class, "drawItemStack", "drawItemStack", new Class[] { ItemStack.class, int.class, int.class, String.class });
  }
  
  public static Method getDrawSlotInventoryMethod(GuiContainer gui) {
    return ReflectionHelper.findMethod(GuiContainer.class, "drawSlot", "drawSlot", new Class[] { Slot.class });
  }
  
  public static File getExportDir() {
    File exportDir = new File(getMillenaireCustomContentDir(), "exports");
    if (!exportDir.exists())
      exportDir.mkdirs(); 
    return exportDir;
  }
  
  public static List<String> getFileLines(File file) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
    List<String> lines = new ArrayList<>();
    String line = reader.readLine();
    while (line != null) {
      lines.add(line);
      line = reader.readLine();
    } 
    reader.close();
    return lines;
  }
  
  public static int getInvItemHashTotal(HashMap<InvItem, Integer> map) {
    int total = 0;
    for (InvItem key : map.keySet())
      total += ((Integer)map.get(key)).intValue(); 
    return total;
  }
  
  public static Item getItemById(int id) {
    return Item.getItemById(id);
  }
  
  public static double getItemWeaponDamage(Item item) {
    Multimap<String, AttributeModifier> multimap = item.getItemAttributeModifiers(EntityEquipmentSlot.MAINHAND);
    if (multimap.containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName()) && (
      multimap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).toArray()).length > 0 && 
      multimap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).toArray()[0] instanceof AttributeModifier) {
      AttributeModifier weaponModifier = (AttributeModifier)multimap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).toArray()[0];
      return weaponModifier.getAmount();
    } 
    return 0.0D;
  }
  
  public static int[] getJumpDestination(World world, int x, int y, int z) {
    if (!WorldUtilities.isBlockFullCube(world, x, y, z) && !WorldUtilities.isBlockFullCube(world, x, y + 1, z))
      return new int[] { x, y, z }; 
    if (!WorldUtilities.isBlockFullCube(world, x + 1, y, z) && !WorldUtilities.isBlockFullCube(world, x + 1, y + 1, z))
      return new int[] { x + 1, y, z }; 
    if (!WorldUtilities.isBlockFullCube(world, x - 1, y, z) && !WorldUtilities.isBlockFullCube(world, x - 1, y + 1, z))
      return new int[] { x - 1, y, z }; 
    if (!WorldUtilities.isBlockFullCube(world, x, y, z + 1) && !WorldUtilities.isBlockFullCube(world, x, y + 1, z + 1))
      return new int[] { x, y, z + 1 }; 
    if (!WorldUtilities.isBlockFullCube(world, x, y, z - 1) && !WorldUtilities.isBlockFullCube(world, x, y + 1, z - 1))
      return new int[] { x, y, z - 1 }; 
    return null;
  }
  
  public static File getMillenaireContentDir() {
    if (baseDir == null)
      baseDir = new File(getModsDir(), "millenaire"); 
    return baseDir;
  }
  
  public static File getMillenaireCustomContentDir() {
    if (customDir == null)
      customDir = new File(getModsDir(), "millenaire-custom"); 
    return customDir;
  }
  
  public static File getMillenaireHelpDir() {
    return new File(getMillenaireContentDir(), "help");
  }
  
  public static File getModsDir() {
    return new File(Loader.instance().getConfigDir().getParentFile(), "mods");
  }
  
  public static int getPriceColourMC(int price) {
    if (price >= 4096)
      return 14; 
    if (price >= 64)
      return 15; 
    return 6;
  }
  
  public static Random getRandom() {
    if (random == null)
      random = new Random(); 
    return random;
  }
  
  public static BufferedReader getReader(File file) throws UnsupportedEncodingException, FileNotFoundException {
    return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
  }
  
  public static String getShortPrice(int price) {
    String res = "";
    if (price >= 4096) {
      res = (int)Math.floor((price / 4096)) + "o ";
      price %= 4096;
    } 
    if (price >= 64) {
      res = res + (int)Math.floor((price / 64)) + "a ";
      price %= 64;
    } 
    if (price > 0)
      res = res + price + "d"; 
    return res.trim();
  }
  
  public static WeightedChoice getWeightedChoice(List<? extends WeightedChoice> choices, EntityPlayer player) {
    int weightTotal = 0;
    List<Integer> weights = new ArrayList<>();
    for (WeightedChoice choice : choices) {
      weightTotal += choice.getChoiceWeight(player);
      weights.add(Integer.valueOf(choice.getChoiceWeight(player)));
    } 
    if (weightTotal < 1)
      return null; 
    int random = randomInt(weightTotal);
    int count = 0;
    for (int i = 0; i < choices.size(); i++) {
      count += ((Integer)weights.get(i)).intValue();
      if (random < count)
        return choices.get(i); 
    } 
    return null;
  }
  
  public static File getWorldSaveDir(World world) {
    ISaveHandler isavehandler = world.getSaveHandler();
    if (isavehandler instanceof SaveHandler)
      return ((SaveHandler)isavehandler).getWorldDirectory(); 
    return null;
  }
  
  public static BufferedWriter getWriter(File file) throws UnsupportedEncodingException, FileNotFoundException {
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
  }
  
  public static void initRandom(int seed) {
    random = new Random(seed);
  }
  
  public static void logInstance(World world) {
    String mode;
    if (!MillConfigValues.sendStatistics)
      return; 
    String os = System.getProperty("os.name");
    if (Mill.proxy.isTrueServer()) {
      mode = "s";
    } else if (Mill.isDistantClient()) {
      mode = "c";
    } else {
      mode = "l";
    } 
    int totalexp = 0;
    if (Mill.proxy.isTrueServer()) {
      if (!Mill.serverWorlds.isEmpty())
        for (UserProfile p : ((MillWorldData)Mill.serverWorlds.get(0)).profiles.values()) {
          for (Culture c : Culture.ListCultures)
            totalexp += Math.abs(p.getCultureReputation(c.key)); 
        }  
    } else {
      UserProfile p = Mill.proxy.getClientProfile();
      if (p != null)
        for (Culture c : Culture.ListCultures)
          totalexp += Math.abs(p.getCultureReputation(c.key));  
    } 
    String lang = "";
    if (MillConfigValues.mainLanguage != null)
      lang = MillConfigValues.mainLanguage.language; 
    int nbplayers = 1;
    if (Mill.proxy.isTrueServer() && !Mill.serverWorlds.isEmpty())
      nbplayers = ((MillWorldData)Mill.serverWorlds.get(0)).profiles.size(); 
    String advancementsSurvivalDone = null;
    for (GenericAdvancement advancement : MillAdvancements.MILL_ADVANCEMENTS) {
      if (advancementsSurvivalDone == null) {
        advancementsSurvivalDone = "";
      } else {
        advancementsSurvivalDone = advancementsSurvivalDone + ",";
      } 
      advancementsSurvivalDone = advancementsSurvivalDone + advancement.getKey() + ":" + MillConfigValues.advancementsSurvival.contains(advancement.getKey());
    } 
    String advancementsCreativeDone = null;
    for (GenericAdvancement advancement : MillAdvancements.MILL_ADVANCEMENTS) {
      if (advancementsCreativeDone == null) {
        advancementsCreativeDone = "";
      } else {
        advancementsCreativeDone = advancementsCreativeDone + ",";
      } 
      advancementsCreativeDone = advancementsCreativeDone + advancement.getKey() + ":" + MillConfigValues.advancementsCreative.contains(advancement.getKey());
    } 
    String url = "http://millenaire.org/php/mlnuse.php?uid=" + MillConfigValues.randomUid + "&mlnversion=" + "8.1.1" + "&mode=" + mode + "&lang=" + lang + "&backuplang=" + MillConfigValues.fallback_language + "&nbplayers=" + nbplayers + "&os=" + os + "&totalexp=" + totalexp + "&advancementssurvival=" + advancementsSurvivalDone + "&advancementscreative=" + advancementsCreativeDone + "&validation=" + MillAdvancements.computeKey();
    if (Mill.proxy.getClientProfile() != null && MillConfigValues.sendAdvancementLogin)
      url = url + "&login=" + (Mill.proxy.getClientProfile()).playerName; 
    url = url.replaceAll(" ", "%20");
    MillConfigValues.logPerformed = true;
    (new LogThread(url)).start();
  }
  
  public static int[] packLong(long nb) {
    return new int[] { (int)(nb >> 32L), (int)nb };
  }
  
  public static String parseItemString(Culture culture, String inputString) {
    if ((inputString.split("/")).length != 2)
      return ""; 
    String result = "";
    String goodKey = inputString.split("/")[0];
    TradeGood good = culture.getTradeGood(goodKey);
    if (good != null)
      result = good.getName() + ": " + inputString.split("/")[1]; 
    return result;
  }
  
  public static boolean probability(double probability) {
    return (getRandom().nextDouble() < probability);
  }
  
  public static int putItemsInChest(IInventory chest, Block block, int toPut) {
    return putItemsInChest(chest, Item.getItemFromBlock(block), 0, toPut);
  }
  
  public static int putItemsInChest(IInventory chest, Block block, int meta, int toPut) {
    return putItemsInChest(chest, Item.getItemFromBlock(block), meta, toPut);
  }
  
  public static int putItemsInChest(IInventory chest, Item item, int toPut) {
    return putItemsInChest(chest, item, 0, toPut);
  }
  
  public static int putItemsInChest(IInventory chest, Item item, int meta, int toPut) {
    if (chest == null)
      return 0; 
    int nb = 0;
    int maxSlot = chest.getSizeInventory();
    if (chest instanceof net.minecraft.entity.player.InventoryPlayer)
      maxSlot -= 5; 
    int i;
    for (i = 0; i < maxSlot && nb < toPut; i++) {
      ItemStack stack = chest.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem() == item && stack.getItemDamage() == meta) {
        if (stack.getMaxStackSize() - stack.getCount() >= toPut - nb) {
          stack.setCount(stack.getCount() + toPut - nb);
          nb = toPut;
        } else {
          nb += stack.getMaxStackSize() - stack.getCount();
          stack.setCount(stack.getMaxStackSize());
        } 
        chest.setInventorySlotContents(i, stack);
      } 
    } 
    for (i = 0; i < maxSlot && nb < toPut; i++) {
      ItemStack stack = chest.getStackInSlot(i);
      if (stack == ItemStack.EMPTY) {
        stack = new ItemStack(item, 1, meta);
        if (stack.getItem() instanceof InvItem.IItemInitialEnchantmens)
          ((InvItem.IItemInitialEnchantmens)stack.getItem()).applyEnchantments(stack); 
        if (toPut - nb <= stack.getMaxStackSize()) {
          stack.setCount(toPut - nb);
          nb = toPut;
        } else {
          stack.setCount(stack.getMaxStackSize());
          nb += stack.getCount();
        } 
        chest.setInventorySlotContents(i, stack);
      } 
    } 
    return nb;
  }
  
  public static int randomInt(int max) {
    return getRandom().nextInt(max);
  }
  
  public static long randomLong() {
    return getRandom().nextLong();
  }
  
  public static int readInteger(String line) throws Exception {
    int res = 1;
    for (String s : line.trim().split("\\*"))
      res *= Integer.parseInt(s); 
    return res;
  }
  
  public static void readInventory(NBTTagList nbttaglist, Map<InvItem, Integer> inventory) {
    for (int i = 0; i < nbttaglist.tagCount(); i++) {
      NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
      String itemName = nbttagcompound1.getString("item");
      String itemMod = nbttagcompound1.getString("itemmod");
      int itemMeta = nbttagcompound1.getInteger("meta");
      inventory.put(InvItem.createInvItem(Item.getByNameOrId(itemMod + ":" + itemName), itemMeta), Integer.valueOf(nbttagcompound1.getInteger("amount")));
    } 
  }
  
  public static boolean testResourcePresence(String domain, String path) {
    return (VillagerType.class.getResourceAsStream("/assets/" + domain + "/" + path) != null);
  }
  
  public static long unpackLong(int nb1, int nb2) {
    return nb1 << 32L | nb2 & 0xFFFFFFFFL;
  }
  
  public static NBTTagList writeInventory(Map<InvItem, Integer> inventory) {
    NBTTagList nbttaglist = new NBTTagList();
    for (InvItem key : inventory.keySet()) {
      if (key.getItem() != null) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        String[] registryParts = key.getItem().getRegistryName().toString().split(":");
        nbttagcompound1.setString("itemmod", registryParts[0]);
        nbttagcompound1.setString("item", registryParts[1]);
        nbttagcompound1.setInteger("meta", key.meta);
        nbttagcompound1.setInteger("amount", ((Integer)inventory.get(key)).intValue());
        nbttaglist.appendTag((NBTBase)nbttagcompound1);
      } 
      MillLog.error(null, "Key with null item when saving inventory: " + key);
    } 
    return nbttaglist;
  }
  
  public static interface WeightedChoice {
    int getChoiceWeight(EntityPlayer param1EntityPlayer);
  }
}
