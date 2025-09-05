package org.millenaire.common.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.common.FMLLog;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.forge.Mill;

public class MillLog {
  public static class MillenaireException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public MillenaireException(String string) {
      super(string);
    }
  }
  
  private static FileWriter writer = null;
  
  public static boolean console = true;
  
  public static final int DEBUG = 3;
  
  public static final int MINOR = 2;
  
  public static final int MAJOR = 1;
  
  private static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm:ss";
  
  private static final Map<Integer, Integer> exceptionCount = new HashMap<>();
  
  public static void debug(Object obj, String s) {
    writeText("DEBUG: " + obj + ": " + s);
  }
  
  public static void error(Object obj, String s) {
    if (MillConfigValues.DEV)
      writeText("    !====================================!"); 
    writeText("ERROR: " + obj + ": " + s);
    if (MillConfigValues.DEV)
      writeText("     ==================================== "); 
  }
  
  public static String getLogLevel(int level) {
    if (level == 1)
      return "major"; 
    if (level == 2)
      return "minor"; 
    if (level == 3)
      return "debug"; 
    return "";
  }
  
  public static void initLogFileWriter() {
    try {
      writer = new FileWriter(Mill.proxy.getLogFile(), true);
    } catch (IOException e) {
      writer = null;
    } 
  }
  
  public static void major(Object obj, String s) {
    writeText("MAJOR: " + obj + ": " + s);
  }
  
  public static void minor(Object obj, String s) {
    writeText("MINOR: " + obj + ": " + s);
  }
  
  public static String now() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    return sdf.format(cal.getTime());
  }
  
  public static int printException(String errorDetail, Throwable e) {
    String hashString, exceptionTitle = "";
    if (errorDetail != null) {
      hashString = errorDetail;
      exceptionTitle = errorDetail;
    } else {
      hashString = "";
      exceptionTitle = "";
    } 
    if (e.getMessage() != null) {
      hashString = hashString + e.getMessage();
      exceptionTitle = exceptionTitle + " - " + e.getMessage();
    } 
    String stackStart = "";
    if (e.getStackTrace() != null)
      for (int i = 0; i < 6 && i < (e.getStackTrace()).length; i++) {
        hashString = hashString + e.getStackTrace()[i].toString();
        if (i == 0)
          stackStart = e.getStackTrace()[i].toString(); 
      }  
    int nbOccurences = 1;
    int hash = hashString.hashCode();
    if (exceptionCount.containsKey(Integer.valueOf(hash))) {
      nbOccurences = ((Integer)exceptionCount.get(Integer.valueOf(hash))).intValue();
      nbOccurences++;
    } 
    exceptionCount.put(Integer.valueOf(hash), Integer.valueOf(nbOccurences));
    if (MillConfigValues.DEV && nbOccurences == 1)
      writeText("    !====================================!"); 
    if (nbOccurences <= 5) {
      if (errorDetail == null) {
        writeText("Exception, printing stack:");
      } else {
        writeText(errorDetail);
      } 
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      e.printStackTrace(pw);
      pw.flush();
      sw.flush();
      writeText(sw.toString());
    } else if (nbOccurences < 20) {
      writeText("Repeat exception x" + nbOccurences + ": " + exceptionTitle + " @ " + stackStart);
    } else if (nbOccurences == 20) {
      writeText("Repeat exception x" + nbOccurences + ": " + exceptionTitle + " @ " + stackStart + ". This error will no longer be logged in this session.");
    } 
    if (MillConfigValues.DEV && nbOccurences == 1)
      writeText("     ==================================== "); 
    return nbOccurences;
  }
  
  public static void printException(Throwable e) {
    printException(null, e);
  }
  
  public static int readLogLevel(String s) {
    if (s.equalsIgnoreCase("major"))
      return 1; 
    if (s.equalsIgnoreCase("minor"))
      return 2; 
    if (s.equalsIgnoreCase("debug"))
      return 3; 
    return 0;
  }
  
  public static void temp(Object obj, String s) {
    if (MillConfigValues.DEV)
      writeText("TEMP: " + obj + ": " + s); 
  }
  
  public static void warning(Object obj, String s) {
    if (MillConfigValues.DEV)
      writeText("    !=============!"); 
    writeText("WARNING: " + obj + ": " + s);
    if (MillConfigValues.DEV)
      writeText("     ============="); 
  }
  
  public static void writeText(String s) {
    if (console)
      FMLLog.info(Mill.proxy.logPrefix() + LanguageUtilities.removeAccent(s), new Object[0]); 
    if (writer != null)
      try {
        writer.write("8.1.1 " + now() + " " + s + MillConfigValues.NEOL);
        writer.flush();
      } catch (IOException e) {
        System.out.println("Failed to write line to log file.");
      }  
  }
  
  public static void writeTextRaw(String s) {
    if (console)
      FMLLog.info(LanguageUtilities.removeAccent(s), new Object[0]); 
    if (writer != null)
      try {
        writer.write(s + MillConfigValues.NEOL);
        writer.flush();
      } catch (IOException e) {
        System.out.println("Failed to write line to log file.");
      }  
  }
}
