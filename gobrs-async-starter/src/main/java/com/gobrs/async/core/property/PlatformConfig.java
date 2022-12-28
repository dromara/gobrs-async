package com.gobrs.async.core.property;

/**
 * The type Platform config.
 *
 * @program: gobrs -async
 * @ClassName PlatformConfig
 * @description:
 * @author: sizegang
 * @create: 2022 -12-16
 */
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

    /**
     * Gets app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets app name.
     *
     * @param appName the app name
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
