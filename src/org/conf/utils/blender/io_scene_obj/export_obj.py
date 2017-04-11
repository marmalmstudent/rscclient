# ##### BEGIN GPL LICENSE BLOCK #####
#
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software Foundation,
#  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
#
# ##### END GPL LICENSE BLOCK #####

# <pep8 compliant>

import os
import numpy as np
import bpy
import mathutils
import bpy_extras.io_utils

from progress_report import ProgressReport, ProgressReportSubstep


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


def name_compat(name):
    if name is None:
        return 'None'
    else:
        return name.replace(' ', '_')


def mesh_triangulate(me):
    import bmesh
    bm = bmesh.new()
    bm.from_mesh(me)
    bmesh.ops.triangulate(bm, faces=bm.faces)
    bm.to_mesh(me)
    bm.free()


def write_mtl(scene, filepath, path_mode, copy_set, mtl_dict):
    from mathutils import Color, Vector

    world = scene.world
    if world:
        world_amb = world.ambient_color
    else:
        world_amb = Color((0.0, 0.0, 0.0))

    source_dir = os.path.dirname(bpy.data.filepath)
    dest_dir = os.path.dirname(filepath)
    mtl_color_dict = {}
    mtl_texture_dict = {}
    with open(filepath, "w", encoding="utf8", newline="\n") as f:
        fw = f.write

        mtl_dict_values = list(mtl_dict.values())
        mtl_dict_values.sort(key=lambda m: m[0])

        # Write material/image combinations we have used.
        # Using mtl_dict.values() directly gives un-predictable order.
        for mtl_mat_name, mat, face_img in mtl_dict_values:
            print(mtl_mat_name, mat, face_img)
            # Get the Blender data for the material and the image.
            # Having an image named None will make a bug, dont do it :)

            fw('\nnewmtl %s\n' % mtl_mat_name)  # Define a new material: matname_imgname

            if mat:
                use_mirror = mat.raytrace_mirror.use and mat.raytrace_mirror.reflect_factor != 0.0

                # convert from blenders spec to 0 - 1000 range.
                if mat.specular_shader == 'WARDISO':
                    tspec = (0.4 - mat.specular_slope) / 0.0004
                else:
                    tspec = (mat.specular_hardness - 1) / 0.51
                fw('Ns %.6f\n' % tspec)
                del tspec

                # Ambient
                if use_mirror:
                    fw('Ka %.6f %.6f %.6f\n' % (mat.raytrace_mirror.reflect_factor * mat.mirror_color)[:])
                else:
                    fw('Ka %.6f %.6f %.6f\n' % (mat.ambient, mat.ambient, mat.ambient))  # Do not use world color!
                fw('Kd %.6f %.6f %.6f\n' % (mat.diffuse_intensity * mat.diffuse_color)[:])  # Diffuse
                mtl_color_dict.update({mtl_mat_name: (mat.diffuse_intensity * mat.diffuse_color)[:]})
                fw('Ks %.6f %.6f %.6f\n' % (mat.specular_intensity * mat.specular_color)[:])  # Specular
                # Emission, not in original MTL standard but seems pretty common, see T45766.
                # XXX Blender has no color emission, it's using diffuse color instead...
                fw('Ke %.6f %.6f %.6f\n' % (mat.emit * mat.diffuse_color)[:])
                if hasattr(mat, "raytrace_transparency") and hasattr(mat.raytrace_transparency, "ior"):
                    fw('Ni %.6f\n' % mat.raytrace_transparency.ior)  # Refraction index
                else:
                    fw('Ni %.6f\n' % 1.0)
                fw('d %.6f\n' % mat.alpha)  # Alpha (obj uses 'd' for dissolve)

                # See http://en.wikipedia.org/wiki/Wavefront_.obj_file for whole list of values...
                # Note that mapping is rather fuzzy sometimes, trying to do our best here.
                if mat.use_shadeless:
                    fw('illum 0\n')  # ignore lighting
                elif mat.specular_intensity == 0:
                    fw('illum 1\n')  # no specular.
                elif use_mirror:
                    if mat.use_transparency and mat.transparency_method == 'RAYTRACE':
                        if mat.raytrace_mirror.fresnel != 0.0:
                            fw('illum 7\n')  # Reflection, Transparency, Ray trace and Fresnel
                        else:
                            fw('illum 6\n')  # Reflection, Transparency, Ray trace
                    elif mat.raytrace_mirror.fresnel != 0.0:
                        fw('illum 5\n')  # Reflection, Ray trace and Fresnel
                    else:
                        fw('illum 3\n')  # Reflection and Ray trace
                elif mat.use_transparency and mat.transparency_method == 'RAYTRACE':
                    fw('illum 9\n')  # 'Glass' transparency and no Ray trace reflection... fuzzy matching, but...
                else:
                    fw('illum 2\n')  # light normaly

            else:
                # Write a dummy material here?
                fw('Ns 0\n')
                fw('Ka %.6f %.6f %.6f\n' % world_amb[:])  # Ambient, uses mirror color,
                fw('Kd 0.8 0.8 0.8\n')
                fw('Ks 0.8 0.8 0.8\n')
                fw('d 1\n')  # No alpha
                fw('illum 2\n')  # light normaly

            # Write images!
            if face_img:  # We have an image on the face!
                filepath = face_img.filepath
                if filepath:  # may be '' for generated images
                    # write relative image path
                    filepath = bpy_extras.io_utils.path_reference(filepath, source_dir, dest_dir,
                                                                  path_mode, "", copy_set, face_img.library)
                    fw('map_Kd %s\n' % filepath)  # Diffuse mapping image
                    # NOTE: THIS HAS NOT BEEN TESTED!!
                    mtl_texture_dict.update({mtl_mat_name: filepath.split("/")[-1]})
                    del filepath
                else:
                    # so we write the materials image.
                    face_img = None

            if mat:  # No face image. if we havea material search for MTex image.
                image_map = {}
                # backwards so topmost are highest priority
                for mtex in reversed(mat.texture_slots):
                    if mtex and mtex.texture and mtex.texture.type == 'IMAGE':
                        image = mtex.texture.image
                        if image:
                            # texface overrides others
                            if (mtex.use_map_color_diffuse and (face_img is None) and
                                (mtex.use_map_warp is False) and (mtex.texture_coords != 'REFLECTION')):
                                image_map["map_Kd"] = (mtex, image)
                            if mtex.use_map_ambient:
                                image_map["map_Ka"] = (mtex, image)
                            # this is the Spec intensity channel but Ks stands for specular Color
                            '''
                            if mtex.use_map_specular:
                                image_map["map_Ks"] = (mtex, image)
                            '''
                            if mtex.use_map_color_spec:  # specular color
                                image_map["map_Ks"] = (mtex, image)
                            if mtex.use_map_hardness:  # specular hardness/glossiness
                                image_map["map_Ns"] = (mtex, image)
                            if mtex.use_map_alpha:
                                image_map["map_d"] = (mtex, image)
                            if mtex.use_map_translucency:
                                image_map["map_Tr"] = (mtex, image)
                            if mtex.use_map_normal:
                                image_map["map_Bump"] = (mtex, image)
                            if mtex.use_map_displacement:
                                image_map["disp"] = (mtex, image)
                            if mtex.use_map_color_diffuse and (mtex.texture_coords == 'REFLECTION'):
                                image_map["refl"] = (mtex, image)
                            if mtex.use_map_emit:
                                image_map["map_Ke"] = (mtex, image)

                for key, (mtex, image) in sorted(image_map.items()):
                    filepath = bpy_extras.io_utils.path_reference(image.filepath, source_dir, dest_dir,
                                                                  path_mode, "", copy_set, image.library)
                    options = []
                    if key == "map_Bump":
                        if mtex.normal_factor != 1.0:
                            options.append('-bm %.6f' % mtex.normal_factor)
                    if mtex.offset != Vector((0.0, 0.0, 0.0)):
                        options.append('-o %.6f %.6f %.6f' % mtex.offset[:])
                    if mtex.scale != Vector((1.0, 1.0, 1.0)):
                        options.append('-s %.6f %.6f %.6f' % mtex.scale[:])
                    if options:
                        fw('%s %s %s\n' % (key, " ".join(options), repr(filepath)[1:-1]))
                    else:
                        fw('%s %s\n' % (key, repr(filepath)[1:-1]))
    return mtl_color_dict, mtl_texture_dict


