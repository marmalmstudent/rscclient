package mopar;
public abstract class AbstractManagedObject extends AccessorMethods implements ManagedObject {
	/*
   private AbstractManager manager;
   private long lastRan = 0L;
   private long pauseTime = 0L;
   protected static MessageSystem messageSystem = new MessageSystem();

   public static void log(String message) {
      Bot.log(message);
   }

   public void setManager(AbstractManager val) {
      this.manager = val;
   }

   public AbstractManager getManager() {
      return this.manager;
   }

   protected void mapMethod(String method, ConditionalStatement cs) {
      this.getManager().mapMethod(method, cs, this);
   }

   protected void mapMethod(String method, Area a) {
      this.mapMethod(method, (ConditionalStatement)(new AreaConditionalStatement(a)));
   }

   protected void mapMethod(String method, String appliesMethod) {
      this.mapMethod(method, (ConditionalStatement)(new ReflectionConditionalStatement(appliesMethod, this)));
   }

   public abstract long run(long var1);

   public void setPauseTime(long x) {
      this.pauseTime = x;
   }

   public long getPauseTime() {
      return this.pauseTime;
   }

   public void setLastRan(long l) {
      this.lastRan = l;
   }

   public long getLastRan() {
      return this.lastRan;
   }
   */
}
