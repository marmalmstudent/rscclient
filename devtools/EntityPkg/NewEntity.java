package EntityPkg;

import dbdev.DataOperations;

public class NewEntity
{
	private final String ext = "SPT";
	private String name;
	private int gender, entityWidth, entityHeight, imageWidth, imageHeight;
	private boolean hasAttack, hasFlip;
	private Sprite[] sprites;
	private DataOperations dataOps;
	private static int BORDER_COLOR = 0xff00ffff;
	private static int TRANSPARENT_COLOR = 0xffff00ff;
	private static int COL_SPACE = 5, ROW_SPACE = 5, COLS = 3, ROWS;
	
	public Sprite[] getSprites() { return sprites; }
	public int imageWidth() { return imageWidth; }
	public int imageHeight() { return imageHeight; }
	public String getName() {return name; }
	
	public NewEntity(byte[] data)
	{
		initDat(data);
	}
	
	public NewEntity(int[] pixelData, String name, int gender)
	{
		this.name = name;
		this.gender = gender;
		initPNG(pixelData);
		switch(sprites.length)
		{
		case 18:
			hasAttack = true;
			hasFlip = false;
			break;
		case 24:
			hasAttack = false;
			hasFlip = true;
			break;
		case 27:
			hasAttack = true;
			hasFlip = true;
			break;
		default:
			hasAttack = false;
			hasFlip = false;
			break;
		}
	}

	private void initDat(byte[] data)
	{
		dataOps = new DataOperations(data);
		if (!dataOps.readString().equals(ext))
			return; // wrong/unknown file extension
		name = dataOps.readString();
		gender = dataOps.readByte(false);
		entityWidth = dataOps.readInt(true);
		entityHeight = dataOps.readInt(true);
		hasAttack = dataOps.readBoolean();
		hasFlip = dataOps.readBoolean();
		sprites = new Sprite[15 + (hasAttack?3:0) + (hasFlip?9:0)];
		ROWS = sprites.length / COLS;
		imageWidth = COLS*entityWidth + (COLS+1)*COL_SPACE;
		imageHeight = ROWS*entityHeight + (ROWS+1)*ROW_SPACE;
		for (int i = 0; i < sprites.length; ++i)
		{
			int xShift = dataOps.readInt(true);
			int yShift = dataOps.readInt(true);
			int width = dataOps.readInt(true);
			int height = dataOps.readInt(true);

			int[] pixelData = new int[width*height];
			for (int j = 0; j < pixelData.length;
					pixelData[j++] = dataOps.readInt(true));
			sprites[i] = new Sprite(pixelData, width, height,
					(xShift > 0 || yShift > 0)?true:false,
							xShift, yShift, entityWidth,
							entityHeight, TRANSPARENT_COLOR);
		}
	}

	private void initPNG(int[] data)
	{
		findImageLayout(data);
		imageWidth = COLS*entityWidth + (COLS+1)*COL_SPACE;
		imageHeight = ROWS*entityHeight + (ROWS+1)*ROW_SPACE;
		sprites = new Sprite[ROWS*COLS];
		for (int row = 0; row < ROWS; ++row)
			for (int col = 0; col < COLS; ++col)
				unpackImage(extractSpriteImage(data, row, col), row*COLS + col);
	}
	
	public byte[] formatEntityDat()
	{
		dataOps = new DataOperations(allocateDat());
		dataOps.writeString(ext);
		dataOps.writeString(name);
		dataOps.writeByte(gender);
		dataOps.write4Bytes(entityWidth, true);
		dataOps.write4Bytes(entityHeight, true);
		dataOps.writeBoolean(hasAttack);
		dataOps.writeBoolean(hasFlip);
		for (int i = 0; i < sprites.length; ++i)
		{
			if (sprites[i] == null)
				break;
			dataOps.write4Bytes(sprites[i].getXShift(), true);
			dataOps.write4Bytes(sprites[i].getYShift(), true);
			dataOps.write4Bytes(sprites[i].getWidth(), true);
			dataOps.write4Bytes(sprites[i].getHeight(), true);
			sprites[i].packImageDat();
			dataOps.writeArray(sprites[i].getImage());
		}
		return dataOps.data;
	}
	
