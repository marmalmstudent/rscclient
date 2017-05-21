package client;

import java.io.DataInputStream;
import java.io.IOException;

public class Model
{
	public static final int INVISIBLE = 0xbc614e; // 12345678, probably
	public static final int BIG_NUMBER = 9999999;
	public Model(int nPoints, int nSurfaces) {
		modelType = Model.MODEL_3D;
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
		invisible = INVISIBLE;
		longestLength = BIG_NUMBER;
		lightSourceX = 180;
		lightSourceZ = 155;
		lightSourceY = 95;
		lightSourceDist = 256;
		featuresLight = 512;
		globalLight = 32;
		initArrays(nPoints, nSurfaces);
		surfacesOrdered = new int[nSurfaces][1];
		for (int k = 0; k < nSurfaces; k++)
			surfacesOrdered[k][0] = k;
		
	}

	public Model(int nbrCoordPoints, int nbrSides, boolean flag, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
		modelType = Model.MODEL_3D;
		visible = true;
		aBoolean254 = true;
		transparentTexture = false;
		transparent = false;
		anInt257 = -1;
		invisible = INVISIBLE;
		longestLength = BIG_NUMBER;
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
		initArrays(nbrCoordPoints, nbrSides);
	}

	private void initArrays(int nbrCoordPoints, int nbrSides)
	{
		xCoords = new double[nbrCoordPoints];
		zCoords = new double[nbrCoordPoints];
		yCoords = new double[nbrCoordPoints];
		pointBrightness = new double[nbrCoordPoints];
		pointsPerCell = new int[nbrSides];
		surfaces = new int[nbrSides][];
		surfaceTexture1 = new int[nbrSides];
		surfaceTexture2 = new int[nbrSides];
		lightSourceProjectToSurfNormal = new double[nbrSides];
		normalLength = new double[nbrSides];
		if (!aBoolean264) {
			xCoordCamDist = new double[nbrCoordPoints];
			yCoordCamDist = new double[nbrCoordPoints];
			zCoordCamDist = new double[nbrCoordPoints];
			xProjected = new double[nbrCoordPoints];
			yProjected = new double[nbrCoordPoints];
		}
		if (!aBoolean263) {
			aByteArray259 = new byte[nbrSides];
			entityType = new int[nbrSides];
		}
		if (aBoolean260) {
        	zCoordsDraw = zCoords;
	        xCoordsDraw = xCoords;
        	yCoordsDraw = yCoords;
		} else {
			xCoordsDraw = new double[nbrCoordPoints];
			zCoordsDraw = new double[nbrCoordPoints];
			yCoordsDraw = new double[nbrCoordPoints];
		}
		if (!aBoolean262 || !aBoolean261) {
			xNormals = new double[nbrSides];
			zNormals = new double[nbrSides];
			yNormals = new double[nbrSides];
		}
		if (!aBoolean261) {
			xMinArray = new double[nbrSides];
			xMaxArray = new double[nbrSides];
			zMinArray = new double[nbrSides];
			zMaxArray = new double[nbrSides];
			yMinArray = new double[nbrSides];
			yMaxArray = new double[nbrSides];
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

	public void resetProjection() {
		xCoordCamDist = new double[nbrCoordPoints];
		yCoordCamDist = new double[nbrCoordPoints];
		zCoordCamDist = new double[nbrCoordPoints];
		xProjected = new double[nbrCoordPoints];
		yProjected = new double[nbrCoordPoints];
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
		modelType = Model.MODEL_3D;
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
		invisible = INVISIBLE;
		longestLength = BIG_NUMBER;
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
		surfacesOrdered = new int[nbrSurfaces][1];
		
		
		// The spatial coordinate points.
		for (int i = 0; i < nbrCoordPoints; i++)
		{
			xCoords[i] = DataOperations.getSigned2Bytes(database, offset) * EngineHandle.SCALE_FACTOR;
			offset += 2;
		}
		for (int i = 0; i < nbrCoordPoints; i++)
		{
			zCoords[i] = DataOperations.getSigned2Bytes(database, offset) * EngineHandle.SCALE_FACTOR;
			offset += 2;
		}
		for (int i = 0; i < nbrCoordPoints; i++)
		{
			yCoords[i] = DataOperations.getSigned2Bytes(database, offset) * EngineHandle.SCALE_FACTOR;
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
		 * Positive numbers (>= 0x0, < 0x7fff) seems to mean the texture index
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
				surfaceTexture1[i] = invisible;
		}
		for (int i = 0; i < nbrSurfaces; i++)
		{ // top/outside/upper i think
			surfaceTexture2[i] = DataOperations.getSigned2Bytes(database, offset);
			offset += 2;
			if (surfaceTexture2[i] == 32767)
				surfaceTexture2[i] = invisible;
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
				lightSourceProjectToSurfNormal[i] = invisible;
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
		modelType = Model.MODEL_3D;
	}

    public Model(String path)
    {
        modelType = Model.MODEL_3D;
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
        invisible = INVISIBLE;
        longestLength = BIG_NUMBER;
        lightSourceX = 180;
        lightSourceZ = 155;
        lightSourceY = 95;
        lightSourceDist = 256;
        featuresLight = 512;
        globalLight = 32;
        byte data[] = null;
        try {
            java.io.InputStream inputstream = DataOperations.streamFromPath(path);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            data = new byte[3];
            offset = 0;
            for (int i = 0; i < 3;
            		i += datainputstream.read(data, i, 3 - i));
            int k = getNextData(data);
            data = new byte[k];
            offset = 0;
            for (int j = 0; j < k;
            		j += datainputstream.read(data, j, k - j));
            datainputstream.close();
        }
        catch (IOException _ex) {
            nbrCoordPoints = 0;
            nbrSurfaces = 0;
            return;
        }
        int nPoints = getNextData(data);
        int nSurfaces = getNextData(data);
        initArrays(nPoints, nSurfaces);
        surfacesOrdered = new int[nSurfaces][];
        for (int i = 0; i < nPoints; i++)
        {
            int x = getNextData(data);
            int z = getNextData(data);
            int y = getNextData(data);
            insertCoordPoint(x, z, y);
        }

        for (int i = 0; i < nSurfaces; i++)
        {
            int pointsInCell = getNextData(data);
            int texture1 = getNextData(data);
            int texture2 = getNextData(data);
            int l2 = getNextData(data);
            featuresLight = getNextData(data);
            globalLight = getNextData(data);
            int i3 = getNextData(data);
            int surfacePoints[] = new int[pointsInCell];
            for (int j = 0; j < pointsInCell; j++)
                surfacePoints[j] = getNextData(data);

            int ai1[] = new int[l2];
            for (int j = 0; j < l2; j++)
                ai1[j] = getNextData(data);

            int lastSurface = addSurface(pointsInCell, surfacePoints, texture1, texture2);
            surfacesOrdered[i] = ai1;
            if (i3 == 0)
                lightSourceProjectToSurfNormal[lastSurface] = 0;
            else
                lightSourceProjectToSurfNormal[lastSurface] = invisible;
        }

        modelType = Model.MODEL_3D;
    }

    public Model(Model model[], int i, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        modelType = Model.MODEL_3D;
        visible = true;
        aBoolean254 = true;
        transparentTexture = false;
        transparent = false;
        anInt257 = -1;
        aBoolean264 = false;
        invisible = INVISIBLE;
        longestLength = BIG_NUMBER;
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
        modelType = Model.MODEL_3D;
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
        invisible = INVISIBLE;
        longestLength = BIG_NUMBER;
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
            surfacesOrdered = new int[totNbrSurfaces][];
        for (int i = 0; i < nModels; i++)
        {
            Model model = models[i];
            model.setCurrentToDefaultConfig();
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
                    cellPoints[k] = insertCoordPoint(
                    		model.xCoords[surface[k]],
                    		model.zCoords[surface[k]],
                    		model.yCoords[surface[k]]);

                int surfaceIdx = addSurface(model.pointsPerCell[j],
                		cellPoints, model.surfaceTexture1[j],
                		model.surfaceTexture2[j]);
                lightSourceProjectToSurfNormal[surfaceIdx] = model.lightSourceProjectToSurfNormal[j];
                normalLength[surfaceIdx] = model.normalLength[j];
                if (flag)
                    if (nModels > 1)
                    {
                        surfacesOrdered[surfaceIdx] = new int[model.surfacesOrdered[j].length + 1];
                        surfacesOrdered[surfaceIdx][0] = i;
                        for (int k = 0; k < model.surfacesOrdered[j].length; k++)
                            surfacesOrdered[surfaceIdx][k + 1] = model.surfacesOrdered[j][k];
                    }
                    else
                    {
                        surfacesOrdered[surfaceIdx] = new int[model.surfacesOrdered[j].length];
                        for (int k = 0; k < model.surfacesOrdered[j].length; k++)
                            surfacesOrdered[surfaceIdx][k] = model.surfacesOrdered[j][k];
                    }
            }
        }
        modelType = Model.MODEL_3D;
    }

    /**
     * Inserts a new coordinate point and returns the index of the point.
     * If the point already exists it will instead return the intex of that point.
     * @param x
     * @param z
     * @param y
     * @return
     */
    public int insertCoordPoint(double x, double z, double y)
    {
        for (int i = 0; i < nbrCoordPoints; i++)
            if (Math.abs(xCoords[i] - x) < 0.00625
            		&& Math.abs(zCoords[i] - z) < 0.00625
            		&& Math.abs(yCoords[i] - y) < 0.00625)
                return i;

        if (nbrCoordPoints >= nPoints)
            return -1;
        xCoords[nbrCoordPoints] = x;
        zCoords[nbrCoordPoints] = z;
        yCoords[nbrCoordPoints] = y;
        return nbrCoordPoints++;
    }

    public int addCoordPoint(double x, double z, double y)
    {
        if (nbrCoordPoints >= nPoints)
            return -1;
        xCoords[nbrCoordPoints] = x;
        zCoords[nbrCoordPoints] = z;
        yCoords[nbrCoordPoints] = y;
        return nbrCoordPoints++;
    }

    public int addSurface(int pointsInCell, int surfacePoints[],
    		int texture1, int texture2)
    {
        if (nbrSurfaces >= nSides)
            return -1;
        pointsPerCell[nbrSurfaces] = pointsInCell;
        surfaces[nbrSurfaces] = surfacePoints;
        surfaceTexture1[nbrSurfaces] = texture1;
        surfaceTexture2[nbrSurfaces] = texture2;
        modelType = Model.MODEL_3D;
        return nbrSurfaces++;
    }

    public Model[] makeModels(double factor1, double factor2,
    		int factor3, int nModels, int k1, boolean flag) {
        setCurrentToDefaultConfig();
        int nPoints[] = new int[nModels];
        int nSides[] = new int[nModels];

        for (int surf = 0; surf < nbrSurfaces; surf++)
        {
            double xSum = 0;
            double ySum = 0;
            int pointsInCell = pointsPerCell[surf];
            int surface[] = surfaces[surf];
            for (int p = 0; p < pointsInCell; p++)
            {
                xSum += xCoords[surface[p]];
                ySum += yCoords[surface[p]];
            }

            int mdl = ((int)(xSum / (pointsInCell * factor1)))
            		+ ((int)(ySum / (pointsInCell * factor2))) * factor3;
            nPoints[mdl] += pointsInCell;
            nSides[mdl]++;
        }

        Model models[] = new Model[nModels];
        for (int mdl = 0; mdl < nModels; mdl++)
        {
            if (nPoints[mdl] > k1)
                nPoints[mdl] = k1;
            models[mdl] = new Model(nPoints[mdl], nSides[mdl], true, true, true, flag, true);
            models[mdl].featuresLight = featuresLight;
            models[mdl].globalLight = globalLight;
        }

        for (int surf = 0; surf < nbrSurfaces; surf++)
        {
            double xSum = 0;
            double ySum = 0;
            int pointsInCell = pointsPerCell[surf];
            int surface[] = surfaces[surf];
            for (int j = 0; j < pointsInCell; j++)
            {
                xSum += xCoords[surface[j]];
                ySum += yCoords[surface[j]];
            }

            int mdl = ((int)(xSum / (pointsInCell * factor1)) + ((int)(ySum / (pointsInCell * factor2))) * factor3);
            makeModelSurfaces(models[mdl], surface, pointsInCell, surf);
        }

        for (int i = 0; i < nModels; i++)
            models[i].resetProjection();

        return models;
    }

    public void makeModelSurfaces(Model model, int surface[],
    		int pointsInCell, int surfacIdx)
    {
        int newSurface[] = new int[pointsInCell];
        for (int k = 0; k < pointsInCell; k++) {
            int point = newSurface[k] = model.insertCoordPoint(
            		xCoords[surface[k]],
            		zCoords[surface[k]],
            		yCoords[surface[k]]);
            model.pointBrightness[point] = pointBrightness[surface[k]];
        }

        int lastSurface = model.addSurface(pointsInCell, newSurface, surfaceTexture1[surfacIdx], surfaceTexture2[surfacIdx]);
        if (!model.aBoolean263 && !aBoolean263)
            model.entityType[lastSurface] = entityType[surfacIdx];
        model.lightSourceProjectToSurfNormal[lastSurface] = lightSourceProjectToSurfNormal[surfacIdx];
        model.normalLength[lastSurface] = normalLength[surfacIdx];
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

    public void setLightAndSource(int globalLight, int featureLight,
    		int x, int z, int y)
    {
    	setLightning(globalLight, featureLight);
        setLightsource(x, z, y);
    }

    public void setLightning(int global, int feature)
    {
        globalLight = 256 - global * 4;
        featuresLight = (64 - feature) * 32 + 256;
    }

    public void setShadowGradient(boolean shadeGradient)
    {
        if (aBoolean262)
            return;
        if (shadeGradient)
        	for (int i = 0; i < nbrSurfaces;
        			lightSourceProjectToSurfNormal[i++] = invisible);
        else
        	for (int i = 0; i < nbrSurfaces;
        			lightSourceProjectToSurfNormal[i++] = 0);
    }

    public void setLightsource(double x, double z, double y)
    {
        if (aBoolean262)
            return;
        lightSourceX = x;
        lightSourceZ = z;
        lightSourceY = y;
        lightSourceDist = Math.sqrt(x * x + z * z + y * y);
        setLightining();
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
        modelType = Model.MODEL_3D;
    }

    public void addTranslate(double x, double z, double y)
    {
    	setTranslate(translateX + x, translateZ + z, translateY + y);
    }

    public void setTranslate(double x, double z, double y)
    {
        translateX = x;
        translateZ = z;
        translateY = y;
        checkActions();
        modelType = Model.MODEL_3D;
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

    private void translate(double x, double z, double y)
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
                double sin = Trig.sin256[y];
                double cos = Trig.cos256[y];
                double tmp = zCoordsDraw[i] * sin + xCoordsDraw[i] * cos;
                zCoordsDraw[i] = zCoordsDraw[i] * cos - xCoordsDraw[i] * sin;
                xCoordsDraw[i] = tmp;
            }
            if (x != 0)
            {
            	double sin = Trig.sin256[x];
            	double cos = Trig.cos256[x];
                double tmp = zCoordsDraw[i] * cos - yCoordsDraw[i] * sin;
                yCoordsDraw[i] = zCoordsDraw[i] * sin + yCoordsDraw[i] * cos;
                zCoordsDraw[i] = tmp;
            }
            if (z != 0)
            {
            	double sin = Trig.sin256[z];
            	double cos = Trig.cos256[z];
                double tmp = yCoordsDraw[i] * sin + xCoordsDraw[i] * cos;
                yCoordsDraw[i] = yCoordsDraw[i] * cos - xCoordsDraw[i] * sin;
                xCoordsDraw[i] = tmp;
            }
        }

    }

    private void skew(double x_skew_z, double y_skew_z, double x_skew_y,
    		double z_skew_y, double y_skew_x, double z_skew_x)
    {
        for (int k1 = 0; k1 < nbrCoordPoints; k1++)
        {
            if (x_skew_z != 0)
                xCoordsDraw[k1] += zCoordsDraw[k1] * x_skew_z / 256D;
            if (y_skew_z != 0)
                yCoordsDraw[k1] += zCoordsDraw[k1] * y_skew_z / 256D;
            if (x_skew_y != 0)
                xCoordsDraw[k1] += yCoordsDraw[k1] * x_skew_y / 256D;
            if (z_skew_y != 0)
                zCoordsDraw[k1] += yCoordsDraw[k1] * z_skew_y / 256D;
            if (y_skew_x != 0)
                yCoordsDraw[k1] += xCoordsDraw[k1] * y_skew_x / 256D;
            if (z_skew_x != 0)
                zCoordsDraw[k1] += xCoordsDraw[k1] * z_skew_x / 256D;
        }

    }

    private void scale(int i, int j, int k) {
        for (int l = 0; l < nbrCoordPoints; l++) {
            xCoordsDraw[l] = xCoordsDraw[l] * i / 256D;
            zCoordsDraw[l] = zCoordsDraw[l] * j / 256D;
            yCoordsDraw[l] = yCoordsDraw[l] * k / 256D;
        }

    }

    private void findModelBounds()
    {
        xMin = zMin = yMin = BIG_NUMBER;
        longestLength = xMax = zMax = yMax = -BIG_NUMBER;
        for (int i = 0; i < nbrSurfaces; i++)
        {
            int surface[] = surfaces[i];
            int firstPointIdx = surface[0];
            int pointsInCell = pointsPerCell[i];
            double xmin;
            double xmax = xmin = xCoordsDraw[firstPointIdx];
            double zmin;
            double zmax = zmin = zCoordsDraw[firstPointIdx];
            double ymin;
            double ymax = ymin = yCoordsDraw[firstPointIdx];
            for (int j = 0; j < pointsInCell; j++)
            {
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
        double lightMod = featuresLight * lightSourceDist / 256;
        for (int j = 0; j < nbrSurfaces; j++)
            if (lightSourceProjectToSurfNormal[j] != invisible)
                lightSourceProjectToSurfNormal[j] = (xNormals[j] * lightSourceX
                		+ zNormals[j] * lightSourceZ
                		+ yNormals[j] * lightSourceY) / lightMod;

        double someXArray[] = new double[nbrCoordPoints];
        double someZArray[] = new double[nbrCoordPoints];
        double someYArray[] = new double[nbrCoordPoints];
        int occurence[] = new int[nbrCoordPoints];

        for (int j = 0; j < nbrSurfaces; j++)
            if (lightSourceProjectToSurfNormal[j] == invisible)
            {
                for (int pointIdx = 0; pointIdx < pointsPerCell[j]; pointIdx++)
                {
                    int point = surfaces[j][pointIdx];
                    someXArray[point] += xNormals[j];
                    someZArray[point] += zNormals[j];
                    someYArray[point] += yNormals[j];
                    occurence[point]++;
                }
            }

        for (int j1 = 0; j1 < nbrCoordPoints; j1++)
            if (occurence[j1] > 0)
            {
                pointBrightness[j1] = (someXArray[j1] * lightSourceX
                		+ someZArray[j1] * lightSourceZ
                		+ someYArray[j1] * lightSourceY) / (lightMod * occurence[j1]);
            }
    }

	public void findNormals()
	{
		if (aBoolean262 && aBoolean261)
			return;
		for (int i = 0; i < nbrSurfaces; i++)
		{
			int surface[] = surfaces[i];
			double x0 = xCoordsDraw[surface[0]];
			double z0 = zCoordsDraw[surface[0]];
			double y0 = yCoordsDraw[surface[0]];
			double u_x = xCoordsDraw[surface[1]] - x0;
			double u_z = zCoordsDraw[surface[1]] - z0;
			double u_y = yCoordsDraw[surface[1]] - y0;
			double v_x = xCoordsDraw[surface[2]] - x0;
			double v_z = zCoordsDraw[surface[2]] - z0;
			double v_y = yCoordsDraw[surface[2]] - y0;
			
			double n_x = u_z * v_y - v_z * u_y;
			double n_z = u_y * v_x - v_y * u_x;
			double n_y = u_x * v_z - v_x * u_z;
			double n_length = Math.sqrt(n_x * n_x + n_z * n_z + n_y * n_y);
			xNormals[i] = 256 * n_x / n_length;
			zNormals[i] = 256 * n_z / n_length;
			yNormals[i] = 256 * n_y / n_length;
		}
		setLightining();
	}

    public void resetWithCurrentConfig()
    {
        if (modelType == Model.MODEL_2D)
        { // 2d-sprites
            modelType = 0;
            for (int i = 0; i < nbrCoordPoints; i++)
            {
                xCoordsDraw[i] = xCoords[i];
                zCoordsDraw[i] = zCoords[i];
                yCoordsDraw[i] = yCoords[i];
            }

            xMin = zMin = yMin = -BIG_NUMBER;
            longestLength = xMax = zMax = yMax = BIG_NUMBER;
            return;
        }
        if (modelType == Model.MODEL_3D)
        { // 3d objects
            modelType = 0;
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

    public void makePerspectiveVectors(
    		double cameraXPos, double cameraZPos, double cameraYPos,
    		int cameraXRot, int cameraZRot, int cameraYRot,
    		int cameraSizeInt, double drawMinDist)
    {
        resetWithCurrentConfig();
        if (yMin > Camera.distMaxHide || yMax < Camera.distMinHide
        		|| xMin > Camera.xMaxHide || xMax < Camera.xMinHide
        		|| zMin > Camera.yMaxHide || zMax < Camera.yMinHide)
        {
            visible = false;
            return;
        }
        visible = true;
        double ySin = cameraYRot != 0 ? Trig.sin1024[cameraYRot] : 0;
        double yCos = cameraYRot != 0 ? Trig.cos1024[cameraYRot] : 0;

        double zSin = cameraZRot != 0 ? Trig.sin1024[cameraZRot] : 0;
        double zCos = cameraZRot != 0 ? Trig.cos1024[cameraZRot] : 0;

        double xSin = cameraXRot != 0 ? Trig.sin1024[cameraXRot] : 0;
        double xCos = cameraXRot != 0 ? Trig.cos1024[cameraXRot] : 0;

        for (int i = 0; i < nbrCoordPoints; i++)
        {
        	/* rotate object */
            double u_x = xCoordsDraw[i] - cameraXPos;
            double u_z = zCoordsDraw[i] - cameraZPos;
            double u_y = yCoordsDraw[i] - cameraYPos;
            if (cameraYRot != 0)
            {
            	double tmp = u_z * ySin + u_x * yCos;
                u_z = u_z * yCos - u_x * ySin;
                u_x = tmp;
            }
            if (cameraZRot != 0)
            {
                double tmp = u_y * zSin + u_x * zCos;
                u_y = u_y * zCos - u_x * zSin;
                u_x = tmp;
            }
            if (cameraXRot != 0)
            {
                double tmp = u_z * xCos - u_y * xSin;
                u_y = u_z * xSin + u_y * xCos;
                u_z = tmp;
            }
            int factr = 1 << cameraSizeInt;
            if (u_y >= drawMinDist)
                xProjected[i] = u_x * factr / u_y;
            else
                xProjected[i] = u_x * factr;
            if (u_y >= drawMinDist)
                yProjected[i] = u_z * factr / u_y;
            else
                yProjected[i] = u_z * factr;
            /* new coordinate system for camera;
             * x grows left to right,
             * y grows top to bottom,
             * z grows into the monitor */
            xCoordCamDist[i] = u_x;
            yCoordCamDist[i] = u_z;
            zCoordCamDist[i] = u_y;
        }
    }
    
    public double getDistanceTo(int p)
    {
    	// seems more accurate sphere-like when using 1.25
		double x = xCoordCamDist[p]*1.25;
		double y = yCoordCamDist[p];
		double z = zCoordCamDist[p];
		return Math.sqrt(x*x + y*y + z*z);
    }

    public void setCurrentToDefaultConfig()
    {
        resetWithCurrentConfig();
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

    public Model newModel()
    {
    	Model model = new Model(new Model[]{this}, 1);
        model.transparent = transparent;
        return model;
    }

    public Model newModel(boolean flag, boolean flag1, boolean flag2, boolean flag3)
    {
        Model model = new Model(new Model[]{this}, 1, flag, flag1, flag2, flag3);
        return model;
    }

    public void copyRotTrans(Model model)
    {
        rotateX = model.rotateX;
        rotateZ = model.rotateZ;
        rotateY = model.rotateY;
        translateX = model.translateX;
        translateZ = model.translateZ;
        translateY = model.translateY;
        checkActions();
        modelType = Model.MODEL_3D;
    }

    public int getNextData(byte abyte0[])
    {
        for (; abyte0[offset] == 10 || abyte0[offset] == 13; offset++);
        int i = anIntArray268[abyte0[offset++] & 0xff];
        int j = anIntArray268[abyte0[offset++] & 0xff];
        int k = anIntArray268[abyte0[offset++] & 0xff];
        int l = (i * 4096 + j * 64 + k) - 0x20000;
        if (l == 0x1e240)
            l = invisible;
        return l;
    }

    public int nbrCoordPoints;
    public double xCoordCamDist[];
    public double yCoordCamDist[];
    public double zCoordCamDist[];
    public double xProjected[];
    public double yProjected[];
    public double pointBrightness[];
    public int nbrSurfaces;
    public int pointsPerCell[];
    public int surfaces[][];
    public int surfaceTexture1[];
    public int surfaceTexture2[];
    public double normalLength[];
    public double lightSourceProjectToSurfNormal[];
    public double xNormals[];
    public double zNormals[];
    private double yNormals[];
    public int modelType;
    public static final int MODEL_3D = 1, MODEL_2D = 2;
    public boolean visible;
    public double xMin;
    public double xMax;
    public double zMin;
    public double zMax;
    public double yMin;
    public double yMax;
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
    private static byte aByteArray267[];
    private static int anIntArray268[];
    private int invisible;
    public int nPoints;
    public double xCoords[];
    public double zCoords[];
    public double yCoords[];
    public double xCoordsDraw[];
    public double zCoordsDraw[];
    public double yCoordsDraw[];
    private int nSides;
    private int surfacesOrdered[][];
    private double xMinArray[];
    private double xMaxArray[];
    private double zMinArray[];
    private double zMaxArray[];
    private double yMinArray[];
    private double yMaxArray[];
    private double translateX;
    private double translateZ;
    private double translateY;
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
    private double longestLength;
    private double lightSourceX;
    private double lightSourceZ;
    private double lightSourceY;
    private double lightSourceDist;
    protected double featuresLight;
    protected double globalLight;
    private int offset;
    private static final int TRANSLATE_MASK = 1, ROTATE_MASK = 2, SCALE_MASK = 4, SKEW_MASK = 8;

    static {
        aByteArray267 = new byte[64];
        anIntArray268 = new int[256];

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
