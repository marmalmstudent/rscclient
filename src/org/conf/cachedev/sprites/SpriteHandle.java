package org.conf.cachedev.sprites;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.conf.cachedev.FileOperations;

public abstract class SpriteHandle implements SpriteHandleInterface {
	
	protected HashMap<Integer, String> names;
	protected Sprite sprite;
	protected int nbrSprites;
	
	public SpriteHandle(File f)
	{
		try {
			names = FileOperations.readHashMap(f, ";");
			nbrSprites = names.size();
		} catch(IOException e) {e.printStackTrace();}
	}
	
	public Sprite getSprite()
	{
		return sprite;
	}
	
	public int getNbrSprites()
	{
		return nbrSprites;
	}
	
	public HashMap<Integer, String> getSpriteNames()
	{
		return names;
	}
	
	public String getSpriteName(int modelID)
	{
		if (names.containsKey(modelID))
			return names.get(modelID);
		return null;
	}
	
	public int getSpriteID(String modelName)
	{
		for (Entry<Integer, String> entry : names.entrySet())
			if (entry.getValue().equals(modelName))
				return entry.getKey();
		return -1;
	}
	
	public void writePNG(File dst)
	{
		try {
			sprite.packDataPNG();
			FileOperations.writeImageAlpha(sprite.getData(), sprite.getWidth(),
					sprite.getHeight(), dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void writeDat(File dst)
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
		return names.toString();
	}
}
