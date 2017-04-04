import numpy as np
import scipy as sp
import os
import matplotlib.pyplot as plt
import matplotlib.tri as tri
from mpl_toolkits.mplot3d import Axes3D
from mpl_toolkits.mplot3d.art3d import Poly3DCollection, Line3DCollection
import matplotlib.pyplot as plt

import matplotlib.colors as colors

def rgba_to_hex(val):
    argb = np.array([val], dtype=np.uint32)
    a = ((argb >> 24) & 0xff)
    r = ((argb >> 16) & 0xff)
    g = ((argb >> 8) & 0xff)
    b = (argb & 0xff)
    return "#%02x%02x%02x" % (r[0], g[0], b[0])


# Convert .ob3 files (readable by the client) to .csg files (readable by OpenSCAD)
"""
max_files = 500
for i in range(0, max_files):
    if (os.path.isfile("object"+str(i)+".ob3")):
        print("Processing file %d of %d (%.1f%%)" % (i, max_files-1,
                                                     100*(i)/(max_files-1)))
        with open("object"+str(i)+".ob3", "rb") as f:
            data_read = sp.array(bytearray(f.read()), dtype=sp.uint8)
        offset = 0
        nPoints = data_read[offset:offset+2]
        offset += 2
        nPoints.dtype = np.uint16
        nPoints = nPoints.byteswap()
        nPoints = nPoints[0]

        nSides = data_read[offset:offset+2]
        offset += 2
        nSides.dtype = np.uint16
        nSides = nSides.byteswap()
        nSides = nSides[0]

        # 2 because 16-bit integer, 3 because 3 dimensions
        dataPoints = data_read[offset:offset+2*3*nPoints]
        offset += 2*3*nPoints
        dataPoints.dtype = sp.int16
        dataPoints = dataPoints.byteswap()
        dataPoints = sp.reshape(dataPoints, (3, nPoints))

        pointsPerCell = data_read[offset:offset+nSides]
        offset += nSides

        surfaceColor1 = data_read[offset:offset+2*nSides]
        offset += 2*nSides
        surfaceColor1.dtype = sp.int16
        surfaceColor1 = surfaceColor1.byteswap()

        surfaceColor2 = data_read[offset:offset+2*nSides]
        offset += 2*nSides
        surfaceColor2.dtype = sp.int16
        surfaceColor2 = surfaceColor2.byteswap()

        someArray = data_read[offset:offset+nSides]
        offset += nSides

        cellArray = []
        for j in range(0, nSides, 1):
            tmpArray = np.empty(pointsPerCell[j], dtype=np.int32)
            if (nPoints < 256):
                tmpArray = data_read[offset:offset+pointsPerCell[j]]
                offset += pointsPerCell[j]
            else:
                tmpArray2 = np.empty(2*pointsPerCell[j], dtype=np.uint8)
                tmpArray2 = data_read[offset:offset+2*pointsPerCell[j]]
                offset += 2*pointsPerCell[j]
                tmpArray2.dtype = sp.int16
                tmpArray = tmpArray2.byteswap()
            cellArray.append(tmpArray.tolist())

        points = np.transpose([dataPoints[0], dataPoints[2], dataPoints[1]])
        with open("object"+str(i)+".csg", "w") as f:
            f.write("group() {\n        polyhedron(points = ")
            f.write(str(points.tolist()))
            f.write(", faces = ")
            f.write(str(cellArray))
            f.write(", convexity = 1);\n}")
"""
def find_color(data):
    i1 = data.find("color(")
    i2 = data[i1:].find(") {")
    clrs = data[i1+6:i1+i2] if i1 > 0 and i2 > 0 else None
    out = None
    if clrs is not None:
        out = list(map(float, clrs[1:-1].split(", ")))
    return out, i1+i2+3

def find_trans(data):
    i1 = data.find("multmatrix(")
    i2 = data[i1:].find(") {")
    trns_mat = data[i1+11:i1+i2] if i1 > 0 and i2 > 0 else None
    out = None
    if trns_mat is not None:
        mat_rows = []
        for rows in trns_mat[2:-2].split("], ["):
            # print(rows)
            list_tmp = list(map(float, rows.split(", ")))
            mat_rows.append(list_tmp[-1])
        out = mat_rows[:-1]
    return out, i1+i2+3

# Convert .csg files (readable by OpenSCAD) to .ob3 files (readable by the client)
if __name__ == "__main__":
    i = 500
    max_files = 1
    if (os.path.isfile("object"+str(i)+".csg")):
        print("Processing file %d of %d (%.1f%%)" % (i, max_files,
                                                     100*(i)/(max_files)))
        with open("object"+str(i)+".csg", "r") as f:
            data_read=f.read()
        # Data points (x,y,z)
        color_array = []
        trans_array = []
        for grps in data_read.split("group() {"):
            color_val, new_idx = find_color(grps)
            if color_val is None:
                continue
            trans_val, new_idx = find_trans(grps[new_idx:])
            if trans_val is None:
                continue
            trans_array.append(trans_val)
            color_array.append(color_val)
        print(color_array)
        print(trans_array)


        """
        points = data_read[data_read.find("points = ")+9:data_read.find(", faces = ")]
        outer_points = points[2:-2].split("], [")
        inner_points = []
        data_points = []
        for l in outer_points:
            inner_points.append(l.split(", "))
        for lo in inner_points:
            tmp_arr = []
            for li in lo:
                tmp_arr.append(int(li))
            data_points.append(tmp_arr)
        dataPoints = np.transpose(np.array(data_points, dtype=np.int16))

        faces = data_read[data_read.find(", faces = ")+10:data_read.find(", convexity = ")]
        outer_faces = faces[2:-2].split("], [")
        inner_faces = []
        faces_data = []
        for l in outer_faces:
            inner_faces.append(l.split(", "))
        for lo in inner_faces:
            tmp_arr = []
            for li in lo:
                tmp_arr.append(int(li))
            faces_data.append(tmp_arr)
        """
# print(data_points)
# print(faces_data)
