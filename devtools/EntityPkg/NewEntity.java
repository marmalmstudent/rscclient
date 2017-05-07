package EntityPkg;

import dbdev.DataOperations;

public class NewEntity
{
	private String name;
	private int gender, normWidth, normHeight,
	atkWidth, atkHeight;
	private boolean hasAttack, hasFlip;
	private Sprite[] sprites;
	private DataOperations dataOps;
	private static int BORDER_COLOR = 0xff00ffff; // cyan-ish
	private static int TRANSPARENT_COLOR = 0xffff00ff; // magenta-ish
	private static int COL_SPACE, ROW_SPACE, COLS = 3, ROWS;
	
	private byte[] data;
	
	public NewEntity(Sprite[] spt)
	{
		sprites = spt;
	}
	
	public Sprite[] getSprites()
	{
		return sprites;
	}
	
	public NewEntity(byte[] data)
	{
		initDat(data);
	}
	
	public NewEntity(int[] pixelData)
	{
		initPNG(pixelData);
	}
	
	private void initDat(byte[] data)
	{
		/* unpack entity in dat-form: unpack header and
		 * create the sprites.
		 */
	}

	private void initPNG(int[] data)
	{
		findImageDimensions(data);
		System.out.printf("%d, %d, %d, %d, %d, %d", normHeight, ROW_SPACE, ROWS, normWidth, COL_SPACE, COLS);
		sprites = new Sprite[ROWS*COLS];
		for (int row = 0; row < ROWS; ++row)
			for (int col = 0; col < COLS; ++col)
				unpackImage(imageData(data, row, col), row, col, row*COLS + col);
	}
	
	private void addAttackPNG(int[] data)
	{
		findImageDimensions(data);
		sprites = extendSprites(3);
		for (int row = 0; row < ROWS; ++row)
			for (int col = 0; col < COLS; ++col)
				unpackImage(imageData(data, row, col), row, col, 0);
		
	}
	
	private void unpackImage(int[] data, int row, int col, int spriteIdx)
	{
		int xShift = 0, yShift = 0, width = 0, height = 0;
		// find yShift
		lab:
			for (int y = 0; y < normHeight; ++y)
				for (int x = 0; x < normWidth; ++x)
					if (data[y*normWidth + x] != 0)
					{
						yShift = y;
						break lab;
					}
		// find height
		lab:
			for (int y = normHeight-1; y >= 0; --y)
				for (int x = 0; x < normWidth; ++x)
					if (data[y*normWidth + x] != 0)
					{
						height = (y+1) - yShift;
						break lab;
					}
		// find x-shift
		lab:
			for (int x = 0; x < normWidth; ++x)
				for (int y = 0; y < normHeight; ++y)
					if (data[y*normWidth + x] != 0)
					{
						xShift = x;
						break lab;
					}
		// find width
		lab:
			for (int x = normWidth-1; x >= 0; --x)
				for (int y = 0; y < normHeight; ++y)
					if (data[y*normWidth + x] != 0)
					{
						width = (x+1) - xShift;
						break lab;
					}
		// create image data without empty rows and cols
		int[] image = new int[width*height];
		setBackground(image);
		int offset = yShift*normWidth + xShift;
		for (int y = 0; y < height; ++y)
		{
			for (int x = 0; x < width; ++x)
			{
				if (data[offset] == TRANSPARENT_COLOR || data[offset] == 0)
				{
					image[y*width + x] = TRANSPARENT_COLOR;
					offset++;
					continue;
				}
				image[y*width + x] = data[offset++];
			}
			offset += normWidth - width;
		}
		sprites[spriteIdx] = new Sprite(image, width, height,
				xShift > 0 || yShift > 0, xShift, yShift,
				normWidth, normHeight, TRANSPARENT_COLOR);
	}
	
	/* Extract sprite image data at row, col from the big image */
	private int[] imageData(int[] data, int row, int col)
	{
		int imageWidth = COLS*normWidth + (COLS+1)*COL_SPACE;
		// nbr pixels between two columns
		int colskip = normWidth+COL_SPACE;
		// nbr pixels between adjacent pixels in y-direction
		int yskip = imageWidth;
		// nbr pixels between two rows
		int rowskip = (normHeight+ROW_SPACE)*yskip;
		int offset = ROW_SPACE*imageWidth + COL_SPACE + row*rowskip + col*colskip;
		int[] pixels = new int[normWidth*normHeight];
		for (int y = 0; y < normHeight; ++y)
		{
			for (int x = 0; x < normWidth; ++x)
				pixels[y*normWidth + x] = data[offset++];
			offset += imageWidth - normWidth;
		}
		return pixels;
	}
	
