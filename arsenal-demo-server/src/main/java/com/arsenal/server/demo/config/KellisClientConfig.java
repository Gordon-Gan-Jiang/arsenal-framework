// Copyright 2024 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.server.demo.config;


import com.arsenal.framework.config.annotations.ArsenalConfiguration;
import com.arsenal.framework.config.annotations.EnvConfig;
import com.arsenal.framework.model.config.ProfileType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Configuration for kellis client.
 *
 * @author yan chao(davis.yan@alo7.com)
 */
@ArsenalConfiguration(prefix = "com.alo7.kellis.client")
public class KellisClientConfig {
    @EnvConfig(env = ProfileType.PROD,
            value = "http://kellis-server.internal-k8s.alo7.com")
    @EnvConfig(env = ProfileType.STAGING,
            value = "http://kellis-server.staging.saybot.net")
    @EnvConfig(env = ProfileType.BETA,
            value = "http://kellis-server.beta.saybot.net")
    @EnvConfig(env = ProfileType.ALPHA,
            value = "http://kellis-server.alpha.saybot.net")
    @EnvConfig(env = ProfileType.UNITTEST,
            value = "")
    @EnvConfig(env = ProfileType.DEV,
            value = "")
    @Getter(onMethod_ = @Autowired)
    @Setter(onMethod_ = @Autowired)
    private String endpoint = "";
}
