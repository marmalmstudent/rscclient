package org.conf.cachedev;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sprite {
	
	protected int width, height, xShift, yShift,
	cameraAngle1, cameraAngle2;
	protected boolean requiresShift;
	protected byte[] header, image, data;
	protected int[] pixelData;
	protected String entryName;
	private static final int HEADER_SIZE = 25;
	
	public Sprite(String name)
	{
		entryName = name;
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public boolean getRequiresShift() { return requiresShift; }
	public int getXShift() { return xShift; }
	public int getYShift() { return yShift; }
	public int getCameraAngle1() { return cameraAngle1; }
	public int getCameraAngle2() { return cameraAngle2; }
	public int[] getPixelData() { return pixelData; }
	public byte[] getHeader() { return header; }
	public byte[] getImage() { return image; }
	public byte[] getData() { return data; }
	
	/**
	 * Formats the header data and image data into
	 * the data array.
	 */
	protected void packDataDat()
	{
		data = new byte[25 + width*height*4];
		FileOperations.write4Bytes(data, 0, width, true);
		FileOperations.write4Bytes(data, 4, height, true);
		FileOperations.writeBoolean(data, 8, requiresShift);
		FileOperations.write4Bytes(data, 9, xShift, true);
		FileOperations.write4Bytes(data, 13, yShift, true);
		FileOperations.write4Bytes(data, 17, cameraAngle1, true);
		FileOperations.write4Bytes(data, 21, cameraAngle2, true);
		packImageDat();
		FileOperations.writeArray(data, HEADER_SIZE, image);
	}
	
	/**
	 * Unpacks the header data into the header variables
	 * (width, height, requiresShift, xShift, yShift,
	 *  cameraAngle1, cameraAngle2) and the image data
	 * into pixelData.
	 */
	protected void unpackDataDat()
	{
		width = FileOperations.readInt(data, 0, true);
		height = FileOperations.readInt(data, 4, true);
		requiresShift = FileOperations.readBoolean(data, 8);
		xShift = FileOperations.readInt(data, 9, true);
		yShift = FileOperations.readInt(data, 13, true);
		cameraAngle1 = FileOperations.readInt(data, 17, true);
		cameraAngle2 = FileOperations.readInt(data, 21, true);
		unpackImage();
	}
	
	/**
	 * Unpacks the image data into the pixelData array.
	 */
	protected void unpackImage()
	{
		int dataSizeBytes = data.length - HEADER_SIZE;
		if (dataSizeBytes != width*height*4)
			return; // data is corrupted or wrong format
		pixelData = new int[dataSizeBytes/4];
		int imageOffset = 0;
		for (int i = HEADER_SIZE; i < data.length; i += 4)
			pixelData[imageOffset++] = FileOperations.readInt(data, i, true);
	}

	/**
	 * loads a png image and stores the data in the pixelData array.
	 * @param file the (image) file.
	 * @throws IOException
	 */
	protected void loadImage(File file) throws IOException
	{
		Dimension d = FileOperations.getImageDimension(file);
		width = d.width;
		height = d.height;
		pixelData = FileOperations.readImage(file, width, height);
	}

	/**
	 * Formats the data contained in pixelData into a dat image
	 * format (i.e. argb, where the alpha channel is zero).
	 */
	protected void packImageDat()
	{
		image = new byte[4*pixelData.length];
		int offset = 0;
		for (int i : pixelData)
		{
			image[offset++] = (byte)0;
			image[offset++] = (byte)((i >> 16) & 0xff);
			image[offset++] = (byte)((i >> 8) & 0xff);
			image[offset++] = (byte)(i & 0xff);
		}
	}

	/**
	 * Formats the data contained in pixelData into a png image
	 * format (i.e. argb).
	 */
	protected byte[] packImagePNG()
	{
		image = new byte[4*pixelData.length];
		int offset = 0;
		for (int i : pixelData)
		{
			image[offset++] = (byte)((i >> 16) & 0xff);
			image[offset++] = (byte)((i >> 8) & 0xff);
			image[offset++] = (byte)(i & 0xff);
			image[offset++] = (byte)((i >> 24) & 0xff);
		}
		return image;
	}
	
	/**
	 * Loads a png file. The transparency conversions have to
	 * be provided by the class that invokes this method.
	 * @param src
	 */
	protected void loadPNG(File src)
	{
		try
		{
			loadImage(src);
		} catch (IOException ioe) { ioe.printStackTrace(); }
	}
	
	/**
	 * Writes a png file. The transparency conversions have to
	 * be provided by the class that invokes this method.
	 * @param dst
	 */
	protected void writePNG(File dst)
	{
		packImagePNG();
		try {
			FileOperations.writeImageAlpha(image, width, height, dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * reads a dat file.
	 * @param unzip TODO
	 * @param dst
	 */
	protected void loadDat(File src, boolean unzip)
	{
		try
		{
			if (unzip)
				data = FileOperations.readZip(src, entryName);
			else
				data = FileOperations.read(src);
		} catch (IOException ioe) { ioe.printStackTrace(); }
		if (data == null)
			return;
		unpackDataDat();
	}
	
	/**
	 * Writes a dat file. The header data have to be set
	 * in the class that invokes this method.
	 * @param dst
	 */
	protected void writeDat(File dst)
	{
		packImageDat();
        packDataDat();
		try {
			FileOperations.write(data, dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
