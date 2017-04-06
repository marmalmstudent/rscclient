""" This document has been optimized for 80 columns
--------------------------------------------------------------------------------
"""
import numpy as np
import scipy as sp
import os


def rgb_to_hex(rgb_i16):
    """
    Converts a 16-bit color value to a 24-bit hex string. The colormasks should
    be:
        r_mask = 0b0111110000000000 (0x7c00)
        g_mask = 0b0000001111100000 (0x3e0)
        b_mask = 0b0000000000011111 (0x1f)

    Parameters
    ----------
    rgb_i16 : int
        A 16-bit color value.

    Returns
    -------
    str
        A string containing the 24-bit hex string.
        The format should be "#rrggbb"
    """
    if rgb_i16 < 0:
        rgb = np.array([-(rgb_i16+1)], dtype=np.int16)
        r = ((rgb >> 10) & 0x1f) << 3
        g = ((rgb >> 5) & 0x1f) << 3
        b = (rgb & 0x1f) << 3
        return "#%02x%02x%02x" % (r[0], g[0], b[0])
    else:
        return "#ff00ff"


def rgb_to_float(rgb_i16):
    """
    Convertsa 16-bit color value to a floating point color array. The
    colormasks should be:
        r_mask = 0b0111110000000000 (0x7c00)
        g_mask = 0b0000001111100000 (0x3e0)
        b_mask = 0b0000000000011111 (0x1f)

    Parameters
    ----------
    rgb_i16 : int
        A 16-bit color value.

    Returns
    -------
    list
        An array containing then red, green, blue and alpha floating point
        color values. The format should be [r,g,b,a] and the alpha should be 1.
    """
    if rgb_i16 < 0:
        rgb = np.array([-(rgb_i16+1)], dtype=np.int16)
        r = ((rgb >> 10) & 0x1f)/0x1f
        g = ((rgb >> 5) & 0x1f)/0x1f
        b = (rgb & 0x1f)/0x1f
        return [r[0], g[0], b[0], 1.0]
    return [1.0, 0.0, 1.0, 1.0]


def float_to_rgb(rgb_f64):
    """
    Converts a floating point color array to a 16-bit color value. The
    colormasks should be:
        r_mask = 0b0111110000000000 (0x7c00)
        g_mask = 0b0000001111100000 (0x3e0)
        b_mask = 0b0000000000011111 (0x1f)

    Parameters
    ----------
    rgb_f64 : numpy.ndarray
        An array containing then red, green and blue color values. The format
        should be [r,g,b].

    Returns
    -------
    int
        The 16-bit color value.
    """
    # positive ranges from 0 to 32767 while negative ranges
    # from -32768 to -1 so negating and subtracting is necessary.
    out = -(
        ((rgb_f64[0] & 0x1f) << 10)
        + ((rgb_f64[1] & 0x1f) << 5)
        + (rgb_f64[2] & 0x1f)) - 1
    return out


def find_color(data, offset):
    """
    Extracts the color array from the data string.

    Parameters
    ----------
    data : str
        The string where the faces points is to be extracted from.
    offset : int
        The offset in data where the search will begin from.

    Returns
    -------
    tuple
        list
            A list containing the color of the (sub-)object.
        int
            The offset after extraction.
    """
    i1 = data[offset:].find("color(")
    begin = offset + i1 + 6
    i2 = data[offset:].find(") {")
    end = offset + i2
    if (i1 >= 0 and i2 >= 0):
        color = data[begin:end]
        color_f64 = np.array(list(map(float, color[1:-1].split(", ")))[:-1])
        # convert array from float to 5-bit integer (ranging from 0 to 31)
        color_ui16 = np.array(np.round(color_f64 * 0x1f, 0), dtype=np.uint16)
        return float_to_rgb(color_ui16), end + 3
    return float_to_rgb(np.array([0.5, 0.5, 0.5], dtype=np.uint16)), offset


def find_trans(data, offset):
    """
    Extracts the translation array from the data string.

    Parameters
    ----------
    data : str
        The string where the faces points is to be extracted from.
    offset : int
        The offset in data where the search will begin from.

    Returns
    -------
    tuple
        list
            A list containing the 3-dimensional translation array.
            This format should be [xshift, yshift, zshift]
        int
            The offset after extraction.
    """
    i1 = data[offset:].find("multmatrix(")
    begin = offset + i1 + 11
    i2 = data[offset:].find(") {")
    end = offset + i2
    if (i1 >= 0 and i2 >= 0):
        # matrix was found
        trns_mat = data[begin:end]
        mat_rows = []
        for rows in trns_mat[2:-2].split("], ["):
            list_tmp = list(map(float, rows.split(", ")))
            mat_rows.append(list_tmp[-1])
        return mat_rows[:-1], end + 3
    return None, offset


