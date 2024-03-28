package com.arsenal.framework.model.config;

import lombok.Getter;

/**
 * @author Gordon.Gan
 */
@Getter
public enum ProfileType {

    /**
     * For RD developing, default value.
     */
    DEV("dev"),

    /**
     * For unit test.
     */
    UNITTEST("unittest"),

    /**
     * For developer alpha environment.
     */
    ALPHA("perf"),

    /**
     * For QA beta environment.
     */
    BETA("alpha", "test", "sandbox"),

    /**
     * For QA performance test.
     */
    PERF("beta", "performance"),

    /**
     * For staging environment.
     */
    STAGING("staging", "pre", "pre-online", "stag"),

    /**
     * For online product environment.
     */
    PROD("prod", "production");
    private static String DEV_NAME = "dev";
    private static final String UNITTEST_NAME = "unittest";

    private static final String PERFORMANCE_NAME = "perf";

    private static final String ALPHA_NAME = "alpha";

    private static final String BETA_NAME = "beta";
    private static final String STAGING_NAME = "staging";

    private static final String PROD_NAME = "prod";

    ProfileType(String... names) {
        this.names = names;
    }

    private String[] names;

    public Boolean matches(String text) {
        for (String name : names) {
            if (name.equals(text)) {
                return true;
            }
        }
        return false;
    }

    public static ProfileType getProfile(String text) {
        if (text != null) {
            return getProfile(text.split(","));
        }
        return DEV;
    }

    public static ProfileType getProfile(String... text) {
        for (String profileTest : text) {
            for (ProfileType type : values()) {
                if (type.matches(profileTest)) {
                    return type;
                }
            }
        }
        return DEV;
    }

}
