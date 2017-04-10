import bpy
import numpy as np


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


def write_obj(filepath):
    """
    convert blender models to .ob3 file format
    TODO: make a similar function that converts models to landscape format.
    """
    sce = bpy.context.scene
    ob = sce.objects

    n_points = 0
    n_faces = 0
    data_points = []
    surface_color1 = []
    surface_color2 = []
    points_per_cell = []
    cell_array = []
    for key, val in enumerate(ob):
        rot_scale_mat = np.array(val.matrix_world)[0:3,0:3]
        trans_vec = np.array(val.matrix_world)[0:3,-1]
        this_coords = []
        for vert in val.data.vertices:
            vtc = np.array([vert.co.x, vert.co.y, vert.co.z])
            crds = np.dot(rot_scale_mat, vtc)
            crds += trans_vec
            crds = np.array(np.round(crds), dtype=np.int16).tolist()
            this_coords.append(crds)
        fcs_lst = insert_data_point(data_points, this_coords)
        cell_array.extend(fcs_lst)
        points_per_cell.append(len(fcs_lst))
        # color
        mtl_slot = val.material_slots
        color_val = []
        for mtl_key, mtl_val in mtl_slot.items():
            r = int(round(mtl_val.material.diffuse_color.r*31))
            g = int(round(mtl_val.material.diffuse_color.g*31))
            b = int(round(mtl_val.material.diffuse_color.b*31))
            color_val = ((r & 0x1f) << 10) + ((g & 0x1f) << 5) + (b & 0x1f)
            color_val = np.array(-color_val-1, dtype=np.int16).tolist()
        surface_color1.append(color_val)
        surface_color2.append(0x7fff)
    data_points = np.array(np.transpose(data_points))
    n_points = np.array([np.size(data_points, axis=1)], dtype=np.uint16)
    n_faces = np.array([np.size(points_per_cell)], dtype=np.uint16)
    x = data_points[0]
    y = data_points[1]
    z = data_points[2]
    data_points = np.reshape(np.array([x, z, y], dtype=np.int16), (-1, np.size(data_points)))[0]
    points_per_cell = np.array(points_per_cell, dtype=np.uint8)
    surface_color1 = np.array(surface_color1, dtype=np.int16)
    surface_color2 = np.array(surface_color2, dtype=np.int16)
    some_array = np.ones(n_faces, dtype=np.uint8)
    cell_array = np.reshape(cell_array, (-1, np.size(cell_array)))[0]
    if (n_faces < 256):
        cell_array = np.array(cell_array, dtype=np.uint8)
    else:
        cell_array = np.array(cell_array, dtype=np.uint16)
    """
    print(n_points, n_points.dtype)
    print(n_faces, n_faces.dtype)
    print(data_points, data_points.dtype)
    print(points_per_cell, points_per_cell.dtype)
    print(surface_color1, surface_color1.dtype)
    print(surface_color2, surface_color2.dtype)
    print(some_array, some_array.dtype)
    print(cell_array, cell_array.dtype)
    """
    n_points = n_points.byteswap()
    n_points.dtype = np.uint8
    n_faces = n_faces.byteswap()
    n_faces.dtype = np.uint8
    data_points = data_points.byteswap()
    data_points.dtype = np.uint8
    surface_color1 = surface_color1.byteswap()
    surface_color1.dtype = np.uint8
    surface_color2 = surface_color2.byteswap()
    surface_color2.dtype = np.uint8
    cell_array = cell_array.byteswap()
    cell_array.dtype = np.uint8
    out_array = np.empty(0, dtype=np.uint8)
    out_array = np.hstack((out_array, n_points))
    out_array = np.hstack((out_array, n_faces))
    out_array = np.hstack((out_array, data_points))
    out_array = np.hstack((out_array, points_per_cell))
    out_array = np.hstack((out_array, surface_color1))
    out_array = np.hstack((out_array, surface_color2))
    out_array = np.hstack((out_array, some_array))
    out_array = np.hstack((out_array, cell_array))
    data_len = np.array([np.size(out_array)], dtype=np.uint32)
    data_len = data_len.byteswap()
    data_len.dtype = np.uint8
    out_array = np.hstack((data_len, out_array))
    with open(filepath, "wb") as f:
        f.write(out_array.tobytes())


write_obj("test.obj")
