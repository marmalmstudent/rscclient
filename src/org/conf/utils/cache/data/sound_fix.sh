#!/bin/zsh
cd sounds
python FixSound.py pcm wav $(echo $(ls | grep '.pcm'))
cd ..
