#!/bin/zsh
cd ~/git/rscclient/src/org/conf/utils/
python Sprites.py
cd ~/git/rscclient/src/org/conf/utils/cache/Sprites/
for var in {20..79}; do
    cp 35$var 32$var
done
cd ..
./Sprites_Archive.sh
cp ~/git/rscclient/src/org/conf/utils/cache/Sprites.zip ~/git/rscclient/src/org/conf/client/
cp ~/git/rscclient/src/org/conf/utils/cache/Textures/Textures ~/git/rscclient/src/org/conf/client/
cd ~/git/rscclient/src/org/conf/utils/
