package org.util;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class PersistenceManager {
    private static final XStream xstream = new XStream();

    static {
        addAlias("NPCDef", "org.entityhandling.defs.NPCDef");
        addAlias("ItemDef", "org.entityhandling.defs.ItemDef");
        addAlias("TextureDef", "org.entityhandling.defs.extras.TextureDef");
        addAlias("AnimationDef", "org.entityhandling.defs.extras.AnimationDef");
        addAlias("ItemDropDef", "org.entityhandling.defs.extras.ItemDropDef");
        addAlias("SpellDef", "org.entityhandling.defs.SpellDef");
        addAlias("PrayerDef", "org.entityhandling.defs.PrayerDef");
        addAlias("TileDef", "org.entityhandling.defs.TileDef");
        addAlias("DoorDef", "org.entityhandling.defs.DoorDef");
        addAlias("ElevationDef", "org.entityhandling.defs.ElevationDef");
        addAlias("GameObjectDef", "org.entityhandling.defs.GameObjectDef");
        addAlias("org.rscdaemon.spriteeditor.Sprite", "org.model.Sprite");
    }

    private static void addAlias(String name, String className) {
        try {
            xstream.alias(name, Class.forName(className));
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Object load(File file) {
        try {
            InputStream is = new GZIPInputStream(new FileInputStream(file));
            Object rv = xstream.fromXML(is);
            return rv;
        }
        catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return null;
    }

    public static void write(File file, Object o) {
        try {
            OutputStream os = new GZIPOutputStream(new FileOutputStream(file));
            xstream.toXML(o, os);
        }
        catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }
}
