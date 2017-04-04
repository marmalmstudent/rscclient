/*
--------------------------------------------------------------------------------
*/
module prism(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
color(color)
    translate([x, y, z])
        rotate([xRot, yRot, zRot])
            polyhedron( points=[
                    [0,0,0], [l,0,0], [l,w,0],
                    [0,w,0], [0,w,h], [l,w,h]],
                        faces=[
                    [0,1,2,3],[5,4,3,2],[0,4,5,1],
                    [0,3,4],[5,2,1]]
    );
}
module pyramid(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
color(color)
    translate([x, y, z])
        rotate([xRot, yRot, zRot])
            polyhedron( points=[
                    [l/2, w/2, 0], [l/2, -w/2, 0],
                    [-l/2, -w/2,0], [-l/2, w/2, 0],
                    [0, 0, h]],
                        faces=[
                    [0,1,4],[1,2,4],[2,3,4],[3,0,4],
                    [1,0,3],[2,1,3]]
    );
}
module diamond(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
color(color)
    translate([x, y, z])
        rotate([xRot, yRot, zRot])
            polyhedron( points=[
                    [l/2, 0, 0], [0, w/2, 0],
                    [-l/2, 0,0], [0, -w/2, 0],
                    [0, 0, h]],
                        faces=[
                    [0,1,4],[1,2,4],[2,3,4],[3,0,4],
                    [1,0,3],[2,1,3]]
    );
}
module cylinder(x, y, z, r, h, xRot, yRot, zRot, color)
{
color(color)
    translate([x, y, z])
        rotate([xRot, yRot, zRot])
            polyhedron( points=[
                    [0,0,0], [0,0,h],
                    [r*cos(0), r*sin(0), 0],
                    [r*cos(0), r*sin(0), h],
                    [r*cos(45), r*sin(45), 0],
                    [r*cos(45), r*sin(45), h],
                    [r*cos(90), r*sin(90), 0],
                    [r*cos(90), r*sin(90), h],
                    [r*cos(135), r*sin(135), 0],
                    [r*cos(135), r*sin(135), h],
                    [r*cos(180), r*sin(180), 0],
                    [r*cos(180), r*sin(180), h],
                    [r*cos(225), r*sin(225), 0],
                    [r*cos(225), r*sin(225), h],
                    [r*cos(270), r*sin(270), 0],
                    [r*cos(270), r*sin(270), h],
                    [r*cos(315), r*sin(315), 0],
                    [r*cos(315), r*sin(315), h]],
                        faces=[
                    [2,3,4],[0,2,4],[4,3,5],[3,1,5],
                    [4,5,6],[0,4,6],[6,5,7],[5,1,7],
                    [6,7,8],[0,6,8],[8,7,9],[7,1,9],
                    [8,9,10],[0,8,10],[10,9,11],[9,1,11],
                    [10,11,12],[0,10,12],[12,11,13],[11,1,13],
                    [12,13,14],[0,12,14],[14,13,15],[13,1,15],
                    [14,15,16],[0,14,16],[16,15,17],[15,1,17],
                    [16,17,2],[0,16,2],[2,17,3],[17,1,3]]
    );
}
module rectangle(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
color(color)
    translate([x, y, z])
        rotate([xRot, yRot, zRot])
            polyhedron( points = [
                    [-l/2, -w/2, 0],
                    [-l/2, -w/2, h],
                    [-l/2, w/2, 0],
                    [-l/2, w/2, h],
                    [l/2, -w/2, 0],
                    [l/2, -w/2, h],
                    [l/2, w/2, 0],
                    [l/2, w/2, h]],
                        faces = [
                    [0,1,2],[0,4,1],[0,2,4],
                    [3,2,1],[5,1,4],[1,5,3],
                    [6,4,2],[6,5,4],[2,3,6],
                    [7,5,6],[6,3,7],[7,3,5]]
    );
}
metal_color = "red";
handle_color = "black";
xStart = 0;
yStart = 0;
zStart = 0;

// botom of handle
hndl_pyr_l = 3;
hndl_pyr_w = 3;
hndl_pyr_h = 4;
pyr1_hndl = [xStart,yStart,zStart,hndl_pyr_l,hndl_pyr_w,hndl_pyr_h,
             180,0,0];
