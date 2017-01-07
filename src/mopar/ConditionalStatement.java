package mopar;
public interface ConditionalStatement {
   boolean applies();

   Priority getPriority();

   void setPriority(Priority var1);
}
