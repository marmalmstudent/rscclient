package mopar.javazoom.jl.player;

import mopar.javazoom.jl.player.AudioDeviceBase;

public class NullAudioDevice extends AudioDeviceBase {
   public int getPosition() {
      return 0;
   }
}
