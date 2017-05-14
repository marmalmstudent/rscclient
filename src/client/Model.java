package client;

import java.io.DataInputStream;
import java.io.IOException;

import client.util.misc;
import entityhandling.EntityHandler;

public class Model
{

	public Model(int i, int j) {
		anInt246 = 1;
		visible = true;
		aBoolean254 = true;
		transparentTexture = false;
		transparent = false;
		anInt257 = -1;
		aBoolean260 = false;
		aBoolean261 = false;
		aBoolean262 = false;
		aBoolean263 = false;
		aBoolean264 = false;
		anInt270 = 0xbc614e;
		longestLength = 0xbc614e;
		lightSourceX = 180;
		lightSourceZ = 155;
		lightSourceY = 95;
		lightSourceDist = 256;
		featuresLight = 512;
		globalLight = 32;
		initArrays(i, j);
		anIntArrayArray279 = new int[j][1];
		for (int k = 0; k < j; k++)
			anIntArrayArray279[k][0] = k;
		
	}

	public Model(int i, int j, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
		anInt246 = 1;
		visible = true;
		aBoolean254 = true;
		transparentTexture = false;
		transparent = false;
		anInt257 = -1;
		anInt270 = 0xbc614e;
		longestLength = 0xbc614e;
		lightSourceX = 180;
		lightSourceZ = 155;
		lightSourceY = 95;
		lightSourceDist = 256;
		featuresLight = 512;
		globalLight = 32;
		aBoolean260 = flag;
		aBoolean261 = flag1;
		aBoolean262 = flag2;
		aBoolean263 = flag3;
		aBoolean264 = flag4;
		initArrays(i, j);
	}

	private void initArrays(int nbrCoordPoints, int nbrSides)
	{
		xCoords = new int[nbrCoordPoints];
		zCoords = new int[nbrCoordPoints];
		yCoords = new int[nbrCoordPoints];
		pointBrightness = new int[nbrCoordPoints];
		aByteArray233 = new byte[nbrCoordPoints];
		pointsPerCell = new int[nbrSides];
		surfaces = new int[nbrSides][];
		surfaceTexture1 = new int[nbrSides];
		surfaceTexture2 = new int[nbrSides];
		lightSourceProjectToSurfNormal = new int[nbrSides];
		scaleFactor = new int[nbrSides];
		normalLength = new int[nbrSides];
		if (!aBoolean264) {
			xDistToPointFromCamera = new int[nbrCoordPoints];
			zDistToPointFromCamera = new int[nbrCoordPoints];
			yDistToPointFromCamera = new int[nbrCoordPoints];
			xCoordOnScreen = new int[nbrCoordPoints];
			yCoordOnScreen = new int[nbrCoordPoints];
		}
		if (!aBoolean263) {
			aByteArray259 = new byte[nbrSides];
			entityType = new int[nbrSides];
		}
		if (aBoolean260) {
			xCoordsDraw = xCoords;
			zCoordsDraw = zCoords;
			yCoordsDraw = yCoords;
		} else {
			xCoordsDraw = new int[nbrCoordPoints];
			zCoordsDraw = new int[nbrCoordPoints];
			yCoordsDraw = new int[nbrCoordPoints];
		}
		if (!aBoolean262 || !aBoolean261) {
			xNormals = new int[nbrSides];
			zNormals = new int[nbrSides];
			yNormals = new int[nbrSides];
		}
		if (!aBoolean261) {
			xMinArray = new int[nbrSides];
			xMaxArray = new int[nbrSides];
			zMinArray = new int[nbrSides];
			zMaxArray = new int[nbrSides];
			yMinArray = new int[nbrSides];
			yMaxArray = new int[nbrSides];
		}
		this.nbrSurfaces = 0;
		this.nbrCoordPoints = 0;
		nPoints = nbrCoordPoints;
		nSides = nbrSides;
		translateX = translateZ = translateY = 0;
		rotateX = rotateZ = rotateY = 0;
		scaleX = scaleZ = scaleY = 256;
		xSkew_z = ySkew_z = xSkew_y = zSkew_y = ySkew_x = zSkew_x = 256;
		actions = 0;
	}

	public void method175() {
		xDistToPointFromCamera = new int[nbrCoordPoints];
		zDistToPointFromCamera = new int[nbrCoordPoints];
		yDistToPointFromCamera = new int[nbrCoordPoints];
		xCoordOnScreen = new int[nbrCoordPoints];
		yCoordOnScreen = new int[nbrCoordPoints];
	}

	public void method176() {
		nbrSurfaces = 0;
		nbrCoordPoints = 0;
	}

	public void method177(int i, int j) {
		nbrSurfaces -= i;
		if (nbrSurfaces < 0)
			nbrSurfaces = 0;
		nbrCoordPoints -= j;
		if (nbrCoordPoints < 0)
			nbrCoordPoints = 0;
	}

