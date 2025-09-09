package org.millenaire.common.utilities;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockStateUtilities {
  private static final Splitter COMMA_SPLITTER = Splitter.on(',');
  
  private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);
  
  private static Map<IProperty<?>, Comparable<?>> getBlockStatePropertyValueMap(Block block, String values) {
    Map<IProperty<?>, Comparable<?>> map = Maps.newHashMap();
    if ("default".equals(values))
      return (Map<IProperty<?>, Comparable<?>>)block.getDefaultState().getProperties(); 
    BlockStateContainer blockstatecontainer = block.getBlockState();
    Iterator<String> iterator = COMMA_SPLITTER.split(values).iterator();
    while (true) {
      if (!iterator.hasNext())
        return map; 
      String s = iterator.next();
      Iterator<String> iterator1 = EQUAL_SPLITTER.split(s).iterator();
      if (!iterator1.hasNext())
        break; 
      IProperty<?> iproperty = blockstatecontainer.getProperty(iterator1.next());
      if (iproperty == null || !iterator1.hasNext())
        break; 
      Comparable<?> comparable = (Comparable<?>)getValueHelper(iproperty, iterator1.next());
      if (comparable == null)
        break; 
      map.put(iproperty, comparable);
    } 
    return null;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static IBlockState getBlockStateWithProperty(IBlockState blockState, IProperty property, Comparable value) {
    return blockState.withProperty(property, value);
  }
  
  public static IBlockState getBlockStateWithValues(IBlockState blockState, String values) {
    Map<IProperty<?>, Comparable<?>> properties = getBlockStatePropertyValueMap(blockState.getBlock(), values);
    if (properties == null) {
      MillLog.error(null, "Could not parse values line of " + values + " for block " + blockState.getBlock());
    } else {
      for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet()) {
        IProperty property = entry.getKey();
        Comparable value = entry.getValue();
        blockState = getBlockStateWithProperty(blockState, property, value);
      }
    } 
    return blockState;
  }
  
  public static BlockPlanks.EnumType getPlankVariant(IBlockState blockState) {
    Comparable rawVariant = getPropertyValueByName(blockState, "variant");
    if (rawVariant != null && rawVariant instanceof BlockPlanks.EnumType)
      return (BlockPlanks.EnumType)rawVariant; 
    return null;
  }
  
  public static Comparable getPropertyValueByName(IBlockState blockState, String propertyName) {
    BlockStateContainer blockStateContainer = blockState.getBlock().getBlockState();
    if (blockStateContainer.getProperty(propertyName) != null) {
      IProperty property = blockStateContainer.getProperty(propertyName);
      Comparable value = blockState.getValue(property);
      return value;
    } 
    return null;
  }
  
  public static String getStringFromBlockState(IBlockState blockState) {
    String properties = "";
    for (IProperty<?> property : (Iterable<IProperty<?>>)blockState.getPropertyKeys()) {
      if (properties.length() > 0)
        properties = properties + ","; 
      properties = properties + property.getName() + "=" + ((Comparable)blockState.getProperties().get(property)).toString();
    } 
    return blockState.getBlock().getRegistryName().toString() + ";" + properties;
  }
  
  @Nullable
  private static <T extends Comparable<T>> T getValueHelper(IProperty<T> p_190792_0_, String p_190792_1_) {
    return (T)p_190792_0_.parseValue(p_190792_1_).orNull();
  }
  
  public static boolean hasPropertyByName(IBlockState blockState, String propertyName) {
    BlockStateContainer blockStateContainer = blockState.getBlock().getBlockState();
    return (blockStateContainer.getProperty(propertyName) != null);
  }
  
  public static IBlockState setPropertyValueByName(IBlockState blockState, String propertyName, Comparable value) {
    BlockStateContainer blockStateContainer = blockState.getBlock().getBlockState();
    if (blockStateContainer.getProperty(propertyName) != null) {
      IProperty property = blockStateContainer.getProperty(propertyName);
      blockState = blockState.withProperty(property, value);
      return blockState;
    } 
    return null;
  }
}
