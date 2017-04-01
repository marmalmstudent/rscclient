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
                    [r*cos(315), r*sin(315), h],
                    [0,0,0], [0,0,h]],
                        faces=[
                    [0,1,2], [1,2,3],
                    [2,3,4],[3,4,5],
                    [4,5,6],[5,6,7],
                    [6,7,8],[7,8,9],
                    [8,9,10],[9,10,11],
                    [10,11,12],[11,12,13],
                    [12,13,14],[13,14,15],
                    [14,15,0],[15,0,1],
                    [16,0,2], [16,2,4], [16,4,6], [16,6,8], [16,8,10], [16,10,12], [16,12,14], [16,14,0],
                    [17,1,3], [17,3,5], [17,5,7], [17,7,9], [17,9,11], [17,11,13], [17,13,15], [17,15,1]]
    );
}

module rectangle(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
color(color)
    translate([x, y, z])
        rotate([xRot, yRot, zRot])
            polyhedron( points = [
                    [-l/2, -w/2, 0],  //0
                    [l/2, -w/2, 0],  //1
                    [-l/2, -w/2, h],  //2
                    [l/2, -w/2, h],  //3
                    [-l/2, w/2, h],  //4
                    [l/2, w/2, h],  //5
                    [-l/2, w/2, 0],  //6
                    [l/2, w/2, 0]], //7
                        faces = [
                    [0,1,2],[1,2,3],
                    [2,3,4],[3,4,5],
                    [4,5,6],[5,6,7],
                    [6,7,0],[7,0,1],
                    [0,2,6],[2,4,6],
                    [1,3,7],[3,5,7]]
    );
}
length = 5;
width = 5;
pyramid(0, 0, 5, length, width, 5, 180, 0, 0, "gold");
cylinder(0, 0, 5, 2, 15, 0, 0, 0, "gray");
pyramid(0, 0, 5, 5, length, width, 0, 0, 0, "gold");
rectangle(0, 0, 20, 16, 5, 5, 0, 0, 0, "gold");
pyramid(8, 0, 22.5, length, width, 5, 0, 90, 0, "gold");
pyramid(-8, 0, 22.5, length, width, 5, 0, 270, 0, "gold");
diamond(0, -2.5, 22.5, 3, 4, 2, 90, 0, 0, "blue");
diamond(4, -2.5, 22.5, 3, 2, 1, 90, 0, 0, "green");
diamond(-4, -2.5, 22.5, 3, 2, 1, 90, 0, 0, "green");
diamond(0, 2.5, 22.5, 3, 4, 2, 270, 0, 0, "blue");
diamond(4, 2.5, 22.5, 3, 2, 1, 270, 0, 0, "green");
diamond(-4, 2.5, 22.5, 3, 2, 1, 270, 0, 0, "green");

prism(5, 0, 25, 100, 5, 2, 0, 270, 90, "gray");
prism(5, 0, 125, 100, 5, 2, 0, 90, 90, "gray");
prism(-5, 0, 25, 100, 5, 2, 0, 270, 270, "gray");
prism(-5, 0, 125, 100, 5, 2, 0, 90, 270, "gray");
diamond(0, 0, 125, 10, 5, 5, 0, 0, 0, "gray");




echo(version=version());