	public Model(byte database[], int offset, boolean flag) {
		anInt246 = 1;
		visible = true;
		aBoolean254 = true;
		transparentTexture = false;
		transparent = false;
		anInt257 = -1;
		aBoolean260 = false;
		aBoolean261 = false;
		aBoolean262 = false;
		aBoolean263 = false;
		aBoolean264 = false;
		anInt270 = 0xbc614e;
		longestLength = 0xbc614e;
		lightSourceX = 180;
		lightSourceZ = 155;
		lightSourceY = 95;
		lightSourceDist = 256;
		featuresLight = 512;
		globalLight = 32;
		int nbrCoordPoints = DataOperations.getUnsigned2Bytes(database, offset);
		offset += 2;
		int nbrSurfaces = DataOperations.getUnsigned2Bytes(database, offset);
		offset += 2;
		initArrays(nbrCoordPoints, nbrSurfaces);
		anIntArrayArray279 = new int[nbrSurfaces][1];
		
		
		// The spatial coordinate points.
		for (int i = 0; i < nbrCoordPoints; i++)
		{
			xCoords[i] = DataOperations.getSigned2Bytes(database, offset);
			offset += 2;
		}
		for (int i = 0; i < nbrCoordPoints; i++)
		{
			zCoords[i] = DataOperations.getSigned2Bytes(database, offset);
			offset += 2;
		}
		for (int i = 0; i < nbrCoordPoints; i++)
		{
			yCoords[i] = DataOperations.getSigned2Bytes(database, offset);
			offset += 2;
		}
		
		/* how many data points should be used to create a surface
		 * (which can be colored or have texture on it).
		 */
		this.nbrCoordPoints = nbrCoordPoints;
		for (int i = 0; i < nbrSurfaces; i++)
		{
			pointsPerCell[i] = database[offset++] & 0xff;
		}
		
		/* 16-bit colors without alpha mask.
		 * Positive numbers (>= 0x0, <= 0xffff) seems to mean the texture index
		 * (e.g. 0 is sprite 3220)
		 * - red bit mask is   0b0111110000000000 (0x7c00)
		 * - green bit mask is 0b0000001111100000 (0x3e0)
		 * - blue bit mask is  0b0000000000011111 (0x1f)
		 * 32767 (0x7fff) denotes invisible (i think)
		 */
		for (int i = 0; i < nbrSurfaces; i++)
		{ // bottom/inside/lower i think
			surfaceTexture1[i] = DataOperations.getSigned2Bytes(database, offset);
			offset += 2;
			if (surfaceTexture1[i] == 32767)
				surfaceTexture1[i] = anInt270;
		}
		for (int i = 0; i < nbrSurfaces; i++)
		{ // top/outside/upper i think
			surfaceTexture2[i] = DataOperations.getSigned2Bytes(database, offset);
			offset += 2;
			if (surfaceTexture2[i] == 32767)
				surfaceTexture2[i] = anInt270;
		}
		
		/* Does not seem to affect anything. Most surfaces
		 * will have k2 != 0. and i can not find any pattern
		 * for those that does not.
		 */
		for (int i = 0; i < nbrSurfaces; i++)
		{
			int k2 = database[offset++] & 0xff;
			if (k2 == 0)
				lightSourceProjectToSurfNormal[i] = 0;
			else
				lightSourceProjectToSurfNormal[i] = anInt270;
		}
		
		/* store which data points are used to create a surface
		 * for each surface.
		 */
		for (int i = 0; i < nbrSurfaces; i++) {
			surfaces[i] = new int[pointsPerCell[i]];
			for (int j = 0; j < pointsPerCell[i]; j++)
				if (nbrCoordPoints < 256) {
					surfaces[i][j] = database[offset++] & 0xff;
				} else {
					surfaces[i][j] = DataOperations.getUnsigned2Bytes(database, offset);
					offset += 2;
				}
			
		}
		this.nbrSurfaces = nbrSurfaces;
		anInt246 = 1;
	}

    public Model(String path) {
        anInt246 = 1;
        visible = true;
        aBoolean254 = true;
        transparentTexture = false;
        transparent = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        longestLength = 0xbc614e;
        lightSourceX = 180;
        lightSourceZ = 155;
        lightSourceY = 95;
        lightSourceDist = 256;
        featuresLight = 512;
        globalLight = 32;
        byte abyte0[] = null;
        try {
            java.io.InputStream inputstream = DataOperations.streamFromPath(path);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            abyte0 = new byte[3];
            anInt309 = 0;
            for (int i = 0; i < 3; i += datainputstream.read(abyte0, i, 3 - i))
                ;
            int k = method206(abyte0);
            abyte0 = new byte[k];
            anInt309 = 0;
            for (int j = 0; j < k; j += datainputstream.read(abyte0, j, k - j))
                ;
            datainputstream.close();
        }
        catch (IOException _ex) {
            nbrCoordPoints = 0;
            nbrSurfaces = 0;
            return;
        }
        int l = method206(abyte0);
        int i1 = method206(abyte0);
        initArrays(l, i1);
        anIntArrayArray279 = new int[i1][];
        for (int j3 = 0; j3 < l; j3++) {
            int j1 = method206(abyte0);
            int k1 = method206(abyte0);
            int l1 = method206(abyte0);
            insertCoordPointNoDuplicate(j1, k1, l1);
        }

        for (int k3 = 0; k3 < i1; k3++) {
            int i2 = method206(abyte0);
            int j2 = method206(abyte0);
            int k2 = method206(abyte0);
            int l2 = method206(abyte0);
            featuresLight = method206(abyte0);
            globalLight = method206(abyte0);
            int i3 = method206(abyte0);
            int ai[] = new int[i2];
            for (int l3 = 0; l3 < i2; l3++)
                ai[l3] = method206(abyte0);

            int ai1[] = new int[l2];
            for (int i4 = 0; i4 < l2; i4++)
                ai1[i4] = method206(abyte0);

            int j4 = method181(i2, ai, j2, k2);
            anIntArrayArray279[k3] = ai1;
            if (i3 == 0)
                lightSourceProjectToSurfNormal[j4] = 0;
            else
                lightSourceProjectToSurfNormal[j4] = anInt270;
        }

        anInt246 = 1;
    }

