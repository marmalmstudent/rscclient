package dbdev;

public abstract class CDConst
{
	public static final int SPRITES_ID = 0;
	public static final int MODELS_ID = 1;
	public static final int LANDSCAPES_ID = 2;
	public static final int SOUNDS_ID = 3;
	public static final String[] DB_NAMES = {
			"Sprites", "Models", "Landscapes", "Sounds"
	};
	public static final int ENTITY_ID = 0;
	public static final int ITEM_ID = 1;
	public static final int LOGO_ID = 2;
	public static final int MEDIA_ID = 3;
	public static final int PROJECTILE_ID = 4;
	public static final int TEXTURE_ID = 5;
	public static final int UTIL_ID = 6	;
	public static final String[] SPRITE_NAMES = {
			"Entity", "Item", "Logo", "Media",
			"Projectile", "Texture", "Util"
	};

	public static final String cacheDir = "database/";
	public static final String cacheSpriteDir = cacheDir+"Sprites/";
	public static final String devRootDir = "devtools/dbdev/";
	public static final String namesDir = devRootDir+"names/";
	
	public static final String devDir = devRootDir+"dev/";
	
	/* Sprites */
	public static final String SpriteDir = devDir+"Sprites/";

	public static final String EntityArchive = "Entity.zip";
	public static final String EntityNames = namesDir+"entitynames";
	public static final String EntityDir = SpriteDir+"Entity/";
	public static final String EntityDatDir = EntityDir+"dat/";
	public static final String EntityPNGDir = EntityDir+"png/";

	public static final String MediaArchive = "Media.zip";
	public static final String MediaNames = namesDir+"medianames";
	public static final String MediaDir = SpriteDir+"Media/";
	public static final String MediaDatDir = MediaDir+"dat/";
	public static final String MediaPNGDir = MediaDir+"png/";

	public static final String UtilArchive = "Util.zip";
	public static final String UtilNames = namesDir+"utilnames";
	public static final String UtilDir = SpriteDir+"Util/";
	public static final String UtilDatDir = UtilDir+"dat/";
	public static final String UtilPNGDir = UtilDir+"png/";

	public static final String ItemArchive = "Item.zip";
	public static final String ItemNames = namesDir+"itemnames";
	public static final String ItemDir = SpriteDir+"Item/";
	public static final String ItemDatDir = ItemDir+"dat/";
	public static final String ItemPNGDir = ItemDir+"png/";

	public static final String LogoArchive = "Logo.zip";
	public static final String LogoNames = namesDir+"logonames";
	public static final String LogoDir = SpriteDir+"Logo/";
	public static final String LogoDatDir = LogoDir+"dat/";
	public static final String LogoPNGDir = LogoDir+"png/";

	public static final String ProjectileArchive = "Projectile.zip";
	public static final String ProjectileNames = namesDir+"projectilenames";
	public static final String ProjectileDir = SpriteDir+"Projectile/";
	public static final String ProjectileDatDir = ProjectileDir+"dat/";
	public static final String ProjectilePNGDir = ProjectileDir+"png/";

	public static final String TextureArchive = "Texture.zip";
	public static final String TextureNames = namesDir+"texturenames";
	public static final String TextureDir = SpriteDir+"Texture/";
	public static final String TextureDatDir = TextureDir+"dat/";
	public static final String TexturePNGDir = TextureDir+"png/";

	/* Models */
	public static final String ModelArchive = "models.zip";
	public static final String ModelNames = namesDir+"modelnames";
	public static final String ModelsDir = devDir+"models/";
	public static final String ModelsOb3Dir = ModelsDir+"ob3/";
	public static final String ModelsStlDir = ModelsDir+"stl/";

	/* Landscapes */
	public static final String LandscapeArchive = "Landscape.zip";
	public static final String LandscapeNames = namesDir+"landscapenames";
	public static final String LandscapesDir = devDir+"Landscape/";
	public static final String LandscapesHeiDir = LandscapesDir+"hei/";
	public static final String LandscapesStlDir = LandscapesDir+"stl/";

	/* Sounds */
	public static final String SoundArchive = "sounds.zip";
	public static final String SoundNames = namesDir+"soundnames";
	public static final String SoundsDir = devDir+"sounds/";
	public static final String SoundsPCMDir = SoundsDir+"pcm/";
	public static final String SoundsWAVDir = SoundsDir+"wav/";
}
