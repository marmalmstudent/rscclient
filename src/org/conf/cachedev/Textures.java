package org.conf.cachedev;

import java.io.File;

public class Textures extends Sprite
{
	public static final int TEXTURES_START = 3220;
	public Textures(String name)
	{
		super(name);
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
	 * @param zip TODO
	 */
	public void pngToDat(File src, File dst, boolean zip)
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
	 * @param unzip TODO
	 */
	public void datToPNG(File src, File dst, boolean unzip)
	{
		loadDat(src, unzip);
		System.out.print(entryName + " ");
		if (pixelData == null)
			return;
		datToPNGTransparent();
		writePNG(dst);
	}
}
