package client;

public class Camera {
	public static int light_x = 0, light_z = -50, light_y = 50;
	private mudclient mc;

	public Camera(mudclient mc, GameImage gameImage, int maxModels, int maxCameraModels, int nSurfaces) {
		this.mc = mc;
		nbrColors = 50;
		colorArray = new int[nbrColors];
		colorGradientArray = new int[nbrColors][256];
		viewPlaneDist = 0.0390625D;
		gradient2Step = false;
		aDouble387 = 1.1D;
		anInt388 = 1;
		aBoolean = false;
		maxVisibleModelCount = 100;
		visibleModelsArray = new Model[maxVisibleModelCount];
		visibleModelIntArray = new int[maxVisibleModelCount];
		anInt402 = 4;
		xScreen = new int[40];
		yScreen = new int[40];
		pointBrightness = new int[40];
		xCoordCamDist = new double[40];
		yCoordCamDist = new double[40];
		zCoordCamDist = new double[40];
		lowDef = false;
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
		spriteModels = new Model(nSurfaces * 2, nSurfaces);
		anIntArray416 = new int[nSurfaces];
		mobWidth = new double[nSurfaces];
		mobHeight = new double[nSurfaces];
		mobX = new double[nSurfaces];
		mobZ = new double[nSurfaces];
		mobY = new double[nSurfaces];
		attackAnimXOffset = new double[nSurfaces];
		if (aByteArray434 == null)
			aByteArray434 = new byte[17691];
		cameraXPos = 0;
		cameraZPos = 0;
		cameraYPos = 0;
		cameraXRot = 0;
		cameraZRot = 0;
		cameraYRot = 0;
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
		spriteModels.method176();
	}

	public void updateFightCount(int i) {
		mobIndex -= i;
		spriteModels.method177(i, i * 2);
		if (mobIndex < 0)
			mobIndex = 0;
	}

	public int add2DModel(int i, double x, double z, double y,
			double width, double height, int type)
	{
		anIntArray416[mobIndex] = i;
		mobX[mobIndex] = x;
		mobZ[mobIndex] = z;
		mobY[mobIndex] = y;
		mobWidth[mobIndex] = width;
		mobHeight[mobIndex] = height;
		attackAnimXOffset[mobIndex] = 0;
		int p0 = spriteModels.addCoordPoint(x, z, y);
		int p1 = spriteModels.addCoordPoint(x, z - height, y);
		int surface[] = {
				p0, p1
		};
		spriteModels.addSurface(2, surface, 0, 0);
		spriteModels.entityType[mobIndex] = type;
		spriteModels.aByteArray259[mobIndex++] = 0;
		return mobIndex - 1;
	}

	public void setOurPlayer(int i) {
		spriteModels.aByteArray259[i] = 1;
	}

	public void setCombat(int id, double xOffs) {
		attackAnimXOffset[id] = xOffs;
	}

