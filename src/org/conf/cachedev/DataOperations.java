package org.conf.cachedev;

import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class DataOperations
{
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
	
	public static int getNextKey(HashMap<Integer, String> names)
	{
		return names.size(); // placeholder
	}
}
