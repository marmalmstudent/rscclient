package client;

import client.util.Config;
import client.util.DataConversions;
import model.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

public class GameImage implements ImageProducer, ImageObserver
{
    public static final int BACKGROUND = 0x349ed8;
    public static int anInt346;
    public static int anInt347;
    public static int anInt348;
    public static int anInt352;
    public Sprite[] sprites;
    public BufferedImage loginScreen;
    public int gameWindowWidth;
    public int gameWindowHeight;
    public int imagePixelArray[];
    public Image image;
    public boolean lowDef;
    public boolean drawStringShadows;

    public GameImage(int width, int height, int maxSprites, Component component)
    {
        lowDef = false;
        drawStringShadows = false;
        bounds = new Rectangle(0, 0, width, height);
        gameWindowWidth = width;
        gameWindowHeight = height;
        imagePixelArray = new int[width * height];
        sprites = new Sprite[maxSprites];
        if (width > 1 && height > 1 && component != null) {
            colourModel = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
            image = component.createImage(this);

            completePixels();
            component.prepareImage(image, component);
            completePixels();
            component.prepareImage(image, component);
            completePixels();
            component.prepareImage(image, component);
        }
        try {
            entityArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Entity.zip"));
            mediaArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Media.zip"));
            utilArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Util.zip"));
            itemArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Item.zip"));
            logoArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Logo.zip"));
            projectileArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Projectile.zip"));
            textureArchive = new ZipFile(new File(Config.DATABASE_DIR + "Sprites/Texture.zip"));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
        	
        	loginScreen = ImageIO.read(new File(Config.DATABASE_DIR + "Loading.png"));
        } catch (IOException e)
        {
        	e.printStackTrace();
        }
    }

    @Override
    public synchronized void addConsumer(ImageConsumer imageconsumer)
    {
        imageConsumer = imageconsumer;
        imageconsumer.setDimensions(gameWindowWidth, gameWindowHeight);
        imageconsumer.setProperties(null);
        imageconsumer.setColorModel(colourModel);
        imageconsumer.setHints(14);
    }

    @Override
    public synchronized boolean isConsumer(ImageConsumer imageconsumer)
    {
        return imageConsumer == imageconsumer;
    }

    @Override
    public synchronized void removeConsumer(ImageConsumer imageconsumer)
    {
        if (imageConsumer == imageconsumer)
            imageConsumer = null;
    }

    @Override
    public void startProduction(ImageConsumer imageconsumer)
    {
        addConsumer(imageconsumer);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer imageconsumer)
    {
        System.out.println("TDLR");
    }