    public Model(Model model[], int i, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        anInt246 = 1;
        visible = true;
        aBoolean254 = true;
        transparentTexture = false;
        transparent = false;
        anInt257 = -1;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        longestLength = 0xbc614e;
        lightSourceX = 180;
        lightSourceZ = 155;
        lightSourceY = 95;
        lightSourceDist = 256;
        featuresLight = 512;
        globalLight = 32;
        aBoolean260 = flag;
        aBoolean261 = flag1;
        aBoolean262 = flag2;
        aBoolean263 = flag3;
        method178(model, i, false);
    }

    public Model(Model models[], int i) {
        anInt246 = 1;
        visible = true;
        aBoolean254 = true;
        transparentTexture = false;
        transparent = false;
        anInt257 = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        aBoolean263 = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        longestLength = 0xbc614e;
        lightSourceX = 180;
        lightSourceZ = 155;
        lightSourceY = 95;
        lightSourceDist = 256;
        featuresLight = 512;
        globalLight = 32;
        method178(models, i, true);
    }

    public void method178(Model models[], int nModels, boolean flag)
    {
        int totNbrSurfaces = 0;
        int totNbrPoints = 0;
        for (int i = 0; i < nModels; i++) {
            totNbrSurfaces += models[i].nbrSurfaces;
            totNbrPoints += models[i].nbrCoordPoints;
        }

        initArrays(totNbrPoints, totNbrSurfaces);
        if (flag)
            anIntArrayArray279 = new int[totNbrSurfaces][];
        for (int i = 0; i < nModels; i++)
        {
            Model model = models[i];
            model.method202();
            globalLight = model.globalLight;
            featuresLight = model.featuresLight;
            lightSourceX = model.lightSourceX;
            lightSourceZ = model.lightSourceZ;
            lightSourceY = model.lightSourceY;
            lightSourceDist = model.lightSourceDist;
            for (int j = 0; j < model.nbrSurfaces; j++)
            {
                int cellPoints[] = new int[model.pointsPerCell[j]];
                int surface[] = model.surfaces[j];
                for (int k = 0; k < model.pointsPerCell[j]; k++)
                    cellPoints[k] = insertCoordPointNoDuplicate(
                    		model.xCoords[surface[k]],
                    		model.zCoords[surface[k]],
                    		model.yCoords[surface[k]]);

                int l1 = method181(model.pointsPerCell[j],
                		cellPoints, model.surfaceTexture1[j],
                		model.surfaceTexture2[j]);
                lightSourceProjectToSurfNormal[l1] = model.lightSourceProjectToSurfNormal[j];
                scaleFactor[l1] = model.scaleFactor[j];
                normalLength[l1] = model.normalLength[j];
                if (flag)
                    if (nModels > 1) {
                        anIntArrayArray279[l1] = new int[model.anIntArrayArray279[j].length + 1];
                        anIntArrayArray279[l1][0] = i;
                        for (int i2 = 0; i2 < model.anIntArrayArray279[j].length; i2++)
                            anIntArrayArray279[l1][i2 + 1] = model.anIntArrayArray279[j][i2];

                    } else {
                        anIntArrayArray279[l1] = new int[model.anIntArrayArray279[j].length];
                        for (int j2 = 0; j2 < model.anIntArrayArray279[j].length; j2++)
                            anIntArrayArray279[l1][j2] = model.anIntArrayArray279[j][j2];

                    }
            }

        }

        anInt246 = 1;
    }

    /**
     * Inserts a new coordinate point and returns the index of the point.
     * If the point already exists it will instead return the intex of that point.
     * @param x
     * @param z
     * @param y
     * @return
     */
    public int insertCoordPointNoDuplicate(int x, int z, int y)
    {
        for (int i = 0; i < nbrCoordPoints; i++)
            if (xCoords[i] == x && zCoords[i] == z && yCoords[i] == y)
                return i;

        if (nbrCoordPoints >= nPoints)
            return -1;
        xCoords[nbrCoordPoints] = x;
        zCoords[nbrCoordPoints] = z;
        yCoords[nbrCoordPoints] = y;
        return nbrCoordPoints++;
    }

