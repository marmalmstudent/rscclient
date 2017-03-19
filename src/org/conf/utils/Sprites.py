import numpy as np
import matplotlib.pyplot as plt
import sys

args = sys.argv[1:]
data_read = np.loadtxt("sprites_txt/"+args[0]+".txt", dtype=np.uint8)
sprite_width = data_read[0:4]; sprite_width.dtype = np.uint32
sprite_width = sprite_width.newbyteorder()
sprite_height = data_read[4:8]; sprite_height.dtype = np.uint32
sprite_height = sprite_height.newbyteorder()

image = data_read[25:]; image.dtype=np.uint32
image = np.reshape(np.array(image.newbyteorder()), (sprite_height, sprite_width))
image_r = np.array((image >> 16) & 0xff, dtype=np.uint8)
image_g = np.array((image >> 8) & 0xff, dtype=np.uint8)
image_b = np.array((image) & 0xff, dtype=np.uint8)
image_disp = np.transpose(np.array([image_r, image_g, image_b]), (1, 2, 0))
plt.imsave("sprites_img/"+args[0]+".png", image_disp)
"""
#plt.imshow(image_disp)
#plt.show()
"""
requires_shift = data_read[8] == 1
x_shift = data_read[9:13]; x_shift.dtype = np.uint32
x_shift = x_shift.newbyteorder()
y_shift = data_read[13:17]; y_shift.dtype = np.uint32
y_shift = y_shift.newbyteorder()
cam_angle_1 = data_read[17:21]; cam_angle_1.dtype = np.uint32
cam_angle_1 = cam_angle_1.newbyteorder()
cam_angle_2 = data_read[21:25]; cam_angle_2.dtype = np.uint32
cam_angle_2 = cam_angle_2.newbyteorder()

#image = np.reshape(np.array(data_read.newbyteorder()), (72, 80))
#image_r = np.array((image >> 16) & 0xff, dtype=np.uint8)
#image_g = np.array((image >> 8) & 0xff, dtype=np.uint8)
#image_b = np.array((image) & 0xff, dtype=np.uint8)
#image_disp = np.transpose(np.array([image_r, image_g, image_b]), (1, 2, 0))
#plt.imshow(image)
#plt.show()
