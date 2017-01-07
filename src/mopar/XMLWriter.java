package mopar;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class XMLWriter {
   protected String rootName;
   protected String itemName;
   protected String childName;
   protected FileOutputStream fOut;

   public XMLWriter(String rootName, String childName, String itemName, String fileName) {
      this.rootName = rootName;
      this.childName = childName;
      this.itemName = itemName;

      try {
         this.init(fileName);
      } catch (Exception var6) {
         Bot.log("I/O error in XMLWriter.init()");
         var6.printStackTrace();
      }
   }

   protected void init(String fileName) throws Exception {
      this.fOut = new FileOutputStream(fileName, false);
      this.writeLine("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
      this.writeLine("<" + this.rootName + ">");
   }

   public void closeXML() {
      this.writeLine("</" + this.rootName + ">");
   }

   protected void writeLine(String line) {
      try {
         line = line + "\n";
         this.fOut.write(line.getBytes());
      } catch (Exception var3) {
         Bot.log("I/O error in XMLWriter.writeLine()");
         var3.printStackTrace();
      }

   }

   public void addItem(Hashtable args, Vector children) {
      String toWrite = "<" + this.itemName + " ";
      Enumeration itemKeys = args.keys();
      Iterator itemVals = args.values().iterator();

      String val;
      while(itemKeys.hasMoreElements() && itemVals.hasNext()) {
         String childVals = this.strip((String)itemKeys.nextElement());
         val = this.strip((String)itemVals.next());
         toWrite = toWrite + childVals + "=\"" + val + "\"";
         if(itemKeys.hasMoreElements()) {
            toWrite = toWrite + " ";
         }
      }

      this.writeLine(toWrite + ">");
      Iterator childVals1 = children.iterator();
      Bot.log("Adding " + children.size() + " actions");

      while(childVals1.hasNext()) {
         val = this.strip((String)childVals1.next());
         String write = "<" + this.childName + ">" + val + "</" + this.childName + ">";
         this.writeLine(write);
      }

      this.writeLine("</" + this.itemName + ">");
   }

   protected String strip(String toStrip) {
      String[][] stripTags = new String[][]{{"\"", "\'"}, {"&", " and "}};

      for(int i = 0; i < stripTags.length; ++i) {
         toStrip = toStrip.replaceAll(stripTags[i][0], stripTags[i][1]);
      }

      return toStrip;
   }
}
