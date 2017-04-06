import os
import scipy as sp
import scipy.io.wavfile as spwf
import matplotlib.pyplot as plt

def array_from_file(src_filename):
    """
    Reads the data from the file.

    Parameters
    ----------
    src_filename : str
        The file to be read from. it should include the path to the
        file relative to where the script will be extecuted from.

    Returns
    -------
    numpy.ndarray
        A signed 8-bit array containing the data.
    """
    with open(src_filename, "rb") as f:
        return sp.array(bytearray(f.read()), dtype=sp.int8)
    return None


def file_from_array(dst_data, dst_filename):
    """
    Writes the data to the file.

    Parameters
    ----------
    dst_data : numpy.ndarray
        The data that is to be written to the file. If the file
        is a wav file the format must comply with the valid formats
        of scipy.io.wavfile.write.
    dst_filename : str
        The file to be written to. it should include the path to the
        file relative to where the script will be extecuted from.
    """
    if (dst_filename[-3:] == 'wav' or dst_filename[-3:] == 'WAV'):
        spwf.write(dst_filename, 8000, dst_data)
    else:
        with open(dst_filename, "wb") as f:
            f.write(dst_data.tobytes())


def fix_rsc_sounds(src):
    """
    Reformats the data points by inverting (max(data)-data)
    the positive data points and add them to the negative data points.

    Parameters
    ----------
    src : numpy.ndarray
        The source file data.
    """
    # store src as int16 to facilitate data manipulation
    src_data = sp.array(src, dtype=sp.int16)
    # invert positive data points
    pos = (max(src_data)-src_data)*(src_data > -1)
    # isolate negative data points
    neg = src_data*(src_data < 0)
    return sp.array(pos + neg, dtype=sp.int8)


def plot_data(src_data, dst_data):
    """
    Plots the data

    Parameters
    ----------
    src_data : numpy.ndarray
        An array containing the data from the source file
    dst_data : numpy.ndarray
        An array containing the data from the destination file.
    """
    fig = plt.figure()
    ax0 = fig.add_subplot(211)
    ax0.plot(src_data, linestyle='-', marker='o', markersize=1,
             markerfacecolor='r', markeredgecolor='r', linewidth=0.5)
    ax1 = fig.add_subplot(212)
    ax1.plot(dst_data, linestyle='-', marker='o', markersize=1,
             markerfacecolor='r', markeredgecolor='r', linewidth=0.5)
    plt.show()


def fix_sounds(src_file, begin, dst_file):
    file_from_array(fix_rsc_sounds(array_from_file(src_file)[begin:]),
                    dst_file)


pcm_dir = "sounds1/"
wav_dir = "sounds/"
def pcm_to_wav(start_file, max_files):
    for i in range(start_file, start_file + max_files):
        if (os.path.isfile(pcm_dir + str(i))):
            print("Processing file %d of %d (%.1f%%)"
                  % (i, max_files, 100*(i-start_file)/(max_files)))
            fix_sounds(pcm_dir + str(i), 0, wav_dir + str(i) + ".wav")


def wav_to_pcm(start_file, max_files):
    for i in range(start_file, start_file + max_files):
        if (os.path.isfile(wav_dir + str(i) + ".wav")):
            print("Processing file %d of %d (%.1f%%)"
                  % (i, max_files, 100*(i-start_file)/(max_files)))
            fix_sounds(wav_dir + str(i) + ".wav", 44, pcm_dir + str(i))

if __name__ == "__main__":
    pcm_to_wav(0, 40)
