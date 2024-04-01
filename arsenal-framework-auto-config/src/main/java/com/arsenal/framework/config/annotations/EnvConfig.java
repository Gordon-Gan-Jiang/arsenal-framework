
package com.arsenal.framework.config.annotations;



import com.arsenal.framework.model.config.ProfileType;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Config annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = EvnConfigs.class)
public @interface EnvConfig {
    ProfileType[] env();

    String region() default "";

    String value();

    /**
     * The property value is encrypted using DES, and the password for decryption is passed by environment variables
     * "ENV_CONFIG_DECRYPT_KEY" which is configured in gitlab.
     */
    @Deprecated
    boolean encrypted() default false;

    /**
     * The property value is encrypted using DES, and the password for decryption is passed by environment variables
     * "ENV_CONFIG_DECRYPT_KEY" which is configured in gitlab.
     */
    boolean encryptedV2() default false;
}
