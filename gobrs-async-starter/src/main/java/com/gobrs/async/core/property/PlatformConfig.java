package com.gobrs.async.core.property;

import lombok.Data;

/**
 * The type Platform config.
 *
 * @program: gobrs -async
 * @ClassName PlatformConfig
 * @description:
 * @author: sizegang
 * @create: 2022 -12-16
 */
@Data
public class PlatformConfig {

    private String appName;

    private String accessToken;


    /**
     * Instantiates a new Platform config.
     *
     * @param appName     the app name
     * @param accessToken the access token
     */
    public PlatformConfig(String appName, String accessToken) {
        this.appName = appName;
        this.accessToken = accessToken;
    }

}
