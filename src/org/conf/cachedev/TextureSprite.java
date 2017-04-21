package org.conf.cachedev;

import java.io.File;

public class TextureSprite extends Sprite
{
	public static final int TEXTURES_START = 3220;
	
	public TextureSprite(byte[] data)
	{ // dat to png
		super(data);
		datToPNGTransparent();
	}
	
	public TextureSprite(int[] pixelData, int width, int height,
			boolean requiresShift, int xShift, int yShift,
			int cameraAngle1, int cameraAngle2)
	{ // png to dat
		super(pixelData, width, height,
				requiresShift, xShift, yShift,
				cameraAngle1, cameraAngle2);
		pngToDatTransparent();
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
}
