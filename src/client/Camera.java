package client;

public class Camera {
	public static int light_x = 0, light_z = -50, light_y = 50;
	private mudclient mc;

	public Camera(mudclient mc, GameImage gameImage, int maxModels, int maxCameraModels, int k) {
		this.mc = mc;
		nbrColors = 50;
		colorArray = new int[nbrColors];
		colorGradientArray = new int[nbrColors][256];
		planeOfViewOffsetFromCamera = 5;
		drawModelMaxDist = 1000;
		drawSpriteMaxDist = 1000;
		zoom3 = 20;
		fadeDist = 10;
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
		triangleScreenX = new int[40];
		triangleScreenY = new int[40];
		triangleBright = new int[40];
		xDistToPointFromCamera = new int[40];
		zDistToPointFromCamera = new int[40];
		yDistToPointFromCamera = new int[40];
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

		anInt415 = 0;
		aModel = new Model(k * 2, k);
		anIntArray416 = new int[k];
		anIntArray420 = new int[k];
		anIntArray421 = new int[k];
		anIntArray417 = new int[k];
		anIntArray418 = new int[k];
		anIntArray419 = new int[k];
		anIntArray422 = new int[k];
		if (aByteArray434 == null)
			aByteArray434 = new byte[17691];
		cameraXPos = 0;
		cameraZPos = 0;
		cameraYPos = 0;
		cameraXRot = 0;
		cameraZRot = 0;
		cameraYRot = 0;
		for (int i1 = 0; i1 < 256;
				sin256[i1++] = (int) (Math.sin((double) i1 * 0.02454369D) * 32768D));
		for (int i1 = 0; i1 < 256;
				cos256[i1++] = (int) (Math.cos((double) i1 * 0.02454369D) * 32768D));

		for (int j1 = 0; j1 < 1024;
				sin1024[j1++] = (int) (Math.sin((double) j1 * 0.00613592315D) * 32768D));
		for (int j1 = 0; j1 < 1024;
				cos1024[j1++] = (int) (Math.cos((double) j1 * 0.00613592315D) * 32768D));

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
		anInt415 = 0;
		aModel.method176();
	}

	public void updateFightCount(int i) {
		anInt415 -= i;
		aModel.method177(i, i * 2);
		if (anInt415 < 0)
			anInt415 = 0;
	}

