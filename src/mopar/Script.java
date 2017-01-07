package mopar;
public abstract class Script extends AbstractManagedObject {
   public static final int SCRIPT_FINISH = -1;
   public static String[] commands = null;

   public abstract boolean start(String var1, String[] var2);

   public void end() {
   }
}
