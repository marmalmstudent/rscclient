package client;

import client.util.Config;
import client.util.DataConversions;
import entityhandling.EntityHandler;
import model.Sector;
import model.Tile;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EngineHandle
{
    public static final double SCALE_FACTOR = 1.0/128.0;
    public static final int SECTOR_HEIGHT = 48;
    public static final int SECTOR_WIDTH = 48;
    public static final int VISIBLE_SECTORS = 3;

    public Point[] selected;

    public EngineHandle(Camera camera, GameImage gameImage)
    {
        this.camera = camera;
        this.gameImage = gameImage;
        objectDirs = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        selected = new Point[SECTOR_WIDTH*SECTOR_HEIGHT*2*VISIBLE_SECTORS*VISIBLE_SECTORS];
        for (int i = 0; i < selected.length; ++i)
        	selected[i] = new Point(0, 0);
        walls = new Model[4][WORLD_OBJ_AREA];
        roofs = new Model[4][WORLD_OBJ_AREA];
        elevHandle = new ElevationHandle();
        requiresClean = true;
        groundTiles = new Model[WORLD_OBJ_AREA];
        groundTextureArray = new int[256];
        walkableValue = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        sectors = new Sector[VISIBLE_SECTORS*VISIBLE_SECTORS];

        if (world == null) {
            world = new Model((73*WORLD_OBJ_AREA)*VISIBLE_SECTORS*VISIBLE_SECTORS,
            		(73*WORLD_OBJ_AREA)*VISIBLE_SECTORS*VISIBLE_SECTORS, true, true, false, false, true);
        }
        try
        {
            tileArchive = new ZipFile(new File(Config.DATABASE_DIR + "/Landscape.zip"));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (int i = 0; i < 64; i++)
        {
            groundTextureArray[i] = Camera.getGroundColorVal(255 - i * 4, 255 - (int) ((double) i * 1.75D), 255 - i * 4);
            groundTextureArray[i + 64] = Camera.getGroundColorVal(i * 3, 144, 0);
            groundTextureArray[i + 128] = Camera.getGroundColorVal(192 - (int) ((double) i * 1.5D), 144 - (int) ((double) i * 1.5D), 0);
            groundTextureArray[i + 192] = Camera.getGroundColorVal(96 - (int) ((double) i * 1.5D), 48 + (int) ((double) i * 1.5D), 0);
        }
    }

    public int getStepCount(int walkSectionX, int walkSectionY, double x1, double y1,
    		double x2, double y2, int[] walkSectionXArray, int[] walkSectionYArray,
    		boolean checkForObjects)
    {
        int[][] tmpTiles = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        for (int k1 = 0; k1 < VISIBLE_SECTORS*SECTOR_WIDTH; k1++)
            for (int l1 = 0; l1 < VISIBLE_SECTORS*SECTOR_HEIGHT; l1++)
                tmpTiles[k1][l1] = 0;

        int requiredSteps = 0;
        int stepCount = 0;
        int x = walkSectionX;
        int y = walkSectionY;
        tmpTiles[walkSectionX][walkSectionY] = 99;
        walkSectionXArray[requiredSteps] = walkSectionX;
        walkSectionYArray[requiredSteps++] = walkSectionY;
        int i3 = walkSectionXArray.length;
        boolean pathAvailable = false;
        while (stepCount != requiredSteps)
        {
            x = walkSectionXArray[stepCount];
            y = walkSectionYArray[stepCount];
            stepCount = (stepCount + 1) % i3;
            if (x >= x1 && x <= x2 && y >= y1 && y <= y2)
            {
                pathAvailable = true;
                break;
            }
            if (checkForObjects)
            {
                if (x > 0 && x - 1 >= x1 && x - 1 <= x2
                		&& y >= y1 && y <= y2
                		&& (walkableValue[x - 1][y] & WALKABLE_3) == 0)
                {
                    pathAvailable = true;
                    break;
                }
                if (x < VISIBLE_SECTORS*SECTOR_WIDTH && x + 1 >= x1 && x + 1 <= x2
                		&& y >= y1 && y <= y2
                		&& (walkableValue[x + 1][y] & WALKABLE_1) == 0)
                {
                    pathAvailable = true;
                    break;
                }
                if (y > 0 && x >= x1 && x <= x2
                		&& y - 1 >= y1 && y - 1 <= y2
                		&& (walkableValue[x][y - 1] & WALKABLE_2) == 0)
                {
                    pathAvailable = true;
                    break;
                }
                if (y < VISIBLE_SECTORS*SECTOR_WIDTH && x >= x1 && x <= x2
                		&& y + 1 >= y1 && y + 1 <= y2
                		&& (walkableValue[x][y + 1] & WALKABLE_0) == 0)
                {
                    pathAvailable = true;
                    break;
                }
            }
            if (x > 0 && tmpTiles[x - 1][y] == 0
            		&& (walkableValue[x - 1][y]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_3)) == 0)
            {
                walkSectionXArray[requiredSteps] = x - 1;
                walkSectionYArray[requiredSteps] = y;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x - 1][y] = 2;
            }
            if (x < VISIBLE_SECTORS*SECTOR_WIDTH-1 && tmpTiles[x + 1][y] == 0
            		&& (walkableValue[x + 1][y]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_1)) == 0)
            {
                walkSectionXArray[requiredSteps] = x + 1;
                walkSectionYArray[requiredSteps] = y;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x + 1][y] = 8;
            }
            if (y > 0 && tmpTiles[x][y - 1] == 0
            		&& (walkableValue[x][y - 1]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_2)) == 0)
            {
                walkSectionXArray[requiredSteps] = x;
                walkSectionYArray[requiredSteps] = y - 1;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x][y - 1] = 1;
            }
            if (y < VISIBLE_SECTORS*SECTOR_HEIGHT-1 && tmpTiles[x][y + 1] == 0
            		&& (walkableValue[x][y + 1]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_0)) == 0)
            {
                walkSectionXArray[requiredSteps] = x;
                walkSectionYArray[requiredSteps] = y + 1;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x][y + 1] = 4;
            }
            if (x > 0 && y > 0 && (walkableValue[x][y - 1]
            		& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_2)) == 0
            		&& (walkableValue[x - 1][y]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_3)) == 0
            		&& (walkableValue[x - 1][y - 1]
            				& (0x7f ^ (WALKABLE_1 | WALKABLE_0))) == 0
            		&& tmpTiles[x - 1][y - 1] == 0)
            {
                walkSectionXArray[requiredSteps] = x - 1;
                walkSectionYArray[requiredSteps] = y - 1;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x - 1][y - 1] = 3;
            }
            if (x < VISIBLE_SECTORS*SECTOR_WIDTH-1 && y > 0
            		&& (walkableValue[x][y - 1]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_2)) == 0
            		&& (walkableValue[x + 1][y]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_1)) == 0
            		&& (walkableValue[x + 1][y - 1]
            				& (0x7f ^ (WALKABLE_3 | WALKABLE_0))) == 0
            		&& tmpTiles[x + 1][y - 1] == 0)
            {
                walkSectionXArray[requiredSteps] = x + 1;
                walkSectionYArray[requiredSteps] = y - 1;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x + 1][y - 1] = 9;
            }
            if (x > 0 && y < VISIBLE_SECTORS*SECTOR_HEIGHT-1
            		&& (walkableValue[x][y + 1]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_0)) == 0
            		&& (walkableValue[x - 1][y]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_3)) == 0
            		&& (walkableValue[x - 1][y + 1]
            				& (0x7f ^ (WALKABLE_2 | WALKABLE_1))) == 0
            		&& tmpTiles[x - 1][y + 1] == 0)
            {
                walkSectionXArray[requiredSteps] = x - 1;
                walkSectionYArray[requiredSteps] = y + 1;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x - 1][y + 1] = 6;
            }
            if (x < VISIBLE_SECTORS*SECTOR_WIDTH-1
            		&& y < VISIBLE_SECTORS*SECTOR_HEIGHT-1
            		&& (walkableValue[x][y + 1]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_0)) == 0
            		&& (walkableValue[x + 1][y]
            				& (WALKABLE_6 | WALKABLE_5 | WALKABLE_4 | WALKABLE_1)) == 0
            		&& (walkableValue[x + 1][y + 1]
            				& (0x7f ^ (WALKABLE_3 | WALKABLE_2))) == 0
            		&& tmpTiles[x + 1][y + 1] == 0)
            {
                walkSectionXArray[requiredSteps] = x + 1;
                walkSectionYArray[requiredSteps] = y + 1;
                requiredSteps = (requiredSteps + 1) % i3;
                tmpTiles[x + 1][y + 1] = 12;
            }
        }
        if (!pathAvailable)
            return -1;
        stepCount = 0;
        walkSectionXArray[stepCount] = x;
        walkSectionYArray[stepCount++] = y;
        int k3;
        for (int j3 = k3 = tmpTiles[x][y]; x != walkSectionX
        		|| y != walkSectionY; j3 = tmpTiles[x][y])
        {
            if (j3 != k3) {
                k3 = j3;
                walkSectionXArray[stepCount] = x;
                walkSectionYArray[stepCount++] = y;
            }
            if ((j3 & 2) != 0) {
                x++;
            } else if ((j3 & 8) != 0) {
                x--;
            }
            if ((j3 & 1) != 0) {
                y++;
            } else if ((j3 & 4) != 0) {
                y--;
            }
        }
        return stepCount;
    }

    public void updateObject(int x, int y, int id, int l)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
            return;
        }
        if (EntityHandler.getObjectDef(id).getType() == 1
        		|| EntityHandler.getObjectDef(id).getType() == 2)
        {
            int i1;
            int j1;
            if (l == 0 || l == 4) {
                i1 = EntityHandler.getObjectDef(id).getWidth();
                j1 = EntityHandler.getObjectDef(id).getHeight();
            } else {
                j1 = EntityHandler.getObjectDef(id).getWidth();
                i1 = EntityHandler.getObjectDef(id).getHeight();
            }
            for (int i = x; i < x + i1; i++) {
                for (int j = y; j < y + j1; j++) {
                    if (EntityHandler.getObjectDef(id).getType() == 1)
                        walkableValue[i][j] &= 0xffff ^ WALKABLE_6; //0xffbf;
                    else if (l == 0) {
                        walkableValue[i][j] &= 0xffff ^ WALKABLE_1; //0xfffd;
                        if (i > 0) {
                            andMinusWalkable(i - 1, j, WALKABLE_3);
                        }
                    } else if (l == 2) {
                        walkableValue[i][j] &= 0xffff ^ WALKABLE_2; //0xfffb;
                        if (j < VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
                            andMinusWalkable(i, j + 1, WALKABLE_0);
                        }
                    } else if (l == 4) {
                        walkableValue[i][j] &= 0xffff ^ WALKABLE_3; //0xfff7;
                        if (i < VISIBLE_SECTORS*SECTOR_WIDTH-1) {
                            andMinusWalkable(i + 1, j, WALKABLE_1);
                        }
                    } else if (l == 6) {
                        walkableValue[i][j] &= 0xffff ^ WALKABLE_0; //0xfffe;
                        if (j > 0) {
                            andMinusWalkable(i, j - 1, WALKABLE_2);
                        }
                    }
                }
            }
        }
    }

    public double getAveragedElevation(double xHighRes, double yHighRes, int hSection)
    {
        int xTile = (int)xHighRes;
        int yTile = (int)yHighRes;
        double x_r = xHighRes % 1;
        double y_r = yHighRes % 1;
        if (xTile < 0 || yTile < 0
        		|| xTile >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| yTile >= VISIBLE_SECTORS*SECTOR_HEIGHT-1)
            return 0;
        double h00;
        double h10;
        double h01;
        if (x_r <= 1 - y_r)
        { // use upper right triangle
            h00 = elevHandle.getElevation(xTile, yTile, hSection);
            h10 = elevHandle.getElevation(xTile + 1, yTile, hSection) - h00;
            h01 = elevHandle.getElevation(xTile, yTile + 1, hSection) - h00;
        }
        else
        { // use lower left triangle
            h00 = elevHandle.getElevation(xTile + 1, yTile + 1, hSection);
            h10 = elevHandle.getElevation(xTile, yTile + 1, hSection) - h00;
            h01 = elevHandle.getElevation(xTile + 1, yTile, hSection) - h00;
            x_r = 1 - x_r;
            y_r = 1 - y_r;
        }
        return h00 + (h10 * x_r) + (h01 * y_r);
    }

    public void updateWorld(int xSector, int ySector, int hSector)
    {
        garbageCollect();

        updateWorld(xSector, ySector, hSector, true);
        if (hSector == 0)
        {
            updateWorld(xSector, ySector, 1, false);
            updateWorld(xSector, ySector, 2, false);

            // update height for 3d objects
        	loadSections(xSector, ySector, hSector);
        }
    }

    public void updateDoorState(int x, int y, int direction, int type)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1)
            return;
        if (EntityHandler.getDoorDef(type).getDoorType() == 1)
        {
        	switch(direction)
        	{
        	case 0:
                walkableValue[x][y] |= WALKABLE_0;
                if (y > 0)
                    orWalkable(x, y - 1, WALKABLE_2);
                break;
        	case 1:
                walkableValue[x][y] |= WALKABLE_1;
                if (x > 0)
                    orWalkable(x - 1, y, WALKABLE_3);
                break;
        	case 2:
                walkableValue[x][y] |= WALKABLE_4;
        		break;
        	case 3:
                walkableValue[x][y] |= WALKABLE_5;
        		break;
        	}
        }
    }

    public void method412(int x, int y, int k, int l)
    {
        if (x < 0 || y < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
            return;
        }
        if (EntityHandler.getObjectDef(k).getType() == 1 || EntityHandler.getObjectDef(k).getType() == 2) {
            int i1;
            int j1;
            if (l == 0 || l == 4) {
                i1 = EntityHandler.getObjectDef(k).getWidth();
                j1 = EntityHandler.getObjectDef(k).getHeight();
            } else {
                j1 = EntityHandler.getObjectDef(k).getWidth();
                i1 = EntityHandler.getObjectDef(k).getHeight();
            }
            for (int k1 = x; k1 < x + i1; k1++) {
                for (int l1 = y; l1 < y + j1; l1++)
                    if (EntityHandler.getObjectDef(k).getType() == 1)
                        walkableValue[k1][l1] |= WALKABLE_6;
                    else if (l == 0) {
                        walkableValue[k1][l1] |= WALKABLE_1;
                        if (k1 > 0) {
                            orWalkable(k1 - 1, l1, WALKABLE_3);
                        }
                    } else if (l == 2) {
                        walkableValue[k1][l1] |= WALKABLE_2;
                        if (l1 < VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
                            orWalkable(k1, l1 + 1, WALKABLE_0);
                        }
                    } else if (l == 4) {
                        walkableValue[k1][l1] |= WALKABLE_3;
                        if (k1 < VISIBLE_SECTORS*SECTOR_WIDTH-1) {
                            orWalkable(k1 + 1, l1, WALKABLE_1);
                        }
                    } else if (l == 6) {
                        walkableValue[k1][l1] |= WALKABLE_0;
                        if (l1 > 0) {
                            orWalkable(k1, l1 - 1, WALKABLE_2);
                        }
                    }
            }
        }
    }

    public void updateDoor(int x, int y, int dir, int type)
    {
        if (x < 0 || y < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
            return;
        }
        if (EntityHandler.getDoorDef(type).getDoorType() == 1)
        {
            if (dir == 0)
            {
                walkableValue[x][y] &= 0xffff ^ WALKABLE_0; //0xfffe;
                if (y > 0)
                    andMinusWalkable(x, y - 1, WALKABLE_2);
            }
            else if (dir == 1)
            {
                walkableValue[x][y] &= 0xffff ^ WALKABLE_1; //0xfffd;
                if (x > 0)
                    andMinusWalkable(x - 1, y, WALKABLE_3);
            }
            else if (dir == 2)
                walkableValue[x][y] &= 0xffff ^ WALKABLE_4; //0xffef;
            else if (dir == 3)
                walkableValue[x][y] &= 0xffff ^ WALKABLE_5; //0xffdf;
        }
    }

    public void registerObjectDir(int x, int y, int dir)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return;
        }
        objectDirs[x][y] = dir;
    }
    
    public boolean getTextureUse()
    {
    	return textures;
    }
    
    public void setTextureUse(boolean useTxtr)
    {
    	textures = useTxtr;
    }

	boolean addHeightSectors(int sectorHeight, boolean showRoof,
			double currentX, double currentY)
	{
		boolean zoomCamera = true;
		for (int i = 0; i < WORLD_OBJ_AREA; i++)
		{ // draw other height sectors
			camera.removeModel(roofs[sectorHeight][i]);
			if (sectorHeight == 0) {
				camera.removeModel(walls[1][i]);
				camera.removeModel(roofs[1][i]);
				camera.removeModel(walls[2][i]);
				camera.removeModel(roofs[2][i]);
			}
			zoomCamera = true;
			if (sectorHeight == 0 && (walkableValue[(int)currentX][(int)currentY] & WALKABLE_INDOORS) == 0)
			{
				if (showRoof) {
					camera.addModel(roofs[sectorHeight][i]);
					if (sectorHeight == 0) {
						camera.addModel(walls[1][i]);
						camera.addModel(roofs[1][i]);
						camera.addModel(walls[2][i]);
						camera.addModel(roofs[2][i]);
					}
				}
				zoomCamera = false;
			}
		}
		return zoomCamera;
	}
	
	private static final double ROOF_LIM = 0x271;
	private static final int WALKABLE_0 = 0x1;
	private static final int WALKABLE_1 = 0x2;
	private static final int WALKABLE_2 = 0x4;
	private static final int WALKABLE_3 = 0x8;
	private static final int WALKABLE_4 = 0x10;
	private static final int WALKABLE_5 = 0x20;
	private static final int WALKABLE_6 = 0x40;
	private static final int WALKABLE_INDOORS = 0x80;
	
	private static final int WORLD_OBJ_SIDE = 4 * VISIBLE_SECTORS;
	private static final int WORLD_OBJ_AREA = WORLD_OBJ_SIDE * WORLD_OBJ_SIDE;

	private ZipFile tileArchive;
	private GameImage gameImage;
	private Camera camera;
	private boolean requiresClean;
	private boolean textures = true;
	private int[] groundTextureArray;

	private int[][] objectDirs;
	private int[][] walkableValue;
	private Model world;
	private ElevationHandle elevHandle;
	private Model[][] walls;
	private Model[] groundTiles;
	private Model[][] roofs;
	private Sector[] sectors;
	
	private int getSectorID(int i, int j)
	{
		return j*VISIBLE_SECTORS + i;
	}
	
	private Tile getTile(double x, double y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return null;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = getSectorID(i, j);
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile((int)x, (int)y);
    }

    private double getGroundElevation(double x, double y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? (tile.groundElevation * 3) : 0;
    }

    private void method400()
    {
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH; x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT; y++)
            {
                if (getGroundTexturesOverlay(x, y) == 250)
                {
                    if (x == SECTOR_WIDTH-1
                    		&& getGroundTexturesOverlay(x + 1, y) != 250
                    		&& getGroundTexturesOverlay(x + 1, y) != 2)
                        setGroundTexturesOverlay(x, y, 9);
                    else if (y == SECTOR_HEIGHT-1
                    		&& getGroundTexturesOverlay(x, y + 1) != 250
                    		&& getGroundTexturesOverlay(x, y + 1) != 2)
                        setGroundTexturesOverlay(x, y, 9);
                    else
                        setGroundTexturesOverlay(x, y, 2);
                }
            }
        }
    }

    private void method403(int wallHeight, int hSector, int x0, int y0, int x1, int y1) {
        double height = EntityHandler.getDoorDef(wallHeight).getHeight();
        if (elevHandle.getElevation(x0, y0, hSector) < ROOF_LIM) {
        	elevHandle.addElevation(x0, y0, ROOF_LIM + height);
        }
        if (elevHandle.getElevation(x1, y1, hSector) < ROOF_LIM) {
        	elevHandle.addElevation(x1, y1, ROOF_LIM + height);
        }
    }

    private void setGroundTexturesOverlay(int x, int y, int overlay)
    {
        Tile tile = getTile(x, y);
        if (tile != null)
        	tile.groundOverlay = (byte) overlay;
    }

    private void orWalkable(int x, int y, int val) {
        walkableValue[x][y] |= val;
    }

    private void garbageCollect() {
        if (requiresClean) {
            camera.cleanupModels();
        }
        for (int i = 0; i < WORLD_OBJ_AREA; i++) {
            groundTiles[i] = null;
            for (int j = 0; j < 4; j++) {
                walls[j][i] = null;
            }
            for (int k = 0; k < 4; k++) {
                roofs[k][i] = null;
            }
        }
        System.gc();
    }
    
    private void loadSections(int xSector, int ySector, int hSector)
    {
        int nextXSector = xSector / SECTOR_WIDTH;
        int nextYSector = ySector / SECTOR_HEIGHT;
        for (int y = -((VISIBLE_SECTORS-1)/2), j = 0; j < VISIBLE_SECTORS; ++j, ++y)
        	for (int x = -((VISIBLE_SECTORS-1)/2), i = 0; i < VISIBLE_SECTORS; ++i, ++x)
            	loadSection(nextXSector + x, nextYSector + y,
            			hSector, getSectorID(i, j));
    	/*
        int nextXSector = (xSector + SECTOR_WIDTH/2) / SECTOR_WIDTH;
        int nextYSector = (ySector + SECTOR_HEIGHT/2) / SECTOR_HEIGHT;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
        	for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	loadSection(nextXSector + (i - 1), nextYSector + (j - 1),
            			hSector, getSectorID(i, j));
    	 */
        method400();
        if (hSector == 0 || hSector == 3)
        	elevHandle.updateBaseElevation();
    }

    private void updateWorld(int xSector, int ySector, int hSector,
    		boolean currentHeight)
    {
    	loadSections(xSector, ySector, hSector);
        
        updateTiles(currentHeight, hSector);
        
        world.resetSurfAndPointCount();
        updateWalls(currentHeight, hSector); // and fences

        world.resetSurfAndPointCount();
        updateRoof(hSector);
    }
    
    private void updateTiles(boolean currentHeight, int hSector)
    {
        if (currentHeight)
        {
            gameImage.resetImagePixels(0);
            /* Reset all tiles to non-walkable */
            for (int xTile = 0; xTile < VISIBLE_SECTORS*SECTOR_WIDTH; xTile++)
                for (int yTile = 0; yTile < VISIBLE_SECTORS*SECTOR_HEIGHT; yTile++)
                    walkableValue[xTile][yTile] = 0;

            Model tiles = world;
            tiles.resetSurfAndPointCount();
            setTileElevation(tiles, hSector);

            makeTiles(tiles, hSector);
            makeBridges(tiles, hSector);

            tiles.setLightAndGradAndSource(true, Camera.GLOBAL_NORMAL,
            		Camera.FEATURE_NORMAL, Camera.light_x,
            		Camera.light_z, Camera.light_y);
            groundTiles = world.makeModels(12, 12, WORLD_OBJ_SIDE,
            		WORLD_OBJ_AREA, 233, false);
            for (int j6 = 0; j6 < WORLD_OBJ_AREA; j6++)
                camera.addModel(groundTiles[j6]); // floor tiles
            
        	// objects; roofs, fences, walls etc but not trees and and stuff.
            for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH; x++)
                for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT; y++)
                	elevHandle.setElevation(x, y, elevHandle.getElevation(x, y, hSector));
        }
    }
    
    private void updateWalls(boolean currentHeight, int hSector)
    {
        makeWalls(currentHeight, hSector);
        
        /* minimap */
        if (currentHeight)
            gameImage.storeSpriteHoriz(mudclient.SPRITE_MEDIA_START - 1,
            		0, 0, SECTOR_WIDTH*VISIBLE_SECTORS*3,
            		SECTOR_HEIGHT*VISIBLE_SECTORS*3);
        
        // viewport walls, fences etc.
        world.setLightAndGradAndSource(false, 60, 24, Camera.light_x, Camera.light_z, Camera.light_y);
        walls[hSector] = world.makeModels(12, 12, WORLD_OBJ_SIDE,
        		WORLD_OBJ_AREA, 338, true);
        for (int building = 0; building < WORLD_OBJ_AREA; building++)
            camera.addModel(walls[hSector][building]);
        
        /* update roof height inside buildings */
        updateWallElevation(hSector); // surrounding walls
        updateRoofElevation(hSector); // inside building
    }
    
    private void updateRoof(int hSector)
    {
        addRoof(hSector);
        
        world.setLightAndGradAndSource(true, 50, 50, Camera.light_x, Camera.light_z, Camera.light_y);
        roofs[hSector] = world.makeModels(12, 12, WORLD_OBJ_SIDE,
        		WORLD_OBJ_AREA, 169, true);
        for (int l9 = 0; l9 < WORLD_OBJ_AREA; l9++)
          camera.addModel(roofs[hSector][l9]);
        
        for (int j12 = 0; j12 < VISIBLE_SECTORS*SECTOR_WIDTH; j12++)
            for (int k14 = 0; k14 < VISIBLE_SECTORS*SECTOR_HEIGHT; k14++)
                if (elevHandle.getElevation(j12, k14, hSector) >= ROOF_LIM)
                	elevHandle.subtractElevation(j12, k14, ROOF_LIM);
    }
    
    private void setTileElevation(Model tiles, int hSector)
    {
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH; x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT; y++)
            {
                double z = -elevHandle.getElevation(x, y, hSector);
                /*
                if (getGroundTexturesOverlay(x, y) > 0
                		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getTexture() == 4)
                    z = -elevHandle.getZeroElevation(hSector);
                if (getGroundTexturesOverlay(x - 1, y) > 0
                		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x - 1, y) - 1).getTexture() == 4)
                    z = -elevHandle.getZeroElevation(hSector);
                if (getGroundTexturesOverlay(x, y - 1) > 0
                		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y - 1) - 1).getTexture() == 4)
                    z = -elevHandle.getZeroElevation(hSector);
                if (getGroundTexturesOverlay(x - 1, y - 1) > 0
                		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x - 1, y - 1) - 1).getTexture() == 4)
                    z = -elevHandle.getZeroElevation(hSector);
                    */
                tiles.insertCoordPoint(x, z, y);
            }
        }
    }

    /** Draws ground tiles that does not have overlays
     * (i.e. not roads, house floors, water etc.)
     */
    private void makeTiles(Model tiles, int hSector)
    {
        for (int tile_x = 0; tile_x < VISIBLE_SECTORS*SECTOR_WIDTH-1; tile_x++)
        {
            for (int tile_y = 0; tile_y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; tile_y++)
            {
                int texture1 = getGroundTexture(tile_x, tile_y);
                if (textures)
                	texture1 = 60 + texture1;
                else
                	texture1 = groundTextureArray[texture1];
                 
                int texture2 = texture1;
                int k12 = texture1;
                int l14 = 0;
                if (hSector == 1 || hSector == 2)
                {
                    texture1 = Model.INVISIBLE;
                    texture2 = Model.INVISIBLE;
                    k12 = Model.INVISIBLE;
                }
                if (getGroundTexturesOverlay(tile_x, tile_y) > 0)
                {  // Draws roads, water etc.
                    int tileOverlay = getGroundTexturesOverlay(tile_x, tile_y);
                    int tileTexture = EntityHandler.getTileDef(tileOverlay - 1).getTexture();
                    int i19 = method427(tile_x, tile_y);
                    if (textures)
                    {
                    	if (tileOverlay == 1)
                    		texture1 = texture2 = 56; // roads
                    	else
                    		texture1 = texture2 = EntityHandler.getTileDef(tileOverlay - 1).getColour();
                    }
                    else
                		texture1 = texture2 = EntityHandler.getTileDef(tileOverlay - 1).getColour();
                    if (tileTexture == 4)
                    {
                    	// water
                        texture1 = 1;
                        texture2 = 1;
                        if (tileOverlay == 12)
                        { // lava
                            texture1 = 31;
                            texture2 = 31;
                        }
                    }
                    if (tileTexture == 5)
                    {  // this part draws the road and water stuff.
                        if (getDiagonalWalls(tile_x, tile_y) > 0
                        		&& getDiagonalWalls(tile_x, tile_y) < 24000)
                            if (getOverlayIfRequired(tile_x - 1, tile_y, k12) != Model.INVISIBLE
                            		&& getOverlayIfRequired(tile_x, tile_y - 1, k12) != Model.INVISIBLE)
                            {
                                texture1 = getOverlayIfRequired(tile_x - 1, tile_y, k12);
                                l14 = 0;
                            }
                            else if (getOverlayIfRequired(tile_x + 1, tile_y, k12) != Model.INVISIBLE
                            		&& getOverlayIfRequired(tile_x, tile_y + 1, k12) != Model.INVISIBLE)
                            {
                                texture2 = getOverlayIfRequired(tile_x + 1, tile_y, k12);
                                l14 = 0;
                            }
                            else if (getOverlayIfRequired(tile_x + 1, tile_y, k12) != Model.INVISIBLE
                            		&& getOverlayIfRequired(tile_x, tile_y - 1, k12) != Model.INVISIBLE)
                            {
                            	texture2 = getOverlayIfRequired(tile_x + 1, tile_y, k12);
                                l14 = 1;
                            }
                            else if (getOverlayIfRequired(tile_x - 1, tile_y, k12) != Model.INVISIBLE
                            		&& getOverlayIfRequired(tile_x, tile_y + 1, k12) != Model.INVISIBLE)
                            {
                            	texture1 = getOverlayIfRequired(tile_x - 1, tile_y, k12);
                                l14 = 1;
                            }
                    }
                    else if (tileTexture != 2 || getDiagonalWalls(tile_x, tile_y) > 0
                    		&& getDiagonalWalls(tile_x, tile_y) < 24000)
                    {  // when a road/water tile and a non-road/water tile shares the same tile
                        if (method427(tile_x - 1, tile_y) != i19 && method427(tile_x, tile_y - 1) != i19)
                        {
                            texture1 = k12;
                            l14 = 0;
                        }
                        else if (method427(tile_x + 1, tile_y) != i19
                        		&& method427(tile_x, tile_y + 1) != i19)
                        {
                            texture2 = k12;
                            l14 = 0;
                        }
                        else if (method427(tile_x + 1, tile_y) != i19
                        		&& method427(tile_x, tile_y - 1) != i19)
                        {
                            texture2 = k12;
                            l14 = 1;
                        }
                        else if (method427(tile_x - 1, tile_y) != i19
                        		&& method427(tile_x, tile_y + 1) != i19)
                        {
                            texture1 = k12;
                            l14 = 1;
                        }
                    }
                    if (EntityHandler.getTileDef(tileOverlay - 1).getObjectType() != 0)
                        walkableValue[tile_x][tile_y] |= WALKABLE_6;
                    if (EntityHandler.getTileDef(tileOverlay - 1).getTexture() == 2)
                        walkableValue[tile_x][tile_y] |= WALKABLE_INDOORS;
                }
                drawOnMinimap(tile_x, tile_y, l14, texture1, texture2);
                double i17 = ((elevHandle.getElevation(tile_x + 1, tile_y + 1, hSector)
                		- elevHandle.getElevation(tile_x + 1, tile_y, hSector))
                		+ elevHandle.getElevation(tile_x, tile_y + 1, hSector))
                		- elevHandle.getElevation(tile_x, tile_y, hSector);
                if (texture1 != texture2 || i17 != 0)
                {
                    int surfacePoints1[] = new int[3];
                    int surfacePoints2[] = new int[3];
                    if (l14 == 0)
                    {
                        if (texture1 != Model.INVISIBLE)
                        {
                            surfacePoints1[0] = tile_y + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints1[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints1[2] = (tile_y+1) + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            int i = tiles.addSurface(3, surfacePoints1, Model.INVISIBLE, texture1);
                            selected[i] = new Point(tile_x, tile_y);
                            tiles.entityType[i] = 0x30d40 + i;
                        }
                        if (texture2 != Model.INVISIBLE)
                        {
                            surfacePoints2[0] = (tile_y+1) + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints2[1] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints2[2] = tile_y + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            int i = tiles.addSurface(3, surfacePoints2, Model.INVISIBLE, texture2);
                            selected[i] = new Point(tile_x, tile_y);
                            tiles.entityType[i] = 0x30d40 + i;
                        }
                    }
                    else
                    {
                        if (texture1 != Model.INVISIBLE)
                        {
                            surfacePoints1[0] = (tile_y+1) + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints1[1] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints1[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            int i = tiles.addSurface(3, surfacePoints1, Model.INVISIBLE, texture1);
                            selected[i] = new Point(tile_x, tile_y);
                            tiles.entityType[i] = 0x30d40 + i;
                        }
                        if (texture2 != Model.INVISIBLE)
                        {
                            surfacePoints2[0] = tile_y + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints2[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            surfacePoints2[2] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                            int i = tiles.addSurface(3, surfacePoints2, Model.INVISIBLE, texture2);
                            selected[i] = new Point(tile_x, tile_y);
                            tiles.entityType[i] = 0x30d40 + i;
                        }
                    }
                }
                else if (texture1 != Model.INVISIBLE)
                {
                    int surfacePoints[] = new int[4];
                    surfacePoints[0] = tile_y + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                    surfacePoints[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                    surfacePoints[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                    surfacePoints[3] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                    int l19 = tiles.addSurface(4, surfacePoints, Model.INVISIBLE, texture1);
                    selected[l19] = new Point(tile_x, tile_y);
                    tiles.entityType[l19] = 0x30d40 + l19;
                }
            }

        }
    }

    /**
     * bridges it seems
     * @param hSector TODO
     */
    private void makeBridges(Model tiles, int hSector)
    {
        for (int x = 1; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 1; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                if (getGroundTexturesOverlay(x, y) > 0
                		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getTexture() == 4)
                {
                    int color = EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getColour();
                    int p00 = tiles.insertCoordPoint(x, -elevHandle.getElevation(x, y, hSector), y);
                    int p10 = tiles.insertCoordPoint(x + 1, -elevHandle.getElevation(x + 1, y, hSector), y);
                    int p11 = tiles.insertCoordPoint(x + 1, -elevHandle.getElevation(x + 1, y + 1, hSector), y + 1);
                    int p01 = tiles.insertCoordPoint(x, -elevHandle.getElevation(x, y + 1, hSector), y + 1);
                    int rect[] = {p00, p10, p11, p01};
                    int i = tiles.addSurface(4, rect, color, Model.INVISIBLE);
                    selected[i] = new Point(x, y);
                    tiles.entityType[i] = 0x30d40 + i;
                    drawOnMinimap(x, y, 0, color, color);
                }
                else if (getGroundTexturesOverlay(x, y) == 0
                		|| EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getTexture() != 3)
                {
                	int[] x_arr = {0, 0, 1, -1};
                	int[] y_arr = {1, -1, 0, 0};
                	for (int i = 0; i < 4; ++i)
                	{
                		if (getGroundTexturesOverlay(x + x_arr[i], y + y_arr[i]) > 0
                        		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x + x_arr[i], y + y_arr[i]) - 1).getTexture() == 4)
                        {
                            int color = EntityHandler.getTileDef(getGroundTexturesOverlay(x + x_arr[i], y + y_arr[i]) - 1).getColour();
                            int p00 = tiles.insertCoordPoint(x, -elevHandle.getElevation(x, y, hSector), y);
                            int p10 = tiles.insertCoordPoint(x + 1, -elevHandle.getElevation(x + 1, y, hSector), y);
                            int p11 = tiles.insertCoordPoint(x + 1, -elevHandle.getElevation(x + 1, y + 1, hSector), y + 1);
                            int p01 = tiles.insertCoordPoint(x, -elevHandle.getElevation(x, y + 1, hSector), y + 1);
                            int rect[] = {p00, p10, p11, p01};
                            int j = tiles.addSurface(4, rect, color, Model.INVISIBLE);
                            selected[j] = new Point(x, y);
                            tiles.entityType[j] = 0x30d40 + j;
                            drawOnMinimap(x, y, 0, color, color);
                        }
                	}
                }
            }
        }
    }
    
    private void makeWalls(boolean currentHeight, int hSector)
    {
        int mmWallColor = 0xc0c0c0;
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                int k3 = getVerticalWall(x, y);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0)
                {
                    addWall(world, hSector, k3 - 1, x, y, x + 1, y);
                    if (currentHeight && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0)
                    {
                        walkableValue[x][y] |= WALKABLE_0;
                        if (y > 0)
                            orWalkable(x, y - 1, WALKABLE_2);
                    }
                    if (currentHeight)
                    {
                        gameImage.drawLineX(x * 3, y * 3, 3, mmWallColor);
                    }
                }
                k3 = getHorizontalWall(x, y);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0)
                {
                    addWall(world, hSector, k3 - 1, x, y, x, y + 1);
                    if (currentHeight && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0)
                    {
                        walkableValue[x][y] |= WALKABLE_1;
                        if (x > 0) {
                            orWalkable(x - 1, y, WALKABLE_3);
                        }
                    }
                    if (currentHeight) 
                    {
                        gameImage.drawLineY(x * 3, y * 3, 3, mmWallColor);
                    }
                }
                k3 = getDiagonalWalls(x, y);
                if (k3 > 0 && k3 < 12000 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0)
                {
                    addWall(world, hSector, k3 - 1, x, y, x + 1, y + 1);
                    if (currentHeight && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0)
                    {
                        walkableValue[x][y] |= WALKABLE_5;
                    }
                    if (currentHeight) {
                        gameImage.setMinimapPixel(x * 3, y * 3, mmWallColor);
                        gameImage.setMinimapPixel(x * 3 + 1, y * 3 + 1, mmWallColor);
                        gameImage.setMinimapPixel(x * 3 + 2, y * 3 + 2, mmWallColor);
                    }
                }
                if (k3 > 12000 && k3 < 24000 && EntityHandler.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    addWall(world, hSector, k3 - 12001, x + 1, y, x, y + 1);
                    if (currentHeight && EntityHandler.getDoorDef(k3 - 12001).getDoorType() != 0) {
                        walkableValue[x][y] |= WALKABLE_4;
                    }
                    if (currentHeight) {
                        gameImage.setMinimapPixel(x * 3 + 2, y * 3, mmWallColor);
                        gameImage.setMinimapPixel(x * 3 + 1, y * 3 + 1, mmWallColor);
                        gameImage.setMinimapPixel(x * 3, y * 3 + 2, mmWallColor);
                    }
                }
            }
        }
    }
    
    private void updateWallElevation(int hSector)
    {
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                int wallID = getVerticalWall(x, y);
                if (wallID > 0)
                    method403(wallID - 1, hSector, x, y, x + 1, y);
                wallID = getHorizontalWall(x, y);
                if (wallID > 0)
                    method403(wallID - 1, hSector, x, y, x, y + 1);
                wallID = getDiagonalWalls(x, y);
                if (wallID > 0 && wallID < 12000)
                    method403(wallID - 1, hSector, x, y, x + 1, y + 1);
                if (wallID > 12000 && wallID < 24000)
                    method403(wallID - 12001, hSector, x + 1, y, x, y + 1);
            }
        }
    }
    
    private void updateRoofElevation(int hSector)
    {
        for (int i = 1; i < VISIBLE_SECTORS*SECTOR_WIDTH-1; i++)
        {
            for (int j = 1; j < VISIBLE_SECTORS*SECTOR_HEIGHT-1; j++)
            {
                if (getRoofTexture(i, j) > 0)
                {
                	int[] x = {i, i+1, i+1, i};
                	int[] y = {j, j, j+1, j+1};
                    double[] z = {
                    		elevHandle.getElevation(x[0], y[0], hSector),
                    		elevHandle.getElevation(x[1], y[1], hSector),
                    		elevHandle.getElevation(x[2], y[2], hSector),
                    		elevHandle.getElevation(x[3], y[3], hSector)
                    };
                    if (z[0] > ROOF_LIM)
                    	z[0] -= ROOF_LIM;
                    if (z[1] > ROOF_LIM)
                    	z[1] -= ROOF_LIM;
                    if (z[2] > ROOF_LIM)
                    	z[2] -= ROOF_LIM;
                    if (z[3] > ROOF_LIM)
                    	z[3] -= ROOF_LIM;
                    double zMax = z[0];
                    if (z[1] > zMax)
                        zMax = z[1];
                    if (z[2] > zMax)
                        zMax = z[2];
                    if (z[3] > zMax)
                        zMax = z[3];
                    if (zMax >= ROOF_LIM)
                        zMax -= ROOF_LIM; 
                    if (z[0] < ROOF_LIM)
                    	elevHandle.setElevation(x[0], y[0], zMax);
                    else
                    	elevHandle.subtractElevation(x[0], y[0], ROOF_LIM);
                    if (z[1] < ROOF_LIM)
                    	elevHandle.setElevation(x[1], y[1], zMax);
                    else
                    	elevHandle.subtractElevation(x[1], y[1], ROOF_LIM);
                    if (z[2] < ROOF_LIM)
                    	elevHandle.setElevation(x[2], y[2], zMax);
                    else
                    	elevHandle.subtractElevation(x[2], y[2], ROOF_LIM);
                    if (z[3] < ROOF_LIM)
                    	elevHandle.setElevation(x[3], y[3], zMax);
                    else
                    	elevHandle.subtractElevation(x[3], y[3], ROOF_LIM);
                }
            }
        }
    }
    
    private void addRoof(int hSector)
    {
        for (int i = 1; i < VISIBLE_SECTORS*SECTOR_WIDTH-1; i++)
        {
            for (int j = 1; j < VISIBLE_SECTORS*SECTOR_HEIGHT-1; j++)
            {
                int color;
                if ((color = getRoofTexture(i, j)) > 0)
                {
                	int[] x = {i, i+1, i+1, i};
                	int[] y = {j, j, j+1, j+1};
                	double[] x_big = {i, i, i+1, i+1};
                	double[] y_big = {j, j, j+1, j+1};
                    double[] z = {
                    		elevHandle.getElevation(x[0], y[0], hSector+1),
                    		elevHandle.getElevation(x[1], y[1], hSector+1),
                    		elevHandle.getElevation(x[2], y[2], hSector+1),
                    		elevHandle.getElevation(x[3], y[3], hSector+1)
                    };
                    double roofHeight = EntityHandler.getElevationDef(color - 1).getRoofHeight();
                    if (isNotEdge(x[0], y[0]) && z[0] < ROOF_LIM) {
                        z[0] += roofHeight + ROOF_LIM;
                    	elevHandle.setElevation(x[0], y[0], z[0]);
                    }
                    if (isNotEdge(x[1], y[1]) && z[1] < ROOF_LIM) {
                        z[1] += roofHeight + ROOF_LIM;
                    	elevHandle.setElevation(x[1], y[1], z[1]);
                    }
                    if (isNotEdge(x[2], y[2]) && z[2] < ROOF_LIM) {
                        z[2] += roofHeight + ROOF_LIM;
                    	elevHandle.setElevation(x[2], y[2], z[2]);
                    }
                    if (isNotEdge(x[3], y[3]) && z[3] < ROOF_LIM) {
                        z[3] += roofHeight + ROOF_LIM;
                    	elevHandle.setElevation(x[3], y[3], z[3]);
                    }
                    if (z[0] >= ROOF_LIM)
                        z[0] -= ROOF_LIM;
                    if (z[1] >= ROOF_LIM)
                        z[1] -= ROOF_LIM;
                    if (z[2] >= ROOF_LIM)
                        z[2] -= ROOF_LIM;
                    if (z[3] >= ROOF_LIM)
                        z[3] -= ROOF_LIM;
                    double roofStickOut = 0.125; // how much the roof should stick out
                    if (!nearbyRoof(x[0] - 1, y[0]))
                    	x_big[0] -= roofStickOut;
                    if (!nearbyRoof(x[0] + 1, y[0]))
                        x_big[0] += roofStickOut;
                    if (!nearbyRoof(x[0], y[0] - 1))
                        y_big[0] -= roofStickOut;
                    if (!nearbyRoof(x[0], y[0] + 1))
                        y_big[0] += roofStickOut;
                    if (!nearbyRoof(x[1] - 1, y[1]))
                        x_big[2] -= roofStickOut;
                    if (!nearbyRoof(x[1] + 1, y[1]))
                        x_big[2] += roofStickOut;
                    if (!nearbyRoof(x[1], y[1] - 1))
                        y_big[1] -= roofStickOut;
                    if (!nearbyRoof(x[1], y[1] + 1))
                        y_big[1] += roofStickOut;
                    if (!nearbyRoof(x[2] - 1, y[2]))
                        x_big[3] -= roofStickOut;
                    if (!nearbyRoof(x[2] + 1, y[2]))
                        x_big[3] += roofStickOut;
                    if (!nearbyRoof(x[2], y[2] - 1))
                        y_big[2] -= roofStickOut;
                    if (!nearbyRoof(x[2], y[2] + 1))
                        y_big[2] += roofStickOut;
                    if (!nearbyRoof(x[3] - 1, y[3]))
                        x_big[1] -= roofStickOut;
                    if (!nearbyRoof(x[3] + 1, y[3]))
                        x_big[1] += roofStickOut;
                    if (!nearbyRoof(x[3], y[3] - 1))
                        y_big[3] -= roofStickOut;
                    if (!nearbyRoof(x[3], y[3] + 1))
                        y_big[3] += roofStickOut;
                    color = EntityHandler.getElevationDef(color - 1).getUnknown2();
                    z[0] = -z[0];
                    z[1] = -z[1];
                    z[2] = -z[2];
                    z[3] = -z[3];
                    if (getDiagonalWalls(i, j) > 12000
                    		&& getDiagonalWalls(i, j) < 24000
                    		&& getRoofTexture(i - 1, j - 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                        rect[1] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                        rect[2] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                        world.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (getDiagonalWalls(i, j) > 12000
                    		&& getDiagonalWalls(i, j) < 24000
                    		&& getRoofTexture(i + 1, j + 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                        rect[1] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                        rect[2] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                        world.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (getDiagonalWalls(i, j) > 0
                    		&& getDiagonalWalls(i, j) < 12000
                    		&& getRoofTexture(i + 1, j - 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                        rect[1] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                        rect[2] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                        world.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (getDiagonalWalls(i, j) > 0
                    		&& getDiagonalWalls(i, j) < 12000
                    		&& getRoofTexture(i - 1, j + 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                        rect[1] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                        rect[2] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                        world.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (z[0] == z[1] && z[2] == z[3]) {
                        int rect[] = new int[4];
                        rect[0] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                        rect[1] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                        rect[2] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                        rect[3] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                        world.addSurface(4, rect, color, Model.INVISIBLE);
                    }
                    else if (z[0] == z[3] && z[1] == z[2]) {
                        int rect[] = new int[4];
                        rect[0] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                        rect[1] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                        rect[2] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                        rect[3] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                        world.addSurface(4, rect, color, Model.INVISIBLE);
                    }
                    else
                    {
                        boolean flag1 = true;
                        if (getRoofTexture(i - 1, j - 1) > 0)
                            flag1 = false;
                        if (getRoofTexture(i + 1, j + 1) > 0)
                            flag1 = false;
                        if (!flag1)
                        {
                            int rect1[] = new int[3];
                            rect1[0] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                            rect1[1] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                            rect1[2] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                            world.addSurface(3, rect1, color, Model.INVISIBLE);
                            int rec2[] = new int[3];
                            rec2[0] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                            rec2[1] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                            rec2[2] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                            world.addSurface(3, rec2, color, Model.INVISIBLE);
                        }
                        else
                        {
                            int rect1[] = new int[3];
                            rect1[0] = world.insertCoordPoint(x_big[0], z[0], y_big[0]);
                            rect1[1] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                            rect1[2] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                            world.addSurface(3, rect1, color, Model.INVISIBLE);
                            int rect2[] = new int[3];
                            rect2[0] = world.insertCoordPoint(x_big[3], z[2], y_big[2]);
                            rect2[1] = world.insertCoordPoint(x_big[1], z[3], y_big[3]);
                            rect2[2] = world.insertCoordPoint(x_big[2], z[1], y_big[1]);
                            world.addSurface(3, rect2, color, Model.INVISIBLE);
                        }
                    }
                }
            }
        }
    }

    private void andMinusWalkable(int x, int y, int val)
    {
        walkableValue[x][y] &= 0xffff ^ val;
    }

    private void drawOnMinimap(int tile_x, int tile_y, int k, int neColor16, int swColor16)
    {
        int x = tile_x * 3;
        int y = tile_y * 3;
        int neColor24 = camera.color16bitTo24bit(neColor16);
        int swColor24 = camera.color16bitTo24bit(swColor16);
        if (k == 0)
        { // one tile is 3x3
            gameImage.drawLineX(x, y, 3, neColor24);
            gameImage.drawLineX(x, y + 1, 2, neColor24);
            gameImage.drawLineX(x, y + 2, 1, neColor24);
            gameImage.drawLineX(x + 2, y + 1, 1, swColor24);
            gameImage.drawLineX(x + 1, y + 2, 2, swColor24);
        }
        else if (k == 1)
        {
            gameImage.drawLineX(x, y, 3, swColor24);
            gameImage.drawLineX(x + 1, y + 1, 2, swColor24);
            gameImage.drawLineX(x + 2, y + 2, 1, swColor24);
            gameImage.drawLineX(x, y + 1, 1, neColor24);
            gameImage.drawLineX(x, y + 2, 2, neColor24);
        }
    }

    private int getVerticalWall(int x, int y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? (tile.verticalWall & 0xff) : 0;
    }

    private boolean nearbyRoof(int x, int y)
    {
        return getRoofTexture(x, y) > 0
        		|| getRoofTexture(x - 1, y) > 0
        		|| getRoofTexture(x - 1, y - 1) > 0
        		|| getRoofTexture(x, y - 1) > 0;
    }

    private int getGroundTexturesOverlay(int x, int y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? (tile.groundOverlay & 0xff) : 0;
    }

    private int getDiagonalWalls(int x, int y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? tile.diagonalWalls : 0;
    }

    private void addWall(Model model, int hSector, int i,
    		int x0, int y0, int x1, int y1)
    {
        double height = EntityHandler.getDoorDef(i).getHeight();
        int texture1 = EntityHandler.getDoorDef(i).getTexture1();
        int texture2 = EntityHandler.getDoorDef(i).getTexture2();
        int p0 = model.insertCoordPoint(x0, -elevHandle.getElevation(x0, y0, hSector), y0);
        int p1 = model.insertCoordPoint(x0, -elevHandle.getElevation(x0, y0, hSector) - height, y0);
        int p2 = model.insertCoordPoint(x1, -elevHandle.getElevation(x1, y1, hSector) - height, y1);
        int p3 = model.insertCoordPoint(x1, -elevHandle.getElevation(x1, y1, hSector), y1);
        int i4 = model.addSurface(4, new int[]{p0, p1, p2, p3}, texture1, texture2);
        if (EntityHandler.getDoorDef(i).getUnknown() == 5) {
            model.entityType[i4] = 30000 + i;
        } else {
            model.entityType[i4] = 0;
        }
    }

    private int getOverlayIfRequired(int x, int y, int underlay)
    {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0)
            return underlay;
        return EntityHandler.getTileDef(texture - 1).getColour();
    }

    private int getGroundTexture(int x, int y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? (tile.groundTexture & 0xFF) : 0;
    }

    private boolean isNotEdge(int i, int j)
    {
        return getRoofTexture(i, j) > 0
        		&& getRoofTexture(i - 1, j) > 0
        		&& getRoofTexture(i - 1, j - 1) > 0
        		&& getRoofTexture(i, j - 1) > 0;
    }

    private int getWalkableValue(int x, int y)
    {
        if (x < 0 || y < 0
        		|| x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y >= VISIBLE_SECTORS*SECTOR_HEIGHT)
            return 0;
        return walkableValue[x][y];
    }

    private int getRoofTexture(int x, int y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? tile.roofTexture : 0;
    }

    private int getHorizontalWall(int x, int y)
    {
        Tile tile = getTile(x, y);
        return tile != null ? (tile.horizontalWall & 0xff) : 0;
    }

    private int method427(int x, int y)
    {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0)
            return -1;
        return EntityHandler.getTileDef(texture - 1).getTexture() != 2 ? 0 : 1;
    }

    private void loadSection(int sectionX, int sectionY, int height, int sector)
    {
        Sector s = null;
        try {
            String filename = "h" + height + "x" + sectionX + "y" + sectionY;
            ZipEntry e = tileArchive.getEntry(filename);
            if (e == null) {
                s = new Sector();
                if (height == 0 || height == 3) {
                    for (int i = 0; i < SECTOR_HEIGHT*SECTOR_WIDTH; i++) {
                        s.getTile(i).groundOverlay = (byte) (height == 0 ? -6 : 8);
                    }
                }
            } else {
                ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(tileArchive.getInputStream(e)));
                s = Sector.unpack(data);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        sectors[sector] = s;
    }
    
    private class ElevationHandle
    {
    	double[][] elevation;
    	final double height = 1.5;
    	ElevationHandle()
    	{
            elevation = new double[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
    	}
    	
    	void updateBaseElevation()
    	{
            for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH; x++)
            {
                for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT; y++)
                {
                    double z = getGroundElevation(x, y);
                    if (getGroundTexturesOverlay(x, y) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getTexture() == 4)
                        z = 0;
                    if (getGroundTexturesOverlay(x - 1, y) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x - 1, y) - 1).getTexture() == 4)
                        z = 0;
                    if (getGroundTexturesOverlay(x, y - 1) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y - 1) - 1).getTexture() == 4)
                        z = 0;
                    if (getGroundTexturesOverlay(x - 1, y - 1) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x - 1, y - 1) - 1).getTexture() == 4)
                        z = 0;
                    elevation[x][y] = z;
                }
            }
    	}
    	
    	void addElevation(int x, int y, double elev)
    	{
    		//elevation[x][y] += elev;
    	}
    	
    	void subtractElevation(int x, int y, double elev)
    	{
    		//elevation[x][y] -= elev;
    	}
    	
    	void setElevation(int x, int y, double elev)
    	{
    		//elevation[x][y] = elev;
    	}
    	
    	double getElevation(int x, int y, int h)
    	{
    		return elevation[x][y] + h*height;
    	}
    	
    	double getZeroElevation(int h)
    	{
    		return h*height;
    	}
    }
}