package org.conf.cachedev;


public class Model
{
	private int fileSize; // int
	private int nPoints, nSides; // unsigned short
	private int[] xCoords, yCoords, zCoords; // signed short
	private int[] pointsPerCell; // unsigned byte
	private int[] color1, color2; // signed short
	private int[] someArray; // unsigned byte
	private int[][] cellArray; // unsigned byte or unsigned short
	private float[][] normals;
	private int[][] triangleCellArray;
	
	public int getFileSize() { return fileSize; }
	public int getNPoints() { return nPoints; }
	public int getNSides() { return nSides; }
	public int[] getXCoords() { return xCoords; }
	public int[] getYCoords() { return yCoords; }
	public int[] getZCoords() { return zCoords; }
	public int[] getPointsPerCell() { return pointsPerCell; }
	public int[] getColor1() { return color1; }
	public int[] getColor2() { return color2; }
	public int[] getSomeArray() { return someArray; }
	public int[][] getCellArray() { return cellArray; }
	public float[][] getNormals() { return normals; }
	public int[][] getTriangleCellArray() { return triangleCellArray; }
	
	public Model(byte[] data)
	{
		int offset = 0;
		fileSize = DataOperations.readInt(data, offset, true);
		offset += 4;
        nPoints = DataOperations.read2Bytes(data, offset, false, true);
        offset += 2;
        nSides = DataOperations.read2Bytes(data, offset, false, true);
        offset += 2;
        xCoords = new int[nPoints];
        yCoords = new int[nPoints];
        zCoords = new int[nPoints];
        pointsPerCell = new int[nSides];
        color1 = new int[nSides];
        color2 = new int[nSides];
        someArray = new int[nSides];
        cellArray = new int[nSides][];
        for (int i = 0; i < nPoints; offset += 2)
        	xCoords[i++] = DataOperations.read2Bytes(data, offset, true, true);
        for (int i = 0; i < nPoints; offset += 2)
        	zCoords[i++] = DataOperations.read2Bytes(data, offset, true, true);
        for (int i = 0; i < nPoints; offset += 2)
        	yCoords[i++] = DataOperations.read2Bytes(data, offset, true, true);
        for (int i = 0; i < nSides; i++)
            pointsPerCell[i] = data[offset++] & 0xff;

        for (int i = 0; i < nSides; offset += 2)
        	color1[i++] = DataOperations.read2Bytes(data, offset, true, true);
        for (int i = 0; i < nSides; offset += 2)
        	color2[i++] = DataOperations.read2Bytes(data, offset, true, true);

        for (int i = 0; i < nSides; i++)
        	someArray[i] = data[offset++] & 0xff;

        for (int i = 0; i < nSides; i++)
        {
        	cellArray[i] = new int[pointsPerCell[i]];
            for (int j = 0; j < pointsPerCell[i]; j++)
                if (nPoints < 256) {
                	cellArray[i][j] = data[offset++] & 0xff;
                } else {
                	cellArray[i][j] = DataOperations.read2Bytes(data, offset, false, true);
                    offset += 2;
                }
        }
        initNormals();
	}
	
	private void initNormals()
	{
		int nTriangles = 0;
		for (int i : pointsPerCell)
			nTriangles += (i-2);
		triangleCellArray = new int[nTriangles][];
		int offs = 0;
		for (int i = 0; i < nSides; ++i)
		{
			int[] cellPoints = cellArray[i];
			if (cellPoints.length > 3)
			{
				int[][] sortedPoints = polyhedronToTriangles(cellPoints);
				for (int[] pts : sortedPoints)
					triangleCellArray[offs++] = pts;
			}
			else
				triangleCellArray[offs++] = cellPoints;
		}
		writeNormals();
	}
	
	private void writeNormals()
	{
		int nTriangles = triangleCellArray.length;
		normals = new float[nTriangles][];
		for (int i = 0; i < nTriangles; ++i)
			normals[i] = calculateNormal(triangleCellArray[i]);
	}
	
	private float[] calculateNormal(int[] cellPoints)
	{
		float[] p1 = {
				xCoords[cellPoints[1]]-xCoords[cellPoints[0]],
				yCoords[cellPoints[1]]-yCoords[cellPoints[0]],
				zCoords[cellPoints[1]]-zCoords[cellPoints[0]]
		};
		float[] p2 = {
				xCoords[cellPoints[2]]-xCoords[cellPoints[0]],
				yCoords[cellPoints[2]]-yCoords[cellPoints[0]],
				zCoords[cellPoints[2]]-zCoords[cellPoints[0]]
		};
			
		float[] out = new float[3];
		// cross porduct
		for (int i = 0; i < 3; ++i)
			out[i] = p1[(i+1)%3]*p2[(i+2)%3] - p1[(i+2)%3]*p2[(i+1)%3];
		float norm = (float)Math.sqrt(out[0]*out[0] + out[1]*out[1] + out[2]*out[2]);
		for (int i = 0; i < 3; ++i)
			out[i] = out[i]/norm;
		return out;
	}
	
	private int[][] polyhedronToTriangles(int[] cellPoints)
	{
		int[][] newPoints = new int[cellPoints.length-2][];
		int cpOffs = 0;
		for (int i = 0; i < newPoints.length; ++i)
		{
			newPoints[i] = new int[3];
			for (int j = 0; j < 3; ++j)
			{
				newPoints[i][j] = cellPoints[cpOffs];
				cpOffs = (cpOffs + 1) % cellPoints.length;
			}
			cpOffs = (cellPoints.length + cpOffs - 1) % cellPoints.length;
		}
		return newPoints;
	}
}
