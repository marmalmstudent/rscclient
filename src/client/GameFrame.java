package client;

import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5653713738612659906L;
	public GameFrame(GameWindow gameWindow, int width, int height, String title, boolean resizable, boolean flag1)
	{
	    //setUndecorated(true);
	    //getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        frameWidth = width;
        frameHeight = height;
        aGameWindow = gameWindow;
        graphicsTranslate = flag1;
        if (graphicsTranslate)
        {
        	offsetX = 2;
        	offsetY = 18;
        }
        else
        {
        	offsetX = 0;
        	offsetY = 0;
        }
        setTitle(title);
        setResizable(resizable);
        setVisible(true);
        toFront();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aGraphics49 = getGraphics();
        this.setSize(new Dimension(frameWidth, frameHeight));
    }

    public Graphics getGraphics() {
    	Graphics g = super.getGraphics();
    	if (graphicsTranslate)
    	{
    		//graphicsTranslate = false;
        	g.translate(offsetX, offsetY);
    	}
        return g;
    }
    
    public final void paint(Graphics g) {
        aGameWindow.paint(g);
    }
    
    public final void update(Graphics g) {
    	aGameWindow.update(g);
    }

    int frameWidth;
    int frameHeight;
    boolean graphicsTranslate;
    int frameOffset;
    public static int offsetX;
    public static int offsetY;
    GameWindow aGameWindow;
    Graphics aGraphics49;
}