def test_nurbs_compat(ob):
    if ob.type != 'CURVE':
        return False

    for nu in ob.data.splines:
        if nu.point_count_v == 1 and nu.type != 'BEZIER':  # not a surface and not bezier
            return True

    return False


def write_nurb(fw, ob, ob_mat):
    tot_verts = 0
    cu = ob.data

    # use negative indices
    for nu in cu.splines:
        if nu.type == 'POLY':
            DEG_ORDER_U = 1
        else:
            DEG_ORDER_U = nu.order_u - 1  # odd but tested to be correct

        if nu.type == 'BEZIER':
            print("\tWarning, bezier curve:", ob.name, "only poly and nurbs curves supported")
            continue

        if nu.point_count_v > 1:
            print("\tWarning, surface:", ob.name, "only poly and nurbs curves supported")
            continue

        if len(nu.points) <= DEG_ORDER_U:
            print("\tWarning, order_u is lower then vert count, skipping:", ob.name)
            continue

        pt_num = 0
        do_closed = nu.use_cyclic_u
        do_endpoints = (do_closed == 0) and nu.use_endpoint_u

        for pt in nu.points:
            fw('v %.6f %.6f %.6f\n' % (ob_mat * pt.co.to_3d())[:])
            pt_num += 1
        tot_verts += pt_num

        fw('g %s\n' % (name_compat(ob.name)))  # name_compat(ob.getData(1)) could use the data name too
        fw('cstype bspline\n')  # not ideal, hard coded
        fw('deg %d\n' % DEG_ORDER_U)  # not used for curves but most files have it still

        curve_ls = [-(i + 1) for i in range(pt_num)]

        # 'curv' keyword
        if do_closed:
            if DEG_ORDER_U == 1:
                pt_num += 1
                curve_ls.append(-1)
            else:
                pt_num += DEG_ORDER_U
                curve_ls = curve_ls + curve_ls[0:DEG_ORDER_U]

        fw('curv 0.0 1.0 %s\n' % (" ".join([str(i) for i in curve_ls])))  # Blender has no U and V values for the curve

        # 'parm' keyword
        tot_parm = (DEG_ORDER_U + 1) + pt_num
        tot_parm_div = float(tot_parm - 1)
        parm_ls = [(i / tot_parm_div) for i in range(tot_parm)]

        if do_endpoints:  # end points, force param
            for i in range(DEG_ORDER_U + 1):
                parm_ls[i] = 0.0
                parm_ls[-(1 + i)] = 1.0

        fw("parm u %s\n" % " ".join(["%.6f" % i for i in parm_ls]))

        fw('end\n')

    return tot_verts


