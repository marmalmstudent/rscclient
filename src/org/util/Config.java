package org.rscdaemon.client.util;

public class Config {
    /**
     * Called to load config settings from the given file
     */
    public static void initConfig()
    {
        START_TIME = System.currentTimeMillis();
        SERVER_IP = "127.0.0.1";
        SERVER_PORT = 43594;
        CONF_DIR = "conf/client";
        MEDIA_DIR = "media";
        MOVIE_FPS = 5;
    }

    public static String SERVER_IP, CONF_DIR, MEDIA_DIR;
    public static int SERVER_PORT, MOVIE_FPS;
    public static long START_TIME;
}