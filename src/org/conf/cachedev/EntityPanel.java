package org.conf.cachedev;

public class EntityPanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityPanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractEntity();
	}

	@Override
	public boolean importSprite() {
		if (super.checkValidEntries())
		{
			cd.cdc.insertEntity(selectedFile,
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
