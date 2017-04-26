package org.conf.cachedev.sprites;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.conf.cachedev.FileOperations;

public class LogoHandle extends SpriteHandle
{
	public LogoHandle(File f)
	{
		super(f);
	}

	@Override
	public void newDat(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sprite = new Entity(data);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	@Override
	public void newPNG(File f, boolean requiresShift, int xShift, int yShift, int cameraAngle1, int cameraAngle2)
	{
		try
		{
			Dimension d = FileOperations.getImageDimension(f);
			int width = d.width;
			int height = d.height;
			int[] pixelData = FileOperations.readImage(f, width, height);
			sprite = new Sprite(pixelData, width, height,
					requiresShift, xShift, yShift, cameraAngle1, cameraAngle2);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
