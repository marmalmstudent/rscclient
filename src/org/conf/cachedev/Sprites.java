package org.conf.cachedev;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class Sprites {
	
	private HashMap<Integer, String> spriteNames;
	private Sprite sprite;
	
	public Sprites(File f)
	{
		try {
			spriteNames = FileOperations.readHashMap(f, ";");
		} catch(IOException e) {e.printStackTrace();}
	}
	
	public String getSpriteName(int modelID)
	{
		if (spriteNames.containsKey(modelID))
			return spriteNames.get(modelID);
		return null;
	}
	
	public int getSpriteID(String modelName)
	{
		for (Entry<Integer, String> entry : spriteNames.entrySet())
			if (entry.getValue().equals(modelName))
				return entry.getKey();
		return -1;
	}
	
	public void newTexturePNG(File f)
	{
		try
		{
			Dimension d = FileOperations.getImageDimension(f);
			int width = d.width;
			int height = d.height;
			int[] pixelData = FileOperations.readImage(f, width, height);
			sprite = new Sprite(pixelData, width, height,
					false, 0, 0, width, height);
		} catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void newOtherPNG(File f, boolean requiresShift,
			int xShift, int yShift,
			int cameraAngle1, int cameraAngle2)
	{
		try
		{
			Dimension d = FileOperations.getImageDimension(f);
			int width = d.width;
			int height = d.height;
			int[] pixelData = FileOperations.readImage(f, width, height);
			sprite = new Sprite(pixelData, width, height,
					requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		} catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void newTextureDat(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sprite = new TextureSprite(data);
		} catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	public void newCharacterDat(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sprite = new CharacterSprite(data);
		} catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	protected void writePNG(File dst)
	{
		try {
			sprite.packDataPNG();
			FileOperations.writeImageAlpha(sprite.getData(), sprite.getWidth(),
					sprite.getHeight(), dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void writeDat(File dst)
	{
		sprite.packDataDat();
		try {
			FileOperations.write(sprite.getData(), dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public String toString()
	{
		return spriteNames.toString();
	}
}