    public int insertCoordPoint(int x, int z, int y)
    {
        if (nbrCoordPoints >= nPoints)
            return -1;
        xCoords[nbrCoordPoints] = x;
        zCoords[nbrCoordPoints] = z;
        yCoords[nbrCoordPoints] = y;
        return nbrCoordPoints++;
    }

    public int method181(int pointsInCell, int surfacePoints[], int texture1, int texture2) {
        if (nbrSurfaces >= nSides)
            return -1;
        pointsPerCell[nbrSurfaces] = pointsInCell;
        surfaces[nbrSurfaces] = surfacePoints;
        surfaceTexture1[nbrSurfaces] = texture1;
        surfaceTexture2[nbrSurfaces] = texture2;
        anInt246 = 1;
        return nbrSurfaces++;
    }

    public Model[] method182(int i, int j, int k, int l, int i1, int j1, int k1,
                             boolean flag) {
        method202();
        int ai[] = new int[j1];
        int ai1[] = new int[j1];
        for (int l1 = 0; l1 < j1; l1++) {
            ai[l1] = 0;
            ai1[l1] = 0;
        }

        for (int i2 = 0; i2 < nbrSurfaces; i2++) {
            int j2 = 0;
            int k2 = 0;
            int i3 = pointsPerCell[i2];
            int ai2[] = surfaces[i2];
            for (int i4 = 0; i4 < i3; i4++) {
                j2 += xCoords[ai2[i4]];
                k2 += yCoords[ai2[i4]];
            }

            int k4 = j2 / (i3 * k) + (k2 / (i3 * l)) * i1;
            ai[k4] += i3;
            ai1[k4]++;
        }

        Model models[] = new Model[j1];
        for (int l2 = 0; l2 < j1; l2++) {
            if (ai[l2] > k1)
                ai[l2] = k1;
            models[l2] = new Model(ai[l2], ai1[l2], true, true, true, flag, true);
            models[l2].featuresLight = featuresLight;
            models[l2].globalLight = globalLight;
        }

        for (int j3 = 0; j3 < nbrSurfaces; j3++) {
            int k3 = 0;
            int j4 = 0;
            int l4 = pointsPerCell[j3];
            int ai3[] = surfaces[j3];
            for (int i5 = 0; i5 < l4; i5++) {
                k3 += xCoords[ai3[i5]];
                j4 += yCoords[ai3[i5]];
            }

            int j5 = k3 / (l4 * k) + (j4 / (l4 * l)) * i1;
            method183(models[j5], ai3, l4, j3);
        }

        for (int l3 = 0; l3 < j1; l3++)
            models[l3].method175();

        return models;
    }

    public void method183(Model model, int ai[], int i, int j) {
        int ai1[] = new int[i];
        for (int k = 0; k < i; k++) {
            int l = ai1[k] = model.insertCoordPointNoDuplicate(
            		xCoords[ai[k]], zCoords[ai[k]], yCoords[ai[k]]);
            model.pointBrightness[l] = pointBrightness[ai[k]];
            model.aByteArray233[l] = aByteArray233[ai[k]];
        }

        int i1 = model.method181(i, ai1, surfaceTexture1[j], surfaceTexture2[j]);
        if (!model.aBoolean263 && !aBoolean263)
            model.entityType[i1] = entityType[j];
        model.lightSourceProjectToSurfNormal[i1] = lightSourceProjectToSurfNormal[j];
        model.scaleFactor[i1] = scaleFactor[j];
        model.normalLength[i1] = normalLength[j];
    }

    public void setLightAndGradAndSource(
    		boolean shadeGradient, int globalBright, int featureBright,
    		int xSrc, int zSrc, int ySrc)
    {
    	setLightning(globalBright, featureBright);
        if (aBoolean262)
            return;
        setShadowGradient(shadeGradient);
        setLightsource(xSrc, zSrc, ySrc);
    }

    public void setLightAndSource(int i, int j, int x, int z, int y)
    {
    	setLightning(i, j);
        setLightsource(x, z, y);
    }

    public void setLightning(int i, int j)
    {
        //globalLight = 256 - i * 4;
        globalLight = 0; // makes the world much brighter
        featuresLight = (64 - j) * 16 + 128;
    }

    public void setShadowGradient(boolean shadeGradient)
    {
        if (aBoolean262)
            return;
        if (shadeGradient)
        	for (int i = 0; i < nbrSurfaces;
        			lightSourceProjectToSurfNormal[i++] = anInt270);
        else
        	for (int i = 0; i < nbrSurfaces;
        			lightSourceProjectToSurfNormal[i++] = 0);
    }

    public void setLightsource(int x, int z, int y) {
        if (aBoolean262)
            return;
        lightSourceX = x;
        lightSourceZ = z;
        lightSourceY = y;
        lightSourceDist = (int) Math.sqrt(x * x + z * z + y * y);
        setLightining();
    }

