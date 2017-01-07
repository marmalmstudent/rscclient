package mopar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageSystem {
   private Map messages = new HashMap();
   private Map objects = new HashMap();

   public String peek(String key) {
      String msg = (String)this.messages.get(key);
      return msg == null?"":msg;
   }

   public String get(String key) {
      String msg = (String)this.messages.remove(key);
      return msg == null?"":msg;
   }

   public void send(String key, String val) {
      this.messages.put(key, val);
   }

   public Object peekObject(String key) {
      return this.objects.get(key);
   }

   public Object getObject(String key) {
      return this.objects.remove(key);
   }

   public void setObject(String key, Object value) {
      this.objects.put(key, value);
   }

   public int messageCount() {
      return this.messages.size();
   }

   public int objectCount() {
      return this.objects.size();
   }

   public Iterator messages() {
      return this.messages.values().iterator();
   }

   public Iterator objects() {
      return this.objects.values().iterator();
   }
}
