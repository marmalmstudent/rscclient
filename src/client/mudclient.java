package client;

import client.UI.InGameButton;
import client.UI.InGameGrid;
import client.UI.menus.MenuRightClick;
import client.UI.panels.AbuseWindow;
import client.UI.panels.BankPanel;
import client.UI.panels.FriendsPanel;
import client.UI.panels.InventoryPanel;
import client.UI.panels.MagicPanel;
import client.UI.panels.OptionsPanel;
import client.UI.panels.PlayerInfoPanel;
import client.UI.panels.TradeConfirmPanel;
import client.UI.panels.TradePanel;
import client.util.Config;
import client.util.DataConversions;
import client.util.misc;
import entityhandling.EntityHandler;
import entityhandling.defs.ItemDef;
import entityhandling.defs.NPCDef;
import model.Item;
import model.Sprite;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* 80 columns
--------------------------------------------------------------------------------
 */
public class mudclient extends GameWindowMiddleMan
{
	private static final long serialVersionUID = 1L;
	public static final int SPRITE_MEDIA_START = 2000;
	public static final int SPRITE_UTIL_START = 2100;
	public static final int SPRITE_ITEM_START = 2150;
	public static final int SPRITE_LOGO_START = 3150;
	public static final int SPRITE_PROJECTILE_START = 3160;
	public static final int SPRITE_TEXTURE_START = 3220;

	/* Custom variables */
	public static int itemSlotWidth = 49;
	public static int itemSlotHeight = 34;

	private long startTime = 0;
	private long serverStartTime = 0;
	private String lastMessage = "";
	private int fatigue;
	private String serverLocation = "";
	private String localhost;
	private int prayerMenuIndex = 0;
	private int magicMenuIndex = 0;
	private boolean showRoof = true;
	private boolean autoScreenshot = true;
	private boolean freeCamera = false;
	private long expGained = 0;
	private boolean hasWorldInfo = false;
	private boolean recording = false;
	private LinkedList<BufferedImage> frames = new LinkedList<BufferedImage>();
	private long lastFrame = 0;

	/* Here are some variable that are added to make the rsc client work with
	 * the mopar wrapper */
	public boolean graphicsEnabled;
	public static boolean showhp;
	public static boolean pkingmap;
	public static boolean cameratoggle;
	public static boolean maplock;
	public static int mapface;
	public static int portNumber = 43594;
	public static boolean coords;
	public static int zoom;
	public static int fwdbwd;
	public static int lftrit;
	public String loginMessage1;
	public String loginMessage2;
	public static mudclient mc;
	/* EOF */

	public static void main(String[] args) throws Exception
	{
		Config.initConfig();
		GameWindowMiddleMan.clientVersion = 25;
		mc = new mudclient();
		mc.appletMode = false;
		mc.createWindow(mc, mc.windowWidth + 4, mc.windowHeight + 32,
				"TestServer v" + GameWindowMiddleMan.clientVersion, false);
	}

	/**
	 * Not a very good way to get the dimensions of the window but it works
	 * @return
	 */
	public static int getWindowHeight()
	{
		return mc.windowHeight;
	}

	/**
	 * Not a very good way to get the dimensions of the window but it works
	 * @return
	 */
	public static int getWindowWidth()
	{
		return mc.windowWidth;
	}

	public boolean getFreeCamera()
	{
		return freeCamera;
	}

	/**
	 * Formats a packet and cleans up necessary variables.
	 * @param packetID The id of the packet.
	 */
	private void formatPacket(int packetID, int id, int amount)
	{
		switch(packetID)
		{
		case 42:
			super.streamClass.createPacket(42);
			super.streamClass.addByte(id);
			super.streamClass.formatPacket();
			break;
		case 48: // close bank
		super.streamClass.createPacket(48);
		super.streamClass.formatPacket();
		showBank = false;
		break;
		case 53: // confirm trade
			tradeConfirmAccepted = true;
			super.streamClass.createPacket(53);
			super.streamClass.formatPacket();
			break;
		case 56:
			super.streamClass.createPacket(56);
			super.streamClass.addByte(id);
			super.streamClass.formatPacket();
			prayerOn[id] = true;
			playSound("prayeron");
			break;
		case 70:
			super.streamClass.createPacket(70);
			super.streamClass.addByte(tradeMyItemCount);
			for (int i = 0; i < tradeMyItemCount; i++)
			{
				super.streamClass.add2ByteInt(tradeMyItemsI[i].id);
				super.streamClass.add4ByteInt(tradeMyItemsI[i].amount);
			}
			super.streamClass.formatPacket();
			tradeOtherAccepted = false;
			tradeWeAccepted = false;
			break;
		case 123:
			super.streamClass.createPacket(123);
			super.streamClass.addByte(duelMyItemCount);
			for (int i = 0; i < duelMyItemCount; i++)
			{
				super.streamClass.add2ByteInt(duelMyItemsI[i].id);
				super.streamClass.add4ByteInt(duelMyItemsI[i].amount);
			}
			super.streamClass.formatPacket();
			duelOpponentAccepted = false;
			duelMyAccepted = false;
			break;
		case 157: // change settings in settings menu
			super.streamClass.createPacket(157);
			super.streamClass.addByte(id);
			super.streamClass.addByte(amount);
			super.streamClass.formatPacket();
			break;
		case 183: // bank withdraw
			super.streamClass.createPacket(183);
			super.streamClass.add2ByteInt(id);
			super.streamClass.add4ByteInt(amount);
			super.streamClass.formatPacket();
			break;
		case 198: // bank deposit
			super.streamClass.createPacket(198);
			super.streamClass.add2ByteInt(id);
			super.streamClass.add4ByteInt(amount);
			super.streamClass.formatPacket();
			break;
		case 211: // trade accept button
			tradeWeAccepted = true;
			super.streamClass.createPacket(211);
			super.streamClass.formatPacket();
			break;
		case 216: // trade decline
			showTradeWindow = false;
			super.streamClass.createPacket(216);
			super.streamClass.formatPacket();
			break;
		case 218: // accept character remake?
			super.streamClass.createPacket(218);
			super.streamClass.addByte(chrHeadGender);
			super.streamClass.addByte(chrHeadType);
			super.streamClass.addByte(chrBodyGender);
			super.streamClass.addByte(character2Colour);
			super.streamClass.addByte(chrHairClr);
			super.streamClass.addByte(chrTopClr);
			super.streamClass.addByte(chrBottomClr);
			super.streamClass.addByte(chrSkinClr);
			super.streamClass.formatPacket();
			gameGraphics.resetImagePixels(0);
			showCharacterLookScreen = false;
			break;
		case 248:  // switch prayer off
			super.streamClass.createPacket(248);
			super.streamClass.addByte(id);
			super.streamClass.formatPacket();
			prayerOn[id] = false;
			playSound("prayeroff");
			break;
		}

	}

	/**
	 * Checks for any errors when requesting to add items to the trade.
	 * @param id Item id
	 * @param amount Item amount
	 * @param offerType Type of offer being made; 0 for trade, 1 for duel.
	 * @return True if no errors were found. False if errors were found.
	 */
	private boolean anyOfferErrors(int id, int amount, int offerType)
	{
		if (offerType == 0 && tradeMyItemCount >= 12)
		{
			displayMessage("@cya@Your trade offer is currently full", 3, 0);
			return false;
		}
		if (offerType == 1 && duelMyItemCount >= 12)
		{
			displayMessage("@cya@Your duel offer is currently full", 3, 0);
			return false;
		}
		if (inventoryCount(id) < amount)
		{
			displayMessage("@cya@You do not have that many"
					+ EntityHandler.getItemDef(id).getName()
					+ " to offer", 3, 0);
			return false;
		}
		if (!EntityHandler.getItemDef(id).isStackable()
				&& amount > 1)
		{
			displayMessage("@cya@You can only offer 1 non stackable at a time",
					3, 0);
			return false;
		}
		return true;
	}

	/**
	 * Handles adding an item to the trade list of the player items currently offered.
	 * If an error occurs (such as invalid amount or id) no items are added.
	 * @param id Item id.
	 * @param amount Item amount.
	 */
	private void handleTradeOfferCommand(int id, int amount)
	{
		if (anyOfferErrors(id, amount, 0))
			return;
		boolean addNewItem = true;
		for (int i = 0; i < tradeMyItemCount; ++i)
		{
			if (tradeMyItemsI[i].id != id)
				continue;
			if (!EntityHandler.getItemDef(id).isStackable())
				break;
			if (inventoryCount(id) < (tradeMyItemsI[i].amount + amount))
			{
				displayMessage("@cya@You do not have that many"
						+ EntityHandler.getItemDef(id).getName()
						+ " to offer", 3, 0);
				return;
			}
			tradeMyItemsI[i].amount += amount;
			addNewItem = false;
			break;
		}
		if (addNewItem)
		{
			tradeMyItemsI[tradeMyItemCount++] = new Item(id, amount);
		}
		formatPacket(70, -1, -1);
	}

	/**
	 * Handles adding an item to the duel list of the player items currently offered.
	 * If an error occurs (such as invalid amount or id) no items are added.
	 * @param id Item id.
	 * @param amount Item amount.
	 */
	private void handleDuelOfferCommand(int id, int amount)
	{
		if (anyOfferErrors(id, amount, 1))
			return;
		boolean addNewItem = true;
		for (int i = 0; i < duelMyItemCount; i++)
		{
			if (duelMyItemsI[i].id != id)
				continue;
			if (!EntityHandler.getItemDef(id).isStackable())
				break;
			if (inventoryCount(id) < (duelMyItemsI[i].amount + amount))
			{
				displayMessage("@cya@You do not have that many"
						+ EntityHandler.getItemDef(id).getName()
						+ " to offer", 3, 0);
				return;
			}
			duelMyItemsI[i].amount += amount;
			addNewItem = false;
			break;
		}
		if (addNewItem)
			duelMyItemsI[duelMyItemCount++] = new Item(id, amount);
		formatPacket(123, -1, -1);
	}

	/**
	 * Passes an offer command to the relevant function depending on what offer window
	 * is open.
	 * @param id Item id.
	 * @param amount Item amount.
	 * @return
	 */
	private boolean handleOfferCommand(int id, int amount)
	{
		if (showTradeWindow)
			handleTradeOfferCommand(id, amount);
		else if (showDuelWindow)
			handleDuelOfferCommand(id, amount);
		else {
			displayMessage("@cya@There is nothing to offer to.", 3, 0);
		}
		return false;
	}

	/**
	 * Takes a command, formats it and passes it to the relevant function.
	 * The command must be on the form:
	 * 
	 * command args0 args1 args2 ... argsN
	 * 
	 * @param s The command.
	 * @return true if the input string matches a valid command
	 */
	private boolean handleCommand(String s)
	{
		String[] cmd = s.split(" ");
		if (cmd[0].equalsIgnoreCase("offer") && cmd.length > 3)
		{
			int id, amount;
			try
			{
				id = Integer.parseInt(cmd[1]);
				amount = Integer.parseInt(cmd[2]);
			} catch (NumberFormatException nfe)
			{
				displayMessage("@cya@Arguments must be integers!", 3, 0);
				return true;
			}
			return handleOfferCommand(id, amount);
		}
		return false;
	}

	/**
	 * Returns the game image. Used to present the game on the screen and take
	 * creenshots.
	 * @return
	 * @throws IOException
	 */
	private BufferedImage getImage() throws IOException
	{
		BufferedImage bufferedImage = new BufferedImage(windowWidth,
				windowHeight + 11, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(gameGraphics.image, 0, 0, this);
		g2d.dispose();
		return bufferedImage;
	}

	/**
	 * Takes a screenshot and saves it.
	 * @param verbose True if additional information about the process should be
	 * printed.
	 * @return True if a screenshot was successfully taken.
	 */
	private boolean takeScreenshot(boolean verbose)
	{
		try
		{
			File file = misc.getEmptyFile(false, currentUser);
			ImageIO.write(getImage(), "png", file);
			if (verbose)
				handleServerMessage("Screenshot saved as " + file.getName() + ".");
			return true;
		}
		catch (IOException e)
		{
			if (verbose)
				handleServerMessage("Error saving screenshot.");
			return false;
		}
	}

	final void method45(int i, int j, int k, int l, int i1, int j1, int k1)
	{
		Mob mob = npcArray[i1];
		int l1 = mob.currentSprite + (cameraZRot + 16) / 32 & 7;
		boolean flag = false;
		int i2 = l1;
		if (i2 == 5) {
			i2 = 3;
			flag = true;
		} else if (i2 == 6) {
			i2 = 2;
			flag = true;
		} else if (i2 == 7) {
			i2 = 1;
			flag = true;
		}
		int j2 = i2 * 3 + walkModel[(mob.stepCount
				/ EntityHandler.getNpcDef(mob.type).getWalkModel()) % 4];
		if (mob.currentSprite == 8) {
			i2 = 5;
			l1 = 2;
			flag = false;
			i -= (EntityHandler.getNpcDef(mob.type).getCombatSprite() * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray1[(loginTimer
					/ (EntityHandler.getNpcDef(mob.type).getCombatModel() - 1)) % 8];
		} else if (mob.currentSprite == 9) {
			i2 = 5;
			l1 = 2;
			flag = true;
			i += (EntityHandler.getNpcDef(mob.type).getCombatSprite() * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray2[(loginTimer
					/ EntityHandler.getNpcDef(mob.type).getCombatModel()) % 8];
		}
		for (int k2 = 0; k2 < 12; k2++)
		{ // draw walking npcs
			int l2 = npcAnimationArray[l1][k2];
			int k3 = EntityHandler.getNpcDef(mob.type).getSprite(l2);
			if (k3 >= 0) {
				int i4 = 0;
				int j4 = 0;
				int k4 = j2;
				if (flag && i2 >= 1 && i2 <= 3
						&& EntityHandler.getAnimationDef(k3).hasFlip())
					k4 += 15;
				if (i2 != 5 || EntityHandler.getAnimationDef(k3).hasAttack()) {
					int l4 = k4 + EntityHandler.getAnimationDef(k3).getNumber();
					i4 = (i4 * k) / ((GameImage) (gameGraphics)).sprites[l4].getTotalWidth();
					j4 = (j4 * l) / ((GameImage) (gameGraphics)).sprites[l4].getTotalHeight();
					int i5 = (k * ((GameImage) (gameGraphics)).sprites[l4].getTotalWidth()) / ((GameImage) (gameGraphics)).sprites[EntityHandler.getAnimationDef(k3).getNumber()].getTotalWidth();
					i4 -= (i5 - k) / 2;
					int colour = EntityHandler.getAnimationDef(k3).getCharColour();
					int skinColour = 0;
					if (colour == 1) {
						colour = EntityHandler.getNpcDef(mob.type).getHairColour();
						skinColour = EntityHandler.getNpcDef(mob.type).getSkinColour();
					} else if (colour == 2) {
						colour = EntityHandler.getNpcDef(mob.type).getTopColour();
						skinColour = EntityHandler.getNpcDef(mob.type).getSkinColour();
					} else if (colour == 3) {
						colour = EntityHandler.getNpcDef(mob.type).getBottomColour();
						skinColour = EntityHandler.getNpcDef(mob.type).getSkinColour();
					}
					gameGraphics.spriteClip4(i + i4, j + j4, i5, l, l4, colour, skinColour, j1, flag);
				}
			}
		}

		if (mob.lastMessageTimeout > 0) {
			mobMsgWidth[mobMessageCount] = gameGraphics.textWidth(mob.lastMessage, 1) / 2;
			if (mobMsgWidth[mobMessageCount] > 150)
				mobMsgWidth[mobMessageCount] = 150;
			mobMsgHeight[mobMessageCount] = (gameGraphics.textWidth(mob.lastMessage, 1) / 300) * gameGraphics.messageFontHeight(1);
			mobMsgX[mobMessageCount] = i + k / 2;
			mobMsgY[mobMessageCount] = j;
			mobMsg[mobMessageCount++] = mob.lastMessage;
		}
		if (mob.currentSprite == 8 || mob.currentSprite == 9 || mob.combatTimer != 0) {
			if (mob.combatTimer > 0) {
				int i3 = i;
				if (mob.currentSprite == 8)
					i3 -= (20 * k1) / 100;
				else if (mob.currentSprite == 9)
					i3 += (20 * k1) / 100;
				int l3 = (mob.hitPointsCurrent * 30) / mob.hitPointsBase;
				anIntArray786[anInt718] = i3 + k / 2;
				anIntArray787[anInt718] = j;
				anIntArray788[anInt718++] = l3;
			}
			if (mob.combatTimer > 150) {
				int j3 = i;
				if (mob.currentSprite == 8)
					j3 -= (10 * k1) / 100;
				else if (mob.currentSprite == 9)
					j3 += (10 * k1) / 100;
				gameGraphics.drawPicture((j3 + k / 2) - 12, (j + l / 2) - 12, SPRITE_MEDIA_START + 12);
				gameGraphics.drawText(String.valueOf(mob.anInt164), (j3 + k / 2) - 1, j + l / 2 + 5, 3, 0xffffff);
			}
		}
	}

	/**
	 * Draws the character creation/look screen
	 */
	private final void drawCharacterLookScreen()
	{
		chrDesignMenu.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if (chrDesignMenu.hasActivated(chrDesignHeadBtnLeft))
			do {
				chrHeadType = ((chrHeadType - 1) + EntityHandler.animationCount())
						% EntityHandler.animationCount();
			} while ((EntityHandler.getAnimationDef(chrHeadType).getGenderModel() & 3) != 1
					|| (EntityHandler.getAnimationDef(chrHeadType).getGenderModel() & 4 * chrHeadGender) == 0);
		else if (chrDesignMenu.hasActivated(chrDesignHeadBtnRight))
			do {
				chrHeadType = (chrHeadType + 1) % EntityHandler.animationCount();
			} while ((EntityHandler.getAnimationDef(chrHeadType).getGenderModel() & 3) != 1
					|| (EntityHandler.getAnimationDef(chrHeadType).getGenderModel() & 4 * chrHeadGender) == 0);
		if (chrDesignMenu.hasActivated(chrDesignGenderBtnLeft)
				|| chrDesignMenu.hasActivated(chrDesignGenderBtnRight))
		{
			for (chrHeadGender = 3 - chrHeadGender;
					(EntityHandler.getAnimationDef(chrHeadType).getGenderModel() & 3) != 1
							|| (EntityHandler.getAnimationDef(chrHeadType).getGenderModel() & 4 * chrHeadGender) == 0;
					chrHeadType = (chrHeadType + 1) % EntityHandler.animationCount());
			for (; (EntityHandler.getAnimationDef(chrBodyGender).getGenderModel() & 3) != 2
					|| (EntityHandler.getAnimationDef(chrBodyGender).getGenderModel() & 4 * chrHeadGender) == 0;
					chrBodyGender = (chrBodyGender + 1) % EntityHandler.animationCount());
		}
		if (chrDesignMenu.hasActivated(chrDesignHairClrBtnLeft))
			chrHairClr = (chrHairClr-1 + chrHairClrs.length)
			% chrHairClrs.length;
		else if (chrDesignMenu.hasActivated(chrDesignHairClrBtnRight))
			chrHairClr = (chrHairClr + 1) % chrHairClrs.length;
		if (chrDesignMenu.hasActivated(chrDesignTopClrBtnLeft))
			chrTopClr = ((chrTopClr - 1) + chrTopBottomClrs.length)
			% chrTopBottomClrs.length;
		else if (chrDesignMenu.hasActivated(chrDesignTopClrBtnRight))
			chrTopClr = (chrTopClr + 1) % chrTopBottomClrs.length;
		if (chrDesignMenu.hasActivated(chrDesignSkinClrBtnLeft))
			chrSkinClr = ((chrSkinClr - 1) + chrSkinClrs.length)
			% chrSkinClrs.length;
		else if (chrDesignMenu.hasActivated(chrDesignSkinClrBtnRight))
			chrSkinClr = (chrSkinClr + 1) % chrSkinClrs.length;
		if (chrDesignMenu.hasActivated(chrDesignBottomClrBtnLeft))
			chrBottomClr = ((chrBottomClr - 1) + chrTopBottomClrs.length)
			% chrTopBottomClrs.length;
		else if (chrDesignMenu.hasActivated(chrDesignBottomClrBtnRight))
			chrBottomClr = (chrBottomClr + 1) % chrTopBottomClrs.length;
		if (chrDesignMenu.hasActivated(characterDesignAcceptButton))
			formatPacket(218, -1, -1);
	}

	/**
	 * Finds the number of occurences of an item id in the player's inventory.
	 * Note: A stack of N items counts as N occurences.
	 * @param reqID Item id.
	 * @return The number of occurences of the item in the player's inventory.
	 */
	protected final int inventoryCount(int reqID)
	{
		int amount = 0;
		for (int index = 0; index < inventoryCount; index++)
		{
			if (inventory[index].id == reqID)
				if (!EntityHandler.getItemDef(reqID).isStackable())
					++amount;
				else
					amount += inventory[index].amount;
		}
		return amount;
	}
	
	public static int itemCount(Item item, Item[] items, int nItems)
	{
		int amount = 0;
		for (int index = 0; index < nItems; index++)
		{
			if (items[index].id == item.id)
				if (!item.stackable())
					++amount;
				else
					amount += items[index].amount;
		}
		return amount;
	}

	/**
	 * Handles updating the welcome login screen, displaying the option to
	 * create a new account or login to an existing account.
	 * @return true if the user have clicked on the existing user button.
	 * false if not.
	 */
	private final boolean updateLoginWelcomeScreen()
	{
		menuWelcome.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if (menuWelcome.hasActivated(loginButtonNewUser))
			loginScreenNumber = 1;
		if (menuWelcome.hasActivated(loginButtonExistingUser))
		{
			loginScreenNumber = 2;
			menuLogin.updateText(loginStatusText,
					"Please enter your username and password");
			menuLogin.updateText(loginUsernameTextBox, currentUser);
			menuLogin.updateText(loginPasswordTextBox, currentPass);
			menuLogin.setFocus(loginUsernameTextBox);
			return true;
		}
		return false;
	}

	/**
	 * Handles updating the new user login screen, displaying information about how
	 * to create a new account.
	 * @return true if the user clicked on the 'OK' button in the new user menu.
	 * false if not.
	 */
	private final boolean updateLoginNewScreen()
	{
		menuNewUser.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if (menuNewUser.hasActivated(newUserOkButton))
		{
			//loginScreenNumber = 0; // menuWelcome
			loginScreenNumber = 2;
			return true;
		}
		return false;
	}

	/**
	 * Handles updating the exising user login screen, displaying the text fields
	 * where the username and password should be enterd.
	 * @return false.
	 */
	private final boolean updateExistingScreen()
	{
		menuLogin.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if (menuLogin.hasActivated(loginCancelButton))
		{
			//loginScreenNumber = 0; // menuwelcome
			loginScreenNumber = 1;
		}
		if (menuLogin.hasActivated(loginUsernameTextBox))
			menuLogin.setFocus(loginPasswordTextBox);
		if (menuLogin.hasActivated(loginPasswordTextBox)
				|| menuLogin.hasActivated(loginOkButton))
		{
			currentUser = menuLogin.getText(loginUsernameTextBox);
			currentPass = menuLogin.getText(loginPasswordTextBox);
			login(currentUser, currentPass, false);
		}
		return false;
	}

	/**
	 * Invokes the method that handles the current login scren.
	 */
	private final void updateLoginScreen()
	{
		if (super.socketTimeout > 0)
			super.socketTimeout--;
		if (loginScreenNumber == 0)
		{
			if (updateLoginWelcomeScreen())
				return;
		} else if (loginScreenNumber == 1)
		{
			if (updateLoginNewScreen())
				return;
		} else if (loginScreenNumber == 2)
		{
			if (updateExistingScreen())
				return;
		}
	}

	/**
	 * Draws relevant sprites on the login screens. This includes the background
	 * image itself.
	 */
	private final void drawLoginScreenSprites()
	{
		if (loginScreenNumber >= 0 && loginScreenNumber <= 3)
		{
			// background image
			/*
			gameGraphics.imageToPixArray(gameGraphics.loginScreen, 0, 0,
					windowWidth, windowHeight, true);*/
			gameGraphics.imageToPixArray(
					FileOperations.readImage(
							gameGraphics.sprites[3151].getPixels(),
							gameGraphics.sprites[3151].getWidth(),
							gameGraphics.sprites[3151].getHeight()),
					0, 0, windowWidth, windowHeight, true);
			// bottom right sprite
			gameGraphics.drawPicture(0, 20, 2010);
		}
		// blue bar at the bottom
		Sprite sprite;
		sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 22];
		int nSprites = windowWidth / sprite.getWidth();
		int currentOffset = 0;
		for (int i=0; i < nSprites; i++)
		{
			gameGraphics.drawPicture(currentOffset, windowHeight, SPRITE_MEDIA_START + 22);
			currentOffset += sprite.getWidth();
		}
		if (windowWidth-nSprites*sprite.getWidth() != 0)
		{
			gameGraphics.spriteClip1(nSprites*sprite.getWidth(), windowHeight,
					windowWidth-nSprites*sprite.getWidth(),
					sprite.getHeight(), SPRITE_MEDIA_START + 22);
		}
	}

	/**
	 * Draws the login screen background as well as the appropriate login menu.
	 */
	private final void drawLoginScreen()
	{
		hasReceivedWelcomeBoxDetails = false;
		gameGraphics.f1Toggle = false;
		gameGraphics.resetImagePixels(0);
		drawLoginScreenSprites();
		if (loginScreenNumber == 0)
			menuWelcome.drawMenu(true);
		if (loginScreenNumber == 1)
			menuNewUser.drawMenu(true);
		if (loginScreenNumber == 2)
			menuLogin.drawMenu(true);
		gameGraphics.drawImage(aGraphics936, 0, 0);
	}

	/**
	 * Draws the main report abuse window.
	 */
	private final void drawAbuseWindow1()
	{
		abuseSelectedType = 1 + abWin.getSelectedType(super.mouseX, super.mouseY);

		if (mouseButtonClick != 0 && abuseSelectedType != 0) {
			mouseButtonClick = 0;
			showAbuseWindow = 2;
			super.inputText = "";
			super.enteredText = "";
			return;
		}
		if (mouseButtonClick != 0) {
			mouseButtonClick = 0;
			if (!abWin.insideWindow(super.mouseX, super.mouseY)
					|| abWin.insideCloseBtn(super.mouseX, super.mouseY))
			{
				showAbuseWindow = 0;
				return;
			}
		}
		gameGraphics.drawBox(abWin.getX(), abWin.getY(), abWin.getWidth(), abWin.getHeight(), 0);
		gameGraphics.drawBoxEdge(abWin.getX(), abWin.getY(), abWin.getWidth(), abWin.getHeight(), 0xffffff);
		String[] info = abWin.getInfo();
		int rowY = abWin.getFirstLineY();
		for (String str : info)
		{
			gameGraphics.drawText(str, abWin.getXCenter(), rowY, 1, 0xffffff);
			rowY += abWin.getRowSeparation();
		}
		String[] reportInfo = abWin.getReportInfo();
		for (String str : reportInfo)
		{
			gameGraphics.drawText(str, abWin.getXCenter(), rowY, 1, 0xffff00);
			rowY += abWin.getRowSeparation();
		}
		String[] rules = abWin.getRules();
		int i = 0;
		for (String str : rules)
		{
			gameGraphics.drawText(str, abWin.getXCenter(), rowY, 1,
					abWin.getSelectedType(super.mouseX, super.mouseY) == i ? 0xff8000 : 0xffffff);
			++i;
			rowY += abWin.getRowSeparation();
		}
		int selectedType = abWin.getSelectedType(super.mouseX, super.mouseY);
		if (selectedType != -1)
		{
			gameGraphics.drawBoxEdge(abWin.getBoxX(),
					abWin.getFirstRuleY() + abWin.getRowYOffset()
					+ abWin.getSelectedType(super.mouseX, super.mouseY)*abWin.getRowSeparation(),
					abWin.getBoxWidth(), abWin.getRowSeparation(), 0xffffff);
		}
		String [] closeText = abWin.getCloseText();
		for (String str : closeText)
		{
			gameGraphics.drawText(str, abWin.getXCenter(), rowY, 1,
					abWin.insideCloseBtn(super.mouseX, super.mouseY) ? 0xffff00 : 0xffffff);
			rowY += abWin.getRowSeparation();
		}
	}

	private final void autoRotateCamera()
	{
		if ((cameraAutoAngle & 1) == 1 && enginePlayerVisible(cameraAutoAngle))
			return;
		if ((cameraAutoAngle & 1) == 0 && enginePlayerVisible(cameraAutoAngle))
		{
			if (enginePlayerVisible(cameraAutoAngle + 1 & 7))
			{
				cameraAutoAngle = cameraAutoAngle + 1 & 7;
				return;
			}
			if (enginePlayerVisible(cameraAutoAngle + 7 & 7))
				cameraAutoAngle = cameraAutoAngle + 7 & 7;
			return;
		}
		int ai[] = {1, -1, 2, -2, 3, -3, 4};
		for (int i = 0; i < ai.length; ++i)
		{
			if (!enginePlayerVisible(cameraAutoAngle + ai[i] + 8 & 7))
				continue;
			cameraAutoAngle = cameraAutoAngle + ai[i] + 8 & 7;
			break;
		}

		if ((cameraAutoAngle & 1) == 0 && enginePlayerVisible(cameraAutoAngle))
		{
			if (enginePlayerVisible(cameraAutoAngle + 1 & 7))
			{
				cameraAutoAngle = cameraAutoAngle + 1 & 7;
				return;
			}
			if (enginePlayerVisible(cameraAutoAngle + 7 & 7))
				cameraAutoAngle = cameraAutoAngle + 7 & 7;
		}
	}

	public final Graphics getGraphics()
	{
		/*
        if (GameWindow.gameFrame != null) {
            return GameWindow.gameFrame.getGraphics();
        }*/
		return super.getGraphics();
	}

	final void method52(int i, int j, int k, int l, int playerID, int j1, int k1)
	{
		Mob plr = playerArray[playerID];
		if (plr.colourBottomType == 255)
			return;
		int l1 = plr.currentSprite + (cameraZRot + 16) / 32 & 7;
		boolean flag = false;
		int i2 = l1;
		if (i2 == 5) {
			i2 = 3;
			flag = true;
		} else if (i2 == 6) {
			i2 = 2;
			flag = true;
		} else if (i2 == 7) {
			i2 = 1;
			flag = true;
		}
		int j2 = i2 * 3 + walkModel[(plr.stepCount / 6) % 4];
		if (plr.currentSprite == 8) {
			i2 = 5;
			l1 = 2;
			flag = false;
			i -= (5 * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray1[(loginTimer / 5) % 8];
		} else if (plr.currentSprite == 9) {
			i2 = 5;
			l1 = 2;
			flag = true;
			i += (5 * k1) / 100;
			j2 = i2 * 3 + npcCombatModelArray2[(loginTimer / 6) % 8];
		}
		for (int k2 = 0; k2 < 12; k2++)
		{ // player animation
			int l2 = npcAnimationArray[l1][k2];
			int l3 = plr.animationCount[l2] - 1;
			if (l3 >= 0) {
				int k4 = 0;
				int i5 = 0;
				int animationFrame = j2;
				if (flag && i2 >= 1 && i2 <= 3)
					if (EntityHandler.getAnimationDef(l3).hasFlip())
						animationFrame += 15;
					else if (l2 == 4 && i2 == 1) {
						k4 = -22;
						i5 = -3;
						animationFrame = i2 * 3 + walkModel[(2 + plr.stepCount / 6) % 4];
					} else if (l2 == 4 && i2 == 2) {
						k4 = 0;
						i5 = -8;
						animationFrame = i2 * 3 + walkModel[(2 + plr.stepCount / 6) % 4];
					} else if (l2 == 4 && i2 == 3) {
						k4 = 26;
						i5 = -5;
						animationFrame = i2 * 3 + walkModel[(2 + plr.stepCount / 6) % 4];
					} else if (l2 == 3 && i2 == 1) {
						k4 = 22;
						i5 = 3;
						animationFrame = i2 * 3 + walkModel[(2 + plr.stepCount / 6) % 4];
					} else if (l2 == 3 && i2 == 2) {
						k4 = 0;
						i5 = 8;
						animationFrame = i2 * 3 + walkModel[(2 + plr.stepCount / 6) % 4];
					} else if (l2 == 3 && i2 == 3) {
						k4 = -26;
						i5 = 5;
						animationFrame = i2 * 3 + walkModel[(2 + plr.stepCount / 6) % 4];
					}
				if (i2 != 5 || EntityHandler.getAnimationDef(l3).hasAttack())
				{
					int k5 = animationFrame + EntityHandler.getAnimationDef(l3).getNumber();
					k4 = (k4 * k) / ((GameImage) (gameGraphics)).sprites[k5].getTotalWidth();
					i5 = (i5 * l) / ((GameImage) (gameGraphics)).sprites[k5].getTotalHeight();
					int l5 = (k * ((GameImage) (gameGraphics)).sprites[k5].getTotalWidth()) / ((GameImage) (gameGraphics)).sprites[EntityHandler.getAnimationDef(l3).getNumber()].getTotalWidth();
					k4 -= (l5 - k) / 2;
					int colour = EntityHandler.getAnimationDef(l3).getCharColour();
					int skinColour = chrSkinClrs[plr.colourSkinType];
					if (colour == 1)
						colour = chrHairClrs[plr.colourHairType];
					else if (colour == 2)
						colour = chrTopBottomClrs[plr.colourTopType];
					else if (colour == 3)
						colour = chrTopBottomClrs[plr.colourBottomType];
					gameGraphics.spriteClip4(i + k4, j + i5, l5, l, k5,
							colour, skinColour, j1, flag);
				}
			}
		}

		if (plr.lastMessageTimeout > 0)
		{
			mobMsgWidth[mobMessageCount] = gameGraphics.textWidth(plr.lastMessage, 1) / 2;
			if (mobMsgWidth[mobMessageCount] > 150)
				mobMsgWidth[mobMessageCount] = 150;
			mobMsgHeight[mobMessageCount] = (
					(gameGraphics.textWidth(plr.lastMessage, 1) / 300)
					* gameGraphics.messageFontHeight(1));
			mobMsgX[mobMessageCount] = i + k / 2;
			mobMsgY[mobMessageCount] = j;
			mobMsg[mobMessageCount++] = plr.lastMessage;
		}
		if (plr.anInt163 > 0) {
			anIntArray858[anInt699] = i + k / 2;
			anIntArray859[anInt699] = j;
			anIntArray705[anInt699] = k1;
			anIntArray706[anInt699++] = plr.anInt162;
		}
		if (plr.currentSprite == 8
				|| plr.currentSprite == 9
				|| plr.combatTimer != 0)
		{
			if (plr.combatTimer > 0)
			{
				int i3 = i;
				if (plr.currentSprite == 8)
					i3 -= (20 * k1) / 100;
				else if (plr.currentSprite == 9)
					i3 += (20 * k1) / 100;
				int i4 = (plr.hitPointsCurrent * 30) / plr.hitPointsBase;
				anIntArray786[anInt718] = i3 + k / 2; //player hitpointsbar x-position
				anIntArray787[anInt718] = j;
				anIntArray788[anInt718++] = i4;
			}
			if (plr.combatTimer > 150)
			{
				int j3 = i;
				if (plr.currentSprite == 8)
					j3 -= (10 * k1) / 100;
				else if (plr.currentSprite == 9)
					j3 += (10 * k1) / 100;
				// red star, i.e. damage was dealt to player
				gameGraphics.drawPicture((j3 + k / 2) - 12,
						(j + l / 2) - 12, SPRITE_MEDIA_START + 11);
				gameGraphics.drawText(String.valueOf(plr.anInt164),
						(j3 + k / 2) - 1, j + l / 2 + 5, 3, 0xffffff);
			}
		}
		if (plr.anInt179 == 1 && plr.anInt163 == 0)
		{
			int k3 = j1 + i + k / 2;
			if (plr.currentSprite == 8)
				k3 -= (20 * k1) / 100;
			else if (plr.currentSprite == 9)
				k3 += (20 * k1) / 100;
			int j4 = (16 * k1) / 100;
			int l4 = (16 * k1) / 100;
			// wildy skull
			gameGraphics.spriteClip1(k3 - j4 / 2, j - l4 / 2 - (10 * k1) / 100,
					j4, l4, SPRITE_MEDIA_START + 13);
		}
	}

	private final void loadConfigFilter()
	{
		drawLoadingBarText(15, "Unpacking Configuration");
		EntityHandler.load();
	}

	private final void loadModel() throws IOException
	{
		ZipFile objectArchive = new ZipFile(new File(Config.DATABASE_DIR + "models.zip"));
		try {
			for (int j = 0; j < EntityHandler.getModelCount(); j++)
			{
				ZipEntry e = objectArchive.getEntry(String.valueOf(j));
				if (e == null)
				{
					System.err.printf("Model %d missing!", j);
					continue;
				}

				DataInputStream datainputstream = new DataInputStream(objectArchive.getInputStream(e));
				byte entrySize[] = new byte[4];
				datainputstream.readFully(entrySize, 0, 4);
				int dataSize = ((entrySize[0] & 0xff) << 24)
						+ ((entrySize[1] & 0xff) << 16)
						+ ((entrySize[2] & 0xff) << 8)
						+ (entrySize[3] & 0xff);
				int offset = 0;
				byte model[] = new byte[dataSize];
				while (offset < dataSize)
				{
					int bufferSize = dataSize - offset;
					if (bufferSize > 1000)
					{
						bufferSize = 1000;
					}
					datainputstream.readFully(model, offset, bufferSize);
					offset += bufferSize;
				}
				datainputstream.close();

				if (j == 0)
					gameDataModels[j] = new Model(1, 1);
				else
					gameDataModels[j] = new Model(model, 0, true);
				gameDataModels[j].transparent = EntityHandler.getModelName(j).equals("giantcrystal");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			objectArchive.close();
		}
	}

	/**
	 * Loads 3D models
	 */
	private final void loadModels()
	{
		drawLoadingBarText(75, "Loading 3d models");

		String[] modelNames = {
				"torcha2", "torcha3", "torcha4",
				"skulltorcha2", "skulltorcha3", "skulltorcha4",
				"firea2", "firea3",
				"fireplacea2", "fireplacea3",
				"firespell2", "firespell3",
				"lightning2", "lightning3",
				"clawspell2", "clawspell3", "clawspell4", "clawspell5",
				"spellcharge2", "spellcharge3"
		};
		for (String name : modelNames)
			EntityHandler.storeModel(name);
		try
		{
			loadModel();
		} catch (IOException ioe) { ioe.printStackTrace(); }
	}

	protected final void handleMouseDown(int button, int x, int y)
	{
		mouseClickX[mouseClickOffset] = x;
		mouseClickYArray[mouseClickOffset] = y;
		mouseClickOffset = mouseClickOffset + 1 & 0x1fff;
		for (int l = 10; l < 4000; l++) {
			int i1 = mouseClickOffset - l & 0x1fff;
			if (mouseClickX[i1] == x && mouseClickYArray[i1] == y) {
				boolean flag = false;
				for (int j1 = 1; j1 < l; j1++) {
					int k1 = mouseClickOffset - j1 & 0x1fff;
					int l1 = i1 - j1 & 0x1fff;
					if (mouseClickX[l1] != x || mouseClickYArray[l1] != y)
						flag = true;
					if (mouseClickX[k1] != mouseClickX[l1] || mouseClickYArray[k1] != mouseClickYArray[l1])
						break;
					if (j1 == l - 1 && flag && lastWalkTimeout == 0 && logoutTimeout == 0) {
						logout();
						return;
					}
				}

			}
		}

	}

	private final void displayLastLoadedNull()
	{
		Graphics g = getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 512, 356);
		g.setFont(new Font("Helvetica", 1, 16));
		g.setColor(Color.yellow);
		int i = 35;
		g.drawString("Sorry, an error has occured whilst loading TestServer", 30, i);
		i += 50;
		g.setColor(Color.white);
		g.drawString("To fix this try the following (in order):", 30, i);
		i += 50;
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", 1, 12));
		g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, i);
		i += 30;
		g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, i);
		i += 30;
		g.drawString("3: Try using a different game-world", 30, i);
		i += 30;
		g.drawString("4: Try rebooting your computer", 30, i);
		i += 30;
		g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, i);
		changeThreadSleepModifier(1);
		return;
	}

	private void displayMemoryError()
	{
		Graphics g2 = getGraphics();
		g2.setColor(Color.black);
		g2.fillRect(0, 0, 512, 356);
		g2.setFont(new Font("Helvetica", 1, 20));
		g2.setColor(Color.white);
		g2.drawString("Error - out of memory!", 50, 50);
		g2.drawString("Close ALL unnecessary programs", 50, 100);
		g2.drawString("and windows before loading the game", 50, 150);
		g2.drawString("TestServer needs about 100mb of spare RAM", 50, 200);
		changeThreadSleepModifier(1);
		return;
	}

	protected final void method4()
	{
		if (lastLoadedNull)
		{
			displayLastLoadedNull();
			return;
		}
		if (memoryError)
		{
			displayMemoryError();
			return;
		}
		try
		{
			if (loggedIn)
			{
				gameGraphics.drawStringShadows = true;
				drawGame();
			}
			else
			{
				gameGraphics.drawStringShadows = false;
				drawLoginScreen();
			}
		}
		catch (OutOfMemoryError e)
		{
			garbageCollect();
			memoryError = true;
		}
	}

	private final void walkToObject(int x, int y, int id, int type)
	{
		int i1;
		int j1;
		if (id == 0 || id == 4)
		{
			i1 = EntityHandler.getObjectDef(type).getWidth();
			j1 = EntityHandler.getObjectDef(type).getHeight();
		}
		else
		{
			j1 = EntityHandler.getObjectDef(type).getWidth();
			i1 = EntityHandler.getObjectDef(type).getHeight();
		}
		if (EntityHandler.getObjectDef(type).getType() == 2
				|| EntityHandler.getObjectDef(type).getType() == 3)
		{
			if (id == 0)
			{
				x--;
				i1++;
			}
			if (id == 2)
				j1++;
			if (id == 4)
				i1++;
			if (id == 6)
			{
				y--;
				j1++;
			}
			sendWalkCommand(sectionX, sectionY, x, y, (x + i1) - 1,
					(y + j1) - 1, false, true);
			return;
		}
		else
		{
			sendWalkCommand(sectionX, sectionY, x, y, (x + i1) - 1,
					(y + j1) - 1, true, true);
			return;
		}
	}

	private void drawItemBox(InGameGrid panel, int itemID,
			int slotX, int slotY, boolean selected, boolean drawSprite)
	{
		if (selected)
			gameGraphics.drawBoxAlpha(slotX+1, slotY+1,
					InGameGrid.ITEM_SLOT_WIDTH-1,
					InGameGrid.ITEM_SLOT_HEIGHT-1,
					panel.getGridBGSelectColor(),
					panel.getGridBGAlpha());
		else
			gameGraphics.drawBoxAlpha(slotX+1, slotY+1,
					InGameGrid.ITEM_SLOT_WIDTH-1,
					InGameGrid.ITEM_SLOT_HEIGHT-1,
					panel.getGridBGNotSelectColor(),
					panel.getGridBGAlpha());
		gameGraphics.drawBoxEdge(slotX, slotY,
				InGameGrid.ITEM_SLOT_WIDTH+1,
				InGameGrid.ITEM_SLOT_HEIGHT+1, panel.getGridLineColor());
		if (drawSprite)
			gameGraphics.spriteClip4(slotX+1, slotY+1,
					InGameGrid.ITEM_SLOT_WIDTH-1,
					InGameGrid.ITEM_SLOT_HEIGHT-1,
					SPRITE_ITEM_START + EntityHandler.getItemDef(
							itemID).getSprite(),
					EntityHandler.getItemDef(itemID).getPictureMask(),
					0, 0, false);
	}

	/**
	 * Handles what happens when you click on an item in the bank.
	 */
	private void clickBankItem()
	{
		InGameGrid bankGrid = bankPan.getBankGrid();
		int itemIdx = mouseOverBankPageText * bankGrid.getRows()*bankGrid.getCols();
		int mouseXGrid = super.mouseX - bankGrid.getX();
		int mouseYGrid = super.mouseY - bankGrid.getY();
		for (int row = 0; row < bankGrid.getRows(); row++)
		{
			for (int col = 0; col < bankGrid.getCols(); col++)
			{
				int slotXMin = col * itemSlotWidth - 1;
				int slotYMin = row * itemSlotHeight - 1;
				if (mouseXGrid > slotXMin && mouseXGrid < slotXMin + itemSlotWidth
						&& mouseYGrid > slotYMin && mouseYGrid < slotYMin + itemSlotHeight
						&& itemIdx < bankItemCount && bankItemsI[itemIdx].id != -1)
				{
					selectedBankItemType = bankItemsI[itemIdx].id;
					selectedBankItem = itemIdx;
				}
				itemIdx++;
			}

		}
	}

	/**
	 * Handles what happens when you withdraw/deposit an item from/to the bank. 
	 */
	private void clickBankItemMove()
	{
		int selectedBankItemId;
		if (selectedBankItem < 0)
			selectedBankItemId = -1;
		else
			selectedBankItemId = bankItemsI[selectedBankItem].id;
		if (selectedBankItemId != -1)
		{
			int selectedBankItemCount = bankItemsI[selectedBankItem].amount;
			int depAmt = bankPan.getDepAmt(super.mouseX, super.mouseY,
					inventoryCount(selectedBankItemId));
			if (depAmt != 0)
				formatPacket(198, selectedBankItemId, depAmt);
			int withAmt = bankPan.getWithAmt(super.mouseX, super.mouseY,
					selectedBankItemCount);
			if (withAmt != 0)
				formatPacket(183, selectedBankItemId, withAmt);
		}
	}

	/**
	 * Checks if an invalid item is selected. If so it will clear the reference.
	 */
	private void checkSelectedBankItem()
	{
		if (selectedBankItem >= bankItemCount || selectedBankItem < 0)
			selectedBankItem = -1;
		if (selectedBankItem != -1
				&& bankItemsI[selectedBankItem].id != selectedBankItemType)
		{
			selectedBankItem = -1;
			selectedBankItemType = -2;
		}
	}

	public static final String getAbbreviatedValue(int amount)
	{
		String abbrevVal = String.valueOf(amount);
		if (amount >= 10000000)
			abbrevVal = String.valueOf(amount/1000000) + "M";
		else if (amount >= 100000)
			abbrevVal = String.valueOf(amount/1000) + "K";
		return abbrevVal;
	}

	/**
	 * Presents the bank on the screen and handles clicking in the bank.
	 */
	private final void drawBankBox()
	{
		mouseOverBankPageText = bankPan.updateVisibleBankTabs(mouseOverBankPageText, bankItemCount);
		checkSelectedBankItem();
		if (mouseButtonClick != 0)
		{
			mouseButtonClick = 0;
			if (bankPan.getBankGrid().isMouseOverGrid(super.mouseX, super.mouseY))
				clickBankItem();
			else if (super.mouseX > bankPan.getBottomInfoBoxX()
					&& super.mouseY > bankPan.getBottomInfoBoxY()
					&& super.mouseX < (bankPan.getBottomInfoBoxX()
							+ bankPan.getBottomInfoBoxWidth())
					&& super.mouseY < (bankPan.getBottomInfoBoxY()
							+ bankPan.getBottomInfoBoxHeight()))
				clickBankItemMove();
			else if (bankPan.getTabButtonPanel().isMouseOver(
					super.mouseX, super.mouseY))
				mouseOverBankPageText = bankPan.switchBankTab(bankItemCount, mouseOverBankPageText, super.mouseX, super.mouseY);
			else if (!bankPan.getFrame().isMouseOver(super.mouseX, super.mouseY)
					|| (bankPan.getFrame().getCloseButton().isMouseOverButton(
							super.mouseX, super.mouseY)))
			{
				formatPacket(48, -1, -1);
				return;
			}
		}
		bankPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		bankPan.drawBankTabs(bankItemCount, mouseOverBankPageText, super.mouseX, super.mouseY);
		bankPan.drawBankInfo();
		InGameGrid bankGrid = bankPan.getBankGrid();
		int itemIdx = mouseOverBankPageText * bankGrid.getRows()*bankGrid.getCols();
		bankGrid.drawStorableGrid(bankItemsI, itemIdx, bankItemCount, bankPan.getBankCountTextColor(), selectedBankItem, inventory, inventoryCount, 0x00ffff);
		bankPan.drawBankDepWithPanel(bankItemsI, selectedBankItem, inventory, inventoryCount, super.mouseX, super.mouseY);
	}

	/**
	 * Draws a box on the screen with text in it.
	 * @param x X-position.
	 * @param y Y-position.
	 * @param width Box width.
	 * @param height Box height.
	 * @param color Box color, e.g. 0xffffff.
	 * @param border true if a border should be drawn.
	 * @param borderThick Border thickness.
	 * @param borderColor Border Color, e.g. 0xffffff.
	 * @param textType The type of text. Defined in GameWindow.loadFonts()
	 * @param textColor Text color, e.g. 0xffffff.
	 * @param text The text to be displayed in the box.
	 */
	public final void drawInfoBox(
			int x, int y, int width, int height, int color,
			boolean border, int borderThick, int borderColor,
			int textType, int textColor, String text)
	{
		gameGraphics.drawBox(x, y, width, height, color);
		gameGraphics.drawBoxEdge(x, y, width, height, borderColor);
		gameGraphics.drawText(text, x+width/2, y+height/2 + 6,
				textType, textColor);
	}

	/**
	 * Displays a message that the player is logging out.
	 */
	private final void drawLoggingOutBox()
	{
		gameGraphics.drawBox(windowHalfWidth - 130,
				windowHalfHeight - 30, 260, 60, 0);
		gameGraphics.drawBoxEdge(windowHalfWidth - 130,
				windowHalfHeight - 30, 260, 60, 0xffffff);
		gameGraphics.drawText("Logging out...", windowHalfWidth,
				windowHalfHeight + 6, 5, 0xffffff);
	}

	private void handleInventoryMouseover()
	{
		InGameGrid invGrid = invPan.getInvGrid();
		int xInInv = super.mouseX - invGrid.getX();
		int yInInv = super.mouseY - invGrid.getY();
		int currentInventorySlot = xInInv / itemSlotWidth
				+ (yInInv / itemSlotHeight) * invGrid.getCols();
		if (currentInventorySlot < invGrid.getCols()*invGrid.getRows())
		{
			Item item = inventory[currentInventorySlot];
			if (selectedSpell >= 0) {
				if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 3)
				{
					rightClickMenu[menuLength].text1 = "Cast "
							+ EntityHandler.getSpellDef(selectedSpell).getName()
							+ " on";
					rightClickMenu[menuLength].text2 = "@lre@" + item.name();
					rightClickMenu[menuLength].id = 600;
					rightClickMenu[menuLength].actionType = currentInventorySlot;
					rightClickMenu[menuLength].actionVariable = selectedSpell;
					menuLength++;
					return;
				}
			} else {
				if (selectedItem >= 0) {
					rightClickMenu[menuLength].text1 = "Use " + selectedItemName + " with";
					rightClickMenu[menuLength].text2 = "@lre@" + item.name();
					rightClickMenu[menuLength].id = 610;
					rightClickMenu[menuLength].actionType = currentInventorySlot;
					rightClickMenu[menuLength].actionVariable = selectedItem;
					menuLength++;
					return;
				}
				if (wearing[currentInventorySlot] == 1) {
					rightClickMenu[menuLength].text1 = "Remove";
					rightClickMenu[menuLength].text2 = "@lre@" + item.name();
					rightClickMenu[menuLength].id = 620;
					rightClickMenu[menuLength].actionType = currentInventorySlot;
					menuLength++;
				} else if (item.wieldable()) {
					rightClickMenu[menuLength].text1 = "Wear";
					rightClickMenu[menuLength].text2 = "@lre@" + item.name();
					rightClickMenu[menuLength].id = 630;
					rightClickMenu[menuLength].actionType = currentInventorySlot;
					menuLength++;
				}
				if (!item.command().equals("")) {
					rightClickMenu[menuLength].text1 = item.command();
					rightClickMenu[menuLength].text2 = "@lre@" + item.name();
					rightClickMenu[menuLength].id = 640;
					rightClickMenu[menuLength].actionType = currentInventorySlot;
					menuLength++;
				}
				rightClickMenu[menuLength].text1 = "Use";
				rightClickMenu[menuLength].text2 = "@lre@" + item.name();
				rightClickMenu[menuLength].id = 650;
				rightClickMenu[menuLength].actionType = currentInventorySlot;
				menuLength++;
				rightClickMenu[menuLength].text1 = "Drop";
				rightClickMenu[menuLength].text2 = "@lre@" + item.name();
				rightClickMenu[menuLength].id = 660;
				rightClickMenu[menuLength].actionType = currentInventorySlot;
				menuLength++;
				rightClickMenu[menuLength].text1 = "Examine";
				rightClickMenu[menuLength].text2 = "@lre@" + item.name() +
						(ourPlayer.admin >= 2 ? " @or1@(" + item.id + ")" : "");
				rightClickMenu[menuLength].id = 3600;
				rightClickMenu[menuLength].actionType = item.id;
				menuLength++;
			}
		}
		else
		{ // we don't want to click through empty inventory slots
			menuLength++;
		}
	}

	private final void drawInventoryMenu(boolean flag)
	{
		invPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		invPan.getInvGrid().drawStaticGrid(inventory,
				inventoryCount, wearing,
				invPan.getInvCountTextColor());
		if (!flag)
			return;
		if (invPan.getInvGrid().isMouseOver(super.mouseX, super.mouseY))
		{
			handleInventoryMouseover();
		}
		else if (invPan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // close button
			menuLength++;
			if (mouseButtonClick == 1)
			{
				mouseOverMenu = 0;
				mouseButtonClick = 0;
			}
		}
		else if (invPan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside inventory but not on the grid or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
	}

	private final void drawChatMessageTabs()
	{
		// blue bar at the botom
		Sprite sprite;
		sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 22];
		int nSprites = windowWidth / sprite.getWidth();
		int currentOffset = 0;
		for (int i=0; i < nSprites; i++)
		{
			gameGraphics.drawPicture(currentOffset, windowHeight, SPRITE_MEDIA_START + 22);
			currentOffset += sprite.getWidth();
		}
		if (windowWidth-nSprites*sprite.getWidth() != 0)
		{
			gameGraphics.spriteClip1(nSprites*sprite.getWidth(), windowHeight,
					windowWidth-nSprites*sprite.getWidth(),
					sprite.getHeight(), SPRITE_MEDIA_START + 22);
		}
		sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 23];
		gameGraphics.drawPicture(0, windowHeight - 4, SPRITE_MEDIA_START + 23);

		int i = GameImage.convertRGBToLong(200, 200, 255);
		if (messagesTab == 0)
			i = GameImage.convertRGBToLong(255, 200, 50);
		if (anInt952 % 30 > 15)
			i = GameImage.convertRGBToLong(255, 50, 50);
		int buttonWidth = 82;
		int j = 14 + buttonWidth/2;
		gameGraphics.drawText("All messages", j, windowHeight + 6, 0, i);
		i = GameImage.convertRGBToLong(200, 200, 255);
		if (messagesTab == 1)
			i = GameImage.convertRGBToLong(255, 200, 50);
		if (anInt953 % 30 > 15)
			i = GameImage.convertRGBToLong(255, 50, 50);
		j += 99;
		gameGraphics.drawText("Chat history", j, windowHeight + 6, 0, i);
		i = GameImage.convertRGBToLong(200, 200, 255);
		if (messagesTab == 2)
			i = GameImage.convertRGBToLong(255, 200, 50);
		if (anInt954 % 30 > 15)
			i = GameImage.convertRGBToLong(255, 50, 50);
		j += 101;
		gameGraphics.drawText("Quest history", j, windowHeight + 6, 0, i);
		i = GameImage.convertRGBToLong(200, 200, 255);
		if (messagesTab == 3)
			i = GameImage.convertRGBToLong(255, 200, 50);
		if (anInt955 % 30 > 15)
			i = GameImage.convertRGBToLong(255, 50, 50);
		j += 101;
		gameGraphics.drawText("Private history", j, windowHeight + 6, 0, i);
		j += 101;
		gameGraphics.drawText("Report abuse", j, windowHeight + 6, 0, 0xffffff);
	}

	/**
	 * draw the sprites for character design menu
	 */
	private final void method62()
	{
		gameGraphics.f1Toggle = false;
		gameGraphics.resetImagePixels(0);
		chrDesignMenu.drawMenu(true);
		int i = windowHalfWidth - 116;
		int j = this.windowHalfHeight - 117;
		i += 116;
		j -= 25;
		gameGraphics.spriteClip3(i - 32 - 55, j, 64, 102, EntityHandler.getAnimationDef(character2Colour).getNumber(), chrTopBottomClrs[chrBottomClr]);
		gameGraphics.spriteClip4(i - 32 - 55, j, 64, 102, EntityHandler.getAnimationDef(chrBodyGender).getNumber(), chrTopBottomClrs[chrTopClr], chrSkinClrs[chrSkinClr], 0, false);
		gameGraphics.spriteClip4(i - 32 - 55, j, 64, 102, EntityHandler.getAnimationDef(chrHeadType).getNumber(), chrHairClrs[chrHairClr], chrSkinClrs[chrSkinClr], 0, false);
		gameGraphics.spriteClip3(i - 32, j, 64, 102, EntityHandler.getAnimationDef(character2Colour).getNumber() + 6, chrTopBottomClrs[chrBottomClr]);
		gameGraphics.spriteClip4(i - 32, j, 64, 102, EntityHandler.getAnimationDef(chrBodyGender).getNumber() + 6, chrTopBottomClrs[chrTopClr], chrSkinClrs[chrSkinClr], 0, false);
		gameGraphics.spriteClip4(i - 32, j, 64, 102, EntityHandler.getAnimationDef(chrHeadType).getNumber() + 6, chrHairClrs[chrHairClr], chrSkinClrs[chrSkinClr], 0, false);
		gameGraphics.spriteClip3((i - 32) + 55, j, 64, 102, EntityHandler.getAnimationDef(character2Colour).getNumber() + 12, chrTopBottomClrs[chrBottomClr]);
		gameGraphics.spriteClip4((i - 32) + 55, j, 64, 102, EntityHandler.getAnimationDef(chrBodyGender).getNumber() + 12, chrTopBottomClrs[chrTopClr], chrSkinClrs[chrSkinClr], 0, false);
		gameGraphics.spriteClip4((i - 32) + 55, j, 64, 102, EntityHandler.getAnimationDef(chrHeadType).getNumber() + 12, chrHairClrs[chrHairClr], chrSkinClrs[chrSkinClr], 0, false);

		// blue bar at the botom
		Sprite sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 22];
		int nSprites = windowWidth / sprite.getWidth();
		int currentOffset = 0;
		for (int n=0; n < nSprites; n++)
		{
			gameGraphics.drawPicture(currentOffset, windowHeight, SPRITE_MEDIA_START + 22);
			currentOffset += sprite.getWidth();
		}
		if (windowWidth-nSprites*sprite.getWidth() != 0)
		{
			gameGraphics.spriteClip1(nSprites*sprite.getWidth(), windowHeight,
					windowWidth-nSprites*sprite.getWidth(),
					sprite.getHeight(), SPRITE_MEDIA_START + 22);
		}
		gameGraphics.drawPicture(0, windowHeight, SPRITE_MEDIA_START + 22);
		gameGraphics.drawImage(aGraphics936, 0, 0);
	}

	private final Mob makePlayer(int mobArrayIndex, double x, double y, int sprite) {
		if (mobArray[mobArrayIndex] == null) {
			mobArray[mobArrayIndex] = new Mob();
			mobArray[mobArrayIndex].serverIndex = mobArrayIndex;
			mobArray[mobArrayIndex].mobIntUnknown = 0;
		}
		Mob mob = mobArray[mobArrayIndex];
		boolean flag = false;
		for (int i1 = 0; i1 < lastPlayerCount; i1++) {
			if (lastPlayerArray[i1].serverIndex != mobArrayIndex)
				continue;
			flag = true;
			break;
		}

		if (flag) {
			mob.nextSprite = sprite;
			int j1 = mob.waypointCurrent;
			if (x != mob.waypointsX[j1] || y != mob.waypointsY[j1]) {
				mob.waypointCurrent = j1 = (j1 + 1) % 10;
				mob.waypointsX[j1] = x;
				mob.waypointsY[j1] = y;
			}
		} else {
			mob.serverIndex = mobArrayIndex;
			mob.waypointEndSprite = 0;
			mob.waypointCurrent = 0;
			mob.waypointsX[0] = mob.currentX = x;
			mob.waypointsY[0] = mob.currentY = y;
			mob.nextSprite = mob.currentSprite = sprite;
			mob.stepCount = 0;
		}
		playerArray[playerCount++] = mob;
		return mob;
	}

	private final void drawWelcomeBox()
	{
		int boxHeight = 65;
		if (!lastLoggedInAddress.equals("0.0.0.0"))
			boxHeight += 30;
		if (subscriptionLeftDays > 0)
			boxHeight += 15;
		int j = windowHalfHeight - boxHeight / 2;
		gameGraphics.drawBox(windowHalfWidth - 200, windowHalfHeight - boxHeight / 2, 400, boxHeight, 0);
		gameGraphics.drawBoxEdge(windowHalfWidth - 200, windowHalfHeight - boxHeight / 2, 400, boxHeight, 0xffffff);
		j += 20;
		gameGraphics.drawText("Welcome to TestServer " + currentUser, windowHalfWidth, j, 4, 0xffff00);
		j += 30;
		String s;
		if (lastLoggedInDays == 0)
			s = "earlier today";
		else if (lastLoggedInDays == 1)
			s = "yesterday";
		else
			s = lastLoggedInDays + " days ago";
		if (!lastLoggedInAddress.equals("0.0.0.0")) {
			gameGraphics.drawText("You last logged in " + s, windowHalfWidth, j, 1, 0xffffff);
			j += 15;
			gameGraphics.drawText("from: " + lastLoggedInAddress, windowHalfWidth, j, 1, 0xffffff);
			j += 15;
		}
		if (subscriptionLeftDays > 0) {
			gameGraphics.drawText("Subscription Left: " + subscriptionLeftDays + " days", windowHalfWidth, j, 1, 0xffffff);
			j += 15;
		}
		int l = 0xffffff;
		if (super.mouseY > j - 12 && super.mouseY <= j
				&& super.mouseX > windowHalfWidth - 150
				&& super.mouseX < windowHalfWidth + 150)
			l = 0xff0000;
		gameGraphics.drawText("Click here to close window", windowHalfWidth, j, 1, l);
		if (mouseButtonClick == 1) {
			if (l == 0xff0000)
				showWelcomeBox = false;
			if ((super.mouseX < windowHalfWidth - 170
					|| super.mouseX > windowHalfWidth + 170)
					&& (super.mouseY < windowHalfHeight - boxHeight / 2
							|| super.mouseY > windowHalfHeight + boxHeight / 2))
				showWelcomeBox = false;
		}
		mouseButtonClick = 0;
	}

	private final void logout() {
		if (!loggedIn) {
			return;
		}
		if (lastWalkTimeout > 450) {
			displayMessage("@cya@You can't logout during combat!", 3, 0);
			return;
		}
		if (lastWalkTimeout > 0) {
			displayMessage("@cya@You can't logout for 10 seconds after combat", 3, 0);
			return;
		}
		super.streamClass.createPacket(129);
		super.streamClass.formatPacket();
		logoutTimeout = 1000;
	}

	private void drawStatsTab()
	{
		int i1 = plrPan.getY() + plrPan.getTabHeight() + plrPan.getHeaderHeight()/*13*/;
		int k1 = -1;
		int yOffset = -5;
		gameGraphics.drawString("Skills", plrPan.getX() + 5, i1 + yOffset,
				3, 0xffff00);
		gameGraphics.drawString("Fatigue: @yel@" + fatigue + "%",
				(plrPan.getX() + plrPan.getWidth() / 2) + 5, i1 + yOffset,
				1, 0xffffff);
		i1 += plrPan.getStatButtonPanel().getHeight() + plrPan.getHeaderHeight();
		int i = 0;
		for (InGameButton button : plrPan.getStatButtonPanel().getButtons())
		{
			gameGraphics.drawString(button.getButtonText() + ":@yel@"
					+ playerStatCurrent[i] + "/" + playerStatBase[i],
					button.getX() + 5, button.getY() + 10, 1,
					button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
			if (button.isMouseOverButton(super.mouseX, super.mouseY))
				k1 = plrPan.getCorrectedSkillIndex(i);
			++i;
		}

		gameGraphics.drawString("Equipment Status", plrPan.getX() + 5,
				i1 + yOffset, 3, 0xffff00);

		i = 0;
		for (InGameButton button : plrPan.getEquipmentButtonPanel().getButtons())
		{
			gameGraphics.drawString(button.getButtonText()
					+ ":@yel@" + equipmentStatus[i],
					button.getX() + 5, button.getY() + 10, 1,
					button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
			++i;
		}
		i1 += plrPan.getEquipmentButtonPanel().getHeight() + 1;

		gameGraphics.drawLineX(plrPan.getX(), i1 /*- 15*/, plrPan.getWidth(),
				plrPan.getLineColor());
		i1 += 11;
		if (k1 != -1) {
			gameGraphics.drawString(skillArrayLong[k1] + " skill",
					plrPan.getX() + 5, i1, 1, 0xffff00);
			i1 += 12;
			int k2 = experienceArray[0];
			for (int i3 = 0; i3 < 98; i3++)
				if (playerStatExperience[k1] >= experienceArray[i3])
					k2 = experienceArray[i3 + 1];

			gameGraphics.drawString("Total xp: " + playerStatExperience[k1],
					plrPan.getX() + 5, i1, 1, 0xffffff);
			i1 += 12;
			gameGraphics.drawString("Next level at: " + k2, plrPan.getX() + 5,
					i1, 1, 0xffffff);
			i1 += 12;
			gameGraphics.drawString("Required xp: " + (k2 - playerStatExperience[k1]),
					plrPan.getX() + 5, i1, 1, 0xffffff);
		} else {
			gameGraphics.drawString("Overall levels", plrPan.getX() + 5, i1, 1, 0xffff00);
			i1 += 12;
			int skillTotal = 0;
			long expTotal = 0;
			for (int j3 = 0; j3 < 18; j3++) {
				skillTotal += playerStatBase[j3];
				expTotal += playerStatExperience[j3];
			}
			gameGraphics.drawString("Skill total: " + skillTotal, plrPan.getX() + 5, i1, 1, 0xffffff);
			i1 += 12;
			gameGraphics.drawString("Total xp: " + expTotal, plrPan.getX() + 5, i1, 1, 0xffffff);
			i1 += 12;
			gameGraphics.drawString("Combat level: " + ourPlayer.level, plrPan.getX() + 5, i1, 1, 0xffffff);
		}
	}

	private void drawQuestsTab()
	{
		/* TODO: if player completed quest; set 0x00ff00,
		 * if player started quest, set 0xffff00, else set 0xff0000
		 */
		int i1 = plrPan.getY() + plrPan.getTabHeight() + 12;
		gameGraphics.drawString("Quest-list (green=completed)",
				plrPan.getX() + 5, i1, 2, 0xffffff);
		questMenu.resetListTextCount(questMenuHandle);
		i1 = 0;
		String s1;
		for (int idx = 1; idx < quests.length; idx++)
		{
			if (true)
			{ // quest complete
				s1 = "@gre@";
			}/*
        	else if (true || false)
        	{ // quest started
        		s1 = "@yel@";
        	}
        	else
        	{ // quest not started
        		s1 = "@red@";
        	}*/
			questMenu.drawMenuListText(questMenuHandle, i1++, s1 + quests[idx]);
		}
		questMenu.drawMenu(true);
	}

	private void drawInfoPanel()
	{
		int questTabColor = (anInt826 == 1) ? plrPan.getBGColor() : plrPan.getInactiveTabColor();
		int statsTabColor = (anInt826 == 0) ? plrPan.getBGColor() : plrPan.getInactiveTabColor();
		gameGraphics.drawBoxAlpha(plrPan.getX(), plrPan.getY(),
				plrPan.getWidth() / 2, plrPan.getTabHeight(),
				statsTabColor, plrPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(plrPan.getX() + plrPan.getWidth() / 2,
				plrPan.getY(), plrPan.getWidth() / 2,
				plrPan.getTabHeight(), questTabColor, plrPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(plrPan.getX(),
				plrPan.getY() + plrPan.getTabHeight(), plrPan.getWidth(),
				plrPan.getHeight() - plrPan.getTabHeight(),
				plrPan.getBGColor(), plrPan.getBGAlpha());

		gameGraphics.drawLineX(plrPan.getX(), plrPan.getY(),
				plrPan.getWidth(), plrPan.getLineColor());
		gameGraphics.drawLineY(plrPan.getX(),
				plrPan.getY(), plrPan.getTabHeight(), plrPan.getLineColor());
		gameGraphics.drawLineY(plrPan.getX() + plrPan.getWidth(),
				plrPan.getY(), plrPan.getTabHeight(), plrPan.getLineColor());

		gameGraphics.drawLineX(plrPan.getX(),
				plrPan.getY() + plrPan.getTabHeight(),
				plrPan.getWidth(), plrPan.getLineColor());
		gameGraphics.drawLineY(plrPan.getX() + plrPan.getWidth() / 2,
				plrPan.getY(), plrPan.getTabHeight(), plrPan.getLineColor());
		InGameButton button = plrPan.getStatButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth()/2,
				button.getY() + button.getHeight()/2 + 4, 4, 0);
		button = plrPan.getQuestButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth()/2,
				button.getY() + button.getHeight()/2 + 4, 4, 0);
	}

	private final void drawPlayerInfoMenu(boolean flag)
	{
		plrPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		drawInfoPanel();
		if (anInt826 == 0)
			drawStatsTab();
		if (anInt826 == 1)
			drawQuestsTab();
		if (!flag)
		{
			return;
		}
		if (plrPan.isMouseOver(super.mouseX, super.mouseY))
		{
			if (super.mouseY <= plrPan.getY() + plrPan.getTabHeight()
			&& mouseButtonClick == 1)
			{
				mouseButtonClick = 0;
				if (super.mouseX < plrPan.getX() + plrPan.getWidth()/2)
				{
					anInt826 = 0;
					return;
				}
				if (super.mouseX > plrPan.getX() + plrPan.getWidth()/2)
				{
					anInt826 = 1;
				}
			}
			else if (super.mouseY > plrPan.getY() + plrPan.getTabHeight())
			{
				if (anInt826 == 0)
				{
					//handle clicking on skills and combat stats
				}
				else if (anInt826 == 1)
				{
					/* TODO: Find out why the scroll bar does not work for quest menu
					 * but it does for magic menu. I fixed it now by increasing the
					 * visible scroll area to twice the width of the scroll bar.
					 */ 
					questMenu.updateActions(super.mouseX, super.mouseY,
							super.lastMouseDownButton, super.mouseDownButton);
					mouseButtonClick = 0;
				}
				mouseButtonClick = 0;
			}
		}
		else if (plrPan.getFrame().getCloseButton().isMouseOverButton(
				super.mouseX, super.mouseY))
		{ // close button

			menuLength++;
			if (mouseButtonClick == 1)
			{
				mouseOverMenu = 0;
				mouseButtonClick = 0;
			}
		}
		else if (plrPan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside info panel but not on the content or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
	}
	/*
    private final void drawPlayerInfoMenu(boolean flag) {
        int i = ((GameImage) (gameGraphics)).menuDefaultWidth - 199;
        int j = 36;
        gameGraphics.drawPicture(i - 49, 3, SPRITE_MEDIA_START + 3);
        char c = '\304';
        char c1 = '\u0113';
        int l;
        int k = l = GameImage.convertRGBToLong(160, 160, 160);
        if (anInt826 == 0)
            k = GameImage.convertRGBToLong(220, 220, 220);
        else
            l = GameImage.convertRGBToLong(220, 220, 220);
        gameGraphics.drawBoxAlpha(i, j, c / 2, 24, k, 128);
        gameGraphics.drawBoxAlpha(i + c / 2, j, c / 2, 24, l, 128);
        gameGraphics.drawBoxAlpha(i, j + 24, c, c1 - 24, GameImage.convertRGBToLong(220, 220, 220), 128);
        gameGraphics.drawLineX(i, j + 24, c, 0);
        gameGraphics.drawLineY(i + c / 2, j, 24, 0);
        gameGraphics.drawText("Stats", i + c / 4, j + 16, 4, 0);
        gameGraphics.drawText("Info", i + c / 4 + c / 2, j + 16, 4, 0);
        if (anInt826 == 0) {
            int i1 = 72;
            int k1 = -1;
            gameGraphics.drawString("Skills", i + 5, i1, 3, 0xffff00);
            i1 += 13;
            gameGraphics.drawString("Fatigue: @yel@" + fatigue + "%", (i + c / 2) - 5, i1 - 13, 1, 0xffffff);
            for (int l1 = 0; l1 < 9; l1++) {
                int i2 = 0xffffff;
                if (super.mouseX > i + 3 && super.mouseY >= i1 - 11 && super.mouseY < i1 + 2 && super.mouseX < i + 90) {
                    i2 = 0xff0000;
                    k1 = l1;
                }
                gameGraphics.drawString(skillArray[l1] + ":@yel@" + playerStatCurrent[l1] + "/" + playerStatBase[l1], i + 5, i1, 1, i2);
                i2 = 0xffffff;
                if (super.mouseX >= i + 90 && super.mouseY >= i1 - 11 && super.mouseY < i1 + 2 && super.mouseX < i + 196) {
                    i2 = 0xff0000;
                    k1 = l1 + 9;
                }
                gameGraphics.drawString(skillArray[l1 + 9] + ":@yel@" + playerStatCurrent[l1 + 9] + "/" + playerStatBase[l1 + 9], (i + c / 2) - 5, i1, 1, i2);
                i1 += 13;
            }

            i1 += 8;
            gameGraphics.drawString("Equipment Status", i + 5, i1, 3, 0xffff00);
            i1 += 12;
            for (int j2 = 0; j2 < 3; j2++) {
                gameGraphics.drawString(equipmentStatusName[j2] + ":@yel@" + equipmentStatus[j2], i + 5, i1, 1, 0xffffff);
                gameGraphics.drawString(equipmentStatusName[j2 + 3] + ":@yel@" + equipmentStatus[j2 + 3], i + c / 2 + 25, i1, 1, 0xffffff);
                i1 += 13;
            }

            i1 += 6;
            gameGraphics.drawLineX(i, i1 - 15, c, 0);
            if (k1 != -1) {
                gameGraphics.drawString(skillArrayLong[k1] + " skill", i + 5, i1, 1, 0xffff00);
                i1 += 12;
                int k2 = experienceArray[0];
                for (int i3 = 0; i3 < 98; i3++)
                    if (playerStatExperience[k1] >= experienceArray[i3])
                        k2 = experienceArray[i3 + 1];

                gameGraphics.drawString("Total xp: " + playerStatExperience[k1], i + 5, i1, 1, 0xffffff);
                i1 += 12;
                gameGraphics.drawString("Next level at: " + k2, i + 5, i1, 1, 0xffffff);
                i1 += 12;
                gameGraphics.drawString("Required xp: " + (k2 - playerStatExperience[k1]), i + 5, i1, 1, 0xffffff);
            } else {
                gameGraphics.drawString("Overall levels", i + 5, i1, 1, 0xffff00);
                i1 += 12;
                int skillTotal = 0;
                long expTotal = 0;
                for (int j3 = 0; j3 < 18; j3++) {
                    skillTotal += playerStatBase[j3];
                    expTotal += playerStatExperience[j3];
                }
                gameGraphics.drawString("Skill total: " + skillTotal, i + 5, i1, 1, 0xffffff);
                i1 += 12;
                gameGraphics.drawString("Total xp: " + expTotal, i + 5, i1, 1, 0xffffff);
                i1 += 12;
                gameGraphics.drawString("Combat level: " + ourPlayer.level, i + 5, i1, 1, 0xffffff);
            }
        }
        if (anInt826 == 1) {
            int i1 = 72; // Player Info
            gameGraphics.drawString("Player Info", i + 5, i1, 3, 0xffff00);
            i1 += 13;
            gameGraphics.drawString("Username:@yel@ " + ourPlayer.name, i + 5, i1, 1, 0xffffff);
            i1 += 13;
            gameGraphics.drawString("Coords:@yel@ (" + (sectionX + areaX) + ", " + (sectionY + areaY) + ")", i + 5, i1, 1, 0xffffff);
            i1 += 13;
            gameGraphics.drawString("Server Index:@yel@ " + ourPlayer.serverIndex, i + 5, i1, 1, 0xffffff);
            i1 += 13;
            gameGraphics.drawString("Exp Gained:@yel@ " + (expGained > 1000 ? (expGained / 1000) + "k" : expGained), i + 5, i1, 1, 0xffffff);
            if (!lastLoggedInAddress.equals("0.0.0.0")) {
                i1 += 13;
                gameGraphics.drawString("Last IP:@yel@ " + lastLoggedInAddress, i + 5, i1, 1, 0xffffff);
            }
            i1 += 21; // Client Info
            gameGraphics.drawString("Client Info", i + 5, i1, 3, 0xffff00);
            i1 += 13;
            gameGraphics.drawString("Hostname:@yel@ " + localhost, i + 5, i1, 1, 0xffffff);
            i1 += 13;
            gameGraphics.drawString("Uptime:@yel@ " + timeSince(startTime), i + 5, i1, 1, 0xffffff);
            i1 += 21; // Server Info
            gameGraphics.drawString("Server Info", i + 5, i1, 3, 0xffff00);
            i1 += 13;
            gameGraphics.drawString("Hostname:@yel@ " + Config.SERVER_IP, i + 5, i1, 1, 0xffffff);
            i1 += 13;
            gameGraphics.drawString("Uptime:@yel@ " + timeSince(serverStartTime), i + 5, i1, 1, 0xffffff);
            i1 += 13;
            gameGraphics.drawString("Location:@yel@ " + serverLocation, i + 5, i1, 1, 0xffffff);
            i1 += 13;
        }
        if (!flag) {
            return;
        }
        i = super.mouseX - (((GameImage) (gameGraphics)).menuDefaultWidth - 199);
        j = super.mouseY - 36;
        if (i >= 0 && j >= 0 && i < c && j < c1) {
            if (j <= 24 && mouseButtonClick == 1) {
                if (i < 98) {
                    anInt826 = 0;
                    return;
                }
                if (i > 98) {
                    anInt826 = 1;
                }
            }
        }
    }
	 */
	private final void drawWildernessWarningBox() {
		int i = 97;
		gameGraphics.drawBox(86, 77, 340, 180, 0);
		gameGraphics.drawBoxEdge(86, 77, 340, 180, 0xffffff);
		gameGraphics.drawText("Warning! Proceed with caution", 256, i, 4, 0xff0000);
		i += 26;
		gameGraphics.drawText("If you go much further north you will enter the", 256, i, 1, 0xffffff);
		i += 13;
		gameGraphics.drawText("wilderness. This a very dangerous area where", 256, i, 1, 0xffffff);
		i += 13;
		gameGraphics.drawText("other players can attack you!", 256, i, 1, 0xffffff);
		i += 22;
		gameGraphics.drawText("The further north you go the more dangerous it", 256, i, 1, 0xffffff);
		i += 13;
		gameGraphics.drawText("becomes, but the more treasure you will find.", 256, i, 1, 0xffffff);
		i += 22;
		gameGraphics.drawText("In the wilderness an indicator at the bottom-right", 256, i, 1, 0xffffff);
		i += 13;
		gameGraphics.drawText("of the screen will show the current level of danger", 256, i, 1, 0xffffff);
		i += 22;
		int j = 0xffffff;
		if (super.mouseY > i - 12 && super.mouseY <= i && super.mouseX > 181 && super.mouseX < 331)
			j = 0xff0000;
		gameGraphics.drawText("Click here to close window", 256, i, 1, j);
		if (mouseButtonClick != 0) {
			if (super.mouseY > i - 12 && super.mouseY <= i && super.mouseX > 181 && super.mouseX < 331)
				wildernessType = 2;
			if (super.mouseX < 86 || super.mouseX > 426 || super.mouseY < 77 || super.mouseY > 257)
				wildernessType = 2;
			mouseButtonClick = 0;
		}
	}

	final void drawItems(int i, int j, int k, int l, int id, int j1, int k1) {
		int l1 = EntityHandler.getItemDef(id).getSprite() + SPRITE_ITEM_START;
		int i2 = EntityHandler.getItemDef(id).getPictureMask();
		gameGraphics.spriteClip4(i, j, k, l, l1, i2, 0, 0, false);
	}

	protected final void handleServerMessage(String s) {
		if (s.startsWith("@bor@")) {
			displayMessage(s, 4, 0);
			return;
		}
		if (s.startsWith("@que@")) {
			displayMessage("@whi@" + s, 5, 0);
			return;
		}
		if (s.startsWith("@pri@")) {
			displayMessage(s, 6, 0);
			return;
		}
		displayMessage(s, 3, 0);
	}

	private final void checkMouseOverMenus()
	{
		if (mouseButtonClick == 1
				&& super.mouseX >= gameWindowMenuBarX + 5*gameWindowMenuBarItemWidth
				&& super.mouseY >= gameWindowMenuBarY
				&& super.mouseX < gameWindowMenuBarX + gameWindowMenuBarWidth
				&& super.mouseY < gameWindowMenuBarY + gameWindowMenuBarHeight)
		{ // iventory
			mouseOverMenu = mouseOverMenu != 1 ? 1 : 0;
			mouseButtonClick = 0;
		}
		if (super.mouseX >= gameWindowMenuBarX + 4*gameWindowMenuBarItemWidth
				&& super.mouseY >= gameWindowMenuBarY
				&& super.mouseX < gameWindowMenuBarX + 5*gameWindowMenuBarItemWidth
				&& super.mouseY < gameWindowMenuBarY + gameWindowMenuBarHeight)
		{ // map
			// we want to always show the minimap
			/*
        	mouseOverMenu = (mouseOverMenu != 2) ? 2 : 0;
            anInt985 = (int) (Math.random() * 13D) - 6;
            anInt986 = (int) (Math.random() * 23D) - 11;
			 */
			mouseButtonClick = 0;
		}
		if (mouseButtonClick == 1
				&& super.mouseX >= gameWindowMenuBarX + 3*gameWindowMenuBarItemWidth
				&& super.mouseY >= gameWindowMenuBarY
				&& super.mouseX < gameWindowMenuBarX + 4*gameWindowMenuBarItemWidth
				&& super.mouseY < gameWindowMenuBarY + gameWindowMenuBarHeight)
		{ // stats
			mouseOverMenu = mouseOverMenu != 3 ? 3 : 0;
			mouseButtonClick = 0;
		}
		if (mouseButtonClick == 1
				&& super.mouseX >= gameWindowMenuBarX + 2*gameWindowMenuBarItemWidth
				&& super.mouseY >= gameWindowMenuBarY
				&& super.mouseX < gameWindowMenuBarX + 3*gameWindowMenuBarItemWidth
				&& super.mouseY < gameWindowMenuBarY + gameWindowMenuBarHeight)
		{ // spells
			mouseOverMenu = mouseOverMenu != 4 ? 4 : 0;
			mouseButtonClick = 0;
		}
		if (mouseButtonClick == 1
				&& super.mouseX >= gameWindowMenuBarX + gameWindowMenuBarItemWidth
				&& super.mouseY >= gameWindowMenuBarY
				&& super.mouseX < gameWindowMenuBarX + 2*gameWindowMenuBarItemWidth
				&& super.mouseY < gameWindowMenuBarY + gameWindowMenuBarHeight)
		{ // friends
			mouseOverMenu = mouseOverMenu != 5 ? 5 : 0;
			mouseButtonClick = 0;
		}
		if (mouseButtonClick == 1
				&& super.mouseX >= gameWindowMenuBarX
				&& super.mouseY >= gameWindowMenuBarY
				&& super.mouseX < gameWindowMenuBarX + gameWindowMenuBarItemWidth
				&& super.mouseY < gameWindowMenuBarY + gameWindowMenuBarHeight)
		{ // settings when some menu is open
			mouseOverMenu = mouseOverMenu != 6 ? 6 : 0;
			mouseButtonClick = 0;
		}
	}

	private final void menuClick(int index)
	{
		int actionX = (int)rightClickMenu[index].actionX;
		int actionY = (int)rightClickMenu[index].actionY;
		int actionType = rightClickMenu[index].actionType;
		int actionVariable = rightClickMenu[index].actionVariable;
		int actionVariable2 = rightClickMenu[index].actionVariable2;
		int currentMenuID = rightClickMenu[index].id;
		if (currentMenuID == 200) {
			walkToGroundItem(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(104);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 210) {
			walkToGroundItem(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(34);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 220) {
			walkToGroundItem(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(245);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3200)
			displayMessage(EntityHandler.getItemDef(actionType).getDescription(), 3, 0);
		if (currentMenuID == 300) {
			walkToAction(actionX, actionY, actionType);
			super.streamClass.createPacket(67);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 310) {
			walkToAction(actionX, actionY, actionType);
			super.streamClass.createPacket(36);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 320) {
			walkToAction(actionX, actionY, actionType);
			super.streamClass.createPacket(126);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2300) {
			walkToAction(actionX, actionY, actionType);
			super.streamClass.createPacket(235);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.addByte(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3300)
			displayMessage(EntityHandler.getDoorDef(actionType).getDescription(), 3, 0);
		if (currentMenuID == 400) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(17);
			super.streamClass.add2ByteInt(actionVariable2);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 410) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(94);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.add2ByteInt(actionVariable2);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 420) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(51);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2400) {
			walkToObject(actionX, actionY, actionType, actionVariable);
			super.streamClass.createPacket(40);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3400)
			displayMessage(EntityHandler.getObjectDef(actionType).getDescription(), 3, 0);
		if (currentMenuID == 600) {
			super.streamClass.createPacket(49);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 610) {
			super.streamClass.createPacket(27);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 620) {
			super.streamClass.createPacket(92);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 630) {
			super.streamClass.createPacket(181);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 640) {
			super.streamClass.createPacket(89);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 650) {
			selectedItem = actionType;
			mouseOverMenu = 0;
			selectedItemName = inventory[selectedItem].name();
		}
		if (currentMenuID == 660) {
			super.streamClass.createPacket(147);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedItem = -1;
			mouseOverMenu = 0;
			displayMessage("Dropping " + inventory[actionType].name(), 4, 0);
		}
		if (currentMenuID == 3600)
			displayMessage(EntityHandler.getItemDef(actionType).getDescription(), 3, 0);
		if (currentMenuID == 700) {
			int l1 = xToTileX(actionX);
			int l3 = yToTileY(actionY);
			method112(sectionX, sectionY, l1, l3, true);
			super.streamClass.createPacket(71);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 710) {
			int i2 = xToTileX(actionX);
			int i4 = yToTileY(actionY);
			method112(sectionX, sectionY, i2, i4, true);
			super.streamClass.createPacket(142);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 720) {
			int j2 = xToTileX(actionX);
			int j4 = yToTileY(actionY);
			method112(sectionX, sectionY, j2, j4, true);
			super.streamClass.createPacket(177);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 725) {
			int k2 = xToTileX(actionX);
			int k4 = yToTileY(actionY);
			method112(sectionX, sectionY, k2, k4, true);
			super.streamClass.createPacket(74);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 715 || currentMenuID == 2715) {
			int l2 = xToTileX(actionX);
			int l4 = yToTileY(actionY);
			method112(sectionX, sectionY, l2, l4, true);
			super.streamClass.createPacket(73);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 3700)
			displayMessage(EntityHandler.getNpcDef(actionType).getDescription(), 3, 0);
		if (currentMenuID == 800) {
			int i3 = xToTileX(actionX);
			int i5 = yToTileY(actionY);
			method112(sectionX, sectionY, i3, i5, true);
			super.streamClass.createPacket(55);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 810) {
			int j3 = xToTileX(actionX);
			int j5 = yToTileY(actionY);
			method112(sectionX, sectionY, j3, j5, true);
			super.streamClass.createPacket(16);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionVariable);
			super.streamClass.formatPacket();
			selectedItem = -1;
		}
		if (currentMenuID == 805 || currentMenuID == 2805) {
			int k3 = xToTileX(actionX);
			int k5 = yToTileY(actionY);
			method112(sectionX, sectionY, k3, k5, true);
			super.streamClass.createPacket(57);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2806) {
			super.streamClass.createPacket(222);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2810) {
			super.streamClass.createPacket(166);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 2820) {
			super.streamClass.createPacket(68);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
		}
		if (currentMenuID == 900) {
			method112(sectionX, sectionY, actionX, actionY, true);
			super.streamClass.createPacket(232);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.add2ByteInt(actionX + areaX);
			super.streamClass.add2ByteInt(actionY + areaY);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 920) {
			method112(sectionX, sectionY, actionX, actionY, false);
			if (actionPictureType == -24)
				actionPictureType = 24;
		}
		if (currentMenuID == 1000) {
			super.streamClass.createPacket(206);
			super.streamClass.add2ByteInt(actionType);
			super.streamClass.formatPacket();
			selectedSpell = -1;
		}
		if (currentMenuID == 4000) {
			selectedItem = -1;
			selectedSpell = -1;
		}
	}

	final void method71(int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = anIntArray782[i1];
		int i2 = anIntArray923[i1];
		if (l1 == 0) {
			int j2 = 255 + i2 * 5 * 256;
			gameGraphics.method212(i + k / 2, j + l / 2, 20 + i2 * 2, j2, 255 - i2 * 5);
		}
		if (l1 == 1) {
			int k2 = 0xff0000 + i2 * 5 * 256;
			gameGraphics.method212(i + k / 2, j + l / 2, 10 + i2, k2, 255 - i2 * 5);
		}
	}

	protected final void method2() {
		if (memoryError)
			return;
		if (lastLoadedNull)
			return;
		try {
			loginTimer++;
			if (!loggedIn) {
				super.lastActionTimeout = 0;
				updateLoginScreen();
			}
			if (loggedIn) {
				super.lastActionTimeout++;
				processGame();
			}
			super.lastMouseDownButton = 0;
			super.keyDown2 = 0;
			screenRotationTimer++;
			if (screenRotationTimer > 500) {
				screenRotationTimer = 0;
				int i = (int) (Math.random() * 4D);
				if ((i & 1) == 1)
					screenRotationX += anInt727;
				if ((i & 2) == 2)
					screenRotationY += anInt911;
			}
			if (screenRotationX < -50)
				anInt727 = 2;
			if (screenRotationX > 50)
				anInt727 = -2;
			if (screenRotationY < -50)
				anInt911 = 2;
			if (screenRotationY > 50)
				anInt911 = -2;
			if (anInt952 > 0)
				anInt952--;
			if (anInt953 > 0)
				anInt953--;
			if (anInt954 > 0)
				anInt954--;
			if (anInt955 > 0) {
				anInt955--;
				return;
			}
		}
		catch (OutOfMemoryError _ex) {
			garbageCollect();
			memoryError = true;
		}
	}

	private final Model makeModel(int x, int y, int k, int l, int i1) {
		int modelX = x;
		int modelY = y;
		int modelX1 = x;
		int modelX2 = y;
		int texture1 = EntityHandler.getDoorDef(l).getTexture1();
		int texture2 = EntityHandler.getDoorDef(l).getTexture2();
		double height = EntityHandler.getDoorDef(l).getHeight();
		Model model = new Model(4, 1);
		if (k == 0)
			modelX1 = x + 1;
		if (k == 1)
			modelX2 = y + 1;
		if (k == 2) {
			modelX = x + 1;
			modelX2 = y + 1;
		}
		if (k == 3) {
			modelX1 = x + 1;
			modelX2 = y + 1;
		}
		modelX *= EngineHandle.GAME_SIZE;
		modelY *= EngineHandle.GAME_SIZE;
		modelX1 *= EngineHandle.GAME_SIZE;
		modelX2 *= EngineHandle.GAME_SIZE;
		int p0 = model.insertCoordPointNoDuplicate(modelX, -engineHandle.getAveragedElevation(modelX, modelY), modelY);
		int p1 = model.insertCoordPointNoDuplicate(modelX, -engineHandle.getAveragedElevation(modelX, modelY) - height, modelY);
		int p2 = model.insertCoordPointNoDuplicate(modelX1, -engineHandle.getAveragedElevation(modelX1, modelX2) - height, modelX2);
		int p3 = model.insertCoordPointNoDuplicate(modelX1, -engineHandle.getAveragedElevation(modelX1, modelX2), modelX2);
		int surface[] = {
				p0, p1, p2, p3
		};
		model.addSurface(4, surface, texture1, texture2);
		model.setLightAndGradAndSource(false, 60, 24, Camera.light_x, Camera.light_z, Camera.light_y);
		if (x >= 0 && y >= 0
				&& x < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_WIDTH
				&& y < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_HEIGHT)
		{
			gameCamera.addModel(model);
		}
		model.anInt257 = i1 + 10000;
		return model;
	}

	private final void resetLoginVars() {
		loggedIn = false;
		//loginScreenNumber = 0; // menuwelcome
		loginScreenNumber = 2;
		currentUser = "";
		currentPass = "";
		playerCount = 0;
		npcCount = 0;
	}

	private static final String method74(int i) {
		String s = String.valueOf(i);
		for (int j = s.length() - 3; j > 0; j -= 3)
			s = s.substring(0, j) + "," + s.substring(j);

		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		else if (s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return s;
	}

	private final void drawGame() {
		long now = System.currentTimeMillis();
		if (recording && now - lastFrame > (1000 / Config.MOVIE_FPS)) {
			try {
				lastFrame = now;
				frames.add(getImage());
			}
			catch (Exception e) {
			}
		}
		if (playerAliveTimeout != 0) {
			gameGraphics.fadePixels();
			gameGraphics.drawText("Oh dear! You are dead...", windowWidth / 2, windowHeight / 2, 7, 0xff0000);
			drawChatMessageTabs();
			gameGraphics.drawImage(aGraphics936, 0, 0);
			return;
		}
		if (showCharacterLookScreen) {
			method62();
			return;
		}
		if (!engineHandle.playerIsAlive) {
			return;
		}
		for (int i = 0; i < 64; i++)
		{ // draw other height sectors
			gameCamera.removeModel(engineHandle.roofs[sectorHeight][i]);
			if (sectorHeight == 0) {
				gameCamera.removeModel(engineHandle.walls[1][i]);
				gameCamera.removeModel(engineHandle.roofs[1][i]);
				gameCamera.removeModel(engineHandle.walls[2][i]);
				gameCamera.removeModel(engineHandle.roofs[2][i]);
			}
			zoomCamera = true;
			if (sectorHeight == 0 && (engineHandle.walkableValue[EngineHandle.getTileXFromX(ourPlayer.currentX)][EngineHandle.getTileYFromY(ourPlayer.currentY)] & EngineHandle.WALKABLE_INDOORS) == 0)
			{
				if (showRoof) {
					gameCamera.addModel(engineHandle.roofs[sectorHeight][i]);
					if (sectorHeight == 0) {
						gameCamera.addModel(engineHandle.walls[1][i]);
						gameCamera.addModel(engineHandle.roofs[1][i]);
						gameCamera.addModel(engineHandle.walls[2][i]);
						gameCamera.addModel(engineHandle.roofs[2][i]);
					}
				}
				zoomCamera = false;
			}
		}

		if (modelFireLightningSpellNumber != anInt742) {
			anInt742 = modelFireLightningSpellNumber;
			for (int j = 0; j < objectCount; j++) {
				if (objectType[j] == 97)
					animateObject(j, "firea" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 274)
					animateObject(j, "fireplacea" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 1031)
					animateObject(j, "lightning" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 1036)
					animateObject(j, "firespell" + (modelFireLightningSpellNumber + 1));
				if (objectType[j] == 1147)
					animateObject(j, "spellcharge" + (modelFireLightningSpellNumber + 1));
			}
		}
		if (modelTorchNumber != anInt743) {
			anInt743 = modelTorchNumber;
			for (int k = 0; k < objectCount; k++) {
				if (objectType[k] == 51)
					animateObject(k, "torcha" + (modelTorchNumber + 1));
				if (objectType[k] == 143)
					animateObject(k, "skulltorcha" + (modelTorchNumber + 1));
			}
		}
		if (modelClawSpellNumber != anInt744) {
			anInt744 = modelClawSpellNumber;
			for (int l = 0; l < objectCount; l++)
				if (objectType[l] == 1142)
					animateObject(l, "clawspell" + (modelClawSpellNumber + 1));
		}
		gameCamera.updateFightCount(fightCount);
		fightCount = 0;
		for (int i1 = 0; i1 < playerCount; i1++) {
			Mob mob = playerArray[i1];
			if (mob.colourBottomType != 255) {
				double x = mob.currentX;
				double y = mob.currentY;
				double z = -engineHandle.getAveragedElevation(x, y);
				double mobWidth = 145 * EngineHandle.SCALE_FACTOR;
				double mobHeight = 220 * EngineHandle.SCALE_FACTOR;
				int l3 = gameCamera.method268(5000 + i1, x, z, y, mobWidth, mobHeight, i1 + 10000);
				fightCount++;
				if (mob == ourPlayer)
					gameCamera.setOurPlayer(l3);
				if (mob.currentSprite == 8)
					gameCamera.setCombat(l3, -30);
				if (mob.currentSprite == 9)
					gameCamera.setCombat(l3, 30);
			}
		}

		for (int j1 = 0; j1 < playerCount; j1++) {
			Mob player = playerArray[j1];
			if (player.anInt176 > 0) {
				Mob npc = null;
				if (player.attackingNpcIndex != -1)
					npc = npcRecordArray[player.attackingNpcIndex];
				else if (player.attackingMobIndex != -1)
					npc = mobArray[player.attackingMobIndex];
				if (npc != null) {
					double px = player.currentX;
					double py = player.currentY;
					double pz = -engineHandle.getAveragedElevation(px, py) - 110;
					double nx = npc.currentX;
					double ny = npc.currentY;
					double nz = -engineHandle.getAveragedElevation(nx, ny) - EntityHandler.getNpcDef(npc.type).getCamera2() / 2;
					double i10 = (px * player.anInt176 + nx * (attackingInt40 - player.anInt176)) / attackingInt40;
					double j10 = (pz * player.anInt176 + nz * (attackingInt40 - player.anInt176)) / attackingInt40;
					double k10 = (py * player.anInt176 + ny * (attackingInt40 - player.anInt176)) / attackingInt40;
					double mobWidth = 32 * EngineHandle.SCALE_FACTOR;
					double mobHeight = 32 * EngineHandle.SCALE_FACTOR;
					gameCamera.method268(SPRITE_PROJECTILE_START + player.attackingCameraInt,
							i10, j10, k10, mobWidth, mobHeight, 0);
					fightCount++;
				}
			}
		}

		for (int l1 = 0; l1 < npcCount; l1++) {
			Mob npc = npcArray[l1];
			double mobx = npc.currentX;
			double moby = npc.currentY;
			double mobz = -engineHandle.getAveragedElevation(mobx, moby);
			double mobWidth = EntityHandler.getNpcDef(npc.type).getCamera1() * EngineHandle.SCALE_FACTOR;
			double mobHeight = EntityHandler.getNpcDef(npc.type).getCamera2() * EngineHandle.SCALE_FACTOR;
			int i9 = gameCamera.method268(20000 + l1, mobx, mobz, moby, mobWidth, mobHeight, l1 + 30000);
			fightCount++;
			if (npc.currentSprite == 8)
				gameCamera.setCombat(i9, -30);
			if (npc.currentSprite == 9)
				gameCamera.setCombat(i9, 30);
		}
		for (int j2 = 0; j2 < groundItemCount; j2++) {
			double x = groundItemX[j2] * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
			double y = groundItemY[j2] * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
			double mobWidth = 96 * EngineHandle.SCALE_FACTOR;
			double mobHeight = 64 * EngineHandle.SCALE_FACTOR;
			gameCamera.method268(40000 + groundItemType[j2], x,
					-engineHandle.getAveragedElevation(x, y) - groundItemZ[j2],
					y, mobWidth, mobHeight, j2 + 20000);
			fightCount++;
		}

		for (int k3 = 0; k3 < anInt892; k3++) {
			double x = anIntArray944[k3] * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
			double y = anIntArray757[k3] * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
			int j9 = anIntArray782[k3];
			double mobWidth = 128 * EngineHandle.SCALE_FACTOR;
			double mobHeight = 0;
			if (j9 == 0)
				mobHeight = 256 * EngineHandle.SCALE_FACTOR;
			if (j9 == 1)
				mobHeight = 64 * EngineHandle.SCALE_FACTOR;
			gameCamera.method268(50000 + k3, x, -engineHandle.getAveragedElevation(x, y),
					y, mobWidth, mobHeight, k3 + 50000);
			fightCount++;
		}

		gameGraphics.f1Toggle = false;
		if (sectorHeight == 3)
			gameGraphics.resetImagePixels(0);
		else
			gameGraphics.resetImagePixels(GameImage.BACKGROUND);
		gameGraphics.f1Toggle = super.keyF1Toggle;
		if (sectorHeight == 3)
		{ // underground, flickering light
			int globalLight = 40 + (int) (Math.random() * 3D);
			int featureLight = 40 + (int) (Math.random() * 7D);
			gameCamera.setLightAndSource(globalLight, featureLight, Camera.light_x, Camera.light_z, Camera.light_y);
		}
		anInt699 = 0;
		mobMessageCount = 0;
		anInt718 = 0;
		if (freeCamera)
		{
			handleCharacterControlBinds();
		}
		if (cameraAutoAngleDebug) {
			if (configAutoCameraAngle && !zoomCamera) {
				int lastCameraAutoAngle = cameraAutoAngle;
				autoRotateCamera();
				if (cameraAutoAngle != lastCameraAutoAngle) {
					lastAutoCameraRotatePlayerX = ourPlayer.currentX;
					lastAutoCameraRotatePlayerY = ourPlayer.currentY;
				}
			}
			gameCamera.drawModelMaxDist = 3000 * EngineHandle.SCALE_FACTOR;
			gameCamera.drawSpriteMaxDist = 3000 * EngineHandle.SCALE_FACTOR;
			gameCamera.fadeFactor = 1 * EngineHandle.SCALE_FACTOR;
			gameCamera.fadeDist = 2800 * EngineHandle.SCALE_FACTOR;
			cameraZRot = cameraAutoAngle * 32;
			double plrX = lastAutoCameraRotatePlayerX + screenRotationX;
			double plrY = lastAutoCameraRotatePlayerY + screenRotationY;
			gameCamera.setCamera(plrX, -engineHandle.getAveragedElevation(plrX, plrY), plrY, cameraXRot, cameraZRot * 4, 0, 2000 * EngineHandle.SCALE_FACTOR, cameraZoom);
		} else {
			if (configAutoCameraAngle && !zoomCamera)
				autoRotateCamera();
			if (!super.keyF1Toggle) {
				//TODO: clean this up. these values are changed when the mouse wheel moves
				//gameCamera.drawModelMaxDist = 2400;
				//gameCamera.drawSpriteMaxDist = 2400;
				gameCamera.fadeFactor = 1;
				//gameCamera.fadeDist = 2300;
			} else {
				gameCamera.drawModelMaxDist = 2200 * EngineHandle.SCALE_FACTOR;
				gameCamera.drawSpriteMaxDist = 2200 * EngineHandle.SCALE_FACTOR;
				gameCamera.fadeFactor = 1;
				gameCamera.fadeDist = 2100 * EngineHandle.SCALE_FACTOR;
			}
			double l5 = lastAutoCameraRotatePlayerX + screenRotationX;
			double i8 = lastAutoCameraRotatePlayerY + screenRotationY;
			gameCamera.setCamera(l5, -engineHandle.getAveragedElevation(l5, i8), i8, cameraXRot, cameraZRot * 4, 0, 2*cameraHeight * EngineHandle.SCALE_FACTOR, cameraZoom);
		}
		gameCamera.finishCamera();
		method119();
		if (actionPictureType > 0)
			gameGraphics.drawPicture(actionPictureX - 8, actionPictureY - 8, SPRITE_MEDIA_START + 14 + (24 - actionPictureType) / 6);
		if (actionPictureType < 0)
			gameGraphics.drawPicture(actionPictureX - 8, actionPictureY - 8, SPRITE_MEDIA_START + 18 + (24 + actionPictureType) / 6);
		if (systemUpdate != 0) {
			int i6 = systemUpdate / 50;
			int j8 = i6 / 60;
			i6 %= 60;
			if (i6 < 10)
				gameGraphics.drawText("System update in: " + j8 + ":0" + i6, 256, windowHeight - 7, 1, 0xffff00);
			else
				gameGraphics.drawText("System update in: " + j8 + ":" + i6, 256, windowHeight - 7, 1, 0xffff00);
		}
		if (!notInWilderness) {
			int j6 = 2203 - (sectionY + wildY + areaY);
			if (sectionX + wildX + areaX >= 2640)
				j6 = -50;
			if (j6 > 0) {
				int k8 = 1 + j6 / 6;
				int wildysignX = windowWidth - 50;
				int wildysignY = miniMapY + miniMapHeight + 3;
				gameGraphics.drawPicture(wildysignX, wildysignY, SPRITE_MEDIA_START + 13);
				gameGraphics.drawText("Wilderness", wildysignX + 12, wildysignY + 36, 1, 0xffff00);
				gameGraphics.drawText("Level: " + k8, wildysignX + 12, wildysignY + 49, 1, 0xffff00);
				if (wildernessType == 0)
					wildernessType = 2;
			}
			if (wildernessType == 0 && j6 > -10 && j6 <= 0)
				wildernessType = 1;
		}
		if (messagesTab == 0)
		{
			gameGraphics.drawBoxAlpha(chatBoxX, chatBoxY, chatBoxWidth, chatBoxHeight, 0x232323, 0xc0);
			gameGraphics.drawBoxEdge(chatBoxX, chatBoxY, chatBoxWidth, chatBoxHeight, 0x000000);
			for (int k6 = 0; k6 < chatBoxVisRows; k6++)
				if (messagesTimeout[k6] > 0) {
					String s = messagesArray[k6];
					gameGraphics.drawString(s, 7, chatBoxY + chatBoxHeight-4 - k6 * 14, 1, 0xffff00);
				}

		}
		gameGraphics.drawBoxAlpha(chatPlayerEntryX, chatPlayerEntryY, chatPlayerEntryWidth, chatPlayerEntryHeight, 0x232323, 0xc0);
		gameGraphics.drawBoxEdge(chatPlayerEntryX, chatPlayerEntryY, chatPlayerEntryWidth, chatPlayerEntryHeight, 0x000000);

		gameMenu.method171(messagesHandleChatHist);
		gameMenu.method171(messagesHandleQuestHist);
		gameMenu.method171(messagesHandlePrivHist);
		if (messagesTab == 1)
			gameMenu.method170(messagesHandleChatHist);
		else if (messagesTab == 2)
			gameMenu.method170(messagesHandleQuestHist);
		else if (messagesTab == 3)
			gameMenu.method170(messagesHandlePrivHist);
		Menu.anInt225 = 2;
		gameMenu.drawMenu(isTyping);
		drawMapMenu(true);
		Menu.anInt225 = 0;
		gameGraphics.method232(gameWindowMenuBarX, gameWindowMenuBarY,
				SPRITE_MEDIA_START, 0x80);
		drawGameWindowsMenus();
		gameGraphics.drawStringShadows = false;
		drawChatMessageTabs();
		gameGraphics.drawImage(aGraphics936, 0, 0);
	}

	private final void drawRightClickMenu() {
		if (mouseButtonClick != 0) {
			for (int i = 0; i < menuLength; i++) {
				int k = MenuRightClick.menuX + 2;
				int i1 = MenuRightClick.menuY + 27 + i * 15;
				if (super.mouseX <= k - 2
						|| super.mouseY <= i1 - 12
						|| super.mouseY >= i1 + 4 
						|| super.mouseX >= (k - 3) + MenuRightClick.menuWidth)
					continue;
				menuClick(rightClickMenu[i].indices);
				break;
			}

			mouseButtonClick = 0;
			showRightClickMenu = false;
			return;
		}
		if (super.mouseX < MenuRightClick.menuX - 10
				|| super.mouseY < MenuRightClick.menuY - 10
				|| super.mouseX > MenuRightClick.menuX + MenuRightClick.menuWidth + 10 
				|| super.mouseY > MenuRightClick.menuY + MenuRightClick.menuHeight + 10) {
			showRightClickMenu = false;
			return;
		}
		gameGraphics.drawBoxAlpha(MenuRightClick.menuX, MenuRightClick.menuY, MenuRightClick.menuWidth, MenuRightClick.menuHeight, 0xd0d0d0, 160);
		gameGraphics.drawString("Choose option", MenuRightClick.menuX + 2, MenuRightClick.menuY + 12, 1, 65535);
		for (int j = 0; j < menuLength; j++) {
			int l = MenuRightClick.menuX + 2;
			int j1 = MenuRightClick.menuY + 27 + j * 15;
			int k1 = 0xffffff;
			if (super.mouseX > l - 2
					&& super.mouseY > j1 - 12
					&& super.mouseY < j1 + 4
					&& super.mouseX < (l - 3) + MenuRightClick.menuWidth)
				k1 = 0xffff00;
			gameGraphics.drawString(rightClickMenu[rightClickMenu[j].indices].text1 + " " + rightClickMenu[rightClickMenu[j].indices].text2, l, j1, 1, k1);
		}

	}

	protected final void resetIntVars() {
		systemUpdate = 0;
		//loginScreenNumber = 0; // menuwelcome
		loginScreenNumber = 2;
		loggedIn = false;
		logoutTimeout = 0;
	}

	private final void drawQuestionMenu() {
		if (mouseButtonClick != 0) {
			for (int i = 0; i < questionMenuCount; i++) {
				if (super.mouseX >= gameGraphics.textWidth(questionMenuAnswer[i], 1) || super.mouseY <= i * 12 || super.mouseY >= 12 + i * 12)
					continue;
				super.streamClass.createPacket(154);
				super.streamClass.addByte(i);
				super.streamClass.formatPacket();
				break;
			}

			mouseButtonClick = 0;
			showQuestionMenu = false;
			return;
		}
		for (int j = 0; j < questionMenuCount; j++) {
			int k = 65535;
			if (super.mouseX < gameGraphics.textWidth(questionMenuAnswer[j], 1) && super.mouseY > j * 12 && super.mouseY < 12 + j * 12)
				k = 0xff0000;
			gameGraphics.drawString(questionMenuAnswer[j], 6, 12 + j * 12, 1, k);
		}

	}

	private final void walkToAction(int actionX, int actionY, int actionType) {
		if (actionType == 0) {
			sendWalkCommand(sectionX, sectionY, actionX, actionY - 1, actionX, actionY, false, true);
			return;
		}
		if (actionType == 1) {
			sendWalkCommand(sectionX, sectionY, actionX - 1, actionY, actionX, actionY, false, true);
			return;
		} else {
			sendWalkCommand(sectionX, sectionY, actionX, actionY, actionX, actionY, true, true);
			return;
		}
	}

	private final void garbageCollect() {
		try {
			if (gameGraphics != null) {
				gameGraphics.cleanupSprites();
				gameGraphics.imagePixelArray = null;
				gameGraphics = null;
			}
			if (gameCamera != null) {
				gameCamera.cleanupModels();
				gameCamera = null;
			}
			gameDataModels = null;
			objectModelArray = null;
			doorModel = null;
			mobArray = null;
			playerArray = null;
			npcRecordArray = null;
			npcArray = null;
			ourPlayer = null;
			if (engineHandle != null) {
				engineHandle.aModelArray596 = null;
				engineHandle.walls = null;
				engineHandle.roofs = null;
				engineHandle.aModel = null;
				engineHandle = null;
			}
			System.gc();
			return;
		}
		catch (Exception _ex) {
			return;
		}
	}

	protected final void loginScreenPrint(String s, String s1) {
		if (loginScreenNumber == 1)
		{
			menuNewUser.updateText(anInt900, s + " " + s1);
		}
		if (loginScreenNumber == 2)
		{
			menuLogin.updateText(loginStatusText, s + " " + s1);
		}
		drawLoginScreen();
		resetCurrentTimeArray();
	}

	private final void drawInventoryRightClickMenu()
	{
		int i = 2203 - (sectionY + wildY + areaY);
		if (sectionX + wildX + areaX >= 2640)
			i = -50;
		int j = -1;
		for (int k = 0; k < objectCount; k++)
			aBooleanArray827[k] = false;

		for (int l = 0; l < doorCount; l++)
			aBooleanArray970[l] = false;

		int i1 = gameCamera.method272();
		Model models[] = gameCamera.getVisibleModels();
		int ai[] = gameCamera.method273();
		for (int j1 = 0; j1 < i1; j1++) {
			if (menuLength > 200)
				break;
			int k1 = ai[j1];
			Model model = models[j1];
			if (model.entityType[k1] <= 65535 || model.entityType[k1] >= 0x30d40 && model.entityType[k1] <= 0x493e0)
				if (model == gameCamera.aModel) {
					int i2 = model.entityType[k1] % 10000;
					int l2 = model.entityType[k1] / 10000;
					if (l2 == 1) {
						String s = "";
						int k3 = 0;
						if (ourPlayer.level > 0 && playerArray[i2].level > 0)
							k3 = ourPlayer.level - playerArray[i2].level;
						if (k3 < 0)
							s = "@or1@";
						if (k3 < -3)
							s = "@or2@";
						if (k3 < -6)
							s = "@or3@";
						if (k3 < -9)
							s = "@red@";
						if (k3 > 0)
							s = "@gr1@";
						if (k3 > 3)
							s = "@gr2@";
						if (k3 > 6)
							s = "@gr3@";
						if (k3 > 9)
							s = "@gre@";
						s = " " + s + "(level-" + playerArray[i2].level + ")";
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 1 || EntityHandler.getSpellDef(selectedSpell).getSpellType() == 2) {
								rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on";
								rightClickMenu[menuLength].text2 = "@whi@" + playerArray[i2].name + s;
								rightClickMenu[menuLength].id = 800;
								rightClickMenu[menuLength].actionX = playerArray[i2].currentX;
								rightClickMenu[menuLength].actionY = playerArray[i2].currentY;
								rightClickMenu[menuLength].actionType = playerArray[i2].serverIndex;
								rightClickMenu[menuLength].actionVariable = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							rightClickMenu[menuLength].text1 = "Use " + selectedItemName + " with";
							rightClickMenu[menuLength].text2 = "@whi@" + playerArray[i2].name + s;
							rightClickMenu[menuLength].id = 810;
							rightClickMenu[menuLength].actionX = playerArray[i2].currentX;
							rightClickMenu[menuLength].actionY = playerArray[i2].currentY;
							rightClickMenu[menuLength].actionType = playerArray[i2].serverIndex;
							rightClickMenu[menuLength].actionVariable = selectedItem;
							menuLength++;
						} else {
							if (i > 0 && (playerArray[i2].currentY - EngineHandle.GAME_SIZE/2) / EngineHandle.GAME_SIZE + wildY + areaY < 2203) {
								rightClickMenu[menuLength].text1 = "Attack";
								rightClickMenu[menuLength].text2 = "@whi@" + playerArray[i2].name + s;
								if (k3 >= 0 && k3 < 5)
									rightClickMenu[menuLength].id = 805;
								else
									rightClickMenu[menuLength].id = 2805;
								rightClickMenu[menuLength].actionX = playerArray[i2].currentX;
								rightClickMenu[menuLength].actionY = playerArray[i2].currentY;
								rightClickMenu[menuLength].actionType = playerArray[i2].serverIndex;
								menuLength++;
							} else {
								rightClickMenu[menuLength].text1 = "Duel with";
								rightClickMenu[menuLength].text2 = "@whi@" + playerArray[i2].name + s;
								rightClickMenu[menuLength].actionX = playerArray[i2].currentX;
								rightClickMenu[menuLength].actionY = playerArray[i2].currentY;
								rightClickMenu[menuLength].id = 2806;
								rightClickMenu[menuLength].actionType = playerArray[i2].serverIndex;
								menuLength++;
							}
							rightClickMenu[menuLength].text1 = "Trade with";
							rightClickMenu[menuLength].text2 = "@whi@" + playerArray[i2].name + s;
							rightClickMenu[menuLength].id = 2810;
							rightClickMenu[menuLength].actionType = playerArray[i2].serverIndex;
							menuLength++;
							rightClickMenu[menuLength].text1 = "Follow";
							rightClickMenu[menuLength].text2 = "@whi@" + playerArray[i2].name + s;
							rightClickMenu[menuLength].id = 2820;
							rightClickMenu[menuLength].actionType = playerArray[i2].serverIndex;
							menuLength++;
						}
					} else if (l2 == 2) {
						ItemDef itemDef = EntityHandler.getItemDef(groundItemType[i2]);
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 3) {
								rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on";
								rightClickMenu[menuLength].text2 = "@lre@" + itemDef.getName();
								rightClickMenu[menuLength].id = 200;
								rightClickMenu[menuLength].actionX = groundItemX[i2];
								rightClickMenu[menuLength].actionY = groundItemY[i2];
								rightClickMenu[menuLength].actionType = groundItemType[i2];
								rightClickMenu[menuLength].actionVariable = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							rightClickMenu[menuLength].text1 = "Use " + selectedItemName + " with";
							rightClickMenu[menuLength].text2 = "@lre@" + itemDef.getName();
							rightClickMenu[menuLength].id = 210;
							rightClickMenu[menuLength].actionX = groundItemX[i2];
							rightClickMenu[menuLength].actionY = groundItemY[i2];
							rightClickMenu[menuLength].actionType = groundItemType[i2];
							rightClickMenu[menuLength].actionVariable = selectedItem;
							menuLength++;
						} else {
							rightClickMenu[menuLength].text1 = "Take";
							rightClickMenu[menuLength].text2 = "@lre@" + itemDef.getName();
							rightClickMenu[menuLength].id = 220;
							rightClickMenu[menuLength].actionX = groundItemX[i2];
							rightClickMenu[menuLength].actionY = groundItemY[i2];
							rightClickMenu[menuLength].actionType = groundItemType[i2];
							menuLength++;
							rightClickMenu[menuLength].text1 = "Examine";
							rightClickMenu[menuLength].text2 = "@lre@" + itemDef.getName() + (ourPlayer.admin >= 2 ? " @or1@(" + groundItemType[i2] + ":" + (groundItemX[i2] + areaX) + "," + (groundItemY[i2] + areaY) + ")" : "");
							rightClickMenu[menuLength].id = 3200;
							rightClickMenu[menuLength].actionType = groundItemType[i2];
							menuLength++;
						}
					} else if (l2 == 3) {
						String s1 = "";
						int l3 = -1;
						NPCDef npcDef = EntityHandler.getNpcDef(npcArray[i2].type);
						if (npcDef.isAttackable()) {
							int j4 = (npcDef.getAtt() + npcDef.getDef() + npcDef.getStr() + npcDef.getHits()) / 4;
							int k4 = (playerStatBase[0] + playerStatBase[1] + playerStatBase[2] + playerStatBase[3] + 27) / 4;
							l3 = k4 - j4;
							s1 = "@yel@";
							if (l3 < 0)
								s1 = "@or1@";
							if (l3 < -3)
								s1 = "@or2@";
							if (l3 < -6)
								s1 = "@or3@";
							if (l3 < -9)
								s1 = "@red@";
							if (l3 > 0)
								s1 = "@gr1@";
							if (l3 > 3)
								s1 = "@gr2@";
							if (l3 > 6)
								s1 = "@gr3@";
							if (l3 > 9)
								s1 = "@gre@";
							s1 = " " + s1 + "(level-" + j4 + ")";
						}
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 2) {
								rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on";
								rightClickMenu[menuLength].text2 = "@yel@" + npcDef.getName();
								rightClickMenu[menuLength].id = 700;
								rightClickMenu[menuLength].actionX = npcArray[i2].currentX;
								rightClickMenu[menuLength].actionY = npcArray[i2].currentY;
								rightClickMenu[menuLength].actionType = npcArray[i2].serverIndex;
								rightClickMenu[menuLength].actionVariable = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							rightClickMenu[menuLength].text1 = "Use " + selectedItemName + " with";
							rightClickMenu[menuLength].text2 = "@yel@" + npcDef.getName();
							rightClickMenu[menuLength].id = 710;
							rightClickMenu[menuLength].actionX = npcArray[i2].currentX;
							rightClickMenu[menuLength].actionY = npcArray[i2].currentY;
							rightClickMenu[menuLength].actionType = npcArray[i2].serverIndex;
							rightClickMenu[menuLength].actionVariable = selectedItem;
							menuLength++;
						} else {
							if (npcDef.isAttackable()) {
								rightClickMenu[menuLength].text1 = "Attack";
								rightClickMenu[menuLength].text2 = "@yel@" + npcDef.getName() + s1;
								if (l3 >= 0)
									rightClickMenu[menuLength].id = 715;
								else
									rightClickMenu[menuLength].id = 2715;
								rightClickMenu[menuLength].actionX = npcArray[i2].currentX;
								rightClickMenu[menuLength].actionY = npcArray[i2].currentY;
								rightClickMenu[menuLength].actionType = npcArray[i2].serverIndex;
								menuLength++;
							}
							rightClickMenu[menuLength].text1 = "Talk-to";
							rightClickMenu[menuLength].text2 = "@yel@" + npcDef.getName();
							rightClickMenu[menuLength].id = 720;
							rightClickMenu[menuLength].actionX = npcArray[i2].currentX;
							rightClickMenu[menuLength].actionY = npcArray[i2].currentY;
							rightClickMenu[menuLength].actionType = npcArray[i2].serverIndex;
							menuLength++;
							if (!npcDef.getCommand().equals("")) {
								rightClickMenu[menuLength].text1 = npcDef.getCommand();
								rightClickMenu[menuLength].text2 = "@yel@" + npcDef.getName();
								rightClickMenu[menuLength].id = 725;
								rightClickMenu[menuLength].actionX = npcArray[i2].currentX;
								rightClickMenu[menuLength].actionY = npcArray[i2].currentY;
								rightClickMenu[menuLength].actionType = npcArray[i2].serverIndex;
								menuLength++;
							}
							rightClickMenu[menuLength].text1 = "Examine";
							rightClickMenu[menuLength].text2 = "@yel@" + npcDef.getName() + (ourPlayer.admin >= 2 ? " @or1@(" + npcArray[i2].type + ")" : "");
							rightClickMenu[menuLength].id = 3700;
							rightClickMenu[menuLength].actionType = npcArray[i2].type;
							menuLength++;
						}
					}
				} else if (model != null && model.anInt257 >= 10000) {
					int j2 = model.anInt257 - 10000;
					int i3 = doorType[j2];
					if (!aBooleanArray970[j2]) {
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 4) {
								rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on";
								rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getDoorDef(i3).getName();
								rightClickMenu[menuLength].id = 300;
								rightClickMenu[menuLength].actionX = doorX[j2];
								rightClickMenu[menuLength].actionY = doorY[j2];
								rightClickMenu[menuLength].actionType = doorDirection[j2];
								rightClickMenu[menuLength].actionVariable = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							rightClickMenu[menuLength].text1 = "Use " + selectedItemName + " with";
							rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getDoorDef(i3).getName();
							rightClickMenu[menuLength].id = 310;
							rightClickMenu[menuLength].actionX = doorX[j2];
							rightClickMenu[menuLength].actionY = doorY[j2];
							rightClickMenu[menuLength].actionType = doorDirection[j2];
							rightClickMenu[menuLength].actionVariable = selectedItem;
							menuLength++;
						} else {
							if (!EntityHandler.getDoorDef(i3).getCommand1().equalsIgnoreCase("WalkTo")) {
								rightClickMenu[menuLength].text1 = EntityHandler.getDoorDef(i3).getCommand1();
								rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getDoorDef(i3).getName();
								rightClickMenu[menuLength].id = 320;
								rightClickMenu[menuLength].actionX = doorX[j2];
								rightClickMenu[menuLength].actionY = doorY[j2];
								rightClickMenu[menuLength].actionType = doorDirection[j2];
								menuLength++;
							}
							if (!EntityHandler.getDoorDef(i3).getCommand2().equalsIgnoreCase("Examine")) {
								rightClickMenu[menuLength].text1 = EntityHandler.getDoorDef(i3).getCommand2();
								rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getDoorDef(i3).getName();
								rightClickMenu[menuLength].id = 2300;
								rightClickMenu[menuLength].actionX = doorX[j2];
								rightClickMenu[menuLength].actionY = doorY[j2];
								rightClickMenu[menuLength].actionType = doorDirection[j2];
								menuLength++;
							}
							rightClickMenu[menuLength].text1 = "Examine";
							rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getDoorDef(i3).getName() + (ourPlayer.admin >= 2 ? " @or1@(" + i3 + ":" + (doorX[j2] + areaX) + "," + (doorY[j2] + areaY) + ")" : "");
							rightClickMenu[menuLength].id = 3300;
							rightClickMenu[menuLength].actionType = i3;
							menuLength++;
						}
						aBooleanArray970[j2] = true;
					}
				} else if (model != null && model.anInt257 >= 0) {
					int k2 = model.anInt257;
					int j3 = objectType[k2];
					if (!aBooleanArray827[k2]) {
						if (selectedSpell >= 0) {
							if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 5) {
								rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on";
								rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getObjectDef(j3).getName();
								rightClickMenu[menuLength].id = 400;
								rightClickMenu[menuLength].actionX = objectX[k2];
								rightClickMenu[menuLength].actionY = objectY[k2];
								rightClickMenu[menuLength].actionType = objectID[k2];
								rightClickMenu[menuLength].actionVariable = objectType[k2];
								rightClickMenu[menuLength].actionVariable2 = selectedSpell;
								menuLength++;
							}
						} else if (selectedItem >= 0) {
							rightClickMenu[menuLength].text1 = "Use " + selectedItemName + " with";
							rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getObjectDef(j3).getName();
							rightClickMenu[menuLength].id = 410;
							rightClickMenu[menuLength].actionX = objectX[k2];
							rightClickMenu[menuLength].actionY = objectY[k2];
							rightClickMenu[menuLength].actionType = objectID[k2];
							rightClickMenu[menuLength].actionVariable = objectType[k2];
							rightClickMenu[menuLength].actionVariable2 = selectedItem;
							menuLength++;
						} else {
							if (!EntityHandler.getObjectDef(j3).getCommand1().equalsIgnoreCase("WalkTo")) {
								rightClickMenu[menuLength].text1 = EntityHandler.getObjectDef(j3).getCommand1();
								rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getObjectDef(j3).getName();
								rightClickMenu[menuLength].id = 420;
								rightClickMenu[menuLength].actionX = objectX[k2];
								rightClickMenu[menuLength].actionY = objectY[k2];
								rightClickMenu[menuLength].actionType = objectID[k2];
								rightClickMenu[menuLength].actionVariable = objectType[k2];
								menuLength++;
							}
							if (!EntityHandler.getObjectDef(j3).getCommand2().equalsIgnoreCase("Examine")) {
								rightClickMenu[menuLength].text1 = EntityHandler.getObjectDef(j3).getCommand2();
								rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getObjectDef(j3).getName();
								rightClickMenu[menuLength].id = 2400;
								rightClickMenu[menuLength].actionX = objectX[k2];
								rightClickMenu[menuLength].actionY = objectY[k2];
								rightClickMenu[menuLength].actionType = objectID[k2];
								rightClickMenu[menuLength].actionVariable = objectType[k2];
								menuLength++;
							}
							rightClickMenu[menuLength].text1 = "Examine";
							rightClickMenu[menuLength].text2 = "@cya@" + EntityHandler.getObjectDef(j3).getName() + (ourPlayer.admin >= 2 ? " @or1@(" + j3 + ":" + (objectX[k2] + areaX) + "," + (objectY[k2] + areaY) + ")" : "");
							rightClickMenu[menuLength].id = 3400;
							rightClickMenu[menuLength].actionType = j3;
							menuLength++;
						}
						aBooleanArray827[k2] = true;
					}
				} else {
					if (k1 >= 0)
						k1 = model.entityType[k1] - 0x30d40;
					if (k1 >= 0)
						j = k1;
				}
		}

		if (selectedSpell >= 0 && EntityHandler.getSpellDef(selectedSpell).getSpellType() <= 1) {
			rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on self";
			rightClickMenu[menuLength].text2 = "";
			rightClickMenu[menuLength].id = 1000;
			rightClickMenu[menuLength].actionType = selectedSpell;
			menuLength++;
		}
		if (j != -1) {
			int l1 = j;
			if (selectedSpell >= 0) {
				if (EntityHandler.getSpellDef(selectedSpell).getSpellType() == 6) {
					rightClickMenu[menuLength].text1 = "Cast " + EntityHandler.getSpellDef(selectedSpell).getName() + " on ground";
					rightClickMenu[menuLength].text2 = "";
					rightClickMenu[menuLength].id = 900;
					rightClickMenu[menuLength].actionX = engineHandle.selectedX[l1];
					rightClickMenu[menuLength].actionY = engineHandle.selectedY[l1];
					rightClickMenu[menuLength].actionType = selectedSpell;
					menuLength++;
					return;
				}
			} else if (selectedItem < 0) {
				rightClickMenu[menuLength].text1 = "Walk here";
				rightClickMenu[menuLength].text2 = "";
				rightClickMenu[menuLength].id = 920;
				rightClickMenu[menuLength].actionX = engineHandle.selectedX[l1];
				rightClickMenu[menuLength].actionY = engineHandle.selectedY[l1];
				menuLength++;
			}
		}
	}

	protected final void startGame() {
		int i = 0;
		for (int j = 0; j < 99; j++) {
			int k = j + 1;
			int i1 = (int) ((double) k + 300D * Math.pow(2D, (double) k / 7D));
			i += i1;
			experienceArray[j] = (i & 0xffffffc) / 4;
		}
		super.yOffset = 0;
		GameWindowMiddleMan.maxPacketReadCount = 1000;
		loadConfigFilter(); // 15%
		if (lastLoadedNull) {
			return;
		}
		aGraphics936 = getGraphics();
		changeThreadSleepModifier(50);
		gameGraphics = new GameImageMiddleMan(windowWidth, windowHeight + 12, 4000, this);
		gameGraphics._mudclient = this;
		gameGraphics.setDimensions(0, 0, windowWidth, windowHeight + 12);
		abWin = new AbuseWindow(windowHalfWidth, windowHalfHeight);
		bankPan = new BankPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		tradePan = new TradePanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		tradeCfrmPan = new TradeConfirmPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		invPan = new InventoryPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		plrPan = new PlayerInfoPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		magicPan = new MagicPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		friendPan = new FriendsPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		optPan = new OptionsPanel(windowHalfWidth, windowHalfHeight, gameGraphics);
		Menu.aBoolean220 = false;
		spellMenu = new Menu(gameGraphics, 5);
		spellMenuHandle = spellMenu.method162(magicPan.getX(),
				magicPan.getY() + magicPan.getTabHeight()+1,
				magicPan.getWidth(), magicPan.getScrollBoxHeight(), 1, 500, true);
		friendsMenu = new Menu(gameGraphics, 5);
		friendsMenuHandle = friendsMenu.method162(friendPan.getX(),
				friendPan.getY() + friendPan.getTabHeight() + friendPan.getScrollBoxTitleHeight(),
				friendPan.getWidth(), friendPan.getScrollBoxHeight(), 1, 500, true);
		questMenu = new Menu(gameGraphics, 5);
		questMenuHandle = questMenu.method162(plrPan.getX(),
				plrPan.getY() + plrPan.getTabHeight() + plrPan.getScrollBoxTitleHeight(),
				plrPan.getWidth(), plrPan.getScrollBoxHeight(), 1, 500, true);
		loadMedia(); // 30%
		tradePan.addAcceptButton(((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 25], SPRITE_MEDIA_START + 25);
		tradePan.addDeclineButton(((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 26], SPRITE_MEDIA_START + 26);
		tradeCfrmPan.addAcceptButton(((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 25], SPRITE_MEDIA_START + 25);
		tradeCfrmPan.addDeclineButton(((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 26], SPRITE_MEDIA_START + 26);
		if (lastLoadedNull)
			return;
		loadEntity(); // 45%
		if (lastLoadedNull)
			return;
		gameCamera = new Camera(this, gameGraphics, 15000, 15000, 1000);
		gameCamera.setCameraSize(windowWidth / 2, windowHeight / 2, windowWidth / 2,
				windowHeight / 2, windowWidth, cameraSizeInt);
		gameCamera.drawModelMaxDist = viewDistance;
		gameCamera.drawSpriteMaxDist = viewDistance;
		gameCamera.fadeFactor = 1;
		gameCamera.fadeDist = (int) (viewDistance*2.3/2.4);
		gameCamera.setModelLightSources(Camera.light_x, Camera.light_z, Camera.light_y);
		engineHandle = new EngineHandle(gameCamera, gameGraphics);
		loadTextures(); // 60%
		if (lastLoadedNull)
			return;
		loadModels(); // 75%
		if (lastLoadedNull)
			return;
		loadSounds(); // 90%
		if (lastLoadedNull) {
			return;
		}
		drawLoadingBarText(100, "Starting game...");
		drawGameMenu();
		makeLoginMenus();
		makeCharacterDesignMenu();
		resetLoginVars();
	}

	private final void loadSprite(int id, String packageName, int amount) {
		for (int i = id; i < id + amount; i++) {
			if (!gameGraphics.loadSprite(i, packageName)) {
				lastLoadedNull = true;
				return;
			}
		}
	}

	private final void loadMedia() {
		drawLoadingBarText(30, "Unpacking media");
		loadSprite(SPRITE_MEDIA_START, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 1, "media", 6);
		loadSprite(SPRITE_MEDIA_START + 9, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 10, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 11, "media", 3);
		loadSprite(SPRITE_MEDIA_START + 14, "media", 8);
		loadSprite(SPRITE_MEDIA_START + 22, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 23, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 24, "media", 1);
		loadSprite(SPRITE_MEDIA_START + 25, "media", 2);
		loadSprite(SPRITE_UTIL_START, "util", 2);
		loadSprite(SPRITE_UTIL_START + 2, "util", 4);
		loadSprite(SPRITE_UTIL_START + 6, "util", 2);
		loadSprite(SPRITE_PROJECTILE_START, "projectile", 7);
		loadSprite(SPRITE_LOGO_START, "logo", 2);

		int i = EntityHandler.invPictureCount();
		for (int j = 1; i > 0; j++) {
			int k = i;
			i -= 30;
			if (k > 30) {
				k = 30;
			}
			loadSprite(SPRITE_ITEM_START + (j - 1) * 30, "item", k);
		}
	}

	private final void loadEntity() {
		drawLoadingBarText(45, "Unpacking entities");
		int animation = 0;
		label0:
			for (int anim = 0; anim < EntityHandler.animationCount(); anim++)
			{
				String s = EntityHandler.getAnimationDef(anim).getName();
				for (int nextAnim = 0; nextAnim < anim; nextAnim++)
				{ // find the next animation
					if (!EntityHandler.getAnimationDef(nextAnim).getName().equalsIgnoreCase(s)) {
						continue;
					}
					EntityHandler.getAnimationDef(anim).number = EntityHandler.getAnimationDef(nextAnim).getNumber();
					continue label0;
				}

				loadSprite(animation, "entity", 15);
				if (EntityHandler.getAnimationDef(anim).hasAttack()) {
					loadSprite(animation + 15, "entity", 3);
				}

				if (EntityHandler.getAnimationDef(anim).hasFlip()) {
					loadSprite(animation + 18, "entity", 9);
				}
				EntityHandler.getAnimationDef(anim).number = animation;
				animation += 27;
			}
	}

	private final void loadTextures() {
		drawLoadingBarText(60, "Unpacking textures");
		gameCamera.method297(EntityHandler.textureCount(), 7, 11);
		for (int i = 0; i < EntityHandler.textureCount(); i++) {
			loadSprite(SPRITE_TEXTURE_START + i, "texture", 1);
			Sprite sprite = ((GameImage) (gameGraphics)).sprites[SPRITE_TEXTURE_START + i];

			int length = sprite.getWidth() * sprite.getHeight();
			int[] pixels = sprite.getPixels();
			int ai1[] = new int[32768];
			for (int k = 0; k < length; k++) {
				ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6) + ((pixels[k] & 0xf8) >> 3)]++;
			}
			int[] dictionary = new int[256];
			dictionary[0] = 0xff00ff;
			int[] temp = new int[256];
			for (int i1 = 0; i1 < ai1.length; i1++) {
				int j1 = ai1[i1];
				if (j1 > temp[255]) {
					for (int k1 = 1; k1 < 256; k1++) {
						if (j1 <= temp[k1]) {
							continue;
						}
						for (int i2 = 255; i2 > k1; i2--) {
							dictionary[i2] = dictionary[i2 - 1];
							temp[i2] = temp[i2 - 1];
						}
						dictionary[k1] = ((i1 & 0x7c00) << 9) + ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3) + 0x40404;
						temp[k1] = j1;
						break;
					}
				}
				ai1[i1] = -1;
			}
			byte[] indices = new byte[length];
			for (int l1 = 0; l1 < length; l1++) {
				int j2 = pixels[l1];
				int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6) + ((j2 & 0xf8) >> 3);
				int l2 = ai1[k2];
				if (l2 == -1) {
					int i3 = 0x3b9ac9ff;
					int j3 = j2 >> 16 & 0xff;
						int k3 = j2 >> 8 & 0xff;
						int l3 = j2 & 0xff;
						for (int i4 = 0; i4 < 256; i4++) {
							int j4 = dictionary[i4];
							int k4 = j4 >> 16 & 0xff;
						int l4 = j4 >> 8 & 0xff;
						int i5 = j4 & 0xff;
						int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4) + (l3 - i5) * (l3 - i5);
						if (j5 < i3) {
							i3 = j5;
							l2 = i4;
						}
						}

						ai1[k2] = l2;
				}
				indices[l1] = (byte) l2;
			}
			gameCamera.method298(i, indices, dictionary, sprite.getTotalWidth());
		}
	}

	private final void checkMouseStatus()
	{
		if (selectedSpell >= 0 || selectedItem >= 0)
		{
			rightClickMenu[menuLength].text1 = "Cancel";
			rightClickMenu[menuLength].text2 = "";
			rightClickMenu[menuLength].id = 4000;
			menuLength++;
		}
		for (int i = 0; i < menuLength; i++)
			rightClickMenu[i].indices = i;

		for (boolean flag = false; !flag;)
		{
			flag = true;
			for (int j = 0; j < menuLength - 1; j++) {
				int l = rightClickMenu[j].indices;
				int j1 = rightClickMenu[j + 1].indices;
				if (rightClickMenu[l].id > rightClickMenu[j1].id) {
					rightClickMenu[j].indices = j1;
					rightClickMenu[j + 1].indices = l;
					flag = false;
				}
			}

		}

		if (menuLength > 20)
			menuLength = 20;
		if (menuLength > 0)
		{
			int k = -1;
			for (int i1 = 0; i1 < menuLength; i1++) {
				if (rightClickMenu[rightClickMenu[i1].indices].text2 == null
						|| rightClickMenu[rightClickMenu[i1].indices].text2.length() <= 0)
					continue;
				k = i1;
				break;
			}

			String s = null;
			if ((selectedItem >= 0 || selectedSpell >= 0)
					&& menuLength == 1)
				s = "Choose a target";
			else if ((selectedItem >= 0 || selectedSpell >= 0)
					&& menuLength > 1)
				s = "@whi@" + rightClickMenu[rightClickMenu[0].indices].text1 + " " + rightClickMenu[rightClickMenu[0].indices].text2;
			else if (k != -1)
				s = rightClickMenu[rightClickMenu[k].indices].text2 + ": @whi@" + rightClickMenu[rightClickMenu[0].indices].text1;
			if (menuLength == 2 && s != null)
				s = s + "@whi@ / 1 more option";
			if (menuLength > 2 && s != null)
				if (s != null)
					gameGraphics.drawString(s, 6, 14, 1, 0xffff00);
			if (!configMouseButtons && mouseButtonClick == 1
					|| configMouseButtons && mouseButtonClick == 1
					&& menuLength == 1)
			{
				menuClick(rightClickMenu[0].indices);
				mouseButtonClick = 0;
				return;
			}
			if (!configMouseButtons && mouseButtonClick == 2
					|| configMouseButtons && mouseButtonClick == 1)
			{
				MenuRightClick.menuHeight = (menuLength + 1) * 15;
				MenuRightClick.menuWidth = gameGraphics.textWidth("Choose option", 1) + 5;
				for (int k1 = 0; k1 < menuLength; k1++) {
					int l1 = gameGraphics.textWidth(rightClickMenu[k1].text1 + " " + rightClickMenu[k1].text2, 1) + 5;
					if (l1 > MenuRightClick.menuWidth)
						MenuRightClick.menuWidth = l1;
				}

				MenuRightClick.menuX = super.mouseX - MenuRightClick.menuWidth / 2;
				MenuRightClick.menuY = super.mouseY - 7;
				showRightClickMenu = true;
				if (MenuRightClick.menuX < 0)
					MenuRightClick.menuX = 0;
				if (MenuRightClick.menuY < 0)
					MenuRightClick.menuY = 0;
				if (MenuRightClick.menuX + MenuRightClick.menuWidth > windowWidth - 2)
					MenuRightClick.menuX = windowWidth - 2 - MenuRightClick.menuWidth;
				if (MenuRightClick.menuY + MenuRightClick.menuHeight > windowHeight - 19)
					MenuRightClick.menuY = windowHeight - 19 - MenuRightClick.menuHeight;
				mouseButtonClick = 0;
			}
		}
	}

	protected final void cantLogout() {
		logoutTimeout = 0;
		displayMessage("@cya@Sorry, you can't logout at the moment", 3, 0);
	}

	private void drawFriendPanel()
	{
		int friendTabColor = (friendTabOn == 0) ? friendPan.getBGColor() : friendPan.getInactiveTabColor();
		int ignoreTabColor = (friendTabOn == 1) ? friendPan.getBGColor() : friendPan.getInactiveTabColor();
		gameGraphics.drawBoxAlpha(friendPan.getX(), friendPan.getY(),
				friendPan.getWidth() / 2, friendPan.getTabHeight(),
				friendTabColor, friendPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(friendPan.getX() + friendPan.getWidth() / 2,
				friendPan.getY(), friendPan.getWidth() / 2,
				friendPan.getTabHeight(), ignoreTabColor, friendPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(friendPan.getX(),
				friendPan.getY() + friendPan.getTabHeight(), friendPan.getWidth(),
				friendPan.getHeight() - friendPan.getTabHeight(),
				friendPan.getBGColor(), friendPan.getBGAlpha());

		gameGraphics.drawLineX(friendPan.getX(), friendPan.getY(),
				friendPan.getWidth(), friendPan.getLineColor());
		gameGraphics.drawLineY(friendPan.getX(),
				friendPan.getY(), friendPan.getTabHeight(), friendPan.getLineColor());
		gameGraphics.drawLineY(friendPan.getX() + friendPan.getWidth(),
				friendPan.getY(), friendPan.getTabHeight(), friendPan.getLineColor());

		gameGraphics.drawLineX(friendPan.getX(),
				friendPan.getY() + friendPan.getTabHeight(),
				friendPan.getWidth(), friendPan.getLineColor());
		gameGraphics.drawLineY(friendPan.getX() + friendPan.getWidth() / 2,
				friendPan.getY(), friendPan.getTabHeight(), friendPan.getLineColor());
		InGameButton button = friendPan.getFriendsButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth()/2,
				button.getY() + button.getHeight()/2 + 4, 4, 0);
		button = friendPan.getIgnoreButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth()/2,
				button.getY() + button.getHeight()/2 + 4, 4, 0);
	}

	private void drawFriendsList()
	{
		for (int i1 = 0; i1 < super.friendsCount; i1++)
		{
			String s;
			if (super.friendsListOnlineStatus[i1] == 99)
				s = "@gre@";
			else if (super.friendsListOnlineStatus[i1] > 0)
				s = "@yel@";
			else
				s = "@red@";
			friendsMenu.drawMenuListText(friendsMenuHandle, i1, s
					+ DataOperations.longToString(super.friendsListLongs[i1])
					+ "~"+(windowWidth-73)+"~@whi@|   Remove");
		}
	}

	private void drawIgnoreList()
	{
		for (int j1 = 0; j1 < super.ignoreListCount; j1++)
			friendsMenu.drawMenuListText(friendsMenuHandle, j1, "@yel@"
					+ DataOperations.longToString(super.ignoreListLongs[j1])
					+ "~"+(windowWidth-73)+"~@whi@|   Remove");
	}

	private void handleMouseOverFriend()
	{
		int friendNameIdx = friendsMenu.selectedListIndex(friendsMenuHandle);
		String displayMsg;
		if (friendNameIdx >= 0
				&& super.mouseX < friendPan.getX() + friendPan.getWidth() - 10)
			if (super.mouseX > friendPan.getX() + friendPan.getWidth() - 70)
				displayMsg = "Click to remove "
						+ DataOperations.longToString(super.friendsListLongs[friendNameIdx]);
			else if (super.friendsListOnlineStatus[friendNameIdx] == 99)
				displayMsg = "Click to message "
						+ DataOperations.longToString(super.friendsListLongs[friendNameIdx]);
			else if (super.friendsListOnlineStatus[friendNameIdx] > 0)
				displayMsg = (DataOperations.longToString(super.friendsListLongs[friendNameIdx])
						+ " is on world " + super.friendsListOnlineStatus[friendNameIdx]);
			else
				displayMsg = (DataOperations.longToString(super.friendsListLongs[friendNameIdx])
						+ " is offline");
		else
			displayMsg = "Click a name to send a message";
		gameGraphics.drawText(displayMsg,
				friendPan.getX() + friendPan.getWidth() / 2,
				friendPan.getY() + friendPan.getTabHeight() + 11, 1, 0xffffff);
		InGameButton button = friendPan.getFriendsAddButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth() / 2,
				(button.getY() + button.getHeight()) - 3, 1,
				button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
	}

	private void handleMouseOverIgnore()
	{
		int ignoreNameIdx = friendsMenu.selectedListIndex(friendsMenuHandle);
		String displayMsg;
		if (ignoreNameIdx >= 0
				&& super.mouseX < friendPan.getX() + friendPan.getWidth() - 10
				&& super.mouseX > friendPan.getX() + friendPan.getWidth() - 70)
			displayMsg = "Click to remove "
					+ DataOperations.longToString(super.ignoreListLongs[ignoreNameIdx]);
		else
			displayMsg = "Blocking messages from:";
		gameGraphics.drawText(displayMsg,
				friendPan.getX() + friendPan.getWidth() / 2,
				friendPan.getY() + friendPan.getTabHeight() + 11, 1, 0xffffff);

		InGameButton button = friendPan.getIgnoreAddButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth() / 2,
				(button.getY() + button.getHeight()) - 3, 1,
				button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
	}

	private void handleFriendsTabClicks()
	{
		if (super.mouseX < friendPan.getX() + friendPan.getWidth()/2
				&& friendTabOn == 1)
		{
			friendTabOn = 0;
			friendsMenu.method165(friendsMenuHandle, 0);
		} else if (super.mouseX > friendPan.getX() + friendPan.getWidth()/2
				&& friendTabOn == 0)
		{
			friendTabOn = 1;
			friendsMenu.method165(friendsMenuHandle, 0);
		}
	}

	private void handleClickOnFriends()
	{
		int friendNameIdx = friendsMenu.selectedListIndex(friendsMenuHandle);
		if (friendNameIdx >= 0 && super.mouseX < friendPan.getX() + friendPan.getWidth() - 10)
		{
			if (super.mouseX > friendPan.getX() + friendPan.getWidth() - 70)
			{
				removeFromFriends(super.friendsListLongs[friendNameIdx]);
			}
			else if (super.friendsListOnlineStatus[friendNameIdx] != 0)
			{
				inputBoxType = 2;
				privateMessageTarget = super.friendsListLongs[friendNameIdx];
				super.inputMessage = "";
				super.enteredMessage = "";
			}
		}
	}

	private void handleClickOnIgnore()
	{
		int j2 = friendsMenu.selectedListIndex(friendsMenuHandle);
		if (j2 >= 0 && super.mouseX < friendPan.getX() + friendPan.getWidth() - 10
				&& super.mouseX > friendPan.getX() + friendPan.getWidth() - 70)
		{
			removeFromIgnoreList(super.ignoreListLongs[j2]);
		}
	}

	private void handleClickAddFriend()
	{
		inputBoxType = 1;
		super.inputText = "";
		super.enteredText = "";
	}

	private void handleClickAddIgnore()
	{
		inputBoxType = 3;
		super.inputText = "";
		super.enteredText = "";
	}

	private void handleFriendsPanelClicks()
	{
		friendsMenu.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if (super.mouseY <= friendPan.getY() + friendPan.getTabHeight()
		&& mouseButtonClick == 1)
			handleFriendsTabClicks();
		if (mouseButtonClick == 1 && friendTabOn == 0)
			handleClickOnFriends();
		if (mouseButtonClick == 1 && friendTabOn == 1)
			handleClickOnIgnore();
		if (friendPan.getFriendsAddButton().isMouseOverButton(super.mouseX, super.mouseY)
				&& mouseButtonClick == 1 && friendTabOn == 0)
			handleClickAddFriend();
		if (friendPan.getIgnoreAddButton().isMouseOverButton(super.mouseX, super.mouseY)
				&& mouseButtonClick == 1 && friendTabOn == 1)
			handleClickAddIgnore();
		mouseButtonClick = 0;
	}

	private final void drawFriendsWindow(boolean flag)
	{
		friendPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		drawFriendPanel();
		friendsMenu.resetListTextCount(friendsMenuHandle);
		if (friendTabOn == 0)
			drawFriendsList();
		if (friendTabOn == 1)
			drawIgnoreList();
		friendsMenu.drawMenu(true);
		if (friendTabOn == 0)
			handleMouseOverFriend();
		if (friendTabOn == 1)
			handleMouseOverIgnore();
		if (!flag)
			return;
		if (friendPan.isMouseOver(super.mouseX, super.mouseY))
			handleFriendsPanelClicks();
		else if (friendPan.getFrame().getCloseButton().isMouseOverButton(
				super.mouseX, super.mouseY))
		{ // close button
			menuLength++;
			if (mouseButtonClick == 1)
			{
				mouseOverMenu = 0;
				mouseButtonClick = 0;
			}
		}
		else if (magicPan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside friends panel but not on the content or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
	}

	private final boolean loadSection(int xPos, int yPos)
	{
		if (playerAliveTimeout != 0) {
			engineHandle.playerIsAlive = false;
			return false;
		}
		notInWilderness = false;
		xPos += wildX;
		yPos += wildY;
		if (sectorHeight == wildYSubtract && xPos > anInt789 && xPos < anInt791 && yPos > anInt790 && yPos < anInt792) {
			engineHandle.playerIsAlive = true;
			return false;
		}
		gameGraphics.drawText("Loading... Please wait", windowHalfWidth, windowHalfHeight + 25, 1, 0xffffff);
		drawChatMessageTabs();
		gameGraphics.drawImage(aGraphics936, 0, 0);
		int oldAreaX = areaX;
		int oldAreaY = areaY;
		int i1 = (xPos + EngineHandle.SECTOR_WIDTH/2) / EngineHandle.SECTOR_WIDTH; // tile x (or y)
		int j1 = (yPos + EngineHandle.SECTOR_HEIGHT/2) / EngineHandle.SECTOR_HEIGHT; // tile y (or x)
		sectorHeight = wildYSubtract;
		areaX = i1 * EngineHandle.SECTOR_WIDTH - EngineHandle.SECTOR_WIDTH; // next areaX
		areaY = j1 * EngineHandle.SECTOR_HEIGHT - EngineHandle.SECTOR_HEIGHT; // next areaY
		anInt789 = i1 * 48 - 32; // lowerAreaXTile
		anInt790 = j1 * 48 - 32; // lowerAreaYTile
		anInt791 = i1 * 48 + 32; // upperAreaXTile
		anInt792 = j1 * 48 + 32; // upperAreaYTile
		engineHandle.method401(xPos, yPos, sectorHeight);
		areaX -= wildX;
		areaY -= wildY;
		int areaXDiff = areaX - oldAreaX;
		int areaYDiff = areaY - oldAreaY;
		for (int objIdx = 0; objIdx < objectCount; objIdx++)
		{
			objectX[objIdx] -= areaXDiff;
			objectY[objIdx] -= areaYDiff;
			int currObjX = objectX[objIdx];
			int currObjY = objectY[objIdx];
			int currObjType = objectType[objIdx];
			int currObjId = objectID[objIdx];
			Model model = objectModelArray[objIdx];
			try {
				int currObjId2 = objectID[objIdx];
				int currObjWidth;
				int currObjeHeight;
				if (currObjId2 == 0 || currObjId2 == 4) {
					currObjWidth = EntityHandler.getObjectDef(currObjType).getWidth();
					currObjeHeight = EntityHandler.getObjectDef(currObjType).getHeight();
				} else {
					currObjeHeight = EntityHandler.getObjectDef(currObjType).getWidth();
					currObjWidth = EntityHandler.getObjectDef(currObjType).getHeight();
				}
				double x = ((currObjX + currObjX + currObjWidth) * EngineHandle.GAME_SIZE) / 2;
				double y = ((currObjY + currObjY + currObjeHeight) * EngineHandle.GAME_SIZE) / 2;
				if (currObjX >= 0 && currObjY >= 0
						&& currObjX < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_WIDTH
						&& currObjY < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_HEIGHT)
				{
					gameCamera.addModel(model); // objects
					model.setTranslate(x, -engineHandle.getAveragedElevation(x, y), y);
					engineHandle.method412(currObjX, currObjY, currObjType, currObjId); // shadows
					if (currObjType == 74)
						model.addTranslate(0, -480, 0);
				}
			}
			catch (RuntimeException runtimeexception) {
				System.out.println("Loc Error: " + runtimeexception.getMessage());
				System.out.println("i:" + objIdx + " obj:" + model);
				runtimeexception.printStackTrace();
			}
		}

		for (int k2 = 0; k2 < doorCount; k2++) {
			doorX[k2] -= areaXDiff;
			doorY[k2] -= areaYDiff;
			int i3 = doorX[k2];
			int l3 = doorY[k2];
			int j4 = doorType[k2];
			int i5 = doorDirection[k2];
			try {
				engineHandle.method408(i3, l3, i5, j4);
				Model model_1 = makeModel(i3, l3, i5, j4, k2);
				doorModel[k2] = model_1;
			}
			catch (RuntimeException runtimeexception1) {
				System.out.println("Bound Error: " + runtimeexception1.getMessage());
				runtimeexception1.printStackTrace();
			}
		}

		for (int j3 = 0; j3 < groundItemCount; j3++) {
			groundItemX[j3] -= areaXDiff;
			groundItemY[j3] -= areaYDiff;
		}

		for (int i4 = 0; i4 < playerCount; i4++) {
			Mob mob = playerArray[i4];
			mob.currentX -= areaXDiff * EngineHandle.GAME_SIZE;
			mob.currentY -= areaYDiff * EngineHandle.GAME_SIZE;
			for (int j5 = 0; j5 <= mob.waypointCurrent; j5++) {
				mob.waypointsX[j5] -= areaXDiff * EngineHandle.GAME_SIZE;
				mob.waypointsY[j5] -= areaYDiff * EngineHandle.GAME_SIZE;
			}

		}

		for (int k4 = 0; k4 < npcCount; k4++) {
			Mob mob_1 = npcArray[k4];
			mob_1.currentX -= areaXDiff * EngineHandle.GAME_SIZE;
			mob_1.currentY -= areaYDiff * EngineHandle.GAME_SIZE;
			for (int l5 = 0; l5 <= mob_1.waypointCurrent; l5++) {
				mob_1.waypointsX[l5] -= areaXDiff * EngineHandle.GAME_SIZE;
				mob_1.waypointsY[l5] -= areaYDiff * EngineHandle.GAME_SIZE;
			}

		}

		engineHandle.playerIsAlive = true;
		return true;
	}

	private void drawMagicPanel()
	{

		int magicTabColor = (menuMagicPrayersSelected == 1) ? magicPan.getBGColor() : magicPan.getInactiveTabColor();
		int prayerTabColor = (menuMagicPrayersSelected == 0) ? magicPan.getBGColor() : magicPan.getInactiveTabColor();
		gameGraphics.drawBoxAlpha(magicPan.getX(), magicPan.getY(),
				magicPan.getWidth() / 2, magicPan.getTabHeight(),
				prayerTabColor, magicPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(magicPan.getX() + magicPan.getWidth() / 2,
				magicPan.getY(), magicPan.getWidth() / 2,
				magicPan.getTabHeight(), magicTabColor, magicPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(magicPan.getX(),
				magicPan.getY() + magicPan.getTabHeight(), magicPan.getWidth(),
				magicPan.getScrollBoxHeight(), magicPan.getBGColor(),
				magicPan.getBGAlpha());
		gameGraphics.drawBoxAlpha(magicPan.getX(),
				magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight(), magicPan.getWidth(),
				magicPan.getHeight() - magicPan.getScrollBoxHeight() - magicPan.getTabHeight(),
				magicPan.getBGColor(), magicPan.getBGAlpha());

		gameGraphics.drawLineX(magicPan.getX(), magicPan.getY(),
				magicPan.getWidth(), magicPan.getLineColor());
		gameGraphics.drawLineY(magicPan.getX(),
				magicPan.getY(), magicPan.getTabHeight(), magicPan.getLineColor());
		gameGraphics.drawLineY(magicPan.getX() + magicPan.getWidth(),
				magicPan.getY(), magicPan.getTabHeight(), magicPan.getLineColor());
		gameGraphics.drawLineX(magicPan.getX(),
				magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight(),
				magicPan.getWidth(), magicPan.getLineColor());

		gameGraphics.drawLineX(magicPan.getX(),
				magicPan.getY() + magicPan.getTabHeight(),
				magicPan.getWidth(), magicPan.getLineColor());
		gameGraphics.drawLineY(magicPan.getX() + magicPan.getWidth() / 2,
				magicPan.getY(), magicPan.getTabHeight(), magicPan.getLineColor());
		InGameButton button = magicPan.getMagicButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth()/2,
				button.getY() + button.getHeight()/2 + 4, 4, 0);
		button = magicPan.getPrayerButton();
		gameGraphics.drawText(button.getButtonText(),
				button.getX() + button.getWidth()/2,
				button.getY() + button.getHeight()/2 + 4, 4, 0);
	}

	private void drawMagicInfoBox(int selectedSpellIndex)
	{
		if (selectedSpellIndex != -1)
		{
			gameGraphics.drawString("Level "
					+ EntityHandler.getSpellDef(selectedSpellIndex).getReqLevel()
					+ ": " + EntityHandler.getSpellDef(selectedSpellIndex).getName(),
					magicPan.getX() + 2,
					magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 10,
					1, 0xffff00);
			gameGraphics.drawString(EntityHandler.getSpellDef(selectedSpellIndex).getDescription(),
					magicPan.getX() + 2,
					magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 22,
					0, 0xffffff);
			int i4 = 0;
			for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(selectedSpellIndex).getRunesRequired())
			{
				int runeID = e.getKey();
				gameGraphics.drawPicture(magicPan.getX() + 2 + i4 * 44,
						magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 36,
						SPRITE_ITEM_START + EntityHandler.getItemDef(runeID).getSprite());
				int runeInvCount = inventoryCount(runeID);
				int runeCount = e.getValue();
				String s2 = "@red@";
				if (hasRequiredRunes(runeID, runeCount))
					s2 = "@gre@";
				gameGraphics.drawString(s2 + runeInvCount + "/" + runeCount, magicPan.getX() + 2 + i4 * 44,
						magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 36, 1, 0xffffff);
				i4++;
			}
		}
		else
			gameGraphics.drawString("Point at a spell for a description", magicPan.getX() + 2,
					magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 13, 1, 0);
	}

	private void drawMagicTab()
	{
		spellMenu.resetListTextCount(spellMenuHandle);
		int i1 = 0;
		for (int spellIndex = 0; spellIndex < EntityHandler.spellCount(); spellIndex++)
		{
			String s = "@yel@";
			for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(spellIndex).getRunesRequired())
			{
				if (hasRequiredRunes(e.getKey(), e.getValue()))
					continue;
				s = "@whi@";
				break;
			}
			int spellLevel = playerStatCurrent[6];
			if (EntityHandler.getSpellDef(spellIndex).getReqLevel() > spellLevel) {
				s = "@bla@";
			}
			spellMenu.drawMenuListText(spellMenuHandle, i1++, s + "Level " + EntityHandler.getSpellDef(spellIndex).getReqLevel() + ": " + EntityHandler.getSpellDef(spellIndex).getName());
		}

		spellMenu.drawMenu(true);
		drawMagicInfoBox(spellMenu.selectedListIndex(spellMenuHandle));
	}

	private void drawPrayerInfoBox(int selectedPrayerIdx)
	{
		if (selectedPrayerIdx != -1)
		{
			gameGraphics.drawText("Level " + EntityHandler.getPrayerDef(selectedPrayerIdx).getReqLevel() + ": "
					+ EntityHandler.getPrayerDef(selectedPrayerIdx).getName(), magicPan.getX() + magicPan.getWidth() / 2,
					magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 16, 1, 0xffff00);
			gameGraphics.drawText(EntityHandler.getPrayerDef(selectedPrayerIdx).getDescription(),
					magicPan.getX() + magicPan.getWidth() / 2, magicPan.getY() + magicPan.getTabHeight()
					+ magicPan.getScrollBoxHeight() + 31, 0, 0xffffff);
			gameGraphics.drawText("Drain rate: " + EntityHandler.getPrayerDef(selectedPrayerIdx).getDrainRate(),
					magicPan.getX() + magicPan.getWidth() / 2, magicPan.getY() + magicPan.getTabHeight()
					+ magicPan.getScrollBoxHeight() + 46, 1, 0);
		}
		else
			gameGraphics.drawString("Point at a prayer for a description", magicPan.getX() + 2,
					magicPan.getY() + magicPan.getTabHeight() + magicPan.getScrollBoxHeight() + 13, 1, 0);
	}

	private void drawPrayerTab()
	{
		spellMenu.resetListTextCount(spellMenuHandle);
		int j1 = 0;
		for (int j2 = 0; j2 < EntityHandler.prayerCount(); j2++)
		{
			String s1 = "@whi@";
			if (EntityHandler.getPrayerDef(j2).getReqLevel() > playerStatBase[5])
				s1 = "@bla@";
			if (prayerOn[j2])
				s1 = "@gre@";
			spellMenu.drawMenuListText(spellMenuHandle, j1++, s1 + "Level " + EntityHandler.getPrayerDef(j2).getReqLevel() + ": " + EntityHandler.getPrayerDef(j2).getName());
		}
		spellMenu.drawMenu(true);
		drawPrayerInfoBox(spellMenu.selectedListIndex(spellMenuHandle));
	}

	private void handleMagicTabClicks()
	{
		if (super.mouseX < magicPan.getX() + magicPan.getWidth()/2
				&& menuMagicPrayersSelected == 1)
		{  // switch to magic tab
			menuMagicPrayersSelected = 0;
			prayerMenuIndex = spellMenu.getMenuIndex(spellMenuHandle);
			spellMenu.method165(spellMenuHandle, magicMenuIndex);
		}
		else if (super.mouseX > magicPan.getX() + magicPan.getWidth()/2
				&& menuMagicPrayersSelected == 0)
		{  // switch to prayer tab
			menuMagicPrayersSelected = 1;
			magicMenuIndex = spellMenu.getMenuIndex(spellMenuHandle);
			spellMenu.method165(spellMenuHandle, prayerMenuIndex);
		}
	}

	private void handleSpellsTabClicks()
	{
		int spellIdx = spellMenu.selectedListIndex(spellMenuHandle);
		if (spellIdx != -1)
		{
			int k2 = playerStatCurrent[6];
			if (EntityHandler.getSpellDef(spellIdx).getReqLevel() > k2)
				displayMessage("Your magic ability is not high enough for this spell", 3, 0);
			else
			{
				int k3 = 0;
				for (Entry<Integer, Integer> e : EntityHandler.getSpellDef(spellIdx).getRunesRequired())
				{
					if (!hasRequiredRunes(e.getKey(), e.getValue()))
					{
						displayMessage("You don't have all the reagents you need for this spell",
								3, 0);
						k3 = -1;
						break;
					}
					k3++;
				}
				if (k3 == EntityHandler.getSpellDef(spellIdx).getRuneCount())
				{
					selectedSpell = spellIdx;
					selectedItem = -1;
				}
			}
		}
	}

	private void handlePrayerTabClicks()
	{
		int prayerIdx = spellMenu.selectedListIndex(spellMenuHandle);
		if (prayerIdx != -1) {
			int l2 = playerStatBase[5];
			if (EntityHandler.getPrayerDef(prayerIdx).getReqLevel() > l2)
				displayMessage("Your prayer ability is not high enough for this prayer", 3, 0);
			else if (playerStatCurrent[5] == 0)
				displayMessage("You have run out of prayer points. Return to a church to recharge", 3, 0);
			else if (prayerOn[prayerIdx])
				formatPacket(248, prayerIdx, -1);  // switch prayer off
			else
				formatPacket(56, prayerIdx, -1);  // switch prayer on
		}
	}

	private void handleMagicPanelClicks()
	{
		spellMenu.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if ((super.mouseY <= magicPan.getY() + magicPan.getTabHeight())
				&& mouseButtonClick == 1)
			handleMagicTabClicks();
		if (mouseButtonClick == 1 && menuMagicPrayersSelected == 0)
			handleSpellsTabClicks();
		if (mouseButtonClick == 1 && menuMagicPrayersSelected == 1)
		{
			handlePrayerTabClicks();
		}
		mouseButtonClick = 0;
	}

	private final void drawMagicWindow(boolean flag)
	{
		magicPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		drawMagicPanel();
		if (menuMagicPrayersSelected == 0)
			drawMagicTab();
		if (menuMagicPrayersSelected == 1)
			drawPrayerTab();
		if (!flag)
			return;

		if (magicPan.isMouseOver(super.mouseX, super.mouseY))
			handleMagicPanelClicks();
		else if (magicPan.getFrame().getCloseButton().isMouseOverButton(
				super.mouseX, super.mouseY))
		{ // close button

			menuLength++;
			if (mouseButtonClick == 1)
			{
				mouseOverMenu = 0;
				mouseButtonClick = 0;
			}
		}
		else if (magicPan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside info panel but not on the content or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
	}

	protected final void handleMenuKeyDown(int keyCode, int keyChar)
	{
		if (keyCode == 122)
		{ // F11
			/* TODO: not using recorder anymore.
        	recording = !recording;
        	if (recording) {
        		try {
        			frames.clear();
        			File file = misc.getEmptyFile(true, currentUser);
        			Recorder recorder = new Recorder(windowWidth, windowHeight + 11,
        					Config.MOVIE_FPS, frames, file.getAbsolutePath(),
        					"video.quicktime");
        			displayMessage("Recording movie to " + file.getName(), 3, 0);
        			new Thread(recorder).start();
        		}
        		catch (Exception e) {
        		}
        	} else {
        		frames.add(null);
        		displayMessage("Movie saved.", 3, 0);
        	}*/
		}
		else if (keyCode == 123)
		{ // F12
			takeScreenshot(true);
		}
		if (!loggedIn && menuWelcome != null)
		{
			if (loginScreenNumber == 0)
				menuWelcome.keyDown(keyCode, keyChar);
			if (loginScreenNumber == 1)
				menuNewUser.keyDown(keyCode, keyChar);
			if (loginScreenNumber == 2)
				menuLogin.keyDown(keyCode, keyChar);
		}
		else
		{
			if (showCharacterLookScreen)
			{
				chrDesignMenu.keyDown(keyCode, keyChar);
				return;
			}
			if (isTyping)
			{
				if (inputBoxType == 0 && showAbuseWindow == 0)
					handleChatBinds(keyCode, keyChar);
			}
			else
			{
				handleKeybinds(keyCode, keyChar);
			}
		}
	}

	private void handleKeybinds(int keyCode, int keyChar)
	{
		switch (keyCode)
		{
		case 10: // enter
			isTyping = !isTyping;
			break;
			/*case 69: // E
        	if (super.keyDownCode[87])
        	{ // W
        		sendWalkCommandKeys(sectionX, sectionY, sectionX - 1, sectionY - 1,
        				sectionX - 1, sectionY - 1, false, true);
        		cameraRotationLeftRight = 160;
        	}
        	else if (super.keyDownCode[83])
        	{ // S
        		sendWalkCommandKeys(sectionX, sectionY, sectionX - 1, sectionY + 1,
        				sectionX - 1, sectionY + 1, false, true);
        		cameraRotationLeftRight = 224;
        	}
        	else
        	{
        		sendWalkCommandKeys(sectionX, sectionY, sectionX - 1, sectionY,
        				sectionX - 1, sectionY, false, true);
        		cameraRotationLeftRight = 192;
        	}
        	break;
        case 81: // Q
        	if (super.keyDownCode[87])
        	{ // W
        		sendWalkCommandKeys(sectionX, sectionY, sectionX + 1, sectionY - 1,
        				sectionX + 1, sectionY - 1, false, true);
        		cameraRotationLeftRight = 96;
        	}
        	else if (super.keyDownCode[83])
        	{ // S
        		sendWalkCommandKeys(sectionX, sectionY, sectionX + 1, sectionY + 1,
        				sectionX + 1, sectionY + 1, false, true);
        		cameraRotationLeftRight = 32;
        	}
        	else
        	{
        		sendWalkCommandKeys(sectionX, sectionY, sectionX + 1, sectionY,
        				sectionX + 1, sectionY, false, true);
        		cameraRotationLeftRight = 64;
        	}
        	break;
        case 83: // S
        	if (super.keyDownCode[69])
        	{ // E
            	sendWalkCommandKeys(sectionX, sectionY, sectionX - 1, sectionY + 1,
            			sectionX - 1, sectionY + 1, false, true);
        		cameraRotationLeftRight = 224;
        	}
        	else if (super.keyDownCode[81])
        	{ // Q
            	sendWalkCommandKeys(sectionX, sectionY, sectionX + 1, sectionY + 1,
            			sectionX + 1, sectionY + 1, false, true);
        		cameraRotationLeftRight = 32;
        	}
        	else
        	{
            	sendWalkCommandKeys(sectionX, sectionY, sectionX, sectionY + 1,
            			sectionX, sectionY + 1, false, true);
            	cameraRotationLeftRight = 0;
        	}
        	break;
        case 87: // W
        	if (super.keyDownCode[69])
        	{ // E
            	sendWalkCommandKeys(sectionX, sectionY, sectionX - 1, sectionY - 1,
            			sectionX - 1, sectionY - 1, false, true);
        		cameraRotationLeftRight = 160;
        	}
        	else if (super.keyDownCode[81])
        	{ // Q
            	sendWalkCommandKeys(sectionX, sectionY, sectionX + 1, sectionY - 1,
            			sectionX + 1, sectionY - 1, false, true);
        		cameraRotationLeftRight = 96;
        	}
        	else
        	{
            	sendWalkCommandKeys(sectionX, sectionY, sectionX, sectionY - 1,
            			sectionX, sectionY - 1, false, true);
            	cameraRotationLeftRight = 128;
        	}
        	break;*/
		}
	}

	private void handleChatBinds(int keyCode, int keyChar)
	{
		switch(keyCode)
		{
		case 38: // Up Arrow
			gameMenu.updateText(chatHandlePlayerEntry, lastMessage);
			break;
		case 40: // Down Arrow
			gameMenu.updateText(chatHandlePlayerEntry, "");
			break;
		default:
			gameMenu.keyDown(keyCode, keyChar);
			break;
		}
		if (keyCode == 10)
		{ // return key
			isTyping = !isTyping;
		}
	}

	private void handleCharacterControlBinds()
	{
		int arrowKeyMask = 0;
		if (super.keyDownCode[69]) // E
			arrowKeyMask |= 1;
		else if (super.keyDownCode[81]) // Q
			arrowKeyMask |= 2;
		if (super.keyDownCode[87]) // W
			arrowKeyMask |= 4;
		else if (super.keyDownCode[83]) // S
			arrowKeyMask |= 8;
		if (arrowKeyMask != 0)
			gameCamera.moveCamera(100, arrowKeyMask);
	}

	private final void drawShopBox() {
		if (mouseButtonClick != 0) {
			mouseButtonClick = 0;
			int i = super.mouseX - 52;
			int j = super.mouseY - 44;
			if (i >= 0 && j >= 12 && i < 408 && j < 246) {
				int k = 0;
				for (int i1 = 0; i1 < 5; i1++) {
					for (int i2 = 0; i2 < 8; i2++) {
						int l2 = 7 + i2 * 49;
						int l3 = 28 + i1 * 34;
						if (i > l2 && i < l2 + 49 && j > l3 && j < l3 + 34 && shopItemsI[k].id != -1) {
							selectedShopItemIndex = k;
							selectedShopItemType = shopItemsI[k].id;
						}
						k++;
					}

				}

				if (selectedShopItemIndex >= 0) {
					int j2 = shopItemsI[selectedShopItemIndex].id;
					if (j2 != -1) {
						if (shopItemsI[selectedShopItemIndex].amount > 0
								&& i > 298 && j >= 204 && i < 408 && j <= 215) {
							int i4 = (shopItemBuyPriceModifier * EntityHandler.getItemDef(j2).getBasePrice()) / 100;
							super.streamClass.createPacket(128);
							super.streamClass.add2ByteInt(shopItemsI[selectedShopItemIndex].id);
							super.streamClass.add4ByteInt(i4);
							super.streamClass.formatPacket();
						}
						if (inventoryCount(j2) > 0 && i > 2 && j >= 229 && i < 112 && j <= 240) {
							int j4 = (shopItemSellPriceModifier * EntityHandler.getItemDef(j2).getBasePrice()) / 100;
							super.streamClass.createPacket(255);
							super.streamClass.add2ByteInt(shopItemsI[selectedShopItemIndex].id);
							super.streamClass.add4ByteInt(j4);
							super.streamClass.formatPacket();
						}
					}
				}
			} else {
				super.streamClass.createPacket(253);
				super.streamClass.formatPacket();
				showShop = false;
				return;
			}
		}
		byte byte0 = 52;
		byte byte1 = 44;
		gameGraphics.drawBox(byte0, byte1, 408, 12, 192);
		int l = 0x989898;
		gameGraphics.drawBoxAlpha(byte0, byte1 + 12, 408, 17, l, 160);
		gameGraphics.drawBoxAlpha(byte0, byte1 + 29, 8, 170, l, 160);
		gameGraphics.drawBoxAlpha(byte0 + 399, byte1 + 29, 9, 170, l, 160);
		gameGraphics.drawBoxAlpha(byte0, byte1 + 199, 408, 47, l, 160);
		gameGraphics.drawString("Buying and selling items", byte0 + 1, byte1 + 10, 1, 0xffffff);
		int j1 = 0xffffff;
		if (super.mouseX > byte0 + 320 && super.mouseY >= byte1 && super.mouseX < byte0 + 408 && super.mouseY < byte1 + 12)
			j1 = 0xff0000;
		gameGraphics.drawBoxTextRight("Close window", byte0 + 406, byte1 + 10, 1, j1);
		gameGraphics.drawString("Shops stock in green", byte0 + 2, byte1 + 24, 1, 65280);
		gameGraphics.drawString("Number you own in blue", byte0 + 135, byte1 + 24, 1, 65535);
		gameGraphics.drawString("Your money: " + inventoryCount(10) + "gp", byte0 + 280, byte1 + 24, 1, 0xffff00);
		int k2 = 0xd0d0d0;
		int k3 = 0;
		for (int k4 = 0; k4 < 5; k4++) {
			for (int l4 = 0; l4 < 8; l4++) {
				int j5 = byte0 + 7 + l4 * 49;
				int i6 = byte1 + 28 + k4 * 34;
				if (selectedShopItemIndex == k3)
					gameGraphics.drawBoxAlpha(j5, i6, 49, 34, 0xff0000, 160);
				else
					gameGraphics.drawBoxAlpha(j5, i6, 49, 34, k2, 160);
				gameGraphics.drawBoxEdge(j5, i6, 50, 35, 0);
				if (shopItemsI[k3].id != -1) {
					gameGraphics.spriteClip4(j5, i6, 48, 32,
							SPRITE_ITEM_START + shopItemsI[k3].icon(),
							shopItemsI[k3].color(), 0, 0, false);
					gameGraphics.drawString(Integer.toString(shopItemsI[k3].amount),
							j5 + 1, i6 + 10, 1, 65280);
					gameGraphics.drawBoxTextRight(Integer.toString(inventoryCount(shopItemsI[k3].id)),
							j5 + 47, i6 + 10, 1, 65535);
				}
				k3++;
			}

		}

		gameGraphics.drawLineX(byte0 + 5, byte1 + 222, 398, 0);
		if (selectedShopItemIndex == -1) {
			gameGraphics.drawText("Select an object to buy or sell", byte0 + 204, byte1 + 214, 3, 0xffff00);
			return;
		}
		int i5 = shopItemsI[selectedShopItemIndex].id;
		if (i5 != -1) {
			if (shopItemsI[selectedShopItemIndex].amount > 0) {
				int j6 = (shopItemBuyPriceModifier * EntityHandler.getItemDef(i5).getBasePrice()) / 100;
				gameGraphics.drawString("Buy a new " + EntityHandler.getItemDef(i5).getName() + " for " + j6 + "gp", byte0 + 2, byte1 + 214, 1, 0xffff00);
				int k1 = 0xffffff;
				if (super.mouseX > byte0 + 298 && super.mouseY >= byte1 + 204 && super.mouseX < byte0 + 408 && super.mouseY <= byte1 + 215)
					k1 = 0xff0000;
				gameGraphics.drawBoxTextRight("Click here to buy", byte0 + 405, byte1 + 214, 3, k1);
			} else {
				gameGraphics.drawText("This item is not currently available to buy", byte0 + 204, byte1 + 214, 3, 0xffff00);
			}
			if (inventoryCount(i5) > 0) {
				int k6 = (shopItemSellPriceModifier * EntityHandler.getItemDef(i5).getBasePrice()) / 100;
				gameGraphics.drawBoxTextRight("Sell your " + EntityHandler.getItemDef(i5).getName() + " for " + k6 + "gp", byte0 + 405, byte1 + 239, 1, 0xffff00);
				int l1 = 0xffffff;
				if (super.mouseX > byte0 + 2 && super.mouseY >= byte1 + 229 && super.mouseX < byte0 + 112 && super.mouseY <= byte1 + 240)
					l1 = 0xff0000;
				gameGraphics.drawString("Click here to sell", byte0 + 2, byte1 + 239, 3, l1);
				return;
			}
			gameGraphics.drawText("You do not have any of this item to sell", byte0 + 204, byte1 + 239, 3, 0xffff00);
		}
	}

	private final void drawGameMenu()
	{
		gameMenu = new Menu(gameGraphics, 10);
		messagesHandleChatHist = gameMenu.createChatHist(chatBoxX, chatBoxY,
				chatBoxWidth, chatBoxHeight, 1, 20, true);
		chatHandlePlayerEntry = gameMenu.createPlayerChatEntry(chatPlayerEntryX, chatPlayerEntryY,
				chatPlayerEntryWidth, chatPlayerEntryHeight, 1, 80, false, true);
		messagesHandleQuestHist = gameMenu.createChatHist(chatBoxX, chatBoxY,
				chatBoxWidth, chatBoxHeight, 1, 20, true);
		messagesHandlePrivHist = gameMenu.createChatHist(chatBoxX, chatBoxY,
				chatBoxWidth, chatBoxHeight, 1, 20, true);
		gameMenu.setFocus(chatHandlePlayerEntry);
	}

	protected final byte[] load(String filename) {
		return super.load(Config.DATABASE_DIR + File.separator + filename);
	}

	private void drawOptionsPanel()
	{
		gameGraphics.drawBoxAlpha(optPan.getX(), optPan.getY(),
				optPan.getWidth(), optPan.getHeight(),
				optPan.getBGColor(), optPan.getBGAlpha());

		gameGraphics.drawLineX(optPan.getX(),
				optPan.getGameOptions().getY() + optPan.getGameOptions().getHeight(),
				optPan.getWidth(), optPan.getLineColor());
		gameGraphics.drawLineX(optPan.getX(),
				optPan.getClientAssists().getY() + optPan.getClientAssists().getHeight(),
				optPan.getWidth(), optPan.getLineColor());
		gameGraphics.drawLineX(optPan.getX(),
				optPan.getPrivacySettings().getY() + optPan.getPrivacySettings().getHeight(),
				optPan.getWidth(), optPan.getLineColor());
	}

	private void drawGameOptions()
	{
		String info[] = {
				"Camera angle mode", "Mouse buttons", "Sound effects"
		};
		boolean condition[] = {
				configAutoCameraAngle, !configMouseButtons, !configSoundEffects
		};
		int xOffset = 2;
		gameGraphics.drawString("Game options - click to toggle",
				optPan.getX() + xOffset, optPan.getY() + optPan.getHeaderHeight() - 3, 1, 0);
		String color;
		int i = 0;
		int clr;
		for (InGameButton button : optPan.getGameOptions().getButtons())
		{
			optPan.setGameOptionState(i, condition[i]);
			color = (condition[i] ? "@gre@" : "@red@");
			clr = (button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
			gameGraphics.drawString(info[i] + " - " + color
					+ button.getButtonText(), button.getX() + xOffset,
					button.getY() + 10, 1, clr);
			++i;
		}
	}

	private void drawClientAssist()
	{
		String info[] = {
				"Hide Roofs", "Auto Screenshots", "Fightmode Selector", "Textures", "Free Camera"
		};
		boolean condition[] = {
				!showRoof, autoScreenshot, combatWindow, engineHandle.getTextureUse(), freeCamera
		};
		int xOffset = 2;
		gameGraphics.drawString("Client assists - click to toggle",
				optPan.getX() + xOffset,
				optPan.getGameOptions().getY() + optPan.getGameOptions().getHeight()
				+ optPan.getHeaderHeight() - 3, 1, 0);
		String color;
		int i = 0;
		int clr;
		for (InGameButton button : optPan.getClientAssists().getButtons())
		{
			optPan.setClientAssistState(i, condition[i]);
			color = (condition[i] ? "@gre@" : "@red@");
			clr = (button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
			gameGraphics.drawString(info[i] + " - " + color
					+ button.getButtonText(), button.getX() + xOffset,
					button.getY() + 10, 1, clr);
			++i;
		}
	}

	private void drawPrivacySettings()
	{
		String info[] = {
				"Block chat messages", "Block private messages",
				"Block trade requests", "Block duel requests"
		};
		boolean condition[] = {
				!(super.blockChatMessages == 0),
				!(super.blockPrivateMessages == 0),
				!(super.blockTradeRequests == 0),
				!(super.blockDuelRequests == 0)
		};
		int xOffset = 2;
		gameGraphics.drawString("Privacy settings. Will be applied to",
				optPan.getX() + xOffset,
				optPan.getClientAssists().getY() + optPan.getClientAssists().getHeight()
				+ optPan.getHeaderHeight() - 3, 1, 0);
		gameGraphics.drawString("all people not on your friends list",
				optPan.getX() + xOffset,
				optPan.getClientAssists().getY() + optPan.getClientAssists().getHeight()
				+ 2*optPan.getHeaderHeight() - 3, 1, 0);
		String color;
		int i = 0;
		int clr;
		for (InGameButton button : optPan.getPrivacySettings().getButtons())
		{
			optPan.setPrivacySettingsState(i, condition[i]);
			color = (condition[i] ? "@gre@" : "@red@");
			clr = (button.isMouseOverButton(super.mouseX, super.mouseY) ? button.getMouseOverColor() : button.getMouseNotOverColor());
			gameGraphics.drawString(info[i] + ": " + color
					+ button.getButtonText(), button.getX() + xOffset,
					button.getY() + 10, 1, clr);
			++i;
		}
	}

	private void drawLogout()
	{
		int xOffset = 2;
		gameGraphics.drawString("Always logout when you finish",
				optPan.getX() + xOffset,
				optPan.getPrivacySettings().getY() + optPan.getPrivacySettings().getHeight()
				+ optPan.getHeaderHeight() - 3, 1, 0);
		int k1 = 0xffffff;
		if (optPan.getLogoutButton().isMouseOverButton(super.mouseX, super.mouseY))
		{
			k1 = 0xffff00;
		}
		gameGraphics.drawString("Click here to logout",
				optPan.getLogoutButton().getX() + xOffset,
				optPan.getLogoutButton().getY() + optPan.getLogoutButton().getHeight() - 3,
				1, k1);
	}

	private void handleGameOptionsClicks()
	{
		if (mouseButtonClick == 1)
		{
			InGameButton buttons[] = optPan.getGameOptions().getButtons();
			if (buttons[0].isMouseOverButton(super.mouseX, super.mouseY))
			{
				configAutoCameraAngle = !configAutoCameraAngle;
				formatPacket(157, 0, configAutoCameraAngle ? 1 : 0);
			}
			else if (buttons[1].isMouseOverButton(super.mouseX, super.mouseY))
			{
				configMouseButtons = !configMouseButtons;
				formatPacket(157, 2, configMouseButtons ? 1 : 0);
			}
			else if (buttons[2].isMouseOverButton(super.mouseX, super.mouseY))
			{
				configSoundEffects = !configSoundEffects;
				formatPacket(157, 3, configSoundEffects ? 1 : 0);
			}
		}
	}

	private void handleClientAssistClicks()
	{
		if (mouseButtonClick == 1)
		{
			InGameButton buttons[] = optPan.getClientAssists().getButtons();
			if (buttons[0].isMouseOverButton(super.mouseX, super.mouseY))
			{
				showRoof = !showRoof;
				formatPacket(157, 4, showRoof ? 1 : 0);
			}
			else if (buttons[1].isMouseOverButton(super.mouseX, super.mouseY))
			{
				autoScreenshot = !autoScreenshot;
				formatPacket(157, 5, autoScreenshot ? 1 : 0);
			}
			else if (buttons[2].isMouseOverButton(super.mouseX, super.mouseY))
			{
				combatWindow = !combatWindow;
				formatPacket(157, 6, combatWindow ? 1 : 0);
			}
			else if (buttons[3].isMouseOverButton(super.mouseX, super.mouseY))
			{
				engineHandle.setTextureUse(!engineHandle.getTextureUse());
				//TODO: send this toggle to server, also should reload the map
			}
			else if (buttons[4].isMouseOverButton(super.mouseX, super.mouseY))
			{
				freeCamera = !freeCamera;
				//TODO: send this toggle to server, also should reload the map
			}
		}
	}

	private void handlePrivacySettingsClicks()
	{
		if (mouseButtonClick == 1)
		{
			boolean flag1 = false;
			InGameButton buttons[] = optPan.getPrivacySettings().getButtons();
			if (buttons[0].isMouseOverButton(super.mouseX, super.mouseY))
			{
				super.blockChatMessages = 1 - super.blockChatMessages;
				flag1 = true;
			}
			else if (buttons[1].isMouseOverButton(super.mouseX, super.mouseY))
			{
				super.blockPrivateMessages = 1 - super.blockPrivateMessages;
				flag1 = true;
			}
			else if (buttons[2].isMouseOverButton(super.mouseX, super.mouseY))
			{
				super.blockTradeRequests = 1 - super.blockTradeRequests;
				flag1 = true;
			}
			else if (buttons[3].isMouseOverButton(super.mouseX, super.mouseY))
			{
				super.blockDuelRequests = 1 - super.blockDuelRequests;
				flag1 = true;
			}
			if (flag1)
			{
				sendUpdatedPrivacyInfo(
						super.blockChatMessages, super.blockPrivateMessages,
						super.blockTradeRequests, super.blockDuelRequests);
			}
		}
	}

	private void handleLogoutClick()
	{
		if (mouseButtonClick == 1)
			logout();
	}

	private void handleOptionsPanelClicks()
	{
		if (optPan.getGameOptions().isMouseOver(super.mouseX, super.mouseY))
			handleGameOptionsClicks();
		else if (optPan.getClientAssists().isMouseOver(super.mouseX, super.mouseY))
			handleClientAssistClicks();
		else if (optPan.getPrivacySettings().isMouseOver(super.mouseX, super.mouseY))
			handlePrivacySettingsClicks();
		else if (optPan.getLogoutButton().isMouseOverButton(super.mouseX, super.mouseY))
			handleLogoutClick();
		mouseButtonClick = 0;
	}

	private final void drawOptionsMenu(boolean flag)
	{
		optPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		drawOptionsPanel();
		drawGameOptions();
		drawClientAssist();
		drawPrivacySettings();
		drawLogout();
		if (!flag)
			return;
		if (optPan.isMouseOver(super.mouseX, super.mouseY))
			handleOptionsPanelClicks();
		else if (optPan.getFrame().getCloseButton().isMouseOverButton(
				super.mouseX, super.mouseY))
		{ // close button
			menuLength++;
			if (mouseButtonClick == 1)
			{
				mouseOverMenu = 0;
				mouseButtonClick = 0;
			}
		}
		else if (optPan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside settings panel but not on the content or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
	}

	private void updatePlayers()
	{
		for (int i = 0; i < playerCount; i++)
		{
			Mob mob = playerArray[i];
			int k = (mob.waypointCurrent + 1) % 10;
			if (mob.waypointEndSprite != k)
			{
				int currentSprite = -1;
				int l2 = mob.waypointEndSprite;
				double j4;
				if (l2 < k)
					j4 = k - l2;
				else
					j4 = (10 + k) - l2;
				double step = 4;
				if (j4 > 2)
					step = (j4 - 1) * 4;
				step *= EngineHandle.SCALE_FACTOR;
				if (mob.waypointsX[l2] - mob.currentX > EngineHandle.GAME_SIZE * 3
						|| mob.waypointsY[l2] - mob.currentY > EngineHandle.GAME_SIZE * 3
						|| mob.waypointsX[l2] - mob.currentX < -EngineHandle.GAME_SIZE * 3
						|| mob.waypointsY[l2] - mob.currentY < -EngineHandle.GAME_SIZE * 3
						|| j4 > 8)
				{ // too far away
					mob.currentX = mob.waypointsX[l2];
					mob.currentY = mob.waypointsY[l2];
				}
				else
				{
					if (mob.currentX < mob.waypointsX[l2])
					{
						mob.currentX += step;
						mob.stepCount++;
						currentSprite = 2;
					}
					else if (mob.currentX > mob.waypointsX[l2])
					{
						mob.currentX -= step;
						mob.stepCount++;
						currentSprite = 6;
					}
					if (mob.currentX - mob.waypointsX[l2] < step
							&& mob.currentX - mob.waypointsX[l2] > -step)
						mob.currentX = mob.waypointsX[l2];
					if (mob.currentY < mob.waypointsY[l2])
					{
						mob.currentY += step;
						mob.stepCount++;
						if (currentSprite == -1)
							currentSprite = 4;
						else if (currentSprite == 2)
							currentSprite = 3;
						else
							currentSprite = 5;
					}
					else if (mob.currentY > mob.waypointsY[l2])
					{
						mob.currentY -= step;
						mob.stepCount++;
						if (currentSprite == -1)
							currentSprite = 0;
						else if (currentSprite == 2)
							currentSprite = 1;
						else
							currentSprite = 7;
					}
					if (mob.currentY - mob.waypointsY[l2] < step
							&& mob.currentY - mob.waypointsY[l2] > -step)
						mob.currentY = mob.waypointsY[l2];
				}
				if (currentSprite != -1)
					mob.currentSprite = currentSprite;
				if (mob.currentX == mob.waypointsX[l2]
						&& mob.currentY == mob.waypointsY[l2])
					mob.waypointEndSprite = (l2 + 1) % 10;
			}
			else
				mob.currentSprite = mob.nextSprite;
			if (mob.lastMessageTimeout > 0)
				mob.lastMessageTimeout--;
			if (mob.anInt163 > 0)
				mob.anInt163--;
			if (mob.combatTimer > 0)
				mob.combatTimer--;
			if (playerAliveTimeout > 0)
			{
				playerAliveTimeout--;
				if (playerAliveTimeout == 0)
					displayMessage("You have been granted another life. Be more careful this time!", 3, 0);
				if (playerAliveTimeout == 0)
					displayMessage("You retain your skills. Your objects land where you died", 3, 0);
			}
		}
	}

	private void updateNPCs()
	{
		for (int j = 0; j < npcCount; j++)
		{
			Mob mob_1 = npcArray[j];
			int j1 = (mob_1.waypointCurrent + 1) % 10;
			if (mob_1.waypointEndSprite != j1)
			{
				int currentSprite = -1;
				int k4 = mob_1.waypointEndSprite;
				int k5;
				if (k4 < j1)
					k5 = j1 - k4;
				else
					k5 = (10 + j1) - k4;
				int step = 4;
				if (k5 > 2)
					step = (k5 - 1) * 4;
				step *= EngineHandle.SCALE_FACTOR;
				if (mob_1.waypointsX[k4] - mob_1.currentX > EngineHandle.GAME_SIZE * 3
						|| mob_1.waypointsY[k4] - mob_1.currentY > EngineHandle.GAME_SIZE * 3
						|| mob_1.waypointsX[k4] - mob_1.currentX < -EngineHandle.GAME_SIZE * 3
						|| mob_1.waypointsY[k4] - mob_1.currentY < -EngineHandle.GAME_SIZE * 3
						|| k5 > 8)
				{
					mob_1.currentX = mob_1.waypointsX[k4];
					mob_1.currentY = mob_1.waypointsY[k4];
				}
				else
				{
					if (mob_1.currentX < mob_1.waypointsX[k4])
					{
						mob_1.currentX += step;
						mob_1.stepCount++;
						currentSprite = 2;
					}
					else if (mob_1.currentX > mob_1.waypointsX[k4])
					{
						mob_1.currentX -= step;
						mob_1.stepCount++;
						currentSprite = 6;
					}
					if (mob_1.currentX - mob_1.waypointsX[k4] < step
							&& mob_1.currentX - mob_1.waypointsX[k4] > -step)
						mob_1.currentX = mob_1.waypointsX[k4];
					if (mob_1.currentY < mob_1.waypointsY[k4])
					{
						mob_1.currentY += step;
						mob_1.stepCount++;
						if (currentSprite == -1)
							currentSprite = 4;
						else if (currentSprite == 2)
							currentSprite = 3;
						else
							currentSprite = 5;
					}
					else if (mob_1.currentY > mob_1.waypointsY[k4])
					{
						mob_1.currentY -= step;
						mob_1.stepCount++;
						if (currentSprite == -1)
							currentSprite = 0;
						else if (currentSprite == 2)
							currentSprite = 1;
						else
							currentSprite = 7;
					}
					if (mob_1.currentY - mob_1.waypointsY[k4] < step
							&& mob_1.currentY - mob_1.waypointsY[k4] > -step)
						mob_1.currentY = mob_1.waypointsY[k4];
				}
				if (currentSprite != -1)
					mob_1.currentSprite = currentSprite;
				if (mob_1.currentX == mob_1.waypointsX[k4] && mob_1.currentY == mob_1.waypointsY[k4])
					mob_1.waypointEndSprite = (k4 + 1) % 10;
			}
			else
			{
				mob_1.currentSprite = mob_1.nextSprite;
				if (mob_1.type == 43)
					mob_1.stepCount++;
			}
			if (mob_1.lastMessageTimeout > 0)
				mob_1.lastMessageTimeout--;
			if (mob_1.anInt163 > 0)
				mob_1.anInt163--;
			if (mob_1.combatTimer > 0)
				mob_1.combatTimer--;
		}
	}

	private void updateCamera()
	{
		if (cameraAutoAngleDebug)
		{
			if (lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500)
			{
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}
		}
		else
		{
			if (lastAutoCameraRotatePlayerX - ourPlayer.currentX < -500
					|| lastAutoCameraRotatePlayerX - ourPlayer.currentX > 500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY < -500
					|| lastAutoCameraRotatePlayerY - ourPlayer.currentY > 500)
			{
				lastAutoCameraRotatePlayerX = ourPlayer.currentX;
				lastAutoCameraRotatePlayerY = ourPlayer.currentY;
			}
			if (lastAutoCameraRotatePlayerX != ourPlayer.currentX)
				lastAutoCameraRotatePlayerX += (ourPlayer.currentX - lastAutoCameraRotatePlayerX) / (16 + (cameraHeight - 500) / 15);
			if (lastAutoCameraRotatePlayerY != ourPlayer.currentY)
				lastAutoCameraRotatePlayerY += (ourPlayer.currentY - lastAutoCameraRotatePlayerY) / (16 + (cameraHeight - 500) / 15);
			if (configAutoCameraAngle)
			{
				int k1 = cameraAutoAngle * 32;
				int j3 = k1 - cameraZRot;
				byte byte0 = 1;
				if (j3 != 0)
				{
					cameraRotationBaseAddition++;
					if (j3 > 128)
					{
						byte0 = -1;
						j3 = 256 - j3;
					}
					else if (j3 > 0)
						byte0 = 1;
					else if (j3 < -128)
					{
						byte0 = 1;
						j3 = 256 + j3;
					}
					else if (j3 < 0)
					{
						byte0 = -1;
						j3 = -j3;
					}
					cameraZRot += ((cameraRotationBaseAddition * j3 + 255) / 256) * byte0;
					cameraZRot &= 0xff;
				}
				else
					cameraRotationBaseAddition = 0;
			}
		}
	}

	private void checkChatTab()
	{
		if (super.mouseY > windowHeight - 4)
		{ // botom bar; chat tabs & report abuse
			int buttonWidth = 82;
			int xOffset = 14;
			if (super.mouseX > xOffset
					&& super.mouseX < xOffset + buttonWidth
					&& super.lastMouseDownButton == 1)
			{
				messagesTab = 0;
			}
			xOffset += 99;
			if (super.mouseX > xOffset
					&& super.mouseX < xOffset + buttonWidth
					&& super.lastMouseDownButton == 1)
			{
				messagesTab = 1;
				gameMenu.anIntArray187[messagesHandleChatHist] = 0xf423f;
			}
			xOffset += 101;
			if (super.mouseX > xOffset
					&& super.mouseX < xOffset + buttonWidth
					&& super.lastMouseDownButton == 1)
			{
				messagesTab = 2;
				gameMenu.anIntArray187[messagesHandleQuestHist] = 0xf423f;
			}
			xOffset += 101;
			if (super.mouseX > xOffset
					&& super.mouseX < xOffset + buttonWidth
					&& super.lastMouseDownButton == 1)
			{
				messagesTab = 3;
				gameMenu.anIntArray187[messagesHandlePrivHist] = 0xf423f;
			}
			xOffset += 101;
			if (super.mouseX > xOffset
					&& super.mouseX < xOffset + buttonWidth
					&& super.lastMouseDownButton == 1)
			{
				showAbuseWindow = 1;
				abuseSelectedType = 0;
				super.inputText = "";
				super.enteredText = "";
			}
			super.lastMouseDownButton = 0;
			super.mouseDownButton = 0;
		}
	}

	private void updateChatTabs()
	{
		gameMenu.updateActions(super.mouseX, super.mouseY,
				super.lastMouseDownButton, super.mouseDownButton);
		if (messagesTab > 0
				&& super.mouseX >= chatBoxX + this.chatBoxWidth - SCROLL_BAR_WIDTH-2
				&& super.mouseX <= chatBoxX + this.chatBoxWidth
				&& super.mouseY >= chatBoxY
				&& super.mouseY <= chatBoxY + chatBoxHeight)
			super.lastMouseDownButton = 0;
		if (gameMenu.hasActivated(chatHandlePlayerEntry))
		{
			String s = lastMessage = gameMenu.getText(chatHandlePlayerEntry);
			gameMenu.updateText(chatHandlePlayerEntry, "");
			if (s.startsWith("::"))
			{
				s = s.substring(2);
				if (!handleCommand(s))
					sendChatString(s);
			}
			else
			{
				byte[] chatMessage = DataConversions.stringToByteArray(s);
				sendChatMessage(chatMessage, chatMessage.length);
				s = DataConversions.byteToString(chatMessage, 0, chatMessage.length);
				ourPlayer.lastMessageTimeout = 150;
				ourPlayer.lastMessage = s;
				displayMessage(ourPlayer.name + ": " + s, 2, ourPlayer.admin);
			}
		}
		if (messagesTab == 0)
		{
			for (int l1 = 0; l1 < chatBoxVisRows; l1++)
				if (messagesTimeout[l1] > 0)
					messagesTimeout[l1]--;

		}
	}

	private void handleOfferAmounts()
	{
		if (showTradeWindow || showDuelWindow)
		{
			if (super.mouseDownButton != 0)
				mouseDownTime++;
			else
				mouseDownTime = 0;
			if (mouseDownTime > 500)
				itemIncrement += 100000;
			else if (mouseDownTime > 350)
				itemIncrement += 10000;
			else if (mouseDownTime > 250)
				itemIncrement += 1000;
			else if (mouseDownTime > 150)
				itemIncrement += 100;
			else if (mouseDownTime > 100)
				itemIncrement += 10;
			else if (mouseDownTime > 50)
				itemIncrement++;
			else if (mouseDownTime > 20 && (mouseDownTime & 5) == 0)
				itemIncrement++;
		}
		else
		{
			mouseDownTime = 0;
			itemIncrement = 0;
		}
	}

	private void updateCameraPosition()
	{

		if (configAutoCameraAngle)
		{
			if (cameraRotationBaseAddition == 0 || cameraAutoAngleDebug)
			{
				if (super.keyLeftDown)
				{
					cameraAutoAngle = cameraAutoAngle + 1 & 7;
					super.keyLeftDown = false;
					if (!zoomCamera)
					{
						if ((cameraAutoAngle & 1) == 0)
							cameraAutoAngle = cameraAutoAngle + 1 & 7;
						for (int i2 = 0; i2 < 8; i2++)
						{
							if (enginePlayerVisible(cameraAutoAngle))
								break;
							cameraAutoAngle = cameraAutoAngle + 1 & 7;
						}

					}
				}
				if (super.keyRightDown)
				{
					cameraAutoAngle = cameraAutoAngle + 7 & 7;
					super.keyRightDown = false;
					if (!zoomCamera) {
						if ((cameraAutoAngle & 1) == 0)
							cameraAutoAngle = cameraAutoAngle + 7 & 7;
						for (int j2 = 0; j2 < 8; j2++) {
							if (enginePlayerVisible(cameraAutoAngle))
								break;
							cameraAutoAngle = cameraAutoAngle + 7 & 7;
						}

					}
				}
			}
		}
		else if (super.keyLeftDown)
			cameraZRot = cameraZRot + 2 & 0xff;
		else if (super.keyRightDown)
			cameraZRot = cameraZRot - 2 & 0xff;
		else if (super.keyUpDown && cameraXRot > 0x390)
			cameraXRot = cameraXRot - 8 & 0x3ff;
		else if (super.keyDownDown && cameraXRot < 0x3f8)
			cameraXRot = cameraXRot + 8 & 0x3ff;
		if (zoomCamera && cameraHeight > 550)
			cameraHeight -= 4;
		else if (!zoomCamera && cameraHeight < 750)
			cameraHeight += 4;
	}

	private final void processGame()
	{
		if (systemUpdate > 1)
			systemUpdate--;
		sendPingPacketReadPacketData();
		if (logoutTimeout > 0)
			logoutTimeout--;
		if (ourPlayer.currentSprite == 8
				|| ourPlayer.currentSprite == 9)
			lastWalkTimeout = 500;
		if (lastWalkTimeout > 0)
			lastWalkTimeout--;
		if (showCharacterLookScreen)
		{
			drawCharacterLookScreen();
			return;
		}
		updatePlayers(); // player walking animations
		updateNPCs(); // npc walking animations

		if (mouseOverMenu != 2)
		{
			if (GameImage.anInt346 > 0)
				anInt658++;
			if (GameImage.anInt347 > 0)
				anInt658 = 0;
			GameImage.anInt346 = 0;
			GameImage.anInt347 = 0;
		}
		for (int l = 0; l < playerCount; l++)
		{
			Mob mob_2 = playerArray[l];
			if (mob_2.anInt176 > 0)
				mob_2.anInt176--;
		}
		updateCamera();

		if (anInt658 > 20)
			anInt658 = 0;

		checkChatTab();
		updateChatTabs();

		if (playerAliveTimeout != 0)
			super.lastMouseDownButton = 0;
		handleOfferAmounts();
		if (super.lastMouseDownButton == 1)
			mouseButtonClick = 1;
		else if (super.lastMouseDownButton == 2)
			mouseButtonClick = 2;
		gameCamera.updateMouseCoords(super.mouseX, super.mouseY);
		super.lastMouseDownButton = 0;
		updateCameraPosition();

		if (actionPictureType > 0)
			actionPictureType--;
		else if (actionPictureType < 0)
			actionPictureType++;
		gameCamera.animateTexture(17);
		modelUpdatingTimer++;
		if (modelUpdatingTimer > 5)
		{
			modelUpdatingTimer = 0;
			modelFireLightningSpellNumber = (modelFireLightningSpellNumber + 1) % 3;
			modelTorchNumber = (modelTorchNumber + 1) % 4;
			modelClawSpellNumber = (modelClawSpellNumber + 1) % 5;
		}
		/* updating models blow it seems */
		for (int k2 = 0; k2 < objectCount; k2++)
		{
			int xObj = objectX[k2];
			int yObj = objectY[k2];
			if (xObj >= 0 && yObj >= 0
					&& xObj < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_WIDTH
					&& yObj < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_HEIGHT
					&& objectType[k2] == 74)
				objectModelArray[k2].addRotation(1, 0, 0);
		}

		for (int i4 = 0; i4 < anInt892; i4++)
		{
			anIntArray923[i4]++;
			if (anIntArray923[i4] > 50)
			{
				anInt892--;
				for (int i5 = i4; i5 < anInt892; i5++) {
					anIntArray944[i5] = anIntArray944[i5 + 1];
					anIntArray757[i5] = anIntArray757[i5 + 1];
					anIntArray923[i5] = anIntArray923[i5 + 1];
					anIntArray782[i5] = anIntArray782[i5 + 1];
				}

			}
		}

	}

	private final void loadSounds()
	{
		try
		{
			drawLoadingBarText(90, "Unpacking Sound effects");
			sounds = load("sounds.mem");
			audioReader = new AudioReader();
			return;
		}
		catch (Throwable throwable)
		{
			System.out.println("Unable to init sounds:" + throwable);
		}
	}

	private final void drawCombatStyleWindow()
	{
		byte byte0 = 7;
		byte byte1 = 15;
		char c = '\257';
		if (mouseButtonClick != 0)
		{
			for (int i = 0; i < 5; i++)
			{
				if (i <= 0
						|| super.mouseX <= byte0
						|| super.mouseX >= byte0 + c
						|| super.mouseY <= byte1 + i * 20
						|| super.mouseY >= byte1 + i * 20 + 20)
					continue;
				combatStyle = i - 1;
				mouseButtonClick = 0;
				formatPacket(42, combatStyle, -1);
				break;
			}

		}
		for (int j = 0; j < 5; j++)
		{
			if (j == combatStyle + 1)
				gameGraphics.drawBoxAlpha(byte0, byte1 + j * 20, c, 20, GameImage.convertRGBToLong(255, 0, 0), 0x80);
			else
				gameGraphics.drawBoxAlpha(byte0, byte1 + j * 20, c, 20, GameImage.convertRGBToLong(190, 190, 190), 0x80);
			gameGraphics.drawLineX(byte0, byte1 + j * 20, c, 0);
			gameGraphics.drawLineX(byte0, byte1 + j * 20 + 20, c, 0);
		}

		gameGraphics.drawText("Select combat style", byte0 + c / 2, byte1 + 16, 3, 0xffffff);
		gameGraphics.drawText("Controlled (+1 of each)", byte0 + c / 2, byte1 + 36, 3, 0);
		gameGraphics.drawText("Aggressive (+3 strength)", byte0 + c / 2, byte1 + 56, 3, 0);
		gameGraphics.drawText("Accurate   (+3 attack)", byte0 + c / 2, byte1 + 76, 3, 0);
		gameGraphics.drawText("Defensive  (+3 defense)", byte0 + c / 2, byte1 + 96, 3, 0);
	}

	private final void drawDuelConfirmWindow() {
		byte byte0 = 22;
		byte byte1 = 36;
		gameGraphics.drawBox(byte0, byte1, 468, 16, 192);
		int i = 0x989898;
		gameGraphics.drawBoxAlpha(byte0, byte1 + 16, 468, 246, i, 160);
		gameGraphics.drawText("Please confirm your duel with @yel@" + DataOperations.longToString(duelOpponentNameLong), byte0 + 234, byte1 + 12, 1, 0xffffff);
		gameGraphics.drawText("Your stake:", byte0 + 117, byte1 + 30, 1, 0xffff00);
		for (int j = 0; j < duelConfirmMyItemCount; j++) {
			String s = duelConfirmMyItemsI[j].name();
			if (duelConfirmMyItemsI[j].stackable())
				s = s + " x " + method74(duelConfirmMyItemsI[j].amount);
			gameGraphics.drawText(s, byte0 + 117, byte1 + 42 + j * 12, 1, 0xffffff);
		}

		if (duelConfirmMyItemCount == 0)
			gameGraphics.drawText("Nothing!", byte0 + 117, byte1 + 42, 1, 0xffffff);
		gameGraphics.drawText("Your opponent's stake:", byte0 + 351, byte1 + 30, 1, 0xffff00);
		for (int k = 0; k < duelConfirmOpponentItemCount; k++) {
			String s1 = duelConfirmOpponentItemsI[k].name();
			if (duelConfirmOpponentItemsI[k].stackable())
				s1 = s1 + " x " + method74(duelConfirmOpponentItemsI[k].amount);
			gameGraphics.drawText(s1, byte0 + 351, byte1 + 42 + k * 12, 1, 0xffffff);
		}

		if (duelConfirmOpponentItemCount == 0)
			gameGraphics.drawText("Nothing!", byte0 + 351, byte1 + 42, 1, 0xffffff);
		if (duelCantRetreat == 0)
			gameGraphics.drawText("You can retreat from this duel", byte0 + 234, byte1 + 180, 1, 65280);
		else
			gameGraphics.drawText("No retreat is possible!", byte0 + 234, byte1 + 180, 1, 0xff0000);
		if (duelUseMagic == 0)
			gameGraphics.drawText("Magic may be used", byte0 + 234, byte1 + 192, 1, 65280);
		else
			gameGraphics.drawText("Magic cannot be used", byte0 + 234, byte1 + 192, 1, 0xff0000);
		if (duelUsePrayer == 0)
			gameGraphics.drawText("Prayer may be used", byte0 + 234, byte1 + 204, 1, 65280);
		else
			gameGraphics.drawText("Prayer cannot be used", byte0 + 234, byte1 + 204, 1, 0xff0000);
		if (duelUseWeapons == 0)
			gameGraphics.drawText("Weapons may be used", byte0 + 234, byte1 + 216, 1, 65280);
		else
			gameGraphics.drawText("Weapons cannot be used", byte0 + 234, byte1 + 216, 1, 0xff0000);
		gameGraphics.drawText("If you are sure click 'Accept' to begin the duel", byte0 + 234, byte1 + 230, 1, 0xffffff);
		if (!duelWeAccept) {
			gameGraphics.drawPicture((byte0 + 118) - 35, byte1 + 238, SPRITE_MEDIA_START + 25);
			gameGraphics.drawPicture((byte0 + 352) - 35, byte1 + 238, SPRITE_MEDIA_START + 26);
		} else {
			gameGraphics.drawText("Waiting for other player...", byte0 + 234, byte1 + 250, 1, 0xffff00);
		}
		if (mouseButtonClick == 1) {
			if (super.mouseX < byte0 || super.mouseY < byte1 || super.mouseX > byte0 + 468 || super.mouseY > byte1 + 262) {
				showDuelConfirmWindow = false;
				super.streamClass.createPacket(35);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 118) - 35 && super.mouseX <= byte0 + 118 + 70 && super.mouseY >= byte1 + 238 && super.mouseY <= byte1 + 238 + 21) {
				duelWeAccept = true;
				super.streamClass.createPacket(87);
				super.streamClass.formatPacket();
			}
			if (super.mouseX >= (byte0 + 352) - 35 && super.mouseX <= byte0 + 353 + 70 && super.mouseY >= byte1 + 238 && super.mouseY <= byte1 + 238 + 21) {
				showDuelConfirmWindow = false;
				super.streamClass.createPacket(35);
				super.streamClass.formatPacket();
			}
			mouseButtonClick = 0;
		}
	}

	private final void updateBankItems() {
		bankItemCount = newBankItemCount;
		for (int i = 0; i < newBankItemCount; i++) {
			bankItemsI[i] = newBankItemsI[i];
		}

		for (int j = 0; j < inventoryCount; j++) {
			if (bankItemCount >= bankItemsMax)
				break;
			int k = inventory[j].id;
			boolean flag = false;
			for (int l = 0; l < bankItemCount; l++) {
				if (bankItemsI[l].id != k)
					continue;
				flag = true;
				break;
			}

			if (!flag) {
				bankItemsI[bankItemCount++] = new Item(k, 0);
			}
		}

	}

	private final void makeCharacterDesignMenu()
	{
		chrDesignMenu = new Menu(gameGraphics, 100);
		chrDesignMenu.drawText(windowHalfWidth, windowHalfHeight - 157, "Please design Your Character", 4, true);
		int i = windowHalfWidth - 116;
		int j = windowHalfHeight - 133;
		i += 116;
		j -= 10;
		chrDesignMenu.drawText(i - 55, j + 110, "Front", 3, true);
		chrDesignMenu.drawText(i, j + 110, "Side", 3, true);
		chrDesignMenu.drawText(i + 55, j + 110, "Back", 3, true);
		byte byte0 = 54;
		j += 145;
		chrDesignMenu.method157(i - byte0, j, 53, 41);
		chrDesignMenu.drawText(i - byte0, j - 8, "Head", 1, true);
		chrDesignMenu.drawText(i - byte0, j + 8, "Type", 1, true);
		chrDesignMenu.method158(i - byte0 - 40, j, SPRITE_UTIL_START + 7);
		chrDesignHeadBtnLeft = chrDesignMenu.makeButton(i - byte0 - 40, j, 20, 20);
		chrDesignMenu.method158((i - byte0) + 40, j, SPRITE_UTIL_START + 6);
		chrDesignHeadBtnRight = chrDesignMenu.makeButton((i - byte0) + 40, j, 20, 20);
		chrDesignMenu.method157(i + byte0, j, 53, 41);
		chrDesignMenu.drawText(i + byte0, j - 8, "Hair", 1, true);
		chrDesignMenu.drawText(i + byte0, j + 8, "Colour", 1, true);
		chrDesignMenu.method158((i + byte0) - 40, j, SPRITE_UTIL_START + 7);
		chrDesignHairClrBtnLeft = chrDesignMenu.makeButton((i + byte0) - 40, j, 20, 20);
		chrDesignMenu.method158(i + byte0 + 40, j, SPRITE_UTIL_START + 6);
		chrDesignHairClrBtnRight = chrDesignMenu.makeButton(i + byte0 + 40, j, 20, 20);
		j += 50;
		chrDesignMenu.method157(i - byte0, j, 53, 41);
		chrDesignMenu.drawText(i - byte0, j, "Gender", 1, true);
		chrDesignMenu.method158(i - byte0 - 40, j, SPRITE_UTIL_START + 7);
		chrDesignGenderBtnLeft = chrDesignMenu.makeButton(i - byte0 - 40, j, 20, 20);
		chrDesignMenu.method158((i - byte0) + 40, j, SPRITE_UTIL_START + 6);
		chrDesignGenderBtnRight = chrDesignMenu.makeButton((i - byte0) + 40, j, 20, 20);
		chrDesignMenu.method157(i + byte0, j, 53, 41);
		chrDesignMenu.drawText(i + byte0, j - 8, "Top", 1, true);
		chrDesignMenu.drawText(i + byte0, j + 8, "Colour", 1, true);
		chrDesignMenu.method158((i + byte0) - 40, j, SPRITE_UTIL_START + 7);
		chrDesignTopClrBtnLeft = chrDesignMenu.makeButton((i + byte0) - 40, j, 20, 20);
		chrDesignMenu.method158(i + byte0 + 40, j, SPRITE_UTIL_START + 6);
		chrDesignTopClrBtnRight = chrDesignMenu.makeButton(i + byte0 + 40, j, 20, 20);
		j += 50;
		chrDesignMenu.method157(i - byte0, j, 53, 41);
		chrDesignMenu.drawText(i - byte0, j - 8, "Skin", 1, true);
		chrDesignMenu.drawText(i - byte0, j + 8, "Colour", 1, true);
		chrDesignMenu.method158(i - byte0 - 40, j, SPRITE_UTIL_START + 7);
		chrDesignSkinClrBtnLeft = chrDesignMenu.makeButton(i - byte0 - 40, j, 20, 20);
		chrDesignMenu.method158((i - byte0) + 40, j, SPRITE_UTIL_START + 6);
		chrDesignSkinClrBtnRight = chrDesignMenu.makeButton((i - byte0) + 40, j, 20, 20);
		chrDesignMenu.method157(i + byte0, j, 53, 41);
		chrDesignMenu.drawText(i + byte0, j - 8, "Bottom", 1, true);
		chrDesignMenu.drawText(i + byte0, j + 8, "Colour", 1, true);
		chrDesignMenu.method158((i + byte0) - 40, j, SPRITE_UTIL_START + 7);
		chrDesignBottomClrBtnLeft = chrDesignMenu.makeButton((i + byte0) - 40, j, 20, 20);
		chrDesignMenu.method158(i + byte0 + 40, j, SPRITE_UTIL_START + 6);
		chrDesignBottomClrBtnRight = chrDesignMenu.makeButton(i + byte0 + 40, j, 20, 20);
		j += 82;
		j -= 35;
		chrDesignMenu.drawBox(i, j, 200, 30, 0x343434, 0x000000, 0xc0);
		chrDesignMenu.drawText(i, j, "Accept", 4, false);
		characterDesignAcceptButton = chrDesignMenu.makeButton(i, j, 200, 30);
	}

	private final void drawAbuseWindow2()
	{
		if (super.enteredText.length() > 0)
		{
			String s = super.enteredText.trim();
			super.inputText = "";
			super.enteredText = "";
			if (s.length() > 0) {
				long l = DataOperations.stringLength12ToLong(s);
				super.streamClass.createPacket(7);
				super.streamClass.addTwo4ByteInts(l);
				super.streamClass.addByte(abuseSelectedType);
				super.streamClass.formatPacket();
			}
			showAbuseWindow = 0;
			return;
		}
		gameGraphics.drawBox(windowHalfWidth - 200, windowHalfHeight - 37, 400, 100, 0);
		gameGraphics.drawBoxEdge(windowHalfWidth - 200, windowHalfHeight - 37, 400, 100, 0xffffff);
		int i = windowHalfHeight - 7;
		gameGraphics.drawText("Now type the name of the offending player, and press enter", windowHalfWidth, i, 1, 0xffff00);
		i += 18;
		gameGraphics.drawText("Name: " + super.inputText + "*", windowHalfWidth, i, 4, 0xffffff);
		i = windowHalfHeight + 55;
		int j = 0xffffff;
		if (super.mouseX > windowHalfWidth - 60 && super.mouseX < windowHalfWidth + 60
				&& super.mouseY > i - 13 && super.mouseY < i + 2) {
			j = 0xffff00;
			if (mouseButtonClick == 1) {
				mouseButtonClick = 0;
				showAbuseWindow = 0;
			}
		}
		gameGraphics.drawText("Click here to cancel", windowHalfWidth, i, 1, j);
		if (mouseButtonClick == 1
				&& (super.mouseX < windowHalfWidth - 200
						|| super.mouseX > windowHalfWidth + 200
						|| super.mouseY < windowHalfHeight - 37
						|| super.mouseY > windowHalfHeight + 63)) {
			mouseButtonClick = 0;
			showAbuseWindow = 0;
		}
	}

	private final void displayMessage(String message, int type, int status)
	{
		if (type == 2 || type == 4 || type == 6) {
			for (; message.length() > 5 && message.charAt(0) == '@' && message.charAt(4) == '@'; message = message.substring(5))
				;
		}
		message = message.replaceAll("\\#pmd\\#", "");
		message = message.replaceAll("\\#mod\\#", "");
		message = message.replaceAll("\\#adm\\#", "");
		if (type == 2)
			message = "@yel@" + message;
		if (type == 3 || type == 4)
			message = "@whi@" + message;
		if (type == 6)
			message = "@cya@" + message;
		if (status == 1)
			message = "#pmd#" + message;
		if (status == 2)
			message = "#mod#" + message;
		if (status == 3)
			message = "#adm#" + message;
		if (messagesTab != 0) {
			if (type == 4 || type == 3)
				anInt952 = 200;
			if (type == 2 && messagesTab != 1)
				anInt953 = 200;
			if (type == 5 && messagesTab != 2)
				anInt954 = 200;
			if (type == 6 && messagesTab != 3)
				anInt955 = 200;
			if (type == 3 && messagesTab != 0)
				messagesTab = 0;
			if (type == 6 && messagesTab != 3 && messagesTab != 0)
				messagesTab = 0;
		}
		for (int k = chatBoxVisRows-1; k > 0; k--)
		{
			messagesArray[k] = messagesArray[k - 1];
			messagesTimeout[k] = messagesTimeout[k - 1];
		}

		messagesArray[0] = message;
		messagesTimeout[0] = 300;

		if (type == 2)
		{
			if (gameMenu.anIntArray187[messagesHandleChatHist] == gameMenu.menuListTextCount[messagesHandleChatHist] - chatBoxVisRows + 1)
				gameMenu.addString(messagesHandleChatHist, message, true);
			else
				gameMenu.addString(messagesHandleChatHist, message, false);
		}
		if (type == 5)
			if (gameMenu.anIntArray187[messagesHandleQuestHist] == gameMenu.menuListTextCount[messagesHandleQuestHist] - chatBoxVisRows + 1)
				gameMenu.addString(messagesHandleQuestHist, message, true);
			else
				gameMenu.addString(messagesHandleQuestHist, message, false);
		if (type == 6) {
			if (gameMenu.anIntArray187[messagesHandlePrivHist] == gameMenu.menuListTextCount[messagesHandlePrivHist] - chatBoxVisRows + 1) {
				gameMenu.addString(messagesHandlePrivHist, message, true);
				return;
			}
			gameMenu.addString(messagesHandlePrivHist, message, false);
		}
	}

	protected final void logoutAndStop() {
		sendLogoutPacket();
		garbageCollect();
		if (audioReader != null) {
			audioReader.stopAudio();
		}
	}

	private final void animateObject(int i, String s) {
		int x = objectX[i];
		int y = objectY[i];
		double tileXDist = x - EngineHandle.getTileXFromX(ourPlayer.currentX);
		double tileYDist = y - EngineHandle.getTileYFromY(ourPlayer.currentY);
		double maxAnimateDist = gameCamera.drawModelMaxDist / EngineHandle.GAME_SIZE;
		boolean animate = Math.sqrt(tileXDist*tileXDist + tileYDist*tileYDist) < maxAnimateDist;
		if (animate && x >= 0 && y >= 0
				&& x < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_WIDTH
				&& y < EngineHandle.VISIBLE_SECTORS*EngineHandle.SECTOR_HEIGHT)
		{
			gameCamera.removeModel(objectModelArray[i]);
			int newMdlIdx = EntityHandler.storeModel(s);
			try {
				Model model = gameDataModels[newMdlIdx].newModel();
				gameCamera.addModel(model);
				model.setLightAndGradAndSource(true, 48, 48, Camera.light_x, Camera.light_z, Camera.light_y);
				model.copyRotTrans(objectModelArray[i]);
				model.anInt257 = i;
				objectModelArray[i] = model;
			}
			catch (Exception e) {
			}
		}
	}

	protected final void resetVars() {
		systemUpdate = 0;
		combatStyle = 0;
		logoutTimeout = 0;
		//loginScreenNumber = 0; // menuwelcome
		loginScreenNumber = 2;
		loggedIn = true;
		resetPrivateMessageStrings();
		gameGraphics.resetImagePixels(0);
		gameGraphics.drawImage(aGraphics936, 0, 0);
		for (int i = 0; i < objectCount; i++) {
			gameCamera.removeModel(objectModelArray[i]);
			engineHandle.updateObject(objectX[i], objectY[i], objectType[i], objectID[i]);
		}

		for (int j = 0; j < doorCount; j++) {
			gameCamera.removeModel(doorModel[j]);
			engineHandle.updateDoor(doorX[j], doorY[j], doorDirection[j], doorType[j]);
		}

		objectCount = 0;
		doorCount = 0;
		groundItemCount = 0;
		playerCount = 0;
		for (int k = 0; k < mobArray.length; k++)
			mobArray[k] = null;

		for (int l = 0; l < playerArray.length; l++)
			playerArray[l] = null;

		npcCount = 0;
		for (int i1 = 0; i1 < npcRecordArray.length; i1++)
			npcRecordArray[i1] = null;

		for (int j1 = 0; j1 < npcArray.length; j1++)
			npcArray[j1] = null;

		for (int k1 = 0; k1 < prayerOn.length; k1++)
			prayerOn[k1] = false;

		mouseButtonClick = 0;
		super.lastMouseDownButton = 0;
		super.mouseDownButton = 0;
		showShop = false;
		showBank = false;
		super.friendsCount = 0;
		
		for (int i = 0; i < rightClickMenu.length; rightClickMenu[i++] = new MenuRightClick());
		
		for (int i = 0; i < bankItemsI.length; bankItemsI[i++] = new Item(0));
		for (int i = 0; i < duelMyItemsI.length; duelMyItemsI[i++] = new Item(0));
		for (int i = 0; i < tradeConfirmItemsI.length; tradeConfirmItemsI[i++] = new Item(0));
		for (int i = 0; i < tradeConfirmOtherItemsI.length; tradeConfirmOtherItemsI[i++] = new Item(0));
		for (int i = 0; i < inventory.length; inventory[i++] = new Item(0));
		for (int i = 0; i < duelOpponentItemsI.length; duelOpponentItemsI[i++] = new Item(0));
		for (int i = 0; i < duelConfirmOpponentItemsI.length; duelConfirmOpponentItemsI[i++] = new Item(0));
		for (int i = 0; i < newBankItemsI.length; newBankItemsI[i++] = new Item(0));
		for (int i = 0; i < duelConfirmMyItemsI.length; duelConfirmMyItemsI[i++] = new Item(0));
		for (int i = 0; i < tradeOtherItemsI.length; tradeOtherItemsI[i++] = new Item(0));
		for (int i = 0; i < tradeMyItemsI.length; tradeMyItemsI[i++] = new Item(0));
	}

	private void drawTradePanel()
	{
		tradeCfrmPan.getFrame().setTitle("Trading with: @yel@"
				+ DataOperations.longToString(tradeConfirmOtherNameLong));
		gameGraphics.drawBoxAlpha(tradePan.getPlrTextBoxX(),
				tradePan.getPlrTextBoxY(), tradePan.getPlrTextBoxWidth(),
				tradePan.getPlrTextBoxHeight(), tradePan.getBGColor(),
				tradePan.getBGAlpha());
		gameGraphics.drawBoxAlpha(tradePan.getMdlMarginBarX(),
				tradePan.getMdlMarginBarY(), tradePan.getMdlMarginBarWidth(),
				tradePan.getMdlMarginBarHeight(), tradePan.getBGColor(),
				tradePan.getBGAlpha());
		gameGraphics.drawBoxAlpha(tradePan.getOpntTextBoxX(),
				tradePan.getOpntTextBoxY(), tradePan.getOpntTextBoxWidth(),
				tradePan.getOpntTextBoxHeight(), tradePan.getBGColor(),
				tradePan.getBGAlpha());
		gameGraphics.drawBoxAlpha(tradePan.getAccptDclnBoxX(),
				tradePan.getAccptDclnBoxY(), tradePan.getAccptDclnBoxWidth(),
				tradePan.getAccptDclnBoxHeight(), tradePan.getBGColor(),
				tradePan.getBGAlpha());
		gameGraphics.drawBoxAlpha(tradePan.getItemInfoBarX(),
				tradePan.getItemInfoBarY(), tradePan.getItemInfoBarWidth(),
				tradePan.getItemInfoBarHeight(), tradePan.getBGColor(),
				tradePan.getBGAlpha());
		tradePan.getFrame().setTitle("Trading with: " + tradeOtherPlayerName);
		gameGraphics.drawString("Your Offer", tradePan.getPlrTextBoxX()+1,
				tradePan.getPlrTextBoxY()+tradePan.getPlrTextBoxHeight()-3,
				4, 0xffffff);
		gameGraphics.drawString("Opponent's Offer", tradePan.getOpntTextBoxX()+1,
				tradePan.getOpntTextBoxY()+tradePan.getOpntTextBoxHeight()-3,
				4, 0xffffff);
		gameGraphics.drawString("Your Inventory", tradePan.getInvGrid().getX(),
				tradePan.getPlrTextBoxY()+tradePan.getPlrTextBoxHeight()-3,
				4, 0xffffff);
	}

	private void handleTradePanelClicks()
	{
		if (mouseButtonClick == 1)
		{
			if (tradePan.getAcceptButton().isMouseOverButton(super.mouseX, super.mouseY))
				this.formatPacket(211, -1, -1);
			if (tradePan.getDeclineButton().isMouseOverButton(super.mouseX, super.mouseY))
				this.formatPacket(216, -1, -1);
		}
	}

	private void drawTradeInventoryGrid()
	{
		InGameGrid invGrid = tradePan.getInvGrid();
		for (int j = 0; j < invGrid.getSlots(); j++)
		{
			int col = invGrid.getX() + (j % invGrid.getCols()) * InGameGrid.ITEM_SLOT_WIDTH;
			int row = invGrid.getY() + (j / invGrid.getCols()) * InGameGrid.ITEM_SLOT_HEIGHT;

			drawItemBox(invGrid, inventory[j].id, col, row,
					false, j < inventoryCount && inventory[j].id != -1);
			if (j < inventoryCount && inventory[j].id != -1
					&& inventory[j].stackable())
				drawTradeInvText(col, row, inventory[j].amount);
			if (super.mouseX > col &&
					super.mouseX < col + InGameGrid.ITEM_SLOT_WIDTH
					&& super.mouseY > row
					&& super.mouseY < row + InGameGrid.ITEM_SLOT_HEIGHT
					&& j < inventoryCount && inventory[j].id != -1)
				gameGraphics.drawString(
						inventory[j].name() + ": @whi@" + inventory[j].description(),
						tradePan.getX() + 2,
						tradePan.getItemInfoBarY() + tradePan.getItemInfoBarHeight() - 5,
						1, 0xffff00);
		}
	}

	private void drawTradePlrOfferGrid()
	{
		InGameGrid myOfferGrid = tradePan.getOfferGrid();
		for (int j = 0; j < myOfferGrid.getSlots(); j++)
		{
			int col = myOfferGrid.getX() + (j % myOfferGrid.getCols()) * InGameGrid.ITEM_SLOT_WIDTH;
			int row = myOfferGrid.getY() + (j / myOfferGrid.getCols()) * InGameGrid.ITEM_SLOT_HEIGHT;

			drawItemBox(myOfferGrid, tradeMyItemsI[j].id, col, row,
					false, j < tradeMyItemCount && tradeMyItemsI[j].id != -1);
			if (j < tradeMyItemCount && tradeMyItemsI[j].id != -1
					&& tradeMyItemsI[j].stackable())
				drawTradeInvText(col, row, tradeMyItemsI[j].amount);
			if (super.mouseX > col &&
					super.mouseX < col + InGameGrid.ITEM_SLOT_WIDTH
					&& super.mouseY > row
					&& super.mouseY < row + InGameGrid.ITEM_SLOT_HEIGHT
					&& j < tradeMyItemCount && tradeMyItemsI[j].id != -1)
				gameGraphics.drawString(tradeMyItemsI[j].name()
						+ ": @whi@" + tradeMyItemsI[j].description(),
						tradePan.getX() + 2,
						tradePan.getItemInfoBarY() + tradePan.getItemInfoBarHeight() - 5,
						1, 0xffff00);
		}
	}

	private void drawTradeOpntOfferGrid()
	{
		InGameGrid otherOfferGrid = tradePan.getOtherOfferGrid();
		for (int j = 0; j < otherOfferGrid.getSlots(); j++)
		{
			int col = otherOfferGrid.getX() + (j % otherOfferGrid.getCols()) * InGameGrid.ITEM_SLOT_WIDTH;
			int row = otherOfferGrid.getY() + (j / otherOfferGrid.getCols()) * InGameGrid.ITEM_SLOT_HEIGHT;

			drawItemBox(otherOfferGrid, tradeOtherItemsI[j].id, col, row,
					false, j < tradeOtherItemCount && tradeOtherItemsI[j].id != -1);
			if (j < tradeOtherItemCount && tradeOtherItemsI[j].id != -1
					&& tradeOtherItemsI[j].stackable())
				drawTradeInvText(col, row, tradeOtherItemsI[j].amount);
			if (super.mouseX > col &&
					super.mouseX < col + InGameGrid.ITEM_SLOT_WIDTH
					&& super.mouseY > row
					&& super.mouseY < row + InGameGrid.ITEM_SLOT_HEIGHT
					&& j < tradeOtherItemCount && tradeOtherItemsI[j].id != -1)
				gameGraphics.drawString(tradeOtherItemsI[j].name()
						+ ": @whi@" + tradeOtherItemsI[j].description(),
						tradePan.getX() + 2,
						tradePan.getItemInfoBarY() + tradePan.getItemInfoBarHeight() - 5,
						1, 0xffff00);
		}
	}

	private void drawTradeInvText(int col, int row, int amount)
	{
		gameGraphics.drawString(getAbbreviatedValue(amount),
				col + 1, row + 10, 1, tradePan.getInvCountTextColor());
	}

	private void updateTradeStatus()
	{
		if (!tradeWeAccepted)
			gameGraphics.drawPicture(tradePan.getAcceptButton().getX(),
					tradePan.getAcceptButton().getY(),
					tradePan.getAcceptButton().getSpriteIdx());
		gameGraphics.drawPicture(tradePan.getDeclineButton().getX(),
				tradePan.getDeclineButton().getY(),
				tradePan.getDeclineButton().getSpriteIdx());
		if (tradeOtherAccepted)
		{
			gameGraphics.drawText("Other player",
					tradePan.getAccptDclnBoxX()+tradePan.getAccptDclnBoxWidth()/2,
					tradePan.getAccptDclnBoxY()+11, 1, 0xffffff);
			gameGraphics.drawText("has accepted",
					tradePan.getAccptDclnBoxX()+tradePan.getAccptDclnBoxWidth()/2,
					tradePan.getAccptDclnBoxY()+21, 1, 0xffffff);
		}
		if (tradeWeAccepted) {
			gameGraphics.drawText("Waiting for",
					tradePan.getAccptDclnBoxX()+tradePan.getAcceptButton().getSprite().getWidth()/2,
					tradePan.getAccptDclnBoxY()+11, 1, 0xffffff);
			gameGraphics.drawText("other player",
					tradePan.getAccptDclnBoxX()+tradePan.getAcceptButton().getSprite().getWidth()/2,
					tradePan.getAccptDclnBoxY()+21, 1, 0xffffff);
		}
	}

	private void handleMouseOverTradeInv()
	{
		InGameGrid invGrid = tradePan.getInvGrid();
		int slotIdx = (super.mouseX - (invGrid.getX()+1)) / InGameGrid.ITEM_SLOT_WIDTH
				+ ((super.mouseY - (invGrid.getY()+1)) / InGameGrid.ITEM_SLOT_HEIGHT) * invGrid.getCols();
		if (slotIdx >= 0 && slotIdx < inventoryCount)
		{
			boolean flag = false;
			int l1 = 0;
			int slotItemId = inventory[slotIdx].id;
			for (int k3 = 0; k3 < tradeMyItemCount; k3++)
			{
				if (tradeMyItemsI[k3].id == slotItemId)
				{
					if (EntityHandler.getItemDef(slotItemId).isStackable())
					{
						for (int i4 = 0; i4 < itemIncrement; i4++)
						{
							if (tradeMyItemsI[k3].amount < inventory[slotIdx].amount)
							{
								tradeMyItemsI[k3].amount++;
							}
							flag = true;
						}

					}
					else
						l1++;
				}
			}
			if (inventoryCount(slotItemId) <= l1)
				flag = true;
			if (!flag && tradeMyItemCount < 12)
			{
				tradeMyItemsI[tradeMyItemCount++] = new Item(slotItemId, 1);
				flag = true;
			}
			if (flag)
				formatPacket(70, -1, -1);
		}
	}

	private void handleMouseOverPlrTradeOffer()
	{
		InGameGrid myOfferGrid = tradePan.getOfferGrid();
		int l = (super.mouseX - (myOfferGrid.getX()+1)) / InGameGrid.ITEM_SLOT_WIDTH
				+ ((super.mouseY - (myOfferGrid.getY()+1)) / InGameGrid.ITEM_SLOT_HEIGHT) * myOfferGrid.getCols();
		if (l >= 0 && l < tradeMyItemCount)
		{
			int j1 = tradeMyItemsI[l].id;
			for (int i2 = 0; i2 < itemIncrement; i2++)
			{
				if (EntityHandler.getItemDef(j1).isStackable()
						&& tradeMyItemsI[l].amount > 1)
				{
					tradeMyItemsI[l].amount--;
					continue;
				}
				tradeMyItemCount--;
				mouseDownTime = 0;
				for (int l2 = l; l2 < tradeMyItemCount; l2++)
				{
					tradeMyItemsI[l2] = tradeMyItemsI[l2 + 1];
				}

				break;
			}
			formatPacket(70, -1, -1);
		}
	}

	private final void drawTradeWindow()
	{
		if (mouseButtonClick != 0 && itemIncrement == 0)
			itemIncrement = 1;
		if (itemIncrement > 0)
		{
			if (tradePan.isMouseOver(super.mouseX, super.mouseY))
			{ // inside trade window
				if (tradePan.getInvGrid().isMouseOver(super.mouseX, super.mouseY))
					handleMouseOverTradeInv();
				else if (tradePan.getOfferGrid().isMouseOver(super.mouseX, super.mouseY))
					handleMouseOverPlrTradeOffer();
				else
					handleTradePanelClicks();
			}
			mouseButtonClick = 0;
			itemIncrement = 0;
		}
		if (!showTradeWindow)
			return;

		// draw the interface
		tradePan.getFrame().drawComponent(super.mouseX, super.mouseY);
		drawTradePanel();
		updateTradeStatus();
		drawTradeInventoryGrid();
		drawTradePlrOfferGrid();
		drawTradeOpntOfferGrid();

		if (tradePan.isMouseOver(super.mouseX, super.mouseY))
			handleTradePanelClicks();
		else if (tradePan.getFrame().getCloseButton().isMouseOverButton(
				super.mouseX, super.mouseY))
		{ // close button
			menuLength++;
			if (mouseButtonClick == 1)
			{
				formatPacket(216, -1, -1);
				mouseButtonClick = 0;
			}
		}
		else if (tradePan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside settings panel but not on the content or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
	}

	private final boolean enginePlayerVisible(int i) {
		int x = EngineHandle.getTileXFromX(ourPlayer.currentX);
		int y = EngineHandle.getTileYFromY(ourPlayer.currentY);
		for (int l = 2; l >= 1; l--) {
			if (i == 1 && ((engineHandle.walkableValue[x][y - l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x - l][y] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x - l][y - l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS))
				return false;
			if (i == 3 && ((engineHandle.walkableValue[x][y + l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x - l][y] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x - l][y + l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS))
				return false;
			if (i == 5 && ((engineHandle.walkableValue[x][y + l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x + l][y] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x + l][y + l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS))
				return false;
			if (i == 7 && ((engineHandle.walkableValue[x][y - l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x + l][y] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS
					|| (engineHandle.walkableValue[x + l][y - l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS))
				return false;
			if (i == 0 && (engineHandle.walkableValue[x][y - l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS)
				return false;
			if (i == 2 && (engineHandle.walkableValue[x - l][y] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS)
				return false;
			if (i == 4 && (engineHandle.walkableValue[x][y + l] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS)
				return false;
			if (i == 6 && (engineHandle.walkableValue[x + l][y] & EngineHandle.WALKABLE_INDOORS) == EngineHandle.WALKABLE_INDOORS)
				return false;
		}

		return true;
	}

	private Mob getLastPlayer(int serverIndex) {
		for (int i1 = 0; i1 < lastPlayerCount; i1++) {
			if (lastPlayerArray[i1].serverIndex == serverIndex) {
				return lastPlayerArray[i1];
			}
		}
		return null;
	}

	private Mob getLastNpc(int serverIndex) {
		for (int i1 = 0; i1 < lastNpcCount; i1++) {
			if (lastNpcArray[i1].serverIndex == serverIndex) {
				return lastNpcArray[i1];
			}
		}
		return null;
	}

	protected final void handleIncomingPacket(
			int packetID, int dataLength, byte data[])
	{
		try
		{
			switch(packetID)
			{
			case 4:
				int currentMob = DataOperations.getUnsigned2Bytes(data, 1);
				if (mobArray[currentMob] != null) // todo: check what that mobArray is
					tradeOtherPlayerName = mobArray[currentMob].name;
				showTradeWindow = true;
				tradeOtherAccepted = false;
				tradeWeAccepted = false;
				tradeMyItemCount = 0;
				tradeOtherItemCount = 0;
				break;
			case 11:
				String[] combat = {
						"combat1a", "combat1b", /* blunt weapons/unarmed*/
						"combat2a", "combat2b", /* sharp weapons*/
						"combat3a", "combat3b", /* spear? goblin sound*/
				};
				String s = new String(data, 1, dataLength - 1);
				System.out.printf("Sounds from server: %s\n", s);
				playSound(s);
				break;
			case 18:
				tradeWeAccepted = data[1] == 1;
				break;
			case 23:
				if (anInt892 < 50)
				{
					int j7 = data[1] & 0xff;
					int k13 = data[2] + sectionX;
					int k18 = data[3] + sectionY;
					anIntArray782[anInt892] = j7;
					anIntArray923[anInt892] = 0;
					anIntArray944[anInt892] = k13;
					anIntArray757[anInt892] = k18;
					anInt892++;
				}
				break;
			case 27:
				for (int offset = 1; offset < dataLength;)
					if (DataOperations.getUnsignedByte(data[offset]) == 255)
					{
						int j8 = 0;
						int l14 = sectionX + data[offset + 1] >> 3;
						int k19 = sectionY + data[offset + 2] >> 3;
						offset += 3;
						for (int i24 = 0; i24 < objectCount; i24++)
						{
							int l26 = (objectX[i24] >> 3) - l14;
							int k29 = (objectY[i24] >> 3) - k19;
							if (l26 != 0 || k29 != 0)
							{
								if (i24 != j8)
								{
									objectModelArray[j8] = objectModelArray[i24];
									objectModelArray[j8].anInt257 = j8;
									objectX[j8] = objectX[i24];
									objectY[j8] = objectY[i24];
									objectType[j8] = objectType[i24];
									objectID[j8] = objectID[i24];
								}
								j8++;
							}
							else
							{
								gameCamera.removeModel(objectModelArray[i24]);
								engineHandle.updateObject(objectX[i24], objectY[i24], objectType[i24], objectID[i24]);
							}
						}

						objectCount = j8;
					}
					else
					{
						int model_id = DataOperations.getUnsigned2Bytes(data, offset);
						offset += 2;
						int obj_x = sectionX + data[offset++];
						int obj_y = sectionY + data[offset++];
						int obj_id = data[offset++];
						int k = 0;
						for (int j = 0; j < objectCount; j++)
							if (objectX[j] != obj_x
									|| objectY[j] != obj_y
									|| objectID[j] != obj_id)
							{
								if (j != k)
								{
									objectModelArray[k] = objectModelArray[j];
									objectModelArray[k].anInt257 = k;
									objectX[k] = objectX[j];
									objectY[k] = objectY[j];
									objectType[k] = objectType[j];
									objectID[k] = objectID[j];
								}
								k++;
							}
							else
							{
								gameCamera.removeModel(objectModelArray[j]);
								engineHandle.updateObject(objectX[j], objectY[j], objectType[j], objectID[j]);
							}

						objectCount = k;
						if (model_id != 60000)
						{
							engineHandle.registerObjectDir(obj_x, obj_y, obj_id);
							int i34;
							int j37;
							if (obj_id == 0 || obj_id == 4)
							{
								i34 = EntityHandler.getObjectDef(model_id).getWidth();
								j37 = EntityHandler.getObjectDef(model_id).getHeight();
							}
							else
							{
								j37 = EntityHandler.getObjectDef(model_id).getWidth();
								i34 = EntityHandler.getObjectDef(model_id).getHeight();
							}
							double x_transl = ((obj_x + obj_x + i34) * EngineHandle.GAME_SIZE) / 2;
							double y_transl = ((obj_y + obj_y + j37) * EngineHandle.GAME_SIZE) / 2;
							int modelID = EntityHandler.getObjectDef(model_id).modelID;
							System.out.printf("Loading requested model from server: %d, %d\n", model_id, modelID);
							Model model_1 = gameDataModels[modelID].newModel();
							// TODO: make the server send objects that are further away
							gameCamera.addModel(model_1);
							model_1.anInt257 = objectCount;
							model_1.addRotation(0, obj_id * 32, 0);
							model_1.addTranslate(x_transl, -engineHandle.getAveragedElevation(x_transl, y_transl), y_transl);
							model_1.setLightAndGradAndSource(true, 48, 48, Camera.light_x, Camera.light_z, Camera.light_y);
							engineHandle.method412(obj_x, obj_y, model_id, obj_id);
							if (model_id == 74)
								model_1.addTranslate(0, -480, 0);
							objectX[objectCount] = obj_x;
							objectY[objectCount] = obj_y;
							objectType[objectCount] = model_id;
							objectID[objectCount] = obj_id;
							objectModelArray[objectCount++] = model_1;
						}
					}
				break;
			case 53:
				int mobCount = DataOperations.getUnsigned2Bytes(data, 1);
				int mobUpdateOffset = 3;
				for (currentMob = 0; currentMob < mobCount; currentMob++)
				{
					int mobArrayIndex = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
					mobUpdateOffset += 2;
					if (mobArrayIndex < 0 || mobArrayIndex > mobArray.length)
						return;
					Mob mob = mobArray[mobArrayIndex];
					if (mob == null)
						return;
					byte mobUpdateType = data[mobUpdateOffset++];
					if (mobUpdateType == 0)
					{
						int i30 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
						mobUpdateOffset += 2;
						if (mob != null)
						{
							mob.anInt163 = 150;
							mob.anInt162 = i30;
						}
					}
					else if (mobUpdateType == 1)
					{ // Player talking
						byte byte7 = data[mobUpdateOffset++];
						if (mob != null)
						{
							String s2 = DataConversions.byteToString(data, mobUpdateOffset, byte7);
							mob.lastMessageTimeout = 150;
							mob.lastMessage = s2;
							displayMessage(mob.name + ": " + mob.lastMessage, 2, mob.admin);
						}
						mobUpdateOffset += byte7;
					}
					else if (mobUpdateType == 2)
					{ // Someone getting hit.
						int j30 = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
						int hits = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
						int hitsBase = DataOperations.getUnsignedByte(data[mobUpdateOffset++]);
						if (mob != null) {
							mob.anInt164 = j30;
							mob.hitPointsCurrent = hits;
							mob.hitPointsBase = hitsBase;
							mob.combatTimer = 200;
							if (mob == ourPlayer) {
								playerStatCurrent[3] = hits;
								playerStatBase[3] = hitsBase;
								showWelcomeBox = false;
								//                                showServerMessageBox = false;
							}
						}
					}
					else if (mobUpdateType == 3)
					{ // Projectile an npc..
						int k30 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
						mobUpdateOffset += 2;
						int k34 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
						mobUpdateOffset += 2;
						if (mob != null)
						{
							mob.attackingCameraInt = k30;
							mob.attackingNpcIndex = k34;
							mob.attackingMobIndex = -1;
							mob.anInt176 = attackingInt40;
						}
					}
					else if (mobUpdateType == 4)
					{ // Projectile another player.
						int l30 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
						mobUpdateOffset += 2;
						int l34 = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
						mobUpdateOffset += 2;
						if (mob != null) {
							mob.attackingCameraInt = l30;
							mob.attackingMobIndex = l34;
							mob.attackingNpcIndex = -1;
							mob.anInt176 = attackingInt40;
						}
					}
					else if (mobUpdateType == 5)
					{ // Apperance update
						if (mob != null)
						{
							mob.mobIntUnknown = DataOperations.getUnsigned2Bytes(data, mobUpdateOffset);
							mobUpdateOffset += 2;
							mob.nameLong = DataOperations.getUnsigned8Bytes(data, mobUpdateOffset);
							mobUpdateOffset += 8;
							mob.name = DataOperations.longToString(mob.nameLong);
							int i31 = DataOperations.getUnsignedByte(data[mobUpdateOffset]);
							mobUpdateOffset++;
							for (int i35 = 0; i35 < i31; i35++)
							{
								mob.animationCount[i35] = DataOperations.getUnsignedByte(data[mobUpdateOffset]);
								mobUpdateOffset++;
							}

							for (int l37 = i31; l37 < 12; l37++)
								mob.animationCount[l37] = 0;

							mob.colourHairType = data[mobUpdateOffset++] & 0xff;
							mob.colourTopType = data[mobUpdateOffset++] & 0xff;
							mob.colourBottomType = data[mobUpdateOffset++] & 0xff;
							mob.colourSkinType = data[mobUpdateOffset++] & 0xff;
							mob.level = data[mobUpdateOffset++] & 0xff;
							mob.anInt179 = data[mobUpdateOffset++] & 0xff;
							mob.admin = data[mobUpdateOffset++] & 0xff;
						}
						else
						{
							mobUpdateOffset += 14;
							int j31 = DataOperations.getUnsignedByte(data[mobUpdateOffset]);
							mobUpdateOffset += j31 + 1;
						}
					}
					else if (mobUpdateType == 6)
					{ // private player talking
						byte byte8 = data[mobUpdateOffset];
						mobUpdateOffset++;
						if (mob != null)
						{
							String s3 = DataConversions.byteToString(data, mobUpdateOffset, byte8);
							mob.lastMessageTimeout = 150;
							mob.lastMessage = s3;
							if (mob == ourPlayer)
								displayMessage(mob.name + ": " + mob.lastMessage, 5, mob.admin);
						}
						mobUpdateOffset += byte8;
					}
				}
				break;
			case 63:
				duelOpponentItemCount = data[1] & 0xff;
				int l5 = 2;
				for (int i12 = 0; i12 < duelOpponentItemCount; i12++)
				{
					duelOpponentItemsI[i12] = new Item(DataOperations.getUnsigned2Bytes(data, l5));
					l5 += 2;
					duelOpponentItemsI[i12].amount = DataOperations.readInt(data, l5);
					l5 += 4;
				}

				duelOpponentAccepted = false;
				duelMyAccepted = false;
				break;
			case 64:
				serverMessage = new String(data, 1, dataLength - 1);
				showServerMessageBox = true;
				serverMessageBoxTop = true;
				break;
			case 65:
				duelOpponentAccepted = data[1] == 1;
				break;
			case 77:
				lastNpcCount = npcCount;
				npcCount = 0;
				for (int lastNpcIndex = 0; lastNpcIndex < lastNpcCount; lastNpcIndex++)
					lastNpcArray[lastNpcIndex] = npcArray[lastNpcIndex];

				int newNpcOffset = 8;
				int newNpcCount = DataOperations.getIntFromByteArray(data, newNpcOffset, 8);
				newNpcOffset += 8;
				for (int newNpcIndex = 0; newNpcIndex < newNpcCount; newNpcIndex++)
				{
					Mob newNPC = getLastNpc(DataOperations.getIntFromByteArray(data, newNpcOffset, 16));
					newNpcOffset += 16;
					int npcNeedsUpdate = DataOperations.getIntFromByteArray(data, newNpcOffset, 1);
					newNpcOffset++;
					if (npcNeedsUpdate != 0)
					{
						int i32 = DataOperations.getIntFromByteArray(data, newNpcOffset, 1);
						newNpcOffset++;
						if (i32 == 0)
						{
							int nextSprite = DataOperations.getIntFromByteArray(data, newNpcOffset, 3);
							newNpcOffset += 3;
							int waypointCurrent = newNPC.waypointCurrent;
							double waypointX = newNPC.waypointsX[waypointCurrent];
							double waypointY = newNPC.waypointsY[waypointCurrent];
							if (nextSprite == 2 || nextSprite == 1 || nextSprite == 3)
								waypointX += EngineHandle.GAME_SIZE;
							if (nextSprite == 6 || nextSprite == 5 || nextSprite == 7)
								waypointX -= EngineHandle.GAME_SIZE;
							if (nextSprite == 4 || nextSprite == 3 || nextSprite == 5)
								waypointY += EngineHandle.GAME_SIZE;
							if (nextSprite == 0 || nextSprite == 1 || nextSprite == 7)
								waypointY -= EngineHandle.GAME_SIZE;
							newNPC.nextSprite = nextSprite;
							newNPC.waypointCurrent = waypointCurrent = (waypointCurrent + 1) % 10;
							newNPC.waypointsX[waypointCurrent] = waypointX;
							newNPC.waypointsY[waypointCurrent] = waypointY;
						}
						else
						{
							int nextSpriteOffset = DataOperations.getIntFromByteArray(data, newNpcOffset, 4);
							newNpcOffset += 4;
							if ((nextSpriteOffset & 0xc) == 12)
								continue;
							newNPC.nextSprite = nextSpriteOffset;
						}
					}
					npcArray[npcCount++] = newNPC;
				}

				while (newNpcOffset + 34 < dataLength * 8)
				{
					int serverIndex = DataOperations.getIntFromByteArray(data, newNpcOffset, 16);
					newNpcOffset += 16;
					int i28 = DataOperations.getIntFromByteArray(data, newNpcOffset, 5);
					newNpcOffset += 5;
					if (i28 > 15)
						i28 -= 32;
					int j32 = DataOperations.getIntFromByteArray(data, newNpcOffset, 5);
					newNpcOffset += 5;
					if (j32 > 15)
						j32 -= 32;
					int nextSprite = DataOperations.getIntFromByteArray(data, newNpcOffset, 4);
					newNpcOffset += 4;
					double x = (sectionX + i28) * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
					double y = (sectionY + j32) * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
					int type = DataOperations.getIntFromByteArray(data, newNpcOffset, 10);
					newNpcOffset += 10;
					if (type >= EntityHandler.npcCount())
						type = 24;
					addNPC(serverIndex, x, y, nextSprite, type);
				}
				break;
			case 92:
				tradeOtherAccepted = data[1] == 1;
				break;
			case 93:
				showBank = true;
				int l4 = 1;
				newBankItemCount = data[l4++] & 0xff;
				bankItemsMax = data[l4++] & 0xff;
				for (int k11 = 0; k11 < newBankItemCount; k11++)
				{
					newBankItemsI[k11] = new Item(DataOperations.getUnsigned2Bytes(data, l4));
					l4 += 2;
					newBankItemsI[k11].amount = DataOperations.getUnsigned4Bytes(data, l4);
					l4 += 4;
				}
				updateBankItems();
				break;
			case 95:
				for (int l1 = 1; l1 < dataLength;)
					if (DataOperations.getUnsignedByte(data[l1]) == 255)
					{
						int j9 = 0;
						int l15 = sectionX + data[l1 + 1] >> 3;
						int j20 = sectionY + data[l1 + 2] >> 3;
						l1 += 3;
						for (int currentDoor = 0; currentDoor < doorCount; currentDoor++)
						{
							int j27 = (doorX[currentDoor] >> 3) - l15;
							int k31 = (doorY[currentDoor] >> 3) - j20;
							if (j27 != 0 || k31 != 0)
							{
								if (currentDoor != j9)
								{
									doorModel[j9] = doorModel[currentDoor];
									doorModel[j9].anInt257 = j9 + 10000;
									doorX[j9] = doorX[currentDoor];
									doorY[j9] = doorY[currentDoor];
									doorDirection[j9] = doorDirection[currentDoor];
									doorType[j9] = doorType[currentDoor];
								}
								j9++;
							}
							else
							{
								gameCamera.removeModel(doorModel[currentDoor]);
								engineHandle.updateDoor(doorX[currentDoor], doorY[currentDoor], doorDirection[currentDoor], doorType[currentDoor]);
							}
						}

						doorCount = j9;
					}
					else
					{
						int k9 = DataOperations.getUnsigned2Bytes(data, l1);
						l1 += 2;
						int i16 = sectionX + data[l1++];
						int k20 = sectionY + data[l1++];
						byte byte5 = data[l1++];
						int k27 = 0;
						for (int l31 = 0; l31 < doorCount; l31++)
							if (doorX[l31] != i16
									|| doorY[l31] != k20
									|| doorDirection[l31] != byte5)
							{
								if (l31 != k27)
								{
									doorModel[k27] = doorModel[l31];
									doorModel[k27].anInt257 = k27 + 10000;
									doorX[k27] = doorX[l31];
									doorY[k27] = doorY[l31];
									doorDirection[k27] = doorDirection[l31];
									doorType[k27] = doorType[l31];
								}
								k27++;
							}
							else
							{
								gameCamera.removeModel(doorModel[l31]);
								engineHandle.updateDoor(doorX[l31], doorY[l31], doorDirection[l31], doorType[l31]);
							}

						doorCount = k27;
						if (k9 != 60000)
						{ // 65535) {
							engineHandle.method408(i16, k20, byte5, k9);
							Model model = makeModel(i16, k20, byte5, k9, doorCount);
							doorModel[doorCount] = model;
							doorX[doorCount] = i16;
							doorY[doorCount] = k20;
							doorType[doorCount] = k9;
							doorDirection[doorCount++] = byte5;
						}
					}
				break;
			case 109:
				for (int l = 1; l < dataLength;)
					if (DataOperations.getUnsignedByte(data[l]) == 255)
					{ // ???
						int newCount = 0;
						int newSectionX = sectionX + data[l + 1] >> 3;
						int newSectionY = sectionY + data[l + 2] >> 3;
						l += 3;
						for (int groundItem = 0; groundItem < groundItemCount; groundItem++)
						{
							int newX = (groundItemX[groundItem] >> 3) - newSectionX;
							int newY = (groundItemY[groundItem] >> 3) - newSectionY;
							if (newX != 0 || newY != 0)
							{
								if (groundItem != newCount)
								{
									groundItemX[newCount] = groundItemX[groundItem];
									groundItemY[newCount] = groundItemY[groundItem];
									groundItemType[newCount] = groundItemType[groundItem];
									groundItemZ[newCount] = groundItemZ[groundItem];
								}
								newCount++;
							}
						}

						groundItemCount = newCount;
					}
					else
					{
						int i8 = DataOperations.getUnsigned2Bytes(data, l);
						l += 2;
						int k14 = sectionX + data[l++];
						int j19 = sectionY + data[l++];
						if ((i8 & 0x8000) == 0)
						{ // New Item
							groundItemX[groundItemCount] = k14;
							groundItemY[groundItemCount] = j19;
							groundItemType[groundItemCount] = i8;
							groundItemZ[groundItemCount] = 0;
							for (int k23 = 0; k23 < objectCount; k23++)
							{
								if (objectX[k23] != k14 || objectY[k23] != j19)
									continue;
								groundItemZ[groundItemCount] = EntityHandler.getObjectDef(objectType[k23]).getGroundItemZ();
								break;
							}

							groundItemCount++;
						}
						else
						{ // Known Item
							i8 &= 0x7fff;
							int l23 = 0;
							for (int k26 = 0; k26 < groundItemCount; k26++)
							{
								if (groundItemX[k26] != k14
										|| groundItemY[k26] != j19
										|| groundItemType[k26] != i8)
								{ // Keep how it is
									if (k26 != l23)
									{
										groundItemX[l23] = groundItemX[k26];
										groundItemY[l23] = groundItemY[k26];
										groundItemType[l23] = groundItemType[k26];
										groundItemZ[l23] = groundItemZ[k26];
									}
									l23++;
								}
								else
								{ // Remove
									i8 = -123;
								}
							}

							groundItemCount = l23;
						}
					}
				break;
			case 110:
				int i = 1;
				serverStartTime = DataOperations.getUnsigned8Bytes(data, i);
				i += 8;
				serverLocation = new String(data, i, dataLength - i);
				break;
			case 114:
				int invOffset = 1;
				inventoryCount = data[invOffset++] & 0xff;
				for (int invItem = 0; invItem < inventoryCount; invItem++)
				{
					int j15 = DataOperations.getUnsigned2Bytes(data, invOffset);
					invOffset += 2;
					inventory[invItem] = new Item(j15 & 0x7fff);
					wearing[invItem] = j15 / 32768;
					if (EntityHandler.getItemDef(j15 & 0x7fff).isStackable())
					{
						inventory[invItem].amount = DataOperations.readInt(data, invOffset);
						invOffset += 4;
					}
					else
						inventory[invItem].amount = 1;
				}
				break;
			case 115:
				int thingLength = (dataLength - 1) / 4;
				for (int currentThing = 0; currentThing < thingLength; currentThing++)
				{
					int currentItemSectionX = sectionX + DataOperations.getSigned2Bytes(data, 1 + currentThing * 4) >> 3;
					int currentItemSectionY = sectionY + DataOperations.getSigned2Bytes(data, 3 + currentThing * 4) >> 3;
					int currentCount = 0;
					for (int currentItem = 0; currentItem < groundItemCount; currentItem++)
					{
						int currentItemOffsetX = (groundItemX[currentItem] >> 3) - currentItemSectionX;
						int currentItemOffsetY = (groundItemY[currentItem] >> 3) - currentItemSectionY;
						if (currentItemOffsetX != 0 || currentItemOffsetY != 0)
						{
							if (currentItem != currentCount)
							{
								groundItemX[currentCount] = groundItemX[currentItem];
								groundItemY[currentCount] = groundItemY[currentItem];
								groundItemType[currentCount] = groundItemType[currentItem];
								groundItemZ[currentCount] = groundItemZ[currentItem];
							}
							currentCount++;
						}
					}

					groundItemCount = currentCount;
					currentCount = 0;
					for (int j33 = 0; j33 < objectCount; j33++)
					{
						int k36 = (objectX[j33] >> 3) - currentItemSectionX;
						int l38 = (objectY[j33] >> 3) - currentItemSectionY;
						if (k36 != 0 || l38 != 0)
						{
							if (j33 != currentCount)
							{
								objectModelArray[currentCount] = objectModelArray[j33];
								objectModelArray[currentCount].anInt257 = currentCount;
								objectX[currentCount] = objectX[j33];
								objectY[currentCount] = objectY[j33];
								objectType[currentCount] = objectType[j33];
								objectID[currentCount] = objectID[j33];
							}
							currentCount++;
						}
						else
						{
							gameCamera.removeModel(objectModelArray[j33]);
							engineHandle.updateObject(objectX[j33], objectY[j33], objectType[j33], objectID[j33]);
						}
					}

					objectCount = currentCount;
					currentCount = 0;
					for (int l36 = 0; l36 < doorCount; l36++)
					{
						int i39 = (doorX[l36] >> 3) - currentItemSectionX;
						int j41 = (doorY[l36] >> 3) - currentItemSectionY;
						if (i39 != 0 || j41 != 0)
						{
							if (l36 != currentCount)
							{
								doorModel[currentCount] = doorModel[l36];
								doorModel[currentCount].anInt257 = currentCount + 10000;
								doorX[currentCount] = doorX[l36];
								doorY[currentCount] = doorY[l36];
								doorDirection[currentCount] = doorDirection[l36];
								doorType[currentCount] = doorType[l36];
							}
							currentCount++;
						}
						else
						{
							gameCamera.removeModel(doorModel[l36]);
							engineHandle.updateDoor(doorX[l36], doorY[l36], doorDirection[l36], doorType[l36]);
						}
					}

					doorCount = currentCount;
				}
				break;
			case 126:
				fatigue = DataOperations.getUnsigned2Bytes(data, 1);
				break;
			case 127:
				showQuestionMenu = false;
				break;
			case 129:
				combatStyle = DataOperations.getUnsignedByte(data[1]);
				break;
			case 131:
				notInWilderness = true;
				hasWorldInfo = true;
				serverIndex = DataOperations.getUnsigned2Bytes(data, 1);
				wildX = DataOperations.getUnsigned2Bytes(data, 3);
				wildY = DataOperations.getUnsigned2Bytes(data, 5);
				wildYSubtract = DataOperations.getUnsigned2Bytes(data, 7);
				wildYMultiplier = DataOperations.getUnsigned2Bytes(data, 9);
				wildY -= wildYSubtract * wildYMultiplier;
				break;
			case 139:
				int bankDataOffset = 1;
				int bankSlot = data[bankDataOffset++] & 0xff;
				int bankItemId = DataOperations.getUnsigned2Bytes(data, bankDataOffset);
				bankDataOffset += 2;
				int bankItemCount = DataOperations.getUnsigned4Bytes(data, bankDataOffset);
				bankDataOffset += 4;
				if (bankItemCount == 0)
				{
					newBankItemCount--;
					for (int currentBankSlot = bankSlot; currentBankSlot < newBankItemCount; currentBankSlot++)
						newBankItemsI[currentBankSlot] = newBankItemsI[currentBankSlot + 1];
				}
				else
				{
					newBankItemsI[bankSlot] = new Item(bankItemId, bankItemCount);
					if (bankSlot >= newBankItemCount)
						newBankItemCount = bankSlot + 1;
				}
				updateBankItems();
				break;
			case 145:
				if (!hasWorldInfo)
					return;
				lastPlayerCount = playerCount;
				for (int k = 0; k < lastPlayerCount; k++)
					lastPlayerArray[k] = playerArray[k];

				int currentOffset = 8;
				sectionX = DataOperations.getIntFromByteArray(data, currentOffset, 11);
				currentOffset += 11;
				sectionY = DataOperations.getIntFromByteArray(data, currentOffset, 13);
				currentOffset += 13;
				int mobSprite = DataOperations.getIntFromByteArray(data, currentOffset, 4);
				currentOffset += 4;
				boolean sectionLoaded = loadSection(sectionX, sectionY);
				sectionX -= areaX;
				sectionY -= areaY;
				double mapEnterX = sectionX * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
				double mapEnterY = sectionY * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
				if (sectionLoaded)
				{
					ourPlayer.waypointCurrent = 0;
					ourPlayer.waypointEndSprite = 0;
					ourPlayer.currentX = ourPlayer.waypointsX[0] = mapEnterX;
					ourPlayer.currentY = ourPlayer.waypointsY[0] = mapEnterY;
				}
				playerCount = 0;
				ourPlayer = makePlayer(serverIndex, mapEnterX, mapEnterY, mobSprite);
				int newPlayerCount = DataOperations.getIntFromByteArray(data, currentOffset, 8);
				currentOffset += 8;
				for (int currentNewPlayer = 0; currentNewPlayer < newPlayerCount; currentNewPlayer++)
				{
					Mob lastMob = getLastPlayer(DataOperations.getIntFromByteArray(data, currentOffset, 16));
					currentOffset += 16;
					int nextPlayer = DataOperations.getIntFromByteArray(data, currentOffset, 1); // 1
					currentOffset++;
					if (nextPlayer != 0)
					{
						int waypointsLeft = DataOperations.getIntFromByteArray(data, currentOffset, 1); // 2
						currentOffset++;
						if (waypointsLeft == 0)
						{
							int currentNextSprite = DataOperations.getIntFromByteArray(data, currentOffset, 3); // 3
							currentOffset += 3;
							int currentWaypoint = lastMob.waypointCurrent;
							double newWaypointX = lastMob.waypointsX[currentWaypoint];
							double newWaypointY = lastMob.waypointsY[currentWaypoint];
							if (currentNextSprite == 2
									|| currentNextSprite == 1
									|| currentNextSprite == 3)
								newWaypointX += EngineHandle.GAME_SIZE;
							if (currentNextSprite == 6
									|| currentNextSprite == 5
									|| currentNextSprite == 7)
								newWaypointX -= EngineHandle.GAME_SIZE;
							if (currentNextSprite == 4
									|| currentNextSprite == 3
									|| currentNextSprite == 5)
								newWaypointY += EngineHandle.GAME_SIZE;
							if (currentNextSprite == 0
									|| currentNextSprite == 1
									|| currentNextSprite == 7)
								newWaypointY -= EngineHandle.GAME_SIZE;
							lastMob.nextSprite = currentNextSprite;
							lastMob.waypointCurrent = currentWaypoint = (currentWaypoint + 1) % 10;
							lastMob.waypointsX[currentWaypoint] = newWaypointX;
							lastMob.waypointsY[currentWaypoint] = newWaypointY;
						}
						else
						{
							int needsNextSprite = DataOperations.getIntFromByteArray(data, currentOffset, 4);
							currentOffset += 4;
							if ((needsNextSprite & 0xc) == 12)
								continue;
							lastMob.nextSprite = needsNextSprite;
						}
					}
					playerArray[playerCount++] = lastMob;
				}

				mobCount = 0;
				while (currentOffset + 24 < dataLength * 8)
				{
					int mobIndex = DataOperations.getIntFromByteArray(data, currentOffset, 16);
					currentOffset += 16;
					int areaMobX = DataOperations.getIntFromByteArray(data, currentOffset, 5);
					currentOffset += 5;
					if (areaMobX > 15)
						areaMobX -= 32;
					int areaMobY = DataOperations.getIntFromByteArray(data, currentOffset, 5);
					currentOffset += 5;
					if (areaMobY > 15)
						areaMobY -= 32;
					int mobArrayMobID = DataOperations.getIntFromByteArray(data, currentOffset, 4);
					currentOffset += 4;
					int addIndex = DataOperations.getIntFromByteArray(data, currentOffset, 1);
					currentOffset++;
					double mobX = (sectionX + areaMobX) * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
					double mobY = (sectionY + areaMobY) * EngineHandle.GAME_SIZE + EngineHandle.GAME_SIZE/2;
					makePlayer(mobIndex, mobX, mobY, mobArrayMobID);
					if (addIndex == 0)
						mobArrayIndexes[mobCount++] = mobIndex;
				}
				if (mobCount > 0)
				{
					super.streamClass.createPacket(83);
					super.streamClass.add2ByteInt(mobCount);
					for (currentMob = 0; currentMob < mobCount; currentMob++)
					{
						Mob dummyMob = mobArray[mobArrayIndexes[currentMob]];
						super.streamClass.add2ByteInt(dummyMob.serverIndex);
						super.streamClass.add2ByteInt(dummyMob.mobIntUnknown);
					}

					super.streamClass.formatPacket();
					mobCount = 0;
				}
				break;
			case 147:
				showDuelConfirmWindow = true;
				duelWeAccept = false;
				showDuelWindow = false;
				int i7 = 1;
				duelOpponentNameLong = DataOperations.getUnsigned8Bytes(data, i7);
				i7 += 8;
				duelConfirmOpponentItemCount = data[i7++] & 0xff;
				for (int j13 = 0; j13 < duelConfirmOpponentItemCount; j13++)
				{
					duelConfirmOpponentItemsI[j13] = new Item(DataOperations.getUnsigned2Bytes(data, i7));
					i7 += 2;
					duelConfirmOpponentItemsI[j13].amount = DataOperations.readInt(data, i7);
					i7 += 4;
				}

				duelConfirmMyItemCount = data[i7++] & 0xff;
				for (int j18 = 0; j18 < duelConfirmMyItemCount; j18++)
				{
					duelConfirmMyItemsI[j18] = new Item(DataOperations.getUnsigned2Bytes(data, i7));
					i7 += 2;
					duelConfirmMyItemsI[j18].amount = DataOperations.readInt(data, i7);
					i7 += 4;
				}

				duelCantRetreat = data[i7++] & 0xff;
				duelUseMagic = data[i7++] & 0xff;
				duelUsePrayer = data[i7++] & 0xff;
				duelUseWeapons = data[i7++] & 0xff;
				break;
			case 148:
				serverMessage = new String(data, 1, dataLength - 1);
				showServerMessageBox = true;
				serverMessageBoxTop = false;
				break;
			case 152:
				configAutoCameraAngle = DataOperations.getUnsignedByte(data[1]) == 1;
				configMouseButtons = DataOperations.getUnsignedByte(data[2]) == 1;
				configSoundEffects = DataOperations.getUnsignedByte(data[3]) == 1;
				showRoof = DataOperations.getUnsignedByte(data[4]) == 1;
				autoScreenshot = DataOperations.getUnsignedByte(data[5]) == 1;
				combatWindow = DataOperations.getUnsignedByte(data[6]) == 1;
				//TODO: send and receive texture on/off info.
				break;
			case 161:
				showDuelWindow = false;
				showDuelConfirmWindow = false;
				break;
			case 165:
				playerAliveTimeout = 250;
				playSound("death");
				break;
			case 171:
				showBank = false;
				break;
			case 172:
				systemUpdate = DataOperations.getUnsigned2Bytes(data, 1) * 32;
				break;
			case 177:
				int i3 = 1;
				for (int x = 0; x < 6; i3 += 2)
					equipmentStatus[x++] = DataOperations.getSigned2Bytes(data, i3);
				break;
			case 180:
				int l2 = 1;
				for (int k10 = 0; k10 < 18;
						playerStatCurrent[k10++] = DataOperations.getUnsignedByte(data[l2++]));
				for (int i17 = 0; i17 < 18;
						playerStatBase[i17++] = DataOperations.getUnsignedByte(data[l2++]));
				for (int k21 = 0; k21 < 18; l2 += 4)
					playerStatExperience[k21++] = DataOperations.readInt(data, l2);
				expGained = 0;
				break;
			case 181:
				if (autoScreenshot)
					takeScreenshot(false);
				break;
			case 187:
				showTradeWindow = false;
				showTradeConfirmWindow = false;
				break;
			case 190:
				int j2 = DataOperations.getUnsigned2Bytes(data, 1);
				int i10 = 3;
				for (int k16 = 0; k16 < j2; k16++)
				{
					int i21 = DataOperations.getUnsigned2Bytes(data, i10);
					i10 += 2;
					Mob mob_2 = npcRecordArray[i21];
					int j28 = DataOperations.getUnsignedByte(data[i10]);
					i10++;
					if (j28 == 1)
					{
						int k32 = DataOperations.getUnsigned2Bytes(data, i10);
						i10 += 2;
						byte byte9 = data[i10];
						i10++;
						if (mob_2 != null)
						{
							String s4 = DataConversions.byteToString(data, i10, byte9);
							mob_2.lastMessageTimeout = 150;
							mob_2.lastMessage = s4;
							if (k32 == ourPlayer.serverIndex)
								displayMessage("@yel@" + EntityHandler.getNpcDef(mob_2.type).getName() + ": " + mob_2.lastMessage, 5, 0);
						}
						i10 += byte9;
					}
					else if (j28 == 2)
					{
						int l32 = DataOperations.getUnsignedByte(data[i10]);
						i10++;
						int i36 = DataOperations.getUnsignedByte(data[i10]);
						i10++;
						int k38 = DataOperations.getUnsignedByte(data[i10]);
						i10++;
						if (mob_2 != null)
						{
							mob_2.anInt164 = l32;
							mob_2.hitPointsCurrent = i36;
							mob_2.hitPointsBase = k38;
							mob_2.combatTimer = 200;
						}
					}
				}
				break;
			case 191:
				int k6 = data[1] & 0xff;
				inventoryCount--;
				for (int l12 = k6; l12 < inventoryCount; l12++)
				{
					inventory[l12] = inventory[l12 + 1];
					wearing[l12] = wearing[l12 + 1];
				}
				break;
			case 197:
				duelMyAccepted = data[1] == 1;
				break;
			case 198:
				duelNoRetreating = data[1] == 1;
				duelNoMagic = data[2] == 1;
				duelNoPrayer = data[3] == 1;
				duelNoWeapons = data[4] == 1;
				duelOpponentAccepted = false;
				duelMyAccepted = false;
				break;
			case 207:
				showCharacterLookScreen = true;
				break;
			case 208:
				int pointer = 1;
				int idx = data[pointer++] & 0xff;
				int oldExp = playerStatExperience[idx];
				playerStatCurrent[idx] = DataOperations.getUnsignedByte(data[pointer++]);
				playerStatBase[idx] = DataOperations.getUnsignedByte(data[pointer++]);
				playerStatExperience[idx] = DataOperations.readInt(data, pointer);
				pointer += 4;

				if (playerStatExperience[idx] > oldExp)
					expGained += (playerStatExperience[idx] - oldExp);
				break;
			case 209:
				for (int currentPrayer = 0; currentPrayer < dataLength - 1; currentPrayer++)
				{
					boolean prayerOff = data[currentPrayer + 1] == 1;
					if (!prayerOn[currentPrayer] && prayerOff)
						playSound("prayeron");
					if (prayerOn[currentPrayer] && !prayerOff)
						playSound("prayeroff");
					prayerOn[currentPrayer] = prayerOff;
				}
				break;
			case 211:
				idx = data[1] & 0xFF;
				oldExp = playerStatExperience[idx];
				playerStatExperience[idx] = DataOperations.readInt(data, 2);
				if (playerStatExperience[idx] > oldExp)
					expGained += (playerStatExperience[idx] - oldExp);
				break;
			case 220:
				showShop = false;
				break;
			case 223:
				showQuestionMenu = true;
				int newQuestionMenuCount = DataOperations.getUnsignedByte(data[1]);
				questionMenuCount = newQuestionMenuCount;
				int newQuestionMenuOffset = 2;
				for (int l16 = 0; l16 < newQuestionMenuCount; l16++)
				{
					int newQuestionMenuQuestionLength = DataOperations.getUnsignedByte(data[newQuestionMenuOffset]);
					newQuestionMenuOffset++;
					questionMenuAnswer[l16] = new String(data, newQuestionMenuOffset, newQuestionMenuQuestionLength);
					newQuestionMenuOffset += newQuestionMenuQuestionLength;
				}
				break;
			case 228:
				int j6 = 1;
				int k12 = 1;
				int i18 = data[j6++] & 0xff;
				int k22 = DataOperations.getUnsigned2Bytes(data, j6);
				j6 += 2;
				if (EntityHandler.getItemDef(k22 & 0x7fff).isStackable())
				{
					k12 = DataOperations.readInt(data, j6);
					j6 += 4;
				}
				inventory[i18] = new Item(k22 & 0x7fff);
				wearing[i18] = k22 / 32768;
				inventory[i18].amount = k12;
				if (i18 >= inventoryCount)
					inventoryCount = i18 + 1;
				break;
			case 229:
				int j5 = DataOperations.getUnsigned2Bytes(data, 1);
				if (mobArray[j5] != null)
					duelOpponentName = mobArray[j5].name;
				showDuelWindow = true;
				duelMyItemCount = 0;
				duelOpponentItemCount = 0;
				duelOpponentAccepted = false;
				duelMyAccepted = false;
				duelNoRetreating = false;
				duelNoMagic = false;
				duelNoPrayer = false;
				duelNoWeapons = false;
				break;
			case 248:
				if (!hasReceivedWelcomeBoxDetails)
				{
					lastLoggedInDays = DataOperations.getUnsigned2Bytes(data, 1);
					subscriptionLeftDays = DataOperations.getUnsigned2Bytes(data, 3);
					lastLoggedInAddress = new String(data, 5, dataLength - 5);
					showWelcomeBox = true;
					hasReceivedWelcomeBoxDetails = true;
				}
				break;
			case 250:
				tradeOtherItemCount = data[1] & 0xff;
				int l3 = 2;
				for (int i11 = 0; i11 < tradeOtherItemCount; i11++)
				{
					tradeOtherItemsI[i11] = new Item(DataOperations.getUnsigned2Bytes(data, l3));
					l3 += 2;
					tradeOtherItemsI[i11].amount = DataOperations.readInt(data, l3);
					l3 += 4;
				}
				tradeOtherAccepted = false;
				tradeWeAccepted = false;
				break;
			case 251:
				showTradeConfirmWindow = true;
				tradeConfirmAccepted = false;
				showTradeWindow = false;
				int k5 = 1;
				tradeConfirmOtherNameLong = DataOperations.getUnsigned8Bytes(data, k5);
				k5 += 8;
				tradeConfirmOtherItemCount = data[k5++] & 0xff;
				for (int l11 = 0; l11 < tradeConfirmOtherItemCount; l11++)
				{
					tradeConfirmOtherItemsI[l11] = new Item(DataOperations.getUnsigned2Bytes(data, k5));
					k5 += 2;
					tradeConfirmOtherItemsI[l11].amount = DataOperations.readInt(data, k5);
					k5 += 4;
				}

				tradeConfirmItemCount = data[k5++] & 0xff;
				for (int k17 = 0; k17 < tradeConfirmItemCount; k17++)
				{
					tradeConfirmItemsI[k17] = new Item(DataOperations.getUnsigned2Bytes(data, k5));
					k5 += 2;
					tradeConfirmItemsI[k17].amount = DataOperations.readInt(data, k5);
					k5 += 4;
				}
				break;
			case 253:
				showShop = true;
				int i4 = 1;
				int j11 = data[i4++] & 0xff;
				byte byte4 = data[i4++];
				shopItemSellPriceModifier = data[i4++] & 0xff;
				shopItemBuyPriceModifier = data[i4++] & 0xff;
				for (int i22 = 0; i22 < 40; i22++)
					shopItemsI[i22] = new Item(-1);

				for (int j25 = 0; j25 < j11; j25++)
				{
					shopItemsI[j25] = new Item(DataOperations.getUnsigned2Bytes(data, i4));
					i4 += 2;
					shopItemsI[j25].amount = DataOperations.getUnsigned2Bytes(data, i4);
					i4 += 2;
				}

				if (byte4 == 1)
				{
					int l28 = 39;
					for (int k33 = 0; k33 < inventoryCount; k33++)
					{
						if (l28 < j11)
							break;
						boolean flag2 = false;
						for (int j39 = 0; j39 < 40; j39++)
						{
							if (shopItemsI[j39].id != inventory[k33].id)
								continue;
							flag2 = true;
							break;
						}
						if (inventory[k33].id == 10)
							flag2 = true;
						if (!flag2) {
							shopItemsI[l28] = new Item(inventory[k33].id & 0x7fff, 0);
							l28--;
						}
					}

				}
				if (selectedShopItemIndex >= 0
						&& selectedShopItemIndex < 40
						&& shopItemsI[selectedShopItemIndex].id != selectedShopItemType)
				{
					selectedShopItemIndex = -1;
					selectedShopItemType = -2;
				}
				break;
			default:
				System.out.printf("Unhandled packet: %d; length: %d", packetID, dataLength);
				break;
			}
		}
		catch (RuntimeException runtimeexception)
		{
			runtimeexception.printStackTrace();
			if (handlePacketErrorCount < 3)
			{
				super.streamClass.createPacket(156);
				super.streamClass.addString(runtimeexception.toString());
				super.streamClass.formatPacket();
				handlePacketErrorCount++;
			}
		}
	}

	protected final void lostConnection() {
		systemUpdate = 0;
		if (logoutTimeout != 0) {
			resetIntVars();
			return;
		}
		super.lostConnection();
	}

	private final void playSound(String s) {
		if (audioReader == null) {
			return;
		}
		if (configSoundEffects) {
			return;
		}
		audioReader.loadData(sounds, DataOperations.getEntryOffset(s + ".pcm", sounds), DataOperations.getEntryLength(s + ".pcm", sounds));
	}


	private final boolean sendWalkCommandKeys(int walkSectionX, int walkSectionY,
			int x1, int y1, int x2, int y2,
			boolean stepBoolean, boolean coordsEqual)
	{
		// TODO: needs checking
		int stepCount = engineHandle.getStepCount(walkSectionX, walkSectionY,
				x1, y1, x2, y2, sectionXArray, sectionYArray, stepBoolean);
		if (stepCount == -1)
		{
			if (coordsEqual)
			{
				stepCount = 1;
				sectionXArray[0] = x1;
				sectionYArray[0] = y1;
			} else {
				return false;
			}
		}
		stepCount--;
		walkSectionX = sectionXArray[stepCount];
		walkSectionY = sectionYArray[stepCount];
		stepCount--;
		if (coordsEqual)
		{
			super.streamClass.createPacket(246);
		}
		else
		{
			super.streamClass.createPacket(132);
		}
		super.streamClass.add2ByteInt(walkSectionX + areaX);
		super.streamClass.add2ByteInt(walkSectionY + areaY);
		if (coordsEqual && stepCount == -1 && (walkSectionX + areaX) % 5 == 0)
			stepCount = 0;
		for (int currentStep = stepCount; currentStep >= 0 && currentStep > stepCount - 25; currentStep--) {
			super.streamClass.addByte(sectionXArray[currentStep] - walkSectionX);
			super.streamClass.addByte(sectionYArray[currentStep] - walkSectionY);
		}

		super.streamClass.formatPacket();
		return true;
	}

	/**
	 * Sends the steps that the player should take to move to a new position to the server.
	 * @param walkSectionX
	 * @param walkSectionY
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param stepBoolean
	 * @param coordsEqual
	 * @return
	 */
	private final boolean sendWalkCommand(int walkSectionX, int walkSectionY,
			int x1, int y1, int x2, int y2,
			boolean stepBoolean, boolean coordsEqual)
	{
		// todo: needs checking
		int stepCount = engineHandle.getStepCount(walkSectionX, walkSectionY,
				x1, y1, x2, y2, sectionXArray, sectionYArray, stepBoolean);
		if (stepCount == -1)
		{
			if (coordsEqual)
			{
				stepCount = 1;
				sectionXArray[0] = (int)x1;
				sectionYArray[0] = (int)y1;
			} else {
				return false;
			}
		}
		stepCount--;
		walkSectionX = sectionXArray[stepCount];
		walkSectionY = sectionYArray[stepCount];
		stepCount--;
		if (coordsEqual)
		{
			super.streamClass.createPacket(246);
		}
		else
		{
			super.streamClass.createPacket(132);
		}
		super.streamClass.add2ByteInt(walkSectionX + areaX);
		super.streamClass.add2ByteInt(walkSectionY + areaY);
		if (coordsEqual && stepCount == -1 && (walkSectionX + areaX) % 5 == 0)
			stepCount = 0;
		for (int currentStep = stepCount; currentStep >= 0 && currentStep > stepCount - 25; currentStep--) {
			super.streamClass.addByte(sectionXArray[currentStep] - walkSectionX);
			super.streamClass.addByte(sectionYArray[currentStep] - walkSectionY);
		}

		super.streamClass.formatPacket();
		actionPictureType = -24;
		actionPictureX = super.mouseX; // guessing the little red/yellow x that appears when you click
		actionPictureY = super.mouseY;
		return true;
	}

	private final boolean sendWalkCommandIgnoreCoordsEqual(
			int walkSectionX, int walkSectionY,
			double x1, double y1, double x2, double y2,
			boolean stepBoolean, boolean coordsEqual)
	{
		int stepCount = engineHandle.getStepCount(walkSectionX, walkSectionY, x1, y1, x2, y2, sectionXArray, sectionYArray, stepBoolean);
		if (stepCount == -1)
			return false;
		stepCount--;
		walkSectionX = sectionXArray[stepCount];
		walkSectionY = sectionYArray[stepCount];
		stepCount--;
		if (coordsEqual)
			super.streamClass.createPacket(246);
		else
			super.streamClass.createPacket(132);
		super.streamClass.add2ByteInt(walkSectionX + areaX);
		super.streamClass.add2ByteInt(walkSectionY + areaY);
		if (coordsEqual && stepCount == -1 && (walkSectionX + areaX) % 5 == 0)
			stepCount = 0;
		for (int currentStep = stepCount; currentStep >= 0 && currentStep > stepCount - 25; currentStep--) {
			super.streamClass.addByte(sectionXArray[currentStep] - walkSectionX);
			super.streamClass.addByte(sectionYArray[currentStep] - walkSectionY);
		}

		super.streamClass.formatPacket();
		actionPictureType = -24;
		actionPictureX = super.mouseX;
		actionPictureY = super.mouseY;
		return true;
	}

	public final Image createImage(int i, int j) {
		if (GameWindow.gameFrame != null) {
			return GameWindow.gameFrame.createImage(i, j);
		}
		return super.createImage(i, j);
	}

	private void drawTradeCfrmPanel()
	{
		tradeCfrmPan.getFrame().setTitle("Please confirm your trade with @yel@"
				+ DataOperations.longToString(tradeConfirmOtherNameLong));
		gameGraphics.drawBoxAlpha(tradeCfrmPan.getX(), tradeCfrmPan.getY(),
				tradeCfrmPan.getWidth(), tradeCfrmPan.getHeight(),
				tradeCfrmPan.getBGColor(), tradeCfrmPan.getBGAlpha());
		int itemsTitleHeight = tradeCfrmPan.getY() + tradeCfrmPan.getPlrTextBoxHeight();
		gameGraphics.drawText("You are about to give:",
				tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/4,
				itemsTitleHeight, 1, 0xffff00);
		gameGraphics.drawText("In return you will receive:",
				tradeCfrmPan.getX()+3*tradeCfrmPan.getWidth()/4,
				itemsTitleHeight, 1, 0xffff00);
		gameGraphics.drawText("Are you sure you want to do this?",
				tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/2,
				tradeCfrmPan.getY()+tradeCfrmPan.getHeight()-62, 4, 65535);
		gameGraphics.drawText("There is NO WAY to reverse a trade if you change your mind.",
				tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/2,
				tradeCfrmPan.getY()+tradeCfrmPan.getHeight()-47, 1, 0xffffff);
		gameGraphics.drawText("Remember that not all players are trustworthy",
				tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/2,
				tradeCfrmPan.getY()+tradeCfrmPan.getHeight()-32, 1, 0xffffff);
	}

	private void drawTradeItems()
	{
		int itemsTitleHeight = tradeCfrmPan.getY() + tradeCfrmPan.getPlrTextBoxHeight();
		if (tradeConfirmItemCount == 0)
			gameGraphics.drawText("Nothing!",
					tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/4,
					itemsTitleHeight + 12, 1, 0xffffff);
		for (int line = 0; line < tradeConfirmItemCount; line++)
		{
			String s = tradeConfirmItemsI[line].name();
			if (tradeConfirmItemsI[line].stackable())
				s = s + " x " + method74(tradeConfirmItemsI[line].amount);
			gameGraphics.drawText(s, tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/4,
					itemsTitleHeight + 12 + line * 12, 1, 0xffffff);
		}

		if (tradeConfirmOtherItemCount == 0)
			gameGraphics.drawText("Nothing!",
					tradeCfrmPan.getX()+3*tradeCfrmPan.getWidth()/4,
					itemsTitleHeight + 12, 1, 0xffffff);
		for (int line = 0; line < tradeConfirmOtherItemCount; line++)
		{
			String s1 = tradeConfirmOtherItemsI[line].name();
			if (tradeConfirmOtherItemsI[line].stackable())
				s1 = s1 + " x " + method74(tradeConfirmOtherItemsI[line].amount);
			gameGraphics.drawText(s1, tradeCfrmPan.getX()+3*tradeCfrmPan.getWidth()/4,
					itemsTitleHeight + 12 + line * 12, 1, 0xffffff);
		}
	}

	private void updateTradeCfrmButtons()
	{
		if (!tradeConfirmAccepted)
		{
			gameGraphics.drawPicture(tradeCfrmPan.getAcceptButton().getX(),
					tradeCfrmPan.getAcceptButton().getY(),
					tradeCfrmPan.getAcceptButton().getSpriteIdx());
			gameGraphics.drawPicture(tradeCfrmPan.getDeclineButton().getX(),
					tradeCfrmPan.getDeclineButton().getY(),
					tradeCfrmPan.getDeclineButton().getSpriteIdx());
		}
		else
		{
			gameGraphics.drawText("Waiting for other player...",
					tradeCfrmPan.getX()+tradeCfrmPan.getWidth()/4,
					tradeCfrmPan.getY()+tradeCfrmPan.getHeight()/4-12, 1, 0xffff00);
		}
	}

	private void handleTradeCfrmPanelClicks()
	{
		if (tradeCfrmPan.getAcceptButton().isMouseOverButton(
				super.mouseX, super.mouseY))
			formatPacket(53, -1, -1);
		if (!tradeCfrmPan.getFrame().isMouseOver(super.mouseX, super.mouseY)
				|| tradeCfrmPan.getDeclineButton().isMouseOverButton(
						super.mouseX, super.mouseY)
				|| tradeCfrmPan.getFrame().getCloseButton().isMouseOverButton(
						super.mouseX, super.mouseY))
			formatPacket(216, -1, -1);
		mouseButtonClick = 0;
	}

	private final void drawTradeConfirmWindow()
	{
		if (tradePan.isMouseOver(super.mouseX, super.mouseY))
		{
			if (mouseButtonClick == 1)
				handleTradeCfrmPanelClicks();
		}
		else if (tradePan.getFrame().getCloseButton().isMouseOverButton(
				super.mouseX, super.mouseY))
		{ // close button
			menuLength++;
			if (mouseButtonClick == 1)
			{
				formatPacket(216, -1, -1);
				mouseButtonClick = 0;
			}
		}
		else if (tradePan.getFrame().isMouseOver(super.mouseX, super.mouseY))
		{ // click inside settings panel but not on the content or close button
			menuLength++;
			if (mouseButtonClick == 1)
				mouseButtonClick = 0;
		}
		tradeCfrmPan.getFrame().drawComponent(super.mouseX, super.mouseY);
		drawTradeCfrmPanel();
		drawTradeItems();
		updateTradeCfrmButtons();
	}

	private final void walkToGroundItem(int walkSectionX, int walkSectionY,
			int x, int y, boolean coordsEqual) {
		if (sendWalkCommandIgnoreCoordsEqual(walkSectionX, walkSectionY, x, y, x, y, false, coordsEqual)) {
			return;
		} else {
			sendWalkCommand(walkSectionX, walkSectionY, x, y, x, y, true, coordsEqual);
			return;
		}
	}

	private final Mob addNPC(int serverIndex, double x, double y, int nextSprite, int type) {
		if (npcRecordArray[serverIndex] == null) {
			npcRecordArray[serverIndex] = new Mob();
			npcRecordArray[serverIndex].serverIndex = serverIndex;
		}
		Mob mob = npcRecordArray[serverIndex];
		boolean npcAlreadyExists = false;
		for (int lastNpcIndex = 0; lastNpcIndex < lastNpcCount; lastNpcIndex++) {
			if (lastNpcArray[lastNpcIndex].serverIndex != serverIndex)
				continue;
			npcAlreadyExists = true;
			break;
		}

		if (npcAlreadyExists) {
			mob.type = type;
			mob.nextSprite = nextSprite;
			int waypointCurrent = mob.waypointCurrent;
			if (x != mob.waypointsX[waypointCurrent] || y != mob.waypointsY[waypointCurrent]) {
				mob.waypointCurrent = waypointCurrent = (waypointCurrent + 1) % 10;
				mob.waypointsX[waypointCurrent] = x;
				mob.waypointsY[waypointCurrent] = y;
			}
		} else {
			mob.serverIndex = serverIndex;
			mob.waypointEndSprite = 0;
			mob.waypointCurrent = 0;
			mob.waypointsX[0] = mob.currentX = x;
			mob.waypointsY[0] = mob.currentY = y;
			mob.type = type;
			mob.nextSprite = mob.currentSprite = nextSprite;
			mob.stepCount = 0;
		}
		npcArray[npcCount++] = mob;
		return mob;
	}

	private final void drawDuelWindow()
	{
		int nInventoryCols = 5;
		int nInventoryRows = 6;
		int nDuelCols = 4;
		int nDuelRows = 2;
		int duelWindowWidth = 8 + (nDuelCols*itemSlotWidth+1) + 11 + (nInventoryCols*itemSlotWidth+1) + 6;
		int duelWindowHalfWidth = duelWindowWidth/2;
		int duelWidnowHeight = 12+18 + 2*(nDuelRows*itemSlotHeight+1) + 22 + 20;//      262;
		int duelWidnowHalfHeight = duelWidnowHeight/2;
		int duelWindowX = (windowHalfWidth - duelWindowHalfWidth);
		int duelWindowY = (windowHalfHeight - duelWidnowHalfHeight);
		int duelBgClr = 0x989898;
		int invBgClr = 0xd0d0d0;
		int duelWindowAlpha = 0xa0;
		int leftMarginWidth = 8;
		int middleMarginWidth = 11;
		int[] titleBar = {duelWindowX, duelWindowY, duelWindowWidth, 12, 0x0000c0, 0x00};
		int[] plrTextBox = {duelWindowX, titleBar[1]+titleBar[3], duelWindowWidth, 18, duelBgClr, duelWindowAlpha};
		int[] plrOfferBox = {duelWindowX+leftMarginWidth, plrTextBox[1]+plrTextBox[3],
				nDuelCols*itemSlotWidth+1, nDuelRows*itemSlotHeight+1, invBgClr, duelWindowAlpha};
		int[] opntTextBox = {duelWindowX+leftMarginWidth, plrOfferBox[1]+plrOfferBox[3],
				nDuelCols*itemSlotWidth+1, 22, duelBgClr, duelWindowAlpha};
		int[] opntOfferBox = {duelWindowX+leftMarginWidth, opntTextBox[1]+opntTextBox[3],
				nDuelCols*itemSlotWidth+1, nDuelRows*itemSlotHeight+1, invBgClr, duelWindowAlpha};
		int[] plrInvBox = {plrOfferBox[0]+plrOfferBox[2]+middleMarginWidth,
				plrTextBox[1]+plrTextBox[3], nInventoryCols*itemSlotWidth+1,
				nInventoryRows*itemSlotHeight+1, invBgClr, duelWindowAlpha};
		int[] btmLeftMarginBar = {duelWindowX+leftMarginWidth, opntOfferBox[1]+opntOfferBox[3],
				nDuelCols*itemSlotWidth+1, 20, duelBgClr, duelWindowAlpha};
		int[] leftMarginBar = {duelWindowX, plrTextBox[1]+plrTextBox[3], leftMarginWidth,
				plrOfferBox[3]+opntTextBox[3]+opntOfferBox[3]+btmLeftMarginBar[3],
				duelBgClr, duelWindowAlpha};
		int[] mdlMarginBar = {plrOfferBox[0]+plrOfferBox[2], plrTextBox[1]+plrTextBox[3],
				middleMarginWidth, plrOfferBox[3]+opntTextBox[3]+opntOfferBox[3]+btmLeftMarginBar[3],
				duelBgClr, duelWindowAlpha};
		int[] rgtMarginBar = {plrInvBox[0]+plrInvBox[2], plrTextBox[1]+plrTextBox[3],
				6, plrOfferBox[3]+opntTextBox[3]+opntOfferBox[3]+btmLeftMarginBar[3],
				duelBgClr, duelWindowAlpha};
		int[] accptDclnBox = {mdlMarginBar[0]+mdlMarginBar[2], plrInvBox[1]+plrInvBox[3], plrInvBox[2],
				btmLeftMarginBar[1]+btmLeftMarginBar[3]-(plrInvBox[1]+plrInvBox[3]),
				duelBgClr, duelWindowAlpha};
		Sprite dclnBtnSprite = ((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 26];
		Sprite accptBtnSprite = ((GameImage) (gameGraphics)).sprites[SPRITE_MEDIA_START + 25];
		int[] accptBtn = {accptDclnBox[0], accptDclnBox[1]+3, accptBtnSprite.getWidth(),
				accptBtnSprite.getHeight(), SPRITE_MEDIA_START + 25};
		int[] dclnBtn = {accptDclnBox[0]+accptDclnBox[2]-dclnBtnSprite.getWidth(),
				accptDclnBox[1]+3, dclnBtnSprite.getWidth(),
				accptBtnSprite.getHeight(), SPRITE_MEDIA_START + 26};
		if (mouseButtonClick != 0 && itemIncrement == 0)
			itemIncrement = 1;
		if (itemIncrement > 0) {
			int i = super.mouseX - 22;
			int j = super.mouseY - 36;
			if (i >= 0 && j >= 0 && i < 468 && j < 262) {
				if (i > 216 && j > 30 && i < 462 && j < 235) {
					int k = (i - 217) / 49 + ((j - 31) / 34) * 5;
					if (k >= 0 && k < inventoryCount) {
						boolean flag1 = false;
						int l1 = 0;
						int k2 = inventory[k].id;
						for (int k3 = 0; k3 < duelMyItemCount; k3++)
							if (duelMyItemsI[k3].id == k2)
								if (inventory[k].stackable()) {
									for (int i4 = 0; i4 < itemIncrement; i4++) {
										if (duelMyItemsI[k3].amount < inventory[k].amount)
											duelMyItemsI[k3].amount++;
										flag1 = true;
									}

								} else {
									l1++;
								}

						if (inventoryCount(k2) <= l1)
							flag1 = true;
						if (!flag1 && duelMyItemCount < 8) {
							duelMyItemsI[duelMyItemCount] = new Item(k2, 1);
							duelMyItemCount++;
							flag1 = true;
						}
						if (flag1) {
							super.streamClass.createPacket(123);
							super.streamClass.addByte(duelMyItemCount);
							for (int duelItem = 0; duelItem < duelMyItemCount; duelItem++) {
								super.streamClass.add2ByteInt(duelMyItemsI[duelItem].id);
								super.streamClass.add4ByteInt(duelMyItemsI[duelItem].amount);
							}

							super.streamClass.formatPacket();
							duelOpponentAccepted = false;
							duelMyAccepted = false;
						}
					}
				}
				if (i > 8 && j > 30 && i < 205 && j < 129) {
					int l = (i - 9) / 49 + ((j - 31) / 34) * 4;
					if (l >= 0 && l < duelMyItemCount) {
						int j1 = duelMyItemsI[l].id;
						for (int i2 = 0; i2 < itemIncrement; i2++) {
							if (EntityHandler.getItemDef(j1).isStackable() && duelMyItemsI[l].amount > 1) {
								duelMyItemsI[l].amount--;
								continue;
							}
							duelMyItemCount--;
							mouseDownTime = 0;
							for (int l2 = l; l2 < duelMyItemCount; l2++) {
								duelMyItemsI[l2] = duelMyItemsI[l2 + 1];
							}

							break;
						}

						super.streamClass.createPacket(123);
						super.streamClass.addByte(duelMyItemCount);
						for (int i3 = 0; i3 < duelMyItemCount; i3++) {
							super.streamClass.add2ByteInt(duelMyItemsI[i3].id);
							super.streamClass.add4ByteInt(duelMyItemsI[i3].amount);
						}

						super.streamClass.formatPacket();
						duelOpponentAccepted = false;
						duelMyAccepted = false;
					}
				}
				boolean flag = false;
				if (i >= 93 && j >= 221 && i <= 104 && j <= 232) {
					duelNoRetreating = !duelNoRetreating;
					flag = true;
				}
				if (i >= 93 && j >= 240 && i <= 104 && j <= 251) {
					duelNoMagic = !duelNoMagic;
					flag = true;
				}
				if (i >= 191 && j >= 221 && i <= 202 && j <= 232) {
					duelNoPrayer = !duelNoPrayer;
					flag = true;
				}
				if (i >= 191 && j >= 240 && i <= 202 && j <= 251) {
					duelNoWeapons = !duelNoWeapons;
					flag = true;
				}
				if (flag) {
					super.streamClass.createPacket(225);
					super.streamClass.addByte(duelNoRetreating ? 1 : 0);
					super.streamClass.addByte(duelNoMagic ? 1 : 0);
					super.streamClass.addByte(duelNoPrayer ? 1 : 0);
					super.streamClass.addByte(duelNoWeapons ? 1 : 0);
					super.streamClass.formatPacket();
					duelOpponentAccepted = false;
					duelMyAccepted = false;
				}
				if (i >= 217 && j >= 238 && i <= 286 && j <= 259) {
					duelMyAccepted = true;
					super.streamClass.createPacket(252);
					super.streamClass.formatPacket();
				}
				if (i >= 394 && j >= 238 && i < 463 && j < 259) {
					showDuelWindow = false;
					super.streamClass.createPacket(35);
					super.streamClass.formatPacket();
				}
			} else if (mouseButtonClick != 0) {
				showDuelWindow = false;
				super.streamClass.createPacket(35);
				super.streamClass.formatPacket();
			}
			mouseButtonClick = 0;
			itemIncrement = 0;
		}
		if (!showDuelWindow)
			return;
		byte byte0 = 22;
		byte byte1 = 36;
		gameGraphics.drawBox(byte0, byte1, 468, 12, 0xc90b1d);
		int i1 = 0x989898;
		gameGraphics.drawBoxAlpha(byte0, byte1 + 12, 468, 18, i1, 160);
		gameGraphics.drawBoxAlpha(byte0, byte1 + 30, 8, 248, i1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 205, byte1 + 30, 11, 248, i1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 462, byte1 + 30, 6, 248, i1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 8, byte1 + 99, 197, 24, i1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 8, byte1 + 192, 197, 23, i1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 8, byte1 + 258, 197, 20, i1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 216, byte1 + 235, 246, 43, i1, 160);
		int k1 = 0xd0d0d0;
		gameGraphics.drawBoxAlpha(byte0 + 8, byte1 + 30, 197, 69, k1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 8, byte1 + 123, 197, 69, k1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 8, byte1 + 215, 197, 43, k1, 160);
		gameGraphics.drawBoxAlpha(byte0 + 216, byte1 + 30, 246, 205, k1, 160);
		for (int j2 = 0; j2 < 3; j2++)
			gameGraphics.drawLineX(byte0 + 8, byte1 + 30 + j2 * 34, 197, 0);

		for (int j3 = 0; j3 < 3; j3++)
			gameGraphics.drawLineX(byte0 + 8, byte1 + 123 + j3 * 34, 197, 0);

		for (int l3 = 0; l3 < 7; l3++)
			gameGraphics.drawLineX(byte0 + 216, byte1 + 30 + l3 * 34, 246, 0);

		for (int k4 = 0; k4 < 6; k4++) {
			if (k4 < 5)
				gameGraphics.drawLineY(byte0 + 8 + k4 * 49, byte1 + 30, 69, 0);
			if (k4 < 5)
				gameGraphics.drawLineY(byte0 + 8 + k4 * 49, byte1 + 123, 69, 0);
			gameGraphics.drawLineY(byte0 + 216 + k4 * 49, byte1 + 30, 205, 0);
		}

		gameGraphics.drawLineX(byte0 + 8, byte1 + 215, 197, 0);
		gameGraphics.drawLineX(byte0 + 8, byte1 + 257, 197, 0);
		gameGraphics.drawLineY(byte0 + 8, byte1 + 215, 43, 0);
		gameGraphics.drawLineY(byte0 + 204, byte1 + 215, 43, 0);
		gameGraphics.drawString("Preparing to duel with: " + duelOpponentName, byte0 + 1, byte1 + 10, 1, 0xffffff);
		gameGraphics.drawString("Your Stake", byte0 + 9, byte1 + 27, 4, 0xffffff);
		gameGraphics.drawString("Opponent's Stake", byte0 + 9, byte1 + 120, 4, 0xffffff);
		gameGraphics.drawString("Duel Options", byte0 + 9, byte1 + 212, 4, 0xffffff);
		gameGraphics.drawString("Your Inventory", byte0 + 216, byte1 + 27, 4, 0xffffff);
		gameGraphics.drawString("No retreating", byte0 + 8 + 1, byte1 + 215 + 16, 3, 0xffff00);
		gameGraphics.drawString("No magic", byte0 + 8 + 1, byte1 + 215 + 35, 3, 0xffff00);
		gameGraphics.drawString("No prayer", byte0 + 8 + 102, byte1 + 215 + 16, 3, 0xffff00);
		gameGraphics.drawString("No weapons", byte0 + 8 + 102, byte1 + 215 + 35, 3, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 93, byte1 + 215 + 6, 11, 11, 0xffff00);
		if (duelNoRetreating)
			gameGraphics.drawBox(byte0 + 95, byte1 + 215 + 8, 7, 7, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 93, byte1 + 215 + 25, 11, 11, 0xffff00);
		if (duelNoMagic)
			gameGraphics.drawBox(byte0 + 95, byte1 + 215 + 27, 7, 7, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 191, byte1 + 215 + 6, 11, 11, 0xffff00);
		if (duelNoPrayer)
			gameGraphics.drawBox(byte0 + 193, byte1 + 215 + 8, 7, 7, 0xffff00);
		gameGraphics.drawBoxEdge(byte0 + 191, byte1 + 215 + 25, 11, 11, 0xffff00);
		if (duelNoWeapons)
			gameGraphics.drawBox(byte0 + 193, byte1 + 215 + 27, 7, 7, 0xffff00);
		if (!duelMyAccepted)
			gameGraphics.drawPicture(byte0 + 217, byte1 + 238, SPRITE_MEDIA_START + 25);
		gameGraphics.drawPicture(byte0 + 394, byte1 + 238, SPRITE_MEDIA_START + 26);
		if (duelOpponentAccepted) {
			gameGraphics.drawText("Other player", byte0 + 341, byte1 + 246, 1, 0xffffff);
			gameGraphics.drawText("has accepted", byte0 + 341, byte1 + 256, 1, 0xffffff);
		}
		if (duelMyAccepted) {
			gameGraphics.drawText("Waiting for", byte0 + 217 + 35, byte1 + 246, 1, 0xffffff);
			gameGraphics.drawText("other player", byte0 + 217 + 35, byte1 + 256, 1, 0xffffff);
		}
		for (int l4 = 0; l4 < inventoryCount; l4++) {
			int i5 = 217 + byte0 + (l4 % 5) * 49;
			int k5 = 31 + byte1 + (l4 / 5) * 34;
			gameGraphics.spriteClip4(i5, k5, 48, 32, SPRITE_ITEM_START + inventory[l4].icon(),
					inventory[l4].color(), 0, 0, false);
			if (inventory[l4].stackable())
				gameGraphics.drawString(Integer.toString(inventory[l4].amount),
						i5 + 1, k5 + 10, 1, 0xffff00);
		}

		for (int j5 = 0; j5 < duelMyItemCount; j5++) {
			int l5 = 9 + byte0 + (j5 % 4) * 49;
			int j6 = 31 + byte1 + (j5 / 4) * 34;
			gameGraphics.spriteClip4(l5, j6, 48, 32, SPRITE_ITEM_START + duelMyItemsI[j5].icon(),
					duelMyItemsI[j5].color(), 0, 0, false);
			if (duelMyItemsI[j5].stackable())
				gameGraphics.drawString(Integer.toString(duelMyItemsI[j5].amount),
						l5 + 1, j6 + 10, 1, 0xffff00);
			if (super.mouseX > l5 && super.mouseX < l5 + 48 && super.mouseY > j6 && super.mouseY < j6 + 32)
				gameGraphics.drawString(
						duelMyItemsI[j5].name() + ": @whi@" + duelMyItemsI[j5].description(),
						byte0 + 8, byte1 + 273, 1, 0xffff00);
		}

		for (int i6 = 0; i6 < duelOpponentItemCount; i6++) {
			int k6 = 9 + byte0 + (i6 % 4) * 49;
			int l6 = 124 + byte1 + (i6 / 4) * 34;
			gameGraphics.spriteClip4(k6, l6, 48, 32,
					SPRITE_ITEM_START + duelOpponentItemsI[i6].icon(),
					duelOpponentItemsI[i6].color(), 0, 0, false);
			if (duelOpponentItemsI[i6].stackable())
				gameGraphics.drawString(Integer.toString(duelOpponentItemsI[i6].amount),
						k6 + 1, l6 + 10, 1, 0xffff00);
			if (super.mouseX > k6 && super.mouseX < k6 + 48 && super.mouseY > l6 && super.mouseY < l6 + 32)
				gameGraphics.drawString(
						duelOpponentItemsI[i6].name() + ": @whi@" + duelOpponentItemsI[i6].description(),
						byte0 + 8, byte1 + 273, 1, 0xffff00);
		}

	}

	private final void drawServerMessageBox() {
		char c = '\u0190';
		char c1 = 'd';
		if (serverMessageBoxTop) {
			c1 = '\u01C2';
			c1 = '\u012C';
		}
		gameGraphics.drawBox(256 - c / 2, 167 - c1 / 2, c, c1, 0);
		gameGraphics.drawBoxEdge(256 - c / 2, 167 - c1 / 2, c, c1, 0xffffff);
		gameGraphics.drawBoxTextColour(serverMessage, 256, (167 - c1 / 2) + 20, 1, 0xffffff, c - 40);
		int i = 157 + c1 / 2;
		int j = 0xffffff;
		if (super.mouseY > i - 12 && super.mouseY <= i && super.mouseX > 106 && super.mouseX < 406)
			j = 0xff0000;
		gameGraphics.drawText("Click here to close window", 256, i, 1, j);
		if (mouseButtonClick == 1) {
			if (j == 0xff0000)
				showServerMessageBox = false;
			if ((super.mouseX < 256 - c / 2 || super.mouseX > 256 + c / 2) && (super.mouseY < 167 - c1 / 2 || super.mouseY > 167 + c1 / 2))
				showServerMessageBox = false;
		}
		mouseButtonClick = 0;
	}

	private final void makeLoginMenus()
	{
		int menuWidth = 210;
		int mnenuHeight = 250;
		int menuX = windowHalfWidth - menuWidth/2;
		int menuY = windowHalfHeight - mnenuHeight/2;
		int menuClor = 0x343434;
		int menuBorderColor = 0x000000;
		int menuAlpha = 0x7f;
		int rowHeight = 20;
		int menuTextFieldWidth = menuWidth-10;
		int menuTextFieldHeight = rowHeight;
		int menuTextFieldColor = 0x343434;
		int menuTextFieldBorderColor = 0x000000;
		int menuTextFieldAlpha = 0x9f;
		int menuButtonWidth = menuWidth-10;
		int menuButtonHeight = rowHeight;
		int menuButtonColor = 0xcc0000;
		int menuButtonBorderColor = 0x000000;
		int menuButtonAlpha = 0xff;
		menuWelcome = new Menu(gameGraphics, 50);
		menuNewUser = new Menu(gameGraphics, 50);
		menuLogin = new Menu(gameGraphics, 50);
		menuWelcome.drawBox(menuX+menuWidth/2, menuY+mnenuHeight/2, menuWidth,
				mnenuHeight, menuClor, menuBorderColor, menuAlpha);
		menuNewUser.drawBox(menuX+menuWidth/2, menuY+mnenuHeight/2, menuWidth,
				mnenuHeight, menuClor, menuBorderColor, menuAlpha);
		menuLogin.drawBox(menuX+menuWidth/2, menuY+mnenuHeight/2, menuWidth,
				mnenuHeight, menuClor, menuBorderColor, menuAlpha);
		int row = 3;

		/* menu welcome */
		menuWelcome.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"Welcome to TestServer", 4, false);
		row++;
		menuWelcome.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"TestServer is still in beta", 4, false);
		row +=2;
		menuWelcome.drawBox(menuX+menuWidth/2, menuY + row*rowHeight,
				menuButtonWidth, menuButtonHeight, menuButtonColor,
				menuButtonBorderColor, menuButtonAlpha);
		menuWelcome.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"Click here to login", 5, false);
		loginButtonExistingUser = menuWelcome.makeButton(menuX+menuWidth/2,
				menuY + row*rowHeight, menuButtonWidth, menuButtonHeight);
		row +=2;
		menuWelcome.drawBox(menuX+menuWidth/2, menuY + row*rowHeight,
				menuButtonWidth, menuButtonHeight, menuButtonColor,
				menuButtonBorderColor, menuButtonAlpha);
		menuWelcome.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"New User", 5, false);
		loginButtonNewUser = menuWelcome.makeButton(menuX+menuWidth/2,
				menuY + row*rowHeight, menuButtonWidth, menuButtonHeight);

		/* menu new user */
		row = 3;
		menuNewUser.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"To create an account please go back to the", 4, false);
		row++;
		menuNewUser.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"localhost front page, and choose 'register'", 4, false);
		row +=2;
		menuNewUser.drawBox(menuX+menuWidth/2, menuY + row*rowHeight,
				menuButtonWidth, menuButtonHeight, menuButtonColor,
				menuButtonBorderColor, menuButtonAlpha);
		menuNewUser.drawText(menuX+menuWidth/2, menuY + row*rowHeight, "Ok", 5, false);
		newUserOkButton = menuNewUser.makeButton(menuX+menuWidth/2,
				menuY + row*rowHeight, 150, 34);

		/* menu login */
		row = 1;
		loginStatusText = menuLogin.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"Please enter your username and password", 4, false);
		row +=4;
		menuLogin.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"Username:", 4, false);
		menuLogin.drawBox(menuX+menuWidth/2, menuY + (row+1)*rowHeight,
				menuTextFieldWidth, menuTextFieldHeight, menuTextFieldColor,
				menuTextFieldBorderColor, menuTextFieldAlpha);
		loginUsernameTextBox = menuLogin.makeTextBox(menuX + 7 + menuTextFieldWidth/2,
				menuY + row*rowHeight + 6 + menuTextFieldHeight/2,
				menuTextFieldWidth, menuTextFieldHeight, 5, 12, false, false);
		row+=2;
		menuLogin.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"Password:", 4, false);
		menuLogin.drawBox(menuX +menuWidth/2, menuY + (row+1)*rowHeight,
				menuTextFieldWidth, menuTextFieldHeight, menuTextFieldColor,
				menuTextFieldBorderColor, menuTextFieldAlpha);
		loginPasswordTextBox = menuLogin.makeTextBox(menuX + 7 + menuTextFieldWidth/2,
				menuY + row*rowHeight+6 + menuTextFieldHeight/2,
				menuTextFieldWidth, menuTextFieldHeight, 5, 20, true, false);
		row +=3;
		menuLogin.drawBox(menuX+menuWidth/2, menuY + row*rowHeight,
				menuButtonWidth, menuButtonHeight, menuButtonColor,
				menuButtonBorderColor, menuButtonAlpha);
		menuLogin.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
				"Log in", 4, false);
		loginOkButton = menuLogin.makeButton(menuX+menuWidth/2, menuY + row*rowHeight,
				menuButtonWidth, menuButtonHeight);
		/*
        menuLogin.drawBox(menuX+menuWidth/2, menuY + row*rowHeight,
        		menuButtonWidth, menuButtonHeight, menuButtonColor,
        		menuButtonBorderColor, menuButtonAlpha);
        menuLogin.drawText(menuX+menuWidth/2, menuY + row*rowHeight,
        		"New User", 4, false);
        loginCancelButton = menuLogin.makeButton(menuX+menuWidth/2,
        		menuY + row*rowHeight, menuButtonWidth, menuButtonHeight);*/
		menuLogin.setFocus(loginUsernameTextBox);
	}

	/**
	 * TODO: remake all of these to make them fit a fullscreen
	 */
	private final void drawGameWindowsMenus()
	{
		if (logoutTimeout != 0)
			drawLoggingOutBox();
		else if (showWelcomeBox)
			drawWelcomeBox();
		else if (showServerMessageBox)
			drawServerMessageBox();
		else if (wildernessType == 1) // 0 = not wild, 1 = close to wild, 2 = wild
			drawWildernessWarningBox();
		else if (showBank && lastWalkTimeout == 0)
			drawBankBox();
		else if (showShop && lastWalkTimeout == 0)
			drawShopBox();
		else if (showTradeConfirmWindow)
			drawTradeConfirmWindow();
		else if (showTradeWindow)
			drawTradeWindow();
		else if (showDuelConfirmWindow)
			drawDuelConfirmWindow();
		else if (showDuelWindow)
			drawDuelWindow();
		else if (showAbuseWindow == 1)
			drawAbuseWindow1();
		else if (showAbuseWindow == 2)
			drawAbuseWindow2();
		else if (inputBoxType != 0) {
			drawInputBox();
		} else {
			if (showQuestionMenu)
				drawQuestionMenu();
			if (ourPlayer.currentSprite == 8
					|| ourPlayer.currentSprite == 9
					|| combatWindow)
				drawCombatStyleWindow();
			checkMouseOverMenus();
			boolean noMenusShown = !showQuestionMenu && !showRightClickMenu;
			if (noMenusShown)
				menuLength = 0;
			if ((mouseOverMenu == 0
					|| mouseOverMenu == 1
					|| mouseOverMenu == 2
					|| mouseOverMenu == 3
					|| mouseOverMenu == 4
					|| mouseOverMenu == 5
					|| mouseOverMenu == 6) && noMenusShown)
			{ // since we no longer need to mouseover the menu to keep them open
				drawInventoryRightClickMenu();
			}
			if (mouseOverMenu == 1)
				drawInventoryMenu(noMenusShown);
			/* We always show the minimap now
            if (mouseOverMenu == 2)
                drawMapMenu(noMenusShown);
			 */
			if (mouseOverMenu == 3)
				drawPlayerInfoMenu(noMenusShown);
			if (mouseOverMenu == 4)
				drawMagicWindow(noMenusShown);
			if (mouseOverMenu == 5)
				drawFriendsWindow(noMenusShown);
			if (mouseOverMenu == 6)
				drawOptionsMenu(noMenusShown);
			if (!showRightClickMenu && !showQuestionMenu)
				checkMouseStatus();
			if (showRightClickMenu && !showQuestionMenu)
				drawRightClickMenu();
		}
		mouseButtonClick = 0;
	}

	private final void method112(int sectionX, int sectionY, int x, int y, boolean flag) {
		sendWalkCommand(sectionX, sectionY, x, y, x, y, false, flag);
	}

	private final void drawInputBox()
	{
		//int i = 145;
		int i = windowHalfHeight - 22;
		if (mouseButtonClick != 0) {
			mouseButtonClick = 0;
			if (inputBoxType == 1
					&& (super.mouseX < windowHalfWidth - 150 || super.mouseY < i
							|| super.mouseX > windowHalfWidth + 150 || super.mouseY > i + 70)) {
				inputBoxType = 0;
				return;
			}
			if (inputBoxType == 2
					&& (super.mouseX < windowHalfWidth - 250 || super.mouseY < i
							|| super.mouseX > windowHalfWidth + 250 || super.mouseY > i + 70)) {
				inputBoxType = 0;
				return;
			}
			if (inputBoxType == 3
					&& (super.mouseX < windowHalfWidth - 150 || super.mouseY < i
							|| super.mouseX > windowHalfWidth + 150 || super.mouseY > i + 70)) {
				inputBoxType = 0;
				return;
			}
			if (super.mouseX > windowHalfWidth - 20 && super.mouseX < windowHalfWidth + 20
					&& super.mouseY > windowHalfHeight + 26 && super.mouseY < windowHalfHeight + 46) {
				inputBoxType = 0;
				return;
			}
		}
		if (inputBoxType == 1) {
			gameGraphics.drawBox(windowHalfWidth - 150, i, 300, 70, 0);
			gameGraphics.drawBoxEdge(windowHalfWidth - 150, i, 300, 70, 0xffffff);
			i += 20;
			gameGraphics.drawText("Enter name to add to friends list", windowHalfWidth, i, 4, 0xffffff);
			i += 20;
			gameGraphics.drawText(super.inputText + "*", windowHalfWidth, i, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s = super.enteredText.trim();
				super.inputText = "";
				super.enteredText = "";
				inputBoxType = 0;
				if (s.length() > 0 && DataOperations.stringLength12ToLong(s) != ourPlayer.nameLong)
					addToFriendsList(s);
			}
		}
		if (inputBoxType == 2) {
			gameGraphics.drawBox(windowHalfWidth - 250, i, 500, 70, 0);
			gameGraphics.drawBoxEdge(windowHalfWidth - 250, i, 500, 70, 0xffffff);
			i += 20;
			gameGraphics.drawText("Enter message to send to " + DataOperations.longToString(privateMessageTarget), 256, i, 4, 0xffffff);
			i += 20;
			gameGraphics.drawText(super.inputMessage + "*", windowHalfWidth, i, 4, 0xffffff);
			if (super.enteredMessage.length() > 0) {
				String s1 = super.enteredMessage;
				super.inputMessage = "";
				super.enteredMessage = "";
				inputBoxType = 0;
				byte[] message = DataConversions.stringToByteArray(s1);
				sendPrivateMessage(privateMessageTarget, message, message.length);
				s1 = DataConversions.byteToString(message, 0, message.length);
				handleServerMessage("@pri@You tell " + DataOperations.longToString(privateMessageTarget) + ": " + s1);
			}
		}
		if (inputBoxType == 3) {
			gameGraphics.drawBox(windowHalfWidth - 150, i, 300, 70, 0);
			gameGraphics.drawBoxEdge(windowHalfWidth - 150, i, 300, 70, 0xffffff);
			i += 20;
			gameGraphics.drawText("Enter name to add to ignore list", windowHalfWidth, i, 4, 0xffffff);
			i += 20;
			gameGraphics.drawText(super.inputText + "*", windowHalfWidth, i, 4, 0xffffff);
			if (super.enteredText.length() > 0) {
				String s2 = super.enteredText.trim();
				super.inputText = "";
				super.enteredText = "";
				inputBoxType = 0;
				if (s2.length() > 0 && DataOperations.stringLength12ToLong(s2) != ourPlayer.nameLong)
					addToIgnoreList(s2);
			}
		}
		int j = 0xffffff;
		if (super.mouseX > windowHalfWidth - 20 && super.mouseX < windowHalfWidth + 20
				&& super.mouseY > windowHalfHeight + 26 && super.mouseY < windowHalfHeight + 46)
			j = 0xffff00;
		gameGraphics.drawText("Cancel", windowHalfWidth, windowHalfHeight + 41, 1, j);
	}

	private final boolean hasRequiredRunes(int i, int j) {
		if (i == 31 && (method117(197) || method117(615) || method117(682))) {
			return true;
		}
		if (i == 32 && (method117(102) || method117(616) || method117(683))) {
			return true;
		}
		if (i == 33 && (method117(101) || method117(617) || method117(684))) {
			return true;
		}
		if (i == 34 && (method117(103) || method117(618) || method117(685))) {
			return true;
		}
		return inventoryCount(i) >= j;
	}

	private final void resetPrivateMessageStrings() {
		super.inputMessage = "";
		super.enteredMessage = "";
	}

	private final boolean method117(int i) {
		for (int j = 0; j < inventoryCount; j++)
			if (inventory[j].id == i && wearing[j] == 1)
				return true;

		return false;
	}

	private final void setPixelsAndAroundColour(int x, int y, int colour) {
		gameGraphics.setMinimapPixel(x, y, colour);
		gameGraphics.setMinimapPixel(x - 1, y, colour);
		gameGraphics.setMinimapPixel(x + 1, y, colour);
		gameGraphics.setMinimapPixel(x, y - 1, colour);
		gameGraphics.setMinimapPixel(x, y + 1, colour);
	}

	private final void method119() {
		for (int i = 0; i < mobMessageCount; i++) {
			int j = gameGraphics.messageFontHeight(1);
			int l = mobMsgX[i];
			int k1 = mobMsgY[i];
			int j2 = mobMsgWidth[i];
			int i3 = mobMsgHeight[i];
			boolean flag = true;
			while (flag) {
				flag = false;
				for (int i4 = 0; i4 < i; i4++)
					if (k1 + i3 > mobMsgY[i4] - j && k1 - j < mobMsgY[i4] + mobMsgHeight[i4] && l - j2 < mobMsgX[i4] + mobMsgWidth[i4] && l + j2 > mobMsgX[i4] - mobMsgWidth[i4] && mobMsgY[i4] - j - i3 < k1) {
						k1 = mobMsgY[i4] - j - i3;
						flag = true;
					}

			}
			mobMsgY[i] = k1;
			gameGraphics.drawBoxTextColour(mobMsg[i], l, k1, 1, 0xffff00, 300);
		}

		for (int k = 0; k < anInt699; k++)
		{
			int i1 = anIntArray858[k];
			int l1 = anIntArray859[k];
			int k2 = anIntArray705[k];
			int j3 = anIntArray706[k];
			int l3 = (39 * k2) / 100;
			int j4 = (27 * k2) / 100;
			int k4 = l1 - j4;
			gameGraphics.spriteClip2(i1 - l3 / 2, k4, l3, j4, SPRITE_MEDIA_START + 9, 85);
			int l4 = (36 * k2) / 100;
			int i5 = (24 * k2) / 100;
			gameGraphics.spriteClip4(i1 - l4 / 2, (k4 + j4 / 2) - i5 / 2, l4, i5,
					EntityHandler.getItemDef(j3).getSprite() + SPRITE_ITEM_START,
					EntityHandler.getItemDef(j3).getPictureMask(), 0, 0, false);
		}

		for (int j1 = 0; j1 < anInt718; j1++) {
			int i2 = anIntArray786[j1];
			int l2 = anIntArray787[j1];
			int k3 = anIntArray788[j1];
			gameGraphics.drawBoxAlpha(i2 - 15, l2 - 3, k3, 5, 65280, 192);
			gameGraphics.drawBoxAlpha((i2 - 15) + k3, l2 - 3, 30 - k3, 5, 0xff0000, 192);
		}

	}

	private final void drawMapMenu(boolean canClick) {
		gameGraphics.drawBox(miniMapX-1, miniMapY-1, miniMapWidth+2,
				miniMapHeight+2, 0x000000);
		gameGraphics.setDimensions(miniMapX, miniMapY,
				miniMapX + miniMapWidth, miniMapY + miniMapHeight);
		int k = 192 + anInt986;
		int i1 = cameraZRot + anInt985 & 0xff;
		double gameSize = EngineHandle.GAME_SIZE;
		double scaleFactor = EngineHandle.SCALE_FACTOR;
		double k1 = ((ourPlayer.currentX - 6040.0*scaleFactor) * 3 * k) / (2048.0 * scaleFactor);
		double i3 = ((ourPlayer.currentY - 6040.0*scaleFactor) * 3 * k) / (2048.0 * scaleFactor);
		double sin = Camera.sin1024[0x400 - i1 * 4 & 0x3ff];
		double cos = Camera.cos1024[0x400 - i1 * 4 & 0x3ff];
		double tmp = (i3 * sin + k1 * cos) / 8;
		i3 = (i3 * cos - k1 * sin) / 8;
		k1 = tmp;
		/* minimap tiles */
		gameGraphics.drawMinimapTiles(miniMapX + miniMapWidth / 2 - (int)k1,
				miniMapY + miniMapHeight / 2 + (int)i3, SPRITE_MEDIA_START - 1,
				i1 + 64 & 0xff, k);
		for (int i7 = 0; i7 < objectCount; i7++)
		{
			double l1 = (((objectX[i7] * gameSize + gameSize/2) - ourPlayer.currentX) * 3 * k) / (2048*scaleFactor);
			double j3 = (((objectY[i7] * gameSize + gameSize/2) - ourPlayer.currentY) * 3 * k) / (2048*scaleFactor);
			tmp = (j3 * sin + l1 * cos) / 8;
			j3 = (j3 * cos - l1 * sin) / 8;
			l1 = tmp;
			setPixelsAndAroundColour(miniMapX + miniMapWidth / 2 + (int)l1,
					miniMapY + miniMapHeight / 2 - (int)j3, 0x00ffff);
		}

		for (int j7 = 0; j7 < groundItemCount; j7++)
		{
			double i2 = (((groundItemX[j7] * gameSize + gameSize/2) - ourPlayer.currentX) * 3 * k) / (2048*scaleFactor);
			double k3 = (((groundItemY[j7] * gameSize + gameSize/2) - ourPlayer.currentY) * 3 * k) / (2048*scaleFactor);
			tmp = (k3 * sin + i2 * cos) / 8;
			k3 = (k3 * cos - i2 * sin) / 8;
			i2 = tmp;
			setPixelsAndAroundColour(miniMapX + miniMapWidth / 2 + (int)i2,
					(miniMapY + miniMapHeight / 2) - (int)k3, 0xff0000);
		}

		for (int k7 = 0; k7 < npcCount; k7++)
		{
			Mob mob = npcArray[k7];
			double j2 = ((mob.currentX - ourPlayer.currentX) * 3 * k) / (2048*scaleFactor);
			double l3 = ((mob.currentY - ourPlayer.currentY) * 3 * k) / (2048*scaleFactor);
			tmp = (l3 * sin + j2 * cos) / 8;
			l3 = (l3 * cos - j2 * sin) / 8;
			j2 = tmp;
			setPixelsAndAroundColour(miniMapX + miniMapWidth / 2 + (int)j2,
					(miniMapY + miniMapHeight / 2) - (int)l3, 0xffff00);
		}

		for (int l7 = 0; l7 < playerCount; l7++) {
			Mob mob_1 = playerArray[l7];
			double k2 = ((mob_1.currentX - ourPlayer.currentX) * 3 * k) / (2048*scaleFactor);
			double i4 = ((mob_1.currentY - ourPlayer.currentY) * 3 * k) / (2048*scaleFactor);
			tmp = (i4 * sin + k2 * cos) / 8;
			i4 = (i4 * cos - k2 * sin) / 8;
			k2 = tmp;
			int j8 = 0xffffff;
			for (int k8 = 0; k8 < super.friendsCount; k8++) {
				if (mob_1.nameLong != super.friendsListLongs[k8]
						|| super.friendsListOnlineStatus[k8] != 99)
					continue;
				j8 = 0x00ff00;
				break;
			}

			setPixelsAndAroundColour(miniMapX + miniMapWidth / 2 + (int)k2,
					(miniMapY + miniMapHeight / 2) - (int)i4, j8);
		}

		// player dot on map
		gameGraphics.method212(miniMapX + miniMapWidth / 2,
				miniMapY + miniMapHeight / 2, 2, 0xffffff, 0xff);
		// compas
		gameGraphics.drawMinimapTiles(miniMapX + 19, miniMapY + 19, SPRITE_MEDIA_START + 24,
				cameraZRot + 128 & 0xff, 128);
		gameGraphics.setDimensions(0, 0, windowWidth, windowHeight + 12);
		if (!canClick)
			return;
		if (super.mouseX >= miniMapX
				&& super.mouseY >= miniMapY
				&& super.mouseX < miniMapX + miniMapWidth
				&& super.mouseY < miniMapY + miniMapHeight)
		{
			int l = 192 + anInt986;
			int j1 = cameraZRot + anInt985 & 0xff;
			double l2 = ((super.mouseX - (miniMapX + miniMapWidth / 2)) * 0x4000) / (3 * l);
			double j4 = ((super.mouseY - (miniMapY + miniMapHeight / 2)) * 0x4000) / (3 * l);
			double sin1 = Camera.sin1024[0x400 - j1 * 4 & 0x3ff];
			double cos1 = Camera.cos1024[0x400 - j1 * 4 & 0x3ff];
			tmp = j4 * sin1 + l2 * cos1;
			j4 = j4 * cos1 - l2 * sin1;
			l2 = tmp;
			l2 += ourPlayer.currentX;
			j4 = ourPlayer.currentY - j4;
			if (mouseButtonClick == 1)
				method112(sectionX, sectionY, EngineHandle.getTileXFromX(l2),
						EngineHandle.getTileYFromY(j4), false);
			mouseButtonClick = 0;
		}
	}

	protected final void handleScrollEvent(int scrollDirection)
	{
		// cameraZoomFactor = 150.0; // how much further away the camera moves
		if (scrollDirection > 0D)
		{
			if (cameraZoom < 1.41)
			{
				cameraZoom += 0.1;
				gameCamera.drawModelMaxDist += 150.0 * EngineHandle.SCALE_FACTOR;
				gameCamera.drawSpriteMaxDist += 150.0 * EngineHandle.SCALE_FACTOR;
				if (!super.keyF1Toggle)
					gameCamera.fadeDist += 150*2.3/2.4 * EngineHandle.SCALE_FACTOR;
				else
					gameCamera.fadeDist += 150*2.1/2.2 * EngineHandle.SCALE_FACTOR;
			}
		}
		else if (scrollDirection < 0D)
		{
			if (cameraZoom > .39)
			{
				cameraZoom -= 0.1;
				gameCamera.drawModelMaxDist -= 150.0 * EngineHandle.SCALE_FACTOR;
				gameCamera.drawSpriteMaxDist -= 150.0 * EngineHandle.SCALE_FACTOR;
				if (!super.keyF1Toggle)
					gameCamera.fadeDist -= 150*2.3/2.4 * EngineHandle.SCALE_FACTOR;
				else
					gameCamera.fadeDist -= 150*2.1/2.2 * EngineHandle.SCALE_FACTOR;
			}
		}
	}
	
	public int xToTileX(double x)
	{
		return (int) ((x - EngineHandle.GAME_SIZE/2) / EngineHandle.GAME_SIZE);
	}
	
	public int yToTileY(double y)
	{
		return (int) ((y - EngineHandle.GAME_SIZE/2) / EngineHandle.GAME_SIZE);
	}

	public mudclient()
	{
		combatWindow = false;
		threadSleepTime = 10;
		try {
			localhost = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException uhe)
		{
			localhost = "unknown";
		}
		startTime = System.currentTimeMillis();
		duelMyItemsI = new Item[8];
		configAutoCameraAngle = true;
		questionMenuAnswer = new String[10];
		lastNpcArray = new Mob[500];
		currentUser = "";
		currentPass = "";
		
		rightClickMenu = new MenuRightClick[250];
		
		duelOpponentAccepted = false;
		duelMyAccepted = false;
		tradeConfirmItemsI = new Item[14];
		tradeConfirmOtherItemsI = new Item[14];
		serverMessage = "";
		duelOpponentName = "";
		inventory = new Item[35];
		wearing = new int[35];
		mobMsg = new String[50];
		showBank = false;
		doorModel = new Model[500];
		mobMsgX = new int[50];
		mobMsgY = new int[50];
		mobMsgWidth = new int[50];
		mobMsgHeight = new int[50];
		npcArray = new Mob[500];
		equipmentStatus = new int[6];
		prayerOn = new boolean[50];
		tradeOtherAccepted = false;
		tradeWeAccepted = false;
		mobArray = new Mob[8000];
		anIntArray705 = new int[50];
		anIntArray706 = new int[50];
		sectorHeight = -1;
		memoryError = false;
		bankItemsMax = 48;
		showQuestionMenu = false;
		viewDistance = 2400*2;
		cameraAutoAngle = 1;
		anInt727 = 2;
		showServerMessageBox = false;
		hasReceivedWelcomeBoxDetails = false;
		playerStatCurrent = new int[18];
		wildYSubtract = -1;
		anInt742 = -1;
		anInt743 = -1;
		anInt744 = -1;
		sectionXArray = new int[8000];
		sectionYArray = new int[8000];
		selectedItem = -1;
		selectedItemName = "";
		duelOpponentItemsI = new Item[8];
		anIntArray757 = new int[50];
		showCharacterLookScreen = false;
		lastPlayerArray = new Mob[500];
		appletMode = true;
		gameDataModels = new Model[1000];
		configMouseButtons = false;
		duelNoRetreating = false;
		duelNoMagic = false;
		duelNoPrayer = false;
		duelNoWeapons = false;
		anIntArray782 = new int[50];
		duelConfirmOpponentItemsI = new Item[8];
		anIntArray786 = new int[50];
		anIntArray787 = new int[50];
		anIntArray788 = new int[50];
		objectModelArray = new Model[1500];
		cameraZRot = 128;
		cameraXRot = 912;
		showWelcomeBox = false;
		chrBodyGender = 1;
		character2Colour = 2;
		chrHairClr = 2;
		chrTopClr = 8;
		chrBottomClr = 14;
		chrHeadGender = 1;
		selectedBankItem = -1;
		selectedBankItemType = -2;
		aBooleanArray827 = new boolean[1500];
		playerStatBase = new int[18];
		shopItemsI = new Item[256];
		anIntArray858 = new int[50];
		anIntArray859 = new int[50];
		newBankItemsI = new Item[256];
		duelConfirmMyItemsI = new Item[8];
		mobArrayIndexes = new int[500];
		objectX = new int[1500];
		objectY = new int[1500];
		objectType = new int[1500];
		objectID = new int[1500];
		ourPlayer = new Mob();
		serverIndex = -1;
		showTradeConfirmWindow = false;
		tradeConfirmAccepted = false;
		playerArray = new Mob[500];
		serverMessageBoxTop = false;
		cameraHeight = 550 * EngineHandle.SCALE_FACTOR; // TODO
		cameraZoom = 1.0;
		bankItemsI = new Item[256];
		notInWilderness = false;
		selectedSpell = -1;
		anInt911 = 2;
		tradeOtherItemsI = new Item[14];
		zoomCamera = false;
		playerStatExperience = new int[18];
		cameraAutoAngleDebug = false;
		npcRecordArray = new Mob[8000];
		showDuelWindow = false;
		anIntArray923 = new int[50];
		lastLoadedNull = false;
		experienceArray = new int[99];
		showShop = false;
		mouseClickX = new int[8192];
		mouseClickYArray = new int[8192];
		showDuelConfirmWindow = false;
		duelWeAccept = false;
		doorX = new int[500];
		doorY = new int[500];
		configSoundEffects = false;
		showRightClickMenu = false;
		attackingInt40 = 40;
		anIntArray944 = new int[50];
		doorDirection = new int[500];
		doorType = new int[500];
		groundItemX = new int[8000];
		groundItemY = new int[8000];
		groundItemType = new int[8000];
		groundItemZ = new double[8000];
		selectedShopItemIndex = -1;
		selectedShopItemType = -2;
		showTradeWindow = false;
		aBooleanArray970 = new boolean[500];
		tradeMyItemsI = new Item[14];
		/*
        windowWidth = 512;
        windowHeight = 334;
		 */
		windowWidth = 1120;
		windowHeight = 630;
		windowHalfWidth = windowWidth/2;
		windowHalfHeight = windowHeight/2;
		cameraSizeInt = 9;
		tradeOtherPlayerName = "";
		chatBoxVisRows = 7;
		messagesArray = new String[chatBoxVisRows];
		messagesTimeout = new int[chatBoxVisRows];
		chatPlayerEntryHeight = 14;
		chatBoxHeight = chatPlayerEntryHeight*chatBoxVisRows;
		chatBoxWidth = 502;
		chatBoxX = 5;
		chatBoxY = windowHeight - chatBoxHeight - chatPlayerEntryHeight-10;
		chatPlayerEntryX = chatBoxX;
		chatPlayerEntryY = chatBoxY + chatBoxHeight;
		chatPlayerEntryWidth = chatBoxWidth - (chatPlayerEntryX - chatBoxX);
		gameWindowMenuBarWidth = 197;
		gameWindowMenuBarHeight = 32;
		gameWindowMenuBarX = windowWidth - gameWindowMenuBarWidth-3;
		gameWindowMenuBarY = windowHeight - gameWindowMenuBarHeight-3;
		gameWindowMenuBarItemWidth = 33;
		gameWindowMenuBarItemHeight = 32;
		miniMapWidth = 156+40;
		miniMapHeight = 152+40;
		miniMapX = windowWidth-miniMapWidth-3;
		miniMapY = 3;

	}

	private int bankItemCount;
	private Item bankItemsI[];
	private int newBankItemCount;
	private Item newBankItemsI[];
	
	private int duelMyItemCount;
	private Item duelMyItemsI[];
	private int duelConfirmMyItemCount;
	private Item duelConfirmMyItemsI[];
	private int duelOpponentItemCount;
	private Item duelOpponentItemsI[];
	private int duelConfirmOpponentItemCount;
	private Item duelConfirmOpponentItemsI[];

	protected int tradeMyItemCount;
	private Item tradeMyItemsI[];
	private int tradeConfirmItemCount;
	private Item tradeConfirmItemsI[];
	protected int tradeOtherItemCount;
	private Item tradeOtherItemsI[];
	private int tradeConfirmOtherItemCount;
	private Item tradeConfirmOtherItemsI[];

	protected int inventoryCount;
	protected Item inventory[];

	private int groundItemCount;

	private Item shopItemsI[];
	
	private MenuRightClick[] rightClickMenu;
	private int menuLength;
	
	private boolean combatWindow;
	private int lastLoggedInDays;
	private int subscriptionLeftDays;
	private boolean configAutoCameraAngle;
	private String questionMenuAnswer[];
	private int anInt658;
	private int handlePacketErrorCount;
	private Mob lastNpcArray[];
	private int loginButtonNewUser;
	private int loginButtonExistingUser;
	private String currentUser;
	private String currentPass;
	private int lastWalkTimeout;
	private boolean duelOpponentAccepted;
	private boolean duelMyAccepted;
	private String serverMessage;
	private String duelOpponentName;
	private int mouseOverBankPageText;
	private int playerCount;
	private int lastPlayerCount;
	private int fightCount;
	private int wearing[];
	private int mobMessageCount;
	String mobMsg[];
	private boolean showBank;
	private Model doorModel[];
	private int mobMsgX[];
	private int mobMsgY[];
	private int mobMsgWidth[];
	private int mobMsgHeight[];
	private Mob npcArray[];
	private int equipmentStatus[];
	private final int chrTopBottomClrs[] = {0xff0000, 0xff8000, 0xffe000, 0xa0e000, 57344, 32768, 41088, 45311, 33023, 12528, 0xe000e0, 0x303030, 0x604000, 0x805000, 0xffffff};
	private int loginScreenNumber;
	private int anInt699;
	private boolean prayerOn[];
	protected boolean tradeOtherAccepted;
	protected boolean tradeWeAccepted;
	private Mob mobArray[];
	private int npcCombatModelArray1[] = {0, 1, 2, 1, 0, 0, 0, 0};
	private int anIntArray705[];
	private int anIntArray706[];
	private int npcCount;
	private int lastNpcCount;
	private int wildX;
	private int wildY;
	private int wildYMultiplier;
	private int sectorHeight;
	private boolean memoryError;
	private int bankItemsMax;
	private int mouseOverMenu;
	private int walkModel[] = {0, 1, 2, 1};
	private boolean showQuestionMenu;
	private int anInt718;
	private int viewDistance;
	private double cameraZoom;
	public boolean loggedIn;
	private int cameraAutoAngle;
	private int cameraRotationBaseAddition;
	private Menu questMenu;
	int questMenuHandle;
	private Menu spellMenu;
	int spellMenuHandle;
	int menuMagicPrayersSelected;
	private double screenRotationX;
	private double screenRotationY;
	private int anInt727;
	private int showAbuseWindow;
	private int duelCantRetreat;
	private int duelUseMagic;
	private int duelUsePrayer;
	private int duelUseWeapons;
	private boolean showServerMessageBox;
	private boolean hasReceivedWelcomeBoxDetails;
	private String lastLoggedInAddress;
	private int loginTimer;
	private int playerStatCurrent[];
	private int areaX;
	private int areaY;
	private int wildYSubtract;
	private int anInt742;
	private int anInt743;
	private int anInt744;
	private int sectionXArray[];
	private int sectionYArray[];
	private int selectedItem;
	String selectedItemName;
	private int anIntArray757[];
	private boolean showCharacterLookScreen;
	private int npcCombatModelArray2[] = {0, 0, 0, 0, 0, 1, 2, 1};
	private Mob lastPlayerArray[];
	private int inputBoxType;
	private boolean appletMode;
	private int combatStyle;
	private Model gameDataModels[];
	private boolean configMouseButtons;
	private boolean duelNoRetreating;
	private boolean duelNoMagic;
	private boolean duelNoPrayer;
	private boolean duelNoWeapons;
	private int anIntArray782[];
	private int anIntArray786[];
	private int anIntArray787[];
	private int anIntArray788[];
	private int anInt789;
	private int anInt790;
	private int anInt791;
	private int anInt792;
	private Menu menuLogin;
	private final int chrHairClrs[] = {0xffc030, 0xffa040, 0x805030, 0x604020, 0x303030, 0xff6020, 0xff4000, 0xffffff, 0xff00, 0xffff};
	private Model objectModelArray[];
	private Menu menuWelcome;
	private int systemUpdate;
	private int cameraZRot;
	private int cameraXRot;
	private int logoutTimeout;
	private Menu gameMenu;
	int messagesHandleChatHist;
	int chatHandlePlayerEntry;
	int messagesHandleQuestHist;
	int messagesHandlePrivHist;
	int messagesTab;
	private boolean showWelcomeBox;
	private int chrHeadType;
	private int chrBodyGender;
	private int character2Colour;
	private int chrHairClr;
	private int chrTopClr;
	private int chrBottomClr;
	private int chrSkinClr;
	private int chrHeadGender;
	private int loginStatusText;
	private int loginUsernameTextBox;
	private int loginPasswordTextBox;
	private int loginOkButton;
	private int loginCancelButton;
	private int selectedBankItem;
	private int selectedBankItemType;
	int anInt826;
	private boolean aBooleanArray827[];
	private int playerStatBase[];
	private AbuseWindow abWin;
	private BankPanel bankPan;
	private InventoryPanel invPan;
	private PlayerInfoPanel plrPan;
	private MagicPanel magicPan;
	private FriendsPanel friendPan;
	private OptionsPanel optPan;
	private TradePanel tradePan;
	private TradeConfirmPanel tradeCfrmPan;
	private int abuseSelectedType;
	private int actionPictureType;
	int actionPictureX;
	int actionPictureY;
	private int npcAnimationArray[][] = {
			{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
			{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3, 4},
			{11, 3, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4},
			{3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
			{3, 4, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
			{4, 3, 2, 9, 7, 1, 6, 10, 8, 11, 0, 5},
			{11, 4, 2, 9, 7, 1, 6, 10, 0, 5, 8, 3},
			{11, 2, 9, 7, 1, 6, 10, 0, 5, 8, 4, 3}
	};
	private int chrDesignHeadBtnLeft;
	private int chrDesignHeadBtnRight;
	private int chrDesignHairClrBtnLeft;
	private int chrDesignHairClrBtnRight;
	private int chrDesignGenderBtnLeft;
	private int chrDesignGenderBtnRight;
	private int chrDesignTopClrBtnLeft;
	private int chrDesignTopClrBtnRight;
	private int chrDesignSkinClrBtnLeft;
	private int chrDesignSkinClrBtnRight;
	private int chrDesignBottomClrBtnLeft;
	private int chrDesignBottomClrBtnRight;
	private int characterDesignAcceptButton;
	private int anIntArray858[];
	private int anIntArray859[];
	private int mobArrayIndexes[];
	private Menu menuNewUser;
	private double lastAutoCameraRotatePlayerX;
	private double lastAutoCameraRotatePlayerY;
	private int questionMenuCount;
	private int objectX[];
	private int objectY[];
	private int objectType[];
	private int objectID[];
	private Mob ourPlayer;
	int sectionX;
	int sectionY;
	int serverIndex;
	protected int mouseDownTime;
	protected int itemIncrement;
	private int modelFireLightningSpellNumber;
	private int modelTorchNumber;
	private int modelClawSpellNumber;
	private boolean showTradeConfirmWindow;
	private boolean tradeConfirmAccepted;
	private int anInt892;
	private EngineHandle engineHandle;
	private Mob playerArray[];
	private boolean serverMessageBoxTop;
	private int referId;
	private int anInt900;
	private int newUserOkButton;
	protected int mouseButtonClick;
	private double cameraHeight;
	private boolean notInWilderness;
	private int selectedSpell;
	private int anInt911;
	private boolean zoomCamera;
	private AudioReader audioReader;
	private int playerStatExperience[];
	private boolean cameraAutoAngleDebug;
	private Mob npcRecordArray[];
	private boolean showDuelWindow;
	private int anIntArray923[];
	protected GameImageMiddleMan gameGraphics;
	private final String skillArrayLong[] = {"Attack", "Defense", "Strength", "Hits", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblaw", "Agility", "Thieving"};
	private boolean lastLoadedNull;
	private int experienceArray[];
	private Camera gameCamera;
	private boolean showShop;
	private int mouseClickOffset;
	int mouseClickX[];
	int mouseClickYArray[];
	private boolean showDuelConfirmWindow;
	private boolean duelWeAccept;
	private Graphics aGraphics936;
	private int doorX[];
	private int doorY[];
	private int wildernessType;
	private boolean configSoundEffects;
	private boolean showRightClickMenu;
	private int screenRotationTimer;
	private int attackingInt40;
	private int anIntArray944[];
	private Menu chrDesignMenu;
	private int shopItemSellPriceModifier;
	private int shopItemBuyPriceModifier;
	private int modelUpdatingTimer;
	private int doorCount;
	private int doorDirection[];
	private int doorType[];
	private int anInt952;
	private int anInt953;
	private int anInt954;
	private int anInt955;
	private int groundItemX[];
	private int groundItemY[];
	private int groundItemType[];
	private double groundItemZ[];
	private int selectedShopItemIndex;
	private int selectedShopItemType;
	private int messagesRows;
	private String messagesArray[];
	private int messagesTimeout[];
	private long tradeConfirmOtherNameLong;
	protected boolean showTradeWindow;
	private int playerAliveTimeout;
	private final int chrSkinClrs[] = {0xecded0, 0xccb366, 0xb38c40, 0x997326, 0x906020};
	private byte sounds[];
	private boolean aBooleanArray970[];
	private int objectCount;
	public int windowWidth;
	public int windowHeight;
	public int windowHalfWidth;
	public int windowHalfHeight;
	private int cameraSizeInt;
	private Menu friendsMenu;
	int friendsMenuHandle;
	int friendTabOn;
	long privateMessageTarget;
	private long duelOpponentNameLong;
	protected String tradeOtherPlayerName;
	private int anInt985;
	private int anInt986;
	public int chatBoxX, chatBoxY, chatBoxWidth, chatBoxHeight, chatBoxVisRows,
	chatPlayerEntryX, chatPlayerEntryY, chatPlayerEntryWidth, chatPlayerEntryHeight;
	public static int SCROLL_BAR_WIDTH = 11;
	public static int SCROLL_BAR_HEIGHT = 12;
	public int gameWindowMenuBarX, gameWindowMenuBarY, gameWindowMenuBarWidth,
	gameWindowMenuBarHeight, gameWindowMenuBarItemWidth, gameWindowMenuBarItemHeight;
	public int miniMapX, miniMapY, miniMapWidth, miniMapHeight;
	public static String quests[] = new String[]{
			"Black Knights' Fortress", "Cook's Assistant", "Demon slayer",
			"Doric's quest", "The restless ghost", "Goblin diplomacy",
			"Ernes the chicken", "Imp catcher", "Pirate's treasure",
			"Romeo & Juliet", "Prince Ali rescue", "Sheep shearer",
			"Shield of Arrav", "The knight's sword", "Vampire slayer", "Wich's potion",
			"Dragon slayer", "Witch's house (members)", "Lost City (members)",
			"Hero's quest (members)", "Druidic ritual (members)", "Merlin's crystal(members)",
			"Scorpion catcher (members)", "Family crest (members)", "Tribal totem (members)",
			"Fishing contest (members)", "Monk's friend (members)", "Temple of Ikov(members)",
			"Clock tower (members)", "The Holy Grail (members)", "Fight Arena (members)",
			"Tree Gnome Village (members)", "The Hazel Cult (members)", "Sheep Herder (members)",
			"Plague City (members)", "Sea Slug (members)", "Waterfall quest (members)",
			"Biohazard (members)", "Jungle potion (members)", "Grand tree (members)",
			"Shilo village (members)", "Underground pass (members)", "Observatory quest (members)",
			"Tourist trap (members)", "Watchtower (members)", "Dwarf Cannon (members)",
			"Murder Mystery (members)", "Digsite (members)", "Gertrude's Cat (members)",
	"Legend's Quest (members)"};
	private boolean isTyping;
}
