package com.arsenal.framework.model;

import com.arsenal.framework.model.config.ProfileType;
import com.arsenal.framework.model.utility.NetworkUtils;

/**
 * @author Gordon.Gan
 */
public class RuntimeInfo {

    public static volatile ProfileType profile = ProfileType.DEV;

    public static volatile String region = "";

    public static volatile int port = ArsenalConstant.DEFAULT_PORT;

    public static volatile String hostname = NetworkUtils.hostname;
    public static volatile String group = "unspecified";

    public static volatile String serverName = "unspecified";

    public static volatile String hostIp = NetworkUtils.ipAddress;

    public static volatile Boolean isAdminEnabled = true;
    public static volatile Boolean unsafeMemoryMonitor = true;

    public static volatile Boolean printDebugInfo = false;

    public static volatile Boolean isShuttingDown = false;

    public static volatile Boolean isWritingDisabled = false;
    public static volatile Boolean isInUnitTest = false;
    public static volatile Boolean isInK8s = false;

    public static volatile String version = "";
    public static volatile String gitCommit = "";
    public static volatile String gitBranch = "";

    public static String ipAndPort() {
        return hostIp + ":" + port;
    }

    public static String hostnameAndPort() {
        return hostname + ":" + port;
    }

    public static String instanceInfo() {
        return hostname + "/" + profile.name() + "/" + hostIp;
    }
}
