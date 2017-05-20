package client;

import client.util.Config;
import client.util.DataConversions;
import entityhandling.EntityHandler;
import model.Sector;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EngineHandle
{

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
                if (x < 95 && x + 1 >= x1 && x + 1 <= x2
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
                if (y < 95 && x >= x1 && x <= x2
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

    public double getGroundElevation(double x, double y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT)
            return 0;
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return (sectors[section].getTile((int)x, (int)y).groundElevation) * 3;
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
                        if (j < 95) {
                            andMinusWalkable(i, j + 1, WALKABLE_0);
                        }
                    } else if (l == 4) {
                        walkableValue[i][j] &= 0xffff ^ WALKABLE_3; //0xfff7;
                        if (i < 95) {
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

    public double getAveragedElevation(double xHighRes, double yHighRes)
    {
        int xTile = (int) (xHighRes / GAME_SIZE);
        int yTile = (int) (yHighRes / GAME_SIZE);
        double x_1 = xHighRes % GAME_SIZE;
        double y_1 = yHighRes % GAME_SIZE;
        if (xTile < 0 || yTile < 0
        		|| xTile >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| yTile >= VISIBLE_SECTORS*SECTOR_HEIGHT-1)
            return 0;
        double h00;
        double h10;
        double h01;
        if (x_1 <= GAME_SIZE - y_1)
        { // y >= x
            h00 = getGroundElevation(xTile, yTile);
            h10 = getGroundElevation(xTile + 1, yTile) - h00;
            h01 = getGroundElevation(xTile, yTile + 1) - h00;
        }
        else
        {
            h00 = getGroundElevation(xTile + 1, yTile + 1);
            h10 = getGroundElevation(xTile, yTile + 1) - h00;
            h01 = getGroundElevation(xTile + 1, yTile) - h00;
            x_1 = GAME_SIZE - x_1;
            y_1 = GAME_SIZE - y_1;
        }
        return h00 + (h10 * x_1) / GAME_SIZE + (h01 * y_1) / GAME_SIZE;
    }

    public void method400()
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

    public void method401(int x, int y, int height)
    {
        garbageCollect();
        int nextXSector = (x + SECTOR_WIDTH/2) / SECTOR_WIDTH;
        int nextYSector = (y + SECTOR_HEIGHT/2) / SECTOR_HEIGHT;
        method409(x, y, height, true);
        if (height == 0)
        {
            method409(x, y, 1, false);
            method409(x, y, 2, false);
            for (int j = 0; j < VISIBLE_SECTORS; ++j)
            	for (int i = 0; i < VISIBLE_SECTORS; ++i)
                	loadSection(i + nextXSector - 1, j + nextYSector - 1,
                			height, j*VISIBLE_SECTORS + i);
            method400();
        }
    }

    public void method403(int wallHeight, int x0, int y0, int x1, int y1) {
        double height = EntityHandler.getDoorDef(wallHeight).getHeight();
        if (elevation[x0][y0] < ROOF_LIM) {
            elevation[x0][y0] += ROOF_LIM + height;
        }
        if (elevation[x1][y1] < ROOF_LIM) {
            elevation[x1][y1] += ROOF_LIM + height;
        }
    }

    public void setGroundTexturesOverlay(int x, int y, int k)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        sectors[section].getTile(x, y).groundOverlay = (byte) k;
    }

    public void orWalkable(int x, int y, int val) {
        walkableValue[x][y] |= val;
    }

    public void garbageCollect() {
        if (requiresClean) {
            camera.cleanupModels();
        }
        for (int i = 0; i < 64; i++) {
            aModelArray596[i] = null;
            for (int j = 0; j < 4; j++) {
                walls[j][i] = null;
            }
            for (int k = 0; k < 4; k++) {
                roofs[k][i] = null;
            }
        }
        System.gc();
    }

    public void method408(int x, int y, int k, int l)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1)
            return;
        if (EntityHandler.getDoorDef(l).getDoorType() == 1) {
            if (k == 0) {
                walkableValue[x][y] |= WALKABLE_0;
                if (y > 0) {
                    orWalkable(x, y - 1, WALKABLE_2);
                }
            } else if (k == 1) {
                walkableValue[x][y] |= WALKABLE_1;
                if (x > 0) {
                    orWalkable(x - 1, y, WALKABLE_3);
                }
            } else if (k == 2) {
                walkableValue[x][y] |= WALKABLE_4;
            } else if (k == 3) {
                walkableValue[x][y] |= WALKABLE_5;
            }
        }
    }

    public void method409(int xSector, int ySector, int hSector, boolean flag)
    {
        int nextXSector = (xSector + SECTOR_WIDTH/2) / SECTOR_WIDTH;
        int nextYSector = (ySector + SECTOR_HEIGHT/2) / SECTOR_HEIGHT;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
        	for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	loadSection(i + nextXSector - 1, j + nextYSector - 1,
            			hSector, j*VISIBLE_SECTORS + i);
        method400();
        if (aModel == null) {
            aModel = new Model(4672*VISIBLE_SECTORS*VISIBLE_SECTORS,
            		4672*VISIBLE_SECTORS*VISIBLE_SECTORS, true, true, false, false, true);
        }
        if (flag) {
            gameImage.resetImagePixels(0);
            /* Reset all tiles to non-walkable */
            for (int xTile = 0; xTile < VISIBLE_SECTORS*SECTOR_WIDTH; xTile++)
                for (int yTile = 0; yTile < VISIBLE_SECTORS*SECTOR_HEIGHT; yTile++)
                    walkableValue[xTile][yTile] = 0;

            Model model = aModel;
            model.method176();
            /* Sets elevation. */
            for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH; x++)
            {
                for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT; y++)
                {
                    double z = -getGroundElevation(x, y);
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
                    model.insertCoordPointNoDuplicate(x * GAME_SIZE, z, y * GAME_SIZE);
                }
            }

            /* Draws ground tiles that does not have overlays */
            /* (i.e. not roads, house floors, water etc.) */
            for (int tile_x = 0; tile_x < VISIBLE_SECTORS*SECTOR_WIDTH-1; tile_x++)
            {
                for (int tile_y = 0; tile_y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; tile_y++)
                {
                    int texture = getGroundTexture(tile_x, tile_y);
                    int texture1;
                    if (textures)
                    	texture1 = 60 + texture; // use textures
                    else
                    	texture1 = groundTextureArray[texture]; // do not use textures
                     
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
                        int unknown = EntityHandler.getTileDef(tileOverlay - 1).getTexture();
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
                        if (unknown == 4)
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
                        if (unknown == 5)
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
                        else if (unknown != 2 || getDiagonalWalls(tile_x, tile_y) > 0
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
                    double i17 = ((getGroundElevation(tile_x + 1, tile_y + 1)
                    		- getGroundElevation(tile_x + 1, tile_y))
                    		+ getGroundElevation(tile_x, tile_y + 1))
                    		- getGroundElevation(tile_x, tile_y);
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
                                int i = model.addSurface(3, surfacePoints1, Model.INVISIBLE, texture1);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
                            }
                            if (texture2 != Model.INVISIBLE)
                            {
                                surfacePoints2[0] = (tile_y+1) + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints2[1] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints2[2] = tile_y + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                int i = model.addSurface(3, surfacePoints2, Model.INVISIBLE, texture2);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
                            }
                        }
                        else
                        {
                            if (texture1 != Model.INVISIBLE)
                            {
                                surfacePoints1[0] = (tile_y+1) + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints1[1] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints1[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                int i = model.addSurface(3, surfacePoints1, Model.INVISIBLE, texture1);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
                            }
                            if (texture2 != Model.INVISIBLE)
                            {
                                surfacePoints2[0] = tile_y + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints2[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints2[2] = (tile_y+1) + (tile_x+1) * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                int i = model.addSurface(3, surfacePoints2, Model.INVISIBLE, texture2);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
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
                        int l19 = model.addSurface(4, surfacePoints, Model.INVISIBLE, texture1);
                        selectedX[l19] = tile_x;
                        selectedY[l19] = tile_y;
                        model.entityType[l19] = 0x30d40 + l19;
                    }
                }

            }
            /* bridges it seems */
            for (int x = 1; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
            {
                for (int y = 1; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
                {
                    if (getGroundTexturesOverlay(x, y) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getTexture() == 4)
                    {
                        int color = EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getColour();
                        int p00 = model.insertCoordPointNoDuplicate(x * GAME_SIZE, -getGroundElevation(x, y), y * GAME_SIZE);
                        int p10 = model.insertCoordPointNoDuplicate((x + 1) * GAME_SIZE, -getGroundElevation(x + 1, y), y * GAME_SIZE);
                        int p11 = model.insertCoordPointNoDuplicate((x + 1) * GAME_SIZE, -getGroundElevation(x + 1, y + 1), (y + 1) * GAME_SIZE);
                        int p01 = model.insertCoordPointNoDuplicate(x * GAME_SIZE, -getGroundElevation(x, y + 1), (y + 1) * GAME_SIZE);
                        int rect[] = {p00, p10, p11, p01};
                        int i = model.addSurface(4, rect, color, Model.INVISIBLE);
                        selectedX[i] = x;
                        selectedY[i] = y;
                        model.entityType[i] = 0x30d40 + i;
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
                                int p00 = model.insertCoordPointNoDuplicate(x * GAME_SIZE, -getGroundElevation(x, y), y * GAME_SIZE);
                                int p10 = model.insertCoordPointNoDuplicate((x + 1) * GAME_SIZE, -getGroundElevation(x + 1, y), y * GAME_SIZE);
                                int p11 = model.insertCoordPointNoDuplicate((x + 1) * GAME_SIZE, -getGroundElevation(x + 1, y + 1), (y + 1) * GAME_SIZE);
                                int p01 = model.insertCoordPointNoDuplicate(x * GAME_SIZE, -getGroundElevation(x, y + 1), (y + 1) * GAME_SIZE);
                                int rect[] = {p00, p10, p11, p01};
                                int j = model.addSurface(4, rect, color, Model.INVISIBLE);
                                selectedX[j] = x;
                                selectedY[j] = y;
                                model.entityType[j] = 0x30d40 + j;
                                drawOnMinimap(x, y, 0, color, color);
                            }
                    	}
                    }
                }
            }

            model.setLightAndGradAndSource(true, 40, 48, Camera.light_x, Camera.light_z, Camera.light_y);
            aModelArray596 = aModel.method182(
            		1536*SCALE_FACTOR, 1536*SCALE_FACTOR,
            		8, 64, 233, false);
            for (int j6 = 0; j6 < 64; j6++) {
                camera.addModel(aModelArray596[j6]); // floor tiles
            }
            for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH; x++)
            {
                for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT; y++)
                {
                	// objects; roofs, fences, walls etc but not trees and and stuff.
                    elevation[x][y] = getGroundElevation(x, y);
                }
            }
        }
        aModel.method176();
        int k1 = 0xc0c0c0;
        // minimap walls, fences
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++) {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++) {
                int k3 = getVerticalWall(x, y);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, x, y, x + 1, y);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[x][y] |= WALKABLE_0;
                        if (y > 0) {
                            orWalkable(x, y - 1, WALKABLE_2);
                        }
                    }
                    if (flag) {
                        gameImage.drawLineX(x * 3, y * 3, 3, k1);
                    }
                }
                k3 = getHorizontalWall(x, y);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, x, y, x, y + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[x][y] |= WALKABLE_1;
                        if (x > 0) {
                            orWalkable(x - 1, y, WALKABLE_3);
                        }
                    }
                    if (flag) {
                        gameImage.drawLineY(x * 3, y * 3, 3, k1);
                    }
                }
                k3 = getDiagonalWalls(x, y);
                if (k3 > 0 && k3 < 12000 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, x, y, x + 1, y + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[x][y] |= WALKABLE_5;
                    }
                    if (flag) {
                        gameImage.setMinimapPixel(x * 3, y * 3, k1);
                        gameImage.setMinimapPixel(x * 3 + 1, y * 3 + 1, k1);
                        gameImage.setMinimapPixel(x * 3 + 2, y * 3 + 2, k1);
                    }
                }
                if (k3 > 12000 && k3 < 24000 && EntityHandler.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    method421(aModel, k3 - 12001, x + 1, y, x, y + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 12001).getDoorType() != 0) {
                        walkableValue[x][y] |= WALKABLE_4;
                    }
                    if (flag) {
                        gameImage.setMinimapPixel(x * 3 + 2, y * 3, k1);
                        gameImage.setMinimapPixel(x * 3 + 1, y * 3 + 1, k1);
                        gameImage.setMinimapPixel(x * 3, y * 3 + 2, k1);
                    }
                }
            }
        }
        if (flag) {
            gameImage.storeSpriteHoriz(mudclient.SPRITE_MEDIA_START - 1, 0, 0, 285, 285);
        }
        // viewport walls, fences etc.
        aModel.setLightAndGradAndSource(false, 60, 24, Camera.light_x, Camera.light_z, Camera.light_y);
        walls[hSector] = aModel.method182(
        		1536*SCALE_FACTOR, 1536*SCALE_FACTOR,
        		8, 64, 338, true);
        for (int building = 0; building < 64; building++)
        {
            camera.addModel(walls[hSector][building]);
        }
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                int wallID = getVerticalWall(x, y);
                if (wallID > 0)
                    method403(wallID - 1, x, y, x + 1, y);
                wallID = getHorizontalWall(x, y);
                if (wallID > 0)
                    method403(wallID - 1, x, y, x, y + 1);
                wallID = getDiagonalWalls(x, y);
                if (wallID > 0 && wallID < 12000)
                    method403(wallID - 1, x, y, x + 1, y + 1);
                if (wallID > 12000 && wallID < 24000)
                    method403(wallID - 12001, x + 1, y, x, y + 1);
            }
        }
        for (int i = 1; i < VISIBLE_SECTORS*SECTOR_WIDTH-1; i++)
        {
            for (int j = 1; j < VISIBLE_SECTORS*SECTOR_HEIGHT-1; j++)
            {
                if (getRoofTexture(i, j) > 0)
                {
                	int[] x = {i, i+1, i+1, i};
                	int[] y = {j, j, j+1, j+1};
                    double[] z = {
                    		elevation[x[0]][y[0]],
                    		elevation[x[1]][y[1]],
                    		elevation[x[2]][y[2]],
                    		elevation[x[3]][y[3]]};
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
                        elevation[x[0]][y[0]] = zMax;
                    else
                        elevation[x[0]][y[0]] -= ROOF_LIM;
                    if (z[1] < ROOF_LIM)
                    	elevation[x[1]][y[1]] = zMax;
                    else
                        elevation[x[1]][y[1]] -= ROOF_LIM;
                    if (z[2] < ROOF_LIM)
                    	elevation[x[2]][y[2]] = zMax;
                    else
                        elevation[x[2]][y[2]] -= ROOF_LIM;
                    if (z[3] < ROOF_LIM)
                    	elevation[x[3]][y[3]] = zMax;
                    else
                        elevation[x[3]][y[3]] -= ROOF_LIM;
                }
            }
        }

        aModel.method176();
        for (int i = 1; i < VISIBLE_SECTORS*SECTOR_WIDTH-1; i++)
        {
            for (int j = 1; j < VISIBLE_SECTORS*SECTOR_HEIGHT-1; j++)
            {
                int color;
                if ((color = getRoofTexture(i, j)) > 0)
                {
                	int[] x = {i, i+1, i+1, i};
                	int[] y = {j, j, j+1, j+1};
                	double[] x_big = {i * GAME_SIZE, i * GAME_SIZE,
                			(i+1) * GAME_SIZE, (i+1) * GAME_SIZE};
                	double[] y_big = {j * GAME_SIZE, j * GAME_SIZE,
                			(j+1) * GAME_SIZE, (j+1) * GAME_SIZE};
                    double[] z = {
                    		elevation[x[0]][y[0]],
                    		elevation[x[1]][y[1]],
                    		elevation[x[2]][y[2]],
                    		elevation[x[3]][y[3]]};
                    double roofHeight = EntityHandler.getElevationDef(color - 1).getRoofHeight();
                    if (isNotEdge(x[0], y[0]) && z[0] < ROOF_LIM) {
                        z[0] += roofHeight + ROOF_LIM;
                        elevation[x[0]][y[0]] = z[0];
                    }
                    if (isNotEdge(x[1], y[1]) && z[1] < ROOF_LIM) {
                        z[1] += roofHeight + ROOF_LIM;
                        elevation[x[1]][y[1]] = z[1];
                    }
                    if (isNotEdge(x[2], y[2]) && z[2] < ROOF_LIM) {
                        z[2] += roofHeight + ROOF_LIM;
                        elevation[x[2]][y[2]] = z[2];
                    }
                    if (isNotEdge(x[3], y[3]) && z[3] < ROOF_LIM) {
                        z[3] += roofHeight + ROOF_LIM;
                        elevation[x[3]][y[3]] = z[3];
                    }
                    if (z[0] >= ROOF_LIM)
                        z[0] -= ROOF_LIM;
                    if (z[1] >= ROOF_LIM)
                        z[1] -= ROOF_LIM;
                    if (z[2] >= ROOF_LIM)
                        z[2] -= ROOF_LIM;
                    if (z[3] >= ROOF_LIM)
                        z[3] -= ROOF_LIM;
                    double roofStickOut = 16*SCALE_FACTOR; // how much the roof should stick out
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
                        rect[0] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (getDiagonalWalls(i, j) > 12000
                    		&& getDiagonalWalls(i, j) < 24000
                    		&& getRoofTexture(i + 1, j + 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (getDiagonalWalls(i, j) > 0
                    		&& getDiagonalWalls(i, j) < 12000
                    		&& getRoofTexture(i + 1, j - 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (getDiagonalWalls(i, j) > 0
                    		&& getDiagonalWalls(i, j) < 12000
                    		&& getRoofTexture(i - 1, j + 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    }
                    else if (z[0] == z[1] && z[2] == z[3]) {
                        int rect[] = new int[4];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                        rect[3] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                        aModel.addSurface(4, rect, color, Model.INVISIBLE);
                    }
                    else if (z[0] == z[3] && z[1] == z[2]) {
                        int rect[] = new int[4];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                        rect[3] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                        aModel.addSurface(4, rect, color, Model.INVISIBLE);
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
                            rect1[0] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                            rect1[1] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                            rect1[2] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                            aModel.addSurface(3, rect1, color, Model.INVISIBLE);
                            int rec2[] = new int[3];
                            rec2[0] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                            rec2[1] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                            rec2[2] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                            aModel.addSurface(3, rec2, color, Model.INVISIBLE);
                        }
                        else
                        {
                            int rect1[] = new int[3];
                            rect1[0] = aModel.insertCoordPointNoDuplicate(x_big[0], z[0], y_big[0]);
                            rect1[1] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                            rect1[2] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                            aModel.addSurface(3, rect1, color, Model.INVISIBLE);
                            int rect2[] = new int[3];
                            rect2[0] = aModel.insertCoordPointNoDuplicate(x_big[3], z[2], y_big[2]);
                            rect2[1] = aModel.insertCoordPointNoDuplicate(x_big[1], z[3], y_big[3]);
                            rect2[2] = aModel.insertCoordPointNoDuplicate(x_big[2], z[1], y_big[1]);
                            aModel.addSurface(3, rect2, color, Model.INVISIBLE);
                        }
                    }
                }
            }
        }
        aModel.setLightAndGradAndSource(true, 50, 50, Camera.light_x, Camera.light_z, Camera.light_y);
        roofs[hSector] = aModel.method182(
        		1536*SCALE_FACTOR, 1536*SCALE_FACTOR,
        		8, 64, 169, true);
        for (int l9 = 0; l9 < 64; l9++) {
            camera.addModel(roofs[hSector][l9]);
        }
        if (roofs[hSector][0] == null) {
            throw new RuntimeException("null roof!");
        }
        for (int j12 = 0; j12 < VISIBLE_SECTORS*SECTOR_WIDTH; j12++) {
            for (int k14 = 0; k14 < VISIBLE_SECTORS*SECTOR_HEIGHT; k14++) {
                if (elevation[j12][k14] >= ROOF_LIM) {
                    elevation[j12][k14] -= ROOF_LIM;
                }
            }
        }
    }

    public int getRoofTexture(int x, int y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile(x, y).roofTexture;
    }

    public void andMinusWalkable(int x, int y, int val)
    {
        walkableValue[x][y] &= 0xffff ^ val;
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

    /* Something to do with drawing the ground and roads/floors on
     * the minimap
     */
    public void drawOnMinimap(int tile_x, int tile_y, int k, int neColor16, int swColor16)
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

    public int getVerticalWall(int x, int y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return 0;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile(x, y).verticalWall & 0xff;
    }

    public boolean nearbyRoof(int x, int y)
    {
        return getRoofTexture(x, y) > 0
        		|| getRoofTexture(x - 1, y) > 0
        		|| getRoofTexture(x - 1, y - 1) > 0
        		|| getRoofTexture(x, y - 1) > 0;
    }

    public int getGroundTexturesOverlay(int x, int y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile(x, y).groundOverlay & 0xff;
    }

    public int getDiagonalWalls(int x, int y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile(x, y).diagonalWalls;
    }

    public void method421(Model model, int i, int xSector_1,
    		int ySector_1, int xSector_2, int ySector_2)
    {
        double height = EntityHandler.getDoorDef(i).getHeight();
        int texture1 = EntityHandler.getDoorDef(i).getTexture1();
        int texture2 = EntityHandler.getDoorDef(i).getTexture2();
        double x_1 = xSector_1 * GAME_SIZE;
        double y_1 = ySector_1 * GAME_SIZE;
        double x_2 = xSector_2 * GAME_SIZE;
        double y_2 = ySector_2 * GAME_SIZE;
        int p0 = model.insertCoordPointNoDuplicate(x_1, -elevation[xSector_1][ySector_1], y_1);
        int p1 = model.insertCoordPointNoDuplicate(x_1, -elevation[xSector_1][ySector_1] - height, y_1);
        int p2 = model.insertCoordPointNoDuplicate(x_2, -elevation[xSector_2][ySector_2] - height, y_2);
        int p3 = model.insertCoordPointNoDuplicate(x_2, -elevation[xSector_2][ySector_2], y_2);
        int i4 = model.addSurface(4, new int[]{p0, p1, p2, p3}, texture1, texture2);
        if (EntityHandler.getDoorDef(i).getUnknown() == 5) {
            model.entityType[i4] = 30000 + i;
        } else {
            model.entityType[i4] = 0;
        }
    }

    public int getOverlayIfRequired(int x, int y, int underlay)
    {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0)
            return underlay;
        return EntityHandler.getTileDef(texture - 1).getColour();
    }

    public int getGroundTexture(int x, int y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile(x, y).groundTexture & 0xFF;
    }

    public boolean isNotEdge(int i, int j)
    {
        return getRoofTexture(i, j) > 0
        		&& getRoofTexture(i - 1, j) > 0
        		&& getRoofTexture(i - 1, j - 1) > 0
        		&& getRoofTexture(i, j - 1) > 0;
    }

    public int getWalkableValue(int x, int y)
    {
        if (x < 0 || y < 0
        		|| x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y >= VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return 0;
        }
        return walkableValue[x][y];
    }

    public int getHorizontalWall(int x, int y)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return 0;
        }
        int section = 0;
        for (int j = 0; j < VISIBLE_SECTORS; ++j)
            for (int i = 0; i < VISIBLE_SECTORS; ++i)
            	if (x >= i*SECTOR_WIDTH && x < (i+1)*SECTOR_WIDTH
            			&& y >= j*SECTOR_HEIGHT && y < (j+1)*SECTOR_HEIGHT)
            	{
            		section = j*VISIBLE_SECTORS + i;
                    x -= i*SECTOR_WIDTH;
                    y -= j*SECTOR_HEIGHT;
                    break;
            	}
        return sectors[section].getTile(x, y).horizontalWall & 0xff;
    }

    public int method427(int x, int y)
    {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0)
            return -1;
        return EntityHandler.getTileDef(texture - 1).getTexture() != 2 ? 0 : 1;
    }

    public void method428(Model[] models)
    {
        for (int x = 0; x < VISIBLE_SECTORS*(SECTOR_WIDTH-1); x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*(SECTOR_HEIGHT-1); y++)
            {
                if (getDiagonalWalls(x, y) > 48000 && getDiagonalWalls(x, y) < 60000) {
                    int k = getDiagonalWalls(x, y) - 48001;
                    int l = objectDirs[x][y];
                    int i1;
                    int j1;
                    if (l == 0 || l == 4) {
                        i1 = EntityHandler.getObjectDef(k).getWidth();
                        j1 = EntityHandler.getObjectDef(k).getHeight();
                    } else {
                        j1 = EntityHandler.getObjectDef(k).getWidth();
                        i1 = EntityHandler.getObjectDef(k).getHeight();
                    }
                    method412(x, y, k, l);
                    Model model = models[EntityHandler.getObjectDef(k).modelID].newModel(false, true, false, false);
                    double k1 = ((x + x + i1) * GAME_SIZE) / 2;
                    double i2 = ((y + y + j1) * GAME_SIZE) / 2;
                    model.addTranslate(k1, -getAveragedElevation(k1, i2), i2);
                    model.setRotation(0, l * 32, 0);
                    camera.addModel(model);
                    model.setLightAndSource(SECTOR_WIDTH, SECTOR_HEIGHT, Camera.light_x, Camera.light_z, Camera.light_y);
                    if (i1 > 1 || j1 > 1) {
                        for (int k2 = x; k2 < x + i1; k2++) {
                            for (int l2 = y; l2 < y + j1; l2++) {
                                if ((k2 > x || l2 > y) && getDiagonalWalls(k2, l2) - 48001 == k) {
                                    int l1 = k2;
                                    int j2 = l2;
                                    int section = 0;
                                    for (int j = 0; j < VISIBLE_SECTORS; ++j)
                                        for (int i = 0; i < VISIBLE_SECTORS; ++i)
                                        	if (l1 >= i*SECTOR_WIDTH && l1 < (i+1)*SECTOR_WIDTH
                                        			&& j2 >= j*SECTOR_HEIGHT && j2 < (j+1)*SECTOR_HEIGHT)
                                        	{
                                        		section = j*VISIBLE_SECTORS + i;
                                        		l1 -= i*SECTOR_WIDTH;
                                        		j2 -= j*SECTOR_HEIGHT;
                                                break;
                                        	}
                                    sectors[section].getTile(l1, j2).diagonalWalls = 0;
                                }
                            }
                        }
                    }
                }
            }
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

    public EngineHandle(Camera camera, GameImage gameImage)
    {
        this.camera = camera;
        this.gameImage = gameImage;
        objectDirs = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        selectedX = new int[SECTOR_WIDTH*SECTOR_HEIGHT*2*VISIBLE_SECTORS*VISIBLE_SECTORS];
        selectedY = new int[SECTOR_WIDTH*SECTOR_HEIGHT*2*VISIBLE_SECTORS*VISIBLE_SECTORS];
        walls = new Model[4][64];
        roofs = new Model[4][64];
        elevation = new double[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        requiresClean = true;
        aModelArray596 = new Model[64];
        groundTextureArray = new int[256];
        walkableValue = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        playerIsAlive = false;
        sectors = new Sector[VISIBLE_SECTORS*VISIBLE_SECTORS];

        try {
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

    public void loadSection(int sectionX, int sectionY, int height, int sector)
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
    
    public boolean getTextureUse()
    {
    	return textures;
    }
    
    public void setTextureUse(boolean useTxtr)
    {
    	textures = useTxtr;
    }
    
    public static int getTileXFromX(double x)
    {
    	return (int) (x / GAME_SIZE);
    }
    
    public static int getTileYFromY(double y)
    {
    	return (int) (y / GAME_SIZE);
    }

    private ZipFile tileArchive;

    public int[][] objectDirs;
    public int[] selectedX;
    public int[] selectedY;
    public int[][] walkableValue;
    public static final double GAME_SIZE = 128;
    public static final double SCALE_FACTOR = GAME_SIZE/128;
    public static final double ROOF_LIM = 0x13880;
    public static final int WALKABLE_0 = 0x1;
    public static final int WALKABLE_1 = 0x2;
    public static final int WALKABLE_2 = 0x4;
    public static final int WALKABLE_3 = 0x8;
    public static final int WALKABLE_4 = 0x10;
    public static final int WALKABLE_5 = 0x20;
    public static final int WALKABLE_6 = 0x40;
    public static final int WALKABLE_INDOORS = 0x80;
    public boolean playerIsAlive;
    public Model aModel;
    private GameImage gameImage;
    private Camera camera;
    private boolean requiresClean;
    public static final int SECTOR_HEIGHT = 48;
    public static final int SECTOR_WIDTH = 48;
    public static final int VISIBLE_SECTORS = 2;
    private boolean textures = true;

    private Sector[] sectors;

    Model[][] walls;
    double[][] elevation;
    Model[] aModelArray596;
    private int[] groundTextureArray;
    Model[][] roofs;
}