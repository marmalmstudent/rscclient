package client;

import java.awt.Rectangle;

import client.util.Config;

public class RSCClient
{
	public static void main(String[] args) throws Exception
	{
		Config.initConfig();
		GameWindowMiddleMan.clientVersion = 25;
		mudclient mc = new mudclient();
		Rectangle rect = mc.getVPBounds();
		mc.createWindow(mc, new Rectangle(rect.x, rect.y, rect.width + 2, rect.height + 37),
				"TestServer v" + GameWindowMiddleMan.clientVersion, false);
	}
}
