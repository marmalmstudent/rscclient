package client;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import javax.swing.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import client.util.Config;
import client.util.DataConversions;
import dbdev.FileOperations;
import model.Sprite;

public class GameWindow extends JApplet 
	implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, WindowListener, ComponentListener
{
	public static final Color BAR_COLOUR = new Color(132, 132, 132); //144, 192, 64
	public static final Font LOADING_FONT = new Font("Helvetica", 0, 12);
	
	protected void startGame()
	{
		
	}
	
	protected synchronized void method2()
	{
		
	}
	
	protected void logoutAndStop()
	{
		
	}
	
	protected synchronized void method4()
	{
		
	}

	protected final void createWindow(mudclient mc, int width, int height, String title, boolean resizable)
	{
		appletMode = false;
		appletWidth = width;
		appletHeight = height;
		gameFrame = new GameFrame(this, width, height, title, resizable, true);
		loadingScreen = 1;
		gameWindowThread = new Thread(this);
		gameWindowThread.start();
		gameWindowThread.setPriority(1);
	}
	
	public void initCreateWindow(int width, int height)
	{
		appletMode = true;
		appletWidth = width;
		appletHeight = height;
		graphics = getFrameComponent().getGraphics();
		loadingScreen = 1;
		startThread(this);
	}
    
	public void setLogo(Image logo)
	{
		loadingLogo = logo; //null to not display background when loading
	}
	
	protected final void changeThreadSleepModifier(int i)
	{
		threadSleepModifier = 1000 / i;
	}
	
	protected final void resetCurrentTimeArray()
	{
		for (int i = 0; i < 10; i++) {
			currentTimeArray[i] = 0L;
		}
	}

	private final void close()
	{
		exitTimeout = -2;
		System.out.println("Closing program");
		logoutAndStop();
		try
		{
			Thread.sleep(1000L);
		} catch(Exception e)
		{
			
		}
		if (gameFrame != null)
			gameFrame.dispose();
		System.exit(0);
	}

	private void loadFonts()
	{
		GameImage.loadFont("Nimbus Sans", 13, Font.PLAIN, false, 0, this); //h11p //??
		GameImage.loadFont("Nimbus Sans", 12, Font.PLAIN/*Font.BOLD*/, false, 1, this); //h12b spells/stats/character creation/duel interface etc.
		GameImage.loadFont("Nimbus Sans", 14, Font.PLAIN, false, 2, this); //h12p player names in chat?
		GameImage.loadFont("Nimbus Sans", 15, Font.PLAIN/*Font.BOLD*/, false, 3, this); //h13b //chat/error text
		GameImage.loadFont("Nimbus Sans", 16, Font.PLAIN/*Font.BOLD*/, false, 4, this); //used for login screen text
		GameImage.loadFont("Nimbus Sans", 18, Font.PLAIN/*Font.BOLD*/, false, 5, this); //used for login screen button
		GameImage.loadFont("Nimbus Sans", 20, Font.PLAIN, false, 6, this); //h20b //right-clicking menu?
		GameImage.loadFont("Nimbus Sans", 24, Font.PLAIN/*Font.BOLD*/, false, 7, this); //h24b //used on display msg when you die
	}

    public Component getFrameComponent()
    {
        if(gameFrame != null)
            return gameFrame;
        else
            return this;
    }

