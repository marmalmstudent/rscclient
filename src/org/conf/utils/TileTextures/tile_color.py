import numpy as np
import matplotlib.pyplot as plt
import sys
import os


def read_sprite(file_name):
    """
    Reads the file and returns the data as an unsigned 8-bit integer
    numpy array.

    Parameters
    ----------
    file_name : str
        The path to the sprite.

    Returns
    -------
    numpy.ndarray
        An unsigned 8-bit array containing the data from the sprite.
        The data should be little endian.
    """
    fr = open(file_name, "rb")
    data_read = np.array(bytearray(fr.read()), dtype=np.uint8)
    fr.close()
    return data_read


def get_data_write(header_data, image_data):
    """
    Concatenates the header array with the image data array and returns
    a byte array.

    Parameters
    ----------
    header_data : numpy.ndarray
        An unsigned 8-bit array containing the header data.
    image_data : numpy.ndarray
        An unsigned 8-bit array containing the image pixel data.

    Returns
    -------
    numpy.ndarray
        The concatenated unsigned 8-bit array.
        The data should be little endian.
    """
    data_out = np.hstack((header_data, image_data))
    return data_out.tobytes()


def write_sprite(file_name, data_write):
    """
    Writes the data to a file.

    Parameters
    ----------
    file_name : str
        The path to the sprite to write to.
    data_write : numpy.ndarray
        An unsigned 8-bit array containing the data to be written to a file.
    """
    fw = open(file_name, "wb")
    fw.write(data_write)
    fw.close()
    return


def get_width(header_data):
    """
    Extracts the width from the header.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    int
        An unsigned 32-bit integer width.
    """
    sprite_width = header_data[0:4]
    sprite_width.dtype = np.uint32
    return sprite_width.byteswap()[0]


def get_width_array(sprite_width):
    """
    Returns a little endian (java uses little endian) 4x8-bit array
    containing the width of the sprite.

    Parameters
    ----------
    sprite_width : int
        The width of the sprite.

    Returns
    -------
    numpy.ndarray
        A 4x8-bit array containing the width. The byte order is little
        endian but it is likely interpreted as big endian.
    """
    arr = np.array([sprite_width],
                   dtype=np.uint32).byteswap()
    arr.dtype = np.uint8
    return arr


def get_height(header_data):
    """
    Extracts the hegith information from the header.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    int
        An unsigned 32-bit integer height.
    """
    sprite_height = header_data[4:8]
    sprite_height.dtype = np.uint32
    return sprite_height.byteswap()[0]


def get_height_array(sprite_height):
    """
    Returns a little endian (java uses little endian) 4x8-bit array
    containing the height of the sprite.

    Parameters
    ----------
    sprite_height : int
        The height of the sprite.

    Returns
    -------
    numpy.ndarray
        A 4x8-bit array containing the height. The byte order is little
        endian but it is likely interpreted as big endian.
    """
    arr = np.array([sprite_height],
                   dtype=np.uint32).byteswap()
    arr.dtype = np.uint8
    return arr


def get_requires_shift(header_data):
    """
    Extracts information about whether the sprite needs to be shifted
    in the x-direction and y-direction.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    bool
        True if the sprite needs shifing. False if not.
    """
    if (data_read[8] == 0):
        return False
    else:
        return True


def get_requires_shift_array(requires_shift):
    """
    Returns a 1x8-bit array containg information about if the sprite needs to
    be shifted.

    Parameters
    ----------
    requires_shift : bool
        True if the sprite requires a shift.

    Returns
    -------
    numpy.ndarray
        An unsigned 1x8-bit array containing 1 if the sprite needs shift
        or 0 if not.
    """
    if requires_shift:
        return np.array([1], dtype=np.uint8)
    else:
        return np.array([0], dtype=np.uint8)


def get_x_shift(header_data):
    """
    Extracts the x-shift from the header.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    int
        An unsigned 32-bit integer x-shift.
    """
    x_shift = header_data[9:13]
    x_shift.dtype = np.uint32
    return x_shift.byteswap()[0]


