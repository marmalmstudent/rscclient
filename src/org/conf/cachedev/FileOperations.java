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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.Model;
import org.entityhandling.EntityHandler;
import org.util.Config;


public class FileOperations {
	
	public static final int MAX_FILE_SIZE = 1000000; // 1 Mb
	
	public FileOperations() {}
	
	public static void main(String args[])
	{
		int offset = 0;
		byte[] array = {(byte)0xff, (byte)0xff};
		int out = ((array[offset] & 0xff) << 8)
				+ (array[offset] & 0xff);
		System.out.printf("%d, %d",(int)((short)out), out);
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
	
	public static int readInt(byte array[], int offset, boolean bigEndian)
	{
		return ((array[offset] & 0xff) << 24)
				+ ((array[offset+1] & 0xff) << 16)
				+ ((array[offset+2] & 0xff) << 8)
				+ (array[offset+3] & 0xff);
	}
	
	public static int read2Bytes(byte array[], int offset, boolean signed, boolean bigEndian)
	{
		int out = ((array[offset] & 0xff) << 8)
				+ (array[offset+1] & 0xff);
		if (signed)
			out = (int)((short)out);
		return out;
	}
	
	public static boolean readBoolean(byte array[], int offset)
	{
		return array[offset] != 0;
	}
	
	public static float readFloat(byte[] array, int offset, boolean bigEndian)
	{
		byte[] tmp = new byte[4];
		for (int i = 0; i < 4; ++i)
			tmp[i] = array[offset++];
		return ByteBuffer.wrap(tmp).getFloat();
	}
	
	public static int write4Bytes(byte array[], int offset, int val, boolean bigEndian)
	{
		if (bigEndian)
		{
			array[offset++] = (byte)((val >> 24) & 0xff);
			array[offset++] = (byte)((val >> 16) & 0xff);
			array[offset++] = (byte)((val >> 8) & 0xff);
			array[offset++] = (byte)(val & 0xff);
		}
		else
		{
			array[offset++] = (byte)(val & 0xff);
			array[offset++] = (byte)((val >> 8) & 0xff);
			array[offset++] = (byte)((val >> 16) & 0xff);
			array[offset++] = (byte)((val >> 24) & 0xff);
		}
		return offset;
	}
	
	public static int write2Bytes(byte array[], int offset, int val, boolean bigEndian)
	{
		if (bigEndian)
		{
			array[offset++] = (byte)((val >> 8) & 0xff);
			array[offset++] = (byte)(val & 0xff);
		}
		else
		{
			array[offset++] = (byte)(val & 0xff);
			array[offset++] = (byte)((val >> 8) & 0xff);
		}
		return offset;
	}
	
	public static int writeByte(byte array[], int offset, int val)
	{
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
	
	public static int writeFloat(byte[] array, int offset, float f, boolean bigEndian)
	{
		byte[] tmp = ByteBuffer.allocate(4).putFloat(f).array();
		if (bigEndian)
			for (int i = 0; i < 4; array[offset++] = tmp[i++]);
		else
			for (int i = 3; i >= 0; array[offset++] = tmp[i--]);
		return offset;
	}
	
	public static byte[] arrayCopy(byte[] array, int newSize)
	{
		byte[] newArray = new byte[newSize];
		for (int i = 0, j = 0; i < newSize && j < array.length;
				newArray[i++] = array[j++]);
		return newArray;
	}
	
	/**
	 * Reads the file and returns the data.
	 * @param f the file that will be read.
	 * @return the data contained in the file, or null if an error occurs.
	 * @throws IOException
	 */	
	public static byte[] read(File f) throws IOException
	{
		FileInputStream fIn = null;
		BufferedInputStream buffIn = null; // seems a bit faster without this
		byte out[] = null;
		try
		{
			fIn = new FileInputStream(f);
			buffIn = new BufferedInputStream(fIn);
            
			int fileSize = buffIn.available();
			if (fileSize > MAX_FILE_SIZE)
			{
				System.err.printf("File too big (%d > %d)", fileSize, MAX_FILE_SIZE);
			}
			else
			{
				byte[] buffer = new byte[MAX_FILE_SIZE];
				int size, offset;
				for(offset = 0, size = 0; (size = buffIn.read(buffer, offset, 4096)) != -1; offset += size);
				out = new byte[offset];
				for(int i = 0, j = 0; i != offset; out[i++] = buffer[j++]);
			}
		}
		finally
		{
			if (buffIn != null)
				buffIn.close();
			if (fIn != null)
				fIn.close();
		}
		return out;
	}

	public static void write(byte[] array, File filename) throws IOException
	{
		FileOutputStream stream = null;
		BufferedOutputStream buffOut = null;
		try
		{
			stream = new FileOutputStream(filename);
			buffOut = new BufferedOutputStream(stream);
			int bufferSize = 4096;
			int offset;
			for(offset = 0; offset < array.length; offset += bufferSize)
			{ // write bufferSize bytes at a time.
				if (array.length - offset < bufferSize)
					bufferSize = array.length - offset;
				buffOut.write(array, offset, bufferSize);
			}
		} finally
		{
			if (buffOut != null)
				buffOut.close();
			if (stream != null)
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
	
	public static byte[] readZip(File src, String entryName) throws IOException
	{
    	ZipFile archive = new ZipFile(src);
		byte out[] = null;
		int dataSize = 0;
        try
        {
        	
        	ZipEntry e = archive.getEntry(entryName);
        	if (e == null)
        		return null; // entry not found
        	System.out.printf("Entry Name: %s\n", e.getName());
        	//DataInputStream inStream = new DataInputStream(archive.getInputStream(e));
        	InputStream inStream = archive.getInputStream(e);
        	int fileSize = inStream.available();
        	if (fileSize > MAX_FILE_SIZE)
        	{
        		System.err.printf("File too big (%d > %d)",
        				fileSize, MAX_FILE_SIZE);
        	}
        	else
        	{
        		byte[] buffer = new byte[MAX_FILE_SIZE];
        		dataSize = inStream.read(buffer);
        		out = new byte[dataSize];
        		for (int i = 0; i < dataSize; ++i)
        			out[i] = buffer[i];
        	}
        }
        finally
        {
        	archive.close();
        }
        System.out.printf("unzip: %s, %d, %d\n", entryName, out.length, dataSize);
        return out;
	}
	
	/**
	 * Unzips the specified archive and extracts the contents to the specified directory.
	 * @param src The source zip file.
	 * @param dstDir path/to/destination/dir/
	 * @throws IOException
	 */
	public static void unzipArchive(File src, String dstDir) throws IOException
	{
		if (!src.exists())
			return;
		FileInputStream fIn = null;
		BufferedInputStream buffIn = null; // to avoid calling the system to read file
        ZipInputStream zipIs = null;
        ZipEntry zEntry = null;
        FileOutputStream fos = null;
        try
        {
        	fIn = new FileInputStream(src);
        	buffIn = new BufferedInputStream(fIn);
            zipIs = new ZipInputStream(buffIn);
            while((zEntry = zipIs.getNextEntry()) != null)
            {
                try
                {
                    byte[] tmp = new byte[4096];
                    File dst = new File(dstDir+zEntry.getName());
                    fos = new FileOutputStream(dst);
                    int size = 0;
                    while((size = zipIs.read(tmp)) != -1)
                    {
                        fos.write(tmp, 0 , size);
                    }
                    fos.flush();
                }
                catch(Exception ex)
                {
                     ex.printStackTrace();
                }
                finally
                {
                	if (fos != null)
                		fos.close();
                }
            }
        }
        finally
        {
        	if (zipIs != null)
        		zipIs.close();
        	if (buffIn != null)
        		buffIn.close();
        	if (fIn != null)
        		fIn.close();
        }
	}
	
	public static void zipArchive(String srcDir, File dst, int maxFiles) throws IOException
	{
		FileOutputStream fOut = null;
		BufferedOutputStream buffOut = null; // to avoid calling the system to write file
		ZipOutputStream out = null;
        ZipEntry zEntry = null;
        FileInputStream fIn = null;
        BufferedInputStream buffIn = null;
		try
		{
			fOut = new FileOutputStream(dst);
			buffOut = new BufferedOutputStream(fOut);
			out = new ZipOutputStream(buffOut);
			for (int i = 0; i < maxFiles; ++i)
			{
				File f = new File(srcDir+Integer.toString(i));
				if (!f.exists())
					continue; // file not found
				try
				{
					fIn = new FileInputStream(f);
					buffIn = new BufferedInputStream(fIn);
					zEntry = new ZipEntry(Integer.toString(i));
    				out.putNextEntry(zEntry);
    				/* Create buffer to control the data flow */
                    byte[] tmp = new byte[4096];
                    int size;
					while((size = buffIn.read(tmp)) != -1)
						out.write(tmp, 0, size);
					out.closeEntry();
				}
                catch(Exception ex)
                {
                     ex.printStackTrace();
                }
                finally
                {
                	/* Close any open streams */
                	if (buffIn != null)
                		buffIn.close();
                	if (fIn != null)
                		fIn.close();
                }
			}
		}
		finally
		{
        	/* Close any open streams */
        	if (out != null)
        		out.close();
        	if (buffOut != null)
        		buffOut.close();
        	if (fOut != null)
        		fOut.close();
		}
	}
	
	public static HashMap<Integer, String> readHashMap(File mapFile) throws IOException
	{
		HashMap<Integer, String> map = null;
		BufferedReader br = new BufferedReader(new FileReader(mapFile));
		try
		{
		    String line = br.readLine();
		    String[] entries = line.split(",");
		    int i = 0;
		    map = new HashMap<Integer, String>(entries.length);
		    for (String str : entries)
		    	if (!map.containsValue(str))
		    		map.put(i++, str);
		} finally {
		    br.close();
		}
		return map;
	}
}
