package org.entityhandling;

import org.entityhandling.defs.DoorDef;
import org.entityhandling.defs.ElevationDef;
import org.entityhandling.defs.GameObjectDef;
import org.entityhandling.defs.ItemDef;
import org.entityhandling.defs.NPCDef;
import org.entityhandling.defs.PrayerDef;
import org.entityhandling.defs.SpellDef;
import org.entityhandling.defs.TileDef;
import org.entityhandling.defs.extras.AnimationDef;
import org.entityhandling.defs.extras.TextureDef;
import org.util.Config;
import org.util.ParseXML;
import org.util.PersistenceManager;

import java.io.File;
import java.util.ArrayList;

/**
 * This class handles the loading of entities from the conf files,
 * and provides methods for relaying these entities to the user.
 */
public class EntityHandler {

    private static NPCDef[] npcs;
    private static ItemDef[] items;
    private static TextureDef[] textures;
    private static AnimationDef[] animations;
    private static SpellDef[] spells;
    private static PrayerDef[] prayers;
    private static TileDef[] tiles;
    private static DoorDef[] doors;
    private static ElevationDef[] elevation;
    private static GameObjectDef[] objects;

    private static ArrayList<String> models = new ArrayList<String>();
    private static int invPictureCount = 0;

    public static int getModelCount() {
        return models.size();
    }

    public static String getModelName(int id) {
        if (id < 0 || id >= models.size()) {
            return null;
        }
        return models.get(id);
    }

    public static int invPictureCount() {
        return invPictureCount;
    }

    public static int npcCount() {
        return npcs.length;
    }

    public static NPCDef getNpcDef(int id) {
        if (id < 0 || id >= npcs.length) {
            return null;
        }
        return npcs[id];
    }

    public static int itemCount() {
        return items.length;
    }

    public static ItemDef getItemDef(int id) {
        if (id < 0 || id >= items.length) {
            return null;
        }
        return items[id];
    }

    public static int textureCount() {
        return textures.length;
    }

    public static TextureDef getTextureDef(int id) {
        if (id < 0 || id >= textures.length) {
            return null;
        }
        return textures[id];
    }

    public static int animationCount() {
        return animations.length;
    }

    public static AnimationDef getAnimationDef(int id) {
        if (id < 0 || id >= animations.length) {
            return null;
        }
        return animations[id];
    }

    public static int spellCount() {
        return spells.length;
    }

    public static SpellDef getSpellDef(int id)
    {
        if (id < 0 || id >= spells.length)
        {
            return null;
        }
        return spells[id];
    }

    public static int prayerCount() {
        return prayers.length;
    }

    public static PrayerDef getPrayerDef(int id) {
        if (id < 0 || id >= prayers.length) {
            return null;
        }
        return prayers[id];
    }

    public static int tileCount() {
        return tiles.length;
    }

    public static TileDef getTileDef(int id) {
        if (id < 0 || id >= tiles.length) {
            return null;
        }
        return tiles[id];
    }

    public static int doorCount() {
        return doors.length;
    }

    public static DoorDef getDoorDef(int id) {
        if (id < 0 || id >= doors.length) {
            return null;
        }
        return doors[id];
    }

    public static int elevationCount() {
        return elevation.length;
    }

    public static ElevationDef getElevationDef(int id) {
        if (id < 0 || id >= elevation.length) {
            return null;
        }
        return elevation[id];
    }

    public static int objectCount() {
        return objects.length;
    }

    public static GameObjectDef getObjectDef(int id) {
        if (id < 0 || id >= objects.length) {
            return null;
        }
        return objects[id];
    }

    public static void load()
    {
		ParseXML pxml = new ParseXML();
		animations = pxml.unpackAnimationDef(new File(Config.CONF_DIR + "Animations"));
		doors = pxml.unpackDoorDef(new File(Config.CONF_DIR + "Doors"));
		elevation = pxml.unpackElevationDef(new File(Config.CONF_DIR + "Elevation"));
		items = pxml.unpackItemDef(new File(Config.CONF_DIR + "Items"));
		npcs = pxml.unpackNPCDef(new File(Config.CONF_DIR + "NPCs"));
		objects = pxml.unpackGameObjectDef(new File(Config.CONF_DIR + "Objects"));
		prayers = pxml.unpackPrayerDef(new File(Config.CONF_DIR + "Prayers"));
		spells = pxml.unpackSpellDef(new File(Config.CONF_DIR + "Spells"));
		textures = pxml.unpackTextureDef(new File(Config.CONF_DIR + "Textures"));
		tiles = pxml.unpackTileDef(new File(Config.CONF_DIR + "Tiles"));
		/*
    	npcs = (NPCDef[]) PersistenceManager.load(Config.CONF_DIR + "NPCs", 4);
    	items = (ItemDef[]) PersistenceManager.load(Config.CONF_DIR + "Items", 3);
    	textures = (TextureDef[]) PersistenceManager.load(Config.CONF_DIR + "Textures", 8);
    	animations = (AnimationDef[]) PersistenceManager.load(Config.CONF_DIR + "Animations", 0);
    	spells = (SpellDef[]) PersistenceManager.load(Config.CONF_DIR + "Spells", 7);
    	prayers = (PrayerDef[]) PersistenceManager.load(Config.CONF_DIR + "Prayers", 6);
    	tiles = (TileDef[]) PersistenceManager.load(Config.CONF_DIR + "Tiles", 9);
    	doors = (DoorDef[]) PersistenceManager.load(Config.CONF_DIR + "Doors", 1);
    	elevation = (ElevationDef[]) PersistenceManager.load(Config.CONF_DIR + "Elevation", 2);
    	objects = (GameObjectDef[]) PersistenceManager.load(Config.CONF_DIR + "Objects", 5);
    	*//*
    	npcs = (NPCDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "NPCs.rscd"));
    	items = (ItemDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Items.rscd"));
    	textures = (TextureDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Textures.rscd"));
    	animations = (AnimationDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Animations.rscd"));
    	spells = (SpellDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Spells.rscd"));
    	prayers = (PrayerDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Prayers.rscd"));
    	tiles = (TileDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Tiles.rscd"));
    	doors = (DoorDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Doors.rscd"));
    	elevation = (ElevationDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Elevation.rscd"));
    	objects = (GameObjectDef[]) PersistenceManager.load(new File(Config.CONF_DIR, "Objects.rscd"));
    	*/
    	for (int id = 0; id < items.length; id++) {
    		if (items[id].getSprite() + 1 > invPictureCount) {
    			invPictureCount = items[id].getSprite() + 1;
    		}
    	}

    	for (int id = 0; id < objects.length; id++) {
    		objects[id].modelID = storeModel(objects[id].getObjectModel());
    	}
    }

    public static int storeModel(String name) {
        if (name.equalsIgnoreCase("na")) {
            return 0;
        }
        int index = models.indexOf(name);
        if (index < 0) {
            models.add(name);
            return models.size() - 1;
        }
        return index;
    }

}
