package mopar;
import java.io.PrintStream;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console extends PrintStream {
   private JTextArea jt;
   private JScrollPane scroller;

   public Console(JTextArea jt, JScrollPane scroller) {
      super(System.out);
      this.jt = jt;
      this.scroller = scroller;
   }

   public void println(Object o) {
      this.jt.append(o.toString() + "\n");
      this.adjustScroller();
   }

   public void println(String s) {
      this.println((Object)s);
   }

   public void println() {
      this.println((Object)"println\n");
   }

   public void print(Object o) {
      this.jt.append(o.toString());
   }

   public void print(String s) {
      this.print((Object)s);
   }

   public void adjustScroller() {
      this.scroller.getVerticalScrollBar().setValue(this.scroller.getVerticalScrollBar().getMaximum());
   }
}
