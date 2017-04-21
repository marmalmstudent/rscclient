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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;


public abstract class FileOperations {
	
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
			pixelData[pdOffs++] = DataOperations.rgbaToInt(pixelDataRaw, i);
		return pixelData;
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
	
	public static HashMap<Integer, String> readHashMap(File mapFile, String delimiter) throws IOException
	{
		HashMap<Integer, String> map = null;
		BufferedReader br = new BufferedReader(new FileReader(mapFile));
		try
		{
		    String line = br.readLine();
		    String[] entries = line.split(delimiter);
		    map = new HashMap<Integer, String>(entries.length);
		    for (String str : entries)
		    {
		    	String[] entry = str.split(",");
		    	if (!map.containsValue(entry[1]))
		    		map.put(Integer.parseInt(entry[0]), entry[1]);
		    }
		}
		finally
		{
		    br.close();
		}
		return map;
	}
}