	private void findImageDimensions(int[] data)
	{
		int offset = 0;
		// skip empy
		for (; data[offset] == BORDER_COLOR; ++offset);

		int tmp = offset;
		// find width of normal image
		for (normWidth = 0; data[offset] != BORDER_COLOR; ++offset, ++normWidth);
		// find column space
		for (COL_SPACE = 0; data[offset] == BORDER_COLOR; ++offset, ++COL_SPACE);
		// find number of columns
		for(COLS = 1; data[offset] !=BORDER_COLOR
				&& data[offset-1] == BORDER_COLOR;
				offset += normWidth + COL_SPACE, ++COLS);

		// move cursor back to upper left corner of first image.
		offset = tmp;
		int imageWidth = COLS*normWidth + (COLS+1)*COL_SPACE;
		// find image height
		for (normHeight = 0; data[offset] != BORDER_COLOR;
				offset += imageWidth, ++normHeight);
		// find row space
		for (ROW_SPACE = 0; data[offset] == BORDER_COLOR;
				offset += imageWidth, ++ROW_SPACE);
		// find number of rows
		for(ROWS = 1; offset < data.length
				&& data[offset] !=BORDER_COLOR
				&& data[offset-imageWidth] == BORDER_COLOR;
				offset += imageWidth*(normHeight + ROW_SPACE), ++ROWS);
	}
	
	private void setBackground(int[] data)
	{
		for (int i = 0; i < data.length;
				data[i++] = TRANSPARENT_COLOR);
	}
	
	private Sprite[] extendSprites(int number)
	{
		Sprite[] tmp = new Sprite[sprites.length + number];
		for (int i = 0; i < sprites.length; ++i)
			tmp[i] = sprites[i];
		return tmp;
	}
	
	/* ###################################################### */
	
	private byte[] formatDat()
	{
		byte[] data = new byte[getHeaderSize() + getSpriteDataSize()];
		dataOps = new DataOperations(data);
		makeHeader();
		makeSpriteData();
		return dataOps.data;
	}
	
	private void unpack(byte[] data)
	{
		
	}
	
	private int getHeaderSize()
	{
		return (name.length()+1) + 19;
	}
	
	private void makeHeader()
	{
		byte[] etyName = name.toUpperCase().getBytes();
		dataOps.writeArray(etyName);
		dataOps.writeByte(0);
		dataOps.writeByte(gender);
		dataOps.write4Bytes(normWidth, true);
		dataOps.write4Bytes(normHeight, true);
		dataOps.writeByte(hasAttack ? 1 : 0);
		dataOps.write4Bytes(atkWidth, true);
		dataOps.write4Bytes(atkHeight, true);
		dataOps.writeByte(hasFlip ? 1 : 0);
	}
	
	private int getSpriteDataSize()
	{
		int dataSize = 0;
		int i;
		for (i = 0; i < 15; ++i)
			dataSize += 16 + 4*sprites[i].getHeight()*sprites[i].getWidth();
		if (hasAttack)
		{
			int j;
			for (j = i; j < i + 3; ++j)
				dataSize += 16 + 4*sprites[i].getHeight()*sprites[i].getWidth();
			i = j;
		}
		if (hasFlip)
		{
			int j;
			for (j = i; j < i + 9; ++j)
				dataSize += 16 + 4*sprites[i].getHeight()*sprites[i].getWidth();
			i = j;
		}
		return dataSize;
	}
	
	private void makeSpriteData()
	{
		int i;
		for (i = 0; i < 15; ++i)
		{
			dataOps.write4Bytes(sprites[i].getXShift(), true);
			dataOps.write4Bytes(sprites[i].getYShift(), true);
			dataOps.write4Bytes(sprites[i].getWidth(), true);
			dataOps.write4Bytes(sprites[i].getHeight(), true);
			dataOps.writeArray(sprites[i].getImage());
		}
		if (hasAttack)
		{
			int j;
			for (j = i; j < i + 3; ++j)
			{
				dataOps.write4Bytes(sprites[i].getXShift(), true);
				dataOps.write4Bytes(sprites[i].getYShift(), true);
				dataOps.write4Bytes(sprites[i].getWidth(), true);
				dataOps.write4Bytes(sprites[i].getHeight(), true);
				dataOps.writeArray(sprites[i].getImage());
			}
			i = j;
		}
		if (hasFlip)
		{
			int j;
			for (j = i; j < i + 9; ++j)
			{
				dataOps.write4Bytes(sprites[i].getXShift(), true);
				dataOps.write4Bytes(sprites[i].getYShift(), true);
				dataOps.write4Bytes(sprites[i].getWidth(), true);
				dataOps.write4Bytes(sprites[i].getHeight(), true);
				dataOps.writeArray(sprites[i].getImage());
			}
			i = j;
		}
	}
}