def find_rot(data, offset):
    """
    Extracts the rotation matrix from the data string.

    Parameters
    ----------
    data : str
        The string where the faces points is to be extracted from.
    offset : int
        The offset in data where the search will begin from.

    Returns
    -------
    tuple
        list
            A list containing the 3-dimensional rotation matrix.
            This matrix is a product of the 3-dimensional rotation matrices
            for rotation about the x-,y-,z-axis.
        int
            The offset after extraction.
    """
    i1 = data[offset:].find("multmatrix(")
    begin = offset + i1 + 11
    i2 = data[offset:].find(") {")
    end = offset + i2
    if (i1 >= 0 and i2 >= 0):
        # matrix was found
        rot_mat = data[begin:end]
        mat_rows = []
        for rows in rot_mat[2:-2].split("], ["):
            list_tmp = list(map(float, rows.split(", ")))
            mat_rows.append(list_tmp[:-1])
        # 15 decimal precision to get rid of floating point errors.
        return np.round(mat_rows[:-1], 15), end + 3
    return None, offset


def find_polyhedron(data, offset):
    """
    Finds the offset where the next polyhedron is located in the data string.

    Parameters
    ----------
    data : str
        The string where the faces points is to be extracted from.
    offset : int
        The offset in data where the search will begin from.

    Returns
    -------
    int
        The offset at the polyhedron.
    """
    data_len = data[offset:].find("polyhedron(")
    return offset + data_len


def find_pts(data, offset):
    """
    Extracts the spatial points from the data string.

    Parameters
    ----------
    data : str
        The string where the faces points is to be extracted from.
    offset : int
        The offset in data where the search will begin from.

    Returns
    -------
    tuple
        list
            A list containing the spacial points.
            The format should be [[x0,y0,z0], [x1,y1,z1], ..., [xN,yN,zN]]
        int
            The offset after extraction.
    """
    i1 = data[offset:].find("points = ")
    begin = offset + i1 + 9
    i2 = data[offset:].find(", faces = ")
    end = offset + i2
    if (i1 >= 0 and i2 >= 0):
        pts_mat = data[begin:end]
        mat_rows = []
        for rows in pts_mat[2:-2].split("], ["):
            list_tmp = list(map(float, rows.split(", ")))
            mat_rows.append(list_tmp)
        return mat_rows, end + 2
    return None, offset


def find_faces(data, offset):
    """
    Extracts the array containing which points forms sides/faces from the data
    string.

    Parameters
    ----------
    data : str
        The string where the faces points is to be extracted from.
    offset : int
        The offset in data where the search will begin from.

    Returns
    -------
    tuple
        list
            A list containing which points forms sides/faces.
        int
            The offset after extraction.
    """
    i1 = data[offset:].find("faces = ")
    start = offset + i1 + 8
    i2 = data[offset:].find(", convexity = ")
    end = offset + i2
    if (i1 >= 0 and i2 >= 0):
        fcs_mat = data[start:end]
        mat_rows = []
        for rows in fcs_mat[2:-2].split("], ["):
            list_tmp = list(map(float, rows.split(", ")))
            mat_rows.append(list_tmp)
        return mat_rows, end + 2
    return None, offset


def insert_data_point(dst_arr, src_arr, appd=True):
    """
    Inserts data points (spatial points) from src_arr to dst_arr. The function
    Ensures that the points that are added are not duplicates.

    Parameters
    ----------
    dst_arr : list
        The destination list.
    src_arr : list
        The sourcs list.
    appd : bool
        True if src_arr should be appended to dst_arr. False if src_arr should
        be extended to dst_arr.

    Returns
    -------
    list
        a list containing the indices in dst_arr where the data from src_arr
        candles be found.
    """
    idx_arr = []
    for point in src_arr:
        if point not in dst_arr:
            if appd:
                dst_arr.append(point)
            else:
                dst_arr.extend(point)
        idx_arr.append(dst_arr.index(point))
    return idx_arr


def read_n_points(data, offset):
    """
    Extracts information about how many spatial points that the object has.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.

    Returns
    -------
    tuple
        int
            The number of spatial points that the object has.
        int
            The offset after extraction.
    """
    data_len = 2
    n_points = data[offset:offset + data_len]
    n_points.dtype = np.uint16
    n_points = n_points.byteswap()
    return n_points[0], offset + data_len


