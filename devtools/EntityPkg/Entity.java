package EntityPkg;

import java.util.HashMap;

import dbdev.DataOperations;

public class Entity
{
	private Sprite[] entities;
	private DataOperations dataOps;
	private int entityWidth, entityHeight, imageWidth, imageHeight;
	public static int ROWS;
	public static final int COLS = 3;
	public static final int ROW_SPACE = 5;
	public static final int COL_SPACE = 5;
	private static final byte[] BG_COLOR = {
			(byte)0x0 /* red */,
			(byte)0xff /* green */,
			(byte)0xff /* blue */,
			(byte)0xff /* alpha */
	};
	
	public Entity(Sprite[] entity) throws IllegalArgumentException
	{
		if (!checkTotalSize(entity))
			throw new IllegalArgumentException("image dimensions does not match");
		entities = entity;
		ROWS = entity.length/COLS;
		entityWidth = entity[0].getTotalWidth();
		entityHeight = entity[0].getTotalHeight();
		imageWidth = 2*COL_SPACE + COLS*(entityWidth+COL_SPACE)-COL_SPACE;
		imageHeight = 2*ROW_SPACE + ROWS*(entityHeight+ROW_SPACE)-ROW_SPACE;
	}
	
	public int imageWidth() { return imageWidth; }
	public int imageHeight() { return imageHeight; }
	
	private boolean checkTotalSize(Sprite[] entity)
	{
		int tWidth = entity[0].getTotalWidth();
		int tHeight = entity[0].getTotalHeight();
		int i;
		for (i = 0; i < entity.length
				&& entity[i].getTotalWidth() == tWidth
				&& entity[i].getTotalHeight() == tHeight; ++i);
		return i == entity.length; // loop finished if input is valid
	}
	
	private void setBackground(byte[] data)
	{
		for (int i = 0; i < data.length;)
		{
			data[i++] = BG_COLOR[0];
			data[i++] = BG_COLOR[1];
			data[i++] = BG_COLOR[2];
			data[i++] = BG_COLOR[3]; 
		}
	}
	
	public byte[] formatPNG()
	{
		int imgW = imageWidth, imgH = imageHeight;
		byte[] data = new byte[4*imgW*imgH];
		dataOps = new DataOperations(data);
		setBackground(dataOps.data);
		for (int row = 0; row < ROWS; ++row)
			for (int col = 0; col < COLS; ++col)
				entityImage(row, col);
		return dataOps.data;
	}
	
	private void entityImage(int row, int col)
	{
		int colskip = entityWidth+COL_SPACE;
		int yskip = imageWidth;
		int rowskip = (entityHeight+ROW_SPACE)*yskip;
		entities[row*COLS + col].packImagePNG();
		byte[] entityImage = entities[row*COLS + col].getImage();
		int xLowBnd = entities[row*COLS + col].getXShift();
		int xUprBnd = xLowBnd + entities[row*COLS + col].getWidth();
		int yLowBnd = entities[row*COLS + col].getYShift();
		int yUprBnd = yLowBnd + entities[row*COLS + col].getHeight();
		byte[] transparent = {
				(byte)((entities[row*COLS + col].getTransparendMask() >> 16) & 0xff),
				(byte)((entities[row*COLS + col].getTransparendMask() >> 8) & 0xff),
				(byte)(entities[row*COLS + col].getTransparendMask() & 0xff),
				(byte)((entities[row*COLS + col].getTransparendMask() >> 24) & 0xff)};
		int offset = 0;
		int x = 0, y = 0, i = 0;
		try {
			for (y = 0; y < entityHeight; ++y)
				for (x = 0; x < entityWidth; ++x)
					if (x < xLowBnd || x >= xUprBnd
							|| y < yLowBnd || y >= yUprBnd)
						for (i = 0; i < 4; ++i)
							dataOps.data[4*(row*rowskip + (y+ROW_SPACE)*yskip + col*colskip + (x + COL_SPACE)) + i] = transparent[i];
					else
						for (i = 0; i < 4; ++i)
							dataOps.data[4*(row*rowskip + (y+ROW_SPACE)*yskip + col*colskip + (x + COL_SPACE)) + i] = entityImage[offset++];
		} catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.printf("Row: %d\nCol: %d\ny: %d\nx: %d\ni: %d\n",
					row, col, x, y, i);
		}
	}
}
