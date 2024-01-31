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

    /**
     * @noinspection unused
     */
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

                if (isGrayScale) {
                    int gray = red;
                    gray = gray + (gray * matrix[x % 2][y % 2]) / 5;

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
                        int channelValue = rgb[i] + (rgb[i] * matrix[x % 2][y % 2]) / 5;

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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];

        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }
                    redErrors[x + 1][y] += (7 * redError) / 16;
                    redErrors[x - 1][y + 1] += (3 * redError) / 16;
                    redErrors[x][y + 1] += (5 * redError) / 16;
                    redErrors[x + 1][y + 1] += (redError) / 16;

                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }
                    greenErrors[x + 1][y] += (7 * greenError) / 16;
                    greenErrors[x - 1][y + 1] += (3 * greenError) / 16;
                    greenErrors[x][y + 1] += (5 * greenError) / 16;
                    greenErrors[x + 1][y + 1] += (greenError) / 16;

                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }
                    blueErrors[x + 1][y] += (7 * blueError) / 16;
                    blueErrors[x - 1][y + 1] += (3 * blueError) / 16;
                    blueErrors[x][y + 1] += (5 * blueError) / 16;
                    blueErrors[x + 1][y + 1] += (blueError) / 16;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += (7 * redError) / 48;
                    redErrors[x + 2][y] += (5 * redError) / 48;

                    redErrors[x - 2][y + 1] += (3 * redError) / 48;
                    redErrors[x - 1][y + 1] += (5 * redError) / 48;
                    redErrors[x][y + 1] += (7 * redError) / 48;
                    redErrors[x + 1][y + 1] += (5 * redError) / 48;
                    redErrors[x + 2][y + 1] += (3 * redError) / 48;

                    redErrors[x - 2][y + 2] += (redError) / 48;
                    redErrors[x - 1][y + 2] += (3 * redError) / 48;
                    redErrors[x][y + 2] += (5 * redError) / 48;
                    redErrors[x + 1][y + 2] += (3 * redError) / 48;
                    redErrors[x + 2][y + 2] += (redError) / 48;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += (7 * greenError) / 48;
                    greenErrors[x + 2][y] += (5 * greenError) / 48;

                    greenErrors[x - 2][y + 1] += (3 * greenError) / 48;
                    greenErrors[x - 1][y + 1] += (5 * greenError) / 48;
                    greenErrors[x][y + 1] += (7 * greenError) / 48;
                    greenErrors[x + 1][y + 1] += (5 * greenError) / 48;
                    greenErrors[x + 2][y + 1] += (3 * greenError) / 48;

                    greenErrors[x - 2][y + 2] += (greenError) / 48;
                    greenErrors[x - 1][y + 2] += (3 * greenError) / 48;
                    greenErrors[x][y + 2] += (5 * greenError) / 48;
                    greenErrors[x + 1][y + 2] += (3 * greenError) / 48;
                    greenErrors[x + 2][y + 2] += (greenError) / 48;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += (7 * blueError) / 48;
                    blueErrors[x + 2][y] += (5 * blueError) / 48;

                    blueErrors[x - 2][y + 1] += (3 * blueError) / 48;
                    blueErrors[x - 1][y + 1] += (5 * blueError) / 48;
                    blueErrors[x][y + 1] += (7 * blueError) / 48;
                    blueErrors[x + 1][y + 1] += (5 * blueError) / 48;
                    blueErrors[x + 2][y + 1] += (3 * blueError) / 48;

                    blueErrors[x - 2][y + 2] += (blueError) / 48;
                    blueErrors[x - 1][y + 2] += (3 * blueError) / 48;
                    blueErrors[x][y + 2] += (5 * blueError) / 48;
                    blueErrors[x + 1][y + 2] += (3 * blueError) / 48;
                    blueErrors[x + 2][y + 2] += (blueError) / 48;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += (5 * redError) / 32;
                    redErrors[x + 2][y] += (3 * redError) / 32;

                    redErrors[x - 2][y + 1] += (2 * redError) / 32;
                    redErrors[x - 1][y + 1] += (4 * redError) / 32;
                    redErrors[x][y + 1] += (5 * redError) / 32;
                    redErrors[x + 1][y + 1] += (4 * redError) / 32;
                    redErrors[x + 2][y + 1] += (2 * redError) / 32;

                    redErrors[x - 1][y + 2] += (2 * redError) / 32;
                    redErrors[x][y + 2] += (3 * redError) / 32;
                    redErrors[x + 1][y + 2] += (2 * redError) / 32;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += (5 * blueError) / 32;
                    blueErrors[x + 2][y] += (3 * blueError) / 32;

                    blueErrors[x - 2][y + 1] += (2 * blueError) / 32;
                    blueErrors[x - 1][y + 1] += (4 * blueError) / 32;
                    blueErrors[x][y + 1] += (5 * blueError) / 32;
                    blueErrors[x + 1][y + 1] += (4 * blueError) / 32;
                    blueErrors[x + 2][y + 1] += (2 * blueError) / 32;

                    blueErrors[x - 1][y + 2] += (2 * blueError) / 32;
                    blueErrors[x][y + 2] += (3 * blueError) / 32;
                    blueErrors[x + 1][y + 2] += (2 * blueError) / 32;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += (5 * greenError) / 32;
                    greenErrors[x + 2][y] += (3 * greenError) / 32;

                    greenErrors[x - 2][y + 1] += (2 * greenError) / 32;
                    greenErrors[x - 1][y + 1] += (4 * greenError) / 32;
                    greenErrors[x][y + 1] += (5 * greenError) / 32;
                    greenErrors[x + 1][y + 1] += (4 * greenError) / 32;
                    greenErrors[x + 2][y + 1] += (2 * greenError) / 32;

                    greenErrors[x - 1][y + 2] += (2 * greenError) / 32;
                    greenErrors[x][y + 2] += (3 * greenError) / 32;
                    greenErrors[x + 1][y + 2] += (2 * greenError) / 32;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += (4 * redError) / 16;
                    redErrors[x + 2][y] += (3 * redError) / 16;

                    redErrors[x - 2][y + 1] += (redError) / 16;
                    redErrors[x - 1][y + 1] += (2 * redError) / 16;
                    redErrors[x][y + 1] += (3 * redError) / 16;
                    redErrors[x + 1][y + 1] += (2 * redError) / 16;
                    redErrors[x + 2][y + 1] += (redError) / 16;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += (4 * greenError) / 16;
                    greenErrors[x + 2][y] += (3 * greenError) / 16;

                    greenErrors[x - 2][y + 1] += (greenError) / 16;
                    greenErrors[x - 1][y + 1] += (2 * greenError) / 16;
                    greenErrors[x][y + 1] += (3 * greenError) / 16;
                    greenErrors[x + 1][y + 1] += (2 * greenError) / 16;
                    greenErrors[x + 2][y + 1] += (greenError) / 16;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += (4 * blueError) / 16;
                    blueErrors[x + 2][y] += (3 * blueError) / 16;

                    blueErrors[x - 2][y + 1] += (blueError) / 16;
                    blueErrors[x - 1][y + 1] += (2 * blueError) / 16;
                    blueErrors[x][y + 1] += (3 * blueError) / 16;
                    blueErrors[x + 1][y + 1] += (2 * blueError) / 16;
                    blueErrors[x + 2][y + 1] += (blueError) / 16;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += (2 * redError) / 4;

                    redErrors[x - 1][y + 1] += (redError) / 4;
                    redErrors[x][y + 1] += (redError) / 4;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += (2 * greenError) / 4;

                    greenErrors[x - 1][y + 1] += (greenError) / 4;
                    greenErrors[x][y + 1] += (greenError) / 4;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += (2 * blueError) / 4;

                    blueErrors[x - 1][y + 1] += (blueError) / 4;
                    blueErrors[x][y + 1] += (blueError) / 4;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 1; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += redError / 8;
                    redErrors[x + 2][y] += redError / 8;

                    redErrors[x - 1][y + 1] += redError / 8;
                    redErrors[x][y + 1] += redError / 8;
                    redErrors[x + 1][y + 1] += redError / 8;

                    redErrors[x][y + 2] += redError / 8;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += greenError / 8;
                    greenErrors[x + 2][y] += greenError / 8;

                    greenErrors[x - 1][y + 1] += greenError / 8;
                    greenErrors[x][y + 1] += greenError / 8;
                    greenErrors[x + 1][y + 1] += greenError / 8;

                    greenErrors[x][y + 2] += greenError / 8;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += blueError / 8;
                    blueErrors[x + 2][y] += blueError / 8;

                    blueErrors[x - 1][y + 1] += blueError / 8;
                    blueErrors[x][y + 1] += blueError / 8;
                    blueErrors[x + 1][y + 1] += blueError / 8;

                    blueErrors[x][y + 2] += blueError / 8;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += (8 * redError) / 42;
                    redErrors[x + 2][y] += (4 * redError) / 42;

                    redErrors[x - 2][y + 1] += (2 * redError) / 42;
                    redErrors[x - 1][y + 1] += (4 * redError) / 42;
                    redErrors[x][y + 1] += (8 * redError) / 42;
                    redErrors[x + 1][y + 1] += (4 * redError) / 42;
                    redErrors[x + 2][y + 1] += (2 * redError) / 42;

                    redErrors[x - 2][y + 2] += (redError) / 42;
                    redErrors[x - 1][y + 2] += (2 * redError) / 42;
                    redErrors[x][y + 2] += (4 * redError) / 42;
                    redErrors[x + 1][y + 2] += (2 * redError) / 42;
                    redErrors[x + 2][y + 2] += (redError) / 42;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += (8 * greenError) / 42;
                    greenErrors[x + 2][y] += (4 * greenError) / 42;

                    greenErrors[x - 2][y + 1] += (2 * greenError) / 42;
                    greenErrors[x - 1][y + 1] += (4 * greenError) / 42;
                    greenErrors[x][y + 1] += (8 * greenError) / 42;
                    greenErrors[x + 1][y + 1] += (4 * greenError) / 42;
                    greenErrors[x + 2][y + 1] += (2 * greenError) / 42;

                    greenErrors[x - 2][y + 2] += (greenError) / 42;
                    greenErrors[x - 1][y + 2] += (2 * greenError) / 42;
                    greenErrors[x][y + 2] += (4 * greenError) / 42;
                    greenErrors[x + 1][y + 2] += (2 * greenError) / 42;
                    greenErrors[x + 2][y + 2] += (greenError) / 42;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += (8 * blueError) / 42;
                    blueErrors[x + 2][y] += (4 * blueError) / 42;

                    blueErrors[x - 2][y + 1] += (2 * blueError) / 42;
                    blueErrors[x - 1][y + 1] += (4 * blueError) / 42;
                    blueErrors[x][y + 1] += (8 * blueError) / 42;
                    blueErrors[x + 1][y + 1] += (4 * blueError) / 42;
                    blueErrors[x + 2][y + 1] += (2 * blueError) / 42;

                    blueErrors[x - 2][y + 2] += (blueError) / 42;
                    blueErrors[x - 1][y + 2] += (2 * blueError) / 42;
                    blueErrors[x][y + 2] += (4 * blueError) / 42;
                    blueErrors[x + 1][y + 2] += (2 * blueError) / 42;
                    blueErrors[x + 2][y + 2] += (blueError) / 42;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 2; x < width - 2; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }

                    redErrors[x + 1][y] += (8 * redError) / 32;
                    redErrors[x + 2][y] += (4 * redError) / 32;

                    redErrors[x - 2][y + 1] += (2 * redError) / 32;
                    redErrors[x - 1][y + 1] += (4 * redError) / 32;
                    redErrors[x][y + 1] += (8 * redError) / 32;
                    redErrors[x + 1][y + 1] += (4 * redError) / 32;
                    redErrors[x + 2][y + 1] += (2 * redError) / 32;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }

                    greenErrors[x + 1][y] += (8 * greenError) / 32;
                    greenErrors[x + 2][y] += (4 * greenError) / 32;

                    greenErrors[x - 2][y + 1] += (2 * greenError) / 32;
                    greenErrors[x - 1][y + 1] += (4 * greenError) / 32;
                    greenErrors[x][y + 1] += (8 * greenError) / 32;
                    greenErrors[x + 1][y + 1] += (4 * greenError) / 32;
                    greenErrors[x + 2][y + 1] += (2 * greenError) / 32;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }

                    blueErrors[x + 1][y] += (8 * blueError) / 32;
                    blueErrors[x + 2][y] += (4 * blueError) / 32;

                    blueErrors[x - 2][y + 1] += (2 * blueError) / 32;
                    blueErrors[x - 1][y + 1] += (4 * blueError) / 32;
                    blueErrors[x][y + 1] += (8 * blueError) / 32;
                    blueErrors[x + 1][y + 1] += (4 * blueError) / 32;
                    blueErrors[x + 2][y + 1] += (2 * blueError) / 32;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
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

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        int error;
        int[][] errors = new int[width][height];

        int redError, greenError, blueError;
        int[][] redErrors = new int[width][height];
        int[][] greenErrors = new int[width][height];
        int[][] blueErrors = new int[width][height];
        for (int y = 0; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    if (red + redErrors[x][y] < threshold) {
                        redError = red + redErrors[x][y];
                        red = 0;
                    } else {
                        redError = red + redErrors[x][y] - 255;
                        red = 255;
                    }
                    redErrors[x + 1][y] += (3 * redError) / 8;
                    redErrors[x][y + 1] += (3 * redError) / 8;
                    redErrors[x + 1][y + 1] += (2 * redError) / 8;


                    if (green + greenErrors[x][y] < threshold) {
                        greenError = green + greenErrors[x][y];
                        green = 0;
                    } else {
                        greenError = green + greenErrors[x][y] - 255;
                        green = 255;
                    }
                    greenErrors[x + 1][y] += (3 * greenError) / 8;
                    greenErrors[x][y + 1] += (3 * greenError) / 8;
                    greenErrors[x + 1][y + 1] += (2 * greenError) / 8;


                    if (blue + blueErrors[x][y] < threshold) {
                        blueError = blue + blueErrors[x][y];
                        blue = 0;
                    } else {
                        blueError = blue + blueErrors[x][y] - 255;
                        blue = 255;
                    }
                    blueErrors[x + 1][y] += (3 * blueError) / 8;
                    blueErrors[x][y + 1] += (3 * blueError) / 8;
                    blueErrors[x + 1][y + 1] += (2 * blueError) / 8;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
            }
        }

        return out;
    }

    private Bitmap simpleLeftToRightErrorDiffusion(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int gray;

        int width = src.getWidth();
        int height = src.getHeight();
        for (int y = 0; y < height; y++) {
            int error = 0;
            int redError = 0;
            int greenError = 0;
            int blueError = 0;

            for (int x = 0; x < width; x++) {

                pixel = src.getPixel(x, y);

                alpha = Color.alpha(pixel);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                if (isGrayScale) {
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
                } else {
                    int redDelta;

                    if (red + redError < threshold) {
                        redDelta = red;
                        red = 0;
                    } else {
                        redDelta = red - 255;
                        red = 255;
                    }

                    if (Math.abs(redDelta) < 10)
                        redDelta = 0;

                    redError += redDelta;


                    int greenDelta;

                    if (green + greenError < threshold) {
                        greenDelta = green;
                        green = 0;
                    } else {
                        greenDelta = green - 255;
                        green = 255;
                    }

                    if (Math.abs(greenDelta) < 10)
                        greenDelta = 0;

                    greenError += greenDelta;


                    int blueDelta;

                    if (blue + blueError < threshold) {
                        blueDelta = blue;
                        blue = 0;
                    } else {
                        blueDelta = blue - 255;
                        blue = 255;
                    }

                    if (Math.abs(blueDelta) < 10)
                        blueDelta = 0;

                    blueError += blueDelta;

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
            }
        }

        return out;
    }

    private Bitmap randomDithering(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int gray;

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
                    gray = red;

                    int threshold = (int) (Math.random() * 1000) % 256;

                    if (gray < threshold) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
                } else {
                    int threshold = (int) (Math.random() * 1000) % 256;

                    if (red < threshold) {
                        red = 0;
                    } else {
                        red = 255;
                    }

                    if (green < threshold) {
                        green = 0;
                    } else {
                        green = 255;
                    }

                    if (blue < threshold) {
                        blue = 0;
                    } else {
                        blue = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
            }
        }

        return out;
    }

    private Bitmap simpleThreshold(Bitmap src) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int alpha, red, green, blue;
        int pixel;
        int gray;

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
                    gray = red;

                    if (gray < threshold) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
                } else {
                    if (red < threshold) {
                        red = 0;
                    } else {
                        red = 255;
                    }

                    if (green < threshold) {
                        green = 0;
                    } else {
                        green = 255;
                    }

                    if (blue < threshold) {
                        blue = 0;
                    } else {
                        blue = 255;
                    }

                    out.setPixel(x, y, Color.argb(alpha, red, green, blue));
                }
            }
        }

        return out;
    }
}