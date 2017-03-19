import sys
import scipy as sp
import scipy.io.wavfile as spwf
import matplotlib.pyplot as plt

def array_from_file(src_filename):
    src_file = None
    src_data = None
    try:
        src_file = open(src_filename, "rb")
        src_data = sp.array(bytearray(src_file.read()), dtype=sp.int16)*256
    except IOError:
        print("Could not execute program: %s" % str(sys.exc_info()[1]))
    finally:
        src_file.close()
    return src_data


def file_from_array(dst_data, dst_filename):
    if (dst_filename[-3:] == 'wav' or dst_filename[-3:] == 'WAV'):
        spwf.write(dst_filename, 8000, dst_data)
    else:
        dst_file = None
        try:
            dst_file = open(dst_filename, "wb")
            dst_file.write(dst_data.tobytes())
        except IOError:
            print("Could not execute program: %s" % str(sys.exc_info()[1]))
        finally:
            dst_file.close()
    return


def fix_rsc_sounds(src_filename, dst_filename):
    src_data = array_from_file(src_filename)
    # add 1 to keep track of native zeros
    src_data_pos = (src_data+1)*(src_data > -1)
    # prepare any zeros (i.e. negative values from src_data_pos) for magnitude swap
    src_data_pos = (src_data_pos + (src_data_pos < 1)*max(src_data_pos)) - 1
    # swap magnitude
    src_data_pos = max(src_data_pos)-src_data_pos

    src_data_neg = src_data*(src_data < 0)
    dst_data = src_data_pos+src_data_neg

    file_from_array(dst_data, dst_filename)
    return


def plot_data():
    fig = plt.figure()
    ax0 = fig.add_subplot(211)
    ax0.plot(src_data, linestyle='-', marker='o', markersize=1,
             markerfacecolor='r', markeredgecolor='r', linewidth=0.5)
    ax1 = fig.add_subplot(212)
    ax1.plot(dst_data, linestyle='-', marker='o', markersize=1,
             markerfacecolor='r', markeredgecolor='r', linewidth=0.5)
    plt.show()


if __name__ == "__main__":
    old_file_ext = sys.argv[1]
    new_file_ext = sys.argv[2]
    sound_files = sys.argv[3:]
    i = 0
    for sound in sound_files:
        print("Processing %s (%.1f%%) ..." % (sound, i/len(sound_files)*100))
        fix_rsc_sounds(sound, sound[:-(1+len(old_file_ext))]+"."+new_file_ext)
        i += 1
    print("Done!")
