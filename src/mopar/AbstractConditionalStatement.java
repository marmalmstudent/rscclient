package mopar;
public abstract class AbstractConditionalStatement implements ConditionalStatement {
   protected Priority p;

   public AbstractConditionalStatement() {
      this.p = Priority.MEDIUM;
   }

   public Priority getPriority() {
      return this.p;
   }

   public void setPriority(Priority p) {
      this.p = p;
   }
}
