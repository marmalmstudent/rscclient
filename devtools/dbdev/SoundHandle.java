package dbdev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class SoundHandle
{
	public static final int HEADER_SIZE = 44;
	
	/* "RIFF" chunk descriptor */
	public static final String ChunkID = "RIFF";
	public static int ChunkSize;
	public static final String Format = "WAVE";
	/* "fmt " chunk sub-chunk */
	public static final String Subchunk1ID = "fmt ";
	public static final int Subchunk1Size = 16;
	public static final int AudioFormat = 1; // 1 for PCM
	public static final int NumChannels = 1;
	public static final int SampleRate = 8000; // 8 kHz sampling rate
	public static final int BitsPerSample = 8;
	public static final int ByteRate = SampleRate*BitsPerSample*NumChannels;
	public static final int BlockAlign = 1;
	/* "data" sub-chunk */
	public static final String Subchunk2ID = "data";
	public static int Subchunk2Size;
	
	private static HashMap<String, String> soundNames;
	private Sound sound;
	private DataOperations dataOps;
	
	public SoundHandle(File f)
	{
		try {
			soundNames = FileOperations.readHashMap(f, ";");
		} catch(IOException e) {e.printStackTrace();}
	}
	
	public HashMap<String, String> getSoundNames()
	{
		return soundNames;
	}
	
	public String getSoundName(int modelID)
	{
		if (soundNames.containsKey(modelID))
			return soundNames.get(modelID);
		return null;
	}
	
	public String getSoundID(String modelName)
	{
		for (Entry<String, String> entry : soundNames.entrySet())
			if (entry.getValue().equals(modelName))
				return entry.getKey();
		return null;
	}
	
	public void newPCM(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sound = new Sound(data, 0, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	public void newWAV(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				sound = new Sound(data, HEADER_SIZE, false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}
	
	public byte[] formatWAV()
	{
		byte[] soundData = sound.getSoundData();
		byte[] data = new byte[soundData.length + HEADER_SIZE];
		dataOps = new DataOperations(data);
		Subchunk2Size = soundData.length;
		ChunkSize = soundData.length + HEADER_SIZE - 8; // exclude ChunkID and ChunkSize
		for (int i = 0; i < 4;
				dataOps.writeByte((int)ChunkID.charAt(i++)));
		dataOps.write4Bytes(ChunkSize, false);
		for (int i = 0; i < 4;
				dataOps.writeByte(Format.charAt(i++)));
		for (int i = 0; i < 4;
				dataOps.writeByte(Subchunk1ID.charAt(i++)));
		dataOps.write4Bytes(Subchunk1Size, false);
		dataOps.write2Bytes(AudioFormat, false);
		dataOps.write2Bytes(NumChannels, false);
		dataOps.write4Bytes(SampleRate, false);
		dataOps.write4Bytes(ByteRate, false);
		dataOps.write2Bytes(NumChannels, false);
		dataOps.write2Bytes(BitsPerSample, false);
		for (int i = 0; i < 4;
				dataOps.writeByte(Subchunk2ID.charAt(i++)));
		dataOps.write4Bytes(Subchunk2Size, false);
		// write soundData, little
		dataOps.writeArray(soundData);
		return data;
	}
	
	public byte[] formatPCM()
	{
		byte[] soundData = sound.getSoundData();
		byte[] data = new byte[soundData.length];
		dataOps = new DataOperations(data);
		dataOps.writeArray(soundData);
		return data;		
	}
	
	public void saveWAV(File f)
	{
		try{
			FileOperations.write(formatWAV(), f);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void savePCM(File f)
	{
		try{
			FileOperations.write(formatPCM(), f);
		}catch(Exception e){e.printStackTrace();}
	}
}
