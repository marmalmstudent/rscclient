package mopar;
public class Area {
   private int x1;
   private int x2;
   private int y1;
   private int y2;

   public Area(Tile t, Tile t2) {
      this(t.x, t2.x, t.y, t2.y);
   }

   public Area(int x1, int x2, int y1, int y2) {
      if(x2 > x1) {
         this.x1 = x2;
         this.x2 = x1;
      } else {
         this.x1 = x1;
         this.x2 = x2;
      }

      if(y2 > y1) {
         this.y1 = y2;
         this.y2 = y1;
      } else {
         this.y1 = y1;
         this.y2 = y2;
      }

   }

   public boolean inArea(Tile t) {
      return this.inArea(t.x, t.y);
   }

   public boolean inArea(int x, int y) {
      return x < this.x1 && x > this.x2 && y < this.y1 && y > this.y2;
   }

   /** @deprecated */
   public boolean withinArea(Tile t) {
      return this.withinArea(t.x, t.y);
   }

   /** @deprecated */
   public boolean withinArea(int x, int y) {
      return this.inArea(x, y);
   }
}
