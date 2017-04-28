package dbdev;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class SpriteHandle {
	
	protected HashMap<String, String> names;
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
	
	public HashMap<String, String> getSpriteNames()
	{
		return names;
	}
	
	public String getSpriteName(int modelID)
	{
		if (names.containsKey(modelID))
			return names.get(modelID);
		return null;
	}
	
	public String getSpriteID(String modelName)
	{
		for (Entry<String, String> entry : names.entrySet())
			if (entry.getValue().equals(modelName))
				return entry.getKey();
		return null;
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

	public void newDat(File f, int transparentMask)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sprite = new Sprite(data, transparentMask);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public void newPNG(File f, boolean requiresShift,
			int xShift, int yShift, int totalWidth, int totalHeight,
			int transparentMask)
	{
		try
		{
			Dimension d = FileOperations.getImageDimension(f);
			int width = d.width;
			int height = d.height;
			int[] pixelData = FileOperations.readImage(f, width, height);
			if (requiresShift)
				sprite = new Sprite(pixelData, width, height,
						false, 0, 0, width, height, transparentMask);
			else
			{
				if (totalWidth < width+xShift)
					totalWidth = width+xShift;
				if (totalHeight < height+yShift)
					totalHeight = height+yShift;
				sprite = new Sprite(pixelData, width, height,
						true, xShift, yShift, totalWidth,
						totalHeight, transparentMask);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public String toString()
	{
		return names.toString();
	}
}
