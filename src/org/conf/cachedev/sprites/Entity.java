package org.conf.cachedev.sprites;


public class Entity extends Sprite {
	
	public Entity(byte[] data)
	{ // dat to png
		super(data);
		datToPNGTransparent();
	}
	
	public Entity(int[] pixelData, int width, int height,
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
