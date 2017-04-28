package dbdev;

import java.io.File;

public class MediaPanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MediaPanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractSprite(cd.cdc.getMedia(), 0,
				CDConst.MediaDatDir, CDConst.MediaPNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getMedia(),
				CDConst.MediaDatDir, 0);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.MediaDatDir,
				cd.cdc.getMedia(), 0);
	}
}
