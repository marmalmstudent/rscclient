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
	
	public static void setChannelVal(byte[] array, byte val, int channel)
	{
		for (; channel < array.length; channel += 4)
			array[channel] = val;
	}
	
	public static void arrayCopyToByte(int[] src, int srcStart,
			int srcLen, byte[] dst, int dstStart,
			boolean bigEndian)
	{
		if (bigEndian)
			for (int i = srcStart, j = dstStart; i < srcLen; ++i)
			{
				dst[j++] = (byte)((src[i] >> 24) & 0xff);
				dst[j++] = (byte)((src[i] >> 16) & 0xff);
				dst[j++] = (byte)((src[i] >> 8) & 0xff);
				dst[j++] = (byte)(src[i] & 0xff);
			}
		else
			for (int i = srcStart, j = dstStart; i < srcLen; ++i)
			{
				dst[j++] = (byte)(src[i] & 0xff);
				dst[j++] = (byte)((src[i] >> 8) & 0xff);
				dst[j++] = (byte)((src[i] >> 16) & 0xff);
				dst[j++] = (byte)((src[i] >> 24) & 0xff);
			}
	}
	
	public static byte[] intToByteArray(int[] src, boolean bigEndian)
	{
		byte[] dst = new byte[Integer.BYTES*src.length];
		arrayCopyToByte(src, 0, src.length, dst, 0, bigEndian);
		return dst;
	}

	public static BufferedImage readImage(int[] pixels, int width,
			int height)
	{
		byte[] pixelData = intToByteArray(pixels, false);
		setChannelVal(pixelData, (byte)0xff, 3);
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