def get_x_shift_array(x_shift):
    """
    Returns a little endian (java uses little endian) 4x8-bit array
    containing the x-shift of the sprite.

    Parameters
    ----------
    x_shift : int
        The x-shift of the sprite.

    Returns
    -------
    numpy.ndarray
        A 4x8-bit array containing the x-shift. The byte order is little
        endian but it is likely interpreted as big endian.
    """
    arr = np.array([x_shift],
                   dtype=np.uint32).byteswap()
    arr.dtype = np.uint8
    return arr


def get_y_shift(header_data):
    """
    Extracts the y-shift from the header.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    int
        An unsigned 32-bit integer y-shift.
    """
    y_shift = header_data[13:17]
    y_shift.dtype = np.uint32
    return y_shift.byteswap()[0]


def get_y_shift_array(y_shift):
    """
    Returns a little endian (java uses little endian) 4x8-bit array
    containing the y-shift of the sprite.

    Parameters
    ----------
    y_shift : int
        The y-shift of the sprite.

    Returns
    -------
    numpy.ndarray
        A 4x8-bit array containing the y-shift. The byte order is little
        endian but it is likely interpreted as big endian.
    """
    arr = np.array([y_shift],
                   dtype=np.uint32).byteswap()
    arr.dtype = np.uint8
    return arr


def get_camera_angle_1(header_data):
    """
    Extracts the camera angle 1 from the header.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    int
        An unsigned 32-bit integer camera angle 1.
    """
    cam_angle_1 = header_data[17:21]
    cam_angle_1.dtype = np.uint32
    return cam_angle_1.byteswap()[0]


def get_camera_angle_1_array(cam_angle_1):
    """
    Returns a little endian (java uses little endian) 4x8-bit array
    containing the camera angle 1 of the sprite.

    Parameters
    ----------
    cam_angle_1 : int
        The camera angle 1 of the sprite.

    Returns
    -------
    numpy.ndarray
        A 4x8-bit array containing the camera angle 1. The byte order is little
        endian but it is likely interpreted as big endian.
    """
    arr = np.array([cam_angle_1],
                   dtype=np.uint32).byteswap()
    arr.dtype = np.uint8
    return arr


def get_camera_angle_2(header_data):
    """
    Extracts the camera angle 2 from the header.

    Parameters
    ----------
    header_data : numpy.ndarray
        The unsigned 8-bit header data array.

    Returns
    -------
    int
        An unsigned 32-bit integer camera angle 2.
    """
    cam_angle_2 = header_data[21:25]
    cam_angle_2.dtype = np.uint32
    return cam_angle_2.byteswap()[0]


def get_camera_angle_2_array(cam_angle_2):
    """
    Returns a little endian (java uses little endian) 4x8-bit array
    containing the camera angle 2 of the sprite.

    Parameters
    ----------
    cam_angle_2 : int
        The camera angle 2 of the sprite.

    Returns
    -------
    numpy.ndarray
        A 4x8-bit array containing the camera angle 2. The byte order is little
        endian but it is likely interpreted as big endian.
    """
    arr = np.array([cam_angle_2],
                   dtype=np.uint32).byteswap()
    arr.dtype = np.uint8
    return arr


def get_header(data_read):
    """
    Extracts the header from the sprite data.

    Parameters
    ----------
    data_read : numpy.ndarray
        The bytes contained in the file.

    Returns
    -------
    numpy.ndarray
        The header array.
    """
    return data_read[:25]


