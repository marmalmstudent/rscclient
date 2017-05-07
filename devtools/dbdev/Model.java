package dbdev;

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
	private DataOperations dataOps;
	
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
	
	public Model(byte[] data, boolean isOb3)
	{
		if (isOb3)
			initOb3(data);
		else
			initStl(data);
	}
	
	private void initStl(byte[] data)
	{
		// TODO: write some interesting stl (or equivalent) unpacking.
	}
	
	private void initOb3(byte[] data)
	{
		dataOps = new DataOperations(data);
		fileSize = dataOps.readInt(true);
        nPoints = dataOps.read2Bytes(false, true);
        nSides = dataOps.read2Bytes(false, true);
        xCoords = new int[nPoints];
        yCoords = new int[nPoints];
        zCoords = new int[nPoints];
        pointsPerCell = new int[nSides];
        color1 = new int[nSides];
        color2 = new int[nSides];
        someArray = new int[nSides];
        cellArray = new int[nSides][];
        for (int i = 0; i < nPoints;
        		xCoords[i++] = dataOps.read2Bytes(true, true));
        for (int i = 0; i < nPoints;
        		zCoords[i++] = dataOps.read2Bytes(true, true));
        for (int i = 0; i < nPoints;
        		yCoords[i++] = dataOps.read2Bytes(true, true));
        for (int i = 0; i < nSides;
        		pointsPerCell[i++] = dataOps.readByte(false));

        for (int i = 0; i < nSides;
        		color1[i++] = dataOps.read2Bytes(true, true));
        for (int i = 0; i < nSides;
        		color2[i++] = dataOps.read2Bytes(true, true));

        for (int i = 0; i < nSides;
        		someArray[i++] = dataOps.readByte(false));

        for (int i = 0; i < nSides; i++)
        {
        	cellArray[i] = new int[pointsPerCell[i]];
            if (nPoints < 256)
                for (int j = 0; j < pointsPerCell[i];
                		cellArray[i][j++] = dataOps.readByte(false));
            else
                for (int j = 0; j < pointsPerCell[i];
                		cellArray[i][j++] = dataOps.read2Bytes(false, true));
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
