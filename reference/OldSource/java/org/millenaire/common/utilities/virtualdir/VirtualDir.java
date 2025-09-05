package org.millenaire.common.utilities.virtualdir;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.millenaire.common.utilities.MillLog;

public class VirtualDir {
  private final List<File> sourceDirs;
  
  private Map<String, File> recursiveChildrenCache = null;
  
  private List<File> recursiveChildrenListCache = null;
  
  public VirtualDir(File sourceDir) {
    this.sourceDirs = new ArrayList<>();
    this.sourceDirs.add(sourceDir);
  }
  
  public VirtualDir(List<File> sourceDirs) throws Exception {
    if (sourceDirs == null || sourceDirs.isEmpty())
      throw new Exception("A virtual directory cannot be created with no source directories."); 
    this.sourceDirs = new ArrayList<>(sourceDirs);
  }
  
  public boolean exists() {
    for (File sourceDir : this.sourceDirs) {
      if (sourceDir.exists())
        return true; 
    } 
    return false;
  }
  
  public List<File> getAllChildFiles(String childName) {
    List<File> childFiles = new ArrayList<>();
    for (File sourceDir : this.sourceDirs) {
      File possibleChild = new File(sourceDir, childName);
      if (possibleChild.exists())
        childFiles.add(possibleChild); 
    } 
    return childFiles;
  }
  
  public VirtualDir getChildDirectory(String childDirectory) {
    List<File> childSourceDir = new ArrayList<>();
    for (File sourceDir : this.sourceDirs)
      childSourceDir.add(new File(sourceDir, childDirectory)); 
    try {
      return new VirtualDir(childSourceDir);
    } catch (Exception e) {
      MillLog.printException(e);
      return null;
    } 
  }
  
  public File getChildFile(String childName) {
    File childFile = null;
    for (File sourceDir : this.sourceDirs) {
      File possibleChild = new File(sourceDir, childName);
      if (possibleChild.exists())
        childFile = possibleChild; 
    } 
    return childFile;
  }
  
  public File getChildFileRecursive(String childName) {
    if (this.recursiveChildrenCache == null)
      rebuildRecursiveCache(); 
    return this.recursiveChildrenCache.get(childName.toLowerCase());
  }
  
  public String getName() {
    return ((File)this.sourceDirs.get(0)).getName();
  }
  
  public List<File> listFiles() {
    return listFiles(null);
  }
  
  public List<File> listFiles(FilenameFilter filter) {
    Map<String, File> children = new HashMap<>();
    for (File sourceDir : this.sourceDirs) {
      if (sourceDir.exists())
        for (File file : sourceDir.listFiles()) {
          if (!file.isDirectory() && (filter == null || filter.accept(sourceDir, file.getName())))
            children.put(file.getName().toLowerCase(), file); 
        }  
    } 
    List<File> childrenList = new ArrayList<>();
    List<String> names = new ArrayList<>(children.keySet());
    Collections.sort(names);
    for (String name : names)
      childrenList.add(children.get(name)); 
    return childrenList;
  }
  
  public List<File> listFilesRecursive() {
    return listFilesRecursive(null);
  }
  
  public List<File> listFilesRecursive(FilenameFilter filter) {
    if (this.recursiveChildrenCache == null)
      rebuildRecursiveCache(); 
    List<File> results = new ArrayList<>();
    for (File file : this.recursiveChildrenListCache) {
      if (filter == null || filter.accept(file.getParentFile(), file.getName()))
        results.add(file); 
    } 
    return results;
  }
  
  public List<VirtualDir> listSubDirs() {
    return listSubDirs(null);
  }
  
  public List<VirtualDir> listSubDirs(FilenameFilter filter) {
    Map<String, File> children = new HashMap<>();
    for (File sourceDir : this.sourceDirs) {
      if (sourceDir.exists())
        for (File file : sourceDir.listFiles()) {
          if (file.isDirectory() && (filter == null || filter.accept(sourceDir, file.getName())))
            children.put(file.getName().toLowerCase(), file); 
        }  
    } 
    List<VirtualDir> childrenList = new ArrayList<>();
    List<String> names = new ArrayList<>(children.keySet());
    Collections.sort(names);
    for (String name : names)
      childrenList.add(getChildDirectory(name)); 
    return childrenList;
  }
  
  public void mkdirs() {
    for (File source : this.sourceDirs) {
      if (!source.exists())
        source.mkdirs(); 
    } 
  }
  
  private void rebuildRecursiveCache() {
    this.recursiveChildrenCache = new HashMap<>();
    for (File sourceDir : this.sourceDirs) {
      if (sourceDir.exists())
        rebuildRecursiveCache_handleDirectory(sourceDir, this.recursiveChildrenCache); 
    } 
    this.recursiveChildrenListCache = new ArrayList<>();
    List<String> names = new ArrayList<>(this.recursiveChildrenCache.keySet());
    Collections.sort(names);
    for (String name : names)
      this.recursiveChildrenListCache.add(this.recursiveChildrenCache.get(name)); 
  }
  
  private void rebuildRecursiveCache_handleDirectory(File directory, Map<String, File> filesFound) {
    for (File file : directory.listFiles()) {
      if (file.isFile())
        filesFound.put(file.getName().toLowerCase(), file); 
    } 
    for (File file : directory.listFiles()) {
      if (file.isDirectory())
        rebuildRecursiveCache_handleDirectory(file, filesFound); 
    } 
  }
}