def write_file(filepath, objects, scene,
               EXPORT_TRI=False,
               EXPORT_EDGES=False,
               EXPORT_SMOOTH_GROUPS=False,
               EXPORT_SMOOTH_GROUPS_BITFLAGS=False,
               EXPORT_NORMALS=False,
               EXPORT_UV=True,
               EXPORT_MTL=True,
               EXPORT_APPLY_MODIFIERS=True,
               EXPORT_BLEN_OBS=True,
               EXPORT_GROUP_BY_OB=False,
               EXPORT_GROUP_BY_MAT=False,
               EXPORT_KEEP_VERT_ORDER=False,
               EXPORT_POLYGROUPS=False,
               EXPORT_CURVE_AS_NURBS=True,
               EXPORT_GLOBAL_MATRIX=None,
               EXPORT_PATH_MODE='AUTO',
               progress=ProgressReport(),
               ):
    """
    Basic write function. The context and options must be already set
    This can be accessed externaly
    eg.
    write( 'c:\\test\\foobar.obj', Blender.Object.GetSelected() ) # Using default options.
    """
    if EXPORT_GLOBAL_MATRIX is None:
        EXPORT_GLOBAL_MATRIX = mathutils.Matrix()

    def veckey3d(v):
        return round(v.x, 4), round(v.y, 4), round(v.z, 4)

    def veckey2d(v):
        return round(v[0], 4), round(v[1], 4)

    def findVertexGroupName(face, vWeightMap):
        """
        Searches the vertexDict to see what groups is assigned to a given face.
        We use a frequency system in order to sort out the name because a given vetex can
        belong to two or more groups at the same time. To find the right name for the face
        we list all the possible vertex group names with their frequency and then sort by
        frequency in descend order. The top element is the one shared by the highest number
        of vertices is the face's group
        """
        weightDict = {}
        for vert_index in face.vertices:
            vWeights = vWeightMap[vert_index]
            for vGroupName, weight in vWeights:
                weightDict[vGroupName] = weightDict.get(vGroupName, 0.0) + weight

        if weightDict:
            return max((weight, vGroupName) for vGroupName, weight in weightDict.items())[1]
        else:
            return '(null)'

    with ProgressReportSubstep(progress, 2, "OBJ Export path: %r" % filepath, "OBJ Export Finished") as subprogress1:
        n_points = 0
        n_faces = 0
        data_points = []
        surface_color1 = []
        surface_color2 = []
        material_list = []
        points_per_cell = []
        cell_array = []
        with open(filepath, "w", encoding="utf8", newline="\n") as f:
            fw = f.write

            # Tell the obj file what material file to use.
            if EXPORT_MTL:
                mtlfilepath = os.path.splitext(filepath)[0] + ".mtl"
                # filepath can contain non utf8 chars, use repr
                fw('mtllib %s\n' % repr(os.path.basename(mtlfilepath))[1:-1])

            # Initialize totals, these are updated each object
            totverts = 1

            face_vert_index = 1

            # A Dict of Materials
            # (material.name, image.name):matname_imagename # matname_imagename has gaps removed.
            mtl_dict = {}
            # Used to reduce the usage of matname_texname materials, which can become annoying in case of
            # repeated exports/imports, yet keeping unique mat names per keys!
            # mtl_name: (material.name, image.name)
            mtl_rev_dict = {}

            copy_set = set()

            # Get all meshes
            subprogress1.enter_substeps(len(objects))
            for i, ob_main in enumerate(objects):
                # ignore dupli children
                if ob_main.parent and ob_main.parent.dupli_type in {'VERTS', 'FACES'}:
                    # XXX
                    subprogress1.step("Ignoring %s, dupli child..." % ob_main.name)
                    continue

                obs = [(ob_main, ob_main.matrix_world)]
                if ob_main.dupli_type != 'NONE':
                    # XXX
                    print('creating dupli_list on', ob_main.name)
                    ob_main.dupli_list_create(scene)

                    obs += [(dob.object, dob.matrix) for dob in ob_main.dupli_list]

                    # XXX debug print
                    print(ob_main.name, 'has', len(obs) - 1, 'dupli children')

                subprogress1.enter_substeps(len(obs))
                for ob, ob_mat in obs:
                    with ProgressReportSubstep(subprogress1, 6) as subprogress2:
                        if EXPORT_CURVE_AS_NURBS and test_nurbs_compat(ob):
                            ob_mat = EXPORT_GLOBAL_MATRIX * ob_mat
                            totverts += write_nurb(fw, ob, ob_mat)
                            continue

                        try:
                            me = ob.to_mesh(scene, EXPORT_APPLY_MODIFIERS, 'PREVIEW', calc_tessface=False)
                        except RuntimeError:
                            me = None
                        if me is None:
                            continue

                        me.transform(EXPORT_GLOBAL_MATRIX * ob_mat)

                        if EXPORT_TRI:
                            # _must_ do this first since it re-allocs arrays
                            mesh_triangulate(me)
                        me_verts = me.vertices[:]

                        # Make our own list so it can be sorted to reduce context switching
                        face_index_pairs = [(face, index) for index, face in enumerate(me.polygons)]
                        if not (len(face_index_pairs) + len(me.vertices)):  # Make sure there is something to write
                            # clean up
                            bpy.data.meshes.remove(me)
                            continue  # dont bother with this mesh.

                        materials = me.materials[:]
                        material_names = [m.name if m else None for m in materials]
                        # avoid bad index errors
                        if not materials:
                            materials = [None]
                            material_names = [name_compat(None)]

                        # Sort by Material, then images
                        # so we dont over context switch in the obj file.
                        if EXPORT_KEEP_VERT_ORDER:
                            pass
                        else:
                            if len(materials) > 1:
                                sort_func = lambda a: (a[0].material_index,
                                                       a[0].use_smooth)
                            else:
                                # no materials
                                sort_func = lambda a: a[0].use_smooth
                            face_index_pairs.sort(key=sort_func)
                            del sort_func
                        # Set the default mat to no material and no image.
                        contextMat = 0, 0  # Can never be this, so we will label a new material the first chance we get.
                        subprogress2.step()

                        # Vert
                        this_obj_points = []
                        for v in me_verts:
                            fw('v %.6f %.6f %.6f\n' % v.co[:])
                            this_obj_points.append(list(v.co[:]))
                        obj_local_pts = np.arange(len(this_obj_points))
                        obj_global_pts = insert_data_point(data_points, this_obj_points)
                        local_to_global = {}
                        for lcl, glbl in zip(obj_local_pts, obj_global_pts):
                            local_to_global.update({lcl: glbl})

                        subprogress2.step()
                        subprogress2.step()
                        subprogress2.step()

                        for f, f_index in face_index_pairs:
                            f_mat = min(f.material_index, len(materials) - 1)
                            # MAKE KEY
                            key = material_names[f_mat], None  # No image, use None instead.

                            # CHECK FOR CONTEXT SWITCH
                            if key == contextMat:
                                pass  # Context already switched, dont do anything
                            else:
                                if key[0] is None and key[1] is None:
                                    # Write a null material, since we know the context has changed.
                                    if EXPORT_MTL:
                                        fw("usemtl (null)\n")  # mat, image
                                else:
                                    mat_data = mtl_dict.get(key)
                                    if not mat_data:
                                        # First add to global dict so we can export to mtl
                                        # Then write mtl

                                        # Make a new names from the mat and image name,
                                        # converting any spaces to underscores with name_compat.

                                        # If none image dont bother adding it to the name
                                        # Try to avoid as much as possible adding texname (or other things)
                                        # to the mtl name (see [#32102])...
                                        mtl_name = "%s" % name_compat(key[0])
                                        if mtl_rev_dict.get(mtl_name, None) not in {key, None}:
                                            if key[1] is None:
                                                tmp_ext = "_NONE"
                                            else:
                                                tmp_ext = "_%s" % name_compat(key[1])
                                            i = 0
                                            while mtl_rev_dict.get(mtl_name + tmp_ext, None) not in {key, None}:
                                                i += 1
                                                tmp_ext = "_%3d" % i
                                            mtl_name += tmp_ext
                                        mat_data = mtl_dict[key] = mtl_name, materials[f_mat], None
                                        mtl_rev_dict[mtl_name] = key
                                    if EXPORT_MTL:
                                        fw("usemtl %s\n" % mat_data[0])  # can be mat_image or (null)
                            contextMat = key

                            # Faces, linked to data points
                            f_v = [(vi, me_verts[v_idx], l_idx)
                                   for vi, (v_idx, l_idx) in enumerate(zip(f.vertices, f.loop_indices))]
                            fw('f')
                            faces_points = []
                            for vi, v, li in f_v:
                                fw(" %d" % (totverts + v.index))
                                faces_points.append(local_to_global[v.index])
                            material_list.append(contextMat)
                            points_per_cell.append(len(faces_points))
                            cell_array.extend(faces_points)
                            fw('\n')

                        subprogress2.step()

                        # Make the indices global rather then per mesh
                        totverts += len(me_verts)

                        # clean up
                        bpy.data.meshes.remove(me)

                if ob_main.dupli_type != 'NONE':
                    ob_main.dupli_list_clear()

                subprogress1.leave_substeps("Finished writing geometry of '%s'." % ob_main.name)
            subprogress1.leave_substeps()

        subprogress1.step("Finished exporting geometry, now exporting materials")

        materials, textures = zip(*material_list)  # zip(*...) is the "inverse" of zip(...)
        # Now we have all our materials, save them
        if EXPORT_MTL:
            mtl_color_dict, mtl_texture_dict = write_mtl(scene, mtlfilepath, EXPORT_PATH_MODE, copy_set, mtl_dict)
        for mtl_name in materials:
            mtl_val = mtl_color_dict[mtl_name]
            r = int(round(mtl_val[0]*31))
            g = int(round(mtl_val[1]*31))
            b = int(round(mtl_val[2]*31))
            color_val = ((r & 0x1f) << 10) + ((g & 0x1f) << 5) + (b & 0x1f)
            color_val = np.array(-color_val-1, dtype=np.int16).tolist()
            surface_color1.append(color_val)
            surface_color2.append(color_val)

        data_points = np.transpose(data_points)
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

        # copy all collected files.
        bpy_extras.io_utils.path_reference_copy(copy_set)




