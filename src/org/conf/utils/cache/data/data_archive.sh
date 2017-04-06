#!/bin/zsh
archive_zip=( models36 sounds1 )
for var in $archive_zip; do
    cd $var; zip -r $var.zip $(ls); mv $var.zip ..; cd ..
done
