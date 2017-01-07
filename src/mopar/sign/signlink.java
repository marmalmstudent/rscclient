package mopar.sign;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class signlink implements Runnable {
	public static int clientversion = 317;
	public static int uid;
	public static int storeid = 32;
	public static RandomAccessFile cache_dat = null;
	public static RandomAccessFile[] cache_idx = new RandomAccessFile[5];
	public static boolean sunjava;
	public static Applet mainapp = null;
	public static boolean active;
	public static int threadliveid;
	public static InetAddress socketip;
	public static int socketreq;
	public static Socket socket = null;
	public static int threadreqpri = 1;
	public static Runnable threadreq = null;
	public static String dnsreq = null;
	public static String dns = null;
	public static String urlreq = null;
	public static DataInputStream urlstream = null;
	public static int savelen;
	public static String savereq = null;
	public static byte[] savebuf = null;
	public static boolean midiplay;
	public static int midipos;
	public static String midi = null;
	public static int midivol;
	public static int midifade;
	public static boolean waveplay;
	public static int wavepos;
	public static String wave = null;
	public static int wavevol;
	public static boolean reporterror = true;
	public static String errorname = "";

	public static void startpriv(InetAddress inetaddress) {
		threadliveid = (int)(Math.random() * 9.9999999E7D);
		if(active) {
			try {
				Thread.sleep(500L);
			} catch (Exception var4) {
				;
			}

			active = false;
		}

		socketreq = 0;
		threadreq = null;
		dnsreq = null;
		savereq = null;
		urlreq = null;
		socketip = inetaddress;
		Thread thread = new Thread(new signlink());
		thread.setDaemon(true);
		thread.start();

		while(!active) {
			try {
				Thread.sleep(50L);
			} catch (Exception var3) {
				;
			}
		}

	}

	public void run() {
		active = true;
		String s = findcachedir();
		uid = getuid(s);

		try {
			File i = new File(s + "main_file_cache.dat");
			if(i.exists() && i.length() > 52428800L) {
				i.delete();
			}

			cache_dat = new RandomAccessFile(s + "main_file_cache.dat", "rw");
			for(int _ex = 0; _ex < 5; ++_ex) {
				cache_idx[_ex] = new RandomAccessFile(s + "main_file_cache.idx" + _ex, "rw");
			}
		} catch (Exception var9) {
			var9.printStackTrace();
		}

		int var10 = threadliveid;

		while(threadliveid == var10) {
			if(socketreq != 0) {
				try {
					socket = new Socket(socketip, socketreq);
				} catch (Exception var4) {
					socket = null;
				}

				socketreq = 0;
			} else if(threadreq != null) {
				Thread var11 = new Thread(threadreq);
				var11.setDaemon(true);
				var11.start();
				var11.setPriority(threadreqpri);
				threadreq = null;
			} else if(dnsreq != null) {
				try {
					dns = InetAddress.getByName(dnsreq).getHostName();
				} catch (Exception var8) {
					dns = "unknown";
				}

				dnsreq = null;
			} else if(savereq != null) {
				if(savebuf != null) {
					try {
						FileOutputStream var12 = new FileOutputStream(s + savereq);
						var12.write(savebuf, 0, savelen);
						var12.close();
					} catch (Exception var7) {
						;
					}
				}

				if(waveplay) {
					wave = s + savereq;
					waveplay = false;
				}

				if(midiplay) {
					midi = s + savereq;
					midiplay = false;
				}

				savereq = null;
			} else if(urlreq != null) {
				try {
					System.out.println("urlstream");
					urlstream = new DataInputStream((new URL(mainapp.getCodeBase(), urlreq)).openStream());
				} catch (Exception var6) {
					urlstream = null;
				}

				urlreq = null;
			}

			try {
				Thread.sleep(50L);
			} catch (Exception var5) {
				;
			}
		}

	}
	
    public static String findcachedir()
    {
            try
            {
            	String s = "";
                String s1 = "src/mopar/Music";
                File file = new File(s1 + s);
                if(file.exists() || file.mkdir())
                    return s1 + s + "/";
            }
            catch(Exception _ex) { }

        return null;
    }
	/* Old version
	public static String findcachedir() {
		String[] as = {"c:/windows/", "c:/winnt/", "d:/windows/", "d:/winnt/", "e:/windows/", "e:/winnt/", "f:/windows/", "f:/winnt/", "c:/", "~/", "/tmp/", "", "c:/rscache", "/rscache"};
		if(storeid < 32 || storeid > 34) {
			storeid = 32;
		}

		String s = ".mpr_file_store_" + storeid;

		for(int i = 0; i < as.length; ++i) {
			try {
				String _ex = as[i];
				File file1;
				if(_ex.length() > 0) {
					file1 = new File(_ex);
					if(!file1.exists()) {
						continue;
					}
				}

				file1 = new File(_ex + s);
				if(file1.exists() || file1.mkdir()) {
					return _ex + s + "/";
				}
			} catch (Exception var5) {
				;
			}
		}

		return null;
	}
	*/
	
	public static int getuid(String s) {
		try {
			File _ex = new File(s + "uid.dat");
			if(!_ex.exists() || _ex.length() < 4L) {
				DataOutputStream i = new DataOutputStream(new FileOutputStream(s + "uid.dat"));
				i.writeInt((int)(Math.random() * 9.9999999E7D));
				i.close();
			}
		} catch (Exception var4) {
			;
		}

		try {
			DataInputStream _ex1 = new DataInputStream(new FileInputStream(s + "uid.dat"));
			int i1 = _ex1.readInt();
			_ex1.close();
			return i1 + 1;
		} catch (Exception var3) {
			return 0;
		}
	}

	public static synchronized Socket opensocket(int i) throws IOException {
		socketreq = i;

		while(socketreq != 0) {
			try {
				Thread.sleep(50L);
			} catch (Exception var2) {
				;
			}
		}

		if(socket == null) {
			throw new IOException("could not open socket");
		} else {
			return socket;
		}
	}

	public static synchronized DataInputStream openurl(String s) throws IOException {
		urlreq = s;

		while(urlreq != null) {
			try {
				Thread.sleep(50L);
			} catch (Exception var2) {
				;
			}
		}

		if(urlstream == null) {
			throw new IOException("could not open: " + s);
		} else {
			return urlstream;
		}
	}

	public static synchronized void dnslookup(String s) {
		dns = s;
		dnsreq = s;
	}

	public static synchronized void startthread(Runnable runnable, int i) {
		threadreqpri = i;
		threadreq = runnable;
	}

	public static synchronized boolean wavesave(byte[] abyte0, int i) {
		if(i > 2000000) {
			return false;
		} else if(savereq != null) {
			return false;
		} else {
			wavepos = (wavepos + 1) % 5;
			savelen = i;
			savebuf = abyte0;
			waveplay = true;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static synchronized boolean wavereplay() {
		if(savereq != null) {
			return false;
		} else {
			savebuf = null;
			waveplay = true;
			savereq = "sound" + wavepos + ".wav";
			return true;
		}
	}

	public static synchronized void midisave(byte[] abyte0, int i) {
		if(i <= 2000000) {
			if(savereq == null) {
				midipos = (midipos + 1) % 5;
				savelen = i;
				savebuf = abyte0;
				midiplay = true;
				savereq = "jingle" + midipos + ".mid";
			}
		}
	}

	public static void reporterror(String s) {
		System.out.println("Error: " + s);
	}
}
