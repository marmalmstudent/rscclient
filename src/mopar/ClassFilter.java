package mopar;
import java.io.File;
import java.io.FileFilter;

class ClassFilter implements FileFilter {
   public boolean accept(File f) {
      String name = f.getName();
      return f.isDirectory() || name.endsWith(".class") && name.indexOf("$") == -1;
   }
}