def get_header_array(width, height, req_shift, x_shift,
                     y_shift, cam_angle_1, cam_angle_2):
    """
    Creates a header array from header data.

    Parameters
    ----------
    width : numpy.ndarray
        An unsigned 8-bit array of length 4 containing the width as a
        little endian array.
    height : numpy.ndarray
        An unsigned 8-bit array of length 4 containing height as a
        little endian array.
    req_shift : numpy.ndarray
        An unsigned 8-bit array of length 1 containing the requires-shift byte.
    x_shift : numpy.ndarray
        An unsigned 8-bit array of length 4 containing the x-shift as a
        little endian array.
    y_shift : numpy.ndarray
        An unsigned 8-bit array of length 4 containing the y-shift as a
        little endian array.
    cam_angle_1 : numpy.ndarray
        An unsigned 8-bit array of length 4 containing the camera angle 1 as a
        little endian array.
    cam_angle_2 : numpy.ndarray
        An unsigned 8-bit array of length 4 containing the camera angle 2 as a
        little endian array.

    Returns
    -------
    numpy.ndarray
        An unsigned 8-bit array of length 25 containing the header data.
    """
    arr = np.empty(25, dtype=np.uint8)
    arr[0:4] = get_width_array(width)
    arr[4:8] = get_height_array(height)
    arr[8] = get_requires_shift_array(req_shift)
    arr[9:13] = get_x_shift_array(x_shift)
    arr[13:17] = get_y_shift_array(y_shift)
    arr[17:21] = get_camera_angle_1_array(cam_angle_1)
    arr[21:25] = get_camera_angle_2_array(cam_angle_2)
    return arr


def get_image_data(data_read):
    """
    Creates a unsigned 32-bit integer matrix with dimensions width*height
    The matrix will be interpreted as a little-endian byte order.

    Parameters
    ----------
    data_read : numpy.ndarray
        The bytes contained in the file.
    width : int
        The width of the sprite.
    height : int
        The height of the sprite.

    Returns
    -------
    numpy.ndarray
        The image data (i.e. the data from the file minus the header data).
        The data should be unsigned 32-bit big endian integer.
    """
    return data_read[25:]


def get_matrix_from_data(image_data, width, height):
    """
    """
    image_data.dtype = np.uint32
    image_data.byteswap()
    mat = np.reshape(image_data, (height, width))
    return mat


def get_data_from_matrix(image_matrix):
    """
    """
    mat_shape = np.shape(image_matrix)
    print(mat_shape)
    data = np.reshape(image_matrix, (-1, mat_shape[0]*mat_shape[1]))[0]
    data.byteswap()
    data.dtype = np.uint8
    return data


def get_image_matrix(image_data):
    """
    Extracts a 2-dimensional unsigned 32-bit data array into a 3-dimensional
    8-bit matrix where elemend [i,j] is on the form [r,g,b].

    Parameters
    ----------
    image_data : numpy.ndarray
        A 2-dimensional matrix containing unsigned 32-bit big endian
        integers.

    Returns
    -------
    numpy.ndarray
        A 3-dimensional matrix containing unsigned 8-bit integers.
    """
    image_b = np.array((image_data >> 24) & 0xff, dtype=np.uint8)
    image_g = np.array((image_data >> 16) & 0xff, dtype=np.uint8)
    image_r = np.array((image_data >> 8) & 0xff, dtype=np.uint8)
    # image_a = np.array(image_data & 0xff, dtype=np.uint8)
    image_a = 255*np.ones(np.shape(image_data), dtype=np.uint8)
    # image_a = np.array(255*(1 - (image_r == 255)*(image_b == 255)*(image_g == 0)),dtype=np.uint8)
    image_disp = np.transpose(
        np.array([image_r, image_g, image_b, image_a]), (1, 2, 0))
    return image_disp


def image_matrix_to_array(image_matrix):
    """
    Converts a 3-dimensional matrix on the form [r,g,b]
    where r,g,b are 2-dimensional matrices containing the red, green and blue
    pixel values.

    Parameters
    ----------
    image_matrix numpy.ndarray
        A 3-dimensional matrix containing unsigned 8-bit integers.

    Returns
    -------
    numpy.ndarray
        A 2-dimensional matrix containing unsigned 32-bit big endian
        integers.
    """
    image_a = np.array(image_matrix[0], dtype=np.uint32)
    image_r = np.array(image_matrix[1], dtype=np.uint32)
    image_g = np.array(image_matrix[2], dtype=np.uint32)
    image_b = np.array(image_matrix[3], dtype=np.uint32)
    image_arr = (((image_a & 0xff) << 24)
                 + ((image_r & 0xff) << 16)
                 + ((image_g & 0xff) << 8)
                 + (image_b & 0xff))
    return image_arr


