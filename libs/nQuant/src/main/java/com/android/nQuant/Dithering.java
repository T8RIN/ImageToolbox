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

package com.android.nQuant;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Dithering {

    public enum Type {
        BayerTwo,
        BayerThree,
        BayerFour,
        BayerEight,
        FloydSteinberg,
        JarvisJudiceNinke,
        Sierra,
        TwoRowSierra,
        SierraLite,
        Atkinson,
        Stucki,
        Burkes,
        FalseFloydSteinberg,
        LeftToRight,
        Random,
        SimpleThreshold
    }


    int threshold = 128;
    boolean isGrayScale = false;

    public Dithering() {
    }

    public Dithering(int threshold, boolean isGrayScale) {
        this.isGrayScale = isGrayScale;
        this.threshold = threshold;
    }

    public Bitmap dither(Type type, Bitmap src) {
        return switch (type) {
            case BayerTwo -> ordered2By2Bayer(src);
            case BayerThree -> ordered3By3Bayer(src);
            case BayerFour -> ordered4By4Bayer(src);
            case BayerEight -> ordered8By8Bayer(src);
            case FloydSteinberg -> floydSteinberg(src);
            case JarvisJudiceNinke -> jarvisJudiceNinke(src);
            case Sierra -> sierra(src);
            case TwoRowSierra -> twoRowSierra(src);
            case SierraLite -> sierraLite(src);
            case Atkinson -> atkinson(src);
            case Stucki -> stucki(src);
            case Burkes -> burkes(src);
            case FalseFloydSteinberg -> falseFloydSteinberg(src);
            case LeftToRight -> simpleLeftToRightErrorDiffusion(src);
            case Random -> randomDithering(src);
            case SimpleThreshold -> simpleThreshold(src);
        };
    }

    /*
     * 2 by 2 Bayer Ordered Dithering
     *
     * 1 3
     * 4 2
     *
     * (1/5)
     *
     */
    private Bitmap ordered2By2Bayer(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int[] rgb = new int[3];

        int[][] matrix = {
                {1, 3},
                {4, 2},
        };

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (!isGrayScale) {
                    rgb[0] = red;
                    rgb[1] = green;
                    rgb[2] = blue;

                    for (int i = 0; i < 3; i++) {
                        int channelValue = rgb[i] + (rgb[i] * matrix[x % 2][y % 2]) / 5;

                        if (channelValue < threshold) {
                            rgb[i] = 0;
                        } else {
                            rgb[i] = 255;
                        }
                    }

                    out.setPixel(x, y, Color.argb(alpha, rgb[0], rgb[1], rgb[2]));
                } else {
                    int gray = red;
                    gray = gray + (gray * matrix[x % 2][y % 2]) / 5;

                    if (gray < threshold) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
                }
            }
        }

        return out;
    }

    /*
     * 3 by 3 Bayer Ordered Dithering
     *
     *  3 7 4
     *  6 1 9
     *  2 8 5
     *
     *  (1/10)
     */
    public Bitmap ordered3By3Bayer(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int[] rgb = new int[3];

        int[][] matrix = {
                {3, 7, 4},
                {6, 1, 9},
                {2, 8, 5},
        };

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
                    int gray = red;
                    gray = gray + (gray * matrix[x % 3][y % 3]) / 10;

                    if (gray < threshold) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
                } else {
                    rgb[0] = red;
                    rgb[1] = green;
                    rgb[2] = blue;

                    for (int i = 0; i < 3; i++) {
                        int channelValue = rgb[i] + (rgb[i] * matrix[x % 3][y % 3]) / 10;

                        if (channelValue < threshold) {
                            rgb[i] = 0;
                        } else {
                            rgb[i] = 255;
                        }
                    }

                    out.setPixel(x, y, Color.argb(alpha, rgb[0], rgb[1], rgb[2]));
                }
            }
        }

        return out;
    }

    /*
     * 4 by 4 Bayer Ordered Dithering
     *
     *  1 9 3 11
     *  13 5 15 7
     *  4 12 2 10
     *  16 8 14 6
     *  (1/17)
     */
    public Bitmap ordered4By4Bayer(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int[] rgb = new int[3];

        int[][] matrix = {
                {1, 9, 3, 11},
                {13, 5, 15, 7},
                {4, 12, 2, 10},
                {16, 8, 14, 6}};

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
                    int gray = red;
                    gray = gray + (gray * matrix[x % 4][y % 4]) / 17;

                    if (gray < threshold) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
                } else {
                    rgb[0] = red;
                    rgb[1] = green;
                    rgb[2] = blue;

                    for (int i = 0; i < 3; i++) {
                        int channelValue = rgb[i] + (rgb[i] * matrix[x % 4][y % 4]) / 17;

                        if (channelValue < threshold) {
                            rgb[i] = 0;
                        } else {
                            rgb[i] = 255;
                        }
                    }

                    out.setPixel(x, y, Color.argb(alpha, rgb[0], rgb[1], rgb[2]));
                }
            }
        }

        return out;
    }

    /*
     * 8 by 8 Bayer Ordered Dithering
     *
     *  1 49 13 61 4 52 16 64
     *  33 17 45 29 36 20 48 32
     *  9 57 5 53 12 60 8 56
     *  41 25 37 21 44 28 40 24
     *  3 51 15 63 2 50 14 62
     *  35 19 47 31 34 18 46 30
     *  11 59 7 55 10 58 6 54
     *  43 27 39 23 42 26 38 22
     *
     *  (1/65)
     */
    public Bitmap ordered8By8Bayer(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int[] rgb = new int[3];

        int[][] matrix = {
                {1, 49, 13, 61, 4, 52, 16, 64},
                {33, 17, 45, 29, 36, 20, 48, 32},
                {9, 57, 5, 53, 12, 60, 8, 56},
                {41, 25, 37, 21, 44, 28, 40, 24},
                {3, 51, 15, 63, 2, 50, 14, 62},
                {35, 19, 47, 31, 34, 18, 46, 30},
                {11, 59, 7, 55, 10, 58, 6, 54},
                {43, 27, 39, 23, 42, 26, 38, 22}
        };

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
                    int gray = red;
                    gray = gray + (gray * matrix[x % 8][y % 8]) / 65;

                    if (gray < threshold) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
                } else {
                    rgb[0] = red;
                    rgb[1] = green;
                    rgb[2] = blue;

                    for (int i = 0; i < 3; i++) {
                        int channelValue = rgb[i] + (rgb[i] * matrix[x % 8][y % 8]) / 65;

                        if (channelValue < threshold) {
                            rgb[i] = 0;
                        } else {
                            rgb[i] = 255;
                        }
                    }

                    out.setPixel(x, y, Color.argb(alpha, rgb[0], rgb[1], rgb[2]));
                }
            }
        }

        return out;
    }


    /*
     * Floyd-Steinberg Dithering
     *
     *   X 7
     * 3 5 1
     *
     * (1/16)
     */
    private Bitmap floydSteinberg(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }
                errors[x + 1][y] += (7 * error) / 16;
                errors[x - 1][y + 1] += (3 * error) / 16;
                errors[x][y + 1] += (5 * error) / 16;
                errors[x + 1][y + 1] += (error) / 16;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Jarvis, Judice , Ninke Dithering
     *
     *     X 7 5
     * 3 5 7 5 3
     * 1 3 5 3 1
     *
     * (1/48)
     */
    private Bitmap jarvisJudiceNinke(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += (7 * error) / 48;
                errors[x + 2][y] += (5 * error) / 48;

                errors[x - 2][y + 1] += (3 * error) / 48;
                errors[x - 1][y + 1] += (5 * error) / 48;
                errors[x][y + 1] += (7 * error) / 48;
                errors[x + 1][y + 1] += (5 * error) / 48;
                errors[x + 2][y + 1] += (3 * error) / 48;

                errors[x - 2][y + 2] += (error) / 48;
                errors[x - 1][y + 2] += (3 * error) / 48;
                errors[x][y + 2] += (5 * error) / 48;
                errors[x + 1][y + 2] += (3 * error) / 48;
                errors[x + 2][y + 2] += (error) / 48;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Sierra Dithering
     *
     *     X 5 3
     * 2 4 5 4 2
     *   2 3 2
     *
     * (1/32)
     */
    private Bitmap sierra(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += (5 * error) / 32;
                errors[x + 2][y] += (3 * error) / 32;

                errors[x - 2][y + 1] += (2 * error) / 32;
                errors[x - 1][y + 1] += (4 * error) / 32;
                errors[x][y + 1] += (5 * error) / 32;
                errors[x + 1][y + 1] += (4 * error) / 32;
                errors[x + 2][y + 1] += (2 * error) / 32;

                errors[x - 1][y + 2] += (2 * error) / 32;
                errors[x][y + 2] += (3 * error) / 32;
                errors[x + 1][y + 2] += (2 * error) / 32;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Two-Row Sierra Dithering
     *
     *     X 4 3
     * 1 2 3 2 1
     *
     * (1/16)
     */
    private Bitmap twoRowSierra(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += (4 * error) / 16;
                errors[x + 2][y] += (3 * error) / 16;

                errors[x - 2][y + 1] += (error) / 16;
                errors[x - 1][y + 1] += (2 * error) / 16;
                errors[x][y + 1] += (3 * error) / 16;
                errors[x + 1][y + 1] += (2 * error) / 16;
                errors[x + 2][y + 1] += (error) / 16;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Sierra Lite Dithering
     *
     *   X 2
     * 1 1
     *
     * (1/4)
     */
    private Bitmap sierraLite(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += (2 * error) / 4;

                errors[x - 1][y + 1] += (error) / 4;
                errors[x][y + 1] += (error) / 4;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Atkinson Dithering
     *
     *   X 1 1
     * 1 1 1
     *   1
     *
     * (1/8)
     */
    private Bitmap atkinson(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 1; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += error / 8;
                errors[x + 2][y] += error / 8;

                errors[x - 1][y + 1] += error / 8;
                errors[x][y + 1] += error / 8;
                errors[x + 1][y + 1] += error / 8;

                errors[x][y + 2] += error / 8;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Stucki Dithering
     *
     *     X 8 4
     * 2 4 8 4 2
     * 1 2 4 2 1
     *
     * (1/42)
     */
    private Bitmap stucki(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += (8 * error) / 42;
                errors[x + 2][y] += (4 * error) / 42;

                errors[x - 2][y + 1] += (2 * error) / 42;
                errors[x - 1][y + 1] += (4 * error) / 42;
                errors[x][y + 1] += (8 * error) / 42;
                errors[x + 1][y + 1] += (4 * error) / 42;
                errors[x + 2][y + 1] += (2 * error) / 42;

                errors[x - 2][y + 2] += (error) / 42;
                errors[x - 1][y + 2] += (2 * error) / 42;
                errors[x][y + 2] += (4 * error) / 42;
                errors[x + 1][y + 2] += (2 * error) / 42;
                errors[x + 2][y + 2] += (error) / 42;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * Burkes Dithering
     *
     *     X 8 4
     * 2 4 8 4 2
     *
     * (1/32)
     */
    private Bitmap burkes(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }

                errors[x + 1][y] += (8 * error) / 32;
                errors[x + 2][y] += (4 * error) / 32;

                errors[x - 2][y + 1] += (2 * error) / 32;
                errors[x - 1][y + 1] += (4 * error) / 32;
                errors[x][y + 1] += (8 * error) / 32;
                errors[x + 1][y + 1] += (4 * error) / 32;
                errors[x + 2][y + 1] += (2 * error) / 32;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    /*
     * False Floyd-Steinberg Dithering
     *
     * X 3
     * 3 2
     *
     * (1/8)
     */
    private Bitmap falseFloydSteinberg(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error = 0;
        int[][] errors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                if (gray + errors[x][y] < threshold) {
                    error = gray + errors[x][y];
                    gray = 0;
                } else {
                    error = gray + errors[x][y] - 255;
                    gray = 255;
                }
                errors[x + 1][y] += (3 * error) / 8;
                errors[x][y + 1] += (3 * error) / 8;
                errors[x + 1][y + 1] += (2 * error) / 8;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    private Bitmap simpleLeftToRightErrorDiffusion(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {
            int error = 0;

            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;
                int delta;

                if (gray + error < threshold) {
                    delta = gray;
                    gray = 0;
                } else {
                    delta = gray - 255;
                    gray = 255;
                }

                if (Math.abs(delta) < 10)
                    delta = 0;

                error += delta;

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    private Bitmap randomDithering(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;

                int threshold = (int) (Math.random() * 1000) % 256;

                if (gray < threshold) {
                    gray = 0;
                } else {
                    gray = 255;
                }

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }

    private Bitmap simpleThreshold(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);

                gray = red;

                if (gray < threshold) {
                    gray = 0;
                } else {
                    gray = 255;
                }

                out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
            }
        }

        return out;
    }
}