package client;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileOperations
{
	public FileOperations()
	{
		
	}

	public static BufferedImage readImage(byte[] pixelData, int width,
			int height) throws IOException
	{
		DataBuffer buffer = new DataBufferByte(
				pixelData, pixelData.length);
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height,
				4*width, 4, new int[]{0, 1, 2, 3}, (Point)null);
		ColorModel cm = new ComponentColorModel(
				ColorModel.getRGBdefault().getColorSpace(),
				true, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, raster, true, null);
	}
}
