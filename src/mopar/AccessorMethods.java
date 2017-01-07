package mopar;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;
import org.mudclient;

public class AccessorMethods {
   public static Bot rs;
   private static Random rand = new Random();

   public static void ping(String ip) {
      long timeStart = 0L;
      long endTime = 0L;

      try {
         InetSocketAddress l = new InetSocketAddress(ip, mudclient.portNumber);
         Socket sock = new Socket();
         timeStart = System.currentTimeMillis();
         sock.connect(l, 1500);
         endTime = System.currentTimeMillis();
         sock.close();
      } catch (UnknownHostException var9) {
         ;
      } catch (SocketTimeoutException var10) {
         log("Server " + ip + " has timed out.");
      } catch (IOException var11) {
         ;
      }

      long l1 = endTime - timeStart;
      long t = l1 == 0L?-1L:l1;
      log("Server " + ip + "\'s ping is " + "\t" + (t < 0L?"---":String.valueOf(t)));
   }

   public static void log(String message) {
      Bot.log(message);
   }

   public static void launchURL(String url) {
      String osName = System.getProperty("os.name");

      try {
         if(osName.startsWith("Mac OS")) {
            Class e = Class.forName("com.apple.eio.FileManager");
            Method browser = e.getDeclaredMethod("openURL", new Class[]{String.class});
            browser.invoke((Object)null, new Object[]{url});
         } else if(osName.startsWith("Windows")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
         } else {
            String[] var6 = new String[]{"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
            String var7 = null;

            for(int count = 0; count < var6.length && var7 == null; ++count) {
               if(Runtime.getRuntime().exec(new String[]{"which", var6[count]}).waitFor() == 0) {
                  var7 = var6[count];
               }
            }

            if(var7 == null) {
               throw new Exception("Could not find web browser");
            }

            Runtime.getRuntime().exec(new String[]{var7, url});
         }
      } catch (Exception var5) {
         log("Error opening URL");
         var5.printStackTrace();
      }

   }
   
   /*
   public boolean isMouseDown() {
      return rs.clickMode2 != 0;
   }

   public void mouse(Tile screenTile) {
      if(screenTile != null) {
         this.mouse(screenTile.x, screenTile.y);
      } else {
         this.mouse(-1, -1);
      }

   }

   public void mouse(int x, int y) {
      if(x == -1 && y == -1) {
         x = random(0, 516);
         y = random(0, 337);
      }

      if(x < 0) {
         x = random(10, 110);
      }

      if(x > 765) {
         x = 765 - random(10, 110);
      }

      if(y < 0) {
         y = random(10, 110);
      }

      if(y > 503) {
         y = 503 - random(10, 110);
      }

      if(rs.mouseX != x || rs.mouseY != y) {
         Iterator list = this.findMovementPath(rs.mouseX, rs.mouseY, x, y, 20, 30, 30, 20, 15);
         if(list != null) {
            Bot.mouseManager.setMovementProcess(list);
         } else {
            log("Mouse movement process failed, skipping");
         }
      }

      Bot.mouseManager.setPostMovementClick(new Tile(x, y));
   }

   public void clickMouse() {
      this.clickMouse(rs.mouseX, rs.mouseY);
   }

   public void clickMouse(Tile t) {
      this.clickMouse(t.x, t.y);
   }

   public void clickMouse(int x, int y) {
      MouseEvent evt = new MouseEvent(rs, 501, System.currentTimeMillis(), 0, x, y, 1, false);
      rs.mousePressed(evt);
   }

   public void mouseUp() {
      this.mouseUp(rs.mouseX, rs.mouseY);
   }

   public void mouseUp(Tile t) {
      this.mouseUp(t.x, t.y);
   }

   public void mouseUp(int x, int y) {
      MouseEvent evt = new MouseEvent(rs, 502, System.currentTimeMillis(), 0, x, y, 1, false);
      rs.mouseReleased(evt);
   }

   public void moveMouse(Tile t) {
      this.moveMouse(t.x, t.y);
   }

   public void moveMouse(int x, int y) {
      MouseEvent evt = new MouseEvent(rs, 503, System.currentTimeMillis(), 0, x, y, 1, false);
      rs.mouseMoved(evt);
   }

   public Iterator findMovementPath(int x1, int y1, int x2, int y2, int minSleepTime, int maxSleepTime, int maxDistance, int gravity, int forces) {
      ArrayList it = new ArrayList();
      double x = (double)x1;
      double y = (double)y1;
      double vx = 0.0D;
      double vy = 0.0D;

      double dist;
      for(double lastdist = 0.0D; Math.sqrt(Math.pow(Math.abs(x - (double)x2), 2.0D) + Math.pow(Math.abs(y - (double)y2), 2.0D)) >= (double)gravity; lastdist = dist) {
         dist = Math.sqrt(Math.pow(Math.abs(x - (double)x2), 2.0D) + Math.pow(Math.abs(y - (double)y2), 2.0D));
         if(lastdist != 0.0D && lastdist < dist - (double)(gravity / 2)) {
            vx = 0.0D;
            vy = 0.0D;
         }

         forces = Math.round((float)((int)Math.min((double)forces, dist)));
         vx += (Math.random() * (double)(forces * 2 + 1) - (double)forces) / Math.sqrt(2.0D);
         vy += (Math.random() * (double)(forces * 2 + 1) - (double)forces) / Math.sqrt(2.0D);
         vx += (double)gravity * ((double)x2 - x) / dist;
         vy += (double)gravity * ((double)y2 - y) / dist;
         if(Math.sqrt(Math.pow(vx, 2.0D) + Math.pow(vy, 2.0D)) > (double)maxDistance) {
            double md = (double)(maxDistance / 2) + Math.random() * (double)(maxDistance / 2);
            double v = Math.sqrt(Math.pow(vx, 2.0D) + Math.pow(vy, 2.0D));
            vx = md * vx / v;
            vy = md * vy / v;
         }

         int lastX = (int)x;
         int lastY = (int)y;
         x += vx;
         y += vy;
         if(x < 0.0D) {
            x = 0.0D;
         }

         if(y < 0.0D) {
            y = 0.0D;
         }

         if(x > 768.0D) {
            x = 767.0D;
         }

         if(y > 525.0D) {
            y = 524.0D;
         }

         if((long)lastX != Math.round(x) || (long)lastY != Math.round(y)) {
            Movement m = new Movement((int)Math.round(x), (int)Math.round(y), (long)minSleepTime + (long)(Math.random() * (double)(maxSleepTime - minSleepTime + 1)));
            it.add(m);
         }
      }

      if(x1 != x2 || y1 != y2) {
         it.add(new Movement(x2, y2, 0L));
      }

      return it.iterator();
   }

   public void setMousePos(Entity ent) {
      this.mouse(this.getScreenPosition(ent));
   }

   public void setMousePosInv(int invIndex) {
      this.mouse(this.positionFromIndex(invIndex));
   }

   public void sendText(String str) {
      ArrayList it = new ArrayList();
      char[] ca = str.toCharArray();
      char[] shiftKeys = new char[]{'~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '|', '{', '}', ':', '\"', '<', '>', '?'};

      for(int i = 0; i < ca.length; ++i) {
         int modifiers = 0;

         for(int c = 0; c < shiftKeys.length; ++c) {
            if((shiftKeys[c] == ca[i] || shiftKeys[c] == Character.toUpperCase(ca[i])) && (modifiers & 64) != 0) {
               modifiers &= 64;
            }
         }

         Character var8 = Character.valueOf(Character.toUpperCase(ca[i]));
         it.add(new KeyAction(ca[i], var8.charValue(), modifiers));
      }

      it.add(new KeyAction('\b', 10));
      Bot.keyActionManager.setKeyActions(it.listIterator());
   }

   public void pressKey(char c, int code, int modifiers) {
      KeyEvent evt = new KeyEvent(rs, 401, System.currentTimeMillis(), modifiers, code, c);
      rs.keyPressed(evt);
   }

   public void releaseKey(char c, int code, int modifiers) {
      KeyEvent evt = new KeyEvent(rs, 402, System.currentTimeMillis(), modifiers, code, c);
      rs.keyReleased(evt);
   }

   public void gainFocus() {
      rs.focusGained(new FocusEvent(rs, 31337));
   }

   public void lostFocus() {
      rs.focusLost(new FocusEvent(rs, 31337));
   }

   public Tile getPosition(Entity ent) {
      int bigX = rs.baseX + (ent.x >> 7);
      int bigY = rs.baseY + (ent.y >> 7);
      return new Tile(bigX, bigY);
   }

   public Tile positionFromIndex(int invIndex) {
      int col = invIndex % 4;
      int row = invIndex / 4;
      int x = 580 + col * 42;
      int y = 228 + row * 36;
      return new Tile(x, y);
   }

   public Tile getScreenPosition(Entity ent) {
      return this.tileToScreen(this.getPosition(ent), ent.height);
   }

   public Tile tileToScreen(Tile bigPos, int height) {
      return this.tileToScreen(bigPos.x, bigPos.y, height);
   }

   public Tile tileToScreen(int x, int y, int height) {
      return this.worldToScreen((x - rs.baseX) * 128, (y - rs.baseY) * 128, height);
   }

   private Tile worldToScreen(Tile worldPos, int height) {
      return this.worldToScreen(worldPos.x, worldPos.y, height);
   }

   private Tile worldToScreen(int x, int y, int height) {
      try {
         int e = this.unknownTileCalculation(x, y) - height;
         x -= rs.unknownInt2;
         e -= rs.unknownInt3;
         y -= rs.unknownInt4;
         int j1 = rs.getUnClass1IntArray1(rs.unknownInt5);
         int k1 = rs.getUnClass1IntArray2(rs.unknownInt5);
         int l1 = rs.getUnClass1IntArray1(rs.unknownInt6);
         int i2 = rs.getUnClass1IntArray2(rs.unknownInt6);
         int j2 = y * l1 + x * i2 >> 16;
         y = y * i2 - x * l1 >> 16;
         x = j2;
         j2 = e * k1 - y * j1 >> 16;
         y = e * j1 + y * k1 >> 16;
         if(y >= 50) {
            return new Tile(rs.getUnClass2Int7() + (x << 9) / y, rs.getUnClass2Int8() + (j2 << 9) / y);
         }
      } catch (Exception var10) {
         log("Error calculating " + x + "," + y + " to screen");
         var10.printStackTrace();
      }

      return null;
   }

   private int unknownTileCalculation(int x, int y) {
      int smallX = x >> 7;
      int smallY = y >> 7;
      int tmp = rs.unknownInt9;
      if(tmp < 3 && (rs.unknownByteTileArray[1][smallX][smallY] & 2) == 2) {
         ++tmp;
      }

      int x2 = x & 127;
      int y2 = y & 127;
      int tmp1 = rs.unknownIntTileArray[tmp][smallX][smallY] * (128 - x2) + rs.unknownIntTileArray[tmp][smallX + 1][smallY] * x2 >> 7;
      int tmp2 = rs.unknownIntTileArray[tmp][smallX][smallY + 1] * (128 - x2) + rs.unknownIntTileArray[tmp][smallX + 1][smallY + 1] * x2 >> 7;
      return tmp1 * (128 - y2) + tmp2 * y2 >> 7;
   }

   public Tile worldToMinimap(Tile t) {
      return this.worldToMinimap(t.x, t.y);
   }

   public Tile worldToMinimap(int x, int y) {
      x -= rs.baseX;
      y -= rs.baseY;
      int i = x * 4 + 2 - this.myPlayer().x / 32;
      int j = y * 4 + 2 - this.myPlayer().y / 32;
      int k = rs.minimapInt1 + rs.minimapInt2 & 2047;
      int l = j * j + i * i;
      if(l > 6400) {
         return null;
      } else {
         int i1 = rs.getUnClass1IntArray1(k);
         int j1 = rs.getUnClass1IntArray2(k);
         i1 = i1 * 256 / (rs.minimapInt3 + 256);
         j1 = j1 * 256 / (rs.minimapInt3 + 256);
         int k1 = j * i1 + i * j1 >> 16;
         int l1 = j * j1 - i * i1 >> 16;
         int screenx = 550 + 94 + k1 + 4;
         int screeny = 4 + (83 - l1 - 4);
         return new Tile(screenx, screeny);
      }
   }

   public Player myPlayer() {
      return client.myPlayer;
   }

   public int distanceTo(Tile other) {
      return other.distanceTo(this.getPosition(this.myPlayer()));
   }

   public int distanceTo(int x, int y) {
      return this.distanceTo(new Tile(x, y));
   }

   public boolean inArea(Area a) {
      return a.inArea(this.getPosition(this.myPlayer()));
   }

   public boolean inArea(Tile topLeft, Tile bottomRight) {
      return this.inArea(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
   }

   public boolean inArea(int x1, int y1, int x2, int y2) {
      Tile pos = this.getPosition(this.myPlayer());
      return pos.x >= x1 && pos.y <= y1 && pos.x <= x2 && pos.y >= y2;
   }

   public boolean isMoving() {
      return rs.destX != 0;
   }

   public Player[] getPlayers() {
      return rs.playerArray;
   }

   public Player findPlayerByName(String name) {
      Player[] arr$ = rs.playerArray;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Player p = arr$[i$];
         if(p != null && p.playerName != null && p.playerName.equals(name)) {
            return p;
         }
      }

      return null;
   }

   public Player findPlayerByCombat(int combat) {
      Player[] arr$ = this.getPlayers();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Player p = arr$[i$];
         if(p != null && p.combatLevel == combat) {
            return p;
         }
      }

      return null;
   }

   public int getDirection(Entity ent) {
      return ent.turnDirection;
   }

   public int getAnimation(Entity ent) {
      return ent.anim;
   }

   public Player getInteractingPlayer(Entity ent) {
      if(ent.interactingEntity >= '耀') {
         int idx = ent.interactingEntity - '耀';
         if(idx == rs.unknownInt10) {
            idx = rs.myPlayerIndex;
         }

         if(idx >= 0 && idx <= this.getPlayers().length) {
            return this.getPlayers()[idx];
         }
      }

      return null;
   }

   public NPC getInteractingNPC(Entity ent) {
      if(ent.interactingEntity < '耀') {
         int idx = ent.interactingEntity;
         if(idx >= 0 && idx <= this.getNPCs().length) {
            return this.getNPCs()[idx];
         }
      }

      return null;
   }

   public boolean inCombat(Entity ent) {
      return ent.loopCycleStatus > client.loopCycle;
   }

   public NPC[] getNPCs() {
      return rs.npcArray;
   }

   public NPC findNPCByName(String name) {
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcName != null && npc.npcDescription.npcName.equals(name)) {
            return npc;
         }
      }

      return null;
   }

   public NPC findNPCByNames(String[] names) {
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null) {
            String[] arr$1 = names;
            int len$1 = names.length;

            for(int i$1 = 0; i$1 < len$1; ++i$1) {
               String name = arr$1[i$1];
               if(npc.npcDescription.npcName != null && npc.npcDescription.npcName.equals(name)) {
                  return npc;
               }
            }
         }
      }

      return null;
   }

   public ArrayList findNPCsByName(String name) {
      ArrayList npcs = new ArrayList();
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcName != null && npc.npcDescription.npcName.equals(name)) {
            npcs.add(npc);
         }
      }

      return npcs;
   }

   public NPC findNearestNPC() {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null) {
            if(near == null) {
               near = npc;
            } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
               near = npc;
            }
         }
      }

      return near;
   }

   public NPC findNearestNPCByName(String name) {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcName != null && npc.npcDescription.npcName.equals(name)) {
            if(near == null) {
               near = npc;
            } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
               near = npc;
            }
         }
      }

      return near;
   }

   public NPC findNearestNPCByNames(String[] names) {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null) {
            String[] arr$1 = names;
            int len$1 = names.length;

            for(int i$1 = 0; i$1 < len$1; ++i$1) {
               String name = arr$1[i$1];
               if(npc.npcDescription.npcName != null && npc.npcDescription.npcName.equals(name)) {
                  if(near == null) {
                     near = npc;
                  } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
                     near = npc;
                  }
               }
            }
         }
      }

      return near;
   }

   public NPC findNPCByCombat(int combatLevel) {
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcCombat == combatLevel) {
            return npc;
         }
      }

      return null;
   }

   public NPC findNPCByType(int type) {
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcType == (long)type) {
            return npc;
         }
      }

      return null;
   }

   public NPC findNPCByTypes(int[] types) {
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null) {
            int[] arr$1 = types;
            int len$1 = types.length;

            for(int i$1 = 0; i$1 < len$1; ++i$1) {
               int type = arr$1[i$1];
               if(npc.npcDescription.npcType == (long)type) {
                  return npc;
               }
            }
         }
      }

      return null;
   }

   public NPC findNearestNPCByType(int type) {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcType == (long)type) {
            if(near == null) {
               near = npc;
            } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
               near = npc;
            }
         }
      }

      return near;
   }

   public NPC findNearestNPCByTypes(int[] types) {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null) {
            int[] arr$1 = types;
            int len$1 = types.length;

            for(int i$1 = 0; i$1 < len$1; ++i$1) {
               int type = arr$1[i$1];
               if(npc.npcDescription.npcType == (long)type) {
                  if(near == null) {
                     near = npc;
                  } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
                     near = npc;
                  }
               }
            }
         }
      }

      return near;
   }

   public NPC findNearestNPCByCombat(int level) {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null && npc.npcDescription.npcCombat == level) {
            if(near == null) {
               near = npc;
            } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
               near = npc;
            }
         }
      }

      return near;
   }

   public NPC findNearestNPCByCombats(int[] levels) {
      NPC near = null;
      NPC[] arr$ = this.getNPCs();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NPC npc = arr$[i$];
         if(npc != null && npc.npcDescription != null) {
            int[] arr$1 = levels;
            int len$1 = levels.length;

            for(int i$1 = 0; i$1 < len$1; ++i$1) {
               int level = arr$1[i$1];
               if(npc.npcDescription.npcCombat == level) {
                  if(near == null) {
                     near = npc;
                  } else if(this.distanceTo(this.getPosition(npc)) < this.distanceTo(this.getPosition(near))) {
                     near = npc;
                  }
               }
            }
         }
      }

      return near;
   }

   public int getNPCActionIndex(NPC npc, String name) {
      if(npc.npcDescription != null && npc.npcDescription.actions != null) {
         for(int i = 0; i < npc.npcDescription.actions.length; ++i) {
            if(npc.npcDescription.actions[i] != null && npc.npcDescription.actions[i].equalsIgnoreCase(name)) {
               return i + 1;
            }
         }
      }

      return -1;
   }

   public int getObjectAt(Tile pos) {
      return this.getObjectAt(pos.x, pos.y);
   }

   public int getObjectAt(int x, int y) {
      for(int i = 0; i < 4; ++i) {
         try {
            Ground e = rs.worldController.groundArray[i][x - rs.baseX][y - rs.baseY];
            if(e != null) {
               if(e.obj5Array != null) {
                  Object5[] arr$ = e.obj5Array;
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     Object5 obj = arr$[i$];
                     if(obj != null) {
                        return obj.uid;
                     }
                  }
               }

               if(e.obj4 != null) {
                  return e.obj4.uid;
               }

               if(e.obj3 != null) {
                  return e.obj3.uid;
               }

               if(e.obj2 != null) {
                  return e.obj2.uid;
               }

               if(e.obj1 != null) {
                  return e.obj1.uid;
               }
            }
         } catch (Exception var9) {
            log(var9.getMessage());
         }
      }

      return -1;
   }

   public int getObjectAt(Tile pos, int objectType) {
      return this.getObjectAt(pos.x, pos.y, objectType);
   }

   public int getObjectAt(int x, int y, int objectType) {
      for(int i = 0; i < 4; ++i) {
         try {
            Ground e = rs.worldController.groundArray[i][x - rs.baseX][y - rs.baseY];
            if(e != null) {
               switch(objectType) {
               case 1:
                  if(e.obj2 != null) {
                     return e.obj1.uid;
                  }
                  break;
               case 2:
                  if(e.obj2 != null) {
                     return e.obj2.uid;
                  }
                  break;
               case 3:
                  if(e.obj3 != null) {
                     return e.obj3.uid;
                  }
                  break;
               case 4:
                  if(e.obj4 != null) {
                     return e.obj4.uid;
                  }
                  break;
               case 5:
                  if(e.obj5Array != null) {
                     Object5[] arr$ = e.obj5Array;
                     int len$ = arr$.length;

                     for(int i$ = 0; i$ < len$; ++i$) {
                        Object5 obj = arr$[i$];
                        if(obj != null) {
                           return obj.uid;
                        }
                     }
                  }
               }
            }
         } catch (Exception var10) {
            log(var10.getMessage());
         }
      }

      return -1;
   }

   public Object getObjectInstanceAt(Tile pos) {
      return this.getObjectInstanceAt(pos.x, pos.y);
   }

   public Object getObjectInstanceAt(int x, int y) {
      for(int i = 0; i < 4; ++i) {
         try {
            Ground e = rs.worldController.groundArray[i][x - rs.baseX][y - rs.baseY];
            if(e != null) {
               if(e.obj5Array != null) {
                  Object5[] arr$ = e.obj5Array;
                  int len$ = arr$.length;

                  for(int i$ = 0; i$ < len$; ++i$) {
                     Object5 obj = arr$[i$];
                     if(obj != null) {
                        return obj;
                     }
                  }
               }

               if(e.obj4 != null) {
                  return e.obj4;
               }

               if(e.obj3 != null) {
                  return e.obj3;
               }

               if(e.obj2 != null) {
                  return e.obj2;
               }

               if(e.obj1 != null) {
                  return e.obj1;
               }
            }
         } catch (Exception var9) {
            log(var9.getMessage());
         }
      }

      return Integer.valueOf(-1);
   }

   public Object getObjectInstanceAt(Tile pos, int objectType) {
      return this.getObjectInstanceAt(pos.x, pos.y, objectType);
   }

   public Object getObjectInstanceAt(int x, int y, int objectType) {
      for(int i = 0; i < 4; ++i) {
         try {
            Ground e = rs.worldController.groundArray[i][x - rs.baseX][y - rs.baseY];
            if(e != null) {
               switch(objectType) {
               case 1:
                  if(e.obj2 != null) {
                     return e.obj1;
                  }
                  break;
               case 2:
                  if(e.obj2 != null) {
                     return e.obj2;
                  }
                  break;
               case 3:
                  if(e.obj3 != null) {
                     return e.obj3;
                  }
                  break;
               case 4:
                  if(e.obj4 != null) {
                     return e.obj4;
                  }
                  break;
               case 5:
                  if(e.obj5Array != null) {
                     Object5[] arr$ = e.obj5Array;
                     int len$ = arr$.length;

                     for(int i$ = 0; i$ < len$; ++i$) {
                        Object5 obj = arr$[i$];
                        if(obj != null) {
                           return obj;
                        }
                     }
                  }
               }
            }
         } catch (Exception var10) {
            log(var10.getMessage());
         }
      }

      return Integer.valueOf(-1);
   }

   public int findObject(int type) {
      return this.findObject(new int[]{type});
   }

   public int findObject(int[] types) {
      int rez = -1;

      for(int x = 0; x < 104; ++x) {
         for(int y = 0; y < 104; ++y) {
            Tile t = new Tile(x + rs.baseX, y + rs.baseY);
            if(rez == -1 || this.distanceTo(t) < this.distanceTo(objectPos(rez))) {
               int obj = this.getObjectAt(t);
               int[] arr$ = types;
               int len$ = types.length;

               for(int i$ = 0; i$ < len$; ++i$) {
                  int xx = arr$[i$];
                  if(objectType(obj) == xx) {
                     rez = obj;
                  }
               }
            }
         }
      }

      return rez;
   }

   public int getGroundItemAt(Tile pos) {
      int[] got = this.getGroundItemsAt(pos);
      return got != null && got.length > 0?got[0]:-1;
   }

   public int[] getGroundItemsAt(Tile pos) {
      return this.getGroundItemsAt(pos.x, pos.y);
   }

   public int[] getGroundItemsAt(int x, int y) {
      try {
         GroundEntity aiobe = rs.groundArray[rs.unknownInt9][x - rs.baseX][y - rs.baseY];
         if(aiobe == null) {
            return new int[0];
         } else {
            ArrayList list = new ArrayList();
            Item it = this.getList(aiobe);
            if(it != null) {
               list.add(Integer.valueOf(it.itemID));

               while(it != null) {
                  it = this.iterateList(aiobe);
                  if(it != null) {
                     list.add(Integer.valueOf(it.itemID));
                  }
               }
            }

            int[] rez = new int[list.size()];

            for(int i = 0; i < rez.length; ++i) {
               rez[i] = ((Integer)list.get(i)).intValue();
            }

            return rez;
         }
      } catch (ArrayIndexOutOfBoundsException var8) {
         log("getGroundItemAt() was out of bounds");
         return new int[0];
      }
   }

   private Item getList(GroundEntity ge) {
      Class30 item = ge.item1.item4;
      if(ge.item1.item4 == ge.item1) {
         ge.item3 = null;
         return null;
      } else {
         ge.item3 = ge.item1.item4.item4;
         return (Item)item;
      }
   }

   private Item iterateList(GroundEntity ge) {
      Class30 item = ge.item3;
      if(ge.item3 == ge.item1) {
         ge.item3 = null;
         return null;
      } else {
         ge.item3 = ge.item3.item4;
         return (Item)item;
      }
   }

   public Tile findGroundItem(int[] types) {
      Tile end = null;

      for(int x = 0; x < 104; ++x) {
         for(int y = 0; y < 104; ++y) {
            Tile pos = new Tile(x + rs.baseX, y + rs.baseY);
            int[] items = this.getGroundItemsAt(pos);
            int[] arr$ = types;
            int len$ = types.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               int type = arr$[i$];
               int[] arr$1 = items;
               int len$1 = items.length;

               for(int i$1 = 0; i$1 < len$1; ++i$1) {
                  int item = arr$1[i$1];
                  if(item == type && (end == null || this.distanceTo(pos) < this.distanceTo(end))) {
                     end = pos;
                  }
               }
            }
         }
      }

      return end;
   }

   public Tile findGroundItem(int type) {
      return this.findGroundItem(new int[]{type});
   }

   public boolean openBank() {
      if(this.getInterface() == 5292) {
         return true;
      } else {
         int booth = this.findObject(2213);
         if(booth == -1) {
            return false;
         } else {
            this.atObject(booth, 2);
            return false;
         }
      }
   }

   public int[] getInventory() {
      RSInterface rsi = RSInterface.interfaceCache[3214];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getInvStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[3214];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public int getInvStackSize(int i) {
      RSInterface rsi = RSInterface.interfaceCache[3214];
      return rsi.invStackSize != null && i >= 0 && i <= rsi.invStackSize.length?rsi.invStackSize[i]:-1;
   }

   public int[] getEquipment(Player player) {
      int[] equip = player.equipment;
      int[] rez = new int[equip.length];

      for(int i = 0; i < equip.length; ++i) {
         if(equip[i] >= 512) {
            rez[i] = equip[i] - 512;
         }
      }

      return rez;
   }

   public int[] getBank() {
      RSInterface rsi = RSInterface.interfaceCache[5382];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getBankStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[5382];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public int getBankStackSize(int i) {
      RSInterface rsi = RSInterface.interfaceCache[5382];
      return rsi != null && i >= 0 && i <= rsi.invStackSize.length?rsi.invStackSize[i]:-1;
   }

   public int[] getStore() {
      RSInterface rsi = RSInterface.interfaceCache[3900];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getStoreStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[3900];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public int getStoreStackSize(int i) {
      RSInterface rsi = RSInterface.interfaceCache[3900];
      return rsi != null && i >= 0 && i <= rsi.invStackSize.length?rsi.invStackSize[i]:-1;
   }

   public int inventoryCount() {
      int count = 0;
      int[] arr$ = this.getInventory();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int id = arr$[i$];
         if(id != -1) {
            ++count;
         }
      }

      return count;
   }

   public int bankCount() {
      int count = 0;
      int[] arr$ = this.getBank();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int id = arr$[i$];
         if(id != -1) {
            ++count;
         }
      }

      return count;
   }

   public int storeCount() {
      int count = 0;
      int[] arr$ = this.getStore();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int id = arr$[i$];
         if(id != -1) {
            ++count;
         }
      }

      return count;
   }

   public int inventoryCount(int itemType) {
      return this.inventoryCount(new int[]{itemType});
   }

   public int bankCount(int itemType) {
      return this.bankCount(new int[]{itemType});
   }

   public int storeCount(int itemType) {
      return this.storeCount(new int[]{itemType});
   }

   public int inventoryCount(int[] itemTypes) {
      int count = 0;
      int[] arr$ = this.getInventory();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int id = arr$[i$];
         int[] arr$1 = itemTypes;
         int len$1 = itemTypes.length;

         for(int i$1 = 0; i$1 < len$1; ++i$1) {
            int itemID = arr$1[i$1];
            if(id == itemID) {
               ++count;
            }
         }
      }

      return count;
   }

   public int bankCount(int[] itemTypes) {
      int count = 0;
      int[] arr$ = this.getBank();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int id = arr$[i$];
         int[] arr$1 = itemTypes;
         int len$1 = itemTypes.length;

         for(int i$1 = 0; i$1 < len$1; ++i$1) {
            int itemID = arr$1[i$1];
            if(id == itemID) {
               ++count;
            }
         }
      }

      return count;
   }

   public int storeCount(int[] itemTypes) {
      int count = 0;
      int[] arr$ = this.getStore();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int id = arr$[i$];
         int[] arr$1 = itemTypes;
         int len$1 = itemTypes.length;

         for(int i$1 = 0; i$1 < len$1; ++i$1) {
            int itemID = arr$1[i$1];
            if(id == itemID) {
               ++count;
            }
         }
      }

      return count;
   }

   public String getItemName(int id) {
      try {
         return ItemDef.forItemID(id).itemName;
      } catch (Exception var3) {
         return "";
      }
   }

   public int getInvCountSuffix(String suffix) {
      int count = 0;

      for(int i = 0; i < this.getInventory().length; ++i) {
         if(this.getInventory()[i] >= 0 && this.getItemName(this.getInventory()[i]).toLowerCase().endsWith(suffix.toLowerCase())) {
            ++count;
         }
      }

      return count;
   }

   public int getInvCountPrefix(String prefix) {
      int count = 0;

      for(int i = 0; i < this.getInventory().length; ++i) {
         if(this.getInventory()[i] >= 0 && this.getItemName(this.getInventory()[i]).toLowerCase().startsWith(prefix.toLowerCase())) {
            ++count;
         }
      }

      return count;
   }

   public int getBankCountSuffix(String suffix) {
      int count = 0;

      for(int i = 0; i < this.getBank().length; ++i) {
         if(this.getBank()[i] >= 0 && this.getItemName(this.getBank()[i]).toLowerCase().endsWith(suffix.toLowerCase())) {
            ++count;
         }
      }

      return count;
   }

   public int getBankCountPrefix(String prefix) {
      int count = 0;

      for(int i = 0; i < this.getBank().length; ++i) {
         if(this.getBank()[i] >= 0 && this.getItemName(this.getBank()[i]).toLowerCase().startsWith(prefix.toLowerCase())) {
            ++count;
         }
      }

      return count;
   }

   public int getStoreCountSuffix(String suffix) {
      int count = 0;

      for(int i = 0; i < this.getStore().length; ++i) {
         if(this.getStore()[i] >= 0 && this.getItemName(this.getStore()[i]).toLowerCase().endsWith(suffix.toLowerCase())) {
            ++count;
         }
      }

      return count;
   }

   public int getStoreCountPrefix(String prefix) {
      int count = 0;

      for(int i = 0; i < this.getStore().length; ++i) {
         if(this.getStore()[i] >= 0 && this.getItemName(this.getStore()[i]).toLowerCase().startsWith(prefix.toLowerCase())) {
            ++count;
         }
      }

      return count;
   }

   public int locateInvSuffix(String suffix) {
      for(int i = 0; i < this.getInventory().length; ++i) {
         if(this.getInventory()[i] >= 0 && this.getItemName(this.getInventory()[i]).toLowerCase().endsWith(suffix.toLowerCase())) {
            return i;
         }
      }

      return -1;
   }

   public int locateInvPrefix(String prefix) {
      for(int i = 0; i < this.getInventory().length; ++i) {
         if(this.getInventory()[i] >= 0 && this.getItemName(this.getInventory()[i]).toLowerCase().startsWith(prefix.toLowerCase())) {
            return i;
         }
      }

      return -1;
   }

   public int locateBankSuffix(String suffix) {
      for(int i = 0; i < this.getBank().length; ++i) {
         if(this.getBank()[i] >= 0 && this.getItemName(this.getBank()[i]).toLowerCase().endsWith(suffix.toLowerCase())) {
            return i;
         }
      }

      return -1;
   }

   public int locateBankPrefix(String prefix) {
      for(int i = 0; i < this.getBank().length; ++i) {
         if(this.getBank()[i] >= 0 && this.getItemName(this.getBank()[i]).toLowerCase().startsWith(prefix.toLowerCase())) {
            return i;
         }
      }

      return -1;
   }

   public int locateStoreSuffix(String suffix) {
      for(int i = 0; i < this.getStore().length; ++i) {
         if(this.getStore()[i] >= 0 && this.getItemName(this.getStore()[i]).toLowerCase().endsWith(suffix.toLowerCase())) {
            return i;
         }
      }

      return -1;
   }

   public int locateStorePrefix(String prefix) {
      for(int i = 0; i < this.getStore().length; ++i) {
         if(this.getStore()[i] >= 0 && this.getItemName(this.getStore()[i]).toLowerCase().startsWith(prefix.toLowerCase())) {
            return i;
         }
      }

      return -1;
   }

   public int getInventoryIndex(int itemType) {
      return this.getInventoryIndex(new int[]{itemType});
   }

   public int getInventoryIndex(int[] types) {
      for(int i = 0; i < this.getInventory().length; ++i) {
         int[] arr$ = types;
         int len$ = types.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            int id = arr$[i$];
            if(this.getInventory()[i] == id) {
               return i;
            }
         }
      }

      return -1;
   }

   public int getBankIndex(int itemType) {
      return this.getBankIndex(new int[]{itemType});
   }

   public int getBankIndex(int[] types) {
      for(int i = 0; i < this.getBank().length; ++i) {
         int[] arr$ = types;
         int len$ = types.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            int id = arr$[i$];
            if(this.getBank()[i] == id) {
               return i;
            }
         }
      }

      return -1;
   }

   public int getStoreIndex(int itemType) {
      return this.getStoreIndex(new int[]{itemType});
   }

   public int getStoreIndex(int[] types) {
      for(int i = 0; i < this.getStore().length; ++i) {
         int[] arr$ = types;
         int len$ = types.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            int id = arr$[i$];
            if(this.getStore()[i] == id) {
               return i;
            }
         }
      }

      return -1;
   }

   public int[] getPlayerTradeOffer() {
      RSInterface rsi = RSInterface.interfaceCache[3415];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getOtherTradeOffer() {
      RSInterface rsi = RSInterface.interfaceCache[3416];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getPlayerTradeAccepted() {
      RSInterface rsi = RSInterface.interfaceCache[3542];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getOtherTradeAccepted() {
      RSInterface rsi = RSInterface.interfaceCache[3532];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] inv = rsi.invArray;
         if(inv == null) {
            return new int[0];
         } else {
            int[] fixed = new int[inv.length];

            for(int i = 0; i < fixed.length; ++i) {
               fixed[i] = inv[i] - 1;
            }

            return fixed;
         }
      }
   }

   public int[] getPlayerTradeOfferStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[3415];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public int[] getOtherTradeOfferStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[3416];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public int[] getPlayerTradeAcceptedStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[3542];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public int[] getOtherTradeAcceptedStackSizes() {
      RSInterface rsi = RSInterface.interfaceCache[3532];
      if(rsi == null) {
         return new int[0];
      } else {
         int[] sizes = rsi.invStackSize;
         return sizes == null?new int[0]:sizes;
      }
   }

   public boolean isTradeAcceptedPlayer() {
      RSInterface rsi = RSInterface.interfaceCache[3431];
      return rsi == null?false:rsi.message != null && rsi.message.contains("Waiting for other player");
   }

   public boolean isTradeAcceptedOther() {
      RSInterface rsi = RSInterface.interfaceCache[3431];
      return rsi == null?false:rsi.message != null && rsi.message.contains("Other player has accepted");
   }

   public boolean isTradeConfirmedPlayer() {
      RSInterface rsi = RSInterface.interfaceCache[3431];
      return rsi == null?false:rsi.message != null && rsi.message.contains("Waiting for other player");
   }

   public boolean isTradeConfirmedOther() {
      RSInterface rsi = RSInterface.interfaceCache[3431];
      return rsi == null?false:rsi.message != null && rsi.message.contains("Other player");
   }

   public Tile[] reversePath(Tile[] other) {
      Tile[] t = new Tile[other.length];

      for(int i = 0; i < t.length; ++i) {
         t[i] = other[other.length - i - 1];
      }

      return t;
   }

   public Tile nextStep(Tile[] path, int distance) {
      Tile me = this.getPosition(this.myPlayer());

      for(int i = path.length - 1; i > -1; --i) {
         if(me.distanceTo(path[i]) <= distance && me.distanceTo(path[i]) != 0) {
            return path[i];
         }
      }

      return null;
   }

   public boolean walkPath(Tile[] path) {
      Tile next = this.nextStep(path, 11);
      return next != null?this.walk(next):false;
   }

   public boolean walkPath(Tile[] path, int distance) {
      Tile next = this.nextStep(path, distance);
      return next != null?this.walk(next):false;
   }

   public void setNextAction(int id, int data1, int data2, int data3) {
      rs.actionJack = new int[]{id, data1, data2, data3};
   }

   public void login(String username, String password, boolean lagged) {
      rs.unknownInt1 = 0;
      rs.login(username, password, lagged);
   }

   public void login() {
      rs.unknownInt1 = 0;
      rs.login();
   }

   public void forceLogout(long length) {
      rs.idleTime = 4501;
      rs.forceLogout = length;
      rs.forceLogoutStart = System.currentTimeMillis();
   }

   public void shutdown() {
      log("Recieved shutdown()!");
      this.forceLogout(10000L);
      System.exit(-1);
   }

   public boolean walk(Tile newPos) {
      return newPos == null?false:this.walk(newPos.x, newPos.y);
   }

   public boolean walk(int x, int y) {
      Tile minimap = this.worldToMinimap(x, y);
      if(minimap == null) {
         return false;
      } else {
         this.mouse(minimap);
         return true;
      }
   }

   public boolean canReach(Tile pos) {
      return this.canReach(pos.x, pos.y);
   }

   public boolean canReach(int x, int y) {
      try {
         rs.cancelWalk = true;
         boolean e = rs.doWalkTo(2, 0, 1, -11308, 0, this.myPlayer().entBigY[0], 1, 0, y - rs.baseY, this.myPlayer().entBigX[0], false, x - rs.baseX);
         rs.cancelWalk = false;
         return e;
      } catch (Exception var4) {
         log("Error in canReach(): " + var4.getMessage());
         rs.cancelWalk = false;
         return false;
      }
   }

   public void dropItem(int itemType, int itemIndex) {
      this.setNextAction(847, itemType, itemIndex, 3214);
      this.setMousePosInv(itemIndex);
   }

   public void takeItem(int itemType, int x, int y) {
      this.setNextAction(234, itemType, x - rs.baseX, y - rs.baseY);
      this.mouse(this.tileToScreen(x, y, 0));
   }

   public void setRun(boolean value) {
      if(this.getRun() != value) {
         this.atInterface(value?153:152, 6);
      }

   }

   public boolean getRun() {
      RSInterface rsi = RSInterface.forID(153);
      return rsi.unknownIntArrayArray1 == null?false:rs.variousSettings[rsi.unknownIntArrayArray1[0][1]] == 1;
   }

   public boolean atNPC(NPC npc, int actionIndex) {
      if(actionIndex >= 1 && actionIndex <= 5) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 20;
            break;
         case 2:
            id = 412;
            break;
         case 3:
            id = 225;
            break;
         case 4:
            id = 965;
            break;
         case 5:
            id = 478;
         }

         if(id != -1) {
            if(npc.index == -1) {
               return false;
            } else {
               this.setNextAction(id, npc.index, 0, 0);
               this.setMousePos(npc);
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean atPlayer(Player p, int actionIndex) {
      if(actionIndex >= 1 && actionIndex <= 5) {
         short id = -1;
         short c = 0;
         switch(actionIndex) {
         case 1:
            id = 561;
            break;
         case 2:
            if(p.combatLevel > client.myPlayer.combatLevel) {
               c = 2000;
            }

            if(client.myPlayer.team != 0 && p.team != 0) {
               if(client.myPlayer.team == p.team) {
                  c = 2000;
               } else {
                  c = 0;
               }
            }

            id = 779;
            break;
         case 3:
            id = 27;
            break;
         case 4:
            id = 577;
            break;
         case 5:
            id = 729;
         }

         if(id != -1) {
            if(p.index == -1) {
               return false;
            } else {
               if(rs.unknownBooleanArray[actionIndex - 1]) {
                  c = 2000;
               }

               this.setNextAction(id + c, p.index, 0, 0);
               this.setMousePos(p);
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean atObject(int uid, int actionIndex) {
      if(actionIndex >= 1 && actionIndex <= 5) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 502;
            break;
         case 2:
            id = 900;
            break;
         case 3:
            id = 113;
            break;
         case 4:
            id = 872;
            break;
         case 5:
            id = 1062;
         }

         if(id != -1) {
            Tile pos = objectPos(uid);
            Tile screen = this.tileToScreen(pos.clone(), 0);
            pos.x -= rs.baseX;
            pos.y -= rs.baseY;
            this.setNextAction(id, uid, pos.x, pos.y);
            this.mouse(screen);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean atInterface(int interfaceID, int actionIndex) {
      if(actionIndex >= 1 && actionIndex <= 7) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 315;
            break;
         case 2:
            id = 626;
            break;
         case 3:
            id = 561;
            break;
         case 4:
            id = 779;
            break;
         case 5:
            id = 169;
            break;
         case 6:
            id = 646;
            break;
         case 7:
            id = 679;
         }

         if(id != -1) {
            this.setNextAction(id, 0, 0, interfaceID);
            this.mouse((Tile)null);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean atInterfaceItem(int interfaceID, int actionIndex, int itemID, int row) {
      if(actionIndex >= 1 && actionIndex <= 4) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 632;
            break;
         case 2:
            id = 78;
            break;
         case 3:
            id = 867;
            break;
         case 4:
            id = 431;
         }

         if(id != -1) {
            this.setNextAction(id, itemID, row, interfaceID);
            this.mouse((Tile)null);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void continueInterface(RSInterface rsi) {
      this.setNextAction(679, 0, 0, rsi.interfaceID);
      this.mouse((Tile)null);
   }

   public boolean continueDialogInterface() {
      return this.continueInterface(rs.backDialogID);
   }

   public boolean continueBackInterface() {
      return this.continueInterface(rs.dialogID);
   }

   public boolean continueInterface(int interfaceId) {
      RSInterface rsi = RSInterface.forID(interfaceId);
      if(rsi == null) {
         return false;
      } else if(!this.getDialogText().toLowerCase().contains("continue")) {
         return false;
      } else {
         this.setNextAction(679, 0, 0, rsi.interfaceID);
         this.mouse((Tile)null);
         return true;
      }
   }

   public boolean atNPC(NPC npc, String actionName) {
      if(actionName == null) {
         return false;
      } else {
         short id = -1;
         int actionIndex = this.getNPCActionIndex(npc, actionName);
         if(actionIndex <= 0) {
            return false;
         } else {
            switch(actionIndex) {
            case 1:
               id = 20;
               break;
            case 2:
               id = 412;
               break;
            case 3:
               id = 225;
               break;
            case 4:
               id = 965;
               break;
            case 5:
               id = 478;
            }

            if(id != -1) {
               if(npc.index == -1) {
                  return false;
               } else {
                  this.setNextAction(id, npc.index, 0, 0);
                  this.setMousePos(npc);
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   public void depositItem(int type, int index) {
      this.atInventoryItem(type, index, 1, 5064);
   }

   public void depositAll(int type, int index) {
      this.atInventoryItem(type, index, 4, 5064);
   }

   public void depositX(int type, int index, int amount) {
      if(rs.inputDialogState == 1) {
         this.sendText(Integer.toString(amount));
      } else {
         this.atInventoryItem(type, index, 5, 5064);
      }

   }

   public void withdrawItem(int type, int index) {
      this.setNextAction(632, type, index, 5382);
      this.mouse((Tile)null);
   }

   public void withdrawAll(int type, int index) {
      this.setNextAction(431, type, index, 5382);
      this.mouse((Tile)null);
   }

   public void withdrawX(int type, int index, int amount) {
      if(rs.inputDialogState == 1) {
         this.sendText(Integer.toString(amount));
      } else {
         this.setNextAction(53, type, index, 5382);
         this.mouse((Tile)null);
      }

   }

   public void sellItem(int type, int index) {
      this.atInventoryItem(type, index, 2);
   }

   public void sellTen(int type, int index) {
      this.atInventoryItem(type, index, 4);
   }

   public void buyItem(int type, int index) {
      this.setNextAction(78, type, index, 3900);
      this.mouse((Tile)null);
   }

   public void buyTen(int type, int index) {
      this.setNextAction(431, type, index, 3900);
      this.mouse((Tile)null);
   }

   public void offerItem(int itemID, int invIndex) {
      this.atInventoryItem(itemID, invIndex, 1, 3322);
   }

   public void offerAll(int itemID, int invIndex) {
      this.atInventoryItem(itemID, invIndex, 4, 3322);
   }

   public void offerX(int itemID, int invIndex, int amount) {
      if(rs.inputDialogState == 1) {
         this.sendText(Integer.toString(amount));
      } else {
         this.atInventoryItem(itemID, invIndex, 5, 3322);
      }

   }

   public void removeOfferItem(int itemID, int invIndex) {
      this.setNextAction(632, itemID, invIndex, 3415);
      this.mouse((Tile)null);
   }

   public void removeOfferAll(int itemID, int invIndex) {
      this.setNextAction(431, itemID, invIndex, 3415);
      this.mouse((Tile)null);
   }

   public void removeOfferX(int itemID, int invIndex, int amount) {
      if(rs.inputDialogState == 1) {
         this.sendText(Integer.toString(amount));
      } else {
         this.setNextAction(53, itemID, invIndex, 3415);
         this.mouse((Tile)null);
      }

   }

   public boolean atInventoryItem(int itemID, int invIndex, int actionIndex) {
      return this.atInventoryItem(itemID, invIndex, actionIndex, 3823);
   }

   public boolean atInventoryItem(int itemID, int invIndex, int actionIndex, int actionID) {
      if(actionIndex >= 1 && actionIndex <= 5) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 632;
            break;
         case 2:
            id = 78;
            break;
         case 3:
            id = 867;
            break;
         case 4:
            id = 431;
            break;
         case 5:
            id = 53;
         }

         if(id != -1) {
            this.setNextAction(id, itemID, invIndex, actionID);
            this.setMousePosInv(invIndex);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean atSpecialInventoryItem(int itemID, int invIndex, int actionIndex) {
      if(actionIndex >= 1 && actionIndex <= 5) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 74;
            break;
         case 2:
            id = 454;
            break;
         case 3:
            id = 539;
            break;
         case 4:
            id = 493;
            break;
         case 5:
            id = 847;
         }

         if(id != -1) {
            this.setNextAction(id, itemID, invIndex, 3214);
            this.setMousePosInv(invIndex);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void atGroundObject(Item item, int actionIndex) {
      if(actionIndex >= 1 && actionIndex <= 5) {
         short id = -1;
         switch(actionIndex) {
         case 1:
            id = 632;
            break;
         case 2:
            id = 78;
            break;
         case 3:
            id = 867;
            break;
         case 4:
            id = 431;
            break;
         case 5:
            id = 53;
         }

         if(id != -1) {
            this.setNextAction(id, item.itemID, item.x - rs.baseX, item.y - rs.baseY);
            this.mouse(this.tileToScreen(item.x, item.y, 0));
         }

      }
   }

   public void selectItem(int type, int index) {
      this.setNextAction(447, type, index, 3214);
      this.setMousePosInv(index);
   }

   public void cancelAction() {
      for(int i = 0; i < 28; ++i) {
         if(this.getInventory()[i] == -1) {
            this.setMousePosInv(i);
            return;
         }
      }

      this.setNextAction(1107, 0, 0, 0);
      this.setMousePosInv(random(1, 26));
   }

   public void useWithItem(int otherType, int otherIndex) {
      this.setNextAction(870, otherType, otherIndex, 3214);
      this.setMousePosInv(otherIndex);
   }

   public void useWithGroundItem(int type, int x, int y) {
      this.setNextAction(511, type, x, y);
      this.setMousePosInv(type);
   }

   public void useWithObject(int uid) {
      Tile pos = objectPos(uid);
      this.setNextAction(62, uid, pos.x - rs.baseX, pos.y - rs.baseY);
      this.mouse((Tile)null);
   }

   public boolean useWithNPC(NPC npc) {
      if(npc == null) {
         return false;
      } else {
         this.setNextAction(582, npc.index, 0, 0);
         this.setMousePos(npc);
         return true;
      }
   }

   public void castSpellOnNPC(NPC npc) {
      if(npc != null) {
         this.setNextAction(413, npc.index, 0, 0);
         this.setMousePos(npc);
      }

   }

   public boolean loggedIn() {
      return rs.loggedIn;
   }

   public boolean ready() {
      return this.loggedIn() && client.myPlayer != null;
   }

   public int getInterface() {
      return rs.interfaceID;
   }

   public void closeInterface() {
      this.setNextAction(27, 0, 0, 0);
      this.mouse((Tile)null);
   }

   public String[] getChatMessages() {
      return rs.chatMessages;
   }

   public String[] getChatNames() {
      return rs.chatNames;
   }

   public int[] getChatTypes() {
      return rs.chatTypes;
   }

   public String getBackText() {
      String rez = "";
      if(rs.dialogID == -1) {
         return rez;
      } else {
         RSInterface rsi = RSInterface.forID(rs.dialogID);
         if(rsi != null && rsi.children != null) {
            int[] arr$ = rsi.children;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               int childID = arr$[i$];
               RSInterface child = RSInterface.forID(childID);
               if(child.message != null) {
                  rez = rez + child.message + "\n";
               }
            }

            return rez;
         } else {
            return rez;
         }
      }
   }

   public String getDialogText() {
      String rez = "";
      if(rs.backDialogID == -1) {
         return rez;
      } else {
         RSInterface rsi = RSInterface.forID(rs.backDialogID);
         if(rsi != null && rsi.children != null) {
            int[] arr$ = rsi.children;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               int childID = arr$[i$];
               RSInterface child = RSInterface.forID(childID);
               if(child.message != null) {
                  rez = rez + child.message + "\n";
               }
            }

            return rez;
         } else {
            return rez;
         }
      }
   }

   public boolean isNoteSelected() {
      if(this.getInterface() != 5292) {
         return false;
      } else {
         RSInterface rsi = RSInterface.forID(5386);
         return rsi.unknownIntArrayArray1 == null?false:rs.variousSettings[rsi.unknownIntArrayArray1[0][1]] == 1;
      }
   }

   public void selectNoteButton() {
      if(this.getInterface() == 5292) {
         this.atInterface(5386, 1);
      }

   }

   public void acceptTrade() {
      this.atInterface(3420, 1);
   }

   public void confirmTrade() {
      this.atInterface(3546, 1);
   }

   public boolean isItemSelected() {
      return rs.itemSelected == 1;
   }

   public String getSelectedItemName() {
      return rs.selectedItemName;
   }

   public int getSkillIndex(String name) {
      for(int i = 0; i < Skills.skillNames.length; ++i) {
         if(Skills.skillNames[i].equalsIgnoreCase(name)) {
            return i;
         }
      }

      return -1;
   }

   public int getCurrentStat(int index) {
      return index >= 0 && index <= rs.currentStats.length - 1?rs.currentStats[index]:-1;
   }

   public int getCurrentStat(String name) {
      return this.getCurrentStat(this.getSkillIndex(name));
   }

   public int getMaxStat(int index) {
      return index >= 0 && index <= rs.maxStats.length - 1?rs.maxStats[index]:-1;
   }

   public int getMaxStat(String name) {
      return this.getMaxStat(this.getSkillIndex(name));
   }

   public int getExp(int index) {
      return index >= 0 && index <= rs.currentExp.length - 1?rs.currentExp[index]:-1;
   }

   public int getExp(String name) {
      return this.getExp(this.getSkillIndex(name));
   }

   public int getEnergy() {
      return rs.energy;
   }

   public int getWeight() {
      return rs.weight;
   }

   public int getTab() {
      return rs.tabID;
   }

   public boolean selectTab(int index) {
      boolean mx = true;
      boolean my = true;
      if(index >= 0 && index <= 13 && index != 7) {
         int lx = 541 + (index < 7?index:index - 7) * 32;
         int mx1 = random(lx, lx + 20);
         int my1;
         if(index < 7) {
            my1 = random(169, 202);
         } else {
            my1 = random(467, 501);
         }

         this.setNextAction(1107, 0, 0, 0);
         this.mouse(mx1, my1);
         return true;
      } else {
         return false;
      }
   }

   public void setUser(String username, String password) {
      rs.setUser(username, password);
   }

   public String getUser() {
      return rs.getUser();
   }

   public void setServer(String server) {
      rs.setServer(server);
   }

   public String getServer() {
      return rs.getServer();
   }*/
}
