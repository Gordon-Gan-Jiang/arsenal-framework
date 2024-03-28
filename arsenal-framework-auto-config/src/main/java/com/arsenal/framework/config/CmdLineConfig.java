package com.arsenal.framework.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Gordon.Gan
 */
@Setter
@Getter
public abstract class CmdLineConfig {
    private ProfileType profile;
    private String region;
    private Integer port;
    private String hostname;
    private String ip;
    private String serverName;
    private Boolean isAdminEnabled;
    private String version;
    private String ipAndPort;
    private String hostnameAndPort;
}
