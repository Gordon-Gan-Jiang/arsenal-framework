package com.arsenal.framework.config;

import com.arsenal.framework.model.RuntimeInfo;
import com.arsenal.framework.model.config.ProfileType;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author Gordon.Gan
 */
@ManagedResource("Arsenal:name=CmdLineConfig")
public class CmdLineConfigImpl extends CmdLineConfig{

    @ManagedAttribute
    @Override
    public ProfileType getProfile() {
        return RuntimeInfo.profile;
    }

    @ManagedAttribute
    @Override
    public void setProfile(ProfileType profile) {
        RuntimeInfo.profile = profile;
    }

    @ManagedAttribute
    @Override
    public String getRegion() {
        return RuntimeInfo.region;
    }

    @ManagedAttribute
    @Override
    public void setRegion(String region) {
        RuntimeInfo.region = region;
    }

    @ManagedAttribute
    @Override
    public Integer getPort() {
        return RuntimeInfo.port;
    }

    @ManagedAttribute
    @Override
    public void setPort(Integer port) {
        RuntimeInfo.port = port;
    }
    
    @ManagedAttribute
    @Override
    public String getHostname() {
        return RuntimeInfo.hostname;
    }

    @ManagedAttribute
    @Override
    public void setHostname(String hostname) {
        RuntimeInfo.hostname = hostname;
    }

    @ManagedAttribute
    @Override
    public String getIp() {
        return RuntimeInfo.hostIp;
    }


    @ManagedAttribute
    @Override
    public String getServerName() {
        return RuntimeInfo.serverName;
    }

    @ManagedAttribute
    @Override
    public void setServerName(String serverName) {
        RuntimeInfo.serverName = serverName;
    }

    @ManagedAttribute
    public Boolean getAdminEnabled() {
        return RuntimeInfo.isAdminEnabled;
    }

    @ManagedAttribute
    public void setAdminEnabled(Boolean adminEnabled) {
        RuntimeInfo.isAdminEnabled = adminEnabled;
    }
    
    @ManagedAttribute
    @Override
    public String getVersion() {
        return RuntimeInfo.version;
    }

    @ManagedAttribute
    @Override
    public void setVersion(String version) {
        RuntimeInfo.version = version;
    }


  /*  @ManagedAttribute
    public String getIpAndPort() {
        return RuntimeInfo.;
    }*/

   /* @ManagedAttribute
    public void setIpAndPort(String ipAndPort) {
        RuntimeInfo.ipAndPort = ipAndPort;
    }*/

   /* @ManagedAttribute
    public String getHostnameAndPort() {
        return hostnameAndPort;
    }*/

    /*@ManagedAttribute
    public void setHostnameAndPort(String hostnameAndPort) {
        RuntimeInfo.hostnameAndPort = hostnameAndPort;
    }*/
}
