package dbdev;

public class Sound
{
	public byte[] soundData;
	
	public byte[] getSoundData()
	{
		return soundData;
	}
	
	public Sound(byte[] data, int offset, boolean isPCM)
	{
		if (isPCM)
		{
			soundData = data;
			fixSoundData();
		}
		else
		{
			soundData = new byte[data.length - offset];
		    for (int j = 0; offset < data.length; soundData[j++] = data[offset++]);
		    revertSoundData();
		}
	}
	
	private void fixSoundData()
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
	
	private void revertSoundData()
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
}
