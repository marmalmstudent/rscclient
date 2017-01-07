package mopar;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import mopar.javazoom.jl.player.Player;

public class MP3Player extends Thread {
   private String filename;
   private Player mp3Player;
   private boolean started;
   private boolean loop;

   public MP3Player(String name, boolean loop) {
      this.filename = name;
      this.setLoop(loop);
      this.start();
   }

   public void run() {
      try {
         this.started = true;

         while(this.loop) {
            this.mp3Player = new Player(this.getFis(this.filename));
            this.mp3Player.play();
         }
      } catch (Exception var2) {
         Bot.log("mp3 Error!");
      }

      this.started = false;
   }

   public boolean isRunning() {
      return this.started;
   }

   public void close() {
      this.setLoop(false);
      this.mp3Player.close();
   }

   public void setLoop(boolean loop) {
      this.loop = loop;
   }

   private InputStream getFis(String fileName) {
      try {
         File ioe = new File(fileName);
         return new FileInputStream(ioe);
      } catch (Exception var3) {
         Bot.log("file Error!");
         return null;
      }
   }
}