    public void method187(int i, int j)
    {
        aByteArray233[i] = (byte) j;
    }

    public void addRotation(int x, int z, int y)
    {
    	setRotation(rotateX + x, rotateZ + z, rotateY + y);
    }

    public void setRotation(int x, int z, int y)
    {
        rotateX = x & 0xff;
        rotateZ = z & 0xff;
        rotateY = y & 0xff;
        checkActions();
        anInt246 = 1; // 1 for 3d model
    }

    public void addTranslate(int x, int z, int y)
    {
    	setTranslate(translateX + x, translateZ + z, translateY + y);
    }

    public void setTranslate(int x, int z, int y)
    {
        translateX = x;
        translateZ = z;
        translateY = y;
        checkActions();
        anInt246 = 1;
    }

    private void checkActions()
    {
        if (xSkew_z != 256 || ySkew_z != 256
        		|| xSkew_y != 256 || zSkew_y != 256
        		|| ySkew_x != 256 || zSkew_x != 256)
        {
            actions |= SKEW_MASK + SCALE_MASK + ROTATE_MASK + TRANSLATE_MASK;
            return;
        }
        if (scaleX != 256 || scaleZ != 256 || scaleY != 256)
        {
            actions |= SCALE_MASK + ROTATE_MASK + TRANSLATE_MASK;
            return;
        }
        if (rotateX != 0 || rotateZ != 0 || rotateY != 0)
        {
            actions |= ROTATE_MASK + TRANSLATE_MASK;
            return;
        }
        if (translateX != 0 || translateZ != 0 || translateY != 0)
        {
            actions |= TRANSLATE_MASK;
            return;
        }
        actions = 0;
        return;
    }

    private void translate(int x, int z, int y)
    {
        for (int l = 0; l < nbrCoordPoints; l++)
        {
            xCoordsDraw[l] += x;
            zCoordsDraw[l] += z;
            yCoordsDraw[l] += y;
        }

    }

    private void rotate(int x, int z, int y)
    {
        for (int i = 0; i < nbrCoordPoints; i++)
        {
            if (y != 0)
            {
                int sin = sin256[y];
                int cos = cos256[y];
                int tmp = zCoordsDraw[i] * sin + xCoordsDraw[i] * cos >> 15;
                zCoordsDraw[i] = zCoordsDraw[i] * cos - xCoordsDraw[i] * sin >> 15;
                xCoordsDraw[i] = tmp;
            }
            if (x != 0)
            {
                int sin = sin256[x];
                int cos = cos256[x];
                int tmp = zCoordsDraw[i] * cos - yCoordsDraw[i] * sin >> 15;
                yCoordsDraw[i] = zCoordsDraw[i] * sin + yCoordsDraw[i] * cos >> 15;
                zCoordsDraw[i] = tmp;
            }
            if (z != 0)
            {
                int sin = sin256[z];
                int cos = cos256[z];
                int tmp = yCoordsDraw[i] * sin + xCoordsDraw[i] * cos >> 15;
                yCoordsDraw[i] = yCoordsDraw[i] * cos - xCoordsDraw[i] * sin >> 15;
                xCoordsDraw[i] = tmp;
            }
        }

    }

    private void skew(int x_skew_z, int y_skew_z, int x_skew_y,
    		int z_skew_y, int y_skew_x, int z_skew_x)
    {
        for (int k1 = 0; k1 < nbrCoordPoints; k1++)
        {
            if (x_skew_z != 0)
                xCoordsDraw[k1] += zCoordsDraw[k1] * x_skew_z >> 8;
            if (y_skew_z != 0)
                yCoordsDraw[k1] += zCoordsDraw[k1] * y_skew_z >> 8;
            if (x_skew_y != 0)
                xCoordsDraw[k1] += yCoordsDraw[k1] * x_skew_y >> 8;
            if (z_skew_y != 0)
                zCoordsDraw[k1] += yCoordsDraw[k1] * z_skew_y >> 8;
            if (y_skew_x != 0)
                yCoordsDraw[k1] += xCoordsDraw[k1] * y_skew_x >> 8;
            if (z_skew_x != 0)
                zCoordsDraw[k1] += xCoordsDraw[k1] * z_skew_x >> 8;
        }

    }

    private void scale(int i, int j, int k) {
        for (int l = 0; l < nbrCoordPoints; l++) {
            xCoordsDraw[l] = xCoordsDraw[l] * i >> 8;
            zCoordsDraw[l] = zCoordsDraw[l] * j >> 8;
            yCoordsDraw[l] = yCoordsDraw[l] * k >> 8;
        }

    }

