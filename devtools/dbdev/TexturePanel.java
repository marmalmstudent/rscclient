package dbdev;

import java.io.File;

public class TexturePanel extends SpritesPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TexturePanel(CacheDev cd)
	{
		super(cd);
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractSprite(cd.cdc.getTexture(), 0xffff00ff,
				CDConst.TextureDatDir, CDConst.TexturePNGDir);
	}

	@Override
	public boolean importSprite() {
		return super.importSprite(cd.cdc.getTexture(),
				CDConst.TextureDatDir, 0xffff00ff);
	}

	@Override
	public void fileSelected(File f) {
		super.fileSelected(f, CDConst.TextureDatDir,
				cd.cdc.getEntity(), 0xffff00ff);
	}
}
