package mopar;
public class Tile {
   public int x;
   public int y;

   public Tile(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public boolean equals(Object o) {
      Tile other = (Tile)o;
      return other.x == this.x && other.y == this.y;
   }

   public int hashCode() {
      return this.x << 16 + this.y;
   }

   public String toString() {
      return this.x + "," + this.y;
   }

   public int distanceTo(Tile other) {
      return other == null?-1:(int)Math.sqrt(Math.pow((double)(this.x - other.x), 2.0D) + Math.pow((double)(this.y - other.y), 2.0D));
   }

   public boolean inArea(Area a) {
      return a.inArea(this);
   }

   public Tile randomize(int xOffset, int yOffset) {
      int newx = this.x + (int)Math.random() * (xOffset + 1) + (int)Math.random() * (xOffset + 1) * -1;
      int newy = this.y + (int)Math.random() * (yOffset + 1) + (int)Math.random() * (yOffset + 1) * -1;
      return new Tile(newx, newy);
   }

   public Tile clone() {
      return new Tile(this.x, this.y);
   }
}
