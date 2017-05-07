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
		/*
		int offset = 0;
		String str = "Hello World!\0";
		byte[] bt = str.getBytes();
		while (bt[offset++] != 0);
		System.out.println(offset);
		byte[] test = new byte[offset-1];
		for (int i = 0; i < offset-1; i++)
			test[i] = bt[i];
		String testStr = new String(test);
		System.out.println(testStr+","+testStr.length());
		System.out.println("Hello World!\0");
		System.out.println("Hello World!\0".length());
		System.out.println("Hello World!");
		System.out.println("Hello World!".length());
		*/
		try {
		NewEntity etty = new NewEntity(FileOperations.readImage(new File(CDConst.EntityDir + "entity/bear.png")));

		Entity ety = new Entity(etty.getSprites());
		FileOperations.writeImageAlpha(ety.formatPNG(), ety.imageWidth(),
				ety.imageHeight(),
				new File(CDConst.EntityDir+"bear.png"));
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		/*
		Sprite[] sprites;
		try
		{
			Main.modelNames = FileOperations.readEntityHashMap(
					new File("devtools/dbdev/names/entity2"), ";");
			System.out.println(Main.modelNames.size());
			for (Entry<String, String> entry : Main.modelNames.entrySet())
			{
				sprites = new Sprite[Integer.parseInt(entry.getValue())+1];
				for (int i = 0; i < sprites.length; ++i)
				{
					byte[] data = FileOperations.read(new File(CDConst.EntityDatDir+entry.getKey()+"_"+Integer.toString(i)));
					if (data != null)
						sprites[i] = new Sprite(data, 0x0);
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
