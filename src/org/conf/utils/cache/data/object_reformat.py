import os
import scipy as sp


src = "models3d/"
dst = "models36/"
if __name__ == "__main__":
    max_files = 500
    for i in range(0, max_files, 1):
        if (os.path.isfile(src + str(i) + ".ob3")):
            print("Processing file %d of %d (%.1f%%)"
                  % (i, max_files, 100*i/max_files))
            with open(src + str(i) + ".ob3", "rb") as s:
                data_read = sp.array(bytearray(s.read()), dtype=sp.uint8)
                file_size_read = data_read[:4]
                file_size_read.dtype = sp.int32
                file_size_read = file_size_read.byteswap()
                if (len(data_read) != file_size_read):
                    file_size = sp.array([len(data_read)], dtype=sp.int32)
                    file_size = file_size.byteswap()  # to little endian
                    with open(dst + str(i), "wb") as d:
                        d.write(file_size.tobytes())
                        d.write(data_read)
                else:
                    print("File %d already has file size header" % i)
