package client;

public class Camera {
	public static int light_x = 0, light_z = -50, light_y = 50;
	private mudclient mc;

	public Camera(mudclient mc, GameImage gameImage, int maxModels, int maxCameraModels, int nSurfaces) {
		this.mc = mc;
		nbrColors = 50;
		colorArray = new int[nbrColors];
		colorGradientArray = new int[nbrColors][256];
		planeOfViewOffsetFromCamera = 5;
		drawModelMaxDist = 1000 * EngineHandle.SCALE_FACTOR;
		drawSpriteMaxDist = 1000 * EngineHandle.SCALE_FACTOR;
		fadeFactor = 20 * EngineHandle.SCALE_FACTOR;
		fadeDist = 10 * EngineHandle.SCALE_FACTOR;
		gradient2Step = false;
		aDouble387 = 1.1000000000000001D;
		anInt388 = 1;
		aBoolean389 = false;
		maxVisibleModelCount = 100;
		visibleModelsArray = new Model[maxVisibleModelCount];
		visibleModelIntArray = new int[maxVisibleModelCount];
		/*
        width = 512;
        halfWidth = 256;
        halfHeight = 192;
        halfWidth2 = 256;
        halfHeight2 = 256;
        cameraSizeInt = 8;
		 */
		anInt402 = 4;
		xScreen = new double[40];
		yScreen = new double[40];
		pointBrightness = new int[40];
		xDistToPointFromCamera = new double[40];
		zDistToPointFromCamera = new double[40];
		yDistToPointFromCamera = new double[40];
		f1Toggle = false;
		this.gameImage = gameImage;
		halfWidth = gameImage.gameWindowWidth / 2;
		halfHeight = gameImage.gameWindowHeight / 2;
		imagePixelArray = gameImage.imagePixelArray;
		modelCount = 0;
		maxModelCount = maxModels;
		modelArray = new Model[maxModelCount];
		modelIntArray = new int[maxModelCount];
		cameraModelCount = 0;
		cameraModels = new CameraModel[maxCameraModels];
		for (int l = 0; l < maxCameraModels; l++)
			cameraModels[l] = new CameraModel();

		mobIndex = 0;
		aModel = new Model(nSurfaces * 2, nSurfaces);
		anIntArray416 = new int[nSurfaces];
		mobWidth = new int[nSurfaces];
		mobHeight = new int[nSurfaces];
		mobX = new double[nSurfaces];
		mobZ = new double[nSurfaces];
		mobY = new double[nSurfaces];
		anIntArray422 = new int[nSurfaces];
		if (aByteArray434 == null)
			aByteArray434 = new byte[17691];
		cameraXPos = 0;
		cameraZPos = 0;
		cameraYPos = 0;
		cameraXRot = 0;
		cameraZRot = 0;
		cameraYRot = 0;
		for (int i1 = 0; i1 < 256; i1++)
				sin256[i1] = (int) (Math.sin((double) i1 * 0.02454369D) * 32768D);
		for (int i1 = 0; i1 < 256; i1++)
				cos256[i1] = (int) (Math.cos((double) i1 * 0.02454369D) * 32768D);

		for (int j1 = 0; j1 < 1024; j1++)
				sin1024[j1] = Math.sin((double) j1 * 0.00613592315D);
		for (int j1 = 0; j1 < 1024; j1++)
				cos1024[j1] = Math.cos((double) j1 * 0.00613592315D);

	}

	public void addModel(Model model) {
		if (model == null)
			System.out.println("Warning tried to add null object!");
		if (modelCount < maxModelCount) {
			modelIntArray[modelCount] = 0;
			modelArray[modelCount++] = model;
		}
	}

	public void removeModel(Model model)
	{
		for (int i = 0; i < modelCount; i++)
			if (modelArray[i] == model)
			{
				modelCount--;
				for (int j = i; j < modelCount; j++)
				{
					modelArray[j] = modelArray[j + 1];
					modelIntArray[j] = modelIntArray[j + 1];
				}

			}

	}

	public void cleanupModels() {
		method266();
		for (int i = 0; i < modelCount; i++)
			modelArray[i] = null;

		modelCount = 0;
	}

	public void method266() {
		mobIndex = 0;
		aModel.method176();
	}

	public void updateFightCount(int i) {
		mobIndex -= i;
		aModel.method177(i, i * 2);
		if (mobIndex < 0)
			mobIndex = 0;
	}

	public int method268(int i, double x, double z, double y, int width, int height, int type) {
		anIntArray416[mobIndex] = i;
		mobX[mobIndex] = x;
		mobZ[mobIndex] = z;
		mobY[mobIndex] = y;
		mobWidth[mobIndex] = width;
		mobHeight[mobIndex] = height;
		anIntArray422[mobIndex] = 0;
		int p0 = aModel.insertCoordPoint(x, z, y);
		int p1 = aModel.insertCoordPoint(x, z - height, y);
		int surface[] = {
				p0, p1
		};
		aModel.addSurface(2, surface, 0, 0);
		aModel.entityType[mobIndex] = type;
		aModel.aByteArray259[mobIndex++] = 0;
		return mobIndex - 1;
	}

	public void setOurPlayer(int i) {
		aModel.aByteArray259[i] = 1;
	}

	public void setCombat(int i, int j) {
		anIntArray422[i] = j;
	}

	public void updateMouseCoords(int x, int y) {
		mouseX = x - halfWidth2;
		mouseY = y;
		currentVisibleModelCount = 0;
		aBoolean389 = true;
	}

	public int method272() {
		return currentVisibleModelCount;
	}

	public int[] method273() {
		return visibleModelIntArray;
	}

	public Model[] getVisibleModels() {
		return visibleModelsArray;
	}

	public void setCameraSize(int halfWindowWidth, int halfWindowHeight, int halfWindowWidth2, int halfWindowHeight2, int windowWidth, int camSizeInt) {
		halfWidth = halfWindowWidth2;
		halfHeight = halfWindowHeight2;
		halfWidth2 = halfWindowWidth;
		halfHeight2 = halfWindowHeight;
		width = windowWidth;
		cameraSizeInt = camSizeInt;
		cameraVariables = new CameraVariables[halfWindowHeight2 + halfWindowHeight];
		for (int k1 = 0; k1 < halfWindowHeight2 + halfWindowHeight; k1++)
			cameraVariables[k1] = new CameraVariables();

	}

	private void sortModelsByDistToCam(CameraModel cameraModels[], int lower, int upper)
	{
		if (lower < upper)
		{
			int nextLower = lower - 1;
			int nextUpper = upper + 1;
			int middle = (lower + upper) / 2;
			CameraModel camMdlMiddle = cameraModels[middle];
			cameraModels[middle] = cameraModels[lower];
			cameraModels[lower] = camMdlMiddle;
			double drawOrderMiddle = camMdlMiddle.drawOrderVal;
			while (nextLower < nextUpper)
			{
				do nextUpper--;
				while (cameraModels[nextUpper].drawOrderVal < drawOrderMiddle);
				do nextLower++;
				while (cameraModels[nextLower].drawOrderVal > drawOrderMiddle);
				if (nextLower < nextUpper)
				{ // swap upper and lower
					CameraModel tmp = cameraModels[nextLower];
					cameraModels[nextLower] = cameraModels[nextUpper];
					cameraModels[nextUpper] = tmp;
				}
			}
			sortModelsByDistToCam(cameraModels, lower, nextUpper);
			sortModelsByDistToCam(cameraModels, nextUpper + 1, upper);
		}
	}

	public void method277(int i, CameraModel cameraModels[], int lastIdx)
	{
		for (int k = 0; k <= lastIdx; k++) {
			cameraModels[k].aBoolean367 = false;
			cameraModels[k].anInt368 = k;
			cameraModels[k].anInt369 = -1;
		}

		int l = 0;
		do {
			while (cameraModels[l].aBoolean367) l++;
			if (l == lastIdx)
				return;
			CameraModel cameraModel = cameraModels[l];
			cameraModel.aBoolean367 = true;
			int i1 = l;
			int j1 = l + i;
			if (j1 >= lastIdx)
				j1 = lastIdx - 1;
			for (int k = j1; k >= i1 + 1; k--)
			{
				CameraModel camMdl = cameraModels[k];
				if (cameraModel.xMin < camMdl.xMax
						&& camMdl.xMin < cameraModel.xMax
						&& cameraModel.yMin < camMdl.yMax
						&& camMdl.yMin < cameraModel.yMax
						&& cameraModel.anInt368 != camMdl.anInt369
						&& !method295(cameraModel, camMdl)
						&& method296(camMdl, cameraModel))
				{
					method278(cameraModels, i1, k);
					if (cameraModels[k] != camMdl)
						k++;
					i1 = anInt454;
					camMdl.anInt369 = cameraModel.anInt368;
				}
			}
		} while (true);
	}