def read_n_sides(data, offset):
    """
    Extracts information about how many sides/faces that the object has.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.

    Returns
    -------
    tuple
        int
            The number of sides/faces that the object has.
        int
            The offset after extraction.
    """
    data_len = 2
    n_sides = data[offset:offset + data_len]
    n_sides.dtype = np.uint16
    n_sides = n_sides.byteswap()
    return n_sides[0], offset + data_len


def read_data_points(data, offset, n_points):
    """
    Extracts the spatial points that makes up the object.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.
    n_points : int
        The number of spatial points that makes up the object.

    Returns
    -------
    tuple
        numpy.ndarray
            An array containing the spatial points that makes up the object.
            The format should be [x, z, y], where x,y,z are arrays that contain
            the x-,y-,z-points respectively.
        int
            The offset after extraction.
    """
    data_len = 6*n_points
    data_points = data[offset:offset + data_len]
    data_points.dtype = sp.int16
    data_points = data_points.byteswap()
    return sp.reshape(data_points, (3, n_points)), offset + data_len


def read_points_per_cell(data, offset, n_sides):
    """
    Extracts information about how many points that makes up each side/face.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.
    n_sides : int
        The number of sides/faces that the object has.

    Returns
    -------
    tuple
        numpy.ndarray
            An array containing information about how many points that makes up
            each side/face.
        int
            The offset after extraction.
    """
    data_len = n_sides
    return data[offset:offset + data_len], offset + data_len


def read_surface_color(data, offset, n_sides):
    """
    Extracts the surface color of the object.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.
    n_sides : int
        The number of sides/faces that the object has.

    Returns
    -------
    tuple
        numpy.ndarray
            An array containing the 16-bit color values of the object.
        int
            The offset after extraction.
    """
    data_len = 2*n_sides
    surface_color = data[offset:offset + data_len]
    surface_color.dtype = sp.int16
    surface_color = surface_color.byteswap()
    return surface_color, offset + data_len


def read_some_array(data, offset, n_sides):
    """
    Extracts some_array from the data. The purpose of this array is unknown.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.
    n_sides : int
        The number of sides/faces that the object has.

    Returns
    -------
    tuple
        numpy.ndarray
            An array containing values whose purpose is unknown.
        int
            The offset after extraction.
    """
    data_len = n_sides
    return data[offset:offset + data_len], offset + data_len


def read_cell_data(data, offset, n_points, n_sides, points_per_cell):
    """
    Extracts information about which spatial coordinates that forms each
    side/face.

    Parameters
    ----------
    data : numpy.narray
        An 8-bit unsigned integer array containing the data from the ob3 file.
    offset : int
        The offset in the data array where the extraction will begin.
    n_points : int
        The number of spatial points that makes up the object.
    n_sides : int
        The number of sides/faces that the object has.
    points_per_cell : numpy.ndarray
        An array containing information about how many spatial points that
        forms each side/face.

    Returns
    -------
    tuple
        list
            A list containing lists of which spatial points that forms each
            side/face.
            The format should be [s0p0, s0p1, ..., s0pK0, s1p0, s1p1, ...,
            s1pK1, sNp0, sNp1, ..., sNpKN]
        int
            The offset after extraction.
    """
    cell_array = []
    for i in range(0, n_sides, 1):
        tmpArray = np.empty(points_per_cell[i], dtype=np.int32)
        if (n_points < 256):
            tmpArray = data[offset:offset+points_per_cell[i]]
            offset += points_per_cell[i]
        else:
            tmp_array2 = np.empty(2*points_per_cell[i], dtype=np.uint8)
            tmp_array2 = data[offset:offset+2*points_per_cell[i]]
            offset += 2*points_per_cell[i]
            tmp_array2.dtype = sp.int16
            tmpArray = tmp_array2.byteswap()
        cell_array.append(tmpArray.tolist())
    return cell_array, offset


