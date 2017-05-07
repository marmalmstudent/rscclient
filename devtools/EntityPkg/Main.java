package EntityPkg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import dbdev.CDConst;
import dbdev.FileOperations;

public class Main
{
	private static HashMap<String, String> modelNames;

	public static void main(String[] args)
	{
		/* For convertinc between entity in png format and spt format */
		try {
			Main.modelNames = FileOperations.readEntityHashMap(
					new File("devtools/dbdev/names/entity2"), ";");
			for (Entry<String, String> entry : Main.modelNames.entrySet())
			{
				NewEntity etty2 = new NewEntity(FileOperations.read(new File(CDConst.EntityDir+"newdat/"+entry.getKey())));
				FileOperations.writeImageAlpha(etty2.formatEntityPNG(), etty2.imageWidth(),
						etty2.imageHeight(),
						new File(CDConst.EntityDir+"newpng/"+entry.getKey()+".png"));
				NewEntity etty = new NewEntity(FileOperations.readImage(new File(CDConst.EntityDir+"newpng/"+entry.getKey()+".png")),
						entry.getKey().toUpperCase(), 3);
				FileOperations.writeImageAlpha(etty.formatEntityPNG(), etty.imageWidth(),
						etty.imageHeight(),
						new File(CDConst.EntityDir+"newpng/"+entry.getKey()+".png"));
				FileOperations.write(etty.formatEntityDat(), new File(CDConst.EntityDir+"newdat/"+entry.getKey()));
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		/* For assembling individual sprites into an entity
		Sprite[] sprites;
		try
		{
			Main.modelNames = FileOperations.readEntityHashMap(
					new File("devtools/dbdev/names/entity2"), ";");
			boolean hasAttack, hasFlip;
			for (Entry<String, String> entry : Main.modelNames.entrySet())
			{
				hasAttack = false;
				hasFlip = false;
				int nSprites = 15;
				if ((new File(CDConst.EntityDatDir+entry.getKey()+"a_0")).exists())
				{
					hasAttack = true;
					nSprites += 3;
				}
				if ((new File(CDConst.EntityDatDir+entry.getKey()+"f_0")).exists())
				{
					hasFlip = true;
					nSprites += 9;
				}
				sprites = new Sprite[nSprites];
				for (int i = 0; i < 15; ++i)
				{
					byte[] data = FileOperations.read(new File(CDConst.EntityDatDir+entry.getKey()+"_"+Integer.toString(i)));
					if (data != null)
						sprites[i] = new Sprite(data, 0x0);
				}
				if (hasAttack)
					for (int i = 0; i < 3; ++i)
					{
						byte[] data = FileOperations.read(new File(CDConst.EntityDatDir+entry.getKey()+"a_"+Integer.toString(i)));
						if (data != null)
							sprites[15 + i] = new Sprite(data, 0x0);
					}
				if (hasFlip)
					for (int i = 0; i < 9; ++i)
					{
						byte[] data = FileOperations.read(new File(CDConst.EntityDatDir+entry.getKey()+"f_"+Integer.toString(i)));
						if (data != null)
							sprites[15 + (hasAttack ? 3 : 0) + i] = new Sprite(data, 0x0);
					}
				int maxTotWidth = 0, maxTotHeight = 0;
				for (int i = 0; i < sprites.length; ++i)
				{
					if (sprites[i].getTotalWidth() > maxTotWidth)
						maxTotWidth = sprites[i].getTotalWidth();
					if (sprites[i].getTotalHeight() > maxTotHeight)
						maxTotHeight = sprites[i].getTotalHeight();
				}
				for (int i = 0; i < sprites.length; ++i)
				{
					if (sprites[i].getTotalWidth() < maxTotWidth)
					{
						sprites[i].setXShift(sprites[i].getXShift()
								+ (maxTotWidth - sprites[i].getTotalWidth())/2);
						sprites[i].setTotalWidth(maxTotWidth);
					}
					if (sprites[i].getTotalHeight() < maxTotHeight)
					{
						sprites[i].setYShift(sprites[i].getYShift()
								+ (maxTotHeight - sprites[i].getTotalHeight())/2);
						sprites[i].setTotalHeight(maxTotHeight);
					}
				}
				Entity ety = new Entity(sprites);
				FileOperations.writeImageAlpha(ety.formatPNG(), ety.imageWidth(),
						ety.imageHeight(),
						new File(CDConst.EntityDir+"entity/"+entry.getKey()+".png"));
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}*/
	}
}
