package client;

import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5653713738612659906L;
	public GameFrame(GameWindow gameWindow, Rectangle bounds, String title, boolean resizable, boolean translate)
	{
	    //setUndecorated(true);
	    //getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
		this.bounds = bounds;
        gw = gameWindow;
        graphicsTranslate = translate;
        if (graphicsTranslate)
        {
        	bounds.x = 1;
        	bounds.y = 24;
        }
        else
        {
        	bounds.x = 0;
        	bounds.y = 0;
        }
        setTitle(title);
        setResizable(resizable);
        setVisible(true);
        toFront();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(bounds.width, bounds.height));
    }

    public Graphics getGraphics() {
    	Graphics g = super.getGraphics();
    	if (graphicsTranslate)
        	g.translate(bounds.x, bounds.y);
        return g;
    }
    
    public final void paint(Graphics g) {
        gw.paint(g);
    }
    
    public final void update(Graphics g) {
    	gw.update(g);
    }

    private Rectangle bounds;
    private boolean graphicsTranslate;
    private GameWindow gw;
}
