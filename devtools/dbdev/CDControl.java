package org.conf.cachedev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class CDControl
{
	private SpriteHandle entity, media, util, item,
	logo, projectile, texture;
	private ModelHandle model;
	private LandscapeHandle landscape;
	private SoundHandle sound;
	
	public CDControl()
	{
		makdeDevDirs();
		entity = new SpriteHandle(new File(CDConst.EntityNames));
		media = new SpriteHandle(new File(CDConst.MediaNames));
		util = new SpriteHandle(new File(CDConst.UtilNames));
		item = new SpriteHandle(new File(CDConst.ItemNames));
		logo = new SpriteHandle(new File(CDConst.LogoNames));
		projectile = new SpriteHandle(new File(CDConst.ProjectileNames));
		texture = new SpriteHandle(new File(CDConst.TextureNames));
		model = new ModelHandle(new File(CDConst.ModelNames));
		landscape = new LandscapeHandle(new File(CDConst.LandscapeNames));
		sound = new SoundHandle(new File(CDConst.SoundNames));
	}
	
	public SpriteHandle getEntity() { return entity; }
	public SpriteHandle getMedia() { return media; }
	public SpriteHandle getUtil() { return util; }
	public SpriteHandle getItem() { return item; }
	public SpriteHandle getLogo() { return logo; }
	public SpriteHandle getProjectile() { return projectile; }
	public SpriteHandle getTexture() { return texture; }
	public ModelHandle getModels() { return model; }
	public LandscapeHandle getLandscape() { return landscape; }
	public SoundHandle getSounds() { return sound; }
	
	private static void makeSpriteDirs() throws Exception
	{
		FileOperations.mkdir(new File(CDConst.SpriteDir)); // Sprites (2D-stuff)
		FileOperations.mkdir(new File(CDConst.EntityDir)); // Entity/Animated stuff
		FileOperations.mkdir(new File(CDConst.EntityDatDir));
		FileOperations.mkdir(new File(CDConst.EntityPNGDir));
		FileOperations.mkdir(new File(CDConst.MediaDir)); // interface stuff
		FileOperations.mkdir(new File(CDConst.MediaDatDir));
		FileOperations.mkdir(new File(CDConst.MediaPNGDir));
		FileOperations.mkdir(new File(CDConst.UtilDir)); // random stuff
		FileOperations.mkdir(new File(CDConst.UtilDatDir));
		FileOperations.mkdir(new File(CDConst.UtilPNGDir));
		FileOperations.mkdir(new File(CDConst.ItemDir)); // items in inventory etc.
		FileOperations.mkdir(new File(CDConst.ItemDatDir));
		FileOperations.mkdir(new File(CDConst.ItemPNGDir));
		FileOperations.mkdir(new File(CDConst.LogoDir));
		FileOperations.mkdir(new File(CDConst.LogoDatDir));
		FileOperations.mkdir(new File(CDConst.LogoPNGDir));
		FileOperations.mkdir(new File(CDConst.ProjectileDir));
		FileOperations.mkdir(new File(CDConst.ProjectileDatDir));
		FileOperations.mkdir(new File(CDConst.ProjectilePNGDir));
		FileOperations.mkdir(new File(CDConst.TextureDir));
		FileOperations.mkdir(new File(CDConst.TextureDatDir));
		FileOperations.mkdir(new File(CDConst.TexturePNGDir));
	}
	
	private static void makeModelDirs() throws Exception
	{
		FileOperations.mkdir(new File(CDConst.ModelsDir)); // Models (3D-stuff)
		FileOperations.mkdir(new File(CDConst.ModelsOb3Dir)); // data files
		FileOperations.mkdir(new File(CDConst.ModelsStlDir)); // models for blender/maya
	}
	
	private static void makeLandscapeDirs() throws Exception
	{
		FileOperations.mkdir(new File(CDConst.LandscapesDir)); // Ground tiles
		FileOperations.mkdir(new File(CDConst.LandscapesHeiDir));
		FileOperations.mkdir(new File(CDConst.LandscapesStlDir));
	}
	
	private static void makeSoundDirs() throws Exception
	{
		FileOperations.mkdir(new File(CDConst.SoundsDir)); // Sound effects
		FileOperations.mkdir(new File(CDConst.SoundsPCMDir)); // data files
		FileOperations.mkdir(new File(CDConst.SoundsWAVDir)); // wave files
	}
	/**
	 * Initialized the cachedevelopment by creating the root development folder
	 * and subfolders. These should be located in the path specified by the
	 * static String devFolder (src/org/conf/cachedev/) 
	 * 
	 * @return true if all folders now exists.
	 */
	public static boolean makdeDevDirs()
	{
		try
		{
        	File dir = new File(CDConst.devDir); // root folder
        	if (!dir.exists())
        		dir.mkdirs();
        	/* Sprites */
        	makeSpriteDirs();
        	/* 3D Models */
        	makeModelDirs();
        	/* Landscape */
        	makeLandscapeDirs();
        	/* Sounds */
        	makeSoundDirs();
        	return true;
		} catch (Exception ex) { ex.printStackTrace(); }
		return false;
	}
	
	public void workon(String archivePath, String datDir,
			HashMap<String, String> names)
	{
		try {
			FileOperations.unzipArchive(
					new File(archivePath), datDir, names);
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoff(String archivePath, String datDir,
			HashMap<String, String> names) {
		try {
			FileOperations.zipArchive(datDir,
					new File(archivePath), names);
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void extractSprite(
			SpriteHandle handle, int transparentMask,
			String datDir, String pngDir)
	{
		HashMap<String, String> entityNames = handle.getSpriteNames();
		for (Entry<String, String> entry : entityNames.entrySet())
		{
			File src = new File(datDir+entry.getValue());
			if (!src.exists())
				continue;
			handle.newDat(src, transparentMask);
			handle.writePNG(new File(pngDir+entry.getValue()+".png"));
		}
	}
	
	/**
	 * Convert models from ob3 to stl format.
	 */
	public void extractModels()
	{
		HashMap<String, String> modelNames = model.getModelNames();
		for (Entry<String, String> entry : modelNames.entrySet())
		{
			File src = new File(CDConst.ModelsOb3Dir+entry.getValue());
			if (!src.exists())
				continue;
			model.newModel(src);
			model.saveSTL(new File(CDConst.ModelsStlDir+entry.getValue()+".stl"));
		}
	}
	
	/**
	 * Convert models from hei to stl format.
	 */
	public void extractLandscapes()
	{
		HashMap<String, String> landscapeNames = landscape.getLandscapeNames();
		for (Entry<String, String> entry : landscapeNames.entrySet())
		{
			File src = new File(CDConst.LandscapesHeiDir+entry.getValue());
			if (!src.exists())
				continue;
			landscape.newLandscape(src);
			landscape.saveSTL(new File(CDConst.LandscapesStlDir+entry.getValue()+".stl"));
		}
	}

	/**
	 * Converts sounds from pcm to wav format.
	 */
	public void extractSounds()
	{
		HashMap<String, String> soundNames = sound.getSoundNames();
		for (Entry<String, String> entry : soundNames.entrySet())
		{
			File src = new File(CDConst.SoundsPCMDir+entry.getValue());
			if (!src.exists())
				continue;
			sound.newPCM(src);
			sound.saveWAV(new File(CDConst.SoundsWAVDir+entry.getValue()+".wav"));
		}
	}
	
	public void insertSprite(SpriteHandle handle, String datDir,
			File src, boolean requiresShift, int xShift,
			int yShift, int totalWidth, int totalHeight,
			int transparentMask)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		handle.newPNG(src, requiresShift, xShift, yShift,
				totalWidth, totalHeight, transparentMask);
		handle.writeDat(new File(datDir+name));
	}
	
	/**
	 * Converts sounds from wav to pcm format.
	 */
	public void insertSounds()
	{
		HashMap<String, String> soundNames = sound.getSoundNames();
		for (Entry<String, String> entry : soundNames.entrySet())
		{
			File src = new File(CDConst.SoundsWAVDir+entry.getValue()+".wav");
			if (!src.exists())
				continue;
			sound.newWAV(src);
			sound.savePCM(new File(CDConst.SoundsPCMDir+entry.getValue()));
		}
	}
}
