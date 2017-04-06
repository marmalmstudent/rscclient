module prism(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
    color(c=color, alpha=1) { translate([x, y, z]) { rotate([xRot, yRot, zRot]) {
        polyhedron( points=[
                        [0,0,0], [0,0,h], [l,0,0],
                        [l,0,h], [0,w,0], [l,w,0]],
                    faces=[
                        [0,1,2], [3,2,1], [4,1,0],
                        [2,4,0], [5,4,2], [3,5,2],
                        [1,4,3], [5,3,4]]
        );
    }}}
}
module pyramid(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
    color(c=color, alpha=1) { translate([x, y, z]) { rotate([xRot, yRot, zRot]) {
        polyhedron( points=[
                        [l/2, w/2, 0], [l/2, -w/2, 0],
                        [-l/2, -w/2,0], [-l/2, w/2, 0],
                        [0, 0, h]],
                    faces=[
                        [0,1,4],[1,2,4],[2,3,4],[3,0,4],
                        [1,0,3],[2,1,3]]
        );
    }}}
}
module diamond(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
    color(c=color, alpha=1) { translate([x, y, z]) { rotate([xRot, yRot, zRot]) {
            polyhedron( points = [
                            [l/2, 0, 0], [0, w/2, 0],
                            [-l/2, 0,0], [0, -w/2, 0],
                            [0, 0, h]],
                        faces = [
                            [0,1,4],[1,2,4],[2,3,4],[3,0,4],
                            [1,0,3],[2,1,3]],
                        convexity = 1
        );
    }}}
}

module cylinder(x, y, z, r, h, xRot, yRot, zRot, color)
{
    color(c=color, alpha=1) { translate([x, y, z]) { rotate([xRot, yRot, zRot]) {
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
    }}}
}

module rectangle(x, y, z, l, w, h, xRot, yRot, zRot, color)
{
    color(c=color, alpha=1) { translate([x, y, z]) { rotate([xRot, yRot, zRot]) {
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
    }}}
}

length = 6;
width = 6;
pyramid(0, 0, -6, length, width, 6, 0, 0, 0, [1,1,0]);
pyramid(0, 0, -6, length, width, 6, 180, 0, 0, [1,1,0]);
cylinder(0, 0, -6, 3, 18, 180, 0, 0, [0.5,0.5,0.5]);

rectangle(0, 0, -24, 16, 6, 6, 180, 0, 0, [1,1,0]);
pyramid(8, 0, -27, length, width, 6, 0, 90, 0, [1,1,0]);
pyramid(-8, 0, -27, length, width, 6, 0, 270, 0, [1,1,0]);
diamond(0, -3, -27, 4, 4, 2, 90, 0, 0, [0,0,1]);
diamond(5, -3, -27, 4, 2, 1, 90, 0, 0, [0,1,0]);
diamond(-5, -3, -27, 4, 2, 1, 90, 0, 0, [0,1,0]);
diamond(0, 3, -27, 4, 4, 2, 270, 0, 0, [0,0,1]);
diamond(5, 3, -27, 4, 2, 1, 270, 0, 0, [0,1,0]);
diamond(-5, 3, -27, 4, 2, 1, 270, 0, 0, [0,1,0]);

prism(0, 0, -30, 100, 5, 2, 0, 90, 90, [0.5,0.5,0.5]);
prism(0, 0, -130, 100, 5, 2, 0, 270, 90, [0.5,0.5,0.5]);
prism(0, 0, -30, 100, 5, 2, 0, 90, 270, [0.5,0.5,0.5]);
prism(0, 0, -130, 100, 5, 2, 0, 270, 270, [0.5,0.5,0.5]);
diamond(0, 0, -130, 10, 5, 5, 180, 0, 0, [0.5,0.5,0.5]);


echo(version=version());