	public boolean method278(CameraModel cameraModels[], int i, int j) {
		do {
			CameraModel cameraModel = cameraModels[i];
			for (int k = i + 1; k <= j; k++) {
				CameraModel cameraModel_1 = cameraModels[k];
				if (!method295(cameraModel_1, cameraModel))
					break;
				cameraModels[i] = cameraModel_1;
				cameraModels[k] = cameraModel;
				i = k;
				if (i == j) {
					anInt454 = i;
					anInt455 = i - 1;
					return true;
				}
			}

			CameraModel cameraModel_2 = cameraModels[j];
			for (int l = j - 1; l >= i; l--) {
				CameraModel cameraModel_3 = cameraModels[l];
				if (!method295(cameraModel_2, cameraModel_3))
					break;
				cameraModels[j] = cameraModel_3;
				cameraModels[l] = cameraModel_2;
				j = l;
				if (i == j) {
					anInt454 = j + 1;
					anInt455 = j;
					return true;
				}
			}

			if (i + 1 >= j) {
				anInt454 = i;
				anInt455 = j;
				return false;
			}
			if (!method278(cameraModels, i + 1, j)) {
				anInt454 = i;
				return false;
			}
			j = anInt455;
		} while (true);
	}

	public void method279(double i, double j, double k) {
		int xRot = -cameraXRot + 1024 & 0x3ff;
		int zRot = -cameraZRot + 1024 & 0x3ff;
		int yRot = -cameraYRot + 1024 & 0x3ff;
		if (yRot != 0) {
			double sin = sin1024[yRot];
			double cos = cos1024[yRot];
			double tmp = j * sin + i * cos;
				j = j * cos - i * sin;
				i = tmp;
		}
		if (xRot != 0) {
			double sin = sin1024[xRot];
			double cos = cos1024[xRot];
			double tmp = j * cos - k * sin;
			k = j * sin + k * cos;
			j = tmp;
		}
		if (zRot != 0) {
			double sin = sin1024[zRot];
			double cos = cos1024[zRot];
			double tmp = k * sin + i * cos;
			k = k * cos - i * sin;
			i = tmp;
		}
		if (i < xMinHide)
			xMinHide = i;
		if (i > xMaxHide)
			xMaxHide = i;
		if (j < zMinHide)
			zMinHide = j;
		if (j > zMaxHide)
			zMaxHide = j;
		if (k < yMinHide)
			yMinHide = k;
		if (k > yMaxHide)
			yMaxHide = k;
	}

