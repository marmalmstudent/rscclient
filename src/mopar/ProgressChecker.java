package mopar;
import java.io.File;

class ProgressChecker extends Thread {
   public String filetocheck;
   public long filelength;

   public ProgressChecker(String filename, long length) {
      this.filetocheck = filename;
      this.filelength = length;
      this.start();
   }

   public void run() {
      try {
         long e = 0L;
         long lastfiledone = 0L;
         boolean percentage = false;
         File file = new File(this.filetocheck);

         while(e < this.filelength) {
            Thread.sleep(1000L);
            if(e < lastfiledone) {
               return;
            }

            lastfiledone = e;
            e = file.length();
            int percentage1 = (int)((double)e / (double)this.filelength * 100.0D);
            Bot.bot.drawLoadingBarText(percentage1, "Downloading file - " + percentage1 + "% done");
            //Bot._instance.method13(percentage1, (byte)4, "Downloading file - " + percentage1 + "% done");
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }
}
