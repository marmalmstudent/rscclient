package org.conf.cachedev;

import java.io.File;

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
		cd.cdc.extractSprite(cd.cdc.getLogo(), 0,
				CDConst.LogoDatDir, CDConst.LogoPNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getLogo(),
				CDConst.LogoDatDir, 0);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.LogoDatDir,
				cd.cdc.getLogo(), 0);
	}
}
