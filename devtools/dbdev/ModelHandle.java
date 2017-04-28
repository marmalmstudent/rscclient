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
	
	public void test()
	{
		int nPoints = 5;
		int nSides = 1;
		int[] xCoords = new int[]{1, -1, -1, 1, 0};
		int[] yCoords = new int[]{1, 1, -1, -1, 0};
		int[] zCoords = new int[]{0, 0, 0, 1, 1};
		int[] pointsPerCell = new int[]{5};
		int[] color1 = new int[]{0};
		int[] color2 = new int[]{0};
		int[] someArray = new int[]{1};
		int[][] cellArray = new int[][]{{0,1,2,3,4}};
		byte[] dataIn = new byte[4 + 4 
		                         + 4*xCoords.length 
		                         + 4*yCoords.length 
		                         + 4*zCoords.length 
		                         + 4*pointsPerCell.length
		                         + 4*color1.length
		                         + 4*color2.length
		                         + 4*someArray.length
		                         + 4*cellArray.length*cellArray[0].length];
		int offset = 0;
		offset = DataOperations.write2Bytes(dataIn, offset, nPoints, true);
		offset = DataOperations.write2Bytes(dataIn, offset, nSides, true);
		for (int i = 0; i < xCoords.length; ++i)
			offset = DataOperations.write2Bytes(dataIn, offset, xCoords[i], true);
		for (int i = 0; i < zCoords.length; ++i)
			offset = DataOperations.write2Bytes(dataIn, offset, zCoords[i], true);
		for (int i = 0; i < yCoords.length; ++i)
			offset = DataOperations.write2Bytes(dataIn, offset, yCoords[i], true);

		for (int i = 0; i < pointsPerCell.length; ++i)
			offset = DataOperations.writeByte(dataIn, offset, pointsPerCell[i]);

        for (int i = 0; i < nSides; ++i)
        	offset = DataOperations.write2Bytes(dataIn, offset, color1[i], true);
        for (int i = 0; i < nSides; ++i)
        	offset = DataOperations.write2Bytes(dataIn, offset, color2[i], true);

        for (int i = 0; i < nSides; ++i)
        	offset = DataOperations.writeByte(dataIn, offset, someArray[i]);
        
		for (int i = 0; i < cellArray.length; ++i)
			for (int j = 0; j < cellArray[i].length; ++j)
				offset = DataOperations.writeByte(dataIn, offset, cellArray[i][j]);
		model = new Model(dataIn, true);
		try{
			FileOperations.write(formatSTL(new byte[HEADER_SIZE]), new File("src/org/conf/cachedev/test.stl"));
		}catch(Exception e){e.printStackTrace();}
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
		if (header.length != HEADER_SIZE)
			header = DataOperations.arrayCopy(header, HEADER_SIZE);
		int nTriangles = triangleCellArray.length;
		byte[] data = new byte[HEADER_SIZE+4+nTriangles*(Float.BYTES*(12)+2)];
		int offset = 0;
		offset = DataOperations.writeArray(data, offset, header);
		offset = DataOperations.write4Bytes(data, offset, nTriangles, false);
		for (int i = 0; i < nTriangles; ++i)
		{
			for (float normal : normals[i])
				offset = DataOperations.writeFloat(data, offset, normal, false);
			for (int point : triangleCellArray[i])
			{
				offset = DataOperations.writeFloat(data, offset,
						(float)xCoords[point], false);
				offset = DataOperations.writeFloat(data, offset,
						(float)yCoords[point], false);
				offset = DataOperations.writeFloat(data, offset,
						(float)zCoords[point], false);
			}
			offset = DataOperations.write2Bytes(data, offset, 0, false);
				
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