def img_32_to_8(arr):
    """
    """
    tmp = arr.byteswap()
    tmp.dtype = np.uint8
    return tmp


def save_image(file_name, image_matrix):
    """
    Saves an image matrix where element i,j is on the form [r,g,b].

    Parameters
    ----------
    file_name : str
        The path to the image.
    image_matrix : numpy.ndarray
        A 3-dimensional unsigned 8-bit matrix containing a matrix where
        each element is an array of red, green and blue pixel values.
    """
    plt.imsave(file_name, image_matrix)


def read_image(file_name):
    """
    Reads a png image and transforms it into an image matrix where the elements
    are r,b,g,a arrays.

    Parameters
    ----------
    file_name : str
        The path to the image.

    Returns
    -------
    numpy.ndarray
        An unsigned 8-bit matrix where element
        - 0 is the red pixels,
        - 1 is the green pixels,
        - 2 is the blue pixels,
        - 3 is the alpha mask.
    """
    img_raw = np.array(plt.imread(file_name)*0xff, dtype=np.uint8)
    img_shape = np.shape(img_raw)
    img_arr = np.reshape(img_raw, (img_shape[0]*img_shape[1], img_shape[2]))
    img_arr = np.transpose(img_arr)
    img_r = img_arr[0]
    img_g = img_arr[1]
    img_b = img_arr[2]
    img_a = img_arr[3]
    return np.array([img_a, img_r, img_g, img_b]), img_shape[1], img_shape[0]


def applyColor(matrix, colormask):
    image_r = (matrix >> 16) & 0xff
    image_g = (matrix >> 8) & 0xff
    image_b = matrix & 0xff
    mask_r = (colormask >> 16) & 0xff
    mask_g = (colormask >> 8) & 0xff
    mask_b = colormask & 0xff
    image_r_masked = ((image_r*mask_r) >> 8) & 0xff
    image_g_masked = ((image_g*mask_g) >> 8) & 0xff
    image_b_masked = ((image_b*mask_b) >> 8) & 0xff
    masked_mat = ((image_r_masked << 16)
                  + (image_g_masked << 8)
                  + image_b_masked)
    use_mask = np.array((image_r == image_g)*(image_g == image_b),
                        dtype=np.uint32)
    not_use_mask = np.array(1 - (image_r == image_g)*(image_g == image_b),
                            dtype=np.uint32)
    return masked_mat*use_mask + matrix*not_use_mask


def getRGBInt(red, green, blue):
    r = int(red)
    g = int(green)
    b = int(blue)
    return ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff)


def getColorArray():
    colorArray = np.empty(0x100, dtype=np.uint32)
    for i in range(0, 64):
        colorArray[i] = getRGBInt(255 - i*4, 255 - int(i*1.75), 255 - i*4)
        colorArray[i + 64] = getRGBInt(i * 3, 144, 0)
        colorArray[i + 128] = getRGBInt(192 - int(i*1.5), 144 - int(i*1.5), 0)
        colorArray[i + 192] = getRGBInt(96 - int(i*1.5), 48 + int(i*1.5), 0)
    return colorArray


def write_tiledef(name, nbr_defs):
    with open("tiledef", "w") as f:
        for i in range(nbr_defs):
            f.write("  <TextureDef>\n")
            f.write("    <dataName>" + name + str(i) + "</dataName>\n")
            f.write("    <animationName></animationName>\n")
            f.write("  </TextureDef>\n")
    return


sprite_path = "../cache/Sprites/"
sprite_path_rs2 = "../rs2textures/"
image_path = "../sprites_img/"


