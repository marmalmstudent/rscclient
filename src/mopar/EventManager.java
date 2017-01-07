package mopar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EventManager {
	/*
   private static HashMap eventQueue = new HashMap();
   public static HashMap eventListeners = new HashMap();
   public static String[] interfaceTypes = new String[]{"newInterface", "itemSpawn", "entityMessage", "serverMessage"};

   public static void processListeners() {
      String[] arr$ = interfaceTypes;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String inter = arr$[i$];
         if(eventListeners.get(inter) != null) {
            Iterator it = ((ArrayList)eventListeners.get(inter)).iterator();

            while(it.hasNext() && eventQueue.get(inter) != null) {
               Iterator eIt = ((ArrayList)eventQueue.get(inter)).iterator();
               Object listener = it.next();

               while(eIt.hasNext()) {
                  Object event = eIt.next();
                  if(Bot.scriptManager.running()) {
                     if(event instanceof RSInterface) {
                        ((InterfaceListener)listener).newInterface((RSInterface)event);
                     } else if(event instanceof ItemEvent) {
                        ((ItemSpawnListener)listener).itemSpawned((ItemEvent)event);
                     } else if(event instanceof EntityEvent) {
                        ((EntityMessageListener)listener).entityMessage((EntityEvent)event);
                     } else if(event instanceof String) {
                        ((ServerMessageListener)listener).serverMessage((String)event);
                     }
                  }

                  if(!it.hasNext()) {
                     eIt.remove();
                  }
               }
            }
         }
      }

   }

   public static void addListeners(Object o) {
      Class c = o.getClass();
      if(c.isAssignableFrom(EntityMessageListener.class)) {
         addEntityMessageListener((EntityMessageListener)o);
      }

      if(c.isAssignableFrom(ServerMessageListener.class)) {
         addServerMessageListener((ServerMessageListener)o);
      }

      if(c.isAssignableFrom(InterfaceListener.class)) {
         addInterfaceListener((InterfaceListener)o);
      }

      if(c.isAssignableFrom(ItemSpawnListener.class)) {
         addItemSpawnListener((ItemSpawnListener)o);
      }

   }

   public static void addEntityMessageListener(EntityMessageListener eml) {
      if(eventListeners.get("entityMessage") == null) {
         eventListeners.put("entityMessage", new ArrayList());
      }

      ((ArrayList)eventListeners.get("entityMessage")).add(eml);
   }

   public static void addServerMessageListener(ServerMessageListener sml) {
      if(eventListeners.get("serverMessage") == null) {
         eventListeners.put("serverMessage", new ArrayList());
      }

      ((ArrayList)eventListeners.get("serverMessage")).add(sml);
   }

   public static void addItemSpawnListener(ItemSpawnListener isl) {
      if(eventListeners.get("itemSpawn") == null) {
         eventListeners.put("itemSpawn", new ArrayList());
      }

      ((ArrayList)eventListeners.get("itemSpawn")).add(isl);
   }

   public static void addInterfaceListener(InterfaceListener il) {
      if(eventListeners.get("newInterface") == null) {
         eventListeners.put("newInterface", new ArrayList());
      }

      ((ArrayList)eventListeners.get("newInterface")).add(il);
   }

   public static void removeListeners(Object o) {
      Class c = o.getClass();
      if(c.isAssignableFrom(EntityMessageListener.class)) {
         removeEntityMessageListener((EntityMessageListener)o);
      }

      if(c.isAssignableFrom(ServerMessageListener.class)) {
         removeServerMessageListener((ServerMessageListener)o);
      }

      if(c.isAssignableFrom(InterfaceListener.class)) {
         removeInterfaceListener((InterfaceListener)o);
      }

      if(c.isAssignableFrom(ItemSpawnListener.class)) {
         removeItemSpawnListener((ItemSpawnListener)o);
      }

   }

   public static void removeEntityMessageListener(EntityMessageListener eml) {
      if(eventListeners.get("entityMessage") == null) {
         eventListeners.put("entityMessage", new ArrayList());
      }

      ((ArrayList)eventListeners.get("entityMessage")).remove(eml);
   }

   public static void removeServerMessageListener(ServerMessageListener sml) {
      if(eventListeners.get("serverMessage") == null) {
         eventListeners.put("serverMessage", new ArrayList());
      }

      ((ArrayList)eventListeners.get("serverMessage")).remove(sml);
   }

   public static void removeItemSpawnListener(ItemSpawnListener isl) {
      if(eventListeners.get("itemSpawn") == null) {
         eventListeners.put("itemSpawn", new ArrayList());
      }

      ((ArrayList)eventListeners.get("itemSpawn")).remove(isl);
   }

   public static void removeInterfaceListener(InterfaceListener il) {
      if(eventListeners.get("newInterface") == null) {
         eventListeners.put("newInterface", new ArrayList());
      }

      ((ArrayList)eventListeners.get("newInterface")).remove(il);
   }

   public static void notifyInterface(RSInterface inter) {
      if(eventListeners.get("newInterface") != null) {
         if(eventQueue.get("newInterface") == null) {
            eventQueue.put("newInterface", new ArrayList());
         }

         ArrayList li = (ArrayList)eventQueue.get("newInterface");
         li.add(inter);
      }
   }

   public static void notifyItemSpawn(Item item, int x, int y) {
      if(eventListeners.get("itemSpawn") != null) {
         if(eventQueue.get("itemSpawn") == null) {
            eventQueue.put("itemSpawn", new ArrayList());
         }

         ArrayList li = (ArrayList)eventQueue.get("itemSpawn");
         li.add(new ItemEvent(x, y, item));
      }
   }

   public static void entityMessage(Entity ent) {
      if(eventListeners.get("entityMessage") != null) {
         if(eventQueue.get("entityMessage") == null) {
            eventQueue.put("entityMessage", new ArrayList());
         }

         ArrayList li = (ArrayList)eventQueue.get("entityMessage");
         li.add(new EntityEvent(ent.textSpoken, ent));
      }
   }

   public static void serverMessage(String message) {
      if(eventListeners.get("serverMessage") != null) {
         if(eventQueue.get("serverMessage") == null) {
            eventQueue.put("serverMessage", new ArrayList());
         }

         ArrayList li = (ArrayList)eventQueue.get("serverMessage");
         li.add(message);
      }
   }*/
}
