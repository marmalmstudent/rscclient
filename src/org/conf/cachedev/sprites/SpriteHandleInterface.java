package org.conf.cachedev.sprites;

import java.io.File;

public interface SpriteHandleInterface
{	
	public void newDat(File f);
	public void newPNG(File f, boolean requiresShift,
			int xShift, int yShift,
			int cameraAngle1, int cameraAngle2);
}
