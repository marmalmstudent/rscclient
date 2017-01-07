package org.rscdaemon.client;

import java.io.InputStream;

public class AudioReader extends InputStream {

    private byte[] dataArray;
    private int offset;
    private int length;

    public AudioReader() {
    }

    public void stopAudio() {
    }

    public void loadData(byte[] abyte0, int start_idx, int sound_length) {
        dataArray = abyte0;
        offset = start_idx;
        length = start_idx + sound_length;
        System.out.println("\n\n\n\n[");
        for (int i =start_idx; i<start_idx+sound_length-1; i++)
        {
        	System.out.print(abyte0[i]+", ");
        }
        System.out.println(abyte0[start_idx+sound_length]+"]\n\n\n\n");
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
