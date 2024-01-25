/*
 *    Copyright 2018 Yizheng Huang
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.watermark.androidwm.utils;


public final class Fft {

    /**
     * Computes the discrete Fourier transform (DFT) of the given complex vector,
     * storing the result back into the vector.
     * <p>
     * The vector can have any length. This is a wrapper function.
     */
    public static void transform(double[] real, double[] imag) {
        int n = real.length;
        if (n != imag.length) {
            throw new IllegalArgumentException("Mismatched lengths");
        }
        if ((n & (n - 1)) == 0) {
            transformRadix2(real, imag);
        } else {
            transformBlueStein(real, imag);
        }

    }


    /**
     * Computes the inverse discrete Fourier transform (IDFT) of the given
     * complex vector, storing the result back into the vector.
     * <p>
     * The vector can have any length. This is a wrapper function.
     * This transform does not perform scaling, so the inverse is not a true inverse.
     */
    private static void inverseTransform(double[] real, double[] imag) {
        transform(imag, real);
    }

    /**
     * Computes the discrete Fourier transform (DFT) of the given complex vector,
     * storing the result back into the vector.
     * <p>
     * The vector's length must be a power of 2. Uses the Cooley-Tukey
     * decimation-in-time radix-2 algorithm.
     */
    private static void transformRadix2(double[] real, double[] imag) {
        int n = real.length;
        if (n != imag.length) {
            throw new IllegalArgumentException("Mismatched lengths");
        }

        int levels = 31 - Integer.numberOfLeadingZeros(n);
        if (1 << levels != n) {
            throw new IllegalArgumentException("Length is not a power of 2");
        }

        double[] cosTable = new double[n / 2];
        double[] sinTable = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            cosTable[i] = Math.cos(2 * Math.PI * i / n);
            sinTable[i] = Math.sin(2 * Math.PI * i / n);
        }

        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        for (int size = 2; size <= n; size *= 2) {
            int halfSize = size / 2;
            int tableStep = n / size;

            for (int i = 0; i < n; i += size) {
                for (int j = i, k = 0; j < i + halfSize; j++, k += tableStep) {
                    int l = j + halfSize;
                    double tpre = real[l] * cosTable[k] + imag[l] * sinTable[k];
                    double tpim = -real[l] * sinTable[k] + imag[l] * cosTable[k];
                    real[l] = real[j] - tpre;
                    imag[l] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }

            if (size == n) {
                break;
            }

        }
    }


    /**
     * Computes the discrete Fourier transform (DFT) of the given complex vector,
     * storing the result back into the vector.
     * <p>
     * The vector can have any length. This requires the convolution function,
     * which in turn requires the radix-2 FFT function.
     * <p>
     * Uses Bluestein's chirp z-transform algorithm.
     */
    private static void transformBlueStein(double[] real, double[] imag) {
        int n = real.length;
        if (n != imag.length) {
            throw new IllegalArgumentException("Mismatched lengths");
        }
        if (n >= 0x20000000) {
            throw new IllegalArgumentException("Array too large");
        }

        int m = Integer.highestOneBit(n) * 4;

        double[] cosTable = new double[n];
        double[] sinTable = new double[n];
        for (int i = 0; i < n; i++) {
            int j = (int) ((long) i * i % (n * 2));
            cosTable[i] = Math.cos(Math.PI * j / n);
            sinTable[i] = Math.sin(Math.PI * j / n);
        }

        double[] aReal = new double[m];
        double[] aImag = new double[m];
        for (int i = 0; i < n; i++) {
            aReal[i] = real[i] * cosTable[i] + imag[i] * sinTable[i];
            aImag[i] = -real[i] * sinTable[i] + imag[i] * cosTable[i];
        }
        double[] bReal = new double[m];
        double[] bImag = new double[m];
        bReal[0] = cosTable[0];
        bImag[0] = sinTable[0];
        for (int i = 1; i < n; i++) {
            bReal[i] = bReal[m - i] = cosTable[i];
            bImag[i] = bImag[m - i] = sinTable[i];
        }

        double[] cReal = new double[m];
        double[] cImag = new double[m];
        convolve(aReal, aImag, bReal, bImag, cReal, cImag);

        for (int i = 0; i < n; i++) {
            real[i] = cReal[i] * cosTable[i] + cImag[i] * sinTable[i];
            imag[i] = -cReal[i] * sinTable[i] + cImag[i] * cosTable[i];
        }
    }

    /**
     * Computes the circular convolution of the given complex vectors.
     * Each vector's length must be the same.
     */
    private static void convolve(double[] xReal, double[] xImag,
                                 double[] yReal, double[] yImag, double[] outReal, double[] outImag) {

        int n = xReal.length;
        if (n != xImag.length || n != yReal.length || n != yImag.length
                || n != outReal.length || n != outImag.length) {
            throw new IllegalArgumentException("Mismatched lengths");
        }

        xReal = xReal.clone();
        xImag = xImag.clone();
        yReal = yReal.clone();
        yImag = yImag.clone();
        transform(xReal, xImag);
        transform(yReal, yImag);

        for (int i = 0; i < n; i++) {
            double temp = xReal[i] * yReal[i] - xImag[i] * yImag[i];
            xImag[i] = xImag[i] * yReal[i] + xReal[i] * yImag[i];
            xReal[i] = temp;
        }

        inverseTransform(xReal, xImag);

        for (int i = 0; i < n; i++) {
            outReal[i] = xReal[i] / n;
            outImag[i] = xImag[i] / n;
        }
    }

}