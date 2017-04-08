"""
--------------------------------------------------------------------------------
"""
import numpy as np
import os


landscape_path = "../cache/Landscape/"
def rgb_to_float(r, g, b):
    return np.array([(r & 0xff) / 255,
                     (g & 0xff) / 255,
                     (b & 0xff) / 255,
                     1.0], dtype=np.float64)


def rgb_to_int32(r, g, b):
    return ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff)


def int32_to_float(rgb):
    rgb = np.array(rgb, dtype=np.uint32)
    float_val =  np.array([((rgb >> 16) & 0xff) / 255,
                           ((rgb >> 8) & 0xff) / 255,
                           (rgb & 0xff) / 255,
                           np.ones(len(rgb), dtype=np.float64)], dtype=np.float64)
    if (type(rgb) == np.ndarray or type(rgb) == np.ndarray == list):
        return np.transpose(float_val).tolist()
    return float_val.tolist()


def ground_color_array_float():
    array = np.empty((256, 4), dtype=np.float64)
    for i in range(0, 64, 1):
        array[i] = rgb_to_float(255 - i*4, 255 - int(i*1.75), 255 - i*4)
        array[i + 64] = rgb_to_float(i*3, 144, 0)
        array[i + 128] = rgb_to_float(192 - int(i*1.5), 144 - int(i*1.5), 0)
        array[i + 192] = rgb_to_float(96 - int(i*1.5), 48 + int(i*1.5), 0)
    return array


def ground_color_array_int32():
    array = np.empty(256, dtype=np.float64)
    for i in range(0, 64, 1):
        array[i] = rgb_to_int32(255 - i*4, 255 - int(i*1.75), 255 - i*4)
        array[i + 64] = rgb_to_int32(i*3, 144, 0)
        array[i + 128] = rgb_to_int32(192 - int(i*1.5), 144 - int(i*1.5), 0)
        array[i + 192] = rgb_to_int32(96 - int(i*1.5), 48 + int(i*1.5), 0)
    return array


def ground_elevation(elev_data):
    return elev_data.tolist()


def ground_texture_float(txtr_data):
    color_array = ground_color_array_float()
    color_list = []
    for i in range(len(txtr_data)):
        color_list.append(color_array[txtr_data[i]].tolist())
    return color_list


def ground_texture_int32(txtr_data):
    color_array = ground_color_array_int32()
    color_list = []
    for i in range(len(txtr_data)):
        color_list.append(color_array[txtr_data[i]])
    return color_list

def ground_texture(txtr_data, use_float=True):
    return ground_texture_float(txtr_data) if use_float else ground_texture_int32(txtr_data)


def ground_overlay(ovrly_data):
    return ovrly_data.tolist()


def roof_texture(rftxtr_data):
    return rftxtr_data.tolist()


def horiz_wall(hwall_data):
    return hwall_data.tolist()


def vert_wall(vwall_data):
    return vwall_data.tolist()


def diag_wall(dwall_data):
    dwall_data = np.reshape(np.transpose(dwall_data),
                            (-1, np.size(dwall_data)))[0]
    dwall_data.dtype = np.int32
    return dwall_data.tolist()

def write_to_csg(h, x, y, elevation, texture, use_translate=True):
    """
    Writes the data to a csg file.
    """
    with open(landsc_dst + "h" + str(h) + "x" + str(x) + "y" + str(y) + ".csg", "w") as f:
        f.write("group() {\n")
        if (not use_translate):
            x = 0
            y = 0
        for i in range(map_size-1):
            for j in range(map_size-1):
                color_write = texture[i*map_size + j]
                f.write("\tgroup() {\n\t\tcolor(" + str(color_write)
                        + ") {\n\t\t\tpolyhedron(points = ")
                points_write = [
                    [(x*(map_size-1) + i)*compression,
                     (y*(map_size-1) + j)*compression,
                     h + elevation[i*map_size + j]],
                    [(x*(map_size-1) + i)*compression,
                     (y*(map_size-1) + j+1)*compression,
                     h + elevation[i*map_size + (j+1)]],
                    [(x*(map_size-1) + i+1)*compression,
                     (y*(map_size-1) + j)*compression,
                     h + elevation[(i+1)*map_size + j]],
                    [(x*(map_size-1) + i+1)*compression,
                     (y*(map_size-1) + j+1)*compression,
                     h + elevation[(i+1)*map_size + (j+1)]]]
                # print(points_write)
                f.write(str(points_write))
                f.write(", faces = ")
                faces_array = [[0, 1, 2], [2, 1, 3]]
                f.write(str(faces_array))
                f.write(", convexity = 1);\n\t\t}\n\t}\n")
        f.write("}")


def avg_color_int(data):
    data = np.array(np.reshape(data, (-1, np.size(data)))[0], dtype=np.uint32)
    r = (data >> 16) & 0xff
    g = (data >> 8) & 0xff
    b = data & 0xff
    r_avg = int(round(np.average(r)))
    g_avg = int(round(np.average(g)))
    b_avg = int(round(np.average(b)))
    return ((r_avg & 0xff) << 16) + ((g_avg & 0xff) << 8) + (b_avg & 0xff)


