package org.conf.cachedev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.DataOperations;

public class Models
{
	private static HashMap<Integer, String> modelNames;
	private Model model;
	
	public Models(File f)
	{
		try {
			modelNames = FileOperations.readHashMap(f);
		} catch(IOException e) {e.printStackTrace();}
	}
	public Models()
	{
		
	}
	
	public static void main(String args[])
	{
		Models m = new Models();
		m.test();
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
		offset = FileOperations.write2Bytes(dataIn, offset, nPoints, true);
		offset = FileOperations.write2Bytes(dataIn, offset, nSides, true);
		for (int i = 0; i < xCoords.length; ++i)
			offset = FileOperations.write2Bytes(dataIn, offset, xCoords[i], true);
		for (int i = 0; i < zCoords.length; ++i)
			offset = FileOperations.write2Bytes(dataIn, offset, zCoords[i], true);
		for (int i = 0; i < yCoords.length; ++i)
			offset = FileOperations.write2Bytes(dataIn, offset, yCoords[i], true);

		for (int i = 0; i < pointsPerCell.length; ++i)
			offset = FileOperations.writeByte(dataIn, offset, pointsPerCell[i]);

        for (int i = 0; i < nSides; ++i)
        	offset = FileOperations.write2Bytes(dataIn, offset, color1[i], true);
        for (int i = 0; i < nSides; ++i)
        	offset = FileOperations.write2Bytes(dataIn, offset, color2[i], true);

        for (int i = 0; i < nSides; ++i)
        	offset = FileOperations.writeByte(dataIn, offset, someArray[i]);
        
		for (int i = 0; i < cellArray.length; ++i)
			for (int j = 0; j < cellArray[i].length; ++j)
				offset = FileOperations.writeByte(dataIn, offset, cellArray[i][j]);
		model = new Model(dataIn);
		try{
			FileOperations.write(formatSTL(new byte[80]), new File("src/org/conf/cachedev/test.stl"));
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void saveSTL(File dst)
	{
		try{
			FileOperations.write(formatSTL(new byte[80]), dst);
		}catch(Exception e){e.printStackTrace();}
	}
	
	private byte[] formatSTL(byte[] header)
	{
		int[] xCoords = model.getXCoords();
		int[] yCoords = model.getYCoords();
		int[] zCoords = model.getZCoords();
		float[][] normals = model.getNormals();
		int[][] triangleCellArray = model.getTriangleCellArray();
		if (header.length != 80)
			header = FileOperations.arrayCopy(header, 80);
		int nTriangles = triangleCellArray.length;
		byte[] data = new byte[80+4+nTriangles*(Float.BYTES*(12)+2)];
		int offset = 0;
		offset = FileOperations.writeArray(data, offset, header);
		offset = FileOperations.write4Bytes(data, offset, nTriangles, false);
		for (int i = 0; i < nTriangles; ++i)
		{
			for (float normal : normals[i])
				offset = FileOperations.writeFloat(data, offset, normal, false);
			for (int point : triangleCellArray[i])
			{
				offset = FileOperations.writeFloat(data, offset,
						(float)xCoords[point], false);
				offset = FileOperations.writeFloat(data, offset,
						(float)yCoords[point], false);
				offset = FileOperations.writeFloat(data, offset,
						(float)zCoords[point], false);
			}
			offset = FileOperations.write2Bytes(data, offset, 0, false);
				
		}
		return data;
	}
	
	public void newModel(File f)
	{
		try
		{
			byte[] data = FileOperations.read(f);
			if (data != null)
				model = new Model(data);
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
