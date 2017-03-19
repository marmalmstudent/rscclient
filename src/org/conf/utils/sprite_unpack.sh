#!/bin/zsh
max_idx=3500
for var in {0..$max_idx}; do
    echo "Processing sprite $var/$max_idx ($(( 100 * $var / ( $max_idx + 1 ) )) %)..."
    if ls sprites_buffer/$var 1> /dev/null 2>&1; then
	    echo $(od -An -t u1 -v --endian=big sprites_buffer/$var) > sprites_txt/$var.txt
	    python Sprites.py $var
	    rm sprites_txt/$var.txt
    else
	    echo "sprite $var do not exist"
    fi
    echo "Done!"
done
