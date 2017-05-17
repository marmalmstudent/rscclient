package entityhandling.defs;

/**
 * The definition wrapper for doors
 */
public class DoorDef extends EntityDef {

    /**
     * The first command of the door
     */
    public String command1;
    /**
     * The second command of the door
     */
    public String command2;
    /**
     * The doors type.
     */
    public int doorType;
    /**
     * Unknown
     */
    public int unknown;

    public int modelVar1;
    public int modelVar2;
    public int modelVar3;

    public String getCommand1() {
        return command1.toLowerCase();
    }

    public String getCommand2() {
        return command2.toLowerCase();
    }

    public int getDoorType() {
        return doorType;
    }

    public int getUnknown() {
        return unknown;
    }

    public int getHeight() {
        return modelVar1;
    }

    public int getTexture1() {
        return modelVar2;
    }

    public int getTexture2() {
        return modelVar3;
    }
}
