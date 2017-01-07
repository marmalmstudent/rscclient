package mopar;
public class KeyAction {
   private final long minTime = 120L;
   private final long maxTime = 170L;
   public char c;
   public int code = 0;
   public int modifiers = 0;
   public long holdTime = 120L + (long)(Math.random() * 50.0D);
   public long releaseTime = 120L + (long)(Math.random() * 50.0D);

   public KeyAction(char c) {
      this.c = c;
   }

   public KeyAction(char c, int code) {
      this.c = c;
      this.code = code;
   }

   public KeyAction(char c, int code, int modifiers) {
      this.c = c;
      this.code = code;
      this.modifiers = modifiers;
   }

   public KeyAction(char c, int code, long holdTime, long releaseTime) {
      this.c = c;
      this.code = code;
      this.holdTime = holdTime;
      this.releaseTime = releaseTime;
   }

   public KeyAction(char c, int code, int modifiers, long holdTime, long releaseTime) {
      this.c = c;
      this.code = code;
      this.modifiers = modifiers;
      this.holdTime = holdTime;
      this.releaseTime = releaseTime;
   }
}
