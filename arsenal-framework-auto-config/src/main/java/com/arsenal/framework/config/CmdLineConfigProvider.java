package com.arsenal.framework.config;

import com.arsenal.framework.model.ArsenalConstant;
import com.arsenal.framework.model.config.ProfileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provider for [CmdLineConfig].
 *
 * @author Gordon.Gan
 */
@Configuration
public class CmdLineConfigProvider {

    @Value("${server.port}")
    private int port = ArsenalConstant.DEFAULT_PORT;

    @Value("${com.server-name}")
    private String serverName = "unspecified";

    @Value("${com.region}")
    private String region = "";

    @Bean
    public CmdLineConfig getCmdLineConfig(ApplicationContext applicationContext) {
        CmdLineConfig cmdLineConfig = new CmdLineConfigImpl();
        cmdLineConfig.setProfile(ProfileType.getProfile(applicationContext.getEnvironment().getActiveProfiles()));
        cmdLineConfig.setRegion(region);
        cmdLineConfig.setServerName(serverName);
        return cmdLineConfig;
    }
}
