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


import java.util.Arrays;
import java.util.Objects;

public final class FastDctFft {

    /**
     * Computes the unscaled DCT type II on the specified array in place.
     * The array length must be a power of 2 or zero.
     *
     * @param vector the vector of numbers to transform
     * @throws NullPointerException if the array is {@code null}
     */
    public static void transform(double[] vector) {
        Objects.requireNonNull(vector);
        int len = vector.length;
        int halfLen = len / 2;
        double[] real = new double[len];

        for (int i = 0; i < halfLen; i++) {
            real[i] = vector[i * 2];
            real[len - 1 - i] = vector[i * 2 + 1];
        }

        if (len % 2 == 1) {
            real[halfLen] = vector[len - 1];
        }

        Arrays.fill(vector, 0.0);
        Fft.transform(real, vector);
        for (int i = 0; i < len; i++) {
            double temp = i * Math.PI / (len * 2);
            vector[i] = real[i] * Math.cos(temp) + vector[i] * Math.sin(temp);
        }
    }


    /**
     * Computes the unscaled DCT type III on the specified array in place.
     * The array length must be a power of 2 or zero.
     *
     * @param vector the vector of numbers to transform
     * @throws NullPointerException if the array is {@code null}
     */
    public static void inverseTransform(double[] vector) {
        Objects.requireNonNull(vector);
        int len = vector.length;
        if (len > 0) {
            vector[0] = vector[0] / 2;
        }

        double[] real = new double[len];

        for (int i = 0; i < len; i++) {
            double temp = i * Math.PI / (len * 2);
            real[i] = vector[i] * Math.cos(temp);
            vector[i] *= -Math.sin(temp);
        }

        Fft.transform(real, vector);

        int halfLen = len / 2;
        for (int i = 0; i < halfLen; i++) {
            vector[i * 2] = real[i];
            vector[i * 2 + 1] = real[len - 1 - i];
        }

        if (len % 2 == 1) {
            vector[len - 1] = real[halfLen];
        }

        double scale = (double) len / 2;
        for (int i = 0; i < len; i++) {
            vector[i] = (int) Math.round(vector[i] / scale);
        }

    }

}