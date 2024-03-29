/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

/*
	ImageTracerAndroid.java
	(Android version with android.graphics. See ImageTracer.java for the desktop version.)
	Simple raster image tracer and vectorizer written in Java. This is a port of imagetracer.js.
	by András Jankovics 2015, 2016
	andras@jankovics.net

 */

package ru.tech.imageresizershrinker.feature.svg.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ImageTracerAndroid {

    public static String versionnumber = "1.1.2";
    // Lookup tables for pathscan
    static byte[] pathscan_dir_lookup = {0, 0, 3, 0, 1, 0, 3, 0, 0, 3, 3, 1, 0, 3, 0, 0};
    static boolean[] pathscan_holepath_lookup = {false, false, false, false, false, false, false, true, false, false, false, true, false, true, true, false};
    // pathscan_combined_lookup[ arr[py][px] ][ dir ] = [nextarrpypx, nextdir, deltapx, deltapy];
    static byte[][][] pathscan_combined_lookup = {
            {{-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}},// arr[py][px]==0 is invalid
            {{0, 1, 0, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}, {0, 2, -1, 0}},
            {{-1, -1, -1, -1}, {-1, -1, -1, -1}, {0, 1, 0, -1}, {0, 0, 1, 0}},
            {{0, 0, 1, 0}, {-1, -1, -1, -1}, {0, 2, -1, 0}, {-1, -1, -1, -1}},

            {{-1, -1, -1, -1}, {0, 0, 1, 0}, {0, 3, 0, 1}, {-1, -1, -1, -1}},
            {{13, 3, 0, 1}, {13, 2, -1, 0}, {7, 1, 0, -1}, {7, 0, 1, 0}},
            {{-1, -1, -1, -1}, {0, 1, 0, -1}, {-1, -1, -1, -1}, {0, 3, 0, 1}},
            {{0, 3, 0, 1}, {0, 2, -1, 0}, {-1, -1, -1, -1}, {-1, -1, -1, -1}},

            {{0, 3, 0, 1}, {0, 2, -1, 0}, {-1, -1, -1, -1}, {-1, -1, -1, -1}},
            {{-1, -1, -1, -1}, {0, 1, 0, -1}, {-1, -1, -1, -1}, {0, 3, 0, 1}},
            {{11, 1, 0, -1}, {14, 0, 1, 0}, {14, 3, 0, 1}, {11, 2, -1, 0}},
            {{-1, -1, -1, -1}, {0, 0, 1, 0}, {0, 3, 0, 1}, {-1, -1, -1, -1}},

            {{0, 0, 1, 0}, {-1, -1, -1, -1}, {0, 2, -1, 0}, {-1, -1, -1, -1}},
            {{-1, -1, -1, -1}, {-1, -1, -1, -1}, {0, 1, 0, -1}, {0, 0, 1, 0}},
            {{0, 1, 0, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}, {0, 2, -1, 0}},
            {{-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}, {-1, -1, -1, -1}}// arr[py][px]==15 is invalid
    };
    // Gaussian kernels for blur
    static double[][] gks = {{0.27901, 0.44198, 0.27901}, {0.135336, 0.228569, 0.272192, 0.228569, 0.135336}, {0.086776, 0.136394, 0.178908, 0.195843, 0.178908, 0.136394, 0.086776},
            {0.063327, 0.093095, 0.122589, 0.144599, 0.152781, 0.144599, 0.122589, 0.093095, 0.063327}, {0.049692, 0.069304, 0.089767, 0.107988, 0.120651, 0.125194, 0.120651, 0.107988, 0.089767, 0.069304, 0.049692}};

    // Saving a String as a file
    public static void saveString(String filename, String str) throws Exception {
        File file = new File(filename);
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(str);
        bw.close();
    }

    // Loading a file to ImageData, ARGB byte order
    public static ImageData loadImageData(String filename) throws Exception {
        Bitmap image = BitmapFactory.decodeFile((new File(filename)).getAbsolutePath());
        return loadImageData(image);
    }


    ////////////////////////////////////////////////////////////
    //
    //  User friendly functions
    //
    ////////////////////////////////////////////////////////////

    public static ImageData loadImageData(Bitmap image) throws Exception {
        int width = image.getWidth();
        int height = image.getHeight();
        IntBuffer ib = IntBuffer.allocate(width * height);
        image.copyPixelsToBuffer(ib);
        int[] rawdata = ib.array();
        byte[] data = new byte[rawdata.length * 4];
        for (int i = 0; i < rawdata.length; i++) {
            data[(i * 4) + 3] = bytetrans((byte) (rawdata[i] >>> 24));
            data[(i * 4) + 2] = bytetrans((byte) (rawdata[i] >>> 16));
            data[(i * 4) + 1] = bytetrans((byte) (rawdata[i] >>> 8));
            data[i * 4] = bytetrans((byte) (rawdata[i]));
        }
        return new ImageData(width, height, data);
    }

    // The bitshift method in loadImageData creates signed bytes where -1 -> 255 unsigned ; -128 -> 128 unsigned ;
    // 127 -> 127 unsigned ; 0 -> 0 unsigned ; These will be converted to -128 (representing 0 unsigned) ...
    // 127 (representing 255 unsigned) and tosvgcolorstr will add +128 to create RGB values 0..255
    public static byte bytetrans(byte b) {
        if (b < 0) {
            return (byte) (b + 128);
        } else {
            return (byte) (b - 128);
        }
    }

    // Loading an image from a file, tracing when loaded, then returning the SVG String
    public static String imageToSVG(String filename, HashMap<String, Float> options, byte[][] palette) throws Exception {
        options = checkoptions(options);
        ImageData imgd = loadImageData(filename);
        return imagedataToSVG(imgd, options, palette);
    }// End of imageToSVG()

    public static String imageToSVG(Bitmap bitmap, HashMap<String, Float> options, byte[][] palette) throws Exception {
        options = checkoptions(options);
        ImageData imgd = loadImageData(bitmap);
        return imagedataToSVG(imgd, options, palette);
    }// End of imageToSVG()

    // Tracing ImageData, then returning the SVG String
    public static String imagedataToSVG(ImageData imgd, HashMap<String, Float> options, byte[][] palette) {
        options = checkoptions(options);
        IndexedImage ii = imagedataToTracedata(imgd, options, palette);
        return getsvgstring(ii, options);
    }// End of imagedataToSVG()

    // Tracing ImageData, then returning IndexedImage with tracedata in layers
    public static IndexedImage imagedataToTracedata(ImageData imgd, HashMap<String, Float> options, byte[][] palette) {
        // 1. Color quantization
        IndexedImage ii = colorquantization(imgd, palette, options);
        // 2. Layer separation and edge detection
        int[][][] rawlayers = layering(ii);
        // 3. Batch pathscan
        ArrayList<ArrayList<ArrayList<Integer[]>>> bps = batchpathscan(rawlayers, (int) (Math.floor(options.get("pathomit"))));
        // 4. Batch interpollation
        ArrayList<ArrayList<ArrayList<Double[]>>> bis = batchinternodes(bps);
        // 5. Batch tracing
        ii.layers = batchtracelayers(bis, options.get("ltres"), options.get("qtres"));
        return ii;
    }// End of imagedataToTracedata()


    // creating options object, setting defaults for missing values
    public static HashMap<String, Float> checkoptions(HashMap<String, Float> options) {
        if (options == null) {
            options = new HashMap<String, Float>();
        }
        // Tracing
        if (!options.containsKey("ltres")) {
            options.put("ltres", 1f);
        }
        if (!options.containsKey("qtres")) {
            options.put("qtres", 1f);
        }
        if (!options.containsKey("pathomit")) {
            options.put("pathomit", 8f);
        }
        // Color quantization
        if (!options.containsKey("colorsampling")) {
            options.put("colorsampling", 1f);
        }
        if (!options.containsKey("numberofcolors")) {
            options.put("numberofcolors", 16f);
        }
        if (!options.containsKey("mincolorratio")) {
            options.put("mincolorratio", 0.02f);
        }
        if (!options.containsKey("colorquantcycles")) {
            options.put("colorquantcycles", 3f);
        }
        // SVG rendering
        if (!options.containsKey("scale")) {
            options.put("scale", 1f);
        }
        if (!options.containsKey("simplifytolerance")) {
            options.put("simplifytolerance", 0f);
        }
        if (!options.containsKey("roundcoords")) {
            options.put("roundcoords", 1f);
        }
        if (!options.containsKey("lcpr")) {
            options.put("lcpr", 0f);
        }
        if (!options.containsKey("qcpr")) {
            options.put("qcpr", 0f);
        }
        if (!options.containsKey("desc")) {
            options.put("desc", 1f);
        }
        if (!options.containsKey("viewbox")) {
            options.put("viewbox", 0f);
        }
        // Blur
        if (!options.containsKey("blurradius")) {
            options.put("blurradius", 0f);
        }
        if (!options.containsKey("blurdelta")) {
            options.put("blurdelta", 20f);
        }

        return options;
    }// End of checkoptions()


    ////////////////////////////////////////////////////////////
    //
    //  Vectorizing functions
    //
    ////////////////////////////////////////////////////////////

    // 1. Color quantization repeated "cycles" times, based on K-means clustering
    // https://en.wikipedia.org/wiki/Color_quantization    https://en.wikipedia.org/wiki/K-means_clustering
    public static IndexedImage colorquantization(ImageData imgd, byte[][] palette, HashMap<String, Float> options) {
        int numberofcolors = (int) Math.floor(options.get("numberofcolors"));
        float minratio = options.get("mincolorratio");
        int cycles = (int) Math.floor(options.get("colorquantcycles"));
        // Creating indexed color array arr which has a boundary filled with -1 in every direction
        int[][] arr = new int[imgd.height + 2][imgd.width + 2];
        for (int j = 0; j < (imgd.height + 2); j++) {
            arr[j][0] = -1;
            arr[j][imgd.width + 1] = -1;
        }
        for (int i = 0; i < (imgd.width + 2); i++) {
            arr[0][i] = -1;
            arr[imgd.height + 1][i] = -1;
        }

        int idx = 0, cd, cdl, ci, c1, c2, c3, c4;

        // Use custom palette if pal is defined or sample or generate custom length palette
        if (palette == null) {
            if (options.get("colorsampling") != 0) {
                palette = samplepalette(numberofcolors, imgd);
            } else {
                palette = generatepalette(numberofcolors);
            }
        }

        // Selective Gaussian blur preprocessing
        if (options.get("blurradius") > 0) {
            imgd = blur(imgd, options.get("blurradius"), options.get("blurdelta"));
        }

        long[][] paletteacc = new long[palette.length][5];

        // Repeat clustering step "cycles" times
        for (int cnt = 0; cnt < cycles; cnt++) {

            // Average colors from the second iteration
            if (cnt > 0) {
                // averaging paletteacc for palette
                float ratio;
                for (int k = 0; k < palette.length; k++) {
                    // averaging
                    if (paletteacc[k][3] > 0) {
                        palette[k][0] = (byte) (-128 + (paletteacc[k][0] / paletteacc[k][4]));
                        palette[k][1] = (byte) (-128 + (paletteacc[k][1] / paletteacc[k][4]));
                        palette[k][2] = (byte) (-128 + (paletteacc[k][2] / paletteacc[k][4]));
                        palette[k][3] = (byte) (-128 + (paletteacc[k][3] / paletteacc[k][4]));
                    }
                    ratio = (float) ((double) (paletteacc[k][4]) / (double) (imgd.width * imgd.height));

                    // Randomizing a color, if there are too few pixels and there will be a new cycle
                    if ((ratio < minratio) && (cnt < (cycles - 1))) {
                        palette[k][0] = (byte) (-128 + Math.floor(Math.random() * 255));
                        palette[k][1] = (byte) (-128 + Math.floor(Math.random() * 255));
                        palette[k][2] = (byte) (-128 + Math.floor(Math.random() * 255));
                        palette[k][3] = (byte) (-128 + Math.floor(Math.random() * 255));
                    }

                }// End of palette loop
            }// End of Average colors from the second iteration

            // Reseting palette accumulator for averaging
            for (int i = 0; i < palette.length; i++) {
                paletteacc[i][0] = 0;
                paletteacc[i][1] = 0;
                paletteacc[i][2] = 0;
                paletteacc[i][3] = 0;
                paletteacc[i][4] = 0;
            }

            // loop through all pixels
            for (int j = 0; j < imgd.height; j++) {
                for (int i = 0; i < imgd.width; i++) {

                    idx = ((j * imgd.width) + i) * 4;

                    // find closest color from palette by measuring (rectilinear) color distance between this pixel and all palette colors
                    cdl = 256 + 256 + 256 + 256;
                    ci = 0;
                    for (int k = 0; k < palette.length; k++) {

                        // In my experience, https://en.wikipedia.org/wiki/Rectilinear_distance works better than https://en.wikipedia.org/wiki/Euclidean_distance
                        c1 = Math.abs(palette[k][0] - imgd.data[idx]);
                        c2 = Math.abs(palette[k][1] - imgd.data[idx + 1]);
                        c3 = Math.abs(palette[k][2] - imgd.data[idx + 2]);
                        c4 = Math.abs(palette[k][3] - imgd.data[idx + 3]);
                        cd = c1 + c2 + c3 + (c4 * 4); // weighted alpha seems to help images with transparency

                        // Remember this color if this is the closest yet
                        if (cd < cdl) {
                            cdl = cd;
                            ci = k;
                        }

                    }// End of palette loop

                    // add to palettacc
                    paletteacc[ci][0] += 128 + imgd.data[idx];
                    paletteacc[ci][1] += 128 + imgd.data[idx + 1];
                    paletteacc[ci][2] += 128 + imgd.data[idx + 2];
                    paletteacc[ci][3] += 128 + imgd.data[idx + 3];
                    paletteacc[ci][4]++;

                    arr[j + 1][i + 1] = ci;
                }// End of i loop
            }// End of j loop

        }// End of Repeat clustering step "cycles" times

        return new IndexedImage(arr, palette);
    }// End of colorquantization


    // Generating a palette with numberofcolors, array[numberofcolors][4] where [i][0] = R ; [i][1] = G ; [i][2] = B ; [i][3] = A
    public static byte[][] generatepalette(int numberofcolors) {
        byte[][] palette = new byte[numberofcolors][4];
        if (numberofcolors < 8) {

            // Grayscale
            double graystep = 255.0 / (numberofcolors - 1);
            for (byte ccnt = 0; ccnt < numberofcolors; ccnt++) {
                palette[ccnt][0] = (byte) (-128 + Math.round(ccnt * graystep));
                palette[ccnt][1] = (byte) (-128 + Math.round(ccnt * graystep));
                palette[ccnt][2] = (byte) (-128 + Math.round(ccnt * graystep));
                palette[ccnt][3] = (byte) 127;
            }

        } else {

            // RGB color cube
            int colorqnum = (int) Math.floor(Math.pow(numberofcolors, 1.0 / 3.0)); // Number of points on each edge on the RGB color cube
            int colorstep = (int) Math.floor(255 / (colorqnum - 1)); // distance between points
            int ccnt = 0;
            for (int rcnt = 0; rcnt < colorqnum; rcnt++) {
                for (int gcnt = 0; gcnt < colorqnum; gcnt++) {
                    for (int bcnt = 0; bcnt < colorqnum; bcnt++) {
                        palette[ccnt][0] = (byte) (-128 + (rcnt * colorstep));
                        palette[ccnt][1] = (byte) (-128 + (gcnt * colorstep));
                        palette[ccnt][2] = (byte) (-128 + (bcnt * colorstep));
                        palette[ccnt][3] = (byte) 127;
                        ccnt++;
                    }// End of blue loop
                }// End of green loop
            }// End of red loop

            // Rest is random
            for (int rcnt = ccnt; rcnt < numberofcolors; rcnt++) {
                palette[ccnt][0] = (byte) (-128 + Math.floor(Math.random() * 255));
                palette[ccnt][1] = (byte) (-128 + Math.floor(Math.random() * 255));
                palette[ccnt][2] = (byte) (-128 + Math.floor(Math.random() * 255));
                palette[ccnt][3] = (byte) (-128 + Math.floor(Math.random() * 255));
            }

        }// End of numberofcolors check

        return palette;
    }

    // End of generatepalette()


    public static byte[][] samplepalette(int numberofcolors, ImageData imgd) {
        int idx = 0;
        byte[][] palette = new byte[numberofcolors][4];
        for (int i = 0; i < numberofcolors; i++) {
            idx = (int) (Math.floor((Math.random() * imgd.data.length) / 4) * 4);
            palette[i][0] = imgd.data[idx];
            palette[i][1] = imgd.data[idx + 1];
            palette[i][2] = imgd.data[idx + 2];
            palette[i][3] = imgd.data[idx + 3];
        }
        return palette;
    }// End of samplepalette()


    // 2. Layer separation and edge detection
    // Edge node types ( ▓:light or 1; ░:dark or 0 )
    // 12  ░░  ▓░  ░▓  ▓▓  ░░  ▓░  ░▓  ▓▓  ░░  ▓░  ░▓  ▓▓  ░░  ▓░  ░▓  ▓▓
    // 48  ░░  ░░  ░░  ░░  ░▓  ░▓  ░▓  ░▓  ▓░  ▓░  ▓░  ▓░  ▓▓  ▓▓  ▓▓  ▓▓
    //     0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15
    //
    public static int[][][] layering(IndexedImage ii) {
        // Creating layers for each indexed color in arr
        int val = 0, aw = ii.array[0].length, ah = ii.array.length, n1, n2, n3, n4, n5, n6, n7, n8;
        int[][][] layers = new int[ii.palette.length][ah][aw];

        // Looping through all pixels and calculating edge node type
        for (int j = 1; j < (ah - 1); j++) {
            for (int i = 1; i < (aw - 1); i++) {

                // This pixel's indexed color
                val = ii.array[j][i];

                // Are neighbor pixel colors the same?
                n1 = ii.array[j - 1][i - 1] == val ? 1 : 0;
                n2 = ii.array[j - 1][i] == val ? 1 : 0;
                n3 = ii.array[j - 1][i + 1] == val ? 1 : 0;
                n4 = ii.array[j][i - 1] == val ? 1 : 0;
                n5 = ii.array[j][i + 1] == val ? 1 : 0;
                n6 = ii.array[j + 1][i - 1] == val ? 1 : 0;
                n7 = ii.array[j + 1][i] == val ? 1 : 0;
                n8 = ii.array[j + 1][i + 1] == val ? 1 : 0;

                // this pixel"s type and looking back on previous pixels
                layers[val][j + 1][i + 1] = 1 + (n5 * 2) + (n8 * 4) + (n7 * 8);
                if (n4 == 0) {
                    layers[val][j + 1][i] = 2 + (n7 * 4) + (n6 * 8);
                }
                if (n2 == 0) {
                    layers[val][j][i + 1] = (n3 * 2) + (n5 * 4) + 8;
                }
                if (n1 == 0) {
                    layers[val][j][i] = (n2 * 2) + 4 + (n4 * 8);
                }

            }// End of i loop
        }// End of j loop

        return layers;
    }// End of layering()

    // 3. Walking through an edge node array, discarding edge node types 0 and 15 and creating paths from the rest.
    // Walk directions (dir): 0 > ; 1 ^ ; 2 < ; 3 v
    // Edge node types ( ▓:light or 1; ░:dark or 0 )
    // ░░  ▓░  ░▓  ▓▓  ░░  ▓░  ░▓  ▓▓  ░░  ▓░  ░▓  ▓▓  ░░  ▓░  ░▓  ▓▓
    // ░░  ░░  ░░  ░░  ░▓  ░▓  ░▓  ░▓  ▓░  ▓░  ▓░  ▓░  ▓▓  ▓▓  ▓▓  ▓▓
    // 0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15
    //
    public static ArrayList<ArrayList<Integer[]>> pathscan(int[][] arr, float pathomit) {
        ArrayList<ArrayList<Integer[]>> paths = new ArrayList<ArrayList<Integer[]>>();
        ArrayList<Integer[]> thispath;
        int px = 0, py = 0, w = arr[0].length, h = arr.length, dir = 0;
        boolean pathfinished = true, holepath = false;
        byte[] lookuprow;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if ((arr[j][i] != 0) && (arr[j][i] != 15)) {

                    // Init
                    px = i;
                    py = j;
                    paths.add(new ArrayList<Integer[]>());
                    thispath = paths.get(paths.size() - 1);
                    pathfinished = false;

                    // fill paths will be drawn, but hole paths are also required to remove unnecessary edge nodes
                    dir = pathscan_dir_lookup[arr[py][px]];
                    holepath = pathscan_holepath_lookup[arr[py][px]];

                    // Path points loop
                    while (!pathfinished) {

                        // New path point
                        thispath.add(new Integer[3]);
                        thispath.get(thispath.size() - 1)[0] = px - 1;
                        thispath.get(thispath.size() - 1)[1] = py - 1;
                        thispath.get(thispath.size() - 1)[2] = arr[py][px];

                        // Next: look up the replacement, direction and coordinate changes = clear this cell, turn if required, walk forward
                        lookuprow = pathscan_combined_lookup[arr[py][px]][dir];
                        arr[py][px] = lookuprow[0];
                        dir = lookuprow[1];
                        px += lookuprow[2];
                        py += lookuprow[3];

                        // Close path
                        if (((px - 1) == thispath.get(0)[0]) && ((py - 1) == thispath.get(0)[1])) {
                            pathfinished = true;
                            // Discarding 'hole' type paths and paths shorter than pathomit
                            if ((holepath) || (thispath.size() < pathomit)) {
                                paths.remove(thispath);
                            }
                        }

                    }// End of Path points loop

                }// End of Follow path

            }// End of i loop
        }// End of j loop

        return paths;
    }// End of pathscan()

    // 3. Batch pathscan
    public static ArrayList<ArrayList<ArrayList<Integer[]>>> batchpathscan(int[][][] layers, float pathomit) {
        ArrayList<ArrayList<ArrayList<Integer[]>>> bpaths = new ArrayList<ArrayList<ArrayList<Integer[]>>>();
        for (int[][] layer : layers) {
            bpaths.add(pathscan(layer, pathomit));
        }
        return bpaths;
    }

    // 4. interpolating between path points for nodes with 8 directions ( East, SouthEast, S, SW, W, NW, N, NE )
    public static ArrayList<ArrayList<Double[]>> internodes(ArrayList<ArrayList<Integer[]>> paths) {
        ArrayList<ArrayList<Double[]>> ins = new ArrayList<ArrayList<Double[]>>();
        ArrayList<Double[]> thisinp;
        Double[] thispoint, nextpoint = new Double[2];
        Integer[] pp1, pp2, pp3;
        int palen = 0, nextidx = 0, nextidx2 = 0;

        // paths loop
        for (int pacnt = 0; pacnt < paths.size(); pacnt++) {
            ins.add(new ArrayList<Double[]>());
            thisinp = ins.get(ins.size() - 1);
            palen = paths.get(pacnt).size();
            // pathpoints loop
            for (int pcnt = 0; pcnt < palen; pcnt++) {

                // interpolate between two path points
                nextidx = (pcnt + 1) % palen;
                nextidx2 = (pcnt + 2) % palen;
                thisinp.add(new Double[3]);
                thispoint = thisinp.get(thisinp.size() - 1);
                pp1 = paths.get(pacnt).get(pcnt);
                pp2 = paths.get(pacnt).get(nextidx);
                pp3 = paths.get(pacnt).get(nextidx2);
                thispoint[0] = (pp1[0] + pp2[0]) / 2.0;
                thispoint[1] = (pp1[1] + pp2[1]) / 2.0;
                nextpoint[0] = (pp2[0] + pp3[0]) / 2.0;
                nextpoint[1] = (pp2[1] + pp3[1]) / 2.0;

                // line segment direction to the next point
                if (thispoint[0] < nextpoint[0]) {
                    if (thispoint[1] < nextpoint[1]) {
                        thispoint[2] = 1.0;
                    }// SouthEast
                    else if (thispoint[1] > nextpoint[1]) {
                        thispoint[2] = 7.0;
                    }// NE
                    else {
                        thispoint[2] = 0.0;
                    } // E
                } else if (thispoint[0] > nextpoint[0]) {
                    if (thispoint[1] < nextpoint[1]) {
                        thispoint[2] = 3.0;
                    }// SW
                    else if (thispoint[1] > nextpoint[1]) {
                        thispoint[2] = 5.0;
                    }// NW
                    else {
                        thispoint[2] = 4.0;
                    }// W
                } else {
                    if (thispoint[1] < nextpoint[1]) {
                        thispoint[2] = 2.0;
                    }// S
                    else if (thispoint[1] > nextpoint[1]) {
                        thispoint[2] = 6.0;
                    }// N
                    else {
                        thispoint[2] = 8.0;
                    }// center, this should not happen
                }

            }// End of pathpoints loop
        }// End of paths loop
        return ins;
    }// End of internodes()

    // 4. Batch interpollation
    static ArrayList<ArrayList<ArrayList<Double[]>>> batchinternodes(ArrayList<ArrayList<ArrayList<Integer[]>>> bpaths) {
        ArrayList<ArrayList<ArrayList<Double[]>>> binternodes = new ArrayList<ArrayList<ArrayList<Double[]>>>();
        for (int k = 0; k < bpaths.size(); k++) {
            binternodes.add(internodes(bpaths.get(k)));
        }
        return binternodes;
    }

    public static ArrayList<Double[]> tracepath(ArrayList<Double[]> path, float ltreshold, float qtreshold) {
        int pcnt = 0, seqend = 0;
        double segtype1, segtype2;
        ArrayList<Double[]> smp = new ArrayList<Double[]>();
        //Double [] thissegment;
        int pathlength = path.size();

        while (pcnt < pathlength) {
            // 5.1. Find sequences of points with only 2 segment types
            segtype1 = path.get(pcnt)[2];
            segtype2 = -1;
            seqend = pcnt + 1;
            while (
                    ((path.get(seqend)[2] == segtype1) || (path.get(seqend)[2] == segtype2) || (segtype2 == -1))
                            && (seqend < (pathlength - 1))) {
                if ((path.get(seqend)[2] != segtype1) && (segtype2 == -1)) {
                    segtype2 = path.get(seqend)[2];
                }
                seqend++;
            }
            if (seqend == (pathlength - 1)) {
                seqend = 0;
            }

            // 5.2. - 5.6. Split sequence and recursively apply 5.2. - 5.6. to startpoint-splitpoint and splitpoint-endpoint sequences
            smp.addAll(fitseq(path, ltreshold, qtreshold, pcnt, seqend));
            // 5.7. TODO? If splitpoint-endpoint is a spline, try to add new points from the next sequence

            // forward pcnt;
            if (seqend > 0) {
                pcnt = seqend;
            } else {
                pcnt = pathlength;
            }

        }// End of pcnt loop

        return smp;

    }// End of tracepath()

    // 5.2. - 5.6. recursively fitting a straight or quadratic line segment on this sequence of path nodes,
    // called from tracepath()
    public static ArrayList<Double[]> fitseq(ArrayList<Double[]> path, float ltreshold, float qtreshold, int seqstart, int seqend) {
        ArrayList<Double[]> segment = new ArrayList<Double[]>();
        Double[] thissegment;
        int pathlength = path.size();

        // return if invalid seqend
        if ((seqend > pathlength) || (seqend < 0)) {
            return segment;
        }

        int errorpoint = seqstart;
        boolean curvepass = true;
        double px, py, dist2, errorval = 0;
        double tl = (seqend - seqstart);
        if (tl < 0) {
            tl += pathlength;
        }
        double vx = (path.get(seqend)[0] - path.get(seqstart)[0]) / tl,
                vy = (path.get(seqend)[1] - path.get(seqstart)[1]) / tl;

        // 5.2. Fit a straight line on the sequence
        int pcnt = (seqstart + 1) % pathlength;
        double pl;
        while (pcnt != seqend) {
            pl = pcnt - seqstart;
            if (pl < 0) {
                pl += pathlength;
            }
            px = path.get(seqstart)[0] + (vx * pl);
            py = path.get(seqstart)[1] + (vy * pl);
            dist2 = ((path.get(pcnt)[0] - px) * (path.get(pcnt)[0] - px)) + ((path.get(pcnt)[1] - py) * (path.get(pcnt)[1] - py));
            if (dist2 > ltreshold) {
                curvepass = false;
            }
            if (dist2 > errorval) {
                errorpoint = pcnt;
                errorval = dist2;
            }
            pcnt = (pcnt + 1) % pathlength;
        }

        // return straight line if fits
        if (curvepass) {
            segment.add(new Double[7]);
            thissegment = segment.get(segment.size() - 1);
            thissegment[0] = 1.0;
            thissegment[1] = path.get(seqstart)[0];
            thissegment[2] = path.get(seqstart)[1];
            thissegment[3] = path.get(seqend)[0];
            thissegment[4] = path.get(seqend)[1];
            thissegment[5] = 0.0;
            thissegment[6] = 0.0;
            return segment;
        }

        // 5.3. If the straight line fails (an error>ltreshold), find the point with the biggest error
        int fitpoint = errorpoint;
        curvepass = true;
        errorval = 0;

        // 5.4. Fit a quadratic spline through this point, measure errors on every point in the sequence
        // helpers and projecting to get control point
        double t = (fitpoint - seqstart) / tl, t1 = (1.0 - t) * (1.0 - t), t2 = 2.0 * (1.0 - t) * t, t3 = t * t;
        double cpx = (((t1 * path.get(seqstart)[0]) + (t3 * path.get(seqend)[0])) - path.get(fitpoint)[0]) / -t2,
                cpy = (((t1 * path.get(seqstart)[1]) + (t3 * path.get(seqend)[1])) - path.get(fitpoint)[1]) / -t2;

        // Check every point
        pcnt = seqstart + 1;
        while (pcnt != seqend) {

            t = (pcnt - seqstart) / tl;
            t1 = (1.0 - t) * (1.0 - t);
            t2 = 2.0 * (1.0 - t) * t;
            t3 = t * t;
            px = (t1 * path.get(seqstart)[0]) + (t2 * cpx) + (t3 * path.get(seqend)[0]);
            py = (t1 * path.get(seqstart)[1]) + (t2 * cpy) + (t3 * path.get(seqend)[1]);

            dist2 = ((path.get(pcnt)[0] - px) * (path.get(pcnt)[0] - px)) + ((path.get(pcnt)[1] - py) * (path.get(pcnt)[1] - py));

            if (dist2 > qtreshold) {
                curvepass = false;
            }
            if (dist2 > errorval) {
                errorpoint = pcnt;
                errorval = dist2;
            }
            pcnt = (pcnt + 1) % pathlength;
        }

        // return spline if fits
        if (curvepass) {
            segment.add(new Double[7]);
            thissegment = segment.get(segment.size() - 1);
            thissegment[0] = 2.0;
            thissegment[1] = path.get(seqstart)[0];
            thissegment[2] = path.get(seqstart)[1];
            thissegment[3] = cpx;
            thissegment[4] = cpy;
            thissegment[5] = path.get(seqend)[0];
            thissegment[6] = path.get(seqend)[1];
            return segment;
        }

        // 5.5. If the spline fails (an error>qtreshold), find the point with the biggest error,
        // set splitpoint = (fitting point + errorpoint)/2
        int splitpoint = (fitpoint + errorpoint) / 2;

        // 5.6. Split sequence and recursively apply 5.2. - 5.6. to startpoint-splitpoint and splitpoint-endpoint sequences
        segment = fitseq(path, ltreshold, qtreshold, seqstart, splitpoint);
        segment.addAll(fitseq(path, ltreshold, qtreshold, splitpoint, seqend));
        return segment;

    }// End of fitseq()

    // 5. Batch tracing paths
    public static ArrayList<ArrayList<Double[]>> batchtracepaths(ArrayList<ArrayList<Double[]>> internodepaths, float ltres, float qtres) {
        ArrayList<ArrayList<Double[]>> btracedpaths = new ArrayList<ArrayList<Double[]>>();
        for (int k = 0; k < internodepaths.size(); k++) {
            btracedpaths.add(tracepath(internodepaths.get(k), ltres, qtres));
        }
        return btracedpaths;
    }


    // 5. tracepath() : recursively trying to fit straight and quadratic spline segments on the 8 direction internode path

    // 5.1. Find sequences of points with only 2 segment types
    // 5.2. Fit a straight line on the sequence
    // 5.3. If the straight line fails (an error>ltreshold), find the point with the biggest error
    // 5.4. Fit a quadratic spline through errorpoint (project this to get controlpoint), then measure errors on every point in the sequence
    // 5.5. If the spline fails (an error>qtreshold), find the point with the biggest error, set splitpoint = (fitting point + errorpoint)/2
    // 5.6. Split sequence and recursively apply 5.2. - 5.7. to startpoint-splitpoint and splitpoint-endpoint sequences
    // 5.7. TODO? If splitpoint-endpoint is a spline, try to add new points from the next sequence

    // This returns an SVG Path segment as a double[7] where
    // segment[0] ==1.0 linear  ==2.0 quadratic interpolation
    // segment[1] , segment[2] : x1 , y1
    // segment[3] , segment[4] : x2 , y2 ; middle point of Q curve, endpoint of L line
    // segment[5] , segment[6] : x3 , y3 for Q curve, should be 0.0 , 0.0 for L line
    //
    // path type is discarded, no check for path.size < 3 , which should not happen

    // 5. Batch tracing layers
    public static ArrayList<ArrayList<ArrayList<Double[]>>> batchtracelayers(ArrayList<ArrayList<ArrayList<Double[]>>> binternodes, float ltres, float qtres) {
        ArrayList<ArrayList<ArrayList<Double[]>>> btbis = new ArrayList<ArrayList<ArrayList<Double[]>>>();
        for (int k = 0; k < binternodes.size(); k++) {
            btbis.add(batchtracepaths(binternodes.get(k), ltres, qtres));
        }
        return btbis;
    }

    public static float roundtodec(float val, float places) {
        return (float) (Math.round(val * Math.pow(10, places)) / Math.pow(10, places));
    }

    // Getting SVG path element string from a traced path
    public static void svgpathstring(StringBuilder sb, String desc, ArrayList<Double[]> segments, String colorstr, HashMap<String, Float> options) {
        float scale = options.get("scale"), lcpr = options.get("lcpr"), qcpr = options.get("qcpr"), roundcoords = (float) Math.floor(options.get("roundcoords"));
        // Path
        sb.append("<path ").append(desc).append(colorstr).append("d=\"").append("M ").append(segments.get(0)[1] * scale).append(" ").append(segments.get(0)[2] * scale).append(" ");

        if (roundcoords == -1) {
            for (int pcnt = 0; pcnt < segments.size(); pcnt++) {
                if (segments.get(pcnt)[0] == 1.0) {
                    sb.append("L ").append(segments.get(pcnt)[3] * scale).append(" ").append(segments.get(pcnt)[4] * scale).append(" ");
                } else {
                    sb.append("Q ").append(segments.get(pcnt)[3] * scale).append(" ").append(segments.get(pcnt)[4] * scale).append(" ").append(segments.get(pcnt)[5] * scale).append(" ").append(segments.get(pcnt)[6] * scale).append(" ");
                }
            }
        } else {
            for (int pcnt = 0; pcnt < segments.size(); pcnt++) {
                if (segments.get(pcnt)[0] == 1.0) {
                    sb.append("L ").append(roundtodec((float) (segments.get(pcnt)[3] * scale), roundcoords)).append(" ")
                            .append(roundtodec((float) (segments.get(pcnt)[4] * scale), roundcoords)).append(" ");
                } else {
                    sb.append("Q ").append(roundtodec((float) (segments.get(pcnt)[3] * scale), roundcoords)).append(" ")
                            .append(roundtodec((float) (segments.get(pcnt)[4] * scale), roundcoords)).append(" ")
                            .append(roundtodec((float) (segments.get(pcnt)[5] * scale), roundcoords)).append(" ")
                            .append(roundtodec((float) (segments.get(pcnt)[6] * scale), roundcoords)).append(" ");
                }
            }
        }// End of roundcoords check

        sb.append("Z\" />");

        // Rendering control points
        for (int pcnt = 0; pcnt < segments.size(); pcnt++) {
            if ((lcpr > 0) && (segments.get(pcnt)[0] == 1.0)) {
                sb.append("<circle cx=\"").append(segments.get(pcnt)[3] * scale).append("\" cy=\"").append(segments.get(pcnt)[4] * scale).append("\" r=\"").append(lcpr).append("\" fill=\"white\" stroke-width=\"").append(lcpr * 0.2).append("\" stroke=\"black\" />");
            }
            if ((qcpr > 0) && (segments.get(pcnt)[0] == 2.0)) {
                sb.append("<circle cx=\"").append(segments.get(pcnt)[3] * scale).append("\" cy=\"").append(segments.get(pcnt)[4] * scale).append("\" r=\"").append(qcpr).append("\" fill=\"cyan\" stroke-width=\"").append(qcpr * 0.2).append("\" stroke=\"black\" />");
                sb.append("<circle cx=\"").append(segments.get(pcnt)[5] * scale).append("\" cy=\"").append(segments.get(pcnt)[6] * scale).append("\" r=\"").append(qcpr).append("\" fill=\"white\" stroke-width=\"").append(qcpr * 0.2).append("\" stroke=\"black\" />");
                sb.append("<line x1=\"").append(segments.get(pcnt)[1] * scale).append("\" y1=\"").append(segments.get(pcnt)[2] * scale).append("\" x2=\"").append(segments.get(pcnt)[3] * scale).append("\" y2=\"").append(segments.get(pcnt)[4] * scale).append("\" stroke-width=\"").append(qcpr * 0.2).append("\" stroke=\"cyan\" />");
                sb.append("<line x1=\"").append(segments.get(pcnt)[3] * scale).append("\" y1=\"").append(segments.get(pcnt)[4] * scale).append("\" x2=\"").append(segments.get(pcnt)[5] * scale).append("\" y2=\"").append(segments.get(pcnt)[6] * scale).append("\" stroke-width=\"").append(qcpr * 0.2).append("\" stroke=\"cyan\" />");
            }// End of quadratic control points
        }

    }// End of svgpathstring()

    // Converting tracedata to an SVG string, paths are drawn according to a Z-index
    // the optional lcpr and qcpr are linear and quadratic control point radiuses
    public static String getsvgstring(IndexedImage ii, HashMap<String, Float> options) {
        options = checkoptions(options);
        // SVG start
        int w = (int) (ii.width * options.get("scale")), h = (int) (ii.height * options.get("scale"));
        String viewboxorviewport = options.get("viewbox") != 0 ? "viewBox=\"0 0 " + w + " " + h + "\" " : "width=\"" + w + "\" height=\"" + h + "\" ";
        StringBuilder svgstr = new StringBuilder("<svg " + viewboxorviewport + "version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" ");
        if (options.get("desc") != 0) {
            svgstr.append("desc=\"Created with ImageTracerAndroid.java version " + ImageTracerAndroid.versionnumber + "\" ");
        }
        svgstr.append(">");

        // creating Z-index
        TreeMap<Double, Integer[]> zindex = new TreeMap<Double, Integer[]>();
        double label;
        // Layer loop
        for (int k = 0; k < ii.layers.size(); k++) {

            // Path loop
            for (int pcnt = 0; pcnt < ii.layers.get(k).size(); pcnt++) {

                // Label (Z-index key) is the startpoint of the path, linearized
                label = (ii.layers.get(k).get(pcnt).get(0)[2] * w) + ii.layers.get(k).get(pcnt).get(0)[1];
                // Creating new list if required
                if (!zindex.containsKey(label)) {
                    zindex.put(label, new Integer[2]);
                }
                // Adding layer and path number to list
                zindex.get(label)[0] = Integer.valueOf(k);
                zindex.get(label)[1] = Integer.valueOf(pcnt);
            }// End of path loop

        }// End of layer loop

        // Sorting Z-index is not required, TreeMap is sorted automatically

        // Drawing
        // Z-index loop
        String thisdesc = "";
        for (Entry<Double, Integer[]> entry : zindex.entrySet()) {
            if (options.get("desc") != 0) {
                thisdesc = "desc=\"l " + entry.getValue()[0] + " p " + entry.getValue()[1] + "\" ";
            } else {
                thisdesc = "";
            }
            svgpathstring(svgstr,
                    thisdesc,
                    ii.layers.get(entry.getValue()[0]).get(entry.getValue()[1]),
                    tosvgcolorstr(ii.palette[entry.getValue()[0]]),
                    options);
        }

        // SVG End
        svgstr.append("</svg>");

        return svgstr.toString();

    }// End of getsvgstring()


    ////////////////////////////////////////////////////////////
    //
    //  SVG Drawing functions
    //
    ////////////////////////////////////////////////////////////

    static String tosvgcolorstr(byte[] c) {
        return "fill=\"rgb(" + (c[0] + 128) + "," + (c[1] + 128) + "," + (c[2] + 128) + ")\" stroke=\"rgb(" + (c[0] + 128) + "," + (c[1] + 128) + "," + (c[2] + 128) + ")\" stroke-width=\"1\" opacity=\"" + ((c[3] + 128) / 255.0) + "\" ";
    }

    // Selective Gaussian blur for preprocessing
    static ImageData blur(ImageData imgd, float rad, float del) {
        int i, j, k, d, idx;
        double racc, gacc, bacc, aacc, wacc;
        ImageData imgd2 = new ImageData(imgd.width, imgd.height, new byte[imgd.width * imgd.height * 4]);

        // radius and delta limits, this kernel
        int radius = (int) Math.floor(rad);
        if (radius < 1) {
            return imgd;
        }
        if (radius > 5) {
            radius = 5;
        }
        int delta = (int) Math.abs(del);
        if (delta > 1024) {
            delta = 1024;
        }
        double[] thisgk = gks[radius - 1];

        // loop through all pixels, horizontal blur
        for (j = 0; j < imgd.height; j++) {
            for (i = 0; i < imgd.width; i++) {

                racc = 0;
                gacc = 0;
                bacc = 0;
                aacc = 0;
                wacc = 0;
                // gauss kernel loop
                for (k = -radius; k < (radius + 1); k++) {
                    // add weighted color values
                    if (((i + k) > 0) && ((i + k) < imgd.width)) {
                        idx = ((j * imgd.width) + i + k) * 4;
                        racc += imgd.data[idx] * thisgk[k + radius];
                        gacc += imgd.data[idx + 1] * thisgk[k + radius];
                        bacc += imgd.data[idx + 2] * thisgk[k + radius];
                        aacc += imgd.data[idx + 3] * thisgk[k + radius];
                        wacc += thisgk[k + radius];
                    }
                }
                // The new pixel
                idx = ((j * imgd.width) + i) * 4;
                imgd2.data[idx] = (byte) Math.floor(racc / wacc);
                imgd2.data[idx + 1] = (byte) Math.floor(gacc / wacc);
                imgd2.data[idx + 2] = (byte) Math.floor(bacc / wacc);
                imgd2.data[idx + 3] = (byte) Math.floor(aacc / wacc);

            }// End of width loop
        }// End of horizontal blur

        // copying the half blurred imgd2
        byte[] himgd = imgd2.data.clone();

        // loop through all pixels, vertical blur
        for (j = 0; j < imgd.height; j++) {
            for (i = 0; i < imgd.width; i++) {

                racc = 0;
                gacc = 0;
                bacc = 0;
                aacc = 0;
                wacc = 0;
                // gauss kernel loop
                for (k = -radius; k < (radius + 1); k++) {
                    // add weighted color values
                    if (((j + k) > 0) && ((j + k) < imgd.height)) {
                        idx = (((j + k) * imgd.width) + i) * 4;
                        racc += himgd[idx] * thisgk[k + radius];
                        gacc += himgd[idx + 1] * thisgk[k + radius];
                        bacc += himgd[idx + 2] * thisgk[k + radius];
                        aacc += himgd[idx + 3] * thisgk[k + radius];
                        wacc += thisgk[k + radius];
                    }
                }
                // The new pixel
                idx = ((j * imgd.width) + i) * 4;
                imgd2.data[idx] = (byte) Math.floor(racc / wacc);
                imgd2.data[idx + 1] = (byte) Math.floor(gacc / wacc);
                imgd2.data[idx + 2] = (byte) Math.floor(bacc / wacc);
                imgd2.data[idx + 3] = (byte) Math.floor(aacc / wacc);

            }// End of width loop
        }// End of vertical blur

        // Selective blur: loop through all pixels
        for (j = 0; j < imgd.height; j++) {
            for (i = 0; i < imgd.width; i++) {

                idx = ((j * imgd.width) + i) * 4;
                // d is the difference between the blurred and the original pixel
                d = Math.abs(imgd2.data[idx] - imgd.data[idx]) + Math.abs(imgd2.data[idx + 1] - imgd.data[idx + 1]) +
                        Math.abs(imgd2.data[idx + 2] - imgd.data[idx + 2]) + Math.abs(imgd2.data[idx + 3] - imgd.data[idx + 3]);
                // selective blur: if d>delta, put the original pixel back
                if (d > delta) {
                    imgd2.data[idx] = imgd.data[idx];
                    imgd2.data[idx + 1] = imgd.data[idx + 1];
                    imgd2.data[idx + 2] = imgd.data[idx + 2];
                    imgd2.data[idx + 3] = imgd.data[idx + 3];
                }
            }
        }// End of Selective blur

        return imgd2;

    }// End of blur()

    // Loading an image from a file, tracing when loaded, then returning IndexedImage with tracedata in layers
    public IndexedImage imageToTracedata(String filename, HashMap<String, Float> options, byte[][] palette) throws Exception {
        options = checkoptions(options);
        ImageData imgd = loadImageData(filename);
        return imagedataToTracedata(imgd, options, palette);
    }// End of imageToTracedata()

    public IndexedImage imageToTracedata(Bitmap bitmap, HashMap<String, Float> options, byte[][] palette) throws Exception {
        options = checkoptions(options);
        ImageData imgd = loadImageData(bitmap);
        return imagedataToTracedata(imgd, options, palette);
    }// End of imageToTracedata()

    // Container for the color-indexed image before and tracedata after vectorizing
    public static class IndexedImage {
        public int width, height;
        public int[][] array; // array[x][y] of palette colors
        public byte[][] palette;// array[palettelength][4] RGBA color palette
        public ArrayList<ArrayList<ArrayList<Double[]>>> layers;// tracedata

        public IndexedImage(int[][] marray, byte[][] mpalette) {
            array = marray;
            palette = mpalette;
            width = marray[0].length - 2;
            height = marray.length - 2;// Color quantization adds +2 to the original width and height
        }
    }

    // https://developer.mozilla.org/en-US/docs/Web/API/ImageData
    public static class ImageData {
        public int width, height;
        public byte[] data; // raw byte data: R G B A R G B A ...

        public ImageData(int mwidth, int mheight, byte[] mdata) {
            width = mwidth;
            height = mheight;
            data = mdata;
        }
    }


}// End of ImageTracerAndroid class