def _write(context, filepath,
           EXPORT_TRI,  # ok
           EXPORT_EDGES,
           EXPORT_SMOOTH_GROUPS,
           EXPORT_SMOOTH_GROUPS_BITFLAGS,
           EXPORT_NORMALS,  # ok
           EXPORT_UV,  # ok
           EXPORT_MTL,
           EXPORT_APPLY_MODIFIERS,  # ok
           EXPORT_BLEN_OBS,
           EXPORT_GROUP_BY_OB,
           EXPORT_GROUP_BY_MAT,
           EXPORT_KEEP_VERT_ORDER,
           EXPORT_POLYGROUPS,
           EXPORT_CURVE_AS_NURBS,
           EXPORT_SEL_ONLY,  # ok
           EXPORT_ANIMATION,
           EXPORT_GLOBAL_MATRIX,
           EXPORT_PATH_MODE,  # Not used
           ):

    with ProgressReport(context.window_manager) as progress:
        base_name, ext = os.path.splitext(filepath)
        context_name = [base_name, '', '', ext]  # Base name, scene name, frame number, extension

        scene = context.scene

        # Exit edit mode before exporting, so current object states are exported properly.
        if bpy.ops.object.mode_set.poll():
            bpy.ops.object.mode_set(mode='OBJECT')

        orig_frame = scene.frame_current

        # Export an animation?
        if EXPORT_ANIMATION:
            scene_frames = range(scene.frame_start, scene.frame_end + 1)  # Up to and including the end frame.
        else:
            scene_frames = [orig_frame]  # Dont export an animation.

        # Loop through all frames in the scene and export.
        progress.enter_substeps(len(scene_frames))
        for frame in scene_frames:
            if EXPORT_ANIMATION:  # Add frame to the filepath.
                context_name[2] = '_%.6d' % frame

            scene.frame_set(frame, 0.0)
            if EXPORT_SEL_ONLY:
                objects = context.selected_objects
            else:
                objects = scene.objects

            full_path = ''.join(context_name)

            # erm... bit of a problem here, this can overwrite files when exporting frames. not too bad.
            # EXPORT THE FILE.
            progress.enter_substeps(1)
            write_file(full_path, objects, scene,
                       EXPORT_TRI,
                       EXPORT_EDGES,
                       EXPORT_SMOOTH_GROUPS,
                       EXPORT_SMOOTH_GROUPS_BITFLAGS,
                       EXPORT_NORMALS,
                       EXPORT_UV,
                       EXPORT_MTL,
                       EXPORT_APPLY_MODIFIERS,
                       EXPORT_BLEN_OBS,
                       EXPORT_GROUP_BY_OB,
                       EXPORT_GROUP_BY_MAT,
                       EXPORT_KEEP_VERT_ORDER,
                       EXPORT_POLYGROUPS,
                       EXPORT_CURVE_AS_NURBS,
                       EXPORT_GLOBAL_MATRIX,
                       EXPORT_PATH_MODE,
                       progress,
                       )
            progress.leave_substeps()

        scene.frame_set(orig_frame, 0.0)
        progress.leave_substeps()


