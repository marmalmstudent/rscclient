package org.conf.cachedev;

import java.io.File;

public class Main {

	public static void main(String args[])
	{
		int textureStart = Textures.TEXTURES_START;
		int textureLen = 500;
		Textures txr, txw;
		File srcr, srcw, dstr, dstw;
		for (int i = textureStart; i < textureStart + textureLen; ++i)
		{
			srcr = new File("src/org/conf/utils/sprites_img/"+Integer.toString(i)+".png");
			if (!srcr.exists())
				continue;
			System.out.printf("Processing file %d/%d, (%d%%)\n",
					i-textureStart, textureLen,
					100*(i-textureStart)/textureLen);
			txr = new Textures();
			dstr = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".dat");
			txr.pngToDat(srcr, dstr);

			srcw = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".dat");
			if (!srcw.exists())
				continue;
			txw = new Textures();
			dstw = new File("src/org/conf/cachedev/Textures/"+Integer.toString(i)+".png");
			txw.datToPNG( srcw, dstw);
		}
		int spritesStart = Sprites.SPRITES_START;
		int spritesLen = 3000;
		Sprites spr, spw;
		for (int i = spritesStart; i < spritesStart + spritesLen; ++i)
		{
			srcr = new File("src/org/conf/utils/sprites_img/"+Integer.toString(i)+".png");
			if (!srcr.exists())
				continue;
			System.out.printf("Processing file %d/%d, (%d%%)\n",
					i-spritesStart, spritesLen,
					100*(i-spritesStart)/spritesLen);
			spr = new Sprites();
			dstr = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".dat");
			spr.pngToDat(srcr, dstr);

			srcw = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".dat");
			if (!srcw.exists())
				continue;
			spw = new Sprites();
			dstw = new File("src/org/conf/cachedev/Sprites/"+Integer.toString(i)+".png");
			spw.datToPNG( srcw, dstw);
		}
	}
}
