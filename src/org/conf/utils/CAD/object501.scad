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
rectangle(0,0,0,5,10,20,0,0,0,[0.2,0.4,0.6]);