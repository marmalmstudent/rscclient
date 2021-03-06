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

    public int getStepCount(int walkSectionX, int walkSectionY, int x1, int y1,
    		int x2, int y2, int[] walkSectionXArray, int[] walkSectionYArray, boolean flag)
    {
        int[][] tmpTiles = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
        for (int k1 = 0; k1 < VISIBLE_SECTORS*SECTOR_WIDTH; k1++)
            for (int l1 = 0; l1 < VISIBLE_SECTORS*SECTOR_HEIGHT; l1++)
                tmpTiles[k1][l1] = 0;

        int i2 = 0;
        int stepCount = 0;
        int x_smth = walkSectionX;
        int y_smth = walkSectionY;
        tmpTiles[walkSectionX][walkSectionY] = 99;
        walkSectionXArray[i2] = walkSectionX;
        walkSectionYArray[i2++] = walkSectionY;
        int i3 = walkSectionXArray.length;
        boolean pathAvailable = false;
        while (stepCount != i2)
        {
            x_smth = walkSectionXArray[stepCount];
            y_smth = walkSectionYArray[stepCount];
            stepCount = (stepCount + 1) % i3;
            if (x_smth >= x1 && x_smth <= x2 && y_smth >= y1 && y_smth <= y2)
            {
                pathAvailable = true;
                break;
            }
            if (flag)
            {
                if (x_smth > 0 && x_smth - 1 >= x1 && x_smth - 1 <= x2
                		&& y_smth >= y1 && y_smth <= y2
                		&& (walkableValue[x_smth - 1][y_smth] & 8) == 0)
                {
                    pathAvailable = true;
                    break;
                }
                if (x_smth < 95 && x_smth + 1 >= x1 && x_smth + 1 <= x2
                		&& y_smth >= y1 && y_smth <= y2
                		&& (walkableValue[x_smth + 1][y_smth] & 2) == 0)
                {
                    pathAvailable = true;
                    break;
                }
                if (y_smth > 0 && x_smth >= x1 && x_smth <= x2
                		&& y_smth - 1 >= y1 && y_smth - 1 <= y2
                		&& (walkableValue[x_smth][y_smth - 1] & 4) == 0)
                {
                    pathAvailable = true;
                    break;
                }
                if (y_smth < 95 && x_smth >= x1 && x_smth <= x2
                		&& y_smth + 1 >= y1 && y_smth + 1 <= y2
                		&& (walkableValue[x_smth][y_smth + 1] & 1) == 0)
                {
                    pathAvailable = true;
                    break;
                }
            }
            if (x_smth > 0 && tmpTiles[x_smth - 1][y_smth] == 0
            		&& (walkableValue[x_smth - 1][y_smth] & 0x78) == 0)
            {
                walkSectionXArray[i2] = x_smth - 1;
                walkSectionYArray[i2] = y_smth;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth - 1][y_smth] = 2;
            }
            if (x_smth < VISIBLE_SECTORS*SECTOR_WIDTH-1 && tmpTiles[x_smth + 1][y_smth] == 0
            		&& (walkableValue[x_smth + 1][y_smth] & 0x72) == 0)
            {
                walkSectionXArray[i2] = x_smth + 1;
                walkSectionYArray[i2] = y_smth;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth + 1][y_smth] = 8;
            }
            if (y_smth > 0 && tmpTiles[x_smth][y_smth - 1] == 0
            		&& (walkableValue[x_smth][y_smth - 1] & 0x74) == 0)
            {
                walkSectionXArray[i2] = x_smth;
                walkSectionYArray[i2] = y_smth - 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth][y_smth - 1] = 1;
            }
            if (y_smth < VISIBLE_SECTORS*SECTOR_HEIGHT-1 && tmpTiles[x_smth][y_smth + 1] == 0
            		&& (walkableValue[x_smth][y_smth + 1] & 0x71) == 0)
            {
                walkSectionXArray[i2] = x_smth;
                walkSectionYArray[i2] = y_smth + 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth][y_smth + 1] = 4;
            }
            if (x_smth > 0 && y_smth > 0 && (walkableValue[x_smth][y_smth - 1] & 0x74) == 0
            		&& (walkableValue[x_smth - 1][y_smth] & 0x78) == 0
            		&& (walkableValue[x_smth - 1][y_smth - 1] & 0x7c) == 0
            		&& tmpTiles[x_smth - 1][y_smth - 1] == 0)
            {
                walkSectionXArray[i2] = x_smth - 1;
                walkSectionYArray[i2] = y_smth - 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth - 1][y_smth - 1] = 3;
            }
            if (x_smth < VISIBLE_SECTORS*SECTOR_WIDTH-1 && y_smth > 0
            		&& (walkableValue[x_smth][y_smth - 1] & 0x74) == 0
            		&& (walkableValue[x_smth + 1][y_smth] & 0x72) == 0
            		&& (walkableValue[x_smth + 1][y_smth - 1] & 0x76) == 0
            		&& tmpTiles[x_smth + 1][y_smth - 1] == 0)
            {
                walkSectionXArray[i2] = x_smth + 1;
                walkSectionYArray[i2] = y_smth - 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth + 1][y_smth - 1] = 9;
            }
            if (x_smth > 0 && y_smth < VISIBLE_SECTORS*SECTOR_HEIGHT-1
            		&& (walkableValue[x_smth][y_smth + 1] & 0x71) == 0
            		&& (walkableValue[x_smth - 1][y_smth] & 0x78) == 0
            		&& (walkableValue[x_smth - 1][y_smth + 1] & 0x79) == 0
            		&& tmpTiles[x_smth - 1][y_smth + 1] == 0)
            {
                walkSectionXArray[i2] = x_smth - 1;
                walkSectionYArray[i2] = y_smth + 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth - 1][y_smth + 1] = 6;
            }
            if (x_smth < VISIBLE_SECTORS*SECTOR_WIDTH-1
            		&& y_smth < VISIBLE_SECTORS*SECTOR_HEIGHT-1
            		&& (walkableValue[x_smth][y_smth + 1] & 0x71) == 0
            		&& (walkableValue[x_smth + 1][y_smth] & 0x72) == 0
            		&& (walkableValue[x_smth + 1][y_smth + 1] & 0x73) == 0
            		&& tmpTiles[x_smth + 1][y_smth + 1] == 0)
            {
                walkSectionXArray[i2] = x_smth + 1;
                walkSectionYArray[i2] = y_smth + 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[x_smth + 1][y_smth + 1] = 12;
            }
        }
        if (!pathAvailable)
            return -1;
        stepCount = 0;
        walkSectionXArray[stepCount] = x_smth;
        walkSectionYArray[stepCount++] = y_smth;
        int k3;
        for (int j3 = k3 = tmpTiles[x_smth][y_smth]; x_smth != walkSectionX
        		|| y_smth != walkSectionY; j3 = tmpTiles[x_smth][y_smth])
        {
            if (j3 != k3) {
                k3 = j3;
                walkSectionXArray[stepCount] = x_smth;
                walkSectionYArray[stepCount++] = y_smth;
            }
            if ((j3 & 2) != 0) {
                x_smth++;
            } else if ((j3 & 8) != 0) {
                x_smth--;
            }
            if ((j3 & 1) != 0) {
                y_smth++;
            } else if ((j3 & 4) != 0) {
                y_smth--;
            }
        }
        return stepCount;
    }

    public int getGroundElevation(int x, int y)
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
        return (sectors[section].getTile(x, y).groundElevation & 0xff) * 3;
    }

    public void updateObject(int x, int y, int k, int l)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
            return;
        }
        if (EntityHandler.getObjectDef(k).getType() == 1
        		|| EntityHandler.getObjectDef(k).getType() == 2)
        {
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
                for (int l1 = y; l1 < y + j1; l1++) {
                    if (EntityHandler.getObjectDef(k).getType() == 1)
                        walkableValue[k1][l1] &= 0xffbf;
                    else if (l == 0) {
                        walkableValue[k1][l1] &= 0xfffd;
                        if (k1 > 0) {
                            andMinusWalkable(k1 - 1, l1, 8);
                        }
                    } else if (l == 2) {
                        walkableValue[k1][l1] &= 0xfffb;
                        if (l1 < 95) {
                            andMinusWalkable(k1, l1 + 1, 1);
                        }
                    } else if (l == 4) {
                        walkableValue[k1][l1] &= 0xfff7;
                        if (k1 < 95) {
                            andMinusWalkable(k1 + 1, l1, 2);
                        }
                    } else if (l == 6) {
                        walkableValue[k1][l1] &= 0xfffe;
                        if (l1 > 0) {
                            andMinusWalkable(k1, l1 - 1, 4);
                        }
                    }
                }
            }
            method407(x, y, i1, j1);
        }
    }

    public int getAveragedElevation(int xHighRes, int yHighRes)
    {
        int x = xHighRes >> 7;
        int y = yHighRes >> 7;
        int x_1 = xHighRes & 0x7f;
        int y_1 = yHighRes & 0x7f;
        if (x < 0 || y < 0
        		|| x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1)
            return 0;
        int h00;
        int h10;
        int h01;
        if (x_1 <= 128 - y_1)
        { // y >= x
            h00 = getGroundElevation(x, y);
            h10 = getGroundElevation(x + 1, y) - h00;
            h01 = getGroundElevation(x, y + 1) - h00;
        }
        else
        {
            h00 = getGroundElevation(x + 1, y + 1);
            h10 = getGroundElevation(x, y + 1) - h00;
            h01 = getGroundElevation(x + 1, y) - h00;
            x_1 = 128 - x_1;
            y_1 = 128 - y_1;
        }
        return h00 + (h10 * x_1) / 128 + (h01 * y_1) / 128;
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

    public void method402(int i, int j, int k, int l, int i1) {
        Model model = aModelArray596[i + j * 8];
        for (int j1 = 0; j1 < model.nbrCoordPoints; j1++) {
            if (model.xCoords[j1] == k * 128 && model.yCoords[j1] == l * 128) {
                model.method187(j1, i1);
                return;
            }
        }
    }

    public void method403(int i, int j, int k, int l, int i1) {
        int j1 = EntityHandler.getDoorDef(i).getHeight();
        if (elevation[j][k] < 0x13880) {
            elevation[j][k] += 0x13880 + j1;
        }
        if (elevation[l][i1] < 0x13880) {
            elevation[l][i1] += 0x13880 + j1;
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

    public void orWalkable(int i, int j, int k) {
        walkableValue[i][j] |= k;
    }

    public void garbageCollect() {
        if (requiresClean) {
            camera.cleanupModels();
        }
        for (int i = 0; i < 64; i++) {
            aModelArray596[i] = null;
            for (int j = 0; j < 4; j++) {
                aModelArrayArray580[j][i] = null;
            }
            for (int k = 0; k < 4; k++) {
                aModelArrayArray598[k][i] = null;
            }
        }
        System.gc();
    }

    public void method407(int x, int y, int k, int l)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return;
        }
        for (int i1 = x; i1 <= x + k; i1++)
        {
            for (int j1 = y; j1 <= y + l; j1++)
            {
                if ((getWalkableValue(i1, j1) & 0x63) != 0
                		|| (getWalkableValue(i1 - 1, j1) & 0x59) != 0
                		|| (getWalkableValue(i1, j1 - 1) & 0x56) != 0 
                		|| (getWalkableValue(i1 - 1, j1 - 1) & 0x6c) != 0)
                {
                    method419(i1, j1, 35);
                } else {
                    method419(i1, j1, 0);
                }
            }
        }
    }

    public void method408(int x, int y, int k, int l)
    {
        if (x < 0 || x >= VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= VISIBLE_SECTORS*SECTOR_HEIGHT-1)
            return;
        if (EntityHandler.getDoorDef(l).getDoorType() == 1) {
            if (k == 0) {
                walkableValue[x][y] |= 1;
                if (y > 0) {
                    orWalkable(x, y - 1, 4);
                }
            } else if (k == 1) {
                walkableValue[x][y] |= 2;
                if (x > 0) {
                    orWalkable(x - 1, y, 8);
                }
            } else if (k == 2) {
                walkableValue[x][y] |= 0x10;
            } else if (k == 3) {
                walkableValue[x][y] |= 0x20;
            }
            method407(x, y, 1, 1);
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
            gameImage.resetImagePixels();
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
                    int z = -getGroundElevation(x, y);
                    if (getGroundTexturesOverlay(x, y) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getUnknown() == 4)
                        z = 0;
                    if (getGroundTexturesOverlay(x - 1, y) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x - 1, y) - 1).getUnknown() == 4)
                        z = 0;
                    if (getGroundTexturesOverlay(x, y - 1) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y - 1) - 1).getUnknown() == 4)
                        z = 0;
                    if (getGroundTexturesOverlay(x - 1, y - 1) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x - 1, y - 1) - 1).getUnknown() == 4)
                        z = 0;
                    int j5 = model.insertCoordPointNoDuplicate(x * 128, z, y * 128);
                    int j7 = (int) (Math.random() * 10D) - 5;
                    model.method187(j5, j7);
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
                        int unknown = EntityHandler.getTileDef(tileOverlay - 1).getUnknown();
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
                            walkableValue[tile_x][tile_y] |= 0x40;
                        if (EntityHandler.getTileDef(tileOverlay - 1).getUnknown() == 2)
                            walkableValue[tile_x][tile_y] |= 0x80;
                    }
                    drawOnMinimap(tile_x, tile_y, l14, texture1, texture2);
                    int i17 = ((getGroundElevation(tile_x + 1, tile_y + 1)
                    		- getGroundElevation(tile_x + 1, tile_y))
                    		+ getGroundElevation(tile_x, tile_y + 1))
                    		- getGroundElevation(tile_x, tile_y);
                    // TODO: figure out if the 96 has to do with width or height
                    if (texture1 != texture2 || i17 != 0)
                    {
                        int surfacePoints1[] = new int[3];
                        int surfacePoints2[] = new int[3];
                        if (l14 == 0)
                        {
                            if (texture1 != Model.INVISIBLE)
                            {
                                surfacePoints1[0] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                                		+ VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints1[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints1[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                                int i = model.addSurface(3, surfacePoints1, Model.INVISIBLE, texture1);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
                            }
                            if (texture2 != Model.INVISIBLE) {
                                surfacePoints2[0] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                                surfacePoints2[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                                		+ VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                                surfacePoints2[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                                		+ VISIBLE_SECTORS*SECTOR_HEIGHT;
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
                                surfacePoints1[0] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                                surfacePoints1[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                                		+ VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                                surfacePoints1[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                int i = model.addSurface(3, surfacePoints1, Model.INVISIBLE, texture1);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
                            }
                            if (texture2 != Model.INVISIBLE)
                            {
                                surfacePoints2[0] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                                		+ VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints2[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                                surfacePoints2[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                                		+ VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                                int i = model.addSurface(3, surfacePoints2, Model.INVISIBLE, texture2);
                                selectedX[i] = tile_x;
                                selectedY[i] = tile_y;
                                model.entityType[i] = 0x30d40 + i;
                            }
                        }
                    }
                    else if (texture1 != Model.INVISIBLE)
                    {
                        int ai1[] = new int[4];
                        ai1[0] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                        		+ VISIBLE_SECTORS*SECTOR_HEIGHT;
                        ai1[1] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT;
                        ai1[2] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                        ai1[3] = tile_y + tile_x * VISIBLE_SECTORS*SECTOR_HEIGHT
                        		+ VISIBLE_SECTORS*SECTOR_HEIGHT + 1;
                        int l19 = model.addSurface(4, ai1, Model.INVISIBLE, texture1);
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
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getUnknown() == 4)
                    {
                        int color = EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getColour();
                        int p00 = model.insertCoordPointNoDuplicate(x * 128, -getGroundElevation(x, y), y * 128);
                        int p10 = model.insertCoordPointNoDuplicate((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                        int p11 = model.insertCoordPointNoDuplicate((x + 1) * 128, -getGroundElevation(x + 1, y + 1), (y + 1) * 128);
                        int p01 = model.insertCoordPointNoDuplicate(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
                        int rect[] = {p00, p10, p11, p01};
                        int i = model.addSurface(4, rect, color, Model.INVISIBLE);
                        selectedX[i] = x;
                        selectedY[i] = y;
                        model.entityType[i] = 0x30d40 + i;
                        drawOnMinimap(x, y, 0, color, color);
                    }
                    else if (getGroundTexturesOverlay(x, y) == 0
                    		|| EntityHandler.getTileDef(getGroundTexturesOverlay(x, y) - 1).getUnknown() != 3)
                    {
                    	int[] x_arr = {0, 0, 1, -1};
                    	int[] y_arr = {1, -1, 0, 0};
                    	for (int i = 0; i < 4; ++i)
                    	{
                    		if (getGroundTexturesOverlay(x + x_arr[i], y + y_arr[i]) > 0
                            		&& EntityHandler.getTileDef(getGroundTexturesOverlay(x + x_arr[i], y + y_arr[i]) - 1).getUnknown() == 4)
                            {
                                int color = EntityHandler.getTileDef(getGroundTexturesOverlay(x + x_arr[i], y + y_arr[i]) - 1).getColour();
                                int p00 = model.insertCoordPointNoDuplicate(x * 128, -getGroundElevation(x, y), y * 128);
                                int p10 = model.insertCoordPointNoDuplicate((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                                int p11 = model.insertCoordPointNoDuplicate((x + 1) * 128, -getGroundElevation(x + 1, y + 1), (y + 1) * 128);
                                int p01 = model.insertCoordPointNoDuplicate(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
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
            aModelArray596 = aModel.method182(0, 0, 1536, 1536, 8, 64, 233, false);
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
        // walls, fences
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++) {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++) {
                int k3 = getVerticalWall(x, y);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, x, y, x + 1, y);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[x][y] |= 1;
                        if (y > 0) {
                            orWalkable(x, y - 1, 4);
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
                        walkableValue[x][y] |= 2;
                        if (x > 0) {
                            orWalkable(x - 1, y, 8);
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
                        walkableValue[x][y] |= 0x20;
                    }
                    if (flag) {
                        gameImage.setMinimapPixel(x * 3, y * 3, k1);
                        gameImage.setMinimapPixel(x * 3*2 + 1, y * 3 + 1, k1);
                        gameImage.setMinimapPixel(x * 3*2 + 2, y * 3 + 2, k1);
                    }
                }
                if (k3 > 12000 && k3 < 24000 && EntityHandler.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    method421(aModel, k3 - 12001, x + 1, y, x, y + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 12001).getDoorType() != 0) {
                        walkableValue[x][y] |= 0x10;
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
        // walls, fences etc.
        aModel.setLightAndGradAndSource(false, 60, 24, Camera.light_x, Camera.light_z, Camera.light_y);
        aModelArrayArray580[hSector] = aModel.method182(0, 0, 1536, 1536, 8, 64, 338, true);
        for (int l2 = 0; l2 < 64; l2++) {
            camera.addModel(aModelArrayArray580[hSector][l2]);
        }
        for (int x = 0; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 0; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                int k6 = getVerticalWall(x, y);
                if (k6 > 0)
                    method403(k6 - 1, x, y, x + 1, y);
                k6 = getHorizontalWall(x, y);
                if (k6 > 0)
                    method403(k6 - 1, x, y, x, y + 1);
                k6 = getDiagonalWalls(x, y);
                if (k6 > 0 && k6 < 12000)
                    method403(k6 - 1, x, y, x + 1, y + 1);
                if (k6 > 12000 && k6 < 24000)
                    method403(k6 - 12001, x + 1, y, x, y + 1);
            }
        }
        for (int x = 1; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 1; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                int roofTxtr = getRoofTexture(x, y);
                if (roofTxtr > 0) {
                    int x00 = x;
                    int y00 = y;
                    int x10 = x + 1;
                    int y10 = y;
                    int x11 = x + 1;
                    int y11 = y + 1;
                    int x01 = x;
                    int y01 = y + 1;
                    int l23 = 0;
                    int j24 = elevation[x00][y00];
                    int l24 = elevation[x10][y10];
                    int j25 = elevation[x11][y11];
                    int l25 = elevation[x01][y01];
                    if (j24 > 0x13880)
                        j24 -= 0x13880;
                    if (l24 > 0x13880)
                        l24 -= 0x13880;
                    if (j25 > 0x13880)
                        j25 -= 0x13880;
                    if (l25 > 0x13880)
                        l25 -= 0x13880;
                    if (j24 > l23)
                        l23 = j24;
                    if (l24 > l23)
                        l23 = l24;
                    if (j25 > l23)
                        l23 = j25;
                    if (l25 > l23)
                        l23 = l25;
                    if (l23 >= 0x13880)
                        l23 -= 0x13880;
                    if (j24 < 0x13880)
                        elevation[x00][y00] = l23;
                    else
                        elevation[x00][y00] -= 0x13880;
                    if (l24 < 0x13880)
                        elevation[x10][y10] = l23;
                    else
                        elevation[x10][y10] -= 0x13880;
                    if (j25 < 0x13880)
                        elevation[x11][y11] = l23;
                    else
                        elevation[x11][y11] -= 0x13880;
                    if (l25 < 0x13880)
                        elevation[x01][y01] = l23;
                    else
                        elevation[x01][y01] -= 0x13880;
                }
            }
        }

        aModel.method176();
        for (int x = 1; x < VISIBLE_SECTORS*SECTOR_WIDTH-1; x++)
        {
            for (int y = 1; y < VISIBLE_SECTORS*SECTOR_HEIGHT-1; y++)
            {
                int color = getRoofTexture(x, y);
                if (color > 0) {
                    int x00 = x;
                    int y00 = y;
                    int x10 = x + 1;
                    int y10 = y;
                    int x11 = x + 1;
                    int y11 = y + 1;
                    int x01 = x;
                    int y01 = y + 1;
                    int x0_hres = x * 128;
                    int y0_hres = y * 128;
                    int x1_hres = x0_hres + 128;
                    int y1_hres = y0_hres + 128;
                    int x0_hres2 = x0_hres;
                    int y0_hres2 = y0_hres;
                    int x1_hres2 = x1_hres;
                    int y1_hres2 = y1_hres;
                    int z00 = elevation[x00][y00];
                    int z10 = elevation[x10][y10];
                    int z11 = elevation[x11][y11];
                    int z01 = elevation[x01][y01];
                    int unknown1 = EntityHandler.getElevationDef(color - 1).getUnknown1();
                    if (method424(x00, y00) && z00 < 0x13880) {
                        z00 += unknown1 + 0x13880;
                        elevation[x00][y00] = z00;
                    }
                    if (method424(x10, y10) && z10 < 0x13880) {
                        z10 += unknown1 + 0x13880;
                        elevation[x10][y10] = z10;
                    }
                    if (method424(x11, y11) && z11 < 0x13880) {
                        z11 += unknown1 + 0x13880;
                        elevation[x11][y11] = z11;
                    }
                    if (method424(x01, y01) && z01 < 0x13880) {
                        z01 += unknown1 + 0x13880;
                        elevation[x01][y01] = z01;
                    }
                    if (z00 >= 0x13880)
                        z00 -= 0x13880;
                    if (z10 >= 0x13880)
                        z10 -= 0x13880;
                    if (z11 >= 0x13880)
                        z11 -= 0x13880;
                    if (z01 >= 0x13880)
                        z01 -= 0x13880;
                    byte byte0 = 16;
                    if (!method416(x00 - 1, y00))
                        x0_hres -= byte0;
                    if (!method416(x00 + 1, y00))
                        x0_hres += byte0;
                    if (!method416(x00, y00 - 1))
                        y0_hres -= byte0;
                    if (!method416(x00, y00 + 1))
                        y0_hres += byte0;
                    if (!method416(x10 - 1, y10))
                        x1_hres -= byte0;
                    if (!method416(x10 + 1, y10))
                        x1_hres += byte0;
                    if (!method416(x10, y10 - 1))
                        y0_hres2 -= byte0;
                    if (!method416(x10, y10 + 1))
                        y0_hres2 += byte0;
                    if (!method416(x11 - 1, y11))
                        x1_hres2 -= byte0;
                    if (!method416(x11 + 1, y11))
                        x1_hres2 += byte0;
                    if (!method416(x11, y11 - 1))
                        y1_hres -= byte0;
                    if (!method416(x11, y11 + 1))
                        y1_hres += byte0;
                    if (!method416(x01 - 1, y01))
                        x0_hres2 -= byte0;
                    if (!method416(x01 + 1, y01))
                        x0_hres2 += byte0;
                    if (!method416(x01, y01 - 1))
                        y1_hres2 -= byte0;
                    if (!method416(x01, y01 + 1))
                        y1_hres2 += byte0;
                    color = EntityHandler.getElevationDef(color - 1).getUnknown2();
                    z00 = -z00;
                    z10 = -z10;
                    z11 = -z11;
                    z01 = -z01;
                    if (getDiagonalWalls(x, y) > 12000 && getDiagonalWalls(x, y) < 24000
                    		&& getRoofTexture(x - 1, y - 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    } else
                    if (getDiagonalWalls(x, y) > 12000 && getDiagonalWalls(x, y) < 24000
                    		&& getRoofTexture(x + 1, y + 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    } else
                    if (getDiagonalWalls(x, y) > 0 && getDiagonalWalls(x, y) < 12000
                    		&& getRoofTexture(x + 1, y - 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    } else
                    if (getDiagonalWalls(x, y) > 0 && getDiagonalWalls(x, y) < 12000
                    		&& getRoofTexture(x - 1, y + 1) == 0)
                    {
                        int rect[] = new int[3];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                        aModel.addSurface(3, rect, color, Model.INVISIBLE);
                    } else if (z00 == z10 && z11 == z01) {
                        int rect[] = new int[4];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                        rect[3] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                        aModel.addSurface(4, rect, color, Model.INVISIBLE);
                    } else if (z00 == z01 && z10 == z11) {
                        int rect[] = new int[4];
                        rect[0] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                        rect[1] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                        rect[2] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                        rect[3] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                        aModel.addSurface(4, rect, color, Model.INVISIBLE);
                    } else {
                        boolean flag1 = true;
                        if (getRoofTexture(x - 1, y - 1) > 0) {
                            flag1 = false;
                        }
                        if (getRoofTexture(x + 1, y + 1) > 0) {
                            flag1 = false;
                        }
                        if (!flag1) {
                            int rect1[] = new int[3];
                            rect1[0] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                            rect1[1] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                            rect1[2] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                            aModel.addSurface(3, rect1, color, Model.INVISIBLE);
                            int rec2[] = new int[3];
                            rec2[0] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                            rec2[1] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                            rec2[2] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                            aModel.addSurface(3, rec2, color, Model.INVISIBLE);
                        } else {
                            int rect1[] = new int[3];
                            rect1[0] = aModel.insertCoordPointNoDuplicate(x0_hres, z00, y0_hres);
                            rect1[1] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                            rect1[2] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                            aModel.addSurface(3, rect1, color, Model.INVISIBLE);
                            int rect2[] = new int[3];
                            rect2[0] = aModel.insertCoordPointNoDuplicate(x1_hres2, z11, y1_hres);
                            rect2[1] = aModel.insertCoordPointNoDuplicate(x0_hres2, z01, y1_hres2);
                            rect2[2] = aModel.insertCoordPointNoDuplicate(x1_hres, z10, y0_hres2);
                            aModel.addSurface(3, rect2, color, Model.INVISIBLE);
                        }
                    }
                }
            }
        }
        aModel.setLightAndGradAndSource(true, 50, 50, Camera.light_x, Camera.light_z, Camera.light_y);
        aModelArrayArray598[hSector] = aModel.method182(0, 0, 1536, 1536, 8, 64, 169, true);
        for (int l9 = 0; l9 < 64; l9++) {
            camera.addModel(aModelArrayArray598[hSector][l9]);
        }
        if (aModelArrayArray598[hSector][0] == null) {
            throw new RuntimeException("null roof!");
        }
        for (int j12 = 0; j12 < VISIBLE_SECTORS*SECTOR_WIDTH; j12++) {
            for (int k14 = 0; k14 < VISIBLE_SECTORS*SECTOR_HEIGHT; k14++) {
                if (elevation[j12][k14] >= 0x13880) {
                    elevation[j12][k14] -= 0x13880;
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

    public void andMinusWalkable(int i, int j, int k) {
        walkableValue[i][j] &= 65535 - k;
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
                        walkableValue[k1][l1] |= 0x40;
                    else if (l == 0) {
                        walkableValue[k1][l1] |= 2;
                        if (k1 > 0) {
                            orWalkable(k1 - 1, l1, 8);
                        }
                    } else if (l == 2) {
                        walkableValue[k1][l1] |= 4;
                        if (l1 < VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
                            orWalkable(k1, l1 + 1, 1);
                        }
                    } else if (l == 4) {
                        walkableValue[k1][l1] |= 8;
                        if (k1 < VISIBLE_SECTORS*SECTOR_WIDTH-1) {
                            orWalkable(k1 + 1, l1, 2);
                        }
                    } else if (l == 6) {
                        walkableValue[k1][l1] |= 1;
                        if (l1 > 0) {
                            orWalkable(k1, l1 - 1, 4);
                        }
                    }
            }
            method407(x, y, i1, j1);
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
                walkableValue[x][y] &= 0xfffe;
                if (y > 0)
                    andMinusWalkable(x, y - 1, 4);
            }
            else if (dir == 1)
            {
                walkableValue[x][y] &= 0xfffd;
                if (x > 0)
                    andMinusWalkable(x - 1, y, 8);
            }
            else if (dir == 2)
                walkableValue[x][y] &= 0xffef;
            else if (dir == 3)
                walkableValue[x][y] &= 0xffdf;
            method407(x, y, 1, 1);
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

    public boolean method416(int x, int y)
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

    public void method419(int i, int j, int k) {
        int l = i / 12;
        int i1 = j / 12;
        int j1 = (i - 1) / 12;
        int k1 = (j - 1) / 12;
        method402(l, i1, i, j, k);
        if (l != j1) {
            method402(j1, i1, i, j, k);
        }
        if (i1 != k1) {
            method402(l, k1, i, j, k);
        }
        if (l != j1 && i1 != k1) {
            method402(j1, k1, i, j, k);
        }
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
        method419(xSector_1, ySector_1, 40);
        method419(xSector_2, ySector_2, 40);
        int height = EntityHandler.getDoorDef(i).getHeight();
        int texture1 = EntityHandler.getDoorDef(i).getTexture1();
        int texture2 = EntityHandler.getDoorDef(i).getTexture2();
        int x_1 = xSector_1 * 128;
        int y_1 = ySector_1 * 128;
        int x_2 = xSector_2 * 128;
        int y_2 = ySector_2 * 128;
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

    public boolean method424(int i, int j)
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
        return EntityHandler.getTileDef(texture - 1).getUnknown() != 2 ? 0 : 1;
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
                    int k1 = ((x + x + i1) * 128) / 2;
                    int i2 = ((y + y + j1) * 128) / 2;
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
        aModelArrayArray580 = new Model[4][64];
        aModelArrayArray598 = new Model[4][64];
        elevation = new int[VISIBLE_SECTORS*SECTOR_WIDTH][VISIBLE_SECTORS*SECTOR_HEIGHT];
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

    private ZipFile tileArchive;

    public int[][] objectDirs;
    public int[] selectedX;
    public int[] selectedY;
    public int[][] walkableValue;
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

    Model[][] aModelArrayArray580;
    int[][] elevation;
    Model[] aModelArray596;
    private int[] groundTextureArray;
    Model[][] aModelArrayArray598;
}