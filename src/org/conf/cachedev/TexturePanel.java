package org.conf.cachedev;

import java.io.File;


import org.conf.cachedev.sprites.Sprite;

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
	public void fileSelected(File f) {
		selectedFile = f;
		String parent = f.getParentFile().getName();
		if (parent.equals("png")) {
			cd.cdc.getTexture().newPNG(f, false, 0, 0, 0, 0);
			Sprite sprite = cd.cdc.getTexture().getSprite();
			widthTxt.setText(Integer.toString(sprite.getWidth()));
			heightTxt.setText(Integer.toString(sprite.getHeight()));
			reqShiftTxt.setText(sprite.getRequiresShift() ? "true" : "false");
			xShiftTxt.setText(Integer.toString(sprite.getXShift()));
			yShiftTxt.setText(Integer.toString(sprite.getYShift()));
			camAngle1Txt.setText(Integer.toString(sprite.getCameraAngle1()));
			camAngle2Txt.setText(Integer.toString(sprite.getCameraAngle2()));
		}
	}

	@Override
	public void exportSprite() {
		cd.cdc.extractTexture();
	}

	@Override
	public boolean importSprite() {
		if (super.checkValidEntries())
		{
			cd.cdc.insertTexture(selectedFile,
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
