import numpy as np
import scipy as sp
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

f = open("object368.ob3", "rb")
data_read = sp.array(bytearray(f.read()), dtype=sp.uint8)
f.close()
nPoints = data_read[0:2]
nPoints.dtype = np.uint16
nPoints = nPoints.newbyteorder()
nPoints = nPoints[0]
nSides = data_read[2:4]
nSides.dtype = np.uint16
nSides = nSides.newbyteorder()
dataPoints = data_read[4:4+2*3*nPoints]
dataPoints.dtype = sp.int16
dataPoints = dataPoints.newbyteorder()
dataPoints = sp.reshape(dataPoints, (3,nPoints))

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
ax.plot(dataPoints[0], dataPoints[2], dataPoints[1])
plt.show()
