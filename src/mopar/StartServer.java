package mopar;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;
import mopar.sign.*;

public class StartServer extends Thread {
   private Method main;

   public StartServer() throws Exception {
	   /*
      String workingDir = "";
      File classDir;
      if(Bot.isApplet()) {
         workingDir = "src/server/";
         classDir = new File(workingDir);
         if(!classDir.exists()) {
            new Update("http://www.moparscape.org/cache/hybrid2.zip", "hybrid2.zip", signlink.findcachedir(), true, false);
         }
      } else {
         classDir = new File("./src/Server/");
      }

      ClassAggregator ca = new ClassAggregator(classDir, false);
      Iterator i$ = ca.iterator();

      while(i$.hasNext()) {
         Class c = (Class)i$.next();
         if(c.getName().equals("server")) {
            Class[] mainArgType = new Class[]{(new String[0]).getClass()};
            this.main = c.getMethod("main", mainArgType);
            if(Bot.isApplet()) {
               c.getDeclaredField("workingDir").set("", workingDir);
            }
         }
      }

      this.start();
      */
   }

   public void run() {
      try {
         this.main.invoke("main", new Object[]{new String[0]});
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }
}
