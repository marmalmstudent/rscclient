package entityhandling;

import java.io.*;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import entityhandling.defs.DoorDef;
import entityhandling.defs.ElevationDef;
import entityhandling.defs.GameObjectDef;
import entityhandling.defs.ItemDef;
import entityhandling.defs.NPCDef;
import entityhandling.defs.PrayerDef;
import entityhandling.defs.SpellDef;
import entityhandling.defs.TileDef;
import entityhandling.defs.extras.AnimationDef;
import entityhandling.defs.extras.ItemDropDef;
import entityhandling.defs.extras.TextureDef;

public class ParseXML
{
	//private String dbEntryType;
	private Document dom;
	private AnimationDef[] animDef;
	private DoorDef[] doorDef;
	private ElevationDef[] elevDef;
	private ItemDef[] itemDef;
	private NPCDef[] npcDef;
	private GameObjectDef[] objDef;
	private PrayerDef[] prayDef;
	private SpellDef[] spellDef;
	private TextureDef[] txtrDef;
	private TileDef[] tileDef;
	/*
	private static String[] fileNames = {"Animations", "Doors",
			"Elevation", "Items", "NPCs", "Objects", "Prayers",
			"Spells", "Textures", "Tiles"};*/
	private static String[] dbEntryHeader = {"AnimationDef",
			"DoorDef", "ElevationDef", "ItemDef", "NPCDef",
			"GameObjectDef", "PrayerDef", "SpellDef",
			"TextureDef", "TileDef"};
	//private int currentFile = -1;
	public static final int ANIMATION_DEF = 0, DOOR_DEF = 1,
			ELEVATION_DEF = 2, ITEM_DEF = 3, NPC_DEF = 4,
			GAME_OBJECT_DEF = 5, PRAYER_DEF = 6, SPELL_DEF = 7,
			TEXTURE_DEF = 8, TILE_DEF = 9;
	
	public ParseXML()
	{
	}

	public AnimationDef[] unpackAnimationDef(File file)
	{
		parseXmlFile(file);
		parseDocument(ANIMATION_DEF);
		return animDef;
	}
	public DoorDef[] unpackDoorDef(File file)
	{
		parseXmlFile(file);
		parseDocument(DOOR_DEF);
		return doorDef;
	}
	public ElevationDef[] unpackElevationDef(File file)
	{
		parseXmlFile(file);
		parseDocument(ELEVATION_DEF);
		return elevDef;
	}
	public ItemDef[] unpackItemDef(File file)
	{
		parseXmlFile(file);
		parseDocument(ITEM_DEF);
		return itemDef;
	}
	public NPCDef[] unpackNPCDef(File file)
	{
		parseXmlFile(file);
		parseDocument(NPC_DEF);
		return npcDef;
	}
	public GameObjectDef[] unpackGameObjectDef(File file)
	{
		parseXmlFile(file);
		parseDocument(GAME_OBJECT_DEF);
		return objDef;
	}
	public PrayerDef[] unpackPrayerDef(File file)
	{
		parseXmlFile(file);
		parseDocument(PRAYER_DEF);
		return prayDef;
	}
	public SpellDef[] unpackSpellDef(File file)
	{
		parseXmlFile(file);
		parseDocument(SPELL_DEF);
		return spellDef;
	}
	public TextureDef[] unpackTextureDef(File file)
	{
		parseXmlFile(file);
		parseDocument(TEXTURE_DEF);
		return txtrDef;
	}
	public TileDef[] unpackTileDef(File file)
	{
		parseXmlFile(file);
		parseDocument(TILE_DEF);
		return tileDef;
	}
	
