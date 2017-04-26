package org.conf.cachedev;

public class LogoPanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogoPanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractLogo();
	}

	@Override
	public boolean importSprite() {
		if (super.checkValidEntries())
		{
			cd.cdc.insertLogo(selectedFile,
					Boolean.getBoolean(reqShiftTxt.getText().toLowerCase()),
					Integer.parseInt(xShiftTxt.getText()),
					Integer.parseInt(yShiftTxt.getText()),
					Integer.parseInt(camAngle1Txt.getText()),
					Integer.parseInt(camAngle2Txt.getText()));
			return true;
		}
		return false;
	}
}
