package org.conf.cachedev;


public class SpriteObjects extends Sprite {

	public static final int SPRITES_START = 0;
	
	public SpriteObjects(byte[] data)
	{ // dat to png
		super(data);
		datToPNGTransparent();
	}
	
	public SpriteObjects(int[] pixelData, int width, int height,
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
				pixelData[i] = 0;
	}

	/**
	 * Converts pixels that are transparent in a dat format (i.e.
	 * 0xff00ff) into pixels that are transparent in a png format
	 * (i.e. alpha channel is zero).
	 */
	private void datToPNGTransparent()
	{
		for (int i = 0; i < pixelData.length; ++i)
			if (pixelData[i] != 0)
				pixelData[i] = 0xff000000 + (pixelData[i] & 0xffffff);
	}
}
