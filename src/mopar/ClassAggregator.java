package mopar;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClassAggregator implements Iterable {
   private File classDir;
   private boolean recursiveSearch;
   private Class filterClass;
   private static FileFilter filter = new ClassFilter();
   private Map classFiles;

   public ClassAggregator(File f, boolean recursive) {
      this(f, (Class)null, recursive);
   }

   public ClassAggregator(File f, Class c, boolean recursive) {
      this.classDir = null;
      this.recursiveSearch = false;
      this.filterClass = null;
      this.classFiles = new HashMap();
      this.setDir(f);
      this.setRecursiveSearch(recursive);
      this.setFilterClass(c);
      this.loadFiles();
   }

   public Class getFilterClass() {
      return this.filterClass;
   }

   public void setFilterClass(Class c) {
      this.filterClass = c;
   }

   public File getDir() {
      return this.classDir;
   }

   public void setDir(File f) {
      if(f.isDirectory()) {
         this.classDir = f;
      }

   }

   public boolean isRecursiveSearch() {
      return this.recursiveSearch;
   }

   public void setRecursiveSearch(boolean b) {
      this.recursiveSearch = b;
   }

   public void loadFiles() {
      this.loadFiles(this.getDir());
   }

   private void loadFiles(File dir) {
      try {
         URLClassLoader e = new URLClassLoader(new URL[]{dir.toURL()});
         File[] arr$ = dir.listFiles(filter);
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            File f = arr$[i$];
            if(f.isDirectory()) {
               if(this.isRecursiveSearch()) {
                  this.loadFiles(dir);
               }
            } else {
               String className = f.getName().substring(0, f.getName().indexOf("."));
               Class c = e.loadClass(className);
               if(this.filterClass == null || this.filterClass != null && this.filterClass.isAssignableFrom(c)) {
                  this.classFiles.put(className, c);
               }
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public Iterator iterator() {
      return this.classFiles.values().iterator();
   }
}
