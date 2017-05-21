package client;

import java.awt.Point;

public class Trig
{
	static double sin1024[];
	static double cos1024[];
	static double sin256[] = new double[0x100];
	static double cos256[] = new double[0x100];
	
	static
	{
		sin1024 = new double[0x400];
		cos1024 = new double[0x400];
		sin256 = new double[0x100];
		cos256 = new double[0x100];

		for (int i = 0; i < 256; i++)
				sin256[i] = Math.sin((double) i * 0.02454369D);
		for (int i = 0; i < 256; i++)
				cos256[i] = Math.cos((double) i * 0.02454369D);

		for (int i = 0; i < 1024; i++)
				sin1024[i] = Math.sin((double) i * 0.00613592315D);
		for (int i = 0; i < 1024; i++)
				cos1024[i] = Math.cos((double) i * 0.00613592315D);
	}
}