def get_data(data):
    """
    Extracts the information from the data from an ob3 file.

    Parameters
    ----------
    data : numpy.ndarray
        An 8-bit unsigned integer array containing the data from the ob3 file.

    Returns
    -------
    tuple
        numpy.ndarray
            See parameter n_points in function format_ob3
        numpy.ndarray
            See parameter n_sides in function format_ob3
        numpy.ndarray
            See parameter data_points in function format_ob3
        numpy.ndarray
            See parameter points_per_cell in function format_ob3
        numpy.ndarray
            See parameter surface_color1 in function format_ob3
        numpy.ndarray
            See parameter surface_color2 in function format_ob3
        numpy.ndarray
            See parameter some_array in function format_ob3
        numpy.ndarray
            See parameter cell_array in function format_ob3
    """
    offset = 0
    n_points, offset = read_n_points(data, offset)
    n_sides, offset = read_n_sides(data, offset)
    data_points, offset = read_data_points(data, offset, n_points)
    points_per_cell, offset = read_points_per_cell(data, offset, n_sides)
    surface_color1, offset = read_surface_color(data, offset, n_sides)
    surface_color2, offset = read_surface_color(data, offset, n_sides)
    some_array, offset = read_some_array(data, offset, n_sides)
    cell_array, offset = read_cell_data(data, offset, n_points, n_sides,
                                        points_per_cell)
    return (n_points, n_sides, data_points, points_per_cell,
            surface_color1, surface_color2, some_array, cell_array)


def write_to_csg(obj_id, data_points, surface_color1,
                 surface_color2, cell_array):
    """
    Writes the data to a csg file.

    Parameters
    ----------
    obj_id : int
        index of the csg file, e.g. object500.csg would be 500.
    data_points : numpy.ndarray
        A 16-bit signed integer array of dimension (3, n_points).
        The array should contain then spatial coordinate points in then order
        [x, y, z].
    surface_color1 : numpy.ndarray
        A 16-bit signed integer array of size n_sides.
        The array should contain information about the color of the "upside"
        of each side/face.
    surface_color2 : numpy.ndarray
        A 16-bit signed integer array of size n_sides.
        The array should contain information about the color of the "downside"
        of each side/face.
    cell_array : list
        The list should contain information about which points creates each
        side/face.
        The format should be [s0p0, s0p1, ..., s0pK0, s1p0, s1p1, ..., s1pK1,
        sNp0, sNp1, ..., sNpKN]
    """
    with open("object"+str(obj_id)+".csg", "w") as f:
        f.write("group() {\n")
        for i in range(len(cell_array)):
            if (surface_color1[i] != 0x7fff):
                color_write = rgb_to_float(surface_color1[i])
            else:
                color_write = rgb_to_float(surface_color2[i])

            f.write("\tgroup() {\n\t\tcolor(" + str(color_write)
                    + ") {\n\t\t\tpolyhedron(points = ")

            points_write = []
            for j in cell_array[i]:
                points_write.append([
                    data_points[0][j],
                    data_points[2][j],
                    data_points[1][j]])

            f.write(str(points_write))
            f.write(", faces = ")
            f.write(str([[0, 1, 2]]))
            f.write(", convexity = 1);\n\t\t}\n\t}\n")
        f.write("}")


def load_csg(data):
    """
    Extracts the arrays from the data contained in a a csg file.

    Parameters
    ----------
    data : str
        The data contained in the csg file.

    Returns
    -------
    tuple
        list
            See parameter color_array in function prepare_ob3
        list
            See parameter pts_array in function prepare_ob3
        list
            See parameter fcs_array in function prepare_ob3
        list
            See parameter n_fcs_per_obj in function prepare_ob3
    """
    color_array = []
    pts_array = []
    fcs_array = []
    n_fcs_per_obj = []
    for grps in data.split("group() {"):
        offset = 0
        color_val, offset = find_color(grps, offset)
        trns, offset = find_trans(grps, offset)
        rot, offset = find_rot(grps, offset)
        offset = find_polyhedron(grps, offset)
        pts, offset = find_pts(grps, offset)
        fcs, offset = find_faces(grps, offset)
        if pts is None or fcs is None:
            continue
        pts = np.array(pts)

        # append color
        color_array.append(color_val)

        # append data points
        for j in range(len(pts)):
            if rot is not None:
                pts[j] = np.transpose(
                    np.dot(rot, np.transpose([pts[j]])))[0]
            if trns is not None:
                pts[j] = np.round(pts[j] + np.array(trns))
        # only insert points if they already exist
        pts_translate_arr = insert_data_point(pts_array, pts.tolist())

        # translate points indices from the inner group to outer group
        fcs = np.array(fcs, dtype=np.uint8)
        for j in range(len(fcs)):
            for k in range(len(fcs[j])):
                fcs[j][k] = pts_translate_arr[fcs[j][k]]
        fcs_array.extend(fcs.tolist())
        n_fcs_per_obj.append(len(fcs.tolist()))
    return color_array, pts_array, fcs_array, n_fcs_per_obj


