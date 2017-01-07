package mopar;
import java.util.ListIterator;

public class KeyActionManager {
	/*
   private Bot rs;
   private long timeOut = -1L;
   private long lastStart;
   private ListIterator keyActions;

   public KeyActionManager(Bot rs) {
      this.rs = rs;
   }

   public void setKeyActions(ListIterator keyActions) {
      this.keyActions = keyActions;
   }

   public boolean isReady() {
      if(this.keyActions != null) {
         if(this.keyActions.hasNext()) {
            return false;
         }

         this.keyActions = null;
         this.timeOut = -1L;
      }

      return true;
   }

   public void processKeyActions() {
      if(this.keyActions != null) {
         KeyAction e;
         if(this.keyActions.hasPrevious()) {
            e = (KeyAction)this.keyActions.previous();
            this.lastStart = System.currentTimeMillis();
            this.timeOut = e.releaseTime;
            Bot.am.releaseKey(e.c, e.code, e.modifiers);
            this.keyActions.remove();
         } else if(this.keyActions.hasNext()) {
            try {
               if(this.nextKeyActionReady()) {
                  e = (KeyAction)this.keyActions.next();
                  this.lastStart = System.currentTimeMillis();
                  this.timeOut = e.holdTime;
                  Bot.am.pressKey(e.c, e.code, e.modifiers);
               }
            } catch (Exception var2) {
               Bot.log("Exception in processKeyActions()");
               var2.printStackTrace();
            }
         }
      }

   }

   private boolean nextKeyActionReady() {
      return System.currentTimeMillis() - this.lastStart >= this.timeOut;
   }
   */
}
