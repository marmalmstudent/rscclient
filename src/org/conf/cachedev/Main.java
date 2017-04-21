package org.conf.cachedev;

import java.io.File;
import java.io.IOException;

public class Main {

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
	public static final String devFolderSounds = devFolder+"sounds1/";
	public static final String devFolderSoundsPCM = devFolderSounds+"pcm/";
	public static final String devFolderSoundsWAV = devFolderSounds+"wav/";
	public static final int MAX_SPRITES = 4000;
	public static final int MAX_MODELS = 500;
	public static final int MAX_SOUNDS = 50;
	
	public static void main(String args[])
	{
		onInit();
		/* Convert models to stl format. */
		try {
			workonModels();
			workoffModels();
		} catch (Exception e) { e.printStackTrace(); }
		
		Models model = new Models(new File(devFolderModelNames));
		for (int i = 0; i < MAX_MODELS; ++i)
		{
			File src = new File(devFolderModelsOb3+Integer.toString(i));
			if (!src.exists())
				continue;
			model.newModel(src);
			model.saveSTL(new File(devFolderModelsStl+model.getModelName(i)+".stl"));
		}
		/* Convert sprites to png format. */
		try {
			workonSprites();
			workoffSprites();
		} catch (Exception e) { e.printStackTrace(); }
		
		Sprites sprite = new Sprites(new File(devFolderSpriteNames));
		for (int i = 0; i < TextureSprite.TEXTURES_START; ++i)
		{
			File src = new File(devFolderSpritesDat+Integer.toString(i));
			if (!src.exists())
				continue;
			sprite.newCharacterDat(src);
			sprite.writePNG(new File(devFolderSpritesPNG+sprite.getSpriteName(i)+".png"));
		}
		for (int i = TextureSprite.TEXTURES_START; i < MAX_SPRITES; ++i)
		{
			File src = new File(devFolderSpritesDat+Integer.toString(i));
			if (!src.exists())
				continue;
			sprite.newTextureDat(src);
			sprite.writePNG(new File(devFolderSpritesPNG+sprite.getSpriteName(i)+".png"));
		}
		
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
	
	public static void workonSprites()
	{
		try
		{
			FileOperations.unzipArchive(new File(cacheFolder+"Sprites.zip"), devFolderSpritesDat);
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
			FileOperations.zipArchive(devFolderSpritesDat, new File(cacheFolder+"Sprites.zip"), MAX_SPRITES);
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
			FileOperations.unzipArchive(new File(cacheDataFolder+"models36.zip"), devFolderModelsOb3);
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
			FileOperations.zipArchive(devFolderModelsOb3, new File(cacheDataFolder+"models36.zip"), MAX_MODELS);
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
			FileOperations.unzipArchive(new File(cacheDataFolder+"sounds1.zip"), devFolderSoundsPCM);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void workoffSounds()
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
	/*
	public static void testFromPNG()
	{
		int textureStart = TextureSprites.TEXTURES_START;
		int textureLen = 500;
		TextureSprites txr, txw;
		File srcr, srcw, dstr, dstw;
		for (int i = textureStart; i < textureStart + textureLen; ++i)
		{
			srcr = new File("src/org/conf/utils/sprites_img/"+Integer.toString(i)+".png");
			if (!srcr.exists())
				continue;
			System.out.printf("Processing file %d/%d, (%d%%)\n",
					i-textureStart, textureLen,
					100*(i-textureStart)/textureLen);
			txr = new TextureSprites(Integer.toString(i));
			dstr = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".dat");
			txr.pngToDat(srcr, dstr, false);

			srcw = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".dat");
			if (!srcw.exists())
				continue;
			txw = new TextureSprites(Integer.toString(i));
			dstw = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".png");
			txw.datToPNG( srcw, dstw, false);
		}
		int spritesStart = CharacterSprites.SPRITES_START;
		int spritesLen = 3000;
		CharacterSprites spr, spw;
		for (int i = spritesStart; i < spritesStart + spritesLen; ++i)
		{
			srcr = new File("src/org/conf/utils/sprites_img/"+Integer.toString(i)+".png");
			if (!srcr.exists())
				continue;
			System.out.printf("Processing file %d/%d, (%d%%)\n",
					i-spritesStart, spritesLen,
					100*(i-spritesStart)/spritesLen);
			spr = new CharacterSprites(Integer.toString(i));
			dstr = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".dat");
			spr.pngToDat(srcr, dstr, false);

			srcw = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".dat");
			if (!srcw.exists())
				continue;
			spw = new CharacterSprites(Integer.toString(i));
			dstw = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".png");
			spw.datToPNG( srcw, dstw, false);
		}
	}*/
}