pyramid(pyr1_hndl[0],pyr1_hndl[1],pyr1_hndl[2]+pyr1_hndl[5],
        pyr1_hndl[3],pyr1_hndl[4],pyr1_hndl[5],
        pyr1_hndl[6],pyr1_hndl[7],pyr1_hndl[8], metal_color);
box_hndl = [xStart, yStart, pyr1_hndl[2]+pyr1_hndl[5],4,4,4,0,0,0];
rectangle(box_hndl[0],box_hndl[1],box_hndl[2],
          box_hndl[3],box_hndl[4],box_hndl[5],
          box_hndl[6],box_hndl[7],box_hndl[8], metal_color);
pyr2_hndl = [box_hndl[0]+box_hndl[3]/2,box_hndl[1],box_hndl[2]+box_hndl[2]/2,
             hndl_pyr_l,hndl_pyr_w,hndl_pyr_h,
             0,90,0];
pyramid(pyr2_hndl[0],pyr2_hndl[1],pyr2_hndl[2],
        pyr2_hndl[3],pyr2_hndl[4],pyr2_hndl[5],
        pyr2_hndl[6],pyr2_hndl[7],pyr2_hndl[8], metal_color);
pyr3_hndl = [box_hndl[0]-box_hndl[3]/2,box_hndl[1],box_hndl[2]+box_hndl[2]/2,
             hndl_pyr_l,hndl_pyr_w,hndl_pyr_h,
             0,270,0];
pyramid(pyr3_hndl[0],pyr3_hndl[1],pyr3_hndl[2],
        pyr3_hndl[3],pyr3_hndl[4],pyr3_hndl[5],
        pyr3_hndl[6],pyr3_hndl[7],pyr3_hndl[8], metal_color);
pyr4_hndl = [box_hndl[0],box_hndl[1]+box_hndl[3]/2,box_hndl[2]+box_hndl[2]/2,
             hndl_pyr_l,hndl_pyr_w,hndl_pyr_h,
             270,0,0];
pyramid(pyr4_hndl[0],pyr4_hndl[1],pyr4_hndl[2],
        pyr4_hndl[3],pyr4_hndl[4],pyr4_hndl[5],
        pyr4_hndl[6],pyr4_hndl[7],pyr4_hndl[8], metal_color);
pyr5_hndl = [box_hndl[0],box_hndl[1]-box_hndl[3]/2,box_hndl[2]+box_hndl[2]/2,
             hndl_pyr_l,hndl_pyr_w,hndl_pyr_h,
             90,0,0];
pyramid(pyr5_hndl[0],pyr5_hndl[1],pyr5_hndl[2],
        pyr5_hndl[3],pyr5_hndl[4],pyr5_hndl[5],
        pyr5_hndl[6],pyr5_hndl[7],pyr5_hndl[8], metal_color);
// shaft of handle
cyl1_hndl = [xStart,yStart,box_hndl[2]+box_hndl[2],
             2,15,0,0,0];
cylinder(cyl1_hndl[0],cyl1_hndl[1],cyl1_hndl[2],cyl1_hndl[3],cyl1_hndl[4],cyl1_hndl[5],cyl1_hndl[6],cyl1_hndl[7],handle_color);
// top of handle
box_hndl_top = [xStart, yStart, cyl1_hndl[2]+cyl1_hndl[4],4,4,4,0,0,0];
rectangle(box_hndl_top[0],box_hndl_top[1],box_hndl_top[2],
          box_hndl_top[3],box_hndl_top[4],box_hndl_top[5],
          box_hndl_top[6],box_hndl_top[7],box_hndl_top[8], metal_color);
pyr1_hndl_top = [box_hndl_top[0],box_hndl_top[1],
                 box_hndl_top[2]+box_hndl_top[5],
                 hndl_pyr_l,hndl_pyr_w,hndl_pyr_h,
                 0,0,0];
pyramid(pyr1_hndl_top[0],pyr1_hndl_top[1],pyr1_hndl_top[2],
        pyr1_hndl_top[3],pyr1_hndl_top[4],pyr1_hndl_top[5],
        pyr1_hndl_top[6],pyr1_hndl_top[7],pyr1_hndl_top[8], metal_color);
/*
rectangle(0,0,0,10,10,10,0,0,0,"red");
diamond(-20,0,0,10,10,10,0,0,0,"red");
cylinder(-20,-20,0,10,10,0,0,0,"red");
prism(20,20,0,10,10,10,0,0,0,"red");
pyramid(20,0,0,10,10,10,0,0,0,"red");
*/


echo(version=version());