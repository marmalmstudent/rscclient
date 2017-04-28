package dbdev;

import java.io.File;

public class ItemPanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ItemPanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractSprite(cd.cdc.getItem(), 0,
				CDConst.ItemDatDir, CDConst.ItemPNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getItem(),
				CDConst.ItemDatDir, 0);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.ItemDatDir,
				cd.cdc.getItem(), 0);
	}
}
