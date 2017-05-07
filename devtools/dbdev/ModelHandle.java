package dbdev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class ModelHandle
{
	private static HashMap<String, String> modelNames;
	private static final int HEADER_SIZE = 80;
	private Model model;
	private DataOperations dataOps;
	
	public ModelHandle(File f)
	{
		try {
			modelNames = FileOperations.readHashMap(f, ";");
		} catch(IOException e) {e.printStackTrace();}
	}
	
	public HashMap<String, String> getModelNames()
	{
		return modelNames;
	}
	
	public String getModelName(int modelID)
	{
		if (modelNames.containsKey(modelID))
			return modelNames.get(modelID);
		return null;
	}
	
	public String getModelID(String modelName)
	{
		for (Entry<String, String> entry : modelNames.entrySet())
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
		int[] xCoords = model.getXCoords();
		int[] yCoords = model.getYCoords();
		int[] zCoords = model.getZCoords();
		float[][] normals = model.getNormals();
		int[][] triangleCellArray = model.getTriangleCellArray();
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
	
	public void newModel(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				model = new Model(data, true);
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
				model = new Model(data, false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return modelNames.toString();
	}
}
