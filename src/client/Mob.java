package client;


public final class Mob {

	protected Mob() {
		waypointsX = new double[10];
		waypointsY = new double[10];
		animationCount = new int[12];
		level = -1;
		unusedBool = false;
		unusedInt = -1;
	}

	public int admin;
	public long nameLong;
	public String name;
	public int serverIndex;
	public int mobIntUnknown;
	public double currentX;
	public double currentY;
	public int type;
	public int stepCount;
	public int currentSprite;
	public int nextSprite;
	public int waypointEndSprite;
	public int waypointCurrent;
	public double waypointsX[];
	public double waypointsY[];
	public int animationCount[];
	public String lastMessage;
	public int lastMessageTimeout;
	public int anInt162;
	public int skullTimer;
	public int dmgRcv;
	public int hitPointsCurrent;
	public int hitPointsBase;
	public int combatTimer;
	public int level;
	public int colourHairType;
	public int colourTopType;
	public int colourBottomType;
	public int colourSkinType;
	public int attackingCameraInt;
	public int attackingMobIndex;
	public int attackingNpcIndex;
	public int anInt176;
	public boolean unusedBool;
	public int unusedInt;
	public int anInt179;
}
