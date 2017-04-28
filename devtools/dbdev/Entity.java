package dbdev;

import java.util.HashMap;

public class Entity
{
	private Sprite[] entities;
	private int entityWidth, entityHeight, imageWidth, imageHeight;
	public static final int ROWS = 5;
	public static final int COLS = 3;
	public static final int ROW_SPACE = 5;
	public static final int COL_SPACE = 5;
	public static final int X_MARGIN = 5;
	public static final int Y_MARGIN = 5;
	private static final byte[] BG_COLOR = {
			(byte)0xff /* red */,
			(byte)0x0 /* green */,
			(byte)0xff /* blue */,
			(byte)0xff /* alpha */
	};
	
	public Entity(Sprite[] entity) throws IllegalArgumentException
	{
		if (!checkTotalSize(entity))
			throw new IllegalArgumentException("image dimensions does not match");
		entities = entity;
		entityWidth = entity[0].getTotalWidth();
		entityHeight = entity[0].getTotalHeight();
		imageWidth = 2*X_MARGIN + COLS*(entityWidth+COL_SPACE)-COL_SPACE;
		imageHeight = 2*Y_MARGIN + ROWS*(entityHeight+ROW_SPACE)-ROW_SPACE;
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
		setBackground(data);
		for (int row = 0; row < ROWS; ++row)
			for (int col = 0; col < COLS; ++col)
				entityImage(data, row, col);
		return data;
	}
	
	private void entityImage(byte[] data, int row, int col)
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
		byte[] tmp = new byte[4];
		DataOperations.write4Bytes(tmp, 0, entities[row*COLS + col].getTransparendMask(), true);
		byte[] transparent = {tmp[1], tmp[2], tmp[3], tmp[0]};
		int offset = 0;
		int x = 0, y = 0, i = 0;
		try {
			for (y = 0; y < entityHeight; ++y)
				for (x = 0; x < entityWidth; ++x)
					if (x < xLowBnd || x >= xUprBnd
							|| y < yLowBnd || y >= yUprBnd)
						for (i = 0; i < 4; ++i)
							data[4*(row*rowskip + (y+Y_MARGIN)*yskip + col*colskip + (x + X_MARGIN)) + i] = transparent[i];
					else
						for (i = 0; i < 4; ++i)
							data[4*(row*rowskip + (y+Y_MARGIN)*yskip + col*colskip + (x + X_MARGIN)) + i] = entityImage[offset++];
		} catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.printf("Row: %d\nCol: %d\ny: %d\nx: %d\ni: %d\n",
					row, col, x, y, i);
		}
	}
}