def prepare_ob3(color_array, pts_array, fcs_array, n_fcs_per_obj):
    """
    Takes the arrays extracted from a csg file and prepares the data to be
    formated (using format_ob3) into an array that can be written to a ob3
    file.

    Parameters
    ----------
    color_array : list
        A list containing the 16-bit color of each of the objects that makes up
        the entire object (i.e. any pyramid, prism, diamond). The colormasks
        should be:
            r_mask = 0b0111110000000000 (0x7c00)
            g_mask = 0b0000001111100000 (0x3e0)
            b_mask = 0b0000000000011111 (0x1f)
        The format should be [rgb0, rgb1, ..., rgbM]
    pts_array : list
        A list containing the spatial coordinates of each point used by the
        object. Duplicate points should be avoided.
        The format should be [[x0,y0,z0], [x1,y1,z1], ..., [xN,yN,zN]]
    fcs_array : list
        A list containing which points (i.e. indices) from pts_array that
        forms the sides/faces.
        The format should be [s0p0, s0p1, ..., s0pK0, s1p0, s1p1, ..., s1pK1,
        sNp0, sNp1, ..., sNpKN]
    n_fcs_per_obj : list
        A list containing information about how many points that forms each
        side/face. The format should be [K0, K1, ..., KN]

    Returns
    -------
    tuple
        numpy.ndarray
            See parameter n_points in function format_ob3
        numpy.ndarray
            See parameter n_sides in function format_ob3
        numpy.ndarray
            See parameter data_points in function format_ob3
        numpy.ndarray
            See parameter points_per_cell in function format_ob3
        numpy.ndarray
            See parameter surface_color1 in function format_ob3
        numpy.ndarray
            See parameter surface_color2 in function format_ob3
        numpy.ndarray
            See parameter some_array in function format_ob3
        numpy.ndarray
            See parameter cell_array in function format_ob3
    """
    # The data points
    xyz_arr = np.transpose(pts_array)
    xzy_arr = np.array([xyz_arr[0], xyz_arr[2], xyz_arr[1]])
    data_points = np.array(xzy_arr, dtype=np.int16)

    # Number of points and sides
    n_points = np.array([np.size(data_points, axis=1)],
                        dtype=np.uint16)
    n_sides = np.array([np.size(fcs_array, axis=0)], dtype=np.uint16)

    # Number of points to construct each side
    points_per_cell = np.empty(n_sides[0], dtype=np.uint8)
    ppc_i = 0
    for n_fcs in fcs_array:
        points_per_cell[ppc_i] = len(n_fcs)
        ppc_i += 1

    # The Colors for each side
    # outer side
    surface_color1 = np.empty(len(points_per_cell), dtype=np.int16)
    fc_idx = 0
    for j in range(len(n_fcs_per_obj)):
        for k in range(n_fcs_per_obj[j]):
            # set color for each side for each object
            surface_color1[fc_idx] = color_array[j]
            fc_idx += 1
    # inner side
    surface_color2 = 0x7fff*np.ones(len(surface_color1), dtype=np.int16)

    # Which points belong to what surface. The order depends on how
    # many points belong to the side, e.g. if points_per_cell[0] = 5
    # then the first five points are that surface. In the same way
    # if points_per_cell[1] = 3 the next 3 points are that surface.
    if (np.size(data_points, axis=1) < 0x100):
        cell_array = np.array(np.reshape(
            fcs_array, (-1, np.size(fcs_array)))[0], dtype=np.uint8)
    else:
        cell_array = np.array(np.reshape(
            fcs_array, (-1, np.size(fcs_array)))[0], dtype=np.int16)

    # some array, this does not seem to do anything but most of the
    # time the elements are non-zero.
    some_array = np.ones(n_sides[0], dtype=np.uint8)
    return (n_points, n_sides, data_points, points_per_cell,
            surface_color1, surface_color2, some_array, cell_array)