if __name__ == "__main__":
    max_files = 256
    start_idx = 3280
    for i in range(0, max_files):
        if (os.path.isfile("gray.png")):
            print("Processing file %d of %d (%.1f%%)" % (i, max_files-1,
                                                         100*i/(max_files-1)))
            # Load png image and write the image to a dat file.
            # get 3-dim matrix [r,g,b,a] each element is a matrix.
            data_read, width, height = read_image("gray.png")
            data_read[1] = np.array(255*(data_read[0] == 0) + data_read[1],
                                    dtype=np.uint8)  # r
            data_read[3] = np.array(255*(data_read[0] == 0) + data_read[3],
                                    dtype=np.uint8)  # b
            # convert to matrix with each elements representing pixel values.
            # typecast to 8-bit.
            image_arr_gray = image_matrix_to_array(data_read)
            colorArray = getColorArray()
            image_arr_color = applyColor(image_arr_gray, colorArray[i])
            image_arr = img_32_to_8(image_arr_color)
            requires_shift = False
            x_shift = 0
            y_shift = 0
            cam_angle_1 = width
            cam_angle_2 = height
            # create header
            image_header = get_header_array(width, height, requires_shift,
                                            x_shift, y_shift, cam_angle_1,
                                            cam_angle_2)
            image_data = get_data_write(image_header, image_arr)
            write_sprite(str(start_idx + i), image_data)

            # Load data file and save to png file.
            data_read = read_sprite(str(start_idx + i))
            header_data = get_header(data_read)
            height = get_height(header_data)
            width = get_width(header_data)
            image_data = get_matrix_from_data(get_image_data(data_read),
                                              width, height)
            image_mat = get_image_matrix(image_data)
            save_image(str(start_idx + i)+".png", image_mat)
            save_image(sprite_path_rs2 + str(start_idx + i)+".png", image_mat)
    write_tiledef("ground", max_files)

"""
if __name__ == "__main__":
    if (os.path.isfile("../wall_dirt.png")):
        print("Processing file wall_dirt_brown")
        # Load png image and write the image to a dat file.
        # get 3-dim matrix [r,g,b,a] each element is a matrix.
        data_read, width, height = read_image("../wall_dirt.png")
        data_read[1] = np.array(255*(data_read[0] == 0) + data_read[1],
                                dtype=np.uint8)  # r
        data_read[3] = np.array(255*(data_read[0] == 0) + data_read[3],
                                dtype=np.uint8)  # b
        r_avg = sum(np.array(data_read[1], dtype=np.uint64))/len(data_read[1])
        g_avg = sum(np.array(data_read[2], dtype=np.uint64))/len(data_read[2])
        b_avg = sum(np.array(data_read[3], dtype=np.uint64))/len(data_read[3])
        print(r_avg, g_avg, b_avg)
        # convert to matrix with each elements representing pixel values.
        # typecast to 8-bit.
        image_arr_gray = image_matrix_to_array(data_read)
        colorArray = np.array([getRGBInt(106, 73, 43)])
        image_arr_color = applyColor(image_arr_gray, colorArray[0])
        image_arr = img_32_to_8(image_arr_color)
        requires_shift = False
        x_shift = 0
        y_shift = 0
        cam_angle_1 = width
        cam_angle_2 = height
        # create header
        image_header = get_header_array(width, height, requires_shift,
                                        x_shift, y_shift, cam_angle_1,
                                        cam_angle_2)
        image_data = get_data_write(image_header, image_arr)
        write_sprite("../wall_dirt_brown.dat", image_data)

        # Load data file and save to png file.
        data_read = read_sprite("../wall_dirt_brown.dat")
        header_data = get_header(data_read)
        height = get_height(header_data)
        width = get_width(header_data)
        image_data = get_matrix_from_data(get_image_data(data_read),
                                          width, height)
        image_mat = get_image_matrix(image_data)
        save_image("../wall_dirt_brown.png", image_mat)
"""
