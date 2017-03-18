package org.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.DataOperations;
import org.mudclient;

public class misc {


    public static String timeSince(long time)
    {
        int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
        int minutes = (int) (seconds / 60);
        int hours = (int) (minutes / 60);
        int days = (int) (hours / 24);
        return days + " days " + (hours % 24) + " hours " + (minutes % 60) + " mins";
    }


    public static File getEmptyFile(
    		boolean movie, String userName) throws IOException
    {
        String charName = DataOperations.longToString(DataOperations.stringLength12ToLong(userName));
        File file = new File(Config.MEDIA_DIR + File.separator + charName);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
        String folder = file.getPath() + File.separator;
        file = null;
        for (int suffix = 0; file == null || file.exists(); suffix++) {
            file = movie ? new File(folder + "movie" + suffix + ".mov") : new File(folder + "screenshot" + suffix + ".png");
        }
        return file;
    }
}