	private void parseXmlFile(File file)
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(file);
		}
		catch(ParserConfigurationException pce)
		{
			pce.printStackTrace();
		}
		catch(SAXException se)
		{
			se.printStackTrace();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	private void parseDocument(int dbType)
	{
		//get the root elememt
		dom.getDocumentElement().normalize();
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName(dbEntryHeader[dbType]);
		if(nl != null && nl.getLength() > 0)
		{
			switch(dbType)
			{
			case ANIMATION_DEF: // animations
				animDef = new AnimationDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					animDef[i] = getAnimationDef(el);
				}
				break;
			case DOOR_DEF:
				doorDef = new DoorDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					doorDef[i] = getDoorDef(el);
				}
				break;
			case ELEVATION_DEF:
				elevDef = new ElevationDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					elevDef[i] = getElevationDef(el);
				}
				break;
			case ITEM_DEF:
				itemDef = new ItemDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					itemDef[i] = getItemDef(el);
				}
				break;
			case NPC_DEF:
				npcDef = new NPCDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					npcDef[i] = getNPCDef(el);
				}
				break;
			case GAME_OBJECT_DEF:
				objDef = new GameObjectDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					objDef[i] = getGameObjectDef(el);
				}
				break;
			case PRAYER_DEF:
				prayDef = new PrayerDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					prayDef[i] = getPrayerDef(el);
				}
				break;
			case SPELL_DEF:
				spellDef = new SpellDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					spellDef[i] = getSpellDef(el);
				}
				break;
			case TEXTURE_DEF:
				txtrDef = new TextureDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					txtrDef[i] = getTextureDef(el);
				}
				break;
			case TILE_DEF:
				tileDef = new TileDef[nl.getLength()];
				for(int i = 0 ; i < nl.getLength();i++)
				{
					//get the db entry element
					Element el = (Element)nl.item(i);
					//get the db entry object
					tileDef[i] = getTileDef(el);
				}
				break;
			}
		}
	}
	
	private AnimationDef getAnimationDef(Element el)
	{
		AnimationDef ad = new AnimationDef();
		ad.name = getTextValue(el, "name");
		ad.charColour = getIntValue(el, "charColour");
		ad.genderModel = getIntValue(el, "genderModel");
		ad.hasAttack = getBoolValue(el, "hasA");
		ad.hasFlip = getBoolValue(el, "hasF");
		ad.number = getIntValue(el, "number");
		return ad;
	}
	
	private DoorDef getDoorDef(Element el)
	{
		DoorDef dd = new DoorDef();
		dd.name = getTextValue(el, "name");
		dd.description = getTextValue(el, "description");
		dd.command1 = getTextValue(el, "command1");
		dd.command2 = getTextValue(el, "command2");
		dd.modelVar1 = getIntValue(el, "modelVar1");
		dd.modelVar2 = getIntValue(el, "modelVar2");
		dd.modelVar3 = getIntValue(el, "modelVar3");
		dd.doorType = getIntValue(el, "doorType");
		dd.unknown = getIntValue(el, "unknown");
		return dd;
	}
	
	private ElevationDef getElevationDef(Element el)
	{
		ElevationDef ed = new ElevationDef();
		ed.unknown1 = getIntValue(el, "unknown1");
		ed.unknown2 = getIntValue(el, "unknown2");
		return ed;
	}
	
	private ItemDef getItemDef(Element el)
	{
		ItemDef id = new ItemDef();
		id.name = getTextValue(el, "name");
		id.description = getTextValue(el, "description");
		id.command = getTextValue(el, "command");
		id.sprite = getIntValue(el, "sprite");
		id.basePrice = getIntValue(el, "basePrice");
		id.stackable = getBoolValue(el, "stackable");
		id.wieldable = getBoolValue(el, "wieldable");
		String s = getTextValue(el, "entity");
		id.entity = s.equalsIgnoreCase("null") ? null : s;
		id.pictureMask = getIntValue(el, "pictureMask");
		return id;
	}
	
	private NPCDef getNPCDef(Element el)
	{
		NPCDef nd = new NPCDef();
		nd.name = getTextValue(el, "name");
		nd.description = getTextValue(el, "description");
		nd.command = getTextValue(el, "command");
		nd.attack= getIntValue(el, "attack");
		nd.strength = getIntValue(el, "strength");
		nd.hits = getIntValue(el, "strength");
		nd.defense = getIntValue(el, "strength");
		nd.attackable = getBoolValue(el, "attackable");
		nd.aggressive = getBoolValue(el, "aggressive");
		nd.respawnTime = getIntValue(el, "respawnTime");
		nd.sprites = getIntArrayValue(el, "sprites");
		nd.hairColour = getIntValue(el, "hairColour");
		nd.topColour = getIntValue(el, "topColour");
		nd.bottomColour = getIntValue(el, "bottomColour");
		nd.skinColour = getIntValue(el, "skinColour");
		nd.camera1 = getIntValue(el, "camera1");
		nd.camera2 = getIntValue(el, "camera2");
		nd.walkModel = getIntValue(el, "walkModel");
		nd.combatModel = getIntValue(el, "combatModel");
		nd.combatSprite = getIntValue(el, "combatSprite");
		nd.drops = getItemDropDefValue(el, "drops");
		return nd;
	}
	
	private GameObjectDef getGameObjectDef(Element el)
	{
		GameObjectDef gd = new GameObjectDef();
		gd.name = getTextValue(el, "name");
		gd.description = getTextValue(el, "description");
		gd.command1 = getTextValue(el, "command1");
		gd.command2 = getTextValue(el, "command2");
		gd.type = getIntValue(el, "type");
		gd.width = getIntValue(el, "width");
		gd.height = getIntValue(el, "height");
		gd.groundItemVar = getIntValue(el, "groundItemVar");
		gd.objectModel = getTextValue(el, "objectModel");
		return gd;
	}
	
	private PrayerDef getPrayerDef(Element el)
	{
		PrayerDef pd = new PrayerDef();
		pd.reqLevel = getIntValue(el, "reqLevel");
		pd.drainRate = getIntValue(el, "drainRate");
		pd.name = getTextValue(el, "name");
		pd.description = getTextValue(el, "description");
		return pd;
	}
	
	private SpellDef getSpellDef(Element el)
	{
		SpellDef sd = new SpellDef();
		sd.reqLevel = getIntValue(el, "reqLevel");
		sd.type = getIntValue(el, "type");
		sd.runeCount = getIntValue(el, "runeCount");
		sd.requiredRunes = getHashValue(el, "requiredRunes");
		sd.exp = getIntValue(el, "exp");
		sd.name = getTextValue(el, "name");
		sd.description = getTextValue(el, "description");
		return sd;
	}
	
	private TextureDef getTextureDef(Element el)
	{
		TextureDef td = new TextureDef();
		td.dataName = getTextValue(el, "dataName");
		td.animationName = getTextValue(el, "animationName");
		return td;
	}
	
	private TileDef getTileDef(Element el)
	{
		TileDef td = new TileDef();
		td.colour = getIntValue(el, "colour");
		td.unknown = getIntValue(el, "unknown");
		td.objectType = getIntValue(el, "objectType");
		return td;
	}
	
	private HashMap<Integer, Integer> getHashValue(Element spellDef, String tagName)
	{
		HashMap<Integer, Integer> returnHash = new HashMap<Integer, Integer>();
		// get requiredRunes element from SpellDef
		Element reqRunes = getElementOfElement(spellDef, tagName, 0);
		if (reqRunes != null)
		{ // requiredRunes exist
			// list of all requiredRunes entries
			NodeList lstObEntries = reqRunes.getElementsByTagName("entry");
			int nbrReqRunes = lstObEntries.getLength();
			for (int i = 0; i < nbrReqRunes; i++)
			{ // loop through all requiredRunes entries
				// an entry element
				Element entryEle = (Element)lstObEntries.item(i);
				if (entryEle != null)
				{ // entry number i exists
					// list of all int entries in current entry element
					NodeList nl = entryEle.getElementsByTagName("int");
					if(nl != null && nl.getLength() > 1)
					{
						Element runeId = (Element)nl.item(0);
						Element runeAmmount = (Element)nl.item(1);
						if (runeId.getFirstChild() != null
								&& runeAmmount.getFirstChild() != null)
						{ // both runeId and runeAmmount have values
							try
							{
								int rune = Integer.parseInt(runeId.getFirstChild().getNodeValue());
								int runeAmnt = Integer.parseInt(runeAmmount.getFirstChild().getNodeValue());
								returnHash.put(rune, runeAmnt);
							}
							catch (NumberFormatException nfe)
							{
								// do not add rune
							}
						}
					}
				}
				
			}
		}
		return returnHash;
	}
	

	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName)
	{
		String textVal = "";
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			if (el.getFirstChild() != null)
				textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int[] getIntArrayValue(Element ele, String tagName)
	{
		// drops
		Element el = getElementOfElement(ele, tagName, 0);
		if (el != null)
		{
			NodeList nl = el.getElementsByTagName("int");
			if(nl != null && nl.getLength() > 0)
			{
				int nbrEntries = nl.getLength();
				int[] intArray = new int[nbrEntries];
				for (int i = 0; i < nl.getLength(); i++)
				{
					Element elm = (Element)nl.item(i);
					if (elm.getFirstChild() != null)
					{
						try
						{
							intArray[i] = Integer.parseInt(elm.getFirstChild().getNodeValue());
						}
						catch (NumberFormatException nfe)
						{
							intArray[i] = -1;
						}
					}
				}
				return intArray;
			}
		}
		return null;
	}
	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private boolean getBoolValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Boolean.parseBoolean(getTextValue(ele,tagName));
	}
	
	/**
	 * If an element itself contains elements this method
	 * is a quick way of getting that element.
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private Element getElementOfElement(Element ele, String tagName, int idx)
	{
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0 && idx < nl.getLength())
		{
			return (Element)nl.item(idx);
		}
		return null;
	}
	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private ItemDropDef[] getItemDropDefValue(Element ele, String tagName)
	{
		// drops
		Element el = getElementOfElement(ele, tagName, 0);
		if (el != null)
		{
			// list of all ItemDropDef entries
			NodeList ndlst = el.getElementsByTagName("ItemDropDef");
			int nbrDrops = ndlst.getLength();
			ItemDropDef[] iddArray = new ItemDropDef[nbrDrops];
			for (int i = 0; i < nbrDrops; i++)
			{ // loop through all ItemDropDef entries
				// ItemDropDef number i
				Element elm = (Element)ndlst.item(i);
				if (elm != null)
				{ // ItemDropDef number i exists
					iddArray[i] = new ItemDropDef();
					iddArray[i].id = getIntValue(elm, "id");
					iddArray[i].amount = getIntValue(elm, "amount");
					iddArray[i].weight = getIntValue(elm, "weight");
				}
			}
			return iddArray;
		}
		return null;
	}
	
	/*
	public static void main(String[] args){
		//create an instance
		ParseXML dpe = new ParseXML();
		dpe.unpackAnimationDef(new File("src/org/conf/client/Animations"));
		dpe.unpackDoorDef(new File("src/org/conf/client/Doors"));
		dpe.unpackElevationDef(new File("src/org/conf/client/Elevation"));
		dpe.unpackItemDef(new File("src/org/conf/client/Items"));
		dpe.unpackNPCDef(new File("src/org/conf/client/NPCs"));
		dpe.unpackGameObjectDef(new File("src/org/conf/client/Objects"));
		dpe.unpackPrayerDef(new File("src/org/conf/client/Prayers"));
		dpe.unpackSpellDef(new File("src/org/conf/client/Spells"));
		dpe.unpackTextureDef(new File("src/org/conf/client/Textures"));
		dpe.unpackTileDef(new File("src/org/conf/client/Tiles"));
	}*/
	

	public static final String dataTypes[] = {"AnimationDef", "DoorDef", "ElevationDef",
			"ItemDef", "NPCDef", "GameObjectDef", "PrayerDef", "SpellDef", "TextureDef",
			"TileDef"};
	public static final String[] AnimationsDef = {"name", "charColour",
    		"genderModel", "hasA", "hasF", "number"};
	public static final String[] DoorDef = {"name", "description", "command1", "command2",
    		"modelVar1", "modelVar2", "modelVar3", "doorType", "unknown"};
	public static final String[] ElevationDef = {"unknown1", "unknown2"};
	public static final String[] ItemDef = {"name", "description", "command", "sprite",
    		"basePrice", "stackable", "wieldable", "pictureMask"};
	public static final String[] NPCDef = {"name", "description", "command", "attack",
    		"strength", "hits", "defense", "attackable", "aggressive", "respawnTime",
    		"sprites", "hairColour", "topColour", "bottomColour", "skinColour", "camera1",
    		"camera2", "walkModel", "combatModel", "combatSprite", "drops"};
	public static final String[][] NPCDefArray = {{null}, {null}, {null}, {null},
			{null}, {null}, {null}, {null},
			{null}, {null}, {"int"},
		{null}, {null}, {null}, {null}, {null}, {null},
		{null}, {null}, {null}, {"ItemDropDef"}};
	public static final String[] ItemDropDef = {"id", "amount", "weight"};
	public static final String[] GameObjectDef = {"name", "description" ,"command1", "command2",
			"type", "width", "height", "groundItemVar", "objectModel"};
	public static final String[] PrayerDef = {"reqLevel", "drainRate", "name", "description"};
	public static final String[] SpellDef = {"reqLevel", "type", "runeCount", "requiredRunes",
			"exp", "name", "description"};
	public static final String[][] SpellDefArray = {{null}, {null}, {null}, {"entry"}, {null},
			{null}, {null}};
	public static final String[][][] SpellDefArrayArray = {{{null}}, {{null}}, {{null}},
			{{"int"}}, {{null}}, {{null}}, {{null}}};
	public static final String[] TextureDef = {"dataName", "animationName"};
	public static final String[] TileDef = {"colour", "unknown", "objectType"};
}
