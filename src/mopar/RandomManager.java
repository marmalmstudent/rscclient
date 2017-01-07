package mopar;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RandomManager extends AbstractManager {
	/*
   private Bot bot;
   public List randomHandlers = new ArrayList();
   public static File randomDir;
   private boolean safeToRun = true;
   private boolean loadingRandoms = false;

   public static synchronized void log(String message) {
      Bot.log(message);
   }

   public RandomManager(Bot bot) {
      this.bot = bot;
   }

   public void loadRandomHandlers() {
      this.loadingRandoms = true;
      this.randomHandlers.clear();
      System.gc();

      try {
         log("");
         ClassAggregator e = new ClassAggregator(randomDir, RandomHandler.class, false);
         Iterator i$ = e.iterator();

         while(i$.hasNext()) {
            Class c = (Class)i$.next();
            this.addHandler(c);
         }

         log("");
         log("Loaded " + this.randomHandlers.size() + " random handlers");
      } catch (Exception var4) {
         log("General exception in loadRandomHandlers()");
         var4.printStackTrace();
      }

      this.loadingRandoms = false;
   }

   public void processRandomHandlers() {
      if(!this.loadingRandoms) {
         Iterator i$ = this.randomHandlers.iterator();

         while(i$.hasNext()) {
            RandomHandler rh = (RandomHandler)i$.next();
            if(this.run(rh) > -1L) {
               this.safeToRun = false;
               return;
            }
         }
      }

      this.safeToRun = true;
   }

   public boolean safeToRun() {
      return this.safeToRun && !this.loadingRandoms;
   }

   public boolean handlerExists(String classname) {
      Iterator i$ = this.randomHandlers.iterator();

      RandomHandler rh;
      do {
         if(!i$.hasNext()) {
            return false;
         }

         rh = (RandomHandler)i$.next();
      } while(!rh.getClass().getName().equalsIgnoreCase(classname));

      return true;
   }

   public void addHandler(RandomHandler rh) {
      log("--Random Handler: " + rh.getClass().getName());
      rh.setManager(this);
      this.randomHandlers.add(rh);
   }

   public void addHandler(Class clazz) {
      try {
         if(RandomHandler.class.isAssignableFrom(clazz)) {
            RandomHandler e = (RandomHandler)clazz.newInstance();
            this.addHandler(e);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public RandomHandler removeHandler(String classname) {
      Iterator i$ = this.randomHandlers.iterator();

      RandomHandler rh;
      do {
         if(!i$.hasNext()) {
            return null;
         }

         rh = (RandomHandler)i$.next();
      } while(!rh.getClass().getName().equalsIgnoreCase(classname));

      this.remove(rh);
      return rh;
   }

   static {
      randomDir = new File(Bot.workingDir + "Randoms");
   }
   */
}