    private void findModelBounds()
    {
        xMin = zMin = yMin = 999999;
        longestLength = xMax = zMax = yMax = -999999;
        for (int i = 0; i < nbrSurfaces; i++)
        {
            int surface[] = surfaces[i];
            int firstPointIdx = surface[0];
            int pointsInCell = pointsPerCell[i];
            int xmin;
            int xmax = xmin = xCoordsDraw[firstPointIdx];
            int zmin;
            int zmax = zmin = zCoordsDraw[firstPointIdx];
            int ymin;
            int ymax = ymin = yCoordsDraw[firstPointIdx];
            for (int j = 0; j < pointsInCell; j++) {
                int point = surface[j];
                if (xCoordsDraw[point] < xmin)
                    xmin = xCoordsDraw[point];
                else if (xCoordsDraw[point] > xmax)
                    xmax = xCoordsDraw[point];
                if (zCoordsDraw[point] < zmin)
                    zmin = zCoordsDraw[point];
                else if (zCoordsDraw[point] > zmax)
                    zmax = zCoordsDraw[point];
                if (yCoordsDraw[point] < ymin)
                    ymin = yCoordsDraw[point];
                else if (yCoordsDraw[point] > ymax)
                    ymax = yCoordsDraw[point];
            }

            if (!aBoolean261) {
                xMinArray[i] = xmin;
                xMaxArray[i] = xmax;
                zMinArray[i] = zmin;
                zMaxArray[i] = zmax;
                yMinArray[i] = ymin;
                yMaxArray[i] = ymax;
            }
            if (xmax - xmin > longestLength)
                longestLength = xmax - xmin;
            if (zmax - zmin > longestLength)
                longestLength = zmax - zmin;
            if (ymax - ymin > longestLength)
                longestLength = ymax - ymin;
            if (xmin < xMin)
                xMin = xmin;
            if (xmax > xMax)
                xMax = xmax;
            if (zmin < zMin)
                zMin = zmin;
            if (zmax > zMax)
                zMax = zmax;
            if (ymin < yMin)
                yMin = ymin;
            if (ymax > yMax)
                yMax = ymax;
        }

    }

    public void setLightining()
    {
        if (aBoolean262)
            return;
        int i = featuresLight * lightSourceDist >> 8;
        for (int j = 0; j < nbrSurfaces; j++)
            if (lightSourceProjectToSurfNormal[j] != anInt270)
                lightSourceProjectToSurfNormal[j] = (xNormals[j] * lightSourceX
                		+ zNormals[j] * lightSourceZ
                		+ yNormals[j] * lightSourceY) / i;

        int someXArray[] = new int[nbrCoordPoints];
        int someZArray[] = new int[nbrCoordPoints];
        int someYArray[] = new int[nbrCoordPoints];
        int occurence[] = new int[nbrCoordPoints];

        for (int surfaceIdx = 0; surfaceIdx < nbrSurfaces; surfaceIdx++)
            if (lightSourceProjectToSurfNormal[surfaceIdx] == anInt270)
            {
                for (int pointIdx = 0; pointIdx < pointsPerCell[surfaceIdx]; pointIdx++)
                {
                    int point = surfaces[surfaceIdx][pointIdx];
                    someXArray[point] += xNormals[surfaceIdx];
                    someZArray[point] += zNormals[surfaceIdx];
                    someYArray[point] += yNormals[surfaceIdx];
                    occurence[point]++;
                }
            }

        for (int j1 = 0; j1 < nbrCoordPoints; j1++)
            if (occurence[j1] > 0)
                pointBrightness[j1] = (someXArray[j1] * lightSourceX
                		+ someZArray[j1] * lightSourceZ
                		+ someYArray[j1] * lightSourceY) / (i * occurence[j1]);
    }

	public void findNormals()
	{
		if (aBoolean262 && aBoolean261)
			return;
		for (int i = 0; i < nbrSurfaces; i++)
		{
			int surface[] = surfaces[i];
			int x0 = xCoordsDraw[surface[0]];
			int z0 = zCoordsDraw[surface[0]];
			int y0 = yCoordsDraw[surface[0]];
			int u_x = xCoordsDraw[surface[1]] - x0;
			int u_z = zCoordsDraw[surface[1]] - z0;
			int u_y = yCoordsDraw[surface[1]] - y0;
			int v_x = xCoordsDraw[surface[2]] - x0;
			int v_z = zCoordsDraw[surface[2]] - z0;
			int v_y = yCoordsDraw[surface[2]] - y0;
			
			int n_x = u_z * v_y - v_z * u_y;
			int n_z = u_y * v_x - v_y * u_x;
			int n_y = u_x * v_z - v_x * u_z;
			while(n_x > 0x2000 || n_z > 0x2000 || n_y > 0x2000
					|| n_x < -0x2000 || n_z < -0x2000 || n_y < -0x2000)
			{
				n_x >>= 1;
				n_z >>= 1;
				n_y >>= 1;
			}

			int n_length = (int) (256D * Math.sqrt(n_x * n_x + n_z * n_z + n_y * n_y));
			if (n_length <= 0)
				n_length = 1;
			xNormals[i] = (n_x * 0x10000) / n_length;
			zNormals[i] = (n_z * 0x10000) / n_length;
			yNormals[i] = (n_y * (0x10000)) / n_length;
			scaleFactor[i] = -1;
		}
		setLightining();
	}

