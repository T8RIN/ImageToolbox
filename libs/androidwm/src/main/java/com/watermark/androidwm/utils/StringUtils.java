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


import com.watermark.androidwm.listener.DetectFinishListener;

import static com.watermark.androidwm.utils.Constant.ERROR_NO_WATERMARK_FOUND;
import static com.watermark.androidwm.utils.Constant.LSB_IMG_PREFIX_FLAG;
import static com.watermark.androidwm.utils.Constant.LSB_IMG_SUFFIX_FLAG;
import static com.watermark.androidwm.utils.Constant.LSB_TEXT_PREFIX_FLAG;
import static com.watermark.androidwm.utils.Constant.LSB_TEXT_SUFFIX_FLAG;

/**
 * Util class for operations with {@link String}.
 *
 * @author huangyz0918
 */
public class StringUtils {

    static {
        System.loadLibrary("Watermark");
    }

    /**
     * Converting a {@link String} text into a binary text.
     * <p>
     * This is the native version.
     */
    public static native String stringToBinary(String inputText);

    /**
     * String to integer array.
     * <p>
     * This is the native version.
     */
    public static native int[] stringToIntArray(String inputString);

    /**
     * Converting a binary string to a ASCII string.
     */
    public static native String binaryToString(String inputText);

    /**
     * Replace the wrong rgb number in a form of binary,
     * the only case is 0 - 1 = 9, so, we need to replace
     * all nines to zero.
     */
    public static native void replaceNines(int[] inputArray);

    public static void replaceNinesJ(int[] inputArray) {
        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i] == 9) {
                inputArray[i] = 0;
            }
        }
    }

    /**
     * Int array to string.
     */
    public static native String intArrayToString(int[] inputArray);

    public static String intArrayToStringJ(int[] inputArray) {
        StringBuilder binary = new StringBuilder();
        for (int num : inputArray) {
            binary.append(num);
        }
        return binary.toString();
    }

    /**
     * native method for calculating the Convolution 1D.
     */
    public static native double[] calConv1D(double[] inputArray1, double[] inputArray2);

    /**
     * get the single digit number and set it to the target one.
     */
    public static int replaceSingleDigit(int target, int singleDigit) {
        return (target / 10) * 10 + singleDigit;
    }

    public static int replaceSingleDigit(double target, int singleDigit) {
        return ((int) target / 10) * 10 + singleDigit;
    }


    /**
     * Get text between two strings. Passed limiting strings are not
     * included into result.
     *
     * @param text Text to search in.
     */
    public static String getBetweenStrings(String text, boolean isText, DetectFinishListener listener) {
        String result = null;
        if (isText) {
            try {
                result = text.substring(text.indexOf(LSB_TEXT_PREFIX_FLAG) + LSB_TEXT_SUFFIX_FLAG.length(),
                        text.length());
                result = result.substring(0, result.indexOf(LSB_TEXT_SUFFIX_FLAG));
            } catch (StringIndexOutOfBoundsException e) {
                listener.onFailure(ERROR_NO_WATERMARK_FOUND);
            }
        } else {
            try {
                result = text.substring(text.indexOf(LSB_IMG_PREFIX_FLAG) + LSB_IMG_SUFFIX_FLAG.length(),
                        text.length());
                result = result.substring(0, result.indexOf(LSB_IMG_SUFFIX_FLAG));
            } catch (StringIndexOutOfBoundsException e) {
                listener.onFailure(ERROR_NO_WATERMARK_FOUND);
            }
        }

        return result;
    }

    /**
     * cast an int array to a double array.
     * System.arrayCopy cannot cast the int array to a double one.
     */
    @SuppressWarnings("PMD")
    public static double[] copyFromIntArray(int[] source) {
        double[] dest = new double[source.length];
        for (int i = 0; i < source.length; i++) {
            dest[i] = source[i];
        }
        return dest;
    }

    /**
     * cast a double array to an int array.
     * System.arrayCopy cannot cast the double array to an int one.
     */
    @SuppressWarnings("PMD")
    public static int[] copyFromDoubleArray(double[] source) {
        int[] dest = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            dest[i] = (int) source[i];
        }
        return dest;
    }

}
