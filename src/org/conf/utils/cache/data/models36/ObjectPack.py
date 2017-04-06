import numpy as np
import os


def str_hash(s):
    s = s.upper()
    i = np.array([0], dtype=np.int32)
    for j in s:
        i = i*61 + ord(j) - 32
    return i


if __name__ == "__main__":
    max_files = 500
    with open("models36.jag", "wb") as d:
        with open("header", "rb") as h:
            # TODO: write a script that generates the header instead
            # of using the old.
            d.write(h.read())
        for i in range(0, max_files):
            if (os.path.isfile("object"+str(i)+".ob3")):
                print("Processing file %d of %d (%.1f%%)"
                      % (i, max_files, 100*(i)/max_files))
                with open("object"+str(i)+".ob3", "rb") as s:
                    d.write(s.read())
