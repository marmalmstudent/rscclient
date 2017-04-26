package org.conf.cachedev;

import java.io.File;

public class ProjectilePanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProjectilePanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractSprite(cd.cdc.getProjectile(), 0,
				CDConst.ProjectileDatDir, CDConst.ProjectilePNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getProjectile(),
				CDConst.ProjectileDatDir, 0);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.ProjectileDatDir,
				cd.cdc.getProjectile(), 0);
	}
}
