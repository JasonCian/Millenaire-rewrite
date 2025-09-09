package org.millenaire.common.annotedparameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ConfigAnnotations {
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  public static @interface FieldDocumentation {
    String explanation();
    
    String explanationCategory() default "";
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  public static @interface ConfigField {
    String defaultValue() default "";
    
    String fieldCategory() default "";
    
    String paramName() default "";
    
    AnnotedParameter.ParameterType type();
  }
}
