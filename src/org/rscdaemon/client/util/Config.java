package org.rscdaemon.client.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    /**
     * Called to load config settings from the given file
     */
    public static void initConfig(String file) throws IOException {
        START_TIME = System.currentTimeMillis();
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        SERVER_IP = props.getProperty("server");
        SERVER_PORT = Integer.parseInt(props.getProperty("port"));
        CONF_DIR = props.getProperty("config_dir");
        MEDIA_DIR = props.getProperty("media_dir");
        MOVIE_FPS = Integer.parseInt(props.getProperty("movie_fps"));
        props.clear();
        /*
        SERVER_IP = "127.0.0.1";
        SERVER_PORT = 43594;
        CONF_DIR = "conf/client";
        MEDIA_DIR = "media";
        MOVIE_FPS = 5;
        */
    }

    public static String SERVER_IP, CONF_DIR, MEDIA_DIR;
    public static int SERVER_PORT, MOVIE_FPS;
    public static long START_TIME;
}