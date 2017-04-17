package org.conf.cachedev;

import java.io.File;

public class Main {

	public static void main(String args[])
	{
		String s1 = "3237";
		Textures txr = new Textures();
		txr.pngToDat(
				new File("src/org/conf/utils/sprites_img/"+s1+".png"),
				new File("src/org/conf/cachedev/"+s1+".dat"));
		Textures txw = new Textures();
		txw.datToPNG(
				new File("src/org/conf/cachedev/"+s1+".dat"),
				new File("src/org/conf/cachedev/"+s1+".png"));
		String s2 = "0";
		Sprites spr = new Sprites();
		spr.pngToDat(
				new File("src/org/conf/utils/sprites_img/"+s2+".png"),
				new File("src/org/conf/cachedev/"+s2+".dat"));
		Sprites spw = new Sprites();
		spw.datToPNG(
				new File("src/org/conf/cachedev/"+s2+".dat"),
				new File("src/org/conf/cachedev/"+s2+".png"));
	}
}