	public int method268(int i, int j, int k, int l, int i1, int j1, int k1) {
		anIntArray416[anInt415] = i;
		anIntArray417[anInt415] = j;
		anIntArray418[anInt415] = k;
		anIntArray419[anInt415] = l;
		anIntArray420[anInt415] = i1;
		anIntArray421[anInt415] = j1;
		anIntArray422[anInt415] = 0;
		int l1 = aModel.insertCoordPoint(j, k, l);
		int i2 = aModel.insertCoordPoint(j, k - j1, l);
		int ai[] = {
				l1, i2
		};
		aModel.addSurface(2, ai, 0, 0);
		aModel.entityType[anInt415] = k1;
		aModel.aByteArray259[anInt415++] = 0;
		return anInt415 - 1;
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
			int drawOrderMiddle = camMdlMiddle.drawOrderVal;
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

	public void method279(int i, int j, int k) {
		int l = -cameraXRot + 1024 & 0x3ff;
		int i1 = -cameraZRot + 1024 & 0x3ff;
		int j1 = -cameraYRot + 1024 & 0x3ff;
		if (j1 != 0) {
			int sin = sin1024[j1];
			int cos = cos1024[j1];
			int tmp = j * sin + i * cos >> 15;
				j = j * cos - i * sin >> 15;
				i = tmp;
		}
		if (l != 0) {
			int sin = sin1024[l];
			int cos = cos1024[l];
			int tmp = j * cos - k * sin >> 15;
			k = j * sin + k * cos >> 15;
			j = tmp;
		}
		if (i1 != 0) {
			int sin = sin1024[i1];
			int cos = cos1024[i1];
			int tmp = k * sin + i * cos >> 15;
			k = k * cos - i * sin >> 15;
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
		int i3 = halfWidth * drawModelMaxDist >> cameraSizeInt;
		int j3 = halfHeight * drawModelMaxDist >> cameraSizeInt;
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
						int zDist = model.zDistToPointFromCameraView[surface[k]];
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
							int x = model.xScreen[surface[k]];
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
								int k1 = model.yScreen[surface[k]];
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
									int sum = 0;
									for (int k = 0; k < pointsInCell; k++)
										sum += model.zDistToPointFromCameraView[surface[k]];
									int avgDist = sum / pointsInCell;
									/* Looks like it is the order the models will be applied */
									camMdl.drawOrderVal = avgDist + model.extraDrawWeight;
									/* Looks like the texture */
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
				int x = model_1.xScreen[p0];
				int y = model_1.yScreen[p0];
				int distToCam = model_1.zDistToPointFromCameraView[p0];
				if (distToCam > planeOfViewOffsetFromCamera && distToCam < drawSpriteMaxDist)
				{
					int modelWidth = (anIntArray420[k] << cameraSizeInt) / distToCam;
					int modelHeight = (anIntArray421[k] << cameraSizeInt) / distToCam;
					if (x - modelWidth / 2 <= halfWidth
							&& x + modelWidth / 2 >= -halfWidth
							&& y - modelHeight <= halfHeight
							&& y >= -halfHeight)
					{
						CameraModel camMdl = cameraModels[cameraModelCount];
						camMdl.model = model_1;
						camMdl.surface = k;
						method294(cameraModelCount);
						camMdl.drawOrderVal = (distToCam + model_1.zDistToPointFromCameraView[surface[1]]) / 2;
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
				int x = model.xScreen[p0];
				int y = model.yScreen[p0];
				int modelDist = model.zDistToPointFromCameraView[p0];
				int modelWidth = (anIntArray420[l] << cameraSizeInt) / modelDist;
				int modelHeight = (anIntArray421[l] << cameraSizeInt) / modelDist;
				int p1 = surface[1];
				int length_y01 = y - model.yScreen[p1];
				int length_x10 = model.xScreen[p1] - x;
				int j11 = length_x10 * length_y01 / modelHeight;
				int modelX = x - modelWidth / 2;
				int modelY = (halfHeight2 + y) - modelHeight;
				gameImage.doSpriteClip1(modelX + halfWidth2, modelY,
						modelWidth, modelHeight, anIntArray416[l],
						j11, (256 << cameraSizeInt) / modelDist);
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
				int k8 = 0;
				int brightness = 0;
				int ptsPerCell = model.pointsPerCell[l];
				int surfaces[] = model.surfaces[l];
				if (model.lightSourceProjectToSurfNormal[l] != Model.INVISIBLE)
					if (cm.normalDirectionToCamera < 0)
						brightness = model.globalLight - model.lightSourceProjectToSurfNormal[l];
					else
						brightness = model.globalLight + model.lightSourceProjectToSurfNormal[l];
				for (int i = 0; i < ptsPerCell; i++)
				{
					int dataPoint = surfaces[i];
					xDistToPointFromCamera[i] = model.xDistToPointFromCameraView[dataPoint];
					zDistToPointFromCamera[i] = model.yDistToPointFromCameraView[dataPoint];
					yDistToPointFromCamera[i] = model.zDistToPointFromCameraView[dataPoint];
					if (model.lightSourceProjectToSurfNormal[l] == Model.INVISIBLE)
						if (cm.normalDirectionToCamera < 0)
							brightness = (model.globalLight - model.pointBrightness[dataPoint]) + model.aByteArray233[dataPoint];
						else
							brightness = model.globalLight + model.pointBrightness[dataPoint] + model.aByteArray233[dataPoint];
					if (model.zDistToPointFromCameraView[dataPoint] >= planeOfViewOffsetFromCamera)
					{
						triangleScreenX[k8] = model.xScreen[dataPoint];
						triangleScreenY[k8] = model.yScreen[dataPoint];
						triangleBright[k8] = brightness;
						if (model.zDistToPointFromCameraView[dataPoint] > fadeDist)
							triangleBright[k8] += (model.zDistToPointFromCameraView[dataPoint] - fadeDist) / zoom3;
						k8++;
					}
					else
					{ // object too close
						int k9;
						if (i == 0)
							k9 = surfaces[ptsPerCell - 1];
						else
							k9 = surfaces[i - 1];
						if (model.zDistToPointFromCameraView[k9] >= planeOfViewOffsetFromCamera) {
							int k7 = model.zDistToPointFromCameraView[dataPoint] - model.zDistToPointFromCameraView[k9];
							int i5 = model.xDistToPointFromCameraView[dataPoint] - ((model.xDistToPointFromCameraView[dataPoint] - model.xDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / k7;
							int j6 = model.yDistToPointFromCameraView[dataPoint] - ((model.yDistToPointFromCameraView[dataPoint] - model.yDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / k7;
							triangleScreenX[k8] = (i5 << cameraSizeInt) / planeOfViewOffsetFromCamera;
							triangleScreenY[k8] = (j6 << cameraSizeInt) / planeOfViewOffsetFromCamera;
							triangleBright[k8] = brightness;
							k8++;
						}
						if (i == ptsPerCell - 1)
							k9 = surfaces[0];
						else
							k9 = surfaces[i + 1];
						if (model.zDistToPointFromCameraView[k9] >= planeOfViewOffsetFromCamera) {
							int l7 = model.zDistToPointFromCameraView[dataPoint] - model.zDistToPointFromCameraView[k9];
							int j5 = model.xDistToPointFromCameraView[dataPoint] - ((model.xDistToPointFromCameraView[dataPoint] - model.xDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / l7;
							int k6 = model.yDistToPointFromCameraView[dataPoint] - ((model.yDistToPointFromCameraView[dataPoint] - model.yDistToPointFromCameraView[k9]) * (model.zDistToPointFromCameraView[dataPoint] - planeOfViewOffsetFromCamera)) / l7;
							triangleScreenX[k8] = (j5 << cameraSizeInt) / planeOfViewOffsetFromCamera;
							triangleScreenY[k8] = (k6 << cameraSizeInt) / planeOfViewOffsetFromCamera;
							triangleBright[k8] = brightness;
							k8++;
						}
					}
				}

				for (int i12 = 0; i12 < ptsPerCell; i12++)
				{
					if (triangleBright[i12] < 0)
						triangleBright[i12] = 0;
					else if (triangleBright[i12] > 255)
						triangleBright[i12] = 255;
				}

				/* Both of these are needed to draw models */
				makeTriangle(0, 0, 0, k8, triangleScreenX, triangleScreenY, triangleBright, model, l);
				if (modelYMax > modelYMin)
					applyColor(0, ptsPerCell, xDistToPointFromCamera, zDistToPointFromCamera,
							yDistToPointFromCamera, cm.color, model);
			}
		}

		aBoolean389 = false;
	}

	private void makeTriangle(int xMin, int xMax, int xMinBright, int pointsInSurface,
			int triangleX[], int triangleY[], int brightness[],
			Model model, int j1)
	{
		int i, j;
		int[] p_y = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; ++i)
			p_y[i] = triangleY[i] + halfHeight2;
		int[] p_x = new int[pointsInSurface];
		for (i = 0; i < pointsInSurface; ++i)
			p_x[i] = triangleX[i];
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
						xMin = min_x[j];
						xMinBright = min_b[j];
					}
					if (min_x[j] > xMax)
					{
						xMax = min_x[j];
						xMaxBright = min_b[j];
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
			int imgPixXStart, int k, int xDist[],
			int zDist[], int yDist[], int color,
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
			int[] p_x = {xDist[0], xDist[0] - xDist[1], xDist[k] - xDist[0]};
			int[] p_z = {zDist[0], zDist[0] - zDist[1], zDist[k] - zDist[0]};
			int[] p_y = {yDist[0], yDist[0] - yDist[1], yDist[k] - yDist[0]};
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

	public void setCamera(int playerX, int playerZ, int playerY,
			int xRot, int zRot, int yRot, int camYStart, double cameraZoom) {
		xRot &= 0x3ff;
		zRot &= 0x3ff;
		yRot &= 0x3ff;
		if (!mc.getFreeCamera())
		{
			cameraXRot = 1024 - xRot & 0x3ff;
			cameraZRot = 1024 - zRot & 0x3ff;
			cameraYRot = 1024 - yRot & 0x3ff;
		}
		int cameraXOffset = 0;
		int cameraZOffset = 0;
		int cameraYOffset = camYStart;
		
		if (xRot != 0)
		{
			int sin = sin1024[xRot];
			int cos = cos1024[xRot];
			int tmp = cameraZOffset * cos - cameraYOffset * sin >> 15;
			cameraYOffset = cameraYOffset * cos + cameraZOffset * sin >> 15;
			cameraZOffset = tmp;
		}
		if (zRot != 0)
		{
			int sin = sin1024[zRot];
			int cos = cos1024[zRot];
			int camXOffsTmp = cameraYOffset * sin + cameraXOffset * cos >> 15;
			cameraYOffset = cameraYOffset * cos - cameraXOffset * sin >> 15;
			cameraXOffset = camXOffsTmp;
		}
		
		if (yRot != 0)
		{
			int sin = sin1024[yRot];
			int cos = cos1024[yRot];
			int tmp = cameraZOffset * sin + cameraXOffset * cos >> 15;
			cameraZOffset = cameraZOffset * cos - cameraXOffset * sin >> 15;
			cameraXOffset = tmp;
		}
		if (!mc.getFreeCamera())
		{
			cameraXPos = playerX - (int)(cameraXOffset*cameraZoom);
			cameraZPos = playerZ - (int)(cameraZOffset*cameraZoom)-200; // -200 makes it so we don't zoom in on out feet
			cameraYPos = playerY - (int)(cameraYOffset*cameraZoom);
		}
	}

	private void method293(int modelIdx) {
		CameraModel cameraModel = cameraModels[modelIdx];
		Model model = cameraModel.model;
		int surfaceIdx = cameraModel.surface;
		int surfaces[] = model.surfaces[surfaceIdx];
		int pointsInCell = model.pointsPerCell[surfaceIdx];
		int scale = model.scaleFactor[surfaceIdx];
		int x0 = model.xDistToPointFromCameraView[surfaces[0]];
		int y0 = model.yDistToPointFromCameraView[surfaces[0]];
		int z0 = model.zDistToPointFromCameraView[surfaces[0]];
		int u_x = model.xDistToPointFromCameraView[surfaces[1]] - x0;
		int u_y = model.yDistToPointFromCameraView[surfaces[1]] - y0;
		int u_z = model.zDistToPointFromCameraView[surfaces[1]] - z0;
		int v_x = model.xDistToPointFromCameraView[surfaces[2]] - x0;
		int v_y = model.yDistToPointFromCameraView[surfaces[2]] - y0;
		int v_z = model.zDistToPointFromCameraView[surfaces[2]] - z0;
		int n_x = u_y * v_z - v_y * u_z;
		int n_y = u_z * v_x - v_z * u_x;
		int n_z = u_x * v_y - v_x * u_y;
		if (scale == -1) {
			scale = 0;
			while(n_x > 25000 || n_y > 25000 || n_z > 25000
					|| n_x < -25000 || n_y < -25000 || n_z < -25000)
			{
				scale++;
				n_x >>= 1;
				n_y >>= 1;
				n_z >>= 1;
			}

			model.scaleFactor[surfaceIdx] = scale;
			model.normalLength[surfaceIdx] = (int) ((double) anInt402 * Math.sqrt(n_x * n_x + n_y * n_y + n_z * n_z));
		} else {
			n_x >>= scale;
			n_y >>= scale;
			n_z >>= scale;
		}
		cameraModel.normalDirectionToCamera = x0 * n_x + y0 * n_y + z0 * n_z;
		cameraModel.xNormal = n_x;
		cameraModel.zNormal = n_y;
		cameraModel.yNormal = n_z;
		int zmin = model.zDistToPointFromCameraView[surfaces[0]];
		int zmax = zmin;
		int xmin = model.xScreen[surfaces[0]];
		int xmax = xmin;
		int ymin = model.yScreen[surfaces[0]];
		int ymax = ymin;
		for (int i = 1; i < pointsInCell; i++)
		{
			int dist = model.zDistToPointFromCameraView[surfaces[i]];
			if (dist > zmax)
				zmax = dist;
			else if (dist < zmin)
				zmin = dist;
			dist = model.xScreen[surfaces[i]];
			if (dist > xmax)
				xmax = dist;
			else if (dist < xmin)
				xmin = dist;
			dist = model.yScreen[surfaces[i]];
			if (dist > ymax)
				ymax = dist;
			else if (dist < ymin)
				ymin = dist;
		}

		cameraModel.zMin = zmin;
		cameraModel.zMax = zmax;
		cameraModel.xMin = xmin;
		cameraModel.xMax = xmax;
		cameraModel.yMin = ymin;
		cameraModel.yMax = ymax;
	}

	private void method294(int modelIdx)
	{
		CameraModel camMdl = cameraModels[modelIdx];
		Model model = camMdl.model;
		int surface = camMdl.surface;
		int surfaces[] = model.surfaces[surface];
		int n_x = 0;
		int n_z = 0;
		int n_y = 1;
		int u0_x = model.xDistToPointFromCameraView[surfaces[0]];
		int u0_y = model.yDistToPointFromCameraView[surfaces[0]];
		int u0_z = model.zDistToPointFromCameraView[surfaces[0]];
		model.normalLength[surface] = 1;
		model.scaleFactor[surface] = 0;
		camMdl.normalDirectionToCamera = u0_x * n_x + u0_y * n_z + u0_z * n_y;
		camMdl.xNormal = n_x;
		camMdl.zNormal = n_z;
		camMdl.yNormal = n_y;
		int zmin = model.zDistToPointFromCameraView[surfaces[0]];
		int zmax = zmin;
		int xmin = model.xScreen[surfaces[0]];
		int xmax = xmin;
		if (model.xScreen[surfaces[1]] < xmin)
			xmin = model.xScreen[surfaces[1]];
		else
			xmax = model.xScreen[surfaces[1]];
		int ymin = model.yScreen[surfaces[1]];
		int ymax = model.yScreen[surfaces[0]];
		int dist = model.zDistToPointFromCameraView[surfaces[1]];
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
		camMdl.zMin = zmin;
		camMdl.zMax = zmax;
		camMdl.xMin = xmin - 20;
		camMdl.xMax = xmax + 20;
		camMdl.yMin = ymin;
		camMdl.yMax = ymax;
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
		int u0_x_1 = model_1.xDistToPointFromCameraView[surfaces_1[0]];
		int u0_y_1 = model_1.yDistToPointFromCameraView[surfaces_1[0]];
		int u0_z_1 = model_1.zDistToPointFromCameraView[surfaces_1[0]];
		int n_x = cm_1.xNormal;
		int n_z = cm_1.zNormal;
		int n_y = cm_1.yNormal;
		int normLen_1 = model_1.normalLength[surfIdx_1];
		int directionToCam = cm_1.normalDirectionToCamera;
		boolean flag = false;
		for (int i = 0; i < pointsInCell_0; i++)
		{
			int point = surfaces_0[i];
			// dot
			int i2 = (u0_x_1 - model_0.xDistToPointFromCameraView[point]) * n_x
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
			int j2 = (u0_x_1 - model_1.xDistToPointFromCameraView[point]) * n_x
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
			ai2[0] = model_0.xScreen[p0_0] - 20;
			ai2[1] = model_0.xScreen[p1_0] - 20;
			ai2[2] = model_0.xScreen[p1_0] + 20;
			ai2[3] = model_0.xScreen[p0_0] + 20;
			ai3[0] = ai3[3] = model_0.yScreen[p0_0];
			ai3[1] = ai3[2] = model_0.yScreen[p1_0];
		}
		else
		{
			ai2 = new int[pointsInCell_0];
			ai3 = new int[pointsInCell_0];
			for (int j5 = 0; j5 < pointsInCell_0; j5++) {
				int i6 = surfaces_0[j5];
				ai2[j5] = model_0.xScreen[i6];
				ai3[j5] = model_0.yScreen[i6];
			}

		}
		int ai4[];
		int ai5[];
		if (pointsInCell_1 == 2) {
			ai4 = new int[4];
			ai5 = new int[4];
			int p0_1 = surfaces_1[0];
			int p1_1 = surfaces_1[1];
			ai4[0] = model_1.xScreen[p0_1] - 20;
			ai4[1] = model_1.xScreen[p1_1] - 20;
			ai4[2] = model_1.xScreen[p1_1] + 20;
			ai4[3] = model_1.xScreen[p0_1] + 20;
			ai5[0] = ai5[3] = model_1.yScreen[p0_1];
			ai5[1] = ai5[2] = model_1.yScreen[p1_1];
		} else {
			ai4 = new int[pointsInCell_1];
			ai5 = new int[pointsInCell_1];
			for (int l5 = 0; l5 < pointsInCell_1; l5++) {
				int j6 = surfaces_1[l5];
				ai4[l5] = model_1.xScreen[j6];
				ai5[l5] = model_1.yScreen[j6];
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
		int a = model_1.xDistToPointFromCameraView[surface_1[0]];
		int b = model_1.yDistToPointFromCameraView[surface_1[0]];
		int c = model_1.zDistToPointFromCameraView[surface_1[0]];
		int n_x = cameraModel_1.xNormal;
		int n_z = cameraModel_1.zNormal;
		int n_y = cameraModel_1.yNormal;
		int k3 = model_1.normalLength[surfaceIdx_1];
		int l3 = cameraModel_1.normalDirectionToCamera;
		boolean flag = false;
		for (int i4 = 0; i4 < ptsPerCell_0; i4++) {
			int i1 = surface_0[i4];
			int k1 = (a - model_0.xDistToPointFromCameraView[i1]) * n_x
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
			int l1 = (a - model_1.xDistToPointFromCameraView[j1]) * n_x
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
			return i + ((k - i) * (i1 - j)) / (l - j);
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

	public int getCameraY()
	{
		//return (cameraXPos - 6208)/64;
		return cameraYPos/128;
	}

	public int getCameraX()
	{
		//return (cameraYPos - 6208)/64;
		return cameraXPos/128;
	}

	public int getCameraZ()
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
	public int planeOfViewOffsetFromCamera;
	public int drawModelMaxDist;
	public int drawSpriteMaxDist;
	public int zoom3;
	public int fadeDist;
	public static int sin1024[] = new int[0x400];
	public static int cos1024[] = new int[0x400];
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
	private int cameraXPos;
	private int cameraZPos;
	private int cameraYPos;
	private int cameraXRot;
	private int cameraZRot;
	private int cameraYRot;
	public int modelCount;
	public int maxModelCount;
	public Model modelArray[];
	private int modelIntArray[];
	private int cameraModelCount;
	private CameraModel cameraModels[];
	private int anInt415;
	private int anIntArray416[];
	private int anIntArray417[];
	private int anIntArray418[];
	private int anIntArray419[];
	private int anIntArray420[];
	private int anIntArray421[];
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
	int triangleScreenX[];
	int triangleScreenY[];
	int triangleBright[];
	int xDistToPointFromCamera[];
	int zDistToPointFromCamera[];
	int yDistToPointFromCamera[];
	boolean f1Toggle;
	static int xMinHide;
	static int xMaxHide;
	static int zMinHide;
	static int zMaxHide;
	static int yMinHide;
	static int yMaxHide;
	int anInt454;
	int anInt455;

}