"""
Currently the exporter lacks these features:
* multiple scene export (only active scene is written)
* particles
"""


def save(context,
         filepath,
         use_triangles=False,
         use_edges=True,
         use_normals=False,
         use_smooth_groups=False,
         use_smooth_groups_bitflags=False,
         use_uvs=True,
         use_materials=True,
         use_mesh_modifiers=True,
         use_blen_objects=True,
         group_by_object=False,
         group_by_material=False,
         keep_vertex_order=False,
         use_vertex_groups=False,
         use_nurbs=True,
         use_selection=True,
         use_animation=False,
         global_matrix=None,
         path_mode='AUTO'
         ):

    _write(context, filepath,
           EXPORT_TRI=use_triangles,
           EXPORT_EDGES=use_edges,
           EXPORT_SMOOTH_GROUPS=use_smooth_groups,
           EXPORT_SMOOTH_GROUPS_BITFLAGS=use_smooth_groups_bitflags,
           EXPORT_NORMALS=use_normals,
           EXPORT_UV=use_uvs,
           EXPORT_MTL=use_materials,
           EXPORT_APPLY_MODIFIERS=use_mesh_modifiers,
           EXPORT_BLEN_OBS=use_blen_objects,
           EXPORT_GROUP_BY_OB=group_by_object,
           EXPORT_GROUP_BY_MAT=group_by_material,
           EXPORT_KEEP_VERT_ORDER=keep_vertex_order,
           EXPORT_POLYGROUPS=use_vertex_groups,
           EXPORT_CURVE_AS_NURBS=use_nurbs,
           EXPORT_SEL_ONLY=use_selection,
           EXPORT_ANIMATION=use_animation,
           EXPORT_GLOBAL_MATRIX=global_matrix,
           EXPORT_PATH_MODE=path_mode,
           )

    return {'FINISHED'}


if __name__ == "__main__":
    save(bpy.context,
         "testfile",
         use_triangles=False,
         use_edges=False,
         use_normals=False,
         use_smooth_groups=False,
         use_smooth_groups_bitflags=False,
         use_uvs=False,
         use_materials=True,
         use_mesh_modifiers=True,
         use_blen_objects=False,
         group_by_object=False,
         group_by_material=False,
         keep_vertex_order=False,
         use_vertex_groups=False,
         use_nurbs=True,
         use_selection=False,
         use_animation=False,
         global_matrix=None,
         path_mode='AUTO'
         )
