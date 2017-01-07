package mopar;
public abstract class Controller extends AbstractManagedObject {
   public ScriptManager scriptManager;
   public RandomManager randomManager;

   public void unload() {
   }

   public void load() {
   }

   public void setScriptManager(ScriptManager sm) {
      this.scriptManager = sm;
   }

   public void setRandomManager(RandomManager rm) {
      this.randomManager = rm;
   }

   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   public RandomManager getRandomManager() {
      return this.randomManager;
   }
}
