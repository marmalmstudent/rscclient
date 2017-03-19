package org;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.util.misc;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class AudioReader extends InputStream {

    private byte[] dataArray;
    private int offset;
    private int length;

    public AudioReader() {
    	AudioPlayer.player.start(this);
    }

    public void stopAudio() {
    	AudioPlayer.player.stop(this);
    }

    public void loadData(byte[] abyte0, int start_idx, int sound_length) {
        dataArray = abyte0;
        offset = start_idx;
        length = start_idx + sound_length;

        byte[] sound = new byte[sound_length];
        for (int i = 0; i < sound_length; ++i)
        	sound[i] = abyte0[start_idx+i];
        try
        {
        	misc.writeToFile(sound, "src/org/conf/sound_"+start_idx+"_"+sound_length+".dat");
        	File f1 = new File("src/org/conf/sound_"+start_idx+"_"+sound_length+".dat"); // The location of your PCM file
        	File f2 = new File("src/org/conf/sound_"+start_idx+"_"+sound_length+".wav"); // The location where you want your WAV file
        	misc.rawToWave(f1, f2);
        } catch (IOException ioe)
        {
        	ioe.printStackTrace();
        } 
    }

    public int read(byte[] abyte0, int start_idx, int sound_length) {
        for (int idx = 0; idx < sound_length; idx++) {
            if (offset < length) {
                abyte0[start_idx + idx] = dataArray[offset++];
            } else {
                abyte0[start_idx + idx] = -1;
            }
        }
        return sound_length;
    }

    public int read() {
        byte abyte0[] = new byte[1];
        read(abyte0, 0, 1);
        return abyte0[0];
    }
}
