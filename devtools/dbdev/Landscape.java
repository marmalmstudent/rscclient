package dbdev;

import java.io.File;
import java.io.IOException;

public class Landscape
{
	/*Belongs to LandscapeHandle*/
	private static final int HEADER_SIZE = 80;

	public static final int LandscapeWidth = 48;
	public static final int LandscapeHeight = 48;
	public static final int TileSize = 128;
	public static final int LandscapeSize = LandscapeWidth*LandscapeHeight;
	private int[] groundElevation, groundTexture,
	groundOverlay, roofTexture, horizontalWall,
	verticalWall, diagonalWalls;
	private float[] xCoords, yCoords, zCoords;
	private float[][] normals;
	private int[][] triangleCellArray;

	public float[] getXCoords() { return xCoords; }
	public float[] getYCoords() { return yCoords; }
	public float[] getZCoords() { return zCoords; }
	public int[] getGroundTexture() { return groundTexture; }
	public int[] getGroundOverlay() { return groundOverlay; }
	public float[][] getNormals() { return normals; }
	public int[][] getTriangleCellArray() { return triangleCellArray; }
	
	public Landscape(byte[] data, boolean isLandscape)
	{
		if (isLandscape)
		{
			try{
				initLandscape(data);
			} catch (IOException ioe) { ioe.printStackTrace(); }
		}
	}

	public void initLandscape(byte[] data) throws IOException
	{
		if (data.length < 10*LandscapeSize)
			throw new IOException("Provided buffer too short");
		groundElevation = new int[LandscapeSize];
		groundTexture = new int[LandscapeSize];
		groundOverlay = new int[LandscapeSize];
		roofTexture = new int[LandscapeSize];
		horizontalWall = new int[LandscapeSize];
		verticalWall = new int[LandscapeSize];
		diagonalWalls = new int[LandscapeSize];
		for (int i = 0, offset = 0; offset < LandscapeSize; ++offset, i += 4)
		{
			groundElevation[offset] = data[i++] & 0xff;
			groundTexture[offset] = data[i++];
			groundOverlay[offset] = data[i++];
			roofTexture[offset] = data[i++];
			horizontalWall[offset] = data[i++];
			verticalWall[offset] = data[i++];
			diagonalWalls[offset] = DataOperations.readInt(data, i, true);
		}
		formatCoords();
		initNormals();
	}

	public void formatCoords()
	{
		xCoords = new float[LandscapeSize];
		yCoords = new float[LandscapeSize];
		zCoords = new float[LandscapeSize];
		for (int y = 0; y < LandscapeHeight; ++y)
		{
			for (int x = 0; x < LandscapeWidth; ++x)
			{
				xCoords[y*LandscapeWidth + x] = x;
				yCoords[y*LandscapeWidth + x] = y;
				zCoords[y*LandscapeWidth + x] = (float)groundElevation[y*LandscapeWidth + x]/(float)(TileSize/4);
			}
		}
	}

	private void initNormals()
	{
		int nTriangles = (LandscapeWidth-1)*(LandscapeHeight-1)*2;
		triangleCellArray = new int[nTriangles][];
		int offs = 0;
		for (int x = 0; x < LandscapeWidth-1; ++x)
		{
			for (int y = 0; y < LandscapeHeight-1; ++y)
			{
				int[] cellPoints = {
						y*LandscapeWidth + x,
						y*LandscapeWidth + x+1,
						(y+1)*LandscapeWidth + x+1,
						(y+1)*LandscapeWidth + x};
				if (cellPoints.length > 3)
				{
					int[][] sortedPoints = polyhedronToTriangles(cellPoints);
					for (int[] pts : sortedPoints)
						triangleCellArray[offs++] = pts;
				}
				else
					triangleCellArray[offs++] = cellPoints;
			}
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

	public static void main(String[] args)
	{
		/*
		try {
			Landscape l = new Landscape(FileOperations.read(new File(CDConst.LandscapesDir+"h0x58y48")), true);
			l.saveSTL(new File(CDConst.LandscapesDir+"h0x58y48.stl"));
		} catch (IOException ioe) { ioe.printStackTrace(); }
		*/
	}
}
