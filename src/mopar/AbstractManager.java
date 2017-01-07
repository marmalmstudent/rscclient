package mopar;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractManager {
	/*
   public Map methods = new HashMap();
   public Class[] defaultArgs;

   public AbstractManager() {
      this.defaultArgs = new Class[]{Long.TYPE};
   }

   public static void log(String message) {
      Bot.log("# " + message);
   }

   public void mapMethod(String methName, ConditionalStatement cs, ManagedObject o) {
      try {
         Class e = o.getClass();
         Method m = e.getDeclaredMethod(methName, this.defaultArgs);
         CustomMethod cm = new CustomMethod(cs, m);
         this.addMethod(o, cm);
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void addMethod(ManagedObject amo, CustomMethod cm) {
      Object temp;
      if(!this.methods.containsKey(amo)) {
         temp = new ArrayList();
         this.methods.put(amo, temp);
      } else {
         temp = (List)this.methods.get(amo);
      }

      ((List)temp).add(cm);
      Collections.sort((List)temp);
   }

   public long run(ManagedObject amo) {
      try {
         long e = System.currentTimeMillis();
         boolean ran = false;
         if(amo.getLastRan() + amo.getPauseTime() < e) {
            if(this.methods.get(amo) != null) {
               Iterator i$ = ((List)this.methods.get(amo)).iterator();

               while(i$.hasNext()) {
                  CustomMethod cm = (CustomMethod)i$.next();
                  if(cm.getConditionalStatement().applies()) {
                     amo.setPauseTime(((Long)cm.getMethod().invoke(amo, new Object[]{Long.valueOf(e)})).longValue());
                     ran = true;
                     break;
                  }
               }
            }

            if(!ran) {
               amo.setPauseTime(amo.run(e));
            }

            amo.setLastRan(e);
            return amo.getPauseTime();
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return amo.getPauseTime();
   }

   protected void remove(ManagedObject amo) {
      EventManager.removeListeners(amo);
      this.methods.remove(amo);
   }*/
}
