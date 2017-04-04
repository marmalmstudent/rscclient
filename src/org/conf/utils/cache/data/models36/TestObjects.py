
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

model_key = {
    345225 : "tree2", 18997 : "tree", 12349 : "well", 823146 : "table",
    28997 : "treestump", 292330 : "ladder", 514043 : "ladderdown",
    144351 : "chair", 321226 : "logpile", 743594 : "longtable",
    516357 : "throne", 347993 : "range", 747578 : "gravestone1",
    748410 : "gravestone2", 820584 : "Bigbed", 682677 : "bed",
    699395 : "barpumps", 491666 : "ChestOpen", 659403 : "ChestClosed",
    94186 : "altar", 8720 : "wallpost", 164230 : "supportnw",
    300754 : "barrel", 521524 : "bench", 703457 : "portrait",
    282449 : "candles", 538894 : "fountain", 26451 : "landscape",
    442510 : "mill", 37528 : "counter", 105013 : "market", 270214 : "target",
    29435 : "palm2", 180553 : "palm", 746842 : "fern", 93598 : "cactus",
    636745 : "bullrushes", 366864 : "flower", 672806 : "mushroom",
    541560 : "coffin", 98116 : "coffin2", 668117 : "woodenstairs",
    403502 : "woodenstairsdown", 231212 : "stonestairs",
    293998 : "stonestairsdown", 280199 : "woodenrailing",
    801566 : "marblepillar", 698195 : "bookcase", 161918 : "sink",
    798830 : "sworddummy", 663148 : "anvil", 223556 : "torcha1",
    657701 : "milltop", 733436 : "millbase", 386924 : "cart",
    399862 : "sacks", 444220 : "cupboard", 255333 : "metalgateclosed",
    766830 : "metalgateopen", 151978 : "woodengateopen",
    764070 : "woodengateclosed", 269729 : "signpost",
    275853 : "doubledoorsopen", 184285 : "doubledoorsclosed", 49084 : "henge",
    324093 : "dolmen", 800688 : "deadtree1", 218064 : "cupboardopen",
    107038 : "wheat", 515643 : "shopsign", 237968 : "windmillsail",
    56434 : "pipe&drain", 664388 : "manholeclosed", 818574 : "manholeopen",
    82404 : "wallpipe", 794669 : "firea1", 326921 : "rocks1",
    330720 : "rocks2", 163702 : "copperrock1", 625499 : "ironrock1",
    869240 : "tinrock1", 820056 : "mithrilrock1", 145066 : "adamiterock1",
    477424 : "coalrock1", 657173 : "goldrock1", 428450 : "clayrock1",
    666744 : "ceilingweb", 270876 : "floorweb", 253243 : "furnace",
    673731 : "madmachine", 14941 : "spinningwheel", 250405 : "leverup",
    661714 : "leverdown", 141582 : "compost", 782268 : "skulltorcha1",
    336256 : "chaosaltar", 395538 : "wallshield", 344043 : "wallgrill",
    677707 : "cauldron", 852658 : "minecart", 230224 : "trackbuffer",
    792847 : "trackcurve", 418254 : "trackpoints", 663896 : "trackstraight",
    317314 : "hole", 341551 : "Shipfront", 519143 : "Shipmiddle",
    527130 : "Shipback", 876491 : "bluriterock1", 181441 : "potteryoven",
    154750 : "potterywheel", 291253 : "crate", 735032 : "potato",
    500199 : "fishing", 785751 : "silverrock1", 291377 : "smashedtable",
    172466 : "nastyfungus", 615821 : "smashedchair", 133860 : "brokenpillar",
    317764 : "fallentree", 873792 : "dangersign", 525312 : "runiterock1",
    768524 : "largegrave", 500401 : "curvedbone",
    873168 : "largebone", 678805 : "carcass", 128364 : "animalskull",
    13349 : "vine", 331209 : "vinecorner", 314570 : "vinejunction",
    518059 : "brokenwall", 461440 : "dramentree", 670061 : "Doormat",
    154245 : "rock3", 321868 : "bigtable", 670793 : "fireplacea1",
    301508 : "bigegg", 315294 : "eggs", 673262 : "stalagmites",
    683739 : "stool", 270640 : "wallbench", 662154 : "bigroundtable",
    105907 : "roundtable", 7490 : "bush1", 7972 : "bush2",
    176525 : "blueflower", 704131 : "smallfern", 169773 : "giantcrystal",
    656595 : "beehive", 405446 : "marblearch", 765594 : "obelisk",
    638789 : "sandpit", 19977 : "oaktree", 401398 : "willowtree",
    136374 : "mapletree", 143305 : "yewtree", 698415 : "magictree",
    623809 : "flax", 617295 : "brokencart", 244532 : "clockpoleblue",
    242624 : "clockpolered", 24679 : "clockpolepurple",
    720384 : "clockpoleblack", 145594 : "wallclockface",
    802486 : "leverbracket", 850324 : "metalgateclosed2",
    735528 : "metalgateopen2", 793955 : "Foodtrough", 766004 : "spearwall",
    147499 : "hornedskull", 778378 : "toplesstree", 689965 : "catabow",
    705263 : "catabowarrow", 212494 : "ent", 396568 : "khazardwall",
    396740 : "jungle tree 2", 394528 : "jungle tree 1",
    790295 : "jungle fern textured", 624955 : "jungle fern textured 2",
    626027 : "jungle fern textured 3", 626571 : "jungle fern textured 4",
    130759 : "jungle fly trap", 660783 : "jungle fern",
    137420 : "jungle spikey fern", 704621 : "jungle strange plant",
    736570 : "jungle strange plant 2", 219082 : "jungle medium size plant",
    284097 : "jungle statue", 817834 : "deadtree2",
    637519 : "doubledoorframe", 157738 : "sewervalve",
    752598 : "caveentrance", 245334 : "logbridgelow",
    270907 : "logbridgehigh", 410564 : "treeplatformhigh",
    770696 : "treeplatformlow", 257027 : "largetreeplatformlow",
    501356 : "largetreeplatformhigh", 87630 : "logbridgecurvedhigh",
    844356 : "logbridgecurvedlow", 823600 : "treeplatformlow2",
    834092 : "treeplatformhigh2", 302392 : "lograft", 186699 : "hazeeltomb",
    96750 : "ardoungewall", 281131 : "ardoungewallcorner",
    765326 : "mudpatch", 13887 : "mudpile", 344449 : "largesewerpipe",
    869768 : "fishingcrane", 141964 : "rowboat", 385731 : "rowboatsinking",
    122324 : "fishingcranerot1", 124798 : "fishingcranerot2",
    303298 : "waterfall", 151117 : "deadtree2base", 756520 : "elventomb",
    720614 : "waterfalllev1", 723664 : "waterfalllev2", 765728 : "stonestand",
    780722 : "glarialsstatue", 493376 : "baxtorianchalice",
    465418 : "baxtorianchalicelow", 153258 : "brokenlograft",
    331965 : "watchtower", 10059 : "ropeladder", 578032 : "gallows",
    541811 : "grand tree-lev 0", 370605 : "logbridge lev0",
    470856 : "gnomewatchtower lev0", 307086 : "logbridgejunction lev0",
    500800 : "climbing_rocks", 184113 : "corner_ledge",
    250280 : "straight_ledge", 872236 : "log_balance1",
    872702 : "log_balance2", 514911 : "mossyrock", 281697 : "rocktile",
    42940 : "tribalstature", 50632 : "grand treeinside-lev 0",
    57186 : "grand treeinside-lev 1", 69508 : "grand treeinside-lev 2",
    562878 : "grand tree-lev 1", 581123 : "grand tree-lev 2",
    277785 : "hillsidedoor", 310828 : "logbridgejunction lev1",
    639171 : "fourwayplatform-lev 0", 648033 : "fourwayplatform-lev 1",
    377105 : "logbridge lev1", 381119 : "logbridge lev2",
    478530 : "gnomewatchtower lev1", 485098 : "gnomewatchtower lev2",
    385235 : "rockpoolwater", 596277 : "grand tree-lev 3",
    334704 : "blurberrybar", 518615 : "gemrock", 49994 : "obstical_ropeswing",
    182569 : "obstical_net", 684197 : "obstical_frame",
    854768 : "tree_for_rope", 688161 : "tree_with_rope",
    30561 : "tree_with_vines", 170578 : "gnomefence", 161816 : "beam",
    796599 : "gnomesign", 447160 : "treeroot1", 448136 : "treeroot2",
    85222 : "gnomecage", 340407 : "gnomeglider",
    196028 : "gnomeglidercrashed", 398808 : "mudpiledown",
    779254 : "gnomehamek", 327449 : "gnomegoal", 170920 : "stonedisc",
    5126 : "obstical_pipe", 622715 : "spikedpit-low", 457130 : "spikedpit",
    32927 : "bridge section 1", 34816 : "bridge section 2",
    37718 : "bridge section 3", 6218 : "cave bridge support",
    726590 : "cave platform small", 4784 : "gnomefence2",
    166003 : "bridge section collapsed", 779936 : "bridge section collapsed2",
    534254 : "rams skull door", 361712 : "rams skull dooropen",
    556965 : "caveentrance2", 127272 : "cave old bridge",
    164887 : "cave old bridgedown", 876057 : "cave large stagamite",
    500034 : "cave small stagamite", 156624 : "cave rock1",
    798138 : "cave ledge", 419963 : "cave lever",
    170056 : "cave large stagatite", 743003 : "cave small stagatite",
    195608 : "cave extra large stagatite", 477952 : "cave swampbubbles",
    8910 : "cave rocktrap1", 268267 : "cave rocktrap1a",
    367758 : "cave swamprocks", 684829 : "cave grilltrap",
    318739 : "cave grilltrapa", 107982 : "cave temple",
    24909 : "cave grilltrapa up", 84048 : "cave grillcage",
    874859 : "cave grillcageup", 153958 : "cave speartrap",
    520829 : "cave speartrapa", 357383 : "cave furnace", 685591 : "cave well",
    739179 : "cave tubetrap", 346205 : "cave tubetrapa",
    222288 : "cave tubetrapa rope", 718784 : "cave snaptrap",
    27125 : "cave snaptrapa", 622417 : "cave planks",
    20887 : "cave bloodwell", 407810 : "cave platform verysmall",
    393934 : "cave carvings", 168927 : "cave wallgrill",
    732858 : "cave bolder", 856376 : "cave templedoor",
    98451 : "cave platform small2", 349619 : "cave smashedcage",
    34090 : "cave bridge supportbase", 740263 : "clawsofiban",
    745868 : "bridge section corner", 734092 : "cave bridge stairs",
    494548 : "cave temple alter", 705444 : "cave templedooropen",
    466590 : "zodiac", 148165 : "telescope", 83180 : "cave pillar",
    802754 : "dwarf multicannon", 24347 : "sandyfootsteps",
    422882 : "dwarf multicannon part1", 423204 : "dwarf multicannon part2",
    428978 : "dwarf multicannon part3", 33467 : "small caveentrance2",
    5808 : "signpost2", 843996 : "brownclimbingrocks",
    452322 : "ogre standard", 665076 : "liftwinch", 445148 : "rockcounter",
    614491 : "liftbed", 557588 : "rock cake counter",
    103399 : "magearena colomn", 32561 : "magearena wall",
    459138 : "magearena corner", 420861 : "magearena tallwall",
    283877 : "magearena cornerfill", 194011 : "magearena tallcorner",
    170476 : "magearena plain wall", 617047 : "spellshock",
    666775 : "magearena door", 244762 : "cactuswatered",
    748126 : "lightning1", 748846 : "poorbed", 174958 : "firespell1",
    4532 : "1-1light", 184011 : "1-1dark", 385133 : "1-3light",
    320281 : "1-3dark", 270112 : "2-2light", 515541 : "2-2dark",
    326673 : "barrier1", 392022 : "halfburiedskeleton", 96274 : "2-1light",
    159346 : "largeurn", 512596 : "halfburiedskeleton2",
    106545 : "dugupsoil1", 107222 : "dugupsoil2", 107532 : "dugupsoil3",
    137700 : "sinkingshipfront", 156984 : "sinkingbarrel",
    135948 : "shipleak", 743168 : "shipleak2", 229340 : "barrelredcross",
    306224 : "shipspray1", 306255 : "shipspray2", 8362 : "ropeforclimbingbot",
    293198 : "trawlernet-l", 306286 : "trawlernet-r", 221732 : "trawlernet",
    522702 : "totemtree1", 523520 : "totemtree2", 524338 : "totemtree3",
    525104 : "totemtree4", 525840 : "totemtree5", 397914 : "rocksteps",
    449582 : "clawspell1", 749626 : "Spellcharge1", 7156 : "saradominstone",
    670255 : "guthixstone", 833488 : "zamorakstone", 784276 : "rockpool",
    320383 : "Scaffoldsupport", 287670 : "ScaffoldsupportRope",
    171114 : "Shamancave", 242854 : "skeletonwithbag",
    155334 : "totemtreeevil", 360422 : "totemtreegood",
    35448 : "totemtreerotten2", 38090 : "totemtreerotten3",
    40794 : "totemtreerotten4", 45856 : "totemtreerotten5",
    225002 : "torcha2", 226448 : "torcha3", 227894 : "torcha4",
    786279 : "skulltorcha2", 788287 : "skulltorcha3", 790839 : "skulltorcha4",
    795634 : "firea2", 797173 : "firea3", 671464 : "fireplacea2",
    672135 : "fireplacea3", 177419 : "firespell2", 178986 : "firespell3",
    749342 : "lightning2", 750670 : "lightning3", 454390 : "clawspell2",
    458190 : "clawspell3", 459372 : "clawspell4", 462318 : "clawspell5",
    751554 : "spellcharge2", 755476 : "spellcharge3"
}
# Reads .ob3 files (readable by the client), plots the object and prints to .png files.
"""
max_files = 500
for i in range(0, max_files):
    if (os.path.isfile("object"+str(i)+".ob3")):
        print("Processing file %d of %d (%.1f%%)" % (i, max_files-1,
                                                     100*(i)/(max_files-1)))
        f = open("object"+str(i)+".ob3", "rb")
        data_read = sp.array(bytearray(f.read()), dtype=sp.uint8)
        f.close()
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
            tmpArray = np.empty(pointsPerCell[j]+1, dtype=np.int32)
            if (nPoints < 256):
                tmpArray[:len(tmpArray)-1] = data_read[offset:offset+pointsPerCell[j]]
                tmpArray[len(tmpArray)-1] = data_read[offset]
                offset += pointsPerCell[j]
            else:
                tmpArray2 = np.empty(2*(pointsPerCell[j]+1), dtype=np.uint8)
                tmpArray2[:len(tmpArray2)-2] = data_read[offset:offset+2*pointsPerCell[j]]
                tmpArray2[len(tmpArray2)-2:len(tmpArray2)] = data_read[offset:offset+2]
                offset += 2*pointsPerCell[j]
                tmpArray2.dtype = sp.int16
                tmpArray = tmpArray2.byteswap()
            cellArray.append(tmpArray)
        fig = plt.figure()
        ax = fig.add_subplot(111, projection='3d')

        x = dataPoints[0]
        y = dataPoints[2]
        z = -dataPoints[1]

        vertices = cellArray

        tupleList = np.transpose(np.array([x, y, z]))
        for j in range(len(vertices)):
            vtx_x = [x[vertices[j][k]] for k in range(len(vertices[j]))]
            vtx_y = [y[vertices[j][k]] for k in range(len(vertices[j]))]
            vtx_z = [z[vertices[j][k]] for k in range(len(vertices[j]))]
            vtx = np.transpose([vtx_x, vtx_y, vtx_z])
            tri = Poly3DCollection([vtx])
            if (surfaceColor1[j] != 0x7fff):
                tri.set_color(rgba_to_hex(surfaceColor1[j]))
            else:
                # tri.set_color("#0xbc614e")
                tri.set_color(rgba_to_hex(surfaceColor2[j]))
            tri.set_edgecolor("#3f3f3f")
            ax.add_collection3d(tri)
        ax.set_xlabel("X")
        ax.set_ylabel("Y")
        ax.set_zlabel("Z")
        ax.set_xlim3d(min(x)*(1.1 if min(x) < 0 else 0.9),
                      max(x)*(1.1 if max(x) > 0 else 0.9))
        ax.set_ylim3d(min(y)*(1.1 if min(y) < 0 else 0.9),
                      max(y)*(1.1 if max(y) > 0 else 0.9))
        ax.set_zlim3d(min(z)*(1.1 if min(z) < 0 else 0.9),
                      max(z)*(1.1 if max(z) > 0 else 0.9))
        # ax.set_aspect("equal")
        plt.savefig("object"+str(i)+".png")
        plt.close()
        # plt.show()
"""
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
# Convert .csg files (readable by OpenSCAD) to .ob3 files (readable by the client)
if __name__ == "__main__":
max_files = 500
for i in range(0, max_files):
    if (os.path.isfile("object"+str(i)+".csg")):
        print("Processing file %d of %d (%.1f%%)" % (i, max_files-1,
                                                     100*(i)/(max_files-1)))
        with open("object"+str(i)+".csg", "r") as f:
            data_read=f.read()
        # Data points (x,y,z)
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

print(data_points)
print(faces_data)
