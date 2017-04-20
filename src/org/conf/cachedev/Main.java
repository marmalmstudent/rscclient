package org.conf.cachedev;

import java.io.File;
import java.io.IOException;

public class Main {

	public static final String cacheFolder = "src/org/conf/client/";
	public static final String cacheDataFolder = "src/org/conf/client/data/";
	public static final String devFolder = "src/org/conf/cachedev/devtmp/";
	public static final String devFolderSprite = devFolder+"Sprites/";
	public static final String devFolderModels = devFolder+"models36/";
	public static final String devFolderLandscape = devFolder+"Landscape/";
	public static final String devFolderSound = devFolder+"sounds1/";
	public static final int MAX_SPRITES = 4000;
	public static final int MAX_MODELS = 500;
	
	public static void main(String args[])
	{
		onInit();
		/* Convert models to stl format. */
		try {
			workonModels();
		} catch (Exception e) { e.printStackTrace(); }
		Models model = new Models(new File("src/org/conf/cachedev/modelnames"));
		for (int i = 0; i < 500; ++i)
		{
			File src = new File(devFolderModels+Integer.toString(i));
			if (!src.exists())
				continue;
			model.newModel(src);
			model.saveSTL(new File("src/org/conf/cachedev/obj_stl/"+Integer.toString(i)+".stl"));
		}
		/* EOF. */
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
        	dir = new File(devFolderSprite); // Sprites (2D-stuff)
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderModels); // Models (3D-stuff)
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderLandscape); // Ground tiles
        	if (!dir.exists())
        		dir.mkdirs();
        	dir = new File(devFolderSound); // Sound effects
        	if (!dir.exists())
        		dir.mkdirs();
        	return true;
		} catch (Exception ex) { ex.printStackTrace(); }
		return false;
	}
	
	public static void workonSprites()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheFolder+"Sprites.zip"), devFolderSprite);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void workoffSprites()
	{
		try
		{
			FileOperations.zipArchive(devFolderSprite, new File(cacheFolder+"Sprites.zip"), MAX_SPRITES);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void workonModels()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheDataFolder+"models36.zip"), devFolderModels);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void workoffModels()
	{
		try
		{
			FileOperations.zipArchive(devFolderModels, new File(cacheDataFolder+"models36.zip"), MAX_MODELS);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void workonSounds()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheDataFolder+"sounds1.zip"), devFolderSound);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void workoffSounds()
	{
		int max_sounds = 50; // TODO: change to static variable in Models.java
		try
		{
			FileOperations.zipArchive(devFolderSound, new File(cacheDataFolder+"sounds1.zip"), max_sounds);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void testFromPNG()
	{
		int textureStart = Textures.TEXTURES_START;
		int textureLen = 500;
		Textures txr, txw;
		File srcr, srcw, dstr, dstw;
		for (int i = textureStart; i < textureStart + textureLen; ++i)
		{
			srcr = new File("src/org/conf/utils/sprites_img/"+Integer.toString(i)+".png");
			if (!srcr.exists())
				continue;
			System.out.printf("Processing file %d/%d, (%d%%)\n",
					i-textureStart, textureLen,
					100*(i-textureStart)/textureLen);
			txr = new Textures(Integer.toString(i));
			dstr = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".dat");
			txr.pngToDat(srcr, dstr, false);

			srcw = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".dat");
			if (!srcw.exists())
				continue;
			txw = new Textures(Integer.toString(i));
			dstw = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".png");
			txw.datToPNG( srcw, dstw, false);
		}
		int spritesStart = Sprites.SPRITES_START;
		int spritesLen = 3000;
		Sprites spr, spw;
		for (int i = spritesStart; i < spritesStart + spritesLen; ++i)
		{
			srcr = new File("src/org/conf/utils/sprites_img/"+Integer.toString(i)+".png");
			if (!srcr.exists())
				continue;
			System.out.printf("Processing file %d/%d, (%d%%)\n",
					i-spritesStart, spritesLen,
					100*(i-spritesStart)/spritesLen);
			spr = new Sprites(Integer.toString(i));
			dstr = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".dat");
			spr.pngToDat(srcr, dstr, false);

			srcw = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".dat");
			if (!srcw.exists())
				continue;
			spw = new Sprites(Integer.toString(i));
			dstw = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".png");
			spw.datToPNG( srcw, dstw, false);
		}
	}
}
