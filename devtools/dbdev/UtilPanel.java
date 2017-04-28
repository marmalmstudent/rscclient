package org.conf.cachedev;

import java.io.File;

public class UtilPanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UtilPanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractSprite(cd.cdc.getUtil(), 0,
				CDConst.UtilDatDir, CDConst.UtilPNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getUtil(),
				CDConst.UtilDatDir, 0);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.UtilDatDir,
				cd.cdc.getUtil(), 0);
	}
}
