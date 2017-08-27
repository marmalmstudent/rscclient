package dbdev;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class DataOperations
{
	public byte[] data;
	public int offset;
	public DataOperations(byte[] data)
	{
		this.data = data;
		offset = 0;
	}

	public static int rgbaToInt(int array[], int offset, boolean hasAlpha)
	{
		int alpha = hasAlpha ? ((array[offset+3] & 0xff) << 24) : 0xff000000;
		return alpha + ((array[offset] & 0xff) << 16)
				+ ((array[offset+1] & 0xff) << 8) + (array[offset+2] & 0xff);
	}
	
	public int readInt(boolean bigEndian)
	{
		offset += 4;
		return ((data[offset-4] & 0xff) << 24)
				+ ((data[offset-3] & 0xff) << 16)
				+ ((data[offset-2] & 0xff) << 8)
				+ (data[offset-1] & 0xff);
	}
	
	public int read2Bytes(boolean signed, boolean bigEndian)
	{
		offset += 2;
		int out = ((data[offset-2] & 0xff) << 8)
				+ (data[offset-1] & 0xff);
		if (signed)
			out = (int)((short)out);
		return out;
	}
	
	public int readByte(boolean signed)
	{
		if (signed)
			return (int)data[offset++];
		else
			return data[offset++] & 0xff;
		
	}
	
	public boolean readBoolean()
	{
		return data[offset++] != 0;
	}
	
	public float readFloat(boolean bigEndian)
	{
		byte[] tmp = new byte[4];
		for (int i = 0; i < 4; ++i)
			tmp[i] = data[offset++];
		return ByteBuffer.wrap(tmp).getFloat();
	}
	
	public void write4Bytes(int val, boolean bigEndian)
	{
		if (bigEndian)
		{
			data[offset++] = (byte)((val >> 24) & 0xff);
			data[offset++] = (byte)((val >> 16) & 0xff);
			data[offset++] = (byte)((val >> 8) & 0xff);
			data[offset++] = (byte)(val & 0xff);
		}
		else
		{
			data[offset++] = (byte)(val & 0xff);
			data[offset++] = (byte)((val >> 8) & 0xff);
			data[offset++] = (byte)((val >> 16) & 0xff);
			data[offset++] = (byte)((val >> 24) & 0xff);
		}
	}
	
	public void write2Bytes(int val, boolean bigEndian)
	{
		if (bigEndian)
		{
			data[offset++] = (byte)((val >> 8) & 0xff);
			data[offset++] = (byte)(val & 0xff);
		}
		else
		{
			data[offset++] = (byte)(val & 0xff);
			data[offset++] = (byte)((val >> 8) & 0xff);
		}
	}
	
	public void writeByte(int val)
	{
		data[offset++] = (byte)(val & 0xff);
	}
	
	public void writeBoolean(boolean flag)
	{
		writeByte(flag ? 1 : 0);
	}
	
	public void writeArray(byte[] src)
	{
		for (int i = 0; i < src.length; data[offset++] = src[i++]);
	}
	
	public void writeString(String str)
	{
		writeArray((str+"\0").getBytes());
	}
	
	public String readString()
	{
		int i = offset;
		while (data[offset++] != 0);
		byte[] dst = new byte[offset-1 - i];
		for (int j = 0; i < offset-1; dst[j++] = data[i++]);
		return new String(dst);
	}
	
	public void writeFloat(float f, boolean bigEndian)
	{
		byte[] tmp = ByteBuffer.allocate(4).putFloat(f).array();
		if (bigEndian)
			for (int i = 0; i < 4; data[offset++] = tmp[i++]);
		else
			for (int i = 3; i >= 0; data[offset++] = tmp[i--]);
	}
	
	public static int getNextKey(HashMap<Integer, String> names)
	{
		return names.size(); // placeholder
	}
}
