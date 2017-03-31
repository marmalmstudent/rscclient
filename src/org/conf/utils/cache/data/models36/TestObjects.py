import numpy as np
import scipy as sp
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D


object_names = {
    0: "tree2", 1: "tree", 2: "well", 3: "table", 4: "treestump", 5: "ladder",
    6: "ladderdown", 7: "chair", 8: "logpile", 9: "longtable", 10: "throne",
    11: "range", 12: "gravestone1", 13: "gravestone2", 14: "Bigbed", 15: "bed",
    16: "barpumps", 17: "ChestOpen", 18: "ChestClosed", 19: "altar",
    20: "wallpost", 21: "supportnw", 22: "barrel", 23: "bench", 24: "portrait",
    25: "candles", 26: "fountain", 27: "landscape", 28: "mill", 29: "counter",
    30: "market", 31: "target", 32: "palm2", 33: "palm", 34: "fern",
    35: "cactus", 36: "bullrushes", 37: "flower", 38: "mushroom", 39: "coffin",
    40: "coffin2", 41: "woodenstairs", 42: "woodenstairsdown",
    43: "stonestairs", 44: "stonestairsdown", 45: "woodenrailing",
    46: "marblepillar", 47: "bookcase", 48: "sink", 49: "sworddummy",
    50: "anvil", 51: "torcha1", 52: "milltop", 53: "millbase", 54: "cart",
    55: "sacks", 56: "cupboard", 57: "metalgateclosed", 58: "metalgateopen",
    59: "woodengateopen", 60: "woodengateclosed", 61: "signpost",
    62: "doubledoorsopen", 63: "doubledoorsclosed", 64: "henge", 65: "dolmen",
    66: "deadtree1", 67: "cupboardopen", 68: "wheat", 69: "shopsign",
    70: "windmillsail", 71: "pipe&drain", 72: "manholeclosed",
    73: "manholeopen", 74: "wallpipe", 75: "firea1", 76: "rocks1", 77: "rocks2",
    78: "copperrock1", 79: "ironrock1", 80: "tinrock1", 81: "mithrilrock1",
    82: "adamiterock1", 83: "coalrock1", 84: "goldrock1", 85: "clayrock1",
    86: "ceilingweb", 87: "floorweb", 88: "furnace", 89: "madmachine",
    90: "spinningwheel", 91: "leverup", 92: "leverdown", 93: "compost",
    94: "skulltorcha1", 95: "chaosaltar", 96: "wallshield", 97: "wallgrill",
    98: "cauldron", 99: "minecart", 100: "trackbuffer", 101: "trackcurve",
    102: "trackpoints", 103: "trackstraight", 104: "hole", 105: "Shipfront",
    106: "Shipmiddle", 107: "Shipback", 108: "bluriterock1", 109: "potteryoven",
    110: "potterywheel", 111: "crate", 112: "potato", 113: "fishing",
    114: "silverrock1", 115: "smashedtable", 116: "nastyfungus",
    117: "smashedchair", 118: "brokenpillar", 119: "fallentree",
    120: "dangersign", 121: "runiterock1", 122: "runiteruck1",
    123: "largegrave", 124: "curvedbone", 125: "largebone", 126: "carcass",
    127: "animalskull", 128: "vine", 129: "vinecorner", 130: "vinejunction",
    131: "brokenwall", 132: "dramentree", 133: "Doormat", 134: "rock3",
    135: "bigtable", 136: "fireplacea1", 137: "bigegg", 138: "eggs",
    139: "stalagmites", 140: "stool", 141: "wallbench", 142: "bigroundtable",
    143: "roundtable", 144: "bush1", 145: "bush2", 146: "blueflower",
    147: "smallfern", 148: "giantcrystal", 149: "beehive", 150: "marblearch",
    151: "obelisk", 152: "sandpit", 153: "oaktree", 154: "willowtree",
    155: "mapletree", 156: "yewtree", 157: "magictree", 158: "flax",
    159: "brokencart", 160: "clockpoleblue", 161: "clockpolered",
    162: "clockpolepurple", 163: "clockpoleblack", 164: "wallclockface",
    165: "leverbracket", 166: "metalgateclosed2", 167: "metalgateopen2",
    168: "Foodtrough", 169: "spearwall", 170: "hornedskull", 171: "toplesstree",
    172: "catabow", 173: "catabowarrow", 174: "ent", 175: "khazardwall",
    176: "jungle tree 2", 177: "jungle tree 1", 178: "jungle fern textured",
    179: "jungle fern textured 2", 180: "jungle fern textured 3",
    181: "jungle fern textured 4", 182: "jungle fly trap", 183: "jungle fern",
    184: "jungle spikey fern", 185: "jungle strange plant",
    186: "jungle strange plant 2", 187: "jungle medium size plant",
    188: "jungle statue", 189: "deadtree2", 190: "doubledoorframe",
    191: "sewervalve", 192: "caveentrance", 193: "logbridgelow",
    194: "logbridgehigh", 195: "treeplatformhigh", 196: "treeplatformlow",
    197: "largetreeplatformlow", 198: "largetreeplatformhigh",
    199: "logbridgecurvedhigh", 200: "logbridgecurvedlow",
    201: "treeplatformlow2", 202: "treeplatformhigh2", 203: "lograft",
    204: "hazeeltomb", 205: "ardoungewall", 206: "ardoungewallcorner",
    207: "mudpatch", 208: "mudpile", 209: "largesewerpipe", 210: "fishingcrane",
    211: "rowboat", 212: "rowboatsinking", 213: "fishingcranerot1",
    214: "fishingcranerot2", 215: "waterfall", 216: "deadtree2base",
    217: "elventomb", 218: "waterfalllev1", 219: "waterfalllev2",
    220: "stonestand", 221: "glarialsstatue", 222: "baxtorianchalice",
    223: "baxtorianchalicelow", 224: "brokenlograft", 225: "watchtower",
    226: "ropeladder", 227: "gallows", 228: "grand tree-lev 0",
    229: "logbridge lev0", 230: "gnomewatchtower lev0",
    231: "logbridgejunction lev0", 232: "climbing_rocks", 233: "corner_ledge",
    234: "straight_ledge", 235: "log_balance1", 236: "log_balance2",
    237: "mossyrock", 238: "rocktile", 239: "tribalstature",
    240: "grand treeinside-lev 0", 241: "grand treeinside-lev 1",
    242: "grand treeinside-lev 2", 243: "grand tree-lev 1",
    244: "grand tree-lev 2", 245: "hillsidedoor", 246: "logbridgejunction lev1",
    247: "fourwayplatform-lev 0", 248: "fourwayplatform-lev 1",
    249: "logbridge lev1", 250: "logbridge lev2", 251: "gnomewatchtower lev1",
    252: "gnomewatchtower lev2", 253: "rockpoolwater", 254: "grand tree-lev 3",
    255: "blurberrybar", 256: "gemrock", 257: "obstical_ropeswing",
    258: "obstical_net", 259: "obstical_frame", 260: "tree_for_rope",
    261: "tree_with_rope", 262: "tree_with_vines", 263: "gnomefence",
    264: "beam", 265: "gnomesign", 266: "treeroot1", 267: "treeroot2",
    268: "gnomecage", 269: "gnomeglider", 270: "gnomeglidercrashed",
    271: "mudpiledown", 272: "gnomehamek", 273: "gnomegoal", 274: "stonedisc",
    275: "obstical_pipe", 276: "spikedpit-low", 277: "spikedpit",
    278: "bridge section 1", 279: "bridge section 2", 280: "bridge section 3",
    281: "cave bridge support", 282: "cave platform small", 283: "gnomefence2",
    284: "bridge section collapsed", 285: "bridge section collapsed2",
    286: "rams skull door", 287: "rams skull dooropen", 288: "caveentrance2",
    289: "cave old bridge", 290: "cave old bridgedown",
    291: "cave large stagamite", 292: "cave small stagamite", 293: "cave rock1",
    294: "cave ledge", 295: "cave lever", 296: "cave large stagatite",
    297: "cave small stagatite", 298: "cave extra large stagatite",
    299: "cave swampbubbles", 300: "cave rocktrap1", 301: "cave rocktrap1a",
    302: "cave swamprocks", 303: "cave grilltrap", 304: "cave grilltrapa",
    305: "cave temple", 306: "cave grilltrapa up", 307: "cave grillcage",
    308: "cave grillcageup", 309: "cave speartrap", 310: "cave speartrapa",
    311: "cave furnace", 312: "cave well", 313: "cave tubetrap",
    314: "cave tubetrapa", 315: "cave tubetrapa rope", 316: "cave snaptrap",
    317: "cave snaptrapa", 318: "cave planks", 319: "cave bloodwell",
    320: "cave platform verysmall", 321: "cave carvings", 322: "cave wallgrill",
    323: "cave bolder", 324: "cave templedoor", 325: "cave platform small2",
    326: "cave smashedcage", 327: "cave bridge supportbase", 328: "clawsofiban",
    329: "bridge section corner", 330: "cave bridge stairs",
    331: "cave temple alter", 332: "cave templedooropen", 333: "zodiac",
    334: "telescope", 335: "cave pillar", 336: "dwarf multicannon",
    337: "sandyfootsteps", 338: "dwarf multicannon part1",
    339: "dwarf multicannon part2", 340: "dwarf multicannon part3",
    341: "small caveentrance2", 342: "signpost2", 343: "brownclimbingrocks",
    344: "ogre standard", 345: "liftwinch", 346: "rockcounter", 347: "liftbed",
    348: "rock cake counter", 349: "magearena colomn", 350: "magearena wall",
    351: "magearena corner", 352: "magearena tallwall",
    353: "magearena cornerfill", 354: "magearena tallcorner",
    355: "magearena plain wall", 356: "spellshock", 357: "magearena door",
    358: "cactuswatered", 359: "lightning1", 360: "poorbed", 361: "firespell1",
    362: "1-1light", 363: "1-1dark", 364: "1-3light", 365: "1-3dark",
    366: "2-2light", 367: "2-2dark", 368: "barrier1", 369: "halfburiedskeleton",
    370: "2-1light", 371: "largeurn", 372: "halfburiedskeleton2",
    373: "dugupsoil1", 374: "dugupsoil2", 375: "dugupsoil3",
    376: "sinkingshipfront", 377: "sinkingbarrel", 378: "shipleak",
    379: "shipleak2", 380: "barrelredcross", 381: "shipspray1",
    382: "shipspray2", 383: "ropeforclimbingbot", 384: "trawlernet-l",
    385: "trawlernet-r", 386: "trawlernet", 387: "totemtree1",
    388: "totemtree2", 389: "totemtree3", 390: "totemtree4", 391: "totemtree5",
    392: "rocksteps", 393: "clawspell1", 394: "Spellcharge1",
    395: "saradominstone", 396: "guthixstone", 397: "zamorakstone",
    398: "rockpool", 399: "Scaffoldsupport", 400: "ScaffoldsupportRope",
    401: "Shamancave", 402: "skeletonwithbag", 403: "totemtreeevil",
    404: "totemtreegood", 405: "totemtreerotten2", 406: "totemtreerotten3",
    407: "totemtreerotten4", 408: "totemtreerotten5", 409: "torcha2",
    410: "torcha3", 411: "torcha4", 412: "skulltorcha2", 413: "skulltorcha3",
    414: "skulltorcha4", 415: "firea2", 416: "firea3", 417: "fireplacea2",
    418: "fireplacea3", 419: "firespell2", 420: "firespell3", 421: "lightning2",
    422: "lightning3", 423: "clawspell2", 424: "clawspell3", 425: "clawspell4",
    426: "clawspell5", 427: "spellcharge2", 428: "spellcharge3"}

f = open("object30.ob3", "rb")
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
dataPoints = sp.reshape(dataPoints, (3, nPoints))
print(dataPoints[0], dataPoints[2], dataPoints[1])
[X, Y] = np.meshgrid(dataPoints[0], dataPoints[2])
Z = dataPoints[1]*np.eye(len(dataPoints[1]))

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
# ax.plot_surface(X, Y, Z)
ax.plot(dataPoints[0], dataPoints[2], dataPoints[1], linestyle='--')
"""
for i in range(0, np.size(dataPoints, axis=1)-4, 1):
    ax.plot(dataPoints[0][i:i+4], dataPoints[2][i:i+4], dataPoints[1][i:i+4])
"""
# ax.set_xlim([-128, 127])
# ax.set_ylim([-128, 127])
# ax.set_zlim([-128, 127])
plt.show()
