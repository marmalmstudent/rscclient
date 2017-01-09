package mopar;
import java.awt.*;
import java.awt.event.*;
import java.awt.KeyboardFocusManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.swing.*;
import mopar.sign.signlink;
import org.*;

public class Bot extends JPanel implements ActionListener, ItemListener, MouseListener,
MouseMotionListener, KeyListener, FocusListener, WindowListener, ComponentListener, KeyEventDispatcher
{
	public JFrame frame;
	public int altInterfaceID;
	private static String newsKey = "ih87h9889h";
	private static String killcode = "56g56536bg";
	public JScrollPane scroll;
	public JTextArea console;
	public String highmem = "1";
	private boolean isHidden = false;
	private static boolean isApplet = false;
	private MP3Player player;
	public int midiCount = 0;
	public Sequencer sequencer;
	public String configFile;
	public static Bot bot;
	public JPanel mainFrame;
	public String server;
	public boolean pause;
	public long forceLogout;
	public long forceLogoutStart;
	private String lastUser;
	private String lastPass;
	public static int failCount;
	public int[] actionJack;
	private static File logDir;
	private static FileOutputStream logFileOut;
	private String frameTitle;
	private String[] badLoginStrings;
	private static boolean debugActions;
	private org.GameWindow gw;
	private org.mudclient mc;
	public Graphics graphics;

	public static void main(String[] args) {
		//bot = new Bot(args);
	}
    public Graphics getGraphics() {
        Graphics g = super.getGraphics();
        return g;
    }
    public final void paint(Graphics g) {
        gw.paint(g);
    }
    
    public final void update(Graphics g) {
    	gw.update(g);
    }

	public Bot(GameWindow gw, mudclient mc, int width, int height, String title,
			boolean resizable, boolean flag1)
	{
        frame = new JFrame("RSC reborn");
		frame.setSize(765, 513);
        frame.setVisible(true);
        /*
		setFocusable(true);
		setPreferredSize(new Dimension(765, 513));
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
        frame.add(this, BorderLayout.CENTER);
        */
        /*
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(100, 100));
        p.setForeground(Color.BLACK);
        p.setBackground(Color.WHITE);
        p.setFocusable(true);
        p.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                System.out.println("Key pressed in Panel");
            }
            public void keyReleased(KeyEvent e) {
                System.out.println("Key released in Panel");
            }
            public void keyTyped(KeyEvent e) {
                System.out.println("Key  typed in Panel");
            }
        });*/
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                System.out.println("Key pressed in Frame");
            }
            public void keyReleased(KeyEvent e) {
                System.out.println("Key released in Frame");
            }
            public void keyTyped(KeyEvent e) {
                System.out.println("Key  typed in Frame");
            }
        });
		this.gw = gw;
		this.mc = mc;
	    //frame.setUndecorated(true);
	    //frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		pause = false;
		forceLogout = -1L;
		forceLogoutStart = -1L;
		frameTitle = "RSC reborn";
		badLoginStrings = new String[]{"disabled", "updated", "members"};
		setup(true);
		graphics = getGraphics();
	}

	private void setup(boolean startMusic)
	{
		try {
			System.setErr(new PrintStream(new FileOutputStream(signlink.findcachedir() + "Mopar_error.log")));
		} catch (FileNotFoundException var4) {
			;
		}
		console = new JTextArea(10, 30);
		AccessorMethods.rs = this;
		((mudclient)gw).graphicsEnabled = true;
		setServer("127.0.0.1");
		initUI();
		log("Starting RSC reborn");
		if(startMusic) {
			this.midiPlayer("Play");
		}

		//getNews();
		log("Server : " + this.server);
	}

	public static boolean isApplet() {
		return isApplet;
	}

	private void midiPlayer(String command) {
		String dir = "src/mopar/Music/";
		this.chkDir(dir);
		if(this.player != null && this.player.isRunning()) {
			this.player.close();
		}

		if(this.sequencer != null && this.sequencer.isRunning()) {
			this.sequencer.stop();
			this.sequencer.close();
		}

		if(command.equals("Next")) {
			++this.midiCount;
		} else if(command.equals("Previous")) {
			--this.midiCount;
		}

		if(!command.equals("Stop")) {
			File index = new File(dir);
			String[] fileList = index.list();
			if(fileList.length > 0) {
				if(this.midiCount > fileList.length - 1) {
					this.midiCount = 0;
				}

				if(this.midiCount < 0) {
					this.midiCount = fileList.length - 1;
				}

				if(fileList[this.midiCount].endsWith(".mp3")) {
					log("Playing " + fileList[this.midiCount]);
					this.player = new MP3Player(dir + fileList[this.midiCount], true);
				} else if(fileList[this.midiCount].endsWith(".mid")) {
					log("Playing " + fileList[this.midiCount]);

					try {
						File ioe = new File(dir + fileList[this.midiCount]);
						this.sequencer = MidiSystem.getSequencer();
						this.sequencer.setSequence(MidiSystem.getSequence(ioe));
						this.sequencer.setLoopCount(-1);
						this.sequencer.open();
						this.sequencer.start();
					} catch (MidiUnavailableException var6) {
						log("Midi device unavailable!");
					} catch (InvalidMidiDataException var7) {
						log("Invalid Midi data!");
					} catch (IOException var8) {
						log("I/O Error!");
					}
				}
			} else {
				log("no midi or mp3 files in folder");
			}
		}

	}

	public void chkDir(String fileName) {
		try {
			File e = new File(fileName);
			if(!e.exists()) {
				e.mkdir();
			}
		} catch (Exception var3) {
			log("Error making file: " + var3);
		}

	}

	public void takeScreenShot() {
		try {
			Point c = gw.getLocationOnScreen();
			int x = c.x;
			int y = c.y;
			int width = gw.getWidth();
			int height = gw.getHeight();
			Rectangle screenRect = new Rectangle(x, y, width, height);
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screenRect);
			this.chkDir("./Screenshots");
			String screenySuffix = "Screenshot" + this.countScreenys() + ".png";
			File file = new File("./Screenshots/", screenySuffix);
			ImageIO.write(image, "png", file);
			log("Screenshot saved as " + screenySuffix);
		} catch (Exception var12) {
			log("Error saving screenshot: " + var12);
		}

	}

	public int countScreenys() {
		int l = 0;
		File file = new File("./Screenshots/");
		String[] as = file.list();

		for(int i1 = 0; i1 < as.length; ++i1) {
			if(as[i1].endsWith(".png")) {
				++l;
			}
		}

		return l;
	}

	public void itemStateChanged(java.awt.event.ItemEvent evt) {
		String cmd = evt.paramString();
		cmd = cmd.substring(cmd.indexOf("item=") + 5);
		int stateChange = evt.getStateChange();
		if(cmd != null) {
			if(cmd.startsWith("HP")) {
				((mudclient)gw).showhp = stateChange == 1;
				if(((mudclient)gw).showhp) {
					log("HP Above Heads On...");
				} else {
					log("HP Above Heads Off...");
				}
			}

			if(cmd.startsWith("Pking")) {
				((mudclient)gw).pkingmap = stateChange == 1;
				if(((mudclient)gw).pkingmap) {
					log("Pking Minimap On...");
				} else {
					log("Pking Minimap Off...");
				}
			}

			if(cmd.startsWith("Zoom")) {
				((mudclient)gw).cameratoggle = stateChange == 1;
				if(((mudclient)gw).cameratoggle) {
					log("Zoom/Camera Movement On...");
				} else {
					log("Zoom/Camera Movement Off...");
				}
			}
		}

	}

	private void initUI() {
		try {
			JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			signlink.mainapp = gw;
			if(!isApplet) {
				frame.setResizable(false);
				frame.setDefaultCloseOperation(3);
			}
			/*
			JPanel e = new JPanel();
			e.setLayout(new BorderLayout());
			e.add(gw);
			e.setPreferredSize(new Dimension(765, 503));
			*/
			String[] titles = new String[]{"File", "Console", "Music Player", "Maplock", "Aryan", "Help"};
			String[][] content = new String[][]{{"New Server", "New Port", "Ping Server", "Quit"}, {"Hide", "Show"}, {"Next Song", "Previous Song", "-", "Play", "Stop", "-", "Info"}, {"North", "South", "East", "West", "-", "Unlock"}, {"Run", "Pause/Resume Scripts", "List Scripts", "Stop Script", "Stop All Scripts", "-", "Reload Scripts", "Reload Randoms"}, {"Website", "Forums", "Server Status", "Key List", "Command List", "-", "Item IDs", "NPC IDs", "Object IDs"}};
			JMenuBar menuBar = this.makeMenuBar(titles, content);
			String[] checkBoxes = new String[]{"HP Above Heads", "Zoom/Camera Movement"};
			String[] cons = checkBoxes;
			int len$ = checkBoxes.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				String name = cons[i$];
				Checkbox menuItem = new Checkbox(name);
				menuItem.addItemListener(this);
				menuBar.add(menuItem);
			}
			this.scroll = new JScrollPane(this.console, 22, 31);
			this.scroll.setPreferredSize(new Dimension(765, 150));
			this.console.setForeground(Color.white);
			this.console.setBackground(Color.black);
			Console var12 = new Console(this.console, this.scroll);
			System.setOut(var12);
			frame.add(this);
			frame.add(menuBar, BorderLayout.NORTH);
			frame.add(this.scroll, BorderLayout.SOUTH);
			if(!isApplet) {
				//frame.pack();
				frame.setVisible(true);
			}
		} catch (Exception var11) {
			log("A general exception occured in initUI()");
			var11.printStackTrace();
		}

	}

	private JMenuBar makeMenuBar(String[] menuTitles, String[][] menuContent) {
		JMenuBar menuBar = new JMenuBar();

		for(int x = 0; x < menuTitles.length; ++x) {
			JMenu fileMenu = new JMenu(menuTitles[x]);
			String[] arr$ = menuContent[x];
			int len$ = arr$.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				String name = arr$[i$];
				JMenuItem menuItem = new JMenuItem(name);
				if(name.equalsIgnoreCase("-")) {
					fileMenu.addSeparator();
				} else {
					menuItem.addActionListener(this);
					fileMenu.add(menuItem);
				}
			}

			menuBar.add(fileMenu);
		}

		return menuBar;
	}

	public void actionPerformed(ActionEvent evt)
	{
		String cmd = evt.getActionCommand();
		if(cmd != null) {
			if(cmd.equalsIgnoreCase("Hide")) {
				if(this.isHidden) {
					log("The console is already hidden");
				} else {
					frame.remove(this.scroll);
					if(!isApplet) {
						frame.pack();
					}

					this.isHidden = true;
				}
			}

			if(cmd.equalsIgnoreCase("Show")) {
				if(!this.isHidden) {
					log("The console is already Visible");
				} else {
					frame.add(this.scroll, "South");
					if(!isApplet) {
						frame.pack();
					}

					this.isHidden = false;
				}
			}

			if(cmd.equalsIgnoreCase("Next Song")) {
				this.midiPlayer("Next");
			}

			if(cmd.equalsIgnoreCase("Previous Song")) {
				this.midiPlayer("Previous");
			}

			if(cmd.equalsIgnoreCase("Play")) {
				this.midiPlayer("Play");
			}

			if(cmd.equalsIgnoreCase("Stop")) {
				this.midiPlayer("Stop");
			}

			if(cmd.equalsIgnoreCase("North")) {
				((mudclient)gw).maplock = true;
				((mudclient)gw).mapface = 0;
			} else if(cmd.equalsIgnoreCase("South")) {
				((mudclient)gw).maplock = true;
				((mudclient)gw).mapface = 1030;
			} else if(cmd.equalsIgnoreCase("East")) {
				((mudclient)gw).maplock = true;
				((mudclient)gw).mapface = 1545;
			} else if(cmd.equalsIgnoreCase("West")) {
				((mudclient)gw).maplock = true;
				((mudclient)gw).mapface = 515;
			} else if(cmd.equalsIgnoreCase("Unlock")) {
				((mudclient)gw).maplock = false;
			}

			if(cmd.equalsIgnoreCase("Quit")) {
				log("Exiting...");
				System.exit(0);
			}
			
			if(cmd.equalsIgnoreCase("New Server")) {
				this.displayWorldSelect();
			}

			if(cmd.equalsIgnoreCase("New Port")) {
				mudclient.portNumber = Integer.parseInt(JOptionPane.showInputDialog(this, "New Port #:"));
				log("Port changed to " + mudclient.portNumber);
			}

			if(cmd.equalsIgnoreCase("Ping Server")) {
				AccessorMethods.ping(JOptionPane.showInputDialog(this, "IP to ping:"));
			}

			if(cmd.equals("Item IDs")) {
				if(isApplet) {
					this.checkIDs();
					new XmlConverter(signlink.findcachedir() + "all_IDs/Items.xml");
				} else {
					new XmlConverter("./all_IDs/Items.xml");
				}
			}

			if(cmd.equals("NPC IDs")) {
				if(isApplet) {
					this.checkIDs();
					new XmlConverter(signlink.findcachedir() + "all_IDs/Npcs.xml");
				} else {
					new XmlConverter("./all_IDs/Npcs.xml");
				}
			}

			if(cmd.equals("Object IDs")) {
				if(isApplet) {
					this.checkIDs();
					new XmlConverter(signlink.findcachedir() + "all_IDs/Objects.xml");
				} else {
					new XmlConverter("./all_IDs/Objects.xml");
				}
			}

			if(cmd.equalsIgnoreCase("Key List")) {
				JOptionPane.showMessageDialog(gw, "F1 = Server Select.\nF2 = Get Interface ID.\nF3 = Print Inventory Contents.\nF4 = Get Position / Toggle coords.\nF5 = Get Animation.\nF6 = Action debugging.\nF7 = Toggle Graphics.\nF8 = Toggle Camera Lock.\nF9 = Take Screenshot.\nF10 = Show HP.\nF11 = Next Song.\nF12 = Previous Song.\n\nThe following move the camera when toggled.\n   Insert = Zoom out.\n   Page Up = Zoom in.\n   Home = Forward.\n   End = Backward.\n   Delete = Left.\n   Page Down = Right.\n", "MoparScape Key List", -1);
			}

			if(cmd.equalsIgnoreCase("Command List")) {
				JOptionPane.showMessageDialog(gw, "Clientside commands(works with every server):\n     ::clientdrop = Disconnects you quickly.\n     ::lag = Lags your client.\n     ::fpson = Turns on frames per second.\n     ::fpsoff = Turns off above.\n     ::coordson = Shows your current world position.\n     ::coordsoff = Turns off above.\n     ::noclip = walk through walls.\n  For whats below FROM and TO are id numbers.\n     ::setitem FROM TO = Transforms one item to another.\n     ::setnpc FROM TO = Transforms one npc to another.\n     ::setobject FROM TO = Transforms one object to another.\n     ::resetall = Resets all transforms.\n\nPopular Serverside Commands(most servers):\n     ::tele COORD1 COORD2 = Teleports you to new coordinates.\n     ::pickup itemid amount = Spawns requested item(s).\n     ::pass newpassword = Changes your password.\n     ::bank = Opens up your bank from anywhere.", "MoparScape Command List", -1);
			}

			if(cmd.equalsIgnoreCase("Info")) {
				JOptionPane.showMessageDialog(gw, "This Music player can play both midi (.mid) or mp3 formats.\nJust put any songs in the \"Music\" folder to play them.", "MoparScape Music Player Info", -1);
			}
		}

	}

	private void checkIDs() {
		File exist = new File(signlink.findcachedir() + "all_IDs/");
		if(!exist.exists()) {
			new Update("http://www.moparscape.org/cache/all_IDs.zip", "all_IDs.zip", signlink.findcachedir(), true, false);
		}

	}

	public URL getCodeBase() {
		try {
			return new URL("http://" + this.server + ":80");
		} catch (Exception var2) {
			log("An exception occured in getCodeBase(): " + var2);
			return gw.getCodeBase();
		}
	}

	public URL getDocumentBase() {
		return this.getCodeBase();
	}

	public void loadError(String s) {
		log("Load error: " + s);
	}

	public String getParameter(String key) {
		return key.equals("nodeid")?"0":(key.equals("portoff")?"0":(key.equals("lowmem")?this.highmem:(key.equals("free")?"0":"")));
	}

	public void displayWorldSelect() {
		try {
			String e = JOptionPane.showInputDialog(this, "New IP:");
			if(e == null) {
				log("Sorry, have to have some input");
			} else if(e.equalsIgnoreCase("")) {
				log("Sorry, have to have some input");
			} else {
				log("Set world to: " + e);
				this.server = e;
			}
		} catch (Exception var2) {
			log("You must enter a numeric value!: " + var2);
		}

	}
	/*
	public void keyPressed(KeyEvent evt) {
		System.out.println("Key pressed!");
		int i = evt.getKeyCode();
		if(i == 113) {
			//log("Interface ID: " + am.getInterface());
		} else {
			if(i == 114) {*//*
				for(int t = 0; t < am.getInventory().length; ++t) {
					if(am.getInventory()[t] != -1) {
						log("Inventory #" + t + " = " + am.getInventory()[t]);
					}
				}
			} else if(i == 115) {
				mudclient.coords = !mudclient.coords;
				Tile var4 = am.getPosition(am.myPlayer());
				log("Your position: new Tile(" + var4 + ")");
			} else if(i == 116) {
				log("Your animation (" + am.myPlayer().anim + ")");*//*
			} else if(i == 117) {
				debugActions = !debugActions;
				log("Action debugging set to " + (debugActions?"on":"off"));
			} else if(i == 118) {
				this.graphicsEnabled = !this.graphicsEnabled;
				log("Graphics are now " + (this.graphicsEnabled?"enabled":"disabled"));
			} else if(i == 112) {
				this.displayWorldSelect();
			} else if(i == 119) {
				mudclient.cameratoggle = !mudclient.cameratoggle;
			} else if(i == 155) {
				mudclient.zoom += 15;
			} else if(i == 33) {
				mudclient.zoom -= 15;
			} else if(i == 36) {
				mudclient.fwdbwd -= 15;
			} else if(i == 35) {
				mudclient.fwdbwd += 15;
			} else if(i == 34) {
				mudclient.lftrit -= 15;
			} else if(i == 127) {
				mudclient.lftrit += 15;
			} else if(i == 120) {
				this.takeScreenShot();
			} else if(i == 121) {
				mudclient.showhp = !mudclient.showhp;
			} else if(i == 122) {
				this.midiPlayer("Next");
			} else if(i == 123) {
				this.midiPlayer("Previous");
			}

			//super.keyPressed(evt);
		}
	}*/

	public void focusGained(FocusEvent evt) {
		if(evt.getID() == 31337) {
			//super.focusGained(new FocusEvent(this, 0));
		}

	}

	public void focusLost(FocusEvent evt) {
		if(evt.getID() == 31337) {
			//super.focusLost(new FocusEvent(this, 0));
		}
	}

	public void login(String username, String password, boolean lagged) {
		if(username != null && password != null) {
			if(!username.equals("") && !password.equals("")) {
				if(username.trim().length() > 1) {
					this.setUser(username, password);
				}

				username = this.getUser();
				password = this.getPass();
				if(logDir != null && !logDir.exists() && !logDir.mkdir()) {
					log("Couldn\'t create directory " + logDir.getName());
					logDir = null;
				}

				if(logDir != null && logFileOut == null) {
					File logFile = new File(logDir, username + "-" + (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + ".log");

					try {
						logFileOut = new FileOutputStream(logFile, true);
					} catch (Exception var6) {
						log("Couldn\'t open output stream to log file " + logFile.getName());
					}
				}

				log("");
				log("Logging into (" + username + ", *****)");
				signlink.uid = (int) (10.0*Math.random());
				if(signlink.uid < 0) {
					signlink.uid = -signlink.uid;
				}

				log("Your UID is: " + signlink.uid);
				signlink.mainapp = null;
				((mudclient)gw).login(username, password, lagged);
				signlink.mainapp = gw;
				signlink.reporterror = false;
				if(!isApplet) {
					frame.setTitle((((mudclient)gw).loggedIn?this.getUser() + " - World #" + this.server + " - ":"") + this.frameTitle);
				}

			}
		}
	}

	public void login() {
		this.login(this.lastUser, this.lastPass, false);
	}

	public void setUser(String username, String password) {
		this.lastUser = username;
		this.lastPass = password;
	}

	public String getUser() {
		return this.lastUser;
	}

	private String getPass() {
		return this.lastPass;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getServer() {
		return this.server;
	}

	public void doAction(int i, boolean flag) {
		if(this.actionJack != null && this.actionJack.length == 4) {
			((mudclient)gw).menuActionID[i] = this.actionJack[0];
			((mudclient)gw).menuActionCmd1[i] = this.actionJack[1];
			((mudclient)gw).menuActionCmd2[i] = this.actionJack[2];
			((mudclient)gw).menuActionCmd3[i] = this.actionJack[3];
			this.actionJack = null;
		}

		if(debugActions) {
			int a = ((mudclient)gw).menuActionID[i];
			int b = ((mudclient)gw).menuActionCmd1[i];
			int c = ((mudclient)gw).menuActionCmd2[i];
			int d = ((mudclient)gw).menuActionCmd3[i];
			log(a + "=" + b + "," + c + "," + d);
		}

		//super.doAction(i, flag);
	}

	public static synchronized void log(String message) {
		message = (new SimpleDateFormat("[hh:mm:ss]")).format(new Date()) + " " + message;
		if(logFileOut != null) {
			try {
				logFileOut.write((message + "\n").getBytes());
			} catch (Exception var2) {
				log("Error writing to logfile");
				var2.printStackTrace();
			}
		}

		System.out.println(message);
	}

	public static void getNews() {
		try {
			URL e = new URL("http://www.moparscape.org/news/news.php");
			URLConnection newsConnect = e.openConnection();
			newsConnect.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(newsConnect.getInputStream()));
			String news = "";
			boolean foundit = false;

			String buffer;
			while((buffer = reader.readLine()) != null) {
				if(buffer.contains(newsKey)) {
					foundit = !foundit;
				} else if(foundit) {
					news = news + buffer + "\n";
				}
			}

			String updateString = "98y9unh98ybhn9";
			if(news.contains(updateString)) {
				String newsarray = news.substring(news.indexOf(updateString) + updateString.length(), news.lastIndexOf(updateString));
				news = news.replaceAll(updateString + newsarray + updateString, "");
				new Update(newsarray, "MoparScape.jar", ".");
			}

			String[] var13 = news.replaceAll(killcode, "").split("\n");
			log("");
			String[] arr$ = var13;
			int len$ = var13.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				String newsLine = arr$[i$];
				log("News: " + newsLine);
			}

			if(news.contains(killcode)) {
				System.exit(-1);
			}
		} catch (Exception var12) {
			log("Error downloading news");
		}

	}

	public void mapReset() {
	}
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Component event!");
	}
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Component event!");
	}
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Component event!");
	}
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Component event!");
	}
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Window event!");
	}
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key typed!");
	}
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key pressed!");
	}
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Key released!");
	}
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse dragged!");
	}
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse moved!");
	}
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse clicked!");
	}
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse pressed!");
	}
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse released!");
	}
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse entered!");
	}
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Mouse exited!");
	}
	public boolean dispatchKeyEvent(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Hierarchy changed");
		return false;
	}
}
