package org.conf.cachedev;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Textures {
	
	private int width, height, xShift, yShift,
	cameraAngle1, cameraAngle2;
	private boolean requiresShift;
	private byte[] header, image, data;
	private int[] pixelData;
	
	public Textures()
	{
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
	private void packDataDat()
	{
		data = new byte[25 + width*height*4];
		FileOperations.write4Bytes(data, 0, width);
		FileOperations.write4Bytes(data, 4, height);
		FileOperations.writeBoolean(data, 8, requiresShift);
		FileOperations.write4Bytes(data, 9, xShift);
		FileOperations.write4Bytes(data, 13, yShift);
		FileOperations.write4Bytes(data, 17, cameraAngle1);
		FileOperations.write4Bytes(data, 21, cameraAngle2);
		packImageDat();
		FileOperations.writeArray(data, 25, image);
	}
	
	/**
	 * Unpacks the header data into the header variables
	 * (width, height, requiresShift, xShift, yShift,
	 *  cameraAngle1, cameraAngle2) and the image data
	 * into pixelData.
	 */
	private void unpackDataDat()
	{
		width = FileOperations.getInt(data, 0);
		height = FileOperations.getInt(data, 4);
		requiresShift = FileOperations.readBoolean(data, 8);
		xShift = FileOperations.getInt(data, 9);
		yShift = FileOperations.getInt(data, 13);
		cameraAngle1 = FileOperations.getInt(data, 17);
		cameraAngle2 = FileOperations.getInt(data, 21);
		unpackImage();
	}
	
	/**
	 * Unpacks the image data into the pixelData array.
	 */
	private void unpackImage()
	{
		int dataSizeBytes = data.length - 25;
		if (dataSizeBytes != width*height*4)
			return; // data is corrupted or wrong format
		pixelData = new int[dataSizeBytes/4];
		int imageOffset = 0;
		for (int i = 25; i < data.length; i += 4)
			pixelData[imageOffset++] = FileOperations.getInt(data, i);
		datToPNGTransparent();
	}

	/**
	 * Reads a png image and stores the data in the pixelData array.
	 * @param file the (image) file.
	 * @throws IOException
	 */
	private void readImage(File file) throws IOException
	{
		BufferedImage img = ImageIO.read(file);
		width = img.getWidth();
		height = img.getHeight();
		int[] pixelDataRaw = new int[width*height*4];
		Raster rast = img.getData();
		rast.getPixels(0, 0, width, height, pixelDataRaw);
		pixelData = new int[width*height];
		int pdOffs = 0;
		// rgba format
		for (int i = 0; i < pixelDataRaw.length; i += 4)
			pixelData[pdOffs++] = FileOperations.rgbaToInt(pixelDataRaw, i);
		pngToDatTransparent();
	}

	/**
	 * Converts pixels that are transparent in a png format (i.e.
	 * alpha channel, 0xff000000, is zero) into pixels that are
	 * transparent in a dat format (i.e. 0xff00ff).
	 */
	private void pngToDatTransparent()
	{
		for (int i = 0; i < pixelData.length; ++i)
			if ((pixelData[i] & 0xff000000) == 0)
				pixelData[i] = 0xffff00ff;
	}

	/**
	 * Converts pixels that are transparent in a dat format (i.e.
	 * 0xff00ff) into pixels that are transparent in a png format
	 * (i.e. alpha channel is zero).
	 */
	private void datToPNGTransparent()
	{
		for (int i = 0; i < pixelData.length; ++i)
			if (pixelData[i] == 0xff00ff)
				pixelData[i] = 0;
			else
				pixelData[i] = 0xff000000 + (pixelData[i] & 0xffffff);
	}

	/**
	 * Formats the data contained in pixelData into a dat image
	 * format (i.e. argb, where the alpha channel is zero).
	 */
	private void packImageDat()
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
	private byte[] packImagePNG()
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
	 * Converts a png image into a dat file.
	 * @param src the source (png) image.
	 * @param dst the destination (dat) file.
	 */
	public void pngToDat(File src, File dst)
	{
		try
		{
			readImage(src);
		} catch (IOException ioe) { ioe.printStackTrace(); }
		packImageDat();
        requiresShift = false;
        xShift = 0;
        yShift = 0;
        cameraAngle1 = width;
        cameraAngle2 = height;
        packDataDat();
		try {
			FileOperations.write(data, dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Converts a dat file into a png image
	 * @param src the source (dat) file.
	 * @param dst the destination (png) image.
	 */
	public void datToPNG(File src, File dst)
	{
		try
		{
			data = FileOperations.read(src);
		} catch (IOException ioe) { ioe.printStackTrace(); }
		if (data == null)
			return;
		unpackDataDat();
		packImagePNG();
		try {
			FileOperations.writeImageAlpha(image, width, height, dst);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		Textures txr = new Textures();
		txr.pngToDat(
				new File("src/org/conf/utils/sprites_img/3237.png"),
				new File("src/org/conf/cachedev/image.dat"));
		Textures txw = new Textures();
		txw.datToPNG(
				new File("src/org/conf/cachedev/image.dat"),
				new File("src/org/conf/cachedev/image.png"));
	}
}
