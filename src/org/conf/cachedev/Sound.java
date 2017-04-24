package org.conf.cachedev;

import java.io.File;

public class Sound
{
	public static final int HEADER_SIZE = 44;
	
	/* "RIFF" chunk descriptor */
	public static final String ChunkID = "RIFF";
	public static int ChunkSize;
	public static final String Format = "WAVE";
	
	/* "fmt " chunk sub-chunk */
	public static final String Subchunk1ID = "fmt ";
	public static final int Subchunk1Size = 16; // 8-bit
	public static final int AudioFormat = 1; // 1 for PCM
	public static final int NumChannels = 1;
	public static final int SampleRate = 8000; // 8 kHz sampling rate
	public static final int BitsPerSample = 8;
	public static final int ByteRate = SampleRate*BitsPerSample*NumChannels;
	public static final int BlockAlign = 1;
	
	/* "data" sub-chunk */
	public static final String Subchunk2ID = "data";
	public static int Subchunk2Size;
	public byte[] soundData, data;
	
	public Sound(byte[] data, boolean isPCM)
	{
		if (isPCM)
			soundData = data;
		else
		{
			soundData = new byte[data.length - HEADER_SIZE];
		    for (int i = HEADER_SIZE, j = 0; i < data.length; soundData[j++] = data[i++]);
		}
	}
	
	public void toWAV(File dst)
	{
		data = new byte[soundData.length + HEADER_SIZE];
		Subchunk2Size = soundData.length;
		ChunkSize = soundData.length + HEADER_SIZE - 8; // exclude ChunkID and ChunkSize
		int offset = 0;
		// write ChunkID, 4 big
		for (int i = 0; i < 4; ++i)
			offset = DataOperations.writeByte(data, offset, (int)ChunkID.charAt(i));
		// write ChunkSize, 4, little
		offset = DataOperations.write4Bytes(data, offset, ChunkSize, false);
		// write Format, 4, big
		for (int i = 0; i < 4; ++i)
			offset = DataOperations.writeByte(data, offset, Format.charAt(i));
		// write Subchunk1ID, 4, big
		for (int i = 0; i < 4; ++i)
			offset = DataOperations.writeByte(data, offset, Subchunk1ID.charAt(i));
		// write Subchunk1Size, 4, little
		offset = DataOperations.write4Bytes(data, offset, Subchunk1Size, false);
		// write AudioFormat, 2, little
		offset = DataOperations.write2Bytes(data, offset, AudioFormat, false);
		// write NumChannels, 2, little
		offset = DataOperations.write2Bytes(data, offset, NumChannels, false);
		// write SampleRate, 4, little
		offset = DataOperations.write4Bytes(data, offset, SampleRate, false);
		// write ByteRate, 4, little
		offset = DataOperations.write4Bytes(data, offset, ByteRate, false);
		// write BlockAlign, 2, little
		offset = DataOperations.write2Bytes(data, offset, NumChannels, false);
		// write BitsPerSample, 2, little
		offset = DataOperations.write2Bytes(data, offset, BitsPerSample, false);
		// write Subchunk2ID, 4, big
		for (int i = 0; i < 4; ++i)
			offset = DataOperations.writeByte(data, offset, Subchunk2ID.charAt(i));
		// write Subchunk2Size, 4, little
		offset = DataOperations.write4Bytes(data, offset, Subchunk2Size, false);
		// write soundData, little
		DataOperations.writeArray(data, offset, soundData);
		try
		{
			FileOperations.write(data, dst);
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public void toPCM(File dst)
	{
		data = new byte[soundData.length];
		DataOperations.writeArray(data, 0, soundData);
		try
		{
			FileOperations.write(data, dst);
		} catch (Exception e) { e.printStackTrace(); }		
	}
	
	public void fixSoundData()
	{
		int maxVal = 0;
		for (byte i : soundData)
			if (i > maxVal)
				maxVal = i;
		for (int i = 0; i < soundData.length; ++i)
			if (soundData[i] >= 0)
				soundData[i] = (byte)((maxVal - soundData[i] + 128) & 0xff);
			else
				soundData[i] = (byte)((soundData[i] + 128) & 0xff);
	}
	
	public void revertSoundData()
	{
		for (int i = 0; i < soundData.length; ++i)
			soundData[i] = (byte)((soundData[i] & 0xff) - 128);
		int maxVal = 0;
		for (byte i : soundData)
			if (i > maxVal)
				maxVal = i;
		for (int i = 0; i < soundData.length; ++i)
			if (soundData[i] >= 0)
				soundData[i] = (byte)((maxVal - soundData[i]) & 0xff);
	}

	/* should be moved, not the responsibility of this class */
	public void pcmToWAV()
	{
		try
		{
			Sound s;
			File f;
			for (int i = 0; i < 40; ++i)
			{
				f = new File("src/org/conf/cachedev/dev/sounds1/pcm/"+Integer.toString(i));
				if (!f.exists())
					continue;
				s = new Sound(FileOperations.read(f), true);
				s.fixSoundData();
				s.toWAV(new File("src/org/conf/cachedev/dev/sounds1/wav/"+Integer.toString(i)+".wav"));	
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	/* should be moved, not the responsibility of this class */
	public void wavToPCM()
	{
		try
		{
			Sound s;
			File f;
			for (int i = 0; i < 40; ++i)
			{
				f = new File("src/org/conf/cachedev/dev/sounds1/wav/"+Integer.toString(i)+".wav");
				if (!f.exists())
					continue;
				s = new Sound(FileOperations.read(f), false);
				s.revertSoundData();
				s.toPCM(new File("src/org/conf/cachedev/dev/sounds1/pcm/"+Integer.toString(i)));	
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
}
