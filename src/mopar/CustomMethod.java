package mopar;
import java.lang.reflect.Method;

public class CustomMethod implements Comparable {
   private ConditionalStatement cs;
   private Method m;

   public CustomMethod(ConditionalStatement cs, Method m) {
      this.cs = cs;
      this.m = m;
   }

   public Method getMethod() {
      return this.m;
   }

   public ConditionalStatement getConditionalStatement() {
      return this.cs;
   }

   public int compareTo(CustomMethod cm) {
      return this.cs.getPriority().compareTo(cm.getConditionalStatement().getPriority());
   }

   public int compareTo(Object o) {
	   // TODO Auto-generated method stub
	   return 0;
   }
}
