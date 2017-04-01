#!/bin/zsh
cd sounds1
python FixSound.py pcm wav $(echo $(ls | grep '.pcm'))
cd ..
