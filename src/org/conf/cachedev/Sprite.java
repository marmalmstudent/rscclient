package org.conf.cachedev;

public class Sprite
{
	protected int width, height, xShift, yShift,
	totalWidth, totalHeight, transparentMask;
	protected boolean requiresShift;
	protected byte[] header, image, data;
	protected int[] pixelData;
	protected String entryName;
	private static final int HEADER_SIZE = 25;
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public boolean getRequiresShift() { return requiresShift; }
	public int getXShift() { return xShift; }
	public int getYShift() { return yShift; }
	public int getTotalWidth() { return totalWidth; }
	public int getTotalHeight() { return totalHeight; }
	public int[] getPixelData() { return pixelData; }
	public byte[] getHeader() { return header; }
	public byte[] getImage() { return image; }
	public byte[] getData() { return data; }

	public Sprite(byte[] data, int transparentMask)
	{
		this.transparentMask = transparentMask;
		int offset = 0;
		width = DataOperations.readInt(data, offset, true);
		offset += 4;
		height = DataOperations.readInt(data, offset, true);
		offset += 4;
		requiresShift = DataOperations.readBoolean(data, offset);
		offset += 1;
		xShift = DataOperations.readInt(data, offset, true);
		offset += 4;
		yShift = DataOperations.readInt(data, offset, true);
		offset += 4;
		totalWidth = DataOperations.readInt(data, offset, true);
		offset += 4;
		totalHeight = DataOperations.readInt(data, offset, true);
		offset += 4;

		pixelData = new int[(data.length - offset)/4];
		for (int i = offset, imageOffset = 0; i < data.length; i += 4)
			pixelData[imageOffset++] = DataOperations.readInt(data, i, true);
		datToPNGTransparent();
	}
	
	public Sprite(int[] pixelData, int width, int height,
			boolean requiresShift, int xShift, int yShift,
			int totalWidth, int totalHeight, int transparentMask)
	{
		this.pixelData = pixelData;
		this.width = width;
		this.height = height;
		this.requiresShift = requiresShift;
		this.xShift = xShift;
		this.yShift = yShift;
		this.totalWidth = totalWidth;
		this.totalHeight = totalHeight;
		this.transparentMask = transparentMask;
		pngToDatTransparent();
	}
	
	/**
	 * Formats the header data and image data into
	 * the data array.
	 */
	protected void packDataDat()
	{
		data = new byte[HEADER_SIZE + width*height*4];
		int offset = 0;
		DataOperations.write4Bytes(data, offset, width, true);
		offset += 4;
		DataOperations.write4Bytes(data, offset, height, true);
		offset += 4;
		DataOperations.writeBoolean(data, offset, requiresShift);
		offset += 1;
		DataOperations.write4Bytes(data, offset, xShift, true);
		offset += 4;
		DataOperations.write4Bytes(data, offset, yShift, true);
		offset += 4;
		DataOperations.write4Bytes(data, offset, totalWidth, true);
		offset += 4;
		DataOperations.write4Bytes(data, offset, totalHeight, true);
		offset += 4;
		packImageDat();
		DataOperations.writeArray(data, offset, image);
	}

	/**
	 * Formats the data contained in pixelData into a dat image
	 * format (i.e. argb, where the alpha channel is zero).
	 */
	protected void packImageDat()
	{
		image = new byte[4*pixelData.length];
		int offset = 0;
		for (int i : pixelData)
		{
			image[offset++] = (byte)0;
			image[offset++] = (byte)((i >> 16) & 0xff);
			image[offset++] = (byte)((i >> 8) & 0xff);
			image[offset++] = (byte)(i & 0xff);
		}
	}
	
	protected void packDataPNG()
	{
		data = new byte[width*height*4];
		packImagePNG();
		DataOperations.writeArray(data, 0, image);
	}

	/**
	 * Formats the data contained in pixelData into a png image
	 * format (i.e. argb).
	 */
	protected void packImagePNG()
	{
		image = new byte[4*pixelData.length];
		int offset = 0;
		for (int i : pixelData)
		{
			image[offset++] = (byte)((i >> 16) & 0xff);
			image[offset++] = (byte)((i >> 8) & 0xff);
			image[offset++] = (byte)(i & 0xff);
			image[offset++] = (byte)((i >> 24) & 0xff);
		}
	}

	/**
	 * Converts pixels that are transparent in a png format (i.e.
	 * alpha channel, 0xff000000, is zero) into pixels that are
	 * transparent in a dat format (i.e. 0xff00ff).
	 */
	protected void pngToDatTransparent() {
		for (int i = 0; i < pixelData.length; ++i)
			if ((pixelData[i] & 0xff000000) == 0)
				pixelData[i] = transparentMask;
	}

	/**
	 * Converts pixels that are transparent in a dat format (i.e.
	 * 0xff00ff) into pixels that are transparent in a png format
	 * (i.e. alpha channel is zero).
	 */
	protected void datToPNGTransparent() {
		for (int i = 0; i < pixelData.length; ++i)
			if (pixelData[i] == (transparentMask & 0xffffff))
				pixelData[i] = 0;
			else
				pixelData[i] = 0xff000000 + (pixelData[i] & 0xffffff);
	}
}
