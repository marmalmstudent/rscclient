#!/bin/zsh
archive_zip=( Animations Doors Elevation Items Landscape NPCs Objects Prayers Spells Sprites Textures Tiles )
for var in $archive_zip; do
    cd $var; zip -r $var.zip $(ls); mv $var.zip ..; cd ..
done
