package org.millenaire.common.forge;

import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerReceiver;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.world.UserProfile;

@EventBusSubscriber(modid = "millenaire")
public class CommonProxy {
  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event) {
    System.out.println("Millenaire: Registering blocks...");
    MillBlocks.registerBlocks(event);
    System.out.println("Millenaire: Block registration complete.");
  }
  
  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event) {
    System.out.println("Millenaire: Registering items...");
    MillBlocks.registerItemBlocks(event);
    MillItems.registerItems(event);
    System.out.println("Millenaire: Item registration complete.");
  }
  
  public IGuiHandler createGuiHandler() {
    return new ServerGuiHandler();
  }
  
  public String getBlockName(Block block, int meta) {
    return null;
  }
  
  public UserProfile getClientProfile() {
    return null;
  }
  
  public File getConfigFile() {
    return new File(MillCommonUtilities.getMillenaireContentDir(), "config-server.txt");
  }
  
  public File getCustomConfigFile() {
    return new File(MillCommonUtilities.getMillenaireCustomContentDir(), "config-server-custom.txt");
  }
  
  public String getItemName(Item item, int meta) {
    return "";
  }
  
  public String getKeyString(int value) {
    return "";
  }
  
  public File getLogFile() {
    return new File(MillCommonUtilities.getMillenaireCustomContentDir(), "millenaire-server.log");
  }
  
  public String getQuestKeyName() {
    return "";
  }
  
  public String getSinglePlayerName() {
    return null;
  }
  
  public EntityPlayer getTheSinglePlayer() {
    return null;
  }
  
  public void handleClientGameUpdate() {}
  
  public void handleClientLogin() {}
  
  public void initNetwork() {
    Mill.millChannel.register(new ServerReceiver());
  }
  
  public boolean isTrueServer() {
    return true;
  }
  
  public int loadKeySetting(String value) {
    return 0;
  }
  
  public void loadLanguagesIfNeeded() {
    LanguageUtilities.loadLanguages(null);
  }
  
  public void localTranslatedSentence(EntityPlayer player, char colour, String code, String... values) {}
  
  public String logPrefix() {
    return "SRV ";
  }
  
  public void refreshClientResources() {}
  
  public void registerForgeClientClasses() {}
  
  public void registerKeyBindings() {}
  
  public void sendChatAdmin(String s) {}
  
  public void sendChatAdmin(String s, TextFormatting colour) {}
  
  public void sendLocalChat(EntityPlayer player, char colour, String s) {}
  
  public void setGraphicsLevel(BlockLeaves blockLeaves, boolean value) {}
}
