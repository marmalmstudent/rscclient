package org.util;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.imageio.ImageIO;

import org.DataOperations;
import org.mudclient;

public class misc {



	public static String timeSince(long time)
	{
		int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
		int minutes = (int) (seconds / 60);
		int hours = (int) (minutes / 60);
		int days = (int) (hours / 24);
		return days + " days " + (hours % 24) + " hours " + (minutes % 60) + " mins";
	}


	public static File getEmptyFile(
			boolean movie, String userName) throws IOException
	{
		String charName = DataOperations.longToString(DataOperations.stringLength12ToLong(userName));
		File file = new File(Config.MEDIA_DIR + File.separator + charName);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdir();
		}
		String folder = file.getPath() + File.separator;
		file = null;
		for (int suffix = 0; file == null || file.exists(); suffix++) {
			file = movie ? new File(folder + "movie" + suffix + ".mov") : new File(folder + "screenshot" + suffix + ".png");
		}
		return file;
	}

	public static void writeToFile(byte[] array, String filename) throws IOException
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

	// 8000*8; sample_rate*bits_per_sample
	private static final int RECORDER_SAMPLERATE = 64000;
	
	public static void rawToWave(final File rawFile, final File waveFile) throws IOException {

		byte[] rawData = new byte[(int) rawFile.length()];
		DataInputStream input = null;
		try {
			input = new DataInputStream(new FileInputStream(rawFile));
			input.read(rawData);
		} finally {
			if (input != null) {
				input.close();
			}
		}

		DataOutputStream output = null;
		try {
			output = new DataOutputStream(new FileOutputStream(waveFile));
			// WAVE header
			// see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
			writeString(output, "RIFF"); // chunk id
			writeInt(output, 36 + rawData.length); // chunk size
			writeString(output, "WAVE"); // format
			writeString(output, "fmt "); // subchunk 1 id
			writeInt(output, 16); // subchunk 1 size
			writeShort(output, (short) 1); // audio format (1 = PCM)
			writeShort(output, (short) 1); // number of channels
			writeInt(output, 8000); // sample rate
			writeInt(output, RECORDER_SAMPLERATE); // byte rate
			writeShort(output, (short) 1/*2*/); // block align
			writeShort(output, (short) 8/*16*/); // bits per sample
			writeString(output, "data"); // subchunk 2 id
			writeInt(output, rawData.length); // subchunk 2 size
			// Audio data (conversion big endian -> little endian)
			short[] shorts = new short[rawData.length / 2];
			ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
			ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
			for (short s : shorts) {
				bytes.putShort(s);
			}
			output.write(fullyReadFileToBytes(rawFile));
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}
	public static byte[] fullyReadFileToBytes(File f) throws IOException {
		int size = (int) f.length();
		byte bytes[] = new byte[size];
		byte tmpBuff[] = new byte[size];
		FileInputStream fis= new FileInputStream(f);
		try { 

			int read = fis.read(bytes, 0, size);
			if (read < size) {
				int remain = size - read;
				while (remain > 0) {
					read = fis.read(tmpBuff, 0, remain);
					System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
					remain -= read;
				} 
			} 
		}  catch (IOException e){
			throw e;
		} finally { 
			fis.close();
		} 

		return bytes;
	} 
	public static void writeInt(final DataOutputStream output, final int value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
		output.write(value >> 16);
		output.write(value >> 24);
	}

	public static void writeShort(final DataOutputStream output, final short value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
	}

	public static void writeString(final DataOutputStream output, final String value) throws IOException {
		for (int i = 0; i < value.length(); i++) {
			output.write(value.charAt(i));
		}
	}
}