def format_ob3(
        n_points, n_sides, data_points, points_per_cell,
        surface_color1, surface_color2, some_array, cell_array):
    """
    Formats the data into an array of unsigned 8-bit integers. The data needs
    to be prepared (using prepare_ob3) before it can be formated.

    Parameters
    ----------
    n_points : numpy.ndarray
        A big endian 16-bit unsigned integer array of size 1. The array
        contains a value that represents the number of data points that are
        used in this object.
    n_sides : numpb.ndarray
        A big endian 16-bit unsigned integer array of size 1. The array
        containg a values that represents then number of sides/faces that the
        object has.
    data_points : numpy.ndarray
        A big endian 16-bit signed integer array of dimension (3, n_points).
        The array should contain then spatial coordinate points in then order
        [x, z, y].
    points_per_cell : numpy.ndarray
        An 8-bit unsigned integer array of size n_sides.
        The array should contain information about how many data points that
        creates each side/face.
    surface_color1 : numpy.ndarray
        A big endian 16-bit signed integer array of size n_sides.
        The array should contain information about the color of the "upside"
        of each side/face.
    surface_color2 : numpy.ndarray
        A big endian 16-bit signed integer array of size n_sides.
        The array should contain information about the color of the "downside"
        of each side/face.
    some_array : numpy.ndarray
        An 8-bit unsigned integer array of size n_sides. The purpose of this
        array is unknown but it is safe to set all elements to 1.
    cell_array : numpy.ndarray
        A big endian 16-bit or 8-bit unsigned integer array which should have
        a size equal to sum(points_per_cell*n_sides). The array should contain
        information about which points creates each side/face.

    Returns
    -------
    numpy.ndarray
       An 8-bit unsigned integer array containing the concatenation of the
       input arrays (in order of appearace), after they have been converted
       into little endian and typecasted to 8-bit unsigned integer arrays.
       This array is ready to be converted into a bytearray and written to
       an ob3 file.
    """
    out_array = np.empty(0, dtype=np.uint8)

    n_points = n_points.byteswap()
    n_points.dtype = np.uint8
    out_array = np.hstack((out_array, n_points))

    n_sides = n_sides.byteswap()
    n_sides.dtype = np.uint8
    out_array = np.hstack((out_array, n_sides))

    data_points = np.reshape(data_points,
                             (-1, np.size(data_points)))[0]
    data_points = data_points.byteswap()
    data_points.dtype = np.uint8
    out_array = np.hstack((out_array, data_points))

    out_array = np.hstack((out_array, points_per_cell))

    surface_color1 = surface_color1.byteswap()
    surface_color1.dtype = np.uint8
    out_array = np.hstack((out_array, surface_color1))

    surface_color2 = surface_color2.byteswap()
    surface_color2.dtype = np.uint8
    out_array = np.hstack((out_array, surface_color2))

    # some array
    out_array = np.hstack((out_array, some_array))

    cell_array = cell_array.byteswap()
    cell_array.dtype = np.uint8
    out_array = np.hstack((out_array, cell_array))
    return out_array


def ob3tocsg(start_file, max_files):
    """
    Convert .ob3 files (readable by the client) to .csg files (readable by
    OpenSCAD)

    Parameters
    ----------
    start_file : int
        index of first csg file, e.g. object500.ob3 would be 500.
    max_files : int
        The number of files (offset from start_file) to be converted
    """
    for i in range(start_file, start_file + max_files):
        if (os.path.isfile("object"+str(i)+".ob3")):
            print("Processing file %d of %d (%.1f%%)"
                  % (i, max_files, 100*(i-start_file)/max_files))
            with open("object"+str(i)+".ob3", "rb") as f:
                data_read = sp.array(bytearray(f.read()), dtype=sp.uint8)
            data = get_data(data_read)
            write_to_csg(i, data[2], data[4], data[5], data[7])


def csgtoob3(start_file, max_files):
    """
    Convert .csg files (readable by OpenSCAD) to .ob3 files (readable by the
    client)

    Parameters
    ----------
    start_file : int
        index of first csg file, e.g. object500.csg would be 500.
    max_files : int
        The number of files (offset from start_file) to be converted
    """
    for i in range(start_file, start_file + max_files):
        if (os.path.isfile("object"+str(i)+".csg")):
            print("Processing file %d of %d (%.1f%%)"
                  % (i, max_files, 100*(i-start_file)/max_files))
            with open("object"+str(i)+".csg", "r") as f:
                data_read = f.read()
            data = load_csg(data_read)
            data_2 = prepare_ob3(data[0], data[1], data[2], data[3])
            out_array = format_ob3(data_2[0], data_2[1], data_2[2], data_2[3],
                                   data_2[4], data_2[5], data_2[6], data_2[7])
            with open("object"+str(i)+".ob3", "wb") as f:
                f.write(out_array.tobytes())


if __name__ == "__main__":
    csgtoob3(500, 2)
    ob3tocsg(500, 2)
