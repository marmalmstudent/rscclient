package dbdev;

import java.io.File;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpritesTab extends JTabbedPane implements ChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EntityPanel entity;
	private ItemPanel item;
	private LogoPanel logo;
	private MediaPanel media;
	private ProjectilePanel projectile;
	private TexturePanel texture;
	private UtilPanel util;
	
	private final int spButtons = 10;
	private boolean[] spSelected = new boolean[spButtons];
	private CacheDev cd;
	
	public SpritesTab(CacheDev cd)
	{
		this.cd = cd;
		addChangeListener(this);

		entity = new EntityPanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.ENTITY_ID], entity);
		item = new ItemPanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.ITEM_ID], item);
		logo = new LogoPanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.LOGO_ID], logo);
		media = new MediaPanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.MEDIA_ID], media);
		projectile = new ProjectilePanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.PROJECTILE_ID], projectile);
		texture = new TexturePanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.TEXTURE_ID], texture);
		util = new UtilPanel(cd);
		addTab(CDConst.SPRITE_NAMES[CDConst.UTIL_ID], util);
	
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
	
	public void fileSelected(File f)
	{
		if (spSelected[CDConst.ENTITY_ID]) {
			entity.fileSelected(f);
		} else if (spSelected[CDConst.ITEM_ID]) {
			item.fileSelected(f);
		} else if (spSelected[CDConst.LOGO_ID]) {
			logo.fileSelected(f);
		} else if (spSelected[CDConst.MEDIA_ID]) {
			media.fileSelected(f);
		} else if (spSelected[CDConst.PROJECTILE_ID]) {
			projectile.fileSelected(f);
		} else if (spSelected[CDConst.TEXTURE_ID]) {
			texture.fileSelected(f);
		} else if (spSelected[CDConst.UTIL_ID]) {
			util.fileSelected(f);
		}
	}

	public void handleEvent(String bName)
	{
		if (spSelected[CDConst.ENTITY_ID]) {
			entity.handleEvent(bName);
		} else if (spSelected[CDConst.ITEM_ID]) {
			item.handleEvent(bName);
		} else if (spSelected[CDConst.LOGO_ID]) {
			logo.handleEvent(bName);
		} else if (spSelected[CDConst.MEDIA_ID]) {
			media.handleEvent(bName);
		} else if (spSelected[CDConst.PROJECTILE_ID]) {
			projectile.handleEvent(bName);
		} else if (spSelected[CDConst.TEXTURE_ID]) {
			texture.handleEvent(bName);
		} else if (spSelected[CDConst.UTIL_ID]) {
			util.handleEvent(bName);
		}
	}
	
	public void handleWorkon()
	{
		if (spSelected[CDConst.ENTITY_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.EntityArchive,
					CDConst.EntityDatDir,
					cd.cdc.getEntity().getSpriteNames());
		}
		else if (spSelected[CDConst.ITEM_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.ItemArchive,
					CDConst.ItemDatDir,
					cd.cdc.getItem().getSpriteNames());
		}
		else if (spSelected[CDConst.LOGO_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.LogoArchive,
					CDConst.LogoDatDir,
					cd.cdc.getLogo().getSpriteNames());
		}
		else if (spSelected[CDConst.MEDIA_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.MediaArchive,
					CDConst.MediaDatDir,
					cd.cdc.getMedia().getSpriteNames());
		}
		else if (spSelected[CDConst.PROJECTILE_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.ProjectileArchive,
					CDConst.ProjectileDatDir,
					cd.cdc.getProjectile().getSpriteNames());
		}
		else if (spSelected[CDConst.TEXTURE_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.TextureArchive,
					CDConst.TextureDatDir,
					cd.cdc.getTexture().getSpriteNames());
		}
		else if (spSelected[CDConst.UTIL_ID])
		{
			cd.cdc.workon(CDConst.cacheSpriteDir+CDConst.UtilArchive,
					CDConst.UtilDatDir,
					cd.cdc.getUtil().getSpriteNames());
		}
	}
	
	public void handleWorkoff()
	{
		if (spSelected[CDConst.ENTITY_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.EntityArchive,
					CDConst.EntityDatDir,
					cd.cdc.getEntity().getSpriteNames());
		}
		else if (spSelected[CDConst.ITEM_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.ItemArchive,
					CDConst.ItemDatDir,
					cd.cdc.getItem().getSpriteNames());
		}
		else if (spSelected[CDConst.LOGO_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.LogoArchive,
					CDConst.LogoDatDir,
					cd.cdc.getLogo().getSpriteNames());
		}
		else if (spSelected[CDConst.MEDIA_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.MediaArchive,
					CDConst.MediaDatDir,
					cd.cdc.getMedia().getSpriteNames());
		}
		else if (spSelected[CDConst.PROJECTILE_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.ProjectileArchive,
					CDConst.ProjectileDatDir,
					cd.cdc.getProjectile().getSpriteNames());
		}
		else if (spSelected[CDConst.TEXTURE_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.TextureArchive,
					CDConst.TextureDatDir,
					cd.cdc.getTexture().getSpriteNames());
		}
		else if (spSelected[CDConst.UTIL_ID])
		{
			cd.cdc.workoff(CDConst.cacheSpriteDir+CDConst.UtilArchive,
					CDConst.UtilDatDir,
					cd.cdc.getUtil().getSpriteNames());
		}
	}
	
	public void handleInsert()
	{
		if (spSelected[CDConst.ENTITY_ID]) {
			entity.importSprite();
		} else if (spSelected[CDConst.ITEM_ID]) {
			item.importSprite();
		} else if (spSelected[CDConst.LOGO_ID]) {
			logo.importSprite();
		} else if (spSelected[CDConst.MEDIA_ID]) {
			media.importSprite();
		} else if (spSelected[CDConst.PROJECTILE_ID]) {
			projectile.importSprite();
		} else if (spSelected[CDConst.TEXTURE_ID]) {
			texture.importSprite();
		} else if (spSelected[CDConst.UTIL_ID]) {
			util.importSprite();
		}
	}
	
	public void handleExtract()
	{
		if (spSelected[CDConst.ENTITY_ID]) {
			entity.exportSprite();
		} else if (spSelected[CDConst.ITEM_ID]) {
			item.exportSprite();
		} else if (spSelected[CDConst.LOGO_ID]) {
			logo.exportSprite();
		} else if (spSelected[CDConst.MEDIA_ID]) {
			media.exportSprite();
		} else if (spSelected[CDConst.PROJECTILE_ID]) {
			projectile.exportSprite();
		} else if (spSelected[CDConst.TEXTURE_ID]) {
			texture.exportSprite();
		} else if (spSelected[CDConst.UTIL_ID]) {
			util.exportSprite();
		}
	}
	
	public File getDefaultPath()
	{
		File path = new File(CDConst.SpriteDir);
		if (spSelected[CDConst.ENTITY_ID]) {
			path = new File(CDConst.EntityDir);
		} else if (spSelected[CDConst.ITEM_ID]) {
			path = new File(CDConst.ItemDir);
		} else if (spSelected[CDConst.LOGO_ID]) {
			path = new File(CDConst.LogoDir);
		} else if (spSelected[CDConst.MEDIA_ID]) {
			path = new File(CDConst.MediaDir);
		} else if (spSelected[CDConst.PROJECTILE_ID]) {
			path = new File(CDConst.ProjectileDir);
		} else if (spSelected[CDConst.TEXTURE_ID]) {
			path = new File(CDConst.TextureDir);
		} else if (spSelected[CDConst.UTIL_ID]) {
			path = new File(CDConst.UtilDir);
		}
		return path;
	}
	
	private void resetSelected()
	{
    	for (int i = 0; i < spButtons; spSelected[i++] = false);
	}

	public void stateChanged(ChangeEvent e) {
	    JTabbedPane tabSource = (JTabbedPane) e.getSource();
	    String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());
	    if (tab.equals(CDConst.SPRITE_NAMES[CDConst.ENTITY_ID]))
	    {
	    	resetSelected();
	    	spSelected[CDConst.ENTITY_ID] = true;
		}
	    else if (tab.equals(CDConst.SPRITE_NAMES[CDConst.ITEM_ID]))
		{
	    	resetSelected();
	    	spSelected[CDConst.ITEM_ID] = true;
		}
	    else if (tab.equals(CDConst.SPRITE_NAMES[CDConst.LOGO_ID]))
		{
	    	resetSelected();
	    	spSelected[CDConst.LOGO_ID] = true;
		}
	    else if (tab.equals(CDConst.SPRITE_NAMES[CDConst.MEDIA_ID]))
		{
	    	resetSelected();
	    	spSelected[CDConst.MEDIA_ID] = true;
		}
	    else if (tab.equals(CDConst.SPRITE_NAMES[CDConst.PROJECTILE_ID]))
		{
	    	resetSelected();
	    	spSelected[CDConst.PROJECTILE_ID] = true;
		}
	    else if (tab.equals(CDConst.SPRITE_NAMES[CDConst.TEXTURE_ID]))
		{
	    	resetSelected();
	    	spSelected[CDConst.TEXTURE_ID] = true;
		}
	    else if (tab.equals(CDConst.SPRITE_NAMES[CDConst.UTIL_ID]))
		{
	    	resetSelected();
	    	spSelected[CDConst.UTIL_ID] = true;
		}
	}
}
