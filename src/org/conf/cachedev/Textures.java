package org.conf.cachedev;

import java.io.File;

public class Textures extends Sprite
{
	public static final int TEXTURES_START = 3220;
	public Textures()
	{
		super();
	}

	/**
	 * Converts pixels that are transparent in a png format (i.e.
	 * alpha channel, 0xff000000, is zero) into pixels that are
	 * transparent in a dat format (i.e. 0xff00ff).
	 */
	private void pngToDatTransparent()
	{
		for (int i = 0; i < pixelData.length; ++i)
			if ((pixelData[i] & 0xff000000) == 0)
				pixelData[i] = 0xffff00ff;
	}

	/**
	 * Converts pixels that are transparent in a dat format (i.e.
	 * 0xff00ff) into pixels that are transparent in a png format
	 * (i.e. alpha channel is zero).
	 */
	private void datToPNGTransparent()
	{
		for (int i = 0; i < pixelData.length; ++i)
			if (pixelData[i] == 0xff00ff)
				pixelData[i] = 0;
			else
				pixelData[i] = 0xff000000 + (pixelData[i] & 0xffffff);
	}
	
	/**
	 * Converts a png image into a dat file.
	 * @param src the source (png) image.
	 * @param dst the destination (dat) file.
	 */
	public void pngToDat(File src, File dst)
	{
		loadPNG(src);
		pngToDatTransparent();
        requiresShift = false;
        xShift = 0;
        yShift = 0;
        cameraAngle1 = width;
        cameraAngle2 = height;
        writeDat(dst);
	}
	
	/**
	 * Converts a dat file into a png image
	 * @param src the source (dat) file.
	 * @param dst the destination (png) image.
	 */
	public void datToPNG(File src, File dst)
	{
		loadDat(src);
		datToPNGTransparent();
		writePNG(dst);
	}
}