def landscape_to_csg(h_start, h_len, x_start, x_len, y_start, y_len):
    max_ovrly = 0
    min_ovrly = 0
    for i in range(h_start, h_start + h_len):
        for j in range(x_start, x_start + x_len):
            for k in range(y_start, y_start + y_len):
                print("Processing file h%dx%dy%d (%4d/%4d, %3d%%)"
                      % (i, j, k, (i-h_start)*x_len*y_len + (j-x_start)*y_len + (k-y_start),
                         h_len*x_len*y_len,
                         int(100*((i-h_start)*x_len*y_len + (j-x_start)*y_len + (k-y_start))/(h_len*x_len*y_len))))
                with open(landsc_src + "h" + str(i) + "x" + str(j) + "y" + str(k), "rb") as f:
                    data_read = np.array(bytearray(f.read()), dtype=np.uint8)
                data_mat = np.transpose(
                    np.reshape(data_read, (orig_map_size*orig_map_size, 10)))
                """
                elevation = ground_elevation(data_mat[0])
                texture = ground_texture(data_mat[1], use_float=False)
                texture = int32_to_float(texture)
                """
                elevation = ground_elevation(data_mat[0])
                texture = ground_texture(data_mat[1], use_float=False)
                overlay = ground_overlay(data_mat[2])

                elev_mat = np.reshape(elevation, (orig_map_size, orig_map_size))
                txtr_mat = np.reshape(texture, (orig_map_size, orig_map_size))
                ovrly_mat = np.reshape(overlay, (orig_map_size, orig_map_size))
                new_elev = np.empty((map_size, map_size), dtype=np.int32)
                new_txtr = np.empty((map_size, map_size), dtype=np.int32)
                new_ovrly = np.empty((map_size, map_size), dtype=np.int32)
                for l in range(0, map_size, 1):
                    for m in range(0, map_size, 1):
                        new_elev[l, m] = int(np.average(
                            elev_mat[l*compression:(l+1)*compression,
                                     m*compression:(m+1)*compression]))
                        new_txtr[l, m] = avg_color_int(
                            txtr_mat[l*compression:(l+1)*compression,
                                     m*compression:(m+1)*compression])
                        # set the overlay to the most occuring overlay in the
                        # compression area.
                        new_ovrly[l, m] = np.argmax(np.bincount(np.reshape(
                            ovrly_mat[l*compression:(l+1)*compression,
                                      m*compression:(m+1)*compression],
                            (-1, compression*compression))[0]))

                elevation = np.reshape(new_elev/32, (-1, np.size(new_elev)))[0]
                texture = np.reshape(new_txtr, (-1, np.size(new_elev)))[0]
                overlay = np.reshape(new_ovrly, (-1, np.size(new_elev)))[0]

                # Ugly but it gets the job done
                texture = (texture*(overlay == 0)
                           + 0x808080*(overlay == 1)
                           + 0x647cb4*(overlay == 2)
                           + 0x5a351c*(overlay == 3)
                           + 0x606060*(overlay == 5)
                           + 0x720410*(overlay == 6)
                           + 0x36856a*(overlay == 7)
                           + 0x202020*(overlay == 8)
                           + 0xb0b0b0*(overlay == 9)
                           + 0xe0541b*(overlay == 11)
                           + 0x103556*(overlay == 13)
                           + 0x250021*(overlay == 15)
                           + 0xe0e0e0*(overlay == 17)
                           + 0x624500*(overlay == 23)
                           + 0x382004*(overlay == 24)
                           + (0xff00ff * (overlay != 2) * (overlay != 1)
                              * (overlay != 11) * (overlay != 250)
                              * (overlay != 0) * (overlay != 3)
                              * (overlay != 4) * (overlay != 9)
                              * (overlay != 7) * (overlay != 6)
                              * (overlay != 13) * (overlay != 23)
                              * (overlay != 15) * (overlay != 8)
                              * (overlay != 24)))
                # print(new_ovrly)

                texture = int32_to_float(texture)

                rooftxtr = roof_texture(data_mat[3])
                hwall = horiz_wall(data_mat[4])
                vwall = vert_wall(data_mat[5])
                dwall = diag_wall(data_mat[6:10])
                write_to_csg(i, j, k, elevation.tolist(), texture, use_translate=True)


landsc_src = "../Landscape/"
landsc_dst = "landscape/"
orig_map_size = 48
# Available compression: 1, 2, 3, 4, 6, 8, 12, 16, 24, 48,
compression = 1
map_size = int(orig_map_size / compression)
if __name__ == "__main__":
    landscape_to_csg(0, 4, 48, 21, 37, 21)
    # landscape_to_csg(0, 1, 56, 1, 40, 1)
