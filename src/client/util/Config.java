package client.util;

public class Config {
    /**
     * Called to load config settings from the given file
     */
    public static void initConfig()
    {
        START_TIME = System.currentTimeMillis();
        SERVER_IP = "127.0.0.1";
        SERVER_PORT = 43594;
        DATABASE_DIR = "database/";
        CONF_DIR = DATABASE_DIR+"config/";
        MEDIA_DIR = "media";
        MOVIE_FPS = 5;
    }

    public static String SERVER_IP = "127.0.0.1";
    public static String DATABASE_DIR = "database/";
    public static String CONF_DIR = DATABASE_DIR+"config/";
    public static String MEDIA_DIR = "media";
    public static int SERVER_PORT = 43594;
    public static int MOVIE_FPS = 5;
    public static long START_TIME;
}