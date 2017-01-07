package mopar;
public interface ManagedObject {
   long getLastRan();

   void setLastRan(long var1);

   long getPauseTime();

   void setPauseTime(long var1);

   long run(long var1);

   AbstractManager getManager();

   void setManager(AbstractManager var1);
}
