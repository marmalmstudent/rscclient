package org.conf.cachedev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.conf.cachedev.models.Models;
import org.conf.cachedev.sprites.EntityHandle;
import org.conf.cachedev.sprites.ItemHandle;
import org.conf.cachedev.sprites.LogoHandle;
import org.conf.cachedev.sprites.MediaHandle;
import org.conf.cachedev.sprites.ProjectileHandle;
import org.conf.cachedev.sprites.TextureHandle;
import org.conf.cachedev.sprites.UtilHandle;

public class CDControl
{
	private EntityHandle entity;
	private MediaHandle media;
	private UtilHandle util;
	private ItemHandle item;
	private LogoHandle logo;
	private ProjectileHandle projectile;
	private TextureHandle texture;
	private Models model;
	private Sounds sound;
	
	public CDControl()
	{
		makdeDevDirs();
		entity = new EntityHandle(new File(CDConst.EntityNames));
		media = new MediaHandle(new File(CDConst.MediaNames));
		util = new UtilHandle(new File(CDConst.UtilNames));
		item = new ItemHandle(new File(CDConst.ItemNames));
		logo = new LogoHandle(new File(CDConst.LogoNames));
		projectile = new ProjectileHandle(new File(CDConst.ProjectileNames));
		texture = new TextureHandle(new File(CDConst.TextureNames));
		model = new Models(new File(CDConst.ModelNames));
		sound = new Sounds(new File(CDConst.SoundNames));
	}
	
	public EntityHandle getEntity()
	{
		return entity;
	}
	
	public MediaHandle getMedia()
	{
		return media;
	}
	
	public UtilHandle getUtil()
	{
		return util;
	}
	
	public ItemHandle getItem()
	{
		return item;
	}
	
	public LogoHandle getLogo()
	{
		return logo;
	}
	
	public ProjectileHandle getProjectile()
	{
		return projectile;
	}
	
	public TextureHandle getTexture()
	{
		return texture;
	}
	
	public Models getModels()
	{
		return model;
	}
	
	private static void makeSpriteDirs() throws Exception
	{
		File dir = new File(CDConst.SpriteDir); // Sprites (2D-stuff)
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.EntityDir); // Entity/Animated stuff
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.EntityDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.EntityPNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	
    	dir = new File(CDConst.MediaDir); // interface stuff
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.MediaDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.MediaPNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	
    	dir = new File(CDConst.UtilDir); // random stuff
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.UtilDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.UtilPNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	
    	dir = new File(CDConst.ItemDir); // items in inventory etc.
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.ItemDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.ItemPNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	
    	dir = new File(CDConst.LogoDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.LogoDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.LogoPNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	
    	dir = new File(CDConst.ProjectileDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.ProjectileDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.ProjectilePNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	
    	dir = new File(CDConst.TextureDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.TextureDatDir);
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.TexturePNGDir);
    	if (!dir.exists())
    		dir.mkdirs();
	}
	
