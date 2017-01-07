package mopar;
public abstract class RandomHandler extends AbstractManagedObject {
   public static void log(String msg) {
      Bot.log("# " + msg);
   }
}
