package com.android.nQuant;

import android.graphics.Color;

public class BitmapUtilities {
    static final char BYTE_MAX = -Byte.MIN_VALUE + Byte.MAX_VALUE;

    static int getColorIndex(final int c, boolean hasSemiTransparency, boolean hasTransparency) {
        if (hasSemiTransparency)
            return (Color.alpha(c) & 0xF0) << 8 | (Color.red(c) & 0xF0) << 4 | (Color.green(c) & 0xF0) | (Color.blue(c) >> 4);
        if (hasTransparency)
            return (Color.alpha(c) & 0x80) << 8 | (Color.red(c) & 0xF8) << 7 | (Color.green(c) & 0xF8) << 2 | (Color.blue(c) >> 3);
        return (Color.red(c) & 0xF8) << 8 | (Color.green(c) & 0xFC) << 3 | (Color.blue(c) >> 3);
    }

    static double sqr(double value) {
        return value * value;
    }

    static int[] calcDitherPixel(int c, int[] clamp, int[] rowerr, int cursor, boolean noBias) {
        int[] ditherPixel = new int[4];
        if (noBias) {
            ditherPixel[0] = clamp[((rowerr[cursor] + 0x1008) >> 4) + Color.red(c)];
            ditherPixel[1] = clamp[((rowerr[cursor + 1] + 0x1008) >> 4) + Color.green(c)];
            ditherPixel[2] = clamp[((rowerr[cursor + 2] + 0x1008) >> 4) + Color.blue(c)];
            ditherPixel[3] = clamp[((rowerr[cursor + 3] + 0x1008) >> 4) + Color.alpha(c)];
            return ditherPixel;
        }

        ditherPixel[0] = clamp[((rowerr[cursor] + 0x2010) >> 5) + Color.red(c)];
        ditherPixel[1] = clamp[((rowerr[cursor + 1] + 0x1008) >> 4) + Color.green(c)];
        ditherPixel[2] = clamp[((rowerr[cursor + 2] + 0x2010) >> 5) + Color.blue(c)];
        ditherPixel[3] = Color.alpha(c);
        return ditherPixel;
    }

    static int[] quantize_image(final int width, final int height, final int[] pixels, final Integer[] palette, final Ditherable ditherable, final boolean hasSemiTransparency, final boolean dither) {
        int[] qPixels = new int[pixels.length];
        int nMaxColors = palette.length;

        int pixelIndex = 0;
        if (dither) {
            final int DJ = 4;
            final int BLOCK_SIZE = 256;
            final int DITHER_MAX = 20;
            final int err_len = (width + 2) * DJ;
            int[] clamp = new int[DJ * BLOCK_SIZE];
            int[] limtb = new int[2 * BLOCK_SIZE];

            for (short i = 0; i < BLOCK_SIZE; ++i) {
                clamp[i] = 0;
                clamp[i + BLOCK_SIZE] = i;
                clamp[i + BLOCK_SIZE * 2] = BYTE_MAX;
                clamp[i + BLOCK_SIZE * 3] = BYTE_MAX;

                limtb[i] = -DITHER_MAX;
                limtb[i + BLOCK_SIZE] = DITHER_MAX;
            }
            for (short i = -DITHER_MAX; i <= DITHER_MAX; ++i)
                limtb[i + BLOCK_SIZE] = i % 4 == 3 ? 0 : i;

            boolean noBias = hasSemiTransparency || nMaxColors < 64;
            int dir = 1;
            int[] row0 = new int[err_len];
            int[] row1 = new int[err_len];
            int[] lookup = new int[65536];
            for (int i = 0; i < height; ++i) {
                if (dir < 0)
                    pixelIndex += width - 1;

                int cursor0 = DJ, cursor1 = width * DJ;
                row1[cursor1] = row1[cursor1 + 1] = row1[cursor1 + 2] = row1[cursor1 + 3] = 0;
                for (int j = 0; j < width; ++j) {
                    int c = pixels[pixelIndex];
                    int[] ditherPixel = calcDitherPixel(c, clamp, row0, cursor0, noBias);
                    int r_pix = ditherPixel[0];
                    int g_pix = ditherPixel[1];
                    int b_pix = ditherPixel[2];
                    int a_pix = ditherPixel[3];

                    int c1 = Color.argb(a_pix, r_pix, g_pix, b_pix);
                    if (noBias && a_pix > 0xF0) {
                        int offset = ditherable.getColorIndex(c1);
                        if (lookup[offset] == 0)
                            lookup[offset] = (Color.alpha(c) == 0) ? 1 : ditherable.nearestColorIndex(palette, c1, i + j) + 1;
                        qPixels[pixelIndex] = palette[lookup[offset] - 1];
                    } else {
                        short qIndex = (Color.alpha(c) == 0) ? 0 : ditherable.nearestColorIndex(palette, c1, i + j);
                        qPixels[pixelIndex] = palette[qIndex];
                    }

                    int c2 = qPixels[pixelIndex];
                    r_pix = limtb[r_pix - Color.red(c2) + BLOCK_SIZE];
                    g_pix = limtb[g_pix - Color.green(c2) + BLOCK_SIZE];
                    b_pix = limtb[b_pix - Color.blue(c2) + BLOCK_SIZE];
                    a_pix = limtb[a_pix - Color.alpha(c2) + BLOCK_SIZE];

                    int k = r_pix * 2;
                    row1[cursor1 - DJ] = r_pix;
                    row1[cursor1 + DJ] += (r_pix += k);
                    row1[cursor1] += (r_pix += k);
                    row0[cursor0 + DJ] += (r_pix + k);

                    k = g_pix * 2;
                    row1[cursor1 + 1 - DJ] = g_pix;
                    row1[cursor1 + 1 + DJ] += (g_pix += k);
                    row1[cursor1 + 1] += (g_pix += k);
                    row0[cursor0 + 1 + DJ] += (g_pix + k);

                    k = b_pix * 2;
                    row1[cursor1 + 2 - DJ] = b_pix;
                    row1[cursor1 + 2 + DJ] += (b_pix += k);
                    row1[cursor1 + 2] += (b_pix += k);
                    row0[cursor0 + 2 + DJ] += (b_pix + k);

                    k = a_pix * 2;
                    row1[cursor1 + 3 - DJ] = a_pix;
                    row1[cursor1 + 3 + DJ] += (a_pix += k);
                    row1[cursor1 + 3] += (a_pix += k);
                    row0[cursor0 + 3 + DJ] += (a_pix + k);

                    cursor0 += DJ;
                    cursor1 -= DJ;
                    pixelIndex += dir;
                }
                if ((i % 2) == 1)
                    pixelIndex += width + 1;

                dir *= -1;
                int[] temp = row0;
                row0 = row1;
                row1 = temp;
            }
            return qPixels;
        }

        return qPixels;
    }
}
