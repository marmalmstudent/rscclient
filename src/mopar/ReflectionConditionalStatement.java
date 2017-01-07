package mopar;
import java.lang.reflect.Method;

public class ReflectionConditionalStatement extends AbstractConditionalStatement {
   private Method m;
   private Object o;

   public ReflectionConditionalStatement(String meth, Object o) {
      try {
         Class e = o.getClass();
         this.m = e.getDeclaredMethod(meth, new Class[0]);
         this.o = o;
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public boolean applies() {
      try {
         return ((Boolean)this.m.invoke(this.o, new Object[0])).booleanValue();
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }
}
