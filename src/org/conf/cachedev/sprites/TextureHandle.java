package org.conf.cachedev.sprites;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.conf.cachedev.FileOperations;

public class TextureHandle extends SpriteHandle
{
	public TextureHandle(File f)
	{
		super(f);
	}

	@Override
	public void newDat(File f) {
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sprite = new Texture(data);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	@Override
	public void newPNG(File f, boolean requiresShift, int xShift, int yShift, int cameraAngle1, int cameraAngle2) {
		try
		{
			Dimension d = FileOperations.getImageDimension(f);
			int width = d.width;
			int height = d.height;
			int[] pixelData = FileOperations.readImage(f, width, height);
			sprite = new Sprite(pixelData, width, height,
					false, 0, 0, width, height);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
