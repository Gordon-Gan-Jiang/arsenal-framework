package com.arsenal.framework.config.annotations;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark/generate a  configuration bean.
 *
 * @author Gordon.Gan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Configuration
public @interface ArsenalConfiguration {

    /**
     * The property prefix used in application.properties.
     *
     * !!!
     * DO NOT use value(), it (looks like) will cause problems because of spring alias.
     */
    String prefix();

}
