package org.conf.cachedev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JProgressBar;

public class CDControl
{

	public static final String cacheFolder = "src/org/conf/client/";
	public static final String cacheDataFolder = "src/org/conf/client/data/";
	public static final String devFolder = "src/org/conf/cachedev/dev/";
	public static final String devFolderSpriteNames = "src/org/conf/cachedev/spritenames";
	public static final String devFolderSprites = devFolder+"Sprites/";
	public static final String devFolderSpritesDat = devFolderSprites+"dat/";
	public static final String devFolderSpritesPNG = devFolderSprites+"png/";
	public static final String devFolderModelNames = "src/org/conf/cachedev/modelnames";
	public static final String devFolderModels = devFolder+"models36/";
	public static final String devFolderModelsOb3 = devFolderModels+"ob3/";
	public static final String devFolderModelsStl = devFolderModels+"stl/";
	public static final String devFolderLandscapes = devFolder+"Landscape/";
	public static final String devFolderSoundNames = "src/org/conf/cachedev/soundnames";
	public static final String devFolderSounds = devFolder+"sounds1/";
	public static final String devFolderSoundsPCM = devFolderSounds+"pcm/";
	public static final String devFolderSoundsWAV = devFolderSounds+"wav/";
	public static final int MAX_SPRITES = 4000;
	public static final int MAX_MODELS = 500;
	public static final int MAX_SOUNDS = 50;
	private Sprites sprite;
	private Models model;
	private Sounds sound;

	
	public static void main(String args[])
	{
		CDControl cc = new CDControl();
		cc.extractModels();
		cc.extractSprites();
		
		/*
		try {
			workonSprites();
			workoffSprites();
			
			workonModels();
			workoffModels();

			workonSounds();
			workoffSounds();
		} catch (Exception e) { e.printStackTrace(); }
		*/
	}
	
	public CDControl()
	{
		onInit();
		sprite = new Sprites(new File(devFolderSpriteNames));
		model = new Models(new File(devFolderModelNames));
		sound = new Sounds(new File(devFolderSoundNames));
	}
	
	public Sprites getSprites()
	{
		return sprite;
	}
	
	public Models getModels()
	{
		return model;
	}
	
	/**
	 * Initialized the cachedevelopment by creating the root development folder
	 * and subfolders. These should be located in the path specified by the
	 * static String devFolder (src/org/conf/cachedev/) 
	 * 
	 * @return true if all folders now exists.
	 */
	public static boolean onInit()
	{
		try
		{
        	File dir = new File(devFolder); // root folder
        	if (!dir.exists())
        		dir.mkdirs();
        	/* Sprites */
        	dir = new File(devFolderSprites); // Sprites (2D-stuff)
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderSpritesDat); // data files
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderSpritesPNG); // image files
        	if (!dir.exists())
        		dir.mkdirs();
        	/* 3D Models */
        	dir = new File(devFolderModels); // Models (3D-stuff)
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderModelsOb3); // data files
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderModelsStl); // models for blender/maya
        	if (!dir.exists())
        		dir.mkdirs();
        	/* Landscape */
        	dir = new File(devFolderLandscapes); // Ground tiles
        	if (!dir.exists())
        		dir.mkdirs();
        	/* Sounds */
        	dir = new File(devFolderSounds); // Sound effects
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderSoundsPCM); // data files
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderSoundsWAV); // wave files
        	if (!dir.exists())
        		dir.mkdirs();
        	return true;
		} catch (Exception ex) { ex.printStackTrace(); }
		return false;
	}
	
	public void workonSprites()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheFolder+"Sprites.zip"), devFolderSpritesDat, sprite.getSpriteNames());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void workoffSprites()
	{
		try
		{
			FileOperations.zipArchive(devFolderSpritesDat, new File(cacheFolder+"Sprites.zip"), MAX_SPRITES);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void workonModels()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheDataFolder+"models36.zip"), devFolderModelsOb3, model.getModelNames());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void workoffModels()
	{
		try
		{
			FileOperations.zipArchive(devFolderModelsOb3, new File(cacheDataFolder+"models36.zip"), MAX_MODELS);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void workonSounds()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheDataFolder+"sounds1.zip"), devFolderSoundsPCM, sound.getSoundNames());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void workoffSounds()
	{
		try
		{
			FileOperations.zipArchive(devFolderSoundsPCM, new File(cacheDataFolder+"sounds1.zip"), MAX_SOUNDS);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	/**
	 * Convert sprites to png format.
	 */
	public void extractSprites()
	{
		HashMap<Integer, String> spriteNames = sprite.getSpriteNames();
		for (Entry<Integer, String> entry : spriteNames.entrySet())
		{
			File src = new File(devFolderSpritesDat+entry.getValue());
			if (!src.exists())
				continue;
			if (entry.getValue().startsWith("sprite"))
				sprite.newObjectDat(src);
			else if (entry.getValue().startsWith("texture"))
				sprite.newTextureDat(src);
			sprite.writePNG(new File(devFolderSpritesPNG+entry.getValue()+".png"));
		}
	}
	
	/**
	 * Convert models from ob3 to stl format.
	 */
	public void extractModels()
	{
		HashMap<Integer, String> modelNames = model.getModelNames();
		for (Entry<Integer, String> entry : modelNames.entrySet())
		{
			File src = new File(devFolderModelsOb3+entry.getValue());
			if (!src.exists())
				continue;
			model.newModel(src);
			model.saveSTL(new File(devFolderModelsStl+entry.getValue()+".stl"));
		}
	}

	/**
	 * Converts sounds from pcm to wav format.
	 */
	public void extractSounds()
	{
		HashMap<Integer, String> soundNames = sound.getSoundNames();
		for (Entry<Integer, String> entry : soundNames.entrySet())
		{
			File src = new File(devFolderSoundsPCM+entry.getValue());
			if (!src.exists())
				continue;
			sound.newPCM(src);
			sound.saveWAV(new File(devFolderModelsStl+entry.getValue()+".wav"));
		}
	}
	
	/**
	 * Converts sounds from wav to pcm format.
	 */
	public void insertSounds()
	{
		HashMap<Integer, String> soundNames = sound.getSoundNames();
		for (Entry<Integer, String> entry : soundNames.entrySet())
		{
			File src = new File(devFolderSoundsWAV+entry.getValue()+".wav");
			if (!src.exists())
				continue;
			sound.newWAV(src);
			sound.savePCM(new File(devFolderSoundsPCM+entry.getValue()));
		}
	}
}