	public byte[] formatEntityPNG()
	{
		int imageWidth = COLS*entityWidth + (COLS+1)*COL_SPACE;
		int imageHeight = ROWS*entityHeight + (ROWS+1)*ROW_SPACE;
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
		int yskip = COLS*entityWidth + (COLS+1)*COL_SPACE; // image width
		int rowskip = (entityHeight+ROW_SPACE)*yskip;
		sprites[row*COLS + col].packImagePNG();
		byte[] entityImage = sprites[row*COLS + col].getImage();
		int xLowBnd = sprites[row*COLS + col].getXShift();
		int xUprBnd = xLowBnd + sprites[row*COLS + col].getWidth();
		int yLowBnd = sprites[row*COLS + col].getYShift();
		int yUprBnd = yLowBnd + sprites[row*COLS + col].getHeight();
		byte[] transparent = {
				(byte)((TRANSPARENT_COLOR >> 16) & 0xff),
				(byte)((TRANSPARENT_COLOR >> 8) & 0xff),
				(byte)(TRANSPARENT_COLOR & 0xff),
				(byte)((TRANSPARENT_COLOR >> 24) & 0xff)};
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
	
	private void unpackImage(int[] data, int spriteIdx)
	{
		int xShift = 0, yShift = 0, width = 0, height = 0;
		// find yShift
		lab:
			for (int y = 0; y < entityHeight; ++y)
				for (int x = 0; x < entityWidth; ++x)
					if (data[y*entityWidth + x] != 0)
					{
						yShift = y;
						break lab;
					}
		// find height
		lab:
			for (int y = entityHeight-1; y >= 0; --y)
				for (int x = 0; x < entityWidth; ++x)
					if (data[y*entityWidth + x] != 0)
					{
						height = (y+1) - yShift;
						break lab;
					}
		// find x-shift
		lab:
			for (int x = 0; x < entityWidth; ++x)
				for (int y = 0; y < entityHeight; ++y)
					if (data[y*entityWidth + x] != 0)
					{
						xShift = x;
						break lab;
					}
		// find width
		lab:
			for (int x = entityWidth-1; x >= 0; --x)
				for (int y = 0; y < entityHeight; ++y)
					if (data[y*entityWidth + x] != 0)
					{
						width = (x+1) - xShift;
						break lab;
					}
		// create image data without empty rows and cols
		int[] image = new int[width*height];
		setBackground(image);
		int offset = yShift*entityWidth + xShift;
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
			offset += entityWidth - width;
		}
		sprites[spriteIdx] = new Sprite(image, width, height,
				xShift > 0 || yShift > 0, xShift, yShift,
				entityWidth, entityHeight, TRANSPARENT_COLOR);
	}
	
	/* Extract sprite image data at row, col from the big image */
	private int[] extractSpriteImage(int[] data, int row, int col)
	{
		int imageWidth = COLS*entityWidth + (COLS+1)*COL_SPACE;
		// nbr pixels between two columns
		int colskip = entityWidth+COL_SPACE;
		// nbr pixels between adjacent pixels in y-direction
		int yskip = imageWidth;
		// nbr pixels between two rows
		int rowskip = (entityHeight+ROW_SPACE)*yskip;
		int offset = ROW_SPACE*imageWidth + COL_SPACE + row*rowskip + col*colskip;
		int[] pixels = new int[entityWidth*entityHeight];
		for (int y = 0; y < entityHeight; ++y)
		{
			for (int x = 0; x < entityWidth; ++x)
				pixels[y*entityWidth + x] = data[offset++];
			offset += imageWidth - entityWidth;
		}
		return pixels;
	}
	
	private void findImageLayout(int[] data)
	{
		int offset = 0;
		// skip empy
		for (; data[offset] == BORDER_COLOR; ++offset);

		int tmp = offset;
		// find width of normal image
		for (entityWidth = 0; data[offset] != BORDER_COLOR;
				++offset, ++entityWidth);
		// find column space
		for (COL_SPACE = 0; data[offset] == BORDER_COLOR;
				++offset, ++COL_SPACE);
		// find number of columns
		for(COLS = 1; data[offset] !=BORDER_COLOR
				&& data[offset-1] == BORDER_COLOR;
				offset += entityWidth + COL_SPACE, ++COLS);

		// move cursor back to upper left corner of first image.
		offset = tmp;
		int imageWidth = COLS*entityWidth + (COLS+1)*COL_SPACE;
		// find image height
		for (entityHeight = 0; data[offset] != BORDER_COLOR;
				offset += imageWidth, ++entityHeight);
		// find row space
		for (ROW_SPACE = 0; offset < data.length
				&& data[offset] == BORDER_COLOR;
				offset += imageWidth, ++ROW_SPACE);
		// find number of rows
		for(ROWS = 1; offset < data.length
				&& data[offset] !=BORDER_COLOR
				&& data[offset-imageWidth] == BORDER_COLOR;
				offset += imageWidth*(entityHeight + ROW_SPACE), ++ROWS);
	}
	
	private void setBackground(int[] data)
	{
		for (int i = 0; i < data.length;
				data[i++] = TRANSPARENT_COLOR);
	}
	
	private void setBackground(byte[] data)
	{
		for (int i = 0; i < data.length;)
		{
			data[i++] = (byte)((BORDER_COLOR >> 16) & 0xff);
			data[i++] = (byte)((BORDER_COLOR >> 8) & 0xff);
			data[i++] = (byte)(BORDER_COLOR & 0xff);
			data[i++] = (byte)((BORDER_COLOR >> 24) & 0xff);
		}
	}
	
	private byte[] allocateDat()
	{
		int size = (ext.length()+1) + (name.length()+1) + 11;
		for (Sprite spt : sprites)
		{
			if (spt == null)
				break;
			size += 16 + spt.getImage().length;
		}
		return new byte[size];
	}
}
