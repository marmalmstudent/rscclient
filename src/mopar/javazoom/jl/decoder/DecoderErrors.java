package mopar.javazoom.jl.decoder;

import mopar.javazoom.jl.decoder.JavaLayerErrors;

public interface DecoderErrors extends JavaLayerErrors {
   int UNKNOWN_ERROR = 512;
   int UNSUPPORTED_LAYER = 513;
}
