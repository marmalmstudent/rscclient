package org.conf.cachedev;

import java.awt.Dimension;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;


public class FileOperations {
	
	public static final int MAX_FILE_SIZE = 100000; // 100 kb
	public static final int BUFFER_SIZE = 1000; // 1 kb
	
	public FileOperations() {}
	
	public static void main(String args[])
	{
	}
	
	public static Dimension getImageDimension(File file) throws IOException
	{
		BufferedImage img = ImageIO.read(file);
		return new Dimension(img.getWidth(), img.getHeight());
	}
	
	public static int[] readImage(File file, int width, int height)
	throws IOException
	{
		BufferedImage img = ImageIO.read(file);
		int[] pixelDataRaw = new int[width*height*4];
		Raster rast = img.getData();
		rast.getPixels(0, 0, width, height, pixelDataRaw);
		int[] pixelData = new int[width*height];
		int pdOffs = 0;
		// rgba format
		for (int i = 0; i < pixelDataRaw.length; i += 4)
			pixelData[pdOffs++] = FileOperations.rgbaToInt(pixelDataRaw, i);
		return pixelData;
	}
	
	public static int rgbaToInt(int array[], int offset)
	{
		return ((array[offset+3] & 0xff) << 24)
				+ ((array[offset] & 0xff) << 16)
				+ ((array[offset+1] & 0xff) << 8)
				+ (array[offset+2] & 0xff);
	}
	
	public static int getInt(byte array[], int offset)
	{
		return ((array[offset] & 0xff) << 24)
				+ ((array[offset+1] & 0xff) << 16)
				+ ((array[offset+2] & 0xff) << 8)
				+ (array[offset+3] & 0xff);
	}
	
	public static boolean readBoolean(byte array[], int offset)
	{
		return array[offset] != 0;
	}
	
	public static int write4Bytes(byte array[], int offset, int val)
	{
		array[offset++] = (byte)((val >> 24) & 0xff);
		array[offset++] = (byte)((val >> 16) & 0xff);
		array[offset++] = (byte)((val >> 8) & 0xff);
		array[offset++] = (byte)(val & 0xff);
		return offset;
	}
	
	public static int writeBoolean(byte array[], int offset, boolean flag)
	{
		array[offset++] = (byte)(flag ? 1 : 0);
		return offset;
	}
	
	public static int writeArray(byte[] dst, int offset, byte[] src)
	{
	    for (byte b : src)
	    	dst[offset++] = b;
	    return offset;
	}
	
	/**
	 * Reads the file and returns the data.
	 * @param f the file that will be read.
	 * @return the data contained in the file, or null if an error occurs.
	 * @throws IOException
	 */
	public static byte[] read(File f) throws IOException
	{
		FileInputStream stream = new FileInputStream(f);
		byte out[] = null;
		int dataSize = 0;
		try
		{
			int fileSize = stream.available();
			/* Perhaps i should verify here that the file size is
			 * the same as fileSize */
			if (fileSize > MAX_FILE_SIZE)
			{
				System.err.printf("File too big (%d > %d)",
						fileSize, MAX_FILE_SIZE);
			}
			else
			{
				byte[] buffer = new byte[MAX_FILE_SIZE];
				dataSize = stream.read(buffer);
				out = new byte[dataSize];
				for (int i = 0; i < dataSize; ++i)
					out[i] = buffer[i];
			}
		}
		finally
		{
			stream.close();
		}
		return out;
	}

	public static void write(byte[] array, File filename) throws IOException
	{
		FileOutputStream stream = new FileOutputStream(filename);
		try
		{
			stream.write(array);
		} finally
		{
			stream.close();
		}
	}
	
	public static void writeImageAlpha(byte[] pixelData, int width,
			int height, File file) throws IOException
	{
		DataBuffer buffer = new DataBufferByte(
				pixelData, pixelData.length);
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height,
				4*width, 4, new int[]{0, 1, 2, 3}, (Point)null);
		ColorModel cm = new ComponentColorModel(
				ColorModel.getRGBdefault().getColorSpace(),
				true, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		BufferedImage image = new BufferedImage(cm, raster, true, null);
		ImageIO.write(image, "png", file);
	}
}