    public final void run()
    {
    	getFrameComponent().addMouseListener(this);
    	getFrameComponent().addMouseMotionListener(this);
    	getFrameComponent().addMouseWheelListener(this);
    	getFrameComponent().addKeyListener(this);
    	getFrameComponent().addFocusListener(this);
        if(gameFrame != null)
        	gameFrame.addWindowListener(this);
        if (loadingScreen == 1)
        { //very first loginscreen
            loadingScreen = 2; // 2
            graphics = getGraphics();
            drawLoadingLogo();
            drawLoadingScreen(0, "Loading...");
            startGame();
            loadingScreen = 0; // removes the login screen
        }
        int i = 0;
        int j = 256;
        int sleepTime = 1;
        int i1 = 0;
        for (int j1 = 0; j1 < 10; j1++)
            currentTimeArray[j1] = System.currentTimeMillis();
        while (exitTimeout >= 0)
        {
            if (exitTimeout > 0)
            {
                exitTimeout--;
                if (exitTimeout == 0)
                {
                    close();
                    gameWindowThread = null;
                    return;
                }
            }
            int k1 = j;
            int i2 = sleepTime;
            j = 300;
            sleepTime = 1;
            long l1 = System.currentTimeMillis();
            if (currentTimeArray[i] == 0L)
            {
                j = k1;
                sleepTime = i2;
            }
            else if (l1 > currentTimeArray[i])
                j = (int) ((long) (2560 * threadSleepModifier) / (l1 - currentTimeArray[i]));
            if (j < 25)
                j = 25;
            if (j > 256)
            {
                j = 256;
                sleepTime = (int) ((long) threadSleepModifier - (l1 - currentTimeArray[i]) / 10L);
                if (sleepTime < threadSleepTime)
                    sleepTime = threadSleepTime;
            }
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException _ex)
            {
            	
            }
            currentTimeArray[i] = l1;
            i = (i + 1) % 10;
            if (sleepTime > 1)
            {
                for (int j2 = 0; j2 < 10; j2++)
                    if (currentTimeArray[j2] != 0L)
                        currentTimeArray[j2] += sleepTime;

            }
            int k2 = 0;
            while (i1 < 256)
            {
                method2();
                i1 += j;
                if (++k2 > anInt5)
                {
                    i1 = 0;
                    anInt10 += 6;
                    if (anInt10 > 25)
                    {
                        anInt10 = 0;
                        keyF1Toggle = true;
                    }
                    break;
                }
            }
            anInt10--;
            i1 &= 0xff;
            method4();
        }
        if (exitTimeout == -1)
            close();
        gameWindowThread = null;
    }

	private final void drawLoadingLogo()
	{
		graphics.setColor(Color.black);
		graphics.drawImage(loadingLogo, 5, 0, this);
		loadFonts();
	}

    private final void drawLoadingScreen(int i, String s)
    {
        try {
            int j = (appletWidth - 281) / 2;
            int k = (appletHeight - 148) / 2;
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, appletWidth, appletHeight);
            graphics.drawImage(loadingLogo, 5, 0, this);
            j += 2;
            k += 120;
            anInt16 = i;
            loadingBarText = s;
            graphics.setColor(BAR_COLOUR);
            graphics.drawRect(j - 2, k - 2, 280, 23);
            graphics.fillRect(j, k, (277 * i) / 100, 20);
            graphics.setColor(Color.black);
            drawString(graphics, s, LOADING_FONT, j + 138, k + 10);
            if (loadingString != null) {
                graphics.setColor(Color.WHITE);
                drawString(graphics, loadingString, LOADING_FONT, j + 138, k - 120);
                return;
            }
        }
        catch (Exception _ex)
        {
        	
        }
    }

    public final void drawLoadingBarText(int i, String s)
    {
        try
        {
            int j = (appletWidth - 281) / 2;
            int k = (appletHeight - 148) / 2;
            j += 2;
            k += 120;
            anInt16 = i;
            loadingBarText = s;
            int l = (277 * i) / 100;
            graphics.setColor(BAR_COLOUR);
            graphics.fillRect(j, k, l, 20);
            graphics.setColor(Color.black);
            graphics.fillRect(j + l, k, 277 - l, 20);
            graphics.setColor(Color.WHITE);
            drawString(graphics, s, LOADING_FONT, j + 138, k + 10);
            return;
        }
        catch (Exception _ex)
        {
            return;
        }
    }

	protected final void drawString(Graphics g, String s, Font font, int i, int j)
	{
		FontMetrics fontmetrics = (gameFrame == null ? this : gameFrame).getFontMetrics(font);
		fontmetrics.stringWidth(s);
		g.setFont(font);
		g.drawString(s, i - fontmetrics.stringWidth(s) / 2, j + fontmetrics.getHeight() / 4);
	}

    protected byte[] load(String filename)
    {
        int uncompressedSize = 0;
        int compressedSize = 0;
        byte compressedData[] = null;
        try
        {
            java.io.InputStream inputstream = DataOperations.streamFromPath(filename);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            byte header[] = new byte[6];
            datainputstream.readFully(header, 0, 6);
            uncompressedSize = ((header[0] & 0xff) << 16)
            		+ ((header[1] & 0xff) << 8)
            		+ (header[2] & 0xff);
            compressedSize = ((header[3] & 0xff) << 16)
            		+ ((header[4] & 0xff) << 8)
            		+ (header[5] & 0xff);
            int offset = 0;
            compressedData = new byte[compressedSize];
            while (offset < compressedSize)
            {
                int bufferSize = compressedSize - offset;
                if (bufferSize > 1000)
                {
                    bufferSize = 1000;
                }
                datainputstream.readFully(compressedData, offset, bufferSize);
                offset += bufferSize;
            }
            datainputstream.close();
        }
        catch (IOException _ex)
        {
        	_ex.printStackTrace();
        }
        if (compressedSize != uncompressedSize)
        {
            byte uncompressedData[] = new byte[uncompressedSize];
            DataFileDecrypter.unpackData(uncompressedData, uncompressedSize, compressedData, compressedSize, 0);
            return uncompressedData;
        }
        else
        {
            return compressedData;
        }
    }

 	protected void handleMenuKeyDown(int keyCode, int keyChar)
	{
		
	}

	protected void handleMouseDown(int button, int x, int y)
	{
		
	}

	protected void handleScrollEvent(int scrollDirection)
	{
		
	}
	/*
    public final synchronized boolean keyDown(Event event, int key)
    {
        handleMenuKeyDown(key);
        keyDown = key;
        keyDown2 = key;
        lastActionTimeout = 0;
        if (key == 1006)
            keyLeftDown = true;
        if (key == 1007)
            keyRightDown = true;
        if (key == 1004)
            keyUpDown = true;
        if (key == 1005)
            keyDownDown = true;
        if ((char) key == ' ')
            keySpaceDown = true;
        if ((char) key == 'n' || (char) key == 'm')
            keyNMDown = true;
        if ((char) key == 'N' || (char) key == 'M')
            keyNMDown = true;
        if ((char) key == '{')
            keyLeftBraceDown = true;
        if ((char) key == '}')
            keyRightBraceDown = true;
        if ((char) key == '\u03F0') // 1008 or F1
            keyF1Toggle = !keyF1Toggle;
        boolean validKeyDown = false;
        for (int j = 0; j < charSet.length(); j++)
        {
            if (key != charSet.charAt(j))
                continue;
            validKeyDown = true;
            break;
        }

        if (validKeyDown && inputText.length() < 20)
            inputText += (char) key;
        if (validKeyDown && inputMessage.length() < 80)
            inputMessage += (char) key;
        if (key == 8 && inputText.length() > 0) // backspace
            inputText = inputText.substring(0, inputText.length() - 1);
        if (key == 8 && inputMessage.length() > 0) // backspace
            inputMessage = inputMessage.substring(0, inputMessage.length() - 1);
        if (key == 10 || key == 13)
        { // enter/return
            enteredText = inputText;
            enteredMessage = inputMessage;
        }
        return true;
    }
    
    public final synchronized boolean keyUp(Event event, int i)
    {
        keyDown = 0;
        if (i == 1006)
            keyLeftDown = false;
        if (i == 1007)
            keyRightDown = false;
        if (i == 1004)
            keyUpDown = false;
        if (i == 1005)
            keyDownDown = false;
        if ((char) i == ' ')
            keySpaceDown = false;
        if ((char) i == 'n' || (char) i == 'm')
            keyNMDown = false;
        if ((char) i == 'N' || (char) i == 'M')
            keyNMDown = false;
        if ((char) i == '{')
            keyLeftBraceDown = false;
        if ((char) i == '}')
            keyRightBraceDown = false;
        return true;
    }

	public final synchronized boolean mouseMove(Event event, int i, int j)
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = 0;
		lastActionTimeout = 0;
		return true;
	}
	
	public final synchronized boolean mouseUp(Event event, int i, int j)
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = 0;
		return true;
	}
	
	public final synchronized boolean mouseDown(Event event, int i, int j)
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = event.metaDown() ? 2 : 1;
		lastMouseDownButton = mouseDownButton;
		lastActionTimeout = 0;
		handleMouseDown(mouseDownButton, i, j);
		return true;
	}

	public final synchronized boolean mouseDrag(Event event, int i, int j)
	{
		mouseX = i;
		mouseY = j + yOffset;
		mouseDownButton = event.metaDown() ? 2 : 1;
		return true;
	}*/

	public final void init()
	{
        Config.initConfig();
        GameWindowMiddleMan.clientVersion = 25;
        //setLogo(Toolkit.getDefaultToolkit().getImage(Config.CONF_DIR + File.separator + "Loading.rscd"));
        initCreateWindow(512, 344);
	}
	
	public final void start()
	{
		if (exitTimeout >= 0)
			exitTimeout = 0;
	}
	
	public final void stop()
	{
		if (exitTimeout >= 0)
			exitTimeout = 4000 / threadSleepModifier;
	}

	public final void destroy()
	{
		exitTimeout = -1;
		try
		{
			Thread.sleep(2000L);
		} catch (Exception e)
		{
			
		}
		if (exitTimeout == -1)
		{
			System.out.println("2 seconds expired, forcing kill");
			close();
			if (gameWindowThread != null)
			{
				gameWindowThread.stop();
				gameWindowThread = null;
			}
		}
	}

	public final void update(Graphics g)
	{
		paint(g);
	}

	public final void paint(Graphics g)
	{
		if (loadingScreen == 2 && loadingLogo != null)
		{
			drawLoadingScreen(anInt16, loadingBarText);
		}
	}
   
	public Graphics getGraphics()
	{
		if (gameFrame != null)
		{
			return gameFrame.getGraphics();
		}
		return super.getGraphics();
	}
	
	public Image createImage(int i, int j)
	{
		if (gameFrame != null)
		{
			return gameFrame.createImage(i, j);
		}
		return super.createImage(i, j);
	}

	protected Socket makeSocket(String address, int port) throws IOException
	{
		System.out.println(InetAddress.getByName(address)+", "+port);
		Socket socket = new Socket(InetAddress.getByName(address), port);
		socket.setSoTimeout(30000);
		socket.setTcpNoDelay(true);
		return socket;
	}

	protected void startThread(Runnable runnable)
	{
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		thread.start();
	}

	public GameWindow()
	{
		appletWidth = 512;
		appletHeight = 384;
		threadSleepModifier = 20;
		anInt5 = 1000;
		currentTimeArray = new long[10];
		loadingScreen = 1;
		loadingBarText = "Loading";
		keyLeftBraceDown = false;
		keyRightBraceDown = false;
		keyLeftDown = false;
		keyRightDown = false;
		keyUpDown = false;
		keyDownDown = false;
		keySpaceDown = false;
		keyNMDown = false;
		threadSleepTime = 1;
		keyF1Toggle = false;
		inputText = "";
		enteredText = "";
		inputMessage = "";
		enteredMessage = "";
	}
	
	private Image loadingLogo;
	private int appletWidth;
	private int appletHeight;
	private Thread gameWindowThread;
	private int threadSleepModifier;
	private int anInt5;
	private long currentTimeArray[];
	public static GameFrame gameFrame = null;
	private boolean appletMode;
	private int exitTimeout;
	private int anInt10;
	public int yOffset;
	public int lastActionTimeout;
	public int loadingScreen;
	public String loadingString;
	private int anInt16;
	private String loadingBarText;
	private Graphics graphics;
	private static String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	public boolean keyLeftBraceDown;
	public boolean keyRightBraceDown;
	public boolean keyLeftDown;
	public boolean keyRightDown;
	public boolean keyUpDown;
	public boolean keyDownDown;
	public boolean keySpaceDown;
	public boolean keyNMDown;
	public boolean[] keyDownCode = new boolean[127];
	public boolean[] keyDownChar = new boolean[0x10000];
	public int threadSleepTime;
	public int mouseX;
	public int mouseY;
	public int mouseDownButton;
	public int lastMouseDownButton;
	public int keyDown;
	public int keyDown2;
	public boolean keyF1Toggle;
	public String inputText;
	public String enteredText;
	public String inputMessage;
	public String enteredMessage;

	
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		int keyChar = e.getKeyChar();
        handleMenuKeyDown(keyCode, keyChar);
        if (keyCode >= 0 && keyCode < keyDownCode.length)
        	keyDownCode[keyCode] = true;
        if (keyChar >= 0 && keyChar < keyDownChar.length)
        	keyDownChar[keyChar] = true;
        keyDown = keyCode;
        keyDown2 = keyChar;
        lastActionTimeout = 0;
        if (keyCode == 37)
            keyLeftDown = true;
        if (keyCode == 39)
            keyRightDown = true;
        if (keyCode == 38)
            keyUpDown = true;
        if (keyCode == 40)
            keyDownDown = true;
        if ((char) keyCode == ' ') // 32
            keySpaceDown = true;
        if ((char) keyCode == 77 || (char) keyCode == 78) // m or n
            keyNMDown = true;
        if ((char) keyCode == 55) // { (123)
            keyLeftBraceDown = true;
        if ((char) keyCode == 48) // } (125)
            keyRightBraceDown = true;
        if ((char) keyCode == 112) //  F1
            keyF1Toggle = !keyF1Toggle;
        boolean validKeyDown = false;
        for (int j = 0; j < charSet.length(); j++)
        {
            if (keyChar != charSet.charAt(j)) {
                continue;
            }
            validKeyDown = true;
            break;
        }
        if (validKeyDown && inputText.length() < 20)
            inputText += (char) keyChar;
        if (validKeyDown && inputMessage.length() < 80)
            inputMessage += (char) keyChar;
        if (keyCode == 8 && inputText.length() > 0) // backspace
            inputText = inputText.substring(0, inputText.length() - 1);
        if (keyCode == 8 && inputMessage.length() > 0) // backspace
            inputMessage = inputMessage.substring(0, inputMessage.length() - 1);
        if (keyCode == 10 || keyCode == 13)
        { // enter/return
            enteredText = inputText;
            enteredMessage = inputMessage;
        }
		
	}

	public void keyReleased(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		int keyChar = e.getKeyChar();
        if (keyCode >= 0 && keyCode < keyDownCode.length)
        	keyDownCode[keyCode] = false;
        if (keyChar >= 0 && keyCode < keyDownChar.length)
        	keyDownChar[keyChar] = false;
        keyDown = 0;
        if (keyCode == 37)
            keyLeftDown = false;
        if (keyCode == 39)
            keyRightDown = false;
        if (keyCode == 38)
            keyUpDown = false;
        if (keyCode == 40)
            keyDownDown = false;
        if ((char) keyCode == ' ')
            keySpaceDown = false;
        if ((char) keyCode == 'n' || (char) keyCode == 'm')
            keyNMDown = false;
        if ((char) keyCode == 'N' || (char) keyCode == 'M')
            keyNMDown = false;
        if ((char) keyCode == '{')
            keyLeftBraceDown = false;
        if ((char) keyCode == '}')
            keyRightBraceDown = false;
	}

	public void mouseDragged(MouseEvent e)
	{
		mouseX = e.getX() - GameFrame.offsetY;
		mouseY = e.getY() - GameFrame.offsetY;
		mouseDownButton = e.isMetaDown() ? 2 : 1;
	}

	public void mouseMoved(MouseEvent e)
	{
		mouseX = e.getX() - GameFrame.offsetX;
		mouseY = e.getY() - GameFrame.offsetY;
		mouseDownButton = 0;
		lastActionTimeout = 0;
		
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int scrollDirection = e.getWheelRotation();
		handleScrollEvent(scrollDirection);
	}

	public void mousePressed(MouseEvent e)
	{
		mouseX = e.getX() - GameFrame.offsetX;
		mouseY = e.getY() - GameFrame.offsetY;
		mouseDownButton = e.isMetaDown() ? 2 : 1;
		lastMouseDownButton = mouseDownButton;
		lastActionTimeout = 0;
		handleMouseDown(mouseDownButton, mouseX, mouseX);
		
	}

	public void mouseReleased(MouseEvent e)
	{
		mouseX = e.getX() - GameFrame.offsetX;
		mouseY = e.getY() - GameFrame.offsetY;
		mouseDownButton = 0;
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// reset keys
		for (int i=0; i < keyDownCode.length; i++)
			keyDownCode[i] = false;
		for (int i=0; i < keyDownChar.length; i++)
			keyDownChar[i] = false;
		keyDown = 0;
		keyLeftDown = false;
		keyRightDown = false;
		keyUpDown = false;
		keyDownDown = false;
		keySpaceDown = false;
		keyNMDown = false;
		keyLeftBraceDown = false;
		keyRightBraceDown = false;
		// reset mouse
		mouseX = e.getX() - GameFrame.offsetX;
		mouseY = e.getY() - GameFrame.offsetY;
		mouseDownButton = 0;
	}

	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}