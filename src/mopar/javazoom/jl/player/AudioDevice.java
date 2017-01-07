package mopar.javazoom.jl.player;

import mopar.javazoom.jl.decoder.Decoder;
import mopar.javazoom.jl.decoder.JavaLayerException;

public interface AudioDevice {
   void open(Decoder var1) throws JavaLayerException;

   boolean isOpen();

   void write(short[] var1, int var2, int var3) throws JavaLayerException;

   void close();

   void flush();

   int getPosition();
}
