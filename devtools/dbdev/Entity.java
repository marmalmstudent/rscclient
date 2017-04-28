package dbdev;

import java.util.HashMap;

public class Entity
{
	private Sprite[] entities;
	private int entityWidth, entityHeight;
	public static final int ROWS = 5;
	public static final int COLS = 3;
	
	public Entity(Sprite[] entity) throws IllegalArgumentException
	{
		if (!checkTotalSize(entity))
			throw new IllegalArgumentException("image dimensions does not match");
		entities = entity;
		entityWidth = entity[0].getTotalWidth();
		entityHeight = entity[0].getTotalHeight();
	}
	
	public int imageWidth() { return COLS*entityWidth; }
	public int imageHeight() { return ROWS*entityHeight; }
	
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
	
	public byte[] formatPNG()
	{
		int imgW = imageWidth(), imgH = imageHeight();
		byte[] data = new byte[4*imgW*imgH];
		for (int row = 0; row < ROWS; ++row)
			for (int col = 0; col < COLS; ++col)
				entityImage(data, row, col);
		return data;
	}
	
	private void entityImage(byte[] data, int row, int col)
	{
		int colskip = entityWidth;
		int yskip = entityWidth*COLS;
		int rowskip = entityHeight*yskip;
		entities[row*COLS + col].packImagePNG();
		byte[] entityImage = entities[row*COLS + col].getImage();
		int xLowBnd = entities[row*COLS + col].getXShift();
		int xUprBnd = xLowBnd + entities[row*COLS + col].getWidth();
		int yLowBnd = entities[row*COLS + col].getYShift();
		int yUprBnd = yLowBnd + entities[row*COLS + col].getHeight();
		byte[] tmp = new byte[4];
		DataOperations.write4Bytes(tmp, 0, entities[row*COLS + col].getTransparendMask(), true);
		byte[] transparent = {tmp[1], tmp[2], tmp[3], tmp[0]};
		System.out.printf("r=%d, g=%d, b=%d, a=%d",
				transparent[0] & 0xff, transparent[1] & 0xff,
				transparent[2] & 0xff, transparent[3] & 0xff);
		int offset = 0;
		int x = 0, y = 0, i = 0;
		System.out.printf("Row: %d\nCol: %d\n", row, col);
		try {
			for (y = 0; y < entityHeight; ++y)
				for (x = 0; x < entityWidth; ++x)
				{
					if (x < xLowBnd || x >= xUprBnd
							|| y < yLowBnd || y >= yUprBnd)
						for (i = 0; i < 4; ++i)
							data[4*(row*rowskip + y*yskip + col*colskip + x) + i] = transparent[i];
					else
					{
						for (i = 0; i < 4; ++i)
							data[4*(row*rowskip + y*yskip + col*colskip + x) + i] = entityImage[offset++];
					}
				}
		} catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.printf("Row: %d\nCol: %d\ny: %d\nx: %d\ni: %d\n",
					row, col, x, y, i);
		}
	}
}
