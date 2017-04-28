package dbdev;

import java.io.File;

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
		cd.cdc.extractSprite(cd.cdc.getEntity(), 0,
				CDConst.EntityDatDir, CDConst.EntityPNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getEntity(),
				CDConst.EntityDatDir, 0);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.EntityDatDir,
				cd.cdc.getEntity(), 0);
	}
}
