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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

public class GameImage implements ImageProducer, ImageObserver {
    public Sprite[] sprites;
    private ZipFile entityArchive, mediaArchive, utilArchive,
    itemArchive, logoArchive, projectileArchive, textureArchive;
    public BufferedImage loginScreen;
    public static final int BACKGROUND = 0;//0x349ed8;

    public GameImage(int width, int height, int k, Component component)
    {
        f1Toggle = false;
        drawStringShadows = false;
        imageHeight = height;
        imageWidth = width;
        gameWindowWidthUnused = gameWindowWidth = width;
        gameWindowHeightUnused = gameWindowHeight = height;
        imagePixelArray = new int[width * height];
        sprites = new Sprite[k];
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
    
    public boolean loadSprite(int id, String packageName) {
    	if (packageName.equals("entity"))
        	return loadArchive(id, entityArchive);
    	else if (packageName.equals("media"))
        	return loadArchive(id, mediaArchive);
    	else if (packageName.equals("util"))
        	return loadArchive(id, utilArchive);
    	else if (packageName.equals("item"))
        	return loadArchive(id, itemArchive);
    	else if (packageName.equals("logo"))
        	return loadArchive(id, logoArchive);
    	else if (packageName.equals("projectile"))
        	return loadArchive(id, projectileArchive);
    	else if (packageName.equals("texture"))
        	return loadArchive(id, textureArchive);
    	return false;
    }

    private boolean loadArchive(int id, ZipFile archive) {
        try {
            ZipEntry e = archive.getEntry(String.valueOf(id));
            if (e == null)
                return false;
            ByteBuffer data = DataConversions.streamToBuffer(new BufferedInputStream(archive.getInputStream(e)));
            sprites[id] = Sprite.unpack(data);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void addConsumer(ImageConsumer imageconsumer) {
        imageConsumer = imageconsumer;
        imageconsumer.setDimensions(gameWindowWidth, gameWindowHeight);
        imageconsumer.setProperties(null);
        imageconsumer.setColorModel(colourModel);
        imageconsumer.setHints(14);
    }

    public synchronized boolean isConsumer(ImageConsumer imageconsumer) {
        return imageConsumer == imageconsumer;
    }

    public synchronized void removeConsumer(ImageConsumer imageconsumer) {
        if (imageConsumer == imageconsumer) {
            imageConsumer = null;
        }
    }

    public void startProduction(ImageConsumer imageconsumer) {
        addConsumer(imageconsumer);
    }

    public void requestTopDownLeftRightResend(ImageConsumer imageconsumer) {
        System.out.println("TDLR");
    }

    public synchronized void completePixels() {
        if (imageConsumer == null) {
            return;
        }
        imageConsumer.setPixels(0, 0, gameWindowWidth, gameWindowHeight,
        		colourModel, imagePixelArray, 0, gameWindowWidth);
        imageConsumer.imageComplete(2);
    }

    public void setDimensions(int x, int y, int width, int height) {
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        if (width > gameWindowWidth)
            width = gameWindowWidth;
        if (height > gameWindowHeight)
            height = gameWindowHeight;
        imageX = x;
        imageY = y;
        imageWidth = width;
        imageHeight = height;
    }

    public void resetDimensions() {
        imageX = 0;
        imageY = 0;
        imageWidth = gameWindowWidth;
        imageHeight = gameWindowHeight;
    }

    public void drawImage(Graphics g, int x, int y) {
        completePixels();
        g.drawImage(image, x, y, this);
    }

    public void resetImagePixels(int color)
    {
        int size = gameWindowWidth * gameWindowHeight;
        if (!f1Toggle) {
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

    public void method212(int i, int j, int k, int fgColor, int fg_alpha) {
        int bg_alpha = 256 - fg_alpha;
        int fg_red = (fgColor >> 16 & 0xff) * fg_alpha;
        int fg_green = (fgColor >> 8 & 0xff) * fg_alpha;
        int fg_blue = (fgColor & 0xff) * fg_alpha;
        int i3 = j - k;
        if (i3 < 0)
            i3 = 0;
        int j3 = j + k;
        if (j3 >= gameWindowHeight)
            j3 = gameWindowHeight - 1;
        byte yStep = 1;
        if (f1Toggle)
        {
            yStep = 2;
            if ((i3 & 1) != 0)
                i3++;
        }
        for (int y = i3; y <= j3; y += yStep) {
            int l3 = y - j;
            int i4 = (int) Math.sqrt(k * k - l3 * l3);
            int x = i - i4;
            if (x < 0)
                x = 0;
            int k4 = i + i4;
            if (k4 >= gameWindowWidth)
                k4 = gameWindowWidth - 1;
            int offset = x + y * gameWindowWidth;
            for (int i5 = x; i5 <= k4; i5++)
            {
                int bg_red = (imagePixelArray[offset] >> 16 & 0xff) * bg_alpha;
                int bg_green = (imagePixelArray[offset] >> 8 & 0xff) * bg_alpha;
                int bg_blue = (imagePixelArray[offset] & 0xff) * bg_alpha;
                int pixelVal = ((fg_red + bg_red >> 8) << 16) + ((fg_green + bg_green >> 8) << 8) + (fg_blue + bg_blue >> 8);
                imagePixelArray[offset++] = pixelVal;
            }

        }

    }

    public void drawBoxAlpha(int x, int y, int width, int height, int colour, int alpha) {
        if (x < imageX) {
            width -= imageX - x;
            x = imageX;
        }
        if (y < imageY) {
            height -= imageY - y;
            y = imageY;
        }
        if (x + width > imageWidth)
            width = imageWidth - x;
        if (y + height > imageHeight)
            height = imageHeight - y;
        int bgAlpha = 256 - alpha;
        int red_a_mult = (colour >> 16 & 0xff) * alpha;
        int green_a_mult = (colour >> 8 & 0xff) * alpha;
        int blue_a_mult = (colour & 0xff) * alpha;
        int skip = gameWindowWidth - width;
        byte yStep = 1;
        if (f1Toggle) {
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

    public void drawGradientBox(int x, int y, int width, int height, int colorTop, int colorBottom) {
        if (x < imageX) {
            width -= imageX - x;
            x = imageX;
        }
        if (x + width > imageWidth)
            width = imageWidth - x;
        int clrBtmRed = colorBottom >> 16 & 0xff;
        int clrBtmGre = colorBottom >> 8 & 0xff;
        int clrBtmBlu = colorBottom & 0xff;
        int clrTopRed = colorTop >> 16 & 0xff;
        int clrTopGre = colorTop >> 8 & 0xff;
        int clrTopBlu = colorTop & 0xff;
        int skipNLastPixels = gameWindowWidth - width;
        byte yStep = 1;
        if (f1Toggle)
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
            if (j + y >= imageY && j + y < imageHeight) 
            {
                int pixelClr = ((clrBtmRed * j + clrTopRed * (height - j)) / height << 16) + ((clrBtmGre * j + clrTopGre * (height - j)) / height << 8) + (clrBtmBlu * j + clrTopBlu * (height - j)) / height;
                for (int i = -width; i < 0; i++)
                    imagePixelArray[offset++] = pixelClr;
                offset += skipNLastPixels;
            } else {
                offset += gameWindowWidth;
            }
    }

    public void drawBox(int x, int y, int width, int height, int colour) {
        if (x < imageX)
        {
            width -= imageX - x;
            x = imageX;
        }
        if (y < imageY)
        {
            height -= imageY - y;
            y = imageY;
        }
        if (x + width > imageWidth)
            width = imageWidth - x;
        if (y + height > imageHeight)
            height = imageHeight - y;

        int skip = gameWindowWidth - width;
        byte yStep = 1;
        if (f1Toggle) {
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

    public void drawBoxEdge(int x1, int y1, int x2, int y2, int colour) {
        drawLineX(x1, y1, x2, colour);
        drawLineX(x1, (y1 + y2) - 1, x2, colour);
        drawLineY(x1, y1, y2, colour);
        drawLineY((x1 + x2) - 1, y1, y2, colour);
    }

    public void drawLineX(int x1, int y1, int x2, int colour) {
        if (y1 < imageY || y1 >= imageHeight)
            return;
        if (x1 < imageX) {
            x2 -= imageX - x1;
            x1 = imageX;
        }
        if (x1 + x2 > imageWidth)
            x2 = imageWidth - x1;
        int xPixel = x1 + y1 * gameWindowWidth;
        for (int yPixel = 0; yPixel < x2; yPixel++)
            imagePixelArray[xPixel + yPixel] = colour;

    }

    public void drawLineY(int x1, int y1, int y2, int colour) {
        if (x1 < imageX || x1 >= imageWidth)
            return;
        if (y1 < imageY) {
            y2 -= imageY - y1;
            y1 = imageY;
        }
        if (y1 + y2 > imageWidth)
            y2 = imageHeight - y1;
        int xPixel = x1 + y1 * gameWindowWidth;
        for (int yPixel = 0; yPixel < y2; yPixel++)
            imagePixelArray[xPixel + yPixel * gameWindowWidth] = colour;

    }

    public void setMinimapPixel(int x, int y, int colour) {
        if (x < imageX || y < imageY || x >= imageWidth || y >= imageHeight) {
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

    public void method221(int i, int j, int k, int l, int i1, int j1) {
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

    public static int convertRGBToLong(int red, int green, int blue) {
        return (red << 16) + (green << 8) + blue;
    }

    public void cleanupSprites() {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = null;
        }
    }

    public void storeSpriteHoriz(int index, int startX, int startY, int width, int height) {
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

    public void storeSpriteVert(int index, int startX, int startY, int width, int height) {
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

    public void drawPicture(int x, int y, int picture) {
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
            if (y < imageY) {
                int j2 = imageY - y;
                j1 -= j2;
                y = imageY;
                i1 += j2 * k1;
                l += j2 * gameWindowWidth;
            }
            if (y + j1 >= imageHeight)
                j1 -= ((y + j1) - imageHeight) + 1;
            if (x < imageX) {
                int k2 = imageX - x;
                k1 -= k2;
                x = imageX;
                i1 += k2;
                l += k2;
                i2 += k2;
                l1 += k2;
            }
            if (x + k1 >= imageWidth) {
                int l2 = ((x + k1) - imageWidth) + 1;
                k1 -= l2;
                i2 += l2;
                l1 += l2;
            }
            if (k1 <= 0 || j1 <= 0)
                return;
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l1 += gameWindowWidth;
                i2 += sprites[picture].getWidth();
                if ((y & 1) != 0) {
                    l += gameWindowWidth;
                    j1--;
                }
            }
            method235(imagePixelArray, sprites[picture].getPixels(), 0, i1, l, k1, j1, l1, i2, byte0);
        }
        catch (Exception e) {
            System.err.println("Error drawing: " + picture);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void spriteClip1(int startX, int startY, int newWidth, int newHeight, int spriteId) {
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
            if (startY < imageY) {
                int l3 = imageY - startY;
                newHeight -= l3;
                startY = 0;
                i3 += l3 * gameWindowWidth;
                i2 += k2 * l3;
            }
            if (startY + newHeight >= imageHeight)
                newHeight -= ((startY + newHeight) - imageHeight) + 1;
            if (startX < imageX) {
                int i4 = imageX - startX;
                newWidth -= i4;
                startX = 0;
                i3 += i4;
                l1 += j2 * i4;
                k3 += i4;
            }
            if (startX + newWidth >= imageWidth) {
                int j4 = ((startX + newWidth) - imageWidth) + 1;
                newWidth -= j4;
                k3 += j4;
            }
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                k3 += gameWindowWidth;
                k2 += k2;
                if ((startY & 1) != 0) {
                    i3 += gameWindowWidth;
                    newHeight--;
                }
            }
            plotSale1(imagePixelArray, sprites[spriteId].getPixels(), 0, l1, i2, i3, k3, newWidth, newHeight, j2, k2, spriteWidthInit, byte0);
            return;
        }
        catch (Exception _ex) {
            System.out.println("error in sprite clipping routine");
        }
    }

    public void method232(int i, int j, int spriteId, int l) {
        if (sprites[spriteId].requiresShift()) {
            i += sprites[spriteId].getXShift();
            j += sprites[spriteId].getYShift();
        }
        int i1 = i + j * gameWindowWidth;
        int j1 = 0;
        int spriteHeight = sprites[spriteId].getHeight();
        int spriteWidth = sprites[spriteId].getWidth();
        int i2 = gameWindowWidth - spriteWidth;
        int j2 = 0;
        if (j < imageY) {
            int k2 = imageY - j;
            spriteHeight -= k2;
            j = imageY;
            j1 += k2 * spriteWidth;
            i1 += k2 * gameWindowWidth;
        }
        if (j + spriteHeight >= imageHeight)
            spriteHeight -= ((j + spriteHeight) - imageHeight) + 1;
        if (i < imageX) {
            int l2 = imageX - i;
            spriteWidth -= l2;
            i = imageX;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (i + spriteWidth >= imageWidth) {
            int i3 = ((i + spriteWidth) - imageWidth) + 1;
            spriteWidth -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (spriteWidth <= 0 || spriteHeight <= 0)
            return;
        byte byte0 = 1;
        if (f1Toggle) {
            byte0 = 2;
            i2 += gameWindowWidth;
            j2 += sprites[spriteId].getWidth();
            if ((j & 1) != 0) {
                i1 += gameWindowWidth;
                spriteHeight--;
            }
        }
        method238(imagePixelArray, sprites[spriteId].getPixels(), 0, j1, i1, spriteWidth, spriteHeight, i2, j2, byte0, l);
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
    public void spriteClip2(int startX, int startY, int newWidth, int newHeight, int spriteId, int j1)
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
            if (startY < imageY) {
                int i4 = imageY - startY;
                newHeight -= i4;
                startY = 0;
                j3 += i4 * gameWindowWidth;
                j2 += l2 * i4;
            }
            if (startY + newHeight >= imageHeight)
                newHeight -= ((startY + newHeight) - imageHeight) + 1;
            if (startX < imageX) {
                int j4 = imageX - startX;
                newWidth -= j4;
                startX = 0;
                j3 += j4;
                i2 += k2 * j4;
                l3 += j4;
            }
            if (startX + newWidth >= imageWidth) {
                int k4 = ((startX + newWidth) - imageWidth) + 1;
                newWidth -= k4;
                l3 += k4;
            }
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l3 += gameWindowWidth;
                l2 += l2;
                if ((startY & 1) != 0) {
                    j3 += gameWindowWidth;
                    newHeight--;
                }
            }
            tranScale(imagePixelArray, sprites[spriteId].getPixels(), 0, i2, j2, j3, l3, newWidth, newHeight, k2, l2, spriteWidthInit, byte0, j1);
            return;
        }
        catch (Exception _ex) {
            System.out.println("error in sprite clipping routine");
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
    public void spriteClip3(int startX, int startY, int newWidth, int newHeight, int spriteId, int spriteColor)
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
            if (startY < imageY) {
                int i4 = imageY - startY;
                newHeight -= i4;
                startY = 0;
                j3 += i4 * gameWindowWidth;
                j2 += l2 * i4;
            }
            if (startY + newHeight >= imageHeight)
                newHeight -= ((startY + newHeight) - imageHeight) + 1;
            if (startX < imageX) {
                int j4 = imageX - startX;
                newWidth -= j4;
                startX = 0;
                j3 += j4;
                i2 += k2 * j4;
                l3 += j4;
            }
            if (startX + newWidth >= imageWidth) {
                int k4 = ((startX + newWidth) - imageWidth) + 1;
                newWidth -= k4;
                l3 += k4;
            }
            byte byte0 = 1;
            if (f1Toggle) {
                byte0 = 2;
                l3 += gameWindowWidth;
                l2 += l2;
                if ((startY & 1) != 0) {
                    j3 += gameWindowWidth;
                    newHeight--;
                }
            }
            plotScale2(imagePixelArray, sprites[spriteId].getPixels(), 0, i2, j2, j3, l3, newWidth, newHeight, k2, l2, spriteWidthInit, byte0, spriteColor);
            return;
        }
        catch (Exception _ex) {
            System.out.println("error in sprite clipping routine");
        }
    }
    
    /**
     * Plots an image to the gamewindow.
     * @param imageRaw
     * @param width
     * @param height
     * @param resize
     */
    public void imageToPixArray(BufferedImage imageRaw, int imgXPos, int imgYPos,
    		int width, int height, boolean resize)
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
    	method235(imagePixelArray, pixels, 0, 0,
    			imgYPos*imageWidth+imgXPos, image.getWidth(),
    			image.getHeight(), imageWidth-image.getWidth(), 0, 1);
    }

    private void method235(int pixelArray[], int imagePixels[], int imagePixVal,
    		int imageArrayIdx, int pixelArrayIdx, int imageWidth, int imageHeight,
    		int skipNLastPixEachRowPixArray, int skipNLastPixEachRowImgArray, int l1)
    {
        int oneFourthImageWidth = -(imageWidth >> 2);
        imageWidth = -(imageWidth & 3);
        for (int j2 = -imageHeight; j2 < 0; j2 += l1)
        {
            for (int k2 = oneFourthImageWidth; k2 < 0; k2++)
            {
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
            }
            
            for (int l2 = imageWidth; l2 < 0; l2++) {
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
            }

            pixelArrayIdx += skipNLastPixEachRowPixArray;
            imageArrayIdx += skipNLastPixEachRowImgArray;
        }
    	/*
        int oneFourthImageWidth = -(imageWidth >> 2);
        imageWidth = -(imageWidth & 3);
        for (int j2 = -imageHeight; j2 < 0; j2 += l1)
        {
            for (int k2 = oneFourthImageWidth; k2 < 0; k2++)
            {
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
            }

            for (int l2 = imageWidth; l2 < 0; l2++) {
                imagePixVal = imagePixels[imageArrayIdx++];
                if (imagePixVal != 0)
                    pixelArray[pixelArrayIdx++] = imagePixVal;
                else
                    pixelArrayIdx++;
            }

            pixelArrayIdx += skipNLastPixEachRowPixArray;
            imageArrayIdx += skipNLastPixEachRowImgArray;
        }*/

    }

    private void plotSale1(int ai[], int ai1[], int i, int j, int k, int l, int i1,
                           int j1, int k1, int l1, int i2, int j2, int k2) {
        try {
            int l2 = j;
            for (int i3 = -k1; i3 < 0; i3 += k2) {
                int j3 = (k >> 16) * j2;
                for (int k3 = -j1; k3 < 0; k3++) {
                    i = ai1[(j >> 16) + j3];
                    if (i != 0)
                        ai[l++] = i;
                    else
                        l++;
                    j += l1;
                }

                k += i2;
                j = l2;
                l += i1;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in plot_scale");
        }
    }

    private void method238(int ai[], int ai1[], int i, int j, int k, int l, int i1,
                           int j1, int k1, int l1, int i2) {
        int j2 = 256 - i2;
        for (int k2 = -i1; k2 < 0; k2 += l1) {
            for (int l2 = -l; l2 < 0; l2++) {
                i = ai1[j++];
                if (i != 0) {
                    int i3 = ai[k];
                    ai[k++] = ((i & 0xff00ff) * i2 + (i3 & 0xff00ff) * j2 & 0xff00ff00) + ((i & 0xff00) * i2 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
                } else {
                    k++;
                }
            }

            k += j1;
            j += k1;
        }

    }

    private void tranScale(int ai[], int ai1[], int i, int j, int k, int l, int i1,
                           int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
        int i3 = 256 - l2;
        try {
            int j3 = j;
            for (int k3 = -k1; k3 < 0; k3 += k2) {
                int l3 = (k >> 16) * j2;
                for (int i4 = -j1; i4 < 0; i4++) {
                    i = ai1[(j >> 16) + l3];
                    if (i != 0) {
                        int j4 = ai[l];
                        ai[l++] = ((i & 0xff00ff) * l2 + (j4 & 0xff00ff) * i3 & 0xff00ff00) + ((i & 0xff00) * l2 + (j4 & 0xff00) * i3 & 0xff0000) >> 8;
                    } else {
                        l++;
                    }
                    j += l1;
                }

                k += i2;
                j = j3;
                l += i1;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in tran_scale");
        }
    }

    private void plotScale2(int ai[], int ai1[], int i, int j, int k, int l, int i1,
                            int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
        int i3 = l2 >> 16 & 0xff;
        int j3 = l2 >> 8 & 0xff;
        int k3 = l2 & 0xff;
        try {
            int l3 = j;
            for (int i4 = -k1; i4 < 0; i4 += k2) {
                int j4 = (k >> 16) * j2;
                for (int k4 = -j1; k4 < 0; k4++) {
                    i = ai1[(j >> 16) + j4];
                    if (i != 0) {
                        int l4 = i >> 16 & 0xff;
                        int i5 = i >> 8 & 0xff;
                        int j5 = i & 0xff;
                        if (l4 == i5 && i5 == j5)
                            ai[l++] = ((l4 * i3 >> 8) << 16) + ((i5 * j3 >> 8) << 8) + (j5 * k3 >> 8);
                        else
                            ai[l++] = i;
                    } else {
                        l++;
                    }
                    j += l1;
                }

                k += i2;
                j = l3;
                l += i1;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in plot_scale");
        }
    }

    public void drawMinimapTiles(int xCorner, int yCorner,
    		int sprite, int rot1, int rot2)
    {
        int windowWidth = gameWindowWidth;
        int windowHeight = gameWindowHeight;
        if (sin256 == null)
        {
            sin256 = new double[256];
            for (int l1 = 0; l1 < 256; l1++)
            	sin256[l1] = Math.sin((double) l1 * 0.02454369D);
        }
        if (cos256 == null)
        {
            cos256 = new double[256];
            for (int l1 = 0; l1 < 256; l1++)
            	cos256[l1] = Math.cos((double) l1 * 0.02454369D);
        }
        int p0_x = -sprites[sprite].getTotalWidth() / 2;
        int p0_y = -sprites[sprite].getTotalHeight() / 2;
        if (sprites[sprite].requiresShift()) {
            p0_x += sprites[sprite].getXShift();
            p0_y += sprites[sprite].getYShift();
        }
        int p2_x = p0_x + sprites[sprite].getWidth();
        int p2_y = p0_y + sprites[sprite].getHeight();
        int p3_x = p2_x;
        int p3_y = p0_y;
        int p1_x = p0_x;
        int p1_y = p2_y;
        rot1 &= 0xff;
        double sin = sin256[rot1] * rot2;
        double cos = cos256[rot1] * rot2;
        int p0_x_rot = (int) (xCorner + (p0_y * sin + p0_x * cos) / 128);
        int p0_y_rot = (int) (yCorner + (p0_y * cos - p0_x * sin) / 128);
        int p3_x_rot = (int) (xCorner + (p3_y * sin + p3_x * cos) / 128);
        int p3_y_rot = (int) (yCorner + (p3_y * cos - p3_x * sin) / 128);
        int p2_x_rot = (int) (xCorner + (p2_y * sin + p2_x * cos) / 128);
        int p2_y_rot = (int) (yCorner + (p2_y * cos - p2_x * sin) / 128);
        int p1_x_rot = (int) (xCorner + (p1_y * sin + p1_x * cos) / 128);
        int p1_y_rot = (int) (yCorner + (p1_y * cos - p1_x * sin) / 128);
        if (rot2 == 192 && (rot1 & 0x3f) == (anInt348 & 0x3f))
            anInt346++;
        else if (rot2 == 128)
            anInt348 = rot1;
        else
            anInt347++;
        int ymin = p0_y_rot;
        int ymax = p0_y_rot;
        if (p3_y_rot < ymin)
            ymin = p3_y_rot;
        else if (p3_y_rot > ymax)
            ymax = p3_y_rot;
        if (p2_y_rot < ymin)
            ymin = p2_y_rot;
        else if (p2_y_rot > ymax)
            ymax = p2_y_rot;
        if (p1_y_rot < ymin)
            ymin = p1_y_rot;
        else if (p1_y_rot > ymax)
            ymax = p1_y_rot;
        if (ymin < imageY)
            ymin = imageY;
        if (ymax > imageHeight)
            ymax = imageHeight;
        if (anIntArray340 == null
        		|| anIntArray340.length != windowHeight + 1)
        {
            anIntArray340 = new int[windowHeight + 1];
            anIntArray341 = new int[windowHeight + 1];
            anIntArray342 = new int[windowHeight + 1];
            anIntArray343 = new int[windowHeight + 1];
            anIntArray344 = new int[windowHeight + 1];
            anIntArray345 = new int[windowHeight + 1];
        }
        for (int i7 = ymin; i7 <= ymax; i7++) {
            anIntArray340[i7] = 0x5f5e0ff;
            anIntArray341[i7] = 0xfa0a1f01;
        }

        int slope_1 = 0;
        int slope_3 = 0;
        int slope_2 = 0;
        int spriteWidth = sprites[sprite].getWidth();
        int spriteHeight = sprites[sprite].getHeight();
        p0_x = 0;
        p0_y = 0;
        p3_x = spriteWidth - 1;
        p3_y = 0;
        p2_x = spriteWidth - 1;
        p2_y = spriteHeight - 1;
        p1_x = 0;
        p1_y = spriteHeight - 1;
        if (p1_y_rot != p0_y_rot)
        {
            slope_1 = (p1_x_rot - p0_x_rot << 8) / (p1_y_rot - p0_y_rot);
            slope_2 = (p1_y - p0_y << 8) / (p1_y_rot - p0_y_rot);
        }
        int min_y_rot;
        int max_y_rot;
        int min_x_rot;
        int min_y;
        if (p0_y_rot > p1_y_rot) {
            min_x_rot = p1_x_rot << 8;
            min_y = p1_y << 8;
            min_y_rot = p1_y_rot;
            max_y_rot = p0_y_rot;
        } else {
            min_x_rot = p0_x_rot << 8;
            min_y = p0_y << 8;
            min_y_rot = p0_y_rot;
            max_y_rot = p1_y_rot;
        }
        if (min_y_rot < 0) {
            min_x_rot -= slope_1 * min_y_rot;
            min_y -= slope_2 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int l9 = min_y_rot; l9 <= max_y_rot; l9++) {
            anIntArray340[l9] = anIntArray341[l9] = min_x_rot;
            min_x_rot += slope_1;
            anIntArray342[l9] = anIntArray343[l9] = 0;
            anIntArray344[l9] = anIntArray345[l9] = min_y;
            min_y += slope_2;
        }

        if (p3_y_rot != p0_y_rot) {
            slope_1 = (p3_x_rot - p0_x_rot << 8) / (p3_y_rot - p0_y_rot);
            slope_3 = (p3_x - p0_x << 8) / (p3_y_rot - p0_y_rot);
        }
        int min_x;
        if (p0_y_rot > p3_y_rot) {
            min_x_rot = p3_x_rot << 8;
            min_x = p3_x << 8;
            min_y_rot = p3_y_rot;
            max_y_rot = p0_y_rot;
        } else {
            min_x_rot = p0_x_rot << 8;
            min_x = p0_x << 8;
            min_y_rot = p0_y_rot;
            max_y_rot = p3_y_rot;
        }
        if (min_y_rot < 0) {
            min_x_rot -= slope_1 * min_y_rot;
            min_x -= slope_3 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int i10 = min_y_rot; i10 <= max_y_rot; i10++)
        {
            if (min_x_rot < anIntArray340[i10]) {
                anIntArray340[i10] = min_x_rot;
                anIntArray342[i10] = min_x;
                anIntArray344[i10] = 0;
            }
            if (min_x_rot > anIntArray341[i10]) {
                anIntArray341[i10] = min_x_rot;
                anIntArray343[i10] = min_x;
                anIntArray345[i10] = 0;
            }
            min_x_rot += slope_1;
            min_x += slope_3;
        }

        if (p2_y_rot != p3_y_rot) {
            slope_1 = (p2_x_rot - p3_x_rot << 8) / (p2_y_rot - p3_y_rot);
            slope_2 = (p2_y - p3_y << 8) / (p2_y_rot - p3_y_rot);
        }
        if (p3_y_rot > p2_y_rot) {
            min_x_rot = p2_x_rot << 8;
            min_x = p2_x << 8;
            min_y = p2_y << 8;
            min_y_rot = p2_y_rot;
            max_y_rot = p3_y_rot;
        } else {
            min_x_rot = p3_x_rot << 8;
            min_x = p3_x << 8;
            min_y = p3_y << 8;
            min_y_rot = p3_y_rot;
            max_y_rot = p2_y_rot;
        }
        if (min_y_rot < 0) {
            min_x_rot -= slope_1 * min_y_rot;
            min_y -= slope_2 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int j10 = min_y_rot; j10 <= max_y_rot; j10++) {
            if (min_x_rot < anIntArray340[j10]) {
                anIntArray340[j10] = min_x_rot;
                anIntArray342[j10] = min_x;
                anIntArray344[j10] = min_y;
            }
            if (min_x_rot > anIntArray341[j10]) {
                anIntArray341[j10] = min_x_rot;
                anIntArray343[j10] = min_x;
                anIntArray345[j10] = min_y;
            }
            min_x_rot += slope_1;
            min_y += slope_2;
        }

        if (p1_y_rot != p2_y_rot) {
            slope_1 = (p1_x_rot - p2_x_rot << 8) / (p1_y_rot - p2_y_rot);
            slope_3 = (p1_x - p2_x << 8) / (p1_y_rot - p2_y_rot);
        }
        if (p2_y_rot > p1_y_rot) {
            min_x_rot = p1_x_rot << 8;
            min_x = p1_x << 8;
            min_y = p1_y << 8;
            min_y_rot = p1_y_rot;
            max_y_rot = p2_y_rot;
        } else {
            min_x_rot = p2_x_rot << 8;
            min_x = p2_x << 8;
            min_y = p2_y << 8;
            min_y_rot = p2_y_rot;
            max_y_rot = p1_y_rot;
        }
        if (min_y_rot < 0) {
            min_x_rot -= slope_1 * min_y_rot;
            min_x -= slope_3 * min_y_rot;
            min_y_rot = 0;
        }
        if (max_y_rot > windowHeight - 1)
            max_y_rot = windowHeight - 1;
        for (int k10 = min_y_rot; k10 <= max_y_rot; k10++) {
            if (min_x_rot < anIntArray340[k10]) {
                anIntArray340[k10] = min_x_rot;
                anIntArray342[k10] = min_x;
                anIntArray344[k10] = min_y;
            }
            if (min_x_rot > anIntArray341[k10]) {
                anIntArray341[k10] = min_x_rot;
                anIntArray343[k10] = min_x;
                anIntArray345[k10] = min_y;
            }
            min_x_rot += slope_1;
            min_x += slope_3;
        }

        int offset = ymin * windowWidth;
        int mapPixels[] = sprites[sprite].getPixels();
        for (int y = ymin; y < ymax; y++)
        {
            int xStart = anIntArray340[y] >> 8;
            int xEnd = anIntArray341[y] >> 8;
            if (xEnd - xStart <= 0) {
                offset += windowWidth;
            } else {
                int l11 = anIntArray342[y] << 9;
                int i12 = ((anIntArray343[y] << 9) - l11) / (xEnd - xStart);
                int j12 = anIntArray344[y] << 9;
                int k12 = ((anIntArray345[y] << 9) - j12) / (xEnd - xStart);
                if (xStart < imageX) {
                    l11 += (imageX - xStart) * i12;
                    j12 += (imageX - xStart) * k12;
                    xStart = imageX;
                }
                if (xEnd > imageWidth)
                    xEnd = imageWidth;
                if (!f1Toggle || (y & 1) == 0)
                    if (!sprites[sprite].requiresShift())
                        method243(imagePixelArray, mapPixels, 0, offset + xStart, l11, j12, i12, k12, xStart - xEnd, spriteWidth);
                    else
                        method244(imagePixelArray, mapPixels, 0, offset + xStart, l11, j12, i12, k12, xStart - xEnd, spriteWidth);
                offset += windowWidth;
            }
        }

    }

    private void method243(int ai[], int ai1[], int i, int j, int k, int l, int i1,
                           int j1, int k1, int l1) {
        for (i = k1; i < 0; i++) {
            imagePixelArray[j++] = ai1[(k >> 17) + (l >> 17) * l1];
            k += i1;
            l += j1;
        }

    }

    private void method244(int ai[], int ai1[], int i, int j, int k, int l, int i1,
                           int j1, int k1, int l1) {
        for (int i2 = k1; i2 < 0; i2++) {
            i = ai1[(k >> 17) + (l >> 17) * l1];
            if (i != 0)
                imagePixelArray[j++] = i;
            else
                j++;
            k += i1;
            l += j1;
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
     * @param newWidth
     * @param newHeight
     * @param spriteId
     * @param hairColor
     * @param skinColor
     * @param l1
     * @param flip
     */
    public void spriteClip4(int startX, int startY, int newWidth, int newHeight,
    		int spriteId, int hairColor, int skinColor, int l1, boolean flip)
    {
        try {
            if (hairColor == 0)
                hairColor = 0xffffff;
            if (skinColor == 0)
                skinColor = 0xffffff;
            int spriteWidth = sprites[spriteId].getWidth();
            int spriteHeight = sprites[spriteId].getHeight();
            int k2 = 0;
            int l2 = 0;
            int i3 = l1 << 16;
            int j3 = (spriteWidth << 16) / newWidth;
            int k3 = (spriteHeight << 16) / newHeight;
            int l3 = -(l1 << 16) / newHeight;
            if (sprites[spriteId].requiresShift())
            {
                int totalWidth = sprites[spriteId].getTotalWidth();
                int totalHeight = sprites[spriteId].getTotalHeight();
                j3 = (totalWidth << 16) / newWidth;
                k3 = (totalHeight << 16) / newHeight;
                int xShift = sprites[spriteId].getXShift();
                int yShift = sprites[spriteId].getYShift();
                if (flip)
                    xShift = totalWidth - sprites[spriteId].getWidth() - xShift;
                startX += ((xShift * newWidth + totalWidth) - 1) / totalWidth;
                int l5 = ((yShift * newHeight + totalHeight) - 1) / totalHeight;
                startY += l5;
                i3 += l5 * l3;
                if ((xShift * newWidth) % totalWidth != 0)
                    k2 = (totalWidth - (xShift * newWidth) % totalWidth << 16) / newWidth;
                if ((yShift * newHeight) % totalHeight != 0)
                    l2 = (totalHeight - (yShift * newHeight) % totalHeight << 16) / newHeight;
                newWidth = ((((sprites[spriteId].getWidth() << 16) - k2) + j3) - 1) / j3;
                newHeight = ((((sprites[spriteId].getHeight() << 16) - l2) + k3) - 1) / k3;
            }
            int windowYIdx = startY * gameWindowWidth;
            i3 += startX << 16;
            if (startY < imageY) {
                int l4 = imageY - startY;
                newHeight -= l4;
                startY = imageY;
                windowYIdx += l4 * gameWindowWidth;
                l2 += k3 * l4;
                i3 += l3 * l4;
            }
            if (startY + newHeight >= imageHeight)
                newHeight -= ((startY + newHeight) - imageHeight) + 1;
            int i5 = windowYIdx / gameWindowWidth & 1;
            if (!f1Toggle)
                i5 = 2;
            if (skinColor == 0xffffff)
            {
                if (!flip)
                {              	
                    spritePlotTransparent(
                    		imagePixelArray, sprites[spriteId].getPixels(),
                    		0, k2, l2, windowYIdx, newWidth, newHeight, j3, k3,
                    		spriteWidth, hairColor, i3, l3, i5);
                    return;
                }
                else
                {
                    spritePlotTransparent(
                    		imagePixelArray, sprites[spriteId].getPixels(), 0,
                    		(sprites[spriteId].getWidth() << 16) - k2 - 1, l2,
                    		windowYIdx, newWidth, newHeight, -j3, k3, spriteWidth,
                    		hairColor, i3, l3, i5);
                    return;
                }
            }
            if (!flip)
            {
                spritePlotTransparent(
                		imagePixelArray, sprites[spriteId].getPixels(), 0,
                		k2, l2, windowYIdx, newWidth, newHeight, j3, k3,
                		spriteWidth, hairColor, skinColor, i3, l3, i5);
                return;
            }
            else
            {
                spritePlotTransparent(
                		imagePixelArray, sprites[spriteId].getPixels(), 0,
                		(sprites[spriteId].getWidth() << 16) - k2 - 1, l2,
                		windowYIdx, newWidth, newHeight, -j3, k3, spriteWidth,
                		hairColor, skinColor, i3, l3, i5);
                return;
            }
        }
        catch (Exception _ex)
        {
            System.out.println("error in sprite clipping routine");
        }
    }
/*
--------------------------------------------------------------------------------
 */
    private void spritePlotTransparent(
    		int imagePixels[], int spritePixels[], int pixelColor, int j, int k, int gameWindowStartIdx,
    		int spriteBoxWidth, int spriteBoxHeight, int k1, int l1, int spriteWidth,
    		int overlay, int k2, int l2, int i3)
    {
    	//j += (int)(0x1000000*Math.random());
    	//k += (int)(0x1000000*Math.random());
    	//l += (int)(100*Math.random());
        int redOverlay = overlay >> 16 & 0xff;
        int greenOverlay = overlay >> 8 & 0xff;
        int blueOverlay = overlay & 0xff;
        try
        {
            int l4 = j;
            for (int spritePixelRow = -spriteBoxHeight; spritePixelRow < 0; spritePixelRow++)
            {
                int j5 = (k >> 16) * spriteWidth;
                int k5 = k2 >> 16;
                int l5 = spriteBoxWidth;
                if (k5 < imageX)
                {
                    int i6 = imageX - k5;
                    l5 -= i6;
                    k5 = imageX;
                    j += k1 * i6;
                }
                if (k5 + l5 >= imageWidth)
                {
                    int j6 = (k5 + l5) - imageWidth;
                    l5 -= j6;
                }
                i3 = 1 - i3;
                if (i3 != 0)
                {
                    for (int spritePixelIdx = k5; spritePixelIdx < k5 + l5; spritePixelIdx++)
                    {
                        pixelColor = spritePixels[(j >> 16) + j5];
                        if (pixelColor != 0)
                        {
                            int spriteRed = pixelColor >> 16 & 0xff;
                            int spriteGreen = pixelColor >> 8 & 0xff;
                            int spriteBlue = pixelColor & 0xff;
                            if (spriteRed == spriteGreen
                            		&& spriteGreen == spriteBlue)
                            {  // apply color mask
                                imagePixels[spritePixelIdx+ + gameWindowStartIdx] = (((spriteRed * redOverlay >> 8) << 16)
                                		+ ((spriteGreen * greenOverlay >> 8) << 8)
                                		+ (spriteBlue * blueOverlay >> 8));
                            }
                            else
                            {  // use the sprite color
                                imagePixels[spritePixelIdx + gameWindowStartIdx] = pixelColor;
                            }
                        }
                        j += k1;
                    }

                }
                k += l1;
                j = l4;
                gameWindowStartIdx += gameWindowWidth;
                k2 += l2;
            }
            return;
        }
        catch (Exception _ex)
        {
            System.out.println("error in transparent sprite plot routine");
        }
    }

    private void spritePlotTransparent(
    		int ai[], int ai1[], int i, int j, int k, int l, int i1, int j1,
    		int k1, int l1, int i2, int overlay, int k2, int l2, int i3, int j3)
    {
        int j4 = overlay >> 16 & 0xff;
        int k4 = overlay >> 8 & 0xff;
        int l4 = overlay & 0xff;
        int i5 = k2 >> 16 & 0xff;
        int j5 = k2 >> 8 & 0xff;
        int k5 = k2 & 0xff;
        try {
            int l5 = j;
            for (int i6 = -j1; i6 < 0; i6++) {
                int j6 = (k >> 16) * i2;
                int k6 = l2 >> 16;
                int l6 = i1;
                if (k6 < imageX) {
                    int i7 = imageX - k6;
                    l6 -= i7;
                    k6 = imageX;
                    j += k1 * i7;
                }
                if (k6 + l6 >= imageWidth) {
                    int j7 = (k6 + l6) - imageWidth;
                    l6 -= j7;
                }
                j3 = 1 - j3;
                if (j3 != 0) {
                    for (int k7 = k6; k7 < k6 + l6; k7++) {
                        i = ai1[(j >> 16) + j6];
                        if (i != 0) {
                            int k3 = i >> 16 & 0xff;
                            int l3 = i >> 8 & 0xff;
                            int i4 = i & 0xff;
                            if (k3 == l3 && l3 == i4)
                                ai[k7 + l] = ((k3 * j4 >> 8) << 16) + ((l3 * k4 >> 8) << 8) + (i4 * l4 >> 8);
                            else if (k3 == 255 && l3 == i4)
                                ai[k7 + l] = ((k3 * i5 >> 8) << 16) + ((l3 * j5 >> 8) << 8) + (i4 * k5 >> 8);
                            else
                                ai[k7 + l] = i;
                        }
                        j += k1;
                    }

                }
                k += l1;
                j = l5;
                l += gameWindowWidth;
                l2 += i3;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in transparent sprite plot routine");
        }
    }

    private void spritePlotTransparent(
    		int ai[], byte abyte0[], int ai1[], int i, int j, int k, int l,
    		int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2,
    		int i3)
    {
        int i4 = overlay >> 16 & 0xff;
        int j4 = overlay >> 8 & 0xff;
        int k4 = overlay & 0xff;
        try {
            int l4 = j;
            for (int i5 = -j1; i5 < 0; i5++) {
                int j5 = (k >> 16) * i2;
                int k5 = k2 >> 16;
                int l5 = i1;
                if (k5 < imageX) {
                    int i6 = imageX - k5;
                    l5 -= i6;
                    k5 = imageX;
                    j += k1 * i6;
                }
                if (k5 + l5 >= imageWidth) {
                    int j6 = (k5 + l5) - imageWidth;
                    l5 -= j6;
                }
                i3 = 1 - i3;
                if (i3 != 0) {
                    for (int k6 = k5; k6 < k5 + l5; k6++) {
                        i = abyte0[(j >> 16) + j5] & 0xff;
                        if (i != 0) {
                            i = ai1[i];
                            int j3 = i >> 16 & 0xff;
                            int k3 = i >> 8 & 0xff;
                            int l3 = i & 0xff;
                            if (j3 == k3 && k3 == l3)
                                ai[k6 + l] = ((j3 * i4 >> 8) << 16) + ((k3 * j4 >> 8) << 8) + (l3 * k4 >> 8);
                            else
                                ai[k6 + l] = i;
                        }
                        j += k1;
                    }

                }
                k += l1;
                j = l4;
                l += gameWindowWidth;
                k2 += l2;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in transparent sprite plot routine");
        }
    }

    private void spritePlotTransparent(
    		int ai[], byte abyte0[], int ai1[], int i, int j, int k, int l,
    		int i1, int j1, int k1, int l1, int i2, int overlay, int k2, int l2,
    		int i3, int j3)
{
        int j4 = overlay >> 16 & 0xff;
        int k4 = overlay >> 8 & 0xff;
        int l4 = overlay & 0xff;
        int i5 = k2 >> 16 & 0xff;
        int j5 = k2 >> 8 & 0xff;
        int k5 = k2 & 0xff;
        try {
            int l5 = j;
            for (int i6 = -j1; i6 < 0; i6++) {
                int j6 = (k >> 16) * i2;
                int k6 = l2 >> 16;
                int l6 = i1;
                if (k6 < imageX) {
                    int i7 = imageX - k6;
                    l6 -= i7;
                    k6 = imageX;
                    j += k1 * i7;
                }
                if (k6 + l6 >= imageWidth) {
                    int j7 = (k6 + l6) - imageWidth;
                    l6 -= j7;
                }
                j3 = 1 - j3;
                if (j3 != 0) {
                    for (int k7 = k6; k7 < k6 + l6; k7++) {
                        i = abyte0[(j >> 16) + j6] & 0xff;
                        if (i != 0) {
                            i = ai1[i];
                            int k3 = i >> 16 & 0xff;
                            int l3 = i >> 8 & 0xff;
                            int i4 = i & 0xff;
                            if (k3 == l3 && l3 == i4)
                                ai[k7 + l] = ((k3 * j4 >> 8) << 16) + ((l3 * k4 >> 8) << 8) + (i4 * l4 >> 8);
                            else if (k3 == 255 && l3 == i4)
                                ai[k7 + l] = ((k3 * i5 >> 8) << 16) + ((l3 * j5 >> 8) << 8) + (i4 * k5 >> 8);
                            else
                                ai[k7 + l] = i;
                        }
                        j += k1;
                    }

                }
                k += l1;
                j = l5;
                l += gameWindowWidth;
                l2 += i3;
            }

            return;
        }
        catch (Exception _ex) {
            System.out.println("error in transparent sprite plot routine");
        }
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
            drawLetter(font, fontmetrics, charSet.charAt(charSetOffset), charSetOffset, gameWindow, fontNumber, addCharWidth);

        aByteArrayArray336[fontNumber] = new byte[anInt350];
        for (int i1 = 0; i1 < anInt350; i1++)
            aByteArrayArray336[fontNumber][i1] = aByteArray351[i1];
    }
    
    public static void drawLetter(Font font, FontMetrics fontmetrics, char letter, int charSetOffset, GameWindow gameWindow, int fontNumber, boolean addCharWidth) {
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
        aByteArray351[charSetOffset * 9] = (byte) (anInt350 / 16384);
        aByteArray351[charSetOffset * 9 + 1] = (byte) (anInt350 / 128 & 0x7f);
        aByteArray351[charSetOffset * 9 + 2] = (byte) (anInt350 & 0x7f);
        aByteArray351[charSetOffset * 9 + 3] = (byte) (j2 - l1);
        aByteArray351[charSetOffset * 9 + 4] = (byte) (k2 - i2);
        aByteArray351[charSetOffset * 9 + 5] = (byte) l1;
        aByteArray351[charSetOffset * 9 + 6] = (byte) (i1 - i2);
        aByteArray351[charSetOffset * 9 + 7] = (byte) oldCharWidth;
        aByteArray351[charSetOffset * 9 + 8] = (byte) k1;
        for (int k5 = i2; k5 < k2; k5++) {
            for (int i6 = l1; i6 < j2; i6++) {
                int j6 = ai[i6 + k5 * charWidth] & 0xff;
                if (j6 > 30 && j6 < 230)
                    aBooleanArray349[fontNumber] = true;
                aByteArray351[anInt350++] = (byte) j6;
            }
        }
    }

    public void drawBoxTextRight(String s, int i, int j, int k, int l) {
        drawString(s, i - textWidth(s, k), j, k, l);
    }

    public void drawText(String s, int i, int j, int k, int l) {
        drawString(s, i - textWidth(s, k) / 2, j, k, l);
    }

    public void drawBoxTextColour(String s, int i, int j, int k, int l, int i1) {
        try {
            int j1 = 0;
            byte abyte0[] = aByteArrayArray336[k];
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
            byte abyte0[] = aByteArrayArray336[k];
            for (int offset = 0; offset < string.length(); offset++)
                if (string.charAt(offset) == '@' && offset + 4 < string.length() && string.charAt(offset + 4) == '@') {
                    if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("red"))
                        colour = 0xff0000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("lre"))
                        colour = 0xff9040;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("yel"))
                        colour = 0xffff00;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gre"))
                        colour = 65280;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("blu"))
                        colour = 255;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("cya"))
                        colour = 65535;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("mag"))
                        colour = 0xff00ff;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("whi"))
                        colour = 0xffffff;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("bla"))
                        colour = 0;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("dre"))
                        colour = 0xc00000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("ora"))
                        colour = 0xff9040;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("ran"))
                        colour = (int) (Math.random() * 16777215D);
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("or1"))
                        colour = 0xffb000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("or2"))
                        colour = 0xff7000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("or3"))
                        colour = 0xff3000;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gr1"))
                        colour = 0xc0ff00;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gr2"))
                        colour = 0x80ff00;
                    else if (string.substring(offset + 1, offset + 4).equalsIgnoreCase("gr3"))
                        colour = 0x40ff00;
                    offset += 4;
                } else
                if (string.charAt(offset) == '~' && offset + 4 < string.length() && string.charAt(offset + 4) == '~') {
                    char c = string.charAt(offset + 1);
                    char c1 = string.charAt(offset + 2);
                    char c2 = string.charAt(offset + 3);
                    if (c >= '0' && c <= '9' && c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9')
                        x = Integer.parseInt(string.substring(offset + 1, offset + 4));
                    offset += 4;
                } else
                if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("adm")) {
                    spriteClip4(x - 12, y - 16, 30, 20, 2339, -256, 0, 0, false);
                    x += 14;
                    offset += 4;
                } else
                if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("mod")) {
                    spriteClip4(x - 12, y - 16, 30, 20, 2339, -2302756, 0, 0, false);
                    x += 14;
                    offset += 4;
                } else
                if (string.charAt(offset) == '#' && offset + 4 < string.length() && string.charAt(offset + 4) == '#' && string.substring(offset + 1, offset + 4).equalsIgnoreCase("pmd")) {
                    spriteClip4(x - 12, y - 16, 30, 20, 2339, -13382656, 0, 0, false);
                    x += 14;
                    offset += 4;
                } else {
                    int charIndex = charIndexes[string.charAt(offset)];
                    if (drawStringShadows && !aBooleanArray349[k] && colour != 0)
                        method257(charIndex, x + 1, y, 0, abyte0, aBooleanArray349[k]);
                    if (drawStringShadows && !aBooleanArray349[k] && colour != 0)
                        method257(charIndex, x, y + 1, 0, abyte0, aBooleanArray349[k]);
                    method257(charIndex, x, y, colour, abyte0, aBooleanArray349[k]);
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

    private void method257(int i, int x, int y, int colour, byte abyte0[], boolean flag) {
        int i1 = x + abyte0[i + 5];
        int j1 = y - abyte0[i + 6];
        int k1 = abyte0[i + 3];
        int l1 = abyte0[i + 4];
        int i2 = abyte0[i] * 16384 + abyte0[i + 1] * 128 + abyte0[i + 2];
        int j2 = i1 + j1 * gameWindowWidth;
        int k2 = gameWindowWidth - k1;
        int l2 = 0;
        if (j1 < imageY) {
            int i3 = imageY - j1;
            l1 -= i3;
            j1 = imageY;
            i2 += i3 * k1;
            j2 += i3 * gameWindowWidth;
        }
        if (j1 + l1 >= imageHeight)
            l1 -= ((j1 + l1) - imageHeight) + 1;
        if (i1 < imageX) {
            int j3 = imageX - i1;
            k1 -= j3;
            i1 = imageX;
            i2 += j3;
            j2 += j3;
            l2 += j3;
            k2 += j3;
        }
        if (i1 + k1 >= imageWidth) {
            int k3 = ((i1 + k1) - imageWidth) + 1;
            k1 -= k3;
            l2 += k3;
            k2 += k3;
        }
        if (k1 > 0 && l1 > 0) {
            if (flag) {
                method259(imagePixelArray, abyte0, colour, i2, j2, k1, l1, k2, l2);
                return;
            }
            plotLetter(imagePixelArray, abyte0, colour, i2, j2, k1, l1, k2, l2);
        }
    }

    private void plotLetter(int ai[], byte abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
        try {
            int l1 = -(l >> 2);
            l = -(l & 3);
            for (int i2 = -i1; i2 < 0; i2++) {
                for (int j2 = l1; j2 < 0; j2++) {
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;
                }
                for (int k2 = l; k2 < 0; k2++)
                    if (abyte0[j++] != 0)
                        ai[k++] = i;
                    else
                        k++;

                k += j1;
                j += k1;
            }
            return;
        }
        catch (Exception exception) {
            System.out.println("plotletter: " + exception);
            exception.printStackTrace();
            return;
        }
    }

    private void method259(int ai[], byte abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
        for (int l1 = -i1; l1 < 0; l1++) {
            for (int i2 = -l; i2 < 0; i2++) {
                int j2 = abyte0[j++] & 0xff;
                if (j2 > 30) {
                    if (j2 >= 230) {
                        ai[k++] = i;
                    } else {
                        int k2 = ai[k];
                        ai[k++] = ((i & 0xff00ff) * j2 + (k2 & 0xff00ff) * (256 - j2) & 0xff00ff00) + ((i & 0xff00) * j2 + (k2 & 0xff00) * (256 - j2) & 0xff0000) >> 8;
                    }
                } else {
                    k++;
                }
            }

            k += j1;
            j += k1;
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
            return aByteArrayArray336[i][8] - 2;
        } else {
            return aByteArrayArray336[i][8] - 1;
        }
    }

    public int textWidth(String s, int i) {
        int j = 0;
        byte abyte0[] = aByteArrayArray336[i];
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

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) {
        return true;
    }

    public int gameWindowWidth;
    public int gameWindowHeight;
    public int gameWindowWidthUnused;
    public int gameWindowHeightUnused;
    ColorModel colourModel;
    public int imagePixelArray[];
    ImageConsumer imageConsumer;
    public Image image;
    private int imageY;
    private int imageHeight;
    private int imageX;
    private int imageWidth;
    public boolean f1Toggle;
    static byte aByteArrayArray336[][] = new byte[50][];
    static int charIndexes[];
    public boolean drawStringShadows;
    double sin256[];
    double cos256[];
    int anIntArray340[];
    int anIntArray341[];
    int anIntArray342[];
    int anIntArray343[];
    int anIntArray344[];
    int anIntArray345[];
    public static int anInt346;
    public static int anInt347;
    public static int anInt348;
    private static boolean aBooleanArray349[] = new boolean[12];
    private static int anInt350;
    private static byte aByteArray351[] = new byte[0x186a0];
    public static int anInt352;

    static {
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
}