    public void method200()
    {
        if (anInt246 == 2)
        { // 2d-sprites
            anInt246 = 0;
            for (int i = 0; i < nbrCoordPoints; i++)
            {
                xCoordsDraw[i] = xCoords[i];
                zCoordsDraw[i] = zCoords[i];
                yCoordsDraw[i] = yCoords[i];
            }

            xMin = zMin = yMin = 0xff676981;
            longestLength = xMax = zMax = yMax = 0x98967f;
            return;
        }
        if (anInt246 == 1)
        { // 3d objects
            anInt246 = 0;
            for (int j = 0; j < nbrCoordPoints; j++)
            {
                xCoordsDraw[j] = xCoords[j];
                zCoordsDraw[j] = zCoords[j];
                yCoordsDraw[j] = yCoords[j];
            }
            if ((actions & ROTATE_MASK) == ROTATE_MASK)
                rotate(rotateX, rotateZ, rotateY);
            if ((actions & SCALE_MASK) == SCALE_MASK)
                scale(scaleX, scaleZ, scaleY);
            if ((actions & SKEW_MASK) == SKEW_MASK)
                skew(xSkew_z, ySkew_z, xSkew_y, zSkew_y, ySkew_x, zSkew_x);
            if ((actions & TRANSLATE_MASK) == TRANSLATE_MASK)
                translate(translateX, translateZ, translateY);
            findModelBounds();
            findNormals();
        }
    }

    public void method201(
    		int cameraXPos, int cameraZPos, int cameraYPos,
    		int cameraXRot, int cameraZRot, int cameraYRot,
    		int cameraSizeInt, int drawMinDist)
    {
        method200();
        if (yMin > Camera.yMaxHide || yMax < Camera.yMinHide
        		|| xMin > Camera.xMaxHide || xMax < Camera.xMinHide
        		|| zMin > Camera.zMaxHide || zMax < Camera.zMinHide)
        {
            visible = false;
            return;
        }
        visible = true;
        int ySin = cameraYRot != 0 ? sin1024[cameraYRot] : 0;
        int yCos = cameraYRot != 0 ? cos1024[cameraYRot] : 0;

        int zSin = cameraZRot != 0 ? sin1024[cameraZRot] : 0;
        int zCos = cameraZRot != 0 ? cos1024[cameraZRot] : 0;

        int xSin = cameraXRot != 0 ? sin1024[cameraXRot] : 0;
        int xCos = cameraXRot != 0 ? cos1024[cameraXRot] : 0;

        for (int i = 0; i < nbrCoordPoints; i++)
        {
            int u_x = xCoordsDraw[i] - cameraXPos;
            int u_z = zCoordsDraw[i] - cameraZPos;
            int u_y = yCoordsDraw[i] - cameraYPos;
            if (cameraYRot != 0)
            {
                int tmp = u_z * ySin + u_x * yCos >> 15;
                u_z = u_z * yCos - u_x * ySin >> 15;
                u_x = tmp;
            }
            if (cameraZRot != 0)
            {
                int tmp = u_y * zSin + u_x * zCos >> 15;
                u_y = u_y * zCos - u_x * zSin >> 15;
                u_x = tmp;
            }
            if (cameraXRot != 0)
            {
                int tmp = u_z * xCos - u_y * xSin >> 15;
                u_y = u_z * xSin + u_y * xCos >> 15;
                u_z = tmp;
            }
            if (u_y >= drawMinDist)
                xCoordOnScreen[i] = (u_x << cameraSizeInt) / u_y;
            else
                xCoordOnScreen[i] = u_x << cameraSizeInt;
            if (u_y >= drawMinDist)
                yCoordOnScreen[i] = (u_z << cameraSizeInt) / u_y;
            else
                yCoordOnScreen[i] = u_z << cameraSizeInt;
            xDistToPointFromCamera[i] = u_x;
            zDistToPointFromCamera[i] = u_z;
            yDistToPointFromCamera[i] = u_y;
        }

    }

    public void method202()
    {
        method200();
        for (int i = 0; i < nbrCoordPoints; i++)
        {
            xCoords[i] = xCoordsDraw[i];
            zCoords[i] = zCoordsDraw[i];
            yCoords[i] = yCoordsDraw[i];
        }

        translateX = translateZ = translateY = 0;
        rotateX = rotateZ = rotateY = 0;
        scaleX = scaleZ = scaleY = 256;
        xSkew_z = ySkew_z = xSkew_y = zSkew_y = ySkew_x = zSkew_x = 256;
        actions = 0;
    }

    public Model method203()
    {
        Model models[] = new Model[1];
        models[0] = this;
        Model model = new Model(models, 1);
        model.anInt245 = anInt245;
        model.transparent = transparent;
        return model;
    }

    public Model method204(boolean flag, boolean flag1, boolean flag2, boolean flag3)
    {
        Model models[] = new Model[1];
        models[0] = this;
        Model model = new Model(models, 1, flag, flag1, flag2, flag3);
        model.anInt245 = anInt245;
        return model;
    }