    @Override
    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) {
        return true;
    }

    public static int convertRGBToLong(int red, int green, int blue)
    {
        return (red << 16) + (green << 8) + blue;
    }
    
    public static void loadFont(String fontName, int fontSize,
    		int fontStyle, boolean addCharWidth, int fontNumber,
    		GameWindow gameWindow)
    {
    	/*
    	String fnts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    	for (String str : fnts)
    	{
    		System.out.println(str);
    	}*/
        Font font = new Font(fontName, fontStyle, fontSize);
        FontMetrics fontmetrics = gameWindow.getFontMetrics(font);
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
        anInt350 = 885; //855
        for (int charSetOffset = 0; charSetOffset < 95; charSetOffset++)
            drawLetter(font, fontmetrics, charSet.charAt(charSetOffset),
            		charSetOffset, gameWindow, fontNumber, addCharWidth);

        fontPixels[fontNumber] = new byte[anInt350];
        for (int i1 = 0; i1 < anInt350; i1++)
            fontPixels[fontNumber][i1] = fontPixelsLoadBuffer[i1];
    }
    
    public static void drawLetter(Font font, FontMetrics fontmetrics,
    		char letter, int charSetOffset, GameWindow gameWindow,
    		int fontNumber, boolean addCharWidth)
    {
        int charWidth = fontmetrics.charWidth(letter);
        int oldCharWidth = charWidth;
        if (addCharWidth)
            try {
                if (letter == '/')
                    addCharWidth = false;
                if (letter == 'f' || letter == 't' || letter == 'w' || letter == 'v' || letter == 'k' || letter == 'x' || letter == 'y' || letter == 'A' || letter == 'V' || letter == 'W')
                    charWidth++;
            }
            catch (Exception _ex) {
            }
        int i1 = fontmetrics.getMaxAscent();
        int j1 = fontmetrics.getMaxAscent() + fontmetrics.getMaxDescent();
        int k1 = fontmetrics.getHeight();
        Image image = gameWindow.createImage(charWidth, j1);
        Graphics g = image.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, charWidth, j1);
        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(String.valueOf(letter), 0, i1);
        if (addCharWidth)
            g.drawString(String.valueOf(letter), 1, i1);
        int ai[] = new int[charWidth * j1];
        PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, charWidth, j1, ai, 0, charWidth);
        try {
            pixelgrabber.grabPixels();
        }
        catch (InterruptedException _ex) {
            return;
        }
        image.flush();
        image = null;
        int l1 = 0;
        int i2 = 0;
        int j2 = charWidth;
        int k2 = j1;
        label0:
        for (int l2 = 0; l2 < j1; l2++) {
            for (int i3 = 0; i3 < charWidth; i3++) {
                int k3 = ai[i3 + l2 * charWidth];
                if ((k3 & 0xffffff) == 0)
                    continue;
                i2 = l2;
                break label0;
            }

        }

        label1:
        for (int j3 = 0; j3 < charWidth; j3++) {
            for (int l3 = 0; l3 < j1; l3++) {
                int j4 = ai[j3 + l3 * charWidth];
                if ((j4 & 0xffffff) == 0)
                    continue;
                l1 = j3;
                break label1;
            }

        }

        label2:
        for (int i4 = j1 - 1; i4 >= 0; i4--) {
            for (int k4 = 0; k4 < charWidth; k4++) {
                int i5 = ai[k4 + i4 * charWidth];
                if ((i5 & 0xffffff) == 0)
                    continue;
                k2 = i4 + 1;
                break label2;
            }

        }
        label3:
        for (int l4 = charWidth - 1; l4 >= 0; l4--) {
            for (int j5 = 0; j5 < j1; j5++) {
                int l5 = ai[l4 + j5 * charWidth];
                if ((l5 & 0xffffff) == 0)
                    continue;
                j2 = l4 + 1;
                break label3;
            }

        }
        fontPixelsLoadBuffer[charSetOffset * 9] = (byte) (anInt350 / 16384);
        fontPixelsLoadBuffer[charSetOffset * 9 + 1] = (byte) (anInt350 / 128 & 0x7f);
        fontPixelsLoadBuffer[charSetOffset * 9 + 2] = (byte) (anInt350 & 0x7f);
        fontPixelsLoadBuffer[charSetOffset * 9 + 3] = (byte) (j2 - l1);
        fontPixelsLoadBuffer[charSetOffset * 9 + 4] = (byte) (k2 - i2);
        fontPixelsLoadBuffer[charSetOffset * 9 + 5] = (byte) l1;
        fontPixelsLoadBuffer[charSetOffset * 9 + 6] = (byte) (i1 - i2);
        fontPixelsLoadBuffer[charSetOffset * 9 + 7] = (byte) oldCharWidth;
        fontPixelsLoadBuffer[charSetOffset * 9 + 8] = (byte) k1;
        for (int k5 = i2; k5 < k2; k5++) {
            for (int i6 = l1; i6 < j2; i6++) {
                int j6 = ai[i6 + k5 * charWidth] & 0xff;
                if (j6 > 30 && j6 < 230)
                    aBooleanArray349[fontNumber] = true;
                fontPixelsLoadBuffer[anInt350++] = (byte) j6;
            }
        }
    }

    public synchronized void completePixels()
    {
        if (imageConsumer == null)
            return;

        imageConsumer.setPixels(0, 0, gameWindowWidth, gameWindowHeight,
        		colourModel, imagePixelArray, 0, gameWindowWidth);
        imageConsumer.imageComplete(ImageConsumer.TOPDOWNLEFTRIGHT);
    }
    
    public boolean loadSprite(int id, String packageName, int amount)
    {
    	ZipFile archive = null;
    	if (packageName.equals("entity"))
    		archive = entityArchive;
    	else if (packageName.equals("media"))
    		archive = mediaArchive;
    	else if (packageName.equals("util"))
    		archive = utilArchive;
    	else if (packageName.equals("item"))
    		archive = itemArchive;
    	else if (packageName.equals("logo"))
    		archive = logoArchive;
    	else if (packageName.equals("projectile"))
    		archive = projectileArchive;
    	else if (packageName.equals("texture"))
    		archive = textureArchive;
    	if (archive == null)
    		return false;
    	for (int i = id; i < id + amount; i++)
    		loadArchive(i, archive);
    	return true;
    }

    public void setDimensions(int x, int y, int width, int height)
    {
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (width > gameWindowWidth)
            width = gameWindowWidth;
        if (height > gameWindowHeight)
            height = gameWindowHeight;

    	bounds.setBounds(x, y, width, height);
    }

    public void resetDimensions()
    {
    	bounds.setBounds(0, 0, gameWindowWidth, gameWindowHeight);
    }

    public void drawImage(Graphics g, int x, int y)
    {
        completePixels();
        g.drawImage(image, x, y, this);
    }

    public void resetImagePixels(int color)
    {
        int size = gameWindowWidth * gameWindowHeight;
        if (!lowDef) {
            for (int j = 0; j < size; ++j)
                imagePixelArray[j] = color;
            return;
        }
        int k = 0;
        for (int l = -gameWindowHeight; l < 0; l += 2)
        {
            for (int i1 = -gameWindowWidth; i1 < 0; i1++)
                imagePixelArray[k++] = color;
            k += gameWindowWidth;
        }

    }

    public void drawCircle(int xCenter, int yCenter,
    		int radius, int fgColor, int fgAlpha)
    {
        int bg_alpha = 256 - fgAlpha;
        int fg_red = (fgColor >> 16 & 0xff) * fgAlpha;
        int fg_green = (fgColor >> 8 & 0xff) * fgAlpha;
        int fg_blue = (fgColor & 0xff) * fgAlpha;
        int yStart = yCenter - radius;
        if (yStart < 0)
            yStart = 0;
        int yStop = yCenter + radius;
        if (yStop >= gameWindowHeight)
            yStop = gameWindowHeight - 1;
        byte yStep = 1;
        if (lowDef)
        {
            yStep = 2;
            if ((yStart & 1) != 0)
                yStart++;
        }
        for (int y = yStart; y <= yStop; y += yStep)
        {
            int j = y - yCenter;
            int i = (int) (Math.sqrt(radius * radius - j * j));
            int xStart = xCenter - i;
            if (xStart < 0)
                xStart = 0;
            int xStop = xCenter + i;
            if (xStop >= gameWindowWidth)
                xStop = gameWindowWidth - 1;
            int offset = xStart + y * gameWindowWidth;
            for (int x = xStart; x <= xStop; x++)
            {
                int bg_red = (imagePixelArray[offset] >> 16 & 0xff) * bg_alpha;
                int bg_green = (imagePixelArray[offset] >> 8 & 0xff) * bg_alpha;
                int bg_blue = (imagePixelArray[offset] & 0xff) * bg_alpha;
                int pixelVal = ((fg_red + bg_red & 0xff00) << 8)
                		+ (fg_green + bg_green & 0xff00)
                		+ (fg_blue + bg_blue >> 8);
                imagePixelArray[offset++] = pixelVal;
            }

        }

    }

    public void drawBoxAlpha(int x, int y, int width, int height,
    		int colour, int alpha)
    {
        if (x < bounds.x) {
            width -= bounds.x - x;
            x = bounds.x;
        }
        if (y < bounds.y) {
            height -= bounds.y - y;
            y = bounds.y;
        }
        if (x + width > bounds.width)
            width = bounds.width - x;
        if (y + height > bounds.height)
            height = bounds.height - y;
        int bgAlpha = 256 - alpha;
        int red_a_mult = (colour >> 16 & 0xff) * alpha;
        int green_a_mult = (colour >> 8 & 0xff) * alpha;
        int blue_a_mult = (colour & 0xff) * alpha;
        int skip = gameWindowWidth - width;
        byte yStep = 1;
        if (lowDef) {
            yStep = 2;
            skip += gameWindowWidth;
            if ((y & 1) != 0) {
                y++;
                height--;
            }
        }
        int offset = x + y * gameWindowWidth;
        for (int j = 0; j < height; j += yStep)
        {
            for (int i = -width; i < 0; i++)
            {
                int red = (imagePixelArray[offset] >> 16 & 0xff) * bgAlpha;
                int green = (imagePixelArray[offset] >> 8 & 0xff) * bgAlpha;
                int blue = (imagePixelArray[offset] & 0xff) * bgAlpha;
                int pixelVal = ((red_a_mult + red >> 8) << 16) + ((green_a_mult + green >> 8) << 8) + (blue_a_mult + blue >> 8);
                imagePixelArray[offset++] = pixelVal;
            }

            offset += skip;
        }

    }

    public void drawGradientBox(int x, int y, int width, int height,
    		int colorTop, int colorBottom)
    {
        if (x < bounds.x) {
            width -= bounds.x - x;
            x = bounds.x;
        }
        if (x + width > bounds.width)
            width = bounds.width - x;
        int clrBtmRed = colorBottom >> 16 & 0xff;
        int clrBtmGre = colorBottom >> 8 & 0xff;
        int clrBtmBlu = colorBottom & 0xff;
        int clrTopRed = colorTop >> 16 & 0xff;
        int clrTopGre = colorTop >> 8 & 0xff;
        int clrTopBlu = colorTop & 0xff;
        int skipNLastPixels = gameWindowWidth - width;
        byte yStep = 1;
        if (lowDef)
        {
            yStep = 2;
            skipNLastPixels += gameWindowWidth;
            if ((y & 1) != 0) {
                y++;
                height--;
            }
        }
        int offset = x + y * gameWindowWidth;
        for (int j = 0; j < height; j += yStep)
            if (j + y >= bounds.y && j + y < bounds.height) 
            {
                int pixelClr = ((clrBtmRed * j + clrTopRed * (height - j)) / height << 16) + ((clrBtmGre * j + clrTopGre * (height - j)) / height << 8) + (clrBtmBlu * j + clrTopBlu * (height - j)) / height;
                for (int i = -width; i < 0; i++)
                    imagePixelArray[offset++] = pixelClr;
                offset += skipNLastPixels;
            } else {
                offset += gameWindowWidth;
            }
    }

    public void drawBox(int x, int y, int width, int height, int colour)
    {
        if (x < bounds.x)
        {
            width -= bounds.x - x;
            x = bounds.x;
        }
        if (y < bounds.y)
        {
            height -= bounds.y - y;
            y = bounds.y;
        }
        if (x + width > bounds.width)
            width = bounds.width - x;
        if (y + height > bounds.height)
            height = bounds.height - y;

        int skip = gameWindowWidth - width;
        byte yStep = 1;
        if (lowDef) {
            yStep = 2;
            skip += gameWindowWidth;
            if ((y & 1) != 0) {
                y++;
                height--;
            }
        }
        int offset = x + y * gameWindowWidth;
        for (int j = -height; j < 0; j += yStep)
        {
            for (int i = -width; i < 0; i++)
                imagePixelArray[offset++] = colour;
            offset += skip;
        }

    }

    public void drawBoxEdge(int x1, int y1, int x2, int y2, int colour)
    {
        drawLineX(x1, y1, x2, colour);
        drawLineX(x1, (y1 + y2) - 1, x2, colour);
        drawLineY(x1, y1, y2, colour);
        drawLineY((x1 + x2) - 1, y1, y2, colour);
    }

    public void drawLineX(int x1, int y1, int x2, int colour)
    {
        if (y1 < bounds.y || y1 >= bounds.height)
            return;
        if (x1 < bounds.x) {
            x2 -= bounds.x - x1;
            x1 = bounds.x;
        }
        if (x1 + x2 > bounds.width)
            x2 = bounds.width - x1;
        int xPixel = x1 + y1 * gameWindowWidth;
        for (int yPixel = 0; yPixel < x2; yPixel++)
            imagePixelArray[xPixel + yPixel] = colour;

    }

    public void drawLineY(int x1, int y1, int y2, int colour)
    {
        if (x1 < bounds.x || x1 >= bounds.width)
            return;
        if (y1 < bounds.y) {
            y2 -= bounds.y - y1;
            y1 = bounds.y;
        }
        if (y1 + y2 > bounds.width)
            y2 = bounds.height - y1;
        int xPixel = x1 + y1 * gameWindowWidth;
        for (int yPixel = 0; yPixel < y2; yPixel++)
            imagePixelArray[xPixel + yPixel * gameWindowWidth] = colour;

    }

    public void setMinimapPixel(int x, int y, int colour)
    {
        if (x < bounds.x || y < bounds.y
        		|| x >= bounds.width || y >= bounds.height) {
            return;
        }
        imagePixelArray[x + y * gameWindowWidth] = colour;
    }

    public void fadePixels() {
        int k = gameWindowWidth * gameWindowHeight;
        for (int j = 0; j < k; j++) {
            int i = imagePixelArray[j] & 0xffffff;
            imagePixelArray[j] = (i >>> 1 & 0x7f7f7f)
            		+ (i >>> 2 & 0x3f3f3f)
            		+ (i >>> 3 & 0x1f1f1f)
            		+ (i >>> 4 & 0xf0f0f);
        }
    }

    public void method221(int i, int j, int k, int l, int i1, int j1)
    {
        for (int k1 = k; k1 < k + i1; k1++) {
            for (int l1 = l; l1 < l + j1; l1++) {
                int i2 = 0;
                int j2 = 0;
                int k2 = 0;
                int l2 = 0;
                for (int i3 = k1 - i; i3 <= k1 + i; i3++)
                    if (i3 >= 0 && i3 < gameWindowWidth) {
                        for (int j3 = l1 - j; j3 <= l1 + j; j3++)
                            if (j3 >= 0 && j3 < gameWindowHeight) {
                                int k3 = imagePixelArray[i3 + gameWindowWidth * j3];
                                i2 += k3 >> 16 & 0xff;
                                j2 += k3 >> 8 & 0xff;
                                k2 += k3 & 0xff;
                                l2++;
                            }

                    }

                imagePixelArray[k1 + gameWindowWidth * l1] = (i2 / l2 << 16) + (j2 / l2 << 8) + k2 / l2;
            }

        }

    }

    public void cleanupSprites()
    {
        for (int i = 0; i < sprites.length; i++)
            sprites[i] = null;
    }

    public void storeSpriteHoriz(int index, int startX, int startY,
    		int width, int height)
    {
        int[] pixels = new int[width * height];
        int pixel = 0;
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                pixels[pixel++] = imagePixelArray[x + y * gameWindowWidth];
            }
        }

        Sprite sprite = new Sprite(pixels, width, height);
        sprite.setShift(0, 0);
        sprite.setRequiresShift(false);
        sprite.setTotalSize(width, height);

        sprites[index] = sprite;
    }

    public void storeSpriteVert(int index, int startX, int startY,
    		int width, int height)
    {
        int[] pixels = new int[width * height];
        int pixel = 0;
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                pixels[pixel++] = imagePixelArray[x + y * gameWindowWidth];
            }
        }

        Sprite sprite = new Sprite(pixels, width, height);
        sprite.setShift(0, 0);
        sprite.setRequiresShift(false);
        sprite.setTotalSize(width, height);

        sprites[index] = sprite;
    }

    public void drawPicture(int x, int y, int picture)
    {
        try {
            if (sprites[picture].requiresShift()) {
                x += sprites[picture].getXShift();
                y += sprites[picture].getYShift();
            }
            int l = x + y * gameWindowWidth;
            int i1 = 0;
            int j1 = sprites[picture].getHeight();
            int k1 = sprites[picture].getWidth();
            int l1 = gameWindowWidth - k1;
            int i2 = 0;
            if (y < bounds.y) {
                int j2 = bounds.y - y;
                j1 -= j2;
                y = bounds.y;
                i1 += j2 * k1;
                l += j2 * gameWindowWidth;
            }
            if (y + j1 >= bounds.height)
                j1 -= ((y + j1) - bounds.height) + 1;
            if (x < bounds.x) {
                int k2 = bounds.x - x;
                k1 -= k2;
                x = bounds.x;
                i1 += k2;
                l += k2;
                i2 += k2;
                l1 += k2;
            }
            if (x + k1 >= bounds.width) {
                int l2 = ((x + k1) - bounds.width) + 1;
                k1 -= l2;
                i2 += l2;
                l1 += l2;
            }
            if (k1 <= 0 || j1 <= 0)
                return;
            byte byte0 = 1;
            if (lowDef) {
                byte0 = 2;
                l1 += gameWindowWidth;
                i2 += sprites[picture].getWidth();
                if ((y & 1) != 0) {
                    l += gameWindowWidth;
                    j1--;
                }
            }
            drawImage(imagePixelArray, sprites[picture].getPixels(), i1, l, k1, j1, l1, i2, byte0);
        }
        catch (Exception e) {
            System.err.println("Error drawing: " + picture);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void spriteClip1(int startX, int startY, int newWidth,
    		int newHeight, int spriteId)
    {
        try {
            int spriteWidthInit = sprites[spriteId].getWidth();
            int spriteHeightInit = sprites[spriteId].getHeight();
            int l1 = 0;
            int i2 = 0;
            int j2 = (spriteWidthInit << 16) / newWidth; // instead of using decimals i guess
            int k2 = (spriteHeightInit << 16) / newHeight; // instead of using decimals i guess
            if (sprites[spriteId].requiresShift())
            {
                int l2 = sprites[spriteId].getTotalWidth();
                int j3 = sprites[spriteId].getTotalHeight();
                j2 = (l2 << 16) / newWidth;
                k2 = (j3 << 16) / newHeight;
                startX += ((sprites[spriteId].getXShift() * newWidth + l2) - 1) / l2;
                startY += ((sprites[spriteId].getYShift() * newHeight + j3) - 1) / j3;
                if ((sprites[spriteId].getXShift() * newWidth) % l2 != 0)
                    l1 = (l2 - (sprites[spriteId].getXShift() * newWidth) % l2 << 16) / newWidth;
                if ((sprites[spriteId].getYShift() * newHeight) % j3 != 0)
                    i2 = (j3 - (sprites[spriteId].getYShift() * newHeight) % j3 << 16) / newHeight;
                newWidth = (newWidth * (sprites[spriteId].getWidth() - (l1 >> 16))) / l2;
                newHeight = (newHeight * (sprites[spriteId].getHeight() - (i2 >> 16))) / j3;
            }
            int i3 = startX + startY * gameWindowWidth;
            int k3 = gameWindowWidth - newWidth;
            if (startY < bounds.y) {
                int l3 = bounds.y - startY;
                newHeight -= l3;
                startY = 0;
                i3 += l3 * gameWindowWidth;
                i2 += k2 * l3;
            }
            if (startY + newHeight >= bounds.height)
                newHeight -= ((startY + newHeight) - bounds.height) + 1;
            if (startX < bounds.x) {
                int i4 = bounds.x - startX;
                newWidth -= i4;
                startX = 0;
                i3 += i4;
                l1 += j2 * i4;
                k3 += i4;
            }
            if (startX + newWidth >= bounds.width) {
                int j4 = ((startX + newWidth) - bounds.width) + 1;
                newWidth -= j4;
                k3 += j4;
            }
            byte byte0 = 1;
            if (lowDef) {
                byte0 = 2;
                k3 += gameWindowWidth;
                k2 += k2;
                if ((startY & 1) != 0) {
                    i3 += gameWindowWidth;
                    newHeight--;
                }
            }
            drawImageScale(imagePixelArray, sprites[spriteId].getPixels(), l1, i2, i3, k3, newWidth, newHeight, j2, k2, spriteWidthInit, byte0);
            return;
        }
        catch (Exception _ex) {
            System.out.println("spriteClip1: error in sprite clipping routine");
        }
    }

    public void method232(int i, int j, int spriteId, int spriteAlpha)
    {
        if (sprites[spriteId].requiresShift()) {
            i += sprites[spriteId].getXShift();
            j += sprites[spriteId].getYShift();
        }
        int imageStart = i + j * gameWindowWidth;
        int spriteStart = 0;
        int spriteHeight = sprites[spriteId].getHeight();
        int spriteWidth = sprites[spriteId].getWidth();
        int imageYStep = gameWindowWidth - spriteWidth;
        int spriteYStep = 0;
        if (j < bounds.y) {
            int k2 = bounds.y - j;
            spriteHeight -= k2;
            j = bounds.y;
            spriteStart += k2 * spriteWidth;
            imageStart += k2 * gameWindowWidth;
        }
        if (j + spriteHeight >= bounds.height)
            spriteHeight -= ((j + spriteHeight) - bounds.height) + 1;
        if (i < bounds.x) {
            int l2 = bounds.x - i;
            spriteWidth -= l2;
            i = bounds.x;
            spriteStart += l2;
            imageStart += l2;
            spriteYStep += l2;
            imageYStep += l2;
        }
        if (i + spriteWidth >= bounds.width) {
            int i3 = ((i + spriteWidth) - bounds.width) + 1;
            spriteWidth -= i3;
            spriteYStep += i3;
            imageYStep += i3;
        }
        if (spriteWidth <= 0 || spriteHeight <= 0)
            return;
        byte yStep = 1;
        if (lowDef) {
            yStep = 2;
            imageYStep += gameWindowWidth;
            spriteYStep += sprites[spriteId].getWidth();
            if ((j & 1) != 0) {
                imageStart += gameWindowWidth;
                spriteHeight--;
            }
        }
        drawImageTransparent(imagePixelArray, sprites[spriteId].getPixels(),
        		spriteStart, imageStart, spriteWidth, spriteHeight,
        		imageYStep, spriteYStep, yStep, spriteAlpha);
    }

    /**
     * 
     * @param startX
     * @param startY
     * @param newWidth
     * @param newHeight
     * @param spriteId
     * @param j1
     */
    public void spriteClip2(int startX, int startY, int newWidth,
    		int newHeight, int spriteId, int j1)
    {
        try
        {
            int spriteWidthInit = sprites[spriteId].getWidth();
            int spriteHeightInit = sprites[spriteId].getHeight();
            int i2 = 0;
            int j2 = 0;
            int k2 = (spriteWidthInit << 16) / newWidth;
            int l2 = (spriteHeightInit << 16) / newHeight;
            if (sprites[spriteId].requiresShift()) {
                int i3 = sprites[spriteId].getTotalWidth();
                int k3 = sprites[spriteId].getTotalHeight();
                k2 = (i3 << 16) / newWidth;
                l2 = (k3 << 16) / newHeight;
                startX += ((sprites[spriteId].getXShift() * newWidth + i3) - 1) / i3;
                startY += ((sprites[spriteId].getYShift() * newHeight + k3) - 1) / k3;
                if ((sprites[spriteId].getXShift() * newWidth) % i3 != 0)
                    i2 = (i3 - (sprites[spriteId].getXShift() * newWidth) % i3 << 16) / newWidth;
                if ((sprites[spriteId].getYShift() * newHeight) % k3 != 0)
                    j2 = (k3 - (sprites[spriteId].getYShift() * newHeight) % k3 << 16) / newHeight;
                newWidth = (newWidth * (sprites[spriteId].getWidth() - (i2 >> 16))) / i3;
                newHeight = (newHeight * (sprites[spriteId].getHeight() - (j2 >> 16))) / k3;
            }
            int j3 = startX + startY * gameWindowWidth;
            int l3 = gameWindowWidth - newWidth;
            if (startY < bounds.y) {
                int i4 = bounds.y - startY;
                newHeight -= i4;
                startY = 0;
                j3 += i4 * gameWindowWidth;
                j2 += l2 * i4;
            }
            if (startY + newHeight >= bounds.height)
                newHeight -= ((startY + newHeight) - bounds.height) + 1;
            if (startX < bounds.x) {
                int j4 = bounds.x - startX;
                newWidth -= j4;
                startX = 0;
                j3 += j4;
                i2 += k2 * j4;
                l3 += j4;
            }
            if (startX + newWidth >= bounds.width) {
                int k4 = ((startX + newWidth) - bounds.width) + 1;
                newWidth -= k4;
                l3 += k4;
            }
            byte byte0 = 1;
            if (lowDef) {
                byte0 = 2;
                l3 += gameWindowWidth;
                l2 += l2;
                if ((startY & 1) != 0) {
                    j3 += gameWindowWidth;
                    newHeight--;
                }
            }
            drawImageTransparentScale(imagePixelArray, sprites[spriteId].getPixels(), i2, j2, j3, l3, newWidth, newHeight, k2, l2, spriteWidthInit, byte0, j1);
            return;
        }
        catch (Exception _ex) {
            System.out.println("spriteClip2: error in sprite clipping routine");
        }
    }

    /**
     * 
     * @param startX
     * @param startY
     * @param newWidth
     * @param newHeight
     * @param spriteId
     * @param spriteColor
     */
    public void spriteClip3(int startX, int startY, int newWidth,
    		int newHeight, int spriteId, int spriteColor)
    {
        try {
            int spriteWidthInit = sprites[spriteId].getWidth();
            int spriteHeightInit = sprites[spriteId].getHeight();
            int i2 = 0;
            int j2 = 0;
            int k2 = (spriteWidthInit << 16) / newWidth;
            int l2 = (spriteHeightInit << 16) / newHeight;
            if (sprites[spriteId].requiresShift()) {
                int i3 = sprites[spriteId].getTotalWidth();
                int k3 = sprites[spriteId].getTotalHeight();
                k2 = (i3 << 16) / newWidth;
                l2 = (k3 << 16) / newHeight;
                startX += ((sprites[spriteId].getXShift() * newWidth + i3) - 1) / i3;
                startY += ((sprites[spriteId].getYShift() * newHeight + k3) - 1) / k3;
                if ((sprites[spriteId].getXShift() * newWidth) % i3 != 0)
                    i2 = (i3 - (sprites[spriteId].getXShift() * newWidth) % i3 << 16) / newWidth;
                if ((sprites[spriteId].getYShift() * newHeight) % k3 != 0)
                    j2 = (k3 - (sprites[spriteId].getYShift() * newHeight) % k3 << 16) / newHeight;
                newWidth = (newWidth * (sprites[spriteId].getWidth() - (i2 >> 16))) / i3;
                newHeight = (newHeight * (sprites[spriteId].getHeight() - (j2 >> 16))) / k3;
            }
            int j3 = startX + startY * gameWindowWidth;
            int l3 = gameWindowWidth - newWidth;
            if (startY < bounds.y) {
                int i4 = bounds.y - startY;
                newHeight -= i4;
                startY = 0;
                j3 += i4 * gameWindowWidth;
                j2 += l2 * i4;
            }
            if (startY + newHeight >= bounds.height)
                newHeight -= ((startY + newHeight) - bounds.height) + 1;
            if (startX < bounds.x) {
                int j4 = bounds.x - startX;
                newWidth -= j4;
                startX = 0;
                j3 += j4;
                i2 += k2 * j4;
                l3 += j4;
            }
            if (startX + newWidth >= bounds.width) {
                int k4 = ((startX + newWidth) - bounds.width) + 1;
                newWidth -= k4;
                l3 += k4;
            }
            byte byte0 = 1;
            if (lowDef) {
                byte0 = 2;
                l3 += gameWindowWidth;
                l2 += l2;
                if ((startY & 1) != 0) {
                    j3 += gameWindowWidth;
                    newHeight--;
                }
            }
            drawImageOverlay(imagePixelArray, sprites[spriteId].getPixels(), i2, j2, j3, l3, newWidth, newHeight, k2, l2, spriteWidthInit, byte0, spriteColor);
            return;
        }
        catch (Exception _ex) {
            System.out.println("spriteClip3: error in sprite clipping routine");
        }
    }
    
    /**
     * Plots an image to the gamewindow.
     * @param imageRaw
     * @param width
     * @param height
     * @param resize
     */
    public void imageToPixArray(BufferedImage imageRaw, int imgXPos,
    		int imgYPos, int width, int height, boolean resize)
    {
    	final int unScaledWidth = imageRaw.getWidth();
    	final int unScaledHeight = imageRaw.getHeight();
    	BufferedImage image;
		if (resize && unScaledWidth != width && unScaledHeight != height)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			AffineTransform at = new AffineTransform();
			at.scale(((double)(width))/unScaledWidth, ((double)(height))/unScaledHeight);
			AffineTransformOp scaleOp = 
			   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			image = scaleOp.filter(imageRaw, image);
		}
		else
		{
			image = imageRaw;
		}
		final int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    	drawImage(imagePixelArray, pixels, 0, imgYPos*bounds.width+imgXPos,
    			image.getWidth(), image.getHeight(),
    			bounds.width-image.getWidth(), 0, 1);
    }

    public void drawMinimapTiles(int xCorner, int yCorner,
    		int sprite, int phi, int theta)
    {
        int windowWidth = gameWindowWidth;
        int windowHeight = gameWindowHeight;
        int x0 = -sprites[sprite].getTotalWidth() / 2 + sprites[sprite].getXShift();
        int x1 = x0 + sprites[sprite].getWidth();
        int y0 = -sprites[sprite].getTotalHeight() / 2 + sprites[sprite].getYShift();
        int y1 = y0 + sprites[sprite].getHeight();
        double[] p_x = { x0, x0, x1, x1 };
        double[] p_y = { y0, y1, y1, y0 };
        phi &= 0x3ff;
        double sin = Trig.sin1024[phi] * theta;
        double cos = Trig.cos1024[phi] * theta;
        double fctr = 1D/128D;
        double[] p_x_rot = {
        		xCorner + (p_y[0] * sin + p_x[0] * cos) * fctr,
        		xCorner + (p_y[1] * sin + p_x[1] * cos) * fctr,
        		xCorner + (p_y[2] * sin + p_x[2] * cos) * fctr,
        		xCorner + (p_y[3] * sin + p_x[3] * cos) * fctr
        };
        double[] p_y_rot = {
        		yCorner + (p_y[0] * cos - p_x[0] * sin) * fctr,
        		yCorner + (p_y[1] * cos - p_x[1] * sin) * fctr,
        		yCorner + (p_y[2] * cos - p_x[2] * sin) * fctr,
        		yCorner + (p_y[3] * cos - p_x[3] * sin) * fctr
        };
        if (theta == 192 && (phi & 0xff) == (anInt348 & 0xff))
            anInt346++;
        else if (theta == 128)
            anInt348 = phi;
        else
            anInt347++;
        double ymn = p_y_rot[0];
        double ymx = p_y_rot[0];
        for (double j : p_y_rot)
        	if (j < ymn)
        		ymn = j;
        	else if (j > ymx)
        		ymx = j;
        int ymin = (int)ymn;
        int ymax = (int)ymx;
        if (ymin < bounds.y)
            ymin = bounds.y;
        if (ymax > bounds.height)
            ymax = bounds.height;
        double[] x_start = new double[windowHeight + 1];
        double[] x_end = new double[windowHeight + 1];
        double[] x_start_sprite = new double[windowHeight + 1];
        double[] x_end_sprite = new double[windowHeight + 1];
        double[] y_start_sprite = new double[windowHeight + 1];
        double[] y_end_sprite = new double[windowHeight + 1];
        for (int i = ymin; i <= ymax;
        		x_start[i++] = 99999999D);
        for (int i = ymin; i <= ymax;
        		x_end[i++] = -99999999D);

        double slope_1 = 0;
        double slope_3 = 0;
        double slope_2 = 0;
        int spriteWidth = sprites[sprite].getWidth();
        int spriteHeight = sprites[sprite].getHeight();
        p_x[0] = 0;
        p_x[1] = 0;
        p_x[2] = spriteWidth - 1;
        p_x[3] = spriteWidth - 1;
        p_y[0] = 0;
        p_y[1] = spriteHeight - 1;
        p_y[2] = spriteHeight - 1;
        p_y[3] = 0;

        double min_y_rot;
        double max_y_rot;
        double x_bnd_img;
        double y_bnd_sprite;
        double x_bnd_sprite;

        if (Math.abs(p_y_rot[1] - p_y_rot[0]) > 1)
        {
            slope_1 = (p_x_rot[1] - p_x_rot[0]) / (p_y_rot[1] - p_y_rot[0]);
            slope_2 = (p_y[1] - p_y[0]) / (p_y_rot[1]- p_y_rot[0]);
        }
        if (p_y_rot[1] > p_y_rot[0]) {
            x_bnd_img = p_x_rot[0];
            y_bnd_sprite = p_y[0];
            min_y_rot = p_y_rot[0];
            max_y_rot = p_y_rot[1];
        } else {
            x_bnd_img = p_x_rot[1];
            y_bnd_sprite = p_y[1];
            min_y_rot = p_y_rot[1];
            max_y_rot = p_y_rot[0];
        }
        if (min_y_rot < 0) {
            x_bnd_img -= slope_1 * min_y_rot;
            y_bnd_sprite -= slope_2 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int i = (int) min_y_rot; i <= max_y_rot; i++)
        {
            x_start[i] = x_end[i] = x_bnd_img;
            x_bnd_img += slope_1;
            x_start_sprite[i] = x_end_sprite[i] = 0;
            y_start_sprite[i] = y_end_sprite[i] = y_bnd_sprite;
            y_bnd_sprite += slope_2;
        }

        if (Math.abs(p_y_rot[2] - p_y_rot[1]) > 1) {
            slope_1 = (p_x_rot[2] - p_x_rot[1]) / (p_y_rot[2] - p_y_rot[1]);
            slope_3 = (p_x[2] - p_x[1]) / (p_y_rot[2] - p_y_rot[1]);
        }
        if (p_y_rot[2] > p_y_rot[1]) {
            x_bnd_img = p_x_rot[1];
            x_bnd_sprite = p_x[1];
            y_bnd_sprite = p_y[1];
            min_y_rot = p_y_rot[1];
            max_y_rot = p_y_rot[2];
        } else {
            x_bnd_img = p_x_rot[2];
            x_bnd_sprite = p_x[2];
            y_bnd_sprite = p_y[2];
            min_y_rot = p_y_rot[2];
            max_y_rot = p_y_rot[1];
        }
        if (min_y_rot < 0) {
            x_bnd_img -= slope_1 * min_y_rot;
            x_bnd_sprite -= slope_3 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int i = (int) min_y_rot; i <= max_y_rot; i++) {
            if (x_bnd_img < x_start[i]) {
                x_start[i] = x_bnd_img;
                x_start_sprite[i] = x_bnd_sprite;
                y_start_sprite[i] = y_bnd_sprite;
            }
            if (x_bnd_img > x_end[i]) {
                x_end[i] = x_bnd_img;
                x_end_sprite[i] = x_bnd_sprite;
                y_end_sprite[i] = y_bnd_sprite;
            }
            x_bnd_img += slope_1;
            x_bnd_sprite += slope_3;
        }

        if (Math.abs(p_y_rot[3] - p_y_rot[2]) > 1)
        {
            slope_1 = (p_x_rot[3] - p_x_rot[2]) / (p_y_rot[3] - p_y_rot[2]);
            slope_2 = (p_y[3] - p_y[2]) / (p_y_rot[3] - p_y_rot[2]);
        }
        if (p_y_rot[3] > p_y_rot[2]) {
            x_bnd_img = p_x_rot[2];
            x_bnd_sprite = p_x[2];
            y_bnd_sprite = p_y[2];
            min_y_rot = p_y_rot[2];
            max_y_rot = p_y_rot[3];
        } else {
            x_bnd_img = p_x_rot[3];
            x_bnd_sprite = p_x[3];
            y_bnd_sprite = p_y[3];
            min_y_rot = p_y_rot[3];
            max_y_rot = p_y_rot[2];
        }
        if (min_y_rot < 0) {
            x_bnd_img -= slope_1 * min_y_rot;
            y_bnd_sprite -= slope_2 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int i = (int) min_y_rot; i <= max_y_rot; i++) {
            if (x_bnd_img < x_start[i]) {
                x_start[i] = x_bnd_img;
                x_start_sprite[i] = x_bnd_sprite;
                y_start_sprite[i] = y_bnd_sprite;
            }
            if (x_bnd_img > x_end[i]) {
                x_end[i] = x_bnd_img;
                x_end_sprite[i] = x_bnd_sprite;
                y_end_sprite[i] = y_bnd_sprite;
            }
            x_bnd_img += slope_1;
            y_bnd_sprite += slope_2;
        }

        if (Math.abs(p_y_rot[0] - p_y_rot[3]) > 1) {
            slope_1 = (p_x_rot[0] - p_x_rot[3]) / (p_y_rot[0] - p_y_rot[3]);
            slope_3 = (p_x[0] - p_x[3]) / (p_y_rot[0] - p_y_rot[3]);
        }
        if (p_y_rot[0] > p_y_rot[3]) {
            x_bnd_img = p_x_rot[3];
            x_bnd_sprite = p_x[3];
            min_y_rot = p_y_rot[3];
            max_y_rot = p_y_rot[0];
        } else {
            x_bnd_img = p_x_rot[0];
            x_bnd_sprite = p_x[0];
            min_y_rot = p_y_rot[0];
            max_y_rot = p_y_rot[3];
        }
        if (min_y_rot < 0) {
            x_bnd_img -= slope_1 * min_y_rot;
            x_bnd_sprite -= slope_3 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int i = (int) min_y_rot; i <= max_y_rot; i++)
        {
            if (x_bnd_img < x_start[i]) {
                x_start[i] = x_bnd_img;
                x_start_sprite[i] = x_bnd_sprite;
                y_start_sprite[i] = 0;
            }
            if (x_bnd_img > x_end[i]) {
                x_end[i] = x_bnd_img;
                x_end_sprite[i] = x_bnd_sprite;
                y_end_sprite[i] = 0;
            }
            x_bnd_img += slope_1;
            x_bnd_sprite += slope_3;
        }

        int offset = ymin * windowWidth;
        int mapPixels[] = sprites[sprite].getPixels();
        for (int y = ymin; y < ymax; y++)
        {
        	double xStart = x_start[y];
        	double xEnd = x_end[y];
            if (xEnd - xStart <= 0) {
                offset += windowWidth;
            } else {
            	double xDrawStart = x_start_sprite[y];
            	double xStep = ((x_end_sprite[y]) - xDrawStart) / (xEnd - xStart);
            	double yDrawstart = y_start_sprite[y];
            	double yStep = ((y_end_sprite[y]) - yDrawstart) / (xEnd - xStart);
                if (xStart < bounds.x)
                {
                    xDrawStart += (bounds.x - xStart) * xStep;
                    yDrawstart += (bounds.x - xStart) * yStep;
                    xStart = bounds.x;
                }
                if (xEnd > bounds.width)
                    xEnd = bounds.width;
                if (!lowDef || (y & 1) == 0)
                    if (!sprites[sprite].requiresShift())
                        drawMapSpriteWOShift(imagePixelArray, mapPixels,
                        		(int)(offset + xStart),
                        		xDrawStart, yDrawstart,
                        		xStep, yStep,
                        		(int)(xStart - xEnd),
                        		spriteWidth);
                    else
                        drawMapSpriteWShift(imagePixelArray, mapPixels,
                        		(int)(offset + xStart),
                        		xDrawStart, yDrawstart,
                        		xStep, yStep,
                        		(int)(xStart - xEnd),
                        		spriteWidth);
                offset += windowWidth;
            }
        }

    }

    public void doSpriteClip1(int startX, int startY, int newWidth,
    		int newHeight, int spriteId, int i, int j)
    {
        spriteClip1(startX, startY, newWidth, newHeight, spriteId);
    }

    /**
     * Looks like this has to do with animations.
     * @param startX
     * @param startY
     * @param scaledWidth
     * @param scaledHeight
     * @param spriteId
     * @param hairColor
     * @param skinColor
     * @param l1
     * @param flip
     */
    public void spriteClip4(int startX, int startY, int scaledWidth,
    		int scaledHeight, int spriteId, int hairColor, int skinColor,
    		int l1, boolean flip)
    {
        try
        {
        	Sprite sprite = sprites[spriteId];
            if (hairColor == 0)
                hairColor = 0xffffff;
            if (skinColor == 0)
                skinColor = 0xffffff;
            
            int spriteWidth = sprite.getWidth();
            int spriteHeight = sprite.getHeight();
            int spriteXIdxHD = 0;
            int spriteYIdxHD = 0;
            int i3 = l1 << 16;
            int spriteXStepHD = (spriteWidth << 16) / scaledWidth;
            int spriteYStepHD = (spriteHeight << 16) / scaledHeight;
            int l3 = -(l1 << 16) / scaledHeight;
            if (sprite.requiresShift())
            {
                int totalWidth = sprite.getTotalWidth();
                int totalHeight = sprite.getTotalHeight();
                spriteXStepHD = (totalWidth << 16) / scaledWidth;
                spriteYStepHD = (totalHeight << 16) / scaledHeight;
                int xShift = sprite.getXShift();
                int yShift = sprite.getYShift();
                if (flip)
                    xShift = totalWidth - sprite.getWidth() - xShift;
                startX += ((xShift * scaledWidth + totalWidth) - 1) / totalWidth;
                int l5 = ((yShift * scaledHeight + totalHeight) - 1) / totalHeight;
                startY += l5;
                i3 += l5 * l3;
                if ((xShift * scaledWidth) % totalWidth != 0)
                    spriteXIdxHD = (totalWidth - (xShift * scaledWidth) % totalWidth << 16) / scaledWidth;
                if ((yShift * scaledHeight) % totalHeight != 0)
                    spriteYIdxHD = (totalHeight - (yShift * scaledHeight) % totalHeight << 16) / scaledHeight;
                scaledWidth = ((((sprite.getWidth() << 16) - spriteXIdxHD) + spriteXStepHD) - 1) / spriteXStepHD;
                scaledHeight = ((((sprite.getHeight() << 16) - spriteYIdxHD) + spriteYStepHD) - 1) / spriteYStepHD;
            }
            int windowYIdx = startY * gameWindowWidth;
            i3 += startX << 16;
            if (startY < bounds.y)
            {
                int l4 = bounds.y - startY;
                scaledHeight -= l4;
                startY = bounds.y;
                windowYIdx += l4 * gameWindowWidth;
                spriteYIdxHD += spriteYStepHD * l4;
                i3 += l3 * l4;
            }
            if (startY + scaledHeight >= bounds.height)
                scaledHeight -= ((startY + scaledHeight) - bounds.height) + 1;
            int yStep = windowYIdx / gameWindowWidth & 1;
            if (!lowDef)
                yStep = 2;
            
            if (skinColor == 0xffffff)
            {
                if (!flip)
                    darwImageOverlayScale(imagePixelArray, sprite.getPixels(),
                    		spriteXIdxHD, spriteYIdxHD,
                    		windowYIdx, scaledWidth, scaledHeight, spriteXStepHD,
                    		spriteYStepHD, spriteWidth, hairColor,
                    		i3, l3, yStep);
                else
                    darwImageOverlayScale(imagePixelArray, sprite.getPixels(),
                    		(spriteWidth << 16) - spriteXIdxHD - 1, spriteYIdxHD,
                    		windowYIdx, scaledWidth, scaledHeight, -spriteXStepHD,
                    		spriteYStepHD, spriteWidth, hairColor,
                    		i3, l3, yStep);
            }
            else
            {
            	if (!flip)
            		drawImageOverlayScale2(imagePixelArray, sprite.getPixels(),
            				spriteXIdxHD, spriteYIdxHD,
            				windowYIdx, scaledWidth, scaledHeight, spriteXStepHD,
            				spriteYStepHD, spriteWidth, hairColor, skinColor,
            				i3, l3, yStep);
            	else
            		drawImageOverlayScale2(imagePixelArray, sprite.getPixels(),
            				(spriteWidth << 16) - spriteXIdxHD - 1, spriteYIdxHD,
            				windowYIdx, scaledWidth, scaledHeight, -spriteXStepHD,
            				spriteYStepHD, spriteWidth, hairColor, skinColor,
            				i3, l3, yStep);
            }
        }
        catch (Exception _ex)
        {
        	_ex.printStackTrace();
        	System.exit(1);
            //System.out.println("spriteClip4: error in sprite clipping routine");
        }
    }

    public void drawBoxTextRight(String s, int i, int j, int k, int l) {
        drawString(s, i - textWidth(s, k), j, k, l);
    }

    public void drawText(String str, int xCenter, int y, int txtType, int color) {
        drawString(str, xCenter - textWidth(str, txtType) / 2, y, txtType, color);
    }

    public void drawBoxTextColour(String s, int i, int j, int k, int l, int i1) {
        try {
            int j1 = 0;
            byte abyte0[] = fontPixels[k];
            int k1 = 0;
            int l1 = 0;
            for (int i2 = 0; i2 < s.length(); i2++) {
                if (s.charAt(i2) == '@' && i2 + 4 < s.length() && s.charAt(i2 + 4) == '@')
                    i2 += 4;
                else if (s.charAt(i2) == '~' && i2 + 4 < s.length() && s.charAt(i2 + 4) == '~')
                    i2 += 4;
                else
                    j1 += abyte0[charIndexes[s.charAt(i2)] + 7];
                if (s.charAt(i2) == ' ')
                    l1 = i2;
                if (s.charAt(i2) == '%') {
                    l1 = i2;
                    j1 = 1000;
                }
                if (j1 > i1) {
                    if (l1 <= k1)
                        l1 = i2;
                    drawText(s.substring(k1, l1), i, j, k, l);
                    j1 = 0;
                    k1 = i2 = l1 + 1;
                    j += messageFontHeight(k);
                }
            }

            if (j1 > 0) {
                drawText(s.substring(k1), i, j, k, l);
                return;
            }
        }
        catch (Exception exception) {
            System.out.println("centrepara: " + exception);
            exception.printStackTrace();
        }
    }

    public void drawString(String string, int x, int y, int k, int colour) {
        try {
            byte abyte0[] = fontPixels[k];
            for (int offset = 0; offset < string.length(); offset++)
            	if ((string.charAt(offset) == '@')
            			&& (offset + 4 < string.length())
            			&& (string.charAt(offset + 4) == '@'))
                {
                	colour = getColor(string.substring(offset, offset + 5), colour);
                    offset += 4;
                }
                else if ((string.charAt(offset) == '~')
                		&& (offset + 4 < string.length())
                		&& (string.charAt(offset + 4) == '~'))
                {
                	try
                	{
                		x = Integer.parseInt(string.substring(offset + 1, offset + 4));
                	}
                	catch (NumberFormatException e) {}
                    offset += 4;
                }
                else if ((string.charAt(offset) == '#')
                		&& (offset + 4 < string.length())
                		&& (string.charAt(offset + 4) == '#'))
                {
                	switch(string.substring(offset + 1, offset + 4))
                	{
                	case "adm":
                		spriteClip4(x - 12, y - 16, 30, 20, 2339, 0xffffff00, 0, 0, false);
                        x += 14;
                        offset += 4;
                		break;
                	case "mod":
                		spriteClip4(x - 12, y - 16, 30, 20, 2339, 0xffdcdcdc, 0, 0, false);
                        x += 14;
                        offset += 4;
                		break;
                	case "pmd":
                        spriteClip4(x - 12, y - 16, 30, 20, 2339, 0xff33cc00, 0, 0, false);
                        x += 14;
                        offset += 4;
                		break;
                	}
                }
                else
                {
                    int charIndex = charIndexes[string.charAt(offset)];
                    if (drawStringShadows && !aBooleanArray349[k] && colour != 0)
                        displayLetter(charIndex, x + 1, y, 0, abyte0, aBooleanArray349[k]);
                    if (drawStringShadows && !aBooleanArray349[k] && colour != 0)
                        displayLetter(charIndex, x, y + 1, 0, abyte0, aBooleanArray349[k]);
                    displayLetter(charIndex, x, y, colour, abyte0, aBooleanArray349[k]);
                    x += abyte0[charIndex + 7];
                }

            return;
        }
        catch (Exception exception) {
            System.out.println("drawstring: " + exception);
            exception.printStackTrace();
            return;
        }
    }

    public int messageFontHeight(int messageType)
    {    	 
        if (messageType == 0)
            return 14;
        if (messageType == 1)
            return 16;
        if (messageType == 2)
            return 16;
        if (messageType == 3)
            return 17;
        if (messageType == 4)
            return 17;
        if (messageType == 5)
            return 21;
        if (messageType == 6)
            return 24;
        if (messageType == 7)
            return 29;
        else
            return method261(messageType);
    }

    public int method261(int i) {
        if (i == 0) {
            return fontPixels[i][8] - 2;
        } else {
            return fontPixels[i][8] - 1;
        }
    }

    public int textWidth(String s, int i) {
        int j = 0;
        byte abyte0[] = fontPixels[i];
        for (int k = 0; k < s.length(); k++) {
            if (s.charAt(k) == '@' && k + 2 < s.length() && s.charAt(k + 2) == '@') {
                k += 2; //2 used to be 4
            } else if (s.charAt(k) == '~' && k + 4 < s.length() && s.charAt(k + 4) == '~') {
                k += 4;
            } else {
                j += abyte0[charIndexes[s.charAt(k)] + 7];
            }
        }
        return j;
    }
    
    private static byte fontPixels[][] = new byte[50][];
    private static int charIndexes[];
    private static boolean aBooleanArray349[] = new boolean[12];
    private static int anInt350;
    private static byte fontPixelsLoadBuffer[] = new byte[0x186a0];
    private ColorModel colourModel;
    private ImageConsumer imageConsumer;
    private Rectangle bounds;
    private ZipFile entityArchive, mediaArchive, utilArchive,
    itemArchive, logoArchive, projectileArchive, textureArchive;
    private static final Map<String, Integer> textColors;

    static {
        textColors = new HashMap<String, Integer>();
        textColors.put("@red@", 0xff0000);
        textColors.put("@lre@", 0xff9040);
        textColors.put("@yel@", 0xffff00);
        textColors.put("@gre@", 0x00ff00);
        textColors.put("@blu@", 0x0000ff);
        textColors.put("@cya@", 0x00ffff);
        textColors.put("@mag@", 0xff00ff);
        textColors.put("@whi@", 0xffffff);
        textColors.put("@bla@", 0x000000);
        textColors.put("@dre@", 0xc00000);
        textColors.put("@ora@", 0xff9040);
        textColors.put("@or1@", 0xffb000);
        textColors.put("@or2@", 0xff7000);
        textColors.put("@or3@", 0xff3000);
        textColors.put("@gr1@", 0xc0ff00);
        textColors.put("@gr2@", 0x80ff00);
        textColors.put("@gr3@", 0x40ff00);
        
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
        charIndexes = new int[256];
        for (int i = 0; i < 256; i++) {
            int j = s.indexOf(i);
            if (j == -1) {
                j = 74;
            }
            charIndexes[i] = j * 9;
        }
    }
	
	private static int getColor(String command, int defaultColor)
	{
		if (textColors.containsKey(command))
			return textColors.get(command);
		else if (command.equalsIgnoreCase("@ran@"))
			return (int) (Math.random() * 0x1000000);
		return defaultColor;
	}

    /**
     * 
     * @param dst Destination pixels
     * @param src Source pixels
     * @param srcStart Source array start index
     * @param dstStart Destination array start index
     * @param srcWidth Source image width
     * @param srcHeight Source image height.
     * @param dstYStep Number of pixels to skip in the destination array
     * 		after each row.
     * @param srcYStep Number of pixels to skip in the source array
     * 		after each row.
     * @param yStep Number of steps (expressed in rows) in the source
     * 		(and destination) array. High-detail should use 1, low detail
     * 		should use &gt;1.
     */
    private void drawImage(int dst[], int src[], int srcStart,
    		int dstStart, int srcWidth, int srcHeight, int dstYStep,
    		int srcYStep, int yStep)
    {
    	int imagePixVal;
        for (int y = -srcHeight; y < 0; y += yStep)
        {
            for (int l2 = -srcWidth; l2 < 0; l2++)
            {
            	imagePixVal = src[srcStart];
                if (imagePixVal != 0)
                    dst[dstStart] = imagePixVal;
                srcStart++;
                dstStart++;
            }

            dstStart += dstYStep;
            srcStart += srcYStep;
        }
    }

    private void drawImageScale(
    		int dstPixels[], int srcPixels[], int srcXIdxHD, int srcYIdxHD, int dstStart, int dstYStep, int srcScaledWidth,
    		int srcScaledHeight, int srcXStep, int srcYStepHD, int srcWidthOriginal, int yStep)
    {
        try
        {
        	int imagePixVal;
            int srcXStartHD = srcXIdxHD;
            for (int y = -srcScaledHeight; y < 0; y += yStep)
            {
                int srcYIdx = (srcYIdxHD >> 16) * srcWidthOriginal;
                for (int x = -srcScaledWidth; x < 0; x++)
                {
                    int srcXIdx = (srcXIdxHD >> 16);
                	imagePixVal = srcPixels[srcYIdx + srcXIdx];
                    if (imagePixVal != 0)
                        dstPixels[dstStart] = imagePixVal;
                    dstStart++;
                    srcXIdxHD += srcXStep;
                }

                srcYIdxHD += srcYStepHD;
                srcXIdxHD = srcXStartHD;
                dstStart += dstYStep;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in plot_scale");
        }
    }

    private void drawImageTransparent(int dstPixels[], int srcPixels[], int srcStart,
    		int dstStart, int srcWidth, int srcHeight, int dstYStep,
    		int srcYStep, int yStep, int fgAlpha)
    {
    	int srcPixelVal;
        int bgAlpha = 0xff - fgAlpha;
        for (int y = -srcHeight; y < 0; y += yStep)
        {
            for (int x = -srcWidth; x < 0; x++)
            {
            	srcPixelVal = srcPixels[srcStart++];
                if (srcPixelVal != 0)
                {
                    int dstPixelVal = dstPixels[dstStart];
                    dstPixels[dstStart] = ((srcPixelVal & 0xff00ff)*fgAlpha + (dstPixelVal & 0xff00ff)*bgAlpha & 0xff00ff00)
                    		+ ((srcPixelVal & 0xff00)*fgAlpha + (dstPixelVal & 0xff00)*bgAlpha & 0xff0000) >> 8;
                }
                dstStart++;
            }

            dstStart += dstYStep;
            srcStart += srcYStep;
        }

    }

    private void drawImageTransparentScale(
    		int dstPixels[], int srcPixels[], int srcXIdxHD, int srcYIdxHD, int dstStart, int dstYStep, int srcScaledWidth,
    		int srcScaledHeight, int srcXStepHD, int srcYStepHD, int srcWidthOriginal, int k2, int fgAlpha)
    {
    	int srcPixelVal;
        int bgAlpha = 256 - fgAlpha;
        try
        {
            int srcXStartHD = srcXIdxHD;
            for (int y = -srcScaledHeight; y < 0; y += k2)
            {
                int srcYIdx = (srcYIdxHD >> 16) * srcWidthOriginal;
                for (int x = -srcScaledWidth; x < 0; x++)
                {
                    int srcXIdx = (srcXIdxHD >> 16);
                    srcPixelVal = srcPixels[srcYIdx + srcXIdx];
                    if (srcPixelVal != 0) {
                        int dstPixelVal = dstPixels[dstStart];
                        dstPixels[dstStart] = ((srcPixelVal & 0xff00ff)*fgAlpha + (dstPixelVal & 0xff00ff)*bgAlpha & 0xff00ff00)
                        		+ ((srcPixelVal & 0xff00)*fgAlpha + (dstPixelVal & 0xff00)*bgAlpha & 0xff0000) >> 8;
                    }
                    dstStart++;
                    srcXIdxHD += srcXStepHD;
                }

                srcYIdxHD += srcYStepHD;
                srcXIdxHD = srcXStartHD;
                dstStart += dstYStep;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in tran_scale");
        }
    }

	private void drawImageOverlay(int dstPixels[], int srcPixels[], int srcXIdxHD,
			int srcYIdxHD, int dstStart, int dstYStep, int srcScaledWidth,
			int srcScaledHeight, int srcXStepHD, int srcYStepHD,
			int srcWidthOriginal, int yStep, int overlay)
	{
		int srcPixelVal;
		int overlayRed = (overlay >> 16) & 0xff;
		int overlayGreen = (overlay >> 8) & 0xff;
		int overlayBlue = overlay & 0xff;
		try
		{
			int srcXStartHD = srcXIdxHD;
			for (int y = -srcScaledHeight; y < 0; y += yStep)
			{
				int srcYIdx = (srcYIdxHD >> 16) * srcWidthOriginal;
				for (int x = -srcScaledWidth; x < 0; x++)
				{
					int srcXIdx = (srcXIdxHD >> 16);
					srcPixelVal = srcPixels[srcYIdx + srcXIdx];
					if (srcPixelVal != 0)
					{
						int srcRed = (srcPixelVal >> 16) & 0xff;
						int srcGreen = (srcPixelVal >> 8) & 0xff;
						int srcBlue = srcPixelVal & 0xff;
						
						if (srcRed == srcGreen && srcGreen == srcBlue)
						{
							dstPixels[dstStart] =
									((srcRed*overlayRed & 0xff00) << 8)
									+ (srcGreen*overlayGreen & 0xff00)
									+ ((srcBlue*overlayBlue & 0xff00) >> 8);
						}
						else
							dstPixels[dstStart] = srcPixelVal;
					}
					dstStart++;
					srcXIdxHD += srcXStepHD;
				}
				
				srcYIdxHD += srcYStepHD;
				srcXIdxHD = srcXStartHD;
				dstStart += dstYStep;
			}

			return;
		}
		catch (Exception _ex) {
			System.out.println("error in plot_scale");
		}
	}
	
	private void darwImageOverlayScale(int dstPixels[], int srcPixels[],
			int srcXIdxHD, int srcYIdxHD, int gameWindowStartIdx,
			int srcScaledWidth, int spriteScaledHeight,
			int srcXStepHD, int srcYStepHD, int spriteWidth, int overlay,
			int srcXIdxStartHD, int srcXIdxStepHD, int yStep)
	{
		int pixelColor;
		int overlayRed = (overlay >> 16) & 0xff;
		int overlayGreen = (overlay >> 8) & 0xff;
		int overlayBlue = overlay & 0xff;
		try
		{
			int srcXStartHD = srcXIdxHD;
			for (int y = -spriteScaledHeight; y < 0; y++)
			{
				int srcXIdxStart = (srcXIdxStartHD >> 16);
				int scaledWidthCrop = srcScaledWidth;
				if (srcXIdxStart < bounds.x)
				{
					int cropWidth = bounds.x - srcXIdxStart;
					scaledWidthCrop -= cropWidth;
					srcXIdxStart = bounds.x;
					srcXIdxHD += srcXStepHD * cropWidth;
				}
				if (srcXIdxStart + scaledWidthCrop >= bounds.width)
				{
					int cropWidth = (srcXIdxStart + scaledWidthCrop) - bounds.width;
					scaledWidthCrop -= cropWidth;
				}
				yStep = 1 - yStep;
				if (yStep != 0)
				{
					int srcYIdx = (srcYIdxHD >> 16) * spriteWidth;
					for (int x = srcXIdxStart; x < srcXIdxStart + scaledWidthCrop; x++)
					{
						int srcXIdx = (srcXIdxHD >> 16);
						pixelColor = srcPixels[srcXIdx + srcYIdx];
						if (pixelColor != 0)
						{
							int spriteRed = (pixelColor >> 16) & 0xff;
							int spriteGreen = (pixelColor >> 8) & 0xff;
							int spriteBlue = pixelColor & 0xff;
							
							if (spriteRed == spriteGreen && spriteGreen == spriteBlue)
							{
								dstPixels[x + gameWindowStartIdx] =
										((spriteRed*overlayRed & 0xff00) << 8)
										+ (spriteGreen*overlayGreen & 0xff00)
										+ ((spriteBlue*overlayBlue & 0xff00) >> 8);
							}
							else
								dstPixels[x + gameWindowStartIdx] = pixelColor;
						}
						srcXIdxHD += srcXStepHD;
					}
					
				}
				srcYIdxHD += srcYStepHD;
				srcXIdxHD = srcXStartHD;
				gameWindowStartIdx += gameWindowWidth;
				srcXIdxStartHD += srcXIdxStepHD;
			}
			return;
		}
		catch (Exception _ex)
		{
			System.out.println("error in transparent sprite plot routine");
		}
	}

	/**
	 * 
	 * @param dstPixels
	 * @param srcPixels
	 * @param srcXIdxHD
	 * @param srcYIdxHD
	 * @param gameWindowStartIdx
	 * @param srcScaledWidth
	 * @param spriteScaledHeight
	 * @param srcXStepHD
	 * @param srcYStepHD
	 * @param spriteWidth
	 * @param overlay
	 * @param k2
	 * @param srcXIdxStartHD
	 * @param srcXIdxStepHD
	 * @param yStep
	 */
	private void drawImageOverlayScale2(int dstPixels[], int srcPixels[],
			int srcXIdxHD, int srcYIdxHD, int gameWindowStartIdx,
			int srcScaledWidth, int spriteScaledHeight, int srcXStepHD,
			int srcYStepHD, int spriteWidth, int overlay, int k2,
			int srcXIdxStartHD, int srcXIdxStepHD, int yStep)
	{
		int pixelColor;
		int overlayRed = (overlay >> 16) & 0xff;
		int overlayGreen = (overlay >> 8) & 0xff;
		int overlayBlue = overlay & 0xff;
		int colorRed = (k2 >> 16) & 0xff;
		int colorGreen = (k2 >> 8) & 0xff;
		int colorBlue = k2 & 0xff;
		try
		{
			int srcXStartHD = srcXIdxHD;
			for (int y = -spriteScaledHeight; y < 0; y++)
			{
				int srcXIdxStart = (srcXIdxStartHD >> 16);
				int scaledWidthCrop = srcScaledWidth;
				if (srcXIdxStart < bounds.x)
				{
					int cropWidth = bounds.x - srcXIdxStart;
					scaledWidthCrop -= cropWidth;
					srcXIdxStart = bounds.x;
					srcXIdxHD += srcXStepHD * cropWidth;
				}
				if (srcXIdxStart + scaledWidthCrop >= bounds.width)
				{
					int cropWidth = (srcXIdxStart + scaledWidthCrop) - bounds.width;
					scaledWidthCrop -= cropWidth;
				}
				yStep = 1 - yStep;
				if (yStep != 0)
				{
					int srcYIdx = (srcYIdxHD >> 16) * spriteWidth;
					for (int x = srcXIdxStart; x < srcXIdxStart + scaledWidthCrop; x++)
					{
						pixelColor = srcPixels[(srcXIdxHD >> 16) + srcYIdx];
						if (pixelColor != 0)
						{
							int spriteRed = (pixelColor >> 16) & 0xff;
							int spriteGreen = (pixelColor >> 8) & 0xff;
							int spriteBlue = pixelColor & 0xff;
							
							if (spriteRed == spriteGreen && spriteGreen == spriteBlue)
							{
								dstPixels[x + gameWindowStartIdx] = 
										((spriteRed*overlayRed & 0xff00) << 8)
										+ (spriteGreen*overlayGreen & 0xff00)
										+ ((spriteBlue * overlayBlue & 0xff00) >> 8);
							}
							else if (spriteRed == 0xff && spriteGreen == spriteBlue)
							{
								dstPixels[x + gameWindowStartIdx] = 
										((spriteRed*colorRed & 0xff00) << 8)
										+ (spriteGreen*colorGreen & 0xff00)
										+ ((spriteBlue*colorBlue & 0xff00) >> 8);
							}
							else
								dstPixels[x + gameWindowStartIdx] = pixelColor;
						}
						srcXIdxHD += srcXStepHD;
					}

                }
                srcYIdxHD += srcYStepHD;
                srcXIdxHD = srcXStartHD;
                gameWindowStartIdx += gameWindowWidth;
                srcXIdxStartHD += srcXIdxStepHD;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in transparent sprite plot routine");
        }
    }

	private void drawMapSpriteWOShift(int imagePixels[], int spritePixels[],
			int offset, double x, double y, double xStep, double yStep,
			int length, int spriteWidth)
	{
		try 
		{
			int pixIdx;
			for (int i = length; i < 0; i++)
			{
				pixIdx = ((int) x) + ((int) y) * spriteWidth;
				imagePixels[offset] = spritePixels[pixIdx];
				offset++;
				x += xStep;
				y += yStep;
			}
		} catch (ArrayIndexOutOfBoundsException e) {}
		
	}
	
	private void drawMapSpriteWShift(int imagePixels[], int spritePixels[],
			int offset, double x, double y, double xStep, double yStep,
			int length, int spriteWidth)
	{
		int pixIdx;
		for (int i = length; i < 0; i++)
		{
			pixIdx = ((int) x) + ((int) y) * spriteWidth;
			if (pixIdx > 0 && spritePixels[pixIdx] != 0)
				imagePixels[offset] = spritePixels[pixIdx];
			offset++;
			x += xStep;
			y += yStep;
		}
	}

    private void displayLetter(int i, int x, int y, int colour,
    		byte letterInfo[], boolean transparent)
    {
        int letterStartIdx = letterInfo[i] * 16384
        		+ letterInfo[i + 1] * 128
        		+ letterInfo[i + 2];
        int letterWidth = letterInfo[i + 3];
        int letterHeight = letterInfo[i + 4];
        int letterXStart = x + letterInfo[i + 5];
        int letterYStart = y - letterInfo[i + 6];
        int windowStartIdx = letterXStart + letterYStart * gameWindowWidth;
        int windowSkipIdx = gameWindowWidth - letterWidth;
        int letterSkipIdx = 0;
        
        if (letterYStart < bounds.y)
        {
            int letterYCrop = bounds.y - letterYStart;
            letterHeight -= letterYCrop;
            letterYStart = bounds.y;
            letterStartIdx += letterYCrop * letterWidth;
            windowStartIdx += letterYCrop * gameWindowWidth;
        }
        if (letterYStart + letterHeight >= bounds.height)
            letterHeight -= ((letterYStart + letterHeight) - bounds.height) + 1;
        if (letterXStart < bounds.x)
        {
            int letterXCrop = bounds.x - letterXStart;
            letterWidth -= letterXCrop;
            letterXStart = bounds.x;
            letterStartIdx += letterXCrop;
            windowStartIdx += letterXCrop;
            letterSkipIdx += letterXCrop;
            windowSkipIdx += letterXCrop;
        }
        if (letterXStart + letterWidth >= bounds.width)
        {
            int letterXCrop = ((letterXStart + letterWidth) - bounds.width) + 1;
            letterWidth -= letterXCrop;
            letterSkipIdx += letterXCrop;
            windowSkipIdx += letterXCrop;
        }
        
        if (letterWidth > 0 && letterHeight > 0)
        {
            if (transparent)
            {
                drawLetterTransparent(imagePixelArray, letterInfo, colour,
                		letterStartIdx, windowStartIdx, letterWidth,
                		letterHeight, windowSkipIdx, letterSkipIdx);
            }
            else
            {
            	drawLetter(imagePixelArray, letterInfo, colour,
            			letterStartIdx, windowStartIdx, letterWidth,
            			letterHeight, windowSkipIdx, letterSkipIdx);
            }
        }
    }

    private void drawLetter(int dstPixels[], byte srcPixels[],
    		int color, int srcStart, int dstStart, int dstWidth,
    		int dstHeight, int dstStep, int srcStep)
    {
        try
        {
            for (int i2 = -dstHeight; i2 < 0; i2++)
            {
                for (int k2 = -dstWidth; k2 < 0; k2++)
                    if (srcPixels[srcStart++] != 0)
                        dstPixels[dstStart++] = color;
                    else
                        dstStart++;

                dstStart += dstStep;
                srcStart += srcStep;
            }
            return;
        }
        catch (Exception exception) {
            System.out.println("plotletter: " + exception);
            exception.printStackTrace();
            return;
        }
    }

    private void drawLetterTransparent(int dstPixels[], byte srcPixels[],
    		int color, int srcStart, int dstStart, int srcWidth, int srcHeight, int dstStep, int srcStep)
    {
        for (int y = -srcHeight; y < 0; y++)
        {
            for (int x = -srcWidth; x < 0; x++)
            {
                int fgAlpha = srcPixels[srcStart++] & 0xff;
                int bgAlpha = 256 - fgAlpha;
                if (fgAlpha > 30)
                {
                    if (fgAlpha >= 230)
                        dstPixels[dstStart] = color;
                    else {
                        int k2 = dstPixels[dstStart];
                        dstPixels[dstStart] = ((color & 0xff00ff)*fgAlpha + (k2 & 0xff00ff)*bgAlpha & 0xff00ff00)
                        		+ ((color & 0xff00)*fgAlpha + (k2 & 0xff00)*bgAlpha & 0xff0000) >> 8;
                    }
                }
                dstStart++;
            }

            dstStart += dstStep;
            srcStart += srcStep;
        }
    }

    private boolean loadArchive(int id, ZipFile archive)
    {
        try
        {
            ZipEntry e = archive.getEntry(String.valueOf(id));
            if (e == null)
                return false;
            ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(archive.getInputStream(e)));
            sprites[id] = Sprite.unpack(data);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