	public void updateMouseCoords(int x, int y) {
		mouseX = x - halfWidth2;
		mouseY = y;
		currentVisibleModelCount = 0;
		aBoolean = true;
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
		do
		{
			while (cameraModels[l].aBoolean367)
				l++;
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

	public boolean method278(CameraModel cameraModels[], int idx1, int idx2) {
		do {
			CameraModel cameraModel = cameraModels[idx1];
			for (int k = idx1 + 1; k <= idx2; k++) {
				CameraModel cameraModel_1 = cameraModels[k];
				if (!method295(cameraModel_1, cameraModel))
					break;
				cameraModels[idx1] = cameraModel_1;
				cameraModels[k] = cameraModel;
				idx1 = k;
				if (idx1 == idx2) {
					anInt454 = idx1;
					anInt455 = idx1 - 1;
					return true;
				}
			}

			CameraModel cameraModel_2 = cameraModels[idx2];
			for (int l = idx2 - 1; l >= idx1; l--) {
				CameraModel cameraModel_3 = cameraModels[l];
				if (!method295(cameraModel_2, cameraModel_3))
					break;
				cameraModels[idx2] = cameraModel_3;
				cameraModels[l] = cameraModel_2;
				idx2 = l;
				if (idx1 == idx2) {
					anInt454 = idx2 + 1;
					anInt455 = idx2;
					return true;
				}
			}

			if (idx1 + 1 >= idx2) {
				anInt454 = idx1;
				anInt455 = idx2;
				return false;
			}
			if (!method278(cameraModels, idx1 + 1, idx2)) {
				anInt454 = idx1;
				return false;
			}
			idx2 = anInt455;
		} while (true);
	}

	public void method279(double x, double y, double dist)
	{
		int xRot = -cameraXRot + 1024 & 0x3ff;
		int zRot = -cameraZRot + 1024 & 0x3ff;
		int yRot = -cameraYRot + 1024 & 0x3ff;
		if (yRot != 0) {
			double sin = Trig.sin1024[yRot];
			double cos = Trig.cos1024[yRot];
			double tmp = y * sin + x * cos;
				y = y * cos - x * sin;
				x = tmp;
		}
		if (xRot != 0) {
			double sin = Trig.sin1024[xRot];
			double cos = Trig.cos1024[xRot];
			double tmp = y * cos - dist * sin;
			dist = y * sin + dist * cos;
			y = tmp;
		}
		if (zRot != 0) {
			double sin = Trig.sin1024[zRot];
			double cos = Trig.cos1024[zRot];
			double tmp = dist * sin + x * cos;
			dist = dist * cos - x * sin;
			x = tmp;
		}
		if (x < xMinHide)
			xMinHide = x;
		if (x > xMaxHide)
			xMaxHide = x;
		if (y < yMinHide)
			yMinHide = y;
		if (y > yMaxHide)
			yMaxHide = y;
		if (dist < distMinHide)
			distMinHide = dist;
		if (dist > distMaxHide)
			distMaxHide = dist;
	}

	public void finishCamera() {
		lowDef = gameImage.lowDef;
		int fctr = 1 << cameraSizeInt;
		double xBounds = halfWidth * drawModelMaxDist / fctr;
		double yBounds = halfHeight * drawModelMaxDist / fctr;
		xMinHide = 0;
		xMaxHide = 0;
		yMinHide = 0;
		yMaxHide = 0;
		distMinHide = 0;
		distMaxHide = 0;
		method279(-xBounds, -yBounds, drawModelMaxDist);
		method279(-xBounds, yBounds, drawModelMaxDist);
		method279(xBounds, -yBounds, drawModelMaxDist);
		method279(xBounds, yBounds, drawModelMaxDist);
		method279(-halfWidth, -halfHeight, 0);
		method279(-halfWidth, halfHeight, 0);
		method279(halfWidth, -halfHeight, 0);
		method279(halfWidth, halfHeight, 0);
		xMinHide += cameraXPos;
		xMaxHide += cameraXPos;
		yMinHide += cameraZPos;
		yMaxHide += cameraZPos;
		distMinHide += cameraYPos;
		distMaxHide += cameraYPos;
		for (int i = 0; i < modelCount; i++)
			modelArray[i].makePerspectiveVectors(
					cameraXPos, cameraZPos, cameraYPos, cameraXRot,
					cameraZRot, cameraYRot, cameraSizeInt, viewPlaneDist);

		modelArray[modelCount] = spriteModels;
		spriteModels.modelType = Model.MODEL_2D;
		modelArray[modelCount].makePerspectiveVectors(
				cameraXPos, cameraZPos, cameraYPos, cameraXRot,
				cameraZRot, cameraYRot, cameraSizeInt, viewPlaneDist);
		cameraModelCount = 0;
		for (int i = 0; i < modelCount; i++)
		{ /* Calculate draw order for 3D model surfaces */
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
						double zDist = model.zCoordCamDist[surface[k]];
						if (zDist <= viewPlaneDist
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
							double x = model.xProjected[surface[k]];
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
								int k1 = (int)model.yProjected[surface[k]];
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
									double sum = 0;
									for (int k = 0; k < pointsInCell; k++)
										sum += model.getDistanceTo(surface[k]);
									camMdl.drawOrderVal = sum / (double)pointsInCell;
									camMdl.color = modelTexture;
									cameraModelCount++;
								}
							}
						}
					}
				}
			}
		}

		Model model_1 = spriteModels;
		if (model_1.visible)
		{ /* Calculate draw order for 2D model surfaces */
			for (int k = 0; k < model_1.nbrSurfaces; k++)
			{
				int surface[] = model_1.surfaces[k];
				int p0 = surface[0];
				double x = model_1.xProjected[p0];
				double y = model_1.yProjected[p0];
				double distToCam = model_1.getDistanceTo(p0);
				if (distToCam > viewPlaneDist
						&& distToCam < drawSpriteMaxDist)
				{
					double modelWidth = mobWidth[k] * fctr / distToCam;
					double modelHeight = mobHeight[k] * fctr / distToCam;
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
		method277(100, cameraModels, cameraModelCount); // not sure what this does
		for (int i = 0; i < cameraModelCount; i++)
		{
			CameraModel cm = cameraModels[i];
			Model model = cm.model;
			int l = cm.surface;
			if (model == spriteModels)
			{ // 2D models
				int surface[] = model.surfaces[l];
				int p0 = surface[0];
				double x = model.xProjected[p0];
				double y = model.yProjected[p0];
				double modelDist = model.zCoordCamDist[p0];
				double modelWidth = mobWidth[l] * fctr / modelDist;
				double modelHeight = mobHeight[l] * fctr / modelDist;
				int p1 = surface[1];
				double length_y01 = y - model.yProjected[p1];
				double length_x10 = model.xProjected[p1] - x;
				double j11 = length_x10 * length_y01 / modelHeight;
				double modelX = x - modelWidth / 2;
				double modelY = (halfHeight2 + y) - modelHeight;
				
				gameImage.doSpriteClip1((int)modelX + halfWidth2, (int)modelY,
						(int)modelWidth, (int)modelHeight, anIntArray416[l],
						(int)j11, (int) ((4D * fctr) / modelDist));
				if (aBoolean && currentVisibleModelCount < maxVisibleModelCount)
				{
					modelX += (attackAnimXOffset[l] * fctr) / modelDist;
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
			{ // 3D model
				int pointsInSurfac = 0;
				int brightness = 0;
				int ptsPerCell = model.pointsPerCell[l];
				int surfaces[] = model.surfaces[l];
				if (model.lightSourceProjectToSurfNormal[l] != Model.INVISIBLE)
					if (cm.normalDirectionToCamera < 0)
						brightness = (int) (model.globalLight - model.lightSourceProjectToSurfNormal[l]);
					else
						brightness = (int) (model.globalLight + model.lightSourceProjectToSurfNormal[l]);
				for (int j = 0; j < ptsPerCell; j++)
				{
					int pt = surfaces[j];
					xCoordCamDist[j] = model.xCoordCamDist[pt];
					yCoordCamDist[j] = model.yCoordCamDist[pt];
					zCoordCamDist[j] = model.zCoordCamDist[pt];
					if (model.lightSourceProjectToSurfNormal[l] == Model.INVISIBLE)
						if (cm.normalDirectionToCamera < 0)
							brightness = (int) (model.globalLight - model.pointBrightness[pt]);
						else
							brightness = (int) (model.globalLight + model.pointBrightness[pt]);
					if (model.zCoordCamDist[pt] >= viewPlaneDist)
					{
						xScreen[pointsInSurfac] = (int) model.xProjected[pt];
						yScreen[pointsInSurfac] = (int) model.yProjected[pt];
						pointBrightness[pointsInSurfac] = brightness;
						/* should chage this to allow for other
						 * background colors than black */
						double dstnc = model.getDistanceTo(pt);
						if (dstnc > fadeDist)
							pointBrightness[pointsInSurfac] += (dstnc - fadeDist) / fadeFactor;
						pointsInSurfac++;
					}
					else
					{
						int pt_other;
						if (j == 0)
							pt_other = surfaces[ptsPerCell - 1];
						else
							pt_other = surfaces[j - 1];
						if (model.zCoordCamDist[pt_other] >= viewPlaneDist)
						{
							double z = model.zCoordCamDist[pt] - model.zCoordCamDist[pt_other];
							double x = model.xCoordCamDist[pt] - ((model.xCoordCamDist[pt] - model.xCoordCamDist[pt_other]) * (model.zCoordCamDist[pt] - viewPlaneDist)) / z;
							double y = model.yCoordCamDist[pt] - ((model.yCoordCamDist[pt] - model.yCoordCamDist[pt_other]) * (model.zCoordCamDist[pt] - viewPlaneDist)) / z;
							xScreen[pointsInSurfac] = (int) (x * fctr / viewPlaneDist);
							yScreen[pointsInSurfac] = (int) (y * fctr / viewPlaneDist);
							pointBrightness[pointsInSurfac] = brightness;
							pointsInSurfac++;
						}
						if (j == ptsPerCell - 1)
							pt_other = surfaces[0];
						else
							pt_other = surfaces[j + 1];
						if (model.zCoordCamDist[pt_other] >= viewPlaneDist)
						{
							double z = model.zCoordCamDist[pt] - model.zCoordCamDist[pt_other];
							double x = model.xCoordCamDist[pt] - ((model.xCoordCamDist[pt] - model.xCoordCamDist[pt_other]) * (model.zCoordCamDist[pt] - viewPlaneDist)) / z;
							double y = model.yCoordCamDist[pt] - ((model.yCoordCamDist[pt] - model.yCoordCamDist[pt_other]) * (model.zCoordCamDist[pt] - viewPlaneDist)) / z;
							xScreen[pointsInSurfac] = (int) (x * fctr / viewPlaneDist);
							yScreen[pointsInSurfac] = (int) (y * fctr / viewPlaneDist);
							pointBrightness[pointsInSurfac] = brightness;
							pointsInSurfac++;
						}
					}
				}

				for (int j = 0; j < ptsPerCell; j++)
				{
					if (pointBrightness[j] < 0)
						pointBrightness[j] = 0;
					else if (pointBrightness[j] > 255)
						pointBrightness[j] = 255;
				}

				if (pointsInSurfac == 0)
					continue;
				makeTriangle(0, 0, 0, pointsInSurfac, xScreen, yScreen, pointBrightness, model, l);
				if (modelYMax > modelYMin)
					applyColor(0, ptsPerCell, xCoordCamDist, yCoordCamDist,
							zCoordCamDist, cm.color, model);
			}
		}

		aBoolean = false;
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

		if (aBoolean && currentVisibleModelCount < maxVisibleModelCount
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
			double[] p_x = {xDist[0], xDist[0] - xDist[1], xDist[k] - xDist[0]};
			double[] p_z = {zDist[0], zDist[0] - zDist[1], zDist[k] - zDist[0]};
			double[] p_y = {yDist[0], yDist[0] - yDist[1], yDist[k] - yDist[0]};
			double factr1 = 1 << 5 + textureSize[color];
			double factr2 = 1 << (5 - cameraSizeInt) + 4 + textureSize[color];
			double factr3 = 1 << (5 - cameraSizeInt) + textureSize[color];
			double nk_y = (p_x[2] * p_z[0] - p_z[2] * p_x[0]) * factr1;
			double nk_x = (p_z[2] * p_y[0] - p_y[2] * p_z[0]) * factr2;
			double nk_z = (p_y[2] * p_x[0] - p_x[2] * p_y[0]) * factr3;
			double n1_y = (p_x[1] * p_z[0] - p_z[1] * p_x[0]) * factr1;
			double n1_x = (p_z[1] * p_y[0] - p_y[1] * p_z[0]) * factr2;
			double n1_z = (p_y[1] * p_x[0] - p_x[1] * p_y[0]) * factr3;

			double factr4 = 1 << 5;
			double factr5 = 1 << (5 - cameraSizeInt) + 4;
			// this will be divided rather than multiplied
			double factr6 = 1.0 / (double)(1 << cameraSizeInt - 5);
			double n_y = (p_z[1] * p_x[2] - p_x[1] * p_z[2]) * factr4;
			double n_x = (p_y[1] * p_z[2] - p_z[1] * p_y[2]) * factr5;
			double n_z = (p_x[1] * p_y[2] - p_y[1] * p_x[2]) * factr6;
			
			double k14 = nk_x / 16;
			double i15 = n1_x / 16;
			double k15 = n_x / 16;
			int i16 = modelYMin - halfHeight2;
			int imgPixSkip = width;
			int imgPixRow = halfWidth2 + modelYMin * imgPixSkip;
			byte rowStep = 1;
			nk_y += nk_z * i16;
			n1_y += n1_z * i16;
			n_y += n_z * i16;
			if (lowDef)
			{
				if ((modelYMin & 1) == 1)
				{
					modelYMin++;
					nk_y += nk_z;
					n1_y += n1_z;
					n_y += n_z;
					imgPixRow += imgPixSkip;
				}
				nk_z *= 2;
				n1_z *= 2;
				n_z *= 2;
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
		if (lowDef)
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
			int xTexture, int yTexture, double smthXTexture,
			double smthYTexture, double smthDivision,
			double smthXTextureStep, double smthYTextureStep,
			double smthDivisionStep, int length, int offset,
			int shadeOffset, int shadeStep,
			int nSteps, boolean transparent, boolean seethrough, int size)
	{
		if (length <= 0)
			return;
		int mask = transparent ? 0x7f7f7f : 0x0;
		shadeStep <<= (nSteps >> 1);
		int lastRow = (1 << 2*size) - (1 << size);
		int maxSpriteIdx = (1 << 2*size) - 1;

		int i3 = 0;
		int j3 = 0;
		int fctr = 1 << size;
		if (smthDivision != 0)
		{
			xTexture = (int) (smthXTexture / smthDivision) * fctr;
			yTexture = (int) (smthYTexture / smthDivision) * fctr;
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
			i3 = (int) (smthXTexture / smthDivision) * fctr;
			j3 = (int) (smthYTexture / smthDivision) * fctr;
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
				xTexture = (xTexture & maxSpriteIdx);
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
				i3 = (int) (smthXTexture / smthDivision) * fctr;
				j3 = (int) (smthYTexture / smthDivision) * fctr;
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
				xTexture = (xTexture & maxSpriteIdx);
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
			double sin = Trig.sin1024[xRot];
			double cos = Trig.cos1024[xRot];
			double tmp = cameraZOffset * cos - cameraYOffset * sin;
			cameraYOffset = cameraYOffset * cos + cameraZOffset * sin;
			cameraZOffset = tmp;
		}
		if (zRot != 0)
		{
			double sin = Trig.sin1024[zRot];
			double cos = Trig.cos1024[zRot];
			double camXOffsTmp = cameraYOffset * sin + cameraXOffset * cos;
			cameraYOffset = cameraYOffset * cos - cameraXOffset * sin;
			cameraXOffset = camXOffsTmp;
		}
		
		if (yRot != 0)
		{
			double sin = Trig.sin1024[yRot];
			double cos = Trig.cos1024[yRot];
			double tmp = cameraZOffset * sin + cameraXOffset * cos;
			cameraZOffset = cameraZOffset * cos - cameraXOffset * sin;
			cameraXOffset = tmp;
		}
		if (!mc.getFreeCamera())
		{
			cameraXPos = playerX - cameraXOffset*cameraZoom;
			cameraZPos = playerZ - cameraZOffset*cameraZoom - 1.5625;
			cameraYPos = playerY - cameraYOffset*cameraZoom;
		}
	}

	private void method293(int modelIdx) {
		CameraModel cameraModel = cameraModels[modelIdx];
		Model model = cameraModel.model;
		int surfaceIdx = cameraModel.surface;
		int surfaces[] = model.surfaces[surfaceIdx];
		int pointsInCell = model.pointsPerCell[surfaceIdx];
		double x0 = model.xCoordCamDist[surfaces[0]];
		double y0 = model.yCoordCamDist[surfaces[0]];
		double z0 = model.zCoordCamDist[surfaces[0]];
		double u_x = model.xCoordCamDist[surfaces[1]] - x0;
		double u_y = model.yCoordCamDist[surfaces[1]] - y0;
		double u_z = model.zCoordCamDist[surfaces[1]] - z0;
		double v_x = model.xCoordCamDist[surfaces[2]] - x0;
		double v_y = model.yCoordCamDist[surfaces[2]] - y0;
		double v_z = model.zCoordCamDist[surfaces[2]] - z0;
		double n_x = u_y * v_z - v_y * u_z;
		double n_y = u_z * v_x - v_z * u_x;
		double n_z = u_x * v_y - v_x * u_y;
		model.normalLength[surfaceIdx] = Math.sqrt(n_x*n_x + n_y*n_y + n_z*n_z);
		cameraModel.normalDirectionToCamera = x0*n_x + y0*n_y + z0*n_z;
		cameraModel.xNormal = n_x;
		cameraModel.zNormal = n_y;
		cameraModel.yNormal = n_z;
		double zmin = model.zCoordCamDist[surfaces[0]];
		double zmax = zmin;
		double xmin = model.xProjected[surfaces[0]];
		double xmax = xmin;
		double ymin = model.yProjected[surfaces[0]];
		double ymax = ymin;
		for (int i = 1; i < pointsInCell; i++)
		{
			double dist = model.zCoordCamDist[surfaces[i]];
			if (dist > zmax)
				zmax = dist;
			else if (dist < zmin)
				zmin = dist;
			dist = model.xProjected[surfaces[i]];
			if (dist > xmax)
				xmax = dist;
			else if (dist < xmin)
				xmin = dist;
			dist = model.yProjected[surfaces[i]];
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
		double n_x = 0D;
		double n_z = 0D;
		double n_y = 1D;
		double u0_x = model.xCoordCamDist[surfaces[0]];
		double u0_y = model.yCoordCamDist[surfaces[0]];
		double u0_z = model.zCoordCamDist[surfaces[0]];
		model.normalLength[surface] = 1D;
		camMdl.normalDirectionToCamera = u0_x * n_x + u0_y * n_z + u0_z * n_y;
		camMdl.xNormal = n_x;
		camMdl.zNormal = n_z;
		camMdl.yNormal = n_y;
		double zmin = model.zCoordCamDist[surfaces[0]];
		double zmax = zmin;
		double xmin = model.xProjected[surfaces[0]];
		double xmax = xmin;
		if (model.xProjected[surfaces[1]] < xmin)
			xmin = model.xProjected[surfaces[1]];
		else
			xmax = model.xProjected[surfaces[1]];
		double ymin = model.yProjected[surfaces[1]];
		double ymax = model.yProjected[surfaces[0]];
		double dist = model.zCoordCamDist[surfaces[1]];
		if (dist > zmax)
			zmax = dist;
		else if (dist < zmin)
			zmin = dist;
		dist = model.xProjected[surfaces[1]];
		if (dist > xmax)
			xmax = dist;
		else if (dist < xmin)
			xmin = dist;
		dist = model.yProjected[surfaces[1]];
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
		double u0_x_1 = model_1.xCoordCamDist[surfaces_1[0]];
		double u0_y_1 = model_1.yCoordCamDist[surfaces_1[0]];
		double u0_z_1 = model_1.zCoordCamDist[surfaces_1[0]];
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
			double i2 = (u0_x_1 - model_0.xCoordCamDist[point]) * n_x
					+ (u0_y_1 - model_0.yCoordCamDist[point]) * n_z
					+ (u0_z_1 - model_0.zCoordCamDist[point]) * n_y;
			if ((i2 >= -normLen_1 || directionToCam >= 0)
					&& (i2 <= normLen_1 || directionToCam <= 0))
				continue;
			flag = true;
			break;
		}
		if (!flag)
			return true;

		u0_x_1 = model_0.xCoordCamDist[surfaces_0[0]];
		u0_y_1 = model_0.yCoordCamDist[surfaces_0[0]];
		u0_z_1 = model_0.zCoordCamDist[surfaces_0[0]];
		n_x = cm_0.xNormal;
		n_z = cm_0.zNormal;
		n_y = cm_0.yNormal;
		normLen_1 = model_0.normalLength[surfIdx_0];
		directionToCam = cm_0.normalDirectionToCamera;
		flag = false;
		for (int i = 0; i < pointsInCell_1; i++)
		{
			int point = surfaces_1[i];
			double j2 = (u0_x_1 - model_1.xCoordCamDist[point]) * n_x
					+ (u0_y_1 - model_1.yCoordCamDist[point]) * n_z
					+ (u0_z_1 - model_1.zCoordCamDist[point]) * n_y;
			if ((j2 >= -normLen_1 || directionToCam <= 0)
					&& (j2 <= normLen_1 || directionToCam >= 0))
				continue;
			flag = true;
			break;
		}
		if (!flag)
			return true;

		int xSurfRect_0[];
		int ySurfRect_0[];
		if (pointsInCell_0 == 2)
		{ // create rectangle surface
			xSurfRect_0 = new int[4];
			ySurfRect_0 = new int[4];
			int p0_0 = surfaces_0[0];
			int p1_0 = surfaces_0[1];
			xSurfRect_0[0] = (int)model_0.xProjected[p0_0] - 20;
			xSurfRect_0[1] = (int)model_0.xProjected[p1_0] - 20;
			xSurfRect_0[2] = (int)model_0.xProjected[p1_0] + 20;
			xSurfRect_0[3] = (int)model_0.xProjected[p0_0] + 20;
			ySurfRect_0[0] = ySurfRect_0[3] = (int)model_0.yProjected[p0_0];
			ySurfRect_0[1] = ySurfRect_0[2] = (int)model_0.yProjected[p1_0];
		}
		else
		{
			xSurfRect_0 = new int[pointsInCell_0];
			ySurfRect_0 = new int[pointsInCell_0];
			for (int j5 = 0; j5 < pointsInCell_0; j5++) {
				int i6 = surfaces_0[j5];
				xSurfRect_0[j5] = (int)model_0.xProjected[i6];
				ySurfRect_0[j5] = (int)model_0.yProjected[i6];
			}

		}
		int xSurfRect_1[];
		int ySurfRect_1[];
		if (pointsInCell_1 == 2)
		{ // create rectangle surface
			xSurfRect_1 = new int[4];
			ySurfRect_1 = new int[4];
			int p0_1 = surfaces_1[0];
			int p1_1 = surfaces_1[1];
			xSurfRect_1[0] = (int)model_1.xProjected[p0_1] - 20;
			xSurfRect_1[1] = (int)model_1.xProjected[p1_1] - 20;
			xSurfRect_1[2] = (int)model_1.xProjected[p1_1] + 20;
			xSurfRect_1[3] = (int)model_1.xProjected[p0_1] + 20;
			ySurfRect_1[0] = ySurfRect_1[3] = (int)model_1.yProjected[p0_1];
			ySurfRect_1[1] = ySurfRect_1[2] = (int)model_1.yProjected[p1_1];
		}
		else
		{
			xSurfRect_1 = new int[pointsInCell_1];
			ySurfRect_1 = new int[pointsInCell_1];
			for (int l5 = 0; l5 < pointsInCell_1; l5++) {
				int j6 = surfaces_1[l5];
				xSurfRect_1[l5] = (int)model_1.xProjected[j6];
				ySurfRect_1[l5] = (int)model_1.yProjected[j6];
			}

		}
		return !method309(xSurfRect_0, ySurfRect_0, xSurfRect_1, ySurfRect_1);
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
		double a = model_1.xCoordCamDist[surface_1[0]];
		double b = model_1.yCoordCamDist[surface_1[0]];
		double c = model_1.zCoordCamDist[surface_1[0]];
		double n_x = cameraModel_1.xNormal;
		double n_z = cameraModel_1.zNormal;
		double n_y = cameraModel_1.yNormal;
		double k3 = model_1.normalLength[surfaceIdx_1];
		double l3 = cameraModel_1.normalDirectionToCamera;
		boolean flag = false;
		for (int i4 = 0; i4 < ptsPerCell_0; i4++) {
			int i1 = surface_0[i4];
			double k1 = (a - model_0.xCoordCamDist[i1]) * n_x
					+ (b - model_0.yCoordCamDist[i1]) * n_z
					+ (c - model_0.zCoordCamDist[i1]) * n_y;
			if ((k1 >= -k3 || l3 >= 0) && (k1 <= k3 || l3 <= 0))
				continue;
			flag = true;
			break;
		}

		if (!flag)
			return true;
		a = model_0.xCoordCamDist[surface_0[0]];
		b = model_0.yCoordCamDist[surface_0[0]];
		c = model_0.zCoordCamDist[surface_0[0]];
		n_x = cameraModel_0.xNormal;
		n_z = cameraModel_0.zNormal;
		n_y = cameraModel_0.yNormal;
		k3 = model_0.normalLength[surfaceIdx_0];
		l3 = cameraModel_0.normalDirectionToCamera;
		flag = false;
		for (int j4 = 0; j4 < ptsPerCell_1; j4++) {
			int j1 = surface_1[j4];
			double l1 = (a - model_1.xCoordCamDist[j1]) * n_x
					+ (b - model_1.yCoordCamDist[j1]) * n_z
					+ (c - model_1.zCoordCamDist[j1]) * n_y;
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

	public void animateTexture(int texture, int shiftMask)
	{
		if (texturePixels[texture] == null)
			return;
		int sideLen = 1 << textureSize[texture];
		int sizeReverse = sideLen-1;
		int txtrPixels[] = texturePixels[texture];
		if ((shiftMask & 1) == 1)
		{
			int rowsReverse = sideLen*sizeReverse;
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
		if ((shiftMask & 2) == 2)
		{
			// TODO: animate along columns
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

	public int findXOther(int yMinOther, int xNext, int yNext, int xMin, int yMin) {
		if (yMin == yNext)
			return xNext;
		else
			return xNext + ((xMin - xNext) * (yMinOther - yNext)) / (yMin - yNext);
	}

	public boolean method307(int ymin_x, int j, int ymin_x_1,
			int ymin_x_2, boolean ymin_x_not_smallest)
	{
		if (ymin_x_not_smallest && ymin_x <= ymin_x_1
				|| ymin_x < ymin_x_1)
		{
			if (ymin_x > ymin_x_2)
				return true;
			if (j > ymin_x_1)
				return true;
			if (j > ymin_x_2)
				return true;
			return !ymin_x_not_smallest;
		}
		if (ymin_x < ymin_x_2)
			return true;
		if (j < ymin_x_1)
			return true;
		if (j < ymin_x_2)
			return true;
		else
			return ymin_x_not_smallest;
	}

	public boolean method308(int ymin_x_1, int ymin_x_2, int ymin_x, boolean ymin_x_not_smallest) {
		if (ymin_x_not_smallest
				&& ymin_x_1 <= ymin_x
				|| ymin_x_1 < ymin_x)
		{
			if (ymin_x_2 > ymin_x)
				return true;
			return !ymin_x_not_smallest;
		}
		if (ymin_x_2 < ymin_x)
			return true;
		else
			return ymin_x_not_smallest;
	}
	
	private int findMinIdx(int[] arr)
	{
		int min = arr[0], idx = 0;
		for (int i = 1; i < arr.length; i++)
			if (arr[i] < min) {
				min = arr[i];
				idx = i;
			}
		return idx;
	}
	
	private int findMaxIdx(int[] arr)
	{
		int max = arr[0], idx = 0;
		for (int i = 1; i < arr.length; i++)
			if (arr[i] > max) {
				max = arr[i];
				idx = i;
			}
		return idx;
	}

	public boolean method309(int x_0[], int y_0[], int x_1[], int y_1[]) {
		int x_0_len = x_0.length;
		int x_1_len = x_1.length;
		byte byte0 = 0;
		int y_min_idx_0 = findMinIdx(y_0);
		int y_min_0 = y_0[y_min_idx_0];
		int y_max_0 = y_0[findMaxIdx(y_0)];
		int y_min_idx_1 = findMinIdx(y_1);
		int y_min_1 = y_1[y_min_idx_1];
		int y_max_1 = y_1[findMaxIdx(y_1)];

		if (y_min_1 >= y_max_0)
			return false;
		if (y_min_0 >= y_max_1)
			return false;
		int smallest_y_arr_greater_than_other_y_arr_idx;
		int y_min_idx_1_next;
		boolean y1min_x_not_smallest;
		if (y_0[y_min_idx_0] < y_1[y_min_idx_1])
		{
			for (smallest_y_arr_greater_than_other_y_arr_idx = y_min_idx_0; y_0[smallest_y_arr_greater_than_other_y_arr_idx] < y_1[y_min_idx_1];
					smallest_y_arr_greater_than_other_y_arr_idx = (smallest_y_arr_greater_than_other_y_arr_idx + 1) % x_0_len);
			for (; y_0[y_min_idx_0] < y_1[y_min_idx_1];
					y_min_idx_0 = ((y_min_idx_0 - 1) + x_0_len) % x_0_len);
			int y1min_x_1 = findXOther(
					y_1[y_min_idx_1],
					x_0[(y_min_idx_0 + 1) % x_0_len],
					y_0[(y_min_idx_0 + 1) % x_0_len],
					x_0[y_min_idx_0],
					y_0[y_min_idx_0]);
			int y1min_x_2 = findXOther(
					y_1[y_min_idx_1],
					x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
					y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
					x_0[smallest_y_arr_greater_than_other_y_arr_idx],
					y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
			int y1min_x = x_1[y_min_idx_1];
			y1min_x_not_smallest = (y1min_x_1 < y1min_x) | (y1min_x_2 < y1min_x);
			if (method308(y1min_x_1, y1min_x_2, y1min_x,
					y1min_x_not_smallest))
				return true;
			y_min_idx_1_next = (y_min_idx_1 + 1) % x_1_len;
			y_min_idx_1 = ((y_min_idx_1 - 1) + x_1_len) % x_1_len;
			if (y_min_idx_0 == smallest_y_arr_greater_than_other_y_arr_idx)
				byte0 = 1;
		}
		else
		{
			for (y_min_idx_1_next = y_min_idx_1; y_1[y_min_idx_1_next] < y_0[y_min_idx_0];
					y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len);
			for (; y_1[y_min_idx_1] < y_0[y_min_idx_0];
					y_min_idx_1 = ((y_min_idx_1 - 1) + x_1_len) % x_1_len);
			int y0min_x = x_0[y_min_idx_0];
			int y0min_x_1 = findXOther(
					y_0[y_min_idx_0],
					x_1[(y_min_idx_1 + 1) % x_1_len],
					y_1[(y_min_idx_1 + 1) % x_1_len],
					x_1[y_min_idx_1],
					y_1[y_min_idx_1]);
			int y0min_x_2 = findXOther(
					y_0[y_min_idx_0],
					x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
					y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
					x_1[y_min_idx_1_next],
					y_1[y_min_idx_1_next]);
			y1min_x_not_smallest = (y0min_x < y0min_x_1) | (y0min_x < y0min_x_2);
			if (method308(y0min_x_1, y0min_x_2, y0min_x,
					!y1min_x_not_smallest))
				return true;
			smallest_y_arr_greater_than_other_y_arr_idx = (y_min_idx_0 + 1) % x_0_len;
			y_min_idx_0 = ((y_min_idx_0 - 1) + x_0_len) % x_0_len;
			if (y_min_idx_1 == y_min_idx_1_next)
				byte0 = 2;
		}
		while (byte0 == 0)
			if (y_0[y_min_idx_0] < y_0[smallest_y_arr_greater_than_other_y_arr_idx])
			{
				if (y_0[y_min_idx_0] < y_1[y_min_idx_1])
				{
					if (y_0[y_min_idx_0] < y_1[y_min_idx_1_next])
					{
						int y0min_x = x_0[y_min_idx_0];
						int y0min_x_i = findXOther(
								y_0[y_min_idx_0],
								x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
								y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
								x_0[smallest_y_arr_greater_than_other_y_arr_idx],
								y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
						int y0min_x_1 = findXOther(
								y_0[y_min_idx_0],
								x_1[(y_min_idx_1 + 1) % x_1_len],
								y_1[(y_min_idx_1 + 1) % x_1_len],
								x_1[y_min_idx_1],
								y_1[y_min_idx_1]);
						int y0min_x_2 = findXOther(
								y_0[y_min_idx_0],
								x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
								y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
								x_1[y_min_idx_1_next],
								y_1[y_min_idx_1_next]);
						if (method307(y0min_x, y0min_x_i, y0min_x_1, y0min_x_2, y1min_x_not_smallest))
							return true;
						y_min_idx_0 = ((y_min_idx_0 - 1) + x_0_len) % x_0_len;
						if (y_min_idx_0 == smallest_y_arr_greater_than_other_y_arr_idx)
							byte0 = 1;
					} else {
						int j2 = findXOther(
								y_1[y_min_idx_1_next],
								x_0[(y_min_idx_0 + 1) % x_0_len],
								y_0[(y_min_idx_0 + 1) % x_0_len],
								x_0[y_min_idx_0],
								y_0[y_min_idx_0]);
						int i7 = findXOther(
								y_1[y_min_idx_1_next],
								x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
								y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
								x_0[smallest_y_arr_greater_than_other_y_arr_idx],
								y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
						int k11 = findXOther(
								y_1[y_min_idx_1_next],
								x_1[(y_min_idx_1 + 1) % x_1_len],
								y_1[(y_min_idx_1 + 1) % x_1_len],
								x_1[y_min_idx_1],
								y_1[y_min_idx_1]);
						int j16 = x_1[y_min_idx_1_next];
						if (method307(j2, i7, k11, j16, y1min_x_not_smallest))
							return true;
						y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len;
						if (y_min_idx_1 == y_min_idx_1_next)
							byte0 = 2;
					}
				} else if (y_1[y_min_idx_1] < y_1[y_min_idx_1_next]) {
					int k2 = findXOther(
							y_1[y_min_idx_1],
							x_0[(y_min_idx_0 + 1) % x_0_len],
							y_0[(y_min_idx_0 + 1) % x_0_len],
							x_0[y_min_idx_0],
							y_0[y_min_idx_0]);
					int j7 = findXOther(
							y_1[y_min_idx_1],
							x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							x_0[smallest_y_arr_greater_than_other_y_arr_idx],
							y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
					int l11 = x_1[y_min_idx_1];
					int k16 = findXOther(
							y_1[y_min_idx_1],
							x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
							y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
							x_1[y_min_idx_1_next],
							y_1[y_min_idx_1_next]);
					if (method307(k2, j7, l11, k16, y1min_x_not_smallest))
						return true;
					y_min_idx_1 = ((y_min_idx_1 - 1) + x_1_len) % x_1_len;
					if (y_min_idx_1 == y_min_idx_1_next)
						byte0 = 2;
				} else {
					int l2 = findXOther(
							y_1[y_min_idx_1_next],
							x_0[(y_min_idx_0 + 1) % x_0_len],
							y_0[(y_min_idx_0 + 1) % x_0_len], x_0[y_min_idx_0],
							y_0[y_min_idx_0]);
					int k7 = findXOther(
							y_1[y_min_idx_1_next],
							x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							x_0[smallest_y_arr_greater_than_other_y_arr_idx],
							y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
					int i12 = findXOther(
							y_1[y_min_idx_1_next],
							x_1[(y_min_idx_1 + 1) % x_1_len],
							y_1[(y_min_idx_1 + 1) % x_1_len], x_1[y_min_idx_1],
							y_1[y_min_idx_1]);
					int l16 = x_1[y_min_idx_1_next];
					if (method307(l2, k7, i12, l16, y1min_x_not_smallest))
						return true;
					y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len;
					if (y_min_idx_1 == y_min_idx_1_next)
						byte0 = 2;
				}
			}
			else if (y_0[smallest_y_arr_greater_than_other_y_arr_idx] < y_1[y_min_idx_1])
			{
				if (y_0[smallest_y_arr_greater_than_other_y_arr_idx] < y_1[y_min_idx_1_next]) {
					int i3 = findXOther(
							y_0[smallest_y_arr_greater_than_other_y_arr_idx],
							x_0[(y_min_idx_0 + 1) % x_0_len],
							y_0[(y_min_idx_0 + 1) % x_0_len],
							x_0[y_min_idx_0],
							y_0[y_min_idx_0]);
					int l7 = x_0[smallest_y_arr_greater_than_other_y_arr_idx];
					int j12 = findXOther(
							y_0[smallest_y_arr_greater_than_other_y_arr_idx],
							x_1[(y_min_idx_1 + 1) % x_1_len],
							y_1[(y_min_idx_1 + 1) % x_1_len], x_1[y_min_idx_1],
							y_1[y_min_idx_1]);
					int i17 = findXOther(
							y_0[smallest_y_arr_greater_than_other_y_arr_idx],
							x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
							y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
							x_1[y_min_idx_1_next],
							y_1[y_min_idx_1_next]);
					if (method307(i3, l7, j12, i17, y1min_x_not_smallest))
						return true;
					smallest_y_arr_greater_than_other_y_arr_idx = (smallest_y_arr_greater_than_other_y_arr_idx + 1) % x_0_len;
					if (y_min_idx_0 == smallest_y_arr_greater_than_other_y_arr_idx)
						byte0 = 1;
				}
				else
				{
					int j3 = findXOther(
							y_1[y_min_idx_1_next],
							x_0[(y_min_idx_0 + 1) % x_0_len],
							y_0[(y_min_idx_0 + 1) % x_0_len],
							x_0[y_min_idx_0],
							y_0[y_min_idx_0]);
					int i8 = findXOther(
							y_1[y_min_idx_1_next],
							x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							x_0[smallest_y_arr_greater_than_other_y_arr_idx],
							y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
					int k12 = findXOther(
							y_1[y_min_idx_1_next],
							x_1[(y_min_idx_1 + 1) % x_1_len],
							y_1[(y_min_idx_1 + 1) % x_1_len],
							x_1[y_min_idx_1],
							y_1[y_min_idx_1]);
					int j17 = x_1[y_min_idx_1_next];
					if (method307(j3, i8, k12, j17, y1min_x_not_smallest))
						return true;
					y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len;
					if (y_min_idx_1 == y_min_idx_1_next)
						byte0 = 2;
				}
			}
			else if (y_1[y_min_idx_1] < y_1[y_min_idx_1_next])
			{
				int k3 = findXOther(
						y_1[y_min_idx_1],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int j8 = findXOther(
						y_1[y_min_idx_1],
						x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						x_0[smallest_y_arr_greater_than_other_y_arr_idx],
						y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
				int l12 = x_1[y_min_idx_1];
				int k17 = findXOther(
						y_1[y_min_idx_1],
						x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						x_1[y_min_idx_1_next],
						y_1[y_min_idx_1_next]);
				if (method307(k3, j8, l12, k17, y1min_x_not_smallest))
					return true;
				y_min_idx_1 = ((y_min_idx_1 - 1) + x_1_len) % x_1_len;
				if (y_min_idx_1 == y_min_idx_1_next)
					byte0 = 2;
			}
			else
			{
				int l3 = findXOther(
						y_1[y_min_idx_1_next],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int k8 = findXOther(
						y_1[y_min_idx_1_next],
						x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						x_0[smallest_y_arr_greater_than_other_y_arr_idx],
						y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
				int i13 = findXOther(
						y_1[y_min_idx_1_next],
						x_1[(y_min_idx_1 + 1) % x_1_len],
						y_1[(y_min_idx_1 + 1) % x_1_len],
						x_1[y_min_idx_1],
						y_1[y_min_idx_1]);
				int l17 = x_1[y_min_idx_1_next];
				if (method307(l3, k8, i13, l17, y1min_x_not_smallest))
					return true;
				y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len;
				if (y_min_idx_1 == y_min_idx_1_next)
					byte0 = 2;
			}
		while (byte0 == 1)
			if (y_0[y_min_idx_0] < y_1[y_min_idx_1])
			{
				if (y_0[y_min_idx_0] < y_1[y_min_idx_1_next]) {
					int i4 = x_0[y_min_idx_0];
					int j13 = findXOther(
							y_0[y_min_idx_0],
							x_1[(y_min_idx_1 + 1) % x_1_len],
							y_1[(y_min_idx_1 + 1) % x_1_len],
							x_1[y_min_idx_1],
							y_1[y_min_idx_1]);
					int i18 = findXOther(
							y_0[y_min_idx_0],
							x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
							y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
							x_1[y_min_idx_1_next],
							y_1[y_min_idx_1_next]);
					return method308(j13, i18, i4, !y1min_x_not_smallest);
				}
				int j4 = findXOther(
						y_1[y_min_idx_1_next],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int l8 = findXOther(
						y_1[y_min_idx_1_next],
						x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						x_0[smallest_y_arr_greater_than_other_y_arr_idx],
						y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
				int k13 = findXOther(
						y_1[y_min_idx_1_next],
						x_1[(y_min_idx_1 + 1) % x_1_len],
						y_1[(y_min_idx_1 + 1) % x_1_len],
						x_1[y_min_idx_1],
						y_1[y_min_idx_1]);
				int j18 = x_1[y_min_idx_1_next];
				if (method307(j4, l8, k13, j18, y1min_x_not_smallest))
					return true;
				y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len;
				if (y_min_idx_1 == y_min_idx_1_next)
					byte0 = 0;
			} else if (y_1[y_min_idx_1] < y_1[y_min_idx_1_next]) {
				int k4 = findXOther(
						y_1[y_min_idx_1],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int i9 = findXOther(
						y_1[y_min_idx_1],
						x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						x_0[smallest_y_arr_greater_than_other_y_arr_idx],
						y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
				int l13 = x_1[y_min_idx_1];
				int k18 = findXOther(
						y_1[y_min_idx_1],
						x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						x_1[y_min_idx_1_next],
						y_1[y_min_idx_1_next]);
				if (method307(k4, i9, l13, k18, y1min_x_not_smallest))
					return true;
				y_min_idx_1 = ((y_min_idx_1 - 1) + x_1_len) % x_1_len;
				if (y_min_idx_1 == y_min_idx_1_next)
					byte0 = 0;
			} else {
				int l4 = findXOther(
						y_1[y_min_idx_1_next],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int j9 = findXOther(
						y_1[y_min_idx_1_next],
						x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						x_0[smallest_y_arr_greater_than_other_y_arr_idx],
						y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
				int i14 = findXOther(
						y_1[y_min_idx_1_next],
						x_1[(y_min_idx_1 + 1) % x_1_len],
						y_1[(y_min_idx_1 + 1) % x_1_len],
						x_1[y_min_idx_1],
						y_1[y_min_idx_1]);
				int l18 = x_1[y_min_idx_1_next];
				if (method307(l4, j9, i14, l18, y1min_x_not_smallest))
					return true;
				y_min_idx_1_next = (y_min_idx_1_next + 1) % x_1_len;
				if (y_min_idx_1 == y_min_idx_1_next)
					byte0 = 0;
			}
		while (byte0 == 2)
			if (y_1[y_min_idx_1] < y_0[y_min_idx_0])
			{
				if (y_1[y_min_idx_1] < y_0[smallest_y_arr_greater_than_other_y_arr_idx])
				{
					int i5 = findXOther(
							y_1[y_min_idx_1],
							x_0[(y_min_idx_0 + 1) % x_0_len],
							y_0[(y_min_idx_0 + 1) % x_0_len],
							x_0[y_min_idx_0],
							y_0[y_min_idx_0]);
					int k9 = findXOther(
							y_1[y_min_idx_1],
							x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
							x_0[smallest_y_arr_greater_than_other_y_arr_idx],
							y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
					int j14 = x_1[y_min_idx_1];
					return method308(i5, k9, j14, y1min_x_not_smallest);
				}
				int j5 = findXOther(
						y_0[smallest_y_arr_greater_than_other_y_arr_idx],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int l9 = x_0[smallest_y_arr_greater_than_other_y_arr_idx];
				int k14 = findXOther(
						y_0[smallest_y_arr_greater_than_other_y_arr_idx],
						x_1[(y_min_idx_1 + 1) % x_1_len],
						y_1[(y_min_idx_1 + 1) % x_1_len],
						x_1[y_min_idx_1],
						y_1[y_min_idx_1]);
				int i19 = findXOther(
						y_0[smallest_y_arr_greater_than_other_y_arr_idx],
						x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						x_1[y_min_idx_1_next],
						y_1[y_min_idx_1_next]);
				if (method307(j5, l9, k14, i19, y1min_x_not_smallest))
					return true;
				smallest_y_arr_greater_than_other_y_arr_idx = (smallest_y_arr_greater_than_other_y_arr_idx + 1) % x_0_len;
				if (y_min_idx_0 == smallest_y_arr_greater_than_other_y_arr_idx)
					byte0 = 0;
			}
			else if (y_0[y_min_idx_0] < y_0[smallest_y_arr_greater_than_other_y_arr_idx])
			{
				int k5 = x_0[y_min_idx_0];
				int i10 = findXOther(
						y_0[y_min_idx_0],
						x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
						x_0[smallest_y_arr_greater_than_other_y_arr_idx],
						y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
				int l14 = findXOther(
						y_0[y_min_idx_0],
						x_1[(y_min_idx_1 + 1) % x_1_len],
						y_1[(y_min_idx_1 + 1) % x_1_len],
						x_1[y_min_idx_1],
						y_1[y_min_idx_1]);
				int j19 = findXOther(
						y_0[y_min_idx_0],
						x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						x_1[y_min_idx_1_next],
						y_1[y_min_idx_1_next]);
				if (method307(k5, i10, l14, j19, y1min_x_not_smallest))
					return true;
				y_min_idx_0 = ((y_min_idx_0 - 1) + x_0_len) % x_0_len;
				if (y_min_idx_0 == smallest_y_arr_greater_than_other_y_arr_idx)
					byte0 = 0;
			}
			else
			{
				int l5 = findXOther(
						y_0[smallest_y_arr_greater_than_other_y_arr_idx],
						x_0[(y_min_idx_0 + 1) % x_0_len],
						y_0[(y_min_idx_0 + 1) % x_0_len],
						x_0[y_min_idx_0],
						y_0[y_min_idx_0]);
				int j10 = x_0[smallest_y_arr_greater_than_other_y_arr_idx];
				int i15 = findXOther(
						y_0[smallest_y_arr_greater_than_other_y_arr_idx],
						x_1[(y_min_idx_1 + 1) % x_1_len],
						y_1[(y_min_idx_1 + 1) % x_1_len],
						x_1[y_min_idx_1],
						y_1[y_min_idx_1]);
				int k19 = findXOther(
						y_0[smallest_y_arr_greater_than_other_y_arr_idx],
						x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
						x_1[y_min_idx_1_next],
						y_1[y_min_idx_1_next]);
				if (method307(l5, j10, i15, k19, y1min_x_not_smallest))
					return true;
				smallest_y_arr_greater_than_other_y_arr_idx = (smallest_y_arr_greater_than_other_y_arr_idx + 1) % x_0_len;
				if (y_min_idx_0 == smallest_y_arr_greater_than_other_y_arr_idx)
					byte0 = 0;
			}
		if (y_0[y_min_idx_0] < y_1[y_min_idx_1])
		{
			int y0min_x = x_0[y_min_idx_0];
			int y0min_x_1 = findXOther(
					y_0[y_min_idx_0],
					x_1[(y_min_idx_1 + 1) % x_1_len],
					y_1[(y_min_idx_1 + 1) % x_1_len],
					x_1[y_min_idx_1],
					y_1[y_min_idx_1]);
			int y0min_x_2 = findXOther(
					y_0[y_min_idx_0],
					x_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
					y_1[((y_min_idx_1_next - 1) + x_1_len) % x_1_len],
					x_1[y_min_idx_1_next],
					y_1[y_min_idx_1_next]);
			return method308(y0min_x_1, y0min_x_2, y0min_x, !y1min_x_not_smallest);
		}
		int y1min_x_1 = findXOther(
				y_1[y_min_idx_1],
				x_0[(y_min_idx_0 + 1) % x_0_len],
				y_0[(y_min_idx_0 + 1) % x_0_len],
				x_0[y_min_idx_0],
				y_0[y_min_idx_0]);
		int y1min_x_2 = findXOther(
				y_1[y_min_idx_1],
				x_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
				y_0[((smallest_y_arr_greater_than_other_y_arr_idx - 1) + x_0_len) % x_0_len],
				x_0[smallest_y_arr_greater_than_other_y_arr_idx],
				y_0[smallest_y_arr_greater_than_other_y_arr_idx]);
		int y1min_x = x_1[y_min_idx_1];
		return method308(y1min_x_1, y1min_x_2, y1min_x, y1min_x_not_smallest);
	}

	public void moveCamera(double factor, int keyMask)
	{
		if (!mc.getFreeCamera())
			return;
		if ((keyMask & 1) == 1) // left key
			cameraZRot = cameraZRot - 8 & 0x3ff;
		else if ((keyMask & 2) == 2)
			cameraZRot = cameraZRot + 8 & 0x3ff;
		double dx = 0;
		double dy = 0;
		if ((keyMask & 4) == 4)
		{ // key up (move forward)
			dx = -Trig.sin1024[cameraZRot];
			dy = Trig.cos1024[cameraZRot];
		}
		else if ((keyMask & 8) == 8)
		{  // key down (move backward)
			dx = Trig.sin1024[cameraZRot];
			dy = -Trig.cos1024[cameraZRot];
		}
		translateCamera(factor*dx, factor*dy);
	}
	
	public void translateCamera(double x, double y)
	{
		cameraXPos += x;
		cameraYPos += y;
	}

	public double getCameraY()
	{
		return cameraYPos;
	}

	public double getCameraX()
	{
		return cameraXPos;
	}

	public double getCameraZ()
	{
		return cameraZPos;
	}

	public int getCameraZRot()
	{
		return cameraZRot;
	}

	public int getCameraXRot()
	{
		return cameraXRot;
	}

	int nbrColors;
	int colorArray[];
	int colorGradientArray[][];
	int colorGradient[];
	public int lastCameraModelCount;
	public double viewPlaneDist;
	public double drawModelMaxDist;
	public double drawSpriteMaxDist;
	public double fadeFactor;
	public double fadeDist;
	public boolean gradient2Step;
	public double aDouble387;
	public int anInt388;
	private boolean aBoolean;
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
	private double mobWidth[];
	private double mobHeight[];
	private double attackAnimXOffset[];
	public Model spriteModels;
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
	int xScreen[];
	int yScreen[];
	int pointBrightness[];
	double xCoordCamDist[];
	double yCoordCamDist[];
	double zCoordCamDist[];
	boolean lowDef;
	static double xMinHide;
	static double xMaxHide;
	static double yMinHide;
	static double yMaxHide;
	static double distMinHide;
	static double distMaxHide;
	int anInt454;
	int anInt455;

	private class CameraVariables
	{
		public CameraVariables()
		{
		}

		int leftX;
		int rightX;
		int leftXBright;
		int rightXBright;
	}
	
	private class CameraModel
	{

	    public CameraModel()
	    {
	        aBoolean367 = false;
	        anInt369 = -1;
	    }

	    protected double xMin;
	    protected double yMin;
	    protected double xMax;
	    protected double yMax;
	    protected double zMin;
	    protected double zMax;
	    protected Model model;
	    protected int surface;
	    protected double drawOrderVal;
	    protected double xNormal;
	    protected double zNormal;
	    protected double yNormal;
	    protected double normalDirectionToCamera;
	    protected int color;
	    protected boolean aBoolean367;
	    protected int anInt368;
	    protected int anInt369;
	}
}
