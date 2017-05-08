package entityhandling.defs;

/**
 * The definition wrapper for items
 */
public class ItemDef extends EntityDef {

    public String command;
    public int basePrice;
    public int sprite;
    public boolean stackable;
    public boolean wieldable;
    public String entity;
    public int pictureMask;

    public String getCommand() {
        return command;
    }

    public int getSprite() {
        return sprite;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isWieldable() {
        return wieldable;
    }

    public String getEntity() {
        return entity;
    }

    public int getPictureMask() {
        return pictureMask;
    }

}
