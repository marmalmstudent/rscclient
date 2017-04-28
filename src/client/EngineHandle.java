package client;

import client.entityhandling.EntityHandler;
import client.model.Sector;
import client.util.Config;
import client.util.DataConversions;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EngineHandle {

    public int getStepCount(int walkSectionX, int walkSectionY, int x1, int y1,
    		int x2, int y2, int[] walkSectionXArray, int[] walkSectionYArray, boolean flag)
    {
        int[][] tmpTiles = new int[NBR_VISIBLE_SECTORS*SECTOR_WIDTH][NBR_VISIBLE_SECTORS*SECTOR_HEIGHT];
        for (int k1 = 0; k1 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH; k1++) {
            for (int l1 = 0; l1 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT; l1++) {
                tmpTiles[k1][l1] = 0;
            }
        }

        int i2 = 0;
        int j2 = 0;
        int k2 = walkSectionX;
        int l2 = walkSectionY;
        tmpTiles[walkSectionX][walkSectionY] = 99;
        walkSectionXArray[i2] = walkSectionX;
        walkSectionYArray[i2++] = walkSectionY;
        int i3 = walkSectionXArray.length;
        boolean flag1 = false;
        while (j2 != i2)
        {
            k2 = walkSectionXArray[j2];
            l2 = walkSectionYArray[j2];
            j2 = (j2 + 1) % i3;
            if (k2 >= x1 && k2 <= x2 && l2 >= y1 && l2 <= y2) {
                flag1 = true;
                break;
            }
            if (flag) {
                if (k2 > 0 && k2 - 1 >= x1 && k2 - 1 <= x2 && l2 >= y1 && l2 <= y2
                		&& (walkableValue[k2 - 1][l2] & 8) == 0)
                {
                    flag1 = true;
                    break;
                }
                if (k2 < 95 && k2 + 1 >= x1 && k2 + 1 <= x2 && l2 >= y1 && l2 <= y2
                		&& (walkableValue[k2 + 1][l2] & 2) == 0)
                {
                    flag1 = true;
                    break;
                }
                if (l2 > 0 && k2 >= x1 && k2 <= x2 && l2 - 1 >= y1 && l2 - 1 <= y2
                		&& (walkableValue[k2][l2 - 1] & 4) == 0)
                {
                    flag1 = true;
                    break;
                }
                if (l2 < 95 && k2 >= x1 && k2 <= x2 && l2 + 1 >= y1 && l2 + 1 <= y2
                		&& (walkableValue[k2][l2 + 1] & 1) == 0)
                {
                    flag1 = true;
                    break;
                }
            }
            if (k2 > 0 && tmpTiles[k2 - 1][l2] == 0
            		&& (walkableValue[k2 - 1][l2] & 0x78) == 0)
            {
                walkSectionXArray[i2] = k2 - 1;
                walkSectionYArray[i2] = l2;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2 - 1][l2] = 2;
            }
            if (k2 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1 && tmpTiles[k2 + 1][l2] == 0
            		&& (walkableValue[k2 + 1][l2] & 0x72) == 0)
            {
                walkSectionXArray[i2] = k2 + 1;
                walkSectionYArray[i2] = l2;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2 + 1][l2] = 8;
            }
            if (l2 > 0 && tmpTiles[k2][l2 - 1] == 0
            		&& (walkableValue[k2][l2 - 1] & 0x74) == 0)
            {
                walkSectionXArray[i2] = k2;
                walkSectionYArray[i2] = l2 - 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2][l2 - 1] = 1;
            }
            if (l2 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1 && tmpTiles[k2][l2 + 1] == 0
            		&& (walkableValue[k2][l2 + 1] & 0x71) == 0)
            {
                walkSectionXArray[i2] = k2;
                walkSectionYArray[i2] = l2 + 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2][l2 + 1] = 4;
            }
            if (k2 > 0 && l2 > 0 && (walkableValue[k2][l2 - 1] & 0x74) == 0
            		&& (walkableValue[k2 - 1][l2] & 0x78) == 0
            		&& (walkableValue[k2 - 1][l2 - 1] & 0x7c) == 0
            		&& tmpTiles[k2 - 1][l2 - 1] == 0)
            {
                walkSectionXArray[i2] = k2 - 1;
                walkSectionYArray[i2] = l2 - 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2 - 1][l2 - 1] = 3;
            }
            if (k2 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1 && l2 > 0
            		&& (walkableValue[k2][l2 - 1] & 0x74) == 0
            		&& (walkableValue[k2 + 1][l2] & 0x72) == 0
            		&& (walkableValue[k2 + 1][l2 - 1] & 0x76) == 0
            		&& tmpTiles[k2 + 1][l2 - 1] == 0)
            {
                walkSectionXArray[i2] = k2 + 1;
                walkSectionYArray[i2] = l2 - 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2 + 1][l2 - 1] = 9;
            }
            if (k2 > 0 && l2 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1
            		&& (walkableValue[k2][l2 + 1] & 0x71) == 0
            		&& (walkableValue[k2 - 1][l2] & 0x78) == 0
            		&& (walkableValue[k2 - 1][l2 + 1] & 0x79) == 0
            		&& tmpTiles[k2 - 1][l2 + 1] == 0)
            {
                walkSectionXArray[i2] = k2 - 1;
                walkSectionYArray[i2] = l2 + 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2 - 1][l2 + 1] = 6;
            }
            if (k2 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1
            		&& l2 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1
            		&& (walkableValue[k2][l2 + 1] & 0x71) == 0
            		&& (walkableValue[k2 + 1][l2] & 0x72) == 0
            		&& (walkableValue[k2 + 1][l2 + 1] & 0x73) == 0
            		&& tmpTiles[k2 + 1][l2 + 1] == 0)
            {
                walkSectionXArray[i2] = k2 + 1;
                walkSectionYArray[i2] = l2 + 1;
                i2 = (i2 + 1) % i3;
                tmpTiles[k2 + 1][l2 + 1] = 12;
            }
        }
        if (!flag1) {
            return -1;
        }
        j2 = 0;
        walkSectionXArray[j2] = k2;
        walkSectionYArray[j2++] = l2;
        int k3;
        for (int j3 = k3 = tmpTiles[k2][l2]; k2 != walkSectionX || l2 != walkSectionY; j3 = tmpTiles[k2][l2]) {
            if (j3 != k3) {
                k3 = j3;
                walkSectionXArray[j2] = k2;
                walkSectionYArray[j2++] = l2;
            }
            if ((j3 & 2) != 0) {
                k2++;
            } else if ((j3 & 8) != 0) {
                k2--;
            }
            if ((j3 & 1) != 0) {
                l2++;
            } else if ((j3 & 4) != 0) {
                l2--;
            }
        }
        return j2;
    }

    public int getGroundElevation(int x, int y)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        byte section = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            section = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            section = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            section = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        return (sectors[section].getTile(x, y).groundElevation & 0xff) * 3;
    }

    public void updateObject(int x, int y, int k, int l)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
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

    public int getAveragedElevation(int i, int j) {
        int k = i >> 7;
        int l = j >> 7;
        int i1 = i & 0x7f;
        int j1 = j & 0x7f;
        if (k < 0 || l < 0
        		|| k >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| l >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
            return 0;
        }
        int k1;
        int l1;
        int i2;
        if (i1 <= 128 - j1) {
            k1 = getGroundElevation(k, l);
            l1 = getGroundElevation(k + 1, l) - k1;
            i2 = getGroundElevation(k, l + 1) - k1;
        } else {
            k1 = getGroundElevation(k + 1, l + 1);
            l1 = getGroundElevation(k, l + 1) - k1;
            i2 = getGroundElevation(k + 1, l) - k1;
            i1 = 128 - i1;
            j1 = 128 - j1;
        }
        return k1 + (l1 * i1) / 128 + (i2 * j1) / 128;
    }

    public void method400()
    {
        for (int i = 0; i < NBR_VISIBLE_SECTORS*SECTOR_WIDTH; i++) {
            for (int j = 0; j < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT; j++) {
                if (getGroundTexturesOverlay(i, j) == 250) {
                    if (i == SECTOR_WIDTH-1
                    		&& getGroundTexturesOverlay(i + 1, j) != 250
                    		&& getGroundTexturesOverlay(i + 1, j) != 2)
                    {
                        setGroundTexturesOverlay(i, j, 9);
                    } else
                    if (j == SECTOR_HEIGHT-1
                    && getGroundTexturesOverlay(i, j + 1) != 250
                    && getGroundTexturesOverlay(i, j + 1) != 2)
                    {
                        setGroundTexturesOverlay(i, j, 9);
                    } else {
                        setGroundTexturesOverlay(i, j, 2);
                    }
                }
            }
        }
    }

    public void method401(int x, int y, int k) {
        garbageCollect();
        int nextXSector = (x + SECTOR_WIDTH/2) / SECTOR_WIDTH;
        int nextYSector = (y + SECTOR_HEIGHT/2) / SECTOR_HEIGHT;
        method409(x, y, k, true);
        if (k == 0) {
            method409(x, y, 1, false);
            method409(x, y, 2, false);
            loadSection(nextXSector - 1, nextYSector - 1, k, 0);
            loadSection(nextXSector, nextYSector - 1, k, 1);
            loadSection(nextXSector - 1, nextYSector, k, 2);
            loadSection(nextXSector, nextYSector, k, 3);
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
        int j1 = EntityHandler.getDoorDef(i).getModelVar1();
        if (anIntArrayArray581[j][k] < 0x13880) {
            anIntArrayArray581[j][k] += 0x13880 + j1;
        }
        if (anIntArrayArray581[l][i1] < 0x13880) {
            anIntArrayArray581[l][i1] += 0x13880 + j1;
        }
    }

    public void setGroundTexturesOverlay(int x, int y, int k)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return;
        }
        byte sector = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            sector = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        sectors[sector].getTile(x, y).groundOverlay = (byte) k;
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
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
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
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1)
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

    public void method409(int x, int y, int k, boolean flag)
    {
        int nextXSector = (x + SECTOR_WIDTH/2) / SECTOR_WIDTH;
        int nextYSectory = (y + SECTOR_HEIGHT/2) / SECTOR_HEIGHT;
        loadSection(nextXSector - 1, nextYSectory - 1, k, 0);
        loadSection(nextXSector, nextYSectory - 1, k, 1);
        loadSection(nextXSector - 1, nextYSectory, k, 2);
        loadSection(nextXSector, nextYSectory, k, 3);
        method400();
        if (aModel == null) {
            aModel = new Model(18688, 18688, true, true, false, false, true);
        }
        if (flag) {
            gameImage.resetImagePixels();
            /*
             * Reset all tiles to non-walkable
             */
            for (int xTile = 0; xTile < NBR_VISIBLE_SECTORS*SECTOR_WIDTH; xTile++)
            {
                for (int yTile = 0; yTile < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT; yTile++)
                {
                    walkableValue[xTile][yTile] = 0;
                }
            }

            Model model = aModel;
            model.method176();
            /*
             * Sets elevation.
             */
            for (int j2 = 0; j2 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH; j2++)
            {
                for (int i3 = 0; i3 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT; i3++)
                {
                    int i4 = -getGroundElevation(j2, i3);
                    if (getGroundTexturesOverlay(j2, i3) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(j2, i3) - 1).getUnknown() == 4)
                        i4 = 0;
                    if (getGroundTexturesOverlay(j2 - 1, i3) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(j2 - 1, i3) - 1).getUnknown() == 4)
                        i4 = 0;
                    if (getGroundTexturesOverlay(j2, i3 - 1) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(j2, i3 - 1) - 1).getUnknown() == 4)
                        i4 = 0;
                    if (getGroundTexturesOverlay(j2 - 1, i3 - 1) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(j2 - 1, i3 - 1) - 1).getUnknown() == 4)
                        i4 = 0;
                    int j5 = model.insertCoordPointNoDuplicate(j2 * 128, i4, i3 * 128);
                    int j7 = (int) (Math.random() * 10D) - 5;
                    model.method187(j5, j7);
                }
            }

            /*
             * Draws ground tiles that does not have overlays (i.e. not roads, house floors,
             * water etc.)
             */
            for (int tile_x = 0; tile_x < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1; tile_x++)
            {
                for (int tile_y = 0; tile_y < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1; tile_y++)
                {
                    int k5 = getGroundTexture(tile_x, tile_y);
                    int k7;
                    if (textures)
                    	k7 = 60 + k5; // use textures
                    else
                    	k7 = groundTextureArray[k5]; // do not use textures
                     
                    int i10 = k7;
                    int k12 = k7;
                    int l14 = 0;
                    if (k == 1 || k == 2)
                    {
                        k7 = 0xbc614e;
                        i10 = 0xbc614e;
                        k12 = 0xbc614e;
                    }
                    if (getGroundTexturesOverlay(tile_x, tile_y) > 0)
                    {  // Draws roads, water etc.
                        int l16 = getGroundTexturesOverlay(tile_x, tile_y);
                        int l5 = EntityHandler.getTileDef(l16 - 1).getUnknown();
                        int i19 = method427(tile_x, tile_y);
                        // 1 water, 3 wood, -16913 gray, 12345678 invisible,
                        // -1 black, -26426 light gray, -27685 red carpet
                        if (textures)
                        {
                        	if (l16 == 1)
                        		k7 = i10 = 56; // roads
                        	else
                        		k7 = i10 = EntityHandler.getTileDef(l16 - 1).getColour();
                        }
                        else
                    		k7 = i10 = EntityHandler.getTileDef(l16 - 1).getColour();
                        if (l5 == 4)
                        {
                        	// water
                            k7 = 1;
                            i10 = 1;
                            if (l16 == 12)
                            { // lava
                                k7 = 31;
                                i10 = 31;
                            }
                        }
                        if (l5 == 5)
                        {  // this part draws the road and water stuff.
                            if (getDiagonalWalls(tile_x, tile_y) > 0
                            		&& getDiagonalWalls(tile_x, tile_y) < 24000)
                                if (getOverlayIfRequired(tile_x - 1, tile_y, k12) != 0xbc614e
                                		&& getOverlayIfRequired(tile_x, tile_y - 1, k12) != 0xbc614e)
                                {
                                    k7 = getOverlayIfRequired(tile_x - 1, tile_y, k12);
                                    l14 = 0;
                                }
                                else if (getOverlayIfRequired(tile_x + 1, tile_y, k12) != 0xbc614e
                                		&& getOverlayIfRequired(tile_x, tile_y + 1, k12) != 0xbc614e)
                                {
                                    i10 = getOverlayIfRequired(tile_x + 1, tile_y, k12);
                                    l14 = 0;
                                }
                                else if (getOverlayIfRequired(tile_x + 1, tile_y, k12) != 0xbc614e
                                		&& getOverlayIfRequired(tile_x, tile_y - 1, k12) != 0xbc614e)
                                {
                                	i10 = getOverlayIfRequired(tile_x + 1, tile_y, k12);
                                    l14 = 1;
                                }
                                else if (getOverlayIfRequired(tile_x - 1, tile_y, k12) != 0xbc614e
                                		&& getOverlayIfRequired(tile_x, tile_y + 1, k12) != 0xbc614e)
                                {
                                	k7 = getOverlayIfRequired(tile_x - 1, tile_y, k12);
                                    l14 = 1;
                                }
                        }
                        else if (l5 != 2 || getDiagonalWalls(tile_x, tile_y) > 0
                        		&& getDiagonalWalls(tile_x, tile_y) < 24000)
                        {  // when a road/water tile and a non-road/water tile shares the same tile
                            if (method427(tile_x - 1, tile_y) != i19 && method427(tile_x, tile_y - 1) != i19)
                            {
                                k7 = k12;
                                l14 = 0;
                            }
                            else if (method427(tile_x + 1, tile_y) != i19
                            		&& method427(tile_x, tile_y + 1) != i19)
                            {
                                i10 = k12;
                                l14 = 0;
                            }
                            else if (method427(tile_x + 1, tile_y) != i19
                            		&& method427(tile_x, tile_y - 1) != i19)
                            {
                                i10 = k12;
                                l14 = 1;
                            }
                            else if (method427(tile_x - 1, tile_y) != i19
                            		&& method427(tile_x, tile_y + 1) != i19)
                            {
                                k7 = k12;
                                l14 = 1;
                            }
                        }
                        if (EntityHandler.getTileDef(l16 - 1).getObjectType() != 0)
                            walkableValue[tile_x][tile_y] |= 0x40;
                        if (EntityHandler.getTileDef(l16 - 1).getUnknown() == 2)
                            walkableValue[tile_x][tile_y] |= 0x80;
                    }
                    method413(tile_x, tile_y, l14, k7, i10);
                    int i17 = ((getGroundElevation(tile_x + 1, tile_y + 1)
                    		- getGroundElevation(tile_x + 1, tile_y))
                    		+ getGroundElevation(tile_x, tile_y + 1))
                    		- getGroundElevation(tile_x, tile_y);
                    // TODO: figure out if the 96 has to do with width or height
                    if (k7 != i10 || i17 != 0)
                    {
                        int ai[] = new int[3];
                        int ai7[] = new int[3];
                        if (l14 == 0)
                        {
                            if (k7 != 0xbc614e)
                            {
                                ai[0] = tile_y + tile_x * 96 + 96;
                                ai[1] = tile_y + tile_x * 96;
                                ai[2] = tile_y + tile_x * 96 + 1;
                                int l21 = model.method181(3, ai, 0xbc614e, k7);
                                selectedX[l21] = tile_x;
                                selectedY[l21] = tile_y;
                                model.entityType[l21] = 0x30d40 + l21;
                            }
                            if (i10 != 0xbc614e) {
                                ai7[0] = tile_y + tile_x * 96 + 1;
                                ai7[1] = tile_y + tile_x * 96 + 96 + 1;
                                ai7[2] = tile_y + tile_x * 96 + 96;
                                int i22 = model.method181(3, ai7, 0xbc614e, i10);
                                selectedX[i22] = tile_x;
                                selectedY[i22] = tile_y;
                                model.entityType[i22] = 0x30d40 + i22;
                            }
                        } else {
                            if (k7 != 0xbc614e) {
                                ai[0] = tile_y + tile_x * 96 + 1;
                                ai[1] = tile_y + tile_x * 96 + 96 + 1;
                                ai[2] = tile_y + tile_x * 96;
                                int j22 = model.method181(3, ai, 0xbc614e, k7);
                                selectedX[j22] = tile_x;
                                selectedY[j22] = tile_y;
                                model.entityType[j22] = 0x30d40 + j22;
                            }
                            if (i10 != 0xbc614e) {
                                ai7[0] = tile_y + tile_x * 96 + 96;
                                ai7[1] = tile_y + tile_x * 96;
                                ai7[2] = tile_y + tile_x * 96 + 96 + 1;
                                int k22 = model.method181(3, ai7, 0xbc614e, i10);
                                selectedX[k22] = tile_x;
                                selectedY[k22] = tile_y;
                                model.entityType[k22] = 0x30d40 + k22;
                            }
                        }
                    } else if (k7 != 0xbc614e) {
                        int ai1[] = new int[4];
                        ai1[0] = tile_y + tile_x * 96 + 96;
                        ai1[1] = tile_y + tile_x * 96;
                        ai1[2] = tile_y + tile_x * 96 + 1;
                        ai1[3] = tile_y + tile_x * 96 + 96 + 1;
                        int l19 = model.method181(4, ai1, 0xbc614e, k7);
                        selectedX[l19] = tile_x;
                        selectedY[l19] = tile_y;
                        model.entityType[l19] = 0x30d40 + l19;
                    }
                }

            }
            /* bridges it seems */
            for (int k4 = 1; k4 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1; k4++)
            {
                for (int i6 = 1; i6 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1; i6++)
                {
                    if (getGroundTexturesOverlay(k4, i6) > 0
                    		&& EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6) - 1).getUnknown() == 4)
                    {
                        int l7 = EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6) - 1).getColour();
                        int j10 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                        int l12 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                        int i15 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1), (i6 + 1) * 128);
                        int j17 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                        int ai2[] = {
                                j10, l12, i15, j17
                        };
                        int i20 = model.method181(4, ai2, l7, 0xbc614e);
                        selectedX[i20] = k4;
                        selectedY[i20] = i6;
                        model.entityType[i20] = 0x30d40 + i20;
                        method413(k4, i6, 0, l7, l7);
                    } else
                    if (getGroundTexturesOverlay(k4, i6) == 0
                    		|| EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6) - 1).getUnknown() != 3)
                    {
                        if (getGroundTexturesOverlay(k4, i6 + 1) > 0
                        		&& EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6 + 1) - 1).getUnknown() == 4)
                        {
                            int i8 = EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6 + 1) - 1).getColour();
                            int k10 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int i13 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int j15 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1), (i6 + 1) * 128);
                            int k17 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai3[] = {
                                    k10, i13, j15, k17
                            };
                            int j20 = model.method181(4, ai3, i8, 0xbc614e);
                            selectedX[j20] = k4;
                            selectedY[j20] = i6;
                            model.entityType[j20] = 0x30d40 + j20;
                            method413(k4, i6, 0, i8, i8);
                        }
                        if (getGroundTexturesOverlay(k4, i6 - 1) > 0
                        		&& EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6 - 1) - 1).getUnknown() == 4)
                        {
                            int j8 = EntityHandler.getTileDef(getGroundTexturesOverlay(k4, i6 - 1) - 1).getColour();
                            int l10 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int j13 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int k15 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1), (i6 + 1) * 128);
                            int l17 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai4[] = {
                                    l10, j13, k15, l17
                            };
                            int k20 = model.method181(4, ai4, j8, 0xbc614e);
                            selectedX[k20] = k4;
                            selectedY[k20] = i6;
                            model.entityType[k20] = 0x30d40 + k20;
                            method413(k4, i6, 0, j8, j8);
                        }
                        if (getGroundTexturesOverlay(k4 + 1, i6) > 0
                        		&& EntityHandler.getTileDef(getGroundTexturesOverlay(k4 + 1, i6) - 1).getUnknown() == 4)
                        {
                            int k8 = EntityHandler.getTileDef(getGroundTexturesOverlay(k4 + 1, i6) - 1).getColour();
                            int i11 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int k13 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int l15 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1), (i6 + 1) * 128);
                            int i18 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai5[] = {
                                    i11, k13, l15, i18
                            };
                            int l20 = model.method181(4, ai5, k8, 0xbc614e);
                            selectedX[l20] = k4;
                            selectedY[l20] = i6;
                            model.entityType[l20] = 0x30d40 + l20;
                            method413(k4, i6, 0, k8, k8);
                        }
                        if (getGroundTexturesOverlay(k4 - 1, i6) > 0
                        		&& EntityHandler.getTileDef(getGroundTexturesOverlay(k4 - 1, i6) - 1).getUnknown() == 4)
                        {
                            int l8 = EntityHandler.getTileDef(getGroundTexturesOverlay(k4 - 1, i6) - 1).getColour();
                            int j11 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int l13 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int i16 = model.insertCoordPointNoDuplicate((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1), (i6 + 1) * 128);
                            int j18 = model.insertCoordPointNoDuplicate(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai6[] = {
                                    j11, l13, i16, j18
                            };
                            int i21 = model.method181(4, ai6, l8, 0xbc614e);
                            selectedX[i21] = k4;
                            selectedY[i21] = i6;
                            model.entityType[i21] = 0x30d40 + i21;
                            method413(k4, i6, 0, l8, l8);
                        }
                    }
                }
            }

            model.method184(true, 40, 48, -50, -10, -50);
            aModelArray596 = aModel.method182(0, 0, 1536, 1536, 8, 64, 233, false);
            for (int j6 = 0; j6 < 64; j6++) {
                camera.addModel(aModelArray596[j6]); // floor tiles
            }
            for (int i9 = 0; i9 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH; i9++)
            {
                for (int k11 = 0; k11 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT; k11++)
                {
                	// objects; roofs, fences, walls etc but not trees and and stuff.
                    anIntArrayArray581[i9][k11] = getGroundElevation(i9, k11);
                }
            }
        }
        aModel.method176();
        int k1 = 0x606060;
        // walls, fences
        for (int i2 = 0; i2 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1; i2++) {
            for (int k2 = 0; k2 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1; k2++) {
                int k3 = getVerticalWall(i2, k2);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, i2, k2, i2 + 1, k2);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[i2][k2] |= 1;
                        if (k2 > 0) {
                            orWalkable(i2, k2 - 1, 4);
                        }
                    }
                    if (flag) {
                        gameImage.drawLineX(i2 * 3, k2 * 3, 3, k1);
                    }
                }
                k3 = getHorizontalWall(i2, k2);
                if (k3 > 0 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, i2, k2, i2, k2 + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[i2][k2] |= 2;
                        if (i2 > 0) {
                            orWalkable(i2 - 1, k2, 8);
                        }
                    }
                    if (flag) {
                        gameImage.drawLineY(i2 * 3, k2 * 3, 3, k1);
                    }
                }
                k3 = getDiagonalWalls(i2, k2);
                if (k3 > 0 && k3 < 12000 && EntityHandler.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, i2, k2, i2 + 1, k2 + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 1).getDoorType() != 0) {
                        walkableValue[i2][k2] |= 0x20;
                    }
                    if (flag) {
                        gameImage.setMinimapPixel(i2 * 3, k2 * 3, k1);
                        gameImage.setMinimapPixel(i2 * 3*2 + 1, k2 * 3 + 1, k1);
                        gameImage.setMinimapPixel(i2 * 3*2 + 2, k2 * 3 + 2, k1);
                    }
                }
                if (k3 > 12000 && k3 < 24000 && EntityHandler.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    method421(aModel, k3 - 12001, i2 + 1, k2, i2, k2 + 1);
                    if (flag && EntityHandler.getDoorDef(k3 - 12001).getDoorType() != 0) {
                        walkableValue[i2][k2] |= 0x10;
                    }
                    if (flag) {
                        gameImage.setMinimapPixel(i2 * 3 + 2, k2 * 3, k1);
                        gameImage.setMinimapPixel(i2 * 3 + 1, k2 * 3 + 1, k1);
                        gameImage.setMinimapPixel(i2 * 3, k2 * 3 + 2, k1);
                    }
                }
            }
        }
        if (flag) {
            gameImage.storeSpriteHoriz(mudclient.SPRITE_MEDIA_START - 1, 0, 0, 285, 285);
        }
        // walls, fences etc.
        aModel.method184(false, 60, 24, -50, -10, -50);
        aModelArrayArray580[k] = aModel.method182(0, 0, 1536, 1536, 8, 64, 338, true);
        for (int l2 = 0; l2 < 64; l2++) {
            camera.addModel(aModelArrayArray580[k][l2]);
        }
        for (int l3 = 0; l3 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1; l3++) {
            for (int l4 = 0; l4 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1; l4++) {
                int k6 = getVerticalWall(l3, l4);
                if (k6 > 0)
                    method403(k6 - 1, l3, l4, l3 + 1, l4);
                k6 = getHorizontalWall(l3, l4);
                if (k6 > 0)
                    method403(k6 - 1, l3, l4, l3, l4 + 1);
                k6 = getDiagonalWalls(l3, l4);
                if (k6 > 0 && k6 < 12000)
                    method403(k6 - 1, l3, l4, l3 + 1, l4 + 1);
                if (k6 > 12000 && k6 < 24000)
                    method403(k6 - 12001, l3 + 1, l4, l3, l4 + 1);
            }
        }
        for (int i5 = 1; i5 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1; i5++) {
            for (int l6 = 1; l6 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1; l6++) {
                int j9 = getRoofTexture(i5, l6);
                if (j9 > 0) {
                    int l11 = i5;
                    int i14 = l6;
                    int j16 = i5 + 1;
                    int k18 = l6;
                    int j19 = i5 + 1;
                    int j21 = l6 + 1;
                    int l22 = i5;
                    int j23 = l6 + 1;
                    int l23 = 0;
                    int j24 = anIntArrayArray581[l11][i14];
                    int l24 = anIntArrayArray581[j16][k18];
                    int j25 = anIntArrayArray581[j19][j21];
                    int l25 = anIntArrayArray581[l22][j23];
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
                        anIntArrayArray581[l11][i14] = l23;
                    else
                        anIntArrayArray581[l11][i14] -= 0x13880;
                    if (l24 < 0x13880)
                        anIntArrayArray581[j16][k18] = l23;
                    else
                        anIntArrayArray581[j16][k18] -= 0x13880;
                    if (j25 < 0x13880)
                        anIntArrayArray581[j19][j21] = l23;
                    else
                        anIntArrayArray581[j19][j21] -= 0x13880;
                    if (l25 < 0x13880)
                        anIntArrayArray581[l22][j23] = l23;
                    else
                        anIntArrayArray581[l22][j23] -= 0x13880;
                }
            }
        }

        aModel.method176();
        for (int i7 = 1; i7 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1; i7++) {
            for (int k9 = 1; k9 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1; k9++) {
                int i12 = getRoofTexture(i7, k9);
                if (i12 > 0) {
                    int j14 = i7;
                    int k16 = k9;
                    int l18 = i7 + 1;
                    int k19 = k9;
                    int k21 = i7 + 1;
                    int i23 = k9 + 1;
                    int k23 = i7;
                    int i24 = k9 + 1;
                    int k24 = i7 * 128;
                    int i25 = k9 * 128;
                    int k25 = k24 + 128;
                    int i26 = i25 + 128;
                    int j26 = k24;
                    int k26 = i25;
                    int l26 = k25;
                    int i27 = i26;
                    int j27 = anIntArrayArray581[j14][k16];
                    int k27 = anIntArrayArray581[l18][k19];
                    int l27 = anIntArrayArray581[k21][i23];
                    int i28 = anIntArrayArray581[k23][i24];
                    int j28 = EntityHandler.getElevationDef(i12 - 1).getUnknown1();
                    if (method424(j14, k16) && j27 < 0x13880) {
                        j27 += j28 + 0x13880;
                        anIntArrayArray581[j14][k16] = j27;
                    }
                    if (method424(l18, k19) && k27 < 0x13880) {
                        k27 += j28 + 0x13880;
                        anIntArrayArray581[l18][k19] = k27;
                    }
                    if (method424(k21, i23) && l27 < 0x13880) {
                        l27 += j28 + 0x13880;
                        anIntArrayArray581[k21][i23] = l27;
                    }
                    if (method424(k23, i24) && i28 < 0x13880) {
                        i28 += j28 + 0x13880;
                        anIntArrayArray581[k23][i24] = i28;
                    }
                    if (j27 >= 0x13880)
                        j27 -= 0x13880;
                    if (k27 >= 0x13880)
                        k27 -= 0x13880;
                    if (l27 >= 0x13880)
                        l27 -= 0x13880;
                    if (i28 >= 0x13880)
                        i28 -= 0x13880;
                    byte byte0 = 16;
                    if (!method416(j14 - 1, k16))
                        k24 -= byte0;
                    if (!method416(j14 + 1, k16))
                        k24 += byte0;
                    if (!method416(j14, k16 - 1))
                        i25 -= byte0;
                    if (!method416(j14, k16 + 1))
                        i25 += byte0;
                    if (!method416(l18 - 1, k19))
                        k25 -= byte0;
                    if (!method416(l18 + 1, k19))
                        k25 += byte0;
                    if (!method416(l18, k19 - 1))
                        k26 -= byte0;
                    if (!method416(l18, k19 + 1))
                        k26 += byte0;
                    if (!method416(k21 - 1, i23))
                        l26 -= byte0;
                    if (!method416(k21 + 1, i23))
                        l26 += byte0;
                    if (!method416(k21, i23 - 1))
                        i26 -= byte0;
                    if (!method416(k21, i23 + 1))
                        i26 += byte0;
                    if (!method416(k23 - 1, i24))
                        j26 -= byte0;
                    if (!method416(k23 + 1, i24))
                        j26 += byte0;
                    if (!method416(k23, i24 - 1))
                        i27 -= byte0;
                    if (!method416(k23, i24 + 1))
                        i27 += byte0;
                    i12 = EntityHandler.getElevationDef(i12 - 1).getUnknown2();
                    j27 = -j27;
                    k27 = -k27;
                    l27 = -l27;
                    i28 = -i28;
                    if (getDiagonalWalls(i7, k9) > 12000 && getDiagonalWalls(i7, k9) < 24000
                    		&& getRoofTexture(i7 - 1, k9 - 1) == 0)
                    {
                        int ai8[] = new int[3];
                        ai8[0] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                        ai8[1] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                        ai8[2] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                        aModel.method181(3, ai8, i12, 0xbc614e);
                    } else
                    if (getDiagonalWalls(i7, k9) > 12000 && getDiagonalWalls(i7, k9) < 24000
                    		&& getRoofTexture(i7 + 1, k9 + 1) == 0)
                    {
                        int ai9[] = new int[3];
                        ai9[0] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                        ai9[1] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                        ai9[2] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                        aModel.method181(3, ai9, i12, 0xbc614e);
                    } else
                    if (getDiagonalWalls(i7, k9) > 0 && getDiagonalWalls(i7, k9) < 12000
                    		&& getRoofTexture(i7 + 1, k9 - 1) == 0)
                    {
                        int ai10[] = new int[3];
                        ai10[0] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                        ai10[1] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                        ai10[2] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                        aModel.method181(3, ai10, i12, 0xbc614e);
                    } else
                    if (getDiagonalWalls(i7, k9) > 0 && getDiagonalWalls(i7, k9) < 12000
                    		&& getRoofTexture(i7 - 1, k9 + 1) == 0)
                    {
                        int ai11[] = new int[3];
                        ai11[0] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                        ai11[1] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                        ai11[2] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                        aModel.method181(3, ai11, i12, 0xbc614e);
                    } else if (j27 == k27 && l27 == i28) {
                        int ai12[] = new int[4];
                        ai12[0] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                        ai12[1] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                        ai12[2] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                        ai12[3] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                        aModel.method181(4, ai12, i12, 0xbc614e);
                    } else if (j27 == i28 && k27 == l27) {
                        int ai13[] = new int[4];
                        ai13[0] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                        ai13[1] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                        ai13[2] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                        ai13[3] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                        aModel.method181(4, ai13, i12, 0xbc614e);
                    } else {
                        boolean flag1 = true;
                        if (getRoofTexture(i7 - 1, k9 - 1) > 0) {
                            flag1 = false;
                        }
                        if (getRoofTexture(i7 + 1, k9 + 1) > 0) {
                            flag1 = false;
                        }
                        if (!flag1) {
                            int ai14[] = new int[3];
                            ai14[0] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                            ai14[1] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                            ai14[2] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                            aModel.method181(3, ai14, i12, 0xbc614e);
                            int ai16[] = new int[3];
                            ai16[0] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                            ai16[1] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                            ai16[2] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                            aModel.method181(3, ai16, i12, 0xbc614e);
                        } else {
                            int ai15[] = new int[3];
                            ai15[0] = aModel.insertCoordPointNoDuplicate(k24, j27, i25);
                            ai15[1] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                            ai15[2] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                            aModel.method181(3, ai15, i12, 0xbc614e);
                            int ai17[] = new int[3];
                            ai17[0] = aModel.insertCoordPointNoDuplicate(l26, l27, i26);
                            ai17[1] = aModel.insertCoordPointNoDuplicate(j26, i28, i27);
                            ai17[2] = aModel.insertCoordPointNoDuplicate(k25, k27, k26);
                            aModel.method181(3, ai17, i12, 0xbc614e);
                        }
                    }
                }
            }
        }
        aModel.method184(true, 50, 50, -50, -10, -50);
        aModelArrayArray598[k] = aModel.method182(0, 0, 1536, 1536, 8, 64, 169, true);
        for (int l9 = 0; l9 < 64; l9++) {
            camera.addModel(aModelArrayArray598[k][l9]);
        }
        if (aModelArrayArray598[k][0] == null) {
            throw new RuntimeException("null roof!");
        }
        for (int j12 = 0; j12 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH; j12++) {
            for (int k14 = 0; k14 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT; k14++) {
                if (anIntArrayArray581[j12][k14] >= 0x13880) {
                    anIntArrayArray581[j12][k14] -= 0x13880;
                }
            }
        }
    }

    public int getRoofTexture(int x, int y)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        byte byte0 = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            byte0 = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            byte0 = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            byte0 = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        return sectors[byte0].getTile(x, y).roofTexture;
    }

    public void andMinusWalkable(int i, int j, int k) {
        walkableValue[i][j] &= 65535 - k;
    }

    public void method412(int x, int y, int k, int l)
    {
        if (x < 0 || y < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
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
                        if (l1 < NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
                            orWalkable(k1, l1 + 1, 1);
                        }
                    } else if (l == 4) {
                        walkableValue[k1][l1] |= 8;
                        if (k1 < NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1) {
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
    public void method413(int i, int j, int k, int neColor16, int swColor16) {
        int j1 = i * 3;
        int k1 = j * 3;
        int neColor24 = camera.color16bitTo24bit(neColor16);
        int swColor24 = camera.color16bitTo24bit(swColor16);
        /* 0x7f7f7f = 0b011111110111111101111111 */
        /* So this removes the most significant bit */
        neColor24 = neColor24 >> 1 & 0x7f7f7f;
        swColor24 = swColor24 >> 1 & 0x7f7f7f;
        if (k == 0) {
        	// one tile is 3x3
            gameImage.drawLineX(j1, k1, 3, neColor24);
            gameImage.drawLineX(j1, k1 + 1, 2, neColor24);
            gameImage.drawLineX(j1, k1 + 2, 1, neColor24);
            gameImage.drawLineX(j1 + 2, k1 + 1, 1, swColor24);
            gameImage.drawLineX(j1 + 1, k1 + 2, 2, swColor24);
        } else if (k == 1) {
            gameImage.drawLineX(j1, k1, 3, swColor24);
            gameImage.drawLineX(j1 + 1, k1 + 1, 2, swColor24);
            gameImage.drawLineX(j1 + 2, k1 + 2, 1, swColor24);
            gameImage.drawLineX(j1, k1 + 1, 1, neColor24);
            gameImage.drawLineX(j1, k1 + 2, 2, neColor24);
        }
    }

    public void updateDoor(int x, int y, int dir, int type)
    {
        if (x < 0 || y < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH-1
        		|| y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT-1) {
            return;
        }
        if (EntityHandler.getDoorDef(type).getDoorType() == 1) {
            if (dir == 0) {
                walkableValue[x][y] &= 0xfffe;
                if (y > 0) {
                    andMinusWalkable(x, y - 1, 4);
                }
            } else if (dir == 1) {
                walkableValue[x][y] &= 0xfffd;
                if (x > 0) {
                    andMinusWalkable(x - 1, y, 8);
                }
            } else if (dir == 2) {
                walkableValue[x][y] &= 0xffef;
            } else if (dir == 3) {
                walkableValue[x][y] &= 0xffdf;
            }
            method407(x, y, 1, 1);
        }
    }

    public int getVerticalWall(int x, int y)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return 0;
        }
        byte byte0 = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            byte0 = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            byte0 = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            byte0 = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        return sectors[byte0].getTile(x, y).verticalWall & 0xff;
    }

    public boolean method416(int i, int j) {
        return getRoofTexture(i, j) > 0
        		|| getRoofTexture(i - 1, j) > 0
        		|| getRoofTexture(i - 1, j - 1) > 0
        		|| getRoofTexture(i, j - 1) > 0;
    }

    public int getGroundTexturesOverlay(int x, int y)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        byte sector = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            sector = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        return sectors[sector].getTile(x, y).groundOverlay & 0xff;
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
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        byte byte0 = 0;
        if (x >= SECTOR_WIDTH && y < 48) {
            byte0 = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= 48) {
            byte0 = 2;
            y -= SECTOR_WIDTH;
        } else if (x >= SECTOR_WIDTH && y >= 48) {
            byte0 = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_WIDTH;
        }
        return sectors[byte0].getTile(x, y).diagonalWalls;
    }

    public void method421(Model model, int i, int j, int k, int l, int i1) {
        method419(j, k, 40);
        method419(l, i1, 40);
        int j1 = EntityHandler.getDoorDef(i).getModelVar1();
        int k1 = EntityHandler.getDoorDef(i).getModelVar2();
        int l1 = EntityHandler.getDoorDef(i).getModelVar3();
        int i2 = j * 128;
        int j2 = k * 128;
        int k2 = l * 128;
        int l2 = i1 * 128;
        int i3 = model.insertCoordPointNoDuplicate(i2, -anIntArrayArray581[j][k], j2);
        int j3 = model.insertCoordPointNoDuplicate(i2, -anIntArrayArray581[j][k] - j1, j2);
        int k3 = model.insertCoordPointNoDuplicate(k2, -anIntArrayArray581[l][i1] - j1, l2);
        int l3 = model.insertCoordPointNoDuplicate(k2, -anIntArrayArray581[l][i1], l2);
        int i4 = model.method181(4, new int[]{i3, j3, k3, l3}, k1, l1);
        if (EntityHandler.getDoorDef(i).getUnknown() == 5) {
            model.entityType[i4] = 30000 + i;
        } else {
            model.entityType[i4] = 0;
        }
    }

    public int getOverlayIfRequired(int x, int y, int underlay) {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0) {
            return underlay;
        }
        return EntityHandler.getTileDef(texture - 1).getColour();
    }

    public int getGroundTexture(int x, int y)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT) {
            return 0;
        }
        byte sector = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            sector = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        return sectors[sector].getTile(x, y).groundTexture & 0xFF;
    }

    public boolean method424(int i, int j) {
        return getRoofTexture(i, j) > 0
        		&& getRoofTexture(i - 1, j) > 0
        		&& getRoofTexture(i - 1, j - 1) > 0
        		&& getRoofTexture(i, j - 1) > 0;
    }

    public int getWalkableValue(int x, int y)
    {
        if (x < 0 || y < 0
        		|| x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return 0;
        }
        return walkableValue[x][y];
    }

    public int getHorizontalWall(int x, int y)
    {
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return 0;
        }
        byte sector = 0;
        if (x >= SECTOR_WIDTH && y < SECTOR_HEIGHT) {
            sector = 1;
            x -= SECTOR_WIDTH;
        } else if (x < SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 2;
            y -= SECTOR_HEIGHT;
        } else if (x >= SECTOR_WIDTH && y >= SECTOR_HEIGHT) {
            sector = 3;
            x -= SECTOR_WIDTH;
            y -= SECTOR_HEIGHT;
        }
        return sectors[sector].getTile(x, y).horizontalWall & 0xff;
    }

    public int method427(int x, int y) {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0) {
            return -1;
        }
        return EntityHandler.getTileDef(texture - 1).getUnknown() != 2 ? 0 : 1;
    }

    public void method428(Model[] models)
    {
        for (int x = 0; x < NBR_VISIBLE_SECTORS*(SECTOR_WIDTH-1); x++)
        {
            for (int y = 0; y < NBR_VISIBLE_SECTORS*(SECTOR_HEIGHT-1); y++)
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
                    Model model = models[EntityHandler.getObjectDef(k).modelID].method204(false, true, false, false);
                    int k1 = ((x + x + i1) * 128) / 2;
                    int i2 = ((y + y + j1) * 128) / 2;
                    model.method190(k1, -getAveragedElevation(k1, i2), i2);
                    model.method189(0, l * 32, 0);
                    camera.addModel(model);
                    model.method185(SECTOR_WIDTH, SECTOR_HEIGHT, -50, -10, -50);
                    if (i1 > 1 || j1 > 1) {
                        for (int k2 = x; k2 < x + i1; k2++) {
                            for (int l2 = y; l2 < y + j1; l2++) {
                                if ((k2 > x || l2 > y) && getDiagonalWalls(k2, l2) - 48001 == k) {
                                    int l1 = k2;
                                    int j2 = l2;
                                    byte byte0 = 0;
                                    if (l1 >= SECTOR_WIDTH && j2 < SECTOR_HEIGHT) {
                                        byte0 = 1;
                                        l1 -= SECTOR_WIDTH;
                                    } else if (l1 < SECTOR_WIDTH && j2 >= SECTOR_HEIGHT) {
                                        byte0 = 2;
                                        j2 -= SECTOR_HEIGHT;
                                    } else if (l1 >= SECTOR_WIDTH && j2 >= SECTOR_HEIGHT) {
                                        byte0 = 3;
                                        l1 -= SECTOR_WIDTH;
                                        j2 -= SECTOR_HEIGHT;
                                    }
                                    sectors[byte0].getTile(l1, j2).diagonalWalls = 0;
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
        if (x < 0 || x >= NBR_VISIBLE_SECTORS*SECTOR_WIDTH
        		|| y < 0 || y >= NBR_VISIBLE_SECTORS*SECTOR_HEIGHT)
        {
            return;
        }
        objectDirs[x][y] = dir;
    }

    public EngineHandle(Camera camera, GameImage gameImage)
    {
        this.camera = camera;
        this.gameImage = gameImage;
        objectDirs = new int[NBR_VISIBLE_SECTORS*SECTOR_WIDTH][NBR_VISIBLE_SECTORS*SECTOR_HEIGHT];
        selectedX = new int[18432];
        selectedY = new int[18432];
        aModelArrayArray580 = new Model[4][64];
        aModelArrayArray598 = new Model[4][64];
        anIntArrayArray581 = new int[NBR_VISIBLE_SECTORS*SECTOR_WIDTH][NBR_VISIBLE_SECTORS*SECTOR_HEIGHT];
        requiresClean = true;
        aModelArray596 = new Model[64];
        groundTextureArray = new int[256];
        walkableValue = new int[NBR_VISIBLE_SECTORS*SECTOR_WIDTH][NBR_VISIBLE_SECTORS*SECTOR_HEIGHT];
        playerIsAlive = false;
        sectors = new Sector[NBR_VISIBLE_SECTORS*NBR_VISIBLE_SECTORS];

        try {
            tileArchive = new ZipFile(new File(Config.DATABASE_DIR + "/Landscape.zip"));
            //tileArchive = new ZipFile(new File(Config.CONF_DIR + "/Landscape.rscd"));
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

    public void loadSection(int sectionX, int sectionY, int height, int sector) {
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
    public static final int NBR_VISIBLE_SECTORS = 2;
    private boolean textures = false;

    private Sector[] sectors;

    Model[][] aModelArrayArray580;
    int[][] anIntArrayArray581;
    Model[] aModelArray596;
    private int[] groundTextureArray;
    Model[][] aModelArrayArray598;
}