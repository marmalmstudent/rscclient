package dbdev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class LandscapeHandle
{
	private static HashMap<String, String> landscapeNames;
	private static final int HEADER_SIZE = 80;
	private Landscape landscape;
	private DataOperations dataOps;
	
	public LandscapeHandle(File f)
	{
		try {
			landscapeNames = FileOperations.readHashMap(f, ";");
		} catch(IOException e) {e.printStackTrace();}
	}
	
	public HashMap<String, String> getLandscapeNames()
	{
		return landscapeNames;
	}
	
	public String getLandscapeName(int modelID)
	{
		if (landscapeNames.containsKey(modelID))
			return landscapeNames.get(modelID);
		return null;
	}
	
	public String getLandscapeID(String modelName)
	{
		for (Entry<String, String> entry : landscapeNames.entrySet())
			if (entry.getValue().equals(modelName))
				return entry.getKey();
		return null;
	}

	public void saveSTL(File dst)
	{
		try{
			FileOperations.write(formatSTL(new byte[HEADER_SIZE]), dst);
		}catch(Exception e){e.printStackTrace();}
	}

	private byte[] formatSTL(byte[] header)
	{
		float[] xCoords = landscape.getXCoords();
		float[] yCoords = landscape.getYCoords();
		float[] zCoords = landscape.getZCoords();
		float[][] normals = landscape.getNormals();
		int[][] triangleCellArray = landscape.getTriangleCellArray();
		int nTriangles = triangleCellArray.length;
		byte[] data = new byte[HEADER_SIZE+4+nTriangles*(Float.BYTES*(12)+2)];
		dataOps = new DataOperations(data);
		dataOps.writeArray(header);
		dataOps.write4Bytes(nTriangles, false);
		for (int i = 0; i < nTriangles; ++i)
		{
			for (float normal : normals[i])
				dataOps.writeFloat(normal, false);
			for (int point : triangleCellArray[i])
			{
				dataOps.writeFloat((float)xCoords[point], false);
				dataOps.writeFloat((float)yCoords[point], false);
				dataOps.writeFloat((float)zCoords[point], false);
			}
			dataOps.write2Bytes(0, false);

		}
		return data;
	}
	
	public void newLandscape(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				landscape = new Landscape(data, true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void newSTL(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				landscape = new Landscape(data, false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
