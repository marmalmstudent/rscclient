package org.conf.cachedev.sprites;

public class Projectile extends Sprite {

	public static final int SPRITES_START = 0;
	
	public Projectile(byte[] data)
	{ // dat to png
		super(data);
		datToPNGTransparent();
	}
	
	public Projectile(int[] pixelData, int width, int height,
			boolean requiresShift, int xShift, int yShift,
			int cameraAngle1, int cameraAngle2)
	{ // png to dat
		super(pixelData, width, height,
				requiresShift, xShift, yShift,
				cameraAngle1, cameraAngle2);
		pngToDatTransparent();
	}

	@Override
	protected void pngToDatTransparent() {
		super.pngToDatTransparent();
	}
	
	@Override
	protected void datToPNGTransparent() {
		super.datToPNGTransparent();
	}
}
