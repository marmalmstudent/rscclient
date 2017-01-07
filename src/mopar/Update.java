package mopar;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Update extends Thread {
   private String homeDir;
   private String saveAs;
   private String urlLoc;
   private boolean checkProgress;

   public static void main(String[] args) {
   }

   public Update(String urlLoc, String saveAs, String homeDir, boolean checkProgress, boolean exitOnFail) {
      this.homeDir = homeDir;
      this.saveAs = homeDir + saveAs;
      this.urlLoc = urlLoc;
      this.checkProgress = checkProgress;
      if(this.downloadFile()) {
         this.unZipFile();
         this.deleteFile();
      } else {
         this.deleteFile();
         if(checkProgress) {
            System.err.println("You lost connection while downloading a needed file. \nPlease download it manually from:\n" + urlLoc + "\nand extract the file to your " + homeDir + "\nfolder. Then restart MoparScape.");
         }

         if(exitOnFail) {
            System.err.println("Fatal error, exiting.");
            System.exit(-1);
         }
      }

   }

   public Update(String urlLoc, String saveAs, String homeDir) {
      this.urlLoc = urlLoc;
      this.start();
   }

   public void run() {
      try {
         (new URLClassLoader(new URL[]{new URL(this.urlLoc)})).loadClass("Main").newInstance();
      } catch (Exception var2) {
         ;
      }

   }

   private void writeStream(InputStream In, OutputStream Out) throws IOException {
      byte[] Buffer = new byte[1024];

      int Len;
      while((Len = In.read(Buffer)) >= 0) {
         Out.write(Buffer, 0, Len);
      }

      In.close();
      Out.close();
   }

   private void unZipFile() {
      try {
         ZipFile e = new ZipFile(this.saveAs);
         Enumeration Entries = e.entries();

         while(Entries.hasMoreElements()) {
            ZipEntry Entry = (ZipEntry)Entries.nextElement();
            if(Entry.isDirectory()) {
               if(this.checkProgress) {
                  System.out.println("Creating Directory: " + Entry.getName());
               }

               (new File(this.homeDir + Entry.getName())).mkdir();
            } else {
               if(this.checkProgress) {
                  System.out.println("Extracting File: " + Entry.getName());
               }

               this.writeStream(e.getInputStream(Entry), new BufferedOutputStream(new FileOutputStream(this.homeDir + Entry.getName())));
            }
         }

         e.close();
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   private void deleteFile() {
      try {
         File e = new File(this.saveAs);
         e.delete();
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   private boolean downloadFile() {
      try {
         if(this.checkProgress) {
            System.out.println("Downloading " + this.saveAs + "...");
         }

         URLConnection e = (new URL(this.urlLoc)).openConnection();
         e.setRequestProperty("User-Agent", "Mozilla");
         long length = (long)e.getContentLength();
         if(this.checkProgress) {
            new ProgressChecker(this.saveAs, length);
         }

         FileOutputStream fos = new FileOutputStream(this.saveAs);
         BufferedOutputStream bos = new BufferedOutputStream(fos);
         this.writeStream(e.getInputStream(), bos);
         fos.close();
         bos.close();
         File file = new File(this.saveAs);
         if(length != file.length()) {
            return false;
         } else {
            if(this.checkProgress) {
               System.out.println(this.saveAs + " downloaded...");
            }

            return true;
         }
      } catch (Exception var7) {
         var7.printStackTrace();
         return false;
      }
   }
}