	private static void makeModelDirs() throws Exception
	{
		File dir = new File(CDConst.ModelsDir); // Models (3D-stuff)
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.ModelsOb3Dir); // data files
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.ModelsStlDir); // models for blender/maya
    	if (!dir.exists())
    		dir.mkdirs();
	}
	
	private static void makeLandscapeDirs() throws Exception
	{
    	File dir = new File(CDConst.LandscapesDir); // Ground tiles
    	if (!dir.exists())
    		dir.mkdirs();
	}
	
	private static void makeSoundDirs() throws Exception
	{
    	File dir = new File(CDConst.SoundsDir); // Sound effects
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.SoundsPCMDir); // data files
    	if (!dir.exists())
    		dir.mkdirs();
    	dir = new File(CDConst.SoundsWAVDir); // wave files
    	if (!dir.exists())
    		dir.mkdirs();
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
	
	public void workonEntity() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Entity.zip"),
					CDConst.EntityDatDir, entity.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffEntity() {
		try {
			FileOperations.zipArchive(CDConst.EntityDatDir,
					new File(CDConst.cacheSpriteDir+"Entity.zip"),
					entity.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonItem() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Item.zip"),
					CDConst.ItemDatDir, item.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffItem() {
		try {
			FileOperations.zipArchive(CDConst.ItemDatDir,
					new File(CDConst.cacheSpriteDir+"Item.zip"),
					item.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonLogo() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Logo.zip"),
					CDConst.LogoDatDir, logo.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffLogo() {
		try {
			FileOperations.zipArchive(CDConst.LogoDatDir,
					new File(CDConst.cacheSpriteDir+"Logo.zip"),
					logo.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonMedia() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Media.zip"),
					CDConst.MediaDatDir, media.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffMedia() {
		try {
			FileOperations.zipArchive(CDConst.MediaDatDir,
					new File(CDConst.cacheSpriteDir+"Media.zip"),
					media.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonProjectile() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Projectile.zip"),
					CDConst.ProjectileDatDir,
					projectile.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffProjectile() {
		try {
			FileOperations.zipArchive(CDConst.ProjectileDatDir,
					new File(CDConst.cacheSpriteDir+"Projectile.zip"),
					projectile.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonTexture() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Texture.zip"),
					CDConst.TextureDatDir, texture.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffTexture() {
		try {
			FileOperations.zipArchive(CDConst.TextureDatDir,
					new File(CDConst.cacheSpriteDir+"Texture.zip"),
					texture.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonUtil() {
		try {
			FileOperations.unzipArchive(
					new File(CDConst.cacheSpriteDir+"Util.zip"),
					CDConst.UtilDatDir, util.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffUtil() {
		try {
			FileOperations.zipArchive(CDConst.UtilDatDir,
					new File(CDConst.cacheSpriteDir+"Util.zip"),
					util.getSpriteNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonModels() {
		try {
			FileOperations.unzipArchive(new File(CDConst.cacheDataDir+"models36.zip"), CDConst.ModelsOb3Dir, model.getModelNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffModels() {
		try {
			FileOperations.zipArchive(CDConst.ModelsOb3Dir, new File(CDConst.cacheDataDir+"models36.zip"), model.getModelNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workonSounds() {
		try {
			FileOperations.unzipArchive(new File(CDConst.cacheDataDir+"sounds1.zip"), CDConst.SoundsPCMDir, sound.getSoundNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void workoffSounds() {
		try {
			FileOperations.zipArchive(CDConst.SoundsPCMDir, new File(CDConst.cacheDataDir+"sounds1.zip"), sound.getSoundNames());
		} catch(IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void extractEntity()
	{
		HashMap<Integer, String> entityNames = entity.getSpriteNames();
		for (Entry<Integer, String> entry : entityNames.entrySet())
		{
			File src = new File(CDConst.EntityDatDir+entry.getValue());
			if (!src.exists())
				continue;
			entity.newDat(src);
			entity.writePNG(new File(CDConst.EntityPNGDir+entry.getValue()+".png"));
		}
	}
	
	public void extractItem()
	{
		HashMap<Integer, String> itemNames = item.getSpriteNames();
		for (Entry<Integer, String> entry : itemNames.entrySet())
		{
			File src = new File(CDConst.ItemDatDir+entry.getValue());
			if (!src.exists())
				continue;
			item.newDat(src);
			item.writePNG(new File(CDConst.ItemPNGDir+entry.getValue()+".png"));
		}
	}
	
	public void extractLogo()
	{
		HashMap<Integer, String> logoNames = logo.getSpriteNames();
		for (Entry<Integer, String> entry : logoNames.entrySet())
		{
			File src = new File(CDConst.LogoDatDir+entry.getValue());
			if (!src.exists())
				continue;
			logo.newDat(src);
			logo.writePNG(new File(CDConst.LogoPNGDir+entry.getValue()+".png"));
		}
	}
	
	public void extractMedia()
	{
		HashMap<Integer, String> mediaNames = media.getSpriteNames();
		for (Entry<Integer, String> entry : mediaNames.entrySet())
		{
			File src = new File(CDConst.MediaDatDir+entry.getValue());
			if (!src.exists())
				continue;
			media.newDat(src);
			media.writePNG(new File(CDConst.MediaPNGDir+entry.getValue()+".png"));
		}
	}
	
	public void extractProjectile()
	{
		HashMap<Integer, String> projectileNames = projectile.getSpriteNames();
		for (Entry<Integer, String> entry : projectileNames.entrySet())
		{
			File src = new File(CDConst.ProjectileDatDir+entry.getValue());
			if (!src.exists())
				continue;
			projectile.newDat(src);
			projectile.writePNG(new File(CDConst.ProjectilePNGDir+entry.getValue()+".png"));
		}
	}
	
	public void extractTexture()
	{
		HashMap<Integer, String> textureNames = texture.getSpriteNames();
		for (Entry<Integer, String> entry : textureNames.entrySet())
		{
			File src = new File(CDConst.TextureDatDir+entry.getValue());
			if (!src.exists())
				continue;
			texture.newDat(src);
			texture.writePNG(new File(CDConst.TexturePNGDir+entry.getValue()+".png"));
		}
	}
	
	public void extractUtil()
	{
		HashMap<Integer, String> utilNames = util.getSpriteNames();
		for (Entry<Integer, String> entry : utilNames.entrySet())
		{
			File src = new File(CDConst.UtilDatDir+entry.getValue());
			if (!src.exists())
				continue;
			util.newDat(src);
			util.writePNG(new File(CDConst.UtilPNGDir+entry.getValue()+".png"));
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
			File src = new File(CDConst.ModelsOb3Dir+entry.getValue());
			if (!src.exists())
				continue;
			model.newModel(src);
			model.saveSTL(new File(CDConst.ModelsStlDir+entry.getValue()+".stl"));
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
			File src = new File(CDConst.SoundsPCMDir+entry.getValue());
			if (!src.exists())
				continue;
			sound.newPCM(src);
			sound.saveWAV(new File(CDConst.SoundsWAVDir+entry.getValue()+".wav"));
		}
	}
	
	public void insertEntity(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		entity.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		entity.writeDat(new File(CDConst.EntityDatDir+name));
	}
	
	public void insertItem(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		item.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		item.writeDat(new File(CDConst.ItemDatDir+name));
	}
	
	public void insertLogo(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		logo.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		logo.writeDat(new File(CDConst.LogoDatDir+name));
	}
	
	public void insertMedia(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		media.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		media.writeDat(new File(CDConst.MediaDatDir+name));
	}
	
	public void insertProjectile(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		projectile.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		projectile.writeDat(new File(CDConst.ProjectileDatDir+name));
	}
	
	public void insertTexture(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		texture.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		texture.writeDat(new File(CDConst.TextureDatDir+name));
	}
	
	public void insertUtil(
			File src, boolean requiresShift, int xShift,
			int yShift, int cameraAngle1, int cameraAngle2)
	{
		if (!src.exists())
			return;
		String name = FileOperations.getFileName(src, ".png").toLowerCase();
		util.newPNG(src, requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		util.writeDat(new File(CDConst.UtilDatDir+name));
	}
	
	/**
	 * Converts sounds from wav to pcm format.
	 */
	public void insertSounds()
	{
		HashMap<Integer, String> soundNames = sound.getSoundNames();
		for (Entry<Integer, String> entry : soundNames.entrySet())
		{
			File src = new File(CDConst.SoundsWAVDir+entry.getValue()+".wav");
			if (!src.exists())
				continue;
			sound.newWAV(src);
			sound.savePCM(new File(CDConst.SoundsPCMDir+entry.getValue()));
		}
	}
}