    public void method205(Model model)
    {
        rotateX = model.rotateX;
        rotateZ = model.rotateZ;
        rotateY = model.rotateY;
        translateX = model.translateX;
        translateZ = model.translateZ;
        translateY = model.translateY;
        checkActions();
        anInt246 = 1;
    }

    public int method206(byte abyte0[])
    {
        for (; abyte0[anInt309] == 10 || abyte0[anInt309] == 13; anInt309++);
        int i = anIntArray268[abyte0[anInt309++] & 0xff];
        int j = anIntArray268[abyte0[anInt309++] & 0xff];
        int k = anIntArray268[abyte0[anInt309++] & 0xff];
        int l = (i * 4096 + j * 64 + k) - 0x20000;
        if (l == 0x1e240)
            l = anInt270;
        return l;
    }

    public int nbrCoordPoints;
    public int xDistToPointFromCamera[];
    public int zDistToPointFromCamera[];
    public int yDistToPointFromCamera[];
    public int xCoordOnScreen[];
    public int yCoordOnScreen[];
    public int pointBrightness[];
    public byte aByteArray233[];
    public int nbrSurfaces;
    public int pointsPerCell[];
    public int surfaces[][];
    public int surfaceTexture1[];
    public int surfaceTexture2[];
    public int normalLength[];
    public int scaleFactor[];
    public int lightSourceProjectToSurfNormal[];
    public int xNormals[];
    public int zNormals[];
    private int yNormals[];
    public int anInt245;
    public int anInt246;
    public boolean visible;
    public int xMin;
    public int xMax;
    public int zMin;
    public int zMax;
    public int yMin;
    public int yMax;
    public boolean aBoolean254;
    public boolean transparentTexture;
    public boolean transparent;
    public int anInt257;
    public int entityType[];
    public byte aByteArray259[];
    private boolean aBoolean260;
    public boolean aBoolean261;
    public boolean aBoolean262;
    public boolean aBoolean263;
    public boolean aBoolean264;
    private static int sin256[];
    private static int cos256[];
    private static int sin1024[];
    private static int cos1024[];
    private static byte aByteArray267[];
    private static int anIntArray268[];
    private int anInt270;
    public int nPoints;
    public int xCoords[];
    public int zCoords[];
    public int yCoords[];
    public int xCoordsDraw[];
    public int zCoordsDraw[];
    public int yCoordsDraw[];
    private int nSides;
    private int anIntArrayArray279[][];
    private int xMinArray[];
    private int xMaxArray[];
    private int zMinArray[];
    private int zMaxArray[];
    private int yMinArray[];
    private int yMaxArray[];
    private int translateX;
    private int translateZ;
    private int translateY;
    private int rotateX;
    private int rotateZ;
    private int rotateY;
    private int scaleX;
    private int scaleZ;
    private int scaleY;
    private int xSkew_z;
    private int ySkew_z;
    private int xSkew_y;
    private int zSkew_y;
    private int ySkew_x;
    private int zSkew_x;
    private int actions;
    private int longestLength;
    private int lightSourceX;
    private int lightSourceZ;
    private int lightSourceY;
    private int lightSourceDist;
    protected int featuresLight;
    protected int globalLight;
    private int anInt309;
    private static final int TRANSLATE_MASK = 1, ROTATE_MASK = 2, SCALE_MASK = 4, SKEW_MASK = 8;

    static {
        sin256 = new int[0x100];
        cos256 = new int[0x100];
        sin1024 = new int[0x400];
        cos1024 = new int[0x400];
        aByteArray267 = new byte[64];
        anIntArray268 = new int[256];
        for (int i = 0; i < 0x100;
        		sin256[i++] = (int) (Math.sin((double) i * 0.02454369D) * 32768D));
        for (int i = 0; i < 0x100;
        		cos256[i++] = (int) (Math.cos((double) i * 0.02454369D) * 32768D));

        for (int i = 0; i < 0x400;
        		sin1024[i++] = (int) (Math.sin((double) i * 0.00613592315D) * 32768D));
        for (int i = 0; i < 0x400;
        		cos1024[i++] = (int) (Math.cos((double) i * 0.00613592315D) * 32768D));

        for (int k = 0; k < 10; k++)
            aByteArray267[k] = (byte) (48 + k);

        for (int l = 0; l < 26; l++)
            aByteArray267[l + 10] = (byte) (65 + l);

        for (int i1 = 0; i1 < 26; i1++)
            aByteArray267[i1 + 36] = (byte) (97 + i1);

        aByteArray267[62] = -93;
        aByteArray267[63] = 36;
        for (int j1 = 0; j1 < 10; j1++)
            anIntArray268[48 + j1] = j1;

        for (int k1 = 0; k1 < 26; k1++)
            anIntArray268[65 + k1] = k1 + 10;

        for (int l1 = 0; l1 < 26; l1++)
            anIntArray268[97 + l1] = l1 + 36;

        anIntArray268[163] = 62;
        anIntArray268[36] = 63;
    }
}
