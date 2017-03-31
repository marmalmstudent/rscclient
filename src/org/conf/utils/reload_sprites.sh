#!/bin/zsh
cd ~/git/rscclient/src/org/conf/utils/
python Sprites.py
cd ~/git/rscclient/src/org/conf/utils/cache/
./Sprites_Archive.sh
cp ~/git/rscclient/src/org/conf/utils/cache/Sprites.zip ~/git/rscclient/src/org/conf/client/
cp ~/git/rscclient/src/org/conf/utils/cache/Textures/Textures ~/git/rscclient/src/org/conf/client/
cd ~/git/rscclient/src/org/conf/utils/
