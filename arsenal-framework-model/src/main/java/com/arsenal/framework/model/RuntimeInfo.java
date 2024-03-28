package com.arsenal.framework.model;

import com.arsenal.framework.model.config.ProfileType;
import com.arsenal.framework.model.utility.NetworkUtils;
import kotlin.jvm.Volatile;

/**
 * @author Gordon.Gan
 */
public class RuntimeInfo {

    public static volatile ProfileType profile = ProfileType.DEV;

    public static volatile String region = "";

    public static volatile int port = ArsenalConstant.DEFAULT_PORT;

    public static volatile String hostname= NetworkUtils.hostname;

}