	public void finishCamera() {
		f1Toggle = gameImage.f1Toggle;
		int fctr = 1 << cameraSizeInt;
		double i3 = halfWidth * drawModelMaxDist / fctr;
		double j3 = halfHeight * drawModelMaxDist / fctr;
		xMinHide = 0;
		xMaxHide = 0;
		zMinHide = 0;
		zMaxHide = 0;
		yMinHide = 0;
		yMaxHide = 0;
		method279(-i3, -j3, drawModelMaxDist);
		method279(-i3, j3, drawModelMaxDist);
		method279(i3, -j3, drawModelMaxDist);
		method279(i3, j3, drawModelMaxDist);
		method279(-halfWidth, -halfHeight, 0);
		method279(-halfWidth, halfHeight, 0);
		method279(halfWidth, -halfHeight, 0);
		method279(halfWidth, halfHeight, 0);
		xMinHide += cameraXPos;
		xMaxHide += cameraXPos;
		zMinHide += cameraZPos;
		zMaxHide += cameraZPos;
		yMinHide += cameraYPos;
		yMaxHide += cameraYPos;
		modelArray[modelCount] = aModel;
		aModel.modelType = 2;
		for (int i = 0; i < modelCount; i++)
			modelArray[i].makePerspectiveVectors(
					cameraXPos, cameraZPos, cameraYPos, cameraXRot,
					cameraZRot, cameraYRot, cameraSizeInt, planeOfViewOffsetFromCamera);

		modelArray[modelCount].makePerspectiveVectors(
				cameraXPos, cameraZPos, cameraYPos, cameraXRot,
				cameraZRot, cameraYRot, cameraSizeInt, planeOfViewOffsetFromCamera);
		cameraModelCount = 0;
		for (int i = 0; i < modelCount; i++)
		{
			Model model = modelArray[i];
			if (model.visible)
			{
				for (int j = 0; j < model.nbrSurfaces; j++)
				{
					int pointsInCell = model.pointsPerCell[j];
					int surface[] = model.surfaces[j];
					boolean draw = false;
					for (int k = 0; k < pointsInCell; k++)
					{
						double zDist = model.zDistToPointFromCameraView[surface[k]];
						if (zDist <= planeOfViewOffsetFromCamera
								|| zDist >= drawModelMaxDist)
							continue;
						draw = true;
						break;
					}

					if (draw)
					{
						int visibleXMask = 0;
						for (int k = 0; k < pointsInCell; k++)
						{
							double x = model.xScreen[surface[k]];
							if (x > -halfWidth)
								visibleXMask |= 1;
							if (x < halfWidth)
								visibleXMask |= 2;
							if (visibleXMask == 3)
								break;
						}

						if (visibleXMask == 3)
						{
							int visibleYMask = 0;
							for (int k = 0; k < pointsInCell; k++)
							{
								int k1 = (int)model.yScreen[surface[k]];
								if (k1 > -halfHeight)
									visibleYMask |= 1;
								if (k1 < halfHeight)
									visibleYMask |= 2;
								if (visibleYMask == 3)
									break;
							}

							if (visibleYMask == 3)
							{
								CameraModel camMdl = cameraModels[cameraModelCount];
								camMdl.model = model;
								camMdl.surface = j;
								method293(cameraModelCount);
								int modelTexture;
								if (camMdl.normalDirectionToCamera < 0)
									modelTexture = model.surfaceTexture1[j];
								else
									modelTexture = model.surfaceTexture2[j];
								if (modelTexture != Model.INVISIBLE)
								{
									/* calculate average distance to object. */
									double sum = 0;
									for (int k = 0; k < pointsInCell; k++)
										sum += model.getDistanceTo(surface[k]);
									camMdl.drawOrderVal = (int)(sum / (double)pointsInCell);
									camMdl.color = modelTexture;
									cameraModelCount++;
								}
							}
						}
					}
				}

			}
		}

		Model model_1 = aModel;
		if (model_1.visible)
		{
			for (int k = 0; k < model_1.nbrSurfaces; k++)
			{
				int surface[] = model_1.surfaces[k];
				int p0 = surface[0];
				double x = model_1.xScreen[p0];
				double y = model_1.yScreen[p0];
				double distToCam = model_1.getDistanceTo(p0);
				if (distToCam > planeOfViewOffsetFromCamera
						&& distToCam < drawSpriteMaxDist)
				{
					double modelWidth = (mobWidth[k] << cameraSizeInt) / distToCam;
					double modelHeight = (mobHeight[k] << cameraSizeInt) / distToCam;
					if (x - modelWidth / 2 <= halfWidth
							&& x + modelWidth / 2 >= -halfWidth
							&& y - modelHeight <= halfHeight
							&& y >= -halfHeight)
					{
						CameraModel camMdl = cameraModels[cameraModelCount];
						camMdl.model = model_1;
						camMdl.surface = k;
						method294(cameraModelCount);
						camMdl.drawOrderVal = (model_1.getDistanceTo(p0) + model_1.getDistanceTo(surface[1])) / 2;
						cameraModelCount++;
					}
				}
			}

		}
		if (cameraModelCount == 0)
			return;
		lastCameraModelCount = cameraModelCount;
		sortModelsByDistToCam(cameraModels, 0, cameraModelCount - 1);
		method277(100, cameraModels, cameraModelCount);
		for (int i4 = 0; i4 < cameraModelCount; i4++)
		{
			CameraModel cm = cameraModels[i4];
			Model model = cm.model;
			int l = cm.surface;
			if (model == aModel)
			{
				int surface[] = model.surfaces[l];
				int p0 = surface[0];
				double x = model.xScreen[p0];
				double y = model.yScreen[p0];
				double modelDist = model.zDistToPointFromCameraView[p0];
				int modelWidth = (int) ((mobWidth[l] << cameraSizeInt) / modelDist * EngineHandle.SCALE_FACTOR);
				int modelHeight = (int) ((mobHeight[l] << cameraSizeInt) / modelDist * EngineHandle.SCALE_FACTOR);
				int p1 = surface[1];
				double length_y01 = y - model.yScreen[p1];
				double length_x10 = model.xScreen[p1] - x;
				double j11 = length_x10 * length_y01 / modelHeight  * EngineHandle.SCALE_FACTOR;
				double modelX = x - modelWidth / 2;
				double modelY = (halfHeight2 + y) - modelHeight;
				gameImage.doSpriteClip1((int)modelX + halfWidth2, (int)modelY,
						modelWidth, modelHeight, anIntArray416[l],
						(int)j11, (int) ((256 << cameraSizeInt) / modelDist));
				if (aBoolean389 && currentVisibleModelCount < maxVisibleModelCount)
				{
					modelX += (anIntArray422[l] << cameraSizeInt) / modelDist;
					if (mouseY >= modelY && mouseY <= modelY + modelHeight
							&& mouseX >= modelX && mouseX <= modelX + modelWidth
							&& !model.aBoolean263 && model.aByteArray259[l] == 0)
					{
						visibleModelsArray[currentVisibleModelCount] = model;
						visibleModelIntArray[currentVisibleModelCount] = l;
						currentVisibleModelCount++;
					}
				}
			}
			else
			{
				int pointsInSurfac = 0;
				int brightness = 0;
				int ptsPerCell = model.pointsPerCell[l];
				int surfaces[] = model.surfaces[l];
				if (model.lightSourceProjectToSurfNormal[l] != Model.INVISIBLE)
					if (cm.normalDirectionToCamera < 0)
						brightness = (int) (model.globalLight - model.lightSourceProjectToSurfNormal[l]);
					else
						brightness = (int) (model.globalLight + model.lightSourceProjectToSurfNormal[l]);
				for (int i = 0; i < ptsPerCell; i++)
				{
					int dataPoint = surfaces[i];
					xDistToPointFromCamera[i] = model.xDistToPointFromCameraView[dataPoint];
					zDistToPointFromCamera[i] = model.yDistToPointFromCameraView[dataPoint];
					yDistToPointFromCamera[i] = (int)model.zDistToPointFromCameraView[dataPoint];
					if (model.lightSourceProjectToSurfNormal[l] == Model.INVISIBLE)
						if (cm.normalDirectionToCamera < 0)
							brightness = (int) (model.globalLight - model.pointBrightness[dataPoint] + model.aByteArray233[dataPoint]);
						else
							brightness = (int) (model.globalLight + model.pointBrightness[dataPoint] + model.aByteArray233[dataPoint]);
					if (model.zDistToPointFromCameraView[dataPoint] >= planeOfViewOffsetFromCamera)
					{
						xScreen[pointsInSurfac] = model.xScreen[dataPoint];
						yScreen[pointsInSurfac] = model.yScreen[dataPoint];
						pointBrightness[pointsInSurfac] = brightness;
						/* should chage this to allow for other
						 * background colors than black */
						double dstnc = model.getDistanceTo(dataPoint);
						if (dstnc > fadeDist)
							pointBrightness[pointsInSurfac] += (dstnc - fadeDist) / fadeFactor;
						pointsInSurfac++;
					}
					else
					{
						int k9;
						if (i == 0)
							k9 = surfaces[ptsPerCell - 1];
						else
							k9 = surfaces[i - 1];
						if (model.zDistToPointFromCameraView[k9] >= planeOfViewOffsetFromCamera)
						{
							double k7 = model.zDistToPointFromCameraView[dataPoint] - model.zDistToPointFromCameraView[k9];
							double i5 = model.xDistToPointFromCameraView[dataPoint] - ((model.xDistToPointFromCameraView[dataPoint] - model.xDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / k7;
							double j6 = model.yDistToPointFromCameraView[dataPoint] - ((model.yDistToPointFromCameraView[dataPoint] - model.yDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / k7;
							int factr = 1 << cameraSizeInt;
							xScreen[pointsInSurfac] = i5 * factr / planeOfViewOffsetFromCamera;
							yScreen[pointsInSurfac] = j6 * factr / planeOfViewOffsetFromCamera;
							pointBrightness[pointsInSurfac] = brightness;
							pointsInSurfac++;
						}
						if (i == ptsPerCell - 1)
							k9 = surfaces[0];
						else
							k9 = surfaces[i + 1];
						if (model.zDistToPointFromCameraView[k9] >= planeOfViewOffsetFromCamera) {
							double l7 = model.zDistToPointFromCameraView[dataPoint] - model.zDistToPointFromCameraView[k9];
							double j5 = model.xDistToPointFromCameraView[dataPoint] - ((model.xDistToPointFromCameraView[dataPoint] - model.xDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / l7;
							double k6 = model.yDistToPointFromCameraView[dataPoint] - ((model.yDistToPointFromCameraView[dataPoint] - model.yDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / l7;
							int factr = 1 << cameraSizeInt;
							xScreen[pointsInSurfac] = j5 * factr / planeOfViewOffsetFromCamera;
							yScreen[pointsInSurfac] = k6 * factr / planeOfViewOffsetFromCamera;
							pointBrightness[pointsInSurfac] = brightness;
							pointsInSurfac++;
						}
					}
				}

				for (int i12 = 0; i12 < ptsPerCell; i12++)
				{
					if (pointBrightness[i12] < 0)
						pointBrightness[i12] = 0;
					else if (pointBrightness[i12] > 255)
						pointBrightness[i12] = 255;
				}

				if (pointsInSurfac == 0)
					continue;
				makeTriangle(0, 0, 0, pointsInSurfac, xScreen, yScreen, pointBrightness, model, l);
				if (modelYMax > modelYMin)
					applyColor(0, ptsPerCell, xDistToPointFromCamera, zDistToPointFromCamera,
							yDistToPointFromCamera, cm.color, model);
			}
		}

		aBoolean389 = false;
	}

	private void makeTriangle(int xMin, int xMax, int xMinBright, int pointsInSurface,
			double triangleX[], double triangleY[], int brightness[],
			Model model, int j1)
	{
		int i, j;
		int[] p_y = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; ++i)
			p_y[i] = (int)triangleY[i] + halfHeight2;
		int[] p_x = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; ++i)
			p_x[i] = (int)triangleX[i];
		int[] p_b = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; ++i)
			p_b[i] = brightness[i];
		int drawYMax = (halfHeight2 + halfHeight) - 1;
		int[] min_x = new int[pointsInSurface];
		int[] min_b = new int[pointsInSurface];
		int[] slope_x = new int[pointsInSurface];
		int[] slope_b = new int[pointsInSurface];
		int[] min_y = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; min_y[i++] = 9999999);
		int[] max_y = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; max_y[i++] = -9999999);

		for (i = 0; i < pointsInSurface; ++i)
		{
			j = (i + 1) % pointsInSurface;
			if (p_y[j] != p_y[i])
			{
				slope_x[i] = (p_x[j] - p_x[i] << 8) / (p_y[j] - p_y[i]);
				slope_b[i] = (p_b[j] - p_b[i] << 8) / (p_y[j] - p_y[i]);
				if (p_y[i] < p_y[j])
				{
					min_x[i] = p_x[i] << 8;
					min_b[i] = p_b[i] << 8;
					min_y[i] = p_y[i];
					max_y[i] = p_y[j];
				}
				else
				{
					min_x[i] = p_x[j] << 8;
					min_b[i] = p_b[j] << 8;
					min_y[i] = p_y[j];
					max_y[i] = p_y[i];
				}
				if (min_y[i] < 0)
				{
					min_x[i] -= slope_x[i] * min_y[i];
					min_b[i] -= slope_b[i] * min_y[i];
					min_y[i] = 0;
				}
				if (max_y[i] > drawYMax)
					max_y[i] = drawYMax;
			}
		}
		modelYMin = min_y[0];
		for (i = 1; i < pointsInSurface; ++i)
			if (min_y[i] < modelYMin)
				modelYMin = min_y[i];
		modelYMax = max_y[0];
		for (i = 1; i < pointsInSurface; ++i)
			if (max_y[i] > modelYMax)
				modelYMax = max_y[i];

		int xMaxBright = 0;
		for (i = modelYMin; i < modelYMax; i++)
		{
			xMin = 9999999;
			xMax = -9999999;
			for (j = 0; j < pointsInSurface; ++j)
			{
				if (i >= min_y[j] && i < max_y[j])
				{
					if (min_x[j] < xMin)
					{
						xMin = (int) min_x[j];
						xMinBright = (int) min_b[j];
					}
					if (min_x[j] > xMax)
					{
						xMax = (int) min_x[j];
						xMaxBright = (int) min_b[j];
					}
					min_x[j] += slope_x[j];
					min_b[j] += slope_b[j];
				}
			}
			CameraVariables cameraVariables_6 = cameraVariables[i];
			cameraVariables_6.leftX = xMin;
			cameraVariables_6.rightX = xMax;
			cameraVariables_6.leftXBright = xMinBright;
			cameraVariables_6.rightXBright = xMaxBright;
		}

		if (modelYMin < halfHeight2 - halfHeight)
			modelYMin = halfHeight2 - halfHeight;

		if (aBoolean389 && currentVisibleModelCount < maxVisibleModelCount
				&& mouseY >= modelYMin && mouseY < modelYMax)
		{
			CameraVariables cameraVariables_1 = cameraVariables[mouseY];
			if (mouseX >= cameraVariables_1.leftX >> 8
					&& mouseX <= cameraVariables_1.rightX >> 8
					&& cameraVariables_1.leftX <= cameraVariables_1.rightX
					&& !model.aBoolean263 && model.aByteArray259[j1] == 0)
			{
				visibleModelsArray[currentVisibleModelCount] = model;
				visibleModelIntArray[currentVisibleModelCount] = j1;
				currentVisibleModelCount++;
			}
		}
	}

	private void applyColor(
			int imgPixXStart, int k, double xDist[],
			double zDist[], double yDist[], int color,
			Model model)
	{
		if (color == -2)
			return; // invisible
		if (color >= 0)
		{ // color is a texture
			if (color >= nbrTextures)
				color = 0;
			setTexturePixels(color);
			--k;
			int[] p_x = {(int)xDist[0], (int)xDist[0] - (int)xDist[1], (int)xDist[k] - (int)xDist[0]};
			int[] p_z = {(int)zDist[0], (int)zDist[0] - (int)zDist[1], (int)zDist[k] - (int)zDist[0]};
			int[] p_y = {(int)yDist[0], (int)yDist[0] - (int)yDist[1], (int)yDist[k] - (int)yDist[0]};
			int nk_y = p_x[2] * p_z[0] - p_z[2] * p_x[0] << 5 + textureSize[color];
			int nk_x = p_z[2] * p_y[0] - p_y[2] * p_z[0] << (5 - cameraSizeInt) + 4 + textureSize[color];
			int nk_z = p_y[2] * p_x[0] - p_x[2] * p_y[0] << (5 - cameraSizeInt) + textureSize[color];
			int n1_y = p_x[1] * p_z[0] - p_z[1] * p_x[0] << 5 + textureSize[color];
			int n1_x = p_z[1] * p_y[0] - p_y[1] * p_z[0] << (5 - cameraSizeInt) + 4 + textureSize[color];
			int n1_z = p_y[1] * p_x[0] - p_x[1] * p_y[0] << (5 - cameraSizeInt) + textureSize[color];
			// normal pointing up from the surface
			int n_y = p_z[1] * p_x[2] - p_x[1] * p_z[2] << 5;
			int n_x = p_y[1] * p_z[2] - p_z[1] * p_y[2] << (5 - cameraSizeInt) + 4;
			int n_z = p_x[1] * p_y[2] - p_y[1] * p_x[2] >> cameraSizeInt - 5;
			
			int k14 = nk_x >> 4;
			int i15 = n1_x >> 4;
			int k15 = n_x >> 4;
			int i16 = modelYMin - halfHeight2;
			int imgPixSkip = width;
			int imgPixRow = halfWidth2 + modelYMin * imgPixSkip;
			byte rowStep = 1;
			nk_y += nk_z * i16;
			n1_y += n1_z * i16;
			n_y += n_z * i16;
			if (f1Toggle)
			{
				if ((modelYMin & 1) == 1)
				{
					modelYMin++;
					nk_y += nk_z;
					n1_y += n1_z;
					n_y += n_z;
					imgPixRow += imgPixSkip;
				}
				nk_z <<= 1;
				n1_z <<= 1;
				n_z <<= 1;
				imgPixSkip <<= 1;
				rowStep = 2;
			}
			boolean trnspar = model.transparentTexture;
			boolean seethu = seethrough[color];
			int nSkip = 4; // this has to be 4 for now.
			for (int i = modelYMin; i < modelYMax; i += rowStep)
			{
				CameraVariables camVar = cameraVariables[i];
				imgPixXStart = camVar.leftX >> 8;
				int imgPixXEnd = camVar.rightX >> 8;
				int lineLength = imgPixXEnd - imgPixXStart;
				if (lineLength <= 0)
				{
					nk_y += nk_z;
					n1_y += n1_z;
					n_y += n_z;
					imgPixRow += imgPixSkip;
				}
				else
				{
					int gradStart = camVar.leftXBright;
					int gradStep = (camVar.rightXBright - gradStart) / lineLength;
					if (imgPixXStart < -halfWidth)
					{
						gradStart += (-halfWidth - imgPixXStart) * gradStep;
						imgPixXStart = -halfWidth;
						lineLength = imgPixXEnd - imgPixXStart;
					}
					if (imgPixXEnd > halfWidth)
						lineLength = halfWidth - imgPixXStart;
					drawTexture(
							imagePixelArray, texturePixels[color], 0, 0,
							nk_y + k14 * imgPixXStart,
							n1_y + i15 * imgPixXStart,
							n_y + k15 * imgPixXStart,
							nk_x, n1_x, n_x, lineLength,
							imgPixRow + imgPixXStart, gradStart, gradStep,
							nSkip, trnspar, seethu, textureSize[color]);
					nk_y += nk_z;
					n1_y += n1_z;
					n_y += n_z;
					imgPixRow += imgPixSkip;
				}
			}
			return;
		}
		// color is a color
		for (int i = 0; i < nbrColors; i++)
		{
			if (colorArray[i] == color)
			{ // color has been used before
				colorGradient = colorGradientArray[i];
				break;
			}
			if (i == nbrColors - 1)
			{ // color has not been used before
				int newIdx = (int) (Math.random() * (double) nbrColors);
				colorArray[newIdx] = color;
				color = -1 - color; // convert to color
				int red = (color >> 10 & 0x1f) * 8;
				int green = (color >> 5 & 0x1f) * 8;
				int blue = (color & 0x1f) * 8;
				for (int j4 = 0; j4 < 256; j4++)
				{
					int j6 = j4 * j4;
					int tmpRed = (red * j6) >> 16;
					int tmpGreen = (green * j6) >> 16;
					int tmpBlue = (blue * j6) >> 16;
					colorGradientArray[newIdx][255 - j4] = (tmpRed << 16) + (tmpGreen << 8) + tmpBlue;
				}
				colorGradient = colorGradientArray[newIdx];
			}
		}

		int imgPixSkip = width;
		int imgPixRow = halfWidth2 + modelYMin * imgPixSkip;
		byte yStep = 1;
		if (f1Toggle)
		{
			if ((modelYMin & 1) == 1)
			{
				modelYMin++;
				imgPixRow += imgPixSkip;
			}
			imgPixSkip <<= 1;
			yStep = 2;
		}

		boolean transparent = model.transparent;
		int nGradSteps = gradient2Step ? 2 : 4;  // lower increases resolution
		for (int i = modelYMin; i < modelYMax; i += yStep)
		{
			CameraVariables camVar = cameraVariables[i];
			imgPixXStart = camVar.leftX >> 8;
			int imgPixXEnd = camVar.rightX >> 8;
			int lineLength = imgPixXEnd - imgPixXStart;
			if (lineLength <= 0)
				imgPixRow += imgPixSkip;
			else
			{
				int gradStart = camVar.leftXBright;
				int gradEnd = (camVar.rightXBright - gradStart) / lineLength;
				if (imgPixXStart < -halfWidth)
				{
					gradStart += (-halfWidth - imgPixXStart) * gradEnd;
					imgPixXStart = -halfWidth;
					lineLength = imgPixXEnd - imgPixXStart;
				}
				if (imgPixXEnd > halfWidth)
					lineLength = halfWidth - imgPixXStart;
				drawColorLine(
						imagePixelArray, -lineLength,
						imgPixRow + imgPixXStart, 0,
						colorGradient, gradStart, gradEnd,
						nGradSteps, transparent);
				imgPixRow += imgPixSkip;
			}
		}
	}

	private static void drawTexture(
			int pixelArray[], int texturePixels[],
			int xTexture, int yTexture, int smthXTexture,
			int smthYTexture, int smthDivision,
			int smthXTextureStep, int smthYTextureStep,
			int smthDivisionStep, int length, int offset,
			int shadeOffset, int shadeStep,
			int nSteps, boolean transparent, boolean seethrough, int size)
	{
		if (length <= 0)
			return;
		int mask = transparent ? 0x7f7f7f : 0x0;
		shadeStep <<= (nSteps >> 1);
		int lastRow = (1 << 2*size) - (1 << size);
		int txtrStep1 = (1 << 2*size) - 1;

		int i3 = 0;
		int j3 = 0;
		if (smthDivision != 0)
		{
			xTexture = smthXTexture / smthDivision << size;
			yTexture = smthYTexture / smthDivision << size;
		}
		if (xTexture < 0)
			xTexture = 0;
		else if (xTexture > lastRow)
			xTexture = lastRow;
		smthXTexture += smthXTextureStep;
		smthYTexture += smthYTextureStep;
		smthDivision += smthDivisionStep;

		if (smthDivision != 0)
		{
			i3 = smthXTexture / smthDivision << size;
			j3 = smthYTexture / smthDivision << size;
		}
		if (i3 < 0)
			i3 = 0;
		else if (i3 > lastRow)
			i3 = lastRow;
		int xTextureStep = i3 - xTexture >> 4;
		int yTextureStep = j3 - yTexture >> 4;
		int pixelColor = 0, color, shadeVal, shadeMod;
		for (int j4 = length >> 4; j4 > 0; j4--)
		{
			for (int i = 0; i < 4; ++i)
			{
				xTexture = (xTexture & txtrStep1);
				shadeOffset += shadeStep;
				for (int j = 0; j < 4; ++j)
				{
					color = texturePixels[(yTexture & lastRow) + (xTexture >> size)];
					shadeVal = (255-(shadeOffset >> 8));
					shadeMod = shadeVal*shadeVal;
					pixelColor = (((color >> 16 & 0xff) * shadeMod) & 0xff0000)
							+ ((((color >> 8 & 0xff) * shadeMod) & 0xff0000) >> 8)
							+ (((color & 0xff) * shadeMod) >> 16);
					if (!seethrough || pixelColor != 0)
						pixelArray[offset] = pixelColor + (pixelArray[offset] >> 1 & mask);
					++offset;
					xTexture += xTextureStep;
					yTexture += yTextureStep;
				}
			}
			xTexture = i3;
			yTexture = j3;
			smthXTexture += smthXTextureStep;
			smthYTexture += smthYTextureStep;
			smthDivision += smthDivisionStep;
			if (smthDivision != 0)
			{
				i3 = smthXTexture / smthDivision << size;
				j3 = smthYTexture / smthDivision << size;
			}
			if (i3 < 0)
				i3 = 0;
			else if (i3 > lastRow)
				i3 = lastRow;
			xTextureStep = i3 - xTexture >> 4;
			yTextureStep = j3 - yTexture >> 4;
		}

		for (int k4 = 0; k4 < (length & 0xf); k4++)
		{
			if ((k4 & 3) == 0)
			{
				xTexture = (xTexture & txtrStep1);
				shadeOffset += shadeStep;
			}
			color = texturePixels[(yTexture & lastRow) + (xTexture >> size)];
			shadeVal = (255-(shadeOffset >> 8));
			shadeMod = shadeVal*shadeVal;
			pixelColor = (((color >> 16 & 0xff) * shadeMod) & 0xff0000)
					+ ((((color >> 8 & 0xff) * shadeMod) & 0xff0000) >> 8)
					+ (((color & 0xff) * shadeMod) >> 16);
			if (!seethrough
					|| pixelColor != 0)
				pixelArray[offset] = pixelColor + (pixelArray[offset] >> 1 & mask);
			++offset;
			xTexture += xTextureStep;
			yTexture += yTextureStep;
		}
	}

	private static void drawColorLine(
			int imagePixels[], int length, int offset, int pixelColor,
			int colorGradient[], int gradColOffs, int gradColStep,
			int nSteps, boolean transparent)
	{
		if (length >= 0)
			return;
		int mask = transparent ? 0x7f7f7f : 0x0;
		gradColStep <<= (nSteps >> 1);
		pixelColor = colorGradient[gradColOffs >> 8 & 0xff];
		gradColOffs += gradColStep;

		int step = length / (4 * nSteps);
		for (int i = step; i < 0; i++)
		{
			for (int j = 0; j < 4; ++j)
			{
				for (int k = 0; k < nSteps; ++k)
					imagePixels[offset++] = pixelColor;
				pixelColor = colorGradient[gradColOffs >> 8 & 0xff]
						+ (imagePixels[offset] >> 1 & mask);
				gradColOffs += gradColStep;
			}
		}

		step = -(length % (4 * nSteps));
		int end = nSteps-1;
		for (int i = 0; i < step; i++)
		{
			imagePixels[offset++] = pixelColor;
			if ((i & end) == end)
			{
				pixelColor = colorGradient[gradColOffs >> 8 & 0xff]
						+ (imagePixels[offset] >> 1 & mask);
				gradColOffs += gradColStep;
			}
		}
	}

	public void setCamera(double playerX, double playerZ, double playerY,
			int xRot, int zRot, int yRot, double camYStart, double cameraZoom)
	{
		xRot &= 0x3ff;
		zRot &= 0x3ff;
		yRot &= 0x3ff;
		if (!mc.getFreeCamera())
		{
			cameraXRot = 1024 - xRot & 0x3ff;
			cameraZRot = 1024 - zRot & 0x3ff;
			cameraYRot = 1024 - yRot & 0x3ff;
		}
		double cameraXOffset = 0;
		double cameraZOffset = 0;
		double cameraYOffset = camYStart;
		
		if (xRot != 0)
		{
			double sin = sin1024[xRot];
			double cos = cos1024[xRot];
			double tmp = cameraZOffset * cos - cameraYOffset * sin;
			cameraYOffset = cameraYOffset * cos + cameraZOffset * sin;
			cameraZOffset = tmp;
		}
		if (zRot != 0)
		{
			double sin = sin1024[zRot];
			double cos = cos1024[zRot];
			double camXOffsTmp = cameraYOffset * sin + cameraXOffset * cos;
			cameraYOffset = cameraYOffset * cos - cameraXOffset * sin;
			cameraXOffset = camXOffsTmp;
		}
		
		if (yRot != 0)
		{
			double sin = sin1024[yRot];
			double cos = cos1024[yRot];
			double tmp = cameraZOffset * sin + cameraXOffset * cos;
			cameraZOffset = cameraZOffset * cos - cameraXOffset * sin;
			cameraXOffset = tmp;
		}
		if (!mc.getFreeCamera())
		{
			cameraXPos = playerX - cameraXOffset*cameraZoom;
			cameraZPos = playerZ - cameraZOffset*cameraZoom;
			cameraYPos = playerY - cameraYOffset*cameraZoom;
		}
	}

	private void method293(int modelIdx) {
		CameraModel cameraModel = cameraModels[modelIdx];
		Model model = cameraModel.model;
		int surfaceIdx = cameraModel.surface;
		int surfaces[] = model.surfaces[surfaceIdx];
		int pointsInCell = model.pointsPerCell[surfaceIdx];
		double x0 = model.xDistToPointFromCameraView[surfaces[0]];
		double y0 = model.yDistToPointFromCameraView[surfaces[0]];
		double z0 = model.zDistToPointFromCameraView[surfaces[0]];
		double u_x = model.xDistToPointFromCameraView[surfaces[1]] - x0;
		double u_y = model.yDistToPointFromCameraView[surfaces[1]] - y0;
		double u_z = model.zDistToPointFromCameraView[surfaces[1]] - z0;
		double v_x = model.xDistToPointFromCameraView[surfaces[2]] - x0;
		double v_y = model.yDistToPointFromCameraView[surfaces[2]] - y0;
		double v_z = model.zDistToPointFromCameraView[surfaces[2]] - z0;
		double n_x = u_y * v_z - v_y * u_z;
		double n_y = u_z * v_x - v_z * u_x;
		double n_z = u_x * v_y - v_x * u_y;
		model.normalLength[surfaceIdx] = Math.sqrt(n_x*n_x + n_y*n_y + n_z*n_z);
		cameraModel.normalDirectionToCamera = x0*n_x + y0*n_y + z0*n_z;
		cameraModel.xNormal = n_x;
		cameraModel.zNormal = n_y;
		cameraModel.yNormal = n_z;
		double zmin = model.zDistToPointFromCameraView[surfaces[0]];
		double zmax = zmin;
		double xmin = model.xScreen[surfaces[0]];
		double xmax = xmin;
		double ymin = model.yScreen[surfaces[0]];
		double ymax = ymin;
		for (int i = 1; i < pointsInCell; i++)
		{
			double dist = model.zDistToPointFromCameraView[surfaces[i]];
			if (dist > zmax)
				zmax = dist;
			else if (dist < zmin)
				zmin = dist;
			dist = (int)model.xScreen[surfaces[i]];
			if (dist > xmax)
				xmax = dist;
			else if (dist < xmin)
				xmin = dist;
			dist = (int)model.yScreen[surfaces[i]];
			if (dist > ymax)
				ymax = dist;
			else if (dist < ymin)
				ymin = dist;
		}

		cameraModel.zMin = (int)zmin;
		cameraModel.zMax = (int)zmax;
		cameraModel.xMin = (int)xmin;
		cameraModel.xMax = (int)xmax;
		cameraModel.yMin = (int)ymin;
		cameraModel.yMax = (int)ymax;
	}

	private void method294(int modelIdx)
	{
		CameraModel camMdl = cameraModels[modelIdx];
		Model model = camMdl.model;
		int surface = camMdl.surface;
		int surfaces[] = model.surfaces[surface];
		double n_x = 0D;
		double n_z = 0D;
		double n_y = 1D;
		double u0_x = model.xDistToPointFromCameraView[surfaces[0]];
		double u0_y = model.yDistToPointFromCameraView[surfaces[0]];
		double u0_z = model.zDistToPointFromCameraView[surfaces[0]];
		model.normalLength[surface] = 1D;
		camMdl.normalDirectionToCamera = u0_x * n_x + u0_y * n_z + u0_z * n_y;
		camMdl.xNormal = n_x;
		camMdl.zNormal = n_z;
		camMdl.yNormal = n_y;
		double zmin = model.zDistToPointFromCameraView[surfaces[0]];
		double zmax = zmin;
		double xmin = model.xScreen[surfaces[0]];
		double xmax = xmin;
		if (model.xScreen[surfaces[1]] < xmin)
			xmin = model.xScreen[surfaces[1]];
		else
			xmax = model.xScreen[surfaces[1]];
		double ymin = model.yScreen[surfaces[1]];
		double ymax = model.yScreen[surfaces[0]];
		double dist = model.zDistToPointFromCameraView[surfaces[1]];
		if (dist > zmax)
			zmax = dist;
		else if (dist < zmin)
			zmin = dist;
		dist = model.xScreen[surfaces[1]];
		if (dist > xmax)
			xmax = dist;
		else if (dist < xmin)
			xmin = dist;
		dist = model.yScreen[surfaces[1]];
		if (dist > ymax)
			ymax = dist;
		else if (dist < ymin)
			ymin = dist;
		camMdl.zMin = (int)zmin;
		camMdl.zMax = (int)zmax;
		camMdl.xMin = (int)xmin - 20;
		camMdl.xMax = (int)xmax + 20;
		camMdl.yMin = (int)ymin;
		camMdl.yMax = (int)ymax;
	}

	private boolean method295(CameraModel cm_0, CameraModel cm_1)
	{
		if (cm_0.xMin >= cm_1.xMax || cm_1.xMin >= cm_0.xMax)
			return true;
		if (cm_0.yMin >= cm_1.yMax || cm_1.yMin >= cm_0.yMax)
			return true;
		if (cm_0.zMin >= cm_1.zMax || cm_1.zMin > cm_0.zMax)
			return true;
		Model model_0 = cm_0.model;
		Model model_1 = cm_1.model;
		int surfIdx_0 = cm_0.surface;
		int surfIdx_1 = cm_1.surface;
		int surfaces_0[] = model_0.surfaces[surfIdx_0];
		int surfaces_1[] = model_1.surfaces[surfIdx_1];
		int pointsInCell_0 = model_0.pointsPerCell[surfIdx_0];
		int pointsInCell_1 = model_1.pointsPerCell[surfIdx_1];
		double u0_x_1 = model_1.xDistToPointFromCameraView[surfaces_1[0]];
		double u0_y_1 = model_1.yDistToPointFromCameraView[surfaces_1[0]];
		double u0_z_1 = model_1.zDistToPointFromCameraView[surfaces_1[0]];
		double n_x = cm_1.xNormal;
		double n_z = cm_1.zNormal;
		double n_y = cm_1.yNormal;
		double normLen_1 = model_1.normalLength[surfIdx_1];
		double directionToCam = cm_1.normalDirectionToCamera;
		boolean flag = false;
		for (int i = 0; i < pointsInCell_0; i++)
		{
			int point = surfaces_0[i];
			// dot
			double i2 = (u0_x_1 - model_0.xDistToPointFromCameraView[point]) * n_x
					+ (u0_y_1 - model_0.yDistToPointFromCameraView[point]) * n_z
					+ (u0_z_1 - model_0.zDistToPointFromCameraView[point]) * n_y;
			if ((i2 >= -normLen_1 || directionToCam >= 0)
					&& (i2 <= normLen_1 || directionToCam <= 0))
				continue;
			flag = true;
			break;
		}
		if (!flag)
			return true;

		u0_x_1 = model_0.xDistToPointFromCameraView[surfaces_0[0]];
		u0_y_1 = model_0.yDistToPointFromCameraView[surfaces_0[0]];
		u0_z_1 = model_0.zDistToPointFromCameraView[surfaces_0[0]];
		n_x = cm_0.xNormal;
		n_z = cm_0.zNormal;
		n_y = cm_0.yNormal;
		normLen_1 = model_0.normalLength[surfIdx_0];
		directionToCam = cm_0.normalDirectionToCamera;
		flag = false;
		for (int i = 0; i < pointsInCell_1; i++)
		{
			int point = surfaces_1[i];
			double j2 = (u0_x_1 - model_1.xDistToPointFromCameraView[point]) * n_x
					+ (u0_y_1 - model_1.yDistToPointFromCameraView[point]) * n_z
					+ (u0_z_1 - model_1.zDistToPointFromCameraView[point]) * n_y;
			if ((j2 >= -normLen_1 || directionToCam <= 0)
					&& (j2 <= normLen_1 || directionToCam >= 0))
				continue;
			flag = true;
			break;
		}
		if (!flag)
			return true;

		int ai2[];
		int ai3[];
		if (pointsInCell_0 == 2)
		{
			ai2 = new int[4];
			ai3 = new int[4];
			int p0_0 = surfaces_0[0];
			int p1_0 = surfaces_0[1];
			ai2[0] = (int)model_0.xScreen[p0_0] - 20;
			ai2[1] = (int)model_0.xScreen[p1_0] - 20;
			ai2[2] = (int)model_0.xScreen[p1_0] + 20;
			ai2[3] = (int)model_0.xScreen[p0_0] + 20;
			ai3[0] = ai3[3] = (int)model_0.yScreen[p0_0];
			ai3[1] = ai3[2] = (int)model_0.yScreen[p1_0];
		}
		else
		{
			ai2 = new int[pointsInCell_0];
			ai3 = new int[pointsInCell_0];
			for (int j5 = 0; j5 < pointsInCell_0; j5++) {
				int i6 = surfaces_0[j5];
				ai2[j5] = (int)model_0.xScreen[i6];
				ai3[j5] = (int)model_0.yScreen[i6];
			}

		}
		int ai4[];
		int ai5[];
		if (pointsInCell_1 == 2) {
			ai4 = new int[4];
			ai5 = new int[4];
			int p0_1 = surfaces_1[0];
			int p1_1 = surfaces_1[1];
			ai4[0] = (int)model_1.xScreen[p0_1] - 20;
			ai4[1] = (int)model_1.xScreen[p1_1] - 20;
			ai4[2] = (int)model_1.xScreen[p1_1] + 20;
			ai4[3] = (int)model_1.xScreen[p0_1] + 20;
			ai5[0] = ai5[3] = (int)model_1.yScreen[p0_1];
			ai5[1] = ai5[2] = (int)model_1.yScreen[p1_1];
		} else {
			ai4 = new int[pointsInCell_1];
			ai5 = new int[pointsInCell_1];
			for (int l5 = 0; l5 < pointsInCell_1; l5++) {
				int j6 = surfaces_1[l5];
				ai4[l5] = (int)model_1.xScreen[j6];
				ai5[l5] = (int)model_1.yScreen[j6];
			}

		}
		return !method309(ai2, ai3, ai4, ai5);
	}

	private boolean method296(CameraModel cameraModel_0, CameraModel cameraModel_1) {
		Model model_0 = cameraModel_0.model;
		Model model_1 = cameraModel_1.model;
		int surfaceIdx_0 = cameraModel_0.surface;
		int surfaceIdx_1 = cameraModel_1.surface;
		int surface_0[] = model_0.surfaces[surfaceIdx_0];
		int surface_1[] = model_1.surfaces[surfaceIdx_1];
		int ptsPerCell_0 = model_0.pointsPerCell[surfaceIdx_0];
		int ptsPerCell_1 = model_1.pointsPerCell[surfaceIdx_1];
		double a = model_1.xDistToPointFromCameraView[surface_1[0]];
		double b = model_1.yDistToPointFromCameraView[surface_1[0]];
		double c = model_1.zDistToPointFromCameraView[surface_1[0]];
		double n_x = cameraModel_1.xNormal;
		double n_z = cameraModel_1.zNormal;
		double n_y = cameraModel_1.yNormal;
		double k3 = model_1.normalLength[surfaceIdx_1];
		double l3 = cameraModel_1.normalDirectionToCamera;
		boolean flag = false;
		for (int i4 = 0; i4 < ptsPerCell_0; i4++) {
			int i1 = surface_0[i4];
			double k1 = (a - model_0.xDistToPointFromCameraView[i1]) * n_x
					+ (b - model_0.yDistToPointFromCameraView[i1]) * n_z
					+ (c - model_0.zDistToPointFromCameraView[i1]) * n_y;
			if ((k1 >= -k3 || l3 >= 0) && (k1 <= k3 || l3 <= 0))
				continue;
			flag = true;
			break;
		}

		if (!flag)
			return true;
		a = model_0.xDistToPointFromCameraView[surface_0[0]];
		b = model_0.yDistToPointFromCameraView[surface_0[0]];
		c = model_0.zDistToPointFromCameraView[surface_0[0]];
		n_x = cameraModel_0.xNormal;
		n_z = cameraModel_0.zNormal;
		n_y = cameraModel_0.yNormal;
		k3 = model_0.normalLength[surfaceIdx_0];
		l3 = cameraModel_0.normalDirectionToCamera;
		flag = false;
		for (int j4 = 0; j4 < ptsPerCell_1; j4++) {
			int j1 = surface_1[j4];
			double l1 = (a - model_1.xDistToPointFromCameraView[j1]) * n_x
					+ (b - model_1.yDistToPointFromCameraView[j1]) * n_z
					+ (c - model_1.zDistToPointFromCameraView[j1]) * n_y;
			if ((l1 >= -k3 || l3 <= 0) && (l1 <= k3 || l3 >= 0))
				continue;
			flag = true;
			break;
		}
		return !flag;
	}

	public void method297(int i, int j, int k) {
		nbrTextures = i;
		colorIndexArray = new byte[i][];
		colorsArray = new int[i][];
		textureSize = new int[i];
		seethrough = new boolean[i];
		texturePixels = new int[i][];
	}

	public void method298(int i, byte clrIdxArray[], int clrArray[], int j) {
		colorIndexArray[i] = clrIdxArray;
		colorsArray[i] = clrArray;
		textureSize[i] = getTextureSize(j);
		seethrough[i] = false;
		texturePixels[i] = null;
		setTexturePixels(i);
	}
	
	private int getTextureSize(int size)
	{
		for (int i = 0; i < Integer.SIZE; ++i)
			if (size >> (i+1) == 0)
				return i;
		return 0;
	}

	public void setTexturePixels(int color)
	{
		if (color < 0 || texturePixels[color] != null)
			return;
		texturePixels[color] = new int[1 << 2*textureSize[color]];
		fillTexturePixels(color);
	}

	private void fillTexturePixels(int texture)
	{
		int width = 1 << textureSize[texture];
		int txtrPixels[] = texturePixels[texture];
		int i = 0;
		for (int y = 0; y < width; y++)
		{
			for (int x = 0; x < width; x++)
			{
				int index = colorIndexArray[texture][x + y * width] & 0xff;
				int color = colorsArray[texture][index];
				color &= 0xf8f8f8;
				if (color == 0)
					color = 1;
				else if (color == 0xf800f8)
				{
					color = 0;
					seethrough[texture] = true;
				}
				txtrPixels[i++] = color;
			}
		}
	}

	public void animateTexture(int texture)
	{
		if (texturePixels[texture] == null)
			return;
		int sideLen = 1 << textureSize[texture];
		int sizeReverse = sideLen-1;
		int rowsReverse = sideLen*sizeReverse;
		int txtrPixels[] = texturePixels[texture];
		for (int row = 0; row < sideLen; row++)
		{
			int rowReverse = row + rowsReverse;
			int l = txtrPixels[rowReverse];
			for (int j1 = 0; j1 < sizeReverse; j1++) {
				txtrPixels[rowReverse] = txtrPixels[rowReverse - sideLen];
				rowReverse -= sideLen;
			}
			texturePixels[texture][rowReverse] = l;
		}
	}

	public int color16bitTo24bit(int color16bit) {
		if (color16bit == Model.INVISIBLE)
			return 0;
		setTexturePixels(color16bit);
		if (color16bit >= 0)
			return texturePixels[color16bit][0];
		if (color16bit < 0) {
			color16bit = -(color16bit + 1);
			int red = color16bit >> 10 & 0x1f;
			int green = color16bit >> 5 & 0x1f;
			int blue = color16bit & 0x1f;
			return (red << 19) + (green << 11) + (blue << 3);
		} else {
			return 0;
		}
	}

	public void setModelLightSources(int x, int z, int y)
	{
		if (x == 0 && z == 0 && y == 0)
		{
			x = light_x;
			z = light_z;
			y = light_y;
		}
		for (int l = 0; l < modelCount; l++)
			modelArray[l].setLightsource(x, z, y);

	}

	public void setLightAndSource(int globalLight, int featureLight, int xSrc, int zSrc, int ySrc) {
		if (xSrc == 0 && zSrc == 0 && ySrc == 0)
		{
			xSrc = Camera.light_x;
			zSrc = Camera.light_z;
			ySrc = Camera.light_y;
		}
		for (int j1 = 0; j1 < modelCount; j1++)
			modelArray[j1].setLightAndSource(globalLight, featureLight,
					xSrc, zSrc, ySrc);

	}

	/* Converts 24 bit rgb to negative 15 bit */
	public static int getGroundColorVal(int red, int green, int blue)
	{
		return -1 - ((red & 0xf8) << 7 ) - ((green & 0xf8) << 2 ) - (blue >> 3);
	}

	public int method306(int i, int j, int k, int l, int i1) {
		if (l == j)
			return i;
		else
			return i + 0*((k - i) * (i1 - j)) / (l - j);
	}

	public boolean method307(int i, int j, int k, int l, boolean flag) {
		if (flag && i <= k || i < k) {
			if (i > l)
				return true;
			if (j > k)
				return true;
			if (j > l)
				return true;
			return !flag;
		}
		if (i < l)
			return true;
		if (j < k)
			return true;
		if (j < l)
			return true;
		else
			return flag;
	}

	public boolean method308(int i, int j, int k, boolean flag) {
		if (flag && i <= k || i < k) {
			if (j > k)
				return true;
			return !flag;
		}
		if (j < k)
			return true;
		else
			return flag;
	}

	public boolean method309(int ai[], int ai1[], int ai2[], int ai3[]) {
		int i = ai.length;
		int j = ai2.length;
		byte byte0 = 0;
		int i20;
		int k20 = i20 = ai1[0];
		int k = 0;
		int j20;
		int l20 = j20 = ai3[0];
		int i1 = 0;
		for (int i21 = 1; i21 < i; i21++)
			if (ai1[i21] < i20) {
				i20 = ai1[i21];
				k = i21;
			} else if (ai1[i21] > k20)
				k20 = ai1[i21];

		for (int j21 = 1; j21 < j; j21++)
			if (ai3[j21] < j20) {
				j20 = ai3[j21];
				i1 = j21;
			} else if (ai3[j21] > l20)
				l20 = ai3[j21];

		if (j20 >= k20)
			return false;
		if (i20 >= l20)
			return false;
		int l;
		int j1;
		boolean flag;
		if (ai1[k] < ai3[i1]) {
			for (l = k; ai1[l] < ai3[i1]; l = (l + 1) % i)
				;
			for (; ai1[k] < ai3[i1]; k = ((k - 1) + i) % i)
				;
			int k1 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[i1]);
			int k6 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[i1]);
			int l10 = ai2[i1];
			flag = (k1 < l10) | (k6 < l10);
			if (method308(k1, k6, l10, flag))
				return true;
			j1 = (i1 + 1) % j;
			i1 = ((i1 - 1) + j) % j;
			if (k == l)
				byte0 = 1;
		} else {
			for (j1 = i1; ai3[j1] < ai1[k]; j1 = (j1 + 1) % j)
				;
			for (; ai3[i1] < ai1[k]; i1 = ((i1 - 1) + j) % j)
				;
			int l1 = ai[k];
			int i11 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[k]);
			int l15 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[k]);
			flag = (l1 < i11) | (l1 < l15);
			if (method308(i11, l15, l1, !flag))
				return true;
			l = (k + 1) % i;
			k = ((k - 1) + i) % i;
			if (i1 == j1)
				byte0 = 2;
		}
		while (byte0 == 0) if (ai1[k] < ai1[l]) {
			if (ai1[k] < ai3[i1]) {
				if (ai1[k] < ai3[j1]) {
					int i2 = ai[k];
					int l6 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai1[k]);
					int j11 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[k]);
					int i16 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[k]);
					if (method307(i2, l6, j11, i16, flag))
						return true;
					k = ((k - 1) + i) % i;
					if (k == l)
						byte0 = 1;
				} else {
					int j2 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[j1]);
					int i7 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[j1]);
					int k11 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai3[j1]);
					int j16 = ai2[j1];
					if (method307(j2, i7, k11, j16, flag))
						return true;
					j1 = (j1 + 1) % j;
					if (i1 == j1)
						byte0 = 2;
				}
			} else if (ai3[i1] < ai3[j1]) {
				int k2 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[i1]);
				int j7 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[i1]);
				int l11 = ai2[i1];
				int k16 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai3[i1]);
				if (method307(k2, j7, l11, k16, flag))
					return true;
				i1 = ((i1 - 1) + j) % j;
				if (i1 == j1)
					byte0 = 2;
			} else {
				int l2 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[j1]);
				int k7 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[j1]);
				int i12 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai3[j1]);
				int l16 = ai2[j1];
				if (method307(l2, k7, i12, l16, flag))
					return true;
				j1 = (j1 + 1) % j;
				if (i1 == j1)
					byte0 = 2;
			}
		} else if (ai1[l] < ai3[i1]) {
			if (ai1[l] < ai3[j1]) {
				int i3 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai1[l]);
				int l7 = ai[l];
				int j12 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[l]);
				int i17 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[l]);
				if (method307(i3, l7, j12, i17, flag))
					return true;
				l = (l + 1) % i;
				if (k == l)
					byte0 = 1;
			} else {
				int j3 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[j1]);
				int i8 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[j1]);
				int k12 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai3[j1]);
				int j17 = ai2[j1];
				if (method307(j3, i8, k12, j17, flag))
					return true;
				j1 = (j1 + 1) % j;
				if (i1 == j1)
					byte0 = 2;
			}
		} else if (ai3[i1] < ai3[j1]) {
			int k3 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[i1]);
			int j8 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[i1]);
			int l12 = ai2[i1];
			int k17 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai3[i1]);
			if (method307(k3, j8, l12, k17, flag))
				return true;
			i1 = ((i1 - 1) + j) % j;
			if (i1 == j1)
				byte0 = 2;
		} else {
			int l3 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[j1]);
			int k8 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[j1]);
			int i13 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai3[j1]);
			int l17 = ai2[j1];
			if (method307(l3, k8, i13, l17, flag))
				return true;
			j1 = (j1 + 1) % j;
			if (i1 == j1)
				byte0 = 2;
		}
		while (byte0 == 1) if (ai1[k] < ai3[i1]) {
			if (ai1[k] < ai3[j1]) {
				int i4 = ai[k];
				int j13 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[k]);
				int i18 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[k]);
				return method308(j13, i18, i4, !flag);
			}
			int j4 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[j1]);
			int l8 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[j1]);
			int k13 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai3[j1]);
			int j18 = ai2[j1];
			if (method307(j4, l8, k13, j18, flag))
				return true;
			j1 = (j1 + 1) % j;
			if (i1 == j1)
				byte0 = 0;
		} else if (ai3[i1] < ai3[j1]) {
			int k4 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[i1]);
			int i9 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[i1]);
			int l13 = ai2[i1];
			int k18 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai3[i1]);
			if (method307(k4, i9, l13, k18, flag))
				return true;
			i1 = ((i1 - 1) + j) % j;
			if (i1 == j1)
				byte0 = 0;
		} else {
			int l4 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[j1]);
			int j9 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[j1]);
			int i14 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai3[j1]);
			int l18 = ai2[j1];
			if (method307(l4, j9, i14, l18, flag))
				return true;
			j1 = (j1 + 1) % j;
			if (i1 == j1)
				byte0 = 0;
		}
		while (byte0 == 2) if (ai3[i1] < ai1[k]) {
			if (ai3[i1] < ai1[l]) {
				int i5 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[i1]);
				int k9 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[i1]);
				int j14 = ai2[i1];
				return method308(i5, k9, j14, flag);
			}
			int j5 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai1[l]);
			int l9 = ai[l];
			int k14 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[l]);
			int i19 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[l]);
			if (method307(j5, l9, k14, i19, flag))
				return true;
			l = (l + 1) % i;
			if (k == l)
				byte0 = 0;
		} else if (ai1[k] < ai1[l]) {
			int k5 = ai[k];
			int i10 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai1[k]);
			int l14 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[k]);
			int j19 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[k]);
			if (method307(k5, i10, l14, j19, flag))
				return true;
			k = ((k - 1) + i) % i;
			if (k == l)
				byte0 = 0;
		} else {
			int l5 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai1[l]);
			int j10 = ai[l];
			int i15 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[l]);
			int k19 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[l]);
			if (method307(l5, j10, i15, k19, flag))
				return true;
			l = (l + 1) % i;
			if (k == l)
				byte0 = 0;
		}
		if (ai1[k] < ai3[i1]) {
			int i6 = ai[k];
			int j15 = method306(ai2[(i1 + 1) % j], ai3[(i1 + 1) % j], ai2[i1], ai3[i1], ai1[k]);
			int l19 = method306(ai2[((j1 - 1) + j) % j], ai3[((j1 - 1) + j) % j], ai2[j1], ai3[j1], ai1[k]);
			return method308(j15, l19, i6, !flag);
		}
		int j6 = method306(ai[(k + 1) % i], ai1[(k + 1) % i], ai[k], ai1[k], ai3[i1]);
		int k10 = method306(ai[((l - 1) + i) % i], ai1[((l - 1) + i) % i], ai[l], ai1[l], ai3[i1]);
		int k15 = ai2[i1];
		return method308(j6, k10, k15, flag);
	}

	public void moveCamera(int factor, int keyMask)
	{
		if (!mc.getFreeCamera())
			return;
		if ((keyMask & 1) == 1) // left key
		{
			cameraZRot = cameraZRot - 8 & 0x3ff;
		}
		else if ((keyMask & 2) == 2)
		{
			cameraZRot = cameraZRot + 8 & 0x3ff;
		}
		/*      cameraLeftRigtRot
		 *             N
		 *    		   0
		 *	  NW 896       128 NE
		 * 			  \|/
		 *	W  768   -----   256  E
		 * 			  /|\
		 *	  SW 640       384 SE
		 *			  512
		 * 			   S
		 */
		double theta = 2*Math.PI*(cameraZRot % 1024)/1024.0;
		/*       theta*180/PI
		 *            --y
		 *             0
		 *    NW 315        45 NE
		 *            \|/
		 * --x  270  -----   90  ++x
		 *            /|\
		 *    SW 225        135 SE
		 *            180
		 *            ++y
		 */
		double dx = 0;
		double dy = 0;
		if ((keyMask & 4) == 4)
		{ // key up (move forward)
			dx = -Math.sin(theta);
			dy = Math.cos(theta);
		}
		else if ((keyMask & 8) == 8)
		{  // key down (move backward)
			dx = Math.sin(theta);
			dy = -Math.cos(theta);
		}
		double xMove = factor*dx;
		double yMove = factor*dy;
		cameraXPos += xMove;
		cameraYPos += yMove;
	}

	public double getCameraY()
	{
		return cameraYPos/EngineHandle.GAME_SIZE;
	}

	public double getCameraX()
	{
		return cameraXPos/EngineHandle.GAME_SIZE;
	}

	public double getCameraZ()
	{
		return cameraZPos;
	}

	public int getCameraAzimuth()
	{
		return cameraZRot;
	}

	public int getCameraElevation()
	{
		return cameraXRot;
	}

	int nbrColors;
	int colorArray[];
	int colorGradientArray[][];
	int colorGradient[];
	public int lastCameraModelCount;
	public double planeOfViewOffsetFromCamera;
	public double drawModelMaxDist;
	public double drawSpriteMaxDist;
	public double fadeFactor;
	public double fadeDist;
	public static double sin1024[] = new double[0x400];
	public static double cos1024[] = new double[0x400];
	private static int sin256[] = new int[0x100];
	private static int cos256[] = new int[0x100];
	public boolean gradient2Step;
	public double aDouble387;
	public int anInt388;
	private boolean aBoolean389;
	private int mouseX;
	private int mouseY;
	private int currentVisibleModelCount;
	private int maxVisibleModelCount;
	private Model visibleModelsArray[];
	private int visibleModelIntArray[];
	private int width;
	private int halfWidth;
	private int halfHeight;
	private int halfWidth2;
	private int halfHeight2;
	private int cameraSizeInt;
	private int anInt402;
	private double cameraXPos;
	private double cameraZPos;
	private double cameraYPos;
	private int cameraXRot;
	private int cameraZRot;
	private int cameraYRot;
	public int modelCount;
	public int maxModelCount;
	public Model modelArray[];
	private int modelIntArray[];
	private int cameraModelCount;
	private CameraModel cameraModels[];
	private int mobIndex;
	private int anIntArray416[];
	private double mobX[];
	private double mobZ[];
	private double mobY[];
	private int mobWidth[];
	private int mobHeight[];
	private int anIntArray422[];
	public Model aModel;
	int nbrTextures;
	byte colorIndexArray[][];
	int colorsArray[][];
	int textureSize[];
	int texturePixels[][];
	boolean seethrough[];
	private static byte aByteArray434[];
	GameImage gameImage;
	public int imagePixelArray[];
	CameraVariables cameraVariables[];
	int modelYMin;
	int modelYMax;
	double xScreen[];
	double yScreen[];
	int pointBrightness[];
	double xDistToPointFromCamera[];
	double zDistToPointFromCamera[];
	double yDistToPointFromCamera[];
	boolean f1Toggle;
	static double xMinHide;
	static double xMaxHide;
	static double zMinHide;
	static double zMaxHide;
	static double yMinHide;
	static double yMaxHide;
	int anInt454;
	int anInt455;

}
